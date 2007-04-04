/*$************************************************************************************************
 **
 ** $Id: InvalidRangeException.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/grid/Attic/InvalidRangeException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.grid;


/**
 * Thrown when a {@linkplain GridRange grid range} is out of
 * {@linkplain GridCoverage grid coverage} bounds.
 *
 * @UML exception GC_InvalidRange
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 */
public class InvalidRangeException extends IllegalArgumentException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3165512862939920847L;

    /**
     * Creates an exception with no message.
     */
    public InvalidRangeException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public InvalidRangeException(String message) {
        super(message);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InvalidRangeException.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
