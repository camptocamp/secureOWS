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
 Aennchenstraße 19  
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.sos.capabilities.SOSGetCapabilities;
import org.deegree.ogcwebservices.sos.configuration.SOSConfiguration;
import org.deegree.ogcwebservices.sos.configuration.SOSDeegreeParams;
import org.deegree.ogcwebservices.sos.describeplatform.DescribePlatformRequest;
import org.deegree.ogcwebservices.sos.describeplatform.DescribePlatformResult;
import org.deegree.ogcwebservices.sos.describeplatform.PlatformDescriptionDocument;
import org.deegree.ogcwebservices.sos.describeplatform.PlatformMetadata;
import org.deegree.ogcwebservices.sos.describesensor.DescribeSensorRequest;
import org.deegree.ogcwebservices.sos.describesensor.DescribeSensorResult;
import org.deegree.ogcwebservices.sos.describesensor.SensorDescriptionDocument;
import org.deegree.ogcwebservices.sos.describesensor.SensorMetadata;
import org.deegree.ogcwebservices.sos.getobservation.GetObservationDocument;
import org.deegree.ogcwebservices.sos.getobservation.GetObservationRequest;
import org.deegree.ogcwebservices.sos.getobservation.GetObservationResult;
import org.deegree.ogcwebservices.sos.om.ObservationArray;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class SOService implements OGCWebService {

    private SOSConfiguration serviceConfiguration;

    private static final ILogger LOG = LoggerFactory.getLogger( SOService.class );

    private static final TriggerProvider TP = TriggerProvider.create( SOService.class );

    /**
     * static create method to create a new instance of the SCService
     * 
     * @param scsConfiguration
     *  
     */
    public static SOService create( SOSConfiguration scsConfiguration ) {
        return new SOService( scsConfiguration );

    }

    /**
     * privater constructor to construct a new SCService instance with the
     * configuration as the only parameter
     * 
     * @param scsConfiguration
     *  
     */
    private SOService( SOSConfiguration scsConfiguration ) {
        this.serviceConfiguration = scsConfiguration;
    }

    /**
     * returns the ServiceTypeId from the ServiceIdentification section of the
     * configuration
     * 
     * @return
     */
    public String getServiceTypeId() {
        return this.serviceConfiguration.getServiceIdentification().getServiceType().getCode();

    }

    /**
     * returns the Version from the configuration
     * 
     * @return
     */
    public String getVersion() {
        return serviceConfiguration.getVersion();
    }

    /**
     * returns the serviceConfiguration
     * 
     * @return
     */
    public OGCCapabilities getCapabilities() {
        return this.serviceConfiguration;
    }

    /**
     * checks the request and do service
     * @throws OGCWebServiceException
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {

        request = (OGCWebServiceRequest) TP.doPreTrigger( this, request )[0];

        Object response = null;
        try {
            if ( request instanceof SOSGetCapabilities ) {
                LOG.logDebug( "-> GetCapabilities received" );
                response = this.getCapabilities();
            } else if ( request instanceof DescribePlatformRequest ) {
                LOG.logDebug( "-> DescribePlatform received" );
                response = this.describePlatform( (DescribePlatformRequest) request );
            } else if ( request instanceof DescribeSensorRequest ) {
                LOG.logDebug( "-> DescribeSensor received" );
                response = this.describeSensor( (DescribeSensorRequest) request );
            } else if ( request instanceof GetObservationRequest ) {
                LOG.logDebug( "-> GetObservation received" );
                response = this.getObservation( (GetObservationRequest) request );
            } else {
                throw new InvalidParameterValueException( "not a valid scs request" );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "SOS Failure\n" + e.getMessage() );
        }

        return TP.doPostTrigger( this, response )[0];
    }

    /**
     * returns a DescribePlatformResult
     * 
     * @param request
     * @return 
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     */
    private DescribePlatformResult describePlatform( DescribePlatformRequest request )
                            throws InvalidParameterValueException, OGCWebServiceException {

        //      validate the requested platforms
        String[] types = request.getTypeNames();

        for ( int i = 0; i < types.length; i++ ) {
            if ( serviceConfiguration.getSOSDeegreeParams().getPlatformConfiguration( types[i] ) == null ) {
                throw new OGCWebServiceException( "InvalidParameterValueException: Platform '"
                                                  + types[i] + "' not found" );
            }
        }

        PlatformDescriptionDocument pdd = new PlatformDescriptionDocument();
        PlatformMetadata[] pm = pdd.getPlatform( serviceConfiguration.getSOSDeegreeParams(), types );

        return new DescribePlatformResult( request, pm );
    }

    /**
     * returns a DescribeSensorResult
     * 
     * @param request
     * @return 
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     */
    private DescribeSensorResult describeSensor( DescribeSensorRequest request )
                            throws InvalidParameterValueException, OGCWebServiceException {

        //      validate the requested sensors
        String[] types = request.getTypeNames();

        for ( int i = 0; i < types.length; i++ ) {
            if ( serviceConfiguration.getSOSDeegreeParams().getSensorConfiguration( types[i] ) == null ) {
                throw new OGCWebServiceException( "InvalidParameterValueException: Sensor '"
                                                  + types[i] + "' not found" );
            }
        }

        SensorDescriptionDocument sdd = new SensorDescriptionDocument();
        SensorMetadata[] sensors = sdd.getSensor( serviceConfiguration.getSOSDeegreeParams(),
                                                  request.getTypeNames() );
        return new DescribeSensorResult( request, sensors );

    }

    /**
     * returns a GetObservationResult
     * 
     * @param request
     * @return 
     * @throws OGCWebServiceException
     * @throws XMLParsingException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    private GetObservationResult getObservation( GetObservationRequest request )
                            throws OGCWebServiceException, XMLParsingException, 
                            TransformerException, IOException, SAXException {

        SOSDeegreeParams dp = serviceConfiguration.getSOSDeegreeParams();

        //      validate the requested platforms
        String[] platforms = request.getPlatforms();
        for ( int i = 0; i < platforms.length; i++ ) {
            if ( dp.getPlatformConfiguration( platforms[i] ) == null ) {
                throw new OGCWebServiceException( "InvalidParameterValueException: Platform '"
                                                  + platforms[i] + "' not found" );
            }
        }

        //      validate the requested sensors
        String[] sensors = request.getSensors();
        for ( int i = 0; i < sensors.length; i++ ) {
            if ( dp.getSensorConfiguration( sensors[i] ) == null ) {
                throw new OGCWebServiceException( "InvalidParameterValueException: Sensor '"
                                                  + sensors[i] + "' not found" );
            }
        }

        GetObservationDocument god = new GetObservationDocument();
        ObservationArray[] obAr = god.getObservations( serviceConfiguration.getSOSDeegreeParams(),
                                                       request );
        return new GetObservationResult( request, obAr );
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: SOService.java,v $
 Revision 1.16  2006/10/18 17:00:56  poth
 made DefaultOGCWebServiceResponse base type for all webservice responses

 Revision 1.15  2006/10/01 11:15:43  poth
 trigger points for doService methods defined

 Revision 1.14  2006/08/24 06:42:16  poth
 File header corrected

 Revision 1.13  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */
