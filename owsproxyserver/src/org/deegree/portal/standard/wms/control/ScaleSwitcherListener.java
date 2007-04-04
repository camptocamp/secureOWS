//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/wms/control/ScaleSwitcherListener.java,v 1.14 2006/11/27 09:07:52 poth Exp $$
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
 Aennchenstraße 19
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

import java.util.ResourceBundle;

import javax.servlet.ServletRequest;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.MapUtils;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.portal.Constants;
import org.deegree.portal.PortalException;

/**
 * The ScaleSwitcherListner handles switching the values of the scales from igeoportal
 * 
 * @author <a href="mailto:ncho@lat-lon.de">Serge N'Cho</a>
 * @author last edited by: $$Author: poth $$
 * 
 * @version $$Revision: 1.14 $$, $$Date: 2006/11/27 09:07:52 $$
 */
public class ScaleSwitcherListener extends AbstractMapListener {

    private static final ILogger LOG = LoggerFactory.getLogger( ScaleSwitcherListener.class );

    private static double PIXEL_SIZE = 0.00028;

    static {
        final ResourceBundle bundle = ResourceBundle.getBundle( "org.deegree.portal.standard.wms.control.map_listener" ); //$NON-NLS-1$
        String ps = bundle.getString( "ScaleSwitcher.pixelSize" );
        if ( ps != null ) {
            PIXEL_SIZE = Double.valueOf( ps );
        }
    }

    /**
     * Constant for "taskFromListener"
     */
    public static final String TASK_FROM_LISTENER = "taskFromListener";

    /**
     * Constant for "BBOX"
     */
    public static final String BBOX = "BBOX";

    /**
     * Constant for "zoomToFullExtent"
     */
    public static final String FULL_EXTENT = "zoomToFullExtent";

    /**
     * Constant for "scaleValue"
     */
    public static final String SCALE_VALUE = "scaleValue";

    /**
     * Constant for "newScaleValue"
     */
    public static final String NEW_SCALE_VALUE = "newScaleValue";

    /**
     * Constant for "newBBox"
     */
    public static final String NEW_BBOX = "newBBox";

    /**
     * Constant for "crs"
     */
    private static final String CRS = "crs";

    /**
     * Constant for "mapWidth"
     */
    private static final String MAP_WIDTH = "mapWidth";

    /**
     * Constant for "mapHeight"
     */
    private static final String MAP_HEIGHT = "mapHeight";

    /**
     * Constant for "taskFromJSObject"
     */
    private static final String JS_TAK = "taskFromJSObject";

    /**
     * Constant for "requestedScale"
     */
    private static final String REQUESTED_SCALE = "requestedScale";

    /**
     * Constant for "getNewBBOX"
     */
    private static final String GET_NEW_BBOX = "getNewBBOX";

    /**
     * Constant for "savedScaleValue"
     */
    private static final String SAVED_SCALE = "savedScaleValue";

    /**
     * Constant for "getActualScaleValue"
     */
    private static final String GET_ACTUAL_SCALE = "getActualScaleValue";

    /**
     * @see org.deegree.enterprise.control
     *      .WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    public void actionPerformed( FormEvent event ) {

        LOG.entering();
        RPCWebEvent rpc = (RPCWebEvent) event;
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        double minx = (Double) struct.getMember( Constants.RPC_BBOXMINX ).getValue();
        double miny = (Double) struct.getMember( Constants.RPC_BBOXMINY ).getValue();
        double maxx = (Double) struct.getMember( Constants.RPC_BBOXMAXX ).getValue();
        double maxy = (Double) struct.getMember( Constants.RPC_BBOXMAXY ).getValue();
        String crs = struct.getMember( CRS ).getValue().toString();
        CoordinateSystem cs;
        try {
            cs = CRSFactory.create( crs );
        } catch ( UnknownCRSException e1 ) {
            gotoErrorPage( e1.getMessage() );
            return;
        }
        Envelope actualBBox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, cs );
        double mapWidth = (Double) struct.getMember( MAP_WIDTH ).getValue();
        double mapHeight = (Double) struct.getMember( MAP_HEIGHT ).getValue();
        String taskFromJSObject = struct.getMember( JS_TAK ).getValue().toString();
        ServletRequest request = this.getRequest();

        try {

            double actualScalValue = MapUtils.calcScale( (int) mapWidth, (int) mapHeight,
                                                        actualBBox,
                                                        actualBBox.getCoordinateSystem(),
                                                        PIXEL_SIZE );

            if ( GET_NEW_BBOX.equals( taskFromJSObject ) ) {

                String scaleRequestString = struct.getMember( REQUESTED_SCALE ).getValue().toString();

                double requestedScale = Double.parseDouble( scaleRequestString.substring(
                                                                                          2,
                                                                                          scaleRequestString.length() ) );

                double savedScaleValue = (Double) struct.getMember( SAVED_SCALE ).getValue();
                Envelope newBbox = calcNewBBox( actualBBox, requestedScale, actualScalValue,
                                                savedScaleValue );

                double[] bbox = { newBbox.getMin().getX(), newBbox.getMin().getY(),
                                 newBbox.getMax().getX(), newBbox.getMax().getY() };

                Integer scaleValue = new Integer( (int) ( requestedScale ) );
                request.setAttribute( SCALE_VALUE, scaleValue );
                request.setAttribute( BBOX, bbox );
                request.setAttribute( TASK_FROM_LISTENER, NEW_BBOX );

            } else if ( GET_ACTUAL_SCALE.equals( taskFromJSObject ) ) {

                Integer scaleValue = new Integer( (int) actualScalValue );
                request.setAttribute( SCALE_VALUE, scaleValue );
                request.setAttribute( TASK_FROM_LISTENER, NEW_SCALE_VALUE );

            } else {
                String message = "Unknown task from ScaleSwitcher module" + taskFromJSObject;
                throw new PortalException( message );
            }
        } catch ( PortalException e ) {
            request.setAttribute( TASK_FROM_LISTENER, FULL_EXTENT );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        LOG.exiting();

    }

    /**
     * The methode <code>calcNewBBox</code> calculates a new boundingbox for a requested scale. It
     * will either zoom in or zoom out of the <code>actualBBOX<code> depending
     * on the ratio of the <code>requestedScale</code> to the <code>actualScale</code>
     * @param actualBBOX    
     * @param crs
     * @param requestedScale
     * @return
     */
    private Envelope calcNewBBox( Envelope actualBBOX, double requestedScale, double actualScale,
                                 double savedScaleValue )
                            throws Exception {

        LOG.entering();
        Envelope newBBox = null;
        double ratio = requestedScale / actualScale;
        // NaN ration will return a null Envelope
        if ( Double.isNaN( ratio ) ) {
            // FIXME when does this occurs? and how should this be handle?
            throw new PortalException( "ratio is not a number" );
        }

        if ( Double.isInfinite( ratio ) ) {// infinite ratio will return infinite Envelope
            // the actualScale calculated was probably 0: use the saveScaleValue
            if ( savedScaleValue > 1 ) {
                ratio = requestedScale / savedScaleValue;
            }
            if ( Double.isInfinite( ratio ) ) {
                throw new PortalException( "ratio is infinite" );
            }
        }
        double newWidth = actualBBOX.getWidth() * ratio;
        double newHeight = actualBBOX.getHeight() * ratio;
        double midX = actualBBOX.getMin().getX() + ( actualBBOX.getWidth() / 2d );
        double midY = actualBBOX.getMin().getY() + ( actualBBOX.getHeight() / 2d );
        double minx = midX - newWidth / 2d;
        double maxx = midX + newWidth / 2d;
        double miny = midY - newHeight / 2d;
        double maxy = midY + newHeight / 2d;
        newBBox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy,
                                                  actualBBOX.getCoordinateSystem() );
        LOG.exiting();
        return newBBox;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $$Log: ScaleSwitcherListener.java,v $
 * Changes to this class. What the people have been up to: $Revision 1.14  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: $JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to: $
 * Changes to this class. What the people have been up to: $Revision 1.13  2006/10/17 20:31:17  poth
 * Changes to this class. What the people have been up to: $*** empty log message ***
 * Changes to this class. What the people have been up to: $
 * Changes to this class. What the people have been up to: $Revision 1.12  2006/09/25 12:47:00  poth
 * Changes to this class. What the people have been up to: $bug fixes - map scale calculation
 * Changes to this class. What the people have been up to: $
 * Changes to this class. What the people have been up to: $Revision 1.11  2006/08/24 06:43:39  poth
 * Changes to this class. What the people have been up to: $File header corrected
 * Changes to this class. What the people have been up to: $
 * Changes to this class. What the people have been up to: $Revision 1.10  2006/08/16 14:01:36  ncho
 * Changes to this class. What the people have been up to: $removed  irrelevant parameters mapWidth and mapHeight from method signature of calcNewBBox
 * Changes to this class. What the people have been up to: $
 * $Revision 1.9 2006/08/10 15:36:56 ncho $refactored, added constants and properties file for pixel
 * size value $$
 **************************************************************************************************/
