//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/DisplayMapListener.java,v 1.14 2006/11/27 09:07:53 poth Exp $
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

package org.deegree.portal.standard.csw.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcbase.BaseURL;
import org.deegree.ogcbase.ImageURL;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.LegendURL;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.Format;
import org.deegree.portal.context.FormatList;
import org.deegree.portal.context.General;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.Server;
import org.deegree.portal.context.Style;
import org.deegree.portal.context.StyleList;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.WebMapContextFactory;
import org.deegree.portal.context.XMLFactory;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;
import org.deegree.portal.standard.csw.model.DataSessionRecord;
import org.deegree.portal.standard.csw.model.ServiceSessionRecord;
import org.deegree.portal.standard.csw.model.ShoppingCart;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A <code>${type_name}</code> class.<br/>
 * 
 * This listener is called when one or more items of the shopping cart shall be displayed in a map.
 * For all chosen items of the cart the wms layers are collected and a new mapcontext containing 
 * these layers is created/saved in the users folder (user must be logged in). Then, the contextname 
 * is passed to the follow up page to be loaded in the portal. 
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.14 $, $Date: 2006/11/27 09:07:53 $
 */
public class DisplayMapListener extends AddToShoppingCartListener {
// extends AddToShoppingCartListener --> SimpleSearchListener --> AbstractListener.
    
    private static final ILogger LOG = LoggerFactory.getLogger( DisplayMapListener.class );
    
    private String userDir = "WEB-INF/conf/igeoportal/users/"; 
    
    public void actionPerformed( FormEvent event ) {
        LOG.entering();
        
        RPCWebEvent rpcEvent = (RPCWebEvent)event;
        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
        ShoppingCart cart = (ShoppingCart)session.getAttribute( Constants.SESSION_SHOPPINGCART );
        config = (CSWClientConfiguration)session.getAttribute( Constants.CSW_CLIENT_CONFIGURATION );
        
        try {
            validateRequest( rpcEvent );            
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpc request: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        List rpcDataSessionRecords = null;
        
        try {
            rpcDataSessionRecords = getDataSessionRecordsFromList( cart.getContents(), rpcEvent );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpc request.\n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        try {
            validateWMSAbility( rpcDataSessionRecords );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( 
                "At least one selected entry of ShoppingCart in the rpcEvent is not WMSable: \n" + 
                e.getMessage() );
            LOG.exiting();
            return;
        }
        
        // GetCapabilities block
        
        // addressTitlesMap(k=address, v=titleList)
        HashMap wmsAddressTitlesMap = createWMSAddressToTitlesMapping( rpcDataSessionRecords ); 

        // addressRequestMap(k=address, v=request)
        HashMap wmsAddressRequestMap = createCapabilitiesRequests( wmsAddressTitlesMap ); 
        
        // addressCapabilitiesMap(k=address, v=WMSCapabilities)
        HashMap wmsAddressCapabilityMap = null;
        try {
            wmsAddressCapabilityMap = createAddressToWMSCapabilitiesMapping( wmsAddressRequestMap );
        } catch( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot create WMSCapabilities object from the request url: \n" 
                           + e.getMessage() );
            LOG.exiting();
            return;
        }
        
//        HashMap titleCSWAddressMap = createTitleToCswAddressMap( rpcDataSessionRecords );
//        HashMap titleIdentifierMap = createTitleToIdentiferMap( rpcDataSessionRecords );
        
        ViewContext vc = null;
        try {
            vc = setContextLayers( wmsAddressTitlesMap, wmsAddressCapabilityMap, rpcDataSessionRecords );
        } catch( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot create context layers: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        // identify user block
        
        String contextName = "newContext";
        try {
            contextName = extractContextName( rpcEvent );
        }  catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot determin context name: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        String sessionId;
        try {
            sessionId = extractSessionId( rpcEvent );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot determin sessionId: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        String userName = null;
        try {
            userName = getUserNameForId( sessionId );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot determin user name: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        // create WebMapContext Document block
        
        try {
            vc.getGeneral().setTitle( contextName );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot set new context title: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        Envelope bbox = null;
        String msg = null;
        try {
            msg = "Cannot determin combining bbox: \n";
            bbox = determinCombiningBBox( rpcDataSessionRecords );
            if ( bbox != null ) {
                msg = "Cannot change bbox in view context: \n";
                changeBBox( vc, bbox );    
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( msg + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        try {
            storeContext( userName, contextName, vc );           
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Cannot store new context: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        // write contextname to request
        StringBuffer path2Dir = new StringBuffer( "users/" );
        path2Dir.append( userName ).append( "/" )
                .append( contextName ).append( ".xml" );
        this.getRequest().setAttribute( "CONTEXTNAME", path2Dir.toString() );
        
        LOG.exiting();
        return;
    }

    protected void validateRequest( RPCWebEvent rpcEvent ) throws CatalogClientException {
        LOG.entering();

        try {
            extractSessionId( rpcEvent );
        } catch ( CatalogClientException e ) {
            throw new CatalogClientException( "SESSION_ID must be set in the RPCWebEvent!"
                                              + e.getMessage() );
        }
        String contextName = null;
        try {
            contextName = extractContextName( rpcEvent );
        } catch ( CatalogClientException e ) {
            throw new CatalogClientException( "CONTEXT_NAME must be set in the RPCWebEvent!"
                                              + e.getMessage() );
        }
        if ( contextName == null || "null".equals( contextName ) ) {
            throw new CatalogClientException( "The context name must not be null." );
        }
        
        RPCParameter[] params = extractRPCParameters( rpcEvent );
        for( int i = 1; i < params.length; i++ ) {
            
            RPCStruct struct = extractRPCStruct( rpcEvent, 0 );
            struct = extractRPCStruct( rpcEvent, i );
            Object o = extractRPCMember( struct, Constants.RPC_IDENTIFIER );
            if ( o == null ) {
                throw new CatalogClientException( "Identifier must be set in the RPCWebEvent." );
            }
            o = extractRPCMember( struct, RPC_CATALOG );
            if ( o == null ) {
                throw new CatalogClientException( "Catalog must be set in the RPCWebEvent." );
            }
            o = extractRPCMember( struct, RPC_TITLE );
            if ( o == null ) {
                throw new CatalogClientException( "Title must be set in the RPCWebEvent." );
            }   
            
            RPCStruct bboxStruct = null;
            if ( struct.getMember( Constants.RPC_BBOX ) != null ) {
                bboxStruct = (RPCStruct)struct.getMember( Constants.RPC_BBOX ).getValue();
            }
            if ( bboxStruct != null ) {
                Double member = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMINX );
                if( member == null ) {
                    throw new CatalogClientException( "minX of the bounding box is not valid." );
                }
                member = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMINY );
                if( member == null ) {
                    throw new CatalogClientException( "minY of the bounding box is not valid." );
                }
                member = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMAXX );
                if( member == null ) {
                    throw new CatalogClientException( "maxX of the bounding box is not valid." );
                }
                member = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMAXY );
                if( member == null ) {
                    throw new CatalogClientException( "maxY of the bounding box is not valid." );
                }
            }
        }
        
        LOG.exiting();
        return;
    }

    /**
     * @param contents The List of DataSessionRecords to get DataSessionRecords from.
     * @param rpcEvent The RpcWebEvent contains the parameters defining the DataSessionRecords to 
     *        get from the passed List.
     * @return Returns a sublist of the passed contents, where each of the contained DataSessionRecords
     *         is defined by the parameters of the passed rpcEvent OR null, if no matches were found 
     *         in the passed List. 
     * @throws CatalogClientException 
     * @throws IllegalArgumentException, if if at least one record defined by the passed rpcEvent is 
     *         not part of the passed List.
     */
    private List getDataSessionRecordsFromList( List contents, RPCWebEvent rpcEvent ) 
                                                      throws CatalogClientException {
        
        RPCParameter[] params = extractRPCParameters( rpcEvent ); 
        List<DataSessionRecord> dsrList = new ArrayList<DataSessionRecord>( params.length );
        boolean matches = false;
        
        for( int i = 1; i < params.length; i++ ) {
            RPCStruct struct = extractRPCStruct( rpcEvent, i );
            String id = (String)struct.getMember( Constants.RPC_IDENTIFIER ).getValue();
            String catalog = (String)struct.getMember( RPC_CATALOG ).getValue();
            String title = (String)struct.getMember( RPC_TITLE ).getValue();
            
            DataSessionRecord dsr = getDataSessionRecordFromList( contents, id, catalog, title );
            if ( dsr == null ) {
                throw new IllegalArgumentException( 
                    "The parameter with identifier " + id + " is not part of the passed contents." );
            } 
            dsrList.add( dsr );
            matches = true;
        }        
        
        if ( matches ) {
            return dsrList; 
        } 
        return null;
    }
    
    /**
     * Checks, if each DataSessionRecord in the passed List contains at least one "OGC:WMS" 
     * serviceType.
     * 
     * @param dataSessionRecords List of DataSessionRecords in the ShoppingCart.
     * @throws CatalogClientException, if at least one record in the passed List has no WMS ability.
     */
    private void validateWMSAbility( List dataSessionRecords ) throws CatalogClientException {
        LOG.entering();
        
        for( int i = 0; i < dataSessionRecords.size(); i++ ) {
            boolean wmsAble = false;
            DataSessionRecord dsr = (DataSessionRecord)dataSessionRecords.get(i);
            for( int j = 0; j < dsr.getServices().length; j++ ) {
                if ( "OGC:WMS".equals( dsr.getServices()[j].getServiceType() ) ) {
                    wmsAble = true;
                    break;
                }
            }
            if ( !wmsAble ) {
                throw new CatalogClientException( 
                    "The record with identifier " + dsr.getIdentifier() + " is not WMSable." );
            }
        }
        
        LOG.exiting();
        return;
    }    

    /**
     * Creates a HashMap with the WebMapService address as key and a List of titles of the resources
     * as value. Both titles and addresses are taken from the passed List of DataSessionRecords.
     * For each DataSessionRecord only the first WMS address is used.  
     * 
     * @param dataSessionRecords
     * @return Returns a HashMap containing the wmsAddress (as key) and the title (as value). 
     */
    private HashMap<String, List<String>> createWMSAddressToTitlesMapping( List dataSessionRecords ) {
        LOG.entering();
        
        HashMap<String, List<String>> wmsAddressToTitlesMap = 
            new HashMap<String, List<String>>( dataSessionRecords.size() );
        
        for( int i = 0; i < dataSessionRecords.size(); i++ ) {
            String title =
                ( (DataSessionRecord)dataSessionRecords.get(i) ).getTitle();
            ServiceSessionRecord[] ssrs = 
                ( (DataSessionRecord)dataSessionRecords.get(i) ).getServices();
            for( int j = 0; j < ssrs.length; j++ ) {
                if ( "OGC:WMS".equals( ssrs[j].getServiceType() ) ) {
                    String addr = ssrs[j].getServiceAddress();

                    // since a WMS address might be used by more than one DataSessionRecord, 
                    // a List of titles for one address is needed.
                    ArrayList<String> titleList = null; 
                    if ( wmsAddressToTitlesMap.get( addr ) != null ) {
                        titleList = (ArrayList<String>)wmsAddressToTitlesMap.get( addr );
                        if ( ! titleList.contains( title ) ) { 
                            titleList.add( title );    
                        }
                    } else {
                        titleList = new ArrayList<String>( 10 );
                        titleList.add( title );
                    }
                    wmsAddressToTitlesMap.put( addr, titleList );
                    break; // only use the first WMS address for each DataSessionRecord.
                }
            }            
        }
        
        LOG.exiting();
        return wmsAddressToTitlesMap;
    }

    /**
     * @param addressTitlesMap
     *            Map from which the WMS addresses are taken.
     * @return Returns a HashMap containing the WMS address as key and the GetCapabilities request
     *         as value.
     */
    private HashMap<String, String> createCapabilitiesRequests( HashMap addressTitlesMap ) {
        LOG.entering();
        
        HashMap<String, String> addressRequestMap = 
            new HashMap<String, String>( addressTitlesMap.size() );
        
        for( Iterator it = addressTitlesMap.keySet().iterator(); it.hasNext(); ) {
            String address = (String)it.next();
            String request = null;
            if ( address.endsWith( "?" ) ) {
                request = address + "request=GetCapabilities&service=WMS";
            } else {
                request = address + "?request=GetCapabilities&service=WMS";
            }
            addressRequestMap.put( address, request );
        }
        
        LOG.exiting();
        return addressRequestMap;
    }

    /**
     * Creates a HashMap with the WebMapService address as key and a WMSCapabilities object as value. 
     * 
     * @param addressRequestMap The Map containing the address as key for the Map to be returned, 
     *        and the request as String, with which to create the Capability object.
     * @return Returns a HashMap containing the WMS address as key and a WMSCapabilities object as value.
     * @throws MalformedURLException
     * @throws XMLParsingException
     * @throws SAXException 
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws InvalidCapabilitiesException 
     */
    private HashMap<String, WMSCapabilities> createAddressToWMSCapabilitiesMapping( 
                            HashMap addressRequestMap )
                            throws MalformedURLException, IOException, 
                                   SAXException, InvalidCapabilitiesException {
        LOG.entering();
        
        HashMap<String, WMSCapabilities> addressCapabilitiesMap = 
            new HashMap<String, WMSCapabilities>( addressRequestMap.size() );
        
        for ( Iterator it = addressRequestMap.keySet().iterator(); it.hasNext(); ) {
            String address = (String)it.next();
            String request = (String)addressRequestMap.get( address );

            WMSCapabilitiesDocument capsDoc= new WMSCapabilitiesDocument();
            capsDoc.load( new URL( request ) );
            WMSCapabilities capabilities = (WMSCapabilities)capsDoc.parseCapabilities();

            addressCapabilitiesMap.put( address, capabilities );
        }
        
        LOG.exiting();
        return addressCapabilitiesMap;
    }
    
    /**
     * Creates a new ViewContext and sets the context Layers from the passed WMSCapabilities objects 
     * that match the corresponding titles of the passed addressTitlesMap. 
     * 
     * @param wmsAddressTitlesMap The Map contains the titles for which to search the matching layers.
     * @param wmsAddressCapabilitiesMap The Map contains the WMSCapabilities for each address.
     * @param dataSessionRecords The List contains the DataSessionRecords.
     * @return Returns a new ViewContext with the ContextLayers set.
     * @throws ContextException 
     * @throws SAXException 
     * @throws IOException 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private ViewContext setContextLayers( HashMap wmsAddressTitlesMap, HashMap wmsAddressCapabilitiesMap, 
                                          List dataSessionRecords ) 
                          throws ContextException, IOException, SAXException, XMLParsingException, UnknownCRSException {
        LOG.entering();
        
        List<org.deegree.portal.context.Layer> contextLayerList = 
            new ArrayList<org.deegree.portal.context.Layer>( 10 );
        
        File file = new File( getHomePath() + config.getMapContextTemplatePath() );
        ViewContext vc = WebMapContextFactory.createViewContext( file.toURL(), null, null ); 
        Point[] env = vc.getGeneral().getBoundingBox();
        String[] srs = new String[] { env[0].getCoordinateSystem().getName() };
        
        Format[] formats = new Format[1];
        formats[0] = new Format( "image/jpeg", true );
        FormatList formatList = new FormatList( formats );
        
        for ( Iterator it = wmsAddressTitlesMap.keySet().iterator(); it.hasNext(); ) {
            String address = (String)it.next();
            List titles = (List)wmsAddressTitlesMap.get( address );
            
            for (int i = 0; i < titles.size(); i++ ) {
                String title = (String)titles.get(i);
                
                WMSCapabilities capabilities = 
                    (WMSCapabilities)wmsAddressCapabilitiesMap.get( address );
                Layer layer = getLayer( capabilities, title );

                BaseURL metadataURL = createMetadataURL( dataSessionRecords, title );
                
                ImageURL imageURL = getLegendURL(address, capabilities, layer);
                Style[] styles = new Style[1];
                styles[0] = new Style( "default", "default", "", imageURL, true );
                StyleList styleList = new StyleList( styles );
                
                Server server = new Server( capabilities.getServiceIdentification().getTitle(), 
                                            capabilities.getVersion(), "OGC:WMS", new URL(address),
                                            capabilities );

                org.deegree.portal.context.Layer contextLayer = 
                    new org.deegree.portal.context.Layer( server, layer.getName(), layer.getTitle(),
                                                          layer.getAbstract(), srs, null, metadataURL, 
                                                          formatList, styleList, layer.isQueryable(), 
                                                          false, null );
                
                contextLayerList.add( contextLayer );
            }
        }
        
        org.deegree.portal.context.Layer[] contextLayers = 
            new org.deegree.portal.context.Layer[ contextLayerList.size() ];
        contextLayers = contextLayerList.toArray( contextLayers );
        
        vc.setLayerList( new LayerList( contextLayers ) );
        
        LOG.exiting();
        return vc; 
    }

    /**
     * Creates a MetadataURL for a layer of the passed title.
     * 
     * @param dataSessionRecords The List contains information necessary for creating the URL.
     * @param title The layer's title.
     * @return Returns the MetadataURL for the passed title. 
     * @throws MalformedURLException
     */
    private BaseURL createMetadataURL( List dataSessionRecords, String title )
                            throws MalformedURLException {
        
        String catalogAddress = getCatalogAddressToTitle( dataSessionRecords, title );
        String identifier = getIdentifierToTitel( dataSessionRecords, title );
        
        StringBuffer  sb = new StringBuffer( catalogAddress );
        if ( sb.lastIndexOf("?") != sb.length() - 1 ) {
            sb.append( "?" );
        }
        sb.append( "request=GetRecordById&version=2.0.0&elementsetname=full&id=" );
        sb.append( identifier ); 
        
        URL onlineResource = new URL( sb.toString() );
        String format = "text/xml";
        
        BaseURL metadataURL = new BaseURL( format, onlineResource );
        
        return metadataURL;
    }

    /**
     * Gets the catalog address from the configuration file for the catalog that is part of the same 
     * DataSessionRecord in the passed list as the passed title. 
     * 
     * @param dataSessionRecords List of DataSessionRecords
     * @param title
     * @return Returns the catalog address for the passed title.
     */
    private String getCatalogAddressToTitle( List dataSessionRecords, String title ) {
        String catalogAddress = null;
        String catalogName;
        for( int i = 0; i < dataSessionRecords.size(); i++ ) {
            String dsrTitle =
                ( (DataSessionRecord)dataSessionRecords.get(i) ).getTitle();
            if ( title.equals( dsrTitle ) ) {
                catalogName = ( (DataSessionRecord)dataSessionRecords.get(i) ).getCatalogName();
                catalogAddress = config.getCatalogServerAddress( catalogName  );
                break;
            }
        }       
        return catalogAddress;
    }
    
    /**
     * Gets the identifier that is part of the same DataSessionRecord in the passed list as 
     * the passed title. 
     * 
     * @param dataSessionRecords List of DataSessionRecords
     * @param title
     * @return Returns the catalog address for the passed title.
     */
    private String getIdentifierToTitel( List dataSessionRecords, String title ) {
        String identifier = null;
        for( int i = 0; i < dataSessionRecords.size(); i++ ) {
            String dsrTitle =
                ( (DataSessionRecord)dataSessionRecords.get(i) ).getTitle();
            if ( title.equals( dsrTitle ) ) {
                identifier = ( (DataSessionRecord)dataSessionRecords.get(i) ).getIdentifier();
                break;
            }
        }       
        return identifier;
    }
    
    /**
     * @param address
     * @param capabilities
     * @param layer
     * @return Returns the getLegendGraphic request url.
     * @throws MalformedURLException
     */
    private ImageURL getLegendURL( String address, WMSCapabilities capabilities, Layer layer ) 
                                                                throws MalformedURLException {
        LOG.entering();
        
        org.deegree.ogcwebservices.wms.capabilities.Style style = 
            layer.getStyleResource( "default:" + layer.getName() );
        if( style == null ) {
            style = layer.getStyleResource( "default" );
        }
        
        ImageURL imageURL = null;
        if ( style != null && style.getLegendURL() != null && style.getLegendURL().length > 0 ) {
            LegendURL lu = style.getLegendURL()[0];
            imageURL = 
                new ImageURL( lu.getWidth(), lu.getHeight(), lu.getFormat(), lu.getOnlineResource() );
        } else {
            StringBuffer sb = new StringBuffer( 5000 ); 
            sb.append( address )
              .append( "?VERSION=").append( capabilities.getVersion() )
              .append( "&STYLE=&REQUEST=GetLegendGraphic&FORMAT=image/jpeg&WIDTH=50&HEIGHT=50" )
              .append( "&EXCEPTIONS=application/vnd.ogc.se_inimage" )
              .append( "&LAYER=" ).append( layer.getName() );
            imageURL = new ImageURL( 50, 50, "image/jpeg", new URL( sb.toString() ) );
        }
        
        LOG.exiting();
        return imageURL;
    }

    /**
     * Returns the Layer identified by the submitted name. If no Layer matches the title 
     * <tt>null</tt> will be returned.
     *
     * @param capabilities
     * @param title The title of the requested layer.
     * @return Returns a layer object or <tt>null</tt>
     */
    private Layer getLayer( WMSCapabilities capabilities, String title ) {
        Layer layer = capabilities.getLayer();
        Layer lay = null;
        
        if ( layer.getTitle() != null && title.equals( layer.getTitle() ) ) {
            lay = layer;
        } else {
            lay = getLayer( title, layer.getLayer() );
        }
        return lay;
    }

    /**
     * Recursion over all layers to find the layer that matches the submitted
     * title. If no layer can be found that fullfills the condition <tt>null</tt>
     * will be returned.
     *
     * @param title The title of the layer to be found
     * @param layers An array of layers to be searched.
     * @return Returns a layer object or <tt>null</tt>
     */
    private Layer getLayer( String title, Layer[] layers ) {
        Layer lay = null;

        if ( layers != null ) {
            for ( int i = 0; i < layers.length; i++ ) {
                // TODO
                // remove break statements
                if ( layers[i].getTitle() != null && title.equals( layers[i].getTitle() ) ) {
                    lay = layers[i];
                    break;
                } 
                lay = getLayer( title, layers[i].getLayer() );
                if ( lay != null ) break;
                
            }
        }
        return lay;
    }

    /**
     * Extracts the context name from the RPCStruct of the first parameter in the passed RPCWebEvent.
     * 
     * @param rpcEvent
     * @return Returns the context (file) name from the rpcEvent.
     * @throws CatalogClientException
     */
    private String extractContextName( RPCWebEvent rpcEvent ) throws CatalogClientException {
        RPCStruct struct = extractRPCStruct( rpcEvent, 0 );
        return (String)extractRPCMember( struct, "CONTEXT_NAME" );
    }
    
    /**
     * Extracts the sessionId from the RPCStruct of the first parameter in the passed RPCWebEvent. 
     * 
     * @param rpcEvent
     * @return Returns the sessionId from the rpcEvent.
     * @throws CatalogClientException
     */
    private String extractSessionId( RPCWebEvent rpcEvent ) throws CatalogClientException {
        RPCStruct struct = extractRPCStruct( rpcEvent, 0 );
        return (String)extractRPCMember( struct, "SESSION_ID" );
    }

    /**
     * Gets the user name assigned to the passed session ID from an authentication service. 
     * 
     * If the session ID equals "null", then the default user name is returned.
     * If no user is assigned to the session ID a CatalogClientException will be returned. 
     * If the session is closed or expired an exception will be thrown.
     * 
     * @param sessionId
     * @return Returns the user name for the passed session id.
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws TransformerException 
     * @throws CatalogClientException 
     * @throws XMLParsingException 
     */
    private String getUserNameForId( String sessionId ) 
                            throws IOException, SAXException, CatalogClientException, XMLParsingException {
        LOG.entering();
        
        if ( "null".equals( sessionId ) ) {
            System.out.println( "sessionId is 'null' => user is 'default'\n" );
            return "default";
        }
        
        HttpSession session = ( (HttpServletRequest) getRequest()).getSession( true );       
        ViewContext vc = (ViewContext)session.getAttribute( Constants.CURRENTMAPCONTEXT );
        GeneralExtension ge = vc.getGeneral().getExtension();

        BaseURL baseUrl = ge.getAuthentificationSettings().getAuthentificationURL();
        StringBuffer sb = new StringBuffer( NetWorker.url2String( baseUrl.getOnlineResource() ) );
        sb.append("?request=GetUser&SESSIONID=").append( sessionId );
        
        NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), new URL( sb.toString() ) );   
        Reader reader = new InputStreamReader( nw.getInputStream() );
        Document doc = XMLTools.parse( reader );
        // Node nsNode = XMLTools.getNamespaceNode(new HashMap());
        
        String tmp = null;
        tmp = XMLTools.getNodeAsString( doc.getDocumentElement(), "/User/Name", nsContext, null );
        if ( tmp == null ) {
            throw new CatalogClientException("Cannot get user name for sessionId: " + sessionId );
        }        
        
        LOG.exiting();
        return tmp;
    }
    
    /**
     * Determins the minimum and maximum values for x and y for the bounding box of each 
     * DataSessionRecord in the passed <code>List</code> and uses these values to determin an all 
     * combining bounding box. If none of the passed records has a bounding box, then null is returned.
     *   
     * @param rpcDataSessionRecords List of DataSessionRecords for which to determin a combining 
     *        bounding box.
     * @return Returns a new GM_Envelope for a combining bounding box, OR null, if no bounding 
     *         box was set in any of the passed DataSessionRecords.
     * @throws UnknownCRSException 
     * @throws CatalogClientException
     */
    private Envelope determinCombiningBBox( List rpcDataSessionRecords ) throws UnknownCRSException  {
        LOG.entering();
        
        Envelope bbox = null;
        
        // upper left corner of combining bounding box
        double minx = Double.MAX_VALUE;
        double maxy = Double.MIN_VALUE; 
        // lower right corner of combining bounding box
        double maxx = Double.MIN_VALUE;
        double miny = Double.MAX_VALUE;
        
        int count = 0;
        for( int i = 0; i < rpcDataSessionRecords.size(); i++ ) {
            DataSessionRecord dsr = (DataSessionRecord)rpcDataSessionRecords.get(i);
            if( dsr.getBoundingBox() != null ) {
                count++;
                double tempMinx, tempMiny, tempMaxx, tempMaxy;
                tempMinx = dsr.getBoundingBox().getMin().getX();
                tempMiny = dsr.getBoundingBox().getMin().getY();
                tempMaxx = dsr.getBoundingBox().getMax().getX();
                tempMaxy = dsr.getBoundingBox().getMax().getY();
                
                // find min for x and y
                minx = Math.min( tempMinx, minx );
                miny = Math.min( tempMiny, miny );
                // find max for x and y
                maxx = Math.max( tempMaxx, maxx );
                maxy = Math.max( tempMaxy, maxy );
            }            
        } 
        if ( count > 0 ) {
            CoordinateSystem srs = CRSFactory.create( config.getSrs() );
            bbox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, srs );
        }
        
        LOG.exiting();
        return bbox;
    }

    /**
     * Changes the bounding box of a given view context.
     * 
     * @param vc The view context to be changed
     * @param bbox The new bounding box
     * @throws ContextException
     */
    private void changeBBox( ViewContext vc, Envelope bbox ) throws ContextException {
        General general = vc.getGeneral();                        
        CoordinateSystem cs = general.getBoundingBox()[0].getCoordinateSystem();
        Point[] p = new Point[]{ GeometryFactory.createPoint( bbox.getMin(),cs ),
                                 GeometryFactory.createPoint( bbox.getMax(),cs ) }; 
        general.setBoundingBox( p );
    }
    
    /**
     * @param userName
     * @param contextName
     * @param vc
     * @throws ParserConfigurationException
     * @throws ClientException
     * @throws IOException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws PortalException 
     */
    private void storeContext( String userName, String contextName, ViewContext vc ) 
                               throws PortalException,
                                      TransformerFactoryConfigurationError {
        LOG.entering();
        
        // save new context
        StringBuffer path2Dir = new StringBuffer( getHomePath() );
        path2Dir.append( userDir );
        path2Dir.append( userName );
        File file = new File( path2Dir.toString() );
        if ( !file.exists() ) {
            // create directory if not existent
            file.mkdir();
        }
        path2Dir.append( "/" ).append( contextName ).append( ".xml" );
        
//        Document doc = XMLFactory.export( vc );
//        FileOutputStream fos = new FileOutputStream( path2Dir.toString() );
//        internalSave( new StreamResult(fos), doc );
//        fos.close();
        saveDocument( vc, path2Dir.toString() );
        
        LOG.exiting();
        return;
    }
    
//    /** 
//     * common method to save xml
//     *
//     * @param result
//     * @param doc
//     * @throws TransformerFactoryConfigurationError 
//     * @throws TransformerException 
//     */
//    protected static void internalSave(Result result, Document doc)
//                                throws TransformerFactoryConfigurationError, TransformerException {
//            Source source = new DOMSource(doc);
//            Transformer transformer = 
//                TransformerFactory.newInstance().newTransformer();
//            transformer.transform(source, result);
//    }
    
    /**
     * saves the new context as xml
     * 
     * @param vc
     * @param filename
     * @throws PortalException
     */
    public static final void saveDocument( ViewContext vc, String filename )
                            throws PortalException {
        try {
            XMLFragment xml = XMLFactory.export( vc );
            FileOutputStream fos = new FileOutputStream( filename );
            xml.write( fos );
            fos.close();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new PortalException( "could not save file '" + filename + "'\n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new PortalException( "could not save file '" + filename + "'\n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        } catch ( ParserConfigurationException e ) {
            e.printStackTrace();
            throw new PortalException( "could not save file '" + filename + "'\n"
                                       + StringTools.stackTraceToString( e.getStackTrace() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DisplayMapListener.java,v $
Revision 1.14  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.13  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.12  2006/10/12 13:07:41  mays
bugfixin createCapabilitiesRequests: check if http address ends with ?

Revision 1.11  2006/08/24 12:15:52  poth
Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)

Revision 1.10  2006/08/20 20:53:54  poth
changes rquired as a consequence of bug fix in wmc implementation/handling. Instead of determining the correct URL as given in a services capabilities deegree always has used the base URL which is just guarenteed to be valid for GetCapabilities requests.

Revision 1.9  2006/08/07 10:51:46  poth
never thrown exception removed / unnecessary type casts removed

Revision 1.8  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.7  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.6  2006/06/30 08:43:19  mays
clean up code and java doc

Revision 1.5  2006/06/29 14:08:32  mays
add metadataURL and legendURL to Layer information

Revision 1.4  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
