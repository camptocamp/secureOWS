//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/XMLFactory.java,v 1.31 2006/10/22 20:32:08 poth Exp $
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
package org.deegree.ogcwebservices.wms;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.MetadataURL;
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
import org.deegree.ogcwebservices.wms.capabilities.LogoURL;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;
import org.deegree.ogcwebservices.wms.capabilities.Style;
import org.deegree.ogcwebservices.wms.capabilities.StyleSheetURL;
import org.deegree.ogcwebservices.wms.capabilities.StyleURL;
import org.deegree.ogcwebservices.wms.capabilities.UserDefinedSymbolization;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities_1_3_0;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;
import org.deegree.owscommon_new.ServiceProvider;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0. $Revision: 1.31 $, $Date: 2006/10/22 20:32:08 $
 * 
 * @since 2.0
 */
public class XMLFactory extends org.deegree.owscommon.XMLFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * Exports a <code>WMSCapabilities</code> instance to a <code>WMSCapabilitiesDocument</code>.
     * 
     * @param capabilities
     * @return DOM representation of the <code>WMSCapabilities</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static WMSCapabilitiesDocument export( WMSCapabilities capabilities )
                            throws IOException {

        // in case of a 1.3.0 capabilities, switch the method
        if( capabilities instanceof WMSCapabilities_1_3_0 ) {
            return XMLFactory_1_3_0.export( (WMSCapabilities_1_3_0) capabilities );
        }
        
        WMSCapabilitiesDocument capabilitiesDocument = new WMSCapabilitiesDocument();
        try {
            capabilitiesDocument.createEmptyDocument();
            Element root = capabilitiesDocument.getRootElement();

            root.setAttribute( "version", "1.1.1" );
            root.setAttribute( "updateSequence", capabilities.getUpdateSequence() );

            // XXXsyp added this line, but not working??
            root.setAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink/" );

            appendService( root, capabilities.getServiceIdentification(),
                           capabilities.getServiceProvider() );

            appendCapabilityRequests( root,
                                      capabilities.getOperationMetadata() );

            appendUserDefinedSymbolization( (Element) XMLTools.getNode( root, "./Capability",
                                                                        nsContext ),
                                            capabilities.getUserDefinedSymbolization() );

            appendCapabilityLayer( (Element) XMLTools.getNode( root, "./Capability", nsContext ),
                                   capabilities.getLayer() );

        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
        }

        return capabilitiesDocument;
    }

    /**
     * This method exports a 1.3.0 capabilities document as 1.1.1, providing backward
     * compatibility.
     * 
     * @param capabilities
     * @return the 1.1.1 document
     * @throws IOException
     */
    public static WMSCapabilitiesDocument exportAs_1_1_1( WMSCapabilities_1_3_0 capabilities )
                                                                             throws IOException {
        WMSCapabilitiesDocument doc = new WMSCapabilitiesDocument();

        try {
            doc.createEmptyDocument();

            Element root = doc.getRootElement();

            root.setAttribute( "version", "1.1.1" );
            root.setAttribute( "updateSequence", capabilities.getUpdateSequence() );

            appendService( root, capabilities.getServiceIdentification(),
                           capabilities.getServiceProvider() );

            appendCapabilityRequests( root,
                                      capabilities.getOperationMetadata() );

            // we don't have that one in 1.3.0 data
//            appendUserDefinedSymbolization( (Element) XMLTools.getNode( root, "./Capability",
//                                                                        nsContext ),
//                                            capabilities.getUserDefinedSymbolization() );

            appendCapabilityLayer( (Element) XMLTools.getNode( root, "./Capability", nsContext ),
                                   capabilities.getLayer() );

        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        }
        
        return doc;
    }
    
    /**
     * 
     * @param root
     * @param uds
     */
    protected static void appendUserDefinedSymbolization( Element root, UserDefinedSymbolization uds ) {

        Element elem = XMLTools.appendElement( root, null, "UserDefinedSymbolization" );
        elem.setAttribute( "SupportSLD", boolean2Number( uds.isSldSupported() ) );
        elem.setAttribute( "UserLayer", boolean2Number( uds.isUserLayerSupported() ) );
        elem.setAttribute( "UserStyle", boolean2Number( uds.isUserStyleSupported() ) );
        elem.setAttribute( "RemoteWFS", boolean2Number( uds.isRemoteWFSSupported() ) );

    }

    private static String boolean2Number( boolean bool ) {
        if ( bool ) {
            return "1";
        }
        return "0";
    }

    /**
     * 
     * @param root
     * @param identification
     * @param provider
     * @throws XMLParsingException
     */
    protected static void appendService( Element root, ServiceIdentification identification,
                                        ServiceProvider provider )
                            throws XMLParsingException {

        root = (Element) XMLTools.getRequiredNode( root, "./Service", nsContext );

        Element node = (Element) XMLTools.getRequiredNode( root, "./Name", nsContext );
        // is this actually desired?
        node.setTextContent( "OGC:WMS" );

        node = (Element) XMLTools.getRequiredNode( root, "./Title", nsContext );
        node.setTextContent( identification.getTitle() );

        String serviceAbstract = identification.getAbstractString();
        if ( serviceAbstract != null ) {
            XMLTools.appendElement( root, null, "Abstract", serviceAbstract );
        }

        List<Keywords> keywords = identification.getKeywords();
        if ( keywords.size() > 0 ) {
            String[] kw = keywords.get( 0 ).getKeywords();
            Element kwl = XMLTools.appendElement( root, null, "KeywordList" );
            for ( int i = 0; i < kw.length; i++ ) {
                XMLTools.appendElement( kwl, null, "Keyword", kw[i] );
            }
        }

        node = root.getOwnerDocument().createElement( "OnlineResource" );

        OnlineResource sLink = provider.getProviderSite();
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource( node, sLink );
        
        root.appendChild( node );

        appendContactInformation( root, provider );

        String fee = null;
        if( identification.getAccessConstraints().size() > 0 )
            fee = identification.getAccessConstraints().get( 0 ).getFees();
        if ( fee != null ) {
            XMLTools.appendElement( root, null, "Fees", fee );
        } else {
            XMLTools.appendElement( root, null, "Fees", "none" );
        }

        if ( identification.getAccessConstraints().size() > 0 ) {
            XMLTools.appendElement( root, null, "AccessConstraints",
                                    identification.getAccessConstraints().get( 0 ).getUseLimitations().get( 0 ) );
        } else {
            XMLTools.appendElement( root, null, "AccessConstraints", "none" );
        }

    }

    /**
     * 
     * @param root
     * @param provider
     * @throws XMLParsingException
     */
    protected static void appendContactInformation( Element root, ServiceProvider provider ) {

        Element ciNode = XMLTools.appendElement( root, null, "ContactInformation" );
        Element cppNode = XMLTools.appendElement( ciNode, null, "ContactPersonPrimary" );
        if ( provider.getServiceContact().getIndividualName().length > 0 ) {
            XMLTools.appendElement( cppNode, null, "ContactPerson",
                                    provider.getServiceContact().getIndividualName()[0] );
        }
        if ( provider.getServiceContact().getOrganisationName().length > 0 ) {
            XMLTools.appendElement( cppNode, null, "ContactOrganization",
                                    provider.getServiceContact().getOrganisationName()[0] );
        }
        if ( provider.getServiceContact().getPositionName().length > 0 ) {
            XMLTools.appendElement( ciNode, null, "ContactPosition",
                                    provider.getServiceContact().getPositionName()[0] );
        }
        Element caNode = XMLTools.appendElement( ciNode, null, "ContactAddress" );

        XMLTools.appendElement( caNode, null, "AddressType", "postal" );

        /* XXXsyp issues with serviceContact XML encoding
        if ( provider.getServiceContact().getContactInfo().length > 0 ) {
            Address addr = provider.getServiceContact().getContactInfo()[0].getAddress();
            String[] dp = addr.getDeliveryPoint();
            if ( dp.length > 0 ) {
                XMLTools.appendElement( caNode, null, "Address", dp[0] );
            }
            if ( addr.getCity() != null ) {
                XMLTools.appendElement( caNode, null, "City", addr.getCity() );
            }
            if ( addr.getAdministrativeArea() != null ) {
                XMLTools.appendElement( caNode, null, "StateOrProvince",
                                        addr.getAdministrativeArea() );
            }
            if ( addr.getPostalCode() != null ) {
                XMLTools.appendElement( caNode, null, "PostCode", addr.getPostalCode() );
            }
            if ( addr.getCountry() != null ) {
                XMLTools.appendElement( caNode, null, "Country", addr.getCountry() );
            }

            Phone phone = provider.getServiceContact().getContactInfo()[0].getPhone();
            if ( phone.getVoice().length > 0 ) {
                XMLTools.appendElement( ciNode, null, "ContactVoiceTelephone", phone.getVoice()[0] );
            }
            if ( phone.getFacsimile().length > 0 ) {
                XMLTools.appendElement( ciNode, null, "ContactFacsimileTelephone",
                                        phone.getFacsimile()[0] );
            }
            if ( addr.getElectronicMailAddress().length > 0 ) {
                XMLTools.appendElement( ciNode, null, "ContactElectronicMailAddress",
                                        addr.getElectronicMailAddress()[0] );
            }
        }
        */

    }

    /**
     * 
     * @param root
     * @param operationsMetadata
     * @throws XMLParsingException
     */
    protected static void appendCapabilityRequests( Element root,
                                                   OperationsMetadata operationsMetadata )
                            throws XMLParsingException {

        root = (Element) XMLTools.getRequiredNode( root, "./Capability/Request", nsContext );

        operationsMetadata.getOperations();

        // just append all operations
        for( Operation operation : operationsMetadata.getOperations() ) {
            appendOperation( root, operation );
        }

    }

    /**
     * 
     * @param root
     * @param operation
     * @throws XMLParsingException
     */
    protected static void appendOperation( Element root, Operation operation ) {


        String name = operation.getName().getLocalName();

        root = XMLTools.appendElement( root, null, name );

        DomainType odt = (DomainType) operation.getParameter( new QualifiedName( "Format" ) );
        
        List<TypedLiteral> values = odt.getValues();
        for( TypedLiteral value : values ) 
            XMLTools.appendElement( root, null, "Format", value.getValue() );

        List<DCP> dcps = operation.getDCP();
        for( DCP dcp : dcps ) {
            Element http = XMLTools.appendElement( root, null, "DCPType" );
            http = XMLTools.appendElement( http, null, "HTTP" );
            HTTP ht = (HTTP) dcp;
            List<HTTP.Type> types = ht.getTypes();
            List<OnlineResource> links = ht.getLinks();
            for( int i = 0; i < types.size(); ++i ) {
                Element elem = null;
                if( types.get( i ) == HTTP.Type.Get )
                    elem = XMLTools.appendElement( http, null, "Get" );
                if( types.get( i ) == HTTP.Type.Post )
                    elem = XMLTools.appendElement( http, null, "Post" );
                if( elem != null ) {
                    elem = XMLTools.appendElement( elem, null, "OnlineResource" );
                    org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource( elem, links.get( i ) );
                }
            }
        }

    }

    /**
     * 
     * @param root
     * @param layer
     * @throws XMLParsingException
     */
    protected static void appendCapabilityLayer( Element root, Layer layer )
                            throws XMLParsingException {

        root = XMLTools.appendElement( root, null, "Layer" );
        root.setAttribute( "queryable", boolean2Number( layer.isQueryable() ) );
        root.setAttribute( "cascaded", Integer.toString( layer.getCascaded() ) );
        root.setAttribute( "opaque", boolean2Number( layer.isOpaque() ) );
        root.setAttribute( "noSubsets", boolean2Number( layer.hasNoSubsets() ) );
        if ( layer.getFixedWidth() > 0 ) {
            root.setAttribute( "fixedWidth", Integer.toString( layer.getFixedWidth() ) );
        }
        if ( layer.getFixedHeight() > 0 ) {
            root.setAttribute( "fixedHeight", Integer.toString( layer.getFixedHeight() ) );
        }

        if ( layer.getName() != null ) {
            XMLTools.appendElement( root, null, "Name", layer.getName() );
        }
        XMLTools.appendElement( root, null, "Title", layer.getTitle() );

        if ( layer.getAbstract() != null ) {
            XMLTools.appendElement( root, null, "Abstract", layer.getAbstract() );
        }

        String[] keywords = layer.getKeywordList();
        if ( keywords.length > 0 ) {
            Element elem = XMLTools.appendElement( root, null, "KeywordList" );
            for ( int i = 0; i < keywords.length; i++ ) {
                XMLTools.appendElement( elem, null, "Keyword", keywords[i] );
            }
        }

        String[] srs = layer.getSrs();
        for ( int i = 0; i < srs.length; i++ ) {
            XMLTools.appendElement( root, null, "SRS", srs[i] );
        }

        Envelope llBox = layer.getLatLonBoundingBox();
        appendLatLonBoundingBox( root, llBox );

        LayerBoundingBox[] lBoxes = layer.getBoundingBoxes();
        for ( int i = 0; i < lBoxes.length; i++ ) {
            appendLayerBoundingBox( root, lBoxes[i] );
        }

        Dimension[] dims = layer.getDimension();
        for ( int i = 0; i < dims.length; i++ ) {
            appendDimension( root, dims[i] );
        }

        Extent[] extents = layer.getExtent();
        for ( int i = 0; i < extents.length; i++ ) {
            appendExtent( root, extents[i] );
        }

        Attribution attr = layer.getAttribution();
        if ( attr != null ) {
            appendAttribution( root, attr );
        }

        AuthorityURL[] authorityURLs = layer.getAuthorityURL();
        for ( int i = 0; i < authorityURLs.length; i++ ) {
            appendAuthorityURL( root, authorityURLs[i] );
        }

        Identifier[] identifiers = layer.getIdentifier();
        for ( int i = 0; i < identifiers.length; i++ ) {
            appendIdentifier( root, identifiers[i] );
        }

        MetadataURL[] metadataURLs = layer.getMetadataURL();
        for ( int i = 0; i < metadataURLs.length; i++ ) {
            appendMetadataURL( root, metadataURLs[i] );
        }

        DataURL[] dataURLs = layer.getDataURL();
        for ( int i = 0; i < dataURLs.length; i++ ) {
            appendDataURL( root, dataURLs[i] );
        }

        FeatureListURL[] featureListURLs = layer.getFeatureListURL();
        for ( int i = 0; i < featureListURLs.length; i++ ) {
            appendFeatureListURL( root, featureListURLs[i] );
        }

        if ( layer.getName() != null && layer.getName().length() > 0 ) {
            Style[] styles = layer.getStyles();
            for ( int i = 0; i < styles.length; i++ ) {
                appendStyle( root, styles[i] );
            }
        }

        ScaleHint scaleHint = layer.getScaleHint();
        Element elem = XMLTools.appendElement( root, null, "ScaleHint" );
        elem.setAttribute( "min", "" + scaleHint.getMin() );
        elem.setAttribute( "max", "" + scaleHint.getMax() );

        Layer[] layers = layer.getLayer();
        for ( int i = 0; i < layers.length; i++ ) {
            appendCapabilityLayer( root, layers[i] );
        }

    }

    /**
     * 
     * @param root
     * @param style
     */
    protected static void appendStyle( Element root, Style style ) {

        String nm = style.getName();
        String tlt = style.getTitle();
        if ( nm.startsWith( "default:" ) ) {
            nm = "default";
            if ( tlt != null ) {
                tlt = StringTools.replace( tlt, "default:", "", false ) + " (default)";
            }
        }

        root = XMLTools.appendElement( root, null, "Style" );
        XMLTools.appendElement( root, null, "Name", nm );
        if ( style.getTitle() != null ) {
            XMLTools.appendElement( root, null, "Title", tlt );
        }
        if ( style.getAbstract() != null ) {
            XMLTools.appendElement( root, null, "Abstract", style.getAbstract() );
        }
        LegendURL[] legendURLs = style.getLegendURL();
        for ( int i = 0; i < legendURLs.length; i++ ) {
            appendLegendURL( root, legendURLs[i] );
        }

        StyleSheetURL styleSheetURL = style.getStyleSheetURL();
        if ( styleSheetURL != null ) {
            appendStyleSheetURL( root, styleSheetURL );
        }

        StyleURL styleURL = style.getStyleURL();
        if ( styleURL != null ) {
            appendStyleURL( root, styleURL );
        }

    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendStyleURL( Element root, StyleURL styleURL ) {
        Element elem = XMLTools.appendElement( root, null, "StyleURL" );
        XMLTools.appendElement( elem, null, "Format", styleURL.getFormat() );
        appendOnlineResource( elem, styleURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendStyleSheetURL( Element root, StyleSheetURL styleSheetURL ) {
        Element elem = XMLTools.appendElement( root, null, "StyleSheetURL" );
        XMLTools.appendElement( elem, null, "Format", styleSheetURL.getFormat() );
        appendOnlineResource( elem, styleSheetURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendLegendURL( Element root, LegendURL legendURL ) {
        Element elem = XMLTools.appendElement( root, null, "LegendURL" );
        elem.setAttribute( "width", "" + legendURL.getWidth() );
        elem.setAttribute( "height", "" + legendURL.getWidth() );
        XMLTools.appendElement( elem, null, "Format", legendURL.getFormat() );

        appendOnlineResource( elem, legendURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendFeatureListURL( Element root, FeatureListURL featureListURL ) {
        Element elem = XMLTools.appendElement( root, null, "FeatureListURL" );
        XMLTools.appendElement( elem, null, "Format", featureListURL.getFormat() );
        appendOnlineResource( elem, featureListURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendDataURL( Element root, DataURL dataURL ) {
        Element elem = XMLTools.appendElement( root, null, "DataURL" );
        XMLTools.appendElement( elem, null, "Format", dataURL.getFormat() );
        appendOnlineResource( elem, dataURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendMetadataURL( Element root, MetadataURL metadataURL ) {
        Element elem = XMLTools.appendElement( root, null, "MetadataURL" );
        elem.setAttribute( "type", metadataURL.getType() );
        XMLTools.appendElement( elem, null, "Format", metadataURL.getFormat() );
        appendOnlineResource( elem, metadataURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param identifiers
     * @param i
     */
    protected static void appendIdentifier( Element root, Identifier identifier ) {
        Element elem = XMLTools.appendElement( root, null, "Identifier" );
        elem.setAttribute( "authority", identifier.getAuthority() );
        elem.setTextContent( identifier.getValue() );
    }

    /**
     * @param root
     * @param authorityURLs
     * @param i
     */
    protected static void appendAuthorityURL( Element root, AuthorityURL authorityURL ) {
        Element elem = XMLTools.appendElement( root, null, "AuthorityURL" );
        elem.setAttribute( "name", authorityURL.getName() );
        appendOnlineResource( elem, authorityURL.getOnlineResource(), false );
    }

    /**
     * @param root
     * @param attr
     */
    protected static void appendAttribution( Element root, Attribution attr ) {
        Element elem = XMLTools.appendElement( root, null, "Attribution" );
        XMLTools.appendElement( elem, null, "Title", attr.getTitle() );
        appendOnlineResource( elem, attr.getOnlineResource(), false );
        LogoURL logoURL = attr.getLogoURL();
        if ( logoURL != null ) {
            elem = XMLTools.appendElement( elem, null, "LogoURL" );
            elem.setAttribute( "width", "" + logoURL.getWidth() );
            elem.setAttribute( "height", "" + logoURL.getHeight() );
            XMLTools.appendElement( elem, null, "Format", logoURL.getFormat() );
            appendOnlineResource( elem, logoURL.getOnlineResource(), false );
        }
    }

    /**
     * @param attr
     * @param elem
     */
    protected static void appendOnlineResource( Element root, URL url, boolean appendFineChar ) {
        Element olr = XMLTools.appendElement( root, null, "OnlineResource" );
        olr.setAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink" );
        olr.setAttribute( "xlink:type", "simple" );
        if ( appendFineChar ) {            
            // according to OGC WMS 1.3 Testsuite a URL to a service operation
            // via HTTPGet must end with '?' or '&'
            String href = OWSUtils.validateHTTPGetBaseURL( url.toExternalForm() );
            olr.setAttribute( "xlink:href", href );
        } else {
            olr.setAttribute( "xlink:href", url.toExternalForm() );
        }
    }

    /**
     * @param root
     * @param extents
     * @param i
     */
    protected static void appendExtent( Element root, Extent extent ) {
        Element exNode = XMLTools.appendElement( root, null, "Extent" );
        exNode.setAttribute( "name", extent.getName() );
        exNode.setAttribute( "default", extent.getDefault() );
        exNode.setAttribute( "nearestValue", boolean2Number( extent.useNearestValue() ) );
        exNode.setTextContent( extent.getValue() );
    }

    /**
     * @param root
     * @param dims
     * @param i
     */
    protected static void appendDimension( Element root, Dimension dim ) {
        Element dimNode = XMLTools.appendElement( root, null, "Dimension" );
        dimNode.setAttribute( "name", dim.getName() );
        dimNode.setAttribute( "units", dim.getUnits() );
        dimNode.setAttribute( "unitSymbol", dim.getUnitSymbol() );
    }

    /**
     * @param root
     * @param lBoxes
     * @param i
     */
    protected static void appendLayerBoundingBox( Element root, LayerBoundingBox lBox ) {
        Element bbNode = XMLTools.appendElement( root, null, "BoundingBox" );
        bbNode.setAttribute( "minx", "" + lBox.getMin().getX() );
        bbNode.setAttribute( "miny", "" + lBox.getMin().getY() );
        bbNode.setAttribute( "maxx", "" + lBox.getMax().getX() );
        bbNode.setAttribute( "maxy", "" + lBox.getMax().getY() );
        bbNode.setAttribute( "resx", "" + lBox.getResx() );
        bbNode.setAttribute( "resy", "" + lBox.getResy() );
        bbNode.setAttribute( "SRS", "" + lBox.getSRS() );
    }

    /**
     * @param root
     * @param llBox
     */
    protected static void appendLatLonBoundingBox( Element root, Envelope llBox ) {
        Element bbNode = XMLTools.appendElement( root, null, "LatLonBoundingBox" );
        bbNode.setAttribute( "minx", "" + llBox.getMin().getX() );
        bbNode.setAttribute( "miny", "" + llBox.getMin().getY() );
        bbNode.setAttribute( "maxx", "" + llBox.getMax().getX() );
        bbNode.setAttribute( "maxy", "" + llBox.getMax().getY() );
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: XMLFactory.java,v $
 Revision 1.31  2006/10/22 20:32:08  poth
 support for vendor specific operation GetScaleBar removed

 Revision 1.30  2006/10/17 20:31:17  poth
 *** empty log message ***

 Revision 1.29  2006/09/12 10:08:33  schmitz
 Fixed another xlink issue, fixed content-type header for
 WMS capabilities.

 Revision 1.28  2006/09/12 08:39:00  schmitz
 Added xmlns:xlink attribute to online resources.

 Revision 1.27  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.26  2006/08/23 07:10:21  schmitz
 Renamed the owscommon_neu package to owscommon_new.

 Revision 1.25  2006/08/22 10:25:01  schmitz
 Updated the WMS to use the new OWS common package.
 Updated the rest of deegree to use the new data classes returned
 by the updated WMS methods/capabilities.

 Revision 1.24  2006/08/20 20:53:54  poth
 changes rquired as a consequence of bug fix in wmc implementation/handling. Instead of determining the correct URL as given in a services capabilities deegree always has used the base URL which is just guarenteed to be valid for GetCapabilities requests.

 Revision 1.23  2006/08/18 08:58:36  poth
 code fragment externalized into seperate method in org.deegree.portal.Util

 Revision 1.22  2006/08/17 19:30:57  poth
 bug fix - OGC WMS 1.3 specification defines that HTTPGet URLs must end with '&' or '?'

 Revision 1.21  2006/08/15 08:03:00  poth
 bug fix - OGC WMS 1.3 specification defines that HTTPGet URLs must end with '&' or '?'

 Revision 1.20  2006/08/01 11:46:07  schmitz
 Added data classes for the new OWS common capabilities framework
 according to the OWS 1.0.0 common specification.
 Added name to service identification.

 Revision 1.19  2006/07/28 08:01:27  schmitz
 Updated the WMS for 1.1.1 compliance.
 Fixed some documentation.

 Revision 1.18  2006/07/12 14:46:17  poth
 comment footer added

 Revision 1.17  2006/07/11 14:08:37  schmitz
 Fixed some documentation warnings.

 Revision 1.16  2006/07/06 08:16:29  poth
 *** empty log message ***

 Revision 1.15  2006/06/22 08:25:56  poth
 bug fix - appending optional contact information


 ********************************************************************** */