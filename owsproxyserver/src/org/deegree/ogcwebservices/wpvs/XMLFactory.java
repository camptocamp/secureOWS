//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/XMLFactory.java,v 1.23 2006/11/27 15:43:50 bezema Exp $
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

package org.deegree.ogcwebservices.wpvs;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.ValueRange;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.BaseURL;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.ImageURL;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wpvs.capabilities.DataProvider;
import org.deegree.ogcwebservices.wpvs.capabilities.Dataset;
import org.deegree.ogcwebservices.wpvs.capabilities.Dimension;
import org.deegree.ogcwebservices.wpvs.capabilities.Identifier;
import org.deegree.ogcwebservices.wpvs.capabilities.MetaData;
import org.deegree.ogcwebservices.wpvs.capabilities.Style;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSCapabilities;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSCapabilitiesDocument;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSOperationsMetadata;
import org.deegree.owscommon.OWSMetadata;
import org.deegree.owscommon.com110.HTTP110;
import org.deegree.owscommon.com110.OWSAllowedValues;
import org.deegree.owscommon.com110.OWSDomainType110;
import org.deegree.owscommon.com110.OWSRequestMethod;
import org.deegree.owscommon.com110.Operation110;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.23 $, $Date: 2006/11/27 15:43:50 $
 * 
 * @since 2.0
 */
public class XMLFactory extends org.deegree.owscommon.XMLFactory {

    private static final URI WPVSNS = CommonNamespaces.WPVSNS;
    private static final String PRE_OWS = CommonNamespaces.OWS_PREFIX+':';

    /**
     * This method exporst a wpvs capabilitiesDocument with following information taken from the
     * given WPVSCapabilities
     * <ul>
     *  <li>ServiceIdentification</li>
     *  <li>ServiceProvide</li>
     *  <li>operationMetadata</li>
     *  <li>the root dataset</li>
     * </ul>
     * @param wpvsCapabilities
     * @return the WPVSCapabilitiesDocument of this wpvs
     * @throws IOException
     */
    public static WPVSCapabilitiesDocument export( WPVSCapabilities wpvsCapabilities )
                            throws IOException {

        LOG.entering();
        WPVSCapabilitiesDocument wpvsCapabilitiesDocument = new WPVSCapabilitiesDocument();

        try {
            wpvsCapabilitiesDocument.createEmptyDocument();
            Element root = wpvsCapabilitiesDocument.getRootElement();

            ServiceIdentification serviceIdentification = wpvsCapabilities.getServiceIdentification();
            if ( serviceIdentification != null ) {
                appendServiceIdentification( root, serviceIdentification );
            }

            ServiceProvider serviceProvider = wpvsCapabilities.getServiceProvider();
            if ( serviceProvider != null ) {
                appendServiceProvider( root, serviceProvider );
            }

            OperationsMetadata operationMetadata = wpvsCapabilities.getOperationsMetadata();
            if ( operationMetadata != null && operationMetadata instanceof WPVSOperationsMetadata ) {
                appendWPVSOperationsMetadata( root, (WPVSOperationsMetadata) operationMetadata );
            }

            Dataset dataset = wpvsCapabilities.getDataset();
            if ( dataset != null ) {
                appendDataset( root, dataset );
            }

        } catch ( SAXException e ) {
            e.printStackTrace();
            LOG.logError( e.getMessage(), e );
        }

        LOG.exiting();
        return wpvsCapabilitiesDocument;
    }

    /**
     * Appends the DOM representation of an <code>WPVSOperationsMetadata</code> to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param operationsMetadata
     */
    private static void appendWPVSOperationsMetadata( Element root,
                                                     WPVSOperationsMetadata operationsMetadata ) {
        // 'ows:OperationsMetadata'-element
        Element operationsMetadataNode = XMLTools.appendElement( root, OWSNS,
                                                                 PRE_OWS+"OperationsMetadata" );

        // append all Operations
        Operation110[] operations = (Operation110[]) operationsMetadata.getAllOperations();
        for ( int i = 0; i < operations.length; i++ ) {
            Operation110 operation = operations[i];

            // 'ows:Operation' - element
            Element operationElement = XMLTools.appendElement( operationsMetadataNode, OWSNS,
                                                               PRE_OWS+"Operation" );
            operationElement.setAttribute( "name", operation.getName() );

            // 'ows:DCP' - elements
            DCPType[] dcps = operation.getDCPs();
            for ( int j = 0; j < dcps.length; j++ ) {
                appendDCP( operationElement, dcps[j] );
            }

            // 'ows:Parameter' - elements
            OWSDomainType110[] parameters = operation.getParameters110();
            for ( int j = 0; j < parameters.length; j++ ) {
                appendDomainType( operationElement, parameters[j], PRE_OWS+"Parameter" );
            }

            // 'ows:Constraint' - elements
            OWSDomainType110[] constraints = operation.getConstraints110();
            for ( int j = 0; j < constraints.length; j++ ) {
                appendDomainType( operationElement, constraints[j], PRE_OWS+"Constraint" );
            }

            // 'ows:Metadata' - elements
            OWSMetadata[] metadata = operation.getMetadata110();
            for ( int j = 0; j < metadata.length; j++ ) {
                appendOWSMetadata( operationElement, metadata[j], PRE_OWS+"Metadata" );
            }
        }

        // append general parameters
        OWSDomainType110[] parameters = operationsMetadata.getParameters110();
        for ( int i = 0; i < parameters.length; i++ ) {
            appendDomainType( operationsMetadataNode, parameters[i], PRE_OWS+"Parameter" );
        }

        // append general constraints
        OWSDomainType110[] constraints = operationsMetadata.getConstraints110();
        for ( int i = 0; i < constraints.length; i++ ) {
            appendDomainType( operationsMetadataNode, constraints[i], PRE_OWS+"Constraint" );
        }

        // append 'ows:ExtendedCapabilities'
        // TODO when needed.

    }

    /**
     * Appends the DOM representation of an <code>OWSMetadata</code> to the passed
     * <code>Element</code>. The given <code>String</code> is used to distinguish between the
     * different Metadata types.
     * 
     * @param element
     * @param metadata
     * @param tagName
     */
    private static void appendOWSMetadata( Element element, OWSMetadata metadata, String tagName ) {

        if ( metadata != null ) {

            Element metadataElement = XMLTools.appendElement( element, OWSNS, tagName );

            appendSimpleLinkAttributes( metadataElement, metadata.getLink() );

            Element nameElement = XMLTools.appendElement( metadataElement, OWSNS, CommonNamespaces.OWS_PREFIX+":Name" );
            metadataElement.appendChild( nameElement );
            nameElement.setNodeValue( metadata.getName() );
        }

    }

    /**
     * Appends the DOM representation of an <code>OWSDomainType</code> to the passed
     * <code>Element</code>. The given <code>String</code> is used to distinguish between
     * <code>Parameter</code> and <code>Constraint</code>.
     * 
     * @param element
     * @param domainType
     * @param tagName
     */
    private static void appendDomainType( Element element, OWSDomainType110 domainType,
                                         String tagName ) {

        Element domainElement = XMLTools.appendElement( element, OWSNS, tagName );

        // attribute
        domainElement.setAttribute( "name", domainType.getName() );

        // elements
        OWSAllowedValues allowedValues = domainType.getAllowedValues();
        OWSMetadata valuesListReference = domainType.getValuesListReference();
        if ( allowedValues != null ) {
            appendAllowedValues( domainElement, allowedValues );
        } else if ( domainType.isAnyValue() ) {
            Element anyElement = XMLTools.appendElement( domainElement, OWSNS, CommonNamespaces.OWS_PREFIX+":AnyValue" );
            // TODO content of this tag!
        } else if ( domainType.hasNoValues() ) {
            Element noValuesElement = XMLTools.appendElement( domainElement, OWSNS, CommonNamespaces.OWS_PREFIX+":NoValues" );
            // TODO content of this tag!
        } else if ( valuesListReference != null ) {
            appendOWSMetadata( domainElement, valuesListReference, CommonNamespaces.OWS_PREFIX+":ValuesListReference" );
        } else {
            // TODO "domainType object is invalid!"
        }

        appendTypedLiteral( domainElement, domainType.getDefaultValue(), PRE_OWS+"DefaultValue", OWSNS );

        appendOWSMetadata( domainElement, domainType.getMeaning(), PRE_OWS+"Meaning" );

        appendOWSMetadata( domainElement, domainType.getOwsDataType(), PRE_OWS+"DataType" );

        String measurement = domainType.getMeasurementType();
        if ( OWSDomainType110.REFERENCE_SYSTEM.equals( measurement ) ) {
            appendOWSMetadata( domainElement, domainType.getMeasurement(), PRE_OWS+"ReferenceSystem" );
        } else if ( OWSDomainType110.UOM.equals( measurement ) ) {
            appendOWSMetadata( domainElement, domainType.getMeasurement(), PRE_OWS+"UOM" );
        }

        OWSMetadata[] metadata = domainType.getMetadata();
        for ( int i = 0; i < metadata.length; i++ ) {
            appendOWSMetadata( domainElement, metadata[i], PRE_OWS+"Metadata" );
        }

    }

    /**
     * Appends the DOM representation of an <code>OWSAllowedValues</code> object to the passed
     * <code>Element</code>.
     * 
     * @param element
     * @param allowedValues
     */
    private static void appendAllowedValues( Element element, OWSAllowedValues allowedValues ) {

        Element allowedElement = XMLTools.appendElement( element, OWSNS, PRE_OWS+"AllowedValues" );

        TypedLiteral[] literals = allowedValues.getOwsValues();
        for ( int i = 0; i < literals.length; i++ ) {
            appendTypedLiteral( allowedElement, literals[i], PRE_OWS+"Value", OWSNS );
        }

        ValueRange[] range = allowedValues.getValueRanges();
        for ( int i = 0; i < range.length; i++ ) {
            Element rangeElement = XMLTools.appendElement( allowedElement, OWSNS, PRE_OWS+"Range" );

            appendTypedLiteral( rangeElement, range[i].getMin(), PRE_OWS+"MinimumValue", OWSNS );
            appendTypedLiteral( rangeElement, range[i].getMax(), PRE_OWS+"MaximumValue", OWSNS );
            appendTypedLiteral( rangeElement, range[i].getSpacing(), PRE_OWS+"Spacing", OWSNS );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.owscommon.XMLFactory#appendDCP(org.w3c.dom.Element,
     *      org.deegree.ogcwebservices.getcapabilities.DCPType)
     */
    protected static void appendDCP( Element operationElement, DCPType dcp ) {

        // 'ows:DCP'-element
        Element dcpNode = XMLTools.appendElement( operationElement, OWSNS, PRE_OWS+"DCP" );

        // currently, the only supported DCP are HTTP and HTTP110!
        if ( dcp.getProtocol() instanceof HTTP110 ) {
            HTTP110 http = (HTTP110) dcp.getProtocol();

            // 'ows:HTTP'-element
            Element httpNode = XMLTools.appendElement( dcpNode, OWSNS, PRE_OWS+"HTTP" );

            // 'ows:Get'-elements
            OWSRequestMethod[] getRequest = http.getGetRequests();
            for ( int i = 0; i < getRequest.length; i++ ) {
                appendRequest( httpNode, PRE_OWS+"Get", getRequest[i] );
            }

            // 'ows:Post'-elements
            OWSRequestMethod[] postRequest = http.getPostRequests();
            for ( int i = 0; i < postRequest.length; i++ ) {
                appendRequest( httpNode, PRE_OWS+"Post", postRequest[i] );
            }
        }

    }

    /**
     * Appends the DOM representation of an <code>OWSRequestMethod</code> to the passed
     * <code>Element</code>. The given <code>String</code> is used to distinguish between
     * <code>ows:Get</code> and <code>ows:Post</code> requests.
     * 
     * @param httpNode
     * @param type
     * @param request
     */
    private static void appendRequest( Element httpNode, String type, OWSRequestMethod request ) {

        Element owsElement = XMLTools.appendElement( httpNode, OWSNS, type );

        appendSimpleLinkAttributes( owsElement, request.getLink() );

        OWSDomainType110[] constraint = request.getConstraints();
        for ( int i = 0; i < constraint.length; i++ ) {
            appendDomainType( owsElement, constraint[i], PRE_OWS+"Constraint" );
        }

    }

    /**
     * Appends the DOM representation of a <code>Dataset</code> to the passed <code>Element</code>.
     * 
     * @param root
     * @param dataset
     */
    private static void appendDataset( Element root, Dataset dataset ) {

        // 'wpvs:Dataset'-element (parent)
        Element datasetNode = XMLTools.appendElement( root, WPVSNS, "wpvs:Dataset" );

        // attributes
        String bool = dataset.getQueryable() == true ? "1" : "0";
        datasetNode.setAttribute( "queriable", bool );

        bool = dataset.getOpaque() == true ? "1" : "0";
        datasetNode.setAttribute( "opaque", bool );

        bool = dataset.getNoSubset() == true ? "1" : "0";
        datasetNode.setAttribute( "noSubsets", bool );

        datasetNode.setAttribute( "fixedWidth", String.valueOf( dataset.getFixedWidth() ) );
        datasetNode.setAttribute( "fixedHeight", String.valueOf( dataset.getFixedHeight() ) );

        // 'wpvs:Name'-element
        appendName( datasetNode, dataset );

        // 'wpvs:Title'-element
        appendTitle( datasetNode, dataset );

        // 'wpvs:Abstract'-element
        appendAbstract( datasetNode, dataset );

        // 'ows:Keywords'-elements
        appendOWSKeywords( datasetNode, dataset.getKeywords() );

        // 'wpvs:CRS'-elements
        appendCRSNodes( datasetNode, dataset.getCrs() );

        // 'wpvs:Format'-elements
        appendFormats( datasetNode, dataset.getMimeTypeFormat() );

        Envelope wgs84BoundingBox = dataset.getWgs84BoundingBox();
        if ( wgs84BoundingBox != null ) {
            appendBoundingBox( datasetNode, wgs84BoundingBox, PRE_OWS+"WGS84BoundingBox",
                               "urn:ogc:def:crs:OGC:2:84", "2" );
        }

        // 'ows:BoundingBox'-elements
        Envelope[] boundingBoxes = dataset.getBoundingBoxes();

        for ( int i = 0; i < boundingBoxes.length; i++ ) {

            if ( boundingBoxes[i] != null ) {
                String crsName = boundingBoxes[i].getCoordinateSystem().getName();

                appendBoundingBox( datasetNode, boundingBoxes[i], PRE_OWS+"BoundingBox", crsName, "2" );
            }
        }

        // 'wpvs:Dimension'-elements
        appendDimensions( datasetNode, dataset.getDimensions() );

        // 'wpvs:DataProvider'-element

        appendDataProvider( datasetNode, dataset.getDataProvider() );

        // 'wpvs:Identifier'-element
        if ( dataset.getIdentifier() != null ) {
            appendIdentifier( datasetNode, dataset.getIdentifier() );
        }

        // 'wpvs:MetaData'-elements
        appendURLs( datasetNode, dataset.getMetadata(), WPVSNS, "wpvs:MetaData" );

        // 'wpvs:DatasetReference'-elements
        appendURLs( datasetNode, dataset.getDatasetReferences(), WPVSNS, "wpvs:DatasetReference" );

        // 'wpvs:FeatureListReference'-elements
        appendURLs( datasetNode, dataset.getFeatureListReferences(), WPVSNS,
                    "wpvs:FeatureListReference" );

        // 'wpvs:Style'-elements
        appendStyles( datasetNode, dataset.getStyles() );

        // 'wpvs:MinimumScaleDenominator'-element
        appendScaleDenominator( datasetNode, dataset.getMinimumScaleDenominator(), "MIN" );

        // 'wpvs:MaximumScaleDenominator'-element
        appendScaleDenominator( datasetNode, dataset.getMaximumScaleDenominator(), "MAX" );

        // 'wpvs:Dataset'-elements (children)
        Dataset[] datasets = dataset.getDatasets();
        for ( int i = 0; i < datasets.length; i++ ) {
            appendDataset( datasetNode, datasets[i] );
        }

        // 'ElevationModel'-element (the simple ogc-ElevationModel)
        String emName = dataset.getElevationModel().getName();
        if ( emName != null ) {
            appendElevationModel( datasetNode, emName );
        }
    }

    /**
     * Appends the DOM representation of an OGC <code>ElevationModel</code> to the passed
     * <code>Element</code>.
     * 
     * @param datasetNode
     * @param elevationModelName
     */
    private static void appendElevationModel( Element datasetNode, String elevationModelName ) {

        Element elevation = XMLTools.appendElement( datasetNode, WPVSNS, "wpvs:ElevationModel" );
        Text elevationText = elevation.getOwnerDocument().createTextNode( elevationModelName );
        elevation.appendChild( elevationText );

        elevation.appendChild( elevationText );

    }

    /**
     * Appends the DOM representations of the given <code>ScaleDenominator</code> to the passed
     * <code>Element</code>. The given <code>String</code> is used to distinguish between
     * MinimumsScaleDenominator and MaximumScaleDenominator.
     * 
     * @param datasetNode
     * @param scaleDenominator
     * @param extremum
     *            must be either 'MIN' or 'MAX'.
     */
    private static void appendScaleDenominator( Element datasetNode, double scaleDenominator,
                                               String extremum ) {
        Element scaleElement = null;

        if ( "MIN".equalsIgnoreCase( extremum ) ) {
            scaleElement = XMLTools.appendElement( datasetNode, WPVSNS,
                                                   "wpvs:MinimumScaleDenominator" );
        } else if ( "MAX".equalsIgnoreCase( extremum ) ) {
            scaleElement = XMLTools.appendElement( datasetNode, WPVSNS,
                                                   "wpvs:MaximumScaleDenominator" );
        } else {
            throw new IllegalArgumentException( "The extremum must be either 'MIN' or 'MAX'." );
        }

        String value = String.valueOf( scaleDenominator );
        Text scaleText = scaleElement.getOwnerDocument().createTextNode( value );
        scaleElement.appendChild( scaleText );

    }

    /**
     * Appends the DOM representations of the <code>Abstract</code> Element from the given
     * <code>Object</code> to the passed <code>Element</code>.
     * 
     * @param root
     * @param obj
     *            may be of the following types: Style, Dataset.
     */
    private static void appendAbstract( Element root, Object obj ) {

        String abstract_ = null;
        if ( obj instanceof Style ) {
            abstract_ = ( (Style) obj ).getAbstract();
        } else if ( obj instanceof Dataset ) {
            abstract_ = ( (Dataset) obj ).getAbstract();
        }

        Element abstractElement = XMLTools.appendElement( root, WPVSNS, "wpvs:Abstract" );
        Text abstractText = abstractElement.getOwnerDocument().createTextNode( abstract_ );
        abstractElement.appendChild( abstractText );

    }

    /**
     * Appends the DOM representations of the <code>Title</code> Element from the given
     * <code>Object</code> to the passed <code>Element</code>.
     * 
     * @param root
     * @param obj
     *            may be of the following types: Style, Dataset.
     */
    private static void appendTitle( Element root, Object obj ) {

        String title = null;
        if ( obj instanceof Style ) {
            title = ( (Style) obj ).getTitle();
        } else if ( obj instanceof Dataset ) {
            title = ( (Dataset) obj ).getTitle();
        }

        Element titleElement = XMLTools.appendElement( root, WPVSNS, "wpvs:Title" );
        Text titleText = titleElement.getOwnerDocument().createTextNode( title );
        titleElement.appendChild( titleText );
    }

    /**
     * Appends the DOM representations of the <code>Name</code> Element from the given
     * <code>Object</code> to the passed <code>Element</code>.
     * 
     * @param root
     * @param obj
     *            may be of the following types: Style, Dataset.
     */
    private static void appendName( Element root, Object obj ) {

        String name = null;
        if ( obj instanceof Style ) {
            name = ( (Style) obj ).getName();
        } else if ( obj instanceof Dataset ) {
            name = ( (Dataset) obj ).getName();
        }

        if ( name != null ) {
            Element nameElement = XMLTools.appendElement( root, WPVSNS, "wpvs:Name" );
            Text nameText = nameElement.getOwnerDocument().createTextNode( name );
            nameElement.appendChild( nameText );
        }

    }

    /**
     * Appends the DOM representations of the given array of <code>Style</code> to the passed
     * <code>Element</code>.
     * 
     * @param datasetNode
     * @param styles
     */
    private static void appendStyles( Element datasetNode, Style[] styles ) {

        for ( int i = 0; i < styles.length; i++ ) {

            Element styleElement = XMLTools.appendElement( datasetNode, WPVSNS, "wpvs:Style" );

            appendName( styleElement, styles[i] );
            appendTitle( styleElement, styles[i] );
            appendAbstract( styleElement, styles[i] );

            appendOWSKeywords( styleElement, styles[i].getKeywords() );

            if ( styles[i].getIdentifier() != null ) {
                appendIdentifier( styleElement, styles[i].getIdentifier() );
            }

            appendURLs( styleElement, styles[i].getLegendURLs(), WPVSNS, "wpvs:LegendURL" );

            Element styleSheetURLElement = XMLTools.appendElement( styleElement, WPVSNS,
                                                                   "wpvs:StyleSheetURL" );
            appendURL( styleSheetURLElement, styles[i].getStyleSheetURL(), WPVSNS );

            Element styleURLElement = XMLTools.appendElement( styleElement, WPVSNS, "wpvs:StyleURL" );
            appendURL( styleURLElement, styles[i].getStyleURL(), WPVSNS );

        }
    }

    /**
     * Appends the DOM representations of the given array of <code>BaseURL</code> under the given
     * name to the passed <code>Element</code>.
     * 
     * @param root
     * @param baseURL
     * @param uri
     * @param newNode
     */
    private static void appendURLs( Element root, BaseURL[] baseURL, URI uri, String newNode ) {

        for ( int i = 0; i < baseURL.length; i++ ) {
            Element urlElement = XMLTools.appendElement( root, uri, newNode );
            appendURL( urlElement, baseURL[i], uri );
        }

    }

    /**
     * Appends the contents of the given <code>BaseURL</code> within the given <code>URI</code>
     * as DOM representation to the passed URL <code>Element</code>.
     * 
     * @param urlElement
     *            example: logoURLElement
     * @param baseURL
     *            example: dataProvider.getLogoURL()
     * @param uri
     *            example: "WPVSNS"
     */
    private static void appendURL( Element urlElement, BaseURL baseURL, URI uri ) {

        // child elements of urlElement
        Element formatElement = XMLTools.appendElement( urlElement, uri, "wpvs:Format" );
        String format = baseURL != null ? baseURL.getFormat() : "";
        Text formatText = formatElement.getOwnerDocument().createTextNode( format );
        formatElement.appendChild( formatText );

        Element onlineElement = XMLTools.appendElement( urlElement, uri, "wpvs:OnlineResource" );
        String url = baseURL != null ? baseURL.getOnlineResource().toString() : "";
        onlineElement.setAttribute( "xlink:href", url );

        // attributes of urlElement
        if ( baseURL instanceof ImageURL ) {
            String width = String.valueOf( ( (ImageURL) baseURL ).getWidth() );
            String height = String.valueOf( ( (ImageURL) baseURL ).getHeight() );
            urlElement.setAttribute( "width", width );
            urlElement.setAttribute( "height", height );

        } else if ( baseURL instanceof MetaData ) {

            urlElement.setAttribute( "type", ( (MetaData) baseURL ).getType() );
        }

    }

    /**
     * Appends the DOM representation of the given <code>Identifier</code> to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param identifier
     */
    private static void appendIdentifier( Element root, Identifier identifier ) {

        Element idElement = XMLTools.appendElement( root, WPVSNS, "wpvs:Identifier" );

        idElement.setAttribute( "codeSpace", identifier.getCodeSpace() );

        Text idText = idElement.getOwnerDocument().createTextNode( identifier.getValue() );
        idElement.appendChild( idText );

    }

    /**
     * Appends the DOM representation of the given <code>DataProvider</code> to the passed
     * <code>Element</code>.
     * 
     * @param datasetNode
     * @param dataProvider
     */
    private static void appendDataProvider( Element datasetNode, DataProvider dataProvider ) {

        Element provider = XMLTools.appendElement( datasetNode, WPVSNS, "wpvs:DataProvider" );

        String provName = dataProvider.getProviderName();
        if ( provName != null ) {
            Element providerName = XMLTools.appendElement( provider, WPVSNS, "wpvs:ProviderName" );
            Text providerNameText = providerName.getOwnerDocument().createTextNode( provName );
            providerName.appendChild( providerNameText );

        }

        Element providerSite = XMLTools.appendElement( provider, WPVSNS, "wpvs:ProviderSite" );
        URL siteURL = dataProvider.getProviderSite();
        String site = "";
        if ( siteURL != null ) {
            site = siteURL.toString();
        }
        providerSite.setAttribute( "xlink:href", site );

        Element logoURLElement = XMLTools.appendElement( provider, WPVSNS, "wpvs:LogoURL" );
        if ( dataProvider != null ) {
            appendURL( logoURLElement, dataProvider.getLogoURL(), WPVSNS );
        }

    }

    /**
     * Appends the DOM representations of the given array of <code>Dimension</code> to the passed
     * <code>Element</code>.
     * 
     * @param datasetNode
     * @param dimensions
     */
    private static void appendDimensions( Element datasetNode, Dimension[] dimensions ) {

        for ( int i = 0; i < dimensions.length; i++ ) {
            Element dimension = XMLTools.appendElement( datasetNode, WPVSNS, "wpvs:Dimension" );

            dimension.setAttribute( "name", dimensions[i].getName() );
            dimension.setAttribute( "units", dimensions[i].getUnits() );
            dimension.setAttribute( "unitSymbol", dimensions[i].getUnitSymbol() );
            dimension.setAttribute( "default", dimensions[i].getDefault() );

            String multipleValue = dimensions[i].getMultipleValues().toString() == "true" ? "1"
                                                                                         : "0";
            String nearestValue = dimensions[i].getNearestValue().toString() == "true" ? "1" : "0";
            String current = dimensions[i].getCurrent().toString() == "true" ? "1" : "0";

            dimension.setAttribute( "multipleValues", multipleValue );
            dimension.setAttribute( "nearestValue", nearestValue );
            dimension.setAttribute( "current", current );

            Text dimensionText = dimension.getOwnerDocument().createTextNode(
                                                                              dimensions[i].getValue() );
            dimension.appendChild( dimensionText );
        }

    }

    /**
     * Appends the DOM representations of the given array of <code>Format</code> to the passed
     * <code>Element</code>.
     * 
     * @param datasetNode
     * @param mimeTypeFormat
     */
    private static void appendFormats( Element datasetNode, String[] mimeTypeFormat ) {

        if ( mimeTypeFormat != null ) {

            for ( int i = 0; i < mimeTypeFormat.length; i++ ) {
                Element format = XMLTools.appendElement( datasetNode, WPVSNS, "wpvs:Format" );
                Text formatText = format.getOwnerDocument().createTextNode( mimeTypeFormat[i] );
                format.appendChild( formatText );
            }
        }
    }

    /**
     * Appends the DOM representations of the given array of <code>CRS</code> to the passed
     * <code>Element</code>.
     * 
     * @param datasetNode
     * @param crs
     */
    private static void appendCRSNodes( Element datasetNode, CoordinateSystem[] coordinateSystems ) {

        if ( coordinateSystems != null ) {
            for ( CoordinateSystem crs : coordinateSystems ) {
                Element crsElement = XMLTools.appendElement( datasetNode, WPVSNS, "wpvs:CRS" );
                Text crsText = crsElement.getOwnerDocument().createTextNode( crs.getAsString() );
                crsElement.appendChild( crsText );
            }
        }
    }

    /**
     * Appends the DOM representation of the given parameters <code>Envelope, elementName, crsName, 
     * dimension</code>
     * to the passed <code>Element</code>.
     * 
     * elementName should be of the kind ows:WGS84BoundingBox" or "ows:BoundingBox". crsName should
     * be of the kind "urn:ogc:def:crs:OGC:2:84" or "...TODO...". dimension should be "2".
     * 
     * @param root
     * @param envelope
     * @param elementName
     * @param crsName
     * @param dimension
     */
    private static void appendBoundingBox( Element root, Envelope envelope, String elementName,
                                          String crsName, String dimension ) {

        Element boundingBoxElement = XMLTools.appendElement( root, OWSNS, elementName );
        boundingBoxElement.setAttribute( "crs", crsName );
        boundingBoxElement.setAttribute( "dimension", dimension );

        Element lowerCornerElement = XMLTools.appendElement( boundingBoxElement, OWSNS,
                                                             PRE_OWS+"LowerCorner" );
        Text lowerCornerText = lowerCornerElement.getOwnerDocument().createTextNode(
                                                                                     envelope.getMin().getX()
                                                                                                             + " "
                                                                                                             + envelope.getMin().getY() );
        lowerCornerElement.appendChild( lowerCornerText );

        Element upperCornerElement = XMLTools.appendElement( boundingBoxElement, OWSNS,
                                                             PRE_OWS+"UpperCorner" );
        Text upperCornerText = upperCornerElement.getOwnerDocument().createTextNode(
                                                                                     envelope.getMax().getX()
                                                                                                             + " "
                                                                                                             + envelope.getMax().getY() );
        upperCornerElement.appendChild( upperCornerText );

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: XMLFactory.java,v $
 * Changes to this class. What the people have been up to: Revision 1.23  2006/11/27 15:43:50  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.22  2006/11/27 11:31:50  bezema
 * Changes to this class. What the people have been up to: UPdating javadocs and cleaning up
 * Changes to this class. What the people have been up to: Revision 1.21
 * 2006/11/07 16:44:12 poth bug fix - null checking for identifiers
 * 
 * Revision 1.20 2006/08/24 06:42:16 poth File header corrected
 * 
 * Revision 1.19 2006/06/20 10:16:01 taddei clean up and javadoc
 * 
 * Revision 1.18 2006/05/01 20:15:26 poth ** empty log message ***
 * 
 * Revision 1.17 2006/04/06 20:25:30 poth ** empty log message ***
 * 
 * Revision 1.16 2006/04/05 07:21:11 poth ** empty log message ***
 * 
 * Revision 1.15 2006/03/30 21:20:28 poth ** empty log message ***
 * 
 * Revision 1.14 2006/01/18 08:58:00 taddei implementation (WFS)
 * 
 * Revision 1.13 2005/12/21 11:05:13 mays clean up: remove typos, add missing namespaces, add
 * missing attribs
 * 
 * Revision 1.12 2005/12/20 13:43:54 mays clean up
 * 
 * Revision 1.11 2005/12/20 13:12:51 mays appendOWSMetadata only appends if metadata is not null
 * 
 * Revision 1.10 2005/12/20 09:56:52 mays append of OWSMetadata and subsequent elements
 * 
 * Revision 1.9 2005/12/16 15:25:56 mays create new method to enable wpvs specific appending of
 * OperationsMetadata still needs to be implemented
 * 
 * Revision 1.8 2005/12/13 16:28:44 mays implementation of missing appendSomething methods and
 * revision of existing methods.
 * 
 * Revision 1.7 2005/12/13 12:40:18 mays added more appendSomething methods
 * 
 * Revision 1.6 2005/12/06 16:43:21 mays add new appendNode methods
 * 
 * Revision 1.5 2005/12/02 15:24:18 mays commented out some parts that are currently not working
 * 
 * Revision 1.4 2005/12/01 10:30:14 mays add standard footer to all java classes in wpvs package
 * 
 **************************************************************************************************/
