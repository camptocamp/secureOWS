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

import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.Feature;
import org.w3c.dom.Element;

/**
 * Encapsulates the information of a <PropertyIsNull>-element (as defined in * Filter DTD). The DTD defines the properties type to be tested as PropertyName * or Literal. *  * @author Markus Schneider * @version 07.08.2002
 */

public class PropertyIsNullOperation extends ComparisonOperation {

    private PropertyName propertyName;


    public PropertyIsNullOperation(PropertyName propertyName) {
        super(OperationDefines.PROPERTYISNULL);
        this.propertyName = propertyName;
    }

    public PropertyName getPropertyName() {
        return this.propertyName;
    }

    /**
     * Given a DOM-fragment, a corresponding Operation-object is built. This
     * method recursively calls other buildFromDOM () - methods to validate the
     * structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Operation buildFromDOM(Element element)
            throws FilterConstructionException {

        // check if root element's name equals 'PropertyIsNull'
        if (!element.getLocalName().equals("PropertyIsNull"))
                throw new FilterConstructionException(
                        "Name of element does not equal 'PropertyIsNull'!");

        ElementList children = XMLTools.getChildElements(element);
        if (children.getLength() != 1)
                throw new FilterConstructionException(
                        "'PropertyIsNull' requires exactly 1 element!");

        Element child = children.item(0);
        PropertyName propertyName = null;

        switch (ExpressionDefines.getIdByName(child.getLocalName())) {
        case ExpressionDefines.PROPERTYNAME: {
            propertyName = (PropertyName) PropertyName.buildFromDOM( child );
            break;
        }
        default: {
            throw new FilterConstructionException(
                "Name of element does not equal 'PropertyIsNull'!" );
        }
        }

        return new PropertyIsNullOperation(propertyName);
    }

    /** Produces an indented XML representation of this object. */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer(500);
        sb.append("<ogc:").append(getOperatorName()).append(">");
        sb.append(propertyName.toXML());
        sb.append("</ogc:").append(getOperatorName()).append(">");
        return sb;
    }

    /**
     * Calculates the <tt>PropertyIsNull</tt> -Operation's logical value based
     * on the certain property values of the given <tt>Feature</tt>.
     * 
     * @param feature
     *            that determines the property values
     * @return true, if the <tt>PropertyIsNull</tt> -Operation evaluates to
     *         true, else false
     * @throws FilterEvaluationException
     *             if the evaluation fails
     */
    public boolean evaluate(Feature feature) throws FilterEvaluationException {
        Object value = propertyName.evaluate(feature);
        if (value == null) return true;
        return false;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PropertyIsNullOperation.java,v $
Revision 1.8  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
