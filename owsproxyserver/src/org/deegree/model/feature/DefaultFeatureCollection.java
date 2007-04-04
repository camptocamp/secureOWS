//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/DefaultFeatureCollection.java,v 1.20 2006/11/02 10:18:44 mschneider Exp $
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.PropertyPath;

/**
 * This interface provides services for the management of groups of features. These groups can come
 * into being for a number of reasons: e.g. a project as a whole, for the scope of a query, as the
 * result of a query or arbitrarily selected by a user for some common manipulation. A feature's
 * membership of a particular FeatureCollection does not necessarily imply any relationship with
 * other member features. Composite or compound features which own constituent member Features (e.g.
 * an Airport composed of Terminals, Runways, Aprons, Hangars, etc) may also support the
 * FeatureCollection interface to provide a generic means for clients to access constituent members
 * without needing to be aware of the internal implementation details of the compound feature.
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.20 $ $Date: 2006/11/02 10:18:44 $
 */
class DefaultFeatureCollection extends AbstractFeatureCollection implements Serializable {

    private List<Feature> collection = null;

    private Envelope envelope = null;

    DefaultFeatureCollection( String id, int initialCapacity ) {
        super( id );
        collection = new ArrayList<Feature>( initialCapacity );
    }

    /**
     * constructor for initializing a featur collection with an id and an array of features.
     */
    DefaultFeatureCollection( String id, Feature[] feature ) {
        this( id, feature.length );
        for (int i = 0; i < feature.length; i++) {
            add( feature[i] );
        }
    }

    /**
     * returns the FeatureType of this Feature(Collection)
     */
    public FeatureType getFeatureType() {
        return this.featureType;
    }

    /**
     * returns an array of all features
     */
    public Feature[] toArray() {
        return collection.toArray( new Feature[collection.size()] );
    }

    /**
     * returns an <tt>Iterator</tt> on the feature contained in a collection
     * 
     * @return
     */
    public Iterator iterator() {
        return collection.iterator();
    }

    /**
     * returns the feature that is assigned to the submitted index. If the submitted value for
     * <tt>index</tt> is smaller 0 and larger then the number features within the
     * featurecollection-1 an exeption will be thrown.
     */
    public Feature getFeature( int index ) {
        return collection.get( index );
    }

    /**
     * returns the feature that is assigned to the submitted id. If no valid feature could be found
     * an <tt>Object[]</tt> with zero length will be returned.
     */
    public Feature getFeature( String id ) {
        Feature feature = null;
        for (int i = 0; i < collection.size(); i++) {
            feature = collection.get( i );
            if ( feature.getId().equals( id ) ) {
                break;
            }
        }
        return feature;
    }

    /**
     * removes the submitted feature from the collection
     */
    public Feature remove( Feature feature ) {
        int index = collection.indexOf( feature );
        return remove( index );
    }
    
    /**
     * removes a feature identified by its index from the feature collection. The removed feature
     * will be returned. If the submitted value for <tt>index</tt> is smaller 0 and larger then
     * the number features within the featurecollection-1 an ArrayIndexOutOfBoundsExcpetion will
     * raise
     */
    public Feature remove( int index ) {        
        return collection.remove( index );
    }


    /**
     * Appends a feature to the collection. If the submitted feature doesn't matches the feature
     * type defined for all features within the collection an exception will be thrown.
     */
    public void add( Feature feature ) {
        collection.add( feature );
    }

    /**
     * returns the number of features within the collection
     */
    public int size() {
        return collection.size();
    }

    public void setProperty( FeatureProperty property, int index ) {
        // TODO Auto-generated method stub
    }

    public void addProperty( FeatureProperty property ) {
        // TODO Auto-generated method stub
    }

    public void removeProperty( QualifiedName propertyName ) {
        // TODO Auto-generated method stub
    }

    public void replaceProperty( FeatureProperty oldProperty, FeatureProperty newProperty ) {
        // TODO Auto-generated method stub        
    }    
    
    /**
     * returns the envelope / boundingbox of the feature collection
     */
    @Override
    public synchronized Envelope getBoundedBy() throws GeometryException {
        
        Envelope combinedEnvelope = this.envelope;
        
        if ( combinedEnvelope == null && this.collection.size() > 0 ) {
            combinedEnvelope = this.collection.get( 0 ).getBoundedBy();
            for (int i = 1; i < this.collection.size(); i++) {
                Envelope nextFeatureEnvelope = this.collection.get( i ).getBoundedBy();
                if (combinedEnvelope == null) {
                    combinedEnvelope = nextFeatureEnvelope;
                } else if (nextFeatureEnvelope != null) {
                    combinedEnvelope = combinedEnvelope.merge (nextFeatureEnvelope);
                }
            }
            this.envelope = combinedEnvelope;
        }
        return combinedEnvelope;
    }

    @Override
    public String toString() {
        String ret = null;
        ret = "collection = "
            + collection + "\n";
        return ret;
    }

    public FeatureProperty getDefaultProperty( PropertyPath path ) throws PropertyPathResolvingException {
        // TODO Auto-generated method stub
        return null;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultFeatureCollection.java,v $
Revision 1.20  2006/11/02 10:18:44  mschneider
Fixed #getBoundedBy().

Revision 1.19  2006/10/11 11:22:54  poth
use generics

Revision 1.18  2006/07/07 15:10:36  poth
footer added

********************************************************************** */