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
package org.deegree.portal.portlet.enterprise;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;


/**
 * The servlet will be used to update a map model (Web Map Context) in the 
 * background (using an invisible iframe) from an iGeoPortal Portlet Edition  
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
public class WMCUpdateServlet extends HttpServlet {
    
  
    private static final long serialVersionUID = 2927537039728672671L;
    private static final ILogger LOG = LoggerFactory.getLogger( WMCUpdateServlet.class );
    
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                                                throws ServletException, IOException {
        
        doPost( request, response );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
                                                throws ServletException, IOException {

        Map parameter = KVP2Map.toMap( request );
        
        String mm = (String)parameter.get( "MAPPORTLET" );
        LOG.logDebug( "parameter: " + parameter );        
        IGeoPortalPortletPerform igeo = 
            new IGeoPortalPortletPerform( request, null, getServletContext() );
        try {
            if ( mm != null ) {
               // update bbox
                ViewContext vc = igeo.getCurrentViewContext( mm );
                
                String bbox = (String)parameter.get( IGeoPortalPortletPerform.PARAM_BBOX );
                if ( bbox != null && vc != null) {
                    double[] coords = StringTools.toArrayDouble( bbox, "," );        
                    CoordinateSystem crs = vc.getGeneral().getBoundingBox()[0].getCoordinateSystem();
                    Point[] pt = new Point[2];
                    pt[0] = GeometryFactory.createPoint( coords[0], coords[1], crs );
                    pt[1] = GeometryFactory.createPoint( coords[2], coords[3], crs );
                    try {
                        vc.getGeneral().setBoundingBox( pt );
                    } catch ( ContextException should_never_happen ) {}                    
                }   
                
                if ( bbox != null && parameter.get( "LAYERS" ) != null ) { 
                    // just change layerlist if the request contains a BBOX parameter
                    // and at least one layer because other wise it will be the initial call;
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
                    
                    igeo.setCurrentMapContext( vc, mm );            
                }
            } else {
                System.out.println( "no mapmodel defined in request; " +
                                    "ensure that parameter 'MAPPORTLET' is defined!" );
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.logError( "could not update WMC: " + e.getMessage(), e );
        } 
        request.setAttribute( "ACTION", getInitParameter( "ACTION" ) );
        
        request.getRequestDispatcher( "igeoportal/enterprise.jsp" ).forward( request, response );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.10  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.9  2006/08/29 19:54:14  poth
footer corrected

Revision 1.8  2006/06/19 06:39:22  poth
*** empty log message ***

Revision 1.7  2006/05/01 20:15:27  poth
*** empty log message ***

Revision 1.6  2006/04/06 20:25:31  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.4  2006/02/27 21:58:57  poth
*** empty log message ***

Revision 1.3  2006/02/16 08:26:49  poth
*** empty log message ***

Revision 1.2  2006/02/07 19:52:44  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:12  poth
*** empty log message ***


********************************************************************** */