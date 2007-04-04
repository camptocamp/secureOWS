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

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletSet;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;


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
public class MapActionPortletPerform extends IGeoPortalPortletPerform {
    
    protected static final ILogger LOG = LoggerFactory.getLogger( MapActionPortletPerform.class );
    
    

    /**
     * 
     * @param request
     * @param portlet
     */
    public MapActionPortletPerform(HttpServletRequest request, Portlet portlet, 
                                   ServletContext sc) {
        super( request, portlet, sc );
    }
    
    @Override
    public void buildNormalContext() throws PortalException {
        super.buildNormalContext();
        
        HttpSession ses = request.getSession();
        List<String[]> wmc = (List<String[]>)ses.getAttribute( AVAILABLE_WMC );
            
        List<String> tmp = StringTools.toList( getInitParam( AVAILABLE_WMC ), ";", false );
        wmc = new ArrayList<String[]>( tmp.size() );
        for (int i = 0; i < tmp.size(); i++) {
            String[] t = StringTools.toArray( tmp.get( i ), "|", false ); 
            wmc.add( t );
        }
        ses.setAttribute( AVAILABLE_WMC, wmc );
        if ( getNamedViewContext( wmc.get( 0 )[0] ) == null ) {
            // initial call - init parameter WMCs has not been read
            for (int i = 0; i < wmc.size(); i++) {
                try {                           
                    if ( getNamedViewContext( wmc.get( i )[0] ) == null ) {
                        File f = new File( wmc.get( i )[1] );
                        if ( !f.isAbsolute() ) {
                            wmc.get( i )[1] = sc.getRealPath( wmc.get( i )[1] );
                        }                
                        File file = new File( wmc.get( i )[1] );      
                        LOG.logDebug( "write context to session: " + wmc.get( i )[0] );
                        
                        setNameContext( wmc.get( i )[0], file.toURL() );
                    }
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new PortalException( e.getMessage() );
                }
            }
                        
        }
        // write map model id for each portlet that has registered an
        // initial WMC into the users session
        PortletSet ps = portlet.getPortletConfig().getPortletSet();
        Enumeration enm = ps.getPortlets();
        while ( enm.hasMoreElements() ) {
            Portlet port = (Portlet)enm.nextElement();
            String cntxId = port.getPortletConfig().getInitParameter( INIT_WMC );
            if ( cntxId != null ) {       
                try {
                    if ( getCurrentViewContext( getInitParam( INIT_MAPPORTLETID ) ) == null ) {                        
                        ViewContext vc = getNamedViewContext( cntxId );
                        setCurrentMapContext( vc, getInitParam( INIT_MAPPORTLETID ) );
                        setCurrentMapContextName( getInitParam( INIT_MAPPORTLETID ), cntxId );
                    }
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new PortalException( e.getMessage() );
                }
            }
        }
       
    }
        
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.17  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.16  2006/08/29 19:54:14  poth
footer corrected

Revision 1.15  2006/08/24 12:15:52  poth
Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)

Revision 1.14  2006/07/06 09:59:04  poth
bug fix - settig correct ID of current map context using more than one WMC (SelectWMCPortlet)

Revision 1.13  2006/06/09 06:27:59  poth
*** empty log message ***

Revision 1.12  2006/06/07 12:19:38  poth
*** empty log message ***

Revision 1.11  2006/06/06 19:02:00  poth
initial upload of SelectWMC portlet

Revision 1.10  2006/04/13 07:49:10  poth
*** empty log message ***

Revision 1.9  2006/04/06 20:25:21  poth
*** empty log message ***

Revision 1.8  2006/03/30 21:20:23  poth
*** empty log message ***

Revision 1.7  2006/03/07 21:40:20  poth
*** empty log message ***

Revision 1.6  2006/02/28 08:47:38  poth
*** empty log message ***

Revision 1.5  2006/02/27 21:58:57  poth
*** empty log message ***

Revision 1.4  2006/02/20 09:28:15  poth
*** empty log message ***

Revision 1.3  2006/02/14 16:05:05  poth
*** empty log message ***

Revision 1.2  2006/02/13 14:13:05  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:11  poth
*** empty log message ***

Revision 1.1  2006/01/09 07:47:09  ap
*** empty log message ***


********************************************************************** */