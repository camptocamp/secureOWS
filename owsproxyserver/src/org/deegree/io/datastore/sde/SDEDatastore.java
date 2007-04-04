//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sde/SDEDatastore.java,v 1.4 2006/08/06 20:58:10 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2006 by: M.O.S.S. Computer Grafik Systeme GmbH
 Hohenbrunner Weg 13
 D-82024 Taufkirchen
 http://www.moss.de/

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

 ---------------------------------------------------------------------------*/
package org.deegree.io.datastore.sde;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.JDBCConnection;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreConfiguration;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.sql.SQLDatastoreConfiguration;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.sdeapi.SDEAdapter;
import org.deegree.io.sdeapi.SDEConnection;
import org.deegree.io.sdeapi.SDEConnectionPool;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcwebservices.wfs.operation.LockFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;

import com.esri.sde.sdk.client.SeCoordinateReference;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeShape;

/**
 * Datastore implementation for an ESRI SDE database.
 *
 * @author <a href="mailto:cpollmann@moss.de">Christoph Pollmann</a>
 *
 * @author last edited by: $Author: poth $
 *
 * @version 2.0, $Revision: 1.4 $
 *
 * @since 2.0
 */
public class SDEDatastore extends Datastore {

    protected static final ILogger LOG = LoggerFactory.getLogger( SDEDatastore.class );

    protected SDEConnectionPool pool = null;

    ///////////////////////////////////////////////////
    // overwritten methods of class Datastore
    ///////////////////////////////////////////////////

    /**
     * Configures the datastore with the supplied configuration.
     *
     * @param config
     *            configuration
     * @throws DatastoreException
     */
    public void configure( DatastoreConfiguration config )
                            throws DatastoreException {
        super.configure( config );
        this.pool = SDEConnectionPool.getInstance();
    }

    /**
     * Returns the configuration parameters of the datastore.
     *
     * @return the configuration parameters of the datastore.
     */
    public DatastoreConfiguration getConfiguration() {
        return super.getConfiguration();
    }

    /**
     * Adds the given GML application schema to the set of schemas that are handled by this
     * datastore instance.
     * <p>
     * Note that this method may be called several times for every GML schema that uses this
     * datastore instance.
     *
     * @param schema
     *            GML application schema to bind
     * @throws DatastoreException
     * @throws DatastoreException
     */
    public void bindSchema( MappedGMLSchema schema )
                            throws DatastoreException {
        super.bindSchema( schema );
    }

    /**
     * Returns the GML application schemas that are handled by this datastore.
     *
     * @return the GML application schemas that are handled by this datastore
     */
    public MappedGMLSchema[] getSchemas() {
        return super.getSchemas();
    }

    /**
     * Returns the feature type with the given name.
     *
     * @param ftName
     *            name of the feature type
     * @return the feature type with the given name.
     */
    public MappedFeatureType getFeatureType( QualifiedName ftName ) {
        return super.getFeatureType( ftName );
    }

    /**
     * Closes the datastore so it can free dependent resources.
     */
    public void close()
                            throws DatastoreException {
        pool = null;
    }

    /**
     * Performs a query against the datastore.
     *
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     */
    public FeatureCollection performQuery( final Query query,
                                          final MappedFeatureType rootFeatureType )
                            throws DatastoreException {

        FeatureCollection result = null;
        SDEConnection conn = acquireConnection();
        SDEQueryHandler queryHandler = new SDEQueryHandler( this, new TableAliasGenerator(), conn,
                                                            rootFeatureType, query );
        result = queryHandler.performQuery();
        return result;
    }

    /**
     * Performs a query with a lock against the datastore.
     *
     * @param Query
     *            query to be performed
     * @return FeatureCollection
     */
    public FeatureCollection performQueryWithLock( Query query )
                            throws DatastoreException {
        throw new UnsupportedOperationException( "Query Lock Feature Operation not supported. " );
    }

    /**
     * Performs a lock feature against the datastore.
     *
     * @param LockFeature
     *            lockfeature to be performed
     *
     */
    public void performLockFeature( LockFeature request )
                            throws DatastoreException {
        throw new UnsupportedOperationException( "Lock Feature Operation not supported. " );
    }

    /**
     * Acquires transactional access to the datastore instance. There's only one active transaction
     * per datastore allowed.
     *
     * @return transaction object that allows to perform transactions operations on the datastore
     * @throws DatastoreException
     */
    public DatastoreTransaction acquireTransaction()
                            throws DatastoreException {
        DatastoreTransaction transactionHandler = new SDETransaction( this,
                                                                      new TableAliasGenerator(),
                                                                      acquireConnection() );
        return transactionHandler;
    }

    ///////////////////////////////////////////////////
    // overwritten methods of class AbstractSQLDatastore
    ///////////////////////////////////////////////////

    /**
     * Returns a specific <code>WhereBuilder</code> implementation for SDE.
     *
     * @param rootFeatureType
     * @param filter
     * @param aliasGenerator
     * @return
     * @throws DatastoreException
     */
    public SDEWhereBuilder getWhereBuilder( MappedFeatureType rootFeatureType, Filter filter,
                                           TableAliasGenerator aliasGenerator )
                            throws DatastoreException {
        SDEWhereBuilder wb = new SDEWhereBuilder( rootFeatureType, filter, aliasGenerator );
        return wb;
    }

    /**
     * Converts a database specific geometry <code>Object</code> from the <code>ResultSet</code>
     * to a deegree <code>Geometry</code>.
     *
     * @param value
     * @param targetSRS
     * @param conn
     * @return corresponding deegree geometry
     * @throws GeometryException
     */
    public Geometry convertDBToDegreeGeometry( Object value )
                            throws DatastoreException {

        Geometry geometry = null;
        if ( value != null ) {
            try {
                SeCoordinateReference coordRef = ( (SeShape) value ).getCoordRef();
                String desc = coordRef.getProjectionDescription();
                LOG.logDebug ( "******* coordSys=" + desc + "****" );
                geometry = SDEAdapter.wrap( (SeShape) value );
            } catch ( Exception e ) {
                throw new DatastoreException( "Error converting SeShape to Geometry: "
                                             + e.getMessage() );
            }
        }
        return geometry;
    }

    /**
     * Converts a deegree <code>Geometry</code> to a database specific geometry
     * <code>Object</code>.
     *
     * @param geometry
     * @param targetSRS
     * @param conn
     * @return corresponding database specific geometry object
     */
    public Object convertDegreeToDBGeometry( Geometry geometry )
                            throws DatastoreException {
        Object value = null;
        if ( geometry != null ) {
            try {
                //TODO: SRS handling
                SeCoordinateReference coordRef = new SeCoordinateReference();
                value = SDEAdapter.export( geometry, coordRef );
            } catch ( Exception e ) {
                throw new DatastoreException( "Error converting Geometry to SeShape: "
                                              + e.getMessage(), e );
            }
        }
        return value;
    }

    /**
     * Returns the database connection requested for.
     *
     * @return Connection
     */
    protected SDEConnection acquireConnection()
                            throws DatastoreException {
        JDBCConnection jdbcConnection = ( (SQLDatastoreConfiguration) this.getConfiguration() ).getJDBCConnection();
        SDEConnection conn = null;
        try {
            String url = jdbcConnection.getURL();
            String[] tmp = url.split( ":" );
            int instance = 5151;
            if ( 2 == tmp.length ) {
                url = tmp[0];
                instance = Integer.parseInt( tmp[1] );
            }
            conn = pool.acquireConnection( url, instance, jdbcConnection.getSDEDatabase(),
                                           jdbcConnection.getSDEVersion(),
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
     * @param Connection
     *            conn to be released.
     */
    protected void releaseConnection( SDEConnection conn )
                            throws DatastoreException {
        JDBCConnection jdbcConnection = ( (SQLDatastoreConfiguration) this.getConfiguration() ).getJDBCConnection();
        try {
            String url = jdbcConnection.getURL();
            String[] tmp = url.split( ":" );
            int instance = 5151;
            if ( 2 == tmp.length ) {
                url = tmp[0];
                instance = Integer.parseInt( tmp[1] );
            }
            pool.releaseConnection( conn, url, instance, jdbcConnection.getSDEDatabase(),
                                    jdbcConnection.getSDEVersion(), jdbcConnection.getUser() );
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
     */
    public SeQuery prepareStatement( SDEConnection conn, StatementBuffer statementBuffer ) {
        LOG.logDebug( "Preparing statement: " + statementBuffer.getQueryString() );

        SeQuery query = null;
        try {
            query = new SeQuery( conn.getConnection() );
            query.prepareSql( statementBuffer.getQueryString() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // TODO
        return query;
    }

    @Override
    public FeatureCollection performQuery( Query query, MappedFeatureType rootFeatureType,
                                          DatastoreTransaction context )
                            throws DatastoreException {
        throw new DatastoreException( "method invocation for sde not applicable" );
    }

    @Override
    public void releaseTransaction( DatastoreTransaction ta )
                            throws DatastoreException {
        releaseConnection( ((SDETransaction) ta).getConnection() );
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: SDEDatastore.java,v $
 * Revision 1.4  2006/08/06 20:58:10  poth
 * never read parameter removed
 *
 * Revision 1.3  2006/08/06 20:38:52  poth
 * never thrown exceptions and never read variables removed
 *
 * Revision 1.2  2006/07/10 21:07:31  mschneider
 * Removed System.out.println's.
 *
 * Revision 1.1  2006/05/21 19:06:21  poth
 * initial load up
 *
 * Revision 1.1  2006/05/09 14:51:52  polli
 * no message
 *
 **************************************************************************************************/

