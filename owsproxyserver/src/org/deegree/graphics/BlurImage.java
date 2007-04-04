//$Header$
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
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

 Jens Fitzke
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.graphics;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

/**
 * Display map surface depending on the security parameters. The rest of the Map Image will be
 * blurred allowing the user a clear view of only the allowed surface.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */

public class BlurImage {

    /**
     * Create a new BlurImage instance.
     */
    public BlurImage() {
    }

    /**
     * Render the surface geometry the user is allowed to see. The geometry must be within the
     * bounding box of the map image.
     * 
     * 1. Geometry contains bbox -> no need to blur the image 2. Geometry complety within bbox. 3.
     * BBOX intersects Geometry a. Returns a MultiSurface b. Returns a Surface 4. BBOX disjunkt
     * Geometry
     * 
     * @param image
     * @param bbox
     * @param geom
     * @return BufferedImage
     * @throws GeometryException
     * @throws XMLParsingException
     */
    public BufferedImage renderUserRealm( BufferedImage image, Envelope bbox, Geometry geom )
                            throws GeometryException, XMLParsingException {

        int blurScale = 8;
        float alpha = 0.2f;

        // Create output image
        BufferedImage output = null;
        if ( image.getType() == BufferedImage.TYPE_INT_RGB ) {
            System.out.println( "setting rgb" );
            output = new BufferedImage( image.getWidth(), image.getHeight(),
                                        BufferedImage.TYPE_INT_RGB );
        } else {
            System.out.println( "setting rgba" );
            output = new BufferedImage( image.getWidth(), image.getHeight(),
                                        BufferedImage.TYPE_INT_ARGB );
        }

        // Transform the world coordinates to screen coordinates.
        WorldToScreenTransform wsTransform = new WorldToScreenTransform( bbox.getMin().getX(),
                                                                         bbox.getMin().getY(),
                                                                         bbox.getMax().getX(),
                                                                         bbox.getMax().getY(),
                                                                         image.getMinX(),
                                                                         image.getMinY(),
                                                                         image.getWidth(),
                                                                         image.getHeight() );

        Graphics2D graphics = output.createGraphics();
        Composite composite = graphics.getComposite();
        graphics.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
        // blur image, along with the blur scale.
        graphics.drawImage( blur( grayScale( image ), blurScale ), null, image.getMinX(),
                            image.getMinY() );
        graphics.setComposite( composite );
        try {
            // convert bbox to geometry.
            StringBuffer envelope = GMLGeometryAdapter.exportAsBox( bbox );
            Geometry boundingBOX = GMLGeometryAdapter.wrap( envelope.toString() );
            Geometry intersection = boundingBOX.intersection( geom );
            if ( intersection instanceof Surface ) {
                Surface surface = (Surface) intersection;
                Polygon polygon = retrieveSurfacePolygon( surface, wsTransform );
                graphics = renderClip( graphics, polygon, image );
            } else if ( intersection instanceof MultiSurface ) {
                MultiSurface multiSurface = (MultiSurface) intersection;
                Surface[] surfaces = multiSurface.getAllSurfaces();
                for ( int i = 0; i < surfaces.length; i++ ) {
                    Surface surface = surfaces[i];
                    Polygon polygon = retrieveSurfacePolygon( surface, wsTransform );
                    graphics = renderClip( graphics, polygon, image );
                }
            }
        } catch ( GeometryException e ) {
            throw new GeometryException( "Error creating a geometry from the bounding box. " + e );
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException( "Error exporting the bounding box to its "
                                           + "string format. " + e );
        }
        graphics.dispose();
        return output;
    }

    /**
     * Render the clip image on the output graphics context.
     * 
     * @param graphics
     * @param polygon
     * @param image
     * @return Graphics2D
     */
    private Graphics2D renderClip( Graphics2D graphics, Polygon polygon, BufferedImage image ) {

        // clip the region which the user is allowed to see
        graphics.setClip( polygon );
        // draw region
        graphics.drawImage( image, null, image.getMinX(), image.getMinY() );

        return graphics;

    }

    /**
     * Retrieve the surface as a java.awt.Polygon. The exterior and interior rings are retrieved and
     * the coordinates transformed to screen coordinates.
     * 
     * @param surface
     * @param wsTransform
     * @return Polygon
     */
    private Polygon retrieveSurfacePolygon( Surface surface, WorldToScreenTransform wsTransform ) {

        Ring exteriorRing = surface.getSurfaceBoundary().getExteriorRing();
        Position[] exteriorPositions = exteriorRing.getPositions();
        Ring[] interiorRings = surface.getSurfaceBoundary().getInteriorRings();

        Position[][] interiorPositions;
        if ( interiorRings != null ) {
            interiorPositions = new Position[interiorRings.length][];
            for ( int i = 0; i < interiorPositions.length; i++ ) {
                interiorPositions[i] = interiorRings[i].getPositions();
            }
        } else {
            interiorPositions = new Position[0][];
        }

        int[] xArray = getXArray( exteriorPositions, interiorPositions, wsTransform );
        int[] yArray = getYArray( exteriorPositions, interiorPositions, wsTransform );

        Polygon polygon = new Polygon( xArray, yArray, xArray.length );

        return polygon;
    }

    /**
     * Retrieve the array of x-coordinates after transformation to screen coordinates.
     * 
     * @param exteriorRing
     * @param interiorRing
     * @param wsTransform
     * @return int[]
     */
    private int[] getXArray( Position[] exteriorRing, Position[][] interiorRing,
                            WorldToScreenTransform wsTransform ) {

        List xList = new ArrayList();
        for ( int i = 0; i < exteriorRing.length; i++ ) {
            Position position = exteriorRing[i];
            xList.add( wsTransform.getDestX( position.getX() ) );
        }
        for ( int i = 0; i < interiorRing.length; i++ ) {
            Position[] positions = interiorRing[i];
            for ( int j = 0; j < positions.length; j++ ) {
                Position position = positions[j];
                xList.add( wsTransform.getDestX( position.getX() ) );
            }
        }

        int[] xArray = new int[xList.size()];
        for ( int i = 0; i < xList.size(); i++ ) {
            Double tmp = (Double) xList.get( i );
            xArray[i] = tmp.intValue();
        }
        return xArray;
    }

    /**
     * Retrieve the array of y-coordinates after transformation to screen coordinates.
     * 
     * @param exteriorRing
     * @param interiorRing
     * @param wsTransform
     * @return int[]
     */
    private int[] getYArray( Position[] exteriorRing, Position[][] interiorRing,
                            WorldToScreenTransform wsTransform ) {

        List yList = new ArrayList();
        for ( int i = 0; i < exteriorRing.length; i++ ) {
            Position position = exteriorRing[i];
            yList.add( wsTransform.getDestY( position.getY() ) );
        }
        for ( int i = 0; i < interiorRing.length; i++ ) {
            Position[] positions = interiorRing[i];
            for ( int j = 0; j < positions.length; j++ ) {
                Position position = positions[j];
                yList.add( wsTransform.getDestY( position.getY() ) );
            }
        }

        int[] yArray = new int[yList.size()];
        for ( int i = 0; i < yList.size(); i++ ) {
            Double tmp = (Double) yList.get( i );
            yArray[i] = tmp.intValue();
        }
        return yArray;
    }

    /**
     * Blur effect carried out on the image. The blur scale defines the intensity of the blur.
     * 
     * @param image
     * @param blurScale
     * @return BufferedImage
     */
    private BufferedImage blur( BufferedImage image, int blurScale ) {

        BufferedImage destination = null;
        if ( image.getType() == BufferedImage.TYPE_INT_RGB ) {
            destination = new BufferedImage( image.getWidth(), image.getHeight(),
                                             BufferedImage.TYPE_INT_RGB );
        } else {
            destination = new BufferedImage( image.getWidth(), image.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB );
        }

        float[] data = new float[blurScale * blurScale];
        float value = 1.0f / ( blurScale * blurScale );
        for ( int i = 0; i < data.length; i++ ) {
            data[i] = value;
        }
        Kernel kernel = new Kernel( blurScale, blurScale, data );
        ConvolveOp convolve = new ConvolveOp( kernel, ConvolveOp.EDGE_NO_OP, null );
        convolve.filter( image, destination );

        return destination;
    }

    /**
     * Convert BufferedImage RGB to black and white image
     * 
     * @param image
     * @return BufferedImage
     */
    private BufferedImage grayScale( BufferedImage image ) {

        BufferedImage destination = null;
        if ( image.getType() == BufferedImage.TYPE_INT_RGB ) {
            destination = new BufferedImage( image.getWidth(), image.getHeight(),
                                             BufferedImage.TYPE_INT_RGB );
        } else {
            destination = new BufferedImage( image.getWidth(), image.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB );
        }
        ColorConvertOp colorConvert = new ColorConvertOp(
                                                          ColorSpace.getInstance( ColorSpace.CS_GRAY ),
                                                          null );
        colorConvert.filter( image, destination );

        return destination;
    }

    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.18  2006/08/29 19:54:14  poth
footer corrected

Revision 1.17  2006/08/24 06:39:28  poth
File header corrected

Revision 1.16  2006/08/06 20:06:13  poth
useless method removed

Revision 1.15  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
