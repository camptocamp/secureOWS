// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/wcs/WCSServiceFactory.java,v
// 1.4 2004/06/18 15:50:30 tf Exp $
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
package org.deegree.ogcwebservices.wms;

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.wcs.configuration.InvalidConfigurationException;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationDocument;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationDocument_1_3_0;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType;

/**
 * 
 *
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.18 $, $Date: 2006/11/29 13:00:36 $
 *
 * @since 2.0
 * 
 */
public final class WMServiceFactory {

    private static WMSConfigurationType CONFIG;

    private static final ILogger LOG = LoggerFactory.getLogger( WMServiceFactory.class );

    private WMServiceFactory() {
        CONFIG = null;
    }

    /**
     * Creates a new WMS service instance configured with the given configuration.
     * 
     * @param config
     * @return a new service instance
     */
    public static WMService getWMSInstance( WMSConfigurationType config ) {
        return new WMService( config );
    }

    /**
     * Sets the default configuration by value.
     * 
     * @param wmsConfiguration
     */
    public static void setConfiguration( WMSConfigurationType wmsConfiguration ) {
        CONFIG = wmsConfiguration;
        // if service instance are already created
        // destroy all instances
        // create new service instances and put in pool

        LOG.logInfo( CONFIG.getServiceIdentification().getTitle() + " (" + CONFIG.getVersion()
                     + ") service pool initialized." );
    }

    /**
     * Sets the default configuration by URL.
     * 
     * @param serviceConfigurationUrl
     * @throws InvalidConfigurationException
     */
    public static void setConfiguration( URL serviceConfigurationUrl )
                            throws InvalidConfigurationException {

        try {
            WMSConfigurationDocument doc = new WMSConfigurationDocument();
            WMSConfigurationDocument_1_3_0 doc130 = new WMSConfigurationDocument_1_3_0();

            // changes start here
            int dc = 50;
            boolean configured = false;
            while ( !configured ) {
                try {
                    doc.load( serviceConfigurationUrl );

                    if ( "1.3.0".equals( doc.getRootElement().getAttribute( "version" ) ) ) {
                        LOG.logInfo( Messages.getMessage( "WMS_VERSION130" ) );
                        doc130.load( serviceConfigurationUrl );
                        doc = null;
                    } else {
                        LOG.logInfo( Messages.getMessage( "WMS_VERSIONDEFAULT" ) );
                        doc130 = null;
                    }

                    configured = true;
                } catch ( IOException ioe ) {
                    if ( serviceConfigurationUrl.getProtocol().startsWith( "http" ) && dc > 0 ) {
                        LOG.logWarning( "No successful connection to the WMS-Configuration-URL, "
                                        + "trying again in 10 seconds. Will try " + dc
                                        + " more times to connect." );
                        Thread.sleep( 10000 );
                        dc--;
                    } else {
                        throw ( ioe );
                    }
                }
            }
            // changes end here

            WMSConfigurationType conf;

            if ( doc != null ) {
                conf = doc.parseConfiguration();
            } else {
                conf = doc130.parseConfiguration();
            }

            WMServiceFactory.setConfiguration( conf );

        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new InvalidConfigurationException( "WMServiceFactory", e.getMessage() );
        }

    }

    /**
     * Returns a new WMS service instance configured with a previously set default
     * configuration.
     * 
     * @return a new service instance
     */
    public static WMService getService() {
        return WMServiceFactory.getWMSInstance( CONFIG );
    }

}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WMServiceFactory.java,v $
 Revision 1.18  2006/11/29 13:00:36  schmitz
 Cleaned up WMS messages.

 Revision 1.17  2006/11/24 09:33:12  schmitz
 Fixed a bug concerning layer specific scale hints.
 Using the central i18n mechanism.
 Changed the localwfs mechanism to just use one WFS and not recreate them.

 Revision 1.16  2006/10/27 12:23:24  schmitz
 Formatting changes.

 Revision 1.15  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.14  2006/07/28 08:01:27  schmitz
 Updated the WMS for 1.1.1 compliance.
 Fixed some documentation.

 Revision 1.13  2006/07/12 14:46:17  poth
 comment footer added

 ********************************************************************** */
