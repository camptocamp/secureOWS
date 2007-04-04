//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/ShortGridCoverage.java,v 1.9 2006/11/13 16:05:37 poth Exp $
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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.model.coverage.grid;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.GridNotEditableException;
import org.opengis.coverage.grid.GridRange;
import org.opengis.coverage.grid.InvalidRangeException;
import org.opengis.pt.PT_Envelope;

/**
 * GridCoverage implementation for holding grids stored in a raw byte
 * matrix (byte[][]) or in a set of <tt>ByteGridCoverage</tt>s
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/11/13 16:05:37 $
 *
 * @since 2.0
 */
public class ShortGridCoverage extends AbstractGridCoverage {

    private static final long serialVersionUID = -2073045348804541362L;

    private short[][][] data = null;

    /**
     * @param coverageOffering
     */
    public ShortGridCoverage( CoverageOffering coverageOffering, Envelope envelope, short[][][] data ) {
        this( coverageOffering, envelope, false, data );
    }

    /**
     * @param coverageOffering
     * @param isEditable
     */
    public ShortGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                             boolean isEditable, short[][][] data ) {
        super( coverageOffering, envelope, isEditable );
        this.data = data;
    }

    /**
     * @param coverageOffering
     * @param sources
     */
    public ShortGridCoverage( CoverageOffering coverageOffering, Envelope envelope,
                             ShortGridCoverage[] sources ) {
        super( coverageOffering, envelope, sources );
    }

    /**
     * The number of sample dimensions in the coverage.
     * For grid coverages, a sample dimension is a band.
     *
     * @return The number of sample dimensions in the coverage.
     * @UML mandatory numSampleDimensions
     */
    public int getNumSampleDimensions() {
        if ( data != null ) {
            return data.length;
        }
        return sources[0].getNumSampleDimensions();
    }

    /**
     * Return a sequence of boolean values for a block.
     * A value for each sample dimension will be returned.
     * The semantic is the same as {@link #getDataBlock(GridRange, double[])}
     * except for the return type.
     *
     * @param  gridRange Grid range for block of data to be accessed.
     * @param  destination An optionally preallocated array in which to store the values,
     *         or <code>null</code> if none.
     * @return A sequence of boolean values for a given block in the coverage.
     *         If <code>destination</code> was non-null, then it is returned.
     *         Otherwise, a new array is allocated and returned.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws ArrayIndexOutOfBoundsException if the <code>destination</code> array is not null
     *         and too small to hold the output.
     * @UML operation getDataBlockAsBoolean
     *
     * @see #setDataBlock(GridRange, boolean[])
     */
    public boolean[] getDataBlock( GridRange gridRange, boolean[] destination )
                            throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        return null;
    }

    /**
     * Return a sequence of double values for a block.
     * A value for each sample dimension will be returned.
     *
     * The return value is an <VAR>N</VAR>+1 dimensional safe-array, with dimensions
     * (sample dimension, dimension <var>n</var>, dimension <var>n</var>-1, ... dimension 1).
     *
     * For 2 dimensional grid coverages, this safe array will be accessed as
     * (sample dimension, column, row).
     *
     * The index values will be based from 0. The indices in the returned <VAR>N</VAR> dimensional
     * safe array will need to be offset by <code>gridRange</code> {@linkplain GridRange#getLower()
     * minimum coordinates} to get equivalent grid coordinates.
     * <br><br>
     * The requested grid range must satisfy the following rules for each dimension of the grid
     * coverage:
     * <center>
     * minimum grid coordinate <= {@linkplain GridRange#getLower() grid range mimimun} <=
     * {@linkplain GridRange#getUpper() grid range maximum} <= maximum grid coordinate
     * </center>
     *
     * The number of values returned will equal:
     * <center>
     * (max<sub>1</sub> � min<sub>1</sub> + 1) *
     * (max<sub>2</sub> � min<sub>2</sub> + 1) ... *
     * (max<sub>n</sub> � min<sub>n</sub> + 1) *
     * </center>
     *
     * Where <var>min</var> is the minimum ordinate in the grid range,
     * <var>max</var> is the maximum ordinate in the grid range and
     * <VAR>N</VAR> is the number of dimensions in the grid coverage.
     *
     * @param  gridRange Grid range for block of data to be accessed.
     * @param  destination An optionally preallocated array in which to store the values,
     *         or <code>null</code> if none.
     * @return A sequence of double values for a given block in the coverage.
     *         If <code>destination</code> was non-null, then it is returned.
     *         Otherwise, a new array is allocated and returned.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws ArrayIndexOutOfBoundsException if the <code>destination</code> array is not null
     *         and too small to hold the output.
     * @UML operation getValueBlockAsDouble
     *
     * @rename Renamed <code>getValueBlockAsDouble</code> as <code>getDataBlockAsDouble</code>
     *         for consistency with all others <code>getDataBlock...</code> methods and
     *         <code>setDataBlockAsDouble</code>.
     *
     * @revisit Which indices vary fastest?
     *
     * @see #setDataBlock(GridRange, double[])
     * @see javax.media.jai.UnpackedImageData#getDoubleData()
     */
    public double[] getDataBlock( GridRange gridRange, double[] destination )
                            throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        short[] data = null;
        data = getDataBlock( gridRange, data );
        double[] sdata = new double[data.length];
        for ( int i = 0; i < sdata.length; i++ ) {
            sdata[i] = data[i];
        }
        return sdata;
    }

    /** Return a block of grid coverage data for all sample dimensions.
     * A value for each sample dimension will be returned.
     * This operation provides efficient access of the grid values.
     * The sequencing order of the values in the sequence will follow the rules
     * given by valueInBytePacking and bandPacking defined in GridPacking.
     *
     * The requested grid range must satisfy the following rules for each dimension
     * of the grid coverage:
     *
     * <blockquote><pre>
     * Min grid coordinate <= grid range minimum <= grid range maximum <= maximum grid coordinate
     * </pre></blockquote>
     *
     * The sequence of bytes returned will match the data type of the dimension.
     * For example, a grid with one 16 bit unsigned (CV_16BIT_U) sample dimension will
     * return 2 bytes for every cell in the block.
     * <br><br>
     * <strong>Byte padding Rules for grid values of less than 8 bits</strong><br>
     * For 2 D grid coverages, padding is to the nearest byte for the following cases:
     *
     * <table border=0>
     * <tr> <td>For PixelInterleaved</td>
     *      <td>For grids with multiple sample dimensions, padding occurs between
     *          pixels for each change in dimension type.</td>
     * </tr>
     * <tr> <td>For LineInterleaved</td>
     *      <td>Padding occurs at the end of each row or column (depending on the
     *          valueSequence of the grid).</td>
     * </tr>
     * <tr> <td>For BandSequencial</td>
     *      <td>Padding occurs at the end of every sample dimension.</td>
     * </tr>
     * </table>
     *
     * For grid values smaller than 8 bits, their order within each byte is given by the
     * value defined in {@link org.opengis.coverage.grid.GridPacking#getValueInBytePacking 
     * valueInBytePacking}.
     * For grid values bigger than 8 bits, the order of their bytes is given by the
     * value defined in {@link org.opengis.coverage.grid.GridPacking#getByteInValuePacking 
     * byteInValuePacking}.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @return a block of grid coverage data for all sample dimensions.
     *
     */
    public byte[] getPackedDataBlock( GridRange gridRange ) {
        return null;
    }

    /** Optimal size to use for each dimension when accessing grid values.
     * These values together give the optimal block size to use when retrieving
     * grid coverage values.
     * For example, a client application can achieve better performance for a 2-D grid
     * coverage by reading blocks of 128 by 128 if the grid is tiled into blocks of
     * this size.
     * The sequence is ordered by dimension.
     * If the implementation does not have optimal sizes the sequence will be empty.
     *
     * @return the optimal size to use for each dimension when accessing grid values.
     *
     */
    public int[] getOptimalDataBlockSizes() {
        return null;
    }

    /**
     * Return a sequence of 32 bits values for a block.
     * A value for each sample dimension will be returned.
     * The semantic is the same as {@link #getDataBlock(GridRange, double[])}
     * except for the return type.
     *
     * @param  gridRange Grid range for block of data to be accessed.
     * @param  destination An optionally preallocated array in which to store the values,
     *         or <code>null</code> if none.
     * @return A sequence of 32 bits values for a given block in the coverage.
     *         If <code>destination</code> was non-null, then it is returned.
     *         Otherwise, a new array is allocated and returned.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws ArrayIndexOutOfBoundsException if the <code>destination</code> array is not null
     *         and too small to hold the output.
     * @UML operation getDataBlockAsInteger
     *
     * @see #setDataBlock(GridRange, int[])
     * @see javax.media.jai.UnpackedImageData#getIntData()
     */
    public int[] getDataBlock( GridRange gridRange, int[] destination )
                            throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        short[] data = null;
        data = getDataBlock( gridRange, data );
        if ( destination == null ) {
            destination = new int[data.length];
        }
        for ( int i = 0; i < destination.length; i++ ) {
            destination[i] = data[i];
        }
        return destination;
    }

    /**
     * Return a sequence of float values for a block.
     * A value for each sample dimension will be returned.
     * The semantic is the same as {@link #getDataBlock(GridRange, double[])}
     * except for the return type.
     *
     * @param  gridRange Grid range for block of data to be accessed.
     * @param  destination An optionally preallocated array in which to store the values,
     *         or <code>null</code> if none.
     * @return A sequence of float values for a given block in the coverage.
     *         If <code>destination</code> was non-null, then it is returned.
     *         Otherwise, a new array is allocated and returned.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws ArrayIndexOutOfBoundsException if the <code>destination</code> array is not null
     *         and too small to hold the output.
     *
     * @see #setDataBlock(GridRange, float[])
     * @see javax.media.jai.UnpackedImageData#getFloatData()
     */
    public float[] getDataBlock( GridRange gridRange, float[] destination )
                            throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        short[] data = null;
        data = getDataBlock( gridRange, data );
        if ( destination == null ) {
            destination = new float[data.length];
        }
        for ( int i = 0; i < destination.length; i++ ) {
            destination[i] = data[i];
        }
        return destination;
    }

    /**
     * Return a sequence of 8 bits values for a block.
     * A value for each sample dimension will be returned.
     * The semantic is the same as {@link #getDataBlock(GridRange, double[])}
     * except for the return type.
     *
     * @param  gridRange Grid range for block of data to be accessed.
     * @param  destination An optionally preallocated array in which to store the values,
     *         or <code>null</code> if none.
     * @return A sequence of 8 bits values for a given block in the coverage.
     *         If <code>destination</code> was non-null, then it is returned.
     *         Otherwise, a new array is allocated and returned.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws ArrayIndexOutOfBoundsException if the <code>destination</code> array is not null
     *         and too small to hold the output.
     * @UML operation getDataBlockAsByte
     *
     * @see #setDataBlock(GridRange, byte[])
     * @see javax.media.jai.UnpackedImageData#getByteData()
     */
    public byte[] getDataBlock( GridRange gridRange, byte[] destination )
                            throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        throw null;
    }

    /**
     * Return a sequence of 16 bits values for a block.
     * A value for each sample dimension will be returned.
     * The semantic is the same as {@link #getDataBlock(GridRange, double[])}
     * except for the return type.
     *
     * @param  gridRange Grid range for block of data to be accessed.
     * @param  destination An optionally preallocated array in which to store the values,
     *         or <code>null</code> if none.
     * @return A sequence of 16 bits values for a given block in the coverage.
     *         If <code>destination</code> was non-null, then it is returned.
     *         Otherwise, a new array is allocated and returned.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws ArrayIndexOutOfBoundsException if the <code>destination</code> array is not null
     *         and too small to hold the output.
     * @UML operation getDataBlockAsInteger
     *
     * @see #setDataBlock(GridRange, int[])
     * @see javax.media.jai.UnpackedImageData#getShortData()
     */
    public short[] getDataBlock( GridRange gridRange, short[] destination )
                            throws InvalidRangeException, ArrayIndexOutOfBoundsException {
        int[] hi = gridRange.getUpper();
        int[] lo = gridRange.getLower();

        if ( data != null ) {
            if ( data.length < 1 || lo[0] < 0 || lo[1] < 0 || hi[0] > data[0].length
                 || hi[1] > data.length ) {
                throw new InvalidRangeException( "upper range and lower range"
                                                 + "must be within the image size." );
            }

            if ( hi[0] <= lo[0] || hi[1] <= lo[1] ) {
                throw new InvalidRangeException( "lower range must be less than upper range" );
            }

            if ( destination == null ) {
                destination = new short[( hi[0] - lo[0] ) * ( hi[1] - lo[1] )];
            }

            int k = 0;
            // sample dimanesion
            for ( int z = 0; z < data.length; z++ ) {
                // x-direction
                for ( int i = lo[0]; i < hi[1]; i++ ) {
                    // y-direction
                    for ( int j = lo[1]; j < hi[1]; j++ ) {
                        destination[k++] = data[z][j][i];
                    }
                }
            }
        } else {
            //TODO if multi images -> sources.length > 0
        }
        return destination;
    }

    /**
     * Set a block of boolean values for all sample dimensions.
     * The semantic is the same as {@link #setDataBlock(GridRange, double[])}.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setDataBlockAsBoolean
     *
     * @see #getDataBlock(GridRange, boolean[])
     */
    public void setDataBlock( GridRange gridRange, boolean[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Set a block of 8 bits values for all sample dimensions.
     * The semantic is the same as {@link #setDataBlock(GridRange, double[])}.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setDataBlockAsByte
     *
     * @see #getDataBlock(GridRange, byte[])
     */
    public void setDataBlock( GridRange gridRange, byte[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Set a block of 16 bits values for all sample dimensions.
     * The semantic is the same as {@link #setDataBlock(GridRange, double[])}.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setDataBlockAsByte
     *
     * @see #getDataBlock(GridRange, short[])
     */
    public void setDataBlock( GridRange gridRange, short[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Set a block of 32 bits values for all sample dimensions.
     * The semantic is the same as {@link #setDataBlock(GridRange, double[])}.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setDataBlockAsInteger
     *
     * @see #getDataBlock(GridRange, int[])
     * @see java.awt.image.WritableRaster#setPixels(int,int,int,int,int[])
     */
    public void setDataBlock( GridRange gridRange, int[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Set a block of float values for all sample dimensions.
     * The semantic is the same as {@link #setDataBlock(GridRange, double[])}.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setDataBlockAsInteger
     *
     * @see #getDataBlock(GridRange, float[])
     * @see java.awt.image.WritableRaster#setPixels(int,int,int,int,float[])
     */
    public void setDataBlock( GridRange gridRange, float[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Set a block of double values for all sample dimensions.
     * The requested grid range must satisfy the following rules for each
     * dimension of the grid coverage:
     *
     * <center>
     * minimum grid coordinate <= {@linkplain GridRange#getLower() grid range mimimun} <=
     * {@linkplain GridRange#getUpper() grid range maximum} <= maximum grid coordinate
     * </center>
     *
     * The number of values must equal:
     *
     * <center>
     * (max<sub>1</sub> � min<sub>1</sub> + 1) *
     * (max<sub>2</sub> � min<sub>2</sub> + 1) ... *
     * (max<sub>n</sub> � min<sub>n</sub> + 1) *
     * </center>
     *
     * Where <var>min</var> is the minimum ordinate in the grid range,
     * <var>max</var> is the maximum ordinate in the grid range and
     * <VAR>N</VAR> is the number of dimensions in the grid coverage.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setDataBlockAsDouble
     *
     * @see #getDataBlock(GridRange, double[])
     * @see java.awt.image.WritableRaster#setPixels(int,int,int,int,double[])
     */
    public void setDataBlock( GridRange gridRange, double[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Set a block of grid coverage data for all sample dimensions.
     * See {@link #getPackedDataBlock} for details on how to pack the values.
     *
     * @param gridRange Grid range for block of data to be accessed.
     * @param values Sequence of grid values for the given region.
     * @throws InvalidRangeException if <code>gridRange</code> is out of this grid range bounds.
     * @throws GridNotEditableException if the grid coverage is not editable.
     * @throws ArrayIndexOutOfBoundsException if the <code>values</code> array is too small.
     * @UML operation setPackedDataBlock
     *
     * @revisit This operation can hardly be implemented efficiently in Java with a
     *          <code>byte[]</code> argument type, since we can't easily cast an array
     *          of <code>byte[]</code> to an array of arbitrary type.
     */
    public void setPackedDataBlock( GridRange gridRange, byte[] values )
                            throws InvalidRangeException, GridNotEditableException,
                            ArrayIndexOutOfBoundsException {

    }

    /**
     * Returns 2D view of this coverage as a renderable image.
     * This optional operation allows interoperability with
     * <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A>.
     * If this coverage is a {@link org.opengis.coverage.grid.GridCoverage} backed
     * by a {@link java.awt.image.RenderedImage}, the underlying image can be obtained
     * with:
     *
     * <code>getRenderableImage(0,1).{@linkplain RenderableImage#createDefaultRendering()
     * createDefaultRendering()}</code>
     *
     * @param  xAxis Dimension to use for the <var>x</var> axis.
     * @param  yAxis Dimension to use for the <var>y</var> axis.
     * @return A 2D view of this coverage as a renderable image.
     * @throws UnsupportedOperationException if this optional operation is not supported.
     * @throws IndexOutOfBoundsException if <code>xAxis</code> or 
     *         <code>yAxis</code> is out of bounds.
     */
    public RenderableImage getRenderableImage( int xAxis, int yAxis )
                            throws UnsupportedOperationException, IndexOutOfBoundsException {
        if ( data != null ) {

            return null;
        }
        //TODO if multi images -> sources.length > 0
        return null;
    }

    /**
     * this is a deegree convenience method which returns the source image
     * of an <tt>ImageGridCoverage</tt>. In procipal the same can be done 
     * with the getRenderableImage(int xAxis, int yAxis) method. but creating
     * a  <tt>RenderableImage</tt> image is very slow.
     * I xAxis or yAxis <= 0 then the size of the returned image will be 
     * calculated from the source images of the coverage.
     * @param  xAxis Dimension to use for the <var>x</var> axis.
     * @param  yAxis Dimension to use for the <var>y</var> axis.
     * @return
     */
    public BufferedImage getAsImage( int xAxis, int yAxis ) {

        if ( xAxis <= 0 || yAxis <= 0 ) {
            // get default size if passed target size is <= 0
            Rectangle rect = calculateOriginalSize();
            xAxis = rect.width;
            yAxis = rect.height;
        }
        BufferedImage bi = null;
        if ( data != null ) {
            
            bi = createBufferedImage( data[0][0].length, data[0].length );
            // total number of fields for one band; it is assumed that each
            // band has the same number of fiels
            int numOfFields = data[0].length * data[0][0].length;
            short[][] bb = new short[data.length][];
            for ( int z = 0; z < data.length; z++ ) {
                bb[z] = new short[numOfFields];
            }
            int c = 0;
            for ( int i = 0; i < data[0].length; i++ ) {
                for ( int j = 0; j < data[0][i].length; j++ ) {
                    for ( int z = 0; z < data.length; z++ ) {
                        bb[z][c] = data[z][i][j];
                    }
                    c++;
                }
            }
            DataBuffer db = new DataBufferShort( bb, numOfFields );
            SampleModel sm = new BandedSampleModel( DataBuffer.TYPE_USHORT, data[0][0].length,
                                                    data[0].length, data.length );
            Raster raster = Raster.createWritableRaster( sm, db, null );
            bi.setData( raster );
        } else {
            bi = createBufferedImage( xAxis, yAxis );
            // it's a complex ImageGridCoverage made up of different
            // source coverages
            for ( int i = 0; i < sources.length; i++ ) {
                PT_Envelope env = sources[i].getEnvelope();
                Envelope sourceEnv = GeometryFactory.createEnvelope( env.minCP.ord[0],
                                                                     env.minCP.ord[1],
                                                                     env.maxCP.ord[0],
                                                                     env.maxCP.ord[1], null );
                BufferedImage sourceImg = ( (AbstractGridCoverage) sources[i] ).getAsImage( -1, -1 );
                                
                env = this.getEnvelope();
                Envelope targetEnv = GeometryFactory.createEnvelope( env.minCP.ord[0],
                                                                     env.minCP.ord[1],
                                                                     env.maxCP.ord[0],
                                                                     env.maxCP.ord[1], null );
                bi = paintImage( bi, targetEnv, sourceImg, sourceEnv );
            }
        }

        return bi;
    }

    private BufferedImage createBufferedImage( int xAxis, int yAxis ) {
        BufferedImage bi = null;
        int sampleDim = getNumSampleDimensions();
        switch ( sampleDim ) {
        case 1: {
            SampleModel sm = new BandedSampleModel( DataBuffer.TYPE_USHORT, xAxis, yAxis, 1 );
            ICC_Profile prof = ICC_Profile.getInstance( ColorSpace.CS_GRAY );
            ColorSpace cs = new ICC_ColorSpace( prof );
            ComponentColorModel colorModel = new ComponentColorModel( cs, false, false,
                                                                      ColorModel.OPAQUE,
                                                                      DataBuffer.TYPE_USHORT );

            WritableRaster wraster = Raster.createWritableRaster( sm, new Point() );
            bi = new BufferedImage( colorModel, wraster, false, null );
        }

        }
        return bi;
    }

    /**
     * calculates the original size of a gridcoverage based on its resolution
     * and the envelope(s) of its source(s).
     * @return 
     */
    private Rectangle calculateOriginalSize() {
        if ( data != null ) {
            return new Rectangle( data[0][0].length, data[0].length );
        }
        BufferedImage bi = ( (ShortGridCoverage) sources[0] ).getAsImage( -1, -1 );
        PT_Envelope env = sources[0].getEnvelope();
        double dx = ( env.maxCP.ord[0] - env.minCP.ord[0] ) / bi.getWidth();
        double dy = ( env.maxCP.ord[1] - env.minCP.ord[1] ) / bi.getHeight();
        env = this.getEnvelope();
        int w = (int) Math.round( ( env.maxCP.ord[0] - env.minCP.ord[0] ) / dx );
        int h = (int) Math.round( ( env.maxCP.ord[1] - env.minCP.ord[1] ) / dy );
        return new Rectangle( w, h );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ShortGridCoverage.java,v $
 Revision 1.9  2006/11/13 16:05:37  poth
 debugging code removed

 Revision 1.8  2006/11/09 07:34:26  poth
 bug fix - remove depency to library not available in deegree

 Revision 1.7  2006/11/08 17:13:46  poth
 *** empty log message ***

 Revision 1.6  2006/11/07 21:36:41  poth
 bug fix - calculating orginal image size

 Revision 1.5  2006/11/07 20:02:51  poth
 bug fix

 Revision 1.4  2006/04/06 20:25:26  poth
 *** empty log message ***

 Revision 1.3  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.2  2006/03/15 22:20:09  poth
 *** empty log message ***

 Revision 1.1.1.1  2005/01/05 10:36:09  poth
 no message

 Revision 1.2  2004/08/24 07:31:33  ap
 no message

 Revision 1.1  2004/08/23 07:00:16  ap
 no message

 Revision 1.4  2004/08/06 06:41:51  ap
 grid coverage implementation extension

 Revision 1.3  2004/07/30 06:29:29  ap
 code optimization

 Revision 1.2  2004/07/23 07:14:45  ap
 no message

 Revision 1.1  2004/07/22 12:17:08  ap
 no message


 ********************************************************************** */