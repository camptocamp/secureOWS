//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/WMSInvoker.java,v 1.24 2006/11/28 16:55:27 bezema Exp $
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wpvs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.deegree.datatypes.values.Values;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.StringTools;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetMapResult;
import org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wpvs.configuration.LocalWMSDataSource;
import org.deegree.ogcwebservices.wpvs.util.ImageUtils;
import org.deegree.ogcwebservices.wpvs.util.ResolutionStripe;

/**
 * Invoker for a Web Map Service.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.24 $, $Date: 2006/11/28 16:55:27 $
 * 
 * @since 2.0
 */
public class WMSInvoker extends GetViewServiceInvoker {

    private static final ILogger LOG = LoggerFactory.getLogger( WMSInvoker.class );

    private int id;

    /**
     * Creates a new instance of this class.
     * 
     * @param owner
     *            the handler that owns this invoker
     * @param id which can be used to sort all the request/responses in the resolutionStripe.
     */
    public WMSInvoker( ResolutionStripe owner, int id ) {
        super( owner );
        this.id = id;
    }

    @Override
    public void invokeService( AbstractDataSource dataSource ) {
        LOG.entering();

        // int count = 0;
        if ( !( dataSource != null &&
                (AbstractDataSource.LOCAL_WMS ==  dataSource.getServiceType()  || 
                AbstractDataSource.REMOTE_WMS ==  dataSource.getServiceType() ) ) ) {
            LOG.logError( "The given AbstractDataSource is no LocalWMSDataSource instance. It is needed for a WMSInvoker" );
            throw new RuntimeException( "DataSource should be an WMS-instance for a WMSInvoker" );
        }
        OGCWebService service = dataSource.getOGCWebService();
        if ( service == null ) {
            LOG.logError( "No WMS instance available for WMSInvoker" );
            throw new RuntimeException( "No WMS instance available for WMSInvoker" );
        }
        Color[] colors = ( (LocalWMSDataSource) dataSource ).getTransparentColors();
        ImageUtils imgUtil = new ImageUtils( colors );

        Object response = null;
        BufferedImage responseImage = null;
        try {
            GetMap getMapRequest = createGetMapRequest( (LocalWMSDataSource) dataSource );
            /**
             * Invoke the wms service.
             */
            response = service.doService( getMapRequest );
        } catch ( OGCWebServiceException e ) {
            LOG.logError( StringTools.concat( 500, "Error when performing WMS GetMap: ",
                                              e.getMessage() ) );
        }
        if ( response != null ) {
            if ( response instanceof GetMapResult ) {
                responseImage = (BufferedImage) ( (GetMapResult) response ).getMap();
                // LOG.logDebug( StringTools.concat( 100, "WMS RESULT: ", response ) );
                if ( responseImage != null ) {
                    if ( colors != null && colors.length > 0 ) {
                        Image tmp = imgUtil.filterImage( responseImage );
                        Graphics2D g2d = (Graphics2D) responseImage.getGraphics();
                        g2d.drawImage( tmp, 0, 0, null );
                   }
                    resolutionStripe.addTexture( ( dataSource.getName().getAsString() + id ),
                                                 responseImage );
                }
            }
        }

        LOG.exiting();
    }

    /**
     * Creates a new GetMap request for the given datasource.
     * 
     * @param ds
     *            the WMs datasource
     * @param box
     *            the surface to be used as the bouding box
     * @return a new GetMap request
     */
    private GetMap createGetMapRequest( LocalWMSDataSource ds ) {

        LOG.entering();

        GetMap getMapRequest = ds.getPartialGetMapRequest();

        Values elevation = null;
        Values time = null;
        Map<String, Values> sampleDim = null;

        // int tileSize = owner.getMaxTileSize();
        int tileWidth = resolutionStripe.getRequestWidthForBBox();
        int tileHeight = resolutionStripe.getRequestHeightForBBox();
        // GeometryUtils.getImageSizeForSurface( (RankedSurface)box, owner.getMaxTileSize());

        IDGenerator idg = IDGenerator.getInstance();
        getMapRequest = GetMap.create( getMapRequest.getVersion(), String.valueOf( idg.generateUniqueID() ),
                                       getMapRequest.getLayers(), elevation, sampleDim,
                                       getMapRequest.getFormat(), tileWidth, tileHeight,
                                       resolutionStripe.getCRSName().getAsString(),
                                       resolutionStripe.getSurface().getEnvelope(),
                                       getMapRequest.getTransparency(), getMapRequest.getBGColor(),
                                       getMapRequest.getExceptions(), time, null, null, null );
        
        return getMapRequest;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WMSInvoker.java,v $
 * Changes to this class. What the people have been up to: Revision 1.24  2006/11/28 16:55:27  bezema
 * Changes to this class. What the people have been up to:  Added support for a default splitter
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.23  2006/11/27 15:43:34  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.22  2006/11/23 11:46:14  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision 1.20
 * 2006/07/13 12:24:45 poth adaptions required according to changes in
 * org.deegree.ogcwebservice.wms.operations.GetMap
 * 
 * Revision 1.19 2006/07/04 09:06:21 taddei todo: excp handling
 * 
 * Revision 1.18 2006/06/29 19:05:40 poth ** empty log message ***
 * 
 * Revision 1.17 2006/06/20 10:16:01 taddei clean up and javadoc
 * 
 * Revision 1.16 2006/04/26 12:15:15 taddei fiddle with getImageSizeforSurfaced
 * 
 * Revision 1.15 2006/04/06 20:25:30 poth ** empty log message ***
 * 
 * Revision 1.14 2006/04/06 15:07:59 taddei added support for ValidArea
 * 
 * Revision 1.13 2006/04/05 09:07:14 taddei added code for computing res of different surfs
 * 
 * Revision 1.11 2006/03/10 10:31:40 taddei changes regarding cood sys and scale calculation
 * 
 * Revision 1.10 2006/03/02 15:23:52 taddei using now StringTools and StringBuilder
 * 
 * Revision 1.9 2006/02/22 17:12:31 taddei implemented correct drawing order
 * 
 * Revision 1.8 2006/02/22 13:36:02 taddei refactoring: added service, createOGCWebService; also
 * better except handling
 * 
 * Revision 1.7 2006/02/17 13:38:12 taddei bug fix when counting (resol was using wrong dim) and
 * fixed � (sz)
 * 
 * Revision 1.6 2006/02/14 15:20:17 taddei utf-8 correction
 * 
 * Revision 1.5 2006/02/10 16:07:02 taddei changes to accomodate remote WMS
 * 
 * Revision 1.4 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.3 2006/01/30 14:57:28 taddei implementation of transparent colors
 * 
 * Revision 1.2 2006/01/26 14:42:31 taddei WMS and WFS invokers woring; minor refactoring
 * 
 * Revision 1.1 2005/12/16 15:19:11 taddei added DeafultViewHandler and the Invokers
 * 
 * 
 **************************************************************************************************/
