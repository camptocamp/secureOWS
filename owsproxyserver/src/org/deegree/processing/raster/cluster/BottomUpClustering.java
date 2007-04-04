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

import org.deegree.processing.raster.DataMatrix;


/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/08/16 21:01:47 $
 *
 * @since 2.0
 */
public class BottomUpClustering {

    private DataMatrix data = null;

    private ClusterComparator comparator = null;

    private Cluster cluster = null;

    private int count = 0;

    /**
     * 
     * @param data
     */
    public BottomUpClustering( DataMatrix data, ClusterComparator comparator ) {
        this.data = data;
        setComparator( comparator );
        cluster = new Cluster( data.getWidth(), data.getHeight() );
    }

    /**
     * performs the clustering
     * @return
     * @throws ClusterException 
     */
    public Cluster perform()
                            throws ClusterException {

        for ( int i = 0; i < data.getWidth(); i++ ) {
            for ( int j = 0; j < data.getHeight(); j++ ) {
                if ( !cluster.isClustered( i, j ) ) {
                    ClusterNode node = new SimpleClusterNode( count++ );
                    node.addCell( i, j, data.getCellAt( i, j ) );
                    cluster.setClusterNode( i, j, node );
                    clusterLoop( i, j, node );             
                }
            }
        }        

        return cluster;
    }

    
    /**
     * 
     * @param cell
     * @param node
     * @return
     * @throws ClusterException
     */
    private boolean checkPixel( int i, int j, double[] cell, ClusterNode node )
                            throws ClusterException {
        return !cluster.isClustered( i, j ) && comparator.evaluate( node, cell );
    }

    /**
     * 
     * @param ii
     * @param jj
     * @param old
     * @param node
     * @throws ClusterException
     */
    private void clusterLoop( int ii, int jj, ClusterNode node )
                            throws ClusterException {
        int fillL, fillR;
        boolean in_line = true;

        // find left side, filling along the way
        fillL = fillR = ii;
        while ( in_line ) {
            cluster.setClusterNode( fillL, jj, node );
            fillL--;
            in_line = ( fillL < 0 ) ? false : 
                checkPixel( fillL, jj , data.getCellAt( fillL, jj ), node );
        }
        fillL++;

        // find right side, filling along the way
        in_line = true;
        while ( in_line ) {
            cluster.setClusterNode( fillR, jj, node );
            fillR++;
            if ( fillR < data.getWidth() - 1 ) {
                double[] cell = data.getCellAt( fillR, jj );
                in_line = checkPixel( fillR, jj, cell, node );
            } else {
                in_line = false;
            }
        }
        fillR--;

        // look up and down
        for ( int i = fillL; i <= fillR; i++ ) {
            if ( jj > 0 && checkPixel( i, jj - 1, data.getCellAt( i, jj - 1 ), node ) ) {
                clusterLoop( i, jj - 1, node );
            }
            if ( jj < data.getHeight() - 1 && 
                 checkPixel( i, jj + 1, data.getCellAt( i, jj + 1 ), node ) ) {
                clusterLoop( i, jj + 1, node );
            }
        }

    }


    /**
     * 
     * @param comparator
     */
    public void setComparator( ClusterComparator comparator ) {
        this.comparator = comparator;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: BottomUpClustering.java,v $
 Revision 1.2  2006/08/16 21:01:47  poth
 refactoring - DataMatrix and extending classes moved from org.deegree.processing.raster.cluster to org.deegree.processing.raster

 Revision 1.1  2006/07/20 06:42:08  poth
 *** empty log message ***


 ********************************************************************** */