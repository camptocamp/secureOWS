// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/wms/control/HighlightingInfoListener.java,v 1.7 2006/10/17 20:31:17 poth Exp $
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
package org.deegree.portal.standard.wms.control;

import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.LayerExtension;
import org.deegree.portal.context.ViewContext;

/**
 * This class is for accessing informations about the highlighted polygons A new WFS GetFeature
 * request will be created and performed
 * 
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/10/17 20:31:17 $
 * 
 * @since 1.1
 */
public class HighlightingInfoListener extends AbstractMapListener {

    /**
     * 
     * 
     * @param event
     */
    public void actionPerformed( FormEvent event ) {
        

        // default actions
        super.actionPerformed( event );
        RPCWebEvent rpc = (RPCWebEvent) event;
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter[] para = mc.getParameters();
        RPCStruct struct = (RPCStruct) para[0].getValue();

        FeatureCollection fc = null;
        try {
            // get boundingbox of the request
            RPCMember member = struct.getMember( "boundingBox" );
            String tmp = (String) member.getValue();
            double[] box = StringTools.toArrayDouble( tmp, "," );
            // get coordinates for filtering from the request
            Position[] coords = getCoordinates( struct );
            // get layers/featuretypes to query
            String[] queryLayers = getQueryLayers( struct );
            // create WFS GetFeature request
            String request = createRequest( queryLayers, coords, box );
            // get responsible WFS URL
            URL url = getResponsibleWFS( queryLayers[0] );
            // get FeatureCollection from WFS
            fc = performGetFeature( request, url );
        } catch (Exception ex) {
            gotoErrorPage( "Invalid WCSCapabilityOperations: "
                + ex.toString() );
            ex.printStackTrace();
            
            return;
        }

        this.getRequest().setAttribute( "HIGHLIGHTINFO", fc );

        
    }

    /**
     * gets the layer to be highlighted.
     */
    private String[] getQueryLayers( RPCStruct struct ) {
        RPCMember mem = struct.getMember( "queryLayers" );
        String tmp = (String) mem.getValue();
        String[] queryLayers = StringTools.toArray( tmp, ",", false );
        for (int i = 0; i < queryLayers.length; i++) {
            int index = queryLayers[i].indexOf( "|" );
            queryLayers[i] = queryLayers[i].substring( 0, index );
        }
        return queryLayers;
    }

    /**
     * returns the URL of the WFS that is responsible for accessing the data of the passed
     * layer/featuretype
     * 
     * @param queryLayer
     *            layer to determine the responsible WFS for data access
     */
    private URL getResponsibleWFS( String queryLayer ) throws PortalException {
        

        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
        ViewContext vc = (ViewContext) session.getAttribute( "DefaultMapContext" );
        if ( vc.getLayerList().getLayer( queryLayer ) == null ) {
            throw new PortalException( "'"
                + queryLayer + "' is not known by the client!" );
        }
        LayerExtension le = vc.getLayerList().getLayer( queryLayer ).getExtension();
        URL wfsurl = null;
        if ( le.getDataService() == null ) {
            throw new PortalException( "no WFS registered in MapContext for requested layer. "
                + "Please contact your responsible administrator." );
        }
        if ( !le.getDataService().getServer().getService().equals( "ogc:WFS" ) ) {
            throw new PortalException( "The responsible services isn't a ogc:WFS; no "
                + "detail informations are available! " );
        }
        wfsurl = le.getDataService().getServer().getOnlineResource();

        
        return wfsurl;
    }

    /**
     * calculates the coordinates of the click event.
     */
    private Position[] getCoordinates( RPCStruct struct ) {
        

        String xs = (String) struct.getMember( "x" ).getValue();
        String ys = (String) struct.getMember( "y" ).getValue();
        double[] x = StringTools.toArrayDouble( xs, "," );
        double[] y = StringTools.toArrayDouble( ys, "," );

        Position[] pos = new Position[x.length];

        for (int i = 0; i < pos.length; i++) {
            pos[i] = GeometryFactory.createPosition( x[i], y[i] );
        }

        
        return pos;
    }

    /**
     * creates a WFS GetFeature request from the passed layers (feature types) and the coordinates.
     * The least are used to create the filter conditions.
     * 
     * @param queryLayers
     *            names of the layers/featuretypes that will be targeted by the request
     * @param coords
     *            coordinates to be used as filter conditions (intersect)
     * @param box
     *            relevant bounding box
     */
    private String createRequest( String[] queryLayers, Position[] coords, double[] box ) {
        

        StringBuffer sb = new StringBuffer( 5000 );
        sb.append( "<?xml version='1.0' encoding='UTF-8'?>" ).append(
            "<GetFeature xmlns='http://www.opengis.net/wfs' " ).append(
            "xmlns:ogc='http://www.opengis.net/ogc' " ).append(
            "xmlns:gml='http://www.opengis.net/gml' " ).append(
            "service='WFS' version='1.0.0' outputFormat='GML2'>" ).append( "<Query typeName='"
            + queryLayers[0] + "'>" );

        sb.append( "<ogc:Filter><ogc:And>" );
        if ( coords.length > 1 )
            sb.append( "<ogc:Or>" );

        for (int k = 0; k < coords.length; k++) {
            sb.append( "<ogc:Intersects><ogc:PropertyName>GEOM</ogc:PropertyName>" ).append(
                "<gml:Point><gml:coordinates>" ).append( coords[k].getX() ).append( ',' ).append(
                coords[k].getY() ).append( "</gml:coordinates>" ).append(
                "</gml:Point></ogc:Intersects>" );
        }

        if ( coords.length > 1 )
            sb.append( "</ogc:Or>" );

        sb.append( "<ogc:BBOX><ogc:PropertyName>GEOM</ogc:PropertyName>" ).append(
            "<gml:Box><gml:coordinates>" ).append( box[0] ).append( ',' ).append( box[1] ).append(
            ' ' ).append( box[2] ).append( ',' ).append( box[3] ).append( "</gml:coordinates>" )
            .append( "</gml:Box></ogc:BBOX>" ).append(
                "</ogc:And></ogc:Filter></Query></GetFeature>" );

        
        return sb.toString();
    }

    /**
     * performs a GetFeature request against the responsible WFS
     */
    private FeatureCollection performGetFeature( String request, URL wfsURL )
        throws PortalException {
        

        FeatureCollection fc = null;
        try {
            NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), wfsURL, request );
            InputStreamReader isr = new InputStreamReader( nw.getInputStream(), CharsetUtils
                .getSystemCharset() );
            GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument ();
            doc.load(isr, wfsURL.toString());
            fc = doc.parse();
        } catch (Exception e) {
            throw new PortalException( "couldn't perform GetFeature request", e );
        }

        
        return fc;
    }

}
/* *************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log $
 * Revision 1.5  2006/08/07 13:56:28  poth
 * useless type cast removed
 * Changes to this class. What the people have been up to:
 * Revision 1.4  2006/04/06 20:25:31  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.3  2006/04/04 20:39:44  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.2  2006/03/30 21:20:28  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.1  2006/02/05 09:30:12  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.8  2006/01/20 18:18:15  mschneider
 * Adapted to use GMLFeatureCollectionDocument.
 * Changes to this class. What the people have been up to:
 * Revision 1.7  2005/11/22 18:08:50  deshmukh
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.6 2005/11/16 13:45:01 mschneider Merge of wfs development branch.
 * 
 * Revision 1.5.2.1 2005/11/10 15:24:44 mschneider Refactoring: use "PropertyPath" in
 * "org.deegree.model.filterencoding.PropertyName".
 * 
 * Revision 1.5 2005/09/27 19:53:19 poth no message
 * 
 * Revision 1.4 2005/08/30 08:25:48 poth no message
 * 
 * Revision 1.3 2005/08/05 19:14:34 poth no message
 * 
 * Revision 1.2 2005/07/18 07:00:50 poth no message
 * 
 * Revision 1.3 2004/07/09 06:58:04 ap no message
 * 
 * Revision 1.2 2004/06/28 06:40:04 ap no message
 * 
 * Revision 1.1 2004/06/23 15:31:28 ap no message
 * 
 * Revision 1.5 2004/06/11 08:47:30 ap no message
 * 
 * Revision 1.4 2004/06/08 13:03:20 tf refactor to org.deegree.enterprise
 * 
 * Revision 1.3 2004/06/03 09:02:20 ap no message
 * 
 * Revision 1.2 2004/05/24 06:58:47 ap no message
 * 
 * Revision 1.1 2004/05/22 09:55:36 ap no message
 * 
 * 
 * 
 ************************************************************************************************ */
