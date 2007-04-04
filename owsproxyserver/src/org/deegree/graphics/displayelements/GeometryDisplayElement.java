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
package org.deegree.graphics.displayelements;

import java.io.Serializable;

import org.deegree.graphics.sld.Symbolizer;
import org.deegree.model.feature.Feature;

/**
 * Basic interface of all display elements that are related to a geometry. * Usually this will be the case. * <p>------------------------------------------------------------------------</p> *  * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @version $Revision: 1.9 $ $Date: 2006/07/12 14:46:16 $
 */

abstract class GeometryDisplayElement extends AbstractDisplayElement
                                        implements Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 465725117946501686L;

    protected org.deegree.model.spatialschema.Geometry geometry = null;
    protected Symbolizer symbolizer = null;
    protected Symbolizer highlightSymbolizer = null;
    protected Symbolizer selectedSymbolizer = null;

    protected Object placement = null;

    /**
     * Creates a new GeometryDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     */
    GeometryDisplayElement( Feature feature, org.deegree.model.spatialschema.Geometry geometry ) {
        super( feature );
        setGeometry( geometry );
    }

    /**
     * Creates a new GeometryDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     * @param symbolizer 
     */
    GeometryDisplayElement( Feature feature, org.deegree.model.spatialschema.Geometry geometry, Symbolizer symbolizer ) {
        super( feature );
        setGeometry( geometry );
        setSymbolizer( symbolizer );
        setHighlightSymbolizer( symbolizer );
        setSelectedSymbolizer( symbolizer );
    }        
    
    /**
     * Creates a new GeometryDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     * @param symbolizer 
     * @param selectedSymbolizer
     * @param highlightSymbolizer
     */
    GeometryDisplayElement( Feature feature, org.deegree.model.spatialschema.Geometry geometry, 
                                 Symbolizer symbolizer, Symbolizer highlightSymbolizer, 
                                 Symbolizer selectedSymbolizer ) {
        super( feature );
        setGeometry( geometry );
        setSymbolizer( symbolizer );
        setSelectedSymbolizer( selectedSymbolizer );
        setHighlightSymbolizer( highlightSymbolizer );
    }

    /**
     * Overwrites the default placement of the <tt>DisplayElement</tt>.
     * This method is used by the <tt>PlacementOptimizer</tt> to minimize
     * the overlapping of labels, for example.
     * <p>
     * @param o the placement to be used
     */
    public void setPlacement(Object o) {
        placement = o;
    }

    /**
     * sets the geometry that determines the position the DisplayElement
     * will be rendered to
     */
    public void setGeometry(org.deegree.model.spatialschema.Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * returns the geometry that determines the position the DisplayElement
     * will be rendered to
     */
    public org.deegree.model.spatialschema.Geometry getGeometry() {
        return geometry;
    }

    /**
     * sets the rules that determines how the geometry will be rendered
     */
    public void setSymbolizer(Symbolizer symbolizer) {
        this.symbolizer = symbolizer;
    }

    /**
     * Returns the symbolizer that determines how the geometry will be rendered.
     */
    public Symbolizer getSymbolizer() {
        return symbolizer;
    }

    /**
     * sets the rule that determines how the geometry will be rendered when it's
     * highlighted
     * @param rule symbolizer defining rendering style
     */
    public void setHighlightSymbolizer(Symbolizer rule) {
        this.highlightSymbolizer = rule;
    }

    /**
     * returns the symbolizer that determines how the geometry will be rendered
     * if it's highlighted
     */
    public Symbolizer getHighlightSymbolizer() {
        return highlightSymbolizer;
    }

    /**
     * sets the rule that determines how the geometry will be rendered when it's
     * selected
     * @param rule symbolizer defining rendering style
     */
    public void setSelectedSymbolizer(Symbolizer rule) {
        selectedSymbolizer = rule;
    }

    /**
     * returns the symbolizer that determines how the geometry will be rendered
     * if it's selected
     */
    public Symbolizer getSelectedSymbolizer() {
        return selectedSymbolizer;
    }

    /**
     * Returns if the <tt>DisplayElement</tt> should be painted at the
     * current scale or not.
     */
    public boolean doesScaleConstraintApply (double scale) {        
        return symbolizer.getMinScaleDenominator() <= scale &&
               symbolizer.getMaxScaleDenominator() > scale;
    }    
       
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeometryDisplayElement.java,v $
Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
