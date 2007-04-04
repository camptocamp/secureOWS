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

// Utilitaires
import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * Liste de ressources s'adaptant � la langue de l'utilisateur. Cette classe s'apparente
 * � la classe {@link java.util.ListResourceBundle} standard du Java, except� qu'elle est
 * l�g�rement plus �conome en m�moire.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class SymbolResources extends java.util.ResourceBundle
{
    /**
     * Table des ressources adapt�es
     * � la langue de l'utilisateur.
     */
    final Object[] map;

    /**
     * Construit la table des ressources.
     *
     * @param contents Liste des cl�s et des valeurs qui y sont associ�es. Cette liste consiste en un
     *        tableau d'objets. Les objets se trouvant aux index pairs (0, 2, 4...) sont les cl�s, et les
     *        objets se trouvant aux index impairs sont les valeurs (voyez la description de cette classe
     *        pour des exemples).
     *
     * @throws IllegalArgumentException Si une cl� a �t� r�p�t�e deux fois.
     */
    protected SymbolResources(final Object[] contents) throws IllegalArgumentException
    {
        map=contents;
        for (int i=0; i<contents.length; i+=2)
        {
            final String key=contents[i].toString();
            for (int j=i; (j+=2)<contents.length;)
            {
                if (key.equals(contents[j]))
                    throw new IllegalArgumentException("Duplicated key: "+key);
            }
        }
    }

    /**
     * Renvoie un �num�rateur qui balayera toutes
     * les cl�s que poss�de cette liste de ressources.
     */
    public final Enumeration getKeys()
    {
        return new Enumeration()
        {
            private int i=0;

            public boolean hasMoreElements()
            {return i<map.length;}

            public Object nextElement()
            {
                if (i<map.length)
                {
                    final int i=this.i;
                    this.i += 2;
                    return map[i];
                }
                throw new NoSuchElementException();
            }
        };
    }

    /**
     * Renvoie la ressource associ�e � une cl� donn�e. Cette m�thode est d�finie
     * pour r�pondre aux exigences de la classe {@link java.util.ResourceBundle}
     * et n'a g�n�ralement pas besoin d'�tre appell�e directement.
     *
     * @param  key Cl� d�signant la ressouce d�sir�e (ne doit pas �tre <code>null</code>).
     * @return La ressource demand�e, ou <code>null</code> si aucune ressource n'est
     *         d�finie pour cette cl�.
     */
    protected final Object handleGetObject(final String key)
    {
        for (int i=0; i<map.length; i+=2)
            if (key.equals(map[i]))
                return map[i+1];
        return null;
    }
}
