// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/getcapabilities/WCSCapabilitiesDocument.java,v 1.18 2006/08/07 13:41:11 poth Exp $
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
package org.deegree.ogcwebservices.wcs.getcapabilities;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.deegree.datatypes.CodeList;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.MetadataLink;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.Capability;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.OGCStandardCapabilitiesDocument;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.Service;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @version $Revision: 1.18 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.18 $, $Date: 2006/08/07 13:41:11 $
 * 
 * @since 2.0
 */
public class WCSCapabilitiesDocument extends OGCStandardCapabilitiesDocument {

	public static final String XML_TEMPLATE = "WCSCapabilitiesTemplate.xml";

	protected static URI WCSNS = CommonNamespaces.WCSNS;

	protected static URI DGRNS = CommonNamespaces.DEEGREEWCS;

	public void createEmptyDocument() throws IOException, SAXException {
        URL url = WCSCapabilitiesDocument.class.getResource(XML_TEMPLATE);
        if (url == null) {
            throw new IOException( "The resource '" + XML_TEMPLATE
                    + " could not be found." );
        }
        load(url);
	}
	
	/**
	 * @see org.deegree.ogcwebservices.getcapabilities.OGCCapabilitiesDocument#parseCapabilities()
	 */
	public OGCCapabilities parseCapabilities() throws InvalidCapabilitiesException {
		String version = parseVersion();
		String updateSeq = parseUpdateSequence();
		Service service = parseServiceSection();
		Capability capabilitiy = getCapabilitySection(WCSNS);
        ContentMetadata contentMetadata = parseContentMetadataSection();
		return new WCSCapabilities( version, updateSeq, service, capabilitiy, contentMetadata );
	}
	/**
	 * returns the service section of the WCS configuration/capabilities
	 * 
	 * @return created <tt>CapabilitiesService</tt>
	 * @throws InvalidCapabilitiesException
	 */
	public Service parseServiceSection() throws InvalidCapabilitiesException {
		try {
			Element element = XMLTools.getRequiredChildElement( "Service", WCSNS, getRootElement());
			Element elem = XMLTools.getChildElement( "metadataLink", WCSNS, element );
			MetadataLink mLink = parseMetadataLink(elem);
			String desc = XMLTools.getStringValue( "description", WCSNS, element, null);
			String name = XMLTools.getRequiredStringValue( "name", WCSNS, element );
			String label = XMLTools.getRequiredStringValue( "label", WCSNS, element );
			ElementList el = XMLTools.getChildElements( "keywords", WCSNS, element );
			Keywords[] keywords = parseKeywords(el, WCSNS);
			elem = XMLTools.getChildElement( "responsibleParty", WCSNS, element );
			CitedResponsibleParty crp = parseResponsibleParty(elem);
			elem = XMLTools.getChildElement( "fees", WCSNS, element );
			CodeList fees = parseCodeList(elem);
			el = XMLTools.getChildElements( "accessConstraints", WCSNS, element );
			CodeList[] accessConstraints = parseCodeListArray(el);

			String version = element.getAttribute( "version" );
			if (version == null || version.equals( "" )) {
				version = this.parseVersion();
			}
			String updateSequence = element.getAttribute( "updateSequence" );
			if (updateSequence == null || updateSequence.equals( "" )) {
				updateSequence = this.getRootElement().getAttribute( "updateSequence" );
			}

			Service service = new Service(desc, name, mLink, label, keywords,
					crp, fees, accessConstraints, version, updateSequence);
			return service;
		} catch (XMLParsingException e) {
			String s = e.getMessage();
			throw new InvalidCapabilitiesException(
					"Error while parsing the Service Section "
							+ "of the WCS capabilities\n" + s
							+ StringTools.stackTraceToString(e));
		} catch (DOMException e) {
			String s = e.getMessage();
			throw new InvalidCapabilitiesException(
					"Error handling the DOM object of the "
							+ "Service Section of the WCS capabilities\n" + s
							+ StringTools.stackTraceToString(e));
		} catch (OGCException e) {
			String s = e.getMessage();
			throw new InvalidCapabilitiesException(
					"Error initializing the Service object from "
							+ "the Service Section of the WCS capabilities\n"
							+ s + StringTools.stackTraceToString(e));
		}
	}

	/**
	 * returns the contentMetadata section of the WCS configuration/capabilities
	 * 
	 * @return @throws
	 *         InvalidCapabilitiesException
	 */
	public ContentMetadata parseContentMetadataSection()
			throws InvalidCapabilitiesException {
		try {
			Element element = 
                XMLTools.getRequiredChildElement( "ContentMetadata", WCSNS, getRootElement());
			ElementList el = 
                XMLTools.getChildElements( "CoverageOfferingBrief", WCSNS, element );
			CoverageOfferingBrief[] cob = parseCoverageOfferingBrief(el);

			String version = element.getAttribute( "version" );
			if (version == null || version.equals( "" )) {
				version = this.parseVersion();
			}
			String updateSequence = element.getAttribute( "updateSequence" );
			if (updateSequence == null || updateSequence.equals( "" )) {
				updateSequence = this.getRootElement().getAttribute( "updateSequence" );
			}

			return new ContentMetadata(version, updateSequence, cob);
		} catch (XMLParsingException e) {
			String s = e.getMessage();
			throw new InvalidCapabilitiesException(
					"Error while parsing the ContentMetadata "
							+ "Section of the WCS capabilities\n" + s
							+ StringTools.stackTraceToString(e));
		} catch (OGCException e) {
			String s = e.getMessage();
			throw new InvalidCapabilitiesException(
					"Error while parsing the ContentMetadata "
							+ "Section of the WCS capabilities\n" + s
							+ StringTools.stackTraceToString(e));
		}
	}

	/**
	 * creates a <tt>CitedResponsibleParty</tt> object from the passed element
	 * 
	 * @param element
	 * @return @throws
	 *         XMLParsingException
	 */
	private CitedResponsibleParty parseResponsibleParty(Element element )
			throws XMLParsingException {
		if (element == null) return null;
		String indName = XMLTools.getStringValue( "individualName", WCSNS, element, null);
		String orgName = XMLTools.getStringValue( "organisationName", WCSNS, element, null);
		String posName = XMLTools.getStringValue( "positionName", WCSNS, element, null);
		Element elem = XMLTools.getChildElement( "contactInfo", WCSNS, element );
		ContactInfo contactInfo = parseContactInfo(elem);
		return new CitedResponsibleParty(new ContactInfo[] { contactInfo },
				new String[] { indName }, new String[] { orgName },
				new String[] { posName }, null);
	}

	/**
	 * creates a <tt>ContactInfo</tt> object from the passed element
	 * 
	 * @param element
	 * @return @throws
	 *         XMLParsingException
	 */
	private ContactInfo parseContactInfo(Element element )
			throws XMLParsingException {
		if (element == null)
			return null;
		Element elem = XMLTools.getChildElement( "phone", WCSNS, element );
		Phone phone = parsePhone(elem);
		elem = XMLTools.getChildElement( "address", WCSNS, element );
		Address addr = parseAddress(elem, WCSNS);
		elem= XMLTools.getChildElement( "onlineResource", WCSNS, element );
		OnlineResource olr = parseOnLineResource(elem);
		return new ContactInfo(addr, null, null, olr, phone);
	}
	/**
	 * creates a <tt>Phone</tt> object from the passed element
	 * 
	 * @param element
	 * @return @throws
	 *         XMLParsingException
	 */
	private Phone parsePhone(Element element ) {
		if (element == null)
			return null;
		ElementList el = XMLTools.getChildElements( "voice", WCSNS,
				element );
		String[] voice = new String[el.getLength()];
		for (int i = 0; i < voice.length; i++) {
			voice[i] = XMLTools.getStringValue(el.item(i));
		}
		el = XMLTools.getChildElements( "facsimile", WCSNS, element );
		String[] facsimile = new String[el.getLength()];
		for (int i = 0; i < facsimile.length; i++) {
			facsimile[i] = XMLTools.getStringValue(el.item(i));
		}
		return new Phone(facsimile, null, null, voice);
	}

	/**
	 * creates a <tt>Request</tt> object (instance of WCSCapabilityRequest)
	 * from the passed element encapsulating the Request part of the WCS
	 * Capabiliy section
	 * 
	 * @param element
	 * @return created <tt>Request</tt>
	 * @throws XMLParsingException
	 */
	protected OperationsMetadata parseOperations(Element element, URI namespaceURI) 
                                                    throws XMLParsingException {
        
		Element gCapa = XMLTools.getRequiredChildElement( "GetCapabilities", WCSNS, element );
		ElementList el = XMLTools.getChildElements( "DCPType", WCSNS, gCapa);
		DCPType[] dcp = getDCPTypes(el, WCSNS);
		Operation getCapaOperation = new Operation( "GetCapabilities", dcp);

		Element dCover = XMLTools.getRequiredChildElement( "DescribeCoverage", WCSNS, element );
		el = XMLTools.getChildElements( "DCPType", WCSNS, dCover);
		dcp = getDCPTypes(el, WCSNS);
		Operation descCoverOperation = new Operation( "DescribeCoverage", dcp);

		Element gCover = XMLTools.getRequiredChildElement( "GetCoverage", WCSNS, element );
		el = XMLTools.getChildElements( "DCPType", WCSNS, gCover);
		dcp = getDCPTypes(el, WCSNS);
		Operation getCoverOperation = new Operation( "GetCoverage", dcp);

		return new WCSCapabilityOperations( getCapaOperation, descCoverOperation, getCoverOperation );

	}

	/**
	 * creates an array of <tt>CoverageOfferingBrief</tt> objects from the
	 * passed element list encapsulating all CoverageOfferingBrief parts of the
	 * WCS ContentMetadata section
	 * 
	 * @param el
	 * @return creates array of <tt>CoverageOfferingBrief</tt>
	 * @throws XMLParsingException
	 * @throws OGCWebServiceException
	 * @throws OGCException
	 */
	private CoverageOfferingBrief[] parseCoverageOfferingBrief(ElementList el)
			throws XMLParsingException, OGCWebServiceException, OGCException {
		if (el == null)
			return null;
		CoverageOfferingBrief[] cob = new CoverageOfferingBrief[el.getLength()];
		for (int i = 0; i < cob.length; i++) {
			cob[i] = parseCoverageOfferingBrief(el.item(i));
		}
		return cob;
	}

	/**
	 * creates a <tt>CoverageOfferingBrief</tt> object from the passed element
	 * encapsulating one CoverageOfferingBrief part of the WCS ContentMetadata
	 * section
	 * 
	 * @param element
	 * @return created <tt>CoverageOfferingBrief</tt>
	 * @throws XMLParsingException
	 * @throws OGCWebServiceException
	 * @throws OGCException
	 */
	protected CoverageOfferingBrief parseCoverageOfferingBrief(Element element )
			throws XMLParsingException, OGCWebServiceException, OGCException {
		Element elem = XMLTools.getChildElement( "metadataLink", WCSNS, element );
		MetadataLink mLink = parseMetadataLink( elem );
		String desc = XMLTools.getStringValue( "description", WCSNS, element, null );
		String name = XMLTools.getRequiredStringValue( "name", WCSNS, element );
		String label = XMLTools.getRequiredStringValue( "label", WCSNS, element );
		elem = XMLTools.getChildElement( "lonLatEnvelope", WCSNS, element );
		LonLatEnvelope llEnv = parseLonLatEnvelope( elem );
		ElementList el = XMLTools.getChildElements( "keywords", WCSNS, element );
		Keywords[] keywords = parseKeywords(el, WCSNS);

		return new CoverageOfferingBrief( name, label, desc, mLink, llEnv, keywords, null );
	}
   
}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * WCSCapabilitiesDocument.java,v $ Revision 1.21 2004/07/05 06:15:00 ap no
 * message
 * 
 * Revision 1.20 2004/07/02 15:36:11 ap no message
 * 
 * Revision 1.19 2004/06/30 15:16:05 mschneider Refactoring of XMLTools.
 * 
 * Revision 1.18 2004/06/28 06:26:52 ap no message
 * 
 * Revision 1.17 2004/06/23 13:37:40 mschneider More work on the
 * CatalogConfiguration.
 * 
 * 
 *  
 ******************************************************************************/
