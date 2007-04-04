//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/map/actions/portlets/ScaleChooserPortletAction.java,v 1.4 2006/10/27 13:07:53 taddei Exp $
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
 * Action that delegates work to Perform class
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: taddei $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/10/27 13:07:53 $
 */

public class ScaleChooserPortletAction extends JspPortletAction {

    @Override
    protected void buildNormalContext( Portlet portlet, RunData rundata ) throws Exception {
        try {
            ScaleChooserPortletPerform scalePerform = 
                new ScaleChooserPortletPerform( rundata.getRequest(), portlet,
                                                rundata.getServletContext() );
            scalePerform.readInitParameter();
            scalePerform.buildNormalContext();
            scalePerform.doChangeScale();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: ScaleChooserPortletAction.java,v $
 * Revision 1.4  2006/10/27 13:07:53  taddei
 * uses init pars; clean up
 *
 * Revision 1.3  2006/10/20 13:38:16  taddei
 * now with current scale value in request
 *
 * Revision 1.2  2006/10/12 15:46:19  poth
 * adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents
 *
 * Revision 1.1  2006/09/22 09:03:31  taddei
 * added portlet code for scale changing
 * Changes to this class. What the people have been up to:
 **************************************************************************************************/