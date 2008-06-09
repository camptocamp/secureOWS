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
package org.deegree.security.owsproxy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.users.MemoryUser;
import org.deegree.enterprise.servlet.ServletRequestWrapper;
import org.deegree.enterprise.servlet.ServletResponseWrapper;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.Encoders;
import org.deegree.ogcbase.BaseURL;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCRequestFactory;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.security.SecurityConfigurationException;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsrequestvalidator.Policy;
import org.deegree.security.owsrequestvalidator.PolicyDocument;
import org.deegree.security.owsrequestvalidator.csw.CSWValidator;
import org.deegree.security.owsrequestvalidator.wfs.WFSValidator;
import org.deegree.security.owsrequestvalidator.wms.WMSValidator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * An OWSProxyPolicyFilter can be registered as a ServletFilter to
 * a web context. It offeres a facade that looks like a OWS but 
 * additionaly enables validating incoming requests and outgoing
 * responses against rules defined in a policy document and/or a
 * deegree user and right management system. 
 * @see org.deegree.security.drm.SecurityRegistry
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.48 $, $Date: 2006/11/27 09:07:53 $
 * 
 * @since 1.1
 */
public class OWSProxyServletFilter implements Filter {

    private static final ILogger LOG = LoggerFactory.getLogger( OWSProxyServletFilter.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private FilterConfig config = null;

    private OWSProxyPolicyFilter pFilter = null;

    private Policy policy = null;

    private String altRequestPage = null;

    private String altResponsePage = null;

    private boolean imageExcpeted = false;

    /**     
     * initialize the filter with parameters from the deployment descriptor
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init( FilterConfig config )
                            throws ServletException {
        this.config = config;
        pFilter = new OWSProxyPolicyFilter();
        String proxyURL = "http://127.0.0.1/owsproxy/proxy";
        if ( config.getInitParameter( "PROXYURL" ) != null ) {
            proxyURL = config.getInitParameter( "PROXYURL" );
        }
        Enumeration iterator = config.getInitParameterNames();

        try {
            while ( iterator.hasMoreElements() ) {
                String paramName = (String) iterator.nextElement();
                String paramValue = config.getInitParameter( paramName );
                if ( paramName.endsWith( "POLICY" ) ) {
                    paramValue = config.getServletContext().getRealPath( paramValue );
                    File file = new File( paramValue );
                    initValidator( proxyURL, paramName, file.toURL() );
                }

            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new ServletException( e );
        }
        altRequestPage = config.getInitParameter( "ALTREQUESTPAGE" );
        altResponsePage = config.getInitParameter( "ALTRESPONSEPAGE" );
    }

    /**
     * @param paramName
     * @param paramValue
     * @throws ServletException
     */
    private void initValidator( String proxyURL, String paramName, URL paramValue )
                            throws ServletException {
        try {
            PolicyDocument doc = new PolicyDocument( paramValue );
            policy = doc.getPolicy();
            int pos = paramName.indexOf( ':' );
            String service = paramName.substring( 0, pos );
            if ( service.equals( "WMS" ) ) {
                WMSValidator v = new WMSValidator( policy, proxyURL );
                pFilter.addValidator( v );
            } else if ( service.equals( "WFS" ) ) {
                pFilter.addValidator( new WFSValidator( policy, proxyURL ) );
            } else if ( service.equals( "WCS" ) ) {
                // TODO
            } else if ( service.equals( "CSW" ) ) {
                pFilter.addValidator( new CSWValidator( policy, proxyURL ) );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new ServletException( StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * free resources allocated by the filter
     * 
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        config = null;
    }

    /**
     * perform filter
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
                            throws IOException, ServletException {
        // encapsulate the servelt request into a wrapper object to ensure
        // the availability of the InputStream
        ServletRequestWrapper reqWrap = new ServletRequestWrapper( (HttpServletRequest) request );
        // create OGCWebServiceRequest from the ServletRequest
//        String req = null;
//        try {
//            req = getRequestContent( reqWrap );
//        } catch ( GeneralSecurityException e ) {
//            LOG.logError( e.getMessage(), e );
//            throw new ServletException( e.getMessage() );
//        }
        OGCWebServiceRequest owsReq = null;

        if (0 == request.getParameterMap().size()) {
            response.setContentType( "text/html" );
            OutputStream os = response.getOutputStream();
            os.write( ("Welcome to the OwsProxyServer application. " +
            		"Please provide GET parameters\n").getBytes() );
            os.close();
            return;
        }
        
        try {
            owsReq = OGCRequestFactory.create( reqWrap );
        } catch ( OGCWebServiceException e ) {
            LOG.logError( e.getMessage(), e );
            throw new ServletException( e.getMessage() );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            throw new ServletException( e.getMessage() );
        }
        // extract user from the request
        User user = null;
        try {
            user = getUser( reqWrap, owsReq );
        } catch ( UnauthorizedException e1 ) {
            handleResponseMissingAutorization( (HttpServletRequest) request,
                                               (HttpServletResponse) response, e1.getMessage() );
            return;
        } catch (InvalidParameterValueException e) {
            // this may happens if a USER with none assigned valueparameter is used within a request
            LOG.logError( e.getMessage(), e );
            request.setAttribute( "MESSAGE", e.getMessage() );
            ServletContext sc = config.getServletContext();
            sc.getRequestDispatcher( altResponsePage ).forward( request, response );
            return;
        }
        
        UsersOperationParameter.setCurrentUser(user);
        
        try {
            // XXXsyp
            //pFilter.validateGeneralConditions( (HttpServletRequest) request, reqWrap.getContentLength(), user );
            pFilter.validate( owsReq, user );
        } catch ( InvalidParameterValueException e ) {
            handleRequestMissingAutorization( (HttpServletRequest) request,
                                              (HttpServletResponse) response, e.getMessage() );
            return;
        } catch ( UnauthorizedException e ) {
            handleRequestMissingAutorization( (HttpServletRequest) request,
                                              (HttpServletResponse) response, e.getMessage() );
            return;
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            request.setAttribute( "MESSAGE", e.getMessage() );
            ServletContext sc = config.getServletContext();
            sc.getRequestDispatcher( altResponsePage ).forward( request, response );
            return;
        }
        // encapsulate the servelt response into a wrapper object to ensure
        // the availability of the OutputStream
        ServletResponseWrapper resWrap = new ServletResponseWrapper( (HttpServletResponse) response );
        logHttpRequest( reqWrap );
        // forward request to the next filter or servlet        
        chain.doFilter( reqWrap, resWrap );
        // get result from performing the request
        OutputStream os = resWrap.getOutputStream();
        byte[] b = ( (ServletResponseWrapper.ProxyServletOutputStream) os ).toByteArray();

        if ( !imageExcpeted ) {
            LOG.logDebug( new String( b ) );
        }
        try {
            // validate the result of a request performing
            String mime = resWrap.getContentType();
            LOG.logDebug( "mime type raw: " + mime );
            if ( mime != null ) {
                mime = StringTools.toArray( mime, ";", false )[0];
            } else {
                if ( imageExcpeted ) {
                    mime = "image/jpeg";
                } else {
                    mime = "text/xml";
                }
            }
            LOG.logDebug( "mime type: " + mime );
            b = pFilter.validate( owsReq, b, mime, user );
        } catch ( InvalidParameterValueException ee ) {
            LOG.logError( ee.getMessage(), ee );
            handleResponseMissingAutorization( (HttpServletRequest) request,
                                               (HttpServletResponse) response, ee.getMessage() );
            return;
        } catch ( UnauthorizedException e ) {
            LOG.logError( e.getMessage(), e );
            handleResponseMissingAutorization( (HttpServletRequest) request,
                                               (HttpServletResponse) response, e.getMessage() );
            return;
        }

        response.setContentType( resWrap.getContentType() );
        // write result back to the client
        os = response.getOutputStream();
        os.write( b );
        os.close();
    }

    /**
     * logs a requests parameters and meta informations
     * @param reqWrap
     */
    private void logHttpRequest( ServletRequestWrapper reqWrap ) {
        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            LOG.logDebug( "getRemoteAddr " + reqWrap.getRemoteAddr() );
            LOG.logDebug( "getPort " + reqWrap.getServerPort() );
            LOG.logDebug( "getMethod " + reqWrap.getMethod() );
            LOG.logDebug( "getQueryString " + reqWrap.getQueryString() );
            LOG.logDebug( "getPathInfo " + reqWrap.getPathInfo() );
            LOG.logDebug( "getRequestURI " + reqWrap.getRequestURI() );
            LOG.logDebug( "getServerName " + reqWrap.getServerName() );
            LOG.logDebug( "getServerPort " + reqWrap.getServerPort() );
            LOG.logDebug( "getServletPath " + reqWrap.getServletPath() );
        }
    }

    /**
     * go to alternative page if autorization to perform the desired request ist missing
     * 
     * @param message
     *            message indicating the missing right
     */
    private void handleRequestMissingAutorization( HttpServletRequest request,
                                                  HttpServletResponse response, String message )
                            throws IOException, ServletException {
        if ( message == null ) {
            message = "missing authorization";
        }
        if ( imageExcpeted ) {
            response.setContentType( "image/jpeg" );
            OutputStream os = response.getOutputStream();
            BufferedImage bi = new BufferedImage( 500, 500, BufferedImage.TYPE_INT_RGB );
            Graphics g = bi.getGraphics();
            g.setColor( Color.WHITE );
            g.fillRect( 0, 0, 500, 500 );
            g.setColor( Color.BLACK );
            g.setFont( new Font( "DIALOG", Font.PLAIN, 14 ) );
            g.drawString( Messages.getString( "MISSINGAUTHORIZATION" ), 5, 60 );
            String[] lines = StringTools.toArray( message, ":|", false );
            int y = 100;
            for ( int i = 0; i < lines.length; i++ ) {
                g.drawString( lines[i], 5, y );
                y = y + 30;
            }
            g.dispose();
            try {
                Encoders.encodeJpeg( os, bi );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            os.close();
        } else {
            request.setAttribute( "MESSAGE", message );
            ServletContext sc = config.getServletContext();
            sc.getRequestDispatcher( altRequestPage ).forward( request, response );
        }
    }

    /**
     * go to alternative page if autorization to deliver the result to a request is missing
     * 
     * @param message
     *            message indicating the missing right
     */
    private void handleResponseMissingAutorization( HttpServletRequest request,
                                                   HttpServletResponse response, String message )
                            throws IOException, ServletException {
        if ( imageExcpeted ) {
            response.setContentType( "image/jpeg" );
            OutputStream os = response.getOutputStream();
            BufferedImage bi = new BufferedImage( 500, 500, BufferedImage.TYPE_INT_RGB );
            Graphics g = bi.getGraphics();
            g.setColor( Color.WHITE );
            g.fillRect( 0, 0, 500, 500 );
            g.setColor( Color.BLACK );
            g.setFont( new Font( "DIALOG", Font.PLAIN, 14 ) );
            String[] lines = StringTools.toArray( message, ":|", false );
            int y = 100;
            for ( int i = 0; i < lines.length; i++ ) {
                g.drawString( lines[i], 5, y );
                y = y + 30;
            }
            g.dispose();
            try {
                Encoders.encodeJpeg( os, bi );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                e.printStackTrace();
            }
            os.write( message.getBytes() );
            os.close();
        } else {
            request.setAttribute( "MESSAGE", message );
            ServletContext sc = config.getServletContext();
            sc.getRequestDispatcher( altResponsePage ).forward( request, response );
        }
    }

    /**
     * returns the user from the incomming request. The extraction of the user takes three steps
     * <ul>
     * <li>1. get the vendorspecific parameter 'USER' & 'PASSWORD'
     * <li>2. if 1.) is null get the remote users name (request.getRemoteUser())
     * </ul>
     * 
     * @param request
     * @return
     * @throws InvalidParameterValueException 
     */
    private User getUser( HttpServletRequest request, OGCWebServiceRequest owsReq )
                            throws UnauthorizedException, IOException, InvalidParameterValueException {

        String sessionId = owsReq.getVendorSpecificParameter( "SESSIONID" );
        String user = owsReq.getVendorSpecificParameter( "USER" );
        String password = null;
        if ( user != null ) {
            LOG.logDebug( "get user from user/password parameter" );
            return authentificateFromUserPw( owsReq );
        } else if ( sessionId == null && user == null && request.getUserPrincipal() != null ) {    
            LOG.logDebug( "get user from UserPrinicipal" );
            user = request.getUserPrincipal().getName();
            if ( user.indexOf( "\\" ) > 1 ) {
                String[] us = StringTools.toArray( user, "\\", false );
                user = us[us.length-1];
            }
        } else if ( policy.getSecurityConfig() != null && sessionId != null ) {
            LOG.logDebug( "get user from WAS/sessionID" );
            AuthentificationSettings as = policy.getSecurityConfig().getAuthsettings();
            BaseURL baseUrl = as.getAuthentificationURL();
            String tmp[] = getUserFromWAS( baseUrl.getOnlineResource().toExternalForm(), sessionId );
            user = tmp[0];
            password = tmp[1];
        } else {
            LOG.logDebug( "get user as source IP address because wether USER, " +
                          "SESSIONID nor Userprincipal are available" );
            user = request.getRemoteAddr();
        }
        LOG.logDebug( StringTools.concat( 100, "USER: ", user, '/', password ) );
        User usr = null;
        try {
            if ( user != null && SecurityAccessManager.isInitialized() ) {
                SecurityAccessManager sam = SecurityAccessManager.getInstance();

                usr = sam.getUserByName( user );
                if ( request.getUserPrincipal() == null ) {
                    // a user just must authenticate himself if he is
                    // not identified by its user name being send within
                    // the HTTP header
                    usr.authenticate( password );
                } else {
                    // if user is read from UserPrincipal his password must
                    // be read from security management
                    usr.authenticate( sam.getUserByName( user ).getPassword() );
                }
            }
            
            // XXXsyp Alternative User
            usr = new User(0, user, "password", "first", "last", "email", null /* registry */);
            
            usr.servletRequest = request;

        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new UnauthorizedException( Messages.format( "OWSProxyServletFilter.USERERROR",
                                                              user ) );
        }

        return usr;
    }

    /**
     * authentificates a user if he is identified by its name and password passed as vendorspecific
     * parameters with an OGC service request
     * 
     * @param request
     * @param owsReq
     * @return
     * @throws UnauthorizedException
     * @throws InvalidParameterValueException 
     * @throws IOException
     */
    private User authentificateFromUserPw( OGCWebServiceRequest owsReq )
                            throws UnauthorizedException, InvalidParameterValueException {
        String user = owsReq.getVendorSpecificParameter( "USER" );
        String password = owsReq.getVendorSpecificParameter( "PASSWORD" );
        
        LOG.logDebug( "USER: ", user );
        LOG.logDebug( "PASSWORD: ", password );
        if ( user == null ) {
            throw new InvalidParameterValueException( Messages.getString( "USERNAMEMISSING" ) );
        }

        User usr = null;
        try {
            SecurityAccessManager sam = SecurityAccessManager.getInstance();
            usr = sam.getUserByName( user );
            usr.authenticate( password );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            if ( !( user.equals( "anonymous" ) ) ) {
                throw new UnauthorizedException( Messages.format( "OWSProxyServletFilter.USERERROR",
                                                                  user ) );
            }
        }

        return usr;
    }

    /**
     * access user informations from a remote WAAS. an array of Strings will be returned. with
     * <ul>
     * <li>[0] = user name
     * <li>[1] = the users password
     * </ul>
     * 
     * @param sessionID
     * @return
     * @throws UnauthorizedException
     * @throws SecurityConfigurationException
     */
    private String[] getUserFromWAS( String urlStr, String sessionID )
                            throws IOException {
        String[] user = new String[3];
        try {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( urlStr ).append( "?REQUEST=DescribeUser&Service=WAS&" );
            sb.append( "SESSIONID=" ).append( sessionID ).append( "&version=1.0.0" );
            URL url = new URL( sb.toString() );
            InputStreamReader isr = new InputStreamReader( url.openStream() );
            Document doc = XMLTools.parse( isr );
            user[0] = XMLTools.getNodeAsString( doc, "/User/UserName", nsContext, null );
            user[1] = XMLTools.getNodeAsString( doc, "/User/Password", nsContext, null );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new IOException( Messages.getString( "OWSProxyServletFilter.WASACCESS" ) );
        }
        return user;
    }

    /**
     * creates an <code>AbstractOGCWebServiceRequest</code> from the content contained within the
     * passed request
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws OGCWebServiceException
     */
//    public OGCWebServiceRequest createOGCWebServiceRequest( ServletRequest request )
//                            throws OGCWebServiceException {
//        
//        try {
//            return OGCRequestFactory.create( request );
//        } catch ( IOException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch ( XMLParsingException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch ( SAXException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        return null;
////        try {
////            if ( request.startsWith( "<" ) ) {
////                Document doc = XMLTools.parse( new StringReader( request ) );
////                return OGCRequestFactory.createFromXML( doc );
////            }
////        } catch ( Exception e ) {
////            e.printStackTrace();
////        }
////        try {
////            return createFromKVP( request );
////        } catch ( Exception e ) {
////            LOG.logError( e.getMessage(), e );
////            throw new OGCWebServiceException( Messages.format( "OWSProxyServletFilter.KVPREQ",
////                                                               request ) );
////        }
//
//    }

    /**
     * creates a request object from a KVP encoded OWS request
     * 
     * @param request
     * @return
     * @throws Exception
     */
//    private OGCWebServiceRequest createFromKVP( String request )
//                            throws Exception {
//
//        OGCWebServiceRequest req = OGCRequestFactory.createFromKVP( request );
//
//        if ( req instanceof GetMap || req instanceof GetLegendGraphic ) {
//            imageExcpeted = true;
//        }
//
//        return req;
//    }

    /**
     * extracts the content of a HTTP request from the encapsulating object
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws GeneralSecurityException 
     */
//    private String getRequestContent( HttpServletRequest request )
//                            throws IOException, GeneralSecurityException {
//        String method = request.getMethod();
//        if ( method.equalsIgnoreCase( "POST" ) ) {
//            Reader reader = request.getReader();
//            BufferedReader br = new BufferedReader( reader );
//            StringBuffer req = new StringBuffer( 10000 );
//            String line = null;
//            while ( ( line = br.readLine() ) != null ) {
//                req.append( line );
//            }
//            br.close();
//            if ( req.length() == 0 ) {
//                throw new GeneralSecurityException( Messages.getString( "OWSProxyServletFilter.QUERYSTRING2" ) );
//            }
//            return req.toString();
//        }
//        String s = request.getQueryString();
//        if ( s == null ) {
//            throw new GeneralSecurityException( Messages.getString( "OWSProxyServletFilter.QUERYSTRING1" ) );
//        }
//        return URLDecoder.decode( s, CharsetUtility.getSystemCharset() );
//    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSProxyServletFilter.java,v $
Revision 1.48  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.47  2006/11/21 17:59:16  poth
useless import removed

Revision 1.46  2006/10/30 08:07:06  poth
bug fix - WFS GetFeature request: FeatureType comparsion

Revision 1.45  2006/10/27 11:55:45  poth
authentication for users changed to use Remote Address as default

Revision 1.44  2006/09/18 11:02:14  poth
loglevel for username and password changed to DEBUG

Revision 1.43  2006/09/18 10:56:48  poth
bug fix - parsing userName from DescribeUser response

Revision 1.42  2006/09/15 19:21:43  poth
debug level changed for printing user/password

Revision 1.41  2006/09/08 15:14:38  schmitz
Updated the core of deegree to use the HttpServletRequest methods
to create the KVP maps, and not to try to parse as XML every time.
Updated the tests to create maps for testing instead of strings.
Updated the OWSProxy subsystem to use ServletRequest classes instead
of strings for request dispatching.

Revision 1.40  2006/08/29 19:20:01  poth
bug fix - changed GetUser to DescribeUser request (WAS)

Revision 1.39  2006/08/14 13:39:38  poth
printing stacktraces removed for authorization errors

Revision 1.38  2006/08/08 15:46:49  poth
bug fix - double URL decoding of user and password removed

Revision 1.37  2006/08/07 15:55:32  poth
debug statements added for authenticateFromUserPw

Revision 1.36  2006/08/02 14:13:45  poth
changed user identification - if a user is null or security manager has not been initialized no validation of user will be performed

Revision 1.35  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.34  2006/07/21 07:39:05  poth
URLdecoding for usernames and passwords added

Revision 1.33  2006/07/03 15:36:17  poth
bug fix - handling case where a request has no content (avoid NPE) / correction of comments

Revision 1.32  2006/06/29 09:08:36  poth
*** empty log message ***

Revision 1.30  2006/06/22 06:55:13  poth
enabled reading user principals having '\' in its name by just using the part after the last '\'

Revision 1.29  2006/05/24 16:12:40  poth
support for WFS GetFeature validation added


********************************************************************** */