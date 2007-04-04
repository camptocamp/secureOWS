/*$************************************************************************************************
 **
 ** $Id: GridCoverageExchange.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/grid/Attic/GridCoverageExchange.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.grid;

// J2SE direct dependencies
import java.io.IOException;
import java.io.InputStream;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;


/**
 * Support for creation of grid coverages from persistent formats as well as exporting
 * a grid coverage to a persistent formats. For example, it allows for creation of grid
 * coverages from the GeoTIFF Well-known binary format and exporting to the GeoTIFF file format.
 * Basic implementations only require creation of grid coverages from a file format or resource.
 * More sophesticated implementations may extract the grid coverages from a database. In such
 * case, a <code>GridCoverageExchange</code> instance will hold a connection to a specific
 * database and the {@link #dispose} method will need to be invoked in order to close this
 * connection.
 *
 * @UML abstract CV_GridCoverageExchange
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 *
 * @see GridCoverageReader
 * @see GridCoverageWriter
 */
public interface GridCoverageExchange {
    /**
     * Retrieve information on file formats or resources available with the
     * <code>GridCoverageExchange</code> implementation.
     *
     * @return Information on file formats or resources available with
     *         the <code>GridCoverageExchange</code> implementation.
     *
     * @UML operation getFormat
     * @UML mandatory numFormats
     */
    Format[] getFormats();

    /**
     * Returns a grid coverage reader that can manage the specified source
     *
     * @param  source An object that specifies somehow the data source. Can be a
     *         {@link java.lang.String}, an {@link java.io.InputStream}, a
     *         {@link java.nio.channels.FileChannel}, whatever. It's up to the associated
     *         grid coverage reader to make meaningful use of it.
     * @return The grid coverage reader.
     * @throws IOException if an error occurs during reading.
     *
     * @revisit We need a mechanism to allow the right GridCoverageReader
     *          Something like an SPI. What if we can't find a GridCoverageReader?
     *          Do we return null or throw an Exception?
     */
    GridCoverageReader getReader(Object source) throws IOException;
    
    /**
     * This method is a deegree specific enhancement of the <tt>GridCoverageExchange</tt> 
     * class/interface as defined by GeoAPI.
     * Returns a grid coverage reader that can manage the specified source
     *
     * @param  source An object that specifies somehow the data source. 
     * @param description an object describing the grid coverage and the access to
     *                   avaiable metadata
     * @param envelope
     * @param format
     * @return The grid coverage reader.
     * @throws IOException if an error occurs during reading.
     *
     * @revisit We need a mechanism to allow the right GridCoverageReader
     *          Something like an SPI. What if we can't find a GridCoverageReader?
     *          Do we return null or throw an Exception?
     */
    GridCoverageReader getReader(InputStream source, CoverageOffering description,
                                 Envelope envelope, Format format) 
                                 throws IOException;
    
    /**
     * This method is a deegree specific enhancement of the <tt>GridCoverageExchange</tt> 
     * class/interface as defined by GeoAPI.
     * Returns a grid coverage reader that can manage the specified source
     *
     * @param  resource a string that specifies somehow the data source (e.g. a file). 
     * @param description an object describing the grid coverage and the access to
     *                   avaiable metadata
     * @param envelope
     * @param format
     * @return The grid coverage reader.
     * @throws IOException if an error occurs during reading.
     *
     * @revisit We need a mechanism to allow the right GridCoverageReader
     *          Something like an SPI. What if we can't find a GridCoverageReader?
     *          Do we return null or throw an Exception? 
     */
    GridCoverageReader getReader(Object resource, CoverageOffering description,
                                 Envelope envelope, Format format) 
                                 throws IOException, InvalidParameterValueException;
    
    /**
     * This method is a deegree specific enhancement of the <tt>GridCoverageExchange</tt> 
     * class/interface as defined by GeoAPI.
     * Returns a grid coverage reader that can manage the specified source
     *
     * @param  resources an array of strings that specifies somehow the data source 
     *          (e.g. some files). 
     * @param description an object describing the grid coverage and the access to
     *                   avaiable metadata
     * @param envelope 
     * @param format
     * @return The grid coverage reader.
     * @throws IOException if an error occurs during reading.
     *
     * @revisit We need a mechanism to allow the right GridCoverageReader
     *          Something like an SPI. What if we can't find a GridCoverageReader?
     *          Do we return null or throw an Exception? 
     */
    GridCoverageReader getReader(Object[] resources, CoverageOffering description,
                                 Envelope envelope, Format format) 
                                 throws IOException, InvalidParameterValueException;

    /**
     * Returns a GridCoverageWriter that can write the specified format.
     * The file format name is determined from the {@link Format} interface.
     * Sample file formats include:
     *
     * <blockquote><table>
     *   <tr><td>"GeoTIFF"</td>  <td>&nbsp;&nbsp;- GeoTIFF</td></tr>
     *   <tr><td>"PIX"</td>      <td>&nbsp;&nbsp;- PCI Geomatics PIX</td></tr>
     *   <tr><td>"HDF-EOS"</td>  <td>&nbsp;&nbsp;- NASA HDF-EOS</td></tr>
     *   <tr><td>"NITF"</td>     <td>&nbsp;&nbsp;- National Image Transfer Format</td></tr>
     *   <tr><td>"STDS-DEM"</td> <td>&nbsp;&nbsp;- Standard Transfer Data Standard</td></tr>
     * </table></blockquote>
     *
     * @param  destination An object that specifies somehow the data destination.
     *         Can be a {@link java.lang.String}, an {@link java.io.OutputStream},
     *         a {@link java.nio.channels.FileChannel}, whatever. It's up to the
     *         associated grid coverage writer to make meaningful use of it.
     * @param  format the output format.
     * @return The grid coverage writer.
     * @throws IOException if an error occurs during reading.
     */
    GridCoverageWriter getWriter(Object destination, Format format) throws IOException;

    /**
     * Allows any resources held by this object to be released. The result of calling any other
     * method subsequent to a call to this method is undefined. Applications should call this
     * method when they know they will no longer be using this <code>GridCoverageExchange</code>,
     * especially if it was holding a connection to a database.
     *
     * @throws IOException if an error occured while disposing resources
     *         (for example closing a database connection).
     */
    void dispose() throws IOException;
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GridCoverageExchange.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
