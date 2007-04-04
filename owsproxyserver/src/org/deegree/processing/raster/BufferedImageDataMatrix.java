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
package org.deegree.processing.raster;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/**
 * 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/08/16 21:01:47 $
 *
 * @since 2.0
 */
public class BufferedImageDataMatrix implements DataMatrix {
    
    private int width = 0;
    private int height = 0;
    private DataBuffer data = null;
    
    /**
     * 
     * @param bi
     */
    public BufferedImageDataMatrix( BufferedImage bi ) {
        data = bi.getData().getDataBuffer();
        height = bi.getHeight();
        width = bi.getWidth();
    }

    /**
     * @param x
     * @param y
     * @return 
     */
    public double[] getCellAt( int x, int y ) {
        int index = (width * y) + x;
        double[] val = new double[ data.getNumBanks() ];
        for ( int i = 0; i < val.length; i++ ) {
            val[i] = data.getElemFloat( index, i );
        }
        return null;
    }

    /**
     * returns the data matrix width (number of cells in x-direction)
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * returns the data matrix height (number of cells in y-direction)
     * @return
     */
    public int getWidth() {
        return width;
    }

    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: BufferedImageDataMatrix.java,v $
Revision 1.1  2006/08/16 21:01:47  poth
refactoring - DataMatrix and extending classes moved from org.deegree.processing.raster.cluster to org.deegree.processing.raster

Revision 1.1  2006/07/20 06:42:08  poth
*** empty log message ***


********************************************************************** */