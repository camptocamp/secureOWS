/*
----------------    FILE HEADER  ------------------------------------------
 
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
Aennchenstr. 19
53115 Bonn
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

package org.deegree.model.metadata.iso19115;


/**
 * PresentationFormCode.java
 *
 * Created on 16. September 2002, 10:31
 */
public class PresentationFormCode {
    
    String value = null;

    /** Creates a new instance of PresentationFormCode */
    public PresentationFormCode(String value) {
        setValue(value);
    }

    /**
     * gets the value-attribute.
     * Available values for the value are:<ul>
     * <li>document
     * <li>hardcopyMap
     * <li>image
     * <li>model
     * <li>profile
     * <li>rasterMap
     * <li>table
     * <li>vectorMap
     * <li>view</ul>
     * 
     * @uml.property name="value"
     */
    public String getValue() {
        return value;
    }

    /**
     * @see #getValue()
     * 
     * @uml.property name="value"
     */
    public void setValue(String value) {
        this.value = value;
    }

	

	/**
     * to String method
     */
	public String toString() {
		String ret = null;
		ret = "value = " + value + "\n";
		return ret;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PresentationFormCode.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
