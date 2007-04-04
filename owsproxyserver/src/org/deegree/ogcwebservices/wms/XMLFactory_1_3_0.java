//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/XMLFactory_1_3_0.java,v 1.2 2006/11/24 15:05:35 schmitz Exp $
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
package org.deegree.ogcwebservices.wms;

import java.io.IOException;
import java.net.URI;
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
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument_1_3_0;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities_1_3_0;
import org.deegree.owscommon.XMLFactory;
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
 * <code>XMLFactory_1_3_0</code> is an XML factory that outputs valid WMS 1.3.0 documents.
 * It is not intended for direct use but is used automatically by the standard <code>XMLFactory</code>.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/11/24 15:05:35 $
 * 
 * @since 2.0
 */

public class XMLFactory_1_3_0 extends XMLFactory {
    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static final String PWMS = CommonNamespaces.WMS_PREFIX + ":";

    private static final URI WMSNS = CommonNamespaces.WMSNS;

    /**
     * Builds a 1.3.0 WMS capabilities document.
     * 
     * @param capabilities
     * @return the XML document
     * @throws IOException
     */
    public static WMSCapabilitiesDocument_1_3_0 export( WMSCapabilities_1_3_0 capabilities )
                            throws IOException {
        WMSCapabilitiesDocument_1_3_0 capabilitiesDocument = new WMSCapabilitiesDocument_1_3_0();
        try {
            capabilitiesDocument.createEmptyDocument();

            Element root = capabilitiesDocument.getRootElement();

            root.setAttribute( "version", "1.3.0" );
            root.setAttribute( "updateSequence", capabilities.getUpdateSequence() );

            appendService( root, capabilities.getServiceIdentification(),
                           capabilities.getServiceProvider() );

            appendCapabilityRequests( root, capabilities.getOperationMetadata() );

            appendCapabilityLayer(
                                   (Element) XMLTools.getNode( root, PWMS + "Capability", nsContext ),
                                   capabilities.getLayer() );

            Element serviceElement = (Element) XMLTools.getRequiredNode( root, PWMS + "Service",
                                                                         nsContext );

            if ( capabilities.getLayerLimit() != 0 )
                XMLTools.appendElement( serviceElement, WMSNS, "LayerLimit",
                                        "" + capabilities.getLayerLimit() );

            if ( capabilities.getMaxWidth() != 0 )
                XMLTools.appendElement( serviceElement, WMSNS, "MaxWidth",
                                        "" + capabilities.getMaxWidth() );

            if ( capabilities.getMaxHeight() != 0 )
                XMLTools.appendElement( serviceElement, WMSNS, "MaxHeight",
                                        "" + capabilities.getMaxHeight() );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
        }

        return capabilitiesDocument;
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

        root = (Element) XMLTools.getRequiredNode( root, PWMS + "Service", nsContext );

        Element node = (Element) XMLTools.getRequiredNode( root, PWMS + "Name", nsContext );
        node.setTextContent( identification.getServiceType().getCode() );

        node = (Element) XMLTools.getRequiredNode( root, PWMS + "Title", nsContext );
        node.setTextContent( identification.getTitle() );

        String serviceAbstract = identification.getAbstractString();
        if ( serviceAbstract != null ) {
            XMLTools.appendElement( root, WMSNS, PWMS + "Abstract", serviceAbstract );
        }

        List<Keywords> keywords = identification.getKeywords();
        if ( keywords.size() > 0 ) {
            String[] kw = keywords.get( 0 ).getKeywords();
            Element kwl = XMLTools.appendElement( root, WMSNS, PWMS + "KeywordList" );
            for ( int i = 0; i < kw.length; i++ ) {
                XMLTools.appendElement( kwl, WMSNS, PWMS + "Keyword", kw[i] );
            }
        }

        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "OnlineResource" );
        OnlineResource sLink = provider.getProviderSite();
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource( elem, sLink );

        appendContactInformation( root, provider );

        String fee = null;
        if ( identification.getAccessConstraints().size() > 0 )
            fee = identification.getAccessConstraints().get( 0 ).getFees();
        if ( fee != null ) {
            XMLTools.appendElement( root, WMSNS, PWMS + "Fees", fee );
        } else {
            XMLTools.appendElement( root, WMSNS, PWMS + "Fees", "none" );
        }

        if ( identification.getAccessConstraints().size() > 0 ) {
            XMLTools.appendElement(
                                    root,
                                    WMSNS,
                                    PWMS + "AccessConstraints",
                                    identification.getAccessConstraints().get( 0 ).getUseLimitations().get(
                                                                                                            0 ) );
        } else {
            XMLTools.appendElement( root, WMSNS, PWMS + "AccessConstraints", "none" );
        }

    }

    /**
     * 
     * @param root
     * @param provider
     * @throws XMLParsingException
     */
    protected static void appendContactInformation( Element root, ServiceProvider provider ) {

        Element ciNode = XMLTools.appendElement( root, WMSNS, PWMS + "ContactInformation" );
        Element cppNode = XMLTools.appendElement( ciNode, WMSNS, PWMS + "ContactPersonPrimary" );
        if ( provider.getServiceContact().getIndividualName().length > 0 ) {
            XMLTools.appendElement( cppNode, WMSNS, PWMS + "ContactPerson",
                                    provider.getServiceContact().getIndividualName()[0] );
        }
        if ( provider.getServiceContact().getOrganisationName().length > 0 ) {
            XMLTools.appendElement( cppNode, WMSNS, PWMS + "ContactOrganization",
                                    provider.getServiceContact().getOrganisationName()[0] );
        }
        if ( provider.getServiceContact().getPositionName().length > 0 ) {
            XMLTools.appendElement( ciNode, WMSNS, PWMS + "ContactPosition",
                                    provider.getServiceContact().getPositionName()[0] );
        }
        Element caNode = XMLTools.appendElement( ciNode, WMSNS, PWMS + "ContactAddress" );

        XMLTools.appendElement( caNode, WMSNS, PWMS + "AddressType", "postal" );

        if ( provider.getServiceContact().getContactInfo().length > 0 ) {
            Address addr = provider.getServiceContact().getContactInfo()[0].getAddress();
            String[] dp = addr.getDeliveryPoint();
            if ( dp.length > 0 ) {
                XMLTools.appendElement( caNode, WMSNS, PWMS + "Address", dp[0] );
            }
            if ( addr.getCity() != null ) {
                XMLTools.appendElement( caNode, WMSNS, PWMS + "City", addr.getCity() );
            }
            if ( addr.getAdministrativeArea() != null ) {
                XMLTools.appendElement( caNode, WMSNS, PWMS + "StateOrProvince",
                                        addr.getAdministrativeArea() );
            }
            if ( addr.getPostalCode() != null ) {
                XMLTools.appendElement( caNode, WMSNS, PWMS + "PostCode", addr.getPostalCode() );
            }
            if ( addr.getCountry() != null ) {
                XMLTools.appendElement( caNode, WMSNS, PWMS + "Country", addr.getCountry() );
            }

            Phone phone = provider.getServiceContact().getContactInfo()[0].getPhone();
            if ( phone.getVoice().length > 0 ) {
                XMLTools.appendElement( ciNode, WMSNS, PWMS + "ContactVoiceTelephone",
                                        phone.getVoice()[0] );
            }
            if ( phone.getFacsimile().length > 0 ) {
                XMLTools.appendElement( ciNode, WMSNS, PWMS + "ContactFacsimileTelephone",
                                        phone.getFacsimile()[0] );
            }
            if ( addr.getElectronicMailAddress().length > 0 ) {
                XMLTools.appendElement( ciNode, WMSNS, PWMS + "ContactElectronicMailAddress",
                                        addr.getElectronicMailAddress()[0] );
            }
        }

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

        root = (Element) XMLTools.getRequiredNode( root, PWMS + "Capability/" + PWMS + "Request",
                                                   nsContext );

        operationsMetadata.getOperations();

        // just append all operations
        for ( Operation operation : operationsMetadata.getOperations() ) {
            appendOperation( root, operation );
        }

        // maybe we have to check for mandatory operations?

    }

    /**
     * 
     * @param root
     * @param operation
     * @throws XMLParsingException
     */
    protected static void appendOperation( Element root, Operation operation ) {

        String name = operation.getName().getAsString();

        if ( "sld:GetLegendGraphic".equals( name ) ) {
            root = XMLTools.appendElement( root, CommonNamespaces.SLDNS, name );
            //            root.setAttribute( "xsi:type", "wms:_ExtendedOperation" );
        } else {
            root = XMLTools.appendElement( root, WMSNS, name );
        }

        DomainType odt = (DomainType) operation.getParameter( new QualifiedName( "Format" ) );

        List<TypedLiteral> values = odt.getValues();
        for ( TypedLiteral value : values )
            XMLTools.appendElement( root, WMSNS, PWMS + "Format", value.getValue() );

        List<DCP> dcps = operation.getDCP();
        for ( DCP dcp : dcps ) {
            Element http = XMLTools.appendElement( root, WMSNS, PWMS + "DCPType" );
            http = XMLTools.appendElement( http, WMSNS, PWMS + "HTTP" );
            HTTP ht = (HTTP) dcp;
            List<HTTP.Type> types = ht.getTypes();
            List<OnlineResource> links = ht.getLinks();
            for ( int i = 0; i < types.size(); ++i ) {
                Element elem = null;
                if ( types.get( i ) == HTTP.Type.Get )
                    elem = XMLTools.appendElement( http, WMSNS, PWMS + "Get" );
                if ( types.get( i ) == HTTP.Type.Post )
                    elem = XMLTools.appendElement( http, WMSNS, PWMS + "Post" );
                if ( elem != null ) {
                    elem = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
                    org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                                         elem,
                                                                                         links.get( i ) );
                }
            }
        }

    }

    //    /**
    //     * @param http
    //     * @param urls
    //     */
    //    protected static void appendURLs( Element http, URL[] urls, String type ) {
    //        for ( int j = 0; j < urls.length; j++ ) {
    //            Element olr = XMLTools.appendElement( http, null, type );
    //            appendOnlineResource( olr, urls[j], "Get".equalsIgnoreCase( type ) );
    //        }
    //    }

    /**
     * 
     * @param root
     * @param layer
     * @throws XMLParsingException
     */
    protected static void appendCapabilityLayer( Element root, Layer layer )
                            throws XMLParsingException {

        root = XMLTools.appendElement( root, WMSNS, PWMS + "Layer" );
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
            XMLTools.appendElement( root, WMSNS, PWMS + "Name", layer.getName() );
        }
        XMLTools.appendElement( root, WMSNS, PWMS + "Title", layer.getTitle() );

        if ( layer.getAbstract() != null ) {
            XMLTools.appendElement( root, WMSNS, PWMS + "Abstract", layer.getAbstract() );
        }

        String[] keywords = layer.getKeywordList();
        if ( keywords.length > 0 ) {
            Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "KeywordList" );
            for ( int i = 0; i < keywords.length; i++ ) {
                XMLTools.appendElement( elem, WMSNS, PWMS + "Keyword", keywords[i] );
            }
        }

        String[] srs = layer.getSrs();
        for ( int i = 0; i < srs.length; i++ ) {
            XMLTools.appendElement( root, WMSNS, PWMS + "CRS", srs[i] );
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
        XMLTools.appendElement( root, WMSNS, PWMS + "MinScaleDenominator", "" + scaleHint.getMin() );
        XMLTools.appendElement( root, WMSNS, PWMS + "MaxScaleDenominator", "" + scaleHint.getMax() );

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

        root = XMLTools.appendElement( root, WMSNS, PWMS + "Style" );
        XMLTools.appendElement( root, WMSNS, PWMS + "Name", nm );
        if ( style.getTitle() != null ) {
            XMLTools.appendElement( root, WMSNS, PWMS + "Title", tlt );
        }
        if ( style.getAbstract() != null ) {
            XMLTools.appendElement( root, WMSNS, PWMS + "Abstract", style.getAbstract() );
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
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "StyleURL" );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Format", styleURL.getFormat() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             styleURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendStyleSheetURL( Element root, StyleSheetURL styleSheetURL ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "StyleSheetURL" );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Format", styleSheetURL.getFormat() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             styleSheetURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendLegendURL( Element root, LegendURL legendURL ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "LegendURL" );
        elem.setAttribute( "width", "" + legendURL.getWidth() );
        elem.setAttribute( "height", "" + legendURL.getWidth() );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Format", legendURL.getFormat() );

        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             legendURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendFeatureListURL( Element root, FeatureListURL featureListURL ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "FeatureListURL" );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Format", featureListURL.getFormat() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             featureListURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendDataURL( Element root, DataURL dataURL ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "DataURL" );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Format", dataURL.getFormat() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             dataURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param metadataURLs
     * @param i
     */
    protected static void appendMetadataURL( Element root, MetadataURL metadataURL ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "MetadataURL" );
        elem.setAttribute( "type", metadataURL.getType() );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Format", metadataURL.getFormat() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             metadataURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param identifiers
     * @param i
     */
    protected static void appendIdentifier( Element root, Identifier identifier ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "Identifier" );
        elem.setAttribute( "authority", identifier.getAuthority() );
        elem.setTextContent( identifier.getValue() );
    }

    /**
     * @param root
     * @param authorityURLs
     * @param i
     */
    protected static void appendAuthorityURL( Element root, AuthorityURL authorityURL ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "AuthorityURL" );
        elem.setAttribute( "name", authorityURL.getName() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             authorityURL.getOnlineResource() );
    }

    /**
     * @param root
     * @param attr
     */
    protected static void appendAttribution( Element root, Attribution attr ) {
        Element elem = XMLTools.appendElement( root, WMSNS, PWMS + "Attribution" );
        XMLTools.appendElement( elem, WMSNS, PWMS + "Title", attr.getTitle() );
        Element res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
        org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                             res,
                                                                             attr.getOnlineResource() );
        LogoURL logoURL = attr.getLogoURL();
        if ( logoURL != null ) {
            elem = XMLTools.appendElement( elem, WMSNS, PWMS + "LogoURL" );
            elem.setAttribute( "width", "" + logoURL.getWidth() );
            elem.setAttribute( "height", "" + logoURL.getHeight() );
            XMLTools.appendElement( elem, WMSNS, PWMS + "Format", logoURL.getFormat() );
            res = XMLTools.appendElement( elem, WMSNS, PWMS + "OnlineResource" );
            org.deegree.model.metadata.iso19115.XMLFactory.appendOnlineResource(
                                                                                 res,
                                                                                 logoURL.getOnlineResource() );
        }
    }

    /**
     * @param root
     * @param extents
     * @param i
     */
    protected static void appendExtent( Element root, Extent extent ) {
        Element exNode = XMLTools.appendElement( root, WMSNS, PWMS + "Extent" );
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
        Element dimNode = XMLTools.appendElement( root, WMSNS, PWMS + "Dimension" );
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
        Element bbNode = XMLTools.appendElement( root, WMSNS, PWMS + "BoundingBox" );
        bbNode.setAttribute( "minx", "" + lBox.getMin().getX() );
        bbNode.setAttribute( "miny", "" + lBox.getMin().getY() );
        bbNode.setAttribute( "maxx", "" + lBox.getMax().getX() );
        bbNode.setAttribute( "maxy", "" + lBox.getMax().getY() );
        bbNode.setAttribute( "resx", "" + lBox.getResx() );
        bbNode.setAttribute( "resy", "" + lBox.getResy() );
        bbNode.setAttribute( "CRS", "" + lBox.getSRS() );
    }

    /**
     * @param root
     * @param llBox
     */
    protected static void appendLatLonBoundingBox( Element root, Envelope llBox ) {
        Element bbNode = XMLTools.appendElement( root, WMSNS, PWMS + "EX_GeographicBoundingBox" );
        XMLTools.appendElement( bbNode, WMSNS, PWMS + "westBoundLongitude", ""
                                                                            + llBox.getMin().getX() );
        XMLTools.appendElement( bbNode, WMSNS, PWMS + "eastBoundLongitude", ""
                                                                            + llBox.getMax().getX() );
        XMLTools.appendElement( bbNode, WMSNS, PWMS + "southBoundLatitude", ""
                                                                            + llBox.getMin().getY() );
        XMLTools.appendElement( bbNode, WMSNS, PWMS + "northBoundLatitude", ""
                                                                            + llBox.getMax().getY() );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: XMLFactory_1_3_0.java,v $
 Revision 1.2  2006/11/24 15:05:35  schmitz
 Added a bogus sld schema document to be able to validate the GetCapabilities response with a GetLegendGraphic request type.

 Revision 1.1  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.



 ********************************************************************** */