//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/servlet/WASSHandler.java,v 1.11 2006/10/17 20:31:18 poth Exp $
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

package org.deegree.enterprise.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wass.common.WASServiceFactory;
import org.deegree.ogcwebservices.wass.common.XMLFactory;
import org.deegree.ogcwebservices.wass.was.WAService;
import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilities;
import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilitiesDocument;
import org.deegree.ogcwebservices.wass.was.operation.DescribeUserResponse;
import org.deegree.ogcwebservices.wass.wss.WSService;
import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilities;
import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilitiesDocument;
import org.deegree.ogcwebservices.wass.wss.operation.DoServiceResponse;

/**
 * This is the servlet handler class for the WASS services, ie, the Web
 * Authentication Service and the Web Security Service. Attention: since much of
 * the WAS/WSS behaviour is specified to be the same, much of the code of this
 * class is shared, see for example the handleResult method.
 * 
 * @see #handleResult(Object)
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/10/17 20:31:18 $
 * 
 * @since 2.0
 */

public class WASSHandler extends AbstractOWServiceHandler implements ServiceDispatcher {

    private static final ILogger LOG = LoggerFactory.getLogger( WASSHandler.class );

    private HttpServletResponse response = null;

    /**
     * Method to handle the various output objects.
     * 
     * @param result
     * @throws IOException
     */
    private void handleResult( Object result )
                            throws IOException {
        // if result is null, was possibly a CloseSession request, so return
        // nothing as specified
        if ( result == null ){
            response.setContentType( "text/plain; charset=" + CharsetUtils.getSystemCharset() );            
            response.getWriter().println();
        }

        if ( result instanceof OGCWebServiceException ) {
            sendException( response, (OGCWebServiceException) result );
        } else if ( result instanceof Exception ) {
            sendException( response, (Exception) result );
        } else if ( result instanceof String ) {
            // just write the SessionID result from GetSession request
            response.setContentType( "text/plain; charset=" + CharsetUtils.getSystemCharset() );
            response.getWriter().print( result );
        } else if ( result instanceof WASCapabilities ) {
            sendCapabilities( (WASCapabilities) result );
        } else if ( result instanceof WSSCapabilities ) {
            sendCapabilities( (WSSCapabilities) result );
        } else if ( result instanceof DescribeUserResponse ) {
            sendDescribeUserResponse( (DescribeUserResponse) result );
        } else if ( result instanceof DoServiceResponse ) {
            sendOtherServiceResponse( (DoServiceResponse) result );
        }
    }

    /**
     * Sends the XML document contained within the given parameter
     * 
     * @param result
     * @throws IOException 
     */
    private void sendDescribeUserResponse( DescribeUserResponse result ) throws IOException {
        response.setContentType( "text/xml;  charset=" + CharsetUtils.getSystemCharset() );
        result.write( response.getOutputStream() );
    }

    /**
     * Method to send the result of another service.
     * 
     * @param result
     * @throws IOException 
     */
    private void sendOtherServiceResponse( DoServiceResponse result )
                            throws IOException {
        Header[] headers = result.getHeaders();
        // footers will be ignored for now
        //        Header[] footers = result.getFooters();
        InputStream in = result.getResponseBody();
        for ( Header h : headers ) {
            // maybe we have to filter some headers here TODO
            LOG.logDebug( h.toExternalForm() );
            response.setHeader( h.getName(), h.getValue() );
        }
//        BufferedReader bin = new BufferedReader( new InputStreamReader( in ) );
//        PrintWriter out = response.getWriter();
        BufferedInputStream bin = new BufferedInputStream( in );
        OutputStream out = response.getOutputStream();
        byte[] buf = new byte[4096];
        int numberRead = 0;
        while ( (numberRead = bin.read( buf )) != -1 ){
            out.write( buf, 0, numberRead );
        }
        bin.close();
        out.close();
    }

    /**
     * Sends the given capabilities.
     * 
     * @param capabilities
     */
    private void sendCapabilities( WSSCapabilities capabilities ) {
        try {
            response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );            
            WSSCapabilitiesDocument document = XMLFactory.export( capabilities );
            document.write( response.getOutputStream() );
        } catch ( IOException e ) {
            LOG.logError( "Error sending GetCapabilities response.", e );
        }
    }

    /**
     * Sends the given capabilities.
     * 
     * @param capabilities
     */
    private void sendCapabilities( WASCapabilities capabilities ) {
        try {
            response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            WASCapabilitiesDocument document = XMLFactory.export( capabilities );
            document.write( response.getOutputStream() );
        } catch ( IOException e ) {
            LOG.logError( "Error sending GetCapabilities response.", e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.enterprise.servlet.ServiceDispatcher#perform(org.deegree.ogcwebservices.OGCWebServiceRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void perform( OGCWebServiceRequest request, HttpServletResponse response )
                            throws ServiceException, OGCWebServiceException {
        Object result = null;
        this.response = response;
        if ( "WAS".equals( request.getServiceName() ) ) {
            WAService service = WASServiceFactory.getUncachedWAService(); // get from factory
            result = service.doService( request );
        } else if ( "WSS".equals( request.getServiceName() ) ) {
            WSService service = WASServiceFactory.getUncachedWSService(); // get from factory
            result = service.doService( request );
        }

        try {
            handleResult( result );
        } catch ( IOException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new OGCWebServiceException( "Error while handling request: \n"
                                              + e.getLocalizedMessage() );
        }
    }

}

/* ******************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WASSHandler.java,v $
 * Revision 1.11  2006/10/17 20:31:18  poth
 * *** empty log message ***
 *
 * Revision 1.10  2006/08/29 11:41:45  schmitz
 * Removed the newline when outputting the sessionID.
 *
 * Revision 1.9  2006/08/11 08:58:50  schmitz
 * WAS implements the DescribeUser operation.
 *
 * Revision 1.8  2006/07/23 10:05:54  poth
 * setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)
 *
 * Revision 1.7  2006/06/26 15:04:06  bezema
 * Finished the wass
 *
 * Revision 1.6  2006/06/22 06:52:46  poth
 * footer corrected
 *
 * 
 ***************************************************************************** */
