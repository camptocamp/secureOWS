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
package org.deegree.ogcwebservices.wms.capabilities;

import java.net.URL;

import org.deegree.ogcbase.ImageURL;

/**
 * A Map Server may use zero or more LegendURL elements to provide an image(s)
 * of a legend relevant to each Style of a Layer. The Format element indicates
 * the MIME type of the legend. Width and height attributes are provided to
 * assist client applications in laying out space to display the legend.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.8 $
 */
public class LegendURL extends ImageURL  {

    /**
     *  constructor initializing the class with the <LegendURL>
     * @param width
     * @param height
     * @param format
     * @param onlineResource
     */
    public LegendURL(int width, int height, String format, URL onlineResource) {
        super(width, height, format, onlineResource);
    }
    
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LegendURL.java,v $
Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
