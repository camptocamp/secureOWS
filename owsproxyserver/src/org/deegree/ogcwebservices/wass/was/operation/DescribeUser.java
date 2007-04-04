//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/operation/DescribeUser.java,v 1.2 2006/08/24 06:42:17 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wass.was.operation;

import java.util.Map;

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * <code>DescribeUser</code> is the request class for the deegree specific DescribeUser
 * operation. The DescribeUser operation returns user data such as email address when given
 * a session ID.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */

public class DescribeUser extends AbstractOGCWebServiceRequest {

    private static final long serialVersionUID = 6876661820417769484L;

    private String sessionID;
    
    /**
     * Creates a new <code>DescribeUser</code> object from the given
     * values.
     * 
     * @param id the request id
     * @param values the request parameters
     */
    public DescribeUser( String id, Map<String, String> values ) {
        super( values.get( "VERSION" ), id, values );
        sessionID = values.get( "SESSIONID" );
    }
    
    /**
     * @return Returns the session id.
     */
    public String getSessionID() {
        return sessionID;
    }

    public String getServiceName() {
        return "WAS";
    }

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeUser.java,v $
Revision 1.2  2006/08/24 06:42:17  poth
File header corrected

Revision 1.1  2006/08/11 08:58:50  schmitz
WAS implements the DescribeUser operation.



********************************************************************** */