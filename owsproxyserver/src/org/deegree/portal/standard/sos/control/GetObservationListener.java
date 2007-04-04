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
package org.deegree.portal.standard.sos.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Debug;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.sos.om.Observation;
import org.deegree.ogcwebservices.sos.om.ObservationArray;
import org.deegree.portal.standard.sos.Constants;
import org.deegree.portal.standard.sos.SOSClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ...
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class GetObservationListener extends AbstractSOSListener {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static final ILogger LOG = LoggerFactory.getLogger( GetObservationListener.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.portal.sos.control.AbstractSOSListener#validateRequest(org.deegree.enterprise.control.RPCMethodCall)
     */
    protected void validateRequest( RPCMethodCall mc ) throws SOSClientException {
        // TODO something
        // Possible parameters:
        // BoundingBox (mandatory)
        // time (optional)
        // platformID (optional)
        // sensorID (optional)
        // sos:Query

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.portal.sos.control.AbstractSOSListener#createRequest(org.deegree.enterprise.control.RPCMethodCall)
     */
    protected String createRequest( RPCMethodCall mc ) throws SOSClientException {
        RPCParameter[] params = mc.getParameters();
        RPCStruct struct = (RPCStruct) params[0].getValue();

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<sos:GetObservation " );
        sb.append( "xmlns:sos='http://www.opengis.net/sos' " );
        sb
            .append( "xmlns:gml='http://www.opengis.net/gml' xmlns:ogc='http://www.opengis.net/ogc' " );
        sb.append( "outputFormat='SWEObservation' " );
        sb.append( "service='SOS' " );
        sb.append( "version='0.8.0'>" );
        sb.append( "<sos:platformID>" );
        sb.append( struct.getMember( "platformID" ).getValue() );
        sb.append( "</sos:platformID>" );
        sb.append( "<sos:sensorID>" );
        sb.append( struct.getMember( "sensorID" ).getValue() );
        sb.append( "</sos:sensorID>" );

        // TODO create bbox from request
        // attention, there is code in deegree to extract bbox from RPC
        sb.append( "<sos:BoundingBox srsName='EPSG:4326'>" );
        sb.append( "<gml:coord>" );
        sb.append( "<gml:X>0</gml:X>" );
        sb.append( "<gml:Y>0</gml:Y>" );
        sb.append( "</gml:coord>" );
        sb.append( "<gml:coord>" );
        sb.append( "<gml:X>50000000</gml:X>" );
        sb.append( "<gml:Y>50000000</gml:Y>" );
        sb.append( "</gml:coord>" );
        sb.append( "</sos:BoundingBox>" );
        sb.append( "</sos:GetObservation >" );
        Debug.debugMethodEnd();

        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.portal.sos.control.AbstractSOSListener#createData(org.deegree.enterprise.control.RPCMethodCall,
     *      java.util.HashMap)
     */
    protected Object createData( RPCMethodCall mc, HashMap map ) throws SOSClientException {

        ObservationArray obsArray = null;

        List obsList = new ArrayList( 100 );

        try {
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Element e = ( (Document) map.get( key ) ).getDocumentElement();

                List obsNodes = XMLTools.getNodes( e,
                    "/om:ObservationCollection/om:observationMembers/om:ObservationArray"
                        + "/om:observationMembers/gml:Observation", nsContext );

                for (int i = 0; i < obsNodes.size(); i++) {

                    String timeStamp = XMLTools.getNodeAsString( (Node) obsNodes.get( i ),
                        "gml:timeStamp/gml:TimeInstant/gml:timePosition", nsContext, null );

                    String value = XMLTools.getNodeAsString( (Node) obsNodes.get( i ),
                        "gml:resultOf/gml:QuantityList", nsContext, null );

                    obsList.add( new Observation( timeStamp, value ) );

                }

                Observation[] observations = (Observation[]) obsList
                    .toArray( new Observation[obsList.size()] );

                obsArray = new ObservationArray( observations, null, null );

            }
        } catch (Exception e) {
            LOG.logError( "Error creating observation array: "
                + e.getMessage() );
            throw new SOSClientException( "Couldn't create sensor descriptions", e );
        }

        return obsArray;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.portal.sos.control.AbstractSOSListener#setNextPageData(java.lang.Object)
     */
    protected void setNextPageData( Object data ) {
        this.getRequest().setAttribute( Constants.OBSERVATION_DATA, data );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetObservationListener.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
