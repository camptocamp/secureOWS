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

// OpenGIS dependencies
import org.deegree.model.csct.pt.CoordinatePoint;
import org.deegree.model.csct.pt.Matrix;

/**
 * Transforms multi-dimensional coordinate points. This interface transforms
 * coordinate value for a point given in the source coordinate system to
 * coordinate value for the same point in the target coordinate system.
 * In an ISO conversion, the transformation is accurate to within the
 * limitations of the computer making the calculations. In an ISO
 * transformation, where some of the operational parameters are derived
 * from observations, the transformation is accurate to within the
 * limitations of those observations.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.ct.CT_MathTransform
 */
public interface MathTransform {
    /**
     * Gets the dimension of input points.
     *
     * @see org.opengis.ct.CT_MathTransform#getDimSource()
     */
    public abstract int getDimSource();

    /**
     * Gets the dimension of output points.
     *
     * @see org.opengis.ct.CT_MathTransform#getDimTarget()
     */
    public abstract int getDimTarget();

    /*
     * Gets flags classifying domain points within a convex hull.
     * Conceptually, each of the (usually infinite) points inside the convex hull is
     * tested against the source domain. The flags of all these tests are then combined.
     * In practice, implementations of different transforms will use different short-cuts
     * to avoid doing an infinite number of tests.
     * <br><br>
     * Convex hull are not yet implemented in the <code>org.deegree.model</code>
     * package. Consequently, the default implementation for this method always
     * throws a {@link UnsupportedOperationException}.
     *
     * @param  hull The convex hull.
     * @return flags classifying domain points within the convex hull.
     *
     * @see org.opengis.ct.CT_MathTransform#getDomainFlags
     */
    //  public DomainFlags getDomainFlags(final ConvexHull hull)
    //  {throw new UnsupportedOperationException("Not implemented");}
    /**
     * Transforms the specified <code>ptSrc</code> and stores the result in <code>ptDst</code>.
     * If <code>ptDst</code> is <code>null</code>, a new {@link CoordinatePoint} object is
     * allocated and then the result of the transformation is stored in this object. In either
     * case, <code>ptDst</code>, which contains the transformed point, is returned for convenience.
     * If <code>ptSrc</code> and <code>ptDst</code> are the same object, the input point is correctly
     * overwritten with the transformed point.
     *
     * @param ptSrc the specified coordinate point to be transformed.
     * @param ptDst the specified coordinate point that stores the
     *              result of transforming <code>ptSrc</code>, or
     *              <code>null</code>.
     * @return the coordinate point after transforming <code>ptSrc</code>
     *         and storing the result in <code>ptDst</code>, or a newly
     *         created point if <code>ptDst</code> was null.
     * @throws TransformException if the point can't be transformed.
     *
     */
    public abstract CoordinatePoint transform( CoordinatePoint ptSrc, CoordinatePoint ptDst )
                            throws TransformException;

    /**
     * Transforms a list of coordinate point ordinal values.
     * This method is provided for efficiently transforming many points.
     * The supplied array of ordinal values will contain packed ordinal
     * values.  For example, if the source dimension is 3, then the ordinals
     * will be packed in this order:
     *
     * (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
     *  <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var> ...).
     *
     * The size of the passed array must be an integer multiple of
     * {@link #getDimSource}.
     *
     * @param srcPts the array containing the source point coordinates.
     * @param srcOff the offset to the first point to be transformed
     *               in the source array.
     * @param dstPts the array into which the transformed point
     *               coordinates are returned. May be the same
     *               than <code>srcPts</code>.
     * @param dstOff the offset to the location of the first
     *               transformed point that is stored in the
     *               destination array.
     * @param numPts the number of point objects to be transformed.
     * @throws TransformException if a point can't be transformed.
     *
     */
    public abstract void transform( double[] srcPts, int srcOff, double[] dstPts, int dstOff,
                                   int numPts )
                            throws TransformException;

    /**
     * Transforms a list of coordinate point ordinal values.
     * This method is provided for efficiently transforming many points.
     * The supplied array of ordinal values will contain packed ordinal
     * values.  For example, if the source dimension is 3, then the ordinals
     * will be packed in this order:
     *
     * (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
     *  <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var> ...).
     *
     * The size of the passed array must be an integer multiple of
     * {@link #getDimSource}.
     *
     * @param srcPts the array containing the source point coordinates.
     * @param srcOff the offset to the first point to be transformed
     *               in the source array.
     * @param dstPts the array into which the transformed point
     *               coordinates are returned. May be the same
     *               than <code>srcPts</code>.
     * @param dstOff the offset to the location of the first
     *               transformed point that is stored in the
     *               destination array.
     * @param numPts the number of point objects to be transformed.
     * @throws TransformException if a point can't be transformed.
     */
    public abstract void transform( float[] srcPts, int srcOff, float[] dstPts, int dstOff,
                                   int numPts )
                            throws TransformException;

    /**
     * Gets the derivative of this transform at a point. The derivative is the
     * matrix of the non-translating portion of the approximate affine map at
     * the point. The matrix will have dimensions corresponding to the source
     * and target coordinate systems. If the input dimension is <var>M</var>,
     * and the output dimension is <var>N</var>, then the matrix will have size
     * <code>N&times;M</code>. The elements of the matrix <code>{e<sub>n,m</sub> : n=0..(N-1)}</code>
     * form a vector in the output space which is parallel to the displacement
     * caused by a small change in the <var>m</var>'th ordinate in the input space.
     * <br><br>
     * For example, if the input dimension is 4 and the output dimension is 3, then a small displacement
     * <code>(x<sub>0</sub>,&nbsp;x<sub>1</sub>,&nbsp;x<sub>2</sub>,&nbsp;x<sub>3</sub>)</code> in the
     * input space will result in a displacement <code>(y<sub>0</sub>,&nbsp;y<sub>1</sub>,&nbsp;y<sub>2</sub>)</code>
     * in the output space computed as below (<code>e<sub>n,m</sub></code> are the matrix's elements):
     *
     * <pre>
     * [ y<sub>0</sub> ]     [ e<sub>00</sub>  e<sub>01</sub>  e<sub>02</sub>  e<sub>03</sub> ] [ x<sub>0</sub> ]
     * [ y<sub>1</sub> ]  =  [ e<sub>10</sub>  e<sub>11</sub>  e<sub>12</sub>  e<sub>13</sub> ] [ x<sub>1</sub> ]
     * [ y<sub>2</sub> ]     [ e<sub>20</sub>  e<sub>21</sub>  e<sub>22</sub>  e<sub>23</sub> ] [ x<sub>2</sub> ]
     *    <sub> </sub>          <sub>  </sub>   <sub>  </sub>   <sub>  </sub>   <sub>  </sub>   [ x<sub>3</sub> ]
     * </pre>
     *
     * @param  point The coordinate point where to evaluate the derivative. Null value is
     *         accepted only if the derivative is the same everywhere. For example affine
     *         transform accept null value since they produces identical derivative no
     *         matter the coordinate value. But most map projection will requires a non-null
     *         value.
     * @return The derivative at the specified point (never <code>null</code>). This method
     *         never returns an internal object: changing the matrix will not change the state
     *         of this math transform.
     * @throws NullPointerException if the derivative dependents on coordinate and <code>point</code> is <code>null</code>.
     * @throws TransformException if the derivative can't be evaluated at the specified point.
     *
     */
    public abstract Matrix derivative( final CoordinatePoint point )
                            throws TransformException;

    /**
     * Creates the inverse transform of this object. The target of the inverse transform
     * is the source of the original. The source of the inverse transform is the target
     * of the original. Using the original transform followed by the inverse's transform
     * will result in an identity map on the source coordinate space, when allowances for
     * error are made. This method may fail if the transform is not one to one. However,
     * all cartographic projections should succeed.
     *
     * @return The inverse transform.
     * @throws NoninvertibleTransformException if the transform can't be inversed.
     *
     */
    public abstract MathTransform inverse()
                            throws NoninvertibleTransformException;

    /**
     * Tests whether this transform does not move any points.
     *
     * @return <code>true</code> if this <code>MathTransform</code> is
     *         an identity transform; <code>false</code> otherwise.
     *
     */
    public abstract boolean isIdentity();
}
