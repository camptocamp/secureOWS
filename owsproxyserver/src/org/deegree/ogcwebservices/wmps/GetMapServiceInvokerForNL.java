//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/GetMapServiceInvokerForNL.java,v 1.28 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.ogcwebservices.wmps;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.util.ArrayList;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.MapFactory;
import org.deegree.graphics.Theme;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.FeatureFilter;
import org.deegree.model.filterencoding.FeatureId;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
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
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wms.configuration.LocalWCSDataSource;
import org.deegree.ogcwebservices.wms.configuration.LocalWFSDataSource;
import org.deegree.ogcwebservices.wms.configuration.RemoteWMSDataSource;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetMapResult;
import org.opengis.coverage.grid.GridCoverage;
import org.w3c.dom.Document;

/**
 * This is a copy of the WMS package.
 * 
 * @version $Revision: 1.28 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.28 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
/**
 * Inner class for accessing the data of one named layer and creating <tt>DisplayElement</tt>s
 * and a <tt>Thrme</tt> from it. The class extends <tt>Thread</tt> and implements the run
 * method, so that a parallel data accessing from several layers is possible.
 * 
 * @version $Revision: 1.28 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
class GetMapServiceInvokerForNL extends Thread {

    private static final ILogger LOG = LoggerFactory.getLogger( GetMapServiceInvokerForNL.class );

    private final DefaultGetMapHandler handler;

    private Layer layer;

    private UserStyle style;

    private int index = 0;

    private AbstractDataSource datasource;

    /**
     * Creates a new ServiceInvokerForNL object.
     * 
     * @param handler
     * @param lay
     * @param source
     * @param style
     * @param index
     *            index of the requested layer
     */
    GetMapServiceInvokerForNL( DefaultGetMapHandler handler, Layer lay, AbstractDataSource source,
                              UserStyle style, int index ) {

        this.layer = lay;
        this.handler = handler;
        this.index = index;
        this.style = style;
        this.datasource = source;
    }

    /**
     * overrides the run-method of the parent class <tt>Thread</tt> for enabling a multi-threaded
     * access to the data.
     */
    @Override
    public void run() {
        LOG.entering();

        if ( this.datasource != null ) {

            OGCWebServiceRequest request = null;
            try {
                int type = this.datasource.getType();
                switch ( type ) {
                case AbstractDataSource.LOCALWFS:
                case AbstractDataSource.REMOTEWFS: {
                    request = createGetFeatureRequest( (LocalWFSDataSource) this.datasource );
                    break;
                }
                case AbstractDataSource.LOCALWCS:
                case AbstractDataSource.REMOTEWCS: {
                    request = createGetCoverageRequest( this.datasource );
                    break;
                }
                case AbstractDataSource.REMOTEWMS: {
                    request = GetMap.createGetMapRequest( this.datasource, handler.request,
                                                          style.getName(), layer.getName() );
                    break;
                }
                }
            } catch ( Exception e ) {
                OGCWebServiceException exce = new OGCWebServiceException(
                                                                          "ServiceInvokerForNL: "
                                                                                                  + this.layer.getName(),
                                                                          "Couldn't create query!"
                                                                                                  + StringTools.stackTraceToString( e.getStackTrace() ) );
                this.handler.putTheme( this.index, exce );
                this.handler.increaseCounter();
                LOG.exiting();
                return;
            }

            try {
                Object o = this.datasource.getOGCWebService().doService( request );
                handleResponse( o );
            } catch ( Exception e ) {
                LOG.logError( "", e );
                OGCWebServiceException exce = new OGCWebServiceException(
                                                                          "ServiceInvokerForNL: "
                                                                                                  + this.layer.getName(),
                                                                          "Couldn't perform doService()!"
                                                                                                  + e.getMessage() );
                this.handler.putTheme( this.index, exce );
                this.handler.increaseCounter();
                LOG.exiting();
                return;
            }
        } else {
            // increase counter because there is no service to call so it
            // is assumed that the request for the current layer if fullfilled
            this.handler.increaseCounter();
        }

        LOG.exiting();
    }

    /**
     * creates a getFeature request considering the getMap request and the filterconditions defined
     * in the submitted <tt>DataSource</tt> object. The request will be encapsualted within a
     * <tt>OGCWebServiceEvent</tt>.
     * 
     * @param ds
     * @return GetFeature event object containing a GetFeature request
     * @throws Exception
     */
    private GetFeature createGetFeatureRequest( LocalWFSDataSource ds )
                            throws Exception {
        LOG.entering();

        Envelope bbox = this.handler.request.getBoundingBox();

        // transform request bounding box to the coordinate reference
        // system the WFS holds the data if requesting CRS and WFS-Data
        // crs are different
        WFService wfs = (WFService) ds.getOGCWebService();
        // WFSCapabilities capa = (WFSCapabilities)wfs.getWFSCapabilities();
        WFSCapabilities capa = wfs.getCapabilities();

        QualifiedName gn = ds.getName();
        WFSFeatureType ft = capa.getFeatureTypeList().getFeatureType( gn );

        if ( ft == null ) {
            throw new OGCWebServiceException( "Feature Type:" + ds.getName()
                                              + " is not known by the WFS" );
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

        String new_srs = this.handler.request.getSrs();
        String new_gml_srs;
        if ( old_gml_srs.startsWith( GML_SRS ) ) {
            new_gml_srs = GML_SRS + new_srs.replace( ':', '#' ).toLowerCase();
        } else {
            new_gml_srs = new_srs;
        }

        if ( !( old_srs.equalsIgnoreCase( new_gml_srs ) ) ) {
            IGeoTransformer gt = new GeoTransformer( CRSFactory.create( old_srs ) );
            bbox = gt.transform( bbox, this.handler.reqCRS );
        }

        // no filter condition has been defined
        StringBuffer sb = new StringBuffer( 5000 );
        sb.append( "<?xml version='1.0' encoding='" + CharsetUtils.getSystemCharset() + "'?>" );
        sb.append( "<GetFeature xmlns='http://www.opengis.net/wfs' " );
        sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
        sb.append( "xmlns:gml='http://www.opengis.net/gml' " );
        sb.append( "xmlns:" ).append( ds.getName().getPrefix() ).append( '=' );
        sb.append( "'" ).append( ds.getName().getNamespace() ).append( "' " );
        sb.append( "service='WFS' version='1.1.0' " );
        if ( ds.getType() == AbstractDataSource.LOCALWFS ) {
            sb.append( "outputFormat='FEATURECOLLECTION'>" );
        } else {
            sb.append( "outputFormat='text/xml; subtype=gml/3.1.1'>" );
        }
        sb.append( "<Query typeName='" + ds.getName().getAsString() + "'>" );

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
                sb.append( "<ogc:BBOX><PropertyName>" ).append( ds.getName().getPrefix() );
                sb.append( ':' ).append( ds.getGeometryProperty() );
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

        // create dom representation of the request
        Document doc = XMLTools.parse( new StringReader( sb.toString() ) );

        // create OGCWebServiceEvent object
        IDGenerator idg = IDGenerator.getInstance();
        GetFeature gfr = GetFeature.create( "" + idg.generateUniqueID(), doc.getDocumentElement() );

        LOG.exiting();
        return gfr;
    }

    /**
     * creates a getCoverage request considering the getMap request and the filterconditions defined
     * in the submitted <tt>DataSource</tt> object The request will be encapsualted within a
     * <tt>OGCWebServiceEvent</tt>.
     * 
     * @param ds
     * @return GetCoverage event object containing a GetCoverage request
     * @throws InconsistentRequestException
     */
    private GetCoverage createGetCoverageRequest( AbstractDataSource ds )
                            throws InconsistentRequestException {
        LOG.entering();

        Envelope bbox = this.handler.request.getBoundingBox();

        GetCoverage gcr = ( (LocalWCSDataSource) ds ).getGetCoverageRequest();

        String crs = this.handler.request.getSrs();
        if ( gcr != null && gcr.getDomainSubset().getRequestSRS() != null ) {
            crs = gcr.getDomainSubset().getRequestSRS().getCode();
        }
        String format = this.handler.request.getFormat();
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
        sb.append( "&CRS=" ).append( crs );
        sb.append( "&BBOX=" ).append( bbox.getMin().getX() ).append( ',' ).append(
                                                                                   bbox.getMin().getY() ).append(
                                                                                                                  ',' ).append(
                                                                                                                                bbox.getMax().getX() ).append(
                                                                                                                                                               ',' ).append(
                                                                                                                                                                             bbox.getMax().getY() );
        sb.append( "&WIDTH=" ).append( this.handler.request.getWidth() );
        sb.append( "&HEIGHT=" ).append( this.handler.request.getHeight() );
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

        LOG.exiting();
        return gcr;

    }

    /**
     * The method implements the <tt>OGCWebServiceClient</tt> interface. So a deegree OWS
     * implementation accessed by this class is able to return the result of a request by calling
     * the write-method.
     * 
     * @param result
     *            to a GetXXX request
     */
    private void handleResponse( Object result ) {

        try {
            if ( result instanceof ResultCoverage ) {
                handleGetCoverageResponse( (ResultCoverage) result );
            } else if ( result instanceof FeatureResult ) {
                handleGetFeatureResponse( (FeatureResult) result );
            } else if ( result instanceof GetMapResult ) {
                handleGetMapResponse( (GetMapResult) result );
            } else {
                OGCWebServiceException exce = new OGCWebServiceException( "ServiceInvokerForNL: "
                                                                          + this.layer.getName(),
                                                                          "unknown response format!" );
                this.handler.putTheme( this.index, exce );
            }
        } catch ( Exception e ) {
            LOG.logError( "-", e );
            OGCWebServiceException exce = new OGCWebServiceException( "ServiceInvokerForNL: "
                                                                      + this.layer.getName(),
                                                                      e.toString() );
            this.handler.putTheme( this.index, exce );
        }
        // increase counter to indicate that one more layers requesting is
        // completed
        this.handler.increaseCounter();
    }

    /**
     * replaces all pixels within the passed image having a color that is defined to be transparent
     * within their datasource with a transparent color.
     * 
     * @param img
     * @return BufferedImage
     */
    private BufferedImage setTransparentColors( BufferedImage img ) {

        LOG.entering();
        Color[] colors = null;
        if ( this.datasource.getType() == AbstractDataSource.LOCALWCS ) {
            LocalWCSDataSource ds = (LocalWCSDataSource) this.datasource;
            colors = ds.getTransparentColors();
        } else {
            RemoteWMSDataSource ds = (RemoteWMSDataSource) this.datasource;
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
                    if ( shouldBeTransparent( clrs, col ) ) {
                        img.setRGB( i, j, 0x00FFFFFF );
                    }
                }
            }
        }
        LOG.exiting();
        return img;
    }

    /**
     * Should be transparent.
     * 
     * @param colors
     * @param color
     * @return boolean
     */
    private boolean shouldBeTransparent( int[] colors, int color ) {
        for ( int i = 0; i < colors.length; i++ ) {
            if ( colors[i] == color ) {
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
     * @throws Exception
     */
    private void handleGetMapResponse( GetMapResult response )
                            throws Exception {
        LOG.entering();

        BufferedImage bi = (BufferedImage) response.getMap();
        bi = setTransparentColors( bi );
        GridCoverage gc = new ImageGridCoverage( null, this.handler.request.getBoundingBox(), bi );
        org.deegree.graphics.Layer rl = MapFactory.createRasterLayer( this.layer.getName(), gc );
        Theme theme = MapFactory.createTheme( this.datasource.getName().getAsString(), rl );
        this.handler.putTheme( this.index, theme );
        LOG.exiting();
    }

    /**
     * handles the response of a WFS and calls a factory to create <tt>DisplayElement</tt> and a
     * <tt>Theme</tt> from it
     * 
     * @param response
     * @throws Exception
     */
    private void handleGetFeatureResponse( FeatureResult response )
                            throws Exception {
        LOG.entering();

        FeatureCollection fc = null;

        Object o = response.getResponse();

        if ( o instanceof FeatureCollection ) {
            fc = (FeatureCollection) o;
        } else {
            throw new Exception( "unknown data format at a GetFeature response" );
        }
        org.deegree.graphics.Layer fl = MapFactory.createFeatureLayer( this.layer.getName(),
                                                                       this.handler.reqCRS, fc );

        this.handler.putTheme( this.index,
                               MapFactory.createTheme( this.datasource.getName().getAsString(), fl,
                                                       new UserStyle[] { this.style } ) );
        LOG.exiting();

    }

    /**
     * handles the response of a WCS and calls a factory to create <tt>DisplayElement</tt> and a
     * <tt>Theme</tt> from it
     * 
     * @param response
     * @throws Exception
     */
    private void handleGetCoverageResponse( ResultCoverage response )
                            throws Exception {
        LOG.entering();

        ImageGridCoverage gc = (ImageGridCoverage) response.getCoverage();
        BufferedImage bi = gc.getAsImage( -1, -1 );

        bi = setTransparentColors( bi );

        gc = new ImageGridCoverage( null, this.handler.request.getBoundingBox(), bi );

        org.deegree.graphics.Layer rl = MapFactory.createRasterLayer( this.layer.getName(), gc );

        this.handler.putTheme( this.index,
                               MapFactory.createTheme( this.datasource.getName().getAsString(), rl ) );
        LOG.exiting();
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * 
 * $Log: GetMapServiceInvokerForNL.java,v $
 * Revision 1.28  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.27  2006/11/22 14:05:06  schmitz
 * Moved some createGetMapRequest methods to GetMap.
 * Added a generic DoServiceTask.
 *
 * Revision 1.26  2006/11/14 15:36:40  poth
 * bug fix - set correct WFS version for GetFeature request
 *
 * Revision 1.25  2006/10/17 20:31:19  poth
 * *** empty log message ***
 *
 * Revision 1.24  2006/09/27 16:46:41  poth
 * transformation method signature changed
 *
 * Revision 1.23  2006/09/13 07:37:58  deshmukh
 * removed excess debug statements.
 *
 * Revision 1.22  2006/09/11 16:22:28  mays
 * changing content of Map for vebdor specific parameters: the contents from configuration are added by the contents from handler request.
 * also: started cleaning up the footer
 *
 * Revision 1.21 2006/08/10 07:11:35 deshmukh WMPS has been modified to support the new 
 * configuration changes and the excess code not needed has been replaced. 
 * 
 * Revision 1.20 2006/08/01 13:41:48 deshmukh The wmps configuration has been modified and extended. 
 * Also fixed the javadoc.
 * 
 * Revision 1.19 2006/07/13 12:24:45 poth adaptions required according to changes in
 * org.deegree.ogcwebservice.wms.operations.GetMap 
 * 
 * Revision 1.18 2006/06/12 09:34:53 deshmukh extended the print map capabilites to support the get 
 * request and changed the db structure. 
 * 
 * Revision 1.17 2006/06/07 12:37:51 deshmukh Reset the changes made to the WMPS. 
 * 
 * Revision 1.15 2006/05/11 20:15:19 poth *** empty log message *** 
 * 
 * Revision 1.14 2006/05/11 20:10:29 poth *** empty log message *** 
 * 
 * Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.13 2006/05/03 20:09:52 poth Changes to this class. What
 * the people have been up to: *** empty log message *** Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.12 2006/05/01
 * 20:15:26 poth Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.11 2006/04/06 20:25:24 poth Changes to this class. What the people
 * have been up to: *** empty log message *** Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.10 2006/03/30 21:20:25
 * poth Changes to this class. What the people have been up to: *** empty log message *** Changes to
 * this class. What the people have been up to: Changes to this class. What the people have been up
 * to: Revision 1.9 2006/02/02 21:10:20 deshmukh Changes to this class. What the people have been up
 * to: restructured wmps Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.8 2006/02/02 08:09:30 deshmukh Changes to this
 * class. What the people have been up to: removed unnecessary comments Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.7
 * 2006/01/31 17:12:27 deshmukh Changes to this class. What the people have been up to: WMPService
 * implemented Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.6 2006/01/18 16:53:45 deshmukh Changes to this class. What
 * the people have been up to: *** empty log message *** Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.5 2006/01/17
 * 16:54:58 deshmukh Changes to this class. What the people have been up to: *** empty log message
 * *** Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.4 2006/01/16 10:49:55 deshmukh Changes to this class. What the
 * people have been up to: *** empty log message *** Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.3 2006/01/13
 * 15:26:55 deshmukh Changes to this class. What the people have been up to: *** empty log message
 * *** Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.2 2006/01/11 15:43:34 deshmukh Changes to this class. What the
 * people have been up to: *** empty log message *** Changes to this class. What the people have
 * been up to: Revision 1.1 2006/01/10 16:37:22 deshmukh ** empty log message ***
 * 
 * Revision 1.20 2006/01/05 17:43:54 poth ** empty log message ***
 * 
 * Revision 1.19 2005/12/21 17:30:10 poth no message
 * 
 * Revision 1.18 2005/12/18 19:06:30 poth no message
 * 
 * Revision 1.17 2005/12/11 18:31:05 poth no message
 * 
 * Revision 1.16 2005/11/25 11:59:26 poth no message
 * 
 * Revision 1.15 2005/11/22 17:31:33 poth no message
 * 
 * Revision 1.14 2005/11/22 17:19:13 poth no message
 * 
 * Revision 1.13 2005/11/16 13:45:00 mschneider Merge of wfs development branch.
 * 
 * Revision 1.12.2.2 2005/11/08 13:52:24 mschneider Refactoring.
 * 
 * Revision 1.12.2.1 2005/11/07 23:01:27 mschneider ** empty log message ***
 * 
 * Revision 1.12 2005/09/29 11:30:25 poth no message
 * 
 * Revision 1.11 2005/09/27 19:53:19 poth no message
 * 
 * Revision 1.10 2005/09/27 19:43:52 poth no message
 * 
 * Revision 1.9 2005/08/26 21:13:02 poth no message
 * 
 * Revision 1.8 2005/08/24 16:11:14 mschneider Renamed GenericName to QualifiedName.
 * 
 * Revision 1.7 2005/08/19 07:45:11 poth no message
 * 
 * Revision 1.6 2005/08/10 07:33:02 poth no message
 * 
 * Revision 1.5 2005/08/09 17:26:56 poth no message
 * 
 * Revision 1.4 2005/08/09 15:48:24 poth no message
 * 
 * Revision 1.3 2005/07/22 16:19:56 poth no message
 * 
 * Revision 1.2 2005/07/07 15:54:40 poth no message
 * 
 * Revision 1.1 2005/06/28 15:58:11 poth no message
 * 
 * 
 **************************************************************************************************/
