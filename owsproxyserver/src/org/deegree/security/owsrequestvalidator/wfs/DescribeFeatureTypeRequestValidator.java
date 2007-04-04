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
import org.deegree.ogcwebservices.wfs.operation.DescribeFeatureType;
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
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/08/15 19:00:19 $
 *
 * @since 2.0
 */
class DescribeFeatureTypeRequestValidator extends AbstractWFSRequestValidator {
    
    // known condition parameter
    private static final String FORMAT = "format"; 
    
    private static final String INVALIDFORMAT = 
        Messages.getString("GetFeatureRequestValidator.INVALIDFORMAT"); 
    
    private static FeatureType gfFT = null;

    static {        
        if ( gfFT == null ) {
            gfFT =DescribeFeatureTypeRequestValidator.createFeatureType();
        }
    }


    /**
     * @param policy
     */
    public DescribeFeatureTypeRequestValidator(Policy policy) {
        super(policy);
    }
    
    /* (non-Javadoc)
     * @see org.deegree_impl.security.RequestValidator#validateRequest(org.deegree.services.OGCWebServiceRequest, java.lang.String)
     */
    public void validateRequest(OGCWebServiceRequest request, User user)
                    throws InvalidParameterValueException, UnauthorizedException {
        userCoupled = false;
        Request req =  policy.getRequest( "WFS", "DescribeFeatureType" );  
        // request is valid because no restrictions are made
        if ( req.isAny() ) return;
        Condition condition = req.getPreConditions();
        
        DescribeFeatureType wfsreq = (DescribeFeatureType)request;
        
        validateVersion( condition, wfsreq.getVersion() );
        
        QualifiedName[] typeNames = wfsreq.getTypeNames();
        String[] ft = new String[typeNames.length];
        for (int i = 0; i < ft.length; i++) {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( '{' ).append( typeNames[i].getNamespace().toASCIIString() );
            sb.append( "}:" ).append( typeNames[i].getLocalName() );
            ft[i] = sb.toString();
        }
        validateFeatureTypes( condition, ft );
        validateFormat( condition, wfsreq.getOutputFormat() );
        
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
     * validates the passed WMS GetMap request against a User- and 
     * Rights-Management DB. 
     * 
     * @param wmsreq
     * @param user
     * @throws InvalidParameterValueException
     */
    private void validateAgainstRightsDB( DescribeFeatureType wfsreq, User user ) 
                                    throws InvalidParameterValueException, 
                                           UnauthorizedException{
        
        if ( user == null ) { 
            throw new UnauthorizedException("no access to anonymous user"); 
        }
        
        // create feature that describes the map request
        FeatureProperty[] fps = new FeatureProperty[2];
        fps[0] = FeatureFactory.createFeatureProperty("version", wfsreq.getVersion() ); 
        fps[1] = FeatureFactory.createFeatureProperty("outputformat",  
                                                      wfsreq.getOutputFormat() );
        
        Feature feature = FeatureFactory.createFeature("id", gfFT, fps); 
        QualifiedName[] typeNames = wfsreq.getTypeNames();
        for (int i = 0; i < typeNames.length; i++) {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( '{' ).append( typeNames[i].getNamespace().toASCIIString() );
            sb.append( "}:" ).append( typeNames[i].getLocalName() );  
            handleUserCoupledRules( user, feature, sb.toString(), 
                                    ClientHelper.TYPE_FEATURETYPE, RightType.DESCRIBEFEATURETYPE );
        }        
        
    }
    
    /**
     * creates a feature type that matches the parameters of a GetLagendGraphic
     * request 
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createFeatureType() {
        PropertyType[] ftps = new PropertyType[2];
        ftps[0] = FeatureFactory.createSimplePropertyType(new QualifiedName("version"),  
                                                           Types.VARCHAR, false); 
        ftps[1] = FeatureFactory.createSimplePropertyType(new QualifiedName("outputformat"),  
                                                           Types.VARCHAR, false); 
        
        return FeatureFactory.createFeatureType( "DescribeFeatureType", false, ftps); 
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeFeatureTypeRequestValidator.java,v $
Revision 1.3  2006/08/15 19:00:19  poth
*** empty log message ***

Revision 1.2  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.3  2006/05/25 14:33:32  poth
support for WFS DescribeFeatureType added


********************************************************************** */