/*$************************************************************************************************
 **
 ** $Id: GridCoverageWriter.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/grid/Attic/GridCoverageWriter.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.grid;

// J2SE dependencies
import java.io.IOException;

import org.opengis.coverage.MetadataNameNotFoundException;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterNotFoundException;


/**
 * Support for writing grid coverages into a persistent store. Instance
 * of <code>GridCoverageWriter</code> are obtained through a call to
 * {@link GridCoverageExchange#getWriter}. Grid coverages are usually
 * added to the output stream in a sequential order.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 *
 * @see GridCoverageExchange#getWriter
 * @see javax.imageio.ImageWriter
 */
public interface GridCoverageWriter {
    /**
     * Returns the format handled by this <code>GridCoverageWriter</code>.
     */
    Format getFormat();

    /**
     * Returns the output destination. This is the object passed to the
     * {@link GridCoverageExchange#getWriter} method. It can be a
     * {@link java.lang.String}, an {@link java.io.OutputStream},
     * a {@link java.nio.channels.FileChannel}, etc.
     */
    Object getDestination();

    /**
     * Returns the list of metadata keywords associated with the {@linkplain #getDestination
     * output destination} as a whole (not associated with any particular grid coverage).
     * If no metadata is allowed, the array will be empty.
     *
     * @return The list of metadata keywords for the output destination.
     *
     * @revisit This javadoc may not apply thats well in the iterator scheme.
     */
    String[] getMetadataNames();
    
    /**
     * Retrieve the metadata value for a given metadata name.
     *
     * @param  name Metadata keyword for which to retrieve metadata.
     * @return The metadata value for the given metadata name. Should be one of
     *         the name returned by {@link #getMetadataNames}.
     * @throws IOException if an error occurs during reading.
     * @throws MetadataNameNotFoundException if there is no value for the specified metadata name.
     *
     * @revisit This javadoc may not apply thats well in the iterator scheme.
     */
    String getMetadataValue(String name) throws IOException, MetadataNameNotFoundException;    

    /**
     * Sets the metadata value for a given metadata name.
     *
     * @param  name Metadata keyword for which to set the metadata.
     * @param  value The metadata value for the given metadata name.
     * @throws IOException if an error occurs during writing.
     * @throws MetadataNameNotFoundException if the specified metadata name is not handled
     *         for this format.
     *
     * @revisit This javadoc may not apply thats well in the iterator scheme.
     */
    void setMetadataValue(String name, String value) throws IOException, MetadataNameNotFoundException;
    
    /**
     * Retrieve the list of grid coverages contained within the {@linkplain #getDestination()
     * input source}. Each grid can have a different coordinate system, number of dimensions
     * and grid geometry. For example, a HDF-EOS file (GRID.HDF) contains 6 grid coverages
     * each having a different projection. An empty array will be returned if no sub names
     * exist.
     *
     * @return The list of grid coverages contained within the input source.
     * @throws IOException if an error occurs during reading.
     *
     * @revisit The javadoc should also be more explicit about hierarchical format.
     *          Should the names be returned as paths?
     *          Explain what to return if the GridCoverage are accessible by index
     *          only. A proposal is to name them "grid1", "grid2", etc.
     */
    String[] listSubNames() throws IOException;
    
    /**
     * Returns the name for the next grid coverage to be 
     * {@linkplain #write(GridCoverage, GeneralParameterValue[]) write} to the
     * {@linkplain #getDestination() output destination}.
     *
     * @throws IOException if an error occurs during reading.
     * @revisit Do we need a special method for that, or should it be a metadata?
     */
    String getCurrentSubname() throws IOException;

    /**
     * Set the name for the next grid coverage to 
     * {@linkplain #write(GridCoverage, GeneralParameterValue[]) write} within the
     * {@linkplain #getDestination output destination}. The subname can been fetch later
     * at reading time.
     *
     * @throws IOException if an error occurs during writing.
     * @revisit Do we need a special method for that, or should it be a metadata?
     */
    void setCurrentSubname(String name) throws IOException;

    /**
     * Writes the specified grid coverage.
     *
     * @param  coverage The {@linkplain GridCoverage grid coverage} to write.
     * @param  parameters An optional set of parameters. Should be any or all of the
     *         parameters returned by {@link Format#getWriteParameters}.
     * @throws InvalidParameterNameException if a parameter in <code>parameters</code>
     *         doesn't have a recognized name.
     * @throws InvalidParameterValueException if a parameter in <code>parameters</code>
     *         doesn't have a valid value.
     * @throws ParameterNotFoundException if a parameter was required for the operation but was
     *         not provided in the <code>parameters</code> list.
     * @throws FileFormatNotCompatibleWithGridCoverageException if the grid coverage
     *         can't be exported in the {@linkplain #getFormat writer format}.
     * @throws IOException if the export failed for some other input/output reason, including
     *         {@link javax.imageio.IIOException} if an error was thrown by the underlying
     *         image library.
     */
    void write(GridCoverage coverage, GeneralParameterValue[] parameters)
        throws InvalidParameterNameException, InvalidParameterValueException, ParameterNotFoundException, IOException;
    
    /**
     * Writes the specified grid coverage.
     *
     * @param  coverage The {@linkplain GridCoverage grid coverage} to write.
     * @param  xAxis Dimension to use for the <var>x</var> axis.
     * @param  yAxis Dimension to use for the <var>y</var> axis.
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
                       InvalidParameterValueException, ParameterNotFoundException, IOException;

    /**
     * Allows any resources held by this object to be released. The result of calling any other
     * method subsequent to a call to this method is undefined. It is important for applications
     * to call this method when they know they will no longer be using this <code>GridCoverageWriter</code>.
     * Otherwise, the writer may continue to hold on to resources indefinitely.
     *
     * @throws IOException if an error occured while disposing resources
     *         (for example while flushing data and closing a file).
     */
    void dispose() throws IOException;
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GridCoverageWriter.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
