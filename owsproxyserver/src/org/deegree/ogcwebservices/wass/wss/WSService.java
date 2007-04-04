//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/WSService.java,v 1.19 2006/10/01 11:15:42 poth Exp $
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

package org.deegree.ogcwebservices.wass.wss;

import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wass.common.AuthenticationData;
import org.deegree.ogcwebservices.wass.common.CloseSession;
import org.deegree.ogcwebservices.wass.common.CloseSessionHandler;
import org.deegree.ogcwebservices.wass.common.GetSession;
import org.deegree.ogcwebservices.wass.common.GetSessionAnonymousHandler;
import org.deegree.ogcwebservices.wass.common.GetSessionDispatcher;
import org.deegree.ogcwebservices.wass.common.GetSessionHandler;
import org.deegree.ogcwebservices.wass.common.GetSessionPasswordHandler;
import org.deegree.ogcwebservices.wass.common.Messages;
import org.deegree.ogcwebservices.wass.common.Operation_1_0;
import org.deegree.ogcwebservices.wass.common.WASSSecurityManager;
import org.deegree.ogcwebservices.wass.exceptions.DoServiceException;
import org.deegree.ogcwebservices.wass.wss.configuration.WSSConfiguration;
import org.deegree.ogcwebservices.wass.wss.configuration.WSSDeegreeParams;
import org.deegree.ogcwebservices.wass.wss.operation.DoService;
import org.deegree.ogcwebservices.wass.wss.operation.DoServiceAnonymousHandler;
import org.deegree.ogcwebservices.wass.wss.operation.DoServiceHandler;
import org.deegree.ogcwebservices.wass.wss.operation.DoServicePasswordHandler;
import org.deegree.ogcwebservices.wass.wss.operation.DoServiceSessionHandler;
import org.deegree.ogcwebservices.wass.wss.operation.WSSGetCapabilities;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.session.SessionStatusException;

/**
 * The Web Security Service - <code>WSService</code> - is the dispatcher of the entire WSS. It
 * calls the appropriate classes according to a given request.
 * 
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.19 $, $Date: 2006/10/01 11:15:42 $
 * 
 * @since 2.0
 */

public class WSService implements OGCWebService {

    private WSSConfiguration configuration = null;

    private static final ILogger LOG = LoggerFactory.getLogger( WSService.class );
    private static final TriggerProvider TP = TriggerProvider.create( WSService.class );

    private GetSessionHandler getSessionHandler = null;

    private CloseSessionHandler closeSessionHandler = null;

    private DoServiceHandler doServiceHandler = null;

    private WASSSecurityManager secManager = null;
    
    /**
     * Creates a new WebSecurityService with the given configuration( = capabilities) bean.
     * 
     * @param config
     * @throws OGCWebServiceException
     */
    public WSService( WSSConfiguration config ) throws OGCWebServiceException {
        configuration = config;

        WSSDeegreeParams dgParams = configuration.getDeegreeParams();
        if( configuration.isSessionAuthenticationSupported() ){
            for( Operation_1_0 operation : configuration.getOperationsMetadata().getAllOperations() ){
                if( "GetSession".equals(operation.getName()) ){
                    try {
                        ArrayList< GetSessionHandler > handlers = new ArrayList< GetSessionHandler>();
                        int lifetime = dgParams.getSessionLifetime();
                        if( configuration.isPasswordAuthenticationSupported() ){
                            secManager = new WASSSecurityManager( dgParams.getDatabaseConnection() );
                            handlers.add( new GetSessionPasswordHandler( secManager, lifetime ) );
                        }
                        if( configuration.isAnonymousAuthenticationSupported() ){
                            handlers.add( new GetSessionAnonymousHandler( lifetime ) );
                        }
                        if( handlers.size() == 0 )
                            throw new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_NO_AUTHMETHOD_HANDLER", "WSS") );
                        getSessionHandler = new GetSessionDispatcher( handlers );

                    } catch ( GeneralSecurityException e ) {
                        LOG.logError( e.getLocalizedMessage(), e );
                        throw new OGCWebServiceException( e.getLocalizedMessage() );
                    }
                }
                else if( "CloseSession".equals(operation.getName()) ){
                    closeSessionHandler = new CloseSessionHandler();
                }
            }
        }
    }

    /*
     * Returns the capabilities of the WSS. This is not the correct default behaviour, for a
     * GetCapabalities request must be able to request only parts of the capabilies of this wss .
     * 
     * @see org.deegree.ogcwebservices.OGCWebService#getCapabilities()
     */
    public OGCCapabilities getCapabilities() {
        return configuration;
    }

    /*
     * The core method. It dispatches the request to the appropriate classes which handle them.
     * 
     * @see org.deegree.ogcwebservices.OGCWebService#doService(org.deegree.ogcwebservices.OGCWebServiceRequest)
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {
        
        request = (OGCWebServiceRequest)TP.doPreTrigger( this, request )[0];
        
        Object response = null;
        try {
            if ( request instanceof WSSGetCapabilities ) {
                response = getCapabilities();
            } else if ( (getSessionHandler != null) && (request instanceof GetSession) ) {
                response = getSessionHandler.handleRequest( (GetSession) request );
            } else if ( (closeSessionHandler != null) && (request instanceof CloseSession) ) {
                closeSessionHandler.handleRequest( (CloseSession) request );
            } else if ( request instanceof DoService ) {
                AuthenticationData authData = ( (DoService) request ).getAuthenticationData();
                // password authentication used?
                if ( authData.usesPasswordAuthentication() ) {
                    if( configuration.isPasswordAuthenticationSupported() )
                        doServiceHandler = new DoServicePasswordHandler( secManager );
                    else 
                        response = new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_AUTHENTICATION_PASSWORD_NOT_SUPPORTED",
                                                                                "WSS") );
                } else if ( authData.usesSessionAuthentication() ) {
                    if( configuration.isSessionAuthenticationSupported() )
                        doServiceHandler = new DoServiceSessionHandler();
                    else 
                        response = new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_AUTHENTICATION_SESSION_NOT_SUPPORTED",
                                                                                "WSS") );
                } else if ( authData.usesAnonymousAuthentication() ) {
                    if( configuration.isAnonymousAuthenticationSupported() )
                        doServiceHandler = new DoServiceAnonymousHandler();
                    else 
                        response = new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_AUTHENTICATION_ANONYMOUS_NOT_SUPPORTED",
                                                                                "WSS") );                    
                }
                if( response == null ){
                    doServiceHandler.handleRequest( (DoService) request );
                    if ( doServiceHandler.requestAllowed() )
                        response = doServiceHandler.sendRequest( (DoService) request,
                                                                 ( configuration.getDeegreeParams() ).getSecuredServiceAddress().getLinkage().getHref(),
                                                                /*configuration.getDeegreeParams().getCharacterSet()*/null,
                                                                /*configuration.getDeegreeParams().getRequestTimeLimit()*/0,
                                                                 configuration.getSecuredServiceType() );
                }
            } else {
                LOG.logError( Messages.format( "ogcwebservices.wass.ERROR_UNKNOWN_REQUEST",
                                               new String[] { "WSS", request.getClass().getName() } ) );
                throw new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_UNKNOWN_REQUEST",
                                                                   new String[] {"WSS", request.getClass().getName() } ) );
            }
        } catch ( DoServiceException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            response = new OGCWebServiceException( e.getLocalizedMessage() );
        } catch ( SessionStatusException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            response = new OGCWebServiceException( Messages.format("ogcwebservices.wass.ERROR_INVALID_SESSION", "WSService" ));
        } catch ( GeneralSecurityException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_SECURITY_SYSTEM",
                                                               "WSService" ) );
        }

        return TP.doPostTrigger( this, response )[0];
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WSService.java,v $
 * Changes to this class. What the people have been up to: Revision 1.19  2006/10/01 11:15:42  poth
 * Changes to this class. What the people have been up to: trigger points for doService methods defined
 * Changes to this class. What the people have been up to:
 * Revision 1.18  2006/08/24 06:42:16  poth
 * File header corrected
 * Changes to this class. What the people have been up to:
 * Revision 1.17  2006/07/07 15:03:03  schmitz
 * Fixed a few warnings.
 * Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Revision 1.16  2006/06/27 13:10:47  bezema
 * Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Revision 1.15  2006/06/26 15:02:58  bezema
 * Finished the wass
 * Changes to this class. What the people have been up to:
 * Revision 1.14  2006/06/23 13:53:48  schmitz
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this
 * class. What the people have been up to: Revision 1.13 2006/06/20 15:31:05 bezema Changes to this
 * class. What the people have been up to: It looks like the completion of wss. was needs further
 * checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is
 * the formatting. Changes to this class.
 * What the people have been up to: Revision 1.12 2006/06/16 15:01:05 schmitz Changes to this class.
 * What the people have been up to: Fixed the WSS to work with all kinds of operation tests. It
 * checks out with both XML and KVP
 * requests. Changes to this class. What the
 * people have been up to: Revision 1.11 2006/06/13 15:19:11 bezema Changes to this class. What the
 * people have been up to: Removed includes -> lesser warnings Changes to this class. What the
 * people have been up to: Revision 1.10
 * 2006/06/13 15:16:18 bezema DoService Test
 * seems to work Changes to this class. What
 * the people have been up to: Revision 1.9 2006/06/12 16:11:21 bezema Changes to this class. What
 * the people have been up to: JUnit test work with for a GetCapabilities request - example
 * configurationfiles in resources added Changes to this class. What the people have been up to:
 * Revision 1.8 2006/06/12 12:16:24 bezema
 * Little rearanging of the GetSession
 * classes, DoService should be ready updating some errors Changes to this class. What the people
 * have been up to: Revision 1.7 2006/06/09
 * 12:58:32 schmitz Set up some tests for
 * WAS/WSS and the URN class. Commented out
 * some of the deegree param stuff in order for the Changes to this class. What the people have been
 * up to: tests to run. Tests have hardcoded
 * URLs in them, so they won't run just anywhere. Changes to this class. What the people have been
 * up to: Revision 1.6 2006/06/06 15:28:05
 * bezema Added a "null" prefix check in
 * xmltools so that it, added a characterset to the deegreeparams and the WSS::DoService class is
 * almost done Changes to this class. What
 * the people have been up to: Revision 1.5 2006/05/30 12:46:33 bezema Changes to this class. What
 * the people have been up to: DoService is now handled Changes to this class. What the people have
 * been up to: Revision 1.4 2006/05/30
 * 10:12:02 bezema Putting the cvs asci
 * option to -kkv which will update the $revision$ $author$ and $date$ variables in a cvs commit
 * Changes to this class. What the people
 * have been up to: Revision 1.3 2006/05/30 08:44:48 bezema Changes to this class. What the people
 * have been up to: Reararranging the layout (again) to use features of OOP. The owscommonDocument
 * is the real baseclass now. Changes to
 * this class. What the people have been up to: Revision 1.2 2006/05/29 16:24:59 bezema Changes to
 * this class. What the people have been up to: Rearranging the layout of the wss and creating the
 * doservice classes. The WSService class is implemented as well Changes to this class. What the
 * people have been up to: Revision 1.1
 * 2006/05/29 12:00:58 bezema Refactored the
 * security and authentication webservices into one package WASS (Web Authentication -and- Security
 * Services), also created a common package and a saml package which could be updated to work in the
 * future. Revision 1.1 2006/05/23 15:22:02
 * bezema Added configuration files to the wss and wss is able to parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
