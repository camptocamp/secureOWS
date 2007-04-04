//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/trigger/TriggerCapability.java,v 1.6 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.framework.trigger;

import java.util.List;
import java.util.Map;

import org.deegree.i18n.Messages;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/11/27 09:07:53 $
 *
 * @since 2.0
 */
public class TriggerCapability {
    
    private String name;
    
    private Class clss;

    private List<String> paramNames;

    private Map<String, Class> paramTypes;

    private Map<String, Object> paramValues;
    
    private List<TriggerCapability> nestedTrigger;

    /**
     * 
     * @param name
     * @param clss
     * @param paramTypes
     * @param paramValues
     * @param nestedTrigger
     * @throws TriggerException
     */
    public TriggerCapability( String name, Class clss, List<String> paramNames, 
                              Map<String, Class> paramTypes,
                              Map<String, Object> paramValues, 
                              List<TriggerCapability> nestedTrigger ) throws TriggerException {
        this.name = name;
        this.clss = clss;
        this.paramTypes = paramTypes;
        this.paramValues = paramValues;
        if ( paramTypes.size() != paramValues.size() ) {
            throw new TriggerException( Messages.getMessage( "FRAMEWORK_INVALID_TRIGGERCAP_PARAMS" ) );
        }
        this.paramNames = paramNames; 
        this.nestedTrigger = nestedTrigger;
    }
    
    /**
     * returns the triggers name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * returns a List of the names of all init parameter assigend to 
     * a @see Trigger 
     * @return
     */
    public List<String> getInitParameterNames() {
        return paramNames;
    }

    /**
     * returns the value of a named init parameter
     * @param name
     */
    public Object getInitParameterValue( String name ) {
        return paramValues.get( name );
    }

    /**
     * returns the type of a named init parameter
     * @param name
     */
    public Class getInitParameterType( String name ) {
        return paramTypes.get( name );
    }

    /**
     * returns the Class responsible for performing trigger functionality
     * @return
     */
    public Class getPerformingClass() {
        return clss;
    }
    
    /**
     * returns a list of Triggers that a nested within this Trigger
     * @return
     */
    public List<TriggerCapability> getTrigger() {
        return nestedTrigger;
    }
    
    public String toString() {
        return "Trigger name: " + name;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TriggerCapability.java,v $
 Revision 1.6  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.5  2006/10/10 11:25:11  poth
 bug fix - reading init params

 Revision 1.4  2006/09/28 09:45:45  poth
 bug fixes

 Revision 1.3  2006/09/22 13:25:02  poth
 ongoing implementation

 Revision 1.2  2006/09/19 20:11:02  poth
 ongoing implementation

 Revision 1.1  2006/09/14 16:06:05  poth
 initial check in


 ********************************************************************** */