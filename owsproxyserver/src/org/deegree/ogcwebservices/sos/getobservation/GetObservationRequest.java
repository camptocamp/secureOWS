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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.FilterConstructionException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * represent a getObservation request
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class GetObservationRequest extends AbstractOGCWebServiceRequest {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();    
    
    private static final ILogger LOG = LoggerFactory.getLogger( GetObservationRequest.class );

    private Envelope envelope = null;

    private Query query = null;

    private Object[] time = null;

    private String[] platforms = null;

    private String[] sensors = null;
    
    private String outputFormat;

    /**
     * create a GetObservationRequest from KVP Map
     * 
     * @param id
     * @param map
     * 
     */
    public static GetObservationRequest create( Map map ) {
        throw new UnsupportedOperationException();
        /*
        // TODO implement this?
        String id = null;
        String version = null;

        String outputFormat = null;
        Query query = null;
        Envelope boundingBox = null;
        Object[] time = null;
        String[] platformId = null;
        String[] sensorId = null;

        return new GetObservationRequest( query, boundingBox, outputFormat, time, platformId,
            sensorId, version, id, null );
            */
    }

    /**
     * create from XML Document
     * 
     * @param id
     * @param xml
     * @throws OGCWebServiceException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * 
     */
    public static GetObservationRequest create( String id, Document doc )
        throws OGCWebServiceException {
        LOG.entering();
        GetObservationRequest goReq = null;
        try {
            // gets the version
            String version = XMLTools.getNodeAsString( doc, "sos:GetObservation/@version", nsContext,
                null );

            // gets the outputFormat
            String outputFormat = XMLTools.getNodeAsString( doc,
                "sos:GetObservation/@outputFormat", nsContext, "SWEObservation" );

            // optional, fixed to "SOS"
            String service = XMLTools.getNodeAsString( doc, "sos:GetObservation/@service", nsContext,
                null );
            if ( ( service != null )
                && ( !service.equals( "SOS" ) ) ) {
                throw new OGCWebServiceException( "service must be 'SOS'" );
            }

            // gets Bounding Box
            Node boxNode = XMLTools.getRequiredNode( doc, "sos:GetObservation/sos:BoundingBox",
                nsContext );
            Envelope env = GMLGeometryAdapter.wrapBox( (Element) boxNode );

            Node queryNode = XMLTools.getNode( doc, "sos:GetObservation/sos:Query", nsContext );
            // gets Query
            Query query = null;
            if ( queryNode != null ) {
                query = createQuery( queryNode );
            }

            List timeList = XMLTools.getNodes( doc, "sos:GetObservation/sos:time", nsContext );

            ArrayList time = getTimeList( timeList );

            // gets id's from the requested platforms
            List platformIdList = XMLTools.getNodes( doc, "sos:GetObservation/sos:platformID",
                nsContext );
            ArrayList platformId = new ArrayList( platformIdList.size() );
            for (int i = 0; i < platformIdList.size(); i++) {
                platformId.add( XMLTools.getRequiredNodeAsString( (Node) platformIdList.get( i ),
                    "text()", nsContext ) );
            }

            LOG.logDebug( "Platforms=" + platformId.size() );

            // gets id's from the requested sensors
            List sensorIdList = XMLTools.getNodes( doc, "sos:GetObservation/sos:sensorID",
                nsContext );
            ArrayList sensorId = new ArrayList( sensorIdList.size() );
            for (int i = 0; i < sensorIdList.size(); i++) {
                sensorId.add( XMLTools.getRequiredNodeAsString( (Node) sensorIdList.get( i ), "text()",
                    nsContext ) );
            }
            LOG.logDebug( "Sensors=" + sensorId.size() );

            goReq = new GetObservationRequest( query, env, outputFormat, ( 
                                    time.toArray( new Object[time.size()] ) ), 
                                    ( (String[]) platformId.toArray( new String[platformId.size()] ) ), 
                                    ( (String[]) sensorId.toArray( new String[sensorId.size()] ) ),
                                    version, id, null );

        } catch (XMLParsingException e) {
            e.printStackTrace();
            throw new OGCWebServiceException( "scs webservice failure" );
        } catch ( UnknownCRSException e ) {
            e.printStackTrace();
            throw new OGCWebServiceException( "scs webservice failure" );
        }
        LOG.exiting();
        return goReq;
    }

    /**
     * @param nsContext
     * @param timeList
     * @return
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     */
    private static ArrayList getTimeList( List timeList )
        throws XMLParsingException,
            OGCWebServiceException {
        LOG.entering();
        ArrayList time = new ArrayList( timeList.size() );
        for (int i = 0; i < timeList.size(); i++) {

            if ( ( XMLTools.getNode( (Node) timeList.get( i ), "gml:TPeriod", nsContext ) ) != null ) {

                String begin = XMLTools.getNodeAsString( (Node) timeList.get( i ),
                    "gml:TPeriod/gml:begin/gml:TInstant/gml:tPosition/text()", nsContext, null );
                if ( begin == null ) {
                    throw new OGCWebServiceException( "TPeriod must have a begin time Position" );
                }

                String end = XMLTools.getNodeAsString( (Node) timeList.get( i ),
                    "gml:TPeriod/gml:end/gml:TInstant/gml:tPosition/text()", nsContext, null );
                if ( end == null ) {
                    throw new OGCWebServiceException( "TPeriod must have a end time Position" );
                }

                LOG.logDebug( "create TPeriod with begin="
                    + begin + " end=" + end );
                time.add( new TPeriod( begin, end ) );
            } else if ( ( XMLTools.getNode( (Node) timeList.get( i ), "gml:TInstant", nsContext ) ) != null ) {

                String temp = XMLTools.getNodeAsString( (Node) timeList.get( i ),
                    "gml:TInstant/gml:tPosition/text()", nsContext, null );
                if ( temp == null ) {
                    throw new OGCWebServiceException( "TInstant must have a time Position" );
                }

                LOG.logDebug( "create TInstant with time="
                    + temp );
                time.add( new TInstant( temp ) );
            } else {
                throw new OGCWebServiceException( "time must have a TPeriod or a TInstant" );
            }
        }
        LOG.exiting();
        return time;
    }

    /**
     * @param nsContext
     * @param queryNode
     * @return
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     */
    private static Query createQuery( Node queryNode )
        throws XMLParsingException,
            OGCWebServiceException {
        Query query;
        String feature = XMLTools.getRequiredNodeAsString( queryNode, "@typeName", nsContext );
        Element filterElement = XMLTools.getChildElement( queryNode, "ogc:Filter" );
        ComplexFilter filter = null;
        if ( filterElement != null ) {
            try {
                filter = (ComplexFilter) ComplexFilter.buildFromDOM( filterElement );
            } catch (FilterConstructionException e) {
                e.printStackTrace();
                throw new OGCWebServiceException( "sos:Query is not valid!" );
            }
        }
        query = new Query( feature, filter );
        return query;
    }

    /**
     * @param id
     * @param version
     * @param sensorId
     * @param platformId
     * @param time
     * @param boundingBox
     * @param query
     * @param outputFormat
     * 
     */
    public static void create( String id, String version, String[] sensorId, String[] platformId,
                              Object[] time, Envelope boundingBox, Query query, String outputFormat ) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param query
     * @param boundingBox
     * @param outputFormat
     * @param time
     * @param platformId
     * @param sensorId
     * @param version
     * @param id
     * 
     */
    private GetObservationRequest( Query query, Envelope envelope, String outputFormat,
                                  Object[] time, String[] platformId, String[] sensorId,
                                  String version, String id, Map vendorSpecificParameter ) {

        super( version, id, vendorSpecificParameter );

        this.envelope = envelope;
        this.query = query;
        this.time = time;
        this.platforms = platformId;
        this.sensors = sensorId;
        this.outputFormat = outputFormat;
    }

    /**
     * fixed 'SOS'
     */
    public String getServiceName() {
        return "SOS";
    }

    public Envelope getBBox() {
        return envelope;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public Query getQuery() {
        return query;
    }

    public String[] getSensors() {
        return sensors;
    }

    public Object[] getTime() {
        return time;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetObservationRequest.java,v $
Revision 1.14  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.13  2006/10/18 17:00:55  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.12  2006/08/24 06:42:16  poth
File header corrected

Revision 1.11  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
