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
package org.deegree.portal.portlet.modules.actions;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.WebMapContextFactory;

/**
 * 
 * 
 *
 * @version $Revision: 1.15 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.15 $, $Date: 2006/10/12 15:46:19 $
 *
 * @since 2.0
 */
public class IGeoPortalPortletPerform extends AbstractPortletPerform {
    
    private static final ILogger LOG = LoggerFactory.getLogger( IGeoPortalPortletPerform.class );
    
    public static String PARAM_BBOX = "BBOX";
    public static String PARAM_LAYER = "LAYER";
    public static String PARAM_MAPMODE = "MAPMODE";
    
    protected ServletContext sc = null;

    public IGeoPortalPortletPerform(HttpServletRequest request, Portlet portlet, 
                                    ServletContext sc) {
        super(request, portlet);
        this.sc = sc;
    }
   
    /**
     * updates the view context with the current bounding box, the list of
     * visible layers and the current map mode
     * 
     * @param pid MapWindowPortlet id
     * @throws PortalException
     */
    public void updateContext() {
        
        // update the view contexts bounding box with the current one.           
        setBoundingBoxFromBBOXParam();
        
        // update the view contexts list of visible layers
        setLayers();
        
        // update the map mode
        setMode();
        
    }   
    
    /**
     * sets layers of the view context as visible or invisble depending on
     * the incoming request
     * 
     * @param pid MapWindowPortlet id
     * @throws PortalException
     */
    public void setLayers() {  
                
        if ( parameter.get( PARAM_BBOX ) != null && parameter.get( "LAYERS" ) != null ) { 
            // just change layerlist if the request contains a BBOX parameter
            // and at least one layer because other wise it will be the initial call;
            ViewContext vc = getCurrentViewContext( portlet.getID() );
            Layer[] layers = vc.getLayerList().getLayers();
            String ly = (String)parameter.get( "LAYERS" );        
            StringBuffer sb = new StringBuffer(100);
            for ( int i = 0; i < layers.length; i++ ) {
                sb.append( layers[i].getName() ).append( '|' );
                sb.append( layers[i].getServer().getOnlineResource() );
                if ( ly.indexOf( sb.toString() ) > -1 ) {
                    layers[i].setHidden( false );                    
                } else {
                    layers[i].setHidden( true );
                }            
                sb.delete( 0, sb.length() );
            }
            
            setCurrentMapContext( vc, portlet.getID() ); 
        }
        
    }
    
    /**
     * writes the current map mode (if set) into the users WMC.
     * 
     *  @param pid MapWindowPortlet id
     */
    public void setMode() {
        String mm = (String)parameter.get( PARAM_MAPMODE );
        if ( mm != null ) {
            ViewContext vc = getCurrentViewContext( portlet.getID() );
            vc.getGeneral().getExtension().setMode( mm );
            setCurrentMapContext( vc, portlet.getID() );
        } 
    }
    
    /**
     * sets a new bounding box of the map read from the the request object
     * passed when initialized an instance of <code>MapWindowPortletPerfom</code>
     * 
     * @param pid MapWindowPortlet id
     */
    public void setBoundingBoxFromBBOXParam() {
        String bbox = (String)parameter.get( PARAM_BBOX );       
        setBoundingBox(  bbox );
    }
    
    /**
     * sets a new bounding box of the map read from the the request object
     * passed when initialized an instance of <code>MapWindowPortletPerfom</code>
     * 
     * @param pid MapWindowPortlet id
     * @param env new bounding box
     */
    public void setBoundingBox(Envelope env) {
          
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        if ( vc != null) {
            CoordinateSystem crs = vc.getGeneral().getBoundingBox()[0].getCoordinateSystem();
            Point[] pt = new Point[2];
            pt[0] = GeometryFactory.createPoint( env.getMin().getX(), env.getMin().getY(), crs );
            pt[1] = GeometryFactory.createPoint( env.getMax().getX(), env.getMax().getY(), crs );
            try {
                vc.getGeneral().setBoundingBox( pt );
            } catch ( ContextException should_never_happen ) {}
        }
        setCurrentMapContext( vc, portlet.getID() );        
    }
    
    /**
     * the method expects a string with four comma seperated coordinate values. 
     * The created box will be written
     *  
     * @param pid MapWindowPortlet id
     * @param bbox
     * @throws PortalException
     * @throws ContextException
     */
    public void setBoundingBox(String bbox) {
     
        ViewContext vc = getCurrentViewContext( portlet.getID() );
        if ( bbox != null && vc != null) {
            double[] coords = StringTools.toArrayDouble( bbox, "," );        
            CoordinateSystem crs = 
                vc.getGeneral().getBoundingBox()[0].getCoordinateSystem();
            Point[] pt = new Point[2];
            pt[0] = GeometryFactory.createPoint( coords[0], coords[1], crs );
            pt[1] = GeometryFactory.createPoint( coords[2], coords[3], crs );
            try {
                vc.getGeneral().setBoundingBox( pt );
            } catch ( ContextException should_never_happen ) {}
            setCurrentMapContext( vc, portlet.getID() );
        }        
    }
    
    /**
     * returns the current @see ViewContext read from the portlet session.
     * 
     * @param session
     * @param pid MapWindowPortlet id
     * @param 
     * 
     * @return
     */
    public static ViewContext getCurrentViewContext(HttpSession session, String pid)  {
        return (ViewContext)session.getAttribute( pid + '_' + CURRENT_WMC );
    }
    
    /**
     * returns the current @see ViewContext read from the portlet session.
     *  
     * @param pid MapWindowPortlet id
     * @param 
     * 
     * @return
     */
    public ViewContext getCurrentViewContext(String pid)  {
        return getCurrentViewContext( request.getSession(), pid );
    }
    
    /**
     * sets the current MapContext to the users session 
     * 
     * @param session
     * @param vc
     * @param pid
     */
    public static void setCurrentMapContext(HttpSession session, ViewContext vc, String pid) {
        session.setAttribute( pid + '_' + CURRENT_WMC, vc );
    }
    
    /**
     * sets the current MapContext to the users session
     * 
     * @param vc
     * @param pid
     */
    public void setCurrentMapContext(ViewContext vc, String pid) {
        setCurrentMapContext( request.getSession(), vc, pid );
    }
    
    /**
     * writes the name of the current WMC into the users session 
     * @param session
     * @param pid
     * @param name
     */
    public static void setCurrentMapContextName(HttpSession session, String pid, String name) {
        session.setAttribute( pid + '_' + CURRENT_WMC_NAME, name );
    }
    
    /**
     * writes the name of the current WMC into the users session
     * @param pid
     * @param name
     */
    public void setCurrentMapContextName(String pid, String name) {
        setCurrentMapContextName( request.getSession(), pid, name );
    }
    
    /**
     * returns the name of the current WMC into the users session
     * @param session
     * @param pid
     * @return
     */
    public static String getCurrentMapContextName(HttpSession session, String pid) {
        return (String)session.getAttribute( pid + '_' + CURRENT_WMC_NAME );
    }
    
    /**
     * returns the name of the current WMC into the users session
     * @param pid
     * @return
     */
    public String getCurrentMapContextName(String pid) {
        return getCurrentMapContextName( request.getSession(), pid );
    }
   
    /**
     * returns an instance of @see ViewContext read from the portlet session.
     * If no instance is available <code>null</code> will be returned.
     * 
     * @param session
     * @param name map context name/id
     * @param 
     * 
     * @return
     */
    public static ViewContext getNamedViewContext(HttpSession session, String name)  {
        URL url = (URL)session.getAttribute( SESSION_VIEWCONTEXT + name );
        ViewContext vc = null;
        try {
            if ( url != null ) {
                vc = WebMapContextFactory.createViewContext( url, null, null );
            }
        } catch ( Exception e ) {
            // should never happen
            LOG.logError( e.getMessage(), e );
        }
        
        return vc;
    }
        
    /**
     * returns an instance of @see ViewContext read from the portlet session.
     * If no instance is available <code>null</code> will be returned.
     * 
     *  @param name map context name/id
     * 
     * @return
     */
    public ViewContext getNamedViewContext(String name)  {
        return getNamedViewContext( request.getSession(), name );
    }
   
    /**
     * writes the URL to a WMC with a assigend name into a users session
     * @param session
     * @param name
     * @param url
     */
    public static void setNameContext(HttpSession session, String name, URL url) {
        session.setAttribute( SESSION_VIEWCONTEXT + name, url );
    }
    
    /**
     * writes the URL to a WMC with a assigend name into a users session
     * @param name
     * @param url
     */
    public void setNameContext(String name, URL url) {
        setNameContext( request.getSession(), name, url );
    }
    

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IGeoPortalPortletPerform.java,v $
Revision 1.15  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.14  2006/08/29 19:54:14  poth
footer corrected

Revision 1.13  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
