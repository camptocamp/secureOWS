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

import java.util.HashMap;
import java.util.Iterator;

import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Debug;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.sos.describesensor.SensorMetadata;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.ComponentDescription;
import org.deegree.ogcwebservices.sos.sensorml.Discussion;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.GeoPositionModel;
import org.deegree.ogcwebservices.sos.sensorml.GeographicCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.deegree.ogcwebservices.sos.sensorml.Phenomenon;
import org.deegree.ogcwebservices.sos.sensorml.Product;
import org.deegree.ogcwebservices.sos.sensorml.ResponseModel;
import org.deegree.portal.standard.sos.Constants;
import org.deegree.portal.standard.sos.SOSClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Listener for performing DescribeSensor requests against Sensor Observation Services.
 * 
 * @author <a href="mailto:che@wupperverband.de.de">Christian Heier</a>
 * @version 1.0
 */
public class DescribeSensorListener extends AbstractSOSListener {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static final ILogger LOG = LoggerFactory.getLogger( DescribeSensorListener.class );

    /**
     * validates the request to be performed.
     * 
     * @param mc
     *            RPCMethodCall containing the request to be performed
     * @throws SOSClientException
     *             if request is not valid
     */
    protected void validateRequest( RPCMethodCall mc ) throws SOSClientException {
        RPCParameter[] params = mc.getParameters();
        if ( params == null
            || params.length != 1 ) {
            throw new SOSClientException( "one rpc parameter containing a struct "
                + "with requiered parameters must be set" );
        }
        RPCStruct struct = (RPCStruct) params[0].getValue();
        if ( struct.getMember( Constants.TYPENAME ) == null
            || "".equals( Constants.TYPENAME ) ) {
            throw new SOSClientException( "TypeName parameter must be set "
                + "to perform a Sensor Observation Service " + "DescribeSensor request" );
        }

    }

    /**
     * creates a SOS DescribeSensor request from the parameters contained in the passed
     * <tt>RPCMethodeCall</tt>.
     * 
     * @param mc
     *            the RPCMethodCall
     * @return SOS DescribeSensor request as String
     * @throws SOSClientException
     */
    protected String createRequest( RPCMethodCall mc ) throws SOSClientException {
        Debug.debugMethodBegin();

        RPCParameter[] params = mc.getParameters();
        RPCStruct struct = (RPCStruct) params[0].getValue();

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<sos:DescribeSensor " );
        sb.append( "xmlns:sos='http://www.opengis.net/sos' " );
        sb.append( "outputFormat='SensorML' " );
        sb.append( "service='SOS' " );
        sb.append( "version='0.8.0'>" );
        sb.append( "<sos:TypeName>" );
        sb.append( struct.getMember( Constants.TYPENAME ).getValue() );
        sb.append( "</sos:TypeName>" );
        sb.append( "</sos:DescribeSensor>" );
        Debug.debugMethodEnd();
        return sb.toString();
    }

    /**
     * creates the result object to send to the next page from the parameters contained in the
     * passed <tt>RPCMethodeCall</tt> and the <tt>Document</tt> array.
     * 
     * @param mc
     *            the RPCMethodCall
     * @param map
     *            the Document array
     * @return the result object
     * @throws SOSClientException
     */
    protected Object createData( RPCMethodCall mc, HashMap map ) throws SOSClientException {
        Debug.debugMethodBegin();

        Identifier[] identifiedAs = null;
        Classifier[] classifiedAs = null;
        EngineeringCRS hasCRS = null;
        LocationModel[] locatedUsing = null;
        ComponentDescription describedBy = null;
        String attachedTo = null;
        Product[] measures = null;

        SensorMetadata[] sensorDescriptions = new SensorMetadata[map.size()];

        int i = 0;
        try {
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {

                Object key = iterator.next();
                Element e = ( (Document) map.get( key ) ).getDocumentElement();

                String[] ids = XMLTools.getNodesAsStrings( e,
                    "/sml:Sensors/sml:Sensor/sml:identifiedAs/sml:Identifier", nsContext );

                identifiedAs = new Identifier[ids.length];
                for (int j = 0; j < ids.length; j++) {
                    identifiedAs[j] = new Identifier( ids[j] );
                }

                String srcSRS = XMLTools
                    .getNodeAsString(
                        e,
                        "/sml:Sensors/sml:Sensor/sml:locatedUsing/sml:GeoPositionModel/sml:sourceCRS/gml:EngineeringCRS/gml:srsName",
                        nsContext, null );

                String refSRS = XMLTools
                    .getNodeAsString(
                        e,
                        "/sml:Sensors/sml:Sensor/sml:locatedUsing/sml:GeoPositionModel/sml:sourceCRS/gml:GeographicCRS/gml:srsName",
                        nsContext, null );

                locatedUsing = new LocationModel[1];
                locatedUsing[0] = new GeoPositionModel( null, null, null, null, new EngineeringCRS(
                    srcSRS ), new GeographicCRS( refSRS ), new Object[0] );

                // TODO What about the coordinates?? (sml:GeoLocation)

                // TODO ComponentDescription describedBy

                attachedTo = XMLTools.getNodeAsString( e, "/sml:Sensors/sml:Sensor/sml:attachedTo",
                    nsContext, null );

                // preparing the values for the product[]
                Identifier[] productIdentifiedAs = null;
                Classifier[] productClassifiedAs = null;
                Discussion[] productDescription = null;
                LocationModel[] productLocatedUsing = null;
                EngineeringCRS productHasCRS = null;
                Phenomenon productObservable = null;
                ResponseModel[] productDerivedFrom = null;
                String productId = null;

                ElementList productList = XMLTools.getChildElements( "Product",
                    CommonNamespaces.SMLNS, e );

                measures = new Product[productList.getLength()];

                for (int l = 0; l < productList.getLength(); l++) {

                    String[] prodIds = XMLTools
                        .getNodesAsStrings(
                            e,
                            "/sml:Sensors/sml:Sensor/sml:measures/sml:Product/sml:identifiedAs/sml:Identifier",
                            nsContext );

                    productIdentifiedAs = new Identifier[prodIds.length];
                    for (int k = 0; k < ids.length; k++) {
                        productIdentifiedAs[k] = new Identifier( prodIds[k] );
                    }

                    // TODO productClassifiedAs

                    // TODO productDescription

                    // TODO productLocatedUsing

                    // TODO productHasCRS

                    // TODO productDerivedFrom

                    // TODO productId

                    String observableName = XMLTools
                        .getNodeAsString(
                            e,
                            "/sml:Sensors/sml:Sensor/sml:measures/sml:Product/sml:observable/sml:Phenomenon/sml:name",
                            nsContext, null );

                    // TODO observableDescription

                    // TODO observableId

                    productObservable = new Phenomenon( observableName, null, null );

                    measures[l] = new Product( productIdentifiedAs, productClassifiedAs,
                        productDescription, productLocatedUsing, productDerivedFrom, productHasCRS,
                        productObservable, productId );

                }

                sensorDescriptions[i] = new SensorMetadata( identifiedAs, classifiedAs, hasCRS,
                    locatedUsing, describedBy, attachedTo, measures );
                i++;
            }

        } catch (Exception e) {
            LOG.logError( "Error creating sensor descriptions: "
                + e.getMessage() );
            throw new SOSClientException( "Couldn't create sensor descriptions", e );
        }
        Debug.debugMethodEnd();
        return sensorDescriptions;
    }

    protected void setNextPageData( Object o ) {
        this.getRequest().setAttribute( Constants.SENSORDESCRIPTION, o );
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DescribeSensorListener.java,v $
 * Changes to this class. What the people have been up to: Revision 1.4  2006/04/06 20:25:29  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/04/04 20:39:44  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/03/30 21:20:28  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/02/05 09:30:12  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2005/11/17 08:13:58  deshmukh
 * Changes to this class. What the people have been up to: Renamed nsNode to nsContext
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2005/11/16 13:44:59  mschneider
 * Changes to this class. What the people have been up to: Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2.2.2  2005/11/07 15:38:04  mschneider
 * Changes to this class. What the people have been up to: Refactoring: use NamespaceContext instead of Node for namespace bindings.
 * Changes to this class. What the people have been up to:
 * Revision 1.2.2.1 2005/11/07 13:09:27 deshmukh Switched namespace definitions in
 * "CommonNamespaces" to URI.
 * 
 * Revision 1.2 2005/09/06 18:08:05 taddei removed unused imports
 * 
 * Revision 1.1 2005/09/05 15:25:55 taddei new listener from C. Heier
 * 
 * 
 * 
 **************************************************************************************************/