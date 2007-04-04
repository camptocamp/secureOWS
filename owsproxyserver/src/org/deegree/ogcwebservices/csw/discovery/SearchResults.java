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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.w3c.dom.Node;

/**
 * Class representation of a &lt;csw:SearchResults&gt;-element.
 * 
 * The SearchResults is a generic container for the actual response to a
 * GetRecords request. The content of the SearchResults is the set of records
 * returned by the GetRecords operation. The actual records returned by the
 * catalogue should substitute for AbstractRecord.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.16 $, $Date: 2006/07/11 07:10:11 $
 */

public class SearchResults {

    private static final String[] ELEMENT_SETS = { "brief", "summary", "full" };

    private URI requestId = null;

    private URI resultSetId = null;

    private String elementSet = null;

    private URI recordSchema = null;

    private int numberOfRecordsReturned = 0;
    
    private int numberOfRecordsMatched = 0;

    private int nextRecord = 0;

    private Date expires = null;
    
    private Node recordsParentNode;
    

    /**
     * 
     * @param requestId
     * @param resultSetId
     * @param elementSet
     * @param recordSchema
     * @param numberOfRecordsReturned
     * @param nextRecord
     * @param recordsParentNode
     * @throws InvalidParameterValueException
     */
    SearchResults(String requestId, String resultSetId, String elementSet,
            String recordSchema, int numberOfRecordsReturned, int numberOfRecordsMatched,  
            int nextRecord, Node recordsParentNode, String expires) throws InvalidParameterValueException {

        if (requestId != null) {
            try {
                this.requestId = new URI(null, requestId, null);
            } catch (URISyntaxException e) {
                throw new InvalidParameterValueException( "Value '" + requestId + 
                        "' of Parameter 'requestId' does not denote a valid URI.");
            }
        }

        if (resultSetId != null) {
            try {
                this.resultSetId = new URI(null, resultSetId, null);
            } catch (URISyntaxException e) {
                throw new InvalidParameterValueException( "Value '" + resultSetId
                                + "' of Parameter 'resultSetId' does not denote a valid URI.");
            }
        }

        if (elementSet != null) {
            for (int i = 0; i < ELEMENT_SETS.length; i++) {
                if (ELEMENT_SETS[i].equals(elementSet)) {
                    this.elementSet = elementSet;
                }
            }
            if (this.elementSet == null) {
                throw new InvalidParameterValueException( "Value '" + elementSet + 
                        "' of Parameter 'elementSet' is invalid. Valid parameters" + 
                        " are: 'full', 'summary' and 'brief'.");
            }
        }

        if (recordSchema != null) {
            try {
                this.recordSchema = new URI(null, recordSchema, null);
            } catch (URISyntaxException e) {
                throw new InvalidParameterValueException( "Value '" + recordSchema
                                + "' of Parameter 'recordSchema' does not denote a valid URI.");
            }
        }

        this.numberOfRecordsReturned = numberOfRecordsReturned;
        this.numberOfRecordsMatched = numberOfRecordsMatched;
        // TODO
        this.expires = new GregorianCalendar().getTime();//TimeTools.createCalendar( expires ).getTime();
        this.nextRecord = nextRecord;
        this.recordsParentNode = recordsParentNode;
    }

    /**
     * 
     */
    public URI getRequestId() {
        return requestId;
    }

    /**
     * A server-generated identifier for the result set. May be used in
     * subsequent GetRecords operations to further refine the result set. If the
     * server does not implement this capability then the attribute should be
     * omitted.
     */
    public URI getResultSetId() {
        return resultSetId;
    }

    /**
     * The element set returned (brief, summary or full). This is null if
     * getElementNames of <tt>GetRecord</tt>!= null; Optional
     */
    public String getElementSet() {
        return elementSet;
    }

    /**
     * A reference to the type or schema of the records returned. Optional
     */
    public URI getRecordSchema() {
        return recordSchema;
    }


    /**
     * Number of records found by the GetRecords operation
     */
    public int getNumberOfRecordsMatched() {
        return numberOfRecordsMatched;
    }

    /**
     * Number of records actually returned to client. This may not be the entire
     * result set since some servers may limit the number of records returned to
     * limit the size of the response package transmitted to the client.
     * Subsequent queries may be executed to see more of the result set. The
     * nextRecord attribute will indicate to the client where to begin the next
     * query
     */
    public int getNumberOfRecordsReturned() {
        return numberOfRecordsReturned;
    }

    /**
     * Start position of next record. A value of 0 means all records have been
     * returned.
     */
    public int getNextRecord() {
        return nextRecord;
    }


    /**
     * Returns the contents of the &lt;SearchResults&gt;-element.
     */
    public Node getRecords() {
        return recordsParentNode;
    }

    /**
     * 
     */
    public Date getExpires() {
        return expires;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SearchResults.java,v $
Revision 1.16  2006/07/11 07:10:11  poth
footer added/corrected


********************************************************************** */