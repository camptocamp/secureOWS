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
package org.deegree.model.spatialschema;

import java.io.Serializable;
import java.util.Arrays;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;

/**
 * default implementation of the Ring interface of the
 * 
 *
 * @version $Revision: 1.13 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 1.13 $, $Date: 2006/11/02 10:20:51 $
 *
 * @since 2.0
 */
public class RingImpl extends OrientableCurveImpl implements Ring, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 9157144642050604928L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( RingImpl.class );

    private Position[] points = null;
    private SurfacePatch sp = null;

    /**
     * Constructor, with Array and CS_CoordinateSystem
     */
    public RingImpl( Position[] points, CoordinateSystem crs ) throws GeometryException {
        super( crs );

        setPositions( points );
    }

    /**
     * Constructor, with Array, CS_CoordinateSystem and Orientation
     */
    public RingImpl( Position[] points, CoordinateSystem crs, char orientation )
                 throws GeometryException {
        super( crs, orientation );
        setPositions( points );
    }

    /**
     * calculates the envelope
     */
    private void calculateEnvelope() {
        double[] min = points[0].getAsArray().clone();
        double[] max = min.clone();

        for ( int i = 1; i < points.length; i++ ) {
            double[] pos = points[i].getAsArray();

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
     * Ring must be closed, so isCycle returns TRUE.
     */
    public boolean isCycle() {
        return true;
    }

    /**
     * Ring is a PrimitiveBoundary, so isSimple returns TRUE.
     */
    public boolean isSimple() {
        return true;
    }

    /**
     * The operation "dimension" shall return the inherent dimension of this
     * Geometry, which shall be less than or equal to the coordinate dimension.
     * The dimension of a collection of geometric objects shall be the largest
     * dimension of any of its pieces. Points are 0-dimensional, curves are
     * 1-dimensional, surfaces are 2-dimensional, and solids are 3-dimensional.
     */
    public int getDimension() {
        return 1;
    }

    /**
     * The operation "coordinateDimension" shall return the dimension of the
     * coordinates that define this Geometry, which must be the same as the
     * coordinate dimension of the coordinate reference system for this Geometry.
     */
    public int getCoordinateDimension() {
        return getPositions()[0].getCoordinateDimension();
    }

    /**
     * gets the Ring as a Array of positions.
     */
    public Position[] getPositions() {
        if ( getOrientation() == '-' ) {
            Position[] temp = new Position[points.length];

            for ( int i = 0; i < points.length; i++ ) {
                temp[i] = points[( points.length - 1 ) - i];
            }

            return temp;
        } 
        return points;
    }

    /**
     * sets the Ring as a ArrayList of points
     */
    protected void setPositions( Position[] positions ) throws GeometryException {
        this.points = positions;

        // checks if the ring has more than 3 elements [!(points.length > 3)]
        if ( positions.length < 3 ) {
            throw new GeometryException( "invalid length of a Ring!" );
        }

        // checks if the startpoint = endpoint of the ring
        if ( !positions[0].equals( positions[positions.length - 1] ) ) {
            throw new GeometryException( "StartPoint of ring isn't equal to EndPoint!" );
        }

        setValid( false );
    }

    /**
     * returns the Ring as a CurveSegment
     */
    public CurveSegment getAsCurveSegment() throws GeometryException {
        return new LineStringImpl( points, getCoordinateSystem() );
    }

    /**
     * returns the CurveBoundary of the Ring. For a CurveBoundary is defines
     * as the first and the last point of a Curve the CurveBoundary of a
     * Ring contains two indentical point (because a Ring is closed)
     */
    public CurveBoundary getCurveBoundary() {
        return (CurveBoundary)boundary;
    }

    /**
     * checks if this curve segment is completly equal to the submitted geometry
     * @param other object to compare to
     */
    public boolean equals( Object other ) {
        if ( !super.equals( other ) || !( other instanceof RingImpl ) ) {
            return false;
        }

        if ( !envelope.equals( ( (Geometry)other ).getEnvelope() ) ) {
            return false;
        }

        Position[] p2 = ( (Ring)other ).getPositions();

        if ( !Arrays.equals( points, p2 ) ) {
            return false;
        }

        return true;
    }

    /**
     * returns a shallow copy of the geometry
     */
    public Object clone() {
        Ring r = null;

        try {
            Position[] p = points.clone();
            r = new RingImpl( p, getCoordinateSystem(), getOrientation() );
        } catch ( Exception ex ) {
            LOG.logError( "Ring_Impl.clone: ", ex );
        }

        return r;
    }

    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry
     * intersects another Geometry. Within a Complex, the Primitives do not
     * intersect one another. In general, topologically structured data uses shared
     * geometric objects to capture intersection information.
     */
    public boolean intersects( Geometry gmo ) {
        boolean inter = false;

        try {
            CurveSegment sp = new LineStringImpl( points, crs );

            if ( gmo instanceof Point ) {
                inter = LinearIntersects.intersects( ( (Point)gmo ).getPosition(), sp );
            } else if ( gmo instanceof Curve ) {
                Curve curve = new CurveImpl( new CurveSegment[] { sp } );
                inter = LinearIntersects.intersects( (Curve)gmo, curve );
            } else if ( gmo instanceof Surface ) {
                Curve curve = new CurveImpl( new CurveSegment[] { sp } );
                inter = LinearIntersects.intersects( curve, (Surface)gmo );
            } else if ( gmo instanceof MultiPrimitive ) {
                inter = intersectsAggregate( (MultiPrimitive)gmo );
            }
        } catch ( Exception e ) {
            LOG.logError( "", e );
        }

        return inter;
    }

    /**
     * the operations returns true if the submitted multi primitive intersects
     * with the curve segment
     */
    private boolean intersectsAggregate( Aggregate mprim ) throws Exception {
        boolean inter = false;

        int cnt = mprim.getSize();

        for ( int i = 0; i < cnt; i++ ) {
            if ( intersects( mprim.getObjectAt( i ) ) ) {
                inter = true;
                break;
            }
        }

        return inter;
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains another Geometry.<p></p>
     * At the moment the operation just works with point geometries
     */
    public boolean contains( Geometry gmo ) {
    	
        try {
        	if ( sp == null ) {	           		
	            sp = new PolygonImpl( new SurfaceInterpolationImpl(), points, 
	                                                      null, crs );
        	}
            return sp.contains( gmo );
        } catch ( Exception e ) {
        }

        return false;
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains a single point given by a coordinate.<p></p>
     * dummy implementation
     */
    public boolean contains( Position position ) {
        return contains( new PointImpl( position, null ) );
    }

    /**
     * calculates the centroid of the ring
     */
    protected void calculateCentroid() {
        double[] cen = new double[getCoordinateDimension()];

        for ( int i = 0; i < points.length; i++ ) {
            for ( int j = 0; j < getCoordinateDimension(); j++ ) {
                cen[j] += ( points[i].getAsArray()[j] / points.length );
            }
        }

        centroid = new PointImpl( new PositionImpl( cen ), crs );
    }

    /**
     * calculates the centroid and the envelope of the ring
     */
    protected void calculateParam() {
        calculateCentroid();
        calculateEnvelope();
        setValid( true );
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = null;
        ret = "points = " + points + "\n";
        ret += ( "envelope = " + envelope + "\n" );
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RingImpl.java,v $
Revision 1.13  2006/11/02 10:20:51  mschneider
Fixed null CRS in Envelope creation.

Revision 1.12  2006/08/08 09:18:56  poth
unecessary type casts removed

Revision 1.11  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
