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
package org.deegree.portal.standard.sos.control;


import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.WebappResourceResolver;
import org.deegree.portal.standard.sos.configuration.SOSClientConfiguration;


/**
 * Client request dispatcher based on CSW's one.
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 */
public class SOSClientRequestDispatcher extends org.deegree.enterprise.control.RequestDispatcher {
    
    private static final ILogger LOG = LoggerFactory.getLogger( SOSClientRequestDispatcher.class );

    
    /**
     * Comment for <code>ERROR_PAGE</code>
     */
    static String ERROR_PAGE = "error.jsp";
    
    /**
     * This method initializes the servlet.
     *
     * @param   cfg  the servlet configuration
     *
     * @throws  ServletException  an exception
     */
    public void init( ServletConfig cfg ) throws ServletException {
        super.init( cfg );        
       
        try {
            // initialize configuration of client and data servers
            URL url = WebappResourceResolver.resolveFileLocation( getInitParameter( "Client.configFile" ),
                this.getServletContext(), LOG);
            SOSClientConfiguration.getInstance( url );
            
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new ServletException( StringTools.stackTraceToString( e.getStackTrace() ) );
        }
        
        if ( getInitParameter( "Client.errorPage" ) != null ) {
            ERROR_PAGE = getInitParameter( "Client.errorPage" );
        }
    }
    
     protected void service(HttpServletRequest request, HttpServletResponse response)
                           throws ServletException, IOException {
                                  
        // create event out of request                               
        FormEvent _event = createEvent( request );

        // deliver event to application handler
        deliverEvent( _event );
        
        // get next page from request attribute
        String nextPage = (String) request.getAttribute( "next" );

        // show error page if next page is null
        if ( nextPage == null ) nextPage = ERROR_PAGE;
        nextPage = "/" + nextPage;
        
        if ( request.getAttribute("javax.servlet.jsp.jspException") != null ) {
            nextPage = "/" + ERROR_PAGE;
        }
        
        try {
            request.setAttribute( "CONFIGURATION",  SOSClientConfiguration.getInstance() );
        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException( "could not create SOS client configuration" );
        }
        //FIXME throwing nasty exception when plaform doesn't exist -> handle this
        // this exception is not showing up on error.jsp
        // call request dispatcher
        getServletConfig().getServletContext().getRequestDispatcher( nextPage ).forward( request, response );
        _event = null;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSClientRequestDispatcher.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
