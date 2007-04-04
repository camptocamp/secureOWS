//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/Datastore.java,v 1.27 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.io.datastore;

import java.util.ArrayList;
import java.util.Collection;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.operation.LockFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * A datastore implementation must extend this class.
 * <p>
 * Describes the access to a datastore that encapsulates the access to a database or file. The
 * accessible objects are {@link Feature} instances. Primarily, datastores are used as persistence
 * layer by the {@link WFService} class.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.27 $, $Date: 2006/11/27 09:07:53 $
 */
public abstract class Datastore {

    private static final TriggerProvider TP = TriggerProvider.create( Datastore.class );

    protected static final ILogger LOG = LoggerFactory.getLogger( Datastore.class );

    private Collection<MappedGMLSchema> schemas = new ArrayList<MappedGMLSchema>( 10 );

    private DatastoreConfiguration config;

    /**
     * Configures the datastore with the supplied configuration.
     * 
     * @param config
     *            configuration
     * @throws DatastoreException
     */
    @SuppressWarnings("unused")
    public void configure( DatastoreConfiguration config )
                            throws DatastoreException {
        this.config = config;
    }

    /**
     * Returns the configuration parameters of the datastore.
     * 
     * @return the configuration parameters of the datastore.
     */
    public DatastoreConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * Adds the given GML application schema to the set of schemas that are handled by this
     * datastore instance.
     * <p>
     * Note that this method may be called several times for every GML schema that uses this
     * datastore instance.
     * 
     * @param schema
     *            GML application schema to bind
     * @throws DatastoreException
     */
    @SuppressWarnings("unused")
    public void bindSchema( MappedGMLSchema schema )
                            throws DatastoreException {
        this.schemas.add( schema );
    }

    /**
     * Returns the GML application schemas that are handled by this datastore.
     * 
     * @return the GML application schemas that are handled by this datastore
     */
    public MappedGMLSchema[] getSchemas() {
        return this.schemas.toArray( new MappedGMLSchema[this.schemas.size()] );
    }

    /**
     * Returns the feature type with the given name.
     * 
     * @param ftName
     *            name of the feature type
     * @return the feature type with the given name.
     */
    public MappedFeatureType getFeatureType( QualifiedName ftName ) {
        MappedFeatureType ft = null;
        MappedGMLSchema[] schemas = getSchemas();
        for ( int i = 0; i < schemas.length; i++ ) {
            ft = (MappedFeatureType) schemas[i].getFeatureType( ftName );
            if ( ft != null ) {
                break;
            }
        }
        return ft;
    }

    /**
     * Closes the datastore so it can free dependent resources.
     * 
     * @throws DatastoreException
     */
    public abstract void close()
                            throws DatastoreException;

    /**
     * Performs a query against the datastore.
     * 
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @return requested feature instances
     * @throws DatastoreException
     */
    public abstract FeatureCollection performQuery( final Query query,
                                                    final MappedFeatureType rootFeatureType )
                            throws DatastoreException, UnknownCRSException ;

    /**
     * Performs a query against the datastore (in the given transaction context).
     * 
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @param context
     *            context (used to specify the JDBCConnection, for example)
     * @return requested feature instances
     * @throws DatastoreException
     */
    public abstract FeatureCollection performQuery( final Query query,
                                                    final MappedFeatureType rootFeatureType,
                                                    final DatastoreTransaction context )
                            throws DatastoreException, UnknownCRSException;

    /**
     * Returns the feature collection that matches the submitted request.
     * 
     * @param query
     * @return requested feature instances
     * @throws DatastoreException 
     */
    public FeatureCollection performQueryWithLock( Query query )
                            throws DatastoreException {
        throw new DatastoreException( Messages.getMessage( "DATASTORE_METHOD_UNSUPPORTED",
                                                           this.getClass().getName(),
                                                           "#performQueryWithLock( Query )" ) );
    }

    /**
     * Performs the locking/unlocking of one or more features.
     * 
     * @param request
     *            the features that should be (un)locked
     * @throws DatastoreException
     */
    public void performLockFeature( LockFeature request )
                            throws DatastoreException {
        throw new DatastoreException( Messages.getMessage( "DATASTORE_METHOD_UNSUPPORTED",
                                                           this.getClass().getName(),
                                                           "#performLockFeature( LockFeature )" ) );        
    }

    /**
     * Acquires transactional access to the datastore instance. There's only one active transaction
     * per datastore allowed.
     * 
     * @return transaction object that allows to perform transactions operations on the datastore
     * @throws DatastoreException
     */
    public DatastoreTransaction acquireTransaction()
                            throws DatastoreException {
        throw new DatastoreException( Messages.getMessage( "DATASTORE_METHOD_UNSUPPORTED",
                                                           this.getClass().getName(),
                                                           "#acquireTransaction()" ) );         
    }

    /**
     * Returns the transaction to the datastore. This makes the transaction available to other
     * clients again (via {@link #acquireTransaction()}). Underlying resources (such as
     * JDBCConnections are freed).
     * <p>
     * The transaction should be terminated, i.e. {@link DatastoreTransaction#commit()} or
     * {@link DatastoreTransaction#rollback()} must have been called before.
     * 
     * @param ta
     *            the DatastoreTransaction to be returned
     * @throws DatastoreException
     */
    public void releaseTransaction( DatastoreTransaction ta )
                            throws DatastoreException {
        throw new DatastoreException( Messages.getMessage( "DATASTORE_METHOD_UNSUPPORTED",
                                                           this.getClass().getName(),
                                                           "#releaseTransaction()" ) );          
    }

    /**
     * Transforms the incoming {@link Query} so that the {@link CoordinateSystem} of all spatial
     * arguments (BBOX, etc.) in the {@link Filter} match the SRS of the targeted
     * {@link MappingGeometryField}s.
     * <p>
     * NOTE: If this transformation can be performed by the backend (e.g. by Oracle Spatial), this
     * method should be overwritten to return the original input {@link Query}.
     * 
     * @param query
     *             query to be transformed
     * @return query with spatial arguments transformed to target SRS
     */
    protected Query transformQuery( Query query ) {
        LOG.logDebug( "Transforming query." );
        Object[] result = TP.doPreTrigger( this, query );
        query = (Query) result[0];
        return query;
    }

    /**
     * Transforms the {@link FeatureCollection} so that the geometries of all contained geometry
     * properties use the requested SRS.  
     * 
     * @param fc
     *            feature collection to be transformed
     * @param targetSRS
     *            requested SRS
     * @return transformed FeatureCollection
     */
    protected FeatureCollection transformResult( FeatureCollection fc, String targetSRS ) {
        LOG.logDebug( "Transforming result to SRS '" + targetSRS + "'." );
        Object[] result = TP.doPostTrigger( this, fc, targetSRS );
        fc = (FeatureCollection) result[0];
        return fc;
    }

    /**
     * Returns whether the datastore is capable of performing a native coordinate transformation
     * (using an SQL function call for example) into the given SRS.
     * <p>
     * <code>Datastore</code> implementations capable of performing native coordinate
     * transformations must override this class.
     * 
     * @param targetSRS
     *            target spatial reference system (usually "EPSG:XYZ")
     * @return true, if the datastore can perform the coordinate transformation, false otherwise
     */
    protected boolean canTransformTo( @SuppressWarnings("unused")
    String targetSRS ) {
        return false;
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: Datastore.java,v $
 * Revision 1.27  2006/11/27 09:07:53  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.26  2006/11/16 08:56:30  mschneider
 * Added dummy implementations for all methods that are not mandatory.
 *
 * Revision 1.25  2006/11/09 17:35:21  mschneider
 * Added #canTransformTo(String).
 *
 * Revision 1.24  2006/09/27 20:08:41  poth
 * methods form CRS transformation of requests and results added
 *
 * Revision 1.23  2006/09/26 16:41:12  mschneider
 * Corrected warnings.
 *
 * Revision 1.22  2006/08/30 17:00:14  mschneider
 * Improved javadoc.
 *
 * Revision 1.21  2006/08/28 16:44:04  mschneider
 * Javadoc fixes.
 *
 * Revision 1.20  2006/05/21 19:09:39  poth
 * comments completed
 *
 ************************************************************************************************* */