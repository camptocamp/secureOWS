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

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.1, $Revision: 1.8 $, $Date: 2006/07/12 14:46:16 $ *  * @since 1.1
 */

public abstract class OWSValidator {

   
    protected Policy policy = null;
    protected GetCapabilitiesRequestValidator getCapabilitiesValidator = null;
    protected GetCapabilitiesResponseValidator getCapabilitiesValidatorR = null;
    
    /**
     * @param getCapabilitiesValidator
     */
    public OWSValidator(Policy policy, String proxyURL) {
        this.policy = policy;
        getCapabilitiesValidator = new GetCapabilitiesRequestValidator( policy );
        getCapabilitiesValidatorR = 
        	new GetCapabilitiesResponseValidator( policy, proxyURL );
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
     * @param response
     * @param user
     *  
     */
    public abstract byte[] validateResponse(OGCWebServiceRequest request, byte[] response, 
                                            String mime, User user) 
    										throws InvalidParameterValueException,
                                            UnauthorizedException;
    
    /**
     * returns the general condition assigned to the encapsulated policy
     * 
     * @return
     */
    public Condition getGeneralCondtion() {
        return policy.getGeneralCondition();
    }
        

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSValidator.java,v $
Revision 1.8  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
