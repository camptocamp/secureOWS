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

/**
 * A set of quantities from which other quantities are calculated.
 * It may be a textual description and/or a set of parameters describing the
 * relationship of a coordinate system to some predefined physical locations
 * (such as center of mass) and physical directions (such as axis of spin).
 * It can be defined as a set of real points on the earth that have coordinates.
 * For example a datum can be thought of as a set of parameters defining completely
 * the origin and orientation of a coordinate system with respect to the earth.
 * The definition of the datum may also include the temporal behavior (such
 * as the rate of change of the orientation of the coordinate axes).
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_Datum
 */
public class Datum extends Info {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 2175857309476007487L;

    /**
     * The datum type.
     */
    private final DatumType type;

    /**
     * Construct a new datum with the
     * specified name and datum type.
     *
     * @param name The datum name.
     * @param type The datum type.
     */
    public Datum( final String name, final DatumType type ) {
        super( name );
        this.type = type;
        ensureNonNull( "type", type );
    }

    /**
     * Construct a new datum with the specified properties.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param type The datum type.
     */
    Datum( final Map properties, final DatumType type ) {
        super( properties );
        this.type = type;
        // Accept null value.
    }

    /**
     * Gets the type of the datum as an enumerated code.
     *
     * @see org.opengis.cs.CS_Datum#getDatumType()
     */
    public DatumType getDatumType() {
        return type;
    }

    /**
     * Returns a hash value for this datum.
     */
    public int hashCode() {
        int code = 37 * super.hashCode();
        final DatumType type = getDatumType();
        if ( type != null )
            code += type.hashCode();
        return code;
    }

    /**
     * Compares the specified object
     * with this datum for equality.
     */
    public boolean equals( final Object object ) {
        if ( super.equals( object ) ) {
            final Datum that = (Datum) object;
            return Utilities.equals( this.type, that.type );
        }
        return false;
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        buffer.append( ", " );
        buffer.append( type.getName() );
        return "DATUM";
    }

 
}
