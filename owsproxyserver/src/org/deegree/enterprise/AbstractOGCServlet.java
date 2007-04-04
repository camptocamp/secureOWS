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

----------------------------------------------------------------------------*/
package org.deegree.enterprise;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.Marshallable;
import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 * Abstract servlet that serves as an OCC-compliant HTTP-frontend to any
 * OGC-WebService (WFS, WMS, ...).
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.9 $ $Date: 2006/11/27 09:07:53 $
 * @todo refactoring required, move to package servlet
 */
public abstract class AbstractOGCServlet extends HttpServlet {
    
    protected static Map flag = null;
    static {
        if ( flag == null ) {
            flag = Collections.synchronizedMap(new HashMap());
        }
    }
       
    /**
     * Called by the servlet container to indicate that the servlet is being
     * placed into service. Sets the debug level according to the debug
     * parameter defined in the ServletEngine's environment.
     * <p>
     * <p>
     * @param servletConfig servlet configuration
     * @throws ServletException exception if something occurred that interferes
     *         with the servlet's normal operation
     */
    public void init (ServletConfig servletConfig) throws ServletException {    
        super.init (servletConfig);
    }    
    
    /**
     * handles fatal errors by creating a OGC exception XML and sending it back
     * to the client
     * 
     * @deprecacted
     */
    protected void handleException(String msg, Exception ex, HttpServletResponse response) {
        String tmp = StringTools.stackTraceToString( ex );
        getServletContext().log (msg +  tmp );
        OGCWebServiceException wex = 
            new OGCWebServiceException( this.getClass().getName(), tmp );
        try {
            PrintWriter pw = response.getWriter();
            pw.write( ((Marshallable)wex).exportAsXML() );
            pw.close();
        } catch (Exception e) {
            getServletContext().log( e.toString() );
        }
    }      
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractOGCServlet.java,v $
Revision 1.9  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
