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
import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * default implementation of @see org.deegree.model.spatialschema.Curve 
 * 
 * @author Andreas Poth
 * @version $Revision: 1.13 $ $Date: 2006/11/02 10:20:51 $
 */
class CurveImpl extends OrientableCurveImpl implements Curve, GenericCurve, Serializable {

    private static final ILogger LOG = LoggerFactory.getLogger( CurveImpl.class );

    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 4060425075179654976L;

    protected ArrayList segments = null;

    /**
     * initialize the curve by submitting a spatial reference system and
     * an array of curve segments. the orientation of the curve is '+'
     *
     * @param segments array of CurveSegment
     */
    public CurveImpl( CurveSegment segments ) throws GeometryException {
        this( '+', new CurveSegment[] { segments } );
    }

    /**
     * initialize the curve by submitting a spatial reference system and
     * an array of curve segments. the orientation of the curve is '+'
     *
     * @param segments array of CurveSegment
     */
    public CurveImpl( CurveSegment[] segments ) throws GeometryException {
        this( '+', segments );
    }

    /**
     * initialize the curve by submitting a spatial reference system,
     * an array of curve segments and the orientation of the curve
     *
     * @param segments array of CurveSegment
     * @param orientation of the curve
     */
    public CurveImpl( char orientation, CurveSegment[] segments ) throws GeometryException {
        super( segments[0].getCoordinateSystem(), orientation );

        this.segments = new ArrayList( segments.length );

        if ( segments != null ) {
            for ( int i = 0; i < segments.length; i++ ) {
                this.segments.add( segments[i] );

                if ( i > 0 ) {
                    if ( !segments[i - 1].getEndPoint().equals( segments[i].getStartPoint() ) ) {
                        throw new GeometryException( "end-point of segment[i-1] "
                                                     + "doesn't match start-point of segment[i]!" );
                    }
                }
            }
        }

        setValid( false );
    }

    /**
     * calculates the envelope of the Curve
     */
    private void calculateEnvelope() {
        try {
            Position[] positions = getAsLineString().getPositions();

            double[] min = positions[0].getAsArray().clone();
            double[] max = min.clone();

            for ( int i = 1; i < positions.length; i++ ) {
                double[] pos = positions[i].getAsArray();

                for ( int j = 0; j < pos.length; j++ ) {
                    if ( pos[j] < min[j] ) {
                        min[j] = pos[j];
                    } else if ( pos[j] > max[j] ) {
                        max[j] = pos[j];
                    }
                }
            }

            envelope = new EnvelopeImpl( new PositionImpl( min ), new PositionImpl( max ), this.crs );
        } catch ( GeometryException e ) {
        }
    }

    /**
     * calculates the boundary of the Curve
     */
    private void calculateBoundary() {
        boundary = new CurveBoundaryImpl( getCoordinateSystem(), getStartPoint().getPosition(),
                                          getEndPoint().getPosition() );
    }

    /**
     * calculates the centroid of the Curve
     */
    private void calculateCentroid() {
        try {
            Position[] positions = getAsLineString().getPositions();

            double[] cen = new double[positions[0].getAsArray().length];

            for ( int i = 0; i < positions.length; i++ ) {
                double[] pos = positions[i].getAsArray();

                for ( int j = 0; j < pos.length; j++ ) {
                    cen[j] += ( pos[j] / positions.length );
                }
            }

            centroid = new PointImpl( new PositionImpl( cen ), null );
        } catch ( Exception e ) {
        }
    }

    /**
     *
     */
    protected void calculateParam() {
        calculateCentroid();
        calculateEnvelope();
        calculateBoundary();
        setValid( true );
    }

    /**
     * returns the boundary of the curve
     */
    public CurveBoundary getCurveBoundary() {
        return (CurveBoundary) boundary;
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
        return getStartPoint().getPosition().getCoordinateDimension();
    }

    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry
     * intersects another Geometry. Within a Complex, the Primitives do not
     * intersect one another. In general, topologically structured data uses shared
     * geometric objects to capture intersection information.<p></p>
     * dummy implementation
     */
    public boolean intersects( Geometry gmo ) {
        boolean inter = false;

        try {
            for ( int i = 0; i < segments.size(); i++ ) {
                CurveSegment cs = getCurveSegmentAt( i );

                if ( cs.intersects( gmo ) ) {
                    inter = true;
                    break;
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "", e );
        }

        return inter;
    }

    /**
     * returns the length of the curve in units of the related spatial reference
     * system
     */
    public double getLength() {
        return -1;
    }

    /**
     * returns the number of segments building the curve
     */
    public int getNumberOfCurveSegments() {
        return segments.size();
    }

    /**
     * returns the first point of the curve. if the curve  doesn't contain a
     * segment or the first segment doesn't contain a point null will be returned
     */
    public Point getStartPoint() {
        if ( getNumberOfCurveSegments() == 0 ) {
            return null;
        }

        Point gmp = null;

        try {
            gmp = getCurveSegmentAt( 0 ).getStartPoint();
        } catch ( GeometryException e ) {
            LOG.logError( "", e );
        }

        return gmp;
    }

    /**
     * returns the last point of the curve.if the curve doesn't contain a segment
     * or the last segment doesn't contain a point null will be returned
     */
    public Point getEndPoint() {
        if ( getNumberOfCurveSegments() == 0 ) {
            return null;
        }

        Point gmp = null;

        try {
            gmp = getCurveSegmentAt( getNumberOfCurveSegments() - 1 ).getEndPoint();
        } catch ( GeometryException e ) {
            LOG.logError( "", e );
        }

        return gmp;
    }

    /**
     * returns the curve as LineString. if there isn't a curve
     * segment within the curve null will be returned
     */
    public LineString getAsLineString()
                            throws GeometryException {
        if ( getNumberOfCurveSegments() == 0 ) {
            return null;
        }

        Position[] tmp = null;

        // normal orientaton
        if ( getOrientation() == '+' ) {
            int cnt = 0;

            for ( int i = 0; i < getNumberOfCurveSegments(); i++ ) {
                cnt += getCurveSegmentAt( i ).getNumberOfPoints();
            }

            tmp = new Position[cnt];

            int k = 0;

            for ( int i = 0; i < getNumberOfCurveSegments(); i++ ) {
                Position[] gmps = getCurveSegmentAt( i ).getPositions();

                for ( int j = 0; j < gmps.length; j++ ) {
                    tmp[k++] = gmps[j];
                }
            }
        } else {
            // inverse orientation
            int cnt = 0;

            for ( int i = getNumberOfCurveSegments() - 1; i >= 0; i-- ) {
                cnt += getCurveSegmentAt( i ).getNumberOfPoints();
            }

            tmp = new Position[cnt];

            int k = 0;

            for ( int i = getNumberOfCurveSegments() - 1; i >= 0; i-- ) {
                Position[] gmps = getCurveSegmentAt( i ).getPositions();

                for ( int j = gmps.length - 1; j >= 0; j-- ) {
                    tmp[k++] = gmps[j];
                }
            }
        }

        return new LineStringImpl( tmp, this.crs );
    }

    /**
     * returns the curve segment at the submitted index
     *
     * @param index index of the curve segment that should be returned
     * @exception GeometryException a exception will be thrown if <tt>index</tt> is smaller
     *             than '0' or larger than <tt>getNumberOfCurveSegments()-1</tt>
     */
    public CurveSegment getCurveSegmentAt( int index )
                            throws GeometryException {
        if ( ( index < 0 ) || ( index > getNumberOfCurveSegments() - 1 ) ) {
            throw new GeometryException( "invalid index/position to get a segment!" );
        }

        return (CurveSegment) segments.get( index );
    }

    /**
     * returns true if no segment is within the curve
     */
    public boolean isEmpty() {
        return ( getNumberOfCurveSegments() == 0 );
    }

    /**
     * translate each point of the curve with the values of the submitted
     * double array.
     */
    public void translate( double[] d ) {
        try {
            for ( int i = 0; i < segments.size(); i++ ) {
                Position[] pos = getCurveSegmentAt( i ).getPositions();

                for ( int j = 0; j < pos.length; j++ ) {
                    pos[j].translate( d );
                }
            }
        } catch ( Exception e ) {
        }
        setValid( false );
    }

    /**
     * checks if this curve is completly equal to the submitted geometry
     * @param other object to compare to
     */
    public boolean equals( Object other ) {
        if ( envelope == null ) {
            calculateEnvelope();
        }
        if ( !super.equals( other ) ) {
            return false;
        }

        if ( !( other instanceof CurveImpl ) ) {
            return false;
        }

        if ( !envelope.equals( ( (Geometry) other ).getEnvelope() ) ) {
            return false;
        }

        if ( segments.size() != ( (Curve) other ).getNumberOfCurveSegments() ) {
            return false;
        }

        try {
            for ( int i = 0; i < segments.size(); i++ ) {
                if ( !getCurveSegmentAt( i ).equals( ( (Curve) other ).getCurveSegmentAt( i ) ) ) {
                    return false;
                }
            }
        } catch ( Exception e ) {
            return false;
        }

        return true;
    }

    /**
     * returns a shallow copy of the geometry
     */
    public Object clone() {
        Curve c = null;

        try {
            CurveSegment[] cs = null;
            cs = (CurveSegment[]) segments.toArray( new CurveSegment[getNumberOfCurveSegments()] );
            c = new CurveImpl( getOrientation(), cs );
        } catch ( Exception ex ) {
            LOG.logError( "Curve_Impl.clone: ", ex );
        }

        return c;
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = null;
        ret = "segments = " + segments + "\n";
        ret += ( "envelope = " + envelope + "\n" );
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CurveImpl.java,v $
Revision 1.13  2006/11/02 10:20:51  mschneider
Fixed null CRS in Envelope creation.

Revision 1.12  2006/08/07 09:49:49  poth
never thrown exception removed

Revision 1.11  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
