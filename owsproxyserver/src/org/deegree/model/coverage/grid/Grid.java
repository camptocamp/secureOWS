/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
University of Bonn
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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.model.coverage.grid;

import org.deegree.model.spatialschema.Envelope;

/**
 * the class encapsulates a describtion of a grid containing the grids * size (grid envelope) and the names of the grids axis. *  * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.1, $Revision: 1.5 $, $Date: 2006/07/12 14:46:19 $ *  * @since 1.1
 */

public class Grid {

    /**
     * 
     * @uml.property name="gridEnvelope"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Envelope gridEnvelope = null;

    private String[] axisNames = null;
        
    /**
     * @param gridEnvelope
     * @param axisNames
     */
    public Grid(Envelope gridEnvelope, String[] axisNames) {
        super();
        this.gridEnvelope = gridEnvelope;
        this.axisNames = axisNames;
    }

    /**
     * returns the names of the axis of the grid. A grid must have
     * at least two dimension (axis). The number of axis is identical 
     * to the dimension of the positions of the grid envelope
     * 
     * @return
     * 
     * @uml.property name="axisNames"
     */
    public String[] getAxisNames() {
        return axisNames;
    }

    /**
     * sets the names of the grids axis. A grid must have
     * at least two dimension (axis). The number of axis must be 
     * identical to the dimension of the positions of the grid envelope
     * 
     * @param axisNames
     * 
     * @uml.property name="axisNames"
     */
    public void setAxisNames(String[] axisNames) {
        this.axisNames = axisNames;
    }

    /**
     * returns the envelope of the grid
     * 
     * @return
     * 
     * @uml.property name="gridEnvelope"
     */
    public Envelope getGridEnvelope() {
        return gridEnvelope;
    }

    /**
     * sets the envelope of the grid
     * 
     * @param gridEnvelope
     * 
     * @uml.property name="gridEnvelope"
     */
    public void setGridEnvelope(Envelope gridEnvelope) {
        this.gridEnvelope = gridEnvelope;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Grid.java,v $
Revision 1.5  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
