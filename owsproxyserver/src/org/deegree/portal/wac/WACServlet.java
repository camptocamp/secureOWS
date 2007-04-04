/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 University of Bonn
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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.portal.wac;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Linkage;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wms.XMLFactory;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
import org.w3c.dom.Document;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 1.1, $Revision: 1.16 $, $Date: 2006/08/23 07:10:22 $
 * 
 * @since 1.1
 */
public class WACServlet extends HttpServlet {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();    
    
    private Map users = new HashMap();

    private Map passwords = new HashMap();

    private Map sessionIDs = Collections.synchronizedMap( new HashMap() );

    private Map expiration = new HashMap();

    private String host = null;

    private int port = 443;

    private String path = null;

    private String certificate = null;

    private String wacURL = null;
    
    private static final ILogger LOG = LoggerFactory.getLogger( WACServlet.class );

    public void init() throws ServletException {
        super.init();
        String user = getInitParameter( "USER" );
        if ( user == null ) {
            throw new ServletException( "user must be set!" );
        }
        users.put( "*", user );
        String password = getInitParameter( "PASSWORD" );
        if ( password == null ) {
            throw new ServletException( "password must be set!" );
        }
        passwords.put( "*", password );
        host = getInitParameter( "HOST" );
        if ( host == null ) {
            throw new ServletException( "WSS host must be set!" );
        }
        try {
            port = Integer.parseInt( getInitParameter( "PORT" ) );
        } catch (NumberFormatException e) {
            getServletContext().log( "-> using default SSL port 443" );
        }
        path = getInitParameter( "PATH" );
        if ( path == null ) {
            throw new ServletException( "path to web application on host must be set!" );
        }
        certificate = getInitParameter( "CERTIFICATE" );
        if ( certificate == null ) {
            getServletContext().log( "no certificate defined" );
        }

    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException,
            IOException {

        wacURL = "http://"
            + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
            + request.getServletPath();

        String user = (String) users.get( "*" );
        WAClient wac = new WAClient( host, path, port, certificate );
        if ( sessionIDs.get( user ) == null ) {
            if ( !accessSessionID( wac, response ) ) {
                return;
            }
        }
        // get sessionID assigned to the user
        String sessionID = (String) sessionIDs.get( user );
        String exp = (String) expiration.get( sessionID );
        long tst1 = TimeTools.createCalendar( exp ).getTimeInMillis();
        long tst2 = System.currentTimeMillis();
        // check if sessionID is expired
        // if so, get a new one
        if ( tst1 > tst2 ) {
            if ( !accessSessionID( wac, response ) ) {
                return;
            }
        }
        InputStream is = null;
        try {
            StringBuffer sb = new StringBuffer( 2000 );
            sb.append( request.getQueryString() ).append( "&sessionID=" ).append( sessionID );
            is = wac.performDoService( request.getQueryString(), sessionID );
        } catch (Exception e) {
            e.printStackTrace();
            sendException( response, "GetSession", "could not perform DoService", StringTools
                .stackTraceToString( e ) );
        }

        OutputStream os = null;
        try {
            os = response.getOutputStream();
            postProcess( request.getQueryString(), is, os );
        } catch (Exception e) {
            sendException( response, "GetSession", "could not post process capabilities",
                StringTools.stackTraceToString( e ) );
        } finally {
            os.flush();
            os.close();
            is.close();
        }

    }

    /**
     * access a sessionID from a WSS and stores it into an internal Map to use for DoService calls
     * against a WSS
     * 
     * @param wac
     * @param response
     * @return
     */
    private boolean accessSessionID( WAClient wac, HttpServletResponse response ) {
        String user = (String) users.get( "*" );
        String password = (String) passwords.get( "*" );
        try {
            Document doc = wac.performGetSession( user, password );
            String sessionID = XMLTools.getRequiredNodeAsString( doc, "/wsssession:Session/@id",
                nsContext );
            String exp = XMLTools.getRequiredNodeAsString( doc,
                "/wsssession:Session/@expirationDate", nsContext );
            sessionIDs.put( user, sessionID );
            expiration.put( sessionID, exp );
        } catch (WACException e) {
            e.printStackTrace();
            sendException( response, "GetSession", "could not perform GetSession", StringTools
                .stackTraceToString( e ) );
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            sendException( response, "GetSession", "could not evaluate GetSession result",
                StringTools.stackTraceToString( e ) );
            return false;
        }
        return true;
    }

    private void sendException( HttpServletResponse response, String req, String message,
                               String stacktrace ) {
        this.getServletContext().log( message );
        this.getServletContext().log( stacktrace );
        response.setContentType( "text/xml" );
        if ( req == null )
            req = "";
        if ( message == null )
            message = "";
        try {
            PrintWriter pw = response.getWriter();
            pw.write( "<OGCWebServiceException>" );
            pw.write( "<Message>" );
            pw.write( req );
            pw.write( ": failed! " );
            pw.write( message );
            pw.write( "</Message>" );
            pw.write( "<Locator>" );
            pw.write( stacktrace );
            pw.write( "</Locator>" );
            pw.write( "</OGCWebServiceException>" );
            pw.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    /**
     * forces a post processing of the wss response if a GetCapabilities request has been performed
     * by replacing contained the online resources
     * 
     * @param request
     * @param is
     * @param os
     *            stream to write the result too
     * @return
     * @throws Exception
     */
    private void postProcess( String request, InputStream is, OutputStream os ) throws Exception {
        Map map = KVP2Map.toMap( request );
        if ( map.get( "REQUEST" ).equals( "GetCapabilities" ) ) {
            Document doc = XMLTools.parse( is );
            if ( map.get( "SERVICE" ).equals( "WMS" ) ) {
                adjustWMSCapabilities( doc, os );
            } else if ( map.get( "SERVICE" ).equals( "WFS" ) ) {
                // TODO
            } else if ( map.get( "SERVICE" ).equals( "WCS" ) ) {
                // TODO
            } else if ( map.get( "SERVICE" ).equals( "CSW" ) ) {
                // TODO
            } else if ( map.get( "SERVICE" ).equals( "SCS" ) ) {
                // TODO
            }
            if ( map.get( "SERVICE" ).equals( "WFS-G" ) ) {
                // TODO
            }
            if ( map.get( "SERVICE" ).equals( "WTS" ) ) {
                // TODO
            }
        }
    }

    /**
     * adjusts the passed WMS capabilities document by replacing the contained online resources with
     * the address of the WAC
     * 
     * @param xml
     * @param user
     * @throws InvalidParameterValueException
     */
    private void adjustWMSCapabilities( Document doc, OutputStream os )
        throws InvalidParameterValueException, IOException{

        WMSCapabilities capa = null;
        try {
            WMSCapabilitiesDocument cdoc = new WMSCapabilitiesDocument();
            cdoc.setRootElement( doc.getDocumentElement() );
            capa = (WMSCapabilities) cdoc.parseCapabilities();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterValueException( "no valid wms capabilities\n"
                + StringTools.stackTraceToString( e ) );
        }

        OperationsMetadata om = capa.getOperationMetadata();

        List<Operation> ops = om.getOperations();
        for ( Operation operation : ops ) {
            setNewOnlineResource( operation );
        }

        WMSCapabilitiesDocument cdoc = XMLFactory.export( capa );
        cdoc.write( os );
    }

    /**
     * sets a new online resource for the passed <tt>Operation</tt>
     * 
     * @param op
     */
    private void setNewOnlineResource( Operation op ) {
        List<DCP> dcps = op.getDCP();
        if ( dcps != null ) {
            for( DCP dcp : dcps ) {
                HTTP http = (HTTP) dcp;
                try {
                    URL url = new URL( wacURL );
                    OnlineResource link = new OnlineResource( new Linkage( url ) );
                    List<OnlineResource> resources = http.getLinks();
                    int size = resources.size();
                    resources.clear();
                    for( int i = 0; i < size; ++i ) resources.add( link );
                } catch (MalformedURLException e1) {
                    LOG.logError( e1.getLocalizedMessage(), e1 );
                }
            }
        }
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException,
            IOException {

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WACServlet.java,v $
Revision 1.16  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.15  2006/08/22 10:25:01  schmitz
Updated the WMS to use the new OWS common package.
Updated the rest of deegree to use the new data classes returned
by the updated WMS methods/capabilities.

Revision 1.14  2006/08/08 15:45:35  poth
never thrown exception removed

Revision 1.13  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
