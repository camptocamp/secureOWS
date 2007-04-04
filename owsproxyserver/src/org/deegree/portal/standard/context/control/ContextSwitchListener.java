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

package org.deegree.portal.standard.context.control;

import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.portal.Constants;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.WebMapContextFactory;
import org.deegree.portal.context.XMLFactory;

/**
 * This class handles switch of map contexts. The basic logic is (1) receiving an rpc request with a
 * context name (xml) and, possibly, a bounding box, (2) transforming this xml using a provided xsl,
 * and (3) forwarding the result back to the browser. <br/> Most of the action takes place in
 * <code>doTransformContext</code>, and is delegated to the <code>ContextTransformer</code>.<br/>
 * In order to perform the transformation from a context xml to a html, a xslt is provided. This is
 * per default called <code>context2HTML.xsl</code> (see the class member
 * <code>DEFAULT_CTXT2HTML</code>) and should be put under
 * <code>${context-home}/WEB-INF/xml/</code>. <br/>
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: mays $
 * 
 * @version 1.0. $Revision: 1.18 $, $Date: 2006/11/14 10:19:08 $
 * 
 */
public class ContextSwitchListener extends AbstractContextListener {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static final ILogger LOG = LoggerFactory.getLogger( ContextSwitchListener.class );

    /**
     * A <code>String</code> used as a key value for the new html (of the client). This key is
     * used in the JSP which output the new(-ly transformed html.
     */
    public static final String NEW_CONTEXT_HTML = "NEW_CONTEXT_HTML";

    /**
     * A <code>String</code> defining the name of the xsl file that defines the transformation
     * from a context to html. This must be placed, together with the map context xml and helper xsl
     * files, under <code>${context-home}/WEB-INF/xml/</code>.
     */
    protected static final String DEFAULT_CTXT2HTML = "WEB-INF/conf/igeoportal/context2HTML.xsl";

    /**
     * script to transform a standard WMC document into a deegree WMC
     */
    protected static final String WEBMAPCTXT2HTML = "WEB-INF/conf/igeoportal/defaultcontext.xsl";

    /**
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    public void actionPerformed( FormEvent event ) {
        

        RPCMethodCall mc = ( (RPCWebEvent) event ).getRPCMethodCall();
        RPCParameter[] pars = mc.getParameters();
        RPCStruct struct = (RPCStruct) pars[0].getValue();

        // get map context value
        String curContxt = RPCUtils.getRpcPropertyAsString( struct, "mapContext" );

        // now get bbox
        Envelope bbox = null;
        RPCMember rpcStruct = struct.getMember( Constants.RPC_BBOX );
        if ( rpcStruct != null ) {
            RPCStruct bboxStruct = (RPCStruct) rpcStruct.getValue();
            bbox = extractBBox( bboxStruct );
        }

        // get the servlet path using the session
        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession();

        // path to context dir
        String path2Dir = getHomePath();

        // context and xsl files
        String mapContext = "file://" + path2Dir + "WEB-INF/conf/igeoportal/" + curContxt;
        String xslFilename = "file://" + path2Dir + DEFAULT_CTXT2HTML;

        String newHtml = null;
        String sid = null;
        try {
            // read session ID trying different possible sources
            sid = readSessionID( struct );

            // ContextSwitchLister.actionPerformed is the first action that
            // will be performed if a user enter iGeoPortal. So store the
            // users sessionID into his HTTP session for futher usage
            session.setAttribute( "SESSIONID", sid );
            // if no sessionID is available the context will be read as
            // anonymous user which will cause that layers assigned to a
            // authorized user may will not be parsed correctly
            XMLFragment xml = getContext( mapContext, bbox, sid );
            newHtml = doTransformContext( xslFilename, xml );
        } catch ( Exception e ) {
            LOG.logDebug( e.getMessage(), e );
            gotoErrorPage( StringTools.stackTraceToString( e ) );
            return;
        }

        session.setAttribute( NEW_CONTEXT_HTML, newHtml );                

        // need to keep a reference to the last context...
        // often used when changing/saving the shown context
        try {
            writeContextToSession( mapContext, sid );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            gotoErrorPage( StringTools.stackTraceToString( e ) );
        }

        
    }

    /**
     * writes the context the user choosed to the users session. It can be accessed using
     * <tt>Constants.CURRENTMAPCONTEXT</tt> key value
     * 
     * @param context
     * @throws Exception
     */
    protected void writeContextToSession( String context, String sessionID )
                            throws Exception {
        
        URL ctxtUrl = new URL( context );
        ViewContext vc = WebMapContextFactory.createViewContext( ctxtUrl, null, sessionID );
        HttpSession session = ( (HttpServletRequest) getRequest() ).getSession();
        session.setAttribute( Constants.CURRENTMAPCONTEXT, vc );
        
    }

    /**
     * returns the context to be used as a String
     * 
     * @param context
     * @param bbox
     * @return
     * @throws Exception
     */
    protected XMLFragment getContext( String context, Envelope bbox, String sessionID )
                            throws Exception {
        
        XMLFragment xml = new XMLFragment();
        try {
            LOG.logInfo( "reading context: " + context );
            URL ctxtUrl = new URL( context );
            if ( bbox == null ) { 
                // no bbox, do it as usual
                xml.load( ctxtUrl );
            } else {
                ViewContext vc = WebMapContextFactory.createViewContext( ctxtUrl, null, sessionID );
                changeBBox( vc, bbox );
                xml = XMLFactory.export( vc );
            }
            // if at least one element is present we have a deegree Web Map Context
            // document; otherwise we have a standard Web Map Context document that
            // must be transformed into a deegree WMC document
            List nl = XMLTools.getNodes( xml.getRootElement().getOwnerDocument(),
                                         "cntxt:ViewContext/cntxt:General/cntxt:Extension/dgcntxt:IOSettings",
                                         nsContext );
            if ( nl.size() == 0 ) {
                xml = transformToDeegreeContext( xml );
            }
        } catch ( Exception e ) {
            throw e;
        }
        
        return xml;
    }

    /**
     * transforms a standard Web Map Context document to a deegree Web Map Context document
     * 
     * @param doc
     * @return
     * @throws Exception
     */
    private XMLFragment transformToDeegreeContext( XMLFragment xml )
                            throws Exception {
        
        String xslFilename = getHomePath() + WEBMAPCTXT2HTML;
        File file = new File( xslFilename );
        XSLTDocument xslt = new XSLTDocument();
        xslt.load( file.toURL() );
        xml = xslt.transform( xml );
        
        return xml;
    }

    /**
     * Transforms the context pointed to by <code>context</code> into html using
     * <code>xsltURL</code> (though this is currently fixed; there's really no need to define
     * one's wn xsl).
     * 
     * @param context
     *            the context xml
     * @param xsl
     *            the transformation xml
     */
    protected String doTransformContext( String xsl, XMLFragment xml ) {

        StringWriter sw = new StringWriter( 60000 );
        try {
            XSLTDocument xslt = new XSLTDocument();
            xslt.load( new URL( xsl ) );
            xml = xslt.transform( xml );
            xml.write( sw );
        } catch ( MalformedURLException e1 ) {
            LOG.logError( e1.getMessage(), e1 );
            gotoErrorPage( "<b>Error creating the context URL: </b>" + e1.getMessage() + "<br/>"
                           + StringTools.stackTraceToString( e1 ) );

        } catch ( TransformerException e1 ) {
            LOG.logError( e1.getMessage(), e1 );
            gotoErrorPage( "<b>Error transforming the context: </b>" + e1.getMessage() + "<br/>"
                           + StringTools.stackTraceToString( e1 ) );

        } catch ( Exception e1 ) {
            LOG.logError( e1.getMessage(), e1 );
            gotoErrorPage( "<b>Error in context transformer: </b>" + e1.getMessage() + "<br/>"
                           + StringTools.stackTraceToString( e1 ) );
        }
        return sw.toString();
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ContextSwitchListener.java,v $
 Revision 1.18  2006/11/14 10:19:08  mays
 restore changes of revision 1.16 as they where overwritten in 1.17:
 made writeContextToSession() and getContext() protected to use in subclass

 Revision 1.17  2006/11/10 15:51:12  plum
 ap: fixed session handling

 Revision 1.15  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.14  2006/08/29 20:18:09  poth
 code for initial accessing a session ID added

 Revision 1.13  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.12  2006/08/29 19:18:16  poth
 reading session ID from RPC centralized

 Revision 1.11  2006/08/27 07:51:04  poth
 *** empty log message ***

 Revision 1.10  2006/08/24 17:03:47  poth
 pass seesionid if available to WebMapContextFactory

 Revision 1.9  2006/08/24 12:15:52  poth
 Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)

 Revision 1.8  2006/05/22 09:30:35  poth
 *** empty log message ***

 Revision 1.7  2006/05/16 15:56:36  poth
 code cleanup


 ********************************************************************** */