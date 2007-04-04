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

import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.deegree.datatypes.parameter.GeneralParameterValueIm;
import org.deegree.datatypes.parameter.OperationParameterIm;
import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.model.coverage.grid.FormatIm;
import org.deegree.model.coverage.grid.GridCoverageExchangeIm;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wcs.WCService;
import org.deegree.ogcwebservices.wcs.WCServiceFactory;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescription;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSGetCapabilities;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.ResultCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.SpatialSubset;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageExchange;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.OperationParameter;

/**
 * Dispatcher for WCService.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.35 $, $Date: 2006/10/17 20:31:18 $
 * 
 * @since 2.0
 */
class WCSHandler extends AbstractOWServiceHandler {

    private static ILogger LOG = LoggerFactory.getLogger( WCSHandler.class );

    /**
     * 
     */
    WCSHandler() {
        LOG.logDebug( "New WCSHandler instance created: " + this.getClass().getName() );
    }

    /**
     * @see org.deegree.enterprise.servlet.ServiceDispatcher#perform(org.deegree.services.AbstractOGCWebServiceRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void perform( OGCWebServiceRequest request, HttpServletResponse response )
                            throws ServiceException, OGCWebServiceException {

        Object serviceRes = this.getService().doService( request );

        if ( serviceRes instanceof OGCWebServiceException ) {
            sendException( response, (OGCWebServiceException) serviceRes );
        } else if ( response instanceof Exception ) {
            sendException( response, (Exception) serviceRes );
        } else if ( serviceRes instanceof ResultCoverage ) {
            SpatialSubset spsu = ( (GetCoverage) request ).getDomainSubset().getSpatialSubset();
            Envelope size = (Envelope) spsu.getGrid();
            sendCoverage( response, (ResultCoverage) serviceRes, size );
        } else if ( serviceRes instanceof WCSCapabilities ) {
            sendCapabilities( response, (WCSGetCapabilities) request, (WCSCapabilities) serviceRes );
        } else if ( serviceRes instanceof CoverageDescription ) {
            sendCoverageDescription( response, (CoverageDescription) serviceRes );
        } else {
            OGCWebServiceException e = new OGCWebServiceException(
                                                                   this.getClass().getName(),
                                                                   "unknown response class: "
                                                                                           + serviceRes.getClass().getName() );
            sendException( response, e );
        }
    }

    /**
     * sends the passed <tt>WCSCapabilities</tt> to the calling client
     * 
     * @param response
     *            <tt>HttpServletResponse</tt> for opening stream to the client
     * @param serviceRes
     *            object to send
     */
    private void sendCapabilities( HttpServletResponse response, WCSGetCapabilities owsr,
                                  WCSCapabilities serviceRes ) {
        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<?xml version=\"1.0\" encoding='" + CharsetUtils.getSystemCharset() + "'?>" );
        sb.append( "<xsl:stylesheet version=\"1.0\" " );
        sb.append( "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" " );
        sb.append( "xmlns:wcs=\"http://www.opengis.net/wcs\" " );
        sb.append( "xmlns:deegree=\"http://www.deegree.org/wcs\">" );
        sb.append( "<xsl:template match=\"wcs:WCS_Capabilities\">"
                   + "<xsl:copy-of select=\"XXX\"/>" );
        sb.append( "</xsl:template>" + "</xsl:stylesheet>" );
        String xslt = sb.toString();
        try {
            XMLFragment doc = org.deegree.ogcwebservices.wcs.XMLFactory.export( serviceRes );
            String[] sections = owsr.getSections();

            if ( sections != null && sections.length > 0 ) {
                // filter out the requested section
                if ( sections[0].equals( "/WCS_Capabilities/Service" ) ) {
                    xslt = StringTools.replace( xslt, "XXX", "./wcs:Service", false );
                } else if ( sections[0].equals( "/WCS_Capabilities/Capability" ) ) {
                    xslt = StringTools.replace( xslt, "XXX", "./wcs:Capability", false );
                } else if ( sections[0].equals( "/WCS_Capabilities/ContentMetadata" ) ) {
                    xslt = StringTools.replace( xslt, "XXX", "./wcs:ContentMetadata", false );
                } else {
                    xslt = StringTools.replace( xslt, "XXX", ".", false );
                }
                XSLTDocument xslSheet = new XSLTDocument();
                xslSheet.load( new StringReader( xslt ), XMLFragment.DEFAULT_URL );
                doc = xslSheet.transform( doc );
            }

            response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            OutputStream os = response.getOutputStream();
            doc.write( os );
            os.close();
        } catch ( Exception e ) {
            LOG.logError( "ERROR: " + StringTools.stackTraceToString( e ), e );
        }
    }

    /**
     * sends the passed <tt>CoverageDescription</tt> to the calling client
     * 
     * @param response
     *            <tt>HttpServletResponse</tt> for opening stream to the client
     * @param serviceRes
     *            object to send
     */
    private void sendCoverageDescription( HttpServletResponse response,
                                         CoverageDescription serviceRes ) {
        try {
            XMLFragment doc = org.deegree.ogcwebservices.wcs.XMLFactory.export( serviceRes );
            response.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            OutputStream os = response.getOutputStream();
            doc.write( os );
            os.close();
        } catch ( Exception e ) {
            LOG.logError( "ERROR: " + StringTools.stackTraceToString( e ), e );
        }
    }

    /**
     * writes the <tt>GridCoverage</tt> that is encapsulated within the <tt>ResultCoverage</tt>
     * into the <tt>OutputStream</tt> taken from the passed <tt>HttpServletResponse</tt>
     * 
     * @param response
     *            destination for writing the result coverage
     * @param serviceRes
     *            response to a GetCoverage request
     * @param size
     *            desired size of the GridCoverage
     */
    private void sendCoverage( HttpServletResponse response, ResultCoverage serviceRes,
                              Envelope size ) {
        try {
            Format format = new FormatIm( serviceRes.getDesiredOutputFormat() );
            Format[] formats = new Format[] { format };
            GridCoverageExchange gce = new GridCoverageExchangeIm( formats );
            String frmt = format.getName();
            if ( frmt.equalsIgnoreCase( "png" ) ) {
                frmt = "image/png";
            } else if ( frmt.equalsIgnoreCase( "bmp" ) ) {
                frmt = "image/bmp";
            } else if ( frmt.equalsIgnoreCase( "tif" ) || frmt.equalsIgnoreCase( "tiff" )
                        || frmt.equalsIgnoreCase( "geotiff" ) ) {
                frmt = "image/tiff";
            } else if ( frmt.equalsIgnoreCase( "gif" ) ) {
                frmt = "image/gif";
            } else if ( frmt.equalsIgnoreCase( "jpg" ) || frmt.equalsIgnoreCase( "jpeg" ) ) {
                frmt = "image/jpeg";
            } else if ( frmt.equalsIgnoreCase( "GML2" ) || frmt.equalsIgnoreCase( "GML3" )
                        || frmt.equalsIgnoreCase( "GML" ) ) {
                frmt = "application/vnd.ogc.gml";
            } else {
                frmt = "application/octet-stream";
            }
            response.setContentType( frmt );

            GetCoverage req = serviceRes.getRequest();
            List<GeneralParameterValue> list = new ArrayList<GeneralParameterValue>( 10 );
            OperationParameter op = new OperationParameterIm( "addr", null,
                                                              OGCServletController.address );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "width", null, new Integer( (int) size.getWidth() + 1 ) );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "height", null, new Integer( (int) size.getHeight() + 1) );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "service", null, "WCS" );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "version", null, req.getVersion() );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "coverage", null, req.getSourceCoverage() );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "crs", null,
                                           req.getDomainSubset().getRequestSRS().getCode() );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "response_crs", null, req.getOutput().getCrs().getCode() );
            list.add( new GeneralParameterValueIm( op ) );
            Envelope env = req.getDomainSubset().getSpatialSubset().getEnvelope();
            String s = StringTools.concat( 100, new Double( env.getMin().getX() ), ',',
                                           new Double( env.getMin().getY() ), ',',
                                           new Double( env.getMax().getX() ), ',',
                                           new Double( env.getMax().getY() ) );
            op = new OperationParameterIm( "BBOX", null, s );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "FORMAT", null, "GeoTiff" );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "Request", null, "GetCoverage" );
            list.add( new GeneralParameterValueIm( op ) );

            GeneralParameterValue[] gpvs = new GeneralParameterValue[list.size()];
            if ( serviceRes.getCoverage() != null ) {
                OutputStream os = response.getOutputStream();
                GridCoverageWriter writer = gce.getWriter( os, format );

                writer.write( (GridCoverage) serviceRes.getCoverage(), list.toArray( gpvs ) );
                os.close();
            } else {
                OGCWebServiceException owse = 
                    new OGCWebServiceException( getClass().getName(),
                                                Messages.getString( "WCSHandler.NULLCOVERAGE" ) );
                sendException( response, owse );
            }

        } catch ( Exception e ) {
            LOG.logError( "ERROR: " + StringTools.stackTraceToString( e ), e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.enterprise.servlet.ServiceDispatcher#setConfiguration(java.net.URL)
     */
    public WCService getService()
                            throws ServiceException {
        LOG.entering();

        WCService service = null;
        try {
            service = WCServiceFactory.getService();
        } catch ( Exception e ) {
            LOG.logError( "ERROR: " + StringTools.stackTraceToString( e ), e );
            throw new ServiceException( e );
        }

        LOG.exiting();
        return service;
    }

}
/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WCSHandler.java,v $
 * Revision 1.35  2006/10/17 20:31:18  poth
 * *** empty log message ***
 *
 * Revision 1.34  2006/07/23 10:05:54  poth
 * setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)
 *
 * Revision 1.33  2006/07/05 12:59:58  poth
 * bug fix - set correct coverage width and height when calling GridCoverageWriter
 *
 * Revision 1.32  2006/05/18 16:32:53  poth
 * exception message externalized / bug fix -> GetCoverage returns null
 *
 *  Revision 1.31  2006/04/06 20:25:23  poth
 *  *** empty log message ***
 * 
 *  Revision 1.30  2006/04/05 07:21:11  poth
 *  *** empty log message ***
 * 
 *  Revision 1.29  2006/04/04 20:39:41  poth
 *  *** empty log message ***
 * 
 *  Revision 1.28  2006/03/30 21:20:24  poth
 *  *** empty log message ***
 * 
 *  Revision 1.27  2006/03/15 22:20:09  poth
 *  *** empty log message ***
 * 
 *  Revision 1.26  2006/03/03 13:37:42  poth
 *  *** empty log message ***
 * 
 *  Revision 1.25  2006/03/02 21:39:38  poth
 *  *** empty log message ***
 * 
 *  Revision 1.24  2006/03/02 11:06:04  poth
 *  *** empty log message ***
 * 
 *  Revision 1.23  2006/03/01 16:22:37  poth
 *  *** empty log message ***
 * 
 *  Revision 1.22  2006/02/28 09:46:06  poth
 *  *** empty log message ***
 * 
 *  Revision 1.21  2006/01/25 10:10:27  poth
 *  *** empty log message ***
 * 
 *  Revision 1.20  2006/01/19 21:24:49  poth
 *  *** empty log message ***
 * 
 *  Revision 1.19  2005/12/01 09:08:00  poth
 *  no message
 * 
 *  Revision 1.18  2005/11/17 13:11:11  deshmukh
 *  *** empty log message ***
 *  Changes to this
 * class. What the people have been up to: Revision 1.17 2005/10/04 14:30:56 poth Changes to this
 * class. What the people have been up to: no message Changes to this class. What the people have
 * been up to:  Revision 1.16 2005/09/27
 * 19:53:18 poth  no message Changes to this
 * class. What the people have been up to: 
 * Revision 1.15 2005/08/29 17:14:14 mschneider Changes to this class. What the people have been up
 * to: Uses new convenience XSLTDocument.transform() now (omits null parameters). Changes to this
 * class. What the people have been up to: 
 * Revision 1.14 2005/08/23 13:38:49 mschneider Changes to this class. What the people have been up
 * to: Refactored due to new XSLTDocument class. Changes to this class. What the people have been up
 * to: Revision 1.18 2004/08/16 06:23:33 ap no message
 * 
 * Revision 1.17 2004/08/13 12:58:25 tf no message
 * 
 * Revision 1.16 2004/08/02 12:43:10 tf no message
 * 
 * Revision 1.15 2004/07/19 06:20:00 ap no message
 * 
 * Revision 1.14 2004/07/15 15:29:42 ap no message
 * 
 * Revision 1.13 2004/07/09 07:01:33 ap no message
 * 
 * Revision 1.12 2004/07/02 15:36:21 ap no message
 * 
 * Revision 1.11 2004/06/30 10:57:31 ap no message
 * 
 * 
 ************************************************************************************************* */
