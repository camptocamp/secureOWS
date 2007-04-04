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
package org.deegree.framework.xml;

import org.deegree.framework.util.StringTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Utility methods for handling geometries within XSLT transformations
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/17 20:31:19 $
 *
 * @since 2.0
 */
public class GeometryUtils {

    /**
     * 
     * @param node
     * @return
     */
    public static String getPolygonCoordinatesFromEnvelope(Node node) {
        StringBuffer sb = new StringBuffer( 500 );
        try {
            Envelope env = GMLGeometryAdapter.wrapBox( (Element)node );            
            sb.append( env.getMin().getX() ).append( ',' ).append( env.getMin().getY() ).append( ' ' );
            sb.append( env.getMin().getX() ).append( ',' ).append( env.getMax().getY() ).append( ' ' );
            sb.append( env.getMax().getX() ).append( ',' ).append( env.getMax().getY() ).append( ' ' );
            sb.append( env.getMax().getX() ).append( ',' ).append( env.getMin().getY() ).append( ' ' );
            sb.append( env.getMin().getX() ).append( ',' ).append( env.getMin().getY() );
        } catch ( Exception e ) {
            e.printStackTrace();
            sb.append( StringTools.stackTraceToString( e) );
        } 
        return sb.toString();
    }
    
    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeometryUtils.java,v $
Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.1  2006/09/29 15:29:14  poth
initial check in


********************************************************************** */