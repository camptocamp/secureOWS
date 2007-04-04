//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/GMLFeatureDocument.java,v 1.32 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.model.feature;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.FeaturePropertyType;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.feature.schema.GMLSchemaDocument;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.MultiGeometryPropertyType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.feature.schema.SimplePropertyType;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.GMLDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Parser and wrapper class for GML feature documents.
 * <p>
 * Has validation capabilities: if the schema is provided or the document contains a reference
 * to a schema the structure of the generated features is checked. If no schema information is
 * available, feature + property types are heuristically determined from the feature instance in
 * the document (guessing of simple property types can be turned off, because it may cause
 * unwanted effects). 
 * </p>  
 * <p>
 * Has some basic understanding of XLink: Supports internal XLinks (i.e. the content for a feature
 * is given by a reference to a feature element in the same document). No support for external
 * XLinks yet.
 * </p>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.32 $, $Date: 2006/11/27 09:07:52 $
 */
public class GMLFeatureDocument extends GMLDocument {

    private static final long serialVersionUID = -7626943858143104276L;

    private final static ILogger LOG = LoggerFactory.getLogger( GMLFeatureDocument.class );

    private static String FID = "fid";

    private static String GMLID = "id";

    private static URI GMLNS = CommonNamespaces.GMLNS;

    private static String GMLID_NS = CommonNamespaces.GMLNS.toString();

    private static QualifiedName PROP_NAME_BOUNDED_BY = new QualifiedName( "boundedBy", GMLNS );

    private static QualifiedName PROP_NAME_DESCRIPTION = new QualifiedName( "description", GMLNS );

    private static QualifiedName PROP_NAME_NAME = new QualifiedName( "name", GMLNS );

    private static QualifiedName PROP_NAME_WKB_GEOM = new QualifiedName( "wkbGeom", GMLNS );

    private static QualifiedName TYPE_NAME_BOX = new QualifiedName( "Box", GMLNS );

    private static QualifiedName TYPE_NAME_LINESTRING = new QualifiedName( "LineString", GMLNS );

    private static QualifiedName TYPE_NAME_MULTIGEOMETRY = new QualifiedName( "MultiGeometry",
                                                                              GMLNS );

    private static QualifiedName TYPE_NAME_MULTILINESTRING = new QualifiedName( "MultiLineString",
                                                                                GMLNS );

    private static QualifiedName TYPE_NAME_MULTIPOINT = new QualifiedName( "MultiPoint", GMLNS );

    private static QualifiedName TYPE_NAME_MULTIPOLYGON = new QualifiedName( "MultiPolygon", GMLNS );

    private static QualifiedName TYPE_NAME_POINT = new QualifiedName( "Point", GMLNS );

    private static QualifiedName TYPE_NAME_POLYGON = new QualifiedName( "Polygon", GMLNS );

    private static QualifiedName TYPE_NAME_SURFACE = new QualifiedName( "Surface", GMLNS );

    private static QualifiedName TYPE_NAME_CURVE = new QualifiedName( "Curve", GMLNS );

    private static QualifiedName TYPE_NAME_MULTISURFACE = new QualifiedName( "MultiSurface", GMLNS );

    private static QualifiedName TYPE_NAME_MULTICURVE = new QualifiedName( "MultiCurve", GMLNS );

    // key: namespace URI, value: GMLSchema 
    protected Map<URI, GMLSchema> gmlSchemaMap;

    // key: feature id, value: Feature 
    protected Map<String, Feature> featureMap = new HashMap<String, Feature>();

    // value: XLinkedFeatureProperty 
    protected Collection<XLinkedFeatureProperty> xlinkPropertyList = new ArrayList<XLinkedFeatureProperty>();

    private boolean guessSimpleTypes = false;

    /**
     * Creates a new instance of <code>GMLFeatureDocument</code>.
     * <p>
     * Simple types encountered during parsing are "guessed", i.e. the parser tries to convert
     * the values to double, integer, calendar, etc. However, this may lead to unwanted results,
     * e.g. a property value of "054604" is converted to "54604". 
     */
    public GMLFeatureDocument() {
        super();
    }

    /**
     * Creates a new instance of <code>GMLFeatureDocument</code>.
     * <p> 
     * @param guessSimpleTypes
     *            set to true, if simple types should be "guessed" during parsing
     */
    public GMLFeatureDocument( boolean guessSimpleTypes ) {
        super();
        this.guessSimpleTypes = guessSimpleTypes;
    }

    /**
     * Explicitly sets the GML schema information that the document must comply to.
     * <p>
     * This overrides any schema information that the document refers to.
     * 
     * @param gmlSchemaMap
     *            key: namespace URI, value: GMLSchema
     */
    public void setSchemas( Map<URI, GMLSchema> gmlSchemaMap ) {
        this.gmlSchemaMap = gmlSchemaMap;
    }

    /**
     * Returns the object representation for the root feature element.
     * 
     * @return object representation for the root feature element.
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    public Feature parseFeature()
                            throws XMLParsingException, UnknownCRSException {
        Feature feature = this.parseFeature( this.getRootElement() );
        resolveXLinkReferences();
        return feature;
    }

    /**
     * Returns the object representation for the given feature element.
     * 
     * @param element
     *            feature element
     * @return object representation for the given feature element.
     * @throws XMLParsingException 
     * @throws UnknownCRSException 
     */
    protected Feature parseFeature( Element element )
                            throws XMLParsingException, UnknownCRSException {

        Feature feature = null;
        String fid = parseFeatureId( element );
        FeatureType featureType = getFeatureType( element );

        ElementList childList = XMLTools.getChildElements( element );
        Collection<FeatureProperty> propertyList = new ArrayList<FeatureProperty>(
                                                                                   childList.getLength() );

        for ( int i = 0; i < childList.getLength(); i++ ) {
            Element propertyElement = childList.item( i );
            QualifiedName propertyName = getQualifiedName( propertyElement );

            if ( PROP_NAME_BOUNDED_BY.equals( propertyName )
                 || PROP_NAME_WKB_GEOM.equals( propertyName ) ) {
                // TODO
            } else if ( PROP_NAME_NAME.equals( propertyName )
                        || PROP_NAME_DESCRIPTION.equals( propertyName ) ) {
                String s = XMLTools.getStringValue( propertyElement );
                if ( s != null ) {
                    s = s.trim();
                }
                FeatureProperty property = createSimpleProperty( s, propertyName, Types.VARCHAR );
                if ( property != null ) {
                    propertyList.add( property );
                }
            } else {
                FeatureProperty property = parseProperty( childList.item( i ), featureType );
                if ( property != null ) {
                    propertyList.add( property );
                }
            }
        }

        FeatureProperty[] featureProperties = propertyList.toArray( new FeatureProperty[propertyList.size()] );
        feature = FeatureFactory.createFeature( fid, featureType, featureProperties );

        if ( !"".equals( fid ) ) {
            if ( this.featureMap.containsKey( fid ) ) {
                String msg = Messages.format( "ERROR_FEATURE_ID_NOT_UNIQUE", fid );
                throw new XMLParsingException( msg );
            }
            this.featureMap.put( fid, feature );
        }

        return feature;
    }

    /**
     * Returns the object representation for the given property element.
     * 
     * @param propertyElement
     *            property element
     * @param featureType
     *            feature type of the feature that the property belongs to
     * @return object representation for the given property element.
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    public FeatureProperty parseProperty( Element propertyElement, FeatureType featureType )
                            throws XMLParsingException, UnknownCRSException {

        FeatureProperty property = null;
        QualifiedName propertyName = getQualifiedName( propertyElement );

        PropertyType propertyType = featureType.getProperty( propertyName );
        if ( propertyType == null ) {
            throw new XMLParsingException( Messages.format( "ERROR_NO_PROPERTY_TYPE", propertyName ) );
        }

        if ( propertyType instanceof SimplePropertyType ) {
            int typeCode = propertyType.getType();
            String s = XMLTools.getStringValue( propertyElement );
            // TODO remove this hack
            if ( s == null || s.length() == 0 ) {
                return null;
            }
            if ( XMLTools.getNode( propertyElement, "text()", nsContext ) == null ) {
                return null;
            }
            s = s.trim();
            property = createSimpleProperty( s, propertyName, typeCode );
        } else if ( propertyType instanceof GeometryPropertyType ) {
            Element contentElement = XMLTools.getFirstChildElement( propertyElement );
            if ( contentElement == null ) {
                String msg = Messages.format( "ERROR_PROPERTY_NO_CHILD", propertyName, "geometry" );
                throw new XMLParsingException( msg );
            }
            property = createGeometryProperty( contentElement, propertyName );
        } else if ( propertyType instanceof MultiGeometryPropertyType ) {
            throw new XMLParsingException( "Handling of MultiGeometryPropertyType not "
                                           + "implemented in GMLFeatureDocument yet." );
        } else if ( propertyType instanceof FeaturePropertyType ) {
            Element contentElement = XMLTools.getFirstChildElement( propertyElement );
            if ( contentElement == null ) {
                // check if feature content is xlinked
                Text xlinkHref = (Text) XMLTools.getNode( propertyElement, "@xlink:href/text()",
                                                          nsContext );
                if ( xlinkHref == null ) {
                    String msg = Messages.format( "ERROR_INVALID_FEATURE_PROPERTY", propertyName );
                    throw new XMLParsingException( msg );
                }
                String href = xlinkHref.getData();
                if ( !href.startsWith( "#" ) ) {
                    String msg = Messages.format( "ERROR_EXTERNAL_XLINK_NOT_SUPPORTED", href );
                    throw new XMLParsingException( msg );
                }
                String fid = href.substring( 1 );
                property = new XLinkedFeatureProperty( propertyName, fid );
                xlinkPropertyList.add( (XLinkedFeatureProperty) property );
            } else {
                // feature content is given inline 
                Feature propertyValue = parseFeature( contentElement );
                property = FeatureFactory.createFeatureProperty( propertyName, propertyValue );
            }
        }
        return property;
    }

    protected void resolveXLinkReferences()
                            throws XMLParsingException {
        Iterator iter = this.xlinkPropertyList.iterator();
        while ( iter.hasNext() ) {
            XLinkedFeatureProperty xlinkProperty = (XLinkedFeatureProperty) iter.next();
            String fid = xlinkProperty.getTargetFeatureId();
            Feature targetFeature = this.featureMap.get( fid );
            if ( targetFeature == null ) {
                String msg = Messages.format( "ERROR_XLINK_NOT_RESOLVABLE", fid );
                throw new XMLParsingException( msg );
            }
            xlinkProperty.setValue( targetFeature );
        }
    }

    /**
     * Creates a simple property from the given parameters.
     * <p>
     * Converts the string value to the given target type.
     * 
     * @param s
     *            string value from a simple property to be converted
     * @param propertyName
     *            name of the simple property
     * @param typeCode
     *            target type code
     * @return property value in the given target type.
     * @throws XMLParsingException
     */
    private FeatureProperty createSimpleProperty( String s, QualifiedName propertyName, int typeCode )
                            throws XMLParsingException {

        Object propertyValue = null;
        switch ( typeCode ) {
        case Types.VARCHAR: {
            propertyValue = s;
            break;
        }
        case Types.INTEGER:
        case Types.SMALLINT: {
            try {
                propertyValue = new Integer( s );
            } catch ( NumberFormatException e ) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName,
                                              "Integer" );
                throw new XMLParsingException( msg );
            }
            break;
        }
        case Types.NUMERIC:
        case Types.DOUBLE: {
            try {
                propertyValue = new Double( s );
            } catch ( NumberFormatException e ) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName,
                                              "Double" );
                throw new XMLParsingException( msg );
            }
            break;
        }
        case Types.DECIMAL:
        case Types.FLOAT: {
            try {
                propertyValue = new Float( s );
            } catch ( NumberFormatException e ) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName, "Float" );
                throw new XMLParsingException( msg );
            }
            break;
        }
        case Types.BOOLEAN: {
            propertyValue = new Boolean( s );
            break;
        }
        case Types.DATE: {
            propertyValue = TimeTools.createCalendar( s ).getTime();
            break;
        }
        default: {
            String typeString = "" + typeCode;
            try {
                typeString = Types.getTypeNameForSQLTypeCode( typeCode );
            } catch ( UnknownTypeException e ) {
                LOG.logError( "No type name for code: " + typeCode );
            }
            String msg = Messages.format( "ERROR_UNHANDLED_TYPE", "" + typeString );
            LOG.logError( msg );
            throw new XMLParsingException( msg );
        }
        }
        FeatureProperty property = FeatureFactory.createFeatureProperty( propertyName,
                                                                         propertyValue );
        return property;
    }

    /**
     * Creates a geometry property from the given parameters.
     * 
     * @param contentElement
     *            child element of a geometry property to be converted
     * @param propertyName
     *            name of the geometry property
     * @return geometry property
     * @throws XMLParsingException
     */
    private FeatureProperty createGeometryProperty( Element contentElement,
                                                   QualifiedName propertyName )
                            throws XMLParsingException {

        Geometry propertyValue = null;
        try {
            propertyValue = GMLGeometryAdapter.wrap( contentElement );
        } catch ( GeometryException e ) {
            e.printStackTrace();
            String msg = Messages.format( "ERROR_CONVERTING_GEOMETRY_PROPERTY", propertyName,
                                          e.getMessage() );
            throw new XMLParsingException( msg );
        }

        FeatureProperty property = FeatureFactory.createFeatureProperty( propertyName,
                                                                         propertyValue );
        return property;
    }

    /**
     * Determines and retrieves the GML schemas that the document refers to.
     * 
     * @return the GML schemas that are attached to the document, keys are URIs (namespaces), values
     *         are GMLSchemas.
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    protected Map<URI, GMLSchema> getGMLSchemas()
                            throws XMLParsingException, UnknownCRSException {

        if ( this.gmlSchemaMap == null ) {
            gmlSchemaMap = new HashMap<URI, GMLSchema>();
            Map schemaMap = getAttachedSchemas();
            Iterator it = schemaMap.keySet().iterator();
            while ( it.hasNext() ) {
                URI nsURI = (URI) it.next();
                URL schemaURL = (URL) schemaMap.get( nsURI );
                GMLSchemaDocument schemaDocument = new GMLSchemaDocument();
                LOG.logDebug( "Retrieving schema document for namespace '" + nsURI + "' from URL '"
                              + schemaURL + "'." );
                try {
                    schemaDocument.load( schemaURL );
                    GMLSchema gmlSchema = schemaDocument.parseGMLSchema();
                    gmlSchemaMap.put( nsURI, gmlSchema );
                } catch ( IOException e ) {
                    String msg = Messages.format( "ERROR_RETRIEVING_SCHEMA", schemaURL,
                                                  e.getMessage() );
                    throw new XMLParsingException( msg );
                } catch ( SAXException e ) {
                    String msg = Messages.format( "ERROR_SCHEMA_NOT_XML", schemaURL, e.getMessage() );
                    throw new XMLParsingException( msg );
                } catch ( XMLParsingException e ) {
                    String msg = Messages.format( "ERROR_SCHEMA_PARSING1", schemaURL,
                                                  e.getMessage() );
                    throw new XMLParsingException( msg );
                }
            }
        }

        return this.gmlSchemaMap;
    }

    /**
     * Returns the GML schema for the given namespace.
     * 
     * @param ns
     * @return the GML schema for the given namespace if it is declared, null otherwise.
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    protected GMLSchema getSchemaForNamespace( URI ns )
                            throws XMLParsingException, UnknownCRSException {
        Map<URI, GMLSchema> gmlSchemaMap = getGMLSchemas();
        GMLSchema schema = gmlSchemaMap.get( ns );
        return schema;
    }

    /**
     * Returns the feature type with the given name.
     * <p>
     * If schema information is available and a feature type with the given name is not defined,
     * an XMLParsingException is thrown.
     * 
     * @param ftName
     *            feature type to look up
     * @return the feature type with the given name if it is declared, null otherwise.
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    protected FeatureType getFeatureType( QualifiedName ftName )
                            throws XMLParsingException, UnknownCRSException {
        FeatureType featureType = null;
        if ( this.gmlSchemaMap != null ) {
            GMLSchema schema = getSchemaForNamespace( ftName.getNamespace() );
            if ( schema == null ) {
                String msg = Messages.format( "ERROR_SCHEMA_NO_SCHEMA_FOR_NS",
                                              ftName.getNamespace() );
                throw new XMLParsingException( msg );
            }
            featureType = schema.getFeatureType( ftName );
            if ( featureType == null ) {
                String msg = Messages.format( "ERROR_SCHEMA_FEATURE_TYPE_UNKNOWN", ftName );
                throw new XMLParsingException( msg );
            }
        }
        return featureType;
    }

    /**
     * Parses the feature id attribute from the given feature element.
     * <p>
     * Looks after 'gml:id' (GML 3 style) first, if no such attribute is present, the 'fid' (GML 2
     * style) attribute is used.
     * 
     * @param featureElement
     * @return the feature id, this is "" if neither a 'gml:id' nor a 'fid' attribute is present
     */
    protected String parseFeatureId( Element featureElement ) {
        String fid = featureElement.getAttributeNS( GMLID_NS, GMLID );
        if ( fid.length() == 0 ) {
            fid = featureElement.getAttribute( FID );
        }
        return fid;
    }

    /**
     * Returns the feature type for the given feature element.
     * <p>
     * If a schema defines a feature type with the element's name, it is returned. Otherwise, a
     * feature type is generated that matches the child elements (properties) of the feature.
     * 
     * @param element
     *            feature element
     * @return the feature type.
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private FeatureType getFeatureType( Element element )
                            throws XMLParsingException, UnknownCRSException {
        QualifiedName ftName = getQualifiedName( element );
        FeatureType featureType = getFeatureType( ftName );
        if ( featureType == null ) {
            LOG.logDebug( "Feature type '" + ftName
                          + "' is not defined in schema. Generating feature type dynamically." );
            featureType = generateFeatureType( element );
        }
        return featureType;
    }

    /**
     * Method to create a <code>FeatureType</code> from the child elements (properties) of the
     * given feature element. Used if no schema (=FeatureType definition) is available.
     * 
     * @param element
     *            feature element
     * @return the generated feature type.
     * @throws XMLParsingException 
     */
    private FeatureType generateFeatureType( Element element )
                            throws XMLParsingException {
        ElementList el = XMLTools.getChildElements( element );
        ArrayList<PropertyType> propertyList = new ArrayList<PropertyType>( el.getLength() );

        for ( int i = 0; i < el.getLength(); i++ ) {
            Element propertyElement = el.item( i );
            QualifiedName propertyName = getQualifiedName( propertyElement );

            if ( !propertyName.equals( PROP_NAME_BOUNDED_BY )
                 && !propertyName.equals( PROP_NAME_NAME )
                 && !propertyName.equals( PROP_NAME_DESCRIPTION ) ) {
                PropertyType propertyType = determinePropertyType( propertyElement, propertyName );
                if ( !propertyList.contains( propertyType ) ) {
                    propertyList.add( propertyType );
                }
            }
        }

        PropertyType[] properties = new PropertyType[propertyList.size()];
        properties = propertyList.toArray( properties );
        QualifiedName ftName = getQualifiedName( element );
        FeatureType featureType = FeatureFactory.createFeatureType( ftName, false, properties );

        return featureType;
    }

    /**
     * Determines the property type for the given property element heuristically.
     * 
     * @param propertyElement
     *            property element
     * @param propertyName
     *            qualified name of the property element
     * @return the property type.
     * @throws XMLParsingException 
     */
    private PropertyType determinePropertyType( Element propertyElement, QualifiedName propertyName )
                            throws XMLParsingException {

        PropertyType propertyType = null;
        ElementList childList = XMLTools.getChildElements( propertyElement );

        // xlink attr present -> feature property
        Attr xlink = (Attr) XMLTools.getNode( propertyElement, "@xlink:href", nsContext );

        if ( childList.getLength() == 0 && xlink == null ) {
            // no child elements -> simple property
            String value = XMLTools.getStringValue( propertyElement );
            if ( value != null ) {
                value = value.trim();
            }
            propertyType = guessSimplePropertyType( value, propertyName );
        } else {
            // geometry or feature property
            if ( xlink != null ) {
                // TODO could be xlinked geometry as well
                propertyType = FeatureFactory.createFeaturePropertyType( propertyName, 0, -1 );
            } else {
                QualifiedName elementName = getQualifiedName( childList.item( 0 ) );
                if ( isGeometry( elementName ) ) {
                    propertyType = FeatureFactory.createGeometryPropertyType( propertyName,
                                                                              elementName, 0, -1 );
                } else {
                    // feature property
                    propertyType = FeatureFactory.createFeaturePropertyType( propertyName, 0, -1 );
                }
            }
        }
        return propertyType;
    }

    /**
     * Heuristically determines the simple property type from the given property value.
     * <p>
     * NOTE: This method may produce unwanted results, for example if an "xsd:string" property
     * contains a value that can be parsed as an integer, it is always determined as a numeric
     * property.
     * 
     * @param value
     *            string value to be used to determine property type
     * @param propertyName
     *            name of the property
     * @return the simple property type.
     */
    private SimplePropertyType guessSimplePropertyType( String value, QualifiedName propertyName ) {

        int typeCode = Types.VARCHAR;

        if ( this.guessSimpleTypes ) {
            // parseable as integer?
            try {
                Integer.parseInt( value );
                typeCode = Types.INTEGER;
            } catch ( NumberFormatException e ) {
                // so it's not an integer
            }

            // parseable as double?
            if ( typeCode == Types.VARCHAR ) {
                try {
                    Double.parseDouble( value );
                    typeCode = Types.NUMERIC;
                } catch ( NumberFormatException e ) {
                    // so it's not a double
                }
            }

            // parseable as ISO date?
            /*
             if (typeCode == Types.VARCHAR) {
             try {
             TimeTools.createCalendar( value );
             typeCode = Types.DATE;
             } catch (Exception e) {}
             }
             */
        }

        SimplePropertyType propertyType = FeatureFactory.createSimplePropertyType( propertyName,
                                                                                   typeCode, 0, -1 );
        return propertyType;
    }

    /**
     * Returns true if the given element name is a known GML geometry.
     * 
     * @param elementName
     * @return true if the given element name is a known GML geometry, false otherwise.
     */
    private boolean isGeometry( QualifiedName elementName ) {
        boolean isGeometry = false;
        if ( TYPE_NAME_BOX.equals( elementName ) || TYPE_NAME_LINESTRING.equals( elementName )
             || TYPE_NAME_MULTIGEOMETRY.equals( elementName )
             || TYPE_NAME_MULTILINESTRING.equals( elementName )
             || TYPE_NAME_MULTIPOINT.equals( elementName )
             || TYPE_NAME_MULTIPOLYGON.equals( elementName )
             || TYPE_NAME_POINT.equals( elementName ) || TYPE_NAME_POLYGON.equals( elementName )
             || TYPE_NAME_SURFACE.equals( elementName )
             || TYPE_NAME_MULTISURFACE.equals( elementName )
             || TYPE_NAME_CURVE.equals( elementName ) || TYPE_NAME_MULTICURVE.equals( elementName ) ) {
            isGeometry = true;
        }
        return isGeometry;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GMLFeatureDocument.java,v $
 Revision 1.32  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.31  2006/08/31 15:21:41  mschneider
 Javadoc improvements.

 Revision 1.30  2006/08/31 15:00:26  mschneider
 Added second constructor that allows to disable the guessing of simple types. Javadoc fixes.

 Revision 1.29  2006/08/21 15:47:59  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.28  2006/08/01 10:41:42  mschneider
 Added handling of empty fids ("").

 Revision 1.27  2006/07/25 15:54:43  mschneider
 Activated check for multiple features with same id in the document.

 Revision 1.26  2006/07/25 14:46:21  mschneider
 Javadoc corrections.

 Revision 1.25  2006/05/23 22:39:46  mschneider
 Fixed error message.

 Revision 1.24  2006/05/23 16:07:28  mschneider
 Changed visibility of parseProperty( Element propertyElement, FeatureType featureType ) to public.

 Revision 1.23  2006/05/03 17:26:40  poth
 *** empty log message ***

 Revision 1.22  2006/05/02 17:32:04  poth
 *** empty log message ***

 Revision 1.21  2006/04/27 09:44:59  poth
 *** empty log message ***

 Revision 1.20  2006/04/26 16:19:37  mschneider
 Synchronized createSimpleProperty() with AbstractSQLDatastore.convertToSQLType().

 Revision 1.19  2006/04/06 20:25:27  poth
 *** empty log message ***

 Revision 1.18  2006/04/04 20:39:42  poth
 *** empty log message ***

 Revision 1.17  2006/04/04 17:51:55  mschneider
 Fixed imports.

 Revision 1.16  2006/04/04 10:32:11  mschneider
 Adapted to XMLSchemaException changes.

 Revision 1.15  2006/03/31 08:13:13  poth
 *** empty log message ***

 Revision 1.14  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.13  2006/03/21 13:23:56  poth
 *** empty log message ***

 Revision 1.12  2006/03/03 13:36:50  mschneider
 Added handling of Floats. Improved error messages.

 Revision 1.11  2006/03/02 18:03:51  poth
 *** empty log message ***

 Revision 1.10  2006/02/23 15:30:41  mschneider
 Added hack to work around empty properties.

 Revision 1.9  2006/02/21 19:47:49  poth
 *** empty log message ***

 Revision 1.8  2006/02/05 18:52:49  mschneider
 Cleanup.

 Revision 1.7  2006/02/04 22:50:48  mschneider
 Added setSchemas() to explicitly specify the application schemas for this document.

 Revision 1.6  2006/01/31 16:24:43  mschneider
 Changes due to refactoring of org.deegree.model.feature package.

 Revision 1.5  2006/01/30 16:21:03  mschneider
 Moved resolveXLinkReferences() here.

 Revision 1.4  2006/01/24 16:13:17  poth
 *** empty log message ***

 Revision 1.3  2006/01/23 10:25:40  mschneider
 Added heuristic for determining Integer / Double / Date property types.
 Fixed handling of String property values.

 Revision 1.2  2006/01/20 18:13:47  mschneider
 Moved parsing functionality from GMLFeatureAdapter here.

 Revision 1.1  2006/01/19 16:18:14  mschneider
 Initial version.

 ********************************************************************** */