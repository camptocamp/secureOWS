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
package org.deegree.portal.standard.wms.util;

import org.deegree.ogcwebservices.wms.capabilities.Layer;




/**
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class ClientHelper {

    /**
     * 
     * @param root
     * @return
     */
    public static String getLayersAsTree(Layer root) {
        StringBuffer sb = new StringBuffer(10000);
        sb.append("<h3>").append( root.getTitle() ).append(":</h3>");
        Layer[] layers = root.getLayer();
        int indent = 0;
        for (int i = 0; i < layers.length; i++) {            
            appendLayer( layers[i], indent, sb);
        }
        return sb.toString();
    }
    
    private static void appendLayer( Layer layer, int indent, StringBuffer target) {
        indent++;
        String s = "";
        for (int i = 0; i < indent; i++) {
            s = s + "&nbsp;";
        }
        target.append(s);
        if ( layer.getName() != null ) {
            target.append( s ).append( "<input name='LAYER' type='checkbox' " )
                  .append( "value='").append( layer.getName() )
                  .append('|').append(layer.getTitle()).append('|')
                  .append( layer.isQueryable() ).append( "' >")
                  .append(layer.getTitle()).append("</input><BR/>\n");            
        } else {
            target.append("<b>").append( layer.getTitle().concat( "<BR/>") ).append("</b>");
            Layer[] layers = layer.getLayer();
            for (int i = 0; i < layers.length; i++) {
                appendLayer( layers[i], indent, target);
            }
            target.append("<br/>");
        }        
        
    }
        
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ClientHelper.java,v $
Revision 1.8  2006/08/29 19:54:14  poth
footer corrected

Revision 1.7  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
