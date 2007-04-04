// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/getcapabilities/OGCCapabilitiesDocument.java,v 1.11 2006/04/25 19:28:52 poth Exp $
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
package org.deegree.ogcwebservices.getcapabilities;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.MetadataLink;
import org.deegree.ogcwebservices.MetadataType;
import org.w3c.dom.Element;

/**
 * Most basic capabilities document for any OGC service instance.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/04/25 19:28:52 $
 * 
 * @since 2.0
 */
public abstract class OGCCapabilitiesDocument extends OGCDocument {

    protected static final URI OGCNS = CommonNamespaces.OGCNS;

    /**
     * Returns the value of the version attribute of the capabilities document.
     * 
     * @return
     */
    public String parseVersion() {
        return getRootElement().getAttribute("version");
    }

    /**
     * Returns the value of the updateSequence attribute of the capabilities
     * document.
     * 
     * @return
     */
    public String parseUpdateSequence() {
        return getRootElement().getAttribute("updateSequence");
    }

    /**
     * Creates a <tt>MetadataLink</tt> instance from the passed element.
     * 
     * @param element
     * @return created <tt>MetadataLink</tt>
     * @throws XMLParsingException
     */
    protected MetadataLink parseMetadataLink(Element element)
            throws XMLParsingException {
        if (element == null)
            return null;

        try {
            URL reference = new URL(XMLTools
                    .getAttrValue(element, "xlink:href"));
            String title = XMLTools.getAttrValue(element, "xlink:title");
            URI about = new URI(XMLTools.getAttrValue(element, "about"));
            String tmp = XMLTools.getAttrValue(element, "metadataType");
            MetadataType metadataType = new MetadataType(tmp);
            return new MetadataLink(reference, title, about, metadataType);
        } catch (MalformedURLException e) {
            throw new XMLParsingException(
                    "Couldn't parse metadataLink reference\n"
                            + StringTools.stackTraceToString(e));
        } catch (URISyntaxException e) {
            throw new XMLParsingException("Couldn't parse metadataLink about\n"
                    + StringTools.stackTraceToString(e));
        }
    }

    /**
     * Creates a class representation of the document.
     * 
     * @return class representation of the document
     */
    public abstract OGCCapabilities parseCapabilities() throws InvalidCapabilitiesException;

    /**
     * Creates an <code>Address</code> instance from the passed element.
     * 
     * @param element
     *            Address-element
     * @param namespaceURI
     *            namespace-prefix of all elements
     * @return @throws
     *         XMLParsingException
     */
    protected Address parseAddress(Element element, URI namespaceURI) {
        ElementList el = XMLTools.getChildElements("DeliveryPoint",
                namespaceURI, element);
        String[] deliveryPoint = new String[el.getLength()];
        for (int i = 0; i < deliveryPoint.length; i++) {
            deliveryPoint[i] = XMLTools.getStringValue(el.item(i));
        }
        String city = XMLTools.getStringValue("City", namespaceURI, element,
                null);
        String adminArea = XMLTools.getStringValue("AdministrativeArea",
                namespaceURI, element, null);
        String postalCode = XMLTools.getStringValue("PostalCode", namespaceURI,
                element, null);
        String country = XMLTools.getStringValue("Country", namespaceURI,
                element, null);
        el = XMLTools.getChildElements("ElectronicMailAddress", namespaceURI,
                element);
        String[] eMailAddresses = new String[el.getLength()];
        for (int i = 0; i < eMailAddresses.length; i++) {
            eMailAddresses[i] = XMLTools.getStringValue(el.item(i));
        }
        return new Address(adminArea, city, country, deliveryPoint,
                eMailAddresses, postalCode);
    }

    /**
     * Creates a <tt>Phone</tt> instance from the passed element.
     * 
     * @param element
     *            Phone-element
     * @param namespaceURI
     *            URI that all elements must have
     * @return @throws
     *         XMLParsingException
     */
    protected Phone parsePhone(Element element, URI namespaceURI) {

        // 'Voice'-elements (optional)
        ElementList el = XMLTools.getChildElements("Voice", namespaceURI,
                element);
        String[] voiceNumbers = new String[el.getLength()];
        for (int i = 0; i < voiceNumbers.length; i++) {
            voiceNumbers[i] = XMLTools.getStringValue(el.item(i));
        }

        // 'Facsimile'-elements (optional)
        el = XMLTools.getChildElements("Facsimile", namespaceURI, element);
        String[] facsimileNumbers = new String[el.getLength()];
        for (int i = 0; i < facsimileNumbers.length; i++) {
            facsimileNumbers[i] = XMLTools.getStringValue(el.item(i));
        }
        return new Phone(facsimileNumbers, null, null, voiceNumbers);
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * CapabilitiesDocument.java,v $ Revision 1.7 2004/06/22 13:25:14 ap no message
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
 ******************************************************************************/
