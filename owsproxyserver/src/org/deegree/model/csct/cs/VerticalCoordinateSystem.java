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
import java.util.Map;


import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * A one-dimensional coordinate system suitable for vertical measurements.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_VerticalCoordinateSystem
 */
public class VerticalCoordinateSystem extends CoordinateSystem {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8629573233560414552L;

    /**
     * Default vertical coordinate system using ellipsoidal datum.
     * Ellipsoidal heights are measured along the normal to the
     * ellipsoid used in the definition of horizontal datum.
     */
    public static final VerticalCoordinateSystem ELLIPSOIDAL = (VerticalCoordinateSystem) pool.intern( new VerticalCoordinateSystem(
                                                                                                                                     "Ellipsoidal",
                                                                                                                                     VerticalDatum.ELLIPSOIDAL ) );

    /**
     * The vertical datum.
     */
    private final VerticalDatum datum;

    /**
     * Units used along the vertical axis.
     */
    private final Unit unit;

    /**
     * Axis details for vertical dimension within coordinate system.
     */
    private final AxisInfo axis;

    /**
     * Creates a vertical coordinate system from a datum. Units
     * will be metres and values will be increasing upward.
     *
     * @param name  Name to give new object.
     * @param datum Datum to use for new coordinate system.
     */
    public VerticalCoordinateSystem( final String name, final VerticalDatum datum ) {
        this( name, datum, Unit.METRE, AxisInfo.ALTITUDE );
    }

    /**
     * Creates a vertical coordinate system from a datum and linear units.
     *
     * @param name  Name to give new object.
     * @param datum Datum to use for new coordinate system.
     * @param unit  Units to use for new coordinate system.
     * @param axis  Axis to use for new coordinate system.
     *
     */
    public VerticalCoordinateSystem( final String name, final VerticalDatum datum, final Unit unit,
                                    final AxisInfo axis ) {
        super( name );
        this.datum = datum;
        this.unit = unit;
        this.axis = axis;
        ensureNonNull( "datum", datum );
        ensureNonNull( "unit", unit );
        ensureNonNull( "axis", axis );
        ensureLinearUnit( unit );
        checkAxis( datum.getDatumType() );
    }

    /**
     * Creates a vertical coordinate system from a datum and linear units.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param datum Datum to use for new coordinate system.
     * @param unit  Units to use for new coordinate system.
     * @param axis  Axis to use for new coordinate system.
     */
    VerticalCoordinateSystem( final Map properties, final VerticalDatum datum, final Unit unit,
                             final AxisInfo axis ) {
        super( properties );
        this.datum = datum;
        this.unit = unit;
        this.axis = axis;
        // Accept null values.
    }

    /**
     * Returns the dimension of this coordinate system, which is 1.
     *
     * @see org.opengis.cs.CS_VerticalCoordinateSystem#getDimension()
     */
    public final int getDimension() {
        return 1;
    }

    /**
     * Override {@link CoordinateSystem#getDatum()}.
     */
    final Datum getDatum() {
        return getVerticalDatum();
    }

    /**
     * Gets the vertical datum, which indicates the measurement method.
     *
     * @see org.opengis.cs.CS_VerticalCoordinateSystem#getVerticalDatum()
     */
    public VerticalDatum getVerticalDatum() {
        return datum;
    }

    /**
     * Gets axis details for vertical dimension within coordinate system.
     * A vertical coordinate system have only one axis, always at index 0.
     *
     * @param dimension Zero based index of axis.
     *
     * @see org.opengis.cs.CS_VerticalCoordinateSystem#getAxis(int)
     */
    public AxisInfo getAxis( final int dimension ) {
        final int maxDim = getDimension();
        if ( dimension >= 0 && dimension < maxDim )
            return axis;
        throw new IndexOutOfBoundsException(
                                             Resources.format(
                                                               ResourceKeys.ERROR_INDEX_OUT_OF_BOUNDS_$1,
                                                               new Integer( dimension ) ) );
    }

    /**
     * Gets units for dimension within coordinate system.
     * A vertical coordinate system have only one unit,
     * always at index 0.
     *
     * @param dimension Must be 0.
     *
     * @see org.opengis.cs.CS_VerticalCoordinateSystem#getUnits(int)
     * @see org.opengis.cs.CS_VerticalCoordinateSystem#getVerticalUnit()
     */
    public Unit getUnits( final int dimension ) {
        final int maxDim = getDimension();
        if ( dimension >= 0 && dimension < maxDim )
            return unit;
        throw new IndexOutOfBoundsException(
                                             Resources.format(
                                                               ResourceKeys.ERROR_INDEX_OUT_OF_BOUNDS_$1,
                                                               new Integer( dimension ) ) );
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
            final VerticalCoordinateSystem that = (VerticalCoordinateSystem) cs;
            return Utilities.equals( this.datum, that.datum )
                   && Utilities.equals( this.unit, that.unit )
                   && Utilities.equals( this.axis, that.axis );
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
        addUnit( buffer, unit );
        buffer.append( ", " );
        buffer.append( axis );
        return "VERT_CS";
    }

}
