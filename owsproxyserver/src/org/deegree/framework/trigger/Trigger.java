//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/trigger/Trigger.java,v 1.4 2006/09/22 15:04:58 poth Exp $
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


/**
 * 
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/09/22 15:04:58 $
 *
 * @since 2.0
 */
public interface Trigger {

	/**
	 * performs the action(s) defined by a concrete trigger. The returned value must
	 * have the same structure as the passed parameter. A trigger may changes the
	 * values of the passed parameter(s) but do not change their type or structure
	 * 
     * @param caller
	 * @param values
	 */
	public Object[] doTrigger(Object caller, Object... values);
    
    /**
     * returns the name of the Trigger. The name starts with the name of the class
     * Trigger is assigend to followed by the method from where it is called followed
     * by its 'specific' name; e.g.<br>
     * org.deegree.enterprise.servlet.OGCServletController.doService.MyTrigger
     * @return
     */
	public String getName();
    
    /**
     * sets the name of a Trigger
     * @param name
     */
    public void setName(String name);

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Trigger.java,v $
Revision 1.4  2006/09/22 15:04:58  poth
ongoing implementation

Revision 1.3  2006/09/21 06:16:52  poth
ongoing implementation

Revision 1.2  2006/09/20 07:12:11  poth
ongoing implementation

Revision 1.1  2006/09/14 16:06:05  poth
initial check in


********************************************************************** */