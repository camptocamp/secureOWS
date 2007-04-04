//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/configuration/WSSConfigurationDocument.java,v 1.14 2006/08/24 06:42:17 poth Exp $
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wass.wss.configuration;

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
import org.deegree.ogcwebservices.wass.common.Operation_1_0;
import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilities;
import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilitiesDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class is called from the WSServiceFactory to read a configuration xml file. This file
 * consains all the capabilities this Web Security Service is able to. The standard calling
 * procedure is new WSSConfigurationDocument().getConfiguration( url_to_file ). This method returns
 * the "bean" in form of a WSSConfiguration class, which can be queried for it's values.
 * 
 * @see WSSConfiguration
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.14 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */

public class WSSConfigurationDocument extends WSSCapabilitiesDocument {
    /**
     * 
     */
    private static final long serialVersionUID = 4612405238432274887L;

    /**
     * The logger enhances the quality and simplicity of Debugging within the deegree2 framework
     */
    private static final ILogger LOG = LoggerFactory.getLogger( WSSConfigurationDocument.class );

    private static final String DBPRE = CommonNamespaces.DGJDBC_PREFIX + ":";

    /**
     * Loads the configuration file located at the given urls location.
     * 
     * @param serviceConfigurationUrl
     *            the url to the configuration file
     * @return a WSSConfiguration which is a "bean" representation of the configuration xml document
     * @throws InvalidConfigurationException
     *             if an error occrur either with opening or parsing the xml configuration file.
     * @throws InvalidCapabilitiesException 
     */
    public WSSConfiguration parseConfiguration( URL serviceConfigurationUrl )
                            throws InvalidConfigurationException, InvalidCapabilitiesException {
        LOG.entering();

        WSSConfiguration result = null;
        try {
            load( serviceConfigurationUrl );
            WSSCapabilities capabilities = (WSSCapabilities) parseCapabilities();
            boolean doService = false;
            for( Operation_1_0 operation : capabilities.getOperationsMetadata().getAllOperations() ){
                if( "DoService".equals(operation.getName()) ){
                    doService = true;
                    break;
                }
            }
            
            if( !doService ) 
                throw new InvalidCapabilitiesException(
                                                       Messages.format(
                                                                       "ogcwebservices.wass.ERROR_CAPABILITIES_MISSING_REQUIRED_OPERATION",
                                                       "DoService" ) );
            
            WSSDeegreeParams deegreeParams = parseDeegreeParams( );
            result = new WSSConfiguration( capabilities, deegreeParams );
        } catch ( IOException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidConfigurationException(
                                                     Messages.format(
                                                                      "ogcwebservices.wass.ERROR_CONFIGURATION_NOT_READ",
                                                                      "WSS" ), e );
        } catch ( SAXException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidConfigurationException(
                                                     Messages.format(
                                                                      "ogcwebservices.wass.ERROR_CONFIGURATION_NOT_PARSED",
                                                                      "WSS" ), e );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidConfigurationException(
                                                     Messages.format(
                                                                      "ogcwebservices.wass.ERROR_CONFIGURATION_NOT_PARSED",
                                                                      "WSS" ), e );
        }

        LOG.exiting();
        return result;
    }

    /**
     * Creates a class representation of the <code>deegreeParams</code>- section which are wss
     * specific.
     * 
     * @return the deegree parameter data
     * @throws XMLParsingException 
     */
    private WSSDeegreeParams parseDeegreeParams()
                            throws XMLParsingException {
        LOG.entering();
        WSSDeegreeParams deegreeParams = null;

        final String preWSS = CommonNamespaces.DEEGREEWSS_PREFIX + ":";
        Node root = this.getRootElement();

        Element element = (Element) XMLTools.getRequiredNode( root, preWSS + "deegreeParam",
                                                              nsContext );

        OnlineResource defaultOnlineResource = null;
        defaultOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
                                                                                         element,
                                                                                         preWSS
                                                                                                                 + "DefaultOnlineResource",
                                                                                         nsContext ) );

        // 'deegreecsw:CacheSize'-element (optional, default: 100)
        int cacheSize = 0;
        cacheSize = XMLTools.getNodeAsInt( element, preWSS + "CacheSize", nsContext, 100 );

        // 'deegreecsw:RequestTimeLimit'-element (optional, default: 15)
        int requestTimeLimit = XMLTools.getNodeAsInt( element, preWSS + "RequestTimeLimit",
                                                          nsContext, 15 );
        requestTimeLimit *= 1000;
        

        // 'deegreecsw:Encoding'-element (optional, default: UTF-8)
        String characterSet = null;
        characterSet = XMLTools.getNodeAsString( element, preWSS + "Encoding", nsContext, "UTF-8" );

        StringBuffer sb = new StringBuffer().append( "/" ).append( preWSS );
        sb.append( "OnlineResource" );
        StringBuffer sor = new StringBuffer( preWSS ).append( "SecuredServiceAddress" ).append( sb );
        OnlineResource securedOnlineResource = null;
        securedOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
                                                                                         element,
                                                                                         sor.toString(),
                                                                                         nsContext ) );

        StringBuffer aor = new StringBuffer( preWSS );
        aor.append( "AuthenticationServiceAddress" ).append( sb );
        OnlineResource authOnlineResource = null;
        authOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
                                                                                      element,
                                                                                      aor.toString(),
                                                                                      nsContext ) );
        
        int sessionLifetime = XMLTools.getNodeAsInt( element, preWSS + "SessionLifetime",
                                                      nsContext, 1200 );
        sessionLifetime *= 1000;
        
        // parse database connection
        Element database = (Element)XMLTools.getNode( element, DBPRE + "JDBCConnection", nsContext );
        JDBCConnection dbConnection = null;
        if( database != null ) {
            IODocument io = new IODocument( database );
            dbConnection = io.parseJDBCConnection();
        }
       
        deegreeParams = new WSSDeegreeParams( defaultOnlineResource, cacheSize, requestTimeLimit,
                                              characterSet, securedOnlineResource,
                                              authOnlineResource, sessionLifetime, dbConnection );

        LOG.exiting();
        return deegreeParams;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WSSConfigurationDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.14  2006/08/24 06:42:17  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.13  2006/07/12 16:59:32  poth
 * Changes to this class. What the people have been up to: required adaptions according to renaming of OnLineResource to OnlineResource
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12  2006/07/07 15:03:03  schmitz
 * Changes to this class. What the people have been up to: Fixed a few warnings.
 * Changes to this class. What the people have been up to: Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/06/26 15:02:58  bezema
 * Changes to this class. What the people have been up to: Finished the wass
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/06/23 13:53:47  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/20 15:31:05  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7 2006/06/19 15:34:04 bezema
 * Changes to this class. What the people have been up to: changed wass to handle things the right
 * way Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.6 2006/06/12 16:11:21 bezema Changes to this class. What the
 * people have been up to: JUnit test work with for a GetCapabilities request - example
 * configurationfiles in resources added Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5 2006/06/09 12:58:32 schmitz
 * Changes to this class. What the people have been up to: Set up some tests for WAS/WSS and the URN
 * class. Changes to this class. What the people have been up to: Commented out some of the deegree
 * param stuff in order for the Changes to this class. What the people have been up to: tests to
 * run. Changes to this class. What the people have been up to: Tests have hardcoded URLs in them,
 * so they won't run just anywhere. Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.4 2006/06/06 15:28:05 bezema Changes
 * to this class. What the people have been up to: Added a "null" prefix check in xmltools so that
 * it, added a characterset to the deegreeparams and the WSS::DoService class is almost done Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.3 2006/05/30 10:12:02 bezema Changes to this class. What the people have been
 * up to: Putting the cvs asci option to -kkv which will update the $revision$ $author$ and $date$
 * variables in a cvs commit Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.2 2006/05/30 08:44:48 bezema Changes to this
 * class. What the people have been up to: Reararranging the layout (again) to use features of OOP.
 * The owscommonDocument is the real baseclass now. Changes to this class. What the people have been
 * up to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Changes to this class. What the people have been up to: Refactored the security and
 * authentication webservices into one package WASS (Web Authentication -and- Security Services),
 * also created a common package and a saml package which could be updated to work in the future.
 * Changes to this class. What the people have been up to: Revision 1.1 2006/05/23 15:22:02 bezema
 * Added configuration files to the wss and wss is able to parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
