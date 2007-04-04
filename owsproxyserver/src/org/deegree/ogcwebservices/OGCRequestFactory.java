// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/OGCRequestFactory.java,v 1.12
// 2004/08/10 17:17:02 tf Exp $
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
package org.deegree.ogcwebservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueGetCapabilities;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueOperationsMetadata;
import org.deegree.ogcwebservices.csw.discovery.DescribeRecord;
import org.deegree.ogcwebservices.csw.discovery.GetRecordById;
import org.deegree.ogcwebservices.csw.discovery.GetRecords;
import org.deegree.ogcwebservices.csw.manager.Harvest;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.sos.capabilities.SOSGetCapabilities;
import org.deegree.ogcwebservices.sos.capabilities.SOSOperationsMetadata;
import org.deegree.ogcwebservices.sos.describeplatform.DescribePlatformRequest;
import org.deegree.ogcwebservices.sos.describesensor.DescribeSensorRequest;
import org.deegree.ogcwebservices.sos.getobservation.GetObservationRequest;
import org.deegree.ogcwebservices.wass.common.CloseSession;
import org.deegree.ogcwebservices.wass.common.GetSession;
import org.deegree.ogcwebservices.wass.was.operation.DescribeUser;
import org.deegree.ogcwebservices.wass.was.operation.WASGetCapabilities;
import org.deegree.ogcwebservices.wass.wss.operation.DoService;
import org.deegree.ogcwebservices.wass.wss.operation.WSSGetCapabilities;
import org.deegree.ogcwebservices.wcs.describecoverage.DescribeCoverage;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSGetCapabilities;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wfs.operation.DescribeFeatureType;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wmps.operation.PrintMap;
import org.deegree.ogcwebservices.wmps.operation.WMPSGetCapabilities;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.WMSGetCapabilities;
import org.deegree.ogcwebservices.wps.capabilities.WPSGetCapabilitiesRequest;
import org.deegree.ogcwebservices.wps.describeprocess.DescribeProcessRequest;
import org.deegree.ogcwebservices.wps.execute.ExecuteRequest;
import org.deegree.ogcwebservices.wpvs.operation.GetView;
import org.deegree.ogcwebservices.wpvs.operation.WPVSGetCapabilities;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Factory for generating request objects for OGC Web Services.
 * <p>
 * Requests may be generated from KVP or DOM representations. Also contains methods that decide
 * whether an incoming request representation is valid for a certain service.
 * </p>
 * Currently supported services are:
 * <ul>
 * <li>CSW</li>
 * <li>WFS</li>
 * <li>WCS</li>
 * <li>WMS</li>
 * <li>WFS-G</li>
 * <li>SOS</li>
 * <li>WMPS</li>
 * <li>WSS</li>
 * <li>WAS</li>
 * </ul>
 * 
 * @version $Revision: 1.87 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.87 $, $Date: 2006/11/29 16:19:10 $
 * 
 * @since 2.0
 */

public class OGCRequestFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( OGCRequestFactory.class );

    private static final String CSW_SERVICE_NAME = "CSW";

    private static final String WFS_SERVICE_NAME = "WFS";

    private static final String WCS_SERVICE_NAME = "WCS";

    private static final String WMS_SERVICE_NAME = "WMS";

    private static final String WFSG_SERVICE_NAME = "WFS-G";

    private static final String SOS_SERVICE_NAME = "SOS";

    private static final String WPVS_SERVICE_NAME = "WPVS";

    private static final String WMPS_SERVICE_NAME = "WMPS";

    private static final String WPS_SERVICE_NAME = "WPS";

    private static final String WSS_SERVICE_NAME = "WSS";

    private static final String WAS_SERVICE_NAME = "WAS";

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the content contained within the passed
     * request.
     * 
     * @param request
     * @return the request object
     * @throws IOException
     * @throws OGCWebServiceException
     * @throws XMLParsingException
     * @throws SAXException 
     */
    public static OGCWebServiceRequest create( ServletRequest request )
                            throws IOException, OGCWebServiceException, SAXException {
        
        // according to javax.servlet.* documentation, the type is correct
        Map<String, String[]> map = request.getParameterMap();
        Map<String, String> result = new HashMap<String, String>();
        for( String key : map.keySet() ) {
            result.put( key.toUpperCase(), StringTools.arrayToString( map.get( key ), ',' ) );
        }
        
        LOG.logDebug( "Request parameters: " + result );
        
        if ( map.size() != 0 ) {
            return createFromKVP( result );
        }
        
        XMLFragment fragment = null;
        if ( request.getContentType() != null ) {
            fragment = new XMLFragment( request.getReader(), XMLFragment.DEFAULT_URL );
        } else {        
            // DO NOT REMOVE THIS !!!!!
            // IT IS ABSOLUTLY NECESSARY TO ENSURE CORRECT CHARACTER ENCODING !!!
            StringReader sr = new StringReader( getRequestContent( (HttpServletRequest)request ) );
            fragment = new XMLFragment( sr, XMLFragment.DEFAULT_URL );
        }
        Document doc = fragment.getRootElement().getOwnerDocument();
        
        return createFromXML( doc );
    }
    
    /**
     * DO NOT REMOVE THIS !!!!!
     * IT IS ABSOLUTLY NECESSARY TO ENSURE CORRECT CHARACTER ENCODING !!!
     * @param request
     * @return
     * @throws IOException
     */
    private static String getRequestContent( HttpServletRequest request )
                            throws IOException {
        String method = request.getMethod();
        if ( method.equalsIgnoreCase( "POST" ) ) {
            String charset = request.getCharacterEncoding();
            if ( charset == null ) {
                charset = "UTF-8";
            }
            StringBuffer req = readPost( request, charset );
            if ( charset.equalsIgnoreCase( CharsetUtils.getSystemCharset() ) ) {
                return req.toString();
            }
            if ( charset.equalsIgnoreCase( "UTF-8" )
                 && !charset.equalsIgnoreCase( CharsetUtils.getSystemCharset() ) ) {
                String s = new String( req.toString().getBytes(), CharsetUtils.getSystemCharset() );
                return s;
            }
            if ( !charset.equalsIgnoreCase( "UTF-8" )
                 && !charset.equalsIgnoreCase( CharsetUtils.getSystemCharset() ) ) {
                String s = new String( req.toString().getBytes(), "UTF-8" );
                return s;
            } 
            return req.toString();            
        } 
        LOG.logDebug( "request string ", request.getQueryString() );
        return URLDecoder.decode( request.getQueryString(), CharsetUtils.getSystemCharset() );
        
    }
    
    /**
     * DO NOT REMOVE THIS !!!!!
     * IT IS ABSOLUTLY NECESSARY TO ENSURE CORRECT CHARACTER ENCODING !!!
     * @param request
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private static StringBuffer readPost( HttpServletRequest request, String charset )
                            throws UnsupportedEncodingException, IOException {
        java.io.Reader reader = new InputStreamReader( request.getInputStream(), charset );
        BufferedReader br = new BufferedReader( reader );
        StringBuffer req = new StringBuffer( 10000 );
        for ( String line = null; ( line = br.readLine() ) != null; ) {
            req.append( ( new StringBuilder( String.valueOf( line ) ) ).append( "\n" ).toString() );
        }

        br.close();
        return req;
    }


    /**
     * Creates an instance of an <code>AbstractOGCWebServiceRequest</code> from the passed DOM
     * object. Supported OWS are 'WMS', 'WFS', 'WCS' and 'CSW'. If a request for another service is
     * passed or a request that isn't supported by one of the listed services an exception will be
     * thrown. <BR>
     * Notice that not all listed services will support request processing by reading the request to
     * be performed from a DOM object. In this case also an exception will be thrown even if the
     * same request may can be performed if KVP is used.
     * 
     * @param doc
     * @return the request object
     * @throws OGCWebServiceException
     * @throws XMLParsingException
     */
    public static OGCWebServiceRequest createFromXML( Document doc )
                            throws OGCWebServiceException {
        
        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            XMLFragment xml = new XMLFragment();
            xml.setRootElement( doc.getDocumentElement() );
            LOG.logDebug( "XML request: ", xml.getAsString() );
        }

        String service = XMLTools.getAttrValue( doc.getDocumentElement(), "service" );
        String request = doc.getDocumentElement().getLocalName();
        service = getTargetService( service, request, doc );
        if ( "unknown".equals( service ) ) {
            throw new OGCWebServiceException("OGCRequestFactory", "Specified service '"
                                             + service + "' is not a known OGC service type." );
        }
        OGCWebServiceRequest ogcRequest = null;
        if ( request == null ) {
            throw new OGCWebServiceException( "Request parameter must be set!" );
        } else if ( WMS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWMSRequest( request, doc );
        } else if ( WFS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWFSRequest( request, doc );
        } else if ( WCS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWCSRequest( request, doc );
        } else if ( CSW_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getCSWRequest( request, doc );
        } else if ( WFSG_SERVICE_NAME.equals( service ) ) {
            // ogcRequest = getWFSGRequest( request, doc );
        } else if ( SOS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getSOSRequest( request, doc );
        } else if ( WPVS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getSOSRequest( request, doc );
        } else if ( WMPS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWMPSRequest( request, doc );
        } else if ( WPS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWPSRequest( request, doc );
        } else if ( WSS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWSSRequest( request, doc );
        } else if ( WAS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWASRequest( request, doc );
        } else {
            throw new OGCWebServiceException( "OGCRequestFactory", "No handler for service "
                                                                   + service
                                                                   + " in OGCRequestFactory." );
        }
        return ogcRequest;
    }

    /**
     * Creates an instance of an <code>AbstractOGCWebServiceRequest</code> from the passed KVP
     * encoded request. Supported OWS are 'WMS', 'WFS', 'WCS' and 'CSW'. If a request for another
     * service is passed or a request that isn't supported by one of the listed services an
     * exception will be thrown. <BR>
     * Notice that not all listed services will support request processing by reading the request to
     * be performed from KVPs. In this case also an exception will be thrown even if the same
     * request may be performed if a DOM object is used.
     * 
     * @param map
     * @return the request object
     * @throws OGCWebServiceException
     */
    public static OGCWebServiceRequest createFromKVP( Map<String, String> map )
                            throws OGCWebServiceException {

        LOG.logDebug( "KVP request: ", map );
        // request parameter given?
        String request = map.get( "REQUEST" );
        if ( request == null ) {
            LOG.logInfo( "parameter: ", map );
            throw new InvalidParameterValueException( "OGCRequestFactory",
                                                      "Request parameter must be set." );
        }
        // service parameter given?
        String service = map.get( "SERVICE" );
        if ( service == null ) {
            // a profile of a service will be treated as a service
            // (e.g. WFS-G)
            service = map.get( "PROFILE" );
            if ( service == null ) {
                service = getTargetService( service, request, null );
            }
        }

        OGCWebServiceRequest ogcRequest = null;
        if ( WMS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWMSRequest( request, map );
        } else if ( WFS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWFSRequest( request, map );
        } else if ( WCS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWCSRequest( request, map );
        } else if ( CSW_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getCSWRequest( request, map );
        } else if ( WFSG_SERVICE_NAME.equals( service ) ) {
            // ogcRequest = getWFSGRequest( request, map );
        } else if ( SOS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getSOSRequest( request, map );
        } else if ( WPVS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWPVSRequest( request, map );
        } else if ( WMPS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWMPSRequest( request, map );
        } else if ( WPS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWPSRequest( request, map );
        } else if ( WSS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWSSRequest( request, map );
        } else if ( WAS_SERVICE_NAME.equals( service ) ) {
            ogcRequest = getWASRequest( request, map );
        } else {
            throw new OGCWebServiceException( "OGCRequestFactory", "Specified service '" + 
                                              map.get( "SERVICE" ) + 
                                              "' is not a known OGC service type." );
        }
        return ogcRequest;
    }

    /**
     * Creates the corresponding WFS request object from the given parameters.
     * 
     * @param request
     * @param map
     * @return the corresponding WFS request object
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWFSRequest( String request, Map<String, String> map )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        map.put( "ID", "" + IDGenerator.getInstance().generateUniqueID() );
        if ( request.equals( "GetCapabilities" ) ) {
            ogcRequest = WFSGetCapabilities.create( map );
        } else if ( request.equals( "GetFeature" ) ) {
            ogcRequest = GetFeature.create( map );
        } else if ( request.equals( "GetFeatureWithLock" ) ) {
            throw new OGCWebServiceException( "'GetFeatureWithLock' operation is not implemented. " );
        } else if ( request.equals( "GetGmlObject" ) ) {
            throw new OGCWebServiceException( "'GetGmlObject' operation is not implemented. " );
        } else if ( request.equals( "LockFeature" ) ) {
            throw new OGCWebServiceException( "'LockFeature' operation is not implemented. " );
        } else if ( request.equals( "DescribeFeatureType" ) ) {
            ogcRequest = DescribeFeatureType.create( map );
        } else if ( request.equals( "Transaction" ) ) {
            ogcRequest = Transaction.create( map );
        } else if ( request.equals( "Transaction" ) ) {
            ogcRequest = Transaction.create( map );
        } else {
            throw new InvalidParameterValueException( "Unknown WFS request type: '" + request
                                                      + "'." );
        }
        return ogcRequest;
    }

    /**
     * @param request
     * @param map
     * @return
     * @todo GetFeature request not completely implemented, parameter missing
     */
    // private static OGCWebServiceRequest getWFSGRequest( String request, Map map )
    // throws OGCWebServiceException {
    // OGCWebServiceRequest ogcRequest = null;
    // map.put( "ID", ""
    // + IDGenerator.getInstance().generateUniqueID() );
    // if ( request.equals( "GetCapabilities" ) ) {
    // ogcRequest =
    // WFSGOperationFactory.createWFSGGetCapabilitiesRequest( (String) map.get( "ID" ) );
    // } else if ( request.equals( "GetFeature" ) ) {
    // ogcRequest = WFSGOperationFactory.createWFSGGetFeatureRequest( "ID", map );
    // } else {
    // throw new InvalidParameterValueException( "Unknown WFS request type: '"
    // + request + "'." );
    // }
    // return ogcRequest;
    // }
    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Document</code>. The
     * returned request will be a WFS request. The type of request is determined by also submitted
     * request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>GetFeature</li>
     * <li>DescribeFeatureType</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param doc
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWFSRequest( String request, Document doc )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        if ( request.equals( "GetCapabilities" ) ) {
            ogcRequest = WFSGetCapabilities.create( id, doc.getDocumentElement() );
        } else if ( request.equals( "GetFeature" ) ) {
            ogcRequest = GetFeature.create( id, doc.getDocumentElement() );
        } else if ( request.equals( "DescribeFeatureType" ) ) {
            ogcRequest = DescribeFeatureType.create( id, doc.getDocumentElement() );
        } else if ( request.equals( "Transaction" ) ) {
            ogcRequest = Transaction.create( id, doc.getDocumentElement() );
        } else {
            throw new OGCWebServiceException( "Unknown / unimplemented WFS request type: '"
                                              + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Document</code>. The
     * returned request will be a WSS request. The type of request is determined by also submitted
     * request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>GetSession</li>
     * <li>CloseSession</li>
     * <li>DoService</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param doc
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWSSRequest( String request, Document doc )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        if ( ( "GetCapabilities" ).equals( request ) ) {
            ogcRequest = WSSGetCapabilities.create( id, doc.getDocumentElement() );
        } else if ( ( "GetSession" ).equals( request ) ) {
            ogcRequest = GetSession.create( id, doc.getDocumentElement() );
        } else if ( ( "CloseSession" ).equals( request ) ) {
            ogcRequest = CloseSession.create( id, doc.getDocumentElement() );
        } else if ( ( "DoService" ).equals( request ) ) {
            ogcRequest = DoService.create( id, doc.getDocumentElement() );
        } else {
            throw new OGCWebServiceException( "Unknown / unimplemented WSS request type: '"
                                              + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>key value pair</code>.
     * The returned request will be a WSS request. The type of request is determined by also
     * submitted request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>GetSession</li>
     * <li>CloseSession</li>
     * <li>DoService</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param kvp
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWSSRequest( String request, Map<String, String> kvp )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        if ( ( "GetCapabilities" ).equals( request ) ) {
            ogcRequest = WSSGetCapabilities.create( id, kvp );
        } else if ( ( "GetSession" ).equals( request ) ) {
            ogcRequest = GetSession.create( id, kvp );
        } else if ( ( "CloseSession" ).equals( request ) ) {
            ogcRequest = CloseSession.create( id, kvp );
        } else if ( ( "DoService" ).equals( request ) ) {
            ogcRequest = DoService.create( id, kvp );
        } else {
            throw new OGCWebServiceException( "Unknown / unimplemented WSS request type: '"
                                              + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Document</code>. The
     * returned request will be a WAS request. The type of request is determined by also submitted
     * request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>GetSession</li>
     * <li>CloseSession</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param doc
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWASRequest( String request, Document doc )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        // note: DescribeUser is only supported through KVP
        if ( ( "GetCapabilities" ).equals( request ) ) {
            ogcRequest = WASGetCapabilities.create( id, doc.getDocumentElement() );
        } else if ( ( "GetSession" ).equals( request ) ) {
            ogcRequest = GetSession.create( id, doc.getDocumentElement() );
        } else if ( ( "CloseSession" ).equals( request ) ) {
            ogcRequest = CloseSession.create( id, doc.getDocumentElement() );
        } else {
            throw new OGCWebServiceException( "Unknown / unimplemented WAS request type: '"
                                              + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>key value pair</code>.
     * The returned request will be a WAS request. The type of request is determined by also
     * submitted request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>GetSession</li>
     * <li>CloseSession</li>
     * <li>DescribeUser</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param kvp
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWASRequest( String request, Map<String, String> kvp )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        if ( ( "GetCapabilities" ).equals( request ) ) {
            ogcRequest = WASGetCapabilities.create( id, kvp );
        } else if ( ( "GetSession" ).equals( request ) ) {
            ogcRequest = GetSession.create( id, kvp );
        } else if ( ( "CloseSession" ).equals( request ) ) {
            ogcRequest = CloseSession.create( id, kvp );
        } else if ( ( "DescribeUser" ).equals( request ) ) {
            ogcRequest = new DescribeUser( id, kvp );
        } else {
            throw new OGCWebServiceException( "Unknown / unimplemented WAS request type: '"
                                              + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * @param request
     * @param doc
     * @return
     */
    // private static OGCWebServiceRequest getWFSGRequest( String request, Document doc )
    // throws OGCWebServiceException {
    //
    // try {
    // return WFSGOperationFactory.createRequest( "id", doc );
    // } catch (GazetteerException e) {
    // throw new OGCWebServiceException( "could not create WFS-G GetFeature request", e
    // .getMessage() );
    // } catch (IOException e) {
    // throw new OGCWebServiceException( "-", e.getMessage() );
    // } catch (SAXException e) {
    // throw new OGCWebServiceException( "could not parse WFS-G GetFeature request", e
    // .getMessage() );
    // }
    // }
    /**
     * return the type of service the passed request targets
     * 
     * @param service
     * @param request
     * @param doc
     * @return
     */
    public static String getTargetService( String service, String request, Document doc ) {

        if ( WMS_SERVICE_NAME.equals( service ) || isWMSRequest( request ) ) {
            return WMS_SERVICE_NAME;
        } else if ( WFSG_SERVICE_NAME.equals( service ) || isWFSGRequest( doc ) ) {
            return WFSG_SERVICE_NAME;
        } else if ( WFS_SERVICE_NAME.equals( service ) || isWFSRequest( request, doc ) ) {
            return WFS_SERVICE_NAME;
        } else if ( WCS_SERVICE_NAME.equals( service ) || isWCSRequest( request ) ) {
            return WCS_SERVICE_NAME;
        } else if ( CSW_SERVICE_NAME.equals( service ) || isCSWRequest( request, doc ) ) {
            return CSW_SERVICE_NAME;
        } else if ( SOS_SERVICE_NAME.equals( service ) || isSOSRequest( request ) ) {
            return SOS_SERVICE_NAME;
        } else if ( WPVS_SERVICE_NAME.equals( service ) || isWPVSRequest( request ) ) {
            return WPVS_SERVICE_NAME;
        } else if ( WMPS_SERVICE_NAME.equals( service ) || isWMPSRequest( request ) ) {
            return WMPS_SERVICE_NAME;
        } else if ( WPS_SERVICE_NAME.equals( service ) || isWPSRequest( request ) ) {
            return WPS_SERVICE_NAME;
        } else if ( WAS_SERVICE_NAME.equals( service ) || isWASRequest( request ) ) {
            return WAS_SERVICE_NAME;
        } else if ( WSS_SERVICE_NAME.equals( service ) || isWSSRequest( request ) ) {
            return WSS_SERVICE_NAME;
        } else {
            return "unknown";
        }

    }

    /**
     * returns true if the request is a WMPS request
     * 
     * @param request
     *            name, e.g. 'GetCapabilities' name, e.g. 'PrintMap'
     * @return
     */
    private static boolean isWMPSRequest( String request ) {
        if ( "GetCapabilities".equals( request ) || ( "PrintMap".equals( request ) ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WMS request
     * 
     * @param request
     * @return
     */
    private static boolean isWMSRequest( String request ) {
        if ( "GetMap".equals( request ) || "map".equals( request )
             || "GetFeatureInfo".equals( request ) || "feature_info".equals( request )
             || "GetLegendGraphic".equals( request ) || "GetStyles".equals( request )
             || "PutStyles".equals( request ) || "DescribeLayer".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WFS request
     * 
     * @param request
     * @param doc
     * @return
     */
    private static boolean isWFSRequest( String request, Document doc ) {
        if ( doc != null ) {
            String s = doc.getDocumentElement().getNamespaceURI();
            if ( CommonNamespaces.WFSNS.toString().equals( s ) ) {
                return true;
            }
        }
        if ( "GetFeature".equals( request ) || "GetFeatureWithLock".equals( request )
             || "DescribeFeatureType".equals( request ) || "Lock".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WFS request
     * 
     * @param doc
     * @param request
     * @return
     */
    private static boolean isWFSGRequest( Document doc ) {
        if ( doc != null ) {
            String s = doc.getDocumentElement().getNamespaceURI();
            if ( CommonNamespaces.WFSGNS.toString().equals( s ) )
                return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WCS request
     * 
     * @param request
     * @return
     */
    private static boolean isWCSRequest( String request ) {
        if ( "GetCoverage".equals( request ) || "DescribeCoverage".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a CSW request
     * 
     * @param request
     * @return
     */
    private static boolean isCSWRequest( String request, Document doc ) {
        if ( doc != null ) {
            String s = doc.getDocumentElement().getNamespaceURI();
            if ( CommonNamespaces.CSWNS.toString().equals( s ) ) {
                return true;
            }
        }
        if ( CatalogueOperationsMetadata.GET_RECORDS_NAME.equals( request )
             || CatalogueOperationsMetadata.DESCRIBE_RECORD_NAME.equals( request )
             || CatalogueOperationsMetadata.GET_RECORD_BY_ID_NAME.equals( request )
             || CatalogueOperationsMetadata.GET_DOMAIN_NAME.equals( request )
             || CatalogueOperationsMetadata.HARVEST_NAME.equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a SOS request
     * 
     * @param request
     * @return
     */
    private static boolean isSOSRequest( String request ) {
        if ( "GetObservation".equals( request ) || "DescribeSensor".equals( request )
             || "DescribePlatform".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WPVS request
     * 
     * @param request
     *            name, e.g. 'GetView'
     * @return
     */
    private static boolean isWPVSRequest( String request ) {
        if ( "GetView".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WPS request
     * 
     * @param request
     *            name, e.g. 'GetCapabilities' name, e.g. 'DescribeProcess', e.g. 'Exceute'
     * @return
     */
    private static boolean isWPSRequest( String request ) {
        if ( "DescribeProcess".equals( request ) || "Execute".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WAS request
     * 
     * @param request
     *            name, e.g. 'GetSession' name, e.g. 'CloseSession', e.g. 'GetSAMLResponse'
     * @return true if and only if the request contains one of the above Strings
     */
    private static boolean isWASRequest( String request ) {
        if ( "GetSession".equals( request ) || "CloseSession".equals( request )
             || "GetSAMLResponse".equals( request ) || "DescribeUser".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * returns true if the request is a WSS request
     * 
     * @param request
     *            name, e.g. 'GetSession' name, e.g. 'CloseSession', e.g. 'GetSAMLResponse'
     * @return true if and only if the request contains one of the above Strins
     */
    private static boolean isWSSRequest( String request ) {
        if ( "GetSession".equals( request ) || "CloseSession".equals( request )
             || "DoService".equals( request ) ) {
            return true;
        }
        return false;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Object</code>. the
     * returned request will be a WCS request. The type of request is determind by the the also
     * passed 'request' parameter. Possible requests are:
     * <ul>
     * <li>GetCapabilities
     * <li>GetCoverage
     * <li>DescribeCoverage
     * </ul>
     * <p>
     * Any other request passed to the method causes an exception to be thrown.
     * </p>
     * 
     * @param request
     * @param req
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWMSRequest( String request, Object req )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        try {
            if ( request.equals( "GetCapabilities" ) || "capabilities".equals( request ) ) {
                if ( req instanceof Map ) {
                    Map<String, String> map = (Map) req;
                    map.put( "ID", id );
                    // defaulting to 1.1.1 is not possible because of spec requirements
//                    if( ( map.get( "VERSION" ) == null ) && ( map.get( "WMTVER" ) == null ) )
//                    {
//                        map.put( "VERSION", "1.3.0" );
//                    }
                    ogcRequest = WMSGetCapabilities.create( map );
//                } else {
                    // ogcRequest = WMSGetCapabilities.create(id, (Document) req);
                }
            } else if ( request.equals( "GetMap" ) || request.equals( "map" ) ) {
                if ( req instanceof Map ) {
                    ( (Map) req ).put( "ID", id );
                    ogcRequest = GetMap.create( (Map) req );
                } else {
                    ogcRequest = GetMap.create( id, (Document) req );
                }
            } else if ( request.equals( "GetFeatureInfo" ) || request.equals( "feature_info" ) ) {
                if ( req instanceof Map ) {
                    ( (Map) req ).put( "ID", id );
                    ogcRequest = GetFeatureInfo.create( (Map) req );
//                } else {
                    // ogcRequest = GetFeatureInfo.create( id, (Document) req);
                }
            } else if ( request.equals( "GetLegendGraphic" ) ) {
                if ( req instanceof Map ) {
                    ( (Map) req ).put( "ID", id );
                    ogcRequest = GetLegendGraphic.create( (Map) req );
//                } else {
                    // ogcRequest = GetLegendGraphic.create( id, (Document) req);
                }
            } else {
                throw new OGCWebServiceException( "Unknown WMS request type: '" + request + "'." );
            }
        } catch ( MalformedURLException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        } 
        return ogcRequest;
    }

    /**
     * Creates an <code>AbstractOGCWebServiceRequest</code> from the passed <code>Object</code>.
     * the returned request will be a WCS request. The type of request is determind by the the also
     * passed 'request' parameter. Possible requests are:
     * <ul>
     * <li>GetCapabilities
     * <li>GetCoverage
     * <li>DescribeCoverage
     * </ul>
     * Any other request passed to the method causes an exception to be thrown.
     * 
     * @param request
     * @param req
     * @return
     * @throws OGCWebServiceException
     */
    private static AbstractOGCWebServiceRequest getWCSRequest( String request, Object req )
                            throws OGCWebServiceException {
        AbstractOGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        if ( request.equals( "GetCapabilities" ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = WCSGetCapabilities.create( (Map) req );
            } else {
                ogcRequest = WCSGetCapabilities.create( id, (Document) req );
            }
        } else if ( request.equals( "GetCoverage" ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = GetCoverage.create( (Map) req );
            } else {
                ogcRequest = GetCoverage.create( id, (Document) req );
            }
        } else if ( request.equals( "DescribeCoverage" ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = DescribeCoverage.create( (Map) req );
            } else {
                ogcRequest = DescribeCoverage.create( id, (Document) req );
            }
        } else {
            throw new OGCWebServiceException( "Unknown WCS request type: '" + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>AbstractOGCWebServiceRequest</code> from the passed <code>Object</code>.
     * The returned request will be a <code>CSW</code> request. The type of request is determined
     * by the the also passed 'request' parameter. Allowed values for the request parameter are:
     * <ul>
     * <li>GetCapabilities
     * <li>GetRecords
     * <li>DescribeRecord
     * </ul>
     * 
     * Any other request passed to the method causes an exception to be thrown.
     * 
     * TODO: Add missing request types.
     * 
     * @param request
     * @param req
     * @return
     * @throws OGCWebServiceException
     */
    private static AbstractOGCWebServiceRequest getCSWRequest( String request, Object req )
                            throws OGCWebServiceException {
        AbstractOGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        LOG.logDebug( StringTools.concat( 200, "Creating CSW request '", request, "' with ID=", id,
                                          "/type:", req.getClass().getName() ) );

        if ( OperationsMetadata.GET_CAPABILITIES_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = CatalogueGetCapabilities.create( (Map) req );
            } else {
                ogcRequest = CatalogueGetCapabilities.create(
                                                              id,
                                                              ( (Document) req ).getDocumentElement() );
            }
        } else if ( CatalogueOperationsMetadata.GET_RECORDS_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = GetRecords.create( (Map) req );
            } else {
                ogcRequest = GetRecords.create( id, ( (Document) req ).getDocumentElement() );
            }
        } else if ( CatalogueOperationsMetadata.GET_RECORD_BY_ID_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ogcRequest = GetRecordById.create( (Map) req );
            } else {
                ogcRequest = GetRecordById.create( id, ( (Document) req ).getDocumentElement() );
            }
        } else if ( CatalogueOperationsMetadata.DESCRIBE_RECORD_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = DescribeRecord.create( (Map) req );
            } else {
                ogcRequest = DescribeRecord.create( id, ( (Document) req ).getDocumentElement() );
            }
        } else if ( CatalogueOperationsMetadata.GET_DOMAIN_NAME.equals( request ) ) {
            // TODO
            throw new OGCWebServiceException( CatalogueOperationsMetadata.TRANSACTION_NAME
                                              + " is not supported." );
        } else if ( CatalogueOperationsMetadata.TRANSACTION_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                throw new OGCWebServiceException( CatalogueOperationsMetadata.TRANSACTION_NAME
                                                  + " through HTTP Get is not supported." );
            }
            ogcRequest = org.deegree.ogcwebservices.csw.manager.Transaction.create(
                                                                                    id,
                                                                                    ( (Document) req ).getDocumentElement() );
        } else if ( CatalogueOperationsMetadata.HARVEST_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = Harvest.create( (Map) req );
            } else {
                throw new OGCWebServiceException( CatalogueOperationsMetadata.HARVEST_NAME
                                                  + " through HTTP post is not supported." );
            }
        } else {
            throw new OGCWebServiceException( "Unknown CSW request type: '" + request + "'." );
        }
        LOG.logDebug( "CSW request created: " + ogcRequest );
        return ogcRequest;
    }

    /**
     * 
     * @param request
     * @param req
     * @return
     * @throws OGCWebServiceException
     */
    private static AbstractOGCWebServiceRequest getSOSRequest( String request, Object req )
                            throws OGCWebServiceException {

        AbstractOGCWebServiceRequest ogcRequest = null;

        String id = "" + IDGenerator.getInstance().generateUniqueID();

        LOG.logDebug( "Creating SOS request '" + request + "' with ID=" + id + "/type:"
                      + req.getClass().getName() );

        if ( req instanceof Map ) {
            ( (Map) req ).put( "ID", id );
        }

        if ( OperationsMetadata.GET_CAPABILITIES_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ogcRequest = SOSGetCapabilities.create( (Map) req );
            } else {
                ogcRequest = SOSGetCapabilities.create( id, (Document) req );
            }

        } else if ( SOSOperationsMetadata.DESCRIBE_PLATFORM_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ogcRequest = DescribePlatformRequest.create( (Map) req );
            } else {
                ogcRequest = DescribePlatformRequest.create( id, (Document) req );
            }

        } else if ( SOSOperationsMetadata.DESCRIBE_SENSOR_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ogcRequest = DescribeSensorRequest.create( (Map) req );
            } else {
                ogcRequest = DescribeSensorRequest.create( id, (Document) req );
            }
        } else if ( SOSOperationsMetadata.GET_OBSERVATION_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ogcRequest = GetObservationRequest.create( (Map) req );
            } else {
                ogcRequest = GetObservationRequest.create( id, (Document) req );
            }
        } else {
            throw new OGCWebServiceException( "Unknown SCS request type: '" + request + "'." );
        }
        LOG.logDebug( "SCS request created: " + ogcRequest );
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Object</code>. the
     * returned request will be a WPVS request. The type of request is determind by the the also
     * passed 'request' parameter. Possible requests are:
     * <ul>
     * <li>GetCapabilities
     * <li>GetView
     * </ul>
     * <p>
     * Any other request passed to the method causes an exception to be thrown.
     * </p>
     * 
     * @param request
     *            name
     * @param req
     *            the request object
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWPVSRequest( String request, Object req )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();

        if ( OperationsMetadata.GET_CAPABILITIES_NAME.equals( request ) ) {
            if ( req instanceof Map ) {
                ( (Map) req ).put( "ID", id );
                ogcRequest = WPVSGetCapabilities.create( (Map) req );
            } else {
                // ogcRequest = WPVSGetCapabilities.create(id, (Document) req);
            }
        } else if ( "GetView".equals( request ) ) {
            ogcRequest = GetView.create( (Map) req );

        } else {
            throw new OGCWebServiceException( "Unknown WPVS request type: '" + request + "'." );
        }

        return ogcRequest;
    }

    /**
     * Creates an <code>AbstractOGCWebServiceRequest</code> from the passed <code>Object</code>.
     * the returned request will be a WMPS request. The type of request is determind by the the also
     * passed 'request' parameter. Possible requests are:
     * <ul>
     * <li>GetCapabilities
     * </ul>
     * Any other request passed to the method causes an exception to be thrown.
     * 
     * @param request
     * @param doc
     * @param req
     * @return
     * @throws OGCWebServiceException
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWMPSRequest( String request, Document doc )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;

        if ( request.equals( "PrintMap" ) ) {
            try {
                ogcRequest = PrintMap.create( doc.getDocumentElement() );
            } catch ( Exception e ) {
                throw new OGCWebServiceException(
                                                  "Error creating a Print Map object for the request '"
                                                                          + request + "'. "
                                                                          + e.getMessage() );
            }
        } else {
            throw new OGCWebServiceException( "Unknown / unimplemented WMPS request type: '"
                                              + request + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>AbstractOGCWebServiceRequest</code> from the passed <code>Object</code>.
     * the returned request will be a WMPS request. The type of request is determind by the the also
     * passed 'request' parameter. Possible requests are:
     * <ul>
     * <li>GetCapabilities
     * </ul>
     * Any other request passed to the method causes an exception to be thrown.
     * 
     * @param request
     * @param map
     * @param req
     * @return OGCWebServiceRequest
     * @throws InconsistentRequestException
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWMPSRequest( String request, Map<String, String> map )
                            throws InconsistentRequestException, InvalidParameterValueException {
        OGCWebServiceRequest ogcRequest = null;
        map.put( "ID", "" + IDGenerator.getInstance().generateUniqueID() );
        if ( request.equals( "GetCapabilities" ) ) {
            ogcRequest = WMPSGetCapabilities.create( map );
        } else if ( request.equals( "PrintMap" ) ) {
            ogcRequest = PrintMap.create( map );
        } else {
            throw new InvalidParameterValueException( "Unknown WMPS request type: '" + request
                                                      + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Map</code>. The
     * returned request will be a WPS request. The type of request is determined by also submitted
     * request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>DescribeProcess</li>
     * <li>Execute</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param map
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWPSRequest( String request, Map<String, String> map )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        map.put( "ID", "" + IDGenerator.getInstance().generateUniqueID() );
        if ( "GetCapabilities".equals( request ) ) {
            ogcRequest = WPSGetCapabilitiesRequest.create( map );
        } else if ( "DescribeProcess".equals( request ) ) {
            ogcRequest = DescribeProcessRequest.create( map );
        } else if ( "Execute".equals( request ) ) {
            ogcRequest = ExecuteRequest.create( map );
        } else {
            throw new InvalidParameterValueException( "Unknown WPS request type: '" + request
                                                      + "'." );
        }
        return ogcRequest;
    }

    /**
     * Creates an <code>OGCWebServiceRequest</code> from the passed <code>Document</code>. The
     * returned request will be a WPS request. The type of request is determined by also submitted
     * request name. Known requests are:
     * <ul>
     * <li>GetCapabilities</li>
     * <li>DescribeProcess</li>
     * <li>Execute</li>
     * </ul>
     * <p>
     * Any other request passed to the method causes an <code>OGCWebServiceException</code> to be
     * thrown.
     * </p>
     * 
     * @param request
     * @param doc
     * @return created <code>OGCWebServiceRequest</code>
     * @throws OGCWebServiceException
     */
    private static OGCWebServiceRequest getWPSRequest( String request, Document doc )
                            throws OGCWebServiceException {
        OGCWebServiceRequest ogcRequest = null;
        String id = "" + IDGenerator.getInstance().generateUniqueID();
        if ( "GetCapabilities".equals( request ) ) {
            ogcRequest = WPSGetCapabilitiesRequest.create( id, doc.getDocumentElement() );
        } else if ( "DescribeProcess".equals( request ) ) {
            ogcRequest = DescribeProcessRequest.create( id, doc.getDocumentElement() );

        } else if ( "Execute".equals( request ) ) {
            ogcRequest = ExecuteRequest.create( id, doc.getDocumentElement() );

        } else {
            throw new OGCWebServiceException( "Unknown WPS request type: '" + request + "'." );
        }
        return ogcRequest;
    }

}
/***************************************************************************************************
 * OGCRequestFactory.java Changes to this class. What the people have been up to: $Log:
 * OGCRequestFactory.java,v $ Revision 1.61 2006/06/07 12:37:51 deshmukh Reset the changes made to
 * the WMPS.
 * 
 * Revision 1.59 2006/05/29 16:19:41 poth bug fix - creatung GetRecordById request from
 * key-value-pairs
 * 
 * Revision 1.58 2006/05/16 16:20:25 mschneider Refactored due to the splitting of
 * org.deegree.ogcwebservices.wfs.operation package.
 * 
 * Revision 1.57 2006/05/16 13:47:59 poth code formatation
 * 
 * Revision 1.56 2006/04/23 20:17:26 poth *** empty log message ***
 * 
 * Revision 1.55 2006/04/09 12:20:40 poth *** empty log message ***
 * 
 * Revision 1.54 2006/04/06 20:25:27 poth *** empty log message ***
 * 
 * Revision 1.53 2006/04/04 20:39:42 poth *** empty log message ***
 * 
 * Revision 1.52 2006/04/04 10:22:02 poth *** empty log message ***
 * 
 * Revision 1.51 2006/03/30 21:20:26 poth *** empty log message ***
 * 
 * Revision 1.50 2006/03/27 07:12:12 mschneider Added exception to inform the client that GetFeature
 * operations using KVP are currently unavailable.
 * 
 * Revision 1.49 2006/02/26 21:30:42 poth *** empty log message ***
 * 
 * Revision 1.48 2006/02/20 14:14:00 poth *** empty log message ***
 * 
 * Revision 1.47 2006/02/06 16:36:41 deshmukh *** empty log message *** Revision 1.46 2006/02/01
 * 17:38:44 deshmukh *** empty log message ***
 * 
 * Revision 1.45 2006/01/06 16:06:34 deshmukh New Service WMPS handling PrintMap requests Revision
 * 1.44 2006/01/05 15:18:27 deshmukh Renamed WPS to WMPS
 * 
 * Revision 1.43 2006/01/05 12:57:11 deshmukh New Service WMPS added Revision 1.42 2005/12/15
 * 15:46:44 taddei added WPVS GetView
 * 
 * Revision 1.41 2005/12/13 14:50:03 taddei added wpvs handler
 * 
 * Revision 1.40 2005/12/05 09:25:17 deshmukh *** empty log message *** Revision 1.39 2005/11/23
 * 17:07:07 mschneider Fixed NS.equals() call.
 * 
 * Revision 1.38 2005/11/23 14:13:51 deshmukh *** empty log message *** Revision 1.37 2005/11/21
 * 15:02:16 deshmukh Transaction methods added Changes to this class. What the people have been up
 * to: Revision 1.36 2005/11/17 13:09:35 deshmukh Changes to this class. What the people have been
 * up to: changes in Exception catching Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.34.2.2 2005/11/16 07:25:19
 * deshmukh Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.34.2.1 2005/11/15 14:36:19 deshmukh Changes to this class. What the
 * people have been up to: QualifiedName modifications Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.34 2005/09/09
 * 19:14:17 poth Changes to this class. What the people have been up to: no message Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.33 2005/09/09 08:20:03 poth Changes to this class. What the people have been up to: no
 * message ======= Changes to this class. What the people have been up to: $Log:
 * OGCRequestFactory.java,v $ What the people have been up to: Revision 1.39 2005/11/23 17:07:07
 * mschneider What the people have been up to: Fixed NS.equals() call. What the people have been up
 * to: What the people have been up to: Revision 1.38 2005/11/23 14:13:51 deshmukh What the people
 * have been up to: *** empty log message *** What the people have been up to: to this class. What
 * the people have been up to: Revision 1.37 2005/11/21 15:02:16 deshmukh to this class. What the
 * people have been up to: Transaction methods added to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.36 2005/11/17 13:09:35
 * deshmukh Changes to this class. What the people have been up to: changes in Exception catching
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.35 2005/11/16 13:45:00 mschneider Changes to this class. What the
 * people have been up to: Merge of wfs development branch. Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.34.2.2
 * 2005/11/16 07:25:19 deshmukh Changes to this class. What the people have been up to: *** empty
 * log message *** Changes to this class. What the people have been up to: Changes to this class.
 * What the people have been up to: Revision 1.34.2.1 2005/11/15 14:36:19 deshmukh Changes to this
 * class. What the people have been up to: QualifiedName modifications Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.34
 * 2005/09/09 19:14:17 poth Changes to this class. What the people have been up to: no message
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.33 2005/09/09 08:20:03 poth Changes to this class. What the people
 * have been up to: no message Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.32 2005/09/08 13:16:33 poth Changes to
 * this class. What the people have been up to: no message Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.31 2005/09/08
 * 12:59:58 poth Changes to this class. What the people have been up to: no message
 * 
 * Revision 1.5 2004/06/28 06:27:05 ap no message
 * 
 * Revision 1.4 2004/06/21 08:05:49 ap no message
 * 
 * Revision 1.3 2004/06/18 08:33:31 ap no message
 * 
 * Revision 1.2 2004/06/17 15:43:12 ap no message
 * 
 * Revision 1.1 2004/06/17 13:48:28 ap no message
 * 
 **************************************************************************************************/
