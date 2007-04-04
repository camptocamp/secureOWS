//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/geodbconf/CreateDataSourceOperation.java,v 1.15 2006/10/17 20:31:19 poth Exp $
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.io.JDBCConnection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * This class represents a CreateDataSourceOperation. The perform() method takes care of
 * creating the tables (with the sql scripts passed in as a request parameter), substituting
 * dummy values in the template files (sql and data store configuration). 
 * 
 * @deprecated
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class CreateDataSourceOperation extends GeoDbConfOperation {

    public static final String REQUEST_NAME = "CreateDataSource";
    
    private String namespace;
	private String dataStoreName;
	private String crs;	
	private String template;
	private String title;
	private String abstractPar;
    private Envelope bbox;
    
   
    /**
     * Create a new CreateDataSourceOperation.
     * @param dataStoreName 
     * @param crs
     * @param bbox
     * @param template
     * @param title
     * @param abstractPar
     */
    private CreateDataSourceOperation( String namespace, String dataStoreName, String  crs, 
                                       Envelope bbox, String template, String title, 
                                       String abstractPar) {
    	
        this.namespace = namespace;
    	this.dataStoreName = dataStoreName.toLowerCase();
    	this.crs = crs.toLowerCase();
    	this.bbox = bbox;
    	this.template = template.toLowerCase();
    	this.title = title.toLowerCase();
    	this.abstractPar = abstractPar.toLowerCase();
    	this.namespace = namespace;
    }
    
    /**
     * 
     * Creates a new CreateDataSourceOperation
     * 
     * @param vname
     * @param vcrs
     * @param vbbox
     * @param vtemplate
     * @param vtitle
     * @param vabstractPar
     * @return
     * @throws InvalidParameterValueException if the paremeters are not valid (according to the 
     * specification)
     */
    public static CreateDataSourceOperation _create( String namespace, String vname, String  vcrs, 
    												String vbbox, String vtemplate, String vtitle,
													String vabstractPar)
    		
    		throws InvalidParameterValueException {
    	
        checkParameterValidity(vname, vcrs, vbbox, vtemplate, vtitle, vabstractPar, namespace);
    	    	
    	return null;/*new CreateDataSourceOperation( namespace,vname, vcrs, vbbox, vtemplate, 
    	    vtitle, vabstractPar);*/
    }
    
    
    /**
     * Instanciates a CreateDataSourceOperation object.
     * 
     * @param request the request conateining te parameters
     * @return a new CreateDataSourceOperation
     * @throws InvalidParameterValueException if the paremeters are not valid (according to the 
     * specification)
     */
    public static CreateDataSourceOperation create( HttpServletRequest request ) 
    	throws InvalidParameterValueException {
    	
        Map map = KVP2Map.toMap( request );
    	
        String nameParameter = (String)map.get("NAME");       
        String crsParameter = (String)map.get("CRS");        
        String bboxParameter = (String)map.get("BBOX");        
        String templateParameter = (String)map.get("TEMPLATE");        
        String titleParameter = (String)map.get("TITLE");        
        String abstractParameter = (String)map.get("ABSTRACT");
        String namespace = (String)map.get("NAMESPACE");

       checkParameterValidity( nameParameter, crsParameter, bboxParameter, templateParameter,
                				titleParameter, abstractParameter, namespace);
         
        if ( titleParameter == null ) {
           titleParameter = "";
        }
        if ( abstractParameter == null ) {
            abstractParameter = "";
        }

        Envelope env = ConfigUtils.createSafeBBOX( bboxParameter );
		
        return new CreateDataSourceOperation( namespace, nameParameter, crsParameter, env,
											  templateParameter, titleParameter, abstractParameter);

    }

    /**
     * @see org.deegree.conf.services.geodbconf.GeoDbConfOperation#getOperationName()
     */
    public String getOperationName() {
        return REQUEST_NAME;
    }
    
    /** 
     * @see org.deegree.conf.services.geodbconf.GeoDbConfOperation#performOperation( HttpServletRequest request, HttpServletResponse response )
     */
    public String performOperation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String filename = config.getTemplateDir().getAbsolutePath() + File.separator + template 
        	+ File.separator ;
        
        String sql = readTemplate( filename + ConfigUtils.SQL_CREATE, true );        
        sql = replacePars(sql);        
        
        JDBCConnection jdbcConnection = config.getJdbcConnection();        
        
        executeQuery( sql, jdbcConnection );        
        
        createDataSourceXML( filename + ConfigUtils.DATASTORE );
        
        return "Created data source";
    }

	/**
	 * 
	 * replaces mydsname with  the dataStoreName and mynamespace with public in the sql template 
	 * 
	 * @param sql
	 * @return 
	 */
	private String replacePars(String sql) {

		sql = sql.replaceAll( "mydsname", this.dataStoreName);
		//FIXME is public the correct namespace
		sql = sql.replaceAll( "mynamespace", this.namespace );
		
		if ( !"31466".equals(crs)) {
			String strippedCrs = crs.substring( crs.indexOf(":")+1, crs.length() );
        	sql.replaceAll("31466", strippedCrs);
        }
		
		return sql;
		
	}	
	
	/**
	 * Checks whether the parameters to instanciate the CreateDataSourceOperation Object are valid 
	 * 
	 * @param vname
	 * @param vcrs
	 * @param vbbox
	 * @param vtemplate
	 * @param vtitle
	 * @param vabstractPar
	 * @throws InvalidParameterValueException
	 */
	private static void checkParameterValidity(	String name, String  crs, String bbox, 
	                                           	String template, String title, 
	                                           	String abstractPar, String namespace)
	
		throws InvalidParameterValueException {

	    if ( name == null || name.length() == 0) {
	    	throw new InvalidParameterValueException( "Parameter 'NAME' is missing." );
	    }
	   
	    if ( crs == null || crs.length() == 0 ) {
	    	throw new InvalidParameterValueException( "Parameter 'CRS' is missing." );
	    }
	    
        if ( bbox == null ) {        
            throw new InvalidParameterValueException( "Parameter 'BBOX' is missing." );
        }        

        if ( template == null || template.length() == 0 ) {
            throw new InvalidParameterValueException( "Parameter 'TEMPLATE' is missing." );
        }
        
        if ( namespace == null || namespace.length() == 0 ) {
            throw new InvalidParameterValueException( "Parameter 'NAMESPACE' is missing." );
        }
        
		String regularExpression = "[A-Za-z_]+";
		validateInputExpression( name, regularExpression, "NAME");
		
		regularExpression = "([A-Z]+)(\\:)([0-9]+)";
		validateInputExpression( crs, regularExpression, "CRS");

		
        if ( template == null ) {
            throw new InvalidParameterValueException( "Parameter 'TEMPLATE' is missing." );
        }
        
        //FIXME what ot do if "TITLE" or "ABSTRACT" is null
        
	}	
	

	/**
     * @param filename to where the datastore template will be saved 
	 * @throws IOException
     */
    private void createDataSourceXML( String filename ) throws IOException {
        
        String xml = readTemplate( filename, false );
        xml = replacePars( xml );
        
        File outFile = new File( config.getDataSourceDirectory() + File.separator + dataStoreName 
            + "." + ConfigUtils.DATASOURCE_EXTENSION); 
        
        Writer writer = new FileWriter( outFile );
        writer.write( xml );
        writer.close();
       
    }	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CreateDataSourceOperation.java,v $
Revision 1.15  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.14  2006/08/24 06:38:30  poth
File header corrected

Revision 1.13  2006/06/28 20:20:15  poth
some code clean ups

Revision 1.12  2006/04/06 20:25:29  poth
*** empty log message ***

Revision 1.11  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.10  2005/12/12 13:13:09  ncho
SN
regularExpression supports _

Revision 1.9  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.8  2005/12/08 15:35:37  taddei
refactoring

Revision 1.7  2005/12/07 16:05:00  taddei
small refactoring, more javadoc

Revision 1.5  2005/12/05 13:08:38  taddei
added code to create data store xml

Revision 1.4  2005/12/05 12:29:13  ncho
SN
implemented methode checkParValidity()

Revision 1.3  2005/12/02 14:32:52  ncho
SN
new Methode
cleaned up

Revision 1.2  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.1  2005/12/01 14:00:51  taddei
renamed class

Revision 1.7  2005/12/01 13:59:37  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.6  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */