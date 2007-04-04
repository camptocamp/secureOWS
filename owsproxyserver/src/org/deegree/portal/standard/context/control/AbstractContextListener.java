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
 53177 Bonn
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
package org.deegree.portal.standard.context.control;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcbase.BaseURL;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.portal.Constants;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.General;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.ViewContext;
import org.xml.sax.SAXException;

/**
 * This exception shall be thrown when a session(ID) will be used that has been expired.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mays $
 * 
 * @version 1.1, $Revision: 1.18 $, $Date: 2006/11/30 11:54:21 $
 * 
 * @since 1.1
 */
abstract public class AbstractContextListener extends AbstractListener {
    
    private static ILogger LOG = LoggerFactory.getLogger( AbstractContextListener.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * gets the user name assigned to the passed session ID from an authentication service. If no
     * user is assigned to the session ID <tt>null</tt> will be returned. If the session is closed
     * or expired an exception will be thrown
     * 
     * @param sessionId
     * @return name of the user assigned to the passed session ID
     * @throws XMLParsingException 
     * @throws SAXException 
     * @throws IOException 
     */
    protected String getUserName( String sessionId )
                            throws XMLParsingException, IOException, SAXException {

        HttpSession session = ( (HttpServletRequest) getRequest() ).getSession( true );
        ViewContext vc = (ViewContext) session.getAttribute( Constants.CURRENTMAPCONTEXT );
        if( vc == null ){
            return null;
        }
        GeneralExtension ge = vc.getGeneral().getExtension();
        String userName = null;
        if ( sessionId != null && ge.getAuthentificationSettings() != null) {
            LOG.logDebug( "try getting user from WAS/sessionID" );
            BaseURL baseUrl = ge.getAuthentificationSettings().getAuthentificationURL();
            String url = OWSUtils.validateHTTPGetBaseURL( baseUrl.getOnlineResource().toExternalForm() );
            StringBuffer sb = new StringBuffer( url );
            sb.append( "request=DescribeUser&SESSIONID=" ).append( sessionId );

            XMLFragment xml = new XMLFragment();
            xml.load( new URL( sb.toString() ) );

            userName = XMLTools.getRequiredNodeAsString( xml.getRootElement(), "/User/UserName",
                                                         nsContext );
        } else {
            LOG.logDebug( "try getting user from getUserPrincipal()" );
            if ( ( (HttpServletRequest) getRequest() ).getUserPrincipal() != null ) {
                userName = ( (HttpServletRequest) getRequest() ).getUserPrincipal().getName();
                if ( userName.indexOf( "\\" ) > 1 ) {
                    String[] us = StringTools.toArray( userName, "\\", false );
                    userName = us[us.length-1];
                }
            }
        }
        LOG.logDebug( "userName: " + userName );
        return userName;
    }
    
    /**
     * reads the users session ID.<br>
     * first the PRC will be parsed for a 'sessionID' element. If not present
     * the sessionID will be read from the users session. If even the user's 
     * HTTP session does not contain a sessionID, it will be tried to get it
     * from the WAS registered to the current context. If no WAS available
     * <code>null</code> will be returned.  
     * 
     * @param struct 
     * @return the users session id
     * @throws IOException 
     * @throws SAXException 
     * @throws XMLParsingException 
     */
    protected String readSessionID( RPCStruct struct )
                            throws XMLParsingException, SAXException, IOException {
        String sid = RPCUtils.getRpcPropertyAsString( struct, "sessionID" );
        if ( sid == null ) {
            LOG.logDebug( "try getting sessionID from HTTP session" );
            HttpSession session = ( (HttpServletRequest) getRequest() ).getSession();
            sid = (String) session.getAttribute( "SESSIONID" );
        }
        if ( sid == null ) {
            // try get SessionID from WAS if user name is available
            // in this case it is assumed that a user's name can be determined
            // evaluating the requests userPrincipal that will be available if
            // the user has been logged in to the the server (or network)
            String userName = getUserName( null );
            if ( userName != null ) {
                LOG.logDebug( "try getting sessionID by authorizing current user: " + userName );
                HttpSession session = ( (HttpServletRequest) getRequest() ).getSession( true );
                ViewContext vc = (ViewContext) session.getAttribute( Constants.CURRENTMAPCONTEXT );
                GeneralExtension ge = vc.getGeneral().getExtension();
                if ( ge.getAuthentificationSettings() != null ) {
                    BaseURL baseUrl = ge.getAuthentificationSettings().getAuthentificationURL();                    
                    StringBuffer sb = new StringBuffer( 500 );
                    String addr = baseUrl.getOnlineResource().toExternalForm();
                    sb.append( OWSUtils.validateHTTPGetBaseURL( addr ) );
                    sb.append( "SERVICE=WAS&VERSION=1.0.0&REQUEST=GetSession&" );
                    sb.append( "AUTHMETHOD=urn:x-gdi-nrw:authnMethod:1.0:password&CREDENTIALS=" );
                    sb.append( userName );
                    LOG.logDebug( "authentificat user: ",  sb.toString() );                    
                    HttpClient client = new HttpClient();
                    GetMethod meth = new GetMethod( sb.toString() );
                    client.executeMethod( meth );
                    sid = meth.getResponseBodyAsString();                
                    session.setAttribute( "SESSIONID", sid );
                }
            }
        }
        LOG.logDebug( "sessionID: " + sid );
        return sid;
    }

    /**
     * Convenience method to extract the boundig box from an rpc fragment.
     * 
     * @param bboxStruct
     *            the <code>RPCStruct</code> containing the bounding box. For example,
     *            <code>&lt;member&gt;&lt;name&gt;boundingBox&lt;/name&gt;etc...</code>.
     * 
     * @return an envelope with the boundaries defined in the rpc structure
     */
    protected Envelope extractBBox( RPCStruct bboxStruct ) {

        Double minx = (Double) bboxStruct.getMember( Constants.RPC_BBOXMINX ).getValue();
        Double miny = (Double) bboxStruct.getMember( Constants.RPC_BBOXMINY ).getValue();
        Double maxx = (Double) bboxStruct.getMember( Constants.RPC_BBOXMAXX ).getValue();
        Double maxy = (Double) bboxStruct.getMember( Constants.RPC_BBOXMAXY ).getValue();

        Envelope bbox = GeometryFactory.createEnvelope( minx.doubleValue(), miny.doubleValue(),
                                                        maxx.doubleValue(), maxy.doubleValue(),
                                                        null );
        return bbox;
    }

    /**
     * changes the bounding box of a given view context
     * 
     * @param vc
     *            the view context to be changed
     * @param bbox
     *            the new bounding box
     * @throws PortalException 
     */
    public static final void changeBBox( ViewContext vc, Envelope bbox )
                            throws PortalException {
        General gen = vc.getGeneral();

        CoordinateSystem cs = gen.getBoundingBox()[0].getCoordinateSystem();
        Point[] p = new Point[] { GeometryFactory.createPoint( bbox.getMin(), cs ),
                                 GeometryFactory.createPoint( bbox.getMax(), cs ) };
        try {
            gen.setBoundingBox( p );
        } catch ( ContextException e ) {
            throw new PortalException( "Error setting new BBOX \n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        }
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:

$Log: AbstractContextListener.java,v $
Revision 1.18  2006/11/30 11:54:21  mays
change request in getUserName from "&ID=" to "&SESSIONID="

Revision 1.17  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.16  2006/10/05 15:07:46  mays
bug fix : validateHTTPGetBaseURL

Revision 1.15  2006/09/12 13:59:25  poth
*** empty log message ***

Revision 1.14  2006/09/01 11:24:17  taddei
fixed NPE in getUserName

Revision 1.13  2006/08/30 11:51:50  poth
support for sessionID authentification/authorization added

Revision 1.12  2006/08/30 08:36:46  poth
bug fix - set logger to be static

Revision 1.11  2006/08/29 20:18:09  poth
code for initial accessing a session ID added

Revision 1.10  2006/08/29 19:54:14  poth
footer corrected

Revision 1.9  2006/08/29 19:18:16  poth
reading session ID from RPC centralized

Revision 1.8  2006/08/24 16:25:09  poth
parsing of DescribeUser response adapted

Revision 1.7  2006/06/22 06:55:02  poth
enabled reading user principals having '\' in its name by just using the part after the last '\'

Revision 1.6  2006/05/23 14:27:49  poth
support for UserPrincipal added if no sessionId is set

********************************************************************** */