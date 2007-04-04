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

// Miscellaneous
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 * Indicates that an operation cannot be completed properly because
 * of a mismatch in the dimensions of object attributes.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class MismatchedDimensionException extends RuntimeException
{
    /**
     * Creates new exception without detail message.
     */
    public MismatchedDimensionException()
    {}

    /**
     * Constructs an exception with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MismatchedDimensionException(final String msg)
    {super(msg);}

    /**
     * Construct an exception with a detail message stating that
     * two objects don't have the same number of dimensions.
     *
     * @param object1 The first dimensioned object.
     * @param object2 The second dimensioned object. Its dimension
     *        should be different than <code>object1</code>'s dimension,
     *        otherwise there is no dimension mismatch!
     */
    public MismatchedDimensionException(final Dimensioned object1, final Dimensioned object2)
    {this(object1.getDimension(), object2.getDimension());}

    /**
     * Construct an exception with a detail message stating that
     * two objects don't have the same number of dimensions.
     *
     * @param dim1 Number of dimensions for the first object.
     * @param dim2 Number of dimensions for the second object.
     *        It shoud be different than <code>dim1</code>,
     *        otherwise there is no dimension mismatch!
     */
    public MismatchedDimensionException(final int dim1, final int dim2)
    {this(Resources.format(ResourceKeys.ERROR_MISMATCHED_DIMENSION_$2, new Integer(dim1), new Integer(dim2)));}
}
