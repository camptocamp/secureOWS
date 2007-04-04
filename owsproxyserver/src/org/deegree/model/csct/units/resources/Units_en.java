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
 * Noms d'unit�s en langue anglaise. Les unit�s qui n'apparaissent
 * pas dans cette ressources ne seront pas localis�es.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class Units_en extends Units
{
    /**
     * Liste des unit�s en anglais. Les cl�s sont les symboles
     * standards (sauf exception) des unit�s. Les valeurs sont
     * les noms en anglais de ces unit�s.
     */
    static final String[] contents=
    {
        "m",      "metre",
        "g",      "gram",
        "s",      "second",
        "A",      "ampere",
        "K",      "kelvin",
        "mol",    "mole",
        "cd",     "candela",
        "rad",    "radian",
        "sr",     "steradian",
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
        "h",      "hour",
        "d",      "day",
        "�",      "degree of angle",
        "'",      "minute of angle",
        "\"",     "seconde of angle",
        "l",      "litre",
        "L",      "litre",
        "t",      "metric ton",
        "eV",     "electronvolt",
        "u",      "unified atomic mass unit",
        "ua",     "astronomical unit",
        "inch",   "inch",              // Symbole non-standard
        "foot",   "foot",              // Symbole non-standard
        "yard",   "yard",              // Symbole non-standard
        "fathom", "English fathom",    // Symbole non-standard
        "brasse", "French fathom",     // Symbole non-standard
        "mile",   "mile",              // Symbole non-standard
        "nmile",  "nautical mile",     // Symbole non-standard
        "knot",   "knot",              // Symbole non-standard
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
        "atm",    "standard atmosphere",
        "pound",  "pound",             // Symbole non-standard
        "onze",   "onze",              // Symbole non-standard
        "�C",     "Celcius degree",
        "�F",     "fahrenheit"         // Symbole non-standard
    };

    /**
     * Initialise les ressources anglaises.
     */
    public Units_en()
    {super(contents);}
}
