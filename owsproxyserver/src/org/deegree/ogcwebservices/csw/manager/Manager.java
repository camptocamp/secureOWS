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
package org.deegree.ogcwebservices.csw.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.io.DBPoolException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.EchoRequest;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfiguration;
import org.deegree.ogcwebservices.csw.configuration.CatalogueDeegreeParams;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.ResourceType;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionResponse;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version 2.0, $Revision: 1.37 $, $Date: 2006/11/28 16:32:09 $
 */

public class Manager {

    private static final ILogger LOG = LoggerFactory.getLogger( Manager.class );

    private static Map<ResourceType, AbstractHarvester> harvester = null;

    private static XSLTDocument IN_XSL = null;

    private static XSLTDocument OUT_XSL = null;

    private WFService wfsService;

    /**
     * initializes a Manager instance
     * 
     * @param wfsService
     * @param cswConfiguration
     */
    public Manager( WFService wfsService, CatalogueConfiguration cswConfiguration )
                            throws MissingParameterValueException {
        this.wfsService = wfsService;

        try {
            CatalogueDeegreeParams cdp = cswConfiguration.getDeegreeParams();
            URL url = cdp.getTransformationInputXSLT().getLinkage().getHref();
            IN_XSL = new XSLTDocument();
            IN_XSL.load( url );
            url = cdp.getTransformationOutputXSLT().getLinkage().getHref();
            OUT_XSL = new XSLTDocument();
            OUT_XSL.load( url );
        } catch ( Exception e ) {
            e.printStackTrace();
            String s = "If a CS-W is defined to handle Transaction and/or Harvest requests "
                       + "XSLT scripts for request transformations must be defined in "
                       + "deegreeParams section of the capabilities document.";
            LOG.logError( s, e );
            throw new MissingParameterValueException( getClass().getName(), s );
        }

        WFSCapabilities capa = wfsService.getCapabilities();

        initHarvester();

        LOG.logInfo( "CSW Manager initialized with WFS resource "
                     + capa.getServiceIdentification().getTitle() );
    }

    /**
     * initializes a static Map containing a harvester for other
     * coatalogues, for services and for single CSW-profile documents.
     */
    public static void initHarvester() {
        if ( harvester == null ) {
            harvester = new HashMap();
            harvester.put( ResourceType.catalogue, CatalogueHarvester.getInstance() );
            harvester.put( ResourceType.service, ServiceHarvester.getInstance() );
            harvester.put( ResourceType.csw_profile, CSWProfileHarvester.getInstance() );
        }
    }

    /**
     * starts all known/registered harvester. This method can be used
     * to start harvesting using requests e.g. if a server has 
     * been shutdown and restarted. 
     */
    public static void startAllHarvester() {
        initHarvester();
        Collection con = harvester.values();
        for ( Iterator iter = con.iterator(); iter.hasNext(); ) {
            AbstractHarvester h = (AbstractHarvester) iter.next();
            if ( !h.isRunning() ) {
                h.startHarvesting();
            }
        }
    }

    /**
     * stpos all known/registered harvester. This method can be used
     * to stop harvesting using requests e.g. if a server has shall be
     * shut down.
     *
     */
    public static void stopAllHarvester() {
        if ( harvester != null ) {
            Collection con = harvester.values();
            for ( Iterator iter = con.iterator(); iter.hasNext(); ) {
                AbstractHarvester h = (AbstractHarvester) iter.next();
                if ( h.isRunning() ) {
                    h.stopHarvesting();
                }
            }
        }
    }

    /**
     * @param request
     * @throws InvalidParameterValueException
     * @throws SQLException
     * @throws DBPoolException
     * @throws IOException
     */
    public Object harvestRecords( Harvest request )
                            throws OGCWebServiceException {

        try {
            HarvesterFactory hf = new HarvesterFactory( harvester );
            AbstractHarvester h = hf.findHarvester( request );
            h.addRequest( request );
            if ( !h.isRunning() ) {
                h.startHarvesting();
            }
            if ( request.getHarvestInterval() == null ) {
                //h.removeRequest( request );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( "could not perform harvest operation", e );
            throw new OGCWebServiceException( getClass().getName(),
                                              "could not perform harvest operation"
                                                                      + e.getMessage() );
        }

        return new EchoRequest( request.getId(), null );
    }

    /**
     * performs a transaction request by transforming and forwarding it to the WFS used as backend
     * 
     * @param request
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     */
    public TransactionResult transaction( Transaction request )
                            throws OGCWebServiceException {

        XMLFragment wfsTransactionDocument = null;
        try {
            XMLFragment transactionDocument = new XMLFragment(
                                                               XMLFactory.export( request ).getRootElement() );
            StringWriter sww = new StringWriter( 5000 );
            transactionDocument.write( sww );
            transactionDocument.load( new StringReader( sww.getBuffer().toString() ),
                                      XMLFragment.DEFAULT_URL );
            wfsTransactionDocument = IN_XSL.transform( transactionDocument );
        } catch ( Exception e ) {
            String msg = "Can't transform CSW Transaction request to WFS Transaction request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }
       
        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            StringWriter sw = new StringWriter( 5000 );
            try {
                wfsTransactionDocument.prettyPrint( sw );
            } catch ( Exception e ) {
                e.printStackTrace();
                wfsTransactionDocument.write( sw );
            }
            try {
                FileUtils.writeToFile( "debug_transaction.xml", sw.getBuffer().toString() );
                LOG.logDebug( "file: debug_transaction.xml has been written" );
            } catch ( IOException e ) {
            }
        }

        org.deegree.ogcwebservices.wfs.operation.transaction.Transaction wfstrans = null;
        try {
            wfstrans = org.deegree.ogcwebservices.wfs.operation.transaction.Transaction.create(
                                                                                                request.getId(),
                                                                                                wfsTransactionDocument.getRootElement() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String msg = "Cannot generate object representation for GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        Object wfsResponse = null;
        try {
            wfsResponse = wfsService.doService( wfstrans );
        } catch ( Exception e ) {
            String msg = "Generated WFS Transaction request failed: " + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        if ( !( wfsResponse instanceof org.deegree.ogcwebservices.wfs.operation.transaction.TransactionResponse ) ) {
            String msg = "Unexpected result type '" + wfsResponse.getClass().getName()
                         + "' from WFS (must be TransactionResponse)."
                         + " Maybe a FeatureType is not correctly registered!?";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }

        TransactionResponse transResp = (TransactionResponse) wfsResponse;
        XMLFragment wfsTransRespDoc = null;
        try {
            wfsTransRespDoc = org.deegree.ogcwebservices.wfs.XMLFactory.export( transResp );
        } catch ( IOException e ) {
            String msg = "export of WFS Transaction response as XML failed: " + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        // --------------------------------------------------------------
        // the following section will replace the feature ids returned by
        // the WFS for Insert requests by the ID of the inserted metadata sets
        List<String> ids = new ArrayList<String>();
        List<Operation> ops = request.getOperations();
        for ( int i = 0; i < ops.size(); i++ ) {
            if ( ops.get( i ) instanceof Insert ) {
                try {
                    ids = extractIdentifiers( ids, (Insert) ops.get( i ) );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new OGCWebServiceException( getClass().getName(), e.getMessage() );
                }
            }
        }
        try {
            if ( ids.size() > 0 ) {
                wfsTransRespDoc = replaceIds( wfsTransRespDoc, ids );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( getClass().getName(), e.getMessage() );
        }
        //---------------------------------------------------------------

        TransactionResultDocument cswTransactionDocument = null;
        try {
            XMLFragment tmp = OUT_XSL.transform( wfsTransRespDoc );
            cswTransactionDocument = new TransactionResultDocument();
            cswTransactionDocument.setRootElement( tmp.getRootElement() );
        } catch ( TransformerException e ) {
            String msg = "Can't transform GetRecord request to WFS GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }
        TransactionResult result = null;
        try {
            result = cswTransactionDocument.parseTransactionResponse( request );
        } catch ( XMLParsingException e ) {
            throw new OGCWebServiceException( "could not create TransactionResponse" );
        }
        return result;

    }

    /**
     * replaces the id values of WFS Insert result with corresponding 
     * metadata identifieres
     * 
     * @param wfsTransRespDoc
     * @param ids
     * @return
     * @throws URISyntaxException
     * @throws XMLParsingException
     */
    private XMLFragment replaceIds( XMLFragment wfsTransRespDoc, List<String> ids )
                            throws XMLParsingException {

        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        List nodes = XMLTools.getRequiredNodes( wfsTransRespDoc.getRootElement(),
                                                "./wfs:InsertResults/wfs:Feature/ogc:FeatureId",
                                                nsc );
        for ( int i = 0; i < nodes.size(); i++ ) {
            Element elem = (Element) nodes.get( i );
            elem.setAttribute( "fid", ids.get( i ) );
        }

        return wfsTransRespDoc;
    }

    /**
     * extracts all identifiers of the records to be inserted in correct order
     * @param ids
     * @param insert
     * @return
     * @throws URISyntaxException
     * @throws XMLParsingException
     */
    private List<String> extractIdentifiers( List<String> ids, Insert insert )
                            throws URISyntaxException, XMLParsingException {
        List<Element> records = insert.getRecords();

        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        nsc.addNamespace( "smXML", new URI( "http://metadata.dgiwg.org/smXML" ) );
        nsc.addNamespace( "iso19119", new URI( "http://schemas.opengis.net/iso19119" ) );
        nsc.addNamespace( "iso19115", new URI( "http://schemas.opengis.net/iso19115full" ) );
        nsc.addNamespace( "dc", new URI( "http://www.purl.org/dc/elements/1.1/" ) );        
//        nsc.addNamespace( "dc", new URI( "http://org/dc/elements/1.1/" ) );
        for ( int i = 0; i < records.size(); i++ ) {           
            String xpath = getIdentifierXPath( records.get( i ) );
            String fileIdentifier = XMLTools.getRequiredNodeAsString( records.get( i ), xpath, nsc );
            ids.add( fileIdentifier );
        }
        return ids;
    }

    /**
     * returns the XPath the metadata records identifier
     * 
     * @param metaData
     * @return
     */
    private String getIdentifierXPath( Element metaData ) {
       
        // default is iso 19115
        String xpath = "iso19115:fileIdentifier/smXML:CharacterString";
        if ( metaData != null ) {
            String nspace = metaData.getNamespaceURI();
            nspace = StringTools.replace( nspace, "http://", "", true );
            xpath = Messages.getString( "Identifier_" + nspace );
        }
        return xpath;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: Manager.java,v $
 * Revision 1.37  2006/11/28 16:32:09  mschneider
 * Fixed dc identifier spelling.
 *
 * Revision 1.36  2006/11/23 18:42:02  poth
 * bug fix - setting correct IDs of inserted records in Transaction response
 *
 * Revision 1.35  2006/10/17 20:31:20  poth
 * *** empty log message ***
 *
 * Revision 1.34  2006/10/13 14:22:08  poth
 * changes required because of extending TransactionResult from DefaultOGCWebServiceResponse
 *
 * Revision 1.33  2006/07/10 15:01:36  poth
 * footer corrected
 *
 * Revision 1.32  2006/06/28 08:52:51  poth
 * bug fixes according catalog harvesting
 * Changes to this class. What the people have been up to:
 * Revision 1.31  2006/06/26 20:32:54  poth
 * bug fixes on harvester functionallity
 * Changes to this class. What the people have been up to:
 * Revision 1.30  2006/06/26 12:25:26  poth
 * public static method for stopping all running harvester added
 * Changes to this class. What the people have been up to:
 * Revision 1.29  2006/06/21 17:11:39  mschneider
 * Fixed error message.
 * Changes to this class. What the people have been up to:
 * Revision 1.28  2006/06/20 15:43:29  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.27  2006/06/20 13:38:37  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.26  2006/05/16 16:20:44  mschneider
 * Refactored due to the splitting of org.deegree.ogcwebservices.wfs.operation package.
 * Changes to this class. What the people have been up to:
 * Revision 1.25  2006/05/12 15:26:05  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.24  2006/05/09 15:51:37  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.23  2006/04/06 20:25:27  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.22  2006/04/04 20:39:43  poth
 * *** empty log message ***
 * Changes to this
 * class. What the people have been up to: Revision 1.21 2006/04/04 10:22:02 poth Changes to this
 * class. What the people have been up to: *** empty log message *** Changes to this class. What the
 * people have been up to: Revision 1.20
 * 2006/03/30 21:20:26 poth *** empty log
 * message *** Changes to this class. What
 * the people have been up to: Revision 1.19 2006/03/28 09:06:41 poth Changes to this class. What
 * the people have been up to: *** empty log message *** Changes to this class. What the people have
 * been up to: Revision 1.18 2006/03/27
 * 20:16:03 poth *** empty log message ***
 * Revision 1.17 2006/03/25 15:58:36 poth **
 * empty log message ***
 * 
 * Revision 1.16 2006/03/24 13:53:22 poth ** empty log message ***
 * 
 * Revision 1.15 2006/03/22 13:22:14 poth ** empty log message ***
 * 
 * Revision 1.14 2006/02/26 20:35:25 poth ** empty log message ***
 * 
 * Revision 1.13 2006/02/23 17:35:12 poth ** empty log message ***
 * 
 * Revision 1.12 2006/02/21 19:47:49 poth ** empty log message ***
 * 
 * Revision 1.11 2006/02/20 14:15:25 poth ** empty log message ***
 * 
 * Revision 1.10 2006/02/20 14:14:00 poth ** empty log message ***
 * 
 * Revision 1.9 2006/02/20 12:40:02 poth ** empty log message ***
 * 
 * 
 * 
 **************************************************************************************************/