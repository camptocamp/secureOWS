// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/GeoTIFFGridCoverageReader.java,v 1.15 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.model.coverage.grid;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.datatypes.CodeList;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.wcs.configuration.File;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterNotFoundException;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.SeekableStream;

/**
 * GridCoverageReader for reading files as defined by the deegree  * CoverageOffering Extension type 'File'. Known formats are: * tiff, GeoTiff, jpeg, bmp, gif, png and img (IDRISI)  *  * @version $Revision: 1.15 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.15 $, $Date: 2006/11/27 09:07:52 $ *  * @since 2.0
 */
public class GeoTIFFGridCoverageReader extends AbstractGridCoverageReader {

    private static final ILogger LOGGER = LoggerFactory.getLogger(GeoTIFFGridCoverageReader.class);

	private SeekableStream sst = null; 
   
    /**
     * @param source source file of the coverage
     * @param description description of the data contained in the source file
     * @param envelope desired envelope of the coverage to be read 
     * @param format image format of the source file
     */
    public GeoTIFFGridCoverageReader(File source, CoverageOffering description, 
    							   Envelope envelope, Format format) {
        super(source, description, envelope, format );
    }
    
    
    /**
     * @param source
     * @param description description of the data contained in the source file
     * @param envelope desired envelope of the coverage to be read 
     * @param format image format of the source file
     */
    public GeoTIFFGridCoverageReader(InputStream source, CoverageOffering description, 
                                    Envelope envelope, Format format) {
        super(source, description, envelope, format );
    }

    /**
     * Read the grid coverage from the current stream position, and move to the next grid
     * coverage.
     *
     * @param  parameters An optional set of parameters. Should be any or all of the
     *         parameters returned by {@link org.opengis.coverage.grid.Format#getReadParameters}.
     * @return A new {@linkplain GridCoverage grid coverage} from the input source.
     * @throws InvalidParameterNameException if a parameter in <code>parameters</code>
     *         doesn't have a recognized name.
     * @throws InvalidParameterValueException if a parameter in <code>parameters</code>
     *         doesn't have a valid value.
     * @throws ParameterNotFoundException if a parameter was required for the operation but was
     *         not provided in the <code>parameters</code> list.
     * @throws IOException if a read operation failed for some other input/output reason, including
     *         {@link java.io.FileNotFoundException} if no file with the given <code>name</code> can
     *         be found, or {@link javax.imageio.IIOException} if an error was thrown by the
     *         underlying image library.
     */
    public GridCoverage read(GeneralParameterValue[] parameters) throws InvalidParameterNameException, 
                      InvalidParameterValueException, ParameterNotFoundException, IOException {
        
        RenderedOp rop = readGeoTIFF();
        int w = rop.getWidth();
        int h = rop.getHeight();
        
		// get image rectangle of interrest, envelope and lonlatenvelope
		Object[] o = getRasterRegion( w, h );
		Rectangle rect = (Rectangle)o[0];        
		//return null if the result GC would have a width or height of zero
		if ( rect.width == 0 || rect.height == 0 ) {
			return null;
		}
		// create a coverage description that matches the sub image (coverage)
		// for this a new LonLatEnvelope must be set
		CoverageOffering co = (CoverageOffering)description.clone();
		co.setLonLatEnvelope( (LonLatEnvelope)o[2] );
		
		// extract required area from the tiff data
		Raster raster = rop.getData( rect );
  
		GridCoverage gc = createGridCoverage( raster, co, (Envelope)o[1] );
		
		return gc;
    }
    
    /**
     * creates an instance of <tt>GridCoverage</tt> from the passed Raster,
     * CoverageOffering and Envelope. Depending on the transfer type of the 
     * the passed raster different types of GirdCoverages will be created.
     * possilbe transfer types are:
     * <ul>
     * 	<li>DataBuffer.TYPE_BYTE
     *  <li>DataBuffer.TYPE_DOUBLE
     *  <li>DataBuffer.TYPE_FLOAT
     *  <li>DataBuffer.TYPE_INT
     *  <li>DataBuffer.TYPE_SHORT
     *  <li>DataBuffer.TYPE_BYTE
     *  <li>DataBuffer.TYPE_USHORT
     * </ul> 
     * @param raster
     * @param co
     * @param env
     * @return
     * @throws InvalidParameterValueException
     */
    private GridCoverage createGridCoverage(Raster raster, CoverageOffering co, 
    										Envelope env) throws InvalidParameterValueException {

    	GridCoverage gc = null;
		int type = raster.getTransferType();
		switch (type) {
			case DataBuffer.TYPE_BYTE: {         
				gc = createByteGridCoverage(raster, co, env); 
				break;
			}
			case DataBuffer.TYPE_DOUBLE:
			case DataBuffer.TYPE_FLOAT:
			case DataBuffer.TYPE_INT: {
				throw new InvalidParameterValueException("not supported transfertype "+
														 "for geotiff ", "type", type);
			}
			case DataBuffer.TYPE_SHORT: 
			case DataBuffer.TYPE_USHORT: {        
				gc = createShortGridCoverage(raster, co, env);
				break;
			}
			case DataBuffer.TYPE_UNDEFINED: 
			default: 
				throw new InvalidParameterValueException("unkown transfertype for geotiff ",
														 "type", type);
		}
		
		return gc;
    }
    
    /**
     * creates a GridCoverage from the passed Raster. The contains data in 
     * <tt>DataBuffer.TYPE_BYTE</tt> format so the result GridCoverage is of
     * type <tt>ByteGridCoverage </tt>
     * @param raster
     * @param co
     * @param env
     * @return
     */
    private ByteGridCoverage createByteGridCoverage(Raster raster, CoverageOffering co, 
    											Envelope env) {
    	
    	Rectangle rect = raster.getBounds();
		int bands = raster.getNumBands();        
		byte[] data = (byte[])raster.getDataElements( rect.x, rect.y, rect.width, 
													    rect.height, null);        
		byte[][][] mat = new byte[bands][rect.height][rect.width];
		int k = 0;        
		for (int i = 0; i < mat[0].length; i++) {
		    for (int j = 0; j < mat[0][i].length; j++) {
		        for ( int b = 0; b < bands; b++ ){
		            mat[b][i][j] = data[k++];
		        }
		    }
		}
		
		return new ByteGridCoverage( co, env, mat );
    }
    
    /**
     * creates a GridCoverage from the passed Raster. The contains data in 
     * <tt>DataBuffer.TYPE_SHORT</tt> format so the result GridCoverage is of
     * type <tt>ShortGridCoverage </tt>
     * @param raster
     * @param co
     * @param env
     * @return
     */
    private ShortGridCoverage createShortGridCoverage(Raster raster, CoverageOffering co, 
    											     Envelope env) {

    	Rectangle rect = raster.getBounds();
		int bands = raster.getNumBands();        
		short[] data = (short[])raster.getDataElements( rect.x, rect.y, rect.width, 
													    rect.height, null);        
		short[][][] mat = new short[bands][rect.height][rect.width];
		int k = 0;        
		for (int i = 0; i < mat[0].length; i++) {
		    for (int j = 0; j < mat[0][i].length; j++) {
		        for ( int b = 0; b < bands; b++ ){
		            mat[b][i][j] = data[k++];
		        }
		    }
		}
		    
		return new ShortGridCoverage( co, env, mat );
    }
    
    /**
     * reads an image from its source
     * @return
     * @throws IOException
     */
    private RenderedOp readGeoTIFF() throws IOException {
        
    	RenderedOp ro = null;
        if ( source.getClass() == File.class ) {
            String s = ((File)source).getName();
            String tmp = s.toLowerCase();
            URL url = null;
            if ( tmp.startsWith("file:") ) {
                tmp = s.substring( 6, s.length() );
                url = new java.io.File( tmp ).toURL();
            } else if ( tmp.startsWith("http:") ) {
                url = new URL( s );
            } else {
                url = new java.io.File( s ).toURL();
            }            
            sst = new MemoryCacheSeekableStream( url.openStream() );        
            ro = JAI.create( "stream", sst );
        } else {
            sst = new MemoryCacheSeekableStream( (InputStream)source );        
            ro = JAI.create( "stream", sst );
        }
        
        return ro;
    }
    
    /**
     * returns the region of the source image that intersects with the
     * GridCoverage to be created as Rectange as well as the Envelope 
     * of the region in the native CRS and the LonLatEnvelope of this region.   
     * @param width width of the source image
     * @param height height of the source image
     * @return
     */
    private Object[] getRasterRegion(int width, int height) {

    	CodeList[] cl = description.getSupportedCRSs().getNativeSRSs();
        String code = cl[0].getCodes()[0];
        
        LonLatEnvelope lle = description.getLonLatEnvelope(); 
        Envelope tmp = 
        	GeometryFactory.createEnvelope(lle.getMin().getX(), lle.getMin().getY(),
                                           lle.getMax().getX(), lle.getMax().getY(), 
                                           null );	
        try {
        	// transform if native CRS is <> EPSG:4326
        	if ( !(code.equals("EPSG:4326")) ) {
                IGeoTransformer trans = new GeoTransformer( code );
                tmp = trans.transform( tmp, "EPSG:4326" );
        	}
		} catch (Exception e) {
			LOGGER.logError( StringTools.stackTraceToString(e) );
		}
                
		// creat tranform object to calculate raster coordinates from 
		// geo coordinates
        GeoTransform gt = 
            new WorldToScreenTransform( tmp.getMin().getX(), tmp.getMin().getY(),
            							tmp.getMax().getX(), tmp.getMax().getY(),
                                        0, 0, width-1, height-1 );

        // calculate envelope of the part of the grid coverage that is contained
        // within the image        
        Envelope env = envelope.createIntersection( tmp );
                        
        LonLatEnvelope lonLatEnvelope = calcLonLatEnvelope(env, code);        
        // calc image coordinates matching the area that is requested
        int minx = (int)Math.round( gt.getDestX( env.getMin().getX() ) );
        int miny = (int)Math.round( gt.getDestY( env.getMax().getY() ) );
        int maxx = (int)Math.round( gt.getDestX( env.getMax().getX() ) );
        int maxy = (int)Math.round( gt.getDestY( env.getMin().getY() ) );
        Rectangle rect = new Rectangle( minx, miny, maxx-minx, maxy-miny );

        return new Object[] { rect, env, lonLatEnvelope };
    }
       

    /**
     * Allows any resources held by this object to be released. The result 
     * of calling any other method subsequent to a call to this method is 
     * undefined. It is important for applications to call this method when 
     * they know they will no longer be using this <code>GridCoverageReader</code>.
     * Otherwise, the reader may continue to hold on to resources indefinitely.
     *
     * @throws IOException if an error occured while disposing resources 
     *          (for example while closing a file).
     */
    public void dispose() throws IOException {
        if ( sst != null ) {
            sst.close();
        }
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GeoTIFFGridCoverageReader.java,v $
   Revision 1.15  2006/11/27 09:07:52  poth
   JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

   Revision 1.14  2006/11/07 20:03:26  poth
   bug fix reading GeoTiff

   Revision 1.13  2006/09/27 16:46:41  poth
   transformation method signature changed

   Revision 1.12  2006/08/15 12:06:26  poth
   bug fix - reading file

   Revision 1.11  2006/05/03 20:09:52  poth
   *** empty log message ***

   Revision 1.10  2006/05/01 20:15:27  poth
   *** empty log message ***

   Revision 1.9  2006/04/06 20:25:26  poth
   *** empty log message ***

   Revision 1.8  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.7  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.6  2006/03/15 22:20:09  poth
   *** empty log message ***

   Revision 1.5  2006/02/23 07:45:24  poth
   *** empty log message ***

   Revision 1.4  2005/11/21 14:58:25  deshmukh
   CRS to SRS

   Revision 1.3  2005/09/27 19:53:18  poth
   no message

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.2  2004/08/24 07:31:33  ap
   no message

   Revision 1.1  2004/08/23 07:00:16  ap
   no message


********************************************************************** */
