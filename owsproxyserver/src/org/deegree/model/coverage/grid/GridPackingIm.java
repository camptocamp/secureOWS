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

import org.opengis.coverage.grid.ByteInValuePacking;
import org.opengis.coverage.grid.GridPacking;
import org.opengis.coverage.grid.ValueInBytePacking;


/**
 * Describes the packing of data values within grid coverages.
 * It includes the packing scheme of data values with less then 8 bits per value
 * within a byte, byte packing (Little Endian / Big Endian) for values with more
 * than 8 bits and the packing of the values within the dimensions.
 *
 * @version 1.00
 * @since   1.00
 */
class GridPackingIm implements GridPacking, Serializable {
    
    private static final long serialVersionUID = -651021980138768415L;

    /** Gives the ordinate index for the band.
     * This index indicates how to form a band-specific coordinate from a grid coordinate
     * and a sample dimension number. This indicates the order in which the grid values
     * are stored in streamed data. This packing order is used when grid values are
     * retrieved using the <code>getPackedDataBlock</code> or set using
     * <code>setPackedDataBlock</code> operations on {@link org.opengis.coverage.grid.GridCoverage}.
     *
     *  bandPacking of
     *  <UL>
     *    <li>0 : the full band-specific coordinate is (b, n1, n2...)</li>
     *    <li>1 : the full band-specific coordinate is (n1, b, n2...)</li>
     *    <li>2 : the full band-specific coordinate is (n1, n2, b...)</li>
     *  </UL>
     *  Where
     *  <UL>
     *    <li>b is band</li>
     *    <li>n1 is dimension 1</li>
     *    <li>n2 is dimension 2</li>
     *  </UL>
     *  For 2 dimensional grids, band packing of 0 is referred to as band sequential,
     *  1 line interleaved and 2 pixel interleaved.
     *
     */
    public int getBandPacking() {
        return -1;
    }
    
    /** Order of bytes packed in values for sample dimensions with greater than 8 bits.
     *
     */
    public ByteInValuePacking getByteInValuePacking() {
        return null;
    }
    
    /** Order of values packed in a byte for
     * {@link org.opengis.coverage.SampleDimensionType#UNSIGNED_1BIT CV_1BIT},
     * {@link org.opengis.coverage.SampleDimensionType#UNSIGNED_2BITS CV_2BIT} and
     * {@link org.opengis.coverage.SampleDimensionType#UNSIGNED_4BITS CV_4BIT} data types.
     *
     */
    public ValueInBytePacking getValueInBytePacking() {
        return null;
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GridPackingIm.java,v $
   Revision 1.2  2006/03/15 22:20:09  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:36:03  poth
   no message

   Revision 1.2  2004/07/12 06:12:11  ap
   no message

   Revision 1.1  2004/05/25 07:14:01  ap
   no message

   Revision 1.1  2004/05/24 06:51:31  ap
   no message


********************************************************************** */