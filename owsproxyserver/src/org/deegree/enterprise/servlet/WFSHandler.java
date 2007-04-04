//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/servlet/WFSHandler.java,v 1.49 2006/10/20 15:35:41 poth Exp $
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

package org.deegree.enterprise.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.ServiceException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.WFServiceFactory;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.capabilities.FormatType;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.deegree.ogcwebservices.wfs.capabilities.WFSOperationsMetadata;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.operation.AbstractWFSRequest;
import org.deegree.ogcwebservices.wfs.operation.DescribeFeatureType;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.FeatureTypeDescription;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;
import org.deegree.ogcwebservices.wfs.operation.transaction.Delete;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionOperation;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionResponse;
import org.deegree.ogcwebservices.wfs.operation.transaction.Update;

/**
 * Web servlet client for WFS.
 * <p>
 * NOTE: Currently, the <code>WFSHandler</code> is responsible for the pre- and postprocessing of
 * virtual feature types. For virtual feature types, requests and responses are transformed using an
 * XSL-script. Virtual feature types can also provide their own schema document that is sent as a
 * response to {@link DescribeFeatureType} requests.
 * <p>
 * The heuristics that determines whether pre- or postprocessing is necessary, is not very
 * accurate; check the methods:
 * <ul>
 * <li><code>#determineFormat(DescribeFeatureType, WFSConfiguration)</code></li>
 * <li><code>#determineFormat(GetFeature, WFSConfiguration)</code></li>
 * <li><code>#determineFormat(Transaction, WFSConfiguration)</code></li>
 * </ul>
 * <p>
 * The code for the handling of virtual features should probably be moved to the {@link WFService}
 * class.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.49 $, $Date: 2006/10/20 15:35:41 $
 */
class WFSHandler extends AbstractOWServiceHandler {

    private static ILogger LOG = LoggerFactory.getLogger( WFSHandler.class );

    /**
     * Performs the given {@link OGCWebServiceRequest} on the {@link WFService} and sends the
     * response to the given {@link HttpServletResponse} object.
     *
     * @param request
     *            OGCWebServiceRequest to be performed
     * @param httpResponse
     *            servlet response object to write to
     * @throws ServiceException
     */
    public void perform( OGCWebServiceRequest request, HttpServletResponse httpResponse )
                            throws ServiceException {

        LOG.logDebug( "Performing request: " + request.toString() );

        try {
            WFService service = WFServiceFactory.createInstance();
            if ( request instanceof WFSGetCapabilities ) {
                performGetCapabilities( service, (WFSGetCapabilities) request, httpResponse );
            } else if ( request instanceof DescribeFeatureType ) {
                performDescribeFeatureType( service, (DescribeFeatureType) request, httpResponse );
            } else if ( request instanceof GetFeature ) {
                performGetFeature( service, (GetFeature) request, httpResponse );
            } else if ( request instanceof Transaction ) {
                performTransaction( service, (Transaction) request, httpResponse );
            } else {
                assert false : "Unhandled WFS request type: '" + request.getClass().getName() + "'";
            }
        } catch ( OGCWebServiceException e ) {
            LOG.logInfo( "Error while performing WFS request.", e );
            sendException( httpResponse, e );
        } catch ( Exception e ) {
            LOG.logError( "Fatal error while performing WFS request.", e );
            sendException( httpResponse, new OGCWebServiceException( getClass().getName(),
                                                                     e.getMessage() ) );
        }
    }

    /**
     * Performs a {@link WFSGetCapabilities} request and sends the response to the given
     * {@link HttpServletResponse} object.
     *
     * @param service
     *            WFService instance to be used
     * @param request
     *            GetCapabilities request to be performed
     * @param httpResponse
     *            servlet response object to write to
     * @throws OGCWebServiceException
     */
    private void performGetCapabilities( WFService service, WFSGetCapabilities request,
                                         HttpServletResponse httpResponse )
                            throws OGCWebServiceException {

        WFSCapabilities capa = (WFSCapabilities) service.doService( request );

        try {
            httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            WFSCapabilitiesDocument document = XMLFactory.export( capa, request.getSections() );
            document.write( httpResponse.getOutputStream() );
        } catch ( IOException e ) {
            LOG.logError( "Error sending GetCapabilities response to client.", e );
        }
    }

    /**
     * Performs a {@link DescribeFeatureType} request and sends the response to the given
     * {@link HttpServletResponse} object.
     *
     * @param service
     *            WFService instance to be used
     * @param request
     *            DescribeFeatureType request to be performed
     * @param httpResponse
     *            servlet response object to write to
     * @throws OGCWebServiceException
     */
    private void performDescribeFeatureType( WFService service, DescribeFeatureType request,
                                             HttpServletResponse httpResponse )
                            throws OGCWebServiceException {

        WFSConfiguration config = (WFSConfiguration) service.getCapabilities();
        FormatType format = determineFormat( request, config );

        XMLFragment schemaDoc = null;

        if ( format.getSchemaLocation() != null ) {
            // read special schema for virtual format
            try {
                schemaDoc = new XMLFragment( format.getSchemaLocation().toURL() );
            } catch ( Exception e ) {
                String msg = Messages.getMessage( "WFS_VIRTUAL_FORMAT_SCHEMA_READ_ERROR",
                                                  format.getSchemaLocation(), format.getValue(), e );
                LOG.logError( msg, e );
                throw new OGCWebServiceException( getClass().getName(), msg );
            }
        } else {
            // get schema from WFService
            FeatureTypeDescription ftDescription = (FeatureTypeDescription) service.doService( request );
            schemaDoc = ftDescription.getFeatureTypeSchema();
        }

        httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
        try {
            schemaDoc.write( httpResponse.getOutputStream() );
        } catch ( IOException e ) {
            LOG.logError( "Error sending DescribeFeatureType response to client.", e );
        }
    }

    /**
     * Performs a {@link GetFeature} request and sends the response to the given
     * {@link HttpServletResponse} object.
     *
     * @param service
     *            WFService instance to be used
     * @param request
     *            GetFeature request to be performed
     * @param httpResponse
     *            servlet response object to write to
     * @throws OGCWebServiceException
     */
    private void performGetFeature( WFService service, GetFeature request,
                                    HttpServletResponse httpResponse )
                            throws OGCWebServiceException {

        WFSConfiguration config = (WFSConfiguration) service.getCapabilities();
        FormatType formatType = determineFormat( request, config );

        // perform pre-processing if necessary (XSLT)
        if ( formatType.isVirtual() ) {
            request = transformGetFeature( request, formatType );
        }

        // perform request on WFService
        FeatureResult result = (FeatureResult) service.doService( request );
        FeatureCollection fc = (FeatureCollection) result.getResponse();

        String format = formatType.getValue();

        if ( GetFeature.FORMAT_FEATURECOLLECTION.equals( format ) ) {
            sendBinaryResponse( fc, httpResponse );
        } else if ( AbstractWFSRequest.FORMAT_XML.equals( format )
                    || format.startsWith( "text/xml; subtype=" ) ) {
            if ( formatType.getOutFilter() != null ) {
                sendTransformedResponse( fc, httpResponse, formatType );
            } else {
                String schemaURL = buildSchemaURL( service, request );
                sendGMLResponse( fc, httpResponse, schemaURL,
                                 suppressXLinkOutput( request, service ) );
            }
        } else {
            String msg = Messages.getMessage( "WFS_QUERY_UNSUPPORTED_FORMAT2", format );
            throw new OGCWebServiceException( msg );
        }
    }

    /**
     * Builds a KVP-encoded DescribeFeatureType-request that can be used to fetch the schemas
     * for all feature types are that queried in the given {@link GetFeature} request.
     *
     * @param service
     * @param request
     * @return KVP-encoded DescribeFeatureType-request
     */
    private String buildSchemaURL( WFService service, GetFeature request ) {

        String schemaURL = null;

        WFSCapabilities capa = (WFSCapabilities) service.getCapabilities();
        WFSOperationsMetadata opMetadata = (WFSOperationsMetadata) capa.getOperationsMetadata();
        Operation describeFTOperation = opMetadata.getDescribeFeatureType();
        DCPType[] dcpTypes = describeFTOperation.getDCPs();
        if ( dcpTypes.length > 0 && dcpTypes[0].getProtocol() instanceof HTTP ) {
            HTTP http = (HTTP) dcpTypes[0].getProtocol();
            if ( http.getGetOnlineResources().length > 0 ) {
                URL baseURL = http.getGetOnlineResources()[0];
                String requestPart = buildDescribeFTRequest( request );
                schemaURL = baseURL.toString() + requestPart;
            }
        }
        return schemaURL;
    }

    /**
     * Builds the parameter part for a KVP-encoded DescribeFeatureType-request that fetches the
     * necessary schemas for all feature types that are queried in the given {@link GetFeature}
     * request.
     *
     * @param request
     * @return the URL-encoded parameter part of a KVP-DescribeFeatureType request
     */
    private String buildDescribeFTRequest( GetFeature request ) {

        Set<QualifiedName> ftNames = new HashSet<QualifiedName>();
        Map<String, URI> nsBindings = new HashMap<String, URI>();

        // get all requested feature types
        Query[] queries = request.getQuery();
        for ( Query query : queries ) {
            QualifiedName[] typeNames = query.getTypeNames();
            for ( QualifiedName name : typeNames ) {
                ftNames.add( name );
            }
        }
        Iterator<QualifiedName> ftNameIter = ftNames.iterator();
        StringBuffer typeNameSb = new StringBuffer( ftNameIter.next().getAsString() );
        while ( ftNameIter.hasNext() ) {
            typeNameSb.append( ',' );
            typeNameSb.append( ftNameIter.next().getAsString() );
        }

        // get all used namespace bindings
        for ( QualifiedName ftName : ftNames ) {
            nsBindings.put( ftName.getPrefix(), ftName.getNamespace() );
        }
        StringBuffer nsParamSb = new StringBuffer( "xmlns(" );
        Iterator<String> prefixIter = nsBindings.keySet().iterator();
        String prefix = prefixIter.next();
        nsParamSb.append( prefix );
        nsParamSb.append( '=' );
        nsParamSb.append( nsBindings.get( prefix ) );
        while ( prefixIter.hasNext() ) {
            nsParamSb.append( ',' );
            prefix = prefixIter.next();
            nsParamSb.append( prefix );
            nsParamSb.append( '=' );
            nsParamSb.append( nsBindings.get( prefix ) );
        }
        nsParamSb.append( ')' );

        // build KVP-DescribeFeatureType-request
        StringBuffer sb = new StringBuffer( "SERVICE=WFS" );
        sb.append( "&VERSION=1.1.0" );
        sb.append( "&REQUEST=DescribeFeatureType" );

        // append TYPENAME parameter
        sb.append( "&TYPENAME=" );
        sb.append( typeNameSb );

        // append NAMESPACE parameter
        sb.append( "&NAMESPACE=" );
        sb.append( nsParamSb.toString() );

        return sb.toString();
    }

    /**
     * Transforms a {@link GetFeature} request depending on the requested virtual format.
     *
     * @param request
     *            GetFeature request to be transformed
     * @param format
     *            requested (virtual) output format
     * @return transformed GetFeature requested
     * @throws OGCWebServiceException
     *            if transformation script could not be loaded or transformation failed
     */
    private GetFeature transformGetFeature( GetFeature request, FormatType format )
                            throws OGCWebServiceException {

        // TODO: cache XSLTDocument
        XSLTDocument xsl = new XSLTDocument();
        try {
            xsl.load( format.getInFilter().toURL() );
        } catch ( Exception e ) {
            String msg = Messages.getMessage( "WFS_PREPROCESS_XSL_FILE_ERROR", format.getValue(),
                                              format.getInFilter().toString(), e );
            LOG.logError( msg );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        XMLFragment xml = null;
        try {
            xml = XMLFactory.export( request );
            xml = xsl.transform( xml, format.getInFilter().toASCIIString(), null, null );
        } catch ( Exception e ) {
            String msg = Messages.getMessage( "WFS_PREPROCESS_XSL_ERROR", format.getValue(), e );
            LOG.logError( msg );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        return GetFeature.create( request.getId(), xml.getRootElement() );
    }

    /**
     * Sends the given {@link FeatureCollection} as GML to the given
     * {@link HttpServletResponse} object.
     *
     * @param fc
     *            feature collection to send
     * @param httpResponse
     *            servlet response object to write to
     * @param schemaURL
     *            URL to schema document (DescribeFeatureType request)
     * @param suppressXLinks
     *            true, if no XLinks must be used in the output, false otherwise
     */
    private void sendGMLResponse( FeatureCollection fc, HttpServletResponse httpResponse,
                                  String schemaURL, boolean suppressXLinks ) {

        try {
            httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            OutputStream os = httpResponse.getOutputStream();
            GMLFeatureAdapter featureAdapter = new GMLFeatureAdapter( suppressXLinks, schemaURL );
            featureAdapter.export( fc, os, CharsetUtils.getSystemCharset() );
        } catch ( Exception e ) {
            LOG.logError( "Error sending GetFeature response (GML) to client.", e );
        }
    }

    /**
     * Sends the given {@link FeatureCollection} as a serialized Java object to the given
     * {@link HttpServletResponse} object.
     *
     * @param fc
     *            feature collection to send
     * @param httpResponse
     *            servlet response object to write to
     */
    private void sendBinaryResponse( FeatureCollection fc, HttpServletResponse httpResponse ) {
        try {
            OutputStream os = httpResponse.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( os );
            oos.writeObject( fc );
            oos.flush();
        } catch ( IOException e ) {
            LOG.logError( "Error sending GetFeature response (binary) to client.", e );
        }
    }

    /**
     * Transforms a {@link FeatureCollection} to the given format using XSLT and sends it to the
     * specified  {@link HttpServletResponse} object.
     *
     * @param fc
     *            feature collection to send
     * @param httpResponse
     *            servlet response object to write to
     * @param format
     *            requested format
     */
    private void sendTransformedResponse( FeatureCollection fc, HttpServletResponse httpResponse,
                                          FormatType format )
                            throws OGCWebServiceException {

        GMLFeatureAdapter featureAdapter = new GMLFeatureAdapter( true );

        // estimate that each feature is 2000 byte
        ByteArrayOutputStream bos = new ByteArrayOutputStream( fc.size() * 2000 );
        try {
            // export result feature collection as GML to enable transformation
            // into another (XML) format
            featureAdapter.export( fc, bos, CharsetUtils.getSystemCharset() );
        } catch ( Exception e ) {
            LOG.logError( "could not export feature collection to GML", e );
            throw new OGCWebServiceException( getClass().getName(),
                                              "could not export feature collection to GML; "
                                                                      + e.getMessage() );
        }

        // TODO: cache Transformer
        Transformer xsl = null;
        try {
            StreamSource src = new StreamSource( new File( format.getOutFilter() ) );
            xsl = TransformerFactory.newInstance().newTransformer( src );
        } catch ( Exception e ) {
            String msg = Messages.getMessage( "WFS_POSTPROCESS_XSL_FILE_ERROR", format.getValue(),
                                              format.getOutFilter().toString(), e );
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }

        try {
        	if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                FileUtils.writeToFile( "WFS_GetFeature_Result.xml", new String( bos.toByteArray() ) );
                LOG.logDebug( "Feature collection has been written to: WFS_GetFeature_Result.xml" );
            }
            // transform GML into desired output format and write back to client
            ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
            StreamSource src = new StreamSource( bis );
            String type = format.getValue().split( ";" )[0];
            httpResponse.setContentType( type + "; charset=" + CharsetUtils.getSystemCharset() );
            StreamResult res = new StreamResult( httpResponse.getOutputStream() );
            xsl.transform( src, res );
        } catch ( Exception e ) {
            String msg = Messages.getMessage( "WFS_POSTPROCESS_XSL_ERROR", format.getValue(), e );
            LOG.logError( msg, e );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }
    }

    /**
     * Performs a {@link Transaction} request and sends the response to the given
     * {@link HttpServletResponse} object.
     *
     * @param service
     *            WFService instance to be used
     * @param request
     *            Transaction request to be performed
     * @param httpResponse
     *            servlet response object to write to
     * @throws OGCWebServiceException
     */
    private void performTransaction( WFService service, Transaction request,
                                     HttpServletResponse httpResponse )
                            throws OGCWebServiceException {

        WFSConfiguration config = (WFSConfiguration) service.getCapabilities();
        FormatType format = determineFormat( request, config );

        // perform pre-processing if necessary (XSLT)
        if ( format.isVirtual() ) {
            request = transformTransaction( request, format );
        }

        TransactionResponse response = (TransactionResponse) service.doService( request );

        try {
            httpResponse.setContentType( "text/xml; charset=" + CharsetUtils.getSystemCharset() );
            XMLFragment document = XMLFactory.export( response );
            document.write( httpResponse.getOutputStream() );
        } catch ( IOException e ) {
            LOG.logError( "Error sending Transaction response to client.", e );
        }
    }

    /**
     * Transforms a {@link Transaction} request depending on the requested virtual format.
     *
     * @param request
     *            Transaction request to be transformed
     * @param formatType
     *            requested (virtual) output format
     * @return transformed Transaction
     * @throws OGCWebServiceException
     *            if transformation script could not be loaded or transformation failed
     */
    private Transaction transformTransaction( Transaction request, FormatType format )
                            throws OGCWebServiceException {

        // TODO: cache XSLTDocument
        XSLTDocument xsl = new XSLTDocument();
        try {
            xsl.load( format.getInFilter().toURL() );
        } catch ( Exception e ) {
            String msg = Messages.getMessage( "WFS_PREPROCESS_XSL_FILE_ERROR", format.getValue(),
                                              format.getInFilter().toString(), e );
            LOG.logError( msg, e );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        XMLFragment xml = null;
        try {
            //xml = XMLFactory.export( request );
            xml = request.getSourceDocument();
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                xml.prettyPrint( System.out );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( getClass().getName(), e.getMessage() );
        }
        // transform Transaction request
        try {
            xml = xsl.transform( xml, format.getInFilter().toASCIIString(), null, null );
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                xml.prettyPrint( System.out );
            }
        } catch ( Exception e ) {
            String msg = Messages.getMessage( "WFS_PREPROCESS_XSL_ERROR",
                                              format.getInFilter().toString(), e );
            LOG.logError( msg, e );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        try {
            request = Transaction.create( request.getId(), xml.getRootElement() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( getClass().getName(), e.getMessage() );
        }
        return request;
    }

    /**
     * Determines whether the response to the given {@link GetFeature} request may use XLinks or
     * not.
     * <p>
     * If the requested feature types belong to schemas that are configured to suppress XLink
     * output, this method returns true, otherwise false. If both suppressXLink=true and
     * suppressXLink=false apply, an OCGWebServiceException is thrown.
     *
     * @param request
     * @return true, if the response document must not contain XLinks, false otherwise
     * @throws OGCWebServiceException
     */
    private boolean suppressXLinkOutput( GetFeature request, WFService service )
                            throws OGCWebServiceException {
        int suppressXLinkOutput = 0;
        int useXLinkOutput = 0;
        Query[] queries = request.getQuery();
        for ( int i = 0; i < queries.length; i++ ) {
            Query query = queries[i];
            QualifiedName[] typeNames = query.getTypeNames();
            for ( int j = 0; j < typeNames.length; j++ ) {
                QualifiedName typeName = typeNames[j];
                MappedFeatureType ft = service.getMappedFeatureType( typeName );
                if ( ft == null ) {
                    throw new OGCWebServiceException( this.getClass().getName(),
                                                      "Internal error: WFS does not know feature type '"
                                                                              + typeName + "'." );
                }
                if ( ft.getGMLSchema().suppressXLinkOutput() ) {
                    suppressXLinkOutput++;
                } else {
                    useXLinkOutput++;
                }
            }
        }
        if ( suppressXLinkOutput > 0 && useXLinkOutput > 0 ) {
            throw new OGCWebServiceException(
                                              this.getClass().getName(),
                                              "Invalid request / configuration - "
                                                                      + "suppressXLinkOutput=true + suppressXLinkOutput=false at the same time." );
        }
        if ( suppressXLinkOutput > 0 ) {
            return true;
        }
        return false;
    }

    private FormatType determineFormat( GetFeature request, WFSConfiguration config )
                            throws OGCWebServiceException {

        Query firstQuery = request.getQuery()[0];
        QualifiedName ftName = firstQuery.getTypeNames()[0];
        WFSFeatureType wfsFT = config.getFeatureTypeList().getFeatureType( ftName );
        if ( wfsFT == null ) {
            String msg = Messages.getMessage( "WFS_QUERY_UNKNOWN_FEATURETYPE", ftName );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        String requestedFormat = request.getOutputFormat();
        FormatType format = wfsFT.getOutputFormat( requestedFormat );
        if ( format == null ) {
            String msg = Messages.getMessage( "WFS_QUERY_UNSUPPORTED_FORMAT", requestedFormat,
                                              ftName );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }
        return format;
    }

    private FormatType determineFormat( DescribeFeatureType request, WFSConfiguration config )
                            throws OGCWebServiceException {

        // NOTE: this cannot cope with a mix of virtual and real features
        QualifiedName ftName = null;
        if ( request.getTypeNames().length > 0 ) {
            ftName = request.getTypeNames()[0];
        } else {
            ftName = config.getFeatureTypeList().getFeatureTypes()[0].getName();
        }

        WFSFeatureType wfsFT = config.getFeatureTypeList().getFeatureType( ftName );
        if ( wfsFT == null ) {
            String msg = Messages.getMessage( "WFS_QUERY_UNKNOWN_FEATURETYPE", ftName );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        String requestedFormat = request.getOutputFormat();
        FormatType format = wfsFT.getOutputFormat( requestedFormat );
        if ( format == null ) {
            String msg = Messages.getMessage( "WFS_QUERY_UNSUPPORTED_FORMAT", requestedFormat,
                                              ftName );
            throw new OGCWebServiceException( getClass().getName(), msg );
        }
        return format;
    }

    private FormatType determineFormat( Transaction request, WFSConfiguration config )
                            throws OGCWebServiceException {

        FormatType format = null;

        WFSFeatureType wfsFT = config.getFeatureTypeList().getFeatureTypes()[0];

        List<TransactionOperation> list = request.getOperations();
        TransactionOperation op = list.get( 0 );
        if ( op instanceof Insert ) {
            QualifiedName qn = ( (Insert) op ).getAffectedFeatureTypes().get( 0 );
            wfsFT = config.getFeatureTypeList().getFeatureType( qn );
            if ( wfsFT == null ) {
                throw new OGCWebServiceException( Messages.getMessage( "WFS_INSERT_UNSUPPORTED_FT",
                                                                       qn ) );
            }
        } else if ( op instanceof Update ) {
            QualifiedName qn = ( (Update) op ).getAffectedFeatureTypes().get( 0 );
            wfsFT = config.getFeatureTypeList().getFeatureType( qn );
            if ( wfsFT == null ) {
                throw new OGCWebServiceException( Messages.getMessage( "WFS_UPDATE_UNSUPPORTED_FT",
                                                                       qn ) );
            }
        } else if ( op instanceof Delete ) {
            QualifiedName qn = ( (Delete) op ).getAffectedFeatureTypes().get( 0 );
            wfsFT = config.getFeatureTypeList().getFeatureType( qn );
            if ( wfsFT == null ) {
                throw new OGCWebServiceException( Messages.getMessage( "WFS_DELETE_UNSUPPORTED_FT",
                                                                       qn ) );
            }
        }

        FormatType[] formats = wfsFT.getOutputFormats();
        for ( int i = 0; i < formats.length; i++ ) {
            format = formats[i];
            if ( format.getInFilter() != null ) {
                break;
            }
        }

        return format;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSHandler.java,v $
 Revision 1.49  2006/10/20 15:35:41  poth
 now transform GetFeature responses (feature collections) will be written directly into the output stream to enable other transformation results than XML

 Revision 1.48  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.47  2006/10/11 20:30:45  mschneider
 Added GML application schema reference in output (xsi:schemaLocation).

 Revision 1.46  2006/10/11 17:59:24  mschneider
 Added code for schema reference in GetFeature-responses. Not activated yet.

 Revision 1.45  2006/10/05 17:00:13  poth
 debuging changed for transforming transactions

 Revision 1.44  2006/10/05 12:54:29  poth
 bug bix - determining FormatType for a Transaction

 Revision 1.43  2006/10/05 11:26:45  mschneider
 Improved javadoc. Added handling for uncatched Exceptions.

 Revision 1.42  2006/10/05 11:07:28  mschneider
 Restructured. Implemented better checks for preprocessing.

 Revision 1.41  2006/10/02 16:51:17  mschneider
 Javadoc fixes.

 Revision 1.40  2006/07/23 10:05:54  poth
 setting content type for Http responses enhanced by adding charset (for mime types text/plain and text/xml)

 Revision 1.39  2006/07/21 14:06:30  mschneider
 GetCapabilities responses should respect requested sections now.

 Revision 1.38  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */
