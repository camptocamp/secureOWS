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
package org.deegree.model.csct.pt;


/**
 * A longitude angle. Positive longitudes are East, while negative longitudes are West.
 * This class has no direct OpenGIS equivalent.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 *
 * @see Latitude
 * @see AngleFormat
 */
public final class Longitude extends Angle
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8614900608052762636L;

    /**
     * Minimum legal value for longitude (-180�).
     */
    public static final double MIN_VALUE = -180;

    /**
     * Maximum legal value for longitude (+180�).
     */
    public static final double MAX_VALUE = +180;

    /**
     * Contruct a new longitude with the specified value.
     *
     * @param theta Angle in degrees.
     */
    public Longitude(final double theta)
    {super(theta);}

    /**
     * Constructs a newly allocated <code>Longitude</code> object that
     * represents the longitude value represented by the string.   The
     * string should represents an angle in either fractional degrees
     * (e.g. 45.5�) or degrees with minutes and seconds (e.g. 45�30').
     * The hemisphere (E or W) is optional (default to East).
     *
     * @param  source A string to be converted to a <code>Longitude</code>.
     * @throws NumberFormatException if the string does not contain a parsable longitude.
     */
    public Longitude(final String source) throws NumberFormatException
    {super(source);}
}
