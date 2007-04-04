//$Header$
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.DefaultMapModelAccess;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.MapModelAccess;
import org.deegree.portal.context.Server;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.AbstractPortletPerform;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * 
 *
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 *
 * @version 1.0. $Revision$, $Date$
 *
 * @since 2.0
 */
public class MapWindowPortletPerform extends IGeoPortalPortletPerform {
    
    private static final ILogger LOG = LoggerFactory.getLogger(  MapWindowPortletPerform.class );
    
    // init parameter of the portlet    
    static String INIT_INITAL_GETMAP = "initialGETMAP";
    static String INIT_PANBUTTONS = "panButtons";
    static String INIT_HOMEBBOX = "homeBoundingBox";
    static String INIT_SCALEMAPTOPORTLETSIZE = "scaleMapToPortletSize";
    static String INIT_FEATUREINFOTARGETPORTLET = "featureInfoTargetPortlet";    
    
    // known request parameter    
    static String PARAM_WIDTH = "WIDTH";
    static String PARAM_HEIGHT = "HEIGHT";
    static String PARAM_CLICKPOINT = "CLICKPOINT";
    static String PARAM_MAPPOINT = "MAPPOINT";
    static String PARAM_FACTOR = "FACTOR";
    static String PARAM_PANDIRECTION = "DIRECTIONCODE";
    static String PARAM_CURRENTFILAYER = "CURRENTFILAYER";
    
    // session attributes
    static String SESSION_HISTORY = "HISTORY";
    static String SESSION_HISTORYPOSITION = "HISTORYPOSITION";
        
    /**
     * private constructor
     * @param request
     * @param portlet
     */
    MapWindowPortletPerform(HttpServletRequest request, Portlet portlet, ServletContext sc) {
        super( request, portlet, sc );
    }
    
    @Override
    public void buildNormalContext() throws PortalException {
        super.buildNormalContext();
        init();        
        setBoundingBoxFromBBOXParam();
        setMapWindowAttributes();
    }

    /**
     * initializes the portlet by reading the assigned map context and storing 
     * it into the users session
     * @throws PortletException
     */
    private void init() {
        
        List<Envelope> history = 
            (List<Envelope>)request.getSession().getAttribute( SESSION_HISTORY );
        if ( history == null ) {
            history = new ArrayList( 1000 );
            request.getSession().setAttribute( SESSION_HISTORY, history );
            request.getSession().setAttribute( SESSION_HISTORYPOSITION, 0 );
        }
        
    }
   
    /**
     * writes the passed values into the attributes of the passed 
     * @see HttpServletRequest to be read by the JSP page assigned to
     * the iGeoPortal:MapWindowPortlet
     * 
     */
    private void setAttributes(ArrayList panButtons) {        
        request.setAttribute( "PANBUTTONS", panButtons );
        request.setAttribute( "PORTLETID", portlet.getID() );
        request.setAttribute( "FEATUREINFOTARGETPORTLET", 
                              getInitParam( INIT_FEATUREINFOTARGETPORTLET ) );
    }
    
    /**
     * sets all request attributes required by a map from the passed ViewContext 
     * @param vc
     */
    private void setMapWindowAttributes() {

        String tmp = getInitParam( INIT_PANBUTTONS );
        ArrayList pB = new ArrayList(20);
        if ( tmp != null ) {
            String[] panButtons = StringTools.toArray( tmp, ",;", true );
            List list = Arrays.asList( panButtons );            
            pB.addAll( list );
        }
                        
        setAttributes( pB );
    }
    
    /**
     * sets a new bounding box of the map read from the the request object
     * passed when initialized an instance of <code>MapWindowPortletPerfom</code>
     */
    void setHomeBoundingbox() {
        
       Envelope env = 
            (Envelope)request.getSession().getAttribute( AbstractPortletPerform.SESSION_HOME );         
        setBoundingBox( env );
        
    }
    
    /**
     * sets a new size of the map read from the request
     * @throws PortalException
     */
    void setMapSize() throws PortalException {
        String tmp = (String)parameter.get( PARAM_WIDTH );
        if ( tmp == null ) {
            throw new PortalException( "request does not contain a WIDTH parameter!" );
        }
        int width = Integer.parseInt( tmp );
        
        tmp = request.getParameter( PARAM_HEIGHT );
        if ( tmp == null ) {
            throw new PortalException( "request does not contain a HEIGHT parameter!" );
        }
        int height = Integer.parseInt( tmp );
        
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        MapModelAccess mma = new DefaultMapModelAccess( vc );
        vc = mma.setMapSize( width, height );
        // if the request contains a BBOX parameter the current bounding box
        // will be updated too 
        setBoundingBox( (String)parameter.get( PARAM_BBOX ) );
        setCurrentMapContext( vc, portlet.getID() );
    }
    
    /**
     * zooms the current map by a factor and recenters it to the point the 
     * user has clicked to.
     * 
     * @throws PortalException
     * @throws ContextException
     */
    void zoom() throws PortalException, ContextException {
        
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        MapModelAccess mma = new DefaultMapModelAccess( vc );
        
        String tmp =  (String)parameter.get( PARAM_FACTOR );
        if ( tmp == null ) {
            throw new PortalException( "FACTOR must be set for zoom operation!" );
        }
        double factor = Double.parseDouble( tmp );
        
        tmp = (String)parameter.get( PARAM_CLICKPOINT );        
        if ( tmp != null ) {
            // zoom by pixel coordinates
            double coords[] = StringTools.toArrayDouble( tmp, "," );
            java.awt.Point point = new java.awt.Point( (int)coords[0], (int)coords[1] );
            vc = mma.zoom( point, factor );
        } else {
            // zoom by map coordinates
            tmp = (String)parameter.get( PARAM_MAPPOINT );
            if ( tmp == null ) {
                throw new PortalException( "at least a CLICKPOINT or a MAPPOINT " +
                        "must be defined for zoom operation!" );
            }
            double coords[] = StringTools.toArrayDouble( tmp, "," );
            Point point = GeometryFactory.createPoint( coords[0], coords[1], null );
            vc = mma.zoom( point, factor );
        }
        setCurrentMapContext( vc, portlet.getID() );
    }
    
    /**
     * pan the map with a set factor to one of eight possible directions
     * 
     * @throws PortalException
     * @throws ContextException
     */
    void pan() throws PortalException, ContextException {
                
        String tmp =  (String)parameter.get( PARAM_FACTOR );
        if ( tmp == null ) {
            throw new PortalException( "FACTOR must be set for pan operation!" );
        }
        double factor = Double.parseDouble( tmp );
        
        tmp =  (String)parameter.get( PARAM_PANDIRECTION );
        if ( tmp == null ) {
            throw new PortalException( "DIRECTION must be set for pan operation!" );
        }
        boolean isnumber = true;
        try {
            Double.parseDouble( tmp );
        } catch ( Exception e ) {
            isnumber = false;
        }
        
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        MapModelAccess mma = new DefaultMapModelAccess( vc );
        if ( isnumber ) {
            // use value as direction in degree
            vc = mma.pan( Double.parseDouble( tmp ), factor );
        } else {
            int dir = 0;
            if ( tmp.equals( "N" ) ) {
                dir = MapModelAccess.PAN_NORTH;
            } else if ( tmp.equals( "NW" ) ) {
                dir = MapModelAccess.PAN_NORTHWEST;
            } else if ( tmp.equals( "NE" ) ) {
                dir = MapModelAccess.PAN_NORTHEAST;
            } else if ( tmp.equals( "W" ) ) {
                dir = MapModelAccess.PAN_WEST;
            } else if ( tmp.equals( "E" ) ) {
                dir = MapModelAccess.PAN_EAST;
            } else if ( tmp.equals( "SW" ) ) {
                dir = MapModelAccess.PAN_SOUTHWEST;
            } else if ( tmp.equals( "S" ) ) {
                dir = MapModelAccess.PAN_SOUTH;
            } else if ( tmp.equals( "SE" ) ) {
                dir = MapModelAccess.PAN_SOUTHEAST;
            } else {
                throw new PortalException( "unknown pan direction: " + tmp );
            }
            vc = mma.pan( dir, factor );
        }
        setCurrentMapContext( vc, portlet.getID() );
    }
    
    /**
     * recenters the current map to the point the user has clicked to
     * @throws PortalException
     * @throws ContextException
     */
    void recenter() throws PortalException, ContextException {
     
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        MapModelAccess mma = new DefaultMapModelAccess( vc );
                
        String tmp = (String)parameter.get( PARAM_CLICKPOINT );        
        if ( tmp != null ) {
            // zoom by pixel coordinates
            double coords[] = StringTools.toArrayDouble( tmp, "," );
            java.awt.Point point = new java.awt.Point( (int)coords[0], (int)coords[1] );
            vc = mma.recenterMap( point );
        } else {
            // zoom by map coordinates
            tmp = (String)parameter.get( PARAM_MAPPOINT );
            if ( tmp == null ) {
                throw new PortalException( "at least a CLICKPOINT or a MAPPOINT " +
                        "must be defined for zoom operation!" );
            }
            double coords[] = StringTools.toArrayDouble( tmp, "," );
            Point point = GeometryFactory.createPoint( coords[0], coords[1], null );
            vc = mma.recenterMap( point );
        }
        setCurrentMapContext( vc, portlet.getID() );
    }
    
    /**
     * sets the name of the the layers that are activated for feature 
     * info requests in the uses WMC
     */
    void setCurrentFILayer() {
        String tmp = (String)parameter.get( PARAM_CURRENTFILAYER );
        String[] fiLayer = StringTools.toArray( tmp, ",", false );
        if ( fiLayer != null ) {
            List list = Arrays.asList( fiLayer );
            list = new ArrayList( list );
            
            ViewContext vc = getCurrentViewContext( portlet.getID() );
            LayerList layerList = vc.getLayerList();
            Layer[] layers = layerList.getLayers();
            for (int i = 0; i < layers.length; i++) {
                try {
                    if ( list.contains( layers[i].getName() ) ) {
                        layers[i].getExtension().setSelectedForQuery( true );
                    } else {
                        layers[i].getExtension().setSelectedForQuery( false );
                    }
                } catch ( Exception e ) {
                    // TODO: handle exception
                    LOG.logError( e.getMessage(), e );
                }
            }
        }
    }
    
    /**
     * moves the layer passed through by the HTTP request up for one position
     * 
     * @throws PortalException
     */
    void moveUp() throws PortalException {
                       
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        String tmp = (String)parameter.get( PARAM_LAYER );
        String[] s = StringTools.toArray( tmp, "|", false );
        LOG.logDebug( StringTools.concat( 150, "moving layer: ", s[0], " map model: ", 
                                          portlet.getID(), " down" ) );
        MapModelAccess mma = new DefaultMapModelAccess( vc );
        try {
            vc = mma.swapLayers( new QualifiedName( s[0] ), new URL( s[1] ), "OGC:WMS", true );
        } catch ( MalformedURLException e ) {
            throw new PortalException( "no valid URL", e );
        }         
        setCurrentMapContext( vc, portlet.getID() );
    }
    
    /**
     * moves the layer passed through by the HTTP request down for one position
     * 
     * @throws PortalException
     */
    void moveDown() throws PortalException {
        ;
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        String tmp = (String)parameter.get( PARAM_LAYER );
        String[] s = StringTools.toArray( tmp, "|", false );        
        LOG.logDebug( StringTools.concat( 150, "moving layer: ", s[0], " map model: ", 
                                          portlet.getID(), " down" ) );
        MapModelAccess mma = new DefaultMapModelAccess( vc );
        try {
            vc = mma.swapLayers( new QualifiedName( s[0] ), new URL( s[1] ), "OGC:WMS", false );
        } catch ( MalformedURLException e ) {
            throw new PortalException( "no valid URL", e );
        } 
        setCurrentMapContext( vc, portlet.getID() );
    }
    
    /**
     * move the map view (just bounding box) to the next entry in the history 
     */
    void doHistoryforward() {
        
        List<Envelope> history = 
            (List<Envelope>)request.getSession().getAttribute( SESSION_HISTORY );
        int p = (Integer)request.getSession().getAttribute( SESSION_HISTORYPOSITION );
        LOG.logDebug( "stepping forward in hinstory to position: " + (p+1) );
        if ( p < history.size() - 1) {
            Envelope env = history.get( ++p ); 
            setBoundingBox( env );
            request.getSession().setAttribute( SESSION_HISTORYPOSITION, p );
        }
    }
    
    /**
     * move the map view (just bounding box) to the previous entry in the history 
     */
    void doHistorybackward() {
        List<Envelope> history = 
            (List<Envelope>)request.getSession().getAttribute( SESSION_HISTORY );
        int p = (Integer)request.getSession().getAttribute( SESSION_HISTORYPOSITION );
        LOG.logDebug( "stepping backward in hinstory to position: " + (p-1) );
        if ( p > 0 ) {
            Envelope env = history.get( --p ); 
            setBoundingBox( env );
            request.getSession().setAttribute( SESSION_HISTORYPOSITION, p );
        }
    }
    
    /**
     * adds a new OWS to one of the WMC available a MapWindow
     * TODO
     */
    void doAddows() {
        System.out.println( parameter );        
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        Layer layer = vc.getLayerList().getLayers()[0];
        parameter.get( "FORMAT" );
        // TODO
    }
    
    /**
     * removes an OWS from a MapWindow
     *
     */
    void doRemoveows() {
        System.out.println( parameter );
        String title = (String)parameter.get( "TITLE" );
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        Layer[] layers = vc.getLayerList().getLayers();
        for ( int i = 0; i < layers.length; i++ ) {
            Server server = layers[i].getServer();
            if ( server.getTitle().equals( title ) ) {
                vc.getLayerList().removeLayer( layers[i].getName() );
            }
        }
    }
   
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.27  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.26  2006/08/29 19:54:13  poth
footer corrected

Revision 1.25  2006/07/06 11:41:49  poth
bug fix - moving layer up/down using more than on map context

Revision 1.24  2006/07/06 09:59:04  poth
bug fix - settig correct ID of current map context using more than one WMC (SelectWMCPortlet)

Revision 1.23  2006/06/09 07:25:05  poth
*** empty log message ***

Revision 1.22  2006/06/08 11:33:30  poth
*** empty log message ***

Revision 1.21  2006/06/07 12:19:38  poth
*** empty log message ***

Revision 1.20  2006/06/06 19:49:25  poth
handling of mapmodel changed - it now will be available for each MapWindowPortlet through the session

Revision 1.19  2006/05/13 16:53:52  poth
*** empty log message ***

Revision 1.18  2006/05/12 14:36:28  poth
*** empty log message ***

Revision 1.17  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.16  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.15  2006/03/22 13:22:14  poth
*** empty log message ***

Revision 1.14  2006/03/04 20:36:18  poth
*** empty log message ***

Revision 1.13  2006/02/28 08:47:38  poth
*** empty log message ***

Revision 1.12  2006/02/27 21:58:57  poth
*** empty log message ***

Revision 1.11  2006/02/20 09:28:15  poth
*** empty log message ***

Revision 1.10  2006/02/14 16:05:05  poth
*** empty log message ***

Revision 1.9  2006/02/13 14:13:05  poth
*** empty log message ***

Revision 1.8  2006/02/10 12:14:08  poth
*** empty log message ***

Revision 1.7  2006/02/10 11:48:50  poth
*** empty log message ***

Revision 1.6  2006/02/07 19:52:44  poth
*** empty log message ***

Revision 1.5  2006/02/07 17:10:18  poth
*** empty log message ***

Revision 1.4  2006/02/07 07:58:56  poth
history function implemented

Revision 1.3  2006/02/06 21:46:19  poth
*** empty log message ***

Revision 1.2  2006/02/05 20:33:09  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.10  2006/01/09 07:47:09  ap
*** empty log message ***



********************************************************************** */