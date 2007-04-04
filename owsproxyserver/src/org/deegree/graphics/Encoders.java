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
package org.deegree.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import Acme.JPM.Encoders.GifEncoder;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;


/**
 * This class offers three methods to encode a <tt>BuffererImage</tt> to
 * a gif-, tif, bmp, jpeg- or png-image.
 *
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/07/11 09:37:16 $
 * 
 * @deprecated use @see org.deegree.ogcwebservices.wpvs.util.ImageUtil instead;
 *                  this class will be removed end of 2007
 *
 * @since 2.0
 */
public final class Encoders {
    /**
     *
     *
     * @param out 
     * @param img 
     *
     * @throws IOException 
     */
    public static synchronized void encodeGif( OutputStream out, BufferedImage img )
                                       throws IOException {
        GifEncoder encoder = new GifEncoder( img, out );
        encoder.encode();
    }

    /**
     *
     *
     * @param out 
     * @param img 
     *
     * @throws IOException 
     */
    public static synchronized void encodeBmp( OutputStream out, BufferedImage img )
                                       throws IOException {
        BMPEncodeParam encodeParam = new BMPEncodeParam();

        com.sun.media.jai.codec.ImageEncoder encoder = ImageCodec.createImageEncoder( "BMP", out, 
                                                                                      encodeParam );

        encoder.encode( img );
    }

    /**
     *
     *
     * @param out 
     * @param img 
     *
     * @throws IOException 
     */
    public static synchronized void encodePng( OutputStream out, BufferedImage img )
                                       throws IOException {
        PNGEncodeParam encodeParam = PNGEncodeParam.getDefaultEncodeParam( img );
        
        if ( encodeParam instanceof PNGEncodeParam.Palette ) {
            PNGEncodeParam.Palette p = (PNGEncodeParam.Palette)encodeParam;       
            byte[] b = new byte[]{-127};        
            p.setPaletteTransparency(b);
        }
                
        com.sun.media.jai.codec.ImageEncoder encoder = 
            ImageCodec.createImageEncoder("PNG", out, encodeParam);
        encoder.encode( img.getData(), img.getColorModel() );
    }

    /**
     *
     *
     * @param out 
     * @param img 
     *
     * @throws IOException 
     */
    public static synchronized void encodeTiff( OutputStream out, BufferedImage img )
                                        throws IOException {
        TIFFEncodeParam encodeParam = new TIFFEncodeParam();

        com.sun.media.jai.codec.ImageEncoder encoder = ImageCodec.createImageEncoder( "TIFF", out, 
                                                                                      encodeParam );

        encoder.encode( img );        
    }

    /**
     *
     *
     * @param out 
     * @param img 
     *
     * @throws IOException 
     */
    public static synchronized void encodeJpeg( OutputStream out, BufferedImage img )
                                        throws IOException {

        // encode JPEG
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( out );
        com.sun.image.codec.jpeg.JPEGEncodeParam jpegParams = encoder.getDefaultJPEGEncodeParam( 
                                                                      img );
        jpegParams.setQuality( 0.95f, false );
        encoder.setJPEGEncodeParam( jpegParams );

        encoder.encode( img );
    }

    /**
     *
     *
     * @param out 
     * @param img 
     * @param quality 
     *
     * @throws IOException 
     */
    public static synchronized void encodeJpeg( OutputStream out, BufferedImage img, float quality )
                                        throws IOException {

        // encode JPEG
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( out );
        com.sun.image.codec.jpeg.JPEGEncodeParam jpegParams = encoder.getDefaultJPEGEncodeParam( 
                                                                      img );
        jpegParams.setQuality( quality, false );
        encoder.setJPEGEncodeParam( jpegParams );

        encoder.encode( img );
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Encoders.java,v $
Revision 1.5  2006/07/11 09:37:16  poth
marked class as deprecated


********************************************************************** */