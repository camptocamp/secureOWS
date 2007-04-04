/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 University of Bonn
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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.tools.raster;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.graphics.Encoders;
import org.deegree.io.geotiff.GeoTiffKey;
import org.deegree.io.geotiff.GeoTiffTag;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.coverage.grid.GridCoverageExchangeIm;
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

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFField;

/**
 * @deprecated use @see org.deegree.tools.raster.RasterTreeBuilder instead; this class
 * will be removed from deegree at the end of 2007
 * 
 * @author Norman Barker
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 */
public class AutoTiler {

    private static final ILogger LOG = LoggerFactory.getLogger( AutoTiler.class );

    private boolean geoTiff = false;

    // main image
    private RenderedOp rop = null;

    private BufferedImage image = null;

    private ProgressObserver progressObserver = null;

    protected double[] targetResolutions = null;

    private HashMap featColl = new HashMap();

    private Interpolation interpolation = null;;

    // main image resolution
    private double resx = 0;

    private double resy = 0;

    private double xmax = 0;

    // main image bounding box
    private double xmin = 0;

    private double ymax = 0;

    private double ymin = 0;

    private String crs = null;

    // layerID and title of the layer
    private String imageName = null;

    private String targetDir = null;

    private String targetFormat = null;

    private String mimeType = null;

    private Integer bitDepth = null;

    private String name = null;

    private String description = null;

    private String keywords = null;

    private String configurationFilename = null;

    private float quality;

    private int count = 0;

    // global variable
    public static double WCS_ORDINATE_LOW_X;

    public static double WCS_ORDINATE_LOW_Y;

    public static double WCS_ORDINATE_HIGH_X;

    public static double WCS_ORDINATE_HIGH_Y;

    public static double MIN_X_SPATIAL = -1;

    public static double MIN_Y_SPATIAL = -1;

    public static double MAX_X_SPATIAL;

    public static double MAX_Y_SPATIAL;

    public static double MIN_X_LL = -1;

    public static double MIN_Y_LL = -1;

    public static double MAX_X_LL = -90;

    public static double MAX_Y_LL = -180;

    public static String CAPABS_XSL = "updateCapabilities.xsl";

    public static String CONFIG_TEMPLATE = "template_wcs_configuration.xml";

    public static String CONFIG_XSL = "updateConfig.xsl";

    /**
     * @param inFile
     * @param dir
     * @param format
     * @param targetRes
     * @param startIndex
     * @param quality
     * @param crs
     */
    public AutoTiler( String imageSource, String targetDir, String targetFormat,
                     double[] targetResolutions, float quality, String crs,
                     String interpolation ) throws Exception {

        this.targetDir = targetDir;
        this.interpolation = createInterpolation( interpolation );
        File file = new File( targetDir );
        if ( !file.exists() ) {
            file.mkdir();
        }
        this.targetFormat = targetFormat.toLowerCase();
        this.quality = quality;
        setTargetResolutions( targetResolutions );
        int pos = imageSource.lastIndexOf( '/' );
        this.imageName = imageSource.substring( pos + 1, imageSource.length() );
        this.crs = crs;
        // load the main image
        image = loadImage( imageSource );
        // get the bounding box of the source image by evaluating its world
        // file
        if ( !geoTiff ) {
            readWorldFile( imageSource );
        }
    }

    private Interpolation createInterpolation( String interpolation )
                            throws Exception {
        Interpolation interpol = null;
        if ( interpolation == null )
            interpolation = "Nearest Neighbor";

        if ( interpolation.equalsIgnoreCase( "Nearest Neighbor" ) ) {
            interpol = new InterpolationNearest();
        } else if ( interpolation.equalsIgnoreCase( "Bicubic" ) ) {
            interpol = new InterpolationBicubic( 5 );
        } else if ( interpolation.equalsIgnoreCase( "Bicubic2" ) ) {
            interpol = new InterpolationBicubic2( 5 );
        } else if ( interpolation.equalsIgnoreCase( "Bilinear" ) ) {
            interpol = new InterpolationBilinear();
        } else {
            throw new Exception( "invalid interpolation method: " + interpolation );
        }

        return interpol;
    }

    /**
     * Gets the latitude and longitude coordinates (xmin, ymin, xmax and ymax) of the image.
     */
    private void readWorldFile( String filename )
                            throws Exception {
        try {
            // Gets the substring beginning at the specified beginIndex (0) -
            // the
            // beginning index, inclusive - and extends to the character at
            // index endIndex (position of '.') - the ending index, exclusive.
            String fname = null;
            int pos = filename.lastIndexOf( "." );
            filename = filename.substring( 0, pos );
            // Looks for corresponding worldfiles.
            if ( ( new File( filename + ".tfw" ) ).exists() ) {
                fname = filename + ".tfw";
            } else if ( ( new File( filename + ".wld" ) ).exists() ) {
                fname = filename + ".wld";
            } else if ( ( new File( filename + ".jgw" ) ).exists() ) {
                fname = filename + ".jgw";
            } else if ( ( new File( filename + ".jpgw" ) ).exists() ) {
                fname = filename + ".jpgw";
            } else if ( ( new File( filename + ".gfw" ) ).exists() ) {
                fname = filename + ".gfw";
            } else if ( ( new File( filename + ".gifw" ) ).exists() ) {
                fname = filename + ".gifw";
            } else if ( ( new File( filename + ".pgw" ) ).exists() ) {
                fname = filename + ".pgw";
            } else if ( ( new File( filename + ".pngw" ) ).exists() ) {
                fname = filename + ".pngw";
            } else {
                throw new Exception( "Not a world file for: " + filename );
            }
            // Reads character files.
            // The constructors of this class (FileReader) assume that the
            // default character
            // encoding and the default byte-buffer size are appropriate.
            // The BufferedReader reads text from a character-input stream,
            // buffering characters so as
            // to provide for the efficient reading of characters
            BufferedReader br = new BufferedReader( new FileReader( fname ) );
            String s = null;
            int cnt = 0;
            double d1 = 0;
            double d2 = 0;
            double d3 = 0;
            double d4 = 0;
            while ( ( s = br.readLine() ) != null ) {
                cnt++;
                s = s.trim();
                switch ( cnt ) {
                case 1:
                    d1 = Double.parseDouble( s );
                    break;
                case 4:
                    d2 = Double.parseDouble( s );
                    break;
                case 5:
                    d3 = Double.parseDouble( s );
                    break;
                case 6:
                    d4 = Double.parseDouble( s );
                    break;
                }
            }
            br.close();
            double d5 = d3 + ( image.getWidth() * d1 );
            double d6 = d4 + ( image.getHeight() * d2 );
            ymax = d4;
            ymin = d6;
            xmax = d5;
            xmin = d3;
            resx = ( xmax - xmin ) / image.getWidth();
            resy = ( ymax - ymin ) / image.getHeight();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * loads the base image
     */
    private BufferedImage loadImage( String imageSource )
                            throws Exception {
        LOG.logInfo( "reading source image ..." );
        BufferedImage bi = null;
        FileSeekableStream fss = new FileSeekableStream( imageSource );
        rop = JAI.create( "stream", fss );
        if ( imageSource.toUpperCase().endsWith( ".TIFF" )
             || imageSource.toUpperCase().endsWith( ".TIF" ) ) {
            geoTiff = isGeoTIFFFormat( rop );
            if ( geoTiff ) {
                readBBoxFromGeoTIFF( rop );

                if ( ( bitDepth != null ) && ( bitDepth.intValue() == 16 ) ) {
                    int width = rop.getWidth();
                    int height = rop.getHeight();
                    // read data as 'raw' information and not as image
                    Raster raster = rop.getData( new Rectangle( 0, 0, width, height ) );

                            // get the data
                    short[] o = (short[]) raster.getDataElements( 0, 0, width, height, null );

                    int bands = raster.getNumBands();
                    short[][] bb = new short[bands][];
                    for ( int i = 0; i < bands; i++ ) {
                        bb[i] = new short[raster.getWidth() * raster.getHeight()];
                    }

                    int c = 0;
                    int u = 0;
                    for ( int i = 0; i < raster.getWidth(); i++ ) {
                        for ( int j = 0; j < raster.getHeight(); j++ ) {
                            for ( int z = 0; z < bands; z++ ) {
                                bb[z][u] = o[c++];
                            }
                            u++;
                        }
                    }

                    // create a new image from the data and serialize it to a file
                    DataBuffer db = new DataBufferShort( bb, width * height );
                    SampleModel sm = new BandedSampleModel( DataBuffer.TYPE_SHORT, width, height,
                                                            bands );
                    raster = Raster.createWritableRaster( sm, db, null );
                    // from a theoretical point of view the usage of TYPE_USHORT_GRAY
                    // doesn't seem to be very good idea but it works and I didn't found a
                    // way to create a RenderedImage with type Short
                    bi = new BufferedImage( width, height, BufferedImage.TYPE_USHORT_GRAY );
                    bi.setData( raster );
                }
            }
        }
        if ( bi == null ) {
            bi = rop.getAsBufferedImage();
        }
        fss.close();
        LOG.logInfo( "finished" );
        return bi;
    }

    /**
     * description: Extracts the GeoKeys of the GeoTIFF. The Following Tags will be
     * extracted(http://www.remotesensing.org/geotiff/spec/geotiffhome.html): ModelPixelScaleTag =
     * 33550 (SoftDesk) ModelTiepointTag = 33922 (Intergraph) implementation status: working
     */
    private void readBBoxFromGeoTIFF( RenderedOp rop )
                            throws Exception {
        TIFFDirectory tifDir = (TIFFDirectory) rop.getDynamicProperty( "tiff_directory" );

        TIFFField modelPixelScaleTag = tifDir.getField( GeoTiffTag.ModelPixelScaleTag );
        resx = modelPixelScaleTag.getAsDouble( 0 );
        resy = modelPixelScaleTag.getAsDouble( 1 );
        TIFFField modelTiepointTag = tifDir.getField( GeoTiffTag.ModelTiepointTag );

        double val1 = 0.0;
        val1 = modelTiepointTag.getAsDouble( 0 );
        double val2 = 0.0;
        val2 = modelTiepointTag.getAsDouble( 1 );
        double val4 = 0.0;
        val4 = modelTiepointTag.getAsDouble( 3 );
        double val5 = 0.0;
        val5 = modelTiepointTag.getAsDouble( 4 );
        if ( ( resx == 0.0 || resy == 0.0 )
             || ( val1 == 0.0 && val2 == 0.0 && val4 == 0.0 && val5 == 0.0 ) ) {
            throw new Exception( "The image/coverage hasn't a bounding box" );
            // set the geoparams derived by geoTiffTags
        } 
        // upper/left pixel
        double xOrigin = val4 - ( val1 * resx );
        double yOrigin = val5 - ( val2 * resy );
        // lower/right pixel
        double xRight = xOrigin + rop.getWidth() * resx;
        double yBottom = yOrigin - rop.getHeight() * resy;
        LOG.logInfo( "resx: " + resx );
        LOG.logInfo( "resy: " + resy );
        LOG.logInfo( "origin x: " + xOrigin );
        LOG.logInfo( "origin y: " + yOrigin );
        xmin = xOrigin;
        ymin = yBottom;
        xmax = xRight;
        ymax = yOrigin;
        
        LOG.logInfo( "bbox: " + xmin + " " + ymin + " " + xmax + " " + ymax );
    }

    /**
     * description: the following TIFFKeys count as indicator if a TIFF-File carries GeoTIFF
     * information: ModelPixelScaleTag = 33550 (SoftDesk) ModelTransformationTag = 34264 (JPL Carto
     * Group) ModelTiepointTag = 33922 (Intergraph) GeoKeyDirectoryTag = 34735 (SPOT)
     * GeoDoubleParamsTag = 34736 (SPOT) GeoAsciiParamsTag = 34737 (SPOT) implementation status:
     * working
     */
    private boolean isGeoTIFFFormat( RenderedOp rop ) {
        TIFFDirectory tifDir = (TIFFDirectory) rop.getDynamicProperty( "tiff_directory" );
        // definition of a geotiff
        if ( tifDir.getField( GeoTiffTag.ModelPixelScaleTag ) == null
             && tifDir.getField( GeoTiffTag.ModelTransformationTag ) == null
             && tifDir.getField( GeoTiffTag.ModelTiepointTag ) == null
             && tifDir.getField( GeoTiffTag.GeoKeyDirectoryTag ) == null
             && tifDir.getField( GeoTiffTag.GeoDoubleParamsTag ) == null
             && tifDir.getField( GeoTiffTag.GeoAsciiParamsTag ) == null ) {
            return false;
        } 
        
        // is a geotiff and possibly might need to be treated as raw data
        TIFFField bitsPerSample = tifDir.getField( GeoTiffTag.BitsPerSample );

        if ( bitsPerSample != null ) {
            int samples = bitsPerSample.getAsInt( 0 );
            if ( samples == 16 )
                this.bitDepth = new Integer( 16 );
        }

        // check the EPSG number
        TIFFField ff = tifDir.getField( GeoTiffTag.GeoKeyDirectoryTag );
        if ( ff == null )
            return false;
        char[] ch = ff.getAsChars();

        // resulting HashMap, containing the key and the array of values
        HashMap geoKeyDirectoryTag = new HashMap( ff.getCount() / 4 );
        // array of values. size is 4-1.
        int keydirversion, keyrevision, minorrevision, numberofkeys = -99;

        for ( int i = 0; i < ch.length; i = i + 4 ) {
            int[] keys = new int[3];
            keydirversion = ch[i];

            keyrevision = ch[i + 1];
            minorrevision = ch[i + 2];
            numberofkeys = ch[i + 3];
            keys[0] = keyrevision;
            keys[1] = minorrevision;
            keys[2] = numberofkeys;

            geoKeyDirectoryTag.put( new Integer( keydirversion ), keys );
        }

        int[] content = new int[3];
        int key = -99;

        if ( geoKeyDirectoryTag.containsKey( new Integer( GeoTiffKey.GTModelTypeGeoKey ) ) ) {
            content = (int[]) geoKeyDirectoryTag.get( new Integer( GeoTiffKey.GTModelTypeGeoKey ) );

            // TIFFTagLocation
            if ( content[0] == 0 ) {
                // return Value_Offset
                key = content[2];
            } else {
                // TODO other TIFFTagLocation that GeoKeyDirectoryTag
            }
        } else {
            LOG.logError( "Can't check EPSG codes, make sure it is ok!" );
        }
        int epsgNo = Integer.parseInt( crs.substring( crs.indexOf( ':' ) + 1, crs.length() ) );
        if ( epsgNo != key && ( key != 1 ) ) {
            // TODO seem to getting 1 here
            /*
             * LOG.logInfo( "Geotiff EPSG Number doesn't match input, overriding"); 
             * epsgNo = key;
             */}

        return true;
        
    }

    /**
     * @param map
     * 
     * @return
     */
    private static boolean validate( HashMap map ) {
        boolean valid = true;
        if ( ( map.get( "-i" ) == null ) || ( map.get( "-o" ) == null )
             || ( map.get( "-f" ) == null ) || ( map.get( "-h" ) != null ) ) {
            valid = false;
        }
        return valid;
    }

    public void setTargetResolutions( double[] targetRes ) {
        if ( targetRes == null )
            return;

        // target resolutions have to be in descending order
        java.util.Arrays.sort( targetRes );
        this.targetResolutions = new double[targetRes.length];
        for ( int j = ( targetRes.length - 1 ); j >= 0; j-- ) {
            this.targetResolutions[targetRes.length - 1 - j] = targetRes[j];
        }

        setProgressObserver( new ProgressObserver() );
    }

    /**
     * starts the creation of the tiles
     */
    public void createTileImageTree()
                            throws Exception {

        for ( int i = 0; i < targetResolutions.length; i++ ) {
            FeatureCollection fc = FeatureFactory.createFeatureCollection( "fc"
                                                                           + targetResolutions[i],
                                                                           1000 );
            featColl.put( "fc" + targetResolutions[i], fc );
        }

        // need to calculate the lat lon bounding box
        // Lat Lon WGS84 Geographic CRS
        String targetCRS = "EPSG:4326";
        double xminll = xmin;
        double yminll = ymin;
        double xmaxll = xmax;
        double ymaxll = ymax;

        if ( !crs.equalsIgnoreCase( targetCRS ) ) {
            Envelope env = GeometryFactory.createEnvelope( xmin, ymin, xmax, ymax, null );
            IGeoTransformer trans = new GeoTransformer(targetCRS);
            
            try {
                env = trans.transform( env, crs );
                xminll = env.getMin().getX();
                yminll = env.getMin().getY();
                xmaxll = env.getMax().getX();
                ymaxll = env.getMax().getY();
            } catch ( Exception e ) {
                LOG.logError( "Unsupported transformation, update lonLatEnvelope manually!" );
            }
        }

        updateBounds( xminll, yminll, xmaxll, ymaxll, xmin, ymin, xmax, ymax );
        LOG.logInfo( "creating tiles ..." );

        tile( image, xmin, ymin, xmax, ymax, 0 );
        for ( int i = 0; i < targetResolutions.length; i++ ) {
            ShapeFile sh = new ShapeFile( targetDir + "/sh" + targetResolutions[i], "rw" );
            sh.writeShape( (FeatureCollection) featColl.get( "fc" + targetResolutions[i] ) );
            sh.close();
        }
        LOG.logInfo( "100%" );
        LOG.logInfo( "finished" );
    }

    /**
     * the method performes the creation of the tiles and the filling of the quadtree XML-document.
     * the method will be call in a recursion for each defined level (scale).
     */
    private void tile( BufferedImage img, double xmin, double ymin, double xmax, double ymax,
                      int res )
                            throws Exception {
        // break condition
        if ( res >= targetResolutions.length ) {
            return;
        }
        BufferedImage im = null;
        double xmin_ = 0;
        double ymin_ = 0;
        double xmax_ = 0;
        double ymax_ = 0;
        // calculate half of tile width and height to get tile (quarter)
        // coordinates
        double x2 = ( xmax - xmin ) / 2d;
        double y2 = ( ymax - ymin ) / 2d;
        // create the four quarters (tiles) for the submitted image and call
        // this method
        // in a recursion to create the next resolution level
        for ( int i = 0; i < 4; i++ ) {
            switch ( i ) {
            case 0: {
                // tile bounding box
                xmin_ = xmin;
                ymin_ = ymin;
                xmax_ = xmin + x2;
                ymax_ = ymin + y2;
                im = img.getSubimage( 0, img.getHeight() / 2, img.getWidth() / 2,
                                      img.getHeight() / 2 );
                break;
            }
            case 1: {
                // tile bounding box
                xmin_ = xmin + x2;
                ymin_ = ymin;
                xmax_ = xmax;
                ymax_ = ymin + y2;
                im = img.getSubimage( img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2,
                                      img.getHeight() / 2 );
                break;
            }
            case 2: {
                // tile bounding box
                xmin_ = xmin;
                ymin_ = ymin + y2;
                xmax_ = xmin + x2;
                ymax_ = ymax;
                im = img.getSubimage( 0, 0, img.getWidth() / 2, img.getHeight() / 2 );
                break;
            }
            case 3: {
                // tile bounding box
                xmin_ = xmin + x2;
                ymin_ = ymin + y2;
                xmax_ = xmax;
                ymax_ = ymax;
                im = img.getSubimage( img.getWidth() / 2, 0, img.getWidth() / 2,
                                      img.getHeight() / 2 );
                break;
            }
            }
            // calculate the tiles width and height for the current resolution
            int tilex = (int) Math.round( ( xmax_ - xmin_ ) / targetResolutions[res] );
            int tiley = (int) Math.round( ( ymax_ - ymin_ ) / targetResolutions[res] );
            BufferedImage tmp = img.getSubimage( 0, 0, tilex, tiley );
            ParameterBlock pb = new ParameterBlock();
            pb.add( im );
            // Create the AWTImage operation.
            RenderedOp ro = JAI.create( "awtImage", pb );
            pb = new ParameterBlock();
            pb.addSource( ro );
            pb.add( ( (float) tilex ) / im.getWidth() ); // The xScale
            pb.add( ( (float) tiley ) / im.getHeight() ); // The yScale
            pb.add( 0.0F ); // The x translation
            pb.add( 0.0F ); // The y translation
            pb.add( interpolation ); // The interpolation
            pb.add( image );
            // Create the scale operation
            ro = JAI.create( "scale", pb, null );

            tmp = new BufferedImage( img.getColorModel(),
                                     tmp.getRaster().createCompatibleWritableRaster(), true,
                                     new Hashtable() );
            Graphics g = tmp.getGraphics();
            g.drawImage( im, 0, 0, tilex, tiley, null );
            g.dispose();

            // observer output
            progressObserver.write( new Integer( count ) );
            // save tile to the filesystem
            saveTile( targetDir + "/l" + targetResolutions[res], xmin_, ymin_, tmp );
            createWorldFile( targetDir + "/l" + targetResolutions[res], xmin_, ymin_, xmax_, ymax_,
                             tilex, tiley );
            //storeEnvelope( targetDir + "/l" + targetResolutions[res], targetResolutions[res], 
            // xmin_, ymin_, xmax_, ymax_ );
            storeEnvelope( "l" + targetResolutions[res], targetResolutions[res], xmin_, ymin_,
                           xmax_, ymax_ );
            // recursion !
            tile( im, xmin_, ymin_, xmax_, ymax_, res + 1 );
        }
    }

    /**
     * @param string
     * @param d
     * @param xmin_
     * @param ymin_
     * @param xmax_
     * @param ymax_
     */
    private void storeEnvelope( String dir, double res, double xmin, double ymin, double xmax,
                               double ymax ) {
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
        FeatureType ftype = FeatureFactory.createFeatureType( new QualifiedName( "tiles" ), false,
                                                              ftp );
        try {
            Envelope env = GeometryFactory.createEnvelope( xmin, ymin, xmax, ymax, null );

            Geometry geom = GeometryFactory.createSurface( env, null );
            FeatureProperty[] props = new FeatureProperty[3];
            props[0] = FeatureFactory.createFeatureProperty( "GEOM", geom );
            DecimalFormat fo = new DecimalFormat( "#.0" );
            String sx = fo.format( xmin * 1000 );
            sx = sx.substring( 0, sx.length() - 3 ) + "0";
            String sy = fo.format( ymin * 1000 );
            sy = sy.substring( 0, sy.length() - 3 ) + "0";
            String file = "";
            if ( targetFormat.equalsIgnoreCase( "geotiff" ) )
                file = sx + "_" + sy + ".tif";
            else
                file = sx + "_" + sy + "." + targetFormat;
            props[1] = FeatureFactory.createFeatureProperty(
                                                             GridCoverageExchangeIm.SHAPE_IMAGE_FILENAME,
                                                             file );
            props[2] = FeatureFactory.createFeatureProperty( GridCoverageExchangeIm.SHAPE_DIR_NAME,
                                                             dir );
            Feature feat = FeatureFactory.createFeature( "file", ftype, props );
            FeatureCollection fc = (FeatureCollection) featColl.get( "fc" + res );
            fc.add( feat );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param string
     * @param xmin_
     * @param ymin_
     * @param xmax_
     * @param ymax_
     * @param tilex
     * @param tiley
     */
    private void createWorldFile( String dir, double xmin_, double ymin_, double xmax_,
                                 double ymax_, double tilex, double tiley )
                            throws Exception {
        DecimalFormat fo = new DecimalFormat( "#.0" );
        String sx = fo.format( xmin_ * 1000 );
        sx = sx.substring( 0, sx.length() - 3 ) + "0";
        String sy = fo.format( ymin_ * 1000 );
        sy = sy.substring( 0, sy.length() - 3 ) + "0";
        String file = dir + "/" + sx + "_" + sy + ".wld";
        FileWriter fos = new FileWriter( file );
        fos.write( ( xmax_ - xmin_ ) / tilex + "\n" );
        fos.write( "0\n" );
        fos.write( "0\n" );
        fos.write( ( ymin_ - ymax_ ) / tiley + "\n" );
        fos.write( xmin_ + "\n" );
        fos.write( ymax_ + "\n" );
        fos.close();

    }

    /**
     * stores one image (tile) in the desired format to the desired target directory.
     */
    private String saveTile( String dir, double x, double y, BufferedImage img )
                            throws Exception {
        DecimalFormat fo = new DecimalFormat( "#.0" );
        String sx = fo.format( x * 1000 );
        sx = sx.substring( 0, sx.length() - 3 ) + "0";
        String sy = fo.format( y * 1000 );
        sy = sy.substring( 0, sy.length() - 3 ) + "0";
        count++;
        // String file = dir + "/tile_" + (startIndex++) + "." + targetFormat;
        // String filename = dir + "/" + sx + "_" + sy + "." + targetFormat;
        String filename = "";
        if ( targetFormat.equalsIgnoreCase( "geotiff" ) )
            filename = dir + "/" + sx + "_" + sy + ".tif";
        else
            filename = dir + "/" + sx + "_" + sy + "." + targetFormat;
        File file = new File( filename );
        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream( file );
        if ( targetFormat.equalsIgnoreCase( "bmp" ) ) {
            Encoders.encodeBmp( fos, img );
        } else if ( targetFormat.equalsIgnoreCase( "gif" ) ) {
            Encoders.encodeGif( fos, img );
        } else if ( targetFormat.equalsIgnoreCase( "png" ) ) {
            Encoders.encodePng( fos, img );
        } else if ( targetFormat.equalsIgnoreCase( "tiff" )
                    || targetFormat.equalsIgnoreCase( "tif" )
                    || targetFormat.equalsIgnoreCase( "geotiff" ) ) {
            Encoders.encodeTiff( fos, img );
        } else if ( targetFormat.equalsIgnoreCase( "jpg" )
                    || targetFormat.equalsIgnoreCase( "jpeg" ) ) {
            Encoders.encodeJpeg( fos, img, quality );
        }
        fos.close();
        return file.toString();
    }

    /**
     * adds a new grid coverage layer to a WCS
     */
    private void updateBounds( double llminx, double llminy, double llmaxx, double llmaxy,
                              double minx, double miny, double maxx, double maxy ) {
        /*
         * // check to see if we need to update global variables if (AutoTiler.MIN_X_LL == -1 ||
         * llminx < AutoTiler.MIN_X_LL) AutoTiler.MIN_X_LL = llminx; if (AutoTiler.MIN_Y_LL == -1 ||
         * llminy < AutoTiler.MIN_Y_LL) AutoTiler.MIN_Y_LL = llminy; if (llmaxx >
         * AutoTiler.MAX_X_LL) AutoTiler.MAX_X_LL = llmaxx; if (llmaxy > AutoTiler.MAX_Y_LL)
         * AutoTiler.MAX_Y_LL = llmaxy; if (AutoTiler.MIN_X_SPATIAL == -1 ||minx <
         * AutoTiler.MIN_X_SPATIAL) AutoTiler.MIN_X_SPATIAL = minx; if (AutoTiler.MIN_Y_SPATIAL ==
         * -1 ||miny < AutoTiler.MIN_Y_SPATIAL) AutoTiler.MIN_Y_SPATIAL = miny; if (maxx >
         * AutoTiler.MAX_X_SPATIAL) AutoTiler.MAX_X_SPATIAL = maxx; if (maxy >
         * AutoTiler.MAX_Y_SPATIAL) AutoTiler.MAX_Y_SPATIAL = maxy; // update the ordinates
         * AutoTiler.WCS_ORDINATE_HIGH_X += width; AutoTiler.WCS_ORDINATE_HIGH_Y += height;
         */
        AutoTiler.MIN_X_LL = llminx;
        AutoTiler.MIN_Y_LL = llminy;
        AutoTiler.MAX_X_LL = llmaxx;
        AutoTiler.MAX_Y_LL = llmaxy;
        AutoTiler.MIN_X_SPATIAL = minx;
        AutoTiler.MIN_Y_SPATIAL = miny;
        AutoTiler.MAX_X_SPATIAL = maxx;
        AutoTiler.MAX_Y_SPATIAL = maxy;

    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main( String[] args ) {
        HashMap map = new HashMap();
        for ( int i = 0; i < args.length; i += 2 ) {
            map.put( args[i], args[i + 1] );
        }
        if ( !validate( map ) ) {
            printHelp();
            System.exit( 1 );
        }

        String inDir = ( (String) map.get( "-i" ) ).trim();
        if ( !inDir.endsWith( "/" ) ) {
            inDir = inDir + "/";
        }

        if ( map.get( "-c" ) != null ) {
            createDescFile( (String) map.get( "-c" ), inDir );
            System.exit( 0 );
        }
        String outDir = ( (String) map.get( "-o" ) ).trim();
        if ( !outDir.endsWith( "/" ) ) {
            outDir = outDir + "/";
        }
        String format = ( (String) map.get( "-f" ) ).toUpperCase();

        double[] targetRes = null;
        if ( map.get( "-a" ) == null ) {
            try {
                StringTokenizer st = new StringTokenizer( (String) map.get( "-r" ), ",; " );
                targetRes = new double[st.countTokens()];
                // TODO search all tiff files in directory and
                // obtain lowest resolution, at the moment assume
                // all the tiff files are the same resolution
                for ( int i = 0; i < targetRes.length; i++ ) {
                    double v = Double.parseDouble( st.nextToken() );
                    targetRes[i] = v;
                }
            } catch ( Exception e ) {
                LOG.logError( "Can't parse target resolutions!", e );
                printHelp();
                System.exit( 1 );
            }
        }
        float quality = 1.0f;
        try {
            quality = Float.parseFloat( (String) map.get( "-q" ) );
            if ( quality > 1 ) {
                quality = 1.0f;
            } else if ( quality < 0.1 ) {
                quality = 0.1f;
            }
        } catch ( Exception ex ) {
        }
        String crs = (String) map.get( "-k" );
        if ( crs == null ) {
            crs = "EPSG:4326";
        }

        String antialising = (String) map.get( "-antialising" );

        try {
            File file = new File( inDir );
            String[] list = file.list( new DFileFilter() );
            IDescReader reader = null;

            if ( map.get( "-qt" ) == null )
                reader = new CommandLineReader();
            else
                reader = new TextFileReader( (String) map.get( "-qt" ) );

            for ( int i = 0; i < list.length; i++ ) {
                int pos = list[i].lastIndexOf( '.' );
                String oDir = outDir + list[i].substring( 0, pos );
                String inFile = inDir + list[i];

                String desc = reader.getDesc( list[i] );
                String keywds = reader.getKeywords( list[i] );

                AutoTiler tiler = new AutoTiler( inFile, oDir, format, targetRes, 
                                                 quality, crs, antialising );
                tiler.description = desc;
                tiler.keywords = keywds;

                LOG.logInfo( "output directory: " + outDir );

                if ( ( map.get( "-a" ) != null ) && targetRes == null ) {
                    // automatically calculate the target resolutions
                    if ( tiler.geoTiff ) {
                        // get the modelPixelScaleTag
                        TIFFDirectory tifDir = (TIFFDirectory) tiler.rop.getDynamicProperty( "tiff_directory" );
                        TIFFField modelPixelScaleTag = tifDir.getField( GeoTiffTag.ModelPixelScaleTag );
                        double resX = modelPixelScaleTag.getAsDouble( 0 );
                        double resY = modelPixelScaleTag.getAsDouble( 1 );
                        double groundRes = ( resX + resY ) / 2.0;
                        // get the number of levels
                        int levels = Integer.parseInt( (String) map.get( "-a" ) );
                        targetRes = new double[levels];
                        for ( int j = 0; j < levels; j++ ) {
                            targetRes[j] = groundRes * Math.pow( 2, j );
                        }
                        tiler.setTargetResolutions( targetRes );
                    } else {
                        double groundRes = ( tiler.resx + tiler.resy ) / 2.0;
                        int levels = Integer.parseInt( (String) map.get( "-a" ) );
                        targetRes = new double[levels];
                        for ( int j = 0; j < levels; j++ ) {
                            targetRes[j] = groundRes * Math.pow( 2, j );
                        }
                        tiler.setTargetResolutions( targetRes );
                    }
                }
                tiler.createTileImageTree();
                tiler.createConfigurationFile();
                // update capabilities XML
                String path = (String) map.get( "-u" );
                if ( path != null ) {
                    File capabFile = new File( path );
                    if ( capabFile.isFile() ) {
                        tiler.updateCapabilitiesFile( capabFile );
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // force reload of Deegree context

    }

    /**
     * @param string
     */
    private static void createDescFile( String filename, String inputDir ) {
        LOG.logInfo( "Creating description file " + filename );

        try {
            PrintWriter writer = new PrintWriter( new FileWriter( new File( filename ) ) );
            writer.println( "data,description,keywords (semi-colon separated)" );

            File file = new File( inputDir );
            String[] list = file.list( new DFileFilter() );
            for ( int i = 0; i < list.length; i++ ) {
                writer.println( list[i] + "," );
            }

            writer.close();
            writer = null;
        } catch ( IOException e ) {
            LOG.logError( "Unable to create new description file : " + filename, e );
        }

    }

    /**
     * 
     */
    private void updateCapabilitiesFile( File capabilitiesFile ) {
        InputStream inputXSL = AutoTiler.class.getResourceAsStream( AutoTiler.CAPABS_XSL );
        if ( inputXSL == null ) {
            LOG.logError( "Unable to make capabilities file" );
            System.exit( 1 );
        }

        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer( new StreamSource( inputXSL ) );

            transformer.setParameter( "dataDirectory", targetDir );
            transformer.setParameter( "configFile",
                                      ( new File( configurationFilename ) ).toURL().toString() );
            transformer.setParameter( "name", name );
            transformer.setParameter( "label", name );
            transformer.setParameter( "upperleftll", String.valueOf( AutoTiler.MIN_X_LL ) + ","
                                                     + String.valueOf( AutoTiler.MIN_Y_LL ) );
            transformer.setParameter( "lowerrightll", String.valueOf( AutoTiler.MAX_X_LL ) + ","
                                                      + String.valueOf( AutoTiler.MAX_Y_LL ) );
            transformer.setParameter( "description", description );
            transformer.setParameter( "keywords", keywords );
            BufferedReader reader = new BufferedReader( new FileReader( capabilitiesFile ) );
            String inputStr = new String();
            String tempStr = new String();
            while ( ( tempStr = reader.readLine() ) != null ) {
                inputStr = inputStr.concat( tempStr );
            }

            StringReader input = new StringReader( inputStr );
            StringWriter result = new StringWriter();
            transformer.transform( new StreamSource( input ), new StreamResult( result ) );

            // write the result
            FileOutputStream fos = new FileOutputStream( capabilitiesFile );
            fos.write( result.toString().getBytes( CharsetUtils.getSystemCharset() ) );
            fos.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void createConfigurationFile() {
        URL configURL = AutoTiler.class.getResource( AutoTiler.CONFIG_TEMPLATE );
        URL configXSL = AutoTiler.class.getResource( AutoTiler.CONFIG_XSL );
        if ( configURL == null || configXSL == null ) {
            LOG.logError( "Unable to make configuration file" );
            System.exit( 1 );
        } else {
            // copy this file to the target directory
            try {
                name = imageName.substring( 0, imageName.indexOf( "." ) );

                String resolutions = "";
                java.util.Arrays.sort( targetResolutions );
                int length = targetResolutions.length;

                for ( int i = 0; i < length; i++ ) {
                    resolutions += String.valueOf( targetResolutions[length - 1 - i] );
                    if ( i < ( length - 1 ) )
                        resolutions += ",";
                }

                try {
                    Map param = new HashMap();
                    param.put( "upperleftll", String.valueOf( AutoTiler.MIN_X_LL ) + ","
                                              + String.valueOf( MIN_Y_LL ) );
                    param.put( "lowerrightll", String.valueOf( AutoTiler.MAX_X_LL ) + ","
                                               + String.valueOf( MAX_Y_LL ) );
                    param.put( "upperleft", String.valueOf( AutoTiler.MIN_X_SPATIAL ) + ","
                                            + String.valueOf( MIN_Y_SPATIAL ) );
                    param.put( "lowerright", String.valueOf( AutoTiler.MAX_X_SPATIAL ) + ","
                                             + String.valueOf( MAX_Y_SPATIAL ) );
                    param.put( "dataDir", targetDir );
                    param.put( "label", name );
                    param.put( "name", name );
                    param.put( "description", description );
                    param.put( "keywords", keywords );
                    param.put( "resolutions", resolutions );
                    param.put( "mimeType", getMimeType() );
                    param.put( "srs", crs );

                    Reader reader = new InputStreamReader( configURL.openStream() );

                    XSLTDocument xslt = new XSLTDocument();
                    xslt.load( configXSL );
                    XMLFragment xml = xslt.transform( reader, XMLFragment.DEFAULT_URL, null, param );
                    reader.close();

                    // write the result
                    String dstFilename = "/wcs_" + name + "_configuration.xml";
                    FileOutputStream fos = new FileOutputStream( targetDir + dstFilename );
                    xml.write( fos );
                    fos.close();

                } catch ( Exception e1 ) {
                    e1.printStackTrace();
                }

            } catch ( Exception e ) {
                e.printStackTrace();
                LOG.logError( "Unable to create configuration files", e );
                System.exit( 1 );
            }
        }

    }

    /**
     * 
     */
    private static void printHelp() {
        System.out.println( "ERROR: List of submitted parameters isn't complete." );
        System.out.println();
        System.out.println( "TileImageTree parameters: " );
        System.out.println( "-i: input directory containing the image(s) " );
        System.out.println( "    to be tiled (mandatory)" );
        System.out.println( "-o: output directory path name (mandatory)" );
        System.out.print( "-f: output format (gif, bmp, jpg, png, tif)" );
        System.out.println( " default = jpg; " );
        System.out.println( "    Consider that the target format must have the" );
        System.out.println( "    same or a higher color depth then the input format" );
        System.out.println( "-r comma sperated list of resolutions; e.g. 1.0,0.5,0.25" );
        System.out.println( "    The length of the list is equal to the number of levels" );
        System.out.println( "    that will be generated. The number of level determines the" );
        System.out.println( "    size of the generated tiles because for each level the tiles" );
        System.out.println( "    of the former level will be devided into four quarters." );
        System.out.println( "    The first level will have the first resolution, the second." );
        System.out.println( "    level the second one etc.." );
        System.out.println( "-s: index where the nameing of the tiles start" );
        System.out.println( "     (optional, default = 0)" );
        System.out.println( "-q: qualitiy of the produced tiles (just if output format = jpg)" );
        System.out.println( "     (optional, default = 1 (best))" );
        System.out.println( "-k: coordinate reference system of the map to be tiled" );
        System.out.println( "     (optional, default = EPSG:4326)" );
        System.out.println( "-a: Automatic deployment, calculates target resolutions "
                            + "from Geotiff files, specify no. of levels" );

        System.out.println( "-u: update capabilities XML, pass in path to wcs_capabilities.xml" );
        System.out.println( "-qt: quiet mode, input descriptions are read in from "
                            + "the associated file name \n usage: -q TextFileName" );
        System.out.println( "-c: create a new text file for use with quiet mode" );
    }

    /**
     * default progress observer class. write the progress in % to the console
     */
    public class ProgressObserver {
        private int max = 0;

        /**
         * Creates a new ProgressObserver object.
         */
        public ProgressObserver() {
            if ( targetResolutions != null )
                for ( int i = 0; i < targetResolutions.length; i++ ) {
                    max += (int) Math.pow( 4, ( i + 1 ) );
                }
        }

        /**
         * @param object
         */
        public void write( Object object ) {
            double v = ( (Integer) object ).intValue();
            if ( ( v % 30 ) == 0 ) {
                // System.gc();
                v = (int) Math.round( v / max * 10000d );
                System.out.println( ( v / 100d ) + "%" );
            }
        }
    }

    /**
     * @version $Revision: 1.27 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
     */
    private static class DFileFilter implements FilenameFilter {
        /**
         * @return
         */
        public boolean accept( File f, String name ) {
            int pos = name.lastIndexOf( "." );
            String ext = name.substring( pos + 1 );
            return ext.toUpperCase().equals( "JPG" ) || ext.toUpperCase().equals( "TIFF" )
                   || ext.toUpperCase().equals( "TIF" ) || ext.toUpperCase().equals( "GIF" )
                   || ext.toUpperCase().equals( "PNG" ) || ext.toUpperCase().equals( "BMP" )
                   || ext.toUpperCase().equals( "JPEG" );
        }
    }

    /**
     * @param progressObserver
     *            The progressObserver to set.
     */
    public void setProgressObserver( ProgressObserver progressObserver ) {
        this.progressObserver = progressObserver;
    }

    /**
     * @param mimeType
     *            The mimeType to set.
     */
    public String getMimeType() {
        if ( targetFormat.equalsIgnoreCase( "bmp" ) ) {
            mimeType = "image/bmp";
        } else if ( targetFormat.equalsIgnoreCase( "gif" ) ) {
            mimeType = "image/gif";
        } else if ( targetFormat.equalsIgnoreCase( "png" ) ) {
            mimeType = "image/png";
        } else if ( targetFormat.equalsIgnoreCase( "tiff" )
                    || targetFormat.equalsIgnoreCase( "tif" ) ) {
            // have to check bit depth here
            if ( bitDepth != null )
                mimeType = "image/GeoTIFF";
            else
                mimeType = "image/tiff";
        } else if ( targetFormat.equalsIgnoreCase( "jpg" )
                    || targetFormat.equalsIgnoreCase( "jpeg" ) ) {
            mimeType = "image/jpeg";
        } else if ( targetFormat.equalsIgnoreCase( "geotiff" ) ) {
            mimeType = "image/GeoTIFF";
        }

        // return mimeType;
        return targetFormat;
    }
}

interface IDescReader {

    String getDesc( String filename );

    String getKeywords( String dataName );
}

class CommandLineReader implements IDescReader {
    BufferedReader reader = null;

  
    public String getDesc( String filename ) {
        /*
         * String description = ""; // prompt the user to enter their name System.out.print("Enter a
         * description for the data " + filename + ": ");
         *  // open up standard input BufferedReader br = getBufferedReader();
         * 
         * try { description = br.readLine(); } catch (IOException ioe) { System.out.println("IO
         * error trying to read your description!"); System.exit(1); } return description;
         */
        return "";
    }

    public String getKeywords( String filename ) {
        /*
         * String keywords = ""; System.out.print("Enter a comma separated list of keywords for the
         * data " + filename + ": "); try { BufferedReader br = getBufferedReader(); keywords =
         * br.readLine(); } catch (IOException ioe) { System.out.println("IO error trying to read
         * your keywords!"); System.exit(1); } return keywords;
         */
        return "";
    }

}

class TextFileReader implements IDescReader {

    private String separator = ",";

    private String keywordSeparator = ";";

    private BufferedReader input = null;

    private HashMap map = new HashMap();

    public TextFileReader( String filename ) {
        try {
            input = new BufferedReader( new FileReader( filename ) );

            String line = null;
            // skip the header
            input.readLine();

            while ( ( line = input.readLine() ) != null ) {
                StringTokenizer tokenizer = new StringTokenizer( line, separator );
                String dataname = (String) tokenizer.nextElement();
                String remainder = line.substring( dataname.length() + 1 );
                map.put( dataname, remainder );
            }
            input.close();

        } catch ( FileNotFoundException e ) {
            System.out.println( "Failed to read in descriptions and keywords from file " + filename );
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.tools.raster.IDescReader#getDesc(int)
     */
    public String getDesc( String dataName ) {
        String result = (String) map.get( dataName );
        if ( result != null ) {
            // format is 'description, ...'
            StringTokenizer tokenizer = new StringTokenizer( result, separator );
            return (String) tokenizer.nextElement();
        } 
        return "";
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.tools.raster.IDescReader#getKeywords(int)
     */
    public String getKeywords( String dataName ) {
        String result = (String) map.get( dataName );
        if ( result != null ) {
            // format is 'description, keywords'
            StringTokenizer tokenizer = new StringTokenizer( result, separator );
            tokenizer.nextElement();
            String keywords = (String) tokenizer.nextElement();
            keywords = keywords.replaceAll( keywordSeparator, separator );
            return keywords;
        } 
        return "";
        
    }

    public void finalize() {
        try {
            input.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        input = null;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AutoTiler.java,v $
Revision 1.27  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.26  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.25  2006/09/27 16:46:41  poth
transformation method signature changed

Revision 1.24  2006/08/07 06:55:06  poth
never read variables removed

Revision 1.23  2006/08/07 06:54:39  poth
never read variables removed

Revision 1.22  2006/08/07 06:52:28  poth
unnecessary else blocks removed

Revision 1.21  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
