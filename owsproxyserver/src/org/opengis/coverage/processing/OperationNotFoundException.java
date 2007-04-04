/*$************************************************************************************************
 **
 ** $Id: OperationNotFoundException.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/processing/Attic/OperationNotFoundException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.processing;


/**
 * Throws if an operation name given to
 * <code>{@linkplain GridCoverageProcessor#doOperation doOperation}(&hellip;)</code>
 * is not a know operation.
 *
 * @UML exception GP_OperationNotFound
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 */
public class OperationNotFoundException extends IllegalArgumentException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8654574655958181935L;

    /**
     * Creates an exception with no message.
     */
    public OperationNotFoundException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public OperationNotFoundException(String message) {
        super(message);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OperationNotFoundException.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
