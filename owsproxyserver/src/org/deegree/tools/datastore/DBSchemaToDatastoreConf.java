//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/datastore/DBSchemaToDatastoreConf.java,v 1.31 2006/09/28 18:58:55 poth Exp $
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
package org.deegree.tools.datastore;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.io.shpapi.ShapeFile;

/**
 * Example: java -classpath .;deegree.jar;$databasedriver.jar
 * org.deegree.tools.datastore.DBSchemaToDatastoreConf -tables mytable,myothertable -user dev
 * -password dev -driver oracle.jdbc.OracleDriver -url jdbc:oracle:thin:@localhost:1521:devs -output
 * e:/temp/schema.xsd<br>
 * or for shapefile:<br>
 * java -classpath .;deegree.jar
 * org.deegree.tools.datastore.DBSchemaToDatastoreConf -url c:/data/myshape 
 * -driver SHAPE -output e:/temp/schema.xsd<br>
 * 
 * @version $Revision: 1.31 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.31 $, $Date: 2006/09/28 18:58:55 $
 * 
 * @since 2.0
 */
public class DBSchemaToDatastoreConf {

    private static final ILogger LOG = LoggerFactory.getLogger( DBSchemaToDatastoreConf.class );

    private String[] tables = null;

    private String user = null;

    private String pw = null;

    private String driver = null;

    private String logon = null;

    private String backend = null;
    
    private String srs = "Enter A CRS!!!!!!"; 

    /**
     * 
     * @param table
     *            list of table names used for one featuretype
     * @param user
     *            database user
     * @param pw
     *            users password
     * @param driver
     *            database driver
     * @param logon
     *            database URL/logon
     */
    public DBSchemaToDatastoreConf( String[] tables, String user, String pw, String driver,
                                    String logon, String srs ) {
        this.driver = driver;
        this.logon = logon;
        this.pw = pw;
        this.user = user;
        this.tables = tables;
        if ( srs != null ) {
            this.srs = srs;
        }
        
        if ( driver.toUpperCase().indexOf( "ORACLE" ) > -1 ) {
            backend = "ORACLE";
        } else if ( driver.toUpperCase().indexOf( "POSTGRES" ) > -1 ) {
            backend = "POSTGIS";
        } else if ( driver.toUpperCase().contains( "SHAPE" ) ) {
            backend = "SHAPE";
        } else {
            backend = "GENERICSQL";
        }
        
    }

    /**
     * creates a schema/datastore configuration for accessin database table through deegree WFS
     * 
     * @return
     * @throws Exception
     */
    public String run() throws Exception {
        StringBuffer sb = new StringBuffer( 1000 );
        

        if( backend.equals( "SHAPE") ){

            //TODO throw RE if tbl.len != 1
            
            printShapeHeader( sb, tables[0] );

            File f = new File( tables[0] ); 
            ShapeFile shp = new ShapeFile( f.getAbsolutePath() );
            
            printComplextHeader( sb, f.getName() );
            
            String[] dataTypes = shp.getDataTypes();

            
            printProperty( f.getName(), "GEOM", 2002, "GEOM", -9999, sb );
            
            String[] props = shp.getProperties();
            for ( int i = 0; i < props.length; i++ ) {
                int sqlCode = toSQLCode( dataTypes[i] );
                printProperty( tables[0], props[i], sqlCode, 
                               Types.getTypeNameForSQLTypeCode( sqlCode ),
                               toPrecision( dataTypes[i] ), sb );
            }
            
            printComplexFooter( sb );
        
            shp.close();
            
        } else {
            
            printHeader( sb );
            
            for (int k = 0; k < tables.length; k++) {
                System.out.println( driver );
                System.out.println( logon );
                Connection con =                    
                    DBConnectionPool.getInstance().acquireConnection( driver, logon, user, pw );
                Statement stmt = con.createStatement();
                // ensure that we do not get a filled resultset because we just
                // need the metainformation
                LOG.logDebug( "read table: ", tables[k] );
                ResultSet rs = stmt.executeQuery( "select * from " + tables[k] + " where 1 = 2" );
    
                ResultSetMetaData rsmd = rs.getMetaData();
                int cols = rsmd.getColumnCount();
    
                printComplextHeader( sb, tables[k] );
                for (int i = 0; i < cols; i++) {
                    if ( rsmd.getColumnType( i + 1 ) != 2004 ) {
                        int tp = rsmd.getColumnType( i + 1 );
                        String tpn = Types.getTypeNameForSQLTypeCode( tp );
                        LOG.logDebug( tables[k] + "." + rsmd.getColumnName( i + 1 ) + ": " + tpn );
                        // add property just if type != BLOB
                        printProperty( tables[k], rsmd.getColumnName( i + 1 ), 
                                       rsmd.getColumnType( i + 1 ), 
                                       tpn, rsmd.getPrecision( i + 1 ), sb );
                    } else {
                        LOG.logDebug( "skiped: " + tables[k] + '.' + rsmd.getColumnName( i + 1 ) + ": "
                            + rsmd.getColumnTypeName( i + 1 ) );
                    }
                }
    
                DBConnectionPool.getInstance().releaseConnection( con, driver, logon, user, pw );
                printComplexFooter( sb );
            }
        }
        printFooter( sb );

        return sb.toString();
    }

    private int toPrecision( String dbfType ) {
        int precision = 0;
        
        if( dbfType.equalsIgnoreCase( "N" ) ){
            precision = 1;
        } else if( dbfType.equalsIgnoreCase( "F" ) ) {
            precision = 2;
        } 
        
        return precision;
    }

    private int toSQLCode( String dbfType ) {
        
        int type = -9999;
        
        if( dbfType.equalsIgnoreCase( "C" ) ){
            type = Types.VARCHAR;
        } else if( dbfType.equalsIgnoreCase( "F" ) || dbfType.equalsIgnoreCase( "N" ) ) {
            type = Types.NUMERIC;
        } else if( dbfType.equalsIgnoreCase( "D" ) || dbfType.equalsIgnoreCase( "M" ) ) {
            type = Types.DATE;
        } else if( dbfType.equalsIgnoreCase( "L" ) ) {
            type = Types.BOOLEAN;
        } else if( dbfType.equalsIgnoreCase( "B" ) ) {
            type = Types.BLOB;
        }
        
        if ( type == -9999 ){
            throw new RuntimeException( "Type '" + dbfType + "' is not suported." );
        }
        
        return type;
    }

    /**
     * adds the header of the configuration/schema
     * 
     * @param sb
     */
    private void printHeader( StringBuffer sb ) {

        sb.append( "<xsd:schema targetNamespace=\"http://www.deegree.org/app\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:deegreewfs=\"http://www.deegree.org/wfs\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:app=\"http://www.deegree.org/app\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n"
                + "    <xsd:import namespace=\"http://www.opengis.net/gml\" schemaLocation=\"http://schemas.opengis.net/gml/3.1.1/base/feature.xsd\"/>\r\n"
                + "    <xsd:import namespace=\"http://www.opengis.net/gml\" schemaLocation=\"http://schemas.opengis.net/gml/3.1.1/base/geometryAggregates.xsd\"/>\r\n"
                + "    <!-- configuration for the persistence backend to be used -->\r\n"
                + "    <xsd:annotation>\r\n"
                + "        <xsd:appinfo>\r\n"
                + "            <deegreewfs:Prefix>app</deegreewfs:Prefix>\r\n"
                + "            <deegreewfs:Backend>" + backend + "</deegreewfs:Backend>\r\n"
                + "            <deegreewfs:DefaultSRS>" + srs + "</deegreewfs:DefaultSRS>\r\n"
                + "            <JDBCConnection xmlns=\"http://www.deegree.org/jdbc\">\r\n"
                + "                <Driver>" + driver + "</Driver>\r\n"
                + "                <Url>" + logon + "</Url>\r\n"
                + "                <User>" + user + "</User>\r\n"
                + "                <Password>" + pw + "</Password>\r\n"
                + "                <SecurityConstraints/>\r\n"
                + "                <Encoding>iso-8859-1</Encoding>\r\n"
                + "            </JDBCConnection>\r\n"
                + "        </xsd:appinfo>\r\n"
                + "    </xsd:annotation>" );

    }

    /**
     * adds the header of the configuration/schema
     * 
     * @param sb
     */
    private void printShapeHeader( StringBuffer sb, String filename ) {

        sb.append( "<xsd:schema targetNamespace=\"http://www.deegree.org/app\" " ) 
            .append( "xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " )
            .append( "xmlns:deegreewfs=\"http://www.deegree.org/wfs\" ")
            .append( "xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:app=\"http://www.deegree.org/app\" " )
            .append( "elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n " )
            .append(  " <xsd:import namespace=\"http://www.opengis.net/gml\" ")
            .append( "schemaLocation=\"http://schemas.opengis.net/gml/3.1.1/base/feature.xsd\"/>\r\n" )
            .append( "    <xsd:import namespace=\"http://www.opengis.net/gml\" " )
            .append( "schemaLocation=\"http://schemas.opengis.net/gml/3.1.1/base/geometryAggregates.xsd\"/>\r\n" )
            .append( "    <!-- configuration for the persistence backend to be used -->\r\n" )
            .append( "  <xsd:annotation> ")
            .append( "  <xsd:appinfo>\n")
            .append( "  <deegreewfs:Prefix>app</deegreewfs:Prefix>\n")
            .append( "  <deegreewfs:Backend>SHAPE</deegreewfs:Backend>\n")
            .append( "  <deegreewfs:File>" ).append( filename ).append( "</deegreewfs:File>\n")
            .append( "  <deegreewfs:DefaultSRS>" + srs + "</deegreewfs:DefaultSRS>\n")
            .append( "  </xsd:appinfo>\n")
            .append( "  </xsd:annotation>\n" );
    }    
    
    /**
     * adds a header for a feature type to the schema
     * 
     * @param sb
     * @param table
     *            name of the table the feature type is assigned to
     * @throws Exception
     */
    private void printComplextHeader( StringBuffer sb, String table ) throws Exception {
        String idField = getPKeyName( table );
        String tp = "INTEGER";
        if ( backend.equals( "GENERICSQL") ) {
            tp = "VARCHAR";
        }
        sb.append( "<!-- ============================================================== -->\n"
            + "<xsd:element name='" + table + "' type='app:" + table + "Type' "
            + "substitutionGroup=\"gml:_Feature\">\r\n" + "        " 
            +     "<xsd:annotation>\r\n"
            + "       <xsd:appinfo>\r\n" 
            + "          <deegreewfs:table>" + table + "</deegreewfs:table>\r\n" + "                <deegreewfs:gmlId prefix=\"ID_\">\r\n"
            + "          <deegreewfs:MappingField field='" + idField + "' type=\"" + tp +  "\"/>\r\n" + "                </deegreewfs:gmlId>\r\n"
            + "      </xsd:appinfo>\r\n" 
            + "   </xsd:annotation>\r\n"
            + "</xsd:element>\r\n"
            + "<!-- ============================================================== -->\n"
            + "    <xsd:complexType name='" + table + "Type'>\r\n"
            + "        <xsd:complexContent>\r\n"
            + "            <xsd:extension base=\"gml:AbstractFeatureType\">\r\n"
            + "                <xsd:sequence>\r\n" );
    }

    /**
     * adds the footer of a feature type definition
     * 
     * @param sb
     */
    private void printComplexFooter( StringBuffer sb ) {
        sb.append( " </xsd:sequence>\r\n"
            + "            </xsd:extension>\r\n" 
            + "        </xsd:complexContent>\r\n"
            + "    </xsd:complexType>\r\n" );
    }

    private void printFooter( StringBuffer sb ) {
        sb.append( "</xsd:schema>" );
    }

    /**
     * adds a property assigned to a database table field to the schema
     * 
     * @param tableName
     *            table name
     * @param name
     *            property name
     * @param type
     *            xsd type name
     * @param typeName
     *            SQL type name
     * @param precision
     *            number precision if type is a number
     * @param sb
     * @throws SQLException 
     * @throws DBPoolException 
     */
    private void printProperty( String tableName, String name, int type, String typeName,
                               int precision, StringBuffer sb ) throws DBPoolException, SQLException {

        String tp = Types.getXSDTypeForSQLType( type, precision );
        if ( !tp.startsWith( "gml:" ) ) {
            tp = "xsd:"  + tp;
        }
        int srid = -1;
        if ( tp.equals( "gml:GeometryPropertyType" ) ) {
            typeName = "GEOMETRY";
            if ( backend.equals( "ORACLE" ) ) {
                srid = getOracleSRID( tableName, name );
            } else if ( backend.equals( "POSTGIS" ) ) {
                srid = getPostGisSRID( tableName, name );
            } 
            sb.append( "<xsd:element name='" + name.toLowerCase() + "' type='" + tp + "'>\r\n" 
                + "    <xsd:annotation>\r\n"
                + "        <xsd:appinfo>\r\n" 
                + "            <deegreewfs:Content>\r\n"
                + "                <deegreewfs:MappingField field='" + name + "' type='"+ typeName.toUpperCase() + "' srs='" + srid + "'/>\r\n" 
                + "            </deegreewfs:Content>\r\n"
                + "        </xsd:appinfo>\r\n" 
                + "    </xsd:annotation>\r\n" 
                + "</xsd:element>\r\n" );
        } else {
            sb.append( "<xsd:element name='" + name.toLowerCase() + "' type='" + tp + "'>\r\n" 
                + "    <xsd:annotation>\r\n"
                + "        <xsd:appinfo>\r\n" 
                + "            <deegreewfs:Content>\r\n"
                + "                <deegreewfs:MappingField field='" + name + "' type='" + typeName.toUpperCase() + "'/>\r\n" 
                + "            </deegreewfs:Content>\r\n"
                + "        </xsd:appinfo>\r\n" 
                + "    </xsd:annotation>\r\n" 
                + "</xsd:element>\r\n" );
        }
    }

    /**
     * Retrieve the srid from the postgis database.
     * 
     * @param tableName
     * @param name
     * @return int
     * @throws DBPoolException
     * @throws SQLException 
     */
    private int getPostGisSRID( String tableName, String name ) throws DBPoolException, SQLException {

        int srid = -1;
        Connection con = null;
        try {
            con = DBConnectionPool.getInstance().acquireConnection( driver, logon, user,
                pw );
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT a.srid FROM GEOMETRY_COLUMNS a WHERE "
                + "a.f_table_name='" + tableName.toUpperCase() + "' AND a.f_geometry_column='"
                + name.toUpperCase() + "'" );

            while (rs.next()) {
                srid = rs.getInt( 1 );
            }
            
            if( srid == 0 ) {
                srid = -1;
            }

        } catch (DBPoolException e) {
            throw new DBPoolException(
                "Unable to acquire a connection from the DBConnectionPool for the postgis database. ", e );
        } catch (SQLException e) {
            throw new SQLException(
                "Error performing the postgis query to retrieve the srid from the GEOMETRY_COLUMNS table. "
                    + e );
        } finally {
            DBConnectionPool.getInstance().releaseConnection( con, driver, logon, user, pw );
        }
        return srid;
    }

    /**
     * Retrieve the srid from the oracle database.
     * 
     * @param tableName
     * @param name
     * @return int
     * @throws DBPoolException 
     * @throws SQLException 
     */
    private int getOracleSRID( String tableName, String name ) throws DBPoolException, SQLException {

        int srid = -1;
        Connection con = null;
        try {
            con = DBConnectionPool.getInstance().acquireConnection( driver, logon, user,
                pw );
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT a.srid FROM USER_SDO_GEOM_METADATA a WHERE "
                + "a.table_name='" + tableName.toUpperCase() + "' AND a.column_name='"
                + name.toUpperCase() + "'" );

            while (rs.next()) {
                srid = rs.getInt( 1 );
            }
            if( srid == 0 ) {
                srid = -1;
            }

        } catch (DBPoolException e) {
            throw new DBPoolException( "Unable to acquire a connection from the " +
                                       "DBConnectionPool for the oracle database. ", e );
        } catch (SQLException e) {
            throw new SQLException( "Error performing the oracle query to retrieve the " +
                                    "srid from the GEOMETRY_COLUMNS table. " + e );
        } finally {
            DBConnectionPool.getInstance().releaseConnection( con, driver, logon, user, pw );
        }

        return srid;
    }

    /**
     * returns the name of the primary key of the passed table
     * 
     * @param table
     * @return
     * @throws Exception
     */
    private String getPKeyName( String table ) throws Exception {
        if ( backend.equals( "ORACLE" ) ) {
            return getOraclePKeyName( table );
        } else if ( backend.equals( "POSTGIS" ) ) {
            return getPostgresPKeyName( table );
        } else if ( backend.equals( "GENERICSQL" ) ) {
            return "FEATURE_ID";
        } else {
            return "ID";
        }
    }

    /**
     * returns the primary key of a table from the oracle database
     * 
     * @param table
     * @return
     * @throws Exception
     */
    private String getOraclePKeyName( String table ) throws Exception {

        String query = "SELECT cols.column_name "
            + "FROM all_constraints cons, all_cons_columns cols " + "WHERE cols.table_name = '"
            + table.toUpperCase() + "' " + "AND cons.constraint_type = 'P' "
            + "AND cons.constraint_name = cols.constraint_name " + "AND cons.owner = cols.owner "
            + "ORDER BY cols.table_name, cols.position ";

        Connection con = DBConnectionPool.getInstance().acquireConnection( driver, logon, user, pw );
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( query );
        Object id = null;
        if ( rs.next() ) {
            id = rs.getObject( 1 );
        }
        if ( id == null ) {
            id = "ID";
        }
        DBConnectionPool.getInstance().releaseConnection( con, driver, logon, user, pw );
        
        return id.toString();
    }

    /**
     * returns the primary key of a table from the postgres database
     * 
     * @param table
     * @return
     * @throws Exception
     */
    private String getPostgresPKeyName( String table ) throws Exception {
        String query = "select b.column_name from pg_catalog.pg_constraint a, "
            + "information_schema.constraint_column_usage b Where a.conname = "
            + "b.constraint_name AND a.contype = 'p' AND " + "b.table_name = '"
            + table.toLowerCase() + "'";
        Connection con = DBConnectionPool.getInstance().acquireConnection( driver, logon, user, pw );
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( query );
        Object id = null;
        if ( rs.next() ) {
            id = rs.getObject( 1 );
        }
        if ( id == null ) {
            id = "ID";
        }
        DBConnectionPool.getInstance().releaseConnection( con, driver, logon, user, pw );
        return id.toString();
    }

    private static void validate( HashMap map ) throws Exception {
        if ( map.get( "-tables" ) == null ) {
            throw new Exception( "-tables must be set" );
        }
        if ( map.get( "-user" ) == null ) {
            map.put( "-password", " " );
        }
        if ( map.get( "-password" ) == null ) {
            map.put( "-password", " " );
        }
        if ( map.get( "-driver" ) == null ) {
            throw new Exception( "-driver must be set" );
        }
        if ( map.get( "-url" ) == null && !"SHAPE".equalsIgnoreCase( (String)map.get( "-driver" ) ) ) {
            throw new Exception( "-url (database connection string) must be set" );
        }
        if ( map.get( "-output" ) == null ) {
            throw new Exception( "-output must be set" );
        }
    }

    public static void main( String[] args ) throws Exception {

        HashMap map = new HashMap();
        for (int i = 0; i < args.length; i += 2) {
            System.out.println( args[i + 1] );            
            map.put( args[i], args[i + 1] );
        }

        validate( map );
        LOG.logInfo( map.toString() );
        String tmp = (String) map.get( "-tables" );
        String[] tables = StringTools.toArray( tmp, ",;|", true );
        String user = (String) map.get( "-user" );
        String pw = (String) map.get( "-password" );
        String driver = (String) map.get( "-driver" );
        String url = (String) map.get( "-url" );
        String output = (String) map.get( "-output" );
        String srs = (String) map.get( "-srs" );

        DBSchemaToDatastoreConf stc = new DBSchemaToDatastoreConf( tables, user, pw, driver, url, srs );

        String conf = stc.run();
        FileWriter fw = new FileWriter( output );
        fw.write( conf );
        fw.close();
        System.exit( 0 );
    }
    
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: DBSchemaToDatastoreConf.java,v $
 * Revision 1.31  2006/09/28 18:58:55  poth
 * *** empty log message ***
 *
 * Revision 1.30  2006/09/15 19:22:02  poth
 * code formatting
 *
 * Revision 1.29  2006/09/08 09:55:25  poth
 * debug statement added
 *
 * Revision 1.28  2006/07/10 12:14:44  poth
 * support for automatic ID identification for GenericSQLDB datastores
 *
 * Revision 1.27  2006/07/04 07:34:51  poth
 * *** empty log message ***
 *
 * Revision 1.26  2006/06/05 15:22:27  poth
 * support for definition of DefaultSRS added
 *
 * Revision 1.25  2006/05/26 19:18:47  poth
 * bug fix using shapes with absolute path informations
 *
 * Revision 1.24  2006/05/26 14:55:33  taddei
 * added support for shapes
 *
 * Revision 1.23  2006/05/26 07:06:17  poth
 * footer corrected
 *
 * Revision 1.22  2006/04/11 08:16:56  poth
 * *** empty log message ***
 * 
 * Revision 1.21  2006/04/07 15:24:44  deshmukh
 * added functionality for oracle table  primary key detection and srid retrieval from the oracle and postgis database.
 * 
 * Revision 1.18 2006/04/06 20:25:29 poth ** empty log message ***
 * 
 ************************************************************************************************* */
