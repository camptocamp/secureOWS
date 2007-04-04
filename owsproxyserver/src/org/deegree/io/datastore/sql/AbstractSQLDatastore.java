//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/AbstractSQLDatastore.java,v 1.84 2006/11/29 16:59:54 mschneider Exp $
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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.i18n.Messages;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.JDBCConnection;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreConfiguration;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.TransactionException;
import org.deegree.io.datastore.idgenerator.IdGenerationException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.io.datastore.schema.content.SQLFunctionCall;
import org.deegree.io.datastore.sql.StatementBuffer.StatementArgument;
import org.deegree.io.datastore.sql.transaction.SQLTransaction;
import org.deegree.io.datastore.sql.wherebuilder.WhereBuilder;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureDocument;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.operation.LockFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * This abstract class implements the common functionality of a {@link Datastore} that is
 * backed by an SQL database.
 *
 * @see QueryHandler
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 1.84 $, $Date: 2006/11/29 16:59:54 $
 */
public abstract class AbstractSQLDatastore extends Datastore {

    protected static final ILogger LOG = LoggerFactory.getLogger( AbstractSQLDatastore.class );

    // database specific SRS code for unspecified SRS
    protected static final int SRS_UNDEFINED = -1;

    protected DBConnectionPool pool;

    private DatastoreTransaction activeTransaction;

    /**
     * Configures the datastore.
     *
     * @param datastoreConfiguration
     *            configuration
     * @throws DatastoreException
     */
    @Override
    public void configure( DatastoreConfiguration datastoreConfiguration )
                            throws DatastoreException {
        super.configure( datastoreConfiguration );
        this.pool = DBConnectionPool.getInstance();
    }

    /**
     * Closes the datastore and returns held resources.
     *
     * @throws DatastoreException
     */
    @Override
    public void close()
                            throws DatastoreException {
        this.pool = null;
    }

    /**
     * Overwrite this to return a database specific (spatial capable) {@link WhereBuilder}
     * implementation.
     *
     * @param ft
     *           requested feature type
     * @param filter
     *           filter that restricts the matched features
     * @param sortProperties
     *           sort criteria for the result, may be null or empty
     * @param aliasGenerator
     *           used to generate unique table aliases
     * @param vcProvider
     * @return <code>WhereBuilder</code> implementation suitable for this datastore
     * @throws DatastoreException
     */
    public WhereBuilder getWhereBuilder( MappedFeatureType ft, Filter filter,
                                        SortProperty[] sortProperties,
                                        TableAliasGenerator aliasGenerator,
                                        VirtualContentProvider vcProvider )
                            throws DatastoreException {
        return new WhereBuilder( ft, filter, sortProperties, aliasGenerator, vcProvider );
    }

    /**
     * Performs a {@link Query} against the datastore.
     *
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @return matched features
     * @throws DatastoreException 
     * @throws UnknownCRSException 
     */
    @Override
    public FeatureCollection performQuery( Query query, MappedFeatureType rootFeatureType )
                            throws DatastoreException, UnknownCRSException {

        Connection conn = acquireConnection();
        FeatureCollection result;
        try {
            result = performQuery( query, rootFeatureType, conn );
        } finally {
            releaseConnection( conn );
        }

        return result;
    }

    /**
     * Performs a {@link Query} against the datastore (in the given transaction context).
     *
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @param context
     *            context (used to specify the JDBCConnection, for example)
     * @return requested feature instances
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    @Override
    public FeatureCollection performQuery( Query query, MappedFeatureType rootFeatureType,
                                          DatastoreTransaction context )
                            throws DatastoreException, UnknownCRSException {
        return performQuery( query, rootFeatureType, ( (SQLTransaction) context ).getConnection() );
    }

    /**
     * Performs a {@link Query} against the datastore.
     * <p>
     * Note that this method is responsible for the coordinate system tranformation of the input
     * {@link Query} and the output {@link FeatureCollection}.
     *
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @param conn
     *            JDBC connection to use
     * @return requested feature instances
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    protected FeatureCollection performQuery( Query query, MappedFeatureType rootFeatureType,
                                             Connection conn )
                            throws DatastoreException, UnknownCRSException {

        query = transformQuery( query );

        FeatureCollection result = null;
        try {
            QueryHandler queryHandler = new QueryHandler( this, new TableAliasGenerator(), conn,
                                                          rootFeatureType, query );
            result = queryHandler.performQuery();
        } catch ( SQLException e ) {
            e.printStackTrace();
            String msg = "SQL error while performing query: " + e.getMessage();
            LOG.logInfo( msg );
            throw new DatastoreException( msg, e );
        }

        // transform result to queried srs (only if necessary)
        String targetSrs = query.getSrsName();
        if ( targetSrs != null && !this.canTransformTo( targetSrs ) ) {
            result = transformResult( result, targetSrs );
        }
        return result;
    }

    /**
     * Acquires transactional access to the datastore. There's only one active transaction per
     * datastore instance allowed.
     *
     * @return transaction object that allows to perform transactions operations on the datastore
     * @throws DatastoreException
     */
    @Override
    public DatastoreTransaction acquireTransaction()
                            throws DatastoreException {
        if ( this.activeTransaction != null ) {
            String msg = Messages.getMessage( "DATASTORE_ACQUIRE_DSTA" );
            throw new DatastoreException( msg );
        }
        this.activeTransaction = new SQLTransaction( this, new TableAliasGenerator(),
                                                     acquireConnection() );
        return this.activeTransaction;
    }

    /**
     * Returns the transaction to the datastore. This makes the transaction available to other
     * clients again (via <code>acquireTransaction</code>). Underlying resources (such as
     * JDBCConnections are freed).
     * <p>
     * The transaction should be terminated, i.e. commit() or rollback() must have been called
     * before.
     *
     * @param ta the DatastoreTransaction to be returned
     * @throws DatastoreException
     */
    @Override
    public void releaseTransaction( DatastoreTransaction ta )
                            throws DatastoreException {
        if ( ta.getDatastore() != this ) {
            String msg = Messages.getMessage( "DATASTORE_TA_NOT_OWNER" );
            throw new TransactionException( msg );
        }
        if ( ta != this.activeTransaction ) {
            String msg = Messages.getMessage( "DATASTORE_TA_NOT_ACTIVE" );
            throw new TransactionException( msg );
        }
        this.activeTransaction = null;
        releaseConnection( ( (SQLTransaction) ta ).getConnection() );
    }

    /**
     * Performs a QueryWithLock request against the datastore.
     *
     * @param query
     *            query to be performed
     * @return FeatureCollection
     * @throws DatastoreException
     */
    @Override
    public FeatureCollection performQueryWithLock( Query query )
                            throws DatastoreException {
        throw new UnsupportedOperationException( "QueryWithLock operation is not supported. " );
    }

    /**
     * Performs a LockFeature request against the datastore.
     *
     * @param request
     *            LockFeature to be performed
     * @throws DatastoreException
     */
    @Override
    public void performLockFeature( LockFeature request )
                            throws DatastoreException {
        throw new UnsupportedOperationException( "LockFeature operation is not supported. " );
    }

    /**
     * Converts a database specific geometry <code>Object</code> from the <code>ResultSet</code>
     * to a deegree <code>Geometry</code>.
     *
     * @param value
     * @param targetSRS
     * @param conn
     * @return corresponding deegree geometry
     * @throws SQLException
     */
    public abstract Geometry convertDBToDeegreeGeometry( Object value, CoordinateSystem targetSRS,
                                                        Connection conn )
                            throws SQLException;

    /**
     * Converts a deegree <code>Geometry</code> to a database specific geometry
     * <code>Object</code>.
     *
     * @param geometry
     * @param nativeSRSCode
     * @param conn
     * @return corresponding database specific geometry object
     * @throws DatastoreException
     */
    public abstract Object convertDeegreeToDBGeometry( Geometry geometry, int nativeSRSCode,
                                                      Connection conn )
                            throws DatastoreException;

    /**
     * Returns the database connection requested for.
     *
     * @return Connection
     * @throws DatastoreException
     */
    protected Connection acquireConnection()
                            throws DatastoreException {
        JDBCConnection jdbcConnection = ( (SQLDatastoreConfiguration) this.getConfiguration() ).getJDBCConnection();
        Connection conn = null;
        try {
            conn = pool.acquireConnection( jdbcConnection.getDriver(), jdbcConnection.getURL(),
                                           jdbcConnection.getUser(), jdbcConnection.getPassword() );
        } catch ( Exception e ) {
            String msg = "Cannot acquire database connection: " + e.getMessage();
            LOG.logInfo( msg );
            throw new DatastoreException( msg, e );
        }
        return conn;
    }

    /**
     * Releases the connection.
     *
     * @param conn
     *            connection to be released.
     * @throws DatastoreException
     */
    public void releaseConnection( Connection conn )
                            throws DatastoreException {
        LOG.logDebug( "Releasing JDBCConnection." );
        JDBCConnection jdbcConnection = ( (SQLDatastoreConfiguration) this.getConfiguration() ).getJDBCConnection();
        try {
            pool.releaseConnection( conn, jdbcConnection.getDriver(), jdbcConnection.getURL(),
                                    jdbcConnection.getUser(), jdbcConnection.getPassword() );
        } catch ( Exception e ) {
            String msg = "Cannot release database connection: " + e.getMessage();
            LOG.logInfo( msg );
            throw new DatastoreException( msg, e );
        }
    }

    /**
     * Converts the <code>StatementBuffer</code> into a <code>PreparedStatement</code>, which
     * is initialized and ready to be performed.
     *
     * @param conn
     *            connection to be used to create the <code>PreparedStatement</code>
     * @param statementBuffer
     * @return the <code>PreparedStatment</code>, ready to be performed
     * @throws SQLException
     *             if a JDBC related error occurs
     * @throws DatastoreException
     */
    public PreparedStatement prepareStatement( Connection conn, StatementBuffer statementBuffer )
                            throws SQLException, DatastoreException {
        LOG.logDebug( "Preparing statement: " + statementBuffer.getQueryString() );

        PreparedStatement preparedStatement = conn.prepareStatement( statementBuffer.getQueryString() );

        Iterator it = statementBuffer.getArgumentsIterator();
        int i = 1;
        while ( it.hasNext() ) {
            StatementArgument argument = (StatementArgument) it.next();
            int targetSqlType = argument.getTypeCode();
            Object sqlObject = convertToSQLType( argument.getArgument(), targetSqlType );

            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                try {
                    String typeName = Types.getTypeNameForSQLTypeCode( targetSqlType );
                    LOG.logDebug( "Setting argument " + i + ": type=" + typeName + ", value class="
                                  + sqlObject.getClass() );
                    if ( sqlObject instanceof String || sqlObject instanceof Number ) {
                        LOG.logDebug( "Value: '" + sqlObject + "'" );
                    }
                } catch ( UnknownTypeException e ) {
                    throw new SQLException( e.getMessage() );
                }
            }
            if ( sqlObject == null ) {
                preparedStatement.setNull( i, targetSqlType );
            } else {
                preparedStatement.setObject( i, sqlObject, targetSqlType );
            }
            i++;
        }
        return preparedStatement;
    }

    /**
     * Converts the given object into an object that is suitable for a table column of the specified
     * SQL type.
     * <p>
     * The return value is used in a java.sql.PreparedStatement.setObject() call.
     * <p>
     * Please note that this implementation is subject to change. There are missing type cases, and
     * it is preferable to use the original string representation of the input object (except
     * for geometries).
     * <p>
     * NOTE: Almost identical functionality exists in {@link GMLFeatureDocument}. This is subject
     *       to change.
     *
     * @see java.sql.PreparedStatement#setObject(int, Object, int)
     * @param o
     * @param sqlTypeCode
     * @return an object that is suitable for a table column of the specified SQL type
     * @throws DatastoreException
     */
    public Object convertToSQLType( Object o, int sqlTypeCode )
                            throws DatastoreException {

        Object sqlType = null;

        switch ( sqlTypeCode ) {
        case Types.VARCHAR: {
            sqlType = o.toString();
            break;
        }
        case Types.INTEGER:
        case Types.SMALLINT: {
            try {
                sqlType = new Integer( o.toString() );
            } catch ( NumberFormatException e ) {
                throw new DatastoreException( "'" + o + "' does not denote a valid Integer value." );
            }
            break;
        }
        case Types.NUMERIC:
        case Types.REAL:
        case Types.DOUBLE: {
            try {
                sqlType = new Double( o.toString() );
            } catch ( NumberFormatException e ) {
                throw new DatastoreException( "'" + o + "' does not denote a valid Double value." );
            }
            break;
        }
        case Types.DECIMAL:
        case Types.FLOAT: {
            try {
                sqlType = new Float( o.toString() );
            } catch ( NumberFormatException e ) {
                throw new DatastoreException( "'" + o + "' does not denote a valid Double value." );
            }
            break;
        }
        case Types.BOOLEAN: {
            sqlType = new Boolean( o.toString() );
            break;
        }
        case Types.DATE: {
            if ( o instanceof Date ) {
                sqlType = new java.sql.Date( ( (Date) o ).getTime() );
            } else {
                String s = o.toString();
                int idx = s.indexOf( " " ); // Datestring like "2005-04-21 00:00:00"
                if ( -1 != idx )
                    s = s.substring( 0, idx );
                sqlType = new java.sql.Date( TimeTools.createCalendar( s ).getTimeInMillis() );
            }
            break;
        }
        case Types.TIME: {
            if ( o instanceof Date ) {
                sqlType = new java.sql.Time( ( (Date) o ).getTime() );
            } else {
                sqlType = new java.sql.Time(
                                             TimeTools.createCalendar( o.toString() ).getTimeInMillis() );
            }
            break;
        }
        case Types.TIMESTAMP: {
            if ( o instanceof Date ) {
                sqlType = new Timestamp( ( (Date) o ).getTime() );
            } else {
                sqlType = new java.sql.Timestamp(
                                                  TimeTools.createCalendar( o.toString() ).getTimeInMillis() );
            }
            break;
        }
        default: {
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                String sqlTypeName = "" + sqlTypeCode;
                try {
                    sqlTypeName = Types.getTypeNameForSQLTypeCode( sqlTypeCode );
                } catch ( UnknownTypeException e ) {
                    LOG.logDebug( e.getMessage() );
                }
                LOG.logDebug( "No type conversion for sql type '" + sqlTypeName
                              + "' defined. Passing argument of type '" + o.getClass().getName()
                              + "'." );
            }
            sqlType = o;
        }
        }
        return sqlType;
    }

    /**
     * Overwrite this to enable the datastore to fetch the next value of a SQL sequence.
     *
     * @param conn
     *            JDBC connection to be used.
     * @param sequence
     *            name of the SQL sequence.
     * @return next value of the given SQL sequence
     * @throws DatastoreException
     *             if the value could not be retrieved
     */
    public Object getSequenceNextVal( @SuppressWarnings("unused")
    Connection conn, @SuppressWarnings("unused")
    String sequence )
                            throws DatastoreException {
        String msg = Messages.getMessage( "DATASTORE_SEQ_NOT_SUPPORTED", this.getClass().getName() );
        throw new DatastoreException( msg );
    }

    /**
     * Returns the successor (+1) to the maximum value stored in a certain table column.
     *
     * @param conn
     *            JDBC connection to be used
     * @param tableName
     *            name of the table
     * @param columnName
     *            name of the column
     * @return the successor (+1) to the maximum value
     * @throws IdGenerationException
     *             if the value could not be retrieved
     */
    public Object getMaxNextVal( Connection conn, String tableName, String columnName )
                            throws IdGenerationException {

        Object nextVal = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery( "SELECT MAX(" + columnName + ") FROM " + tableName );
                if ( rs.next() ) {
                    nextVal = rs.getObject( 1 );
                    if ( null == nextVal ) {
                        nextVal = new Integer( 1 );
                    } else {
                        if ( nextVal instanceof Integer ) {
                            nextVal = new Integer( ( (Integer) nextVal ).intValue() + 1 );
                        } else {
                            nextVal = new Integer( Integer.parseInt( nextVal.toString() ) + 1 );
                        }
                    }
                } else {
                    nextVal = new Integer( 1 );
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
        } catch ( SQLException e ) {
            String msg = "Could not retrieve max value for table column '" + tableName + "."
                         + columnName + "': " + e.getMessage();
            throw new IdGenerationException( msg, e );
        } catch ( NumberFormatException e ) {
            String msg = "Could not convert selected value to integer: " + e.getMessage();
            throw new IdGenerationException( msg, e );
        }
        return nextVal;
    }

    /**
     * Returns an {@link SQLFunctionCall} that refers to the given {@link MappedGeometryPropertyType} in
     * the specified target SRS using a database specific SQL function.
     *  
     * @param geoProperty
     *            geometry property
     * @param targetSRS
     *            target spatial reference system (usually "EPSG:XYZ")
     * @return an {@link SQLFunctionCall} that refers to the geometry in the specified srs
     * @throws DatastoreException 
     */
    public SQLFunctionCall buildSRSTransformCall( @SuppressWarnings("unused")
    MappedGeometryPropertyType geoProperty, @SuppressWarnings("unused")
    String targetSRS )
                            throws DatastoreException {
        String msg = Messages.getMessage( "DATASTORE_SQL_NATIVE_CT_UNSUPPORTED",
                                          this.getClass().getName() );
        throw new DatastoreException( msg );
    }

    public String buildSRSTransformCall( String geomIdentifier, int nativeSRSCode )
                            throws DatastoreException {
        String msg = Messages.getMessage( "DATASTORE_SQL_NATIVE_CT_UNSUPPORTED",
                                          this.getClass().getName() );
        throw new DatastoreException( msg );
    }

    /**
     * Checks whether the (native) coordinate transformation of the specified geometry property to
     * the given SRS is possible (and necessary), i.e.
     * <ul>
     * <li>the internal srs of the property is specified (and not -1)
     * <li>or the requested SRS is null or equal to the property's srs
     * </ul>
     * If this is not the case, a {@link DatastoreException} is thrown to indicate the problem.
     * 
     * @param pt
     * @param queriedSrs
     * @throws DatastoreException
     */
    String checkTransformation( MappedGeometryPropertyType pt, String queriedSrs )
                            throws DatastoreException {

        String targetSrs = null;
        int internalSrs = pt.getMappingField().getSRS();
        String propertySrs = pt.getCS().getAsString();

        if ( queriedSrs != null && !propertySrs.equals( queriedSrs ) ) {
            if ( internalSrs == SRS_UNDEFINED ) {
                String msg = Messages.getMessage( "DATASTORE_SRS_NOT_SPECIFIED", pt.getName(),
                                                  queriedSrs, propertySrs );
                throw new DatastoreException( msg );
            }
            targetSrs = queriedSrs;
        }
        return targetSrs;
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: AbstractSQLDatastore.java,v $
 * Revision 1.84  2006/11/29 16:59:54  mschneider
 * Improved handling of native coordinate transformation.
 *
 * Revision 1.83  2006/11/27 09:07:53  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.82  2006/11/09 17:39:13  mschneider
 * Added #buildSRSTransformCall() methods.
 *
 * Revision 1.81  2006/09/27 20:30:20  mschneider
 * Activated transformation of query and result SRS.
 *
 * Revision 1.80  2006/09/27 16:10:24  mschneider
 * Changed visibility of #performQuery( Query query, MappedFeatureType rootFeatureType, Connection conn ).
 *
 * Revision 1.79  2006/09/26 16:45:04  mschneider
 * Refactored because of moving of TransactionException.
 *
 * Revision 1.78  2006/09/22 11:23:37  mschneider
 * Javadoc fixes.
 *
 * Revision 1.77  2006/09/20 11:35:41  mschneider
 * Merged datastore related messages with org.deegree.18n.
 *
 * Revision 1.76  2006/09/19 14:54:02  mschneider
 * Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.
 *
 * Revision 1.75  2006/09/14 00:18:33  mschneider
 * Javadoc fixes.
 *
 * Revision 1.74  2006/09/05 14:43:24  mschneider
 * Adapted due to merging of messages.
 *
 * Revision 1.73  2006/08/21 15:45:22  mschneider
 * Javadoc improvements.
 *
 * Revision 1.72  2006/08/14 16:52:18  mschneider
 * Changed to respect (optional) SortProperties.
 *
 * Revision 1.71  2006/07/26 18:54:47  mschneider
 * Javadoc improvements.
 *
 * Revision 1.70  2006/07/23 09:19:56  poth
 * support for real datatype added
 *
 * Revision 1.69  2006/05/26 09:42:41  poth
 * bug fix for supporting numberOfFeatures for returned FeatureCollections / footer correction
 *
 * Revision 1.68  2006/05/15 10:51:40  mschneider
 * Added performQuery(Query,MappedFeatureType,DatastoreTransaction) to allow queries with given transaction context.
 *
 * Revision 1.67  2006/05/08 09:58:45  poth
 * *** empty log message ***
 ************************************************************************************************* */