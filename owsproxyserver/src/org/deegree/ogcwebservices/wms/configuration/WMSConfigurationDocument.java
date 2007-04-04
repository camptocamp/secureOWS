//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/WMSConfigurationDocument.java,v 1.52 2006/11/30 20:05:08 poth Exp $
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
package org.deegree.ogcwebservices.wms.configuration;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.Proxy;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BootLogger;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.getcapabilities.MetadataURL;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wcs.WCService;
import org.deegree.ogcwebservices.wcs.configuration.WCSConfiguration;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wfs.RemoteWFService;
import org.deegree.ogcwebservices.wfs.WFServiceFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wms.RemoteWMService;
import org.deegree.ogcwebservices.wms.capabilities.Attribution;
import org.deegree.ogcwebservices.wms.capabilities.AuthorityURL;
import org.deegree.ogcwebservices.wms.capabilities.DataURL;
import org.deegree.ogcwebservices.wms.capabilities.Dimension;
import org.deegree.ogcwebservices.wms.capabilities.Extent;
import org.deegree.ogcwebservices.wms.capabilities.FeatureListURL;
import org.deegree.ogcwebservices.wms.capabilities.Identifier;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.LayerBoundingBox;
import org.deegree.ogcwebservices.wms.capabilities.LegendURL;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;
import org.deegree.ogcwebservices.wms.capabilities.Style;
import org.deegree.ogcwebservices.wms.capabilities.StyleSheetURL;
import org.deegree.ogcwebservices.wms.capabilities.StyleURL;
import org.deegree.ogcwebservices.wms.capabilities.UserDefinedSymbolization;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;
import org.deegree.owscommon_new.ServiceProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <code>WMSConfigurationDocument</code> is the parser class for a standard 1.1.1 WMS
 * configuration document, ie, a capabilities document enriched by deegree parameters.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.52 $, $Date: 2006/11/30 20:05:08 $
 * 
 * @since 2.0
 */
public class WMSConfigurationDocument extends WMSCapabilitiesDocument {

    private static final long serialVersionUID = 2320990982989322325L;

    protected static final URI DEEGREEWMSNS = CommonNamespaces.DEEGREEWMS;

    private static final String XML_TEMPLATE = "WMSConfigurationTemplate.xml";

    private static final String XSLT_TEMPLATE_NAME = "WMSConfigurationTransform.xsl";

    private static XSLTDocument XSLT_TEMPLATE;

    private static Map<URL, OGCCapabilities> capaCache = new HashMap<URL, OGCCapabilities>();

    private static final ILogger LOG = LoggerFactory.getLogger( WMSConfigurationDocument.class );

    private static final QualifiedName DEFAULT_GEO_PROP = new QualifiedName(
                                                                             "app",
                                                                             "GEOM",
                                                                             CommonNamespaces.buildNSURI( "http://www.deegree.org/app" ) );

    static {
        XSLT_TEMPLATE = new XSLTDocument();
        try {
            XSLT_TEMPLATE.load( WMSConfigurationDocument.class.getResource( XSLT_TEMPLATE_NAME ) );
        } catch ( Exception e ) {
            BootLogger.logError( "Error loading XSLT sheet in WMSConfigurationDocument.", e );
        }
    }

    /**
     * Creates a skeleton capabilities document that contains the mandatory elements only.
     * 
     * @throws IOException
     * @throws SAXException
     */
    @Override
    public void createEmptyDocument()
                            throws IOException, SAXException {
        URL url = WMSConfigurationDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * Creates a class representation of the document.
     * 
     * @return class representation of the configuration document
     * @throws InvalidConfigurationException
     * @throws XMLParsingException
     */
    public WMSConfiguration parseConfiguration()
                            throws InvalidConfigurationException, XMLParsingException {

        try {
            // transform document to fill missing elements and attributes with
            // default values
            XMLFragment frag = XSLT_TEMPLATE.transform( this );
            this.setRootElement( frag.getRootElement() );
        } catch ( TransformerException e ) {
            String msg = "Error transforming WMS configuration document (in order to fill in default value).";
            LOG.logError( msg, e );
            throw new InvalidConfigurationException( msg, e );
        }

        ServiceIdentification serviceIdentification = null;
        ServiceProvider serviceProvider = null;
        OperationsMetadata metadata = null;
        Layer layer = null;
        UserDefinedSymbolization uds = null;
        WMSDeegreeParams params = null;
        Element root = getRootElement();
        String version = XMLTools.getRequiredNodeAsString( root, "@version", nsContext );
        String updateSeq = XMLTools.getNodeAsString( root, "@updateSequence", nsContext, null );
        try {
            Node node = XMLTools.getRequiredNode( getRootElement(), "./deegreewms:DeegreeParam",
                                                  nsContext );
            params = parseDeegreeParams( node );

            serviceIdentification = parseServiceIdentification();
            serviceProvider = parseServiceProvider();
            metadata = parseOperationsMetadata();

            uds = parseUserDefinedSymbolization();
            Element layerElem = (Element) XMLTools.getRequiredNode( getRootElement(),
                                                                    "./Capability/Layer", nsContext );
            layer = parseLayers( layerElem, null, null );

        } catch ( XMLParsingException e ) {
            e.printStackTrace();
            throw new InvalidConfigurationException( e.getMessage()
                                                     + StringTools.stackTraceToString( e ) );
        } catch ( MalformedURLException e ) {
            throw new InvalidConfigurationException( e.getMessage() + " - "
                                                     + StringTools.stackTraceToString( e ) );
        } catch (UnknownCRSException e) {
            throw new InvalidConfigurationException( e.getMessage() + " - "
                                                     + StringTools.stackTraceToString( e ) );
        }

        WMSConfiguration wmsConfiguration = new WMSConfiguration( version, updateSeq,
                                                                  serviceIdentification,
                                                                  serviceProvider, uds, metadata,
                                                                  layer, params, getSystemId() );

        return wmsConfiguration;
    }

    /**
     * Creates a class representation of the <code>deegreeParams</code>- section.
     * 
     * @param root
     * 
     * @return the deegree params
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    public WMSDeegreeParams parseDeegreeParams( Node root )
                            throws XMLParsingException, MalformedURLException {

        Element elem = (Element) XMLTools.getRequiredNode( root,
                                                           "./deegreewms:DefaultOnlineResource",
                                                           nsContext );
        OnlineResource ol = parseOnLineResource( elem );
        int cache = XMLTools.getNodeAsInt( root, "./deegreewms:CacheSize", nsContext, 100 );
        int maxLifeTime = XMLTools.getNodeAsInt( root, "./deegreewms:MaxLifeTime", nsContext, 3600 );
        int reqTimeLimit = XMLTools.getNodeAsInt( root, "./deegreewms:RequestTimeLimit", nsContext,
                                                  15 );
        reqTimeLimit *= 1000;
        double mapQuality = XMLTools.getNodeAsDouble( root, "./deegreewms:MapQuality", nsContext,
                                                      0.95 );
        int maxMapWidth = XMLTools.getNodeAsInt( root, "./deegreewms:MaxMapWidth", nsContext, 1000 );
        int maxMapHeight = XMLTools.getNodeAsInt( root, "./deegreewms:MaxMapHeight", nsContext,
                                                  1000 );
        int featureInfoRadius = XMLTools.getNodeAsInt( root, "./deegreewms:FeatureInfoRadius",
                                                       nsContext, 5 );
        String copyright = XMLTools.getNodeAsString( root, "./deegreewms:Copyright", nsContext, "" );

        URL dtdLocation = null;
        if ( XMLTools.getNode( root, "deegreewms:DTDLocation", nsContext ) != null ) {
            elem = (Element) XMLTools.getRequiredNode(
                                                       root,
                                                       "./deegreewms:DTDLocation/deegreewms:OnlineResource",
                                                       nsContext );
            OnlineResource olr = parseOnLineResource( elem );
            dtdLocation = olr.getLinkage().getHref();
        } else {
            dtdLocation = new URL( "http://schemas.opengis.net/wms/1.1.1/WMS_MS_Capabilities.dtd" );
        }

        URL featureSchemaLocation = null;
        String featureSchemaNamespace = null;
        if ( XMLTools.getNode( root, "deegreewms:FeatureInfoSchema", nsContext ) != null ) {
            featureSchemaNamespace = 
                XMLTools.getRequiredNodeAsString( root,
                                                  "deegreewms:FeatureInfoSchema/deegreewms:Namespace",
                                                  nsContext );
            elem = (Element) XMLTools.getRequiredNode( root,
                                                       "deegreewms:FeatureInfoSchema/deegreewms:OnlineResource",
                                                       nsContext );
            OnlineResource link = parseOnLineResource( elem );
            featureSchemaLocation = link.getLinkage().getHref();
        }

        boolean antiAliased = XMLTools.getNodeAsBoolean( root, "./deegreewms:AntiAliased",
                                                         nsContext, true );

        Proxy proxy = parseProxy( root );

        List<String> supportedVersions = parseSupportedVersions( root );

        WMSDeegreeParams deegreeParams = new WMSDeegreeParams( cache, maxLifeTime, reqTimeLimit,
                                                               (float) mapQuality, ol, maxMapWidth,
                                                               maxMapHeight, antiAliased,
                                                               featureInfoRadius, copyright,
                                                               null, dtdLocation, proxy,
                                                               supportedVersions,
                                                               featureSchemaLocation,
                                                               featureSchemaNamespace );

        return deegreeParams;
    }

    // returns the list of supported versions
    private List<String> parseSupportedVersions( Node root )
                            throws XMLParsingException {

        String[] versions = XMLTools.getNodesAsStrings( root, "./deegreewms:SupportedVersion",
                                                        nsContext );

        if ( versions != null )
            return Arrays.asList( versions );

        return new ArrayList<String>();

    }

    /**
     * @param root
     * @return the proxy
     * @throws XMLParsingException
     */
    private Proxy parseProxy( Node root )
                            throws XMLParsingException {

        Proxy proxy = null;
        Node pro = XMLTools.getNode( root, "./deegreewms:Proxy", nsContext );
        if ( pro != null ) {
            String proxyHost = XMLTools.getRequiredNodeAsString( pro, "./@proxyHost", nsContext );
            String proxyPort = XMLTools.getRequiredNodeAsString( pro, "./@proxyPort", nsContext );
            proxy = new Proxy( proxyHost, proxyPort );
        }

        return proxy;
    }

    /**
     * returns the layers offered by the WMS
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    @Override
    protected Layer parseLayers( Element layerElem, Layer parent, ScaleHint scaleHint )
                            throws XMLParsingException, UnknownCRSException {

        boolean queryable = XMLTools.getNodeAsBoolean( layerElem, "./@queryable", nsContext, false );
        int cascaded = XMLTools.getNodeAsInt( layerElem, "./@cascaded", nsContext, 0 );
        boolean opaque = XMLTools.getNodeAsBoolean( layerElem, "./@opaque", nsContext, false );
        boolean noSubsets = XMLTools.getNodeAsBoolean( layerElem, "./@noSubsets", nsContext, false );
        int fixedWidth = XMLTools.getNodeAsInt( layerElem, "./@fixedWidth", nsContext, 0 );
        int fixedHeight = XMLTools.getNodeAsInt( layerElem, "./@fixedHeight", nsContext, 0 );
        String name = XMLTools.getNodeAsString( layerElem, "./Name", nsContext, null );
        String title = XMLTools.getRequiredNodeAsString( layerElem, "./Title", nsContext );
        String layerAbstract = XMLTools.getNodeAsString( layerElem, "./Abstract", nsContext, null );
        String[] keywords = XMLTools.getNodesAsStrings( layerElem, "./KeywordList/Keyword",
                                                        nsContext );
        String[] srs = XMLTools.getNodesAsStrings( layerElem, "./SRS", nsContext );

        List nl = XMLTools.getNodes( layerElem, "./BoundingBox", nsContext );
        // TODO
        // substitue with Envelope
        LayerBoundingBox[] bboxes = null;
        if ( nl.size() == 0 && parent != null ) {
            // inherit BoundingBoxes from parent layer
            bboxes = parent.getBoundingBoxes();
        } else {
            bboxes = parseLayerBoundingBoxes( nl );
        }

        Element llBox = (Element) XMLTools.getNode( layerElem, "./LatLonBoundingBox", nsContext );
        Envelope llBoundingBox = null;
        if ( llBox == null && parent != null ) {
            // inherit LatLonBoundingBox parent layer
            llBoundingBox = parent.getLatLonBoundingBox();
        } else {
            llBoundingBox = parseLatLonBoundingBox( llBox );
        }

        Dimension[] dimensions = parseDimensions( layerElem );
        Extent[] extents = parseExtents( layerElem );

        Attribution attribution = parseAttribution( layerElem );

        AuthorityURL[] authorityURLs = parseAuthorityURLs( layerElem );

        MetadataURL[] metadataURLs = parseMetadataURLs( layerElem );

        DataURL[] dataURLs = parseDataURL( layerElem );

        Identifier[] identifiers = parseIdentifiers( layerElem );

        FeatureListURL[] featureListURLs = parseFeatureListURL( layerElem );

        Style[] styles = parseStyles( layerElem );

        scaleHint = parseScaleHint( layerElem, scaleHint );

        AbstractDataSource[] ds = parseDataSources( layerElem, name, scaleHint );

        Layer layer = new Layer( queryable, cascaded, opaque, noSubsets, fixedWidth, fixedHeight,
                                 name, title, layerAbstract, llBoundingBox, attribution, scaleHint,
                                 keywords, srs, bboxes, dimensions, extents, authorityURLs,
                                 identifiers, metadataURLs, dataURLs, featureListURLs, styles,
                                 null, ds, parent );

        // get Child layers
        nl = XMLTools.getNodes( layerElem, "./Layer", nsContext );
        Layer[] layers = new Layer[nl.size()];
        for ( int i = 0; i < layers.length; i++ ) {
            layers[i] = parseLayers( (Element) nl.get( i ), layer, scaleHint );
        }

        // set child layers
        layer.setLayer( layers );

        return layer;
    }

    /**
     * 
     * @param layerElem
     * @return the data sources
     * @throws XMLParsingException
     */
    protected AbstractDataSource[] parseDataSources( Element layerElem, String layerName,
                                                    ScaleHint scaleHint )
                            throws XMLParsingException {

        List nl = XMLTools.getNodes( layerElem, "./deegreewms:DataSource", nsContext );

        AbstractDataSource[] ds = new AbstractDataSource[nl.size()];
        for ( int i = 0; i < ds.length; i++ ) {
            boolean failOnEx = XMLTools.getNodeAsBoolean( (Node) nl.get( i ), "./@failOnException",
                                                          nsContext, true );
            boolean queryable = XMLTools.getNodeAsBoolean( (Node) nl.get( i ), "./@queryable",
                                                           nsContext, false );
            QualifiedName name = XMLTools.getNodeAsQualifiedName( (Node) nl.get( i ),
                                                                  "./deegreewms:Name/text()",
                                                                  nsContext,
                                                                  new QualifiedName( layerName ) );
            String stype = XMLTools.getRequiredNodeAsString( (Node) nl.get( i ),
                                                             "./deegreewms:Type", nsContext );

            int reqTimeLimit = XMLTools.getNodeAsInt( (Node) nl.get( i ),
                                                      "./deegreewms:RequestTimeLimit/text()",
                                                      nsContext, 30 );

            scaleHint = parseDSScaleHint( (Element) nl.get( i ), scaleHint );

            String s = "./deegreewms:OWSCapabilities/deegreewms:OnlineResource";
            Node node = XMLTools.getRequiredNode( (Node) nl.get( i ), s, nsContext );

            URL url = parseOnLineResource( (Element) node ).getLinkage().getHref();

            Geometry validArea = parseValidArea( (Node) nl.get( i ) );

            try {
                if ( "LOCALWFS".equals( stype ) ) {
                    ds[i] = createLocalWFSDataSource( (Node) nl.get( i ), failOnEx, queryable,
                                                      name, url, scaleHint, validArea, reqTimeLimit );
                } else if ( "LOCALWCS".equals( stype ) ) {
                    ds[i] = createLocalWCSDataSource( (Node) nl.get( i ), failOnEx, queryable,
                                                      name, url, scaleHint, validArea, reqTimeLimit );
                } else if ( "REMOTEWFS".equals( stype ) ) {
                    ds[i] = createRemoteWFSDataSource( (Node) nl.get( i ), failOnEx, queryable,
                                                       name, url, scaleHint, validArea,
                                                       reqTimeLimit );
                } else if ( "REMOTEWCS".equals( stype ) ) {
                    // int type = AbstractDataSource.REMOTEWCS;
                    // GetCoverage getCoverage =
                    parseWCSFilterCondition( (Node) nl.get( i ) );
                    // Color[] colors =
                    parseTransparentColors( (Node) nl.get( i ) );
                    // TODO
                    throw new XMLParsingException( "REMOTEWCS is not supported yet!" );
                } else if ( "REMOTEWMS".equals( stype ) ) {
                    ds[i] = createRemoteWMSDataSource( (Node) nl.get( i ), failOnEx, queryable,
                                                       name, url, scaleHint, validArea,
                                                       reqTimeLimit );
                } else {
                    throw new XMLParsingException(
                                                   "invalid DataSource type: "
                                                                           + stype
                                                                           + " defined "
                                                                           + "in deegree WMS configuration for DataSource: "
                                                                           + name );
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new XMLParsingException( "could not create service instance for WMS "
                                               + "datasource: " + name, e );
            }
        }

        return ds;
    }

    /**
     * parses the ScaleHint for a Datasource
     * @param layerElem
     * @param scaleHint
     * @return
     * @throws XMLParsingException
     */
    protected ScaleHint parseDSScaleHint( Element layerElem, ScaleHint scaleHint )
                            throws XMLParsingException {

        Node scNode = XMLTools.getNode( layerElem, "./deegreewms:ScaleHint", nsContext );
        if ( scNode != null ) {
            double mn = XMLTools.getNodeAsDouble( scNode, "./@min", nsContext, 0 );
            double mx = XMLTools.getNodeAsDouble( scNode, "./@max", nsContext, Double.MAX_VALUE );
            scaleHint = new ScaleHint( mn, mx );
        }

        if ( scaleHint == null ) {
            // set default value to avoid NullPointerException
            // when accessing a layers scalehint
            scaleHint = new ScaleHint( 0, Double.MAX_VALUE );
        }

        return scaleHint;
    }

    /**
     * returns the area a data source is valid. If the optional element <ValidArea>is not defined in
     * the configuration <code>null</code>.
     * 
     * @param node
     * @return the geometry
     * @throws Exception
     */
    private Geometry parseValidArea( Node node )
                            throws XMLParsingException {

        Geometry geom = null;

        List nl = XMLTools.getNodes( node, "./deegreewms:ValidArea/*", nsContext );
        if ( node != null ) {

            try {
                for ( int i = 0; i < nl.size(); i++ ) {

                    if ( ( (Node) nl.get( 0 ) ).getNamespaceURI().equals( GMLNS.toString() ) ) {

                        geom = GMLGeometryAdapter.wrap( (Element) nl.get( 0 ) );
                        break;
                    }
                }
            } catch ( GeometryException e ) {
                e.printStackTrace();
                throw new XMLParsingException( "couldn't parse/create valid aera of a datasource",
                                               e );
            }
        }

        return geom;
    }

    /**
     * @param node
     * @param failOnEx
     * @param queryable
     * @param name
     * @param geoProp
     * @param url
     * @param scaleHint
     * @throws Exception
     */
    private RemoteWMSDataSource createRemoteWMSDataSource( Node node, boolean failOnEx,
                                                          boolean queryable, QualifiedName name,
                                                          URL url, ScaleHint scaleHint,
                                                          Geometry validArea, int reqTimeLimit )
                            throws Exception {
        int type = AbstractDataSource.REMOTEWMS;

        String s = "./deegreewms:FeatureInfoTransformation/deegreewms:OnlineResource";
        Node fitNode = XMLTools.getNode( node, s, nsContext );
        URL fitURL = null;
        if ( fitNode != null ) {
            fitURL = parseOnLineResource( (Element) fitNode ).getLinkage().getHref();
        }

        GetMap getMap = parseWMSFilterCondition( node );
        Color[] colors = parseTransparentColors( node );
        WMSCapabilities wCapa = null;
        if ( capaCache.get( url ) != null ) {
            wCapa = (WMSCapabilities) capaCache.get( url );
        } else {
            WMSCapabilitiesDocument doc = new WMSCapabilitiesDocument();
            doc.load( url );
            wCapa = (WMSCapabilities) doc.parseCapabilities();
            capaCache.put( url, wCapa );
        }
        OGCWebService ows = new RemoteWMService( wCapa );

        return new RemoteWMSDataSource( queryable, failOnEx, name, type, ows, url, scaleHint,
                                        validArea, getMap, colors, fitURL, reqTimeLimit );
    }

    /**
     * @param node
     * @param failOnEx
     * @param queryable
     * @param name
     * @param geoProp
     * @param url
     * @param scaleHint
     * @throws Exception
     */
    private RemoteWFSDataSource createRemoteWFSDataSource( Node node, boolean failOnEx,
                                                          boolean queryable, QualifiedName name,
                                                          URL url, ScaleHint scaleHint,
                                                          Geometry validArea, int reqTimeLimit )
                            throws Exception {
        int type = AbstractDataSource.REMOTEWFS;
        String s = "./deegreewms:FeatureInfoTransformation/deegreewms:OnlineResource";
        Node fitNode = XMLTools.getNode( node, s, nsContext );
        URL fitURL = null;
        if ( fitNode != null ) {
            fitURL = parseOnLineResource( (Element) fitNode ).getLinkage().getHref();
        }
        Query query = parseWFSFilterCondition( node );

        WFSCapabilities wfsCapa = null;
        if ( capaCache.get( url ) != null ) {
            wfsCapa = (WFSCapabilities) capaCache.get( url );
        } else {
            WFSCapabilitiesDocument wfsDoc = new WFSCapabilitiesDocument();
            wfsDoc.load( url );
            wfsCapa = (WFSCapabilities) wfsDoc.parseCapabilities();
            capaCache.put( url, wfsCapa );
        }
        OGCWebService ows = new RemoteWFService( wfsCapa );
        //OGCWebService ows = null;

        Node geoPropNode = XMLTools.getNode( node, "deegreewms:GeometryProperty/text()", nsContext );
        QualifiedName geoProp = DEFAULT_GEO_PROP;
        if ( geoPropNode != null ) {
            geoProp = parseQualifiedName( geoPropNode );
        }

        return new RemoteWFSDataSource( queryable, failOnEx, name, type, geoProp, ows, url,
                                        scaleHint, validArea, query, fitURL, reqTimeLimit );

    }

    /**
     * @param node
     * @param failOnEx
     * @param queryable
     * @param name
     * @param geoProp
     * @param url
     * @param scaleHint
     * @throws Exception
     */
    private LocalWCSDataSource createLocalWCSDataSource( Node node, boolean failOnEx,
                                                        boolean queryable, QualifiedName name,
                                                        URL url, ScaleHint scaleHint,
                                                        Geometry validArea, int reqTimeLimit )
                            throws Exception {
        int type = AbstractDataSource.LOCALWCS;
        GetCoverage getCoverage = parseWCSFilterCondition( node );
        Color[] colors = parseTransparentColors( node );
        WCSConfiguration configuration = null;
        if ( capaCache.get( url ) != null ) {
            configuration = (WCSConfiguration) capaCache.get( url );
        } else {
            configuration = WCSConfiguration.create( url );
            capaCache.put( url, configuration );
        }

        OGCWebService ows = new WCService( configuration );

        return new LocalWCSDataSource( queryable, failOnEx, name, type, ows, url, scaleHint,
                                       validArea, getCoverage, colors, reqTimeLimit );
    }

    /**
     * @param node
     * @param failOnEx
     * @param queryable
     * @param name
     * @param geoProp
     * @param url
     * @param scaleHint
     * @throws Exception
     */
    private LocalWFSDataSource createLocalWFSDataSource( Node node, boolean failOnEx,
                                                        boolean queryable, QualifiedName name,
                                                        URL url, ScaleHint scaleHint,
                                                        Geometry validArea, int reqTimeLimit )
                            throws Exception {
        int type = AbstractDataSource.LOCALWFS;
        String s = "./deegreewms:FeatureInfoTransformation/deegreewms:OnlineResource";
        Node fitNode = XMLTools.getNode( node, s, nsContext );
        URL fitURL = null;
        if ( fitNode != null ) {
            fitURL = parseOnLineResource( (Element) fitNode ).getLinkage().getHref();
        }
        Query query = parseWFSFilterCondition( node );
        WFSConfiguration wfsCapa = null;
        if ( capaCache.get( url ) != null ) {
            wfsCapa = (WFSConfiguration) capaCache.get( url );
        } else {
            WFSConfigurationDocument wfsDoc = new WFSConfigurationDocument();
            wfsDoc.load( url );
            wfsCapa = wfsDoc.getConfiguration();
            // wfsCapa = new WFSCapabilitiesDocument( url ).createCapabilities();
            capaCache.put( url, wfsCapa );
        }
        // OGCWebService ows = WFServiceFactory.getUncachedService( wfsCapa );
        OGCWebService ows = WFServiceFactory.createInstance( wfsCapa );

        Node geoPropNode = XMLTools.getNode( node, "deegreewms:GeometryProperty/text()", nsContext );
        QualifiedName geoProp = DEFAULT_GEO_PROP;
        if ( geoPropNode != null ) {
            geoProp = parseQualifiedName( geoPropNode );
        }

        LOG.logDebug( "geometry property", geoProp );

        return new LocalWFSDataSource( queryable, failOnEx, name, type, geoProp, ows, url,
                                       scaleHint, validArea, query, fitURL, reqTimeLimit );
    }

    /**
     * @param nl
     * @param type
     * @throws XMLParsingException
     */
    private Color[] parseTransparentColors( Node node )
                            throws XMLParsingException {

        String s = "./deegreewms:TransparentColors/deegreewms:Color";
        List clnl = XMLTools.getNodes( node, s, nsContext );
        Color[] colors = new Color[clnl.size()];
        for ( int j = 0; j < colors.length; j++ ) {
            colors[j] = Color.decode( XMLTools.getStringValue( (Node) clnl.get( j ) ) );
        }

        return colors;
    }

    /**
     * 
     * @param node
     * @return the query
     * @throws XMLParsingException
     */
    private Query parseWFSFilterCondition( Node node )
                            throws XMLParsingException {

        Query o = null;

        Node queryNode = XMLTools.getNode( node, "./deegreewms:FilterCondition/wfs:Query",
                                           nsContext );
        if ( queryNode != null ) {
            try {
                o = Query.create( (Element) queryNode );
            } catch ( Exception e ) {
                throw new XMLParsingException( StringTools.stackTraceToString( e ) );
            }
        }

        return o;
    }

    /**
     * 
     * @param node
     * @return the request
     * @throws XMLParsingException
     */
    private GetCoverage parseWCSFilterCondition( Node node )
                            throws XMLParsingException {

        GetCoverage o = null;

        String id = "" + IDGenerator.getInstance().generateUniqueID();

        StringBuffer sd = new StringBuffer( 1000 );
        sd.append( "version=1.0.0&Coverage=%default%&" );
        sd.append( "CRS=EPSG:4326&BBOX=0,0,1,1&Width=1" );
        sd.append( "&Height=1&Format=%default%&" );
        String s = XMLTools.getNodeAsString( node,
                                             "./deegreewms:FilterCondition/deegreewms:WCSRequest",
                                             nsContext, "" );
        sd.append( s );
        try {
            o = GetCoverage.create( id, sd.toString() );
        } catch ( Exception e ) {
            throw new XMLParsingException( "could not create GetCoverage from WMS FilterCondition",
                                           e );
        }

        return o;
    }

    /**
     * 
     * @param node
     * @return the request
     * @throws XMLParsingException
     */
    private GetMap parseWMSFilterCondition( Node node )
                            throws XMLParsingException {

        GetMap o = null;

        String id = "" + IDGenerator.getInstance().generateUniqueID();

        StringBuffer sd = new StringBuffer( 1000 );
        sd.append( "REQUEST=GetMap&LAYERS=%default%&" );
        sd.append( "STYLES=&SRS=EPSG:4326&BBOX=0,0,1,1&WIDTH=1&" );
        sd.append( "HEIGHT=1&FORMAT=%default%" );
        Map<String, String> map1 = KVP2Map.toMap( sd.toString() );
        String s = XMLTools.getRequiredNodeAsString(
                                                     node,
                                                     "./deegreewms:FilterCondition/deegreewms:WMSRequest",
                                                     nsContext );
        Map<String, String> map2 = KVP2Map.toMap( s );
        if ( map2.get( "VERSION" ) == null && map2.get( "WMTVER" ) == null ) {
            map2.put( "VERSION", "1.1.1" );
        }
        // if no service is set use WMS as default
        if ( map2.get( "SERVICE" ) == null ) {
            map2.put( "SERVICE", "WMS" );
        }
        map1.putAll( map2 );
        try {
            map1.put( "ID", id );
            o = GetMap.create( map1 );
        } catch ( Exception e ) {
            throw new XMLParsingException( "could not create GetCoverage from WMS FilterCondition",
                                           e );
        }

        return o;
    }

    /**
     * 
     * @param layerElem
     * @throws XMLParsingException
     */
    @Override
    protected Style[] parseStyles( Element layerElem )
                            throws XMLParsingException {

        List nl = XMLTools.getNodes( layerElem, "./Style", nsContext );
        Style[] styles = new Style[nl.size()];
        for ( int i = 0; i < styles.length; i++ ) {
            String name = XMLTools.getRequiredNodeAsString( (Node) nl.get( i ), "./Name", nsContext );
            String title = XMLTools.getNodeAsString( (Node) nl.get( i ), "./Title", nsContext, null );
            String styleAbstract = XMLTools.getNodeAsString( (Node) nl.get( i ), "./Abstract",
                                                             nsContext, null );
            LegendURL[] legendURLs = parseLegendURL( (Node) nl.get( i ) );
            StyleURL styleURL = parseStyleURL( (Node) nl.get( i ) );
            StyleSheetURL styleSheetURL = parseStyleSheetURL( (Node) nl.get( i ) );
            String styleResource = XMLTools.getNodeAsString( (Node) nl.get( i ),
                                                             "deegreewms:StyleResource", nsContext,
                                                             "styles.xml" );
            URL sr = null;
            try {
                sr = resolve( styleResource );
            } catch ( MalformedURLException e ) {
                throw new XMLParsingException( "could not parse style resource of style: " + name,
                                               e );
            }
            styles[i] = new Style( name, title, styleAbstract, legendURLs, styleSheetURL, styleURL,
                                   sr );
        }

        return styles;
    }

}

/* ***********************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WMSConfigurationDocument.java,v $
 * Revision 1.52  2006/11/30 20:05:08  poth
 * support useless gazetteer parameters removed
 *
 * Revision 1.51  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.50  2006/09/27 13:51:43  poth
 * bug fix - considering scalehints for datasources
 *
 * Revision 1.49  2006/09/08 08:42:01  schmitz
 * Updated the WMS to be 1.1.1 conformant once again.
 * Cleaned up the WMS code.
 * Added cite WMS test data.
 *
 * Revision 1.48  2006/09/05 11:57:27  schmitz
 * Added a constant for app namespace.
 * Using XMLFragment parsing method for QualifiedNames.
 *
 * Revision 1.47  2006/08/23 07:10:21  schmitz
 * Renamed the owscommon_neu package to owscommon_new.
 *
 * Revision 1.46  2006/08/22 10:25:01  schmitz
 * Updated the WMS to use the new OWS common package.
 * Updated the rest of deegree to use the new data classes returned
 * by the updated WMS methods/capabilities.
 *
 * Revision 1.45  2006/08/06 19:51:45  poth
 * bug fix - reading datastore timelimit from configuration
 *
 * Revision 1.44  2006/08/02 14:17:38  poth
 * *** empty log message ***
 *
 *  Revision 1.43  2006/07/27 13:08:46  poth
 *  support for request time limit added for each datasource added
 * 
 *  Revision 1.42  2006/07/12 16:59:32  poth
 *  required adaptions according to renaming of OnLineResource to OnlineResource
 * 
 *  Revision 1.41  2006/07/04 20:56:15  poth
 *  bug fix - creating default geometry property for WFS datastores
 * 
 *  Revision 1.40  2006/05/31 13:04:11  taddei
 *  added GMLNS.toString() because equals was failing
 * 
 *  Revision 1.39  2006/04/25 07:14:48  poth
 *  *** empty log message ***
 * 
 *  Revision 1.38  2006/04/24 08:03:12  poth
 *  *** empty log message ***
 * 
 *  Revision 1.37  2006/04/06 20:25:25  poth
 *  *** empty log message ***
 * 
 *  Revision 1.36  2006/04/04 20:39:41  poth
 *  *** empty log message ***
 * 
 *  Revision 1.35  2006/03/30 21:20:25  poth
 *  *** empty log message ***
 * 
 *  Revision 1.34  2006/02/05 20:33:09  poth
 *  *** empty log message ***
 * 
 *  Revision 1.33  2006/02/04 14:36:40  poth
 *  *** empty log message ***
 * 
 *  Revision 1.32  2006/01/11 16:56:48  poth
 *  *** empty log message ***
 * 
 *  Revision 1.31  2005/12/21 17:30:10  poth
 *  no message
 * 
 *  Revision 1.30  2005/12/20 09:09:09  poth
 *  no message
 * 
 *  Revision 1.29  2005/11/30 16:30:41  poth
 *  no message
 * 
 *  Revision 1.28  2005/11/22 17:19:13  poth
 *  no message
 * 
 *  Revision 1.27  2005/11/17 08:14:51  deshmukh
 *  Renamed nsNode to nsContext
 * 
 *  Revision 1.26  2005/11/16 13:45:00  mschneider
 *  Merge of wfs development branch.
 * 
 *  Revision 1.25.2.3  2005/11/07 16:25:57  deshmukh
 *  NodeList to List
 * 
 *  Revision 1.25.2.2  2005/11/07 15:38:04  mschneider
 *  Refactoring: use NamespaceContext instead of Node for namespace bindings.
 * 
 *  Revision 1.25.2.1 2005/11/07 13:09:26
 * deshmukh  Switched namespace definitions
 * in "CommonNamespaces" to URI.  Changes to
 * this class. What the people have been up to: Revision 1.25 2005/10/18 08:07:17 poth Changes to
 * this class. What the people have been up to: no message Changes to this class. What the people
 * have been up to:  Revision 1.24 2005/09/27
 * 19:53:19 poth  no message Changes to this
 * class. What the people have been up to: 
 * Revision 1.23 2005/09/15 09:51:45 poth  no
 * message  Changes to this class. What the
 * people have been up to: Revision 1.22 2005/09/05 09:11:00 mschneider Changes to this class. What
 * the people have been up to: Refactored due to removal of obsolete element "rootDirectory" from
 * "deegreeParams" section.  Changes to this
 * class. What the people have been up to: Revision 1.21 2005/09/02 12:22:23 mschneider Changes to
 * this class. What the people have been up to: Iimproved exception handling and logging. Changes to
 * this class. What the people have been up to: Changes to this class. What the people have been up
 * to: Revision 1.20 2005/08/29 17:19:52 mschneider Changes to this class. What the people have been
 * up to: *** empty log message ***  Changes
 * to this class. What the people have been up to: Revision 1.19 2005/08/28 11:51:45 poth Changes to
 * this class. What the people have been up to: no message Changes to this class. What the people
 * have been up to:  Revision 1.18 2005/08/26
 * 21:13:02 poth  no message Changes to this
 * class. What the people have been up to: 
 * Revision 1.17 2005/08/23 13:44:01 mschneider Changes to this class. What the people have been up
 * to: Refactored due to new XSLTDocument class. Changes to this class. What the people have been up
 * to:  Revision 1.16 2005/08/09 16:38:46
 * poth  no message Changes to this class.
 * What the people have been up to:  Revision
 * 1.15 2005/08/09 15:48:24 poth  no message
 *  Changes to this class. What the people
 * have been up to: Revision 1.14 2005/08/05 09:42:20 poth Changes to this class. What the people
 * have been up to: no message  Changes to
 * this class. What the people have been up to: Revision 1.13 2005/08/04 12:21:18 poth Changes to
 * this class. What the people have been up to: no message Changes to this class. What the people
 * have been up to:  Revision 1.12 2005/08/04
 * 08:56:58 poth  no message Changes to this
 * class. What the people have been up to: 
 * Revision 1.11 2005/08/01 20:19:01 mschneider Changes to this class. What the people have been up
 * to: Fixed handling of relative paths. 
 * Revision 1.10 2005/07/22 16:19:56 poth no message
 * 
 * Revision 1.9 2005/07/14 15:31:53 mschneider Removed obsolete catch block.
 * 
 * Revision 1.8 2005/07/07 15:54:40 poth no message
 * 
 * Revision 1.7 2005/06/28 15:58:11 poth no message
 * 
 * Revision 1.6 2005/06/27 09:24:17 poth no message
 * 
 * Revision 1.5 2005/06/27 08:59:06 poth no message
 * 
 * Revision 1.4 2005/06/24 16:36:44 poth no message
 * 
 * Revision 1.3 2005/06/23 15:45:10 poth no message
 * 
 * Revision 1.2 2005/06/22 15:33:00 poth no message
 * 
 *********************************************************************************************** */
