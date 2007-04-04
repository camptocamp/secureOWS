/*$************************************************************************************************
 **
 ** $Id: GeneralOperationParameter.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/parameter/Attic/GeneralOperationParameter.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.parameter;

// OpenGIS direct dependencies
import org.opengis.metadata.Info;


/**
 * Abstract definition of a parameter or group of parameters used by an operation method.
 *  
 * @UML abstract CC_GeneralOperationParameter
 * @author ISO 19111
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/03-073r1.zip">Abstract specification 2.0</A>
 *
 * @see GeneralParameterValue
 */
public interface GeneralOperationParameter extends Info {
    /**
     * The minimum number of times that values for this parameter group or
     * parameter are required. The default value is one. A value of 0 means
     * an optional parameter.
     *
     * @return The minimum occurrences.
     * @UML optional minimumOccurs
     *
     */
    int getMinimumOccurs();

    /**
     * The maximum number of times that values for this parameter group or
     * parameter can be included. The default value is one.
     *
     * @return The maximum occurrences.
     * @UML optional OperationParameterGroup.maximumOccurs
     *
     * @see #getMinimumOccurs
     */
    int getMaximumOccurs();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeneralOperationParameter.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
