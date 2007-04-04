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
53115 Bonn
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
package org.deegree.security.owsrequestvalidator;

import java.util.List;
import java.util.Properties;

import org.deegree.framework.util.StringTools;
import org.deegree.model.feature.Feature;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.WrongCredentialsException;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.SecuredObject;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.DefaultDBConnection;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsproxy.SecurityConfig;

/**
 * basic class for validating OWS requests
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.1, $Revision: 1.20 $, $Date: 2006/10/04 10:54:49 $ *  * @since 1.1
 */

public abstract class RequestValidator {
    
    private static final String VERSION = "version"; 
    private static final String EXCEPTION = "exception"; 
    
    // message strings   
    private static final String INVALIDEXCEPTIONS = 
        Messages.getString("RequestValidator.INVALIDEXCEPTIONS");
    private static final String UNAUTORIZEDACCESS = 
        Messages.getString("RequestValidator.UNAUTORIZEDACCESS");

  
    protected Policy policy = null;
    protected GeneralPolicyValidator gpv = null;
    protected boolean userCoupled = false;
    protected SecurityConfig securityConfig = null;
     
    
    /**
     * @param policy
     */
    public RequestValidator(Policy policy) {
        this.policy = policy;        
        Condition cond = policy.getGeneralCondition();
        gpv = new GeneralPolicyValidator( cond );
        securityConfig = policy.getSecurityConfig();
        
        // XXXsyp
        securityConfig = null;
        
        if ( securityConfig != null ) {
            DefaultDBConnection db = securityConfig.getRegistryConfig().getDbConnection();
            Properties properties = new Properties();
            properties.setProperty("driver", db.getDirver() ); 
            properties.setProperty("url", db.getUrl() ); 
            properties.setProperty("user", db.getUser() ); 
            properties.setProperty("password", db.getPassword() ); 
            try {
                if ( !SecurityAccessManager.isInitialized() ) {
                    SecurityAccessManager.initialize(securityConfig.getRegistryClass(),
                                    properties, securityConfig.getReadWriteTimeout()*1000 );
                }
            } catch (GeneralSecurityException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * @return Returns the policy.
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * @param policy The policy to set.
     */
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    
    /**
     * validates if the passed request itself and its content is valid
     * against the conditions defined in the policies assigned to a
     * <tt>OWSPolicyValidator</tt>
     * @param request
     * @param user
     */
    public abstract void validateRequest(OGCWebServiceRequest request, User user)
                    throws InvalidParameterValueException, UnauthorizedException;
    
    /**
     * 
     * @param version
     * @param updateSeq
     * @throws InvalidParameterValueException
     */
    protected void validateVersion( Condition condition, String version ) 
    				throws InvalidParameterValueException {
        OperationParameter op = condition.getOperationParameter( VERSION );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) return;        
        List list = op.getValues();
        if (!list.contains(version) ) {
            if ( !op.isUserCoupled() ) {
                String INVALIDVERSION = Messages.format( "RequestValidator.INVALIDVERSION", version );
                throw new InvalidParameterValueException( INVALIDVERSION );
            }
            userCoupled = true;
        }
        
    }
    
    /**
     * checks if the passed exceptions format is valid against the exceptions formats 
     * defined in the policy. If <tt>user</ff> != <tt>null</tt> the valid exceptions 
     * formats will be read from the user/rights repository
     * @param condition condition containing the definition of the valid exceptions
     * @param exceptions
     * @throws InvalidParameterValueException
     */
    protected void validateExceptions(Condition condition, String exceptions) 
                                            throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( EXCEPTION );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) return;
        
        List list = op.getValues();
        if (!list.contains( exceptions ) ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDEXCEPTIONS + exceptions );
            } 
            userCoupled = true;
        }
        
    }

    /**
     * handles the validation of user coupled parameters of a request
     * 
     * @param feature
     * @throws UnauthorizedException
     */
    protected void handleUserCoupledRules(User user, Feature feature, 
                                          String secObjName, String secObjType,
                                          RightType rightType) throws 
                                          UnauthorizedException, 
                                          InvalidParameterValueException {        
        try {                        
            SecurityAccessManager sam = SecurityAccessManager.getInstance();             
            SecurityAccess access = sam.acquireAccess( user );
            SecuredObject secObj = access.getSecuredObjectByName(secObjName,secObjType);
            if ( !user.hasRight(access, rightType, feature, secObj) ) {
                throw new UnauthorizedException( UNAUTORIZEDACCESS + secObjName + ':' + feature); 
            }
        } catch (WrongCredentialsException e) {
            throw new UnauthorizedException(  e.getMessage() );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new UnauthorizedException( e.getMessage() );
        } catch (Exception e) {        
            throw new InvalidParameterValueException( StringTools.stackTraceToString(e)); 
        }
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RequestValidator.java,v $
Revision 1.20  2006/10/04 10:54:49  poth
*** empty log message ***

Revision 1.19  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.18  2006/08/02 18:51:40  poth
bug fixes

Revision 1.17  2006/07/31 12:33:39  poth
comments corrected

Revision 1.16  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.15  2006/07/22 15:16:29  poth
comments corrected

Revision 1.14  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
