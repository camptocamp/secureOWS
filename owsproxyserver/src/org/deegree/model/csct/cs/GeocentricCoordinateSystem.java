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
import java.util.Arrays;
import java.util.Map;


import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * A 3D coordinate system, with its origin at the center of the Earth.
 * The <var>X</var> axis points towards the prime meridian.
 * The <var>Y</var> axis points East or West.
 * The <var>Z</var> axis points North or South. By default the
 * <var>Z</var> axis will point North, and the <var>Y</var> axis
 * will point East (e.g. a right handed system), but you should
 * check the axes for non-default values.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_GeocentricCoordinateSystem
 */
public class GeocentricCoordinateSystem extends CoordinateSystem {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -6577810243397267703L;

    /**
     * The set of default axis orientation.
     * The <var>X</var> axis points towards the prime meridian.
     * The <var>Y</var> axis points East.
     * The <var>Z</var> axis points North.
     */
    private static final AxisInfo[] DEFAULT_AXIS = new AxisInfo[] {
                                                                   new AxisInfo(
                                                                                 "x",
                                                                                 AxisOrientation.OTHER ),
                                                                   new AxisInfo(
                                                                                 "y",
                                                                                 AxisOrientation.EAST ),
                                                                   new AxisInfo(
                                                                                 "z",
                                                                                 AxisOrientation.NORTH ) };

    /**
     * The default geocentric coordinate system. Prime meridian is Greenwich,
     * horizontal datum in WGS84 and linear units are metre.
     * The <var>X</var> axis points towards the prime meridian.
     * The <var>Y</var> axis points East.
     * The <var>Z</var> axis points North.
     */
    public static final GeocentricCoordinateSystem DEFAULT = (GeocentricCoordinateSystem) pool.intern( new GeocentricCoordinateSystem(
                                                                                                                                       "WGS84",
                                                                                                                                       Unit.METRE,
                                                                                                                                       HorizontalDatum.WGS84,
                                                                                                                                       PrimeMeridian.GREENWICH,
                                                                                                                                       DEFAULT_AXIS ) );

    /**
     * The linear unit.
     */
    private final Unit unit;

    /**
     * The horizontal datum.
     */
    private final HorizontalDatum datum;

    /**
     * The prime meridian.
     */
    private final PrimeMeridian meridian;

    /**
     * The axis infos.
     */
    private final AxisInfo[] axis;

    /**
     * Construct a geocentric coordinate system with default
     * axis. Unit are metres and prime meridian is greenwich.
     *
     * @param name  The coordinate system name.
     * @param datum The horizontal datum.
     */
    public GeocentricCoordinateSystem( final String name, final HorizontalDatum datum ) {
        this( name, Unit.METRE, datum, PrimeMeridian.GREENWICH );
    }

    /**
     * Construct a geocentric coordinate system with default axis.
     * The <var>X</var> axis points towards the prime meridian.
     * The <var>Y</var> axis points East.
     * The <var>Z</var> axis points North.
     *
     * @param name     The coordinate system name.
     * @param unit     The linear unit.
     * @param datum    The horizontal datum.
     * @param meridian The prime meridian.
     */
    public GeocentricCoordinateSystem( final String name, final Unit unit,
                                      final HorizontalDatum datum, final PrimeMeridian meridian ) {
        this( name, unit, datum, meridian, DEFAULT_AXIS );
    }

    /**
     * Construct a geocentric coordinate system.
     *
     * @param name     The coordinate system name.
     * @param unit     The linear unit.
     * @param datum    The horizontal datum.
     * @param meridian The prime meridian.
     * @param axis     The axis info. This is usually an array of lenght 3.
     */
    public GeocentricCoordinateSystem( final String name, final Unit unit,
                                      final HorizontalDatum datum, final PrimeMeridian meridian,
                                      final AxisInfo[] axis ) {
        super( name );
        this.unit = unit;
        this.datum = datum;
        this.meridian = meridian;
        ensureNonNull( "axis", axis );
        ensureNonNull( "unit", unit );
        ensureNonNull( "datum", datum );
        ensureNonNull( "meridian", meridian );
        ensureLinearUnit( unit );
        this.axis = clone( axis );
    }

    /**
     * Construct a geocentric coordinate system.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param unit       The linear unit.
     * @param datum      The horizontal datum.
     * @param meridian   The prime meridian.
     * @param axis       The axis info. This is usually an array of lenght 3.
     */
    GeocentricCoordinateSystem( final Map properties, final Unit unit, final HorizontalDatum datum,
                               final PrimeMeridian meridian, final AxisInfo[] axis ) {
        super( properties );
        this.unit = unit;
        this.datum = datum;
        this.meridian = meridian;
        this.axis = clone( axis );
    }

    /**
     * Clone the specified axis array.
     */
    private static AxisInfo[] clone( final AxisInfo[] axis ) {
        return Arrays.equals( axis, DEFAULT_AXIS ) ? DEFAULT_AXIS : (AxisInfo[]) axis.clone();
    }

    /**
     * Returns the dimension of this coordinate system, which is usually 3.
     *
     * @see org.opengis.cs.CS_GeocentricCoordinateSystem#getDimension()
     */
    public int getDimension() {
        return axis.length;
    }

    /**
     * Override {@link CoordinateSystem#getDatum()}.
     */
    final Datum getDatum() {
        return getHorizontalDatum();
    }

    /**
     * Returns the horizontal datum.
     * The horizontal datum is used to determine where the center of the Earth
     * is considered to be. All coordinate points will be measured from the
     * center of the Earth, and not the surface.
     *
     * @see org.opengis.cs.CS_GeocentricCoordinateSystem#getHorizontalDatum()
     */
    public HorizontalDatum getHorizontalDatum() {
        return datum;
    }

    /**
     * Gets units for dimension within coordinate system.
     * For a <code>GeocentricCoordinateSystem</code>, the
     * units is the same for all axis.
     *
     * @param dimension Zero based index of axis.
     *
     * @see org.opengis.cs.CS_GeocentricCoordinateSystem#getUnits(int)
     */
    public Unit getUnits( final int dimension ) {
        if ( dimension >= 0 && dimension < getDimension() )
            return unit;
        throw new IndexOutOfBoundsException(
                                             Resources.format(
                                                               ResourceKeys.ERROR_INDEX_OUT_OF_BOUNDS_$1,
                                                               new Integer( dimension ) ) );
    }

    /**
     * Gets axis details for dimension within coordinate system.
     * Each dimension in the coordinate system has a corresponding axis.
     *
     * @param dimension Zero based index of axis.
     *
     * @see org.opengis.cs.CS_CoordinateSystem#getAxis(int)
     */
    public AxisInfo getAxis( final int dimension ) {
        return axis[dimension];
    }

    /**
     * Returns the prime meridian.
     *
     * @see org.opengis.cs.CS_GeocentricCoordinateSystem#getPrimeMeridian()
     */
    public PrimeMeridian getPrimeMeridian() {
        return meridian;
    }

    /**
     * Returns  <code>true</code> if this coordinate system is equivalents to
     * the specified coordinate system. Two coordinate systems are considered
     * equivalent if the {@link org.deegree.model.csct.ct.CoordinateTransformation} from
     * <code>this</code> to <code>cs</code> would be the identity transform.
     * The default implementation compare datum, units and axis, but ignore
     * name, alias and other meta-data informations.
     *
     * @param  cs The coordinate system (may be <code>null</code>).
     * @return <code>true</code> if both coordinate systems are equivalent.
     */
    public boolean equivalents( final CoordinateSystem cs ) {
        if ( cs == this )
            return true;
        if ( super.equivalents( cs ) ) {
            final GeocentricCoordinateSystem that = (GeocentricCoordinateSystem) cs;
            return Utilities.equals( this.unit, that.unit )
                   && Utilities.equals( this.datum, that.datum )
                   && Utilities.equals( this.meridian, that.meridian );
        }
        return false;
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        buffer.append( ", " );
        buffer.append( datum );
        buffer.append( ", " );
        buffer.append( meridian );
        buffer.append( ", " );
        addUnit( buffer, unit );
        for ( int i = 0; i < axis.length; i++ ) {
            buffer.append( ", " );
            buffer.append( axis[i] );
        }
        return "GEOCCS";
    }

}
