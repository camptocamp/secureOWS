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
 * Encapsulates the information of a <PropertyIsCOMP>-element (as defined in * Filter DTD). COMP can be one of the following: * <ul> * <li>EqualTo</li> * <li>LessThan</li> * <li>GreaterThan</li> * <li>LessThanOrEqualTo</li> * <li>GreaterThanOrEqualTo</li> * </ul> * * @author Markus Schneider * @version 07.08.2002
 */

public class PropertyIsCOMPOperation extends ComparisonOperation {

    private Expression expr1;
    private Expression expr2;
    /**
     *
     * matchCase flag
     */
    private boolean matchCase = true;

    /**
     * Creates a new PropertyIsCOMPOperation object.
     *
     * @param id
     * @param expr1
     * @param expr2
     * @param matchCase
     */
    public PropertyIsCOMPOperation( int id, Expression expr1, Expression expr2 ) {
        this( id, expr1, expr2, true );
    }
    
    public PropertyIsCOMPOperation( int id, Expression expr1, Expression expr2, boolean matchCase ) {
        super(id);
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.matchCase = matchCase;
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
        // check if root element's name is a known operator
        String name = element.getLocalName();
        int operatorId = OperationDefines.getIdByName(name);
        boolean matchCase = true;
        String tmp = element.getAttribute( "matchCase" );
        if ( tmp != null && tmp.length() > 0 ) {
            try {
                matchCase = Boolean.parseBoolean(tmp);
            } catch (Exception e) {};
        }

        switch (operatorId) {
        case OperationDefines.PROPERTYISEQUALTO:
        case OperationDefines.PROPERTYISLESSTHAN:
        case OperationDefines.PROPERTYISGREATERTHAN:
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
            break;
        default:
            throw new FilterConstructionException("'" + name + "' is not a PropertyIsOperator!");
        }

        ElementList children = XMLTools.getChildElements(element);

        if (children.getLength() != 2) {
            throw new FilterConstructionException( "'" + name + "' requires exactly 2 elements!");
        }

        Expression expr1 = Expression.buildFromDOM(children.item(0));
        Expression expr2 = Expression.buildFromDOM(children.item(1));

        return new PropertyIsCOMPOperation(operatorId, expr1, expr2, matchCase);
    }

    /**
     * returns the first <tt>Expression</tt> of the comparison
     */
    public Expression getFirstExpression() {
        return expr1;
    }

    /**
     * returns the second <tt>Expression</tt> of the comparison
     */
    public Expression getSecondExpression() {
        return expr2;
    }

    /**
     * returns matchCase flag
     */
    public boolean isMatchCase() {
        return matchCase;
    }

    /** Produces an indented XML representation of this object. */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer(500);
        sb.append("<ogc:").append(getOperatorName());
        if ( !matchCase )
            sb.append(" matchCase=\"false\"");
        sb.append(">");
        sb.append(expr1.toXML());
        sb.append(expr2.toXML());
        sb.append("</ogc:").append(getOperatorName()).append(">");
        return sb;
    }

    /**
     * Calculates the <tt>ComparisonOperation</tt>'s logical value based on
     * the certain property values of the given <tt>Feature</tt>. TODO:
     * Improve datatype handling.
     *
     * @param feature
     *            that determines the property values
     * @return true, if the <tt>FeatureFilter</tt> evaluates to true, else
     *         false
     * @throws FilterEvaluationException
     *             if the expressions to be compared are of different types
     */
    public boolean evaluate(Feature feature) throws FilterEvaluationException {
        Object value1 = expr1.evaluate(feature);
        Object value2 = expr2.evaluate(feature);

        if (value1 == null || value2 == null) return false;

        //Convert to comparable datatype
        if ((value1 instanceof String && value2 instanceof Number)
                || (value1 instanceof Number && value2 instanceof String)) {
            if (value1 instanceof String) {
                //Prefer numeric comparison
                try {
                    value1 = Double.valueOf((String) value1);
                } catch (NumberFormatException e) {
                    value2 = value2.toString();
                }
            } else {
                try {
                    value2 = Double.valueOf((String) value2);
                } catch (NumberFormatException e) {
                    value1 = value1.toString();
                }
            }
        }

        // compare Strings
        if (value1 instanceof String && value2 instanceof String) {
            switch (getOperatorId()) {
            case OperationDefines.PROPERTYISEQUALTO:
                {
                    if ((value1 == null) || (value2 == null)) { return false; }

                    if ( matchCase ) {
                        return value1.equals(value2);
                    } 
                    return ( (String) value1).equalsIgnoreCase( (String) value2 );
                    
                }
            case OperationDefines.PROPERTYISLESSTHAN:
            case OperationDefines.PROPERTYISGREATERTHAN:
            case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
            case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                throw new FilterEvaluationException("'" + getOperatorName()
                        + "' can not be applied to " + "String values!");
            default:
                throw new FilterEvaluationException(
                        "Unknown comparison operation: '" + getOperatorName()
                                + "'!");
            }
        }// compare Doubles
        else if ((value1 instanceof Number) && (value2 instanceof Number)) {
            double d1 = Double.parseDouble(value1.toString());
            double d2 = Double.parseDouble(value2.toString());

            switch (getOperatorId()) {
            case OperationDefines.PROPERTYISEQUALTO:
                return d1 == d2;
            case OperationDefines.PROPERTYISLESSTHAN:
                return d1 < d2;
            case OperationDefines.PROPERTYISGREATERTHAN:
                return d1 > d2;
            case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
                return d1 <= d2;
            case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                return d1 >= d2;
            default:
                throw new FilterEvaluationException(
                        "Unknown comparison operation: '" + getOperatorName()
                                + "'!");
            }
        } else {
            throw new FilterEvaluationException("Can not apply operation '"
                    + getOperatorName() + "' to " + "different datatypes!");
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PropertyIsCOMPOperation.java,v $
Revision 1.10  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
