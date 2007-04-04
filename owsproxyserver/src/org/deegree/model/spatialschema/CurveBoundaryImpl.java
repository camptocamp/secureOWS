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
 * default implementation of the CurveBoundary interface from * package jago.model.  *  * <p>------------------------------------------------------------</p> * @version 10.6.2001 * @author Andreas Poth
 */

class CurveBoundaryImpl extends PrimitiveBoundaryImpl implements CurveBoundary,
                                                                         Serializable {
    
    private ILogger LOG = LoggerFactory.getLogger( CurveBoundaryImpl.class );
    
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 4226497939552424434L;
    private Position ep = null;
    private Position sp = null;

    /**
     * constructor of curve_boundary with CS_CoordinateSystem and startpoint and endpoint
     */
    public CurveBoundaryImpl( CoordinateSystem crs, Position sp, Position ep ) {
        super( crs );

        this.sp = sp;
        this.ep = ep;

        setValid( false );
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
        return getStartPoint().getCoordinateDimension();
    }

    /**
    * returns a shallow copy of the geometry
    */
    public Object clone() {
        CurveBoundary cb = null;

        try {
            cb = new CurveBoundaryImpl( getCoordinateSystem(), sp, ep );
        } catch ( Exception ex ) {
            LOG.logError( ex.getMessage(), ex );
        }

        return cb;
    }

    /**
     * returns the StartPoint of the boundary
     */
    public Position getStartPoint() {
        return sp;
    }

    /**
     * returns the EndPoint of the boundary
     */
    public Position getEndPoint() {
        return ep;
    }

    /**
    * checks if this curve is completly equal to the submitted geometry
    * @param other object to compare to
    */
    public boolean equals( Object other ) {
        if ( !super.equals( other ) || !( other instanceof CurveBoundaryImpl ) ) {
            return false;
        }

        if ( !ep.equals( ( (CurveBoundary)other ).getEndPoint() ) || 
                 !sp.equals( ( (CurveBoundary)other ).getStartPoint() ) ) {
            return false;
        }

        return true;
    }

    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry
     * intersects another Geometry. Within a Complex, the Primitives do not
     * intersect one another. In general, topologically structured data uses shared
     * geometric objects to capture intersection information.
     */
    public boolean intersects( Geometry gmo ) {
        boolean inter = false;
        Point p1 = new PointImpl( sp, crs );
        Point p2 = new PointImpl( ep, crs );

        try {
            if ( gmo instanceof Point ) {
                inter = LinearIntersects.intersects( p1, (Point)gmo );

                if ( !inter ) {
                    inter = LinearIntersects.intersects( p2, (Point)gmo );
                }
            } else if ( gmo instanceof Curve ) {
                inter = LinearIntersects.intersects( p1, (Curve)gmo );

                if ( !inter ) {
                    inter = LinearIntersects.intersects( p2, (Curve)gmo );
                }
            } else if ( gmo instanceof Surface ) {
                inter = LinearIntersects.intersects( p1, (Surface)gmo );

                if ( !inter ) {
                    inter = LinearIntersects.intersects( p2, (Surface)gmo );
                }
            } else if ( gmo instanceof MultiPrimitive ) {
                inter = intersectsMultiPrimitive( (MultiPrimitive)gmo );
            }
        } catch ( Exception e ) {
        }

        return inter;
    }

    /**
     * the operations returns true if the submitted multi primitive intersects
     * with the curve segment
     */
    private boolean intersectsMultiPrimitive( MultiPrimitive mprim ) throws Exception {
        boolean inter = false;

        int cnt = mprim.getSize();

        for ( int i = 0; i < cnt; i++ ) {
            if ( intersects( mprim.getPrimitiveAt( i ) ) ) {
                inter = true;
                break;
            }
        }

        return inter;
    }

    /**
     * calculates the envelope of the curve boundary
     */
    private void calculateEnvelope() {
        double[] min = sp.getAsArray().clone();
        double[] max = ep.getAsArray().clone();

        for ( int i = 0; i < min.length; i++ ) {
            if ( min[i] > max[i] ) {
                double d = min[i];
                min[i] = max[i];
                max[i] = d;
            }
        }

        envelope = new EnvelopeImpl( new PositionImpl( min ), new PositionImpl( max ), this.crs );
    }

    /**
     * calculates the envelope of the curve boundary
     */
    protected void calculateParam() {
        calculateEnvelope();
        setValid( true );
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        return "point1: [" + sp + "] - point2: [" + ep + "]";
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CurveBoundaryImpl.java,v $
Revision 1.13  2006/11/02 10:20:51  mschneider
Fixed null CRS in Envelope creation.

Revision 1.12  2006/08/07 09:49:50  poth
never thrown exception removed

Revision 1.11  2006/08/07 09:49:12  poth
unneccessary type cast removed

Revision 1.10  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
