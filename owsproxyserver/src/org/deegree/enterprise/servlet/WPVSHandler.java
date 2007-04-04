//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/servlet/WPVSHandler.java,v 1.10 2006/10/17 20:31:18 poth Exp $
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.Marshallable;
import org.deegree.graphics.Encoders;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wpvs.WPVService;
import org.deegree.ogcwebservices.wpvs.WPVServiceFactory;
import org.deegree.ogcwebservices.wpvs.XMLFactory;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSCapabilities;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSCapabilitiesDocument;
import org.deegree.ogcwebservices.wpvs.configuration.WPVSConfiguration;
import org.deegree.ogcwebservices.wpvs.operation.GetView;
import org.deegree.ogcwebservices.wpvs.operation.GetViewResponse;
import org.w3c.dom.Node;

/**
 * Handler for the Web Perspective View Service (WPVS).
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.10 $, $Date: 2006/10/17 20:31:18 $
 * 
 * @since 2.0
 */
public class WPVSHandler extends AbstractOWServiceHandler {
    
    private static ILogger LOG = LoggerFactory.getLogger( WPVSHandler.class );
    
    /**
     * Performs the passed OGCWebServiceRequest by accessing service from the
     * pool and passing the request to it
     * 
     * @param request the incoming web service request
     * @param httpResponse the outgoing web serivce response
     * @throws ServiceException
     * @throws OGCWebServiceException
     */
    public void perform(OGCWebServiceRequest request,
                        HttpServletResponse httpResponse) throws ServiceException,
                                                        OGCWebServiceException {
  
        LOG.entering();

        LOG.logDebug( StringTools.concat( 200, "Performing request: ", request.toString() ) );
        
        OGCWebService service = WPVServiceFactory.createInstance();
        
        try {
            Object response = service.doService( request );
            if ( response instanceof WPVSCapabilities ) {
                sendGetCapabilitiesResponse( httpResponse, (WPVSCapabilities) response );
            } else if ( response instanceof GetViewResponse ) {                
                sendGetViewResponse( httpResponse, (GetViewResponse) response );
            } else {
                String s = ( response == null ? "null response object" : response.getClass().getName() );                
                //this is not really nice...because excepts get cought later on below
                throw new OGCWebServiceException(  getClass().getName(), 
                		StringTools.concat( 200, "Unknown response class: '", s, "'." ) );
            }
        } catch (OGCWebServiceException e) {

            LOG.logError( "Error performing WPVFS request.", e );
			if ( request instanceof GetView && 
                 ((GetView)request).getExceptionFormat().equals( "INIMAGE") ){
				sendExceptionImage( httpResponse, e, (GetView)request );
				
			} else {
				sendException( httpResponse, e );
			}
        }

        
        LOG.exiting();
    }
 
    //  TODO common to WMS
    private void sendExceptionImage( HttpServletResponse httpResponse, OGCWebServiceException e, 
    								 GetView request) {
    	
    	Dimension d = request.getImageDimension();
    	
    	BufferedImage bi = new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_RGB );
        Graphics g = bi.getGraphics();
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, d.width, d.height );
        g.setColor( Color.BLUE );
        
        String s = e.getLocator() ;
        String location = s != null ? s : "Unknown";
        s = e.getMessage();
        String message = s != null ? s : "Unknown reason!";
        
        g.drawString( location, 5, 20 );
        g.drawString( message, 15, 50 );
        String mime = MimeTypeMapper.toMimeType( request.getOutputFormat() );
        g.dispose();
        writeImage( bi, mime, httpResponse );
	}
    
    //TODO common to WMS
    private void writeImage( Object output, String mime, HttpServletResponse resp ) {
        try {
            OutputStream os = null;
            resp.setContentType( mime );

            if ( mime.equalsIgnoreCase( "image/gif" ) ) { 
                os = resp.getOutputStream();
                ImageUtils.saveImage ( (BufferedImage)output, os, "gif", 1 );
            } else if ( mime.equalsIgnoreCase( "image/jpg" ) ||  
                            mime.equalsIgnoreCase( "image/jpeg" ) ) { 
                os = resp.getOutputStream();
                ImageUtils.saveImage ( (BufferedImage)output, os, "jpeg", 1 );
            } else if ( mime.equalsIgnoreCase( "image/png" ) ) { 
                os = resp.getOutputStream();
                ImageUtils.saveImage ( (BufferedImage)output, os, "png", 1 );
            } else if ( mime.equalsIgnoreCase( "image/tif" ) ||  
                            mime.equalsIgnoreCase( "image/tiff" ) ) { 
                os = resp.getOutputStream();
                ImageUtils.saveImage ( (BufferedImage)output, os, "tif", 1 );
            } else if ( mime.equalsIgnoreCase( "image/bmp" ) ) { 
                os = resp.getOutputStream();
                ImageUtils.saveImage ( (BufferedImage)output, os, "bmp", 1 );
            } else if ( mime.equalsIgnoreCase( "image/svg+xml" ) ) { 
                os = resp.getOutputStream();
                PrintWriter pw = new PrintWriter( os );
                DOMPrinter.printNode( pw, (Node)output );
                pw.close();
            } else {               
                resp.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
                os = resp.getOutputStream();
                OGCWebServiceException exce = 
                    new OGCWebServiceException( "WMS:writeImage",  
                                                "unsupported image format: " + mime ); 
                os.write( ( (Marshallable)exce ).exportAsXML().getBytes() );
            }

            os.close();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }
    
    
	/**
     * Sends the result of a someWPVService.doService( request ) bacn to the client
     * @param httpResponse the response object used to pipe the result
     * @param getViewResponse the actua result to be sent
     */
    private void sendGetViewResponse( HttpServletResponse httpResponse, 
                                      GetViewResponse getViewResponse ) {
        LOG.entering();
        
        String mime = MimeTypeMapper.toMimeType( getViewResponse.getOutputFormat() );
        httpResponse.setContentType( mime );
        
        //GetView response is, for the time being, always an image
        writeImage( (Image)getViewResponse.getOutput(), httpResponse, mime );

        LOG.exiting();
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
                                             WPVSCapabilities capabilities )
        															throws OGCWebServiceException {
        try {
            httpResponse.setContentType( "text/xml" );
            WPVSCapabilitiesDocument document = XMLFactory.export( capabilities );
            document.write( httpResponse.getOutputStream() );
        } catch (IOException e) {
            LOG.logError( "Error sending GetCapabilities response.", e );
            
            throw new OGCWebServiceException( getClass().getName(),
            	"Error exporting capabilities for GetCapabilities response." );
        } 
    }    

    /**
     * Writes an output of a GetView request to the <code>httpResponse</code> using the
     * <code>mime</code> type.
     * @param output the image to be sent back
     * @param httpResponse the response to pipe the image
     * @param mime the type of image
     */
    private void writeImage( Image output, HttpServletResponse httpResponse , String mime ) {
        try {
            
            OutputStream os = httpResponse.getOutputStream();
            httpResponse.setContentType( mime );
            
            if ( mime.equalsIgnoreCase( "image/jpg" ) ||  
                            mime.equalsIgnoreCase( "image/jpeg" ) ) { 
                
                OGCWebService service = WPVServiceFactory.createInstance();
                WPVSConfiguration config = (WPVSConfiguration)((WPVService)service)
                	.getCapabilities();
                float quality = config.getDeegreeParams().getViewQuality();
                Encoders.encodeJpeg( os, (BufferedImage)output, quality );
            
                //TODO png accepted?
            } else if ( mime.equalsIgnoreCase( "image/png" ) ) { 
                Encoders.encodePng( os, (BufferedImage)output );
            } else if ( mime.equalsIgnoreCase( "image/tif" ) ||  
                            mime.equalsIgnoreCase( "image/tiff" ) ) { 
                Encoders.encodeTiff( os, (BufferedImage)output );
            } else if ( mime.equalsIgnoreCase( "image/bmp" ) ) { 
                Encoders.encodeBmp( os, (BufferedImage)output );
            } else {               
                httpResponse.setContentType( "text/xml" ); 
                os = httpResponse.getOutputStream();
                OGCWebServiceException exce = 
                    new OGCWebServiceException( "WMS:writeImage",  
                                                "unsupported image format: " + mime ); 
                os.write( ( (Marshallable)exce ).exportAsXML().getBytes() );
            }

            os.close();
        } catch ( Exception e ) {
            LOG.logError( "-", e );
        }        
    }
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPVSHandler.java,v $
Revision 1.10  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.9  2006/07/23 10:05:54  poth
setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

Revision 1.8  2006/04/06 20:25:23  poth
*** empty log message ***

Revision 1.7  2006/04/05 07:21:11  poth
*** empty log message ***

Revision 1.6  2006/03/30 21:20:24  poth
*** empty log message ***

Revision 1.5  2006/03/02 15:22:23  taddei
returning exceptions as images when it's the case

Revision 1.4  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.3  2005/12/16 15:16:33  taddei
added handling for GetView

Revision 1.2  2005/12/13 14:59:48  taddei
minimal javadoc changes

Revision 1.1  2005/12/13 14:44:11  taddei
added wpvs handler to cvs


********************************************************************** */