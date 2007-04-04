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
package org.deegree.security.owsproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.6 $, $Date: 2006/08/02 09:43:43 $
 * 
 * @since 1.1
 */
public class OperationParameter {
	
    private boolean userCoupled = false;
	private String name = null;
	private List<String> values = new ArrayList<String>();
	private boolean any = false;
	    
	/**
	 * @param name
	 * @param value
	 * @param request
	 * @param userCoupled
	 */
	public OperationParameter(String name, String[] values, boolean userCoupled) {		
	    this.name = name;
	    setValues( values );
		this.userCoupled = userCoupled;
	}
	
	/**
	 * @param name
	 * @param any
	 */
	public OperationParameter(String name, boolean any) {			    
		this.any = any;
		this.name = name;
	}
	
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @return
     */
    public List<String> getValues() {
        return values;
    }
    
    /**
     * returns the first value of the list as integer. This is useful for operation
     * parameter that only allow one single string expression (e.g. BBOX)
     * @return
     */
    public String getFirstAsString() {
        return values.get( 0 );
    }
    
    /**
     * returns the first value of the list as integer. This is useful for operation
     * parameter that only allow one single integer expression (e.g. maxHeight)
     * @return
     */
    public int getFirstAsInt() {
        return Integer.parseInt( values.get( 0 ) );
    }
    
    /**
     * returns the first value of the list as integer. This is useful for operation
     * parameter that only allow one single double expression (e.g. resolution)
     * @return
     */
    public double getFirstAsDouble() {
        return Double.parseDouble( values.get( 0 ) );
    }
    
    /**
     * 
     * @param values
     */
    public void setValues(String[] values) {
        this.values.clear();
        for (int i = 0; i < values.length; i++) {
            this.values.add( values[i] );
        }
    }
    
    /**
     * 
     * @param value
     */
    public void addValue(String value) {
        values.add( value );
    }
    
    /**
     * 
     * @param value
     */
    public void removeValue(String value) {
        values.remove(value);
    }   
	
	/**
	 * @return Returns the userCoupled.
	 */
	public boolean isUserCoupled() {
		return userCoupled;
	}
	
	/**
	 * @param userCoupled The userCoupled to set.
	 */
	public void setUserCoupled(boolean userCoupled) {
		this.userCoupled = userCoupled;
	}
	
	/**
     * @return Returns the all.
     */
    public boolean isAny() {
        return any;
    }
    /**
     * @param all The all to set.
     */
    public void setAny(boolean any) {
        this.any = any;
    }
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OperationParameter.java,v $
Revision 1.6  2006/08/02 09:43:43  poth
return value of getValues substituted by a typed List / additional convenience methods for accessing first value

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
