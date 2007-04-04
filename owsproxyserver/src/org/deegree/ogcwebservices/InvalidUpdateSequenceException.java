// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/InvalidUpdateSequenceException.java,v 1.5 2006/07/28 08:01:27 schmitz Exp $
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
package org.deegree.ogcwebservices;

import org.deegree.ogcbase.ExceptionCode;


/**
 * Value of (optional) updateSequence parameter in GetCapabilities 
 * operation request is greater than current value of service 
 * metadata updateSequence number
 * <p>locator = None, omit �locator� parameter
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/07/28 08:01:27 $
 *
 * @since 2.0
 */
public class InvalidUpdateSequenceException extends OGCWebServiceException  {
    
    
    /**
     * @param message
     */
    public InvalidUpdateSequenceException(String message) {
        super( message );       
        this.code = ExceptionCode.INVALID_UPDATESEQUENCE;
    }
    
    
    /**
     * @param locator
     * @param message
     */
    public InvalidUpdateSequenceException(String locator, String message) {
        super( locator, message );
        this.code = ExceptionCode.INVALID_UPDATESEQUENCE;
    }
    
    /**
     * @param locator
     * @param message
     * @param code 
     */
    public InvalidUpdateSequenceException(String locator, String message, ExceptionCode code) {
        super( locator, message, code );        
    }
    
          
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: InvalidUpdateSequenceException.java,v $
   Revision 1.5  2006/07/28 08:01:27  schmitz
   Updated the WMS for 1.1.1 compliance.
   Fixed some documentation.

   Revision 1.4  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:30:48  poth
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/06/18 06:18:45  ap
   no message

   Revision 1.1  2004/06/16 09:46:02  ap
   no message


********************************************************************** */