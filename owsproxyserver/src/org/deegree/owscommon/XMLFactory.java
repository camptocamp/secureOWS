// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon/XMLFactory.java,v 1.18 2006/11/22 14:06:26 schmitz Exp $
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
package org.deegree.owscommon;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.Linkage;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.model.metadata.iso19115.TypeCode;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.ExceptionDocument;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.w3c.dom.Element;

/**
 * Factory to create XML representations of components that are defined in the
 * <code>OWS Common Implementation Capabilities Specification 0.3</code>.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 1.0. $Revision: 1.18 $, $Date: 2006/11/22 14:06:26 $
 * 
 * @since 2.0
 */
public class XMLFactory extends org.deegree.ogcbase.XMLFactory {

    protected static final URI OWSNS = CommonNamespaces.OWSNS;
    
    private static final String POGC = CommonNamespaces.OGC_PREFIX + ":";

    protected static final URI DEEGREECSWNS = CommonNamespaces.DEEGREECSW;

    private static final String POWS = CommonNamespaces.OWS_PREFIX + ":";

    /**
     * Exports an <tt>ExceptionReport</tt> to an XML Document as defined in the
     * <code>OGC common implementation specification 0.2.0</code>.
     * 
     * @param exr
     * @return a new ServiceException document
     * @throws Exception
     */
    public static XMLFragment export( ExceptionReport exr )
                            throws Exception {

        ExceptionDocument eDoc = new ExceptionDocument();
        eDoc.createEmptyDocument();
        Element node = eDoc.getRootElement();

        for ( int i = 0; i < exr.getExceptions().length; i++ ) {
            appendException( node, exr.getExceptions()[i], false );
        }

        return eDoc;
    }

    /**
     * @param exr
     * @return a new ServiceException document
     */
    public static XMLFragment exportNS( ExceptionReport exr ) {

        ExceptionDocument eDoc = new ExceptionDocument();
        eDoc.createEmptyDocumentNS();
        Element node = eDoc.getRootElement();

        for ( int i = 0; i < exr.getExceptions().length; i++ ) {
            appendException( node, exr.getExceptions()[i], true );
        }

        return eDoc;

    }

    /**
     * appends a xml representation of an <tt>OGCWebServiceException</tt> to the passed
     * <tt>Element</tt>
     * 
     * @param node
     * @param ex
     */
    protected static void appendException( Element node, OGCWebServiceException ex,
                                          boolean namespace ) {

        if ( namespace ) {
            node = XMLTools.appendElement( node, OGCNS, POGC + "ServiceException", ex.getMessage() );
        } else {
            node = XMLTools.appendElement( node, null, "ServiceException", ex.getMessage() );
        }

        if ( ex.getCode() != null ) {
            node.setAttribute( "code", ex.getCode().value );
        }
        String locator = ex.getLocator();
        try {
            if ( locator != null ) {
                locator = URLEncoder.encode( locator, CharsetUtils.getSystemCharset() );
            } else {
                locator = "-";
            }
        } catch ( UnsupportedEncodingException e ) {
        }
        node.setAttribute( "locator", locator );
    }

    /**
     * Appends the DOM representation of the <code>ServiceIdentification</code>- section to the
     * passed <code>Element</code>.
     * 
     * @param root
     * @param serviceIdentification
     * @throws XMLParsingException
     */
    protected static void appendServiceIdentification( Element root,
                                                      ServiceIdentification serviceIdentification ) {

        // 'ServiceIdentification'-element
        Element serviceIdentificationNode = XMLTools.appendElement( root, OWSNS,
                                                                    "ows:ServiceIdentification" );

        // 'ServiceType'-element
        XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:ServiceType",
                                serviceIdentification.getServiceType().getCode() );

        // 'ServiceTypeVersion'-elements
        String[] versions = serviceIdentification.getServiceTypeVersions();
        for ( int i = 0; i < versions.length; i++ ) {
            XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:ServiceTypeVersion",
                                    versions[i] );
        }

        // 'Title'-element
        XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:Title",
                                serviceIdentification.getTitle() );

        // 'Abstract'-element
        if ( serviceIdentification.getAbstract() != null ) {
            XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:Abstract",
                                    serviceIdentification.getAbstract() );
        }

        // 'Keywords'-element
        appendOWSKeywords( serviceIdentificationNode, serviceIdentification.getKeywords() );

        // 'Fees'-element
        XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:Fees",
                                serviceIdentification.getFees() );

        // 'AccessConstraints'-element
        String[] constraints = serviceIdentification.getAccessConstraints();
        if ( constraints != null ) {
            for ( int i = 0; i < constraints.length; i++ ) {
                XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:AccessConstraints",
                                        constraints[i] );
            }
        }
    }

    /**
     * Appends a <code>ows:Keywords</code> -element for each <code>Keywords</code> object of the
     * passed array to the passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param keywords
     */
    protected static void appendOWSKeywords( Element xmlNode, Keywords[] keywords ) {
        if ( keywords != null ) {
            for ( int i = 0; i < keywords.length; i++ ) {
                Element node = XMLTools.appendElement( xmlNode, OWSNS, "ows:Keywords" );
                appendOWSKeywords( node, keywords[i] );
            }
        }
    }

    /**
     * Appends a <code>ows:Keywords</code> -element to the passed <code>Element</code> and fills
     * it with the available keywords.
     * 
     * @param xmlNode
     * @param keywords
     */
    protected static void appendOWSKeywords( Element xmlNode, Keywords keywords ) {
        if ( keywords != null ) {
            String[] kw = keywords.getKeywords();
            for ( int i = 0; i < kw.length; i++ ) {
                XMLTools.appendElement( xmlNode, OWSNS, "ows:Keyword", kw[i] );
            }
            TypeCode typeCode = keywords.getTypeCode();
            if ( typeCode != null ) {
                Element node = XMLTools.appendElement( xmlNode, OWSNS, "ows:Type",
                                                       typeCode.getCode() );
                if ( typeCode.getCodeSpace() != null ) {
                    node.setAttribute( "codeSpace", typeCode.getCodeSpace().toString() );
                }
            }
        }
    }

    /**
     * Appends the DOM representation of the <code>ServiceProvider</code>- section to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param serviceProvider
     * @throws XMLParsingException
     */
    protected static void appendServiceProvider( Element root, ServiceProvider serviceProvider ) {

        // 'ServiceProvider'-element
        Element serviceProviderNode = XMLTools.appendElement( root, OWSNS, "ows:ServiceProvider" );

        // 'ProviderName'-element
        XMLTools.appendElement( serviceProviderNode, OWSNS, "ows:ProviderName",
                                serviceProvider.getProviderName() );

        // 'ProviderSite'-element
        if ( serviceProvider.getProviderSite() != null ) {
            Element providerSiteNode = XMLTools.appendElement( serviceProviderNode, OWSNS,
                                                               "ows:ProviderSite" );
            appendSimpleLinkAttributes( providerSiteNode, serviceProvider.getProviderSite() );
        }

        // 'ServiceContact'-element
        Element serviceContactNode = XMLTools.appendElement( serviceProviderNode, OWSNS,
                                                             "ows:ServiceContact" );

        // 'IndividualName'-element
        XMLTools.appendElement( serviceContactNode, OWSNS, "ows:IndividualName",
                                serviceProvider.getIndividualName() );

        // 'PositionName'-element
        if ( serviceProvider.getPositionName() != null ) {
            XMLTools.appendElement( serviceContactNode, OWSNS, "ows:PositionName",
                                    serviceProvider.getPositionName() );
        }

        // 'ContactInfo'-element
        ContactInfo contactInfo = serviceProvider.getContactInfo();
        if ( contactInfo != null ) {
            Element contactInfoNode = XMLTools.appendElement( serviceContactNode, OWSNS,
                                                              "ows:ContactInfo" );
            Phone phone = contactInfo.getPhone();
            if ( phone != null ) {
                appendPhone( contactInfoNode, phone );
            }
            Address address = contactInfo.getAddress();
            if ( address != null ) {
                appendAddress( contactInfoNode, address );
            }
            OnlineResource onlineResource = contactInfo.getOnLineResource();
            if ( onlineResource != null ) {
                appendOnlineResource( contactInfoNode, "ows:OnlineResource", onlineResource, OWSNS );
            }
            String hoursOfService = contactInfo.getHoursOfService();
            if ( hoursOfService != null ) {
                XMLTools.appendElement( contactInfoNode, OWSNS, "ows:HoursOfService",
                                        hoursOfService );
            }
            String contactInstructions = contactInfo.getContactInstructions();
            if ( contactInstructions != null ) {
                XMLTools.appendElement( contactInfoNode, OWSNS, "ows:ContactInstructions",
                                        contactInstructions );
            }
        }
        TypeCode role = serviceProvider.getRole();
        if ( role != null ) {
            Element roleElement = XMLTools.appendElement( serviceContactNode, OWSNS, "ows:Role",
                                                          role.getCode() );
            if ( role.getCodeSpace() != null ) {
                roleElement.setAttribute( "codeSpace", role.getCodeSpace().toString() );
            }
        }
    }

    /**
     * Appends the DOM representation of the <code>Phone</code> -section to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param phone
     */
    protected static void appendPhone( Element root, Phone phone ) {

        // 'Phone'-element
        Element phoneNode = XMLTools.appendElement( root, OWSNS, "ows:Phone" );

        // 'Voice'-elements
        String[] voiceNumbers = phone.getVoice();
        for ( int i = 0; i < voiceNumbers.length; i++ ) {
            XMLTools.appendElement( phoneNode, OWSNS, "ows:Voice", voiceNumbers[i] );
        }

        // 'Facsimile'-elements
        String[] facsimileNumbers = phone.getFacsimile();
        for ( int i = 0; i < facsimileNumbers.length; i++ ) {
            XMLTools.appendElement( phoneNode, OWSNS, "ows:Facsimile", facsimileNumbers[i] );
        }
    }

    /**
     * Appends the DOM representation of the <code>Address</code> -section to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param address
     */
    protected static void appendAddress( Element root, Address address ) {

        // 'Address'-element
        Element addressNode = XMLTools.appendElement( root, OWSNS, "ows:Address" );

        // 'DeliveryPoint'-elements
        String[] deliveryPoints = address.getDeliveryPoint();
        for ( int i = 0; i < deliveryPoints.length; i++ ) {
            XMLTools.appendElement( addressNode, OWSNS, "ows:DeliveryPoint", deliveryPoints[i] );
        }

        // 'City'-element
        if ( address.getCity() != null ) {
            XMLTools.appendElement( addressNode, OWSNS, "ows:City", address.getCity() );
        }

        // 'AdministrativeArea'-element
        if ( address.getAdministrativeArea() != null ) {
            XMLTools.appendElement( addressNode, OWSNS, "ows:AdministrativeArea",
                                    address.getAdministrativeArea() );
        }

        // 'PostalCode'-element
        if ( address.getPostalCode() != null ) {
            XMLTools.appendElement( addressNode, OWSNS, "ows:PostalCode", address.getPostalCode() );
        }

        // 'Country'-element
        if ( address.getCountry() != null ) {
            XMLTools.appendElement( addressNode, OWSNS, "ows:Country", address.getCountry() );
        }

        // 'ElectronicMailAddress'-elements
        String[] electronicMailAddresses = address.getElectronicMailAddress();
        if ( address.getElectronicMailAddress() != null ) {
            for ( int i = 0; i < electronicMailAddresses.length; i++ ) {
                XMLTools.appendElement( addressNode, OWSNS, "ows:ElectronicMailAddress",
                                        electronicMailAddresses[i] );
            }
        }
    }

    /**
     * Appends the DOM representation of the <code>OperationsMetadata</code>- section to the
     * passed <code>Element</code>.
     * 
     * @param root
     */
    protected static void appendOperationsMetadata( Element root,
                                                   OperationsMetadata operationsMetadata ) {

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
                appendParameter( operationElement, parameters[j], "ows:Parameter" );
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
     * Appends the DOM representation of a <code>DCPType</code> instance to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param dcp
     */
    protected static void appendDCP( Element root, DCPType dcp ) {

        // 'ows:DCP'-element
        Element dcpNode = XMLTools.appendElement( root, OWSNS, "ows:DCP" );

        // currently, the only supported DCP is HTTP!
        if ( dcp.getProtocol() instanceof HTTP ) {
            HTTP http = (HTTP) dcp.getProtocol();

            // 'ows:HTTP'-element
            Element httpNode = XMLTools.appendElement( dcpNode, OWSNS, "ows:HTTP" );

            // 'ows:Get'-elements
            URL[] getURLs = http.getGetOnlineResources();
            for ( int i = 0; i < getURLs.length; i++ ) {
                appendOnlineResource( httpNode, "ows:Get",
                                      new OnlineResource( new Linkage( getURLs[i] ) ), OWSNS );
            }

            // 'ows:Post'-elements
            URL[] postURLs = http.getPostOnlineResources();
            for ( int i = 0; i < postURLs.length; i++ ) {
                appendOnlineResource( httpNode, "ows:Post",
                                      new OnlineResource( new Linkage( postURLs[i] ) ), OWSNS );
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
    protected static void appendParameter( Element root, OWSDomainType parameter, String elementName ) {

        // 'ows:Parameter'-element
        Element parameterNode = XMLTools.appendElement( root, OWSNS, elementName );
        parameterNode.setAttribute( "name", parameter.getName() );

        // 'ows:Value'-elements
        String[] values = parameter.getValues();
        for ( int i = 0; i < values.length; i++ ) {
            XMLTools.appendElement( parameterNode, OWSNS, "ows:Value", values[i] );
        }
    }

    /**
     * Appends the DOM representation of a <code>Metadata</code> instance to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param metadata
     */
    protected static void appendMetadata( Element root, Object metadata ) {

        // TODO

    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: XMLFactory.java,v $
 * Changes to this class. What the people have been up to: Revision 1.18  2006/11/22 14:06:26  schmitz
 * Changes to this class. What the people have been up to: Fixed some minor details in the WMS example configuration.
 * Changes to this class. What the people have been up to: Added CRS:84 to proj4.
 * Changes to this class. What the people have been up to: Fixed exception handling for WMS.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.17  2006/10/27 09:52:23  schmitz
 * Changes to this class. What the people have been up to: Brought the WMS up to date regarding 1.1.1 and 1.3.0 conformance.
 * Changes to this class. What the people have been up to: Fixed a bug while creating the default GetLegendGraphics URLs.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.16  2006/10/17 20:31:20  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.15  2006/09/08 08:42:02  schmitz
 * Changes to this class. What the people have been up to: Updated the WMS to be 1.1.1 conformant once again.
 * Changes to this class. What the people have been up to: Cleaned up the WMS code.
 * Changes to this class. What the people have been up to: Added cite WMS test data.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.14  2006/07/12 17:00:02  poth
 * Changes to this class. What the people have been up to: required adaptions according to renaming of OnLineResource to OnlineResource
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.13  2006/07/07 15:35:01  mschneider
 * Changes to this class. What the people have been up to: IndividualName is always exported (even when null), because it is mandatory.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12  2006/04/06 20:25:31  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/04/05 07:21:11  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Revision 1.4
 * 2004/06/16 09:46:02 ap no message
 **************************************************************************************************/
