//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/AbstractGridCoverageWriter.java,v 1.5 2006/04/06 20:25:26 poth Exp $
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
import java.util.HashMap;
import java.util.Map;

import org.opengis.coverage.MetadataNameNotFoundException;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverageWriter;

/**
 * @version $Revision: 1.5 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.5 $, $Date: 2006/04/06 20:25:26 $ *  * @since 2.0
 */

public abstract class AbstractGridCoverageWriter implements GridCoverageWriter {
       
    protected Object destination = null;
    private Map metadata = new HashMap();
    private String[] subNames = null;
    private String currentSubname = null;
    protected Format format = null;

    
 
    /**
     * @param destination
     * @param metadata
     * @param subNames
     * @param currentSubname
     * @param format
     */
    public AbstractGridCoverageWriter(Object destination, Map metadata,
            String[] subNames, String currentSubname, Format format) {
        this.destination = destination;
        this.metadata = metadata;
        this.subNames = subNames;
        this.currentSubname = currentSubname;
        this.format = format;
    }

    /**
     * Returns the format handled by this <code>GridCoverageWriter</code>.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Returns the output destination. This is the object passed to the
     * {@link org.opengis.coverage.grid.GridCoverageExchange#getWriter} method. 
     * It can be a {@link java.lang.String}, an {@link java.io.OutputStream},
     * a {@link java.nio.channels.FileChannel}, etc.
     */
    public Object getDestination() {
        return destination;
    }


    /**
     * Returns the list of metadata keywords associated with the {@linkplain #getDestination
     * output destination} as a whole (not associated with any particular grid coverage).
     * If no metadata is allowed, the array will be empty.
     *
     * @return The list of metadata keywords for the output destination.
     *
     * @revisit This javadoc may not apply thats well in the iterator scheme.
     */
    public String[] getMetadataNames() {
        String[] mn = (String[])metadata.keySet().toArray(new String[metadata.size()]);
        return mn;
    }
    
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
    public String getMetadataValue(String name) throws IOException, MetadataNameNotFoundException {
        return (String)metadata.get(name);
    }

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
    public void setMetadataValue(String name, String value) throws IOException, 
                                                    MetadataNameNotFoundException {
        metadata.put( name, value);
    }
    
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
    public String[] listSubNames() throws IOException {
        return subNames;
    }

    /**
     * Returns the name for the next grid coverage to be read from the
     * {@linkplain #getDestination() output destination}.
     * 
     * @throws IOException if an error occurs during reading.
     * @revisit Do we need a special method for that, or should it be a metadata?
     */
    public String getCurrentSubname() throws IOException {
        return currentSubname;
    }

    /**
     * Set the name for the next grid coverage to 
     * GridCoverageWriter.write(GridCoverage, GeneralParameterValue[])  
     * within the{@linkplain #getDestination output destination}. The subname can been fetch 
     * later at reading time.
     * 
     * @throws IOException if an error occurs during writing.
     * @revisit Do we need a special method for that, or should it be a metadata?
     */
    public void setCurrentSubname(String name) throws IOException {
        currentSubname = name;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractGridCoverageWriter.java,v $
Revision 1.5  2006/04/06 20:25:26  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.3  2006/02/28 17:53:31  poth
*** empty log message ***

Revision 1.2  2005/01/18 22:08:54  poth
no message

Revision 1.3  2004/07/19 06:20:00  ap
no message

Revision 1.2  2004/07/15 11:31:09  ap
no message

Revision 1.1  2004/07/15 08:18:08  ap
no message


********************************************************************** */