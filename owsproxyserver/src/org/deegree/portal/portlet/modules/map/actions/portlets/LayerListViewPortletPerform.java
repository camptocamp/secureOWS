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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.deegree.framework.util.StringTools;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * class more or less independ from a concrete porztal implementation
 * that handles action assigned to LayerListView portlet
 *
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 *
 * @version 1.0. $Revision$, $Date$
 *
 * @since 2.0
 */
public class LayerListViewPortletPerform extends IGeoPortalPortletPerform {
    
    private static String INIT_SELECTABLELIST = "SelectableList";
    private static String INIT_FORCELINEBREAK = "forceLineBreak";
    
    private  Map selectableList = null; 

    /**
     * @param request
     * @param portlet
     */
    public LayerListViewPortletPerform(HttpServletRequest request, Portlet portlet, 
                                       ServletContext sc) {
        super( request, portlet, sc );
        String tmp = getInitParam( INIT_SELECTABLELIST );
        String[] tmps = StringTools.toArray( tmp, ",", false );
        selectableList = new HashMap( tmps.length );       
        for ( int i = 0; i < tmps.length; i++ ) {
            String[] s = StringTools.toArray( tmps[i], "|", false );
            selectableList.put( s[0], s[1] );
        }
    }
   
    /**
     * writes all parameters required by the assigned JSP page to the
     * forwarded http request. 
     *
     */
    void putParametersToRequest() {
        
        PortletConfig pc = portlet.getPortletConfig(); 
        request.setAttribute( "PORTLETID", portlet.getID() );  
        request.setAttribute( "SELECTABLELIST", selectableList );
        String tmp = pc.getInitParameter( INIT_FORCELINEBREAK );
        List list = new ArrayList();
        if ( tmp != null ) {
            for ( int i = 0; i < tmp.length(); i++ ) {
                list.add( "" + tmp.charAt(i) );
            }
        }
        request.setAttribute( "LINEBREAK", list );
        
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.4  2006/02/20 09:28:15  poth
*** empty log message ***

Revision 1.3  2006/02/10 12:14:08  poth
*** empty log message ***

Revision 1.2  2006/02/07 19:52:44  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.2  2006/01/09 07:47:09  ap
*** empty log message ***

Revision 1.1  2005/10/23 16:49:38  ap
*** empty log message ***


********************************************************************** */