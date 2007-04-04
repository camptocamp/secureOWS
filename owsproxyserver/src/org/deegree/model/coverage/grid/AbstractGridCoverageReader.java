// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/AbstractGridCoverageReader.java,v 1.10 2006/11/27 09:07:52 poth Exp $
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.MetadataNameNotFoundException;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * @version $Revision: 1.10 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.10 $, $Date: 2006/11/27 09:07:52 $ *  * @since 2.0
 */

public abstract class AbstractGridCoverageReader implements GridCoverageReader {

    protected CoverageOffering description = null;
    protected Object source = null;
    protected Envelope envelope = null; 
    private Map metadata = new HashMap();
    private String[] subNames = null;
    private String currentSubname = null;
    protected Format format = null;

   
    /**
     * @param source
     * @param description
     * @param envelope
     * @param format
     */
    public AbstractGridCoverageReader(Object source, CoverageOffering description, 
                                      Envelope envelope, Format format) {    
        this.description = description;
        this.source = source;
        this.envelope = envelope;
        this.format = format;
    }

    /**
     * Returns the input source. This is the object passed to the
     * {@link org.opengis.coverage.grid.GridCoverageExchange#getReader(Object)} 
     * method. It can be a {@link java.lang.String}, an {@link java.io.InputStream}, a
     * {@link java.nio.channels.FileChannel}, whatever.
     * 
     */
    public Object getSource() {
        return source;
    }


    /**
     * Returns the list of metadata keywords associated with the {@linkplain #getSource
     * input source} as a whole (not associated with any particular grid coverage).
     * If no metadata is available, the array will be empty.
     *
     * @return The list of metadata keywords for the input source.
     * @throws IOException if an error occurs during reading.
     *
     * @revisit This javadoc may not apply thats well in the iterator scheme.
     */
    public String[] getMetadataNames() throws IOException {
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
     * Set the name for the next grid coverage to GridCoverageWriter#write 
     * within the{@linkplain #getSource() input}. The subname can been fetch 
     * later at reading time.
     * 
     * @throws IOException if an error occurs during writing.
     * @revisit Do we need a special method for that, or should it be a metadata?
     * 
     */
    public void setCurrentSubname(String name) throws IOException {
        currentSubname = name;
    }


    /**
     * Retrieve the list of grid coverages contained within the {@linkplain #getSource
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
     * {@linkplain #getSource input source}.
     * 
     * @throws IOException if an error occurs during reading.
     * @revisit Do we need a special method for that, or should it be a metadata?
     * 
     */
    public String getCurrentSubname() throws IOException {
        return currentSubname;
    }

    /**
     * Returns the format handled by this <code>GridCoverageReader</code>.
     * 
     */
    public Format getFormat() {
        return format;
    }

    
    /**
     * transforms the passed <tt>Envelope</tt> to a <tt>LonLatEnvelope</tt>
     * If the passed source CRS isn't equal to "EPSG:4326" the <tt>Envelope</tt>
     * will be transformed to "EPSG:4326" first.
     * @param env
     * @param sourceCRS
     * @return
     */
    protected LonLatEnvelope calcLonLatEnvelope(Envelope env, String sourceCRS) {
        LonLatEnvelope lle = null;
        if ( sourceCRS.equalsIgnoreCase("EPSG:4326") ) {
            lle = new LonLatEnvelope(env);
        } else {
            try {
                IGeoTransformer tr = new GeoTransformer("EPSG:4326");
                env = tr.transform(env, sourceCRS );
            } catch (Exception e) {
                e.printStackTrace();
            }
            lle = new LonLatEnvelope(env);
        }
        return lle;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: AbstractGridCoverageReader.java,v $
   Revision 1.10  2006/11/27 09:07:52  poth
   JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

   Revision 1.9  2006/09/27 16:46:41  poth
   transformation method signature changed

   Revision 1.8  2006/05/03 20:09:52  poth
   *** empty log message ***

   Revision 1.7  2006/05/01 20:15:27  poth
   *** empty log message ***

   Revision 1.6  2006/04/06 20:25:26  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.3  2006/02/23 07:45:24  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.8  2004/07/22 15:20:41  ap
   no message

   Revision 1.7  2004/07/19 06:20:00  ap
   no message

   Revision 1.6  2004/07/16 06:19:38  ap
   no message

   Revision 1.5  2004/07/15 08:18:08  ap
   no message

   Revision 1.4  2004/07/14 06:52:48  ap
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/07/05 06:14:59  ap
   no message

   Revision 1.1  2004/05/25 07:14:01  ap
   no message

   Revision 1.1  2004/05/24 06:51:31  ap
   no message


********************************************************************** */
