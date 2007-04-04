//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/CloseSessionHandler.java,v 1.5 2006/06/26 15:02:58 bezema Exp $
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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.security.session.MemoryBasedSessionManager;
import org.deegree.security.session.SessionStatusException;

/**
 * This class handles CloseSession requests as specified by the GDI NRW Access Control spec V1.0.
 * Note that according to the spec, the response should be empty.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/06/26 15:02:58 $
 * 
 * @since 2.0
 */

public class CloseSessionHandler {

    private static ILogger LOG = LoggerFactory.getLogger( CloseSessionHandler.class );

    private MemoryBasedSessionManager sessionManager = null;

    /**
     * Creates new instance that can handle CloseSession requests.
     */
    public CloseSessionHandler() {
        sessionManager = MemoryBasedSessionManager.getInstance();
    }

    /**
     * Closes the session encapsulated in the request.
     * 
     * @param request
     *            the request
     * @throws SessionStatusException
     */
    public void handleRequest( CloseSession request )
                            throws SessionStatusException {
        LOG.entering();
        String session = request.getSessionID();
        if ( sessionManager.getSessionByID( session ) == null )
            throw new SessionStatusException( Messages.format( "ogcwebservices.wass.ERROR_INVALID_SESSION", request.getServiceName() ) );
        sessionManager.removeSessionByID( session );
        LOG.exiting();
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: CloseSessionHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/26 15:02:58  bezema
 * Changes to this class. What the people have been up to: Finished the wass
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/06/23 13:53:47  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3 2006/06/16 15:01:05 schmitz
 * Changes to this class. What the people have been up to: Fixed the WSS to work with all kinds of
 * operation tests. It checks out Changes to this class. What the people have been up to: with both
 * XML and KVP requests. Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.2 2006/05/30 11:44:51 schmitz Changes to this
 * class. What the people have been up to: Updated the documentation, fixed some warnings. Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.1 2006/05/29 12:00:58 bezema Changes to this class. What the people have been
 * up to: Refactored the security and authentication webservices into one package WASS (Web
 * Authentication -and- Security Services), also created a common package and a saml package which
 * could be updated to work in the future. Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3 2006/05/26 11:55:09 schmitz
 * Changes to this class. What the people have been up to: Extended the handlers to actually do
 * something useful. Added configuration package to WAS, added plan text file. Added
 * GetSessionHandler interface, added CloseSessionHandler. Changes to this class. What the people
 * have been up to:
 * 
 * 
 **************************************************************************************************/
