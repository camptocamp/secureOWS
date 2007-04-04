//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/CatalogueService.java,v 1.14 2006/10/01 11:15:43 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Meckenheimer Allee 176
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueGetCapabilities;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueOperationsMetadata;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfiguration;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfigurationDocument;
import org.deegree.ogcwebservices.csw.discovery.DescribeRecord;
import org.deegree.ogcwebservices.csw.discovery.Discovery;
import org.deegree.ogcwebservices.csw.discovery.GetDomain;
import org.deegree.ogcwebservices.csw.discovery.GetRecordById;
import org.deegree.ogcwebservices.csw.discovery.GetRecords;
import org.deegree.ogcwebservices.csw.manager.Harvest;
import org.deegree.ogcwebservices.csw.manager.Manager;
import org.deegree.ogcwebservices.csw.manager.Transaction;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wfs.RemoteWFService;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.WFServiceFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;

/**
 * The Catalogue Service class provides the foundation for an OGC 
 * catalogue service. The Catalogue Service class directly includes
 * only the serviceTypeID attribute. In most cases, this attribute
 * will not be directly visible to catalogue clients.
 * <p>
 * The catalog service is an implementation of the OpenGIS 
 * Catalogue Service Specification 2.0.
 * </p>
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.14 $, $Date: 2006/10/01 11:15:43 $
 * 
 * @since 2.0
 * 
 * @see <a href="http://www.opengis.org/specs/">OGC Specification </a>
 */

public class CatalogueService implements OGCWebService {

    private static final ILogger LOG = LoggerFactory.getLogger( CatalogueService.class );
    
    private static final TriggerProvider TP = TriggerProvider.create( CatalogueService.class );
    
    private Discovery discovery;
    private Manager manager;
    private CatalogueConfiguration serviceConfiguration;
    static Map<URL,OGCWebService> wfsMap = null;
    static {
        // simple workaround to cache WFS configurations
        // TODO: use more enhanced pooling/caching        
        if ( wfsMap == null ) {
            wfsMap = new HashMap();
        }
    }

    /**
     * Creates a new <code>CatalogService</code> instance.
     * 
     * @return
     */
    static final CatalogueService create( CatalogueConfiguration config ) throws OGCWebServiceException {
        // get WFS: local or remote
        OGCWebService wfsResource = null;
        try {
            CatalogueConfigurationDocument document = new CatalogueConfigurationDocument();
            document.setSystemId( config.getSystemId() );

            URL wfsCapabilitiesFileURL = 
                document.resolve( config.getDeegreeParams().getWfsResource().getHref().toString() );
            if ( wfsMap.get( wfsCapabilitiesFileURL ) == null ) {
                if ( wfsCapabilitiesFileURL.getProtocol().equals( "http" ) ) {
                    WFSCapabilitiesDocument capaDoc = new WFSCapabilitiesDocument();
                    capaDoc.load( wfsCapabilitiesFileURL );
                    WFSCapabilities capabilities = (WFSCapabilities) capaDoc.parseCapabilities();
                    LOG.logInfo( "Creating remote WFS with capabilities file "
                        + wfsCapabilitiesFileURL );
                    wfsResource = new RemoteWFService( capabilities );
                } else {
                    WFSConfigurationDocument capaDoc = new WFSConfigurationDocument();
                    capaDoc.load( wfsCapabilitiesFileURL );
                    WFSConfiguration conf = capaDoc.getConfiguration();
                    LOG.logInfo( "Creating local WFS with capabilities file "
                        + wfsCapabilitiesFileURL );
                    wfsResource = WFServiceFactory.createInstance( conf );
                }
                wfsMap.put( wfsCapabilitiesFileURL, wfsResource );
            } else {
                wfsResource = wfsMap.get( wfsCapabilitiesFileURL );
            }
        } catch (Exception e) {
            LOG.logError( "Error creating WFS for CSW", e );
            throw new OGCWebServiceException( CatalogueService.class.getName(),
                "Error creating WFS for CSW: " + e.getMessage() );
        }

        // initialize manager and discovery
        return new CatalogueService( config, (WFService) wfsResource );
    }

    /**
     * 
     * @param config
     * @param wfsService
     * @throws OGCWebServiceException
     */
    private CatalogueService( CatalogueConfiguration config, WFService wfsService ) 
                                                        throws OGCWebServiceException {
        this.serviceConfiguration = config;
        this.discovery = new Discovery( wfsService, config );
        CatalogueOperationsMetadata com = 
            (CatalogueOperationsMetadata)config.getOperationsMetadata();
        if ( com.getHarvest() != null || com.getTransaction() != null ) {
            try {
                this.manager = new Manager( wfsService, config );
            } catch (MissingParameterValueException e) {
                LOG.logError( e.getMessage(), e );
                throw new OGCWebServiceException( getClass().getName(), e.getMessage() );
            }
        }
    }

    /**
     * Returns the OGC-capabilities of the service.
     * 
     * @param request
     * @return
     * @todo analyze incoming request! return only requested sections
     */
    public OGCCapabilities getCapabilities() {
        return this.serviceConfiguration;
    }

    /**
     * Returns the service type (CSW).
     * 
     * @return
     */
    public String getServiceTypeId() {
        return this.serviceConfiguration.getServiceIdentification().getServiceType().getCode();
    }

    public String getVersion() {
        return this.serviceConfiguration.getVersion();
    }

    /**
     * Method for event based request processing.
     * 
     * @param request
     *            request object containing the request
     * @return
     * 
     * @todo validation of requested version against accepted versions
     * @todo return type
     */
    public Object doService( OGCWebServiceRequest request ) throws OGCWebServiceException {
        
        request = (OGCWebServiceRequest)TP.doPreTrigger( this, request )[0];
        
        Object response = null;
        
        if ( request instanceof DescribeRecord ) {
            response = this.getDiscovery().describeRecordType( (DescribeRecord) request );
        } else if ( request instanceof GetDomain ) {
            throw new OGCWebServiceException( getClass().getName(), 
                                              "Operation GetDomain is not implement yet" );
            // TODO is not implemented            
            //response = this.getDiscovery().getDomain( (GetDomain) request );
        } else if ( request instanceof GetRecords ) {
            response = this.getDiscovery().query( (GetRecords) request );
        } else if ( request instanceof GetRecordById ) {
            response = this.getDiscovery().query( (GetRecordById) request );
        } else if ( request instanceof Transaction ) {
            response = this.getManager().transaction( (Transaction)request );
        } else if ( request instanceof Harvest ) {
            response = this.getManager().harvestRecords( (Harvest)request );
        } else if ( request instanceof CatalogueGetCapabilities ) {
            LOG.logDebug( "GetCapabilities for version:" + request.getVersion(), request );
            String[] acceptVersions = ( (CatalogueGetCapabilities) request ).getAcceptVersions();
            boolean versionOk = false;
            if ( acceptVersions == null || acceptVersions.length == 0 ) {
                versionOk = true;
            } else {
                for (int i = 0; i < acceptVersions.length; i++) {
                    if ( acceptVersions[i].equals( "2.0.0" ) ) { 
                        versionOk = true;
                        break;
                    }
                }
            }
            if ( versionOk ) {
                response = this.getCapabilities();
            } else {
                throw new InvalidParameterValueException(
                    "Unsupported version requested, only version 2.0.0 is supported." );
            }
        } else {
            throw new OGCWebServiceException( "Invalid request type: '" + 
                                              request.getClass().getName() + "'." );
        }
        
        return TP.doPostTrigger( this, response )[0];
    }

    /**
     * @return Returns the discovery.
     * 
     */
    public Discovery getDiscovery() {
        return discovery;
    }

    /**
     * @return Returns the manager.
     */
    public Manager getManager() throws OGCWebServiceException {
        if ( manager == null ) {
            throw new OGCWebServiceException( getClass().getName(), "CSW Manager class for " +
                    "handling transactional requests is not initialized. Please verfiy, " +
                    "that you have defined Transaction and/or Harvest in the " +
                    "OperationMetadata section of your capabilities/configuration." );
        }
        return manager;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueService.java,v $
Revision 1.14  2006/10/01 11:15:43  poth
trigger points for doService methods defined

Revision 1.13  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
