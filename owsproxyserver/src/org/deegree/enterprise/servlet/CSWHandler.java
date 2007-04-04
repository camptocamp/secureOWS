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
package org.deegree.enterprise.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.CSWFactory;
import org.deegree.ogcwebservices.csw.CatalogueService;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueCapabilities;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueGetCapabilities;
import org.deegree.ogcwebservices.csw.discovery.DescribeRecordResult;
import org.deegree.ogcwebservices.csw.discovery.GetRecordByIdResult;
import org.deegree.ogcwebservices.csw.discovery.GetRecordsResult;
import org.deegree.ogcwebservices.csw.manager.HarvestResult;
import org.deegree.ogcwebservices.csw.manager.TransactionResult;

/**
 * Web servlet client for CSW.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.21 $, $Date: 2006/10/17 20:31:18 $
 * 
 * @see <a href="http://www.dofactory.com/patterns/PatternChain.aspx">Chain of Responsibility Design
 *      Pattern </a>
 * 
 * @since 2.0
 */

public class CSWHandler extends AbstractOWServiceHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( CSWHandler.class );

    /**
     * @see org.deegree.enterprise.servlet.ServiceDispatcher#perform(org.deegree.services.AbstractOGCWebServiceRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void perform( OGCWebServiceRequest request, HttpServletResponse httpResponse )
                                                                     throws ServiceException,
                                                                     OGCWebServiceException {

        LOG.logDebug( "Performing request: " + request.toString() );

        CatalogueService service = CSWFactory.getService();
        Object response = service.doService( request );
        try {
            if ( response instanceof OGCWebServiceException ) {
                sendException( httpResponse, (OGCWebServiceException) response );
            } else if ( response instanceof Exception ) {
                sendException( httpResponse, (Exception) response );
            } else if ( response instanceof CatalogueCapabilities ) {
                sendCapabilities( httpResponse, (CatalogueGetCapabilities) request,
                                  (CatalogueCapabilities) response );
            } else if ( response instanceof GetRecordsResult ) {
                sendGetRecord( httpResponse, (GetRecordsResult) response );
            } else if ( response instanceof GetRecordByIdResult ) {
                sendGetRecordById( httpResponse, (GetRecordByIdResult) response );
            } else if ( response instanceof DescribeRecordResult ) {
                sendDescribeRecord( httpResponse, (DescribeRecordResult) response );
            } else if ( response instanceof TransactionResult ) {
                sendTransactionResult( httpResponse, (TransactionResult) response );
            } else if ( response instanceof HarvestResult ) {
                sendHarvestResult( httpResponse, (HarvestResult) response );
            } else {
                OGCWebServiceException e = 
                    new OGCWebServiceException( this.getClass().getName(),
                                                "Unknown response class: "
                                                + ( response == null ? "null response object"
                                                : response.getClass().getName() ) + "." );
                sendException( httpResponse, e );
            }
        } catch ( IOException ex ) {
            throw new ServiceException( "Error while sending response: " + ex.getMessage(), ex );
        }

    }

    /**
     * Sends the passed <tt>HarvestResult</tt> to the http client.
     * 
     * @param httpResponse
     *            http connection to the client
     * @param result
     *            object to send
     * @throws IOException 
     * @throws XMLParsingException
     */
    private void sendHarvestResult( HttpServletResponse httpResponse, HarvestResult result )
                                                                        throws IOException {
        XMLFragment doc = null;
        try {
            doc = org.deegree.ogcwebservices.csw.manager.XMLFactory.export( result );
        } catch ( XMLParsingException e ) {
            throw new IOException( "could not export TransactionResult as XML: " + e.getMessage() );
        }
        httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
        OutputStream os = httpResponse.getOutputStream();
        doc.write( os );
        os.close();        
    }

    /**
     * Sends the passed <tt>TransactionResult</tt> to the http client.
     * 
     * @param httpResponse
     *            http connection to the client
     * @param result
     *            object to send
     * @throws XMLParsingException
     */
    private void sendTransactionResult( HttpServletResponse httpResponse, TransactionResult result )
                                                                            throws IOException {
        XMLFragment doc = null;
        try {
            doc = org.deegree.ogcwebservices.csw.manager.XMLFactory.export( result );
        } catch ( XMLParsingException e ) {
            throw new IOException( "could not export TransactionResult as XML: " + e.getMessage() );
        }
        httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
        OutputStream os = httpResponse.getOutputStream();
        doc.write( os );
        os.close();
    }

    /**
     * Sends the passed <tt>CatalogCapabilities</tt> to the http client.
     * 
     * @param response
     *            http connection to the client
     * @param capabilities
     *            object to send
     */
    private void sendCapabilities( HttpServletResponse response,
                                  CatalogueGetCapabilities getCapabilities,
                                  CatalogueCapabilities capabilities ) throws IOException {

        boolean xmlOk = false;
        String[] formats = getCapabilities.getAcceptFormats();
        if ( formats == null || formats.length == 0 ) {
            xmlOk = true;
        } else {
            for ( int i = 0; i < formats.length; i++ ) {
                if ( formats[i].equals( "text/xml" ) ) {
                    xmlOk = true;
                    break;
                }
            }
        }
        if ( !xmlOk ) {
            ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
            InvalidParameterValueException e = new InvalidParameterValueException(
                                                                                   this.getClass().getName(),
                                                                                   "OutputFormat must be 'text/xml'.",
                                                                                   code );
            sendException( response, e );
        } else {

            XMLFragment doc = org.deegree.ogcwebservices.csw.XMLFactory.export(
                                                                                capabilities,
                                                                                getCapabilities.getSections() );
            response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            OutputStream os = response.getOutputStream();
            doc.write( os );
            os.close();
        }
    }

    /**
     * 
     * @param response
     * @param getRecordResponse
     * @throws IOException
     */
    private void sendGetRecord( HttpServletResponse response, GetRecordsResult getRecordResponse )
                                                                                  throws IOException {
        XMLFragment doc = 
            org.deegree.ogcwebservices.csw.discovery.XMLFactory.export( getRecordResponse );
        response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
        OutputStream os = response.getOutputStream();
        doc.write( os );
        os.close();
    }

    /**
     * 
     * @param response
     * @param getRecordByResponse
     * @throws IOException
     */
    private void sendGetRecordById( HttpServletResponse response,
                                   GetRecordByIdResult getRecordByIdResponse ) throws IOException {
        XMLFragment doc = org.deegree.ogcwebservices.csw.discovery.XMLFactory.export( getRecordByIdResponse );
        response.setContentType( "text/xml" );
        OutputStream os = response.getOutputStream();
        doc.write( os );
        os.close();
    }

    /**
     * 
     * @param response
     * @param describeRecordRequest
     * @param describeRecordResponse
     * @throws IOException
     */
    private void sendDescribeRecord( HttpServletResponse response,
                                     DescribeRecordResult describeRecordResponse )
                                                                           throws IOException {
        XMLFragment doc = 
            org.deegree.ogcwebservices.csw.discovery.XMLFactory.export( describeRecordResponse );
        response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
        OutputStream os = response.getOutputStream();
        doc.write( os );
        os.close();
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CSWHandler.java,v $
Revision 1.21  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.20  2006/07/23 10:05:54  poth
setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

Revision 1.19  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
