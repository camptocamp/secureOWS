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
package org.deegree.ogcwebservices.sos.configuration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.xml.parsers.ParserConfigurationException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.metadata.iso19115.Linkage;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.sos.capabilities.CapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.RemoteWFService;
import org.deegree.ogcwebservices.wfs.WFServiceFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Reads the SOSConfiguration from a XML File
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class SOSConfigurationDocument extends CapabilitiesDocument {

    private static final String XML_TEMPLATE = "SOSConfigurationTemplate.xml";

    protected static final URI DEEGREE_SOS = CommonNamespaces.DEEGREESOS;

    private static final ILogger LOG = LoggerFactory.getLogger( SOSConfigurationDocument.class );

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * creates a empty document from template File
     */
    public void createEmptyDocument() throws IOException, SAXException {
        URL url = SOSConfigurationDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '"
                + XML_TEMPLATE + " could not be found." );
        }
        this.load( url );
    }

    /**
     * returns a class representation of the document
     * 
     * @return
     * @throws InvalidConfigurationException
     */
    public SOSConfiguration getConfiguration() throws InvalidConfigurationException {

        try {
            return new SOSConfiguration( getSOSDeegreeParams(), getSensorList(), getPlatformList(),
                getOperationsMetadata(), getServiceProvider(), getServiceIdentification(),
                parseUpdateSequence(), parseVersion(), null );

        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new InvalidConfigurationException(
                "Class representation of the SOS configuration "
                    + "document could not be generated: " + e.getMessage() );
        }

    }

    /**
     * reads the scs deegree params from xml file
     * 
     * @return
     * @throws InvalidConfigurationException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws URISyntaxException
     * @throws IOException
     */
    private SOSDeegreeParams getSOSDeegreeParams()
        throws ParserConfigurationException,
            XMLParsingException,
            IOException {

        Document doc = this.getRootElement().getOwnerDocument();

        try {
            if ( doc != null ) {

                String defaultOnlineResourceHref = XMLTools.getRequiredNodeAsString( doc,
                    "sos:SCS_Capabilities/deegreesos:"
                        + "deegreeParams/deegreesos:DefaultOnlineResource/@xlink:href", nsContext );

                String defaultOnlineResourceType = XMLTools.getRequiredNodeAsString( doc,
                    "sos:SCS_Capabilities/deegreesos:"
                        + "deegreeParams/deegreesos:DefaultOnlineResource/@xlink:type", nsContext );
                int cacheSize = XMLTools.getRequiredNodeAsInt( doc,
                    "sos:SCS_Capabilities/deegreesos:"
                        + "deegreeParams/deegreesos:CacheSize", nsContext );

                int requestTimeLimit = XMLTools.getRequiredNodeAsInt( doc,
                    "sos:SCS_Capabilities/deegreesos:deegreeParams/"
                        + "deegreesos:RequestTimeLimit", nsContext );

                String characterSet = XMLTools.getRequiredNodeAsString( doc,
                    "sos:SCS_Capabilities/deegreesos:"
                        + "deegreeParams/deegreesos:Encoding", nsContext );

                int sourceServerTimeLimit = XMLTools.getRequiredNodeAsInt( doc,
                    "sos:SCS_Capabilities/deegreesos:"
                        + "deegreeParams/deegreesos:SourceServerTimeLimit", nsContext );

                // gets the sourceServer configs
                SourceServerConfiguration[] sourceServers = getSourceServerConfigs( doc );
                LOG.logDebug( "found "
                    + sourceServers.length + " Servers" );

                // gets the platform configs
                PlatformConfiguration[] platforms = getPlatformConfigs( doc, sourceServers );
                LOG.logDebug( "found "
                    + platforms.length + " Platforms" );

                // gets the sensor configs
                SensorConfiguration[] sensors = getSensorConfigs( doc, sourceServers );
                LOG.logDebug( "found "
                    + sensors.length + " Sensors" );

                URL url = new URL( defaultOnlineResourceHref );
                Linkage linkage = new Linkage( url, defaultOnlineResourceType );
                return new SOSDeegreeParams( new OnlineResource( linkage ), cacheSize,
                    requestTimeLimit, characterSet, sourceServerTimeLimit, sensors, platforms,
                    sourceServers );

            }

            else {
                throw new IOException( "can't access to the configuration document" );
            }
        } catch (Exception e) {
            throw new XMLParsingException( "could not parse SOS configuration DeegreeParam "
                + "section", e );
        }

    }

    /**
     * reads all sensor configs from xml file
     * 
     * @return
     * @throws XMLParsingException
     * @throws MalformedURLException
     * @throws XMLParsingException
     * @throws URISyntaxException
     * @throws ConfigurationException
     */
    private SensorConfiguration[] getSensorConfigs( Document doc,
                                                   SourceServerConfiguration[] sourceServers )
        throws MalformedURLException,
            XMLParsingException,
            URISyntaxException {

        ArrayList sensors = new ArrayList();

        List parts = XMLTools.getNodes( doc, "sos:SCS_Capabilities/sos:SensorList/sos:Sensor",
                                        nsContext );

        for (int i = 0; i < parts.size(); i++) {

            String id = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ), 
                                                          "@Id", nsContext );

            String idPropertyValue = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "deegreesos:DescriptionSource/deegreesos:IdPropertyValue/text()", nsContext );

            String sourceServerId = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "deegreesos:DescriptionSource/deegreesos:SourceServerID/text()", nsContext );

            List measurements = XMLTools.getNodes( (Node) parts.get( i ),
                "deegreesos:MeasurementList/deegreesos:Measurement", nsContext );

            SensorConfiguration temp = new SensorConfiguration( id, idPropertyValue,
                sourceServerId, getMeasurementConfigs( measurements ) );

            if ( sensors.contains( temp ) ) {
                LOG.logWarning( "Sensor id's have to be unique! Sensor not added!" );
            } else {
                for (int a = 0; a < sourceServers.length; a++) {
                    if ( ( (SourceServerConfiguration) sourceServers[a] ).getId().equals(
                        sourceServerId ) ) {
                        if ( ( (SourceServerConfiguration) sourceServers[a] )
                            .haveSensorDescriptionData() ) {
                            LOG.logDebug( "-> found Sensor on "
                                + ( (SourceServerConfiguration) sourceServers[a] ).getId() );
                            ( (SourceServerConfiguration) sourceServers[a] ).addSensor( temp );
                            sensors.add( temp );
                        } else {
                            LOG.logWarning( "Server can't support DescribeSensor! " +
                                            "Sensor not added!" );
                        }
                    }
                }
            }
        }

        return ( (SensorConfiguration[]) sensors.toArray( new SensorConfiguration[sensors.size()] ) );
    }

    /**
     * reads the measurements from a sensor
     * 
     * @param node
     * 
     * @return
     * @throws XMLParsingException
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private MeasurementConfiguration[] getMeasurementConfigs( List parts )
        throws XMLParsingException,
            MalformedURLException,
            URISyntaxException {

        ArrayList measurements = new ArrayList();

        if ( ( parts == null )
            || ( parts.size() < 1 ) ) {
            LOG.logWarning( "Sensor must have at least one Measures" );
        }

        for (int i = 0; i < parts.size(); i++) {

            String id = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ), 
                                                         "@id", nsContext );

            String sourceServerId = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "deegreesos:SourceServerID/text()", nsContext );

            String phenomenon = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "@phenomenon", nsContext );

            QualifiedName featureTypeName = XMLTools.getRequiredNodeAsQualifiedName( (Node) parts.get( i ),
                "deegreesos:FeatureTypeName/text()", nsContext );

            Node constraint = XMLTools.getNode( (Node) parts.get( i ),
                "deegreesos:Constraint/ogc:Filter", nsContext );
            Filter filter = null;
            if ( constraint != null ) {
                filter = AbstractFilter.buildFromDOM( (Element) constraint );
            }

            QualifiedName timePropertyName = 
                XMLTools.getRequiredNodeAsQualifiedName((Node) parts.get( i ),
                "deegreesos:TimePropertyName/text()", nsContext );

            QualifiedName measurandPropertyName = XMLTools.getRequiredNodeAsQualifiedName( (Node) parts.get( i ),
                "deegreesos:MeasurandPropertyName/text()", nsContext );

            String timeResolution = XMLTools.getNodeAsString( (Node) parts.get( i ),
                "deegreesos:TimeResolution/text()", nsContext, null );

            String timeResolutionType = null;

            if ( timeResolution != null ) {
                timeResolutionType = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                    "deegreesos:TimeResolution/@type", nsContext );
            }

            URL xsltSource = resolve( XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "deegreesos:XSLTSource/text()", nsContext ) );

            MeasurementConfiguration temp = new MeasurementConfiguration( id, sourceServerId,
                phenomenon, featureTypeName, filter, timePropertyName, measurandPropertyName,
                timeResolution, timeResolutionType, xsltSource );

            if ( measurements.contains( temp ) ) {
                LOG.logWarning( "measurement id's have to be unique! Measurement not added!" );
            } else {
                measurements.add( temp );
            }

        }

        MeasurementConfiguration[] tmp = new MeasurementConfiguration[measurements.size()];
        return ( (MeasurementConfiguration[]) measurements.toArray( tmp ) );
    }

    /**
     * reads all platform configs from xml file
     * 
     * @param doc
     * 
     * @throws MalformedURLException
     * @throws XMLParsingException
     */
    private PlatformConfiguration[] getPlatformConfigs( Document doc,
                                                       SourceServerConfiguration[] sourceServers )
        throws MalformedURLException,
            XMLParsingException {

        ArrayList platforms = new ArrayList();

        List parts = XMLTools.getNodes( doc, "sos:SCS_Capabilities/sos:PlatformList/sos:Platform",
            nsContext );

        for (int i = 0; i < parts.size(); i++) {

            String id = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ), "@Id", nsContext );

            String idPropertyValue = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "deegreesos:DescriptionSource/deegreesos:IdPropertyValue/text()", nsContext );

            String sourceServerId = XMLTools.getRequiredNodeAsString( (Node) parts.get( i ),
                "deegreesos:DescriptionSource/deegreesos:SourceServerID/text()", nsContext );

            PlatformConfiguration temp = new PlatformConfiguration( id, idPropertyValue,
                sourceServerId );

            if ( platforms.contains( temp ) ) {
                LOG.logWarning( "Platfom id's have to be unique! Platform not added!" );
            } else {
                for (int a = 0; a < sourceServers.length; a++) {
                    SourceServerConfiguration sso = (SourceServerConfiguration) sourceServers[a];
                    if ( sso.getId().equals( sourceServerId ) ) {
                        if ( sso.havePlatformDescriptionData() ) {
                            LOG.logDebug( "-> found Platform on "
                                + sso.getId() );
                            sso.addPlatform( temp );
                            platforms.add( temp );
                        } else {
                            LOG.logWarning( "Server can't support DescribePlatform! " +
                                    "Platform not added!" );
                        }
                    }
                }
            }
        }
        PlatformConfiguration[] tmp = new PlatformConfiguration[platforms.size()];
        return (PlatformConfiguration[]) platforms.toArray( tmp );

    }

    /**
     * reads all source server configs from xml file
     * 
     * @param node
     * 
     * @throws XMLParsingException
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private SourceServerConfiguration[] getSourceServerConfigs( Node node )
        throws XMLParsingException,
            MalformedURLException,
            URISyntaxException,
            OGCWebServiceException,
            IOException,
            SAXException,
            InvalidConfigurationException {

        ArrayList sourceServers = new ArrayList();

        List sourceServerList = XMLTools.getNodes( node, "sos:SCS_Capabilities/"
            + "deegreesos:deegreeParams/deegreesos:SourceServerList/deegreesos:SourceServer",
            nsContext );

        for (int i = 0; i < sourceServerList.size(); i++) {

            String id = XMLTools.getRequiredNodeAsString( (Node) sourceServerList.get( i ), 
                                                           "@id", nsContext );

            String service = XMLTools.getRequiredNodeAsString( (Node) sourceServerList.get( i ),
                                                               "@service", nsContext );

            String version = XMLTools.getRequiredNodeAsString( (Node) sourceServerList.get( i ),
                                                                "@version", nsContext );

            Node olNode = XMLTools.getRequiredNode( (Node) sourceServerList.get( i ),
                                                    "ogc:OnlineResource", nsContext );
            OGCWebService ows = getDataServiceCapaOnlineResource( olNode );

            // gets the optional platformDescription config
            List platformDescription = XMLTools.getNodes( (Node) sourceServerList.get( i ),
                "deegreesos:PlatformDescription", nsContext );

            if ( platformDescription.size() < 1 ) {
                throw new XMLParsingException( "no platformDescription Config added "
                    + "for server '" + id + "'" );
            }

            QualifiedName platformDescriptionFeatureType = XMLTools.getRequiredNodeAsQualifiedName(
                (Node) platformDescription.get( 0 ), "deegreesos:FeatureType/text()", nsContext );
            QualifiedName platformDescriptionIdPropertyName = XMLTools.getRequiredNodeAsQualifiedName(
                (Node) platformDescription.get( 0 ), "deegreesos:IdPropertyName/text()", nsContext );
            QualifiedName platformDescriptionCoordPropertyName = XMLTools.getRequiredNodeAsQualifiedName(
                (Node) platformDescription.get( 0 ), "deegreesos:CoordPropertyName/text()", nsContext );
            String platformDescriptionXSLTSource = XMLTools.getRequiredNodeAsString(
                (Node) platformDescription.get( 0 ), "deegreesos:XSLTSource/text()", nsContext );

            // gets the optional SensorDescription config
            List sensorDescription = XMLTools.getNodes( (Node) sourceServerList.get( i ),
                "deegreesos:SensorDescription", nsContext );

            if ( sensorDescription.size() < 1 ) {
                new XMLParsingException( "info: no sensorDescription Config added for "
                    + "server '" + id + "'" );
            }

            QualifiedName sensorDescriptionFeatureType = 
                XMLTools.getRequiredNodeAsQualifiedName( (Node) sensorDescription.get( 0 ), 
                                              "deegreesos:FeatureType/text()", nsContext );
            
            QualifiedName sensorDescriptionIdPropertyName = 
                XMLTools.getRequiredNodeAsQualifiedName( (Node) sensorDescription.get( 0 ), 
                                              "deegreesos:IdPropertyName/text()", nsContext );
            String sensorDescriptionXSLTSource = 
                XMLTools.getRequiredNodeAsString( (Node) sensorDescription.get( 0 ),
                                                "deegreesos:XSLTSource/text()", nsContext );

            URL platformDescriptionXSLTSourceURL = resolve( platformDescriptionXSLTSource );
            URL sensorDescriptionXSLTSourceURL = resolve( sensorDescriptionXSLTSource );

            SourceServerConfiguration config = new SourceServerConfiguration( id, service, version,
                ows, platformDescriptionFeatureType, platformDescriptionIdPropertyName,
                platformDescriptionCoordPropertyName, platformDescriptionXSLTSourceURL,
                sensorDescriptionFeatureType, sensorDescriptionIdPropertyName,
                sensorDescriptionXSLTSourceURL );

            if ( sourceServers.contains( config ) ) {
                LOG.logWarning( "SourceServer with id '"
                    + id + "' exists! It won't be added again!" );

            } else {
                sourceServers.add( config );
            }
        }

        SourceServerConfiguration[] ssc = new SourceServerConfiguration[sourceServers.size()];
        return (SourceServerConfiguration[]) sourceServers.toArray( ssc );

    }

    /**
     * returns the URL of the data service capabilities
     * 
     * @param node
     * @return
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    private OGCWebService getDataServiceCapaOnlineResource( Node node )
        throws XMLParsingException,
            MalformedURLException,
            IOException,
            SAXException,
            InvalidConfigurationException,
            OGCWebServiceException {
        SimpleLink link = parseSimpleLink( (Element) node );

        Linkage linkage = new Linkage( link.getHref().toURL() );

        OGCWebService wfs = null;
        String s = link.getHref().toURL().toExternalForm().toUpperCase();

        if ( s.startsWith( "FILE" ) ) {
            WFSConfigurationDocument doc = new WFSConfigurationDocument();
            doc.load( resolve( linkage.getHref().toExternalForm() ) );
            wfs = WFServiceFactory.createInstance( doc.getConfiguration() );
        } else {
            WFSCapabilitiesDocument doc = new WFSCapabilitiesDocument();
            doc.load( linkage.getHref() );
            wfs = new RemoteWFService( (WFSCapabilities) doc.parseCapabilities() );
        }

        return wfs;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSConfigurationDocument.java,v $
Revision 1.16  2006/08/24 06:42:17  poth
File header corrected

Revision 1.15  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.14  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
