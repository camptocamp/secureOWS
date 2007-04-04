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

import javax.media.jai.ParameterList;

import org.deegree.model.csct.ct.MissingParameterException;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * A 2D cartographic coordinate system. Projected coordinates are the two-dimensional
 * cartesian coordinates typically found on maps and computer displays. The cartesian
 * axes are often called "paper coordinates" or "display coordinates." The conversions
 * from a three-dimensional curvilinear coordinate system (whether ellipsoidal or spherical)
 * to projected coordinates may be assumed to be well known. Examples of projected coordinate
 * systems are: Lambert, Mercator, and transverse Mercator. Conversions to, and conversions
 * between, projected spatial coordinate systems often do not preserve distances, areas and angles.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_ProjectedCoordinateSystem
 */
public class ProjectedCoordinateSystem extends HorizontalCoordinateSystem {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 5412822472156531329L;

    /**
     * The angular unit.
     */
    private final Unit unit;

    /**
     * Geographic coordinate system to base projection on.
     */
    private final GeographicCoordinateSystem gcs;

    /**
     * projection Projection from geographic to projected coordinate system.
     */
    private final Projection projection;

    /**
     * Creates a projected coordinate system using the specified geographic
     * system. Projected coordinates will be in meters, <var>x</var> values
     * increasing east and <var>y</var> values increasing north.
     *
     * @param  name Name to give new object.
     * @param  gcs Geographic coordinate system to base projection on.
     * @param  projection Projection from geographic to projected coordinate system.
     */
    public ProjectedCoordinateSystem( final String name, final GeographicCoordinateSystem gcs,
                                     final Projection projection ) {
        this( name, gcs, projection, Unit.METRE, AxisInfo.X, AxisInfo.Y );
    }

    /**
     * Creates a projected coordinate system using a projection object.
     *
     * @param  name Name to give new object.
     * @param  gcs Geographic coordinate system to base projection on.
     * @param  projection Projection from geographic to projected coordinate system.
     * @param  unit Linear units of created PCS.
     * @param  axis0 Details of 0th ordinates in created PCS coordinates.
     * @param  axis1 Details of 1st ordinates in created PCS coordinates.
     *
     */
    public ProjectedCoordinateSystem( final String name, final GeographicCoordinateSystem gcs,
                                     Projection projection, final Unit unit, final AxisInfo axis0,
                                     final AxisInfo axis1 ) {
        super( name, gcs.getHorizontalDatum(), axis0, axis1 );
        ensureNonNull( "gcs", gcs );
        ensureNonNull( "projection", projection );
        ensureNonNull( "unit", unit );
        ensureLinearUnit( unit );

        final Ellipsoid ellipsoid = getHorizontalDatum().getEllipsoid();
        final double semiMajor = ellipsoid.getSemiMajorAxis();
        final double semiMinor = ellipsoid.getSemiMinorAxis();
        String invalidParameter = null;
        boolean resetAxisLength = false;
        try {
            if ( semiMinor != projection.getValue( "semi_minor" ) )
                invalidParameter = "semi_minor";
        } catch ( MissingParameterException exception ) {
            // Axis length not set.
            resetAxisLength = true;
        }
        try {
            if ( semiMajor != projection.getValue( "semi_major" ) )
                invalidParameter = "semi_major";
        } catch ( MissingParameterException exception ) {
            // Axis length not set.
            resetAxisLength = true;
        }
        if ( invalidParameter != null ) {
            throw new IllegalArgumentException(
                                                Resources.format(
                                                                  ResourceKeys.ERROR_INCOMPATIBLE_ELLIPSOID_$2,
                                                                  invalidParameter,
                                                                  ellipsoid.getName( null ) ) );
        }
        if ( resetAxisLength ) {
            final ParameterList parameters = projection.getParameters();
            parameters.setParameter( "semi_major", semiMajor );
            parameters.setParameter( "semi_minor", semiMinor );
            projection = new Projection( projection.getName( null ), projection.getClassName(),
                                         parameters );
        }
        this.gcs = gcs;
        this.projection = projection;
        this.unit = unit;
    }

    /**
     * Creates a projected coordinate system using a projection object.
     *
     * @param  properties The set of properties (see {@link Info}).
     * @param  gcs Geographic coordinate system to base projection on.
     * @param  projection Projection from geographic to projected coordinate system.
     * @param  unit Linear units of created PCS.
     * @param  axis0 Details of 0th ordinates in created PCS coordinates.
     * @param  axis1 Details of 1st ordinates in created PCS coordinates.
     */
    ProjectedCoordinateSystem( final Map properties, final GeographicCoordinateSystem gcs,
                              final Projection projection, final Unit unit, final AxisInfo axis0,
                              final AxisInfo axis1 ) {
        super( properties, gcs.getHorizontalDatum(), axis0, axis1 );
        this.gcs = gcs;
        this.projection = projection;
        this.unit = unit;
        // Accept null values.
    }

    /**
     * Returns the geographic coordinate system.
     *
     * @see org.opengis.cs.CS_ProjectedCoordinateSystem#getGeographicCoordinateSystem()
     */
    public GeographicCoordinateSystem getGeographicCoordinateSystem() {
        return gcs;
    }

    /**
     * Gets the projection.
     *
     * @see org.opengis.cs.CS_ProjectedCoordinateSystem#getProjection()
     */
    public Projection getProjection() {
        return projection;
    }

    /**
     * Gets units for dimension within coordinate system.
     * This linear unit is the same for all axis.
     *
     * @param dimension Zero based index of axis.
     *
     * @see org.opengis.cs.CS_ProjectedCoordinateSystem#getUnits(int)
     * @see org.opengis.cs.CS_ProjectedCoordinateSystem#getLinearUnit()
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
            final ProjectedCoordinateSystem that = (ProjectedCoordinateSystem) cs;
            return Utilities.equals( this.gcs, that.gcs )
                   && Utilities.equals( this.projection, that.projection )
                   && Utilities.equals( this.unit, that.unit );
        }
        return false;
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        buffer.append( ", " );
        buffer.append( gcs );
        buffer.append( ", " );
        buffer.append( projection );
        buffer.append( ", " );
        addUnit( buffer, unit );
        buffer.append( ", " );
        buffer.append( getAxis( 0 ) );
        buffer.append( ", " );
        buffer.append( getAxis( 1 ) );
        return "PROJCS";
    }

}
