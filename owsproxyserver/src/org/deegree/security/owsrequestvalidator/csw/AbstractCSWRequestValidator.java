//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/security/owsrequestvalidator/csw/AbstractCSWRequestValidator.java,v 1.2 2006/08/07 06:37:46 poth Exp $
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
package org.deegree.security.owsrequestvalidator.csw;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsrequestvalidator.Policy;
import org.deegree.security.owsrequestvalidator.RequestValidator;




/**
 * Abstract super class for validating catalogue requests.  
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/08/07 06:37:46 $
 *
 * @since 2.0
 */
public abstract class AbstractCSWRequestValidator extends RequestValidator {
    

    /**
     * initializes the AbstractCSWRequestValidator by passing an instance
     * of the policy to be used by each concrete implementation 
     * 
     * @param policy
     */
    public AbstractCSWRequestValidator(Policy policy) {
        super( policy );
    }
    
    /**
     * validates the requested record type / outputTypeRec. If the current user is 
     * not allowed to request a record type (e.g. ISO 19115) an UnauthorizedException
     * will be thrown.
     *  
     * @param condition
     * @param typeNames
     * @throws InvalidParameterValueException
     * @throws UnauthorizedException
     */
    public void validateRecordTypes( Condition condition, String[] typeNames ) {
        throw new UnsupportedOperationException( "validateRecordTypes is not implemented yet" );
    }

    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractCSWRequestValidator.java,v $
Revision 1.2  2006/08/07 06:37:46  poth
throw UnsupportedOperation exception for method validateRecordTypes(..)

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.1  2006/04/19 12:48:34  poth
*** empty log message ***

Revision 1.2  2006/03/23 16:28:56  poth
*** empty log message ***

Revision 1.1  2005/09/27 07:27:14  poth
no message


********************************************************************** */