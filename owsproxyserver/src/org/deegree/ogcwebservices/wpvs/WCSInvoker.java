//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/WCSInvoker.java,v 1.31 2006/12/04 17:06:44 bezema Exp $
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
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.TransposeDescriptor;

import org.deegree.datatypes.Code;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.IDGenerator;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wcs.WCSException;
import org.deegree.ogcwebservices.wcs.WCService;
import org.deegree.ogcwebservices.wcs.getcoverage.DomainSubset;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.Output;
import org.deegree.ogcwebservices.wcs.getcoverage.ResultCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.SpatialSubset;
import org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wpvs.configuration.LocalWCSDataSource;
import org.deegree.ogcwebservices.wpvs.util.ImageUtils;
import org.deegree.ogcwebservices.wpvs.util.ResolutionStripe;

/**
 * Invoker for a Web Coverage Service.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.31 $, $Date: 2006/12/04 17:06:44 $
 * 
 * @since 2.0
 */
public class WCSInvoker extends GetViewServiceInvoker {

    private static final ILogger LOG = LoggerFactory.getLogger( WCSInvoker.class );

    /* the rank represent the order in whoch the image will be painted */
    private int id;

    /* whether the image will be used as texture or as data for the elevation model */
    private final boolean isElevationModelRequest;

    private String requestFormat;

    /**
     * Creates a new instance of this class.
     * 
     * @param owner
     *            the handler that owns this invoker
     * @param id
     * @param requestFormat 
     * @param isElevationModelRequest
     */
    public WCSInvoker( ResolutionStripe owner, int id, String requestFormat, boolean isElevationModelRequest ) {
        super( owner );
        this.id = id;
        this.isElevationModelRequest = isElevationModelRequest;
        this.requestFormat = requestFormat;
    }

    @Override
    public void invokeService( AbstractDataSource dataSource ) {
        LOG.entering();

        if ( !( dataSource instanceof LocalWCSDataSource ) ) {
            LOG.logError( "The given AbstractDataSource is no WCSDataSource instance. It is needed for a WCSInvoker" );
            throw new RuntimeException( "DataSource should be a WCS-instance for a WCSInvoker" );
        }

        WCService service = (WCService) dataSource.getOGCWebService();

        if ( service == null ) {
            throw new RuntimeException( "No Web Coverage Service instance available for WCSInvoker" );
        }

        Color[] colors = ( (LocalWCSDataSource) dataSource ).getTransparentColors();
        ImageUtils imgUtil = new ImageUtils( colors );

        Object coverageResponse = null;
        GetCoverage getCoverageRequest;
        try {
            getCoverageRequest = createGetCoverageRequest( ( (LocalWCSDataSource) dataSource ).getCoverageFilterCondition() );
            coverageResponse = service.doService( getCoverageRequest );
        } catch ( WCSException e ) {
            LOG.logWarning( "Error performing WCS GetCoverage", e );
        } catch ( OGCWebServiceException e ) {
            LOG.logWarning( "OGCWebServiceException when performing GetCoverage: ", e );
        }
        if ( coverageResponse != null && coverageResponse instanceof ResultCoverage ) {

            LOG.logDebug( "\t -> a valid response\n");
            
            ImageGridCoverage igc = (ImageGridCoverage) ( (ResultCoverage) coverageResponse ).getCoverage();
            BufferedImage image = igc.getAsImage( resolutionStripe.getRequestWidthForBBox(), resolutionStripe.getRequestHeightForBBox() );

            if ( !isElevationModelRequest ) {
                if ( colors != null && colors.length > 0 ) {
                    Image tmp = imgUtil.filterImage( image );
                    Graphics2D g2d = (Graphics2D) image.getGraphics();
                    g2d.drawImage( tmp, 0, 0, null );
                }

                if( ! resolutionStripe.addTexture( dataSource.getName().getAsString() + id, image ) )
                    LOG.logDebug("could not add the texture");
            } else {
                //the heightdata is in x and -y coordinates, they must be flipped before using
                PlanarImage im2 = JAI.create("transpose", image, TransposeDescriptor.FLIP_VERTICAL);
                resolutionStripe.setElevationModelFromHeightMap( im2.getAsBufferedImage() );
            }
        } else {
            System.out.println( "coverage result: " + coverageResponse );
            // TODO exception handling
            // UT: the problem here is that view trapeze might very easily
            // go out of valid WCS regions. So I found it better to silently ingnore
            // excepts.
            LOG.logWarning( "WCS request is null or not a ResultCoverage: " + coverageResponse );
        }

        LOG.exiting();
    }

    /**
     * Creates a getCoverage request for the given surface
     * 
     * @param surface
     *            teh surface to be used as the bouding box
     * @return a new GetCoverageRequest.
     * @throws WCSException
     * @throws OGCWebServiceException
     */
    private GetCoverage createGetCoverageRequest( GetCoverage filterCondition )
                            throws WCSException, OGCWebServiceException {
        LOG.entering();

        String format = "GeoTiff";

        if ( !isElevationModelRequest ) {
            format = requestFormat;
            int pos = format.indexOf( '/' );
            if ( pos > -1 ) {
                format = format.substring( pos + 1, format.length() );
            }

            if ( format.indexOf( "svg" ) > -1 ) {
                format = "png";
            }
        }
        Output output = GetCoverage.createOutput( resolutionStripe.getCRSName().getAsString(), null, format, null );
        
        // put mising parts in this map:
        Map<String, String> map = new HashMap<String, String>( 5 );

        int tileWidth = resolutionStripe.getRequestWidthForBBox();
        int tileHeight = resolutionStripe.getRequestHeightForBBox();
        

        map.put( "WIDTH", String.valueOf( tileWidth ) );
        map.put( "HEIGHT", String.valueOf( tileHeight ) );

        StringBuffer sb = new StringBuffer( 1000 );
        Envelope env = resolutionStripe.getSurface().getEnvelope();
        Position p = env.getMin();
        sb.append( p.getX() ).append( "," ).append( p.getY() ).append( "," );
        p = env.getMax();
        sb.append( p.getX() ).append( "," ).append( p.getY() );
        map.put( "BBOX", sb.toString() );
        
        System.out.println( "BBOX: " + sb.toString() + ", resolutionStripeResolution: " + resolutionStripe.getMaxResolution() + ", tileWidth: " + tileWidth + ", tileHeight: " + tileHeight + " - " + env.getWidth()/env.getHeight() + " " + tileWidth/(float)tileHeight );
        
        SpatialSubset sps = GetCoverage.createSpatialSubset( map, resolutionStripe.getCRSName().getAsString() );

        Code code = filterCondition.getDomainSubset().getRequestSRS();
        DomainSubset domainSubset = new DomainSubset( code, sps, null );

        IDGenerator idg = IDGenerator.getInstance();

        GetCoverage getCoverageRequest = new GetCoverage( String.valueOf( idg.generateUniqueID() ),
                                                          filterCondition.getVersion(),
                                                          filterCondition.getSourceCoverage(),
                                                          domainSubset, null,
                                                          filterCondition.getInterpolationMethod(),
                                                          output );

        LOG.exiting();
        return getCoverageRequest;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WCSInvoker.java,v $
 * Changes to this class. What the people have been up to: Revision 1.31  2006/12/04 17:06:44  bezema
 * Changes to this class. What the people have been up to: enhanced dgm from wcs support
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.30  2006/11/30 11:26:27  bezema
 * Changes to this class. What the people have been up to: working on the raster heightmap elevationmodel
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.29  2006/11/29 16:00:11  bezema
 * Changes to this class. What the people have been up to: bug fixes and added features
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.28  2006/11/28 16:55:27  bezema
 * Changes to this class. What the people have been up to:  Added support for a default splitter
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.27  2006/11/27 15:43:34  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.26  2006/11/23 11:46:14  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision 1.24
 * 2006/08/07 13:38:37 poth never used import removed
 * 
 * Revision 1.23 2006/07/18 15:13:32 taddei changes in DEM (WCS) geometry
 * 
 * Revision 1.22 2006/07/04 09:03:27 taddei getImage call done once
 * 
 * Revision 1.20 2006/06/29 19:05:33 poth bug fix and performance enhancements
 * 
 * Revision 1.19 2006/06/20 10:16:01 taddei clean up and javadoc
 * 
 * Revision 1.18 2006/06/20 07:39:39 taddei added parent dataset
 * 
 * Revision 1.17 2006/04/26 12:15:26 taddei fiddle with getImageSizeforSurfaced
 * 
 * Revision 1.16 2006/04/06 20:25:30 poth ** empty log message ***
 * 
 * Revision 1.15 2006/04/06 15:07:59 taddei added support for ValidArea
 * 
 * Revision 1.14 2006/04/05 09:07:14 taddei added code for computing res of different surfs
 * 
 * Revision 1.12 2006/03/17 13:33:31 taddei dgm/wcs working; triangulation
 * 
 * Revision 1.11 2006/03/16 11:33:42 taddei takes care now of texture *and* dems
 * 
 * Revision 1.10 2006/03/10 10:31:40 taddei changes regarding cood sys and scale calculation
 * 
 * Revision 1.9 2006/03/02 15:23:52 taddei using now StringTools and StringBuilder
 * 
 * Revision 1.8 2006/02/22 17:12:31 taddei implemented correct drawing order
 * 
 * Revision 1.7 2006/02/22 13:36:02 taddei refactoring: added service, createOGCWebService; also
 * better except handling
 * 
 * Revision 1.6 2006/02/17 13:38:12 taddei bug fix when counting (resol was using wrong dim) and
 * fixed � (sz)
 * 
 * Revision 1.5 2006/02/14 15:19:46 taddei bug fix in regarding calc resolution
 * 
 * Revision 1.4 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.3 2006/01/30 14:58:58 taddei commit of working invoker
 * 
 * Revision 1.2 2006/01/26 14:42:31 taddei WMS and WFS invokers woring; minor refactoring
 * 
 * Revision 1.1 2005/12/16 15:19:11 taddei added DeafultViewHandler and the Invokers
 * 
 * 
 **************************************************************************************************/
