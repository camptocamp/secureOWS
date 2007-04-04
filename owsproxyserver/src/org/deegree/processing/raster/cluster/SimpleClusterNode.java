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

import java.util.HashMap;
import java.util.Map;

/**
 * simple cluster just described by the mean values of each
 * cluster dimension.
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
public class SimpleClusterNode implements ClusterNode {
    
    private int id = 0;
    private double[] mean = null;
    private float num = 0;
    private Map values = new HashMap( 100 );
    
    public SimpleClusterNode(int id) {
        this.id = id;
    }
    
    /**
     * returns the mean values of each dimension of the cluster 
     * @return
     */
    public double[] getMean() {
        return mean;
    }

    /**
     * returns the ID of the Cluster
     * @return
     */
    public int getId() {
        return id;
    }
    
    /**
     * adds a new cell to the cluster. adding a new cell forces a 
     * recalculation of the clusters mean values.
     * @param cell
     */
    public void addCell(int x, int y, double[] cell) {
        if ( mean == null ) {
            mean = new double[cell.length];
        }
        num++;
        if ( num > 1 ) {
            for ( int i = 0; i < cell.length; i++ ) {
                mean[i] = (mean[i] / ( 1 - (1 / num) ) ) + ( cell[i] / num );  
            }
        } else {
            for ( int i = 0; i < cell.length; i++ ) {
                mean[i] = cell[i];  
            }
        }
        values.put( x + "_" + y, cell );
    }

    /**
     * removes a cell from a cluster. removing a cell forces a 
     * recalculation of a clusters mean vector
     * @param cell
     */
    public void removeCell( int x, int y ) {
        double[] cell = (double[])values.remove( x + "_" + y );
        if ( cell != null ) {            
            if ( num > 1 ) {
                for ( int i = 0; i < cell.length; i++ ) {
                    mean[i] = (mean[i] / ( 1 - (1 / num) ) ) - ( cell[i] / num );  
                }
            } else {
                for ( int i = 0; i < cell.length; i++ ) {
                    mean[i] = 0;  
                }
            }
            num--;
        }
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SimpleClusterNode.java,v $
Revision 1.1  2006/07/20 06:42:08  poth
*** empty log message ***


********************************************************************** */