//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/configuration/WFSConfigurationDocument.java,v 1.19 2006/10/17 20:31:20 poth Exp $
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
package org.deegree.ogcwebservices.wfs.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.Protocol;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.capabilities.WFSOperationsMetadata;
import org.deegree.owscommon.OWSDomainType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents an XML configuration document for a deegree WFS instance, i.e. it consists of all
 * sections common to an OGC WFS 1.1 capabilities document plus some deegree specific elements.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.19 $, $Date: 2006/10/17 20:31:20 $
 */
public class WFSConfigurationDocument extends WFSCapabilitiesDocument {

    private static final long serialVersionUID = -6415476866015999971L;

    protected static final ILogger LOG = LoggerFactory.getLogger( WFSConfigurationDocument.class );

    /**
     * Creates an object representation of the document.
     * 
     * @return class representation of the configuration document
     * @throws InvalidConfigurationException
     */
    public WFSConfiguration getConfiguration()
                            throws InvalidConfigurationException {

        WFSConfiguration config = null;

        try {
            WFSDeegreeParams deegreeParams = getDeegreeParams();

            // get default Urls (used when DCP element is ommitted in Operation-elements)
            OnlineResource defaultOnlineResource = deegreeParams.getDefaultOnlineResource();
            String defaultUrl = defaultOnlineResource.getLinkage().getHref().toString();
            if ( defaultUrl.endsWith( "?" ) ) {
                defaultUrl = defaultUrl.substring( 0, defaultUrl.length() - 1 );
            }
            URL defaultUrlGet = new URL( defaultUrl + '?' );
            URL defaultUrlPost = new URL( defaultUrl );

            OperationsMetadata opMetadata = getOperationsMetadata( defaultUrlGet, defaultUrlPost );
            config = new WFSConfiguration( parseVersion(), parseUpdateSequence(),
                                           getServiceIdentification(), getServiceProvider(),
                                           opMetadata, getFeatureTypeList(),
                                           getServesGMLObjectTypeList(),
                                           getSupportsGMLObjectTypeList(), null,
                                           getFilterCapabilities(), deegreeParams );
        } catch ( Exception e ) {
            throw new InvalidConfigurationException( e.getMessage() + "\n"
                                                     + StringTools.stackTraceToString( e ) );
        }
        return config;
    }

    /**
     * Creates an object representation of the <code>deegreeParams</code>- section.
     * 
     * @return class representation of the <code>deegreeParams</code>- section
     * @throws InvalidConfigurationException
     */
    public WFSDeegreeParams getDeegreeParams()
                            throws InvalidConfigurationException {

        WFSDeegreeParams deegreeParams = null;

        try {
            Element element = (Element) XMLTools.getRequiredNode( getRootElement(),
                                                                  "deegreewfs:deegreeParams",
                                                                  nsContext );
            OnlineResource defaultOnlineResource = parseOnLineResource( (Element) XMLTools.getRequiredNode(
                                                                                                            element,
                                                                                                            "deegreewfs:DefaultOnlineResource",
                                                                                                            nsContext ) );
            int cacheSize = XMLTools.getNodeAsInt( element, "deegreewfs:CacheSize/text()",
                                                   nsContext, 100 );
            int requestTimeLimit = XMLTools.getNodeAsInt( element,
                                                          "deegreewfs:RequestTimeLimit/text()",
                                                          nsContext, 2 );
            String characterSet = XMLTools.getNodeAsString( element, "deegreewfs:Encoding/text()",
                                                            nsContext,
                                                            CharsetUtils.getSystemCharset() );
            String[] dataDirectories = XMLTools.getNodesAsStrings(
                                                                   element,
                                                                   "deegreewfs:DataDirectoryList/deegreewfs:DataDirectory/text()",
                                                                   nsContext );
            if ( dataDirectories.length == 0 ) {
                LOG.logInfo( "No data directory specified. Using configuration document directory." );
                dataDirectories = new String[] { "." };
            }
            for ( int i = 0; i < dataDirectories.length; i++ ) {
                try {
                    dataDirectories[i] = resolve( dataDirectories[i] ).getFile();
                } catch ( MalformedURLException e ) {
                    throw new InvalidConfigurationException(
                                                             "DataDirectory '"
                                                                                     + dataDirectories[i]
                                                                                     + "' cannot be resolved as a directory: "
                                                                                     + e.getMessage() );
                }
            }
            deegreeParams = new WFSDeegreeParams( defaultOnlineResource, cacheSize,
                                                  requestTimeLimit, characterSet, dataDirectories );

        } catch ( XMLParsingException e ) {
            throw new InvalidConfigurationException( "Error parsing the deegreeParams "
                                                     + "section of the WFS configuration: \n"
                                                     + e.getMessage()
                                                     + StringTools.stackTraceToString( e ) );
        }
        return deegreeParams;
    }

    /**
     * Creates an object representation of the <code>ows:OperationsMetadata</code> section.
     * 
     * @param defaultUrlGet 
     * @param defaultUrlPost
     * @return object representation of the <code>ows:OperationsMetadata</code> section
     * @throws XMLParsingException
     */
    public OperationsMetadata getOperationsMetadata( URL defaultUrlGet, URL defaultUrlPost )
                            throws XMLParsingException {

        List operationElementList = XMLTools.getNodes( getRootElement(),
                                                       "ows:OperationsMetadata/ows:Operation",
                                                       nsContext );

        // build HashMap of 'ows:Operation'-elements for easier access
        Map<String, Element> operations = new HashMap<String, Element>();
        for ( int i = 0; i < operationElementList.size(); i++ ) {
            operations.put( XMLTools.getRequiredNodeAsString( (Node) operationElementList.get( i ),
                                                              "@name", nsContext ),
                            (Element) operationElementList.get( i ) );
        }

        Operation getCapabilities = getOperation( OperationsMetadata.GET_CAPABILITIES_NAME, true,
                                                  operations, defaultUrlGet, defaultUrlPost );
        Operation describeFeatureType = getOperation(
                                                      WFSOperationsMetadata.DESCRIBE_FEATURETYPE_NAME,
                                                      true, operations, defaultUrlGet,
                                                      defaultUrlPost );
        Operation getFeature = getOperation( WFSOperationsMetadata.GET_FEATURE_NAME, false,
                                             operations, defaultUrlGet, defaultUrlPost );
        Operation getFeatureWithLock = getOperation(
                                                     WFSOperationsMetadata.GET_FEATURE_WITH_LOCK_NAME,
                                                     false, operations, defaultUrlGet,
                                                     defaultUrlPost );
        Operation getGMLObject = getOperation( WFSOperationsMetadata.GET_GML_OBJECT_NAME, false,
                                               operations, defaultUrlGet, defaultUrlPost );
        Operation lockFeature = getOperation( WFSOperationsMetadata.LOCK_FEATURE_NAME, false,
                                              operations, defaultUrlGet, defaultUrlPost );
        Operation transaction = getOperation( WFSOperationsMetadata.TRANSACTION_NAME, false,
                                              operations, defaultUrlGet, defaultUrlPost );

        List parameterElementList = XMLTools.getNodes( getRootElement(),
                                                       "ows:OperationsMetadata/ows:Parameter",
                                                       nsContext );
        OWSDomainType[] parameters = new OWSDomainType[parameterElementList.size()];
        for ( int i = 0; i < parameters.length; i++ ) {
            parameters[i] = getOWSDomainType( null, (Element) parameterElementList.get( i ) );
        }

        List constraintElementList = XMLTools.getNodes( getRootElement(),
                                                        "ows:OperationsMetadata/ows:Constraint",
                                                        nsContext );
        OWSDomainType[] constraints = new OWSDomainType[constraintElementList.size()];
        for ( int i = 0; i < constraints.length; i++ ) {
            constraints[i] = getOWSDomainType( null, (Element) constraintElementList.get( i ) );
        }
        WFSOperationsMetadata metadata = new WFSOperationsMetadata( getCapabilities,
                                                                    describeFeatureType,
                                                                    getFeature, getFeatureWithLock,
                                                                    getGMLObject, lockFeature,
                                                                    transaction, parameters,
                                                                    constraints );
        LOG.exiting();
        return metadata;
    }

    /**
     * Creates an object representation of an <code>ows:Operation</code>- element.
     * 
     * @param name
     * @param isMandatory
     * @param operations
     * @param defaultUrlGet
     * @param defaultUrlPost
     * @return object representation of <code>ows:Operation</code>- element
     * @throws XMLParsingException
     */
    protected Operation getOperation( String name, boolean isMandatory,
                                      Map<String, Element> operations, URL defaultUrlGet,
                                      URL defaultUrlPost )
                            throws XMLParsingException {

        Operation operation = null;
        Element operationElement = operations.get( name );
        if ( operationElement == null ) {
            if ( isMandatory ) {
                throw new XMLParsingException( "Mandatory operation '" + name + "' not defined in "
                                               + "'OperationsMetadata'-section." );
            }
        } else {
            // "ows:Parameter"-elements
            List parameterElements = XMLTools.getNodes( operationElement, "ows:Parameter",
                                                        nsContext );
            OWSDomainType[] parameters = new OWSDomainType[parameterElements.size()];
            for ( int i = 0; i < parameters.length; i++ ) {
                parameters[i] = getOWSDomainType( name, (Element) parameterElements.get( i ) );
            }

            DCPType[] dcps = null;
            List nl = XMLTools.getNodes( operationElement, "ows:DCP", nsContext );
            if ( nl.size() > 0 ) {
                dcps = getDCPs( nl );
            } else {
                // add default URLs
                dcps = new DCPType[1];
                Protocol protocol = new HTTP( new URL[] { defaultUrlGet },
                                              new URL[] { defaultUrlPost } );
                dcps[0] = new DCPType( protocol );
            }
            operation = new Operation( name, dcps, parameters );
        }
        return operation;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSConfigurationDocument.java,v $
 Revision 1.19  2006/10/17 20:31:20  poth
 *** empty log message ***

 Revision 1.18  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.17  2006/10/11 18:01:21  mschneider
 Fixed automatically inserted DCPs.

 Revision 1.16  2006/10/11 13:54:18  mschneider
 Added automatic use of DefaultOnlineResource in case a DCP of an Operation definition is omitted.

 Revision 1.15  2006/09/21 12:22:11  mschneider
 Adapted to changes in WFSCapabilitiesDocument.

 Revision 1.14  2006/09/20 12:59:30  mschneider
 Javadoc fixes.

 Revision 1.13  2006/08/24 06:42:17  poth
 File header corrected

 Revision 1.12  2006/07/12 16:59:32  poth
 required adaptions according to renaming of OnLineResource to OnlineResource

 Revision 1.11  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */