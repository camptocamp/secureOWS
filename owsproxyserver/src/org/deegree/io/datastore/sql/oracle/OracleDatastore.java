//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/oracle/OracleDatastore.java,v 1.36 2006/11/29 17:11:52 mschneider Exp $
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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.io.datastore.sql.oracle;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.ConstantContent;
import org.deegree.io.datastore.schema.content.FieldContent;
import org.deegree.io.datastore.schema.content.FunctionParam;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.io.datastore.schema.content.SQLFunctionCall;
import org.deegree.io.datastore.sql.AbstractSQLDatastore;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.datastore.sql.VirtualContentProvider;
import org.deegree.io.datastore.sql.StatementBuffer.StatementArgument;
import org.deegree.io.datastore.sql.wherebuilder.WhereBuilder;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * {@link Datastore} implementation for Oracle Spatial database systems. Supports Oracle Spatial for
 * Oracle 10g.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.36 $, $Date: 2006/11/29 17:11:52 $
 */
public class OracleDatastore extends AbstractSQLDatastore {

    protected static final ILogger LOG = LoggerFactory.getLogger( OracleDatastore.class );

    private static final String SRS_CODE_PROP_FILE = "srs_codes_oracle.properties";

    private static Map<String, Integer> nativeSrsCodeMap = new HashMap<String, Integer>();

    private static final int SRS_UNDEFINED = -1;    
    
    static {
        try {
            initSRSCodeMap();
        } catch ( IOException e ) {
            String msg = "Cannot load native srs code file '" + SRS_CODE_PROP_FILE + "'.";
            LOG.logError( msg, e );
        }
    }

    /**
     * Returns a specific <code>WhereBuilder</code> implementation for Oracle Spatial.
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
     * @return <code>WhereBuilder</code> implementation for Oracle Spatial
     * @throws DatastoreException
     */
    @Override
    public WhereBuilder getWhereBuilder( MappedFeatureType ft, Filter filter,
                                        SortProperty[] sortProperties,
                                        TableAliasGenerator aliasGenerator,
                                        VirtualContentProvider vcProvider )
                            throws DatastoreException {
        return new OracleSpatialWhereBuilder( ft, filter, sortProperties, aliasGenerator,
                                              vcProvider );
    }

    /**
     * Converts an Oracle specific geometry <code>Object</code> from the <code>ResultSet</code>
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
            LOG.logDebug( "Converting STRUCT to JGeometry." );
            JGeometry jGeometry = JGeometry.load( (STRUCT) value );
            try {
                LOG.logDebug( "Converting JGeometry to deegree geometry ('" + targetCS + "')" );
                geometry = JGeometryAdapter.wrap( jGeometry, targetCS );
            } catch ( Exception e ) {
                throw new SQLException( "Error converting STRUCT to Geometry: " + e.getMessage() );
            }
        }
        return geometry;
    }

    /**
     * Converts a deegree <code>Geometry</code> to an Oracle specific geometry object.
     * 
     * @param geometry
     * @param nativeSRSCode
     * @param conn
     * @return corresponding Oracle specific geometry object
     * @throws DatastoreException
     */
    @Override
    public STRUCT convertDeegreeToDBGeometry( Geometry geometry, int nativeSRSCode, Connection conn )
                            throws DatastoreException {

        JGeometry jGeometry = null;
        LOG.logDebug( "Converting deegree geometry to JGeometry." );
        try {
            jGeometry = JGeometryAdapter.export( geometry, nativeSRSCode );
        } catch ( GeometryException e ) {
            throw new DatastoreException( "Error converting deegree geometry to JGeometry: "
                                          + e.getMessage(), e );
        }

        LOG.logDebug( "Converting JGeometry to STRUCT." );
        STRUCT struct = null;
        try {
            struct = JGeometry.store( jGeometry, conn );
        } catch ( SQLException e ) {
            throw new DatastoreException(
                                          "Error converting JGeometry to STRUCT: " + e.getMessage(),
                                          e );
        }
        return struct;
    }

    /**
     * Returns the next value of the given SQL sequence.
     *
     * @param conn
     *            JDBC connection to be used.
     * @param sequence
     *            name of the SQL sequence
     * @return next value of the given SQL sequence
     * @throws DatastoreException
     *             if the value could not be retrieved
     */
    @Override
    public Object getSequenceNextVal( Connection conn, String sequence )
                            throws DatastoreException {

        Object nextVal = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery( "SELECT " + sequence + ".nextval FROM dual" );
                if ( rs.next() ) {
                    nextVal = rs.getObject( 1 );
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
            String msg = "Could not retrieve value for sequence '" + sequence + "': "
                         + e.getMessage();
            throw new DatastoreException( msg, e );
        }
        return nextVal;
    }

    /**
     * Converts the {@link StatementBuffer} into a {@link PreparedStatement}, which is initialized
     * and ready to be performed.
     * 
     * TODO remove this method (use super class method instead), change handling of JGeometry
     * 
     * @param conn
     *            connection to be used to create the <code>PreparedStatement</code>
     * @param statementBuffer
     * @return the <code>PreparedStatment</code>, ready to be performed
     * @throws SQLException
     *             if a JDBC related error occurs
     */
    @Override
    public PreparedStatement prepareStatement( Connection conn, StatementBuffer statementBuffer )
                            throws SQLException {
        LOG.logDebug( "Preparing statement: " + statementBuffer.getQueryString() );

        PreparedStatement preparedStatement = conn.prepareStatement( statementBuffer.getQueryString() );

        Iterator it = statementBuffer.getArgumentsIterator();
        int i = 1;
        while ( it.hasNext() ) {
            StatementArgument argument = (StatementArgument) it.next();
            Object parameter = argument.getArgument();
            int targetSqlType = argument.getTypeCode();
            if ( parameter != null ) {
                if ( targetSqlType == Types.DATE || targetSqlType == Types.TIMESTAMP ) {
                    if ( parameter instanceof String ) {
                        parameter = TimeTools.createCalendar( (String) parameter ).getTime();
                    }
                    parameter = new java.sql.Date( ( (Date) parameter ).getTime() );
                } else if ( parameter != null && parameter instanceof JGeometry ) {
                    parameter = JGeometry.store( (JGeometry) parameter, conn );
                } else if ( targetSqlType == Types.INTEGER || targetSqlType == Types.SMALLINT
                            || targetSqlType == Types.TINYINT ) {
                    parameter = Integer.parseInt( parameter.toString() );
                } else if ( targetSqlType == Types.DECIMAL || targetSqlType == Types.DOUBLE
                            || targetSqlType == Types.REAL || targetSqlType == Types.FLOAT ) {
                    parameter = Double.parseDouble( parameter.toString() );
                } else if ( targetSqlType == Types.NUMERIC ) {
                    try {
                        parameter = Integer.parseInt( parameter.toString() );
                    } catch ( Exception e ) {
                        parameter = Double.parseDouble( parameter.toString() );
                    }
                }
                if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                    try {
                        String typeName = Types.getTypeNameForSQLTypeCode( targetSqlType );
                        LOG.logDebug( "Setting argument " + i + ": type=" + typeName
                                      + ", value class=" + parameter.getClass() );
                        if ( parameter instanceof String || parameter instanceof Number
                             || parameter instanceof java.sql.Date ) {
                            LOG.logDebug( "Value: " + parameter );
                        }
                    } catch ( UnknownTypeException e ) {
                        throw new SQLException( e.getMessage() );
                    }
                }
                preparedStatement.setObject( i, parameter, targetSqlType );
            } else {
                setNullValue( preparedStatement, i, targetSqlType );
            }
            i++;
        }
        return preparedStatement;
    }

    /**
     * Transforms the incoming {@link Query} so that the {@link CoordinateSystem} of all spatial
     * arguments (BBOX, etc.) in the {@link Filter} match the SRS of the targeted
     * {@link MappingGeometryField}s.
     * <p>
     * NOTE: If this transformation can be performed by the backend (e.g. by Oracle Spatial), this
     * method should be overwritten to return the original input {@link Query}.
     * 
     * @param query
     *             query to be transformed
     * @return query with spatial arguments transformed to target SRS
     */
    @Override
    protected Query transformQuery( Query query ) {
        return query;
    }

    /**
     * Transforms the {@link FeatureCollection} so that the geometries of all contained geometry
     * properties use the requested SRS.  
     * 
     * @param fc
     *            feature collection to be transformed
     * @param targetSRS
     *            requested SRS
     * @return transformed FeatureCollection
     */
    @Override
    protected FeatureCollection transformResult( FeatureCollection fc, String targetSRS ) {
        return fc;
    }

    /**
     * Returns whether the datastore is capable of performing a native coordinate transformation
     * (using an SQL function call for example) into the given SRS.
     * 
     * @param targetSRS
     *            target spatial reference system (usually "EPSG:XYZ")
     * @return true, if the datastore can perform the coordinate transformation, false otherwise
     */
    @Override
    protected boolean canTransformTo( String targetSRS ) {
        return getNativeSRSCode( targetSRS ) != SRS_UNDEFINED;
    }

    /**
     * Returns an {@link SQLFunctionCall} that refers to the given {@link MappingGeometryField} in
     * the specified target SRS using a database specific SQL function.
     *  
     * @param geoProperty
     *            geometry property
     * @param targetSRS
     *            target spatial reference system (usually "EPSG:XYZ")
     * @return an {@link SQLFunctionCall} that refers to the geometry in the specified srs
     * @throws DatastoreException 
     */
    @Override
    public SQLFunctionCall buildSRSTransformCall( MappedGeometryPropertyType geoProperty, String targetSRS )
                            throws DatastoreException {
       
        int nativeSRSCode = getNativeSRSCode( targetSRS );
        if ( nativeSRSCode == SRS_UNDEFINED ) {
            String msg = Messages.getMessage( "DATASTORE_SQL_NATIVE_CT_UNKNOWN_SRS",
                                              this.getClass().getName(), targetSRS );
            throw new DatastoreException( msg );
        }

        MappingGeometryField field = geoProperty.getMappingField();          
        FunctionParam param1 = new FieldContent( field, new TableRelation[0] );
        FunctionParam param2 = new ConstantContent( "" + nativeSRSCode );

        SQLFunctionCall transformCall = new SQLFunctionCall( "SDO_CS.TRANSFORM($1,$2)",
                                                             field.getType(), param1, param2 );
        return transformCall;
    }

    @Override
    public String buildSRSTransformCall( String geomIdentifier, int nativeSRSCode )
                            throws DatastoreException {
        String call = "SDO_CS.TRANSFORM(" + geomIdentifier + "," + nativeSRSCode + ")";
        return call;
    }

    /**
     * Returns the database specific code for the given SRS name.
     *  
     * @param srsName
     *            spatial reference system name (usually "EPSG:XYZ")
     * @return the database specific SRS code, or -1 if no corresponding native code is known
     */
    int getNativeSRSCode( String srsName ) {
        Integer nativeSRSCode = nativeSrsCodeMap.get( srsName );
        if ( nativeSRSCode == null ) {
            return SRS_UNDEFINED;
        }
        return nativeSRSCode;
    }

    private void setNullValue( PreparedStatement preparedStatement, int i, int targetSqlType )
                            throws SQLException {
        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            try {
                String typeName = Types.getTypeNameForSQLTypeCode( targetSqlType );
                LOG.logDebug( "Setting argument " + i + ": type=" + typeName );
                LOG.logDebug( "Value: null" );
            } catch ( UnknownTypeException e ) {
                throw new SQLException( e.getMessage() );
            }
        }
        preparedStatement.setNull( i, targetSqlType );
    }

    private static void initSRSCodeMap()
                            throws IOException {
        InputStream is = OracleDatastore.class.getResourceAsStream( SRS_CODE_PROP_FILE );
        Properties props = new Properties();
        props.load( is );
        for ( Object key : props.keySet() ) {
            String nativeCodeStr = props.getProperty( (String) key ).trim();
            try {
                int nativeCode = Integer.parseInt( nativeCodeStr );
                nativeSrsCodeMap.put( (String) key, nativeCode );
            } catch ( NumberFormatException e ) {
                String msg = Messages.getMessage( "DATASTORE_SRS_CODE_INVALID", SRS_CODE_PROP_FILE,
                                                  nativeCodeStr, key );
                throw new IOException( msg );
            }
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: OracleDatastore.java,v $
 Revision 1.36  2006/11/29 17:11:52  mschneider
 Fixed #buildSRSTransformCall().

 Revision 1.35  2006/11/29 16:59:54  mschneider
 Improved handling of native coordinate transformation.

 Revision 1.34  2006/11/16 08:54:48  mschneider
 Javadoc improvements.

 Revision 1.33  2006/11/09 17:48:52  mschneider
 Implemented native coordinate transformations. Needs testing.

 Revision 1.32  2006/09/22 11:23:37  mschneider
 Javadoc fixes.

 Revision 1.31  2006/09/19 14:55:16  mschneider
 Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.

 Revision 1.30  2006/08/24 06:40:05  poth
 File header corrected

 Revision 1.29  2006/08/14 16:50:54  mschneider
 Changed to respect (optional) SortProperties.

 Revision 1.28  2006/07/26 18:56:20  mschneider
 Fixed spelling in method name.

 Revision 1.27  2006/06/15 18:30:48  poth
 *** empty log message ***

 Revision 1.26  2006/06/01 12:17:07  mschneider
 Fixed header + footer.

 ********************************************************************** */