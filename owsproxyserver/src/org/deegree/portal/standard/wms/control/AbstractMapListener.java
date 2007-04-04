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
package org.deegree.portal.standard.wms.control;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.Debug;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.portal.context.ViewContext;


/**
 * Basic class for all listerens that shall be notified if a map
 * cliented raises an action/event.<p></p>
 * There are several predefined listeres for actions that most
 * map clients support:
 * <ul>
 * <li><tt>ZoomInListener</tt> for handling zoomin actions. supported
 *     are zooming via point or vie rectangle.
 * <li><tt>ZoomOutListener</tt> for handling zoomout action.
 * <li><tt>PanListener</tt> for handling of pan action. supported
 *     is panning to eight directions.
 * <li><tt>RecenterListener</tt> recenters the map to a specified point.
 *     This can be interpreted as a special versio of zooming
 * <li><tt>RefreshListener</tt> reloads the map without any change
 * <li><tt>ResetListener</tt> recovers the initial status of the map
 * <li><tt>InfoListener</tt> will be notified if a feature info request
 *     should be send.
 * </ul>
 * The user can additional listeners/action by extending the
 * <tt>AbstractActionListener</tt> class or one of the predefined
 * listener.<p></p>
 * Each Listerner have to be registered to the <tt>MapListener</tt>
 * which is the class that will be informed about each event/action
 * within a map client. To register a class as listener it has to
 * stored within the MapListener.ConfigurationFile.
 *
 * <p>---------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.7 $ $Date: 2006/10/17 20:31:17 $
 */
abstract class AbstractMapListener extends AbstractListener {
    
    /**
     *
     *
     * @param event 
     */
    public void actionPerformed( FormEvent event ) {
        Debug.debugMethodBegin( this, "actionPerformed" );

        HttpSession session = ( (HttpServletRequest)getRequest() ).getSession();
        // get configuration from the users session
        //WMSClientConfiguration config = 
        //     (WMSClientConfiguration)session.getAttribute( Constants.WMSCLIENTCONFIGURATION );
        ViewContext vc = (ViewContext)session.getAttribute( "DefaultMapContext" );
        
        // if no configuration is stored in the session get default config
        if ( vc == null ) {
            // get default client configuration 
            //config = MapApplicationHandler.getDefaultClientConfiguration();
            
            //vc = MapApplicationHandler.getDefaultWebMapContext();          
        }
        this.getRequest().setAttribute( "MapContext", vc );

        Debug.debugMethodEnd();
    }

    /**
     * maps a string representation of a request to a <tt>HashMap</tt>
     */
    protected HashMap toMap( String request ) {
        int p = request.indexOf( '?' );
        if ( p >= 0 ) {
            request = request.substring( p+1, request.length() );
        }
        StringTokenizer st = new StringTokenizer( request, "&" );
        HashMap map = new HashMap();

        while ( st.hasMoreTokens() ) {
            String s = st.nextToken();
            int pos = s.indexOf( '=' );
            String s1 = s.substring( 0, pos );
            String s2 = s.substring( pos + 1, s.length() );
            try {
                map.put( s1.toUpperCase(), 
                         URLDecoder.decode(s2, CharsetUtils.getSystemCharset()) );
            } catch ( UnsupportedEncodingException e ) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * the method returns the scale of the map defined as diagonal size
     * of a pixel at the center of the map.
     */
    protected double getScale( GetMap mrm ) {
        double minx = mrm.getBoundingBox().getMin().getX();
        double maxx = mrm.getBoundingBox().getMax().getX();
        double miny = mrm.getBoundingBox().getMin().getY();
        double maxy = mrm.getBoundingBox().getMax().getY();
        double width = mrm.getWidth();
        double height = mrm.getHeight();

        double sx = Math.sqrt( Math.pow( maxx - minx, 2 ) + Math.pow( maxy - miny, 2 ) );
        double px = Math.sqrt( width * width + height * height );

        return sx / px;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractMapListener.java,v $
Revision 1.7  2006/10/17 20:31:17  poth
*** empty log message ***

Revision 1.6  2006/08/29 19:54:13  poth
footer corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
