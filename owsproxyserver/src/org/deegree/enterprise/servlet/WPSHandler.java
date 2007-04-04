/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wps.WPService;
import org.deegree.ogcwebservices.wps.WPServiceFactory;
import org.deegree.ogcwebservices.wps.XMLFactory;
import org.deegree.ogcwebservices.wps.capabilities.WPSCapabilities;
import org.deegree.ogcwebservices.wps.capabilities.WPSCapabilitiesDocument;
import org.deegree.ogcwebservices.wps.configuration.WPSConfiguration;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescriptions;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescriptionsDocument;
import org.deegree.ogcwebservices.wps.execute.ComplexValue;
import org.deegree.ogcwebservices.wps.execute.ExecuteResponse;
import org.deegree.ogcwebservices.wps.execute.ExecuteResponseDocument;

/**
 * WPSHandler.java
 * 
 * Created on 08.03.2006. 17:01:31h
 * 
 * @author <a href="mailto:kiehle@giub.uni-bonn.de">Christian Kiehle</a>
 * @author <a href="mailto:che@wupperverband.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */
public class WPSHandler extends AbstractOWServiceHandler implements ServiceDispatcher {

	private static final ILogger LOG = LoggerFactory.getLogger( WPSHandler.class );

	/**
	 * 
	 */
	public void perform( OGCWebServiceRequest request, HttpServletResponse httpServletResponse )
			throws ServiceException, OGCWebServiceException {

		WPService service = WPServiceFactory.getInstance();
		@SuppressWarnings ( "unused")
		WPSConfiguration config = ( WPSConfiguration ) service.getCapabilities();
		Object response = service.doService( request );
		if ( response instanceof WPSCapabilities ) {
			sendGetCapabilitiesResponse( httpServletResponse, ( WPSCapabilities ) response );
		} else if ( response instanceof ProcessDescriptions ) {
			sendDescribeProcessResponse( httpServletResponse, ( ProcessDescriptions ) response );
		} else if ( response instanceof ExecuteResponse ) {
			sendExecuteResponse( httpServletResponse, ( ExecuteResponse ) response );
		}

	}

	/**
	 * Sends the response to a GetCapabilities request to the client.
	 * 
	 * @param httpResponse
	 * @param capabilities
	 * @throws OGCWebServiceException
	 *             if an exception occurs which can be propagated to the client
	 */
	private void sendGetCapabilitiesResponse( HttpServletResponse httpResponse,
			WPSCapabilities capabilities ) {
		try {
            httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
			WPSCapabilitiesDocument document = XMLFactory.export( capabilities );
			document.write( httpResponse.getOutputStream() );
		} catch ( IOException e ) {
			LOG.logError( "Error sending GetCapabilities response.", e );
		} 
	}

	/**
	 * Sends the response to a DescribeProcess request to the client.
	 * 
	 * @param httpResponse
	 * @param capabilities
	 * @throws OGCWebServiceException
	 *             if an exception occurs which can be propagated to the client
	 */
	private void sendDescribeProcessResponse( HttpServletResponse httpResponse,
			ProcessDescriptions processDescriptions ) {
		try {
            httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
			ProcessDescriptionsDocument document = XMLFactory.export( processDescriptions );
			document.write( httpResponse.getOutputStream() );
		} catch ( IOException e ) {
			LOG.logError( "Error sending DescribeProcess response.", e );
		}
	}

	/**
	 * Sends the response to an Execute request to the client.
	 * 
	 * @param httpServletResponse
	 * @param request
	 * @throws OGCWebServiceException
	 *             if an exception occurs which can be propagated to the client
	 */
	private void sendExecuteResponse( HttpServletResponse httpResponse,
			ExecuteResponse executeResponse ) throws OGCWebServiceException {

		/*
		 * @see OGC 05-007r4 Subclauses 10.3.1 and 10.3.2
		 * @see OGC 05-007r4 Tables 43, 44
		 * @see OGC 05-007r4 Table 27: If the �store� parameter is �false�,
		 *      process execution was successful, there is only one output, and
		 *      that output has a ComplexValue, then this ComplexValue shall be
		 *      returned to the client outside of any ExecuteResponse document.
		 */
		String processSucceeded = executeResponse.getStatus().getProcessSucceeded();

		if ( null != processSucceeded && executeResponse.isDirectResponse() ) {

			ComplexValue complexValue = executeResponse.getProcessOutputs().getOutputs().get( 0 )
					.getComplexValue();

			if ( null != complexValue ) {
				sendDirectResponse( httpResponse, complexValue );
			}

		} else {
			try {
                httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
				ExecuteResponseDocument document = XMLFactory.export( executeResponse );
				document.write( httpResponse.getOutputStream() );
			} catch ( IOException e ) {
				LOG.logError( "error sending execute response.", e );
			}
		}
	}

	/**
	 * Writes the passed <code>ComplexValue</code> to the
	 * <code>HTTPServletResponse</code>
	 * 
	 * @param httpResponse
	 * @param complexValue
	 */
	private static void sendDirectResponse( HttpServletResponse httpResponse,
			ComplexValue complexValue ) throws OGCWebServiceException {

		Object content = complexValue.getContent();

		if ( content instanceof FeatureCollection ) {

			LOG.logInfo( "content is instance of featurecollection" );

			FeatureCollection fc = ( FeatureCollection ) content;

			GMLFeatureAdapter gmlFeatureAdapter = new GMLFeatureAdapter();

			try {
				gmlFeatureAdapter.export( fc, httpResponse.getOutputStream() );
			} catch ( Exception e ) {
				String msg = "Error sending direct execute response.";
				LOG.logError( msg, e );
				throw new OGCWebServiceException( "", msg, ExceptionCode.NOAPPLICABLECODE );
			}
		} else {
			// TODO implement direct output methods for complexvalue types other
			// than
			// featurecollection
		}
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSHandler.java,v $
Revision 1.10  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.9  2006/08/24 06:39:04  poth
File header corrected

Revision 1.8  2006/07/23 10:05:54  poth
setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
