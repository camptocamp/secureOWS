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

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.w3c.dom.Element;

/**
 * 
 * 
 * 
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/10/13 14:22:08 $
 * 
 * @since 2.0
 */
public class TransactionResult extends DefaultOGCWebServiceResponse {

    private int totalInserted = 0;

    private int totalDeleted = 0;

    private int totalUpdated = 0;

    private InsertResults results = null;

    /**
     * 
     * @param id
     * @param root
     * @return
     * @throws XMLParsingException
     */
    public static final TransactionResult create( Transaction transaction, Element root )
                            throws XMLParsingException {
        TransactionResultDocument doc = new TransactionResultDocument();
        doc.setRootElement( root );
        return doc.parseTransactionResponse( transaction );
    }

    /**
     * 
     * @param transaction
     *             source Transaction request
     * @param totalInserted
     *            the amount of records that has been inserted
     * @param totalDeleted
     *            the amount of records that has been deleted
     * @param totalUpdated
     *            the amount of records that has been updated
     * @param results
     *            insert result description
     */
    TransactionResult( OGCWebServiceRequest transaction, int totalInserted, int totalDeleted, 
                       int totalUpdated, InsertResults results ) {
        super( transaction );
        this.totalInserted = totalInserted;
        this.totalDeleted = totalDeleted;
        this.totalUpdated = totalUpdated;
        this.results = results;
    }

    /**
     * returns insert result description
     * 
     * @return
     */
    public InsertResults getResults() {
        return results;
    }

    /**
     * returns the amount of records that has been deleted
     * 
     * @return
     */
    public int getTotalDeleted() {
        return totalDeleted;
    }

    /**
     * returns the amount of records that has been inserted
     * 
     * @return
     */
    public int getTotalInserted() {
        return totalInserted;
    }

    /**
     * returns the amount of records that has been updated
     * 
     * @return
     */
    public int getTotalUpdated() {
        return totalUpdated;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: TransactionResult.java,v $
 * Changes to this class. What the people have been up to: Revision 1.7  2006/10/13 14:22:08  poth
 * Changes to this class. What the people have been up to: changes required because of extending TransactionResult from DefaultOGCWebServiceResponse
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/04/06 20:25:27  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/04/04 20:39:42  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Revision
 * 1.4 2006/03/30 21:20:26 poth ** empty log message ***
 * 
 * Revision 1.3 2006/03/25 15:58:36 poth ** empty log message ***
 * 
 * Revision 1.5 2006/02/21 19:47:49 poth ** empty log message ***
 * 
 * Revision 1.4 2006/02/20 14:14:00 poth ** empty log message ***
 * 
 * Revision 1.3 2006/02/20 12:40:02 poth ** empty log message ***
 * 
 * 
 * 
 **************************************************************************************************/