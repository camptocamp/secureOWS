// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/AbstractGridCoverage.java,v 1.11 2006/12/03 21:19:53 poth Exp $
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
package org.deegree.model.coverage.grid;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.model.coverage.AbstractCoverage;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridPacking;

/**
 * Represent the basic implementation which provides access to grid coverage data.
 * A <code>GC_GridCoverage</code> implementation may provide the ability to update
 * grid values.
 * 
 * @author  <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version 2.11.2002
 */

public abstract class AbstractGridCoverage extends AbstractCoverage implements GridCoverage {

    private GridGeometry gridGeometry = null;

    private GridPacking gridPacking = null;

    private GridGeometry[] overviewGridGeometry = null;

    private GridCoverage[] overviews = null;

    private boolean isEditable = false;

    /**
     * @param coverageOffering
     * @param envelope
     */
    public AbstractGridCoverage( CoverageOffering coverageOffering, Envelope envelope ) {
        super( coverageOffering, envelope );
    }

    /**
     * @param coverageOffering
     * @param sources
     * @param envelope
     */
    public AbstractGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                                Coverage[] sources ) {
        super( coverageOffering, envelope, sources );
    }

    /**
     * 
     * @param coverageOffering
     * @param envelope
     * @param isEditable
     */
    public AbstractGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                                boolean isEditable ) {
        super( coverageOffering, envelope );
        this.isEditable = isEditable;
    }
    
    /**
     * 
     * @param coverageOffering
     * @param envelope
     * @param isEditable
     */
    public AbstractGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                                 CoordinateSystem crs, boolean isEditable ) {
        super( coverageOffering, envelope, null, crs );
        this.isEditable = isEditable;
    }

    /**
     * 
     * @param coverageOffering
     * @param envelope
     * @param sources
     * @param isEditable
     */
    public AbstractGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                                Coverage[] sources, boolean isEditable ) {
        super( coverageOffering, envelope, sources );
        this.isEditable = isEditable;
    }
    
    /**
     * 
     * @param coverageOffering
     * @param envelope
     * @param sources
     * @param crs
     * @param isEditable
     */
    public AbstractGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                                Coverage[] sources, CoordinateSystem crs, boolean isEditable ) {
        super( coverageOffering, envelope, sources, crs );
        this.isEditable = isEditable;
    }

    /**
     * Returns <code>true</code> if grid data can be edited.
     *
     * @return <code>true</code> if grid data can be edited.
     */
    public boolean isDataEditable() {
        return isEditable;
    }

    /** Return the grid geometry for an overview.
     *
     * @param overviewIndex Overview index for which to retrieve grid geometry. Indices start at 0.
     * @return the grid geometry for an overview.
     *
     */
    public GridGeometry getOverviewGridGeometry( int overviewIndex )
                            throws IndexOutOfBoundsException {
        return overviewGridGeometry[overviewIndex];
    }

    /** Returns a pre-calculated overview for a grid coverage.
     * The overview indices are numbered from 0 to <code>numberOverviews-1</code>.
     * The overviews are ordered from highest (index 0)
     * to lowest (numberOverviews -1) resolution.
     * Overview grid coverages will have overviews which are the overviews for
     * the grid coverage with lower resolution than the overview.
     * For example, a 1 meter grid coverage with 3, 9, and 27 meter overviews
     * will be ordered as follows:
     *
     * <table border=0 align="center">
     *   <tr> <td align="center">Index</td> <td align="center">resolution</td> </tr>
     *   <tr> <td align="center">  0  </td> <td align="center">   3      </td> </tr>
     *   <tr> <td align="center">  1  </td> <td align="center">   9      </td> </tr>
     *   <tr> <td align="center">  2  </td> <td align="center">   27     </td> </tr>
     * </table><br><br>
     *
     * The 3 meter overview will have 2 overviews as follows:
     * <table border=0 align="center">
     *   <tr> <td align="center">Index</td> <td align="center">resolution</td> </tr>
     *   <tr> <td align="center">  0  </td> <td align="center">   9      </td> </tr>
     *   <tr> <td align="center">  1  </td> <td align="center">   27     </td> </tr>
     * </table>
     *
     * @param overviewIndex Index of grid coverage overview to retrieve. Indexes start at 0.
     * @return a pre-calculated overview for a grid coverage.
     *
     */
    public GridCoverage getOverview( int overviewIndex ) {
        return overviews[overviewIndex];
    }

    /** Number of predetermined overviews for the grid.
     *
     * @return the number of predetermined overviews for the grid.
     *
     */
    public int getNumOverviews() {
        if ( overviews != null ) {
            return overviews.length;
        }
        return 0;
    }

    /**
     * Information for the packing of grid coverage values.
     * 
     * @return the information for the packing of grid coverage values.
     * 
     * @uml.property name="gridPacking"
     */
    public GridPacking getGridPacking() {
        return gridPacking;
    }

    /**
     * Information for the grid coverage geometry.
     * Grid geometry includes the valid range of grid coordinates and the georeferencing.
     * 
     * @return the information for the grid coverage geometry.
     * 
     * @uml.property name="gridGeometry"
     */
    public GridGeometry getGridGeometry() {
        return gridGeometry;
    }

    /**
     * this is a deegree convenience method which returns the source image
     * of an <tt>ImageGridCoverage</tt>. In procipal the same can be done 
     * with the getRenderableImage(int xAxis, int yAxis) method. but creating
     * a  <tt>RenderableImage</tt> image is very slow.
     * @param  xAxis Dimension to use for the <var>x</var> axis.
     * @param  yAxis Dimension to use for the <var>y</var> axis.
     * @return
     */
    abstract public BufferedImage getAsImage( int xAxis, int yAxis );

    /**
     * renders a source image onto the correct position of a target image
     * according to threir geographic extends (Envelopes). 
     * @param targetImg
     * @param targetEnv
     * @param sourceImg
     * @param sourceEnv
     * @return targetImg with sourceImg rendered on
     */
    protected BufferedImage paintImage( BufferedImage targetImg, Envelope targetEnv,
                                       BufferedImage sourceImg, Envelope sourceEnv ) {

        int w = targetImg.getWidth();
        int h = targetImg.getHeight();

        GeoTransform gt = new WorldToScreenTransform( targetEnv.getMin().getX(),
                                                      targetEnv.getMin().getY(),
                                                      targetEnv.getMax().getX(),
                                                      targetEnv.getMax().getY(), 0, 0, w, h );
        int x1 = (int) Math.round( gt.getDestX( sourceEnv.getMin().getX() ) );
        int y1 = (int) Math.round( gt.getDestY( sourceEnv.getMax().getY() ) );
        int x2 = (int) Math.round( gt.getDestX( sourceEnv.getMax().getX() ) );
        int y2 = (int) Math.round( gt.getDestY( sourceEnv.getMin().getY() ) );

        if ( Math.abs( x2 - x1 ) > 0 && Math.abs( y2 - y1 ) > 0 ) {
            
            float ww = sourceImg.getWidth();
            float hh = sourceImg.getHeight();
            
            if ( Math.abs( y2 - y1 ) / hh != 1.0 || Math.abs( x2 - x1 ) / ww != 1.0 ) {
                ParameterBlock pb = new ParameterBlock();
                pb.addSource( sourceImg );
                pb.add( Math.abs( x2 - x1 ) / ww ); // The xScale
                pb.add( Math.abs( y2 - y1 ) / hh ); // The yScale
                pb.add( 0.0F ); // The x translation
                pb.add( 0.0F ); // The y translation
                pb.add( new InterpolationNearest() ); // The interpolation
                // Create the scale operation
                RenderedOp ro = JAI.create( "scale", pb, null );
                sourceImg = ro.getAsBufferedImage();
            }

            for ( int i = 0; i < sourceImg.getWidth(); i++ ) {
                for ( int j = 0; j < sourceImg.getHeight(); j++ ) {
                    if ( x1 + i < targetImg.getWidth() && y1 + j < targetImg.getHeight() ) {
                        targetImg.setRGB( x1 + i, y1 + j, sourceImg.getRGB( i, j ) );
                    }
                }
            }
        }

        return targetImg;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: AbstractGridCoverage.java,v $
 Revision 1.11  2006/12/03 21:19:53  poth
 new constructor added

 Revision 1.10  2006/11/23 21:40:21  poth
 Bug fix - JAI does not like scaling with factor 1.0

 Revision 1.9  2006/11/21 17:30:45  poth
 bug fix / code formatting

 Revision 1.8  2006/11/17 08:58:01  poth
 bug fixes - setting correct interpolation

 Revision 1.7  2006/11/16 21:02:37  poth
 bug fixes - rescaling images

 Revision 1.6  2006/04/06 20:25:26  poth
 *** empty log message ***

 Revision 1.5  2006/04/04 20:39:44  poth
 *** empty log message ***

 Revision 1.4  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.3  2005/03/16 11:54:25  poth
 no message

 Revision 1.2  2005/01/18 22:08:54  poth
 no message

 Revision 1.5  2004/07/19 06:20:00  ap
 no message

 Revision 1.4  2004/07/14 15:34:37  ap
 no message

 Revision 1.3  2004/07/12 06:12:11  ap
 no message

 Revision 1.2  2004/06/28 06:39:45  ap
 no message

 Revision 1.1  2004/05/25 07:14:01  ap
 no message

 Revision 1.1  2004/05/24 06:51:31  ap
 no message


 ********************************************************************** */