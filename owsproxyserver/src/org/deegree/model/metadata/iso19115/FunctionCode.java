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

import java.io.Serializable;


/**
 * FunctionCode.java
 *
 * Created on 16. September 2002, 10:02
 * <p>----------------------------------------------------------------------</p>
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer</a>
 * @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:18 $
 */

public class FunctionCode implements Serializable {
    
    String value = null;
    
    /** Creates a new instance of FunctionCode_Impl */
    public FunctionCode(String value) {
        setValue(value);
    }

    /**
     * returns the attribute "value". use="required"
     * Possible values are:<ul>
     * <li>access
     * <li>additionalInformation
     * <li>download
     * <li>order
     * <li>search
     * @return the value-attribute
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
        ret = "value = " + value;
	return ret;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FunctionCode.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
