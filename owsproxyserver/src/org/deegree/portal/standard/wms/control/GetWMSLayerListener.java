//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/wms/control/GetWMSLayerListener.java,v 1.19 2006/11/21 17:30:48 mays Exp $
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
package org.deegree.portal.standard.wms.control;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.portal.PortalException;
import org.deegree.portal.standard.wms.util.ClientHelper;
import org.xml.sax.SAXException;

/**
 * Lister class for accessing the layers of WMS and return it to the
 * client.
 *
 * @version $Revision: 1.19 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mays $
 *
 * @version 1.0. $Revision: 1.19 $, $Date: 2006/11/21 17:30:48 $
 *
 * @since 2.0
 */
public class GetWMSLayerListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( GetWMSLayerListener.class );

    /**
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    @Override
    public void actionPerformed( FormEvent event ) {

        RPCWebEvent rpc = (RPCWebEvent) event;
        try {
            validate( rpc );
        } catch ( Exception e ) {
            gotoErrorPage( "Not a valid RPC for GetWMSLayer\n" + e.getMessage() );
        }

        WMSCapabilities capabilities = null;
        URL url = null;
        try {
            url = getURL( rpc );
            capabilities = getWMSCapabilities( url );
        } catch ( MalformedURLException ue ) {
            LOG.logError( ue.getMessage(), ue );
            gotoErrorPage( "Not a valid URL for a WMS in GetWMSLayer\n" + ue.getMessage() );
            return;
        } catch ( PortalException e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( "Wrong WMS version: \n" + e.getMessage() );
            return;
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( "unspecified error: \n" + e.getMessage() );
            return;
        }

        String s = ClientHelper.getLayersAsTree( capabilities.getLayer() );
        getRequest().setAttribute( "WMSLAYER", s );
        getRequest().setAttribute( "WMSURL", NetWorker.url2String( url ) );
        getRequest().setAttribute( "WMSVERSION", capabilities.getVersion() );
        s = capabilities.getServiceIdentification().getTitle();
        s = s.replaceAll( "'", "" );
        getRequest().setAttribute( "WMSNAME", s );

        OperationsMetadata om = capabilities.getOperationMetadata();
        Operation op = om.getOperation( new QualifiedName( "GetMap" ) );
        if( op == null ) om.getOperation( new QualifiedName( "map" ) );
        DomainType parameter = (DomainType) op.getParameter( new QualifiedName( "Format" ) );
        
        // the request needs a String[], not a String
        List<TypedLiteral> values = parameter.getValues();
        String[] valueArray = new String[values.size()];
        for( int i = 0; i < values.size(); ++i) valueArray[i] = values.get( i ).getValue();
        getRequest().setAttribute( "FORMATS", valueArray );
    }

    /**
     * @param rpc
     * @throws PortalException
     */
    private void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        RPCMember address = struct.getMember( "WMSURL" );
        if ( address == null ) {
            throw new PortalException( "missing parameter WMSURL in RPC for GetWMSLayer" );
        }
        RPCMember version = struct.getMember( "VERSION" );
        if ( version != null && 
             ( ( (String) version.getValue() ).compareTo( "1.3.0" ) > 0 ||  
               ( (String) version.getValue() ).compareTo( "1.1.0" ) < 0 ) ) {
            throw new PortalException( "VERSION must be >= 1.1.0 and <= 1.3.0 but it is " 
                                       + version.getValue() );
        }
    }

    private URL getURL( RPCWebEvent rpc )
                            throws MalformedURLException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        String address = RPCUtils.getRpcPropertyAsString( struct, "WMSURL" );        

        // according to OGC WMS 1.3 Testsuite a URL to a service operation
        // via HTTPGet must end with '?' or '&'
        String href = OWSUtils.validateHTTPGetBaseURL( address );

        StringBuffer sb = new StringBuffer( href );
        sb.append( "service=WMS&request=GetCapabilities" );   
        String version = RPCUtils.getRpcPropertyAsString( struct, "VERSION" );
        if ( version != null ) {
            sb.append( "&version=" ).append( version );
        }
        
        String sessionid = RPCUtils.getRpcPropertyAsString( struct, "SESSIONID" );
        if ( sessionid != null ) {
            sb.append( "&sessionID=" ).append( sessionid );
        } 
        
        return new URL( sb.toString() );
    }

    /**
     * 
     * @param url
     * @return the capabilities object
     * @throws XMLParsingException
     * @throws PortalException
     * @throws IOException 
     * @throws SAXException 
     * @throws InvalidCapabilitiesException 
     */
    private WMSCapabilities getWMSCapabilities( URL url )
                            throws PortalException, IOException, SAXException,
                            InvalidCapabilitiesException {

        WMSCapabilitiesDocument doc = new WMSCapabilitiesDocument();
        doc.load( url );
        WMSCapabilities capabilities = (WMSCapabilities) doc.parseCapabilities();

        if ( capabilities.getVersion().compareTo( "1.1.0" ) < 0
             || capabilities.getVersion().compareTo( "1.3.0" ) > 0 ) {
            throw new PortalException( "VERSION must be >= 1.1.0 && <= 1.3.0" );
        }

        return capabilities;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetWMSLayerListener.java,v $
 Revision 1.19  2006/11/21 17:30:48  mays
 bug fix: validate version to be smaler than 1.3.0 AND larger than 1.1.0;
 clean up java docs (add missing tags)

 Revision 1.18  2006/10/17 20:31:17  poth
 *** empty log message ***

 Revision 1.17  2006/09/15 08:47:22  poth
 bug fixes

 Revision 1.16  2006/09/15 07:52:11  poth
 bug fix - fomart list passed to jsp page / considering WMS 1.3

 Revision 1.15  2006/09/01 12:28:43  schmitz
 Fixed two bugs:
 $DEFAULT is no longer appended instead of empty string in remote WMS requests (STYLE parameter).
 The FORMATS attribute now returns once more a String[] in the GetWMSLayerListener class.

 Revision 1.14  2006/08/29 20:19:12  poth
 code for reading userPrincipal removed; shall be substitued by evaluating the users sessionID if required

 Revision 1.13  2006/08/29 19:54:13  poth
 footer corrected

 Revision 1.12  2006/08/23 07:10:22  schmitz
 Renamed the owscommon_neu package to owscommon_new.

 Revision 1.11  2006/08/22 10:25:01  schmitz
 Updated the WMS to use the new OWS common package.
 Updated the rest of deegree to use the new data classes returned
 by the updated WMS methods/capabilities.

 Revision 1.10  2006/08/20 20:53:54  poth
 changes rquired as a consequence of bug fix in wmc implementation/handling. Instead of determining the correct URL as given in a services capabilities deegree always has used the base URL which is just guarenteed to be valid for GetCapabilities requests.

 Revision 1.9  2006/08/18 09:00:01  poth
 code fragment externalized into seperate method in org.deegree.portal.Util

 Revision 1.8  2006/08/17 19:30:35  poth
 bug fix - OGC WMS 1.3 specification defines that HTTPGet URLs must end with '&' or '?'

 Revision 1.7  2006/08/15 08:03:00  poth
 bug fix - OGC WMS 1.3 specification defines that HTTPGet URLs must end with '&' or '?'

 Revision 1.6  2006/06/25 20:35:42  poth
 support for forwarding UserPrincipal as part of GetCapabilitities, GetMap and GetLegendGraphicRequests

 Revision 1.5  2006/05/12 11:17:41  poth
 *** empty log message ***

 Revision 1.4  2006/04/06 20:25:31  poth
 *** empty log message ***

 Revision 1.3  2006/03/30 21:20:28  poth
 *** empty log message ***

 Revision 1.2  2006/03/03 15:18:32  poth
 *** empty log message ***

 Revision 1.1  2006/02/05 09:30:12  poth
 *** empty log message ***

 Revision 1.9  2006/01/16 20:36:39  poth
 *** empty log message ***

 Revision 1.8  2005/09/27 19:53:19  poth
 no message

 Revision 1.7  2005/08/05 19:14:34  poth
 no message

 Revision 1.6  2005/06/27 09:24:17  poth
 no message

 Revision 1.5  2005/06/27 08:59:07  poth
 no message

 Revision 1.4  2005/04/06 15:56:46  poth
 no message

 Revision 1.3  2005/03/09 11:55:46  mschneider
 *** empty log message ***

 Revision 1.2  2005/02/16 11:03:00  poth
 no message

 Revision 1.1.1.1  2005/01/05 10:29:53  poth
 no message

 Revision 1.1  2004/07/23 07:14:45  ap
 no message

 Revision 1.2  2004/07/21 07:43:42  poth
 no message

 Revision 1.1  2004/07/19 07:41:42  poth
 no message

 ********************************************************************** */
