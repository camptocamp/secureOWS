//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/wms/control/RecenterToLayerListener.java,v 1.1 2006/11/28 13:47:27 mays Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
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

package org.deegree.portal.standard.wms.control;

import java.net.URL;

import javax.servlet.ServletRequest;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.util.MapUtils;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.portal.PortalException;

/**
 * This class sets the bounding box of the portal to the bounding box of one selected layer as it 
 * is specified in the WMSCapabilities document of that layer.
 * 
 * If the ScaleHint of the layer reduces the visible part of the layer's LatLonBoundingBox, 
 * then a new bounding box for this scale hint is calculated. 
 * Ohterwise the LatLonBoundingBox is used.
 * 
 * The chosen bounding box is transformed to the portals crs 
 * and the portals aspect ratio of width and height.
 * 
 * @author <a href="mailto:ncho@lat-lon.de">Serge N'Cho</a>
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version $Revision: 1.1 $, $Date: 2006/11/28 13:47:27 $
 */
public class RecenterToLayerListener extends AbstractListener {

    private static final double PIXEL_SIZE_IN_METERS = 0.00028;

    /* (non-Javadoc)
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    public void actionPerformed( FormEvent event ) {

        RPCWebEvent rpc = (RPCWebEvent) event;
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();

        String capabilitiesRequest = struct.getMember( "capabilitiesRequest" ).getValue().toString();
        String layerName = struct.getMember( "layerName" ).getValue().toString();

        String crs = struct.getMember( "crs" ).getValue().toString();

        int mapWidth = Integer.parseInt( struct.getMember( "mapWidth" ).getValue().toString() );
        int mapHeight = Integer.parseInt( struct.getMember( "mapHeight" ).getValue().toString() );

        try {
            WMSCapabilitiesDocument capsDoc = new WMSCapabilitiesDocument();
            capsDoc.load( new URL( capabilitiesRequest ) );
            WMSCapabilities wmsCaps = (WMSCapabilities) capsDoc.parseCapabilities();
            //Envelope in epsg4326
            Envelope latLonBBox = wmsCaps.getLayer( layerName ).getLatLonBoundingBox();

            if ( latLonBBox == null ) {
                // this will never happen, because:
                // capsDoc.parseCapabilities will check, that there is a latLonBBox for the document. 
                // otherwise an exception will be thrown there.
                // getLayer( someName ).getLatLonBoundingBox() returns the bbox for the given layer, 
                // or, if not set, returns the bbox of the parentLayer. 
                throw new PortalException( "LatLonBoundingBox not found in Capabilities" );
            }

            Envelope outBBox = null;
            final String epsg4326 = "EPSG:4326";
            ScaleHint scaleHint = wmsCaps.getLayer( layerName ).getScaleHint();
            
            //should use Double.POSITIVE_INFINITY instead of Double.MAX_VALUE  
            if ( scaleHint.getMax() == Double.MAX_VALUE && scaleHint.getMin() == 0  ){
                // scaleHint is not defined in capabilities
                
                double latLonScale = MapUtils.calcScale( mapWidth, mapHeight, latLonBBox, 
                                                         CRSFactory.create( epsg4326 ), 
                                                         PIXEL_SIZE_IN_METERS );
                Envelope scaleHintBBox = MapUtils.scaleEnvelope( latLonBBox, latLonScale, 
                                                                 scaleHint.getMax() );
                
                //both bboxes are in crs EPSG:4326
                if ( scaleHintBBox.getWidth() < latLonBBox.getWidth() && 
                     scaleHintBBox.getHeight() < latLonBBox.getHeight() ) {
                    
                    outBBox = scaleHintBBox; 
                } else {
                    outBBox = latLonBBox;
                }
            } else {
                outBBox = latLonBBox;
            }
            
            // transform outBBox to the crs of the portal
            outBBox = new GeoTransformer( crs ).transform( outBBox, epsg4326 );
            
            // ensure aspect ratio of the portal
            outBBox = MapUtils.ensureAspectRatio( outBBox, mapWidth, mapHeight );

            double layerBBoxArray[] = { outBBox.getMin().getX(), outBBox.getMin().getY(),
                                       outBBox.getMax().getX(), outBBox.getMax().getY() };

            ServletRequest req = this.getRequest();
            req.setAttribute( "BBOX", layerBBoxArray );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:

$Log: RecenterToLayerListener.java,v $
Revision 1.1  2006/11/28 13:47:27  mays
add RecenterToLayerListener to deegree2 iGeoPortal Std.Ed.

********************************************************************** */
