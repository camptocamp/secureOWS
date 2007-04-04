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
Aennchenstraße 19
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

package org.deegree.conf.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.conf.services.wfsconf.AddFeatureTypeOperation;
import org.deegree.conf.services.wfsconf.RemoveFeatureTypeOperation;
import org.deegree.conf.services.wfsconf.WFSConfConfiguration;
import org.deegree.conf.services.wfsconf.WFSConfOperation;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.WebappResourceResolver;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 * This servlet implements the wfs-conf service. The supported operations 
 * (AddFeatureType and RemoveFeatureType) 
 * are implemented in doGet.
 * @deprecated
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class WFSConfServlet extends HttpServlet {

    private static final ILogger LOG = LoggerFactory.getLogger( WFSConfServlet.class );

    private WFSConfConfiguration config;
    
    /**
     * Default constructor
     */
    public WFSConfServlet() {
        super();
    }

    /** 
     * Initializes this servlet. This method also initializes the configuration
     * for this service. The configuration URL is passed as a initial "wfsconf-config"
     * parameter.
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init( ServletConfig servletConfig ) throws ServletException {
        super.init( servletConfig );
        String configUrlTemp = servletConfig.getInitParameter( "wfsconf-config" );
        URL configURL = null;
        
        
        if ( config == null ) {
            try {
                this.config = WFSConfConfiguration.getInstance();
                ServletContext ctxt = getServletContext();
                configURL = WebappResourceResolver.resolveFileLocation(  configUrlTemp, ctxt, LOG );                
                this.config.initFromUrl( configURL );

            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException( e );
            }            
        }
    }
    
    /**
     * Handles the AddFeatureType and RemoveFeatureType requests. 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException {
        
        try {

            // create
	        WFSConfOperation wfsConfOperation = createOperation( request );
	        
	        // perform
	        String result = wfsConfOperation.performOperation( request );
	        
	        //inform user
	        ConfigUtils.sendResponse( wfsConfOperation.getOperationName(), result, response);
        
        } catch (Exception e) {
            ConfigUtils.sendException( response, e);
        }
    }

    /**
     * Creates one instance of AddFeatureType or RemoveFeatureType
     * based on the value of the REQUEST parameter
     * @param request the servlet request object
     * @return a WFSConfOperation, that is, either a AddFeatureType or RemoveFeatureType
     * @throws Exception if the request value is invalid
     */
    private WFSConfOperation createOperation( HttpServletRequest request ) throws Exception {
   
    	
    	Map map = KVP2Map.toMap( request );
    	String requestParameter = (String)map.get( "REQUEST" );
    	
        if ( requestParameter == null ) {
            throw new InvalidParameterValueException( "OGCRequestFactory",
                "'REQUEST' parameter must be set." );
        }

        WFSConfOperation ftOperation = null;
        
        if ( requestParameter.equals(AddFeatureTypeOperation.REQUEST_NAME ) ) {
        	ftOperation = AddFeatureTypeOperation.create( request );
        } else if ( requestParameter.equals( RemoveFeatureTypeOperation.REQUEST_NAME )) {
        	ftOperation = RemoveFeatureTypeOperation.create( request );
        } else {
            throw new OGCWebServiceException( "OGCRequestFactory", "Specified request '"
                + requestParameter + "' is not a known service request." );
        }
        
        
        if ( ftOperation != null ){
            ftOperation.setConfiguration( config );
        }
        
        return ftOperation;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WFSConfServlet.java,v $
Revision 1.15  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.14  2006/08/24 06:38:30  poth
File header corrected

Revision 1.13  2006/04/06 20:25:24  poth
*** empty log message ***

Revision 1.12  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.11  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.10  2005/12/21 17:30:10  poth
no message

Revision 1.9  2005/12/12 09:39:49  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.8  2005/12/08 15:30:02  taddei
refactoring: send mesgs and exception are now in utilities class

Revision 1.7  2005/12/07 15:20:04  taddei
javadoc

Revision 1.6  2005/12/07 14:52:43  taddei
added KVP2Map support (case insensitiveness)

Revision 1.5  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.4  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */