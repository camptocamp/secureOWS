/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
University of Bonn
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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.security.owsrequestvalidator.wfs;

import java.util.List;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.Policy;
import org.deegree.security.owsrequestvalidator.RequestValidator;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.3 $, $Date: 2006/10/30 08:07:06 $
 * 
 * @since 1.1
 */
abstract class AbstractWFSRequestValidator extends RequestValidator {

    // known condition parameter
    private static final String FEATURETYPES = "featureTypes";
    
    private static final String INVALIDFEATURETYPE = 
        Messages.getString("AbstractWFSRequestValidator.INVALIDFEATURETYPE"); 
    
    /**
     * @param policy
     */
    public AbstractWFSRequestValidator(Policy policy) {
        super(policy);
    }
    
    /**
     * validates if the requested info featuretypes are valid against the
     * policy/condition. If the passed user <> null this is checked against 
     * the user- and rights-management system/repository
     * 
     * @param condition
     * @param featureTypes
     * @param user
     * @throws InvalidParameterValueException
     */
    protected void validateFeatureTypes( Condition condition, String[] featureTypes) 
                                                throws InvalidParameterValueException {
        
        OperationParameter op = condition.getOperationParameter( FEATURETYPES );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) return;
                
        List validLayers = op.getValues();
        if ( op.isUserCoupled() ) {
            userCoupled = true;
        } else {
            for (int i = 0; i < featureTypes.length; i++) {
                if ( !validLayers.contains( featureTypes[i] ) ) {
                    throw new InvalidParameterValueException( INVALIDFEATURETYPE + 
                                                              featureTypes[i] );
                }
            }
        }
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractWFSRequestValidator.java,v $
Revision 1.3  2006/10/30 08:07:06  poth
bug fix - WFS GetFeature request: FeatureType comparsion

Revision 1.2  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.8  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
