//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/TransactionResponse.java,v 1.5 2006/10/12 16:24:00 mschneider Exp $
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
 Aennchenstraße 19
 53177 Bonn
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
package org.deegree.ogcwebservices.wfs.operation.transaction;

import java.util.List;

import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;

/**
 * Encapsulates a TransactionResponse element according to WFS Specification OGC 04-094 (#12.3
 * Pg.72).
 * <p>
 * Because deegree supports atomic transactions, there is no need for the optional
 * TransactionResults child element.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/10/12 16:24:00 $
 */
public class TransactionResponse extends DefaultOGCWebServiceResponse {

    private int totalInserted;

    private int totalUpdated;

    private int totalDeleted;           

    private List<InsertResults> insertResults;

    /**
     * Creates a new <code>TransactionResponse</code> instance from the given parameters.
     * 
     * @param transaction request caused a response
     * @param totalInserted
     * @param totalUpdated
     * @param totalDeleted
     * @param insertResults
     */
    public TransactionResponse( Transaction transaction, int totalInserted, int totalUpdated, 
                                int totalDeleted, List<InsertResults> insertResults ) {
        super( transaction );
        this.totalInserted = totalInserted;
        this.totalUpdated = totalUpdated;
        this.totalDeleted = totalDeleted;
        this.insertResults = insertResults;
    }

    /**
     * Returns the number of features that have been deleted in the transaction.
     * 
     * @return number of features that have been deleted in the transaction.
     */
    public int getTotalDeleted() {
        return totalDeleted;
    }

    /**
     * Returns the number of features that have been inserted in the transaction.
     * 
     * @return number of features that have been inserted in the transaction.
     */    
    public int getTotalInserted() {
        return totalInserted;
    }

    /**
     * Returns the number of features that have been updated in the transaction.
     * 
     * @return number of features that have been updated in the transaction.
     */    
    public int getTotalUpdated() {
        return totalUpdated;
    }

    /**
     * Returns the insert results, i.e. the feature ids of the features that have been inserted for
     * every insert operation of the transaction.
     * 
     * @return the insert results.
     */
    public List<InsertResults> getInsertResults() {
        return insertResults;
    }    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TransactionResponse.java,v $
Revision 1.5  2006/10/12 16:24:00  mschneider
Javadoc + compiler warning fixes.

Revision 1.4  2006/10/10 08:07:59  poth
Collection substituted by List

Revision 1.3  2006/10/09 09:01:48  poth
bug fix - TransactionResponse extending DefaultOGCWebServiceResponse

Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */