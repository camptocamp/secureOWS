//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/TurnPageListener.java,v 1.8 2006/10/17 20:31:18 poth Exp $
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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;
import org.deegree.portal.standard.csw.model.DataSessionRecord;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A <code>${type_name}</code> class.<br/> TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/10/17 20:31:18 $
 */
public class TurnPageListener extends SimpleSearchListener {

    private static final ILogger LOG = LoggerFactory.getLogger( TurnPageListener.class );

    public void actionPerformed( FormEvent event ) {
        LOG.entering();

        RPCWebEvent rpcEvent = (RPCWebEvent) event;
        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
        config = (CSWClientConfiguration) session.getAttribute( Constants.CSW_CLIENT_CONFIGURATION );

        nsContext = CommonNamespaces.getNamespaceContext();
        // TODO remove if not needed
//         try {
//            nsNode = XMLTools.getNamespaceNode( config.getNamespaceBindings() );
//        } catch ( ParserConfigurationException e ) {
//            e.printStackTrace();
//            gotoErrorPage( "Could not create namespace node for TurnPageListener." + e.getMessage() );
//            LOG.exiting();
//            return;
//        }

        try {
            validateRequest( rpcEvent );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid Request: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        RPCStruct struct = null;
        try {
            struct = extractRPCStruct( rpcEvent, 0 );
        } catch ( CatalogClientException e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpcEvent: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        // load request from session
        String req = (String) session.getAttribute( SESSION_REQUESTFORRESULTS );

        // exchange digits
        try {
            req = replaceIndexAttribs( req, struct );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid Request: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        // load request into session again for further use
        session.setAttribute( SESSION_REQUESTFORRESULTS, req );

        List<String> catalogs = new ArrayList<String>( 1 );
        catalogs.add( (String) struct.getMember( RPC_CATALOG ).getValue() );
        HashMap result = null;

        String protocol = (String) session.getAttribute( Constants.RPC_PROTOCOL );

        // performRequest of SimpleSearchListener
        try {
            result = performRequest( protocol, req, catalogs, "TPL" );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Server is not reachable or did not answer with valid XML: \n"
                           + e.getMessage() );
            LOG.exiting();
            return;
        }

        // create data session records for results and add them to the List in the session.
        List<DataSessionRecord> dsrListSession = 
            (ArrayList<DataSessionRecord>) session.getAttribute( SESSION_DATARECORDS );
        List<DataSessionRecord> dsrList = null;
        try {
            dsrList = createDataSessionRecords( result );
        } catch ( CatalogClientException e ) {
            e.printStackTrace();
            gotoErrorPage( "Could not create list of DataSessionRecords." + e.getMessage() );
            LOG.exiting();
            return;
        }
        for ( int i = 0; i < dsrList.size(); i++ ) {
            if ( !dsrListSession.contains( dsrList.get( i ) ) ) {
                dsrListSession.add( dsrList.get( i ) );
            }
        }
        session.setAttribute( SESSION_DATARECORDS, dsrListSession );

        Map availableServiceCatalogsMap = null;
        try { // TODO make usable for other formats, not only "ISO19119"
            availableServiceCatalogsMap = doServiceSearch( result, "ISO19119", "HITS", null );
        } catch ( CatalogClientException e ) {
            e.printStackTrace();
            gotoErrorPage( "Error creating relevant search result list: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }
        session.setAttribute( SESSION_AVAILABLESERVICECATALOGS, availableServiceCatalogsMap );

        // handleResult von SimpleSearchListener
        HashMap resultHits = (HashMap) session.getAttribute( SESSION_RESULTFORHITS );
        try {
            String pathToFile = "file:" + getHomePath()
                                + "WEB-INF/conf/igeoportal/metaList2html.xsl";
            handleResult( resultHits, result, pathToFile );

        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Error handling result: " + e.getMessage() );
            LOG.exiting();
            return;
        }

        LOG.exiting();
    }

    protected void validateRequest( RPCWebEvent rpcEvent )
                            throws CatalogClientException {
        LOG.entering();

        RPCParameter[] params = extractRPCParameters( rpcEvent );

        // validity check for number of parameters in RPCMethodCall
        if ( params.length != 1 ) {
            throw new CatalogClientException( "Request/Method Call must contain only one parameter." );
        }

        RPCStruct struct = extractRPCStruct( rpcEvent, 0 );
        String catalog = (String) extractRPCMember( struct, RPC_CATALOG );
        String format = (String) extractRPCMember( struct, RPC_FORMAT );
        Integer matches = (Integer) extractRPCMember( struct, "matches" );
        Integer recReturned = (Integer) extractRPCMember( struct, "recReturned" );
        String direction = (String) extractRPCMember( struct, "direction" );
        if ( catalog == null || format == null || matches == null || recReturned == null
             || direction == null ) {
            throw new CatalogClientException( "RPC_CATALOG, RPC_FORMAT, matches, recReturned and " +
                                              "direction must be set in the rpcEvent." );
        }

        // validity check for format
        // is requested catalog capable to serve requested metadata format?
        List formats = config.getCatalogFormats( catalog );
        if ( !formats.contains( format ) ) {
            throw new CatalogClientException( "The catalog " + catalog + 
                                              " does not provide the requested format " + format );
        }

        LOG.exiting();
        return;
    }

    /**
     * @param request
     * @param struct
     * @return Returns the request after replacing the index.
     * @throws IOException
     * @throws SAXException
     * @throws CatalogClientException
     */
    private String replaceIndexAttribs( String request, RPCStruct struct )
                            throws IOException, SAXException, CatalogClientException {

        // read string as xml document
        StringReader sr = new StringReader( request );
        Document doc = XMLTools.parse( sr );
        // 
        int oldStartPos = Integer.parseInt( doc.getDocumentElement().getAttribute( "startPosition" ) );
        int maxRec = Integer.parseInt( doc.getDocumentElement().getAttribute( "maxRecords" ) );

        int matches;
        int recRet;
        String direction;

        try {
            matches = ( (Integer) struct.getMember( "matches" ).getValue() ).intValue();
            recRet = ( (Integer) struct.getMember( "recReturned" ).getValue() ).intValue();
            direction = (String) struct.getMember( "direction" ).getValue();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new CatalogClientException( "Invalid struct: \n" + e.getMessage() );
        }

        // validity check
        if ( recRet > maxRec ) {
            throw new CatalogClientException( "Error while creating request for new page.\n" +
                                              "recordsReturned should be less or equal to maxRecords." );
        }

        int newStartPos = 1;
        if ( "previous".equals( direction ) ) {
            newStartPos = oldStartPos - maxRec < 1 ? 1 : oldStartPos - maxRec;
        } else if ( "next".equals( direction ) ) {
            newStartPos = oldStartPos + recRet;
        } else {
            throw new CatalogClientException( "Error while creating request for new page.\n"
                                              + "direction must be either 'previous' or 'next'." );
        }

        // validity check: 1 <= startPosition <= matches
        if ( newStartPos > matches || newStartPos < 1 ) {
            throw new CatalogClientException( "Error while creating request for new page.\n"
                                              + "new start position " + newStartPos + " is not valid." );
        }

        // replace value of startPosition
        doc.getDocumentElement().setAttribute( "startPosition", String.valueOf( newStartPos ) );

        // return doc as string
        return DOMPrinter.nodeToString( doc, CharsetUtils.getSystemCharset() );
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TurnPageListener.java,v $
Revision 1.8  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.7  2006/10/13 12:16:47  mays
bugfix: change calculation of new startPosition with default 1.

Revision 1.6  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.5  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.4  2006/06/30 08:43:19  mays
clean up code and java doc

Revision 1.3  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
