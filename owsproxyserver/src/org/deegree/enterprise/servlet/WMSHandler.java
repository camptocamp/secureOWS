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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.Marshallable;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.OGCWebServiceResponse;
import org.deegree.ogcwebservices.wms.InvalidFormatException;
import org.deegree.ogcwebservices.wms.WMService;
import org.deegree.ogcwebservices.wms.WMServiceFactory;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities_1_3_0;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType;
import org.deegree.ogcwebservices.wms.configuration.WMSDeegreeParams;
import org.deegree.ogcwebservices.wms.operation.DescribeLayerResult;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfoResult;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphicResult;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetMapResult;
import org.deegree.ogcwebservices.wms.operation.GetStylesResult;
import org.deegree.ogcwebservices.wms.operation.PutStylesResult;
import org.deegree.ogcwebservices.wms.operation.WMSGetCapabilitiesResult;
import org.deegree.owscommon.XMLFactory;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <code>WMSHandler</code> is the handler class for WMS requests and their results.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.50 $, $Date: 2006/11/24 15:05:35 $
 * 
 * @since 2.0
 */
public class WMSHandler extends AbstractOWServiceHandler {

    private static ILogger LOG = LoggerFactory.getLogger( WMSHandler.class );

    private Color bgColor = Color.WHITE;

    private HttpServletResponse resp = null;

    private OGCWebServiceRequest request = null;

    private String exceptionFormat;

    private String format = null;

    private boolean transparent = false;

    private int height = 400;

    private int width = 600;

    private WMSConfigurationType configuration = null;

    /**
     *  
     */
    WMSHandler() {
        LOG.logDebug( "New WMSHandler instance created: " + this.getClass().getName() );
    }

    /**
     * performs the passed OGCWebServiceRequest by accessing service from the
     * pool and passing the request to it
     */
    public void perform( OGCWebServiceRequest request, HttpServletResponse response )
                            throws ServiceException {
        LOG.entering();
        try {
            resp = response;
            this.request = request;

            OGCWebService service = WMServiceFactory.getService();
            configuration = (WMSConfigurationType) ( (WMService) service ).getCapabilities();

            // EXCEPTION HANDLING NOTES:
            // currently, the exceptions are handled differently for each request type,
            // change the behaviour here
            if ( request instanceof GetMap ) {
                GetMap req = (GetMap) request;
                exceptionFormat = req.getExceptions();
                format = req.getFormat();
                bgColor = req.getBGColor();
                transparent = req.getTransparency();
                height = req.getHeight();
                width = req.getWidth();
            }

            if ( request instanceof GetLegendGraphic ) {
                GetLegendGraphic req = (GetLegendGraphic) request;
                exceptionFormat = req.getExceptions();
                format = req.getFormat();
                height = req.getHeight();
                width = req.getWidth();
            }

            if ( request instanceof GetFeatureInfo ) {
                GetFeatureInfo req = (GetFeatureInfo) request;
                exceptionFormat = req.getExceptions();
            }

            if ( exceptionFormat == null || exceptionFormat.equals( "" ) ) {
                if ( "1.1.1".equals( request.getVersion() ) ) {
                    exceptionFormat = "application/vnd.ogc.se_xml";
                } else {
                    exceptionFormat = "XML";
                }
            }

            // fixup the exception formats, 1.3.0 has it different
            if ( "INIMAGE".equalsIgnoreCase( exceptionFormat ) ) {
                exceptionFormat = "application/vnd.ogc.se_inimage";
            }
            if ( "BLANK".equalsIgnoreCase( exceptionFormat ) ) {
                exceptionFormat = "application/vnd.ogc.se_blank";
            }

            if ( service == null ) {
                writeServiceExceptionReport( new OGCWebServiceException( "WMS",
                                                                         "could not access a WMService instance" ) );
                return;
            }

            // first, try the normal case
            Object o = service.doService( request );
            handleResponse( o );

        } catch ( OGCWebServiceException e ) {
            writeServiceExceptionReport( e );
        }
        LOG.exiting();
    }

    /**
     *
     *
     * @param result
     * @throws OGCWebServiceException 
     */
    private void handleResponse( Object result ) {
        LOG.entering();

        // this method may need restructuring

        // handle exception case
        if ( result instanceof OGCWebServiceException ) {
            writeServiceExceptionReport( (OGCWebServiceException) result );
            LOG.exiting();
            return;
        }

        try {
            OGCWebServiceResponse response = (OGCWebServiceResponse) result;

            if ( response.getException() != null ) {
                // handle the case that an exception occured during the
                // request performance
                writeServiceExceptionReport( response.getException() );
            } else {
                if ( response instanceof OGCWebServiceException ) {
                    writeServiceExceptionReport( (OGCWebServiceException) response );
                } else if ( response instanceof Exception ) {
                    sendException( resp, (Exception) response );
                } else if ( response instanceof WMSGetCapabilitiesResult ) {
                    handleGetCapabilitiesResponse( (WMSGetCapabilitiesResult) response );
                } else if ( response instanceof GetMapResult ) {
                    handleGetMapResponse( (GetMapResult) response );
                } else if ( response instanceof GetFeatureInfoResult ) {
                    handleFeatureInfoResponse( (GetFeatureInfoResult) response );
                } else if ( response instanceof GetStylesResult ) {
                    handleGetStylesResponse( (GetStylesResult) response );
                } else if ( response instanceof PutStylesResult ) {
                    handlePutStylesResponse( (PutStylesResult) response );
                } else if ( response instanceof DescribeLayerResult ) {
                    handleDescribeLayerResponse( (DescribeLayerResult) response );
                } else if ( response instanceof GetLegendGraphicResult ) {
                    handleGetLegendGraphicResponse( (GetLegendGraphicResult) response );
                }
            }
        } catch ( InvalidFormatException ife ) {
            LOG.logError( ife.getMessage(), ife );
            writeServiceExceptionReport( new OGCWebServiceException( "InvalidFormat",
                                                                     ife.getMessage() ) );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            writeServiceExceptionReport( new OGCWebServiceException( "WMS:write",
                                                                     e.getLocalizedMessage() ) );
        }

        LOG.exiting();
    }

    /**
     * handles the response to a get capabilities request
     *
     * @param response 
     * @throws IOException 
     * @throws TransformerException 
     */
    private void handleGetCapabilitiesResponse( WMSGetCapabilitiesResult response )
                            throws IOException, TransformerException {
        LOG.entering();

        WMSConfigurationType capa = response.getCapabilities();

        WMSDeegreeParams params = capa.getDeegreeParams();

        // version war follows

        boolean version130 = "1.3.0".equals( capa.calculateVersion( request.getVersion() ) );

        // version not set -> use highest supported version
        // use request's version otherwise

        boolean support111 = false;
        boolean support130 = false;
        for ( String version : params.getSupportedVersions() ) {
            if ( "1.1.1".equals( version ) )
                support111 = true;
            if ( "1.3.0".equals( version ) )
                support130 = true;
        }

        if ( ( !support130 ) && ( !support111 ) ) {
            support111 = true;
        }

        if ( version130 && support130 ) {
            resp.setContentType( "text/xml" );
        } else {
            resp.setContentType( "application/vnd.ogc.wms_xml" );
        }

        XMLFragment doc = null;

        if ( ( ( ( !version130 ) && support111 ) || ( !support130 ) )
             && ( capa instanceof WMSCapabilities_1_3_0 ) ) {
            doc = org.deegree.ogcwebservices.wms.XMLFactory.exportAs_1_1_1( (WMSCapabilities_1_3_0) capa );
        } else {
            doc = org.deegree.ogcwebservices.wms.XMLFactory.export( (WMSCapabilities) capa );
        }

        if ( ( version130 && support130 ) || ( !support111 ) ) {
            doc.getRootElement().setAttribute( "xmlns:xsi",
                                               "http://www.w3.org/2001/XMLSchema-instance" );
            doc.getRootElement().setAttribute(
                                               "xsi:schemaLocation",
                                               "http://www.opengis.net/wms "
                                                                       + "http://schemas.opengis.net/wms/1.3.0/capabilities_1_3_0.xsd"
                                                                       + " http://www.opengis.net/sld "
                                                                       + "http://hillary.lat-lon.de/~schmitz/sld.xsd" );

            doc.prettyPrint( resp.getWriter() );
        } else {
            String xml = DOMPrinter.nodeToString( doc.getRootElement(), "" );

            String dtd = NetWorker.url2String( configuration.getDeegreeParams().getDTDLocation() );
            StringBuffer sb = new StringBuffer();
            sb.append( "<!DOCTYPE WMT_MS_Capabilities SYSTEM " );
            sb.append( "'" + dtd + "' \n" );
            sb.append( "[\n<!ELEMENT VendorSpecificCapabilities EMPTY>\n]>" );

            if ( xml.indexOf( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ) > -1 ) {
                xml = StringTools.replace( xml, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                                           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
                                                                   + sb.toString(), false );
            } else {
                xml = StringTools.concat( 50000, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                                          "\n", sb.toString(), xml );
            }
            xml = StringTools.replace( xml,
                                       "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
                                       "", false );

            try {
                PrintWriter pw = resp.getWriter();
                pw.print( xml );
                pw.close();
            } catch ( Exception e ) {
                LOG.logError( "-", e );
            }

        }

        LOG.exiting();
    }

    /**
     * handles the response to a get map request
     *
     * @param response 
     */
    private void handleGetMapResponse( GetMapResult response )
                            throws InvalidFormatException {
        LOG.entering();

        String mime = MimeTypeMapper.toMimeType( ( (GetMap) request ).getFormat() );

        if ( !MimeTypeMapper.isImageType( mime ) ) {
            throw new InvalidFormatException( mime + " is not a known image format" );
        }

        writeImage( response.getMap(), mime );

        LOG.exiting();
    }

    /**
     * handles the response to a get featureinfo request
     *
     * @param response 
     */
    private void handleFeatureInfoResponse( GetFeatureInfoResult response )
                            throws Exception {
        LOG.entering();

        GetFeatureInfo req = (GetFeatureInfo) request;

        String s = req.getInfoFormat();

        // check if GML is actually the correct one
        // THIS IS A HACK
        if ( req.isInfoFormatDefault() ) {
            OperationsMetadata om = configuration.getOperationMetadata();
            Operation op = om.getOperation( new QualifiedName( "GetFeatureInfo" ) );
            DomainType dt = (DomainType) op.getParameter( new QualifiedName( "Format" ) );
            List<TypedLiteral> vals = dt.getValues();
            s = vals.get( 0 ).getValue();
        }

        String mime = MimeTypeMapper.toMimeType( s );
        resp.setContentType( mime + "; charset=" + CharsetUtils.getSystemCharset() );

        String fir = response.getFeatureInfo();

        String filter = FeatureInfoFilterDef.getString( s );

        if ( filter != null ) {
            handleFilteredFeatureInfoResponse( fir, filter );
        } else {
            OutputStreamWriter os = null;
            try {
                os = new OutputStreamWriter( resp.getOutputStream(),
                                             CharsetUtils.getSystemCharset() );
                os.write( fir );
            } catch ( Exception e ) {
                LOG.logError( "could not write to outputstream", e );
            } finally {
                os.close();
            }
        }

        LOG.exiting();
    }

    /**
     * @param fir
     * @param filter
     * @throws MalformedURLException
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws TransformerException
     */
    private void handleFilteredFeatureInfoResponse( String fir, String filter )
                            throws Exception {
        LOG.entering();

        URL url = new URL( configuration.getBaseURL(), filter );
        LOG.logDebug( "used XSLT for transformation: ", url );
        LOG.logDebug( "GML document to transform", fir );
        if ( url != null ) {
            Source xmlSource = new StreamSource( new StringReader( fir ) );
            Source xslSource;
            try {
                xslSource = new StreamSource( url.openStream() );
            } catch ( IOException ioe ) {
                throw new InvalidFormatException( "Unknown feature info format." );
            }
            OutputStream os = null;
            try {
                os = resp.getOutputStream();
                StreamResult result = new StreamResult( os );
                XSLTDocument.transform( xmlSource, xslSource, result, null, null );
            } catch ( IOException e ) {
                LOG.logError( "could not write to outputstream", e );
            } finally {
                os.close();
            }
        }
        LOG.exiting();
    }

    /**
     * handles the response to a get styles request
     *
     * @param response 
     */
    private void handleGetStylesResponse( GetStylesResult response ) {
        throw new RuntimeException( "method: handleGetStylesResponse not implemented yet" );
    }

    /**
     * handles the response to a put styles request
     *
     * @param response 
     */
    private void handlePutStylesResponse( PutStylesResult response ) {
        throw new RuntimeException( "method: handlePutStylesResponse not implemented yet" );
    }

    /**
     * handles the response to a describe layer request
     *
     * @param response 
     */
    private void handleDescribeLayerResponse( DescribeLayerResult response ) {
        throw new RuntimeException( "method: handleDescribeLayerResponse not implemented yet" );
    }

    /**
     * handles the response to a get legend graphic request
     *
     * @param response 
     */
    private void handleGetLegendGraphicResponse( GetLegendGraphicResult response )
                            throws Exception {
        LOG.entering();

        String mime = MimeTypeMapper.toMimeType( ( (GetLegendGraphic) request ).getFormat() );

        if ( !MimeTypeMapper.isImageType( mime ) ) {
            throw new InvalidFormatException( mime + " is not a known image format" );
        }

        writeImage( response.getLegendGraphic(), mime );

        LOG.exiting();
    }

    /**
     * writes an service exception report into the <tt>OutputStream</tt> back 
     * to the client. the method considers the format an exception shall be 
     * returned to the client as defined in the request.
     *
     * @param exception the exception object containing the code and message
     * @throws OGCWebServiceException 
     */
    private void writeServiceExceptionReport( OGCWebServiceException exception ) {
        LOG.entering();

        String code = "none";
        if ( exception.getCode() != null ) {
            code = exception.getCode().value;
        }
        String message = exception.getMessage();

        LOG.logInfo( "sending exception in format " + exceptionFormat );

        if ( exceptionFormat.equals( "application/vnd.ogc.se_inimage" ) ) {
            BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics g = bi.getGraphics();

            if ( !transparent ) {
                g.setColor( bgColor );
                g.fillRect( 0, 0, bi.getWidth(), bi.getHeight() );
            }

            g.setColor( Color.BLUE );
            g.drawString( code, 5, 20 );
            int pos1 = message.indexOf( ':' );
            g.drawString( message.substring( 0, pos1 + 1 ), 5, 50 );
            g.drawString( message.substring( pos1 + 1, message.length() ), 5, 80 );
            String mime = MimeTypeMapper.toMimeType( format );
            writeImage( bi, mime );
        } else if ( exceptionFormat.equals( "application/vnd.ogc.se_blank" ) ) {
            BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics g = bi.getGraphics();

            if ( !transparent ) {
                g.setColor( bgColor );
                g.fillRect( 0, 0, bi.getWidth(), bi.getHeight() );
            }

            g.dispose();
            String mime = MimeTypeMapper.toMimeType( format );
            writeImage( bi, mime );
        } else {
            LOG.logInfo( "Sending OGCWebServiceException to client." );
            ExceptionReport report = new ExceptionReport(
                                                          new OGCWebServiceException[] { exception } );
            try {
                XMLFragment doc;

                if ( exceptionFormat.equals( "XML" ) ) {
                    resp.setContentType( "text/xml" );
                    doc = XMLFactory.exportNS( report );
                } else {
                    resp.setContentType( "application/vnd.ogc.se_xml" );
                    doc = XMLFactory.export( report );
                }

                OutputStream os = resp.getOutputStream();
                doc.write( os );
                os.close();
            } catch ( Exception ex ) {
                LOG.logError( "ERROR: " + ex.getMessage(), ex );
            }
        }

        LOG.exiting();
    }

    /**
     * writes the passed image to the response output stream.
     * @param output
     * @param mime
     */
    private void writeImage( Object output, String mime ) {
        try {
            OutputStream os = null;
            resp.setContentType( mime );
            if ( mime.equalsIgnoreCase( "image/gif" ) ) {
                os = resp.getOutputStream();
                ImageUtils.saveImage( (BufferedImage) output, os, "gif", 1 );
            } else if ( mime.equalsIgnoreCase( "image/jpg" )
                        || mime.equalsIgnoreCase( "image/jpeg" ) ) {
                os = resp.getOutputStream();
                ImageUtils.saveImage( (BufferedImage) output, os, "jpeg",
                                      configuration.getDeegreeParams().getMapQuality() );
            } else if ( mime.equalsIgnoreCase( "image/png" ) ) {
                os = resp.getOutputStream();
                ImageIO.write( (BufferedImage) output, "png", os );
            } else if ( mime.equalsIgnoreCase( "image/tif" )
                        || mime.equalsIgnoreCase( "image/tiff" ) ) {
                os = resp.getOutputStream();
                ImageUtils.saveImage( (BufferedImage) output, os, "tif", 1 );
            } else if ( mime.equalsIgnoreCase( "image/bmp" ) ) {
                os = resp.getOutputStream();
                ImageUtils.saveImage( (BufferedImage) output, os, "bmp", 1 );
            } else if ( mime.equalsIgnoreCase( "image/svg+xml" ) ) {
                os = resp.getOutputStream();
                resp.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
                PrintWriter pw = new PrintWriter( os );
                DOMPrinter.printNode( pw, (Node) output );
                pw.close();
            } else {
                resp.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
                os = resp.getOutputStream();
                OGCWebServiceException exce = new OGCWebServiceException(
                                                                          "WMS:writeImage",
                                                                          "unsupported image format: "
                                                                                                  + mime );
                os.write( ( (Marshallable) exce ).exportAsXML().getBytes() );
            }

            os.close();
        } catch ( Exception e ) {
            LOG.logError( "-", e );
        }
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WMSHandler.java,v $
 Revision 1.50  2006/11/24 15:05:35  schmitz
 Added a bogus sld schema document to be able to validate the GetCapabilities response with a GetLegendGraphic request type.

 Revision 1.49  2006/11/22 15:38:31  schmitz
 Fixed more exception handling, especially for the GetFeatureInfo request.

 Revision 1.48  2006/11/22 14:06:26  schmitz
 Fixed some minor details in the WMS example configuration.
 Added CRS:84 to proj4.
 Fixed exception handling for WMS.

 Revision 1.47  2006/10/27 09:52:24  schmitz
 Brought the WMS up to date regarding 1.1.1 and 1.3.0 conformance.
 Fixed a bug while creating the default GetLegendGraphics URLs.

 Revision 1.46  2006/10/22 20:19:52  poth
 support for vendor specific operation getScaleBar removed

 Revision 1.45  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.44  2006/09/12 10:08:33  schmitz
 Fixed another xlink issue, fixed content-type header for
 WMS capabilities.

 Revision 1.43  2006/09/08 13:43:42  schmitz
 Fixed the versioning of WMS for the case no <SupportedVersion> was defined.

 Revision 1.42  2006/09/08 08:42:02  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.39  2006/08/21 15:42:08  mschneider
 Removed (senseless) "implements ServiceDispatcher".

 Revision 1.38  2006/08/06 20:10:20  poth
 runtime ex ception will be thrown if a not implemented method will be called

 Revision 1.37  2006/08/06 19:48:11  poth
 file header and footer added

 Revision 1.36  2006/07/28 08:01:27  schmitz
 Updated the WMS for 1.1.1 compliance.
 Fixed some documentation.

 Revision 1.35  2006/07/23 10:05:54  poth
 setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

 Revision 1.34  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */
