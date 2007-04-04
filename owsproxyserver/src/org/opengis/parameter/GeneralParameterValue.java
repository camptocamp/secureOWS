/*$************************************************************************************************
 **
 ** $Id: GeneralParameterValue.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/parameter/Attic/GeneralParameterValue.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.parameter;

// OpenGIS direct dependencies
import org.opengis.util.Cloneable;


/**
 * Abstract parameter value or group of parameter values.
 *  
 * @UML abstract CC_GeneralParameterValue
 * @author ISO 19111
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/03-073r1.zip">Abstract specification 2.0</A>
 *
 * @see GeneralOperationParameter
 */
public interface GeneralParameterValue extends Cloneable {
    /**
     * Returns the abstract definition of this parameter or group of parameters.
     *
     * @return The abstract definition of this parameter or group of parameters.
     */
    GeneralOperationParameter getDescriptor();

    /**
     * Returns a copy of this parameter value or group.
     *
     * @return A copy of this parameter value or group.
     */
    Object clone();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeneralParameterValue.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
