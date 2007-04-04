//$CVSHeader: deegree/src/org/deegree/io/datastore/sql/transaction/FeatureRow.java,v 1.13 2006/09/26 16:45:44 mschneider Exp $
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
package org.deegree.io.datastore.sql.transaction;

import java.util.Iterator;

import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;

/**
 * Represents a feature table row (columns + values) which has to be inserted as part of a
 * {@link Insert} operation.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.13 $, $Date: 2006/09/26 16:45:44 $
 */
public class FeatureRow extends InsertRow {

    /**
     * Creates a new <code>FeatureRow</code> instance for the given feature table.
     * 
     * @param table
     */
    public FeatureRow( String table ) {
        super( table );
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "FeatureRow, table: '" );
        sb.append( this.table );
        sb.append( "'" );
        Iterator<String> columnIter = this.columnMap.keySet().iterator();
        while ( columnIter.hasNext() ) {
            sb.append( ", " );
            String column = columnIter.next();
            InsertField field = this.columnMap.get( column );
            sb.append( field );
        }
        return sb.toString();
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: FeatureRow.java,v $
 * Revision 1.13  2006/09/26 16:45:44  mschneider
 * Javadoc corrections + fixed warnings.
 *
 * Revision 1.12  2006/09/19 14:57:01  mschneider
 * Fixed warnings, improved javadoc.
 *
 * Revision 1.11  2006/05/21 19:07:16  poth
 * several methods set to public; required by SDE datastore
 *
 * Revision 1.10  2006/04/25 12:37:10  mschneider
 * Testing code formatter and cvs log.
 *
 * Revision 1.9  2006/04/25 12:34:19  mschneider
 * Testing code formatter and cvs log.
 * Revision 1.8 2006/04/25 12:29:47 mschneider Testing code formatter and
 * cvs log.
 * 
 * Revision 1.7 2006/04/25 12:28:36 mschneider Testing code formatter and cvs log.
 * 
 * Revision 1.3 2006/04/06 20:25:27 poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/22 02:33:00 mschneider Simplified toString().
 * 
 * Revision 1.1 2006/02/20 16:33:03 mschneider Initial version.
 * 
 **************************************************************************************************/
