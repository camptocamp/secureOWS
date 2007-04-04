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
package org.deegree.ogcwebservices.getcapabilities;

import java.io.Serializable;

/**
 * Available Distributed Computing Platforms (DCPs) are listed here. At present, * only HTTP is defined. *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @version $Revision: 1.10 $ $Date: 2006/07/12 14:46:16 $
 */

public class DCPType implements Serializable {

    private Protocol protocol = null;

    /**
     * default constructor
     */
    public DCPType() {
    }

    /**
     * constructor initializing the class with the protocol
     * @param protocol the used protocol
     */
    public DCPType(Protocol protocol) {
        setProtocol(protocol);
    }

    /**
     * returns the protocol of the available Distributed Computing Platforms
     * (DCPs)
     * 
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * sets the protocol of the available Distributed Computing Platforms (DCPs)
     * 
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

   
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DCPType.java,v $
Revision 1.10  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
