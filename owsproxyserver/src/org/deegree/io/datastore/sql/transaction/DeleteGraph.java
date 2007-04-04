//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/Attic/DeleteGraph.java,v 1.7 2006/10/31 16:52:14 mschneider Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.content.MappingField;

/**
 * Represents a delete operation on a feature that contains the explicit information on all table
 * rows that have to be removed from the database.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/10/31 16:52:14 $
 */
class DeleteGraph {

    private List<DeleteNode> rootNodes = new ArrayList<DeleteNode>();

    private List<DeleteNode> allNodes = new ArrayList<DeleteNode>();

    /**
     * Looks for a {@link DeleteNode} that represents the specified feature instance. 
     * 
     * @param ft
     *            MappedFeatureType of the feature instance
     * @param fid
     *            FeatureId of the feature instance
     * @return DeleteNode for the feature instance if it exists, null otherwise
     */
    DeleteNode findNode( MappedFeatureType ft, FeatureId fid ) {

        Map<String, Object> keyColumns = new HashMap<String, Object>();
        MappingField[] fidFields = fid.getFidDefinition().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            keyColumns.put( fidFields[i].getField(), fid.getValue( i ) );
        }
        DeleteNode newNode = new DeleteNode( ft.getTable(), keyColumns );

        for ( DeleteNode node : allNodes ) {
            if ( node.equals( newNode ) ) {
                return node;
            }
        }

        return null;
    }

    DeleteNode findNode( String table, Map<String, Object> keyColumns ) {

        DeleteNode newNode = new DeleteNode( table, keyColumns );

        for ( DeleteNode node : allNodes ) {
            if ( node.equals( newNode ) ) {
                return node;
            }
        }

        return null;
    }

    DeleteNode addNode( MappedFeatureType ft, FeatureId fid ) {

        Map<String, Object> keyColumns = new HashMap<String, Object>();
        MappingField[] fidFields = fid.getFidDefinition().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            keyColumns.put( fidFields[i].getField(), fid.getValue( i ) );
        }

        DeleteNode newNode = new DeleteNode( ft.getTable(), keyColumns );
        this.allNodes.add( newNode );
        return newNode;
    }

    DeleteNode addNode( String table, Map<String, Object> keyColumns ) {
        DeleteNode newNode = new DeleteNode( table, keyColumns );
        this.allNodes.add( newNode );
        return newNode;
    }

    void markAsRootNode( DeleteNode node ) {
        this.rootNodes.add( node );
    }

    /**
     * Returns the nodes of the graph in topological order, i.e. they may be deleted in
     * that order without violating any foreign key constraints.
     * <p>
     * NOTE: If the foreign key constraints constitute a cycle, no topological order is
     * possible.
     * 
     * @return the nodes of the graph in topological order.
     */
    List<DeleteNode> getNodesInTopologicalOrder() {
        List<DeleteNode> orderedList = new ArrayList<DeleteNode>( this.allNodes.size() );

        // key: node (table entry), value: number of open fk constraints
        Map<DeleteNode, Integer> preMap = new HashMap<DeleteNode, Integer>();
        // contains only nodes that have no open fk constraints
        List<DeleteNode> noPreNodes = new ArrayList<DeleteNode>();
        for ( DeleteNode node : this.allNodes ) {
            int preCount = node.getPreNodes().size();
            preMap.put( node, preCount );
            if ( preCount == 0 ) {
                noPreNodes.add( node );
            }
        }

        while ( !noPreNodes.isEmpty() ) {
            DeleteNode currentNode = noPreNodes.remove( noPreNodes.size() - 1 );
            orderedList.add( currentNode );
            for ( DeleteNode post : currentNode.getPostNodes() ) {
                int preCount = preMap.get( post ) - 1;
                preMap.put( post, preCount );
                if ( preCount == 0 ) {
                    noPreNodes.add( post );
                }
            }
        }

        if ( orderedList.size() != this.allNodes.size() ) {
            throw new RuntimeException( "Cannot perform delete operation: cycle in fk constraints." );
        }

        return orderedList;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Set<DeleteNode> printedNodes = new HashSet<DeleteNode>();
        for ( DeleteNode rootNode : rootNodes ) {
            sb.append( rootNode.toString( "", printedNodes ) );
            sb.append( '\n' );
        }
        return sb.toString();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:

 $Log: DeleteGraph.java,v $
 Revision 1.7  2006/10/31 16:52:14  mschneider
 Cleanup. Removed out commented #show().

 Revision 1.6  2006/10/06 14:16:09  mschneider
 Removed BOM (Byte Order Mark).

 Revision 1.5  2006/10/05 10:11:41  mschneider
 Javadoc fixes.

 Revision 1.4  2006/09/26 16:45:44  mschneider
 Javadoc corrections + fixed warnings.

 Revision 1.3  2006/09/19 14:57:09  mschneider
 Fixed warnings, improved javadoc.

 Revision 1.2  2006/08/21 16:42:36  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.1  2006/05/12 15:48:49  mschneider
 Initial version.

 ********************************************************************** */