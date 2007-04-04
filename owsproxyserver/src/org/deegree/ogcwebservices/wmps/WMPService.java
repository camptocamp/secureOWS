// $Header: /cvsroot/deegree/src/org/deegree/ogcwebservices/wms/WMPService.java,v
// 1.3 2004/07/12 06:12:11 ap Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
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
 Aennchenstr. 19
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
package org.deegree.ogcwebservices.wmps;

import java.lang.reflect.Constructor;

import org.deegree.enterprise.Proxy;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.CurrentUpdateSequenceException;
import org.deegree.ogcwebservices.InvalidUpdateSequenceException;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wmps.configuration.WMPSConfiguration;
import org.deegree.ogcwebservices.wmps.configuration.WMPSDeegreeParams;
import org.deegree.ogcwebservices.wmps.operation.PrintMap;
import org.deegree.ogcwebservices.wmps.operation.WMPSGetCapabilities;
import org.deegree.ogcwebservices.wmps.operation.WMPSGetCapabilitiesResult;
import org.deegree.ogcwebservices.wmps.operation.WMPSProtocolFactory;

/**
 * Handles saving the PrintMap request to the databank and generating the initial response to be
 * sent to the user.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public class WMPService implements OGCWebService {

    private static final ILogger LOG = LoggerFactory.getLogger( WMPService.class );
    
    private static final TriggerProvider TP = TriggerProvider.create( WMPService.class );

    private PrintMapHandler printMapHandler;

    private WMPSConfiguration configuration;

    /**
     * Creates a new WMPService object.
     * 
     * @param configuration
     */
    public WMPService( WMPSConfiguration configuration ) {
        this.configuration = configuration;
        this.printMapHandler = new PrintMapHandler( configuration );
        Proxy proxy = this.configuration.getDeegreeParams().getProxy();
        if ( proxy != null ) {
            proxy.setProxy( true );
        }
    }

    /**
     * Return the OGCCapabilities.
     * 
     * @return OGCCapabilities
     */
    public OGCCapabilities getCapabilities() {
        return this.configuration;
    }

    /**
     * the method performs the handling of the passed OGCWebServiceEvent directly and returns the
     * result to the calling class/method
     * 
     * @param request
     *            request to perform
     * @return Object
     * @throws OGCWebServiceException
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {
        
        request = (OGCWebServiceRequest)TP.doPreTrigger( this, request )[0];

        Object result = null;
        if ( request instanceof PrintMap ) {

            String template = ( (PrintMap) request ).getTemplate();
            WMPSDeegreeParams params = this.configuration.getDeegreeParams();
            boolean isSynchronous = params.getSynchronousTemplates().contains( template );

            String handler = HandlerMapping.getString( "WMPService.PRINTMAP" );
            RequestManager rqManager = (RequestManager) createHandler( request, PrintMap.class,
                                                                       handler );

            try {
                rqManager.saveRequestToDB();

                result = rqManager.createInitialResponse( Messages.getMessage( "WMPS_INIT_RESPONSE" ) );
            } catch ( OGCWebServiceException e ) {

                throw new OGCWebServiceException( "Error saving the PrintMap request "
                                                  + "to the DB. " + e.getMessage() );
            }

            try {
                if ( isSynchronous ) {
                    result = this.printMapHandler.runSynchronous( (PrintMap) request );
                } else {
                    Thread printMap = new Thread( this.printMapHandler );
                    printMap.start();
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new OGCWebServiceException( "Error performing the PrintMap request. "
                                                  + e.getMessage() );
            }
        } else if ( request instanceof WMPSGetCapabilities ) {
            result = handleGetCapabilities( (WMPSGetCapabilities) request );
        }

        return TP.doPostTrigger( this, result )[0];
    }

    /**
     * creates a handler class for performing the incomming request. The instance that will be
     * created depends on the responsible class the for the submitted request in the WMPS
     * capabilities/configuration.
     * 
     * @param request
     *            request to be performed
     * @param requestClass
     *            of the request (GetStyles, WMSFeatureInfoRequest etc.)
     * @param className
     *            type of the operation to perform by the handler
     * @return Object
     * @throws OGCWebServiceException
     */
    private Object createHandler( OGCWebServiceRequest request, Class requestClass, String className )
                            throws OGCWebServiceException {

        // describes the signature of the required constructor
        Class[] cl = new Class[2];
        cl[0] = WMPSConfiguration.class;
        cl[1] = requestClass;

        // set parameter to submitt to the constructor
        Object[] o = new Object[2];
        o[0] = this.configuration;
        o[1] = request;

        Object handler = null;

        try {
            // get constructor
            Class creator = Class.forName( className );
            Constructor con = creator.getConstructor( cl );
            // call constructor and instantiate a new DataStore
            handler = con.newInstance( o );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "Couldn't instantiate " + className + '!' );
        }

        return handler;
    }

    /**
     * reads/creates the capabilities of the WMPS.
     * 
     * @param request
     *            capabilities request
     * @return WMPSGetCapabilitiesResult
     * @throws CurrentUpdateSequenceException
     * @throws InvalidUpdateSequenceException
     */
    private WMPSGetCapabilitiesResult handleGetCapabilities( WMPSGetCapabilities request )
                            throws CurrentUpdateSequenceException, InvalidUpdateSequenceException {

        String rUp = request.getUpdateSequence();
        String cUp = this.configuration.getUpdateSequence();

        if ( ( rUp != null ) && ( cUp != null ) && ( rUp.compareTo( cUp ) == 0 ) ) {
            throw new CurrentUpdateSequenceException( "request update sequence: " + rUp
                                                      + "is equal to capabilities"
                                                      + " update sequence " + cUp );
        }

        if ( ( rUp != null ) && ( cUp != null ) && ( rUp.compareTo( cUp ) > 0 ) ) {
            throw new InvalidUpdateSequenceException( "request update sequence: " + rUp
                                                      + " is higher then the "
                                                      + "capabilities update sequence " + cUp );
        }
        return WMPSProtocolFactory.createGetCapabilitiesResult( request, null, this.configuration );
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WMPService.java,v $
 * Changes to this class. What the people have been up to: Revision 1.26  2006/10/02 06:30:35  poth
 * Changes to this class. What the people have been up to: bug fixes
 * Changes to this class. What the people have been up to:
 * Revision 1.25  2006/09/13 07:37:58  deshmukh
 * removed excess debug statements.
 * Changes to this
 * class. What the people have been up to: Revision 1.24 2006/08/29 19:54:14 poth Changes to this
 * class. What the people have been up to: footer corrected Changes to this class. What the people
 * have been up to: Revision 1.23 2006/08/10
 * 07:11:35 deshmukh WMPS has been modified
 * to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people
 * have been up to: Revision 1.22 2006/07/31 11:21:07 deshmukh Changes to this class. What the
 * people have been up to: wmps implemention... Changes to this class. What the people have been up
 * to: Revision 1.21 2006/07/20 13:24:12
 * deshmukh Removed a few floating bugs.
 * Changes to this class. What the people
 * have been up to: Revision 1.20 2006/06/13 09:30:08 poth Changes to this class. What the people
 * have been up to: *** empty log message *** Changes to this class. What the people have been up
 * to: Revision 1.19 2006/06/12 09:34:53
 * deshmukh extended the print map
 * capabilites to support the get request and changed the db structure. Changes to this class. What
 * the people have been up to: Revision 1.18 2006/06/07 12:37:51 deshmukh Reset the changes made to
 * the WMPS.
 * 
 * Revision 1.16 2006/05/17 12:21:37 poth not required exceptions removed
 * 
 * Revision 1.15 2006/05/17 12:18:48 poth not required exceptions removed
 * 
 * Revision 1.14 2006/05/16 14:55:54 poth alternative synchronous performance of PrintMap requests
 * implemented
 * 
 * Revision 1.13 2006/05/16 06:43:12 poth ** empty log message ***
 * 
 * 
 **************************************************************************************************/
