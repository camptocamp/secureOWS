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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.6 $, $Date: 2006/07/12 14:46:18 $
 * 
 * @since 1.1
 *  
 */
public class Condition {
	
	private Map opMap = null;

	public Condition(OperationParameter[] operationParameters) {
		opMap = new HashMap();
		setOperationParameters( operationParameters );
	}

	public OperationParameter[] getOperationParameters(){
		OperationParameter[] op = new OperationParameter[opMap.size()];		
		return (OperationParameter[])opMap.values().toArray(op);
	}

	/**
	 * @param name
	 * 
	 */
	public OperationParameter getOperationParameter(String name){	    
		//return (OperationParameter)opMap.get(name);
        
        // XXXsyp
        OperationParameter op = (OperationParameter)opMap.get(name);
        if (op != null)
            return op;
        return new OperationParameter("dummy", true) {
            // XXXsyp needed??
        };
	}

	/**
	 * @param param
	 * 
	 */
	public void setOperationParameters(OperationParameter[] param){
		opMap.clear();
		for (int i = 0; i < param.length; i++) {
			opMap.put( param[i].getName(), param[i] );
		}
	}

	/**
	 * @param param
	 * 
	 */
	public void addOperationParameter(OperationParameter param){
		opMap.put( param.getName(), param );
	}

	/**
	 * @param param
	 * 
	 */
	public void removeOperationParameter(OperationParameter param){
		removeOperationParameter( param.getName() );
	}

	/**
	 * @param name
	 * 
	 */
	public void removeOperationParameter(String name){
		opMap.remove(name);
	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Condition.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
