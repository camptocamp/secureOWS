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
 53115 Bonn
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.ViewContext;

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
public abstract class FeatureInfoForwardPortletPerform extends FeatureInfoPortletPerform {
    
    private static final ILogger LOG = LoggerFactory.getLogger( FeatureInfoForwardPortletPerform.class );
    
    
    /**
     * @param request
     * @param portlet
     */
    public FeatureInfoForwardPortletPerform(HttpServletRequest request, Portlet portlet,
                                            ServletContext servletContext) {
        super( request, portlet, servletContext );    
        
    }
    
    /**
     * 
     * @return
     */
    protected void doGetFeatureInfo() throws PortalException, OGCWebServiceException {
        
        // a get feature info request only can be performed if a viewcontext
        // has been initialized before by the MapWindowPortletAction
        ViewContext vc = getCurrentViewContext( portlet.getID() ); 
        if ( vc == null ) {
            throw new PortalException( "no valid view context available through users session" );
        }
        String reqlayer = (String)parameter.get( PARAM_FILAYERS );
        if ( reqlayer == null || reqlayer.length() == 0 ) {
            throw new PortalException( "at least one layer/featuretype/coverage must be " +
                    "targeted by a feature info request" );
        }
        // update the view contexts bounding box with the current one. This information
        // will be evaluated when creating a GetFeatureInfo request later
        String tmp = (String)parameter.get( PARAM_BBOX );
        if ( tmp == null || tmp.length() == 0 ) {
            throw new PortalException( "required parameter " + PARAM_BBOX + " is missing!" );
        }
        double[] coords = StringTools.toArrayDouble( tmp, "," );        
        CoordinateSystem crs = vc.getGeneral().getBoundingBox()[0].getCoordinateSystem();
        Point[] pt = new Point[2];
        pt[0] = GeometryFactory.createPoint( coords[0], coords[1], crs );
        pt[1] = GeometryFactory.createPoint( coords[2], coords[3], crs );
        try {
            vc.getGeneral().setBoundingBox( pt );
        } catch ( ContextException e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( "could not update viewcontexts bbox", e );
        }
        
        List layerList = new ArrayList(50);
        layerList.add( vc.getLayerList().getLayer( reqlayer ) );
        tmp = perform( layerList, vc ).trim();     
        String[] ids = StringTools.toArray( tmp, ";", true );
            
        List list = performQuery( ids );
        
        request.setAttribute( "RESULT", list );
        
    }
    
    /**
     * the IDs of the features targeted by a GetFeatureInfo request will
     * be passed. A concret implementation of this method may use them to
     * perform a database query, a GetFeature request or something else.
     * 
     * @param ids
     * @return
     */
    protected abstract List performQuery(String[] ids);
    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.8  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.7  2006/08/29 19:54:13  poth
footer corrected

Revision 1.6  2006/06/07 12:19:38  poth
*** empty log message ***

Revision 1.5  2006/05/01 20:15:27  poth
*** empty log message ***

Revision 1.4  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.2  2006/03/22 13:22:14  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.3  2005/10/05 20:45:11  ap
*** empty log message ***

Revision 1.2  2005/09/16 09:38:18  ap
*** empty log message ***

Revision 1.1  2005/09/16 07:06:30  ap
*** empty log message ***

Revision 1.1  2005/09/15 09:45:48  ap
*** empty log message ***


********************************************************************** */