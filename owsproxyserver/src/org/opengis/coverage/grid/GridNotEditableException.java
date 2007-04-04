/*$************************************************************************************************
 **
 ** $Id: GridNotEditableException.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/grid/Attic/GridNotEditableException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.grid;


/**
 * Thrown when an attempt is made to write in a non-editable grid.
 *
 * @UML exception GC_GridNotEditable
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 * @see GridCoverage#isDataEditable
 */
public class GridNotEditableException extends IllegalStateException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 612186655921122650L;

    /**
     * Creates an exception with no message.
     */
    public GridNotEditableException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for 
     *         later retrieval by the {@link #getMessage()} method.
     */
    public GridNotEditableException(String message) {
        super(message);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GridNotEditableException.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
