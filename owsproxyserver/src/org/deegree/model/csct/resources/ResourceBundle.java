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

// Utilities
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

/**
 * {link java.util.ResourceBundle} implementation using integers instead of strings for
 * resource keys.  Because it doesn't use strings, this implementation avoid adding all
 * those string constants to <code>.class</code> files and runtime images.  Developpers
 * still have meaningful labels in their code   (e.g.  <code>DIMENSION_MISMATCH</code>)
 * through a set of constants defined in interfaces. This approach furthermore give the
 * benefict of compile-time safety.    Because integer constants are inlined right into
 * class files at compile time, the declarative interface is never loaded at run time.
 * <br><br>
 * This class also provides facilities for string formatting using {@link MessageFormat}.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class ResourceBundle extends java.util.ResourceBundle {
    /**
     * Maximal string length for text inserted into an other text.   This parameter
     * is used by {@link #summarize}. Resource strings are never cut to this length.
     * However, text replacing "{0}" in a string like "Parameter name is {0}." will
     * be cut to this length.
     */
    private static final int MAX_STRING_LENGTH = 80;

    /**
     * The resource name of the binary file containing resources.
     * It is usually a file name, but may also be the name of an
     * entry in a JAR file.
     */
    private final String filename;

    /**
     * The array of resources.  Keys are array index. For example the value for
     * key "14" is <code>values[14]</code>. This array will be loaded only when
     * first needed.   We should not load it at construction time, because some
     * <code>ResourceBundle</code> objects will never ask for values. This is
     * case especially for ancestor classes of <code>Resources_fr_CA</code>,
     * <code>Resources_en</code>, <code>Resources_de</code>, etc., which will
     * be used only if a key has not been found in the subclasse.
     */
    protected String[] values;

    /**
     * The object to use for formatting messages. This
     * object will be constructed only when first needed.
     */
    private transient MessageFormat format;

    /**
     * The key of the last resource requested.    If the same resource is requested
     * many consecutive time, knowing this fact allows to avoid invoking the costly
     * {@link MessageFormat#applyPattern} method.
     */
    private transient int lastKey;

    /**
     * Construct a new resource bundle.
     *
     * @param filename The resource name containing resources. It is
     *        usually a filename, but may also be an entry in a JAR file.
     */
    protected ResourceBundle( final String filename ) {
        this.filename = filename;
    }

    /**
     * Returns the name of the logger to use. Default
     * implementation returns the package name.
     */
    protected String getLoggerName() {
        final String name = getClass().getName();
        final int index = name.lastIndexOf( '.' );
        return ( index >= 0 ) ? name.substring( 0, index ) : "org.deegree.model";
    }

    /**
     * List resources to the specified stream. If a resource has
     * more than one line, only the first line will be written.
     * This method is used mostly for debugging purpose.
     *
     * @param  out The destination stream.
     * @throws IOException if an output operation failed.
     */
    public final synchronized void list( final Writer out )
                            throws IOException {
        ensureLoaded( null );
        list( out, 0, values.length );
    }

    /**
     * List resources to the specified stream. If a resource has
     * more than one line, only the first line will be written.
     * This method is used mostly for debugging purpose.
     *
     * @param  out   The destination stream.
     * @param  lower The beginning index (inclusive).
     * @param  upper The ending index (exclusive).
     * @throws IOException if an output operation failed.
     */
    private void list( final Writer out, int lower, int upper )
                            throws IOException {
        final String lineSeparator = System.getProperty( "line.separator", "\n" );
        for ( int i = lower; i < upper; i++ ) {
            String value = values[i];
            if ( value == null )
                continue;
            int indexCR = value.indexOf( '\r' );
            if ( indexCR < 0 )
                indexCR = value.length();
            int indexLF = value.indexOf( '\n' );
            if ( indexLF < 0 )
                indexLF = value.length();
            final String number = String.valueOf( i );
            out.write( Utilities.spaces( 5 - number.length() ) );
            out.write( number );
            out.write( ":\t" );
            out.write( value.substring( 0, Math.min( indexCR, indexLF ) ) );
            out.write( lineSeparator );
        }
    }

    /**
     * Ensure that resource values are loaded.
     * If they are not, load them immediately.
     *
     * @param  key Key for the requested resource, or <code>null</code>
     *         if all resources are requested. This key is used mostly
     *         for constructing messages.
     * @throws MissingResourceException if this method failed to load resources.
     */
    private void ensureLoaded( final String key )
                            throws MissingResourceException {
        if ( values != null ) {
            return;
        }
        /*
         * Prepare a log record. We will wait for succesfull loading before to post this
         * record. If loading fail, the record will be changed into an error record.
         */
        try {
            final InputStream in = getClass().getClassLoader().getResourceAsStream( filename );
            if ( in == null ) {
                throw new FileNotFoundException( filename );
            }
            final DataInputStream input = new DataInputStream( new BufferedInputStream( in ) );
            values = new String[input.readInt()];
            for ( int i = 0; i < values.length; i++ ) {
                values[i] = input.readUTF();
                if ( values[i].length() == 0 )
                    values[i] = null;
            }
            input.close();

        } catch ( IOException exception ) {
            final MissingResourceException error = new MissingResourceException(
                                                                                 exception.getLocalizedMessage(),
                                                                                 getClass().getName(),
                                                                                 key );
            throw error;
        }
    }

    /**
     * Returns an enumeration of the keys.
     */
    public final synchronized Enumeration getKeys() {
        ensureLoaded( null );
        return new Enumeration() {
            private int i = 0;

            public boolean hasMoreElements() {
                while ( true ) {
                    if ( i >= values.length )
                        return false;
                    if ( values[i] != null )
                        return true;
                    i++;
                }
            }

            public Object nextElement() {
                while ( true ) {
                    if ( i >= values.length )
                        throw new NoSuchElementException();
                    if ( values[i] != null )
                        return String.valueOf( i++ );
                    i++;
                }
            }
        };
    }

    /**
     * Gets an object for the given key from this resource bundle.
     * Returns null if this resource bundle does not contain an
     * object for the given key.
     *
     * @param key the key for the desired object
     * @exception NullPointerException if <code>key</code> is <code>null</code>
     * @return the object for the given key, or null
     */
    protected final synchronized Object handleGetObject( final String key ) {
        ensureLoaded( key );
        final int keyID;
        try {
            keyID = Integer.parseInt( key );
        } catch ( NumberFormatException exception ) {
            return null;
        }
        return ( keyID >= 0 && keyID < values.length ) ? values[keyID] : null;
    }

    /**
     * Make sure that the <code>text</code> string is no longer than <code>maxLength</code>
     * characters. If <code>text</code> is not longer, it is returned unchanged (except for
     * trailing blancks, which are removed). If <code>text</code> is longer, it will be cut
     * somewhere in the middle. This method try to cut between two words and replace the
     * missing words with "(...)". For example, the following string:
     *
     * <blockquote>
     *     "This sentence given as an example is way too long to be included in a message."
     * </blockquote>
     *
     * May be "summarized" by something like this:
     *
     * <blockquote>
     *     "This sentence given (...) included in a message."
     * </blockquote>
     *
     * @param  text The sentence to summarize if it is too long.
     * @param  maxLength The maximal length allowed for <code>text</code>.
     *         If <code>text</code> is longer, it will summarized.
     * @return A sentence not longer than <code>text</code>.
     */
    private static String summarize( String text, int maxLength ) {
        text = text.trim();
        final int length = text.length();
        if ( length <= maxLength )
            return text;
        /*
         * Compute maximum length for one half of the string. Take in
         * account the space needed for inserting the " (...) " string.
         */
        maxLength = ( maxLength - 7 ) >> 1;
        if ( maxLength <= 0 )
            return text;
        /*
         * We will remove characters from 'break1' to 'break2', both exclusive.
         * We try to adjust 'break1' and 'break2' in such a way that first and
         * last removed characters will be spaces or punctuation characters.
         * Constants 'lower' and 'upper' are limit values. If we don't find values
         * for 'break1' and 'break2' inside those limits, we will give it up.
         */
        int break1 = maxLength;
        int break2 = length - maxLength;
        for ( final int lower = ( maxLength >> 1 ); break1 >= lower; break1-- ) {
            if ( !Character.isUnicodeIdentifierPart( text.charAt( break1 ) ) ) {
                while ( --break1 >= lower
                        && !Character.isUnicodeIdentifierPart( text.charAt( break1 ) ) )
                    ;
                break;
            }
        }
        for ( final int upper = length - ( maxLength >> 1 ); break2 < upper; break2++ ) {
            if ( !Character.isUnicodeIdentifierPart( text.charAt( break2 ) ) ) {
                while ( ++break2 < upper
                        && !Character.isUnicodeIdentifierPart( text.charAt( break2 ) ) )
                    ;
                break;
            }
        }
        return ( text.substring( 0, break1 + 1 ) + " (...) " + text.substring( break2 ) ).trim();
    }

    /**
     * Returns <code>arguments</code> as an array. If <code>arguments</code> is already an
     * array, this array or a copy of this array will be returned. If <code>arguments</code>
     * is not an array, it will be wrapped in an array of length 1. In all case, all array's
     * elements will be checked for {@link String} objects. Any strings of length greater than
     * {@link #MAX_STRING_LENGTH} will be reduced using the {@link #summarize} method.
     *
     * @param  arguments The object to check.
     * @return <code>arguments</code> as an array.
     */
    private static Object[] toArray( final Object arguments ) {
        Object[] array;
        if ( arguments instanceof Object[] ) {
            array = (Object[]) arguments;
        } else {
            array = new Object[] { arguments };
        }
        for ( int i = 0; i < array.length; i++ ) {
            {
                final String s0 = array[i].toString();
                final String s1 = summarize( s0, MAX_STRING_LENGTH );
                if ( s0 != s1 && !s0.equals( s1 ) ) {
                    if ( array == arguments ) {
                        array = new Object[array.length];
                        System.arraycopy( arguments, 0, array, 0, array.length );
                    }
                    array[i] = s1;
                }
            }
        }
        return array;
    }

    /**
     * Gets a string for the given key and append "..." to it.
     * This is method is typically used for creating menu label.
     *
     * @param  keyID The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getMenuLabel( final int keyID )
                            throws MissingResourceException {
        return getString( keyID ) + "...";
    }

    /**
     * Gets a string for the given key and append ": " to it.
     * This is method is typically used for creating menu label.
     *
     * @param  keyID The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getLabel( final int keyID )
                            throws MissingResourceException {
        return getString( keyID ) + ": ";
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  keyID The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getString( final int keyID )
                            throws MissingResourceException {
        return getString( String.valueOf( keyID ) );
    }

    /**
     * Gets a string for the given key and format it with the specified argument.
     * The message if formatted using {@link MessageFormat}. Calling his method is
     * approximatively equivalent to calling:
     *
     * <blockquote><pre>
     *     String pattern = getString(key);
     *     Format f = new MessageFormat(pattern);
     *     return f.format(arg0);
     * </pre></blockquote>
     *
     * If <code>arg0</code> is not already an array, it will be wrapped into an array
     * of length 1. Using {@link MessageFormat}, all occurence of "{0}", "{1}", "{2}"
     * in the resource string will be replaced by <code>arg0[0]</code>, <code>arg0[1]</code>,
     * <code>arg0[2]</code>, etc.
     *
     * @param  keyID The key for the desired string.
     * @param  arg0 A single object or an array of objects to be formatted and substituted.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     *
     * @see MessageFormat
     */
    public final synchronized String getString( final int keyID, final Object arg0 )
                            throws MissingResourceException {
        final Object object = getObject( String.valueOf( keyID ) );
        final Object[] arguments = toArray( arg0 );
        if ( format == null ) {
            /*
             * Construct a new {@link MessageFormat} for formatting the arguments. There is two
             * possible {@link Locale} we could use:  default locale or resource bundle locale.
             * If the default locale use the same language than this <code>ResourceBundle</code>
             * locale, then we will use the default locale. This allow formatting dates and numbers
             * with user conventions (e.g. French Canada) even if the <code>ResourceBundle</code>
             * locale is different (e.g. standard French). However, if languages don't match, then
             * we will use <code>ResourceBundle</code> locale for better coherence.
             */
            Locale locale = Locale.getDefault();
            final Locale resourceLocale = getLocale();
            if ( !locale.getLanguage().equalsIgnoreCase( resourceLocale.getLanguage() ) ) {
                locale = resourceLocale;
            }
        } else if ( keyID != lastKey ) {
            /*
             * Method {@link MessageFormat#applyPattern} is costly! We will avoid
             * calling it again if {@link #format} already has the right pattern.
             */
            format.applyPattern( object.toString() );
            lastKey = keyID;
        }
        return format.format( arguments );
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of <code>arg0</code>, <code>arg1</code>, etc.
     *
     * @param  keyID The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getString( final int keyID, final Object arg0, final Object arg1 )
                            throws MissingResourceException {
        return getString( keyID, new Object[] { arg0, arg1 } );
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of <code>arg0</code>, <code>arg1</code>, etc.
     *
     * @param  keyID The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getString( final int keyID, final Object arg0, final Object arg1,
                                  final Object arg2 )
                            throws MissingResourceException {
        return getString( keyID, new Object[] { arg0, arg1, arg2 } );
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of <code>arg0</code>, <code>arg1</code>, etc.
     *
     * @param  keyID The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @param  arg3 Value to substitute to "{3}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getString( final int keyID, final Object arg0, final Object arg1,
                                  final Object arg2, final Object arg3 )
                            throws MissingResourceException {
        return getString( keyID, new Object[] { arg0, arg1, arg2, arg3 } );
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of <code>arg0</code>, <code>arg1</code>, etc.
     *
     * @param  keyID The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @param  arg3 Value to substitute to "{3}".
     * @param  arg4 Value to substitute to "{4}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public final String getString( final int keyID, final Object arg0, final Object arg1,
                                  final Object arg2, final Object arg3, final Object arg4 )
                            throws MissingResourceException {
        return getString( keyID, new Object[] { arg0, arg1, arg2, arg3, arg4 } );
    }

    /**
     * Returns a string representation of this object.
     * This method is for debugging purpose only.
     */
    public synchronized String toString() {
        final StringBuffer buffer = new StringBuffer( Utilities.getShortClassName( this ) );
        buffer.append( '[' );
        if ( values != null ) {
            int count = 0;
            for ( int i = 0; i < values.length; i++ )
                if ( values[i] != null )
                    count++;
            buffer.append( count );
            buffer.append( " values" );
        }
        buffer.append( ']' );
        return buffer.toString();
    }
}
