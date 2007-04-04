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
package org.deegree.graphics.sld;

import org.deegree.framework.xml.Marshallable;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * The simple SVG/CSS2 styling parameters are given with the CssParameter * element, which is defined as follows: *  * <pre> *   *   &lt;xs:element name=&quot;CssParameter&quot; type=&quot;sld:ParameterValueType&quot;/&gt; *      &lt;xs:complexType name=&quot;ParameterValueType&quot; mixed=&quot;true&quot;&gt; *         &lt;xs:choice minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;&gt; *              &lt;xs:element ref=&quot;wfs:expression&quot;/&gt; *          &lt;/xs:choice&gt; *   &lt;/xs:complexType&gt; *   * </pre> *  * The parameter values are allowed to be complex expressions for maximum * flexibility. The mixed="true" definition means that regular text may be mixed * in with various sub-expressions, implying a text-substitution model for * parameter values. Numeric and character-string data types are not * distinguished, which may cause some complications. * <p> * </p> * Here are some usage examples: *  * <pre> *  *  1. &lt;CssParameter name=&quot;stroke-width&quot;&gt;3&lt;/CssParameter&gt; *  2. &lt;CssParameter name=&quot;stroke-width&quot;&gt; *          &lt;wfs:Literal&gt;3&lt;/wfs:Literal&gt; *     &lt;/CssParameter&gt; *  3. &lt;CssParameter name=&quot;stroke-width&quot;&gt; *          &lt;wfs:Add&gt; *              &lt;wfs:PropertyName&gt;/A&lt;/wfs:PropertyName&gt; *              &lt;wfs:Literal&gt;2&lt;/wfs:Literal&gt; *          &lt;/wfs:Add&gt; *     &lt;/CssParameter&gt; *  4. &lt;Label&gt;This is city &quot;&lt;wfs:PropertyName&gt;/NAME&lt;/wfs:PropertyName&gt;&quot; *  of state &lt;wfs:PropertyName&gt;/STATE&lt;/wfs:PropertyName&gt;&lt;/Label&gt; *   * </pre> *  * The allowed SVG/CSS styling parameters for a stroke are: stroke (color), * stroke-opacity, stroke-width, stroke-linejoin, stroke-linecap, * stroke-dasharray, and stroke-dashoffset. The chosen parameter is given by the * name attribute of the CssParameter element. * <p> *  * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @version $Revision: 1.9 $ $Date: 2006/09/26 12:44:22 $
 */

public class CssParameter implements Marshallable {

    private ParameterValueType pvt = null;
    private String name = null;

    /**
     * constructor initializing the class with the <CssParameter>
     */
    CssParameter(String name, ParameterValueType pvt) {
        this.name = name;
        this.pvt = pvt;
    }

    /**
     * Returns the name attribute's value of the CssParameter.
     * <p>
     * 
     * @return the value of the name attribute of the CssParameter
     */
    String getName() {
        return name;
    }

    /**
     * Sets the name attribute's value of the CssParameter.
     * <p>
     * 
     * @param name
     *            the value of the name attribute of the CssParameter
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the value of the CssParameter as an <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @return the mixed content of the element
     */
    public ParameterValueType getValue() {
        return pvt;
    }

    /**
     * Sets the value of the CssParameter as an <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @param value
     *            the mixed content of the element
     */
    void setValue(ParameterValueType value) {
        this.pvt = value;
    }

    /**
     * Returns the (evaluated) value of the CssParameter as a simple
     * <tt>String</tt>.
     * <p>
     * 
     * @param feature
     *            specifies the <tt>Feature</tt> to be used for evaluation of
     *            the underlying 'sld:ParameterValueType'
     * @return the (evaluated) <tt>String</tt> value of the parameter
     * @throws FilterEvaluationException
     *             if the evaluations fails
     */
    String getValue(Feature feature) throws FilterEvaluationException {
        return pvt.evaluate(feature);
    }

    /**
     * Sets the value of the CssParameter as a simple <tt>String</tt>.
     * <p>
     * 
     * @param value
     *            CssParameter-Value to be set
     */
    void setValue(String value) {
        ParameterValueType pvt = null;
        pvt = StyleFactory.createParameterValueType("" + value);
        this.pvt = pvt;
    }

    /**
     * exports the content of the CssParameter as XML formated String
     * 
     * @return xml representation of the CssParameter
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer("<CssParameter name=");
        sb.append("'" + name + "'>");
        sb.append(((Marshallable) pvt).exportAsXML());
        sb.append("</CssParameter>");
        
        return sb.toString();
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CssParameter.java,v $
Revision 1.9  2006/09/26 12:44:22  poth
set to public

Revision 1.8  2006/07/21 12:09:03  poth
methods that has no use outside the package are now declared package protected

Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
