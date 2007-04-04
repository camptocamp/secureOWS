//$Header$
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcbase;

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;

/**
 * Factory class for <code>PropertyPath</code> and <code>PropertyPathStep</code> instances.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 *
 * @see PropertyPath
 * @see PropertyPathStep
 */
public class PropertyPathFactory {

    /**
     * Creates a new <code>PropertyPath</code> instance that consists of one element step.
     * 
     * @param elementName
     *            name of selected element
     * @return new <code>PropertyPath</code> instance
     */    
    public static PropertyPath createPropertyPath (QualifiedName elementName) {
        List<PropertyPathStep> steps = new ArrayList<PropertyPathStep> ();
        steps.add (new ElementStep (elementName));
        PropertyPath path = new PropertyPath (steps);
        return path;
    }

    /**
     * Creates a new <code>PropertyPath</code> instance that consists of a subset of the steps
     * from the given <code>PropertyPath</code>.
     * 
     * @param propertyPath
     *            original <code>PropertyPath</code>
     * @param fromIndex
     *            index of the first step to be included
     * @param toIndex
     *            index of the final step to be included
     * @return new <code>PropertyPath</code> instance
     */
    public static PropertyPath createPropertyPath( PropertyPath propertyPath, int fromIndex, int toIndex ) {
        if (toIndex - fromIndex < 1) {
            throw new IllegalArgumentException ("PropertyPath must contain at least one step.");
        }
        List<PropertyPathStep> steps = propertyPath.getAllSteps();
        List<PropertyPathStep> newSteps = steps.subList(fromIndex, toIndex);
        PropertyPath newPath = new PropertyPath (newSteps);
        return newPath;
    }    
    
    /**
     * Creates a new <code>PropertyPath</code> instance with the specified steps.
     * 
     * @param steps
     *            property path steps, may not be null
     * @return new <code>PropertyPath</code> instance
     */
    public static PropertyPath createPropertyPath ( List<PropertyPathStep> steps ) {
        return new PropertyPath (steps);
    }

    /**
     * Creates a new <code>PropertyPathStep</code> instance that selects the attribute with the
     * given name.
     * 
     * @param attrName
     *            attribute to be selected
     * @return new <code>PropertyPathStep</code> instance
     */    
    public static PropertyPathStep createAttributePropertyPathStep (QualifiedName attrName) {
        return new AttributeStep (attrName);
    }    
    
    /**
     * Creates a new <code>PropertyPathStep</code> instance that selects the element with the
     * given name.
     * 
     * @param elementName
     *            element to be selected
     * @return new <code>PropertyPathStep</code> instance
     */     
    public static PropertyPathStep createPropertyPathStep (QualifiedName elementName) {
        return new ElementStep (elementName);
    }

    /**
     * Creates a new <code>PropertyPathStep</code> instance that selects the specified occurence
     * of the element with the given name.
     * 
     * @param elementName
     *            element to be selected
     * @param selectedIndex
     *            occurence of the element            
     * @return new <code>PropertyPathStep</code> instance
     */         
    public static PropertyPathStep createPropertyPathStep (QualifiedName elementName, int selectedIndex) {
        return new IndexStep (elementName, selectedIndex);
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/04/06 20:25:22  poth
*** empty log message ***

Revision 1.2  2006/04/04 10:34:10  mschneider
Added handling of attributes to PropertyPaths.

********************************************************************** */
