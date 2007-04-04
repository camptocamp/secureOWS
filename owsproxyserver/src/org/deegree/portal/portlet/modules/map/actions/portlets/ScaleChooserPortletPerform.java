//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/ScaleChooserPortletPerform.java,v 1.9 2006/11/27 10:22:54 taddei Exp $
/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/deegree/
lat/lon GmbH
http://www.lat-lon.de

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

Contact:

Andreas Poth
lat/lon GmbH
Aennchenstraße 19
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

import java.awt.Rectangle;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * This Perform class takes care of changing the WMC's bounding box based on a 
 * scale paramter. The parameter is passed in the request. The paramter name is 
 * defined by the static member NEW_SCALE_VALUE 
 *
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: taddei $
 *
 * @version $Revision: 1.9 $, $Date: 2006/11/27 10:22:54 $
 */

public class ScaleChooserPortletPerform extends IGeoPortalPortletPerform {
    
    private static final ILogger LOG = LoggerFactory.getLogger( ScaleChooserPortletPerform.class );

    public static final String REQUESTED_SCALE_VALUE = "REQUESTED_SCALE_VALUE";
    
    public static final String AVAILABLE_SCALES = "AVAILABLE_SCALES";

    //  TODO make pixel size a property
    public static final double DEFAULT_PIXEL_SIZE = 0.00028;

    
    /**
     * private constructor
     * @param request
     * @param portlet
     * @param sc
     */
    ScaleChooserPortletPerform(HttpServletRequest request, Portlet portlet, ServletContext sc) {
        super( request, portlet, sc );
    }    
    
    /**
     * TODO
     * reads the init parameters of the portlet and build the scale list
     *
     */
    void readInitParameter(){
        
        String list = getInitParam( AVAILABLE_SCALES );
        if( list == null ){
            list = "10000;25000;50000;100000;500000;1000000";
        }
        String[] tmp = list.split( ";" );

        HttpSession ses = request.getSession();
        ses.setAttribute( AVAILABLE_SCALES, tmp );
        //TODO read init parameters and build scale list? 
        
    }

    /**
     * This method changes the scale of the current bounding box
     * @param rundata 
     * @throws PortalException 
     *
     */
    void doChangeScale() throws PortalException {
        
        String newScale = (String) parameter.get( REQUESTED_SCALE_VALUE );
        
        if( newScale == null ){
            //throw new PortalException( "No scale available. Missing " + REQUESTED_SCALE_VALUE  
            //+ "  parameter" );
            return;
        }
        
        ViewContext vc = getCurrentViewContext( getInitParam( INIT_MAPPORTLETID ) );
        if ( vc == null ) {
            throw new PortalException( "no valid view context available through users session" );
        }
        
        // rad need pars from ViewCOntext
        Point p0 = vc.getGeneral().getBoundingBox()[0];
        Point p1 = vc.getGeneral().getBoundingBox()[1];
        CoordinateSystem cs = p0.getCoordinateSystem();
        
        Rectangle window = vc.getGeneral().getWindow();
        
        Envelope env = GeometryFactory.createEnvelope( p0.getX(), p0.getY(), p1.getX(), p1.getY(), cs );
        
        try {
            double reqScale = Double.parseDouble( newScale );
            
            double currentScale = MapUtils.calcScale( window.width, window.height, env,
                                                     p1.getCoordinateSystem(), DEFAULT_PIXEL_SIZE );
            
            // calc new envelope
            env = MapUtils.scaleEnvelope( env, currentScale, reqScale );
            
            // set new bbox
            p0 = GeometryFactory.createPoint( env.getMin().getX(), env.getMin().getY(), cs );
            p1 = GeometryFactory.createPoint( env.getMax().getX(), env.getMax().getY(), cs );     
            
            vc.getGeneral().setBoundingBox( new Point[]{ p0, p1 } );
            
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( e.getMessage() );            
        }        
    }
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ScaleChooserPortletPerform.java,v $
Revision 1.9  2006/11/27 10:22:54  taddei
putting String[] into session, and not List<String>

Revision 1.8  2006/10/27 13:07:53  taddei
uses init pars; clean up

Revision 1.7  2006/10/20 13:38:16  taddei
now with current scale value in request

Revision 1.6  2006/10/17 20:31:17  poth
*** empty log message ***

Revision 1.5  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.4  2006/09/25 20:33:19  poth
useless parameters removed from scalelistener classes/methods

Revision 1.3  2006/09/25 12:47:00  poth
bug fixes - map scale calculation

Revision 1.2  2006/09/22 11:36:51  taddei
fixed bug (requested and actual scale were swapped); added javadoc

Revision 1.1  2006/09/22 09:03:31  taddei
added portlet code for scale changing

********************************************************************** */ 