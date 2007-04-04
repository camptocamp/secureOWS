//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/quadtree/DBQuadtreeManager.java,v 1.2 2006/12/02 11:20:39 poth Exp $
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
 Aennchenstr. 19
 53115 Bonn
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
package org.deegree.io.quadtree;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BasicUUIDFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.JDBCConnection;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/12/02 11:20:39 $
 * 
 * @since 2.0
 */
public class DBQuadtreeManager {

    private static final ILogger LOG = LoggerFactory.getLogger( DBQuadtreeManager.class );

    protected JDBCConnection jdbc = null;

    protected String table = null;

    protected String column = null;

    protected String owner = null;

    protected String indexName = null;

    protected int maxDepth = 6;

    protected Quadtree qt = null;

    protected Envelope envelope = null;

    protected String backend = null;

    protected void checkForBackend() {
        String driver = jdbc.getDriver();
        // find out which database is used
        if ( driver.toUpperCase().indexOf( "POSTGRES" ) > -1 ) {
            backend = "POSTGRES";
        } else if ( driver.toUpperCase().indexOf( "SQLSERVER" ) > -1 ) {
            backend = "SQLSERVER";
        } else if ( driver.toUpperCase().indexOf( "INGRES" ) > -1
                    || driver.equals( "ca.edbc.jdbc.EdbcDriver" ) ) {
            backend = "INGRES";
        } else if ( driver.toUpperCase().indexOf( "HSQLDB" ) > -1 ) {
            backend = "HSQLDB";
        } else {
            backend = "GENERICSQL";
        }
    }

    /**
     * @param jdbc
     *            database connection info
     * @param indexName
     *            this name will be used to create the table that stores the nodes of a specific
     *            quadtree
     * @param table
     *            name of table the index shall be created for
     * @param column
     *            name of column the index shall be created for
     * @param owner
     *            owner of the table (optional, database user will be used if set to null )
     * @param maxDepth
     *            max depth of the generated quadtree (default = 6 if a value &lt; 2 will be passed)
     */
    public DBQuadtreeManager( JDBCConnection jdbc, String indexName, String table, String column,
                           String owner, int maxDepth ) {
        this.jdbc = jdbc;
        this.table = table.trim();
        this.column = column.trim();
        this.indexName = indexName.trim();
        if ( owner == null ) {
            this.owner = jdbc.getUser();
        } else {
            this.owner = owner;
        }
        if ( maxDepth > 1 ) {
            this.maxDepth = maxDepth;
        }

        checkForBackend();

        createIndexTable( indexName );
    }

    /**
     * 
     * @param driver
     *            database connection driver
     * @param logon
     *            database connection logon
     * @param user
     *            database user
     * @param password
     *            database user's password
     * @param encoding
     *            character encoding to be used (if possible)
     * @param indexName
     *            this name will be used to create the table that stores the nodes of a specific
     *            quadtree
     * @param table
     *            name of table the index shall be created for
     * @param column
     *            name of column the index shall be created for
     * @param owner
     *            owner of the table (optional, database user will be used if set to null )
     * @param maxDepth
     *            max depth of the generated quadtree (default = 6 if a value &lt; 2 will be passed)
     */
    public DBQuadtreeManager( String driver, String logon, String user, String password,
                           String encoding, String indexName, String table, String column,
                           String owner, int maxDepth ) {
        jdbc = new JDBCConnection( driver, logon, user, password, null, encoding, null );
        this.table = table.trim();
        this.column = column.trim();
        this.indexName = indexName.trim();
        if ( owner == null ) {
            this.owner = user;
        } else {
            this.owner = owner;
        }
        if ( maxDepth > 1 ) {
            this.maxDepth = maxDepth;
        }

        checkForBackend();

        createIndexTable( indexName );
    }

    /**
     * initializes a QuadtreeManager to access an alread existing Quadtree
     * 
     * @param jdbc
     *            database connection info
     * @param table
     *            name of table the index shall be created for
     * @param column
     *            name of column the index shall be created for
     * @param owner
     *            owner of the table (optional, database user will be used if set to null )
     * @throws IndexException
     */
    public DBQuadtreeManager( JDBCConnection jdbc, String table, String column, String owner ) {
        this.jdbc = jdbc;
        this.table = table.trim();
        this.column = column.trim();
        if ( owner == null ) {
            this.owner = jdbc.getUser();
        } else {
            this.owner = owner;
        }

        checkForBackend();
    }

    /**
     * initializes a QuadtreeManager to access an alread existing Quadtree
     * 
     * @param driver
     *            database connection driver
     * @param logon
     *            database connection logon
     * @param user
     *            database user
     * @param password
     *            database user's password
     * @param encoding
     *            character encoding to be used (if possible)
     * @param table
     *            name of table the index shall be created for
     * @param column
     *            name of column the index shall be created for
     * @param owner
     *            owner of the table (optional, database user will be used if set to null )
     * @throws IndexException
     */
    public DBQuadtreeManager( String driver, String logon, String user, String password,
                           String encoding, String table, String column, String owner ) {
        jdbc = new JDBCConnection( driver, logon, user, password, null, encoding, null );
        this.table = table.trim();
        this.column = column.trim();
        if ( owner == null ) {
            this.owner = user;
        } else {
            this.owner = owner;
        }

        checkForBackend();
    }

    /**
     * loads the metadata of a Index from the TAB_DEEGREE_IDX table
     * 
     * @throws IndexException
     */
    private int loadIndexMetadata()
                            throws IndexException {
        int fk_indexTree = -1;
        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            StringBuffer sb = new StringBuffer( 200 );
            sb.append( "Select INDEX_NAME, FK_INDEXTREE from TAB_DEEGREE_IDX where " );
            sb.append( "column_name = '" ).append( column ).append( "' AND " );
            sb.append( "table_name = '" ).append( table ).append( "' AND " );
            sb.append( "owner = '" ).append( owner ).append( "'" );

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( sb.toString() );

            if ( rs.next() ) {
                indexName = rs.getString( "INDEX_NAME" );
                fk_indexTree = rs.getInt( "FK_INDEXTREE" );
            } else {
                throw new IndexException( "could not read index metadata" );
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            throw new IndexException( "could not load quadtree definition from database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
        return fk_indexTree;
    }

    /**
     * returns the current Quadtree
     * 
     * @return
     * @throws IndexException
     */
    public Quadtree getQuadtree()
                            throws IndexException {
        if ( qt == null ) {
            qt = loadQuadtree();
        }
        return qt;
    }

    /**
     * loads an already existing quadtree
     * 
     * @return
     * @throws IndexException
     */
    protected Quadtree loadQuadtree()
                            throws IndexException {
        int fk_index = loadIndexMetadata();
        return new DBQuadtree( fk_index, indexName, jdbc );
    }

    /**
     * stores one feature into the defined table
     * 
     * @param feature
     * @param jdbc
     * @throws Exception
     */
    private void storeFeature( Feature feature, String id, JDBCConnection jdbc )
                            throws Exception {

        Connection con = null;
        DBConnectionPool pool = null;

        FeatureType ft = feature.getFeatureType();
        PropertyType[] ftp = ft.getProperties();
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "INSERT INTO " ).append( table ).append( '(' );
            sb.append( "FEATURE_ID," );
            for ( int i = 0; i < ftp.length; i++ ) {
                if ( ftp[i].getType() == Types.GEOMETRY ) {
                    sb.append( column ).append( ' ' );
                } else {
                    sb.append( ftp[i].getName().getLocalName() );
                }
                if ( i < ftp.length - 1 ) {
                    sb.append( ", " );
                }
            }
            sb.append( ") VALUES (?," );
            for ( int i = 0; i < ftp.length; i++ ) {
                sb.append( '?' );
                if ( i < ftp.length - 1 ) {
                    sb.append( ", " );
                }
            }
            sb.append( ')' );
            LOG.logDebug( "SQL for inser feature: " + sb );

            PreparedStatement stmt = con.prepareStatement( sb.toString() );
            stmt.setString( 1, id );
            for ( int i = 0; i < ftp.length; i++ ) {
                Object o = null;
                if ( feature.getProperties( ftp[i].getName() ) != null ) {
                    if ( feature.getProperties( ftp[i].getName() ).length > 0 ) {
                        o = feature.getProperties( ftp[i].getName() )[0].getValue();
                    }
                }
                if ( o == null ) {
                    stmt.setNull( i + 2, ftp[i].getType() );
                } else {
                    switch ( ftp[i].getType() ) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                        stmt.setString( i + 2, o.toString() );
                        break;
                    case Types.SMALLINT:
                    case Types.TINYINT:
                    case Types.INTEGER:
                    case Types.BIGINT:
                        stmt.setInt( i + 2, (int) Double.parseDouble( o.toString() ) );
                        break;
                    case Types.DOUBLE:
                    case Types.FLOAT:
                    case Types.DECIMAL:
                    case Types.NUMERIC:
                        stmt.setFloat( i + 2, Float.parseFloat( o.toString() ) );
                        break;
                    case Types.DATE:
                    case Types.TIME:
                    case Types.TIMESTAMP:
                        stmt.setDate( i + 2, (Date) o );
                        break;
                    case Types.GEOMETRY: {
                        StringBuffer gs = GMLGeometryAdapter.export( (Geometry) o );
                        String s = StringTools.replace( gs.toString(), ">",
                                                        " xmlns:gml=\"http://www.opengis.net/gml\">",
                                                        false );
                        if ( backend.equals( "POSTGRES" ) || backend.equals( "HSQLDB" ) ) {
                            stmt.setString( i + 2, s );
                        } else  if ( backend.equals( "INGRES" ) ) {
                            stmt.setObject( i + 2, new StringReader( s ) );
                        } else {
                            stmt.setObject( i + 2, s.getBytes() );
                        }
                        break;
                    }
                    default: {
                        LOG.logWarning( "unsupported type: " + ftp[i].getType() );
                    }
                    }
                }
            }

            stmt.execute();
            stmt.close();

        } catch ( Exception e ) {
            e.printStackTrace();
            throw new IndexException( "could not insert feature into database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * initializes the root node of the quadtree
     * 
     */
    protected void initRootNode( String fileName )
                            throws IndexException, IOException {
        ShapeFile sf = new ShapeFile( fileName );
        if ( envelope == null ) {
            envelope = sf.getFileMBR();
        }
        envelope = envelope.getBuffer( envelope.getWidth() / 20 );
        LOG.logInfo( "root envelope: " + envelope );
        sf.close();
        new DBNode( "1", envelope, null, indexName, jdbc, 1 );
    }

    /**
     * before importing a shape a user may set an envelope for the quadtree to bee created that is
     * different from the one of the shape by calling this method. Notice: calling this method does
     * not have any effect when calling
     * 
     * @see #appendShape(String) method.
     * @param envelope
     */
    public void setRootEnvelope( Envelope envelope ) {
        this.envelope = envelope;
    }

    /**
     * initializes a new Quadtree by adding a row into table TAB_QUADTREE and into TAB_QTNODE (->
     * root node)
     * 
     * @return
     * @throws IndexException
     */
    protected int initQuadtree( String fileName )
                            throws IndexException, IOException {

        initRootNode( fileName );

        Connection con = null;
        DBConnectionPool pool = null;
        int id = -1;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "INSERT INTO TAB_QUADTREE (" );
            if ( backend.equals( "INGRES" ) || backend.equals( "HSQLDB" ) ) {
                sb.append( "ID, " );
            }
            sb.append( "FK_ROOT, DEPTH ) VALUES ( " );
            if ( backend.equals( "INGRES" ) || backend.equals( "HSQLDB" ) ) {
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery( "SELECT MAX(ID) FROM TAB_QUADTREE" );
                rs.next();
                int myid = rs.getInt( 1 ) + 1;
                sb.append( myid + ", " );
            } 
            sb.append( " '1', ? ) " );

            PreparedStatement stmt = con.prepareStatement( sb.toString() );
            stmt.setInt( 1, maxDepth );
            stmt.execute();
            stmt.close();
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery( "select max(ID) from TAB_QUADTREE" );
            rs.next();
            id = rs.getInt( 1 );
            if ( id < 0 ) {
                throw new IndexException( "could not read ID of quadtree from database." );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
        return id;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public void insertIndexMetadata( int fk_indexTree )
                            throws IndexException {

        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "INSERT INTO TAB_DEEGREE_IDX ( " );
            if ( backend.equals( "INGRES" ) || backend.equals( "HSQLDB" ) ) {
                sb.append( "ID, " );
            }
            sb.append( "column_name, table_name, " );
            sb.append( "owner, INDEX_NAME, FK_indexTree ) " );
            sb.append( "VALUES ( " );
            if ( backend.equals( "INGRES" ) || backend.equals( "HSQLDB" ) ) {
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery( "SELECT MAX(ID) FROM TAB_QUADTREE" );
                rs.next();
                int myid = rs.getInt( 1 ) + 1;
                sb.append( myid + ", " );
            } 
            sb.append( "?, ?, ?, ?, ? ) " );
            PreparedStatement stmt = con.prepareStatement( sb.toString() );
            stmt.setString( 1, column );
            stmt.setString( 2, table );
            stmt.setString( 3, owner );
            stmt.setString( 4, indexName );
            stmt.setInt( 5, fk_indexTree );

            stmt.execute();
            stmt.close();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * creates table the shape data shall be stored
     * 
     * @param fileName
     * @throws IndexException
     */
    protected void createDataTable( String fileName )
                            throws IndexException, IOException {
        ShapeFile sf = new ShapeFile( fileName );
        FeatureType ft = null;
        try {
            ft = sf.getFeatureByRecNo( 1 ).getFeatureType();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new IndexException( e );
        }
        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "CREATE TABLE " ).append( table ).append( '(' );
        sb.append( "FEATURE_ID VARCHAR(50)," );
        PropertyType[] ftp = ft.getProperties();
        for ( int i = 0; i < ftp.length; i++ ) {
            if ( ftp[i].getType() == Types.GEOMETRY ) {
                sb.append( column ).append( ' ' );
            } else {
                sb.append( ftp[i].getName().getLocalName() ).append( ' ' );
            }
            sb.append( getDatabaseType( ftp[i].getType() ) );
            if ( i < ftp.length - 1 ) {
                sb.append( ", " );
            }
        }
        sb.append( ')' );

        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            Statement stmt = con.createStatement();
            LOG.logDebug( sb.toString() );
            stmt.execute( sb.toString() );
            stmt.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * returns the type name for a generic type code as used by SQLServer
     * 
     * @param dataTypeCode
     * @return
     * @throws IndexException
     */
    protected String getDatabaseType( int dataTypeCode )
                            throws IndexException {
        String driver = jdbc.getDriver();
        String backend = null;
        if ( driver.toUpperCase().indexOf( "POSTGRES" ) > -1 ) {
            backend = "POSTGRES";
        } else if ( driver.toUpperCase().indexOf( "SQLSERVER" ) > -1 ) {
            backend = "SQLSERVER";
        } else if ( driver.toUpperCase().indexOf( "INGRES" ) > -1
                    || driver.equals( "ca.edbc.jdbc.EdbcDriver" ) ) {
            backend = "INGRES";
        } else if ( driver.toUpperCase().indexOf( "HSQLDB" ) > -1 ) {
            backend = "HSQLDB";
        } else {
            backend = "GENERICSQL";
        } 
        String type = null;

        switch ( dataTypeCode ) {
        case Types.CHAR:
        case Types.VARCHAR:
            type = DBQuadtreeDataTypes.getString( backend + ".string" );
            break;
        case Types.SMALLINT:
        case Types.TINYINT:
        case Types.INTEGER:
        case Types.BIGINT:
            type = DBQuadtreeDataTypes.getString( backend + ".integer" );
            break;
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.DECIMAL:
        case Types.NUMERIC:
            type = DBQuadtreeDataTypes.getString( backend + ".float" );
            break;
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
            type = DBQuadtreeDataTypes.getString( backend + ".datetime" );
            break;
        case Types.GEOMETRY:
            type = DBQuadtreeDataTypes.getString( backend + ".geometry" );
            break;
        default:
            throw new IndexException( "unknown data type code: " + dataTypeCode );
        }

        return type;
    }

    /**
     * imports a shape into the database and builds a quadtree on it
     * 
     * @param fileName
     * @throws Exception
     */
    public void importShape( String fileName )
                            throws Exception, IOException {

        createDataTable( fileName );

        int qtid = initQuadtree( fileName );

        insertIndexMetadata( qtid );

        qt = new DBQuadtree( qtid, indexName, jdbc );

        ShapeFile sf = new ShapeFile( fileName );

        int b = sf.getRecordNum() / 100;
        if ( b == 0 )
            b = 1;
        int k = 0;
        Envelope sfEnv = sf.getFileMBR();
        BasicUUIDFactory uudiFac = new BasicUUIDFactory();
        for ( int i = 0; i < sf.getRecordNum(); i++ ) {
            Feature feat = sf.getFeatureByRecNo( i + 1 );
            if ( i % b == 0 ) {
                System.out.println( k + "%" );
                k++;
            }
            if ( i % 200 == 0 ) {
                System.gc();
            }
            Envelope env = feat.getDefaultGeometryPropertyValue().getEnvelope();
            LOG.logDebug( i + " --- " + env );
            if ( env == null ) {
                // must be a point geometry
                Point point = (Point) feat.getDefaultGeometryPropertyValue();
                double w = sfEnv.getWidth() / 1000;
                double h = sfEnv.getHeight() / 1000;
                env = GeometryFactory.createEnvelope( point.getX() - w / 2d, point.getY() - h / 2d,
                                                      point.getX() + w / 2d, point.getY() + h / 2d,
                                                      null );
            }
            String id = uudiFac.createUUID().toANSIidentifier();
            qt.insert( id, env );
            storeFeature( feat, id, jdbc );
        }
        LOG.logInfo( " finished!" );
        sf.close();
    }

    /**
     * appends the features of a shape to an existing datatable and inserts references into the
     * assigned quadtree table.
     * <p>
     * you have to consider that the quadtree is just valid for a defined area. if the features to
     * append exceeds this area the quadtree has to be rebuilded.
     * </p>
     * 
     * @param fileName
     * @throws Exception
     * @throws IOException
     */
    public void appendShape( String fileName )
                            throws Exception, IOException {

        ShapeFile sf = new ShapeFile( fileName );

        int b = sf.getRecordNum() / 100;
        if ( b == 0 )
            b = 1;
        int k = 0;
        qt = getQuadtree();
        Envelope sfEnv = sf.getFileMBR();
        BasicUUIDFactory uudiFac = new BasicUUIDFactory();
        for ( int i = 0; i < sf.getRecordNum(); i++ ) {
            Feature feat = sf.getFeatureByRecNo( i + 1 );
            if ( i % b == 0 ) {
                System.out.println( k + "%" );
                k++;
            }
            if ( i % 200 == 0 ) {
                System.gc();
            }
            Envelope env = feat.getDefaultGeometryPropertyValue().getEnvelope();
            if ( env == null ) {
                // must be a point geometry
                Point point = (Point) feat.getDefaultGeometryPropertyValue();
                double w = sfEnv.getWidth() / 1000;
                double h = sfEnv.getHeight() / 1000;
                env = GeometryFactory.createEnvelope( point.getX() - w / 2d, point.getY() - h / 2d,
                                                      point.getX() + w / 2d, point.getY() + h / 2d,
                                                      null );
            }
            String id = uudiFac.createUUID().toANSIidentifier();
            qt.insert( id, env );
            storeFeature( feat, id, jdbc );
        }
        LOG.logInfo( " finished!" );
        sf.close();
    }

    /**
     * creates a table that will store the nodes assigned to a specific quadtree index.
     * 
     * @param indexTable
     * @throws IndexException
     */
    protected void createIndexTable( String indexTable ) {
        StringBuffer sb = new StringBuffer( 2000 );
        sb.append( "CREATE TABLE " ).append( indexTable ).append( " ( " );
        sb.append( "ID varchar(150) NOT NULL," );
        sb.append( "minx float NOT NULL," );
        sb.append( "miny float NOT NULL," );
        sb.append( "maxx float NOT NULL," );
        sb.append( "maxy float NOT NULL," );
        sb.append( "FK_SUBNODE1 varchar(150)," );
        sb.append( "FK_SUBNODE2 varchar(150)," );
        sb.append( "FK_SUBNODE3 varchar(150)," );
        sb.append( "FK_SUBNODE4 varchar(150) )" );

        StringBuffer sb2 = new StringBuffer( 1000 );
        sb2.append( "CREATE TABLE " ).append( indexName ).append( "_ITEM ( " );
        sb2.append( "FK_QTNODE varchar(150) NOT NULL," );
        sb2.append( "FK_ITEM varchar(150) NOT NULL )" );

        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            Statement stmt = con.createStatement();
            stmt.execute( sb.toString() );
            stmt.close();

            stmt = con.createStatement();
            stmt.execute( sb2.toString() );
            stmt.close();
        } catch ( Exception e ) {
            // throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
    }

}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: DBQuadtreeManager.java,v $
 * Revision 1.2  2006/12/02 11:20:39  poth
 * bug fix - handling ingres db
 *
 * Revision 1.1  2006/10/20 07:56:00  poth
 * core methods extracted to interfaces
 *
 * Revision 1.34  2006/07/26 12:43:41  poth
 * support for alternative object identifier type (integer)
 *
 * Revision 1.33  2006/07/20 12:30:01  poth
 * *** empty log message ***
 *
 * Revision 1.32  2006/07/18 14:50:45  poth
 * *** empty log message ***
 *
 * Revision 1.31  2006/07/10 11:44:56  poth
 * log statements inserted
 *
 * Revision 1.30  2006/06/12 10:59:49  schmitz
 * Updated the Quadtree framework to work with INGRES database backends.
 *
 * Revision 1.29  2006/05/18 14:08:54  poth
 * file comments completed
 *
 * Revision 1.28  2006/05/16 09:01:45  poth
 * Ingres identification adapted
 *
 * Revision 1.27  2006/05/15 19:13:39  poth
 * support for Ingres added
 *
 * Revision 1.26  2006/05/12 15:26:05  poth
 * *** empty log message ***
 *
 * Revision 1.25  2006/05/12 06:46:23  poth
 * *** empty log message ***
 *
 * Revision 1.24  2006/05/11 16:37:35  poth
 * *** empty log message ***
 *
 * Revision 1.23  2006/05/11 13:26:31  poth
 * *** empty log message ***
 *
 * Revision 1.22  2006/05/11 08:02:14  poth
 * *** empty log message ***
 *
 *  Revision 1.21  2006/04/13 07:49:10  poth
 *  *** empty log message ***
 * 
 *  Revision 1.20  2006/04/06 20:25:31  poth
 *  *** empty log message ***
 * 
 *  Revision 1.19  2006/03/30 21:20:28  poth
 *  *** empty log message ***
 * 
 *  Revision 1.18  2006/01/31 16:23:14  mschneider
 *  Changes due to refactoring of org.deegree.model.feature package.
 * 
 *  Revision 1.17  2006/01/25 10:26:24  poth
 *  *** empty log message ***
 * 
 *  Revision 1.16  2006/01/16 20:36:40  poth
 *  *** empty log message ***
 * 
 *  Revision 1.15  2006/01/08 14:09:35  poth
 *  *** empty log message ***
 * 
 *  Revision 1.14  2005/12/18 19:06:30  poth
 *  no message
 * 
 *  Revision 1.13  2005/12/06 13:45:20  poth
 *  System.out.println substituted by logging api
 * 
 *  Revision 1.12  2005/12/04 19:21:09  poth
 *  no message
 * 
 *  Revision 1.11  2005/11/21 18:42:10  mschneider
 *  Refactoring due to changes in Feature class.
 * 
 *  Revision 1.10  2005/11/18 08:47:35  deshmukh
 *  Geometry cast replaced
 *  Revision
 * 
 ************************************************************************************************* */
