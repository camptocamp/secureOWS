// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/csw/capabilities/CatalogCapabilitiesDocument.java,v
// 1.22 2004/08/05 15:40:08 ap Exp $
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
package org.deegree.ogcwebservices.csw.capabilities;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities100Fragment;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.OWSCommonCapabilitiesDocument;
import org.deegree.owscommon.OWSDomainType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents an XML capabilities document for an OGC CSW 2.0 compliant service.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.13 $, $Date: 2006/08/02 21:14:52 $
 * 
 * @since 2.0
 *  
 */
public class CatalogueCapabilitiesDocument extends OWSCommonCapabilitiesDocument {
    
    private static final ILogger LOG = 
        LoggerFactory.getLogger( CatalogueCapabilitiesDocument.class );

    public final static String FILTER_CAPABILITIES_NAME = "FilterCapabilities";

    protected static final URI OGCNS = CommonNamespaces.OGCNS;

    private static final String XML_TEMPLATE = "CatalogueCapabilitiesTemplate.xml";

    /**
     * Creates a skeleton capabilities document that contains the mandatory
     * elements only.
     * 
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument() throws IOException, SAXException {
        URL url = CatalogueCapabilitiesDocument.class.getResource(XML_TEMPLATE);
        if (url == null) {
            throw new IOException("The resource '" + XML_TEMPLATE
                    + " could not be found.");
        }
        load(url);
    }

    /**
     * Creates a class representation of the document.
     * 
     * @return class representation of the configuration document
     */
    public OGCCapabilities parseCapabilities()
            throws InvalidCapabilitiesException {
        try {
            FilterCapabilities filterCapabilities = null;
            Element filterCapabilitiesElement = 
                (Element) XMLTools.getNode( getRootElement(), "ogc:Filter_Capabilities", nsContext );
            if (filterCapabilitiesElement != null) {
                filterCapabilities = 
                    new FilterCapabilities100Fragment(filterCapabilitiesElement, getSystemId()).parseFilterCapabilities();
            }
            return new CatalogueCapabilities(parseVersion(), parseUpdateSequence(),
                    getServiceIdentification(), getServiceProvider(),
                    getOperationsMetadata(), null, filterCapabilities);
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new InvalidCapabilitiesException( e.getMessage() );
        }
    }

    /**
     * Creates a class representation of the <code>OperationsMetadata</code>-
     * section.
     * 
     * @return
     * @throws InvalidConfigurationException
     */
    public OperationsMetadata getOperationsMetadata() throws XMLParsingException {

        Node root = this.getRootElement();
        Node omNode = XMLTools.getRequiredChildElement("OperationsMetadata", OWSNS, root);
        ElementList elementList = XMLTools.getChildElements("Operation", OWSNS, omNode);

        ElementList parameterElements = XMLTools.getChildElements("Parameter", OWSNS, omNode);
        OWSDomainType[] parameters = new OWSDomainType[parameterElements.getLength()];

        for (int i = 0; i < parameters.length; i++) {            
            parameters[i] = getOWSDomainType( null, parameterElements.item(i));
        }

        // build HashMap of 'Operation'-elements for easier access
        HashMap operations = new HashMap();
        for (int i = 0; i < elementList.getLength(); i++) {
            operations.put( XMLTools.getRequiredAttrValue("name", null,
                            elementList.item(i)), elementList.item(i));
        }

        // 'GetCapabilities'-operation
        Operation getCapabilites = getOperation(
                OperationsMetadata.GET_CAPABILITIES_NAME, true, operations);
        // 'DescribeRecord'-operation
        Operation describeRecord = getOperation(
                CatalogueOperationsMetadata.DESCRIBE_RECORD_NAME, true,
                operations);
        // 'GetDomain'-operation
        Operation getDomain = getOperation(
                CatalogueOperationsMetadata.GET_DOMAIN_NAME, false, operations);
        // 'GetRecords'-operation
        Operation getRecords = getOperation(
                CatalogueOperationsMetadata.GET_RECORDS_NAME, true, operations);
        // 'GetRecordById'-operation
        Operation getRecordById = getOperation(
                CatalogueOperationsMetadata.GET_RECORD_BY_ID_NAME, true,
                operations);
        // 'Transaction'-operation
        Operation transaction = getOperation(
                CatalogueOperationsMetadata.TRANSACTION_NAME, false, operations);
        // 'Harvest'-operation
        Operation harvest = getOperation(
                CatalogueOperationsMetadata.HARVEST_NAME, false, operations);

        return new CatalogueOperationsMetadata(getCapabilites, describeRecord,
                getDomain, getRecords, getRecordById, transaction, harvest,
                parameters, null);
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueCapabilitiesDocument.java,v $
Revision 1.13  2006/08/02 21:14:52  poth
code formating

Revision 1.12  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
