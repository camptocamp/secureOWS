/*$************************************************************************************************
 **
 ** $Id: ParameterValueGroup.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/parameter/Attic/ParameterValueGroup.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.parameter;


/**
 * A group of related parameter values. The same group can be repeated more than once in an
 * Operation.operation or higher level <code>ParameterValueGroup</code>,
 * if those instances contain different values of one or more {@link ParameterValue}s which suitably
 * distinquish among those groups.
 *  
 * @UML abstract CC_ParameterValueGroup
 * @author ISO 19111
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/03-073r1.zip">Abstract specification 2.0</A>
 *
 * @see OperationParameterGroup
 * @see ParameterValue
 */
public interface ParameterValueGroup extends GeneralParameterValue {
    /**
     * Returns the group this value belong to.
     *
     * @return The abstract definition of this group of parameters.
     * @UML association valuesOfGroup
     *
     * @rename Renamed <CODE>getDescriptor()</CODE> because <CODE>getGroup()</CODE> seems too
     *         restrictive, misleading (this method returns an abstract definition of a group
     *         of parameters, not the actual group), and for consistency with usage in other
     *         Java extensions (e.g.
     *         {@link javax.media.jai.ParameterList#getParameterListDescriptor ParameterList}).
     */
    GeneralOperationParameter getDescriptor();

    /**
     * Returns the values in this group.
     *
     * @return The values.
     * @UML association includesValue
     */
    GeneralParameterValue[] getValues();

    /**
     * Returns the first value in this group for the specified name. If no
     * {@linkplain ParameterValue parameter value} or group is found for the
     * given name, then this method search recursively in subgroups (if any).
     *
     * @param  name The case insensitive name of the parameter to search for.
     * @return The parameter value for the given name.
     * @throws InvalidParameterNameException if there is no parameter for the given name.
     */
    GeneralParameterValue getValue(String name) throws InvalidParameterNameException;

    /**
     * Returns a copy of this group of parameter values.
     * Included parameter values and subgroups are cloned recursively.
     *
     * @return A copy of this group of parameter values.
     */
    Object clone();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ParameterValueGroup.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
