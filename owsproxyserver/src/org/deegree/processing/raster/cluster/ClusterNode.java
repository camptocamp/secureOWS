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
package org.deegree.processing.raster.cluster;

/**
 * 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/07/20 06:42:08 $
 *
 * @since 2.0
 */
public interface ClusterNode {
    
    /**
     * returns the ID of the Cluster
     * @return
     */
    int getId();
    
    /**
     * 
     * @return
     */
    double[] getMean();

    /**
     * adds a new cell to a cluster. adding a new cell may forces a 
     * recalculation of statistical behaviors of a cluster
     * @param cell
     */
    void addCell(int x, int y, double[] cell);
    
    /**
     * removes a cell from a cluster. removing a cell may forces a 
     * recalculation of statistical behaviors of a cluster
     * @param cell
     */
    void removeCell(int x, int y);
    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ClusterNode.java,v $
Revision 1.1  2006/07/20 06:42:08  poth
*** empty log message ***


********************************************************************** */