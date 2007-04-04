//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/generic/GenericSQLDatastore.java,v 1.45 2006/11/29 16:59:54 mschneider Exp $
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
package org.deegree.io.datastore.sql.generic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.JDBCConnection;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.sql.AbstractSQLDatastore;
import org.deegree.io.datastore.sql.QueryHandler;
import org.deegree.io.datastore.sql.SQLDatastoreConfiguration;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.datastore.sql.VirtualContentProvider;
import org.deegree.io.datastore.sql.wherebuilder.WhereBuilder;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryImpl;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * {@link Datastore} implementation for any SQL database that can be accessed through a jdbc
 * connection (even the odbc-jdbc bridge is supported) and that supports the storing of BLOBs.
 * <p>
 * The spatial information is assumed to be stored in a BLOB field as a serialized deegree
 * geometry. It also will be assumed that a spatial index exists and that it has been created
 * using deegree's quadtree api.
 * 
 * @see org.deegree.io.quadtree
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.45 $, $Date: 2006/11/29 16:59:54 $ 
 */
public class GenericSQLDatastore extends AbstractSQLDatastore {

    protected static final ILogger LOG = LoggerFactory.getLogger( GenericSQLDatastore.class );

    /**
     * Returns a specific <code>WhereBuilder</code> implementation.
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
    @Override
    public WhereBuilder getWhereBuilder( MappedFeatureType ft, Filter filter,
                                         SortProperty[] sortProperties,
                                         TableAliasGenerator aliasGenerator,
                                         VirtualContentProvider vcProvider )
                            throws DatastoreException {
        JDBCConnection jdbc = ( (SQLDatastoreConfiguration) getConfiguration() ).getJDBCConnection();
        return new GenericSQLWhereBuilder( ft, filter, sortProperties, aliasGenerator, vcProvider,
                                           jdbc );
    }

    /**
     * Performs a {@link Query} against the datastore.
     *
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @param conn
     *            JDBC connection to use
     * @return requested feature instances
     * @throws DatastoreException
     */
    @Override
    protected FeatureCollection performQuery( Query query, MappedFeatureType rootFeatureType,
                                              Connection conn )
                            throws DatastoreException {

        query = transformQuery( query );

        FeatureCollection result = null;
        try {
            QueryHandler queryHandler = new QueryHandler( this, new TableAliasGenerator(), conn,
                                                          rootFeatureType, query );
            result = queryHandler.performQuery();
        } catch ( SQLException e ) {
            String msg = "SQL error while performing query: " + e.getMessage();
            LOG.logError( msg, e );
            throw new DatastoreException( msg, e );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new DatastoreException( e );
        }

        if ( query.getFilter() != null ) {
            try {
                LOG.logDebug( "Features (before refinement): " + result.size() );
                result = filterCollection( result, query.getFilter() );
                LOG.logDebug( "Features (after refinement): " + result.size() );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new DatastoreException( e.getMessage(), e );
            }
        }

        result = transformResult( result, query.getSrsName() );
        return result;
    }

    /**
     * Filters the feature collection using the given filter.
     * <p>
     * This is required because spatial filtering performed in the {@link GenericSQLWhereBuilder}
     * just considers the BBOX and all non-spatial filter conditions.
     *  
     * TODO remove BBOX + all non-spatial conditions from the filter
     * 
     * @param fc
     * @param filter
     * @return filtered feature collection
     */
    private FeatureCollection filterCollection( FeatureCollection fc, Filter filter )
                            throws FilterEvaluationException {
        for ( int i = fc.size() - 1; i >= 0; i-- ) {
            Feature feat = fc.getFeature( i );
            if ( !filter.evaluate( feat ) ) {
                fc.remove( i );
            }
        }
        return fc;
    }

    /**
     * Converts a database specific geometry <code>Object</code> from the <code>ResultSet</code>
     * to a deegree <code>Geometry</code>.
     * 
     * @param value
     * @param targetCS
     * @param conn
     * @return corresponding deegree geometry
     * @throws SQLException
     */
    @Override
    public Geometry convertDBToDeegreeGeometry( Object value, CoordinateSystem targetCS,
                                                Connection conn )
                            throws SQLException {

        Geometry geometry = null;
        if ( value != null ) {
            try {
                if ( targetCS == null ) {
                    targetCS = CRSFactory.create( "EPSG:4326" );
                }
                if ( value instanceof String ) {
                    geometry = GMLGeometryAdapter.wrap( (String) value );
                } else if ( value instanceof InputStream ) {
                    StringBuffer sb = new StringBuffer( 10000 );
                    BufferedReader br = new BufferedReader(
                                                            new InputStreamReader(
                                                                                   (InputStream) value ) );
                    String line = "";
                    while ( ( line = br.readLine() ) != null ) {
                        sb.append( line );
                    }
                    geometry = GMLGeometryAdapter.wrap( sb.toString() );
                } else if ( value instanceof Reader ) {
                    StringBuffer sb = new StringBuffer( 10000 );
                    BufferedReader br = new BufferedReader( (Reader) value );
                    String line = "";
                    while ( ( line = br.readLine() ) != null ) {
                        sb.append( line );
                    }
                    geometry = GMLGeometryAdapter.wrap( sb.toString() );
                } else {
                    geometry = GMLGeometryAdapter.wrap( new String( (byte[]) value ) );
                }
                ( (GeometryImpl) geometry ).setCoordinateSystem( targetCS );
            } catch ( Exception e ) {
                LOG.logError( "could not transform result to geometry; ", e );
                throw new SQLException( "could not transform result to geometry; " + e.getMessage() );
            }
        }
        return geometry;
    }

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
    @Override
    public Object convertDeegreeToDBGeometry( Geometry geometry, int nativeSRSCode, Connection conn )
                            throws DatastoreException {
        throw new UnsupportedOperationException( this.getClass().getName()
                                                 + ".convertDeegreeToDBGeometry() not implemented." );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 
 $Log: GenericSQLDatastore.java,v $
 Revision 1.45  2006/11/29 16:59:54  mschneider
 Improved handling of native coordinate transformation.

 Revision 1.44  2006/10/11 11:22:31  poth
 exception handling enhanced for method performQuery

 Revision 1.43  2006/09/27 20:30:20  mschneider
 Activated transformation of query and result SRS.

 Revision 1.42  2006/09/27 17:35:30  mschneider
 Refinement step (refiltering) works again.

 Revision 1.41  2006/09/27 16:09:11  mschneider
 Moved BBOX evaluation to GenericSQLWhereBuilder.

 Revision 1.40  2006/09/22 11:23:37  mschneider
 Javadoc fixes.

 Revision 1.39  2006/09/19 14:54:12  mschneider
 Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.

 Revision 1.38  2006/08/15 17:40:45  mschneider
 Javadoc fixes.

 Revision 1.37  2006/08/14 16:50:55  mschneider
 Changed to respect (optional) SortProperties.

 Revision 1.36  2006/08/11 09:48:40  poth
 not necessary import removed

 Revision 1.35  2006/07/26 18:55:59  mschneider
 Javadoc improvements.

 Revision 1.34  2006/07/26 12:42:17  poth
 support for alternative object identifier type (Integer) added

 Revision 1.33  2006/07/22 15:15:53  poth
 performance enhancement

 Revision 1.32  2006/07/12 14:46:19  poth
 comment footer added

 ********************************************************************** */