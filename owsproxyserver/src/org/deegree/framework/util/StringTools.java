/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

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

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * this is a collection of some methods that extends the functionallity of the sun-java string
 * class.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mays $
 * 
 * @version $Revision: 1.21 $, $Date: 2006/11/13 11:02:29 $
 */
public class StringTools {

    /**
     * This map is used for methods normalizeString() and initMap().
     * 
     * key = locale language, e.g. "de"
     * value = map of substitution rules for this locale
     */
    private static Map<String, Map<String, String>> localeMap;

    /**
     * concatenates an array of strings using a
     * 
     * @see StringBuffer
     * 
     * @param size
     *            estimated size of the target string
     * @param objects
     *            toString() will be called for each object to append it to the result string
     * @return
     */
    public static String concat( int size, Object... objects ) {
        StringBuilder sbb = new StringBuilder( size );
        for ( int i = 0; i < objects.length; i++ ) {
            sbb.append( objects[i] );
        }
        return sbb.toString();
    }

    /**
     * replaces occurences of a string fragment within a string by a new string.
     * 
     * @param target
     *            is the original string
     * @param from
     *            is the string to be replaced
     * @param to
     *            is the string which will used to replace
     * @param all
     *            if it's true all occurences of the string to be replaced will be replaced. else
     *            only the first occurence will be replaced.
     */
    public static String replace( String target, String from, String to, boolean all ) {

        StringBuffer buffer = new StringBuffer( target.length() );
        int copyFrom = 0;
        char[] targetChars = null;
        int lf = from.length();
        int start = -1;
        do {
            start = target.indexOf( from );
            copyFrom = 0;
            if ( start == -1 ) {
                return target;
            }

            targetChars = target.toCharArray();
            while ( start != -1 ) {
                buffer.append( targetChars, copyFrom, start - copyFrom );
                buffer.append( to );
                copyFrom = start + lf;
                start = target.indexOf( from, copyFrom );
                if ( !all ) {
                    start = -1;
                }
            }
            buffer.append( targetChars, copyFrom, targetChars.length - copyFrom );
            target = buffer.toString();
            buffer.delete( 0, buffer.length() );
        } while ( target.indexOf( from ) > -1 && to.indexOf( from ) < 0 );

        return target;
    }

    /**
     * replaces all special german letters (umlaute)
     * 
     * draft version
     */
    public static String HTMLEncode( String source ) {
        String target = null;
        target = StringTools.replace( source, "�", "&auml;", true );
        target = StringTools.replace( target, "�", "&Auml;", true );
        target = StringTools.replace( target, "�", "&ouml;", true );
        target = StringTools.replace( target, "�", "&Ouml;", true );
        target = StringTools.replace( target, "�", "&uuml;", true );
        target = StringTools.replace( target, "�", "&Uuml;", true );
        target = StringTools.replace( target, "�", "&szlig;", true );

        return target;
    }

    /**
     * parse a string and return its tokens as array
     * 
     * @param s
     *            string to parse
     * @param delimiter
     *            delimiter that marks the end of a token
     * @param deleteDoubles
     *            if it's true all string that are already within the resulting array will be
     *            deleted, so that there will only be one copy of them.
     */
    public static String[] toArray( String s, String delimiter, boolean deleteDoubles ) {
        if ( s == null || s.equals( "" ) ) {
            return new String[0];
        }

        StringTokenizer st = new StringTokenizer( s, delimiter );
        ArrayList vec = new ArrayList( st.countTokens() );
        for ( int i = 0; st.hasMoreTokens(); i++ ) {
            String t = st.nextToken();
            if ( ( t != null ) && ( t.length() > 0 ) ) {
                vec.add( t.trim() );
            }
        }

        // no value selected
        if ( vec.size() == 0 ) {
            return new String[0];
        }

        String[] kw = (String[]) vec.toArray( new String[vec.size()] );
        if ( deleteDoubles ) {
            kw = deleteDoubles( kw );
        }

        return kw;
    }

    /**
     * parse a string and return its tokens as typed List
     * 
     * @param s
     *            string to parse
     * @param delimiter
     *            delimiter that marks the end of a token
     * @param deleteDoubles
     *            if it's true all string that are already within the resulting array will be
     *            deleted, so that there will only be one copy of them.
     * @return
     */
    public static List<String> toList( String s, String delimiter, boolean deleteDoubles ) {
        if ( s == null || s.equals( "" ) ) {
            return new ArrayList<String>();
        }

        StringTokenizer st = new StringTokenizer( s, delimiter );
        ArrayList<String> vec = new ArrayList<String>( st.countTokens() );
        for ( int i = 0; st.hasMoreTokens(); i++ ) {
            String t = st.nextToken();
            if ( ( t != null ) && ( t.length() > 0 ) ) {
                if ( deleteDoubles ) {
                    if ( !vec.contains( t.trim() ) ) {
                        vec.add( t.trim() );
                    }
                } else {
                    vec.add( t.trim() );
                }
            }
        }

        return vec;
    }

    /**
     * transforms a string array to one string. the array fields are seperated by the submitted
     * delimiter:
     * 
     * @param s
     *            stringarray to transform
     * @param delimiter
     */
    public static String arrayToString( String[] s, char delimiter ) {
        StringBuffer res = new StringBuffer( s.length * 20 );

        for ( int i = 0; i < s.length; i++ ) {
            res.append( s[i] );

            if ( i < ( s.length - 1 ) ) {
                res.append( delimiter );
            }
        }

        return res.toString();
    }

    /**
     * transforms a list to one string. the array fields are seperated by the submitted delimiter:
     * 
     * @param s
     *            stringarray to transform
     * @param delimiter
     */
    public static String listToString( List s, char delimiter ) {
        StringBuffer res = new StringBuffer( s.size() * 20 );

        for ( int i = 0; i < s.size(); i++ ) {
            res.append( s.get( i ) );

            if ( i < ( s.size() - 1 ) ) {
                res.append( delimiter );
            }
        }

        return res.toString();
    }

    /**
     * transforms a double array to one string. the array fields are seperated by the submitted
     * delimiter:
     * 
     * @param s
     *            stringarray to transform
     * @param delimiter
     */
    public static String arrayToString( double[] s, char delimiter ) {
        StringBuffer res = new StringBuffer( s.length * 20 );

        for ( int i = 0; i < s.length; i++ ) {
            res.append( Double.toString( s[i] ) );

            if ( i < ( s.length - 1 ) ) {
                res.append( delimiter );
            }
        }

        return res.toString();
    }

    /**
     * transforms a int array to one string. the array fields are seperated by the submitted
     * delimiter:
     * 
     * @param s
     *            stringarray to transform
     * @param delimiter
     */
    public static String arrayToString( int[] s, char delimiter ) {
        StringBuffer res = new StringBuffer( s.length * 20 );

        for ( int i = 0; i < s.length; i++ ) {
            res.append( Integer.toString( s[i] ) );

            if ( i < ( s.length - 1 ) ) {
                res.append( delimiter );
            }
        }

        return res.toString();
    }

    /**
     * clears the begin and end of a string from the strings sumitted
     * 
     * @param s
     *            string to validate
     * @param mark
     *            string to remove from begin and end of <code>s</code>
     */
    public static String validateString( String s, String mark ) {
        if ( s == null ) {
            return null;
        }

        if ( s.length() == 0 ) {
            return s;
        }

        s = s.trim();

        while ( s.startsWith( mark ) ) {
            s = s.substring( mark.length(), s.length() ).trim();
        }

        while ( s.endsWith( mark ) ) {
            s = s.substring( 0, s.length() - mark.length() ).trim();
        }

        return s;
    }

    /**
     * deletes all double entries from the submitted array
     */
    public static String[] deleteDoubles( String[] s ) {
        ArrayList vec = new ArrayList( s.length );

        for ( int i = 0; i < s.length; i++ ) {
            if ( !vec.contains( s[i] ) ) {
                vec.add( s[i] );
            }
        }

        return (String[]) vec.toArray( new String[vec.size()] );
    }

    /**
     * removes all fields from the array that equals <code>s</code>
     * 
     * @param target
     *            array where to remove the submitted string
     * @param s
     *            string to remove
     */
    public static String[] removeFromArray( String[] target, String s ) {
        ArrayList vec = new ArrayList( target.length );

        for ( int i = 0; i < target.length; i++ ) {
            if ( !target[i].equals( s ) ) {
                vec.add( target[i] );
            }
        }

        return (String[]) vec.toArray( new String[vec.size()] );
    }

    /**
     * checks if the submitted array contains the string <code>value</code>
     * 
     * @param target
     *            array to check if it contains <code>value</code>
     * @param value
     *            string to check if it within the array
     */
    public static boolean contains( String[] target, String value ) {
        if ( target == null || value == null ) {
            return false;
        }

        if ( value.endsWith( "," ) ) {
            value = value.substring( 0, value.length() - 1 );
        }

        for ( int i = 0; i < target.length; i++ ) {
            if ( value.equalsIgnoreCase( target[i] ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * convert the array of string like [(x1,y1),(x2,y2)...] into an array of double
     * [x1,y1,x2,y2...]
     * 
     * @param s
     * @param delimiter
     * 
     * @return
     */
    public static double[] toArrayDouble( String s, String delimiter ) {
        if ( s == null ) {
            return null;
        }

        if ( s.equals( "" ) ) {
            return null;
        }

        StringTokenizer st = new StringTokenizer( s, delimiter );

        ArrayList vec = new ArrayList( st.countTokens() );

        for ( int i = 0; st.hasMoreTokens(); i++ ) {
            String t = st.nextToken().replace( ' ', '+' );

            if ( ( t != null ) && ( t.length() > 0 ) ) {
                vec.add( t.trim() );
            }
        }

        double[] array = new double[vec.size()];

        for ( int i = 0; i < vec.size(); i++ ) {
            array[i] = Double.parseDouble( (String) vec.get( i ) );
        }

        return array;
    }

    /**
     * convert the array of string like [(x1,y1),(x2,y2)...] into an array of 
     * float values [x1,y1,x2,y2...]
     * 
     * @param s
     * @param delimiter
     * 
     * @return
     */
    public static float[] toArrayFloat( String s, String delimiter ) {
        if ( s == null ) {
            return null;
        }

        if ( s.equals( "" ) ) {
            return null;
        }

        StringTokenizer st = new StringTokenizer( s, delimiter );

        ArrayList vec = new ArrayList( st.countTokens() );
        for ( int i = 0; st.hasMoreTokens(); i++ ) {
            String t = st.nextToken().replace( ' ', '+' );
            if ( ( t != null ) && ( t.length() > 0 ) ) {
                vec.add( t.trim() );
            }
        }

        float[] array = new float[vec.size()];

        for ( int i = 0; i < vec.size(); i++ ) {
            array[i] = Float.parseFloat( (String) vec.get( i ) );
        }

        return array;
    }

    /**
     * transforms an array of StackTraceElements into a String
     */
    public static String stackTraceToString( StackTraceElement[] se ) {

        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < se.length; i++ ) {
            sb.append( se[i].getClassName() + " " );
            sb.append( se[i].getFileName() + " " );
            sb.append( se[i].getMethodName() + "(" );
            sb.append( se[i].getLineNumber() + ")\n" );
        }
        return sb.toString();
    }

    /**
     * gets the stacktrace array from the passed Excption and transforms it into a String
     */
    public static String stackTraceToString( Throwable e ) {

        StackTraceElement[] se = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append( e.getMessage() ).append( "\n" );
        sb.append( e.getClass().getName() ).append( "\n" );
        for ( int i = 0; i < se.length; i++ ) {
            sb.append( se[i].getClassName() + " " );
            sb.append( se[i].getFileName() + " " );
            sb.append( se[i].getMethodName() + "(" );
            sb.append( se[i].getLineNumber() + ")\n" );
            if ( i > 4 )
                break;
        }
        return sb.toString();
    }

    /**
     * countString count the occurrences of token into target
     * 
     * @param target
     * @param token
     * 
     * @return
     */
    public static int countString( String target, String token ) {
        int start = target.indexOf( token );
        int count = 0;

        while ( start != -1 ) {
            count++;
            start = target.indexOf( token, start + 1 );
        }

        return count;
    }

    /**
     * Extract all the strings that begin with "start" and end with "end" and store it into an array
     * of String
     * 
     * @param target
     * @param startString
     * @param endString
     * 
     * @return
     */
    public static String[] extractStrings( String target, String startString, String endString ) {
        int start = target.indexOf( startString );

        if ( start == -1 ) {
            return null;
        }

        int count = countString( target, startString );
        String[] subString = null;
        if ( startString.equals( endString ) ) {
            count = count / 2;
            subString = new String[count];
            for ( int i = 0; i < count; i++ ) {
                int tmp = target.indexOf( endString, start + 1 );
                subString[i] = target.substring( start, tmp + 1 );
                start = target.indexOf( startString, tmp + 1 );
            }
        } else {
            subString = new String[count];
            for ( int i = 0; i < count; i++ ) {
                subString[i] = target.substring( start, target.indexOf( endString, start + 1 ) + 1 );
                subString[i] = extractString( subString[i], startString, endString, true, true );
                start = target.indexOf( startString, start + 1 );
            }
        }

        return subString;
    }

    /**
     * extract a string contained between startDel and endDel, you can remove the delimiters if set
     * true the parameters delStart and delEnd
     * 
     * @param target
     * @param startDel
     * @param endDel
     * @param delStart
     * @param delEnd
     * 
     * @return
     */
    public static String extractString( String target, String startDel, String endDel,
                                       boolean delStart, boolean delEnd ) {
        int start = target.indexOf( startDel );

        if ( start == -1 ) {
            return null;
        }

        String s = target.substring( start, target.indexOf( endDel, start + 1 ) + 1 );

        s = s.trim();

        if ( delStart ) {
            while ( s.startsWith( startDel ) ) {
                s = s.substring( startDel.length(), s.length() ).trim();
            }
        }

        if ( delEnd ) {
            while ( s.endsWith( endDel ) ) {
                s = s.substring( 0, s.length() - endDel.length() ).trim();
            }
        }

        return s;
    }

    /**
     * Initialize the substitution map with all normalization rules for a given locale and 
     * add this map to the static localeMap.
     * 
     * @param locale
     * @throws IOException
     * @throws SAXException
     * @throws XMLParsingException
     */
    private static void initMap( String locale )
                            throws IOException, SAXException, XMLParsingException {

        // read normalization file
        StringBuffer sb = new StringBuffer( 1000 );
        InputStream is = StringTools.class.getResourceAsStream( "normalization.xml" );
        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        String s = null;
        while ( ( s = br.readLine() ) != null ) {
            sb.append( s );
        }
        br.close();

        // transform into xml fragment
        XMLFragment xml = new XMLFragment();
        xml.load( new StringReader( sb.toString() ),
                  StringTools.class.getResource( "normalization.xml" ).toString() ); //FIXME

        // create map
        Map<String, String> substitutionMap = new HashMap<String, String>( 20 );

        // extract case attrib ( "toLower" or "toUpper" or missing ) for passed locale
        String xpath = "Locale[@name = '" + Locale.GERMANY.getLanguage() + "']/@case";
        String letterCase = XMLTools.getNodeAsString( xml.getRootElement(), xpath,
                                                      CommonNamespaces.getNamespaceContext(), null );
        if ( letterCase != null ) {
            substitutionMap.put( "case", letterCase );
        }

        // extract removeDoubles attrib ( "true" or "false" ) for passed locale
        xpath = "Locale[@name = '" + Locale.GERMANY.getLanguage() + "']/@removeDoubles";
        String removeDoubles = XMLTools.getNodeAsString( xml.getRootElement(), xpath,
                                                         CommonNamespaces.getNamespaceContext(),
                                                         null );
        if ( removeDoubles != null && removeDoubles.length() > 0 ) {
            substitutionMap.put( "removeDoubles", removeDoubles );
        }

        // extract rules section for passed locale
        xpath = "Locale[@name = '" + locale + "']/Rule";
        List list = XMLTools.getNodes( xml.getRootElement(), xpath,
                                       CommonNamespaces.getNamespaceContext() );
        if ( list != null ) {
            for ( int i = 0; i < list.size(); i++ ) {
                String src = XMLTools.getRequiredNodeAsString( (Node) list.get( i ),
                                                               "Source",
                                                               CommonNamespaces.getNamespaceContext() );
                String target = XMLTools.getRequiredNodeAsString( (Node) list.get( i ),
                                                                  "Target",
                                                                  CommonNamespaces.getNamespaceContext() );
                substitutionMap.put( src, target );
            }
        }

        // init localeMap if needed
        if ( localeMap == null ) {
            localeMap = new HashMap<String, Map<String, String>>( 20 );
        }

        localeMap.put( locale, substitutionMap );
    }

    /**
     * The passed string gets normalized along the rules for the given locale as they are set in 
     * the file "./normalization.xml". 
     * If such rules are specified, the following order is obeyed:
     * 
     * <ol>
     * <li>if the attribute "case" is set with "toLower" or "toUpper", the letters are switched 
     * to lower case or to upper case respectively.</li>
     * <li>all rules given in the "Rule" elements are performed.</li>
     * <li>if the attribute "removeDoubles" is set and not empty, all multi occurences of the 
     * letters given in this attribute are reduced to a single occurence.</li>
     * </ol>
     * 
     * @param source the String to normalize
     * @param locale the locale language defining the rules to choose, e.g. "de" 
     * @return the normalized String
     * @throws IOException
     * @throws SAXException
     * @throws XMLParsingException
     */
    public static String normalizeString( String source, String locale )
                            throws IOException, SAXException, XMLParsingException {

        if ( localeMap == null ) {
            localeMap = new HashMap<String, Map<String, String>>( 20 );
        }
        Map<String, String> substitutionMap = localeMap.get( locale );

        if ( substitutionMap == null ) {
            initMap( locale );
        }
        substitutionMap = localeMap.get( locale );

        String output = source;
        Set<String> keys = substitutionMap.keySet();

        boolean toUpper = false;
        boolean toLower = false;
        boolean removeDoubles = false;

        for ( String key : keys ) {
            if ( "case".equals( key ) ) {
                toUpper = "toUpper".equals( substitutionMap.get( key ) );
                toLower = "toLower".equals( substitutionMap.get( key ) );
            }
            if ( "removeDoubles".equals( key ) && substitutionMap.get( key ).length() > 0 ) {
                removeDoubles = true;
            }
        }

        // first: change letters to upper / lower case
        if ( toUpper ) {
            output = output.toUpperCase();
        } else if ( toLower ) {
            output = output.toLowerCase();
        }

        // second: change string according to specified rules
        for ( String key : keys ) {
            if ( !"case".equals( key ) && !"removeDoubles".equals( key ) ) {
                output = output.replaceAll( key, substitutionMap.get( key ) );
            }
        }

        // third: remove doubles
        if ( removeDoubles ) {
            String doubles = substitutionMap.get( "removeDoubles" );
            for ( int i = 0; i < doubles.length(); i++ ) {
                String remove = "" + doubles.charAt( i ) + "+";
                String replaceWith = "" + doubles.charAt( i );
                output = output.replaceAll( remove, replaceWith );
            }
        }

        return output;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: StringTools.java,v $
 Revision 1.21  2006/11/13 11:02:29  mays
 add normalizeString() initMap() and localeMap

 Revision 1.20  2006/09/11 11:25:40  poth
 mathod toArrayFloat added

 Revision 1.19  2006/09/07 12:50:19  poth
 bug fix in method replace

 Revision 1.18  2006/09/06 10:38:31  poth
 bug fix

 Revision 1.17  2006/09/06 08:20:42  poth
 bug fix in method replace

 Revision 1.16  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.15  2006/07/12 14:46:17  poth
 comment footer added

 ********************************************************************** */
