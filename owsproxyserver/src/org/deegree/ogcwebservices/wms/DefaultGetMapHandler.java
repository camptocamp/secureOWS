//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/DefaultGetMapHandler.java,v 1.74 2006/11/30 20:04:27 poth Exp $
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
package org.deegree.ogcwebservices.wms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.batik.svggen.SVGGraphics2D;
import org.deegree.framework.concurrent.ExecutionFinishedEvent;
import org.deegree.framework.concurrent.ExecutionFinishedListener;
import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.graphics.MapFactory;
import org.deegree.graphics.Theme;
import org.deegree.graphics.optimizers.LabelOptimizer;
import org.deegree.graphics.sld.AbstractLayer;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.NamedLayer;
import org.deegree.graphics.sld.NamedStyle;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserLayer;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.i18n.Messages;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.InvalidSRSException;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceResponse;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;
import org.deegree.ogcwebservices.wms.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType;
import org.deegree.ogcwebservices.wms.configuration.WMSConfiguration_1_3_0;
import org.deegree.ogcwebservices.wms.configuration.WMSDeegreeParams;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetMapResult;
import org.deegree.ogcwebservices.wms.operation.WMSProtocolFactory;
import org.deegree.ogcwebservices.wms.operation.GetMap.Layer;
import org.w3c.dom.Element;

/**
 * 
 * 
 * @version $Revision: 1.74 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class DefaultGetMapHandler implements GetMapHandler, ExecutionFinishedListener<Object[]> {

    private static final ILogger LOG = LoggerFactory.getLogger( DefaultGetMapHandler.class );

    private static final double DEFAULT_PIXEL_SIZE = 0.00028;

    private GetMap request = null;

    private Object[] themes = null;

    private double scale = 0;

    private int count = 0;

    private CoordinateSystem reqCRS = null;

    private WMSConfigurationType configuration = null;

    private BufferedImage copyrightImg = null;

    boolean version130 = false;

    /**
     * Creates a new GetMapHandler object.
     * 
     * @param configuration
     * @param request
     *            request to perform
     */
    public DefaultGetMapHandler( WMSConfigurationType configuration, GetMap request ) {
        this.request = request;
        this.configuration = configuration;

        try {
            // get copyright image if possible
            copyrightImg = ImageUtils.loadImage( configuration.getDeegreeParams().getCopyRight() );
        } catch ( Exception e ) {
        }

    }

    /**
     * returns the configuration used by the handler
     * 
     * @return the configuration document
     */
    public WMSConfigurationType getConfiguration() {
        return configuration;
    }

    /**
     * increases the counter variable that holds the number of services that has sent a response.
     * All data are available if the counter value equals the number of requested layers.
     */
    protected synchronized void increaseCounter() {
        count++;
    }

    /**
     * performs a GetMap request and retruns the result encapsulated within a <tt>GetMapResult</tt>
     * object.
     * <p>
     * The method throws an WebServiceException that only shall be thrown if an fatal error occurs
     * that makes it imposible to return a result. If something wents wrong performing the request
     * (none fatal error) The exception shall be encapsulated within the response object to be
     * returned to the client as requested (GetMap-Request EXCEPTION-Parameter).
     * 
     * @return response to the GetMap response
     */
    public OGCWebServiceResponse performGetMap()
                            throws OGCWebServiceException {

        // some initialization is done here because the constructor is called by reflection
        // and the exceptions won't be properly handled in that case
        try {
            reqCRS = CRSFactory.create( request.getSrs().toLowerCase() );
        } catch ( Exception e ) {
            throw new InvalidSRSException(
                                           Messages.getMessage( "WMS_UNKNOWN_CRS", request.getSrs() ) );
        }

        version130 = "1.3.0".equals( request.getVersion() );

        // exceeds the max allowed map width ?
        int maxWidth = configuration.getDeegreeParams().getMaxMapWidth();
        if ( ( maxWidth != 0 ) && ( request.getWidth() > maxWidth ) ) {
            throw new InconsistentRequestException( Messages.getMessage( "WMS_EXCEEDS_WIDTH",
                                                                         new Integer( maxWidth ) ) );
        }

        // exceeds the max allowed map height ?
        int maxHeight = configuration.getDeegreeParams().getMaxMapHeight();
        if ( ( maxHeight != 0 ) && ( request.getHeight() > maxHeight ) ) {
            throw new InconsistentRequestException( Messages.getMessage( "WMS_EXCEEDS_HEIGHT",
                                                                         new Integer( maxHeight ) ) );
        }

        try {
            double pixelSize = 1;
            if ( version130 ) {
                // required because for WMS 1.3.0 'scale' represents the ScaleDenominator
                // and for WMS < 1.3.0 it represents the size of a pixel diagonal in meter
                pixelSize = DEFAULT_PIXEL_SIZE;
            }

            scale = MapUtils.calcScale( request.getWidth(), request.getHeight(),
                                        request.getBoundingBox(), reqCRS, pixelSize );

            LOG.logInfo( "OGC WMS scale: " + scale );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( Messages.getMessage( "WMS_SCALECALC" ) );
        }

        GetMap.Layer[] ls = request.getLayers();

        // if 1.3.0, check for maximum allowed layers
        if ( version130 ) {
            WMSConfiguration_1_3_0 cfg = (WMSConfiguration_1_3_0) configuration;
            if ( ls.length > cfg.getLayerLimit() ) {
                String ms = Messages.getMessage( "WMS_EXCEEDS_NUMBER", cfg.getLayerLimit() );
                throw new InconsistentRequestException( ms );
            }
        }

        ls = validateLayers( ls );

        StyledLayerDescriptor sld = toSLD( ls, request.getStyledLayerDescriptor() );
     
        AbstractLayer[] layers = sld.getLayers();

        // get the number of themes assigned to the selected layers
        // notice that there maybe more themes as there are layers because
        // 1 .. n datasources can be assigned to one layer.
        int cntTh = countNumberOfThemes( layers, scale );
        themes = new Object[cntTh];
        // invokes the data supplyer for each layer in an independ thread
        int kk = 0;
        for ( int i = 0; i < layers.length; i++ ) {

            if ( layers[i] instanceof NamedLayer ) {
                String styleName = null;
                if ( i < request.getLayers().length ) {
                    styleName = request.getLayers()[i].getStyleName();
                }

                // please note that this may be undesirable behaviour, I (schmitz) just added
                // the safety 'if' because it will throw nasty exceptions otherwise
                // (I don't know what this code actually does)
                if ( kk < cntTh ) {
                    kk = invokeNamedLayer( layers[i], kk, styleName );
                }
            } else {
                double sc = scale;
                if ( !version130 ) {
                    // required because for WMS 1.3.0 'scale' represents the ScaleDenominator
                    // and for WMS < 1.3.0 it represents the size of a pixel diagonal in meter
                    sc = scale / DEFAULT_PIXEL_SIZE;
                }
                GetMapServiceInvokerForUL si = new GetMapServiceInvokerForUL( this, (UserLayer) layers[i],
                                                                              sc, kk++ );
                new Thread( si ).start();
            }
        }

        // TODO
        // substitue by an event based approach
        waitForFinished();

        GetMapResult res = renderMap();

        return res;
    }

    /**
     * this methods validates layer in two ways:<br>
     * a) are layers available from the current WMS<br>
     * b) If a layer is selected that includes other layers determine all its sublayers having
     * <Name>s and return them instead
     * 
     * @param ls
     * @return the layers
     * @throws LayerNotDefinedException
     */
    private Layer[] validateLayers( Layer[] ls )
                            throws LayerNotDefinedException {

        List<Layer> layer = new ArrayList<Layer>( ls.length );
        for ( int i = 0; i < ls.length; i++ ) {
            org.deegree.ogcwebservices.wms.capabilities.Layer l = 
                configuration.getLayer( ls[i].getName() );

            if ( l == null ) {
                throw new LayerNotDefinedException( Messages.getMessage( "WMS_UNKNOWNLAYER",
                                                                         ls[i].getName() ) );
            }

            layer.add( ls[i] );
            if ( l.getLayer() != null ) {
                layer = addNestedLayers( l.getLayer(), ls[i].getStyleName(), layer );
            }
        }

        return layer.toArray( new Layer[layer.size()] );
    }

    /**
     * adds all direct and none direct sub-layers of the passed WMS capabilities layer as
     * 
     * @see GetMap.Layer to the passed list.
     * @param layer
     * @param reqLayer
     * @param list
     * @return all sublayers
     */
    private List<Layer> addNestedLayers( org.deegree.ogcwebservices.wms.capabilities.Layer[] ll,
                                        String styleName, List<Layer> list ) {

        for ( int j = 0; j < ll.length; j++ ) {
            if ( ll[j].getName() != null ) {
                list.add( GetMap.createLayer( ll[j].getName(), styleName ) );
            }
            if ( ll[j].getLayer() != null ) {
                list = addNestedLayers( ll[j].getLayer(), styleName, list );
            }

        }
        return list;
    }

    /**
     * @param layers
     * @param kk
     * @param i
     * @return a counter, kk + 1 I guess
     * @throws OGCWebServiceException
     */
    private int invokeNamedLayer( AbstractLayer layer, int kk, String styleName )
                            throws OGCWebServiceException {

        org.deegree.ogcwebservices.wms.capabilities.Layer lay = configuration.getLayer( layer.getName() );

        if ( validate( lay, layer.getName() ) ) {

            UserStyle us = getStyles( (NamedLayer) layer, styleName );
            AbstractDataSource[] ds = lay.getDataSource();

            for ( int j = 0; j < ds.length; j++ ) {

                ScaleHint scaleHint = ds[j].getScaleHint();
                if ( scale >= scaleHint.getMin() && scale < scaleHint.getMax()
                     && isValidArea( ds[j].getValidArea() ) ) {
                    double sc = scale;
                    if ( !version130 ) {
                        // required because for WMS 1.3.0 'scale' represents the ScaleDenominator
                        // and for WMS < 1.3.0 it represents the size of a pixel diagonal in meter
                        sc = scale / DEFAULT_PIXEL_SIZE;
                    }
                    GetMapServiceInvokerForNL si = new GetMapServiceInvokerForNL(
                                                                                  this,
                                                                                  (NamedLayer) layer,
                                                                                  ds[j], us, sc,
                                                                                  kk++ );
                    MapServiceTask task = new MapServiceTask( si );
                    try {
                        Executor.getInstance().performAsynchronously( task, this );
                    } catch ( Exception e ) {
                        LOG.logDebug( e.getMessage(), e );
                        throw new OGCWebServiceException( getClass().getName(), e.getMessage() );
                    }
                } else {
                    LOG.logDebug( Messages.getMessage( "WMS_LAYER_NOT_SHOWN", layer.getName() ) );
                }
            }
        } else {
            // set theme to null if no data are available for the requested
            // area and/or scale. This will cause this index position will be ignored
            // when creating the final result
            themes[kk++] = null;
            increaseCounter();
        }
        return kk;
    }

    /**
     * returns the number of <code>DataSource</code>s involved in a GetMap request
     * 
     * @param layers
     * @param scale
     * @return the number of themes
     * @throws OGCWebServiceException 
     * @throws OGCWebServiceException 
     */
    private int countNumberOfThemes( AbstractLayer[] layers, double scale )
                            throws OGCWebServiceException {
        int cnt = 0;

        for ( int i = 0; i < layers.length; i++ ) {
            if ( layers[i] instanceof NamedLayer ) {
                org.deegree.ogcwebservices.wms.capabilities.Layer lay = configuration.getLayer( layers[i].getName() );
                validate( lay, layers[i].getName() );
                if ( lay != null ) {
                    AbstractDataSource[] ds = lay.getDataSource();
                    for ( int j = 0; j < ds.length; j++ ) {
                        ScaleHint scaleHint = ds[j].getScaleHint();
                        if ( scale >= scaleHint.getMin() && scale < scaleHint.getMax()
                             && isValidArea( ds[j].getValidArea() ) ) {
                            cnt++;
                        }
                    }
                }
            } else {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * returns true if the requested boundingbox intersects with the valid area of a datasource
     * 
     * @param validArea
     */
    private boolean isValidArea( Geometry validArea ) {

        if ( validArea != null ) {
            try {
                Envelope env = request.getBoundingBox();
                Geometry geom = GeometryFactory.createSurface( env, reqCRS );
                if ( !reqCRS.getName().equals( validArea.getCoordinateSystem().getName() ) ) {
                    // if requested CRS is not identical to the CRS of the valid area
                    // a transformation must be performed before intersection can
                    // be checked
                    IGeoTransformer gt = new GeoTransformer( validArea.getCoordinateSystem() );
                    geom = gt.transform( geom );
                }
                return geom.intersects( validArea );
            } catch ( Exception e ) {
                // should never happen
                LOG.logError( Messages.getMessage( "WMS_VALIDATE_DATASOURCE" ), e );
            }
        }
        return true;
    }

    /**
     * runs a loop until all sub requestes (one for each layer) has been finished or the maximum
     * time limit has been exceeded.
     * 
     * @throws OGCWebServiceException
     */
    private void waitForFinished()
                            throws OGCWebServiceException {
        if ( count < themes.length ) {
            // waits until the requested layers are available as <tt>DisplayElements</tt>
            // or the time limit has been reached.
            // if count == themes.length then no request must be performed
            long timeStamp = System.currentTimeMillis();
            long lapse = 0;
            long timeout = 1000 * ( configuration.getDeegreeParams().getRequestTimeLimit() - 1 );
            do {
                try {
                    Thread.sleep( 50 );
                    lapse += 50;
                } catch ( InterruptedException e ) {
                    LOG.logError( e.getMessage(), e );
                    String s = Messages.getMessage( "WMS_WAITING" );
                    throw new OGCWebServiceException( getClass().getName(), s );
                }
            } while ( count < themes.length && lapse < timeout );
            if ( System.currentTimeMillis() - timeStamp >= timeout ) {
                String s = Messages.getMessage( "WMS_TIMEOUT" );
                LOG.logError( s );
                throw new OGCWebServiceException( getClass().getName(), s );
            }
        }
    }
    
    /**
     * creates a StyledLayerDocument containing all requested layer,
     * nested layers if required and assigend styles. Not considered
     * are nested layers for mixed requests (LAYERS- and SLD(_BODY)-
     * parameter has been defined)
     * 
     * @param layers
     * @param inSLD
     * @return
     */
    private StyledLayerDescriptor toSLD( GetMap.Layer[] layers, StyledLayerDescriptor inSLD ) {
        StyledLayerDescriptor sld = null;

        if ( layers != null && layers.length > 0 && inSLD == null ) {            
            // if just a list of layers has been requested
            
            // create a SLD from the requested LAYERS and assigned STYLES         
            List<AbstractLayer> al = new ArrayList<AbstractLayer>( layers.length * 2 );
            for ( int i = 0; i < layers.length; i++ ) {
                AbstractStyle[] as = new AbstractStyle[] { new NamedStyle( layers[i].getStyleName() ) };
                al.add( new NamedLayer( layers[i].getName(), null, as ) );
                // collect all named nested layers 
                org.deegree.ogcwebservices.wms.capabilities.Layer lla = 
                    configuration.getLayer( layers[i].getName() );
                List<GetMap.Layer> list = new ArrayList<GetMap.Layer>();
                addNestedLayers( lla.getLayer(), layers[i].getStyleName(), list );
                // add nested layers to list of layers to be handled
                for ( int j = 0; j < list.size(); j++ ) {
                    GetMap.Layer nestedLayer = list.get( j ); 
                    as = new AbstractStyle[] { new NamedStyle( nestedLayer.getStyleName() ) };
                    al.add( new NamedLayer( nestedLayer.getName(), null, as ) );
                }
            }            
            sld = new StyledLayerDescriptor( al.toArray( new AbstractLayer[al.size()] ), "1.0.0" );
        } else if ( layers != null && layers.length > 0 && inSLD != null ) {
            // if layers not null and sld is not null then SLD layers just be
            // considered if present in the layers list
            // TODO
            // layer with nested layers are not handled correctly and I think
            // it really causes a lot of problems to use them in such a way
            // because the style assigned to the mesting layer must be 
            // applicable for all nested layers.
            List<String> list = new ArrayList<String>();
            for ( int i = 0; i < layers.length; i++ ) {
                list.add( layers[i].getName() );
            }

            List<AbstractLayer> newList = new ArrayList<AbstractLayer>( 20 );
            AbstractLayer[] al = inSLD.getLayers();
            for ( int i = 0; i < al.length; i++ ) {
                if ( list.contains( al[i].getName() ) ) {
                    newList.add( al[i] );
                }
            }
            al = new AbstractLayer[newList.size()];
            sld = new StyledLayerDescriptor( newList.toArray( al ), inSLD.getVersion() );
        } else {
            // if no layers but a SLD is defined ...
            sld = inSLD;
        }

        return sld;
    }
        
    /**
     * returns the <tt>UserStyle</tt>s assigned to a named layer
     * 
     * @param sldLayer
     *            layer to get the styles for
     * @param styleName
     *            requested stylename (from the KVP encoding)
     */
    private UserStyle getStyles( NamedLayer sldLayer, String styleName )
                            throws OGCWebServiceException {

        AbstractStyle[] styles = sldLayer.getStyles();
        UserStyle us = null;

        // to avoid retrieving the layer again for each style
        org.deegree.ogcwebservices.wms.capabilities.Layer layer = null;
        layer = configuration.getLayer( sldLayer.getName() );
        int i = 0;
        while ( us == null && i < styles.length ) {
            if ( styles[i] instanceof NamedStyle ) {
                // styles will be taken from the WMS's style repository
                us = getPredefinedStyle( styles[i].getName(), sldLayer.getName(), layer );
            } else {
                // if the requested style fits the name of the defined style or
                // if the defined style is marked as default and the requested
                // style if 'default' the condition is true. This includes that
                // if more than one style with the same name or more than one
                // style is marked as default always the first will be choosen
                if ( styleName == null
                     || ( styles[i].getName() != null && styles[i].getName().equals( styleName ) )
                     || ( styleName.equalsIgnoreCase( "$DEFAULT" ) && ( (UserStyle) styles[i] ).isDefault() ) ) {
                    us = (UserStyle) styles[i];
                }
            }
            i++;
        }
        if ( us == null ) {
            // this may happens if the SLD contains a named layer but not
            // a style! yes this is valid according to SLD spec 1.0.0
            us = getPredefinedStyle( styleName, sldLayer.getName(), layer );
        }
        return us;
    }

    /**
     * 
     * @param styleName
     * @param layerName
     * @param layer
     * @return the style
     * @throws StyleNotDefinedException
     */
    private UserStyle getPredefinedStyle( String styleName, String layerName,
                                         org.deegree.ogcwebservices.wms.capabilities.Layer layer )
                            throws StyleNotDefinedException {
        UserStyle us = null;

        if ( "default".equals( styleName ) ) {
            us = layer.getStyle( styleName );
        }

        if ( us == null ) {
            if ( styleName == null || styleName.length() == 0 || styleName.equals( "$DEFAULT" )
                 || styleName.equals( "default" ) ) {
                styleName = "default:" + layerName;
            }
        }

        us = layer.getStyle( styleName );
        if ( us == null && !( styleName.startsWith( "default" ) )
             && !( styleName.startsWith( "$DEFAULT" ) ) ) {
            String s = Messages.getMessage( "WMS_STYLENOTDEFINED", styleName, layer );
            throw new StyleNotDefinedException( s );
        }
        return us;
    }

    /**
     * validates if the requested layer matches the conditions of the request if not a
     * <tt>WebServiceException</tt> will be thrown. If the layer matches the request, but isn't
     * able to deviever data for the requested area and/or scale false will be returned. If the
     * layer matches the request and contains data for the requested area and/or scale true will be
     * returned.
     * 
     * @param layer
     *            layer as defined at the capabilities/configuration
     * @param name
     *            name of the layer (must be submitted seperatly because the layer parameter can be
     *            <tt>null</tt>
     */
    private boolean validate( org.deegree.ogcwebservices.wms.capabilities.Layer layer, String name )
                            throws OGCWebServiceException {

        // check if layer is available
        if ( layer == null ) {
            throw new LayerNotDefinedException( Messages.getMessage( "WMS_UNKNOWNLAYER", name ) );
        }

        // check scale
        ScaleHint scaleHint = layer.getScaleHint();
        if ( scale < scaleHint.getMin() || scale > scaleHint.getMax() ) {
            LOG.logDebug( Messages.getMessage( "WMS_LAYER_NOT_SHOWN", layer.getName() ) );
            return false;
        }

        // check bounding box
        try {
            Envelope bbox = request.getBoundingBox();
            Envelope layerBbox = layer.getLatLonBoundingBox();
            if ( !request.getSrs().equalsIgnoreCase( "EPSG:4326" ) ) {
                // transform the bounding box of the request to EPSG:4326
                IGeoTransformer gt = new GeoTransformer( CRSFactory.create( "epsg:4326" ) );
                bbox = gt.transform( bbox, reqCRS );
            }
            if ( !bbox.intersects( layerBbox ) ) {
                return false;
            }

        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( Messages.getMessage( "WMS_BBOXCOMPARSION" ) );
        }

        return true;
    }

    /**
     * put a theme to the passed index of the themes array. The second param passed is a
     * <tt>Theme</tt> or an exception
     */

    protected synchronized void putTheme( int index, Object o ) {
        themes[index] = o;
    }

    /**
     * will be called each time a datasource has been read
     * 
     * @param finishedEvent
     */
    public synchronized void executionFinished( ExecutionFinishedEvent<Object[]> finishedEvent ) {
        Object[] o = null;
        try {
            o = finishedEvent.getResult();
        } catch ( Throwable t ) {
            String msg = Messages.getMessage( "WMS_ASYNC_TASK_ERROR", t.getMessage() );
            LOG.logError( msg, t );
        }
        themes[( (Integer) o[0] ).intValue()] = o[1];
        increaseCounter();
    }

    /**
     * renders the map from the <tt>DisplayElement</tt>s
     */
    private GetMapResult renderMap() {

        GetMapResult response = null;
        OGCWebServiceException exce = null;

        ArrayList<Object> list = new ArrayList<Object>( 50 );
        for ( int i = 0; i < themes.length; i++ ) {
            if ( themes[i] instanceof Exception ) {
                exce = new OGCWebServiceException( getClass().getName(), themes[i].toString() );
            }
            if ( themes[i] instanceof OGCWebServiceException ) {
                exce = (OGCWebServiceException) themes[i];
                break;
            }
            if ( themes[i] != null ) {
                list.add( themes[i] );
            }
        }

        String mime = MimeTypeMapper.toMimeType( request.getFormat() );

        // get target object for rendering
        Object target = GraphicContextFactory.createGraphicTarget( mime, request.getWidth(),
                                                                   request.getHeight() );
        // get graphic context of the target
        Graphics g = GraphicContextFactory.createGraphicContext( mime, target );

        if ( exce == null ) {
            // only if no exception occured
            try {
                Theme[] th = list.toArray( new Theme[list.size()] );
                org.deegree.graphics.MapView map = null;
                if ( th.length > 0 ) {
                    map = MapFactory.createMapView( "deegree WMS", request.getBoundingBox(),
                                                    reqCRS, th );
                }
                g.setClip( 0, 0, request.getWidth(), request.getHeight() );
                if ( !request.getTransparency() ) {
                    g.setColor( request.getBGColor() );
                    g.fillRect( 0, 0, request.getWidth(), request.getHeight() );
                }
                if ( map != null ) {
                    Theme[] themes = map.getAllThemes();
                    map.addOptimizer( new LabelOptimizer( themes ) );
                    // antialiasing must be switched of for gif output format
                    // because the antialiasing may create more than 255 colors
                    // in the map/image, even just a few colors are defined in
                    // the styles
                    if ( !request.getFormat().equalsIgnoreCase( "image/gif" ) ) {
                        if ( configuration.getDeegreeParams().isAntiAliased() ) {
                            ( (Graphics2D) g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                                                                 RenderingHints.VALUE_ANTIALIAS_ON );
                            ( (Graphics2D) g ).setRenderingHint(
                                                                 RenderingHints.KEY_TEXT_ANTIALIASING,
                                                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                        }
                    }
                    map.paint( g );
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                exce = new OGCWebServiceException( "GetMapHandler_Impl: renderMap", e.toString() );
            }
        }

        // print a copyright note at the left lower corner of the map
        printCopyright( g, request.getHeight() );

        if ( mime.equals( "image/svg+xml" ) || mime.equals( "image/svg xml" ) ) {
            Element root = ( (SVGGraphics2D) g ).getRoot();
            root.setAttribute( "xmlns:xlink", "http://www.w3.org/1999/xlink" );
            response = WMSProtocolFactory.createGetMapResponse( request, exce, root );
        } else {
            response = WMSProtocolFactory.createGetMapResponse( request, exce, target );
        }
        g.dispose();

        return response;
    }

    /**
     * prints a copyright note at left side of the map bottom. The copyright note will be extracted
     * from the WMS capabilities/configuration
     * 
     * @param g
     *            graphic context of the map
     * @param heigth
     *            height of the map in pixel
     */
    private void printCopyright( Graphics g, int heigth ) {
        WMSDeegreeParams dp = configuration.getDeegreeParams();
        String copyright = dp.getCopyRight();
        if ( copyrightImg != null ) {
            g.drawImage( copyrightImg, 8, heigth - copyrightImg.getHeight() - 5, null );
        } else {
            if ( copyright != null ) {
                g.setFont( new Font( "SANSSERIF", Font.PLAIN, 14 ) );
                g.setColor( Color.BLACK );
                g.drawString( copyright, 8, heigth - 15 );
                g.drawString( copyright, 10, heigth - 15 );
                g.drawString( copyright, 8, heigth - 13 );
                g.drawString( copyright, 10, heigth - 13 );
                g.setColor( Color.WHITE );
                g.setFont( new Font( "SANSSERIF", Font.PLAIN, 14 ) );
                g.drawString( copyright, 9, heigth - 14 );
            }
        }

    }

    // ///////////////////////////////////////////////////////////////////////////
    // inner classes //
    // ///////////////////////////////////////////////////////////////////////////

    private class MapServiceTask implements Callable<Object[]> {

        GetMapServiceInvokerForNL invoker;

        MapServiceTask( GetMapServiceInvokerForNL invoker ) {
            this.invoker = invoker;
        }

        public Object[] call()
                                throws Exception {
            return (Object[]) this.invoker.run();
        }
    }

    /**
     * @return the request that is being handled
     */
    protected GetMap getRequest() {
        return request;
    }

    /**
     * @return the requests coordinate system
     */
    protected CoordinateSystem getRequestCRS() {
        return reqCRS;
    }

}
/* *************************************************************************************************
 $Log: DefaultGetMapHandler.java,v $
 Revision 1.74  2006/11/30 20:04:27  poth
 bug fix - handling SLD GetMap requests

 Revision 1.73  2006/11/29 21:28:30  poth
 bug fixing - SLD GetMap requests containing user layers with featuretypeconstraints

 Revision 1.72  2006/11/29 14:10:24  schmitz
 Added more messages. The Default handlers should now be fully externalized.

 Revision 1.71  2006/11/29 13:00:36  schmitz
 Cleaned up WMS messages.

 Revision 1.70  2006/11/29 12:28:19  schmitz
 Removed unnecessary test for CRS in validate().

 Revision 1.69  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.67  2006/11/24 10:52:40  poth
 proj reference removed

 Revision 1.66  2006/11/24 09:33:12  schmitz
 Fixed a bug concerning layer specific scale hints.
 Using the central i18n mechanism.
 Changed the localwfs mechanism to just use one WFS and not recreate them.

 Revision 1.65  2006/11/17 16:44:27  schmitz
 Removed debugging output.

 Revision 1.64  2006/11/17 16:38:31  schmitz
 Added support for GetMap POST requests.
 Sublayers of requested layers specified in SLD are now added as well.

 Revision 1.63  2006/10/27 09:52:23  schmitz
 Brought the WMS up to date regarding 1.1.1 and 1.3.0 conformance.
 Fixed a bug while creating the default GetLegendGraphics URLs.

 Revision 1.62  2006/10/17 20:31:17  poth
 *** empty log message ***

 Revision 1.61  2006/10/10 13:46:02  schmitz
 Updated the WMS GetMap and GetFeatureInfo requests to handle sublayers of layers as well.

 Revision 1.60  2006/10/07 15:06:08  poth
 infomation about requested wms version made global

 Revision 1.59  2006/10/04 07:43:38  poth
 bug fix

 Revision 1.58  2006/09/27 16:46:41  poth
 transformation method signature changed

 Revision 1.57  2006/09/26 14:22:04  poth
 bug fix

 Revision 1.56  2006/09/25 20:32:08  poth
 pass scale to GetMapServiceInvokerNL

 Revision 1.55  2006/09/25 12:47:00  poth
 bug fixes - map scale calculation

 Revision 1.54  2006/09/15 09:38:51  schmitz
 Removed debugging output.

 Revision 1.53  2006/09/15 09:18:29  schmitz
 Updated WMS to use SLD or SLD_BODY sld documents as default when also giving
 LAYERS and STYLES parameters at the same time.

 Revision 1.52  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.51  2006/08/09 15:26:55  poth
 bug fix - case sensitivity of crs corrected

 Revision 1.50  2006/08/08 09:57:11  mschneider
 Added generics for type safety - constrained the return type of tasks.

 Revision 1.49 2006/08/07 13:52:05 mschneider
 Refactored due to changes in
 deegree.framework.concurrent package.
 
 Revision 1.48 2006/07/29 08:55:06 poth
 implementation changed to new org.deegree.framework.concurrent packeage
 
 Revision 1.47 2006/07/28 08:01:27 schmitz
 Updated the WMS for 1.1.1 compliance. Fixed some
 documentation.
 
 Revision 1.46 2006/07/12 14:46:17 poth comment footer added
 
 ************************************************************************************************ */
