//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/Attic/DeleteHandler.java,v 1.21 2006/10/31 16:52:42 mschneider Exp $
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.TableRelation.FK_INFO;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.sql.AbstractRequestHandler;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcwebservices.wfs.operation.transaction.Delete;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;

/**
 * Handler for {@link Delete} operations (which are usually contained in
 * {@link Transaction} requests).
 * <p>
 * When a {@link Delete} operation is performed, the following actions are taken:
 * <ul>
 * <li>the {@link FeatureId}s of all (root) feature instances that match the associated
 * {@link Filter} are determined</li>
 * <li>the {@link DeleteGraph} is built; each of the affected root {@link FeatureId}s is a root
 * node in this graph</li>
 * <li>the {@link DeleteNode}s of the graph are sorted in topological order, i.e. they may be
 * deleted in that order without violating any foreign key constraints</li>
 * </ul>
 * 
 * @see DeleteGraph
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.21 $, $Date: 2006/10/31 16:52:42 $
 */
public class DeleteHandler extends AbstractRequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( DeleteHandler.class );

    private DeleteGraph deleteGraph = new DeleteGraph();

    /**
     * Creates a new <code>DeleteHandler</code> from the given parameters.
     * 
     * @param dsTa
     * @param aliasGenerator
     * @param conn
     * @throws DatastoreException
     */
    DeleteHandler( SQLTransaction dsTa, TableAliasGenerator aliasGenerator, Connection conn ) {
        super( dsTa.getDatastore(), aliasGenerator, conn );
    }

    /**
     * Deletes the features from the {@link Datastore} that are matched by the given filter and
     * type.
     * 
     * @param ft
     * @param filter
     * @return number of deleted feature instances
     * @throws DatastoreException
     */
    int performDelete( MappedFeatureType ft, Filter filter )
                            throws DatastoreException {

        List<FeatureId> fids = determineAffectedFIDs( ft, filter );

        for ( FeatureId fid : fids ) {
            DeleteNode rootNode = findOrAddDeleteNode( ft, fid );
            this.deleteGraph.markAsRootNode( rootNode );
        }

        try {
            for ( DeleteNode node : this.deleteGraph.getNodesInTopologicalOrder() ) {
                List<DeleteNode> referencingRows = getReferencingRows( node );
                if ( referencingRows.size() > 0 ) {
                    LOG.logDebug( "Skipping delete of " + node + ": " + referencingRows.size()
                                  + " reference(s) exist." );
                } else {
                    performDelete( node );
                }
            }
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error performing delete operation: " + e.getMessage() );
        }

        // return count of deleted (root) features
        return fids.size();
    }

    /**
     * Deletes the table entry from the SQL database that is represented by the given
     * {@link DeleteNode}.
     * 
     * @param node
     * @throws SQLException 
     */
    void performDelete( DeleteNode node )
                            throws SQLException {

        StringBuffer sql = new StringBuffer( "DELETE FROM " );
        sql.append( node.getTable() );
        sql.append( " WHERE " );
        boolean first = true;
        for ( String column : node.getKeyColumns() ) {
            if ( first ) {
                first = false;
            } else {
                sql.append( " AND " );
            }
            sql.append( column );
            sql.append( "=?" );
        }

        PreparedStatement stmt = null;
        try {
            LOG.logDebug( "Preparing delete: " + sql.toString() );
            stmt = this.conn.prepareStatement( sql.toString() );
            int i = 1;
            for ( String column : node.getKeyColumns() ) {
                Object value = node.getKeyColumnValue( column );
                LOG.logDebug( "Setting argument " + i + ": " + value + "(" + value.getClass() + ")" );
                stmt.setObject( i++, value );
            }
            stmt.execute();
            LOG.logDebug( "Performing: " + stmt.toString() );
        } catch ( SQLException e ) {
            String msg = "Error performing delete '" + stmt.toString() + "': " + e.getMessage();
            LOG.logError( msg, e );
            throw e;
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    String msg = "Error closing statement: " + e.getMessage();
                    LOG.logError( msg, e );
                }
            }
        }
    }

    /**
     * Finds/adds a subgraph to the {@link DeleteGraph} that represents the specified feature
     * instance (including it's properties and subfeatures). 
     * 
     * @param ft
     *            MappedFeatureType of the feature instance
     * @param fid
     *            FeatureId of the feature instance
     * @return node that represents the given feature instance in the DeleteGraph
     * @throws DatastoreException
     */
    private DeleteNode findOrAddDeleteNode( MappedFeatureType ft, FeatureId fid )
                            throws DatastoreException {

        DeleteNode node = this.deleteGraph.findNode( ft, fid );
        if ( node == null ) {
            node = this.deleteGraph.addNode( ft, fid );
            LOG.logDebug( "Adding feature with id '" + fid + "' and type '" + ft.getName() + "'..." );

            // add nodes for all properties stored in related tables
            PropertyType[] properties = ft.getProperties();
            for ( int i = 0; i < properties.length; i++ ) {
                MappedPropertyType pt = (MappedPropertyType) properties[i];
                if ( pt instanceof MappedSimplePropertyType
                     || pt instanceof MappedGeometryPropertyType ) {
                    TableRelation[] relations = pt.getTableRelations();
                    if ( relations.length == 1 ) {
                        LOG.logDebug( "Deleting property '" + pt.getName() + "'..." );
                        addPropertyNodes( node, relations[0] );
                    } else if ( relations.length > 1 ) {
                        String msg = Messages.getMessage( "DATASTORE_SQL_DELETE_UNSUPPORTED_JOIN" );
                        throw new DatastoreException( msg );
                    }
                    // if (relations.length == 0) property is deleted automatically
                } else if ( pt instanceof MappedFeaturePropertyType ) {
                    LOG.logDebug( "Deleting property '" + pt.getName() + "'..." );
                    addToDeleteGraph( node, fid, pt, (MappedFeaturePropertyType) pt );
                } else {
                    String msg = "Unsupported property type: '" + pt.getClass().getName() + "'";
                    assert false : msg;
                }
            }
        }
        return node;
    }

    /**
     * Adds nodes to the given {@link DeleteNode} that represent the simple/geometry properties in
     * the property table attached by the given {@link TableRelation}. 
     * 
     * @param sourceNode
     *            represents the feature that owns the properties
     * @param relation
     *            describes how the property table is joined to the sourceNode
     * @throws DatastoreException
     */
    private List<DeleteNode> addPropertyNodes( DeleteNode sourceNode, TableRelation relation )
                            throws DatastoreException {

        List<DeleteNode> relatedRows = new ArrayList<DeleteNode>();
        this.aliasGenerator.reset();
        String fromAlias = this.aliasGenerator.generateUniqueAlias();
        String toAlias = this.aliasGenerator.generateUniqueAlias();
        MappingField[] fromFields = relation.getFromFields();
        MappingField[] toFields = relation.getToFields();

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT DISTINCT " );
        for ( int i = 0; i < toFields.length; i++ ) {
            query.append( toAlias );
            query.append( "." );
            query.append( toFields[i].getField() );
            if ( i != toFields.length - 1 ) {
                query.append( ',' );
            }
        }
        query.append( " FROM " );
        query.append( sourceNode.getTable() );
        query.append( " " );
        query.append( fromAlias );
        query.append( " INNER JOIN " );
        query.append( relation.getToTable() );
        query.append( " " );
        query.append( toAlias );
        query.append( " ON " );
        for ( int j = 0; j < fromFields.length; j++ ) {
            query.append( fromAlias );
            query.append( '.' );
            query.append( fromFields[j].getField() );
            query.append( '=' );
            query.append( toAlias );
            query.append( '.' );
            query.append( toFields[j].getField() );
        }
        query.append( " WHERE " );
        int i = sourceNode.getKeyColumns().size();
        for ( String key : sourceNode.getKeyColumns() ) {
            Object keyValue = sourceNode.getKeyColumnValue( key );
            query.append( fromAlias );
            query.append( '.' );
            query.append( key );
            query.append( "=?" );
            // TODO get real type here
            query.addArgument( keyValue, Types.INTEGER );
            if ( --i != 0 ) {
                query.append( " AND " );
            }
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                Map<String, Object> keyColumns = new HashMap<String, Object>();
                for ( i = 0; i < toFields.length; i++ ) {
                    keyColumns.put( toFields[i].getField(), rs.getObject( i + 1 ) );
                }
                DeleteNode relatedRow = this.deleteGraph.findNode( relation.getToTable(),
                                                                   keyColumns );
                if ( relatedRow == null ) {
                    relatedRow = this.deleteGraph.addNode( relation.getToTable(), keyColumns );
                    relatedRows.add( relatedRow );
                }
                if ( relation.getFKInfo() == FK_INFO.fkIsFromField ) {
                    sourceNode.connect( relatedRow );
                } else {
                    relatedRow.connect( sourceNode );
                }
            }
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in addRelatedRows(): " + e.getMessage() );
        } finally {
            try {
                if ( rs != null ) {
                    try {
                        rs.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing result set: '" + e.getMessage() + "'.", e );
                    }
                }
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                    }
                }
            }
        }
        return relatedRows;
    }    
    
    /**
     * Adds the subgraph to the <code>deleteGraph</code> member variable that represents the
     * specified feature property instance (including it's subfeatures). findNode
     * 
     * @param node
     *            represents the feature that the property belongs to
     * @param fid
     *            feature id of the feature that the property belongs to
     * @param property 
     * @param content
     * @throws DatastoreException
     */
    private void addToDeleteGraph( DeleteNode node, FeatureId fid, MappedPropertyType property,
                                  MappedFeaturePropertyType content )
                            throws DatastoreException {
        MappedFeatureType subFeatureType = content.getFeatureTypeReference().getFeatureType();
        TableRelation[] relations = content.getTableRelations();
        if ( relations.length == 1 ) {
            List<FeatureId> subFeatureIds = determineAffectedFIDs( fid, content );
            for ( FeatureId subFid : subFeatureIds ) {
                DeleteNode childNode = null;
                if ( subFeatureType.isDeletable() ) {
                    childNode = findOrAddDeleteNode( subFeatureType, subFid );
                    if ( relations[0].getFKInfo() == FK_INFO.fkIsFromField ) {
                        // fk is stored in feature type table
                        node.connect( childNode );
                    } else if ( relations[0].getFKInfo() == FK_INFO.fkIsToField ) {
                        // fk is stored in sub feature type table
                        LOG.logInfo( "TODO: delete feature table entry" );
                        childNode.connect( node );
                    } else {
                        String msg = "Foreign key information missing for property '"
                                     + property.getName() + "'.";
                        throw new DatastoreException( msg );
                    }
                } else {
                    LOG.logDebug( "Skipping subfeature '" + subFeatureType.getName()
                                  + "': deletable=false." );
                }
            }
        } else if ( relations.length == 2 ) {
            List<DeleteNode> joinNodes = addToDeleteGraph( node, relations[0], relations[1] );
            for ( DeleteNode joinNode : joinNodes ) {
                List<FeatureId> subFeatureIds = determineAffectedFIDs( joinNode, subFeatureType,
                                                                       relations[1] );
                for ( FeatureId subFid : subFeatureIds ) {
                    if ( subFeatureType.isDeletable() ) {
                        DeleteNode childNode = findOrAddDeleteNode( subFeatureType, subFid );
                        if ( relations[1].getFKInfo() == FK_INFO.fkIsFromField ) {
                            joinNode.connect( childNode );
                        } else if ( relations[1].getFKInfo() == FK_INFO.fkIsToField ) {
                            childNode.connect( joinNode );
                        } else {
                            String msg = "Foreign key information missing for property '"
                                         + property.getName() + "'.";
                            throw new DatastoreException( msg );
                        }
                    } else {
                        LOG.logDebug( "Skipping subfeature '" + subFeatureType.getName()
                                      + "': deletable=false." );
                    }
                }
            }
        } else {
            String msg = "Deleting of Feature properties using " + relations.length
                         + " join relations are not supported.";
            throw new DatastoreException( msg );
        }
    }

    /**
     * Adds the nodes to the given <code>DeleteNode</code> that represent the entries in the 
     * property table that are attached by the given <code>TableRelations</code>. 
     * 
     * @param sourceNode
     *            represents the feature that the properties belong to
     * @param relation1
     *            describes how the join table is attached
     * @param relation2
     *            describes how the property table is joined
     * @throws DatastoreException
     */
    private List<DeleteNode> addToDeleteGraph( DeleteNode sourceNode, TableRelation relation1,
                                              TableRelation relation2 )
                            throws DatastoreException {

        List<DeleteNode> relatedRows = new ArrayList<DeleteNode>();
        this.aliasGenerator.reset();
        String fromAlias = this.aliasGenerator.generateUniqueAlias();
        String toAlias = this.aliasGenerator.generateUniqueAlias();
        MappingField[] fromFields = relation1.getFromFields();
        MappingField[] fromFields2 = relation2.getFromFields();
        MappingField[] toFields = relation1.getToFields();

        // need to select from fields from second relation element as well
        MappingField[] selectFields = new MappingField[toFields.length + fromFields2.length];
        for ( int i = 0; i < toFields.length; i++ ) {
            selectFields[i] = toFields[i];
        }
        for ( int i = 0; i < fromFields2.length; i++ ) {
            selectFields[i + toFields.length] = fromFields2[i];
        }

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT DISTINCT " );
        for ( int i = 0; i < selectFields.length; i++ ) {
            query.append( toAlias );
            query.append( "." );
            query.append( selectFields[i].getField() );
            if ( i != selectFields.length - 1 ) {
                query.append( ',' );
            }
        }
        query.append( " FROM " );
        query.append( relation1.getFromTable() );
        query.append( " " );
        query.append( fromAlias );
        query.append( " INNER JOIN " );
        query.append( relation1.getToTable() );
        query.append( " " );
        query.append( toAlias );
        query.append( " ON " );
        for ( int j = 0; j < fromFields.length; j++ ) {
            query.append( fromAlias );
            query.append( '.' );
            query.append( fromFields[j].getField() );
            query.append( '=' );
            query.append( toAlias );
            query.append( '.' );
            query.append( toFields[j].getField() );
        }
        query.append( " WHERE " );
        int i = sourceNode.getKeyColumns().size();
        for ( String key : sourceNode.getKeyColumns() ) {
            Object keyValue = sourceNode.getKeyColumnValue( key );
            query.append( fromAlias );
            query.append( '.' );
            query.append( key );
            query.append( "=?" );
            // TODO use real type here
            query.addArgument( keyValue, Types.INTEGER );
            if ( --i != 0 ) {
                query.append( " AND " );
            }
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                Map<String, Object> keyColumns = new HashMap<String, Object>();
                for ( i = 0; i < selectFields.length; i++ ) {
                    keyColumns.put( selectFields[i].getField(), rs.getObject( i + 1 ) );
                }
                DeleteNode relatedRow = this.deleteGraph.findNode( relation1.getToTable(),
                                                                   keyColumns );
                if ( relatedRow == null ) {
                    relatedRow = this.deleteGraph.addNode( relation1.getToTable(), keyColumns );
                    relatedRows.add( relatedRow );
                }
                if ( relation1.getFKInfo() == FK_INFO.fkIsFromField ) {
                    sourceNode.connect( relatedRow );
                } else {
                    relatedRow.connect( sourceNode );
                }
            }
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in addRelatedRows(): " + e.getMessage() );
        } finally {
            try {
                if ( rs != null ) {
                    try {
                        rs.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing result set: '" + e.getMessage() + "'.", e );
                    }
                }
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                    }
                }
            }
        }
        return relatedRows;
    }

    /**
     * Determines the feature ids for the selected subfeatures.
     * 
     * @param joinNode
     * @param subFeatureType
     * @param relation
     * @throws DatastoreException
     */
    private List<FeatureId> determineAffectedFIDs( DeleteNode joinNode,
                                                  MappedFeatureType subFeatureType,
                                                  TableRelation relation )
                            throws DatastoreException {

        this.aliasGenerator.reset();
        String fromTableAlias = this.aliasGenerator.generateUniqueAlias();
        String toTableAlias = this.aliasGenerator.generateUniqueAlias();

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT DISTINCT " );
        appendFeatureIdColumns( subFeatureType, toTableAlias, query );
        query.append( " FROM " );
        query.append( relation.getFromTable() );
        query.append( " " );
        query.append( fromTableAlias );
        // append join
        query.append( " JOIN " );
        query.append( relation.getToTable() );
        query.append( " " );
        query.append( toTableAlias );
        query.append( " ON " );
        MappingField[] fromFields = relation.getFromFields();
        MappingField[] toFields = relation.getToFields();
        for ( int j = 0; j < fromFields.length; j++ ) {
            query.append( fromTableAlias );
            query.append( '.' );
            query.append( fromFields[j].getField() );
            query.append( '=' );
            query.append( toTableAlias );
            query.append( '.' );
            query.append( toFields[j].getField() );
        }
        query.append( " WHERE " );
        int i = joinNode.getKeyColumns().size();
        for ( String key : joinNode.getKeyColumns() ) {
            Object keyValue = joinNode.getKeyColumnValue( key );
            query.append( fromTableAlias );
            query.append( '.' );
            query.append( key );
            query.append( "=?" );
            // TODO use real type here
            query.addArgument( keyValue, Types.INTEGER );
            if ( --i != 0 ) {
                query.append( " AND " );
            }
        }

        List<FeatureId> fids = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            fids = extractFeatureIds( rs, subFeatureType );
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in determineAffectedFIDs(): " + e.getMessage() );
        } finally {
            try {
                if ( rs != null ) {
                    try {
                        rs.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing result set: '" + e.getMessage() + "'.", e );
                    }
                }
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                    }
                }
            }
        }
        return fids;
    }

    /**
     * Returns all table rows that reference the given table row (DeleteNode).
     * 
     * @param node
     * @return all table rows that reference the given table row.
     * @throws DatastoreException 
     */
    List<DeleteNode> getReferencingRows( DeleteNode node )
                            throws DatastoreException {

        List<DeleteNode> rows = new ArrayList<DeleteNode>();
        for ( TableReference tableReference : getReferencingTables( node.getTable() ) ) {
            rows.addAll( getReferencingRows( node, tableReference ) );
        }
        return rows;
    }

    /**
     * Returns all stored rows (as {@link DeleteNode}s) that reference the given row
     * ({@link DeleteNode}) via the also given reference relation.
     * 
     * @param node
     * @param ref
     * @return all stored rows that reference the given row
     * @throws DatastoreException
     */
    private List<DeleteNode> getReferencingRows( DeleteNode node, TableReference ref )
                            throws DatastoreException {

        List<DeleteNode> referencingRows = new ArrayList<DeleteNode>();
        this.aliasGenerator.reset();
        String fromAlias = this.aliasGenerator.generateUniqueAlias();
        String toAlias = this.aliasGenerator.generateUniqueAlias();
        MappingField[] fromFields = ref.getFkColumns();
        MappingField[] toFields = ref.getKeyColumns();

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT DISTINCT " );
        for ( int i = 0; i < fromFields.length; i++ ) {
            query.append( fromAlias );
            query.append( "." );
            query.append( fromFields[i].getField() );
            if ( i != fromFields.length - 1 ) {
                query.append( ',' );
            }
        }
        query.append( " FROM " );
        query.append( ref.getFromTable() );
        query.append( " " );
        query.append( fromAlias );
        query.append( " INNER JOIN " );
        query.append( ref.getToTable() );
        query.append( " " );
        query.append( toAlias );
        query.append( " ON " );
        for ( int j = 0; j < fromFields.length; j++ ) {
            query.append( fromAlias );
            query.append( '.' );
            query.append( fromFields[j].getField() );
            query.append( '=' );
            query.append( toAlias );
            query.append( '.' );
            query.append( toFields[j].getField() );
        }
        query.append( " WHERE " );
        int i = node.getKeyColumns().size();
        for ( String key : node.getKeyColumns() ) {
            Object keyValue = node.getKeyColumnValue( key );
            query.append( toAlias );
            query.append( '.' );
            query.append( key );
            query.append( "=?" );
            // TODO get real type here
            query.addArgument( keyValue, Types.INTEGER );
            if ( --i != 0 ) {
                query.append( " AND " );
            }
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                Map<String, Object> keyColumns = new HashMap<String, Object>();
                for ( i = 0; i < fromFields.length; i++ ) {
                    keyColumns.put( fromFields[i].getField(), rs.getObject( i + 1 ) );
                }
                DeleteNode referencingRow = new DeleteNode( ref.getFromTable(), keyColumns );
                referencingRows.add( referencingRow );
            }
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in addRelatedRows(): " + e.getMessage() );
        } finally {
            try {
                if ( rs != null ) {
                    try {
                        rs.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing result set: '" + e.getMessage() + "'.", e );
                    }
                }
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                    }
                }
            }
        }
        return referencingRows;
    }

    /**
     * Returns all tables that reference the given table.
     * 
     * TODO cache search
     * 
     * @param table
     * @return all tables that reference the given table.
     */
    private List<TableReference> getReferencingTables( String table ) {

        List<TableReference> tables = new ArrayList<TableReference>();
        MappedGMLSchema[] schemas = this.datastore.getSchemas();
        for ( int i = 0; i < schemas.length; i++ ) {
            MappedGMLSchema schema = schemas[i];
            FeatureType[] fts = schema.getFeatureTypes();
            for ( int j = 0; j < fts.length; j++ ) {
                MappedFeatureType ft = (MappedFeatureType) fts[j];
                PropertyType[] properties = ft.getProperties();
                for ( int k = 0; k < properties.length; k++ ) {
                    tables.addAll( getReferencingTables( (MappedPropertyType) properties[k], table ) );
                }
            }
        }
        return tables;
    }

    /**
     * Returns all tables that reference the given table that are defined in the mapping of the
     * given property type.
     * 
     * @param property
     * @param table
     * @return all tables that reference the given table.
     */
    private List<TableReference> getReferencingTables( MappedPropertyType property, String table ) {

        List<TableReference> tables = new ArrayList<TableReference>();
        TableRelation[] relations = property.getTableRelations();
        for ( int j = 0; j < relations.length; j++ ) {
            TableReference ref = new TableReference( relations[j] );
            if ( ref.getToTable().equals( table ) ) {
                tables.add( ref );
            }
        }
        return tables;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: DeleteHandler.java,v $
 * Revision 1.21  2006/10/31 16:52:42  mschneider
 * Removed references to DeleteGraph#show().
 *
 * Revision 1.20  2006/10/06 14:16:09  mschneider
 * Removed BOM (Byte Order Mark).
 *
 * Revision 1.19  2006/10/05 10:12:33  mschneider
 * Javadoc fixes. Cleanup.
 *
 * Revision 1.18  2006/09/19 14:57:01  mschneider
 * Fixed warnings, improved javadoc.
 *
 * Revision 1.17  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.16  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.15  2006/08/21 15:46:53  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.14  2006/06/29 10:25:35  mschneider
 * Added more logging.
 *
 * Revision 1.13  2006/05/29 16:38:45  mschneider
 * Changed visibility of some methods to allow UpdateHandler to call them as well.
 *
 * Revision 1.12  2006/05/24 15:24:05  mschneider
 * Changed return types from FeatureId[] to List<FeatureId>.
 *
 * Revision 1.11  2006/05/18 15:46:37  mschneider
 * Changed constructor: use SQLTransaction instead of AbstractSQLDatastore.
 *
 * Revision 1.10  2006/05/17 16:46:10  mschneider
 * Cleanup.
 *
 * Revision 1.9  2006/05/16 13:21:56  poth
 * Debug statement added
 *
 * Revision 1.8  2006/05/15 16:13:11  mschneider
 * Added handling for delete=false (to prevent feature types from being deleted).
 *
 * Revision 1.7  2006/05/12 15:49:16  mschneider
 * Works now (partially).
 * 
 * Revision 1.6  2006/04/25 12:18:33  mschneider Testing code formatter and cvs log.
 *
 * Revision 1.5 2006/04/25 12:13:40 mschneider Testing code formatter and cvs log.
 * 
 * Revision 1.4 2006/04/18 12:46:53 mschneider Adapted to cope with DatastoreException from AbstractSQLDatastore.prepareStatement().
 * 
 * Revision 1.3 2006/04/06 20:25:27 poth ** empty log message ***
 * 
 * Revision 1.2 2006/03/28 13:38:22 mschneider Added debug messages.
 * 
 * Revision 1.1 2006/02/13 17:54:55 mschneider Initial version.
 * 
 **************************************************************************************************/