//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/XMLFactory.java,v 1.33 2006/08/20 12:54:22 poth Exp $
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
package org.deegree.ogcwebservices.csw;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.JDBCConnection;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueCapabilities;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueCapabilitiesDocument;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfiguration;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfigurationDocument;
import org.deegree.ogcwebservices.csw.configuration.CatalogueDeegreeParams;
import org.deegree.ogcwebservices.csw.configuration.CatalogueOutputSchemaParameter;
import org.deegree.ogcwebservices.csw.configuration.CatalogueOutputSchemaValue;
import org.deegree.ogcwebservices.csw.configuration.CatalogueTypeNameSchemaParameter;
import org.deegree.ogcwebservices.csw.configuration.CatalogueTypeNameSchemaValue;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.owscommon.OWSCommonCapabilitiesDocument;
import org.deegree.owscommon.OWSDomainType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0. $Revision: 1.33 $, $Date: 2006/08/20 12:54:22 $
 * 
 * @since 2.0
 */
public class XMLFactory extends org.deegree.owscommon.XMLFactory {

    protected static final URI DEEGREECSW = CommonNamespaces.DEEGREECSW;

    /**
     * Exports a <code>CatalogConfiguration</code> instance to an <code>XmlDocument</code>.
     * 
     * @param configuration
     * @return DOM representation of the <code>CatalogConfiguration</code>
     */
    public static CatalogueConfigurationDocument export( CatalogueConfiguration configuration ) {
        CatalogueConfigurationDocument configurationDocument = new CatalogueConfigurationDocument();
        try {
            configurationDocument.createEmptyDocument();
            Element root = configurationDocument.getRootElement();

            // 'deegreeParams'-section
            CatalogueDeegreeParams deegreeParams = configuration.getDeegreeParams();
            if ( deegreeParams != null ) {
                appendDeegreeParams( root, configuration.getDeegreeParams() );
            }

            // 'ServiceIdentification'-section
            ServiceIdentification serviceIdentification = configuration.getServiceIdentification();
            if ( serviceIdentification != null ) {
                appendServiceIdentification( root, serviceIdentification );
            }

            // 'ServiceProvider'-section
            ServiceProvider serviceProvider = configuration.getServiceProvider();
            if ( serviceProvider != null ) {
                appendServiceProvider( root, configuration.getServiceProvider() );
            }

            // 'OperationsMetadata'-section
            OperationsMetadata operationsMetadata = configuration.getOperationsMetadata();
            if ( operationsMetadata != null ) {
                appendOperationsMetadata( root, operationsMetadata );
            }

            // 'Contents'-section
            Contents contents = configuration.getContents();
            if ( contents != null ) {
                // appendContents(root, contents);
            }

            // 'Filter_Capabilities'-section
            FilterCapabilities filterCapabilities = configuration.getFilterCapabilities();
            if ( filterCapabilities != null ) {
                org.deegree.model.filterencoding.XMLFactory.appendFilterCapabilities100( root,
                                                                                         filterCapabilities );
            }
        } catch ( SAXException e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
        } catch ( XMLParsingException e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
        }
        return configurationDocument;
    }

    /**
     * Exports a <code>CatalogCapabilities</code> instance as an <code>XmlDocument</code>.
     * 
     * @param capabilities
     * @param sections
     *            names of sections to be exported, may contain 'All'
     * @return DOM representation of the <code>CatalogCapabilities</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static CatalogueCapabilitiesDocument export( CatalogueCapabilities capabilities,
                                                       String[] sections )
                            throws IOException {

        // no sections specified? => return all sections
        if ( sections == null || sections.length == 0 ) {
            sections = new String[] { OWSCommonCapabilitiesDocument.ALL_NAME };
        }

        // build HashSet with the names of the sections to be exported
        Set<String> sectionSet = new HashSet<String>();
        for ( int i = 0; i < sections.length; i++ ) {
            sectionSet.add( sections[i] );
        }

        CatalogueCapabilitiesDocument doc = new CatalogueCapabilitiesDocument();
        try {
            doc.createEmptyDocument();
            Element root = doc.getRootElement();

            // 'ServiceIdentification'-section
            if ( sectionSet.contains( CatalogueCapabilitiesDocument.ALL_NAME )
                 || sectionSet.contains( CatalogueCapabilitiesDocument.SERVICE_IDENTIFICATION_NAME ) ) {
                ServiceIdentification serviceIdentification = capabilities.getServiceIdentification();
                if ( serviceIdentification != null ) {
                    appendServiceIdentification( root, serviceIdentification );
                }
            }

            // 'ServiceProvider'-section
            if ( sectionSet.contains( CatalogueCapabilitiesDocument.ALL_NAME )
                 || sectionSet.contains( CatalogueCapabilitiesDocument.SERVICE_PROVIDER_NAME ) ) {
                ServiceProvider serviceProvider = capabilities.getServiceProvider();
                if ( serviceProvider != null ) {
                    appendServiceProvider( root, capabilities.getServiceProvider() );
                }
            }

            // 'OperationsMetadata'-section
            if ( sectionSet.contains( CatalogueCapabilitiesDocument.ALL_NAME )
                 || sectionSet.contains( CatalogueCapabilitiesDocument.OPERATIONS_METADATA_NAME ) ) {
                OperationsMetadata operationsMetadata = capabilities.getOperationsMetadata();
                if ( operationsMetadata != null ) {
                    appendOperationsMetadata( root, operationsMetadata, true );
                }
            }

            // 'Contents'-section
            if ( sectionSet.contains( CatalogueCapabilitiesDocument.ALL_NAME )
                 || sectionSet.contains( CatalogueCapabilitiesDocument.CONTENTS_NAME ) ) {
                Contents contents = capabilities.getContents();
                if ( contents != null ) {
                    // appendContents(root, contents);
                }
            }

            // 'Filter_Capabilities'-section
            if ( sectionSet.contains( CatalogueCapabilitiesDocument.ALL_NAME )
                 || sectionSet.contains( CatalogueCapabilitiesDocument.FILTER_CAPABILITIES_NAME ) ) {
                FilterCapabilities filterCapabilities = capabilities.getFilterCapabilities();
                if ( filterCapabilities != null ) {
                    org.deegree.model.filterencoding.XMLFactory.appendFilterCapabilities100( root,
                                                                                             filterCapabilities );
                }
            }
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        }
        return doc;
    }

    /**
     * Appends the DOM representation of the <code>deegreeParams</code>- section to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param deegreeParams
     * @throws XMLParsingException
     */
    private static void appendDeegreeParams( Element root, CatalogueDeegreeParams deegreeParams )
                            throws XMLParsingException {

        Element deegreeParamsNode = XMLTools.getRequiredChildElement( "deegreeParams", DEEGREECSW,
                                                                      root );

        // 'DefaultOnlineResource'-element
        appendOnlineResource( deegreeParamsNode, "deegree:DefaultOnlineResource",
                              deegreeParams.getDefaultOnlineResource(), DEEGREECSW );

        // 'CacheSize'-element
        XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:CacheSize",
                                "" + deegreeParams.getCacheSize() );

        // 'RequestTimeLimit'-element
        XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:RequestTimeLimit",
                                "" + deegreeParams.getRequestTimeLimit() );

        // 'Encoding'-element
        XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:Encoding",
                                deegreeParams.getCharacterSet() );

        // 'WFSResource'-element
        Element wfsResourceElement = XMLTools.appendElement( deegreeParamsNode, DEEGREECSW,
                                                             "deegree:WFSResource" );

        appendSimpleLinkAttributes( wfsResourceElement, deegreeParams.getWfsResource() );

        // 'CatalogAddresses'-element
        OnlineResource[] catalogAddresses = deegreeParams.getCatalogAddresses();
        if ( catalogAddresses.length > 0 ) {
            Element catalogAddressesNode = XMLTools.appendElement( deegreeParamsNode, DEEGREECSW,
                                                                   "deegree:CatalogAddresses" );
            for ( int i = 0; i < catalogAddresses.length; i++ ) {
                appendOnlineResource( catalogAddressesNode, "deegree:CatalogAddress",
                                      catalogAddresses[i], DEEGREECSW );
            }
        }

        // 'HarvestRepository'-element
        if ( deegreeParams.getHarvestRepository() != null ) {
            JDBCConnection connection = deegreeParams.getHarvestRepository();
            Element harvestRepositoryNode = XMLTools.appendElement( deegreeParamsNode, DEEGREECSW,
                                                                    "deegree:HarvestRepository" );
            // 'Connection'-element
            Element connectionNode = XMLTools.appendElement( harvestRepositoryNode, DEEGREECSW,
                                                             "deegree:Connection" );
            // 'Driver'-element
            XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:Driver",
                                    connection.getDriver() );
            // 'Logon'-element
            XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:Logon",
                                    connection.getURL() );
            // 'User'-element
            XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:User",
                                    connection.getUser() );
            // 'Password'-element
            XMLTools.appendElement( deegreeParamsNode, DEEGREECSW, "deegree:Password",
                                    connection.getPassword() );
        }
    }

    /**
     * Appends the DOM representation of the <code>OperationsMetadata</code>- section to the
     * passed <code>Element</code>.
     * 
     * @param root
     */
    protected static void appendOperationsMetadata( Element root,
                                                   OperationsMetadata operationsMetadata,
                                                   boolean capabilities ) {

        // 'ows:OperationsMetadata'-element
        Element operationsMetadataNode = XMLTools.appendElement( root, OWSNS,
                                                                 "ows:OperationsMetadata" );

        // append all Operations
        Operation[] operations = operationsMetadata.getOperations();
        for ( int i = 0; i < operations.length; i++ ) {
            Operation operation = operations[i];

            // 'ows:Operation'-element
            Element operationElement = XMLTools.appendElement( operationsMetadataNode, OWSNS,
                                                               "ows:Operation" );

            operationElement.setAttribute( "name", operation.getName() );

            // 'ows:DCP'-elements
            DCPType[] dcps = operation.getDCPs();
            for ( int j = 0; j < dcps.length; j++ ) {
                appendDCP( operationElement, dcps[j] );
            }

            // 'ows:Parameter'-elements
            OWSDomainType[] parameters = operation.getParameters();
            for ( int j = 0; j < parameters.length; j++ ) {
                if ( parameters[j] instanceof CatalogueOutputSchemaParameter ) {
                    appendParameter( operationElement,
                                     (CatalogueOutputSchemaParameter) parameters[j],
                                     "ows:Parameter", capabilities );
                } else if ( parameters[j] instanceof CatalogueTypeNameSchemaParameter ) {
                    appendParameter( operationElement,
                                     (CatalogueTypeNameSchemaParameter) parameters[j],
                                     "ows:Parameter", capabilities );
                } else {
                    appendParameter( operationElement, parameters[j], "ows:Parameter" );
                }
            }

            // 'ows:Metadata'-elements
            Object[] metadata = operation.getMetadata();
            if ( metadata != null ) {
                for ( int j = 0; j < metadata.length; j++ ) {
                    appendMetadata( operationElement, metadata[j] );
                }
            }
        }

        // append general parameters
        OWSDomainType[] parameters = operationsMetadata.getParameter();
        for ( int i = 0; i < parameters.length; i++ ) {
            appendParameter( operationsMetadataNode, parameters[i], "ows:Parameter" );
        }

        // append constraints
        OWSDomainType[] constraints = operationsMetadata.getConstraints();
        for ( int i = 0; i < constraints.length; i++ ) {
            appendParameter( operationsMetadataNode, constraints[i], "ows:Constraint" );
        }
    }

    /**
     * Appends the DOM representation of a <code>OWSDomainType</code> instance to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param parameter
     */
    protected static void appendParameter( Element root, CatalogueOutputSchemaParameter parameter,
                                          String elementName, boolean capabilities ) {

        // 'ows:Parameter'-element
        Element parameterNode = XMLTools.appendElement( root, OWSNS, elementName );
        parameterNode.setAttribute( "name", parameter.getName() );

        // 'ows:Value'-elements
        CatalogueOutputSchemaValue[] values = parameter.getSpecializedValues();
        for ( int i = 0; i < values.length; i++ ) {
            Element elem = XMLTools.appendElement( parameterNode, OWSNS, "ows:Value",
                                                   values[i].getValue() );
            if ( !capabilities ) {
                elem.setAttribute( "deegree:input", values[i].getInXsl() );
                elem.setAttribute( "deegree:ouput", values[i].getOutXsl() );
            }
        }
    }

    /**
     * Appends the DOM representation of a <code>OWSDomainType</code> instance to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param parameter
     */
    protected static void appendParameter( Element root,
                                          CatalogueTypeNameSchemaParameter parameter,
                                          String elementName, boolean capabilities ) {

        // 'ows:Parameter'-element
        Element parameterNode = XMLTools.appendElement( root, OWSNS, elementName );
        parameterNode.setAttribute( "name", parameter.getName() );

        // 'ows:Value'-elements
        CatalogueTypeNameSchemaValue[] values = parameter.getSpecializedValues();
        for ( int i = 0; i < values.length; i++ ) {
            Element elem = XMLTools.appendElement( parameterNode, OWSNS, "ows:Value",
                                                   values[i].getValue() );
            if ( !capabilities ) {
                elem.setAttribute( "deegree:schema", values[i].getSchema() );
            }
        }
    }

}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: XMLFactory.java,v $
 Revision 1.33  2006/08/20 12:54:22  poth
 *** empty log message ***

 Revision 1.32  2006/07/21 14:07:43  mschneider
 Added constants for section names.

 Revision 1.31  2006/07/12 16:59:32  poth
 required adaptions according to renaming of OnLineResource to OnlineResource

 Revision 1.30  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */
