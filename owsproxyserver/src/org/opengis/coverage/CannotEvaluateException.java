/*$************************************************************************************************
 **
 ** $Id: CannotEvaluateException.java,v 1.3 2006/11/26 18:17:49 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/Attic/CannotEvaluateException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage;

// OpenGIS direct dependencies

/**
 * The base class for exceptions thrown when a quantity can't be evaluated.
 * This exception is usually invoked by a
 * <code>Coverage.evaluate(PT_CoordinatePoint)</code>
 * method, for example when a point is outside the coverage.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 1.0
 *
 * @see org.opengis.coverage.Coverage
 */
public class CannotEvaluateException extends RuntimeException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 506793649975583062L;

    /**
     * Creates an exception with no message.
     */
    public CannotEvaluateException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public CannotEvaluateException( String message ) {
        super( message );
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: CannotEvaluateException.java,v $
 Revision 1.3  2006/11/26 18:17:49  poth
 unnecessary cast removed / code formatting

 Revision 1.2  2006/07/13 06:28:31  poth
 comment footer added

 ********************************************************************** */
