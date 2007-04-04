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
import java.awt.geom.AffineTransform;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.media.jai.ParameterList;

import org.deegree.model.csct.cs.Projection;
import org.deegree.model.csct.pt.Matrix;
import org.deegree.model.csct.resources.Naming;
import org.deegree.model.csct.resources.WeakHashSet;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * Creates math transforms. <code>MathTransformFactory</code> is a low level
 * factory that is used to create {@link MathTransform} objects.   Many high
 * level GIS applications will never need to use a <code>MathTransformFactory</code>
 * directly; they can use a {@link CoordinateTransformationFactory} instead.
 * However, the <code>MathTransformFactory</code> class is specified here,
 * since it can be used directly by applications that wish to transform other
 * types of coordinates (e.g. color coordinates, or image pixel coordinates).
 * <br><br>
 * A math transform is an object that actually does the work of applying
 * formulae to coordinate values.    The math transform does not know or
 * care how the coordinates relate to positions in the real world.  This
 * lack of semantics makes implementing <code>MathTransformFactory</code>
 * significantly easier than it would be otherwise.
 *
 * For example <code>MathTransformFactory</code> can create affine math
 * transforms. The affine transform applies a matrix to the coordinates
 * without knowing how what it is doing relates to the real world. So if
 * the matrix scales <var>Z</var> values by a factor of 1000, then it could
 * be converting meters into millimeters, or it could be converting kilometers
 * into meters.
 * <br><br>
 * Because math transforms have low semantic value (but high mathematical
 * value), programmers who do not have much knowledge of how GIS applications
 * use coordinate systems, or how those coordinate systems relate to the real
 * world can implement <code>MathTransformFactory</code>.
 *
 * The low semantic content of math transforms also means that they will be
 * useful in applications that have nothing to do with GIS coordinates.  For
 * example, a math transform could be used to map color coordinates between
 * different color spaces, such as converting (red, green, blue) colors into
 * (hue, light, saturation) colors.
 * <br><br>
 * Since a math transform does not know what its source and target coordinate
 * systems mean, it is not necessary or desirable for a math transform object
 * to keep information on its source and target coordinate systems.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.ct.CT_MathTransformFactory
 */
public class MathTransformFactory {
    /**
     * The default math transform factory. This factory
     * will be constructed only when first needed.
     */
    private static MathTransformFactory DEFAULT;

    /**
     * A pool of math transform. This pool is used in order to
     * returns instance of existing math transforms when possible.
     */
    static final WeakHashSet pool = new WeakHashSet();

    /**
     * List of registered math transforms.
     */
    private final MathTransformProvider[] providers;

    /**
     * Construct a factory using the specified providers.
     */
    public MathTransformFactory( final MathTransformProvider[] providers ) {
        this.providers = providers.clone();
    }

    /**
     * Returns the default math transform factory.
     */
    public static synchronized MathTransformFactory getDefault() {
        if ( DEFAULT == null ) {
            DEFAULT = new MathTransformFactory(
                                                new MathTransformProvider[] {
                                                                             new MercatorProjection.Provider(),
                                                                             new LambertConformalProjection.Provider(),
                                                                             new StereographicProjection.Provider(), // Automatic
                                                                             new StereographicProjection.Provider(
                                                                                                                   true ), // Polar
                                                                             new StereographicProjection.Provider(
                                                                                                                   false ), // Oblique
                                                                             new TransverseMercatorProjection.Provider(
                                                                                                                        false ), // Universal
                                                                             new TransverseMercatorProjection.Provider(
                                                                                                                        true ), // Modified
                                                                             new GeocentricTransform.Provider(
                                                                                                               false ), // Geographic to Geocentric
                                                                             new GeocentricTransform.Provider(
                                                                                                               true ) // Geocentric to Geographic
                                                } );
            for ( int i = DEFAULT.providers.length; --i >= 0; ) {
                final MathTransformProvider provider = DEFAULT.providers[i];
                if ( provider instanceof MapProjection.Provider ) {
                    // Register only projections.
                    Naming.PROJECTIONS.bind( provider.getClassName(),
                                             provider.getParameterListDescriptor() );
                }
            }
        }
        return DEFAULT;
    }

    /**
     * Creates an identity transform of the specified dimension.
     *
     * @param  dimension The source and target dimension.
     * @return The identity transform.
     */
    public MathTransform createIdentityTransform( final int dimension ) {
        // Affine transform has one more row/column than dimension.
        return createAffineTransform( new Matrix( dimension + 1 ) );
    }

    /**
     * Creates an affine transform from a matrix.
     *
     * @param matrix The matrix used to define the affine transform.
     * @return The affine transform.
     */
    public MathTransform2D createAffineTransform( final AffineTransform matrix ) {
        return (MathTransform2D) pool.intern( new AffineTransform2D( matrix ) );
    }

    /**
     * Creates an affine transform from a matrix.
     *
     * @param  matrix The matrix used to define the affine transform.
     * @return The affine transform.
     *
     */
    public MathTransform createAffineTransform( final Matrix matrix ) {
        /*
         * If the user is requesting a 2D transform, delegate to the
         * highly optimized java.awt.geom.AffineTransform class.
         */
        if ( matrix.getNumRow() == 3 && matrix.isAffine() ) // Affine transform are square.
        {
            return createAffineTransform( matrix.toAffineTransform2D() );
        }
        /*
         * General case (slower). May not be a real
         * affine transform. We accept it anyway...
         */
        return (MathTransform) pool.intern( new MatrixTransform( matrix ) );
    }

    /**
     * Returns the underlying matrix for the specified transform,
     * or <code>null</code> if the matrix is unavailable.
     */
    private static Matrix getMatrix( final MathTransform transform ) {
        if ( transform instanceof AffineTransform )
            return new Matrix( (AffineTransform) transform );
        if ( transform instanceof MatrixTransform )
            return ( (MatrixTransform) transform ).getMatrix();
        return null;
    }

    /**
     * Tests if one math transform is the inverse of the other. This implementation
     * can't detect every case. It just detect the case when <code>tr2</code> is an
     * instance of {@link AbstractMathTransform.Inverse}.
     */
    private static boolean areInverse( final MathTransform tr1, final MathTransform tr2 ) {
        if ( tr2 instanceof AbstractMathTransform.Inverse ) {
            return tr1.equals( ( (AbstractMathTransform.Inverse) tr2 ).inverse() );
            // TODO: we could make this test more general (just compare with tr2.inverse(),
            //       no matter if it is an instance of AbstractMathTransform.Inverse or not,
            //       and catch the exception if one is thrown). Would it be too expensive to
            //       create inconditionnaly the inverse transform?
        }
        return false;
    }

    /**
     * Creates a transform by concatenating two existing transforms.
     * A concatenated transform acts in the same way as applying two
     * transforms, one after the other. The dimension of the output
     * space of the first transform must match the dimension of the
     * input space in the second transform. If you wish to concatenate
     * more than two transforms, then you can repeatedly use this method.
     *
     * @param  tr1 The first transform to apply to points.
     * @param  tr2 The second transform to apply to points.
     * @return The concatenated transform.
     *
     */
    public MathTransform createConcatenatedTransform( MathTransform tr1, MathTransform tr2 ) {
        if ( tr1.isIdentity() )
            return tr2;
        if ( tr2.isIdentity() )
            return tr1;
        /*
         * If both transforms use matrix, then we can create
         * a single transform using the concatened matrix.
         */
        final Matrix matrix1 = getMatrix( tr1 );
        if ( matrix1 != null ) {
            final Matrix matrix2 = getMatrix( tr2 );
            if ( matrix2 != null ) {
                // May not be really affine, but work anyway...
                // This call will detect and optimize the special
                // case where an 'AffineTransform' can be used.
                matrix2.mul( matrix1 );
                return createAffineTransform( matrix2 );
            }
        }
        /*
         * If one transform is the inverse of the
         * other, returns the identity transform.
         */
        if ( areInverse( tr1, tr2 ) || areInverse( tr2, tr1 ) ) {
            return createIdentityTransform( tr1.getDimSource() );
        }
        /*
         * If one or both math transform are instance of {@link ConcatenedTransform},
         * then maybe it is possible to efficiently concatenate <code>tr1</code> or
         * <code>tr2</code> with one of step transforms. Try that...
         */
        if ( tr1 instanceof ConcatenedTransform ) {
            final ConcatenedTransform ctr = (ConcatenedTransform) tr1;
            tr1 = ctr.transform1;
            tr2 = createConcatenatedTransform( ctr.transform2, tr2 );
        }
        if ( tr2 instanceof ConcatenedTransform ) {
            final ConcatenedTransform ctr = (ConcatenedTransform) tr2;
            tr1 = createConcatenatedTransform( tr1, ctr.transform1 );
            tr2 = ctr.transform2;
        }
        /*
         * The returned transform will implements {@link MathTransform2D} if source and
         * target dimensions are equal to 2.  {@link MathTransform} implementations are
         * available in two version: direct and non-direct. The "non-direct" version use
         * an intermediate buffer when performing transformations;   they are slower and
         * consume more memory. They are used only as a fallback when a "direct" version
         * can't be created.
         */
        final MathTransform transform;
        final int dimSource = tr1.getDimSource();
        final int dimTarget = tr2.getDimTarget();
        if ( dimSource == 2 && dimTarget == 2 ) {
            if ( tr1 instanceof MathTransform2D && tr2 instanceof MathTransform2D ) {
                transform = new ConcatenedTransformDirect2D( this, (MathTransform2D) tr1,
                                                             (MathTransform2D) tr2 );
            } else
                transform = new ConcatenedTransform2D( this, tr1, tr2 );
        } else if ( dimSource == tr1.getDimTarget() && tr2.getDimSource() == dimTarget ) {
            transform = new ConcatenedTransformDirect( this, tr1, tr2 );
        } else
            transform = new ConcatenedTransform( this, tr1, tr2 );
        return (MathTransform) pool.intern( transform );
    }

    /**
     * Creates a transform which passes through a subset of ordinates to another transform.
     * This allows transforms to operate on a subset of ordinates. For example, if you have
     * (<var>latitidue</var>,<var>longitude</var>,<var>height</var>) coordinates, then you
     * may wish to convert the height values from feet to meters without affecting the
     * latitude and longitude values.
     *
     * @param  firstAffectedOrdinate Index of the first affected ordinate.
     * @param  subTransform The sub transform.
     * @param  numTrailingOrdinates Number of trailing ordinates to pass through.
     *         Affected ordinates will range from <code>firstAffectedOrdinate</code>
     *         inclusive to <code>dimTarget-numTrailingOrdinates</code> exclusive.
     * @return A pass through transform with the following dimensions:<br>
     *         <pre>
     * Source: firstAffectedOrdinate + subTransform.getDimSource() + numTrailingOrdinates
     * Target: firstAffectedOrdinate + subTransform.getDimTarget() + numTrailingOrdinates</pre>
     *
     */
    public MathTransform createPassThroughTransform( final int firstAffectedOrdinate,
                                                    final MathTransform subTransform,
                                                    final int numTrailingOrdinates ) {
        if ( firstAffectedOrdinate < 0 ) {
            throw new IllegalArgumentException(
                                                Resources.format(
                                                                  ResourceKeys.ERROR_ILLEGAL_ARGUMENT_$2,
                                                                  "firstAffectedOrdinate",
                                                                  new Integer(
                                                                               firstAffectedOrdinate ) ) );
        }
        if ( numTrailingOrdinates < 0 ) {
            throw new IllegalArgumentException(
                                                Resources.format(
                                                                  ResourceKeys.ERROR_ILLEGAL_ARGUMENT_$2,
                                                                  "numTrailingOrdinates",
                                                                  new Integer( numTrailingOrdinates ) ) );
        }
        if ( firstAffectedOrdinate == 0 && numTrailingOrdinates == 0 ) {
            return subTransform;
        }
        //
        // Optimize the "Identity transform" case.
        //
        if ( subTransform.isIdentity() ) {
            final int dimension = subTransform.getDimSource();
            if ( dimension == subTransform.getDimTarget() ) {
                // The AffineTransform is easier to concatenate with other transforms.
                return createIdentityTransform( firstAffectedOrdinate + dimension
                                                + numTrailingOrdinates );
            }
        }
        //
        // Optimize the "Pass through case": this is done
        // right into PassThroughTransform's constructor.
        //
        return (MathTransform) pool.intern( new PassThroughTransform( firstAffectedOrdinate,
                                                                      subTransform,
                                                                      numTrailingOrdinates ) );
    }

    /**
     * Creates a transform which retains only a portion of an other transform. For example
     * if the source coordinate system has (<var>longitude</var>, <var>latitude</var>,
     * <var>height</var>) values, then a sub-transform may be used to keep only the
     * (<var>longitude</var>, <var>latitude</var>) part. In most cases, the created
     * sub-transform is non-invertible since it loose informations.
     * <br><br>
     * This transform is a special case of a non-square matrix transform with less
     * rows than columns. However, using a <code>createSubMathTransfom(...)</code>
     * method makes it easier to optimize some common cases.
     *
     * @param transform The transform.
     * @param lower Index of the first ordinate to keep.
     * @param upper Index of the first ordinate. Must be greater than <code>lower</code>.
     */
    public MathTransform createSubMathTransform( final int lower, final int upper,
                                                final MathTransform transform ) {
        if ( lower < 0 || lower >= upper ) {
            throw new IllegalArgumentException(
                                                Resources.format(
                                                                  ResourceKeys.ERROR_ILLEGAL_ARGUMENT_$2,
                                                                  "lower", new Integer( lower ) ) );
        }
        final int dimTarget = transform.getDimTarget();
        if ( upper > dimTarget ) {
            throw new IllegalArgumentException(
                                                Resources.format(
                                                                  ResourceKeys.ERROR_ILLEGAL_ARGUMENT_$2,
                                                                  "upper", new Integer( upper ) ) );
        }
        if ( lower == 0 && upper == dimTarget ) {
            return transform;
        }
        if ( transform instanceof PassThroughTransform ) {
            // Special case for pass through transform:
            // Compute lower and upper values relatives
            // to the underlying sub-transform.
            final PassThroughTransform passThrough = (PassThroughTransform) transform;
            final int lowerTr = lower - passThrough.firstAffectedOrdinate;
            final int upperTr = upper - passThrough.firstAffectedOrdinate;
            final int passDim = passThrough.transform.getDimTarget();
            if ( lowerTr >= 0 && upperTr <= passDim ) {
                return createSubMathTransform( lowerTr, upperTr, passThrough.transform );
            }
            if ( lowerTr <= 0 && upperTr >= passDim ) {
                return createPassThroughTransform( -lowerTr, passThrough.transform, upperTr
                                                                                    - passDim );
            }
        }
        // General case: use a matrix.
        final int dimOutput = upper - lower;
        final Matrix matrix = new Matrix( dimOutput + 1, dimTarget + 1 );
        matrix.setZero();
        for ( int i = lower; i < upper; i++ ) {
            matrix.setElement( i - lower, i, 1 );
        }
        matrix.setElement( dimOutput, dimTarget, 1 ); // Affine transform has one more row/column than dimension.
        return createConcatenatedTransform( transform, createAffineTransform( matrix ) );
    }

    /**
     * Creates a transform from a classification name and parameters.
     * The client must ensure that all the linear parameters are expressed
     * in meters, and all the angular parameters are expressed in degrees.
     * Also, they must supply "semi_major" and "semi_minor" parameters
     * for cartographic projection transforms.
     *
     * @param  classification The classification name of the transform
     *         (e.g. "Transverse_Mercator"). Leading and trailing spaces
     *         are ignored, and comparaison is case-insensitive.
     * @param  parameters The parameter values in standard units.
     * @return The parameterized transform.
     * @throws NoSuchElementException if there is no transform for the specified classification.
     * @throws MissingParameterException if a parameter was required but not found.
     *
     */
    public MathTransform createParameterizedTransform( String classification,
                                                      final ParameterList parameters )
                            throws NoSuchElementException, MissingParameterException {
        final MathTransform transform;
        classification = classification.trim();
        if ( classification.equalsIgnoreCase( "Affine" ) ) {
            // Special case for "Affine", since the ParameterListDescriptor
            // depends of the matrix size.
            transform = MatrixTransform.Provider.staticCreate( parameters );
        } else {
            transform = getMathTransformProvider( classification ).create( parameters );
        }
        return (MathTransform) pool.intern( transform );
    }

    /**
     * Convenience method for creating a transform from a projection.
     *
     * @param  projection The projection.
     * @return The parameterized transform.
     * @throws NoSuchElementException if there is no transform for the specified projection.
     * @throws MissingParameterException if a parameter was required but not found.
     */
    public MathTransform createParameterizedTransform( final Projection projection )
                            throws NoSuchElementException, MissingParameterException {
        return createParameterizedTransform( projection.getClassName(), projection.getParameters() );
    }

    /**
     * Returns the classification names of every available transforms.
     * The returned array may have a zero length, but will never be null.
     *
     */
    public String[] getAvailableTransforms() {
        final String[] names = new String[providers.length + 1];
        int i;
        for ( i = 0; i < names.length; i++ ) {
            names[i] = providers[i].getClassName();
        }
        // Special case for "Affine", since the ParameterListDescriptor
        // depends of the matrix size.
        names[i] = "Affine";
        return names;
    }

    /**
     * Returns the provider for the specified classification. This provider
     * may be used to query parameter list for a classification name (e.g.
     * <code>getMathTransformProvider("Transverse_Mercator").getParameterList()</code>),
     * or the transform name in a given locale (e.g.
     * <code>getMathTransformProvider("Transverse_Mercator").getName({@link Locale#FRENCH})</code>)
     *
     * @param  classification The classification name of the transform
     *         (e.g. "Transverse_Mercator"). It should be one of the name
     *         returned by {@link #getAvailableTransforms}. Leading and
     *         trailing spaces are ignored. Comparisons are case-insensitive.
     * @return The provider for a math transform.
     * @throws NoSuchElementException if there is no provider registered
     *         with the specified classification name.
     */
    public MathTransformProvider getMathTransformProvider( String classification )
                            throws NoSuchElementException {
        classification = classification.trim();
        for ( int i = 0; i < providers.length; i++ ) {
            if ( classification.equalsIgnoreCase( providers[i].getClassName().trim() ) )
                return providers[i];
        }
        throw new NoSuchElementException(
                                          Resources.format(
                                                            ResourceKeys.ERROR_NO_TRANSFORM_FOR_CLASSIFICATION_$1,
                                                            classification ) );
    }

    /**
     * Create a provider for affine transforms of the specified
     * dimension. Created affine transforms will have a size of
     * <code>numRow&nbsp;&times;&nbsp;numCol</code>.
     * <br><br>
     * <table align="center" border='1' cellpadding='3' bgcolor="F4F8FF">
     *   <tr bgcolor="#B9DCFF"><th>Parameter</th> <th>Description</th></tr>
     *   <tr><td><code>Num_row</code></td> <td>Number of rows in matrix</td></tr>
     *   <tr><td><code>Num_col</code></td> <td>Number of columns in matrix</td></tr>
     *   <tr><td><code>elt_&lt;r&gt;_&lt;c&gt;</code></td> <td>Element of matrix</td></tr>
     * </table>
     * <br>
     * For the element parameters, <code>&lt;r&gt;</code> and <code>&lt;c&gt;</code>
     * should be substituted by printed decimal numbers. The values of <var>r</var>
     * should be from 0 to <code>(num_row-1)</code>, and the values of <var>c</var>
     * should be from 0 to <code>(num_col-1)</code>. Any undefined matrix elements
     * are assumed to be zero for <code>(r!=c)</code>, and one for <code>(r==c)</code>.
     * This corresponds to the identity transformation when the number of rows and columns
     * are the same. The number of columns corresponds to one more than the dimension of
     * the source coordinates and the number of rows corresponds to one more than the
     * dimension of target coordinates. The extra dimension in the matrix is used to
     * let the affine map do a translation.
     *
     * @param  numRow The number of matrix's rows.
     * @param  numCol The number of matrix's columns.
     * @return The provider for an affine transform.
     * @throws IllegalArgumentException if <code>numRow</code>
     *         or <code>numCol</code> is not a positive number.
     */
    public MathTransformProvider getAffineTransformProvider( final int numRow, final int numCol )
                            throws IllegalArgumentException {
        return new MatrixTransform.Provider( numRow, numCol );
    }

}
