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
package org.deegree.ogcwebservices.wmps;

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
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.WMSGetCapabilities;
import org.deegree.ogcwebservices.wms.operation.WMSProtocolFactory;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.OperationsMetadata;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;

/**
 * An instance of the class acts as a wrapper to a remote WMS.
 * 
 * @version $Revision: 1.17 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class RemoteWMService implements OGCWebService {
    
    private static ILogger LOG = LoggerFactory.getLogger( RemoteWMService.class ); 

    protected HashMap<String, URL> addresses = null;

    protected WMSCapabilities capabilities = null;

    /**
     * Creates a new instance of RemoteWMService
     * 
     * @param capabilities
     */
    public RemoteWMService( WMSCapabilities capabilities ) {
        this.capabilities = capabilities;
        this.addresses = new HashMap<String, URL>();

        // get GetCapabilities operation address
        List<DCP> dcps = null;
        HTTP http = null;

        OperationsMetadata om = capabilities.getOperationMetadata();

        if ( capabilities.getVersion().equals( "1.0.0" ) ) {
            dcps = om.getOperation( new QualifiedName( "Capabilities" ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            this.addresses.put( "Capabilities", http.getLinks().get( 0 ).getLinkage().getHref() );
        } else {
            dcps = om.getOperation( new QualifiedName( "GetCapabilities" ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            this.addresses.put( "GetCapabilities", http.getLinks().get( 0 ).getLinkage().getHref() );
        }

        // get GetMap operation address
        if ( capabilities.getVersion().equals( "1.0.0" ) ) {
            // hui, is this the right name? Though it is all lowercase or 'Map'?
            dcps = om.getOperation( new QualifiedName( "MAP" ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            this.addresses.put( "MAP", http.getLinks().get( 0 ).getLinkage().getHref() );
        } else {
            dcps = om.getOperation( new QualifiedName( "GetMap" ) ).getDCP();
            for( DCP dcp : dcps ) if( dcp instanceof HTTP ) http = (HTTP) dcp;
            this.addresses.put("GetMap", http.getLinks().get( 0 ).getLinkage().getHref() );
        }

    }

    /**
     * Returns the OGCCapabilities
     * 
     * @return OGCCapabilities
     */
    public OGCCapabilities getCapabilities() {
        return this.capabilities;
    }

    /**
     * the method performs the handling of the passed OGCWebServiceEvent directly and returns the
     * result to the calling class/method
     * 
     * @param request
     *            request (WMS, WCS, WFS, WCAS, WCTS, WTS, Gazetter) to perform
     * @return Object
     * @throws OGCWebServiceException
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {
        
        Object object = null;
        if ( request instanceof GetMap ) {
            object = handleGetMap( (GetMap) request );
            object = WMSProtocolFactory.createGetMapResponse( request, null, object );
        }
        return object;

    }

    /**
     * performs a GetMap request against the remote service. The result contains the map decoded in
     * the desired format as a byte array.
     * 
     * @param request
     *            GetMap request
     * @return Object
     * @throws OGCWebServiceException
     */
    protected Object handleGetMap( GetMap request )
                            throws OGCWebServiceException {
        
        URL url = null;
        if ( request.getVersion().equals( "1.0.0" ) ) {
            url = this.addresses.get( "MAP" );
        } else {
            url = this.addresses.get( "GetMap" );
        }

        String param = request.getRequestParameter();
        String us = url.toExternalForm();
        if ( !us.endsWith( "?" ) ) {
            us = us + '?';
        }
        us = us + param;
        LOG.logDebug( "remote wms getmap", us );

        if ( this.capabilities.getVersion().compareTo( "1.0.0" ) <= 0 ) {
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
            for ( int i = 0; i < tmp.length; i++ ) {
                if ( tmp[i].indexOf( "image" ) > -1 ) {
                    contentType = tmp[i];
                    break;
                }
                contentType = tmp[0];
            }

            if ( MimeTypeMapper.isImageType( contentType )
                 && MimeTypeMapper.isKnownImageType( contentType ) ) {
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

                throw new OGCWebServiceException( "RemoteWMS:handleGetMap",
                                                  "Response of the remote " + "WMS " + "contains "
                                                                          + "wrong content type: "
                                                                          + contentType
                                                                          + ";request: " + param
                                                                          + res );
            }
        } catch ( Exception e ) {
            throw new OGCWebServiceException(
                                              "RemoteWMS:handleGetMap",
                                              "Could not get map from RemoteWMS: "
                                                                      + this.capabilities.getServiceIdentification().getTitle()
                                                                      + "; " + "request: " + param
                                                                      + " " + e.toString() );
        }

        return result;
    }

     /**
         * reads the capabilities from the remote WMS by performing a GetCapabilities request
         * against it.
         * 
         * @param request
         *            capabilities request
         * @return WMSCapabilities
         * @throws OGCWebServiceException
         */
    protected WMSCapabilities handleGetCapabilities( WMSGetCapabilities request )
                            throws OGCWebServiceException {
        
        URL url = null;

        if ( request.getVersion().equals( "1.0.0" ) ) {
            url = this.addresses.get( "capabilites" );
        } else {
            url = this.addresses.get( "GetCapabilities" );
        }

        String param = request.getRequestParameter();
        String us = url.toExternalForm();
        if ( !us.endsWith( "?" ) ) {
            us = us + '?';
        }
        us = us + param;

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
                doc.load( reader, XMLFragment.DEFAULT_URL );
                result = (WMSCapabilities) doc.parseCapabilities();
            } else {
                throw new OGCWebServiceException(
                                                  "RemoteWMS:handleGetCapabilities",
                                                  "Response of the "
                                                                          + "remote WMS "
                                                                          + "contains "
                                                                          + "unknown content type: "
                                                                          + contentType
                                                                          + ";request: " + param );
            }
        } catch ( Exception e ) {
            throw new OGCWebServiceException(
                                              "RemoteWMS:handleGetCapabilities",
                                              "Could not get map from RemoteWMS: "
                                                                      + this.capabilities.getServiceIdentification().getTitle()
                                                                      + "; request: " + param
                                                                      + "; " + e.toString() );
        }

        return result;
    }

    // /**
    // * Returns the legend graphics
    // *
    // * @param request
    // * describe layer request (WMS 1.1.1 - SLD)
    // * @return Object
    // * @throws OGCWebServiceException
    // */
    // protected Object handleGetLegendGraphic( GetLegendGraphic request )
    // throws OGCWebServiceException {
    //
    // URL url = addresses.get( WMPSConstants.GETLEGENDGRAPHIC );
    //
    // if ( url == null ) {
    // throw new OGCWebServiceException( "GETLEGENDGRAPHIC is not supported by "
    // + "the RemoteWMS: "
    // + capabilities.getServiceIdentification().getTitle() );
    // }
    //
    // String param = request.getRequestParameter();
    // String us = url.toExternalForm();
    // if ( !us.endsWith( "?" ) ) {
    // us = us + '?';
    // }
    // us = us + param;
    //        // FIXME
    //        return null;
    //    }

    /**
     * 
     * 
     * @param is
     * 
     * @return String
     * 
     * @throws IOException
     */
    protected String getInputStreamContent( InputStream is )
                            throws IOException {
        StringBuffer sb = new StringBuffer( 1000 );
        int c = 0;

        while ( ( c = is.read() ) >= 0 ) {
            sb.append( (char) c );
        }

        is.close();
        return sb.toString();
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: RemoteWMService.java,v $
 * Changes to this class. What the people have been up to: Revision 1.17  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.16  2006/10/22 20:36:05  poth
 * Changes to this class. What the people have been up to: operations not supported by WMPS removed
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.15  2006/08/23 07:10:22  schmitz
 * Changes to this class. What the people have been up to: Renamed the owscommon_neu package to owscommon_new.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.14  2006/08/22 10:25:01  schmitz
 * Changes to this class. What the people have been up to: Updated the WMS to use the new OWS common package.
 * Changes to this class. What the people have been up to: Updated the rest of deegree to use the new data classes returned
 * Changes to this class. What the people have been up to: by the updated WMS methods/capabilities.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.13  2006/08/10 07:11:35  deshmukh
 * Changes to this class. What the people have been up to: WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.12 2006/08/01 13:41:48 deshmukh Changes
 * to this class. What the people have been up to: The wmps configuration has been modified and
 * extended. Also fixed the javadoc. Changes to this class. What the people have been up to:
 * Revision 1.11 2006/07/12 14:46:16 poth comment footer added
 * 
 **************************************************************************************************/
