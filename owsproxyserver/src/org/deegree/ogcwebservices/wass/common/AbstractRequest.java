//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/AbstractRequest.java,v 1.7 2006/06/19 15:34:04 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Meckenheimer Allee 176
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
package org.deegree.ogcwebservices.wass.common;

import java.util.Map;

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * Base class for the GDI NRW access control specification's requests.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/06/19 15:34:04 $
 * 
 * @since 2.0
 */

public abstract class AbstractRequest extends AbstractOGCWebServiceRequest {

    private String service = null;

    private String request = null;

    /**
     * Constructs new base request from the given map.
     * @param id the request id
     * @param kvp
     *            the map
     */
    public AbstractRequest( String id, Map<String, String> kvp ) {
        super( kvp.get( "VERSION" ), id, null );
        service = kvp.get( "SERVICE" );
        request = kvp.get( "REQUEST" );
    }

    /**
     * Constructs new base request from the given values.
     *
     * @param id the request id
     * @param version
     *            request version
     * @param service
     *            request service
     * @param request
     *            request name
     */
    public AbstractRequest(  String id, String version, String service, String request ) {
        super( version, id, null );
        this.service = service;
        this.request = request;
    }

    /**
     * @return the service of this request
     */
    public String getServiceName() {
        return service;
    }

    /**
     * @return the request's name
     */
    public String getRequest() {
        return request;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: AbstractRequest.java,v $
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/19 15:34:04  bezema
 * Changes to this class. What the people have been up to: changed wass to handle things the right way
 * Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.6 2006/06/13 15:16:18 bezema Changes to
 * this class. What the people have been up to: DoService Test seems to work Changes to this class.
 * What the people have been up to: Changes to this class. What the people have been up to: Revision
 * 1.5 2006/05/30 11:44:51 schmitz Changes to this class. What the people have been up to: Updated
 * the documentation, fixed some warnings. Changes to this class. What the people have been up to:
 * Revision 1.4 2006/05/30 11:20:05 schmitz docu test
 * 
 * Revision 1.3 2006/05/30 11:17:10 schmitz docu test2
 * 
 * Revision 1.2 2006/05/30 11:16:32 schmitz docu test
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.1 2006/05/26 14:38:32 schmitz Added some KVP constructors to WAS operations. Added
 * some comments, updated the plan. Restructured WAS operations by adding an AbstractRequest base
 * class.
 * 
 * 
 * 
 **************************************************************************************************/
