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
package org.deegree.processing.raster.interpolation;

/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/10/20 14:57:08 $
 *
 * @since 2.0
 */
public class DataTuple implements Comparable<DataTuple> {

    public double x = 0;

    public double y = 0;

    public double value = 0;

    /**
     * This may not be the best choice for epsilon.
     */
//    public static final double EPSILON = 0.00000000001;

    /**
     * Convenience constructor.
     * 
     * @param x
     * @param y
     * @param value
     */
    public DataTuple( double x, double y, double value ) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    /**
     * Empty constructor. Data is pre-set to zero.
     */
    public DataTuple() {
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo( DataTuple o ) {

//        boolean xequal = ( ( ( x - EPSILON ) < o.x ) && ( ( x + EPSILON ) > o.x ) );
//        boolean yequal = ( ( ( y - EPSILON ) < o.y ) && ( ( y + EPSILON ) > o.y ) );

        boolean xequal = ( x == o.x );
        boolean yequal = ( y == o.y );
        
        if ( xequal && yequal ) {
            return 0;
        }

        if ( x < o.x ) {
            return -1;
        }

        if ( xequal && ( y < o.y ) ) {
            return -1;
        }

        return 1;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DataTuple.java,v $
 Revision 1.2  2006/10/20 14:57:08  schmitz
 Added a memory point quadtree implementation.
 Used the quadtree for interpolation.
 Updated the text2tiff tool to use quadtree and interpolation.

 Revision 1.1  2006/10/12 15:44:57  poth
 initial checkin


 ********************************************************************** */