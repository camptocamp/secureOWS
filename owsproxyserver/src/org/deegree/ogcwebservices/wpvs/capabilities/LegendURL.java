//$Header$
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
 Aennchenstraße 19
 53177 Bonn
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

package org.deegree.ogcwebservices.wpvs.capabilities;

import java.net.URL;

import org.deegree.ogcbase.ImageURL;

/**
 * TODO this class is a COPY of org.deegree.ogcwebservices.wms.capabilities.LegendURL
 * it needs to be moved up!
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class LegendURL extends ImageURL  {

    /**
     * Creates a new legendURL object from witdh, height, format and onlineResource.
     * 
     * @param width
     * @param height
     * @param format
     * @param onlineResource
     */
    public LegendURL(int width, int height, String format, URL onlineResource) {
        super(width, height, format, onlineResource);
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/08/24 06:42:15  poth
File header corrected

Revision 1.5  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.3  2005/12/05 09:36:38  mays
revision of comments

Revision 1.2  2005/12/01 10:30:14  mays
add standard footer to all java classes in wpvs package

******************************************************************** */
