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
package org.deegree.security.owsrequestvalidator.wfs;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
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
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.1, $Revision: 1.4 $, $Date: 2006/10/30 08:07:06 $ *  * @since 1.1
 */

class GetFeatureRequestValidator extends AbstractWFSRequestValidator {
    
    // known condition parameter
    private static final String FORMAT = "format"; 
    private static final String MAXFEATURES = "maxFeatures"; 
    
    private static final String INVALIDFORMAT = 
        Messages.getString("GetFeatureRequestValidator.INVALIDFORMAT"); 
    private static final String INVALIDMAXFEATURES = 
        Messages.getString("GetFeatureRequestValidator.INVALIDMAXFEATURES"); 

    private static FeatureType gfFT = null;

    static {        
        if ( gfFT == null ) {
            gfFT =GetFeatureRequestValidator.createFeatureType();
        }
    }

    /**
     * @param policy
     */
    public GetFeatureRequestValidator(Policy policy) {
        super(policy);
    }
    
    /**
     * validates if the passed request is valid against the policy
     * assigned to the validator. If the passed user is not <tt>null</tt>
     * user coupled parameters will be validated against a users and 
     * rights management system.
     */
    public void validateRequest(OGCWebServiceRequest request, User user)
                    throws InvalidParameterValueException, UnauthorizedException {
        
        userCoupled = false;
        Request req =  policy.getRequest( "WFS", "GetFeature" );  
        // request is valid because no restrictions are made
        if ( req.isAny() ) return;
        Condition condition = req.getPreConditions();
        
        GetFeature wfsreq = (GetFeature)request;
        
        validateVersion( condition, wfsreq.getVersion() );
        
        Query[] queries = wfsreq.getQuery();
        String[] ft = new String[queries.length];
        StringBuffer sb = new StringBuffer( 200 );
        for (int i = 0; i < ft.length; i++) {     
            sb.delete( 0, sb.length() );
            sb.append( '{' ).append( queries[i].getTypeNames()[0].getNamespace().toASCIIString() );
            sb.append( "}:" ).append( queries[i].getTypeNames()[0].getLocalName() );
            ft[i] = sb.toString();
        }
        validateFeatureTypes( condition, ft );
        validateFormat( condition, wfsreq.getOutputFormat() );
        validateMaxFeatures( condition, wfsreq.getMaxFeatures() );
        
        if ( userCoupled ) {
            validateAgainstRightsDB( wfsreq, user );
        }

    }
    
    /**
     * valides if the format you in a GetFeature request is valid against
     * the policy assigned to Validator. If the passed user is not <tt>null</tt>
     * and the format parameter is user coupled the format will be validated
     * against a users and rights management system.
     * 
     * @param condition
     * @param format
     * @throws InvalidParameterValueException
     */
    private void validateFormat(Condition condition, String format) 
                    throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( FORMAT );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) return;
                
        List validLayers = op.getValues();
        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            if ( !validLayers.contains( format ) ) {
                throw new InvalidParameterValueException( INVALIDFORMAT + format );
            }
        }
        
    }
    
    /**
     * valides if the format you in a GetFeature request is valid against
     * the policy assigned to Validator. If the passed user is not <tt>null</tt>
     * and the maxFeatures parameter is user coupled the maxFeatures will be 
     * validated against a users and rights management system.
     * 
     * @param condition
     * @param maxFeatures
     * @throws InvalidParameterValueException
     */
    private void validateMaxFeatures(Condition condition, int maxFeatures) 
                    throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( MAXFEATURES );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) return;
                
        int maxF = Integer.parseInt( op.getValues().get( 0 ) );
        
        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            if ( maxFeatures > maxF || maxFeatures < 0 ) {
                throw new InvalidParameterValueException( INVALIDMAXFEATURES + maxFeatures );
            }
        }
        
    }
    
    /**
     * validates the passed WMS GetMap request against a User- and 
     * Rights-Management DB. 
     * 
     * @param wmsreq
     * @param user
     * @throws InvalidParameterValueException
     */
    private void validateAgainstRightsDB( GetFeature wfsreq, User user ) 
                                    throws InvalidParameterValueException, 
                                           UnauthorizedException{
        
        if ( user == null ) { 
            throw new UnauthorizedException("no access to anonymous user"); 
        }
        
        // create feature that describes the map request
        FeatureProperty[] fps = new FeatureProperty[3];
        fps[0] = FeatureFactory.createFeatureProperty("version", wfsreq.getVersion() ); 
        Integer mxf = new Integer(wfsreq.getMaxFeatures());
        //The database can handle "features as a key", this feature is build from the request's features
        fps[1] = FeatureFactory.createFeatureProperty("maxfeatures", mxf ); 
        fps[2] = FeatureFactory.createFeatureProperty("outputformat",  
                                                      wfsreq.getOutputFormat() );
        
        Feature feature = FeatureFactory.createFeature("id", gfFT, fps); 
        Query[] queries = wfsreq.getQuery();
        for (int i = 0; i < queries.length; i++) {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( '{' ).append( queries[i].getTypeNames()[0].getNamespace().toASCIIString() );
            sb.append( "}:" ).append( queries[i].getTypeNames()[0].getLocalName() );  
            handleUserCoupledRules( user, //the user who posted the request
                                    feature, //This is the Database feature
                                    sb.toString(), //the Qualified name of the users Featurerequest
                                    ClientHelper.TYPE_FEATURETYPE, //a primary key in the db.
                                    RightType.GETFEATURE );//We're requesting a featuretype.
        }        
        
    }
    
    /**
     * creates a feature type that matches the parameters of a GetLagendGraphic
     * request 
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createFeatureType() {
        PropertyType[] ftps = new PropertyType[3];
        ftps[0] = FeatureFactory.createSimplePropertyType(new QualifiedName("version"),  
                                                           Types.VARCHAR, false); 
        ftps[1] = FeatureFactory.createSimplePropertyType(new QualifiedName("maxfeatures"),  
                                                           Types.INTEGER, false); 
        ftps[2] = FeatureFactory.createSimplePropertyType(new QualifiedName("outputformat"),  
                                                           Types.VARCHAR, false); 
        
        return FeatureFactory.createFeatureType( "GetFeature", false, ftps); 
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetFeatureRequestValidator.java,v $
Revision 1.4  2006/10/30 08:07:06  poth
bug fix - WFS GetFeature request: FeatureType comparsion

Revision 1.3  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.2  2006/08/02 09:45:09  poth
changes required as consequence of changing OperationParameter

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.23  2006/05/30 14:40:14  bezema
Docu on the validateAgainsrightsDB updated - maybe for the future

Revision 1.22  2006/05/25 09:53:30  poth
adapated to changed/simplified policy xml-schema

Revision 1.21  2006/05/24 16:12:56  poth
support for WFS GetFeature validation added


********************************************************************** */