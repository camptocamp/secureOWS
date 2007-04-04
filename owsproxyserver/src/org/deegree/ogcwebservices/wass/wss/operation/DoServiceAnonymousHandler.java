//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/DoServiceAnonymousHandler.java,v 1.8 2006/08/24 06:42:16 poth Exp $
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
import org.deegree.ogcwebservices.wass.exceptions.DoServiceException;

/**
 * Enables the possibily for a client to make an anonymous DoService Request to the wss.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class DoServiceAnonymousHandler extends DoServiceHandler {

    /**
     * The logger enhances the quality and simplicity of Debugging within the deegree2 framework
     */
    private static final ILogger LOG = LoggerFactory.getLogger( DoServiceAnonymousHandler.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.ogcwebservices.wass.wss.operation.DoServiceHandler#handleRequest(org.deegree.ogcwebservices.wass.wss.operation.DoService)
     */
    @Override
    public void handleRequest( DoService request )
                            throws DoServiceException {
        LOG.entering();
        AuthenticationData authData = request.getAuthenticationData();
        if ( authData.usesAnonymousAuthentication() ) {
            /*
             * This was intentended to be a - mandatory email kind of thing.
             */
//            if ( !( "" ).equals( authData.getCredentials().trim() ) ) {
//                LOG.logError( "#handleRequest no anonymous name presented" );
//                throw new DoServiceException( "WSS: the given sessionid is unknown or expired.\n" );
//            }
            setRequestAllowed( true );
        }
        LOG.exiting();
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DoServiceAnonymousHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/26 07:28:03  bezema
 * Changes to this class. What the people have been up to: just a comment
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/26 07:24:01  bezema
 * Changes to this class. What the people have been up to: Deleted some comments and a small change to the messages.properties
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/23 13:53:48  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/06/20 15:31:04  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3 2006/06/12 12:16:24 bezema
 * Changes to this class. What the people have been up to: Little rearanging of the GetSession
 * classes, DoService should be ready updating some errors Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.2 2006/06/06
 * 15:28:05 bezema Changes to this class. What the people have been up to: Added a "null" prefix
 * check in xmltools so that it, added a characterset to the deegreeparams and the WSS::DoService
 * class is almost done Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.1 2006/05/30 12:46:33 bezema Changes to this
 * class. What the people have been up to: DoService is now handled Changes to this class. What the
 * people have been up to:
 **************************************************************************************************/
