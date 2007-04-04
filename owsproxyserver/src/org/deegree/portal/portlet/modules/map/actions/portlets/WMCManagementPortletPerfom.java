//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/WMCManagementPortletPerfom.java,v 1.4 2006/11/27 09:07:52 poth Exp $
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jetspeed.portal.Portlet;
import org.apache.turbine.util.RunData;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.XMLFactory;
import org.deegree.portal.portlet.modules.actions.AbstractPortletPerform;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * Perform class for saving WMCs
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/11/27 09:07:52 $
 */

public class WMCManagementPortletPerfom extends IGeoPortalPortletPerform {

    /**
     * constructor
     * @param request
     * @param portlet
     * @param sc
     */
    WMCManagementPortletPerfom(HttpServletRequest request, Portlet portlet, ServletContext sc) {
        super( request, portlet, sc );
    }
    
    /**
     * 
     * @param data
     * @throws Exception
     */
    void saveCurrentContext( RunData data )
                            throws Exception {
        
        ViewContext vc = getCurrentViewContext( getInitParam( INIT_MAPPORTLETID ) );
        if ( vc == null ) {
            throw new PortalException( "no valid view context available through users session" );
        }
        
        String user = data.getUserFromSession().getUserName();

        File dir = new File( data.getServletContext().getRealPath( "WEB-INF/wmc/" + user ) );
        
        HttpSession ses = data.getRequest().getSession( false );
        if ( ses != null && dir.exists() ) {
            String prefix = getMapPortletID(); 
            
            storeContext( dir, prefix + AbstractPortletPerform.CURRENT_WMC, vc );
        }
    }

    //TODO copied from LogoutUser, re-use code
    private void storeContext( File dir, String name, ViewContext context )
                            throws ParserConfigurationException, IOException {

        XMLFragment xml = XMLFactory.export( context );

        File file = new File( dir.getAbsolutePath() + '/' + name + ".xml" );

        FileOutputStream fos = new FileOutputStream( file );
        xml.write( fos );
        fos.close();

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WMCManagementPortletPerfom.java,v $
 * Changes to this class. What the people have been up to: Revision 1.4  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/10/27 15:40:10  taddei
 * Changes to this class. What the people have been up to: changed impl to use map portlet id
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/10/12 15:46:19  poth
 * Changes to this class. What the people have been up to: adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/09/22 11:38:53  taddei
 * Changes to this class. What the people have been up to: portlet for saving WMCs
 * Changes to this class. What the people have been up to:
 **************************************************************************************************/