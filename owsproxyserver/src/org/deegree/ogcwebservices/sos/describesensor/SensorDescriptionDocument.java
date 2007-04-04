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
package org.deegree.ogcwebservices.sos.describesensor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.sos.ComponentDescriptionDocument;
import org.deegree.ogcwebservices.sos.WFSRequestGenerator;
import org.deegree.ogcwebservices.sos.WFSRequester;
import org.deegree.ogcwebservices.sos.XMLFactory;
import org.deegree.ogcwebservices.sos.XSLTransformer;
import org.deegree.ogcwebservices.sos.configuration.MeasurementConfiguration;
import org.deegree.ogcwebservices.sos.configuration.SOSDeegreeParams;
import org.deegree.ogcwebservices.sos.configuration.SensorConfiguration;
import org.deegree.ogcwebservices.sos.sensorml.BasicResponse;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.ComponentDescription;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.deegree.ogcwebservices.sos.sensorml.Phenomenon;
import org.deegree.ogcwebservices.sos.sensorml.Product;
import org.deegree.ogcwebservices.sos.sensorml.ResponseModel;
import org.deegree.ogcwebservices.sos.sensorml.TypedQuantity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * reads the metadata of a sensor from the xsl transformed result from a wfs
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 */

public class SensorDescriptionDocument extends ComponentDescriptionDocument {

    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    private static final String XML_TEMPLATE = "DescribeSensorTemplate.xml";

    public void createEmptyDocument() throws IOException, SAXException {
        URL url = SensorDescriptionDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '"
                + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * 
     * @param deegreeParams
     * @param typNames
     * @return
     * @throws OGCWebServiceException
     */
    public SensorMetadata[] getSensor( SOSDeegreeParams deegreeParams, String[] typNames )
        throws OGCWebServiceException {
        try {

            // gets the documents from wfs server
            Document[] docs = getSourceServerDocuments( deegreeParams, typNames );

            ArrayList sensorMetadata = new ArrayList( docs.length );
            for (int d = 0; d < docs.length; d++) {

                if ( docs[d] != null ) {

                    List nl = XMLTools.getNodes( docs[d], "sml:Sensors/sml:Sensor", nsContext );

                    if ( nl.size() < 1 ) {
                        LOG.logError( "no sensors found in wfs result document" );
                    }

                    // process all sensors in document
                    for (int y = 0; y < nl.size(); y++) {

                        Node sensorNode = (Node) nl.get( y );

                        String actIdPropertyValue = XMLTools.getRequiredNodeAsString( sensorNode,
                            "@id", nsContext );

                        // get identifiedAs
                        ArrayList identifiers = new ArrayList();
                        List identifierList = XMLTools.getNodes( sensorNode,
                            "sml:identifiedAs", nsContext );
                        if ( ( identifierList == null )
                            || ( identifierList.size() <= 0 ) ) {
                            throw new XMLParsingException( "at least one identifiedAs required" );

                        }
                        for (int i = 0; i < identifierList.size(); i++) {
                            identifiers.add( getIdentifiedAs( (Node) identifierList.get( i ) ) );
                        }

                        // get ClassifiedAs
                        List classifierList = XMLTools.getNodes( sensorNode,
                            "sml:classifiedAs", nsContext );
                        ArrayList classifiers = new ArrayList( classifierList.size() );
                        for (int i = 0; i < classifierList.size(); i++) {
                            classifiers.add( getClassifiedAs( (Node)classifierList.get( i ) ) );
                        }

                        // get attachedTo
                        String attachedTo = getAttachedTo( XMLTools.getNode( sensorNode,
                            "sml:attachedTo", nsContext ) );

                        // get hasCRS
                        EngineeringCRS hasCRS = getHasCRS( XMLTools.getNode( sensorNode,
                            "sml:hasCRS", nsContext ) );

                        // get locatedUsing
                        List locationModelList = XMLTools.getNodes( sensorNode,
                            "sml:locatedUsing", nsContext );
                        ArrayList locationModels = new ArrayList( locationModelList.size() );
                        for (int i = 0; i < locationModelList.size(); i++) {
                            locationModels.add( getLocatedUsing( (Node)locationModelList.get( i ) ) );
                        }

                        // getdescribedBy
                        ComponentDescription describedBy = getDescribedBy( XMLTools.getNode(
                            sensorNode, "sml:describedBy", nsContext ) );

                        // TODO---------add the products from Configuration
                        SensorConfiguration temp = deegreeParams
                            .getSensorConfigurationByIdPropertyValue( actIdPropertyValue );

                        MeasurementConfiguration[] measurements = temp
                            .getMeasurementConfigurations();

                        if ( ( measurements == null )
                            || ( measurements.length < 1 ) ) {
                            throw new XMLParsingException( "at least one measures needed" );
                        }

                        // get measures
                        ArrayList measures = new ArrayList( measurements.length );
                        for (int i = 0; i < measurements.length; i++) {

                            // Identifier anlegen
                            Identifier[] ti = new Identifier[] { new Identifier( measurements[i]
                                .getId() ) };

                            // observable anlegen
                            Phenomenon phen = new Phenomenon( measurements[i].getPhenomenon(),
                                null, null );

                            ResponseModel[] resm = null;
                            if ( measurements[i].getTimeResolution() != null ) {
                                // derivedfrom anlegen
                                double dd = Double
                                    .parseDouble( measurements[i].getTimeResolution() );
                                URI uri = new URI( measurements[i].getTimeResolutionType() );
                                TypedQuantity typedQan = new TypedQuantity( dd, uri );

                                Object[] o = new Object[] { new BasicResponse( typedQan ) };
                                resm = new ResponseModel[] { new ResponseModel( null, null, null,
                                    null, o ) };
                            }
                            // product anlegen und zur liste hinzuf�gen
                            Product product = new Product( ti, null, null, null, resm, null, phen,
                                null );
                            measures.add( product );
                        }

                        // --end of measurments

                        // add act Metadata to ArrayList
                        Identifier[] i1 = (Identifier[]) identifiers
                            .toArray( new Identifier[identifiers.size()] );
                        Classifier[] c1 = (Classifier[]) classifiers
                            .toArray( new Classifier[classifiers.size()] );
                        LocationModel[] l1 = new LocationModel[locationModels.size()];
                        l1 = (LocationModel[]) locationModels.toArray( l1 );
                        Product[] p1 = (Product[]) measures.toArray( new Product[measures.size()] );
                        sensorMetadata.add( new SensorMetadata( i1, c1, hasCRS, l1, describedBy,
                            attachedTo, p1 ) );
                    }
                }
            }

            // return the Array with Sensormetadata
            return ( (SensorMetadata[]) sensorMetadata.toArray( new SensorMetadata[sensorMetadata
                .size()] ) );

        } catch (Exception e) {
            e.printStackTrace();
            throw new OGCWebServiceException( "scs webservice failure" );
        }
    }

    /**
     * 
     * @param deegreeParams
     * @param typNames
     * @return
     * @throws IOException
     * @throws IOException
     * @throws SAXException
     * @throws XMLParsingException
     * @throws TransformerException
     */
    private Document[] getSourceServerDocuments( SOSDeegreeParams dp, String[] typNames )
        throws IOException,
            SAXException,
            XMLParsingException,
            TransformerException,
            OGCWebServiceException {

        ArrayList transformedDocuments = new ArrayList();

        Hashtable servers = new Hashtable();

        for (int t = 0; t < typNames.length; t++) {
            String sourceServerId = dp.getSensorConfiguration( typNames[t] ).getSourceServerId();

            // server schon in liste; nur sensor hinzuf�gen
            if ( servers.containsKey( sourceServerId ) ) {
                ( (ArrayList) servers.get( sourceServerId ) ).add( typNames[t] );
            }
            // server nicht in liste; server hinzuf�gen und sensor hinzuf�gen
            else {
                ArrayList temp = new ArrayList();
                temp.add( typNames[t] );
                servers.put( sourceServerId, temp );
            }

        }

        // request all servers from servers hashtable
        Iterator iter = servers.keySet().iterator();
        while (iter.hasNext()) {

            String key = (String) iter.next();
            List sensorIds = (ArrayList) servers.get( key );

            String[] idProps = new String[sensorIds.size()];
            for (int a = 0; a < sensorIds.size(); a++) {
                idProps[a] = (String) sensorIds.get( a );
            }

            QualifiedName sdft = dp.getSourceServerConfiguration( key ).getSensorDescriptionFeatureType();
            QualifiedName sdid = dp.getSourceServerConfiguration( key )
                .getSensorDescriptionIdPropertyName();
            Document request = WFSRequestGenerator.createIsLikeOperationWFSRequest( idProps, sdft,
                sdid );

            OGCWebService ows = dp.getSourceServerConfiguration( key ).getDataService();
            Document result = null;
            try {
                result = WFSRequester.sendWFSrequest( request, ows );
            } catch (Exception e) {
                LOG.logError( "could not sensor data from DataService", e );
                throw new OGCWebServiceException( this.getClass().getName(), "could not sensor"
                    + " data from DataService" + e.getMessage() );
            }
            if ( result != null ) {
                transformedDocuments.add( XSLTransformer.transformDocument( result, dp
                    .getSourceServerConfiguration( key ).getSensorDescriptionXSLTScriptSource() ) );
            }

        }

        return ( (Document[]) transformedDocuments.toArray( new Document[transformedDocuments
            .size()] ) );
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SensorDescriptionDocument.java,v $
Revision 1.18  2006/10/18 17:00:56  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.17  2006/08/24 06:42:17  poth
File header corrected

Revision 1.16  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
