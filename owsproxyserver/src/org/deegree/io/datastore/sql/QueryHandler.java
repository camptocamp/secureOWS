//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/QueryHandler.java,v 1.85 2006/11/27 09:07:53 poth Exp $
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.PropertyPathResolver;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.wherebuilder.QueryTableTree;
import org.deegree.io.datastore.sql.wherebuilder.WhereBuilder;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.XLinkedFeatureProperty;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;

/**
 * Handles {@link Query} requests to SQL backed datastores.
 * 
 * @see FeatureFetcher
 * @see AbstractSQLDatastore
 * @see QueryTableTree
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.85 $, $Date: 2006/11/27 09:07:53 $
 */
public class QueryHandler extends FeatureFetcher {

    private static final ILogger LOG = LoggerFactory.getLogger( QueryHandler.class );

    // requested feature type
    protected MappedFeatureType rootFT;

    // requested properties of the feature type
    protected PropertyPath[] propertyNames;

    // used to build the initial SELECT (especially the WHERE-clause)
    protected WhereBuilder whereBuilder;

    /**
     * Creates a new instance of <code>QueryHandler</code> from the given parameters.
     *
     * @param ds
     *            datastore that spawned this QueryHandler
     * @param aliasGenerator
     *            used to generate unique aliases for the tables in the SELECT statements
     * @param conn
     *            JDBCConnection to execute the generated SELECT statements against
     * @param rootFeatureType
     *            queried feature type
     * @param query
     *            query to perform
     * @throws DatastoreException
     */
    public QueryHandler( AbstractSQLDatastore ds, TableAliasGenerator aliasGenerator,
                        Connection conn, MappedFeatureType rootFeatureType, Query query )
                            throws DatastoreException {
        super( ds, aliasGenerator, conn, query );
        this.rootFT = rootFeatureType;
        this.propertyNames = PropertyPathResolver.normalizePropertyPaths( rootFeatureType,
                                                                          query.getPropertyNames() );
        this.vcProvider = new VirtualContentProvider( query.getFilter(), ds, conn );
        this.whereBuilder = this.datastore.getWhereBuilder( rootFeatureType, query.getFilter(),
                                                            query.getSortProperties(),
                                                            aliasGenerator, this.vcProvider );
        this.aliasGenerator = aliasGenerator;
        this.query = query;
    }

    /**
     * Performs the associated {@link Query} against the datastore.
     *
     * @return collection of requested features
     * @throws SQLException
     *             if a JDBC error occurs
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    public FeatureCollection performQuery()
                            throws SQLException, DatastoreException, UnknownCRSException {

        long start = -1;
        if (LOG.getLevel() == ILogger.LOG_DEBUG) {
            start = System.currentTimeMillis();
        }
        
        FeatureCollection result = null;

        if ( this.query.getResultType() == RESULT_TYPE.HITS ) {
            result = performHitsQuery();
        } else {
            result = performResultsQuery();
        }

        if (LOG.getLevel() == ILogger.LOG_DEBUG) {
            long elapsed = System.currentTimeMillis() - start;
            LOG.logDebug("Performing of query took " + elapsed + " milliseconds.");
        }        
        
        return result;
    }

    /**
     * Performs a query for the feature instances that match the query constraints. This
     * corresponds to a query with resultType=RESULTS.
     *
     * @return collection of requested features
     * @throws PropertyPathResolvingException
     * @throws SQLException
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    private FeatureCollection performResultsQuery()
                            throws PropertyPathResolvingException, SQLException, DatastoreException, UnknownCRSException {

        FeatureCollection result = FeatureFactory.createFeatureCollection( "ID", 10000 );

        // determine properties to fetch
        Map<MappedPropertyType, Collection<PropertyPath>> requestedPropertyMap = PropertyPathResolver.determineFetchProperties(
                                                                                                                                this.rootFT,
                                                                                                                                this.propertyNames );
        MappedPropertyType[] requestedProps = new MappedPropertyType[requestedPropertyMap.size()];
        requestedProps = requestedPropertyMap.keySet().toArray( requestedProps );

        // determine contents (fields / functions) that must be SELECTed from root table
        List<List<SimpleContent>> fetchContents = determineFetchContents( this.rootFT,
                                                                          requestedProps );
        Map<SimpleContent, Integer> resultPosMap = buildResultPosMap( fetchContents );

        // build initial SQL query
        StatementBuffer querybuf = buildInitialSelect( fetchContents );
        LOG.logDebug( "Initial query: '" + querybuf + "'" );

        Object[] resultValues = new Object[fetchContents.size()];
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // used to handle that a feature may occur several times in result set
        Set<FeatureId> rootFeatureIds = new HashSet<FeatureId>();
        stmt = this.datastore.prepareStatement( this.conn, querybuf );

        try {
            rs = stmt.executeQuery();

            // skip features in resultSet (startPosition is first feature to be included)
            int startPosition = this.query.getStartPosition();
            Set<FeatureId> skippedFeatures = new HashSet<FeatureId>();
            while ( skippedFeatures.size() < startPosition - 1 && rs.next() ) {
                LOG.logDebug( "Skipping result row." );
                // collect result values
                for ( int i = 0; i < resultValues.length; i++ ) {
                    resultValues[i] = rs.getObject( i + 1 );
                }
                FeatureId fid = extractFeatureId( this.rootFT, resultPosMap, resultValues );
                skippedFeatures.add( fid );
            }

            int maxFeatures = this.query.getMaxFeatures();
            while ( rs.next() ) {

                // already maxFeature features extracted?
                if ( maxFeatures != -1 && rootFeatureIds.size() == maxFeatures ) {
                    break;
                }

                // collect result values
                for ( int i = 0; i < resultValues.length; i++ ) {
                    resultValues[i] = rs.getObject( i + 1 );
                }
                FeatureId fid = extractFeatureId( this.rootFT, resultPosMap, resultValues );

                // skip it if this root feature has already been fetched or if it is a feature
                // (again) that has been skipped
                if ( !rootFeatureIds.contains( fid ) && !skippedFeatures.contains( fid ) ) {

                    rootFeatureIds.add( fid );

                    // feature may have been fetched as a subfeature already
                    Feature feature = this.featureMap.get( fid );
                    if ( feature == null ) {
                        feature = extractFeature( fid, this.rootFT, requestedPropertyMap,
                                                  resultPosMap, resultValues );
                    }
                    result.add( feature );
                }
            }
        } finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
            } finally {
                if ( stmt != null ) {
                    stmt.close();
                }
            }
        }
        resolveXLinks();
        result.setAttribute( "numberOfFeatures", "" + result.size() );
        return result;
    }

    /**
     * Performs a query for the number feature instances that match the query constraints. This
     * corresponds to a query with resultType=HITS.
     *
     * @return a feature collection containing number of features that match the query constraints
     * @throws SQLException
     * @throws DatastoreException
     */
    private FeatureCollection performHitsQuery()
                            throws SQLException, DatastoreException {

        FeatureCollection result = FeatureFactory.createFeatureCollection( "ID", 2 );

        String tableAlias = this.whereBuilder.getRootTableAlias();
        String field = this.rootFT.getGMLId().getIdFields()[0].getField();
        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT COUNT( DISTINCT " );
        query.append( tableAlias + '.' + field );
        query.append( ") FROM " );

        whereBuilder.appendJoinTableList( query );
        whereBuilder.appendWhereCondition( query );
        LOG.logDebug( "Count query: '" + query + "'" );

        ResultSet rs = null;
        PreparedStatement stmt = this.datastore.prepareStatement( this.conn, query );
        try {
            rs = stmt.executeQuery();
            if ( rs.next() ) {
                result.setAttribute( "numberOfFeatures", rs.getObject( 1 ).toString() );
            } else {
                LOG.logError( "Internal error. Count result is empty (no rows)." );
                throw new SQLException();
            }
        } catch ( SQLException e ) {
            throw new SQLException( "Error performing count (HITS) query: " + query );
        } finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
            } finally {
                if ( stmt != null ) {
                    stmt.close();
                }
            }
        }
        return result;
    }

    protected void resolveXLinks() {
        for ( XLinkedFeatureProperty property : this.xlinkProperties ) {
            Feature feature = this.featureMap.get( property.getTargetFeatureId() );
            assert feature != null;
            property.setValue( feature );
        }
    }

    /**
     * Builds the initial SELECT statement.
     * <p>
     * This statement determines all feature ids that are affected by the filter, but also SELECTs
     * all properties that are stored in the root feature type's table (to improve efficiency).
     * </p>
     * <p>
     * The statement is structured like this:
     * <ul>
     * <li><code>SELECT</code></li>
     * <li>comma-separated list of qualified columns/functions to fetch</li>
     * <li><code>FROM</code></li>
     * <li>comma-separated list of tables and their aliases (this is needed to constrain the paths
     * to selected XPath-PropertyNames)</li>
     * <li><code>WHERE</code></li>
     * <li>SQL representation of the Filter expression</li>
     * <li><code>ORDER BY</code></li>
     * <li>qualified sort criteria columns/functions</li>
     * </ul>
     * </p>
     *
     * @param fetchContents
     *            contents (columns/functions) to be fetched from the root database table
     * @return initial select statement
     * @throws DatastoreException
     */
    protected StatementBuffer buildInitialSelect( List<List<SimpleContent>> fetchContents )
                            throws DatastoreException {

        String tableAlias = this.whereBuilder.getRootTableAlias();
        StatementBuffer stmt = new StatementBuffer();

        stmt.append( "SELECT " );
        appendQualifiedContentList( stmt, tableAlias, fetchContents );
        stmt.append( " FROM " );

        whereBuilder.appendJoinTableList( stmt );
        whereBuilder.appendWhereCondition( stmt );
        whereBuilder.appendOrderByCondition( stmt );

        return stmt;
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: QueryHandler.java,v $
 * Revision 1.85  2006/11/27 09:07:53  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.84  2006/11/17 16:57:04  mschneider
 * Added profiling information (for loglevel DEBUG).
 *
 * Revision 1.83  2006/11/09 17:40:52  mschneider
 * Moved query member variable here.
 *
 * Revision 1.82  2006/11/02 14:29:11  mschneider
 * Javadoc / formatting fixes.
 *
 * Revision 1.81  2006/10/10 16:41:01  mschneider
 * Fixed problem with startPosition. Sometimes an already skipped feature made it into the result feature collection.
 *
 * Revision 1.80  2006/10/10 15:52:56  mschneider
 * Fixed handling of startPosition.
 *
 * Revision 1.79  2006/09/27 14:15:13  mschneider
 * Simplified #resolveXLinks().
 *
 * Revision 1.78  2006/09/19 14:54:02  mschneider
 * Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.
 *
 * Revision 1.77  2006/09/13 18:27:46  mschneider
 * Added some hacks to make virtual properties work that use the value of the query-bbox.
 *
 * Revision 1.76  2006/09/11 15:05:29  mschneider
 * Fixed array bug when SQLFunctionCalls are used.
 *
 * Revision 1.75  2006/09/07 17:07:09  mschneider
 * Added some basic handling of properties that use SQLFunctionCalls as to compute their value.
 *
 * Revision 1.74  2006/09/05 14:43:24  mschneider
 * Adapted due to merging of messages.
 *
 * Revision 1.73  2006/08/28 16:41:06  mschneider
 * Javadoc fixes.
 *
 * Revision 1.72  2006/08/23 16:34:36  mschneider
 * Added handling of virtual properties.
 *
 * Revision 1.71  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.70  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.69  2006/08/21 15:46:05  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.68  2006/08/15 17:40:14  mschneider
 * Moved generation of SORT BY condition to WhereBuilder.
 *
 * Revision 1.67  2006/08/14 16:51:29  mschneider
 * Improved javadoc.
 *
 * Revision 1.66  2006/08/14 13:34:23  mschneider
 * Javadoc corrections.
 *
 * Revision 1.65  2006/08/06 20:51:36  poth
 * *** empty log message ***
 *
 * Revision 1.64  2006/07/26 18:55:38  mschneider
 * Fixed spelling in method name.
 *
 * Revision 1.63  2006/07/25 06:22:40  poth
 * *** empty log message ***
 *
 * Revision 1.62  2006/07/23 10:07:46  poth
 * simple sorting algorithm added
 *
 * Revision 1.61  2006/06/01 15:21:20  mschneider
 * Renamed PropertyPathResolver.determineRequestedProperties() to PropertyPathResolver.determineFetchProperties().
 *
 * Revision 1.60  2006/06/01 13:10:02  mschneider
 * Added use of Generics for type safety.
 *
 * Revision 1.59  2006/06/01 12:40:16  mschneider
 * Added use of Generics for type safety.
 *
 * Revision 1.58  2006/05/29 16:38:05  mschneider
 * Fixed bug concerning startPosition attribute. Result was wrong when features were present in the SQL result set more than once.
 *
 * Revision 1.57  2006/05/26 09:42:41  poth
 * bug fix for supporting numberOfFeatures for returned FeatureCollections / footer correction
 *
 * Revision 1.56  2006/05/12 15:48:17  mschneider
 * Removed unnecessary whitespace from SELECT statement.
 *
 * Revision 1.55  2006/05/10 07:59:10  mschneider
 * Fixed regression that caused result sets to contain a root feature several times.
 *
 * Revision 1.54  2006/05/01 20:15:27  poth
 * *** empty log message ***
 *
 * Revision 1.53  2006/04/28 07:52:26  poth
 * *** empty log message ***
 *
 * Revision 1.52  2006/04/27 14:54:43  poth
 * *** empty log message ***
 *
 * Revision 1.51  2006/04/18 14:40:03  mschneider
 * Improved count query. Uses table alias now (instead of table name).
 *
 * Revision 1.50  2006/04/18 13:45:17  mschneider
 * Fixed problem with XLinkedFeatureProperties which have a type that has more than one possible substitutions. Fixed problem with multi geometry properties (cs was not set). Thanks, Edward.
 *
 * Revision 1.49  2006/04/18 12:46:32  mschneider
 * Adapted to cope with DatastoreException from AbstractSQLDatastore.prepareStatement().
 *
 * Revision 1.48  2006/04/15 15:30:20  poth
 * *** empty log message ***
 *
 * Revision 1.47  2006/04/13 07:49:10  poth
 * *** empty log message ***
 *
 * Revision 1.46  2006/04/07 10:16:28  mschneider
 * Added usage of generics.
 *
 * Revision 1.45  2006/04/06 20:25:25  poth
 * *** empty log message ***
 *
 * Revision 1.44  2006/04/04 20:39:42  poth
 * *** empty log message ***
 *
 * Revision 1.43  2006/04/04 10:30:02  mschneider
 * Improved javadoc.
 *
 * Revision 1.42  2006/03/30 21:20:26  poth
 * *** empty log message ***
 *
 * Revision 1.41  2006/03/29 14:55:16  mschneider
 * Changed result type constants to enum.
 *
 * Revision 1.40  2006/03/22 08:02:15  poth
 * *** empty log message ***
 *
 * Revision 1.39  2006/03/17 15:21:48  poth
 * *** empty log message ***
 *
 * Revision 1.38  2006/03/01 13:52:03  poth
 * *** empty log message ***
 *
 * Revision 1.37  2006/02/28 09:12:01  poth
 * *** empty log message ***
 *
 * Revision 1.36  2006/02/26 21:30:42  poth
 * *** empty log message ***
 *
 * Revision 1.35  2006/02/26 16:31:55  poth
 * *** empty log message ***
 *
 * Revision 1.34  2006/02/23 21:00:18  poth
 * *** empty log message ***
 *
 * Revision 1.33  2006/02/23 18:18:37  poth
 * *** empty log message ***
 *
 * Revision 1.32  2006/02/05 18:52:00  mschneider
 * Uses Generics for type safety now.
 *
 * Revision 1.31  2006/02/05 00:16:46  mschneider
 * Fixed handling of root feature instances that are also subfeatures.
 *
 * Revision 1.30  2006/01/31 16:22:46  mschneider
 * Changes due to refactoring of org.deegree.model.feature package.
 *
 * Revision 1.29  2006/01/20 18:12:25  mschneider
 * Uses XLinkedFeatureProperties correctly.
 *
 * Revision 1.28  2006/01/18 19:22:34  mschneider
 * Adapted to use SQL type code (instead of type name).
 *
 * Revision 1.27  2006/01/17 03:26:13  mschneider
 * Implemented workaround for multiple instances of the same feature instance in result feature collection.
 *
 * Revision 1.26  2006/01/17 02:45:38  mschneider
 * Fixes in handling of PropertyName references to substituted feature types (in Filter expressions).
 *
 * Revision 1.25  2006/01/17 01:45:26  mschneider
 * Fixes in handling of PropertyName references to substituted feature types.
 *
 * Revision 1.24  2006/01/13 14:49:50  mschneider
 * Added handling of mapping field types, so PreparedStatement.setObject() can respect the type code information for the field.
 *
 * Revision 1.23  2006/01/13 13:26:06  mschneider
 * Improved handling of SRS for geometry properties.
 *
 * Revision 1.22  2006/01/12 21:34:19  mschneider
 * Added handling of "PropertyNames" in queries.
 *
 * Revision 1.21  2006/01/08 14:09:35  poth
 * *** empty log message ***
 *
 * Revision 1.20  2005/12/29 10:55:57  mschneider
 * Cleanup. Moved WhereBuilder specific classes to own package.
 *
 * Revision 1.19  2005/12/28 16:14:42  mschneider
 * Fetching of feature properties with types that have more than one substitution works now (at least for bplan example).
 *
 * Revision 1.18  2005/12/27 23:26:03  mschneider
 * Outfactored GenericSQLDatastore specific code.
 *
 * Revision 1.17  2005/12/27 14:07:29  poth
 * no message
 *
 * Revision 1.16  2005/12/20 14:47:30  mschneider
 * Renamed #getFeatureType() to #getFeatureTypeReference().
 *
 * Revision 1.15  2005/12/19 14:17:19  mschneider
 * More work on delete operations.
 *
 * Revision 1.14  2005/12/19 10:20:19  mschneider
 * Salute to style guide!
 *
 * Revision 1.13  2005/12/19 09:31:34  deshmukh
 * Delete Transaction implemented
 *
 * Revision 1.12 2005/12/16 10:21:09 poth
 * no message
 *
 * Revision 1.11 2005/12/13 16:09:20 deshmukh
 * Changes made to accomodate Transactions
 *
 * Revision 1.10 2005/12/09 13:48:26 poth
 * no message
 *
 * Revision 1.9 2005/12/06 15:47:05 deshmukh
 * Modification to accomodate Transaction
 *
 * Revision 1.7 2005/11/29 16:51:59 mschneider
 * Added basic xlink handling.
 *
 * Revision 1.6 2005/11/28 19:26:33 mschneider
 * Implemented correct retrieval of properties with type FeatureArrayPropertyType.
 *
 * Revision 1.5 2005/11/23 00:52:30 mschneider
 * Major cleanup.
 *
 * Revision 1.4 2005/11/22 18:59:56 mschneider
 * Cleanup.
 *
 * Revision 1.3 2005/11/17 18:00:56 mschneider
 * Added conversion from database geometries to deegree geometries.
 *
 * Revision 1.2 2005/11/17 17:21:02 mschneider
 * added cvs log Changes to this class.
 ************************************************************************************************** */