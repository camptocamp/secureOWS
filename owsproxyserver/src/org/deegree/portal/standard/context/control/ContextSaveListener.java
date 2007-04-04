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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/

package org.deegree.portal.standard.context.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.deegree.framework.xml.XMLFragment;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.portal.Constants;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.Server;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.XMLFactory;
import org.w3c.dom.Document;

/**
 * This class saves a new context based on changes made by the
 * user (on the client) and based on the original context xml.
 * <br/>
 * Files are saved under .../WEB-INF/xml/users/some_user, where
 * some_user is passed as an RPC parameter. Files should be saved with
 * .xml extension becuase the default load context listener class looks 
 * up those files.
 * <br/>
 * Currently this class is only channing the bounding box and the layers
 * visibility.
 *   
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 */
public class ContextSaveListener extends AbstractContextListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( ContextSaveListener.class );

    private static String userDir = "WEB-INF/conf/igeoportal/users/";

    private static String contextDir = "WEB-INF/conf/igeoportal/";

    /** 
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    public void actionPerformed( FormEvent event ) {

        RPCWebEvent rpc = (RPCWebEvent) event;
        try {
            validate( rpc );
        } catch ( PortalException e ) {
            gotoErrorPage( "Not a valid RPC for ContextSave <br/>" + e.getMessage() );
            return;
        }

        String newContext = null;
        try {
            newContext = storeContext( rpc );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Context could not be saved <br/>" + e.getMessage() );
            return;
        }

        // forward to new page
        this.getRequest().setAttribute( Constants.MESSAGE, "Saved context: " + newContext );

    }

    /**
     * stores the current context of the user with a defined name
     * 
     * @param event
     * @return name of the context that has been stored
     */
    private String storeContext( RPCWebEvent event )
                            throws Exception {

        RPCMethodCall mc = event.getRPCMethodCall();
        RPCParameter[] pars = mc.getParameters();
        RPCStruct struct = (RPCStruct) pars[0].getValue();

        // read base context
        StringBuffer path2Dir = new StringBuffer( getHomePath() );
        path2Dir.append( contextDir );

        // access base context
        HttpSession session = ( (HttpServletRequest) getRequest() ).getSession();
        ViewContext vc = (ViewContext) session.getAttribute( Constants.CURRENTMAPCONTEXT );
        // change values: BBOX and Layer List
        Envelope bbox = extractBBox( (RPCStruct) struct.getMember( Constants.RPC_BBOX ).getValue() );
        changeBBox( vc, bbox );
        RPCMember[] layerList = createLayerList( (RPCStruct) struct.getMember( "layerList" ).getValue() );
        changeLayerList( vc, layerList );

        // save new context
        // get map context value
        String username = "default";
        try {     
            String sid = RPCUtils.getRpcPropertyAsString( struct, "sessionID" );
            LOG.logDebug( "sessionID ", sid );
            username = getUserName( sid );
            if ( username == null ) {
                username = "default";
            }
            LOG.logDebug( "username ", username );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        String newContext = RPCUtils.getRpcPropertyAsString( struct, "newContext" );
        path2Dir = new StringBuffer( getHomePath() );
        path2Dir.append( userDir );
        path2Dir.append( username );
        File file = new File( path2Dir.toString() );
        if ( !file.exists() ) {
            // create directory if not exists
            file.mkdir();
        }
        path2Dir.append( "/" );
        path2Dir.append( newContext );

        saveDocument( vc, path2Dir.toString() );

        return newContext;
    }

    /**
     *  saves the new context as xml 
     */
    public static final void saveDocument( ViewContext vc, String filename )
                            throws PortalException {
        try {
            XMLFragment xml = XMLFactory.export( vc );
            FileOutputStream fos = new FileOutputStream( filename );
            xml.write( fos );
            fos.close();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new PortalException( "could not save file '" + filename + "'\n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new PortalException( "could not save file '" + filename + "'\n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        } catch ( ParserConfigurationException e ) {
            e.printStackTrace();
            throw new PortalException( "could not save file '" + filename + "'\n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     *  validates the incoming RPC event 
     */
    private void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        RPCMember username = struct.getMember( "sessionID" );
        if ( username == null ) {
            throw new PortalException( "missing parameter 'sessionID' in RPC for ContextSave" );
        }
        RPCMember newContext = struct.getMember( "newContext" );
        if ( newContext == null ) {
            throw new PortalException( "missing parameter 'newContext' in RPC for ContextSave" );
        }
        RPCMember layerList = struct.getMember( "layerList" );
        if ( layerList == null ) {
            throw new PortalException( "missing parameter 'layerList' in RPC for ContextSave" );
        }
        // TODO validate box: should do this in a common (static) method
        // for many listeners that need a bbox
    }

    /**
     * changes the layer list of the ViewContext vc according to the information
     * contined in the rpcLayerList 
     */
    private void changeLayerList( ViewContext vc, RPCMember[] rpcLayerList )
                            throws PortalException {
        LayerList layerList = vc.getLayerList();
        ArrayList nLayers = new ArrayList( rpcLayerList.length );

        // stores visibility vals
        HashMap layersMap = new HashMap( rpcLayerList.length );
        // stores order vals
        String[] ordered = new String[rpcLayerList.length];
        String[] types = new String[rpcLayerList.length];
        String[] names = new String[rpcLayerList.length];
        String[] addr = new String[rpcLayerList.length];

        // this is needed to keep layer order
        // order is correct in rpc call JavaScript) but get lost in translation...
        for ( int i = 0; i < rpcLayerList.length; i++ ) {
            String[] v = StringTools.toArray( (String) rpcLayerList[i].getValue(), "|", false );
            String n = rpcLayerList[i].getName();
            layersMap.put( n, v[0] );
            int cc = Integer.valueOf( v[1] ).intValue();
            ordered[cc] = n;
            types[cc] = v[2];
            names[cc] = v[3];
            addr[cc] = v[4];
        }

        for ( int i = 0; i < rpcLayerList.length; i++ ) {

            String n = ordered[i];
            boolean isVisible = Boolean.valueOf( ( (String) layersMap.get( n ) ) ).booleanValue();
            Layer l = layerList.getLayer( n );
            if ( l != null ) {
                // needed to reconstruct new layer order
                // otherwise layer order is still from original context
                l.setHidden( !isVisible );
            } else {

                if ( layerList.getLayers().length == 0 ) {
                    //FIXME is this Exception Correct                    
                    throw new PortalException( "LayerList is empty for required mapcontext" );
                }

                Layer p = layerList.getLayers()[0];
                // a new layer must be created because it is not prsent
                // in the current context. This is the case if the client
                // has loaded an additional WMS
                String[] tmp = StringTools.toArray( types[i], " ", false );
                try {
                    WMSCapabilitiesDocument doc = new WMSCapabilitiesDocument();
                    doc.load( new URL( addr[i] ) );
                    OGCCapabilities capa = doc.parseCapabilities();
                    Server server = new Server( names[i], tmp[1], tmp[0], new URL( addr[i] ), capa );
                    l = new Layer( server, n, n, "", p.getSrs(), null, null, p.getFormatList(),
                                   p.getStyleList(), false, false, null );
                } catch ( Exception e1 ) {
                    throw new PortalException( StringTools.stackTraceToString( e1 ) );
                }
            }
            nLayers.add( l );
        }
        try {
            nLayers.trimToSize();
            Layer[] ls = new Layer[nLayers.size()];
            ls = (Layer[]) nLayers.toArray( ls );
            vc.setLayerList( new LayerList( ls ) );
        } catch ( ContextException e ) {
            throw new PortalException( "Error setting new layer list \n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        }
    }

    /** 
     * common method to save xml
     */
    protected static void internalSave( OutputStream os, Document doc )
                            throws PortalException {
        try {
            Source source = new DOMSource( doc );
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( source, new StreamResult( os ) );
        } catch ( Exception e ) {
            throw new PortalException( "Error saving context xml \n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        }

    }

    /** 
     * creates a layer list as RPCMember[] from an RPC struct.
     * this method might change to accomodate for others layer props
     */
    private RPCMember[] createLayerList( RPCStruct rpcStruc ) {
        RPCMember[] ls = rpcStruc.getMembers();
        return ls;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ContextSaveListener.java,v $
 Revision 1.13  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.12  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.11  2006/08/24 16:30:44  poth
 method extracted to RPCUtil

 Revision 1.10  2006/08/20 20:53:54  poth
 changes rquired as a consequence of bug fix in wmc implementation/handling. Instead of determining the correct URL as given in a services capabilities deegree always has used the base URL which is just guarenteed to be valid for GetCapabilities requests.

 Revision 1.9  2006/06/29 10:29:37  poth
 *** empty log message ***

 Revision 1.8  2006/05/23 14:27:49  poth
 support for UserPrincipal added if no sessionId is set

 Revision 1.7  2006/05/16 15:56:36  poth
 code cleanup


 ********************************************************************** */