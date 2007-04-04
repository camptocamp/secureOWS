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
package org.deegree.enterprise;

// JDK 1.3
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The <code>ServiceException</code> class is used across all core framework
 * services and is also suitable for use by developers extending the framework
 * using the framework SPI.
 * 
 * Based on code published by Terren Suydam in JavaWorld
 * 
 * @url http://www.javaworld.com/javaworld/javatips/jw-javatip91.html
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/07/12 14:46:18 $
 * 
 * @see <a href="http://www.javaworld.com/javaworld/javatips/jw-javatip91.html">JavaWorld tip 91</a>
 */
public class ServiceException extends Exception implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // the nested exception
    private Throwable nestedException;

    // String representation of stack trace - not transient!
    private String stackTraceString;

    /**
     * Convert a stack trace to a String so it can be serialized
     */
    public static String generateStackTraceString( Throwable t ) {
        StringWriter s = new StringWriter();

        t.printStackTrace( new PrintWriter( s ) );

        return s.toString();
    }

    /**
     * java.lang.Exception constructors
     */
    public ServiceException() {
    }

    /**
     * Constructor declaration
     * 
     * @param msg
     *  
     */
    public ServiceException(String msg) {
        super( msg );
    }

    /**
     * additional c'tors - nest the exceptions, storing the stack trace
     */
    public ServiceException(Throwable nestedException) {
        this.nestedException = nestedException;
        stackTraceString = generateStackTraceString( nestedException );
    }

    /**
     * Constructor declaration
     * 
     * 
     * @param msg
     * @param nestedException
     * 
     *  
     */
    public ServiceException(String msg, Throwable nestedException) {
        this( msg );

        this.nestedException = nestedException;
        stackTraceString = generateStackTraceString( nestedException );
    }

    // methods

    /**
     * Method declaration
     * 
     * 
     * @return
     * 
     * @uml.property name="nestedException"
     */
    public Throwable getNestedException() {
        return nestedException;
    }

    /**
     * descend through linked-list of nesting exceptions, & output trace note
     * that this displays the 'deepest' trace first
     * 
     * @uml.property name="stackTraceString"
     */
    public String getStackTraceString() {

        // if there's no nested exception, there's no stackTrace
        if (nestedException == null) {
            return null;
        }

        StringBuffer traceBuffer = new StringBuffer();

        if (nestedException instanceof ServiceException) {
            traceBuffer.append(((ServiceException) nestedException)
                .getStackTraceString());
            traceBuffer.append(" nested by:\n");
        }

        traceBuffer.append(stackTraceString);

        return traceBuffer.toString();
    }

    // overrides Exception.getMessage()

    /**
     * Method declaration
     * 
     * 
     * @return 
     *  
     */
    public String getMessage() {

        // superMsg will contain whatever String was passed into the
        // constructor, and null otherwise.
        String superMsg = super.getMessage();

        // if there's no nested exception, do like we would always do
        if ( getNestedException() == null ) {
            return superMsg;
        }

        StringBuffer theMsg = new StringBuffer();

        // get the nested exception's message
        String nestedMsg = getNestedException().getMessage();

        if ( superMsg != null ) {
            theMsg.append( superMsg ).append( ": " ).append( nestedMsg );
        } else {
            theMsg.append( nestedMsg );
        }

        return theMsg.toString();
    }

    // overrides Exception.toString()

    /**
     * Method declaration
     * 
     * 
     * @return 
     *  
     */
    public String toString() {
        StringBuffer theMsg = new StringBuffer( super.toString() );

        if ( getNestedException() != null ) {
            theMsg.append( "; \n\t---> nested " ).append( getNestedException() );
        }

        return theMsg.toString();
    }

    /**
     * Method declaration
     * 
     * 
     *  
     */
    public void printStackTrace() {
        if ( this.getNestedException() != null ) {
            this.getNestedException().printStackTrace();
        } else {
            super.printStackTrace();
        }
    }

    /**
     * Method declaration
     * 
     * 
     * @param inPrintStream
     * 
     *  
     */
    public void printStackTrace( PrintStream inPrintStream ) {
        this.printStackTrace( new PrintWriter( inPrintStream ) );
    }

    /**
     * Method declaration
     * 
     * 
     * @param inPrintWriter
     * 
     *  
     */
    public void printStackTrace( PrintWriter inPrintWriter ) {
        if ( this.getNestedException() != null ) {
            this.getNestedException().printStackTrace( inPrintWriter );
        } else {
            super.printStackTrace( inPrintWriter );
        }
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServiceException.java,v $
Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
