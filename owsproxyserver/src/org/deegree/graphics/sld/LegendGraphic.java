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
 * The LegendGraphic element gives an optional explicit Graphic symbol to be * displayed in a legend for this rule. * <p>----------------------------------------------------------------------</p> *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @version $Revision: 1.8 $ $Date: 2006/07/29 08:51:12 $
 */

public class LegendGraphic implements Marshallable {

    /**
     * 
     * @uml.property name="graphic"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Graphic graphic = null;


    /**
    * default constructor
    */
    LegendGraphic() {
    }

    /**
    * constructor initializing the class with the <LegendGraphic>
    */
    LegendGraphic( Graphic graphic ) {
        setGraphic( graphic );
    }

    /**
     * A Graphic is a graphic symbol with an inherent shape, color(s), and
     * possibly size. A graphic can be very informally defined as a little picture
     * and can be of either a raster or vector-graphic source type. The term
     * graphic is used since the term symbol is similar to symbolizer which is
     * used in a different context in SLD.
     * @return graphic
     * 
     * @uml.property name="graphic"
     */
    public Graphic getGraphic() {
        return graphic;
    }

    /**
     * sets the <Graphic>
     * @param graphic
     * 
     * @uml.property name="graphic"
     */
    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    
    /**
     * exports the content of the Font as XML formated String
     *
     * @return xml representation of the Font
     */
    public String exportAsXML() {
        
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<LegendGraphic>" );
        sb.append( ((Marshallable)graphic).exportAsXML() );
        sb.append( "</LegendGraphic>" );
        
        
        return sb.toString();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LegendGraphic.java,v $
Revision 1.8  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
