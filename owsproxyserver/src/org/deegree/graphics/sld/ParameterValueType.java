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
package org.deegree.graphics.sld;

import java.io.Serializable;
import java.util.ArrayList;

import org.deegree.framework.xml.Marshallable;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * 
 * 
 *
 * @version $Revision: 1.10 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.10 $
 *
 * @since 2.0
 */
public class ParameterValueType implements Serializable, Marshallable {

    private ArrayList components = new ArrayList();

    /**
     * Constructs a new <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @param components
     *            <tt>String</tt>s/<tt>Expression</tt> s that make up the
     *            contents of the <tt>ParameterValueType</tt>
     */
    public ParameterValueType(Object[] components) {
        setComponents(components);
    }

    /**
     * Returns the contents (mix of <tt>String</tt>/<tt>Expression</tt>
     * -objects) of this <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @return mix of <tt>String</tt>/<tt>Expression</tt> -objects
     * 
     */
    public Object[] getComponents() {
        return components.toArray(new Object[components.size()]);
    }

    /**
     * Sets the contents (mix of <tt>String</tt>/<tt>Expression</tt>
     * -objects) of this <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @param components
     *            mix of <tt>String</tt> and <tt>Expression</tt> -objects
     */
    public void setComponents(Object[] components) {
        this.components.clear();
        
        if (components != null) {
            this.components.ensureCapacity( components.length );
            for (int i = 0; i < components.length; i++) {
                this.components.add(components[i]);
            }
        }
    }

    /**
     * Concatenates a component (a<tt>String</tt> or an <tt>Expression</tt>
     * -object) to this <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @param component
     *            either a <tt>String</tt> or an <tt>Expression</tt> -object
     */
    public void addComponent(Object component) {
        components.add(component);
    }

    /**
     * Removes a component (a<tt>String</tt> or an <tt>Expression</tt>
     * -object) from this <tt>ParameterValueType</tt>.
     * <p>
     * 
     * @param component
     *            either a <tt>String</tt> or an <tt>Expression</tt> -object
     */
    public void removeComponent(Object component) {
        components.remove(components.indexOf(component));
    }

    /**
     * Returns the actual <tt>String</tt> value of this object. Expressions
     * are evaluated according to the given <tt>Feature</tt> -instance.
     * <p>
     * 
     * @param feature
     *            used for the evaluation of the underlying
     *            'wfs:Expression'-elements
     * @return the (evaluated) String value
     * @throws FilterEvaluationException
     *             if the evaluation fails
     */
    public String evaluate(Feature feature) throws FilterEvaluationException {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < components.size(); i++) {
            Object component = components.get(i);
            if (component instanceof Expression) {
                sb.append(((Expression) component).evaluate(feature));
            } else if (component != null && component instanceof String) {
                sb.append(((String) component).trim());
            } else {
                sb.append(component);
            }
        }

        return sb.toString();
    }

    /**
     * exports the content of the ParameterValueType as XML formated String
     * 
     * @return xml representation of the ParameterValueType
     */
    public String exportAsXML() {
        

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < components.size(); i++) {
            Object component = components.get(i);
            if (component instanceof Expression) {
                sb.append(((Expression) component).toXML());
            } else if (component != null && component instanceof String) {
                sb.append(((String) component).trim());
            } else {
                sb.append(component);
            }
        }

        
        return sb.toString();
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ParameterValueType.java,v $
Revision 1.10  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.9  2006/09/25 20:29:28  poth
changes required for extracting PropertyPath's used by a Style

Revision 1.8  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.7  2006/07/04 19:09:34  poth
comments corrected - code formatation


********************************************************************** */