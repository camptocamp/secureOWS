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
package org.deegree.enterprise.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.OGCRequestFactory;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;

/**
 * simple proxy servlet  The servlet is intended to run in its own
 * context combined with ServletFilter (e.g. OWSProxyServletFilter )
 * to filter out invalid requests/responses
 * 
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author$
 * 
 * @version 1.0. $Revision$, $Date$
 * 
 * @since 1.1
 */
public class SimpleProxyServlet extends HttpServlet {

    private ILogger LOG = LoggerFactory.getLogger( SimpleProxyServlet.class );

    private static final long serialVersionUID = 3086952074808203858L;

    private Map<String, String> host = null;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init()
                            throws ServletException {
        super.init();
        host = new HashMap<String, String>();
        Enumeration enu = getInitParameterNames();
        while ( enu.hasMoreElements() ) {
            String pn = (String) enu.nextElement();
            String[] tmp = StringTools.toArray( pn, ":", false );
            String hostAddr = this.getInitParameter( pn );
            host.put( tmp[0], hostAddr );
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     * 											 javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
                            throws ServletException, IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            String query = request.getQueryString();
            String service = getService( KVP2Map.toMap( query ) );
            String hostAddr = host.get( service );
            String req = hostAddr + "?" + query;
            URL url = new URL( req );
            LOG.logDebug( "forward URL: " + url );

            URLConnection con = url.openConnection();
            con.setDoInput( true );
            con.setDoOutput( false );
            is = con.getInputStream();
            response.setContentType( con.getContentType() );
            os = response.getOutputStream();
            int c = 0;
            while ( ( c = is.read() ) > -1 ) {
                os.write( c );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            response.setContentType( "text/plain; charset=" + CharsetUtils.getSystemCharset() );
            os.write( StringTools.stackTraceToString( e ).getBytes() );
        } finally {
            try {
                is.close();
            } catch ( Exception e ) {
            }
            try {
                os.close();
            } catch ( Exception e ) {
            }
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, 
     * 											  javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost( HttpServletRequest origReq, HttpServletResponse response )
                            throws ServletException, IOException {
        // wrap request to enable access of the requests InputStream more
        // than one time
        ServletRequestWrapper request = new ServletRequestWrapper( origReq );
        OutputStream os = null;
        try {
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                // because this is an expensive operation it just will
                // performed if debug level is set too DEBUG
                InputStream reqIs = request.getInputStream();            
                StringBuffer sb = new StringBuffer( 10000 );
                int c = 0;
                while ( ( c = reqIs.read() ) > -1 ) {
                    sb.append( (char) c );
                }
                reqIs.close();
                LOG.logDebug( "Request: " + sb );
            }            
            OGCWebServiceRequest req = OGCRequestFactory.create( request );

            String hostAddr = host.get( req.getServiceName() );
            LOG.logDebug( "forward URL: " + hostAddr );
            if ( hostAddr == null ) {
                throw new Exception( Messages.getMessage( "PROXY_SERVLET_UNDEFINED_HOST", 
                                                          req.getServiceName() ) );
            }
            
            // determine charset for setting request content type
            // use system charset if no charset can be determined
            // from incoming request
            String charset = origReq.getCharacterEncoding();
            LOG.logDebug( "request character encoding: ", charset );
            if ( charset == null ) {
                charset = CharsetUtils.getSystemCharset();
                LOG.logDebug( "use sytem character encoding: ", charset );
            }
            
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod( hostAddr );
            post.setRequestHeader( "Content-type", "text/xml; charset=" + charset );
            post.setRequestEntity( new InputStreamRequestEntity( request.getInputStream() ) );
            client.executeMethod( post );

            LOG.logDebug( "Content-type: ", post.getResponseHeader( "Content-type" ) );
            
            os = response.getOutputStream();
            os.write( post.getResponseBody() );
        } catch ( Exception e ) {
            e.printStackTrace();
            response.setContentType( "text/plain; charset=" + CharsetUtils.getSystemCharset() );
            os.write( StringTools.stackTraceToString( e ).getBytes() );
        } finally {
            try {
                os.close();
            } catch ( Exception e ) {
            }
        }
    }

    /**
     * @return the name of the service that is targeted by the passed
     * KVP encoded request
     * 
     * @param map
     * @throws Exception
     */
    private String getService( Map map )
                            throws Exception {
        String service = null;
        String req = (String) map.get( "REQUEST" );
        if ( "WMS".equals( map.get( "SERVICE" ) ) || req.equals( "GetMap" )
             || req.equals( "GetFeatureInfo" ) || req.equals( "GetLegend" ) ) {
            service = "WMS";
        } else if ( "WFS".equals( map.get( "SERVICE" ) ) || req.equals( "DescribeFeatureType" )
                    || req.equals( "GetFeature" ) ) {
            service = "WFS";
        } else if ( "WCS".equals( map.get( "SERVICE" ) ) || req.equals( "GetCoverage" )
                    || req.equals( "DescribeCoverage" ) ) {
            service = "WCS";
        } else if ( "CSW".equals( map.get( "SERVICE" ) ) ) {
            service = "CSW";
        } else {
            throw new OGCWebServiceException( "unknown service/request: " + map.get( "SERVICE" ) );
        }
        return service;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log$
 Revision 1.17  2006/11/24 12:51:11  poth
 *** empty log message ***

 Revision 1.16  2006/11/16 15:04:11  poth
 explicit setting of content type and character encoding added

 Revision 1.15  2006/10/22 20:32:08  poth
 support for vendor specific operation GetScaleBar removed

 Revision 1.14  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.13  2006/09/08 15:14:38  schmitz
 Updated the core of deegree to use the HttpServletRequest methods
 to create the KVP maps, and not to try to parse as XML every time.
 Updated the tests to create maps for testing instead of strings.
 Updated the OWSProxy subsystem to use ServletRequest classes instead
 of strings for request dispatching.

 Revision 1.12  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.11  2006/08/14 13:41:38  poth
 determination of service type for HTTP post enhanced

 Revision 1.10  2006/07/23 10:05:54  poth
 setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

 Revision 1.9  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */
