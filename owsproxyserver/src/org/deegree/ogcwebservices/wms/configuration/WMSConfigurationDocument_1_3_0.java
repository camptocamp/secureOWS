//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/WMSConfigurationDocument_1_3_0.java,v 1.5 2006/11/30 20:05:08 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wms.configuration;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.deegree.ogcwebservices.wms.capabilities.FeatureListURL;
import org.deegree.ogcwebservices.wms.capabilities.Identifier;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.LayerBoundingBox;
import org.deegree.ogcwebservices.wms.capabilities.LegendURL;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;
import org.deegree.ogcwebservices.wms.capabilities.Style;
import org.deegree.ogcwebservices.wms.capabilities.StyleSheetURL;
import org.deegree.ogcwebservices.wms.capabilities.StyleURL;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument_1_3_0;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;
import org.deegree.owscommon_new.ServiceProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <code>WMSConfigurationDocument_1_3_0</code> is the parser class for a WMS 1.3.0
 * configuration document.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/11/30 20:05:08 $
 * 
 * @since 2.0
 */

public class WMSConfigurationDocument_1_3_0 extends WMSCapabilitiesDocument_1_3_0 {

    private static final long serialVersionUID = -2304421871404603016L;

    private static Map<URL, OGCCapabilities> capaCache = new HashMap<URL, OGCCapabilities>();

    private static final String XML_TEMPLATE = "WMSConfigurationTemplate_1_3_0.xml";

    private static final String XSLT_TEMPLATE_NAME = "WMSConfigurationTransform_1_3_0.xsl";

    private static XSLTDocument XSLT_TEMPLATE;

    private static final ILogger LOG = LoggerFactory.getLogger( WMSConfigurationDocument_1_3_0.class );

    private static final String PWMS = CommonNamespaces.WMS_PREFIX + ":";

    private static final QualifiedName DEFAULT_GEO_PROP = new QualifiedName(
                                                                             "app",
                                                                             "GEOM",
                                                                             CommonNamespaces.buildNSURI( "http://www.deegree.org/app" ) );
    static {
        XSLT_TEMPLATE = new XSLTDocument();
        try {
            XSLT_TEMPLATE.load( WMSConfigurationDocument_1_3_0.class.getResource( XSLT_TEMPLATE_NAME ) );
        } catch ( Exception e ) {
            BootLogger.logError( "Error loading XSLT sheet in WMSConfigurationDocument_1_3_0.", e );
        }
    }

    @Override
    public void createEmptyDocument()
                            throws IOException, SAXException {
        URL url = WMSConfigurationDocument_1_3_0.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * Added the prefix.
     * @return the parsed configuration
     * @throws InvalidConfigurationException 
     * @throws XMLParsingException 
     */
    public WMSConfiguration_1_3_0 parseConfiguration()
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
        WMSDeegreeParams params = null;
        Element root = getRootElement();
        String version = XMLTools.getRequiredNodeAsString( root, "@version", nsContext );
        String updateSeq = XMLTools.getNodeAsString( root, "@updateSequence", nsContext, null );

        int layerLimit = 0;

        try {
            Node node = XMLTools.getRequiredNode( root, "./deegreewms:DeegreeParam", nsContext );
            params = parseDeegreeParams( node );

            Element serviceElement = (Element) XMLTools.getRequiredNode( root, PWMS + "Service",
                                                                         nsContext );

            layerLimit = XMLTools.getNodeAsInt( serviceElement, PWMS + "LayerLimit", nsContext, 0 );
            int maxWidth = XMLTools.getNodeAsInt( serviceElement, PWMS + "MaxWidth", nsContext, 0 );
            int maxHeight = XMLTools.getNodeAsInt( serviceElement, PWMS + "MaxHeight", nsContext, 0 );

            params.setMaxMapHeight( maxHeight );
            params.setMaxMapWidth( maxWidth );

            serviceIdentification = parseServiceIdentification();
            serviceProvider = parseServiceProvider();
            metadata = parseOperationsMetadata();

            Element layerElem = (Element) XMLTools.getRequiredNode(
                                                                    getRootElement(),
                                                                    PWMS
                                                                                            + "Capability/"
                                                                                            + PWMS
                                                                                            + "Layer",
                                                                    nsContext );
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

        WMSConfiguration_1_3_0 wmsConfiguration = new WMSConfiguration_1_3_0(
                                                                              version,
                                                                              updateSeq,
                                                                              serviceIdentification,
                                                                              serviceProvider,
                                                                              metadata, layer,
                                                                              params,
                                                                              getSystemId(),
                                                                              layerLimit );
        
        return wmsConfiguration;
    }

    /**
     * Creates a class representation of the <code>deegreeParams</code>- section.
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
        //        int maxMapWidth = XMLTools.getNodeAsInt( root, "./deegreewms:MaxMapWidth", nsContext, 1000 );
        //        int maxMapHeight = XMLTools.getNodeAsInt( root, "./deegreewms:MaxMapHeight", nsContext, 1000 );
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
            featureSchemaNamespace = XMLTools.getRequiredNodeAsString( root, "deegreewms:FeatureInfoSchema/deegreewms:Namespace", nsContext );
            elem = (Element) XMLTools.getRequiredNode( root, "deegreewms:FeatureInfoSchema/deegreewms:OnlineResource", nsContext );
            OnlineResource link = parseOnLineResource( elem );
            featureSchemaLocation = link.getLinkage().getHref();
        }
        
        boolean antiAliased = XMLTools.getNodeAsBoolean( root, "./deegreewms:AntiAliased",
                                                         nsContext, true );
      
        Proxy proxy = parseProxy( root );

        List<String> supportedVersions = parseSupportedVersions( root );

        WMSDeegreeParams deegreeParams = new WMSDeegreeParams( cache, maxLifeTime, reqTimeLimit,
                                                               (float) mapQuality, ol, 0, 0,
                                                               antiAliased, featureInfoRadius,
                                                               copyright, null, dtdLocation, proxy,
                                                               supportedVersions, featureSchemaLocation,
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

    /*
     * Removed Extent.
     * Added prefix.
     * Changed SRS to CRS.
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
        String name = XMLTools.getNodeAsString( layerElem, PWMS + "Name", nsContext, null );
        String title = XMLTools.getRequiredNodeAsString( layerElem, PWMS + "Title", nsContext );
        String layerAbstract = XMLTools.getNodeAsString( layerElem, PWMS + "Abstract", nsContext,
                                                         null );
        String[] keywords = XMLTools.getNodesAsStrings( layerElem, PWMS + "KeywordList/" + PWMS
                                                                   + "Keyword", nsContext );
        String[] srs = XMLTools.getNodesAsStrings( layerElem, PWMS + "CRS", nsContext );

        List nl = XMLTools.getNodes( layerElem, PWMS + "BoundingBox", nsContext );
        // TODO
        // substitue with Envelope
        LayerBoundingBox[] bboxes = null;
        if ( nl.size() == 0 && parent != null ) {
            // inherit BoundingBoxes from parent layer
            bboxes = parent.getBoundingBoxes();
        } else {
            bboxes = parseLayerBoundingBoxes( nl );
        }

        Element llBox = (Element) XMLTools.getNode( layerElem, PWMS + "EX_GeographicBoundingBox",
                                                    nsContext );
        Envelope llBoundingBox = null;
        if ( llBox == null && parent != null ) {
            // inherit LatLonBoundingBox parent layer            
            llBoundingBox = parent.getLatLonBoundingBox();
        } else {
            llBoundingBox = parseEX_GeographicBoundingBox( llBox );
        }

        Dimension[] dimensions = parseDimensions( layerElem );
        //        Extent[] extents = parseExtents( layerElem );

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
                                 keywords, srs, bboxes, dimensions, null, authorityURLs,
                                 identifiers, metadataURLs, dataURLs, featureListURLs, styles,
                                 null, ds, parent );

        // get Child layers
        nl = XMLTools.getNodes( layerElem, PWMS + "Layer", nsContext );
        Layer[] layers = new Layer[nl.size()];
        for ( int i = 0; i < layers.length; i++ ) {
            layers[i] = parseLayers( (Element) nl.get( i ), layer, scaleHint );
        }

        // set child layers
        layer.setLayer( layers );

        
        return layer;
    }

    /*
     * Added the prefix.
     */
    @Override
    protected Style[] parseStyles( Element layerElem )
                            throws XMLParsingException {
        

        List nl = XMLTools.getNodes( layerElem, PWMS + "Style", nsContext );
        Style[] styles = new Style[nl.size()];
        for ( int i = 0; i < styles.length; i++ ) {
            String name = XMLTools.getRequiredNodeAsString( (Node) nl.get( i ), PWMS + "Name",
                                                            nsContext );
            String title = XMLTools.getNodeAsString( (Node) nl.get( i ), PWMS + "Title", nsContext,
                                                     null );
            String styleAbstract = XMLTools.getNodeAsString( (Node) nl.get( i ), PWMS + "Abstract",
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
                    //int type = AbstractDataSource.REMOTEWCS;
                    //GetCoverage getCoverage = 
                    parseWCSFilterCondition( (Node) nl.get( i ) );
                    //Color[] colors = 
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
                e.printStackTrace();
                throw new XMLParsingException(
                                               "could not create service instance for WMS datasource: "
                                                                       + name, e );
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
     * @return the geom
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
     * @return the request object
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
     * @return the request object
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

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WMSConfigurationDocument_1_3_0.java,v $
 Revision 1.5  2006/11/30 20:05:08  poth
 support useless gazetteer parameters removed

 Revision 1.4  2006/11/29 15:09:05  schmitz
 Moved more messages to central location, added warning message if double layers are defined.

 Revision 1.3  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.2  2006/09/27 13:51:43  poth
 bug fix - considering scalehints for datasources

 Revision 1.1  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.



 ********************************************************************** */