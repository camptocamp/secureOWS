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
public class Cluster {
    
    private ClusterNode[][] mat = null;
    
    /**
     * initializes a Cluster with a specific width and height
     * @param width
     * @param height
     */
    public Cluster( int width, int height ) {        
        mat = new ClusterNode[height][width];
    }
    
    /**
     * sets a ClusterNode at a specific index postion
     * @param x
     * @param y
     * @param node
     */
    public void setClusterNode(int x, int y, ClusterNode node) {
        mat[y][x] = node;
    }
    
    /**
     * returns a ClusterNode from a specific index postion
     * @param x
     * @param y
     * @param node
     */
    public ClusterNode getClusterNode(int x, int y) {
        return mat[y][x];
    }

    /**
     * returns true if the cell at the defined index position
     * has already been clustered.
     * @param x
     * @param y
     * @return
     */
    public boolean isClustered(int x, int y) {
        return mat[y][x] != null;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Cluster.java,v $
Revision 1.1  2006/07/20 06:42:08  poth
*** empty log message ***


********************************************************************** */