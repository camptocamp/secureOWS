//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/OverviewMetadataListener.java,v 1.16 2006/10/17 20:31:18 poth Exp $
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
import java.util.ArrayList;
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
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.MetadataTransformer;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A <code>${type_name}</code> class.<br/>
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.16 $, $Date: 2006/10/17 20:31:18 $
 * 
 * @since 2.0
 */
public class OverviewMetadataListener extends SimpleSearchListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( OverviewMetadataListener.class );
    
    static final String SESSION_METADATA = "SESSION_METADATA";
    
    public void actionPerformed( FormEvent event ) {
        LOG.entering(); 
        
        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
        config = (CSWClientConfiguration)session.getAttribute( Constants.CSW_CLIENT_CONFIGURATION );
        
        
        nsContext = CommonNamespaces.getNamespaceContext();
        // TODO remove if not needed
//        try {
//            nsNode = XMLTools.getNamespaceNode( config.getNamespaceBindings() );
//        } catch( ParserConfigurationException e ) {
//            e.printStackTrace();
//            gotoErrorPage( "Could not create namespace node for OverviewMetadataListener." 
//                           + e.getMessage() );
//            LOG.exiting();
//            return;
//        }
        
        RPCWebEvent rpcEvent = (RPCWebEvent)event;
        RPCParameter[] params;
        try {
            params = extractRPCParameters( rpcEvent );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpcEvent: " + e.getMessage() );
            LOG.exiting();
            return;
        }
        
        // get transformation file name
        String fileName = "metaOverview2html.xsl"; // default value
        // FIXME replace format with current value
        String format = "Profiles.ISO19115";
        HashMap xslMap = config.getProfileXSL( format );
        if ( xslMap != null) {
            if ( xslMap.get( "full" ) != null ) {
                fileName = (String)xslMap.get( "full" );
            }
        }
        String pathToXslFile = "file:" + getHomePath() + "WEB-INF/conf/igeoportal/" + fileName;
        
        if ( params == null || params.length == 0 ) {
            // get Metadata from the users session
            session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
            Object o = session.getAttribute( SESSION_METADATA );
            
            if ( o != null ) {
                try {
                    handleResult( o, pathToXslFile, "overview" );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    gotoErrorPage( "Error handling result 1: \n" + e.getMessage() );
                    LOG.exiting();
                    return;
                }
            } 
        } else {
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
            String rpcFormat;
            String rpcProtocol;
            
            try {
                rpcStruct = extractRPCStruct( rpcEvent, 0 );
                rpcCatalog =(String)extractRPCMember( rpcStruct, RPC_CATALOG );
                rpcFormat = (String)extractRPCMember( rpcStruct, RPC_FORMAT );
                rpcProtocol = (String)extractRPCMember( rpcStruct, Constants.RPC_PROTOCOL );
            } catch ( Exception e ) {
                e.printStackTrace();
                gotoErrorPage( "Invalid rpcEvent: \n" + e.getMessage() );
                LOG.exiting();
                return;
            }

            
            // "GetRecordById"-request
            String req = null;
            HashMap result = null;
            try {
                req = createRequest( rpcStruct, rpcFormat, null );
            } catch ( Exception e ) {
                e.printStackTrace();
                gotoErrorPage( "Invalid Request: \n" + e.getMessage() );
                LOG.exiting();
                return;
            }
            try {
                List<String> dataCatalogs = new ArrayList<String>(1);
                dataCatalogs.add( rpcCatalog );
                
                // key = data catalog name; value = csw:GetRecordByIdResponse
                result = performRequest( rpcProtocol, req, dataCatalogs, null );
                // result is a HashMap that contains only one key-value-pair, 
                // because dataCatalogs contains only one catalog.
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
                gotoErrorPage( "Error handling result 2: \n" + e.getMessage() );
                LOG.exiting();
                return;
            }
            
        }
        
        LOG.exiting();
    }
    
    
    protected void validateRequest( RPCWebEvent rpcEvent ) throws CatalogClientException {
        LOG.entering();
        
        RPCParameter[] params = extractRPCParameters( rpcEvent ); 
        if ( params.length != 1 ) {
            throw new CatalogClientException( "Request/Method Call must contain one parameter, not: " 
                                              + params.length );
        }
        
        RPCStruct struct = extractRPCStruct( rpcEvent, 0 );
        
        // validity check for catalog
        String rpcCatalog = (String)extractRPCMember( struct, RPC_CATALOG );
        String[] catalogs = config.getCatalogNames();
        boolean containsCatalog = false;
        for ( int i = 0; i < catalogs.length; i++ ) {
            if ( catalogs[i].equals(rpcCatalog) ) {
                containsCatalog = true;
                break;
            }
        }
        if ( !containsCatalog ) {
            throw new CatalogClientException( "The catalog " + rpcCatalog + " is not " +
                                              "configured for this client.");
        }
        
        // validity check for format 
        // is requested catalog capable to serve requested metadata format?
        List formats = config.getCatalogFormats( rpcCatalog );
        String rpcFormat = (String)extractRPCMember( struct, RPC_FORMAT );
        if ( ! formats.contains( rpcFormat ) ) {
            throw new CatalogClientException( "The catalog " + rpcCatalog + " does not " +
                                              "provide the requested format " + rpcFormat );
        }
        
        // validity check for protocol 
        // is requested catalog reachable through requested protocol?
        List protocols = config.getCatalogProtocols( rpcCatalog );
        String rpcProtocol = (String)extractRPCMember( struct, Constants.RPC_PROTOCOL );
        if ( ! protocols.contains( rpcProtocol ) ) {
            throw new CatalogClientException( "The catalog " + rpcCatalog + " does not " +
                                              "provide the requested protocol " + rpcProtocol );
        }
        
        try {
            struct.getMember( Constants.RPC_IDENTIFIER ).getValue();
        } catch (Exception e) {
            throw new CatalogClientException( "The identifier must be set." );
        } 
        
        // validity check for bounding box values
        RPCStruct bBox;
        try {
            bBox = (RPCStruct)struct.getMember( Constants.RPC_BBOX ).getValue();
        } catch (Exception e) {
            throw new CatalogClientException( "The bounding box must be set." ); 
        }
        double minx, miny, maxx, maxy;
        try {
            minx = ((Double)bBox.getMember( Constants.RPC_BBOXMINX ).getValue()).doubleValue();
            miny = ((Double)bBox.getMember( Constants.RPC_BBOXMINY ).getValue()).doubleValue();
            maxx = ((Double)bBox.getMember( Constants.RPC_BBOXMAXX ).getValue()).doubleValue();
            maxy = ((Double)bBox.getMember( Constants.RPC_BBOXMAXY ).getValue()).doubleValue();
        } catch (Exception e) {
            throw new CatalogClientException( "All bounding box values (minx, miny, maxx, maxy) " +
                                              "must be set as decimal numbers." );
        }
        if ( minx > maxx || miny > maxy ) {
            throw new CatalogClientException("the requested bounding box has invalid values. \n" +
                                             "maxx must be larger than minx and maxy must be " +
                                             "larger than miny.");
        }
        
        LOG.exiting();
    }
    
    // super.createRequest();
    
    // super.performeRequest();
    
    /**
     * @param result
     * @param pathToXslFile
     *            e.g. file://$iGeoPortal_home$/WEB-INF/conf/igeoportal/metaOverview2html.xsl
     * @param metaVersion
     *            e.g. overview, detailed
     * @throws XMLParsingException
     * @throws CatalogClientException
     * @throws TransformerException
     * @throws IOException
     */
    protected void handleResult( Object result, String pathToXslFile, String metaVersion )
                            throws XMLParsingException, CatalogClientException,
                            TransformerException, IOException {
        LOG.entering();
        
        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
        
        // result is a very short hashmap with only one entry!
        Map map = (HashMap)result;
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        while( it.hasNext() ) {            
            catalog = (String)it.next();
            doc = (Document)map.get( catalog );
            docString = DOMPrinter.nodeToString( doc, CharsetUtils.getSystemCharset() );
            Reader reader = new StringReader(docString);
            
            List nl = extractMetadata( doc );
            if ( nl.size() > 1 ) {
                throw new CatalogClientException( 
                    "The result contains a Document with too many metadata nodes." );
            }
            
            String xPathToTitle = config.getXPathToDataTitleFull();
            String title = extractValue( (Node)nl.get( 0 ), xPathToTitle ) ;
            String[] serviceCatalogs = null;
            Map catalogsMap = 
                (HashMap)session.getAttribute( SESSION_AVAILABLESERVICECATALOGS );
            if ( catalogsMap != null ) {
                serviceCatalogs = extractServiceCatalogs( catalogsMap, title );
            }
            
            // transformation
            htmlFragment.append( mt.transformMetadata( reader, catalog, serviceCatalogs, 0, 0, metaVersion ) );
        } 
        
        this.getRequest().setAttribute( HTML_FRAGMENT, htmlFragment.toString() );
        session.setAttribute( SESSION_METADATA, result );
        
        LOG.exiting();
    }

    
    /**
     * Extracts all Metadata nodes from the passed csw:GetRecordByIdResponse Document. 
     * 
     * @param doc The csw:GetRecordByIdResponse Document from which to extract the Metadata nodes.
     * @return Returns a NodeList of Metadata Elements for the passed Document.
     * @throws CatalogClientException 
     *         if metadata nodes could not be extracted from the passed Document.
     * @throws XMLParsingException 
     */
    protected List extractMetadata( Document doc ) throws CatalogClientException, XMLParsingException {
        
        List nl = null;
        
        String xPathToMetadata = "csw:GetRecordByIdResponse/child::*";
        
        nl = XMLTools.getNodes( doc, xPathToMetadata, nsContext );

        if ( nl == null || nl.size() < 1 ){
            throw new CatalogClientException( "could not extract metadata nodes." );
        }
        
        return nl;
    }
    
    /**
     * Extracts a List of available service catalogues from the Map in the session and returns its 
     * contents as an Array of Strings.
     * 
     * @param catalogsMap The Map containing the data title (as key) and the List of available 
     *                    service catalogues names (as value)
     * @param title The key for the value in the passed Map. 
     * @return Returns an Array of Strings for the available service catalogues. 
     *         If no service catalogues are available, an Array of one empty String is returned.
     */
    protected String[] extractServiceCatalogs( Map catalogsMap, String title ) {
        
        String[] catalogs = null;
        
        List catalogsList = (ArrayList)catalogsMap.get( title );

        if ( catalogsList != null ) {
            catalogs = new String[ catalogsList.size() ];
            for( int i = 0; i < catalogsList.size(); i++ ) {
                catalogs[i] = (String)catalogsList.get(i);
            }
        } else {
            catalogs = new String[] { "" };    
        }
        
        return catalogs;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OverviewMetadataListener.java,v $
Revision 1.16  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.15  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.14  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.13  2006/06/30 12:38:34  mays
set serviceCatalogs if information is already provided in the session

Revision 1.12  2006/06/30 08:43:18  mays
clean up code and java doc

Revision 1.11  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
