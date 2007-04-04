/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
lat/lon GmbH
http://www.lat-lon.de

It has been implemented within SEAGIS - An OpenSource implementation of OpenGIS specification
(C) 2001, Institut de Recherche pour le D�veloppement (http://sourceforge.net/projects/seagis/)
SEAGIS Contacts:  Surveillance de l'Environnement Assist�e par Satellite
                  Institut de Recherche pour le D�veloppement / US-Espace
                  mailto:seasnet@teledetection.fr


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
package org.deegree.model.csct.pt;

// OpenGIS dependencies (SEAGIS)
import java.awt.geom.AffineTransform;
import java.text.FieldPosition;
import java.text.NumberFormat;

import javax.vecmath.GMatrix;

import org.deegree.model.csct.cs.AxisOrientation;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 * A two dimensional array of numbers.
 * Row and column numbering begins with zero.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.pt.PT_Matrix
 * @see javax.vecmath.GMatrix
 * @see java.awt.geom.AffineTransform
 * @see javax.media.jai.PerspectiveTransform
 * @see javax.media.j3d.Transform3D
 * @see <A HREF="http://math.nist.gov/javanumerics/jama/">Jama matrix</A>
 * @see <A HREF="http://jcp.org/jsr/detail/83.jsp">JSR-83 Multiarray package</A>
 */
public class Matrix extends GMatrix
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3126899762163038129L;

    /**
     * Construct a square identity matrix of size
     * <code>size</code>&nbsp;&times;&nbsp;<code>size</code>.
     */
    public Matrix(final int size)
    {super(size,size);}

    /**
     * Construct a matrix of size
     * <code>numRow</code>&nbsp;&times;&nbsp;<code>numCol</code>.
     * Elements on the diagonal <var>j==i</var> are set to 1.
     */
    public Matrix(final int numRow, final int numCol)
    {super(numRow, numCol);}

    /**
     * Constructs a <code>numRow</code>&nbsp;&times;&nbsp;<code>numCol</code> matrix
     * initialized to the values in the <code>matrix</code> array. The array values
     * are copied in one row at a time in row major fashion. The array should be
     * exactly <code>numRow*numCol</code> in length. Note that because row and column
     * numbering begins with zero, <code>row</code> and <code>numCol</code> will be
     * one larger than the maximum possible matrix index values.
     */
    public Matrix(final int numRow, final int numCol, final double[] matrix)
    {
        super(numRow, numCol, matrix);
        if (numRow*numCol != matrix.length)
        {
            throw new IllegalArgumentException(String.valueOf(matrix.length));
        }
    }

    /**
     * Constructs a new matrix from a two-dimensional array of doubles.
     *
     * @param  matrix Array of rows. Each row must have the same length.
     * @throws IllegalArgumentException if the specified matrix is not regular
     *         (i.e. if all rows doesn't have the same length).
     */
    public Matrix(final double[][] matrix) throws IllegalArgumentException
    {
        super(matrix.length, (matrix.length!=0) ? matrix[0].length : 0);
        final int numRow = getNumRow();
        final int numCol = getNumCol();
        for (int j=0; j<numRow; j++)
        {
            if (matrix[j].length!=numCol)
            {
                throw new IllegalArgumentException(Resources.format(ResourceKeys.ERROR_MATRIX_NOT_REGULAR));
            }
            setRow(j, matrix[j]);
        }
    }

    /**
     * Constructs a new matrix and copies the initial
     * values from the parameter matrix.
     */
    public Matrix(final GMatrix matrix)
    {super(matrix);}

    /**
     * Construct a 3&times;3 matrix from
     * the specified affine transform.
     */
    public Matrix(final AffineTransform transform)
    {
        super(3,3, new double[]
        {
            transform.getScaleX(), transform.getShearX(), transform.getTranslateX(),
            transform.getShearY(), transform.getScaleY(), transform.getTranslateY(),
                                0,                     0,                         1
        });
    }

    /**
     * Construct an affine transform mapping a source region to a destination
     * region. The regions must have the same number of dimensions, but their
     * axis order and axis orientation may be different.
     *
     * @param srcRegion The source region.
     * @param srcAxis   Axis orientation for each dimension of the source region.
     * @param dstRegion The destination region.
     * @param dstAxis   Axis orientation for each dimension of the destination region.
     * @param validRegions   <code>true</code> if source and destination regions must
     *        be taken in account. If <code>false</code>, then source and destination
     *        regions will be ignored and may be null.
     */
    private Matrix(final Envelope srcRegion, final AxisOrientation[] srcAxis,
                   final Envelope dstRegion, final AxisOrientation[] dstAxis,
                   final boolean validRegions)
    {
        this(srcAxis.length+1);
        /*
         * Arguments check. NOTE: those exceptions are catched
         * by 'org.deegree.model.csct.ct.CoordinateTransformationFactory'.
         * If exception type change, update the factory class.
         */
        final int dimension = srcAxis.length;
        if (dstAxis.length != dimension)
        {
            throw new MismatchedDimensionException(dimension, dstAxis.length);
        }
        if (validRegions)
        {
            srcRegion.ensureDimensionMatch(dimension);
            dstRegion.ensureDimensionMatch(dimension);
        }
        /*
         * Map source axis to destination axis.  If no axis is moved (for example if the user
         * want to transform (NORTH,EAST) to (SOUTH,EAST)), then source and destination index
         * will be equal.   If some axis are moved (for example if the user want to transform
         * (NORTH,EAST) to (EAST,NORTH)),  then ordinates at index <code>srcIndex</code> will
         * have to be moved at index <code>dstIndex</code>.
         */
        setZero();
        for (int srcIndex=0; srcIndex<dimension; srcIndex++)
        {
            boolean hasFound = false;
            final AxisOrientation srcAxe = srcAxis[srcIndex];
            final AxisOrientation search = srcAxe.absolute();
            for (int dstIndex=0; dstIndex<dimension; dstIndex++)
            {
                final AxisOrientation dstAxe = dstAxis[dstIndex];
                if (search.equals(dstAxe.absolute()))
                {
                    if (hasFound)
                    {
                        throw new IllegalArgumentException(Resources.format(ResourceKeys.ERROR_COLINEAR_AXIS_$2,
                                                           srcAxe.getName(null), dstAxe.getName(null)));
                    }
                    hasFound = true;
                    /*
                     * Set the matrix elements. Some matrix elements will never
                     * be set. They will be left to zero, which is their wanted
                     * value.
                     */
                    final boolean normal = srcAxe.equals(dstAxe);
                    double scale     = (normal) ? +1 : -1;
                    double translate = 0;
                    if (validRegions)
                    {
                        translate  = (normal) ? dstRegion.getMinimum(dstIndex) : dstRegion.getMaximum(dstIndex);
                        scale     *= dstRegion.getLength (dstIndex) / srcRegion.getLength (srcIndex);
                        translate -= srcRegion.getMinimum(srcIndex)*scale;
                    }
                    setElement(dstIndex, srcIndex,  scale);
                    setElement(dstIndex, dimension, translate);
                }
            }
            if (!hasFound)
            {
                throw new IllegalArgumentException(Resources.format(ResourceKeys.ERROR_NO_DESTINATION_AXIS_$1,
                                                                    srcAxis[srcIndex].getName(null)));
            }
        }
        setElement(dimension, dimension, 1);

    }

    /**
     * Construct an affine transform changing axis order and/or orientation.
     * For example, the affine transform may convert (NORTH,WEST) coordinates
     * into (EAST,NORTH). Axis orientation can be inversed only. For example,
     * it is illegal to transform (NORTH,WEST) coordinates into (NORTH,DOWN).
     *
     * @param  srcAxis The set of axis orientation for source coordinate system.
     * @param  dstAxis The set of axis orientation for destination coordinate system.
     * @throws MismatchedDimensionException if <code>srcAxis</code> and <code>dstAxis</code> don't have the same length.
     * @throws IllegalArgumentException if the affine transform can't be created for some other raison.
     */
    public static Matrix createAffineTransform(final AxisOrientation[] srcAxis, final AxisOrientation[] dstAxis)
    {return new Matrix(null, srcAxis, null, dstAxis, false);}

    /**
     * Construct an affine transform that maps
     * a source region to a destination region.
     * Axis order and orientation are left unchanged.
     *
     * @param  srcRegion The source region.
     * @param  dstRegion The destination region.
     * @throws MismatchedDimensionException if regions don't have the same dimension.
     */
    public static Matrix createAffineTransform(final Envelope srcRegion, final Envelope dstRegion)
    {
        final int dimension = srcRegion.getDimension();
        dstRegion.ensureDimensionMatch(dimension);
        final Matrix matrix = new Matrix(dimension+1);
        for (int i=0; i<dimension; i++)
        {
            final double scale     = dstRegion.getLength (i) / srcRegion.getLength (i);
            final double translate = dstRegion.getMinimum(i) - srcRegion.getMinimum(i)*scale;
            matrix.setElement(i, i,         scale);
            matrix.setElement(i, dimension, translate);
        }
        matrix.setElement(dimension, dimension, 1);
        return matrix;
    }

    /**
     * Construct an affine transform mapping a source region to a destination
     * region. Axis order and/or orientation can be changed during the process.
     * For example, the affine transform may convert (NORTH,WEST) coordinates
     * into (EAST,NORTH). Axis orientation can be inversed only. For example,
     * it is illegal to transform (NORTH,WEST) coordinates into (NORTH,DOWN).
     *
     * @param  srcRegion The source region.
     * @param  srcAxis   Axis orientation for each dimension of the source region.
     * @param  dstRegion The destination region.
     * @param  dstAxis   Axis orientation for each dimension of the destination region.
     * @throws MismatchedDimensionException if all arguments don't have the same dimension.
     * @throws IllegalArgumentException if the affine transform can't be created for some other raison.
     */
    public static Matrix createAffineTransform(final Envelope srcRegion, final AxisOrientation[] srcAxis,
                                               final Envelope dstRegion, final AxisOrientation[] dstAxis)
    {return new Matrix(srcRegion, srcAxis, dstRegion, dstAxis, true);}

    /**
     * Retrieves the specifiable values in the transformation matrix into a
     * 2-dimensional array of double precision values. The values are stored
     * into the 2-dimensional array using the row index as the first subscript
     * and the column index as the second. Values are copied; changes to the
     * returned array will not change this matrix.
     *
     * @see org.opengis.pt.PT_Matrix#elt
     */
    public final double[][] getElements()
    {
        final int numCol = getNumCol();
        final double[][] matrix = new double[getNumRow()][];
        for (int j=0; j<matrix.length; j++)
        {
            getRow(j, matrix[j]=new double[numCol]);
        }
        return matrix;
    }

    /**
     * Returns <code>true</code> if this matrix is an affine transform.
     * A transform is affine if the matrix is square and last row contains
     * only zeros, except in the last column which contains 1.
     */
    public final boolean isAffine()
    {
        int dimension  = getNumRow();
        if (dimension != getNumCol())
            return false;

        dimension--;
        for (int i=0; i<=dimension; i++)
            if (getElement(dimension, i) != (i==dimension ? 1 : 0))
                return false;
        return true;
    }

    /**
     * Returns <code>true</code> if this matrix is an identity matrix.
     */
    public final boolean isIdentity()
    {
        final int numRow = getNumRow();
        final int numCol = getNumCol();
        if (numRow != numCol) return false;

        for (int j=0; j<numRow; j++)
            for (int i=0; i<numCol; i++)
                if (getElement(j,i) != (i==j ? 1 : 0))
                    return false;
        return true;
    }

    /**
     * Returns an affine transform for this matrix.
     * This is a convenience method for interoperability with Java2D.
     *
     * @throws IllegalStateException if this matrix is not 3x3,
     *         or if the last row is not [0 0 1].
     */
    public final AffineTransform toAffineTransform2D() throws IllegalStateException
    {
        int check;
        if ((check=getNumRow())!=3 || (check=getNumCol())!=3)
        {
            throw new IllegalStateException(Resources.format(ResourceKeys.ERROR_NOT_TWO_DIMENSIONAL_$1, new Integer(check-1)));
        }
        if (isAffine())
        {
            return new AffineTransform(getElement(0,0), getElement(1,0),
                                       getElement(0,1), getElement(1,1),
                                       getElement(0,2), getElement(1,2));
        }
        throw new IllegalStateException(Resources.format(ResourceKeys.ERROR_NOT_AN_AFFINE_TRANSFORM));
    }

    /**
     * Returns a string representation of this matrix.
     * The returned string is implementation dependent.
     * It is usually provided for debugging purposes only.
     */
    public String toString()
    {
        final int    numRow = getNumRow();
        final int    numCol = getNumCol();
        StringBuffer buffer = new StringBuffer(10000);
        final int      columnWidth = 12;
        final String lineSeparator = System.getProperty("line.separator", "\n");
        final FieldPosition  dummy = new FieldPosition(0);
        final NumberFormat  format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(6);
        format.setMaximumFractionDigits(6);
        for (int j=0; j<numRow; j++)
        {
            for (int i=0; i<numCol; i++)
            {
                final int position = buffer.length();
                buffer = format.format(getElement(j,i), buffer, dummy);
                buffer.insert(position, Utilities.spaces(columnWidth-(buffer.length()-position)));
            }
            buffer.append(lineSeparator);
        }
        return buffer.toString();
    }
}
