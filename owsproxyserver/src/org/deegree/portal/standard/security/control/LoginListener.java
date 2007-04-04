// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/security/control/LoginListener.java,v 1.11 2006/11/30 13:50:02 mays Exp $
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
package org.deegree.portal.standard.security.control;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.BaseURL;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.portal.Constants;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.ViewContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Listener class for handling login to iGeoPortal standard edition
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mays $
 * 
 * @version $Revision: 1.11 $, $Date: 2006/11/30 13:50:02 $
 */
public class LoginListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( LoginListener.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * performs a login request. the passed event contains a RPC method call containing user name
     * and password.
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     * 
     * @param event
     */
    @Override
    public void actionPerformed( FormEvent event ) {
        RPCWebEvent re = (RPCWebEvent) event;

        if ( !validateRequest( re ) ) {
            String s = "Invalid content for login. Please validate if you have NAME and PASSWORD";
            gotoErrorPage( s );
            LOG.logDebug( s );
            return;
        }

        String[] result = null;
        try {
            result = performLogin( re );
        } catch ( InvalidParameterValueException ipve ) {
            gotoErrorPage( ipve.toString() );
            LOG.logDebug( ipve.getMessage(), ipve );
            return;
        } catch ( LoginFailureException lfe ) {
            LOG.logDebug( lfe.getMessage(), lfe );
            try {
                handleLoginFailure( lfe.getMessage() );
            } catch ( Exception e ) {
                gotoErrorPage( e.toString() );
                return;
            }
            return;
        } catch ( Exception e ) {
            gotoErrorPage( e.toString() );
            LOG.logDebug( e.getMessage(), e );
            return;
        }

        // write request parameter into session to reconstruct the search form
        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
        session.setAttribute( "SESSIONID", result[0] );
        getRequest().setAttribute( "SESSIONID", result[0] );
        getRequest().setAttribute( "USER", result[1] );
    }

    /**
     * handles the case if a login fails. extracts the message from the exception XML and pass it to
     * the error page (error.jsp)
     * 
     * @param messageXML
     * @throws SAXException 
     * @throws XMLParsingException 
     * @throws IOException 
     */
    private void handleLoginFailure( String messageXML )
                            throws SAXException, XMLParsingException, IOException {
        Reader reader = new StringReader( messageXML );
        Document doc = XMLTools.parse( reader );
        String message = XMLTools.getRequiredNodeAsString( doc.getDocumentElement(),
                                                           "/ServiceExceptionReport/ServiceException",
                                                           nsContext );
        gotoErrorPage( message );
    }

    /**
     * validates the passed event to be valid agaist the requirements of the listener (contains user
     * name and password)
     * 
     * @param event
     * @return true if the request is valide
     */
    private boolean validateRequest( RPCWebEvent event ) {
        RPCMethodCall mc = event.getRPCMethodCall();
        if ( mc.getParameters().length == 0 ) {
            return false;
        }
        RPCStruct struct = (RPCStruct) mc.getParameters()[0].getValue();
        if ( struct.getMember( "NAME" ) == null ) {
            return false;
        }
        if ( struct.getMember( "PASSWORD" ) == null ) {
            return false;
        }
        return true;
    }

    private String getAddress() {
        HttpSession session = ( (HttpServletRequest) getRequest() ).getSession( true );
        ViewContext vc = (ViewContext) session.getAttribute( Constants.CURRENTMAPCONTEXT );
        GeneralExtension ge = vc.getGeneral().getExtension();
        BaseURL baseUrl = ge.getAuthentificationSettings().getAuthentificationURL();
        return NetWorker.url2String( baseUrl.getOnlineResource() );
    }

    /**
     * peforms a login by requesting a session ID from a WAAS like service (deegre SessionServlet).
     * The returned session ID is assigned to a session at the WAAS like service to enable vice
     * versa to identify the user through the ID.
     * 
     * @param event
     * @return a string array containing session id [0] and user name [1], if login was successful
     * @throws InvalidParameterValueException 
     * @throws LoginFailureException if user/passwd is not known to the system.
     * @throws IOException 
     */
    private String[] performLogin( RPCWebEvent event )
                            throws InvalidParameterValueException, LoginFailureException, IOException {
        RPCMethodCall mc = event.getRPCMethodCall();
        RPCStruct struct = (RPCStruct) mc.getParameters()[0].getValue();
        String name = RPCUtils.getRpcPropertyAsString( struct, "NAME" );
        String password = RPCUtils.getRpcPropertyAsString( struct, "PASSWORD" );
        if ( name == null || name.length() == 0 ) {
            throw new InvalidParameterValueException( "Invalid user name: " + name );
        }
        if ( password == null || password.length() == 0 ) {
            throw new InvalidParameterValueException( "Invalid user password" );
        }

        // create request against WAS
        StringBuffer sb = new StringBuffer( 500 );
        String address = OWSUtils.validateHTTPGetBaseURL( getAddress() );
        sb.append( address );
        sb.append( "SERVICE=WAS&VERSION=1.0.0&REQUEST=GetSession&" );
        sb.append( "AUTHMETHOD=urn:x-gdi-nrw:authnMethod:1.0:password&CREDENTIALS=" );
        sb.append( name ).append( ',' ).append( password );
        URL url = new URL( sb.toString() );

        // if the user is not known to the WAS an Exception is thrown at
        // org.deegree.security.drm.SQLRegistry.getUserByName(SQLRegistry.java:276)
        // "ERROR: Lookup of user 'sadfas' failed! A user with this name does not exist."
        // this Exception message gets transformed at 
        // org.deegree.ogcwebservices.wass.was.WAService.doService(WAService.java:188)
        // to something like "ERROR: WAService: There was an internal error in the security system."
        
        // connect WAS to aquire a seesion ID
        NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), url );
        byte[] b = nw.getDataAsByteArr( 50 );
        String response = new String( b );
        
        // the bytearray and the new String() may contain an xml exception message:
        // <?xml version="1.0" encoding="ISO-8859-1"?>
        // <ServiceExceptionReport>
        //     <ServiceException locator="-">WAService: There was an internal error in the security system.</ServiceException>
        // </ServiceExceptionReport>

        if ( response.contains( "ServiceExceptionReport" ) ) {
            throw new LoginFailureException( response );
        }

        // return session id and user name if login was successful
        String[] res = new String[2];
        res[0] = response;
        res[1] = name;
        return res;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:

 $Log: LoginListener.java,v $
 Revision 1.11  2006/11/30 13:50:02  mays
 change handleLoginFailure() according to changes in performLogin()

 Revision 1.10  2006/11/30 12:22:29  mays
 change handling of login failure in performLogin();
 clean up javadocs and format code
 
 Revision 1.7  2006/08/29 08:16:47  taddei
 forcing base url to end with ? (attempt at bug fix)

 Revision 1.6  2006/08/09 14:01:33  poth
 bug fix - support for GDINRW Access Control 1.0 / references to deprecated class Debug removed / Logger added

 Revision 1.5  2006/07/13 08:10:56  poth
 file header added / references to Debug.XXXX removed

 Revision 1.4  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */
