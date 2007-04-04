// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/ExceptionCode.java,v 1.9 2006/11/22 14:06:26 schmitz Exp $
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
package org.deegree.ogcbase;

/**
 * @version $Revision: 1.9 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: schmitz $ *  * @version 1.0. $Revision: 1.9 $, $Date: 2006/11/22 14:06:26 $ *  * @since 2.0
 */

public class ExceptionCode {

    public static final ExceptionCode INVALID_FORMAT = new ExceptionCode(
        "InvalidFormat");
    public static final ExceptionCode INVALID_UPDATESEQUENCE = new ExceptionCode(
        "InvalidUpdateSequence");
    public static final ExceptionCode CURRENT_UPDATE_SEQUENCE = new ExceptionCode(
        "CurrentUpdateSequence");
    public static final ExceptionCode MISSINGPARAMETERVALUE = new ExceptionCode(
        "MissingParameterValue");
    public static final ExceptionCode INVALIDPARAMETERVALUE = new ExceptionCode(
        "InvalidParameterValue");
    public static final ExceptionCode OPERATIONNOTSUPPORTED = new ExceptionCode(
        "OperationNotSupported");
    public static final ExceptionCode VERSIONNEGOTIATIONFAILED = new ExceptionCode(
        "VersionNegotiationFailed");
    public static final ExceptionCode NOAPPLICABLECODE = new ExceptionCode(
        "NoApplicableCode");
    
    public static final ExceptionCode LAYER_NOT_DEFINED = new ExceptionCode( "LayerNotDefined" );

    public static final ExceptionCode STYLE_NOT_DEFINED = new ExceptionCode( "StyleNotDefined" );
    
    public static final ExceptionCode INVALID_SRS = new ExceptionCode( "InvalidSRS" );
    
    public static final ExceptionCode INVALID_CRS = new ExceptionCode( "InvalidCRS" );

    public static final ExceptionCode LAYER_NOT_QUERYABLE = new ExceptionCode( "LayerNotQueryable" );
    
    public static final ExceptionCode INVALID_POINT = new ExceptionCode( "InvalidPoint" );
    
    public String value = "InvalidFormat";
    
    /**
     * default = TC211     
     */
    public ExceptionCode() {
    }
    
    /**
     * @param value
     */
    public ExceptionCode(String value) {
        this.value = value;
    }
    
    /**
     * Compares the specified object with
     * this enum for equality.
     */
    public boolean equals(Object object)
    {
        if (object!=null && getClass().equals(object.getClass()))
        {
            return ((ExceptionCode) object).value.equals( value );
        }
        return false;
    }
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ExceptionCode.java,v $
   Revision 1.9  2006/11/22 14:06:26  schmitz
   Fixed some minor details in the WMS example configuration.
   Added CRS:84 to proj4.
   Fixed exception handling for WMS.

   Revision 1.8  2006/09/08 08:42:02  schmitz
   Updated the WMS to be 1.1.1 conformant once again.
   Cleaned up the WMS code.
   Added cite WMS test data.

   Revision 1.7  2006/07/28 08:01:27  schmitz
   Updated the WMS for 1.1.1 compliance.
   Fixed some documentation.

   Revision 1.6  2006/06/25 20:34:08  poth
   *** empty log message ***

   Revision 1.5  2006/04/06 20:25:22  poth
   *** empty log message ***

   Revision 1.4  2006/04/04 20:39:40  poth
   *** empty log message ***

   Revision 1.3  2006/03/30 21:20:24  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.8  2004/07/12 06:12:11  ap
   no message

   Revision 1.7  2004/06/28 15:40:13  mschneider
   Finished the generation of the ServiceIdentification part of the
   Capabilities from DOM, added functionality to the XMLTools helper
   class.

   Revision 1.6  2004/06/18 06:18:45  ap
   no message

   Revision 1.5  2004/06/16 11:48:17  ap
   no message

   Revision 1.4  2004/06/16 09:46:02  ap
   no message

   Revision 1.3  2004/06/14 15:50:23  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:52:07  ap
   no message


********************************************************************** */
