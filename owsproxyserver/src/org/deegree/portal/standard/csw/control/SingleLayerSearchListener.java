//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/SingleLayerSearchListener.java,v 1.7 2006/10/17 20:31:18 poth Exp $
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

package org.deegree.portal.standard.csw.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.ParameterList;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.csw.discovery.GetRecordByIdResultDocument;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.Module;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.MetadataTransformer;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A <code>${type_name}</code> class.<br/>
 * This class handles a CSW metadata search request for a single WMS layer. The layer for which to 
 * search the metedata needs to contain a MetadataURL tag with the address of the corresponding CSW.  
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/10/17 20:31:18 $
 * 
 * @since 2.0
 */
public class SingleLayerSearchListener extends OverviewMetadataListener {
//  extends OverviewMetadataListener --> SimpleSearchListener --> AbstractListener.
    private static final ILogger LOG = LoggerFactory.getLogger( SingleLayerSearchListener.class );
    
    public void actionPerformed( FormEvent event ) {
        LOG.entering();

        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
        config = (CSWClientConfiguration) session.getAttribute( Constants.CSW_CLIENT_CONFIGURATION );

        if ( config == null ) {
            try {
                config = initConfiguration( session );
            } catch ( Exception e ) {
                e.printStackTrace();
                gotoErrorPage( "Could not initialize CSW Client Module." + e.getMessage() );
                LOG.exiting();
                return;
            }
        }

        nsContext = CommonNamespaces.getNamespaceContext();
        // TODO remove if not needed        
//        try {
//            nsNode = XMLTools.getNamespaceNode( config.getNamespaceBindings() );
//        } catch ( ParserConfigurationException e ) {
//            e.printStackTrace();
//            gotoErrorPage( "Could not create namespace node for SingleLayerSearchListener."
//                           + e.getMessage() );
//            LOG.exiting();
//            return;
//        }

        RPCWebEvent rpcEvent = (RPCWebEvent) event;

        // get transformation file name
        String fileName = "metaContent2html.xsl"; // default value
        // FIXME replace format with current value
        String format = "Profiles.ISO19115";
        HashMap xslMap = config.getProfileXSL( format );
        if ( xslMap != null ) {
            if ( xslMap.get( "full" ) != null ) {
                fileName = (String) xslMap.get( "full" );
            }
        }
        String pathToXslFile = "file:" + getHomePath() + "WEB-INF/conf/igeoportal/" + fileName;

        try {
            validateRequest( rpcEvent );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid Request: " + e.getMessage() );
            LOG.exiting();
            return;
        }

        String rpcCatalog;
        RPCStruct rpcStruct;
        String metadataURL;

        try {
            rpcStruct = extractRPCStruct( rpcEvent, 0 );
            rpcCatalog = (String) extractRPCMember( rpcStruct, RPC_CATALOG );
            metadataURL = (String) extractRPCMember( rpcStruct, "METADATA_URL" );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpcEvent: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        // "GetRecordById"-request
        HashMap<String, Document> result = new HashMap<String, Document>( 1 );
        try {
            //URL url = new URL( metadataURL );
            //Document doc = XMLTools.parse( url.openStream() );
            GetRecordByIdResultDocument doc = new GetRecordByIdResultDocument ();
            doc.load(new URL (metadataURL));
            result.put( rpcCatalog, doc.getRootElement().getOwnerDocument() );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Server is not reachable or did not answer with valid XML: \n"
                           + e.getMessage() );
            return;
        }

        // handle result: take result and transform it to produce html output
        try {
            handleResult( result, pathToXslFile, "overview" );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Error handling result: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        LOG.exiting();
    }

    /**
     * @param session
     * @return CSWClientConfiguration
     * @throws CatalogClientException
     * @throws RemoteException
     */
    private CSWClientConfiguration initConfiguration( HttpSession session )
                            throws CatalogClientException, RemoteException {
        LOG.entering();

        InitCSWModuleListener iml = new InitCSWModuleListener();
        ViewContext vc = 
            (ViewContext) session.getAttribute( org.deegree.portal.Constants.CURRENTMAPCONTEXT );
        GeneralExtension gen = (GeneralExtension) vc.getGeneral().getExtension();
        Module module = null;

        try {
            module = iml.findCswClientModule( gen );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Client error: " + e.getMessage() );
        }
        CSWClientConfiguration config = new CSWClientConfiguration();

        ParameterList parList = module.getParameter();
        iml.initConfig( config, parList );

        String srs = "EPSG:4236";
        srs = vc.getGeneral().getBoundingBox()[0].getCoordinateSystem().getName();
        config.setSrs( srs );

        session.setAttribute( Constants.CSW_CLIENT_CONFIGURATION, config );

        LOG.exiting();
        return config;
    }

    protected void validateRequest( RPCWebEvent rpcEvent )
                            throws CatalogClientException {
        LOG.entering();

        RPCParameter[] params = extractRPCParameters( rpcEvent );
        if ( params.length != 1 ) {
            throw new CatalogClientException( "Request/Method Call must contain one parameter, not: "
                                              + params.length );
        }

        RPCStruct struct = extractRPCStruct( rpcEvent, 0 );

        extractRPCMember( struct, "METADATA_URL" );
        extractRPCMember( struct, "METADATA_TITLE" );
        
        // validity check for catalog
        String rpcCatalog = (String) extractRPCMember( struct, RPC_CATALOG );
        String[] catalogs = config.getCatalogNames();
        boolean containsCatalog = false;
        for ( int i = 0; i < catalogs.length; i++ ) {
            if ( catalogs[i].equals( rpcCatalog ) ) {
                containsCatalog = true;
                break;
            }
        }
        if ( !containsCatalog ) {
            throw new CatalogClientException( "The catalog " + rpcCatalog + 
                                              " is not configured for this client." );
        }

        LOG.exiting();
    }

    // super.createRequest();

    // super.performeRequest();

    /**
     * @param result
     * @param pathToXslFile 
     *           e.g. file://$iGeoPortal_home$/WEB-INF/conf/igeoportal/metaOverview2html.xsl
     * @param metaVersion 
     *           e.g. overview, detailed
     * @throws XMLParsingException
     * @throws CatalogClientException
     * @throws TransformerException
     * @throws IOException
     */
    protected void handleResult( Object result, String pathToXslFile, String metaVersion )
                            throws XMLParsingException, CatalogClientException,
                            TransformerException, IOException {
        LOG.entering();

        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );

        // result is a very short hashmap with only one entry!
        Map map = (HashMap) result;
        // key = data catalog name; value = csw:GetRecordByIdResponse
        Iterator it = map.keySet().iterator();

        String catalog = null;
        Document doc = null;
        String docString = null;

        URL u = null;
        StringBuffer htmlFragment = new StringBuffer( 5000 );
        MetadataTransformer mt = null;
        try {
            u = new URL( pathToXslFile );
            mt = new MetadataTransformer( u.getFile() );
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }

        while ( it.hasNext() ) {
            catalog = (String) it.next();
            doc = (Document) map.get( catalog );
            docString = DOMPrinter.nodeToString( doc, CharsetUtils.getSystemCharset() );
            Reader reader = new StringReader( docString );

            List nl = extractMetadata( doc );
            if ( nl.size() > 1 ) {
                throw new CatalogClientException( 
                    "The result contains a Document with too many metadata nodes." );
            }
            
            String xPathToTitle = config.getXPathToDataTitleFull();
            String title = extractValue( (Node)nl.get( 0 ), xPathToTitle );
            String[] serviceCatalogs = null;
            Map catalogsMap = 
                (HashMap)session.getAttribute( SESSION_AVAILABLESERVICECATALOGS );
            if ( catalogsMap != null ) {
                serviceCatalogs = extractServiceCatalogs( catalogsMap, title );
            }

            // transformation
            htmlFragment.append( mt.transformMetadata( reader, catalog, serviceCatalogs, 0, 0,
                                                       metaVersion ) );
        }

        this.getRequest().setAttribute( HTML_FRAGMENT, htmlFragment.toString() );
        session.setAttribute( SESSION_METADATA, result );

        LOG.exiting();
        return;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to: 
$Log: SingleLayerSearchListener.java,v $
Revision 1.7  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.6  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.5  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.4  2006/06/30 12:47:16  mays
set serviceCatalogs if information is already provided in the session

Revision 1.3  2006/06/30 08:43:18  mays
clean up code and java doc

Revision 1.2  2006/06/29 14:11:52  mays
adjust pathToXslFile;
replace XMLTools with specialised XMLFragment

Revision 1.1  2006/06/23 13:38:41  mays
add/update csw control files

********************************************************************** */
