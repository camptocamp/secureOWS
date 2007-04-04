//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/MapActionPortletAction.java,v 1.19 2006/08/29 19:54:13 poth Exp $
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

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.modules.actions.portlets.JspPortletAction;
import org.apache.jetspeed.portal.Portlet;
import org.apache.turbine.util.RunData;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.portal.PortalException;

/**
 * 
 *
 * @version $Revision: 1.19 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.19 $, $Date: 2006/08/29 19:54:13 $
 *
 * @since 2.0
 */
public class MapActionPortletAction extends JspPortletAction {

    private ILogger LOG = LoggerFactory.getLogger( MapActionPortletAction.class );

    /**
     * 
     */
    protected void buildNormalContext( Portlet portlet, RunData data )
                            throws Exception {

        try {
            MapActionPortletPerform mapp = new MapActionPortletPerform( data.getRequest(), portlet,
                                                                        data.getServletContext() );
            mapp.buildNormalContext();
            data.getRequest().setAttribute( "User", data.getUser().getUserName() );
            data.getRequest().setAttribute( "Password", data.getUser().getPassword() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * returns the MapWindowPortlet targeted by the current action
     * @param rundata
     * @param portlet
     * @return
     */
    private Portlet getMapWindowPortlet( RunData rundata, Portlet portlet ) {
        Map map = KVP2Map.toMap( rundata.getRequest() );
        String id = (String) map.get( "MAPPORTLET" );
        Portlet port = portlet.getPortletConfig().getPortletSet().getPortletByID( id );
        return port;
    }

    /**
     * 
     * @param data
     * @param portlet
     * @throws Exception
     */
    public void doFeatureinfo( RunData data, Portlet portlet )
                            throws Exception {

        try {
            HttpServletRequest req = data.getRequest();
            req.setAttribute( "$U$", data.getUser().getUserName() );
            req.setAttribute( "$P$", data.getUser().getPassword() );
            Portlet port = getMapWindowPortlet( data, portlet );
            FeatureInfoPortletPerform fipp = 
                new FeatureInfoPortletPerform( req, port, data.getServletContext() );
            fipp.doGetFeatureInfo();            
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param data
     * @param portlet
     * @throws Exception
     */
    public void doFeatureinfoForward( RunData data, Portlet portlet )
                            throws Exception {

        try {
            Portlet port = getMapWindowPortlet( data, portlet );
            String className = portlet.getPortletConfig().getInitParameter( "performingClass" );
            Class[] classes = new Class[3];
            classes[0] = data.getRequest().getClass();
            classes[1] = port.getClass();
            classes[2] = data.getRequest().getClass();
            Object[] o = new Object[3];
            o[0] = data.getRequest();
            o[1] = portlet;
            o[2] = data.getRequest();

            Class clss = Class.forName( className );
            Constructor constructor = clss.getConstructor( classes );
            constructor.newInstance( o );

            FeatureInfoForwardPortletPerform fifpp = 
                (FeatureInfoForwardPortletPerform) constructor.newInstance( o );
            fifpp.doGetFeatureInfo();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    /**
     * sets a new bounding box for the current ViewContext. 
     * 
     * @param data
     * @param portlet
     * @throws Exception
     */
    public void doSetboundingbox( RunData data, Portlet portlet ) {
        try {
            Portlet port = getMapWindowPortlet( data, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( data.getRequest(), port,
                                                                        data.getServletContext() );
            mwpp.setBoundingBoxFromBBOXParam();
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * sets the maps boundingbox to the defined home boundingbox
     * @param data
     * @param portlet
     */
    public void doSethomeboundingbox( RunData data, Portlet portlet ) {
        try {
            Portlet port = getMapWindowPortlet( data, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( data.getRequest(), port,
                                                                        data.getServletContext() );
            mwpp.setHomeBoundingbox();
            //mwpp.setLayers( port.getID() );
            //mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    /**
     * sets a new size of the map (pixel) 
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doSetmapsize( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setMapSize();
            mwpp.setBoundingBoxFromBBOXParam();
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * performs a repaint of the current ViewContext by regenerating the assigend
     * OWS requests. In opposite to  @see #doActualizeViewContext(RunData, Portlet)
     * no parameter frm the client are solved to actualize the current ViewContext
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doRepaint( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setBoundingBoxFromBBOXParam();
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
            mwpp.setMode();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * performs a zoomIn or a zoomOut on the current map model (ViewContext)
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doZoom( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.zoom();
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * performs a panning on the current map model (ViewContext)
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doPan( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.pan();
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * performs a recentering on the current map model (ViewContext)
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doRecenter( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.recenter();
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * sets layers of the view context as visible or invisble depending on
     * the incoming request
     * 
     * @param rundata
     * @param portlet
     * 
     * @throws PortalException
     */
    public void doSetlayers( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setLayers();
            mwpp.setBoundingBoxFromBBOXParam();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * moves the layer passed through by the HTTP request up for one position 
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doMoveup( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setLayers();
            mwpp.moveUp();
            mwpp.setBoundingBoxFromBBOXParam();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * moves the layer passed through by the HTTP request down for one position 
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doMovedown( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setLayers();
            mwpp.moveDown();
            mwpp.setBoundingBoxFromBBOXParam();
            mwpp.setCurrentFILayer();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * move the map view (just bounding box) to the next entry in the history 
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doHistoryforward( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
            mwpp.doHistoryforward();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * move the map view (just bounding box) to the previous entry in the history 
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doHistorybackward( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.setLayers();
            mwpp.setCurrentFILayer();
            mwpp.doHistorybackward();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doAddows( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.doAddows();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param rundata
     * @param portlet
     * @throws Exception
     */
    public void doRemoveows( RunData rundata, Portlet portlet )
                            throws Exception {
        try {
            Portlet port = getMapWindowPortlet( rundata, portlet );
            MapWindowPortletPerform mwpp = new MapWindowPortletPerform( rundata.getRequest(), port,
                                                                        rundata.getServletContext() );
            mwpp.doRemoveows();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * selects the current context of a MapWindowPortlet
     * @param data
     * @param portlet
     * @throws PortalException
     */
    public void doSelectwmc( RunData data, Portlet portlet )
                            throws PortalException {
        try {
            Portlet port = 
                portlet.getPortletConfig().getPortletSet().getPortletByName("iGeoPortal:SelectWMCPortlet");
            SelectWMCPortletPerform swp = new SelectWMCPortletPerform( data.getRequest(), port,
                                                                       data.getServletContext() );
            swp.doSelectwmc();
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
            throw new PortalException( e.getMessage() );
        }
    }
    
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: MapActionPortletAction.java,v $
 Revision 1.19  2006/08/29 19:54:13  poth
 footer corrected

 Revision 1.18  2006/08/09 16:17:08  poth
 bug fix - pssing correct portlet instance at doSelectWMC

 Revision 1.17  2006/07/06 09:59:04  poth
 bug fix - settig correct ID of current map context using more than one WMC (SelectWMCPortlet)

 Revision 1.16  2006/06/09 15:10:42  poth
 *** empty log message ***

 Revision 1.15  2006/06/07 12:19:38  poth
 *** empty log message ***

 Revision 1.14  2006/06/06 19:02:00  poth
 initial upload of SelectWMC portlet

 Revision 1.13  2006/05/13 16:53:52  poth
 *** empty log message ***

 Revision 1.12  2006/05/12 14:36:28  poth
 *** empty log message ***

 Revision 1.11  2006/04/06 20:25:21  poth
 *** empty log message ***

 Revision 1.10  2006/03/31 13:16:50  poth
 *** empty log message ***

 Revision 1.9  2006/03/30 21:20:23  poth
 *** empty log message ***

 Revision 1.8  2006/03/22 13:22:14  poth
 *** empty log message ***

 Revision 1.7  2006/03/04 20:36:18  poth
 *** empty log message ***

 Revision 1.6  2006/02/27 21:58:57  poth
 *** empty log message ***

 Revision 1.5  2006/02/27 17:11:38  poth
 *** empty log message ***

 Revision 1.4  2006/02/14 16:05:05  poth
 *** empty log message ***

 Revision 1.3  2006/02/13 14:13:05  poth
 *** empty log message ***

 Revision 1.2  2006/02/06 21:46:19  poth
 *** empty log message ***

 Revision 1.1  2006/02/05 09:30:11  poth
 *** empty log message ***

 Revision 1.1  2006/01/09 07:47:09  ap
 *** empty log message ***


 ********************************************************************** */