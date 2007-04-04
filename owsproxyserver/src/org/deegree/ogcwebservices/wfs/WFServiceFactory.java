//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/WFServiceFactory.java,v 1.22 2006/11/07 11:09:36 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs;

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;
import org.xml.sax.SAXException;

/**
 * Factory class for creating instances of {@link WFService}.
 * 
 * TODO manage several instances of different WFServices in a pool
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.22 $, $Date: 2006/11/07 11:09:36 $
 * 
 * @see WFService
 */
public class WFServiceFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( WFServiceFactory.class );

    private static WFSConfiguration CONFIG;

    private WFServiceFactory() {
        // prevent instantiation
    }

    /**
     * 
     * @param config
     * @return
     */
    public static WFService createInstance()
                            throws OGCWebServiceException {
        if ( CONFIG == null ) {
            throw new OGCWebServiceException( WFServiceFactory.class.getName(),
                                              "configuration has not been initialized" );
        }
        return new WFService( CONFIG );
    }

    /**
     * 
     * @param config
     * @return
     * @throws OGCWebServiceException 
     */
    public static WFService createInstance( WFSConfiguration wfsConfiguration )
                            throws OGCWebServiceException {
        LOG.logInfo( "Creating WFService instance." );
        return new WFService( wfsConfiguration );
    }

    /**
     * Sets the <code>WFSConfiguration</code>. Afterwards, all <code>WFService</code> instances
     * returned by <code>createInstance()</code> will use this configuration.
     * 
     * @param config
     * @throws InvalidConfigurationException 
     */
    public synchronized static void setConfiguration( WFSConfiguration config )
                            throws InvalidConfigurationException {
        validateConfiguration(config);
        CONFIG = config;

        // TODO: if service instances have already been created
        // - destroy all instances
        // - create new service instances and put them in the pool
    }

    /**
     * 
     * @param serviceConfigurationURL
     * @throws InvalidConfigurationException
     * @throws IOException
     */
    public synchronized static void setConfiguration( URL serviceConfigurationURL )
                            throws InvalidConfigurationException, IOException {
        LOG.entering();
        try {
            WFSConfigurationDocument cd = new WFSConfigurationDocument();
            cd.load( serviceConfigurationURL );
            WFServiceFactory.setConfiguration( cd.getConfiguration() );
        } catch ( InvalidConfigurationException e ) {
            throw new InvalidConfigurationException( "WFSServiceFactory", e.getMessage() );
        } catch ( SAXException e ) {
            throw new InvalidConfigurationException( "WFSServiceFactory", e.getMessage() );
        }
        LOG.exiting();
    }

    public static WFSConfiguration getConfiguration() {
        return CONFIG;
    }

    private static void validateConfiguration( WFSConfiguration config )
                            throws InvalidConfigurationException {
        String[] versions = config.getServiceIdentification().getServiceTypeVersions();
        for ( String version : versions ) {
            if ( !WFService.VERSION.equals( version ) ) {
                String msg = Messages.getMessage( "WFS_CONF_UNSUPPORTED_VERSION", version,
                                                  WFService.VERSION );
                throw new InvalidConfigurationException( msg );
            }
        }
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: WFServiceFactory.java,v $
 * Revision 1.22  2006/11/07 11:09:36  mschneider
 * Added exceptions in case anything other than the 1.1.0 format is requested.
 *
 * Revision 1.21  2006/09/05 17:44:01  mschneider
 * Fixed javadoc version information.
 *
 * Revision 1.20  2006/04/25 06:50:27  poth
 * *** empty log message ***
 *
 * Revision 1.19  2006/04/06 20:25:21  poth
 * *** empty log message ***
 *
 * Revision 1.18  2006/03/30 21:20:23  poth
 * *** empty log message ***
 *
 * Revision 1.17  2005/12/22 20:12:32  poth
 * no message
 *
 * Revision 1.16  2005/12/13 09:54:36  taddei
 * fixed typo
 *
 * Revision 1.15  2005/11/16 13:44:59  mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.13.2.1  2005/11/08 16:37:47  mschneider
 * More refactoring to make it compile again.
 * 
 * Revision 1.13 2005/08/27 10:09:44 poth
 * no message
 * 
 * Revision 1.12 2005/08/26 21:11:29 poth
 * no message
 *
 * Revision 1.10 2005/08/24 16:09:53 mschneider
 * Renamed GenericName to QualifiedName.
 * 
 * Revision 1.9 2005/08/23 13:41:47 mschneider
 * *** empty log message ***
 * 
 * Revision 1.8 2005/08/10 15:00:30 mschneider
 * Fixed behaviour of createInstance() methods.
 *
 * Revision 1.7 2005/07/19 15:07:00 mschneider
 * Moved handling of default values to WFSConfiguration.
 * 
 * Revision 1.6 2005/07/14 15:34:20 mschneider
 * Refactoring, error handling and cleanup.
 * 
 * Revision 1.3 2005/04/25 15:23:33 poth no message
 * 
 * Revision 1.2 2005/04/25 14:04:38 poth no message
 * 
 * Revision 1.1 2005/04/25 06:47:45 poth no message
 * 
 ************************************************************************************************* */