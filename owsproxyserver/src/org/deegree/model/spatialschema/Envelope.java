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

import org.deegree.model.crs.CoordinateSystem;

/**
 * a boundingbox as child of a Polygon isn't part of the iso19107 spec but it simplifies the
 * geometry handling
 * 
 * 
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/09/18 14:11:28 $
 * 
 * @since 2.0
 */
public interface Envelope {

    /**
     * returns the spatial reference system of a geometry
     * 
     * @return the spatial reference system of a geometry
     */
    CoordinateSystem getCoordinateSystem();

    /**
     * returns the width of bounding box
     * 
     * @return the width of bounding box
     */
    double getWidth();

    /**
     * returns the height of bounding box
     * 
     * @return the height of bounding box
     */
    double getHeight();

    /**
     * returns the minimum coordinates of bounding box
     * 
     * @return the minimum coordinates of bounding box
     */
    Position getMin();

    /**
     * returns the maximum coordinates of bounding box
     * 
     * @return the maximum coordinates of bounding box
     */
    Position getMax();

    /**
     * returns true if the bounding box contains the submitted position
     * 
     * @param position
     *            the position to find
     * @return true if the bounding box contains the submitted position
     */
    boolean contains( Position position );

    /**
     * returns true if this envelope intersects the submitted envelope
     * 
     * @param bb
     *            another Envelope
     * @return true if this envelope intersects the submitted envelope
     */
    boolean intersects( Envelope bb );

    /**
     * returns true if all positions of the submitted bounding box are within this bounding box
     * 
     * @param bb
     *            another boundingbox
     * @return true if all positions of the submitted bounding box are within this bounding box
     */
    boolean contains( Envelope bb );

    /**
     * returns a new Envelope object representing the intersection of this Envelope with the
     * specified Envelope.
     * 
     * @param bb
     *            another boundingbox
     * @return a new Envelope object representing the intersection of this Envelope with the
     *         specified Envelope.
     */
    Envelope createIntersection( Envelope bb );

    /**
     * merges two Envelops and returns the minimum envelope containing both.
     * 
     * @param envelope
     *            another envelope to merge with this one
     * 
     * @return the minimum envelope containing both.
     * @throws GeometryException
     *             if the coordinatesystems are not equal
     */
    Envelope merge( Envelope envelope )
                            throws GeometryException;

    /**
     * creates a new envelope
     * 
     * @param b an extra bound around the Envelope
     * @return a new Envelope
     */
    Envelope getBuffer( double b );

    /**
     * ensures that the passed Envepole is contained within this.Envelope
     * 
     * @param other to expand this Envelope.
     */
    void expandToContain( Envelope other );

    /**
     * translate a envelope in the direction defined by the two passed values and retiurns the
     * resulting envelope
     * 
     * @param x coordinate
     * @param y coordinate
     * @return the resulting translated Envelope
     */
    Envelope translate( double x, double y );

}
/*
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: Envelope.java,v $
 * Revision 1.9  2006/09/18 14:11:28  bezema
 * added documentation
 *
 * Revision 1.8  2006/09/18 12:38:06  bezema
 * Added javadoc
 * Revision 1.7 2006/05/31 12:59:36 poth translate method added
 * 
 */