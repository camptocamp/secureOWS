//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/geodbconf/GeoDbConfOperation.java,v 1.5 2006/08/24 06:38:30 poth Exp $
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

package org.deegree.conf.services.geodbconf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.io.DBConnectionPool;
import org.deegree.io.JDBCConnection;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * This class represents a WMSConfOperation (CreateDataStore or RemoveDataStore)
 * @deprecated
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public abstract class GeoDbConfOperation {
    
    protected GeoDBConfConfiguration config;
    public abstract String performOperation( HttpServletRequest request, HttpServletResponse response) throws Exception;
    public abstract String getOperationName();
    
    /**
     * 
     * @param config
     */
    public void setConfiguration( GeoDBConfConfiguration config ){
        
    	if ( config == null ){
            throw new NullPointerException( "config cannot be null." );
        }
    	
        this.config = config;
    }

    /**
     * Executes a sql query against the DB pointed at by the parameters in jdbcConnection 
     * @param sql
     * @param jdbcConnection
     * @throws Exception if could not create DB connection or if SQL went wrong
     */
    protected void executeQuery( String sql, JDBCConnection jdbcConnection ) throws Exception {
	      	            
		//SQLDatastoreConfiguration dataStoreConfig = new SQLDatastoreConfiguration( "dummy", PostGISDatastore.class, jdbcConnection);        
	    DBConnectionPool pool = DBConnectionPool.getInstance();        
	    Connection conn = null;
	    
	    try {
	    	
	        conn = pool.acquireConnection( jdbcConnection.getDriver(), 
	        							   jdbcConnection.getURL(),
										   jdbcConnection.getUser(),
										   jdbcConnection.getPassword() );
	        
	    } catch (Exception e) {
	        
	    	String msg = " Cannot acquire database connection: " + e.getMessage( );
	        throw new DatastoreException( msg, e );
	        
	    }
	    
	    Statement stmt = null;
	    
	    try {
	    	
	    	stmt = conn.createStatement();        		    
	        stmt.execute( sql );
			conn.commit();

	    } catch ( Exception e) {
	        
	    	conn.rollback( );
	        throw new Exception( e );
	        
	    }
	    
	    stmt.close( );
	    conn.close( );	    
	    
    }

	/**
	 * Reads a sql template file. If trim is true, white spaces are trimmed. This method
	 * als strips SQL comments (lines containing '--')
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected String readTemplate(String filename, boolean trim) throws IOException {
		
		StringBuffer sb = new StringBuffer( 1000 );		
		Reader fr = new FileReader( filename );
		BufferedReader br = new BufferedReader( fr );
		String s = null;
        
		while ( ( s = br.readLine() ) != null ) {
		    if ( trim ) {
		        s = s.trim();
		    }
        	if ( !s.startsWith( "--" ) ){
        		sb.append( s );
        	}
        }
        
        br.close();

		return sb.toString();
	}

	
	
	/**
	 * matches the input to the regularExpresion for a given Parameter
	 * @param input - user input
	 * @param regularExpression - regularExpresion
	 * @param Parameter - i.e CRS , NAME
	 * @throws InvalidParameterValueException
	 */
	protected static void validateInputExpression( String input, 
			 									   String regularExpression,
												   String Parameter) 
		throws InvalidParameterValueException  {		
			
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(input);
		matcher.lookingAt();		
		boolean valid = matcher.matches();
		
		if ( input == null ) {
		    throw 
		    	new InvalidParameterValueException( "Parameter '" + Parameter + "' is missing." );
		}  
		
		if ( !valid ) {
		    throw 
		    	new InvalidParameterValueException( "Parameter '" + Parameter + "' is not valid. " );
		} 
		
	}    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoDbConfOperation.java,v $
Revision 1.5  2006/08/24 06:38:30  poth
File header corrected

Revision 1.4  2006/04/06 20:25:29  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.2  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.1  2005/12/08 15:34:49  taddei
added (instead of DbOperation)

Revision 1.8  2005/12/07 16:05:00  taddei
small refactoring, more javadoc

Revision 1.6  2005/12/05 13:09:30  taddei
added .sql and .xml extensions for filenames; readTemplate may trim (sql) or not (xml)

Revision 1.5  2005/12/02 14:34:08  ncho
SN
cleaned up

Revision 1.4  2005/12/01 13:59:37  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.3  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */