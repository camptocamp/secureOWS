/*----------------    FILE HEADER  -------------/-----------------------------

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
package org.deegree.graphics.displayelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.GraphicFill;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.MultiPrimitive;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Primitive;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfacePatch;

/**
 * DisplayElement for handling polygons
 * 
 *
 * @version $Revision: 1.20 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.20 $, $Date: 2006/09/28 13:32:35 $
 *
 * @since 2.0
 */
public class PolygonDisplayElement extends GeometryDisplayElement implements DisplayElement,
                                                                 Serializable {

    private static final ILogger LOG = LoggerFactory.getLogger( PolygonDisplayElement.class );

    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -2980154437699081214L;

    private List pathes = new ArrayList( 1000 );

    /**
     * Creates a new PolygonDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     */
    public PolygonDisplayElement( Feature feature, Surface geometry ) {
        super( feature, geometry, null );

        Symbolizer defaultSymbolizer = new PolygonSymbolizer();
        this.setSymbolizer( defaultSymbolizer );
    }

    /**
     * Creates a new PolygonDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     * @param symbolizer 
     */
    public PolygonDisplayElement( Feature feature, Surface geometry, PolygonSymbolizer symbolizer ) {
        super( feature, geometry, symbolizer );
    }

    /**
     * Creates a new PolygonDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     */
    public PolygonDisplayElement( Feature feature, MultiSurface geometry ) {
        super( feature, geometry, null );

        Symbolizer defaultSymbolizer = new PolygonSymbolizer();
        this.setSymbolizer( defaultSymbolizer );
    }

    /**
     * Creates a new PolygonDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     * @param symbolizer 
     */
    public PolygonDisplayElement( Feature feature, MultiSurface geometry,
                                 PolygonSymbolizer symbolizer ) {
        super( feature, geometry, symbolizer );
    }

    /**
     *  renders the DisplayElement to the submitted graphic context
     */
    public void paint( Graphics g, GeoTransform projection, double scale ) {
        
        if ( feature != null ) {
            ( (ScaledFeature) feature ).setScale( scale );
        }
        try {
            if ( geometry == null ) {
                return;
            }
            // a local instance must be used because following statement may
            // changes the original geometry
            Geometry geom = geometry;     
            if ( geom == null ) {
                LOG.logInfo( "null geometry in " + this.getClass().getName()  );
                return;
            }

            if ( geom instanceof Surface ) {
                GeneralPath path = calcPolygonPath( projection, (Surface) geom );
                if ( path != null ) {
                    drawPolygon( g, path );
                } else {
                    LOG.logWarning( "null path in " + this.getClass().getName()  );
                }
            } else {
                MultiPrimitive msurface = (MultiPrimitive) geom;
                for ( int i = 0; i < msurface.getSize(); i++ ) {
                    Primitive prim = msurface.getPrimitiveAt( i );
                    if ( prim instanceof Surface ) {
                        GeneralPath path = calcPolygonPath( projection, (Surface)prim );
                        if ( path != null ) {
                            drawPolygon( g, path );
                        } else {
                            LOG.logWarning( "null path in " + this.getClass().getName()  );
                        }
                    } else {
                        System.out.println( prim.getClass().getName() );
                    }
                }
            }
        } catch ( FilterEvaluationException e ) {
            LOG.logError( "FilterEvaluationException caught evaluating an Expression!", e );
        } catch ( Exception ex ) {
            LOG.logError( "Exception caught evaluating an Expression!", ex );
        }
        
    }

    private double distance( Position p1, Position p2 ) {
        double x1 = p1.getX(); 
        double y1 = p1.getY(); 
        double x2 = p2.getX();
        double y2 = p2.getY();
        return Math.sqrt( ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 ) );
    }

    private GeneralPath calcPolygonPath(GeoTransform projection, Surface surface )
                            throws Exception {
        GeneralPath path = new GeneralPath();

        SurfacePatch patch = surface.getSurfacePatchAt( 0 );
        if ( patch == null )
            return null;
        appendRingToPath(path, patch.getExteriorRing(), projection);
        Position[][] inner = patch.getInteriorRings();
        if ( inner != null ) {
            for (int i = 0; i < inner.length; i++) {
                appendRingToPath(path, inner[i], projection);
            }
        }

        return path;
    }

    private void appendRingToPath(GeneralPath path, Position[] ring,
                                  GeoTransform projection) {
        if (ring.length == 0)
            return;

        int[] x = new int[ring.length];
        int[] y = new int[ring.length];
        int k = 0;
        
        Position p = projection.getDestPoint( ring[0] );
        Position pp = p; 
        path.moveTo((float)p.getX(), (float)p.getY());
        for ( int i = 1; i < ring.length; i++ ) {
            p = projection.getDestPoint( ring[i] );
            if ( distance( p, pp ) > 1 ) {
                path.lineTo((float)p.getX(), (float)p.getY());
                pp = p;
                x[k] = (int)p.getX();
                y[k++] = (int)p.getY();
            }            
        }
        int[][] tmp = new int[3][];
        tmp[0] = x;
        tmp[1] = y;
        tmp[2] = new int[] { k };
        pathes.add( tmp );
    }

  
    private void drawPolygon( Graphics g, GeneralPath path )
                            throws FilterEvaluationException {
        Graphics2D g2 = (Graphics2D) g;

        PolygonSymbolizer sym = (PolygonSymbolizer) symbolizer;
        org.deegree.graphics.sld.Fill fill = sym.getFill();
        org.deegree.graphics.sld.Stroke stroke = sym.getStroke();

        if ( fill != null ) {
            double opacity = fill.getOpacity( feature );

            // is completly transparent
            // if not fill polygon
            if ( opacity > 0.01 ) {
                Color color = fill.getFill( feature );
                int alpha = (int) Math.round( opacity * 255 );
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                color = new Color( red, green, blue, alpha );
                g2.setColor( color );
                GraphicFill gFill = fill.getGraphicFill();

                if ( gFill != null ) {
                    BufferedImage texture = gFill.getGraphic().getAsImage( feature );
                    if ( texture != null ) {
                        Rectangle anchor = new Rectangle( 0, 0, texture.getWidth( null ),
                                                          texture.getHeight( null ) );
                        g2.setPaint( new TexturePaint( texture, anchor ) );
                    }
                }
                try {
                    g2.fill( path );
                } catch ( Exception e ) {
                }
            }
        }
        
        // only stroke outline, if Stroke-Element is given
        if ( stroke != null ) {
            if ( stroke.getOpacity( feature ) > 0.001 ) {
                // do not paint if feature is completly transparent
                drawLine( g2, path, stroke );
            }
            if ( stroke.getGraphicStroke() != null ) {
                try {
                    Image image = stroke.getGraphicStroke().getGraphic().getAsImage( feature );
                    CurveWalker walker = new CurveWalker( g.getClipBounds() );

                    int[][] pos = null;
                    for ( int i = 0; i < pathes.size(); i++ ) {
                        pos = (int[][]) pathes.get( i );
                        ArrayList positions = walker.createPositions( pos, image.getWidth( null ) );
                        Iterator it = positions.iterator();
                        while ( it.hasNext() ) {
                            double[] label = (double[]) it.next();
                            int x = (int) ( label[0] + 0.5 );
                            int y = (int) ( label[1] + 0.5 );
                            paintImage( image, g2, x, y, Math.toRadians( label[2] ) );
                        }
                    }
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                } finally {
                    pathes.clear();
                }
            } 
            
        }

        
    }

    /**
     * Renders a curve to the submitted graphic context.
     *
     * TODO: Calculate miterlimit.
     */
    private void drawLine( Graphics g, GeneralPath path, org.deegree.graphics.sld.Stroke stroke )
                            throws FilterEvaluationException {

        // Color & Opacity
        Graphics2D g2 = (Graphics2D) g;
        setColor( g2, stroke.getStroke( feature ), stroke.getOpacity( feature ) );

        float[] dash = stroke.getDashArray( feature );

        // use a simple Stroke if dash == null or its length < 2
        // that's faster
        float width = (float) stroke.getWidth( feature );
        int cap = stroke.getLineCap( feature );
        int join = stroke.getLineJoin( feature );
        BasicStroke bs2 = null;

        if ( ( dash == null ) || ( dash.length < 2 ) ) {
            bs2 = new BasicStroke( width, cap, join );
        } else {
            bs2 = new BasicStroke( width, cap, join, 10.0f, dash, stroke.getDashOffset( feature ) );
        }

        g2.setStroke( bs2 );
        g2.draw( path );        

    }

    /**
     *
     *
     * @param g2 
     * @param color 
     * @param opacity 
     *
     * @return 
     */
    private Graphics2D setColor( Graphics2D g2, Color color, double opacity ) {
        if ( opacity < 0.999 ) {
            // just use a color having an alpha channel if a significant
            // level of transparency has been defined
            final int alpha = (int) Math.round( opacity * 255 );
            final int red = color.getRed();
            final int green = color.getGreen();
            final int blue = color.getBlue();
            color = new Color( red, green, blue, alpha );
        }

        g2.setColor( color );
        return g2;
    }

    /**
     * 
     * @param image
     * @param g
     * @param x
     * @param y
     * @param rotation
     */
    private void paintImage( Image image, Graphics2D g, int x, int y, double rotation ) {

        // get the current transform
        AffineTransform saveAT = g.getTransform();

        // translation parameters (rotation)
        AffineTransform transform = new AffineTransform();
        transform.rotate( rotation, x, y );
        transform.translate( -image.getWidth( null ), -image.getHeight( null ) / 2.0 );
        g.setTransform( transform );

        // render the image     
        g.drawImage( image, x, y, null );

        // restore original transform
        g.setTransform( saveAT );
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: PolygonDisplayElement.java,v $
 Revision 1.20  2006/09/28 13:32:35  poth
 bug fixes - rendering strokes having a GraphicStroke or GraphicFill

 Revision 1.19  2006/07/04 19:09:06  poth
 comments corrected - code formatation

 Revision 1.18  2006/07/04 18:30:25  poth
 support for MultiPrimitives added

 Revision 1.17  2006/06/23 15:18:42  poth
 enhanced version using GeneralPath instead of Area to render polygons

 Revision 1.16  2006/06/12 06:44:04  poth
 *** empty log message ***

 Revision 1.15  2006/05/25 16:37:09  poth
 bugfix handling outter line coordinates (pathes)

 Revision 1.14  2006/05/22 07:38:11  poth
 bug fixes according to handling geometries


 ********************************************************************** */