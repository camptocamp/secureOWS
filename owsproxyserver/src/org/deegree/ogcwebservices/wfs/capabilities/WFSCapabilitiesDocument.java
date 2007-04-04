// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/capabilities/WFSCapabilitiesDocument.java,v 1.20 2006/11/09 17:47:18 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.capabilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities110Fragment;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.MetadataURL;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.owscommon.OWSCommonCapabilitiesDocument;
import org.deegree.owscommon.OWSDomainType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents a capabilities document for an OGC WFS 1.1.0 compliant web service.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.20 $, $Date: 2006/11/09 17:47:18 $
 */
public class WFSCapabilitiesDocument extends OWSCommonCapabilitiesDocument {

    private static final long serialVersionUID = 6664839532969382269L;

    public final static String FEATURE_TYPE_LIST_NAME = "FeatureTypeList";

    public final static String SERVES_GML_OBJECT_TYPE_LIST_NAME = "ServesGMLObjectTypeList";

    public final static String SUPPORTS_GML_OBJECT_TYPE_LIST_NAME = "SupportsGMLObjectTypeList";

    public final static String FILTER_CAPABILITIES_NAME = "FilterCapabilities";

    protected static final URI WFSNS = CommonNamespaces.WFSNS;

    protected static final URI OGCNS = CommonNamespaces.OGCNS;

    protected static final URI DEEGREEWFSNS = CommonNamespaces.DEEGREEWFS;

    private static final String XML_TEMPLATE = "WFSCapabilitiesTemplate.xml";

    private static final String[] VALID_TYPES = { "TDC211", "FGDC", "19115", "19139" };

    private static final String[] VALID_FORMATS = { "text/xml", "text/html", "text/sgml",
                                                   "text/plain" };

    private static final ILogger LOG = LoggerFactory.getLogger( WFSCapabilitiesDocument.class );

    /**
     * Creates a skeleton capabilities document that contains the mandatory elements only.
     * 
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument()
                            throws IOException, SAXException {
        URL url = WFSCapabilitiesDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * Creates a class representation of the document.
     * 
     * @return class representation of the configuration document
     */
    @Override
    public OGCCapabilities parseCapabilities()
                            throws InvalidCapabilitiesException {
        LOG.entering();
        WFSCapabilities wfsCapabilities = null;
        try {

            wfsCapabilities = new WFSCapabilities( parseVersion(), parseUpdateSequence(),
                                                   getServiceIdentification(),
                                                   getServiceProvider(), getOperationsMetadata(),
                                                   getFeatureTypeList(),
                                                   getServesGMLObjectTypeList(),
                                                   getSupportsGMLObjectTypeList(), null,
                                                   getFilterCapabilities() );
        } catch ( XMLParsingException e ) {
            throw new InvalidCapabilitiesException( e.getMessage() + "\n"
                                                    + StringTools.stackTraceToString( e ) );
        }
        LOG.exiting();
        return wfsCapabilities;
    }

    /**
     * Returns the class representation for the <code>ServiceIdentification</code> section of the
     * document.
     * <p>
     * NOTE: this method is overridden, because the WFS 1.1.0 requires the OWS 1.0.0 version of
     * the element
     * 
     * @return class representation for the <code>ServiceIdentification</code> section
     * @throws XMLParsingException
     */
    @Override
    public ServiceIdentification getServiceIdentification()
                            throws XMLParsingException {

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
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "Given value '"
                                           + XMLTools.getAttrValue( serviceTypeElement, OWSNS,
                                                                    "codeSpace" )
                                           + "' in attribute 'codeSpace' of element 'ServiceType' "
                                           + "(namespace: '" + OWSNS + "') is not a valid URI." );
        }

        // 'ServiceTypeVersion' elements (mandatory)
        String[] serviceTypeVersions = XMLTools.getRequiredNodeAsStrings( element,
                                                                          "ows:ServiceTypeVersion",
                                                                          nsContext, ",;" );
        if ( serviceTypeVersions.length == 0 ) {
            String msg = "No version specified in 'ows:ServiceTypeVersion' element.";
            throw new XMLParsingException( msg );
        }

        // 'Fees' element (optional)
        String fees = XMLTools.getStringValue( "Fees", OWSNS, element, null );

        // 'AccessConstraints' elements (optional)
        String accessConstraints[] = XMLTools.getNodesAsStrings( element, "ows:AccessConstraints",
                                                                 nsContext );

        ServiceIdentification serviceIdentification = new ServiceIdentification(
                                                                                 serviceType,
                                                                                 serviceTypeVersions,
                                                                                 null, null, null,
                                                                                 fees,
                                                                                 accessConstraints );
        return serviceIdentification;
    }

    /**
     * Creates an object representation of the <code>ows:OperationsMetadata</code> section.
     * 
     * @return object representation of the <code>ows:OperationsMetadata</code> section
     * @throws XMLParsingException
     */
    public OperationsMetadata getOperationsMetadata()
                            throws XMLParsingException {
        LOG.entering();
        List operationElementList = XMLTools.getNodes( getRootElement(),
                                                       "ows:OperationsMetadata/ows:Operation",
                                                       nsContext );

        // build HashMap of 'ows:Operation'-elements for easier access
        Map operations = new HashMap();
        for ( int i = 0; i < operationElementList.size(); i++ ) {
            operations.put( XMLTools.getRequiredNodeAsString( (Node) operationElementList.get( i ),
                                                              "@name", nsContext ),
                            operationElementList.get( i ) );
        }

        Operation getCapabilities = getOperation( OperationsMetadata.GET_CAPABILITIES_NAME, true,
                                                  operations );
        Operation describeFeatureType = getOperation(
                                                      WFSOperationsMetadata.DESCRIBE_FEATURETYPE_NAME,
                                                      true, operations );
        Operation getFeature = getOperation( WFSOperationsMetadata.GET_FEATURE_NAME, false,
                                             operations );
        Operation getFeatureWithLock = getOperation(
                                                     WFSOperationsMetadata.GET_FEATURE_WITH_LOCK_NAME,
                                                     false, operations );
        Operation getGMLObject = getOperation( WFSOperationsMetadata.GET_GML_OBJECT_NAME, false,
                                               operations );
        Operation lockFeature = getOperation( WFSOperationsMetadata.LOCK_FEATURE_NAME, false,
                                              operations );
        Operation transaction = getOperation( WFSOperationsMetadata.TRANSACTION_NAME, false,
                                              operations );

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
     * Returns the object representation for the <code>wfs:FeatureTypeList</code>- section.
     * 
     * @return object representation of the <code>wfs:FeatureTypeList</code> section, may be empty
     *         (if missing)
     * @throws XMLParsingException
     */
    public FeatureTypeList getFeatureTypeList()
                            throws XMLParsingException {

        List<WFSFeatureType> wfsFeatureTypes = new ArrayList<WFSFeatureType>();

        FeatureTypeList featureTypeList = new FeatureTypeList(
                                                               new org.deegree.ogcwebservices.wfs.capabilities.Operation[0],
                                                               wfsFeatureTypes );

        Element element = (Element) XMLTools.getNode( getRootElement(), "wfs:FeatureTypeList",
                                                      nsContext );
        if ( element != null ) {
            org.deegree.ogcwebservices.wfs.capabilities.Operation[] globalOperations = null;
            Element operationsTypeElement = (Element) XMLTools.getNode( element, "wfs:Operations",
                                                                        nsContext );
            if ( operationsTypeElement != null ) {
                globalOperations = getOperationsType( operationsTypeElement );
            }
            List featureTypeElementList = XMLTools.getNodes( element, "wfs:FeatureType", nsContext );
            // TODO Check this.
            // if ( featureTypeElementList.getLength() < 1 ) {
            // throw new XMLParsingException(
            // "A wfs:FeatureTypeListType must contain at least one wfs:FeatureType-element." );
            // }
            for ( int i = 0; i < featureTypeElementList.size(); i++ ) {
                WFSFeatureType wfsFT = getFeatureTypeType( (Element) featureTypeElementList.get( i ) );
                wfsFeatureTypes.add( wfsFT );
            }

            featureTypeList = new FeatureTypeList( globalOperations, wfsFeatureTypes );
        }

        return featureTypeList;
    }

    /**
     * Returns the object representation for the <code>wfs:ServesGMLObjectTypeList</code>-
     * section.
     * 
     * @return object representation of the <code>wfs:ServesGMLObjectTypeList</code> section, null
     *         if the section does not exist
     * @throws XMLParsingException
     */
    public GMLObject[] getServesGMLObjectTypeList()
                            throws XMLParsingException {
        LOG.entering();
        GMLObject[] gmlObjectTypes = null;
        Element element = (Element) XMLTools.getNode( getRootElement(),
                                                      "wfs:ServesGMLObjectTypeList", nsContext );
        if ( element != null ) {
            List nodeList = XMLTools.getRequiredNodes( element, "wfs:GMLObjectType", nsContext );
            gmlObjectTypes = new GMLObject[nodeList.size()];
            for ( int i = 0; i < gmlObjectTypes.length; i++ ) {
                gmlObjectTypes[i] = getGMLObjectType( (Element) nodeList.get( i ) );
            }
        }
        LOG.exiting();
        return gmlObjectTypes;
    }

    /**
     * Returns the object representation for the <code>wfs:SupportsGMLObjectTypeList</code>-
     * section.
     * 
     * @return object representation of the <code>wfs:SupportsGMLObjectTypeList</code> section,
     *         null if the section does not exist
     * @throws XMLParsingException
     */
    public GMLObject[] getSupportsGMLObjectTypeList()
                            throws XMLParsingException {
        LOG.entering();
        GMLObject[] gmlObjectTypes = null;
        Element element = (Element) XMLTools.getNode( getRootElement(),
                                                      "wfs:SupportsGMLObjectTypeList", nsContext );
        if ( element != null ) {
            List nodeList = XMLTools.getRequiredNodes( element, "wfs:GMLObjectType", nsContext );
            gmlObjectTypes = new GMLObject[nodeList.size()];
            for ( int i = 0; i < gmlObjectTypes.length; i++ ) {
                gmlObjectTypes[i] = getGMLObjectType( (Element) nodeList.get( i ) );
            }
        }
        LOG.exiting();
        return gmlObjectTypes;
    }

    /**
     * Returns the object representation for an element of type <code>wfs:GMLObjectType</code>.
     * 
     * @param element
     * @return object representation of the element of type <code>wfs:GMLObjectType</code>
     * @throws XMLParsingException
     */
    public GMLObject getGMLObjectType( Element element )
                            throws XMLParsingException {
        QualifiedName name = parseQualifiedName( XMLTools.getRequiredNode( element,
                                                                           "wfs:Name/text()",
                                                                           nsContext ) );
        String title = XMLTools.getNodeAsString( element, "wfs:Title/text()", nsContext, null );
        String abstract_ = XMLTools.getNodeAsString( element, "wfs:Abstract/text()", nsContext,
                                                     null );
        Keywords[] keywords = getKeywords( XMLTools.getNodes( element, "ows:Keywords", nsContext ) );
        List formatElementList = XMLTools.getNodes( element, "wfs:OutputFormats/wfs:Format",
                                                    nsContext );
        FormatType[] outputFormats = new FormatType[formatElementList.size()];
        for ( int i = 0; i < outputFormats.length; i++ ) {
            outputFormats[i] = getFormatType( (Element) formatElementList.get( i ) );
        }
        return new GMLObject( name, title, abstract_, keywords, outputFormats );
    }

    /**
     * Returns the object representation for an element of type <code>wfs:FeatureTypeType</code>.
     * 
     * @param element
     * @return object representation for the element of type <code>wfs:OperationsType</code>
     * @throws XMLParsingException
     */
    public WFSFeatureType getFeatureTypeType( Element element )
                            throws XMLParsingException {
        LOG.entering();
        QualifiedName name = parseQualifiedName( XMLTools.getRequiredNode( element,
                                                                           "wfs:Name/text()",
                                                                           nsContext ) );
        String title = XMLTools.getRequiredNodeAsString( element, "wfs:Title/text()", nsContext );
        String abstract_ = XMLTools.getNodeAsString( element, "wfs:Abstract/text()", nsContext,
                                                     null );
        Keywords[] keywords = getKeywords( XMLTools.getNodes( element, "ows:Keywords", nsContext ) );

        URI defaultSrs = null;
        URI[] otherSrs = null;
        Node noSrsElement = XMLTools.getNode( element, "wfs:NoSRS", nsContext );
        if ( noSrsElement == null ) {
            defaultSrs = XMLTools.getNodeAsURI( element, "wfs:DefaultSRS/text()", nsContext, null );
            if ( defaultSrs == null ) {
                String msg = "A 'wfs:FeatureType' element must always contain a 'wfs:NoSRS' "
                             + "element  or a 'wfs:DefaultSRS' element";
                throw new XMLParsingException (msg);
            }
            otherSrs = XMLTools.getNodesAsURIs( element, "wfs:OtherSRS/text()", nsContext );
        }

        org.deegree.ogcwebservices.wfs.capabilities.Operation[] operations = null;
        Element operationsTypeElement = (Element) XMLTools.getNode( element, "wfs:Operations",
                                                                    nsContext );
        if ( operationsTypeElement != null ) {
            operations = getOperationsType( operationsTypeElement );
        }
        List formatElementList = XMLTools.getNodes( element, "wfs:OutputFormats/wfs:Format",
                                                    nsContext );
        FormatType[] formats = new FormatType[formatElementList.size()];
        for ( int i = 0; i < formats.length; i++ ) {
            formats[i] = getFormatType( (Element) formatElementList.get( i ) );
        }
        List wgs84BoundingBoxElements = XMLTools.getNodes( element, "ows:WGS84BoundingBox",
                                                           nsContext );
        if ( wgs84BoundingBoxElements.size() < 1 ) {
            throw new XMLParsingException( "A 'wfs:FeatureTypeType' must contain at least one "
                                           + "'ows:WGS84BoundingBox'-element." );
        }
        Envelope[] wgs84BoundingBoxes = new Envelope[wgs84BoundingBoxElements.size()];
        for ( int i = 0; i < wgs84BoundingBoxes.length; i++ ) {
            wgs84BoundingBoxes[i] = getWGS84BoundingBoxType( (Element) wgs84BoundingBoxElements.get( i ) );
        }
        List metadataURLElementList = XMLTools.getNodes( element, "wfs:MetadataURL", nsContext );
        MetadataURL[] metadataUrls = new MetadataURL[metadataURLElementList.size()];
        for ( int i = 0; i < metadataUrls.length; i++ ) {
            metadataUrls[i] = getMetadataURL( (Element) metadataURLElementList.get( i ) );
        }
        WFSFeatureType featureType = new WFSFeatureType( name, title, abstract_, keywords,
                                                         defaultSrs, otherSrs, operations, formats,
                                                         wgs84BoundingBoxes, metadataUrls );
        LOG.exiting();
        return featureType;
    }

    /**
     * Returns the object representation for an <code>wfs:OutputFormat</code> -element.
     * 
     * @param element
     * @return object representation for the element
     * @throws XMLParsingException
     */
    public FormatType getFormatType( Element element )
                            throws XMLParsingException {

        LOG.entering();
        URI inFilter = XMLTools.getNodeAsURI( element, "@deegreewfs:inFilter", nsContext, null );
        URI outFilter = XMLTools.getNodeAsURI( element, "@deegreewfs:outFilter", nsContext, null );
        URI schemaLocation = XMLTools.getNodeAsURI( element, "@deegreewfs:schemaLocation",
                                                    nsContext, null );
        String value = XMLTools.getRequiredNodeAsString( element, "text()", nsContext );
        FormatType outputFormat = new FormatType( inFilter, outFilter, schemaLocation, value );
        LOG.exiting();
        return outputFormat;
    }

    /**
     * Returns the object representation for an element node of type
     * <code>wfs:MetadataURLType</code>.
     * 
     * TODO: Schema says base type is String, not URL!
     * 
     * @param element
     * @return object representation for the element of type <code>wfs:MetadataURLType</code>
     * @throws XMLParsingException
     */
    public MetadataURL getMetadataURL( Element element )
                            throws XMLParsingException {
        LOG.entering();
        String type = XMLTools.getRequiredNodeAsString( element, "@type", nsContext, VALID_TYPES );
        String format = XMLTools.getRequiredNodeAsString( element, "@format", nsContext,
                                                          VALID_FORMATS );
        String url = XMLTools.getRequiredNodeAsString( element, "text()", nsContext );
        URL onlineResource;
        try {
            onlineResource = new URL( url );
        } catch ( MalformedURLException e ) {
            throw new XMLParsingException( "A wfs:MetadataURLType must contain a valid URL: "
                                           + e.getMessage() );
        }
        LOG.exiting();
        return new MetadataURL( type, format, onlineResource );
    }

    /**
     * Returns the object representation for an element node of type <code>wfs:OperationsType</code>.
     * 
     * @param element
     * @return object representation for the element of type <code>wfs:OperationsType</code>
     * @throws XMLParsingException
     */
    public org.deegree.ogcwebservices.wfs.capabilities.Operation[] getOperationsType(
                                                                                     Element element )
                            throws XMLParsingException {
        LOG.entering();
        String[] operationCodes = XMLTools.getNodesAsStrings( element, "wfs:Operation/text()",
                                                              nsContext );
        org.deegree.ogcwebservices.wfs.capabilities.Operation[] operations = new org.deegree.ogcwebservices.wfs.capabilities.Operation[operationCodes.length];
        for ( int i = 0; i < operations.length; i++ ) {
            try {
                operations[i] = new org.deegree.ogcwebservices.wfs.capabilities.Operation(
                                                                                           operationCodes[i] );
            } catch ( InvalidParameterException e ) {
                throw new XMLParsingException( e.getMessage() );
            }
        }
        LOG.exiting();
        return operations;
    }

    /**
     * Returns the object representation for the <code>Filter_Capabilities</code> section of the
     * document.
     * 
     * @return class representation for the <code>Filter_Capabilities</code> section
     * @throws XMLParsingException
     */
    public FilterCapabilities getFilterCapabilities()
                            throws XMLParsingException {

        FilterCapabilities filterCapabilities = null;
        Element filterCapabilitiesElement = (Element) XMLTools.getNode( getRootElement(),
                                                                        "ogc:Filter_Capabilities",
                                                                        nsContext );
        if ( filterCapabilitiesElement != null ) {
            filterCapabilities = new FilterCapabilities110Fragment( filterCapabilitiesElement,
                                                                    getSystemId() ).parseFilterCapabilities();
        }
        return filterCapabilities;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSCapabilitiesDocument.java,v $
 Revision 1.20  2006/11/09 17:47:18  mschneider
 Improved parsing of NoSRS element.

 Revision 1.19  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.18  2006/10/02 16:53:34  mschneider
 Reformatted.

 Revision 1.17  2006/09/21 12:21:49  mschneider
 Changed #getFilterCapabilities() to comply to WFS 1.1.0 and Filter Capabilities 1.1.0 specification.

 Revision 1.16  2006/09/20 12:58:50  mschneider
 Added overriden #getServiceIdentification() method for OWS 1.0.0 compliance.

 Revision 1.15  2006/07/21 14:10:03  mschneider
 Added constants for section names.

 Revision 1.14  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */