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
package org.deegree.io.oraclegeoraster;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.spatial.georaster.GeoRasterException;
import oracle.spatial.georaster.JGeoRaster;
import oracle.spatial.georaster.JGeoRasterMeta;
import oracle.sql.STRUCT;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.JDBCConnection;
import org.deegree.model.spatialschema.Envelope;

/**
 * 
 * 
 *
 * @version $Revision: 1.11 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/07/05 12:59:09 $
 *
 * @since 2.0
 */
public class GeoRasterReader {
	
	private static final ILogger LOG = LoggerFactory.getLogger( GeoRasterReader.class );
	
    /**
     * 
     * @param grDesc
     * @param envelope requested envelope
     * @param level requested level (resolution)
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws GeoRasterException
     * @throws Exception
     */
	public static RenderedImage exportRaster( GeoRasterDescription grDesc, Envelope envelope) 
                                                throws SQLException, IOException,
																	GeoRasterException,
																	Exception {
        
        DBConnectionPool pool = DBConnectionPool.getInstance();
        JDBCConnection jdbc = grDesc.getJdbcConnection();
        Connection con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), 
                jdbc.getUser(), jdbc.getPassword() );
        
        RenderedImage ri = exportRaster( con, envelope, grDesc.getRdtTable(), 
                grDesc.getTable(), grDesc.getColumn(), grDesc.getIdentification(), 
                grDesc.getLevel() );
        
        /*
        System.out.println( grDesc.getTable() );
        System.out.println( grDesc.getLevel() );
        System.out.println(ri.getWidth() );
        System.out.println(ri.getHeight() );
        */
        
        pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), 
                jdbc.getUser(), jdbc.getPassword() );
        
        return ri;
	}

	/**
	 * 
	 * @param connection connnection to Oracle database
	 * @param envelope requested area
	 * @param rasterRDT name of the RDT-table
	 * @param rasterTable name of the table containing a georaster col
	 * @param geoRasterCol name of the geoRaster column
	 * @param identification SQL where clause that identifies the raster of interest
	 * @param level requested resolution level
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws GeoRasterException
	 * @throws Exception
	 */
	public static RenderedImage exportRaster( Connection connection, Envelope envelope,
                                              String rasterRDT, String rasterTable, 
                                              String geoRasterCol, String identification, 
                                              int level ) throws Exception {
		RenderedImage img = null;
		try {
		   
		   int rasterID = readRasterID( connection, identification, rasterTable, geoRasterCol );
		   
		   STRUCT struct = readGeoRasterMetadata( connection, rasterRDT, rasterTable, 
				   								  geoRasterCol, rasterID);
		 
		   LOG.logDebug( "mapping STRUCT to a JGeoRaster object" );
		   JGeoRaster jGeoRaster = JGeoRaster.load( struct );
		   jGeoRaster.setViewerUse( true );
		   Properties props = jGeoRaster.getProperties();
		
		   int maxWidth = Integer.parseInt( props.getProperty( "rasterInfo/dimensionSize_column" ) );
		   int maxHeight = Integer.parseInt( props.getProperty( "rasterInfo/dimensionSize_row" ) );
		
		   JGeoRasterMeta metaObj = jGeoRaster.getMetadataObject();
		
		   double xMin = metaObj.getX( 0, 0 );
		   double xMax = metaObj.getX( maxWidth - 1, maxHeight - 1 );
           double sc = Math.pow( 2, level );
		   double lenX = (xMax - xMin) * sc;
		   double yMin = metaObj.getY( 0, 0 );
		   double yMax = metaObj.getY( maxWidth - 1, maxHeight - 1 );
		   double lenY = (yMax - yMin) * sc;
		
		   int xMinCell = (int) Math.round( ( envelope.getMin().getX() - xMin ) * maxWidth / lenX );
		   int xMaxCell = (int) Math.round( ( envelope.getMax().getX() - xMin ) * maxWidth / lenX )-1;
		   int yMaxCell = (int) Math.round( ( envelope.getMin().getY() - yMin ) * maxHeight / lenY );
		   int yMinCell = (int) Math.round( ( envelope.getMax().getY() - yMin ) * maxHeight / lenY )-1;

           String bb = StringTools.concat( 100, xMinCell, " ", yMinCell, " ", xMaxCell, " ", yMaxCell);
           LOG.logInfo( "requested box:", bb );
           
		   LOG.logDebug( "reading georaster image, with level: " + level );
		   img = jGeoRaster.getRasterImage( connection, level, xMinCell, yMinCell, 
                                            xMaxCell, yMaxCell );
		
		} catch (SQLException e1) {
            e1.printStackTrace();
            String s = StringTools.concat( 1000, e1.getMessage(), " ", rasterTable, "; ", 
                                           rasterRDT, "; ", geoRasterCol, "; ", 
                                           identification, "; level: ", level);
            throw new RuntimeException( s );
        }catch (Exception e) {
            e.printStackTrace();
		   throw new RuntimeException( e );
		}
		return img;
	}
    

	/**
	 * 
	 * @param connection
	 * @param rasterRDT
	 * @param rasterTable
	 * @param geoRasterCol
	 * @param rasterID
	 * @return
	 * @throws SQLException
	 */
	private static STRUCT readGeoRasterMetadata(Connection connection, String rasterRDT, 
										 String rasterTable, String geoRasterCol, int rasterID)
										 throws SQLException {
		LOG.logDebug( "reading georaster" );
		PreparedStatement ps = 
		       connection.prepareStatement("select " + geoRasterCol + " from " + rasterTable + 
		    		   " a where a." + geoRasterCol +  ".rasterid = " + rasterID + " and a." + 
		    		   geoRasterCol + ".rasterdatatable = '" + rasterRDT.toUpperCase() + "'");
		   ResultSet resultset = ps.executeQuery();
		   if( !resultset.next() ) {
		       throw new SQLException( "No GeoRaster object exists at rasterid = " + rasterID + 
		    		   				   ", RDT = " + rasterRDT );
		   } 
		       
		   STRUCT struct = (STRUCT)resultset.getObject( geoRasterCol.toUpperCase() );
		   resultset.close();
		return struct;
	}

	/**
	 * returns the rasterID of the requested GeoRaster  
	 * 
	 * @param connection
	 * @param identification
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws GeoRasterException
	 */
	private static int readRasterID( Connection connection, String identification, String rasterTable,
							  String geoRasterCol) throws SQLException, GeoRasterException {
		
		LOG.logDebug( "reading rasterid " );
		
		String sql = "SELECT  a." + geoRasterCol + ".rasterid FROM " + rasterTable + 
	 				 " a where " + identification;
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery( sql );
		if( !rs.next() ) {
			throw new GeoRasterException( "Georaster with identification = " + identification + 
			     						  " not found!" );
		}
		int rasterID = rs.getInt( 1 );
		stmt.close();
		rs.close();
		return rasterID;
	}
		
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoRasterReader.java,v $
Revision 1.11  2006/07/05 12:59:09  poth
bug fix - raster coordinates calculation for other pyramid levels than '0'

Revision 1.10  2006/07/03 06:40:13  poth
*** empty log message ***

Revision 1.9  2006/06/30 14:15:29  poth
footer added


********************************************************************** */