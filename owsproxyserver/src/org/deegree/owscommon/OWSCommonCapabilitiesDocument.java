// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon/OWSCommonCapabilitiesDocument.java,v 1.27 2006/10/11 13:53:31 mschneider Exp $
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.model.metadata.iso19115.TypeCode;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilitiesDocument;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.Protocol;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents a configuration document for an OGC-Webservice according to the
 * <code>OWS Common Implementation Specification 0.2</code>.
 * <p>
 * It consists of the following elements: <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Function</th>
 * </tr>
 * <tr>
 * <td>ServiceIdentification</td>
 * <td>corresponds to and expands the SV_ServiceIdentification class in ISO 19119</td>
 * </tr>
 * <tr>
 * <td>ServiceProvider</td>
 * <td>corresponds to and expands the SV_ServiceProvider class in ISO 19119 </td>
 * </tr>
 * <tr>
 * <td>OperationsMetadata</td>
 * <td>contains set of Operation elements that each corresponds to and expand the
 * SV_OperationsMetadata class in ISO 19119</td>
 * </tr>
 * <tr>
 * <td>Contents</td>
 * <td>whenever relevant, contains set of elements that each corresponds to the
 * MD_DataIdentification class in ISO 19119 and 19115</td>
 * </tr>
 * </table>
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version 2.0, $Revision: 1.27 $, $Date: 2006/10/11 13:53:31 $
 * 
 * @since 2.0
 * 
 */
public abstract class OWSCommonCapabilitiesDocument extends OGCCapabilitiesDocument {

    private static final ILogger LOG = LoggerFactory
        .getLogger( OWSCommonCapabilitiesDocument.class );

    public final static String ALL_NAME = "All";

    public final static String SERVICE_IDENTIFICATION_NAME = "ServiceIdentification";

    public final static String SERVICE_PROVIDER_NAME = "ServiceProvider";

    public final static String OPERATIONS_METADATA_NAME = "OperationsMetadata";

    public final static String CONTENTS_NAME = "Contents";

    protected static final URI OWSNS = CommonNamespaces.OWSNS;

    protected static final URI OGCNS = CommonNamespaces.OGCNS;

    /**
     * Returns the class representation for the <code>ServiceProvider</code> section of the
     * document.
     * 
     * @return class representation for the <code>ServiceProvider</code> section
     * @throws XMLParsingException
     */
    public ServiceProvider getServiceProvider() throws XMLParsingException {
        LOG.entering();
        Element element = XMLTools.getRequiredChildElement( "ServiceProvider", OWSNS,
            getRootElement() );

        // 'ProviderName' element (optional, default value: 'deegree')
        String providerName = XMLTools.getStringValue( "ProviderName", OWSNS, element, "deegree" );

        // 'ProviderSite' element (optional)
        Element providerSiteElement = XMLTools.getChildElement( "ProviderSite", OWSNS, element );
        SimpleLink providerSite = null;
        if ( providerSiteElement != null ) {
            providerSite = parseSimpleLink( providerSiteElement );
        }

        // 'ServiceContact' element (mandatory)
        Element serviceContactElement = XMLTools.getRequiredChildElement( "ServiceContact", OWSNS,
            element );

        // 'IndividualName' element (optional)
        String individualName = XMLTools.getStringValue( "IndividualName", OWSNS,
            serviceContactElement, null );

        // 'PositionName' element (optional)
        String positionName = XMLTools.getStringValue( "PositionName", OWSNS,
            serviceContactElement, null );

        // 'ContactInfo' element (optional)
        ContactInfo contactInfo = null;
        Element contactInfoElement = XMLTools.getChildElement( "ContactInfo", OWSNS,
            serviceContactElement );
        if ( contactInfoElement != null ) {
            contactInfo = getContactInfo( contactInfoElement );
        }
        TypeCode role = null;
        Element roleElement = (Element) XMLTools
            .getNode( serviceContactElement, "ows:Role", nsContext );
        if ( roleElement != null ) {
            role = getCodeType( roleElement );
        }
        ServiceProvider serviceProvider = new ServiceProvider( providerName, providerSite,
            individualName, positionName, contactInfo, role );
        LOG.exiting();
        return serviceProvider;
    }

    /**
     * Returns the class representation for the <code>ServiceIdentification</code> section of the
     * document.
     * 
     * @return class representation for the <code>ServiceIdentification</code> section
     * @throws XMLParsingException
     */
    public ServiceIdentification getServiceIdentification() throws XMLParsingException {
        LOG.entering();

        Element element = XMLTools.getRequiredChildElement( "ServiceIdentification", OWSNS,
            getRootElement() );

        // 'ServiceType' element (mandatory)
        Element serviceTypeElement = XMLTools.getRequiredChildElement( "ServiceType", OWSNS,
            element );
        Code serviceType = null;
        try {
            String codeSpace = XMLTools.getAttrValue( serviceTypeElement, OWSNS, "codeSpace" );
            URI uri = codeSpace != null ? new URI( codeSpace ) : null;
            serviceType = new Code( XMLTools.getStringValue( serviceTypeElement ), uri );
        } catch (URISyntaxException e) {
            throw new XMLParsingException( "Given value '"
                + XMLTools.getAttrValue( serviceTypeElement, OWSNS, "codeSpace" )
                + "' in attribute 'codeSpace' of element 'ServiceType' " + "(namespace: '" + OWSNS
                + "') is not a valid URI." );
        }

        // 'ServiceTypeVersion' elements (mandatory)
        String[] serviceTypeVersions = XMLTools.getRequiredNodeAsStrings( element,
            "ows:ServiceTypeVersion", nsContext, ",;" );

        // 'Title' element (mandatory)
        String title = XMLTools.getRequiredStringValue( "Title", OWSNS, element );

        // 'Abstract' element (optional)
        String serviceAbstract = XMLTools.getRequiredStringValue( "Abstract", OWSNS, element );

        // 'Keywords' elements (optional)
        List keywordsList = XMLTools.getNodes( element, "ows:Keywords", nsContext );
        Keywords[] keywords = getKeywords( keywordsList );

        // 'Fees' element (optional)
        String fees = XMLTools.getStringValue( "Fees", OWSNS, element, null );

        // 'AccessConstraints' elements (optional)
        String accessConstraints[] = XMLTools.getNodesAsStrings( element, "ows:AccessConstraints",
            nsContext );

        ServiceIdentification serviceIdentification = new ServiceIdentification( serviceType,
            serviceTypeVersions, title, serviceAbstract, keywords, fees, accessConstraints );
        LOG.exiting();
        return serviceIdentification;
    }

    /**
     * Creates a <code>Keywords</code> instance from the given element of type
     * <code>ows:KeywordsType</code>.
     * 
     * NOTE: This method is redefined here (it is already defined in <code>OGCDocument</code>),
     * because the spelling of the first letter ('K') changed in the OWS Common Implementation
     * Specification 0.2 from lowercase to uppercase.
     * 
     * @param element
     * @return created <code>Keywords</code>
     * @throws XMLParsingException
     */
    protected Keywords getKeywords( Element element ) throws XMLParsingException {
        LOG.entering();
        TypeCode codeType = null;
        Element codeTypeElement = (Element) XMLTools.getNode( element, "ows:Type", nsContext );
        if ( codeTypeElement != null ) {
            codeType = getCodeType( codeTypeElement );
        }
        Keywords keywords = new Keywords( XMLTools.getNodesAsStrings( element,
            "ows:Keyword/text()", nsContext ), null, codeType );
        LOG.exiting();
        return keywords;
    }

    /**
     * Creates an array of <code> Keywords </code> instances from the passed list of elements of
     * type <code> ows:KeywordsType </code>.
     * 
     * This may appear to be pretty superfluous (as one <code> ows:KeywordsType
     * </code> can hold
     * several elements of type <code> ows:Keyword
     * </code>.
     * 
     * @param nl
     *            may be null
     * @return created array of <code> Keywords </code>, null if <code>NodeList</code> constains
     *         zero elements
     * @throws XMLParsingException
     */
    protected Keywords[] getKeywords( List nl ) throws XMLParsingException {
        LOG.entering();
        Keywords[] kws = null;
        if ( nl.size() > 0 ) {
            kws = new Keywords[nl.size()];
            for (int i = 0; i < kws.length; i++) {
                kws[i] = getKeywords( (Element) nl.get( i ) );
            }
        }
        LOG.exiting();
        return kws;
    }

    /**
     * Creates a <code>DCPType</code> object from the passed <code>DCP</code> element.
     * <p>
     * NOTE: Currently the <code>OnlineResources</code> included in the <code>DCPType</code> are
     * just stored as simple <code>URLs</code> (not as <code>OnLineResource</code> instances)!
     * <p>
     * NOTE: In an <code>OGCStandardCapabilitiesDocument</code> the <code>XLinks</code> (the
     * <code>URLs</code>) are stored in separate elements (<code>OnlineResource</code>), in
     * an <code>OGCCommonCapabilitiesDocument</code> they are the
     * <code>Get<code>/<code>Post</code> elements themselves.
     * 
     * @param element
     * @param namespaceURI
     * @return created <code>DCPType</code>
     * @throws XMLParsingException
     * @see org.deegree.ogcwebservices.getcapabilities.OGCStandardCapabilities
     */
    protected DCPType getDCP( Element element ) throws XMLParsingException {
        LOG.entering();
        DCPType dcpType = null;
        try {
            Element elem = (Element) XMLTools.getRequiredNode( element, "ows:HTTP", nsContext );
            List nl = XMLTools.getNodes( elem, "ows:Get", nsContext );

            URL[] get = new URL[nl.size()];
            for (int i = 0; i < get.length; i++) {
                String s = XMLTools.getNodeAsString( (Node)nl.get( i ), "./@xlink:href", nsContext, null );
                if ( s == null ) {
                    s = XMLTools.getRequiredNodeAsString( (Node)nl.get( i ),
                        "./ows:OnlineResource/@xlink:href", nsContext );
                }
                get[i] = new URL( s );
            }
            nl = XMLTools.getNodes( elem, "ows:Post", nsContext );

            URL[] post = new URL[nl.size()];
            for (int i = 0; i < post.length; i++) {
                String s = XMLTools.getNodeAsString( (Node)nl.get( i ), "./@xlink:href", nsContext, null );
                if ( s == null ) {
                    s = XMLTools.getRequiredNodeAsString( (Node)nl.get( i ),
                        "./ows:OnlineResource/@xlink:href", nsContext );
                }
                post[i] = new URL( s );
            }
            Protocol protocol = new HTTP( get, post );
            dcpType = new DCPType( protocol );
        } catch (MalformedURLException e) {
            throw new XMLParsingException( "Couldn't parse DCPType onlineresource URL about: "
                + StringTools.stackTraceToString( e ) );
        }
        LOG.exiting();
        return dcpType;
    }

    /**
     * Creates an array of <code>DCPType</code> objects from the passed element list.
     * <p>
     * NOTE: Currently the <code>OnlineResources</code> included in the <code>DCPType</code> are
     * just stored as simple <code>URLs</code> (not as <code>OnLineResource</code> instances)!
     * 
     * @param el
     * @return
     * @throws XMLParsingException
     */
    protected DCPType[] getDCPs( List el ) throws XMLParsingException {
        LOG.entering();
        DCPType[] dcpTypes = new DCPType[el.size()];
        for (int i = 0; i < dcpTypes.length; i++) {
            dcpTypes[i] = getDCP( (Element) el.get( i ) );
        }
        LOG.exiting();
        return dcpTypes;
    }

    /**
     * Creates a class representation of an <code>ows:Operation</code>- element.
     * 
     * @param name
     * @param isMandatory
     * @param operations
     * @return
     * @throws InvalidConfigurationException
     */
    protected Operation getOperation( String name, boolean isMandatory, Map operations )
        throws XMLParsingException {
        LOG.entering();
        Operation operation = null;
        Element operationElement = (Element) operations.get( name );
        if ( operationElement == null ) {
            if ( isMandatory ) {
                throw new XMLParsingException( "Mandatory operation '"
                    + name + "' not defined in " + "'OperationsMetadata'-section." );
            }
        } else {
            // "ows:Parameter"-elements
            ElementList parameterElements = XMLTools.getChildElements( "Parameter", OWSNS,
                operationElement );
            OWSDomainType[] parameters = new OWSDomainType[parameterElements.getLength()];                     
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = getOWSDomainType( name, parameterElements.item( i ) );
            }
            DCPType[] dcps = 
                getDCPs( XMLTools.getRequiredNodes( operationElement, "ows:DCP", nsContext ) );
            operation = new Operation( name, dcps, parameters );

        }
        LOG.exiting();
        return operation;
    }

    /**
     * Creates a class representation of an element of type <code>ows:DomainType</code>.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    protected OWSDomainType getOWSDomainType( String operation, Element element ) 
                                                            throws XMLParsingException {
        LOG.entering();
        // "name"-attribute
        String name = XMLTools.getRequiredNodeAsString( element, "@name", nsContext );

        // "ows:Value"-elements
        String[] values = XMLTools.getNodesAsStrings( element, "ows:Value/text()", nsContext );
        if ( values.length < 1 ) {
            throw new XMLParsingException(
                "At least one 'ows:Value'-element must be defined in each " +
                "element of type 'ows:DomainType'." );
        }

        // TODO: "ows:Metadata"-elements
        OWSDomainType domainType = new OWSDomainType( name, values, null );
        LOG.exiting();
        return domainType;
    }

    /**
     * Creates a class representation of an element of type <code>ows:CodeType</code>.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    protected TypeCode getCodeType( Element element ) throws XMLParsingException {

        String code = XMLTools.getRequiredNodeAsString( element, "text()", nsContext );

        URI codeSpace = null;
        String codeSpaceString = XMLTools.getNodeAsString( element, "ows:Type/@codespace", nsContext,
            null );
        if ( codeSpaceString != null ) {
            try {
                codeSpace = new URI( codeSpaceString );
            } catch (URISyntaxException e) {
                throw new XMLParsingException( "'"
                    + codeSpaceString + "' does not denote a valid URI in: " + e.getMessage() );
            }
        }
        return new TypeCode( code, codeSpace );
    }

    /**
     * Creates a <code>ContactInfo</code> object from the given element of type
     * <code>ows:ContactInfoType</code>.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    private ContactInfo getContactInfo( Element element ) throws XMLParsingException {

        // 'Phone' element (optional)
        Phone phone = null;
        Element phoneElement = XMLTools.getChildElement( "Phone", OWSNS, element );
        if ( phoneElement != null ) {
            phone = parsePhone( phoneElement, OWSNS );
        }

        // 'Address' element (optional)
        Address address = null;
        Element addressElement = XMLTools.getChildElement( "Address", OWSNS, element );
        if ( addressElement != null ) {
            address = parseAddress( addressElement, OWSNS );
        }

        // 'OnlineResource' element (optional)
        OnlineResource onlineResource = null;
        Element onlineResourceElement = XMLTools.getChildElement( "OnlineResource", OWSNS, element );
        if ( onlineResourceElement != null ) {
            onlineResource = parseOnLineResource( onlineResourceElement );
        }

        String hoursOfService = XMLTools.getNodeAsString( element, "ows:HoursOfService/text()",
            nsContext, null );
        String contactInstructions = XMLTools.getNodeAsString( element,
            "ows:ContactInstructions/text()", nsContext, null );

        return new ContactInfo( address, contactInstructions, hoursOfService, onlineResource, phone );
    }

    /**
     * Creates an <code>Envelope</code> object from the given element of type
     * <code>ows:WGS84BoundingBoxType</code>.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    protected Envelope getWGS84BoundingBoxType( Element element ) throws XMLParsingException {
        double[] lowerCorner = XMLTools.getRequiredNodeAsDoubles( element,
            "ows:LowerCorner/text()", nsContext, " " );
        if ( lowerCorner.length != 2 ) {
            throw new XMLParsingException(
                "Element 'ows:LowerCorner' must contain exactly two double values." );
        }
        double[] upperCorner = XMLTools.getRequiredNodeAsDoubles( element,
            "ows:UpperCorner/text()", nsContext, " " );
        if ( upperCorner.length != 2 ) {
            throw new XMLParsingException(
                "Element 'ows:UpperCorner' must contain exactly two double values." );
        }
        return GeometryFactory.createEnvelope( lowerCorner[0], lowerCorner[1], upperCorner[0],
            upperCorner[1], null );
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: OWSCommonCapabilitiesDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.27  2006/10/11 13:53:31  mschneider
 * Changes to this class. What the people have been up to: Javadoc improvement.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.26  2006/07/12 17:00:02  poth
 * Changes to this class. What the people have been up to: required adaptions according to renaming of OnLineResource to OnlineResource
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.25  2006/04/06 20:25:31  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.24  2006/04/04 20:39:44  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.23  2006/03/30 21:20:28  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.22  2006/03/28 09:06:41  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.21  2006/01/04 08:59:11  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.20  2005/11/17 08:13:58  deshmukh
 * Changes to this class. What the people have been up to: Renamed nsNode to nsContext
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.19  2005/11/16 13:45:00  mschneider
 * Changes to this class. What the people have been up to: Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.18.2.3  2005/11/07 16:25:57  deshmukh
 * Changes to this class. What the people have been up to: NodeList to List
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.18.2.2  2005/11/07 15:38:04  mschneider
 * Changes to this class. What the people have been up to: Refactoring: use NamespaceContext instead of Node for namespace bindings.
 * Changes to this class. What the people have been up to:
 * Revision 1.7 2004/06/22 13:25:14 ap no message
 * 
 * Revision 1.6 2004/06/21 06:44:57 ap no message
 * 
 * Revision 1.5 2004/06/09 15:30:37 ap no message
 * 
 * Revision 1.4 2004/06/08 07:01:27 ap no message
 * 
 * Revision 1.3 2004/06/03 09:02:20 ap no message
 * 
 * Revision 1.2 2004/06/02 14:10:44 ap no message
 * 
 * Revision 1.1 2004/06/02 09:34:07 ap no message
 * 
 **************************************************************************************************/
