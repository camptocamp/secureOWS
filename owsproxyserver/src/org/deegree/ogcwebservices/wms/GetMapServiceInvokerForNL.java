//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/GetMapServiceInvokerForNL.java,v 1.51 2006/12/03 21:21:22 poth Exp $
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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.concurrent.DoServiceTask;
import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.MapFactory;
import org.deegree.graphics.Theme;
import org.deegree.graphics.sld.NamedLayer;
import org.deegree.graphics.sld.StyleUtils;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.i18n.Messages;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.FeatureFilter;
import org.deegree.model.filterencoding.FeatureId;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wcs.WCSException;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.ResultCoverage;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wms.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wms.configuration.LocalWCSDataSource;
import org.deegree.ogcwebservices.wms.configuration.LocalWFSDataSource;
import org.deegree.ogcwebservices.wms.configuration.RemoteWMSDataSource;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetMapResult;
import org.opengis.coverage.grid.GridCoverage;
import org.w3c.dom.Document;

/**
 * Class for accessing the data of one layers datasource and creating a <tt>Theme</tt> from it.
 * 
 * @version $Revision: 1.51 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.51 $, $Date: 2006/12/03 21:21:22 $
 * 
 * @since 2.0
 */
public class GetMapServiceInvokerForNL extends GetMapServiceInvoker {

    private static final ILogger LOG = LoggerFactory.getLogger( GetMapServiceInvokerForNL.class );

    private final GetMap request;

    private NamedLayer layer = null;

    private UserStyle style = null;

    private AbstractDataSource datasource = null;

    /**
     * Creates a new ServiceInvokerForNL object.
     * 
     * @param handler
     * @param layer
     * @param datasource
     * @param style
     * @param index
     */
    GetMapServiceInvokerForNL( DefaultGetMapHandler handler, NamedLayer layer,
                              AbstractDataSource datasource, UserStyle style, double scale,
                              int index ) {

        super( handler, index, scale );

        this.layer = layer;
        this.request = handler.getRequest();
        this.style = style;
        this.datasource = datasource;
    }

    /**
     * central method for access the data assigned to a datasource
     * 
     * @return the data
     */
    public Object run() {

        Object response = null;
        if ( datasource != null ) {
            OGCWebServiceRequest request = null;
            try {
                int type = datasource.getType();
                switch ( type ) {
                case AbstractDataSource.LOCALWFS:
                case AbstractDataSource.REMOTEWFS: {
                    request = createGetFeatureRequest( (LocalWFSDataSource) datasource );
                    break;
                }
                case AbstractDataSource.LOCALWCS:
                case AbstractDataSource.REMOTEWCS: {
                    request = createGetCoverageRequest( datasource );
                    break;
                }
                case AbstractDataSource.REMOTEWMS: {
                    String styleName = null;

                    if ( style != null ) {
                        styleName = style.getName();
                    }

                    request = GetMap.createGetMapRequest( datasource, handler.getRequest(),
                                                          styleName, layer.getName() );
                    break;
                }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                OGCWebServiceException exce = new OGCWebServiceException(
                                                                          getClass().getName(),
                                                                          Messages.getMessage(
                                                                                               "WMS_ERRORQUERYCREATE",
                                                                                               e ) );
                // exception can't be re-thrown because responsible GetMapHandler
                // must collect all responses of all datasources
                response = new Object[] { new Integer( index ), exce };
                LOG.logError( e.getMessage(), e );
            }

            try {
                // start reading data with a limited time frame. The time limit
                // readed from the datasource muts be multiplied by 1000 because
                // the method expects milliseconds as timelimit
                Executor executor = Executor.getInstance();
                DoServiceTask<Object> task = new DoServiceTask<Object>(
                                                                        datasource.getOGCWebService(),
                                                                        request );
                Object o = executor.performSynchronously( task,
                                                          datasource.getRequestTimeLimit() * 1000 );
                response = handleResponse( o );
            } catch ( CancellationException e ) {
                // exception can't be re-thrown because responsible GetMapHandler
                // must collect all responses of all datasources
                String s = Messages.getMessage( "WMS_TIMEOUTDATASOURCE",
                                                new Integer( datasource.getRequestTimeLimit() ) );
                LOG.logError( s, e );
                if ( datasource.isFailOnException() ) {
                    OGCWebServiceException exce = new OGCWebServiceException( getClass().getName(),
                                                                              s );
                    response = new Object[] { new Integer( index ), exce };
                } else {
                    response = new Object[] { new Integer( index ), null };
                }
            } catch ( Throwable e ) {
                // exception can't be re-thrown because responsible GetMapHandler
                // must collect all responses of all datasources
                String s = Messages.getMessage( "WMS_ERRORDOSERVICE", e.getMessage() );
                LOG.logError( s, e );
                if ( datasource.isFailOnException() ) {
                    OGCWebServiceException exce = new OGCWebServiceException( getClass().getName(),
                                                                              s );
                    response = new Object[] { new Integer( index ), exce };
                } else {
                    response = new Object[] { new Integer( index ), null };
                }
            }
        }

        return response;
    }

    /**
     * creates a getFeature request considering the getMap request and the filterconditions defined
     * in the submitted <tt>DataSource</tt> object. The request will be encapsualted within a
     * <tt>OGCWebServiceEvent</tt>.
     * 
     * @param ds
     * @return GetFeature request object
     * @throws Exception
     */
    private GetFeature createGetFeatureRequest( LocalWFSDataSource ds )
                            throws Exception {

        Envelope bbox = transformBBOX( ds );

        List<PropertyPath> pp = null;
        if ( style != null ) {
            List<UserStyle> styleList = new ArrayList<UserStyle>();
            styleList.add( style );
            pp = StyleUtils.extractRequiredProperties( styleList, scaleDen );
        } else {
            pp = new ArrayList<PropertyPath>();
        }
        LOG.logDebug( "required properties: ", pp );
        Map<String, URI> namesp = extractNameSpaceDef( pp );

        // no filter condition has been defined
        StringBuffer sb = new StringBuffer( 5000 );
        sb.append( "<?xml version='1.0' encoding='" + CharsetUtils.getSystemCharset() + "'?>" );
        sb.append( "<GetFeature xmlns='http://www.opengis.net/wfs' " );
        sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
        sb.append( "xmlns:gml='http://www.opengis.net/gml' " );
        sb.append( "xmlns:" ).append( ds.getName().getPrefix() ).append( '=' );
        sb.append( "'" ).append( ds.getName().getNamespace() ).append( "' " );
        Iterator iter = namesp.keySet().iterator();
        while ( iter.hasNext() ) {
            String pre = (String) iter.next();
            URI nsp = namesp.get( pre );
            if ( !pre.equals( "xmlns" ) && !pre.equals( ds.getName().getPrefix() ) ) {
                sb.append( "xmlns:" ).append( pre ).append( "='" );
                sb.append( nsp.toASCIIString() ).append( "' " );
            }
        }

        sb.append( "service='WFS' version='1.1.0' " );
        if ( ds.getType() == AbstractDataSource.LOCALWFS ) {
            sb.append( "outputFormat='FEATURECOLLECTION'>" );
        } else {
            sb.append( "outputFormat='text/xml; subtype=gml/3.1.1'>" );
        }
        sb.append( "<Query typeName='" + ds.getName().getAsString() + "'>" );

        for ( int j = 0; j < pp.size(); j++ ) {
            if ( !pp.get( j ).getAsString().endsWith( "$SCALE" ) ) {
                // $SCALE is a dynamicly created property of each feature
                // and can not be requested
                sb.append( "<PropertyName>" ).append( pp.get( j ).getAsString() );
                sb.append( "</PropertyName>" );
            }
        }

        Query query = ds.getQuery();
        if ( query == null ) {
            sb.append( "<ogc:Filter><ogc:BBOX>" );
            sb.append( "<PropertyName>" );
            sb.append( ds.getGeometryProperty().getAsString() );
            sb.append( "</PropertyName>" );
            sb.append( GMLGeometryAdapter.exportAsBox( bbox ) );
            sb.append( "</ogc:BBOX>" );
            sb.append( "</ogc:Filter></Query></GetFeature>" );
        } else {
            Filter filter = query.getFilter();
            sb.append( "<ogc:Filter>" );
            if ( filter instanceof ComplexFilter ) {
                sb.append( "<ogc:And>" );
                sb.append( "<ogc:BBOX>" );
                sb.append( "<PropertyName>" );
                sb.append( ds.getGeometryProperty().getAsString() );
                sb.append( "</PropertyName>" );
                sb.append( GMLGeometryAdapter.exportAsBox( bbox ) );
                sb.append( "</ogc:BBOX>" );

                // add filter as defined in the layers datasource description
                // to the filter expression
                org.deegree.model.filterencoding.Operation op = ( (ComplexFilter) filter ).getOperation();
                sb.append( op.toXML() ).append( "</ogc:And>" );
            } else {
                ArrayList featureIds = ( (FeatureFilter) filter ).getFeatureIds();
                if ( featureIds.size() > 1 ) {
                    sb.append( "<ogc:And>" );
                }
                for ( int i = 0; i < featureIds.size(); i++ ) {
                    FeatureId fid = (FeatureId) featureIds.get( i );
                    sb.append( fid.toXML() );
                }
                if ( featureIds.size() > 1 ) {
                    sb.append( "</ogc:And>" );
                }
            }
            sb.append( "</ogc:Filter></Query></GetFeature>" );
        }

        LOG.logDebug( sb.toString() );
        // create dom representation of the request
        Document doc = XMLTools.parse( new StringReader( sb.toString() ) );

        // create OGCWebServiceEvent object
        IDGenerator idg = IDGenerator.getInstance();
        GetFeature gfr = GetFeature.create( "" + idg.generateUniqueID(), doc.getDocumentElement() );

        return gfr;
    }

    /**
     * transforms the requested BBOX into the DefaultSRS of the assigend 
     * feature type
     *  
     * @param ds
     * @return the envelope
     * @throws OGCWebServiceException
     * @throws CRSTransformationException
     * @throws UnknownCRSException 
     * @throws Proj4Exception
     */
    private Envelope transformBBOX( LocalWFSDataSource ds )
                            throws OGCWebServiceException, CRSTransformationException,
                            UnknownCRSException {
        Envelope bbox = request.getBoundingBox();
        // transform request bounding box to the coordinate reference
        // system the WFS holds the data if requesting CRS and WFS-Data
        // crs are different
        WFService wfs = (WFService) ds.getOGCWebService();
        // WFSCapabilities capa = (WFSCapabilities)wfs.getWFSCapabilities();
        WFSCapabilities capa = wfs.getCapabilities();
        QualifiedName gn = ds.getName();
        WFSFeatureType ft = capa.getFeatureTypeList().getFeatureType( gn );

        if ( ft == null ) {
            throw new OGCWebServiceException( Messages.getMessage( "WMS_UNKNOWNFT", ds.getName() ) );
        }

        // enable different formatations of the crs encoding for GML geometries
        String GML_SRS = "http://www.opengis.net/gml/srs/";
        String old_gml_srs = ft.getDefaultSRS().toASCIIString();
        String old_srs;
        if ( old_gml_srs.startsWith( GML_SRS ) ) {
            old_srs = old_gml_srs.substring( 31 ).replace( '#', ':' ).toUpperCase();
        } else {
            old_srs = old_gml_srs;
        }

        String new_srs = request.getSrs();
        String new_gml_srs;
        if ( old_gml_srs.startsWith( GML_SRS ) ) {
            new_gml_srs = GML_SRS + new_srs.replace( ':', '#' ).toLowerCase();
        } else {
            new_gml_srs = new_srs;
        }

        if ( !( old_srs.equalsIgnoreCase( new_gml_srs ) ) ) {
            IGeoTransformer transformer = new GeoTransformer( CRSFactory.create( old_srs ) );
            bbox = transformer.transform( bbox, this.handler.getRequestCRS() );
        }
        return bbox;
    }

    /**
     * creates a getCoverage request considering the getMap request and the filterconditions defined
     * in the submitted <tt>DataSource</tt> object The request will be encapsualted within a
     * <tt>OGCWebServiceEvent</tt>.
     * 
     * @param ds
     * @return GetCoverage request object
     * @throws InconsistentRequestException
     */
    private GetCoverage createGetCoverageRequest( AbstractDataSource ds )
                            throws InconsistentRequestException {

        Envelope bbox = request.getBoundingBox();

        GetCoverage gcr = ( (LocalWCSDataSource) ds ).getGetCoverageRequest();

        String crs = request.getSrs();
        // if (gcr != null && gcr.getDomainSubset().getRequestSRS() != null) {
        // crs = gcr.getDomainSubset().getRequestSRS().getCode();
        // }
        String format = request.getFormat();
        int pos = format.indexOf( '/' );
        if ( pos > -1 )
            format = format.substring( pos + 1, format.length() );
        if ( gcr != null && !"%default%".equals( gcr.getOutput().getFormat().getCode() ) ) {
            format = gcr.getOutput().getFormat().getCode();
        }
        if ( format.indexOf( "svg" ) > -1 ) {
            format = "tiff";
        }

        String version = "1.0.0";
        if ( gcr != null && gcr.getVersion() != null ) {
            version = gcr.getVersion();
        }
        String lay = ds.getName().getAsString();
        if ( gcr != null && !"%default%".equals( gcr.getSourceCoverage() ) ) {
            lay = gcr.getSourceCoverage();
        }
        String ipm = null;
        if ( gcr != null && gcr.getInterpolationMethod() != null ) {
            ipm = gcr.getInterpolationMethod().value;
        }

        // TODO
        // handle rangesets e.g. time and elevation
        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "service=WCS&request=GetCoverage" );
        sb.append( "&version=" ).append( version );
        sb.append( "&COVERAGE=" ).append( lay );
        sb.append( "&crs=" ).append( crs );        
        sb.append( "&response_crs=" ).append( crs );
        sb.append( "&BBOX=" ).append( bbox.getMin().getX() ).append( ',' );
        sb.append( bbox.getMin().getY() ).append( ',' ).append( bbox.getMax().getX() );
        sb.append( ',' ).append( bbox.getMax().getY() );
        sb.append( "&WIDTH=" ).append( request.getWidth() );
        sb.append( "&HEIGHT=" ).append( request.getHeight() );
        sb.append( "&FORMAT=" ).append( format );
        sb.append( "&INTERPOLATIONMETHOD=" ).append( ipm );
        try {
            IDGenerator idg = IDGenerator.getInstance();
            gcr = GetCoverage.create( "id" + idg.generateUniqueID(), sb.toString() );
        } catch ( WCSException e ) {
            throw new InconsistentRequestException( e.getMessage() );
        } catch ( org.deegree.ogcwebservices.OGCWebServiceException e ) {
            throw new InconsistentRequestException( e.getMessage() );
        }

        return gcr;

    }

    /**
     * 
     * @param result
     * @return the response objects
     * @throws Exception
     */
    private Object[] handleResponse( Object result )
                            throws Exception {

        Object[] theme = null;
        if ( result instanceof ResultCoverage ) {
            theme = handleGetCoverageResponse( (ResultCoverage) result );
        } else if ( result instanceof FeatureResult ) {
            theme = handleGetFeatureResponse( (FeatureResult) result );
        } else if ( result instanceof GetMapResult ) {
            theme = handleGetMapResponse( (GetMapResult) result );
        } else {
            String s = Messages.getMessage( "WMS_UNKNOWNRESPONSEFORMAT" );
            if ( datasource.isFailOnException() ) {
                OGCWebServiceException exce = new OGCWebServiceException( getClass().getName(), s );
                theme = new Object[] { new Integer( index ), exce };
            } else {
                theme = new Object[] { new Integer( index ), null };
            }
        }
        return theme;
    }

    /**
     * replaces all pixels within the passed image having a color that is defined to be transparent
     * within their datasource with a transparent color.
     * 
     * @param img
     * @return modified image
     */
    private BufferedImage setTransparentColors( BufferedImage img ) {
        LOG.entering();
        Color[] colors = null;
        if ( datasource.getType() == AbstractDataSource.LOCALWCS ) {
            LocalWCSDataSource ds = (LocalWCSDataSource) datasource;
            colors = ds.getTransparentColors();
        } else {
            RemoteWMSDataSource ds = (RemoteWMSDataSource) datasource;
            colors = ds.getTransparentColors();
        }

        if ( colors != null && colors.length > 0 ) {

            int[] clrs = new int[colors.length];
            for ( int i = 0; i < clrs.length; i++ ) {
                clrs[i] = colors[i].getRGB();
            }

            if ( img.getType() != BufferedImage.TYPE_INT_ARGB ) {
                // if the incoming image does not allow transparency
                // it must be copyed to a image of ARGB type
                BufferedImage tmp = new BufferedImage( img.getWidth(), img.getHeight(),
                                                       BufferedImage.TYPE_INT_ARGB );
                Graphics g = tmp.getGraphics();
                g.drawImage( img, 0, 0, null );
                g.dispose();
                img = tmp;
            }

            // TODO
            // should be replaced by a JAI operation
            int w = img.getWidth();
            int h = img.getHeight();
            for ( int i = 0; i < w; i++ ) {
                for ( int j = 0; j < h; j++ ) {
                    int col = img.getRGB( i, j );
                    if ( shouldBeTransparent( colors, col ) ) {
                        img.setRGB( i, j, 0x00FFFFFF );
                    }
                }
            }

        }
        LOG.exiting();
        return img;
    }

    /**
     * @return true if the distance between the image color and at least of the colors to be truned
     * to be transparent is less than 3 in an int RGB cube
     * 
     * @param colors
     * @param color
     */
    private boolean shouldBeTransparent( Color[] colors, int color ) {
        Color c2 = new Color( color );
        int r = c2.getRed();
        int g = c2.getGreen();
        int b = c2.getBlue();
        for ( int i = 0; i < colors.length; i++ ) {
            int r1 = colors[i].getRed();
            int g1 = colors[i].getGreen();
            int b1 = colors[i].getBlue();
            if ( Math.sqrt( ( r1 - r ) * ( r1 - r ) + ( g1 - g ) * ( g1 - g ) + ( b1 - b )
                            * ( b1 - b ) ) < 3 ) {
                return true;
            }
        }
        return false;
    }

    /**
     * handles the response of a cascaded WMS and calls a factory to create <tt>DisplayElement</tt>
     * and a <tt>Theme</tt> from it
     * 
     * @param response
     * @return the response objects
     * @throws Exception
     */
    private Object[] handleGetMapResponse( GetMapResult response )
                            throws Exception {

        BufferedImage bi = (BufferedImage) response.getMap();

        bi = setTransparentColors( bi );
        GridCoverage gc = new ImageGridCoverage( null, request.getBoundingBox(), bi );
        org.deegree.graphics.Layer rl = MapFactory.createRasterLayer( layer.getName(), gc );
        Theme theme = MapFactory.createTheme( datasource.getName().getAsString(), rl );
        Object[] ro = new Object[2];
        ro[0] = new Integer( index );
        ro[1] = theme;
        return ro;

    }

    /**
     * handles the response of a WFS and calls a factory to create <tt>DisplayElement</tt> and a
     * <tt>Theme</tt> from it
     * 
     * @param response
     * @return the response objects
     * @throws Exception
     */
    private Object[] handleGetFeatureResponse( FeatureResult response )
                            throws Exception {

        FeatureCollection fc = null;

        Object o = response.getResponse();
        if ( o instanceof FeatureCollection ) {
            fc = (FeatureCollection) o;
        } else {
            throw new Exception( Messages.getMessage( "WMS_UNKNOWNDATAFORMATFT" ) );
        }
        LOG.logDebug( "result: " + fc );

        org.deegree.graphics.Layer fl = MapFactory.createFeatureLayer(
                                                                       layer.getName(),
                                                                       this.handler.getRequestCRS(),
                                                                       fc );

        Object[] ro = new Object[2];
        ro[0] = new Integer( index );
        ro[1] = MapFactory.createTheme( datasource.getName().getAsString(), fl,
                                        new UserStyle[] { style } );
        return ro;
    }

    /**
     * handles the response of a WCS and calls a factory to create <tt>DisplayElement</tt> and a
     * <tt>Theme</tt> from it
     * 
     * @param response
     * @return the response objects
     * @throws Exception
     */
    private Object[] handleGetCoverageResponse( ResultCoverage response )
                            throws Exception {
        LOG.entering();

        ImageGridCoverage gc = (ImageGridCoverage) response.getCoverage();
        Object[] ro = new Object[2];
        if ( gc != null ) {
            BufferedImage bi = gc.getAsImage( -1, -1 );

            bi = setTransparentColors( bi );

            gc = new ImageGridCoverage( null, request.getBoundingBox(), bi );

            org.deegree.graphics.Layer rl = MapFactory.createRasterLayer( layer.getName(), gc );

            ro[0] = new Integer( index );
            ro[1] = MapFactory.createTheme( datasource.getName().getAsString(), rl );
        } else {
            throw new OGCWebServiceException( getClass().getName(),
                                              Messages.getMessage( "WMS_NOCOVERAGE",
                                                                   datasource.getName() ) );
        }
        return ro;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GetMapServiceInvokerForNL.java,v $
 * Revision 1.51  2006/12/03 21:21:22  poth
 * support for requesting coverages in different CRS than its native one
 *
 * Revision 1.50  2006/11/29 13:00:36  schmitz
 * Cleaned up WMS messages.
 *
 * Revision 1.49  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.48  2006/11/24 10:44:43  schmitz
 * Fixed bug in call to GetMap createGetMap.
 *
 * Revision 1.47  2006/11/24 09:33:13  schmitz
 * Fixed a bug concerning layer specific scale hints.
 * Using the central i18n mechanism.
 * Changed the localwfs mechanism to just use one WFS and not recreate them.
 *
 * Revision 1.46  2006/11/22 14:05:06  schmitz
 * Moved some createGetMapRequest methods to GetMap.
 * Added a generic DoServiceTask.
 *
 * Revision 1.45  2006/10/17 20:31:17  poth
 * *** empty log message ***
 *
 * Revision 1.44  2006/10/10 13:46:02  schmitz
 * Updated the WMS GetMap and GetFeatureInfo requests to handle sublayers of layers as well.
 *
 * Revision 1.43  2006/10/10 09:22:38  poth
 * bug fix - null checking for style
 *
 * Revision 1.42  2006/10/08 18:28:18  poth
 * footer comment corrected
 *
 * Revision 1.41  2006/10/07 15:05:28  poth
 * creation of GetFeature requests optimized in a way that just required properties will be requested
 * Changes to this class. What the people have been up to:
 * Revision 1.40  2006/09/27 16:46:41  poth
 * transformation method signature changed
 * Changes to this class. What the people have been up to:
 * Revision 1.39  2006/09/15 09:18:29  schmitz
 * Updated WMS to use SLD or SLD_BODY sld documents as default when also giving
 * LAYERS and STYLES parameters at the same time.
 * Changes to this class. What the people have been up to:
 * Revision 1.38  2006/09/08 08:42:01  schmitz
 * Updated the WMS to be 1.1.1 conformant once again.
 * Cleaned up the WMS code.
 * Added cite WMS test data.
 * Changes to this class. What the people have been up to:
 * Revision 1.37  2006/08/10 13:30:41  mschneider
 * Changed TimeoutException to CancellationException (due to changes in Executor). See Executor annotations for further info.
 * Changes to this class. What the people have been up to:
 * Revision 1.36  2006/08/07 13:52:05  mschneider
 * Refactored due to changes in deegree.framework.concurrent package.
 * Changes to this class. What the people have been up to:
 * Revision 1.35 2006/08/06 19:50:33 poth bug fix - exception handling
 * 
 * Revision 1.34 2006/07/29 08:55:06 poth implementation changed to new
 * org.deegree.framework.concurrent packeage
 * 
 * Revision 1.33 2006/07/13 12:24:45 poth adaptions required according to changes in
 * org.deegree.ogcwebservice.wms.operations.GetMap
 * 
 * Revision 1.32 2006/07/04 20:55:39 poth bug fix - creating GetFeature request if a filer condition
 * is defined
 * 
 * Revision 1.31 2006/06/06 07:57:50 poth changes in logging
 * 
 * Revision 1.30 2006/05/18 16:48:18 poth exception message externalized / bug fix -> GetCoverage
 * returns null 
 * 
 **************************************************************************************************/
