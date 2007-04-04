
package org.deegree.portal.standard.gazetteer.control;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.portal.standard.gazetteer.configuration.GazetteerClientConfiguration;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author Administrator
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/10/05 09:48:53 $
 */
public class GazetteerClientRequestDispatcher extends org.deegree.enterprise.control.RequestDispatcher {
    
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
            String tmp = getInitParameter( "Client.configFile" );
            File file = new File( tmp );
            if ( !file.isAbsolute() ) {
                tmp = getServletContext().getRealPath( tmp );
                file = new File( tmp );
            }
            GazetteerClientConfiguration.getInstance( file.toURL() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new ServletException( "could not create GazetteerClientConfiguration" +
                                        e.getMessage() );
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
               
        request.setAttribute( "CONFIGURATION",  GazetteerClientConfiguration.getInstance() );
       
        // call request dispatcher
        getServletConfig().getServletContext().getRequestDispatcher( nextPage ).forward( request, response );
        _event = null;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:

$Log: GazetteerClientRequestDispatcher.java,v $
Revision 1.5  2006/10/05 09:48:53  mays
enable keyword expansion

Revision 1.4  2006/10/05 09:31:12  mays
initializing the configuration now works with absolute AND relative paths

Revision 1.3  2006/08/29 19:54:14  poth
footer corrected

Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
