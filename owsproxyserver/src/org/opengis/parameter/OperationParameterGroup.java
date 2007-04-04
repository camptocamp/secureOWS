/*$************************************************************************************************
 **
 ** $Id: OperationParameterGroup.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/parameter/Attic/OperationParameterGroup.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.parameter;


/**
 * The definition of a group of related parameters used by an operation method.
 *  
 * @UML abstract CC_OperationParameterGroup
 * @author ISO 19111
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/03-073r1.zip">Abstract specification 2.0</A>
 *
 * @see ParameterValueGroup
 * @see OperationParameter
 */
public interface OperationParameterGroup extends GeneralOperationParameter {
    /**
     * Returns the parameters in this group.
     *
     * @return The parameters.
     * @UML association includesParameter
     */
    GeneralOperationParameter[] getParameters();

    /**
     * Returns the first parameter in this group for the specified name. If no
     * {@linkplain OperationParameter operation parameter} or group is found for
     * the given name, then this method search recursively in subgroups (if any).
     *
     * @param  name The case insensitive name of the parameter to search for.
     * @return The parameter for the given name.
     * @throws InvalidParameterNameException if there is no parameter for the given name.
     */
    GeneralOperationParameter getParameter(String name) throws InvalidParameterNameException;
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OperationParameterGroup.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
