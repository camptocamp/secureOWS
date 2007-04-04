/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree
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
E-Mail: fitzke@giub.uni-bonn.de


 ---------------------------------------------------------------------------*/
package org.deegree.model.coverage.grid;

import java.io.Serializable;

import org.opengis.coverage.grid.GridRange;

/**
 * Specifies the range of valid coordinates for each dimension of the coverage.
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/04/06 20:25:26 $
 *
 * @since 2.0
 */
public class GridRangeIm implements GridRange, Serializable {
    
    private static final long serialVersionUID = -7292466343852424913L;
    
    private int[] up  = null;
    private int[] lo  = null;
    
    /**
     * @param lo
     * @param up
     */
    public GridRangeIm(int[]lo, int[] up) {
        this.up = up;
        this.lo = lo;
    }
    
    /** The valid maximum exclusive grid coordinate.
     * The sequence contains a maximum value for each dimension of the grid coverage.
     *
     */
    public int[] getUpper() {
        return up;
    }
    
    /** The valid minimum inclusive grid coordinate.
     * The sequence contains a minimum value for each dimension of the grid coverage.
     * The lowest valid grid coordinate is zero.
     *
     */
    public int[] getLower() {
        return lo;
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GridRangeIm.java,v $
   Revision 1.4  2006/04/06 20:25:26  poth
   *** empty log message ***

   Revision 1.3  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.2  2006/03/15 22:20:09  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:36:03  poth
   no message

   Revision 1.1  2004/05/25 07:14:01  ap
   no message

   Revision 1.1  2004/05/24 06:51:31  ap
   no message


********************************************************************** */
