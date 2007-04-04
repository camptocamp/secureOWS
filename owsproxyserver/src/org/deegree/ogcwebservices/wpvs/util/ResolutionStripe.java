//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/ResolutionStripe.java,v 1.5 2006/12/04 17:06:43 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wpvs.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3f;

import org.deegree.framework.util.MapUtils;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.WKTAdapter;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wpvs.GetViewServiceInvoker;
import org.deegree.ogcwebservices.wpvs.WCSInvoker;
import org.deegree.ogcwebservices.wpvs.WFSInvoker;
import org.deegree.ogcwebservices.wpvs.WMSInvoker;
import org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wpvs.j3d.DefaultSurface;
import org.deegree.ogcwebservices.wpvs.j3d.TerrainModel;
import org.deegree.ogcwebservices.wpvs.j3d.TexturedHeightMapTerrain;
import org.deegree.ogcwebservices.wpvs.j3d.TriangleTerrain;
import org.deegree.processing.raster.converter.Image2RawData;
import org.j3d.geom.GeometryData;

/**
 * The <code>ResolutionStripe</code> class encapsulates a Surface with a maximum Resolution, which
 * is convenient for the creation of a quadtree.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/12/04 17:06:43 $
 * 
 */

public class ResolutionStripe implements Callable<ResolutionStripe> {

    private final double maxResolution;

    private final double minResolution;

    private Surface surface;

    private TerrainModel elevationModel;

    private AbstractDataSource elevationModelDataSource = null;

    private HashMap<String, BufferedImage> textures;

    private ArrayList<AbstractDataSource> texturesDataSources;

    private HashMap<String, DefaultSurface> features;

    private ArrayList<AbstractDataSource> featureCollectionDataSources;

    private double minimalHeightLevel;

    private String outputFormat = null;

    private BranchGroup resultingJ3DScene;

    /**
     * @param surface
     * @param maximumResolution
     * @param minimumResolution
     * @param minimalHeight
     */
    public ResolutionStripe( Surface surface, double maximumResolution, double minimumResolution,
                            double minimalHeight ) {
        this.surface = surface;
        this.maxResolution = maximumResolution;
        this.minResolution = minimumResolution;
        this.minimalHeightLevel = minimalHeight;
        featureCollectionDataSources = new ArrayList<AbstractDataSource>( 5 );
        texturesDataSources = new ArrayList<AbstractDataSource>( 5 );
        textures = new HashMap<String, BufferedImage>( 10 );
        features = new HashMap<String, DefaultSurface>( 1000 );
        resultingJ3DScene = new BranchGroup();

    }

    /**
     * @param surface
     * @param maximumResolution
     * @param minimumResolution
     * @param minimalHeight
     * @param outputFormat
     */
    public ResolutionStripe( Surface surface, double maximumResolution, double minimumResolution,
                            double minimalHeight, String outputFormat ) {
        this( surface, maximumResolution, minimumResolution, minimalHeight );
        this.outputFormat = outputFormat;
    }

    /**
     * @return the CRS of this ResolutionStripe
     */
    public CoordinateSystem getCRSName() {
        return surface.getCoordinateSystem();
    }

    /**
     * @return the resolution of the largest stripe in the surface.
     */
    public double getMaxResolution() {
        return maxResolution;
    }

    /**
     * @return the (always possitive) resolution of the largest (away from the viewer) side of the
     *         surface as scale denominator, which means divide by
     *         {@link MapUtils#DEFAULT_PIXEL_SIZE}.
     */
    public double getMaxResolutionAsScaleDenominator() {
        return Math.abs( maxResolution ) / MapUtils.DEFAULT_PIXEL_SIZE;
    }

    /**
     * @return the (always possitive) resolution of the smallest (towards the viewer) side of the
     *         surface as scale denominator, which means divide by
     *         {@link MapUtils#DEFAULT_PIXEL_SIZE}.
     */
    public double getMinResolutionAsScaleDenominator() {
        return Math.abs( minResolution ) / MapUtils.DEFAULT_PIXEL_SIZE;
    }

    /**
     * @return the resolution of the smallest stripe in the surface. Attention the resolution may be
     *         negative if the requested data is "behind" the viewer.
     */
    public double getMinResolution() {
        return minResolution;
    }

    /**
     * @return the geometric surface which is defines this resolutionStripe.
     */
    public Surface getSurface() {
        return surface;
    }

    /**
     * @return the minimalTerrainHeight.
     */
    public double getMinimalTerrainHeight() {
        return minimalHeightLevel;
    }

    /**
     * @return the requestsize for the bbox containing this resolutionsstripe.
     */
    public int getRequestWidthForBBox() {
        return (int) ( surface.getEnvelope().getWidth() / Math.abs( maxResolution ) );
    }

    /**
     * @return the height (in pixels) of the request envelope
     */
    public int getRequestHeightForBBox() {
        return (int) ( surface.getEnvelope().getHeight() / Math.abs( maxResolution ) );
    }

    /**
     * @return the elevationModel.
     */
    public TerrainModel getElevationModel() {
        return elevationModel;
    }

    /**
     * @param elevationModel
     *            An other elevationModel.
     */
    public void setElevationModel( TerrainModel elevationModel ) {
        this.elevationModel = elevationModel;
    }

    /**
     * @param pointsList
     *            containing Points which represents the heights of measures points (normally
     *            aquired from a wfs).
     */
    public void setElevationModelFromMeassurePoints( List<Point> pointsList ) {
        Envelope env = surface.getEnvelope();
        double width = env.getWidth();
        double height = env.getHeight();
        Point lowerLeft = GeometryFactory.createPoint( env.getMin().getX(), env.getMin().getY(),
                                                       55, env.getCoordinateSystem() );
        Point upperRight = GeometryFactory.createPoint( env.getMax().getX(), env.getMax().getY(),
                                                        55, env.getCoordinateSystem() );
        Point lowerRight = GeometryFactory.createPoint( lowerLeft.getX() + width, lowerLeft.getY(),
                                                        lowerLeft.getZ(), env.getCoordinateSystem() );
        Point upperLeft = GeometryFactory.createPoint( lowerLeft.getX(), lowerLeft.getY() + height,
                                                       lowerLeft.getZ(), env.getCoordinateSystem() );

        pointsList.add( lowerLeft );
        pointsList.add( upperRight );
        pointsList.add( lowerRight );
        pointsList.add( upperLeft );

        VisADWrapper vw = new VisADWrapper( pointsList );
        List<float[][]> triangles = vw.getTriangleCollectionAsList();

        elevationModel = new TriangleTerrain( triangles, surface.getEnvelope() );
    }

    /**
     * @param heightMap
     *            a BufferedImage which contains height values, normally aquired from a wcs.
     */
    public void setElevationModelFromHeightMap( BufferedImage heightMap ) {
        Image2RawData i2rd = new Image2RawData( heightMap );
        float[][] heights = i2rd.parse();

        Envelope env = surface.getEnvelope();

        Position lowerLeft = surface.getEnvelope().getMin();
        Vector3f lLeft = new Vector3f( (float) lowerLeft.getX(), (float) lowerLeft.getY(), 0 );
        // Triangles won't work -> an error in org.j3d.geom.terrain.ElevationGridGenerator therefor
        // using QUADS
        elevationModel = new TexturedHeightMapTerrain( (float) env.getWidth(),
                                                       (float) env.getHeight(), heights, lLeft,
                                                       GeometryData.QUADS, false );

    }

    /**
     * @return the features of this resolutionstripe
     */
    public HashMap<String, DefaultSurface> getFeatures() {
        return features;
    }

    /**
     * @param key
     *            the name of the feature to be added.
     * @param feature
     *            (e.g a building, tree etc.) as a DefaultSurface (derived frome Shape3D) to be
     *            added to the hashmap.
     * @return true if the feature wasn't allready defined in the hashmap and could therefore be
     *         inserted, or if the key or feature are null.
     */
    public boolean addFeature( String key, DefaultSurface feature ) {
        if ( feature != null && key != null ) {
            DefaultSurface tmp = features.get( key );
            if ( tmp == null && !features.containsKey( key ) ) {
                features.put( key, feature );
                return true;
            }
        }
        return false;
    }

    /**
     * @return the textures value.
     */
    public HashMap<String, BufferedImage> getTextures() {
        return textures;
    }

    /**
     * @param key
     *            the name of the texture to be added.
     * @param texture
     *            to be added to the hashmap.
     * @return true if the texture wasn't allready defined in the hashmap and could therefore be
     *         inserted, or if the key or texture are null.
     */
    public boolean addTexture( String key, BufferedImage texture ) {
        if ( texture != null && key != null ) {
            BufferedImage tmp = textures.get( key );
            if ( tmp == null && !textures.containsKey( key ) ) {
                textures.put( key, texture );
                return true;
            }
        }
        return false;
    }

    /**
     * @return the elevationModelDataSource value.
     */
    public AbstractDataSource getElevationModelDataSource() {
        return elevationModelDataSource;
    }

    /**
     * @param elevationModelDataSource
     *            An other elevationModelDataSource value.
     */
    public void setElevationModelDataSource( AbstractDataSource elevationModelDataSource ) {
        this.elevationModelDataSource = elevationModelDataSource;
    }

    /**
     * @return the featureCollectionDataSources value.
     */
    public ArrayList<AbstractDataSource> getFeatureCollectionDataSources() {
        return featureCollectionDataSources;
    }

    /**
     * @param featureCollectionDataSource
     *            a DataSources for a specific featureCollection.
     */
    public void addFeatureCollectionDataSource( AbstractDataSource featureCollectionDataSource ) {
        if ( featureCollectionDataSource != null ) {
            if ( !featureCollectionDataSources.contains( featureCollectionDataSource ) ) {
                featureCollectionDataSources.add( featureCollectionDataSource );
            }
        }
    }

    /**
     * @return the texturesDataSources value.
     */
    public ArrayList<AbstractDataSource> getTexturesDataSources() {
        return texturesDataSources;
    }

    /**
     * @param textureDataSource
     *            An other texturesDataSources value.
     */
    public void addTextureDataSource( AbstractDataSource textureDataSource ) {
        if ( textureDataSource != null ) {
            if ( !texturesDataSources.contains( textureDataSource ) ) {
                texturesDataSources.add( textureDataSource );
            }
        }
    }

    /**
     * 
     * @return the OutputFormat of the resultImage
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat
     *            the mime type of the resultimage
     */
    public void setOutputFormat( String outputFormat ) {
        this.outputFormat = outputFormat;
    }

    /**
     * After a call to this class call method, it is possible to get a Java3D representation --in
     * form of a BranchGroup-- of this resolutionStripe. In this BranchGroup all the textures and
     * requested features are added to the ElevationModel.
     * 
     * @return a Java3D representation of this ResolutionStripe.
     */
    public BranchGroup getJava3DRepresentation() {
        return resultingJ3DScene;
    }

    /**
     * This call method is part of the Deegree Concurrent framework ({@link org.deegree.framework.concurrent.Executor}) .
     * In this case it requests all the Data for a <tt>ResolutionStripe</tt> by invoking the
     * necessary webservices.
     * 
     * @see java.util.concurrent.Callable#call()
     */
    public ResolutionStripe call()
                            throws OGCWebServiceException {
        int invokeCounter = 0;
        // Strictly the different datasources must not be separated into two different
        // DataSourceList, it might be handy (for caching) to do so though.
        for ( AbstractDataSource textureDS : texturesDataSources ) {
            invokeDataSource( textureDS, invokeCounter++ );
        }

        for ( AbstractDataSource featureDS : featureCollectionDataSources ) {
            invokeDataSource( featureDS, invokeCounter++ );
        }
        if ( elevationModelDataSource != null ) {
            invokeDataSource( elevationModelDataSource, -1 );
        } else {
            elevationModel = new TriangleTerrain( createTrianglesFromBBox(), surface.getEnvelope() );
        }

        createJava3DRepresentation();

        return this;
    }

    private void invokeDataSource( AbstractDataSource ads, int id ) {
        try {
            GetViewServiceInvoker invoker = null;
            if ( ads.getServiceType() == AbstractDataSource.LOCAL_WMS
                 || ads.getServiceType() == AbstractDataSource.REMOTE_WMS ) {
                invoker = new WMSInvoker( this, id );
            } else if ( ads.getServiceType() == AbstractDataSource.LOCAL_WCS
                        || ads.getServiceType() == AbstractDataSource.REMOTE_WCS ) {
                invoker = new WCSInvoker( this, id, outputFormat,
                                          ( ads == elevationModelDataSource ) );
            } else { // WFS -> was checked in DefaultGetViewHandler
                invoker = new WFSInvoker( this, id, ( ads == elevationModelDataSource ) );
            }
            invoker.invokeService( ads );
        } catch ( Exception e ) {
            System.out.println( "\tError after invoking AbstractDataSource in ResolutonStripe: "
                                + e.getLocalizedMessage() );
            // e.printStackTrace();
        }
    }

    private List<float[][]> createTrianglesFromBBox() {
        ArrayList<float[][]> triangles = new ArrayList<float[][]>( 2 );
        Envelope env = surface.getEnvelope();
        double width = env.getWidth();
        double height = env.getHeight();

        Position lowerLeft = env.getMin();
        Position upperRight = env.getMax();
        Position lowerRight = GeometryFactory.createPosition( lowerLeft.getX() + width,
                                                              lowerLeft.getY(), lowerLeft.getZ() );
        Position upperLeft = GeometryFactory.createPosition( lowerLeft.getX(), lowerLeft.getY()
                                                                               + height,
                                                             lowerLeft.getZ() );

        float zValue = new Double( lowerLeft.getZ() ).floatValue();
        if ( Float.isNaN( zValue ) ) {
            zValue = new Double( minimalHeightLevel ).floatValue();
            if ( Float.isNaN( zValue ) )
                zValue = 0f;
        }

        float[][] triangle = new float[3][3];
        triangle[0][0] = (float) lowerLeft.getX();
        triangle[0][1] = (float) lowerLeft.getY();
        triangle[0][2] = zValue;

        triangle[1][0] = (float) upperRight.getX();
        triangle[1][1] = (float) upperRight.getY();
        triangle[1][2] = zValue;

        triangle[2][0] = (float) upperLeft.getX();
        triangle[2][1] = (float) upperLeft.getY();
        triangle[2][2] = zValue;

        triangles.add( triangle );

        triangle = new float[3][3];
        triangle[0][0] = (float) lowerLeft.getX();
        triangle[0][1] = (float) lowerLeft.getY();
        triangle[0][2] = zValue;

        triangle[1][0] = (float) lowerRight.getX();
        triangle[1][1] = (float) lowerRight.getY();
        triangle[1][2] = zValue;

        triangle[2][0] = (float) upperRight.getX();
        triangle[2][1] = (float) upperRight.getY();
        triangle[2][2] = zValue;

        triangles.add( triangle );
        return triangles;
    }

    /**
     * 
     */
    private void createJava3DRepresentation() {
        if ( elevationModel == null ) {
            elevationModel = new TriangleTerrain( createTrianglesFromBBox(), surface.getEnvelope() );
        }
        // add the maps as textures to the elevationModel.
        Collection<BufferedImage> textureImages = textures.values();
        if ( textureImages != null ) {
            // create texture as BufferedImage
            BufferedImage texture = new BufferedImage( getRequestWidthForBBox(),
                                                       getRequestHeightForBBox(),
                                                       BufferedImage.TYPE_INT_ARGB );
            Graphics2D g2d = (Graphics2D) texture.getGraphics();
            if ( textureImages.size() > 0 ) {
                for ( BufferedImage tex : textureImages ) {
                    g2d.drawImage( tex, 0, 0, null );
                }
                // paintString( g2d, Double.toString( minResolution ) );
            } else {
                paintString( g2d, "An error occurred, or no image" );
            }
            elevationModel.setTexture( texture );
        }

        elevationModel.createTerrain();

        // If the elevation model is a heightmap (wcs) the 3d scene must be transformed to the
        // scene's coordinates {@link ResolutionStripe#setElevationModelFromHeightMap}
        // if ( elevationTransform != null ) {
        // elevationTransform.addChild( elevationModel );
        // resultingJ3DScene.addChild( elevationTransform );
        // } else {
        resultingJ3DScene.addChild( elevationModel );
        // }

        // add the features to the elevationModel
        Collection<DefaultSurface> featureSurfaces = features.values();
        if ( featureSurfaces != null ) {
            for ( DefaultSurface ds : featureSurfaces ) {
                resultingJ3DScene.addChild( ds );
            }
        }
        resultingJ3DScene.compile();
    }

    private void paintString( Graphics2D g2d, String stringToPaint ) {
        Font originalFont = g2d.getFont();
        float originalFontSize = originalFont.getSize();
        TextLayout tl = new TextLayout( stringToPaint, originalFont, g2d.getFontRenderContext() );
        Rectangle2D r2d = tl.getBounds();
        double width = r2d.getWidth();
        double height = r2d.getHeight();

        double requestWidth = getRequestWidthForBBox();
        double requestHeight = getRequestHeightForBBox();
        // little widther than the requestwidth ensures total readabillity
        double approx = requestWidth / ( width * 1.2 );
        originalFont = originalFont.deriveFont( (float) ( originalFontSize * approx ) );
        tl = new TextLayout( stringToPaint, originalFont, g2d.getFontRenderContext() );
        r2d = tl.getBounds();
        width = r2d.getWidth();
        height = r2d.getHeight();

        int x = (int) Math.round( ( requestWidth * 0.5 ) - ( width * 0.5 ) );
        int y = (int) Math.round( ( requestHeight * 0.5 ) + ( height * 0.5 ) );
        g2d.setColor( Color.GRAY );
        g2d.drawRect( 0, 0, (int) requestWidth, (int) requestHeight );
        g2d.setColor( Color.RED );
        g2d.setFont( originalFont );
        g2d.drawString( stringToPaint, x, y );
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "Resolution: " + maxResolution + "\n" );
        try {
            sb.append( WKTAdapter.export( this.surface ) );
        } catch ( GeometryException e ) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @return a well known representation of the geometry of this Resolutionstripe
     */
    public String toWKT() {
        try {
            return new StringBuffer( WKTAdapter.export( this.surface ) ).toString();
        } catch ( GeometryException e ) {
            e.printStackTrace();
            return new String( "" );
        }
    }

    /**
     * Outputs the textures to the tmp directory with following format:
     * <code>key_response:___res:_maxresolution__random_id.jpg</code> this file will be deleted at
     * jvm termination.
     */
    public void outputTextures() {
        Set<String> keys = textures.keySet();
        for ( String key : keys ) {
            try {
                // System.out.println( "saving image" );
                File f = File.createTempFile( key + "_response:_" + "__res:_" + maxResolution
                                              + "___", ".jpg" );
                f.deleteOnExit();
                // System.out.println( f );
                // ImageUtils.saveImage( responseImage, f, 1 );
                ImageIO.write( textures.get( key ), "jpg", f );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: -----------------------------------------
 * $Log: ResolutionStripe.java,v $
 * Revision 1.5  2006/12/04 17:06:43  bezema
 * enhanced dgm from wcs support
 * Revision 1.4 2006/11/30 11:26:27 bezema working on the raster
 * heightmap elevationmodel Revision 1.3 2006/11/28 16:53:45 bezema bbox resolution works plus clean
 * up and javadoc
 * 
 * Revision 1.2 2006/11/27 15:43:11 bezema Updated the coordinatesystem handling
 * 
 * Revision 1.1 2006/11/23 11:46:40 bezema The initial version of the new wpvs
 * 
 **************************************************************************************************/

