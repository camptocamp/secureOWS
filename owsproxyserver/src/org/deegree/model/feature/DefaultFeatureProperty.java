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

package org.deegree.model.feature;

import java.io.Serializable;

import org.deegree.datatypes.QualifiedName;

/**
*
* the interface describes a property entry of a feature. It is made of
* a name and a value associated to it.
* 
* <p>---------------------------------------------------------------</p>
*
* @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
* @version $Revision: 1.13 $ $Date: 2006/10/16 09:34:59 $
*/ 

class DefaultFeatureProperty implements FeatureProperty, Serializable {

    private Object value = null;
    private QualifiedName name = null;  
  
    
   /**
    * constructor for complete initializing the FeatureProperty
    * @param name qualified name of the property 
    * @param value the properties value
    */
    DefaultFeatureProperty(QualifiedName name, Object value) {
        setValue( value );
        this.name = name;
    }
    
    /**
     * returns the qualified name of the property
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * returns the value of the property
     */
    public Object getValue() {        
        return value;
    }
    
    /**
     * returns the value of the property; if the value is null the passed 
     * defaultValuewill be returned
     * 
     * @param defaultValue
     */
    public Object getValue(Object defaultValue) {
        if ( value == null ) {
            return defaultValue;
        }
        return value;
    }

    /**
     * sets the value of the property
     */
    public void setValue(Object value) {
        this.value = value;
    }

	public String toString() {
		String ret = null;
		ret = "name = " + name + "\n";
		ret += "value = " + value + "\n";
		return ret;
	}

	public Feature getOwner(){
		return null;
	}

}
/*
 * Changes to this class. What the people haven been up to:
 *
 * $Log: DefaultFeatureProperty.java,v $
 * Revision 1.13  2006/10/16 09:34:59  poth
 * enbaled default value return for Feature.getDefaultProperty and FeatureProperty.getValue
 *
 * Revision 1.12  2006/04/06 20:25:27  poth
 * *** empty log message ***
 *
 * Revision 1.11  2006/04/04 20:39:42  poth
 * *** empty log message ***
 *
 * Revision 1.10  2006/03/30 21:20:26  poth
 * *** empty log message ***
 *
 * Revision 1.9  2006/02/08 17:42:45  mschneider
 * Indentation fixes.
 *
 * Revision 1.8  2005/08/30 13:40:03  poth
 * no message
 *
 * Revision 1.7  2005/06/16 08:27:31  poth
 * no message
 *
 * Revision 1.6  2005/03/01 08:28:07  poth
 * no message
 *
 * Revision 1.5  2005/02/28 14:14:05  poth
 * no message
 *
 * Revision 1.4  2005/02/28 13:34:57  poth
 * no message
 *
 * Revision 1.3  2005/01/18 22:08:54  poth
 * no message
 *
 * Revision 1.2  2005/01/17 22:16:28  poth
 * no message
 *
 * Revision 1.1  2004/06/28 06:42:16  ap
 * no message
 *
 * Revision 1.1  2004/05/24 07:05:33  ap
 * no message
 *
 * Revision 1.2  2004/02/09 07:59:57  poth
 * no message
 *
 * Revision 1.1.1.1  2002/09/25 16:00:38  poth
 * no message
 *
 *
 */
