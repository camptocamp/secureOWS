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
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.values.Values;
import org.deegree.framework.concurrent.ExecutionFinishedEvent;
import org.deegree.framework.concurrent.ExecutionFinishedListener;
import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.i18n.Messages;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.FeatureFilter;
import org.deegree.model.filterencoding.FeatureId;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.InvalidSRSException;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wcs.describecoverage.DescribeCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.ResultCoverage;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wms.configuration.LocalWFSDataSource;
import org.deegree.ogcwebservices.wms.configuration.RemoteWMSDataSource;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfoResult;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.WMSProtocolFactory;
import org.w3c.dom.Document;

/**
 * 
 * 
 *
 * @version $Revision: 1.70 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.70 $, $Date: 2006/11/29 14:10:24 $
 *
 */
class DefaultGetFeatureInfoHandler implements GetFeatureInfoHandler,
                                  ExecutionFinishedListener<Object[]> {

    protected static final ILogger LOG = LoggerFactory.getLogger( DefaultGetFeatureInfoHandler.class );

    private static final double DEFAULT_PIXEL_SIZE = 0.00028;

    protected GetFeatureInfo request = null;

    protected GetMap getMapRequest = null;

    protected WMSConfigurationType configuration = null;

    // collects the reponse for each layer
    private Object[] featCol = null;

    // scale of the map
    protected double scale = 0;

    // holds the number of request services that have responsed
    private int count = 0;

    // CRS of the request
    protected CoordinateSystem reqCRS = null;

    /**
     * Creates a new GetMapHandler object.        
     * @param capabilities 
     * @param request request to perform
     * @throws OGCWebServiceException 
     */
    public DefaultGetFeatureInfoHandler( WMSConfigurationType capabilities, GetFeatureInfo request )
                            throws OGCWebServiceException {
        LOG.entering();
        this.request = request;
        this.configuration = capabilities;
        getMapRequest = request.getGetMapRequestCopy();
        try {
            reqCRS = CRSFactory.create( getMapRequest.getSrs() );
            if ( reqCRS == null ) {
                throw new InvalidSRSException( Messages.getMessage( "WMS_UNKNOWN_CRS",
                                                                    getMapRequest.getSrs() ) );
            }
        } catch ( Exception e ) {
            throw new InvalidSRSException( Messages.getMessage( "WMS_UNKNOWN_CRS",
                                                                getMapRequest.getSrs() ) );
        }
        try {
            Envelope bbox = getMapRequest.getBoundingBox();
            scale = MapUtils.calcScale( getMapRequest.getWidth(), getMapRequest.getHeight(), bbox,
                                        reqCRS, DEFAULT_PIXEL_SIZE );
        } catch ( Exception e ) {
            LOG.logDebug( e.getLocalizedMessage(), e );
            throw new OGCWebServiceException( Messages.getMessage( "WMS_SCALECALC", e ) );
        }
        LOG.exiting();
    }

    /**
     * increases the counter variable that holds the number of services that
     * has sent a response. All data are available if the counter value equals
     * the number of requested layers.
     */
    protected synchronized void increaseCounter() {
        count++;
    }

    /**
     * performs a GetFeatureInfo request and retruns the result encapsulated within
     * a <tt>WMSFeatureInfoResponse</tt> object. <p>
     * The method throws an WebServiceException that only shall be thrown if an
     * fatal error occurs that makes it imposible to return a result.
     * If something wents wrong performing the request (none fatal error) The 
     * exception shall be encapsulated within the response object to be returned 
     * to the client as requested (GetFeatureInfo-Request EXCEPTION-Parameter).
     * 
     * <p>
     * All sublayers of the queried layer will be added automatically. Non-queryable
     * sublayers are then ignored in the response.
     * </p>
     * 
     * @return response to the GetFeatureInfo response
     */
    public GetFeatureInfoResult performGetFeatureInfo()
                            throws OGCWebServiceException {
        LOG.entering();

        String[] qlayers = request.getQueryLayers();

        List<Layer> allLayers = new ArrayList<Layer>();

        // here, the explicitly queried layers are checked for being queryable and known
        for ( int i = 0; i < qlayers.length; i++ ) {
            Layer layer = configuration.getLayer( qlayers[i] );

            if ( layer == null ) {
                throw new LayerNotDefinedException( Messages.getMessage( "WMS_UNKNOWNLAYER",
                                                                         qlayers[i] ) );
            }
            if ( !layer.isQueryable() ) {
                throw new LayerNotQueryableException(
                                                      Messages.getMessage(
                                                                           "WMS_LAYER_NOT_QUERYABLE",
                                                                           qlayers[i] ) );
            }
            if ( !layer.isSrsSupported( getMapRequest.getSrs() ) ) {
                throw new InvalidSRSException( Messages.getMessage( "WMS_UNKNOWN_CRS",
                                                                    getMapRequest.getSrs(),
                                                                    qlayers[i] ) );
            }

            allLayers.add( layer );

            // sublayers are added WITHOUT being checked for being queryable
            // This is desirable for example in the following scenario:
            // Suppose one queryable layer contains a lot of other layers,
            // that are mostly queryable. Then you can query all of those layers
            // at once by just querying the enclosing layer (the unqueryable
            // sublayers are ignored).
            allLayers.addAll( Arrays.asList( layer.getLayer() ) );
        }

        Layer[] layerList = allLayers.toArray( new Layer[allLayers.size()] );

        // there must be one feature collection for each requested layer
        int cnt = countNumberOfQueryableDataSources( layerList );
        featCol = new Object[cnt];

        // invokes the data supplyer for each layer in an independ thread
        int kk = 0;
        for ( int i = 0; i < layerList.length; i++ ) {
            if ( validate( layerList[i] ) ) {
                AbstractDataSource datasource[] = layerList[i].getDataSource();
                for ( int j = 0; j < datasource.length; j++ ) {
                    if ( datasource[j].isQueryable() && isValidArea( datasource[j].getValidArea() ) ) {
                        ServiceInvoker si = new ServiceInvoker( layerList[i], datasource[j], kk++ );
                        ServiceInvokerTask task = new ServiceInvokerTask( si );
                        Executor.getInstance().performAsynchronously( task, this );
                    }
                }
            } else {
                // set feature collection to null if no data are available for the requested
                // area and/or scale. This will cause this index position will be ignored
                // when creating the final result
                featCol[kk++] = null;
                increaseCounter();
            }
        }

        // waits until the requested layers are available as <tt>DisplayElements</tt>
        // or the time limit has been reached.
        // TODO
        // substitue by an event based approach 
        try {
            waitForFinish();
        } catch ( Exception e ) {
            return createExceptionResponse( e );
        }

        GetFeatureInfoResult res = createFeatureInfoResponse();

        return res;
    }

    /**
     * returns the number of datasources assigned to the queried layers
     * that are queryable
     * 
     * @param layerList
     * @return the number
     */
    private int countNumberOfQueryableDataSources( Layer[] layerList ) {
        int cnt = 0;
        for ( int i = 0; i < layerList.length; i++ ) {
            AbstractDataSource[] ds = layerList[i].getDataSource();
            for ( int j = 0; j < ds.length; j++ ) {
                if ( ds[j].isQueryable() && isValidArea( ds[j].getValidArea() ) ) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    /**
     * returns true if the requested boundingbox intersects with the valid
     * area of a datasource
     * @param validArea
     */
    private boolean isValidArea( Geometry validArea ) {

        if ( validArea != null ) {
            try {
                Envelope env = request.getGetMapRequestCopy().getBoundingBox();
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
     * validates if the requested layer matches the conditions of the request
     * if not a <tt>WebServiceException</tt> will be thrown. If the layer matches
     * the request, but isn't able to deviever data for the requested area and/or
     * scale false will be returned. If the layer matches the request and contains
     * data for the requested area and/or scale true will be returned.
     * 
     * @param layer layer as defined at the capabilities/configuration
     */
    private boolean validate( Layer layer )
                            throws OGCWebServiceException {

        LOG.entering();

        // why the layer can be null here is not known, but just in case:
        String name = ( layer == null ) ? "" : layer.getName();

        // check for valid coordinated reference system
        String[] srs = layer.getSrs();
        boolean tmp = false;
        for ( int i = 0; i < srs.length; i++ ) {
            if ( srs[i].equalsIgnoreCase( getMapRequest.getSrs() ) ) {
                tmp = true;
                break;
            }
        }

        if ( !tmp ) {
            throw new InvalidSRSException( Messages.getMessage( "WMS_INVALIDSRS", name,
                                                                getMapRequest.getSrs() ) );
        }

        // check bounding box
        try {

            Envelope bbox = getMapRequest.getBoundingBox();
            Envelope layerBbox = layer.getLatLonBoundingBox();
            if ( !getMapRequest.getSrs().equalsIgnoreCase( "EPSG:4326" ) ) {
                // transform the bounding box of the request to EPSG:4326
                IGeoTransformer gt = new GeoTransformer( CRSFactory.create( "EPSG:4326" ) );
                bbox = gt.transform( bbox, reqCRS );
            }

            if ( !bbox.intersects( layerBbox ) ) {
                return false;
            }

        } catch ( Exception e ) {
            throw new OGCWebServiceException( Messages.getMessage( "WMS_BBOXCOMPARSION" ) );
        }

        LOG.exiting();

        return true;
    }

    /**
     * creates a <tt>GetFeatureInfoResult</tt> containing an <tt>OGCWebServiceException</tt>
     *
     * @param e exception to encapsulate into the response
     */
    private GetFeatureInfoResult createExceptionResponse( Exception e ) {

        OGCWebServiceException exce = null;

        // default --> application/vnd.ogc.se_xml
        exce = new OGCWebServiceException( getClass().getName(), e.getMessage() );

        GetFeatureInfoResult res = WMSProtocolFactory.createGetFeatureInfoResponse( request, exce,
                                                                                    null );

        return res;
    }

    /**
     * waits until the requested layers are available as <tt>DisplayElements</tt>
     * or the time limit has been reached. If the waiting is terminated by reaching
     * the time limit an <tt>WebServiceException</tt> will be thrown to indicated
     * that the request couldn't be performed correctly.
     *
     * @throws WebServiceException if the time limit has been reached
     */
    private void waitForFinish()
                            throws OGCWebServiceException, Exception {
        LOG.entering();

        // subtract 1 second for architecture overhead and image creation
        long timeLimit = 1000 * ( configuration.getDeegreeParams().getRequestTimeLimit() - 1 );
        //long timeLimit = 1000 * 100;
        long runTime = 0;

        while ( count < featCol.length ) {
            try {
                Thread.sleep( 100 );
            } catch ( Exception e ) {
                LOG.logError( "WMS_WAITING_LOOP", e );
                throw e;
            }

            runTime += 100;

            // finish loop after if request performing hasn't been completed 
            // after the time limit is reached
            if ( runTime > timeLimit ) {
                throw new OGCWebServiceException( Messages.getMessage( "WMS_TIMEOUT" ) );
            }
        }

        LOG.exiting();
    }

    /**
     * will be called each time a datasource has been read
     * 
     * @param returnValue
     */
    public synchronized void executionFinished( ExecutionFinishedEvent<Object[]> returnValue ) {
        Object[] o = null;
        try {
            o = returnValue.getResult();
        } catch ( Throwable t ) {
            LOG.logError( "WMS_GETFEATURE_EXCEPTION", t );
        }
        featCol[( (Integer) o[0] ).intValue()] = o[1];
        increaseCounter();
    }

    /**
     * generates the desired output from the GMLs
     * @return the result object
     * @throws OGCWebServiceException
     */
    private GetFeatureInfoResult createFeatureInfoResponse()
                            throws OGCWebServiceException {
        LOG.entering();

        Envelope bbox = getMapRequest.getBoundingBox();

        StringBuffer sb = new StringBuffer( 20000 );
        sb.append( "<ll:FeatureCollection " );

        URL schemaLoc = configuration.getDeegreeParams().getFeatureSchemaLocation();
        if ( schemaLoc != null ) {
            sb.append( "xsi:schemaLocation='" );
            sb.append( configuration.getDeegreeParams().getFeatureSchemaNamespace() );
            sb.append( " " );
            sb.append( schemaLoc.toExternalForm() );
            sb.append( "'" );
        }

        sb.append( " xmlns:gml='http://www.opengis.net/gml' " );
        sb.append( "xmlns:ll='http://www.lat-lon.de' " );
        sb.append( "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " );
        URL url = configuration.getDeegreeParams().getSchemaLocation();
        if ( url != null ) {
            sb.append( "xsi:schemaLocation='" );
            sb.append( "http://www.lat-lon.de " + NetWorker.url2String( url ) + "'" );
        }
        sb.append( "><gml:boundedBy>" );
        sb.append( "<gml:Box srsName='" + getMapRequest.getSrs() + "'>" );
        sb.append( "<gml:coordinates>" + bbox.getMin().getX() + "," );
        sb.append( bbox.getMin().getY() + " " + bbox.getMax().getX() + "," );
        sb.append( bbox.getMax().getY() + "</gml:coordinates>" );
        sb.append( "</gml:Box></gml:boundedBy>" );

        int cnt = 0;

        for ( int i = 0; i < featCol.length; i++ ) {
            if ( featCol[i] instanceof OGCWebServiceException ) {
                throw (OGCWebServiceException) featCol[i];
            }
            FeatureCollection fc = (FeatureCollection) featCol[i];
            cnt = appendFeatureCollection( fc, sb, cnt );

            //if ( cnt >= request.getFeatureCount() ) break;
        }
        sb.append( "</ll:FeatureCollection>" );

        GetFeatureInfoResult response = WMSProtocolFactory.createGetFeatureInfoResponse(
                                                                                         request,
                                                                                         null,
                                                                                         sb.toString() );

        LOG.exiting();
        return response;
    }

    /**
     * 
     * @param col
     * @param sb
     * @param cnt
     * @return a counter, probably the same that is given as argument
     */
    private int appendFeatureCollection( FeatureCollection col, StringBuffer sb, int cnt ) {

        LOG.entering();

        Feature[] feat = col.toArray();
        if ( feat != null ) {
            for ( int j = 0; j < feat.length; j++ ) {
                FeatureType ft = feat[j].getFeatureType();
                PropertyType[] ftp = ft.getProperties();
                cnt++;
                sb.append( "<gml:featureMember>" );
                sb.append( "<ll:" + ft.getName().getLocalName() );
                sb.append( " fid='" + feat[j].getId().replace( ' ', '_' ) + "'>" );
                for ( int i = 0; i < ftp.length; i++ ) {
                    if ( ftp[i].getType() != Types.GEOMETRY && ftp[i].getType() != Types.POINT
                         && ftp[i].getType() != Types.CURVE && ftp[i].getType() != Types.SURFACE
                         && ftp[i].getType() != Types.MULTIPOINT
                         && ftp[i].getType() != Types.MULTICURVE
                         && ftp[i].getType() != Types.MULTISURFACE ) {
                        sb.append( "<ll:" + ftp[i].getName().getLocalName() + ">" );
                        Object prop = feat[j].getDefaultProperty( ftp[i].getName() ).getValue();
                        if ( prop instanceof FeatureCollection ) {
                            FeatureCollection fc = (FeatureCollection) prop;
                            appendFeatureCollection( fc, sb, cnt );
                        } else {
                            sb.append( "<![CDATA[" ).append( prop ).append( "]]>" );
                        }
                        sb.append( "</ll:" + ftp[i].getName().getLocalName() + ">" );
                    }
                }
                sb.append( "</ll:" + ft.getName().getLocalName() + ">" );
                sb.append( "</gml:featureMember>" );
                if ( cnt >= request.getFeatureCount() )
                    break;
            }
        }

        LOG.exiting();

        return cnt;
    }

    ////////////////////////////////////////////////////////////////////////////
    //                          inner classes                                 //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Inner class for accessing the data of one layer and creating 
     * a GML document from it. The class extends <tt>Thread</tt> and implements 
     * the run method, so that a parallel data accessing from several layers is 
     * possible.
     *
     * @version $Revision: 1.70 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     */
    public class ServiceInvoker {
        private Layer layer = null;

        private int index = 0;

        private AbstractDataSource datasource = null;

        /**
         * Creates a new ServiceInvoker object.
         *
         * @param layer 
         * @param datasource
         * @param index index of the requested layer
         */
        ServiceInvoker( Layer layer, AbstractDataSource datasource, int index ) {
            this.layer = layer;
            this.index = index;
            this.datasource = datasource;
        }

        /**
         * central method for access the data assigned to a datasource
         * @return result of feature info query
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
                        request = createDescribeCoverageRequest( datasource );
                        break;
                    }
                    case AbstractDataSource.REMOTEWMS: {
                        request = createGetFeatureInfo( datasource );
                        break;
                    }
                    }
                } catch ( Exception e ) {
                    OGCWebServiceException exce = new OGCWebServiceException(
                                                                              "ServiceInvoker: "
                                                                                                      + layer.getName(),
                                                                              Messages.getMessage( "WMS_CREATE_QUERY" ) );
                    response = new Object[] { new Integer( index ), exce };
                    LOG.logError(
                                  Messages.getMessage( "WMS_CREATE_QUERY" ) + ": " + e.getMessage(),
                                  e );
                    throw new RuntimeException( e );
                }

                try {
                    Executor executor = Executor.getInstance();
                    DoServiceTask task = new DoServiceTask( datasource.getOGCWebService(), request );
                    Object o = executor.performSynchronously(
                                                              task,
                                                              datasource.getRequestTimeLimit() * 1000 );
                    response = handleResponse( o );
                } catch ( CancellationException e ) {
                    // exception can't be re-thrown because responsible GetMapHandler
                    // must collect all responses of all datasources
                    String s = Messages.getMessage( "WMS_TIMEOUTDATASOURCE",
                                                    new Integer( datasource.getRequestTimeLimit() ) );
                    LOG.logError( s, e );
                    if ( datasource.isFailOnException() ) {
                        OGCWebServiceException exce = new OGCWebServiceException(
                                                                                  getClass().getName(),
                                                                                  s );
                        response = new Object[] { new Integer( index ), exce };
                    } else {
                        response = new Object[] { new Integer( index ), null };
                    }
                } catch ( Throwable t ) {
                    // exception can't be re-thrown because responsible GetMapHandler
                    // must collect all responses of all datasources
                    String s = Messages.getMessage( "WMS_ERRORDOSERVICE", t.getMessage() );
                    LOG.logError( s, t );
                    if ( datasource.isFailOnException() ) {
                        OGCWebServiceException exce = new OGCWebServiceException(
                                                                                  getClass().getName(),
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
         * creates a getFeature request considering the getMap request and the
         * filterconditions defined in the submitted <tt>DataSource</tt> object.
         * The request will be encapsualted within a <tt>OGCWebServiceEvent</tt>.
         * @param ds 
         * @return GetFeature request object
         */
        private GetFeature createGetFeatureRequest( LocalWFSDataSource ds )
                                throws Exception {

            LOG.entering();

            Envelope targetArea = calcTargetArea( ds );

            // no filter condition has been defined
            StringBuffer sb = new StringBuffer( 2000 );
            sb.append( "<?xml version='1.0' encoding='" + CharsetUtils.getSystemCharset() + "'?>" );
            sb.append( "<GetFeature xmlns='http://www.opengis.net/wfs' " );
            sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
            sb.append( "xmlns:gml='http://www.opengis.net/gml' " );
            sb.append( "xmlns:" ).append( ds.getName().getPrefix() ).append( '=' );
            sb.append( "'" ).append( ds.getName().getNamespace() ).append( "' " );
            sb.append( "service='WFS' version='1.1.0' " );
            sb.append( "outputFormat='FEATURECOLLECTION'>" );
            sb.append( "<Query typeName='" + ds.getName().getAsString() + "'><ogc:Filter>" );

            Query query = ds.getQuery();
            if ( query == null ) {
                // BBOX operation for speeding up the search at simple datasources
                // like shapes
                sb.append( "<ogc:BBOX><PropertyName>" );
                sb.append( ds.getGeometryProperty().getAsString() );
                sb.append( "</PropertyName>" );
                sb.append( GMLGeometryAdapter.exportAsBox( targetArea ) );
                sb.append( "</ogc:BBOX>" );
                sb.append( "</ogc:Filter></Query></GetFeature>" );
            } else {
                Filter filter = query.getFilter();

                sb.append( "<ogc:And>" );
                // BBOX operation for speeding up the search at simple datasources
                // like shapes
                sb.append( "<ogc:BBOX><PropertyName>" + ds.getGeometryProperty().getAsString() );
                sb.append( "</PropertyName>" );
                sb.append( GMLGeometryAdapter.exportAsBox( targetArea ) );
                sb.append( "</ogc:BBOX>" );

                if ( filter instanceof ComplexFilter ) {
                    org.deegree.model.filterencoding.Operation op = ( (ComplexFilter) filter ).getOperation();
                    sb.append( op.toXML() );
                } else {
                    ArrayList featureIds = ( (FeatureFilter) filter ).getFeatureIds();
                    for ( int i = 0; i < featureIds.size(); i++ ) {
                        FeatureId fid = (FeatureId) featureIds.get( i );
                        sb.append( fid.toXML() );
                    }
                }

                sb.append( "</ogc:And></ogc:Filter></Query></GetFeature>" );

            }

            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                LOG.logDebug( "GetFeature-request:\n" + sb );
            }

            // create dom representation of the request
            StringReader sr = new StringReader( sb.toString() );
            Document doc = XMLTools.parse( sr );

            // create OGCWebServiceEvent object
            IDGenerator idg = IDGenerator.getInstance();
            GetFeature gfr = GetFeature.create( "" + idg.generateUniqueID(),
                                                doc.getDocumentElement() );

            LOG.exiting();

            return gfr;
        }

        /**
         * calculates the target area for the getfeatureinfo request from the
         * maps bounding box, the its size and the image coordinates of interest. An
         * area is calculated instead of using a point because to consider 
         * uncertainties determining the point of interest
         * @param ds <tt>DataSource</tt> of the layer that is requested for feature 
         *          infos (each layer may be offered in its own crs)
         */
        private Envelope calcTargetArea( AbstractDataSource ds )
                                throws OGCWebServiceException {

            LOG.entering();

            int width = request.getGetMapRequestCopy().getWidth();
            int height = request.getGetMapRequestCopy().getHeight();
            int x = request.getClickPoint().x;
            int y = request.getClickPoint().y;

            Envelope bbox = request.getGetMapRequestCopy().getBoundingBox();

            // transform request bounding box to the coordinate reference
            // system the WFS holds the data if requesting CRS and WFS-Data
            // crs are different
            //            WFService se = (WFService)ds.getOGCWebService();
            //            WFSCapabilities capa = (WFSCapabilities)se.getWFSCapabilities();
            //            
            //            org.deegree.ogcwebservices.wfs.capabilities.FeatureType ft =
            //                capa.getFeatureTypeList().getFeatureType( ds.getName() );
            WFService se = (WFService) ds.getOGCWebService();
            WFSCapabilities capa = se.getCapabilities();
            QualifiedName gn = ds.getName();
            WFSFeatureType ft = capa.getFeatureTypeList().getFeatureType( gn );

            if ( ft == null ) {
                throw new OGCWebServiceException( Messages.getMessage( "WMS_UNKNOWNFT",
                                                                       ds.getName() ) );
            }
            String crs = ft.getDefaultSRS().toASCIIString();
            Envelope tBbox = null;
            try {
                GeoTransform gt = new WorldToScreenTransform( bbox.getMin().getX(),
                                                              bbox.getMin().getY(),
                                                              bbox.getMax().getX(),
                                                              bbox.getMax().getY(), 0, 0,
                                                              width - 1, height - 1 );
                double[] target = new double[4];
                int rad = configuration.getDeegreeParams().getFeatureInfoRadius();
                target[0] = gt.getSourceX( x - rad );
                target[1] = gt.getSourceY( y + rad );
                target[2] = gt.getSourceX( x + rad );
                target[3] = gt.getSourceY( y - rad );

                tBbox = GeometryFactory.createEnvelope( target[0], target[1], target[2], target[3],
                                                        null );
                if ( !( crs.equalsIgnoreCase( request.getGetMapRequestCopy().getSrs() ) ) ) {
                    IGeoTransformer transformer = new GeoTransformer( CRSFactory.create( crs ) );
                    tBbox = transformer.transform( tBbox, reqCRS );
                }

            } catch ( Exception e ) {
                throw new OGCWebServiceException( e.toString() );
            }

            LOG.exiting();
            return tBbox;
        }

        /**
         * creates a describe Coverage request
         * The request will be encapsualted within a <tt>OGCWebServiceEvent</tt>.
         * @param ds
         * @return DescribeCoverage request object
         */
        private DescribeCoverage createDescribeCoverageRequest( AbstractDataSource ds ) {
            throw new UnsupportedOperationException(
                                                     "createDescribeCoverageRequest is not implemented yet" );
        }

        /**
         * creates a GetFeatureInfo request for requesting a cascaded remote WMS          
         * The request will be encapsualted within a <tt>OGCWebServiceEvent</tt>.
         * @param ds
         * @return GetFeatureInfo request object
         */
        private GetFeatureInfo createGetFeatureInfo( AbstractDataSource ds ) {
            LOG.entering();

            // create embbeded map request
            GetMap gmr = ( (RemoteWMSDataSource) ds ).getGetMapRequest();

            String format = getMapRequest.getFormat();

            if ( gmr != null && !"%default%".equals( gmr.getFormat() ) ) {
                format = gmr.getFormat();
            }

            org.deegree.ogcwebservices.wms.operation.GetMap.Layer[] lys = null;
            lys = new org.deegree.ogcwebservices.wms.operation.GetMap.Layer[1];
            lys[0] = GetMap.createLayer( layer.getName(), "$DEFAULT" );

            if ( gmr != null && gmr.getLayers() != null ) {
                lys = gmr.getLayers();
            }
            Color bgColor = getMapRequest.getBGColor();
            if ( gmr != null && gmr.getBGColor() != null ) {
                bgColor = gmr.getBGColor();
            }
            Values time = getMapRequest.getTime();
            if ( gmr != null && gmr.getTime() != null ) {
                time = gmr.getTime();
            }
            Map<String, String> vendorSpecificParameter = getMapRequest.getVendorSpecificParameters();
            if ( gmr != null && gmr.getVendorSpecificParameters() != null
                 && gmr.getVendorSpecificParameters().size() > 0 ) {
                vendorSpecificParameter = gmr.getVendorSpecificParameters();
            }
            String version = "1.1.0";
            if ( gmr != null && gmr.getVersion() != null ) {
                version = gmr.getFormat();
            }
            Values elevation = getMapRequest.getElevation();
            if ( gmr != null && gmr.getElevation() != null ) {
                elevation = gmr.getElevation();
            }
            Map<String, Values> sampleDim = null;
            if ( gmr != null && gmr.getSampleDimension() != null ) {
                sampleDim = gmr.getSampleDimension();
            }

            IDGenerator idg = IDGenerator.getInstance();
            gmr = GetMap.create( version, "" + idg.generateUniqueID(), lys, elevation, sampleDim,
                                 format, getMapRequest.getWidth(), getMapRequest.getHeight(),
                                 getMapRequest.getSrs(), getMapRequest.getBoundingBox(),
                                 getMapRequest.getTransparency(), bgColor,
                                 getMapRequest.getExceptions(), time, null, null,
                                 vendorSpecificParameter );

            // create GetFeatureInfo request for cascaded/remote WMS
            String[] queryLayers = new String[] { ds.getName().getAsString() };
            GetFeatureInfo req = GetFeatureInfo.create( "1.1.0", this.toString(), queryLayers, gmr,
                                                        "application/vnd.ogc.gml",
                                                        request.getFeatureCount(),
                                                        request.getClickPoint(),
                                                        request.getExceptions(), null,
                                                        request.getVendorSpecificParameters() );

            LOG.exiting();
            return req;
        }

        /**
         * The method implements the <tt>OGCWebServiceClient</tt> interface. So a
         * deegree OWS implementation accessed by this class is able to return the
         * result of a request by calling the write-method.
         * @param result to a GetXXX request
         * @return the response object
         * @throws Exception 
         */
        private Object handleResponse( Object result )
                                throws Exception {
            Object[] response = null;
            if ( result instanceof FeatureResult ) {
                response = handleGetFeatureResponse( (FeatureResult) result );
            } else if ( result instanceof ResultCoverage ) {
                response = handleDescribeCoverageResponse( (ResultCoverage) result );
            } else if ( result instanceof GetFeatureInfoResult ) {

                response = handleGetFeatureInfoResult( (GetFeatureInfoResult) result );
            } else {
                throw new Exception( Messages.getMessage( "WMS_UNKNOWNRESPONSEFORMAT" ) );
            }
            return response;
        }

        /**
         * handles the response of a WFS and calls a factory to create
         * <tt>DisplayElement</tt> and a <tt>Theme</tt> from it
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
            } else if ( o.getClass() == byte[].class ) {
                Reader reader = new InputStreamReader( new ByteArrayInputStream( (byte[]) o ) );
                GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
                doc.load( reader, XMLFragment.DEFAULT_URL );
                fc = doc.parse();
            } else {
                throw new Exception( Messages.getMessage( "WMS_UNKNOWNDATAFORMATFT" ) );
            }
            Object[] ro = new Object[2];
            ro[0] = new Integer( index );
            ro[1] = fc;
            return ro;
        }

        /**
         * 
         * @param res
         */
        private Object[] handleGetFeatureInfoResult( GetFeatureInfoResult res )
                                throws Exception {

            FeatureCollection fc = null;
            StringReader sr = new StringReader( res.getFeatureInfo() );
            XMLFragment xml = new XMLFragment( sr, XMLFragment.DEFAULT_URL );

            URL url = ( (RemoteWMSDataSource) datasource ).getFeatureInfoTransform();
            if ( url != null ) {
                // transform incoming GML/XML to a GML application schema
                // that is understood by deegree
                XSLTDocument xslt = new XSLTDocument();
                xslt.load( url );
                xml = xslt.transform( xml, null, null, null );
            }
            GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
            doc.setRootElement( xml.getRootElement() );
            fc = doc.parse();
            Object[] ro = new Object[2];
            ro[0] = new Integer( index );
            ro[1] = fc;
            return ro;
        }

        /**
         * handles the response of a WCS and calls a factory to create
         * <tt>DisplayElement</tt> and a <tt>Theme</tt> from it
         * @param response
         * @return the reponse objects
         * @throws Exception
         */
        private Object[] handleDescribeCoverageResponse( ResultCoverage response )
                                throws Exception {

            throw new UnsupportedOperationException(
                                                     "handleDescribeCoverageResponse is not implemented yet" );

        }

    }

    private class DoServiceTask implements Callable<Object> {

        OGCWebService webService;

        OGCWebServiceRequest request;

        DoServiceTask( OGCWebService webService, OGCWebServiceRequest request ) {
            this.webService = webService;
            this.request = request;
        }

        public Object call()
                                throws Exception {
            return this.webService.doService( request );
        }
    }

    private class ServiceInvokerTask implements Callable<Object[]> {

        ServiceInvoker invoker;

        ServiceInvokerTask( ServiceInvoker invoker ) {
            this.invoker = invoker;
        }

        public Object[] call()
                                throws Exception {
            return (Object[]) this.invoker.run();
        }
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DefaultGetFeatureInfoHandler.java,v $
 Revision 1.70  2006/11/29 14:10:24  schmitz
 Added more messages. The Default handlers should now be fully externalized.

 Revision 1.69  2006/11/29 13:00:36  schmitz
 Cleaned up WMS messages.

 Revision 1.68  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.67  2006/11/24 09:33:12  schmitz
 Fixed a bug concerning layer specific scale hints.
 Using the central i18n mechanism.
 Changed the localwfs mechanism to just use one WFS and not recreate them.

 Revision 1.66  2006/11/14 15:37:06  poth
 bug fix - set correct WFS version for GetFeature request

 Revision 1.65  2006/10/17 20:31:17  poth
 *** empty log message ***

 Revision 1.64  2006/10/10 13:46:02  schmitz
 Updated the WMS GetMap and GetFeatureInfo requests to handle sublayers of layers as well.

 Revision 1.63  2006/09/27 16:46:41  poth
 transformation method signature changed

 Revision 1.62  2006/09/25 12:47:00  poth
 bug fixes - map scale calculation

 Revision 1.61  2006/09/15 09:18:29  schmitz
 Updated WMS to use SLD or SLD_BODY sld documents as default when also giving
 LAYERS and STYLES parameters at the same time.

 Revision 1.60  2006/09/11 15:26:15  mschneider
 Fixed bug in createGetFeatureRequest(). Geometry property must be exported using QualifiedName#getAsString().

 Revision 1.59  2006/09/11 14:31:48  mschneider
 Added debugging messages (to find GetFeature-Info related problem).

 Revision 1.58  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.57  2006/08/10 13:30:41  mschneider
 Changed TimeoutException to CancellationException (due to changes in Executor). See Executor annotations for further info.

 Revision 1.56  2006/08/08 09:57:11  mschneider
 Added generics for type safety - constrained the return type of tasks.

 Revision 1.55  2006/08/07 13:59:46  mschneider
 Refactored due to changes in deegree.framework.concurrent package.

 Revision 1.54  2006/08/07 10:01:41  poth
 throw UnsupportedOperation exception for for handling DescribeCoverage added

 Revision 1.53  2006/08/06 19:50:49  poth
 bug fix - exception handling

 Revision 1.52  2006/07/13 12:24:45  poth
 adaptions required according to changes in org.deegree.ogcwebservice.wms.operations.GetMap

 Revision 1.51  2006/07/12 14:46:17  poth
 comment footer added

 ********************************************************************** */
