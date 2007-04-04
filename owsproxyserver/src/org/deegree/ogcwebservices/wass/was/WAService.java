//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/WAService.java,v 1.14 2006/10/01 11:15:43 poth Exp $
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
package org.deegree.ogcwebservices.wass.was;

import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
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
import org.deegree.ogcwebservices.wass.was.configuration.WASConfiguration;
import org.deegree.ogcwebservices.wass.was.configuration.WASDeegreeParams;
import org.deegree.ogcwebservices.wass.was.operation.DescribeUser;
import org.deegree.ogcwebservices.wass.was.operation.DescribeUserHandler;
import org.deegree.ogcwebservices.wass.was.operation.WASGetCapabilities;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.session.SessionStatusException;

/**
 * This is the main WAService class that implements a WAS according to the GDI NRW spec V1.0.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.14 $, $Date: 2006/10/01 11:15:43 $
 * 
 * @since 2.0
 */

public class WAService implements OGCWebService {

    private WASConfiguration configuration = null;

    private static final ILogger LOG = LoggerFactory.getLogger( WAService.class );
    private static final TriggerProvider TP = TriggerProvider.create( WAService.class );

    private GetSessionHandler getSessionHandler = null;

    private CloseSessionHandler closeSessionHandler = null;
    
    private DescribeUserHandler describeUserHandler = null;

    /**
     * Creates a new service according to the given configuration.
     * 
     * @param configuration
     *            the config
     * @throws OGCWebServiceException
     */
    public WAService( WASConfiguration configuration ) throws OGCWebServiceException {
        LOG.entering();

        this.configuration = configuration;

        // setup GetSession/CloseSession handler(s)
        WASDeegreeParams dgParams = configuration.getDeegreeParams();
        if( configuration.isSessionAuthenticationSupported() ){
            for( Operation_1_0 operation : configuration.getOperationsMetadata().getAllOperations() ){
                if( "GetSession".equals(operation.getName()) ){
                    try {
                        ArrayList< GetSessionHandler > handlers = new ArrayList< GetSessionHandler>(4);
                        int lifetime = dgParams.getSessionLifetime();
                        if( configuration.isPasswordAuthenticationSupported() ){
                            WASSSecurityManager secManager = new WASSSecurityManager( dgParams.getDatabaseConnection() );
                            handlers.add( new GetSessionPasswordHandler( secManager, lifetime ) );
                        }
                        if( configuration.isAnonymousAuthenticationSupported() ){
                            handlers.add( new GetSessionAnonymousHandler( lifetime ) );
                        }
                        if( handlers.size() == 0 )
                            throw new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_NO_AUTHMETHOD_HANDLER", "WAS") );
                        getSessionHandler = new GetSessionDispatcher( handlers );

                    } catch ( GeneralSecurityException e ) {
                        LOG.logError( e.getLocalizedMessage(), e );
                        throw new OGCWebServiceException( e.getLocalizedMessage() );
                    }
                } else if( "CloseSession".equals( operation.getName() ) ){
                    closeSessionHandler = new CloseSessionHandler();
                } else if ( "DescribeUser".equals( operation.getName() ) ) {
                    try{
                        WASSSecurityManager secManager = new WASSSecurityManager( dgParams.getDatabaseConnection() );
                        describeUserHandler = new DescribeUserHandler( secManager );
                    } catch ( GeneralSecurityException e ) {
                        LOG.logError( e.getLocalizedMessage(), e );
                        throw new OGCWebServiceException( e.getLocalizedMessage() );
                    }
                }
            }
        }

        LOG.exiting();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.ogcwebservices.OGCWebService#getCapabilities()
     */
    public OGCCapabilities getCapabilities() {
        return configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.ogcwebservices.OGCWebService#doService(org.deegree.ogcwebservices.OGCWebServiceRequest)
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {
        
        request = (OGCWebServiceRequest)TP.doPreTrigger( this, request )[0];
        
        Object response = null;

        try{        
            if ( request instanceof WASGetCapabilities ) {
                response = configuration;
            } else if ( ( getSessionHandler != null ) && ( request instanceof GetSession ) ) {
                response = getSessionHandler.handleRequest( (GetSession) request );
            } else if ( ( closeSessionHandler != null ) && ( request instanceof CloseSession ) ) {
                closeSessionHandler.handleRequest( (CloseSession) request );
            } else if ( ( describeUserHandler != null ) && ( request instanceof DescribeUser ) ) {
                response = describeUserHandler.handleRequest( (DescribeUser) request );
            } else {
                throw new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_UNKNOWN_REQUEST",
                                                                 new String[] { getClass().getName(),
                                                                                request.getClass().getName() } ) );
            }
        } catch ( SessionStatusException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            response = new OGCWebServiceException( Messages.format("ogcwebservices.wass.ERROR_INVALID_SESSION", "WAService" ));
        } catch ( GeneralSecurityException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new OGCWebServiceException( Messages.format( "ogcwebservices.wass.ERROR_SECURITY_SYSTEM",
                                                               "WAService" ) );
        }

        return TP.doPostTrigger( this, response )[0];
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WAService.java,v $
 * Changes to this class. What the people have been up to: Revision 1.14  2006/10/01 11:15:43  poth
 * Changes to this class. What the people have been up to: trigger points for doService methods defined
 * Changes to this class. What the people have been up to:
 * Revision 1.13  2006/08/11 08:58:50  schmitz
 * WAS implements the DescribeUser operation.
 * Changes to this class. What the people have been up to:
 * Revision 1.12  2006/07/07 15:03:03  schmitz
 * Fixed a few warnings.
 * Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Revision 1.11  2006/06/27 13:10:47  bezema
 * Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Revision 1.10  2006/06/26 15:02:58  bezema
 * Finished the wass
 * Changes to this class. What the people have been up to:
 * Revision 1.9  2006/06/23 13:53:48  schmitz
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this
 * class. What the people have been up to: Revision 1.8 2006/06/23 10:23:50 schmitz Changes to this
 * class. What the people have been up to: Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people
 * have been up to: Revision 1.7 2006/06/16 15:01:05 schmitz Changes to this class. What the people
 * have been up to: Fixed the WSS to work with all kinds of operation tests. It checks out Changes
 * to this class. What the people have been up to: with both XML and KVP requests. Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.6 2006/06/12 13:32:29 bezema Changes to this class. What the people have been up to:
 * kvp is implemented Changes to this class.
 * What the people have been up to: Revision 1.5 2006/06/12 12:16:24 bezema Changes to this class.
 * What the people have been up to: Little rearanging of the GetSession classes, DoService should be
 * ready updating some errors Changes to
 * this class. What the people have been up to: Revision 1.4 2006/06/09 12:58:32 schmitz Changes to
 * this class. What the people have been up to: Set up some tests for WAS/WSS and the URN class.
 * Commented out some of the deegree param
 * stuff in order for the tests to run.
 * Tests have hardcoded URLs in them, so
 * they won't run just anywhere. Revision
 * 1.3 2006/05/30 08:42:18 bezema the password handler gets one instance of the WASSSecurityManager
 * 
 * Revision 1.2 2006/05/29 16:24:59 bezema Rearranging the layout of the wss and creating the
 * doservice classes. The WSService class is implemented as well
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.3 2006/05/26 14:38:32 schmitz Added some KVP constructors to WAS operations. Added
 * some comments, updated the plan. Restructured WAS operations by adding an AbstractRequest base
 * class.
 * 
 * Revision 1.2 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
 * 
 * Revision 1.1 2006/05/19 15:35:35 schmitz Updated the documentation, added the GetCapabilities
 * operation and implemented a rough WAService outline. Fixed some warnings.
 * 
 * 
 * 
 **************************************************************************************************/
