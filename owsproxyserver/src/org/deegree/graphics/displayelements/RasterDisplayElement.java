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
package org.deegree.graphics.displayelements;

import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.pt.PT_Envelope;

/**
 * 
 * 
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/08/06 20:13:30 $
 *
 * @since 2.0
 */
public class RasterDisplayElement extends AbstractDisplayElement
                                    implements Serializable {
    
    
    private static ILogger LOG = LoggerFactory.getLogger( RasterDisplayElement.class ); 

 
    private RasterSymbolizer symbolizer = null;
    private GridCoverage gc = null;

    /**
     * Creates a new RasterDisplayElement_Impl object.
     * 
     * @param gc
     *            raster
     */
    RasterDisplayElement(GridCoverage gc) {
        setRaster(gc);
        symbolizer = new RasterSymbolizer();
    }

    /**
     * Creates a new RasterDisplayElement_Impl object.
     * 
     * @param gc
     *            raster
     * @param symbolizer
     */
    RasterDisplayElement(GridCoverage gc, RasterSymbolizer symbolizer) {
        setRaster(gc);
        this.symbolizer = symbolizer;
    }

    /**
     * renders the DisplayElement to the submitted graphic context
     *  
     */
    public void paint(Graphics g, GeoTransform projection, double scale) {
        try {
            PT_Envelope env = gc.getEnvelope();
            int minx = (int) (projection.getDestX(env.minCP.ord[0]) + 0.5);
            int maxy = (int) (projection.getDestY(env.minCP.ord[1]) + 0.5);
            int maxx = (int) (projection.getDestX(env.maxCP.ord[0]) + 0.5);
            int miny = (int) (projection.getDestY(env.maxCP.ord[1]) + 0.5);

            if (gc instanceof ImageGridCoverage) {
                Image image = ((ImageGridCoverage) gc).getAsImage(-1,-1);    
                g.drawImage(image, minx, miny, maxx - minx, maxy - miny, null);
            } else {
                //TODO
                // handle other grid coverages
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RuntimeException( e.getMessage(), e );
        }

    }

    /**
     * returns the content of the <tt>RasterDisplayElement</tt>
     * @return  
     */
    public GridCoverage getRaster() {
        return gc;
    }

    /**
     * sets the grid coverage that represents the content of the
     * <tt>RasterDisplayElement</tt>
     * 
     * @param gc
     *  
     */
    public void setRaster(GridCoverage gc) {
        this.gc = gc;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RasterDisplayElement.java,v $
Revision 1.9  2006/08/06 20:13:30  poth
logError statement added in paint method

Revision 1.8  2006/06/28 20:20:15  poth
some code clean ups

Revision 1.7  2006/05/31 12:05:57  poth
useless code removed


********************************************************************** */