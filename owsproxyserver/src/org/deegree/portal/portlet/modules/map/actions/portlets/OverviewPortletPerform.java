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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.framework.util.StringTools;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * handles actions performed on a map overview
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
public class OverviewPortletPerform extends IGeoPortalPortletPerform {
    
    protected static String PARAM_OVRECTCOLOR = "rectColor";
    protected static String PARAM_OVWIDTH = "width";
    protected static String PARAM_OVHEIGHT = "height";
    protected static String PARAM_OVTITLE = "title";
    protected static String PARAM_OVFOOTER = "footer";
    protected static String PARAM_OVSRC = "imageSource";

    /**
     * 
     * @param request
     * @param portlet
     * @param sc
     */
    public OverviewPortletPerform(HttpServletRequest request, Portlet portlet, ServletContext sc) {
        super(request, portlet, sc);
    }

    @Override
    public void buildNormalContext() throws PortalException {
        super.buildNormalContext();
        
        // static bbox of the map overview 
        String tmp = getInitParam( PARAM_BBOX );
        double[] bbox = StringTools.toArrayDouble( tmp, "," );

        ViewContext vc = getCurrentViewContext( getInitParam( INIT_MAPPORTLETID ) );
        
        Point[] env = null;
        if ( vc != null ) { 
            env = vc.getGeneral().getBoundingBox();
        } else {
            // use overviews bbox if no map model is available
            env[0] = GeometryFactory.createPoint( bbox[0], bbox[1], null );
            env[1] = GeometryFactory.createPoint( bbox[2], bbox[3], null );
        }
        
        String rectColor = getInitParam( PARAM_OVRECTCOLOR );
        if ( rectColor == null ) {
            // default color = red
            rectColor = "#FF0000";
        }
        tmp = getInitParam( PARAM_OVWIDTH );
        if ( tmp == null ) {
            tmp = "150";
        }
        int width = new Integer( tmp );
        tmp = getInitParam( PARAM_OVHEIGHT );
        if ( tmp == null ) {
            tmp = "150";
        }
        int height = new Integer( tmp );
        String title = getInitParam( PARAM_OVTITLE );
        String footer = getInitParam( PARAM_OVFOOTER );
        String src = getInitParam( PARAM_OVSRC );
        
        request.setAttribute( "TITLE", title );
        request.setAttribute( "FOOTER", footer );
        request.setAttribute( "RECTCOLOR", rectColor );
        request.setAttribute( "WIDTH", width );
        request.setAttribute( "HEIGHT", height );
        request.setAttribute( "SRC", src );        
        request.setAttribute( "MINX", bbox[0] );
        request.setAttribute( "MINY", bbox[1] );
        request.setAttribute( "MAXX", bbox[2] );
        request.setAttribute( "MAXY", bbox[3] );
        request.setAttribute( "VIEWMINX", env[0].getX() );
        request.setAttribute( "VIEWMINY", env[0].getY() );
        request.setAttribute( "VIEWMAXX", env[1].getX() );
        request.setAttribute( "VIEWMAXY", env[1].getY() );
    }
    
    
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.10  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.9  2006/08/29 19:54:13  poth
footer corrected

Revision 1.8  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.6  2006/03/04 20:36:18  poth
*** empty log message ***

Revision 1.5  2006/02/27 21:58:57  poth
*** empty log message ***

Revision 1.4  2006/02/20 09:28:15  poth
*** empty log message ***

Revision 1.3  2006/02/07 19:52:44  poth
*** empty log message ***

Revision 1.2  2006/02/07 17:10:03  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.1  2006/01/09 07:47:09  ap
*** empty log message ***


********************************************************************** */