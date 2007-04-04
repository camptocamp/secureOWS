//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/configuration/WPVSConfigurationDocument.java,v 1.40 2006/11/28 18:09:28 mschneider Exp $
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

package org.deegree.ogcwebservices.wpvs.configuration;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wpvs.capabilities.DataProvider;
import org.deegree.ogcwebservices.wpvs.capabilities.Dataset;
import org.deegree.ogcwebservices.wpvs.capabilities.DatasetReference;
import org.deegree.ogcwebservices.wpvs.capabilities.Dimension;
import org.deegree.ogcwebservices.wpvs.capabilities.ElevationModel;
import org.deegree.ogcwebservices.wpvs.capabilities.FeatureListReference;
import org.deegree.ogcwebservices.wpvs.capabilities.Identifier;
import org.deegree.ogcwebservices.wpvs.capabilities.MetaData;
import org.deegree.ogcwebservices.wpvs.capabilities.OWSCapabilities;
import org.deegree.ogcwebservices.wpvs.capabilities.Style;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSCapabilitiesDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Parser for WPVS configuration documents.
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.40 $, $Date: 2006/11/28 18:09:28 $
 */
public class WPVSConfigurationDocument extends WPVSCapabilitiesDocument {

    private static final long serialVersionUID = 1511898601495679163L;

    private static final ILogger LOG = LoggerFactory.getLogger( WPVSConfigurationDocument.class );

    private static String PRE_DWPVS = CommonNamespaces.DEEGREEWPVS_PREFIX + ":";

    // The smallestMinimalScaleDenomiator is needed to calculate the smallest resolutionstripe
    // possible
    private double smallestMinimalScaleDenominator = Double.MAX_VALUE;

    /**
     * Creates a class representation of the <code>WPVSConfiguration</code> document.
     * 
     * @return Returns a WPVSConfiguration object.
     * @throws InvalidConfigurationException
     */
    public WPVSConfiguration parseConfiguration()
                            throws InvalidConfigurationException {

        LOG.entering();
        WPVSConfiguration wpvsConfiguration = null;
        try {
            // TODO 'contents' field not verified, therefore null! Check spec.
            wpvsConfiguration = new WPVSConfiguration(
                                                       parseVersion(),
                                                       parseUpdateSequence(),
                                                       getServiceIdentification(),
                                                       getServiceProvider(),
                                                       parseOperationsMetadata(),
                                                       null,
                                                       getDataset(),
                                                       getDeegreeParams(),
                                                       ( Double.isInfinite( smallestMinimalScaleDenominator ) ? 1.0
                                                                                                             : smallestMinimalScaleDenominator ) );

        } catch ( XMLParsingException e ) {
            throw new InvalidConfigurationException( e.getMessage() + "\n"
                                                     + StringTools.stackTraceToString( e ) );

        } catch ( MissingParameterValueException e ) {
            throw new InvalidConfigurationException( e.getMessage() + "\n"
                                                     + StringTools.stackTraceToString( e ) );

        } catch ( InvalidParameterValueException e ) {
            throw new InvalidConfigurationException( e.getMessage() + "\n"
                                                     + StringTools.stackTraceToString( e ) );

        } catch ( OGCWebServiceException e ) {
            throw new InvalidConfigurationException( e.getMessage() + "\n"
                                                     + StringTools.stackTraceToString( e ) );

        } catch ( InvalidConfigurationException e ) {
            throw new InvalidConfigurationException( e.getMessage() + "\n"
                                                     + StringTools.stackTraceToString( e ) );

        }
        LOG.exiting();
        return wpvsConfiguration;
    }

    /**
     * Gets the <code>WPVSDeegreeParams</code> object from the <code>WPVSConfiguration</code>
     * element.
     * 
     * @return Returns the wpvsDeegreeParams object.
     * @throws InvalidConfigurationException
     * @throws XMLParsingException
     */
    public WPVSDeegreeParams getDeegreeParams()
                            throws InvalidConfigurationException, XMLParsingException {

        Node deegree = XMLTools.getRequiredNode( getRootElement(), PRE_DWPVS + "deegreeParams",
                                                 nsContext );
        WPVSDeegreeParams wpvsDeegreeParams = parseDeegreeParams( deegree );

        return wpvsDeegreeParams;
    }

    /**
     * Creates and returns a new <code>WPVSDeegreeParams</code> object from the given
     * <code>Node</code>.
     * 
     * @param deegreeNode
     * @return Returns a new WPVSDeegreeParams object.
     * @throws XMLParsingException
     * @throws InvalidConfigurationException
     */
    private WPVSDeegreeParams parseDeegreeParams( Node deegreeNode )
                            throws XMLParsingException, InvalidConfigurationException {

        LOG.entering();

        Element deegreeElement = (Element) XMLTools.getRequiredNode(
                                                                     deegreeNode,
                                                                     PRE_DWPVS
                                                                                             + "DefaultOnlineResource",
                                                                     nsContext );
        OnlineResource online = parseOnLineResource( deegreeElement );

        int cacheSize = XMLTools.getNodeAsInt( deegreeNode, PRE_DWPVS + "CacheSize", nsContext, 100 );

        int maxLifeTime = XMLTools.getNodeAsInt( deegreeNode, PRE_DWPVS + "MaxLifeTime", nsContext,
                                                 3600 );

        int reqTimeLimit = XMLTools.getNodeAsInt( deegreeNode, PRE_DWPVS + "RequestTimeLimit",
                                                  nsContext, 60 );
        reqTimeLimit *= 1000;

        float viewQuality = (float) XMLTools.getNodeAsDouble( deegreeNode, PRE_DWPVS
                                                                           + "ViewQuality",
                                                              nsContext, 0.95f );

        int maxMapWidth = XMLTools.getNodeAsInt( deegreeNode, PRE_DWPVS + "MaxViewWidth",
                                                 nsContext, 1000 );

        int maxMapHeight = XMLTools.getNodeAsInt( deegreeNode, PRE_DWPVS + "MaxViewHeight",
                                                  nsContext, 1000 );

        int maxTextureDim = XMLTools.getNodeAsInt( deegreeNode, PRE_DWPVS + "MaxTextureDimension",
                                                   nsContext, 1024 );

        if ( !( maxTextureDim == 1024 || maxTextureDim == 512 || maxTextureDim == 256 || maxTextureDim == 128 ) ) {
            throw new InvalidConfigurationException(
                                                     "maxTextureDimension must be either 1024, 512, 256 or 128." );
        }
        String charSet = XMLTools.getNodeAsString( deegreeNode, PRE_DWPVS + "CharacterSet",
                                                   nsContext, "UTF-8" );

        Node copyrightNode = XMLTools.getNode( deegreeNode, PRE_DWPVS + "Copyright", nsContext );

        boolean isFixedSplitter = XMLTools.getNodeAsBoolean(
                                                             deegreeNode,
                                                             PRE_DWPVS
                                                                                     + "FixedSplittingMode/text()",
                                                             nsContext, true );

        // TODO UT: I wonder if this the best way to do this?

        Node copyTextNode = XMLTools.getNode( copyrightNode, PRE_DWPVS + "Text", nsContext );
        Node copyURLNode = XMLTools.getNode( copyrightNode, PRE_DWPVS + "ImageURL/@xlink:href",
                                             nsContext );

        boolean isWatermarked = false;

        String copyright;
        if ( copyTextNode != null ) {
            copyright = XMLTools.getRequiredNodeAsString( copyrightNode, PRE_DWPVS + "Text/text()",
                                                          nsContext );
        } else if ( copyURLNode != null ) {
            copyright = XMLTools.getRequiredNodeAsString( copyrightNode, PRE_DWPVS
                                                                         + "ImageURL/@xlink:href",
                                                          nsContext );

            isWatermarked = XMLTools.getNodeAsBoolean( copyrightNode, PRE_DWPVS
                                                                      + "ImageURL/@watermark",
                                                       nsContext, isWatermarked );

            try {
                copyright = resolve( copyright ).toString();
            } catch ( MalformedURLException e ) {
                throw new InvalidConfigurationException( "Copyright/ImageURL '" + copyright
                                                         + "' doesn't seem to be a valid URL!" );
            }

        } else {
            throw new InvalidConfigurationException( "Copyright must contain either "
                                                     + "a Text-Element or an ImageURL-Element!" );
        }

        Map<String, URL> backgroundMap = new HashMap<String, URL>( 10 );
        Element backgrounds = (Element) XMLTools.getNode( deegreeNode,
                                                          PRE_DWPVS + "BackgroundList", nsContext );
        if ( backgrounds != null ) {
            List backgroundList = XMLTools.getNodes( backgrounds, PRE_DWPVS + "Background",
                                                     nsContext );
            for ( Iterator iter = backgroundList.iterator(); iter.hasNext(); ) {
                Element background = (Element) iter.next();

                String bgName = background.getAttribute( "name" );
                String bgHref = background.getAttribute( "href" );

                if ( bgName == null || bgName.length() == 0 || bgHref == null
                     || bgHref.length() == 0 )
                    throw new InvalidConfigurationException(
                                                             "Background must contain a 'name' and a "
                                                                                     + " 'href' attribute, both if which must contain non-empty strings." );

                try {

                    backgroundMap.put( bgName, resolve( bgHref ) );
                } catch ( MalformedURLException e ) {
                    throw new InvalidConfigurationException( "Background", e.getMessage() );
                }
            }

        }

        boolean quality = XMLTools.getNodeAsBoolean( deegreeNode, PRE_DWPVS
                                                                  + "RequestQualityPreferred",
                                                     nsContext, true );
        double maximumFarClippingPlane = XMLTools.getNodeAsDouble(
                                                                   deegreeNode,
                                                                   PRE_DWPVS
                                                                                           + "RequestsMaximumFarClippingPlane",
                                                                   nsContext, 15000 );

        String defaultSplitter = XMLTools.getNodeAsString( deegreeNode,
                                                           PRE_DWPVS + "DefaultSplitter",
                                                           nsContext, "QUAD" ).toUpperCase();

        WPVSDeegreeParams wpvsDeegreeParams = new WPVSDeegreeParams( online, cacheSize,
                                                                     reqTimeLimit, charSet,
                                                                     copyright, isWatermarked,
                                                                     maxLifeTime, viewQuality,
                                                                     backgroundMap,
                                                                     isFixedSplitter, maxMapWidth,
                                                                     maxMapHeight, maxTextureDim,
                                                                     quality,
                                                                     maximumFarClippingPlane,
                                                                     defaultSplitter );

        LOG.exiting();
        return wpvsDeegreeParams;
    }

    /**
     * Gets the <code>Dataset</code> object from the <code>WPVSConfiguration</code> element.
     * 
     * @return Returns the Dataset object form root element.
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     * @throws InvalidParameterValueException
     * @throws MissingParameterValueException
     * @throws InvalidConfigurationException
     */
    private Dataset getDataset()
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException, OGCWebServiceException,
                            InvalidConfigurationException {

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
     * @throws MissingParameterValueException
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     * @throws InvalidConfigurationException
     */
    private Dataset parseDataset( Element datasetElement, Dataset parent )
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException, OGCWebServiceException,
                            InvalidConfigurationException {
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

        // update the smallestMinimalScaleDenomiator
        if ( minScaleDenom < smallestMinimalScaleDenominator )
            smallestMinimalScaleDenominator = minScaleDenom;

        double maxScaleDenom = XMLTools.getNodeAsDouble(
                                                         datasetElement,
                                                         PRE_DWPVS
                                                                                 + "MaximumScaleDenominator/text()",
                                                         nsContext, 9E9 );

        if ( minScaleDenom >= maxScaleDenom ) {
            throw new InvalidCapabilitiesException( "MinimumScaleDenominator must be "
                                                    + "less than MaximumScaleDenominator!" );
        }
        ElevationModel elevationModel = parseElevationModel( datasetElement, name );
        AbstractDataSource[] dataSources = parseAbstractDatasources( datasetElement, name );

        // create new root dataset
        Dataset dataset = new Dataset( queryable, opaque, noSubsets, fixedWidth, fixedHeight, name,
                                       title, abstract_, keywords, crsList, format,
                                       wgs84BoundingBox, boundingBoxes, dimensions, dataProvider,
                                       identifier, metaData, datasetRefs, featureListRefs, style,
                                       minScaleDenom, maxScaleDenom, null, elevationModel,
                                       dataSources, parent );

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

    /**
     * Creates and returns a new <code>ElevationModel</code> object from the given
     * <code>Element</code> and the parent <code>Dataset</code>.
     * 
     * The OGC ElevationModel contains only a String. The Deegree ElevationModel additionaly
     * contains a complex dataSource.
     * 
     * @param datasetElement
     * @param parent
     * @return Returns the ElevationModel object.
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     * @throws InvalidParameterValueException
     * @throws MissingParameterValueException
     * @throws InvalidConfigurationException
     */
    private ElevationModel parseElevationModel( Element datasetElement, String parentName )
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException, OGCWebServiceException,
                            InvalidConfigurationException {

        Element elevationElement = null;
        String name = null;
        ElevationModel elevationModel = null;

        elevationElement = (Element) XMLTools.getNode( datasetElement,
                                                       PRE_DWPVS + "ElevationModel", nsContext );

        AbstractDataSource[] dataSources = null;
        if ( elevationElement != null ) {

            name = XMLTools.getRequiredNodeAsString( elevationElement, PRE_DWPVS + "Name/text()",
                                                     nsContext );

            dataSources = parseAbstractDatasources( elevationElement, parentName );
            if ( dataSources.length < 1 ) {
                throw new InvalidCapabilitiesException(
                                                        "Each '"
                                                                                + elevationElement.getNodeName()
                                                                                + "' must contain at least one data source!" );
            }
        }

        elevationModel = new ElevationModel( name, dataSources );

        return elevationModel;
    }

    /**
     * Creates and returns a new array of <code>AbstractDataSource</code> objects from the given
     * <code>Element</code>.
     * 
     * If the objects are used within an ElevationModel object, they may be of the following types:
     * LocalWCSDataSource, RemoteWCSDataSource, LocalWFSDataSource, RemoteWFSDataSource. If the
     * objects are used within a Dataset object, they may additionaly be of the types:
     * LocalWMSDataSource, RemoteWMSDataSource.
     * 
     * @param element
     * @return Returns a new array of AbstractDataSource objects.
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     * @throws InvalidConfigurationException
     */
    private AbstractDataSource[] parseAbstractDatasources( Element element, String parentName )
                            throws XMLParsingException, OGCWebServiceException,
                            InvalidConfigurationException {

        List abstractDataSources = XMLTools.getNodes( element, "./*", nsContext );
        List<AbstractDataSource> tempDataSources = new ArrayList<AbstractDataSource>(
                                                                                      abstractDataSources.size() );

        for ( int i = 0; i < abstractDataSources.size(); i++ ) {

            Element dataSourceElement = (Element) abstractDataSources.get( i );

            // String nodeName = dataSourceElement.getNodeName();
            String nodeName = dataSourceElement.getLocalName();

            if ( nodeName.endsWith( "DataSource" ) ) {
                QualifiedName pn = null;
                if ( parentName != null ) {
                    pn = new QualifiedName( PRE_DWPVS, parentName, nsContext.getURI( PRE_DWPVS ) );
                }
                QualifiedName name = XMLTools.getNodeAsQualifiedName( dataSourceElement,
                                                                      PRE_DWPVS + "Name/text()",
                                                                      nsContext, pn );

                OWSCapabilities owsCapabilities = parseOWSCapabilities( dataSourceElement );

                double minScaleDenom = XMLTools.getNodeAsDouble(
                                                                 dataSourceElement,
                                                                 PRE_DWPVS
                                                                                         + "MinimumScaleDenominator/text()",
                                                                 nsContext, 0 );

                // update the smallestMinimalScaleDenomiator
                if ( minScaleDenom < smallestMinimalScaleDenominator )
                    smallestMinimalScaleDenominator = minScaleDenom;

                double maxScaleDenom = XMLTools.getNodeAsDouble(
                                                                 dataSourceElement,
                                                                 PRE_DWPVS
                                                                                         + "MaximumScaleDenominator/text()",
                                                                 nsContext, 9E9 );

                Surface validArea = (Surface) parseValidArea( dataSourceElement );
                AbstractDataSource dataSource = null;

                if ( nodeName.equals( "LocalWCSDataSource" ) ) {
                    Element filterElement = (Element) XMLTools.getRequiredNode(
                                                                                dataSourceElement,
                                                                                PRE_DWPVS
                                                                                                        + "FilterCondition",
                                                                                nsContext );
                    GetCoverage getCoverage = parseWCSFilterCondition( filterElement );
                    Color[] transparentColors = parseTransparentColors( dataSourceElement );

                    dataSource = new LocalWCSDataSource( name, owsCapabilities, validArea,
                                                         minScaleDenom, maxScaleDenom, getCoverage,
                                                         transparentColors );

                } else if ( nodeName.equals( "RemoteWCSDataSource" ) ) {
                    Element filterElement = (Element) XMLTools.getRequiredNode(
                                                                                dataSourceElement,
                                                                                PRE_DWPVS
                                                                                                        + "FilterCondition",
                                                                                nsContext );
                    GetCoverage getCoverage = parseWCSFilterCondition( filterElement );
                    Color[] transparentColors = parseTransparentColors( dataSourceElement );

                    dataSource = new RemoteWCSDataSource( name, owsCapabilities, validArea,
                                                          minScaleDenom, maxScaleDenom,
                                                          getCoverage, transparentColors );

                } else if ( nodeName.equals( "LocalWFSDataSource" ) ) {
                    Text geoPropNode = (Text) XMLTools.getRequiredNode(
                                                                        dataSourceElement,
                                                                        PRE_DWPVS
                                                                                                + "GeometryProperty/text()",
                                                                        nsContext );
                    PropertyPath geometryProperty = parsePropertyPath( geoPropNode );

                    Element filterElement = (Element) XMLTools.getNode(
                                                                        dataSourceElement,
                                                                        PRE_DWPVS
                                                                                                + "FilterCondition/ogc:Filter",
                                                                        nsContext );

                    Filter filterCondition = null;
                    if ( filterElement != null ) {
                        filterCondition = AbstractFilter.buildFromDOM( filterElement );
                    }

                    //FeatureCollectionAdapter adapter = createFCAdapterFromAdapterClassName( dataSourceElement );

                    dataSource = new LocalWFSDataSource( name, owsCapabilities, validArea,
                                                         minScaleDenom, maxScaleDenom,
                                                         geometryProperty, filterCondition/*, adapter*/);

                } else if ( nodeName.equals( "RemoteWFSDataSource" ) ) {
                    Text geoPropNode = (Text) XMLTools.getRequiredNode(
                                                                        dataSourceElement,
                                                                        PRE_DWPVS
                                                                                                + "GeometryProperty/text()",
                                                                        nsContext );
                    PropertyPath geometryProperty = parsePropertyPath( geoPropNode );

                    Element filterElement = (Element) XMLTools.getNode(
                                                                        dataSourceElement,
                                                                        PRE_DWPVS
                                                                                                + "FilterCondition/ogc:Filter",
                                                                        nsContext );

                    Filter filterCondition = null;
                    if ( filterElement != null ) {
                        filterCondition = AbstractFilter.buildFromDOM( filterElement );
                    }

                    //FeatureCollectionAdapter adapter = createFCAdapterFromAdapterClassName( dataSourceElement );

                    dataSource = new RemoteWFSDataSource( name, owsCapabilities, validArea,
                                                          minScaleDenom, maxScaleDenom,
                                                          geometryProperty, filterCondition
                    /*,adapter*/);

                } else if ( nodeName.equals( "LocalWMSDataSource" ) ) {
                    if ( element.getNodeName().endsWith( "ElevationModel" ) ) {
                        throw new InvalidConfigurationException( "An ElevationModel cannot "
                                                                 + "contain a LocalWMSDataSource!" );
                    }
                    Element filterElement = (Element) XMLTools.getRequiredNode(
                                                                                dataSourceElement,
                                                                                PRE_DWPVS
                                                                                                        + "FilterCondition",
                                                                                nsContext );
                    GetMap getMap = parseWMSFilterCondition( filterElement );

                    Color[] transparentColors = parseTransparentColors( dataSourceElement );

                    dataSource = new LocalWMSDataSource( name, owsCapabilities, validArea,
                                                         minScaleDenom, maxScaleDenom, getMap,
                                                         transparentColors );

                } else if ( nodeName.equals( "RemoteWMSDataSource" ) ) {
                    if ( element.getNodeName().endsWith( "ElevationModel" ) ) {
                        throw new InvalidConfigurationException( "An ElevationModel cannot "
                                                                 + "contain a LocalWMSDataSource!" );
                    }
                    Element filterElement = (Element) XMLTools.getRequiredNode(
                                                                                dataSourceElement,
                                                                                PRE_DWPVS
                                                                                                        + "FilterCondition",
                                                                                nsContext );
                    GetMap getMap = parseWMSFilterCondition( filterElement );

                    Color[] transparentColors = parseTransparentColors( dataSourceElement );

                    dataSource = new RemoteWMSDataSource( name, owsCapabilities, validArea,
                                                          minScaleDenom, maxScaleDenom, getMap,
                                                          transparentColors );

                } else {
                    throw new InvalidCapabilitiesException( "Unknown data source: '" + nodeName
                                                            + "'" );
                }

                tempDataSources.add( dataSource );
            }
        }

        AbstractDataSource[] dataSources = tempDataSources.toArray( new AbstractDataSource[tempDataSources.size()] );

        return dataSources;
    }

    //    private FeatureCollectionAdapter createFCAdapterFromAdapterClassName( Element dataSourceElement )
    //                            throws InvalidConfigurationException, XMLParsingException {
    //
    //        String adapterClassName = XMLTools.getNodeAsString(
    //                                                            dataSourceElement,
    //                                                            PRE_DWPVS
    //                                                                                    + "FeatureCollectionAdapter/@class",
    //                                                            nsContext, null );
    //
    //        if ( adapterClassName == null ) {
    //            throw new InvalidConfigurationException( "A WFS data source for must define a "
    //                                                     + "FeatureCollectionAdapter class" );
    //        }
    //
    //        FeatureCollectionAdapter adapter = null;
    //
    //        try {
    //            adapter = (FeatureCollectionAdapter) Class.forName( adapterClassName.trim() ).newInstance();
    //        } catch ( Exception e ) {
    //            e.printStackTrace();
    //            String s = StringTools.concat(
    //                                           200,
    //                                           "A WFS data source with FeatureCollectionAdapter class '",
    //                                           adapterClassName,
    //                                           "' cannot be instantiated. Make sure ",
    //                                           " such a class is defined in the classpath." );
    //            throw new InvalidConfigurationException( s );
    //        }
    //
    //        String parentName = dataSourceElement.getParentNode().getLocalName();
    //        if ( "ElevationModel".equals( parentName ) ) {
    //            if ( !( adapter instanceof PointListFactory ) ) {
    //                String s = StringTools.concat(
    //                                               500,
    //                                               "A FeatureCollectionAdapter for an elevation ",
    //                                               "model must define a class name such as '",
    //                                               "org.deegree.ogcwebservices.wpvs.j3d.PointsToPointListFactory', ",
    //                                               "'org.deegree.ogcwebservices.wpvs.j3d.LinesToPointListFactory',",
    //                                               "'org.deegree.ogcwebservices.wpvs.j3d.PolygonsToPointListFactory' ",
    //                                               " or any class that implements 'org.deegree.ogcwebservices.wpvs.j3d.PointListFactory'\n" );
    //                throw new InvalidConfigurationException( s );
    //            }
    //        } /*
    //             * else if ( !( adapter instanceof ThreeDObjectFactory ) ) { String s =
    //             * StringTools.concat(400, "A FeatureCollectionAdapter for a Dataset must ", " define a
    //             * class name such as '", "org.deegree.ogcwebservices.wpvs.j3d.BuildingsFactory' ", "or
    //             * any class that implements ",
    //             * "'org.deegree.ogcwebservices.wpvs.j3d.ThreeDObjectFactory'." ); throw new
    //             * InvalidConfigurationException( s ); }
    //             */
    //
    //        return adapter;
    //    }

    /**
     * FIXME check content of StringBuffer and Map! This is an adapted copy from:
     * org.deegree.ogcwebservices.wms.configuration#parseWMSFilterCondition(Node)
     * 
     * Creates and returns a new <code>GetMap</code> object from the given <code>Element</code>.
     * 
     * @param filterElement
     * @return a partial wms GetMap request instance
     * @throws XMLParsingException
     */
    private GetMap parseWMSFilterCondition( Element filterElement )
                            throws XMLParsingException {

        GetMap getMap = null;

        String wmsRequest = XMLTools.getRequiredNodeAsString( filterElement, PRE_DWPVS
                                                                             + "WMSRequest/text()",
                                                              nsContext );

        StringBuffer sd = new StringBuffer( 1000 );
        sd.append( "REQUEST=GetMap&LAYERS=%default%&STYLES=&SRS=EPSG:4326&" );
        sd.append( "BBOX=0,0,1,1&WIDTH=1&HEIGHT=1&FORMAT=%default%" );

        Map<String, String> map1 = KVP2Map.toMap( sd.toString() );

        Map<String, String> map2 = KVP2Map.toMap( wmsRequest );
        if ( map2.get( "VERSION" ) == null && map2.get( "WMTVER" ) == null ) {
            map2.put( "VERSION", "1.1.1" );
        }
        // if no service is set use WMS as default
        if ( map2.get( "SERVICE" ) == null ) {
            map2.put( "SERVICE", "WMS" );
        }

        map1.putAll( map2 );

        String id = Long.toString( IDGenerator.getInstance().generateUniqueID() );
        map1.put( "ID", id );
        try {
            getMap = GetMap.create( map1 );
        } catch ( Exception e ) {
            throw new XMLParsingException( "could not create GetMap from WMS FilterCondition", e );
        }

        return getMap;
    }

    /**
     * FIXME check content of StringBuffer ! This is an adapted copy from:
     * org.deegree.ogcwebservices.wms.configuration#parseWCSFilterCondition(Node)
     * 
     * Creates and returns a new <code>GetCoverage</code> object from the given
     * <code>Element</code>.
     * 
     * @param filterElement
     * @return a partial GetCoverage request
     * @throws XMLParsingException
     */
    private GetCoverage parseWCSFilterCondition( Element filterElement )
                            throws XMLParsingException {

        GetCoverage coverage = null;

        String wcsRequest = XMLTools.getRequiredNodeAsString( filterElement, PRE_DWPVS
                                                                             + "WCSRequest/text()",
                                                              nsContext );

        StringBuffer sd = new StringBuffer( 1000 );
        sd.append( "version=1.0.0&Coverage=%default%&CRS=EPSG:4326&BBOX=0,0,1,1" );
        sd.append( "&Width=1&Height=1&Format=%default%&" );
        sd.append( wcsRequest );

        String id = "" + IDGenerator.getInstance().generateUniqueID();

        try {
            coverage = GetCoverage.create( id, sd.toString() );
        } catch ( Exception e ) {
            throw new XMLParsingException( "Could not create GetCoverage "
                                           + "from WPVS FilterCondition", e );
        }

        return coverage;
    }

    /**
     * Creates and returns a new <code>OWSCapabilities</code> object from the given
     * <code>Element</code>.
     * 
     * @param element
     * @return Returns a new OWSCapabilities object.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    private OWSCapabilities parseOWSCapabilities( Element element )
                            throws XMLParsingException, InvalidCapabilitiesException {

        Element owsCapabilitiesElement = (Element) XMLTools.getRequiredNode(
                                                                             element,
                                                                             PRE_DWPVS
                                                                                                     + "OWSCapabilities",
                                                                             nsContext );

        String format = null;

        // FIXME
        // schema has onlineResourceType as not optional, so it should be mandatory.
        // but in other examples onlineResource is never created with this onlineResourceType.
        // therefore it gets omitted here, too.

        // String onlineResourceType = XMLTools.getRequiredNodeAsString(
        // owsCapabilitiesElement, PRE_DWPVS+"OnlineResource/@xlink:type", nsContext );

        URI onlineResourceURI = XMLTools.getRequiredNodeAsURI(
                                                               owsCapabilitiesElement,
                                                               PRE_DWPVS
                                                                                       + "OnlineResource/@xlink:href",
                                                               nsContext );
        URL onlineResource;
        try {

            onlineResource = resolve( onlineResourceURI.toURL().toString() );
        } catch ( MalformedURLException e ) {
            throw new InvalidCapabilitiesException( onlineResourceURI
                                                    + " does not represent a valid URL: "
                                                    + e.getMessage() );
        }

        return new OWSCapabilities( format, onlineResource );

        // FIXME
        // if onlineResourceType is going to be used, the returned new OnlineResource should be
        // created with different constructor:
        // return new OWSCapabilities( format, onlineResourceType, onlineResource );
    }

    /**
     * Creates and returns a new <code>Geometry</code> object from the given Element.
     * 
     * @param dataSource
     * @return Returns a new Geometry object.
     * @throws XMLParsingException
     * @throws InvalidConfigurationException
     */
    private Geometry parseValidArea( Element dataSource )
                            throws XMLParsingException, InvalidConfigurationException {

        LOG.entering();

        Geometry geom = null;

        List nl = XMLTools.getNodes( dataSource, PRE_DWPVS + "ValidArea/*", nsContext );

        if ( nl.size() == 1
             && ( (Node) nl.get( 0 ) ).getNamespaceURI().equals( GMLNS.toASCIIString() ) ) {
            try {
                geom = GMLGeometryAdapter.wrap( (Element) nl.get( 0 ) );
            } catch ( GeometryException e ) {
                throw new InvalidConfigurationException( "could not parse/create valid area "
                                                         + "of a datasource", e );
            }
        }

        LOG.exiting();

        return geom;
    }

    /**
     * Creates and returns a new array of <code>Color</code> objects from the given Element.
     * 
     * @param dataSourceElement
     * @return Returns a new array of Color objects.
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     */
    private Color[] parseTransparentColors( Element dataSourceElement )
                            throws XMLParsingException {

        List colorList = XMLTools.getNodes( dataSourceElement, PRE_DWPVS + "TransparentColors/"
                                                               + PRE_DWPVS + "Color", nsContext );

        Color[] transparentColors = null;
        if ( colorList != null ) {
            transparentColors = new Color[colorList.size()];

            for ( int i = 0; i < transparentColors.length; i++ ) {

                Element colorElement = (Element) colorList.get( i );
                String color = XMLTools.getRequiredNodeAsString( colorElement, "./text()",
                                                                 nsContext );

                transparentColors[i] = Color.decode( color );
            }
        }

        return transparentColors;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: WPVSConfigurationDocument.java,v $
 * Revision 1.40  2006/11/28 18:09:28  mschneider
 * Fixed header.
 *
 * Revision 1.39  2006/11/28 18:08:55  mschneider
 * Fixed parsing of GeometryProperty name. Namespace binding was not correctly extracted (null).
 *
 * Revision 1.38  2006/11/28 16:52:19  bezema Added support for a default splitter
 *
 * Revision 1.37  2006/11/27 15:41:13  bezema Updated the coordinatesystem handling and the featurecollection adapter
 *
 * Revision 1.36 2006/11/27 11:44:14 bezema Added a minimalScaleDenomitator check

 * Revision 1.34 2006/11/23 11:46:40 bezema The initial version of the new wpvs
 * 
 * Revision 1.33 2006/11/07 16:33:49 poth bug fixes and code formatting
 * 
 * Revision 1.32 2006/08/07 12:14:57 poth never read variable removed
 * 
 * Revision 1.31 2006/07/20 08:13:05 taddei use of QualiName for geometry property
 * 
 * Revision 1.30 2006/07/12 16:59:32 poth required adaptions according to renaming of OnLineResource to OnlineResource
 * 
 * Revision 1.29 2006/07/05 15:58:23 poth bug fix - changed Query to Filter for WFS datasources
 * 
 * Revision 1.28 2006/06/29 19:06:54 poth ** empty log message ***
 * 
 * Revision 1.27 2006/06/29 18:50:16 poth bug fix reading optional element TransparentColors
 * 
 * Revision 1.26 2006/06/20 07:45:21 taddei datasources use quali names now
 * 
 * Revision 1.25 2006/05/05 12:41:36 taddei fixed bug in max tex size
 * 
 * Revision 1.24 2006/04/26 12:13:07 taddei max size and tex config parameters
 * 
 * Revision 1.23 2006/04/06 20:25:24 poth ** empty log message ***
 * 
 * Revision 1.22 2006/04/05 08:56:21 taddei refactoring: fc adapter
 * 
 * Revision 1.20 2006/03/07 08:46:26 taddei added pts list factory
 * 
 * Revision 1.19 2006/02/14 15:14:43 taddei added possibility to choose splitter
 * 
 * Revision 1.18 2006/02/10 16:05:41 taddei capabilities use now resolve()
 * 
 * Revision 1.17 2006/01/30 14:56:10 taddei implementation of copyright info
 * 
 * Revision 1.16 2006/01/26 13:54:59 taddei added code for parsing ElevationModel/Name
 * 
 * Revision 1.15 2006/01/18 08:47:54 taddei bug fixes
 * 
 * Revision 1.14 2005/12/23 11:16:50 mays add comments
 * 
 * Revision 1.13 2005/12/23 10:39:19 mays add imports that where accidentally lost
 * 
 * Revision 1.12 2005/12/23 10:34:56 mays correction of color and filterConditions
 * 
 * Revision 1.10 2005/12/13 13:31:49 mays changes in parseElevationModel because of necessary changes in namespace
 * 
 * Revision 1.9 2005/12/12 13:51:38 mays revision of parseElevationModel, parseDataset, parseDeegreeParams and parseOWSCapabilities.
 * 
 * Revision 1.8 2005/12/09 14:11:22 mays code clean up
 * 
 * Revision 1.7 2005/12/08 16:53:10 mays organized imports
 * 
 * Revision 1.6 2005/12/08 16:49:08 mays move configuration specific stuff from WPVSCapabilitiesDocument to here
 * 
 * Revision 1.5 2005/12/01 10:30:14 mays add standard footer to all java classes in wpvs package
 * 
 **************************************************************************************************/