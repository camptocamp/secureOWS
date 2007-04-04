/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
package org.deegree.model.csct.ct;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Locale;

import org.deegree.model.csct.cs.Projection;
import org.deegree.model.csct.pt.Latitude;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * Projections de Mercator tranverses Universelle et Modifi�e. Il s'agit de la projection
 * Mercator cylindrique, mais dans lequel le cylindre a subit une rotation de 90�. Au lieu
 * d'�tre tangeant � l'�quateur (ou � une autre latitude standard), le cylindre de la
 * projection tranverse est tangeant � un m�ridien central. Les d�formation deviennent
 * de plus en plus importantes � mesure que l'on s'�loigne du m�ridien central. Cette
 * projection est appropri�e pour les r�gions qui s'�tendent d'avantage dans le sens
 * nord-sud que dans le sens est-ouest.
 *
 * R�f�rence: John P. Snyder (Map Projections - A Working Manual,
 *            U.S. Geological Survey Professional Paper 1395, 1987)
 *
 * @version 1.0
 * @author Andr� Gosselin
 * @author Martin Desruisseaux
 */
final class TransverseMercatorProjection extends CylindricalProjection {
    /*
     * Constants used to calculate {@link #en0}, {@link #en1},
     * {@link #en2}, {@link #en3}, {@link #en4}.
     */
    private static final double C00 = 1.0, C02 = 0.25, C04 = 0.046875, C06 = 0.01953125,
                            C08 = 0.01068115234375, C22 = 0.75, C44 = 0.46875,
                            C46 = 0.01302083333333333333, C48 = 0.00712076822916666666,
                            C66 = 0.36458333333333333333, C68 = 0.00569661458333333333,
                            C88 = 0.3076171875;

    /*
     * Contants used for the forward and inverse transform for the eliptical
     * case of the Transverse Mercator.
     */
    private static final double FC1 = 1.00000000000000000000000, // 1/1
                            FC2 = 0.50000000000000000000000, // 1/2
                            FC3 = 0.16666666666666666666666, // 1/6
                            FC4 = 0.08333333333333333333333, // 1/12
                            FC5 = 0.05000000000000000000000, // 1/20
                            FC6 = 0.03333333333333333333333, // 1/30
                            FC7 = 0.02380952380952380952380, // 1/42
                            FC8 = 0.01785714285714285714285; // 1/56   

    /**
     * Relative precisions.
     */
    private static final double EPS10 = 1e-10;

    /**
     * Relative precisions.
     */
    private static final double EPS11 = 1e-11;

    /**
     * scale factor for semi mayor axis
     */
    private double scale_factor = 1.0;

    /**
     * Global scale factor. Value <code>ak0</code>
     * is equals to <code>{@link #a}*k0</code>.
     */
    private final double ak0;

    /**
     * Constant needed for the <code>mlfn<code> method.
     * Setup at construction time.
     */
    private final double en0, en1, en2, en3, en4;

    /**
     * Variante de l'eccentricit�, calcul�e par
     * <code>e'� = (a�-b�)/b� = es/(1-es)</code>.
     */
    private final double esp;

    /**
     * indicates if the projection should be performed for the north hemisphere 
     * (1) or the south hemisphere (-1)
     */
    private int hemisphere = 1;

    /**
     * Construct a new map projection from the suplied parameters.
     * Projection will default to Universal Transverse Mercator (UTM).
     *
     * @param  parameters The parameter values in standard units.
     * @throws MissingParameterException if a mandatory parameter is missing.
     */
    protected TransverseMercatorProjection( final Projection parameters )
                            throws MissingParameterException {
        this( parameters, false );
    } // Default to UTM.

    /**
     * Construct a new map projection from the suplied parameters.
     *
     * @param  parameters The parameter values in standard units.
     * @param  modified <code>true</code> for MTM, <code>false</code> for UTM.
     * @throws MissingParameterException if a mandatory parameter is missing.
     */
    protected TransverseMercatorProjection( final Projection parameters, final boolean modified )
                            throws MissingParameterException {

        super( parameters );

        String[] param = parameters.getParameters().getParameterListDescriptor().getParamNames();
        ArrayList params = new ArrayList();

        for ( int i = 0; i < param.length; i++ ) {
            params.add( param[i] );
        }

        hemisphere = (int) parameters.getValue( "hemisphere", 1 );

        scale_factor = 1.0;

        if ( params.contains( "scale_factor" ) ) {
            scale_factor = parameters.getParameters().getDoubleParameter( "scale_factor" );
        }

        ak0 = a * scale_factor;

        double t;
        esp = ( ( a * a ) / ( b * b ) ) - 1.0;
        en0 = C00 - ( es * ( C02 + es * ( C04 + es * ( C06 + es * C08 ) ) ) );
        en1 = es * ( C22 - es * ( C04 + es * ( C06 + es * C08 ) ) );
        en2 = ( t = es * es ) * ( C44 - es * ( C46 + es * C48 ) );
        en3 = ( t *= es ) * ( C66 - es * C68 );
        en4 = t * es * C88;
    }

    /**
     * Returns a human readable name localized for the specified locale.
     */
    public String getName( final Locale locale ) {
        return "TransverseMercatorProjection";
    } //Resources.getResources(locale).getString(key);

    /**
     * Calcule la distance m�ridionale sur un
     * ellipso�de � la latitude <code>phi</code>.
     */
    private final double mlfn( final double phi, double sphi, double cphi ) {
        cphi *= sphi;
        sphi *= sphi;
        return ( en0 * phi ) - ( cphi * ( en1 + sphi * ( en2 + sphi * ( en3 + sphi * en4 ) ) ) );
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinate
     * and stores the result in <code>ptDst</code>.
     */
    protected Point2D transform( double x, double y, final Point2D ptDst )
                            throws TransformException {

        if ( Math.abs( y ) > ( Math.PI / 2.0 - EPS ) ) {
            throw new TransformException( Resources.format( ResourceKeys.ERROR_POLE_PROJECTION_$1,
                                                            new Latitude( Math.toDegrees( y ) ) ) );
        }

        x -= centralMeridian;
        y -= centralLatitude;
        y *= hemisphere;

        double sinphi = Math.sin( y );
        double cosphi = Math.cos( y );

        if ( isSpherical ) {
            // Spherical model.
            double b = cosphi * Math.sin( x );

            if ( Math.abs( Math.abs( b ) - 1.0 ) <= EPS10 ) {
                throw new TransformException(
                                              Resources.format( ResourceKeys.ERROR_VALUE_TEND_TOWARD_INFINITY ) );
            }

            double yy = ( cosphi * Math.cos( x ) ) / Math.sqrt( 1.0 - ( b * b ) );
            x = ( 0.5 * ak0 * Math.log( ( 1.0 + b ) / ( 1.0 - b ) ) ) + false_easting;/* 8-1 */

            if ( ( b = Math.abs( yy ) ) >= 1.0 ) {
                if ( ( b - 1.0 ) > EPS10 ) {
                    throw new TransformException(
                                                  Resources.format( ResourceKeys.ERROR_VALUE_TEND_TOWARD_INFINITY ) );
                }
                yy = 0.0;

            } else {
                yy = Math.acos( yy );
            }

            if ( y < 0 ) {
                yy = -yy;
            }

            y = ak0 * yy;
        } else {

            // Ellipsoidal model.
            double t = ( Math.abs( cosphi ) > EPS10 ) ? ( sinphi / cosphi ) : 0;
            t *= t;

            double al = cosphi * x;
            double als = al * al;
            al /= Math.sqrt( 1.0 - ( es * sinphi * sinphi ) );

            double n = esp * cosphi * cosphi;

            /* NOTE: meridinal distance at central latitude is always 0 */
            y = ( ak0 * ( mlfn( y, sinphi, cosphi ) + sinphi
                                                      * al
                                                      * x
                                                      * FC2
                                                      * ( 1.0 + FC4
                                                                * als
                                                                * ( 5.0 - t
                                                                    + ( n * ( 9.0 + 4.0 * n ) ) + FC6
                                                                                                  * als
                                                                                                  * ( 61.0
                                                                                                      + ( t * ( t - 58.0 ) )
                                                                                                      + ( n * ( 270.0 - 330.0 * t ) ) + FC8
                                                                                                                                        * als
                                                                                                                                        * ( 1385.0 + t
                                                                                                                                                     * ( t
                                                                                                                                                         * ( 543.0 - t ) - 3111.0 ) ) ) ) ) ) );
            y += false_northing;

            x = ( ak0 * al * ( FC1 + FC3
                                     * als
                                     * ( 1.0 - t + n + FC5
                                                       * als
                                                       * ( 5.0 + ( t * ( t - 18.0 ) )
                                                           + ( n * ( 14.0 - 58.0 * t ) ) + FC7
                                                                                           * als
                                                                                           * ( 61.0 + t
                                                                                                      * ( t
                                                                                                          * ( 179.0 - t ) - 479.0 ) ) ) ) ) );
            x += false_easting;
        }

        if ( ptDst != null ) {
            ptDst.setLocation( x, y );
            return ptDst;
        }
        return new Point2D.Double( x, y );

    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinate
     * and stores the result in <code>ptDst</code>.
     */
    protected Point2D inverseTransform( double x, double y, final Point2D ptDst )
                            throws TransformException {
        x -= false_easting;
        y -= false_northing;
        y *= hemisphere;
        //x = x - ( (int)( x / 1000000 ) * 1000000.0 );

        if ( isSpherical ) {
            // Spherical model.
            double t = Math.exp( x / ak0 );
            double d = 0.5 * ( t - 1 / t );
            t = Math.cos( y / ak0 );

            double phi = Math.asin( Math.sqrt( ( 1.0 - t * t ) / ( 1.0 + d * d ) ) );
            y = ( y < 0.0 ) ? ( -phi ) : phi;
            x = ( Math.abs( d ) > EPS10 || Math.abs( t ) > EPS10 ) ? ( Math.atan2( d, t ) + centralMeridian )
                                                                  : centralMeridian;
        } else {
            // Ellipsoidal projection.
            final double y_ak0 = y / ak0;
            final double k = 1.0 - es;
            double phi = y_ak0;

            for ( int i = 20; true; ) // rarely goes over 5 iterations
            {
                if ( ( --i ) < 0 ) {
                    throw new TransformException(
                                                  Resources.format( ResourceKeys.ERROR_NO_CONVERGENCE ) );
                }

                final double s = Math.sin( phi );
                double t = 1.0 - ( es * ( s * s ) );
                t = ( mlfn( phi, s, Math.cos( phi ) ) - y_ak0 ) / ( k * t * Math.sqrt( t ) );
                phi -= t;

                if ( Math.abs( t ) < EPS11 ) {
                    break;
                }
            }

            if ( Math.abs( phi ) >= ( Math.PI / 2.0 ) ) {
                y = ( y < 0.0 ) ? ( -( Math.PI / 2.0 ) ) : ( Math.PI / 2.0 );
                x = centralMeridian;
            } else {
                double sinphi = Math.sin( phi );
                double cosphi = Math.cos( phi );
                double t = ( Math.abs( cosphi ) > EPS10 ) ? ( sinphi / cosphi ) : 0.0;
                double n = esp * cosphi * cosphi;
                double con = 1.0 - ( es * sinphi * sinphi );
                double d = ( x * Math.sqrt( con ) ) / ak0;
                con *= t;
                t *= t;

                double ds = d * d;
                y = phi
                    - ( ( con * ds / ( 1.0 - es ) ) * FC2 * ( 1.0 - ds
                                                                    * FC4
                                                                    * ( 5.0
                                                                        + ( t * ( 3.0 - 9.0 * n ) )
                                                                        + ( n * ( 1.0 - 4 * n ) ) - ds
                                                                                                    * FC6
                                                                                                    * ( 61.0
                                                                                                        + ( t * ( 90.0 - ( 252.0 * n ) + 45.0 * t ) )
                                                                                                        + ( 46.0 * n ) - ds
                                                                                                                         * FC8
                                                                                                                         * ( 1385.0 + t
                                                                                                                                      * ( 3633.0 + t
                                                                                                                                                   * ( 4095.0 + 1574.0 * t ) ) ) ) ) ) )
                    + centralLatitude;

                x = ( ( d * ( FC1 - ds
                                    * FC3
                                    * ( 1.0 + ( 2.0 * t ) + n - ds
                                                                * FC5
                                                                * ( 5.0
                                                                    + ( t * ( 28.0 + ( 24 * t ) + 8.0 * n ) )
                                                                    + ( 6.0 * n ) - ds
                                                                                    * FC7
                                                                                    * ( 61.0 + t
                                                                                               * ( 662.0 + t
                                                                                                           * ( 1320.0 + 720.0 * t ) ) ) ) ) ) ) / cosphi )
                    + centralMeridian;
            }
        }

        if ( ptDst != null ) {
            ptDst.setLocation( x, y );
            return ptDst;
        }
        return new Point2D.Double( x, y );

    }

    /**
     * Returns a hash value for this projection.
     */
    public int hashCode() {
        final long code = Double.doubleToLongBits( false_easting );
        return ( (int) code ^ (int) ( code >>> 32 ) ) + ( 37 * super.hashCode() );
    }

    /**
     * Compares the specified object with
     * this map projection for equality.
     */
    public boolean equals( final Object object ) {
        if ( object == this ) {
            return true; // Slight optimization
        }

        if ( super.equals( object ) ) {
            final TransverseMercatorProjection that = (TransverseMercatorProjection) object;
            return ( Double.doubleToLongBits( this.false_easting ) == Double.doubleToLongBits( that.false_easting ) )
                   && ( Double.doubleToLongBits( this.ak0 ) == Double.doubleToLongBits( that.ak0 ) );
        }

        return false;
    }

    /**
     * Impl�mentation de la partie entre crochets
     * de la cha�ne retourn�e par {@link #toString()}.
     */
    void toString( final StringBuffer buffer ) {
        super.toString( buffer );
    }

    /**
     * Informations about a {@link TransverseMercatorProjection}.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    static final class Provider extends MapProjection.Provider {
        /**
         * <code>true</code> for Modified Mercator Projection (MTM), or
         * <code>false</code> for Universal Mercator Projection (UTM).
         */
        private final boolean modified;

        /**
         * Construct a new registration.
         *
         * @param modified <code>true</code> for Modified Mercator Projection (MTM),
         *       or <code>false</code> for Universal Mercator Projection (UTM).
         */
        public Provider( final boolean modified ) {
            super( modified ? "Modified_Transverse_Mercator" : "Transverse_Mercator",
                   modified ? ResourceKeys.MTM_PROJECTION : ResourceKeys.UTM_PROJECTION );
            this.modified = modified;
        }

        /**
         * Create a new map projection.
         */
        protected Object create( final Projection parameters ) {
            return new TransverseMercatorProjection( parameters, modified );
        }
    }
}