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
package org.deegree.ogcwebservices.sos.getobservation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.ComparisonOperation;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterConstructionException;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsBetweenOperation;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyIsNullOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.sos.WFSRequestGenerator;
import org.deegree.ogcwebservices.sos.WFSRequester;
import org.deegree.ogcwebservices.sos.XMLFactory;
import org.deegree.ogcwebservices.sos.XSLTransformer;
import org.deegree.ogcwebservices.sos.configuration.MeasurementConfiguration;
import org.deegree.ogcwebservices.sos.configuration.PlatformConfiguration;
import org.deegree.ogcwebservices.sos.configuration.SOSDeegreeParams;
import org.deegree.ogcwebservices.sos.configuration.SensorConfiguration;
import org.deegree.ogcwebservices.sos.configuration.SourceServerConfiguration;
import org.deegree.ogcwebservices.sos.om.Observation;
import org.deegree.ogcwebservices.sos.om.ObservationArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * get the observation data from the xsl transformed wfs requests
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 */

public class GetObservationDocument extends OGCDocument {

    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    private static final String XML_TEMPLATE = "GetObservationTemplate.xml";

    public void createEmptyDocument()
                            throws IOException, SAXException {
        URL url = GetObservationDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * gets the data from sensors
     * 
     * @param sensorIds
     * @param platformIds
     * @param deegreeParams
     * @return
     * @throws OGCWebServiceException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    public ObservationArray[] getObservations( SOSDeegreeParams deegreeParams,
                                              GetObservationRequest request )
                            throws OGCWebServiceException, XMLParsingException,
                            TransformerException, IOException, SAXException {

        Envelope bbox = request.getBBox();
        Object[] times = request.getTime();
        Query query = request.getQuery();
        String[] platforms = request.getPlatforms();
        String[] sensors = request.getSensors();

        ArrayList sensorList = null;

        if ( ( sensors.length < 1 ) && ( platforms.length < 1 ) ) {

            String[] platformsInBbox = 
                getPlatformsInBBoxFromServers( bbox, deegreeParams.getSourceServerConfigurations(),
                                               deegreeParams );

            LOG.logDebug( "## found " + platformsInBbox.length + " platforms in bbox" );

            String[] sensorsFromPlatforms = getSensorIdsFromPlatforms( platformsInBbox,
                                                                       deegreeParams );

            LOG.logDebug( "## found " + sensorsFromPlatforms.length + " sensors in bbox" );

            sensorList = new ArrayList( sensorsFromPlatforms.length );
            for ( int i = 0; i < sensorsFromPlatforms.length; i++ ) {
                if ( !sensorList.contains( sensorsFromPlatforms[i] ) ) {
                    sensorList.add( sensorsFromPlatforms[i] );
                }
            }
        } else {
            LOG.logDebug( "info: found sensors and/or platforms" );
            // checks the position in the bbox
            boolean contains = areSensorsInBBox( bbox, sensors, platforms, deegreeParams );

            if ( contains ) {
                sensorList = new ArrayList( 100 );
                for ( int i = 0; i < sensors.length; i++ ) {
                    if ( !sensorList.contains( sensors[i] ) ) {
                        sensorList.add( sensors[i] );
                    }
                }

                String[] sensorsFromPlatforms = getSensorIdsFromPlatforms( platforms, deegreeParams );
                for ( int i = 0; i < sensorsFromPlatforms.length; i++ ) {
                    if ( !sensorList.contains( sensorsFromPlatforms[i] ) ) {
                        sensorList.add( sensorsFromPlatforms[i] );
                    }
                }
            }
        }

        ArrayList observations = new ArrayList( sensorList.size() );

        for ( int i = 0; i < sensorList.size(); i++ ) {
            Document observationDocument = getObservationFromSensor( (String) sensorList.get( i ),
                                                                     deegreeParams, times, query );

            if ( observationDocument != null ) {
                ObservationArray observationArray = 
                    getObservationsFromDocuments( observationDocument, bbox,
                                                  (String) sensorList.get( i ) );
                observations.add( observationArray );
            }
        }

        LOG.logDebug( "-> have " + observations.size() + " observations collected" );

        return (ObservationArray[]) observations.toArray( new ObservationArray[observations.size()] );
    }

    /**
     * returns all platforms in the given bbox, but only from the given servers
     * 
     * @param bbox
     * @param servers
     * @param params
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     */
    private String[] getPlatformsInBBoxFromServers( Envelope bbox,
                                                   SourceServerConfiguration[] servers,
                                                   SOSDeegreeParams params )
                            throws OGCWebServiceException {

        ArrayList tPDResults = new ArrayList( servers.length );

        try {
            for ( int i = 0; i < servers.length; i++ ) {

                // only if server can provide platformmetadata
                if ( servers[i].havePlatformDescriptionData() ) {
                    Document request = 
                        WFSRequestGenerator.createBBoxWFSRequest( bbox,
                                                                  servers[i].getPlatformDescriptionFeatureType(),
                                                                  servers[i].getPlatformDescriptionCoordPropertyName() );

                    Document result = WFSRequester.sendWFSrequest( request,
                                                                   servers[i].getDataService() );

                    if ( result != null ) {
                        URL pdxs = servers[i].getPlatformDescriptionXSLTScriptSource();

                        Document doc = XSLTransformer.transformDocument( result, pdxs );
                        tPDResults.add( doc );
                    }
                }

            }
        } catch ( Exception e ) {
            LOG.logError( "could not access platforms in BBOX from DataService ", e );
            throw new OGCWebServiceException( this.getClass().getName(),
                                              "could not access platforms in BBOX from DataService " );
        }

        Document[] docs = (Document[]) tPDResults.toArray( new Document[tPDResults.size()] );

        return getPlatformIdsFromPlatformDocs( docs, params );

    }

    /**
     * returns the observationArray, with all observations, from a wfs result
     * 
     * @param docs
     * @param bbox
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     */
    private ObservationArray getObservationsFromDocuments( Document doc, Envelope bbox,
                                                          String sensorId )
                            throws XMLParsingException {

        List observations = XMLTools.getNodes( doc,
                                               "om:ObservationArray/om:observationMembers/gml:Observation",
                                               nsContext );

        if ( observations.size() < 1 ) {
            LOG.logDebug( "warning: no observations found in document" );
        }

        ArrayList observationsList = new ArrayList( observations.size() );

        for ( int i = 0; i < observations.size(); i++ ) {

            String timeStamp = XMLTools.getRequiredNodeAsString( (Node) observations.get( i ),
                                                                 "./gml:timeStamp/gml:TimeInstant/gml:timePosition",
                                                                 nsContext );

            String resultOf = XMLTools.getRequiredNodeAsString( (Node) observations.get( i ),
                                                                "./gml:resultOf/gml:QuantityList",
                                                                nsContext );

            observationsList.add( new Observation( timeStamp, resultOf ) );
        }

        LOG.logDebug( "-> ObservationArray created" );
        Observation[] obs = new Observation[observationsList.size()];
        obs = (Observation[]) observationsList.toArray( obs );
        return new ObservationArray( obs, bbox, sensorId );
    }

    /**
     * gets the observation from a sensor, by requesting the correct wfs
     * 
     * @param sensor
     * @param params
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     */
    private Document getObservationFromSensor( String sensor, SOSDeegreeParams params,
                                              Object[] times, Query query )
                            throws OGCWebServiceException {

        try {
            // query
            MeasurementConfiguration measureConfig = null;
            Operation filterOperation = null;

            if ( query != null ) {
                String queryFeature = query.getFeature();
                measureConfig = 
                    params.getSensorConfiguration( sensor ).getMeasurementById(  queryFeature );
                if ( measureConfig == null ) {
                    throw new Exception( "warning: sensor not support the requested "
                                         + "observationFeature!" );
                }

                // gets the filter operations
                if ( query.getFilter() != null ) {
                    LOG.logDebug( "-> QueryFilter found" );
                    Operation op = query.getFilter().getOperation();
                    filterOperation = modifyOperation( op, measureConfig );
                }
            } else {
                // TODO
                // I think this is a mistake and instead all measurements of a
                // sensor has to requested from the WFS
                LOG.logDebug( "warning: no query given, will use the default from sensor" );
                measureConfig = params.getSensorConfiguration( sensor ).getFirstMeasurementConfiguration();
                Filter filter = measureConfig.getConstraint();
                if ( filter != null ) {
                    filterOperation = ( (ComplexFilter) filter ).getOperation();
                }
            }

            Document request = 
                WFSRequestGenerator.createObservationWFSRequest( times,
                                                                 measureConfig.getFeatureTypeName(),
                                                                 measureConfig.getTimePropertyName(),
                                                                 filterOperation );

            SensorConfiguration sc = params.getSensorConfiguration( sensor );
            String ssID = sc.getFirstMeasurementConfiguration().getSourceServerId();
            OGCWebService ows = params.getSourceServerConfiguration( ssID ).getDataService();

            Document result = WFSRequester.sendWFSrequest( request, ows );

            return XSLTransformer.transformDocument( result, measureConfig.getXSLTScriptSource() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( this.getClass().getName(),
                                              "could not access observations from sensor" );
        }

    }

    /**
     * gets all sensorIds from the given platforms
     * 
     * @param platforms
     * @param params
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     */
    private String[] getSensorIdsFromPlatforms( String[] platforms, SOSDeegreeParams params )
                            throws OGCWebServiceException {

        // gets all servers which have to request
        ArrayList transformedResultDocs = null;
        try {
            Map servers = new HashMap( platforms.length );

            for ( int t = 0; t < platforms.length; t++ ) {
                String sourceServerId = params.getPlatformConfiguration( platforms[t] ).getSourceServerId();

                // server schon in liste; nur platform hinzuf�gen
                if ( servers.containsKey( sourceServerId ) ) {
                    ( (ArrayList) servers.get( sourceServerId ) ).add( platforms[t] );
                }
                // server nicht in liste; server hinzuf�gen und platform hinzuf�gen
                else {
                    ArrayList temp = new ArrayList( 10 );
                    temp.add( platforms[t] );
                    servers.put( sourceServerId, temp );
                }
            }

            transformedResultDocs = new ArrayList( servers.keySet().size() );

            Iterator iter = servers.keySet().iterator();
            while ( iter.hasNext() ) {
                String key = (String) iter.next();
                List list = (List) servers.get( key );

                String[] idProps = new String[list.size()];
                for ( int a = 0; a < list.size(); a++ ) {
                    idProps[a] = params.getPlatformConfiguration( (String) list.get( a ) ).getIdPropertyValue();
                }

                QualifiedName pdft = params.getSourceServerConfiguration( key ).getPlatformDescriptionFeatureType();
                QualifiedName pdid = params.getSourceServerConfiguration( key ).getPlatformDescriptionIdPropertyName();
                Document request = WFSRequestGenerator.createIsLikeOperationWFSRequest( idProps,
                                                                                        pdft, pdid );

                OGCWebService ows = params.getSourceServerConfiguration( key ).getDataService();
                Document result = WFSRequester.sendWFSrequest( request, ows );

                if ( result != null ) {
                    SourceServerConfiguration ssc = params.getSourceServerConfiguration( key );
                    URL url = ssc.getPlatformDescriptionXSLTScriptSource();
                    transformedResultDocs.add( XSLTransformer.transformDocument( result, url ) );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( this.getClass().getName(),
                                              "could not access sensorsIDs from platforms " );
        }

        Document[] docs = new Document[transformedResultDocs.size()];
        docs = (Document[]) transformedResultDocs.toArray( docs );
        return getSensorIdsFromCarriesInPlatformDoc( docs, params );
    }

    /**
     * returns true if all Sensor and/or Platforms inside the given BBox
     * 
     * 
     * @param bbox
     *            null is not allowed
     * @param sensorIds
     * @param platformIds
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     * @throws OGCWebServiceException
     */
    private boolean areSensorsInBBox( Envelope bbox, String[] sensorIds, String[] platformIds,
                                     SOSDeegreeParams deegreeParams )
                            throws OGCWebServiceException, XMLParsingException,
                            TransformerException, IOException, SAXException {
        if ( bbox == null ) {
            throw new NullPointerException( "bbox must be set" );
        }

        // sensor and platforms
        if ( ( sensorIds != null ) && ( platformIds != null ) ) {
            if ( ( areSensorsInBBox( sensorIds, bbox, deegreeParams ) )
                 && ( arePlatformsInBBox( platformIds, bbox, deegreeParams ) ) ) {
                return true;
            }
        } else if ( sensorIds != null && areSensorsInBBox( sensorIds, bbox, deegreeParams ) ) {
            // only sensors
            return true;
        } else if ( platformIds != null && arePlatformsInBBox( platformIds, bbox, deegreeParams ) ) {
            // only platforms
            return true;
        }

        return false;
    }

    /**
     * returns true, if the given sensors in bbox
     * 
     * @param sensors
     * @param bbox
     * @param deegreeParams
     * @return
     * @throws OGCWebServiceException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     */
    private boolean areSensorsInBBox( String[] sensors, Envelope bbox,
                                     SOSDeegreeParams deegreeParams )
                            throws OGCWebServiceException, XMLParsingException,
                            TransformerException, IOException, SAXException {

        // gets all servers which have to request
        Map servers = new HashMap( sensors.length );

        for ( int t = 0; t < sensors.length; t++ ) {
            String sourceServerId = deegreeParams.getSensorConfiguration( sensors[t] ).getSourceServerId();

            if ( servers.containsKey( sourceServerId ) ) {
                ( (ArrayList) servers.get( sourceServerId ) ).add( sensors[t] );
            } else {
                ArrayList temp = new ArrayList();
                temp.add( sensors[t] );
                servers.put( sourceServerId, temp );
            }

        }

        String[] keySet = (String[]) servers.keySet().toArray( new String[servers.keySet().size()] );
        ArrayList transformedWFSResults = new ArrayList( keySet.length );

        // process all servers in the hashTable
        for ( int i = 0; i < keySet.length; i++ ) {

            Document result = getSensors( deegreeParams, servers, keySet, i );

            // if the result is not null, transform it and add it to the result
            // document list
            if ( result != null ) {
                SourceServerConfiguration ssc = deegreeParams.getSourceServerConfiguration( keySet[i] );
                URL url = ssc.getSensorDescriptionXSLTScriptSource();
                Document doc = XSLTransformer.transformDocument( result, url );
                transformedWFSResults.add( doc );
            }

        }

        Document[] docs = new Document[transformedWFSResults.size()];
        docs = (Document[]) transformedWFSResults.toArray( docs );
        String[] platformList = getPlatformIdsFromAttachedToInSensorDocs( docs, deegreeParams );

        if ( arePlatformsInBBox( platformList, bbox, deegreeParams ) ) {
            return true;
        }
        return false;
    }

    /**
     * @param deegreeParams
     * @param servers
     * @param keySet
     * @param i
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     */
    private Document getSensors( SOSDeegreeParams deegreeParams, Map servers, String[] keySet, int i )
                            throws OGCWebServiceException {
        Document result = null;
        try {
            String[] tmp = new String[( (ArrayList) servers.get( keySet[i] ) ).size()];
            ArrayList al = (ArrayList) servers.get( keySet[i] );
            String[] sensorIds = (String[]) al.toArray( tmp );

            String[] sensorIdPropertyValues = new String[sensorIds.length];

            for ( int x = 0; x < sensorIds.length; x++ ) {
                sensorIdPropertyValues[i] = deegreeParams.getSensorConfiguration( sensorIds[x] ).getIdPropertyValue();
            }

            SourceServerConfiguration ssc = deegreeParams.getSourceServerConfiguration( keySet[i] );
            QualifiedName sdft = ssc.getSensorDescriptionFeatureType();
            QualifiedName sdpn = ssc.getSensorDescriptionIdPropertyName();
            // generates the request for the current wfs
            Document request = WFSRequestGenerator.createIsLikeOperationWFSRequest(
                                                                                    sensorIdPropertyValues,
                                                                                    sdft, sdpn );
            // sends the request to the current wfs
            result = WFSRequester.sendWFSrequest( request, ssc.getDataService() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( this.getClass().getName(), "could not evaluate " + 
                                              "if platform is contained in BBOX" );
        }
        return result;
    }

    /**
     * checks if the platforms inside the bbox
     * 
     * @param platformIds
     * @param bbox
     * @param configs
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     * @throws OGCWebServiceException
     */
    private boolean arePlatformsInBBox( String[] platformIds, Envelope bbox,
                                       SOSDeegreeParams deegreeParams )
                            throws OGCWebServiceException {

        ArrayList serverConfigsList = new ArrayList();

        // gets all platforms in bbox, only from neccessary servers
        for ( int i = 0; i < platformIds.length; i++ ) {
            String id = deegreeParams.getPlatformConfiguration( platformIds[i] ).getSourceServerId();
            SourceServerConfiguration ssc = deegreeParams.getSourceServerConfiguration( id );
            if ( !serverConfigsList.contains( ssc ) ) {
                serverConfigsList.add( ssc );
            }
        }

        SourceServerConfiguration[] ssc = new SourceServerConfiguration[serverConfigsList.size()];
        ssc = (SourceServerConfiguration[]) serverConfigsList.toArray( ssc );
        String[] platformsInBBox = getPlatformsInBBoxFromServers( bbox, ssc, deegreeParams );

        // compares platforms in bbox with the given platforms
        for ( int i = 0; i < platformIds.length; i++ ) {
            boolean found = false;

            for ( int a = 0; a < platformsInBBox.length; a++ ) {

                if ( platformIds[i].equals( platformsInBBox[a] ) ) {

                    found = true;
                }
            }
            if ( !found ) {
                throw new OGCWebServiceException( "Sensor or Platform not in given bbox" );
            }
        }

        return true;
    }

    /**
     * gets all platformIds from attachedTo parts in SensorDescriptionDocuments
     * 
     * @param doc
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     */
    private String[] getPlatformIdsFromAttachedToInSensorDocs( Document[] docs,
                                                              SOSDeegreeParams deegreeParams )
                            throws XMLParsingException {

        ArrayList platformIdList = new ArrayList();

        for ( int a = 0; a < docs.length; a++ ) {

            List nl = XMLTools.getNodes( docs[a], "sml:Sensors/sml:Sensor", nsContext );

            for ( int i = 0; i < nl.size(); i++ ) {
                String platformIdPropertyValue = 
                    XMLTools.getNodeAsString( (Node) nl.get( i ),
                                              "sml:attachedTo/sml:Component/text()",
                                              nsContext, null );

                PlatformConfiguration pc = deegreeParams.getPlatformConfigurationByIdPropertyValue( platformIdPropertyValue );

                String platformId = null;
                if ( pc != null ) {
                    platformId = pc.getId();
                }

                if ( platformId != null ) {
                    platformIdList.add( platformId );
                }
            }
        }

        return ( (String[]) platformIdList.toArray( new String[platformIdList.size()] ) );
    }

    /**
     * gets the platformIds from all PlatformDescriptionDocuments
     * 
     * @param doc
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws SAXException
     * @throws IOException
     */
    private String[] getPlatformIdsFromPlatformDocs( Document[] docs, SOSDeegreeParams deegreeParams )
                            throws OGCWebServiceException {

        if ( docs == null ) {
            throw new NullPointerException( "null not allowed" );
        }

        ArrayList platformIdList;
        try {

            platformIdList = new ArrayList( docs.length );
            for ( int a = 0; a < docs.length; a++ ) {

                List itemsList = XMLTools.getNodes( docs[a], "sml:Platforms/sml:Platform",
                                                    nsContext );
                for ( int i = 0; i < itemsList.size(); i++ ) {
                    String platformIdPropertyValue = 
                        XMLTools.getRequiredNodeAsString( (Node) itemsList.get( i ), "@id",
                                                          nsContext );

                    PlatformConfiguration pc = deegreeParams.getPlatformConfigurationByIdPropertyValue( platformIdPropertyValue );

                    if ( pc != null ) {
                        platformIdList.add( pc.getId() );
                    }
                }

            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( this.getClass().getName(), "could not access " + 
                                              "platform IDs" );
        }

        return (String[]) platformIdList.toArray( new String[platformIdList.size()] );

    }

    /**
     * gets all sensorSCSId's from the given platformDescription Document
     * 
     * @param doc
     * @param deegreeParams
     * @return
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     */
    private String[] getSensorIdsFromCarriesInPlatformDoc( Document[] docs,
                                                          SOSDeegreeParams deegreeParams )
                            throws OGCWebServiceException {

        try {
            ArrayList sensorIdList = new ArrayList( 100 );

            for ( int x = 0; x < docs.length; x++ ) {

                List platformNodeList = XMLTools.getNodes( docs[x], "sml:Platforms/sml:Platform",
                                                           nsContext );

                for ( int i = 0; i < platformNodeList.size(); i++ ) {
                    List carriesNodeList = XMLTools.getNodes( (Node) platformNodeList.get( i ),
                                                              "sml:carries", nsContext );

                    for ( int a = 0; a < carriesNodeList.size(); a++ ) {
                        String sensorIdPropertyValue = 
                            XMLTools.getRequiredNodeAsString( (Node) carriesNodeList.get( a ),
                                                              "sml:Asset/text()", nsContext );

                        if ( deegreeParams.getSensorConfigurationByIdPropertyValue( sensorIdPropertyValue ) != null ) {
                            String sensorId = 
                                deegreeParams.getSensorConfigurationByIdPropertyValue( sensorIdPropertyValue ).getId();

                            if ( sensorId != null ) {
                                sensorIdList.add( sensorId );
                            }
                        }
                    }
                }
            }
            return ( (String[]) sensorIdList.toArray( new String[sensorIdList.size()] ) );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException(  this.getClass().getName(),
                                              "could not access sensor IDs carres in platform " );
        }
    }

    /**
     * returns the modified operation; modify the scs query to create the correct wfs querry
     * 
     * @param operation
     * @param measuresConfig
     * @return
     * @throws FilterConstructionException
     */
    private Operation modifyOperation( Operation operation, MeasurementConfiguration measureConfig )
                            throws FilterConstructionException {

        Operation op = null;
        if ( operation instanceof LogicalOperation ) {
            LOG.logDebug( "logical operation found" );
            List modOps = new ArrayList();
            List<Operation> ops = ( (LogicalOperation) operation ).getArguments();
            for ( int i = 0; i < ops.size(); i++ ) {
                modOps.add( modifyOperation( ops.get( i ), measureConfig ) );
            }
            op = new LogicalOperation( ( (LogicalOperation) operation ).getOperatorId(), modOps );
        } else if ( operation instanceof ComparisonOperation ) {
            LOG.logDebug( "comparison operation found" );
            op = ( getModifiedComparisonOperation( (ComparisonOperation) operation, measureConfig ) );
        } else if ( operation instanceof SpatialOperation ) {
            LOG.logDebug( "spatial operation not supported!" );
        }

        Filter filter = measureConfig.getConstraint();
        if ( filter != null ) {
            // adds a predefined constraint taken from the measurements
            // configuration
            Operation constraint = ( (ComplexFilter) filter ).getOperation();
            if ( op != null ) {
                ArrayList ar = new ArrayList( 2 );
                ar.add( constraint );
                ar.add( op );
                op = new LogicalOperation( OperationDefines.AND, ar );
            } else {
                op = constraint;
            }
        }

        return op;
    }

    /**
     * returns the modified ComparisonOperation
     * 
     * @param compOp
     * @param measuresConfig
     * @return
     * @throws FilterConstructionException
     */
    private Operation getModifiedComparisonOperation( ComparisonOperation compOp,
                                                     MeasurementConfiguration measuresConfig ) {

        if ( compOp instanceof PropertyIsBetweenOperation ) {
            LOG.logDebug( "PropertyIsBetweenOperation found" );
            // FIXME if function is supported by your wfs; use it! instead of
            // this
            ArrayList temp = new ArrayList();
            // set the lower boundary
            temp.add( new PropertyIsCOMPOperation( OperationDefines.PROPERTYISGREATERTHAN,
                                                   new PropertyName(  measuresConfig.getMeasurandPropertyName() ),
                                                   ( (PropertyIsBetweenOperation) compOp ).getLowerBoundary() ) );
            // set the upper boundary
            temp.add( new PropertyIsCOMPOperation( OperationDefines.PROPERTYISLESSTHAN,
                                                   new PropertyName( measuresConfig.getMeasurandPropertyName() ),
                                                   ( (PropertyIsBetweenOperation) compOp ).getUpperBoundary() ) );
            return new LogicalOperation( OperationDefines.AND, temp );

        } else if ( compOp instanceof PropertyIsCOMPOperation ) {
            LOG.logDebug( "PropertyIsCompOperation found" );
            // returns a PropertyIsCOMPOperation with modified PropertyName
            return new PropertyIsCOMPOperation( compOp.getOperatorId(),
                                                new PropertyName( measuresConfig.getMeasurandPropertyName() ),
                                                ( (PropertyIsCOMPOperation) compOp ).getSecondExpression() );

        } else if ( compOp instanceof PropertyIsLikeOperation ) {
            LOG.logDebug( "PropertyIsLike found" );
            // returns a PropertyIsLikeOperation with modified PropertyName
            return new PropertyIsLikeOperation( new PropertyName( measuresConfig.getMeasurandPropertyName() ),
                                                ( (PropertyIsLikeOperation) compOp ).getLiteral(),
                                                ( (PropertyIsLikeOperation) compOp ).getWildCard(),
                                                ( (PropertyIsLikeOperation) compOp ).getSingleChar(),
                                                ( (PropertyIsLikeOperation) compOp ).getEscapeChar() );

        } else if ( compOp instanceof PropertyIsNullOperation ) {
            LOG.logDebug( "PropertyIsNull is not supported!" );
        }

        return null;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetObservationDocument.java,v $
 Revision 1.22  2006/09/28 07:49:46  poth
 Generics introduced for Filter - LogicalOperation and required adaptions

 Revision 1.21  2006/08/24 06:42:16  poth
 File header corrected

 Revision 1.20  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */
