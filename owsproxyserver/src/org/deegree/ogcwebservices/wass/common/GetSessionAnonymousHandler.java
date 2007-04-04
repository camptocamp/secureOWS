//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/GetSessionAnonymousHandler.java,v 1.3 2006/08/24 06:42:16 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
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

package org.deegree.ogcwebservices.wass.common;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.session.MemoryBasedSessionManager;
import org.deegree.security.session.Session;
import org.deegree.security.session.SessionStatusException;

/**
 * A <code>GetSessionAnonymousHandler</code> class <br/> creates a session for a client and saves
 * it in the local database.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class GetSessionAnonymousHandler implements GetSessionHandler {

    private final MemoryBasedSessionManager sessionManager;

    private final int sessionLifetime;

    private static final ILogger LOG = LoggerFactory.getLogger( GetSessionAnonymousHandler.class );

    /**
     * Creates a sessionHandler which can handle a request for a session without being given any
     * username or password.
     * 
     * @param sessionLifetime
     *            the time for a session to be valid
     */
    public GetSessionAnonymousHandler( int sessionLifetime ) {
        LOG.entering();
        this.sessionLifetime = sessionLifetime;
        sessionManager = MemoryBasedSessionManager.getInstance();
        LOG.exiting();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.ogcwebservices.wass.common.GetSessionHandler#handleRequest(org.deegree.ogcwebservices.wass.common.GetSession)
     */
    public String handleRequest( GetSession request )
                            throws SessionStatusException, GeneralSecurityException {
        LOG.entering();
        String result = null;
        if ( request.getAuthenticationData().usesSessionAuthentication() ) {
            Session session = new Session( sessionLifetime );
            sessionManager.addSession( session );
            result = session.getSessionID().getId();
        }
        LOG.exiting();
        return result;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetSessionAnonymousHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/06/19 12:47:26  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1 2006/06/12 12:16:24 bezema
 * Changes to this class. What the people have been up to: Little rearanging of the GetSession
 * classes, DoService should be ready updating some errors Changes to this class. What the people
 * have been up to:
 **************************************************************************************************/

