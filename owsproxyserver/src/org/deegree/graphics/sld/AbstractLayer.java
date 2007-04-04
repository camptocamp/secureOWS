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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deegree.framework.xml.Marshallable;

/**
 * <p>----------------------------------------------------------------------</p> *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @version $Revision: 1.9 $ $Date: 2006/09/15 09:17:33 $
 */

public abstract class AbstractLayer implements Marshallable {

   
    protected LayerFeatureConstraints layerFeatureConstraints = null;

    protected List<AbstractStyle> styles = null;
    
    protected String name = null;

    /**
    * constructor initializing the class with the <NamedLayer>
    */
    AbstractLayer( String name, LayerFeatureConstraints layerFeatureConstraints, AbstractStyle[] styles ) {
        this.styles = new ArrayList<AbstractStyle>();
        setName( name );
        setLayerFeatureConstraints( layerFeatureConstraints );
        setStyles( styles );
    }

    /**
     * The Name element identifies the well-known name of the layer being
     * referenced, and is required. All possible well-known names are usually
     * identified in the capabilities document for a server.
     * @return the name of the layer
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * sets the <Name>
     * @param name the name of the layer
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The LayerFeatureConstraints element is optional in a NamedLayer and allows
     * the user to specify constraints on what features of what feature types are
     * to be selected by the named-layer reference. It is essentially a filter
     * that allows the selection of fewer features than are present in the named
     * layer. 
     * @return the LayerFeatureConstraints
     * 
     */
    public LayerFeatureConstraints getLayerFeatureConstraints() {
        return layerFeatureConstraints;
    }

    /**
     * sets the <LayerFeatureConstraints>
     * @param layerFeatureConstraints the LayerFeatureConstraints
     *    
     */
    public void setLayerFeatureConstraints(
        LayerFeatureConstraints layerFeatureConstraints) {
        this.layerFeatureConstraints = layerFeatureConstraints;
    }

    /**
     * Returns the styles associated to the Layer. This may be UserStyles or
     * NamedStyles<p></p>
     * A UserStyle is at the same semantic level as a NamedStyle used in the 
     * context of a WMS. In a sense, a named style can be thought of as a 
     * reference to a hidden UserStyle that is stored inside of a map server.
     * @return the Styles of the Layer as ArrayList
     * 
     */
    public AbstractStyle[] getStyles() {
        return styles.toArray(new AbstractStyle[styles.size()]);
    }

   /**
    * Adds styles to the Layer. 
    * @param styles the styles for the layer as Array
    */ 
    public void setStyles(AbstractStyle[] styles) {
        this.styles.clear();

        if ( styles != null ) {
            this.styles = Arrays.asList( styles );
        }
    }
    
    /**
     * @see org.deegree.graphics.sld.AbstractLayer#getStyles()
     * @param style a style to add
     */
    public void addStyle( AbstractStyle style ) {
        styles.add( style );
    }    
    
    /**
     * @see org.deegree.graphics.sld.AbstractLayer#getStyles()
     * @param style a style to remove
     */
    public void removeStyle(AbstractStyle style) {
        styles.remove( styles.indexOf(style) );
    }

    /**
     * returns a STring-Representation of the layer
     *
     * @return the layer as String
     */
    @Override
    public String toString() {
        String ret = getClass().getName() + "\n";
        ret = "name = " + name + "\n";
        ret += ( "layerFeatureConstraints = " + layerFeatureConstraints + "\n" );
        ret += ( "styles = " + styles + "\n" );

        return ret;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractLayer.java,v $
Revision 1.9  2006/09/15 09:17:33  schmitz
Implementing Marshallable, since all subclasses to it...

Revision 1.8  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
