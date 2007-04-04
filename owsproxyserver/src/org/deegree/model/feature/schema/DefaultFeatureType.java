//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/schema/DefaultFeatureType.java,v 1.7 2006/08/24 06:40:27 poth Exp $
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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;

/**
 * Default implementation for GML feature types.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/08/24 06:40:27 $
 */
public class DefaultFeatureType implements FeatureType, Serializable {

    private static final long serialVersionUID = -4774232985192401467L;

    private QualifiedName name;

    private boolean isAbstract;

    private URI schemaLocation;

    private PropertyType[] properties;

    private GeometryPropertyType[] geometryProperties;

    /**
     * Creates a new instance of <code>DefaultFeatureType</code> from the given parameters.
     * 
     * @param name
     * @param isAbstract
     * @param properties
     */
    public DefaultFeatureType( QualifiedName name, boolean isAbstract, PropertyType[] properties ) {
        this.name = name;
        this.isAbstract = isAbstract;
        this.properties = properties;
    }

    /**
     * Creates a new instance of <code>DefaultFeatureType</code> from the given parameters.
     * 
     * @param name
     * @param isAbstract
     * @param schemaLocation
     * @param properties
     */
    public DefaultFeatureType( QualifiedName name, boolean isAbstract, URI schemaLocation,
                              PropertyType[] properties ) {
        this( name, isAbstract, properties );
        this.schemaLocation = schemaLocation;
    }

    /**
     * returns the name of the FeatureType
     */
    public QualifiedName getName() {
        return this.name;
    }

    /**
     * Returns whether this feature type is abstract or not.
     * 
     * @return true, if the feature type is abstract, false otherwise
     */
    public boolean isAbstract() {
        return this.isAbstract;
    }

    /**
     * returns the namespace of the feature type (maybe null)
     * 
     * @return
     */
    public URI getNameSpace() {
        return this.name.getNamespace();
    }

    /**
     * returns the location of the XML schema defintion assigned to a namespace
     * 
     * @return
     */
    public URI getSchemaLocation() {
        return this.schemaLocation;
    }

    /**
     * returns the name of the property a the passed index position
     */
    public QualifiedName getPropertyName( int index ) {
        return this.properties[index].getName();
    }

    /**
     * returns the properties of this feature type
     * 
     */
    public PropertyType[] getProperties() {
        return this.properties;
    }

    /**
     * returns a property of this feature type identified by its name
     */
    public PropertyType getProperty( QualifiedName name ) {
        PropertyType ftp = null;
        // TODO use Map for improved lookup
        for (int i = 0; i < this.properties.length; i++) {
            if ( this.properties[i].getName().getLocalName().equals( name.getLocalName() ) ) {
                URI u1 = this.properties[i].getName().getNamespace();
                URI u2 = name.getNamespace();
                if ( ( u1 == null && u2 == null ) ||
                     ( u1 != null && u1.equals( u2 ) ) ) {
                    ftp = this.properties[i];
                    break;
                }
            }
        }
        return ftp;
    }

    /**
     * Returns the spatial properties of the feature type.
     * 
     * @return
     */
    public GeometryPropertyType[] getGeometryProperties() {
        if ( this.geometryProperties == null ) {
            List geometryPropertyList = new ArrayList();
            for (int i = 0; i < properties.length; i++) {
                if ( properties[i].getType() == Types.GEOMETRY ) {
                    geometryPropertyList.add( properties[i] );
                }
            }
            
            this.geometryProperties = new GeometryPropertyType[geometryPropertyList.size()];
            this.geometryProperties = 
                (GeometryPropertyType[]) geometryPropertyList.toArray( this.geometryProperties );
            
        }
        return this.geometryProperties;
    }

    /**
     * returns true if the passed FeatureType equals this FeatureType. Two FeatureTypes are equal if
     * they have the same qualified name
     * 
     * @return
     */
    public boolean equals( FeatureType featureType ) {
        return featureType.getName().equals( this.name );
    }

    /**
     * returns true if <code>other</code> is of type
     * 
     * @see FeatureType and #equals(FeatureType) is true
     */
    public boolean equals( Object other ) {
        if ( other instanceof FeatureType ) {
            return equals( (FeatureType) other );
        }
        return false;
    }

    public String toString() {
        String ret = "";
        ret += "name = " + name + "\n";
        ret += "properties = ";
        for (int i = 0; i < properties.length; i++) {
            ret += properties[i].getName()
                + " " + properties[i].getType() + "\n";
        }
        return ret;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultFeatureType.java,v $
Revision 1.7  2006/08/24 06:40:27  poth
File header corrected

Revision 1.6  2006/07/04 20:15:19  poth
comments corrected - bug fix: consider namespace URIs when comparing feature type property names not just local names


********************************************************************** */