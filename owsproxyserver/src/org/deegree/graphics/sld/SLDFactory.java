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
package org.deegree.graphics.sld;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.FalseFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcbase.PropertyPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Factory class for all mapped SLD-elements.
 * <p>
 * TODO: Default values for omitted elements (such as fill color) should better not be used in the
 * construction of the corresponding objects (Fill), but marked as left out (to make it possible to
 * differentiate between explicitly given values and default values).
 * <p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.26 $ $Date: 2006/11/30 08:40:34 $
 */
public class SLDFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( SLDFactory.class );

    private static URI ogcNS = CommonNamespaces.OGCNS;

    private static URI xlnNS = CommonNamespaces.XLNNS;

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static XMLFragment sldDoc = null;
    
    /**
     * Creates a <tt>StyledLayerDescriptor</tt>-instance from the given XML-representation.
     * <p>
     * 
     * @param s
     *            contains the XML document
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the XML document is encountered
     * @return the constructed <tt>StyledLayerDescriptor</tt>-instance
     */
    public static synchronized StyledLayerDescriptor createSLD( String s )
                            throws XMLParsingException {
        StyledLayerDescriptor sld = null;
        try {
            sldDoc = new XMLFragment();
            sldDoc.load( new StringReader( s ), XMLFragment.DEFAULT_URL );
            sld = createSLD( sldDoc );
        } catch ( IOException e ) {
            LOG.logDebug( e.getMessage(), e );
            throw new XMLParsingException( "IOException encountered while parsing SLD-Document" );
        } catch ( SAXException e ) {
            LOG.logDebug( e.getMessage(), e );
            throw new XMLParsingException( "SAXException encountered while parsing SLD-Document" );
        }

        return sld;
    }

    public static StyledLayerDescriptor createSLD( URL url )
                            throws XMLParsingException {
        StyledLayerDescriptor sld = null;

        try {
            sldDoc = new XMLFragment();
            sldDoc.load( url );
            sld = createSLD( sldDoc );
        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLParsingException( "IOException encountered while parsing SLD-Document" );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLParsingException( "SAXException encountered while parsing SLD-Document" );
        }

        return sld;
    }
    
    /**
     * Creates a <tt>StyledLayerDescriptor</tt>-instance according to the contents of the
     * DOM-subtree starting at the given 'StyledLayerDescriptor'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'StyledLayerDescriptor'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>StyledLayerDescriptor</tt>-instance
     */
    public static StyledLayerDescriptor createSLD( XMLFragment sldDoc )
                            throws XMLParsingException {

        Element element = sldDoc.getRootElement();

        // optional: <Name>
        String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS, element, null );

        // optional: <Title>
        String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS, element, null );
        // optional: <Abstract>
        String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS, element,
                                                    null );
        // required: version-Attribute
        String version = XMLTools.getRequiredAttrValue( "version", null, element );

        // optional: <NamedLayer>(s) / <UserLayer>(s)
        NodeList nodelist = element.getChildNodes();
        ArrayList layerList = new ArrayList( 100 );

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            if ( nodelist.item( i ) instanceof Element ) {
                Element child = (Element) nodelist.item( i );
                String namespace = child.getNamespaceURI();
                if ( !CommonNamespaces.SLDNS.toASCIIString().equals( namespace ) ) {
                    continue;
                }

                String childName = child.getLocalName();
                if ( childName.equals( "NamedLayer" ) ) {
                    layerList.add( createNamedLayer( child ) );
                } else if ( childName.equals( "UserLayer" ) ) {
                    layerList.add( createUserLayer( child ) );
                }
            }
        }

        AbstractLayer[] al = new AbstractLayer[layerList.size()];
        AbstractLayer[] layers = (AbstractLayer[]) layerList.toArray( al );

        return new StyledLayerDescriptor( name, title, version, abstract_, layers );
    }

    /**
     * 
     * @param element
     * @param min
     * @param max
     * @return
     */
    private static RasterSymbolizer createRasterSymbolizer( Element element, double min, double max ) {
        throw new UnsupportedOperationException(
                                                 "method createRasterSymbolizer is not implemented yet" );
    }

    /**
     * Creates a <tt>TextSymbolizer</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'TextSymbolizer'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'TextSymbolizer'-<tt>Element</tt>
     * @param min
     *            scale-constraint to be used
     * @param max
     *            scale-constraint to be used
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>TextSymbolizer</tt>-instance
     */
    private static TextSymbolizer createTextSymbolizer( Element element, double min, double max )
                            throws XMLParsingException {

        // optional: <Geometry>
        Geometry geometry = null;
        Element geometryElement = XMLTools.getChildElement( "Geometry", CommonNamespaces.SLDNS,
                                                            element );

        if ( geometryElement != null ) {
            geometry = createGeometry( geometryElement );
        }

        // optional: <Label>
        ParameterValueType label = null;
        Element labelElement = XMLTools.getChildElement( "Label", CommonNamespaces.SLDNS, element );

        if ( labelElement != null ) {
            label = createParameterValueType( labelElement );
        }

        // optional: <Font>
        Font font = null;
        Element fontElement = XMLTools.getChildElement( "Font", CommonNamespaces.SLDNS, element );

        if ( fontElement != null ) {
            font = createFont( fontElement );
        }

        // optional: <LabelPlacement>
        LabelPlacement labelPlacement = null;
        Element lpElement = XMLTools.getChildElement( "LabelPlacement", CommonNamespaces.SLDNS,
                                                      element );

        if ( lpElement != null ) {
            labelPlacement = createLabelPlacement( lpElement );
        } else {
            PointPlacement pp = StyleFactory.createPointPlacement();
            labelPlacement = StyleFactory.createLabelPlacement( pp );
        }

        // optional: <Halo>
        Halo halo = null;
        Element haloElement = XMLTools.getChildElement( "Halo", CommonNamespaces.SLDNS, element );

        if ( haloElement != null ) {
            halo = createHalo( haloElement );
        }

        // optional: <Fill>
        Fill fill = null;

        TextSymbolizer ps = null;
        String respClass = XMLTools.getAttrValue( element, "responsibleClass" );
        if ( respClass == null ) {
            ps = new TextSymbolizer( geometry, label, font, labelPlacement, halo, fill, min, max );
        } else {
            ps = new TextSymbolizer( geometry, respClass, label, font, labelPlacement, halo, fill,
                                     min, max );
        }

        return ps;
    }

    /**
     * Creates a <tt>Halo</tt>-instance according to the contents of the DOM-subtree starting at
     * the given 'Halo'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Halo'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Halo</tt>-instance
     */
    private static Halo createHalo( Element element )
                            throws XMLParsingException {
        // optional: <Radius>
        ParameterValueType radius = null;
        Element radiusElement = XMLTools.getChildElement( "Radius", CommonNamespaces.SLDNS, element );

        if ( radiusElement != null ) {
            radius = createParameterValueType( radiusElement );
        }

        // optional: <Fill>
        Fill fill = null;
        Element fillElement = XMLTools.getChildElement( "Fill", CommonNamespaces.SLDNS, element );

        if ( fillElement != null ) {
            fill = createFill( fillElement );
        }

        // optional: <Stroke>
        Stroke stroke = null;
        Element strokeElement = XMLTools.getChildElement( "Stroke", CommonNamespaces.SLDNS, element );

        if ( strokeElement != null ) {
            stroke = createStroke( strokeElement );
        }

        return new Halo( radius, fill, stroke );
    }

    /**
     * Creates a <tt>LabelPlacement</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'LabelPlacement'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'LabelPlacement'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>LabelPlacement</tt>-instance
     */
    private static LabelPlacement createLabelPlacement( Element element )
                            throws XMLParsingException {
        LabelPlacement labelPlacement = null;

        // required: <PointPlacement> / <LinePlacement>
        NodeList nodelist = element.getChildNodes();
        PointPlacement pPlacement = null;
        LinePlacement lPlacement = null;

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            if ( nodelist.item( i ) instanceof Element ) {
                Element child = (Element) nodelist.item( i );
                String namespace = child.getNamespaceURI();

                if ( !CommonNamespaces.SLDNS.toASCIIString().equals( namespace ) ) {
                    continue;
                }

                String childName = child.getLocalName();

                if ( childName.equals( "PointPlacement" ) ) {
                    pPlacement = createPointPlacement( child );
                } else if ( childName.equals( "LinePlacement" ) ) {
                    lPlacement = createLinePlacement( child );
                }
            }
        }

        if ( ( pPlacement != null ) && ( lPlacement == null ) ) {
            labelPlacement = new LabelPlacement( pPlacement );
        } else if ( ( pPlacement == null ) && ( lPlacement != null ) ) {
            labelPlacement = new LabelPlacement( lPlacement );
        } else {
            throw new XMLParsingException( "Element 'LabelPlacement' must contain exactly one "
                                           + "'PointPlacement'- or one 'LinePlacement'-element!" );
        }

        return labelPlacement;
    }

    /**
     * Creates a <tt>PointPlacement</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'PointPlacement'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'PointPlacement'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>PointPlacement</tt>-instance
     */
    private static PointPlacement createPointPlacement( Element element )
                            throws XMLParsingException {

        // optional: auto-Attribute (this is deegree-specific)
        boolean auto = false;
        String autoStr = XMLTools.getAttrValue( element, "auto" );

        if ( autoStr != null && autoStr.equals( "true" ) ) {
            auto = true;
        }

        // optional: <AnchorPoint>
        ParameterValueType[] anchorPoint = null;
        Element apElement = XMLTools.getChildElement( "AnchorPoint", CommonNamespaces.SLDNS,
                                                      element );

        if ( apElement != null ) {
            anchorPoint = new ParameterValueType[2];

            Element apXElement = XMLTools.getChildElement( "AnchorPointX", CommonNamespaces.SLDNS,
                                                           apElement );
            Element apYElement = XMLTools.getChildElement( "AnchorPointY", CommonNamespaces.SLDNS,
                                                           apElement );

            if ( ( apXElement == null ) || ( apYElement == null ) ) {
                throw new XMLParsingException( "Element 'AnchorPoint' must contain exactly one "
                                               + "'AnchorPointX'- and one 'AnchorPointY'-element!" );
            }

            anchorPoint[0] = createParameterValueType( apXElement );
            anchorPoint[1] = createParameterValueType( apYElement );
        }

        // optional: <Displacement>
        ParameterValueType[] displacement = null;
        Element dElement = XMLTools.getChildElement( "Displacement", CommonNamespaces.SLDNS,
                                                     element );

        if ( dElement != null ) {
            displacement = new ParameterValueType[2];

            Element dXElement = XMLTools.getChildElement( "DisplacementX", CommonNamespaces.SLDNS,
                                                          dElement );
            Element dYElement = XMLTools.getChildElement( "DisplacementY", CommonNamespaces.SLDNS,
                                                          dElement );

            if ( ( dXElement == null ) || ( dYElement == null ) ) {
                throw new XMLParsingException(
                                               "Element 'Displacement' must contain exactly one "
                                                                       + "'DisplacementX'- and one 'DisplacementY'-element!" );
            }

            displacement[0] = createParameterValueType( dXElement );
            displacement[1] = createParameterValueType( dYElement );
        }

        // optional: <Rotation>
        ParameterValueType rotation = null;
        Element rElement = XMLTools.getChildElement( "Rotation", CommonNamespaces.SLDNS, element );

        if ( rElement != null ) {
            rotation = createParameterValueType( rElement );
        }

        return new PointPlacement( anchorPoint, displacement, rotation, auto );
    }

    /**
     * Creates a <tt>LinePlacement</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'LinePlacement'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'LinePlacement'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>LinePlacement</tt>-instance
     */
    private static LinePlacement createLinePlacement( Element element )
                            throws XMLParsingException {

        // optional: <PerpendicularOffset>
        ParameterValueType pOffset = null;
        Element pOffsetElement = XMLTools.getChildElement( "PerpendicularOffset",
                                                           CommonNamespaces.SLDNS, element );

        if ( pOffsetElement != null ) {
            pOffset = createParameterValueType( pOffsetElement );
        }

        // optional: <Gap> (this is deegree-specific)
        ParameterValueType gap = null;
        Element gapElement = XMLTools.getChildElement( "Gap", CommonNamespaces.SLDNS, element );

        if ( gapElement != null ) {
            gap = createParameterValueType( gapElement );
        }

        // optional: <LineWidth> (this is deegree-specific)
        ParameterValueType lineWidth = null;
        Element lineWidthElement = XMLTools.getChildElement( "LineWidth", CommonNamespaces.SLDNS,
                                                             element );

        if ( lineWidthElement != null ) {
            lineWidth = createParameterValueType( lineWidthElement );
        }

        return new LinePlacement( pOffset, lineWidth, gap );
    }

    /**
     * Creates a <tt>Font</tt>-instance according to the contents of the DOM-subtree starting at
     * the given 'Font'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Font'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Font</tt>-instance
     */
    private static Font createFont( Element element )
                            throws XMLParsingException {

        // optional: <CssParameter>s
        ElementList nl = XMLTools.getChildElements( "CssParameter", CommonNamespaces.SLDNS, element );
        HashMap cssParams = new HashMap( nl.getLength() );

        for ( int i = 0; i < nl.getLength(); i++ ) {
            CssParameter cssParam = createCssParameter( nl.item( i ) );
            cssParams.put( cssParam.getName(), cssParam );
        }

        return new Font( cssParams );
    }

    /**
     * Creates a <tt>ParameterValueType</tt>-instance according to the contents of the
     * DOM-subtree starting at the given <tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the <tt>Element</tt> (must be of the type sld:ParameterValueType)
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>ParameterValueType</tt>-instance
     */
    private static ParameterValueType createParameterValueType( Element element )
                            throws XMLParsingException {
        // mix of text nodes and <wfs:Expression>-elements
        ArrayList componentList = new ArrayList();
        NodeList nl = element.getChildNodes();

        for ( int i = 0; i < nl.getLength(); i++ ) {
            Node node = nl.item( i );

            switch ( node.getNodeType() ) {
            case Node.TEXT_NODE: {
                componentList.add( node.getNodeValue() );
                break;
            }
            case Node.ELEMENT_NODE: {
                Expression expression = Expression.buildFromDOM( (Element) node );
                componentList.add( expression );
                break;
            }
            default:
                throw new XMLParsingException( "Elements of type 'ParameterValueType' may only "
                                               + "consist of CDATA and 'ogc:Expression'-elements!" );
            }
        }

        Object[] components = componentList.toArray( new Object[componentList.size()] );
        return new ParameterValueType( components );
    }
    

    /**
     * Creates a <tt>NamedStyle</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'NamedStyle'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'NamedStyle'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>NamedStyle</tt>-instance
     */
    private static NamedStyle createNamedStyle( Element element )
                            throws XMLParsingException {
        // required: <Name>
        String name = XMLTools.getRequiredStringValue( "Name", CommonNamespaces.SLDNS, element );

        return new NamedStyle( name );
    }

    /**
     * 
     */
    public static NamedStyle createNamedStyle( String name ) {
        return new NamedStyle( name );
    }

    /**
     * Creates a <tt>RemoteOWS</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'RemoteOWS'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'RemoteOWS'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>RemoteOWS</tt>-instance
     */
    private static RemoteOWS createRemoteOWS( Element element )
                            throws XMLParsingException {
        // required: <Service>
        String service = XMLTools.getRequiredStringValue( "Service", CommonNamespaces.SLDNS,
                                                          element );

        if ( !( service.equals( "WFS" ) || service.equals( "WCS" ) ) ) {
            throw new XMLParsingException( "Value ('" + service
                                           + "') of element 'service' is invalid. "
                                           + "Allowed values are: 'WFS' and 'WCS'." );
        }

        // required: <OnlineResource>
        Element onlineResourceElement = XMLTools.getRequiredChildElement( "OnlineResource",
                                                                          CommonNamespaces.SLDNS,
                                                                          element );
        String href = XMLTools.getRequiredAttrValue( "xlink:href", null, onlineResourceElement );
        URL url = null;

        try {
            url = new URL( href );
        } catch ( MalformedURLException e ) {
            LOG.logDebug( e.getMessage(), e );
            throw new XMLParsingException( "Value ('" + href + "') of attribute 'href' of "
                                           + "element 'OnlineResoure' does not denote a valid URL" );
        }

        return new RemoteOWS( service, url );
    }

    /**
     * Creates a <tt>NamedLayer</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'UserLayer'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'NamedLayer'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>NamedLayer</tt>-instance
     */
    private static NamedLayer createNamedLayer( Element element )
                            throws XMLParsingException {
        // required: <Name>
        String name = XMLTools.getRequiredStringValue( "Name", CommonNamespaces.SLDNS, element );

        // optional: <LayerFeatureConstraints>
        LayerFeatureConstraints lfc = null;
        Element lfcElement = XMLTools.getChildElement( "LayerFeatureConstraints",
                                                       CommonNamespaces.SLDNS, element );

        if ( lfcElement != null ) {
            lfc = createLayerFeatureConstraints( lfcElement );
        }

        // optional: <NamedStyle>(s) / <UserStyle>(s)
        NodeList nodelist = element.getChildNodes();
        ArrayList styleList = new ArrayList();

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            if ( nodelist.item( i ) instanceof Element ) {
                Element child = (Element) nodelist.item( i );
                String namespace = child.getNamespaceURI();

                if ( !CommonNamespaces.SLDNS.toASCIIString().equals( namespace ) ) {
                    continue;
                }

                String childName = child.getLocalName();

                if ( childName.equals( "NamedStyle" ) ) {
                    styleList.add( createNamedStyle( child ) );
                } else if ( childName.equals( "UserStyle" ) ) {
                    styleList.add( createUserStyle( child ) );
                }
            }
        }

        AbstractStyle[] styles = (AbstractStyle[]) styleList.toArray( new AbstractStyle[styleList.size()] );

        return new NamedLayer( name, lfc, styles );
    }

    /**
     * 
     */
    public static NamedLayer createNamedLayer( String name,
                                              LayerFeatureConstraints layerFeatureConstraints,
                                              AbstractStyle[] styles ) {
        return new NamedLayer( name, layerFeatureConstraints, styles );
    }

    /**
     * Creates a <tt>UserLayer</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'UserLayer'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'UserLayer'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>UserLayer</tt>-instance
     */
    private static UserLayer createUserLayer( Element element )
                            throws XMLParsingException {
        // optional: <Name>
        String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS, element, null );

        // optional: <RemoteOWS>
        RemoteOWS remoteOWS = null;
        Element remoteOWSElement = XMLTools.getChildElement( "RemoteOWS", CommonNamespaces.SLDNS,
                                                             element );

        if ( remoteOWSElement != null ) {
            remoteOWS = createRemoteOWS( remoteOWSElement );
        }

        // required: <LayerFeatureConstraints>
        LayerFeatureConstraints lfc = null;
        Element lfcElement = XMLTools.getRequiredChildElement( "LayerFeatureConstraints",
                                                               CommonNamespaces.SLDNS, element );
        lfc = createLayerFeatureConstraints( lfcElement );

        // optional: <UserStyle>(s)
        ElementList nodelist = XMLTools.getChildElements( "UserStyle", CommonNamespaces.SLDNS,
                                                          element );
        UserStyle[] styles = new UserStyle[nodelist.getLength()];

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            styles[i] = createUserStyle( nodelist.item( i ) );
        }

        return new UserLayer( name, lfc, styles, remoteOWS );
    }

    /**
     * Creates a <tt>FeatureTypeConstraint</tt>-instance according to the contents of the
     * DOM-subtree starting at the given 'FeatureTypeConstraint'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'FeatureTypeConstraint'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>FeatureTypeConstraint</tt>-instance
     */
    private static FeatureTypeConstraint createFeatureTypeConstraint( Element element )
                            throws XMLParsingException {
        // optional: <Name>
        Node node = XMLTools.getRequiredNode( element, "sld:FeatureTypeName/text()", 
                                              CommonNamespaces.getNamespaceContext() );
        
        QualifiedName name = XMLTools.getQualifiedNameValue( node );

        // optional: <Filter>
        Filter filter = null;
        Element filterElement = XMLTools.getChildElement( "Filter", ogcNS, element );

        if ( filterElement != null ) {
            filter = AbstractFilter.buildFromDOM( filterElement );
        }

        // optional: <Extent>(s)
        ElementList nodelist = XMLTools.getChildElements( "Extent", CommonNamespaces.SLDNS, element );
        Extent[] extents = new Extent[nodelist.getLength()];

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            extents[i] = createExtent( nodelist.item( i ) );
        }

        return new FeatureTypeConstraint( name, filter, extents );
    }

    /**
     * Creates an <tt>Extent</tt>-instance according to the contents of the DOM-subtree starting
     * at the given 'Extent'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Extent'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Extent</tt>-instance
     */
    private static Extent createExtent( Element element )
                            throws XMLParsingException {
        // required: <Name>
        String name = XMLTools.getRequiredStringValue( "Name", CommonNamespaces.SLDNS, element );
        // required: <Value>
        String value = XMLTools.getRequiredStringValue( "Value", CommonNamespaces.SLDNS, element );

        return new Extent( name, value );
    }

    /**
     * Creates a <tt>LayerFeatureConstraints</tt>-instance according to the contents of the
     * DOM-subtree starting at the given 'LayerFeatureConstraints'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'LayerFeatureConstraints'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>LayerFeatureConstraints</tt>-instance
     */
    public static LayerFeatureConstraints createLayerFeatureConstraints( Element element )
                            throws XMLParsingException {
        // required: <FeatureTypeConstraint>(s)
        ElementList nodelist = XMLTools.getChildElements( "FeatureTypeConstraint",
                                                          CommonNamespaces.SLDNS, element );
        FeatureTypeConstraint[] ftcs = new FeatureTypeConstraint[nodelist.getLength()];

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            ftcs[i] = createFeatureTypeConstraint( nodelist.item( i ) );
        }

        return new LayerFeatureConstraints( ftcs );
    }

    /**
     * Creates a <tt>UserStyle</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'UserStyle'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'UserStyle'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>UserStyle</tt>-instance
     */
    private static UserStyle createUserStyle( Element element )
                            throws XMLParsingException {
        // optional: <Name>
        String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS, element, null );
        // optional: <Title>
        String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS, element, null );
        // optional: <Abstract>
        String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS, element,
                                                    null );

        // optional: <IsDefault>
        String defaultString = XMLTools.getStringValue( "IsDefault", CommonNamespaces.SLDNS,
                                                        element, null );
        boolean isDefault = false;

        if ( defaultString != null ) {
            if ( defaultString.equals( "1" ) ) {
                isDefault = true;
            }
        }

        // required: <FeatureTypeStyle> (s)
        ElementList nl = XMLTools.getChildElements( "FeatureTypeStyle", CommonNamespaces.SLDNS,
                                                    element );
        FeatureTypeStyle[] styles = new FeatureTypeStyle[nl.getLength()];

        if ( styles.length == 0 ) {
            throw new XMLParsingException( "Required child-element 'FeatureTypeStyle' of element "
                                           + "'UserStyle' is missing!" );
        }

        for ( int i = 0; i < nl.getLength(); i++ ) {
            styles[i] = createFeatureTypeStyle( nl.item( i ) );
        }

        return new UserStyle( name, title, abstract_, isDefault, styles );
    }

    /**
     * Creates a <tt>FeatureTypeStyle</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'FeatureTypeStyle'-<tt>Element</tt>.
     * <p>
     * TODO: The ElseFilter currently does not work correctly with FeatureFilters.
     * <p>
     * 
     * @param element
     *            the 'FeatureTypeStyle'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>FeatureTypeStyle</tt>-instance
     */
    public static FeatureTypeStyle createFeatureTypeStyle( Element element )
                            throws XMLParsingException {
        // optional: <Name>
        String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS, element, null );
        // optional: <Title>
        String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS, element, null );
        // optional: <Abstract>
        String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS, element,
                                                    null );
        // optional: <FeatureTypeName>
        String featureTypeName = XMLTools.getStringValue( "FeatureTypeName",
                                                          CommonNamespaces.SLDNS, element, null );

        // optional: several <Rule> / <SemanticTypeIdentifier>
        NodeList nodelist = element.getChildNodes();
        ArrayList ruleList = new ArrayList();
        ArrayList typeIdentifierList = new ArrayList();

        // collect Filters of all Rules
        ArrayList filters = new ArrayList();
        // collect all Rules that have an ElseFilter
        ArrayList elseRules = new ArrayList();

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            if ( nodelist.item( i ) instanceof Element ) {
                Element child = (Element) nodelist.item( i );
                String namespace = child.getNamespaceURI();
                if ( !CommonNamespaces.SLDNS.toString().equals( namespace ) ) {
                    continue;
                }

                String childName = child.getLocalName();

                if ( childName.equals( "Rule" ) ) {
                    Rule rule = createRule( child );
                    if ( rule.hasElseFilter() ) {
                        elseRules.add( rule );
                    } else if ( rule.getFilter() == null
                                || rule.getFilter() instanceof ComplexFilter ) {
                        filters.add( rule.getFilter() );
                    }
                    ruleList.add( rule );
                } else if ( childName.equals( "SemanticTypeIdentifier" ) ) {
                    typeIdentifierList.add( XMLTools.getStringValue( child ) );
                }
            }
        }

        // compute and set the ElseFilter for all ElseFilter-Rules
        Filter elseFilter = null;
        // a Rule exists with no Filter at all -> elseFilter = false
        if ( filters.contains( null ) ) {
            elseFilter = new FalseFilter();
            // one Rule with a Filter exists -> elseFilter = NOT Filter
        } else if ( filters.size() == 1 ) {
            elseFilter = new ComplexFilter( OperationDefines.NOT );
            List arguments = ( (LogicalOperation) ( (ComplexFilter) elseFilter ).getOperation() ).getArguments();
            ComplexFilter complexFilter = (ComplexFilter) filters.get( 0 );
            arguments.add( complexFilter.getOperation() );
            // several Rules with Filters exist -> elseFilter = NOT (Filter1 OR Filter2 OR...)
        } else if ( filters.size() > 1 ) {
            ComplexFilter innerFilter = new ComplexFilter( OperationDefines.OR );
            elseFilter = new ComplexFilter( innerFilter, null, OperationDefines.NOT );
            List arguments = ( (LogicalOperation) innerFilter.getOperation() ).getArguments();
            Iterator it = filters.iterator();
            while ( it.hasNext() ) {
                ComplexFilter complexFilter = (ComplexFilter) it.next();
                arguments.add( complexFilter.getOperation() );
            }
        }
        Iterator it = elseRules.iterator();
        while ( it.hasNext() ) {
            ( (Rule) it.next() ).setFilter( elseFilter );
        }

        Rule[] rules = (Rule[]) ruleList.toArray( new Rule[ruleList.size()] );
        String[] typeIdentifiers = (String[]) typeIdentifierList.toArray( new String[typeIdentifierList.size()] );

        return new FeatureTypeStyle( name, title, abstract_, featureTypeName, typeIdentifiers,
                                     rules );
    }

    /**
     * Creates a <tt>Rule</tt>-instance according to the contents of the DOM-subtree starting at
     * the given 'Rule'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Rule'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Rule</tt>-instance
     */
    private static Rule createRule( Element element )
                            throws XMLParsingException {
        // optional: <Name>
        String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS, element, null );
        // optional: <Title>
        String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS, element, null );
        // optional: <Abstract>
        String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS, element,
                                                    null );

        // optional: <LegendGraphic>
        LegendGraphic legendGraphic = null;
        Element legendGraphicElement = XMLTools.getChildElement( "LegendGraphic",
                                                                 CommonNamespaces.SLDNS, element );

        if ( legendGraphicElement != null ) {
            legendGraphic = createLegendGraphic( legendGraphicElement );
        }

        // optional: <Filter>
        boolean isAnElseFilter = false;
        Filter filter = null;
        Element filterElement = XMLTools.getChildElement( "Filter", ogcNS, element );
        if ( filterElement != null ) {
            filter = AbstractFilter.buildFromDOM( filterElement );
        }

        // optional: <ElseFilter>
        Element elseFilterElement = XMLTools.getChildElement( "ElseFilter", CommonNamespaces.SLDNS,
                                                              element );
        if ( elseFilterElement != null ) {
            isAnElseFilter = true;
        }

        if ( ( filterElement != null ) && ( elseFilterElement != null ) ) {
            throw new XMLParsingException( "Element 'Rule' may contain a 'Filter'- or "
                                           + "an 'ElseFilter'-element, but not both!" );
        }

        // optional: <MinScaleDenominator>
        double min = XMLTools.getNodeAsDouble( element, "sld:MinScaleDenominator", nsContext, 0.0 );
        // optional: <MaxScaleDenominator>
        double max = XMLTools.getNodeAsDouble( element, "sld:MaxScaleDenominator", nsContext, 9E99 );

        // optional: different Symbolizer-elements
        NodeList symbolizerNL = element.getChildNodes();
        ArrayList symbolizerList = new ArrayList();

        for ( int i = 0; i < symbolizerNL.getLength(); i++ ) {
            if ( symbolizerNL.item( i ) instanceof Element ) {
                Element symbolizerElement = (Element) symbolizerNL.item( i );
                String namespace = symbolizerElement.getNamespaceURI();

                if ( !CommonNamespaces.SLDNS.toString().equals( namespace ) ) {
                    continue;
                }

                String symbolizerName = symbolizerElement.getLocalName();

                if ( symbolizerName.equals( "LineSymbolizer" ) ) {
                    symbolizerList.add( createLineSymbolizer( symbolizerElement, min, max ) );
                } else if ( symbolizerName.equals( "PointSymbolizer" ) ) {
                    symbolizerList.add( createPointSymbolizer( symbolizerElement, min, max ) );
                } else if ( symbolizerName.equals( "PolygonSymbolizer" ) ) {
                    symbolizerList.add( createPolygonSymbolizer( symbolizerElement, min, max ) );
                } else if ( symbolizerName.equals( "TextSymbolizer" ) ) {
                    symbolizerList.add( createTextSymbolizer( symbolizerElement, min, max ) );
                } else if ( symbolizerName.equals( "RasterSymbolizer" ) ) {
                    symbolizerList.add( createRasterSymbolizer( symbolizerElement, min, max ) );
                }
            }
        }

        Symbolizer[] symbolizers = (Symbolizer[]) symbolizerList.toArray( new Symbolizer[symbolizerList.size()] );

        return new Rule( symbolizers, name, title, abstract_, legendGraphic, filter,
                         isAnElseFilter, min, max );
    }

    /**
     * Creates a <tt>PointSymbolizer</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'PointSymbolizer'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'PointSymbolizer'-<tt>Element</tt>
     * @param min
     *            scale-constraint to be used
     * @param max
     *            scale-constraint to be used
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>PointSymbolizer</tt>-instance
     */
    private static PointSymbolizer createPointSymbolizer( Element element, double min, double max )
                            throws XMLParsingException {

        // optional: <Geometry>
        Geometry geometry = null;
        Element geometryElement = XMLTools.getChildElement( "Geometry", CommonNamespaces.SLDNS,
                                                            element );

        if ( geometryElement != null ) {
            geometry = createGeometry( geometryElement );
        }

        // optional: <Graphic>
        Graphic graphic = null;
        Element graphicElement = XMLTools.getChildElement( "Graphic", CommonNamespaces.SLDNS,
                                                           element );

        if ( graphicElement != null ) {
            graphic = createGraphic( graphicElement );
        }

        PointSymbolizer ps = null;
        String respClass = XMLTools.getAttrValue( element, "responsibleClass" );
        if ( respClass == null ) {
            ps = new PointSymbolizer( graphic, geometry, min, max );
        } else {
            ps = new PointSymbolizer( graphic, geometry, respClass, min, max );
        }

        return ps;
    }

    /**
     * Creates a <tt>LineSymbolizer</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'LineSymbolizer'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'LineSymbolizer'-<tt>Element</tt>
     * @param min
     *            scale-constraint to be used
     * @param max
     *            scale-constraint to be used
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>LineSymbolizer</tt>-instance
     */
    private static LineSymbolizer createLineSymbolizer( Element element, double min, double max )
                            throws XMLParsingException {

        // optional: <Geometry>
        Geometry geometry = null;
        Element geometryElement = XMLTools.getChildElement( "Geometry", CommonNamespaces.SLDNS,
                                                            element );

        if ( geometryElement != null ) {
            geometry = createGeometry( geometryElement );
        }

        // optional: <Stroke>
        Stroke stroke = null;
        Element strokeElement = XMLTools.getChildElement( "Stroke", CommonNamespaces.SLDNS, element );

        if ( strokeElement != null ) {
            stroke = createStroke( strokeElement );
        }

        LineSymbolizer ls = null;
        String respClass = XMLTools.getAttrValue( element, "responsibleClass" );
        if ( respClass == null ) {
            ls = new LineSymbolizer( stroke, geometry, min, max );
        } else {
            ls = new LineSymbolizer( stroke, geometry, respClass, min, max );
        }
        return ls;
    }

    /**
     * Creates a <tt>PolygonSymbolizer</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'PolygonSymbolizer'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'PolygonSymbolizer'-<tt>Element</tt>
     * @param min
     *            scale-constraint to be used
     * @param max
     *            scale-constraint to be used
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>PolygonSymbolizer</tt>-instance
     */
    private static PolygonSymbolizer createPolygonSymbolizer( Element element, double min,
                                                             double max )
                            throws XMLParsingException {
        // optional: <Geometry>
        Geometry geometry = null;
        Element geometryElement = XMLTools.getChildElement( "Geometry", CommonNamespaces.SLDNS,
                                                            element );

        if ( geometryElement != null ) {
            geometry = createGeometry( geometryElement );
        }

        // optional: <Fill>
        Fill fill = null;
        Element fillElement = XMLTools.getChildElement( "Fill", CommonNamespaces.SLDNS, element );

        if ( fillElement != null ) {
            fill = createFill( fillElement );
        }

        // optional: <Stroke>
        Stroke stroke = null;
        Element strokeElement = XMLTools.getChildElement( "Stroke", CommonNamespaces.SLDNS, element );

        if ( strokeElement != null ) {
            stroke = createStroke( strokeElement );
        }

        PolygonSymbolizer ps = null;
        String respClass = XMLTools.getAttrValue( element, "responsibleClass" );
        if ( respClass == null ) {
            ps = new PolygonSymbolizer( fill, stroke, geometry, min, max );
        } else {
            ps = new PolygonSymbolizer( fill, stroke, geometry, respClass, min, max );
        }

        return ps;
    }

    /**
     * Creates a <tt>Geometry</tt>-instance according to the contents of the DOM-subtree starting
     * at the given 'Geometry'-<tt>Element</tt>.
     * <p>
     * FIXME: Add support for 'Function'-Elements.
     * <p>
     * 
     * @param element
     *            the 'Geometry'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Geometry</tt>-instance
     */
    private static Geometry createGeometry( Element element )
                            throws XMLParsingException {
        Geometry geometry = null;

        // required: <PropertyName>
        Element propertyNameElement = XMLTools.getRequiredChildElement( "PropertyName", ogcNS,
                                                                        element );

        // optional: <Function>
        Element functionElement = XMLTools.getChildElement( "Function", ogcNS, propertyNameElement );

        // just a property name exists
        if ( functionElement == null ) {
            Node node = XMLTools.getNode( propertyNameElement, "/text()", nsContext );
            PropertyPath pp = OGCDocument.parsePropertyPath( (Text) node );
            geometry = new Geometry( pp, null );
        } else {
            // FIXME:
            // the property probably contains a wfs:Function expression
        }

        return geometry;
    }

    /**
     * Creates a <tt>Fill</tt>-instance according to the contents of the DOM-subtree starting at
     * the given 'Fill'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Fill'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Fill</tt>-instance
     */
    private static Fill createFill( Element element )
                            throws XMLParsingException {
        // optional: <GraphicFill>
        GraphicFill graphicFill = null;
        Element graphicFillElement = XMLTools.getChildElement( "GraphicFill",
                                                               CommonNamespaces.SLDNS, element );

        if ( graphicFillElement != null ) {
            graphicFill = createGraphicFill( graphicFillElement );
        }

        // optional: <CssParameter>s
        ElementList nl = XMLTools.getChildElements( "CssParameter", CommonNamespaces.SLDNS, element );
        HashMap cssParams = new HashMap( nl.getLength() );

        for ( int i = 0; i < nl.getLength(); i++ ) {
            CssParameter cssParam = createCssParameter( nl.item( i ) );
            cssParams.put( cssParam.getName(), cssParam );
        }

        return new Fill( cssParams, graphicFill );
    }

    /**
     * Creates a <tt>LegendGraphic</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'LegendGraphic'-element.
     * <p>
     * 
     * @param element
     *            the 'LegendGraphic'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Graphic</tt>-instance
     */
    private static LegendGraphic createLegendGraphic( Element element )
                            throws XMLParsingException {
        // required: <Graphic>
        Element graphicElement = XMLTools.getRequiredChildElement( "Graphic",
                                                                   CommonNamespaces.SLDNS, element );
        Graphic graphic = createGraphic( graphicElement );

        return new LegendGraphic( graphic );
    }

    /**
     * Creates an <tt>ExternalGraphic</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'ExternalGraphic'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'ExternalGraphic'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>ExternalGraphic</tt>-instance
     */
    private static ExternalGraphic createExternalGraphic( Element element )
                            throws XMLParsingException {
        // required: <OnlineResource>
        Element onlineResourceElement = XMLTools.getRequiredChildElement( "OnlineResource",
                                                                          CommonNamespaces.SLDNS,
                                                                          element );

        // required: href-Attribute (in <OnlineResource>)
        String href = XMLTools.getRequiredAttrValue( "href", xlnNS, onlineResourceElement );
        URL url = null;
        try {
            url = sldDoc.resolve( href );
        } catch ( MalformedURLException e ) {
            LOG.logDebug( e.getMessage(), e );
            throw new XMLParsingException( "Value ('" + href + "') of attribute 'href' of "
                                           + "element 'OnlineResoure' does not denote a valid URL" );
        }

        // required: <Format> (in <OnlineResource>)
        String format = XMLTools.getRequiredStringValue( "Format", CommonNamespaces.SLDNS, element );

        return new ExternalGraphic( format, url );
    }

    /**
     * Creates a <tt>Mark</tt>-instance according to the contents of the DOM-subtree starting at
     * the given 'Mark'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Mark'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Mark</tt>-instance
     */
    private static Mark createMark( Element element )
                            throws XMLParsingException {
        Stroke stroke = null;
        Fill fill = null;

        // optional: <WellKnownName>
        String wkn = XMLTools.getStringValue( "WellKnownName", CommonNamespaces.SLDNS, element,
                                              null );

        // optional: <Stroke>
        Element strokeElement = XMLTools.getChildElement( "Stroke", CommonNamespaces.SLDNS, element );

        if ( strokeElement != null ) {
            stroke = createStroke( strokeElement );
        }

        // optional: <Fill>
        Element fillElement = XMLTools.getChildElement( "Fill", CommonNamespaces.SLDNS, element );

        if ( fillElement != null ) {
            fill = createFill( fillElement );
        }

        return new Mark( wkn, stroke, fill );
    }

    /**
     * Creates a <tt>Stroke</tt>-instance according to the contents of the DOM-subtree starting
     * at the given 'Stroke'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'Stroke'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Stroke</tt>-instance
     */
    private static Stroke createStroke( Element element )
                            throws XMLParsingException {
        GraphicFill gf = null;
        GraphicStroke gs = null;

        // optional: <GraphicFill>
        Element gfElement = XMLTools.getChildElement( "GraphicFill", CommonNamespaces.SLDNS,
                                                      element );

        if ( gfElement != null ) {
            gf = createGraphicFill( gfElement );
        }

        // optional: <GraphicStroke>
        Element gsElement = XMLTools.getChildElement( "GraphicStroke", CommonNamespaces.SLDNS,
                                                      element );

        if ( gsElement != null ) {
            gs = createGraphicStroke( gsElement );
        }

        // optional: <CssParameter>s
        ElementList nl = XMLTools.getChildElements( "CssParameter", CommonNamespaces.SLDNS, element );
        HashMap cssParams = new HashMap( nl.getLength() );

        for ( int i = 0; i < nl.getLength(); i++ ) {
            CssParameter cssParam = createCssParameter( nl.item( i ) );
            cssParams.put( cssParam.getName(), cssParam );
        }

        return new Stroke( cssParams, gs, gf );
    }

    /**
     * Creates a <tt>GraphicFill</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'GraphicFill'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'GraphicFill'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>GraphicFill</tt>-instance
     */
    private static GraphicFill createGraphicFill( Element element )
                            throws XMLParsingException {
        // required: <Graphic>
        Element graphicElement = XMLTools.getRequiredChildElement( "Graphic",
                                                                   CommonNamespaces.SLDNS, element );
        Graphic graphic = createGraphic( graphicElement );

        return new GraphicFill( graphic );
    }

    /**
     * Creates a <tt>GraphicStroke</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'GraphicStroke'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'GraphicStroke'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>GraphicStroke</tt>-instance
     */
    private static GraphicStroke createGraphicStroke( Element element )
                            throws XMLParsingException {
        // required: <Graphic>
        Element graphicElement = XMLTools.getRequiredChildElement( "Graphic",
                                                                   CommonNamespaces.SLDNS, element );
        Graphic graphic = createGraphic( graphicElement );

        return new GraphicStroke( graphic );
    }

    /**
     * Creates a <tt>Graphic</tt>-instance according to the contents of the DOM-subtree starting
     * at the given 'Graphic'-element.
     * <p>
     * 
     * @param element
     *            the 'Graphic'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>Graphic</tt>-instance
     */
    private static Graphic createGraphic( Element element )
                            throws XMLParsingException {

        // optional: <Opacity>
        ParameterValueType opacity = null;
        // optional: <Size>
        ParameterValueType size = null;
        // optional: <Rotation>
        ParameterValueType rotation = null;

        // optional: <ExternalGraphic>s / <Mark>s
        NodeList nodelist = element.getChildNodes();
        ArrayList marksAndExtGraphicsList = new ArrayList();

        for ( int i = 0; i < nodelist.getLength(); i++ ) {
            if ( nodelist.item( i ) instanceof Element ) {
                Element child = (Element) nodelist.item( i );
                String namespace = child.getNamespaceURI();

                if ( !CommonNamespaces.SLDNS.toString().equals( namespace ) ) {
                    continue;
                }

                String childName = child.getLocalName();

                if ( childName.equals( "ExternalGraphic" ) ) {
                    marksAndExtGraphicsList.add( createExternalGraphic( child ) );
                } else if ( childName.equals( "Mark" ) ) {
                    marksAndExtGraphicsList.add( createMark( child ) );
                } else if ( childName.equals( "Opacity" ) ) {
                    opacity = createParameterValueType( child );
                } else if ( childName.equals( "Size" ) ) {
                    size = createParameterValueType( child );
                } else if ( childName.equals( "Rotation" ) ) {
                    rotation = createParameterValueType( child );
                }
            }
        }

        Object[] marksAndExtGraphics = 
            marksAndExtGraphicsList.toArray( new Object[marksAndExtGraphicsList.size()] );

        return new Graphic( marksAndExtGraphics, opacity, size, rotation );
    }

    /**
     * Creates a <tt>CssParameter</tt>-instance according to the contents of the DOM-subtree
     * starting at the given 'CssParameter'-<tt>Element</tt>.
     * <p>
     * 
     * @param element
     *            the 'CssParamter'-<tt>Element</tt>
     * @throws XMLParsingException
     *             if a syntactic or semantic error in the DOM-subtree is encountered
     * @return the constructed <tt>CssParameter</tt>-instance
     */
    private static CssParameter createCssParameter( Element element )
                            throws XMLParsingException {
        // required: name-Attribute
        String name = XMLTools.getRequiredAttrValue( "name", null, element );
        ParameterValueType pvt = createParameterValueType( element );

        return ( new CssParameter( name, pvt ) );
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: SLDFactory.java,v $
 Revision 1.26  2006/11/30 08:40:34  poth
 refactoring (renaming of a method)

 Revision 1.25  2006/11/29 21:28:30  poth
 bug fixing - SLD GetMap requests containing user layers with featuretypeconstraints

 Revision 1.24  2006/10/02 10:02:37  poth
 debug statements changed from logDebug to logError in parsing method

 Revision 1.23  2006/09/28 12:49:28  poth
 code formating

 Revision 1.22  2006/09/08 08:42:02  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.21  2006/08/06 20:26:00  poth
 UnsupportedOperationException exception will be thrown if a not implemented method will be called

 Revision 1.20  2006/07/29 08:51:12  poth
 references to deprecated classes removed

 Revision 1.19  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */
