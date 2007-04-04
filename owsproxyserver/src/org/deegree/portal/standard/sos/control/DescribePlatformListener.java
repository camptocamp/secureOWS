//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/sos/control/DescribePlatformListener.java,v 1.5 2006/08/29 19:54:14 poth Exp $

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
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.sos.describeplatform.PlatformMetadata;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.ComponentDescription;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.GeoPositionModel;
import org.deegree.ogcwebservices.sos.sensorml.GeographicCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.deegree.portal.standard.sos.Constants;
import org.deegree.portal.standard.sos.SOSClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Listener for performing DescribePlatform requests against Sensor Observation Services.
 * 
 * @author <a href="mailto:che@wupperverband.de.de">Christian Heier</a>
 * @version 0.1
 */
public class DescribePlatformListener extends AbstractSOSListener {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static final ILogger LOG = LoggerFactory.getLogger( DescribePlatformListener.class );

    /**
     * validates the request to be performed.
     * 
     * @param mc
     *            object containing the request to be performed
     */
    protected void validateRequest( RPCMethodCall mc ) throws SOSClientException {
        RPCParameter[] params = mc.getParameters();
        if ( params == null
            || params.length != 1 ) {
            throw new SOSClientException( "one rpc parameter containing a struct "
                + "with requiered parameters must be set" );
        }
        RPCStruct struct = (RPCStruct) params[0].getValue();
        if ( struct.getMember( Constants.TYPENAME ) == null ) {
            // TODO "".equals( type value )
            throw new SOSClientException( "TypeName parameter must be set "
                + "to perform a Sensor Observation Service " + "DescribePlatform request" );
        }

    }

    /**
     * creates a SOS DescribePlatform request from the parameters contained in the passed
     * <tt>RPCMethodeCall</tt>.
     * 
     * @param mc
     *            the RPCMethodCall
     * @return SOS DescribePlatform request
     * @throws SOSClientException
     */
    protected String createRequest( RPCMethodCall mc ) throws SOSClientException {
        Debug.debugMethodBegin();

        RPCParameter[] params = mc.getParameters();
        RPCStruct struct = (RPCStruct) params[0].getValue();

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<sos:DescribePlatform " );
        sb.append( "xmlns:sos='http://www.opengis.net/sos' " );
        sb.append( "outputFormat='SensorML' " );
        sb.append( "service='SOS' " );
        sb.append( "version='0.8.0'>" );
        sb.append( "<sos:TypeName>" );
        sb.append( struct.getMember( Constants.TYPENAME ).getValue() );
        sb.append( "</sos:TypeName>" );
        sb.append( "</sos:DescribePlatform>" );
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
     * @return
     */
    protected Object createData( RPCMethodCall mc, HashMap map ) throws SOSClientException {
        Debug.debugMethodBegin();

        Identifier[] identifiedAs = null;
        Classifier[] classifiedAs = null;
        EngineeringCRS engineerCRS = null;
        LocationModel[] locatedUsing = null;
        ComponentDescription describedBy = null;
        String attachedTo = null;
        String[] carries = null;

        PlatformMetadata[] platformDescriptions = new PlatformMetadata[map.size()];

        int i = 0;
        try {
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {

                Object key = iterator.next();
                Element e = ( (Document) map.get( key ) ).getDocumentElement();

                String[] ids = XMLTools.getNodesAsStrings( e,
                    "/sml:Platforms/sml:Platform/sml:identifiedAs/sml:Identifier", nsContext );

                identifiedAs = new Identifier[ids.length];
                for (int j = 0; j < ids.length; j++) {
                    identifiedAs[i] = new Identifier( ids[i] );
                }

                String srcSRS = XMLTools
                    .getNodeAsString(
                        e,
                        "/sml:Platforms/sml:Platform/sml:locatedUsing/sml:GeoPositionModel/sml:sourceCRS/gml:EngineeringCRS/gml:srsName",
                        nsContext, null );

                String refSRS = XMLTools
                    .getNodeAsString(
                        e,
                        "/sml:Platforms/sml:Platform/sml:locatedUsing/sml:GeoPositionModel/sml:sourceCRS/gml:GeographicCRS/gml:srsName",
                        nsContext, null );

                locatedUsing = new LocationModel[1];
                locatedUsing[0] = new GeoPositionModel( null, null, null, null, new EngineeringCRS(
                    srcSRS ), new GeographicCRS( refSRS ), new Object[0] );

                carries = XMLTools.getNodesAsStrings( e,
                    "/sml:Platforms/sml:Platform/sml:carries/sml:Asset", nsContext );

                platformDescriptions[i] = new PlatformMetadata( identifiedAs, classifiedAs,
                    engineerCRS, locatedUsing, describedBy, attachedTo, carries );

                i++;
            }

        } catch (Exception e) {
            LOG.logError( "Error creating platform descriptions: "
                + e.getMessage() );
            throw new SOSClientException( "Couldn't create platform descriptions", e );
        }
        Debug.debugMethodEnd();
        return platformDescriptions;
    }

    protected void setNextPageData( Object o ) {
        this.getRequest().setAttribute( Constants.PLATFORMDESCRIPTION, o );
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DescribePlatformListener.java,v $
 * Changes to this class. What the people have been up to: Revision 1.5  2006/08/29 19:54:14  poth
 * Changes to this class. What the people have been up to: footer corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/04/06 20:25:30  poth
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
 * Changes to this class. What the people have been up to: Revision 1.6  2005/11/17 08:13:58  deshmukh
 * Changes to this class. What the people have been up to: Renamed nsNode to nsContext
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2005/11/16 13:44:59  mschneider
 * Changes to this class. What the people have been up to: Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4.2.1  2005/11/07 15:38:04  mschneider
 * Changes to this class. What the people have been up to: Refactoring: use NamespaceContext instead of Node for namespace bindings.
 * Changes to this class. What the people have been up to:
 * Revision 1.4 2005/10/03 12:55:39 poth no message
 * 
 * Revision 1.3 2005/09/01 12:58:42 taddei describe platfrm listener is now based an a common
 * listener for the sos client
 * 
 * Revision 1.2 2005/08/31 13:18:30 taddei Made listener work. PlatformMetadata has now some
 * parameters
 * 
 * Revision 1.1 2005/08/26 10:12:40 taddei first upload of C. Heyer's client code
 * 
 **************************************************************************************************/