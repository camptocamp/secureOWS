//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/portlets/WebMapPortlet.java,v 1.6 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.portal.portlet.portlets;

import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.JspPortlet;
import org.deegree.framework.util.StringTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/08/29 19:54:14 $
 *
 * @since 2.0
 */
public class WebMapPortlet extends JspPortlet {
        
    static String INIT_HOMEBBOX = "homeBoundingBox";
    
    private Envelope home = null;


    /**
     * loads the ViewContext assigend to a portlet instance from
     * the resource defined in the portles configuration
     */
    public void init() throws PortletException {
        super.init();
        
        PortletConfig pc = getPortletConfig();
         
        // get HOME boundingbox
        String tmp = pc.getInitParameter( INIT_HOMEBBOX );
        if ( tmp == null ) {        
            
        } else {
            double[] coords = StringTools.toArrayDouble( tmp, "," );
            home = GeometryFactory.createEnvelope( coords[0], coords[1], coords[2], 
                                                   coords[3], null );
        }

    }
    
    /**
     * returns the home boundingbox of the context assigned to this portlet
     * @return
     */
    public Envelope getHome() {
        return home;
    }
    
    /**
     * sets the home boundingbox of the context assigned to this portlet
     * @param home
     */
    public void setHome( Envelope home ) {
        this.home = home;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WebMapPortlet.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.3  2006/02/07 07:58:38  poth
*** empty log message ***

Revision 1.2  2006/02/06 21:46:29  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:12  poth
*** empty log message ***

Revision 1.3  2006/01/09 07:46:43  ap
*** empty log message ***

Revision 1.2  2005/12/29 16:47:57  ap
*** empty log message ***

Revision 1.1  2005/10/05 20:45:31  ap
*** empty log message ***


********************************************************************** */