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

package org.deegree.framework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The interface defines the access to a list of paramters that can be used
 * as submitted parameters to methods that may receive an variable list of
 * parameters.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mays $
 * 
 * @version $Revision: 1.7 $ $Date: 2006/10/04 10:25:12 $
 */
final public class ParameterList {

    private HashMap<String,Parameter> params = new HashMap<String,Parameter>();
    private ArrayList keys = new ArrayList(100); 

   /**
    * returns the parameter that matches the submitted name. if no parameter can
    * be found <tt>null</tt> will be returned.
    */
    public Parameter getParameter(String name)
    {
        return (Parameter)params.get( name );
    }

   /**
    * adds a new <tt>Parameter</tt> to the list
    */
    public void addParameter(String name, Object value)
    {
        Parameter p = new Parameter( name, value );
        addParameter( p );
    }

   /**
    * adds a new <tt>Parameter</tt> to the list
    */
    public void addParameter(Parameter param)
    {
        params.put( param.getName(), param );
        keys.add( param.getName() );
    }

   /**
    * returns all <tt>Parameter</tt>s contained within the list as array.
    * it is guarenteered that the arrays isn't <tt>null</tt>
    * 
    * @return returns an array with all Parameters from the list.
    */
    public Parameter[] getParameters() {
        Parameter[] p = new Parameter[ keys.size() ];
        Iterator iter = keys.iterator();
        int i = 0;
        while ( iter.hasNext() ) {
            p[i++] = params.get( iter.next() );
        }
        return p;
    }

   /**
    * returns an array of all <tt>Parameter</tt>s names.
    * it is guarenteered that the arrays isn't <tt>null</tt>
    */
    public String[] getParameterNames()
    {
        String[] s = new String[ keys.size() ];
        return (String[])keys.toArray( s );
    }

   /**
    * returns an array of all <tt>Parameter</tt>s values.
    * it is guarenteered that the arrays isn't <tt>null</tt>
    */
    public Object[] getParameterValues()
    {
        Object[] o = new Object[ keys.size() ];
        for (int i = 0; i < o.length; i++) {
			Parameter p = (Parameter)params.get( keys.get(i) );
            o[i] = p.getValue();
        }
        return o;
    }
    
    /**
     * removes a parameter from the list
     *
     * @param name name of the parameter
     */
    public Parameter removeParameter(String name) {
    	keys.remove( name );
        return (Parameter)params.remove( name );
    }

		
	public String toString() {
		String ret = null;
		ret = "params = " + params + "\n";
		return ret;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ParameterList.java,v $
Revision 1.7  2006/10/04 10:25:12  mays
remove bug in getParameters()

Revision 1.6  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
