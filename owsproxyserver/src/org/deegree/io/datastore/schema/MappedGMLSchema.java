//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/MappedGMLSchema.java,v 1.43 2006/12/04 18:22:14 mschneider Exp $
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
package org.deegree.io.datastore.schema;

import java.net.URI;
import java.util.Iterator;
import java.util.Properties;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.ComplexTypeDeclaration;
import org.deegree.framework.xml.schema.ElementDeclaration;
import org.deegree.framework.xml.schema.SimpleTypeDeclaration;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreConfiguration;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreRegistry;
import org.deegree.io.datastore.idgenerator.IdGenerator;
import org.deegree.io.datastore.schema.MappedGMLId.IDPART_INFO;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.AbstractPropertyType;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.feature.schema.UndefinedFeatureTypeException;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;

/**
 * Represents a GML Application Schema document which is annotated with mapping (persistence)
 * information.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.43 $, $Date: 2006/12/04 18:22:14 $
 */
public class MappedGMLSchema extends GMLSchema {

    private final static ILogger LOG = LoggerFactory.getLogger( MappedGMLSchema.class );

    private static URI XSDNS = CommonNamespaces.XSNS;

    private MappedGMLSchemaDocument doc;

    private Datastore datastore;

    private boolean suppressXLinkOutput;

    private String namespacePrefix;

    private URI defaultSRS;

    private CoordinateSystem defaultCS;

    // TODO remove this hack (which is used to mark the first feature type as visible by default)
    private boolean firstFeatureType = true;

    /**
     * Creates a new <code>MappedGMLSchema</code> instance from the given parameters.
     * 
     * @param targetNamespace
     * @param simpleTypes
     * @param complexTypes
     * @param elementDeclarations
     * @param namespacePrefix
     * @param defaultSRS
     * @param backendConfiguration
     * @param suppressXLinkOutput 
     * @param doc
     * @throws UnknownCRSException 
     * @throws XMLSchemaException
     */
    MappedGMLSchema( URI targetNamespace, SimpleTypeDeclaration[] simpleTypes,
                    ComplexTypeDeclaration[] complexTypes,
                    ElementDeclaration[] elementDeclarations, String namespacePrefix,
                    URI defaultSRS, DatastoreConfiguration backendConfiguration,
                    boolean suppressXLinkOutput, MappedGMLSchemaDocument doc )
                            throws XMLParsingException, UnknownCRSException {

        super( elementDeclarations, targetNamespace, simpleTypes, complexTypes );

        this.doc = doc;
        this.namespacePrefix = namespacePrefix;
        this.defaultSRS = defaultSRS;
        this.defaultCS = CRSFactory.create( defaultSRS.toString() );
        this.datastore = registerDatastore( backendConfiguration );
        this.suppressXLinkOutput = suppressXLinkOutput;

        buildFeatureTypeMap( elementDeclarations );
        buildSubstitutionMap( elementDeclarations );
        resolveFeatureTypeReferences();
        resolveTargetTables();
        checkIdentityPartConsistency();

        try {
            this.datastore.bindSchema( this );
        } catch ( DatastoreException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLParsingException( e.getMessage() );
        }
    }

    /**
     * Checks for all feature type definitions if it's featureIds 'identityPart' setting is valid:
     * <ul>
     * <li>if there is a direct fk from the feature's table to another feature table,
     * 'identityPart' must be true</li>
     * <li>if there is no explicit setting for the feature type, the implied setting is used,
     * otherwise it is checked for validity</li>
     * </ul>
     * 
     * @throws XMLSchemaException
     */
    private void checkIdentityPartConsistency()
                            throws XMLSchemaException {
        for ( FeatureType ft : this.featureTypeMap.values() ) {
            MappedFeatureType mft = (MappedFeatureType) ft;
            PropertyType[] properties = mft.getProperties();
            for ( int i = 0; i < properties.length; i++ ) {
                MappedPropertyType property = (MappedPropertyType) properties[i];
                if ( property instanceof MappedFeaturePropertyType ) {
                    MappedFeaturePropertyType featurePT = (MappedFeaturePropertyType) property;
                    TableRelation[] relations = featurePT.getTableRelations();
                    if ( relations.length == 1 ) {
                        if ( relations[0].getFKInfo() == TableRelation.FK_INFO.fkIsToField ) {
                            MappedFeatureType targetFT = featurePT.getFeatureTypeReference().getFeatureType();
                            MappedGMLId id = targetFT.getGMLId();
                            if ( id.getIdPartInfo() == IDPART_INFO.noIDInfo ) {
                                String msg = "FeatureId for feature type '"
                                             + targetFT.getName()
                                             + "' has to be part of the feature's identity - feature table "
                                             + "is a property of feature type '" + mft.getName()
                                             + "' and stores a fk.";
                                LOG.logInfo( msg );
                            } else if ( id.getIdPartInfo() == IDPART_INFO.notIDPart ) {
                                String msg = "Invalid schema annotation: "
                                             + "FeatureId for feature type '"
                                             + targetFT.getName()
                                             + "' has to be part of the feature's identity - feature table "
                                             + "is a property of feature type '" + mft.getName()
                                             + "' and stores a fk. Set 'identityPart' to true for "
                                             + "feature type '" + targetFT.getName() + "'.";
                                throw new XMLSchemaException( msg );
                            }
                            id.setIdentityPart( true );
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves a <code>Datastore</code> instance for the given configuration.
     * <p>
     * If a datastore with exactly the same configuration exists, the existing instance is returned.
     * 
     * @param backendConfiguration
     * @throws XMLSchemaException
     */
    private Datastore registerDatastore( DatastoreConfiguration backendConfiguration )
                            throws XMLSchemaException {
        Datastore datastore = DatastoreRegistry.getDatastore( backendConfiguration );
        if ( datastore == null ) {
            try {
                datastore = (Datastore) backendConfiguration.getDatastoreClass().newInstance();
                datastore.configure( backendConfiguration );
            } catch ( DatastoreException e ) {
                String msg = "Error configuring datastore with configuration '"
                             + backendConfiguration + "'.";
                LOG.logError( msg, e );
                throw new XMLSchemaException( msg, e );
            } catch ( Exception e ) {
                String msg = "Error instantiating datastore for class '"
                             + backendConfiguration.getDatastoreClass() + "'.";
                LOG.logError( msg, e );
                throw new XMLSchemaException( msg, e );
            }
            try {
                DatastoreRegistry.registerDatastore( datastore );
            } catch ( DatastoreException e ) {
                String msg = "Error registering datastore with configuration '"
                             + backendConfiguration + "'.";
                LOG.logError( msg, e );
                throw new XMLSchemaException( msg, e );
            }
        }
        return datastore;
    }

    /**
     * Returns the underlying GML Application Schema document.
     * 
     * @return the underlying GML Application Schema document
     */
    public MappedGMLSchemaDocument getDocument() {
        return this.doc;
    }

    /**
     * Returns the {@link Datastore} instance that handles this schema.
     * 
     * @return the Datastore instance that handles this schema
     */
    public Datastore getDatastore() {
        return this.datastore;
    }

    /**
     * Returns whether GML output (of the associated datastore) will not use any XLinks.
     * 
     * @return true, if the GML output will not use XLinks, false otherwise
     */
    public boolean suppressXLinkOutput() {
        return this.suppressXLinkOutput;
    }

    /**
     * Returns the default SRS for all geometry properties in the schema.
     * 
     * @return the default SRS for all geometry properties in the schema
     */
    public URI getDefaultSRS() {
        return this.defaultSRS;
    }

    /**
     * Returns the default {@link CoordinateSystem} for all geometry properties in the schema.
     * 
     * @return the default CoordinateSystem for all geometry properties in the schema
     */
    public CoordinateSystem getDefaultCS() {
        return this.defaultCS;
    }

    /**
     * Builds a {@link MappedFeatureType} from the given element declaration.
     * 
     * @param element
     * @return feature type with persistence information
     * @throws XMLParsingException 
     * @throws UnknownCRSException 
     */
    @Override
    protected MappedFeatureType buildFeatureType( ElementDeclaration element )
                            throws XMLParsingException, UnknownCRSException {

        LOG.logDebug( "Building (mapped) feature type from element declaration '"
                      + element.getName() + "'..." );

        int visibleCode = -1;
        boolean isVisible = false;
        boolean isUpdatable = false;
        boolean isDeletable = false;
        boolean isInsertable = false;
        QualifiedName name = new QualifiedName( this.namespacePrefix,
                                                element.getName().getLocalName(),
                                                getTargetNamespace() );
        MappedComplexTypeDeclaration complexType = (MappedComplexTypeDeclaration) element.getType().getTypeDeclaration();

        // extract mapping information from element annotation
        Element annotationElement = ( (MappedElementDeclaration) element ).getAnnotation();
        MappedGMLId gmlId = null;
        String table = name.getLocalName().toLowerCase();
        // use complexType annotation, if no element annotation present
        if ( annotationElement == null ) {
            annotationElement = complexType.getAnnotation();
        }
        // neither element nor complexType annotation, then use default mapping
        if ( annotationElement == null ) {
            LOG.logInfo( "Declaration of feature type '" + name
                         + "' has no mapping information (annotation element). Defaulting to "
                         + "table name '" + table + "' and gmlId field 'fid' (not identity part)." );
            MappingField[] idFields = new MappingField[] { new MappingField( table, "fid",
                                                                             Types.VARCHAR ) };
            IdGenerator idGenerator = IdGenerator.getInstance( IdGenerator.TYPE_UUID,
                                                               new Properties() );
            gmlId = new MappedGMLId( name.getLocalName().toUpperCase(), "_", idFields, idGenerator,
                                     IDPART_INFO.noIDInfo );
        } else {
            gmlId = doc.extractGMLId( annotationElement, table );
            table = gmlId.getIdFields()[0].getTable();
            try {
                visibleCode = doc.parseVisible( annotationElement );
                isUpdatable = doc.parseIsUpdatable( annotationElement );
                isDeletable = doc.parseIsDeletable( annotationElement );
                isInsertable = doc.parseIsInsertable( annotationElement );
            } catch ( XMLParsingException e ) {
                throw new XMLSchemaException( e );
            }
        }

        ElementDeclaration[] subElements = complexType.getElements();
        PropertyType[] properties = new PropertyType[subElements.length];
        for ( int i = 0; i < subElements.length; i++ ) {
            MappedElementDeclaration subElement = (MappedElementDeclaration) subElements[i];
            properties[i] = buildPropertyType( subElement, table );
        }

        // default visibility for first feature type is true, for all others it's false
        if ( this.firstFeatureType ) {
            isVisible = true;
            if ( visibleCode == 0 ) {
                isVisible = false;
            }
            this.firstFeatureType = false;
        } else {
            if ( visibleCode == 1 ) {
                isVisible = true;
            }
        }

        return new MappedFeatureType( name, element.isAbstract(), properties, table, gmlId, this,
                                      isVisible, isUpdatable, isDeletable, isInsertable );
    }

    protected PropertyType buildPropertyType( MappedElementDeclaration element, String table )
                            throws XMLParsingException, UnknownCRSException {

        AbstractPropertyType propertyType;
        QualifiedName propertyName = new QualifiedName( this.namespacePrefix,
                                                        element.getName().getLocalName(),
                                                        getTargetNamespace() );

        int minOccurs = element.getMinOccurs();
        int maxOccurs = element.getMaxOccurs();

        QualifiedName typeName = element.getType().getName();
        LOG.logDebug( "Building (mapped) property type from element declaration '" + propertyName
                      + "', type='" + typeName + "'..." );
        int type = determinePropertyType( element );

        // extract mapping annotation
        Element annotationElement = element.getAnnotation();

        // get identityPart information from annotation
        int identityCode = -1;
        if ( annotationElement != null ) {
            identityCode = doc.parseIdentityPart( annotationElement );
        }

        if ( typeName.isInNamespace( XSDNS ) ) {
            // simple property (basic xsd type)
            if ( annotationElement == null ) {
                LOG.logDebug( "Using default mapping for property type '" + propertyName + "'." );
                String field = propertyName.getLocalName().toLowerCase();
                int typeCode = getDefaultSQLTypeForXSDType( typeName );
                MappingField mappingField = new MappingField( table, field, typeCode );
                propertyType = new MappedSimplePropertyType( propertyName, type, minOccurs,
                                                             maxOccurs, true, new TableRelation[0],
                                                             mappingField );
            } else {
                LOG.logDebug( "Parsing mapping information for simple property type." );
                boolean isIdentityPart = identityCode == 0 ? false : true;
                propertyType = doc.parseMappedSimplePropertyType( annotationElement, propertyName,
                                                                  type, minOccurs, maxOccurs,
                                                                  isIdentityPart, table );
            }
        } else {
            switch ( type ) {
            case Types.GEOMETRY: {
                // geometry property
                if ( annotationElement == null ) {
                    LOG.logDebug( "Using default mapping for property type '" + propertyName + "'." );
                    String field = propertyName.getLocalName().toLowerCase();
                    MappingGeometryField mappingField = new MappingGeometryField( table, field,
                                                                                  Types.OTHER, -1 );
                    propertyType = new MappedGeometryPropertyType( propertyName, typeName, type,
                                                                   minOccurs, maxOccurs, false,
                                                                   this.defaultSRS,
                                                                   new TableRelation[0],
                                                                   mappingField );
                } else {
                    LOG.logDebug( "Parsing mapping information for geometry property type." );
                    boolean isIdentityPart = identityCode == 1 ? true : false;
                    propertyType = doc.parseMappedGeometryPropertyType( annotationElement,
                                                                        propertyName, typeName,
                                                                        type, minOccurs, maxOccurs,
                                                                        isIdentityPart, table );
                }
                break;
            }
            case Types.FEATURE: {
                // feature property
                if ( annotationElement == null ) {
                    String msg = "Declaration of property type '" + propertyName
                                 + "' has no mapping information (annotation element missing).";
                    throw new XMLSchemaException( msg );
                }
                LOG.logDebug( "Parsing mapping information for feature property type." );
                boolean isIdentityPart = identityCode == 0 ? false : true;
                propertyType = doc.parseMappedFeaturePropertyType( annotationElement, propertyName,
                                                                   minOccurs, maxOccurs,
                                                                   isIdentityPart, table );
                break;
            }
            default: {
                // no known namespace -> assume simple property with user defined simple type 
                // TODO check for inherited types

                if ( annotationElement == null ) {
                    LOG.logDebug( "Using default mapping for property type '" + propertyName + "'." );
                    String field = propertyName.getLocalName().toLowerCase();
                    int typeCode = getDefaultSQLTypeForXSDType( typeName );
                    MappingField mappingField = new MappingField( table, field, typeCode );
                    propertyType = new MappedSimplePropertyType( propertyName, type, minOccurs,
                                                                 maxOccurs, true,
                                                                 new TableRelation[0], mappingField );
                } else {
                    LOG.logDebug( "Parsing mapping information for simple property type." );
                    boolean isIdentityPart = identityCode == 0 ? false : true;
                    propertyType = doc.parseMappedSimplePropertyType( annotationElement,
                                                                      propertyName, type,
                                                                      minOccurs, maxOccurs,
                                                                      isIdentityPart, table );
                }
            }
            }
        }
        return propertyType;
    }

    /**
     * @throws XMLSchemaException
     */
    private void resolveTargetTables()
                            throws XMLSchemaException {
        LOG.logDebug( "Resolving unspecified (null) table references for all FeaturePropertyTypes." );
        Iterator iter = featureTypeMap.values().iterator();
        while ( iter.hasNext() ) {
            resolveTargetTables( (MappedFeatureType) iter.next() );
        }
    }

    private void resolveTargetTables( MappedFeatureType type )
                            throws XMLSchemaException {
        PropertyType[] properties = type.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            MappedPropertyType property = (MappedPropertyType) properties[i];
            if ( property instanceof MappedFeaturePropertyType ) {
                resolveTargetTables( (MappedFeaturePropertyType) property );
            }
        }
    }

    private void resolveTargetTables( MappedFeaturePropertyType featurePT )
                            throws XMLSchemaException {
        MappedFeatureType targetFeatureType = featurePT.getFeatureTypeReference().getFeatureType();
        if ( !targetFeatureType.isAbstract() ) {
            TableRelation[] tableRelations = featurePT.getTableRelations();
            if ( tableRelations.length == 0 ) {
                String msg = "Invalid feature property mapping '" + featurePT.getName()
                             + ": no relation elements - feature properties cannot be embedded in "
                             + "feature tables directly, but must use key relations to reference "
                             + "subfeatures.";
                LOG.logError( msg );
                throw new XMLSchemaException( msg );
            }
            TableRelation lastRelation = tableRelations[tableRelations.length - 1];
            MappingField[] targetFields = lastRelation.getToFields();
            for ( int i = 0; i < targetFields.length; i++ ) {
                String table = targetFields[i].getTable();
                if ( table != null ) {
                    if ( !targetFeatureType.getTable().equals( table ) ) {
                        String msg = "Invalid feature property mapping: type '"
                                     + targetFeatureType.getName() + "' is bound to table '"
                                     + targetFeatureType.getTable()
                                     + "', but last table relation specifies table '" + table
                                     + "'.";
                        LOG.logError( msg );
                        throw new XMLSchemaException( msg );
                    }
                }
                targetFields[i].setTable( targetFeatureType.getTable() );
            }
        }
    }

    private void resolveFeatureTypeReferences()
                            throws UndefinedFeatureTypeException {
        LOG.logDebug( "Resolving (mapped) FeatureType references for namespace '"
                      + getTargetNamespace() + "'." );
        Iterator iter = featureTypeMap.values().iterator();
        while ( iter.hasNext() ) {
            resolveFeatureTypeReferences( (MappedFeatureType) iter.next() );
        }
    }

    private void resolveFeatureTypeReferences( MappedFeatureType featureType )
                            throws UndefinedFeatureTypeException {
        LOG.logDebug( "Resolving (mapped) FeatureType references in definition of FeatureType '"
                      + featureType.getName() + "'." );
        PropertyType[] properties = featureType.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            if ( properties[i] instanceof MappedFeaturePropertyType ) {
                MappedFeaturePropertyType featurePT = (MappedFeaturePropertyType) properties[i];
                resolveFeatureTypeReferences( featurePT.getFeatureTypeReference() );
            }
        }
    }

    private void resolveFeatureTypeReferences( MappedFeatureTypeReference reference )
                            throws UndefinedFeatureTypeException {
        LOG.logDebug( "Resolving (mapped) FeatureType references to FeatureType '"
                      + reference.getName() + "'." );
        if ( reference.isResolved() ) {
            LOG.logDebug( "Already resolved." );
        } else {
            MappedFeatureType featureType = (MappedFeatureType) getFeatureType( reference.getName() );
            if ( featureType == null ) {
                String msg = "Reference to feature type '" + reference.getName()
                             + "' in mapping annotation can not be resolved.";
                LOG.logDebug( msg );
                throw new UndefinedFeatureTypeException( msg );
            }
            reference.resolve( featureType );
            resolveFeatureTypeReferences( featureType );
        }
    }

    // TODO: implement this
    private int getDefaultSQLTypeForXSDType( @SuppressWarnings("unused")
    QualifiedName xsdTypeName ) {
        return -1;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "GML schema targetNamespace='" );
        sb.append( getTargetNamespace() );
        sb.append( "'\n" );
        sb.append( "\n*** " );
        sb.append( featureTypeMap.size() );
        sb.append( " feature type declarations ***\n" );
        Iterator featureTypeIter = featureTypeMap.values().iterator();
        while ( featureTypeIter.hasNext() ) {
            MappedFeatureType featureType = (MappedFeatureType) featureTypeIter.next();
            sb.append( featureTypeToString( featureType ) );
            if ( featureTypeIter.hasNext() ) {
                sb.append( "\n\n" );
            }
        }
        return sb.toString();
    }

    private String featureTypeToString( MappedFeatureType featureType ) {
        StringBuffer sb = new StringBuffer( "- " );
        if ( featureType.isAbstract() ) {
            sb.append( "(abstract) " );
        }
        sb.append( "Feature type '" );
        sb.append( featureType.getName() );
        sb.append( "' -> TABLE: '" );
        sb.append( featureType.getTable() + "'" );
        if ( featureType.isUpdatable() ) {
            sb.append( " updatable" );
        }
        if ( featureType.isDeletable() ) {
            sb.append( " deletable" );
        }
        if ( featureType.isInsertable() ) {
            sb.append( " insertable" );
        }
        sb.append( '\n' );
        PropertyType[] properties = featureType.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            sb.append( " + '" );
            sb.append( properties[i].getName() );
            sb.append( "', type: " );
            sb.append( properties[i].getType() );
            sb.append( ", minOccurs: " );
            sb.append( properties[i].getMinOccurs() );
            sb.append( ", maxOccurs: " );
            sb.append( properties[i].getMaxOccurs() );
            sb.append( " -> " );
            //sb.append( ( (MappedPropertyType) properties[i] ).getContents()[0] );
            if ( i != properties.length - 1 ) {
                sb.append( "\n" );
            }
        }
        return sb.toString();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: MappedGMLSchema.java,v $
 Revision 1.43  2006/12/04 18:22:14  mschneider
 Added error message in case no relation elements for a feature property type are present.

 Revision 1.42  2006/11/29 17:00:40  mschneider
 Changed internal SRS to integer.

 Revision 1.41  2006/11/27 09:07:51  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.40  2006/08/29 15:52:12  mschneider
 Fixed warnings.

 Revision 1.39  2006/08/28 16:43:25  mschneider
 Fixed @Override annotations.

 Revision 1.38  2006/08/23 12:18:02  mschneider
 Javadoc fixes.

 Revision 1.37  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.36  2006/08/21 16:42:36  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.35  2006/08/21 15:43:20  mschneider
 Cleanup. Added "content" subpackage. Removed (completely unused and outdated) FeatureArrayPropertyType.

 Revision 1.34  2006/07/10 20:54:00  mschneider
 Optimized formatting.

 Revision 1.33  2006/05/21 19:06:46  poth
 *** empty log message ***

 Revision 1.32  2006/05/01 20:15:26  poth
 *** empty log message ***

 Revision 1.31  2006/04/19 18:24:03  mschneider
 Added identity part for feature ids.

 Revision 1.30  2006/04/18 12:45:06  mschneider
 Added sanity checks to prevent common misconfigurations.

 Revision 1.29  2006/04/06 20:25:23  poth
 *** empty log message ***

 Revision 1.28  2006/04/04 20:39:41  poth
 *** empty log message ***

 Revision 1.27  2006/04/04 10:28:31  mschneider
 Added identityPart handling. Used for equality definition of feature types.

 Revision 1.26  2006/03/30 21:20:24  poth
 *** empty log message ***

 Revision 1.25  2006/03/02 18:03:51  poth
 *** empty log message ***

 Revision 1.24  2006/02/22 00:21:44  mschneider
 Renamed AbstractDatastore to Datastore.

 Revision 1.23  2006/02/17 14:34:21  mschneider
 Changed due to refactoring of GMLIdGenerator.

 Revision 1.22  2006/02/09 10:36:35  mschneider
 Fixed handling of "deegreewfs:visible" annotation.

 Revision 1.21  2006/02/08 17:42:10  mschneider
 Added handling of suppressXLinkOutput parameter.

 ********************************************************************** */