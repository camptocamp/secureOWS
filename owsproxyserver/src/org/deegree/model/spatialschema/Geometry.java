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
*
* The basic interface for all geometries. it declares the methods that
* are common to all geometries. this doesn't means for example that all
* geometries defines a valid boundary but is there asked for they should
* be able to answer (with null).
*
* <p>-----------------------------------------------------</p>
*
* @author Andreas Poth
* @version $Revision: 1.9 $ $Date: 2006/11/27 09:07:51 $
* <p>
*/
public interface Geometry extends Serializable {
    /*#CS_CoordinateSystem lnkCS_CoordinateSystem;*/

    /**
     * returns the bounding box of a geometry
     */
    Envelope getEnvelope();

    /**
     * returns the boundary of a geometry
     */
    Boundary getBoundary();

    /**
     * The operation "dimension" shall return the inherent dimension of this
     * Geometry, which shall be less than or equal to the coordinate dimension.
     * The dimension of a collection of geometric objects shall be the largest
     * dimension of any of its pieces. Points are 0-dimensional, curves are
     * 1-dimensional, surfaces are 2-dimensional, and solids are 3-dimensional.
     */
    int getDimension();

    /**
     * The operation "coordinateDimension" shall return the dimension of the
     * coordinates that define this Geometry, which must be the same as the
     * coordinate dimension of the coordinate reference system for this Geometry.
     */
    int getCoordinateDimension();

    /**
     * returns the spatial reference system of a geometry
     */
    CoordinateSystem getCoordinateSystem();


    /**
     * returns true if no geometry values resp. points stored
     * within the geometry.
     */
    boolean isEmpty();

    /**
     * The operation "distance" shall return the distance between this Geometry
     * and another Geometry. This distance is defined to be the greatest lower
     * bound of the set of distances between all pairs of points that include
     * one each from each of the two Geometries. A "distance" value shall be a
     * positive number associated to distance units such as meters or standard
     * foot. If necessary, the second geometric object shall be transformed into
     * the same coordinate reference system as the first before the distance is
     * calculated.<p></p>
     * If the geometric objects overlap, or touch, then their distance apart
     * shall be zero. Some current implementations use a "negative" distance for
     * such cases, but the approach is neither consistent between implementations,
     * nor theoretically viable.
     */
    double distance(Geometry gmo);

    /**
     * translate a geometry by the submitted values. if the length
     * of <tt>d</tt> is smaller then the dimension of the geometry
     * only the first d.length'th coordinates will be translated.
     * If the length of <tt>d</tt> is larger then the dimension of
     * the geometry an ArrayIndexOutOfBoundExceptions raises.
     */
    void translate(double[] d);

    /**
     * The operation "centroid" shall return the mathematical centroid for this
     * Geometry. The result is not guaranteed to be on the object. For heterogeneous
     * collections of primitives, the centroid only takes into account those of the
     * largest dimension. For example, when calculating the centroid of surfaces,
     * an average is taken weighted by area. Since curves have no area they do not
     * contribute to the average.
     */
    Point getCentroid();

    /**
     * The operation "convexHull" shall return a Geometry that represents the
     * convex hull of this Geometry.
     */
    Geometry getConvexHull();

    /**
     * The operation "buffer" shall return a Geometry containing all points whose
     * distance from this Geometry is less than or equal to the "distance" passed
     * as a parameter. The Geometry returned is in the same reference system as
     * this original Geometry. The dimension of the returned Geometry is normally
     * the same as the coordinate dimension - a collection of Surfaces in 2D
     * space and a collection of Solids in 3D space, but this may be application
     * defined.
     */
    Geometry getBuffer(double distance);

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains another Geometry.
     */
    boolean contains(Geometry gmo);

    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains a single point given by a coordinate.
     */
    boolean contains(Position position);

    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry
     * intersects another Geometry. Within a Complex, the Primitives do not
     * intersect one another. In general, topologically structured data uses shared
     * geometric objects to capture intersection information.
     */
    boolean intersects(Geometry gmo);

    /**
     * The "union" operation shall return the set theoretic union of this Geometry
     * and the passed Geometry.
     */
    Geometry union(Geometry gmo);

    /**
     * The "intersection" operation shall return the set theoretic intersection
     * of this Geometry and the passed Geometry.
     */
    Geometry intersection(Geometry gmo) throws GeometryException;

    /**
     * The "difference" operation shall return the set theoretic difference of
     * this Geometry and the passed Geometry.
     */
    Geometry difference(Geometry gmo);

     /**
     provide optimized proximity queries within for a distance .
     calvin  added on 10/21/2003
    */
    boolean isWithinDistance(Geometry gmo, double distance);
    
    void setTolerance(double tolerance);

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Geometry.java,v $
Revision 1.9  2006/11/27 09:07:51  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
