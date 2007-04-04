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

// Miscellaneous
import java.util.Map;


/**
 * Procedure used to measure time.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class TemporalDatum extends Datum
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 6313740402733520130L;

    /**
     * Default datum for Universal Time Clock (UTC).
     * UTC is based on an atomic clock, while GMT is
     * based on astronomical observations.
     */
    public static final TemporalDatum UTC = (TemporalDatum) pool.intern(
                        new TemporalDatum("UTC", DatumType.UTC));

    /**
     * Creates a temporal datum from an enumerated type value.
     *
     * @param name Name to give new object.
     * @param type Type of temporal datum to create.
     */
    public TemporalDatum(final String name, final DatumType.Temporal type)
    {super(name, type);}

    /**
     * Creates a temporal datum.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param type Type of temporal datum to create.
     */
    TemporalDatum(final Map properties, final DatumType type)
    {super(properties, type);}

    /**
     * Gets the type of the datum as an enumerated code.
     *
     * Note: return type will be changed to {@link DatumType.Temporal}
     *       when we will be able to use generic types (with JDK 1.5).
     */
    public DatumType/*.Temporal*/ getDatumType()
    {return (DatumType.Temporal) super.getDatumType();}

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString(final StringBuffer buffer)
    {
        super.addString(buffer);
        return "TEMPORAL_DATUM";
    }
}
