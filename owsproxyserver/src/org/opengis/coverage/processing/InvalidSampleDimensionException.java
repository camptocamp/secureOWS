/*$************************************************************************************************
 **
 ** $Id: InvalidSampleDimensionException.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/processing/Attic/InvalidSampleDimensionException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.processing;


/**
 * Thrown when the index of a {@linkplain org.opengis.coverage.SampleDimension sample dimension}
 * is out of bounds.
 *
 * @UML exception GP_InvalidSampleDimension
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 * @see GridAnalysis
 */
public class InvalidSampleDimensionException extends IllegalArgumentException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -7526979322493497443L;

    /**
     * Creates an exception with no message.
     */
    public InvalidSampleDimensionException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public InvalidSampleDimensionException(String message) {
        super(message);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InvalidSampleDimensionException.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
