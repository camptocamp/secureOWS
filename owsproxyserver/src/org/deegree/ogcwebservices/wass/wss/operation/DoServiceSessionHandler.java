//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/DoServiceSessionHandler.java,v 1.10 2006/08/24 06:42:16 poth Exp $
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

package org.deegree.ogcwebservices.wass.wss.operation;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.wass.common.AuthenticationData;
import org.deegree.ogcwebservices.wass.common.Messages;
import org.deegree.ogcwebservices.wass.exceptions.DoServiceException;
import org.deegree.security.session.MemoryBasedSessionManager;
import org.deegree.security.session.Session;
import org.deegree.security.session.SessionStatusException;

/**
 * Checks if the session presented from a client is valid.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.10 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class DoServiceSessionHandler extends DoServiceHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( DoServiceSessionHandler.class );

    private final MemoryBasedSessionManager sessionManager;

    /**
     * 
     */
    public DoServiceSessionHandler() {
        sessionManager = MemoryBasedSessionManager.getInstance();
    }

    @Override
    public void handleRequest( DoService request )
                            throws DoServiceException {
        LOG.entering();
        AuthenticationData authData = request.getAuthenticationData();
        if ( authData.usesSessionAuthentication() ) {
            try {
                Session ses = sessionManager.getSessionByID( authData.getCredentials() );
                if ( ses == null || !ses.isAlive() ){
                    throw new DoServiceException(
                                                  Messages.format(
                                                                   "ogcwebservices.wass.ERROR_INVALID_SESSION",
                                                                   "WSS" ) );

                }
                setRequestAllowed( true );
            } catch ( SessionStatusException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new DoServiceException( e.getLocalizedMessage(), e );
            }
        }
        LOG.exiting();
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DoServiceSessionHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.10  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/26 15:02:58  bezema
 * Changes to this class. What the people have been up to: Finished the wass
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/26 07:24:01  bezema
 * Changes to this class. What the people have been up to: Deleted some comments and a small change to the messages.properties
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6 2006/06/23 13:53:47 schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some
 * exceptions and messages, reviewed/fixed some code. Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.5 2006/06/20
 * 15:31:05 bezema Changes to this class. What the people have been up to: It looks like the
 * completion of wss. was needs further checking in a tomcat environment. The Strings must still be
 * externalized. Logging is done, so is the formatting. Changes to this class. What the people have
 * been up to: Revision 1.4 2006/06/12 12:16:24 bezema Little rearanging of the GetSession classes,
 * DoService should be ready updating some errors
 * 
 * Revision 1.3 2006/06/06 15:28:05 bezema Added a "null" prefix check in xmltools so that it, added
 * a characterset to the deegreeparams and the WSS::DoService class is almost done
 * 
 * Revision 1.2 2006/05/30 15:11:28 bezema Working on the postclient from apachecommons to place a
 * request to the services behind the wss proxy
 * 
 * Revision 1.1 2006/05/30 12:46:33 bezema DoService is now handled
 * 
 **************************************************************************************************/

