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

import java.net.URL;

/**
 * The address is represented by the &lt;onlineResource&gt; element.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @version 2002-03-01, $Revision: 1.7 $, $Date: 2006/07/12 14:46:14 $
 * @since 1.0
 */
public class BaseURL {

    private String format = null;

    private URL onlineResource = null;

    /**
     * constructor initializing the class with the &lt;BaseURL&gt;
     */
    public BaseURL(String format, URL onlineResource) {
        setFormat(format);
        setOnlineResource(onlineResource);
    }

    /**
     * returns the MIME type of the resource
     */
    public String getFormat() {
        return format;
    }

    /**
     * sets the MIME type of the resource
     * 
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * returns the address (URL) of the resource
     */
    public URL getOnlineResource() {
        return onlineResource;
    }

    /**
     * returns the address (URL) of the resource
     */
    public void setOnlineResource(URL onlineResource) {
        this.onlineResource = onlineResource;
    }

    /**
     * @return
     */
    public String toString() {
        String ret = null;
        ret = "format = " + format + "\n";
        ret += ("onlineResource = " + onlineResource + "\n");
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: BaseURL.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
