//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/actions/AbstractPortletPerform.java,v 1.14 2006/10/12 15:46:19 poth Exp $
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
package org.deegree.portal.portlet.modules.actions;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletSet;
import org.deegree.framework.util.KVP2Map;
import org.deegree.portal.PortalException;

/**
 * 
 *
 * @version $Revision: 1.14 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.14 $, $Date: 2006/10/12 15:46:19 $
 *
 * @since 2.0
 */
public abstract class AbstractPortletPerform {

    public static final String INIT_MAPPORTLETID = "mapPortletID";

    public static final String INIT_WMC = "wmc";

    public static final String AVAILABLE_WMC = "availableWMC";

    //attributes stored in the users session
    public static final String SESSION_HOME = "HOME";
    
    public static final String CURRENT_WMC = "CURRENTWMC";
    
    public static final String CURRENT_WMC_NAME = "CURRENTWMCNAME";

    public static final String SESSION_VIEWCONTEXT = "VIEWCONTEXT";

    public static final String SESSION_CURRENTFILAYER = "CURRENTFILAYER";

    public static final String PARAM_MAPPORTLET = "MAPPORTLET";

    public static final String PARAM_MAPACTION = "MAPACTIONPORTLET";

    public static final String PARAM_MODE = "MODE";

    protected Portlet portlet = null;

    protected HttpServletRequest request = null;

    protected Map parameter = null;

    /**
     * @param portlet
     * @param request
     */
    public AbstractPortletPerform( HttpServletRequest request, Portlet portlet ) {
        this.portlet = portlet;
        this.request = request;
        parameter = KVP2Map.toMap( request );
        if ( portlet != null ) {
            setMapActionPortletID();
        }
    }

    /**
     * returns the value of the passed init parameter. This method shal be used
     * to hide functional implementation from concrete portlet implementation.
     * @param name
     * @return
     */
    protected String getInitParam( String name ) {
        if ( portlet != null ) {
            return portlet.getPortletConfig().getInitParameter( name );
        }
        return null;
    }

    /**
     * returns the ID of the mapmodel assigned to a portlet. First the
     * method tries to read it from the portlets initparameter. If not
     * present it returns the ID of the first iGeoPortal:MapWindowPortlet
     * it finds.
     * @return
     */
    protected String getMapPortletID() {
        String mmid = portlet.getPortletConfig().getInitParameter( INIT_MAPPORTLETID );
        if ( mmid == null ) {
            PortletSet ps = portlet.getPortletConfig().getPortletSet();
            Portlet port = ps.getPortletByName( "iGeoPortal:MapWindowPortlet" );
            mmid = port.getID();
        }
        return mmid;
    }

    /**
     * this method will be called each time a portlet will be repainted. It
     * determines all portlets visible in a page and writes a Map with the
     * portlets name as key and the portlets ID as value into the forwarded
     * HttpRequest object 
     * 
     * @throws Exception
     */
    public void buildNormalContext()
                            throws PortalException {

        request.setAttribute( "PORTLETID", portlet.getID() );
        Map map = new HashMap();
        Enumeration enume = portlet.getPortletConfig().getPortletSet().getPortlets();
        while ( enume.hasMoreElements() ) {
            Portlet p = (Portlet) enume.nextElement();
            map.put( p.getName(), p.getID() );
        }
        request.setAttribute( "PORTLETS", map );
        //request.setAttribute( PARAM_WMC, readMapContextID() );
        request.setAttribute( PARAM_MAPPORTLET, getInitParam( INIT_MAPPORTLETID ) );
    }

    protected String readMapContextID_() {
        String pid = null;

        String cntxID = null;
        if ( portlet != null ) {
            cntxID = (String) request.getSession().getAttribute( portlet.getID() + "_" + CURRENT_WMC );
            if ( cntxID == null ) {
                pid = portlet.getPortletConfig().getInitParameter(  AbstractPortletPerform.INIT_MAPPORTLETID );
            }
        }

        if ( cntxID == null ) {
            if ( pid == null ) {
                cntxID = getInitParam( INIT_WMC );
            } else {
                Portlet port = portlet.getPortletConfig().getPortletSet().getPortletByID( pid );
                if ( port == null ) {
                    // try to access first MapWindowPortlet found in a page
                    pid = getMapPortletID();
                    port = portlet.getPortletConfig().getPortletSet().getPortletByID( pid );
                }
                if ( port != null ) {
                    cntxID = port.getPortletConfig().getInitParameter( INIT_WMC );
                }
            }
        }
        return cntxID;
    }

    /**
     * 
     *
     */
    private void setMapActionPortletID() {
        Portlet port = portlet.getPortletConfig().getPortletSet().getPortletByName(
                                                                                    "iGeoPortal:MapActionPortlet" );
        if ( port != null ) {
            request.setAttribute( PARAM_MAPACTION, port.getID() );
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: AbstractPortletPerform.java,v $
 Revision 1.14  2006/10/12 15:46:19  poth
 adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

 Revision 1.13  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.12  2006/06/06 19:49:12  poth
 handling of mapmodel changed - it now will be available for each MapWindowPortlet through the session

 Revision 1.11  2006/06/06 19:02:00  poth
 initial upload of SelectWMC portlet

 Revision 1.10  2006/05/12 14:36:28  poth
 *** empty log message ***

 Revision 1.9  2006/05/03 10:52:42  poth
 *** empty log message ***

 Revision 1.8  2006/04/06 20:25:32  poth
 *** empty log message ***

 Revision 1.7  2006/03/30 21:20:29  poth
 *** empty log message ***

 Revision 1.6  2006/03/04 20:36:18  poth
 *** empty log message ***

 Revision 1.5  2006/02/27 21:58:57  poth
 *** empty log message ***

 Revision 1.4  2006/02/20 09:27:51  poth
 *** empty log message ***

 Revision 1.3  2006/02/16 17:21:38  poth
 *** empty log message ***

 Revision 1.2  2006/02/07 19:52:44  poth
 *** empty log message ***

 Revision 1.1  2006/02/05 09:30:12  poth
 *** empty log message ***

 Revision 1.4  2006/01/23 16:18:23  ap
 *** empty log message ***

 Revision 1.3  2005/10/23 16:49:37  ap
 *** empty log message ***

 Revision 1.2  2005/09/15 09:45:48  ap
 *** empty log message ***

 Revision 1.1  2005/08/29 20:20:28  ap
 *** empty log message ***


 ********************************************************************** */