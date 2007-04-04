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

/**
 * Procedure used to measure vertical distances.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_VerticalDatum
 */
public class VerticalDatum extends Datum {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 1663224345779675117L;

    /**
     * Default vertical datum for ellipsoidal heights. Ellipsoidal heights
     * are measured along the normal to the ellipsoid used in the definition
     * of horizontal datum.
     */
    public static final VerticalDatum ELLIPSOIDAL = (VerticalDatum) pool.intern( new VerticalDatum(
                                                                                                    "Ellipsoidal",
                                                                                                    DatumType.ELLIPSOIDAL ) );

    /**
     * Creates a vertical datum from an enumerated type value.
     *
     * @param name Name to give new object.
     * @param type Type of vertical datum to create.
     *
     */
    public VerticalDatum( final String name, final DatumType.Vertical type ) {
        super( name, type );
    }

    /**
     * Creates a vertical datum.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param type Type of vertical datum to create.
     */
    VerticalDatum( final Map properties, final DatumType type ) {
        super( properties, type );
    }

    /**
     * Gets the type of the datum as an enumerated code.
     *
     * Note: return type will be changed to {@link DatumType.Vertical}
     *       when we will be able to use generic types (with JDK 1.5).
     *
     * @see org.opengis.cs.CS_VerticalDatum#getDatumType()
     */
    public DatumType/*.Vertical*/getDatumType() {
        return (DatumType.Vertical) super.getDatumType();
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        super.addString( buffer );
        return "VERT_DATUM";
    }

}
