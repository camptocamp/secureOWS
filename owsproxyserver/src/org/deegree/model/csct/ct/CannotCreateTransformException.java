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
package org.deegree.model.csct.ct;

// OpenGIS dependences (SEAGIS)
import org.deegree.model.csct.cs.CoordinateSystem;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 * Thrown when a coordinate transformation can't be created.
 * It may be because there is no known path between source and coordinate systems,
 * or because the requested transformation is not available in the environment.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class CannotCreateTransformException extends TransformException
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 5368463308772454145L;

    /**
     * Construct an exception with no detail message.
     */
    public CannotCreateTransformException()
    {}

    /**
     * Construct an exception with the specified detail message.
     */
    public CannotCreateTransformException(final String message)
    {super(message);}

    /**
     * Construct an exception with a message stating that no transformation
     * path has been found between the specified coordinate system.
     */
    public CannotCreateTransformException(final CoordinateSystem sourceCS, final CoordinateSystem targetCS)
    {this(Resources.format(ResourceKeys.ERROR_NO_TRANSFORMATION_PATH_$2, getName(sourceCS), getName(targetCS)));}

    /**
     * Gets a display name for the specified coordinate system.
     */
    private static String getName(final CoordinateSystem cs)
    {return Utilities.getShortClassName(cs)+'('+cs.getName(null)+')';}
}
