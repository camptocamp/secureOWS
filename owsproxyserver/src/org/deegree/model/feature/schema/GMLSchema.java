//$Header$
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
package org.deegree.model.feature.schema;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.ComplexTypeDeclaration;
import org.deegree.framework.xml.schema.ElementDeclaration;
import org.deegree.framework.xml.schema.SimpleTypeDeclaration;
import org.deegree.framework.xml.schema.UndefinedXSDTypeException;
import org.deegree.framework.xml.schema.XMLSchema;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * Represents a GML Application Schema document to provide easy access to it's components,
 * especially the <code>FeatureType</code> instances defined within.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GMLSchema extends XMLSchema {

    private final static ILogger LOG = LoggerFactory.getLogger( GMLSchema.class );

    private static URI XSDNS = CommonNamespaces.XSNS;

    private static URI GMLNS = CommonNamespaces.GMLNS;

    private static final QualifiedName ABSTRACT_FEATURE = new QualifiedName( "_Feature", GMLNS );

    // keys: QualifiedNames (feature type names), values: FeatureTypes
    protected Map<QualifiedName, FeatureType> featureTypeMap = new HashMap<QualifiedName, FeatureType>();

    // keys: FeatureTypes, values: List (of FeatureTypes)
    private Map<FeatureType, List<FeatureType>> substitutionMap = new HashMap<FeatureType, List<FeatureType>>();

    /**
     * Creates a new <code>GMLSchema</code> instance from the given parameters.
     * 
     * @param targetNamespace
     * @param simpleTypes
     * @param complexTypes
     * @param elementDeclarations
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    public GMLSchema( URI targetNamespace, SimpleTypeDeclaration[] simpleTypes,
                     ComplexTypeDeclaration[] complexTypes, ElementDeclaration[] elementDeclarations )
                            throws XMLParsingException, UnknownCRSException {
        super( targetNamespace, simpleTypes, complexTypes, elementDeclarations );
        buildFeatureTypeMap( elementDeclarations );
        buildSubstitutionMap( elementDeclarations );
    }

    // TODO remove this constructor
    protected GMLSchema( ElementDeclaration[] elementDeclarations, URI targetNamespace,
                        SimpleTypeDeclaration[] simpleTypes, ComplexTypeDeclaration[] complexTypes )
                            throws XMLSchemaException {
        super( targetNamespace, simpleTypes, complexTypes, elementDeclarations );
    }

    /**
     * Returns all <code>FeatureTypes</code>s that are defined in the document.
     * 
     * @return all FeatureTypes
     */
    public FeatureType[] getFeatureTypes() {
        return this.featureTypeMap.values().toArray( new FeatureType[this.featureTypeMap.size()] );
    }

    /**
     * Looks up the <code>FeatureType</code> with the given <code>QualifiedName</code>.
     * 
     * @param qName
     *            the QualifiedName to look up
     * @return the FeatureType, if it is defined in the document, null otherwise
     */
    public FeatureType getFeatureType( QualifiedName qName ) {
        return this.featureTypeMap.get( qName );
    }

    /**
     * Looks up the <code>FeatureType</code> with the given name.
     * 
     * @param localName
     *            the name to look up
     * @return the FeatureType, if it is defined in the document, null otherwise
     */
    public FeatureType getFeatureType( String localName ) {
        return getFeatureType( new QualifiedName( localName, getTargetNamespace() ) );
    }

    /**
     * Returns all non-abstract implementations of a given feature type that are defined in this
     * schema.
     * 
     * @param featureType
     * @return all non-abstract implementations of the feature type
     */
    public FeatureType[] getSubstitutions( FeatureType featureType ) {
        FeatureType[] substitutions = new FeatureType[0];
        List<FeatureType> featureTypeList = this.substitutionMap.get( featureType );
        if ( featureTypeList != null ) {
            substitutions = featureTypeList.toArray( new FeatureType[featureTypeList.size()] );
        }
        return substitutions;
    }

    /**
     * Returns whether the specified feature type is a valid substitution for the other specified
     * feature type (according to the schema).
     * 
     * @param ft
     * @param substitution
     * @return true, if it is valid substitution, false otherwise
     */
    public boolean isValidSubstitution( FeatureType ft, FeatureType substitution ) {
        FeatureType[] substitutions = getSubstitutions( ft );
        for ( int i = 0; i < substitutions.length; i++ ) {
            if ( substitutions[i].getName().equals( substitution.getName() ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the internal feature type map which is used to lookup feature types by name.
     * 
     * @param elementDeclarations
     *            element declarations to process, only element declarations that are substitutable
     *            for "gml:_Feature" are considered
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    protected void buildFeatureTypeMap( ElementDeclaration[] elementDeclarations )
                            throws XMLParsingException, UnknownCRSException {
        for ( int i = 0; i < elementDeclarations.length; i++ ) {
            LOG.logDebug( "Is element '" + elementDeclarations[i].getName()
                          + "' a feature type definition?" );
            if ( elementDeclarations[i].isSubstitutionFor( ABSTRACT_FEATURE ) ) {
                LOG.logDebug( "Yes." );
                FeatureType featureType = buildFeatureType( elementDeclarations[i] );
                featureTypeMap.put( featureType.getName(), featureType );
            } else {
                LOG.logDebug( "No." );
            }
        }
    }

    /**
     * Initializes the internal feature type substitution map which is used to lookup substitutions
     * for feature types.
     * <p>
     * NOTE: As this method relies on the feature type map,
     * #initializeFeatureTypeMap(ElementDeclaration[]) must have been executed before.
     * 
     * @see #buildFeatureTypeMap(ElementDeclaration[])
     * 
     * @param elementDeclarations
     *            element declarations of the feature types to process
     */
    protected void buildSubstitutionMap( ElementDeclaration[] elementDeclarations ) {
        Iterator iter = featureTypeMap.values().iterator();
        while ( iter.hasNext() ) {
            FeatureType featureType = (FeatureType) iter.next();
            List<FeatureType> substitutionList = new ArrayList<FeatureType>();
            LOG.logDebug( "Collecting possible substitutions for feature type '"
                          + featureType.getName() + "'." );
            for ( int i = 0; i < elementDeclarations.length; i++ ) {
                if ( elementDeclarations[i].isAbstract() ) {
                    LOG.logDebug( "Skipping '" + elementDeclarations[i].getName()
                                  + "' as it is abstract." );
                } else if ( elementDeclarations[i].isSubstitutionFor( featureType.getName() ) ) {
                    LOG.logDebug( "Feature type '" + elementDeclarations[i].getName()
                                  + "' is a concrete substitution for feature type '"
                                  + featureType.getName() + "'." );
                    FeatureType substitution = this.featureTypeMap.get( elementDeclarations[i].getName() );
                    substitutionList.add( substitution );
                }
            }
            this.substitutionMap.put( featureType, substitutionList );
        }
    }

    protected FeatureType buildFeatureType( ElementDeclaration element )
                            throws XMLParsingException, UnknownCRSException {
        LOG.logDebug( "Building feature type from element declaration '" + element.getName()
                      + "'..." );
        QualifiedName name = new QualifiedName( element.getName().getLocalName(),
                                                getTargetNamespace() );
        ComplexTypeDeclaration complexType = (ComplexTypeDeclaration) element.getType().getTypeDeclaration();
        ElementDeclaration[] subElements = complexType.getElements();
        PropertyType[] properties = new PropertyType[subElements.length];
        for ( int i = 0; i < properties.length; i++ ) {
            properties[i] = buildPropertyType( subElements[i] );
        }
        return FeatureFactory.createFeatureType( name, element.isAbstract(), properties );
    }

    protected PropertyType buildPropertyType( ElementDeclaration element )
                            throws XMLSchemaException {
        AbstractPropertyType propertyType = null;
        QualifiedName propertyName = new QualifiedName( element.getName().getLocalName(),
                                                        getTargetNamespace() );
        QualifiedName typeName = element.getType().getName();
        int type = determinePropertyType( element );
        if ( typeName == null ) {
            throw new XMLSchemaException( "No type defined for the property '" + propertyName
                                          + "'. No inline definitions supported." );
        }
        if ( typeName.isInNamespace( XSDNS ) ) {
            propertyType = FeatureFactory.createSimplePropertyType( propertyName, type,
                                                                    element.getMinOccurs(),
                                                                    element.getMaxOccurs() );
        } else {
            switch ( type ) {
            case Types.FEATURE: {
                propertyType = FeatureFactory.createFeaturePropertyType( propertyName,
                                                                         element.getMinOccurs(),
                                                                         element.getMaxOccurs() );
                break;
            }
            case Types.GEOMETRY: {
                propertyType = FeatureFactory.createGeometryPropertyType( propertyName, typeName,
                                                                          element.getMinOccurs(),
                                                                          element.getMaxOccurs() );
                break;
            }
            default: {
                // hack to make extended simple types work...
                propertyType = FeatureFactory.createSimplePropertyType( propertyName, type,
                                                                        element.getMinOccurs(),
                                                                        element.getMaxOccurs() );
                // throw new XMLSchemaException( "Unexpected type '"
                // + type + "' in buildPropertyType()." );
            }
            }
        }
        return propertyType;
    }

    /**
     * Heuristic method that tries to determine the type of GML property that is defined in an XSD
     * element declaration.
     * 
     * @param element
     *            <code>ElementDeclaration</code> that is a GML property definition
     * @return type code from <code>Types</code>
     * @throws UndefinedXSDTypeException
     * 
     * @see Types
     */
    protected final int determinePropertyType( ElementDeclaration element )
                            throws UndefinedXSDTypeException {
        QualifiedName typeName = element.getType().getName();
        LOG.logDebug( "Determining property type code for property type='" + typeName + "'..." );
        int type = Types.FEATURE;
        if ( element.getType().isAnonymous() ) {
            LOG.logDebug( "Inline declaration. Assuming generic GML feature of some kind." );
        } else if ( typeName.isInNamespace( XSDNS ) ) {
            LOG.logDebug( "Must be a basic XSD type." );
            try {
                type = Types.getJavaTypeForXSDType( typeName.getLocalName() );
            } catch ( UnknownTypeException e ) {
                throw new UndefinedXSDTypeException( e.getMessage(), e );
            }
        } else if ( typeName.isInNamespace( GMLNS ) ) {
            LOG.logDebug( "Maybe a geometry property type?" );
            try {
                type = Types.getJavaTypeForGMLType( typeName.getLocalName() );
                LOG.logDebug( "Yes." );
            } catch ( UnknownTypeException e ) {
                LOG.logDebug( "No. Must be a generic GML feature of some kind." );
            }
        } else {
            LOG.logDebug( "Should be a primitive type in our own namespace." );
            if ( !typeName.isInNamespace( getTargetNamespace() ) ) {
                throw new UndefinedXSDTypeException(
                                                     "Type '"
                                                                             + typeName
                                                                             + "' cannot be resolved (not in a supported namespace)." );
            }
            SimpleTypeDeclaration simpleType = getSimpleTypeDeclaration( typeName );
            if ( simpleType == null ) {
                throw new UndefinedXSDTypeException( "Simple type '" + typeName
                                                     + "' cannot be resolved." );
            }
            typeName = simpleType.getRestrictionBaseType().getName();
            LOG.logDebug( "Simple base type: '" + typeName + "'. Must be a basic XSD Type." );
            try {
                type = Types.getJavaTypeForXSDType( typeName.getLocalName() );
            } catch ( UnknownTypeException e ) {
                throw new UndefinedXSDTypeException( e );
            }
        }
        return type;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    public String toString() {

        Map<FeatureType, List<FeatureType>> substitutesMap = buildSubstitutesMap();

        StringBuffer sb = new StringBuffer( "GML schema targetNamespace='" );
        sb.append( getTargetNamespace() );
        sb.append( "'\n" );
        sb.append( "\n*** " );
        sb.append( featureTypeMap.size() );
        sb.append( " feature type declarations ***\n" );
        Iterator featureTypeIter = featureTypeMap.values().iterator();
        while ( featureTypeIter.hasNext() ) {
            FeatureType featureType = (FeatureType) featureTypeIter.next();
            sb.append( featureTypeToString( featureType, substitutesMap ) );
            if ( featureTypeIter.hasNext() ) {
                sb.append( "\n\n" );
            }
        }
        return sb.toString();
    }

    private Map<FeatureType, List<FeatureType>> buildSubstitutesMap() {

        Map<FeatureType, List<FeatureType>> substitutesMap = new HashMap<FeatureType, List<FeatureType>>();

        for ( FeatureType ft : getFeatureTypes() ) {
            List<FeatureType> substitutesList = new ArrayList<FeatureType>();
            for ( FeatureType substitution : getFeatureTypes() ) {
                if ( isValidSubstitution( substitution, ft ) ) {
                    substitutesList.add( substitution );
                }
            }
            substitutesMap.put( ft, substitutesList );
        }
        return substitutesMap;
    }

    private String featureTypeToString( FeatureType ft,
                                       Map<FeatureType, List<FeatureType>> substitutesMap ) {
        StringBuffer sb = new StringBuffer( "- Feature type '" );
        sb.append( ft.getName() );
        sb.append( "'\n" );

        FeatureType[] substFTs = getSubstitutions( ft );
        if ( substFTs.length > 0 ) {
            sb.append( "  is implemented by: " );
            for ( int i = 0; i < substFTs.length; i++ ) {
                sb.append ("'");                
                sb.append( substFTs[i].getName().getLocalName() );
                sb.append ("'");                
                if ( i != substFTs.length - 1 ) {
                    sb.append( "," );
                } else {
                    sb.append( "\n" );
                }
            }
        } else {
            sb.append( "  has no concrete implementations?!\n" );
        }

        List<FeatureType> substitutesList = substitutesMap.get( ft );
        sb.append( "  substitutes      : " );
        for ( int i = 0; i < substitutesList.size(); i++ ) {
            sb.append ("'");
            sb.append( substitutesList.get( i ).getName().getLocalName() );
            sb.append ("'");
            if ( i != substitutesList.size() - 1 ) {
                sb.append( "," );
            }
        }
        sb.append( "\n" );

        PropertyType[] properties = ft.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            sb.append( " + '" );
            sb.append( properties[i].getName() );
            sb.append( "', type: " );
            try {
                sb.append( Types.getTypeNameForSQLTypeCode( properties[i].getType() ) );
            } catch ( UnknownTypeException e ) {
                sb.append( "unknown" );
            }
            sb.append( ", min: " );
            sb.append( properties[i].getMinOccurs() );
            sb.append( ", max: " );
            sb.append( properties[i].getMaxOccurs() );
            if ( i != properties.length - 1 ) {
                sb.append( "\n" );
            }
        }
        return sb.toString();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log$
 Revision 1.26  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.25  2006/11/23 15:23:35  mschneider
 Improved #toString().

 Revision 1.24  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.23  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.22  2006/08/21 15:48:46  mschneider
 Changes due to removing of (unused + outdated) FeatureArrayPropertyType.

 Revision 1.21  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */