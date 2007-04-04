//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/ImageGridCoverageWriter.java,v 1.6 2006/04/06 20:25:26 poth Exp $
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
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import org.deegree.graphics.Encoders;
import org.opengis.coverage.grid.FileFormatNotCompatibleWithGridCoverageException;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.OperationParameter;
import org.opengis.parameter.ParameterNotFoundException;

/**
 * This class encapsulates functionality for writing a <tt>GridCoverage</tt>
 * as a GeoTIFF to a defined destination. Ths destination will be given as
 * an <tt>OutputStream</tt>. The current implementation is limited to support
 * <tt>ImageGridCoverage</tt>s to be written as GeoTIFF.
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/06 20:25:26 $
 *
 * @since 2.0
 */
public class ImageGridCoverageWriter extends AbstractGridCoverageWriter {
    
    /**
     * @param destination
     * @param metadata
     * @param subNames
     * @param currentSubname
     * @param format
     */
    public ImageGridCoverageWriter(Object destination, Map metadata,
            String[] subNames, String currentSubname, Format format) {
        super(destination, metadata, subNames, currentSubname, format);
    }
    
    /**
     * Writes the specified grid coverage.
     *
     * @param  coverage The {@linkplain GridCoverage grid coverage} to write.
     * @param  parameters An optional set of parameters. Should be any or all of the
     *         parameters returned by {@link org.opengis.coverage.grid.Format#getWriteParameters}.
     * @throws InvalidParameterNameException if a parameter in <code>parameters</code>
     *         doesn't have a recognized name.
     * @throws InvalidParameterValueException if a parameter in <code>parameters</code>
     *         doesn't have a valid value.
     * @throws ParameterNotFoundException if a parameter was required for the operation but was
     *         not provided in the <code>parameters</code> list.
     * @throws FileFormatNotCompatibleWithGridCoverageException if the grid coverage
     *         can't be exported in the 
     *         {@linkplain org.opengis.coverage.grid.GridCoverageWriter#getFormat writer format}.
     * @throws IOException if the export failed for some other input/output reason, including
     *         {@link javax.imageio.IIOException} if an error was thrown by the underlying
     *         image library.
     */
    public void write(GridCoverage coverage, GeneralParameterValue[] parameters)
                      throws InvalidParameterNameException, InvalidParameterValueException, 
                                ParameterNotFoundException, IOException {
        int width = -1;
        int height = -1;
        for (int i = 0; i < parameters.length; i++) {
            OperationParameter op = (OperationParameter)parameters[i].getDescriptor();
            String name = op.getName( Locale.getDefault() );
            if ( name.equalsIgnoreCase( "WIDTH" ) ) {
                Object o = op.getDefaultValue();
                width = ((Integer)o).intValue();
            } else if ( name.equalsIgnoreCase( "HEIGHT" ) ) {
                Object o = op.getDefaultValue();
                height = ((Integer)o).intValue();
            } 
        }
        
        OutputStream out = (OutputStream)destination;
        ImageGridCoverage igc = (ImageGridCoverage)coverage;
        if ( format.getName().equalsIgnoreCase("gif") ) {
            Encoders.encodeGif( out, igc.getAsImage( width, height ) );
        } else if ( format.getName().equalsIgnoreCase("tiff") || 
                format.getName().equalsIgnoreCase("tif") ) {
            Encoders.encodeTiff( out, igc.getAsImage( width, height ) );
        } else if ( format.getName().equalsIgnoreCase("jpeg") || 
                format.getName().equalsIgnoreCase("jpg") ) {
            Encoders.encodeJpeg( out, igc.getAsImage( width, height ) );
        } else if ( format.getName().equalsIgnoreCase("bmp") ) {
            Encoders.encodeBmp( out, igc.getAsImage( width, height ) );
        } else if ( format.getName().equalsIgnoreCase("png") ) {
            Encoders.encodePng( out, igc.getAsImage( width, height ) );
        } else {
            throw new IOException("unsupported output format: " + format.getName() );
        }
    }
    
    /**
     * Writes the specified grid coverage.
     *
     * @param  coverage The {@linkplain GridCoverage grid coverage} to write.
     * @param  parameters An optional set of parameters. Should be any or all of the
     *         parameters returned by {@link org.opengis.coverage.grid.Format#getWriteParameters}.
     * @throws InvalidParameterNameException if a parameter in <code>parameters</code>
     *         doesn't have a recognized name.
     * @throws InvalidParameterValueException if a parameter in <code>parameters</code>
     *         doesn't have a valid value.
     * @throws ParameterNotFoundException if a parameter was required for the operation but was
     *         not provided in the <code>parameters</code> list.
     * @throws FileFormatNotCompatibleWithGridCoverageException if the grid coverage
     *         can't be exported in the 
     *         {@linkplain org.opengis.coverage.grid.GridCoverageWriter#getFormat writer format}.
     * @throws IOException if the export failed for some other input/output reason, including
     *         {@link javax.imageio.IIOException} if an error was thrown by the underlying
     *         image library.
     */
    public void write(GridCoverage coverage, int xAxis, int yAxis,
                      GeneralParameterValue[] parameters) throws InvalidParameterNameException, 
                       InvalidParameterValueException, ParameterNotFoundException, IOException {
        OutputStream out = (OutputStream)destination;
        ImageGridCoverage igc = (ImageGridCoverage)coverage;
        if ( format.getName().equalsIgnoreCase("gif") ) {
            Encoders.encodeGif( out, igc.getAsImage( xAxis, yAxis ) );
        } else if ( format.getName().equalsIgnoreCase("tiff") || 
                format.getName().equalsIgnoreCase("tif") ) {
            Encoders.encodeTiff( out, igc.getAsImage( xAxis, yAxis ) );
        } else if ( format.getName().equalsIgnoreCase("jpeg") || 
                format.getName().equalsIgnoreCase("jpg") ) {
            Encoders.encodeJpeg( out, igc.getAsImage( xAxis, yAxis ) );
        } else if ( format.getName().equalsIgnoreCase("bmp") ) {
            Encoders.encodeBmp( out, igc.getAsImage( xAxis, yAxis ) );
        } else if ( format.getName().equalsIgnoreCase("png") ) {
            Encoders.encodePng( out, igc.getAsImage( xAxis, yAxis ) );
        } else {
            throw new IOException("unsupported output format: " + format.getName() );
        }
    }

    /**
     * Allows any resources held by this object to be released. The result of calling any other
     * method subsequent to a call to this method is undefined. It is important for applications
     * to call this method when they know they will no longer be using this <code>GridCoverageWriter</code>.
     * Otherwise, the writer may continue to hold on to resources indefinitely.
     *
     * @throws IOException if an error occured while disposing resources
     *         (for example while flushing data and closing a file).
     */
    public void dispose() throws IOException {
        ((OutputStream)destination).close();
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ImageGridCoverageWriter.java,v $
Revision 1.6  2006/04/06 20:25:26  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.4  2006/03/15 22:20:09  poth
*** empty log message ***

Revision 1.3  2006/03/05 17:41:07  poth
*** empty log message ***

Revision 1.2  2006/03/02 11:06:04  poth
*** empty log message ***

Revision 1.1.1.1  2005/01/05 10:36:06  poth
no message

Revision 1.3  2004/07/19 06:20:01  ap
no message

Revision 1.2  2004/07/16 10:24:00  ap
no message

Revision 1.1  2004/07/16 06:19:38  ap
no message


********************************************************************** */