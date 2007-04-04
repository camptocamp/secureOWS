//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/DoServiceDocument.java,v 1.10 2006/08/24 06:42:16 poth Exp $
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
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wass.common.AuthenticationData;
import org.deegree.ogcwebservices.wass.common.AuthenticationDocument;
import org.deegree.ogcwebservices.wass.common.Messages;
import org.w3c.dom.Element;

/**
 * A parser for a xml DoService Request.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.10 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class DoServiceDocument extends XMLFragment {

    private static final long serialVersionUID = -8223141905965433189L;

    private static final String PRE = CommonNamespaces.GDINRWWSS_PREFIX + ":";

    /**
     * The logger enhances the quality and simplicity of Debugging within the deegree2 framework
     */
    private static final ILogger LOG = LoggerFactory.getLogger( DoServiceDocument.class );

    /**
     * @param id
     *            the request id
     * @param rootElement
     * @return the encapsulated data
     * @throws XMLParsingException
     */
    public DoService parseDoService( String id, Element rootElement )
                            throws XMLParsingException {
        LOG.entering();

        String service = XMLTools.getRequiredNodeAsString( rootElement, "@service", nsContext );
        if ( !service.equals( "WSS" ) )
            throw new XMLParsingException( Messages.getString( "ogcwebservices.wass.ERROR_NOT_WSS" ) );

        String version = XMLTools.getRequiredNodeAsString( rootElement, "@version", nsContext );
        if ( !version.equals( "1.0" ) )
            throw new XMLParsingException(
                                           Messages.getString( "ogcwebservices.wass.ERROR_NO_VERSION_ATTRIBUTE" ) );

        String currentPre = CommonNamespaces.GDINRW_AUTH_PREFIX + ":";
        Element authData = (Element) XMLTools.getRequiredNode( rootElement, currentPre
                                                                            + "AuthenticationData",
                                                               nsContext );

        AuthenticationData authenticationData = new AuthenticationDocument().parseAuthenticationData( authData );

        Element serviceRequest = (Element) XMLTools.getRequiredNode( rootElement,
                                                                     PRE + "ServiceRequest",
                                                                     nsContext );

        String DCP = XMLTools.getRequiredNodeAsString( serviceRequest, "@DCP", nsContext );
        if ( !( DCP.equals( "HTTP_GET" ) || DCP.equals( "HTTP_POST" ) ) )
            throw new XMLParsingException(
                                           Messages.format(
                                                            "ogcwebservices.wass.ERROR_NOT_POST_OR_GET",
                                                            "WSS" ) );

        ArrayList<RequestParameter> requestParameters = parseRequestParameters( serviceRequest );

        String payload = XMLTools.getRequiredNodeAsString( serviceRequest, PRE + "Payload",
                                                           nsContext );

        URI facadeURL = XMLTools.getRequiredNodeAsURI( rootElement, PRE + "FacadeURL", nsContext );

        DoService theService = new DoService( id, service, version, authenticationData, DCP,
                                              requestParameters, payload, facadeURL );
        LOG.exiting();
        return theService;
    }

    private ArrayList<RequestParameter> parseRequestParameters( Element serviceRequest )
                            throws XMLParsingException {
        LOG.entering();
        List requestParameter = XMLTools.getNodes( serviceRequest, PRE + "RequestParameter",
                                                   nsContext );
        if ( requestParameter == null ) {
            LOG.exiting();
            return null;
        }
        ArrayList<RequestParameter> serviceRequests = new ArrayList<RequestParameter>();
        for ( Object element : requestParameter ) {
            String id = XMLTools.getRequiredNodeAsString( (Element) element, "@id", nsContext );
            String nodeValue = ( (Element) element ).getNodeValue();
            RequestParameter param = new RequestParameter( nodeValue, id );
            serviceRequests.add( param );
        }
        LOG.exiting();
        return serviceRequests;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DoServiceDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.10  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/06/23 13:53:48  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.8 2006/06/20 15:31:04 bezema Changes
 * to this class. What the people have been up to: It looks like the completion of wss. was needs
 * further checking in a tomcat environment. The Strings must still be externalized. Logging is
 * done, so is the formatting. Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.7 2006/06/19 15:34:04 bezema Changes to
 * this class. What the people have been up to: changed wass to handle things the right way Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.6 2006/06/19 10:24:11 bezema Changes to this class. What the people have been
 * up to: LIttle error message correction Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5 2006/06/16 15:01:05 schmitz
 * Changes to this class. What the people have been up to: Fixed the WSS to work with all kinds of
 * operation tests. It checks out Changes to this class. What the people have been up to: with both
 * XML and KVP requests. Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.4 2006/06/13 15:16:18 bezema Changes to this
 * class. What the people have been up to: DoService Test seems to work Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.3
 * 2006/05/30 12:46:33 bezema Changes to this class. What the people have been up to: DoService is
 * now handled Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.2 2006/05/30 10:12:02 bezema Changes to this class. What
 * the people have been up to: Putting the cvs asci option to -kkv which will update the $revision$
 * $author$ and $date$ variables in a cvs commit Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Changes to this class. What the people have been up to: Refactored the security and
 * authentication webservices into one package WASS (Web Authentication -and- Security Services),
 * also created a common package and a saml package which could be updated to work in the future.
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.2 2006/05/23 15:22:02 bezema Changes to this class. What the people
 * have been up to: Added configuration files to the wss and wss is able to parse a DoService
 * Request in xml Changes to this class. What the people have been up to: Revision 1.1 2006/05/22
 * 15:48:16 bezema Starting the parsing of the xml request in wss
 * 
 * 
 **************************************************************************************************/
