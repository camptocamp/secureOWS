//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/FeatureInfoPortletPerform.java,v 1.24 2006/10/26 08:54:26 poth Exp $
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
package org.deegree.portal.portlet.modules.map.actions.portlets;

import java.awt.Color;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * 
 *
 * @version $Revision: 1.24 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.24 $, $Date: 2006/10/26 08:54:26 $
 *
 * @since 2.0
 */
public class FeatureInfoPortletPerform extends IGeoPortalPortletPerform {

    private static final ILogger LOG = LoggerFactory.getLogger( FeatureInfoPortletPerform.class );

    protected static String PARAM_FILAYERS = "FILAYERS";

    protected static String PARAM_LAYERS = "LAYERS";

    protected static String PARAM_BBOX = "BBOX";

    protected static String PARAM_X = "X";

    protected static String PARAM_Y = "Y";

    protected static String PARAM_FEATURECOUNT = "FEATURECOUNT";

    protected static String SESSION_INITPARAM = "FIINITPARAM";

    protected static String INIT_PATHTOXSLT = "pathToXSLT";
    
    private Map initParams = null;

    /**
     * @param request
     * @param portlet
     * @param servletContext will be needed to evaluate the absolut path of the
     *                      transform script(s)
     */
    public FeatureInfoPortletPerform( HttpServletRequest request, Portlet portlet, ServletContext sc ) {
        super( request, portlet, sc );
        initParams = (Map)request.getSession().getAttribute( SESSION_INITPARAM );
    }

    /**
     * initializes the portlet by putting the init parameters to the users 
     * session. Even if this is not absolutly neccessary it simplifies a
     * few things ...
     *
     */
    public void init() {
        if ( request.getSession().getAttribute( SESSION_INITPARAM ) == null ) {
            Map map = portlet.getPortletConfig().getInitParameters();
            request.getSession().setAttribute( SESSION_INITPARAM, map );
        }
    }

    /**
     * performs a GetFeatureInfo/GetFeature/DescribeCoverage request depending
     * on the layer typs the request targets. If defiend the result will be transformed 
     * by a XSLT script to get a human readable out put.
     * 
     * @return
     */
    protected void doGetFeatureInfo()
                            throws PortalException, OGCWebServiceException {
        
        String tmp = (String) parameter.get( PARAM_FILAYERS );
        if ( tmp == null || tmp.length() == 0 ) {
            throw new PortalException( "at least one layer/featuretype/coverage must be set" );
        }
        String[] layers = StringTools.toArray( tmp, ",", true );

        setCurrentFILayer( layers );

        // a get feature info request only can be performed if a viewcontext
        // has been initialized before by the MapWindowPortletAction
        ViewContext vc = getCurrentViewContext( (String)initParams.get( INIT_MAPPORTLETID ) );
        if ( vc == null ) {
            throw new PortalException( "no valid view context available through users session" );
        }

        // synchronize list of visible layer and BBOX with the users view context
        // because maybe the user had changed the visible layers before performing 
        // a GetFeatureInfo request
        updateContext();

        Layer layer = null;
        Layer former = null;
        List layerList = new ArrayList( 50 );
        StringBuffer sb = new StringBuffer( 20000 );
        
        // performe a feature info request every time the hosting
        // server changes. This is required because maybe a user has
        // selected two or more layers hosted by different OWS
        for ( int i = 0; i < layers.length; i++ ) {
            former = layer;
            layer = vc.getLayerList().getLayer( layers[i] );
            if ( i > 0 ) {
                if ( layer.getServer().equals( former.getServer() ) ) {
                    layerList.add( layer );
                } else {
                    sb.append( perform( layerList, vc ) );
                    layerList.clear();
                    layerList.add( layer );
                }
            } else {
                layerList.add( layer );
            }
        }
        sb.append( perform( layerList, vc ) );
        vc.getGeneral().getExtension().setMode( "FEATUREINFO" );
        
        setCurrentMapContext( vc, (String)initParams.get( INIT_MAPPORTLETID ) );

        // the result will be available through the forwarded request as well as
        // through the users session (the portal frontend deciceds what behavior
        // it should have)
        request.setAttribute( "HTML", sb.toString() );
        request.getSession().setAttribute( "HTML", sb.toString() ); 

    }

    /**
     * sets the name of the the layers that are activated for feature 
     * info requests in the uses WMC
     */
    void setCurrentFILayer( String[] fiLayer ) {

        List list = Arrays.asList( fiLayer );
        list = new ArrayList( list );

        ViewContext vc = getCurrentViewContext( (String)initParams.get( INIT_MAPPORTLETID ) );
        LayerList layerList = vc.getLayerList();
        Layer[] layers = layerList.getLayers();
        for ( int i = 0; i < layers.length; i++ ) {
            if ( list.contains( layers[i].getName() ) ) {
                layers[i].getExtension().setSelectedForQuery( true );
            } else {
                layers[i].getExtension().setSelectedForQuery( false );
            }
        }

    }

    /**
     * distributes the performance of the feature info requests depending on
     * the requested service to a specialized method
     * 
     * @param layerList list of context layers provided by the same OWS
     * @param vc
     * @return
     */
    protected String perform( List layerList, ViewContext vc )
                            throws OGCWebServiceException, PortalException {

        Layer layer = (Layer) layerList.get( 0 );
        if ( layer.getServer().getService().indexOf( "WMS" ) > -1 ) {
            return performWMS( layerList, vc );
        } else if ( layer.getServer().getService().indexOf( "WFS" ) > -1 ) {
            throw new PortalException( "WFS is not supported as feature info target yet!" );
        } else if ( layer.getServer().getService().indexOf( "WCS" ) > -1 ) {
            throw new PortalException( "WCS is not supported as feature info target yet!" );
        } else {
            throw new PortalException( "not supported service: " + layer.getServer().getService()
                                       + " as feature info target!" );
        }

    }

    /**
     * performes a GetFeatureInfo request on a WMS and transforms the result using
     * a XSLT script defined in the portlets init-parameters
     * 
     * @param layerList list of context layers provided by the same WMS
     * @param vc
     * @return
     */
    private String performWMS( List layerList, ViewContext vc )
                            throws OGCWebServiceException, PortalException {

        GetFeatureInfo gfi = createGetFeatureInfoRequest( layerList, vc );
        Layer layer = (Layer) layerList.get( 0 );

        URL url = OWSUtils.getHTTPGetOperationURL( layer.getServer().getCapabilities(), 
                                               GetFeatureInfo.class );
        String href = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() );
        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( href ).append( gfi.getRequestParameter() );
        // If a user is registered to the portal use his name and password to
        // perform GetFeatureInfo request because maybe the connected server
        // is hidden behind a owsProxy 
        // TODO
        // read informations too which server user name and password shall be send
        // TODO
        // replace sending user name and password by a sessionID
        if ( !"anon".equals( request.getAttribute( "$U$" ) ) ) {
            sb.append( "&user=" ).append( request.getAttribute( "$U$" ) );
            request.removeAttribute( "$U$" );
        }
        if ( request.getAttribute( "$P$" ) != null ) {
            sb.append( "&password=" ).append( request.getAttribute( "$P$" ) );
            request.removeAttribute( "$P$" );
        }
        LOG.logDebug( "info request: ", sb );

        XMLFragment frag = null;
        try {
            frag = new XMLFragment( new URL( sb.toString() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new OGCWebServiceException( "could not perform GetFeatureInfo request "
                                              + e.getMessage() );
        }
        
        LOG.logDebug( "path to XSLT:", initParams );

        String path = getPathToXSLTScript( layer, href );

        path = sc.getRealPath( "/WEB-INF/" + path );

        Source xmlSource = new DOMSource( frag.getRootElement() );
        Source xslSource = new StreamSource( new File( path ) );
        StringWriter sw = new StringWriter( 20000 );
        
        String s = null;
        try {
            XSLTDocument.transform( xmlSource, xslSource, new StreamResult( sw ), null, null );
            s = sw.getBuffer().toString();
            sw.close();
        } catch ( Exception e ) {
            throw new PortalException( "could not transform GetFeatureInfo result of request: "
                                       + sb + " - " + StringTools.stackTraceToString( e ) );
        }

        return s;

    }

    /**
     * returns the path to the xslt script to be used for transforming
     * info data 
     * 
     * @param layer
     * @param server
     * @return
     * @throws PortalException
     */
    private String getPathToXSLTScript( Layer layer, String server )
                            throws PortalException {
        StringBuffer sbb = new StringBuffer( 1000 );
        sbb.append( INIT_PATHTOXSLT ).append( ';' ).append( server ).append( ';' );
        sbb.append( layer.getServer().getService() );
        String path = (String) initParams.get( sbb.toString() );
        if ( path == null ) {
            sbb.delete( 0, sbb.length() );
            sbb.append( INIT_PATHTOXSLT ).append( ';' );
            sbb.append( layer.getServer().getService() );
            path = (String) initParams.get( sbb.toString() );
        }
        if ( path == null ) {
            path = (String) initParams.get( INIT_PATHTOXSLT );
        }
        if ( path == null ) {
            // for being compliant with an error in init-param that
            // has been used in several instances
            sbb = new StringBuffer( 1000 );
            sbb.append( "pathToXLST" ).append( ';' ).append( server ).append( ';' );
            sbb.append( layer.getServer().getService() );
            path = (String) initParams.get( sbb.toString() );
            if ( path == null ) {
                sbb.delete( 0, sbb.length() );
                sbb.append( "pathToXLST" ).append( ';' );
                sbb.append( layer.getServer().getService() );
                path = (String) initParams.get( sbb.toString() );
            }
            if ( path == null ) {
                path = (String) initParams.get( "pathToXLST" );
            }
        }

        if ( path == null ) {
            LOG.logDebug( "initParams: ", initParams );
            throw new PortalException( "no XSLT script defined for processing GetFeatureInfo "
                                       + "response of:" + INIT_PATHTOXSLT + ';' + server + ';'
                                       + layer.getServer().getService() );
        }
        return path;
    }

    /**
     * creates a GetFeatureInfo request from the requested target layers depending
     * on the current view context
     * 
     * @param layerList
     * @param vc
     * @return
     * @throws OGCWebServiceException
     */
    private GetFeatureInfo createGetFeatureInfoRequest( List layerList, ViewContext vc ) {

        String[] fiLayers = new String[layerList.size()];
        GetMap.Layer[] gmLayers = new GetMap.Layer[layerList.size()];
        for ( int i = 0; i < gmLayers.length; i++ ) {
            Layer layer = (Layer) layerList.get( i );
            fiLayers[i] = layer.getName();
            gmLayers[i] = new GetMap.Layer( layer.getName(),
                                            layer.getStyleList().getCurrentStyle().getName() );
        }

        Layer layer = (Layer) layerList.get( 0 );
        Point[] pt = vc.getGeneral().getBoundingBox();
        String srs = pt[0].getCoordinateSystem().getName();
        int width = vc.getGeneral().getWindow().width;
        int height = vc.getGeneral().getWindow().height;
        Envelope bbox = GeometryFactory.createEnvelope( pt[0].getX(), pt[0].getY(), pt[1].getX(),
                                                        pt[1].getY(), pt[0].getCoordinateSystem() );

        // create GetMap request being mandatory part of the GetFeatureInfo request
        GetMap gm = GetMap.create( layer.getServer().getVersion(), "id", gmLayers, null, null,
                                   layer.getFormatList().getCurrentFormat().getName(), width,
                                   height, srs, bbox, false, Color.WHITE, null, null, null, null,
                                   null );

        int x = Integer.parseInt( (String) parameter.get( PARAM_X ) );
        int y = Integer.parseInt( (String) parameter.get( PARAM_Y ) );
        java.awt.Point point = new java.awt.Point( x, y );
        int featureCount = Integer.parseInt( (String) parameter.get( PARAM_FEATURECOUNT ) );
        GetFeatureInfo gfi = GetFeatureInfo.create( layer.getServer().getVersion(), "id", fiLayers,
                                                    gm, "application/vnd.ogc.gml", featureCount,
                                                    point, null, null, null );

        return gfi;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FeatureInfoPortletPerform.java,v $
 Revision 1.24  2006/10/26 08:54:26  poth
 logging statement added

 Revision 1.23  2006/10/17 20:31:17  poth
 *** empty log message ***

 Revision 1.22  2006/10/12 15:46:19  poth
 adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

 Revision 1.21  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.20  2006/08/20 20:53:54  poth
 changes rquired as a consequence of bug fix in wmc implementation/handling. Instead of determining the correct URL as given in a services capabilities deegree always has used the base URL which is just guarenteed to be valid for GetCapabilities requests.

 Revision 1.19  2006/08/07 10:52:20  poth
 not used imports removed

 Revision 1.18  2006/07/06 09:59:04  poth
 bug fix - settig correct ID of current map context using more than one WMC (SelectWMCPortlet)

 Revision 1.17  2006/07/06 08:16:29  poth
 *** empty log message ***

 Revision 1.16  2006/06/07 12:19:38  poth
 *** empty log message ***

 Revision 1.15  2006/05/26 11:09:53  poth
 bug fix -> correct naming for initparameter

 Revision 1.14  2006/05/01 20:15:27  poth
 *** empty log message ***

 Revision 1.13  2006/04/11 15:12:31  poth
 *** empty log message ***

 Revision 1.12  2006/04/11 14:04:25  poth
 *** empty log message ***

 Revision 1.11  2006/04/11 14:03:17  poth
 *** empty log message ***

 Revision 1.10  2006/04/06 20:25:21  poth
 *** empty log message ***

 ********************************************************************** */