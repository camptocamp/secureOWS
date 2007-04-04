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

 Contact :

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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;

/**
 * Default implementation of the Geometry interface from package deegree.model. The implementation
 * is abstract because only the management of the spatial reference system is unique for all
 * geometries.
 * <p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.16 $ $Date: 2006/11/27 09:07:51 $
 */

public abstract class GeometryImpl implements Geometry, Serializable {

    private static final ILogger LOG = LoggerFactory.getLogger( GeometryImpl.class );

    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 130728662284673112L;

    protected static double mute = 0.000000001;

    protected CoordinateSystem crs = null;

    protected Boundary boundary = null;

    protected Envelope envelope = null;

    protected Geometry convexHull = null;

    protected Point centroid = null;

    protected boolean empty = true;

    protected boolean valid = false;

    /**
     * constructor that sets the spatial reference system
     * 
     * @param crs
     *            new spatial reference system
     */
    protected GeometryImpl( CoordinateSystem crs ) {
        setCoordinateSystem( crs );
    }

    /**
     * @return the spatial reference system of a geometry
     */
    public CoordinateSystem getCoordinateSystem() {
        return crs;
    }

    /**
     * sets the spatial reference system
     * 
     * @param crs
     *            new spatial reference system
     */
    public void setCoordinateSystem( CoordinateSystem crs ) {
        this.crs = crs;
    }

    /**
     * @return a shallow copy of the geometry. this isn't realized at this level so a
     *         CloneNotSupportedException will be thrown.
     */
    @Override
    public Object clone()
                            throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * @return true if no geometry values resp. points stored within the geometry.
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * 
     * @param empty
     *            indicates the geometry as empty
     */
    public void setEmpty( boolean empty ) {
        this.empty = empty;
    }

    /**
     * returns the boundary of the surface as general boundary
     * 
     */
    public Boundary getBoundary() {
        if ( !isValid() ) {
            calculateParam();
        }
        return boundary;
    }

    /**
     * dummy implementation of this method
     */
    public void translate( double[] d ) {
        setValid( false );
    }

    /**
     * <p>
     * The operation "distance" shall return the distance between this Geometry and another
     * Geometry. This distance is defined to be the greatest lower bound of the set of distances
     * between all pairs of points that include one each from each of the two Geometries. A
     * "distance" value shall be a positive number associated to distance units such as meters or
     * standard foot. If necessary, the second geometric object shall be transformed into the same
     * coordinate reference system as the first before the distance is calculated.
     * </p>
     * <p>
     * If the geometric objects overlap, or touch, then their distance apart shall be zero. Some
     * current implementations use a "negative" distance for such cases, but the approach is neither
     * consistent between implementations, nor theoretically viable.
     * </p>
     */
    public double distance( Geometry gmo ) {
        try {
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( gmo );
            return jtsThis.distance( jtsThat );
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
            return -1;
        }
    }

    /**
     * <p>
     * The operation "centroid" shall return the mathematical centroid for this Geometry. The result
     * is not guaranteed to be on the object. For heterogeneous collections of primitives, the
     * centroid only takes into account those of the largest dimension. For example, when
     * calculating the centroid of surfaces, an average is taken weighted by area. Since curves have
     * no area they do not contribute to the average.
     * </p>
     */
    public Point getCentroid() {
        if ( !isValid() ) {
            calculateParam();
        }
        return centroid;
    }

    /**
     * returns the bounding box / envelope of a geometry
     */
    public Envelope getEnvelope() {
        if ( !isValid() ) {
            calculateParam();
        }
        return envelope;
    }

    /**
     * <p>
     * The operation "convexHull" shall return a Geometry that represents the convex hull of this
     * Geometry.
     * </p>
     * This method throws an @see java.lang.UnsupportedOperationException an may has an
     * useful implementation in extending classes
     */
    public Geometry getConvexHull() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * The operation "buffer" shall return a Geometry containing all points whose distance from this
     * Geometry is less than or equal to the "distance" passed as a parameter. The Geometry returned
     * is in the same reference system as this original Geometry. The dimension of the returned
     * Geometry is normally the same as the coordinate dimension - a collection of Surfaces in 2D
     * space and a collection of Solids in 3D space, but this may be application defined.
     * </p>
     * This method throws an @see java.lang.UnsupportedOperationException an may has an
     * useful implementation in extending classes
     */
    public Geometry getBuffer( double distance ) {
        throw new UnsupportedOperationException();
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry contains another
     * Geometry.
     * <p>
     * 
     * @param that
     *            the Geometry to test (whether is is contained)
     * @return true if the given object is contained, else false
     */
    public boolean contains( Geometry that ) {
        try {
            // let JTS do the hard work
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( that );
            return jtsThis.contains( jtsThat );

        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
            return false;
        }
    }

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry contains a single
     * point given by a coordinate.
     * 
     * @param position
     *            Position to test (whether is is contained)
     * @return true if the given object is contained, else false
     */
    public boolean contains( Position position ) {
        return contains( new PointImpl( position, null ) );
    }

    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry intersects
     * another Geometry. Within a Complex, the Primitives do not intersect one another. In general,
     * topologically structured data uses shared geometric objects to capture intersection
     * information.
     * 
     * @param that
     *            the Geometry to intersect with
     * @return true if the objects intersects, else false
     */
    public boolean intersects( Geometry that ) {
        try {
            // let JTS do the hard work
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( that );
            return jtsThis.intersects( jtsThat );

        } catch ( GeometryException e ) {
            LOG.logError( "", e );
            return false;
        }
    }

    /**
     * The "union" operation shall return the set theoretic union of this Geometry and the passed
     * Geometry.
     * 
     * @param that
     *            the Geometry to unify
     * @return intersection or null, if computation failed
     */
    public Geometry union( Geometry that ) {
        Geometry union = null;

        try {
            // let JTS do the hard work
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( that );
            com.vividsolutions.jts.geom.Geometry jtsUnion = jtsThis.union( jtsThat );

            if ( !jtsUnion.isEmpty() ) {
                union = JTSAdapter.wrap( jtsUnion );
                ( (GeometryImpl) union ).setCoordinateSystem( getCoordinateSystem() );
            }
        } catch ( GeometryException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
        }
        return union;
    }

    /**
     * The "intersection" operation shall return the set theoretic intersection of this
     * <tt>Geometry</tt> and the passed <tt>Geometry</tt>.
     * 
     * @param that
     *            the Geometry to intersect with
     * @return intersection or null, if it is empty (or computation failed)
     */
    public Geometry intersection( Geometry that )
                            throws GeometryException {

        Geometry intersection = null;

        // let JTS do the hard work
        com.vividsolutions.jts.geom.Geometry jtsIntersection = null;
        try{
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( that );
            jtsIntersection = jtsThis.intersection( jtsThat );
        }
        catch (Exception e ){
            throw new GeometryException( e.getLocalizedMessage() );
        }

        if ( jtsIntersection != null  && !jtsIntersection.isEmpty() ) {
            intersection = JTSAdapter.wrap( jtsIntersection );
            ( (GeometryImpl) intersection ).setCoordinateSystem( getCoordinateSystem() );
        }

        return intersection;
    }

    /**
     * The "difference" operation shall return the set theoretic difference of this Geometry and the
     * passed Geometry.
     * 
     * @param that
     *            the Geometry to calculate the difference with
     * @return difference or null, if it is empty (or computation failed)
     */
    public Geometry difference( Geometry that ) {
        Geometry difference = null;

        try {
            // let JTS do the hard work
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( that );
            com.vividsolutions.jts.geom.Geometry jtsDifference = jtsThis.difference( jtsThat );

            if ( !jtsDifference.isEmpty() ) {
                difference = JTSAdapter.wrap( jtsDifference );
                ( (GeometryImpl) difference ).setCoordinateSystem( getCoordinateSystem() );
            }
        } catch ( GeometryException e ) {
            LOG.logError( "", e );
        }
        return difference;
    }

    /**
     * Compares the Geometry to be equal to another Geometry.
     * 
     * @param that
     *            the Geometry to test for equality
     * @return true if the objects are equal, else false
     */
    @Override
    public boolean equals( Object that ) {
        if ( ( that == null ) || !( that instanceof GeometryImpl ) ) {
            return false;
        }
        if ( crs != null ) {
            if ( !crs.equals( ( (Geometry) that ).getCoordinateSystem() ) ) {
                return false;
            }
        } else {
            if ( ( (Geometry) that ).getCoordinateSystem() != null ) {
                return false;
            }             
        }
        
        // do not add JTS calls here!!!!
        
        return true;

    }

    /**
     * provide optimized proximity queries within for a distance . calvin added on 10/21/2003
     */
    public boolean isWithinDistance( Geometry that, double distance ) {
        if ( that == null )
            return false;
        try {
            // let JTS do the hard work
            com.vividsolutions.jts.geom.Geometry jtsThis = JTSAdapter.export( this );
            com.vividsolutions.jts.geom.Geometry jtsThat = JTSAdapter.export( that );
            return jtsThis.isWithinDistance( jtsThat, distance );
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
            return false;
        }

    }
    
    public void setTolerance(double tolerance) {
        mute = tolerance;
    }

    /**
     * 
     * @param valid
     *            invalidates the calculated parameters of the Geometry
     */
    protected void setValid( boolean valid ) {
        this.valid = valid;
    }

    /**
     * @return true if the calculated parameters of the Geometry are valid and false if they must be
     *         recalculated
     */
    protected boolean isValid() {
        return valid;
    }
       

    /**
     * recalculates internal parameters
     */
    protected abstract void calculateParam();

    /**
     * 
     * @return the String representation containing the crs, empty-field and the mut-field
     */
    @Override
    public String toString() {
        String ret = null;
        ret = "CoordinateSystem = " + crs + "\n";
        ret += ( "empty = " + empty + "\n" );
        ret += ( "mute = " + mute + "\n" );
        return ret;
    }
}
/* *************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GeometryImpl.java,v $
 * Revision 1.16  2006/11/27 09:07:51  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.15  2006/10/17 08:14:23  poth
 * getBuffer now throws UnsupportedOperationException
 *
 * Revision 1.14  2006/10/02 07:31:34  bezema
 * added a try clause over the merge section.
 * Changes to this class. What the people have been up to:
 * Revision 1.13  2006/09/14 08:44:39  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.12  2006/08/28 07:50:11  bezema
 * Added isNan support for the 3 dimension
 * Revision 1.11
 * 2006/07/04 18:32:09 poth code formatation
 * 
 * 
 ************************************************************************************************ */