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

import org.apache.jetspeed.modules.actions.portlets.JspPortletAction;
import org.apache.jetspeed.portal.Portlet;
import org.apache.turbine.util.RunData;

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
public class ToolbarPortletAction extends JspPortletAction {
    
    protected void buildNormalContext( Portlet portlet, RunData rundata ) throws Exception {
        
        try { 
            ToolbarPortletPerform tbpp = 
                ToolbarPortletPerform.getInstance( rundata.getRequest(), portlet );
            tbpp.readInitParameter();
            tbpp.buildNormalContext();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.3  2006/01/09 07:47:09  ap
*** empty log message ***

Revision 1.2  2005/09/15 09:45:48  ap
*** empty log message ***

Revision 1.1  2005/08/29 20:20:28  ap
*** empty log message ***


********************************************************************** */