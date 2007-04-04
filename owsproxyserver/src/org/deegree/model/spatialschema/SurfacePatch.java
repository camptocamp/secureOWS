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

import org.deegree.model.crs.CoordinateSystem;

/**
 *
 * Defining the iso geometry <code>SurfacePatch</code> which is used
 * for building surfaces. A surface patch is made of one exterior ring
 * and 0..n interior rings. By definition there can't be a surface patch
 * with no exterior ring.
 * A polygon is a specialized surface patch.
 *
 * -----------------------------------------------------
 *
* @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:15 $
 */

public interface SurfacePatch extends GenericSurface {
    
    
    /**
     * The interpolation determines the surface interpolation mechanism
     * used for this SurfacePatch. This mechanism uses the control
     * points and control parameters defined in the various subclasses
     * to determine the position of this SurfacePatch.
     */
    SurfaceInterpolation getInterpolation();
    
    /**
     * returns the exterior ring of the surface
     */
    Position[] getExteriorRing();
    
    /**
     * returns the interior rings of the surface
     */
    Position[][] getInteriorRings();
    
    /**
     * returns the coordinate system of the surface patch
     */
    CoordinateSystem getCoordinateSystem();
    
    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry
     * intersects another Geometry. Within a Complex, the Primitives do not
     * intersect one another. In general, topologically structured data uses shared
     * geometric objects to capture intersection information.
     */
    boolean intersects(Geometry gmo);
    
    /**
     * The Boolean valued operation "contains" shall return TRUE if this Geometry
     * contains another Geometry.
     */
    boolean contains(Geometry gmo);
    
    /**
     * The operation "centroid" shall return the mathematical centroid for this
     * Geometry. The result is not guaranteed to be on the object.
     */
    public Point getCentroid();
    
    /**
     * The operation "area" shall return the area of this GenericSurface.
     * The area of a 2 dimensional geometric object shall be a numeric
     * measure of its surface area Since area is an accumulation (integral)
     * of the product of two distances, its return value shall be in a unit
     * of measure appropriate for measuring distances squared.
     */
    public double getArea();
    
    /**
     * @link aggregationByValue
     * @clientCardinality 1..*
     */
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SurfacePatch.java,v $
Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
