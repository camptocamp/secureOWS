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
* default implementierung of the MultiPoint interface of
* package jago.model. 
*
* <p>------------------------------------------------------------</p>
* @version 12.6.2001
* @author Andreas Poth href="mailto:poth@lat-lon.de"
* <p>
*/
final class MultiPointImpl extends MultiPrimitiveImpl implements MultiPoint, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -1105623021535230655L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( MultiPointImpl.class );

    /**
     * Creates a new MultiPointImpl object.
     *
     * @param crs 
     */
    public MultiPointImpl( CoordinateSystem crs ) {
        super( crs );
    }

    /**
     * Creates a new MultiPointImpl object.
     *
     * @param gmp 
     */
    public MultiPointImpl( Point[] gmp ) {
        super( gmp[0].getCoordinateSystem() );

        for ( int i = 0; i < gmp.length; i++ ) {
            aggregate.add( gmp[i] );
        }

    }

    /**
     * Creates a new MultiPointImpl object.
     *
     * @param gmp 
     * @param crs 
     */
    public MultiPointImpl( Point[] gmp, CoordinateSystem crs ) {
        super( crs );

        for ( int i = 0; i < gmp.length; i++ ) {
            aggregate.add( gmp[i] );
        }

    }

    /**
     * adds a Point to the aggregation 
     */
    public void addPoint( Point gmp ) {
        super.add( gmp );
    }

    /**
     * inserts a Point into the aggregation. all elements with an index 
     * equal or larger index will be moved. if index is
     * larger then getSize() - 1 or smaller then 0 or gmp equals null 
     * an exception will be thrown.
     *
     * @param gmp Point to insert.     
     * @param index position where to insert the new Point
     */
    public void insertPointAt( Point gmp, int index ) throws GeometryException {
        super.insertObjectAt( gmp, index );
    }

    /**
     * sets the submitted Point at the submitted index. the element
     * at the position <code>index</code> will be removed. if index is
     * larger then getSize() - 1 or smaller then 0 or gmp equals null 
     * an exception will be thrown.
     *
     * @param gmp Point to set.     
     * @param index position where to set the new Point
     */
    public void setPointAt( Point gmp, int index ) throws GeometryException {
        setObjectAt( gmp, index );
    }

    /**
     * removes the submitted Point from the aggregation
     *
     * @return the removed Point
     */
    public Point removePoint( Point gmp ) {
        return (Point)super.removeObject( gmp );
    }

    /**
     * removes the Point at the submitted index from the aggregation.
     * if index is larger then getSize() - 1 or smaller then 0 
     * an exception will be thrown.
     *
     * @return the removed Point
     */
    public Point removePointAt( int index ) throws GeometryException {
        return (Point)super.removeObjectAt( index );
    }

    /**
     * returns the Point at the submitted index. 
     */
    public Point getPointAt( int index ) {
        return (Point)super.getPrimitiveAt( index );
    }

    /**
     * returns all Points as array
     */
    public Point[] getAllPoints() {
        return (Point[])aggregate.toArray( new Point[getSize()] );
    }

    /**
     * updates the bounding box of the aggregation
     */
    private void calculateEnvelope() {
        Point gmp = getPointAt( 0 );

        double[] min = gmp.getAsArray().clone();
        double[] max = min.clone();

        for ( int i = 1; i < getSize(); i++ ) {
            double[] pos = getPointAt( i ).getAsArray();

            for ( int j = 0; j < pos.length; j++ ) {
                if ( pos[j] < min[j] ) {
                    min[j] = pos[j];
                } else if ( pos[j] > max[j] ) {
                    max[j] = pos[j];
                }
            }
        }

        envelope = new EnvelopeImpl( new PositionImpl( min ), new PositionImpl( max ), this.crs );
    }

    /**
     * calculates the centroid of the surface
     */
    private void calculateCentroid() {
        try {
            Point gmp = getPointAt( 0 );

            double[] cen = new double[gmp.getAsArray().length];

            for ( int i = 0; i < getSize(); i++ ) {
                double[] pos = getPointAt( i ).getAsArray();

                for ( int j = 0; j < pos.length; j++ ) {
                    cen[j] += ( pos[j] / getSize() );
                }
            }

            centroid = new PointImpl( new PositionImpl( cen ), null );
        } catch ( Exception ex ) {
            LOG.logError( "", ex );
        }
    }

    /**
     * calculates the centroid and envelope of the aggregation
     */
    protected void calculateParam() {
        calculateCentroid();
        calculateEnvelope();
        setValid( true );
    }

    /**
     * The operation "dimension" shall return the inherent dimension of this
     * Geometry, which shall be less than or equal to the coordinate dimension.
     * The dimension of a collection of geometric objects shall be the largest
     * dimension of any of its pieces. Points are 0-dimensional, curves are
     * 1-dimensional, surfaces are 2-dimensional, and solids are 3-dimensional.
     */
    public int getDimension() {
        return 0;
    }

    /**
     * The operation "coordinateDimension" shall return the dimension of the
     * coordinates that define this Geometry, which must be the same as the
     * coordinate dimension of the coordinate reference system for this Geometry.
     */
    public int getCoordinateDimension() {
        return getPointAt( 0 ).getCoordinateDimension();
    }

    /**
    * returns a shallow copy of the geometry
    */
    public Object clone() {
        MultiPoint mp = null;

        try {
            mp = new MultiPointImpl( getCoordinateSystem() );

            for ( int i = 0; i < this.getSize(); i++ ) {
                PointImpl pi = (PointImpl)getPointAt( i );
                mp.add( (Point)pi.clone() );
            }
        } catch ( Exception ex ) {
            LOG.logError( "MultiPoint_Impl.clone: ", ex );
        }

        return mp;
    }
} /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MultiPointImpl.java,v $
Revision 1.9  2006/11/02 10:20:51  mschneider
Fixed null CRS in Envelope creation.

Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
