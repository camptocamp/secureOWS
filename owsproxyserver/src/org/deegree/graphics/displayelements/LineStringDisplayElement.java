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
package org.deegree.graphics.displayelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.LineString;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;

/**
 * DisplayElement that encapsulates a linestring (<tt>GM_Curve</tt>) or
 * multi-linestring geometry (<tt>GM_MultiCurve</tt>) and a
 * <tt>LineStringSymbolizer</tt>.
 * <p>
 * It can be rendered using a solid stroke or a graphics stroke.
 * <p>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.10 $ $Date: 2006/09/28 13:32:35 $
 */
class LineStringDisplayElement extends GeometryDisplayElement implements DisplayElement,
                                                             Serializable {
    private static final ILogger LOG = LoggerFactory.getLogger( LineStringDisplayElement.class );
    
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -4657962592230618248L;

    /**
     * Creates a new LineStringDisplayElement_Impl object.
     *
     * @param feature 
     * @param geometry 
     */
    public LineStringDisplayElement( Feature feature, Curve geometry ) {
        super( feature, geometry, null );

        Symbolizer defaultSymbolizer = new LineSymbolizer();
        this.setSymbolizer( defaultSymbolizer );
    }

    /**
     * Creates a new LineStringDisplayElement object.
     *
     * @param feature 
     * @param geometry 
     * @param symbolizer 
     */
    public LineStringDisplayElement( Feature feature, Curve geometry, LineSymbolizer symbolizer ) {
        super( feature, geometry, symbolizer );
    }

    /**
     * Creates a new LineStringDisplayElement object.
     *
     * @param feature 
     * @param geometry 
     */
    public LineStringDisplayElement( Feature feature, MultiCurve geometry ) {
        super( feature, geometry, null );

        Symbolizer defaultSymbolizer = new LineSymbolizer();
        this.setSymbolizer( defaultSymbolizer );
    }

    /**
     * Creates a new LineStringDisplayElement object.
     *
     * @param feature 
     * @param geometry 
     * @param symbolizer 
     */
    public LineStringDisplayElement( Feature feature, MultiCurve geometry, LineSymbolizer symbolizer ) {
        super( feature, geometry, symbolizer );
    }

    /**
     * draws a graphics symbol (image) onto a defined position on the line
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

    /**
     *  renders the DisplayElement to the submitted graphic context
     */
    public void paint( Graphics g, GeoTransform projection, double scale ) {

        if ( geometry == null ) {
            return;
        }
        // a local instance must be used because following statement may
        // changes the original geometry
        Geometry geom = geometry;
        try {
            Surface sur = GeometryFactory.createSurface( projection.getSourceRect(), null );
            geom = sur.intersection( geometry );
        } catch ( Exception e ) {
            // use original geometry 
        }
        if ( geom == null )
            return;

        ( (ScaledFeature) feature ).setScale( scale );

        LineSymbolizer sym = (LineSymbolizer) symbolizer;
        org.deegree.graphics.sld.Stroke stroke = sym.getStroke();

        // no stroke defined -> don't draw anything
        if ( stroke == null ) {
            return;
        }

        try {
            if ( stroke.getOpacity( feature ) > 0.001 ) {
                // do not paint if feature is completly transparent
                int[][] pos = null;
                Graphics2D g2 = (Graphics2D) g;

                if ( geom instanceof Curve ) {
                    pos = calcTargetCoordinates( projection, (Curve) geom );
                    drawLine( g2, pos, stroke );
                } else {
                    MultiCurve mc = (MultiCurve) geom;
                    for ( int i = 0; i < mc.getSize(); i++ ) {
                        pos = calcTargetCoordinates( projection, mc.getCurveAt( i ) );
                        drawLine( g2, pos, stroke );
                    }
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }

        // GraphicStroke label
        if ( stroke.getGraphicStroke() != null ) {
            try {
                Image image = stroke.getGraphicStroke().getGraphic().getAsImage( feature );
                CurveWalker walker = new CurveWalker( g.getClipBounds() );

                if ( geom instanceof Curve ) {
                    int[][] pos = LabelFactory.calcScreenCoordinates( projection, (Curve) geom );
                    ArrayList positions = walker.createPositions( pos, image.getWidth( null ) );
                    Iterator it = positions.iterator();
                    while ( it.hasNext() ) {
                        double[] label = (double[]) it.next();
                        int x = (int) ( label[0] + 0.5 );
                        int y = (int) ( label[1] + 0.5 );
                        paintImage( image, (Graphics2D) g, x, y, Math.toRadians( label[2] ) );
                    }
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }

        }

    }

    /**
     * Calculates the screen coordinates of the curve.
     */
    private int[][] calcTargetCoordinates( GeoTransform projection, Curve curve )
                            throws Exception {
        LineString lineString = curve.getAsLineString();
        int count = lineString.getNumberOfPoints();
        int[][] pos = new int[3][];
        pos[0] = new int[count];
        pos[1] = new int[count];
        pos[2] = new int[1];

        int k = 0;
        for ( int i = 0; i < count; i++ ) {
            Position position = lineString.getPositionAt( i );
            double tx = projection.getDestX( position.getX() );
            double ty = projection.getDestY( position.getY() );

            if ( i > 0 ) {
                if ( distance( tx, ty, pos[0][k - 1], pos[1][k - 1] ) > 1 ) {
                    pos[0][k] = (int) ( tx + 0.5 );
                    pos[1][k] = (int) ( ty + 0.5 );
                    k++;
                }
            } else {
                pos[0][k] = (int) ( tx + 0.5 );
                pos[1][k] = (int) ( ty + 0.5 );
                k++;
            }
        }
        pos[2][0] = k;

        return pos;
    }

    /**
     * Renders a curve to the submitted graphic context.
     *
     * TODO: Calculate miterlimit.
     */
    private void drawLine( Graphics g, int[][] pos, org.deegree.graphics.sld.Stroke stroke )
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

        g2.drawPolyline( pos[0], pos[1], pos[2][0] );

    }

    private double distance( double x1, double y1, double x2, double y2 ) {
        return Math.sqrt( ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 ) );
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
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: LineStringDisplayElement.java,v $
 Revision 1.10  2006/09/28 13:32:35  poth
 bug fixes - rendering strokes having a GraphicStroke or GraphicFill

 Revision 1.9  2006/07/29 08:51:12  poth
 references to deprecated classes removed

 Revision 1.8  2006/05/22 07:38:11  poth
 bug fixes according to handling geometries


 ********************************************************************** */