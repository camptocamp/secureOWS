// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/SimpleSearchListener.java,v 1.12 2006/10/17 20:31:18 poth Exp $
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCFactory;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.MetadataTransformer;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;
import org.deegree.portal.standard.csw.model.DataSessionRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A <code>${type_name}</code> class.<br/> This listener does more than just search for data. It
 * searches for data *and* then searches if there are services (WMS, WFS) available, which provide
 * this data.
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.12 $, $Date: 2006/10/17 20:31:18 $
 */
public class SimpleSearchListener extends AbstractListener {

    private static final ILogger LOG = LoggerFactory.getLogger( AddToShoppingCartListener.class );

    // needs to be public for jsp pages
    public static final String HTML_FRAGMENT = "HTML_FRAGMENT";

    static final String RESULT_SEARCH = "RESULT_SEARCH";

    static final String RPC_CATALOG = "catalog";

    static final String RPC_FORMAT = "RPC_FORMAT"; // ISO19115, ISO19119, ...

    static final String SESSION_AVAILABLESERVICECATALOGS = "AVAILABLESERVICECATALOGS";

    static final String SESSION_DATARECORDS = "DATARECORDS";

    static final String SESSION_REQUESTFORRESULTS = "SESSION_REQUESTFORRESULTS";

    static final String SESSION_RESULTFORHITS = "SESSION_RESULTFORHITS";

    protected CSWClientConfiguration config = null;

    // protected Node nsNode = null;
    protected NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    public void actionPerformed( FormEvent event ) {
        LOG.entering();

        RPCWebEvent rpcEvent = (RPCWebEvent) event;
        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
        config = (CSWClientConfiguration) session.getAttribute( Constants.CSW_CLIENT_CONFIGURATION );
        
//        try {
//            nsNode = XMLTools.getNamespaceNode( config.getNamespaceBindings() );
//        } catch( ParserConfigurationException e ) {
//            e.printStackTrace();
//            gotoErrorPage( "Could not create namespace node for SimpleSearchListener." 
//                           + e.getMessage() );
//            LOG.exiting();
//            return;
//        }
        
        try {
            validateRequest( rpcEvent );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpc request: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        List rpcCatalogs;
        RPCStruct rpcStruct;
        String rpcFormat;
        String rpcProtocol;

        try {
            rpcCatalogs = extractRPCCatalogs( rpcEvent );
            rpcStruct = extractRPCStruct( rpcEvent, 1 );
            rpcFormat = (String) extractRPCMember( rpcStruct, RPC_FORMAT );
            rpcProtocol = (String) extractRPCMember( rpcStruct, Constants.RPC_PROTOCOL );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpc event: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        // for further use in TurnPageListener
        session.setAttribute( Constants.RPC_PROTOCOL, rpcProtocol );

        // first "GetRecords"-request (resultType="HITS", typeNames="dataset")
        String req = null;
        HashMap resultHits = null;
        try {
            req = createRequest( rpcStruct, rpcFormat, "HITS" );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid request for hits: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        try {
            resultHits = performRequest( rpcProtocol, req, rpcCatalogs, "HITS" );
            // save to session for further use in TurnPageListener
            session.setAttribute( SESSION_RESULTFORHITS, resultHits );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Server is not reachable or did not answer with valid XML: \n"
                           + e.getMessage() );
            LOG.exiting();
            return;
        }

        int hits = 0;
        try {
            hits = numberOfMatchesInMap( resultHits );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid Result: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        HashMap resultResults = null;
        List dsrListSearch = null;
        Map availableServiceCatalogsMap = null;
        if ( hits > 0 ) {

            // second "GetRecords"-request (resultType="RESULTS", typeNames="dataset")
            req = null;
            try {
                req = createRequest( rpcStruct, rpcFormat, "RESULTS" );
                // if resultType="RESULT", then save request to session to be able to perform
                // the request again and again for more of the google-like pages (TurnPageListener)
                session.setAttribute( SESSION_REQUESTFORRESULTS, req );
            } catch ( Exception e ) {
                e.printStackTrace();
                gotoErrorPage( "Invalid request for results: \n" + e.getMessage() );
                LOG.exiting();
                return;
            }
            try {
                resultResults = performRequest( rpcProtocol, req, rpcCatalogs, "RESULTS" );
            } catch ( Exception e ) {
                e.printStackTrace();
                gotoErrorPage( "Server is not reachable or did not answer with valid XML: \n"
                               + e.getMessage() );
                LOG.exiting();
                return;
            }

            // create data session records for results and store them in the session
            try {
                dsrListSearch = createDataSessionRecords( resultResults );
            } catch ( CatalogClientException e ) {
                e.printStackTrace();
                gotoErrorPage( "Could not create list of DataSessionRecords." + e.getMessage() );
                LOG.exiting();
                return;
            }

            try {
                // TODO make usable for other formats, not only "ISO19119"
                availableServiceCatalogsMap = doServiceSearch( resultResults, "ISO19119", "HITS",
                                                               null );
            } catch ( CatalogClientException e ) {
                e.printStackTrace();
                gotoErrorPage( "Error creating relevant search result list: \n" + e.getMessage() );
                LOG.exiting();
                return;
            }
        }
        // save List of data session records to session (may be null or empty)
        session.setAttribute( SESSION_DATARECORDS, dsrListSearch );
        // save Map of available service catalogs to session (may be null or empty)
        session.setAttribute( SESSION_AVAILABLESERVICECATALOGS, availableServiceCatalogsMap );

        // handle result: take result and transform it to produce html output
        String fileName = "metaList2html.xsl"; // default value
        // FIXME replace format with current value
        HashMap xslMap = config.getProfileXSL( "Profiles." + rpcFormat );
        if ( xslMap != null ) {
            if ( xslMap.get( "brief" ) != null ) {
                fileName = (String) xslMap.get( "brief" );
            }
        }

        try {
            String pathToFile = "file:" + getHomePath() + "WEB-INF/conf/igeoportal/" + fileName;
            handleResult( resultHits, resultResults, pathToFile );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Error handling result: " + e.getMessage() );
            LOG.exiting();
            return;
        }

        LOG.exiting();
        return;
    }

// /**
// *
// * @param session
// * @param dsrListSearch
//     */
//    private void addDataSessionRecordsToSessionAttrib( HttpSession session, List dsrListSearch ) {
//        LOG.entering();
//        
//        List dsrListSession;
//        if ( session.getAttribute( Constants.SESSION_DATARECORDS ) != null ) {
//            dsrListSession = (ArrayList)session.getAttribute( Constants.SESSION_DATARECORDS );
//            for( int i = 0; i < dsrListSearch.size(); i++ ) {
//                if ( ! dsrListSession.contains( dsrListSearch.get(i) ) ) {
//                    dsrListSession.add( dsrListSearch );
//                }
//            } 
//        } else {
//            dsrListSession = new ArrayList( dsrListSearch.size() );
//            dsrListSession.addAll( dsrListSearch );            
//        }
//        session.setAttribute( Constants.SESSION_DATARECORDS, dsrListSession );
//        
//        LOG.exiting();
//    }   

    /**
     * 
     * @param rpcEvent
     * @throws CatalogClientException
     */
    protected void validateRequest( RPCWebEvent rpcEvent )
                            throws CatalogClientException {
        LOG.entering();

        RPCParameter[] params = extractRPCParameters( rpcEvent );

        // validity check for number of parameters in RPCMethodCall
        if ( params.length != 2 ) {
            throw new CatalogClientException( "Request/Method Call must contain two parameters, not: "
                                              + params.length );
        }

        RPCStruct rpcStruct = extractRPCStruct( rpcEvent, 1 );
        String rpcFormat = (String) extractRPCMember( rpcStruct, RPC_FORMAT );
        String rpcProtocol = (String) extractRPCMember( rpcStruct, Constants.RPC_PROTOCOL );
        if ( rpcFormat == null || rpcProtocol == null ) {
            throw new CatalogClientException( "Format and protocol must be set in the rpcEvent." );
        }

        // go through each catalog of the rpc and validate
        List rpcCatalogs = extractRPCCatalogs( rpcEvent );
        String rpc_catalog = null;
        for ( int i = 0; i < rpcCatalogs.size(); i++ ) {
            rpc_catalog = (String) rpcCatalogs.get( i );

            // validity check for catalog
            String[] catalogs = config.getCatalogNames();
            boolean containsCatalog = false;
            for ( int j = 0; j < catalogs.length; j++ ) {
                if ( catalogs[j].equals( rpc_catalog ) ) {
                    containsCatalog = true;
                }
            }
            if ( !containsCatalog ) {
                throw new CatalogClientException( "The catalog " + rpc_catalog + " is not "
                                                  + "configured for this client." );
            }

            // validity check for format
            // is requested catalog capable to serve requested metadata format?
            List formats = config.getCatalogFormats( rpc_catalog );
            if ( formats == null || !formats.contains( rpcFormat ) ) {
                throw new CatalogClientException( "The catalog " + rpc_catalog + " does not "
                                                  + "provide the requested format " + rpcFormat );
            }

            // validity check for protocol
            // is requested catalog reachable through requested protocol?
            List protocols = config.getCatalogProtocols( rpc_catalog );
            if ( !protocols.contains( rpcProtocol ) ) {
                throw new CatalogClientException( "The catalog " + rpc_catalog + " does not "
                                                  + "provide the requested protocol " + rpcProtocol );
            }
        }

        LOG.exiting();
        return;
    }

    /**
     * This method creates a csw request with the RequestFactory of the passed format, using the
     * paramter values passed in the rpcStruct. (csw:GetRecords request, if the passed resultType is
     * HITS or RESULTS, and csw:GetRecordsById request, if the passed resultType is null).
     * 
     * @param rpcStruct
     *            The struct contains parameter values to be used in the RequestFactory.
     * @param format
     *            The format determines the RequestFactory to be used.
     * @param resultType
     *            The type of desired result. Possible values are HITS or RESULTS or null.
     * @return Returns the xml encoded request as <code>String</code>.
     * @throws CatalogClientException
     */
    protected String createRequest( RPCStruct rpcStruct, String format, String resultType )
                            throws CatalogClientException {
        LOG.entering();

        CSWRequestFactory fac = RequestFactoryFinder.findFactory( format );
        fac.setConfiguration( config );
        String request = fac.createRequest( rpcStruct, resultType );

        LOG.exiting();
        return request;
    }

    /**
     * 
     * @param protocol
     * @param request
     * @param catalogs
     * @param type
     * @return Returns a <code>HashMap</code>, which contains one key-value-pair for each
     *         catalogue, that has been searched. The key is the name of the catalogue. The value is
     *         the doc Document, that contains the number of matches (resultType="HITS"), or 1 to n
     *         metadata entries (resultType="RESULTS")
     * @throws CatalogClientException
     */
    protected HashMap performRequest( String protocol, String request, List catalogs, String type )
                            throws CatalogClientException {
        LOG.entering();

        HashMap<String, Document> result = new HashMap<String, Document>();

        // loop for all catalogues contained in catalogs
        for ( int i = 0; i < catalogs.size(); i++ ) {
            boolean useSOAP = false;
            List list = config.getCatalogProtocols( (String) catalogs.get( i ) );
            if ( protocol != null ) {
                if ( !list.contains( protocol ) ) {
                    throw new CatalogClientException( "The catalog configuration does not support " +
                                                      "the requested protocol.\n" );
                }
                useSOAP = "SOAP".equals( protocol );
            } else {
                for ( int j = 0; j < list.size(); j++ ) {
                    if ( "SOAP".equals( list.get( j ) ) ) {
                        useSOAP = true;
                        break;
                    }
                }
            }
            String cswAddress = config.getCatalogServerAddress( (String) catalogs.get( i ) );
            if ( cswAddress == null ) {
                throw new CatalogClientException( "The catalog configuration does not support the " +
                                                  "requested server address.\n" );
            }

            try {
                if ( useSOAP ) {
                    // TODO test if this SOAP is working properly
                    StringBuffer soapRequest = new StringBuffer( 5000 );
                    soapRequest.append( "<soap:Envelope " )
                               .append( "xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" " )
                               .append( "soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">" )
                               .append( request )
                               .append( "</soap:Envelope>" );

                    request = soapRequest.toString();
                }

                // send post request
                HttpClient httpclient = new HttpClient();
                httpclient.getHttpConnectionManager().getParams().setSoTimeout( 30000 );
                PostMethod postMethod = new PostMethod( cswAddress );
                postMethod.setRequestEntity( new StringRequestEntity( request ) );

                httpclient.executeMethod( postMethod );
                String resp = postMethod.getResponseBodyAsString();
                Document doc = XMLTools.parse( new StringReader( resp ) );

                // write key-value-pair to HashMap
                result.put( (String) catalogs.get( i ), doc );

            } catch ( Exception e ) {
                e.printStackTrace();
                throw new CatalogClientException( e.getMessage() );
            }
        }

        LOG.exiting();
        return result;
    }

    /**
     * 
     * @param resultHits
     * @param resultResults
     * @param pathToXslFile (
     *            e.g. file://$iGeoPortal_home$/WEB-INF/conf/igeoportal/metaList2html.xsl );
     * @throws XMLParsingException
     *             if the documents contained in resultHits don't have the expected structure.
     * @throws CatalogClientException
     */
    protected void handleResult( Object resultHits, Object resultResults, String pathToXslFile )
                            throws XMLParsingException, CatalogClientException {
        LOG.entering();

        Map map = ( resultResults != null ) ? (HashMap) resultResults : (HashMap) resultHits;
        Iterator it = map.keySet().iterator();

        URL u = null;
        StringBuffer htmlFragment = new StringBuffer( 5000 );
        MetadataTransformer mt = null;
        try {
            u = new URL( pathToXslFile );
            mt = new MetadataTransformer( u.getFile() );
        } catch ( Exception e ) {
            throw new CatalogClientException( e.getMessage() );
        }

        String catalog = null;
        Document doc = null;
        String docString = null;
        // one loop for each catalog in result ( where resultType="RESULTS" )
        while ( it.hasNext() ) {
            catalog = (String) it.next();
            doc = (Document) map.get( catalog );
            docString = DOMPrinter.nodeToString( doc, CharsetUtils.getSystemCharset() );
            Reader reader = new StringReader( docString );

            // need to get numberOfRecordsMatched from the document where resultType is "HITS",
            // because only there the number is correct.
            // In document where resultType="RESULTS" the numberOfRecordsMatched is always equal to
            // numberOfRecordReturned, which is LESS OR EQUAL to the correct numberOfRecordsMatched.
            int matches = numberOfMatchesInDoc( (Document) ( (HashMap) resultHits ).get( catalog ) );

            // need to get startPosition from the request, because the value 'nextRec' in
            // GetRecordsResponse is always '0', which is not always correct ;o)
            int startPos = 0;
            // load request from session
            HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
            String req = (String) session.getAttribute( SESSION_REQUESTFORRESULTS );
            if ( req != null ) {
                try {
                    StringReader sr = new StringReader( req );
                    Document docReq = XMLTools.parse( sr );
                    startPos = 
                        Integer.parseInt( docReq.getDocumentElement().getAttribute( "startPosition" ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new CatalogClientException( e.getMessage() );
                }
            }

            // transformation
            try {
                htmlFragment.append( mt.transformMetadata( reader, catalog, null, matches, startPos, 
                                                           "list" ) );
            } catch ( Exception e ) {
                throw new CatalogClientException( e.getMessage() );
            }
        }

        this.getRequest().setAttribute( RESULT_SEARCH, resultResults );
        this.getRequest().setAttribute( HTML_FRAGMENT, htmlFragment.toString() );

        LOG.exiting();
    }

    /**
     * @param results
     * @return Returns a List of distinct DataSessionRecords for all metadata elements within the
     *         passed results.
     * @throws CatalogClientException
     *             if the identifier or the title of a metadata element could not be extracted.
     */
    protected List<DataSessionRecord> createDataSessionRecords( HashMap results )
                            throws CatalogClientException {
        LOG.entering();

        List<DataSessionRecord> dsrList = new ArrayList<DataSessionRecord>();

        Iterator it = results.keySet().iterator();
        while ( it.hasNext() ) {
            String catalog = (String) it.next();
            Document doc = (Document) results.get( catalog );

            List mdList; // list of unique metadata element nodes
            try {
                mdList = extractMetadata( doc );
            } catch ( Exception e ) {
                throw new CatalogClientException( "Cannot extract metadata elements: "
                                                  + e.getMessage() );
            }

            String xPathToId = config.getXPathToDataIdentifier();
            String xPathToTitle = config.getXPathToDataTitle();

            for ( int j = 0; j < mdList.size(); j++ ) {
                Node mdNode = (Node) mdList.get( j );

                String id = null;
                String title = null;
                try {
                    id = extractValue( mdNode, xPathToId );
                    title = extractValue( mdNode, xPathToTitle );
                } catch ( Exception e ) {
                    throw new CatalogClientException( "could not extract title and/or identifier: "
                                                      + e.getMessage() );
                }
                DataSessionRecord dsr = new DataSessionRecord( id, catalog, title );

                if ( !dsrList.contains( dsr ) ) {
                    // this should be a redundant check. testing just in case...
                    dsrList.add( dsr );
                }
            }
        }

        LOG.exiting();
        return dsrList;
    }

    /**
     * Extracts all Metadata nodes from the passed csw:GetRecordsResponse Document.
     * 
     * @param doc
     *            The csw:GetRecordsResponse Document from which to extract the Metadata nodes.
     * @return Returns a NodeList of Metadata Elements for the passed Document.
     * @throws CatalogClientException
     *             if metadata nodes could not be extracted from the passed Document.
     * @throws XMLParsingException
     */
    protected List extractMetadata( Document doc )
                            throws CatalogClientException, XMLParsingException {

        List nl = null;

        String xPathToMetadata = "csw:GetRecordsResponse/csw:SearchResults/child::*";
        // nl = XMLTools.getXPath( xPathToMetadata, doc, nsNode ); // old
        nl = XMLTools.getNodes( doc, xPathToMetadata, nsContext ); // new
        if ( nl == null || nl.size() < 1 ) {
            throw new CatalogClientException( "could not extract metadata nodes." );
        }

        return nl;
    }

    /**
     * @param node
     * @param xPath
     * @return Returns the value for the passed node and xPath.
     * @throws TransformerException
     * @throws CatalogClientException
     * @throws XMLParsingException
     */
    protected String extractValue( Node node, String xPath )
                            throws CatalogClientException, XMLParsingException {
        LOG.entering();

        String s = XMLTools.getNodeAsString( node, xPath, nsContext, null );
        if ( s == null ) {
            throw new CatalogClientException( "no value could be found using xPath: " + xPath );
        }

        LOG.exiting();
        return s;
    }

    /**
     * The number of matches returned is the number of matches for all catalogs added together.
     * 
     * @param result
     *            The HashMap containing the result document from the performed request.
     * @return Returns the number of matches indicated in the result.
     * @throws XMLParsingException,
     *             if the result document in the passed HashMap does not contain the expected nodes
     *             and attributes.
     */
    private int numberOfMatchesInMap( HashMap result )
                            throws XMLParsingException {
        LOG.entering();

        int hits = 0;

        Iterator iterator = result.keySet().iterator();
        while ( iterator.hasNext() ) {
            String catalog = (String) iterator.next();
            Document doc = (Document) result.get( catalog ); // result(value)
            int matches = numberOfMatchesInDoc( doc );
            hits += matches;
        }

        LOG.exiting();
        return hits;
    }

    /**
     * The number of matches returned is the number for one single catalog.
     * 
     * @param doc
     *            The Document containing the result for one catalog.
     * @return Returns the number of matches for one catalog only.
     * @throws XMLParsingException
     */
    private int numberOfMatchesInDoc( Document doc )
                            throws XMLParsingException {
        LOG.entering();

        Element docElement = doc.getDocumentElement(); // root element
        Element searchResults = (Element) XMLTools.getRequiredNode( docElement,
                                                                    "csw:SearchResults", nsContext );
        String matches = XMLTools.getRequiredAttrValue( "numberOfRecordsMatched", null,
                                                        searchResults );

        LOG.exiting();
        return Integer.parseInt( matches );
    }

    /**
     * 
     * @param result
     *            HashMap containing data catalog names (as keys) and GetRecordResponse Documents
     *            (as values).
     * @param format
     *            some service format like "ISO19119"
     * @param resultType
     *            either "HITS" or "RESULTS".
     * @param elementSet
     *            The elementSetName to use ("brief", "full"). Default is "brief".
     * @return Returns a Map that contains the title extracted from the passed document (as key) and
     *         a List of all corresponding available service catalogs (as value).
     * @throws CatalogClientException
     */
    protected Map doServiceSearch( HashMap result, String format, String resultType,
                                  String elementSet )
                            throws CatalogClientException {
        LOG.entering();

        List serviceCatalogs = config.getServiceMetadataCatalogs();
        Map<String, List<String>> availableServiceCatalogsMap = 
            new HashMap<String, List<String>>( 10 );

        // for each kvp (i.e. for each data catalog) in result do the following loop
        Iterator iterator = result.keySet().iterator();
        while ( iterator.hasNext() ) {
            String catalog = (String) iterator.next(); // result(key)
            Document doc = (Document) result.get( catalog ); // result(value)

            List nl; // list of unique metadata element nodes
            try {
                nl = extractMetadata( doc );
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new CatalogClientException( "Cannot extract metadata: \n" + e.getMessage() );
            }
            List<String> titles = new ArrayList<String>( nl.size() );
            String xPathToTitle = config.getXPathToDataTitle();
            ;
            if ( "full".equals( elementSet ) ) {
                xPathToTitle = config.getXPathToDataTitleFull();
            }

            // get list of titles for current catalog
            // REASON: the link between iso19115 and iso19119 is the "title".
            // TODO make this work for other formats where the link is something else completely.
            try {
                for ( int i = 0; i < nl.size(); i++ ) {
                    titles.add( extractValue( (Node) nl.get( i ), xPathToTitle ) );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new CatalogClientException( "Cannot extract titles: \n" + e.getMessage() );
            }

            // for each title
            for ( int i = 0; i < titles.size(); i++ ) {
                // DO SERVICE SEARCH
                // simple search had results (number of hits > 0), so a getRecords request with
                // resulttype=RESULTS was done and now a service search is needed for those results.

                // get the service info: search a service for the current title
                RPCStruct serviceStruct = null;
                String template = "CSWServiceSearchRPCMethodCallTemplate.xml";
                try {
                    serviceStruct = createRpcStructForServiceSearch( template, titles.get( i ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new CatalogClientException( "Invalid RPCStruct: \n" + e.getMessage() );
                }
                String serviceReq = null;
                try {
                    serviceReq = createRequest( serviceStruct, format, resultType );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new CatalogClientException( "Invalid Request: \n" + e.getMessage() );
                }

                HashMap serviceResult = null;
                try {
                    serviceResult = performRequest( null, serviceReq, serviceCatalogs, resultType );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new CatalogClientException( 
                        "The server is not reachable or did not answer with valid XML: \n" 
                        + e.getMessage() );
                }
                List<String> availableServiceCatalogs = null;
                // get service catalogs that are available for the current dataTitle
                try {
                    availableServiceCatalogs = extractAvailableServiceCatalogs( serviceResult );
                } catch ( XMLParsingException e ) {
                    e.printStackTrace();
                    throw new CatalogClientException( "Invalid Result: \n" + e.getMessage() );
                }
                availableServiceCatalogsMap.put( titles.get( i ), availableServiceCatalogs );
            } // end for each dataTitle
        } // end for each kvp in result

        LOG.exiting();
        return availableServiceCatalogsMap;
    }

    /**
     * Extract all catalogs from serviceResult that actually do have the service available (= where
     * number of records matched is greater than 0) and add them to the returned List.
     * 
     * @param serviceResult
     * @return Returns a <code>List</code> of all available service catalogs. May be null.
     * @throws XMLParsingException
     */
    private List<String> extractAvailableServiceCatalogs( Map serviceResult )
                            throws XMLParsingException {
        LOG.entering();

        List<String> availableServiceCatalogs = new ArrayList<String>( serviceResult.size() );

        // one loop for each catalog in serviceResult
        Iterator it = serviceResult.keySet().iterator();
        while ( it.hasNext() ) {
            String servCatalog = (String) it.next();
            Document servDoc = (Document) serviceResult.get( servCatalog );

            int matches = numberOfMatchesInDoc( servDoc );
            if ( matches > 0 ) {
                availableServiceCatalogs.add( servCatalog );
            }
        }

        LOG.exiting();
        return ( availableServiceCatalogs.size() == 0 ) ? null : availableServiceCatalogs;
    }

    /**
     * @param template
     * @param title
     * @return Returns the new rpcStruct.
     * @throws CatalogClientException
     * @throws RPCException
     */
    protected RPCStruct createRpcStructForServiceSearch( String template, String title )
                            throws CatalogClientException, RPCException {
        LOG.entering();

        RPCStruct rpcStruct = null;
        InputStream is = SimpleSearchListener.class.getResourceAsStream( template );

        String rpc = null;
        try {
            InputStreamReader ireader = new InputStreamReader( is );
            BufferedReader br = new BufferedReader( ireader );
            StringBuffer sb = new StringBuffer( 50000 );
            while ( ( rpc = br.readLine() ) != null ) {
                sb.append( rpc );
            }
            rpc = sb.toString();
            br.close();
        } catch ( Exception e ) {
            throw new CatalogClientException( "Could not read template or transform it to String: "
                                              + e.getMessage() );
        }

        // replace templates in struct with passed values
        rpc = rpc.replaceAll( "\\$TITLE", title );

        StringReader reader = new StringReader( rpc );
        RPCMethodCall mc = RPCFactory.createRPCMethodCall( reader );
        try {
            RPCParameter[] params = mc.getParameters();
            rpcStruct = (RPCStruct) params[0].getValue();
        } catch ( Exception e ) {
            throw new CatalogClientException( "Cannot extract struct from RPCMethodCall: \n"
                                              + e.getMessage() );
        }

        LOG.exiting();
        return rpcStruct;
    }

    /**
     * Extracts the parameters from the method call element within the passed rpcEvent.
     * 
     * @param rpcEvent
     * @return Returns the parameters as array of <code>RPCParameter</code>.
     * @throws CatalogClientException
     */
    protected RPCParameter[] extractRPCParameters( RPCWebEvent rpcEvent )
                            throws CatalogClientException {

        RPCParameter[] params;
        try {
            RPCMethodCall mc = rpcEvent.getRPCMethodCall();
            params = mc.getParameters();
        } catch ( Exception e ) {
            throw new CatalogClientException( "Cannot extract parameters from RPCWebEvent: "
                                              + e.getMessage() );
        }
        return params;
    }

    /**
     * Extracts the catalog names from the first parameter of the params element within the passed
     * rpcEvent.
     * 
     * @param rpcEvent
     * @return Returns the catalogue names as array of <code>String</code>.
     * @throws CatalogClientException
     */
    protected List extractRPCCatalogs( RPCWebEvent rpcEvent )
                            throws CatalogClientException {

        List<String> catalogs = new ArrayList<String>( 10 );
        try {
            RPCParameter[] params = extractRPCParameters( rpcEvent );
            RPCParameter[] rpcCatalogs = (RPCParameter[]) params[0].getValue();
            for ( int i = 0; i < rpcCatalogs.length; i++ ) {
                catalogs.add( (String) rpcCatalogs[i].getValue() );
            }
        } catch ( Exception e ) {
            throw new CatalogClientException( "Cannot extract catalog names from RPCWebEvent: "
                                              + e.getMessage() );
        }
        return catalogs;
    }

    /**
     * Extracts the <code>RPCStruct</code> from the indicated parameter in the params element of
     * the passed <code>RPCWebEvent</code>.
     * 
     * @param rpcEvent
     *            The RPCWebEvent, that contains the RPCStruct to extract.
     * @param index
     *            The index of the parameter from which to extract the RPCStruct (starting with 0).
     * @return Returns the <code>RPCStruct</code> from the indicated params element.
     * @throws CatalogClientException
     */
    protected RPCStruct extractRPCStruct( RPCWebEvent rpcEvent, int index )
                            throws CatalogClientException {
        RPCStruct rpcStruct;
        try {
            RPCParameter[] params = extractRPCParameters( rpcEvent );
            rpcStruct = (RPCStruct) params[index].getValue();
        } catch ( Exception e ) {
            throw new CatalogClientException( "Cannot extract struct from RPCWebEvent: "
                                              + e.getMessage() );
        }
        return rpcStruct;
    }

    /**
     * Extracts the member of the passed name from the passed struct.
     * 
     * @param struct
     *            The rpcStruct to extract the passed member from.
     * @param member
     *            The Member to extract from the passed rpcStruct.
     * @return Returns the member value object.
     * @throws CatalogClientException
     */
    protected Object extractRPCMember( RPCStruct struct, String member )
                            throws CatalogClientException {
        RPCMember rpcMember;
        try {
            rpcMember = struct.getMember( member );
        } catch ( Exception e ) {
            throw new CatalogClientException( "Cannot extract member " + member
                                              + " from RPCStruct: " + e.getMessage() );
        }
        return rpcMember.getValue();
    }
    
//    /**
//     * Extracts the member of the passed name from the passed struct.
//     * 
//     * @param struct The rpcStruct to extract the passed member from.
//     * @param memberName The Member to extract from the passed rpcStruct.
//     * @return Returns the member value object.
//     * @throws CatalogClientException 
//     */
//    protected Object extractRPCMemberValue( RPCStruct struct, String memberName ) 
//                                       throws CatalogClientException {
//        String memberValue = null; 
//        try {
//            memberValue = (String) struct.getMember( memberName ).getValue();
//        } catch (Exception e) {
//            throw new CatalogClientException( "Cannot extract member value "+ memberName +" from RPCStruct: " 
//                                              + e.getMessage() );
//        }
//        return memberValue;
//    }
      
      
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: SimpleSearchListener.java,v $
 Revision 1.12  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.11  2006/10/12 13:09:50  mays
 bugfix: avoid NullPointerException

 Revision 1.10  2006/08/02 14:10:26  poth
 *** empty log message ***
 
 Revision 1.9  2006/07/31 11:02:44  mays
 move constants from class Constants to the classes where they are needed
 
 Revision 1.8  2006/07/31 09:33:58  mays
 move Constants to package control, update imports
 
 Revision 1.7  2006/06/30 08:43:19  mays
 clean up code and java doc
 
 Revision 1.6  2006/06/23 13:38:25  mays
 add/update csw control files
 
 Revision 1.15  2006/05/31 17:05:50  mays
 add elementSet to parameter list of doServiceSearch()
 
 Revision 1.14  2006/05/15 13:28:05  mays
 get xsl file name for transformation from configuration file
 
 Revision 1.13  2006/04/20 15:39:21  mays
 made path to xsl file relativ;
 some cleanup
 
 Revision 1.12  2006/04/11 09:41:54  mays
 changing return type of extractRPCMember from String to Object for wider usability
 
 Revision 1.11  2006/04/11 08:20:07  mays
 adapting header
 
 Revision 1.10  2006/04/10 15:20:51  mays
 make sure that SESSION_DATARECORDS are set under all circumstances
 
 Revision 1.9  2006/04/07 07:29:54  mays
 major restructuring
 
 Revision 1.8  2006/03/28 15:45:14  mays
 clean up
 
 Revision 1.7  2006/03/27 18:40:25  mays
 WIP. major changes
 
 Revision 1.6  2006/03/16 18:10:23  mays
 add new methods, add params to method calls, clean up, and more
 
 Revision 1.5  2006/03/14 18:37:11  mays
 major restructuring according to ap
 
 Revision 1.4  2006/03/10 16:11:47  mays
 restructuring of service search
 
 Revision 1.3  2006/03/09 17:12:59  mays
 WIP: add handling of service search (not finished yet)
 
 Revision 1.2  2006/03/03 17:17:54  mays
 change return type of createRequest from HashMap to String.
 change parameter list of performeRequest because of this.
 
 Revision 1.1  2006/03/02 12:35:37  poth
 *** empty log message ***
 
 Revision 1.11  2006/02/14 17:26:18  mays
 structural changes
 
 Revision 1.10  2006/02/10 16:50:21  mays
 minor changes
 
 Revision 1.9  2006/02/09 16:43:15  mays
 only two prameters are passed to csw
 
 Revision 1.8  2006/02/09 10:58:36  mays
 minor changes, check new param from cws config file
 
 Revision 1.7  2006/02/08 14:27:31  mays
 Fixed CVS keyword expansion mode.
 
 Revision 1.6  2006/02/08 12:41:01  mays
 make use of csw-classes instead of wcasclient-classes
 make use of RequestFactoryFinder
 
 Revision 1.5  2006/02/03 15:20:45  mays
 minor cleanup. more to do
 
 Revision 1.4  2006/01/31 17:28:38  mays
 work in progress
 
 Revision 1.3  2006/01/27 15:29:10  mays
 first implementation of various methods
 
 Revision 1.2  2006/01/18 16:47:43  mays
 WIP - change fake search result
 
 Revision 1.1  2006/01/13 15:18:00  mays
 first implementation of new empty  classes for csw metadata
 
 ********************************************************************** */
