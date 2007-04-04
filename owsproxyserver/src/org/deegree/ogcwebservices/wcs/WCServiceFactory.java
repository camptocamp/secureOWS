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
package org.deegree.ogcwebservices.wcs;

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wcs.configuration.InvalidConfigurationException;
import org.deegree.ogcwebservices.wcs.configuration.WCSConfiguration;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.8 $, $Date: 2006/07/12 14:46:19 $ *  * @since 2.0 *  * @todo Usage of {@link org.deegree.ogcwebservices.OGCServiceFaxctoy OGC service factory}
 */

public final class WCServiceFactory {

    private static WCSConfiguration CONFIG;
    private static final ILogger LOG = LoggerFactory.getLogger(WCServiceFactory.class);

    private WCServiceFactory(){};
    
    /**
     * 
     * @param config
     * @return
     */
    public static WCService getInstance(WCSConfiguration config) {
        return new WCService(config);
    }
    
    /**
     * 
     * @param wcsConfiguration
     */
    public static void setConfiguration(WCSConfiguration wcsConfiguration) {
        CONFIG = wcsConfiguration;
        // if service instance are already created
        // destroy all instances
        // create new service instances and put in pool        
        LOG.logInfo( StringTools.concat( 200, CONFIG.getService().getName(), " (",
                                         CONFIG.getVersion(), ") service pool initialized." ) );
    }

    /**
     * 
     * @param serviceConfigurationUrl
     * @throws InvalidConfigurationException
     * @throws IOException
     */
    public static void setConfiguration(URL serviceConfigurationUrl)
            throws InvalidConfigurationException {
        try {
            WCServiceFactory.setConfiguration(WCSConfiguration.create(serviceConfigurationUrl));
        } catch (InvalidCapabilitiesException e) {
            LOG.logError(e.getMessage(), e);
            throw new InvalidConfigurationException("WCSServiceFactory", e
                    .getMessage());
        } catch (IOException e) {
            LOG.logError(e.getMessage(), e);
            throw new InvalidConfigurationException("WCSServiceFactory", e
                    .getMessage());
        } catch (SAXException e) {
            LOG.logError(e.getMessage(), e);
            throw new InvalidConfigurationException("WCSServiceFactory", e
                    .getMessage());
        }

    }

    /**
     * 
     * @return
     * @throws OGCWebServiceException
     * @todo 
     */
    public static WCService getService() {
        return WCServiceFactory.getInstance( CONFIG );
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WCServiceFactory.java,v $
Revision 1.8  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
