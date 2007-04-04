//$Header$
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
package org.deegree.ogcwebservices.csw.manager;

import java.util.List;

import org.w3c.dom.Node;

/**
 * A InsertResult object may appear zero or more times in the transaction response. It is used to
 * report to the client a brief representation of each new record, including the record identifier,
 * created in the catalogue. The records must be reported in the same order in which a Insert object
 * appear in a transaction request and must map 1-to-1. Optionally, the handle attribute may be used
 * to correlate a particular Insert object in the Transaction request with an InsertResult obejt
 * found in the transaction response.
 * 
 * 
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version 1.0. $Revision$, $Date$
 * 
 * @since 2.0
 */
public class InsertResults {

    private List<Node> records = null;

    /**
     * @param records
     *            records inserted into a backend by a CS-W Insert operation
     */
    public InsertResults( List<Node> records ) {
        this.records = records;
    }

    /**
     * returns the records inserted into a backend by a CS-W Insert operation
     * 
     * @return
     */
    public List<Node> getRecords() {
        return records;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log$
 * Changes to this class. What the people have been up to: Revision 1.5  2006/08/29 19:54:14  poth
 * Changes to this class. What the people have been up to: footer corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/04/06 20:25:27  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/04/04 20:39:43  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Revision 1.2 2006/03/30 21:20:26
 * poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/20 12:40:02 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/