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

import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.Feature;
import org.w3c.dom.Element;

/**
 * Encapsulates the information of a <Literal>element as defined in the
 * FeatureId DTD.
 * 
 * @author Markus Schneider
 * @version 07.08.2002
 */
public class Literal extends Expression {

    /** The literal's value. */
    private String value;

    /** Constructs a new Literal. */
    public Literal(String value) {
        id = ExpressionDefines.LITERAL;
        this.value = value;
    }

    /**
     * Given a DOM-fragment, a corresponding Expression-object is built. This
     * method recursively calls other buildFromDOM () - methods to validate the
     * structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Expression buildFromDOM(Element element)
            throws FilterConstructionException {

        // check if root element's name equals 'Literal'
        if (!element.getLocalName().equals("Literal"))
                throw new FilterConstructionException(
                        "Name of element does not equal 'Literal'!");

        return new Literal(XMLTools.getStringValue(element));
    }

    /**
     * Returns the literal's value (as String).
     */
    public String getValue() {
        return value;
    }

    /**
     * @see org.deegree.model.filterencoding.Literal#getValue()
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** Produces an indented XML representation of this object. */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("<ogc:Literal>").append(value).append("</ogc:Literal>");
        return sb;
    }

    /**
     * Returns the <tt>Literal</tt>'s value (to be used in the evaluation of
     * a complexer <tt>Expression</tt>). If the value appears to be
     * numerical, a <tt>Double</tt> is returned, else a <tt>String</tt>.
     * TODO: Improve datatype handling.
     * 
     * @param feature
     *            that determines the values of <tt>PropertyNames</tt> in the
     *            expression (no use here)
     * @return the resulting value
     */
    public Object evaluate(Feature feature) {
        try {
            return new Double(value);
        } catch (NumberFormatException e) {
        }
        return value;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Literal.java,v $
Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
