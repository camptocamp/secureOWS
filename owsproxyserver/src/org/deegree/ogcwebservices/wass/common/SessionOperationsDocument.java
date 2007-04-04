//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/SessionOperationsDocument.java,v 1.6 2006/06/23 13:53:47 schmitz Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
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
 Meckenheimer Allee 176
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

package org.deegree.ogcwebservices.wass.common;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parser class that can parse all elements within the namespace.
 * 
 * Namespace: http://www.gdi-nrw.de/session
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/06/23 13:53:47 $
 * 
 * @since 2.0
 */

public class SessionOperationsDocument extends OGCDocument {

    private static final long serialVersionUID = 7190634032990406558L;

    private static final ILogger LOG = LoggerFactory.getLogger( SessionOperationsDocument.class );

    private static final String PSESSION = CommonNamespaces.WSSSESSION_PREFIX + ":";

    /**
     * Parses a GetSession element.
     * 
     * @param id
     *            the request id
     * 
     * @param request
     *            the element
     * @return an object with the parsed data
     * @throws XMLParsingException
     */
    public GetSession parseGetSession( String id, Element request )
                            throws XMLParsingException {
        LOG.entering();

        String serviceName = parseService( request );
        String version = parseVersion( request );

        String pre = CommonNamespaces.GDINRW_AUTH_PREFIX + ":";
        Node data = XMLTools.getRequiredNode( request, pre + "AuthenticationData", nsContext );
        AuthenticationData authenticationData = new AuthenticationDocument().parseAuthenticationData( data );
        GetSession gs = new GetSession( id, serviceName, version, authenticationData );
        LOG.exiting();
        return gs;
    }

    /**
     * Parses a CloseSession element.
     * 
     * @param id
     *            the request id
     * 
     * @param request
     *            the element
     * @return an object with the data
     * @throws XMLParsingException
     */
    public CloseSession parseCloseSession( String id, Element request )
                            throws XMLParsingException {
        LOG.entering();
        String serviceName = parseService( request );
        String version = parseVersion( request );
        String sessionID = XMLTools.getRequiredNodeAsString( request, PSESSION + "SessionID",
                                                             nsContext );
        CloseSession cs = new CloseSession( id, serviceName, version, sessionID );
        LOG.exiting();
        return cs;
    }

    /**
     * Parses the service name.
     * 
     * @param basicRequest
     *            the request element
     * @return a String containing the service name
     * @throws XMLParsingException
     *             if the service name was not WAS or WSS
     */
    private String parseService( Element basicRequest )
                            throws XMLParsingException {
        LOG.entering();
        String serviceName = XMLTools.getRequiredNodeAsString( basicRequest, "@service", nsContext );
        if ( !( serviceName.equals( "WAS" ) || serviceName.equals( "WSS" ) ) ) {
            throw new XMLParsingException(
                                           Messages.getString( "ogcwebservices.wass.ERROR_NO_SERVICE_ATTRIBUTE" ) );
        }
        LOG.exiting();
        return serviceName;
    }

    /**
     * Parses the version attribute of a request element.
     * 
     * @param basicRequest
     *            the element
     * @return a string containing the version number (currently "1.0")
     * @throws XMLParsingException
     */
    private String parseVersion( Element basicRequest )
                            throws XMLParsingException {
        LOG.entering();
        String version = XMLTools.getRequiredNodeAsString( basicRequest, "@version", nsContext );
        if ( !version.equals( "1.0" ) ) {
            throw new XMLParsingException(
                                           Messages.getString( "ogcwebservices.wass.ERROR_NO_VERSION_ATTRIBUTE" ) );
        }
        LOG.exiting();
        return version;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: SessionOperationsDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/23 13:53:47  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5 2006/06/19 15:34:04 bezema
 * Changes to this class. What the people have been up to: changed wass to handle things the right
 * way Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.4 2006/06/16 15:01:05 schmitz Changes to this class. What the
 * people have been up to: Fixed the WSS to work with all kinds of operation tests. It checks out
 * Changes to this class. What the people have been up to: with both XML and KVP requests. Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.3 2006/05/30 11:44:51 schmitz Changes to this class. What the people have been
 * up to: Updated the documentation, fixed some warnings. Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.2 2006/05/30
 * 07:47:27 schmitz Changes to this class. What the people have been up to: Started on the
 * XMLFactory. Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.1 2006/05/29 12:00:58 bezema Changes to this class. What
 * the people have been up to: Refactored the security and authentication webservices into one
 * package WASS (Web Authentication -and- Security Services), also created a common package and a
 * saml package which could be updated to work in the future. Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/19
 * 15:35:35 schmitz Changes to this class. What the people have been up to: Updated the
 * documentation, added the GetCapabilities operation and implemented a rough WAService outline.
 * Fixed some warnings. Changes to this class. What the people have been up to:
 * 
 **************************************************************************************************/
