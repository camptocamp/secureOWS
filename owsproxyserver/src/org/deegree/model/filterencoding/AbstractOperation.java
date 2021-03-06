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
package org.deegree.model.filterencoding;

import org.w3c.dom.Element;

/**
 * Abstract superclass representing spatial_ops, comparison_ops and logical_ops
 * entities (as defined in the Filter DTD). 
 * The three different types are reflected in SpatialOperation,
 * ComparisonOperation and LogicalOperation classes.
 * @author Markus Schneider
 * @version 10.08.2002
 */
public abstract class AbstractOperation implements Operation {
    
    /**
     * The underlying operator's id.
     * @see OperationDefines
     */
    protected int operatorId;
    
    AbstractOperation(int operatorId)
    {
    	this.operatorId = operatorId;
    }

    /**
     * Given a DOM-fragment, a corresponding Operation-object is built. This method
     * recursively calls other buildFromDOM () - methods to validate the structure
     * of the DOM-fragment.
     * @throws FilterConstructionException if the structure of the DOM-fragment is invalid
     */    
    public static Operation buildFromDOM (Element element)
        throws FilterConstructionException {

        // check if root element's name is a known operator
        String name = element.getLocalName();
        int type = OperationDefines.getTypeByName (name);
        Operation operation = null;

        switch (type) {
            case OperationDefines.TYPE_SPATIAL: {
                operation = SpatialOperation.buildFromDOM (element);
                break;
            }
            case OperationDefines.TYPE_COMPARISON: {
                operation = ComparisonOperation.buildFromDOM (element);
                break;
            }
            case OperationDefines.TYPE_LOGICAL: {
                operation = LogicalOperation.buildFromDOM (element);
                break;
            }
            default: {
                throw new FilterConstructionException ("Unknown operator '" + name + "'!");
            }
        }
        return operation;
    }

    /** Returns the name of the operator. */
    public String getOperatorName () {
        return OperationDefines.getNameById (operatorId);
    }

    /**
     * Returns the operator's id.
     * @see OperationDefines
     * 
     * @uml.property name="operatorId"
     */
    public int getOperatorId() {
        return operatorId;
    }

    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractOperation.java,v $
Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
