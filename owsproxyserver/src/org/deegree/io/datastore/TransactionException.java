//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/TransactionException.java,v 1.1 2006/09/26 16:42:42 mschneider Exp $
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
package org.deegree.io.datastore;

/**
 * Indicates that an error occured so the transaction could not be performed.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.1 $, $Date: 2006/09/26 16:42:42 $
 */
public class TransactionException extends DatastoreException {

    private static final long serialVersionUID = 2922243295498899916L;

    /**
     * Constructs an instance of <code>TransactionException</code> with the specified
     * detail message.
     * 
     * @param msg
     *            the detail message
     */    
    public TransactionException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>TransactionException</code> with the specified
     * cause.
     * 
     * @param cause
     *            the Throwable that caused this TransactionException
     * 
     */    
    public TransactionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of <code>TransactionException</code> with the specified
     * detail message and cause.
     * 
     * @param msg
     *            the detail message
     * @param cause
     *            the Throwable that caused this TransactionException
     * 
     */    
    public TransactionException(String msg, Throwable cause) {
        super(msg, cause);        
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TransactionException.java,v $
Revision 1.1  2006/09/26 16:42:42  mschneider
Moved from org.deegree.io.datastore.sql.transaction.

Revision 1.5  2006/09/19 14:57:01  mschneider
Fixed warnings, improved javadoc.

Revision 1.4  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.3  2006/04/04 20:39:43  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.1  2006/02/17 14:41:16  mschneider
Initial version.

********************************************************************** */