//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/geodbconf/GeoDBConfConfiguration.java,v 1.12 2006/08/24 06:38:30 poth Exp $
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
import java.net.URL;

import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.IODocument;
import org.deegree.io.JDBCConnection;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;

/**
 * This class represents a configuration for the GeoDBConf Service.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class GeoDBConfConfiguration {
    
    private static GeoDBConfConfiguration INSTANCE; 
    
    private JDBCConnection jdbcConnection;  
    
    private File templateDirectory;
    
    private File dataSourceDirectory;
    
    public static GeoDBConfConfiguration getInstance(){
        if( INSTANCE == null ) {
          synchronized( GeoDBConfConfiguration.class ) {
              if( INSTANCE == null ) {
                  INSTANCE = new GeoDBConfConfiguration();
              }
         }
       }
       
        return INSTANCE;
    }
    
    private GeoDBConfConfiguration(){
        // empty
    }
    
    /**
     * Inits this confiuration with the configuration file at configFile
     * @param configFile
     * @throws InvalidConfigurationException the file is invalid
     */
    public void initFromUrl( URL configFile ) throws InvalidConfigurationException {

        XMLFragment xmlFragment = new XMLFragment();
        
        try {
            
	        xmlFragment.load( configFile );
	        
	        Element root = xmlFragment.getRootElement();
	        Element jdbcElement = 
	            (Element) XMLTools.getNode( root, 
	                						"./dgjdbc:JDBCConnection", 
	                						CommonNamespaces.getNamespaceContext() );
	
	        IODocument ioDocument = new IODocument( jdbcElement );
	
	        jdbcConnection = ioDocument.parseJDBCConnection();
	        
	        File dummyFile = new File( xmlFragment.getSystemId().getFile()  );

	        String templDir = 
	            XMLTools.getRequiredNodeAsString( root, 
	                							  "./TemplateDir", 
	                							  CommonNamespaces.getNamespaceContext() );
	        
	        String parentDir = dummyFile.getParent() + File.separator; 
	        templateDirectory = new File( parentDir + templDir );

	        this.dataSourceDirectory = createDatasourceDirectory( root, dummyFile );
	        
        } catch (Exception e) {
            throw new InvalidConfigurationException( "Error initializing GeoDBConf Configuration", 
                									 e );
        }
    }

    /* convenience method to initialize the datasource directory */
    private File createDatasourceDirectory( Element root, File parentDir ) 
    	throws XMLParsingException {
        
        String templDir = 
            XMLTools.getRequiredNodeAsString( root, 
                							  "./DatasourceDirectory", 
                							  CommonNamespaces.getNamespaceContext() );
        
        return new File( parentDir.getParent() + File.separator + templDir   );
        
    }
    
    /**
     * Gets a File object pointing to the directory where the SQL and XML templates
     * are stored. 
     * @return a directory where templates are stored.
     */
    public File getTemplateDir() {
        return this.templateDirectory;
    }

    /**
     * Gets the JDBCConnection object used to create DB tables.
     * @return the JDBCConnection object defined in the configuration file
     */
    public JDBCConnection getJdbcConnection() {
        return this.jdbcConnection;
    }
    
    /**
     * Gets a File object pointing to the directory where the created data stores
     * are stored. 
     * @return a directory where templates are stored.
     */
    public File getDataSourceDirectory() {
        return dataSourceDirectory;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoDBConfConfiguration.java,v $
Revision 1.12  2006/08/24 06:38:30  poth
File header corrected

Revision 1.11  2006/04/06 20:25:29  poth
*** empty log message ***

Revision 1.10  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.9  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.8  2005/12/07 16:05:00  taddei
small refactoring, more javadoc

Revision 1.6  2005/12/05 13:10:10  taddei
include dir4ectorys for datasources

Revision 1.5  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.4  2005/12/01 13:59:37  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.3  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */