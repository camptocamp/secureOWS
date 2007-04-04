//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/DoService.java,v 1.12 2006/08/24 06:42:16 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wass.wss.operation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wass.common.AbstractRequest;
import org.deegree.ogcwebservices.wass.common.AuthenticationData;
import org.deegree.ogcwebservices.wass.common.URN;
import org.w3c.dom.Element;

/**
 * The <code>DoService</code> class represents (a bean) a DoService Operation which is send by a client (or other
 * server) which is checked by the wss for the right credentials and than send to the requested
 * serviceprovider. In the case that a client not has the right credentials a ServiceException is
 * thrown. The Specification does mention the fact that ther might be another response for example:
 * A client orders A and B but only has the credentials for A -> should we return A and not B or
 * nothing at all. We do the last, the client gets nothing.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class DoService extends AbstractRequest {

    private static final long serialVersionUID = -8538267299180579690L;
    
    /**
     * The logger enhances the quality and simplicity of Debugging within the deegree2 framework
     */
    private static final ILogger LOG = LoggerFactory.getLogger( DoService.class );

    private AuthenticationData authenticationData = null;

    private String dcp = null;

    private ArrayList<RequestParameter> requestParameters = null;

    private String payload = null;

    private URI facadeURL = null;

    /**
     * @param id the request id
     * @param service
     * @param version
     * @param authenticationData
     * @param dcp
     * @param requestParameters
     * @param payload
     * @param facadeURL
     */
    public DoService( String id, String service, String version, AuthenticationData authenticationData,
                     String dcp, ArrayList<RequestParameter> requestParameters, String payload,
                     URI facadeURL ) {
        super( id, version, service, "DoService" );
        this.authenticationData = authenticationData;
        this.dcp = dcp;
        this.requestParameters = requestParameters;
        this.payload = payload;
        this.facadeURL = facadeURL;
    }

    /**
     * @param id the request id
     * @param keyValues
     */
    public DoService( String id, Map<String, String> keyValues ) {
        super( id, keyValues );
        LOG.entering();
        this.authenticationData = new AuthenticationData( new URN( keyValues.get( "AUTHMETHOD" ) ),
                                                          keyValues.get( "CREDENTIALS" ) );
        this.dcp = keyValues.get( "DCP" );
        this.payload = keyValues.get( "SERVICEREQUEST" );
        try {
            this.facadeURL = new URI( keyValues.get( "FACADEURL" ) );
        } catch ( URISyntaxException e ) {
            LOG.logError( e.getMessage(), e );
        }

        requestParameters = new ArrayList<RequestParameter>();

        String requestParams = keyValues.get( "REQUESTPARAMS" );
        List<String> params = StringTools.toList( requestParams, ",", false );

        String requestParamValues = keyValues.get( "REQUESTPARAMVALUES" );
        List<String> paramValues = StringTools.toList( requestParamValues, ",", false );

        for ( int i = 0; i < params.size(); ++i ) {
            this.requestParameters.add( new RequestParameter( params.get( i ), paramValues.get( i ) ) );
        }
        LOG.exiting();
    }

    /**
     * @return Returns the authenticationData.
     */
    public AuthenticationData getAuthenticationData() {
        return authenticationData;
    }

    /**
     * @return Returns the dcp.
     */
    public String getDcp() {
        return dcp;
    }

    /**
     * @return Returns the facadeURL.
     */
    public URI getFacadeURL() {
        return facadeURL;
    }

    /**
     * @return Returns the payload.
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @return Returns the requestParameters.
     */
    public ArrayList<RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    /**
     * @param id
     * @param documentElement
     * @return a new instance of this class
     * @throws OGCWebServiceException 
     */
    public static OGCWebServiceRequest create( String id, Element documentElement ) throws OGCWebServiceException {
        try {
            return new DoServiceDocument().parseDoService( id, documentElement );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        }
    }

    /**
     * @param id
     * @param kvp
     * @return a new instance of this class
     */
    public static OGCWebServiceRequest create( String id, Map<String, String> kvp ) {
        return new DoService( id, kvp );
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DoService.java,v $
 * Changes to this class. What the people have been up to: Revision 1.12  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/06/23 13:53:47  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/06/23 10:23:50  schmitz
 * Changes to this class. What the people have been up to: Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/06/20 15:31:04  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/19 15:34:04  bezema
 * Changes to this class. What the people have been up to: changed wass to handle things the right way
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/16 15:01:05  schmitz
 * Changes to this class. What the people have been up to: Fixed the WSS to work with all kinds of operation tests. It checks out
 * Changes to this class. What the people have been up to: with both XML and KVP requests.
 * Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.6 2006/06/13 15:16:18 bezema Changes to this
 * class. What the people have been up to: DoService Test seems to work Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.5
 * 2006/06/12 13:32:29 bezema Changes to this class. What the people have been up to: kvp is
 * implemented Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.4 2006/05/30 15:11:28 bezema Changes to this class. What
 * the people have been up to: Working on the postclient from apachecommons to place a request to
 * the services behind the wss proxy Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.3 2006/05/30 10:12:02 bezema Changes
 * to this class. What the people have been up to: Putting the cvs asci option to -kkv which will
 * update the $revision$ $author$ and $date$ variables in a cvs commit Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.2
 * 2006/05/29 16:24:59 bezema Changes to this class. What the people have been up to: Rearranging
 * the layout of the wss and creating the doservice classes. The WSService class is implemented as
 * well Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Refactored the security and authentication webservices into one package WASS (Web
 * Authentication -and- Security Services), also created a common package and a saml package which
 * could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
