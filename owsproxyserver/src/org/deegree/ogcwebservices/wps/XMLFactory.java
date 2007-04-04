/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
package org.deegree.ogcwebservices.wps;

import static org.deegree.ogcbase.CommonNamespaces.WPSNS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.ValueRange;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureException;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.MetadataType;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wps.capabilities.ProcessOfferings;
import org.deegree.ogcwebservices.wps.capabilities.WPSCapabilities;
import org.deegree.ogcwebservices.wps.capabilities.WPSCapabilitiesDocument;
import org.deegree.ogcwebservices.wps.describeprocess.ComplexData;
import org.deegree.ogcwebservices.wps.describeprocess.InputDescription;
import org.deegree.ogcwebservices.wps.describeprocess.LiteralInput;
import org.deegree.ogcwebservices.wps.describeprocess.LiteralOutput;
import org.deegree.ogcwebservices.wps.describeprocess.OutputDescription;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescription;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescriptions;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescriptionsDocument;
import org.deegree.ogcwebservices.wps.describeprocess.SupportedCRSs;
import org.deegree.ogcwebservices.wps.describeprocess.SupportedComplexData;
import org.deegree.ogcwebservices.wps.describeprocess.SupportedUOMs;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescription.DataInputs;
import org.deegree.ogcwebservices.wps.describeprocess.ProcessDescription.ProcessOutputs;
import org.deegree.ogcwebservices.wps.execute.ComplexValue;
import org.deegree.ogcwebservices.wps.execute.ExecuteDataInputs;
import org.deegree.ogcwebservices.wps.execute.ExecuteResponse;
import org.deegree.ogcwebservices.wps.execute.ExecuteResponseDocument;
import org.deegree.ogcwebservices.wps.execute.IOValue;
import org.deegree.ogcwebservices.wps.execute.OutputDefinition;
import org.deegree.ogcwebservices.wps.execute.OutputDefinitions;
import org.deegree.ogcwebservices.wps.execute.ProcessFailed;
import org.deegree.ogcwebservices.wps.execute.ProcessStarted;
import org.deegree.ogcwebservices.wps.execute.Status;
import org.deegree.ogcwebservices.wps.execute.IOValue.ComplexValueReference;
import org.deegree.owscommon.OWSMetadata;
import org.deegree.owscommon.com110.OWSAllowedValues;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * XMLFactory.java
 * 
 * Created on 08.03.2006. 23:28:46h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */
public class XMLFactory extends org.deegree.owscommon.XMLFactory {

    /**
     * 
     * @param capabilities
     * @return
     * @throws InvalidCapabilitiesException
     * @throws IOException
     */
    public static WPSCapabilitiesDocument export( WPSCapabilities capabilities )
                            throws IOException {
        LOG.entering();
        WPSCapabilitiesDocument capabilitiesDocument = new WPSCapabilitiesDocument();

        try {
            capabilitiesDocument.createEmptyDocument();
            Element root = capabilitiesDocument.getRootElement();

            ServiceIdentification serviceIdentification = capabilities.getServiceIdentification();
            if ( serviceIdentification != null ) {
                appendServiceIdentification( root, serviceIdentification );
            }

            ServiceProvider serviceProvider = capabilities.getServiceProvider();
            if ( serviceProvider != null ) {
                appendServiceProvider( root, capabilities.getServiceProvider() );
            }

            OperationsMetadata operationsMetadata = capabilities.getOperationsMetadata();
            if ( operationsMetadata != null ) {
                appendOperationsMetadata( root, operationsMetadata );
            }
            Contents contents = capabilities.getContents();
            if ( contents != null ) {
                // appendContents(root, contents);
            }
            ProcessOfferings processOfferings = capabilities.getProcessOfferings();
            if ( null != processOfferings ) {
                appendProcessOfferings( root, processOfferings );
            }

        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        }
        LOG.exiting();
        return capabilitiesDocument;
    }

    /**
     * Appends the DOM representation of the <code>wps:ProcessOfferings</code>-
     * section to the passed <code>Element</code>.
     * 
     * @param root
     * @param processOfferings
     */
    private static void appendProcessOfferings( Element root, ProcessOfferings processOfferings ) {
        LOG.entering();
        // Add a <wps:ProcessOfferings>node to the <wps:Capabilities> node
        Element processOfferingsNode = XMLTools.appendElement( root, WPSNS, "wps:ProcessOfferings",
                                                               null );
        // Add <wps:Process> nodes to the <wps:ProcessOfferings> node
        List<ProcessBrief> processBriefTypeList = processOfferings.getProcessBriefTypesList();
        if ( null != processBriefTypeList && 0 != processBriefTypeList.size() ) {
            int size = processBriefTypeList.size();
            for ( int i = 0; i < size; i++ ) {
                appendProcessBriefType( processOfferingsNode, processBriefTypeList.get( i ) );
            }
        }

        LOG.exiting();
    }

    /**
     * Appends the DOM representation of the <code>ProcessBriefType</code>
     * instance to the passed <code>Element</code>.
     * 
     * @param processOfferingsNode
     * @param processBriefType
     */
    private static void appendProcessBriefType( Element processOfferingsNode,
                                               ProcessBrief processBriefType ) {

        // Add a <wps:Process> node to the <wps:ProcessOfferings> node
        Element processBriefTypeNode = XMLTools.appendElement( processOfferingsNode, WPSNS,
                                                               "wps:Process" );
        // Add optional attribute "processVersion" to <wps:Process> node if
        // present
        String processVersion = processBriefType.getProcessVersion();
        if ( null != processVersion && !"".equals( processVersion ) ) {
            processBriefTypeNode.setAttribute( "processVersion", processVersion );
        }

        // Add mandatory node <ows:Identifier>
        Code identifier = processBriefType.getIdentifier();
        if ( null != identifier ) {
            appendIdentifier( processBriefTypeNode, identifier );
        } else {
            LOG.logError( "identifier is null." );
        }

        // Add mandatory node <ows:Title>
        String title = processBriefType.getTitle();
        if ( null != title ) {
            appendTitle( processBriefTypeNode, title );
        } else {
            LOG.logError( "title is null." );
        }

        // Add optional node <ows:Abstract/>

        String _abstract = processBriefType.getAbstract();
        if ( null != _abstract ) {
            appendAbstract( processBriefTypeNode, _abstract );
        }

        // Add optional nodes <ows:Metadata/>
        List<MetadataType> metaDataTypeList = processBriefType.getMetadata();
        if ( null != metaDataTypeList && 0 != metaDataTypeList.size() ) {
            int size = metaDataTypeList.size();
            for ( int i = 0; i < size; i++ ) {
                appendMetaDataType( processBriefTypeNode, metaDataTypeList.get( i ) );
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param e
     * @param identifier
     */
    private static void appendIdentifier( Element e, Code identifier ) {

        String identifierString = identifier.getCode();

        if ( null != identifierString && !"".equals( identifierString ) ) {
            XMLTools.appendElement( e, OWSNS, "ows:Identifier", identifierString );
        } else {
            LOG.logError( "identifier is empty." );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param e
     * @param title
     */
    private static void appendTitle( Element e, String title ) {

        if ( !"".equals( title ) ) {
            XMLTools.appendElement( e, OWSNS, "ows:Title", title );
        } else {
            LOG.logError( "title is empty." );
        }
        LOG.exiting();
    }

    /**
     * Appends the DOM representation of the <code>MetadataType</code>
     * instance to the passed <code>Element</code>.
     * 
     * @param e
     * @param metaDataType
     */
    private static void appendMetaDataType( Element e, MetadataType metaDataType ) {
        LOG.entering();
        // Add optional node <ows:Metadata>
        String metadataTypeValue = metaDataType.value;
        if ( null != metadataTypeValue && !"".equals( metadataTypeValue ) ) {
            Element metadataNode = XMLTools.appendElement( e, OWSNS, "ows:Metadata" );
            metadataNode.setAttributeNS( XLNNS.toString(), "xlink:title", metadataTypeValue );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param processDescriptions
     * @return
     * @throws InvalidCapabilitiesException
     * @throws IOException
     */
    public static ProcessDescriptionsDocument export( ProcessDescriptions processDescriptions )
                            throws IOException {
        ProcessDescriptionsDocument processDescriptionsDocument = new ProcessDescriptionsDocument();
        try {
            processDescriptionsDocument.createEmptyDocument();
            Element root = processDescriptionsDocument.getRootElement();
            List<ProcessDescription> processDescriptionList = processDescriptions.getProcessDescription();
            if ( null != processDescriptionList ) {
                int processDescriptionListSize = processDescriptionList.size();
                for ( int i = 0; i < processDescriptionListSize; i++ ) {
                    ProcessDescription processDescription = processDescriptionList.get( i );
                    appendProcessDescription( root, processDescription );
                }
            }
        } catch ( SAXException sex ) {
            LOG.logError( sex.getMessage(), sex );
        }
        LOG.exiting();
        return processDescriptionsDocument;
    }

    /**
     * Appends the DOM representation of the <code>ProcessDescription</code>
     * instance to the passed <code>Element</code>..
     * 
     * @param root
     * @param processDescription
     */
    private static void appendProcessDescription( Element root,
                                                 ProcessDescription processDescription ) {
        LOG.entering();

        // Add a <wps:ProcessDescription> node to the <wps:ProcessDescriptions>
        // node
        Element processDescriptionNode = XMLTools.appendElement( root, WPSNS,
                                                                 "wps:ProcessDescription", null );

        String statusSupported = Boolean.toString( processDescription.isStatusSupported() );
        processDescriptionNode.setAttribute( "statusSupported", statusSupported );

        String storeSupported = Boolean.toString( processDescription.isStoreSupported() );
        processDescriptionNode.setAttribute( "storeSupported", storeSupported );

        String processVersion = processDescription.getProcessVersion();
        processDescriptionNode.setAttribute( "processVersion", processVersion );

        Code identifier = processDescription.getIdentifier();
        if ( null != identifier ) {
            appendIdentifier( processDescriptionNode, identifier );
        } else {
            LOG.logError( "Identifier is null." );
        }

        String title = processDescription.getTitle();
        if ( null != title ) {
            appendTitle( processDescriptionNode, title );
        } else {
            LOG.logError( "title is null." );
        }

        String _abstract = processDescription.getAbstract();
        if ( null != _abstract ) {
            appendAbstract( processDescriptionNode, _abstract );
        }

        Element metaDataTypeNode = null;

        MetadataType metadataType = null;
        List<MetadataType> metaDataTypeList = processDescription.getMetadata();
        int metaDataTypeListSize = metaDataTypeList.size();
        for ( int i = 0; i < metaDataTypeListSize; i++ ) {
            metadataType = metaDataTypeList.get( i );
            metaDataTypeNode = XMLTools.appendElement( processDescriptionNode, OWSNS,
                                                       "ows:Metadata" );
            metaDataTypeNode.setAttributeNS( XLNNS.toString(), "xlink:title", metadataType.value );
        }

        DataInputs dataInputs = processDescription.getDataInputs();
        appendDataInputs( processDescriptionNode, dataInputs );

        ProcessOutputs processOutputs = processDescription.getProcessOutputs();
        appendProcessOutputs( processDescriptionNode, processOutputs );

        LOG.exiting();
    }

    /**
     * 
     * @param e
     * @param _abstract
     */
    private static void appendAbstract( Element e, String _abstract ) {
        if ( !"".equals( _abstract ) ) {
            XMLTools.appendElement( e, OWSNS, "ows:Abstract", _abstract );
        } else {
            LOG.logError( "abstract is empty." );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param processDescriptionNode
     * @param dataInputs
     */
    private static void appendDataInputs( Element processDescriptionNode, DataInputs dataInputs ) {
        LOG.entering();
        // Add a <wps:DataInputs> node to the <wps:ProcessDescription> node
        Element dataInputsNode = XMLTools.appendElement( processDescriptionNode, WPSNS,
                                                         "wps:DataInputs", null );
        if ( null != dataInputs ) {
            List<InputDescription> dataInputsList = dataInputs.getInputDescriptions();
            InputDescription inputDescription = null;
            int dataInputListSize = dataInputsList.size();
            for ( int i = 0; i < dataInputListSize; i++ ) {
                inputDescription = dataInputsList.get( i );
                appendDataInput( dataInputsNode, inputDescription );
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param dataInputsNode
     * @param inputDescription
     */
    private static void appendDataInput( Element dataInputsNode, InputDescription inputDescription ) {
        LOG.entering();
        // Add a <wps:DataInput> node to the <wps:DataInputs> node
        Element inputNode = XMLTools.appendElement( dataInputsNode, WPSNS, "wps:Input", null );

        if ( null != inputNode ) {

            Code identifier = inputDescription.getIdentifier();
            if ( null != identifier ) {
                appendIdentifier( inputNode, identifier );
            } else {
                LOG.logError( "identifier is null." );
            }

            String title = inputDescription.getTitle();
            if ( null != title ) {
                appendTitle( inputNode, title );
            } else {
                LOG.logError( "title is null." );
            }

            String _abstract = inputDescription.getAbstract();
            if ( null != _abstract ) {
                appendAbstract( inputNode, _abstract );
            }

            try {

                SupportedCRSs supportedCRSs = inputDescription.getBoundingBoxData();
                ComplexData complexData = inputDescription.getComplexData();
                LiteralInput literalInput = inputDescription.getLiteralData();

                if ( null != supportedCRSs ) {
                    appendBoundingBoxData( inputNode, supportedCRSs );
                } else if ( null != complexData ) {
                    appendComplexData( inputNode, complexData );
                } else if ( null != literalInput ) {
                    appendLiteralInput( inputNode, literalInput );
                } else {
                    LOG.logError( "a required datatype description is missing." );
                }

            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }

            XMLTools.appendElement( inputNode, WPSNS, "wps:MinimumOccurs",
                                    String.valueOf( inputDescription.getMinimumOccurs() ) );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param root
     * @param processOutputs
     */
    private static void appendProcessOutputs( Element root, ProcessOutputs processOutputs ) {
        LOG.entering();
        if ( null != processOutputs ) {
            // Add a <wps:ProcessOutputs> node to the <wps:ProcessDescription>
            // node
            Element processOutputsNode = XMLTools.appendElement( root, WPSNS, "wps:ProcessOutputs",
                                                                 null );
            OutputDescription outputDescription = null;
            List<OutputDescription> outputDescriptionList = processOutputs.getOutput();
            int outputDescriptionListSize = outputDescriptionList.size();
            for ( int i = 0; i < outputDescriptionListSize; i++ ) {
                outputDescription = outputDescriptionList.get( i );
                appendOutputDescription( processOutputsNode, outputDescription );
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param processOutputsNode
     * @param outputDescription
     */
    private static void appendOutputDescription( Element processOutputsNode,
                                                OutputDescription outputDescription ) {
        LOG.entering();
        if ( null != outputDescription ) {

            Element outputNode = XMLTools.appendElement( processOutputsNode, WPSNS, "wps:Output",
                                                         null );

            Code identifier = outputDescription.getIdentifier();
            if ( null != identifier ) {
                appendIdentifier( outputNode, identifier );
            } else {
                LOG.logError( "identifier is null." );
            }

            String title = outputDescription.getTitle();
            if ( null != title ) {
                appendTitle( outputNode, title );
            } else {
                LOG.logError( "title is null." );
            }

            String _abstract = outputDescription.getAbstract();
            if ( null != _abstract ) {
                appendAbstract( outputNode, _abstract );
            }

            SupportedCRSs supportedCRSs = outputDescription.getBoundingBoxOutput();
            ComplexData complexData = outputDescription.getComplexOutput();
            LiteralOutput literalOutput = outputDescription.getLiteralOutput();

            if ( null != supportedCRSs ) {
                appendBoundingBoxOutput( outputNode, supportedCRSs );
            } else if ( null != complexData ) {
                appendComplexOutput( outputNode, complexData );
            } else if ( null != literalOutput ) {
                appendLiteralOutput( outputNode, literalOutput );
            } else {
                LOG.logError( "a required output datatype description is missing." );
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param outputNode
     * @param complexData
     */
    private static void appendComplexOutput( Element outputNode, ComplexData complexData ) {

        LOG.entering();
        String defaultEncoding = complexData.getDefaultEncoding();
        String defaultFormat = complexData.getDefaultFormat();
        String defaultSchema = complexData.getDefaultSchema();
        Element complexDataNode = XMLTools.appendElement( outputNode, WPSNS, "wps:ComplexOutput",
                                                          null );

        if ( null != defaultFormat && !"".equals( defaultFormat ) ) {
            complexDataNode.setAttribute( "defaultFormat", defaultFormat );
        }
        if ( null != defaultEncoding && !"".equals( defaultEncoding ) ) {
            complexDataNode.setAttribute( "defaultEncoding", defaultEncoding );
        }
        if ( null != defaultSchema && !"".equals( defaultSchema ) ) {
            complexDataNode.setAttribute( "defaultSchema", defaultSchema );
        }

        List<SupportedComplexData> supportedComplexDataList = complexData.getSupportedComplexData();
        int supporteComplexDataListSize = supportedComplexDataList.size();
        for ( int i = 0; i < supporteComplexDataListSize; i++ ) {
            appendSupportedComplexData( complexDataNode, supportedComplexDataList.get( i ) );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param outputNode
     * @param supportedCRSs
     */
    private static void appendBoundingBoxOutput( Element outputNode, SupportedCRSs supportedCRSs ) {
        LOG.entering();
        URI defaultCrs = supportedCRSs.getDefaultCRS();
        Element defaultCRSs = XMLTools.appendElement( outputNode, WPSNS, "wps:BoundingBoxOutput",
                                                      null );
        defaultCRSs.setAttribute( "defaultCRS", defaultCrs.toString() );

        List<URI> crsList = supportedCRSs.getCRS();
        int crsListSize = crsList.size();
        for ( int i = 0; i < crsListSize; i++ ) {
            URI uri = crsList.get( i );
            XMLTools.appendElement( defaultCRSs, WPSNS, "wps:CRS", uri.toString() );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param outputNode
     * @param literalOutput
     */
    private static void appendLiteralOutput( Element outputNode, LiteralOutput literalOutput ) {
        LOG.entering();
        Element literalOutputNode = XMLTools.appendElement( outputNode, WPSNS, "wps:LiteralOutput",
                                                            null );
        Element owsDataType = XMLTools.appendElement( literalOutputNode, OWSNS, "ows:DataType",
                                                      literalOutput.getDataType().getName() );
        owsDataType.setAttribute( "ows:reference",
                                  literalOutput.getDataType().getLink().getHref().toString() );
        Element supportedUOMsNode = XMLTools.appendElement( literalOutputNode, WPSNS,
                                                            "wps:SupportedUOMs", null );

        supportedUOMsNode.setAttribute(
                                        "defaultUOM",
                                        literalOutput.getSupportedUOMs().getDefaultUOM().getLink().getHref().toString() );

        List<OWSMetadata> supportedUOMs = literalOutput.getSupportedUOMs().getUOM();
        int size = supportedUOMs.size();
        for ( int i = 0; i < size; i++ ) {
            OWSMetadata uom = supportedUOMs.get( i );
            Element uomNode = XMLTools.appendElement( supportedUOMsNode, OWSNS, "ows:UOM", null );
            uomNode.setAttribute( "ows:reference", uom.getLink().getHref().toString() );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param inputNode
     * @param complexData
     */
    private static void appendComplexData( Element inputNode, ComplexData complexData ) {
        LOG.entering();
        String defaultEncoding = complexData.getDefaultEncoding();
        String defaultFormat = complexData.getDefaultFormat();
        String defaultSchema = complexData.getDefaultSchema();
        Element complexDataNode = XMLTools.appendElement( inputNode, WPSNS, "wps:ComplexData", null );

        if ( null != defaultFormat && !"".equals( defaultFormat ) ) {
            complexDataNode.setAttribute( "defaultFormat", defaultFormat );
        }
        if ( null != defaultEncoding && !"".equals( defaultEncoding ) ) {
            complexDataNode.setAttribute( "defaultEncoding", defaultEncoding );
        }
        if ( null != defaultSchema && !"".equals( defaultSchema ) ) {
            complexDataNode.setAttribute( "defaultSchema", defaultSchema );
        }

        List<SupportedComplexData> supportedComplexDataList = complexData.getSupportedComplexData();
        int supporteComplexDataListSize = supportedComplexDataList.size();
        for ( int i = 0; i < supporteComplexDataListSize; i++ ) {
            appendSupportedComplexData( complexDataNode, supportedComplexDataList.get( i ) );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param complexDataNode
     * @param supportedComplexData
     */
    private static void appendSupportedComplexData( Element complexDataNode,
                                                   SupportedComplexData supportedComplexData ) {
        LOG.entering();

        Element supportedComplexDataNode = XMLTools.appendElement( complexDataNode, WPSNS,
                                                                   "wps:SupportedComplexData", null );

        XMLTools.appendElement( supportedComplexDataNode, WPSNS, "wps:Format",
                                supportedComplexData.getFormat() );

        XMLTools.appendElement( supportedComplexDataNode, WPSNS, "wps:Encoding",
                                supportedComplexData.getEncoding() );

        XMLTools.appendElement( supportedComplexDataNode, WPSNS, "wps:Schema",
                                supportedComplexData.getSchema() );

        LOG.exiting();
    }

    /**
     * 
     * @param inputNode
     * @param literalInput
     */
    private static void appendLiteralInput( Element inputNode, LiteralInput literalInput ) {
        LOG.entering();
        Element literalDataNode = XMLTools.appendElement( inputNode, WPSNS, "wps:LiteralData", null );
        Element owsDataType = XMLTools.appendElement( literalDataNode, OWSNS, "ows:DataType",
                                                      literalInput.getDataType().getName() );
        owsDataType.setAttribute( "ows:reference",
                                  literalInput.getDataType().getLink().getHref().toString() );
        appendLiteralInputTypes( literalDataNode, literalInput );

        LOG.exiting();
    }

    /**
     * 
     * @param literalDataNode
     * @param literalInput
     */
    private static void appendLiteralInputTypes( Element literalDataNode, LiteralInput literalInput ) {
        LOG.entering();
        Element supportedUOMsNode = XMLTools.appendElement( literalDataNode, WPSNS,
                                                            "wps:SupportedUOMs", null );

        SupportedUOMs supportedUOMs = literalInput.getSupportedUOMs();
        if ( null != supportedUOMs ) {

            OWSMetadata defaultUOM = literalInput.getSupportedUOMs().getDefaultUOM();
            if ( null != defaultUOM ) {
                supportedUOMsNode.setAttribute(
                                                "defaultUOM",
                                                literalInput.getSupportedUOMs().getDefaultUOM().getLink().getHref().toString() );
            }

            List<OWSMetadata> supportedUOMsList = literalInput.getSupportedUOMs().getUOM();
            int size = supportedUOMsList.size();
            for ( int i = 0; i < size; i++ ) {
                OWSMetadata uom = supportedUOMsList.get( i );
                Element uomNode = XMLTools.appendElement( supportedUOMsNode, OWSNS, "ows:UOM", null );
                uomNode.setAttribute( "ows:reference", uom.getLink().getHref().toString() );
            }
        }

        // append <ows:AllowedValues> on <LiteralData>
        OWSAllowedValues owsAllowedValues = literalInput.getAllowedValues();
        if ( null != owsAllowedValues ) {
            Element allowedValuesNode = XMLTools.appendElement( literalDataNode, OWSNS,
                                                                "ows:AllowedValues", null );
            TypedLiteral[] typedLiteralArray = owsAllowedValues.getOwsValues();
            if ( null != typedLiteralArray ) {
                for ( int i = 0; i < typedLiteralArray.length; i++ ) {
                    // append <ows:Value/> on <ows:AllowedValues>
                    XMLTools.appendElement( allowedValuesNode, OWSNS, "ows:Value",
                                            typedLiteralArray[i].getValue() );
                }
            }
            // append <ows:Range> on <ows:AllowedValues>
            ValueRange[] valueRangeArray = owsAllowedValues.getValueRanges();
            if ( null != valueRangeArray ) {
                for ( int i = 0; i < valueRangeArray.length; i++ ) {
                    Element owsRangeNode = XMLTools.appendElement( allowedValuesNode, OWSNS,
                                                                   "ows:Range" );
                    String closure = valueRangeArray[i].getClosure().value;
                    if ( null != closure ) {
                        owsRangeNode.setAttribute( "ows:rangeClosure", closure );
                    }
                    String minimumValue = valueRangeArray[i].getMin().getValue();
                    if ( null != minimumValue ) {
                        XMLTools.appendElement( owsRangeNode, OWSNS, "ows:MinimumValue",
                                                minimumValue );
                    }
                    String maximumValue = valueRangeArray[i].getMax().getValue();
                    if ( null != maximumValue ) {
                        XMLTools.appendElement( owsRangeNode, OWSNS, "ows:MaximumValue",
                                                maximumValue );
                    }
                    TypedLiteral typedLiteralSpacing = valueRangeArray[i].getSpacing();
                    if ( null != typedLiteralSpacing ) {
                        String spacing = typedLiteralSpacing.getValue();
                        if ( null != spacing ) {
                            XMLTools.appendElement( owsRangeNode, OWSNS, "ows:Spacing", spacing );
                        }
                    }

                }
            }
        }

        // append <ows:AnyValue> on <LiteralData>
        boolean anyValueAllowed = literalInput.getAnyValue();
        if ( false != anyValueAllowed ) {
            XMLTools.appendElement( literalDataNode, OWSNS, "ows:AnyValue" );
        }

        // append <ows:ValuesReference> on <LiteralData>
        OWSMetadata owsValuesReference = literalInput.getValuesReference();
        if ( null != owsValuesReference ) {
            Element valuesReference = XMLTools.appendElement( literalDataNode, OWSNS,
                                                              "ows:ValuesReference",
                                                              owsValuesReference.getName() );
            String reference = owsValuesReference.getLink().getHref().toString();
            if ( null != reference && !"".equals( reference ) ) {
                valuesReference.setAttribute( "ows:reference", reference );
            }
        }

        // append <ows:DefaultValue> on <LiteralData>
        ValueRange defaultValue = literalInput.getDefaultValue();
        if ( null != defaultValue ) {
            Element owsRangeNode = XMLTools.appendElement( literalDataNode, OWSNS,
                                                           "ows:DefaultValue" );
            String closure = defaultValue.getClosure().value;
            if ( null != closure ) {
                owsRangeNode.setAttribute( "ows:rangeClosure", closure );
            }
            String minimumValue = defaultValue.getMin().getValue();
            if ( null != minimumValue ) {
                XMLTools.appendElement( owsRangeNode, OWSNS, "ows:MinimumValue", minimumValue );
            }
            String maximumValue = defaultValue.getMax().getValue();
            if ( null != maximumValue ) {
                XMLTools.appendElement( owsRangeNode, OWSNS, "ows:MaximumValue", maximumValue );
            }
            String spacing = defaultValue.getSpacing().getValue();
            if ( null != spacing ) {
                XMLTools.appendElement( owsRangeNode, OWSNS, "ows:Spacing", spacing );
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param inputNode
     * @param supportedCRSs
     */
    private static void appendBoundingBoxData( Element inputNode, SupportedCRSs supportedCRSs ) {
        LOG.entering();
        URI defaultCrs = supportedCRSs.getDefaultCRS();
        Element defaultCRSs = XMLTools.appendElement( inputNode, WPSNS, "wps:BoundingBoxData", null );
        defaultCRSs.setAttribute( "defaultCRS", defaultCrs.toString() );

        List<URI> crsList = supportedCRSs.getCRS();
        int crsListSize = crsList.size();
        for ( int i = 0; i < crsListSize; i++ ) {
            URI uri = crsList.get( i );
            XMLTools.appendElement( defaultCRSs, WPSNS, "wps:CRS", uri.toString() );

        }
        LOG.exiting();
    }

    /**
     * 
     * @param executeResponse
     * @return
     */
    public static ExecuteResponseDocument export( ExecuteResponse executeResponse ) {
        LOG.entering();
        ExecuteResponseDocument executeResponseDocument = new ExecuteResponseDocument();

        try {
            // Prepare empty ExecuteResponseDocument
            executeResponseDocument.createEmptyDocument();

            // Get root of execute
            Element root = executeResponseDocument.getRootElement();

            // Append optional statusLocation attribute
            String statusLocation = executeResponse.getStatusLocation();
            if ( null != statusLocation ) {
                root.setAttribute( "statusLocation", statusLocation );
            }

            // Append mandatory <ows:Identifier> node
            Code identifier = executeResponse.getIdentifier();
            if ( null != identifier ) {
                appendIdentifier( root, identifier );
            } else {
                LOG.logError( "identifier is null." );
            }

            // Append mandatory <Status> node
            appendStatus( root, executeResponse.getStatus() );

            // Append optional <DataInputs> node
            ExecuteDataInputs executeDataInputs = executeResponse.getDataInputs();
            if ( null != executeDataInputs ) {
                appendExecuteDataInputs( root, executeResponse.getDataInputs() );
            }

            // Append optional <OutputDefinitions> node
            OutputDefinitions outputDefinitions = executeResponse.getOutputDefinitions();
            if ( null != outputDefinitions ) {
                appendOutputDefinitions( root, outputDefinitions );
            }

            // Append optional <ProcessOutputs> node
            ExecuteResponse.ProcessOutputs processOutputs = executeResponse.getProcessOutputs();
            if ( null != processOutputs ) {
                appendExecuteProcessOutputs( root, processOutputs );
            }

        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        }
        LOG.exiting();
        return executeResponseDocument;
    }

    /**
     * 
     * @param root
     * @param status
     */
    private static void appendStatus( Element root, Status status ) {

        LOG.entering();

        Element statusNode = XMLTools.appendElement( root, WPSNS, "Status" );

        String processSucceeded = status.getProcessSucceeded();

        ProcessFailed processFailed = status.getProcessFailed();

        String processAccepted = status.getProcessAccepted();

        ProcessStarted processStarted = status.getProcessStarted();

        if ( null != processSucceeded ) {
            if ( "".equals( processSucceeded ) ) {
                XMLTools.appendElement( statusNode, WPSNS, "ProcessSucceeded" );
            } else {
                XMLTools.appendElement( statusNode, WPSNS, "ProcessSucceeded", processSucceeded );
            }
        }

        else if ( null != processFailed ) {

            Element processfailedNode = XMLTools.appendElement( statusNode, WPSNS, "ProcessFailed" );

            ExceptionReport exceptionReport = processFailed.getExceptionReport();
            if ( null != exceptionReport ) {
                Element execeptionReportNode = XMLTools.appendElement( processfailedNode, OWSNS,
                                                                       "ows:ExceptionReport" );
                String version = exceptionReport.getVersion();
                if ( null != version && !"".equals( version ) ) {
                    execeptionReportNode.setAttribute( "version", version );
                }
                OGCWebServiceException[] ogcWebserviceExceptions = exceptionReport.getExceptions();
                int size = ogcWebserviceExceptions.length;
                if ( 0 < size ) {
                    for ( int i = 0; i < size; i++ ) {
                        appendException( execeptionReportNode, ogcWebserviceExceptions[i] );
                    }
                }
            }
        }

        else if ( null != processAccepted ) {

            if ( "".equals( processAccepted ) ) {
                XMLTools.appendElement( statusNode, WPSNS, "ProcessAccepted" );
            } else {
                XMLTools.appendElement( statusNode, WPSNS, "ProcessAccepted", processAccepted );
            }
        }

        else if ( null != processStarted ) {

            Element processStartedNode = null;
            String processStartedMessage = processStarted.getValue();
            if ( "".equals( processStartedMessage ) ) {
                processStartedNode = XMLTools.appendElement( statusNode, WPSNS, "ProcessStarted" );
            } else {
                processStartedNode = XMLTools.appendElement( statusNode, WPSNS, "ProcessStarted",
                                                             processStartedMessage );
            }
            int percentCompleted = processStarted.getPercentCompleted();
            if ( 0 >= percentCompleted && percentCompleted <= 100 ) {
                processStartedNode.setAttribute( "PercentCompleted",
                                                 String.valueOf( percentCompleted ) );
            }
        }
        LOG.exiting();
    }

    /**
     * appends a xml representation of an <tt>OGCWebServiceException</tt> to
     * the passed <tt>Element</tt> Overriding method of superclass because the
     * nodes appended from that method do not conform with current ows
     * specification
     * 
     * @param node
     * @param ex
     */
    protected static void appendException( Element node, OGCWebServiceException ex ) {

        LOG.entering();

        Element exceptionNode = XMLTools.appendElement( node, OWSNS, "ows:Exception" );

        if ( null != ex.getCode() ) {
            exceptionNode.setAttribute( "exceptionCode", ex.getCode().value );
        }
        String locator = ex.getLocator();
        try {
            if ( null != locator ) {
                locator = URLEncoder.encode( locator, CharsetUtils.getSystemCharset() );
            } else {
                locator = "-";
            }
        } catch ( UnsupportedEncodingException e ) {
        }
        exceptionNode.setAttribute( "locator", locator );
        LOG.exiting();
    }

    /**
     * 
     * @param root
     * @param executeDataInputs
     */
    private static void appendExecuteDataInputs( Element root, ExecuteDataInputs executeDataInputs ) {

        LOG.entering();

        Map<String, IOValue> inputs = executeDataInputs.getInputs();

        if ( null != inputs ) {

            int size = inputs.size();

            if ( 0 < size ) {

                Iterator it = inputs.keySet().iterator();

                Element dataInputsNode = XMLTools.appendElement( root, WPSNS, "DataInputs" );

                while ( it.hasNext() ) {

                    IOValue ioValue = inputs.get( it.next() );

                    appendInput( dataInputsNode, ioValue );
                }
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param dataInputsNode
     * @param ioValue
     */
    private static void appendInput( Element dataInputsNode, IOValue ioValue ) {

        LOG.entering();

        Element inputNode = XMLTools.appendElement( dataInputsNode, WPSNS, "Input" );

        Code identifier = ioValue.getIdentifier();
        if ( null != identifier ) {
            appendIdentifier( inputNode, identifier );
        } else {
            LOG.logError( "identifier is null." );
        }

        String title = ioValue.getTitle();
        if ( null != title ) {
            appendTitle( inputNode, title );
        } else {
            LOG.logError( "title is null." );
        }

        String _abstract = ioValue.getAbstract();
        if ( null != _abstract ) {
            appendAbstract( inputNode, _abstract );
        }

        ComplexValue complexValue = ioValue.getComplexValue();
        ComplexValueReference complexValueReference = ioValue.getComplexValueReference();
        TypedLiteral literalValue = ioValue.getLiteralValue();
        Envelope boundingBoxValue = ioValue.getBoundingBoxValue();

        if ( null != complexValue ) {
            appendComplexValue( inputNode, complexValue );
        } else if ( null != complexValueReference ) {
            appendComplexValueReference( inputNode, complexValueReference );
        } else if ( null != literalValue ) {
            appendLiteralValue( inputNode, literalValue );
        } else if ( null != boundingBoxValue ) {
            appendBoundingBoxValue( inputNode, boundingBoxValue );
        } else {
            LOG.logError( "a required input element is missing." );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param e
     * @param complexValue
     */
    private static void appendComplexValue( Element e, ComplexValue complexValue ) {
        LOG.entering();
        Element complexValueNode = XMLTools.appendElement( e, WPSNS, "ComplexValue" );

        String format = complexValue.getFormat();
        if ( null != format && !"".equals( format ) ) {
            complexValueNode.setAttribute( "format", format );
        }

        URI encoding = complexValue.getEncoding();
        if ( null != encoding ) {
            complexValueNode.setAttribute( "encoding", encoding.toString() );
        }

        URL schema = complexValue.getSchema();
        if ( null != schema ) {
            complexValueNode.setAttribute( "schema", schema.toString() );
        }

        Object content = complexValue.getContent();

        if ( content instanceof FeatureCollection ) {

            // TODO weird hack! are there better ways to append a
            // featurecollection to an element?
            FeatureCollection fc = (FeatureCollection) content;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                new GMLFeatureAdapter().export( fc, bos );
            } catch ( IOException e1 ) {
                LOG.logError( "error exporting featurecollection." );
            } catch ( FeatureException e1 ) {
                LOG.logError( "error exporting featurecollection." );
            }

            String gmlString = null;
            try {
                gmlString = bos.toString( "UTF-8" );
            } catch ( UnsupportedEncodingException e1 ) {
            }

            XMLFragment xmlFragment = null;
            try {
                xmlFragment = new XMLFragment( new StringReader( gmlString ), "http://fluggs-che" );
            } catch ( SAXException e1 ) {
                LOG.logError( "error converting featurecollection to xmlfragment." );
            } catch ( IOException e1 ) {
                LOG.logError( "error converting featurecollection to xmlfragment." );
            }

            Node fcElement = xmlFragment.getRootElement();

            Document owner = complexValueNode.getOwnerDocument();

            complexValueNode.appendChild( owner.importNode( fcElement, true ) );

        } else {
            // TODO implement output methods for complex value types other than
            // featurecollection
        }

        LOG.exiting();
    }

    /**
     * 
     * @param inputNode
     * @param complexValueReference
     */
    private static void appendComplexValueReference( Element inputNode,
                                                    ComplexValueReference complexValueReference ) {
        LOG.entering();

        Element complexValueReferenceNode = XMLTools.appendElement( inputNode, WPSNS,
                                                                    "ComplexValueReference" );

        String format = complexValueReference.getFormat();
        if ( null != format && !"".equals( format ) ) {
            complexValueReferenceNode.setAttribute( "format", format );
        }

        URI encoding = complexValueReference.getEncoding();
        if ( null != encoding ) {
            complexValueReferenceNode.setAttribute( "encoding", encoding.toString() );
        }

        URL schema = complexValueReference.getSchema();
        if ( null != schema ) {
            complexValueReferenceNode.setAttribute( "schema", schema.toString() );
        }

        URL reference = complexValueReference.getReference();
        if ( null != reference ) {
            complexValueReferenceNode.setAttributeNS( OWSNS.toString(), "ows:reference",
                                                      reference.toString() );
        }

        LOG.exiting();
    }

    /**
     * 
     * @param inputNode
     * @param literalValue
     */
    private static void appendLiteralValue( Element inputNode, TypedLiteral literalValue ) {

        LOG.entering();

        Element literalValueNode = XMLTools.appendElement( inputNode, WPSNS, "LiteralValue" );

        URI dataType = literalValue.getType();
        if ( null != dataType ) {
            literalValueNode.setAttribute( "dataType", dataType.toString() );
        }

        URI uom = literalValue.getUom();
        if ( null != uom ) {
            literalValueNode.setAttribute( "uom", uom.toString() );
        }

        String value = literalValue.getValue();
        if ( null != value && !"".equals( value ) ) {
            literalValueNode.setTextContent( value );
        }

        LOG.exiting();
    }

    /**
     * 
     * @param inputNode
     * @param boundingBoxValue
     */
    private static void appendBoundingBoxValue( Element inputNode, Envelope boundingBoxValue ) {

        LOG.entering();

        Element boundingBoxValueNode = XMLTools.appendElement( inputNode, WPSNS, "BoundingBoxValue" );

        String crs = crs = boundingBoxValue.getCoordinateSystem().getName();
        if ( null != crs ) {
            boundingBoxValueNode.setAttribute( "crs", crs );
        }

        int dimensions = boundingBoxValue.getCoordinateSystem().getDimension();
        if ( 0 != dimensions ) {
            boundingBoxValueNode.setAttribute( "dimensions", String.valueOf( dimensions ) );
        }

        Position positionMin = boundingBoxValue.getMin();
        if ( null != positionMin ) {
            XMLTools.appendElement( boundingBoxValueNode, OWSNS, "ows:LowerCorner",
                                    String.valueOf( positionMin.getX() ) + " "
                                                            + String.valueOf( positionMin.getY() ) );
        }

        Position positionMax = boundingBoxValue.getMax();
        if ( null != positionMax ) {
            XMLTools.appendElement( boundingBoxValueNode, OWSNS, "ows:UpperCorner",
                                    String.valueOf( positionMax.getX() ) + " "
                                                            + String.valueOf( positionMax.getY() ) );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param root
     * @param outputDefintions
     */
    private static void appendOutputDefinitions( Element root, OutputDefinitions outputDefintions ) {

        LOG.entering();

        Element outputDefinitionsNode = XMLTools.appendElement( root, WPSNS, "OutputDefinitions" );

        List<OutputDefinition> outputDefinitionsList = outputDefintions.getOutputDefinitions();

        if ( null != outputDefinitionsList ) {

            int size = outputDefinitionsList.size();

            if ( 0 < size ) {
                for ( int i = 0; i < outputDefinitionsList.size(); i++ ) {

                    appendOutputDefinition( outputDefinitionsNode, outputDefinitionsList.get( i ) );

                }
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param outputDefinitionsNode
     * @param outputDefinition
     */
    private static void appendOutputDefinition( Element outputDefinitionsNode,
                                               OutputDefinition outputDefinition ) {

        LOG.entering();

        Element outputNode = XMLTools.appendElement( outputDefinitionsNode, WPSNS, "Output" );

        String format = outputDefinition.getFormat();
        if ( null != format && !"".equals( format ) ) {
            outputNode.setAttribute( "format", format );
        }

        URI encoding = outputDefinition.getEncoding();
        if ( null != encoding ) {
            outputNode.setAttribute( "encoding", encoding.toString() );
        }

        URL schema = outputDefinition.getSchema();
        if ( null != schema ) {
            outputNode.setAttribute( "schema", schema.toString() );
        }

        URI uom = outputDefinition.getUom();
        if ( null != uom ) {
            outputNode.setAttribute( "uom", uom.toString() );
        }

        Code identifier = outputDefinition.getIdentifier();
        if ( null != identifier ) {
            appendIdentifier( outputNode, identifier );
        } else {
            LOG.logError( "identifier is null." );
        }

        String title = outputDefinition.getTitle();
        if ( null != title ) {
            appendTitle( outputNode, title );
        } else {
            LOG.logError( "title is null." );
        }

        String _abstract = outputDefinition.getAbstract();
        if ( null != _abstract ) {
            appendAbstract( outputNode, _abstract );
        }
        LOG.exiting();
    }

    /**
     * 
     * @param root
     * @param processOutputs
     */
    private static void appendExecuteProcessOutputs( Element root,
                                                    ExecuteResponse.ProcessOutputs processOutputs ) {

        LOG.entering();

        Element processOutputsNode = XMLTools.appendElement( root, WPSNS, "ProcessOutputs" );

        List<IOValue> processOutputsList = processOutputs.getOutputs();
        if ( null != processOutputsList ) {

            int size = processOutputsList.size();
            if ( 0 < size ) {

                for ( int i = 0; i < size; i++ ) {
                    appendExecuteProcessOutput( processOutputsNode, processOutputsList.get( i ) );
                }
            }
        }
        LOG.exiting();
    }

    /**
     * 
     * @param root
     * @param processOutputs
     */
    private static void appendExecuteProcessOutput( Element processOutputsNode,
                                                   IOValue processOutput ) {

        LOG.entering();

        Element outputNode = XMLTools.appendElement( processOutputsNode, WPSNS, "Output" );

        Code identifier = processOutput.getIdentifier();
        if ( null != identifier ) {
            appendIdentifier( outputNode, identifier );
        } else {
            LOG.logError( "identifier is null." );
        }

        String title = processOutput.getTitle();
        if ( null != title ) {
            appendTitle( outputNode, title );
        } else {
            LOG.logError( "title is null." );
        }

        String _abstract = processOutput.getAbstract();
        if ( null != _abstract ) {
            appendAbstract( outputNode, _abstract );
        }

        ComplexValue complexValue = processOutput.getComplexValue();
        ComplexValueReference complexValueReference = processOutput.getComplexValueReference();
        TypedLiteral literalValue = processOutput.getLiteralValue();
        Envelope boundingBoxValue = processOutput.getBoundingBoxValue();

        if ( null != complexValue ) {
            appendComplexValue( outputNode, complexValue );
        } else if ( null != complexValueReference ) {
            appendComplexValueReference( outputNode, complexValueReference );
        } else if ( null != literalValue ) {
            appendLiteralValue( outputNode, literalValue );
        } else if ( null != boundingBoxValue ) {
            appendBoundingBoxValue( outputNode, boundingBoxValue );
        } else {
            LOG.logError( "a required output element is missing." );
        }
        LOG.exiting();
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XMLFactory.java,v $
Revision 1.10  2006/10/17 20:31:20  poth
*** empty log message ***

Revision 1.9  2006/08/24 06:42:16  poth
File header corrected

Revision 1.8  2006/05/25 14:47:58  poth
LiteralValue substituted by TypedLiteral


********************************************************************** */