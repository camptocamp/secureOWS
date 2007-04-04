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
package org.deegree.processing.raster.converter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import org.apache.batik.ext.awt.image.codec.FileCacheSeekableStream;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFDecodeParam;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFImage;

/**
 * Parses 4 channel (32Bit) tiff images as DEM and returns
 * a float matrix containing the DEM heights 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/11/17 08:58:29 $
 *
 * @since 2.0
 */
public class Image2RawData {
    
    private BufferedImage image; 
    private float scale = 0;
    private float offset = 0;
    private int width = 0;
    private int height = 0;
    
    /**
     * 
     * @param dataFile image containing raw data instead color information. 
     *                 Data file must be a TIFF image
     */
    public Image2RawData(File dataFile) {
        this( dataFile, 1, 0 );
    }
    
    /**
     * 
     * @param dataFile image containing raw data instead color information. 
     *                 Data file must be a TIFF image
     * @param scale scale factor; newHeight[i][j] = height[i][j] * scale
     */
    public Image2RawData(File dataFile, float scale) {
        this( dataFile, scale, 0 );
    }
    
    /**
     * 
     * @param dataFile image containing raw data instead color information. 
     *                 Data file must be a TIFF image
     * @param scale scale factor; newHeight[i][j] = height[i][j] * scale
     * @param offset height offset; newHeight[i][j] = height[i][j] + offset
     */
    public Image2RawData(File dataFile, float scale, float offset) {
        try {
            FileCacheSeekableStream fss = new FileCacheSeekableStream( dataFile.toURL().openStream() );
            TIFFImage tiff = new TIFFImage(fss, new TIFFDecodeParam(), 0 );
            image = new BufferedImage( tiff.getColorModel(), (WritableRaster)tiff.getData(), 
                                       false, null );
            width = tiff.getWidth();
            height = tiff.getHeight();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        this.scale = scale;
        this.offset = offset;
    }
    
    /**
     * 
     * @param data image containing raw data instead color information
     */
    public Image2RawData(BufferedImage data) {
        this( data, 1, 0 );
    }
    
    /**
     * 
     * @param data image containing raw data instead color information
     * @param scale scale factor; newHeight[i][j] = height[i][j] * scale
     */
    public Image2RawData(BufferedImage data, float scale) {
        this( data, scale, 0 );
    }
    
    /**
     * 
     * @param data image containing raw data instead color information
     * @param scale scale factor; newHeight[i][j] = height[i][j] * scale
     * @param offset height offset; newHeight[i][j] = height[i][j] + offset
     */
    public Image2RawData(BufferedImage data, float scale, float offset) {
        image = data;
        this.scale = scale;
        this.offset = offset;
        width = data.getWidth();
        height = data.getHeight();
    }
    
    /**
     * returns the DEM heights as float matrix
     * @return
     */
    public float[][] parse() {
       
        float[][] terrain = new float[height][width];    
            
        DataBuffer db = image.getData().getDataBuffer();
        int ps = image.getColorModel().getPixelSize();        
        if ( ps == 8 ) {    
            for ( int j = 0; j < height; j++ ) {
                for ( int i = 0; i < width; i++ ) {                                
                    terrain[j][i] = db.getElem( width * j + i ) * scale + offset;
                }
            }
        } else if ( ps == 16  ) {          
            for ( int j = 0; j < height; j++ ) {
                for ( int i = 0; i < width; i++ ) {                                
                    terrain[j][i] = db.getElemFloat( width * j + i ) * scale + offset;
                }
            }
        } else if ( ps == 24 || ps == 32 ) {
            for ( int j = 0; j < height; j++ ) {
                for ( int i = 0; i < width; i++ ) {
                    int v = image.getRGB( i, j );                    
                    terrain[j][i] = Float.intBitsToFloat( v )  * scale + offset;                    
                }
            }
        }
         
        return terrain;
        
    }
    

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Image2RawData.java,v $
Revision 1.1  2006/11/17 08:58:29  poth
*** empty log message ***

Revision 1.4  2006/11/16 21:02:37  poth
bug fixes - rescaling images

Revision 1.3  2006/11/16 17:11:43  poth
*** empty log message ***

Revision 1.2  2006/11/14 20:53:55  poth
bug fix parsing raw data from image

Revision 1.1  2006/11/14 13:07:53  poth
lnitial checkin

Revision 1.2  2006/10/26 17:04:54  ap
*** empty log message ***

Revision 1.1  2006/10/23 09:01:25  ap
*** empty log message ***


********************************************************************** */