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
package org.deegree.portal.standard.security.control;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.deegree.enterprise.control.RequestDispatcher;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.IODocument;
import org.deegree.io.JDBCConnection;
import org.deegree.security.drm.SecurityAccessManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/29 19:54:14 $
 *
 * @since 2.0
 */
public class SecurityRequestDispatcher extends RequestDispatcher {
    
    private ILogger LOG = LoggerFactory.getLogger( SecurityRequestDispatcher.class );

    private static String SECURITY_CONFIG = "Security.configFile"; 

    /**
     * This method initializes the servlet.
     *
     * @param cfg the servlet configuration
     * @throws ServletException an exception
     */
    public void init( ServletConfig cfg )
                            throws ServletException {
        super.init( cfg );

        try {
            // config file -> DOM
            String s = getInitParameter( SECURITY_CONFIG );
            File file = new File( s );
            if ( !file.isAbsolute() ) {
                file = new File( getServletContext().getRealPath( s ) );
            }
            URL url = file.toURL();
            Reader reader = new InputStreamReader( url.openStream() );
            Document doc = XMLTools.parse( reader );
            Element element = doc.getDocumentElement();
            reader.close();

            // extract configuration information from DOM
            String readWriteTimeoutString = XMLTools.getStringValue( "readWriteTimeout", null, 
                                                                     element, "600" ); 
            int readWriteTimeout = Integer.parseInt( readWriteTimeoutString );

            String registryClass = XMLTools.getRequiredStringValue( "registryClass", null, element ); 
            Element registryConfig = (Element) XMLTools.getRequiredNode( element, "registryConfig", 
                                                                         null );

            //required: <connection>
            NamespaceContext nsc = new NamespaceContext();
            nsc.addNamespace( "jdbc", new URI( "http://www.deegree.org/jdbc") );
            element = (Element) XMLTools.getRequiredNode( registryConfig, "jdbc:JDBCConnection", nsc ); 
            IODocument xml = new IODocument( element );
            JDBCConnection jdbc = xml.parseJDBCConnection();
            
            Properties properties = new Properties();

            // required: <driver>
            properties.put( "driver", jdbc.getDriver() ); 
            // required: <logon>
            properties.put( "url", jdbc.getURL() ); 
            // required: <user>
            properties.put( "user", jdbc.getUser() ); 
            // required: <password>
            properties.put( "password", jdbc.getPassword() ); 

            if ( !SecurityAccessManager.isInitialized() ) {
                SecurityAccessManager.initialize( registryClass, properties, 
                                                  readWriteTimeout * 1000 );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
            throw new ServletException( Messages.format("portal.standard.security.control.SECMANAGERINIT", 
                                                        e.getMessage() ) ); 
        }

    }
}
/* **************************************************************************
 * Changes to this class. What the people haven been up to:
 * $Log: SecurityRequestDispatcher.java,v $
 * Revision 1.5  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.4  2006/06/19 18:47:22  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/05/22 19:06:39  poth
 * logging added / switched to IODocument for reading database connection info
 *
 * Revision 1.2  2006/05/22 09:30:16  poth
 * code formated / not required variables removed
 *
 *
 ************************************************************************** */
