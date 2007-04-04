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
package org.deegree.model.csct.resources;

// Collections
import java.util.Arrays;

/**
 * A set of miscellaneous methods.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public final class Utilities {
    /**
     * An array of strings containing only white spaces. String length are equal
     * to their index + 1 in the <code>spacesFactory</code> array.  For example,
     * <code>spacesFactory[4]</code> contains a string of length 5.  Strings are
     * constructed only when first needed.
     */
    private static final String[] spacesFactory = new String[20];

    /**
     * Forbive object creation.
     */
    private Utilities() {
    }

    /**
     * Determines whether the character is a superscript. Most superscripts have
     * unicode value from \\u2070 to \\u207F inclusive. Superscripts are the
     * following symbols:
     *
     * <blockquote><pre>
     * \u2070 \u00B9 \u00B2 \u00B3 \u2074 \u2075 \u2076 \u2077 \u2078 \u2079 \u207A \u207B \u207C \u207D \u207E \u207F
     * </pre></blockquote>
     */
    public static boolean isSuperScript( final char c ) {
        switch ( c ) {
        /*1*/case '\u2071':
        /*2*/case '\u2072':
        /*3*/case '\u2073':
            return false;
        /*1*/case '\u00B9':
        /*2*/case '\u00B2':
        /*3*/case '\u00B3':
            return true;
        }
        return ( c >= '\u2070' && c <= '\u207F' );
    }

    /**
     * Determines whether the character is a subscript. Most subscripts have
     * unicode value from \\u2080 ti \\u208E inclusive. Subscripts are the
     * following symbols:
     *
     * <blockquote><pre>
     * \u2080 \u2081 \u2082 \u2083 \u2084 \u2085 \u2086 \u2087 \u2088 \u2089 \u208A \u208B \u208C \u208D \u208E
     * </pre></blockquote>
     */
    public static boolean isSubScript( final char c ) {
        return ( c >= '\u2080' && c <= '\u208E' );
    }

    /**
     * Converts the character argument to superscript.
     * Only the following characters can be converted
     * (other characters are left unchanged):
     *
     * <blockquote><pre>
     * 0 1 2 3 4 5 6 7 8 9 + - = ( ) n
     * </pre></blockquote>
     */
    public static char toSuperScript( final char c ) {
        switch ( c ) {
        case '1':
            return '\u00B9';
        case '2':
            return '\u00B2';
        case '3':
            return '\u00B3';
        case '+':
            return '\u207A';
        case '-':
            return '\u207B';
        case '=':
            return '\u207C';
        case '(':
            return '\u207D';
        case ')':
            return '\u207E';
        case 'n':
            return '\u207F';
        }
        if ( c >= '0' && c <= '9' )
            return (char) ( c + ( '\u2070' - '0' ) );
        return c;
    }

    /**
     * Converts the character argument to subscript.
     * Only the following characters can be converted
     * (other characters are left unchanged):
     *
     * <blockquote><pre>
     * 0 1 2 3 4 5 6 7 8 9 + - = ( ) n
     * </pre></blockquote>
     */
    public static char toSubScript( final char c ) {
        switch ( c ) {
        case '+':
            return '\u208A';
        case '-':
            return '\u208B';
        case '=':
            return '\u208C';
        case '(':
            return '\u208D';
        case ')':
            return '\u208E';
        }
        if ( c >= '0' && c <= '9' )
            return (char) ( c + ( '\u2080' - '0' ) );
        return c;
    }

    /**
     * Converts the character argument to normal script.
     */
    public static char toNormalScript( final char c ) {
        switch ( c ) {
        case '\u00B9':
            return '1';
        case '\u00B2':
            return '2';
        case '\u00B3':
            return '3';
        case '\u2071':
            return c;
        case '\u2072':
            return c;
        case '\u2073':
            return c;
        case '\u207A':
            return '+';
        case '\u207B':
            return '-';
        case '\u207C':
            return '=';
        case '\u207D':
            return '(';
        case '\u207E':
            return ')';
        case '\u207F':
            return 'n';
        case '\u208A':
            return '+';
        case '\u208B':
            return '-';
        case '\u208C':
            return '=';
        case '\u208D':
            return '(';
        case '\u208E':
            return ')';
        }
        if ( c >= '\u2070' && c <= '\u2079' )
            return (char) ( c - ( '\u2070' - '0' ) );
        if ( c >= '\u2080' && c <= '\u2089' )
            return (char) ( c - ( '\u2080' - '0' ) );
        return c;
    }

    /**
     * Returns a string of the specified length filled with white spaces.
     * This method try to returns a pre-allocated string if possible.
     *
     * @param  length The string length. Negative values are clamp to 0.
     * @return A string of length <code>length</code> filled with with spaces.
     */
    public static String spaces( int length ) {
        // No need to synchronize.   In the unlikely case where two threads
        // call this method in the same time and the two calls create a new
        // string,  the String.intern() call will take care to canonicalize
        // the strings.
        final int last = spacesFactory.length - 1;
        if ( length < 0 )
            length = 0;
        if ( length <= last ) {
            if ( spacesFactory[length] == null ) {
                if ( spacesFactory[last] == null ) {
                    char[] blancs = new char[last];
                    Arrays.fill( blancs, ' ' );
                    spacesFactory[last] = new String( blancs ).intern();
                }
                spacesFactory[length] = spacesFactory[last].substring( 0, length ).intern();
            }
            return spacesFactory[length];
        } 
        char[] blancs = new char[length];
        Arrays.fill( blancs, ' ' );
        return new String( blancs );
        
    }

    /**
     * Returns a short class name for the specified class. This method will
     * ommit the package name. For exemple, it will returns "String" instead
     * of "java.lang.String" for a {@link String} object.
     *
     * @param  classe The object (may be <code>null</code>).
     * @return A short class name for the specified object.
     */
    public static String getShortName( final Class classe ) {
        if ( classe == null )
            return "<*>";
        String name = classe.getName();
        int lower = name.lastIndexOf( '.' );
        int upper = name.length();
        return name.substring( lower + 1, upper ).replace( '$', '.' );
    }

    /**
     * Returns a short class name for the specified object. This method will
     * ommit the package name. For exemple, it will returns "String" instead
     * of "java.lang.String" for a {@link String} object.
     *
     * @param  object The object (may be <code>null</code>).
     * @return A short class name for the specified object.
     */
    public static String getShortClassName( final Object object ) {
        return getShortName( object != null ? object.getClass() : null );
    }

    /**
     * Convenience method for testing two objects for
     * equality. One or both objects may be null.
     */
    public static boolean equals( final Object object1, final Object object2 ) {
        return ( object1 == object2 ) || ( object1 != null && object1.equals( object2 ) );
    }

   
}
