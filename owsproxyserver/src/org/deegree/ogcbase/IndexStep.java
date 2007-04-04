//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/IndexStep.java,v 1.2 2006/04/06 20:25:22 poth Exp $
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

import org.deegree.datatypes.QualifiedName;

/**
 * PropertyPathStep implementation that selects a specified occurence of an element (using the
 * property name as element name).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/04/06 20:25:22 $
 * 
 * @since 2.0
 * 
 * @see PropertyPathStep
 */
public class IndexStep extends PropertyPathStep {

    private int selectedIndex;

    /**
     * Creates a new instance of <code>PropertyPathStep</code> that selects the specified
     * occurence of the element with the given name.
     * 
     * @param elementName
     * @param selectedIndex
     */
    IndexStep( QualifiedName elementName, int selectedIndex ) {
        super (elementName);
        this.selectedIndex = selectedIndex;
    }    
    
    /**
     * Returns the index of the selected element.
     * 
     * @return the index of the selected element
     */
    public int getSelectedIndex() {
        return this.selectedIndex;
    }    

    /**
     * Returns a hash code value for the object.
     * 
     * @return a hash code value for the object
     */
    public int hashCode() {
        return this.selectedIndex
            + this.propertyName.hashCode();
    }    

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @return true if this object is the same as the obj argument; false otherwise
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof IndexStep ) ) {
            return false;
        }
        IndexStep that = (IndexStep) obj;
        return this.getSelectedIndex() == that.getSelectedIndex()
            && this.getPropertyName().equals( that.getPropertyName() );
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return string representation of the object
     */
    public String toString() {
        return this.propertyName.getAsString() + "[" + this.selectedIndex + "]";
    }        
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IndexStep.java,v $
Revision 1.2  2006/04/06 20:25:22  poth
*** empty log message ***

Revision 1.1  2006/04/04 10:34:10  mschneider
Added handling of attributes to PropertyPaths.

********************************************************************** */
