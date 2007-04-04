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
 * The class encapsulates the result to a RPC. This can be an object or an * instance of <tt>RPCFault</tt> if an exception occured while performing a * RPC * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:18 $
 */

public class RPCMethodResponse {
    
    private boolean fault_ = false;
    private RPCParameter[] return_ = null;
    private RPCFault fault = null;

    
    RPCMethodResponse(RPCParameter[] return_) {
        this.return_ = return_;
    }
    
    RPCMethodResponse(RPCFault fault) {
        this.fault = fault;
        fault_ = true;
    }
    
    /**
     * returns true if the result contains a fault and not the expected data
     *
     * @return true if a fault occured
     */
    public boolean hasFault() {
        return fault_;
    }

    /**
     * returns the result of a method call as array of <tt>RPCParameter</tt>s
     *
     * @return result parameters
     */
    public RPCParameter[] getReturn() {
        return return_;
    }

    /**
     * returns the fault object if a fault occured while performing a RPC
     * 
     * @return fault object
     */
    public RPCFault getFault() {
        return fault;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RPCMethodResponse.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
