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

// OpenGIS (SEAS) dependencies
import org.deegree.model.csct.cs.Projection;


/**
 * Classe de base des projections cartographiques coniques. Les projections
 * coniques consistent � projeter la surface de la Terre sur un c�ne tangeant ou s�cant � la
 * Terre. Les parall�les apparaissent habituellement comme des arcs de cercles et les m�ridiens
 * comme des lignes droites. Les projections coniques ne sont pas tr�s utilis�s du fait que
 * leurs distorsions augmentent rapidement � mesure que l'on s'�loigne des parall�les standards.
 * Elles sont plut�t utilis�es pour les r�gions aux latitudes moyennes qui s'�tendent sur une
 * large r�gion d'est en ouest, comme les Etats-Unis.
 *
 * On peut trouver plus de d�tails sur les projections coniques � l'adresse
 * <a href="http://everest.hunter.cuny.edu/mp/conic.html">http://everest.hunter.cuny.edu/mp/conic.html</a>.
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="doc-files/ConicProjection.png"></p>
 * <p align="center">Repr�sentation d'une projection conique<br>
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
abstract class ConicProjection extends MapProjection
{
    /**
     * Construct a new map projection from the suplied parameters.
     *
     * @param  parameters The parameter values in standard units.
     * @throws MissingParameterException if a mandatory parameter is missing.
     */
    protected ConicProjection(final Projection parameters) throws MissingParameterException
    {super(parameters);}
}
