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
package org.deegree.model.csct.cs;

// OpenGIS dependencies
import java.awt.geom.Point2D;
import java.util.Map;


import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.XMath;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * The figure formed by the rotation of an ellipse about an axis.
 * In this context, the axis of rotation is always the minor axis. It is named geodetic
 * ellipsoid if the parameters are derived by the measurement of the shape and the size
 * of the Earth to approximate the geoid as close as possible.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_Ellipsoid
 */
public class Ellipsoid extends Info {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -1047804526105439230L;

    /**
     * WGS 1984 ellipsoid. This ellipsoid is used in GPS system
     * and is the default for most <code>org.deegree.model</code> packages.
     */
    public static final Ellipsoid WGS84 = (Ellipsoid) pool.intern( createFlattenedSphere(
                                                                                          "WGS84",
                                                                                          6378137.0,
                                                                                          298.257223563,
                                                                                          Unit.METRE ) );

    /**
     * The equatorial radius.
     */
    private final double semiMajorAxis;

    /**
     * The polar radius.
     */
    private final double semiMinorAxis;

    /**
     * The inverse of the flattening value, or
     * {@link Double#POSITIVE_INFINITY} if the
     * ellipsoid is a sphere.
     *
     */
    private final double inverseFlattening;

    /**
     * Is the Inverse Flattening definitive for this ellipsoid?
     *
     */
    private final boolean ivfDefinitive;

    /**
     * The units of the semi-major
     * and semi-minor axis values.
     */
    private final Unit unit;

    /**
     * Construct a new sphere using the specified radius.
     *
     * @param name   Name of this sphere.
     * @param radius The equatorial and polar radius.
     * @param unit   The units of the semi-major and semi-minor axis values.
     */
    public Ellipsoid( final String name, final double radius, final Unit unit ) {
        this( name, check( "radius", radius ), radius, Double.POSITIVE_INFINITY, false, unit );
    }

    /**
     * Construct a new ellipsoid using the specified axis length.
     *
     * @param name          Name of this ellipsoid.
     * @param semiMajorAxis The equatorial radius.
     * @param semiMinorAxis The polar radius.
     * @param unit          The units of the semi-major and semi-minor axis values.
     *
     */
    public Ellipsoid( final String name, final double semiMajorAxis, final double semiMinorAxis,
                     final Unit unit ) {
        this( name, semiMajorAxis, semiMinorAxis,
              semiMajorAxis / ( semiMajorAxis - semiMinorAxis ), false, unit );
    }

    /**
     * Construct a new ellipsoid using the specified axis length.
     *
     * @param name              Name of this ellipsoid.
     * @param semiMajorAxis     The equatorial radius.
     * @param semiMinorAxis     The polar radius.
     * @param inverseFlattening The inverse of the flattening value.
     * @param ivfDefinitive     Is the Inverse Flattening definitive for this ellipsoid?
     * @param unit              The units of the semi-major and semi-minor axis values.
     */
    private Ellipsoid( final String name, final double semiMajorAxis, final double semiMinorAxis,
                      final double inverseFlattening, final boolean ivfDefinitive, final Unit unit ) {
        super( name );
        this.unit = unit;
        this.semiMajorAxis = check( "semiMajorAxis", semiMajorAxis );
        this.semiMinorAxis = check( "semiMinorAxis", semiMinorAxis );
        this.inverseFlattening = check( "inverseFlattening", inverseFlattening );
        this.ivfDefinitive = ivfDefinitive;
        ensureNonNull( "unit", unit );
        ensureLinearUnit( unit );
    }

    /**
     * Construct a new ellipsoid using the specified axis length.
     *
     * @param properties        The set of properties (see {@link Info}).
     * @param semiMajorAxis     The equatorial radius.
     * @param semiMinorAxis     The polar radius.
     * @param inverseFlattening The inverse of the flattening value.
     * @param ivfDefinitive     Is the Inverse Flattening definitive for this ellipsoid?
     * @param unit              The units of the semi-major and semi-minor axis values.
     */
    Ellipsoid( final Map properties, final double semiMajorAxis, final double semiMinorAxis,
              final double inverseFlattening, final boolean ivfDefinitive, final Unit unit ) {
        super( properties );
        this.unit = unit;
        this.semiMajorAxis = semiMajorAxis;
        this.semiMinorAxis = semiMinorAxis;
        this.inverseFlattening = inverseFlattening;
        this.ivfDefinitive = ivfDefinitive;
        // Accept null values.
    }

    /**
     * Construct a new ellipsoid using the specified axis length
     * and inverse flattening value.
     *
     * @param name              Name of this ellipsoid.
     * @param semiMajorAxis     The equatorial radius.
     * @param inverseFlattening The inverse flattening value.
     * @param unit              The units of the semi-major and semi-minor axis values.
     *
     */
    public static Ellipsoid createFlattenedSphere( final String name, final double semiMajorAxis,
                                                  final double inverseFlattening, final Unit unit ) {
        return new Ellipsoid( name, semiMajorAxis, semiMajorAxis * ( 1 - 1 / inverseFlattening ),
                              inverseFlattening, true, unit );
    }

    /**
     * Check the argument validity. Argument
     * <code>value</code> should be greater
     * than zero.
     *
     * @param  name  Argument name.
     * @param  value Argument value.
     * @return <code>value</code>.
     * @throws IllegalArgumentException if <code>value</code> is not greater than 0.
     */
    private static double check( final String name, final double value )
                            throws IllegalArgumentException {
        if ( value > 0 )
            return value;
        throw new IllegalArgumentException(
                                            Resources.format(
                                                              ResourceKeys.ERROR_ILLEGAL_ARGUMENT_$2,
                                                              name, new Double( value ) ) );
    }

    /**
     * Gets the equatorial radius.
     * The returned length is expressed in this object's axis units.
     *
     * @see org.opengis.cs.CS_Ellipsoid#getSemiMajorAxis()
     */
    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    /**
     * Gets the polar radius.
     * The returned length is expressed in this object's axis units.
     *
     * @see org.opengis.cs.CS_Ellipsoid#getSemiMinorAxis()
     */
    public double getSemiMinorAxis() {
        return semiMinorAxis;
    }

    /**
     * The ratio of the distance between the center and a focus of the ellipse
     * to the length of its semimajor axis. The eccentricity can alternately be
     * computed from the equation: <code>e=sqrt(2f-f�)</code>.
     */
    public double getEccentricity() {
        final double f = 1 - getSemiMinorAxis() / getSemiMajorAxis();
        return Math.sqrt( 2 * f - f * f );
    }

    /**
     * Returns the value of the inverse of the flattening constant.
     * Flattening is a value used to indicate how closely an ellipsoid approaches a
     * spherical shape. The inverse flattening is related to the equatorial/polar
     * radius (<var>r<sub>e</sub></var> and <var>r<sub>p</sub></var> respectively)
     * by the formula <code>ivf=r<sub>e</sub>/(r<sub>e</sub>-r<sub>p</sub>)</code>.
     * For perfect spheres, this method returns {@link Double#POSITIVE_INFINITY}
     * (which is the correct value).
     *
     * @see org.opengis.cs.CS_Ellipsoid#getInverseFlattening()
     */
    public double getInverseFlattening() {
        return inverseFlattening;
    }

    /**
     * Is the Inverse Flattening definitive for this ellipsoid?
     * Some ellipsoids use the IVF as the defining value, and calculate the
     * polar radius whenever asked. Other ellipsoids use the polar radius to
     * calculate the IVF whenever asked. This distinction can be important to
     * avoid floating-point rounding errors.
     */
    public boolean isIvfDefinitive() {
        return ivfDefinitive;
    }

    /**
     * Returns an <em>estimation</em> of orthodromic distance between two geographic coordinates.
     * The orthodromic distance is the shortest distance between two points on a sphere's surface.
     * The orthodromic path is always on a great circle. An other possible distance measurement
     * is the loxodromic distance, which is a longer distance on a path with a constant direction
     * on the compas.
     *
     * @param  P1 Longitude and latitude of first point (in degrees).
     * @param  P2 Longitude and latitude of second point (in degrees).
     * @return The orthodromic distance (in the units of this ellipsoid).
     */
    public double orthodromicDistance( final Point2D P1, final Point2D P2 ) {
        return orthodromicDistance( P1.getX(), P1.getY(), P2.getX(), P2.getY() );
    }

    /**
     * Returns an <em>estimation</em> of orthodromic distance between two geographic coordinates.
     * The orthodromic distance is the shortest distance between two points on a sphere's surface.
     * The orthodromic path is always on a great circle. An other possible distance measurement
     * is the loxodromic distance, which is a longer distance on a path with a constant direction
     * on the compas.
     *
     * @param  x1 Longitude of first point (in degrees).
     * @param  y1 Latitude of first point (in degrees).
     * @param  x2 Longitude of second point (in degrees).
     * @param  y2 Latitude of second point (in degrees).
     * @return The orthodromic distance (in the units of this ellipsoid).
     */
    public double orthodromicDistance( double x1, double y1, double x2, double y2 ) {
        /*
         * Le calcul de la distance orthodromique sur une surface ellipso�dale est complexe,
         * sujet � des erreurs d'arrondissements et sans solution � proximit� des p�les.
         * Nous utilisont plut�t un calcul bas� sur une forme sph�rique de la terre. Un
         * programme en Fortran calculant les distances orthodromiques sur une surface
         * ellipso�dale peut �tre t�l�charg� � partir du site de NOAA:
         *
         *            ftp://ftp.ngs.noaa.gov/pub/pcsoft/for_inv.3d/source/
         */
        y1 = Math.toRadians( y1 );
        y2 = Math.toRadians( y2 );
        final double y = 0.5 * ( y1 + y2 );
        final double dx = Math.toRadians( Math.abs( x2 - x1 ) % 360 );
        double rho = Math.sin( y1 ) * Math.sin( y2 ) + Math.cos( y1 ) * Math.cos( y2 )
                     * Math.cos( dx );
        if ( rho > +1 )
            rho = +1; // Catch rounding error.
        if ( rho < -1 )
            rho = -1; // Catch rounging error.
        return Math.acos( rho )
               / XMath.hypot( Math.sin( y ) / getSemiMajorAxis(), Math.cos( y )
                                                                  / getSemiMinorAxis() );
        // 'hypot' calcule l'inverse du rayon **apparent** de la terre � la latitude 'y'.
    }

    /**
     * Returns the units of the semi-major
     * and semi-minor axis values.
     *
     * @see org.opengis.cs.CS_Ellipsoid#getAxisUnit()
     */
    public Unit getAxisUnit() {
        return unit;
    }

    /**
     * Compares the specified object with
     * this ellipsoid for equality.
     */
    public boolean equals( final Object object ) {
        if ( super.equals( object ) ) {
            final Ellipsoid that = (Ellipsoid) object;
            return this.ivfDefinitive == that.ivfDefinitive
                   && Double.doubleToLongBits( this.semiMajorAxis ) == Double.doubleToLongBits( that.semiMajorAxis )
                   && Double.doubleToLongBits( this.semiMinorAxis ) == Double.doubleToLongBits( that.semiMinorAxis )
                   && Double.doubleToLongBits( this.inverseFlattening ) == Double.doubleToLongBits( that.inverseFlattening )
                   && Utilities.equals( this.unit, that.unit );
        }
        return false;
    }

    /**
     * Returns a hash value for this ellipsoid.
     */
    public int hashCode() {
        final long longCode = Double.doubleToLongBits( getSemiMajorAxis() );
        return ( ( (int) ( longCode >>> 32 ) ) ^ (int) longCode ) + 37 * super.hashCode();
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        buffer.append( ", " );
        buffer.append( semiMajorAxis );
        buffer.append( ", " );
        buffer.append( Double.isInfinite( inverseFlattening ) ? 0 : inverseFlattening );
        return "SPHEROID";
    }

}
