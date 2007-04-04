//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/Attic/DeleteNode.java,v 1.5 2006/10/06 14:16:09 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a table row that has to be deleted as part of a {@link Delete} operation.
 * <p>
 * Connected table entries (rows that refer to this row or that are referenced by this row) are
 * also held.
 * 
 * @see DeleteGraph
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/10/06 14:16:09 $
 */
class DeleteNode {

    private String table;

    private Map<String, Object> keyColumns;

    private Set<DeleteNode> preNodes = new HashSet<DeleteNode> ();

    private Set<DeleteNode> postNodes = new HashSet<DeleteNode> ();

    /**
     * Creates a new <code>DeleteNode</code> instance.
     * 
     * @param table
     * @param keyColumns
     */
    DeleteNode( String table, Map<String, Object> keyColumns ) {
        this.table = table;
        this.keyColumns = keyColumns;
    }

    /**
     * Returns the table name.
     * 
     * @return the table name
     */
    String getTable() {
        return this.table;
    }

    /**
     * Returns the key columns that identify the table row.
     * 
     * @return the key columns that identify the table row
     */
    Collection<String> getKeyColumns() {
        return this.keyColumns.keySet();
    }

    /**
     * Returns the value for the given key column name.
     * 
     * @return the value for the given key column name
     */
    Object getKeyColumnValue( String key ) {
        return this.keyColumns.get( key );
    }

    /**
     * Returns the set of post nodes, i.e. table rows that <i>are referenced</i> by this row. 
     * 
     * @return the set of post nodes
     */
    Collection<DeleteNode> getPostNodes () {
        return this.postNodes;
    }

    /**
     * Returns the set of pre nodes, i.e. table rows that <i>refer</i> to this row. 
     * 
     * @return the set of pre nodes
     */    
    Collection<DeleteNode> getPreNodes () {
        return this.preNodes;
    }    
    
    /**
     * Connects this node to the given target node.
     * <p>
     * NOTE: The target node is the one that stores the primary key. 
     * 
     * @param targetNode
     */
    void connect( DeleteNode targetNode ) {
        if ( !this.postNodes.contains( targetNode ) ) {
            this.postNodes.add( targetNode );
            targetNode.preNodes.add( this );
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || !( obj instanceof DeleteNode ) ) {
            return false;
        }
        DeleteNode that = (DeleteNode) obj;
        if ( !this.table.equals( that.table ) ) {
            return false;
        }
        if ( this.keyColumns.size() != that.keyColumns.size() ) {
            return false;
        }
        for ( String keyColumn : this.keyColumns.keySet() ) {
            Object thisKeyValue = this.keyColumns.get( keyColumn );
            Object thatKeyValue = that.keyColumns.get( keyColumn );
            if ( !thisKeyValue.equals( thatKeyValue ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */    
    @Override
    public String toString () {
        StringBuffer sb = new StringBuffer("");
        sb.append( this.table );
        for ( String keyColumn : this.keyColumns.keySet() ) {
            sb.append (",");            
            sb.append( keyColumn );
            sb.append( "='" );
            sb.append( this.keyColumns.get( keyColumn ) );
            sb.append( "'" );
        }
        return sb.toString ();
    }
    
    String toString( String indent, Set<DeleteNode> printedNodes ) {
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "- table: " );
        sb.append( this.table );
        for ( String keyColumn : this.keyColumns.keySet() ) {
            sb.append( ", " );
            sb.append( keyColumn );
            sb.append( "='" );
            sb.append( this.keyColumns.get( keyColumn ) );
            sb.append( "'" );
        }
        sb.append( '\n' );

        for ( DeleteNode postNode : this.postNodes ) {
            sb.append( indent );
            if ( printedNodes.contains( postNode ) ) {
                sb.append( indent + " " );
                sb.append( "- table: " );
                sb.append( postNode.getTable() );
                sb.append( " (DUP)\n" );
            } else {
                printedNodes.add( postNode );
                sb.append( postNode.toString( indent + " ", printedNodes ) );
            }
        }
        return sb.toString();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:

 $Log: DeleteNode.java,v $
 Revision 1.5  2006/10/06 14:16:09  mschneider
 Removed BOM (Byte Order Mark).

 Revision 1.4  2006/10/05 10:12:55  mschneider
 Javadoc fixes.

 Revision 1.3  2006/09/26 16:45:45  mschneider
 Javadoc corrections + fixed warnings.

 Revision 1.2  2006/09/19 14:57:01  mschneider
 Fixed warnings, improved javadoc.

 Revision 1.1  2006/05/12 15:48:49  mschneider
 Initial version.

 ********************************************************************** */