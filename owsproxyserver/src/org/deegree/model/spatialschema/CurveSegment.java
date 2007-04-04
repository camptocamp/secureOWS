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
 * The interface defines the root of each unit building Curves
 *
 * <p>-----------------------------------------------------</p>
 *
 * @author Andreas Poth
 * @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:15 $
 * <p>
 */
public interface CurveSegment extends GenericCurve {
    
    /**
     * returns the number of points building the curve segment
     */
    int getNumberOfPoints();
    
    /**
     * returns all positions of the segement as array of Point
     */
    Position[] getPositions();
    
    /**
     * returns the curve position at the submitted index
     */
    Position getPositionAt(int index);
    
    /**
     * reverses the direction of the curvesegment
     */
    void reverse();
    
    /**
     * returns the coordinate system of the curve segment
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
     * @link aggregationByValue
     * @clientCardinality 2..*
     */    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CurveSegment.java,v $
Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
