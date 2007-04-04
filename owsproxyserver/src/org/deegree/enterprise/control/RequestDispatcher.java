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

package org.deegree.enterprise.control;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.servlet.ServletRequestWrapper;

/**
 * This is a <code>RequestDispatcher</code> which creates a event out of a GET
 * or POST requests.
 * <P>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * 
 * @version $Revision: 1.13 $ $Date: 2006/05/23 14:26:57 $
 */
public class RequestDispatcher extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String CONFIGURATION = "Handler.configFile";

    protected transient ApplicationHandler appHandler = null;

    /**
     * This method initializes the servlet.
     * 
     * @param cfg
     *            the servlet configuration
     * 
     * @throws ServletException
     *             an exception
     */
    public void init( ServletConfig cfg )
                            throws ServletException {
        super.init( cfg );

        try {
            String url = null;

            String s = getInitParameter( CONFIGURATION );
            if ( new File( s ).isAbsolute() ) {
                url = s;
            } else {
                url = getServletContext().getRealPath( s );
            }

            this.appHandler = new ApplicationHandler( url );
        } catch ( Exception e ) {
            e.printStackTrace();
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
        // create event out of request
        FormEvent _event = createEvent( request );

        // deliver event to application handler
        deliverEvent( _event );

        // get next page from request attribute
        String nextPage = (String) request.getAttribute( "next" );

        // show error page if next page is null or an error occured
        nextPage = "/" + ( ( nextPage == null ) ? "error.jsp" : nextPage );

        if ( request.getAttribute( "javax.servlet.jsp.jspException" ) != null ) {
            nextPage = "/error.jsp";
        }

        // call request dispatcher
        getServletConfig().getServletContext().getRequestDispatcher( nextPage ).forward( request,
                                                                                         response );
        _event = null;
    }

    /**
     * 
     * 
     * @param request
     * 
     * @return
     */
    protected FormEvent createEvent( HttpServletRequest request ) {
        return new WebEvent( new ServletRequestWrapper( request ) );
    }

    /**
     * 
     * 
     * @param event
     */
    protected void deliverEvent( FormEvent event ) {
        if ( appHandler == null ) {
            try {
                String url = null;

                String s = getInitParameter( CONFIGURATION );
                if ( new File( s ).isAbsolute() ) {
                    url = s;
                } else {
                    url = getServletContext().getRealPath( s );
                }

                this.appHandler = new ApplicationHandler( url );
            } catch ( Exception e ) {
                e.printStackTrace();
            }         
        }
        this.appHandler.actionPerformed( event );
    }
}

/*
 * Changes to this class. What the people haven been up to: $Log:
 * RequestDispatcher.java,v $ Revision 1.2 2005/01/18 22:08:54 poth no message
 * 
 * Revision 1.2 2004/07/21 06:16:00 ap no message
 * 
 * Revision 1.1 2004/06/08 13:03:20 tf refactor to org.deegree.enterprise
 * 
 * Revision 1.1 2004/05/24 07:00:03 ap no message
 * 
 * Revision 1.2 2004/01/03 13:46:45 poth no message
 * 
 * Revision 1.1 2003/07/11 12:47:10 poth no message
 * 
 * Revision 1.1.1.1 2002/03/22 15:23:10 ap no message
 *  
 */
