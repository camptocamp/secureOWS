package org.deegree.security.owsrequestvalidator.csw;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.discovery.DescribeRecord;
import org.deegree.portal.standard.security.control.ClientHelper;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsrequestvalidator.Policy;

/**
 * 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/08/15 19:00:19 $
 *
 * @since 2.0
 */
class DescribeRecordRequestValidator extends AbstractCSWRequestValidator {
       
    private static FeatureType drtFT = null;

    static {        
        if ( drtFT == null ) {
            drtFT =DescribeRecordRequestValidator.createFeatureType();
        }
    }


    /**
     * @param policy
     */
    public DescribeRecordRequestValidator(Policy policy) {
        super(policy);
    }
    
    /* (non-Javadoc)
     * @see org.deegree_impl.security.RequestValidator#validateRequest(org.deegree.services.OGCWebServiceRequest, java.lang.String)
     */
    public void validateRequest(OGCWebServiceRequest request, User user)
                    throws InvalidParameterValueException, UnauthorizedException {
        userCoupled = false;
        Request req =  policy.getRequest( "CSW", "DescribeRecord" );  
        // request is valid because no restrictions are made
        if ( req.isAny() ) return;
        Condition condition = req.getPreConditions();
        
        DescribeRecord cswreq = (DescribeRecord)request;
        
        validateVersion( condition, cswreq.getVersion() );
        
        
        if ( userCoupled ) {
            validateAgainstRightsDB( cswreq, user );
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
    private void validateAgainstRightsDB( DescribeRecord wfsreq, User user ) 
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
        
        Feature feature = FeatureFactory.createFeature("id", drtFT, fps); 
        handleUserCoupledRules( user, feature, "", 
                                ClientHelper.TYPE_METADATASCHEMA, 
                                RightType.DESCRIBERECORDTYPE );
        
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
        
        return FeatureFactory.createFeatureType( "DescribeRecord", false, ftps); 
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeRecordRequestValidator.java,v $
Revision 1.1  2006/08/15 19:00:19  poth
*** empty log message ***

********************************************************************** */