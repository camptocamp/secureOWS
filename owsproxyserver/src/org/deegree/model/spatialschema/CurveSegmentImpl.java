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

import org.deegree.model.crs.CoordinateSystem;

/**
 * default implementation of the CurveSegment interface from * package jago.model. the class is abstract because it should be * specialized by derived classes <code>LineString</code> for example *  * <p>---------------------------------------------------------------------------</p> * @version 10.6.2001 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */

abstract class CurveSegmentImpl implements CurveSegment, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -8102075931849374162L;

    /**
     * 
     * @uml.property name="crs"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    protected CoordinateSystem crs = null;

    /**
     * 
     * @uml.property name="points"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    protected Position[] points = new Position[0];

    /**
     * Creates a new CurveSegmentImpl object.
     *
     * @param gmps 
     * @param crs 
     *
     * @throws GeometryException 
     */
    protected CurveSegmentImpl( Position[] gmps, CoordinateSystem crs )
                            throws GeometryException {
        if ( gmps == null ) {
            throw new GeometryException( "can't create an empty curve segment" );
        }

        points = gmps;

        // get spatial reference system of the curve segment from the first point
        this.crs = crs;
    }

    /**
     * returns the first point of the curve. if the curve segment
     * doesn't contain a point <code>null</code> will be returned
     */
    public Point getStartPoint() {
        return new PointImpl( points[0], crs );
    }

    /**
     * returns the last point of the curve. if the curve segment
     * doesn't contain a point <code>null</code> will be returned
     */
    public Point getEndPoint() {
        return new PointImpl( points[getNumberOfPoints() - 1], crs );
    }

    /**
     * returns the number of points building the curve 
     * or curve segment
     */
    public int getNumberOfPoints() {
        return points.length;
    }

    /**
     * returns all positions of the segement as array of Position. If
     * the segment is empty null will be returned
     */
    public Position[] getPositions() {
        return points;
    }

    /**
     * returns the curve segment position at the submitted index
     */
    public Position getPositionAt( int index ) {
        return points[index];
    }

    /**
     * reverses the direction of the curvesegment
     */
    public void reverse() {
        Position[] reverse_ = new Position[points.length];

        for ( int i = 0; i < points.length; i++ ) {
            reverse_[points.length - 1 - i] = points[i];
        }

        points = reverse_;
    }

    /**
     * returns the coordinate system of the curve segment
     */
    public CoordinateSystem getCoordinateSystem() {
        return crs;
    }

    /**
     * checks if this curve segment is completly equal to the submitted geometry
     * @param other object to compare to
     */
    public boolean equals( Object other ) {
        if ( ( other == null ) || !( other instanceof CurveSegmentImpl ) ) {
            return false;
        }

        if ( ( crs == null ) && ( ( (CurveSegmentImpl)other ).getCoordinateSystem() != null ) ) {
            return false;
        }

        if ( crs != null ) {
            if ( !crs.equals( ( (CurveSegmentImpl)other ).getCoordinateSystem() ) ) {
                return false;
            }
        } else {
            if ( ( (CurveSegmentImpl)other ).getCoordinateSystem() != null ) {
                return false;
            }
        }

        Position[] p1 = getPositions();
        Position[] p2 = ( (CurveSegment)other ).getPositions();
        
        if ( p1.length != p2.length ) {
            return false;
        }
       

        //if ( !Arrays.equals( p1, p2 ) ) {
            //TODO 
            // correct comparing of each point considering current tolerance level
        //}

        return true;
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains another Geometry.<p></p>
     */
    public boolean contains( Geometry gmo ) {
        throw new NoSuchMethodError( "the contains operation for curve segments " + 
                                     "isn't supported at the moment." );
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = null;
        ret = "points = ";
        ret += ( "crs = " + crs + "\n" );
        return ret;
    }
} /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CurveSegmentImpl.java,v $
Revision 1.8  2006/11/27 09:07:51  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
