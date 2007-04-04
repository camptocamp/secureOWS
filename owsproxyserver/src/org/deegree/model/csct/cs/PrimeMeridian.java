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
import org.deegree.model.csct.units.Unit;

/**
 * A meridian used to take longitude measurements from.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_PrimeMeridian
 */
public class PrimeMeridian extends Info {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 7570594768127669147L;

    /**
     * The Greenwich meridian, with angular measures in degrees.
     */
    public static final PrimeMeridian GREENWICH = (PrimeMeridian) pool.intern( new PrimeMeridian(
                                                                                                  "Greenwich",
                                                                                                  Unit.DEGREE,
                                                                                                  0 ) );

    /**
     * The angular units.
     */
    private final Unit unit;

    /**
     * The longitude value relative to the Greenwich Meridian.
     */
    private final double longitude;

    /**
     * Creates a prime meridian, relative to Greenwich.
     *
     * @param name      Name to give new object.
     * @param unit      Angular units of longitude.
     * @param longitude Longitude of prime meridian in supplied angular units East of Greenwich.
     *
     */
    public PrimeMeridian( final String name, final Unit unit, final double longitude ) {
        super( name );
        this.unit = unit;
        this.longitude = longitude;
        ensureNonNull( "unit", unit );
        ensureAngularUnit( unit );
    }

    /**
     * Creates a prime meridian, relative to Greenwich.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param unit       Angular units of longitude.
     * @param longitude  Longitude of prime meridian in supplied angular units East of Greenwich.
     */
    PrimeMeridian( final Map properties, final Unit unit, final double longitude ) {
        super( properties );
        this.unit = unit;
        this.longitude = longitude;
        // Accept null values.
    }

    /**
     * Returns the longitude value relative to the Greenwich Meridian.
     * The longitude is expressed in this objects angular units.
     *
     * @see org.opengis.cs.CS_PrimeMeridian#getLongitude()
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the longitude value relative to the Greenwich Meridian,
     * expressed in the specified units. This convenience method make
     * easier to obtains longitude in degrees (<code>getLongitude(Unit.DEGREE)</code>),
     * no matter the underlying angular units of this prime meridian.
     *
     * @param targetUnit The unit in which to express longitude.
     */
    public double getLongitude( final Unit targetUnit ) {
        return targetUnit.convert( getLongitude(), getAngularUnit() );
    }

    /**
     * Returns the angular units.
     *
     * @see org.opengis.cs.CS_PrimeMeridian#getAngularUnit()
     */
    public Unit getAngularUnit() {
        return unit;
    }

    /**
     * Returns a hash value for this prime meridian.
     */
    public int hashCode() {
        final long code = Double.doubleToLongBits( longitude );
        return super.hashCode() * 37 + ( (int) ( code >>> 32 ) ^ (int) code );
    }

    /**
     * Compares the specified object with
     * this prime meridian for equality.
     */
    public boolean equals( final Object object ) {
        if ( super.equals( object ) ) {
            final PrimeMeridian that = (PrimeMeridian) object;
            return Double.doubleToLongBits( this.longitude ) == Double.doubleToLongBits( that.longitude )
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
        buffer.append( Unit.DEGREE.convert( longitude, unit ) );
        return "PRIMEM";
    }

}
