//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/TransactionHandler.java,v 1.65 2006/11/27 09:07:53 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wfs;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.PropertyPathResolver;
import org.deegree.io.datastore.idgenerator.FeatureIdAssigner;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureDocument;
import org.deegree.model.feature.Validator;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathStep;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.operation.transaction.Delete;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;
import org.deegree.ogcwebservices.wfs.operation.transaction.InsertResults;
import org.deegree.ogcwebservices.wfs.operation.transaction.Native;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionOperation;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionResponse;
import org.deegree.ogcwebservices.wfs.operation.transaction.Update;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for transaction requests to the {@link WFService}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a> 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.65 $, $Date: 2006/11/27 09:07:53 $
 */
public class TransactionHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( TransactionHandler.class );

    private WFService service;

    private Transaction request;

    private Map<QualifiedName, MappedFeatureType> ftMap;

    // filled in #acquireDSTransactions()
    private Map<QualifiedName, DatastoreTransaction> taMap = new HashMap<QualifiedName, DatastoreTransaction>();

    // filled in #acquireDSTransactions()    
    private Map<Datastore, DatastoreTransaction> dsToTaMap = new HashMap<Datastore, DatastoreTransaction>();

    /**
     * Creates a new <code>TransactionHandler</code> instance.
     * 
     * @param service
     * @param request
     */
    public TransactionHandler( WFService service, Transaction request ) {
        this.service = service;
        this.request = request;
        this.ftMap = service.getMappedFeatureTypes();
    }

    /**
     * Performs the associated transaction.
     * 
     * @return transaction response
     * @throws OGCWebServiceException
     *             if an error occured
     */
    public TransactionResponse handleRequest()
                            throws OGCWebServiceException {
        
        try {
            validate( this.request );
        } catch ( UnknownCRSException e1 ) {   
            throw new OGCWebServiceException( getClass().getName(), e1.getMessage() );
        }

        TransactionResponse response = null;

        acquireDSTransactions();

        try {
            try {
                response = performOperations();
            } catch ( OGCWebServiceException e ) {
                abortDSTransactions();
                throw e;
            }
            commitDSTransactions();
        } finally {
            releaseDSTransactions();
        }
                
        return response;
    }

    /**
     * Validates the feature instances in the given transaction against the WFS' application
     * schemas.
     * <p>
     * The feature instances are assigned the corresponding <code>MappedFeatureType</code> in the
     * process.
     * 
     * @param request
     * @throws OGCWebServiceException
     * @throws UnknownCRSException 
     */
    private void validate( Transaction request )
                            throws OGCWebServiceException, UnknownCRSException {

        List<TransactionOperation> operations = request.getOperations();

        Iterator<TransactionOperation> iter = operations.iterator();
        while ( iter.hasNext() ) {
            TransactionOperation operation = iter.next();
            if ( operation instanceof Insert ) {
                validateInsert( (Insert) operation );
            } else if ( operation instanceof Delete ) {
                // nothing to do (contains no features)
            } else if ( operation instanceof Update ) {
                validateUpdate( (Update) operation );                
            } else if ( operation instanceof Native ) {
                // nothing to do (contains no features)                
            } else {
                String msg = "Internal error. Unhandled transaction operation type '"
                             + operation.getClass().getName() + "'.";
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
        }
    }

    /**
     * Validates all feature instances in the given insert operation against the WFS' application
     * schemas.
     * <p>
     * The feature instances are assigned the corresponding <code>MappedFeatureType</code> in the
     * process.
     * 
     * @param operation
     * @throws OGCWebServiceException
     */
    private void validateInsert( Insert operation )
                            throws OGCWebServiceException {
        FeatureCollection fc = operation.getFeatures();
        Validator validator = new Validator( (Map) this.service.getMappedFeatureTypes() );
        for ( int i = 0; i < fc.size(); i++ ) {
            validator.validate( fc.getFeature( i ) );
        }
    }

    /**
     * Validates any feature instance in the given update operation against the WFS' application
     * schemas.
     * <p>
     * Feature instances are assigned the corresponding <code>MappedFeatureType</code> in the
     * process, property names are normalized and their values are parsed into the respective
     * objects.
     * 
     * @param operation update operation
     * @throws OGCWebServiceException
     * @throws UnknownCRSException 
     */
    private void validateUpdate( Update operation )
                            throws OGCWebServiceException, UnknownCRSException {

        QualifiedName ftName = operation.getTypeName();
        MappedFeatureType ft = this.ftMap.get( ftName );
        if ( ft == null ) {
            String msg = Messages.getMessage( "WFS_UPDATE_FEATURE_TYPE_UNKNOWN", ftName );
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }

        Feature feature = operation.getFeature();
        if ( feature != null ) {
            Validator validator = new Validator( (Map) this.service.getMappedFeatureTypes() );
            validator.validate( feature );
        } else {
            validateProperties( ft, operation );
        }
    }

    /**
     * Validates the properties and their replacement values that are specified in the given
     * <code>Update</code> operation.
     * <p>
     * Property names are normalized and their values are parsed into the respective objects.
     * 
     * @param ft feature type
     * @param operation update operation
     * @throws OGCWebServiceException
     * @throws UnknownCRSException 
     */
    private void validateProperties( MappedFeatureType ft, Update operation )
                            throws OGCWebServiceException, UnknownCRSException {
        Map<PropertyPath, Node> rawProperties = operation.getRawProperties();
        Map<PropertyPath, Object> parsedProperties = new LinkedHashMap<PropertyPath, Object>();
        for ( PropertyPath path : rawProperties.keySet() ) {
            Node propertyValue = rawProperties.get( path );
            path = PropertyPathResolver.normalizePropertyPath( ft, path );
            Object property = validateProperty( ft, path, propertyValue );
            parsedProperties.put( path, property );
        }
        operation.setParsedProperties( parsedProperties );
    }

    /**
     * Validates the properties and their replacement values that are specified in the given
     * <code>Update</code> operation.
     * <p>
     * Values are parsed into the respective objects.
     * 
     * @param ft feature type
     * @param path property name
     * @param value replacement property value (as XML node)
     * @return object representation of the replacement property value 
     * @throws OGCWebServiceException
     * @throws UnknownCRSException 
     */
    private Object validateProperty( MappedFeatureType ft, PropertyPath path, Node value )
                            throws OGCWebServiceException, UnknownCRSException {

        Object propertyValue = null;

        for ( int i = 0; i < path.getSteps(); i += 2 ) {

            // check if feature step is valid
            PropertyPathStep ftStep = path.getStep( i );
            FeatureType stepFt = this.ftMap.get( ftStep.getPropertyName() );
            if ( stepFt == null ) {
                String msg = Messages.getMessage( "WFS_UPDATE_FEATURE_STEP_UNKNOWN", path,
                                              stepFt.getName() );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            MappedGMLSchema schema = ft.getGMLSchema();
            if ( !schema.isValidSubstitution( ft, stepFt ) ) {
                String msg = Messages.getMessage( "WFS_UPDATE_FEATURE_STEP_INVALID", path,
                                              stepFt.getName(), ft.getName() );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }

            // check if property step is valid
            PropertyPathStep propertyStep = path.getStep( i + 1 );
            QualifiedName propertyName = propertyStep.getPropertyName();
            PropertyType pt = ft.getProperty( propertyName );
            if ( pt == null ) {
                String msg = Messages.getMessage( "WFS_UPDATE_PROPERTY_STEP_UNKNOWN", path,
                                              propertyName, ft.getName() );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            if ( i + 2 == path.getSteps() ) {
                if ( value != null ) {
                    try {
                        // create a wrapper document for the new property value
                        GMLFeatureDocument propertyDoc = new GMLFeatureDocument();
                        Map<URI, GMLSchema> nsToSchemaMap = buildNsToSchemaMap();
                        propertyDoc.setSchemas( nsToSchemaMap );
                        Document wrapperDoc = XMLTools.create();
                        Element root = wrapperDoc.createElementNS(
                                                                   propertyName.getNamespace().toString(),
                                                                   propertyName.getAsString() );
                        wrapperDoc.appendChild( root );
                        XMLTools.appendNSBinding( root, propertyName.getPrefix(),
                                                  propertyName.getNamespace() );
                        propertyDoc.setRootElement( root );
                        root.appendChild( wrapperDoc.importNode( value, true ) );

                        FeatureProperty replacementProperty = null;
                        replacementProperty = propertyDoc.parseProperty( root, ft );
                        propertyValue = replacementProperty.getValue();
                    } catch ( XMLParsingException e ) {
                        e.printStackTrace();
                        String msg = Messages.getMessage( "WFS_UPDATE_PROPERTY_VALUE_INVALID", path,
                                                      e.getMessage() );
                        throw new OGCWebServiceException( this.getClass().getName(), msg );
                    }
                }
            } else {
                if ( !( pt instanceof MappedFeaturePropertyType ) ) {
                    String msg = Messages.getMessage( "WFS_UPDATE_NOT_FEATURE_PROPERTY", path,
                                                  propertyName );
                    throw new OGCWebServiceException( this.getClass().getName(), msg );
                }
                MappedFeaturePropertyType fpt = (MappedFeaturePropertyType) pt;
                ft = fpt.getFeatureTypeReference().getFeatureType();
            }
        }
        return propertyValue;
    }

    private Map<URI, GMLSchema> buildNsToSchemaMap() {
        Map<URI, GMLSchema> nsToSchemaMap = new HashMap<URI, GMLSchema>();
        Set<GMLSchema> schemas = new HashSet<GMLSchema>();
        for ( MappedFeatureType ft : this.ftMap.values() ) {
            schemas.add( ft.getGMLSchema() );
        }
        for ( GMLSchema schema : schemas ) {
            nsToSchemaMap.put( schema.getTargetNamespace(), schema );
        }
        return nsToSchemaMap;
    }

    /**
     * Performs the operations contained in the transaction.
     * 
     * @throws OGCWebServiceException
     */
    private TransactionResponse performOperations()
                            throws OGCWebServiceException {

        int inserts = 0;
        int deletes = 0;
        int updates = 0;

        List<InsertResults> insertResults = new ArrayList<InsertResults>();

        List<TransactionOperation> operations = request.getOperations();

        Iterator<TransactionOperation> iter = operations.iterator();
        while ( iter.hasNext() ) {
            TransactionOperation operation = iter.next();
            String handle = operation.getHandle();
            try {
                if ( operation instanceof Insert ) {
                    List<FeatureId> insertedFIDs = performInsert( (Insert) operation );
                    InsertResults results = new InsertResults( handle, insertedFIDs );
                    insertResults.add( results );
                    inserts += insertedFIDs.size();
                } else if ( operation instanceof Delete ) {
                    deletes += performDelete( (Delete) operation );
                } else if ( operation instanceof Update ) {
                    updates += performUpdate( (Update) operation );
                } else if ( operation instanceof Native ) {
                    String msg = Messages.getMessage( "WFS_NATIVE_OPERATIONS_UNSUPPORTED" );
                    throw new OGCWebServiceException( this.getClass().getName(), msg );
                } else {
                    String opType = operation.getClass().getName();
                    String msg = Messages.getMessage( "WFS_UNHANDLED_OPERATION_TYPE", opType );
                    throw new OGCWebServiceException( this.getClass().getName(), msg );
                }
            } catch ( DatastoreException e ) {
                LOG.logDebug( e.getMessage(), e );
                String msg = "A datastore exception occured during the processing of operation with handle '"
                             + handle + "': " + e.getMessage();
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
        }
        TransactionResponse response = new TransactionResponse( request, inserts, updates, deletes,
                                                                insertResults );
        return response;
    }

    /**
     * Performs the given insert operation.
     * 
     * @param insert
     *            insert operation to be performed
     * @throws DatastoreException 
     */
    private List<FeatureId> performInsert( Insert insert )
                            throws DatastoreException {

        List<FeatureId> fids = new ArrayList<FeatureId>();
        FeatureCollection fc = insert.getFeatures();

        // merge all equal and anonymous features (without fid)
        FeatureDisambiguator merger = new FeatureDisambiguator( fc );
        fc = merger.mergeFeatures();

        Map<DatastoreTransaction, List<Feature>> taFeaturesMap = new HashMap<DatastoreTransaction, List<Feature>>();

        FeatureIdAssigner fidAssigner = new FeatureIdAssigner( insert.getIdGen() );

        // assign each feature instance to the corresponding transaction (and datastore)        
        for ( int i = 0; i < fc.size(); i++ ) {
            Feature feature = fc.getFeature( i );
            QualifiedName ftName = feature.getName();
            //MappedFeatureType ft = this.ftMap.get(ftName);
            DatastoreTransaction dsTa = this.taMap.get( ftName );
            // reassign feature ids (if necessary)
            fidAssigner.assignFID( feature, dsTa );
            List<Feature> features = taFeaturesMap.get( dsTa );
            if ( features == null ) {
                features = new ArrayList<Feature>();
                taFeaturesMap.put( dsTa, features );
            }
            features.add( feature );
        }

        // TODO remove this hack
        fidAssigner.markStoredFeatures();

        Iterator<DatastoreTransaction> taIter = taFeaturesMap.keySet().iterator();
        while ( taIter.hasNext() ) {
            DatastoreTransaction ta = taIter.next();
            List<Feature> features = taFeaturesMap.get( ta );
            fids.addAll( ta.performInsert( features ) );
        }
        return fids;
    }

    /**
     * Performs the given delete operation.
     * 
     * @param delete
     *            delete operation to be performed
     * @throws DatastoreException
     */
    private int performDelete( Delete delete )
                            throws DatastoreException {
        QualifiedName ftName = delete.getTypeName();
        MappedFeatureType ft = this.ftMap.get( ftName );
        DatastoreTransaction dsTa = this.taMap.get( ftName );
        int deleted = dsTa.performDelete( ft, delete.getFilter() );
        return deleted;
    }

    /**
     * Performs the given update operation.
     * <p>
     * Assigning of FIDs to replacment features is performed in the <code>UpdateHandler</code>.
     * 
     * @param update
     *            update operation to be perform
     * @throws DatastoreException
     */
    private int performUpdate( Update update )
                            throws DatastoreException {

        QualifiedName ftName = update.getTypeName();
        MappedFeatureType ft = this.ftMap.get( ftName );
        DatastoreTransaction dsTa = this.taMap.get( ftName );
        int updated = 0;
        if ( update.getFeature() == null ) {
            updated = dsTa.performUpdate( ft, update.getReplacementProperties(), update.getFilter() );
        } else {
            updated = dsTa.performUpdate( ft, update.getFeature(), update.getFilter() );
        }
        return updated;
    }

    /**
     * Acquires the necessary <code>DatastoreTransaction</code>s. For each participating
     * <code>Datastore</code>, one transaction is needed.
     * <p>
     * Fills the taMap and dsToTaMap members of this class.
     * 
     * @throws OGCWebServiceException
     *             if a feature type is unknown or a DatastoreTransaction could not be acquired
     */
    private void acquireDSTransactions()
                            throws OGCWebServiceException {
        Set<QualifiedName> ftNames = this.request.getAffectedFeatureTypes();
        for ( QualifiedName ftName : ftNames ) {
            MappedFeatureType ft = this.ftMap.get( ftName );
            if ( ft == null ) {
                String msg = "FeatureType '" + ftName + "' is not known to the WFS.";
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            Datastore ds = ft.getGMLSchema().getDatastore();
            DatastoreTransaction dsTa = this.dsToTaMap.get( ds );
            if ( dsTa == null ) {
                try {
                    dsTa = ds.acquireTransaction();
                } catch ( DatastoreException e ) {
                    String msg = "Could not acquire transaction for FeatureType '" + ftName + "'.";
                    throw new OGCWebServiceException( this.getClass().getName(), msg );
                }
                this.dsToTaMap.put( ds, dsTa );
            }
            this.taMap.put( ftName, dsTa );
        }
    }

    /**
     * Releases all acquired <code>DatastoreTransaction</code>s.
     * 
     * @throws OGCWebServiceException if a DatastoreTransaction could not be released
     */
    private void releaseDSTransactions()
                            throws OGCWebServiceException {
        String msg = "";
        for ( DatastoreTransaction dsTa : this.taMap.values() ) {
            LOG.logDebug( "Releasing DatastoreTransaction." );
            try {
                dsTa.release();
            } catch ( DatastoreException e ) {
                LOG.logError( "Error releasing DatastoreTransaction: " + e.getMessage(), e );
                msg += e.getMessage();
            }
        }
        if ( msg.length() != 0 ) {
            msg = "Could not release one or more DatastoreTransactions: " + msg;
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }
    }

    /**
     * Commits all pending <code>DatastoreTransaction</code>s.
     * 
     * @throws OGCWebServiceException
     *             if a DatastoreException could not be committed
     */
    private void commitDSTransactions()
                            throws OGCWebServiceException {
        Iterator<DatastoreTransaction> iter = this.dsToTaMap.values().iterator();
        while ( iter.hasNext() ) {
            DatastoreTransaction dsTa = iter.next();
            try {
                dsTa.commit();
            } catch ( DatastoreException e ) {
                String msg = "Could not commit transaction.";
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
        }
    }

    /**
     * Aborts all pending <code>DatastoreTransaction</code>s.
     * 
     * @throws OGCWebServiceException
     *             if a DatastoreException could not be aborted
     */
    private void abortDSTransactions()
                            throws OGCWebServiceException {
        Iterator<DatastoreTransaction> iter = this.dsToTaMap.values().iterator();
        while ( iter.hasNext() ) {
            DatastoreTransaction dsTa = iter.next();
            try {
                dsTa.rollback();
            } catch ( DatastoreException e ) {
                String msg = "Could not abort transaction.";
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TransactionHandler.java,v $
 Revision 1.65  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.64  2006/11/16 08:53:21  mschneider
 Merged messages from org.deegree.ogcwebservices.wfs and its subpackages.

 Revision 1.63  2006/10/10 08:07:59  poth
 Collection substituted by List

 Revision 1.62  2006/10/09 09:01:48  poth
 bug fix - TransactionResponse extending DefaultOGCWebServiceResponse

 Revision 1.61  2006/10/01 11:15:43  poth
 trigger points for doService methods defined

 Revision 1.60  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.59  2006/08/21 15:49:15  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.58  2006/08/14 13:17:25  mschneider
 Does not longer extend AbstractHandler. Javadoc fixes.

 Revision 1.57  2006/08/01 10:42:23  mschneider
 Adapted because of changes in FeatureDisambiguator.

 Revision 1.56  2006/07/27 16:21:13  mschneider
 Fixed formatting.

 Revision 1.55  2006/07/25 15:50:53  mschneider
 Outfactored disambiguation of features into class FeatureDisambiguator.

 Revision 1.54  2006/07/13 07:27:26  poth
 *** empty log message ***

 Revision 1.53  2006/07/10 08:26:02  poth
 allow geometry fields to be identity part

 Revision 1.52  2006/06/29 10:28:14  mschneider
 Moved identifying of stored features / assigning of feature ids to UpdateHandler.

 Revision 1.51  2006/06/29 07:05:04  poth
 *** empty log message ***

 Revision 1.50  2006/06/28 08:52:51  poth
 bug fixes according catalog harvesting

 Revision 1.49  2006/05/24 15:25:45  mschneider
 Added feature id assigning for replacement features (update).

 Revision 1.48  2006/05/23 22:39:26  mschneider
 Updates are now handled correctly.

 Revision 1.47  2006/05/23 21:25:41  mschneider
 Removed System.out.println() calls.

 Revision 1.46  2006/05/23 16:10:37  mschneider
 Added functionality to validate properties in Update operations.

 Revision 1.45  2006/05/18 15:45:57  mschneider
 Added handling of non-standard update (feature replace).

 Revision 1.44  2006/05/16 16:22:13  mschneider
 Refactored due to the splitting of org.deegree.ogcwebservices.wfs.operation package.
 Added validation for features in update operations.

 ********************************************************************** */