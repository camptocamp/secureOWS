//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/AbstractRequestHandler.java,v 1.38 2006/11/15 18:38:18 mschneider Exp $
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
package org.deegree.io.datastore.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.sql.wherebuilder.WhereBuilder;
import org.deegree.model.filterencoding.Filter;

/**
 * This abstract class implements common SQL functionality needed by request handlers for SQL based
 * datastores.
 * 
 * @see org.deegree.io.datastore.sql.QueryHandler
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.38 $, $Date: 2006/11/15 18:38:18 $
 */
public class AbstractRequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractRequestHandler.class );

    protected static final String FT_COLUMN = "featuretype";

    protected static final String FT_PREFIX = "ft_";

    protected AbstractSQLDatastore datastore;

    protected TableAliasGenerator aliasGenerator;

    protected Connection conn;

    /**
     * Creates a new instance of <code>AbstractRequestHandler</code> from the given parameters.
     * 
     * @param ds
     * @param aliasGenerator
     * @param conn
     */
    public AbstractRequestHandler( AbstractSQLDatastore ds, TableAliasGenerator aliasGenerator,
                                  Connection conn ) {
        this.datastore = ds;
        this.aliasGenerator = aliasGenerator;
        this.conn = conn;
    }

    /**
     * Determines the feature ids that are matched by the given filter.
     * 
     * @param ft
     * @param filter
     * @return the feature ids that are matched by the given filter.
     * @throws DatastoreException
     */
    public List<FeatureId> determineAffectedFIDs( MappedFeatureType ft, Filter filter )
                            throws DatastoreException {

        TableAliasGenerator aliasGenerator = new TableAliasGenerator();
        VirtualContentProvider vcProvider = new VirtualContentProvider( filter, this.datastore,
                                                                        this.conn );
        WhereBuilder whereBuilder = this.datastore.getWhereBuilder( ft, filter, null,
                                                                    aliasGenerator, vcProvider );

        // if no filter is given
        StatementBuffer query = buildInitialFIDSelect( ft, whereBuilder );
        LOG.logDebug( "Determine affected feature id query: '" + query + "'" );

        List<FeatureId> fids = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            fids = extractFeatureIds( rs, ft );
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error while determining affected features of type: '"
                                          + ft.getName() + "': " + e.getMessage() );
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
     * Determines the feature ids for the subfeatures contained in the given feature property.
     * 
     * @param fid
     * @param pt
     * @return the matched feature ids
     * @throws DatastoreException
     */
    public List<FeatureId> determineAffectedFIDs( FeatureId fid, MappedFeaturePropertyType pt )
                            throws DatastoreException {

        TableRelation[] relations = pt.getTableRelations();

        this.aliasGenerator.reset();
        String[] tableAliases = this.aliasGenerator.generateUniqueAliases( relations.length + 1 );
        // toTable from last relation is target feature type table
        String tableAlias = tableAliases[tableAliases.length - 1];
        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        appendFeatureIdColumns( pt.getFeatureTypeReference().getFeatureType(), tableAlias, query );
        query.append( " FROM " );
        query.append( relations[0].getFromTable() );
        query.append( " " );
        query.append( tableAliases[0] );
        // append joins
        for ( int i = 0; i < relations.length; i++ ) {
            query.append( " JOIN " );
            query.append( relations[i].getToTable() );
            query.append( " " );
            query.append( tableAliases[i + 1] );
            query.append( " ON " );
            MappingField[] fromFields = relations[i].getFromFields();
            MappingField[] toFields = relations[i].getToFields();
            for ( int j = 0; j < fromFields.length; j++ ) {
                query.append( tableAliases[i] );
                query.append( '.' );
                query.append( fromFields[j].getField() );
                query.append( '=' );
                query.append( tableAliases[i + 1] );
                query.append( '.' );
                query.append( toFields[j].getField() );
            }
        }
        query.append( " WHERE " );
        MappingField[] fidFields = fid.getFidDefinition().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            query.append( tableAliases[0] );
            query.append( '.' );
            query.append( fidFields[i].getField() );
            query.append( "=?" );
            query.addArgument( fid.getValue( i ), fidFields[i].getType() );
            if ( i != fidFields.length - 1 ) {
                query.append( " AND " );
            }
        }

        List<FeatureId> fids = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            fids = extractFeatureIds( rs, pt.getFeatureTypeReference().getFeatureType() );
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
     * Builds the initial SELECT statement that retrieves the feature ids that are matched by the
     * given <code>WhereBuilder</code>.
     * <p>
     * The statement is structured like this:
     * <ul>
     * <li><code>SELECT</code></li>
     * <li>comma-separated list of qualified fid fields</li>
     * <li><code>FROM</code></li>
     * <li>comma-separated list of tables and their aliases (this is needed to constrain the paths
     * to selected XPath-PropertyNames)</li>
     * <li><code>WHERE</code></li>
     * <li>SQL representation of the Filter expression</li>
     * </ul>
     *
     * @param rootFeatureType
     * @param whereBuilder
     * @return initial SELECT statement to retrieve the feature ids
     * @throws DatastoreException 
     */
    private StatementBuffer buildInitialFIDSelect( MappedFeatureType rootFeatureType,
                                                  WhereBuilder whereBuilder )
                            throws DatastoreException {

        String tableAlias = whereBuilder.getRootTableAlias();
        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        appendFeatureIdColumns( rootFeatureType, tableAlias, query );
        query.append( " FROM " );
        whereBuilder.appendJoinTableList( query );
        whereBuilder.appendWhereCondition( query );
        return query;
    }

    /**
     * Appends the alias qualified columns that make up the feature id to the given query.
     * 
     * @param featureType
     * @param tableAlias
     * @param query
     */
    protected void appendFeatureIdColumns( MappedFeatureType featureType, String tableAlias,
                                          StatementBuffer query ) {
        MappingField[] fidFields = featureType.getGMLId().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            query.append( tableAlias );
            query.append( '.' );
            query.append( fidFields[i].getField() );
            if ( i != fidFields.length - 1 ) {
                query.append( ',' );
            }
        }
    }

    /**
     * Extracts the feature ids in the given {@link ResultSet} as a {@List} of {@FeatureId}s.
     * 
     * @param rs
     * @param featureType
     * @return feature ids
     * @throws SQLException
     */
    protected List<FeatureId> extractFeatureIds( ResultSet rs, MappedFeatureType featureType )
                            throws SQLException {
        List<FeatureId> featureIdList = new ArrayList<FeatureId>();
        MappedGMLId gmlId = featureType.getGMLId();
        MappingField[] idFields = gmlId.getIdFields();
        while ( rs.next() ) {
            Object[] idValues = new Object[idFields.length];
            for ( int i = 0; i < idValues.length; i++ ) {
                idValues[i] = rs.getObject( i + 1 );
            }
            featureIdList.add( new FeatureId( gmlId, idValues ) );
        }
        return featureIdList;
    }

    protected void appendJoins( TableRelation[] tableRelation, String fromAlias,
                               String[] toAliases, StatementBuffer query ) {
        for ( int i = 0; i < toAliases.length; i++ ) {
            String toAlias = toAliases[i];
            appendJoin( tableRelation[i], fromAlias, toAlias, query );
            fromAlias = toAlias;
        }
    }

    private void appendJoin( TableRelation tableRelation, String fromAlias, String toAlias,
                            StatementBuffer query ) {
        query.append( " JOIN " );
        query.append( tableRelation.getToTable() );
        query.append( " " );
        query.append( toAlias );
        query.append( " ON " );

        MappingField[] fromFields = tableRelation.getFromFields();
        MappingField[] toFields = tableRelation.getToFields();
        for ( int i = 0; i < fromFields.length; i++ ) {
            query.append( toAlias );
            query.append( "." );
            query.append( toFields[i].getField() );
            query.append( "=" );
            query.append( fromAlias );
            query.append( "." );
            query.append( fromFields[i].getField() );
            if ( i != fromFields.length - 1 ) {
                query.append( " AND " );
            }
        }
    }

    /**
     * Appends the specified columns as a comma-separated list to the given query.
     * 
     * @param query
     *            StatementBuffer that the list is appended to
     * @param columns
     *            array of column names
     */
    public void appendColumnsList( StatementBuffer query, String[] columns ) {
        for ( int i = 0; i < columns.length; i++ ) {
            if ( columns[i].indexOf( '$' ) != -1 ) {
                // function call
                String column = columns[i];
                column = column.replaceAll( "\\$\\.", "" );
                query.append( column );

            } else {
                query.append( columns[i] );
            }

            if ( i != columns.length - 1 ) {
                query.append( ',' );
            }
        }
    }

    /**
     * Appends the specified columns as alias-qualified, comma-separated list to the given query.
     * 
     * @param query
     *            StatementBuffer that the list is appended to
     * @param tableAlias
     *            alias to use as qualifier (alias.field)
     * @param columns
     *            array of column names
     */
    public void appendQualifiedColumnsList( StatementBuffer query, String tableAlias,
                                           String[] columns ) {
        for ( int i = 0; i < columns.length; i++ ) {
            appendQualifiedColumn( query, tableAlias, columns[i] );
            if ( i != columns.length - 1 ) {
                query.append( ',' );
            }
        }
    }

    /**
     * Appends the specified column to the given query.
     * 
     * @param query
     *            StatementBuffer that the list is appended to
     * @param tableAlias
     *            alias to use as qualifier (alias.field)
     * @param column
     *            column name
     */
    public void appendQualifiedColumn( StatementBuffer query, String tableAlias, String column ) {
        query.append( tableAlias );
        query.append( '.' );
        query.append( column );
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: AbstractRequestHandler.java,v $
 * Revision 1.38  2006/11/15 18:38:18  mschneider
 * Changed signatures to allow the correct chaining of DatastoreExceptions.
 *
 * Revision 1.37  2006/11/02 14:29:11  mschneider
 * Javadoc / formatting fixes.
 *
 * Revision 1.36  2006/09/19 14:54:02  mschneider
 * Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.
 *
 * Revision 1.35  2006/09/14 00:18:08  mschneider
 * Fixed exception message chaining.
 *
 * Revision 1.34  2006/09/07 17:05:38  mschneider
 * Added quirks in #appendColumnsList(...) and #appendQualifiedColumnsList(...) to cope with "columns" that actually are SQL function calls.
 *
 * Revision 1.33  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.32  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.31  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.30  2006/08/21 15:45:42  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.29  2006/08/21 12:51:19  mschneider
 * Changed visibility of some methods. Improved javadoc.
 *
 * Revision 1.28  2006/08/14 16:52:06  mschneider
 * Adapted #getWhereBuilder() calls.
 *
 * Revision 1.27  2006/06/01 12:15:51  mschneider
 * Fixed imports.
 *
 * Revision 1.26  2006/05/29 16:36:53  mschneider
 * Removed moveToStartPosition().
 *
 * Revision 1.25  2006/05/26 09:42:41  poth
 * bug fix for supporting numberOfFeatures for returned FeatureCollections / footer correction
 *
 ************************************************************************************************** */