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

import org.deegree.model.csct.resources.WeakHashSet;

/**
 * Repr�sentation d'un pr�fix du syst�me m�trique. Un objet <code>Prefix</code>
 * peut par exemple repr�senter des "centi" (symbole "c") comme dans "centim�tres" (symbole "cm").
 * La description du paquet <code>javax.units</code> donne une liste des pr�fix standards du
 * syst�me SI.
 *
 * <p><em>Note: this class has a natural ordering that is inconsistent with equals.</em>
 * La m�thode {@link #compareTo} ne compare que le champ {@link #amount}, tandis que la m�thode
 * {@link #equals} compare tous les champs ({@link #name}, {@link #symbol} et {@link #amount}).</p>
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
/*public*/final class Prefix implements Comparable, Serializable {
    /**
     * Banque des objets qui ont �t� pr�c�demment cr��s et
     * enregistr�s par un appel � la m�thode {@link #intern}.
     */
    static final WeakHashSet pool = new WeakHashSet();

    /**
     * Nom neutre du pr�fix. Le syst�me SI d�finit plusieurs noms de pr�fix, parmi lesquels on trouve
     * "milli", "centi" et "kilo". Certaines unit�s (notamment des unit�s du type )
     * pourront combiner leurs noms avec un nom de pr�fix. Par exemple le pr�fix "centi" (symbole "c")
     * pourra �tre combin� avec les unit�s "m�tres" (symbole "m") pour former les "centim�tres" (symbole
     * "cm"). La cha�ne <code>name</code> peut �tre vide, mais ne sera jamais nulle. Notez enfin que
     * <code>name</code> est "language-neutral". Pour obtenir un nom dans la langue de l'utilisateur,
     * utilisez la m�thode {@link #getLocalizedName}.
     */
    public final String name;

    /**
     * Symbole du pr�fix. La plupart des symboles de pr�fix n'ont qu'une seule lettre. Il s'agit
     * la plupart du temps de la premi�re lettre de <code>name</code>, parfois en majuscule. Les
     * majuscules et minuscules sont significatifs et tr�s importants. Par exemple le symbole "m"
     * est pour "milli" tandis que le symbole "M" est pour "mega".
     */
    public final String symbol;

    /**
     * Quantit� repr�sent� par ce pr�fix. Pour les pr�fix SI, cette quantit� est toujours une puissance de 10.
     * Par exemple pour les "kilo" (symbole 'k'), la quantit� <code>amount</code> est 1000. Cette quantit� ne
     * sera jamais <code>NaN</code> ni infinie.
     */
    public final double amount;

    /**
     * Construit un pr�fix temporaire. Ce constructeur ne sert qu'� effectuer
     * des recherches dans une liste de pr�fix par {@link PrefixSet}.
     */
    Prefix( final double amount ) {
        this.name = "";
        this.symbol = "";
        this.amount = amount;
    }

    /**
     * Construit un nouveau pr�fix.
     *
     * @param name    Nom du pr�fix (par exemple "centi" comme dans "centim�tres").
     * @param symbol  Symbole du pr�fix (par exemple "c" pour "centim�tres").
     * @param amount  Quantit� repr�sent� par ce pr�fix (par exemple 0.01 pour "c").
     */
    private Prefix( final String name, final String symbol, final double amount ) {
        this.name = name.trim();
        this.symbol = symbol.trim();
        this.amount = amount;
        if ( !( amount > 0 ) || Double.isInfinite( amount ) )
            throw new IllegalArgumentException();
    }

    /**
     * Construit un nouveau pr�fix.
     *
     * @param name    Nom du pr�fix (par exemple "centi" comme dans "centim�tres").
     * @param symbol  Symbole du pr�fix (par exemple "c" pour "centim�tres").
     * @param amount  Quantit� repr�sent� par ce pr�fix (par exemple 0.01 pour "c").
     */
    public static Prefix getPrefix( final String name, final String symbol, final double amount ) {
        return new Prefix( name, symbol, amount ).intern();
    }

    /**
     * Retourne le nom du pr�fix dans la langue de l'utilisateur.
     * Par exemple le pr�fix "deci" est �crit "d�ci" en fran�ais.
     */
    public String getLocalizedName() {
        return org.deegree.model.csct.units.resources.Prefix.localize( name );
    }

    /**
     * Retourne le symbole du pr�fix. Cette m�thode retourne
     * syst�matiquement le champ {@link #symbol}.
     */
    public String toString() {
        return symbol;
    }

    /**
     * Compare deux pr�fix. Cette m�thode compare les quantit�s {@link #amount} de fa�on � permettre un classement
     * des pr�fix en ordre croissant de quantit�. Contrairement � la m�thode {@link #equals}, <code>compareTo</code>
     * ne compare pas les noms et symboles des pr�fix. Ainsi, deux pr�fix repr�sentant la m�me quantit� mais avec
     * des symboles diff�rents seront consid�r�s �gaux par <code>compareTo</code>.
     */
    public int compareTo( final Object object ) {
        final Prefix that = (Prefix) object;
        if ( this.amount > that.amount )
            return +1;
        if ( this.amount < that.amount )
            return -1;
        return 0;
    }

    /**
     * Indique si ce pr�fix est identique � l'objet sp�cifi�.
     * Cette m�thode retourne <code>true</code> si <code>object</code> est aussi un
     * objet <code>Prefix</code> et si les deux pr�fix ont les m�mes nom et symbole
     * et repr�sentent la m�me quantit� {@link #amount}.
     */
    public boolean equals( final Object object ) {
        if ( object == this )
            return true; // slight optimisation
        if ( object instanceof Prefix ) {
            final Prefix prefix = (Prefix) object;
            return Double.doubleToLongBits( amount ) == Double.doubleToLongBits( prefix.amount )
                   && symbol.equals( prefix.symbol ) && name.equals( prefix.name );
        }
        return false;
    }

    /**
     * Retourne un code repr�sentant ce pr�fix.
     */
    public int hashCode() {
        final long code = Double.doubleToLongBits( amount );
        return (int) code ^ (int) ( code >>> 32 );
    }

    /**
     * Retourne un exemplaire unique de ce pr�fix. Une banque de pr�fix, initialement
     * vide, est maintenue de fa�on interne par la classe <code>Prefix</code>. Lorsque
     * la m�thode <code>intern</code> est appel�e, elle recherchera un pr�fix �gale �
     * <code>this</code> au sens de la m�thode {@link #equals}. Si un tel pr�fix est
     * trouv�, il sera retourn�. Sinon, le pr�fix <code>this</code> sera ajout� � la
     * banque de donn�es en utilisant une r�f�rence faible et cette m�thode retournera
     * <code>this</code>.
     * <br><br>
     * De cette m�thode il s'ensuit que pour deux pr�fix <var>u</var> et <var>v</var>,
     * la condition <code>u.intern()==v.intern()</code> sera vrai si et seulement si
     * <code>u.equals(v)</code> est vrai.
     */
    private final Prefix intern() {
        return (Prefix) pool.intern( this );
    }

    /**
     * Apr�s la lecture d'une unit�, v�rifie si ce pr�fix
     * appara�t d�j� dans la banque des pr�fix. Si oui,
     * l'exemplaire de la banque sera retourn� plut�t
     * que de garder inutilement le pr�fix courant comme copie.
     */
    private Object readResolve() {
        return intern();
    }
}
