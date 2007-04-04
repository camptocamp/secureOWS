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
package org.deegree.io.oraclegeoraster;

import org.deegree.io.JDBCConnection;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/07/12 14:46:19 $
 *
 * @since 2.0
 */
public class GeoRasterDescription {

    private JDBCConnection jdbcConnection = null;
    private String table = null;
    private String rdtTable = null;
    private String column = null;
    private String identification = null;
    private int level = 0;
    
    /**
     * 
     * @param jdbcConnection
     * @param table
     * @param rdtTable
     * @param column
     * @param identification
     */
    public GeoRasterDescription(JDBCConnection jdbcConnection, String table, 
                                String rdtTable, String column, String identification,
                                int level) {
        super();
        // TODO Auto-generated constructor stub
        this.jdbcConnection = jdbcConnection;
        this.table = table;
        this.rdtTable = rdtTable;
        this.column = column;
        this.identification = identification;
        this.level = level;
    }
    
    /**
     * @return name of the georaster column
     */
    public String getColumn() {
        return column;
    }
    
    /**
     * @return SQL where clause that identifies the target georaster
     */
    public String getIdentification() {
        return identification;
    }
    
    /**
     * @return JDBC description for accessing the DB
     */
    public JDBCConnection getJdbcConnection() {
        return jdbcConnection;
    }
    
    /**
     * @return name of the RDT table assigned to a GeoRaster column
     */
    public String getRdtTable() {
        return rdtTable;
    }
    
    /**
     * @return name of the tab le containing a georaster column to access
     */
    public String getTable() {
        return table;
    }
    
    /**
     * returns the Oracle tree level of the describe coverage
     * @return
     */
    public int getLevel() {
        return level;
    }
    
    public String toString() {
        String s = "GeoRasterDescription: \n" +
                   "table: " + table + "\n" +
                   "RDTTable: " + rdtTable + "\n" +
                   "column: " + column + "\n" +
                   "identification: " + identification + "\n" +
                   "JDBC: " + jdbcConnection + "\n";
        return s;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoRasterDescription.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
