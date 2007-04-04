// $Id: OGCServletController.java,v 1.66 2006/11/22 14:06:26 schmitz Exp $
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

package org.deegree.enterprise.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.enterprise.AbstractOGCServlet;
import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.WebappResourceResolver;
import org.deegree.framework.version.Version;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.OGCRequestFactory;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.owscommon.XMLFactory;

/**
 * An <code>OGCServletController</code> handles all incoming requests. The controller for all OGC
 * service requests. Dispatcher to specific handler for WMS, WFS and other.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 *
 * @author last edited by: $Author: schmitz $
 * 
 * @see <a
 *      href="http://java.sun.com/blueprints/corej2eepatterns/Patterns/FrontController.html">Front
 *      controller </a>
 */
public class OGCServletController extends AbstractOGCServlet {

    /**
     * address is the url of the client which requests.
     */
    public static String address = null;

    private static final long serialVersionUID = -4461759017823581221L;

    private static final ILogger LOG = LoggerFactory.getLogger( OGCServletController.class );

    private static final String SERVICE = "services";

    private static final String HANDLER_CLASS = ".handler";

    private static final String HANDLER_CONF = ".config";

    private static final Map<Class, String> SERVICE_FACTORIES_MAPPINGS = new HashMap<Class, String>();

    private static final String ERR_MSG = "Can't set configuration for {0}";

    /**
     * 
     * 
     * @param request 
     * @param response 
     * @throws ServiceException 
     * @TODO refactor and optimize code for initializing handler
     */
    public void doService( HttpServletRequest request, HttpServletResponse response )
                            throws ServiceException {

        Long time = new Long( System.currentTimeMillis() );

        LOG.logInfo( StringTools.concat( 500, "Incoming request from ", request.getRemoteAddr(),
                                         "/", request.getRemoteHost(), " - ", time ) );

        address = request.getRequestURL().toString();
        LOG.logInfo( StringTools.concat( 500, "requested server address: ", address ) );

        try {

            OGCWebServiceRequest ogcRequest = OGCRequestFactory.create( request );

            LOG.logInfo( StringTools.concat( 500, "Handling request '", ogcRequest.getId(),
                                             "' from '", request.getRemoteAddr(),
                                             "' to service: '", ogcRequest.getServiceName(), "'" ) );

            // get service from request
            String service = ogcRequest.getServiceName().toUpperCase();

            // get handler instance
            ServiceDispatcher handler = ServiceLookup.getInstance().getHandler( service );
            // dispatch request to specific handler
            handler.perform( ogcRequest, response );
        } catch ( OGCWebServiceException e ) {
            LOG.logError( e.getMessage(), e );
            sendException( response, e, request );
        } catch ( ServiceException e ) {
            if ( e.getNestedException() instanceof OGCWebServiceException ) {
                sendException( response, (OGCWebServiceException) e.getNestedException(), request );
            } else {
                sendException( response, new OGCWebServiceException( this.getClass().getName(),
                                                                     e.getMessage() ), request );
            }
            LOG.logError( e.getMessage(), e );
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
            throw new ServiceException( e );
        }
    }

    /**
     * Sends the passed <tt>OGCWebServiceException</tt> to the calling client.
     * 
     * @param response
     * @param e
     */
    private void sendException( HttpServletResponse response, OGCWebServiceException e,
                               HttpServletRequest request ) {
        LOG.logInfo( "Sending OGCWebServiceException to client." );
        ExceptionReport report = new ExceptionReport( new OGCWebServiceException[] { e } );

        // according to the JavaDoc, the map always has this type
        Map<String, String[]> map = request.getParameterMap();

        boolean isWMS130 = false;
        for ( String str : map.keySet() ) {
            if ( str.equalsIgnoreCase( "version" ) ) {
                String[] version = map.get( str );
                if ( version != null && version.length > 0 && version[0].equals( "1.3.0" ) ) {
                    isWMS130 = true;
                }
            }
        }

        try {
            XMLFragment doc;
            if ( isWMS130 ) {
                response.setContentType( "text/xml" );
                doc = XMLFactory.exportNS( report );
            } else {
                response.setContentType( "application/vnd.ogc.se_xml" );
                doc = XMLFactory.export( report );
            }
            OutputStream os = response.getOutputStream();
            doc.write( os );
            os.close();
        } catch ( Exception ex ) {
            LOG.logError( "ERROR: " + ex.getMessage(), ex );
        }
    }

    /**
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
                            throws ServletException, IOException {
        LOG.logDebug( "query string ", request.getQueryString() );
        try {
            if ( request.getParameter( "RELOADDEEGREE" ) != null ) {
                reloadServices( request, response );
            } else {
                this.doService( request, response );
            }
        } catch ( ServiceException e ) {
            LOG.logError( e.getMessage(), e );
            this.handleException( e.getMessage(), e, response );
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void reloadServices( HttpServletRequest request, HttpServletResponse response )
                            throws ServletException, IOException {
        Map map = KVP2Map.toMap( request );
        String user = (String) map.get( "USER" );
        String password = (String) map.get( "PASSWORD" );
        String message = null;
        if ( getInitParameter( "USER" ) != null && getInitParameter( "PASSWORD" ) != null
             && getInitParameter( "USER" ).equals( user )
             && getInitParameter( "PASSWORD" ).equals( password ) ) {
            initServices( getServletContext() );
            ctDestroyed();
            message = Messages.getString( "OGCServletController.reloadsuccess" );
        } else {
            message = Messages.getString( "OGCServletController.reloadfailed" );
        }
        PrintWriter pw = response.getWriter();
        pw.print( message );
        pw.flush();
        pw.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost( HttpServletRequest arg0, HttpServletResponse arg1 )
                            throws ServletException, IOException {

        try {
            this.doService( arg0, arg1 );
        } catch ( ServiceException e ) {
            LOG.logError( e.getMessage(), e );
            this.handleException( e.getMessage(), e, arg1 );
        }
    }

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init()
                            throws ServletException {
        super.init();

        LOG.logDebug( "Logger for " + this.getClass().getName() + " initialized." );

        SERVICE_FACTORIES_MAPPINGS.put( CSWHandler.class,
                                        "org.deegree.ogcwebservices.csw.CSWFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WFSHandler.class,
                                        "org.deegree.ogcwebservices.wfs.WFServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WCSHandler.class,
                                        "org.deegree.ogcwebservices.wcs.WCServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WMSHandler.class,
                                        "org.deegree.ogcwebservices.wms.WMServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( SOSHandler.class,
                                        "org.deegree.ogcwebservices.sos.SOServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WPVSHandler.class,
                                        "org.deegree.ogcwebservices.wpvs.WPVServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WMPSHandler.class,
                                        "org.deegree.ogcwebservices.wmps.WMPServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WPSHandler.class,
                                        "org.deegree.ogcwebservices.wps.WPServiceFactory" );
        SERVICE_FACTORIES_MAPPINGS.put( WASSHandler.class,
                                        "org.deegree.ogcwebservices.wass.common.WASServiceFactory" );

        LOG.logInfo( "Starting deegree version " + Version.getVersion() + " on server: "
                     + this.getServletContext().getServerInfo() + " / Java version: "
                     + System.getProperty( "java.version" ) );
        LOG.logInfo( "Initializing OGC services in context "
                     + this.getServletContext().getServletContextName() + " and real path "
                     + this.getServletContext().getRealPath( "/index.jsp" ) );

        this.initServices( getServletContext() );

        LOG.logInfo( "Installed OGC services: " + this.getServiceList() + " on server:" );
        try {
            LOG.logInfo( "- IP: " + InetAddress.getLocalHost().getHostAddress() );
            LOG.logInfo( "- Host name: " + InetAddress.getLocalHost().getHostName() );
            LOG.logInfo( "- Domain name: " + InetAddress.getLocalHost().getCanonicalHostName() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
        //Sets the attributes for tomcat -> application.getAttribute(); in jsp sites
        this.getServletContext().setAttribute( "deegree_ogc_services", this.getServiceList() );
    }

    private void initServices( ServletContext context )
                            throws ServletException {

        // get list of OGC services
        String serviceList = this.getRequiredInitParameter( SERVICE );

        String[] serviceNames = StringTools.toArray( serviceList, ",", false );

        ServiceLookup lookup = ServiceLookup.getInstance();
        for ( int i = 0; i < serviceNames.length; i++ ) {
            LOG.logInfo( StringTools.concat( 100, "- Initializing ", serviceNames[i].toUpperCase(),
                                             " -" ) );
            try {
                String className = this.getRequiredInitParameter( serviceNames[i] + HANDLER_CLASS );
                Class handlerClzz = Class.forName( className );

                // initialize each service factory
                String s = this.getRequiredInitParameter( serviceNames[i] + HANDLER_CONF );
                URL serviceConfigurationURL = WebappResourceResolver.resolveFileLocation( s,
                                                                                          context,
                                                                                          LOG );

                // set configuration
                LOG.logInfo( StringTools.concat( 300, "Reading configuration for ",
                                                 serviceNames[i].toUpperCase(), " from URL: '",
                                                 serviceConfigurationURL, "'." ) );

                String factoryClassName = SERVICE_FACTORIES_MAPPINGS.get( handlerClzz );

                Class factory = Class.forName( factoryClassName );
                Method method = factory.getMethod( "setConfiguration", new Class[] { URL.class } );
                method.invoke( factory, new Object[] { serviceConfigurationURL } );

                // put handler to available service list
                lookup.addService( serviceNames[i].toUpperCase(), handlerClzz );

                LOG.logInfo( StringTools.concat( 300, serviceNames[i].toUpperCase(),
                                                 " successfully initialized." ) );
            } catch ( ServletException e ) {
                LOG.logError( e.getMessage(), e );
            } catch ( InvocationTargetException e ) {
                e.getTargetException().printStackTrace();
                LOG.logError( this.produceMessage( ERR_MSG, new Object[] { serviceNames[i] } ), e );
            } catch ( Exception e ) {
                LOG.logError( "Can't initialize OGC service:" + serviceNames[i], e );
            }
        }
    }

    private String getRequiredInitParameter( String name )
                            throws ServletException {
        String paramValue = getInitParameter( name );
        if ( paramValue == null ) {

            String msg = "Required init parameter '" + name + "' missing in web.xml";
            LOG.logError( msg );
            throw new ServletException( msg );
        }
        return paramValue;
    }

    /**
     * @return the services, separated by ","
     */
    private String getServiceList() {

        StringBuffer buf = new StringBuffer();
        ServiceLookup lookup = ServiceLookup.getInstance();
        for ( Iterator iter = lookup.getIterator(); iter.hasNext(); ) {
            String serviceName = (String) iter.next();
            buf.append( serviceName );
            if ( iter.hasNext() ) {
                buf.append( ',' );
            }
        }
        return buf.toString();
    }

    /**
     * Formats the provided string and the args array into a String using MessageFormat.
     * @param pattern 
     * @param args 
     * @return the message to present the client.
     */
    private String produceMessage( String pattern, Object[] args ) {
        return new MessageFormat( pattern ).format( args );
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void ctDestroyed() {
        LOG.logInfo( "Stopping context: " );

        ServiceLookup lookup = ServiceLookup.getInstance();
        for ( Iterator iter = lookup.getIterator(); iter.hasNext(); ) {
            String serviceName = (String) iter.next();
            LOG.logInfo( "Stopping service " + serviceName );

            try {
                String s = SERVICE_FACTORIES_MAPPINGS.get( lookup.getService( serviceName ) );
                Class clzz = Class.forName( s );
                // TODO stop and reset all service instances
                Method[] methods = clzz.getMethods();
                for ( int j = 0; j < methods.length; j++ ) {
                    if ( methods[j].getName().equals( "reset" ) ) {
                        Object[] args = new Object[0];
                        methods[j].invoke( clzz.newInstance(), args );
                    }
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }
    }

    /**
     * @see javax.servlet.Servlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
    }
}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: OGCServletController.java,v $
 Revision 1.66  2006/11/22 14:06:26  schmitz
 Fixed some minor details in the WMS example configuration.
 Added CRS:84 to proj4.
 Fixed exception handling for WMS.

 Revision 1.65  2006/10/31 20:07:13  poth
 not used code removed

 Revision 1.64  2006/09/23 09:01:53  poth
 *** empty log message ***

 Revision 1.63  2006/09/08 15:14:38  schmitz
 Updated the core of deegree to use the HttpServletRequest methods
 to create the KVP maps, and not to try to parse as XML every time.
 Updated the tests to create maps for testing instead of strings.
 Updated the OWSProxy subsystem to use ServletRequest classes instead
 of strings for request dispatching.

 Revision 1.62  2006/09/08 08:42:02  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.61  2006/08/17 20:08:57  poth
 code formating

 Revision 1.60  2006/07/26 18:50:54  mschneider
 Improved log messages.

 Revision 1.59  2006/07/23 10:05:54  poth
 setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

 Revision 1.58  2006/07/21 09:33:10  poth
 setting character encoding changed

 Revision 1.57  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */
