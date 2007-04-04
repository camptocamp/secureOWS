//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/DoServicePasswordHandler.java,v 1.9 2006/08/29 19:14:17 poth Exp $
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
import org.deegree.ogcwebservices.wass.common.WASSSecurityManager;
import org.deegree.ogcwebservices.wass.exceptions.DoServiceException;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.model.User;

/**
 * This class handles a webservice request which is . It's primary roles are to check if the user
 * has (sufficient) credentials and to delegate the request to the service provider behind this
 * proxy.
 * 
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/08/29 19:14:17 $
 * 
 * @since 2.0
 */

public class DoServicePasswordHandler extends DoServiceHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( DoServicePasswordHandler.class );

    private final SecurityAccessManager manager;

    /**
     * @param securityManager
     * @throws GeneralSecurityException
     */
    public DoServicePasswordHandler( WASSSecurityManager securityManager )
                            throws GeneralSecurityException {
        manager = securityManager.getSecurityAccessManager();
    }

    /**
     * Checks if the request has sufficient credentials to request the feature, and if so request
     * the feature at the service.
     * 
     * @throws DoServiceException
     */
    @Override
    public void handleRequest( DoService request )
                            throws DoServiceException {
        LOG.entering();
        AuthenticationData authData = request.getAuthenticationData();
        // password authentication used?
        if ( authData.usesPasswordAuthentication() ) {
            try {
                String user = authData.getUsername();
                String pass = authData.getPassword();
                User usr = manager.getUserByName( user );
                usr.authenticate( pass );
                // SecurityAccess secAccess = manager.acquireAccess( usr );
                // usr.hasRight( secAccess );
                /**
                 * TODO Here it is specified that the wss should check if the user has the
                 * sufficient right to do the service request. Deegree does these request in the
                 * owsRequestvalidator package, which means we only support - for the moment - a
                 * check if the user is registered. For Details on how to get the right for
                 * particular object please look at the following method.
                 * 
                 * @see org.deegree.security.owsrequestvalidator.GetFeatureRequestValidator#validateAgainstRightsDB
                 * 
                 */
            } catch ( GeneralSecurityException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new DoServiceException( e.getLocalizedMessage(), e );
            } catch ( StringIndexOutOfBoundsException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new DoServiceException( Messages.format( "ogcwebservices.wass.ERROR_USERPASS_NOT_PARSED",
                                                               "WSS" ) );
            }
        }

        setRequestAllowed( true );
        LOG.exiting();
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: DoServicePasswordHandler.java,v $
 * Revision 1.9  2006/08/29 19:14:17  poth
 * code formating / footer correction
 *
 * Revision 1.8  2006/08/24 06:42:16  poth
 * File header corrected
 * Changes to this class. What the people have been up to:
 * Revision 1.7  2006/06/26 07:24:01  bezema
 * Deleted some comments and a small change to the messages.properties
 * Changes to this class. What the people have been up to:
 * Revision 1.6  2006/06/23 13:53:47  schmitz
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Revision 1.5 2006/06/20 15:31:05 bezema
 * It looks like the completion of wss. was
 * needs further checking in a tomcat environment. The Strings must still be externalized. Logging
 * is done, so is the formatting. Changes to
 * this class. What the people have been up to: Revision 1.4 2006/06/16 15:01:05 schmitz Changes to
 * this class. What the people have been up to: Fixed the WSS to work with all kinds of operation
 * tests. It checks out with both XML and
 * KVP requests. Changes to this class. What
 * the people have been up to: Revision 1.3 2006/06/06 15:28:05 bezema Changes to this class. What
 * the people have been up to: Added a "null" prefix check in xmltools so that it, added a
 * characterset to the deegreeparams and the WSS::DoService class is almost done Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.2 2006/05/30 15:11:28 bezema Changes to this class. What the people have been up to:
 * Working on the postclient from apachecommons to place a request to the services behind the wss
 * proxy Revision 1.1 2006/05/30 12:46:33
 * bezema DoService is now handled
 * 
 * Revision 1.3 2006/05/30 10:12:02 bezema Putting the cvs asci option to -kkv which will update the
 * $revision$ $author$ and $date$ variables in a cvs commit
 * 
 * Revision 1.2 2006/05/30 08:44:48 bezema Reararranging the layout (again) to use features of OOP.
 * The owscommonDocument is the real baseclass now.
 * 
 * Revision 1.1 2006/05/29 16:24:59 bezema Rearranging the layout of the wss and creating the
 * doservice classes. The WSService class is implemented as well
 * 
 * 
 **************************************************************************************************/
