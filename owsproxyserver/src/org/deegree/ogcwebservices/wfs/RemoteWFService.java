//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/RemoteWFService.java,v 1.30 2006/12/04 18:22:44 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
package org.deegree.ogcwebservices.wfs;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.capabilities.WFSOperationsMetadata;
import org.deegree.ogcwebservices.wfs.operation.DescribeFeatureType;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.FeatureTypeDescription;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.LockFeature;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;

/**
 * An instance of the class acts as a wrapper to a remote WFS. *  * @version $Revision: 1.30 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 */
public class RemoteWFService extends WFService {
    
    private static final ILogger LOG = LoggerFactory.getLogger( RemoteWFService.class );

    protected static final String GETCAPABILITIES = "GETCAPABILITIES";
    protected static final String GETFEATURE = "GETFEATURE";
    protected static final String GETFEATUREWITHLOCK = "GETFEATUREWITHLOCK";
    protected static final String DESCRIBEFEATURETYPE = "DESCRIBEFEATURETYPE";
    protected static final String TRANSACTION = "TRANSACTION";
    protected static final String LOCKFEATURE = "LOCKFEATURE";

   
    protected WFSCapabilities capabilities = null;

    protected HashMap addresses = new HashMap();

    /**
     * Creates a new instance of RemoteWFService
     * 
     * @param capabilities
     * @throws OGCWebServiceException
     */
    public RemoteWFService(WFSCapabilities capabilities) throws OGCWebServiceException {
        super( null );
        this.capabilities = capabilities;

        WFSOperationsMetadata om = (WFSOperationsMetadata)capabilities.getOperationsMetadata();
        Operation op = om.getGetCapabilitiesOperation();

        // get GetCapabilities address
        DCPType[] dcp = op.getDCPs();
        URL[] get = ((HTTP) dcp[0].getProtocol()).getGetOnlineResources();
        addresses.put(GETCAPABILITIES, get[0]);

        // get GetFeature address
        op = om.getGetFeature();
        dcp = op.getDCPs();
        boolean po = false;
        for (int i = 0; i < dcp.length; i++) {
            get = ((HTTP) dcp[i].getProtocol()).getPostOnlineResources();
            if (get != null && get.length > 0) {
                addresses.put(GETFEATURE, get[0]);
                po = true;
            }
        }
        if (!po) {
            String s = "WFS: " + capabilities.getServiceIdentification().getTitle()
                    + " doesn't " + "support HTTP POST for GetFeature requests";
            LOG.logDebug( s );
            throw new OGCWebServiceException(s);
        }

        // get DescribeFeatureType address
        op = om.getDescribeFeatureType();
        dcp = op.getDCPs();
        get = ((HTTP) dcp[0].getProtocol()).getGetOnlineResources();
        addresses.put(DESCRIBEFEATURETYPE, get[0]);

        op = om.getGetFeatureWithLock();       
        if ( op != null ) {
            // get GetFeatureWithLock address
            dcp = op.getDCPs();
            po = false;
            for (int i = 0; i < dcp.length; i++) {
                get = ((HTTP) dcp[i].getProtocol()).getPostOnlineResources();
                if (get != null && get.length > 0) {
                    addresses.put(GETFEATUREWITHLOCK, get[0]);
                    po = true;
                }
            }
            if (!po) {
                String s = "WFS: " + capabilities.getServiceIdentification().getTitle()
                        + " doesn't support HTTP POST for GetFeatureWithLock requests";
                LOG.logDebug( s );
                throw new OGCWebServiceException(s);
            }
        }

        op = om.getTransaction();
        if ( op != null ) {
            // get Transaction address
            dcp = op.getDCPs();
            po = false;
            for (int i = 0; i < dcp.length; i++) {
                get = ((HTTP) dcp[i].getProtocol()).getPostOnlineResources();
                if (get != null && get.length > 0) {
                    addresses.put(TRANSACTION, get[0]);
                    po = true;
                }
            }
            if (!po) {
                String s = "WFS: " + capabilities.getServiceIdentification().getTitle()
                        + " doesn't support HTTP POST for Transaction requests";
                LOG.logDebug( s );
                throw new OGCWebServiceException(s);
            }
        }

        op = om.getLockFeature();
        if ( op != null ) {
            // get LockFeature address
            dcp = op.getDCPs();
            get = ((HTTP) dcp[0].getProtocol()).getGetOnlineResources();
            if (get != null && get.length > 0) {
            	addresses.put(LOCKFEATURE, get[0]);
            }
        }

    }
    
    /**
     * 
     * @return
     */
    public WFSCapabilities getWFSCapabilities() {
        return capabilities;
    }

    /**
     * the method performs the handling of the passed OGCWebServiceEvent
     * directly and returns the result to the calling class/method
     * 
     * @param request
     *            request (WMS, WCS, WFS, WCAS, WCTS, WTS, Gazetter) to perform
     * 
     * @throws WebServiceException
     * 
     * @todo not implemented yet!
     */
    public Object doService(OGCWebServiceRequest request) throws OGCWebServiceException {        

        Object response = null;
        if (request instanceof GetFeature) {
            response = handleGetFeature((GetFeature) request);
        } else if (request instanceof DescribeFeatureType) {
            response = handleDescribeFeatureType((DescribeFeatureType) request );
        } else if (request instanceof WFSGetCapabilities) {
            response = handleGetCapabilities((WFSGetCapabilities) request );
        } else if (request instanceof LockFeature) {
            response = handleLockFeature((LockFeature) request);
        } else if (request instanceof Transaction) {
            response = handleTransaction((Transaction) request);
        }
        return response;
    }

    /**
     * performs a GetFeature request against the remote service. The method uses
     * http-POST to call the remote WFS
     * 
     * @param request
     *            get feature request
     */
    private FeatureResult handleGetFeature(GetFeature request) throws OGCWebServiceException {
        
        URL url = (URL) addresses.get(GETFEATURE);
        StringWriter writer = new StringWriter(1000);
        try {
            XMLFactory.export( request ).write( writer );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "could not transform GetFeature requst to its " +
                                              "string representation" + 
                                              StringTools.stackTraceToString(e) );
        }
        String param = writer.getBuffer().toString();

        FeatureCollection result = null;
        try {
            // get map from the remote service
            NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), url, param);
            String contentType = nw.getContentType();
            if (contentType == null || MimeTypeMapper.isKnownMimeType(contentType)) {                
                try {
                    InputStreamReader isr = new InputStreamReader(nw
                            .getInputStream(), CharsetUtils.getSystemCharset());
                    GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument ();
                    doc.load(isr, url.toString());
                    result = doc.parse();
                } catch (Exception e) {
                    throw new OGCWebServiceException(e.toString());
                }
            } else {
                throw new OGCWebServiceException( "RemoteWFS:handleGetFeature",
                        "Response of the remote WFS contains unknown content "
                        + "type: " + contentType + ";request: " + param);
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "RemoteWFS:handleGetFeature",
                    "Could not get feature from RemoteWFS: "
                    + capabilities.getServiceIdentification().getTitle()
                    + "; request: " + param + "; " + e.toString());

        }

        FeatureResult fr = new FeatureResult( request, result );                
        
        return fr;

    }

    /**
     * Pefroms a describe feature type request against a remote WFS. The method
     * uses http-GET to call the remote WFS
     * 
     * @param request
     *            describe feature type request
     * @param client
     *            receiver of the response to the request
     */
    private FeatureTypeDescription handleDescribeFeatureType(DescribeFeatureType request) 
                                                              throws OGCWebServiceException {

        URL url = (URL) addresses.get(DESCRIBEFEATURETYPE);

        String param = request.getRequestParameter();

        String result = null;
        try {
            String us = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() ) + param;
            URL ur = new URL( us );
            // get map from the remote service
            NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), ur);
            byte[] b = nw.getDataAsByteArr(20000);
            String contentType = nw.getContentType();
            if (MimeTypeMapper.isKnownMimeType(contentType)) {
                // create a WFSCapabilities instance from the result
                result = new String(b);
            } else {
                throw new OGCWebServiceException("RemoteWFS:handleDescribeFeatureType",
                        "Response of the remote WFS contains unknown content "
                        + "type: " + contentType + ";request: " + param);
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "RemoteWFS:handleDescribeFeatureType",
                    "Could not get map from RemoteWFS: "
                            + capabilities.getServiceIdentification().getTitle()
                            + "; request: " + param + "; " + e.toString());

        }

        FeatureTypeDescription ftd = null;
        try {
            XMLFragment frag = new XMLFragment( new StringReader(result), null );
            ftd = new FeatureTypeDescription( frag );
        } catch ( Exception e1 ) {
            LOG.logError( e1.getMessage(), e1 );
            throw new OGCWebServiceException( this.getClass().getName() +
                                              "Could not create response", 
                                              StringTools.stackTraceToString( e1 ) );
        } 
           
        return ftd;
    }

    /**
     * reads the capabilities from the remote WFS by performing a
     * GetCapabilities request against it. The method uses http-GET to call the
     * remote WFS
     * 
     * @param request
     *            capabilities request
     * @param client
     *            receiver of the response to the request
     */
    private WFSCapabilities handleGetCapabilities(WFSGetCapabilities request) 
                                               throws OGCWebServiceException {
        

        URL url = (URL) addresses.get(GETCAPABILITIES);
        String param = request.getRequestParameter();

        WFSCapabilities response = null;
        try {
            String remoteAddress = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() );
            URL ur = new URL( remoteAddress + param ); 
            WFSCapabilitiesDocument capabilitiesDoc = new WFSCapabilitiesDocument();
            capabilitiesDoc.load(ur);
            response = (WFSCapabilities)capabilitiesDoc.parseCapabilities();
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "RemoteWFS:handleGetCapabilities",
                    "Could not get map from RemoteWFS: "
                            + capabilities.getServiceIdentification().getTitle()
                            + "; request: " + param + "; " + e.toString());

        }        
                
                
        return response;

    }

    /**
     * @param request
     */
    private Object handleLockFeature(LockFeature request) {
        // FIXME 
        // TODO
        return null;
    }

    /**
     * @param request
     */
    private Object handleTransaction(Transaction request) {
        // FIXME
        // TODO
        return null;
    }

    /**
     * 
     */
    public WFSCapabilities getCapabilities() {
        return capabilities;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoteWFService.java,v $
Revision 1.30  2006/12/04 18:22:44  mschneider
Removed senseless assignments.

Revision 1.29  2006/11/07 11:09:36  mschneider
Added exceptions in case anything other than the 1.1.0 format is requested.

Revision 1.28  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.27  2006/10/09 12:48:34  poth
bug fix - chanes required because results now extending DefaultOGCWebServiceResponse

Revision 1.26  2006/09/06 08:41:30  poth
code enhancment / removing references to deprecated class / use OWSUtil instead of mauelly creating a base URL

Revision 1.25  2006/08/14 13:16:47  mschneider
Fixed header.

Revision 1.24  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */