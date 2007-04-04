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
* default implementation of the LineString interface of
* package jago.model. 
*
* ------------------------------------------------------------
* @version 10.6.2001
* @author Andreas Poth
*/
class LineStringImpl extends CurveSegmentImpl implements LineString, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 8093549521711824076L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( LineStringImpl.class );
    
    /**
     * Creates a new LineStringImpl object.
     *
     * @param gmps 
     * @param cs 
     *
     * @throws GeometryException 
     */
    public LineStringImpl( Position[] gmps, CoordinateSystem cs )
                       throws GeometryException {
        super( gmps, cs );
    }

    /**
    * returns a shallow copy of the geometry
    */
    public Object clone() {
        CurveSegment cs = null;

        try {
            cs = new LineStringImpl( getPositions(), getCoordinateSystem() );
        } catch ( Exception ex ) {
            LOG.logError( "LineString_Impl.clone: ", ex );
        }

        return cs;
    }

    /**
     * returns the length of the curve in units of the 
     * related spatial reference system
     */
    public double getLength() {
        return -1;
    }

    /**
     * returns a reference to itself 
     */
    public LineString getAsLineString() throws GeometryException {
        return this;
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
                CurveSegment[] cs = new CurveSegment[] { this };
                inter = LinearIntersects.intersects( (Curve)gmo, new CurveImpl( cs ) );
            } else if ( gmo instanceof Surface ) {
                CurveSegment[] cs = new CurveSegment[] { this };
                inter = LinearIntersects.intersects( new CurveImpl( cs ), (Surface)gmo );
            } else if ( gmo instanceof MultiPrimitive ) {
                inter = intersectsMultiPrimitive( (MultiPrimitive)gmo );
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
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains another Geometry.
     */
    public boolean contains( Geometry gmo ) {
        return false;
    }
} /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LineStringImpl.java,v $
Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
