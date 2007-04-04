//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/Transaction.java,v 1.10 2006/11/23 18:43:30 poth Exp $
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
package org.deegree.ogcwebservices.wfs.operation.transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.operation.AbstractWFSRequest;
import org.w3c.dom.Element;

/**
 * Represents a <code>Transaction</code> request to a web feature service.
 * <p>
 * From the WFS Specification 1.1.0 OGC 04-094 (#12, Pg.63):
 * <p>
 * A <code>Transaction</code> request is used to describe data transformation operations that are
 * to be applied to web accessible feature instances. When the transaction has been completed, a
 * web feature service will generate an XML response document indicating the completion status of
 * the transaction.
 * <p>
 * A <code>Transaction</code> consists of a sequence of {@link Insert}, {@link Update},
 * {@link Delete} and {@link Native} operations.
 *
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.10 $, $Date: 2006/11/23 18:43:30 $
 */
public class Transaction extends AbstractWFSRequest {

    private static final long serialVersionUID = 6904739857311368390L;

    private static final ILogger LOG = LoggerFactory.getLogger( Transaction.class );

    private List<TransactionOperation> operations;

    // request version
    private String version;

    // transaction ID
    private String id;

    // LockID associated with the request
    private String lockID;

    /**
     * Specifies if ALL records should be released or if SOME records, indicating only those records
     * which have been modified will be released. The default is ALL.
     */
    private boolean releaseAllFeatures = true;
    
    private TransactionDocument sourceDocument;

    /**
     * Creates a new <code>Transaction</code> instance.
     * 
     * @param version
     *            WFS version
     * @param id
     *            Transaction id
     * @param versionSpecificParameter
     * @param lockID
     *            Lock Id specified
     * @param operations
     *            List of operations to be carried out
     * @param releaseAllFeatures
     * @param sourceDocument
     */
    public Transaction( String id, String version, Map<String,String> versionSpecificParameter, String lockID,
                       List<TransactionOperation> operations, boolean releaseAllFeatures,
                       TransactionDocument sourceDocument) {
        super( version, id, null, versionSpecificParameter );
        this.id = id;
        this.version = version;
        this.lockID = lockID;
        this.operations = operations;
        this.releaseAllFeatures = releaseAllFeatures;
        this.sourceDocument = sourceDocument;
    }
   
    /**
     * Returns the source document that was used to create this <code>Transaction</code> instance. 
     * 
     * @return the source document
     */
    public TransactionDocument getSourceDocument() {
        return this.sourceDocument;
    }

    /**
     * Returns the {@link TransactionOperation}s that are contained in the transaction.
     * 
     * @return the contained operations
     */
    public List<TransactionOperation> getOperations() {
        return this.operations;
    }

    /**
     * Returns the names of the feature types that are affected by the transaction.
     * 
     * @return the names of the affected feature types
     */
    public Set<QualifiedName> getAffectedFeatureTypes() {
        Set<QualifiedName> featureTypeSet = new HashSet<QualifiedName>();

        Iterator<TransactionOperation> iter = this.operations.iterator();
        while ( iter.hasNext() ) {
            TransactionOperation operation = iter.next();
            featureTypeSet.addAll( operation.getAffectedFeatureTypes() );
        }
        return featureTypeSet;
    }

    /**
     * Creates a <code>Transaction</code> request from a key-value-pair encoding of the parameters
     * contained in the passed variable 'request'.
     * 
     * @param id
     *            id of the request
     * @param request
     *            key-value-pair encoded GetFeature request
     * @return new created Transaction instance
     * @throws InconsistentRequestException
     * @throws InvalidParameterValueException
     */
    public static Transaction create( String id, String request )
                            throws InconsistentRequestException, InvalidParameterValueException {
        LOG.entering();
        Map<String,String> model = KVP2Map.toMap( request );
        model.put( "ID", id );
        LOG.exiting();
        return create( model );
    }

    /**
     * Creates a <code>Transaction</code> request from a key-value-pair encoding of the parameters
     * contained in the given Map.
     * 
     * @param model
     *            key-value-pair encoded Transaction request
     * @return new Transaction instance
     * @throws InconsistentRequestException
     * @throws InvalidParameterValueException
     */
    public static Transaction create( Map<String, String> model )
                            throws InconsistentRequestException, InvalidParameterValueException {

        Map<String,String> versionSpecificParameter = null;

        String id = model.get( "ID" );

        String version = checkVersionParameter( model );

        checkServiceParameter( model );

        String request = model.remove( "REQUEST" );
        if ( request == null ) {
            throw new InconsistentRequestException(
                                                    "Request parameter for a transaction request must be set." );
        }

        String lockID = model.remove( "LOCKID" );

        String releaseAction = model.remove( "RELEASEACTION" );
        boolean releaseAllFeatures = true;
        if ( releaseAction != null ) {
            if ( "SOME".equals( releaseAction ) ) {
                releaseAllFeatures = false;
            } else if ( "ALL".equals( releaseAction ) ) {
                releaseAllFeatures = true;
            } else {
                throw new InvalidParameterValueException( "releaseAction", releaseAction );
            }
        }

        QualifiedName[] typeNames = extractTypeNames( model );           
        
        String featureIdParameter = model.remove( "FEATUREID" );
        if ( typeNames == null && featureIdParameter == null ) {
            throw new InconsistentRequestException( "TypeName OR FeatureId parameter must be set." );
        }     
        
//        String[] featureIds = null;
//        if ( featureIdParameter != null ) {
//            // FEATUREID specified. Looking for featureId
//            // declaration TYPENAME contained in featureId declaration (eg.
//            // FEATUREID=InWaterA_1M.1013)
//            featureIds = StringTools.toArray( featureIdParameter, ",", false );
//            //typeNameSet = extractTypeNameFromFeatureId( featureIds, context, (HashSet) typeNameSet );
//        }

        // Filters
        //Map typeFilter = buildFilterMap( model, typeNames, featureIds, context );

//        // BBOX
//        typeFilter = extractBBOXParameter( model, typeNames, typeFilter );
//
//        if ( typeFilter == null || typeFilter.size() == 0 ) {
//            for ( int i = 0; i < typeNames.length; i++ ) {
//                typeFilter.put( typeNames[i], null );
//            }
//        }

        List<TransactionOperation> operations = extractOperations( model, null );

        return new Transaction( id, version, versionSpecificParameter, lockID, operations,
                                releaseAllFeatures, null );
    }

    /**
     * Extracts the {@link TransactionOperation}s contained in the given kvp request.
     * 
     * @param model
     * @param typeFilter
     * @param typeNames
     * @return List
     * @throws InconsistentRequestException
     */
    private static List<TransactionOperation> extractOperations( Map model, Map typeFilter )
                            throws InconsistentRequestException {
        List<TransactionOperation> operation = new ArrayList<TransactionOperation>();
        String op = (String) model.remove( "OPERATION" );
        if ( op == null ) {
            throw new InconsistentRequestException( "Operation parameter must be set" );
        }
        if ( op.equals( "Delete" ) ) {
            List<Delete> deletes = Delete.create( typeFilter );
            operation.addAll( deletes );
        } else {
            String msg = "Invalid OPERATION parameter '" + op
                         + "'. KVP Transactions only support the 'Delete' operation.";
            throw new InconsistentRequestException( msg );
        }
        return operation;
    }

    /**
     * Creates a <code>Transaction</code> instance from a document that contains the DOM
     * representation of the request.
     * 
     * @param id
     * @param root element that contains the DOM representation of the request
     * @return transaction instance
     * @throws OGCWebServiceException
     */
    public static Transaction create( String id, Element root )
                            throws OGCWebServiceException {
        TransactionDocument doc = new TransactionDocument();
        doc.setRootElement( root );
        Transaction request;        
        try {
            request = doc.parse( id );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "Transaction", e.getMessage() );
        }        
        return request;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String ret = this.getClass().getName();
        ret += "version: " + this.version + "\n";
        ret += "id: " + this.id + "\n";
        ret += "lockID: " + this.lockID + "\n";
        ret += "operations: \n";
        for ( int i = 0; i < operations.size(); i++ ) {
            ret += ( i + ": " + operations.get( i ) + "\n " );
        }
        ret += "releaseAllFeatures: " + this.releaseAllFeatures;
        return ret;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Transaction.java,v $
 Revision 1.10  2006/11/23 18:43:30  poth
 Log statement added

 Revision 1.9  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.8  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.7  2006/10/05 16:58:55  poth
 required changes to support none-GML formated inserts and updates

 Revision 1.6  2006/09/14 00:01:20  mschneider
 Little corrections + javadoc fixes.

 Revision 1.5  2006/07/05 23:25:58  mschneider
 Uses extractTypeNames() from superclass now.

 Revision 1.4  2006/07/04 14:49:22  mschneider
 Removed checkServiceParameter(). Now in superclass.

 Revision 1.3  2006/06/07 17:19:55  mschneider
 Renamed parseTransaction() to parse().

 Revision 1.2  2006/06/06 17:08:32  mschneider
 Added usage of Generics for type safety.

 Revision 1.1  2006/05/16 16:25:30  mschneider
 Moved transaction related classes from org.deegree.ogcwebservices.wfs.operation to org.deegree.ogcwebservices.wfs.operation.transaction.

 ********************************************************************** */