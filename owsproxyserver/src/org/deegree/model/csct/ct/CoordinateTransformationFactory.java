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
import javax.media.jai.ParameterList;
import javax.vecmath.SingularMatrixException;

import org.deegree.model.csct.cs.AxisOrientation;
import org.deegree.model.csct.cs.CompoundCoordinateSystem;
import org.deegree.model.csct.cs.CoordinateSystem;
import org.deegree.model.csct.cs.Ellipsoid;
import org.deegree.model.csct.cs.GeocentricCoordinateSystem;
import org.deegree.model.csct.cs.GeographicCoordinateSystem;
import org.deegree.model.csct.cs.HorizontalCoordinateSystem;
import org.deegree.model.csct.cs.HorizontalDatum;
import org.deegree.model.csct.cs.PrimeMeridian;
import org.deegree.model.csct.cs.ProjectedCoordinateSystem;
import org.deegree.model.csct.cs.Projection;
import org.deegree.model.csct.cs.TemporalCoordinateSystem;
import org.deegree.model.csct.cs.VerticalCoordinateSystem;
import org.deegree.model.csct.cs.VerticalDatum;
import org.deegree.model.csct.cs.WGS84ConversionInfo;
import org.deegree.model.csct.pt.Dimensioned;
import org.deegree.model.csct.pt.Matrix;
import org.deegree.model.csct.resources.OpenGIS;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * Creates coordinate transformations.
 *
 * @version 1.0
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.ct.CT_CoordinateTransformationFactory
 */
public class CoordinateTransformationFactory {
    /**
     * The default coordinate transformation factory.
     * Will be constructed only when first needed.
     */
    private static CoordinateTransformationFactory DEFAULT;

    /**
     * Number for temporary created objects. This number is
     * incremented each time {@link #getTemporaryName} is
     * invoked.
     */
    private static volatile int temporaryID;

    /**
     * The underlying math transform factory.
     */
    private final MathTransformFactory factory;

    /**
     * Construct a coordinate transformation factory.
     *
     * @param factory The math transform factory to use.
     */
    public CoordinateTransformationFactory( final MathTransformFactory factory ) {
        this.factory = factory;
    }

    /**
     * Returns the default coordinate transformation factory.
     */
    public static synchronized CoordinateTransformationFactory getDefault() {
        if ( DEFAULT == null ) {
            DEFAULT = new CoordinateTransformationFactory( MathTransformFactory.getDefault() );
        }
        return DEFAULT;
    }

    /**
     * Returns the underlying math transform factory. This factory
     * is used for constructing {@link MathTransform} objects for
     * all {@link CoordinateTransformation}.
     */
    public final MathTransformFactory getMathTransformFactory() {
        return factory;
    }

    /**
     * Creates a transformation between two coordinate systems.
     * This method will examine the coordinate systems in order to construct a
     * transformation between them. This method may fail if no path between the
     * coordinate systems is found.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     *
     */
    public CoordinateTransformation createFromCoordinateSystems( final CoordinateSystem sourceCS,
                                                                final CoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        /////////////////////////////////////////////////////////////////////
        ////                                                             ////
        ////     Geographic  -->  Geographic, Projected or Geocentric    ////
        ////                                                             ////
        /////////////////////////////////////////////////////////////////////
        if ( sourceCS instanceof GeographicCoordinateSystem ) {
            final GeographicCoordinateSystem source = (GeographicCoordinateSystem) sourceCS;
            if ( targetCS instanceof GeographicCoordinateSystem ) {
                return createTransformationStep( source, (GeographicCoordinateSystem) targetCS );
            }
            if ( targetCS instanceof ProjectedCoordinateSystem ) {
                return createTransformationStep( source, (ProjectedCoordinateSystem) targetCS );
            }
            if ( targetCS instanceof GeocentricCoordinateSystem ) {
                return createTransformationStep( source, (GeocentricCoordinateSystem) targetCS );
            }
        }
        /////////////////////////////////////////////////////////////////////
        ////                                                             ////
        ////     Projected  -->  Projected, Geographic or Geocentric     ////
        ////                                                             ////
        /////////////////////////////////////////////////////////////////////
        if ( sourceCS instanceof ProjectedCoordinateSystem ) {
            final ProjectedCoordinateSystem source = (ProjectedCoordinateSystem) sourceCS;
            if ( targetCS instanceof ProjectedCoordinateSystem ) {
                return createTransformationStep( source, (ProjectedCoordinateSystem) targetCS );
            }
            if ( targetCS instanceof GeographicCoordinateSystem ) {
                return createTransformationStep( source, (GeographicCoordinateSystem) targetCS );
            }
            if ( targetCS instanceof GeocentricCoordinateSystem ) {
                return createTransformationStep( source, (GeocentricCoordinateSystem) targetCS );
            }
        }
        /////////////////////////////////////////////////////////////////////
        ////                                                             ////
        ////     Geocentric  -->  Geocentric, Horizontal or Compound     ////
        ////                                                             ////
        /////////////////////////////////////////////////////////////////////
        if ( sourceCS instanceof GeocentricCoordinateSystem ) {
            final GeocentricCoordinateSystem source = (GeocentricCoordinateSystem) sourceCS;
            if ( targetCS instanceof GeocentricCoordinateSystem ) {
                return createTransformationStep( source, (GeocentricCoordinateSystem) targetCS );
            }
            try {
                return createFromCoordinateSystems( targetCS, sourceCS ).inverse();
            } catch ( TransformException exception ) {
                final CannotCreateTransformException e = new CannotCreateTransformException(
                                                                                             sourceCS,
                                                                                             targetCS );
                e.initCause( exception );
                throw e;
            }
        }
        /////////////////////////////////////////
        ////                                 ////
        ////     Vertical  -->  Vertical     ////
        ////                                 ////
        /////////////////////////////////////////
        if ( sourceCS instanceof VerticalCoordinateSystem ) {
            final VerticalCoordinateSystem source = (VerticalCoordinateSystem) sourceCS;
            if ( targetCS instanceof VerticalCoordinateSystem ) {
                return createTransformationStep( source, (VerticalCoordinateSystem) targetCS );
            }
        }
        /////////////////////////////////////////
        ////                                 ////
        ////     Temporal  -->  Temporal     ////
        ////                                 ////
        /////////////////////////////////////////
        if ( sourceCS instanceof TemporalCoordinateSystem ) {
            final TemporalCoordinateSystem source = (TemporalCoordinateSystem) sourceCS;
            if ( targetCS instanceof TemporalCoordinateSystem ) {
                return createTransformationStep( source, (TemporalCoordinateSystem) targetCS );
            }
        }
        ///////////////////////////////////////////
        ////                                   ////
        ////     Compound  -->  various CS     ////
        ////                                   ////
        ///////////////////////////////////////////
        if ( sourceCS instanceof CompoundCoordinateSystem ) {
            final CompoundCoordinateSystem source = (CompoundCoordinateSystem) sourceCS;
            if ( targetCS instanceof CompoundCoordinateSystem ) {
                return createTransformationStep( source, (CompoundCoordinateSystem) targetCS );
            }
            if ( targetCS instanceof GeocentricCoordinateSystem ) {
                return createTransformationStep( source, (GeocentricCoordinateSystem) targetCS );
            }
            /*
             * Try a loosely transformation. For example, the source CS may be
             * a geographic + vertical coordinate systems,  will the target CS
             * may be only the geographic part.     The code below will try to
             * discart one or more dimension.
             */
            final CoordinateSystem headSourceCS = source.getHeadCS();
            final CoordinateSystem tailSourceCS = source.getTailCS();
            final int dimHeadCS = headSourceCS.getDimension();
            final int dimSource = source.getDimension();
            CoordinateTransformation step2;
            int lower, upper;
            try {
                lower = 0;
                upper = dimHeadCS;
                step2 = createFromCoordinateSystems( headSourceCS, targetCS );
            } catch ( CannotCreateTransformException exception ) {
                /*
                 * If we can't construct a transformation from the head CS,
                 * then try a transformation from the tail CS. If this step
                 * fails also, then the head CS will be taken as the raison
                 * for the failure.
                 */
                try {
                    lower = dimHeadCS;
                    upper = dimSource;
                    step2 = createFromCoordinateSystems( tailSourceCS, targetCS );
                } catch ( CannotCreateTransformException ignore ) {
                    CannotCreateTransformException e = new CannotCreateTransformException(
                                                                                           sourceCS,
                                                                                           targetCS );
                    e.initCause( exception );
                    throw e;
                }
            }
            /*
             * A coordinate transformation from the head or tail part of 'sourceCS'
             * has been succesfully contructed. Now, build a matrix transform that
             * will select only the corresponding ordinates from input arrays, and
             * pass them to the transform.
             */
            final MathTransform step1 = factory.createSubMathTransform(
                                                                        lower,
                                                                        upper,
                                                                        factory.createIdentityTransform( dimSource ) );
            final MathTransform transform = factory.createConcatenatedTransform(
                                                                                 step1,
                                                                                 step2.getMathTransform() );
            return createFromMathTransform( sourceCS, targetCS, step2.getTransformType(), transform );
        }
        throw new CannotCreateTransformException( sourceCS, targetCS );
    }

    /**
     * Creates a transformation between two temporal coordinate systems. This
     * method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. The default implementation checks if
     * both coordinate systems use the same datum, and then adjusts for axis
     * orientation, units and epoch.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final TemporalCoordinateSystem sourceCS,
                                                                final TemporalCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        if ( Utilities.equals( sourceCS.getTemporalDatum(), targetCS.getTemporalDatum() ) ) {
            // Compute the epoch shift
            double epochShift = sourceCS.getEpoch().getTime() - targetCS.getEpoch().getTime();
            epochShift = targetCS.getUnits( 0 ).convert( epochShift / ( 24 * 60 * 60 * 1000 ),
                                                         Unit.DAY );

            // Get the affine transform, add the epoch
            // shift and returns the resulting transform.
            final Matrix matrix = swapAndScaleAxis( sourceCS, targetCS );
            final int translationColumn = matrix.getNumCol() - 1;
            if ( translationColumn >= 0 ) // Paranoiac check: should always be 1.
            {
                final double translation = matrix.getElement( 0, translationColumn );
                matrix.setElement( 0, translationColumn, translation + epochShift );
            }
            final MathTransform transform = factory.createAffineTransform( matrix );
            return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION, transform );
        }
        throw new CannotCreateTransformException( sourceCS, targetCS );
    }

    /**
     * Creates a transformation between two vertical coordinate systems. This
     * method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. The default implementation checks if
     * both coordinate systems use the same datum, and then adjusts for axis
     * orientation and units.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final VerticalCoordinateSystem sourceCS,
                                                                final VerticalCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        if ( Utilities.equals( sourceCS.getVerticalDatum(), targetCS.getVerticalDatum() ) ) {
            final Matrix matrix = swapAndScaleAxis( sourceCS, targetCS );
            final MathTransform transform = factory.createAffineTransform( matrix );
            return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION, transform );
        }
        throw new CannotCreateTransformException( sourceCS, targetCS );
    }

    /**
     * Creates a transformation between two geographic coordinate systems. This
     * method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. The default implementation can adjust
     * axis order and orientation (e.g. transforming from <code>(NORTH,WEST)</code>
     * to <code>(EAST,NORTH)</code>), performs units conversion and apply Bursa Wolf
     * transformation if needed.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final GeographicCoordinateSystem sourceCS,
                                                                final GeographicCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        final HorizontalDatum sourceDatum = sourceCS.getHorizontalDatum();
        final HorizontalDatum targetDatum = targetCS.getHorizontalDatum();
        final Ellipsoid sourceEllipsoid = sourceDatum.getEllipsoid();
        final Ellipsoid targetEllipsoid = targetDatum.getEllipsoid();
        if ( !Utilities.equals( sourceEllipsoid, targetEllipsoid ) )
            try {
                /*
                 * If the two geographic coordinate systems use differents ellipsoid,
                 * convert from the source to target ellipsoid through the geocentric
                 * coordinate system.
                 */

                final String name = getTemporaryName();
                final GeocentricCoordinateSystem gcs1 = new GeocentricCoordinateSystem( name,
                                                                                        sourceDatum );
                final GeocentricCoordinateSystem gcs3 = new GeocentricCoordinateSystem( name,
                                                                                        targetDatum );
                CoordinateTransformation step1 = createTransformationStep( sourceCS, gcs1 );
                CoordinateTransformation step2 = createTransformationStep( gcs1, gcs3 );
                CoordinateTransformation step3 = createTransformationStep( targetCS, gcs3 ).inverse();
                return concatenate( step1, step2, step3 );
                
            } catch ( TransformException exception ) {
                CannotCreateTransformException e = new CannotCreateTransformException( sourceCS,
                                                                                       targetCS );
                throw e;
            }
        /*
         * Swap axis order, and rotate the longitude
         * coordinate if prime meridians are different.
         */
        final Matrix matrix = swapAndScaleGeoAxis( sourceCS, targetCS );
        // TODO: We should ensure that longitude is in range [-180..+180�].
        final MathTransform transform = factory.createAffineTransform( matrix );
        return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION, transform );
    }

    /**
     * Creates a transformation between two projected coordinate systems. This
     * method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. The default implementation can adjust
     * axis order and orientation. It also performs units conversion if it
     * is the only extra change needed. Otherwise, it performs three steps:
     *
     * <ol>
     *   <li>Unproject <code>sourceCS</code>.</li>
     *   <li>Transform from <code>sourceCS.geographicCS</code> to <code>targetCS.geographicCS</code>.</li>
     *   <li>Project <code>targetCS</code>.</li>
     * </ol>
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final ProjectedCoordinateSystem sourceCS,
                                                                final ProjectedCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        if ( Utilities.equals( sourceCS.getProjection(), targetCS.getProjection() )
             && Utilities.equals( sourceCS.getHorizontalDatum(), targetCS.getHorizontalDatum() ) ) {
            // This special case is necessary for createTransformationStep(GeographicCS,ProjectedCS).
            // 'swapAndScaleAxis(...) takes care of axis orientation and units. Datum and projection
            // have just been checked above. Prime meridien is not checked (TODO: should we do???)
            final Matrix matrix = swapAndScaleAxis( sourceCS, targetCS );
            final MathTransform transform = factory.createAffineTransform( matrix );
            return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION, transform );
        }
        final GeographicCoordinateSystem sourceGeo = sourceCS.getGeographicCoordinateSystem();
        final GeographicCoordinateSystem targetGeo = targetCS.getGeographicCoordinateSystem();
        final CoordinateTransformation step1 = createTransformationStep( sourceCS, sourceGeo );
        final CoordinateTransformation step2 = createTransformationStep( sourceGeo, targetGeo );
        final CoordinateTransformation step3 = createTransformationStep( targetGeo, targetCS );
        return concatenate( step1, step2, step3 );
    }

    /**
     * Creates a transformation between a geographic and a projected coordinate systems.
     * This method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final GeographicCoordinateSystem sourceCS,
                                                                final ProjectedCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        
        final ProjectedCoordinateSystem stepProjCS = normalize( targetCS );      
        
        final GeographicCoordinateSystem stepGeoCS = stepProjCS.getGeographicCoordinateSystem();
        final Projection projection = stepProjCS.getProjection();
        final MathTransform mapProjection = factory.createParameterizedTransform( projection );
        final CoordinateTransformation step1 = createTransformationStep( sourceCS, stepGeoCS );
        final CoordinateTransformation step2 = createFromMathTransform( stepGeoCS, stepProjCS,
                                                                        TransformType.CONVERSION,
                                                                        mapProjection );

        // TODO
        // this is possibly an error because  stepProjCS and targetCS
        // are identical
        //final CoordinateTransformation step3 = createTransformationStep( stepProjCS, targetCS );
        //return concatenate( step1, step2, step3 );
        // it seems it must look like this
        return concatenate( step1, step2, null );
    }

    /**
     * Creates a transformation between a projected and a geographic coordinate systems.
     * This method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. The default implementation returns
     * <code>{@link #createTransformationStep(GeographicCoordinateSystem, ProjectedCoordinateSystem)
     * createTransformationStep}(targetCS, sourceCS).{@link MathTransform#inverse() inverse()}</code>.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final ProjectedCoordinateSystem sourceCS,
                                                                final GeographicCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        try {
            return createTransformationStep( targetCS, sourceCS ).inverse();
        } catch ( NoninvertibleTransformException exception ) {
            final CannotCreateTransformException e = new CannotCreateTransformException( sourceCS,
                                                                                         targetCS );
            throw e;
        }
    }

    /**
     * Creates a transformation between two geocentric coordinate systems. This
     * method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. The default implementation can adjust
     * for axis order and orientation, adjust for prime meridian, performs units
     * conversion and apply Bursa Wolf transformation if needed.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final GeocentricCoordinateSystem sourceCS,
                                                                final GeocentricCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        final HorizontalDatum sourceHD = sourceCS.getHorizontalDatum();
        final HorizontalDatum targetHD = targetCS.getHorizontalDatum();                 
        if ( Utilities.equals( sourceHD, targetHD ) ) {            
            if ( Utilities.equals( sourceCS.getPrimeMeridian(), targetCS.getPrimeMeridian() ) ) {
                final Matrix matrix = swapAndScaleAxis( sourceCS, targetCS );
                final MathTransform transform = factory.createAffineTransform( matrix );       
                return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION,
                                                transform );
            }
            // If prime meridians are not the same, performs the full transformation.
        }
        if ( !PrimeMeridian.GREENWICH.equals( sourceCS.getPrimeMeridian() )
             || !PrimeMeridian.GREENWICH.equals( targetCS.getPrimeMeridian() ) ) {
            throw new CannotCreateTransformException(
                                                      "Rotation of prime meridian not yet implemented" );
        }
        // Transform between differents ellipsoids
        // using Bursa Wolf parameters.
        final Matrix step1 = swapAndScaleAxis( sourceCS, GeocentricCoordinateSystem.DEFAULT );
        final Matrix step2 = getWGS84Parameters( sourceHD );
        final Matrix step3 = getWGS84Parameters( targetHD );
        final Matrix step4 = swapAndScaleAxis( GeocentricCoordinateSystem.DEFAULT, targetCS );
        
        if ( step2 != null && step3 != null )
            try {
                // Note: GMatrix.mul(GMatrix) is equivalents to AffineTransform.concatenate(...):
                //       First transform by the supplied transform and then transform the result
                //       by the original transform.
                step3.invert(); // Invert in place.
                step4.mul( step3 ); // step4 = step4*step3
                step4.mul( step2 ); // step4 = step4*step3*step2
                step4.mul( step1 ); // step4 = step4*step3*step2*step1
                final MathTransform transform = factory.createAffineTransform( step4 );
                return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION,
                                                transform );
            } catch ( SingularMatrixException exception ) {
                final CannotCreateTransformException e = new CannotCreateTransformException(
                                                                                             sourceCS,
                                                                                             targetCS );
                throw e;
            }
        throw new CannotCreateTransformException(
                                                  Resources.format( ResourceKeys.BURSA_WOLF_PARAMETERS_REQUIRED ) );
    }

    /**
     * Creates a transformation between a geographic and a geocentric coordinate systems.
     * Since the source coordinate systems doesn't have a vertical axis, height above the
     * ellipsoid is assumed equals to zero everywhere. This method is automatically invoked
     * by {@link #createFromCoordinateSystems createFromCoordinateSystems(...)}.
     *
     * @param  sourceCS Input geographic coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final GeographicCoordinateSystem sourceCS,
                                                                final GeocentricCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        if ( !PrimeMeridian.GREENWICH.equals( targetCS.getPrimeMeridian() ) ) {
            throw new CannotCreateTransformException(
                                                      "Rotation of prime meridian not yet implemented" );
        }
        final MathTransform step1 = factory.createAffineTransform( swapAndScaleGeoAxis(
                                                                                        sourceCS,
                                                                                        GeographicCoordinateSystem.WGS84 ) );
        final MathTransform step2 = getGeocentricTransform(
                                                            "Ellipsoid_To_Geocentric",
                                                            2,
                                                            sourceCS.getHorizontalDatum().getEllipsoid() );
        return createFromMathTransform( sourceCS, targetCS, TransformType.CONVERSION,
                                        factory.createConcatenatedTransform( step1, step2 ) );
    }

    /**
     * Creates a transformation between a projected and a geocentric coordinate systems.
     * This method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}. This method doesn't need to be public since
     * its decomposition in two step should be general enough.
     *
     * @param  sourceCS Input projected coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    private CoordinateTransformation createTransformationStep(
                                                              final ProjectedCoordinateSystem sourceCS,
                                                              final GeocentricCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        final GeographicCoordinateSystem sourceGCS = sourceCS.getGeographicCoordinateSystem();
        final CoordinateTransformation step1 = createTransformationStep( sourceCS, sourceGCS );
        final CoordinateTransformation step2 = createTransformationStep( sourceGCS, targetCS );
        return concatenate( step1, step2 );
    }

    /**
     * Creates a transformation between a compound and a geocentric coordinate systems.
     * The compound coordinate system <strong>must</strong> be an aggregate of the two
     * following coordinate systems:
     *
     * <ul>
     *   <li>The head must be an instance of {@link HorizontalCoordinateSystem}</li>
     *   <li>The tail must be an instance of {@link VerticalCoordinateSystem}</li>
     * </ul>
     *
     * This method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}.
     *
     * @param  sourceCS Input compound coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final CompoundCoordinateSystem sourceCS,
                                                                final GeocentricCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        /*
         * Construct a temporary transformation from 'sourceCS' to a standard coordinate
         * system which is an agregate of a geographic and a vertical coordinate system.
         * The horizontal datum is preserved, but other properties (vertical datum, axis
         * order, units, prime meridian...) are "standardized".
         */
        final String name = getTemporaryName();
        final HorizontalDatum datum = OpenGIS.getHorizontalDatum( sourceCS );
        final CompoundCoordinateSystem stdCS = 
            new CompoundCoordinateSystem( name, new GeographicCoordinateSystem( name, datum ),
                                          new VerticalCoordinateSystem( name, VerticalDatum.ELLIPSOIDAL ) );
        final CoordinateTransformation step1 = createTransformationStep( sourceCS, stdCS );
        /*
         * Construct the next steps: conversion to the geocentric coordinate
         * system, and then conversion to the requested coordinate system.
         * In summary, the 3 conversions steps are:
         *
         *    sourceCS --> standardized geographic CS --> standardized geocentric CS --> targetCS
         */
        final MathTransform transform = getGeocentricTransform( "Ellipsoid_To_Geocentric", 3,
                                                                datum.getEllipsoid() );
        final CoordinateTransformation step2 = createFromMathTransform( stdCS,
                                                                        GeocentricCoordinateSystem.DEFAULT,
                                                                        TransformType.CONVERSION,
                                                                        transform );
        final CoordinateTransformation step3 = createTransformationStep( GeocentricCoordinateSystem.DEFAULT,
                                                                         targetCS );
        return concatenate( step1, step2, step3 );
    }

    /**
     * Creates a transformation between two compound coordinate systems. This
     * method is automatically invoked by {@link #createFromCoordinateSystems
     * createFromCoordinateSystems(...)}.
     *
     * @param  sourceCS Input coordinate system.
     * @param  targetCS Output coordinate system.
     * @return A coordinate transformation from <code>sourceCS</code> to <code>targetCS</code>.
     * @throws CannotCreateTransformException if no transformation path has been found.
     */
    protected CoordinateTransformation createTransformationStep(
                                                                final CompoundCoordinateSystem sourceCS,
                                                                final CompoundCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        final CoordinateSystem headSourceCS = sourceCS.getHeadCS();
        final CoordinateSystem tailSourceCS = sourceCS.getTailCS();
        final CoordinateSystem headTargetCS = targetCS.getHeadCS();
        final CoordinateSystem tailTargetCS = targetCS.getTailCS();
        if ( tailSourceCS.equivalents( tailTargetCS ) ) {
            final CoordinateTransformation tr = createFromCoordinateSystems( headSourceCS,
                                                                             headTargetCS );
            final MathTransform transform = factory.createPassThroughTransform(
                                                                                0,
                                                                                tr.getMathTransform(),
                                                                                tailSourceCS.getDimension() );
            return createFromMathTransform( sourceCS, targetCS, tr.getTransformType(), transform );
        }
        if ( headSourceCS.equivalents( headTargetCS ) ) {
            final CoordinateTransformation tr = createFromCoordinateSystems( tailSourceCS,
                                                                             tailTargetCS );
            final MathTransform transform = factory.createPassThroughTransform(
                                                                                headSourceCS.getDimension(),
                                                                                tr.getMathTransform(),
                                                                                0 );
            return createFromMathTransform( sourceCS, targetCS, tr.getTransformType(), transform );
        }
        // TODO: implement others CompoundCoordinateSystem cases.
        //       We could do it in a more general way be creating
        //       and using a 'CompoundTransform' class instead of
        //       of 'PassThroughTransform'.  PassThroughTransform
        //       is really a special case of a 'CompoundTransform'
        //       where the head transform is the identity transform.
        throw new CannotCreateTransformException( sourceCS, targetCS );
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////                                                ////////////
    ////////////            HELPER METHODS (private)            ////////////
    ////////////                                                ////////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Returns the WGS84 parameters as an affine transform,
     * or <code>null</code> if not available.
     */
    private static Matrix getWGS84Parameters( final HorizontalDatum datum ) {
        final WGS84ConversionInfo info = datum.getWGS84Parameters();
        if ( info != null ) {
            return info.getAffineTransform();
        }
        if ( Ellipsoid.WGS84.equals( datum.getEllipsoid() ) ) {
            return new Matrix( 4 ); // Identity matrix
        }
        return null;
    }

    /**
     * Returns a transform between a geocentric
     * coordinate system and an ellipsoid.
     *
     * @param  classification either "Ellipsoid_To_Geocentric" or "Geocentric_To_Ellipsoid".
     * @param  dimGeoCS Dimension of the geographic coordinate system (2 or 3).
     * @param  ellipsoid The ellipsoid.
     * @return The transformation.
     */
    private MathTransform getGeocentricTransform( final String classification, final int dimGeoCS,
                                                 final Ellipsoid ellipsoid ) {
        final Unit unit = ellipsoid.getAxisUnit();
        ParameterList param = factory.getMathTransformProvider( classification ).getParameterList();
        param = param.setParameter( "semi_major", Unit.METRE.convert( ellipsoid.getSemiMajorAxis(),
                                                                      unit ) );
        param = param.setParameter( "semi_minor", Unit.METRE.convert( ellipsoid.getSemiMinorAxis(),
                                                                      unit ) );
        try {
            param = param.setParameter( "dim_geoCS", dimGeoCS );
        } catch ( IllegalArgumentException exception ) {
            // The "dim_geoCS" is a custom argument needed for our SEAGIS
            // implementation. It is not part of OpenGIS's specification.
            // But if the required dimension is not 3, we can't finish
            // the operation (TODO: What should we do? Open question...)
            if ( dimGeoCS != 3 ) {
                throw exception;
            }
        }
        return factory.createParameterizedTransform( classification, param );
    }

    /**
     * Concatenate two transformation steps.
     *
     * @param  step1 The first  step, or <code>null</code> for the identity transform.
     * @param  step2 The second step, or <code>null</code> for the identity transform.
     * @return A concatenated transform, or <code>null</code> if all arguments was nul.
     */
    private CoordinateTransformation concatenate( final CoordinateTransformation step1,
                                                 final CoordinateTransformation step2 ) {
        if ( step1 == null )
            return step2;
        if ( step2 == null )
            return step1;
        if ( !step1.getTargetCS().equivalents( step2.getSourceCS() ) ) {
            throw new IllegalArgumentException( String.valueOf( step1 ) );
        }
        final MathTransform step = factory.createConcatenatedTransform( step1.getMathTransform(),
                                                                        step2.getMathTransform() );
        final TransformType type = step1.getTransformType().concatenate( step2.getTransformType() );

        return createFromMathTransform( step1.getSourceCS(), step2.getTargetCS(), type, step );
    }

    /**
     * Concatenate three transformation steps.
     *
     * @param  step1 The first  step, or <code>null</code> for the identity transform.
     * @param  step2 The second step, or <code>null</code> for the identity transform.
     * @param  step3 The third  step, or <code>null</code> for the identity transform.
     * @return A concatenated transform, or <code>null</code> if all arguments was nul.
     */
    private CoordinateTransformation concatenate( final CoordinateTransformation step1,
                                                 final CoordinateTransformation step2,
                                                 final CoordinateTransformation step3 ) {
        if ( step1 == null )
            return concatenate( step2, step3 );
        if ( step2 == null )
            return concatenate( step1, step3 );
        if ( step3 == null ) {
            return concatenate( step1, step2 );
        }
        if ( !step1.getTargetCS().equivalents( step2.getSourceCS() ) ) {
            // Current message is for debugging purpose only.
            throw new IllegalArgumentException( String.valueOf( step1 ) );
        }
        if ( !step2.getTargetCS().equivalents( step3.getSourceCS() ) ) {
            // Current message is for debugging purpose only.
            throw new IllegalArgumentException( String.valueOf( step3 ) );
        }
        final MathTransform step = factory.createConcatenatedTransform(
                                                                        step1.getMathTransform(),
                                                                        factory.createConcatenatedTransform(
                                                                                                             step2.getMathTransform(),
                                                                                                             step3.getMathTransform() ) );
        final TransformType type = step1.getTransformType().concatenate(
                                                                         step2.getTransformType().concatenate(
                                                                                                               step3.getTransformType() ) );
        return createFromMathTransform( step1.getSourceCS(), step3.getTargetCS(), type, step );
    }

    /**
     * Create a coordinate transform from a math transform.
     * If the specified math transform is already a coordinate transform,  and if source
     * and target coordinate systems match, then <code>transform</code> is returned with
     * no change. Otherwise, a new coordinate transform is created.
     *
     * @param  sourceCS  The source coordinate system.
     * @param  targetCS  The destination coordinate system.
     * @param  type      The transform type.
     * @param  transform The math transform.
     * @return A coordinate transform using the specified math transform.
     */
    private static CoordinateTransformation createFromMathTransform(
                                                                    final CoordinateSystem sourceCS,
                                                                    final CoordinateSystem targetCS,
                                                                    final TransformType type,
                                                                    final MathTransform transform ) {
        if ( transform instanceof CoordinateTransformation ) {
            final CoordinateTransformation ct = (CoordinateTransformation) transform;
            if ( Utilities.equals( ct.getSourceCS(), sourceCS )
                 && Utilities.equals( ct.getTargetCS(), targetCS ) ) {
                return ct;
            }
        }
        return (CoordinateTransformation) MathTransformFactory.pool.intern( new CoordinateTransformation(
                                                                                                          null,
                                                                                                          sourceCS,
                                                                                                          targetCS,
                                                                                                          type,
                                                                                                          transform ) );
    }

    /**
     * Returns an affine transform between two coordinate systems. Only units and
     * axis order (e.g. transforming from (NORTH,WEST) to (EAST,NORTH)) are taken
     * in account. Other attributes (especially the datum) must be checked before
     * invoking this method.
     *
     * @param sourceCS The source coordinate system. If <code>null</code>, then
     *        (x,y,z,t) axis order is assumed.
     * @param targetCS The target coordinate system. If <code>null</code>, then
     *        (x,y,z,t) axis order is assumed.
     */
    private Matrix swapAndScaleAxis( final CoordinateSystem sourceCS,
                                    final CoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        final AxisOrientation[] sourceAxis = getAxisOrientations( sourceCS, targetCS );
        final AxisOrientation[] targetAxis = getAxisOrientations( targetCS, sourceCS );
        final Matrix matrix;
        try {
            matrix = Matrix.createAffineTransform( sourceAxis, targetAxis );
        } catch ( RuntimeException exception ) {
            final CannotCreateTransformException e = new CannotCreateTransformException( sourceCS,
                                                                                         targetCS );
            throw e;
        }
        // Convert units (Optimized case where the conversion
        // can be applied right into the AffineTransform).
        final int dimension = matrix.getNumRow() - 1;
        for ( int i = 0; i < dimension; i++ ) {
            // TODO: check if units conversion is really linear.
            final Unit sourceUnit = sourceCS.getUnits( i );
            final Unit targetUnit = targetCS.getUnits( i );
            final double offset = targetUnit.convert( 0, sourceUnit );
            final double scale = targetUnit.convert( 1, sourceUnit ) - offset;
            matrix.setElement( i, i, scale * matrix.getElement( i, i ) );
            matrix.setElement( i, dimension, scale * matrix.getElement( i, dimension ) + offset );
        }
        return matrix;
    }

    /**
     * Returns an affine transform between two geographic coordinate systems. Only
     * units, axis order (e.g. transforming from (NORTH,WEST) to (EAST,NORTH)) and
     * prime meridian are taken in account. Other attributes (especially the datum)
     * must be checked before invoking this method.
     *
     * @param sourceCS The source coordinate system.
     * @param targetCS The target coordinate system.
     */
    private Matrix swapAndScaleGeoAxis( final GeographicCoordinateSystem sourceCS,
                                       final GeographicCoordinateSystem targetCS )
                            throws CannotCreateTransformException {
        final Matrix matrix = swapAndScaleAxis( sourceCS, targetCS );
        for ( int i = targetCS.getDimension(); --i >= 0; ) {
            // Find longitude ordinate, and apply a rotation if prime meridian are different.
            final AxisOrientation orientation = targetCS.getAxis( i ).orientation;
            if ( AxisOrientation.EAST.equals( orientation.absolute() ) ) {
                final Unit unit = targetCS.getUnits( i );
                final double sourceLongitude = sourceCS.getPrimeMeridian().getLongitude( unit );
                final double targetLongitude = targetCS.getPrimeMeridian().getLongitude( unit );
                final int lastMatrixColumn = matrix.getNumCol() - 1;
                double rotate = targetLongitude - sourceLongitude;
                if ( AxisOrientation.WEST.equals( orientation ) )
                    rotate = -rotate;
                matrix.setElement( i, lastMatrixColumn, matrix.getElement( i, lastMatrixColumn )
                                                        - rotate );
            }
        }
        return matrix;
    }

    /**
     * Returns the axis orientation for the specified coordinate system.
     * If <code>cs</code> is <code>null</code>, then an array of length
     * <code>dim.getDimension()</code> is created and filled with
     * <code>(x,y,z,t)</code> axis orientations.
     */
    private static AxisOrientation[] getAxisOrientations( final CoordinateSystem cs,
                                                         final Dimensioned dim ) {
        final AxisOrientation[] axis;
        if ( cs != null ) {
            axis = new AxisOrientation[cs.getDimension()];
            for ( int i = 0; i < axis.length; i++ )
                axis[i] = cs.getAxis( i ).orientation;
        } else {
            axis = new AxisOrientation[dim.getDimension()];
            switch ( axis.length ) {
            default:
                for ( int i = axis.length; --i >= 4; )
                    axis[i] = AxisOrientation.OTHER; // fall through
            case 4:
                axis[3] = AxisOrientation.FUTURE; // fall through
            case 3:
                axis[2] = AxisOrientation.UP; // fall through
            case 2:
                axis[1] = AxisOrientation.NORTH; // fall through
            case 1:
                axis[0] = AxisOrientation.EAST; // fall through
            case 0:
                break;
            }
        }
        return axis;
    }

    /**
     * Makes sure that the specified {@link GeographicCoordinateSystem} use standard axis
     * (longitude and latitude in degrees), Greenwich prime meridian and an ellipsoid
     * matching projection's parameters. If <code>cs</code> already meets all those
     * conditions, then it is returned unchanged. Otherwise, a new normalized geographic
     * coordinate system is created and returned.
     */
    private static GeographicCoordinateSystem normalize( GeographicCoordinateSystem cs,
                                                        final Projection projection ) {
        HorizontalDatum datum = cs.getHorizontalDatum();
        Ellipsoid ellipsoid = datum.getEllipsoid();
        final String name = getTemporaryName();
        final double semiMajorEll = ellipsoid.getSemiMajorAxis();
        final double semiMinorEll = ellipsoid.getSemiMinorAxis();
        final double semiMajorPrj = projection.getValue( "semi_major", semiMajorEll );
        final double semiMinorPrj = projection.getValue( "semi_minor", semiMinorEll );
        if ( !hasStandardAxis( cs, Unit.DEGREE )
             || cs.getPrimeMeridian().getLongitude( Unit.DEGREE ) != 0 ) {
            cs = null; // Signal that it needs to be reconstructed.
        }
        if ( semiMajorEll != semiMajorPrj || semiMinorEll != semiMinorPrj ) {
            // Those objects are temporary. We assume it is not
            // a big deal if their name are not very explicit...            
            ellipsoid = new Ellipsoid( name, semiMajorPrj, semiMinorPrj, Unit.METRE );
            datum = new HorizontalDatum( name, ellipsoid );
            cs = null; // Signal that it needs to be reconstructed.
        }
        if ( cs != null ) {
            return cs;
        }
        return new GeographicCoordinateSystem( name, datum );
    }

    /**
     * Makes sure that a {@link ProjectedCoordinateSystem} use standard axis
     * (x and y in metres) and a normalized {@link GeographicCoordinateSystem}.
     * If <code>cs</code> already meets all those conditions, then it is
     * returned unchanged. Otherwise, a new normalized projected coordinate
     * system is created and returned.
     */
    private static ProjectedCoordinateSystem normalize( final ProjectedCoordinateSystem cs ) {
        final Projection projection = cs.getProjection();
        final GeographicCoordinateSystem geoCS = cs.getGeographicCoordinateSystem();
        final GeographicCoordinateSystem normalizedGeoCS = normalize( geoCS, projection );

        if ( hasStandardAxis( cs, Unit.METRE ) && normalizedGeoCS == geoCS ) {
            return cs;
        }
        return new ProjectedCoordinateSystem( getTemporaryName(), normalizedGeoCS, projection );
    }

    /**
     * Returns <code>true</code> if the specified coordinate
     * system use standard axis and standard units.
     *
     * @param cs   The coordinate system to test.
     * @paral unit The standard units.
     */
    private static boolean hasStandardAxis( final HorizontalCoordinateSystem cs, final Unit unit ) {
        return cs.getDimension() == 2 /* Just a paranoiac check */
               && unit.equals( cs.getUnits( 0 ) ) && unit.equals( cs.getUnits( 1 ) )
               && AxisOrientation.EAST.equals( cs.getAxis( 0 ).orientation )
               && AxisOrientation.NORTH.equals( cs.getAxis( 1 ).orientation );
    }

    /**
     * Returns a temporary name for generated objects. The first object
     * has a name like "Temporary-1", the second is "Temporary-2", etc.
     *
     */
    private static String getTemporaryName() {
        return "Temporary-" + ( ++temporaryID );
    }

}
