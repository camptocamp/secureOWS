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
package org.deegree.security.owsrequestvalidator.wms;

import java.util.List;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.Policy;
import org.deegree.security.owsrequestvalidator.RequestValidator;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.4 $, $Date: 2006/11/07 09:56:11 $
 * 
 * @since 1.1
 *  
 */
abstract class AbstractWMSRequestValidator extends RequestValidator {
    
    // known condition parameter
    private static final String FORMAT = "format";
    private static final String MAXWIDTH = "maxWidth";
    private static final String MAXHEIGHT = "maxHeight";
    
    private static final String INVALIDFORMAT = 
        Messages.getString("AbstractWMSRequestValidator.INVALIDFORMAT"); 
    private static final String INVALIDWIDTH1 = 
        Messages.getString("AbstractWMSRequestValidator.INVALIDWIDTH1"); 
    private static final String INVALIDWIDTH2 = 
        Messages.getString("AbstractWMSRequestValidator.INVALIDWIDTH2"); 
    private static final String INVALIDHEIGHT1 = 
        Messages.getString("AbstractWMSRequestValidator.INVALIDHEIGHT1"); 
    private static final String INVALIDHEIGHT2 = 
        Messages.getString("AbstractWMSRequestValidator.INVALIDHEIGHT2"); 

    /**
     * @param policy
     */
    public AbstractWMSRequestValidator(Policy policy) {
        super(policy);
    }
    
    /**
     * checks if the passed format is valid against the formats 
     * defined in the policy. If <tt>user</ff> != <tt>null</tt> the valid  
     * formats will be read from the user/rights repository
     * @param condition condition containing the definition of the valid format
     * @param format
     * @throws InvalidParameterValueException
     */
    protected void validateFormat(Condition condition, String format) 
                                            throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( FORMAT );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) {
            return;
        }
        
        List list = op.getValues();

        if (!list.contains( format ) ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDFORMAT + format );
            } 
            userCoupled = true;
        }
        
    }

    /**
     * checks if the passed width is > 0  and if it's valid against the maxWidth 
     * defined in the policy. If <tt>user</ff> != <tt>null</tt> the valid  
     * width will be read from the user/rights repository
     * @param condition condition containing the definition of the valid width
     * @param width
     * @throws InvalidParameterValueException
     */
    protected void validateMaxWidth(Condition condition, int width) 
                                            throws InvalidParameterValueException {

        if ( width < 1 ) {
            throw new InvalidParameterValueException( INVALIDWIDTH1 + width );
        }
        
        OperationParameter op = condition.getOperationParameter( MAXWIDTH );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) {
            return;
        }
          
        if ( width > op.getFirstAsInt() ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDWIDTH2 + width );
            }
            userCoupled = true;
        }
        
    }

    /**
     * checks if the passed height is > 0  and if it's valid against the maxHeight 
     * defined in the policy. If <tt>user</ff> != <tt>null</tt> the valid  
     * height will be read from the user/rights repository
     * @param condition condition containing the definition of the valid height
     * @param height
     * @param user
     * @throws InvalidParameterValueException
     */
    protected void validateMaxHeight(Condition condition, int height) 
                                        throws InvalidParameterValueException {

        if ( height < 1 ) {
            throw new InvalidParameterValueException( INVALIDHEIGHT1 + height );
        }
        
        OperationParameter op = condition.getOperationParameter( MAXHEIGHT );
        
        // version is valid because no restrictions are made
        if ( op.isAny() ) {
            return;
        }
        
        if ( height > op.getFirstAsInt() ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDHEIGHT2 + height );
            }
            userCoupled = true;
        }
        
    }

    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractWMSRequestValidator.java,v $
Revision 1.4  2006/11/07 09:56:11  poth
support for GetMap SLD parameter added

Revision 1.3  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.2  2006/08/02 09:45:09  poth
changes required as consequence of changing OperationParameter

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
