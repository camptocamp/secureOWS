// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/util/ImageUtils.java,v 1.9 2006/10/17 14:59:02 poth Exp $
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
package org.deegree.framework.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.batik.ext.awt.image.codec.ImageDecoderImpl;
import org.apache.batik.ext.awt.image.codec.PNGDecodeParam;
import org.apache.batik.ext.awt.image.codec.PNGImageDecoder;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFDecodeParam;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFImage;
import org.deegree.graphics.Encoders;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.SeekableStream;

/**
 * Some util methods for reading standard images 
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/10/17 14:59:02 $
 *
 * @since 2.0
 */
public class ImageUtils {

    /**
     * reads an image from the passed <tt>URL</tt> using JAI mechanism
     * @param url address of the image
     *
     * @return read image
     *
     * @throws IOException 
     */
    public static BufferedImage loadImage( URL url )
                            throws IOException {
        InputStream is = url.openStream();
        return loadImage( is );
    }

    /**
     * reads an image from the passed <tt>InputStream</tt> using JAI mechanism
     * @param url address of the image
     *
     * @return read image
     *
     * @throws IOException 
     */
    public static BufferedImage loadImage( InputStream is )
                            throws IOException {
        SeekableStream fss = new MemoryCacheSeekableStream( is );
        RenderedOp ro = JAI.create( "stream", fss );
        BufferedImage img = ro.getAsBufferedImage();
        fss.close();
        is.close();
        return img;
    }

    /**
     * reads an image from the passed file location using JAI mechanism
     *
     * @param fileName 
     *
     * @return read imagey
     *
     * @throws IOException 
     */
    public static BufferedImage loadImage( String fileName )
                            throws IOException {
        return loadImage( new File( fileName ) );
    }

    /**
     * reads an image from the passed file location using JAI mechanism
     *
     * @param fileName 
     *
     * @return read imagey
     *
     * @throws IOException 
     */
    public static BufferedImage loadImage( File file )
                            throws IOException {
        
        BufferedImage img = null;
        String tmp = file.getName().toLowerCase();
        if ( tmp.endsWith( ".tif" ) || tmp.endsWith( ".tiff" ) ) {         
            InputStream is = file.toURL().openStream();
            org.apache.batik.ext.awt.image.codec.SeekableStream fss = 
                new org.apache.batik.ext.awt.image.codec.MemoryCacheSeekableStream( is );
            TIFFImage tiff = new TIFFImage(fss, new TIFFDecodeParam(), 0 );
            img = RenderedOp.wrapRenderedImage( tiff ).getAsBufferedImage();
            fss.close();
        } else if ( tmp.endsWith( ".png" ) ) {
            InputStream is = file.toURL().openStream();
            ImageDecoderImpl dec = new PNGImageDecoder( is, new PNGDecodeParam() ); 
            img = RenderedOp.wrapRenderedImage( dec.decodeAsRenderedImage() ).getAsBufferedImage();
            is.close();
        } else {
            InputStream is = file.toURL().openStream();
            SeekableStream fss = new MemoryCacheSeekableStream( is );
            RenderedOp ro = JAI.create( "stream", fss );
            img = ro.getAsBufferedImage();
            fss.close();
        }

        return img;
    }

    /**
     * stores the passed image in the passed file name with defined quality
     * 
     * @param image
     * @param fileName
     * @param quality just supported for jpeg (0..1)
     * @throws IOException
     */
    public static void saveImage( BufferedImage image, String fileName, float quality )
                            throws IOException {
        File file = new File( fileName );
        saveImage( image, file, quality );
    }

    /**
     * stores the passed image in the passed file with defined quality
     * 
     * @param image
     * @param file
     * @param quality just supported for jpeg (0..1)
     * @throws IOException
     */
    public static void saveImage( BufferedImage image, File file, float quality )
                            throws IOException {
        int pos = file.getName().lastIndexOf( '.' );
        String ext = file.getName().substring( pos + 1, file.getName().length() ).toLowerCase();

        FileOutputStream fos = new FileOutputStream( file );
        saveImage( image, fos, ext, quality );

    }

    /**
     * write an image into the passed output stream. after writing the image the
     * stream will be closed.
     * @param image
     * @param os
     * @param format
     * @param quality
     * @throws IOException
     */
    public static void saveImage( BufferedImage image, OutputStream os, String format, float quality )
                            throws IOException {
        try {

            if ( "jpeg".equals( format ) || "jpg".equals( format ) ) {
                Encoders.encodeJpeg( os, image, quality );
            } else if ( "tif".equals( format ) || "tiff".equals( format ) ) {
                Encoders.encodeTiff( os, image );
            } else if ( "png".equals( format ) ) {
                Encoders.encodePng( os, image );
            } else if ( "gif".equals( format ) ) {
                Encoders.encodeGif( os, image );
            } else if ( "bmp".equals( format ) ) {
                Encoders.encodeBmp( os, image );
            } else {
                throw new IOException( "invalid image format: " + format );
            }
        } catch ( IOException e ) {
            throw e;
        } finally {
            os.close();
        }

    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ImageUtils.java,v $
 Revision 1.9  2006/10/17 14:59:02  poth
 reader for image files enhanced -> support of more tiff- and png formats

 Revision 1.8  2006/07/23 10:06:32  poth
 addtional method for saving images to any outputstream added

 Revision 1.7  2006/07/11 09:35:48  poth
 methods for storing images added

 Revision 1.6  2006/05/08 08:45:47  poth
 *** empty log message ***

 Revision 1.5  2006/04/06 20:25:28  poth
 *** empty log message ***

 Revision 1.4  2006/04/04 20:39:43  poth
 *** empty log message ***

 Revision 1.3  2006/03/30 21:20:27  poth
 *** empty log message ***

 Revision 1.2  2006/01/16 20:36:39  poth
 *** empty log message ***

 Revision 1.1.1.1  2005/01/05 10:38:33  poth
 no message

 Revision 1.3  2004/08/09 06:42:38  ap
 exiting() method add to logger framework

 Revision 1.2  2004/05/25 07:19:13  ap
 no message

 Revision 1.1  2004/05/24 06:48:47  ap
 no message

 Revision 1.1  2004/04/02 06:41:56  poth
 no message



 ********************************************************************** */
