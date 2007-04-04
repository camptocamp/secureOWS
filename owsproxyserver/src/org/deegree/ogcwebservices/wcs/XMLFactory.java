// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/XMLFactory.java,v 1.21 2006/08/07 13:48:08 poth Exp $
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
package org.deegree.ogcwebservices.wcs;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.deegree.datatypes.CodeList;
import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.ValueEnum;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.Linkage;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.ExceptionFormat;
import org.deegree.ogcwebservices.MetadataLink;
import org.deegree.ogcwebservices.SupportedFormats;
import org.deegree.ogcwebservices.SupportedSRSs;
import org.deegree.ogcwebservices.getcapabilities.Capability;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.Service;
import org.deegree.ogcwebservices.wcs.configuration.InvalidConfigurationException;
import org.deegree.ogcwebservices.wcs.configuration.WCSConfiguration;
import org.deegree.ogcwebservices.wcs.configuration.WCSConfigurationDocument;
import org.deegree.ogcwebservices.wcs.configuration.WCSDeegreeParams;
import org.deegree.ogcwebservices.wcs.describecoverage.AxisDescription;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescription;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescriptionDocument;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.describecoverage.DomainSet;
import org.deegree.ogcwebservices.wcs.describecoverage.InvalidCoverageDescriptionExcpetion;
import org.deegree.ogcwebservices.wcs.describecoverage.RangeSet;
import org.deegree.ogcwebservices.wcs.describecoverage.SpatialDomain;
import org.deegree.ogcwebservices.wcs.getcapabilities.ContentMetadata;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilitiesDocument;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilityOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @version $Revision: 1.21 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.21 $, $Date: 2006/08/07 13:48:08 $
 * 
 * @since 1.1
 */
public class XMLFactory extends org.deegree.ogcbase.XMLFactory {
    
    private static final ILogger LOG = LoggerFactory.getLogger(  XMLFactory.class );

    protected static final URI WCSNS = CommonNamespaces.WCSNS;

    protected static final URI DGRNS = CommonNamespaces.DEEGREEWCS;

    /**
     * ... notice:
     * </p>
     * at the moment just HTTP is supported as DCPType. It is assumed that just
     * one HTTP element is assigned within an operation
     * </p>
     * vendor specific capabilities are not supported yet and won't be appended
     * to the document if present.
     * 
     * @param capabilities
     * @return
     * @throws InvalidCapabilitiesException
     */
    public static WCSCapabilitiesDocument export(WCSCapabilities capabilities)
            throws InvalidCapabilitiesException, IOException {
        WCSCapabilitiesDocument wcsCapaDoc = new WCSCapabilitiesDocument();
        try {
            wcsCapaDoc.createEmptyDocument();
            Element root = wcsCapaDoc.getRootElement();

            Service service = capabilities.getService();
            appendService(root, service);
            Capability capability = capabilities.getCapabilitiy();
            appendCapability(root, capability);
            ContentMetadata contentMetadata = capabilities.getContentMetadata();
            appendContentMetadata(root, contentMetadata);
        } catch (XMLParsingException e) {
            LOG.logError( "could not parse the WCSCapabilitiesTemplate.xml", e );
            String s = "could not parse the WCSCapabilitiesTemplate.xml" + "\n" + 
                        e.getMessage();
            throw new InvalidCapabilitiesException(s);
        } catch (SAXException e) {
            LOG.logError( "couldn't create XML Document for CoverageDescription ", e );
            throw new IOException( "couldn't create XML Document for CoverageDescription" + 
                                   e.getMessage() );
        }
        return wcsCapaDoc;
    }

    public static XMLFragment export(WCSConfiguration configuration)
            throws InvalidConfigurationException, IOException {
        WCSConfigurationDocument wcsConfigDoc = new WCSConfigurationDocument();
        try {
            wcsConfigDoc.createEmptyDocument();
            Element root = wcsConfigDoc.getRootElement();
            WCSDeegreeParams deegreeParams = configuration.getDeegreeParams();
            appendDeegreeParams(root, deegreeParams);
            Service service = configuration.getService();
            appendService(root, service);
            Capability capability = configuration.getCapabilitiy();
            appendCapability(root, capability);
            ContentMetadata contentMetadata = configuration
                    .getContentMetadata();
            appendContentMetadata(root, contentMetadata);
        } catch (XMLParsingException e) {
            String s = "could not parse the WCSCapabilitiesTemplate.xml" + "\n"
                    + e.getMessage() + "\n"
                    + StringTools.stackTraceToString(e);
            throw new InvalidConfigurationException(s);
        } catch (SAXException e) {
            throw new IOException(
                    "couldn't create XML Document for CoverageDescription "
                            + e.getMessage() + "\n"
                            + StringTools.stackTraceToString(e));
        }
        return wcsConfigDoc;
    }

    /**
     * exprots a WCS <tt>CoverageDescription</tt> object to its XML
     * represetation encapsulated within a <tt>XmlDocument</tt>
     * 
     * @param coverageDescription
     * @return
     * @throws InvalidCoverageDescriptionExcpetion
     * @throws IOException
     */
    public static XMLFragment export(CoverageDescription coverageDescription)
                                                            throws IOException {
        CoverageDescriptionDocument covDescDoc = new CoverageDescriptionDocument();
        try {
            covDescDoc.createEmptyDocument();
            Element root = covDescDoc.getRootElement();
            root.setAttribute( "version", coverageDescription.getVersion());
            CoverageOffering[] cos = coverageDescription.getCoverageOfferings();
            for (int i = 0; i < cos.length; i++) {
                appendCoverageOffering(root, cos[i]);
            }
        } catch (SAXException e) {
            throw new IOException(
                    "couldn't create XML Document for CoverageDescription "
                            + e.getMessage() + "\n"
                            + StringTools.stackTraceToString(e));
        }
        return covDescDoc;
    }

    /**
     * appends the XML representation of the deegreeParams section to the passed
     * <tt>Element</tt>
     * 
     * @param root
     * @param deegreeParam
     * @throws XMLParsingException
     */
    protected static void appendDeegreeParams(Element root,
            WCSDeegreeParams deegreeParam) throws XMLParsingException {

        Element element = XMLTools.getRequiredChildElement( "deegreeParam",
                DGRNS, root);
        Element node = XMLTools.appendElement(element, DGRNS,
                "deegree:DefaultOnlineResource" );
        node.setAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink" );
        node.setAttribute( "xlink:type", "simple" );
        node.setAttribute( "xlink:href", deegreeParam.getDefaultOnlineResource().toString());
        XMLTools.appendElement(element, DGRNS, "deegree:CacheSize", ""
                + deegreeParam.getCacheSize());
        XMLTools.appendElement(element, DGRNS, "deegree:RequestTimeLimit", ""
                + deegreeParam.getRequestTimeLimit());
        appendDirectoryList(element, deegreeParam.getDirectoryList());

    }

    /**
     * appends the XML representation of a list of data directory names to the
     * passed <tt>XmlNode</tt>
     * 
     * @param dgrParam
     * @param directoryList
     */
    protected static void appendDirectoryList(Element dgrParam,
            String[] directoryList) {
        if (directoryList == null || directoryList.length == 0)
            return;
        Element node = XMLTools.appendElement(dgrParam, DGRNS,
                "deegree:DataDirectoryList", null);
        for (int i = 0; i < directoryList.length; i++) {
            XMLTools.appendElement(node, DGRNS, "deegree:DataDirectory",
                    directoryList[i]);
        }
    }

    protected static void appendCoverageOffering(Element parent, CoverageOffering coverageOffering) {

        //Document doc = XMLTools.create();
        Document doc = parent.getOwnerDocument(); 
        Element xmlNode = doc.createElementNS(WCSNS.toString(), "CoverageDescription" );
        /*
        Element node = doc.createElementNS(WCSNS, "CoverageDescription" );
        doc.appendChild(node);
        Element xmlNode = doc.getDocumentElement();
        */

        appendMetadataLink(xmlNode, coverageOffering.getMetadataLink());
        if (coverageOffering.getDescription() != null) {
            XMLTools.appendElement(xmlNode, WCSNS , "description",
                                   coverageOffering.getName());
        }
        XMLTools.appendElement(xmlNode, WCSNS, "name", coverageOffering.getName());
        XMLTools.appendElement(xmlNode, WCSNS, "label", coverageOffering.getLabel());
        org.deegree.ogcbase.XMLFactory.appendLonLatEnvelope(xmlNode,
                coverageOffering.getLonLatEnvelope(), WCSNS);
        appendKeywords(xmlNode, coverageOffering.getKeywords(), WCSNS);
        appendDomainSet(xmlNode, coverageOffering.getDomainSet());
        appendRangeSet(xmlNode, coverageOffering.getRangeSet());
        appendSupportedCRSs(xmlNode, coverageOffering.getSupportedCRSs());
        appendSupportedFormats(xmlNode, coverageOffering.getSupportedFormats());
        appendSupportedInterpolations(xmlNode, coverageOffering.getSupportedInterpolations());
        //TODO appendExtension
//        XmlNode coveDesc = new XmlNode( parent );
//        coveDesc.appendDomElement( WCSNS, "CoverageOffering", xmlNode);   
        parent.appendChild( xmlNode );
    }

    /**
     * appends the XML representation of the passed <tt>MetadataLink</tt> to
     * the passed <tt>XmlNode</tt> including all attributes
     * 
     * @param xmlNode
     * @param mLink
     */
    protected static void appendMetadataLink(Element xmlNode, MetadataLink mLink) {
        if (mLink != null) {
            Element element = XMLTools.appendElement(xmlNode, WCSNS,
                    "metadataLink" );
            element.setAttribute( "xlink:type", "simple" );
            element.setAttribute( "xlink:href", mLink.getReference().toString());
            element.setAttribute( "about", mLink.getAbout().toString());
            element.setAttribute( "metadataType", mLink.getMetadataType().value);
            element.setAttribute( "xlink:title", mLink.getTitle());
        }
    }

    /**
     * appends a XML representation of the passed <tt>DomainSet</tt> to the
     * passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param domainSet
     */
    protected static void appendDomainSet(Element xmlNode, DomainSet domainSet) {
        Element node = XMLTools.appendElement(xmlNode, WCSNS, "domainSet" );
        appendSpatialDomain(node, domainSet.getSpatialDomain());
        appendTemporalDomain(node, domainSet.getTimeSequence(), WCSNS);
    }

    /**
     * append the XML representation of a <tt>SpatialDomain</tt> object to the
     * passed <tt>XmlNode</tt>. At the moment deegree just considers enclosed
     * gml:Envelope elements gml:Grid and gml:Polygon are not supported yet
     * 
     * @param xmlNode
     * @param spatialDomain
     */
    protected static void appendSpatialDomain(Element xmlNode,
            SpatialDomain spatialDomain) {
        Element node = XMLTools
                .appendElement(xmlNode, WCSNS, "spatialDomain" );
        Envelope[] envelops = spatialDomain.getEnvelops();
        for (int i = 0; i < envelops.length; i++) {
            appendEnvelope(node, envelops[i]);
        }
    }

    /**
     * appends a XML representation of the passed <tt>RangeSet</tt> to the
     * passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param rangeSet
     */
    protected static void appendRangeSet(Element xmlNode, RangeSet rangeSet) {
        Element node = XMLTools.appendElement(xmlNode, WCSNS, "rangeSet" );
        Element tmp = node;
        node = XMLTools.appendElement(tmp, WCSNS, "RangeSet", null);
        Element rs = node;
        appendMetadataLink(rs, rangeSet.getMetadataLink());
        if (rangeSet.getDescription() != null) {
            XMLTools.appendElement(rs, WCSNS, "description", rangeSet
                    .getDescription());
        }
        XMLTools.appendElement(rs, WCSNS, "name", rangeSet.getName());
        XMLTools.appendElement(rs, WCSNS, "label", rangeSet.getLabel());
        AxisDescription[] axisDesc = rangeSet.getAxisDescription();
        if (axisDesc != null) {
            for (int i = 0; i < axisDesc.length; i++) {
                appendAxisDescription(rs, axisDesc[i]);
            }
        }
        appendNullValues(rs, rangeSet.getNullValues());
    }

    /**
     * appends a XML representation of the passed <tt>AxisDescription</tt> to
     * the passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param axisDesc
     */
    protected static void appendAxisDescription(Element xmlNode,
            AxisDescription axisDesc) {
        Element node = XMLTools.appendElement(xmlNode, WCSNS,
                "axisDescription", null);
        Element ad = node;
        node = XMLTools.appendElement(ad, WCSNS, "AxisDescription", null);
        Element aD = node;
        appendMetadataLink(aD, axisDesc.getMetadataLink());
        if (axisDesc.getDescription() != null) {
            XMLTools.appendElement(aD, WCSNS, "description", axisDesc
                    .getDescription());
        }
        XMLTools.appendElement(aD, WCSNS, "name", axisDesc.getName());
        XMLTools.appendElement(aD, WCSNS, "label", axisDesc.getLabel());
        appendValues(aD, axisDesc.getValues(), WCSNS);

    }

    /**
     * appends a XML representation of the passed <tt>ValueEnum</tt> to the
     * passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param values
     */
    protected static void appendNullValues(Element xmlNode, ValueEnum values) {
        Element node = XMLTools.appendElement(xmlNode, WCSNS, "nullValues" );
        Element val = node;
        if (values.getType() != null) {
            node.setAttribute( "xmlns:wcs", WCSNS.toString());
            node.setAttributeNS(WCSNS.toString(), "wcs:type", values.getType().toString());
        }
        if (values.getSemantic() != null) {
            node.setAttribute( "xmlns:wcs", WCSNS.toString());
            node.setAttributeNS(WCSNS.toString(), "wcs:semantic", values.getSemantic()
                    .toString());
        }
        Interval[] intervals = values.getInterval();
        if (intervals != null) {
            for (int i = 0; i < intervals.length; i++) {
                appendInterval(val, intervals[i], WCSNS);
            }
        }
        TypedLiteral[] sVal = values.getSingleValue();
        if (sVal != null) {
            for (int i = 0; i < sVal.length; i++) {
                appendTypedLiteral(val, sVal[i], "singleValue", WCSNS);
            }
        }
    }

    /**
     * appends a XML representation of the passed <tt>SupportedSRSs</tt> to
     * the passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param supportedCRSs
     */
    protected static void appendSupportedCRSs(Element xmlNode,
            SupportedSRSs supportedCRSs) {
        Element node = XMLTools
                .appendElement(xmlNode, WCSNS, "supportedCRSs" );
        Element supCRS = node;
        CodeList[] rrCRS = supportedCRSs.getRequestResponseSRSs();
        for (int i = 0; i < rrCRS.length; i++) {
            appendCodeList(supCRS, rrCRS[i], WCSNS);
        }
        CodeList[] reqCRS = supportedCRSs.getRequestSRSs();
        for (int i = 0; i < reqCRS.length; i++) {
            appendCodeList(supCRS, reqCRS[i], WCSNS);
        }
        CodeList[] resCRS = supportedCRSs.getResponseSRSs();
        for (int i = 0; i < resCRS.length; i++) {
            appendCodeList(supCRS, resCRS[i], WCSNS);
        }
        CodeList[] natCRS = supportedCRSs.getNativeSRSs();
        for (int i = 0; i < natCRS.length; i++) {
            appendCodeList(supCRS, natCRS[i], WCSNS);
        }
    }

    /**
     * appends a XML representation of the passed <tt>SupportedFormats</tt> to
     * the passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param supportedFormats
     */
    protected static void appendSupportedFormats(Element xmlNode,
            SupportedFormats supportedFormats) {
        Element node = XMLTools.appendElement(xmlNode, WCSNS,
                "supportedFormats" );
        CodeList[] formats = supportedFormats.getFormats();
        for (int i = 0; i < formats.length; i++) {
            appendCodeList(node, formats[i], WCSNS);
        }
    }

    /**
     * appends a XML representation of the passed
     * <tt>SupportedInterpolations</tt> to the passed <tt>XmlNode</tt>
     * 
     * @param xmlNode
     * @param supportedInterpolations
     */
    protected static void appendSupportedInterpolations(Element xmlNode,
            SupportedInterpolations supportedInterpolations) {
        Element node = XMLTools.appendElement(xmlNode, WCSNS,
                "supportedInterpolations" );
        if (supportedInterpolations.getDefault() != null) {
            String s = supportedInterpolations.getDefault().value;
            node.setAttribute( "default", s);
        }
        Element supInter = node;
        InterpolationMethod[] ims = supportedInterpolations
                .getInterpolationMethod();
        for (int i = 0; i < ims.length; i++) {
            XMLTools.appendElement(supInter, WCSNS, "interpolationMethod",
                    ims[i].value);
        }
    }

    /**
     * appends a XML representation of the passed <tt>CapabilitiesService</tt>
     * to the passed <tt>XmlNode</tt>
     * 
     * @param element
     * @param service
     */
    protected static void appendService(Element element, Service service) {
        Element elem = XMLTools.getChildElement( "Service", WCSNS, element);
        elem.setAttribute( "version", service.getVersion());
        elem.setAttribute( "updateSequence", service.getUpdateSequence());
        Element servNode = elem;
        appendMetadataLink(servNode, service.getMetadataLink());
        if (service.getDescription() != null) {
            XMLTools.appendElement(servNode, WCSNS, "description", service.getDescription());
        }
        XMLTools.appendElement(servNode, WCSNS, "name", service.getName());
        XMLTools.appendElement(servNode, WCSNS, "label", service.getLabel());

        appendResponsibleParty(servNode, service.getCitedResponsibleParty());
        appendCodeList(servNode, service.getFees(), WCSNS);
        CodeList[] ac = service.getAccessConstraints();
        if (ac != null) {
            for (int i = 0; i < ac.length; i++) {
                appendCodeList(servNode, ac[i], WCSNS);
            }
        }
    }

    /**
     * appends a XML representation of the passed <tt>CitedResponsibleParty</tt>
     * to the passed <tt>XmlNode</tt>
     * 
     * @param servNode
     * @param responsibleParty
     */
    protected static void appendResponsibleParty(Element servNode,
            CitedResponsibleParty responsibleParty) {

        Element element = XMLTools.appendElement(servNode, WCSNS,
                "responsibleParty" );
        Element respNode = element;
        String[] in = responsibleParty.getIndividualName();
        if (in != null) {
            for (int i = 0; i < in.length; i++) {
                XMLTools.appendElement(respNode, WCSNS, "individualName",
                        in[i]);
            }
        }
        String[] on = responsibleParty.getOrganisationName();
        if (on != null) {
            for (int i = 0; i < on.length; i++) {
                XMLTools.appendElement(respNode, WCSNS, "organisationName",
                        on[i]);
            }
        }
        String[] pn = responsibleParty.getPositionName();
        if (pn != null) {
            for (int i = 0; i < pn.length; i++) {
                XMLTools
                        .appendElement(respNode, WCSNS, "positionName", pn[i]);
            }
        }
        appendContactInfo(element, responsibleParty.getContactInfo()[0]);
    }

    /**
     * appends a XML representation of the passed <tt>ContactInfo</tt> to the
     * passed <tt>XmlNode</tt>
     * 
     * @param contactNode
     * @param contactInfo
     */
    protected static void appendContactInfo(Element contactNode,
            ContactInfo contactInfo) {
        Element element = XMLTools.appendElement(contactNode, WCSNS,
                "contactInfo" );
        Element ciNode = element;
        Phone phone = contactInfo.getPhone();
        if (phone != null) {
            appendPhone(ciNode, phone);
        }
        Address address = contactInfo.getAddress();
        if (address != null) {
            appendAddress(ciNode, address);
        }
        OnlineResource olr = contactInfo.getOnLineResource();
        appendOnlineResource(ciNode, olr);
    }

    /**
     * appends a XML representation of the passed <tt>Phone</tt> to the passed
     * <tt>XmlNode</tt>
     * 
     * @param ciNode
     * @param phone
     */
    protected static void appendPhone(Element ciNode, Phone phone) {
        Element node = XMLTools.appendElement(ciNode, WCSNS, "phone" );
        Element pnNode = node;
        String[] voice = phone.getVoice();
        if (voice != null) {
            for (int i = 0; i < voice.length; i++) {
                XMLTools.appendElement(pnNode, WCSNS, "voice", voice[i]);
            }
        }
        String[] facsimile = phone.getFacsimile();
        if (facsimile != null) {
            for (int i = 0; i < facsimile.length; i++) {
                XMLTools.appendElement(pnNode, WCSNS, "facsimile",
                        facsimile[i]);
            }
        }
    }

    /**
     * appends a XML representation of the passed <tt>Phone</tt> to the passed
     * <tt>XmlNode</tt>
     * 
     * @param ciNode
     * @param address
     */
    protected static void appendAddress(Element ciNode, Address address) {
        Element node = XMLTools.appendElement(ciNode, WCSNS, "address" );
        Element adNode = node;
        String[] delPoint = address.getDeliveryPoint();
        if (delPoint != null) {
            for (int i = 0; i < delPoint.length; i++) {
                XMLTools.appendElement(adNode, WCSNS, "deliveryPoint",
                        delPoint[i]);
            }
        }
        XMLTools.appendElement(adNode, WCSNS, "city", address.getCity());
        XMLTools.appendElement(adNode, WCSNS, "administrativeArea", address
                .getAdministrativeArea());
        XMLTools.appendElement(adNode, WCSNS, "postalCode", address
                .getPostalCode());
        XMLTools
                .appendElement(adNode, WCSNS, "country", address.getCountry());
        String[] eMail = address.getElectronicMailAddress();
        if (eMail != null) {
            for (int i = 0; i < eMail.length; i++) {
                XMLTools.appendElement(adNode, WCSNS,
                        "electronicMailAddress", eMail[i]);
            }
        }
    }

    /**
     * appends a XML representation of the passed <tt>OnLineResource</tt> to
     * the passed <tt>XmlNode</tt>
     * 
     * @param ciNode
     * @param olr
     */
    protected static void appendOnlineResource(Element ciNode,
            OnlineResource olr) {
        Element node = XMLTools
                .appendElement(ciNode, WCSNS, "onlineResource" );
        node.setAttribute( "xlink:type", "simple" );
        Linkage linkage = olr.getLinkage();
        node.setAttribute( "xlink:href", linkage.getHref().toString());
    }

    /**
     * appends a XML representation of the passed <tt>OnLineResource</tt> to
     * the passed <tt>Element</tt>. Notice: vendor specific capabilities are
     * not supported yet and, if present, won't be appended to the root element
     * 
     * @param root
     * @param capability
     * @throws XMLParsingException
     */
    protected static void appendCapability(Element root, Capability capability)
            throws XMLParsingException {

        Element capab = XMLTools.getRequiredChildElement( "Capability", WCSNS, root);
        capab.setAttribute( "version", capability.getVersion());
        capab.setAttribute( "updateSequence", capability.getUpdateSequence());
        Element request = XMLTools.getRequiredChildElement( "Request", WCSNS, capab);
        WCSCapabilityOperations req = (WCSCapabilityOperations) capability.getOperations();
        Operation operation = req.getGetCapabilitiesOperation();
        appendOperation( "GetCapabilities", request, operation);
        operation = req.getDescribeCoverageOperation();
        appendOperation( "DescribeCoverage", request, operation);
        operation = req.getGetCoverageOperation();
        appendOperation( "GetCoverage", request, operation);

        ExceptionFormat excepForm = capability.getException();
        appendExceptionFormat(capab, excepForm);

    }

    /**
     * appends a XML representation of the passed <tt>OnLineResource</tt> to
     * the passed <tt>Element</tt>. at the moment just the first DCPType is
     * considered because other types than HTTP are not supported yet.
     * 
     * @param name
     * @param root
     * @param operation
     * @throws XMLParsingException
     */
    protected static void appendOperation(String name, Element root,
            Operation operation) throws XMLParsingException {
        Element getCapa = XMLTools.getRequiredChildElement(name, WCSNS, root);
        Element dcp = XMLTools.getRequiredChildElement( "DCPType", WCSNS,
                getCapa);
        Element http = XMLTools.getRequiredChildElement( "HTTP", WCSNS, dcp);
        Element get = XMLTools.getRequiredChildElement( "Get", WCSNS, http);
        Element getNode = get;
        URL[] urls = ((HTTP) operation.getDCPs()[0].getProtocol()).getGetOnlineResources();
        for (int i = 0; i < urls.length; i++) {
            Element node = XMLTools.appendElement(getNode, WCSNS, "OnlineResource" );
            node.setAttribute( "xlink:type", "simple" );
            node.setAttribute( "xlink:href", urls[i].toString());
        }
        urls = ((HTTP) operation.getDCPs()[0].getProtocol()).getPostOnlineResources();
        if (urls != null && urls.length > 0) {
            Element httpNode = http;
            Element node = XMLTools.appendElement(httpNode, WCSNS, "Post" );
            Element postNode = node;
            for (int i = 0; i < urls.length; i++) {
                XMLTools.appendElement(postNode, WCSNS, "OnlineResource" );
                node.setAttribute( "xlink:type", "simple" );
                node.setAttribute( "xlink:href", urls[i].toString());
            }
        }

    }

    /**
     * appends a XML representation of the passed <tt>OnLineResource</tt> to
     * the passed <tt>Element</tt>.
     * 
     * @param root
     * @param excepForm
     */
    protected static void appendExceptionFormat(Element root,
            ExceptionFormat excepForm) {
        Element node = XMLTools.appendElement(root, WCSNS, "Exception" );
        String[] formats = excepForm.getFormat();
        for (int i = 0; i < formats.length; i++) {
            XMLTools.appendElement(node, WCSNS, "Format", formats[i]);
        }
    }

    /**
     * appends a XML representation of the passed <tt>ContentMetadata</tt> to
     * the passed <tt>Element</tt>
     * 
     * @param root
     * @param contentMetadata
     * @throws XMLParsingException
     */
    protected static void appendContentMetadata(Element root,
            ContentMetadata contentMetadata) throws XMLParsingException {
        Element content = XMLTools.getRequiredChildElement( "ContentMetadata", WCSNS, root);
        content.setAttribute( "version", contentMetadata.getVersion());
        content.setAttribute( "updateSequence", contentMetadata
                .getUpdateSequence());
        Element contentNode = content;
        CoverageOfferingBrief[] cob = contentMetadata.getCoverageOfferingBrief();
        for (int i = 0; i < cob.length; i++) {
            appendCoverageOfferingBrief(contentNode, cob[i]);
        }
    }

    /**
     * appends a XML representation of the passed <tt>CoverageOfferingBrief</tt>
     * to the passed <tt>XmlNode</tt>
     * 
     * @param contentNode
     * @param cob
     */
    protected static void appendCoverageOfferingBrief(Element contentNode,
            CoverageOfferingBrief cob) {
        Element node = XMLTools.appendElement(contentNode, WCSNS, "CoverageOfferingBrief" );
        Element xmlNode = node;
        appendMetadataLink(xmlNode, cob.getMetadataLink());
        if (cob.getDescription() != null) {
            XMLTools.appendElement(xmlNode, WCSNS, "description", cob.getDescription());
        }
        XMLTools.appendElement(xmlNode, WCSNS, "name", cob.getName());
        XMLTools.appendElement(xmlNode, WCSNS, "label", cob.getLabel());
        org.deegree.ogcbase.XMLFactory.appendLonLatEnvelope(xmlNode, cob
                .getLonLatEnvelope(), WCSNS);
        appendKeywords(xmlNode, cob.getKeywords(), WCSNS);
    }
}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * XMLFactory.java,v $ Revision 1.2 2005/02/14 16:58:18 mschneider ** empty log
 * message ***
 * 
 * Revision 1.1.1.1 2005/01/05 10:32:22 poth no message
 * 
 * Revision 1.22 2004/09/07 08:40:42 ap no message
 * 
 * Revision 1.21 2004/08/26 17:29:24 tf no message
 * 
 * Revision 1.20 2004/07/13 14:16:10 mschneider ** empty log message ***
 * 
 * Revision 1.19 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.18 2004/07/06 16:44:25 mschneider More work on
 * CatalogConfiguration and CatalogConfigurationDocument. This includes the
 * hierarchy of these classes.
 * 
 * Revision 1.17 2004/07/05 13:42:38 mschneider Changed deegreeParam to
 * deegreeParams wherever it is used.
 * 
 * Revision 1.16 2004/06/30 15:16:05 mschneider Refactoring of XMLTools.
 * 
 * Revision 1.15 2004/06/28 06:26:52 ap no message
 * 
 * Revision 1.14 2004/06/23 11:55:40 mschneider Changed hierarchy in
 * org.deegree.ogcwebservices.getcapabilities: - OGCCommonCapabilities are
 * derived for Capabilities according to the OGCCommon Implementation
 * Specification 0.2 - OGCStandardCapabilities are derived for Capabilities
 * prior to the OGCCommon Implementation Specification 0.2
 * 
 * Revision 1.13 2004/06/22 11:19:19 mschneider Corrected some misspellings in
 * class and method names.
 * 
 * Revision 1.12 2004/06/21 08:05:49 ap no message
 * 
 * Revision 1.11 2004/06/17 15:43:12 ap no message
 * 
 * Revision 1.10 2004/06/14 14:52:20 ap no message
 * 
 * Revision 1.9 2004/06/14 08:05:58 ap no message
 * 
 * Revision 1.8 2004/06/14 07:56:34 ap no message
 * 
 * Revision 1.7 2004/06/11 08:54:51 ap no message
 * 
 * Revision 1.6 2004/06/09 15:30:37 ap no message
 * 
 * Revision 1.5 2004/06/08 07:01:51 ap no message
 * 
 * Revision 1.4 2004/06/03 11:16:35 ap no message
 * 
 * Revision 1.3 2004/06/03 09:02:20 ap no message
 * 
 * Revision 1.2 2004/06/02 15:47:33 ap no message
 * 
 * Revision 1.1 2004/05/31 07:37:45 ap no message
 *  
 ******************************************************************************/
