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

package org.deegree.ogcwebservices.csw.discovery;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents an XML DescribeRecord document of an OGC CSW 2.0 compliant
 * service.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version 2.0, $Revision: 1.21 $, $Date: 2006/07/10 13:12:44 $
 * 
 * @since 2.0
 */

public class DescribeRecordDocument extends OGCDocument {

    private static final long serialVersionUID = 6554937884331546780L;

    private static final ILogger LOG = LoggerFactory.getLogger(DescribeRecordDocument.class);

    private static final String XML_TEMPLATE = "DescribeRecordTemplate.xml";


    /**
     * Extracts a <code>DescribeRecord</code> representation of this object.
     * 
     * TODO use QualifiedName for type names
     * 
     * @param id
     *            unique ID of the request
     * @return @throws
     *         MissingParameterValueException
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     */
    public DescribeRecord parse(String id)
            throws MissingParameterValueException,
            InvalidParameterValueException, OGCWebServiceException {

        LOG.entering();

        String version;
        Map vendorSpecificParameter = null;
        Map namespaceMappings = null;
        String[] typeNames = null;
        String outputFormat = "text/xml";
        URI schemaLanguage = null;

        try {
            // '<csw:DescribeRecord>'-element (required)
            Node contextNode = XMLTools.getRequiredNode(this.getRootElement(),
                    "self::csw:DescribeRecord", nsContext);

            // 'service'-attribute (required, must be CSW)
            String service = XMLTools.getRequiredNodeAsString(contextNode,
                    "@service", nsContext);
            if (!service.equals("CSW")) {
                ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
                throw new InvalidParameterValueException("DescribeRecords",
                        "'service' must be 'CSW'", code);
            }

            // 'version'-attribute (required)
            version = XMLTools.getRequiredNodeAsString(contextNode, "@version",
                    nsContext);

            // 'outputFormat'-attribute (optional, default="text/xml")
            outputFormat = XMLTools.getNodeAsString(contextNode,
                    "@outputFormat", nsContext, outputFormat);

            // 'schemaLanguage'-attribute (optional,
            // default="http://www.w3.org/XML/Schema")
            String schemaLanguageString = XMLTools.getNodeAsString(contextNode,
                    "@schemaLanguage", nsContext, "http://www.w3.org/XML/Schema");
            try {
                schemaLanguage = new URI(schemaLanguageString);
            } catch (URISyntaxException e) {
                throw new XMLParsingException("Value '" + schemaLanguageString
                        + "' for attribute 'schemaLanguage' is invalid. Must "
                        + "denote a valid URI.");
            }

            // 'csw:TypeName' elements
            List nl = XMLTools.getNodes( contextNode, "csw:TypeName", nsContext );
            typeNames = new String [nl.size()];
            for ( int i = 0; i < typeNames.length; i++ ) {
                typeNames [i] = parseTypeName( (Element) nl.get( i ) );
            }
        } catch (Exception e) {
            ExceptionCode code = ExceptionCode.INVALID_FORMAT;
            throw new OGCWebServiceException("CatalogGetCapabilities",
                    StringTools.stackTraceToString(e), code);
        }

        LOG.exiting();

        return new DescribeRecord(id, version, vendorSpecificParameter,
                namespaceMappings, typeNames, outputFormat, schemaLanguage);
    }

    /**
     * Parses a <code>TypeName</code> element into a <code>QualifiedName</code>.
     * 
     * TODO respect "targetNamespace" attribute and really return a <code>QualifiedName</code>.
     * 
     * @return
     * @throws XMLParsingException 
     */
    private String parseTypeName (Element root) throws XMLParsingException {
        return XMLTools.getRequiredNodeAsString( root, "text()", nsContext );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.framework.xml.XMLFragment#createEmptyDocument()
     */
    void createEmptyDocument() throws IOException, SAXException {
        URL url = DescribeRecordDocument.class.getResource(XML_TEMPLATE);
        if (url == null) {
            throw new IOException("The resource '" + XML_TEMPLATE
                    + " could not be found.");
        }
        load(url);
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeRecordDocument.java,v $
Revision 1.21  2006/07/10 13:12:44  mschneider
Added parsing of TypeName elements.

Revision 1.20  2006/06/19 19:26:29  mschneider
Changed visibility. Added footer.

********************************************************************** */