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

// Resources
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 * Thrown when a parameter was missing.
 * For example, this exception may be thrown when a map projection
 * was requested but the "semi_major" parameter was not specified.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class MissingParameterException extends RuntimeException
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3365753083955970327L;

    /**
     * The missing parameter name.
     */
    private final String parameter;

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param msg the detail message, or <code>null</code> to construct
     *        a default message from the missing parameter name.
     * @param parameter The missing parameter name.
     */
    public MissingParameterException(final String msg, final String parameter)
    {
        super((msg!=null || parameter==null) ? msg : Resources.format(ResourceKeys.ERROR_MISSING_PARAMETER_$1, parameter));
        this.parameter = parameter;
    }

    /**
     * Returns the missing parameter name.
     */
    public String getMissingParameterName()
    {return parameter;}
}
