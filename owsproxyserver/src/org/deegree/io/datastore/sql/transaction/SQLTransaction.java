//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/SQLTransaction.java,v 1.15 2006/10/05 10:13:15 mschneider Exp $
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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.sql.AbstractRequestHandler;
import org.deegree.io.datastore.sql.AbstractSQLDatastore;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.wfs.operation.transaction.Native;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;

/**
 * Handles {@link Transaction} requests to SQL based datastores.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.15 $, $Date: 2006/10/05 10:13:15 $
 */
public class SQLTransaction extends AbstractRequestHandler implements DatastoreTransaction {

    private static final ILogger LOG = LoggerFactory.getLogger( SQLTransaction.class );

    /**
     * Creates a new instance of <code>SQLTransaction</code> from the given parameters.
     * 
     * @param datastore
     * @param aliasGenerator
     * @param conn
     * @throws DatastoreException
     */
    public SQLTransaction( AbstractSQLDatastore datastore, TableAliasGenerator aliasGenerator,
                          Connection conn ) throws DatastoreException {
        super( datastore, aliasGenerator, conn );
        try {
            conn.setAutoCommit( false );
        } catch ( SQLException e ) {
            String msg = "Unable to disable auto commit: " + e.getMessage();
            LOG.logError( msg );
            throw new DatastoreException( msg, e );
        }
    }

    /**
     * Returns the underlying <code>AbstractSQLDatastore</code>.
     * 
     * @return the underlying <code>AbstractSQLDatastore</code>
     */
    public AbstractSQLDatastore getDatastore() {
        return this.datastore;
    }

    /**
     * Returns the underlying JDBC connection.
     * 
     * @return the underlying JDBC connection
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Makes the changes persistent that have been performed in this transaction.
     * 
     * @throws DatastoreException
     */
    public void commit()
                            throws DatastoreException {
        try {
            conn.commit();
        } catch ( SQLException e ) {
            String msg = "Unable to commit transaction: " + e.getMessage();
            LOG.logError( msg );
            throw new DatastoreException( msg, e );
        }
    }

    /**
     * Aborts the changes that have been performed in this transaction.
     * 
     * @throws DatastoreException
     */
    public void rollback()
                            throws DatastoreException {
        try {
            conn.rollback();
        } catch ( SQLException e ) {
            String msg = "Unable to rollback transaction: " + e.getMessage();
            LOG.logError( msg );
            throw new DatastoreException( msg, e );
        }
    }

    /**
     * Returns the transaction instance so other clients may acquire a transaction
     * (and underlying resources, such as JDBCConnections can be freed).
     * 
     * @throws DatastoreException
     */
    public void release()
                            throws DatastoreException {
        this.datastore.releaseTransaction( this );
    }

    /**
     * Inserts the given feature instances into the datastore.
     * 
     * @param features
     * @return feature ids of the inserted (root) features
     * @throws DatastoreException
     */
    public List<FeatureId> performInsert( List<Feature> features )
                            throws DatastoreException {

        InsertHandler handler = new InsertHandler( this, this.aliasGenerator, this.conn );
        List<FeatureId> fids = handler.performInsert( features );
        return fids;
    }

    /**
     * Performs an update operation against the datastore.
     * 
     * @param mappedFeatureType
     *            feature type that is to be updated
     * @param properties
     *            properties and their replacement values
     * @param filter
     *            selects the feature instances that are to be updated 
     * @return number of updated feature instances
     * @throws DatastoreException
     */
    public int performUpdate( MappedFeatureType mappedFeatureType,
                             Map<PropertyPath, Object> properties, Filter filter )
                            throws DatastoreException {

        UpdateHandler handler = new UpdateHandler( this, this.aliasGenerator, this.conn );
        int updatedFeatures = handler.performUpdate( mappedFeatureType, properties, filter );
        return updatedFeatures;
    }

    /**
     * Performs an update operation against the datastore.
     * <p>
     * The filter is expected to match exactly one feature which will be replaced by the specified
     * replacement feature. 
     * 
     * @param mappedFeatureType
     *            feature type that is to be updated
     * @param replacementFeature
     *            feature instance that will replace the selected feature
     * @param filter
     *            selects the single feature instances that is to be replaced
     * @return number of updated feature instances (must be 0 or 1)
     * @throws DatastoreException
     */
    public int performUpdate( MappedFeatureType mappedFeatureType, Feature replacementFeature,
                             Filter filter )
                            throws DatastoreException {

        UpdateHandler handler = new UpdateHandler( this, this.aliasGenerator, this.conn );
        int updatedFeatures = handler.performUpdate( mappedFeatureType, replacementFeature, filter );
        return updatedFeatures;
    }

    /**
     * Deletes the features from the datastore that are matched by the given filter and type.
     * 
     * @param mappedFeatureType
     * @param filter
     * @return number of deleted feature instances
     * @throws DatastoreException
     */
    public int performDelete( MappedFeatureType mappedFeatureType, Filter filter )
                            throws DatastoreException {

        DeleteHandler handler = new DeleteHandler( this, this.aliasGenerator, this.conn );
        int deletedFeatures = handler.performDelete( mappedFeatureType, filter );
        return deletedFeatures;
    }

    /**
     * Performs a 'native' operation against the datastore.
     * 
     * @param operation
     * @return number of processed feature instances.
     * @throws DatastoreException
     */
    public int performNative( Native operation )
                            throws DatastoreException {

        throw new UnsupportedOperationException( "Native operations are not supported." );
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SQLTransaction.java,v $
Revision 1.15  2006/10/05 10:13:15  mschneider
Javadoc fixes.

Revision 1.14  2006/07/26 18:57:07  mschneider
Fixed formatting.

Revision 1.13  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */