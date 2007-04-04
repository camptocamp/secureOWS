// $Header:
// /deegreerepository/deegree/src/org/deegree/model/filterencoding/XMLFactory.java,v
// 1.1.1.1 2005/01/05 10:34:46 poth Exp $
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

import java.io.StringReader;
import java.net.URI;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.model.filterencoding.capabilities.Function;
import org.deegree.model.filterencoding.capabilities.IdCapabilities;
import org.deegree.model.filterencoding.capabilities.Operator;
import org.deegree.model.filterencoding.capabilities.OperatorFactory100;
import org.deegree.model.filterencoding.capabilities.ScalarCapabilities;
import org.deegree.model.filterencoding.capabilities.SpatialCapabilities;
import org.deegree.model.filterencoding.capabilities.SpatialOperator;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * 
 *
 * @version $Revision: 1.15 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 1.15 $, $Date: 2006/09/20 12:57:43 $
 *
 * @since 2.0
 */
public class XMLFactory {

    private static URI OGCNS = CommonNamespaces.OGCNS;

    /**
     * Appends the DOM representation of the <code>FilterCapabilities</code>
     * to the passed <code>Element</code>. Generated DOM structure fulfills
     * <code>Filter Encoding Specification 1.0.0</code>.
     * 
     * @param root
     */
    public static void appendFilterCapabilities100(Element root, FilterCapabilities filterCapabilities) {

        Element filterCapabilitiesNode = 
            XMLTools.appendElement(root, OGCNS, "ogc:Filter_Capabilities");
        Element spatialCapabilitiesNode = 
            XMLTools.appendElement( filterCapabilitiesNode, OGCNS, "ogc:Spatial_Capabilities");
        Element spatialOperationsNode = 
            XMLTools.appendElement( spatialCapabilitiesNode, OGCNS, "ogc:Spatial_Operators");
        SpatialCapabilities spatialCapabilities = 
            filterCapabilities.getSpatialCapabilities();
        String[] operators = new String[] { OperatorFactory100.OPERATOR_BBOX,
                OperatorFactory100.OPERATOR_EQUALS,
                OperatorFactory100.OPERATOR_DISJOINT,
                OperatorFactory100.OPERATOR_INTERSECT,
                OperatorFactory100.OPERATOR_TOUCHES,
                OperatorFactory100.OPERATOR_CROSSES,
                OperatorFactory100.OPERATOR_WITHIN,
                OperatorFactory100.OPERATOR_CONTAINS,
                OperatorFactory100.OPERATOR_OVERLAPS,
                OperatorFactory100.OPERATOR_BEYOND,
                OperatorFactory100.OPERATOR_DWITHIN };
        for (int i = 0; i < operators.length; i++) {
            if (spatialCapabilities.hasOperator(operators[i])) {
                XMLTools.appendElement(spatialOperationsNode, OGCNS, "ogc:" + operators[i]);
            }
        }
        Element scalarCapabilitiesNode = 
            XMLTools.appendElement( filterCapabilitiesNode, OGCNS, "ogc:Scalar_Capabilities");
        ScalarCapabilities scalarCapabilities = filterCapabilities.getScalarCapabilities();
        if (scalarCapabilities.hasLogicalOperatorsSupport()) {
            XMLTools.appendElement( scalarCapabilitiesNode, OGCNS, "ogc:" + 
                                    OperatorFactory100.OPERATOR_LOGICAL_OPERATORS);
        }
        
        if (scalarCapabilities.getComparisonOperators().length > 0) {
            
            Element operatorsNode = 
                XMLTools.appendElement( scalarCapabilitiesNode, OGCNS, "ogc:Comparison_Operators");
            operators = new String[] {
                    OperatorFactory100.OPERATOR_SIMPLE_COMPARISONS,
                    OperatorFactory100.OPERATOR_LIKE,
                    OperatorFactory100.OPERATOR_BETWEEN,
                    OperatorFactory100.OPERATOR_NULL_CHECK };
            for (int i = 0; i < operators.length; i++) {
                if (scalarCapabilities.hasComparisonOperator(operators[i])) {
                    XMLTools.appendElement(operatorsNode, OGCNS, "ogc:" + operators[i]);
                }
            }
        }
        // 'ogc:Arithmetic_Operators'-element
        if (scalarCapabilities.getArithmeticOperators().length > 0) {
            Element operatorsNode = 
                XMLTools.appendElement( scalarCapabilitiesNode, OGCNS, "ogc:Arithmetic_Operators");
            // 'ogc:Simple_Arithmetic'-element
            int hasFunctions = 1;
            if (scalarCapabilities
                    .hasArithmeticOperator(OperatorFactory100.OPERATOR_SIMPLE_ARITHMETIC)) {
                XMLTools.appendElement(operatorsNode, OGCNS, "ogc:"
                        + OperatorFactory100.OPERATOR_SIMPLE_ARITHMETIC);
                hasFunctions = 2;
            }
            // 'ogc:Functions'-element
            Operator[] arithmeticOperators = scalarCapabilities.getArithmeticOperators();
            if (arithmeticOperators.length >= hasFunctions) {
                Element functionsNode = XMLTools.appendElement(operatorsNode,
                        OGCNS, "ogc:" + OperatorFactory100.OPERATOR_FUNCTIONS, null);
                for (int i = 0; i < arithmeticOperators.length; i++) {
                    if (arithmeticOperators[i] instanceof Function) {
                        Function function = (Function) arithmeticOperators[i];
                        Element functionNode = 
                            XMLTools.appendElement( functionsNode, OGCNS, "ogc:Function_Name");
                        functionNode.setAttribute("nArgs", "" + function.getArgumentCount());
                    }
                }
            }
        }
    }

    /**
     * Appends the DOM representation of the <code>FilterCapabilities</code>
     * to the passed <code>Element</code>. Generated DOM structure fulfills
     * <code>Filter Encoding Specification 1.1.0</code>.
     * 
     * @param root
     * @param filterCapabilities
     */
    public static void appendFilterCapabilities110(Element root,
            FilterCapabilities filterCapabilities) {
        Element filterCapabilitiesNode = XMLTools.appendElement(root, OGCNS,
                "ogc:Filter_Capabilities");
        appendSpatialCapabilities110(filterCapabilitiesNode, filterCapabilities
                .getSpatialCapabilities());
        appendScalarCapabilities110(filterCapabilitiesNode, filterCapabilities
                .getScalarCapabilities());
        appendIdCapabilities110(filterCapabilitiesNode, filterCapabilities
                .getIdCapabilities());
    }

    /**
     * Appends the DOM representation of the <code>SpatialCapabilities</code>
     * to the passed <code>Element</code>. Generated DOM structure fulfills
     * <code>Filter Encoding Specification 1.1.0</code>.
     * 
     * @param root
     * @param spatialCapabilities
     */
    public static void appendSpatialCapabilities110(Element root,
            SpatialCapabilities spatialCapabilities) {
        Element spatialCapabilitiesNode = XMLTools.appendElement(root, OGCNS,
                "ogc:Spatial_Capabilities");
        QualifiedName[] geometryOperands = spatialCapabilities
                .getGeometryOperands();
        if (geometryOperands != null && geometryOperands.length > 0) {
            appendGeometryOperands(spatialCapabilitiesNode, geometryOperands);
        }
        SpatialOperator[] spatialOperators = spatialCapabilities
                .getSpatialOperators();
        Element spatialOperatorsNode = XMLTools.appendElement(
                spatialCapabilitiesNode, OGCNS, "ogc:SpatialOperators");
        for (int i = 0; i < spatialOperators.length; i++) {
            Element spatialOperatorNode = XMLTools.appendElement(
                    spatialOperatorsNode, OGCNS, "ogc:SpatialOperator");
            spatialOperatorNode.setAttribute("name", spatialOperators[i]
                    .getName());
            geometryOperands = spatialOperators[i].getGeometryOperands();
            if (geometryOperands != null && geometryOperands.length > 0) {
                appendGeometryOperands(spatialOperatorsNode, geometryOperands);
            }
        }
    }

    /**
     * Appends the DOM representation of the <code>SpatialCapabilities</code>
     * to the passed <code>Element</code>. Generated DOM structure fulfills
     * <code>Filter Encoding Specification 1.1.0</code>.
     * 
     * @param root
     * @param spatialCapabilities
     */
    public static void appendGeometryOperands(Element root,
            QualifiedName[] geometryOperands) {
        Element geometryOperandsNode = XMLTools.appendElement(root, OGCNS,
                "ogc:GeometryOperands");
        for (int i = 0; i < geometryOperands.length; i++) {
            XMLTools.appendElement(geometryOperandsNode, OGCNS,
                    "ogc:GeometryOperand", geometryOperands[i].getAsString());
        }
    }

    /**
     * Appends the DOM representation of the <code>ScalarCapabilities</code>
     * to the passed <code>Element</code>. Generated DOM structure fulfills
     * <code>Filter Encoding Specification 1.1.0</code>.
     * 
     * @param root
     * @param scalarCapabilities
     */
    public static void appendScalarCapabilities110(Element root, ScalarCapabilities scalarCapabilities) {
        
        Element scalarCapabilitiesNode = 
            XMLTools.appendElement(root, OGCNS, "ogc:Scalar_Capabilities");
        if (scalarCapabilities.hasLogicalOperatorsSupport()) {
            XMLTools.appendElement(scalarCapabilitiesNode, OGCNS, "ogc:LogicalOperators");
        }
        Operator[] comparisonOperators = scalarCapabilities.getComparisonOperators();
        if (comparisonOperators != null) {
            Element comparisonOperatorsNode = 
                XMLTools.appendElement( scalarCapabilitiesNode, OGCNS, "ogc:ComparisonOperators");
            for (int i = 0; i < comparisonOperators.length; i++) {
                XMLTools.appendElement( comparisonOperatorsNode, OGCNS, 
                                        "ogc:ComparisonOperator", comparisonOperators[i].getName());
            }
        }
        Operator[] arithmeticOperators = scalarCapabilities.getArithmeticOperators();
        if (arithmeticOperators != null) {
            Element arithmeticOperatorsNode = XMLTools.appendElement(
                    scalarCapabilitiesNode, OGCNS, "ogc:ArithmeticOperators");
            XMLTools.appendElement(arithmeticOperatorsNode, OGCNS, "ogc:SimpleArithmetic");

            boolean functionAvailable = false;
            for (int i = 0; i < arithmeticOperators.length; i++) {
                if (arithmeticOperators[i] instanceof Function) {
                    functionAvailable = true;
                }
            }            
            
            if (functionAvailable) {
                Element functionsNode = 
                    XMLTools.appendElement( arithmeticOperatorsNode, OGCNS, "ogc:Functions");
                Element functionNamesNode = 
                    XMLTools.appendElement(functionsNode, OGCNS, "ogc:FunctionNames");
                for (int i = 0; i < arithmeticOperators.length; i++) {
                    if (arithmeticOperators[i] instanceof Function) {
                        Function function = (Function) arithmeticOperators[i];
                        Element functionNameNode = XMLTools.appendElement(
                                functionNamesNode, OGCNS, "ogc:FunctionName",
                                function.getName());
                        functionNameNode.setAttribute("nArgs", "" + function.getArgumentCount());
                    }
                }                
            }
        }
    }

    /**
     * Appends the DOM representation of the <code>IdCapabilities</code> to
     * the passed <code>Element</code>. Generated DOM structure fulfills
     * <code>Filter Encoding Specification 1.1.0</code>.
     * 
     * @param root
     * @param idCapabilities
     */
    public static void appendIdCapabilities110(Element root,
            IdCapabilities idCapabilities) {
        Element idCapabilitiesNode = XMLTools.appendElement(root, OGCNS,
                "ogc:Id_Capabilities");
        Element[] eidElements = idCapabilities.getEidElements();
        for (int i = 0; i < eidElements.length; i++) {
            XMLTools.insertNodeInto(eidElements [i], idCapabilitiesNode);
        }
        Element[] fidElements = idCapabilities.getFidElements();
        for (int i = 0; i < fidElements.length; i++) {
            XMLTools.insertNodeInto(fidElements [i], idCapabilitiesNode);
        }
    }

    /**
     * Appends the DOM representation of the given <code>Filter</code>-
     * section to the passed <code>Element</code>.
     * 
     * TODO: Append the DOM-structure "node by node".
     * 
     * @param root
     * @param filter must not be null
     */
    public static void appendFilter(Element root, Filter filter)
            throws XMLException {
        String filterString = filter.toXML().toString();
        try {
            Document doc = XMLTools.parse(new StringReader(filterString));
            XMLTools.insertNodeInto(doc.getFirstChild(), root);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XMLException("Error appending Filter-expression: "
                    + e.getMessage());
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XMLFactory.java,v $
Revision 1.15  2006/09/20 12:57:43  mschneider
Fixed erroneous output of "FunctionNames" element  in #appendScalarCapabilities110() when no functions are available.

Revision 1.14  2006/08/02 21:16:10  poth
code formating

Revision 1.13  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
