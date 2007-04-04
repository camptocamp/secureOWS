//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/Discovery.java,v 1.85 2006/10/18 17:00:55 poth Exp $
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
package org.deegree.ogcwebservices.csw.discovery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.deegree.enterprise.servlet.OGCServletController;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.framework.xml.schema.XSDocument;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueOperationsMetadata;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfiguration;
import org.deegree.ogcwebservices.csw.configuration.CatalogueConfigurationDocument;
import org.deegree.ogcwebservices.csw.configuration.CatalogueOutputSchemaParameter;
import org.deegree.ogcwebservices.csw.configuration.CatalogueOutputSchemaValue;
import org.deegree.ogcwebservices.csw.configuration.CatalogueTypeNameSchemaParameter;
import org.deegree.ogcwebservices.csw.configuration.CatalogueTypeNameSchemaValue;
import org.deegree.ogcwebservices.csw.discovery.GetRecords.RESULT_TYPE;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;

/**
 * The Discovery class allows clients to discover resources registered in a catalogue, by providing
 * four operations named <code>query</code>,<code>present</code>,
 * <code>describeRecordType</code>, and <code>getDomain</code>. This class has a required
 * association from the Catalogue Service class, and is thus always implemented by all Catalogue
 * Service implementations. The Session class can be included with the Discovery class, in
 * associations with the Catalogue Service class. The &quote;query&quote; and &quote;present&quote;
 * operations may be executed in a session or stateful context. If a session context exists, the
 * dynamic model uses internal states of the session and the allowed transitions between states.
 * When the &quote;query&quote; and &quote;present&quote; state does not include a session between a
 * server and a client, any memory or shared information between the client and the server may be
 * based on private understandings or features available in the protocol binding. The
 * describeRecordType and getDomain operations do not require a session context.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.85 $, $Date: 2006/10/18 17:00:55 $
 * 
 * @since 2.0
 */
public class Discovery {

    private static final ILogger LOG = LoggerFactory.getLogger( Discovery.class );

    // Keys are Strings, values are XSLDocuments
    private static final Map<String, XSLTDocument> IN_XSL = new HashMap<String, XSLTDocument>();

    // Keys are Strings, values are XSLDocuments
    private static final Map<String, XSLTDocument> OUT_XSL = new HashMap<String, XSLTDocument>();

    // Keys are Strings, values are URLs
    private static final Map<String, URL> SCHEMA_URLS = new HashMap<String, URL>();

    // Keys are Strings, values are XMLFragments
    private static final Map<String, XSDocument> SCHEMA_DOCS = new HashMap<String, XSDocument>();

    private static final String DEFAULT_SCHEMA = "DublinCore";

    private static final String OGC_CORE_SCHEMA = "OGCCORE";

    private CatalogueConfiguration cswConfiguration = null;

    /**
     * The complete data access of a catalog service is managed by one instances of WFService.
     */
    private WFService wfsResource; // single instance only for this CSW

    public Discovery( WFService wfsService, CatalogueConfiguration cswConfiguration ) {
        this.wfsResource = wfsService;
        this.cswConfiguration = cswConfiguration;
        try {
            CatalogueOperationsMetadata catalogMetadata = (CatalogueOperationsMetadata) cswConfiguration.getOperationsMetadata();
            CatalogueOutputSchemaParameter outputSchemaParameter = (CatalogueOutputSchemaParameter) catalogMetadata.getGetRecords().getParameter(
                                                                                                                                                  "outputSchema" );

            CatalogueConfigurationDocument document = new CatalogueConfigurationDocument();
            document.setSystemId( cswConfiguration.getSystemId() );
            CatalogueOutputSchemaValue[] values = outputSchemaParameter.getSpecializedValues();
            for ( int i = 0; i < values.length; i++ ) {
                CatalogueOutputSchemaValue value = values[i];
                String schemaName = value.getValue().toUpperCase();

                URL fileURL = document.resolve( value.getInXsl() );
                LOG.logInfo( StringTools.concat( 300, "Input schema '", schemaName,
                                                 "' is processed using XSLT-sheet from URL '",
                                                 fileURL, "'" ) );
                XSLTDocument inXSLSheet = new XSLTDocument();
                inXSLSheet.load( fileURL );
                IN_XSL.put( schemaName, inXSLSheet );

                fileURL = document.resolve( value.getOutXsl() );
                LOG.logInfo( StringTools.concat( 300, "Output schema '", schemaName,
                                                 "' is processed using XSLT-sheet from URL '",
                                                 fileURL, "'" ) );
                XSLTDocument outXSLSheet = new XSLTDocument();
                outXSLSheet.load( fileURL );
                OUT_XSL.put( schemaName, outXSLSheet );

            }

            // read and store schema definitions
            // each type(Name) provided by a CS-W is assigned to one schema
            CatalogueTypeNameSchemaParameter outputTypeNameParameter = (CatalogueTypeNameSchemaParameter) catalogMetadata.getGetRecords().getParameter(
                                                                                                                                                        "typeName" );
            CatalogueTypeNameSchemaValue[] tn_values = outputTypeNameParameter.getSpecializedValues();
            for ( int i = 0; i < tn_values.length; i++ ) {
                CatalogueTypeNameSchemaValue value = tn_values[i];
                URL fileURL = document.resolve( value.getSchema() );
                XSDocument schemaDoc = new XSDocument();
                schemaDoc.load( fileURL );
                String typeName = value.getValue().toUpperCase();
                LOG.logInfo( StringTools.concat( 300, "Schema for type '", typeName,
                                                 "' is defined in XSD-file at URL '", fileURL, "'" ) );
                SCHEMA_URLS.put( typeName, fileURL );
                SCHEMA_DOCS.put( typeName, schemaDoc );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( "Error while creating CSW Discovery: " + e.getMessage(), e );
        }
        WFSCapabilities capa = (WFSCapabilities) wfsResource.getCapabilities();
        LOG.logInfo( "CSW Discovery initialized with WFS resource "
                     + capa.getServiceIdentification().getTitle() );
    }

    public void finalize()
                            throws Throwable {
        super.finalize();
    }

    /**
     * Performs the submitted <code>DescribeRecord</code> -request.
     * 
     * TODO: Check output schema & Co.
     * 
     * @param request
     */
    public DescribeRecordResult describeRecordType( DescribeRecord request )
                            throws OGCWebServiceException {

        // requested output format must be 'text/xml'
        if ( !( "text/xml".equals( request.getOutputFormat() ) ) ) {
            throw new OGCWebServiceException( "Discovery", "Requested output format '" + 
                                              request.getOutputFormat() + "' is not " +
                                               "supported. The only supported format " + 
                                               "is 'text/xml'.", ExceptionCode.INVALID_FORMAT );
        }

        // requested schema language must be 'XMLSCHEMA'
        if ( !( "XMLSCHEMA".equals( request.getSchemaLanguage().toString() ) ) ) {
            throw new InvalidParameterValueException( "Requested schema language '" + 
                                                      request.getSchemaLanguage().toString() + 
                                                      "' is not supported. The only " +
                                                      "supported schema language is 'XMLSCHEMA'." );
        }

        // if no type names are specified, describe all known types 
        String[] typeNames = request.getTypeNames();
        if ( typeNames == null || typeNames.length == 0 ) {
            
            typeNames = SCHEMA_DOCS.keySet().toArray( new String[SCHEMA_DOCS.keySet().size()] );
        }

        SchemaComponent[] schemaComponents = new SchemaComponent[typeNames.length];

        for ( int i = 0; i < typeNames.length; i++ ) {
            XSDocument doc = SCHEMA_DOCS.get( typeNames[i].toUpperCase() );
            if ( doc == null ) {
                String msg = "Type '" + typeNames[i] + " is not known to this Catalogue Service.";
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            try {
                schemaComponents[i] = new SchemaComponent( doc,
                                                           new URI( "http://www.deegree.org/csw" ),
                                                           null, new URI( "XMLSCHEMA" ) );
            } catch ( URISyntaxException e ) {
                throw new OGCWebServiceException( this.getClass().getName(), e.getMessage() );
            }
        }

        return new DescribeRecordResult( request, "2.0.0", schemaComponents );
    }

    /**
     * @param request
     * @todo not implemented, yet
     */
    public DomainValues getDomain( GetDomain request ) {
        return new DomainValues();
    }

    private String normalizeOutputSchema( String outputSchema )
                            throws InvalidParameterValueException {
        if ( outputSchema == null ) {
            outputSchema = DEFAULT_SCHEMA;
        } else if ( outputSchema.equalsIgnoreCase( OGC_CORE_SCHEMA ) ) {
            outputSchema = DEFAULT_SCHEMA;
        }
        outputSchema = outputSchema.toUpperCase();
        if ( IN_XSL.get( outputSchema ) == null ) {
            String msg = "Unsupported output schema '" + outputSchema
                         + "' requested. Supported schemas are: ";
            Iterator it = IN_XSL.keySet().iterator();
            while ( it.hasNext() ) {
                msg += (String) it.next();
                if ( it.hasNext() ) {
                    msg += ", ";
                } else {
                    msg += ".";
                }
            }
            throw new InvalidParameterValueException( msg );
        }
        return outputSchema;
    }

    /**
     * Performs a <code>GetRecords</code> request.
     * <p>
     * This involves the following steps:
     * <ul>
     * <li><code>GetRecords</code>-><code>GetRecordsDocument</code></li>
     * <li><code>GetRecordsDocument</code>-><code>GetFeatureDocument</code> using XSLT</li>
     * <li><code>GetFeatureDocument</code>-><code>GetFeature</code></li>
     * <li><code>GetFeature</code> request is performed against the underlying WFS</li>
     * <li>WFS answers with a <code>FeatureResult</code> object (which contains a
     * <code>FeatureCollection</code>)</li>
     * <li><code>FeatureCollection</code>-> GMLFeatureCollectionDocument (as a String)</li>
     * <li>GMLFeatureCollectionDocument</code>-><code>GetRecordsResultDocument</code> using
     * XSLT</li>
     * <li><code>GetRecordsResultDocument</code>-><code>GetRecordsResult</code></li>
     * </ul>
     * </p>
     * 
     * @param getRecords
     * @return GetRecordsResult
     * @throws OGCWebServiceException
     */
    public GetRecordsResult query( GetRecords getRecords )
                            throws OGCWebServiceException {

        LOG.entering();
        
        GetFeature getFeature = null;
        XMLFragment getFeatureDocument = null;
        Object wfsResponse = null;
        GetRecordsResult cswResponse = null;
        String outputSchema = normalizeOutputSchema( getRecords.getOutputSchema() );
        

        // TODO remove this (only necessary because determineRecordsMatched changes the resultType)
        String resultType = getRecords.getResultTypeAsString();

        XMLFragment getRecordsDocument = 
            new XMLFragment( XMLFactory.export( getRecords ).getRootElement() );
        LOG.logDebug( "Input GetRecords request:\n" + getRecordsDocument );
        try {
            // incoming GetRecord request must be transformed to a GetFeature
            // request because the underlying 'data engine' of the CSW is a WFS
            XSLTDocument xslSheet = IN_XSL.get( outputSchema );
            getFeatureDocument = xslSheet.transform( getRecordsDocument );
            LOG.logDebug( "Generated WFS GetFeature request:\n" + getFeatureDocument );
        } catch ( TransformerException e ) {
            e.printStackTrace();
            String msg = "Can't transform GetRecord request to WFS GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            StringWriter sw = new StringWriter( 5000 );
            try {
                getFeatureDocument.prettyPrint( sw );
            } catch ( TransformerException e ) {
                getFeatureDocument.write( sw );
            }
            LOG.logDebug( sw.getBuffer().toString() );
        }

        try {
            getFeature = GetFeature.create( getRecords.getId(), getFeatureDocument.getRootElement() );
        } catch ( Exception e ) {
            String msg = "Cannot generate object representation for GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        try {
            wfsResponse = wfsResource.doService( getFeature );
        } catch ( OGCWebServiceException e ) {
            String msg = "Generated WFS GetFeature request failed: " + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        // theoretical it is possible the result of a GetFeature request is not
        // an instance of FeatureResult; but this never should happen
        if ( !( wfsResponse instanceof FeatureResult ) ) {
            String msg = "Unexpected result type '" + wfsResponse.getClass().getName()
                         + "' from WFS (must be FeatureResult)."
                         + " Maybe a FeatureType is not correctly registered!?";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }

        FeatureResult featureResult = (FeatureResult) wfsResponse;

        // this never should happen too - but it is possible
        if ( !( featureResult.getResponse() instanceof FeatureCollection ) ) {
            String msg = "Unexpected reponse type: '"
                         + featureResult.getResponse().getClass().getName() + " "
                         + featureResult.getResponse().getClass()
                         + "' in FeatureResult of WFS (must be a FeatureCollection).";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }
        FeatureCollection featureCollection = (FeatureCollection) featureResult.getResponse();

        try {
            int numberOfRecordsReturned = featureCollection.size();
            int numberOfMatchedRecords = 0;
            if ( getRecords.getResultType().equals( RESULT_TYPE.HITS ) ) {
                numberOfMatchedRecords = 
                    Integer.parseInt( featureCollection.getAttribute( "numberOfFeatures" ) );
            } else {
                // if result type does not equal 'HITS', a separate request must
                // be created and performed to determine how many records match
                // the query
                numberOfMatchedRecords = determineRecordsMatched( getRecords );
            }

            int startPosition = getRecords.getStartPosition();
            if ( startPosition < 1 )
                startPosition = 1;
            int nextRecord = startPosition + featureCollection.size();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put( "REQUEST_ID", getRecords.getId() );
            if ( numberOfRecordsReturned != 0 ) {
                params.put( "SEARCH_STATUS", "complete" );
            } else {
                params.put( "SEARCH_STATUS", "none" );
            }
            params.put( "TIMESTAMP", TimeTools.getISOFormattedTime() );
            String[] typenames = getRecords.getQueries()[0].getTypeNames();
            // this is a bit critical because 
            // a) not the complete result can be validated but just single records
            // b) it is possible that several different record types are part
            //    of a response that must be validated against different schemas
            String s = StringTools.concat( 300, OGCServletController.address,
                                           "?service=CSW&version=2.0.0&",
                                           "request=DescribeRecord&typeName=", typenames[0] );
            params.put( "RECORD_SCHEMA", s );
            params.put( "RECORDS_MATCHED", "" + numberOfMatchedRecords );
            params.put( "RECORDS_RETURNED", "" + numberOfRecordsReturned );
            params.put( "NEXT_RECORD", "" + nextRecord );
            String elementSet = getRecords.getQueries()[0].getElementSetName();
            if ( elementSet == null ) {
                elementSet = "CUSTOM";
            }
            params.put( "ELEMENT_SET", elementSet );
            params.put( "RESULT_TYPE",  resultType );
            params.put( "REQUEST_NAME", "GetRecords" );

            ByteArrayOutputStream bos = new ByteArrayOutputStream( 50000 );
            GMLFeatureAdapter ada = new GMLFeatureAdapter( true );
            ada.export( featureCollection, bos );

            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                s = new String( bos.toByteArray() );
                LOG.logDebug( s );
                FileUtils.writeToFile( "CSW_GetRecord_FC.xml", s );
            }

            // vice versa to request transforming the feature collection being result
            // to the GetFeature request must be transformed into a GetRecords result
            ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
            XSLTDocument xslSheet = OUT_XSL.get( outputSchema );
            XMLFragment resultDocument = xslSheet.transform( bis, null, null, params );
            GetRecordsResultDocument cswResponseDocument = new GetRecordsResultDocument();
            cswResponseDocument.setRootElement( resultDocument.getRootElement() );
            cswResponse = cswResponseDocument.parseGetRecordsResponse( getRecords );
        } catch ( Exception e ) {
            String msg = "Can't transform WFS response (FeatureCollection) to CSW response: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        LOG.exiting();
        return cswResponse;
    }

    /**
     * Returns the number of records matching a GetRecords request.
     * 
     * @param getRecords
     * @return the number of records matching a GetRecords request
     * @throws OGCWebServiceException 
     */
    private int determineRecordsMatched( GetRecords getRecords )
                            throws OGCWebServiceException {
        getRecords.setResultType( GetRecords.RESULT_TYPE.HITS );
        GetFeature getFeature = null;
        XMLFragment getFeatureDocument = null;
        Object wfsResponse = null;
        String outputSchema = normalizeOutputSchema( getRecords.getOutputSchema() );

        XMLFragment getRecordsDocument = new XMLFragment(
                                                          XMLFactory.export( getRecords ).getRootElement() );
        try {
            XSLTDocument xslSheet = IN_XSL.get( outputSchema );
            getFeatureDocument = xslSheet.transform( getRecordsDocument );
            LOG.logDebug( "Generated WFS GetFeature request (HITS):\n" + getFeatureDocument );
        } catch ( TransformerException e ) {
            e.printStackTrace();
            String msg = "Can't transform GetRecord request to WFS GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        try {
            getFeature = GetFeature.create( getRecords.getId(), getFeatureDocument.getRootElement() );
        } catch ( Exception e ) {
            String msg = "Cannot generate object representation for GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        try {
            wfsResponse = wfsResource.doService( getFeature );
        } catch ( OGCWebServiceException e ) {
            String msg = "Generated WFS GetFeature request failed: " + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        if ( !( wfsResponse instanceof FeatureResult ) ) {
            String msg = "Unexpected result type '" + wfsResponse.getClass().getName()
                         + "' from WFS (must be FeatureResult)."
                         + " Maybe a FeatureType is not correctly registered!?";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }

        FeatureResult featureResult = (FeatureResult) wfsResponse;

        if ( !( featureResult.getResponse() instanceof FeatureCollection ) ) {
            String msg = "Unexpected reponse type: '"
                         + featureResult.getResponse().getClass().getName() + " "
                         + featureResult.getResponse().getClass()
                         + "' in FeatureResult of WFS (must be a FeatureCollection).";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }
        FeatureCollection featureCollection = (FeatureCollection) featureResult.getResponse();

        return Integer.parseInt( featureCollection.getAttribute( "numberOfFeatures" ) );
    }

    /**
     * Performs a <code>GetRecordById</code> request.
     * <p>
     * This involves the following steps:
     * <ul>
     * <li><code>GetRecordById</code>-><code>GetRecordByIdDocument</code></li>
     * <li><code>GetRecordByIdDocument</code>-><code>GetFeatureDocument</code> using XSLT</li>
     * <li><code>GetFeatureDocument</code>-><code>GetFeature</code></li>
     * <li><code>GetFeature</code> request is performed against the underlying WFS</li>
     * <li>WFS answers with a <code>FeatureResult</code> object (which contains a
     * <code>FeatureCollection</code>)</li>
     * <li><code>FeatureCollection</code>-> GMLFeatureCollectionDocument (as a String)</li>
     * <li>GMLFeatureCollectionDocument</code>-><code>GetRecordsResultDocument</code> using
     * XSLT</li>
     * <li><code>GetRecordsResultDocument</code>-><code>GetRecordsResult</code></li>
     * </ul>
     * </p>
     * @param getRecordById
     * @return
     * @throws OGCWebServiceException
     */
    public GetRecordByIdResult query( GetRecordById getRecordById )
                            throws OGCWebServiceException {
        
        GetFeature getFeature = null;
        XMLFragment getFeatureDocument = null;
        Object wfsResponse = null;
        GetRecordByIdResult cswResponse = null;
        String outputSchema = cswConfiguration.getDeegreeParams().getDefaultOutputSchema();

        XMLFragment getRecordsDocument = new XMLFragment(
                                                          XMLFactory.export( getRecordById ).getRootElement() );
        try {
            XSLTDocument xslSheet = IN_XSL.get( outputSchema.toUpperCase() );
            getFeatureDocument = xslSheet.transform( getRecordsDocument );
            LOG.logDebug( "Generated WFS GetFeature request:\n" + getFeatureDocument );
        } catch ( TransformerException e ) {
            String msg = "Can't transform GetRecordById request to WFS GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            StringWriter sw = new StringWriter( 5000 );
            getFeatureDocument.write( sw );
            LOG.logDebug( sw.getBuffer().toString() );
        }

        try {
            getFeature = GetFeature.create( getRecordById.getId(),
                                            getFeatureDocument.getRootElement() );
        } catch ( Exception e ) {
            String msg = "Cannot generate object representation for GetFeature request: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        try {
            wfsResponse = wfsResource.doService( getFeature );
        } catch ( OGCWebServiceException e ) {
            String msg = "Generated WFS GetFeature request failed: " + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        if ( !( wfsResponse instanceof FeatureResult ) ) {
            String msg = "Unexpected result type '" + wfsResponse.getClass().getName()
                         + "' from WFS (must be FeatureResult)."
                         + " Maybe a FeatureType is not correctly registered!?";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }

        FeatureResult featureResult = (FeatureResult) wfsResponse;

        if ( !( featureResult.getResponse() instanceof FeatureCollection ) ) {
            String msg = "Unexpected reponse type: '"
                         + featureResult.getResponse().getClass().getName() + " "
                         + featureResult.getResponse().getClass()
                         + "' in FeatureResult of WFS (must be a FeatureCollection).";
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }
        FeatureCollection featureCollection = (FeatureCollection) featureResult.getResponse();

        try {
            int numberOfMatchedRecords = featureCollection == null ? 0 : featureCollection.size();
            int startPosition = 1;
            long maxRecords = Integer.MAX_VALUE;
            long numberOfRecordsReturned = startPosition + maxRecords < numberOfMatchedRecords ? maxRecords
                                                                                              : numberOfMatchedRecords
                                                                                                - startPosition
                                                                                                + 1;
            long nextRecord = numberOfRecordsReturned + startPosition > numberOfMatchedRecords ? 0
                                                                                              : numberOfRecordsReturned
                                                                                                + startPosition;

            HashMap<String, String> params = new HashMap<String, String>();
            params.put( "REQUEST_ID", getRecordById.getId() );
            if ( numberOfRecordsReturned != 0 ) {
                params.put( "SEARCH_STATUS", "complete" );
            } else {
                params.put( "SEARCH_STATUS", "none" );
            }
            params.put( "TIMESTAMP", TimeTools.getISOFormattedTime() );
            String s = OGCServletController.address
                       + "?service=CSW&version=2.0.0&request=DescribeRecord";
            params.put( "RECORD_SCHEMA", s );
            params.put( "RECORDS_MATCHED", "" + numberOfMatchedRecords );
            params.put( "RECORDS_RETURNED", "" + numberOfRecordsReturned );
            params.put( "NEXT_RECORD", "" + nextRecord );
            params.put( "ELEMENT_SET", "full" );
            params.put( "REQUEST_NAME", "GetRecordById" );

            featureCollection.setAttribute( "byID", "true" );
            ByteArrayOutputStream bos = new ByteArrayOutputStream( 50000 );
            GMLFeatureAdapter ada = new GMLFeatureAdapter( true );
            ada.export( featureCollection, bos );

            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                LOG.logDebug( new String( bos.toByteArray() ) );
            }

            ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
            XSLTDocument xslSheet = OUT_XSL.get( outputSchema.toUpperCase() );
            XMLFragment resultDocument = xslSheet.transform( bis, null, null, params );
            GetRecordByIdResultDocument cswResponseDocument = new GetRecordByIdResultDocument();
            cswResponseDocument.setRootElement( resultDocument.getRootElement() );
            cswResponse = cswResponseDocument.parseGetRecordByIdResponse( getRecordById );
        } catch ( Exception e ) {
            e.printStackTrace();
            String msg = "Can't transform WFS response (FeatureCollection) " + "to CSW response: "
                         + e.getMessage();
            LOG.logError( msg, e );
            throw new OGCWebServiceException( msg );
        }

        return cswResponse;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Discovery.java,v $
 Revision 1.85  2006/10/18 17:00:55  poth
 made DefaultOGCWebServiceResponse base type for all webservice responses

 Revision 1.84  2006/10/17 20:31:20  poth
 *** empty log message ***

 Revision 1.83  2006/10/10 15:53:26  mschneider
 Improved debug output.

 Revision 1.82  2006/10/09 20:03:53  poth
 bug fixes

 Revision 1.81  2006/08/02 14:17:13  poth
 comments added

 Revision 1.80  2006/07/11 06:39:05  poth
 code formatting

 Revision 1.79  2006/07/10 20:56:37  mschneider
 Added XSLT parameter RESULT_TYPE.

 Revision 1.78  2006/07/09 20:53:08  mschneider
 Improved type handling. Added generics.

 Revision 1.77  2006/06/29 12:00:11  poth
 caused by some ambigous parts of the CSW spec resultType in GetRecords requests now wil be treated not case-sensitive

 Revision 1.76  2006/06/29 10:27:17  mschneider
 Changed resultType of GetRecords to enum.

 Revision 1.75  2006/06/21 17:11:24  mschneider
 Added handling for null ELEMENT_SET parameter.

 Revision 1.74  2006/05/20 15:57:34  poth
 code formating

 Revision 1.73  2006/04/26 15:09:37  poth
 *** empty log message ***

 Revision 1.72  2006/04/18 18:22:55  poth
 *** empty log message ***

 Revision 1.71  2006/04/15 15:30:20  poth
 *** empty log message ***

 Revision 1.70  2006/04/06 20:25:25  poth
 *** empty log message ***

 Revision 1.69  2006/04/04 20:39:41  poth
 *** empty log message ***

 Revision 1.68  2006/03/30 21:20:25  poth
 *** empty log message ***

 Revision 1.67  2006/03/30 17:26:49  poth
 *** empty log message ***

 Revision 1.66  2006/03/29 20:32:31  poth
 *** empty log message ***

 Revision 1.65  2006/03/21 13:23:56  poth
 *** empty log message ***

 Revision 1.64  2006/03/20 12:17:50  poth
 *** empty log message ***

 Revision 1.63  2006/03/17 15:21:48  poth
 *** empty log message ***

 Revision 1.62  2006/03/17 14:55:31  poth
 *** empty log message ***

 Revision 1.61  2006/03/17 07:56:21  poth
 *** empty log message ***

 Revision 1.60  2006/03/16 17:02:11  poth
 *** empty log message ***

 Revision 1.59  2006/03/16 16:47:45  poth
 *** empty log message ***

 Revision 1.58  2006/03/13 10:39:08  poth
 *** empty log message ***

 Revision 1.57  2006/03/01 14:11:04  poth
 *** empty log message ***

 Revision 1.56  2006/02/26 21:30:42  poth
 *** empty log message ***

 Revision 1.55  2006/02/23 11:46:13  mays
 bugfix -> using OutputStream instead of PrintWriter for exporting FeatureCollection

 Revision 1.54  2006/02/20 12:40:02  poth
 *** empty log message ***

 ********************************************************************** */