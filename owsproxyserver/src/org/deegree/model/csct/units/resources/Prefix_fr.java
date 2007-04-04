/*
 * Map and oceanographical data visualisation
 * Copyright (C) 1999 P�ches et Oc�ans Canada
 *               2000 Institut de Recherche pour le D�veloppement
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Library General Public License for more details (http://www.gnu.org/).
 *
 *
 * Contacts:
 *     FRANCE: Surveillance de l'Environnement Assist�e par Satellite
 *             Institut de Recherche pour le D�veloppement / US-Espace
 *             mailto:seasnet@teledetection.fr
 *
 *     CANADA: Observatoire du Saint-Laurent
 *             Institut Maurice-Lamontagne
 *             mailto:osl@osl.gc.ca
 */
package org.deegree.model.csct.units.resources;


/**
 * Noms de pr�fix en langue fran�aise. Les pr�fix qui n'apparaissent
 * pas dans cette ressources garderont leur nom neutre.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class Prefix_fr extends Prefix
{
    /**
     * Liste des pr�fix en fran�ais.
     */
    static final String[] contents=
    {
        "deci",   "d�ci"
    };

    /**
     * Initialise les ressources fran�aises.
     */
    public Prefix_fr()
    {super(contents);}
}
