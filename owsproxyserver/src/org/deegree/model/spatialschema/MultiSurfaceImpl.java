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
package org.deegree.model.spatialschema;

import java.io.Serializable;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;


/**
 * default implementation of the MultiSurface interface from
 * package jago.model.
 *
 * <p>------------------------------------------------------------</p>
 * @version 12.6.2001
 * @author Andreas Poth
 * <p>
 */
final class MultiSurfaceImpl extends MultiPrimitiveImpl implements MultiSurface,
                                                                           Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -6471121873087659850L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( MultiSurfaceImpl.class );
    
    private double area = 0;

    /**
     * Creates a new MultiSurfaceImpl object.
     *
     * @param crs 
     */
    public MultiSurfaceImpl( CoordinateSystem crs ) {
        super( crs );
    }

    /**
     * Creates a new MultiSurfaceImpl object.
     *
     * @param surface 
     */
    public MultiSurfaceImpl( Surface[] surface ) {
        super( surface[0].getCoordinateSystem() );

        for ( int i = 0; i < surface.length; i++ ) {
            aggregate.add( surface[i] );
        }

        setValid( false );
    }

    /**
     * Creates a new MultiSurfaceImpl object.
     *
     * @param surface 
     * @param crs 
     */
    public MultiSurfaceImpl( Surface[] surface, CoordinateSystem crs ) {
        super( crs );

        for ( int i = 0; i < surface.length; i++ ) {
            aggregate.add( surface[i] );
        }

        setValid( false );
    }

    /**
     * adds an Surface to the aggregation
     */
    public void addSurface( Surface gms ) {
        super.add( gms );
    }

    /**
     * inserts a Surface in the aggregation. all elements with an index
     * equal or larger index will be moved. if index is
     * larger then getSize() - 1 or smaller then 0 or gms equals null
     * an exception will be thrown.
     *
     * @param gms Surface to insert.
     * @param index position where to insert the new Surface
     */
    public void insertSurfaceAt( Surface gms, int index ) throws GeometryException {
        super.insertObjectAt( gms, index );
    }

    /**
     * sets the submitted Surface at the submitted index. the element
     * at the position <code>index</code> will be removed. if index is
     * larger then getSize() - 1 or smaller then 0 or gms equals null
     * an exception will be thrown.
     *
     * @param gms Surface to set.
     * @param index position where to set the new Surface
     */
    public void setSurfaceAt( Surface gms, int index ) throws GeometryException {
        setObjectAt( gms, index );
    }

    /**
     * removes the submitted Surface from the aggregation
     *
     * @return the removed Surface
     */
    public Surface removeSurface( Surface gms ) {
        return (Surface)super.removeObject( gms );
    }

    /**
     * removes the Surface at the submitted index from the aggregation.
     * if index is larger then getSize() - 1 or smaller then 0
     * an exception will be thrown.
     *
     * @return the removed Surface
     */
    public Surface removeSurfaceAt( int index ) throws GeometryException {
        return (Surface)super.removeObjectAt( index );
    }

    /**
     * returns the Surface at the submitted index.
     */
    public Surface getSurfaceAt( int index ) {
        return (Surface)super.getPrimitiveAt( index );
    }

    /**
     * returns all Surfaces as array
     */
    public Surface[] getAllSurfaces() {        
        return (Surface[])aggregate.toArray( new Surface[getSize()] );
    }

    /**
     * calculates the bounding box / envelope of the aggregation
     */
    private void calculateEnvelope() {
        Envelope bb = getSurfaceAt( 0 ).getEnvelope();

        double[] min = bb.getMin().getAsArray().clone();
        double[] max = bb.getMax().getAsArray().clone();

        for ( int i = 1; i < getSize(); i++ ) {
            double[] pos1 = getSurfaceAt( i ).getEnvelope().getMin().getAsArray();
            double[] pos2 = getSurfaceAt( i ).getEnvelope().getMax().getAsArray();

            for ( int j = 0; j < pos1.length; j++ ) {
                if ( pos1[j] < min[j] ) {
                    min[j] = pos1[j];
                } else if ( pos1[j] > max[j] ) {
                    max[j] = pos1[j];
                }

                if ( pos2[j] < min[j] ) {
                    min[j] = pos2[j];
                } else if ( pos2[j] > max[j] ) {
                    max[j] = pos2[j];
                }
            }
        }

        envelope = new EnvelopeImpl( new PositionImpl( min ), new PositionImpl( max ), this.crs );
    }

    /**
     * calculates the centroid and area of the aggregation
     */
    private void calculateCentroidArea() {

        area = 0;
        int cnt = getCoordinateDimension();        
        try {
            double[] cen = new double [cnt];

            for ( int i = 0; i < getSize(); i++ ) {
                double a = getSurfaceAt( i ).getArea();
                area = area + a;

                double[] pos = getSurfaceAt( i ).getCentroid().getAsArray();

                for ( int j = 0; j < cnt; j++ ) {
                    cen[j] = cen[j] + ( pos[j] * a );
                }
            }

            for ( int j = 0; j < cnt; j++ ) {
                cen[j] = cen[j] / area;
            }

            centroid = new PointImpl( new PositionImpl( cen ), null );
        } catch ( Exception e ) {
            LOG.logError( "", e );
        }
    }

    /**
     * calculates the centroid, area and envelope of the aggregation
     */
    protected void calculateParam() {
        calculateEnvelope();
        calculateCentroidArea();
        setValid( true );
    }

    /**
     * returns the area of the multi surface. this is calculate as the sum
     * of all containing surface areas.
     * 
     * @uml.property name="area"
     */
    public double getArea() {
        if (!isValid()) {
            calculateParam();
        }
        return area;
    }

    /**
     * returns a shallow copy of the geometry
     */
    public Object clone() {
        MultiSurface ms = null;

        try {
            ms = new MultiSurfaceImpl( getCoordinateSystem() );

            for ( int i = 0; i < this.getSize(); i++ ) {
                SurfaceImpl si = (SurfaceImpl)getSurfaceAt( i );
                ms.add( (Surface)si.clone() );
            }
        } catch ( Exception ex ) {
            LOG.logError(  "MultiSurface_Impl.clone: ", ex );
        }

        return ms;
    }

    /**
     * The operation "dimension" shall return the inherent dimension of this
     * Geometry, which shall be less than or equal to the coordinate dimension.
     * The dimension of a collection of geometric objects shall be the largest
     * dimension of any of its pieces. Points are 0-dimensional, curves are
     * 1-dimensional, surfaces are 2-dimensional, and solids are 3-dimensional.
     */
    public int getDimension() {
        return 2;
    }

    /**
     * The operation "coordinateDimension" shall return the dimension of the
     * coordinates that define this Geometry, which must be the same as the
     * coordinate dimension of the coordinate reference system for this Geometry.
     */
    public int getCoordinateDimension() {
        return getSurfaceAt( 0 ).getCoordinateDimension();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MultiSurfaceImpl.java,v $
Revision 1.12  2006/11/02 10:20:51  mschneider
Fixed null CRS in Envelope creation.

Revision 1.11  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
