//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/graphics/displayelements/ScaledFeature.java,v 1.22 2006/07/04 19:09:06 poth Exp $
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
package org.deegree.graphics.displayelements;

import java.util.HashMap;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.PropertyPath;

/**
 * This class is a wrapper for a Feature and a Feature itself. It adds a special behavior/property
 * to a feature that is required by deegree DisplayElements. This special behavior is an additional
 * property named "$SCALE". In oposite to conventional properties this one can change its value
 * during lifetime of a feature without changing the underlying feature itself. <bR>
 * The class is use to offer usere the opportunity to use the scale of a map within expressions
 * embeded in SLD rules/symbolizers. E.g. this enables a user to define that a symbol shall appear
 * in 10m size independ of a maps scale.
 * 
 * @version $Revision: 1.22 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.22 $, $Date: 2006/07/04 19:09:06 $
 * 
 * @since 2.0
 */
public class ScaledFeature implements Feature {

    private Feature feature = null;

    private FeatureType ft = null;

    private FeatureProperty[] props = null;

    private Map<String, String> attributeMap = new HashMap<String, String>();    
    
    /**
     * 
     * @param feature
     *            feature wrap
     * @param scale
     *            maps scale (should be -1 if not known)
     */
    public ScaledFeature( Feature feature, double scale ) {
        this.feature = feature;
        PropertyType[] ftp = feature.getFeatureType().getProperties();
        PropertyType[] ftp2 = new PropertyType[ftp.length + 1];
        for (int i = 0; i < ftp.length; i++) {
            ftp2[i] = ftp[i];
        }
        QualifiedName qn = new QualifiedName( feature.getName().getPrefix() , "$SCALE", 
                                              feature.getName().getNamespace() ); 
        ftp2[ftp2.length - 1] = FeatureFactory.createSimplePropertyType( qn, Types.DOUBLE, false );
        FeatureProperty[] o = feature.getProperties();
        props = new FeatureProperty[o.length + 1];
        for (int i = 0; i < o.length; i++) {
            props[i] = o[i];
        }
        props[props.length - 1] = FeatureFactory.createFeatureProperty( qn, new Double( scale ) );
        ft = FeatureFactory.createFeatureType( feature.getFeatureType().getName(), false, ftp2 );
    }

    public FeatureProperty getOwner() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public QualifiedName getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see Feature#getDefaultGeometryPropertyValue()
     */
    public Geometry getDefaultGeometryPropertyValue() {
        return feature.getDefaultGeometryPropertyValue();
    }

    /**
     * @see Feature#getEnvelope()
     */
    public Envelope getBoundedBy() throws GeometryException {
        return feature.getBoundedBy();
    }

    /**
     * @see Feature#getFeatureType() the returned feature type contains all properties of the
     *      wrapped feature plus a property named '$SCALE'
     */
    public FeatureType getFeatureType() {
        return ft;
    }

    /**
     * @see Feature#getGeometryPropertyValues()
     */
    public Geometry[] getGeometryPropertyValues() {
        return feature.getGeometryPropertyValues();
    }

    /**
     * @see Feature#getId()
     */
    public String getId() {
        return feature.getId();
    }

    /**
     * @see Feature#getProperties() the returned array contains all properties of the wrapped
     *      feature plus a property named '$SCALE'
     */
    public FeatureProperty[] getProperties() {
        return props;
    }

    /**
     * @see Feature#getProperties(int) The property '$SCALE' has the highest valid index
     */
    public FeatureProperty[] getProperties( int index ) {
        return new FeatureProperty[] { props[index] };
    }

    /**
     * @see Feature#getDefaultProperty(String) use '$SCALE' to access the scale property value
     */
    public FeatureProperty getDefaultProperty( QualifiedName name ) {
        QualifiedName qn = new QualifiedName( "$SCALE" );
        if ( name.equals( qn ) ) {
            return props[props.length - 1];
        } 
        return feature.getDefaultProperty( name );        
    }

    public FeatureProperty[] getProperties( QualifiedName name ) {
        if ( name.getLocalName().equalsIgnoreCase( "$SCALE" ) ) {
            return new FeatureProperty[] { props[ props.length -1 ] };
        }        
        return feature.getProperties( name );
    }

    public FeatureProperty getDefaultProperty( PropertyPath path )
        throws PropertyPathResolvingException {
        if ( path.getStep(0).getPropertyName().getLocalName().equalsIgnoreCase( "$SCALE" ) ) {
            return props[ props.length -1 ];
        }
        return feature.getDefaultProperty( path );
    }

    /**
     * @see Feature#setProperty(FeatureProperty)
     */
    public void setProperty( FeatureProperty property, int index ) {
        feature.setProperty( property, index );
    }

    /**
     * sets the features scale
     * 
     * @param scale
     */
    public void setScale( double scale ) {
        props[props.length - 1].setValue(new Double( scale ));
    }

    /**
     * returns the features scale
     * 
     * @return
     */
    public double getScale() {
        return ((Double)props[props.length - 1].getValue()).doubleValue();
    }

    public void addProperty( FeatureProperty property ) {
        this.feature.addProperty( property );
    }

    public void removeProperty( QualifiedName propertyName ) {
        this.feature.removeProperty( propertyName );
    }

    public void replaceProperty( FeatureProperty oldProperty, FeatureProperty newProperty ) {
        this.feature.replaceProperty( oldProperty, newProperty );        
    }        
    
    public void setId( String fid ) {                
    }

    /**
     * Returns the attribute value of the attribute with the specified name.
     * 
     * @param name name of the attribute
     * @return the attribute value
     */
    public String getAttribute (String name) {
        return this.attributeMap.get( name );
    }

    /**
     * Returns all attributes of the feature.
     * 
     * @return all attributes, keys are names, values are attribute values 
     */    
    public Map<String,String> getAttributes () {
        return this.attributeMap;
    }

    /**
     * Sets the value of the attribute with the given name.
     * 
     * @param name name of the attribute
     * @param value value to set
     */
    public void setAttribute (String name, String value) {
        this.attributeMap.put( name, value );
    }

    /**
     * Sets the feature type of this feature.
     * 
     * @param ft feature type to set
     */
    public void setFeatureType( FeatureType ft ) {
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: ScaledFeature.java,v $
 * Changes to this class. What the people have been up to: Revision 1.22  2006/07/04 19:09:06  poth
 * Changes to this class. What the people have been up to: comments corrected - code formatation
 * Changes to this class. What the people have been up to:
 *  Revision 1.21  2006/05/15 06:55:37  poth
 *  *** empty log message ***
 * 
 *  Revision 1.20  2006/04/15 15:30:20  poth
 *  *** empty log message ***
 * 
 *  Revision 1.19  2006/04/06 20:25:26  poth
 *  *** empty log message ***
 * 
 *  Revision 1.18  2006/04/04 17:49:50  mschneider
 *  Added replaceProperty().
 * 
 *  Revision 1.17  2006/03/30 21:20:26  poth
 *  *** empty log message ***
 * 
 *  Revision 1.16  2006/02/24 13:28:13  mschneider
 *  Added method setFeatureType().
 * 
 *  Revision 1.15  2006/02/09 14:54:19  mschneider
 *  Added "attributes" to Features.
 * 
 *  Revision 1.14  2006/02/03 18:12:07  mschneider
 *  Added method setId().
 * 
 *  Revision 1.13  2006/01/31 16:21:25  mschneider
 *  Changes due to refactoring of org.deegree.model.feature package.
 * 
 *  Revision 1.12  2005/12/08 20:42:32  mschneider
 *  Added 'isAbstract' field to FeatureType.
 * 
 *  Revision 1.11  2005/11/23 17:48:09  poth
 *  no message
 * 
 *  Revision 1.10  2005/11/23 17:09:51  mschneider
 *  Added getDefaultProperty( PropertyPath).
 *  Changes to
 * this class. What the people have been up to: Revision 1.9 2005/11/21 18:42:10 mschneider Changes
 * to this class. What the people have been up to: Refactoring due to changes in Feature class.
 *  Changes to this class. What the people
 * have been up to: Revision 1.8 2005/11/16 13:45:01 mschneider Changes to this class. What the
 * people have been up to: Merge of wfs development branch. Changes to this class. What the people
 * have been up to:  Revision 1.7.2.4
 * 2005/11/15 13:36:55 deshmukh  Modified
 * Object to FeatureProperty  Revision
 * 1.7.2.3 2005/11/14 00:50:52 mschneider MappedPropertyType -> PropertyType.
 * 
 * Revision 1.7.2.2 2005/11/09 08:00:49 mschneider More refactoring of 'org.deegree.io.datastore'.
 * 
 * Revision 1.7.2.1 2005/11/07 11:19:09 deshmukh Refactoring of 'createPropertyType()' methods in
 * FeatureFactory.
 * 
 * Revision 1.7 2005/08/30 13:40:03 poth no message
 * 
 * Revision 1.6 2005/08/24 16:06:56 mschneider Renamed GenericName to QualifiedName.
 * 
 * Revision 1.5 2005/08/22 13:50:07 poth no message
 * 
 * Revision 1.4 2005/07/08 12:21:06 poth no message
 * 
 * Revision 1.3 2005/06/16 08:27:31 poth no message
 * 
 * Revision 1.2 2005/06/15 16:16:53 poth no message
 * 
 * Revision 1.1 2005/02/21 08:57:59 poth no message
 * 
 * 
 ************************************************************************************************* */
