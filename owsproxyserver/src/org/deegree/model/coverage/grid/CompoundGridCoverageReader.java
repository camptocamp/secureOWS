//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/CompoundGridCoverageReader.java,v 1.8 2006/10/12 15:42:37 poth Exp $
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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.model.coverage.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.wcs.configuration.File;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterNotFoundException;

/**
 * This reader enables creation of <tt>GridCoverage</tt>s from more than * one source. This will be used for example for tiled images.  *  * @version $Revision: 1.8 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.8 $, $Date: 2006/10/12 15:42:37 $ *  * @since 2.0
 */

public class CompoundGridCoverageReader extends AbstractGridCoverageReader {

    private static final ILogger LOGGER = LoggerFactory.getLogger(CompoundGridCoverageReader.class);

    
    /**
     * @param source
     * @param description
     * @param envelope
     */
    public CompoundGridCoverageReader(File[] source, CoverageOffering description, 
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
    public GridCoverage read(GeneralParameterValue[] parameters) 
    					throws InvalidParameterNameException, 
							   InvalidParameterValueException, 
							   ParameterNotFoundException, IOException {

        File[] files = (File[])source;        
        List list = new ArrayList(files.length);
                          
        for (int i = 0; i < files.length; i++) {
        	GridCoverageReader gcr = createGridCoverageReader(files[i]);
            GridCoverage gc = gcr.read( parameters );
            if ( gc != null ) {
            	list.add( gc );
            }
        }        
        return createGridCoverage( list );
    }
    
    /**
     * creates a GridCoverage compound from the GridCoverages contained
     * in the passed List. It is assumed that all GridCoverages in the List
     * are of the same type and that the list contains at least one GridCoverage  
     * @param list
     * @return
     */
    private GridCoverage createGridCoverage(List list) {

    	GridCoverage gc = null;
        if ( list != null && list.size() > 0 ) {
            gc = (GridCoverage)list.get(0);    	
        	if ( gc instanceof ImageGridCoverage ) {
        		ImageGridCoverage[] tmp = new ImageGridCoverage[list.size()];
        		tmp = (ImageGridCoverage[])list.toArray(tmp);
                gc = new ImageGridCoverage( description, envelope, tmp);
        	} else if ( gc instanceof ByteGridCoverage ) {
        		ByteGridCoverage[] tmp = new ByteGridCoverage[list.size()];
        		tmp = (ByteGridCoverage[])list.toArray(tmp);
                gc = new ByteGridCoverage( description, envelope, tmp);
        	} else if ( gc instanceof ShortGridCoverage ) {
        		ShortGridCoverage[] tmp = new ShortGridCoverage[list.size()];
        		tmp = (ShortGridCoverage[])list.toArray(tmp);
                gc = new ShortGridCoverage( description, envelope, tmp);
        	} else if ( gc instanceof FloatGridCoverage ) {
        		FloatGridCoverage[] tmp = new FloatGridCoverage[list.size()];
        		tmp = (FloatGridCoverage[])list.toArray(tmp);
                gc = new FloatGridCoverage( description, envelope, tmp);
        	}
        }

    	return gc;
    }
    
    /**
     * creates a GridCoverageReader depending on the native format of the data source
     * @param file
     * @return
     * @throws IOException
     */
    private GridCoverageReader createGridCoverageReader(File file) 
    					throws IOException, InvalidParameterValueException {
    	LOGGER.entering();
    	// calculate and set LonLatBoundingBox for the GC CoverageOffering
        // as source of the returned GridCoverage
        LonLatEnvelope lle = calcLonLatEnvelope( file.getEnvelope(), 
                                                 file.getCrs().getName());
        CoverageOffering desc = (CoverageOffering)description.clone();                
        desc.setLonLatEnvelope( lle );        
        Envelope env = envelope.createIntersection(file.getEnvelope());
        GridCoverageReader gcr = null;
        if ( format.getName().equalsIgnoreCase( "GEOTIFF" ) ) {
            gcr = new GeoTIFFGridCoverageReader( file, desc, env, format );
    	} else if ( isImageFormat(format) ) {            	 
            gcr = new ImageGridCoverageReader( file, desc, env, format );
        } else {            
        	throw new IOException( "not supported file format: " + format.getName() );
        }
        LOGGER.exiting();
        return gcr;
    }
    
    /**
     * returns true if the passed format is an image format
     * @param format
     * @return
     */
    private boolean isImageFormat(Format format) {
        String frmt = format.getName().toUpperCase();
        return frmt.equalsIgnoreCase( "png" ) ||frmt.equalsIgnoreCase( "bmp" ) || 
               frmt.equalsIgnoreCase( "tif" ) || frmt.equalsIgnoreCase( "tiff" ) ||
               frmt.equalsIgnoreCase( "gif" ) || frmt.equalsIgnoreCase( "jpg" ) ||
               frmt.equalsIgnoreCase( "jpeg" ) || frmt.indexOf( "ECW" ) > -1;
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
        
    }
   
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CompoundGridCoverageReader.java,v $
Revision 1.8  2006/10/12 15:42:37  poth
code formating

Revision 1.7  2006/05/18 16:50:04  poth
*** empty log message ***

Revision 1.6  2006/04/06 20:25:26  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.4  2006/03/15 22:20:09  poth
*** empty log message ***

Revision 1.3  2006/01/29 20:59:08  poth
*** empty log message ***

Revision 1.2  2005/01/18 22:08:54  poth
no message

Revision 1.5  2004/08/23 06:59:51  ap
no message

Revision 1.4  2004/08/09 06:43:50  ap
no message

Revision 1.3  2004/08/06 06:41:51  ap
grid coverage implementation extension

Revision 1.2  2004/07/22 15:20:41  ap
no message

Revision 1.1  2004/07/19 06:20:00  ap
no message


********************************************************************** */