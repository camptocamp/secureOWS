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

/**
 * default implementation of the Surface interface from package jago.model.
 * <p>
 * </p>
 * for simplicity of the implementation it is assumed that a surface is build from just one surface
 * patch. this isn't completly confrom to the ISO 19107 and the OGC GAIA specification but
 * sufficient for most applications.
 * <p>
 * </p>
 * It will be extended to fullfill the complete specs as soon as possible.
 * 
 * @version 05.04.2002
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */

public class SurfaceImpl extends OrientableSurfaceImpl implements Surface, GenericSurface,
                                                      Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -2148069106391096842L;

    private static final ILogger LOG = LoggerFactory.getLogger( SurfaceImpl.class );

    protected SurfacePatch[] patch = null;

    private double area = 0;

    /**
     * initializes the surface with default orientation submitting one surface patch.
     * 
     * @param surfacePatch
     *            patches of the surface.
     * @throws GeometryException
     *             will be thrown if orientation is invalid
     */
    public SurfaceImpl( SurfacePatch surfacePatch ) throws GeometryException {
        this( '+', surfacePatch );
    }

    /**
     * initializes the surface with default orientation submitting one surface patch.
     * 
     * @param surfacePatches
     *            patches of the surface.
     * @throws GeometryException
     *             will be thrown if orientation is invalid
     */
    public SurfaceImpl( SurfacePatch[] surfacePatches ) throws GeometryException {
        this( '+', surfacePatches );
    }

    /**
     * initializes the surface submitting the orientation and one surface patch.
     * 
     * @param orientation
     *            of the surface
     * 
     * @param surfacePatch
     *            patches of the surface.
     * @throws GeometryException
     *             will be thrown if orientation is invalid
     */
    public SurfaceImpl( char orientation, SurfacePatch surfacePatch ) throws GeometryException {
        super( surfacePatch.getCoordinateSystem(), orientation );

        patch = new SurfacePatch[] { surfacePatch };

        setValid( false );
    }

    /**
     * initializes the surface submitting the orientation and one surface patch.
     * 
     * @param orientation
     *            of the surface
     * 
     * @param surfacePatches
     *            patches of the surface.
     * @throws GeometryException
     *             will be thrown if orientation is invalid
     */
    public SurfaceImpl( char orientation, SurfacePatch[] surfacePatches ) throws GeometryException {
        super( surfacePatches[0].getCoordinateSystem(), orientation );
        patch = surfacePatches;
        setValid( false );
    }

    /**
     * initializes the surface with default orientation submitting the surfaces boundary
     * 
     * @param boundary
     *            boundary of the surface
     * @throws GeometryException
     *             will be thrown if orientation is invalid
     */
    public SurfaceImpl( SurfaceBoundary boundary ) throws GeometryException {
        this( '+', boundary );
    }

    /**
     * initializes the surface submitting the orientation and the surfaces boundary.
     * 
     * @param orientation
     *            of the surface
     * 
     * @param boundary
     *            boundary of the surface
     * 
     * @throws GeometryException
     *             will be thrown if orientation is invalid
     */
    public SurfaceImpl( char orientation, SurfaceBoundary boundary ) throws GeometryException {
        // todo
        // extracting surface patches from the boundary
        super( boundary.getCoordinateSystem(), orientation );

        this.boundary = boundary;
    }

    /**
     * calculates the centroid and area of the surface
     */
    private void calculateCentroidArea() {
        double x = 0;
        double y = 0;
        area = 0;
        for ( int i = 0; i < patch.length; i++ ) {
            x += ( patch[i].getCentroid().getX() * patch[i].getArea() );
            y += ( patch[i].getCentroid().getY() * patch[i].getArea() );
            area += patch[i].getArea();
        }
        centroid = GeometryFactory.createPoint( x / area, y / area, this.crs );
    }

    /**
     * calculates the boundary and area of the surface
     */
    private void calculateBoundary() {
        // TODO
        // consider more than one patch
        try {
            Ring ext = new RingImpl( patch[0].getExteriorRing(), crs );
            Position[][] inn_ = patch[0].getInteriorRings();
            Ring[] inn = null;

            if ( inn_ != null ) {
                inn = new RingImpl[inn_.length];

                for ( int i = 0; i < inn_.length; i++ ) {
                    inn[i] = new RingImpl( inn_[i], crs );
                }
            }
            boundary = new SurfaceBoundaryImpl( ext, inn );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * calculates area, centroid and the envelope of the surface
     */
    @Override
    protected void calculateParam() {
        calculateCentroidArea();
        try {
            calculateEnvelope();
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }
        calculateBoundary();
        setValid( true );
    }

    /**
     * calculates the envelope of the surface
     */
    private void calculateEnvelope()
                            throws GeometryException {

        envelope = patch[0].getEnvelope();
        for ( int i = 1; i < patch.length; i++ ) {
            envelope = envelope.merge( patch[i].getEnvelope() );
        }

    }

    /**
     * returns the length of all boundaries of the surface in a reference system appropriate for
     * measuring distances.
     */
    public double getPerimeter() {
        return -1;
    }

    /**
     * The operation "area" shall return the area of this GenericSurface. The area of a 2
     * dimensional geometric object shall be a numeric measure of its surface area Since area is an
     * accumulation (integral) of the product of two distances, its return value shall be in a unit
     * of measure appropriate for measuring distances squared.
     */
    public double getArea() {
        if ( !isValid() ) {
            calculateParam();
        }
        return area;
    }

    public SurfaceBoundary getSurfaceBoundary() {
        if ( !isValid() ) {
            calculateParam();
        }
        return (SurfaceBoundary) boundary;
    }

    public int getNumberOfSurfacePatches() {
        return patch.length;
    }

    public SurfacePatch getSurfacePatchAt( int index )
                            throws GeometryException {
        if ( index >= patch.length ) {
            throw new GeometryException( "invalid index/position to get a patch!" );
        }
        return patch[index];
    }

    /**
     * checks if this surface is completly equal to the submitted geometry
     * 
     * @param other
     *            object to compare to
     */
    @Override
    public boolean equals( Object other ) {
        if ( !super.equals( other ) ) {
            return false;
        }
        if ( !( other instanceof SurfaceImpl ) ) {
            return false;
        }
        if ( envelope == null ) {
            try {
                calculateEnvelope();
            } catch ( GeometryException e1 ) {
                return false;
            }
        }
        if ( !envelope.equals( ( (Geometry) other ).getEnvelope() ) ) {
            return false;
        }
        try {
            for ( int i = 0; i < patch.length; i++ ) {
                if ( !patch[i].equals( ( (Surface) other ).getSurfacePatchAt( i ) ) ) {
                    return false;
                }
            }
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    /**
     * The operation "dimension" shall return the inherent dimension of this Geometry, which shall
     * be less than or equal to the coordinate dimension. The dimension of a collection of geometric
     * objects shall be the largest dimension of any of its pieces. Points are 0-dimensional, curves
     * are 1-dimensional, surfaces are 2-dimensional, and solids are 3-dimensional.
     */
    public int getDimension() {
        return 2;
    }

    /**
     * The operation "coordinateDimension" shall return the dimension of the coordinates that define
     * this Geometry, which must be the same as the coordinate dimension of the coordinate reference
     * system for this Geometry.
     */
    public int getCoordinateDimension() {
        return patch[0].getExteriorRing()[0].getCoordinateDimension();
    }

    /**
     * @return a shallow copy of the geometry
     */
    @Override
    public Object clone() {
        Surface s = null;
        try {
            s = new SurfaceImpl( getOrientation(), patch );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }

        return s;
    }

    /**
     * translate each point of the surface with the values of the submitted double array.
     */
    @Override
    public void translate( double[] d ) {
        for ( int i = 0; i < patch.length; i++ ) {
            Position[] ext = patch[i].getExteriorRing();
            Position[][] inn = patch[i].getInteriorRings();
            for ( int j = 0; j < ext.length; j++ ) {
                ext[j].translate( d );
            }
            if ( inn != null ) {
                for ( int j = 0; j < inn.length; j++ ) {
                    for ( int k = 0; k < inn[j].length; k++ ) {
                        inn[j][k].translate( d );
                    }
                }
            }
        }
        setValid( false );
    }

    /**
     * The boolean valued operation "intersects" shall return TRUE if this <tt>SurfaceImpl</tt>
     * intersects with the given <tt>Geometry</t>.
     * Within a <tt>Complex</tt>, the <tt>Primitives</tt> do not
     * intersect one another. In general, topologically structured data uses
     * shared geometric objects to capture intersection information.
     * @param gmo the <tt>Geometry</tt> to test for intersection
     * @return true if the <tt>Geometry</tt> intersects with this
     */
    @Override
    public boolean intersects( Geometry gmo ) {
        if ( !isValid() ) {
            calculateParam();
        }

        for ( int i = 0; i < patch.length; i++ ) {
            if ( patch[i].intersects( gmo ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry contains a single
     * point given by a coordinate.
     * <p>
     * </p>
     */
    @Override
    public boolean contains( Position position ) {
        return contains( new PointImpl( position, null ) );
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry contains another
     * Geometry.
     * <p>
     * </p>
     */
    @Override
    public boolean contains( Geometry gmo ) {
        if ( !isValid() ) {
            calculateParam();
        }
        return boundary.contains( gmo );
    }

    /**
     * 
     * 
     * @return the Stringrepresenation of this surface.
     */
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer( 2000 );
        ret.append( "\n------------------------------------------\n" );
        ret.append( getClass().getName() ).append( ":\n" );
        ret.append( "envelope = " ).append( envelope ).append( "\n" );
        ret.append( "patch = " ).append( patch.length ).append( "\n" );
        for ( int i = 0; i < patch.length; i++ ) {
            Position[] pos = patch[i].getExteriorRing();
            ret.append( "Exterior Ring: \n" );
            ret.append( "length: " ).append( pos.length ).append("\n" );
            for ( int j = 0; j < pos.length; j++ ) {
                ret.append( pos[j]+"\n" );
            }
        }
        ret.append( "\n------------------------------------------\n" );
        return ret.toString();
    }
}
/* **********************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: SurfaceImpl.java,v $
 * Revision 1.22  2006/11/23 09:20:03  bezema
 * changed the to String method, so that each Surface resides inside a textual box
 *
 * Revision 1.21  2006/10/16 18:10:58  poth
 * toString method adjusted
 *
 * Revision 1.20  2006/10/16 12:49:23  poth
 * toString method adjusted
 *
 * Revision 1.19  2006/10/12 15:44:26  poth
 * useless code fragment removed / footer comment corrected
 *
 * Revision 1.18  2006/10/02 07:33:28  bezema
 * formatted and documentation
 * Revision
 * 1.17 2006/09/18 12:39:48 bezema added annotations and javadoc
 * 
 * Revision 1.16 2006/07/12 14:46:15 poth comment footer added
 * 
 ********************************************************************************************* */
