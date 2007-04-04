//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/capabilities/WPVSCapabilitiesDocument.java,v 1.31 2006/11/27 15:40:32 bezema Exp $
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

package org.deegree.ogcwebservices.wpvs.capabilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.ValueRange;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.Protocol;
import org.deegree.owscommon.OWSCommonCapabilitiesDocument;
import org.deegree.owscommon.OWSMetadata;
import org.deegree.owscommon.com110.HTTP110;
import org.deegree.owscommon.com110.OWSAllowedValues;
import org.deegree.owscommon.com110.OWSDomainType110;
import org.deegree.owscommon.com110.OWSRequestMethod;
import org.deegree.owscommon.com110.Operation110;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class represents a <code>WPVSCapabilitiesDocument</code> object.
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.31 $, $Date: 2006/11/27 15:40:32 $
 * 
 */
public class WPVSCapabilitiesDocument extends OWSCommonCapabilitiesDocument {

    /**
     * 
     */
    private static final long serialVersionUID = 2633513531080190745L;

    private static final ILogger LOG = LoggerFactory.getLogger( WPVSCapabilitiesDocument.class );

    private static final String XML_TEMPLATE = "WPVSCapabilitiesTemplate.xml";

    private static String PRE_DWPVS = CommonNamespaces.DEEGREEWPVS_PREFIX + ":";

    /**
     * Creates a skeleton capabilities document that contains the mandatory elements only.
     * 
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument()
                            throws IOException, SAXException {
        URL url = WPVSCapabilitiesDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * @see org.deegree.ogcwebservices.getcapabilities.OGCCapabilitiesDocument#parseCapabilities()
     */
    @Override
    public OGCCapabilities parseCapabilities()
                            throws InvalidCapabilitiesException {
        LOG.entering();

        WPVSCapabilities wpvsCapabilities = null;

        try {
            wpvsCapabilities = new WPVSCapabilities( parseVersion(), parseUpdateSequence(),
                                                     getServiceIdentification(),
                                                     getServiceProvider(),
                                                     parseOperationsMetadata(), null, getDataset() );

        } catch ( XMLParsingException e ) {
            throw new InvalidCapabilitiesException( e.getMessage() + "\n"
                                                    + StringTools.stackTraceToString( e ) );

        } catch ( MissingParameterValueException e ) {
            throw new InvalidCapabilitiesException( e.getMessage() + "\n"
                                                    + StringTools.stackTraceToString( e ) );

        } catch ( InvalidParameterValueException e ) {
            throw new InvalidCapabilitiesException( e.getMessage() + "\n"
                                                    + StringTools.stackTraceToString( e ) );

        } catch ( OGCWebServiceException e ) {
            throw new InvalidCapabilitiesException( e.getMessage() + "\n"
                                                    + StringTools.stackTraceToString( e ) );
        }

        LOG.exiting();
        return wpvsCapabilities;
    }

    /**
     * Gets the <code>Dataset</code> object from the root element of the WPVSCapabilities element.
     * 
     * @return Returns the Dataset object form root element.
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     * @throws InvalidParameterValueException
     * @throws MissingParameterValueException
     */
    private Dataset getDataset()
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException, OGCWebServiceException {

        Element datasetElement = (Element) XMLTools.getRequiredNode( getRootElement(), PRE_DWPVS
                                                                                       + "Dataset",
                                                                     nsContext );
        Dataset dataset = parseDataset( datasetElement, null );

        return dataset;
    }

    /**
     * Creates and returns a new <code>Dataset</code> object from the given <code>Element</code>
     * and the parent <code>Dataset</code> object.
     * 
     * @param datasetElement
     * @param parent
     *            may be null
     * @return Returns a new Dataset object.
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     * @throws InvalidParameterValueException
     * @throws MissingParameterValueException
     */
    private Dataset parseDataset( Element datasetElement, Dataset parent )
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException, OGCWebServiceException {
        LOG.entering();

        // attributes
        boolean queryable = XMLTools.getNodeAsBoolean( datasetElement, "./@queryable", nsContext,
                                                       false );
        boolean opaque = XMLTools.getNodeAsBoolean( datasetElement, "./@opaque", nsContext, false );
        boolean noSubsets = XMLTools.getNodeAsBoolean( datasetElement, "./@noSubsets", nsContext,
                                                       false );
        int fixedWidth = XMLTools.getNodeAsInt( datasetElement, "./@fixedWidth", nsContext, 0 );
        int fixedHeight = XMLTools.getNodeAsInt( datasetElement, "./@fixedHeight", nsContext, 0 );

        // elements
        String name = XMLTools.getNodeAsString( datasetElement, PRE_DWPVS + "Name/text()",
                                                nsContext, null );
        String title = XMLTools.getRequiredNodeAsString( datasetElement,
                                                         PRE_DWPVS + "Title/text()", nsContext );
        String abstract_ = XMLTools.getNodeAsString( datasetElement, PRE_DWPVS + "Abstract/text()",
                                                     nsContext, null );
        Keywords[] keywords = getKeywords( XMLTools.getNodes( datasetElement, "ows:Keywords",
                                                              nsContext ) );
        String[] crsStrings = XMLTools.getNodesAsStrings( datasetElement, PRE_DWPVS + "CRS/text()",
                                                          nsContext );
        
        List<CoordinateSystem> crsList = parseCoordinateSystems( crsStrings );
        
        String[] format = XMLTools.getRequiredNodesAsStrings( datasetElement, PRE_DWPVS
                                                                              + "Format/text()",
                                                              nsContext );
        Envelope wgs84BoundingBox = getBoundingBox( datasetElement, "ows:WGS84BoundingBox" );
        Envelope[] boundingBoxes = getBoundingBoxes( datasetElement, parent );
        Dimension[] dimensions = parseDimensions( datasetElement );
        DataProvider dataProvider = parseDataProvider( datasetElement );
        Identifier identifier = parseIdentifier( datasetElement, PRE_DWPVS + "Identifier" );
        MetaData[] metaData = parseMetaData( datasetElement );
        DatasetReference[] datasetRefs = parseDatasetReferences( datasetElement );
        FeatureListReference[] featureListRefs = parseFeatureListReferences( datasetElement );
        Style[] style = parseStyles( datasetElement );
        double minScaleDenom = XMLTools.getNodeAsDouble(
                                                         datasetElement,
                                                         PRE_DWPVS
                                                                                 + "MinimumScaleDenominator/text()",
                                                         nsContext, 0 );
        double maxScaleDenom = XMLTools.getNodeAsDouble(
                                                         datasetElement,
                                                         PRE_DWPVS
                                                                                 + "MaximumScaleDenominator/text()",
                                                         nsContext, 9E9 );

        if ( minScaleDenom >= maxScaleDenom ) {
            throw new InvalidCapabilitiesException( "MinimumScaleDenominator must be "
                                                    + "less than MaximumScaleDenominator!" );
        }
        ElevationModel elevationModel = parseElevationModel( datasetElement );
        


        // create new root dataset
        Dataset dataset = new Dataset( queryable, opaque, noSubsets, fixedWidth, fixedHeight, name,
                                       title, abstract_, keywords, crsList, format,
                                       wgs84BoundingBox, boundingBoxes, dimensions, dataProvider,
                                       identifier, metaData, datasetRefs, featureListRefs, style,
                                       minScaleDenom, maxScaleDenom, null, elevationModel, null,
                                       parent );

        // get child datasets
        List nl = XMLTools.getNodes( datasetElement, PRE_DWPVS + "Dataset", nsContext );
        Dataset[] childDatasets = new Dataset[nl.size()];
        for ( int i = 0; i < childDatasets.length; i++ ) {
            childDatasets[i] = parseDataset( (Element) nl.get( i ), dataset );
        }

        // set child datasets
        dataset.setDatasets( childDatasets );

        LOG.exiting();
        return dataset;
    }
    
    protected List<CoordinateSystem> parseCoordinateSystems( String[] coordinateStrings ){
        if( coordinateStrings == null ) return new ArrayList<CoordinateSystem>( );
        ArrayList<CoordinateSystem> crsList = new ArrayList<CoordinateSystem>( coordinateStrings.length );
        for ( String tmpCRS : coordinateStrings ) {
            try {
                CoordinateSystem crs = CRSFactory.create( tmpCRS );
                crsList.add( crs );
            } catch ( UnknownCRSException e ) {
                // fail configuration notify the user
                LOG.logError( e.getLocalizedMessage(), e );
            }
        }
        return crsList;
    }

    /**
     * Creates and returns a new <code>ElevationModel</code> object from the given
     * <code>Element</code>.
     * 
     * This OGC ElevationModel contains only a String.
     * 
     * @param datasetElement
     * @return Returns the ElevationModel object.
     * @throws XMLParsingException
     */
    private ElevationModel parseElevationModel( Element datasetElement )
                            throws XMLParsingException {

        String name = XMLTools.getRequiredNodeAsString( datasetElement, PRE_DWPVS
                                                                        + "ElevationModel/text()",
                                                        nsContext );
        ElevationModel elevationModel = new ElevationModel( name );

        return elevationModel;
    }

    /**
     * Creates and returns a new array of <code>Style</code> objects from the given
     * <code>Element</code>.
     * 
     * @param datasetElement
     * @return Returns a new array of Style objects.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    protected Style[] parseStyles( Element datasetElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        List styleList = XMLTools.getNodes( datasetElement, PRE_DWPVS + "Style", nsContext );
        Style[] styles = new Style[styleList.size()];

        for ( int i = 0; i < styles.length; i++ ) {

            Element styleElement = (Element) styleList.get( i );

            String name = XMLTools.getRequiredNodeAsString( styleElement,
                                                            PRE_DWPVS + "Name/text()", nsContext );
            String title = XMLTools.getRequiredNodeAsString( styleElement, PRE_DWPVS
                                                                           + "Title/text()",
                                                             nsContext );
            String abstract_ = XMLTools.getRequiredNodeAsString( styleElement, PRE_DWPVS
                                                                               + "Abstract/text()",
                                                                 nsContext );
            Keywords[] keywords = getKeywords( XMLTools.getNodes( styleElement, "ows:Keywords",
                                                                  nsContext ) );
            Identifier identifier = parseIdentifier( styleElement, PRE_DWPVS + "Identifier" );

            LegendURL[] legendURLs = parseLegendURLs( styleElement );

            StyleSheetURL styleSheetURL = parseStyleSheetURL( styleElement );
            StyleURL styleURL = parseStyleURL( styleElement );

            styles[i] = new Style( name, title, abstract_, keywords, identifier, legendURLs,
                                   styleSheetURL, styleURL );
        }

        return styles;
    }

    /**
     * Creates and returns a new <code>StyleURL</code> object from the given <code>Element</code>.
     * 
     * @param styleElement
     * @return Returns a new StyleURL object.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    private StyleURL parseStyleURL( Element styleElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        Element StyleURLElement = (Element) XMLTools.getNode( styleElement, PRE_DWPVS + "StyleURL",
                                                              nsContext );

        String format = XMLTools.getRequiredNodeAsString( StyleURLElement, PRE_DWPVS
                                                                           + "Format/text()",
                                                          nsContext );

        URI onlineResourceURI = XMLTools.getNodeAsURI( StyleURLElement,
                                                       PRE_DWPVS + "OnlineResource/@xlink:href",
                                                       nsContext, null );
        URL onlineResource;
        try {
            onlineResource = onlineResourceURI.toURL();
        } catch ( MalformedURLException e ) {
            throw new InvalidCapabilitiesException( onlineResourceURI
                                                    + " does not represent a valid URL: "
                                                    + e.getMessage() );
        }

        return new StyleURL( format, onlineResource );
    }

    /**
     * Creates and returns a new <code>StyleSheetURL</code> object from the given
     * <code>Element</code>.
     * 
     * @param styleElement
     * @return Returns a new StyleSheetURL object.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    private StyleSheetURL parseStyleSheetURL( Element styleElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        Element StyleSheetURLElement = (Element) XMLTools.getNode( styleElement, PRE_DWPVS
                                                                                 + "StyleSheetURL",
                                                                   nsContext );

        String format = XMLTools.getRequiredNodeAsString( StyleSheetURLElement, PRE_DWPVS
                                                                                + "Format/text()",
                                                          nsContext );

        URI onlineResourceURI = XMLTools.getNodeAsURI( StyleSheetURLElement,
                                                       PRE_DWPVS + "OnlineResource/@xlink:href",
                                                       nsContext, null );
        URL onlineResource;
        try {
            onlineResource = onlineResourceURI.toURL();
        } catch ( MalformedURLException e ) {
            throw new InvalidCapabilitiesException( onlineResourceURI
                                                    + " does not represent a valid URL: "
                                                    + e.getMessage() );
        }

        return new StyleSheetURL( format, onlineResource );
    }

    /**
     * Creates and returns a new array of <code>LegendURL</code> objects from the given
     * <code>Element</code>.
     * 
     * @param styleElement
     * @return Returns a new array of LegendURL objects.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    private LegendURL[] parseLegendURLs( Element styleElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        List legendList = XMLTools.getNodes( styleElement, PRE_DWPVS + "LegendURL", nsContext );
        LegendURL[] legendURLs = new LegendURL[legendList.size()];

        for ( int i = 0; i < legendURLs.length; i++ ) {

            Element legendURLElement = (Element) legendList.get( i );

            int width = XMLTools.getRequiredNodeAsInt( legendURLElement, "./@width", nsContext );
            int height = XMLTools.getRequiredNodeAsInt( legendURLElement, "./@height", nsContext );
            if ( width < 0 || height < 0 ) {
                throw new InvalidCapabilitiesException( "The attributes width and height of '"
                                                        + legendURLElement.getNodeName()
                                                        + "' must be positive!" );
            }

            String format = XMLTools.getRequiredNodeAsString( legendURLElement, PRE_DWPVS
                                                                                + "Format/text()",
                                                              nsContext );
            URI onlineResourceURI = XMLTools.getNodeAsURI(
                                                           legendURLElement,
                                                           PRE_DWPVS + "OnlineResource/@xlink:href",
                                                           nsContext, null );
            URL onlineResource;
            try {
                onlineResource = onlineResourceURI.toURL();
            } catch ( MalformedURLException e ) {
                throw new InvalidCapabilitiesException( onlineResourceURI
                                                        + " does not represent a valid URL: "
                                                        + e.getMessage() );
            }

            legendURLs[i] = new LegendURL( width, height, format, onlineResource );
        }
        return legendURLs;
    }

    /**
     * Creates and returns a new array of <code>FeatureListReference</code> objects from the given
     * <code>Element</code>.
     * 
     * @param datasetElement
     * @return Returns a new array of FeatureListReference objects.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    protected FeatureListReference[] parseFeatureListReferences( Element datasetElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        List featureList = XMLTools.getNodes( datasetElement, PRE_DWPVS + "FeatureListReference",
                                              nsContext );
        FeatureListReference[] featureRefs = new FeatureListReference[featureList.size()];

        for ( int i = 0; i < featureRefs.length; i++ ) {

            Element featureRefElement = (Element) featureList.get( i );

            String format = XMLTools.getRequiredNodeAsString( featureRefElement, PRE_DWPVS
                                                                                 + "Format/text()",
                                                              nsContext );

            URI onlineResourceURI = XMLTools.getNodeAsURI(
                                                           featureRefElement,
                                                           PRE_DWPVS + "OnlineResource/@xlink:href",
                                                           nsContext, null );
            URL onlineResource;
            try {
                onlineResource = onlineResourceURI.toURL();
            } catch ( MalformedURLException e ) {
                throw new InvalidCapabilitiesException( onlineResourceURI
                                                        + " does not represent a valid URL: "
                                                        + e.getMessage() );
            }

            featureRefs[i] = new FeatureListReference( format, onlineResource );
        }

        return featureRefs;
    }

    /**
     * Creates and returns a new array of <code>DatasetReference</code> objects from the given
     * <code>Element</code>.
     * 
     * @param datasetElement
     * @return Returns a new array of DatasetReference objects.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    protected DatasetReference[] parseDatasetReferences( Element datasetElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        List datasetRefList = XMLTools.getNodes( datasetElement, PRE_DWPVS + "DatasetReference",
                                                 nsContext );
        DatasetReference[] datasetRefs = new DatasetReference[datasetRefList.size()];

        for ( int i = 0; i < datasetRefs.length; i++ ) {

            Element datasetRefElement = (Element) datasetRefList.get( i );

            String format = XMLTools.getRequiredNodeAsString( datasetRefElement, PRE_DWPVS
                                                                                 + "Format/text()",
                                                              nsContext );

            URI onlineResourceURI = XMLTools.getNodeAsURI(
                                                           datasetRefElement,
                                                           PRE_DWPVS + "OnlineResource/@xlink:href",
                                                           nsContext, null );
            URL onlineResource;
            try {
                onlineResource = onlineResourceURI.toURL();
            } catch ( MalformedURLException e ) {
                throw new InvalidCapabilitiesException( onlineResourceURI
                                                        + " does not represent a valid URL: "
                                                        + e.getMessage() );
            }

            datasetRefs[i] = new DatasetReference( format, onlineResource );
        }

        return datasetRefs;
    }

    /**
     * Creates and returns a new <code>MetaData</code> object from the given <code>Element</code>.
     * 
     * @param datasetElement
     * @return Returns a new MetaData object.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    protected MetaData[] parseMetaData( Element datasetElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        List metaDataList = XMLTools.getNodes( datasetElement, PRE_DWPVS + "MetaData", nsContext );
        MetaData[] metaData = new MetaData[metaDataList.size()];

        for ( int i = 0; i < metaData.length; i++ ) {

            Element metaDataElement = (Element) metaDataList.get( i );

            String type = XMLTools.getRequiredNodeAsString( metaDataElement, "./@type", nsContext );

            String format = XMLTools.getRequiredNodeAsString( metaDataElement, PRE_DWPVS
                                                                               + "Format/text()",
                                                              nsContext );
            URI onlineResourceURI = XMLTools.getNodeAsURI(
                                                           metaDataElement,
                                                           PRE_DWPVS + "OnlineResource/@xlink:href",
                                                           nsContext, null );
            URL onlineResource;
            try {
                onlineResource = onlineResourceURI.toURL();
            } catch ( MalformedURLException e ) {
                throw new InvalidCapabilitiesException( onlineResourceURI
                                                        + " does not represent a valid URL: "
                                                        + e.getMessage() );
            }

            metaData[i] = new MetaData( type, format, onlineResource );
        }

        return metaData;
    }

    /**
     * Creates and returns a new <code>Identifier</code> object from the given
     * <code>Element</code> and the given <cod>xPathQuery</code>.
     * 
     * @param element
     * @param xPathQuery
     * @return Returns a new Identifier object.
     * @throws XMLParsingException
     */
    protected Identifier parseIdentifier( Element element, String xPathQuery )
                            throws XMLParsingException {

        Element identifierElement = (Element) XMLTools.getNode( element, xPathQuery, nsContext );

        Identifier id = null;
        if ( identifierElement != null ) {
            String value = XMLTools.getNodeAsString( identifierElement, "./text()", nsContext, null );
            String codeSpace = XMLTools.getNodeAsString( identifierElement, "./@codeSpace",
                                                         nsContext, null );

            id = new Identifier( value, codeSpace );
        }
        return id;
    }

    /**
     * Creates and returns a new <code>DataProvider</code> object from the given
     * <code>Element</code>.
     * 
     * @param datasetElement
     * @return Returns a new DataProvider object.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    protected DataProvider parseDataProvider( Element datasetElement )
                            throws XMLParsingException, InvalidCapabilitiesException {

        String providerName = null;
        URL providerSite = null;
        LogoURL logoURL = null;

        Element dataProviderElement = (Element) XMLTools.getNode( datasetElement, PRE_DWPVS
                                                                                  + "DataProvider",
                                                                  nsContext );

        if ( dataProviderElement != null ) {

            providerName = XMLTools.getNodeAsString( dataProviderElement, PRE_DWPVS
                                                                          + "ProviderName/text()",
                                                     nsContext, null );
            URI providerSiteURI = XMLTools.getNodeAsURI( dataProviderElement,
                                                         PRE_DWPVS + "ProviderSite/@xlink:href",
                                                         nsContext, null );
            try {
                providerSite = providerSiteURI.toURL();
            } catch ( MalformedURLException e ) {
                throw new InvalidCapabilitiesException( providerSiteURI
                                                        + " does not represent a valid URL: "
                                                        + e.getMessage() );
            }

            Element logoURLElement = (Element) XMLTools.getNode( dataProviderElement, PRE_DWPVS
                                                                                      + "LogoURL",
                                                                 nsContext );

            if ( logoURLElement != null ) {

                int width = XMLTools.getRequiredNodeAsInt( logoURLElement, "./@width", nsContext );
                int height = XMLTools.getRequiredNodeAsInt( logoURLElement, "./@height", nsContext );
                if ( width < 0 || height < 0 ) {
                    throw new InvalidCapabilitiesException( "width and height of '"
                                                            + logoURLElement
                                                            + "' must be positive!" );
                }

                String format = XMLTools.getRequiredNodeAsString( logoURLElement,
                                                                  PRE_DWPVS + "Format/text()",
                                                                  nsContext );

                URI onlineResourceURI = XMLTools.getNodeAsURI(
                                                               logoURLElement,
                                                               PRE_DWPVS
                                                                                       + "OnlineResource/@xlink:href",
                                                               nsContext, null );
                URL onlineResource;
                try {
                    onlineResource = onlineResourceURI.toURL();
                } catch ( MalformedURLException e ) {
                    throw new InvalidCapabilitiesException( onlineResourceURI
                                                            + " does not represent a valid URL: "
                                                            + e.getMessage() );
                }

                logoURL = new LogoURL( width, height, format, onlineResource );
            }
        }

        return new DataProvider( providerName, providerSite, logoURL );
    }

    /**
     * TODO adapted copy from WMSCapabilitiesDocument#parseDimensions(). move to common class ?!
     * changed some object types and added more attribs.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    protected Dimension[] parseDimensions( Element element )
                            throws XMLParsingException {
        LOG.entering();

        List nl = XMLTools.getNodes( element, PRE_DWPVS + "Dimension", nsContext );
        Dimension[] dimensions = new Dimension[nl.size()];

        for ( int i = 0; i < dimensions.length; i++ ) {

            String name = XMLTools.getRequiredNodeAsString( (Node) nl.get( i ), "./@name",
                                                            nsContext );
            String units = XMLTools.getRequiredNodeAsString( (Node) nl.get( i ), "./@units",
                                                             nsContext );
            String unitSymbol = XMLTools.getNodeAsString( (Node) nl.get( i ), "./@unitSymbol",
                                                          nsContext, null );
            String default_ = XMLTools.getNodeAsString( (Node) nl.get( i ), "./@default",
                                                        nsContext, null );
            Boolean multipleValues = Boolean.valueOf( XMLTools.getNodeAsBoolean(
                                                                                 (Node) nl.get( i ),
                                                                                 "./@multipleValues",
                                                                                 nsContext, true ) );
            Boolean nearestValues = Boolean.valueOf( XMLTools.getNodeAsBoolean( (Node) nl.get( i ),
                                                                                "./@nearestValues",
                                                                                nsContext, true ) );
            Boolean current = Boolean.valueOf( XMLTools.getNodeAsBoolean( (Node) nl.get( i ),
                                                                          "./@current", nsContext,
                                                                          true ) );
            String value = XMLTools.getNodeAsString( (Node) nl.get( i ), ".", nsContext, null );

            dimensions[i] = new Dimension( name, units, unitSymbol, default_, multipleValues,
                                           nearestValues, current, value );
        }

        LOG.exiting();
        return dimensions;
    }

    /**
     * Gets an array of <code>boundingBoxes</code> from the given <code>Element</code>. This
     * method returns all boundingBoxes together in one array.
     * 
     * @param element
     * @param parent
     * @return Returns an array of boundingBoxes.
     * @throws XMLParsingException
     * @throws InvalidParameterValueException
     */
    protected Envelope[] getBoundingBoxes( Element element, Dataset parent )
                            throws XMLParsingException, InvalidParameterValueException {

        List boundingBoxList = XMLTools.getNodes( element, "ows:BoundingBox", nsContext );

        List bboxesList = new ArrayList( boundingBoxList.size() );

        for ( int i = 0; i < boundingBoxList.size(); i++ ) {
            bboxesList.add( parseBoundingBox( (Element) boundingBoxList.get( i ) ) );
        }

        if ( parent != null ) {
            Envelope[] boundingBoxes = parent.getBoundingBoxes();
            for ( int i = 0; i < boundingBoxes.length; i++ ) {
                bboxesList.add( boundingBoxes[i] );
            }
        }

        Envelope[] boxes = (Envelope[]) bboxesList.toArray( new Envelope[bboxesList.size()] );
        return boxes;
    }

    /**
     * Gets a single <code>boundingBox</code> from the given <code>Element</code> at the given
     * <code>XPathQuery</code>.
     * 
     * @param datasetElement
     * @param xPathQuery
     * @return Returns a single boundingBox.
     * @throws XMLParsingException
     * @throws InvalidParameterValueException
     */
    protected Envelope getBoundingBox( Element datasetElement, String xPathQuery )
                            throws XMLParsingException, InvalidParameterValueException {

        Envelope boundingBox = null;

        Element boundingBoxElement = (Element) XMLTools.getNode( datasetElement, xPathQuery,
                                                                 nsContext );

        if ( boundingBoxElement != null ) {
            boundingBox = parseBoundingBox( boundingBoxElement );

        }
        return boundingBox;
    }

    /**
     * TODO move to a common mehtod/class: method body copied from
     * OWSCommonCapabilitiesDocument#getWGS84BoundingBoxType()
     * 
     * Changed name, because it is usable with any BoundingBox. Changed crs from null to given
     * attribute value of crs. Added check for min values to be smaler than max values.
     * 
     * Creates an <code>Envelope</code> object from the given element of type
     * <code>ows:WGS84BoundingBox</code> or <code>ows:BoundingBox</code>.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws InvalidParameterValueException
     */
    private Envelope parseBoundingBox( Element element )
                            throws XMLParsingException, InvalidParameterValueException {

        Envelope env = getWGS84BoundingBoxType( element );

        Position min = env.getMin();
        Position max = env.getMax();

        if ( min.getX() >= max.getX() ) {
            throw new InvalidParameterValueException( "X value of LowerCorner must be smaler "
                                                      + "than X value of UpperCorner." );
        }
        if ( min.getY() >= max.getY() ) {
            throw new InvalidParameterValueException( "Y value of LowerCorner must be smaler "
                                                      + "than Y value of UpperCorner." );
        }

        String crsAtt = XMLTools.getAttrValue( element, "crs" );
        CoordinateSystem crs;
        try {
            crs = CRSFactory.create( crsAtt );
        } catch ( UnknownCRSException e ) {
            throw new InvalidParameterValueException( e.getMessage() );
        }

        return GeometryFactory.createEnvelope( min.getX(), min.getY(), max.getX(), max.getY(), crs );

    }

    /**
     * Creates and returns a new <code>OperationsMetadata</code> object.
     * 
     * @return Returns a new OperationsMetadata object.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    protected OperationsMetadata parseOperationsMetadata()
                            throws XMLParsingException, InvalidCapabilitiesException {

        LOG.entering();

        Node operationMetadata = XMLTools.getRequiredNode( getRootElement(),
                                                           "./ows:OperationsMetadata", nsContext );
        List operationElementList = XMLTools.getNodes( operationMetadata, "./ows:Operation",
                                                       nsContext );

        Map operations = new HashMap();
        for ( int i = 0; i < operationElementList.size(); i++ ) {
            operations.put( XMLTools.getRequiredNodeAsString( (Node) operationElementList.get( i ),
                                                              "./@name", nsContext ),
                            operationElementList.get( i ) );
        }

        Operation110 getCapabilities = getOperation110( OperationsMetadata.GET_CAPABILITIES_NAME,
                                                        true, operations );
        Operation110 getView = getOperation110( WPVSOperationsMetadata.GET_VIEW_NAME, true,
                                                operations );
        Operation110 getDescription = getOperation110( WPVSOperationsMetadata.GET_DESCRIPTION_NAME,
                                                       false, operations );
        Operation110 getLegendGraphics = getOperation110(
                                                          WPVSOperationsMetadata.GET_LEGEND_GRAPHIC_NAME,
                                                          false, operations );

        List parameterElementList = XMLTools.getNodes( operationMetadata, "./ows:Parameter",
                                                       nsContext );
        OWSDomainType110[] parameters = new OWSDomainType110[parameterElementList.size()];
        for ( int i = 0; i < parameters.length; i++ ) {
            parameters[i] = getOWSDomainType110( (Element) parameterElementList.get( i ) );
        }

        List constraintElementList = XMLTools.getNodes( operationMetadata, "./ows:Constraint",
                                                        nsContext );
        OWSDomainType110[] constraints = new OWSDomainType110[constraintElementList.size()];
        for ( int i = 0; i < constraints.length; i++ ) {
            constraints[i] = getOWSDomainType110( (Element) constraintElementList.get( i ) );
        }

        List extendedCapsList = XMLTools.getNodes( operationMetadata, "./ows:ExtendedCapabilities",
                                                   nsContext );
        Object[] extendedCapabilities = new Object[extendedCapsList.size()];
        for ( int i = 0; i < extendedCapabilities.length; i++ ) {
            extendedCapabilities[i] = extendedCapsList.get( i );
        }

        WPVSOperationsMetadata metadata = new WPVSOperationsMetadata( getCapabilities, getView,
                                                                      getDescription,
                                                                      getLegendGraphics,
                                                                      parameters, constraints,
                                                                      extendedCapabilities );

        LOG.exiting();
        return metadata;
    }

    /**
     * FIXME needs to be handled, when OWSDomainType110 ceases to exist.
     * 
     * @see org.deegree.owscommon.OWSCommonCapabilitiesDocument#getOperation()
     * 
     * @param name
     * @param isMandatory
     * @param operations
     * @return
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    private Operation110 getOperation110( String name, boolean isMandatory, Map operations )
                            throws XMLParsingException, InvalidCapabilitiesException {

        LOG.entering();
        Operation110 operation = null;
        Element operationElement = (Element) operations.get( name );
        if ( operationElement == null ) {
            if ( isMandatory ) {
                throw new XMLParsingException( "Mandatory operation '" + name + "' not defined in "
                                               + "'OperationsMetadata'-section." );
            }
        } else {
            // 'ows:DCP' - elements
            DCPType[] dcps = getDCPs( XMLTools.getRequiredNodes( operationElement, "ows:DCP",
                                                                 nsContext ) );

            // 'Parameter' - elements
            List parameterList = XMLTools.getNodes( operationElement, "./ows:Parameter", nsContext );
            OWSDomainType110[] parameters = new OWSDomainType110[parameterList.size()];
            for ( int i = 0; i < parameters.length; i++ ) {
                parameters[i] = getOWSDomainType110( (Element) parameterList.get( i ) );
            }
            // 'Constraint' - elements
            List constraintList = XMLTools.getNodes( operationElement, "./ows:Constraint",
                                                     nsContext );
            OWSDomainType110[] constraints = new OWSDomainType110[constraintList.size()];
            for ( int i = 0; i < constraintList.size(); i++ ) {
                constraints[i] = getOWSDomainType110( (Element) constraintList.get( i ) );
            }
            // 'ows:Metadata' - element
            List metadataList = XMLTools.getNodes( operationElement, "./ows:Metadata", nsContext );
            OWSMetadata[] metadata = new OWSMetadata[metadataList.size()];
            for ( int i = 0; i < metadata.length; i++ ) {
                metadata[i] = getOWSMetadata( operationElement, "ows:Metadata", nsContext );
            }

            // return new Operation110 object
            operation = new Operation110( name, dcps, parameters, constraints, metadata );
        }

        LOG.exiting();
        return operation;
    }

    /**
     * FIXME there is a similar method in
     * org.deegree.owscommon.OWSCommonCapabilitiesDocument#getDCP. overrides that method!
     * 
     * Creates a <code>DCPType</code> object from the passed <code>DCP</code> element.
     * 
     * @param element
     * @param namespaceURI
     * @return created <code>DCPType</code>
     * @throws XMLParsingException
     * @see org.deegree.ogcwebservices.getcapabilities.OGCStandardCapabilities
     */
    protected DCPType getDCP( Element element )
                            throws XMLParsingException {
        LOG.entering();

        DCPType dcpType = null;
        Element httpElement = (Element) XMLTools.getRequiredNode( element, "./ows:HTTP", nsContext );

        try {
            List requestList = XMLTools.getNodes( httpElement, "./ows:Get", nsContext );
            OWSRequestMethod[] getRequests = new OWSRequestMethod[requestList.size()];
            for ( int i = 0; i < getRequests.length; i++ ) {

                List constraintList = XMLTools.getNodes( (Node) requestList.get( i ),
                                                         "./ows:Constraint", nsContext );
                OWSDomainType110[] constraint = new OWSDomainType110[constraintList.size()];
                for ( int j = 0; j < constraint.length; j++ ) {
                    constraint[j] = getOWSDomainType110( (Element) constraintList.get( i ) );
                }

                SimpleLink link = parseSimpleLink( (Element) requestList.get( i ) );

                getRequests[i] = new OWSRequestMethod( link, constraint );
            }

            requestList = XMLTools.getNodes( httpElement, "./ows:Post", nsContext );
            OWSRequestMethod[] postRequests = new OWSRequestMethod[requestList.size()];
            for ( int i = 0; i < postRequests.length; i++ ) {

                List constraintList = XMLTools.getNodes( (Node) requestList.get( i ),
                                                         "./ows:Constraint", nsContext );
                OWSDomainType110[] constraint = new OWSDomainType110[constraintList.size()];
                for ( int j = 0; j < constraint.length; j++ ) {
                    constraint[j] = getOWSDomainType110( (Element) constraintList.get( i ) );
                }

                SimpleLink link = parseSimpleLink( (Element) requestList.get( i ) );

                postRequests[i] = new OWSRequestMethod( link, constraint );
            }

            Protocol protocol = new HTTP110( getRequests, postRequests );
            dcpType = new DCPType( protocol );

        } catch ( InvalidCapabilitiesException e ) {
            throw new XMLParsingException( "Couldn't parse the OWSDomainType110 within DCPType: "
                                           + StringTools.stackTraceToString( e ) );
        }

        LOG.exiting();
        return dcpType;
    }

    /**
     * FIXME needs to be handled, when OWSDomainType110 ceases to exist.
     * 
     * @see org.deegree.owscommon.OWSCommonCapabilitiesDocument#getOWSDomainType()
     * 
     * @param element
     * @return Returns owsDomainType110 object.
     * @throws InvalidCapabilitiesException
     */
    private OWSDomainType110 getOWSDomainType110( Element element )
                            throws XMLParsingException, InvalidCapabilitiesException {

        LOG.entering();

        // 'name' - attribute
        String name = XMLTools.getRequiredNodeAsString( element, "@name", nsContext );

        // 'ows:AllowedValues' - element
        Element allowedElement = (Element) XMLTools.getNode( element, "./ows:AllowedValues",
                                                             nsContext );
        OWSAllowedValues allowedValues = null;
        if ( allowedElement != null ) {

            // 'ows:Value' - elements
            String[] values = XMLTools.getNodesAsStrings( allowedElement, "./ows:Value/text()",
                                                          nsContext );
            TypedLiteral[] literals = null;
            if ( values != null ) {
                literals = new TypedLiteral[values.length];
                for ( int i = 0; i < literals.length; i++ ) {
                    literals[i] = new TypedLiteral( values[i], null );
                }
            }

            // 'ows:Range' - elements
            List rangeList = XMLTools.getNodes( allowedElement, "./ows:Range", nsContext );
            ValueRange[] ranges = new ValueRange[rangeList.size()];
            for ( int i = 0; i < ranges.length; i++ ) {
                String minimum = XMLTools.getNodeAsString( (Node) rangeList.get( i ),
                                                           "./ows:MinimumValue", nsContext, null );
                String maximum = XMLTools.getNodeAsString( (Node) rangeList.get( i ),
                                                           "./ows:MaximumValue", nsContext, null );
                String spacing = XMLTools.getNodeAsString( (Node) rangeList.get( i ),
                                                           "./ows:Spacing", nsContext, null );
                TypedLiteral min = new TypedLiteral( minimum, null );
                TypedLiteral max = new TypedLiteral( maximum, null );
                TypedLiteral space = new TypedLiteral( spacing, null );

                ranges[i] = new ValueRange( min, max, space );
            }

            if ( values.length < 1 && ranges.length < 1 ) {
                throw new XMLParsingException(
                                               "At least one 'ows:Value'-element or one 'ows:Range'-element must be defined "
                                                                       + "in each element of type 'ows:AllowedValues'." );
            }

            allowedValues = new OWSAllowedValues( literals, ranges );
        }

        // FIXME manage elements: ows:AnyValue, ows:NoValues.
        boolean anyValue = false;
        boolean noValues = false;

        // 'ows:ValuesListReference' - element
        OWSMetadata valuesListReference = getOWSMetadata( element, "./ows:ValuesListReference",
                                                          nsContext );

        // 'ows:DefaulValue' - element
        String defaultValue = XMLTools.getNodeAsString( element, "./ows:DefaultValue/text()",
                                                        nsContext, null );

        // 'ows:Meaning' - element
        OWSMetadata meaning = getOWSMetadata( element, "./ows:Meaning", nsContext );

        // 'ows:DataType - element
        OWSMetadata dataType = getOWSMetadata( element, "./ows:DataType", nsContext );

        // choose up to one measurement element
        String measurementType = null;
        // 'ows:ReferenceSystem' - element
        Element referenceElement = (Element) XMLTools.getNode( element, "./ows:ReferenceSystem",
                                                               nsContext );
        // 'ows:UOM' - element
        Element uomElement = (Element) XMLTools.getNode( element, "./ows:UOM", nsContext );
        OWSMetadata measurement = null;

        if ( referenceElement != null && uomElement != null ) {
            throw new InvalidCapabilitiesException( "Within an 'ows:DomainType'-Element only one "
                                                    + "of the following elements is allowed: "
                                                    + "'ows:ReferenceSystem' OR 'ows:UOM'." );
        } else if ( referenceElement != null ) {
            measurementType = OWSDomainType110.REFERENCE_SYSTEM;
            measurement = getOWSMetadata( element, "./ows:ReferenceSystem", nsContext );
        } else if ( uomElement != null ) {
            measurementType = OWSDomainType110.UOM;
            measurement = getOWSMetadata( element, "./ows:UOM", nsContext );
        }

        // 'ows:Metadata' - elements
        List metaList = XMLTools.getNodes( element, "./ows:Metadata", nsContext );
        OWSMetadata[] metadata = new OWSMetadata[metaList.size()];
        for ( int i = 0; i < metadata.length; i++ ) {
            metadata[i] = getOWSMetadata( (Element) metaList.get( i ), "./ows:Metadata", nsContext );
        }

        // return new OWSDomainType110
        OWSDomainType110 domainType110 = null;
        if ( allowedValues != null && !anyValue && !noValues && valuesListReference == null ) {
            domainType110 = new OWSDomainType110( allowedValues, defaultValue, meaning, dataType,
                                                  measurementType, measurement, metadata, name );
        } else if ( ( anyValue || noValues ) && allowedValues == null
                    && valuesListReference == null ) {
            domainType110 = new OWSDomainType110( anyValue, noValues, defaultValue, meaning,
                                                  dataType, measurementType, measurement, metadata,
                                                  name );
        } else if ( valuesListReference != null && allowedValues == null && !anyValue && !noValues ) {
            domainType110 = new OWSDomainType110( valuesListReference, defaultValue, meaning,
                                                  dataType, measurementType, measurement, metadata,
                                                  name );
        } else {
            throw new InvalidCapabilitiesException(
                                                    "Only one of the following elements may be "
                                                                            + "contained within an 'ows:DomainType': 'ows:AllowedValues', 'ows:AnyValue', "
                                                                            + "'ows:NoValues' or 'ows:ValuesListReference'." );
        }

        LOG.exiting();
        return domainType110;
    }

    /**
     * FIXME check, wether the URIs go to the correct address within OWSMetadata. So far, no example
     * was given to check this with.
     * 
     * Creates and returns a new <code>OWSMetadata</code> object (or null) from the given
     * <code>Element</code> at the given <code>XPath</code>.
     * 
     * @param element
     * @param xPath
     * @param nsContext
     * @return Returns a new OWSMetadata object (may be null).
     * @throws XMLParsingException
     */
    private OWSMetadata getOWSMetadata( Element element, String xPath, NamespaceContext nsContext )
                            throws XMLParsingException {

        Element child = (Element) XMLTools.getNode( element, xPath, nsContext );

        if ( child == null ) {
            return null;
        }

        // attrib about
        URI about = XMLTools.getNodeAsURI( child, "./@about", nsContext, null );

        // attribs for SimpleLink
        URI href = XMLTools.getNodeAsURI( child, "./@xlink:href", nsContext, null );
        URI role = XMLTools.getNodeAsURI( child, "./@xlink:role", nsContext, null );
        URI arcrole = XMLTools.getNodeAsURI( child, "./@xlink:arcrole", nsContext, null );
        String title = XMLTools.getNodeAsString( child, "./@xlink:title", nsContext, null );
        String show = XMLTools.getNodeAsString( child, "./@xlink:show", nsContext, null );
        String actuate = XMLTools.getNodeAsString( child, "./@xlink:actuate", nsContext, null );

        // ows:name (ows:AbstractMetaData)
        String name = XMLTools.getNodeAsString( child, "./text()", nsContext, null );

        SimpleLink link = new SimpleLink( href, role, arcrole, title, show, actuate );

        return new OWSMetadata( about, link, name );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WPVSCapabilitiesDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.31  2006/11/27 15:40:32  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling and the generics
 * Changes to this class. What the people have been up to:
 * Revision 1.30 2006/11/27 09:07:53 poth JNI integration of proj4 has been removed. The CRS
 * functionality now will be done by native deegree code.
 * 
 * Revision 1.29 2006/11/23 11:46:40 bezema The initial version of the new wpvs
 * 
 * Revision 1.28 2006/11/07 16:34:02 poth bug fixes and code formatting
 * 
 * Revision 1.27 2006/08/24 06:42:15 poth File header corrected
 * 
 * Revision 1.26 2006/05/01 20:15:27 poth ** empty log message ***
 * 
 * Revision 1.25 2006/04/06 20:25:25 poth ** empty log message ***
 * 
 * Revision 1.24 2006/03/30 21:20:26 poth ** empty log message ***
 * 
 * Revision 1.23 2006/01/18 08:47:54 taddei bug fixes
 * 
 * Revision 1.22 2005/12/21 11:08:20 mays clean up: remove typos, remove wgs84bboxes from bboxes
 * array
 * 
 * Revision 1.21 2005/12/20 13:14:52 mays clean up and addition of minor missing parts
 * 
 * Revision 1.20 2005/12/20 10:08:46 mays remove typo
 * 
 * Revision 1.19 2005/12/20 09:59:41 mays implement ValueRange in getOWSDomainType110 and implement
 * SimpleLink in getOWSMetadata
 * 
 * Revision 1.18 2005/12/19 10:05:23 mays changes for call of Operation110 constructor in
 * getOperation110
 * 
 * Revision 1.17 2005/12/16 15:29:38 mays necessary changes due to new definition of
 * ows:OperationsMetadata
 * 
 * Revision 1.16 2005/12/13 16:53:10 mays change parseOperationsMetadata to 'protected'
 * 
 * Revision 1.15 2005/12/12 10:25:51 mays removed typos
 * 
 * Revision 1.14 2005/12/09 14:07:56 mays add parseOperationsMetadata and clean up
 * 
 * Revision 1.13 2005/12/08 16:46:03 mays change method names to parseSomeThing; move configuration
 * specific stuff to WPVSConfigurationDocument and leave only capabilties specifics in here
 * 
 * Revision 1.12 2005/12/07 09:45:14 mays redesign of filterCondition request from String to Map
 * form wcs and wms datasources
 * 
 * Revision 1.11 2005/12/06 16:45:21 mays necessary changes for AbstractDataSources: use Query for
 * WFSDataSources
 * 
 * Revision 1.10 2005/12/06 12:57:07 mays add create-methods for new subnodes of dataset; remove
 * errors in existing methods
 * 
 * Revision 1.9 2005/12/05 09:36:38 mays revision of comments
 * 
 * Revision 1.8 2005/12/02 15:28:55 mays adaptations according to schema specifications, mainly
 * concerning the number of item occurences
 * 
 * Revision 1.7 2005/12/01 16:50:40 mays add some more subnodes to dataset (not finished yet), add
 * create-methods for new subnodes
 * 
 * Revision 1.6 2005/12/01 10:30:14 mays add standard footer to all java classes in wpvs package
 * 
 **************************************************************************************************/
