//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/servlet/GeoDBConfServlet.java,v 1.18 2006/10/17 20:31:19 poth Exp $
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
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.conf.services.geodbconf.CreateDataSourceOperation;
import org.deegree.conf.services.geodbconf.GeoDBConfConfiguration;
import org.deegree.conf.services.geodbconf.GeoDbConfOperation;
import org.deegree.conf.services.geodbconf.RemoveDataSourceOperation;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.WebappResourceResolver;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.owscommon.XMLFactory;


/**
 * This servlet implements the geodb-conf service. The supported operations 
 * (CreateDataSource and RemoveDataSource) 
 * are implemented in doGet.
 * @deprecated
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class GeoDBConfServlet extends HttpServlet {
	
    private static final ILogger LOG = LoggerFactory.getLogger( GeoDBConfServlet.class );

    private GeoDBConfConfiguration config; 

    /**
     * Default constructor
     */
    public GeoDBConfServlet() {
        super();
    }
    
    /** 
     * Initializes this servlet. This method also initializes the configuration
     * for this service. The configuration URL is passed as a initial "geodbconf-config"
     * parameter.
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init( ServletConfig servletConfig ) throws ServletException {
        super.init( servletConfig );

        String configUrlTemp = servletConfig.getInitParameter( "geodbconf-config" );
        URL configURL = null;
        
        if ( config == null ) {
            try {
                this.config = GeoDBConfConfiguration.getInstance();

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
     * Handles the CreateDataSource and RemoveDataSource requests. 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException {
        try {
            
	        //create data store operation; validate request as it goes
	        GeoDbConfOperation dbOperation = createOperation( request );
	        
	        String result = dbOperation.performOperation( request, response);
	        
	        ConfigUtils.sendResponse( dbOperation.getOperationName(), result, response);
        
        } catch (Exception e) {
            this.sendException( response, e);
        }
    }
    
    /**
     * Creates one instance of CreateDataSourceOperation or RemoveDataSourceOperation
     * based on the value of the REQUEST parameter
     * 
     * @param request the servlet request object
     * @return a GeoDBConfOperation, that is either a CreateDataSourceOperation or 
     * RemoveDataSourceOperation
     * @throws Exception if the request value is invalid
     */
    private GeoDbConfOperation createOperation( HttpServletRequest request )
    	throws Exception {
   
    	Map map = KVP2Map.toMap( request );
    	String requestParameter = (String)map.get( "REQUEST" );
        
        if ( requestParameter == null ) {
            throw new InvalidParameterValueException( "OGCRequestFactory",
                "'request' parameter must be set." );
        }

        GeoDbConfOperation dbOperation = null;

        if ( requestParameter.equals(CreateDataSourceOperation.REQUEST_NAME ) ) {
            dbOperation = CreateDataSourceOperation.create( request ); 
        } else if ( requestParameter.equals( RemoveDataSourceOperation.REQUEST_NAME )) {
            dbOperation = RemoveDataSourceOperation.create( request ); 
        } else {
            throw new OGCWebServiceException( "OGCRequestFactory", "Specified request '"
                + requestParameter + "' is not a known service request." );
        }
        
        if ( dbOperation != null ){
            dbOperation.setConfiguration( config );
        }
        
        return dbOperation;
    }
    
    /* conveniece method to send an exception back to the service as XML */
    private void sendException( HttpServletResponse response, Exception e ) {
        
//LOG.logInfo( "Sending OGCWebServiceException to client." );
        OGCWebServiceException oe = new OGCWebServiceException( e.getMessage() );
        ExceptionReport report = new ExceptionReport( new OGCWebServiceException[] { oe } );
        e.printStackTrace();
        try {
            response.setContentType( "application/vnd.ogc.se_xml" );
            XMLFragment doc = XMLFactory.export( report );
            OutputStream os = response.getOutputStream();
            doc.write( os );
            os.close();
        } catch (Exception ex) {
            
//LOG.logError( "ERROR: " + ex.getMessage(), ex );
        }
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoDBConfServlet.java,v $
Revision 1.18  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.17  2006/08/24 06:38:30  poth
File header corrected

Revision 1.16  2006/04/06 20:25:24  poth
*** empty log message ***

Revision 1.15  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.14  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.13  2005/12/12 09:39:49  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.12  2005/12/08 15:30:02  taddei
refactoring: send mesgs and exception are now in utilities class

Revision 1.11  2005/12/07 15:20:45  taddei
small refactoring, more javadoc

Revision 1.10  2005/12/07 15:11:42  taddei
javadoc

Revision 1.9  2005/12/07 14:52:21  taddei
added KVP2Map support (case insensitiveness)

Revision 1.8  2005/12/05 13:11:46  taddei
clean up

Revision 1.7  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.6  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */