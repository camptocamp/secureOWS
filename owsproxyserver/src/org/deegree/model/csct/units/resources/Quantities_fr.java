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
 * Noms de quantit�s en langue fran�aise. Les quantit�s qui n'apparaissent
 * pas dans cette ressources garderont leur nom neutre.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class Quantities_fr extends Quantities
{
    /**
     * Liste des quantit�s en fran�ais.
     */
    static final String[] contents=
    {
        "dimensionless",             "sans dimension",
        "length",                    "longueur",
        "mass",                      "masse",
        "time",                      "temps",
        "electric current",          "courant �lectrique",
        "thermodynamic temperature", "temp�rature thermodynamique",
        "amount of substance",       "quantit� de mati�re",
        "luminous intensity",        "intensit� lumineuse",
        "plane angle",               "angle plan",
        "solid angle",               "angle solide",
        "salinity",                  "salinit�",
        "area",                      "superficie",
        "volume",                    "volume",
        "speed",                     "vitesse",
        "acceleration",              "acc�l�ration",
        "magnetic field strength",   "champ magn�tique",
        "luminance",                 "luminance lumineuse",
        "frequency",                 "fr�quence",
        "force",                     "force",
        "pressure",                  "pression",
        "energy",                    "�nergie",
        "power",                     "puissance",
        "electric charge",           "charge �lectrique",
        "potential",                 "potentiel",
        "capacitance",               "capacit�",
        "resistance",                "r�sistance",
        "conductance",               "conductance",
        "magnetic flux",             "flux magn�tique",
        "magnetic flux density",     "induction magn�tique",
        "inductance",                "inductance",
        "luminous flux",             "flux lumineux",
        "illuminance",               "�clairement lumineux",
        "activity",                  "activit�",
        "absorbed dose",             "dose absorb�e",
        "dose equivalent",           "�quivalent de dose"
    };

    /**
     * Initialise les ressources fran�aises.
     */
    public Quantities_fr()
    {super(contents);}
}
