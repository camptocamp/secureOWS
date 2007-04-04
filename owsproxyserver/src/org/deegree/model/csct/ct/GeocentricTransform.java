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

// OpenGIS dependencies (SEAGIS)
import java.io.Serializable;

import javax.media.jai.ParameterList;
import javax.media.jai.util.Range;

import org.deegree.model.csct.cs.Ellipsoid;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * Transforms three dimensional geographic points  to geocentric
 * coordinate points. Input points must be longitudes, latitudes
 * and heights above the ellipsoid.
 *
 * @version 1.00
 * @author Frank Warmerdam
 * @author Martin Desruisseaux
 */
class GeocentricTransform extends AbstractMathTransform implements Serializable {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -3352045463953828140L;

    /**
     * Cosine of 67.5 degrees.
     */
    private static final double COS_67P5 = 0.38268343236508977;

    /**
     * Toms region 1 constant.
     */
    private static final double AD_C = 1.0026000;

    /**
     * Semi-major axis of ellipsoid in meters.
     */
    private final double a;

    /**
     * Semi-minor axis of ellipsoid in meters.
     */
    private final double b;

    /**
     * Square of semi-major axis (@link #a}�).
     */
    private final double a2;

    /**
     * Square of semi-minor axis ({@link #b}�).
     */
    private final double b2;

    /**
     * Eccentricity squared.
     */
    private final double e2;

    /**
     * 2nd eccentricity squared.
     */
    private final double ep2;

    /**
     * <code>true</code> if geographic coordinates
     * include an ellipsoidal height (i.e. are 3-D),
     * or <code>false</code> if they are strictly 2-D.
     */
    private final boolean hasHeight;

    /**
     * The inverse of this transform.
     * Will be created only when needed.
     */
    private transient MathTransform inverse;

    /**
     * Construct a transform.
     *
     * @param ellipsoid The ellipsoid.
     * @param hasHeight <code>true</code> if geographic coordinates
     *                  include an ellipsoidal height (i.e. are 3-D),
     *                  or <code>false</code> if they are strictly 2-D.
     */
    protected GeocentricTransform( final Ellipsoid ellipsoid, final boolean hasHeight ) {
        this( ellipsoid.getSemiMajorAxis(), ellipsoid.getSemiMinorAxis(), ellipsoid.getAxisUnit(),
              hasHeight );
    }

    /**
     * Construct a transform.
     *
     * @param semiMajor The semi-major axis length.
     * @param semiMinor The semi-minor axis length.
     * @param units The axis units.
     * @param hasHeight <code>true</code> if geographic coordinates
     *                  include an ellipsoidal height (i.e. are 3-D),
     *                  or <code>false</code> if they are strictly 2-D.
     */
    protected GeocentricTransform( final double semiMajor, final double semiMinor,
                                  final Unit units, final boolean hasHeight ) {
        this.hasHeight = hasHeight;
        a = Unit.METRE.convert( semiMajor, units );
        b = Unit.METRE.convert( semiMinor, units );
        a2 = a * a;
        b2 = b * b;
        e2 = ( a2 - b2 ) / a2;
        ep2 = ( a2 - b2 ) / b2;
        checkArgument( "a", a, Double.MAX_VALUE );
        checkArgument( "b", b, a );
    }

    /**
     * Check an argument value. The argument must be greater
     * than 0 and finite, otherwise an exception is thrown.
     *
     * @param name  The argument name.
     * @param value The argument value.
     * @param max   The maximal legal argument value.
     */
    private static void checkArgument( final String name, final double value, final double max )
                            throws IllegalArgumentException {
        if ( !( value >= 0 && value <= max ) ) // Use '!' in order to trap NaN
        {
            throw new IllegalArgumentException(
                                                Resources.format(
                                                                  ResourceKeys.ERROR_ILLEGAL_ARGUMENT_$2,
                                                                  name, new Double( value ) ) );
        }
    }

    /**
     * Converts geodetic coordinates (longitude, latitude, height) to
     * geocentric coordinates (x, y, z) according to the current ellipsoid
     * parameters.
     */
    public void transform( double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts ) {
        transform( srcPts, srcOff, dstPts, dstOff, numPts, false );
    }

    /**
     * Implementation of geodetic to geocentric conversion. This
     * implementation allows the caller to use height in computation.
     */
    private void transform( final double[] srcPts, int srcOff, final double[] dstPts, int dstOff,
                           int numPts, boolean hasHeight ) {
        int step = 0;
        final int dimSource = getDimSource();
        hasHeight |= ( dimSource >= 3 );
        if ( srcPts == dstPts && srcOff < dstOff && srcOff + numPts * dimSource > dstOff ) {
            step = -dimSource;
            srcOff -= ( numPts - 1 ) * step;
            dstOff -= ( numPts - 1 ) * step;
        }
        while ( --numPts >= 0 ) {
            final double L = Math.toRadians( srcPts[srcOff++] ); // Longitude
            final double P = Math.toRadians( srcPts[srcOff++] ); // Latitude
            final double h = hasHeight ? srcPts[srcOff++] : 0; // Height above the ellipsoid (metres).

            final double cosLat = Math.cos( P );
            final double sinLat = Math.sin( P );
            final double rn = a / Math.sqrt( 1 - e2 * ( sinLat * sinLat ) );

            dstPts[dstOff++] = ( rn + h ) * cosLat * Math.cos( L ); // X
            dstPts[dstOff++] = ( rn + h ) * cosLat * Math.sin( L ); // Y
            dstPts[dstOff++] = ( rn * ( 1 - e2 ) + h ) * sinLat; // Z
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Converts geodetic coordinates (longitude, latitude, height) to
     * geocentric coordinates (x, y, z) according to the current ellipsoid
     * parameters.
     */
    public void transform( final float[] srcPts, int srcOff, final float[] dstPts, int dstOff,
                          int numPts ) {
        int step = 0;
        final int dimSource = getDimSource();
        final boolean hasHeight = ( dimSource >= 3 );
        if ( srcPts == dstPts && srcOff < dstOff && srcOff + numPts * dimSource > dstOff ) {
            step = -dimSource;
            srcOff -= ( numPts - 1 ) * step;
            dstOff -= ( numPts - 1 ) * step;
        }
        while ( --numPts >= 0 ) {
            final double L = Math.toRadians( srcPts[srcOff++] ); // Longitude
            final double P = Math.toRadians( srcPts[srcOff++] ); // Latitude
            final double h = hasHeight ? srcPts[srcOff++] : 0; // Height above the ellipsoid (metres).

            final double cosLat = Math.cos( P );
            final double sinLat = Math.sin( P );
            final double rn = a / Math.sqrt( 1 - e2 * ( sinLat * sinLat ) );

            dstPts[dstOff++] = (float) ( ( rn + h ) * cosLat * Math.cos( L ) ); // X
            dstPts[dstOff++] = (float) ( ( rn + h ) * cosLat * Math.sin( L ) ); // Y
            dstPts[dstOff++] = (float) ( ( rn * ( 1 - e2 ) + h ) * sinLat ); // Z
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Converts geocentric coordinates (x, y, z) to geodetic coordinates
     * (longitude, latitude, height), according to the current ellipsoid
     * parameters. The method used here is derived from "An Improved
     * Algorithm for Geocentric to Geodetic Coordinate Conversion", by
     * Ralph Toms, Feb 1996.
     */
    protected final void inverseTransform( final double[] srcPts, int srcOff,
                                          final double[] dstPts, int dstOff, int numPts ) {
        int step = 0;
        final int dimSource = getDimSource();
        final boolean hasHeight = ( dimSource >= 3 );
        boolean computeHeight = hasHeight;

        if ( srcPts == dstPts && srcOff < dstOff && srcOff + numPts * dimSource > dstOff ) {
            step = -dimSource;
            srcOff -= ( numPts - 1 ) * step;
            dstOff -= ( numPts - 1 ) * step;
        }
        while ( --numPts >= 0 ) {
            final double x = srcPts[srcOff++];
            final double y = srcPts[srcOff++];
            final double z = srcPts[srcOff++];

            // Note: The Java version of 'atan2' work correctly for x==0.
            //       No need for special handling like in the C version.
            //       No special handling neither for latitude. Formulas
            //       below are generic enough, considering that 'atan'
            //       work correctly with infinities (1/0).

            // Note: Variable names follow the notation used in Toms, Feb 1996
            final double W2 = x * x + y * y; // square of distance from Z axis
            final double W = Math.sqrt( W2 ); // distance from Z axis
            final double T0 = z * AD_C; // initial estimate of vertical component
            final double S0 = Math.sqrt( T0 * T0 + W2 ); // initial estimate of horizontal component
            final double sin_B0 = T0 / S0; // sin(B0), B0 is estimate of Bowring aux variable
            final double cos_B0 = W / S0; // cos(B0)
            final double sin3_B0 = sin_B0 * sin_B0 * sin_B0; // cube of sin(B0)
            final double T1 = z + b * ep2 * sin3_B0; // corrected estimate of vertical component
            final double sum = W - a * e2 * ( cos_B0 * cos_B0 * cos_B0 ); // numerator of cos(phi1)
            final double S1 = Math.sqrt( T1 * T1 + sum * sum ); // corrected estimate of horizontal component
            final double sin_p1 = T1 / S1; // sin(phi1), phi1 is estimated latitude
            final double cos_p1 = sum / S1; // cos(phi1)

            final double longitude = Math.toDegrees( Math.atan2( y, x ) );
            final double latitude = Math.toDegrees( Math.atan( sin_p1 / cos_p1 ) );
            final double height;

            dstPts[dstOff++] = longitude;
            dstPts[dstOff++] = latitude;
            if ( computeHeight ) {
                final double rn = a / Math.sqrt( 1 - e2 * ( sin_p1 * sin_p1 ) ); // Earth radius at location
                if ( cos_p1 >= +COS_67P5 )
                    height = W / +cos_p1 - rn;
                else if ( cos_p1 <= -COS_67P5 )
                    height = W / -cos_p1 - rn;
                else
                    height = z / sin_p1 + rn * ( e2 - 1.0 );
                if ( hasHeight ) {
                    dstPts[dstOff++] = height;
                }
            }
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Converts geocentric coordinates (x, y, z) to geodetic coordinates
     * (longitude, latitude, height), according to the current ellipsoid
     * parameters. The method used here is derived from "An Improved
     * Algorithm for Geocentric to Geodetic Coordinate Conversion", by
     * Ralph Toms, Feb 1996.
     */
    protected final void inverseTransform( final float[] srcPts, int srcOff, final float[] dstPts,
                                          int dstOff, int numPts ) {
        int step = 0;
        final int dimSource = getDimSource();
        final boolean hasHeight = ( dimSource >= 3 );
        boolean computeHeight = hasHeight;
        if ( srcPts == dstPts && srcOff < dstOff && srcOff + numPts * dimSource > dstOff ) {
            step = -dimSource;
            srcOff -= ( numPts - 1 ) * step;
            dstOff -= ( numPts - 1 ) * step;
        }
        while ( --numPts >= 0 ) {
            final double x = srcPts[srcOff++];
            final double y = srcPts[srcOff++];
            final double z = srcPts[srcOff++];

            // Note: The Java version of 'atan2' work correctly for x==0.
            //       No need for special handling like in the C version.
            //       No special handling neither for latitude. Formulas
            //       below are generic enough, considering that 'atan'
            //       work correctly with infinities (1/0).

            // Note: Variable names follow the notation used in Toms, Feb 1996
            final double W2 = x * x + y * y; // square of distance from Z axis
            final double W = Math.sqrt( W2 ); // distance from Z axis
            final double T0 = z * AD_C; // initial estimate of vertical component
            final double S0 = Math.sqrt( T0 * T0 + W2 ); // initial estimate of horizontal component
            final double sin_B0 = T0 / S0; // sin(B0), B0 is estimate of Bowring aux variable
            final double cos_B0 = W / S0; // cos(B0)
            final double sin3_B0 = sin_B0 * sin_B0 * sin_B0; // cube of sin(B0)
            final double T1 = z + b * ep2 * sin3_B0; // corrected estimate of vertical component
            final double sum = W - a * e2 * ( cos_B0 * cos_B0 * cos_B0 ); // numerator of cos(phi1)
            final double S1 = Math.sqrt( T1 * T1 + sum * sum ); // corrected estimate of horizontal component
            final double sin_p1 = T1 / S1; // sin(phi1), phi1 is estimated latitude
            final double cos_p1 = sum / S1; // cos(phi1)

            final double longitude = Math.toDegrees( Math.atan2( y, x ) );
            final double latitude = Math.toDegrees( Math.atan( sin_p1 / cos_p1 ) );
            final double height;

            dstPts[dstOff++] = (float) longitude;
            dstPts[dstOff++] = (float) latitude;
            if ( computeHeight ) {
                final double rn = a / Math.sqrt( 1 - e2 * ( sin_p1 * sin_p1 ) ); // Earth radius at location
                if ( cos_p1 >= +COS_67P5 )
                    height = W / +cos_p1 - rn;
                else if ( cos_p1 <= -COS_67P5 )
                    height = W / -cos_p1 - rn;
                else
                    height = z / sin_p1 + rn * ( e2 - 1.0 );
                if ( hasHeight ) {
                    dstPts[dstOff++] = (float) height;
                }
            }
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Gets the dimension of input points, which is 2 or 3.
     */
    public int getDimSource() {
        return hasHeight ? 3 : 2;
    }

    /**
     * Gets the dimension of output points, which is 3.
     */
    public final int getDimTarget() {
        return 3;
    }

    /**
     * Tests whether this transform does not move any points.
     * This method returns always <code>false</code>.
     */
    public final boolean isIdentity() {
        return false;
    }

    /**
     * Returns the inverse of this transform.
     */
    public synchronized MathTransform inverse() {
        if ( inverse == null )
            inverse = new Inverse();
        return inverse;
    }

    /**
     * Returns a hash value for this transform.
     */
    public final int hashCode() {
        final long code = Double.doubleToLongBits( a )
                          + 37
                          * ( Double.doubleToLongBits( b ) + 37 * ( Double.doubleToLongBits( a2 ) + 37 * ( Double.doubleToLongBits( b2 ) + 37 * ( Double.doubleToLongBits( e2 ) + 37 * ( Double.doubleToLongBits( ep2 ) ) ) ) ) );
        return (int) code ^ (int) ( code >>> 32 );
    }

    /**
     * Compares the specified object with
     * this math transform for equality.
     */
    public final boolean equals( final Object object ) {
        if ( object == this )
            return true; // Slight optimization
        if ( super.equals( object ) ) {
            final GeocentricTransform that = (GeocentricTransform) object;
            return Double.doubleToLongBits( this.a ) == Double.doubleToLongBits( that.a )
                   && Double.doubleToLongBits( this.b ) == Double.doubleToLongBits( that.b )
                   && Double.doubleToLongBits( this.a2 ) == Double.doubleToLongBits( that.a2 )
                   && Double.doubleToLongBits( this.b2 ) == Double.doubleToLongBits( that.b2 )
                   && Double.doubleToLongBits( this.e2 ) == Double.doubleToLongBits( that.e2 )
                   && Double.doubleToLongBits( this.ep2 ) == Double.doubleToLongBits( that.ep2 );
        }
        return false;
    }

    /**
     * Returns the WKT for this math transform.
     */
    public final String toString() {
        return toString( "Ellipsoid_To_Geocentric" );
    }

    /**
     * Returns the WKT for this math transform with the
     * specified classification name. The classification
     * name should be "Ellipsoid_To_Geocentric" or
     * "Geocentric_To_Ellipsoid".
     */
    final String toString( final String classification ) {
        final StringBuffer buffer = paramMT( classification );
        addParameter( buffer, "semi_major", a );
        addParameter( buffer, "semi_minor", b );
        buffer.append( ']' );
        return buffer.toString();
    }

    /**
     * Inverse of a geocentric transform.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    private final class Inverse extends AbstractMathTransform.Inverse {
        public Inverse() {
            GeocentricTransform.this.super();
        }

        public void transform( final double[] source, final int srcOffset, final double[] dest,
                              final int dstOffset, final int length )
                                throws TransformException {
            GeocentricTransform.this.inverseTransform( source, srcOffset, dest, dstOffset, length );
        }

        public void transform( final float[] source, final int srcOffset, final float[] dest,
                              final int dstOffset, final int length )
                                throws TransformException {
            GeocentricTransform.this.inverseTransform( source, srcOffset, dest, dstOffset, length );
        }

        public final String toString() {
            return GeocentricTransform.this.toString( "Geocentric_To_Ellipsoid" );
        }
    }

    /**
     * The provider for {@link GeocentricTransform}.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    static final class Provider extends MathTransformProvider {
        /**
         * The range of values for the dimension.
         */
        private static final Range DIM_RANGE = new Range( Integer.class, new Integer( 2 ),
                                                          new Integer( 3 ) );

        /**
         * <code>false</code> for the direct transform,
         * or <code>true</code> for the inverse transform.
         */
        private final boolean inverse;

        /**
         * Create a provider.
         *
         * @param inverse <code>false</code> for the direct transform,
         *                or <code>true</code> for the inverse transform.
         */
        public Provider( final boolean inverse ) {
            super( inverse ? "Geocentric_To_Ellipsoid" : "Ellipsoid_To_Geocentric",
                   0/*ResourceKeys.GEOCENTRIC_TRANSFORM*/, null ); // TODO
            put( "semi_major", Double.NaN, POSITIVE_RANGE );
            put( "semi_minor", Double.NaN, POSITIVE_RANGE );
            putInt( "dim_geoCS", 3, DIM_RANGE ); // Custom parameter: NOT AN OPENGIS SPECIFICATION
            this.inverse = inverse;
        }

        /**
         * Returns a transform for the specified parameters.
         *
         * @param  parameters The parameter values in standard units.
         * @return A {@link MathTransform} object of this classification.
         */
        public MathTransform create( final ParameterList parameters ) {
            final double semiMajor = parameters.getDoubleParameter( "semi_major" );
            final double semiMinor = parameters.getDoubleParameter( "semi_minor" );
            int dimGeographic = 3;
            try {
                dimGeographic = parameters.getIntParameter( "dim_geoCS" );
            } catch ( IllegalArgumentException exception ) {
                // the "dim_geoCS" parameter is a custom one required
                // by our SEAGIS implementation. It is NOT an OpenGIS
                // one. We can't require clients to know it.
            }
            GeocentricTransform transform = new GeocentricTransform( semiMajor, semiMinor,
                                                                     Unit.METRE, dimGeographic != 2 );
            return ( inverse ) ? transform.inverse() : transform;
        }
    }
}
