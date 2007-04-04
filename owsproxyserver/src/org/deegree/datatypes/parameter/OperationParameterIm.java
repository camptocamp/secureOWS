// $Header:
// /cvsroot/deegree/src/org/deegree/datatypes/parameter/OperationParameterIm.java,v
// 1.5 2004/08/16 06:23:33 ap Exp $
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
package org.deegree.datatypes.parameter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.opengis.metadata.Identifier;
import org.opengis.parameter.OperationParameter;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.10 $, $Date: 2006/04/06 20:25:32 $
 * 
 * @since 2.0
 */
public class OperationParameterIm extends GeneralOperationParameterIm implements
        OperationParameter, Serializable {

    private static final long serialVersionUID = 1L;

    private Comparable maximumValue = null;

    private Comparable minimumValue = null;

    private Set validValues = null;

    private Class valueClass = null;

    private Object defaultValue = null;

    /**
     * Convenience constructor.
     * 
     * @param name
     * @param validValues
     */
    public OperationParameterIm(String name, String[] validValues) {
        this(new Identifier[0], name, null, 1, 0, null, null,
                buildSet(validValues), String.class, null);
    }
    
    /**
     * Convenience constructor.
     * 
     * @param name
     * @param validValues
     */
    public OperationParameterIm(String name, String[] validValues, Object defaultValue) {
        this(new Identifier[0], name, null, 1, 0, null, null,
                buildSet(validValues), defaultValue.getClass(), defaultValue);
    }

    /**
     * @param identifiers
     * @param name
     * @param remarks
     * @param maximumOccurs
     * @param minimumOccurs
     * @param maximumValue
     * @param minimumValue
     * @param validValues
     * @param valueClass
     * @param defaultValue
     */
    public OperationParameterIm(Identifier[] identifiers, String name,
            String remarks, int maximumOccurs, int minimumOccurs,
            Comparable maximumValue, Comparable minimumValue, Set validValues,
            Class valueClass, Object defaultValue) {
        super(identifiers, name, remarks, maximumOccurs, minimumOccurs);
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.validValues = validValues;
        this.valueClass = valueClass;
        this.defaultValue = defaultValue;
    }

    /**
     * @return Returns the defaultValue.
    */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     *            The defaultValue to set.
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return Returns the maximumValue.
     * 
     */
    public Comparable getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     *            The maximumValue to set.
     */
    public void setMaximumValue(Comparable maximumValue) {
        this.maximumValue = maximumValue;
    }

    /**
     * @return Returns the minimumValue.
     */
    public Comparable getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     *            The minimumValue to set.
     */
    public void setMinimumValue(Comparable minimumValue) {
        this.minimumValue = minimumValue;
    }

    /**
     * @return Returns the validValues.
     */
    public Set getValidValues() {
        return validValues;
    }

    /**
     * @param validValues
     *            The validValues to set.
    */
    public void setValidValues(Set validValues) {
        this.validValues = validValues;
    }

    /**
     * @return Returns the valueClass.
     */
    public Class getValueClass() {
        return valueClass;
    }

    /**
     * @param valueClass
     *            The valueClass to set.
     */
    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }

    private static Set buildSet(String[] values) {
        Set valueSet = new LinkedHashSet();
        if ( values != null ) {
            for (int i = 0; i < values.length; i++) {
                valueSet.add(values[i]);
            }
        }
        return valueSet;
    }
}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * OperationParameterIm.java,v $ Revision 1.1 2004/05/25 12:55:01 ap no message
 * 
 *  
 ******************************************************************************/
