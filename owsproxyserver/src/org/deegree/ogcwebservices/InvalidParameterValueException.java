// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/InvalidParameterValueException.java,v 1.6 2006/04/06 20:25:27 poth Exp $
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

import org.deegree.framework.util.StringTools;
import org.deegree.ogcbase.ExceptionCode;


/**
 * Operation request contains an invalid parameter value
 * <p>locator = Name of parameter with invalid value
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public class InvalidParameterValueException extends OGCWebServiceException  {
    
    
    /**
     * @param message
     */
    public InvalidParameterValueException(String message) {
        super( message );    
        this.code = ExceptionCode.INVALIDPARAMETERVALUE;
    }
    
    
    /**
     * @param locator
     * @param message
     */
    public InvalidParameterValueException(String locator, String message) {
        super( locator, message );
        this.code = ExceptionCode.INVALIDPARAMETERVALUE;
    }
    
    /**
     * @param ex
     */
    public InvalidParameterValueException(Throwable ex) {
        super( "", StringTools.stackTraceToString( ex ) );
        this.code = ExceptionCode.INVALIDPARAMETERVALUE;
    }
    
    /**
     * @param message
     * @param ex
     */
    public InvalidParameterValueException(String message, Throwable ex) {
        super( "", message + StringTools.stackTraceToString( ex ) );
        this.code = ExceptionCode.INVALIDPARAMETERVALUE;
    }
    
    /**
     * @param locator
     * @param message
     * @param code 
     */
    public InvalidParameterValueException(String locator, String message, ExceptionCode code) {
        super( locator, message, code );        
    }
    
          
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: InvalidParameterValueException.java,v $
   Revision 1.6  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.3  2005/09/27 19:53:18  poth
   no message

   Revision 1.2  2005/04/07 20:31:49  poth
   no message

   Revision 1.5  2004/07/12 06:12:11  ap
   no message

   Revision 1.4  2004/06/18 06:18:45  ap
   no message

   Revision 1.3  2004/06/16 11:48:17  ap
   no message

   Revision 1.2  2004/06/16 09:46:02  ap
   no message


********************************************************************** */