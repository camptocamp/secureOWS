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
package org.deegree.model.csct.ct;

// Geometry
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;

import javax.media.jai.ParameterList;
import javax.media.jai.PerspectiveTransform;
import javax.media.jai.util.Range;
import javax.vecmath.GMatrix;
import javax.vecmath.SingularMatrixException;

import org.deegree.model.csct.pt.CoordinatePoint;
import org.deegree.model.csct.pt.Matrix;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 * Transforms multi-dimensional coordinate points using a {@link Matrix}.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 */
final class MatrixTransform extends AbstractMathTransform implements Serializable
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -2104496465933824935L;

    /**
     * the number of rows.
     */
    private final int numRow;

    /**
     * the number of columns.
     */
    private final int numCol;

    /**
     * Elements of the matrix. Column indice vary fastest.
     */
    private final double[] elt;

    /**
     * Construct a transform.
     */
    protected MatrixTransform(final GMatrix matrix)
    {
        numRow = matrix.getNumRow();
        numCol = matrix.getNumCol();
        elt = new double[numRow*numCol];
        int index = 0;
        for (int j=0; j<numRow; j++)
            for (int i=0; i<numCol; i++)
                elt[index++] = matrix.getElement(j,i);
    }

    /**
     * Transforms an array of floating point coordinates by this matrix. Point coordinates
     * must have a dimension equals to <code>{@link Matrix#getNumCol}-1</code>. For example,
     * for square matrix of size 4&times;4, coordinate points are three-dimensional and
     * stored in the arrays starting at the specified offset (<code>srcOff</code>) in the order
     * <code>[x<sub>0</sub>, y<sub>0</sub>, z<sub>0</sub>,
     *        x<sub>1</sub>, y<sub>1</sub>, z<sub>1</sub>...,
     *        x<sub>n</sub>, y<sub>n</sub>, z<sub>n</sub>]</code>.
     *
     * The transformed points <code>(x',y',z')</code> are computed as below
     * (note that this computation is similar to {@link PerspectiveTransform}):
     *
     * <blockquote><pre>
     * [ u ]     [ m<sub>00</sub>  m<sub>01</sub>  m<sub>02</sub>  m<sub>03</sub> ] [ x ]
     * [ v ]  =  [ m<sub>10</sub>  m<sub>11</sub>  m<sub>12</sub>  m<sub>13</sub> ] [ y ]
     * [ w ]     [ m<sub>20</sub>  m<sub>21</sub>  m<sub>22</sub>  m<sub>23</sub> ] [ z ]
     * [ t ]     [ m<sub>30</sub>  m<sub>31</sub>  m<sub>32</sub>  m<sub>33</sub> ] [ 1 ]
     *
     *   x' = u/t
     *   y' = v/t
     *   y' = w/t
     * </pre></blockquote>
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is stored
     *               in the destination array. The source and destination array sections can
     *               be overlaps.
     * @param numPts The number of points to be transformed
     */
    public void transform(float[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts)
    {
        final int  inputDimension = numCol-1; // The last ordinate will be assumed equals to 1.
        final int outputDimension = numRow-1;
        final double[]     buffer = new double[numRow];
        if (srcPts==dstPts)
        {
            // We are going to write in the source array. Checks if
            // source and destination sections are going to clash.
            final int upperSrc = srcOff + numPts*inputDimension;
            if (upperSrc > dstOff)
            {
                if (inputDimension >= outputDimension ? dstOff > srcOff :
                              dstOff + numPts*outputDimension > upperSrc)
                {
                    // If source overlaps destination, then the easiest workaround is
                    // to copy source data. This is not the most efficient however...
                    srcPts = new float[numPts*inputDimension];
                    System.arraycopy(dstPts, srcOff, srcPts, 0, srcPts.length);
                    srcOff = 0;
                }
            }
        }
        while (--numPts>=0)
        {
            int mix=0;
            for (int j=0; j<numRow; j++)
            {
                double sum=elt[mix + inputDimension];
                for (int i=0; i<inputDimension; i++)
                {
                    sum += srcPts[srcOff+i]*elt[mix++];
                }
                buffer[j] = sum;
                mix++;
            }
            final double w = buffer[outputDimension];
            for (int j=0; j<outputDimension; j++)
            {
                // 'w' is equals to 1 if the transform is affine.
                dstPts[dstOff++] = (float) (buffer[j]/w);
            }
            srcOff += inputDimension;
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix. Point coordinates
     * must have a dimension equals to <code>{@link Matrix#getNumCol}-1</code>. For example,
     * for square matrix of size 4&times;4, coordinate points are three-dimensional and
     * stored in the arrays starting at the specified offset (<code>srcOff</code>) in the order
     * <code>[x<sub>0</sub>, y<sub>0</sub>, z<sub>0</sub>,
     *        x<sub>1</sub>, y<sub>1</sub>, z<sub>1</sub>...,
     *        x<sub>n</sub>, y<sub>n</sub>, z<sub>n</sub>]</code>.
     *
     * The transformed points <code>(x',y',z')</code> are computed as below
     * (note that this computation is similar to {@link PerspectiveTransform}):
     *
     * <blockquote><pre>
     * [ u ]     [ m<sub>00</sub>  m<sub>01</sub>  m<sub>02</sub>  m<sub>03</sub> ] [ x ]
     * [ v ]  =  [ m<sub>10</sub>  m<sub>11</sub>  m<sub>12</sub>  m<sub>13</sub> ] [ y ]
     * [ w ]     [ m<sub>20</sub>  m<sub>21</sub>  m<sub>22</sub>  m<sub>23</sub> ] [ z ]
     * [ t ]     [ m<sub>30</sub>  m<sub>31</sub>  m<sub>32</sub>  m<sub>33</sub> ] [ 1 ]
     *
     *   x' = u/t
     *   y' = v/t
     *   y' = w/t
     * </pre></blockquote>
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is stored
     *               in the destination array. The source and destination array sections can
     *               be overlaps.
     * @param numPts The number of points to be transformed
     */
    public void transform(double[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts)
    {
        final int  inputDimension = numCol-1; // The last ordinate will be assumed equals to 1.
        final int outputDimension = numRow-1;
        final double[]     buffer = new double[numRow];
        if (srcPts==dstPts)
        {
            // We are going to write in the source array. Checks if
            // source and destination sections are going to clash.
            final int upperSrc = srcOff + numPts*inputDimension;
            if (upperSrc > dstOff)
            {
                if (inputDimension >= outputDimension ? dstOff > srcOff :
                              dstOff + numPts*outputDimension > upperSrc)
                {
                    // If source overlaps destination, then the easiest workaround is
                    // to copy source data. This is not the most efficient however...
                    srcPts = new double[numPts*inputDimension];
                    System.arraycopy(dstPts, srcOff, srcPts, 0, srcPts.length);
                    srcOff = 0;
                }
            }
        }
        while (--numPts>=0)
        {
            int mix=0;
            for (int j=0; j<numRow; j++)
            {
                double sum=elt[mix + inputDimension];
                for (int i=0; i<inputDimension; i++)
                {
                    sum += srcPts[srcOff+i]*elt[mix++];
                }
                buffer[j] = sum;
                mix++;
            }
            final double w = buffer[outputDimension];
            for (int j=0; j<outputDimension; j++)
            {
                // 'w' is equals to 1 if the transform is affine.
                dstPts[dstOff++] = buffer[j]/w;
            }
            srcOff += inputDimension;
        }
    }

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the
     * same everywhere.
     */
    public Matrix derivative(final Point2D point)
    {return derivative((CoordinatePoint)null);}

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the
     * same everywhere.
     */
    public Matrix derivative(final CoordinatePoint point)
    {
        final Matrix matrix = getMatrix();
        matrix.setSize(numRow-1, numCol-1);
        return matrix;
    }

    /**
     * Returns a copy of the matrix.
     */
    public Matrix getMatrix()
    {return new Matrix(numRow, numCol, elt);}

    /**
     * Gets the dimension of input points.
     */
    public int getDimSource()
    {return numCol-1;}

    /**
     * Gets the dimension of output points.
     */
    public int getDimTarget()
    {return numRow-1;}

    /**
     * Tests whether this transform does not move any points.
     */
    public boolean isIdentity()
    {
        if (numRow != numCol)
            return false;

        int index=0;
        for (int j=0; j<numRow; j++)
            for (int i=0; i<numCol; i++)
                if (elt[index++] != (i==j ? 1 : 0))
                    return false;
        return true;
    }

    /**
     * Creates the inverse transform of this object.
     */
    public MathTransform inverse() throws NoninvertibleTransformException
    {
        if (isIdentity()) return this;
        final Matrix matrix = getMatrix();
        try
        {
            matrix.invert();
        }
        catch (SingularMatrixException exception)
        {
            NoninvertibleTransformException e = new NoninvertibleTransformException(Resources.format(ResourceKeys.ERROR_NONINVERTIBLE_TRANSFORM));
            throw e;
        }
        return new MatrixTransform(matrix);
    }

    /**
     * Returns a hash value for this transform.
     * This value need not remain consistent between
     * different implementations of the same class.
     */
    public int hashCode()
    {
        long code=2563217;
        for (int i=elt.length; --i>=0;)
        {
            code = code*37 + Double.doubleToLongBits(elt[i]);
        }
        return (int)(code >>> 32) ^ (int)code;
    }

    /**
     * Compares the specified object with
     * this math transform for equality.
     */
    public boolean equals(final Object object)
    {
        if (object==this) return true; // Slight optimization
        if (super.equals(object))
        {
            final MatrixTransform that = (MatrixTransform) object;
            return this.numRow == that.numRow &&
                   this.numCol == that.numCol &&
                   Arrays.equals(this.elt, that.elt);
        }
        return false;
    }

    /**
     * Returns the WKT for this math transform.
     */
    public String toString()
    {return toString(getMatrix());}

    /**
     * Returns the WKT for an affine transform
     * using the specified matrix.
     */
    static String toString(final Matrix matrix)
    {
        final int numRow = matrix.getNumRow();
        final int numCol = matrix.getNumCol();
        final StringBuffer buffer = paramMT("Affine");
        final StringBuffer eltBuf = new StringBuffer("elt_");
        addParameter(buffer, "Num_row", numRow);
        addParameter(buffer, "Num_col", numCol);
        for (int j=0; j<numRow; j++)
        {
            for (int i=0; i<numCol; i++)
            {
                final double value = matrix.getElement(j,i);
                if (value != (i==j ? 1 : 0))
                {
                    eltBuf.setLength(4);
                    eltBuf.append(j);
                    eltBuf.append('_');
                    eltBuf.append(i);
                    addParameter(buffer, eltBuf.toString(), value);
                }
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * The provider for {@link MatrixTransform}.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    static final class Provider extends MathTransformProvider
    {
        /**
         * Range of positives values. Range
         * goes from 1 to the maximum value.
         */
        private static final Range POSITIVE_RANGE = new Range(Integer.class, new Integer(1), new Integer(Integer.MAX_VALUE));

        /**
         * Create a provider for affine transforms of the specified
         * dimension. Created affine transforms will have a size of
         * <code>numRow&nbsp;&times;&nbsp;numCol</code>.
         *
         * @param numRow The number of matrix's rows.
         * @param numCol The number of matrix's columns.
         */
        public Provider(final int numRow, final int numCol)
        {
            super("Affine", ResourceKeys.AFFINE_TRANSFORM, null);
            putInt("Num_row", numRow, POSITIVE_RANGE); // Add integer (not double) parameter
            putInt("Num_col", numCol, POSITIVE_RANGE); // Add integer (not double) parameter
            final StringBuffer buffer=new StringBuffer("elt_");
            for (int j=0; j<=numRow; j++)
            {
                for (int i=0; i<=numCol; i++)
                {
                    buffer.setLength(4);
                    buffer.append(j);
                    buffer.append('_');
                    buffer.append(i);
                    put(buffer.toString(), (i==j) ? 1.0 : 0.0, null);
                }
            }
        }

        /**
         * Returns a transform for the specified parameters.
         *
         * @param  parameters The parameter values in standard units.
         * @return A {@link MathTransform} object of this classification.
         */
        public MathTransform create(final ParameterList parameters)
        {return staticCreate(parameters);}

        /**
         * Static version of {@link #create}, for use by
         * {@link MathTransformFactory#createParameterizedTransform}.
         */
        public static MathTransform staticCreate(final ParameterList parameters)
        {
            final int numRow = parameters.getIntParameter("Num_row");
            final int numCol = parameters.getIntParameter("Num_col");
            final Matrix  matrix = new Matrix(numRow, numCol);
            final String[] names = parameters.getParameterListDescriptor().getParamNames();
            if (names!=null)
            {
                for (int i=0; i<names.length; i++)
                {
                    final String name = names[i];
                    if (name.regionMatches(true, 0, "elt_", 0, 4))
                    {
                        final int separator = name.lastIndexOf('_');
                        final int row = Integer.parseInt(name.substring(4, separator));
                        final int col = Integer.parseInt(name.substring(separator+1));
                        matrix.setElement(row, col, parameters.getDoubleParameter(name));
                    }
                }
            }
            if (numRow==3 && matrix.isAffine())
            {
                return new AffineTransform2D(matrix.toAffineTransform2D());
            }
            return new MatrixTransform(matrix);
        }
    }
}
