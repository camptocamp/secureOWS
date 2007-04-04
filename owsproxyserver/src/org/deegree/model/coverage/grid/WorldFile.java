//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/WorldFile.java,v 1.10 2006/11/15 15:46:25 poth Exp $
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
package org.deegree.model.coverage.grid;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * class representation of a ESRI world file
 * 
 *
 * @version $Revision: 1.10 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.10 $, $Date: 2006/11/15 15:46:25 $
 *
 * @since 2.0
 */
public class WorldFile {

    private static ILogger LOG = LoggerFactory.getLogger( WorldFile.class );

    private double resx;

    private double resy;

    private double rotation1;

    private double rotation2;

    private Envelope envelope;

    /**
     * returns a class represention of  a ESRI world file
     * @param filename name of the image/raster file inclusing path and extension 
     * @return
     * @throws Exception
     */
    public static WorldFile readWorldFile( String filename, String type )
                            throws Exception {

        BufferedImage bi = ImageUtils.loadImage( filename );

        return readWorldFile( filename, type, bi );

    }
    
    /**
     * returns a class represention of  a ESRI world file
     * @param filename name of the image/raster file inclusing path and extension
     * @param filename
     * @param type
     * @param width image width in pixel
     * @param height image height in pixel
     * @return
     * @throws Exception
     */
    public static WorldFile readWorldFile( String filename, String type, int width, int height )
                            throws Exception {
        //Gets the substring beginning at the specified beginIndex (0) - the beginning index, 
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
        double d7 = 0;
        double d8 = 0;
        while ( ( s = br.readLine() ) != null ) {
            cnt++;
            s = s.trim();
            switch ( cnt ) {
            case 1:
                // spatial resolution x direction
                d1 = Double.parseDouble( s.replace( ',', '.' ) );
                break;
            case 2:
                // rotation1
                d7 = Double.parseDouble( s.replace( ',', '.' ) );
                break;
            case 3:
                // rotation2
                d8 = Double.parseDouble( s.replace( ',', '.' ) );
                break;
            case 4:
                // spatial resolution y direction
                d2 = Double.parseDouble( s.replace( ',', '.' ) );
                break;
            case 5:
                // minimum x coordinate
                d3 = Double.parseDouble( s.replace( ',', '.' ) );
                break;
            case 6:
                // maximum y coordinate
                d4 = Double.parseDouble( s.replace( ',', '.' ) );
                break;
            }
        }
        br.close();

        double d5 = d3 + ( width * d1 );
        double d6 = d4 + ( height * d2 );
        double resx = Math.abs( d1 );
        double resy = Math.abs( d2 );
        double ymax = d4;
        double ymin = d6;
        double xmax = d5;
        double xmin = d3;
        if ( "center".equals( type ) ) {
            LOG.logDebug( xmin + " " + ymin + " " + xmax + " " + ymax );
            xmin = xmin - resx / 2d;
            ymin = ymin - resy / 2d;
            xmax = xmin + resx * width;
            ymax = ymin + resy * height;
        }

        Envelope envelope = GeometryFactory.createEnvelope( xmin, ymin, xmax, ymax, null );

        return new WorldFile( resx, resy, d7, d8, envelope );
    }

    /**
     * returns a class represention of  a ESRI world file
     * @param filename name of the image/raster file inclusing path and extension
     * @param image image/raster the worldfile belongs too 
     * @return
     * @throws Exception
     */
    public static WorldFile readWorldFile( String filename, String type, BufferedImage image )
                            throws Exception {

        return readWorldFile( filename, type, image.getWidth(), image.getHeight() );
    }
    
    /**
     * writes a WorldFile
     * @param wfs
     * @param fileBaseName
     * @throws IOException
     */
    public static void writeWorldFile( WorldFile wfs, String fileBaseName )
                            throws IOException {

        Envelope env = wfs.envelope;

        StringBuffer sb = new StringBuffer( 200 );

        sb.append( wfs.resx ).append( "\n" ).append( 0.0 ).append( "\n" ).append( 0.0 );
        sb.append( "\n" ).append( ( -1 ) * wfs.resy ).append( "\n" ).append( env.getMin().getX() );
        sb.append( "\n" ).append( env.getMax().getY() ).append( "\n" );

        File f = new File( fileBaseName + ".wld" );

        FileWriter fw = new FileWriter( f );
        PrintWriter pw = new PrintWriter( fw );

        pw.print( sb.toString() );

        pw.close();
        fw.close();
    }
 

    /**
     * 
     * @param resx resolution x-direction
     * @param resy resolution y-direction (negative value)
     * @param rotation1 first rotation parameter
     * @param rotation2 second rotation parameter
     * @param minx minimum x coordinate of the raster
     * @param maxy maximum y coordinate of the raster
     */
    public WorldFile( double resx, double resy, double rotation1, double rotation2,
                     Envelope envelope ) {
        this.resx = resx;
        this.resy = resy;
        this.rotation1 = rotation1;
        this.rotation2 = rotation2;
        this.envelope = envelope;
    }

    /**
     * returns the envelope described by a word file
     * @return
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * returns the x-resolution described by a word file
     * @return
     */
    public double getResx() {
        return resx;
    }

    /**
     * returns the y-resolution described by a word file
     * @return
     */
    public double getResy() {
        return resy;
    }

    /**
     * returns the first rotation described by a word file
     * @return
     */
    public double getRotation1() {
        return rotation1;
    }

    /**
     * returns the second rotation described by a word file
     * @return
     */
    public double getRotation2() {
        return rotation2;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append( "envelope: " ).append( envelope ).append( "\n" );
        sb.append( "resx: " ).append( resx ).append( "\n" );
        sb.append( "resy: " ).append( resy ).append( "\n" );
        sb.append( "rotation1: " ).append( rotation1 ).append( "\n" );
        sb.append( "rotation2: " ).append( rotation2 );
        return sb.toString();
    }

}
/*
 * Changes to this class. What the people haven been up to:
 * $Log: WorldFile.java,v $
 * Revision 1.10  2006/11/15 15:46:25  poth
 * method for writing a worldfile added
 *
 * Revision 1.9  2006/11/06 21:29:38  poth
 * additional read method added
 *
 * Revision 1.8  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.7  2006/07/05 12:57:38  poth
 * comments added / toString method added
 *
 */
