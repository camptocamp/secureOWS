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

package org.deegree.graphics;

import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.displayelements.DisplayElement;
import org.deegree.graphics.displayelements.DisplayElementFactory;
import org.deegree.graphics.displayelements.LabelDisplayElement;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.GeometryException;
import org.opengis.coverage.grid.GridCoverage;

/**
 * A Theme is for usual a homogenious collection of Features coupled with
 * a portrayal model for their graphical representation. Considering the OGC
 * Styled Layer Descriptor specification this is not nessecary the case. In
 * confirmation with the SLD a theme can be build from a lot of thematic
 * completly different feature types.<p></p>
 * From a theoretical point of view this isn't very satisfying. But it will
 * be supported by the <tt>Theme</tt> class.<p></p>
 * Assigned to the Theme are:
 * <ul>
 * 	<li>a Layer that contains the data (features)
 * 	<li>a Portrayal model that determines how the features shall be rendered
 * 	<li>a Selector that offers method for selection and de-selection of
 * 		features
 * 	<li>a event listener that handles event occuring on a theme that's
 * 		for usual part of a map.
 * </ul>
 * 
 * <p>------------------------------------------------------------------------</p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.23 $ $Date: 2006/09/28 15:39:01 $
 */

public class Theme {

    private static final ILogger LOG = LoggerFactory.getLogger( Theme.class );

    private String name = null;

    private Layer layer = null;

    private UserStyle[] styles = null;

    private ArrayList displayElements = null;

    /**
     * the MapView (map) the theme is associated to
     * 
     */
    private MapView parent = null;

    /**
     * this ArrayList contains all DisplayElements (and so the features) that
     * are marked as selected.
     */
    private List selector = Collections.synchronizedList( new ArrayList() );

    private List highlighter = Collections.synchronizedList( new ArrayList() );

    private List eventController = Collections.synchronizedList( new ArrayList() );
    
    /**
     * 
     * @param name
     * @param layer
     * @param styles
     */
    protected Theme( String name, Layer layer, UserStyle[] styles ) {
        this.layer = layer;
        this.name = name;
        displayElements = new ArrayList( 1000 );
        setStyles( styles );
    }

    /**
     * sets the parent MapView of the Theme.
     * 
     */
    public void setParent( MapView parent ) {
        this.parent = parent;
    }

    /**
     * returns the name of the layer
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * renders the layer to the submitted graphic context
     */
    public void paint( Graphics g ) {

        double scale = parent.getScale();
        
        if ( layer instanceof LazyRasterLayer ) {
            // re-create raster displayelements to adapt current 
            // current boundingbox
            createLazyRasterDisplayElements();
        } else if ( layer instanceof OWSRasterLayer ) {
            createOWSRasterDisplayElements();
        }
        for ( int i = 0; i < displayElements.size(); i++ ) {
            DisplayElement de = (DisplayElement) displayElements.get( i );

            if ( de.doesScaleConstraintApply( scale ) ) {
                de.paint( g, parent.getProjection(), scale );
            }
        }

    }

    /**
     * renders the display elements matching the submitted ids
     */
    public void paint( Graphics g, String[] ids ) {
        
        double scale = parent.getScale();
        
        if ( layer instanceof LazyRasterLayer ) {
            // re-create raster displayelements to adapt current 
            // current boundingbox
            createLazyRasterDisplayElements();
        }

        for ( int k = 0; k < displayElements.size(); k++ ) {
            DisplayElement de = (DisplayElement) displayElements.get( k );
            for ( int i = 0; i < ids.length; i++ ) {                
                if (  de.getAssociateFeatureId().equals( ids[i] ) ) {
                    de.paint( g, parent.getProjection(), scale );
                    break;
                }
            }
        }
    }

    /**
     * renders the selected display elements of the layer
     */
    public void paintSelected( Graphics g ) {

        double scale = parent.getScale();
        
        if ( layer instanceof LazyRasterLayer ) {
            // re-create raster displayelements to adapt current 
            // current boundingbox
            createLazyRasterDisplayElements();
        }
        
        if ( layer instanceof OWSRasterLayer ) {
            
        }
        
        for ( int i = 0; i < displayElements.size(); i++ ) {
            DisplayElement de = ( (DisplayElement) displayElements.get( i ) );
            if ( de.isSelected() ) {
                de.paint( g, parent.getProjection(), scale );
            }
        }

    }

    /**
     * renders the highlighted display elements of the layer
     */
    public void paintHighlighted( Graphics g ) {

        double scale = parent.getScale();
        
        if ( layer instanceof LazyRasterLayer ) {
            // re-create raster displayelements to adapt current 
            // current boundingbox
            createLazyRasterDisplayElements();
        }

        for ( int i = 0; i < displayElements.size(); i++ ) {
            DisplayElement de = ( (DisplayElement) displayElements.get( i ) );
            if ( de.isHighlighted() ) {
                de.paint( g, parent.getProjection(), scale );
            }
        }

    }

    /**
     * A selector is a class that offers methods for selecting and
     * deselecting single DisplayElements or groups of DisplayElements.
     * A selector may offers methods like 'select all DisplayElements
     * within a specified bounding box' or 'select all DisplayElements
     * thats area is larger than 120 km�' etc.
     */
    public void addSelector( Selector selector ) {
        this.selector.add( selector );
        selector.addTheme( this );
    }

    /**
     * @see org.deegree.graphics.Theme#addSelector(Selector)
     */
    public void removeSelector( Selector selector ) {
        this.selector.remove( selector );
        selector.removeTheme( this );
    }

    /**
     * A Highlighter is a class that is responsible for managing the highlight
     * capabilities for one or more Themes.
     */
    public void addHighlighter( Highlighter highlighter ) {
        this.highlighter.add( highlighter );
        highlighter.addTheme( this );
    }

    /**
     * @see org.deegree.graphics.Theme#addHighlighter(Highlighter)
     */
    public void removeHighlighter( Highlighter highlighter ) {
        this.highlighter.remove( highlighter );
        highlighter.removeTheme( this );
    }

    /**
     * adds an eventcontroller to the theme that's reponsible for handling
     * events that targets the theme.
     */
    public void addEventController( ThemeEventController controller ) {
        eventController.add( controller );
        controller.addTheme( this );
    }

    /**
     * @see org.deegree.graphics.Theme#addEventController(ThemeEventController)
     */
    public void removeEventController( ThemeEventController controller ) {
        eventController.remove( controller );
        controller.removeTheme( this );
    }

    /**
     * Sets the styles used for this <tt>Theme</tt>. If this method will be 
     * called all <tt>DisplayElement</tt>s will be recreated to consider the
     * new style definitions.
     * 
     */
    public void setStyles( UserStyle[] styles ) {
        
        this.styles = styles;
        displayElements.clear();
        if ( layer instanceof FeatureLayer ) {
            createFeatureDisplayElements();
        } else if ( layer instanceof RasterLayer ) {
            createRasterDisplayElements( );
        } else {
            createLazyRasterDisplayElements( );
        }
        
    }

    /**
     * creates <code>DisplayElement</code>s for <code>Feature</code> instances
     */
    private void createFeatureDisplayElements( ) {
        displayElements.clear();
        DisplayElementFactory fac = new DisplayElementFactory();
        // keep LabelDisplayElements separate from the other elements
        // and append them to the end of the DisplayElement-list
        ArrayList labelDisplayElements = new ArrayList( 100 );
        try {
            // instance of FeatureLayer
            for ( int i = 0; i < ( (FeatureLayer) layer ).getSize(); i++ ) {
                Feature feature = ( (FeatureLayer) layer ).getFeature( i );
                featureToDisplayElement( styles, fac, labelDisplayElements, feature );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
        displayElements.addAll( labelDisplayElements );
    }

    /**
     * creates <code>DisplayElement</code>s for <code>GridCoverage</code> instances
     */
    private void createRasterDisplayElements( ) {
        displayElements.clear();
        DisplayElementFactory fac = new DisplayElementFactory();
        try {
            // instance of RasterLayer
            RasterLayer rl = (RasterLayer) layer;
            DisplayElement[] de = fac.createDisplayElement( rl.getRaster(), styles );
            for ( int k = 0; k < de.length; k++ ) {
                displayElements.add( de[k] );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * creates <code>DisplayElement</code>s for <code>GridCoverage</code> instances that
     * are loaded depending on current boundingbox.
     */
    private void createLazyRasterDisplayElements( ) {
        displayElements.clear();
        DisplayElementFactory fac = new DisplayElementFactory();
        try {
            if ( parent != null ) {
                LazyRasterLayer rl = (LazyRasterLayer) layer;
                double w = parent.getProjection().getDestRect().getWidth();
                double d = parent.getBoundingBox().getWidth()/w;
                GridCoverage gc = rl.getRaster( parent.getBoundingBox(), d );
                //gc can be null if e.g. the area covered by the raster
                // layer is outside the visible area.
                if ( gc != null ) {
                    DisplayElement[] de = fac.createDisplayElement( gc, styles );
                    for ( int k = 0; k < de.length; k++ ) {
                        displayElements.add( de[k] );
                    }
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    private void createOWSRasterDisplayElements( ) {
        displayElements.clear();

        DisplayElementFactory fac = new DisplayElementFactory();
        try {
            if ( parent != null ) {
                OWSRasterLayer rl = (OWSRasterLayer) layer;
                double w = parent.getProjection().getDestRect().getWidth();
                double h = parent.getProjection().getDestRect().getHeight();
                GridCoverage gc = rl.getRaster( parent.getBoundingBox(), w, h );
                if ( gc != null ){
                    DisplayElement[] de = 
                        fac.createDisplayElement( gc, styles );
                    for ( int k = 0; k < de.length; k++ ) {
                        displayElements.add( de[k] );
                    }
                } 
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }
    /**
     * 
     * @param styles
     * @param fac
     * @param labelDisplayElements
     * @param feature
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws GeometryException
     * @throws PropertyPathResolvingException
     */
    private void featureToDisplayElement( UserStyle[] styles, DisplayElementFactory fac,
                                         ArrayList labelDisplayElements, Feature feature )
                            throws ClassNotFoundException, IllegalAccessException,
                            InstantiationException, NoSuchMethodException,
                            InvocationTargetException, GeometryException,
                            PropertyPathResolvingException {
        DisplayElement[] de = fac.createDisplayElement( feature, styles );
        for ( int k = 0; k < de.length; k++ ) {
            if ( de[k] instanceof LabelDisplayElement ) {
                labelDisplayElements.add( de[k] );
            } else {
                displayElements.add( de[k] );
            }
        }
        FeatureProperty[] fp = feature.getProperties();
        for ( int i = 0; i < fp.length; i++ ) {
            if ( fp[i].getValue() != null && fp[i].getValue() instanceof Feature ) {
                featureToDisplayElement( styles, fac, labelDisplayElements,
                                         (Feature) fp[i].getValue() );
            }
        }
    }

    /**
     * returns the styles used for this <tt>Theme</tt>.
     * 
     */
    public UserStyle[] getStyles() {
        return styles;
    }

    /**
     * returns the layer that holds the data of the theme
     * 
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Returns all <tt>DisplayElements</tt> that this <tt>Theme</tt> contains.
     * <p>
     * @return <tt>ArrayList</tt> containing <tt>DisplayElements</tt>
     * 
     */
    public ArrayList getDisplayElements() {
        return displayElements;
    }

    /**
     * returns the <tt>DisplayElements</tt> of the Theme
     * 
     */
    public void setDisplayElements( ArrayList de ) {
        this.displayElements = de;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Theme.java,v $
 Revision 1.23  2006/09/28 15:39:01  poth
 bug fix - using LazyRasterLayer

 Revision 1.22  2006/09/22 12:16:28  taddei
 made constructor protected

 Revision 1.21  2006/09/07 13:26:20  taddei
 handling/ignoring exceptions in OWSRasterlayer; and using img dimension

 Revision 1.20  2006/08/25 14:11:39  taddei
 added code for OWSRasterLayer

 Revision 1.19  2006/08/24 09:58:19  poth
 support for OWSRasterLayer added

 Revision 1.18  2006/05/31 17:53:33  poth
 bug fix

 Revision 1.17  2006/05/31 17:23:59  poth
 first implementation of LazyRasterLayer

 Revision 1.16  2006/05/26 06:43:23  poth
 bug fix creating display elements

 Revision 1.15  2006/05/25 16:16:46  poth
 bug fix in all paint methods

 Revision 1.14  2006/05/24 08:05:23  poth
 support for LazyRasterLayer added


 ********************************************************************** */