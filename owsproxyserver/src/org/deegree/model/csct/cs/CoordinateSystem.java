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


import org.deegree.model.csct.pt.Dimensioned;
import org.deegree.model.csct.pt.Envelope;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * Base class for all coordinate systems.
 * A coordinate system is a mathematical space, where the elements of
 * the space are called positions.  Each position is described by a list
 * of numbers.  The length of the list corresponds to the dimension of
 * the coordinate system.  So in a 2D coordinate system each position is
 * described by a list containing 2 numbers.
 * <br><br>
 * However, in a coordinate system, not all lists of numbers correspond
 * to a position - some lists may be outside the domain of the coordinate
 * system.  For example, in a 2D Lat/Lon coordinate system, the list (91,91)
 * does not correspond to a position.
 * <br><br>
 * Some coordinate systems also have a mapping from the mathematical space
 * into locations in the real world.  So in a Lat/Lon coordinate system, the
 * mathematical position (lat, long) corresponds to a location on the surface
 * of the Earth.  This mapping from the mathematical space into real-world
 * locations is called a Datum.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_CoordinateSystem
 */
public abstract class CoordinateSystem extends Info implements Dimensioned {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -4539963180028417479L;

    /**
     * Construct a coordinate system.
     *
     * @param name The coordinate system name.
     */
    public CoordinateSystem( final String name ) {
        super( name );
    }

    /**
     * Construct a coordinate system.
     *
     * @param properties The set of properties (see {@link Info}).
     */
    CoordinateSystem( final Map properties ) {
        super( properties );
    }

    /**
     * Make sure there is no axis among the same direction
     * (e.g. two north axis, or a east and a west axis).
     * This methods may be invoked from subclasses constructors.
     *
     * @param  type The datum type, or <code>null</code> if unknow.
     * @throws IllegalArgumentException if two axis have the same direction.
     */
    final void checkAxis( final DatumType type )
                            throws IllegalArgumentException {
        final int dimension = getDimension();
        for ( int i = 0; i < dimension; i++ ) {
            AxisOrientation check = getAxis( i ).orientation;
            if ( type != null && !type.isCompatibleOrientation( check ) ) {
                throw new IllegalArgumentException(
                                                    Resources.format(
                                                                      ResourceKeys.ERROR_ILLEGAL_AXIS_ORIENTATION_$2,
                                                                      check.getName( null ),
                                                                      Utilities.getShortClassName( this ) ) );
            }
            check = check.absolute();
            if ( !check.equals( AxisOrientation.OTHER ) ) {
                for ( int j = i + 1; j < dimension; j++ ) {
                    if ( check.equals( getAxis( j ).orientation.absolute() ) ) {
                        final String nameI = getAxis( i ).orientation.getName( null );
                        final String nameJ = getAxis( j ).orientation.getName( null );
                        throw new IllegalArgumentException(
                                                            Resources.format(
                                                                              ResourceKeys.ERROR_COLINEAR_AXIS_$2,
                                                                              nameI, nameJ ) );
                    }
                }
            }
        }
    }

    /**
     * Returns the dimension of the coordinate system.
     *
     * @see org.opengis.cs.CS_CoordinateSystem#getDimension()
     */
    public abstract int getDimension();

    /**
     * Gets axis details for dimension within coordinate system.
     * Each dimension in the coordinate system has a corresponding axis.
     *
     * @param dimension Zero based index of axis.
     *
     * @see org.opengis.cs.CS_CoordinateSystem#getAxis(int)
     */
    public abstract AxisInfo getAxis( int dimension );

    /**
     * Gets units for dimension within coordinate system.
     * Each dimension in the coordinate system has corresponding units.
     *
     * @param dimension Zero based index of axis.
     *
     * @see org.opengis.cs.CS_CoordinateSystem#getUnits(int)
     */
    public abstract Unit getUnits( int dimension );

    /**
     * If all dimensions use the same units, returns this
     * units. Otherwise, returns <code>null</code>.
     */
    final Unit getUnits() {
        Unit units = null;
        for ( int i = getDimension(); --i >= 0; ) {
            final Unit check = getUnits( i );
            if ( units == null )
                units = check;
            else if ( !units.equals( check ) )
                return null;
        }
        return units;
    }

    /**
     * Returns the datum.
     */
    Datum getDatum() {
        return null;
    }

    /**
     * Gets default envelope of coordinate system.
     * Coordinate systems which are bounded should return the minimum bounding
     * box of their domain.  Unbounded coordinate systems should return a box
     * which is as large as is likely to be used.  For example, a (lon,lat)
     * geographic coordinate system in degrees should return a box from
     * (-180,-90) to (180,90), and a geocentric coordinate system could return
     * a box from (-r,-r,-r) to (+r,+r,+r) where r is the approximate radius
     * of the Earth.
     * <br><br>
     * The default implementation returns an envelope with infinite bounds.
     *
     * @see org.opengis.cs.CS_CoordinateSystem#getDefaultEnvelope()
     */
    public Envelope getDefaultEnvelope() {
        final int dimension = getDimension();
        final Envelope envelope = new Envelope( dimension );
        for ( int i = dimension; --i >= 0; ) {
            envelope.setRange( i, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY );
        }
        return envelope;
    }

    /**
     * Returns  <code>true</code> if this coordinate system is equivalents to
     * the specified coordinate system. Two coordinate systems are considered
     * equivalent if the {@link org.deegree.model.csct.ct.CoordinateTransformation} from
     * <code>this</code> to <code>cs</code> would be the identity transform.
     * The <code>equivalents</code> method is less strict than <code>equals</code>
     * in that it doesn't compare names, alias, authority codes or others similar
     * informations.
     *
     * @param  cs The coordinate system (may be <code>null</code>).
     * @return <code>true</code> if both coordinate systems are equivalent.
     */
    public boolean equivalents( final CoordinateSystem cs ) {
        return ( cs != null ) && cs.getClass().equals( getClass() );
    }

    /**
     * Compares the specified object with
     * this coordinate system for equality.
     */
    public boolean equals( final Object object ) {
        if ( object == this )
            return true; // Slight optimization
        return super.equals( object ) && equivalents( (CoordinateSystem) object );
    }

}
