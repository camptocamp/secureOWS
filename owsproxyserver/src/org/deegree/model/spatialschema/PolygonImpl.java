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
 * default implementierung of the Polygon 
 *  * @version 11.6.2001 * @author Andreas Poth
 */

class PolygonImpl extends SurfacePatchImpl implements Polygon, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -1293845886457211088L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( PolygonImpl.class );

    private SurfaceBoundary boundary = null;


    /**
     * Creates a new PolygonImpl object.
     *
     * @param interpolation 
     * @param exteriorRing 
     * @param interiorRings 
     * @param crs 
     *
     * @throws GeometryException 
     */
    public PolygonImpl( SurfaceInterpolation interpolation, Position[] exteriorRing, 
                            Position[][] interiorRings, CoordinateSystem crs )
                    throws GeometryException {
        super( interpolation, exteriorRing, interiorRings, crs );

        Ring outer = new RingImpl( exteriorRing, crs );
        Ring[] inner = null;

        if ( interiorRings != null ) {
            inner = new Ring[interiorRings.length];

            for ( int i = 0; i < inner.length; i++ ) {
                inner[i] = new RingImpl( interiorRings[i], crs );
            }
        }

        boundary = new SurfaceBoundaryImpl( outer, inner );
    }

    /**
     * The operation "boundary" shall return the boundary of this
     * SurfacePatch represented as a collection of Curves organized
     * as a SurfaceBoundary, consisting of Curve instances along the
     * boundary of the aggregate Surface, and interior to the Surface
     * where SurfacePatches are adjacent.
     * 
     * @uml.property name="boundary"
     */
    public SurfaceBoundary getBoundary() {
        return boundary;
    }

    /**
     * checks if this curve is completly equal to the submitted geometry
     * @param other object to compare to
     */
    public boolean equals( Object other ) {
        if ( !super.equals( other ) || !( other instanceof PolygonImpl ) ) {
            return false;
        }

        return true;
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = "SurfacePatch: ";
        ret = "interpolation = " + interpolation + "\n";
        ret += "exteriorRing = \n";

        for ( int i = 0; i < exteriorRing.length; i++ ) {
            ret += ( exteriorRing[i] + "\n" );
        }

        ret += ( "interiorRings = " + interiorRings + "\n" );
        ret += ( "envelope = " + envelope + "\n" );
        return ret;
    }

    /**
     * returns a shallow copy of the geometry
     */
    public Object clone() {
        Polygon p = null;

        try {
            p = new PolygonImpl( 
                        new SurfaceInterpolationImpl( getInterpolation().getValue() ), 
                        getExteriorRing(), getInteriorRings(), this.crs );
        } catch ( Exception ex ) {
            LOG.logError( "Polygon_Impl.clone: ", ex );
        }

        return p;
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
            if ( gmo instanceof Point ) {
                inter = LinearIntersects.intersects( ( (Point)gmo ).getPosition(), this );
            } else if ( gmo instanceof Curve ) {
                inter = LinearIntersects.intersects( (Curve)gmo, new SurfaceImpl( this ) );
            } else if ( gmo instanceof Surface ) {
                inter = LinearIntersects.intersects( (Surface)gmo, new SurfaceImpl( this ) );
            } else if ( gmo instanceof Aggregate ) {
                inter = intersectsMultiObject( (Aggregate)gmo );
            }
        } catch ( Exception e ) {
        }

        return inter;
    }

    /**
     * the operations returns true if the submitted multi primitive intersects
     * with the curve segment
     */
    private boolean intersectsMultiObject( Aggregate mprim ) throws Exception {
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
     */
    public boolean contains( Geometry gmo ) {
        boolean contain = false;

        try {
            if ( gmo instanceof Point ) {
                contain = LinearContains.contains( this, ( (Point)gmo ).getPosition() );
            } else if ( gmo instanceof Curve ) {                
                contain = LinearContains.contains( this, ( (Curve)gmo ).getAsLineString() );
            } else if ( gmo instanceof Surface ) {
                contain = LinearContains.contains( new SurfaceImpl( this ), (Surface)gmo );
            } else if ( gmo instanceof Aggregate ) {
                contain = containsMultiObject( (Aggregate)gmo );
            }
        } catch ( Exception e ) {
            LOG.logError( "", e );
        }

        return contain;
    }

    /**
     *
     *
     * @param gmo 
     *
     * @return 
     */
    private boolean containsMultiObject( Aggregate gmo ) {
        try {
            for ( int i = 0; i < gmo.getSize(); i++ ) {
                if ( !contains( gmo.getObjectAt( i ) ) ) {
                    return false;
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "", e );
        }

        return true;
    }
} /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PolygonImpl.java,v $
Revision 1.9  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
