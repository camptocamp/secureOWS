//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/GetMapServiceInvokerForUL.java,v 1.16 2006/11/29 21:28:30 poth Exp $
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

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.graphics.MapFactory;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.FeatureTypeConstraint;
import org.deegree.graphics.sld.LayerFeatureConstraints;
import org.deegree.graphics.sld.RemoteOWS;
import org.deegree.graphics.sld.UserLayer;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.configuration.AbstractDataSource;
import org.w3c.dom.Element;

/**
 * This a copy of the WMS package.
 * 
 * class for accessing the data of one user layer and creating <tt>DisplayElement</tt>s and a
 * <tt>Thrme</tt> from it. The class extends <tt>Thread</tt> and implements the run method, so
 * that a parallel data accessing from several layers is possible.
 * 
 * @version $Revision: 1.16 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.16 $, $Date: 2006/11/29 21:28:30 $
 * 
 * @since 2.0
 */
class GetMapServiceInvokerForUL extends Thread {

    private static final ILogger LOG = LoggerFactory.getLogger( GetMapServiceInvokerForUL.class );

    private final DefaultGetMapHandler handler;

    private UserLayer layer = null;

    private UserStyle[] styles = null;

    private int index = 0;

    GetMapServiceInvokerForUL( DefaultGetMapHandler handler, UserLayer layer, int index ) {
        this.layer = layer;
        this.handler = handler;
        AbstractStyle[] tmp = layer.getStyles();
        this.styles = new UserStyle[tmp.length];
        for ( int i = 0; i < tmp.length; i++ ) {
            this.styles[i] = (UserStyle) tmp[i];
        }

        this.index = index;
    }

    /**
     * overrides/implements the run-method of <tt>Thread</tt>
     */
    @Override
    public void run() {
        LOG.entering();

        try {
            if ( this.layer.getRemoteOWS() == null
                 || this.layer.getRemoteOWS().getService().equals( RemoteOWS.WFS ) ) {
                handleWFS();
            } else if ( this.layer.getRemoteOWS().getService().equals( RemoteOWS.WCS ) ) {
                handleWCS();
            }
        } catch ( Exception e ) {
            LOG.logError( "", e );
            OGCWebServiceException exce = new OGCWebServiceException(
                                                                      "ServiceInvokerForUL: "
                                                                                              + this.layer.getName(),
                                                                      "Couldn't perform query!"
                                                                                              + StringTools.stackTraceToString( e ) );
            this.handler.putTheme( this.index, exce );
            this.handler.increaseCounter();
            LOG.exiting();
            return;
        }

        LOG.exiting();
    }

    /**
     * handles requests against a WFS
     * 
     * @throws Exception
     */
    private void handleWFS()
                            throws Exception {

        LOG.entering();

        FeatureCollection fc = null;
        String request = createGetFeatureRequest();
        
        if ( this.layer.getRemoteOWS() != null ) {
            // handle request against a remote WFS
            RemoteOWS remoteOWS = this.layer.getRemoteOWS();
            URL url = remoteOWS.getOnlineResource();
                       NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), url, request );
            InputStreamReader isr = new InputStreamReader( nw.getInputStream(),
                                                           CharsetUtils.getSystemCharset() );
            GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
            doc.load( isr, url.toString() );
            Element root = doc.getRootElement();

            if ( root.getNodeName().indexOf( "Exception" ) > -1 ) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream( 1000 );
                doc.write( bos );
                throw new Exception( new String( bos.toByteArray() ) );
            }
            fc = doc.parse();
        } else {
            // handle request agaist a local WFS; this is bit problematic
            // because deegree WMS is able to handle more than one
            // local WFS. At the moment the WFS will be used that will
            // returned by the WFServiceFactory as default
            XMLFragment xml = new XMLFragment( new StringReader( request ), XMLFragment.DEFAULT_URL );
            Element root = xml.getRootElement();
            // create OGCWebServiceEvent object
            IDGenerator idg = IDGenerator.getInstance();
            GetFeature gfr = GetFeature.create( "" + idg.generateUniqueID(), root );

            // returns the WFS responsible for handling current feature type
            WFService wfs = getResponsibleService();
            FeatureResult fr = (FeatureResult) wfs.doService( gfr );
            fc = (FeatureCollection) fr.getResponse();
        }
        org.deegree.graphics.Layer fl = MapFactory.createFeatureLayer( this.layer.getName(),
                                                                       this.handler.reqCRS, fc );
        this.handler.putTheme( this.index, MapFactory.createTheme( this.layer.getName(), fl,
                                                                   this.styles ) );
        this.handler.increaseCounter();
        LOG.exiting();
    }

    /**
     * Returns the responsible service.
     * 
     * @return Exception
     * @throws OGCWebServiceException
     */
    private WFService getResponsibleService()
                            throws OGCWebServiceException {

        LayerFeatureConstraints lfc = this.layer.getLayerFeatureConstraints();
        FeatureTypeConstraint[] ftc = lfc.getFeatureTypeConstraint();
        Layer root = this.handler.getConfiguration().getLayer();
        WFService wfs = findService( root, ftc[0].getFeatureTypeName().getAsString() );
        if ( wfs == null ) {
            throw new OGCWebServiceException( this.getName(), "feature type: "
                                                              + ftc[0].getFeatureTypeName()
                                                              + " is not serverd by this WMS/WFS" );
        }
        return wfs;

    }

    /**
     * searches/findes the WFService that is resposible for handling the feature types of the
     * current request. If no WFService instance can be found <code>null</code> will be returned
     * to indicated that the current feature type is not served by the internal WFS of a WMS
     * 
     * @param currentlayer
     * @param featureType
     * @return WFService
     * @throws OGCWebServiceException
     */
    private WFService findService( Layer currentlayer, String featureType )
                            throws OGCWebServiceException {
        Layer[] layers = currentlayer.getLayer();
        for ( int i = 0; i < layers.length; i++ ) {
            AbstractDataSource[] ad = layers[i].getDataSource();
            if ( ad != null ) {
                for ( int j = 0; j < ad.length; j++ ) {
                    if ( ad[j].getName().getAsString().equals( featureType ) ) {
                        return (WFService) ad[j].getOGCWebService();
                    }
                }
            }
            // recursion
            WFService wfs = findService( layers[i], featureType );
            if ( wfs != null ) {
                return wfs;
            }
        }

        return null;
    }

    /**
     * creates a GetFeature request related to the UserLayer encapsulated in this object
     * 
     * @return String
     * @throws Exception
     */
    private String createGetFeatureRequest()
                            throws Exception {
        
        LayerFeatureConstraints lfc = this.layer.getLayerFeatureConstraints();
        FeatureTypeConstraint[] ftc = lfc.getFeatureTypeConstraint();

        // no filter condition has been defined
        StringBuffer sb = new StringBuffer( 5000 );
        sb.append( "<?xml version='1.0' encoding='UTF-8'?>" );
        sb.append( "<GetFeature xmlns='http://www.opengis.net/wfs' " );
        sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
        sb.append( "xmlns:gml='http://www.opengis.net/gml' " );
        sb.append( "xmlns:app=\"http://www.deegree.org/app\" " );
        sb.append( "service='WFS' version='1.1.0' " );
        sb.append( "outputFormat='text/xml; subtype=gml/3.1.1'>" );
        for ( int i = 0; i < ftc.length; i++ ) {
            sb.append( "<Query typeName='" + ftc[i].getFeatureTypeName() + "'>" );
            Filter filter = ftc[i].getFilter();
            if ( filter != null ) {
                sb.append( filter.toXML() );
            }
            sb.append( "</Query>" );
        }
        sb.append( "</GetFeature>" );

        return sb.toString();
    }

    /**
     * handles requests against a WCS
     * 
     * @throws Exception
     */
    private void handleWCS()
                            throws Exception {
        LOG.entering();
        LOG.exiting();
        throw new UnsupportedOperationException(
                                                 "The WCS support has not been implemented as of now. "
                                                                         + "Please bear with us." );
        /*
         * TODO RemoteOWS remoteOWS = layer.getRemoteOWS(); URL url = remoteOWS.getOnlineResource();
         * 
         * NetWorker nw = new NetWorker( url ); MemoryCacheSeekableStream mcss = new
         * MemoryCacheSeekableStream( nw.getInputStream() );
         * 
         * RenderedOp rop = JAI.create("stream", mcss);
         * 
         * GC_GridCoverage gc = new ImageGridCoverage(rop.getAsBufferedImage(),
         * request.getBoundingBox(), reqCRS, false); mcss.close();
         * 
         * org.deegree.graphics.Layer rl = MapFactory.createRasterLayer(layer.getName(), gc);
         * 
         * putTheme(index, MapFactory.createTheme(layer.getName(), rl)); mcss.close();
         * increaseCounter();
         */

    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetMapServiceInvokerForUL.java,v $
 * Changes to this class. What the people have been up to: Revision 1.16  2006/11/29 21:28:30  poth
 * Changes to this class. What the people have been up to: bug fixing - SLD GetMap requests containing user layers with featuretypeconstraints
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.15  2006/11/14 15:36:40  poth
 * Changes to this class. What the people have been up to: bug fix - set correct WFS version for GetFeature request
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.14  2006/10/17 20:31:19  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.13  2006/09/13 07:37:58  deshmukh
 * Changes to this class. What the people have been up to: removed excess debug statements.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12  2006/09/04 11:32:25  deshmukh
 * Changes to this class. What the people have been up to: comments added
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11 2006/08/10 07:11:35
 * deshmukh Changes to this class. What the people have been up to: WMPS has been modified to
 * support the new configuration changes and the excess code not needed has been replaced. Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.10 2006/08/01 13:41:48 deshmukh Changes to this class. What the people have
 * been up to: The wmps configuration has been modified and extended. Also fixed the javadoc.
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.9 2006/06/12 09:34:53 deshmukh Changes to this class. What the people
 * have been up to: extended the print map capabilites to support the get request and changed the db
 * structure. Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.8 2006/05/11 20:15:19 poth Changes to this class. What the
 * people have been up to: *** empty log message *** Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.7 2006/05/11
 * 20:10:29 poth Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.6 2006/04/06 20:25:24 poth Changes to this class. What the people
 * have been up to: *** empty log message *** Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.5 2006/03/30 21:20:25 poth
 * Changes to this class. What the people have been up to: *** empty log message *** Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.4 2006/02/09 16:51:07 deshmukh Changes to this class. What the people have been up to:
 * implemented deegree style guide Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.3 2006/01/20 18:16:16 mschneider
 * Changes to this class. What the people have been up to: Adapted to use
 * GMLFeatureCollectionDocument. Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.2 2006/01/11 15:34:53 deshmukh Changes to
 * this class. What the people have been up to: *** empty log message *** Changes to this class.
 * What the people have been up to: Revision 1.1 2006/01/10 16:37:22 deshmukh ** empty log message
 * ***
 * 
 * Revision 1.9 2006/01/08 14:09:35 poth ** empty log message ***
 * 
 * Revision 1.8 2005/12/22 20:12:32 poth no message
 * 
 * Revision 1.7 2005/12/11 18:31:05 poth no message
 * 
 * Revision 1.6 2005/11/14 22:15:37 poth no message
 * 
 * Revision 1.5 2005/11/14 15:37:40 poth no message
 * 
 * Revision 1.4 2005/09/27 19:53:19 poth no message
 * 
 * Revision 1.3 2005/08/30 08:25:48 poth no message
 * 
 * Revision 1.2 2005/07/18 07:00:50 poth no message
 * 
 * Revision 1.1 2005/06/28 15:58:11 poth no message
 * 
 * 
 **************************************************************************************************/
