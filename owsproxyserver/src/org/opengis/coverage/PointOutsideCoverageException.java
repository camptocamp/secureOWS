/*$************************************************************************************************
 **
 ** $Id: PointOutsideCoverageException.java,v 1.3 2006/11/26 18:17:49 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/Attic/PointOutsideCoverageException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage;

// OpenGIS direct dependencies

/**
 * Thrown when a
 * <code>org.opengis.coverage.Coverage.evaluate</code>
 * method is invoked with a point outside coverage.
 *
 * @UML exception CV_PointOutsideCoverage
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 */
public class PointOutsideCoverageException extends CannotEvaluateException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8718412090539227101L;

    /**
     * Creates an exception with no message.
     */
    public PointOutsideCoverageException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public PointOutsideCoverageException( String message ) {
        super( message );
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: PointOutsideCoverageException.java,v $
 Revision 1.3  2006/11/26 18:17:49  poth
 unnecessary cast removed / code formatting

 Revision 1.2  2006/07/13 06:28:31  poth
 comment footer added

 ********************************************************************** */
