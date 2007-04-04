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

/**
 * A sequence of decimals numbers which when written on a width are a sequence of coordinate
 * positions. The width is derived from the CRS or coordinate dimension of the container.
 * 
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 * 
 * @version 5.6.2001
 * @author Andreas Poth
 *         <p>
 */
public interface Position {
    /**
     * @return the x-value of the point
     */
    public double getX();

    /**
     * @return the y-value of the point
     */
    public double getY();

    /**
     * @return the z-value of the point
     */
    public double getZ();

    /**
     * @return the coordinate dimension of the position
     */
    public int getCoordinateDimension();

    /**
     * NOTE: The returned array always has a length of 3, regardless of the dimension. This is due
     * to a limitation in the coordinate transformation package (proj4), which expects coordinates
     * to have 3 dimensions.
     * 
     * @return the x- and y-value of the point as a two dimensional array the first field contains
     *         the x- the second field the y-value.
     * 
     */
    public double[] getAsArray();

    /**
     * translates the coordinates of the position.
     * 
     * @param d
     *            the first coordinate of the position will be translated by the first field of
     *            <tt>d</tt> the second coordinate by the second field of <tt>d</tt> and so
     *            on...
     */
    public void translate( double[] d );

    /**
     * returns the accuracy the position is defined. The accuracy is measured in values of the CRS
     * the positions coordinates are stored
     * 
     * @return
     */
    public double getAccuracy();

    /**
     * @see #getAccuracy()
     * @param accuracy
     */
    public void setAccuracy( double accuracy );
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Position.java,v $
Revision 1.9  2006/08/28 07:50:11  bezema
Added isNan support for the 3 dimension

Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
