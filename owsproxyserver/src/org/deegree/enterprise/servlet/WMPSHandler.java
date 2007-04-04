//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/servlet/WMPSHandler.java,v 1.16 2006/07/21 14:49:01 deshmukh Exp $
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

package org.deegree.enterprise.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.OGCWebServiceResponse;
import org.deegree.ogcwebservices.wmps.WMPService;
import org.deegree.ogcwebservices.wmps.WMPServiceFactory;
import org.deegree.ogcwebservices.wmps.XMLFactory;
import org.deegree.ogcwebservices.wmps.capabilities.WMPSCapabilities;
import org.deegree.ogcwebservices.wmps.capabilities.WMPSCapabilitiesDocument;
import org.deegree.ogcwebservices.wmps.configuration.WMPSConfiguration;
import org.deegree.ogcwebservices.wmps.operation.PrintMapResponseDocument;
import org.deegree.ogcwebservices.wmps.operation.WMPSGetCapabilitiesResult;
import org.deegree.ogcwebservices.wms.InvalidFormatException;

/**
 * 
 * Web servlet client for WMPS.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: deshmukh $
 * 
 * @version 2.0, $Revision: 1.16 $, $Date: 2006/07/21 14:49:01 $
 * 
 * @since 2.0
 */

public class WMPSHandler extends AbstractOWServiceHandler  {

    private static ILogger LOG = LoggerFactory.getLogger( WMPSHandler.class );

    private HttpServletResponse resp = null;
    
    private WMPSConfiguration configuration = null;

    WMPSHandler() {
        LOG.logDebug( "New WMPSHandler instance created: "
            + this.getClass().getName() );
    }

    /**
     * performs the passed OGCWebServiceRequest by accessing service from the pool and passing the
     * request to it
     */
    public void perform( OGCWebServiceRequest request, HttpServletResponse response )
        throws OGCWebServiceException {

        resp = response;
        OGCWebService service;
        try {
            service = WMPServiceFactory.getService();
        } catch (Exception e) {
            throw new OGCWebServiceException( "Error performing the WMPService(s)." );
        }
        configuration = (WMPSConfiguration) ( (WMPService) service ).getCapabilities();
        if ( service == null ) {
            OGCWebServiceException exce = 
                new OGCWebServiceException( "WMPS:WMPS", "could not access a WMPService instance" );
            sendException( response, exce );
            return;
        }
        Object o = service.doService( request );
        handleResponse( o );
    }

    /**
     * 
     * 
     * @param result
     */
    private void handleResponse( Object result ) {
        LOG.entering();

        try {
            OGCWebServiceResponse response = (OGCWebServiceResponse) result;
            if ( response.getException() != null ) {
                // handle the case that an exception occured during the
                // request performance
                OGCWebServiceException exce = response.getException();
                sendException( resp, exce );
            } else {
                if ( response instanceof WMPSGetCapabilitiesResult ) {
                    handleGetCapabilitiesResponse( (WMPSGetCapabilitiesResult) response );
                } else if ( response instanceof PrintMapResponseDocument ) {
                    handlePrintMapResponse( (PrintMapResponseDocument) response );
                }
            }
        } catch (InvalidFormatException ife) {
            LOG.logError( ife.getMessage(), ife );            
            OGCWebServiceException exce = 
                new OGCWebServiceException( "InvalidFormat", ife.getMessage() );
            sendException( resp, exce );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            OGCWebServiceException exce = new OGCWebServiceException( "WMPS:write",e.getMessage() );
            sendException( resp, exce );
        }

        LOG.exiting();
    }

    /**
     * handles the response to a get capabilities request
     * 
     * @param response
     */
    private void handleGetCapabilitiesResponse( WMPSGetCapabilitiesResult response )
        throws Exception {
        LOG.entering();
        resp.setContentType( "application/vnd.ogc.wms_xml" );
        WMPSCapabilities capa = response.getCapabilities();
        WMPSCapabilitiesDocument doc = XMLFactory.export( capa );

        // XMLFragment frag = doc.transform( url.openStream() , XMLFragment.DEFAULT_URL );
        String xml = DOMPrinter.nodeToString( doc.getRootElement(), "" );

        String dtd = NetWorker.url2String( configuration.getDeegreeParams().getDTDLocation() );
        StringBuffer sb = new StringBuffer();
        sb.append( "<!DOCTYPE WMT_PS_Capabilities SYSTEM " );
        sb.append( "'"
            + dtd + "' \n" );
        sb.append( "[\n<!ELEMENT VendorSpecificCapabilities EMPTY>\n]>" );

        xml = StringTools.replace( xml, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "\n" + sb.toString(), false );
        xml = StringTools.replace( xml, "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
            "", false );

        try {
            PrintWriter pw = resp.getWriter();
            pw.print( xml );
            pw.close();
        } catch (Exception e) {
            LOG.logError( "-", e );
        }

        LOG.exiting();
    }

    /**
     * handles the response to a print map request
     * 
     * @param response
     */
    private void handlePrintMapResponse( PrintMapResponseDocument response ) {

        resp.setContentType( "application/vnd.ogc.wms_xml" );

        XMLFragment xml = new XMLFragment( response.getRootElement() );
        try {
            PrintWriter pw = resp.getWriter();
            //pw.append( "id=");
            //pw.append( response.getRootElement().getAttribute( "id" ) );
            xml.prettyPrint( pw );
            pw.close();
        } catch (Exception e) {
            LOG.logError( "-", e );
        }

        LOG.exiting();

    }

   
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMPSHandler.java,v $
Revision 1.16  2006/07/21 14:49:01  deshmukh
Added vendor specific parameters for the wmps

Revision 1.15  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
