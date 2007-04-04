//$Header$
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
 Aennchenstraße 19
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

package org.deegree.tools.raster;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.io.dbaseapi.DBaseFile;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.coverage.grid.GridCoverageExchangeIm;
import org.deegree.model.coverage.grid.WorldFile;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;

import com.sun.media.jai.codec.FileSeekableStream;

/**
 * This class represents a <code>RasterTreeBuilder</code> object.
 * TODO more info
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class RasterTreeBuilder {

    private static final ILogger LOG = LoggerFactory.getLogger( RasterTreeBuilder.class );

    private static final URI DEEGREEAPP = CommonNamespaces.buildNSURI( "http://www.deegree.org/app" );

    private static final String APP_PREFIX = "app";

    //  templates and transformation scripts
    private URL configURL = RasterTreeBuilder.class.getResource( "template_wcs_configuration.xml" );

    private URL configXSL = RasterTreeBuilder.class.getResource( "updateConfig.xsl" );

    private URL inputXSL = RasterTreeBuilder.class.getResource( "updateCapabilities.xsl" );

    private int bitDepth = 16;

    private boolean geoTiff = false;

    // input for new MergeRaste object
    private List imageFiles;

    private List imageFilesEnvs;

    private Map imageFilesErrors;

    private ImageCache imageCache = null;

    private String outputDir;

    private String baseName;

    private String outputFormat;

    private double maxTileSize;

    private String srs = null;

    private Interpolation interpolation = null;

    private String worldFileType = null;

    private float quality = 0;

    private String bgColor = null;

    // minimum resolution of input images
    private double minimumRes;

    // combining image bounding box
    private Envelope combiningEnvelope;

    // size of virtual bounding box in px
    private long pxWidthVirtualBBox;

    private long pxHeightVirtualBBox;

    // size of every tile in virtual bounding box in px
    private long pxWidthTile;

    private long pxHeightTile;

    // number of tiles in virtual bounding box
    private int tileRows;

    private int tileCols;

    private FeatureType ftype = null;

    private FeatureCollection fc = null;

    /**
     * 
     * @param imageFiles
     * @param outputDir
     * @param baseName
     * @param outputFormat
     * @param maxTileSize
     * @param srs
     * @param interpolation
     * @param worldFileType
     * @param quality
     * @param cacheSize
     * @param bgColor 
     */
    public RasterTreeBuilder( List imageFiles, String outputDir, String baseName,
                             String outputFormat, double maxTileSize, String srs,
                             String interpolation, String worldFileType, float quality,
                             int cacheSize, String bgColor, int depth ) {

        this.imageFiles = imageFiles;
        this.imageFilesErrors = new HashMap( imageFiles.size() );
        this.imageFilesEnvs = new ArrayList( imageFiles.size() );
        for ( int i = 0; i < imageFiles.size(); i++ ) {
            this.imageFilesEnvs.add( null );
        }
        this.outputDir = outputDir;
        File dir = new File( outputDir ).getAbsoluteFile();
        if ( !dir.exists() ) {
            dir.mkdir();
        }
        this.baseName = baseName;
        this.outputFormat = outputFormat.toLowerCase();
        this.maxTileSize = maxTileSize;
        this.srs = srs;
        this.interpolation = createInterpolation( interpolation );
        this.worldFileType = worldFileType;
        this.quality = quality;
        this.bgColor = bgColor;
        if ( depth != 0 ) {
            this.bitDepth = depth;
        }

        imageCache = new ImageCache( cacheSize );

        PropertyType[] ftp = new PropertyType[3];
        ftp[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "GEOM" ),
                                                          Types.GEOMETRY, false );
        ftp[1] = FeatureFactory.createSimplePropertyType(
                                                          new QualifiedName(
                                                                             GridCoverageExchangeIm.SHAPE_IMAGE_FILENAME ),
                                                          Types.VARCHAR, false );
        ftp[2] = FeatureFactory.createSimplePropertyType(
                                                          new QualifiedName(
                                                                             GridCoverageExchangeIm.SHAPE_DIR_NAME ),
                                                          Types.VARCHAR, false );
        ftype = FeatureFactory.createFeatureType( new QualifiedName( "tiles" ), false, ftp );
    }

    /**
     * @throws IOException
     */
    public void logCollectedErrors()
                            throws IOException {
        FileOutputStream fos = new FileOutputStream( "RasterTreeBuilder" + minimumRes + ".log" );
        PrintWriter pw = new PrintWriter( fos );
        pw.println( "processing the following files caused an error" );
        Iterator iter = imageFilesErrors.keySet().iterator();
        while ( iter.hasNext() ) {
            String key = (String) iter.next();
            String value = (String) imageFilesErrors.get( key );
            pw.print( key );
            pw.print( ": " );
            pw.println( value );
        }
        pw.close();
        LOG.logInfo( "LOG file RasterTreeBuilder.log has been written" );
    }

    /**
     * starts creating of a raster tile level using the current bbox and 
     * resolution
     * @throws Exception 
     */
    public void start()
                            throws Exception {
        System.gc();
        fc = FeatureFactory.createFeatureCollection( Double.toString( minimumRes ), tileRows
                                                                                    * tileCols );
        createTiles( tileRows, tileCols );

        LOG.logInfo( "creating shape for georeferencing ... " );
        ShapeFile sf = new ShapeFile( outputDir + "/sh" + minimumRes, "rw" );
        sf.writeShape( fc );
        sf.close();

    }

    public void init( Envelope env, double resolution ) {

        // set target envelope
        setEnvelope( env );
        setResolution( resolution );
        determineVirtualBBox();
        determineTileSize();
    }

    /**
     * sets the resolution level to be used for tiling
     * @param resolution
     */
    public void setResolution( double resolution ) {
        minimumRes = resolution;
    }

    /**
     * sets the bounding box used for tiling
     * @param bbox
     */
    public void setEnvelope( Envelope bbox ) {
        combiningEnvelope = bbox;
    }

    /**
     * TODO this is a copy from org.deegree.tools.raster#AutoTiler
     * 
     * Extracts the GeoKeys of the GeoTIFF. The Following Tags will be extracted 
     * (http://www.remotesensing.org/geotiff/spec/geotiffhome.html): 
     *         ModelPixelScaleTag = 33550 (SoftDesk) 
     *         ModelTiepointTag = 33922 (Intergraph) 
     * implementation status: working
     */
    //    private WorldFile readBBoxFromGeoTIFF( RenderedOp rop ) {
    //
    //        TIFFDirectory tifDir = (TIFFDirectory) rop.getDynamicProperty( "tiff_directory" );
    //
    //        TIFFField modelPixelScaleTag = tifDir.getField( GeoTiffTag.ModelPixelScaleTag );
    //        double resx = modelPixelScaleTag.getAsDouble( 0 );
    //        double resy = modelPixelScaleTag.getAsDouble( 1 );
    //        TIFFField modelTiepointTag = tifDir.getField( GeoTiffTag.ModelTiepointTag );
    //
    //        double val1 = 0.0;
    //        val1 = modelTiepointTag.getAsDouble( 0 );
    //        double val2 = 0.0;
    //        val2 = modelTiepointTag.getAsDouble( 1 );
    //        double val4 = 0.0;
    //        val4 = modelTiepointTag.getAsDouble( 3 );
    //        double val5 = 0.0;
    //        val5 = modelTiepointTag.getAsDouble( 4 );
    //
    //        Envelope envelope = null;
    //        if ( ( resx == 0.0 || resy == 0.0 )
    //             || ( val1 == 0.0 && val2 == 0.0 && val4 == 0.0 && val5 == 0.0 ) ) {
    //            throw new RuntimeException( "The image/coverage has no bounding box." );
    //            // set the geoparams derived by geoTiffTags            
    //        }
    //        // upper/left pixel
    //        double xOrigin = val4 - ( val1 * resx );
    //        double yOrigin = val5 - ( val2 * resy );
    //        // lower/right pixel
    //        double xRight = xOrigin + rop.getWidth() * resx;
    //        double yBottom = yOrigin - rop.getHeight() * resy;
    //
    //        envelope = GeometryFactory.createEnvelope( xOrigin, yBottom, xRight, yOrigin, null );
    //
    //        return new WorldFile( resx, resy, 0, 0, envelope );
    //    }
    /**
     * TODO this is a copy from org.deegree.tools.raster#AutoTiler
     * 
     * the following TIFFKeys count as indicator if a TIFF-File carries GeoTIFF information: 
     *         ModelPixelScaleTag = 33550 (SoftDesk) 
     *         ModelTransformationTag = 34264 (JPL Carto Group) 
     *         ModelTiepointTag = 33922 (Intergraph) 
     *         GeoKeyDirectoryTag = 34735 (SPOT)
     *         GeoDoubleParamsTag = 34736 (SPOT) 
     *         GeoAsciiParamsTag = 34737 (SPOT) 
     * implementation status: working
     */
    //    private boolean isGeoTIFFFormat( RenderedOp rop ) {
    //
    //        TIFFDirectory tifDir = (TIFFDirectory) rop.getDynamicProperty( "tiff_directory" );
    //        // definition of a geotiff
    //        if ( tifDir.getField( GeoTiffTag.ModelPixelScaleTag ) == null
    //             && tifDir.getField( GeoTiffTag.ModelTransformationTag ) == null
    //             && tifDir.getField( GeoTiffTag.ModelTiepointTag ) == null
    //             && tifDir.getField( GeoTiffTag.GeoKeyDirectoryTag ) == null
    //             && tifDir.getField( GeoTiffTag.GeoDoubleParamsTag ) == null
    //             && tifDir.getField( GeoTiffTag.GeoAsciiParamsTag ) == null ) {
    //
    //            return false;
    //
    //        }
    //        // is a geotiff and possibly might need to be treated as raw data
    //        TIFFField bitsPerSample = tifDir.getField( GeoTiffTag.BitsPerSample );
    //
    //        if ( bitsPerSample != null ) {
    //            int samples = bitsPerSample.getAsInt( 0 );
    //            if ( samples == 16 ) {
    //                bitDepth = 16;
    //            }
    //            if ( samples == 32 ) {
    //                bitDepth = 32;
    //            }
    //        }
    //
    //        // check the EPSG number
    //        TIFFField ff = tifDir.getField( GeoTiffTag.GeoKeyDirectoryTag );
    //        if ( ff == null ) {
    //            return false;
    //        }
    //
    //        char[] ch = ff.getAsChars();
    //
    //        // resulting HashMap, containing the key and the array of values
    //        HashMap geoKeyDirectoryTag = new HashMap( ff.getCount() / 4 );
    //        // array of values. size is 4-1.
    //        int keydirversion, keyrevision, minorrevision, numberofkeys = -99;
    //
    //        for ( int i = 0; i < ch.length; i = i + 4 ) {
    //            int[] keys = new int[3];
    //            keydirversion = ch[i];
    //
    //            keyrevision = ch[i + 1];
    //            minorrevision = ch[i + 2];
    //            numberofkeys = ch[i + 3];
    //            keys[0] = keyrevision;
    //            keys[1] = minorrevision;
    //            keys[2] = numberofkeys;
    //
    //            geoKeyDirectoryTag.put( new Integer( keydirversion ), keys );
    //        }
    //
    //        return true;
    //
    //    }
    /**
     * TODO this is a copy from org.deegree.tools.raster#AutoTiler
     * 
     * loads the base image
     * @throws IOException 
     */
    private Object[] loadImage( String imageSource )
                            throws IOException {
        System.out.println( "read image: " + imageSource );
        BufferedImage bi = null;

        FileSeekableStream fss = new FileSeekableStream( imageSource );
        RenderedOp rop = JAI.create( "stream", fss );

        bi = rop.getAsBufferedImage();

        try {
            fss.close();
        } catch ( IOException e ) {
            // should never happen
        }

        Object[] o = new Object[2];
        o[0] = bi;
        // place holder for later usage
        o[1] = null;

        return o;
    }

    /**
     * Determins the necessary size of a bounding box, which is large enough to hold all 
     * input image files. The result is stored in the combining <code>Envelope</code>.
     * 
     * @throws Exception 
     */
    private WorldFile determineCombiningBBox()
                            throws Exception {

        System.out.println( "calculating overall bounding box ..." );

        if ( imageFiles == null || imageFiles.isEmpty() ) {
            throw new Exception( "No combining BoundingBox to be determined: "
                                 + "The list of image files is null or empty." );
        }

        WorldFile wf1 = null;
        if ( combiningEnvelope == null ) {

            // upper left corner of combining bounding box
            double minX = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;
            // lower right corner of combining bounding box
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            // minimum resolution within combining bounding box
            double minResX = Double.MAX_VALUE;
            double minResY = Double.MAX_VALUE;

            for ( int i = 0; i < imageFiles.size(); i++ ) {

                File file = new File( (String) imageFiles.get( i ) );
                if ( file.exists() && !file.isDirectory() ) {

                    // for faster clean up 
                    System.gc();

                    Object[] o = loadImage( (String) imageFiles.get( i ) );
                    BufferedImage bi = (BufferedImage) o[0];
                    imageCache.putImage( (String) imageFiles.get( i ), bi );
                    WorldFile wf = (WorldFile) o[1];
                    if ( !geoTiff ) {
                        try {
                            wf = WorldFile.readWorldFile( (String) imageFiles.get( i ),
                                                          worldFileType, bi );
                        } catch ( Exception e ) {
                            LOG.logError( e.getMessage() );
                            continue;
                        }
                    }
                    // now the values of resx, resy, envelope of the current image file are available

                    // find min for x and y
                    minX = Math.min( minX, wf.getEnvelope().getMin().getX() );
                    minY = Math.min( minY, wf.getEnvelope().getMin().getY() );
                    // find max for x and y
                    maxX = Math.max( maxX, wf.getEnvelope().getMax().getX() );
                    maxY = Math.max( maxY, wf.getEnvelope().getMax().getY() );

                    // find min for resolution of x and y
                    minResX = Math.min( minResX, wf.getResx() );
                    minResY = Math.min( minResY, wf.getResy() );
                } else {
                    System.out.println( "File: " + imageFiles.get( i ) + " does not exist!" );
                    System.out.println( "Image will be ignored" );
                }

            }
            // store minimum resolution        
            minimumRes = Math.min( minResX, minResY );
            combiningEnvelope = GeometryFactory.createEnvelope( minX, minY, maxX, maxY, null );
        }
        wf1 = new WorldFile( minimumRes, minimumRes, 0, 0, combiningEnvelope );
        return wf1;
    }

    /**
     * Determins a usefull size for the virtual bounding box. It is somewhat larger 
     * than the combining bounding box. The result is stored in the virtual <code>Envelope</code>.
     * 
     */
    private Envelope determineVirtualBBox() {

        WorldFile wf = new WorldFile( minimumRes, minimumRes, 0, 0, combiningEnvelope );
        double width = combiningEnvelope.getWidth();
        double height = combiningEnvelope.getHeight();

        long pxWidth = Math.round( width / minimumRes );
        long pxHeight = Math.round( height / minimumRes );

        // set width and height to next higher even-numbered thousand
        pxWidth = (long) ( maxTileSize * (long) Math.ceil( pxWidth / maxTileSize ) );
        pxHeight = (long) ( maxTileSize * (long) Math.ceil( pxHeight / maxTileSize ) );

        pxWidthVirtualBBox = pxWidth;
        pxHeightVirtualBBox = pxHeight;

        // upper left corner of virtual bounding box
        double minX = combiningEnvelope.getMin().getX();
        double maxY = combiningEnvelope.getMax().getY();

        // lower right corner of virtual bounding box
        double maxX = minX + ( pxWidth * wf.getResx() );
        double minY = maxY - ( pxHeight * wf.getResx() );

        return GeometryFactory.createEnvelope( minX, minY, maxX, maxY, null );
    }

    /**
     * This method determins and sets the size of the tiles in pixel both horizontally (pxWidthTile) 
     * and vertically (pxHeightTile). 
     * It also sets the necessary number of <code>tileCols</code> (depending on the tileWidth) 
     * and <code>tileRows</code> (depending on the tileHeight).
     * 
     * By default, all tiles have a size of close to but less than 6000 pixel either way.
     */
    private void determineTileSize() {
        /*
         * The size of the virtual bbox gets divided by maxTileSize to find an approximat number of 
         * tiles (a).
         * 
         * If the virtual bbox is in any direction (horizontally or vertically) smaler than 
         * maxTileSize px, then it has only 1 tile in that direction. In this case, the size of the tile
         * equals the size of the virtual bbox.
         * 
         * Otherwise, use 'tileCols' and 'tileRows' as the next larger integer to 'a' to get the 
         * minimum number of tiles. Increase this number until the tile size becomes a whole number.
         * 
         * Divide the size of the virtual bbox by the final number of tiles to get its tile size.
         * 
         */
        double a;
        int tileCols, tileRows;

        // determin width of tile
        a = ( pxWidthVirtualBBox / maxTileSize );
        tileCols = (int) Math.ceil( a );
        if ( a <= 1.0 ) {
            pxWidthTile = pxWidthVirtualBBox;
        } else {
            while ( pxWidthVirtualBBox % tileCols > 0 ) {
                tileCols++;
            }
            pxWidthTile = pxWidthVirtualBBox / tileCols;
        }

        // determin height of tile
        a = ( pxHeightVirtualBBox / maxTileSize );
        tileRows = (int) Math.ceil( a );
        if ( a <= 1.0 ) {
            pxHeightTile = pxHeightVirtualBBox;
        } else {
            while ( pxHeightVirtualBBox % tileRows > 0 ) {
                tileRows++;
            }
            pxHeightTile = pxHeightVirtualBBox / tileRows;
        }

        this.tileCols = tileCols;
        this.tileRows = tileRows;

        System.out.println( "minimum resolution: " + minimumRes );
        System.out.println( "width = " + pxWidthVirtualBBox + " *** height = "
                            + pxHeightVirtualBBox );
        System.out.println( "pxWidthTile = " + pxWidthTile + " *** pxHeightTile = " + pxHeightTile );
        System.out.println( "number of tiles: horizontally = " + tileCols + ", vertically = "
                            + tileRows );
    }

    /**
     * Creates one <code>Tile</code> object after the other, with the number of tiles being 
     * specified by the given number of <code>rows</code> and <code>cols</code>. 
     * 
     * Each Tile gets written to the FileOutputStream by the internal call to #paintImagesOnTile.
     *  
     * @param rows
     * @param cols
     * @throws IOException 
     * @throws Exception 
     */
    private void createTiles( int rows, int cols )
                            throws IOException {

        System.out.println( "creating merged image ..." );

        Tile tile = null;

        Envelope virtualEnv = determineVirtualBBox();

        double tileWidth = virtualEnv.getWidth() / cols;
        double tileHeight = virtualEnv.getHeight() / rows;

        double leftX;
        double upperY = virtualEnv.getMax().getY();
        double rightX;
        double lowerY;

        File file = new File( outputDir + "/" + Double.toString( minimumRes ) ).getAbsoluteFile();
        file.mkdir();

        for ( int i = 0; i < rows; i++ ) {
            System.gc();
            System.out.println( "processing row " + i );
            leftX = virtualEnv.getMin().getX();
            lowerY = upperY - tileHeight;
            for ( int j = 0; j < cols; j++ ) {

                System.out.println( "processing tile: " + i + " - " + j );
                rightX = leftX + tileWidth;
                Envelope env = GeometryFactory.createEnvelope( leftX, lowerY, rightX, upperY, null );
                leftX = rightX;

                String postfix = "_" + i + "_" + j;
                tile = new Tile( env, postfix );

                paintImagesOnTile( tile );

            }
            upperY = lowerY;
        }
        System.gc();

    }

    /**
     * Paints all image files that intersect with the passed <code>tile</code> onto that tile 
     * and creates an output file in the <code>outputDir</code>. If no image file intersects with 
     * the given tile, then an empty output file is created. The name of the output file is defined 
     * by the <code>baseName</code> and the tile's index of row and column.
     * 
     * @param tile  The tile on which to paint the image.
     * @throws IOException 
     */
    private void paintImagesOnTile( Tile tile )
                            throws IOException {

        Envelope tileEnv = tile.getTileEnvelope();
        String postfix = tile.getPostfix();

        BufferedImage out = createOutputImage();

        if ( bgColor != null ) {
            Graphics g = out.getGraphics();
            g.setColor( Color.decode( bgColor ) );
            g.fillRect( 0, 0, out.getWidth(), out.getHeight() );
            g.dispose();
        }
        boolean paint = false;
        int gcc = 0;

        for ( int i = 0; i < imageFiles.size(); i++ ) {

            File file = new File( (String) imageFiles.get( i ) );
            if ( imageFilesErrors.get( imageFiles.get( i ) ) == null && file.exists()
                 && !file.isDirectory() ) {

                WorldFile wf = (WorldFile) imageFilesEnvs.get( i );
                BufferedImage bi = null;
                if ( wf == null ) {
                    // just read image if bbox has not been already read
                    Object[] o = loadImage( (String) imageFiles.get( i ) );
                    bi = (BufferedImage) o[0];
                    wf = (WorldFile) o[1];
                    if ( !geoTiff ) {
                        try {
                            wf = WorldFile.readWorldFile( (String) imageFiles.get( i ),
                                                          worldFileType, bi );
                        } catch ( Exception e ) {
                            imageFilesErrors.put( imageFiles.get( i ), e.getMessage() );
                            continue;
                        }
                    }
                    // cache bounding boxes
                    imageFilesEnvs.set( i, wf );
                }
                // now the values of resx, resy, envelope of the current image file are available
                if ( wf.getEnvelope().intersects( tileEnv ) ) {
                    if ( bi == null ) {
                        bi = imageCache.getImage( (String) imageFiles.get( i ) );
                    }
                    if ( bi == null ) {
                        Object[] o = loadImage( (String) imageFiles.get( i ) );
                        bi = (BufferedImage) o[0];
                        imageCache.putImage( (String) imageFiles.get( i ), bi );
                        if ( gcc % 5 == 0 ) {
                            System.gc();
                        }
                        gcc++;
                    }

                    try {
                        drawImage( out, bi, tile, wf );
                        paint = true;
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        imageFilesErrors.put( imageFiles.get( i ), e.getMessage() );
                    }
                }
            } else {
                imageFilesErrors.put( imageFiles.get( i ), "image does not exist!" );
            }
        }
        if ( paint ) {
            // just write files if something has been painted
            storeTileImageToFileSystem( postfix, out );
            createWorldFile( tile );
            String frm = outputFormat;
            if ( "raw".equals( outputFormat ) ) {
                frm = "tif";
            }
            storeEnvelope( Double.toString( minimumRes ), baseName + postfix + '.' + frm, tileEnv );
        }
    }

    /**
     * creates an instance of a BufferedImage depending on requested
     * target format 
     * 
     * @return the new image
     */
    private BufferedImage createOutputImage() {

        BufferedImage out = null;
        if ( "jpg".equals( outputFormat ) || "jpeg".equals( outputFormat )
             || "bmp".equals( outputFormat ) ) {            
            // for bmp, jpg, jpeg use 3 byte:
            out = new BufferedImage( (int) pxWidthTile, (int) pxHeightTile,
                                     BufferedImage.TYPE_INT_RGB );
        } else if ( "tif".equals( outputFormat ) || "tiff".equals( outputFormat )
                    || "png".equals( outputFormat ) ) {            
            // for tif, tiff and png use 4 byte:
            out = new BufferedImage( (int) pxWidthTile, (int) pxHeightTile,
                                     BufferedImage.TYPE_INT_ARGB );
        } else {
            ComponentColorModel ccm;

            if ( bitDepth == 16 ) {                
                ccm = new ComponentColorModel( ColorSpace.getInstance( ColorSpace.CS_GRAY ), null,
                                               false, false, BufferedImage.OPAQUE,
                                               DataBuffer.TYPE_USHORT );
                WritableRaster wr = ccm.createCompatibleWritableRaster( (int) pxWidthTile,
                                                                        (int) pxHeightTile );

                out = new BufferedImage( ccm, wr, false, new Hashtable() );
            } else {                
                out = new BufferedImage( (int) pxWidthTile, (int) pxHeightTile,
                                         BufferedImage.TYPE_INT_ARGB );
            }
        }

        return out;

    }

    /**
     * 
     * @param postfix tile name postfix ( -> tile index $x_$y )
     * @param out tile image to save
     */
    private void storeTileImageToFileSystem( String postfix, BufferedImage out ) {
        try {
            String frm = outputFormat;
            if ( "raw".equals( frm ) ) {
                frm = "tif";
            }
            String imageFile = outputDir + '/' + Double.toString( minimumRes ) + '/' + baseName
                               + postfix + '.' + frm;
            File file = new File( imageFile ).getAbsoluteFile();

            ImageUtils.saveImage( out, file, quality );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * draws an image map to the target tile considering defined interpolation
     * method for rescaling  
     * 
     * @param out target image tile
     * @param image source image map
     * @param tile tile description
     * @param mapEnv bbox of source image map
     * @param wf world fiel description of target image tile 
     * @throws Exception
     */
    private void drawImage( BufferedImage out,final BufferedImage image, Tile tile, WorldFile wf ) {

        Envelope tileEnv = tile.getTileEnvelope();
        Envelope mapEnv = wf.getEnvelope();

        GeoTransform gt2 = new WorldToScreenTransform( mapEnv.getMin().getX(),
                                                       mapEnv.getMin().getY(),
                                                       mapEnv.getMax().getX(),
                                                       mapEnv.getMax().getY(), 0, 0,
                                                       image.getWidth() - 1, image.getHeight() - 1 );

        Envelope inter = mapEnv.createIntersection( tileEnv );
        int x1 = (int) Math.round( gt2.getDestX( inter.getMin().getX() ) );
        int y1 = (int) Math.round( gt2.getDestY( inter.getMax().getY() ) );
        int x2 = (int) Math.round( gt2.getDestX( inter.getMax().getX() ) );
        int y2 = (int) Math.round( gt2.getDestY( inter.getMin().getY() ) );
        BufferedImage newImg = null;
        if ( x2 - x1 + 1 > 1 && y2 - y1 + 1 > 1 ) {
            BufferedImage img = image.getSubimage( x1, y1, x2 - x1 + 1, y2 - y1 + 1 );

            // copy source image to a 4 Byte BufferedImage because there are
            // problems with handling 8 Bit palette images
            if ( img.getColorModel().getPixelSize() == 8 ) { 
                LOG.logInfo( "copy 8Bit image to 32Bit image" );
                BufferedImage bi = new BufferedImage( img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_ARGB );
        
                Graphics g = bi.getGraphics();
                try {
                    g.drawImage( img, 0, 0, null );
                } catch ( Exception e1 ) {
                    System.out.println( e1.getMessage() );
                }
                g.dispose();
            }
            
            if ( wf.getResx() / minimumRes != 1d ||  
                 wf.getResy() / minimumRes != 1d ) {
                ParameterBlock pb = new ParameterBlock();
                pb.addSource( img );
                pb.add( (float) ( wf.getResx() / minimumRes ) ); // The xScale
                pb.add( (float) ( wf.getResy() / minimumRes ) ); // The yScale
                pb.add( 0.0F ); // The x translation
                pb.add( 0.0F ); // The y translation
                pb.add( interpolation ); // The interpolation
                // Create the scale operation
                RenderedOp ro = JAI.create( "scale", pb, null );
                try {
                    newImg = ro.getAsBufferedImage();
                } catch ( Exception e ) {
                    System.out.println( image.getWidth() + " " + image.getHeight() );
                    System.out.println( "ee " + x1 + " " + y1 + " " + ( x2 - x1 + 1 ) + " "
                                        + ( y2 - y1 + 1 ) );
                    System.out.println( e.getMessage() );
                    imageFilesErrors.put( tile.getPostfix(), StringTools.stackTraceToString( e ) );
                }
            } else {
                newImg = img;
            }
        }

        GeoTransform gt = new WorldToScreenTransform( tileEnv.getMin().getX(),
                                                      tileEnv.getMin().getY(),
                                                      tileEnv.getMax().getX(),
                                                      tileEnv.getMax().getY(), 0, 0,
                                                      out.getWidth() - 1, out.getHeight() - 1 );

        x1 = (int) Math.round( gt.getDestX( inter.getMin().getX() ) );
        y1 = (int) Math.round( gt.getDestY( inter.getMax().getY() ) );
        x2 = (int) Math.round( gt.getDestX( inter.getMax().getX() ) );
        y2 = (int) Math.round( gt.getDestY( inter.getMin().getY() ) );
        if ( x2 - x1 > 1 && y2 - y1 > 1 ) {
            // ensure that there is something to draw
            try {
                for ( int i = 0; i < newImg.getWidth(); i++ ) {
                    for ( int j = 0; j < newImg.getHeight(); j++ ) {
                        if ( x1 + i < out.getWidth() && y1 + j < out.getHeight() ) {
                            out.setRGB( x1 + i, y1 + j, newImg.getRGB( i, j ) );
                        }
                    }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                System.out.println( newImg.getWidth() + " " + newImg.getHeight() );
                System.out.println( "ww " + x1 + " " + y1 + " " + ( x2 - x1 + 1 ) + " "
                                    + ( y2 - y1 + 1 ) );
                System.out.println( e.getMessage() );
                imageFilesErrors.put( tile.getPostfix(), StringTools.stackTraceToString( e ) );
            }
        }
    }

    /**
     * creates an interpolation object from a well known name
     * @param interpolation
     * @return
     * @throws Exception
     */
    private Interpolation createInterpolation( String interpolation ) {
        Interpolation interpol = null;

        if ( interpolation.equalsIgnoreCase( "Nearest Neighbor" ) ) {
            interpol = new InterpolationNearest();
        } else if ( interpolation.equalsIgnoreCase( "Bicubic" ) ) {
            interpol = new InterpolationBicubic( 5 );
        } else if ( interpolation.equalsIgnoreCase( "Bicubic2" ) ) {
            interpol = new InterpolationBicubic2( 5 );
        } else if ( interpolation.equalsIgnoreCase( "Bilinear" ) ) {
            interpol = new InterpolationBilinear();
        } else {
            throw new RuntimeException( "invalid interpolation method: " + interpolation );
        }

        return interpol;
    }

    /**
     * Creates a world file for the corresponding tile in the <code>outputDir</code>. The name of 
     * the output file is defined by the <code>baseName</code> and the tile's index of row and 
     * column.
     * 
     * @param tile  The tile for which to create a world file.
     * @throws IOException
     */
    private void createWorldFile( Tile tile )
                            throws IOException {

        Envelope env = tile.getTileEnvelope();
        String postfix = tile.getPostfix();

        StringBuffer sb = new StringBuffer( 1000 );

        sb.append( minimumRes ).append( "\n" ).append( 0.0 ).append( "\n" ).append( 0.0 );
        sb.append( "\n" ).append( ( -1 ) * minimumRes ).append( "\n" );
        sb.append( env.getMin().getX() ).append( "\n" ).append( env.getMax().getY() );
        sb.append( "\n" );

        File f = new File( outputDir + '/' + Double.toString( minimumRes ) + '/' + baseName
                           + postfix + ".wld" );

        FileWriter fw = new FileWriter( f );
        PrintWriter pw = new PrintWriter( fw );

        pw.print( sb.toString() );

        pw.close();
        fw.close();
    }

    /**
     * stores an envelope and the assigend image file information into a 
     * feature/featureCollection
     *  
     * @param dir directory where the image file is stored 
     * @param file name of the image file
     * @param env bbox of the image file
     */
    private void storeEnvelope( String dir, String file, Envelope env ) {
        try {
            Geometry geom = GeometryFactory.createSurface( env, null );
            FeatureProperty[] props = new FeatureProperty[3];
            props[0] = FeatureFactory.createFeatureProperty( "GEOM", geom );
            props[1] = FeatureFactory.createFeatureProperty(
                                                             GridCoverageExchangeIm.SHAPE_IMAGE_FILENAME,
                                                             file );
            props[2] = FeatureFactory.createFeatureProperty( GridCoverageExchangeIm.SHAPE_DIR_NAME,
                                                             dir );
            Feature feat = FeatureFactory.createFeature( "file", ftype, props );
            fc.add( feat );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * creates a configuration file (extended CoverageDescriotion) for a WCS coverage
     * considering the passed resolution levels
     * 
     * @param targetResolutions
     */
    private void createConfigurationFile( double[] targetResolutions ) {

        // copy this file to the target directory
        String resolutions = "";
        java.util.Arrays.sort( targetResolutions );
        int length = targetResolutions.length;

        for ( int i = 0; i < length; i++ ) {
            resolutions += String.valueOf( targetResolutions[length - 1 - i] );
            if ( i < ( length - 1 ) )
                resolutions += ',';
        }

        try {
            Map<String, String> param = new HashMap<String, String>( 20 );
            Envelope llEnv = getLatLonEnvelope( combiningEnvelope );
            param.put( "upperleftll", String.valueOf( llEnv.getMin().getX() ) + ','
                                      + String.valueOf( llEnv.getMin().getY() ) );
            param.put( "lowerrightll", String.valueOf( llEnv.getMax().getX() ) + ','
                                       + String.valueOf( llEnv.getMax().getY() ) );
            param.put( "upperleft", String.valueOf( combiningEnvelope.getMin().getX() ) + ','
                                    + String.valueOf( combiningEnvelope.getMin().getY() ) );
            param.put( "lowerright", String.valueOf( combiningEnvelope.getMax().getX() ) + ','
                                     + combiningEnvelope.getMax().getY() );
            File dir = new File( outputDir );
            if ( dir.isAbsolute() ) {
                param.put( "dataDir", outputDir + '/' );
            } else {
                param.put( "dataDir", "" );
            }
            param.put( "label", baseName );
            param.put( "name", baseName );
            param.put( "description", "" );
            param.put( "keywords", "" );
            param.put( "resolutions", resolutions );
            String frm = outputFormat;
            if ( "raw".equals( outputFormat ) ) {
                frm = "tif";
            }
            param.put( "mimeType", frm );
            String[] t = StringTools.toArray( srs, ":", false );
            param.put( "srs", t[t.length-1] );

            Reader reader = new InputStreamReader( configURL.openStream() );

            XSLTDocument xslt = new XSLTDocument();
            xslt.load( configXSL );
            XMLFragment xml = xslt.transform( reader, XMLFragment.DEFAULT_URL, null, param );
            reader.close();

            // write the result
            String dstFilename = "wcs_" + baseName + "_configuration.xml";
            File dstFile = new File( outputDir, dstFilename );
            String configurationFilename = dstFile.getAbsolutePath().toString();
            FileOutputStream fos = new FileOutputStream( configurationFilename );
            xml.write( fos );
            fos.close();

        } catch ( Exception e1 ) {
            e1.printStackTrace();
        }

    }

    private Envelope getLatLonEnvelope( Envelope env )
                            throws Exception {
        IGeoTransformer gt = new GeoTransformer( "EPSG:4326" );
        return gt.transform( env, srs );
    }

    /**
     * 
     */
    private void updateCapabilitiesFile( File capabilitiesFile ) {

        try {
            XSLTDocument xslt = new XSLTDocument();
            xslt.load( inputXSL );
            Map<String, String> param = new HashMap<String, String>();

            param.put( "dataDirectory", outputDir );
            String url = new File( "wcs_" + baseName + "_configuration.xml" ).toURL().toString();
            param.put( "configFile", url );
            Envelope llEnv = getLatLonEnvelope( combiningEnvelope );
            param.put( "upperleftll", String.valueOf( llEnv.getMin().getX() ) + ','
                                      + String.valueOf( llEnv.getMin().getY() ) );
            param.put( "lowerrightll", String.valueOf( llEnv.getMax().getX() ) + ','
                                       + String.valueOf( llEnv.getMax().getY() ) );

            param.put( "name", baseName );
            param.put( "label", baseName );

            param.put( "description", "" );
            param.put( "keywords", "" );

            XMLFragment xml = new XMLFragment();
            xml.load( capabilitiesFile.toURL() );

            xml = xslt.transform( xml, capabilitiesFile.toURL().toExternalForm(), null, param );

            // write the result
            FileOutputStream fos = new FileOutputStream( capabilitiesFile );
            xml.write( fos );
            fos.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the content of <code>map</code>, to see, if necessary arguments were passed 
     * when calling this class.
     * 
     * @param map
     * @throws Exception
     */
    private static void validate( Properties map )
                            throws Exception {

        if ( map.get( "-outDir" ) == null ) {
            throw new Exception( "-outDir must be set" );
        }
        String s = (String) map.get( "-outDir" );
        if ( s.endsWith( "/" ) || s.endsWith( "\\" ) ) {
            s = s.substring( 0, s.length() - 1 );
        }

        if ( map.get( "-baseName" ) == null ) {
            throw new Exception( "-baseName must be set" );
        }
        if ( map.get( "-outputFormat" ) == null ) {
            map.put( "-outputFormat", "png" );
        } else {
            String format = ( (String) map.get( "-outputFormat" ) ).toLowerCase();
            if ( !"bmp".equals( format ) && !"png".equals( format ) && !"jpg".equals( format )
                 && !"jpeg".equals( format ) && !"tif".equals( format ) && !"tiff".equals( format )
                 && !( "raw" ).equals( format ) ) {

                throw new Exception( "-outputFormat must be one of the following: "
                                     + "'bmp', 'jpeg', 'jpg', 'png', 'tif', 'tiff', 'raw'." );
            }
        }
        if ( map.get( "-maxTileSize" ) == null ) {
            map.put( "-maxTileSize", "500" );
        }
        if ( map.get( "-srs" ) == null ) {
            map.put( "-srs", "EPSG:4326" );
        }
        if ( map.get( "-interpolation" ) == null ) {
            map.put( "-interpolation", "Nearest Neighbor" );
        }
        if ( map.get( "-noOfLevel" ) == null ) {
            map.put( "-noOfLevel", "1" );
        }
        if ( map.get( "-worldFileType" ) == null ) {
            map.put( "-worldFileType", "center" );
        }
        if ( map.get( "-quality" ) == null ) {
            map.put( "-quality", "0.95" );
        }
        if ( map.get( "-cacheSize" ) == null ) {
            map.put( "-cacheSize", "5" );
        }
        if ( map.get( "-bbox" ) != null ) {
            double[] d = StringTools.toArrayDouble( (String) map.get( "-bbox" ), "," );
            Envelope env = GeometryFactory.createEnvelope( d[0], d[1], d[2], d[3], null );
            map.put( "-bbox", env );
            if ( map.get( "-resolution" ) == null ) {
                throw new Exception( "-resolution must be set if -bbox is set" );
            }
            map.put( "-resolution", new Double( (String) map.get( "-resolution" ) ) );
        } else {
            map.put( "-resolution", new Double( -1 ) );
        }
    }

    /**
     * returns the list of image map files to consider read from -mapFiles
     * parameter  
     * 
     * @param mapFiles
     * @return
     */
    private static List getFileList( String[] mapFiles ) {
        List imageFiles = new ArrayList();
        for ( int i = 0; i < mapFiles.length; i++ ) {
            imageFiles.add( mapFiles[i] );
        }
        return imageFiles;
    }

    /**
     * returns the list of image map files to consider read from a defined
     * root directory. 
     * 
     * @param rootDir root directory where to read image map files
     * @param subdirs true if subdirectories of the root directory shall be parsed
     *          for image maps too
     * @return
     */
    private static List getFileList( String rootDir, boolean subdirs ) {
        List list = new ArrayList( 10000 );
        File file = new File( rootDir );
        String[] entries = file.list( new DFileFilter() );
        for ( int i = 0; i < entries.length; i++ ) {
            File entry = new File( rootDir + '/' + entries[i] );
            if ( entry.isDirectory() && subdirs ) {
                list = readSubDirs( entry, list );
            } else {
                list.add( rootDir + '/' + entries[i] );
            }
        }
        return list;
    }

    /**
     * 
     * @param file
     * @param list
     * @return
     */
    private static List readSubDirs( File file, List list ) {
        String[] entries = file.list( new DFileFilter() );
        for ( int i = 0; i < entries.length; i++ ) {
            File entry = new File( file.getAbsolutePath() + '/' + entries[i] );
            if ( entry.isDirectory() ) {
                list = readSubDirs( entry, list );
            } else {
                list.add( file.getAbsolutePath() + '/' + entries[i] );
            }
        }
        return list;
    }

    /**
     * returns the list of image map files to consider read from a dbase file
     * defined by the dbase parameter
     * 
     * @param dbaseFile name of the dbase file 
     * @param fileColumn name of the column containing the image map files names
     * @param baseDir name of the directory where the image map files are stored
     *                if this parameter is <code>null</code> it is assumed that
     *                the image map files are full referenced within the dbase
     * @param sort true if map image file names shall be sorted 
     * @param sortColum name of the column that shall be used for sorting
     * @return
     */
    private static List getFileList( String dBaseFile, String fileColumn, String baseDir,
                                    boolean sort, String sortColum, String sortDirection )
                            throws Exception {

        //handle dbase file extension and file location/reading problems
        if ( dBaseFile.endsWith( ".dbf" ) ) {
            dBaseFile = dBaseFile.substring( 0, dBaseFile.lastIndexOf( "." ) );
        }
        DBaseFile dbf = new DBaseFile( dBaseFile );

        // sort dbase file contents chronologicaly (oldest first)
        int cnt = dbf.getRecordNum();

        Object[][] mapItems = new Object[cnt][2];
        QualifiedName fileC = new QualifiedName( APP_PREFIX, fileColumn.toUpperCase(), DEEGREEAPP );
        QualifiedName sortC = null;
        if ( sort ) {
            sortC = new QualifiedName( APP_PREFIX, sortColum.toUpperCase(), DEEGREEAPP );
        }
        for ( int i = 0; i < cnt; i++ ) {
            if ( sort ) {
                mapItems[i][0] = dbf.getFRow( i + 1 ).getDefaultProperty( sortC ).getValue();
            } else {
                mapItems[i][0] = new Integer( 1 );
            }
            // name of map file
            mapItems[i][1] = dbf.getFRow( i + 1 ).getDefaultProperty( fileC ).getValue();
        }
        Arrays.sort( mapItems, new MapAgeComparator( sortDirection ) );

        // extract names of image files from dBase file and attach them to rootDir
        if ( baseDir == null ) {
            baseDir = "";
        } else if ( !baseDir.endsWith( "/" ) && !baseDir.endsWith( "\\" ) ) {
            baseDir = baseDir + "/";
        }
        List imageFiles = new ArrayList( mapItems.length );
        for ( int i = 0; i < mapItems.length; i++ ) {
            if ( mapItems[i][0] != null ) {
                LOG.logDebug( "" + mapItems[i][0] );
                imageFiles.add( baseDir + mapItems[i][1] );
            }
        }

        return imageFiles;
    }

    private static void printHelp() {
        System.out.println( "-outDir directory where resulting tiles and describing shape(s) will be stored (mandatory)\r\n"
                            + "-baseName base name used for creating names of the raster tile files. It also will be the name of the created coverage. (mandatory)\r\n"
                            + "-outputFormat name of the image format used for created tiles (png|jpg|jpeg|bmp|tif|tiff|gif|raw default png)\r\n"
                            + "-maxTileSize maximum size of created raster tiles in pixel (default 500)\r\n"
                            + "-srs name of the spatial reference system used for the coverage (default EPSG:4326)\r\n"
                            + "-interpolation interpolation method used for rescaling raster images (Nearest Neighbor|Bicubic|Bicubic2|Bilinear default Nearest Neighbor)\r\n"
                            + "               be careful using Bicubic and Bicubic2 interpolation; there seems to be a problem with JAI\r\n"
                            + "               If you use the proogram with images (tif) containing raw data like DEMs just use \r\n"
                            + "               Nearest Neighbor interpolation. All other interpolation methods will cause artefacts."
                            + "-bbox boundingbox of the the resulting coverage. If not set the bbox will be determined by analysing the input map files. (optional)\r\n"
                            + "-resolution spatial resolution of the resulting coverage. If not set the resolution will determined by analysing the input map files. This parameter is conditional; if -bbox is defined -resolution must be defined too.\r\n"
                            + "-noOfLevel number of tree levels created (optional default = 1)\r\n"
                            + "-capabilitiesFile name of a deegree WCS capabilities/configuration file. If defined the program will add the created rastertree as a new coverage to the WCS configuration.\r\n"
                            + "-h or -? print this help\r\n"
                            + "\r\n"
                            + "Input files\r\n"
                            + "there are three alternative ways/parameters to define which input files shall be used for creating a raster tree:\r\n"
                            + "1)\r\n"
                            + "-mapFiles defines a list of image file names (including full path information) seperated by \',\', \';\' or \'|\'\r\n"
                            + "\r\n"
                            + "2)\r\n"
                            + "-rootDir defines a directory that shall be parsed for files in a known image format. Each file found will be used as input.\r\n"
                            + "-subDirs conditional parameter used with -rootDir. It defines if all sub directories of -rootDir shall be parsed too (true|false default false)\r\n"
                            + "\r\n"
                            + "3)\r\n"
                            + "-dbaseFile name a dBase file that contains a column listing all files to be considered by the program\r\n"
                            + "-fileColumn name of the column containing the file names (mandatory if -dbaseFile is defined)\r\n"
                            + "-baseDir name of the directory where the files are stored. If this parameter will not be set the program assumes the -fileColumn contains completely referenced file names (optional)\r\n"
                            + "-sortColumn If -dbaseFile is defined one can define a column that shall be used for sorting the files referenced by the -fileColumn (optional)\r\n"
                            + "-sortDirection If -sortColumn is defined this parameter will be used for definition of sorting direction (UP|DOWN default UP)\r\n"
                            + "-worldFileType two types of are common: \r\n "
                            + "               a) the boundingbox is defined on the center of the corner pixels; \r\n "
                            + "               b) the boundingbox is defined on the outter corner of the corner pixels; \r\n "
                            + "               first is default and will be used if this parameter is not set; second will be use if '-worldFileType outter' is defined.\r\n"
                            + "-quality image quality if jpeg is used as output format; valid range is from 0..1 (default 0.95) \r\n"
                            + "-bitDepth image bit depth; valid values are 32 and 16, default is 16 \r\n"
                            + "-cacheSize number of images that shall be cached. A larger value (~20) increases speed but also amount of required memory. (default = 5)\r\n"
                            + "-bgColor defines the background color of the created tiles for those region no data are available (e.g. -bgColor 0xFFFFF defines background as white) \r\n"
                            + "         If no -bgColor is defined, transparent background will be used for image formats that are transparency enabled (e.g. png) and black is used for all other formats (e.g. bmp) \r\n"
                            + "\r\n"
                            + "Common to all option defining the input files is that each referenced file must be in a known image format (png, tif, jpeg, bmp, gif) and if must be geo-referenced by a world file or must be a GeoTIFF." );
        System.out.println();
        System.out.println( "Example (windows):" );
        System.out.println( "rem set environment variables to enable coordinate system transformation" );
        System.out.println( "set PATH=%PATH%;D:\\deegree\\lib\\proj4\\win32" );
        System.out.println( "set PROJ_LIB=D:\\java\\source\\deegree2\\lib\\proj4" );
        System.out.println( "java -Xms300m -Xmx1000m -classpath .;./classes;./lib/jai_codec.jar;"
                            + "./lib/jai_core.jar;./lib/mlibwrapper_jai.jar;./lib/jts-1.6.jar;"
                            + "./lib/jaxen-1.1-beta-7.jar org.deegree.tools.raster.RasterTreeBuilder "
                            + "-dbaseFile D:/lgv/resources/data/dbase/dip.dbf -outDir D:/lgv/output/ "
                            + "-baseName out -outputFormat jpg -maxTileSize 500 -noOfLevel 4 -interpolation "
                            + "Bilinear -bbox 3542428,5918168,3593354,5957043 -resolution 0.2 -sortColumn "
                            + "PLANJAHR -fileColumn NAME_PNG -sortDirection UP -quality 0.91 -baseDir "
                            + "D:/lgv/resources/data/images/ -cacheSize 10" );
    }

    /**
     * 
     * @param args Example arguments to pass when calling are:
     * <ul> 
     *      <li>-mapFiles D:/temp/europe_DK.jpg,D:/temp/europe_BeNeLux.jpg</li> 
     *      <li>-outDir D:/temp/out/</li>
     *      <li>-baseName pretty</li>
     *      <li>-outputFormat png</li>
     *      <li>-maxTileSize 600</li>
     * </ul>     
     * 
     * @throws Exception
     */
    public static void main( String[] args )
                            throws Exception {

        Properties map = new Properties();
        for ( int i = 0; i < args.length; i += 2 ) {
            map.put( args[i], args[i + 1] );
        }

        if ( map.get( "-?" ) != null || map.get( "-h" ) != null ) {
            printHelp();
            return;
        }

        try {
            validate( map );
        } catch ( Exception e ) {
            LOG.logInfo( map.toString() );
            System.out.println( e.getMessage() );
            System.out.println();
            printHelp();
            return;
        }

        // read input parameters        
        String outDir = map.getProperty( "-outDir" );
        String baseName = map.getProperty( "-baseName" );
        String outputFormat = map.getProperty( "-outputFormat" );
        String srs = map.getProperty( "-srs" );
        String interpolation = map.getProperty( "-interpolation" );
        Envelope env = (Envelope) map.get( "-bbox" );
        double resolution = ( (Double) map.get( "-resolution" ) ).doubleValue();
        int level = Integer.parseInt( map.getProperty( "-noOfLevel" ) );
        double maxTileSize = ( Double.valueOf( map.getProperty( "-maxTileSize" ) ) ).doubleValue();
        String worldFileType = map.getProperty( "-worldFileType" );
        float quality = Float.parseFloat( map.getProperty( "-quality" ) );
        int cacheSize = Integer.parseInt( map.getProperty( "-cacheSize" ) );
        String backgroundColor = map.getProperty( "-bgColor" );

        int depth = 0;

        if ( map.get( "-bitDepth" ) != null ) {
            depth = Integer.parseInt( map.get( "-bitDepth" ).toString() );
        }

        List imageFiles = null;
        if ( map.get( "-mapFiles" ) != null ) {
            String[] mapFiles = StringTools.toArray( map.getProperty( "-mapFiles" ), ",;|", true );
            imageFiles = getFileList( mapFiles );
        } else if ( map.get( "-dbaseFile" ) != null ) {
            String dBaseFile = map.getProperty( "-dbaseFile" );
            String fileColum = map.getProperty( "-fileColumn" );
            String baseDir = map.getProperty( "-baseDir" );
            if ( baseDir == null ) {
                baseDir = map.getProperty( "-rootDir" );
            }
            boolean sort = map.get( "-sortColumn" ) != null;
            String sortColumn = map.getProperty( "-sortColumn" );
            if ( map.get( "-sortDirection" ) == null ) {
                map.put( "-sortDirection", "UP" );
            }
            String sortDirection = map.getProperty( "-sortDirection" );
            imageFiles = getFileList( dBaseFile, fileColum, baseDir, sort, sortColumn,
                                      sortDirection );
        } else if ( map.get( "-rootDir" ) != null ) {
            String rootDir = map.getProperty( "-rootDir" );
            boolean subDirs = "true".equals( map.get( "-subDirs" ) );
            imageFiles = getFileList( rootDir, subDirs );
        } else {
            LOG.logInfo( map.toString() );
            System.out.println( "-mapFiles, -rootDir or -dbaseFile parameter must be defined" );
            printHelp();
            return;
        }

        LOG.logDebug( imageFiles.toString() );
        LOG.logInfo( map.toString() );

        // initialize RasterTreeBuilder 
        RasterTreeBuilder rtb = new RasterTreeBuilder( imageFiles, outDir, baseName, outputFormat,
                                                       maxTileSize, srs, interpolation,
                                                       worldFileType, quality, cacheSize,
                                                       backgroundColor, depth );

        // calculate bbox and resolution from input images if parameters are not set
        if ( env == null ) {
            WorldFile wf = rtb.determineCombiningBBox();
            env = wf.getEnvelope();
            resolution = wf.getResx();
        }

        // Calculate necessary number of levels to get not more than 4
        // tiles in highest resolution
        if ( level == -1 ) {
            rtb.init( env, resolution );
            level = 0;
            int numTilesMax = Math.min( rtb.tileCols, rtb.tileRows );
            int numTiles = 4;
            while ( numTiles < numTilesMax ) {
                level += 1;
                numTiles *= 2;
            }
        }
        if ( level == 0 )
            level = 1;
        System.out.println( "Number of Levels: " + level );

        // create tree where for each loop resolution will be halfed
        double[] re = new double[level];
        for ( int i = 0; i < level; i++ ) {
            rtb.init( env, resolution );
            rtb.start();
            rtb.logCollectedErrors();
            re[i] = resolution;
            if ( i < level - 1 ) {
                String dir = outDir + "/" + Double.toString( resolution );
                imageFiles = getFileList( dir, false );
                rtb = new RasterTreeBuilder( imageFiles, outDir, baseName, outputFormat,
                                             maxTileSize, srs, interpolation, "outter", quality,
                                             cacheSize, backgroundColor, depth );
                resolution = resolution * 2;
            }
        }

        LOG.logInfo( "create configuration files ..." );
        rtb.createConfigurationFile( re );

        if ( map.get( "-capabilitiesFile" ) != null ) {
            LOG.logInfo( "adjust capabilities ..." );
            File file = new File( map.getProperty( "-capabilitiesFile" ) );
            rtb.updateCapabilitiesFile( file );
        }

        rtb.logCollectedErrors();
    }

    /**
     * simple class for caching images
     *      
     */
    class ImageCache {

        private int size = 3;

        private int k = 1;

        private Object[][] cache;

        public ImageCache( int size ) {
            this.size = size;
            cache = new Object[size][2];
        }

        public BufferedImage getImage( String name ) {
            for ( int i = 0; i < cache.length; i++ ) {
                if ( name.equals( cache[i][0] ) ) {
                    return (BufferedImage) cache[i][1];
                }
            }
            return null;
        }

        public void putImage( String name, BufferedImage image ) {
            if ( getImage( name ) == null ) {
                cache[k % size][0] = name;
                cache[k % size][1] = image;
                k++;
            }
        }

    }

    /**
     * class: official version of a FilenameFilter
     */
    static class DFileFilter implements FilenameFilter {

        private List extensions = null;

        public DFileFilter() {
            extensions = new ArrayList();
            extensions.add( "JPEG" );
            extensions.add( "JPG" );
            extensions.add( "BMP" );
            extensions.add( "PNG" );
            extensions.add( "GIF" );
            extensions.add( "TIF" );
            extensions.add( "TIFF" );
            extensions.add( "GEOTIFF" );
        }

        public String getDescription() {
            return "*.*";
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept( java.io.File file, String name ) {
            int pos = name.lastIndexOf( "." );
            String ext = name.substring( pos + 1 ).toUpperCase();
            if ( file.isDirectory() ) {
                String s = file.getAbsolutePath() + '/' + name;
                File tmp = new File( s );
                if ( tmp.isDirectory() ) {
                    return true;
                }
            }
            return extensions.contains( ext );
        }
    }

    /**
     * 
     * This class enables sorting of dBaseFile objects in chronological order 
     * (lowest first, highest last).
     * 
     * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version 2.0, $Revision$, $Date$
     * 
     * @since 2.0
     */
    private static class MapAgeComparator implements Comparator {

        private String direction = null;

        public MapAgeComparator( String direction ) {
            this.direction = direction.toUpperCase();
        }

        public int compare( Object o1, Object o2 ) {
            Object[] o1a = (Object[]) o1;
            Object[] o2a = (Object[]) o2;

            if ( o1a[0] == null || o2a[0] == null ) {
                return 0;
            }
            if ( direction.equals( "UP" ) ) {
                return o1a[0].toString().compareTo( o2a[0].toString() );
            }
            return o2a[0].toString().compareTo( o1a[0].toString() );
        }
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log$
 Revision 1.52  2006/12/03 21:21:47  poth
 bug fix - setting CRS to created WCS configuration

 Revision 1.51  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.50  2006/11/18 11:08:52  poth
 *** empty log message ***

 Revision 1.49  2006/11/17 08:59:26  poth
 bug fix

 Revision 1.48  2006/11/16 10:33:40  poth
 useless code removed

 Revision 1.47  2006/11/16 10:02:29  poth
 bug fix: g.drawImage( img, ... ) does not work as expected if 'img' contains raw data ...

 Revision 1.46  2006/11/15 16:30:40  schmitz
 Using BufferedImage.TYPE_INT_ARGB now, and returned to Graphics.drawImage copying.

 Revision 1.45  2006/11/15 11:22:02  schmitz
 Data is now copied by hand using the databuffers.

 Revision 1.44  2006/11/13 10:23:16  poth
 support for automatic detection of useful pyramid levels

 Revision 1.43  2006/11/08 16:47:47  schmitz
 Changed formatting, removed imports.

 Revision 1.42  2006/11/08 16:12:29  schmitz
 Fixed the usage of ushort databuffers.

 Revision 1.41  2006/11/08 10:52:46  poth
 a few simplifications reading source images

 Revision 1.40  2006/11/06 20:48:19  poth
 support for raw data (32 BIT Tiff without color map ->  DEM) added

 Revision 1.39  2006/09/27 16:46:41  poth
 transformation method signature changed

 Revision 1.38  2006/09/07 07:10:20  poth
 bug fix reading sub directories

 Revision 1.37  2006/08/30 16:59:13  mschneider
 Moved definitions of DEEGREEAPP and APP_PREFIX here (this are *not* constant bindings).

 Revision 1.36  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.35  2006/05/25 20:02:13  poth
 support for using relative pathes for -outDir parameter added

 Revision 1.34  2006/05/18 07:52:35  poth
 bug fix for tiles sorted by a dbase file

 Revision 1.33  2006/05/12 15:26:05  poth
 *** empty log message ***

 Revision 1.32  2006/05/04 08:31:29  poth
 *** empty log message ***

 Revision 1.31  2006/05/03 20:09:52  poth
 *** empty log message ***

 Revision 1.30  2006/05/03 13:00:06  poth
 *** empty log message ***

 Revision 1.29  2006/05/03 09:02:43  poth
 *** empty log message ***

 Revision 1.28  2006/05/01 20:15:26  poth
 *** empty log message ***

 Revision 1.27  2006/04/15 15:30:20  poth
 *** empty log message ***

 Revision 1.26  2006/04/06 20:25:26  poth
 *** empty log message ***

 Revision 1.25  2006/03/31 13:31:28  poth
 *** empty log message ***

 Revision 1.24  2006/03/31 13:22:09  poth
 *** empty log message ***

 Revision 1.23  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.22  2006/03/15 22:20:09  poth
 *** empty log message ***

 Revision 1.21  2006/03/14 08:06:14  poth
 *** empty log message ***

 Revision 1.20  2006/03/06 12:40:45  poth
 *** empty log message ***

 Revision 1.19  2006/02/23 07:45:24  poth
 *** empty log message ***

 Revision 1.18  2006/02/20 09:27:26  poth
 *** empty log message ***

 Revision 1.17  2006/02/09 11:30:07  poth
 *** empty log message ***

 Revision 1.16  2006/02/09 11:29:09  poth
 *** empty log message ***

 Revision 1.15  2006/02/07 13:40:48  poth
 *** empty log message ***

 Revision 1.14  2006/02/06 15:34:50  poth
 *** empty log message ***

 Revision 1.13  2006/02/06 13:38:48  ncho
 SN

 AP changed

 Revision 1.12  2006/02/04 14:36:20  poth
 *** empty log message ***

 Revision 1.11  2006/02/03 07:53:37  poth
 *** empty log message ***

 Revision 1.10  2006/01/31 19:19:05  poth
 *** empty log message ***

 Revision 1.9  2006/01/31 19:16:34  poth
 *** empty log message ***

 Revision 1.5  2006/01/31 13:06:16  poth
 *** empty log message ***

 Revision 1.3  2006/01/30 09:39:55  poth
 *** empty log message ***

 Revision 1.2  2006/01/30 08:52:48  poth
 *** empty log message ***

 Revision 1.1  2006/01/29 20:59:08  poth
 *** empty log message ***

 Revision 1.11  2006/01/27 15:44:46  poth
 *** empty log message ***

 Revision 1.10  2006/01/20 19:45:53  poth
 *** empty log message ***

 Revision 1.9  2006/01/13 08:48:42  poth
 *** empty log message ***

 Revision 1.8  2006/01/12 15:27:26  mays
 create world file for each tile and create empty image files for empty tiles

 Revision 1.7  2006/01/09 09:18:39  mays
 validate outputFormat

 Revision 1.6  2006/01/06 18:06:28  mays
 make use of args passed to main method; and minor restructuring

 Revision 1.5  2006/01/06 15:18:19  poth
 *** empty log message ***

 Revision 1.4  2006/01/06 14:24:35  mays
 major restructuring because of buffer size problems. 
 now, images and tiles are only read into buffer, when realy needed.

 Revision 1.3  2006/01/06 10:35:53  mays
 add methods to create tiles and images; major restructuring and minor cleanup

 Revision 1.2  2006/01/05 11:37:55  mays
 add methods to create tiles images and tiles bboxes; minor cleanup

 Revision 1.1  2006/01/04 17:17:26  mays
 first implementation of class - not finished yet

 ********************************************************************** */
