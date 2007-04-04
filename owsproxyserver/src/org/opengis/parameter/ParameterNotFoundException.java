/*$************************************************************************************************
 **
 ** $Id: ParameterNotFoundException.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/parameter/Attic/ParameterNotFoundException.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.parameter;


/**
 * Thrown when a required parameter was not found in a
 * {@linkplain OperationParameterGroup parameter group}.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 */
public class ParameterNotFoundException extends IllegalArgumentException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8074834945993975175L;

    /**
     * The invalid parameter name.
     */
    private final String parameterName;

    /**
     * Creates an exception with the specified message and parameter name.
     *
     * @param message The detail message. The detail message is saved for 
     *        later retrieval by the {@link #getMessage()} method.
     * @param parameterName The required parameter name.
     */
    public ParameterNotFoundException(String message, String parameterName) {
        super(message);
        this.parameterName = parameterName;
    }

    /**
     * Returns the required parameter name.
     */
    public String getParameterName() {
        return parameterName;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ParameterNotFoundException.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
