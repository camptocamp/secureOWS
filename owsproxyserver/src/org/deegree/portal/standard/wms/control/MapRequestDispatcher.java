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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.WebMapContextFactory;
import org.deegree.portal.standard.wms.Constants;

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
 *
 * @version $Revision: 1.8 $ $Date: 2006/08/29 19:54:13 $
 *
 */
public class MapRequestDispatcher extends org.deegree.enterprise.control.RequestDispatcher {              
    
    private ViewContext vc = null;
        
    /**
     * This method initializes the servlet.
     *
     * @param   cfg  the servlet configuration
     *
     * @throws  ServletException  an exception
     */
    public void init( ServletConfig cfg ) throws ServletException {
        super.init( cfg );

        String controllerFile = getInitParameter( "Handler.configFile" );
        if ( ! ( new File( controllerFile ).exists() ) ) {
            controllerFile = getServletContext().getRealPath( controllerFile );
        }
        String clientContext = this.getInitParameter( "MapContext.configFile" );
        if ( ! ( new File( clientContext ).exists() ) ) {
            clientContext = getServletContext().getRealPath( clientContext );
            try {
                File file = new File( clientContext );
                vc = WebMapContextFactory.createViewContext( file.toURL(), null, null );
                appHandler = new MapApplicationHandler( controllerFile, vc );
            } catch(Exception e) {
                e.printStackTrace();
            }
        }        
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
        super.service( request, response );
    }
    

}

/*
 * Changes to this class. What the people haven been up to:
 * $Log: MapRequestDispatcher.java,v $
 * Revision 1.8  2006/08/29 19:54:13  poth
 * footer corrected
 *
 * Revision 1.7  2006/08/24 12:15:52  poth
 * Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)
 *
 * Revision 1.6  2006/05/12 11:17:41  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/06 20:25:31  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/04/04 20:39:44  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/30 21:20:28  poth
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/05 20:33:09  poth
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/05 09:30:12  poth
 * *** empty log message ***
 *
 * Revision 1.1.1.1  2005/01/05 10:29:55  poth
 * no message
 *
 * Revision 1.4  2004/06/23 15:31:28  ap
 * no message
 *
 * Revision 1.2  2004/06/08 13:03:20  tf
 * refactor to org.deegree.enterprise
 *
 * Revision 1.1  2004/05/22 09:55:36  ap
 * no message
 *
 * Revision 1.2  2004/01/03 13:46:45  poth
 * no message
 *
 * Revision 1.1  2003/07/11 12:29:58  poth
 * no message
 *
 * Revision 1.1.1.1  2002/03/22 15:23:10  ap
 * no message
 *
 *
 */
