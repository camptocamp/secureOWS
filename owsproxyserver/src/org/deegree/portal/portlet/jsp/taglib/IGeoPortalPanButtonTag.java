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
package org.deegree.portal.portlet.jsp.taglib;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.deegree.framework.util.StringTools;

/**
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/29 19:54:14 $
 *
 * @since 2.0
 */
public class IGeoPortalPanButtonTag extends TagSupport {
    
    private String direction = null;
    private String imageBase = "./igeoportal/images/";
     
    /**
     * @return
     */
    public String getDirection() {
        return direction;
    }
    
    /**
     * 
     * @param direction
     */
    public void setDirection( String direction ) {
        this.direction = direction;
    }
    
    /**
     * 
     * @return
     */
    public String getImageBase() {
        return imageBase;
    }
    
    /**
     * 
     * @param imageBase
     */
    public void setImageBase( String imageBase ) {
        this.imageBase = imageBase;
    }
    
    /**
     * @return
     */
    public int doStartTag() throws JspException {
        
        ArrayList list = (ArrayList)pageContext.getRequest().getAttribute( "PANBUTTONS" );
        String portletID = (String)pageContext.getRequest().getAttribute( "PORTLETID" );
        portletID = StringTools.replace( portletID, "-", "", true );
        if ( list.contains( direction) ) {
            try {
                pageContext.getOut().flush();  
                
                String img = StringTools.concat( 300, "<a href=\"javascript:mapWindowPortlet", portletID, '.', 
                        "pan( '$2', 25 );\" ><img src='./igeoportal/images/$1Arrow.gif' ",
                        "border='0' title='pan $1'/></a>" );
                if ( direction.equals( "NORTH" ) ) {
                    img = StringTools.replace( img, "$1", "north", true );
                    img = StringTools.replace( img, "$2", "N", true );
                } else if ( direction.equals( "NORTHEAST" ) ) {
                    img = StringTools.replace( img, "$1", "northEast", true );
                    img = StringTools.replace( img, "$2", "NE", true );
                } else if ( direction.equals( "NORTHWEST" ) ) {
                    img = StringTools.replace( img, "$1", "northWest", true );
                    img = StringTools.replace( img, "$2", "NW", true );
                } else if ( direction.equals( "WEST" ) ) {
                    img = StringTools.replace( img, "$1", "west", true );
                    img = StringTools.replace( img, "$2", "W", true );
                } else if ( direction.equals( "EAST" ) ) {
                    img = StringTools.replace( img, "$1", "east", true );
                    img = StringTools.replace( img, "$2", "E", true );
                } else if ( direction.equals( "SOUTH" ) ) {
                    img = StringTools.replace( img, "$1", "south", true );
                    img = StringTools.replace( img, "$2", "S", true );
                } else if ( direction.equals( "SOUTHEAST" ) ) {
                    img = StringTools.replace( img, "$1", "southEast", true );
                    img = StringTools.replace( img, "$2", "SE", true );
                } else if ( direction.equals( "SOUTHWEST" ) ) {
                    img = StringTools.replace( img, "$1", "southWest", true );
                    img = StringTools.replace( img, "$2", "SW", true );
                } 
                
                JspWriter pw = pageContext.getOut();                
                pw.print( img.toString() );
            } catch ( IOException e ) {
                e.printStackTrace();
                String message = "Error processing name '" + direction + "'.";
                try {
                    pageContext.getOut().print( message );
                }
                catch (java.io.IOException ioe) {}
            }
        }

        return SKIP_BODY;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IGeoPortalPanButtonTag.java,v $
Revision 1.5  2006/08/29 19:54:14  poth
footer corrected

Revision 1.4  2006/05/12 14:36:28  poth
*** empty log message ***

Revision 1.3  2006/04/06 20:25:29  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.1  2006/02/05 20:34:31  poth
*** empty log message ***


********************************************************************** */