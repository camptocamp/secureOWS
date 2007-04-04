//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/quadtree/DBQuadtreeManagerWithNumberId.java,v 1.1 2006/10/20 07:56:00 poth Exp $
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
 * Same as @see org.deegree.io.quadtree.QuadtreeManager but uses
 * Integer values as IDs instead of UUIDs.
 * 
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/20 07:56:00 $
 * 
 * @since 2.0
 */
public class DBQuadtreeManagerWithNumberId extends DBQuadtreeManager {

    private static final ILogger LOG = LoggerFactory.getLogger( DBQuadtreeManagerWithNumberId.class );

    
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
     * @throws IndexException 
     */
    public DBQuadtreeManagerWithNumberId( JDBCConnection jdbc, String indexName, String table, String column,
                           String owner, int maxDepth )  {
        super( jdbc, indexName, table, column, owner, maxDepth );
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
     * @throws IndexException 
     */
    public DBQuadtreeManagerWithNumberId( String driver, String logon, String user, String password,
                           String encoding, String indexName, String table, String column,
                           String owner, int maxDepth ) {
        super( driver, logon, user, password, encoding, indexName, table, column, owner, maxDepth );
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
    public DBQuadtreeManagerWithNumberId( JDBCConnection jdbc, String table, String column, String owner ) {
        super( jdbc, table, column, owner );        
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
    public DBQuadtreeManagerWithNumberId( String driver, String logon, String user, String password,
                           String encoding, String table, String column, String owner ) {
        super( driver, logon, user, password, encoding, table, column, owner );
    }
   

    /**
     * stores one feature into the defined table
     * 
     * @param feature
     * @param jdbc
     * @throws Exception
     */
    private void storeFeature( Feature feature, int id, JDBCConnection jdbc )
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
            stmt.setInt( 1, id );
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
        sb.append( "FEATURE_ID " ).append( getDatabaseType( Types.INTEGER ) ).append("," );
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
            int id = i;
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
        
        int cnt = getMaxIdValue();

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
            int id = cnt + i + 1;
            qt.insert( id, env );
            storeFeature( feat, id, jdbc );
        }
        LOG.logInfo( " finished!" );
        sf.close();
    }

    /**
     * returns the maximum ID of the data table
     * @return
     * @throws IndexException
     */
    private int getMaxIdValue() throws IndexException {
        String sql = "SELECT MAX( FEATURE_ID ) FROM " + table;
        
        Connection con = null;
        DBConnectionPool pool = null;
        Statement stmt = null;
        int maxId = 0;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                          jdbc.getPassword() );

            stmt = con.createStatement();
            LOG.logDebug( sql );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                maxId = rs.getInt( 1 );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new IndexException( "could not read max( Faeture_Id ) from table: " + table ); 
        } finally {
            try {
                stmt.close();
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(),
                                        jdbc.getPassword() );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }
        
        return maxId;
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
        try {
            sb.append( "ID " ).append( getDatabaseType( Types.VARCHAR ) ).append( " NOT NULL," );
        } catch ( IndexException neverhappens ) {}
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
        try {
            sb2.append( "FK_QTNODE " ).append( getDatabaseType( Types.VARCHAR ) ).append( " NOT NULL," );
            sb2.append( "FK_ITEM " ).append( getDatabaseType( Types.INTEGER ) ).append( " NOT NULL )" );
        } catch ( IndexException neverhappens ) {}

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
 * $Log: DBQuadtreeManagerWithNumberId.java,v $
 * Revision 1.1  2006/10/20 07:56:00  poth
 * core methods extracted to interfaces
 *
 * Revision 1.2  2006/07/26 12:58:47  poth
 * implementation of appendShape method
 *
 * Revision 1.1  2006/07/26 12:43:12  poth
 * new quadtree manager using integer as datatype for object IDs
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
