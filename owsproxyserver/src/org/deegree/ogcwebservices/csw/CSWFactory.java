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

package org.deegree.ogcwebservices.csw;

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfiguration;
import org.xml.sax.SAXException;

/**
 * Service factory to create a catalog service. *  * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> *  * @author last edited by: $Author: poth $ *  * @version 2.0, $Revision: 1.14 $, $Date: 2006/07/12 14:46:18 $ *  * @since 2.0 *  * @todo all instances of the service should be pooled. * @todo Usage of {@link org.deegree.ogcwebservices.OGCServiceFaxctoy OGC service factory}
 */

public final class CSWFactory {

   
    private static CatalogueConfiguration CONFIG;

    private static final ILogger LOG = LoggerFactory.getLogger(CSWFactory.class);

    //  @todo the factory has a pool of service instances

    /**
     * Hidden constructur
     *  
     */
    private CSWFactory() {
        // nothing to do
    }

    /**
     * must be synchronized
     * 
     * @param config
     *  
     */
    public static void setConfiguration( CatalogueConfiguration catalogConfiguration) {
        CONFIG = catalogConfiguration;
        
        // if service instance are already created
        // destroy all instances
        // create new service instances and put in pool

        LOG.logInfo(CONFIG.getServiceIdentification().getServiceType().getCode()
                + " (" + CONFIG.getVersion() + ") service pool initialized.");

    }

    public static void setConfiguration(URL serviceConfigurationUrl)
            throws InvalidConfigurationException {
        try {
            CSWFactory.setConfiguration(CatalogueConfiguration
                    .createConfiguration( serviceConfigurationUrl ) );
        } catch (IOException e) {
            LOG.logError(e.getMessage(), e);
            throw new InvalidConfigurationException("CSWFactory", e.getMessage());
        } catch (SAXException e) {
            LOG.logError(e.getMessage(), e);
            throw new InvalidConfigurationException("CSWFactory", e.getMessage());
        }

    }

    /**
     * 
     * @return
     * 
     * @TODO
     */
    public static CatalogueService getService() throws OGCWebServiceException {
        if (CONFIG == null) {
                throw new OGCWebServiceException( CSWFactory.class.getName(),
                                                  "CSW has no configuration" );
        }
        // get an instance of the service from the pool
        return CatalogueService.create( CONFIG ); // @TODO get instance from pool
    }

    public static CatalogueService getUncachedService(CatalogueConfiguration config) throws OGCWebServiceException {
        return CatalogueService.create( config );
    }

    public static void reset() {
        // stop all service instances
        // clear pool
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CSWFactory.java,v $
Revision 1.14  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
