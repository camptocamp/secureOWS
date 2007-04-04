/*
 * Map and oceanographical data visualisation
 * Copyright (C) 1998 University Corporation for Atmospheric Research (Unidata)
 *               1998 Bill Hibbard & al. (VisAD)
 *               1999 P�ches et Oc�ans Canada
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
 *
 *
 *    This package is inspired from the units package of VisAD.
 *    Unidata and Visad's work is fully acknowledged here.
 *
 *                   THIS IS A TEMPORARY CLASS
 *
 *    This is a placeholder for future <code>Unit</code> class.
 *    This skeleton will be removed when the real classes from
 *    JSR-108: Units specification will be publicly available.
 */
package org.deegree.model.csct.units;

// Entr�s/sorties
import java.io.Serializable;
import java.util.Arrays;

import org.deegree.model.csct.resources.WeakHashSet;

/**
 * Ensemble de pr�fix. Cette classe maintient une liste d'objets
 * {@link Prefix} en ordre croissant et sans doublons, c'est-�-dire qu'elle garanti
 * qu'il n'y aura pas deux pr�fix repr�sentant la m�me quantit� {@link Prefix#amount}.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
/*public*/final class PrefixSet implements Serializable {
    /**
     * Banque des objets qui ont �t� pr�c�demment cr��s et
     * enregistr�s par un appel � la m�thode {@link #intern}.
     */
    private static final WeakHashSet pool = Prefix.pool;

    /**
     * Ensemble de pr�fix. Les pr�fix de cet ensemble doivent
     * obligatoirement �tre un ordre croissant et sans doublons.
     */
    private final Prefix[] prefix;

    /**
     * Construit un ensemble de pr�fix. Le tableau <code>p</code>
     * sera copi�, puis class�. Les �ventuels doublons seront �limin�s.
     * Le tableau <code>p</code> original ne sera pas affect� par ces
     * traitements.
     */
    private PrefixSet( final Prefix[] p ) {
        final Prefix[] px = new Prefix[p.length];
        System.arraycopy( p, 0, px, 0, px.length );
        Arrays.sort( px );
        int length = px.length;
        for ( int i = length; --i >= 1; ) {
            if ( px[i].amount == px[i - 1].amount ) {
                px[i] = null;
                length--;
            }
        }
        int i = 0;
        prefix = new Prefix[length];
        for ( int j = 0; j < px.length; j++ )
            if ( px[j] != null )
                prefix[i++] = px[j];
        //----- BEGIN JDK 1.4 DEPENDENCIES ----
        //        assert i==length;
        //----- END OF JDK 1.4 DEPENDENCIES ----
    }

    /**
     * Construit un ensemble de pr�fix. Le tableau <code>p</code>
     * sera copi�, puis class�. Les �ventuels doublons seront �limin�s.
     * Le tableau <code>p</code> original ne sera pas affect� par ces
     * traitements.
     */
    public static PrefixSet getPrefixSet( final Prefix[] p ) {
        return new PrefixSet( p ).intern();
    }

    /**
     * Retourne le pr�fix repr�sent� par le symbole sp�fifi�.
     * Si aucun pr�fix ne correspond � ce symbole, retourne
     * <code>null</code>.
     *
     * @param  symbol Symbole du pr�fix recherch�.
     * @return Pr�fix d�sign� par le symbole <code>symbol</code>.
     */
    public Prefix getPrefix( final String symbol ) {
        for ( int i = 0; i < prefix.length; i++ ) {
            final Prefix p = prefix[i];
            if ( symbol.equals( p.symbol ) )
                return p;
        }
        return null;
    }

    /**
     * Retourne le pr�fix repr�sentant une quantit� �gale ou inf�rieure � la quantit� sp�cifi�e.
     * Si <code>amount</code> est inf�rieur � la plus petite quantit� pouvant �tre repr�sent�
     * par un pr�fix, alors cette m�thode retourne <code>null</code>.
     */
    public Prefix getPrefix( double amount ) {
        amount += 1E-8 * Math.abs( amount ); // Pour �viter d'�ventuelles erreurs d'arrondissements.
        int index = Arrays.binarySearch( prefix, new Prefix( amount ) );
        if ( index < 0 ) {
            index = ~index;
            if ( index == 0 )
                return null;
            if ( index > prefix.length )
                index = prefix.length;
            index--;
        }
        return prefix[index];
    }

    /**
     * Retourne une cha�ne de caract�res qui �num�re tous les pr�fix contenu dans
     * cet ensemble. La cha�ne sera de la forme "milli(m),centi(c),d�ci(d),kilo(k)"
     * par exemple.
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < prefix.length; i++ ) {
            final Prefix p = prefix[i];
            final String name = p.getLocalizedName();
            final String symb = p.symbol;
            if ( name.length() != 0 || symb.length() != 0 ) {
                if ( buffer.length() != 0 ) {
                    buffer.append( ',' );
                }
                buffer.append( name );
                if ( symb.length() != 0 ) {
                    buffer.append( '(' );
                    buffer.append( symb );
                    buffer.append( ')' );
                }
            }
        }
        return buffer.toString();
    }

    /**
     * V�rifie si cet ensemble est identique � l'objet <code>other</code>
     * sp�cifi�. Deux ensembles sont consid�r�s identiques s'ils contienent
     * les m�mes pr�fix.
     */
    public boolean equals( final Object other ) {
        if ( other == this )
            return true; // slight optimisation
        if ( other instanceof PrefixSet ) {
            final Prefix[] array = ( (PrefixSet) other ).prefix;
            if ( prefix.length == array.length ) {
                for ( int i = 0; i < array.length; i++ )
                    if ( !prefix[i].equals( array[i] ) )
                        return false;
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne un code repr�sentant cet ensemble de pr�fix.
     */
    public int hashCode() {
        int code = prefix.length << 1;
        for ( int i = 0; i < prefix.length; i += 5 )
            code += prefix[i].hashCode();
        return code;
    }

    /**
     * Retourne un exemplaire unique de cet ensemble de pr�fix. Une banque de pr�fix, initialement
     * vide, est maintenue de fa�on interne par la classe <code>PrefixSet</code>. Lorsque la m�thode
     * <code>intern</code> est appel�e, elle recherchera des pr�fix �gaux � <code>this</code> au
     * sens de la m�thode {@link #equals}. Si de tels pr�fix sont trouv�s, ils seront retourn�s.
     * Sinon, les pr�fix <code>this</code> seront ajout�s � la banque de donn�es en utilisant une
     * {@link java.lang.ref.WeakReference r�f�rence faible} et cette m�thode retournera <code>this</code>.
     * <br><br>
     * De cette m�thode il s'ensuit que pour deux ensembles de pr�fix <var>u</var> et <var>v</var>,
     * la condition <code>u.intern()==v.intern()</code> sera vrai si et seulement si
     * <code>u.equals(v)</code> est vrai.
     */
    private final PrefixSet intern() {
        return (PrefixSet) pool.intern( this );
    }

    /**
     * Apr�s la lecture d'une unit�, v�rifie si ce pr�fix
     * appara�t d�j� dans la banque des pr�fix .
     * Si oui, l'exemplaire de la banque sera retourn� plut�t
     * que de garder inutilement le pr�fix courant comme copie.
     */
    private Object readResolve() {
        return intern();
    }
}
