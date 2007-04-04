// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/ExceptionFormat.java,v 1.5 2006/04/06 20:25:27 poth Exp $
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

import java.io.Serializable;

/**
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public class ExceptionFormat implements Serializable {
    
    private String[] format = null; 

    /**
     * default format = application/vnd.ogc.se_xml
     */
    public ExceptionFormat() {
        format = new String[] {"application/vnd.ogc.se_xml"};
    }
    /**
     * @param format
     */
    public ExceptionFormat(String[] format) {
        this.format = format;
    }

    /**
     * @return Returns the format.
     * 
     * @uml.property name="format"
     */
    public String[] getFormat() {
        return format;
    }

    /**
     * @param format The format to set.
     * 
     * @uml.property name="format"
     */
    public void setFormat(String[] format) {
        this.format = format;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ExceptionFormat.java,v $
   Revision 1.5  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.4  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.3  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.4  2004/08/16 06:23:33  ap
   no message

   Revision 1.3  2004/06/16 09:46:02  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
