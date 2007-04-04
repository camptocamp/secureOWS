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
 * Noms d'unit�s en langue fran�aise. Les unit�s qui n'apparaissent
 * pas dans cette ressources ne seront pas localis�es.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class Units_fr extends Units
{
    /**
     * Liste des unit�s en fran�ais. Les cl�s sont les symboles
     * standards (sauf exception) des unit�s.  Les valeurs sont
     * les noms en fran�ais de ces unit�s.
     */
    static final String[] contents=
    {
        "m",      "m�tre",
        "g",      "gramme",
        "s",      "seconde",
        "A",      "amp�re",
        "K",      "kelvin",
        "mol",    "mole",
        "cd",     "candela",
        "rad",    "radian",
        "sr",     "st�radian",
        "Hz",     "hertz",
        "N",      "newton",
        "Pa",     "pascal",
        "J",      "joule",
        "W",      "watt",
        "C",      "coulomb",
        "V",      "volt",
        "F",      "farad",
        "\u03A9", "Ohm",
        "S",      "siemmens",
        "T",      "tesla",
        "Wb",     "weber",
        "lx",     "lux",
        "Bq",     "becquerel",
        "Gy",     "gray",
        "Sv",     "sievert",
        "H",      "henry",
        "lm",     "lumen",
        "min",    "minute",
        "h",      "heure",
        "d",      "jour",
        "�",      "degr� d'angle",
        "'",      "minute d'angle",
        "\"",     "seconde d'angle",
        "l",      "litre",
        "L",      "litre",
        "t",      "tonne m�trique",
        "eV",     "�lectronvolt",
        "u",      "unit� de masse atomique unifi�e",
        "ua",     "unit� astronomique",
        "inch",   "pouce",             // Symbole non-standard
        "foot",   "pied",              // Symbole non-standard
        "yard",   "yard",              // Symbole non-standard
        "fathom", "brasse anglaise",   // Symbole non-standard
        "brasse", "brasse fran�aise",  // Symbole non-standard
        "mile",   "mille",             // Symbole non-standard
        "nmile",  "mille marin",       // Symbole non-standard
        "knot",   "noeud",             // Symbole non-standard
        "are",    "are",
        "ha",     "hectare",
        "bar",    "bar",
        "�",      "�ngstr�m",
        "barn",   "barn",
        "erg",    "erg",
        "dyn",    "dyne",
        "P",      "poise",
        "St",     "stokes",
        "G",      "gauss",
        "Oe",     "oersted",
        "Mx",     "maxwell",
        "sb",     "stilb",
        "ph",     "phot",
        "Gal",    "gal",
        "Ci",     "curie",
        "R",      "r�ntgen",
        "rd",     "rad",
        "rem",    "rem",
        "Jy",     "jansky",
        "Torr",   "torr",
        "atm",    "atmosph�re normale",
        "pound",  "livre",             // Symbole non-standard
        "onze",   "onze",              // Symbole non-standard
        "�C",     "degr� celcius",
        "�F",     "fahrenheit"         // Symbole non-standard
    };

    /**
     * Initialise les ressources fran�aises.
     */
    public Units_fr()
    {super(contents);}
}
