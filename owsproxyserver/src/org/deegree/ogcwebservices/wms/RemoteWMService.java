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
package org.deegree.ogcwebservices.wms;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.operation.DescribeLayer;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetStyles;
import org.deegree.ogcwebservices.wms.operation.PutStyles;
import org.deegree.ogcwebservices.wms.operation.WMSGetCapabilities;
import org.deegree.ogcwebservices.wms.operation.WMSProtocolFactory;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;



/**
 * An instance of the class acts as a wrapper to a remote WMS.
 *
 * @version $Revision: 1.24 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class RemoteWMService implements OGCWebService {
    
    private static ILogger LOG = LoggerFactory.getLogger( RemoteWMService.class );
    
    private static final String GETCAPABILITIES_NAME  = "GetCapabilities";
    private static final String CAPABILITIES_NAME     = "Capabilities";
    private static final String GETMAP_NAME           = "GetMap";
    private static final String MAP_NAME              = "Map";
    private static final String GETFEATUREINFO_NAME   = "GetFeatureInfo";
    private static final String FEATUREINFO_NAME      = "FeatureInfo";
    private static final String DESCRIBELAYER_NAME    = "DescribeLayer";
    private static final String GETLEGENDGRAPHIC_NAME = "GetLegendGraphic";
    private static final String GETSTYLES_NAME        = "GetStyles";
    private static final String PUTSTYLES_NAME        = "PutStyles";
//    private static final String UNKNOWN_NAME          = "Unknown";
    
    protected HashMap<String, URL> addresses = null;
    protected WMSCapabilities capabilities = null;

    /**
     * Creates a new instance of RemoteWMService 
     * 
     * @param capabilities 
     */
    public RemoteWMService( WMSCapabilities capabilities ) {
        this.capabilities = capabilities;
        addresses = new HashMap<String, URL>();

        // get GetCapabilities operation address
        List<DCP> dcps = null;
        HTTP http = null;
        
        OperationsMetadata om = capabilities.getOperationMetadata();

        if ( capabilities.getVersion().equals( "1.0.0" ) ) {
            dcps = om.getOperation( new QualifiedName( CAPABILITIES_NAME ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( CAPABILITIES_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        } else {
            dcps = om.getOperation( new QualifiedName( GETCAPABILITIES_NAME ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( GETCAPABILITIES_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        }

        // get GetMap operation address
        if ( capabilities.getVersion().equals( "1.0.0" ) ) {
            dcps = om.getOperation( new QualifiedName( MAP_NAME ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( MAP_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        } else {
            dcps = om.getOperation( new QualifiedName( GETMAP_NAME ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( GETMAP_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        }

        // get GetFeatureInfo operation address
        if ( capabilities.getVersion().equals( "1.0.0" ) ) {
            Operation operation = om.getOperation( new QualifiedName( FEATUREINFO_NAME ) );
            
            if ( operation != null ) {
                dcps = operation.getDCP();
                for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
                addresses.put( FEATUREINFO_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
            }
        } else {
            Operation operation = om.getOperation( new QualifiedName( GETFEATUREINFO_NAME ) );

            if ( operation != null ) {
                dcps = operation.getDCP();
                for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
                addresses.put( GETFEATUREINFO_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
            }
        }

        // get GetLegendGraphic operation address
        Operation operation = om.getOperation( new QualifiedName( GETLEGENDGRAPHIC_NAME ) );

        if ( operation != null ) {
            dcps = operation.getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( GETLEGENDGRAPHIC_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        }

        // get GetStyles operation address
        operation = om.getOperation( new QualifiedName( GETSTYLES_NAME ) );

        if ( operation != null ) {
            dcps = operation.getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( GETSTYLES_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        }

        // get PutStyles operation address
        operation = om.getOperation( new QualifiedName( PUTSTYLES_NAME ) );

        if ( operation != null ) {
            dcps = operation.getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( PUTSTYLES_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        }

        // get DescribeLayer operation address
        operation = om.getOperation( new QualifiedName( DESCRIBELAYER_NAME ) );

        if ( operation != null ) {
            dcps = operation.getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            addresses.put( DESCRIBELAYER_NAME, http.getLinks().get( 0 ).getLinkage().getHref() );
        }

    }
    
    public OGCCapabilities getCapabilities() {
        return capabilities;
    }
    
    /**
     * the method performs the handling of the passed OGCWebServiceEvent directly 
     * and returns the result to the calling class/method
     *
     * @param request request (WMS, WCS, WFS, WCAS, WCTS, WTS, Gazetter) to perform
     *
     * @throws OGCWebServiceException 
     */
    public Object doService( OGCWebServiceRequest request ) 
                                    throws OGCWebServiceException {
        Object o = null;
        if ( request instanceof GetMap ) {            
            o = handleGetMap( (GetMap)request );       
            o = WMSProtocolFactory.createGetMapResponse( request, null, o );
        } else if ( request instanceof GetFeatureInfo ) {
            o = handleFeatureInfo( (GetFeatureInfo)request  );
            o = WMSProtocolFactory.createGetFeatureInfoResponse( request, null, (String)o );
        }
        /*
        else if ( request instanceof WMSGetCapabilities) {
            handleGetCapabilities( (WMSGetCapabilities)request, client );
        } else if ( request instanceof GetStyles ) {
            handleGetStyles( (GetStyles)request, client );
        } else if ( request instanceof PutStyles ) {
            handlePutStyles( (PutStyles)request, client );
        } else if ( request instanceof DescribeLayer ) {
            handleDescribeLayer( (DescribeLayer)request, client );
        } else if ( request instanceof GetLegendGraphic ) {
            handleGetLegendGraphic( (GetLegendGraphic)request, client );
        }
        */
        
        return o;
        
    }

    /**
     * performs a GetMap request against the remote service. The result contains
     * the map decoded in the desired format as a byte array.
     *
     * @param request GetMap request
     */
    protected Object handleGetMap( GetMap request ) throws OGCWebServiceException {
    
        URL url = null;

        if ( request.getVersion().equals( "1.0.0" ) ) {
            url = addresses.get( MAP_NAME );
        } else {
            url = addresses.get( GETMAP_NAME );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param;
        
        LOG.logDebug( "remote wms getmap", us );

        if ( capabilities.getVersion().compareTo( "1.0.0" ) <= 0 ) {
            us = StringTools.replace( us, "TRANSPARENCY", "TRANSPARENT", false );
            us = StringTools.replace( us, "GetMap", "map", false );
            us = StringTools.replace( us, "image/", "", false );
        }
 
        Object result = null;     
        try {
            URL ur = new URL( us );                    
            // get map from the remote service                
            NetWorker nw = new NetWorker( ur );
            InputStream is = nw.getInputStream();

            String contentType = nw.getContentType();
            String[] tmp = StringTools.toArray( contentType, ";", true );
            for (int i = 0; i < tmp.length; i++) {
                if ( tmp[i].indexOf( "image" ) > -1 ) {
                    contentType = tmp[i];
                    break;
                }
                contentType = tmp[0];
            }

            if ( MimeTypeMapper.isImageType( contentType ) && 
                 MimeTypeMapper.isKnownImageType( contentType ) ) {
                MemoryCacheSeekableStream mcss = new MemoryCacheSeekableStream( is );
                RenderedOp rop = JAI.create( "stream", mcss );
                result = rop.getAsBufferedImage();
                mcss.close();
            } else {
                // extract remote (error) message if the response
                // contains a known mime type
                String res = "";
                if ( MimeTypeMapper.isKnownMimeType( contentType ) ) {
                    res = "; remote message: ";
                    res += getInputStreamContent( is );
                }

                throw new OGCWebServiceException( "RemoteWMS:handleGetMap", "Response of the remote " + 
                                                  "WMS contains wrong content type: " + contentType + 
                                                  ";request: " + param + res );
            }
        } catch ( Exception e ) {
            throw new OGCWebServiceException( "RemoteWMS:handleGetMap", "Could not get map from RemoteWMS: " + 
                                              capabilities.getServiceIdentification().getTitle() + 
                                              "; " + "request: " + param + " " + e.toString() );
        }
        
        
        return result;
    }

    /**
     * reads feature infos from the remote WMS by performing a FeatureInfo
     * request against it. As long the result of a FeatureInfo request is generic
     * (for usual it is som HTML) it isn't easy to combine the result with that
     * of other WMS's
     *
     * @param request feature info request
     */
    protected Object handleFeatureInfo( GetFeatureInfo request ) throws OGCWebServiceException {
        

        URL url = null;

        if ( request.getVersion().equals( "1.0.0" ) ) {
            url = addresses.get( FEATUREINFO_NAME );
        } else {
            url = addresses.get( GETFEATUREINFO_NAME );
        }

        if ( url == null ) {
            throw new OGCWebServiceException( "GetFeatureInfo is not supported by " + 
                                              "the RemoteWMS: " + 
                                              capabilities.getServiceIdentification().getTitle() );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param ;

        String result = null;
        try {
            URL ur = new URL( us );
            // get map from the remote service                    
            NetWorker nw = new NetWorker( ur );
            byte[] b = nw.getDataAsByteArr( 20000 );
            String contentType = nw.getContentType();

            if ( contentType.toLowerCase().startsWith( "application/vnd.ogc.gml" ) ) {
                result = new String( b );
            } else {                
                throw new OGCWebServiceException( "RemoteWMS:handleFeatureInfo" );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new OGCWebServiceException( "RemoteWMS:handleFeatureInfo", 
                                                "Could not get map from RemoteWMS: " + 
                                                capabilities.getServiceIdentification().getTitle() + 
                                                "; request: " + param + "; " + e.toString() );
        }
        
        
        
        return result;
    }

    /**
     * reads the capabilities from the remote WMS by performing a GetCapabilities
     * request against it.
     *
     * @param request capabilities request
     */
    protected WMSCapabilities handleGetCapabilities( WMSGetCapabilities request) 
                                                          throws OGCWebServiceException {
        

        URL url = null;

        if ( request.getVersion().equals( "1.0.0" ) ) {
            url = addresses.get( CAPABILITIES_NAME );
        } else {
            url = addresses.get( GETCAPABILITIES_NAME );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param ;

        WMSCapabilities result = null;

        try {
            URL ur = new URL( us );
            // get map from the remote service                    
            NetWorker nw = new NetWorker( ur );
            byte[] b = nw.getDataAsByteArr( 20000 );
            String contentType = nw.getContentType();

            if ( MimeTypeMapper.isKnownMimeType( contentType ) ) {
                // create a WMSCapabilitiesTEMP instance from the result
                StringReader reader = new StringReader( new String( b ) );
                WMSCapabilitiesDocument doc = new WMSCapabilitiesDocument();
                doc.load(reader, XMLFragment.DEFAULT_URL );
                result = (WMSCapabilities)doc.parseCapabilities();
            } else {
                throw new OGCWebServiceException( "RemoteWMS:handleGetCapabilities", "Response of the " +
                                                  "remote WMS contains unknown content type: " + 
                                                  contentType + ";request: " + param );
            }
        } catch ( Exception e ) {
            throw new OGCWebServiceException( "RemoteWMS:handleGetCapabilities", 
                                                "Could not get map from RemoteWMS: " + 
                                                capabilities.getServiceIdentification().getTitle() + 
                                                "; request: " + param + "; " + e.toString() );
        }

        
        return result;
    }

    /**
     *
     *
     * @param request get styles request (WMS 1.1.1 - SLD)
     */
    protected Object handleGetStyles( GetStyles request ) throws OGCWebServiceException {

        URL url = addresses.get( GETSTYLES_NAME );

        if ( url == null ) {
            throw new OGCWebServiceException( "GetStyles is not supported by " + 
                                              "the RemoteWMS: " + 
                                               capabilities.getServiceIdentification().getTitle() );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param ;        
        
        // FIXME
        // TODO
        return null;
    }

    /**
     *
     *
     * @param request put styles request (WMS 1.1.1 - SLD)
     */
    protected Object handlePutStyles( PutStyles request ) throws OGCWebServiceException {

        URL url = addresses.get( PUTSTYLES_NAME );

        if ( url == null ) {
            throw new OGCWebServiceException( "PUTSTYLES is not supported by " + "the RemoteWMS: " + 
                                           capabilities.getServiceIdentification().getTitle() );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param ;

        // FIXME
        // TODO
        
        return null;
    }

    /**
     *
     *
     * @param request describe layer request (WMS 1.1.1 - SLD)
     */
    protected Object handleDescribeLayer( DescribeLayer request ) throws OGCWebServiceException {
        

        URL url = addresses.get( DESCRIBELAYER_NAME );

        if ( url == null ) {
            throw new OGCWebServiceException( "DESCRIBELAYER is not supported by " + 
                                              "the RemoteWMS: " + 
                                              capabilities.getServiceIdentification().getTitle() );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param ;

        // FIXME
        // TODO
        
        return null;
    }

    /**
     *
     *
     * @param request describe layer request (WMS 1.1.1 - SLD)
     */
    protected Object handleGetLegendGraphic( GetLegendGraphic request ) throws OGCWebServiceException {
        

        URL url = addresses.get( GETLEGENDGRAPHIC_NAME );

        if ( url == null ) {
            throw new OGCWebServiceException( "GETLEGENDGRAPHIC is not supported by " + 
                                           "the RemoteWMS: " + 
                                           capabilities.getServiceIdentification().getTitle() );
        }

        String param = request.getRequestParameter();
        String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param ;

        // FIXME
        // TODO
        
        return null;
    }
    
    /**
    *
    *
    * @param is 
    *
    * @return thr content as String
    *
    * @throws IOException 
    */
   protected String getInputStreamContent( InputStream is ) throws IOException {
       StringBuffer sb = new StringBuffer( 1000 );
       int c = 0;

       while ( ( c = is.read() ) >= 0 ) {
           sb.append( (char)c );
       }

       is.close();
       return sb.toString();
   }
   
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoteWMService.java,v $
Revision 1.24  2006/11/22 16:20:46  schmitz
Fixed checking for remote GetFeatureInfo content type.

Revision 1.23  2006/10/22 20:32:08  poth
support for vendor specific operation GetScaleBar removed

Revision 1.22  2006/10/17 20:31:17  poth
*** empty log message ***

Revision 1.21  2006/09/06 08:41:40  poth
code enhancment / removing references to deprecated class / use OWSUtil instead of mauelly creating a base URL

Revision 1.20  2006/08/23 07:10:21  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.19  2006/08/22 10:25:01  schmitz
Updated the WMS to use the new OWS common package.
Updated the rest of deegree to use the new data classes returned
by the updated WMS methods/capabilities.

Revision 1.18  2006/07/28 08:01:27  schmitz
Updated the WMS for 1.1.1 compliance.
Fixed some documentation.

Revision 1.17  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
