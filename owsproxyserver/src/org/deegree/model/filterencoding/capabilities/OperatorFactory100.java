//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/filterencoding/capabilities/OperatorFactory100.java,v 1.6 2006/07/12 14:46:16 poth Exp $
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
package org.deegree.model.filterencoding.capabilities;

import org.deegree.ogcwebservices.getcapabilities.UnknownOperatorNameException;

/**
 * @author mschneider
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class OperatorFactory100 {

    // arithmetic operators as defined in filterCapabilities.xsd

    public final static String OPERATOR_LOGICAL_OPERATORS = "Logical_Operators";
    
    public final static String OPERATOR_SIMPLE_ARITHMETIC = "Simple_Arithmetic";

    public final static String OPERATOR_FUNCTIONS = "Functions";

    // comparison operators as defined in filterCapabilities.xsd

    public final static String OPERATOR_SIMPLE_COMPARISONS = "Simple_Comparisons";

    public final static String OPERATOR_LIKE = "Like";

    public final static String OPERATOR_BETWEEN = "Between";

    public final static String OPERATOR_NULL_CHECK = "NullCheck";

    // spatial operators as defined in filterCapabilities.xsd

    public final static String OPERATOR_BBOX = "BBOX";

    public final static String OPERATOR_EQUALS = "Equals";

    public final static String OPERATOR_DISJOINT = "Disjoint";

    public final static String OPERATOR_INTERSECT = "Intersect";

    public final static String OPERATOR_TOUCHES = "Touches";

    public final static String OPERATOR_CROSSES = "Crosses";

    public final static String OPERATOR_WITHIN = "Within";

    public final static String OPERATOR_CONTAINS = "Contains";

    public final static String OPERATOR_OVERLAPS = "Overlaps";

    public final static String OPERATOR_BEYOND = "Beyond";

    public final static String OPERATOR_DWITHIN = "DWithin";

    public static SpatialOperator createSpatialOperator(String name)
            throws UnknownOperatorNameException {
        if (name.equals(OPERATOR_BBOX) || name.equals(OPERATOR_EQUALS)
                || name.equals(OPERATOR_DISJOINT)
                || name.equals(OPERATOR_INTERSECT)
                || name.equals(OPERATOR_TOUCHES)
                || name.equals(OPERATOR_CROSSES)
                || name.equals(OPERATOR_WITHIN)
                || name.equals(OPERATOR_CONTAINS)
                || name.equals(OPERATOR_OVERLAPS)
                || name.equals(OPERATOR_BEYOND)
                || name.equals(OPERATOR_DWITHIN)) {
            return new SpatialOperator(name);
        }
        throw new UnknownOperatorNameException("'" + name
                + "' is no known spatial operator.");
    }

    public static Operator createComparisonOperator(String name)
            throws UnknownOperatorNameException {
        if (name.equals(OPERATOR_SIMPLE_COMPARISONS)
                || name.equals(OPERATOR_LIKE) || name.equals(OPERATOR_BETWEEN)
                || name.equals(OPERATOR_NULL_CHECK)) {
            return new Operator(name);
        }
        throw new UnknownOperatorNameException("'" + name
                + "' is no known comparison operator.");
    }

    public static Operator createArithmeticOperator(String name)
            throws UnknownOperatorNameException {
        if (name.equals(OPERATOR_SIMPLE_ARITHMETIC)
                || name.equals(OPERATOR_FUNCTIONS)) {
            return new Operator(name);
        }
        throw new UnknownOperatorNameException("'" + name
                + "' is no known arithmetic operator.");
    }

    public static Function createArithmeticFunction(String name,
            int argumentCount) {
        return new Function(name, argumentCount);
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OperatorFactory100.java,v $
Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
