/*$************************************************************************************************
 **
 ** $Id: CannotCreateGridCoverageException.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/grid/Attic/CannotCreateGridCoverageException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.grid;

// J2SE direct depencies
import java.io.IOException;


/**
 * Thrown when a {@linkplain GridCoverage grid coverage} can't be created.
 *
 * @UML exception GC_CannotCreateGridCoverage
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 * @see GridCoverageReader#read
 *
 * @revisit In a J2SE 1.4 profile, this exception should extends
 *          {@link javax.imageio.IIOException}.
 */
public class CannotCreateGridCoverageException extends IOException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3768704221879769389L;

    /**
     * Creates an exception with no message.
     */
    public CannotCreateGridCoverageException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public CannotCreateGridCoverageException(String message) {
        super(message);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CannotCreateGridCoverageException.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
