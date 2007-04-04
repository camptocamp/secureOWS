//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/TransactionOperation.java,v 1.2 2006/10/12 16:24:00 mschneider Exp $
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

import org.deegree.datatypes.QualifiedName;

/**
 * Abstract base class for all operations that can occur inside a {@link Transaction} request.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.2 $, $Date: 2006/10/12 16:24:00 $
 */
public abstract class TransactionOperation {

    private String handle;

    /**
     * Creates a new <code>TransactionOperation</code> instance that may be identified (in the
     * scope of a transaction) by the optional handle.
     * 
     * @param handle optional identifier for the operation (for error messsages)
     */
    protected TransactionOperation( String handle ) {
        this.handle = handle;
    }

    /**
     * Returns the idenfifier of the operation.
     * 
     * @return the idenfifier of the operation.
     */
    public String getHandle() {
        return this.handle;
    }

    /**
     * Returns the names of the feature types that are affected by the operation.
     * 
     * @return the names of the affected feature types.
     */
    public abstract List<QualifiedName> getAffectedFeatureTypes();
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: TransactionOperation.java,v $
 * Revision 1.2  2006/10/12 16:24:00  mschneider
 * Javadoc + compiler warning fixes.
 *
 * Revision 1.1  2006/05/16 16:25:30  mschneider
 * Moved transaction related classes from org.deegree.ogcwebservices.wfs.operation to org.deegree.ogcwebservices.wfs.operation.transaction.
 *
 * Revision 1.6  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/02 20:45:32  mschneider
 * Former WFSOperation.
 * Revision 1.10 2005/12/20 14:50:11 mschneider Fixed imports.
 * 
 * Revision 1.9 2005/12/19 09:31:55 deshmukh Delete Transaction implemented Revision 1.8 2005/12/13
 * 16:10:59 deshmukh Changes made for Transactions Revision 1.7 2005/12/06 15:47:48 deshmukh
 * Modification to accomodate Transaction Revision 1.6 2005/11/23 14:15:13 deshmukh WFSTransaction
 * work in progress Revision 1.5 2005/11/22 18:08:04 deshmukh Transaction WFS.. work in progress
 * Revision 1.4 2005/11/16 13:44:59 mschneider Merge of wfs development branch.
 * 
 * Revision 1.3.2.1 2005/11/15 14:36:19 deshmukh QualifiedName modifications Revision 1.3 2005/08/26
 * 21:11:29 poth no message
 * 
 * Revision 1.1 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.2 2004/08/11 15:52:28 tf no message Revision 1.1 2004/06/07 13:38:34 tf code adapted
 * to wfs1 refactoring
 * 
 * Revision 1.2 2003/04/23 07:23:15 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:53 poth no message
 * 
 * Revision 1.5 2002/08/15 10:02:41 ap no message
 * 
 * Revision 1.4 2002/07/10 14:17:53 ap no message
 * 
 * Revision 1.3 2002/04/26 09:02:51 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 *
 ********************************************************************** */