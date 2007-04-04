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
package org.deegree.enterprise.control;


/**
 * Extends <tt>RPCParameter_Impl</tt> by adding a new member variable 'name'.
 * A <tt>RPCMember</tt> is contained within a <tt>RPCStruct</tt> that enables
 * the creation of complex parameters.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.7 $ $Date: 2006/07/12 14:46:17 $
 */
public class RPCMember extends RPCParameter {
    
    private String name = null;
    
    /**
     * @param type class of the parameter
     * @param value the value of the parameter
     * @param name name of the member
     */
    public RPCMember(Class type, Object value, String name) {
        super( type, value );
        this.name = name;
    }

    /**
     * returns the name of the struct member
     * 
     * @return name of the member
     */
    public String getName() {
        return name;
    }

    
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RPCMember.java,v $
Revision 1.7  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
