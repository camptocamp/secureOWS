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

import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.apache.jetspeed.modules.actions.portlets.JspPortletAction;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.turbine.util.RunData;
import org.deegree.framework.util.StringTools;
import org.deegree.portal.portlet.modules.actions.AbstractPortletPerform;
import org.deegree.portal.portlet.portlets.WebMapPortlet;

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
public class MapWindowPortletAction extends JspPortletAction {
    
    protected void buildConfigureContext( Portlet portlet, RunData rundata ) throws Exception {
        // TODO 
        super.buildConfigureContext( portlet, rundata );
    }
    
    protected void buildMaximizedContext( Portlet portlet, RunData rundata ) throws Exception {        
        super.buildMaximizedContext( portlet, rundata );
        
        try {
            MapWindowPortletPerform mwpp = 
                new MapWindowPortletPerform( rundata.getRequest(), portlet, rundata.getServletContext() );
            
            // common to all operations
            mwpp.setMode();
            
            // set map size to 95% of the window size
            rundata.getRequest().setAttribute( "WIDTH", new Integer( -95 ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
        
    }
    
    protected void buildNormalContext( Portlet portlet, RunData rundata ) throws Exception {
       
        try {
            MapWindowPortletPerform mwpp = 
                new MapWindowPortletPerform( rundata.getRequest(), portlet, 
                                             rundata.getServletContext() );
            
            mwpp.buildNormalContext();
            // common to all operations
            mwpp.setMode();
            
            HttpSession ses = rundata.getRequest().getSession(); 
            if ( ses.getAttribute( AbstractPortletPerform.SESSION_HOME ) == null ) {                
                ses.setAttribute( AbstractPortletPerform.SESSION_HOME, 
                                  ((WebMapPortlet)portlet).getHome() );
            }
                        
            PortletConfig pc = portlet.getPortletConfig();     
            // set map with to current portlet width if responsible init parameter is set 
            if ( pc.getInitParameter( MapWindowPortletPerform.INIT_SCALEMAPTOPORTLETSIZE ) != null && 
                 pc.getInitParameter( MapWindowPortletPerform.INIT_SCALEMAPTOPORTLETSIZE )
                   .equalsIgnoreCase( "true" ) ) {
                int width = calcPortletWidth( portlet, rundata );
                rundata.getRequest().setAttribute( "WIDTH", Integer.toString( width ) );
            }   
            
            // this is set here because it's jetspeed specific stuff
            String fiTarget = 
                portlet.getPortletConfig().getInitParameter( MapWindowPortletPerform.
                                                             INIT_FEATUREINFOTARGETPORTLET );
            
            String[] tmp = StringTools.toArray( fiTarget, ";", false );
            if ( tmp.length > 1 ) {
                rundata.getRequest().setAttribute( "FEATUREINFOTARGETPANE", tmp[0] ); 
                rundata.getRequest().setAttribute( "FEATUREINFOTARGETPORTLET", tmp[1] );
            } else {
                rundata.getRequest().setAttribute( "FEATUREINFOTARGETPANE", "" ); 
                rundata.getRequest().setAttribute( "FEATUREINFOTARGETPORTLET", fiTarget );
            }
                   
        } catch ( Exception e ) {
            e.printStackTrace();
        }
       
    }
    
    
    /**
     * returns the width of a portlet. If the size is given as a relative value
     * the returned value i &lt; 0 an absolute size will be returned as positive
     * value. 
     * 
     * @param portlet
     * @param rundata
     */
    private int calcPortletWidth( Portlet portlet, RunData rundata ) {
        int width = 0;
        try {
            Iterator iter = 
                portlet.getInstance(rundata).getDocument().getPortlets().getPortletsIterator();
            boolean contains = false;
            Portlets portlets = null;
            int col = 0;
            //find psml portlet that contains the portlet in question
            do {                
                portlets = (Portlets)iter.next();
                Iterator iter2 = portlets.getEntriesIterator();
                Entry entry = null;
                while ( entry == null && iter2.hasNext() ) {
                    entry = (Entry)iter2.next();
                    if ( !entry.getId().equals( portlet.getID() ) ) {
                        entry = null;
                    } else {
                        String tmp = entry.getLayout().getParameterValue( "column" );
                        col = Integer.parseInt( tmp );
                    }
                }
                contains = entry != null;
            } while ( !contains && iter.hasNext() );

            Controller controller = portlets.getController();
            String sizes = controller.getParameterValue( "sizes" );            
            if ( sizes != null ) {
                String[] tmp = StringTools.toArray( sizes, ",", false );
                if ( tmp[col].endsWith( "%" ) ) {
                    // parse relative portlet width. a relative portlet width is 
                    // marked by a negative sign
                    width = -1 * Integer.parseInt( tmp[col].substring( 0, tmp[col].length()-1 ) );
                } else {
                    // parse absolute portlet width. 
                    width = Integer.parseInt( tmp[col] );
                }
            } else {
                // use 50% as default
                width = 50;
            }
        } catch ( Exception e2 ) {
            e2.printStackTrace();
        }
        return width;
    }

    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.8  2006/08/29 19:54:14  poth
footer corrected

Revision 1.7  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.6  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.5  2006/03/04 20:36:18  poth
*** empty log message ***

Revision 1.4  2006/02/27 21:58:57  poth
*** empty log message ***

Revision 1.3  2006/02/14 16:05:05  poth
*** empty log message ***

Revision 1.2  2006/02/13 14:13:05  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.11  2006/01/09 07:47:09  ap
*** empty log message ***

Revision 1.10  2005/10/23 16:49:38  ap
*** empty log message ***

Revision 1.9  2005/10/07 15:54:58  ap
*** empty log message ***

Revision 1.8  2005/10/07 15:52:55  ap
*** empty log message ***

Revision 1.7  2005/10/05 20:45:11  ap
*** empty log message ***

Revision 1.6  2005/09/19 19:09:58  ap
*** empty log message ***

Revision 1.5  2005/09/15 09:45:48  ap
*** empty log message ***

Revision 1.4  2005/08/29 20:20:28  ap
*** empty log message ***

Revision 1.3  2005/08/16 14:56:44  ap
*** empty log message ***

Revision 1.2  2005/08/13 13:29:38  ap
*** empty log message ***

Revision 1.1  2005/08/12 18:36:22  ap
*** empty log message ***


********************************************************************** */