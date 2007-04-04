//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/security/owsrequestvalidator/csw/GetRecordsRequestValidator.java,v 1.3 2006/08/07 06:47:02 poth Exp $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.discovery.GetRecords;
import org.deegree.ogcwebservices.csw.discovery.Query;
import org.deegree.portal.standard.security.control.ClientHelper;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.Policy;

/**
 * 
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/08/07 06:47:02 $
 *
 * @since 2.0
 */
public class GetRecordsRequestValidator extends AbstractCSWRequestValidator {

    private static final String ELEMENTSETNAME = "elementSetName";
    
    private static final String MAXRECORDS = "maxRecords";

    private static final String OUTPUTFORMAT = "outputFormat";

    private static final String RESULTTYPE = "resultType";

    private static final String SORTBY = "sortBy";

    private static final String TYPENAMES = "typeNames";
    
    

    private static FeatureType grFT = null;

    static {
        if ( grFT == null ) {
            grFT = GetRecordsRequestValidator.createFeatureType();
        }
    }

    /**
     * @param policy
     */
    public GetRecordsRequestValidator( Policy policy ) {
        super( policy );
    }

    /**
     * @param request
     * @param user
     */
    public void validateRequest( OGCWebServiceRequest request, User user )
                            throws InvalidParameterValueException, UnauthorizedException {

        userCoupled = false;
        Request req = policy.getRequest( "CSW", "GetRecords" );
        // request is valid because no restrictions are made
        if ( req.isAny() )
            return;
        Condition condition = req.getPreConditions();

        GetRecords casreq = (GetRecords) request;

        validateVersion( condition, casreq.getVersion() );

        Query[] queries = casreq.getQueries();
        String[] tn = new String[queries.length];
        for ( int i = 0; i < tn.length; i++ ) {
            tn[i] = queries[i].getTypeNames()[0];
        }

        //validateRecordTypes( condition, tn );
        validateMaxRecords( condition, casreq.getMaxRecords() );
        validateOutputFormat( condition, casreq.getOutputFormat() );
        validateResultType( condition, casreq.getResultTypeAsString() );
        validateElementSetName( condition, casreq.getQueries()[0].getElementSetName() );
        validateSortBy( condition, casreq.getQueries()[0].getSortProperties() );
        List list = Arrays.asList( casreq.getQueries()[0].getTypeNames() );
        list = new ArrayList( list );
        validateTypeNames( condition, list );
        
        if ( userCoupled ) {
            validateAgainstRightsDB( casreq, user );
        }

    }

    /**
     * validates the passed CSW GetRecords request against a User- and Rights-Management DB.
     * 
     * @param casreq
     * @param user
     */
    private void validateAgainstRightsDB( GetRecords casreq, User user ) 
                        throws InvalidParameterValueException, UnauthorizedException {
        
        if ( user == null ) {
            throw new UnauthorizedException( Messages.getString( "RequestValidator.NOACCESS" ) );
        }
        
        // create a feature instance from the parameters of the GetRecords request
        // to enable comparsion with a filter encoding expression stored in the
        // assigned rights management system 
        List<FeatureProperty> fp = new ArrayList<FeatureProperty>();
        fp.add( FeatureFactory.createFeatureProperty( "version", casreq.getVersion() ) );
        fp.add( FeatureFactory.createFeatureProperty( "maxRecords", casreq.getMaxRecords() ) );
        fp.add( FeatureFactory.createFeatureProperty( "outputFormat", casreq.getOutputFormat() ) );
        fp.add( FeatureFactory.createFeatureProperty( "resultType", casreq.getResultTypeAsString() ) );        
        SortProperty[] sp = casreq.getQueries()[0].getSortProperties();
        if ( sp != null ) {
            for ( int i = 0; i < sp.length; i++ ) {
                fp.add( FeatureFactory.createFeatureProperty( "sortBy", sp[i].getSortProperty().getAsString() ) );
            }
        }
        String[] tp = casreq.getQueries()[0].getTypeNames();
        for ( int i = 0; i < tp.length; i++ ) {
            fp.add( FeatureFactory.createFeatureProperty( "typeNames", tp[i] ) );
        }
        fp.add( FeatureFactory.createFeatureProperty( "elementSetName", 
                                                      casreq.getQueries()[0].getElementSetName() ) );
        
        Feature feature = FeatureFactory.createFeature( "id", grFT, fp );
        handleUserCoupledRules( user, feature, casreq.getOutputSchema(), 
                                ClientHelper.TYPE_METADATASCHEMA, 
                                RightType.GETRECORDS );
        
    }

    /**
     * valides if the maxRecords parameter in a GetRecords request is valid against
     * the policy assigned to Validator. 
     * 
     * @param condition
     * @param maxRecords
     * @throws InvalidParameterValueException
     */
    private void validateMaxRecords( Condition condition, int maxRecords )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( MAXRECORDS );

        // is valid because no restrictions are made
        if ( op.isAny() )
            return;

        int maxF = op.getFirstAsInt();

        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            if ( maxRecords > maxF || maxRecords < 0 ) {
                String s = Messages.format( "GetRecordsRequestValidator.INVALIDMAXRECORDS",
                                            MAXRECORDS );
                throw new InvalidParameterValueException( s );
            }
        }

    }
    
    /**
     * valides if the elementSetName parameter in a GetRecords request is valid against
     * the policy assigned to Validator.
     * 
     * @param condition
     * @param elementSetName
     * @throws InvalidParameterValueException
     */
    private void validateElementSetName( Condition condition, String elementSetName )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( ELEMENTSETNAME );

        // is valid because no restrictions are made
        if ( op.isAny() )
            return;

        List<String> list = op.getValues();

        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            if ( !list.contains( elementSetName ) ) {
                String s = Messages.format( "GetRecordsRequestValidator.INVALIDELEMENTSETNAME",
                                            elementSetName );
                throw new InvalidParameterValueException( s );
            }
        }

    }

    /**
     * valides if the metadataFormat parameter in a GetRecords request is valid against
     * the policy assigned to Validator.
     * 
     * @param condition
     * @param outputFormat
     * @throws InvalidParameterValueException
     */
    private void validateOutputFormat( Condition condition, String outputFormat )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( OUTPUTFORMAT );

        // is valid because no restrictions are made
        if ( op.isAny() )
            return;

        List<String> list = op.getValues();

        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            if ( !list.contains( outputFormat ) ) {
                String s = Messages.format( "GetRecordsRequestValidator.INVALIDOUTPUTFORMAT",
                                            outputFormat );
                throw new InvalidParameterValueException( s );
            }
        }

    }

    /**
     * valides if the resultType parameter in a GetRecords request is valid against
     * the policy assigned to Validator.
     * 
     * @param condition
     * @param resultType
     * @throws InvalidParameterValueException
     */
    private void validateResultType( Condition condition, String resultType )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( RESULTTYPE );

        // is valid because no restrictions are made
        if ( op.isAny() )
            return;

        List<String> list = op.getValues();

        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            if ( !list.contains( resultType ) ) {
                String s = Messages.format( "GetRecordsRequestValidator.INVALIDRESULTTYPE",
                                            resultType );
                throw new InvalidParameterValueException( s );
            }
        }

    }

    /**
     * valides if the sortBy parameter in a GetRecords request is valid against
     * the policy assigned to Validator.
     * 
     * @param condition
     * @param sortBy
     * @throws InvalidParameterValueException
     */
    private void validateSortBy( Condition condition, SortProperty[] sortBy )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( SORTBY );

        // is valid because no restrictions are made or
        // nothing to validate
        if ( op.isAny() || sortBy == null )
            return;

        List<String> list = op.getValues();

        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            for ( int i = 0; i < sortBy.length; i++ ) {

                if ( !list.contains( sortBy[i].getSortProperty().getAsString() ) ) {
                    String s = Messages.format( "GetRecordsRequestValidator.INVALIDSORTBY",
                                                sortBy[i] );
                    throw new InvalidParameterValueException( s );
                }
            }
        }

    }
    
    /**
     * valides if the sortBy parameter in a GetRecords request is valid against
     * the policy assigned to Validator.
     * 
     * @param condition
     * @param typeNames
     * @throws InvalidParameterValueException
     */
    private void validateTypeNames( Condition condition, List<String> typeNames )
                            throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( TYPENAMES );

        // is valid because no restrictions are made
        if ( op.isAny() )
            return;

        List<String> list = op.getValues();

        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            for ( int i = 0; i < typeNames.size(); i++ ) {
                if ( !list.contains( typeNames.get(i) ) ) {
                    String s = Messages.format( "GetRecordsRequestValidator.INVALIDTYPENAMES",
                                                typeNames.get(i) );
                    throw new InvalidParameterValueException( s );
                }
            }
        }

    }

    /**
     * creates a feature type that matches the parameters of a GetRecords
     * request 
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createFeatureType() {
        PropertyType[] ftps = new PropertyType[7];
        QualifiedName qn = new QualifiedName( "version" );
        ftps[0] = FeatureFactory.createSimplePropertyType( qn, Types.VARCHAR, false );
        
        qn = new QualifiedName( "maxRecords" );
        ftps[1] = FeatureFactory.createSimplePropertyType( qn, Types.INTEGER, false );
        
        qn = new QualifiedName( "outputFormat" );
        ftps[2] = FeatureFactory.createSimplePropertyType( qn, Types.VARCHAR, false );
        
        qn = new QualifiedName( "resultType" );
        ftps[3] = FeatureFactory.createSimplePropertyType( qn, Types.VARCHAR, false );
        
        qn = new QualifiedName( "sortBy" );
        ftps[4] = FeatureFactory.createSimplePropertyType( qn, Types.VARCHAR, 0, Integer.MAX_VALUE );
        
        qn = new QualifiedName( "typeNames" );
        ftps[5] = FeatureFactory.createSimplePropertyType( qn, Types.VARCHAR, 0, Integer.MAX_VALUE );
        
        qn = new QualifiedName( "elementSetName" );
        ftps[6] = FeatureFactory.createSimplePropertyType( qn, Types.VARCHAR, false );
        
        return FeatureFactory.createFeatureType( "GetRecords", false, ftps );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetRecordsRequestValidator.java,v $
 Revision 1.3  2006/08/07 06:47:02  poth
 call of an unsupported methdo removed

 Revision 1.2  2006/08/03 08:02:24  poth
 implementation completed

 Revision 1.1  2006/08/03 07:39:31  poth
 class renamed from GetRecordRequestValidator to GetRecordsRequestValidator / bug-fix setting elementSetName comparing request parameter against rights database

 Revision 1.3  2006/08/02 20:32:49  poth
 bug fix - support for validating elementSetName added

 Revision 1.2  2006/08/02 14:15:23  poth
 support for missing request parameters added

 Revision 1.1  2006/07/23 08:44:53  poth
 refactoring - moved validators assigned to OWS into specialized packages

 Revision 1.2  2006/05/25 09:53:30  poth
 adapated to changed/simplified policy xml-schema

 Revision 1.1  2006/04/19 12:48:34  poth
 *** empty log message ***

 Revision 1.3  2006/03/23 16:28:56  poth
 *** empty log message ***

 Revision 1.2  2005/09/27 19:44:55  poth
 no message

 Revision 1.1  2005/09/27 07:27:14  poth
 no message


 ********************************************************************** */