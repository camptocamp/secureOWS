//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/WMCManagementPortletAction.java,v 1.3 2006/10/12 15:46:19 poth Exp $
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

import org.apache.jetspeed.modules.actions.portlets.JspPortletAction;
import org.apache.jetspeed.portal.Portlet;
import org.apache.turbine.util.RunData;

/**
 * General action for managing WMC (saving, loading sharing). Currently
 * only saving is implemented.
 *
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.3 $, $Date: 2006/10/12 15:46:19 $
 */

public class WMCManagementPortletAction extends JspPortletAction {
    
    @Override
    protected void buildNormalContext( Portlet portlet, RunData runData )
                            throws Exception {
        
        WMCManagementPortletPerfom perform = 
            new WMCManagementPortletPerfom( runData.getRequest(), portlet,
                                            runData.getServletContext() );

        // this is currently te only impl. the perform should in the future do more
        perform.saveCurrentContext( runData );
        
    }
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMCManagementPortletAction.java,v $
Revision 1.3  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.2  2006/09/25 20:33:19  poth
useless parameters removed from scalelistener classes/methods

Revision 1.1  2006/09/22 11:38:15  taddei
portlet for saving WMCs

********************************************************************** */ 