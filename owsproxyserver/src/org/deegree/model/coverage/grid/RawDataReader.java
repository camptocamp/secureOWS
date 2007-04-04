//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/RawDataReader.java,v 1.3 2006/04/06 20:25:26 poth Exp $
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
import java.io.InputStream;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wcs.configuration.File;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterNotFoundException;

/**
 * 
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/04/06 20:25:26 $
 *
 * @since 2.0
 */
public class RawDataReader extends AbstractGridCoverageReader {

    /**
     * @param source
     * @param description
     */
    public RawDataReader(File source, CoverageOffering description, 
                                   Format format) {
        super(source, description, source.getEnvelope(), format );
    }
    
    
    /**
     * @param source
     * @param description
     * @param envelope
     * @param format
     */
    public RawDataReader(InputStream source, CoverageOffering description, 
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
        return null;
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
        if ( source instanceof InputStream ) {
            ((InputStream)source).close();
        }
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RawDataReader.java,v $
Revision 1.3  2006/04/06 20:25:26  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.1.1.1  2005/01/05 10:36:07  poth
no message

Revision 1.1  2004/08/23 07:00:16  ap
no message


********************************************************************** */