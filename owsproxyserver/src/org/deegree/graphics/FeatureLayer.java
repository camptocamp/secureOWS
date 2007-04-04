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
package org.deegree.graphics;

import java.util.ArrayList;

import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * A Layer is a collection of <tt>Feature</tt>s building a thematic 'unit' waterways or country
 * borders for example. <tt>Feature</tt>s can be added or removed from the layer. A
 * <tt>Feature</tt> can e changed by a modul of the application using the layer because only
 * references to <tt>Feature</tt>s are stored within a layer.
 * 
 * <p>
 * ------------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.21 $ $Date: 2006/11/27 09:07:52 $
 */

public class FeatureLayer extends AbstractLayer {

    private FeatureCollection fc = null;

    /**
     * creates a layer with EPSG:4326 as default coordinate system
     */
    FeatureLayer( String name ) throws Exception {
        super( name );

        fc = FeatureFactory.createFeatureCollection( name, 50 );

        init( fc );
    }

    /**
     * Creates a new FeatureLayer object.
     * 
     * @param name
     * @param crs
     * 
     * @throws Exception
     */
    FeatureLayer( String name, CoordinateSystem crs ) throws Exception {
        super( name, crs );

        fc = FeatureFactory.createFeatureCollection( name, 50 );

        init( fc );
    }

    /**
     * Creates a new AbstractLayer object.
     * 
     * @param name
     * @param crs
     * @param fc
     * 
     * @throws Exception
     */
    FeatureLayer( String name, CoordinateSystem crs, FeatureCollection fc ) throws Exception {
        super( name, crs );
        init( fc );
    }

    /**
     * initializes serveral parameters of the layer and homogenizes the coordinate reference systems
     * of the features
     */
    private void init( FeatureCollection feature ) throws Exception {

        this.fc = FeatureFactory.createFeatureCollection( feature.getId(), feature.size() );
        // create object for coordinate transformation
        IGeoTransformer gt = new GeoTransformer( cs );
        double minx = 9E99;
        double maxx = -9E99;
        double miny = 9E99;
        double maxy = -9E99;
        String s1 = cs.getName();
        for (int i = 0; i < feature.size(); i++) {                
            Feature feat = feature.getFeature( i );
            FeatureProperty[] prop = feat.getProperties();
            FeatureProperty[] propN = new FeatureProperty[prop.length];
            boolean changed = false;
            for (int k = 0; k < prop.length; k++) {

                Object value = prop[k].getValue();    
                propN[k] = prop[k]; 
                if ( value instanceof Geometry ) {
                    
                    CoordinateSystem _cs_ = ( (Geometry) value ).getCoordinateSystem();
                    String s2 = null;
                    if ( _cs_ != null ) {
                        s2 = _cs_.getName();
                    } else {
                        // default reference system
                        s2 = "EPSG:4326";
                    }

                    if ( !s1.equalsIgnoreCase( s2 ) ) {
                        Geometry transformedGeometry = gt.transform( (Geometry) value );                        
                        propN[k] = FeatureFactory.createFeatureProperty( prop[k].getName(),
                                                                         transformedGeometry );
                        changed = true;                      
                        value = transformedGeometry;
                    }
                    if ( value instanceof Point ) {
                        Position pos = ( (Point) value ).getPosition();
                        if ( pos.getX() > maxx ) {
                            maxx = pos.getX();
                        }
                        if ( pos.getX() < minx ) {
                            minx = pos.getX();
                        }
                        if ( pos.getY() > maxy ) {
                            maxy = pos.getY();
                        }
                        if ( pos.getY() < miny ) {
                            miny = pos.getY();
                        }
                    } else {
                        Envelope en = ( (Geometry) value ).getEnvelope();
                        if ( en.getMax().getX() > maxx ) {
                            maxx = en.getMax().getX();
                        }
                        if ( en.getMin().getX() < minx ) {
                            minx = en.getMin().getX();
                        }
                        if ( en.getMax().getY() > maxy ) {
                            maxy = en.getMax().getY();
                        }
                        if ( en.getMin().getY() < miny ) {
                            miny = en.getMin().getY();
                        }
                    }
                }
            }
            if ( changed ) {
                FeatureProperty[] fp = new FeatureProperty[propN.length];
                for (int j = 0; j < fp.length; j++) {                    
                    fp[j] = FeatureFactory.createFeatureProperty( propN[j].getName(), 
                                                                  propN[j].getValue() );
                }
                feat = FeatureFactory.createFeature( feat.getId(), feat.getFeatureType(), fp );
            }
            fc.add( feat );
        }

        boundingbox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, null );

    }

    private void recalculateBoundingbox() {

        double minx = 9E99;
        double maxx = -9E99;
        double miny = 9E99;
        double maxy = -9E99;

        for (int i = 0; i < fc.size(); i++) {
            Geometry[] prop = fc.getFeature( i ).getGeometryPropertyValues();
            for (int k = 0; k < prop.length; k++) {
                if ( prop[k] instanceof Point ) {
                    Position pos = ( (Point) prop[k] ).getPosition();
                    if ( pos.getX() > maxx ) {
                        maxx = pos.getX();
                    }
                    if ( pos.getX() < minx ) {
                        minx = pos.getX();
                    }
                    if ( pos.getY() > maxy ) {
                        maxy = pos.getY();
                    }
                    if ( pos.getY() < miny ) {
                        miny = pos.getY();
                    }
                } else {
                    Envelope en = ( prop[k] ).getEnvelope();
                    if ( en.getMax().getX() > maxx ) {
                        maxx = en.getMax().getX();
                    }
                    if ( en.getMin().getX() < minx ) {
                        minx = en.getMin().getX();
                    }
                    if ( en.getMax().getY() > maxy ) {
                        maxy = en.getMax().getY();
                    }
                    if ( en.getMin().getY() < miny ) {
                        miny = en.getMin().getY();
                    }
                }
            }
        }

        boundingbox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, null );

    }

    /**
     * returns the feature that matches the submitted id
     */
    public Feature getFeatureById( String id ) {
        return fc.getFeature( id );
    }

    /**
     * returns the feature that matches the submitted id
     */
    public Feature[] getFeaturesById( String[] ids ) {

        ArrayList list = new ArrayList();

        Feature feature = null;

        for (int i = 0; i < fc.size(); i++) {
            feature = fc.getFeature( i );

            for (int k = 0; k < ids.length; k++) {
                if ( feature.getId().equals( ids[k] ) ) {
                    list.add( feature );
                    break;
                }
            }
        }

        return (Feature[]) list.toArray( new Feature[list.size()] );
    }

    /**
     * returns the feature that matches the submitted index
     */
    public Feature getFeature( int index ) {
        Feature feature = fc.getFeature( index );
        return feature;
    }

    /**
     * returns all features
     */
    public Feature[] getAllFeatures() {
        return fc.toArray();
    }

    /**
     * adds a feature to the layer
     */
    public void addFeature( Feature feature ) throws Exception {
        fc.add( feature );
        recalculateBoundingbox();
    }

    /**
     * adds a feature collection to the layer
     */
    public void addFeatureCollection( FeatureCollection featureCollection ) throws Exception {
        fc.add( featureCollection );
        recalculateBoundingbox();
    }

    /**
     * removes a display Element from the layer
     */
    public void removeFeature( Feature feature ) throws Exception {
        fc.remove( feature );
        recalculateBoundingbox();
    }

    /**
     * removes the display Element from the layer that matches the submitted id
     */
    public void removeFeature( int id ) throws Exception {
        removeFeature( getFeature( id ) );
    }

    /**
     * returns the amount of features within the layer.
     */
    public int getSize() {
        return fc.size();
    }

    /**
     * sets the coordinate reference system of the MapView. If a new crs is set all geometries of
     * GeometryFeatures will be transformed to the new coordinate reference system.
     */
    public void setCoordinatesSystem( CoordinateSystem crs ) throws Exception {
        if ( !cs.equals( crs ) ) {
            this.cs = crs;
            init( fc );
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeatureLayer.java,v $
Revision 1.21  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.20  2006/11/21 13:13:11  poth
bug fix - calculating bbox for point layers

Revision 1.19  2006/08/29 14:38:15  poth
bug fix - recreating feature if geometry/CRS has been transformed; check for CRS being equal

Revision 1.18  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.17  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
