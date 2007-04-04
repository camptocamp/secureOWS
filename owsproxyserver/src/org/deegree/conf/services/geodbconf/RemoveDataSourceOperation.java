//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/geodbconf/RemoveDataSourceOperation.java,v 1.16 2006/10/17 20:31:19 poth Exp $
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
import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.io.JDBCConnection;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
/**
 * @deprecated
 * @author sncho
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveDataSourceOperation extends GeoDbConfOperation {

    private String dataStoreName;
   
    public static final String REQUEST_NAME = "RemoveDataSource"; 
    
    /**
     * 
     */
    private RemoveDataSourceOperation( String dataStoreName ) {
        this.dataStoreName = dataStoreName.toLowerCase();
        
    }

    public static RemoveDataSourceOperation create( String vname ) {
    		return new RemoveDataSourceOperation( vname );
    }
    
    public static RemoveDataSourceOperation create(HttpServletRequest request) 
    	throws Exception {
        Map map = KVP2Map.toMap( request );
        
        String nameParameter = (String)map.get( "NAME" );
         
         //TODO namespace????

         if ( nameParameter == null) {
             //par is missing gexception
             throw new InvalidParameterValueException( "Parameter 'NAME' is missing." );
         }
         
        return new RemoveDataSourceOperation( nameParameter );
    }
    
    /* (non-Javadoc)
     * @see org.deegree.conf.services.geodbconf.DbOperation#performOperation(java.util.HashMap)
     */
    public String performOperation( HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String filename = config.getTemplateDir().getAbsolutePath() + File.separator 
        	+ this.dataStoreName + File.separator;
        
        String sql = readTemplate( filename + ConfigUtils.SQL_REMOVE, true );
        sql = replacePars(sql);	        
        JDBCConnection jdbcConnection = config.getJdbcConnection();
        executeQuery( sql, jdbcConnection );
        
        File datastoreFile = new File( config.getDataSourceDirectory() + File.separator + dataStoreName 
            + "." + ConfigUtils.DATASOURCE_EXTENSION);
        if ( datastoreFile.exists() ){
            datastoreFile.delete();
        } else {
            throw new OGCWebServiceException( "Could not find datasource file: " 
                + datastoreFile.getAbsolutePath() );
        }
        
        return "Removed data source";
    }
    
    
    /* @param sql
	 * @return
	 */
	private String replacePars(String sql) {

		sql = sql.replaceAll( "mydsname", dataStoreName);
		//FIXME is public the correct namespace
		sql = sql.replaceAll( "mynamespace", "public" );
	
		return sql;
		
	}
    /**
     * @see org.deegree.conf.services.geodbconf.GeoDbConfOperation#getOperationName()
     */
    public String getOperationName() {
        return REQUEST_NAME;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoveDataSourceOperation.java,v $
Revision 1.16  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.15  2006/08/24 06:38:30  poth
File header corrected

Revision 1.14  2006/06/28 20:20:15  poth
some code clean ups

Revision 1.13  2006/04/06 20:25:29  poth
*** empty log message ***

Revision 1.12  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.11  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.10  2005/12/08 15:35:54  taddei
refactoring

Revision 1.9  2005/12/07 16:05:00  taddei
small refactoring, more javadoc

Revision 1.7  2005/12/05 13:10:31  taddei
update due to chanegs in DbOperation

Revision 1.6  2005/12/02 14:34:47  ncho
SN
new Methode
cleaned up

Revision 1.5  2005/12/01 13:59:37  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.4  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */