/*
 * OpenGIS� Grid Coverage Implementation Specification
 *
 * This Java profile is derived from OpenGIS's specification
 * available on their public web site:
 *
 *     http://www.opengis.org/techno/implementation.htm
 *
 * You can redistribute it, but should not modify it unless
 * for greater OpenGIS compliance.
 */
package org.deegree.model.coverage.grid;

import java.io.Serializable;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridRange;

/**
 * Describes the geometry and georeferencing information of the grid coverage.
 * The grid range attribute determines the valid grid coordinates and allows
 * for calculation of grid size. A grid coverage may or may not have georeferencing.
 * 
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/05/01 20:15:27 $
 * 
 * @since 2.0
 */

class GridGeometryIm implements GridGeometry, Serializable {

    private static final long serialVersionUID = -1854324911295104602L;

    private GridRange gridRange = null;

    /**
     * 
     * @param gridRange
     */
    GridGeometryIm( GridRange gridRange ) {
        this.gridRange = gridRange;
    }

    /**
     * The valid coordinate range of a grid coverage.
     * The lowest valid grid coordinate is zero.
     * A grid with 512 cells can have a minimum coordinate of 0 and maximum of 512,
     * with 511 as the highest valid index.

     */
    public GridRange getGridRange() {
        return gridRange;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GridGeometryIm.java,v $
 Revision 1.4  2006/05/01 20:15:27  poth
 *** empty log message ***

 Revision 1.3  2006/03/15 22:20:09  poth
 *** empty log message ***

 Revision 1.2  2005/01/18 22:08:54  poth
 no message

 Revision 1.2  2004/07/12 06:12:11  ap
 no message

 Revision 1.1  2004/05/25 07:14:01  ap
 no message

 Revision 1.1  2004/05/24 06:51:31  ap
 no message


 ********************************************************************** */
