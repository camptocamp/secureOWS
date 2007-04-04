//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/GetSessionDispatcher.java,v 1.9 2006/06/27 13:10:47 bezema Exp $
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

import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.session.SessionStatusException;

/**
 * This class handles/dispatches all GetSession requests.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/06/27 13:10:47 $
 * 
 * @since 2.0
 */

public class GetSessionDispatcher implements GetSessionHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( GetSessionDispatcher.class );

    private ArrayList< GetSessionHandler> handlers;

    /**
     * Constructs a new handler ready to process your requests.
     * @param getSessionHandlers 
     */
    public GetSessionDispatcher( ArrayList<GetSessionHandler> getSessionHandlers ) {
        this.handlers = getSessionHandlers;
    }

    /**
     * Returns a new session ID.
     * 
     * @param request
     *            the request on which to base the authentication
     * @return the new session ID or null, if no authentication took place
     * @throws SessionStatusException
     * @throws GeneralSecurityException
     */
    public String handleRequest( GetSession request )
                            throws SessionStatusException, GeneralSecurityException {
        LOG.entering();

        String res = null;
       
        for ( GetSessionHandler handler : handlers ){
            res = handler.handleRequest( request );
            if( res != null ) //The handler handled the request
                return res;
        }

        if( res == null ){
            // did not find a handler, just return null and log a warning
            StringBuffer msg = new StringBuffer( Messages.getString( "ogcwebservices.wass.ERROR_NO_AUTHMETHOD_HANDLER" ) );
            msg.append( request.getAuthenticationData().getAuthenticationMethod() );
            LOG.logWarning( msg.toString() );
        }
        
        LOG.exiting();
        return res;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetSessionDispatcher.java,v $
 * Changes to this class. What the people have been up to: Revision 1.9  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/26 15:02:58  bezema
 * Changes to this class. What the people have been up to: Finished the wass
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/23 13:53:47  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/19 12:47:26  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5 2006/06/16 15:01:05 schmitz
 * Changes to this class. What the people have been up to: Fixed the WSS to work with all kinds of
 * operation tests. It checks out Changes to this class. What the people have been up to: with both
 * XML and KVP requests. Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.4 2006/06/12 12:16:24 bezema Changes to this
 * class. What the people have been up to: Little rearanging of the GetSession classes, DoService
 * should be ready updating some errors Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3 2006/05/30 11:44:51 schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed some
 * warnings. Changes to this class. What the people have been up to: Revision 1.2 2006/05/29
 * 16:24:59 bezema Rearranging the layout of the wss and creating the doservice classes. The
 * WSService class is implemented as well
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
 * Revision 1.1 2006/05/22 15:48:12 schmitz Added some rudimentary Handler classes.
 * 
 * 
 **************************************************************************************************/
