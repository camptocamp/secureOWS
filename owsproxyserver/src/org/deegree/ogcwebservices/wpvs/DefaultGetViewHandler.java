//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/DefaultGetViewHandler.java,v 1.70 2006/12/04 17:06:44 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Node;
import javax.media.j3d.OrderedGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.deegree.framework.concurrent.ExecutionFinishedEvent;
import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wpvs.capabilities.Dataset;
import org.deegree.ogcwebservices.wpvs.capabilities.ElevationModel;
import org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wpvs.configuration.WPVSConfiguration;
import org.deegree.ogcwebservices.wpvs.configuration.WPVSDeegreeParams;
import org.deegree.ogcwebservices.wpvs.j3d.OffScreenWPVSRenderer;
import org.deegree.ogcwebservices.wpvs.j3d.ViewPoint;
import org.deegree.ogcwebservices.wpvs.j3d.WPVSScene;
import org.deegree.ogcwebservices.wpvs.operation.GetView;
import org.deegree.ogcwebservices.wpvs.operation.GetViewResponse;
import org.deegree.ogcwebservices.wpvs.util.QuadTreeSplitter;
import org.deegree.ogcwebservices.wpvs.util.ResolutionStripe;
import org.deegree.ogcwebservices.wpvs.util.StripeFactory;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * Default handler for WPVS GetView requests. This Class is the central position where the
 * {@link GetView} request lands the configured Datasources are gathered and the scene is put
 * together.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.70 $, $Date: 2006/12/04 17:06:44 $
 * 
 */
public class DefaultGetViewHandler extends GetViewHandler {

    static private final ILogger LOG = LoggerFactory.getLogger( DefaultGetViewHandler.class );

    private WPVSConfiguration config;

    private URL backgroundImgURL;

    private WPVSScene theScene;

    private OffScreenWPVSRenderer renderer;

    /**
     * Constructor for DefaultGetViewHandler.
     * 
     * @param owner
     *            the service creating this handler
     */
    public DefaultGetViewHandler( WPVService owner ) {
        super( owner );

        this.config = owner.getConfiguration();
    }

    /**
     * This Method handles a clients GetView request by creating the appropriate (configured)
     * Datasources, the {@link ResolutionStripe}s, the requeststripes (which are actually
     * axisalligned Resolutionsripes) and finally putting them all together in a java3d scene. The
     * creation of the Shape3D Objects (by requesting the ResolutionStripe relevant Datasources) is
     * done in separate Threads for each ResolutionStripe (which is in conflict with the deegree
     * styleguides) using the {@link Executor} class.
     * 
     * @see org.deegree.ogcwebservices.wpvs.GetViewHandler#handleRequest(org.deegree.ogcwebservices.wpvs.operation.GetView)
     */
    @Override
    public GetViewResponse handleRequest( final GetView request )
                            throws OGCWebServiceException {

        // request = req;

        validateImageSize( request );

        CoordinateSystem coordSys = request.getCrs();

        List<Dataset> validDatasets = getValidRequestDatasets( request );

        if ( validDatasets.size() == 0 ) {
            throw new OGCWebServiceException(
                                              StringTools.concat( 200, "The CRS '",
                                                                  coordSys.getAsString(),
                                                                  "' is not supported by any of the requested Datasets '" ) );

        }

        ElevationModel elevationModel = getValidElevationModel( request.getElevationModel() );

        if ( request.getFarClippingPlane() > config.getDeegreeParams().getMaximumFarClippingPlane() )
            request.setFarClippingPlane( config.getDeegreeParams().getMaximumFarClippingPlane() );

        ViewPoint viewPoint = new ViewPoint( request );
        viewPoint.setTerrainDistanceToSeaLevel( getTerrainHeightAboveSeaLevel( viewPoint.getObserverPosition() ) );

        ArrayList<ResolutionStripe> resolutionStripes = createRequestBoxes(
                                                                            request,
                                                                            viewPoint,
                                                                            config.getSmallestMinimalScaleDenomiator() );

        if ( resolutionStripes.size() == 0 ) {
            throw new OGCWebServiceException(
                                              StringTools.concat( 200,
                                                                  "There were no RequestBoxes found, therefor this WPVS-request is invalid" ) );

        }

        Surface visibleArea = viewPoint.getVisibleArea();

        findValidDataSourcesFromDatasets( validDatasets, resolutionStripes,
                                          request.getOutputFormat(), visibleArea );
        findValidEMDataSourceFromElevationModel( elevationModel, resolutionStripes, visibleArea );

        // will also check is background is valid, before doing any hard work
        this.backgroundImgURL = createBackgroundImageURL( request );

        Executor exec = Executor.getInstance();
        List<ExecutionFinishedEvent<ResolutionStripe>> resultingBoxes = null;
        try {
            resultingBoxes = exec.performSynchronously( new ArrayList<Callable<ResolutionStripe>>(
                                                                                                   resolutionStripes ) );
        } catch ( InterruptedException ie ) {
            throw new OGCWebServiceException(
                                              StringTools.concat( 200,
                                                                  "A Threading-Error occurred while placing your request." ) );

        }

        if ( resultingBoxes != null && resultingBoxes.size() > 0 ) {
            theScene = createScene( request, viewPoint, resolutionStripes );

            renderer = new OffScreenWPVSRenderer( theScene, request.getImageDimension().width,
                                                  request.getImageDimension().height );

            BufferedImage output = renderScene();

            return new GetViewResponse( output, request.getOutputFormat() );

        }
        return null;
    }

    /**
     * @param elevationModelName
     * @return an elevationModell or <tt>null</tt> if no name was given
     * @throws OGCWebServiceException
     *             if no elevationModel is found for the given elevationmodelname
     */
    private ElevationModel getValidElevationModel( String elevationModelName )
                            throws OGCWebServiceException {
        ElevationModel resultEMModel = null;
        if ( elevationModelName != null ) {
            resultEMModel = config.findElevationModel( elevationModelName );// dataset.getElevationModel();
            if ( resultEMModel == null ) {
                throw new OGCWebServiceException( StringTools.concat( 150, "ElevationModel '",
                                                                      elevationModelName,
                                                                      "' is not known to the WPVS" ) );
            }
        }
        return resultEMModel;
    }

    /**
     * Finds the datasets which can handle the requested crs's, and check if they are defined inside
     * the requested bbox
     * 
     * @param request
     *            the GetView request
     * @param coordSys
     *            the name of the requested coordsys
     * @return an ArrayList containing all the datasets which comply with the requested crs.
     * @throws OGCWebServiceException
     */
    private ArrayList<Dataset> getValidRequestDatasets( GetView request )
                            throws OGCWebServiceException {
        ArrayList<Dataset> resultDatasets = new ArrayList<Dataset>();
        Envelope bbox = request.getBoundingBox();
        List<String> datasets = request.getDatasets();
        CoordinateSystem coordSys = request.getCrs();

        // If the BoundingBox request not is
        try {
            if ( !"EPSG:4326".equalsIgnoreCase( coordSys.getAsString() ) ) {
                // transform the bounding box of the request to EPSG:4326/WGS 84
                IGeoTransformer gt = new GeoTransformer( CRSFactory.create( "EPSG:4326" ) );
                bbox = gt.transform( bbox, coordSys );
            }
        } catch ( CRSTransformationException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        } catch ( UnknownCRSException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        }

        for ( String dset : datasets ) {
            Dataset configuredDataset = config.findDataset( dset );
            if ( configuredDataset != null ) {
                CoordinateSystem[] dataSetCRS = configuredDataset.getCrs();
                for ( CoordinateSystem crs : dataSetCRS ) {
                    if ( crs.equals( coordSys ) ) {
                        // lookslike compatible crs therefor check if bbox intersect
                        if ( configuredDataset.getWgs84BoundingBox().intersects( bbox ) ) {
                            if ( !resultDatasets.contains( configuredDataset ) )
                                resultDatasets.add( configuredDataset );
                        }
                    }
                }
            }
        }

        return resultDatasets;
    }

    /**
     * Finds the valid datasources in the configured (and allready checked) datasets.
     * 
     * @param datasets
     *            the datasets which are valid for this request
     */
    private void findValidDataSourcesFromDatasets( List<Dataset> datasets,
                                                  ArrayList<ResolutionStripe> stripes,
                                                  String outputFormat, Surface visibleArea ) {
        double resolution = 0;
        for ( ResolutionStripe stripe : stripes ) {
            stripe.setOutputFormat( outputFormat );
            resolution = stripe.getMaxResolutionAsScaleDenominator();
            for ( int i = 0; i < datasets.size(); ++i ) {
                Dataset dset = datasets.get( i );
                AbstractDataSource[] dataSources = dset.getDataSources();

                for ( AbstractDataSource ads : dataSources ) {
                    System.out.println( "AbstractDataSource: " + ads );
                    if ( resolution >= dset.getMinimumScaleDenominator()
                         && resolution < dset.getMaximumScaleDenominator()
                         && resolution >= ads.getMinScaleDenominator()
                         && resolution < ads.getMaxScaleDenominator()
                         && ( ( ads.getValidArea() != null ) ? ads.getValidArea().intersects(
                                                                                              visibleArea )
                                                            : true ) ) {
                        if ( ads.getServiceType() == AbstractDataSource.LOCAL_WFS
                             || ads.getServiceType() == AbstractDataSource.REMOTE_WFS ) {
                            stripe.addFeatureCollectionDataSource( ads );
                        } else if ( ads.getServiceType() == AbstractDataSource.LOCAL_WMS
                                    || ads.getServiceType() == AbstractDataSource.REMOTE_WMS
                                    || ads.getServiceType() == AbstractDataSource.LOCAL_WCS
                                    || ads.getServiceType() == AbstractDataSource.REMOTE_WCS ) {
                            stripe.addTextureDataSource( ads );
                        }
                    }
                }
            }
        }
    }

    /**
     * @param elevationModel
     *            and it's datasources.
     */
    private void findValidEMDataSourceFromElevationModel( ElevationModel elevationModel,
                                                         ArrayList<ResolutionStripe> stripes,
                                                         Surface visibleArea ) {
        if ( elevationModel != null ) {
            AbstractDataSource[] emDataSources = elevationModel.getDataSources();
            Dataset dataset = elevationModel.getParentDataset();
            for ( ResolutionStripe stripe : stripes ) {
                double resolution = stripe.getMaxResolution();
                for ( AbstractDataSource ads : emDataSources ) {
                    if ( resolution >= dataset.getMinimumScaleDenominator()
                         && resolution < dataset.getMaximumScaleDenominator()
                         && resolution >= ads.getMinScaleDenominator()
                         && resolution < ads.getMaxScaleDenominator()
                         && ( ( ads.getValidArea() != null ) 
                                 ? ads.getValidArea().intersects( visibleArea )
                                  : true ) ) {
                        stripe.setElevationModelDataSource( ads );
                    }
                }
            }
        }
    }

    /**
     * Extracts from the request and the configuration the URL behind teh name of a given BACKGROUND
     * 
     * @return the URL, under which the background image is found
     * @throws OGCWebServiceException
     *             if no URL with the name given by 'BACKGROUND' can be found.
     */
    private URL createBackgroundImageURL( GetView request )
                            throws OGCWebServiceException {

        String imageName = request.getVendorSpecificParameter( "BACKGROUND" );
        URL imgURL = null;
        if ( imageName != null ) {
            imgURL = (URL) config.getDeegreeParams().getBackgroundMap().get( imageName );
            if ( imgURL == null ) {
                throw new OGCWebServiceException(
                                                  StringTools.concat(
                                                                      100,
                                                                      "Cannot find any image referenced by parameter BACKGROUND=",
                                                                      imageName ) );
            }
        }

        return imgURL;
    }

    /**
     * Creates a Java3D Node representing the background.
     * 
     * @param minAltitude
     *            the minimum altitude of the backgroudn plane
     * @param viewPoint
     *            the viewersposition
     * @return a new Node containing a geometry representing the background
     * @throws IOException
     */
    private Node createBackground( ViewPoint viewPoint, GetView request )
                            throws OGCWebServiceException {

        Point3d ftPrint = viewPoint.getFootprint()[0];

        Background bg = new Background( new Color3f( request.getBackgroundColor() ) );
        Point3d origin = new Point3d( ftPrint.x, ftPrint.y, ftPrint.z );
        BoundingSphere bounds = new BoundingSphere(
                                                    origin,
                                                    ftPrint.x );

        bg.setApplicationBounds( bounds );
        try {
            if ( backgroundImgURL != null ) {
                BufferedImage buffImg = ImageIO.read( backgroundImgURL );

                // scale image to fill the whole bakground
                BufferedImage tmpImg = new BufferedImage( request.getImageDimension().width,
                                                          request.getImageDimension().height,
                                                          buffImg.getType() );
                Graphics g = tmpImg.getGraphics();
                g.drawImage( buffImg, 0, 0, tmpImg.getWidth() - 1, tmpImg.getHeight() - 1, null );
                g.dispose();

                ImageComponent2D img = new TextureLoader( tmpImg ).getImage();
                bg.setImage( img );
            }
        } catch ( IOException e ) {
            throw new OGCWebServiceException(
                                              StringTools.concat(
                                                                  100,
                                                                  "Could not create backgoudn image: ",
                                                                  e.getMessage() ) );
        }

        return bg;
    }

    /**
     * Creates the request boxes from the parameters available i teh incoming request.
     * 
     * @param viewPoint
     *            where the viewer is
     * @return a new array of surfaces representing the area in which data will be collected
     */
    private ArrayList<ResolutionStripe> createRequestBoxes( GetView request, ViewPoint viewPoint,
                                                           double smallestMinimalScaleDenominator ) {
        ArrayList<ResolutionStripe> requestStripes = new ArrayList<ResolutionStripe>();
        String splittingMode = request.getVendorSpecificParameter( "SPLITTER" );
        StripeFactory stripesFactory = new StripeFactory( viewPoint,
                                                          smallestMinimalScaleDenominator );
        int imageWidth = request.getImageDimension().width;
        if ( "BBOX".equals( splittingMode ) ) {
            requestStripes = stripesFactory.createBBoxResolutionStripe(
                                                                        viewPoint.getVisibleArea().getEnvelope(),
                                                                        imageWidth,
                                                                        viewPoint.getPointOfInterest().z );
        } else {
            // Calculate the resolution stripes perpendicular to the viewdirection
            requestStripes = stripesFactory.createResolutionStripes(
                                                                     request.getImageDimension().width,
                                                                     viewPoint.getPointOfInterest().z,
                                                                     null );
            QuadTreeSplitter splittree = new QuadTreeSplitter(
                                                               requestStripes,
                                                               request.getImageDimension().width,
                                                               request.getImageDimension().height,
                                                               config.getDeegreeParams().isRequestQualityPreferred(),
                                                               null );
            requestStripes = splittree.getRequestQuads( null );
        }
        return requestStripes;
    }

    /**
     * Should return the height of the terrain above the sealevel. TODO just returns a constant
     * value.
     * 
     * @param eyePositionX
     * @param eyePositionY
     * @param eyePositionZ
     * @return the height above the terrain
     */
    protected double getTerrainHeightAboveSeaLevel( double eyePositionX, double eyePositionY,
                                                   double eyePositionZ ) {
        return getTerrainHeightAboveSeaLevel( new Point3d( eyePositionX, eyePositionY, eyePositionZ ) );
    }

    /**
     * Should return the height of the terrain above the sealevel. TODO just returns a constant
     * value.
     * 
     * @param eyePosition
     * 
     * @return the height above
     */
    protected double getTerrainHeightAboveSeaLevel( @SuppressWarnings("unused")
    Point3d eyePosition ) {
        return 0.0;
    }

    /**
     * Creates a WPVS scene
     * 
     * @param viewPoint
     *            position of the viewer
     * @return a new scene
     * @throws OGCWebServiceException
     *             if the background img cannot be read
     */
    private WPVSScene createScene( GetView request, ViewPoint viewPoint,
                                  List<ResolutionStripe> stripes )
                            throws OGCWebServiceException {

        BranchGroup scene = new BranchGroup();
        for ( ResolutionStripe stripe : stripes ) {
            scene.addChild( stripe.getJava3DRepresentation() );
        }
        Calendar date = TimeTools.createCalendar( request.getVendorSpecificParameters().get(
                                                                                             "DATETIME" ) );

        return new WPVSScene( scene, viewPoint, date, null, createBackground( viewPoint, request ) );
    }

    /**
     * Renders the scene and the resulting image.
     * 
     * @return a new image representing a screen shot of the scene
     */
    public BufferedImage renderScene() {

        BufferedImage image = renderer.renderScene();

        if ( !config.getDeegreeParams().isWatermarked() ) {
            paintCopyright( image );
        }

        return image;

    }

    /**
     * prints a copyright note at left side of the map bottom. The copyright note will be extracted
     * from the WMS capabilities/configuration
     * 
     * @param image
     *            the image onto which to print the copyright message/image
     */
    private void paintCopyright( BufferedImage image ) {

        Graphics2D g2 = (Graphics2D) image.getGraphics();

        WPVSDeegreeParams dp = config.getDeegreeParams();
        String copyright = dp.getCopyright();
        if ( config.getDeegreeParams().getCopyrightImage() != null ) {
            g2.drawImage(
                          config.getDeegreeParams().getCopyrightImage(),
                          0,
                          image.getHeight()
                                                  - config.getDeegreeParams().getCopyrightImage().getHeight(),
                          null );
        } else if ( copyright != null ) {

            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

            final int fontSize = 14;
            final int margin = 5;

            int imgHeight = image.getHeight();

            Font f = new Font( "SANSSERIF", Font.PLAIN, fontSize );
            g2.setFont( f );
            // draw text shadow
            g2.setColor( Color.black );
            g2.drawString( copyright, margin, imgHeight - margin );
            // draw text
            g2.setColor( Color.white );
            g2.drawString( copyright, margin - 1, imgHeight - margin - 1 );

        }
        g2.dispose();
    }

    /**
     * Checks if the image size is compatible with that given in the configuration
     * 
     * @throws OGCWebServiceException
     */
    private void validateImageSize( GetView request )
                            throws OGCWebServiceException {
        int width = request.getImageDimension().width;
        int maxWidth = config.getDeegreeParams().getMaxViewWidth();
        if ( width > maxWidth ) {
            throw new OGCWebServiceException(
                                              StringTools.concat(
                                                                  100,
                                                                  "Requested view width exceeds allowed maximum width of ",
                                                                  maxWidth, " pixels." ) );
        }
        int height = request.getImageDimension().height;
        int maxHeight = config.getDeegreeParams().getMaxViewHeight();
        if ( height > maxHeight ) {
            throw new OGCWebServiceException(
                                              StringTools.concat(
                                                                  100,
                                                                  "Requested view height exceeds allowed maximum height of ",
                                                                  maxHeight, " pixels." ) );
        }

    }

    /**
     * @return Returns the generated scene for this request... handy for debugging.
     */
    public WPVSScene getTheScene() {
        return theScene;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DefaultGetViewHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.70  2006/12/04 17:06:44  bezema
 * Changes to this class. What the people have been up to: enhanced dgm from wcs support
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.69  2006/11/29 16:00:11  bezema
 * Changes to this class. What the people have been up to: bug fixes and added features
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.68  2006/11/28 16:55:05  bezema
 * Changes to this class. What the people have been up to: bbox resolution works plus clean up and javadoc
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.67  2006/11/27 16:11:41  bezema
 * Changes to this class. What the people have been up to: formatting question
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.66 2006/11/27 15:35:11 bezema
 * Changes to this class. What the people have been up to: Fixed coordinatesystem stuff system
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.65 2006/11/27 11:54:02 bezema Changes to this class. What the people
 * have been up to: Cleaned up and removed a lot of member variables Changes to this class. What the
 * people have been up to: Changes to this class. What the people have been up to: Revision 1.63
 * 2006/11/23 11:46:14 bezema Changes to this class. What the people have been up to: The initial
 * version of the new wpvs Changes to this class. What the people have been up to: Revision 1.55
 * 2006/07/19 10:17:20 taddei bug fix: texture for rasters was being painted wrongly
 * 
 * Revision 1.54 2006/07/18 15:12:25 taddei changes in DEM (WCS) geometry
 * 
 * Revision 1.52 2006/07/05 14:08:04 taddei attempt to put budligns at z = 0; buggy for reasons not
 * understood Changes to this class. What the people have been up to: Revision 1.51 2006/07/05
 * 11:21:27 taddei now sets EM to zero plane (if EM == null); j3D background gets its img scaled
 * Changes to this class. What the people have been up to: Revision 1.50 2006/07/04 09:01:43 taddei
 * remove println, use img util for transparency Changes to this class. What the people have been up
 * to: Revision 1.48 2006/06/30 06:34:36 poth *** empty log message *** Changes to this class. What
 * the people have been up to: Revision 1.47 2006/06/29 19:11:40 poth performance enhancements
 * Changes to this class. What the people have been up to: Revision 1.46 2006/06/29 19:05:14 poth
 * bug fix and performance enhancements Changes to this class. What the people have been up to:
 * Revision 1.45 2006/06/29 16:50:09 poth *** empty log message *** Changes to this class. What the
 * people have been up to: Revision 1.44 2006/06/27 09:07:35 taddei renaming of vars, change in
 * GetView response Changes to this class. What the people have been up to: Revision 1.43 2006/06/20
 * 10:16:01 taddei clean up and javadoc Changes to this class. What the people have been up to:
 * Revision 1.42 2006/06/20 07:38:16 taddei check if bbox and crs are valid Changes to this class.
 * What the people have been up to: Revision 1.41 2006/05/15 09:53:52 taddei set crs of boxes
 * Changes to this class. What the people have been up to: Revision 1.40 2006/05/12 13:12:07 taddei
 * renamed splitter to simple; log debug are partially warn Changes to this class. What the people
 * have been up to: Revision 1.39 2006/05/10 14:58:19 taddei >>>>>>> 1.55 string comparison w equals
 * ignore case Changes to this class. What the people have been up to: Revision 1.38 2006/05/03
 * 20:09:52 poth Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Revision 1.37 2006/05/01 20:15:26 poth
 * *** empty log message *** Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.36 2006/04/26 12:16:19 taddei Changes to this
 * class. What the people have been up to: style formatting and max sizes Changes to this class.
 * What the people have been up to: Revision 1.35 2006/04/07 08:48:24 taddei isValidArea is checking
 * agains vis are, not with req.envelope
 * 
 * Revision 1.33 2006/04/06 15:07:59 taddei added support for ValidArea
 * 
 * Revision 1.32 2006/04/05 09:05:55 taddei added code for computing res of different surfs
 * 
 * Revision 1.30 2006/03/29 15:06:21 taddei with buildings
 * 
 * Revision 1.29 2006/03/17 13:33:31 taddei dgm/wcs working; triangulation
 * 
 * Revision 1.28 2006/03/16 11:34:30 taddei handles dem from coverages; small changes to splitters
 * (overlapping)
 * 
 * Revision 1.27 2006/03/13 12:55:29 taddei corrected threaded implementation
 * 
 * Revision 1.26 2006/03/10 15:51:42 taddei removing unsused objs and clear ops in createScene
 * 
 * Revision 1.25 2006/03/10 10:31:41 taddei changes regarding cood sys and scale calculation
 * 
 * Revision 1.24 2006/03/09 15:40:45 taddei fixed bug: counting datasets wrongly
 * 
 * Revision 1.23 2006/03/09 08:58:52 taddei splitted background code into img and J3D BG
 * 
 * Revision 1.22 2006/03/07 15:17:58 taddei splitter changes
 * 
 * Revision 1.21 2006/03/07 08:49:20 taddei changes due to pts list factories
 * 
 * Revision 1.20 2006/03/06 10:38:13 taddei improved image handling, also watermark
 * 
 * Revision 1.19 2006/03/02 15:26:08 taddei using now StringTools, also some clean up
 * 
 * Revision 1.18 2006/03/01 13:08:08 taddei commit of splitter as is, clean-up coming (AOK from AP)
 * 
 * Revision 1.17 2006/02/24 14:56:27 taddei scale par is aok
 * 
 * Revision 1.16 2006/02/24 11:42:41 taddei refactoring (background)
 * 
 * Revision 1.15 2006/02/22 17:12:31 taddei implemented correct drawing order
 * 
 * Revision 1.14 2006/02/21 14:04:44 taddei javadoc, added better positioning for background
 * 
 * Revision 1.13 2006/02/21 13:02:05 taddei small improvements
 * 
 * Revision 1.12 2006/02/21 09:29:06 taddei using flag for paiting transparent texture border
 * 
 * Revision 1.11 2006/02/17 15:40:12 taddei ** empty log message ***
 * 
 * Revision 1.10 2006/02/14 15:19:17 taddei added possibility to choose splitter
 * 
 * Revision 1.9 2006/02/10 16:08:39 taddei no comments (nothing, really)
 * 
 * Revision 1.8 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.7 2006/01/30 15:00:08 taddei working local wcs invoker; copyright info is being
 * printed
 * 
 * Revision 1.6 2006/01/26 14:40:15 taddei WFS and WMS invokers working
 * 
 * Revision 1.5 2006/01/18 08:58:13 taddei implementation (WFS)
 * 
 * Revision 1.3 2005/12/23 11:59:45 taddei removed harcoded stuff
 * 
 * Revision 1.2 2005/12/21 13:49:01 taddei dummy functions for live testing
 * 
 * Revision 1.1 2005/12/16 15:19:11 taddei added DeafultViewHandler and the Invokers
 * 
 * 
 **************************************************************************************************/
