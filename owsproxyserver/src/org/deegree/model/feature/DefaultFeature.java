//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/DefaultFeature.java,v 1.46 2006/10/16 09:34:59 poth Exp $
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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathStep;

/**
 * Features are, according to the Abstract Specification, digital representations of real world
 * entities. Feature Identity thus refers to mechanisms to identify such representations: not to
 * identify the real world entities that are the subject of a representation. Thus two different
 * representations of a real world entity (say the Mississippi River) will be two different features
 * with distinct identities. Real world identification systems, such as title numbers, while
 * possibly forming a sound basis for an implementation of a feature identity mechanism, are not of
 * themselves such a mechanism.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.46 $ $Date: 2006/10/16 09:34:59 $
 */
public class DefaultFeature extends AbstractFeature implements Serializable {

    private static final long serialVersionUID = -1636744791597960465L;

    // key class: QualifiedName
    // value class: FeatureProperty []
    protected Map propertyMap;

    protected FeatureProperty[] properties;

    protected Object[] propertyValues;

    protected Geometry[] geometryPropertyValues;

    /**
     * Creates a new instance of <code>DefaultFeature</code> from the given parameters.
     * 
     * @param id
     * @param featureType
     * @param properties
     *            properties of the new feature, given in their intended order
     */
    protected DefaultFeature( String id, FeatureType featureType, FeatureProperty[] properties ) {
        this( id, featureType, properties, null );
    }

    /**
     * Creates a new instance of <code>DefaultFeature</code> from the given parameters.
     * 
     * @param id
     * @param featureType
     * @param properties
     *            properties of the new feature, given in their intended order
     * @param owner
     */
    protected DefaultFeature( String id, FeatureType featureType, FeatureProperty[] properties,
                             FeatureProperty owner ) {
        super( id, featureType, owner );
        for ( int i = 0; i < properties.length; i++ ) {
            FeatureProperty property = properties[i];
            URI namespace = property.getName().getNamespace();
            if ( ( namespace == null ) ) {
                PropertyType propertyType = featureType.getProperty( property.getName() );
                if ( propertyType == null ) {
                    throw new IllegalArgumentException(
                                                        "Unknown property '"
                                                                                + property.getName()
                                                                                + "' for feature with type '"
                                                                                + featureType.getName()
                                                                                + "': the feature type has no such property." );
                }
            } else if ( ( !namespace.equals( CommonNamespaces.GMLNS ) ) ) {
                PropertyType propertyType = featureType.getProperty( property.getName() );
                if ( propertyType == null ) {
                    throw new IllegalArgumentException(
                                                        "Unknown property '"
                                                                                + property.getName()
                                                                                + "' for feature with type '"
                                                                                + featureType.getName()
                                                                                + "': the feature type has no such property." );
                }
            }
        }
        this.properties = properties;
    }

    /**
     * TODO type checks!
     * 
     * @throws FeatureException
     */
    public void validate()
                            throws FeatureException {
        if ( this.propertyMap == null ) {
            this.propertyMap = buildPropertyMap();
        }
        PropertyType[] propertyTypes = featureType.getProperties();
        for ( int i = 0; i < propertyTypes.length; i++ ) {
            List propertyList = (List) this.propertyMap.get( propertyTypes[i].getName() );
            if ( propertyList == null ) {
                if ( propertyTypes[i].getMinOccurs() != 0 ) {
                    throw new FeatureException( "Feature is not a valid instance of type '"
                                                + featureType.getName() + "', mandatory property '"
                                                + propertyTypes[i].getName() + "' is missing." );
                }
            } else {
                if ( propertyTypes[i].getMinOccurs() > propertyList.size() ) {
                    throw new FeatureException( "Feature is not a valid instance of type '"
                                                + featureType.getName() + "', property '"
                                                + propertyTypes[i].getName() + "' has minOccurs="
                                                + propertyTypes[i].getMinOccurs()
                                                + ", but is only present " + propertyList.size()
                                                + " times." );
                }
                if ( propertyTypes[i].getMaxOccurs() != -1
                     && propertyTypes[i].getMaxOccurs() < propertyList.size() ) {
                    throw new FeatureException( "Feature is not a valid instance of type '"
                                                + featureType.getName() + "', property '"
                                                + propertyTypes[i].getName() + "' has maxOccurs="
                                                + propertyTypes[i].getMaxOccurs()
                                                + ", but is present " + propertyList.size()
                                                + " times." );
                }
            }
        }
    }

    /**
     * Builds a map for efficient lookup of the feature's properties by name.
     * 
     * @return key class: QualifiedName, value class: FeatureProperty []
     */
    private Map buildPropertyMap() {
        Map propertyMap = new HashMap();
        for ( int i = 0; i < this.properties.length; i++ ) {
            List propertyList = (List) propertyMap.get( this.properties[i].getName() );
            if ( propertyList == null ) {
                propertyList = new ArrayList();
            }
            propertyList.add( properties[i] );
            propertyMap.put( properties[i].getName(), propertyList );
        }
        Iterator propertyNameIter = propertyMap.keySet().iterator();
        while ( propertyNameIter.hasNext() ) {
            QualifiedName propertyName = (QualifiedName) propertyNameIter.next();
            List propertyList = (List) propertyMap.get( propertyName );
            propertyMap.put( propertyName,
                             propertyList.toArray( new FeatureProperty[propertyList.size()] ) );
        }
        return propertyMap;
    }

    /**
     * Builds a map for efficient lookup of the feature's properties by name.
     * 
     * @return key class: QualifiedName, value class: FeatureProperty []
     */
    private Geometry[] extractGeometryPropertyValues() {
        List geometryPropertiesList = new ArrayList();
        for ( int i = 0; i < this.properties.length; i++ ) {
            if ( this.properties[i].getValue() instanceof Geometry ) {
                geometryPropertiesList.add( this.properties[i].getValue() );
            } else if ( this.properties[i].getValue() instanceof Object[] ) {
                Object[] objects = (Object[]) this.properties[i].getValue();
                for ( int j = 0; j < objects.length; j++ ) {
                    if ( objects[j] instanceof Geometry ) {
                        geometryPropertiesList.add( objects[j] );
                    }
                }

            }
        }
        Geometry[] geometryPropertyValues = new Geometry[geometryPropertiesList.size()];
        geometryPropertyValues = (Geometry[]) geometryPropertiesList.toArray( geometryPropertyValues );
        return geometryPropertyValues;
    }

    /**
     * Returns all properties of the feature in their original order.
     * 
     * @return all properties of the feature
     */
    public FeatureProperty[] getProperties() {
        return this.properties;
    }

    /**
     * Returns the properties of the feature with the given name in their original order.
     * 
     * @return the properties of the feature with the given name or null if the feature has no
     *         property with that name
     */
    public FeatureProperty[] getProperties( QualifiedName name ) {        
        if ( this.propertyMap == null ) {
            this.propertyMap = buildPropertyMap();
        }
        FeatureProperty[] properties = (FeatureProperty[]) propertyMap.get( name );
        return properties;
    }

    /**
     * Returns the property of the feature identified by the given PropertyPath in their original
     * order.
     * 
     * NOTE: Current implementation does not handle multiple properties (on the path) or index
     * addressing in the path.
     * 
     * @return the properties of the feature identified by the given PropertyPath or null if the
     *         feature has no such properties
     * 
     * @see PropertyPath
     */
    public FeatureProperty getDefaultProperty( PropertyPath path )
                            throws PropertyPathResolvingException {

        Feature currentFeature = this;
        FeatureProperty currentProperty = null;
        for ( int i = 0; i < path.getSteps(); i += 2 ) {
            QualifiedName propertyName = path.getStep( i ).getPropertyName();
            currentProperty = currentFeature.getDefaultProperty( propertyName );
            if ( i + 1 < path.getSteps() ) {
                QualifiedName featureName = path.getStep( i + 1 ).getPropertyName();
                Object value = currentProperty.getValue();
                if ( !( value instanceof Feature ) ) {
                    throw new PropertyPathResolvingException( "PropertyPath '" + path
                                                              + "' cannot be matched to feature. Value of property '"
                                                              + propertyName
                                                              + "' is not a feature, but the path does not stop there." );
                }
                currentFeature = (Feature) value;
                if ( !featureName.equals( currentFeature.getName() ) ) {
                    throw new PropertyPathResolvingException("PropertyPath '" + path
                                                             + "' cannot be matched to feature. Property '"
                                                             + propertyName
                                                             + "' contains a feature with name '"
                                                             + currentFeature.getName()
                                                             + "', but requested was: '"
                                                             + featureName + "'." );
                }
            }
        }
        if ( currentProperty == null ) {
            List<PropertyPathStep> list = path.getAllSteps();            
            currentProperty = 
                new DefaultFeatureProperty( list.get( list.size()-1 ).getPropertyName(), null );
        }
        return currentProperty; 
    }

    /**
     * Returns the first property of the feature with the given name.
     * 
     * @return the first property of the feature with the given name or null if the feature has no
     *         such property
     */
    public FeatureProperty getDefaultProperty( QualifiedName name ) {
        FeatureProperty[] properties = getProperties( name );
        if ( properties != null ) {
            return properties[0];
        }
        return new DefaultFeatureProperty(name, null );
    }

    /**
     * Returns the properties of the feature at the submitted index of the feature type definition.
     * 
     * @return the properties of the feature at the submitted index
     * @deprecated
     */
    public FeatureProperty[] getProperties( int index ) {
        QualifiedName s = featureType.getPropertyName( index );
        return getProperties( s );
    }

    /**
     * Returns the values of all geometry properties of the feature.
     * 
     * @return the values of all geometry properties of the feature, or a zero-length array if the
     *         feature has no geometry properties
     */
    public Geometry[] getGeometryPropertyValues() {
        if ( this.geometryPropertyValues == null ) {
            this.geometryPropertyValues = extractGeometryPropertyValues();
        }
        return this.geometryPropertyValues;
    }

    /**
     * Returns the value of the default geometry property of the feature. If the feature has no
     * geometry property, this is a Point at the coordinates (0,0).
     * 
     * @return default geometry or Point at (0,0) if feature has no geometry
     */
    public Geometry getDefaultGeometryPropertyValue() {
        Geometry[] geometryValues = getGeometryPropertyValues();
        if ( geometryValues.length < 1 ) {
            return GeometryFactory.createPoint( 0, 0, null );
        }
        return geometryValues[0];
    }

    /**
     * Sets the value for the given property. The index is needed to specify the occurences of the
     * property that is to be replaced. Set to 0 for properties that may only occur once.
     * 
     * @param property
     *            property name and the property's new value
     * @param index
     *            position of the property that is to be replaced
     */
    public void setProperty( FeatureProperty property, int index ) {
        FeatureProperty[] oldProperties = getProperties( property.getName() );
        if ( oldProperties == null ) {
            throw new IllegalArgumentException( "Cannot set property '" + property.getName()
                                                + "': feature has no property with that name." );
        }
        if ( index > oldProperties.length - 1 ) {
            throw new IllegalArgumentException( "Cannot set property '" + property.getName()
                                                + "' with index " + index + ": feature has only "
                                                + oldProperties.length
                                                + " properties with that name." );
        }
        oldProperties[index].setValue( property.getValue() );
        this.geometryPropertyValues = extractGeometryPropertyValues();
    }

    /**
     * Adds the given property to the feature's properties. The position of the property is
     * determined by the feature type. If the feature already has properties with this name, the
     * new one is inserted directly behind them.
     * 
     * @param property
     *            property to insert
     */
    public void addProperty( FeatureProperty property ) {

        FeatureProperty[] newProperties;

        if ( this.properties == null ) {
            newProperties = new FeatureProperty[] { property };
        } else {
            newProperties = new FeatureProperty[this.properties.length + 1];
            for ( int i = 0; i < this.properties.length; i++ ) {
                newProperties[i] = this.properties[i];
            }
            // TODO insert at correct position
            newProperties[this.properties.length] = property;
        }
        this.properties = newProperties;
        this.propertyMap = buildPropertyMap();
        this.geometryPropertyValues = extractGeometryPropertyValues();
    }

    /**
     * Removes the properties with the given name.
     * 
     * @param propertyName
     *            name of the properties to remove
     */
    public void removeProperty( QualifiedName propertyName ) {
        // TODO
    }

    /**
     * Replaces the given property with a new one.
     * 
     * @param oldProperty
     *            property to be replaced
     * @param newProperty
     *            new property
     */
    public void replaceProperty( FeatureProperty oldProperty, FeatureProperty newProperty ) {
        for ( int i = 0; i < properties.length; i++ ) {
            if ( properties[i] == oldProperty ) {
                properties[i] = newProperty;
            }
        }
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    public String toString() {
        String ret = getClass().getName();
        /*
         ret = "\nid = " + getId() + "\n";
         ret += "featureType = " + featureType + "\n";
         ret += "geoProps = " + geometryPropertyValues + "\n";
         ret += "properties = " + propertyMap + "\n";
         */
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultFeature.java,v $
Revision 1.46  2006/10/16 09:34:59  poth
enbaled default value return for Feature.getDefaultProperty and FeatureProperty.getValue

Revision 1.45  2006/08/30 16:59:43  mschneider
Improved javadoc.

Revision 1.44  2006/08/24 06:40:27  poth
File header corrected

Revision 1.43  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
