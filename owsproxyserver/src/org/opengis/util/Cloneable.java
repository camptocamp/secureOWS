/*$************************************************************************************************
 **
 ** $Id: Cloneable.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/util/Attic/Cloneable.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.util;


/**
 * Indicates that it is legal to make a field-for-field copy of instances of implementing classes.
 * A cloneable class implements the J2SE's {@link java.lang.Cloneable} standard interface and
 * additionnaly overrides the Object.clone() method with public access.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 *
 * @see java.lang.Cloneable
 */
public interface Cloneable extends java.lang.Cloneable {
    /**
     * Creates and returns a copy of this object.
     * The precise meaning of "copy" may depend on the class of the object.
     *
     * @return A clone of this instance.
     * @see Object#clone
     */
    public Object clone();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Cloneable.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
