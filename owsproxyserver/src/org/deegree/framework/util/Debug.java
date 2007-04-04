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

import java.io.PrintStream;
import java.util.Stack;

/**
 * 
 * 
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @since 2.0
 */
public class Debug {

    /**
     * Comment for <code>NODEBUG</code>
     */
    public static final int NODEBUG = 0;

    /**
     * Comment for <code>COMMENTS</code>
     */
    public static final int COMMENTS = 1;

    /**
     * Comment for <code>METHOD</code>
     */
    public static final int METHOD = 2;

    /**
     * Comment for <code>METHOD_AND_COMMENTS</code>
     */
    public static final int METHOD_AND_COMMENTS = 3;

    /**
     * Comment for <code>ERRORSSHORT</code>
     */
    public static final int ERRORSSHORT = 4;

    /**
     * Comment for <code>ERRORSMEDIUM</code>
     */
    public static final int ERRORSMEDIUM = 5;

    /**
     * Comment for <code>ERRORSLONG</code>
     */
    public static final int ERRORSLONG = 6;

    /**
     * Comment for <code>ERRORSONLY</code>
     */
    public static final int ERRORSONLY = 7;

    /**
     * Comment for <code>ERRORS_AND_COMMENTS</code>
     */
    public static final int ERRORS_AND_COMMENTS = 8;

    /**
     * Comment for <code>ALL</code>
     */
    public static final int ALL = 9;

    /**
     * Comment for <code>level</code>
     */
    private static int level = 7;

    /**
     * Comment for <code>method</code>
     */
    private static Stack method = new Stack();

    /**
     * Comment for <code>timer</code>
     */
    private static Stack timer = new Stack();

    /**
     * Comment for <code>out</code>
     */
    public static PrintStream out = System.out;

    /**
     * Sets the debug level according to the given String constant.
     * 
     * @param levelStr
     *            must be a String that equals a known debuglevel.
     */
    public static void setLevel( String levelStr ) {
        level = 8;

        if ( levelStr == null )
            return;
        if ( levelStr.equals( "NODEBUG" ) ) {
            level = 0;
            return;
        }
        if ( levelStr.equals( "COMMENTS" ) ) {
            level = 1;
            return;
        }
        if ( levelStr.equals( "METHOD" ) ) {
            level = 2;
            return;
        }
        if ( levelStr.equals( "METHOD_AND_COMMENTS" ) ) {
            level = 3;
            return;
        }
        if ( levelStr.equals( "ERRORSSHORT" ) ) {
            level = 4;
            return;
        }
        if ( levelStr.equals( "ERRORSMEDIUM" ) ) {
            level = 5;
            return;
        }
        if ( levelStr.equals( "ERRORSLONG" ) ) {
            level = 6;
            return;
        }
        if ( levelStr.equals( "ERRORSONLY" ) ) {
            level = 7;
            return;
        }
        if ( levelStr.equals( "ERRORS_AND_COMMENTS" ) ) {
            level = 8;
            return;
        }
        if ( levelStr.equals( "ALL" ) ) {
            level = 9;
        }
    }

    /**
     * 
     * @uml.property name="level"
     */
    public static void setLevel( int level ) {
        if ( level < 0
            || level > 9 )
            return;
        Debug.level = level;
    }

    private static void blank() {
        for (int i = 0; i < method.size(); i++) {
            out.print( "   " );
        }
    }

    /**
     * @param className
     * @param name
     */
    public static void debugMethodBegin( String className, String name ) {
        if ( level < 2
            || level == 7 || level == 8 )
            return;
        out.println();
        blank();
        out.println( "---------------- begin -----------------------" );
        blank();
        out.println( "Method: "
            + name );
        blank();
        out.println( "Class: "
            + className );
        method.push( name );
        timer.push( new Long( System.currentTimeMillis() ) );
    }

    /**
     * 
     */
    public static void debugMethodBegin() {
        if ( level < 2
            || level == 7 || level == 8 )
            return;
        StackTraceElement element = new Exception().getStackTrace()[1];
        out.println();
        blank();
        out.println( "---------------- begin -----------------------" );
        blank();
        out.println( "Method: '"
            + element.getMethodName() );
        blank();
        out.println( "Class: '"
            + element.getClassName() );
        method.push( element.getMethodName() );
        timer.push( new Long( System.currentTimeMillis() ) );
    }

    /**
     * @param cl
     * @param name
     */
    public static void debugMethodBegin( Object cl, String name ) {
        if ( cl instanceof String ) {
            debugMethodBegin( (String) cl, name );
        } else {
            debugMethodBegin( cl.getClass().getName(), name );
        }
    }

    /**
     * 
     */
    public static void debugMethodEnd() {
        if ( level < 2
            || level == 7 || level == 8 )
            return;
        String name = null;
        long time = 0;
        try {
            name = (String) method.pop();
            time = ( (Long) timer.pop() ).longValue();
            time = System.currentTimeMillis()
                - time;
        } catch (Exception ex) {
        }
        blank();
        out.println( "Method: "
            + name + "  -  time: " + time );
        blank();
        out.println( "----------------- end ------------------------" );
    }

    /**
     * @param e
     * @param additional
     */
    public static void debugException( Exception e, String additional ) {
        if ( level == 0 )
            return;
        switch (level) {
        case 0:
            break;
        case 1:
            break;
        case 2:
            break;
        case 3:
            break;
        case 4:
            blank();
            out.println( "l4 Error: "
                + e.getMessage() );
            break;
        case 5:
            blank();
            out.println( "l5 Message: "
                + e.toString() );
            break;
        case 6: {
            blank();
            out.println( "l6 Message: "
                + e.getMessage() );
            blank();
            e.printStackTrace( out );
            blank();
            out.println( additional );
            break;
        }
        case 7: {
            blank();
            out.println( "l7 Message: "
                + e.getMessage() );
            blank();
            e.printStackTrace( out );
            break;
        }
        case 8: {
            blank();
            out.println( "l7 Message: "
                + e.getMessage() );
            blank();
            e.printStackTrace( out );
            break;
        }
        case 9: {
            blank();
            out.println( "l8 Message: "
                + e.getMessage() );
            blank();
            e.printStackTrace( out );
            blank();
            out.println( additional );
            break;
        }
        }
    }

    /**
     * @param message
     */
    public static void debugSimpleMessage( String message ) {
        if ( level != 1
            && level != 3 && level != 8 && level != 9 )
            return;
        blank();
        out.println( "Debug message: "
            + message );
    }

    /**
     * @param name
     * @param value
     */
    public static void debugObject( String name, Object value ) {
        if ( level != 1
            && level != 3 && level != 8 && level != 9 )
            return;
        blank();
        out.println( "object: "
            + name + " = " + value );
    }

    /**
     * @param name
     * @param value
     */
    public static void debugInt( String name, int value ) {
        if ( level != 1
            && level != 3 && level != 8 && level != 9 )
            return;
        blank();
        out.println( "int: "
            + name + " = " + value );
    }

    /**
     * @param name
     * @param value
     */
    public static void debugDouble( String name, double value ) {
        if ( level != 1
            && level != 3 && level != 8 && level != 9 )
            return;
        blank();
        out.println( "double: "
            + name + " = " + value );
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Debug.java,v $
Revision 1.7  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
