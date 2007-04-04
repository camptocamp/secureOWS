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


import org.deegree.model.csct.pt.CoordinatePoint;
import org.deegree.model.csct.pt.Envelope;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;
import org.deegree.model.csct.units.Unit;

/**
 * An aggregate of two coordinate systems.
 * One of these is usually a two dimensional coordinate system such as a
 * geographic or a projected coordinate system with a horizontal datum.
 * The other is one-dimensional coordinate system with a vertical datum.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_CompoundCoordinateSystem
 */
public class CompoundCoordinateSystem extends CoordinateSystem {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -488997059924367289L;

    /**
     * A default three-dimensional coordinate system for use with geographic
     * coordinates with heights above the ellipsoid. The head coordinate
     * system is {@link GeographicCoordinateSystem#WGS84} and the tail
     * coordinate system is {@link VerticalCoordinateSystem#ELLIPSOIDAL}.
     */
    public static final CompoundCoordinateSystem WGS84 = (CompoundCoordinateSystem) pool.intern( new CompoundCoordinateSystem(
                                                                                                                               "WGS84",
                                                                                                                               GeographicCoordinateSystem.WGS84,
                                                                                                                               VerticalCoordinateSystem.ELLIPSOIDAL ) );

    /**
     * First sub-coordinate system.
     */
    private final CoordinateSystem head;

    /**
     * Second sub-coordinate system.
     */
    private final CoordinateSystem tail;

    /**
     * Creates a compound coordinate system.
     *
     * @param name Name to give new object.
     * @param head Coordinate system to use for earlier ordinates.
     * @param tail Coordinate system to use for later ordinates.
     *
     */
    public CompoundCoordinateSystem( final String name, final CoordinateSystem head,
                                    final CoordinateSystem tail ) {
        super( name );
        this.head = head;
        this.tail = tail;
        ensureNonNull( "head", head );
        ensureNonNull( "tail", tail );
        checkAxis( null );
    }

    /**
     * Creates a compound coordinate system.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param head Coordinate system to use for earlier ordinates.
     * @param tail Coordinate system to use for later ordinates.
     */
    CompoundCoordinateSystem( final Map properties, final CoordinateSystem head,
                             final CoordinateSystem tail ) {
        super( properties );
        this.head = head;
        this.tail = tail;
        // Accept null values.
    }

    /**
     * Returns the first sub-coordinate system.
     *
     * @see org.opengis.cs.CS_CompoundCoordinateSystem#getHeadCS()
     */
    public CoordinateSystem getHeadCS() {
        return head;
    }

    /**
     * Returns the second sub-coordinate system.
     *
     * @see org.opengis.cs.CS_CompoundCoordinateSystem#getTailCS()
     */
    public CoordinateSystem getTailCS() {
        return tail;
    }

    /**
     * Returns the dimension of the coordinate system.
     *
     * @see org.opengis.cs.CS_CompoundCoordinateSystem#getDimension()
     */
    public int getDimension() {
        return head.getDimension() + tail.getDimension();
    }

    /**
     * Gets axis details for dimension within coordinate system.
     * Each dimension in the coordinate system has a corresponding axis.
     *
     * @see org.opengis.cs.CS_CompoundCoordinateSystem#getAxis(int)
     */
    public AxisInfo getAxis( final int dimension ) {
        if ( dimension >= 0 ) {
            final int headDim = head.getDimension();
            if ( dimension < headDim ) {
                return head.getAxis( dimension );
            }
            final int dim = dimension - headDim;
            if ( dim < tail.getDimension() ) {
                return tail.getAxis( dim );
            }
        }
        throw new IndexOutOfBoundsException(
                                             Resources.format(
                                                               ResourceKeys.ERROR_INDEX_OUT_OF_BOUNDS_$1,
                                                               new Integer( dimension ) ) );
    }

    /**
     * Gets units for dimension within coordinate system.
     * Each dimension in the coordinate system has corresponding units.
     *
     * @see org.opengis.cs.CS_CompoundCoordinateSystem#getUnits(int)
     */
    public Unit getUnits( final int dimension ) {
        if ( dimension >= 0 ) {
            final int headDim = head.getDimension();
            if ( dimension < headDim ) {
                return head.getUnits( dimension );
            }
            final int dim = dimension - headDim;
            if ( dim < tail.getDimension() ) {
                return head.getUnits( dim );
            }
        }
        throw new IndexOutOfBoundsException(
                                             Resources.format(
                                                               ResourceKeys.ERROR_INDEX_OUT_OF_BOUNDS_$1,
                                                               new Integer( dimension ) ) );
    }

    /**
     * Gets default envelope of coordinate system.
     *
     * @see org.opengis.cs.CS_CompoundCoordinateSystem#getDefaultEnvelope()
     */
    public Envelope getDefaultEnvelope() {
        final Envelope headEnv = head.getDefaultEnvelope();
        final Envelope tailEnv = tail.getDefaultEnvelope();
        final int headDim = headEnv.getDimension();
        final int tailDim = tailEnv.getDimension();
        final CoordinatePoint min = new CoordinatePoint( headDim + tailDim );
        final CoordinatePoint max = new CoordinatePoint( headDim + tailDim );
        for ( int i = 0; i < headDim; i++ ) {
            min.ord[i] = headEnv.getMinimum( i );
            max.ord[i] = headEnv.getMaximum( i );
        }
        for ( int i = 0; i < tailDim; i++ ) {
            min.ord[headDim + i] = tailEnv.getMinimum( i );
            max.ord[headDim + i] = tailEnv.getMaximum( i );
        }
        return new Envelope( min, max );
    }

    /**
     * Returns  <code>true</code> if this coordinate system is equivalents to
     * the specified coordinate system. Two coordinate systems are considered
     * equivalent if the {@link org.deegree.model.csct.ct.CoordinateTransformation} from
     * <code>this</code> to <code>cs</code> would be the identity transform.
     *
     * @param  cs The coordinate system (may be <code>null</code>).
     * @return <code>true</code> if both coordinate systems are equivalent.
     */
    public boolean equivalents( final CoordinateSystem cs ) {
        if ( cs == this )
            return true;
        if ( super.equivalents( cs ) ) {
            final CompoundCoordinateSystem that = (CompoundCoordinateSystem) cs;
            return head.equivalents( that.head ) && tail.equivalents( that.tail );
        }
        return false;
    }

    /**
     * Compares the specified object with
     * this coordinate system for equality.
     */
    public boolean equals( final Object object ) {
        if ( object == this )
            return true;
        if ( super.equals( object ) ) {
            final CompoundCoordinateSystem that = (CompoundCoordinateSystem) object;
            return Utilities.equals( this.head, that.head )
                   && Utilities.equals( this.tail, that.tail );
        }
        return false;
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        buffer.append( ", " );
        buffer.append( head );
        buffer.append( ", " );
        buffer.append( tail );
        return "COMPD_CS";
    }

}
