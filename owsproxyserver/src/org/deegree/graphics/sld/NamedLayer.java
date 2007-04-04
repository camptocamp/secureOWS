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
package org.deegree.graphics.sld;



import org.deegree.framework.xml.Marshallable;


/**
 * A NamedLayer uses the "name" attribute to identify a layer known to the WMS
 * and can contain zero or more styles, either NamedStyles or UserStyles. In the
 * absence of any styles the default style for the layer is used.
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @version $Revision: 1.7 $ $Date: 2006/07/29 08:51:12 $
 */
public class NamedLayer extends AbstractLayer implements Marshallable {
    /**
    * constructor initializing the class with the <NamedLayer>
    */
    public NamedLayer( String name, LayerFeatureConstraints layerFeatureConstraints, 
                     AbstractStyle[] styles ) {
        super( name, layerFeatureConstraints, styles );
    }
    
    /**
     * exports the content of the Font as XML formated String
     *
     * @return xml representation of the Font
     */
    public String exportAsXML() {
        
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<NamedLayer>" );
        sb.append( "<Name>" ).append( name ).append( "</Name>" );
        
        if (layerFeatureConstraints != null) {
        	sb.append( ((Marshallable)layerFeatureConstraints).exportAsXML() );
        } 
        
        for (int i = 0; i < styles.size(); i++) {
            sb.append( ((Marshallable)styles.get(i)).exportAsXML() );
        }
        sb.append( "</NamedLayer>" );
        
        
        return sb.toString();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: NamedLayer.java,v $
Revision 1.7  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
