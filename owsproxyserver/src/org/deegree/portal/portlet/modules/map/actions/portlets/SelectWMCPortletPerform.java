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

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletSet;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * 
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/11/06 13:53:54 $
 *
 * @since 2.0
 */
public class SelectWMCPortletPerform extends IGeoPortalPortletPerform {
    
    private static ILogger LOG = LoggerFactory.getLogger( SelectWMCPortletPerform.class );
    
    private static String INIT_KEEP_BBOX = "keep_bbox";

    /**
     * 
     * @param request
     * @param portlet
     * @param sc
     */
    public SelectWMCPortletPerform( HttpServletRequest request, Portlet portlet, ServletContext sc ) {
        super( request, portlet, sc );

    }

    @Override
    public void buildNormalContext()
                            throws PortalException {
        super.buildNormalContext();

        if ( request.getSession().getAttribute( CURRENT_WMC ) == null ) {
            List<String[]> wmc = (List<String[]>) request.getSession().getAttribute( AVAILABLE_WMC );
            request.getSession().setAttribute( CURRENT_WMC, wmc.get( 0 )[1] );
        }
    }

    /**
     * selects the current context of a MapWindowPortlet
     * @throws PortalException 
     *
     */
    void doSelectwmc() throws PortalException {

        String cntxid = (String) parameter.get( "WMCID" );

        PortletSet ps = portlet.getPortletConfig().getPortletSet();
        String mapid = ps.getPortletByName( "iGeoPortal:MapActionPortlet" ).getID();
        Portlet port = ps.getPortletByID( mapid );
        port.getPortletConfig().setInitParameter( INIT_WMC, cntxid );

        String mwinid = getInitParam( INIT_MAPPORTLETID );
        port = ps.getPortletByID( mwinid );        
        port.getPortletConfig().setInitParameter( INIT_WMC, cntxid );
        
        request.setAttribute( PARAM_MAPPORTLET, mwinid );
 
        ViewContext vc = null;
        if ( "true".equals( getInitParam( INIT_KEEP_BBOX ) ) ) {
            // get old current context to read its bounding box
            vc = getCurrentViewContext( (String)parameter.get( "MAPPORTLET" ) );
            Point[] currentEnv = vc.getGeneral().getBoundingBox();
            
            // get new current context to set its bounding box with value of the
            // old current context bounding box to keep viewing area
            vc = getNamedViewContext( cntxid );
            try {
                vc.getGeneral().setBoundingBox( currentEnv );
            } catch ( ContextException e ) {
                LOG.logError( e.getMessage(), e );
                throw new PortalException( e.getMessage(), e );
            }
        }
        
        setCurrentMapContext( vc, getInitParam( INIT_MAPPORTLETID ) );
        setCurrentMapContextName( getInitParam( INIT_MAPPORTLETID ), cntxid );

    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: SelectWMCPortletPerform.java,v $
 Revision 1.5  2006/11/06 13:53:54  poth
 bug fix - using correct map portlet ID for accessing WMC

 Revision 1.4  2006/10/12 15:46:19  poth
 adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

 Revision 1.3  2006/08/06 19:52:54  poth
 support for taking bbox from the previous context into the new one added

 Revision 1.2  2006/06/06 19:49:25  poth
 handling of mapmodel changed - it now will be available for each MapWindowPortlet through the session

 Revision 1.1  2006/06/06 19:02:00  poth
 initial upload of SelectWMC portlet


 ********************************************************************** */