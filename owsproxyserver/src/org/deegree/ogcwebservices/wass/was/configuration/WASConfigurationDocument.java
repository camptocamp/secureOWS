//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/configuration/WASConfigurationDocument.java,v 1.11 2006/07/12 16:59:32 poth Exp $
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

package org.deegree.ogcwebservices.wass.was.configuration;

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.IODocument;
import org.deegree.io.JDBCConnection;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wass.common.Messages;
import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilities;
import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilitiesDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Parser for the configuration documents of a WAS.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/07/12 16:59:32 $
 * 
 * @since 2.0
 */

public class WASConfigurationDocument extends WASCapabilitiesDocument {

    private static final long serialVersionUID = 4612405238432274887L;

    private static final ILogger LOG = LoggerFactory.getLogger( WASConfigurationDocument.class );
    
    private static final String DBPRE = CommonNamespaces.DGJDBC_PREFIX + ":";

    /**
     * @param serviceConfigurationUrl
     * @return the configuration data
     * @throws InvalidCapabilitiesException
     * @throws InvalidConfigurationException
     */
    public WASConfiguration parseConfiguration( URL serviceConfigurationUrl )
                            throws InvalidCapabilitiesException, InvalidConfigurationException {
        LOG.entering();

        WASConfiguration result = null;

        try {
            load( serviceConfigurationUrl );
            WASCapabilities cap = (WASCapabilities) parseCapabilities();

            /*
             * The required operation GetSAMLResponse is currently not supported for the was.
             */
//            boolean saml = false;
//            for ( Operation_1_0 operation : cap.getOperationsMetadata().getAllOperations() ) {
//                if ( "GetSAMLResponse".equals( operation.getName() ) ) {
//                    saml = true;
//                    break;
//                }
//            }
//
//            if ( !saml )
//                throw new InvalidCapabilitiesException(
//                                                        Messages.format(
//                                                                         "ogcwebservices.wass.ERROR_CAPABILITIES_MISSING_REQUIRED_OPERATION",
//                                                                         "GetSAMLResponse" ) );

            WASDeegreeParams deegreeParams = parseDeegreeParams();

            result = new WASConfiguration( cap, deegreeParams );

        } catch ( IOException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidConfigurationException(
                                                     Messages.format(
                                                                      "ogcwebservices.wass.ERROR_CONFIGURATION_NOT_READ",
                                                                      "WAS" ), e );
        } catch ( SAXException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidConfigurationException(
                                                     Messages.format(
                                                                      "ogcwebservices.wass.ERROR_CONFIGURATION_NOT_PARSED",
                                                                      "WAS" ), e );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidConfigurationException(
                                                     Messages.format(
                                                                      "ogcwebservices.wass.ERROR_CONFIGURATION_NOT_PARSED",
                                                                      "WAS" ), e );
        }

        LOG.exiting();
        return result;
    }

    /**
     * Creates a class representation of the <code>deegreeParams</code>- section.
     * 
     * @return the representation
     * @throws XMLParsingException
     */
    private WASDeegreeParams parseDeegreeParams()
                            throws XMLParsingException {
        LOG.entering();

        WASDeegreeParams deegreeParams = null;

        final String preWAS = CommonNamespaces.DEEGREEWAS_PREFIX + ":";
        Node root = this.getRootElement();
        Element element = (Element) XMLTools.getRequiredNode( root, preWAS + "deegreeParam",
                                                              nsContext );

        OnlineResource defaultOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
                                                                                                        element,
                                                                                                        preWAS
                                                                                                                                + "DefaultOnlineResource",
                                                                                                        nsContext ) );

        // 'CacheSize'-element (optional, default: 100)
        int cacheSize = XMLTools.getNodeAsInt( element, preWAS + "CacheSize", nsContext, 100 );

        // 'RequestTimeLimit'-element (optional, default: 15)
        int requestTimeLimit = XMLTools.getNodeAsInt( element, preWAS + "RequestTimeLimit",
                                                      nsContext, 15 ) * 1000;

        // 'Encoding'-element (optional, default: UTF-8)
        String characterSet = XMLTools.getStringValue( "Encoding", CommonNamespaces.DEEGREEWAS,
                                                       element, "UTF-8" );

        StringBuffer sb = new StringBuffer().append( "/" ).append( preWAS );
        sb.append( "OnlineResource" );
        
        // SecuredServiceAddress does not make sense for a WAS
//        StringBuffer sor = new StringBuffer( preWAS ).append( "SecuredServiceAddress" ).append( sb );
//        OnLineResource securedOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
//                                                                                                        element,
//                                                                                                        sor.toString(),
//                                                                                                        nsContext ) );

        StringBuffer aor = new StringBuffer( preWAS );
        aor.append( "AuthenticationServiceAddress" ).append( sb );
        OnlineResource authOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
                                                                                                     element,
                                                                                                     aor.toString(),
                                                                                                     nsContext ) );

        int sessionLifetime = XMLTools.getNodeAsInt( element, preWAS + "SessionLifetime",
                                                     nsContext, 1200 );
        sessionLifetime *= 1000;
        
        // parse database connection
        Element database = (Element)XMLTools.getNode( element, DBPRE + "JDBCConnection", nsContext );
        JDBCConnection dbConnection = null;
        if( database != null ) {
            IODocument io = new IODocument( database );
            dbConnection = io.parseJDBCConnection();
        }
       
        deegreeParams = new WASDeegreeParams( defaultOnlineResource, cacheSize, requestTimeLimit,
                                              characterSet, authOnlineResource, sessionLifetime, dbConnection );

        LOG.exiting();
        return deegreeParams;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WASConfigurationDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.11  2006/07/12 16:59:32  poth
 * Changes to this class. What the people have been up to: required adaptions according to renaming of OnLineResource to OnlineResource
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/07/07 15:03:03  schmitz
 * Changes to this class. What the people have been up to: Fixed a few warnings.
 * Changes to this class. What the people have been up to: Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/07/07 10:41:18  schmitz
 * Changes to this class. What the people have been up to: Fixed unnecessary entries in the was configuration.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7 2006/06/26 15:02:58 bezema
 * Changes to this class. What the people have been up to: Finished the wass Changes to this class.
 * What the people have been up to: Changes to this class. What the people have been up to: Revision
 * 1.6 2006/06/23 13:53:48 schmitz Changes to this class. What the people have been up to:
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.5 2006/06/23 10:23:50 schmitz Changes to this class. What the people
 * have been up to: Completed the WAS, GetSession and CloseSession work. Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.4
 * 2006/06/12 16:11:21 bezema Changes to this class. What the people have been up to: JUnit test
 * work with for a GetCapabilities request - example configurationfiles in resources added Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.3 2006/06/09 12:58:32 schmitz Changes to this class. What the people have been
 * up to: Set up some tests for WAS/WSS and the URN class. Changes to this class. What the people
 * have been up to: Commented out some of the deegree param stuff in order for the Changes to this
 * class. What the people have been up to: tests to run. Changes to this class. What the people have
 * been up to: Tests have hardcoded URLs in them, so they won't run just anywhere. Changes to this
 * class. What the people have been up to: Revision 1.2 2006/05/30 08:44:48 bezema Reararranging the
 * layout (again) to use features of OOP. The owscommonDocument is the real baseclass now.
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.1 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
 * 
 * Revision 1.1 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
