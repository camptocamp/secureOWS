//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/AuthenticationDocument.java,v 1.5 2006/06/19 12:47:26 schmitz Exp $
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parser class that can parse all elements within the namespace.
 * 
 * Namespace: http://www.gdi-nrw.de/authentication
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/06/19 12:47:26 $
 * 
 * @since 2.0
 */
public class AuthenticationDocument extends XMLFragment {

    private static final long serialVersionUID = -6467874541905139362L;

    private final static String PRE = CommonNamespaces.GDINRW_AUTH_PREFIX + ":";

    private final static ILogger LOG = LoggerFactory.getLogger( AuthenticationDocument.class );

    /**
     * Parses a SupportedAuthenticationMethodList element.
     * 
     * @param listRoot
     *            the list element
     * @return an ArrayList with the parsed methods
     * @throws MalformedURLException
     * @throws XMLParsingException
     */
    public ArrayList<SupportedAuthenticationMethod> parseSupportedAuthenticationMethodList(
                                                                                           Element listRoot )
                            throws MalformedURLException, XMLParsingException {
        LOG.entering();
        List methods = XMLTools.getRequiredNodes( listRoot, PRE + "SupportedAuthenticationMethod",
                                                  nsContext );
        ArrayList<SupportedAuthenticationMethod> values = new ArrayList<SupportedAuthenticationMethod>();
        for ( Object element : methods ) {
            values.add( parseSupportedAuthenticationMethod( (Element) element ) );
        }
        LOG.exiting();
        return values;
    }

    /**
     * Parses a SupportedAuthenticationMethod element.
     * 
     * @param elem
     *            the element
     * @return the parsed data
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    public SupportedAuthenticationMethod parseSupportedAuthenticationMethod( Node elem )
                            throws XMLParsingException, MalformedURLException {
        LOG.entering();
        Node method = XMLTools.getNode( elem, PRE + "AuthenticationMethod", nsContext );
        URN methodURN = parseAuthenticationMethod( method );
        Node metadata = XMLTools.getNode( elem, PRE + "WASAuthenticationMethodMD", nsContext );
        if ( metadata != null ) {
            WASAuthenticationMethodMD wasamd = parseWASAuthenticationMethodMD( metadata );
            return new SupportedAuthenticationMethod( methodURN, wasamd );
        }
        metadata = XMLTools.getNode( elem, PRE + "UnknownMethodMetadata", nsContext );
        String ummd = null;
        if ( metadata != null )
            ummd = parseUnknownMethodMetadata( metadata );

        SupportedAuthenticationMethod result = new SupportedAuthenticationMethod( methodURN, ummd );
        LOG.exiting();
        return result;
    }

    /**
     * Parses an AuthenticationMethod.
     * 
     * @param elem
     *            the AuthenticationMethod element
     * @return an URN with the method
     * @throws XMLParsingException
     */
    public URN parseAuthenticationMethod( Node elem )
                            throws XMLParsingException {
        return new URN( XMLTools.getNodeAsString( elem, "@id", nsContext, null ) );
    }

    /**
     * Parses an UnknownMethodMetadata element.
     * 
     * @param elem
     *            the element
     * @return a String with the data
     * @throws XMLParsingException
     */
    public String parseUnknownMethodMetadata( Node elem )
                            throws XMLParsingException {
        return XMLTools.getNodeAsString( elem, ".", nsContext, null );
    }

    /**
     * Parses a WASAuthenticationMethodMD element.
     * 
     * @param elem
     *            the element
     * @return an object with the parsed data
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    public WASAuthenticationMethodMD parseWASAuthenticationMethodMD( Node elem )
                            throws XMLParsingException, MalformedURLException {
        LOG.entering();
        String mdName = XMLTools.getRequiredNodeAsString( elem, PRE + "Name", nsContext );
        URL mdURL = XMLTools.getRequiredNodeAsURI( elem, PRE + "URL", nsContext ).toURL();
        ArrayList<URN> mdAuthenticationMethods = new ArrayList<URN>();
        String[] urns = XMLTools.getNodesAsStrings( elem, PRE + "AuthenticationMethod", nsContext );
        for ( int i = 0; i < urns.length; ++i )
            mdAuthenticationMethods.add( new URN( urns[i] ) );

        WASAuthenticationMethodMD result = new WASAuthenticationMethodMD( mdName, mdURL,
                                                                          mdAuthenticationMethods );
        LOG.exiting();
        return result;
    }

    /**
     * Parses an AuthenticationData element
     * 
     * @param elem
     *            the element
     * @return an object with the parsed data
     * @throws XMLParsingException
     */
    public AuthenticationData parseAuthenticationData( Node elem )
                            throws XMLParsingException {
        LOG.entering();
        Node method = XMLTools.getRequiredNode( elem, PRE + "AuthenticationMethod", nsContext );
        URN authenticationMethod = parseAuthenticationMethod( method );
        Node cred = XMLTools.getRequiredNode( elem, PRE + "Credentials", nsContext );
        String credentials = parseCredentials( cred );

        AuthenticationData result = new AuthenticationData( authenticationMethod, credentials );
        LOG.exiting();
        return result;
    }

    /**
     * Parses a Credentials element.
     * 
     * @param elem
     *            the element
     * @return a String containing the data
     * @throws XMLParsingException
     */
    public String parseCredentials( Node elem )
                            throws XMLParsingException {
        return XMLTools.getRequiredNodeAsString( elem, ".", nsContext );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: AuthenticationDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/19 12:47:26  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4 2006/06/12 12:16:24 bezema
 * Changes to this class. What the people have been up to: Little rearanging of the GetSession
 * classes, DoService should be ready updating some errors Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.3 2006/05/30
 * 11:44:51 schmitz Changes to this class. What the people have been up to: Updated the
 * documentation, fixed some warnings. Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2 2006/05/30 08:44:48 bezema
 * Changes to this class. What the people have been up to: Reararranging the layout (again) to use
 * features of OOP. The owscommonDocument is the real baseclass now. Changes to this class. What the
 * people have been up to: Changes to this class. What the people have been up to: Revision 1.1
 * 2006/05/29 12:00:58 bezema Changes to this class. What the people have been up to: Refactored the
 * security and authentication webservices into one package WASS (Web Authentication -and- Security
 * Services), also created a common package and a saml package which could be updated to work in the
 * future. Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.5 2006/05/23 15:20:50 bezema Changes to this class. What the
 * people have been up to: Cleaned up the warnings and added some minor methods Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.4 2006/05/22 15:47:05 bezema Changes to this class. What the people have been up to:
 * Cleaning up redundant Documents Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.3 2006/05/19 15:35:35 schmitz Changes
 * to this class. What the people have been up to: Updated the documentation, added the
 * GetCapabilities operation and implemented a rough WAService outline. Fixed some warnings. Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.2 2006/05/16 14:45:07 bezema Changes to this class. What the people have been
 * up to: getsession and close session can now be parses Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/15
 * 15:22:19 bezema Changes to this class. What the people have been up to: The authentication.xsd
 * elements will now be parsed by AuthenticationDocument. Data containing classes have also been
 * created. Changes to this class. What the people have been up to:
 * 
 **************************************************************************************************/
