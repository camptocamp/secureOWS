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


/**
 * A sequence of decimals numbers which when written on a width are a sequence of
 * coordinate positions. The width is derived from the CRS or coordinate dimension
 * of the container.
 *
 * <p>-----------------------------------------------------------------------</p>
 * @version 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 * @version $Revision: 1.13 $ $Date: 2006/09/18 14:11:49 $
 */
class PositionImpl implements Position, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -3780255674921824356L;

    private double[] point = null;

    private double accuracy = 0.000001;

    private int dimension = 3;

    /**
     * constructor. initializes a point to the coordinate 0/0
     */
    PositionImpl() {
        point = new double[] { 0, 0, 0 };
    }

    /**
     * constructor
     *
     * @param x x-value of the point
     * @param y y-value of the point
     */
    PositionImpl( double x, double y ) {
        point = new double[] { x, y, Double.NaN };
        dimension = 2;
    }

    /**
     * constructor
     *
     * @param x x-value of the point
     * @param y y-value of the point
     * @param z z-value of the point
     */
    PositionImpl( double x, double y, double z ) {
        point = new double[] { x, y, z };
    }

    /**
     * constructor.
     * @param coords the Coordinates from which the position is build.
     */
    PositionImpl( double[] coords ) {        
        if ( coords.length == 3 && !Double.isNaN( coords[2] ) ) {
            dimension = 3;
        } else {
            if ( coords.length == 2 ) {
                coords = new double[] { coords[0], coords[1], Double.NaN };
            }
            dimension = 2;
        }
        point = coords;
    }

    /**
     * @return the coordinate dimension of the position
     */
    public int getCoordinateDimension() {
        return dimension;
    }

    /**
     * @return a shallow copy of the geometry.
     */
    @Override
    public Object clone() {
        return new PositionImpl( point.clone() );
    }

    /**
     * @return the x-value of this point
     */
    public double getX() {
        return point[0];
    }

    /**
     * @return the y-value of this point
     */
    public double getY() {
        return point[1];
    }

    /**
     * @return the z-value of this point. 
     */
    public double getZ() {
        return point[2];
    }

    /**
     * @return the position as a array the first field contains the x- the second
     * field the y-value etc.
     * 
     * NOTE: The returned array always has a length of 3, regardless of the dimension. This is due to
     * a limitation in the coordinate transformation package (proj4), which expects coordinates to have
     * 3 dimensions.
     */
    public double[] getAsArray() {
        return point;
    }

    /**
     * translate the point by the submitted values. the <code>dz</code>-
     * value will be ignored.
     */
    public void translate( double[] d ) {
        for ( int i = 0; i < d.length; i++ ) {
            point[i] += d[i];
        }
    }

    /**
     * compares if all field of other are equal to the corresponding
     * fields of this position
     */
    @Override
    public boolean equals( Object other ) {
        boolean eq = true;
        double[] other_ = ( (Position) other ).getAsArray();

        if ( eq ) {
            for ( int i = 0; i < dimension; i++ ) {
                if ( Math.abs( point[i] - other_[i] ) > accuracy ) {
                    eq = false;
                    break;
                }
            }
        }

        return eq;
    }

    /**
     * @return the accuracy the position is defined. The accuracy is measured 
     * in values of the CRS the positions coordinates are stored
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * @see #getAccuracy()
     * @param accuracy
     */
    public void setAccuracy( double accuracy ) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        String ret = "Position: ";

        for ( int i = 0; i < dimension; i++ ) {
            ret += ( point[i] + " " );
        }

        return ret;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PositionImpl.java,v $
Revision 1.13  2006/09/18 14:11:49  bezema
fixed documentation

Revision 1.12  2006/08/28 14:22:56  poth
bug fix coordinate dimension

Revision 1.11  2006/08/28 07:50:11  bezema
Added isNan support for the 3 dimension

Revision 1.10  2006/08/17 20:09:23  poth
bug fix - dimension determination

Revision 1.9  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
