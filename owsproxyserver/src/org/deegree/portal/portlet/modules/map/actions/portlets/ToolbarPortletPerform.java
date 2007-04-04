//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/ToolbarPortletPerform.java,v 1.11 2006/09/19 14:13:57 taddei Exp $
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

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.deegree.framework.util.StringTools;
import org.deegree.portal.PortalException;
import org.deegree.portal.portlet.modules.actions.AbstractPortletPerform;


/**
 * 
 *
 * @version $Revision: 1.11 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: taddei $
 *
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/09/19 14:13:57 $
 *
 * @since 2.0
 */
public class ToolbarPortletPerform extends AbstractPortletPerform {

    // init parameter of the portlet
    public static final String INIT_BUTTONS = "buttons";    
    public static final String INIT_ORIENTATION = "orientation";
    public static final String INIT_COLS = "columns";
    public static final String INIT_ROWS = "rows";
    
    private String imageBase = "./igeoportal/images/";
    
    /**
     * private constructor
     * @param request
     * @param portlet
     */
    private ToolbarPortletPerform(HttpServletRequest request, Portlet portlet) {
        super( request, portlet );
    }
    
    /**
     * returns an instance of @see MapWindowPortletPerform
     * @param request
     * @param portlet
     * @return
     */
    static ToolbarPortletPerform getInstance(HttpServletRequest request, Portlet portlet) {
        return new ToolbarPortletPerform( request, portlet );
    }
    
    /**
     * reads the init parameters of the ToolbarPortlet and writes them
     * into the requests attributes.
     *
     */
    void readInitParameter() throws PortalException {
        PortletConfig pc = portlet.getPortletConfig();
        String tmp = pc.getInitParameter( INIT_BUTTONS );
        if ( tmp == null ) {
            throw new PortalException( "buttons init parameter must be set" );
        }
        String[] ar = StringTools.toArray( tmp, ";", false );
        String[][] buttonList = new String[ar.length][];
        for ( int i = 0; i < buttonList.length; i++ ) {
            buttonList[i] = getButtonSrc( ar[i] );
        }
                
        request.setAttribute( INIT_BUTTONS, buttonList );
        
        tmp = pc.getInitParameter( INIT_COLS ); 
        if ( tmp != null ) {
            request.setAttribute( INIT_COLS, new Integer( tmp ) );
        } else {
            request.setAttribute( INIT_COLS, new Integer( 99999 ) );
        }
        tmp = pc.getInitParameter( INIT_ROWS ); 
        if ( tmp != null ) {
            request.setAttribute( INIT_ROWS, new Integer( tmp ) );
        } else {
            request.setAttribute( INIT_ROWS, new Integer( 99999 ) );
        }
        
        tmp = pc.getInitParameter( INIT_ORIENTATION );
        if ( tmp == null || tmp.equals( "FLOW" ) ) {
            request.setAttribute( INIT_ORIENTATION, "FLOW" );
        } else if ( tmp.equals( "VERTICAL" ) ) {
            request.setAttribute( INIT_ORIENTATION, "VERTICAL" );
        } else if ( tmp.equals( "HORIZONTAL" ) ) {
            request.setAttribute( INIT_ORIENTATION, "HORIZONTAL" );
        } else {
            throw new PortalException( "init parameter orientation must be " +
                                       "VERTICAL, HORIZONTAL or FLOW" );
        }
        request.setAttribute( "PORTLETID", portlet.getID() );        
    }
    
    /**
     * 
     * @param button
     * @return
     */
    private String[] getButtonSrc(String button) {
        String[] tmp = StringTools.toArray( button, "|", false );
        button = tmp[0];
        String[] src = new String[5];
        src[3] = button;
        src[4] = tmp[1];
        if ( button.equals( "ZOOMIN" ) ) {
            src[0] = imageBase + "zoomin.gif";
            src[1] = imageBase + "zoomin_a.gif";
            src[2] = "toggle";
        } else if ( button.equals( "ZOOMOUT" ) ) {
            src[0] = imageBase + "zoomout.gif";
            src[1] = imageBase + "zoomout_a.gif";
            src[2] = "toggle";
        } else if ( button.equals( "RECENTER" ) ) {
            src[0] = imageBase + "recenter.gif";
            src[1] = imageBase + "recenter_a.gif";
            src[2] = "toggle";
        } else if ( button.equals( "FULLEXTEND" ) ) {
            src[0] = imageBase + "fullextent.gif";
            src[1] = imageBase + "fullextent_a.gif";
            src[2] = "push";            
        } else if ( button.equals( "FEATUREINFO" ) ) {
            src[0] = imageBase + "featureinfo.gif";
            src[1] = imageBase + "featureinfo_a.gif";
            src[2] = "toggle";            
        } else if ( button.equals( "REFRESH" ) ) {
            src[0] = imageBase + "refresh.gif";
            src[1] = imageBase + "refresh_a.gif";
            src[2] = "push";            
        } else if ( button.equals( "HOME" ) ) {
            src[0] = imageBase + "home.gif";
            src[1] = imageBase + "home_a.gif";
            src[2] = "push";            
        } else if ( button.equals( "MOVE" ) ) {
            src[0] = imageBase + "move.gif";
            src[1] = imageBase + "move_a.gif";
            src[2] = "toggle";
        } else if ( button.equals( "ADDOWS1" ) || button.equals( "ADDOWS2" ) ) {
            src[0] = imageBase + "addows.gif";
            src[1] = imageBase + "addows_a.gif";
            src[2] = "push";
        } else if ( button.equals( "PRINT1" ) || button.equals( "PRINT2" ) ) {
            src[0] = imageBase + "print.gif";
            src[1] = imageBase + "print_a.gif";
            src[2] = "push";
        } else if ( button.equals( "DOWNLOAD" ) ) {
            src[0] = imageBase + "download.gif";
            src[1] = imageBase + "download_a.gif";
            src[2] = "push";
        } else if ( button.equals( "DISTANCE" ) ) {
            src[0] = imageBase + "distance.gif";
            src[1] = imageBase + "distance_a.gif";
            src[2] = "toggle";
        } else if ( button.equals( "HISTORYFORWARD" ) ) {
            src[0] = imageBase + "movetonext.gif";
            src[1] = imageBase + "movetonext_a.gif";
            src[2] = "push";
        } else if ( button.equals( "HISTORYBACKWARD" ) ) {
            src[0] = imageBase + "movetoprevious.gif";
            src[1] = imageBase + "movetoprevious_a.gif";
            src[2] = "push";
        } else if ( button.equals( "ANNOTATE" ) ) {
            src[0] = imageBase + "annotation.gif";
            src[1] = imageBase + "annotation_a.gif";
            src[2] = "toggle";
        } else if ( button.equals( "MEASURELEN" ) ) {
            src[0] = imageBase + "measurelen.gif";
            src[1] = imageBase + "measurelen_a.gif";
            src[2] = "toggle";
        } else {
            throw new RuntimeException( "No button '" + button + "' found." );
        }
        
        return src;
    }
    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ToolbarPortletPerform.java,v $
Revision 1.11  2006/09/19 14:13:57  taddei
added code for length measurement

Revision 1.10  2006/08/29 19:54:13  poth
footer corrected

Revision 1.9  2006/05/15 13:31:18  poth
*** empty log message ***

Revision 1.8  2006/05/12 11:17:41  poth
*** empty log message ***

Revision 1.7  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.6  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.5  2006/02/27 21:58:57  poth
*** empty log message ***

Revision 1.4  2006/02/27 16:14:11  poth
*** empty log message ***

Revision 1.3  2006/02/20 09:28:15  poth
*** empty log message ***

Revision 1.2  2006/02/06 21:46:19  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.3  2005/10/23 16:49:38  ap
*** empty log message ***

Revision 1.2  2005/10/05 20:45:11  ap
*** empty log message ***

Revision 1.1  2005/08/29 20:20:28  ap
*** empty log message ***


********************************************************************** */