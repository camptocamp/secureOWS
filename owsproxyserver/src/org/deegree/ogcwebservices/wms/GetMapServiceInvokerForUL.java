//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/GetMapServiceInvokerForUL.java,v 1.27 2006/11/29 21:28:30 poth Exp $
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

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.graphics.MapFactory;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.FeatureTypeConstraint;
import org.deegree.graphics.sld.LayerFeatureConstraints;
import org.deegree.graphics.sld.RemoteOWS;
import org.deegree.graphics.sld.StyleUtils;
import org.deegree.graphics.sld.UserLayer;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.w3c.dom.Element;

/**
 * class for accessing the data of one user layer and creating 
 * <tt>DisplayElement</tt>s and a <tt>Thrme</tt> from it. The class extends
 * <tt>Thread</tt> and implements the run method, so that a parallel data
 * accessing from several layers is possible. 
 *
 * @version $Revision: 1.27 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.27 $, $Date: 2006/11/29 21:28:30 $
 *
 * @since 2.0
 */
class GetMapServiceInvokerForUL extends GetMapServiceInvoker implements Runnable {

    private static final ILogger LOG = LoggerFactory.getLogger( GetMapServiceInvokerForUL.class );

    private UserLayer layer = null;

    private UserStyle[] styles = null;

    /**
     * 
     * @param handler
     * @param layer
     * @param scale current mapscale denominator
     * @param index
     */
    GetMapServiceInvokerForUL( DefaultGetMapHandler handler, UserLayer layer, double scale,
                              int index ) {
        super( handler, index, scale );
        
        this.layer = layer;
        AbstractStyle[] tmp = layer.getStyles();
        styles = new UserStyle[tmp.length];
        for ( int i = 0; i < tmp.length; i++ ) {
            styles[i] = (UserStyle) tmp[i]; 
        }

    }

    /**
     * overrides/implements the run-method of <tt>Thread</tt>
     */
    public void run() {
        
        try {
            if ( layer.getRemoteOWS() == null
                 || layer.getRemoteOWS().getService().equals( RemoteOWS.WFS ) ) {
                handleWFS();
            } else if ( layer.getRemoteOWS().getService().equals( RemoteOWS.WCS ) ) {
                handleWCS();
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            OGCWebServiceException exce = new OGCWebServiceException( "ServiceInvokerForUL: "
                                                                      + layer.getName(),
                                                                      "Couldn't perform query!" );
            this.handler.putTheme( index, exce );
            this.handler.increaseCounter();
            LOG.exiting();
            return;
        }

    }

    /**
     * handles requests against a WFS
     */
    private void handleWFS()
                            throws Exception {

        FeatureCollection fc = null;
        String request = createGetFeatureRequest();
        LOG.logDebug( request );
        if ( layer.getRemoteOWS() != null ) {
            // handle request against a remote WFS
            RemoteOWS remoteOWS = layer.getRemoteOWS();
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
            WFService wfs = getResponsibleService( layer );
            FeatureResult fr = (FeatureResult) wfs.doService( gfr );
            fc = (FeatureCollection) fr.getResponse();
        }
        org.deegree.graphics.Layer fl = MapFactory.createFeatureLayer(
                                                                       layer.getName(),
                                                                       this.handler.getRequestCRS(),
                                                                       fc );
        this.handler.putTheme( index, MapFactory.createTheme( layer.getName(), fl, styles ) );
        this.handler.increaseCounter();

    }
    

    /**
     * creates a GetFeature request related to the UserLayer encapsulated
     * in this object
     */
    private String createGetFeatureRequest()
                            throws Exception {

        LayerFeatureConstraints lfc = layer.getLayerFeatureConstraints();
        FeatureTypeConstraint[] ftc = lfc.getFeatureTypeConstraint();

        List<UserStyle> styleList = Arrays.asList( styles );
        List<PropertyPath> pp = StyleUtils.extractRequiredProperties( styleList, scaleDen );
        LOG.logDebug( "required properties: ", pp );
        pp = findGeoProperties( layer, ftc, pp );
        Map<String, URI> namesp = extractNameSpaceDef( pp );
        for ( int i = 0; i < ftc.length; i++ ) {
            QualifiedName qn = ftc[i].getFeatureTypeName();
            namesp.put( qn.getPrefix(), qn.getNamespace() );
        }

        StringBuffer sb = new StringBuffer( 5000 );
        sb.append( "<?xml version='1.0' encoding='" + CharsetUtils.getSystemCharset() + "'?>" );
        sb.append( "<GetFeature xmlns='http://www.opengis.net/wfs' " );
        sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
        sb.append( "xmlns:gml='http://www.opengis.net/gml' " );

        Iterator iter = namesp.keySet().iterator();
        while ( iter.hasNext() ) {
            String pre = (String) iter.next();
            URI nsp = namesp.get( pre );
            if ( !pre.equals( "xmlns" ) ) {
                sb.append( "xmlns:" ).append( pre ).append( "='" );
                sb.append( nsp.toASCIIString() ).append( "' " );
            }
        }

        sb.append( "service='WFS' version='1.1.0' " );
        sb.append( "outputFormat='text/xml; subtype=gml/3.1.1'>" );
        for ( int i = 0; i < ftc.length; i++ ) {
            QualifiedName qn = ftc[i].getFeatureTypeName();
            sb.append( "<Query typeName='" ).append( qn.getAsString() ).append( "'>" );

            for ( int j = 0; j < pp.size(); j++ ) {
                if ( !pp.get( j ).getAsString().endsWith( "$SCALE" ) ) {
                    // $SCALE is a dynamicly created property of each feature
                    // and can not be requested
                    sb.append( "<PropertyName>" ).append( pp.get( j ).getAsString() );
                    sb.append( "</PropertyName>" );
                }
            }

            Filter filter = ftc[i].getFilter();
            if ( filter != null ) {
                sb.append( filter.toXML() );
            }
            sb.append( "</Query>" );
        }
        sb.append( "</GetFeature>" );

        LOG.logDebug( sb.toString() );

        return sb.toString();
    }

    /**
     * handles requests against a WCS
     */
    private void handleWCS()
                            throws Exception {

        /*    TODO        
         RemoteOWS remoteOWS = layer.getRemoteOWS();
         URL url = remoteOWS.getOnlineResource();
         
         NetWorker nw = new NetWorker( url );
         MemoryCacheSeekableStream mcss = 
         new MemoryCacheSeekableStream( nw.getInputStream() );
         
         RenderedOp rop = JAI.create("stream", mcss);

         GC_GridCoverage gc = new ImageGridCoverage(rop.getAsBufferedImage(), 
         request.getBoundingBox(), 
         reqCRS, false);
         mcss.close();
         
         org.deegree.graphics.Layer rl = MapFactory.createRasterLayer(layer.getName(), gc);
         
         putTheme(index, MapFactory.createTheme(layer.getName(), rl));
         mcss.close();
         increaseCounter();
         */

    }
    
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetMapServiceInvokerForUL.java,v $
 Revision 1.27  2006/11/29 21:28:30  poth
 bug fixing - SLD GetMap requests containing user layers with featuretypeconstraints

 Revision 1.26  2006/11/14 15:37:06  poth
 bug fix - set correct WFS version for GetFeature request

 Revision 1.25  2006/10/17 20:31:17  poth
 *** empty log message ***

 Revision 1.24  2006/10/07 15:04:44  poth
 method used by GetMapServiceInvokerForNL too removed to a commen parent class

 Revision 1.23  2006/10/04 07:43:38  poth
 bug fix

 Revision 1.22  2006/10/02 10:01:46  poth
 debug statements added

 Revision 1.21  2006/09/26 12:45:38  poth
 correct determination of namespaces and requested properties implemented

 Revision 1.20  2006/09/25 20:32:39  poth
 methods for extracting all PropertyPath's required by a layer/style

 Revision 1.19  2006/09/08 08:42:01  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.18  2006/08/06 19:50:05  poth
 log statement removed

 Revision 1.17  2006/07/28 08:01:27  schmitz
 Updated the WMS for 1.1.1 compliance.
 Fixed some documentation.

 Revision 1.16  2006/07/14 11:32:05  poth
 support for xmlns:app=http://www.deegree.org/app and xmlns:apps=http://www.deegree.org/apps performing GetFeature requestes added. This is a coarse workaround since automytic detection of a features namespace is not supported yet

 Revision 1.15  2006/07/13 09:54:26  poth
 logging for generated GetFeature request added

 Revision 1.14  2006/04/25 19:28:52  poth
 *** empty log message ***

 Revision 1.13  2006/04/06 20:25:29  poth
 *** empty log message ***

 Revision 1.12  2006/03/30 21:20:27  poth
 *** empty log message ***

 Revision 1.11  2006/01/20 18:17:26  mschneider
 Adapted to use GMLFeatureCollectionDocument.

 Revision 1.9  2006/01/08 14:09:35  poth
 *** empty log message ***

 Revision 1.8  2005/12/22 20:12:32  poth
 no message

 Revision 1.7  2005/12/11 18:31:05  poth
 no message

 Revision 1.6  2005/11/14 22:15:37  poth
 no message

 Revision 1.5  2005/11/14 15:37:40  poth
 no message

 Revision 1.4  2005/09/27 19:53:19  poth
 no message

 Revision 1.3  2005/08/30 08:25:48  poth
 no message

 Revision 1.2  2005/07/18 07:00:50  poth
 no message

 Revision 1.1  2005/06/28 15:58:11  poth
 no message


 ********************************************************************** */