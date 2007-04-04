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

package org.deegree.portal.standard;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.RequestDispatcher;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.version.Version;
import org.deegree.portal.Constants;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.WebMapContextFactory;



/**
 * This is a <code>RequestDispatcher</code> which creates a event out of
 * a GET or POST requests.<P>
 *
 * Furthermore this class implements
 *
 * <HR>
 * <B>Design Patterns:</B>:<BR>
 *
 * The following Design Patterns are used:
 * <UL>
 * <LI> Proxy
 * </UL>
 *
 * @author  <a href="mailto:friebe@gmx.net">Torsten Friebe</a>
 * @author  <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 *
 * @version $Revision: 1.9 $ $Date: 2006/08/29 19:54:14 $
 *
 */
public class PortalRequestDispatcher extends RequestDispatcher {
    
    private static final ILogger LOG = 
        LoggerFactory.getLogger(  PortalRequestDispatcher.class );
    
    protected ViewContext vc = null;
        
    /**
     * This method initializes the servlet.
     *
     * @param   cfg  the servlet configuration
     *
     * @throws  ServletException  an exception
     */
    public void init( ServletConfig cfg ) throws ServletException {
        super.init( cfg );

        String clientContext = this.getInitParameter( "MapContext.configFile" );
        if ( ! ( new File( clientContext ).exists() ) ) {
            clientContext = getServletContext().getRealPath( clientContext );
        }
        try {
            File file = new File( clientContext );
            vc = WebMapContextFactory.createViewContext( file.toURL(), null, null );
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        try {
            if ( this.getInitParameter( "UserRepository" ) != null ) {
                URL userRepository = new URL( this.getInitParameter( "UserRepository" ) );
                getServletContext().setAttribute( Constants.USERREPOSITORY, userRepository );
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        
        LOG.logInfo( "Starting deegree version "
                + Version.getVersion() + " on server: " + this.getServletContext().getServerInfo()
                + " / Java version: " + System.getProperty( "java.version" ) );
    }

    /**
     *
     *
     * @param request 
     * @param response 
     *
     * @throws ServletException 
     * @throws IOException 
     */
    protected void service( HttpServletRequest request, HttpServletResponse response )
                    throws ServletException, IOException {
        HttpSession session = request.getSession( true );
        session.setAttribute( Constants.DEFAULTMAPCONTEXT , vc );
        if ( session.getAttribute( Constants.CURRENTMAPCONTEXT ) == null ) {
            session.setAttribute( Constants.CURRENTMAPCONTEXT , vc );
        }        
        super.service( request, response );
    }

}

/*
 * Changes to this class. What the people haven been up to:
 * $Log: PortalRequestDispatcher.java,v $
 * Revision 1.9  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.8  2006/08/24 12:15:52  poth
 * Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)
 *
 * Revision 1.7  2006/05/22 09:30:51  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/04/06 20:25:32  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/04 20:39:44  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:29  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/08 09:20:26  poth
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/05 20:33:09  poth
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/05 09:30:12  poth
 * *** empty log message ***
 *
 * Revision 1.2  2005/04/06 13:44:07  poth
 * no message
 *
 * Revision 1.2  2004/10/11 07:23:02  poth
 * no message
 *
 * Revision 1.1  2004/09/20 15:26:24  poth
 * no message
 *
 */
