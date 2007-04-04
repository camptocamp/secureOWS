//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/GetSessionPasswordHandler.java,v 1.8 2006/08/29 19:14:17 poth Exp $
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
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.model.User;
import org.deegree.security.session.MemoryBasedSessionManager;
import org.deegree.security.session.Session;
import org.deegree.security.session.SessionStatusException;

/**
 * GetSession handler that handles the password method.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/08/29 19:14:17 $
 * 
 * @since 2.0
 */

public class GetSessionPasswordHandler implements GetSessionHandler {

    private final static ILogger LOG = LoggerFactory.getLogger( GetSessionPasswordHandler.class );

    private final SecurityAccessManager manager;

    private final MemoryBasedSessionManager sessionManager;
    
    private int sessionLifetime = 0;

    /**
     * Creates new instance using a wass SecurityAccessManager instance to create and instantiate
     * the deegree SecurityAccessManager.
     * 
     * @param securityManager
     * @param sessionLifetime 
     * @throws GeneralSecurityException
     */
    public GetSessionPasswordHandler( WASSSecurityManager securityManager, int sessionLifetime )
                            throws GeneralSecurityException {
        manager = securityManager.getSecurityAccessManager();
        sessionManager = MemoryBasedSessionManager.getInstance();
        this.sessionLifetime = sessionLifetime;
    }

    /**
     * Handles only requests with password authentication method.
     * 
     * @return a string with a session ID or null, if the method of the request is not password
     * @see org.deegree.ogcwebservices.wass.common.GetSessionHandler#handleRequest(org.deegree.ogcwebservices.wass.common.GetSession)
     */
    public String handleRequest( GetSession request )
                            throws SessionStatusException, GeneralSecurityException {
        LOG.entering();

        AuthenticationData authData = request.getAuthenticationData();
        String res = null;
        // password authentication used?
        if ( authData.usesPasswordAuthentication() ) {

            // use manager to authenticate the user with the password
            String user = authData.getUsername();
            String pass = authData.getPassword();
            User usr = manager.getUserByName( user );

            usr.authenticate( pass );

            // create session
            Session session = MemoryBasedSessionManager.createSession( authData.getUsername(), sessionLifetime );
            sessionManager.addSession( session );
            res = session.getSessionID().getId();
        }

        LOG.exiting();
        return res;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GetSessionPasswordHandler.java,v $
 * Revision 1.8  2006/08/29 19:14:17  poth
 * code formating / footer correction
 *
 * Revision 1.7  2006/06/26 15:02:58  bezema
 * Finished the wass
 * Changes to this class. What the people have been up to:
 * Revision 1.6  2006/06/19 12:47:26  schmitz
 * Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Revision 1.5 2006/06/16 15:01:05 schmitz
 * Fixed the WSS to work with all kinds of
 * operation tests. It checks out with both
 * XML and KVP requests. Changes to this
 * class. What the people have been up to: Revision 1.4 2006/05/30 11:44:51 schmitz Changes to this
 * class. What the people have been up to: Updated the documentation, fixed some warnings. Changes
 * to this class. What the people have been up to: Revision 1.3 2006/05/30 08:44:48 bezema
 * Reararranging the layout (again) to use features of OOP. The owscommonDocument is the real
 * baseclass now.
 * 
 * Revision 1.2 2006/05/29 16:24:59 bezema Rearranging the layout of the wss and creating the
 * doservice classes. The WSService class is implemented as well
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/26 14:38:32 schmitz Added some KVP constructors to WAS operations. Added
 * some comments, updated the plan. Restructured WAS operations by adding an AbstractRequest base
 * class.
 * 
 * Revision 1.1 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
 * 
 **************************************************************************************************/
