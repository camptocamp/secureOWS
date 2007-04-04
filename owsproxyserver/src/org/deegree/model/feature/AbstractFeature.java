//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/AbstractFeature.java,v 1.23 2006/11/02 10:18:23 mschneider Exp $
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
package org.deegree.model.feature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version 1.0. $Revision: 1.23 $, $Date: 2006/11/02 10:18:23 $
 */
abstract class AbstractFeature implements Feature {

    private String id;

    protected FeatureType featureType;

    protected Envelope envelope;
    
    protected boolean envelopeCalculated;

    protected String description;

    protected FeatureProperty owner;

    private Map<String, String> attributeMap = new HashMap<String, String>();

    /**
     * @param id
     * @param featureType
     */
    AbstractFeature( String id, FeatureType featureType ) {
        this.id = id;
        this.featureType = featureType;
    }

    /**
     * @param id
     * @param featureType
     */
    AbstractFeature( String id, FeatureType featureType, FeatureProperty owner ) {
        this.id = id;
        this.featureType = featureType;
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public QualifiedName getName() {
        return featureType.getName();
    }

    /**
     * Returns the envelope / boundingbox of the feature. The bounding box
     * will be created in a recursion. That means if a property of the feature (a)
     * contains another feature (b) the bounding box of b will be merged with the
     * bounding box calculated from the geometry properties of a. A feature has no
     * geometry properties and no properties that are features (and having a 
     * bounding box theirself) <code>null</code> will be returned.
     */
    public Envelope getBoundedBy()
                            throws GeometryException {
        
        if ( !this.envelopeCalculated) {
            getBoundedBy(new HashSet<Feature>());
        }
        return this.envelope;
    }

    public FeatureProperty getOwner() {
        return this.owner;
    }

    /**
     * returns the id of the Feature. the id has to be a name space that must be unique for each
     * feature. use the adress of the datasource in addition to a number for example .
     */
    public String getId() {
        return id;
    }

    public void setId( String fid ) {
        this.id = fid;
    }

    /**
     * returns the FeatureType of this Feature
     * 
     */
    public FeatureType getFeatureType() {
        return featureType;
    }

    /**
     * Sets the feature type of this feature.
     * 
     * @param ft feature type to set
     */
    public void setFeatureType( FeatureType ft ) {
        this.featureType = ft;
    }

    /**
     * resets the envelope of the feature so the next call of getBounds() will force a
     * re-calculation
     * 
     */
    protected void resetBounds() {
        envelope = null;
    }

    /**
     * Returns the attribute value of the attribute with the specified name.
     * 
     * @param name name of the attribute
     * @return the attribute value
     */
    public String getAttribute( String name ) {
        return this.attributeMap.get( name );
    }

    /**
     * Returns all attributes of the feature.
     * 
     * @return all attributes, keys are names, values are attribute values 
     */
    public Map<String, String> getAttributes() {
        return this.attributeMap;
    }

    /**
     * Sets the value of the attribute with the given name.
     * 
     * @param name name of the attribute
     * @param value value to set
     */
    public void setAttribute( String name, String value ) {
        this.attributeMap.put( name, value );
    }

    /**
     * Returns the {@link Envelope} of the feature. If the envelope has not been calculated yet,
     * it is calculated recursively, using the given feature set to detected cycles in the feature
     * structure.
     * 
     * @param features
     *           used to detected cycles in feature structure and prevent endless recursion
     * @return envelope of the feature
     * @throws GeometryException
     */
    private Envelope getBoundedBy (Set<Feature> features) throws GeometryException {
        if ( !this.envelopeCalculated) {
            this.envelope = calcEnvelope(features);
            this.envelopeCalculated = true;
        }
        return this.envelope;        
    }
    
    /**
     * Calculates the envelope of the feature recursively. Respects all geometry properties
     * and sub features of the feature.
     * 
     * @param features
     *           used to detected cycles in feature structure and prevent endless recursion
     * @return envelope of the feature
     * @throws GeometryException
     */
    private Envelope calcEnvelope(Set<Feature> features)
                            throws GeometryException {

        Envelope combinedEnvelope = null;
        
        if (features.contains(this)) {
            return combinedEnvelope;
        }
        features.add(this);

        FeatureProperty[] props = getProperties();
        for ( FeatureProperty prop : props ) {
            if ( prop != null ) {
                Object propValue = prop.getValue();
                if ( propValue instanceof Geometry ) {
                    Geometry geom = (Geometry) propValue;
                    Envelope env = null;                   
                    if ( geom instanceof Point ) {
                        env = GeometryFactory.createEnvelope( ( (Point) geom ).getPosition(),
                                                              ( (Point) geom ).getPosition(),
                                                              geom.getCoordinateSystem() );
                    } else {
                        env = geom.getEnvelope();
                    }
                    if (combinedEnvelope == null) {
                        combinedEnvelope = env;
                    } else {
                        combinedEnvelope = combinedEnvelope.merge( env );
                    }
                } else if ( propValue instanceof AbstractFeature ) {
                    Envelope subEnvelope = ( (AbstractFeature) propValue ).getBoundedBy(features);
                    if ( combinedEnvelope == null ) {
                        combinedEnvelope = subEnvelope;
                    } else if ( subEnvelope != null ) {
                        combinedEnvelope = combinedEnvelope.merge( subEnvelope );
                    }
                }
            }
        }
        return combinedEnvelope;
    }    
}

/***************************************************************************************************
 * $Log: AbstractFeature.java,v $
 * Revision 1.23  2006/11/02 10:18:23  mschneider
 * Fixed #getBoundedBy(). Changed visibility of constructors to package protected.
 *
 * Revision 1.22  2006/10/31 16:53:19  mschneider
 * Fixed null CRS in #getBoundedBy(Set<Feature>).
 *
 * Revision 1.21  2006/08/09 17:02:56  mschneider
 * Fixed endless recursion in getBoundedBy().
 *
 * Revision 1.20  2006/05/09 15:51:37  poth
 * *** empty log message ***
 *
 * Revision 1.19  2006/04/26 15:09:37  poth
 * *** empty log message ***
 * 
 * Revision 1.18  2006/04/26 13:34:09  poth
 * *** empty log message ***
 *
 * Revision 1.17  2006/04/06 20:25:27  poth
 * *** empty log message ***
 *
 * Revision 1.16  2006/03/30 21:20:26  poth
 * *** empty log message ***
 *
 * Revision 1.15  2006/02/24 13:28:13  mschneider
 * Added method setFeatureType().
 *
 * Revision 1.14  2006/02/09 14:54:19  mschneider
 * Added "attributes" to Features.
 *
 * Revision 1.13  2006/02/03 18:12:07  mschneider
 * Added method setId().
 *
 * Revision 1.12  2006/01/31 16:24:43  mschneider
 * Changes due to refactoring of org.deegree.model.feature package.
 *
 * Revision 1.11  2006/01/30 16:20:10  mschneider
 * Feature has a name on it's own now (and not from it's feature type). This is useful, because some features have no feature type.
 *
 * Revision 1.10  2005/12/11 18:31:05  poth
 * no message
 *
 * Revision 1.9  2005/12/06 13:45:20  poth
 * System.out.println substituted by logging api
 *
 * Revision 1.8  2005/12/05 09:25:17  deshmukh
 * *** empty log message ***
 *
 * Revision 1.7 2005/11/21 18:41:26 mschneider
 * Added methods for precise modification of the feature's properties.
 *
 * Revision 1.6 2005/11/16 13:44:59 mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.5.2.1 2005/11/14 19:51:41 mschneider
 * Fixed compilation problems.
 * 
 * Revision 1.5 2005/08/30 13:40:03 poth no message
 * 
 * Revision 1.4 2005/08/24 16:09:26 mschneider Renamed GenericName to QualifiedName.
 * 
 * Revision 1.3 2005/08/22 13:50:07 poth no message
 * 
 * Revision 1.2 2005/06/16 08:27:31 poth no message
 * 
 * Revision 1.1 2005/01/28 11:16:59 poth no message
 * 
 **************************************************************************************************/