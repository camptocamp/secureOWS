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
package org.deegree.ogcwebservices.csw.discovery;

import java.util.Date;

import org.deegree.framework.util.TimeTools;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * Class representation of a &lt;csw:SearchStatus&gt;-element.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/07/11 07:10:11 $
 */
public class SearchStatus {

    private static final String[] STATES = { "complete", "subset", "interim",
            "processing", "none"};

    private String status;

    private Date timestamp;

    private SearchStatus() {
        this.timestamp = new Date(System.currentTimeMillis());
    }

    SearchStatus(String status) {
        this();
        for (int i = 0; i < STATES.length; i++) {
            String aState = STATES[i];
            if (aState.equalsIgnoreCase(status)) {
                this.status = status;
            }
        }
    }

    SearchStatus(String status, Date timestamp) {
        this(status);
        this.timestamp = timestamp;
    }

    /**
     * Create a new instance from status-String and timestamp-String.
     * 
     * TODO: parse timestampString
     * 
     * @param status
     * @param timestampString
     * @throws InvalidParameterValueException
     */
    SearchStatus(String status, String timestampString) {
        this(status); 
        this.timestamp = TimeTools.createCalendar( timestampString ).getTime();
    }

    /**
     * possible values are:
     * <ul>
     * <li>complete: The request was successfully completed and valid results
     * are available or have been returned.
     * <li>subset: The request was successfully completed and partial valid
     * results are available or have been returned. In this case subsequest
     * queries with new start positions may be used to see more results.
     * <li>interim: The request was successfully completed and partial results
     * are available or have been returned but the results may not be valid. For
     * example, an intermediate server in a distributed search may have failed
     * cause the partial, invalid result set to be generated.
     * <li>processing: The request is still processing. When completed, the
     * response will be sent to the specified response handler.
     * <li>none: No records found.
     * </ul>
     * 
     * @uml.property name="status"
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 
     * @uml.property name="timestamp"
     */
    public Date getTimestamp() {
        return this.timestamp;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SearchStatus.java,v $
Revision 1.7  2006/07/11 07:10:11  poth
footer added/corrected


********************************************************************** */