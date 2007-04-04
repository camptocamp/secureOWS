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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.graphics.Encoders;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.SeekableStream;


/**
 * utility program to group several image tiles into one large image
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.7 $, $Date: 2006/07/12 14:46:18 $
 * 
 * @since 1.1
 * 
 * @deprecated use @see org.deegree.tools.raster.RasterTreeBuilder instead; this class
 * will be removed from deegree at the end of 2007
 */
public class GlueTiles {
    
    private String rootDir = null;
    private String inputFileFormat = null;
    private String outputFileFormat = null;    
    private double inputXSize = 0;
    private double inputYSize = 0;
    private double outputXSize = 0;
    private double outputYSize = 0;
    private double nX = 0;
    private double nY = 0;
    private String resInd = ""; 
    GeoTransform gti = null;
    
    /**
     * 
     * @param rootDir
     * @param inputFileFormat
     * @param outputFileFormat
     * @param inputXSize
     * @param inputYSize
     * @param outputXSize
     * @param outputYSize
     * @param minx
     * @param miny
     * @param maxx
     * @param maxy
     * @param nX
     * @param nY
     */
    public GlueTiles(String rootDir, String inputFileFormat, String outputFileFormat,
                      int outputXSize, int outputYSize,
                     double minx, double miny, double maxx, double maxy, double nX, 
                     double nY, String resInd) {
                     
        this.rootDir = rootDir;
        this.inputFileFormat = inputFileFormat;
        this.outputFileFormat = outputFileFormat.toLowerCase();
        this.outputXSize = outputXSize;
        this.outputYSize = outputYSize;
        this.nX = nX;
        this.nY = nY;
        gti = new WorldToScreenTransform( minx, miny, maxx, maxy, 0, 0, 
                                          outputXSize-1, outputYSize-1 );
        this.resInd = resInd;
    }
    
    /**
     * walk in a recursion through all directories under the passed one
     * and returns the names/pathes of all contained XML-files
     * 
     * @param dir directory to start from
     * @return
     */
    private List getFiles(String dir) {
        List list = new ArrayList(200);        
        File file = new File( dir );
        String[] lst = file.list(new DFileFilter());
        for (int i = 0; i < lst.length; i++) {
            File fl = new File( dir + '/' + lst[i] );
            if ( fl.isDirectory() ) {                 
                List l = getFiles( dir + '/' + lst[i] );
                list.addAll( l );
            } else {
                list.add( dir + '/' + lst[i] );
            }
        }        
        return list;
    }
    
    /**
     * 
     * @throws Exception
     */
    private void readTiles()throws Exception{
        
        if ( !rootDir.endsWith( "/" ) ) rootDir = rootDir+ "/";
        
        List list = getFiles( rootDir );
        String[] tiles = (String[])list.toArray(new String[list.size()]);
                               
        System.out.println("create IMG");  
        BufferedImage outBi = 
            new BufferedImage( (int)outputXSize, (int)outputYSize, BufferedImage.TYPE_INT_ARGB );        
        Graphics g = outBi.getGraphics();        
        g.setColor( Color.BLUE );
        g.fillRect(0,0,(int)outputXSize, (int)outputYSize);
        
        int t5 = (int)Math.round(tiles.length/100d * 5d);
        int cnt = 0;
        for (int i = 0; i < tiles.length; i++ ) {

            if ( i % t5 == 0) {
                System.out.println(cnt + "%");
                cnt = cnt+5;
            }
            try {
                Thread.sleep(80);
            } catch(Exception e) {
                e.printStackTrace();
            }                    
       
            File fil = new File( tiles[i] );
            if ( !fil.exists() ) continue;
            try {
                double[] inBBox = readWorldFile( tiles[i] );
                
                try {
                    Thread.sleep(20);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                SeekableStream sst = new FileSeekableStream( tiles[i] );
                RenderedOp ro = JAI.create( "stream", sst );     
                BufferedImage bi = ro.getAsBufferedImage();
                inputXSize = bi.getWidth();
                inputYSize = bi.getHeight();                
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(ro);                   // The source image
                pb.add( (float)((outputXSize/nX)/inputXSize) );  // The xScale
                pb.add( (float)((outputYSize/nY)/inputYSize) );  // The yScale 
                pb.add(0.0F);                       // The x translation
                pb.add(0.0F);                       // The y translation
                pb.add(new InterpolationNearest() ); // The interpolation

                // Create the scale operation
                ro = JAI.create("scale", pb, null);
                bi = ro.getAsBufferedImage(); 
                sst.close();
                      
                try {
                    Thread.sleep(20);
                } catch(Exception e) {
                    System.out.println(e);	
                }
             
                int c = (int)Math.round( gti.getDestX( inBBox[0] ) );
                int r = (int)Math.round( gti.getDestY( inBBox[3] ) );
                int c1 = (int)Math.round( gti.getDestX( inBBox[2] ) );
                int r2 = (int)Math.round( gti.getDestY( inBBox[1] ) );
                g.drawImage( bi, c, r, Math.abs(c1-c), Math.abs(r2-r), null );
                                
                bi = null;
            } catch(Exception ee) {
                ee.printStackTrace();
            }
            System.gc();
        }
                    
        FileOutputStream fileOut = new FileOutputStream( rootDir + "out." + outputFileFormat );
        if ( outputFileFormat.equals( "jpg" ) ) {
            Encoders.encodeJpeg(fileOut, outBi);
        } else if ( outputFileFormat.equals( "bmp" ) ) {
            Encoders.encodeBmp(fileOut, outBi);
        } else if ( outputFileFormat.equals( "tif" ) ) {
            Encoders.encodeTiff(fileOut, outBi);
        } else if ( outputFileFormat.equals( "png" ) ) {
            Encoders.encodePng(fileOut, outBi);
        }  else if ( outputFileFormat.equals( "gif" ) ) {
            Encoders.encodeGif(fileOut, outBi);
        } 
        fileOut.close();
        System.gc();
                
    }
    
    /**
     * Gets the latitude and longitude coordinates (xmin, ymin, xmax and ymax) 
     * of the image.
     */
    private double[] readWorldFile( String filename ) throws Exception {
        try {
            // Gets the substring beginning at the specified beginIndex (0) - the
            // beginning index, inclusive - and extends to the character at
            //	index endIndex (position of '.') - the ending index, exclusive.
            String fname = null;
            int pos = filename.lastIndexOf( "." );
            filename = filename.substring( 0, pos );

            //Looks for corresponding worldfiles.       
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
            //	 encoding and the default byte-buffer size are appropriate.
            // The BufferedReader reads text from a character-input stream, buffering characters so as
            //	to provide for the efficient reading of characters
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

            double d5 = d3 + ( inputXSize * d1 );
            double d6 = d4 + ( inputYSize * d2 );

            double[] bbox = new double[4];
            bbox[0] = d3;            
            bbox[1] = d6;
            bbox[2] = d5;
            bbox[3] = d4;
            return bbox;
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return null;
    }

    
    /**
     * class: official version of a FilenameFilter
     */
    private class DFileFilter implements FilenameFilter {
        public String getDescription() {
            return "*."+inputFileFormat;
        }
        
        public boolean accept(File f, String name) {
            int pos = name.lastIndexOf(".");
            String ext = name.substring( pos+1 ); 
            File fl = new File( f.getAbsolutePath() + '/' + name );
            if ( fl.isDirectory() ) {
                System.out.println(f.getAbsolutePath() + '/' + name );          
            }
            return (ext.equalsIgnoreCase(inputFileFormat) && 
                    f.getAbsolutePath().indexOf(resInd) > -1 ) || fl.isDirectory();
        }
    }
    
    public static void main(String args[]) {
        
        if ( args == null || args.length < 12 ) {
            System.out.println("Parameter is missing: ");
            System.out.println("Parameter in order as required: ");
            System.out.println("root directory ");            
            System.out.println("image format of the tiles");
            System.out.println("image format of the result ");
            System.out.println("width of the result in pixel ");
            System.out.println("height of the result in pixel ");
            System.out.println("BBOX minx of the result");
            System.out.println("BBOX miny of the result");
            System.out.println("BBOX maxx of the result");
            System.out.println("BBOX maxy of the result");
            System.out.println("number of tiles in x direction");
            System.out.println("number of tiles in y direction");
            System.out.println("resolution prefix; e.g. l20.0 or l34.3");
            System.exit(1);
        }
        
        GlueTiles l = new GlueTiles(args[0], args[1], args[2], Integer.parseInt(args[3]),
                                    Integer.parseInt(args[4]), Double.parseDouble(args[5]),  
                                    Double.parseDouble(args[6]), Double.parseDouble(args[7]),
                                    Double.parseDouble(args[8]), Integer.parseInt(args[9]),
                                    Integer.parseInt(args[10]), args[11] );
        try{
            l.readTiles();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GlueTiles.java,v $
Revision 1.7  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
