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
 Aennchenstr. 19
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
package org.deegree.security.owsrequestvalidator.csw;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.util.StringTools;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.manager.Delete;
import org.deegree.ogcwebservices.csw.manager.Insert;
import org.deegree.ogcwebservices.csw.manager.Operation;
import org.deegree.ogcwebservices.csw.manager.Transaction;
import org.deegree.ogcwebservices.csw.manager.Update;
import org.deegree.portal.standard.security.control.ClientHelper;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.Policy;
import org.w3c.dom.Element;

/**
 * Validator for OGC CSW Transaction requests. It will validated values of:<br>
 * <ul>
 *  <li>service version</li>
 *  <li>operation</li>
 *  <li>type names</li>
 *  <li>metadata standard</li>
 * </ul>
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/14 13:39:02 $
 *
 * @since 2.0
 */
public class TransactionValidator extends AbstractCSWRequestValidator {

    private final static String METADATAFORMAT = "metadataFormat";

    private final static String TYPENAME = "typeName";

    private static FeatureType insertFT = null;

    private static FeatureType updateFT = null;

    private static FeatureType deleteFT = null;

    static {
        if ( insertFT == null ) {
            insertFT = TransactionValidator.createInsertFeatureType();
        }
        if ( updateFT == null ) {
            updateFT = TransactionValidator.createUpdateFeatureType();
        }
        if ( deleteFT == null ) {
            deleteFT = TransactionValidator.createDeleteFeatureType();
        }
    }

    /**
     * 
     * @param policy
     */
    public TransactionValidator( Policy policy ) {
        super( policy );
    }

    @Override
    public void validateRequest( OGCWebServiceRequest request, User user )
                            throws InvalidParameterValueException, UnauthorizedException {

        userCoupled = false;

        Transaction cswreq = (Transaction) request;

        List<Operation> ops = cswreq.getOperations();
        for ( int i = 0; i < ops.size(); i++ ) {
            userCoupled = false;
            if ( ops.get( i ) instanceof Insert ) {
                Request req = policy.getRequest( "CSW", "CSW_Insert" );
                if ( !req.isAny() ) {
                    Condition condition = req.getPreConditions();
                    validateOperation( condition, (Insert) ops.get( i ) );
                }
                if ( userCoupled ) {
                    validateAgainstRightsDB( (Insert) ops.get( i ), user );
                }
            } else if ( ops.get( i ) instanceof Update ) {
                Request req = policy.getRequest( "CSW", "CSW_Update" );
                if ( !req.isAny() ) {
                    Condition condition = req.getPreConditions();
                    validateOperation( condition, (Update) ops.get( i ) );
                }
                if ( userCoupled ) {
                    validateAgainstRightsDB( (Update) ops.get( i ), user );
                }
            } else if ( ops.get( i ) instanceof Delete ) {
                Request req = policy.getRequest( "CSW", "CSW_Delete" );
                if ( !req.isAny() ) {
                    Condition condition = req.getPreConditions();
                    validateOperation( condition, (Delete) ops.get( i ) );
                }
                if ( userCoupled ) {
                    validateAgainstRightsDB( (Delete) ops.get( i ), user );
                }
            }
        }

    }

    /**
     * 
     * @param condition
     * @param insert
     * @throws InvalidParameterValueException
     */
    private void validateOperation( Condition condition, Insert insert )
                            throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( METADATAFORMAT );

        // version is valid because no restrictions are made
        if ( op.isAny() ) {
            return;
        }

        List vals = op.getValues();

        List<Element> records = insert.getRecords();
        for ( int i = 0; i < records.size(); i++ ) {
            String name = records.get( i ).getLocalName();
            String ns = records.get( i ).getNamespaceURI();
            String qn = StringTools.concat( 200, '{', ns, "}:", name );

            if ( !vals.contains( qn ) ) {
                if ( !op.isUserCoupled() ) {
                    String s = Messages.format( "CSWTransactionValidator.INVALIDMETADATAFORMAT", qn );
                    throw new InvalidParameterValueException( s );
                }
                userCoupled = true;
                break;
            }
        }

    }

    /**
     * 
     * @param condition
     * @param delete
     * @throws InvalidParameterValueException
     */
    private void validateOperation( Condition condition, Delete delete )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( TYPENAME );

        // version is valid because no restrictions are made
        if ( op.isAny() )
            return;

        URI typeName = delete.getTypeName();

        if ( typeName == null ) {
            String s = Messages.getString( "CSWTransactionValidator.INVALIDDELETETYPENAME1" );
            throw new InvalidParameterValueException( s );
        }

        List vals = op.getValues();
        if ( !vals.contains( typeName.toASCIIString() ) ) {
            if ( !op.isUserCoupled() ) {
                String s = Messages.format( "CSWTransactionValidator.INVALIDDELETETYPENAME2",
                                            typeName );
                throw new InvalidParameterValueException( s );
            }
            userCoupled = true;
        }

    }

    /**
     * 
     * @param condition
     * @param update
     * @throws InvalidParameterValueException
     */
    private void validateOperation( Condition condition, Update update )
                            throws InvalidParameterValueException {

        URI typeName = update.getTypeName();
        Element record = update.getRecord();

        if ( typeName == null && record == null ) {
            String s = Messages.getString( "CSWTransactionValidator.INVALIDUPDATETYPENAME1" );
            throw new InvalidParameterValueException( s );
        }

        OperationParameter op = condition.getOperationParameter( TYPENAME );
        List vals = op.getValues();

        if ( typeName != null && !vals.contains( typeName.toASCIIString() ) ) {
            // version is valid because no restrictions are made
            if ( op.isAny() ) {
                return;
            }
            if ( !op.isUserCoupled() ) {
                String s = Messages.format( "CSWTransactionValidator.INVALIDUPDATETYPENAME2",
                                            typeName );
                throw new InvalidParameterValueException( s );
            }
            userCoupled = true;
        } else {
            op = condition.getOperationParameter( METADATAFORMAT );
            // version is valid because no restrictions are made
            if ( op.isAny() ) {
                return;
            }
            vals = op.getValues();
            String name = record.getLocalName();
            String ns = record.getNamespaceURI();
            String qn = StringTools.concat( 200, '{', ns, "}:", name );
            if ( !vals.contains( qn ) ) {
                if ( !op.isUserCoupled() ) {
                    String s = Messages.format( "CSWTransactionValidator.INVALIDMETADATAFORMAT", qn );
                    throw new InvalidParameterValueException( s );
                }
                userCoupled = true;
            }
        }
    }

    /**
     * validates a Transcation.Delete request against the underlying users and
     * rights management system
     * @param delete
     * @param version
     * @param user
     * @throws InvalidParameterValueException
     * @throws UnauthorizedException
     */
    private void validateAgainstRightsDB( Delete delete, User user )
                            throws InvalidParameterValueException, UnauthorizedException {
        if ( user == null ) {
            throw new UnauthorizedException( Messages.getString( "RequestValidator.NOACCESS" ) );
        }

        // create a feature instance from the parameters of the GetRecords request
        // to enable comparsion with a filter encoding expression stored in the
        // assigned rights management system
        List<FeatureProperty> fps = new ArrayList<FeatureProperty>();
        
        URI typeName = delete.getTypeName();
        String tn = null;
        if ( typeName != null ) {
            tn = typeName.toASCIIString();
        }
        FeatureProperty fp = FeatureFactory.createFeatureProperty( "typeName", tn );
        fps.add( fp );
        Feature feature = FeatureFactory.createFeature( "id", insertFT, fps );
        
        handleUserCoupledRules( user, //the user who posted the request
                                feature, //This is the Database feature
                                //the name the metadataFormat to be deleted
                                "{http://www.opengis.net/cat/csw}:profil", 
                                ClientHelper.TYPE_METADATASCHEMA, //a primary key in the db.
                                RightType.DELETE );//We're requesting a featuretype.

    }

    /**
     * validates a Transcation.Update request against the underlying users and
     * rights management system
     * @param update
     * @param user
     */
    private void validateAgainstRightsDB( Update update, User user ) {
        throw new NoSuchMethodError( getClass().getName()
                                     + ".validateAgainstRightsDB not implemented yet" );
    }

    /**
     * validates the passed insert operation against the deegree user/rights 
     * management system 
     * 
     * @param insert
     * @param version
     * @param user
     * @throws InvalidParameterValueException
     * @throws UnauthorizedException
     */
    private void validateAgainstRightsDB( Insert insert, User user )
                            throws InvalidParameterValueException, UnauthorizedException {

        if ( user == null ) {
            throw new UnauthorizedException( Messages.getString( "RequestValidator.NOACCESS" ) );
        }

        // create a feature instance from the parameters of the GetRecords request
        // to enable comparsion with a filter encoding expression stored in the
        // assigned rights management system
        List<FeatureProperty> fps = new ArrayList<FeatureProperty>();
        FeatureProperty fp = null;
        fps.add( fp );

        Feature feature = FeatureFactory.createFeature( "id", insertFT, fps );

        List<Element> records = insert.getRecords();
        for ( int i = 0; i < records.size(); i++ ) {
            String name = records.get( i ).getLocalName();
            String ns = records.get( i ).getNamespaceURI();
            String qn = StringTools.concat( 200, '{', ns, "}:", name );

            handleUserCoupledRules( user, //the user who posted the request
                                    feature, //This is the Database feature
                                    qn, //the Qualified name of the users Featurerequest
                                    ClientHelper.TYPE_METADATASCHEMA, //a primary key in the db.
                                    RightType.INSERT );//We're requesting a featuretype.
        }

    }

    /**
     * creates a feature type that matches the parameters of a Insert operation 
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createInsertFeatureType() {
        PropertyType[] ftps = new PropertyType[1];
        ftps[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "metadataFormat" ),
                                                           Types.VARCHAR, false );

        return FeatureFactory.createFeatureType( "CSW_Insert", false, ftps );
    }

    /**
     * creates a feature type that matches the parameters of a Update operation 
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createUpdateFeatureType() {
        PropertyType[] ftps = new PropertyType[2];
        ftps[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "metadataFormat" ),
                                                           Types.VARCHAR, false );
        ftps[1] = FeatureFactory.createSimplePropertyType( new QualifiedName( "typeName" ),
                                                           Types.VARCHAR, false );

        return FeatureFactory.createFeatureType( "CSW_Update", false, ftps );
    }

    /**
     * creates a feature type that matches the parameters of a Delete operation 
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createDeleteFeatureType() {
        PropertyType[] ftps = new PropertyType[1];
        ftps[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "typeName" ),
                                                           Types.VARCHAR, false );

        return FeatureFactory.createFeatureType( "CSW_Delete", false, ftps );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TransactionValidator.java,v $
 Revision 1.5  2006/08/14 13:39:02  poth
 bug fix - removing version from tested parameters / corrected reading of policy parameter lists

 Revision 1.4  2006/08/10 07:17:52  poth
 bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

 Revision 1.3  2006/08/03 16:40:36  poth
 code formated

 Revision 1.2  2006/08/03 08:04:07  poth
 support for validating delete requests against user and rights managemend system

 Revision 1.1  2006/07/23 08:44:53  poth
 refactoring - moved validators assigned to OWS into specialized packages


 ********************************************************************** */