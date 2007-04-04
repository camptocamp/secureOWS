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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.graphics.Encoders;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.io.geotiff.GeoTiffKey;
import org.deegree.io.geotiff.GeoTiffTag;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFField;

/**
 * This class represents a <code>MergeRaster</code> object.
 * TODO more info
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 * 
 * @deprecated use @see org.deegree.tools.raster.RasterTreeBuilder instead; this class
 * will be removed from deegree at the end of 2007
 */
public class MergeRaster {
    
    private static final ILogger LOG = LoggerFactory.getLogger( MergeRaster.class );
	
	private Integer bitDepth = null;
    private boolean geoTiff = false;
	
    // input for new MergeRaste object
	private List imageFiles;
	private String outputDir;
	private String baseName;
    private String outputFormat;
    private double maxTileSize;
    
	// resoultion of current input image 
	private double resx = 0;
    private double resy = 0;
    // bounding box of current input image
    private Envelope envelope;
    
    // minimum resolution of input images
	private double minimumRes;
    
    // combining image bounding box
	private Envelope combiningEnvelope;
	
	// virtual bounding box
	private Envelope virtualEnvelope;
	// size of virtual bounding box in px
	private long pxWidthVirtualBBox;
	private long pxHeightVirtualBBox;
	
	// size of every tile in virtual bounding box in px
	private long pxWidthTile;
	private long pxHeightTile;
	// number of tiles in virtual bounding box
	private int tileRows;
	private int tileCols;

    
    /**
     * TODO write comment
     * 
     * @param imageFiles
     * @param outputDir
     * @param baseName
     * @param outputFormat
     * @param maxTileSize
     * @throws Exception
     */
    public MergeRaster( List imageFiles, String outputDir, String baseName, String outputFormat, 
                        double maxTileSize, Envelope bbox, double resolution ) 
        throws Exception {
        
		this.imageFiles = imageFiles;
		this.outputDir = outputDir;
		this.baseName = baseName;
        this.outputFormat = outputFormat.toLowerCase();
        this.maxTileSize = maxTileSize;
        combiningEnvelope = bbox;
        minimumRes = resolution;
		
        determineCombiningBBox();
        determineVirtualBBox();
        determineTileSize();
        createTiles( tileRows, tileCols );
    }
    
    /**
     * Gets the latitude and longitude coordinates (xmin, ymin, xmax and ymax) of the image.
     */
    private void readWorldFile( String filename, BufferedImage image ) throws Exception {
        try {
            // Gets the substring beginning at the specified beginIndex (0) - the beginning index, 
        	// inclusive - and extends to the character at index endIndex (position of '.') - the 
        	// ending index, exclusive.
        	
            String fname = null;
            int pos = filename.lastIndexOf( "." );
            filename = filename.substring( 0, pos );
            
            // Look for corresponding worldfiles.
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
            // The constructors of this class (FileReader) assume that the default character 
            // encoding and the default byte-buffer size are appropriate.
            // The BufferedReader reads text from a character-input stream, buffering characters 
            // so as to provide for the efficient reading of characters.
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
                switch (cnt) {
	                case 1:
	                	// r�uml. aufl�sung in x richtung (size of 1 pixel in degree or meter)
	                    d1 = Double.parseDouble( s.replace( ',', '.' ) );
	                    break;
	                case 4:
	                	// r�uml. aufl�sung in y richtung (size of 1 pixel in degree or meter)
	                    d2 = Double.parseDouble( s.replace( ',', '.' ) );
	                    break;
	                case 5:
	                	// x - Ecke oben links ( = x-min )
	                    d3 = Double.parseDouble( s.replace( ',', '.' ) );
	                    break;
	                case 6:
	                	// y - Ecke oben links ( = y-max )
	                    d4 = Double.parseDouble( s.replace( ',', '.' ) );
	                    break;
                }
            }
            br.close();
            
            double d5 = d3 + ( image.getWidth() * d1 );
            double d6 = d4 + ( image.getHeight() * d2 );
            double ymax = d4;
            double ymin = d6;
            double xmax = d5;
            double xmin = d3;
            
            resx = ( xmax - xmin ) / image.getWidth();
            resy = ( ymax - ymin ) / image.getHeight();
            
            envelope = GeometryFactory.createEnvelope( xmin, ymin, xmax, ymax, null );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * TODO this is a copy from org.deegree.tools.raster#AutoTiler
     * 
     * Extracts the GeoKeys of the GeoTIFF. The Following Tags will be extracted 
     * (http://www.remotesensing.org/geotiff/spec/geotiffhome.html): 
     * 		ModelPixelScaleTag = 33550 (SoftDesk) 
     * 		ModelTiepointTag = 33922 (Intergraph) 
     * implementation status: working
     */
    private void readBBoxFromGeoTIFF( RenderedOp rop ) throws Exception {
    	
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
        
        if ( ( resx == 0.0 || resy == 0.0 ) || 
        	 ( val1 == 0.0 && val2 == 0.0 && val4 == 0.0 && val5 == 0.0 ) ) {
        	
            throw new Exception( "The image/coverage has no bounding box." );
            // set the geoparams derived by geoTiffTags
            
        } else {
            // upper/left pixel
            double xOrigin = val4 - ( val1 * resx );
            double yOrigin = val5 - ( val2 * resy );
            // lower/right pixel
            double xRight = xOrigin + rop.getWidth() * resx;
            double yBottom = yOrigin - rop.getHeight() * resy;
            
            envelope = GeometryFactory.createEnvelope( xOrigin, yBottom, xRight, yOrigin, null );
        }

    }
    
    /**
     * TODO this is a copy from org.deegree.tools.raster#AutoTiler
     * 
     * the following TIFFKeys count as indicator if a TIFF-File carries GeoTIFF information: 
     * 		ModelPixelScaleTag = 33550 (SoftDesk) 
     * 		ModelTransformationTag = 34264 (JPL Carto Group) 
     * 		ModelTiepointTag = 33922 (Intergraph) 
     * 		GeoKeyDirectoryTag = 34735 (SPOT)
     * 		GeoDoubleParamsTag = 34736 (SPOT) 
     * 		GeoAsciiParamsTag = 34737 (SPOT) 
     * implementation status: working
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
            
        } else {
            // is a geotiff and possibly might need to be treated as raw data
            TIFFField bitsPerSample = tifDir.getField( GeoTiffTag.BitsPerSample );

            if ( bitsPerSample != null ) {
                int samples = bitsPerSample.getAsInt( 0 );
                if ( samples == 16 )
                    bitDepth = new Integer( 16 );
            }

            // check the EPSG number
            TIFFField ff = tifDir.getField( GeoTiffTag.GeoKeyDirectoryTag );
            if ( ff == null ) {
                return false;
            }
            
            char[] ch = ff.getAsChars();

            // resulting HashMap, containing the key and the array of values
            HashMap geoKeyDirectoryTag = new HashMap( ff.getCount() / 4 );
            // array of values. size is 4-1.
            int keydirversion, keyrevision, minorrevision, numberofkeys = -99;

            for (int i = 0; i < ch.length; i = i + 4) {
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

            if ( geoKeyDirectoryTag.containsKey( new Integer( GeoTiffKey.GTModelTypeGeoKey ) ) ) {
                content = 
                	(int[]) geoKeyDirectoryTag.get( new Integer( GeoTiffKey.GTModelTypeGeoKey ) );

                // TIFFTagLocation
                if ( content[0] == 0 ) {
                    // return Value_Offset
                    //key = content[2];
                } else {
                    // TODO other TIFFTagLocation that GeoKeyDirectoryTag
                }
            } else {
                System.out.println("Can't check EPSG codes, make sure it is ok!");
            }            

            return true;
        }
    }
    
    /**
     * TODO this is a copy from org.deegree.tools.raster#AutoTiler
     * 
     * loads the base image
     */
    private BufferedImage loadImage( String imageSource ) throws Exception {
        
        BufferedImage bi = null;
        FileSeekableStream fss = new FileSeekableStream( imageSource );
        RenderedOp rop = JAI.create( "stream", fss );
        
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
                    for (int i = 0; i < bands; i++) {
                        bb[i] = new short[raster.getWidth()* raster.getHeight()];
                    }

                    int c = 0;
                    int u = 0;
                    for (int i = 0; i < raster.getWidth(); i++) {
                        for (int j = 0; j < raster.getHeight(); j++) {
                            for (int z = 0; z < bands; z++) {
                                bb[z][u] = o[c++];
                            }
                            u++;
                        }
                    }

                    // create a new image from the data and serialize it to a file
                    DataBuffer db = new DataBufferShort( bb, width * height );
                    SampleModel sm = 
                    	new BandedSampleModel( DataBuffer.TYPE_SHORT, width, height, bands );
                    raster = Raster.createWritableRaster( sm, db, null );
                    
                    // from a theoretical point of view the usage of TYPE_USHORT_GRAY
                    // doesn't seem to be a very good idea but it works and I didn't find a
                    // way to create a RenderedImage with type Short
                    bi = new BufferedImage( width, height, BufferedImage.TYPE_USHORT_GRAY );
                    bi.setData( raster );
                }
            }
        }

        bi = rop.getAsBufferedImage();
        fss.close();
        
        return bi;
    }
    
    /**
     * Determins the necessary size of a bounding box, which is large enough to hold all 
     * input image files. The result is stored in the combining <code>Envelope</code>.
     * 
     * @throws Exception 
     */
    private void determineCombiningBBox( ) throws Exception {
        
        System.out.println( "calculating overall bounding box ..." );
       
        if ( imageFiles == null || imageFiles.isEmpty() ) {
            throw new Exception( "No combining BoundingBox to be determined: " +
                                 "The list of image files is null or empty." );
        }

        // upper left corner of combining bounding box
        double minX = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE; 
        // lower right corner of combining bounding box
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        // minimum resolution within combining bounding box
        double minResX = Double.MAX_VALUE;
        double minResY = Double.MAX_VALUE;
        
        if ( combiningEnvelope == null ) {
            for ( int i = 0; i < imageFiles.size(); i++ ) {
                
                File file = new File( (String)imageFiles.get(i) );
                if ( file.exists() && !file.isDirectory()) {
                
                    // for faster clean up 
                    System.gc();
                    
                    BufferedImage bi = loadImage( (String)imageFiles.get(i) );                
                    if ( !geoTiff ) {
                        try {
                            readWorldFile( (String)imageFiles.get(i), bi );
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }            
                    // now the values of resx, resy, envelope of the current image file are available
                    
                    // find min for x and y
                    minX = Math.min( minX, envelope.getMin().getX() );
                    minY = Math.min( minY, envelope.getMin().getY() );
                    // find max for x and y
                    maxX = Math.max( maxX, envelope.getMax().getX() );
                    maxY = Math.max( maxY, envelope.getMax().getY() );
                    
                    // find min for resolution of x and y
                    minResX = Math.min( minResX, resx );
                    minResY = Math.min( minResY, resy );
                } else {
                    System.out.println( "File: " + imageFiles.get(i) + " does not exist!" );
                    System.out.println( "Image will be ignored" );
                }
                
            }
            // store minimum resolution        
            minimumRes =  Math.min( minResX, minResY );
            combiningEnvelope = GeometryFactory.createEnvelope( minX, minY, maxX, maxY, null );
         }       

    }

    /**
     * Determins a usefull size for the virtual bounding box. It is somewhat larger 
     * than the combining bounding box. The result is stored in the virtual <code>Envelope</code>.
     * 
     */
    private void determineVirtualBBox() {

        double width = combiningEnvelope.getWidth();
        double height = combiningEnvelope.getHeight();
        
        long pxWidth = (long)Math.round( width / minimumRes );
        long pxHeight = (long)Math.round( height / minimumRes );

        // set width and height to next higher even-numbered thousand
        pxWidth = 2000 * (long)Math.ceil( pxWidth / 2000.0 );
        pxHeight = 2000 * (long)Math.ceil( pxHeight / 2000.0 );
        
        pxWidthVirtualBBox = pxWidth;
        pxHeightVirtualBBox = pxHeight;
        
        // upper left corner of virtual bounding box
        double minX = combiningEnvelope.getMin().getX();
        double maxY = combiningEnvelope.getMax().getY();
        
        // lower right corner of virtual bounding box
        double maxX = minX + ( pxWidth * minimumRes );
        double minY = maxY - ( pxHeight * minimumRes );
        
        virtualEnvelope = GeometryFactory.createEnvelope( minX, minY, maxX, maxY, null );
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
            pxWidthTile = pxWidthVirtualBBox / tileCols ;
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
            pxHeightTile = pxHeightVirtualBBox / tileRows ;
        }
        
        this.tileCols = tileCols;
        this.tileRows = tileRows;
        
        System.out.println( "minimum resolution: " + minimumRes );
        System.out.println( "width = "+ pxWidthVirtualBBox +" *** height = " + pxHeightVirtualBBox );
        System.out.println( "pxWidthTile = " + pxWidthTile + " *** pxHeightTile = " + pxHeightTile );
        System.out.println( "number of tiles: horizontally = " + tileCols + ", vertically = " + tileRows );
    }

    /**
     * Creates one <code>Tile</code> object after the other, with the number of tiles being 
     * specified by the given number of <code>rows</code> and <code>cols</code>. 
     * 
     * Each Tile gets written to the FileOutputStream by the internal call to #paintImagesOnTile.
     *  
     * @param rows
     * @param cols
     * @throws Exception 
     */
    private void createTiles( int rows, int cols ) throws Exception {
        
        Tile tile = null;
        
        double tileWidth = virtualEnvelope.getWidth() / cols; // [�]
        double tileHeight = virtualEnvelope.getHeight() / rows; // [�]
        
        double leftX;
        double upperY = virtualEnvelope.getMax().getY();
        double rightX;
        double lowerY;
        
        for ( int i = 0; i < rows; i++ ) {
            
            leftX = virtualEnvelope.getMin().getX();
            lowerY = upperY - tileHeight;
            
            for ( int j = 0; j < cols; j++ ) {
                
                System.out.println( "processing tile: " + i + " - " + j);
                rightX = leftX + tileWidth;
                Envelope env = 
                    GeometryFactory.createEnvelope( leftX, lowerY, rightX, upperY, null );
//                System.out.print(" (" + leftX + "," + lowerY + " / " + rightX + "," + upperY + ") ");
                leftX = rightX;
                
                String postfix = "_" + i + "_" + j;
                tile = new Tile( env, postfix );
                
                paintImagesOnTile( tile );
                createWorldFile( tile );
            }
            
//            System.out.println();           
            upperY = lowerY;
        }
    }

    /**
     * Paints all image files that intersect with the passed <code>tile</code> onto that tile 
     * and creates an output file in the <code>outputDir</code>. If no image file intersects with 
     * the given tile, then an empty output file is created. The name of the output file is defined 
     * by the <code>baseName</code> and the tile's index of row and column.
     * 
     * @param tile  The tile on which to paint the image.
     * @throws Exception 
     */
    private void paintImagesOnTile( Tile tile ) throws Exception {
        
        System.out.println( "creating merged image ...");
        
        Envelope tileEnv = tile.getTileEnvelope();
        String postfix = tile.getPostfix();
        
        // for png, tif, tiff use 4 byte:
        int type = BufferedImage.TYPE_INT_ARGB;
        
        // for bmp, jpg, jpeg use 3 byte:
        if ( ( "jpg" ).equals( outputFormat ) || ( "jpeg" ).equals( outputFormat ) || 
             ( "bmp" ).equals( outputFormat ) ) {
            
            type = BufferedImage.TYPE_INT_RGB;
        } 

        BufferedImage out = new BufferedImage( (int)pxWidthTile, (int)pxHeightTile, type );;
        
        for ( int i = 0; i < imageFiles.size(); i++ ) {
            
            File file = new File( (String)imageFiles.get(i) );
            if ( file.exists() && !file.isDirectory() ) {
            
                // for faster clean up 
                System.gc();
                
                //System.out.println("processing: "  + imageFiles.get(i));
                
                BufferedImage bi = loadImage( (String)imageFiles.get(i) );
                if ( !geoTiff ) {
                    try {
                        readWorldFile( (String)imageFiles.get(i), bi );
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }            
                // now the values of resx, resy, envelope of the current image file are available
                if ( envelope.intersects( tileEnv ) ) {
                    
                    GeoTransform gt = 
                        new WorldToScreenTransform( tileEnv.getMin().getX(), tileEnv.getMin().getY(), 
                                                    tileEnv.getMax().getX(), tileEnv.getMax().getY(), 
                                                    0, 0, out.getWidth()-1, out.getHeight()-1 );
    
                    int x1 = (int)Math.round( gt.getDestX( envelope.getMin().getX() ) );
                    int y1 = (int)Math.round( gt.getDestY( envelope.getMax().getY() ) );
                    int x2 = (int)Math.round( gt.getDestX( envelope.getMax().getX() ) );
                    int y2 = (int)Math.round( gt.getDestY( envelope.getMin().getY() ) );
    
                    Graphics g = out.getGraphics();
                    g.drawImage( bi, x1, y1, x2-x1, y2-y1 , null );
                    g.dispose();
                }
                
                try {
                    String imageFile = outputDir + baseName + postfix + "." + outputFormat;
                    FileOutputStream fos = new FileOutputStream( imageFile );
                    
                    if ( "png".equals( outputFormat ) ) {
                        Encoders.encodePng( fos, out );
                    } else if ( "bmp".equals( outputFormat ) ) {
                        Encoders.encodeBmp( fos, out );
                    } else if ( "tif".equals( outputFormat ) || "tiff".equals( outputFormat ) ) {
                        Encoders.encodeTiff( fos, out );
                    } else if ( "jpg".equals( outputFormat ) || "jpeg".equals( outputFormat ) ) {
                        Encoders.encodeJpeg( fos, out, 1 );
                    } 
                    fos.close();
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            }
        }
    }

    /**
     * Creates a world file for the corresponding tile in the <code>outputDir</code>. The name of 
     * the output file is defined by the <code>baseName</code> and the tile's index of row and 
     * column.
     * 
     * @param tile  The tile for which to create a world file.
     * @throws IOException
     */
    private void createWorldFile( Tile tile ) throws IOException {

        Envelope env = tile.getTileEnvelope();
        String postfix = tile.getPostfix();
        
        StringBuffer sb = new StringBuffer( 1000 );
        
        sb.append( resx ).append( "\n" )
          .append( 0.0 ).append( "\n" )
          .append( 0.0 ).append( "\n" )
          .append( (-1)*resy ).append( "\n" )
          .append( env.getMin().getX() ).append( "\n" )
          .append( env.getMax().getY() ).append( "\n" );

        File f = new File( outputDir + baseName + postfix + ".wld" );
        
        FileWriter fw = new FileWriter( f );
        PrintWriter pw = new PrintWriter( fw );

        pw.print( sb.toString() );

        pw.close();
        fw.close();
    }
    
    /**
     * Validates the content of <code>map</code>, to see, if necessary arguments were passed 
     * when calling this class.
     * 
     * @param map
     * @throws Exception
     */
    private static void validate( HashMap map ) throws Exception {
        
        if ( map.get( "-mapFiles" ) == null ) {
            throw new Exception( "-mapFiles must be set" );
        }
        if ( map.get( "-outDir" ) == null ) {
            throw new Exception( "-outDir must be set" );
        }
        if ( map.get( "-baseName" ) == null ) {
            map.put( "-baseName", "out" );
        }
        if ( map.get( "-outputFormat" ) == null ) {
            map.put( "-outputFormat", "png" );
        } else {
            String format = ( (String)map.get( "-outputFormat" ) ).toLowerCase();
            if ( !( "bmp" ).equals( format ) && !( "png" ).equals( format ) &&
                 !( "jpg" ).equals( format ) && !( "jpeg" ).equals( format ) && 
                 !( "tif" ).equals( format ) && !( "tiff" ).equals( format ) ) {
                
                throw new Exception( "-outputFormat must be one of the following: " +
                                     "'bmp', 'jpeg', 'jpg', 'png', 'tif', 'tiff'." );
            }
        }
        if ( map.get( "-maxTileSize" ) == null ) {
            map.put( "-maxTileSize", "6000" );
        }
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
    public static void main( String[] args ) throws Exception {
        
        if ( args == null || args.length == 0 ) {
            args = new String[] { "-mapFiles", "D:/temp/europe_DK.jpg," +
                                  "D:/temp/europe_GB.jpg,D:/temp/europe_BeNeLux.jpg", 
                                  "-outDir", "D:/temp/outdir/", "-baseName", "out", 
                                  "-outputFormat", "png", "-maxTileSize", "6000" };
        }
        
        HashMap map = new HashMap();
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
        validate( map );
        LOG.logInfo( map.toString() );
        
        String tmp = (String)map.get( "-mapFiles" );
        String[] mapFiles = StringTools.toArray( tmp, ",;|", true );
        String outDir = (String)map.get( "-outDir" );
        String baseName = (String)map.get( "-baseName" );
        String outputFormat = (String)map.get( "-outputFormat" );
        String bbox = (String)map.get( "-bbox" );
        String res = (String)map.get( "-resolution" );
        double maxTileSize = ( Double.valueOf( (String)map.get( "-maxTileSize" ) ) ).doubleValue();
        
    	List imageFiles = new ArrayList();
        for (int i = 0; i < mapFiles.length; i++) {
            imageFiles.add(mapFiles[i]);
        }
        
        Envelope env = null;
        double resolution = 0;
        if ( bbox != null ) {
            double[] d = StringTools.toArrayDouble( bbox, "," );
            env = GeometryFactory.createEnvelope( d[0], d[1], d[2], d[3], null );
            resolution = Double.parseDouble( res );
        }

    	new MergeRaster( imageFiles, outDir, baseName, outputFormat, maxTileSize,
                          env, resolution );
        
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.17  2006/08/29 19:54:14  poth
footer corrected

Revision 1.16  2006/08/24 06:43:54  poth
File header corrected

Revision 1.15  2006/05/25 20:02:39  poth
classes marked as deprecated

Revision 1.14  2006/04/06 20:25:26  poth
*** empty log message ***

Revision 1.13  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.12  2006/01/29 20:59:08  poth
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
