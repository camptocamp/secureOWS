//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/XMLFactory.java,v 1.27 2006/10/26 13:41:04 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.datastore.FeatureId;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureException;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Function;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wfs.capabilities.FeatureTypeList;
import org.deegree.ogcwebservices.wfs.capabilities.FormatType;
import org.deegree.ogcwebservices.wfs.capabilities.GMLObject;
import org.deegree.ogcwebservices.wfs.capabilities.Operation;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;
import org.deegree.ogcwebservices.wfs.operation.transaction.Delete;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;
import org.deegree.ogcwebservices.wfs.operation.transaction.InsertResults;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionResponse;
import org.deegree.ogcwebservices.wfs.operation.transaction.Update;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Responsible for the generation of XML representations of objects from the WFS context.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.27 $, $Date: 2006/10/26 13:41:04 $
 */
public class XMLFactory extends org.deegree.owscommon.XMLFactory {

    private static final URI WFS = CommonNamespaces.WFSNS;

    private static final URI OWS = CommonNamespaces.OWSNS;

    private static final URI OGCNS = CommonNamespaces.OGCNS;

    private static final URI DEEGREEWFS = CommonNamespaces.DEEGREEWFS;

    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    /**
     * Exports a <code>WFSCapabilities</code> instance to a <code>WFSCapabilitiesDocument</code>.
     * 
     * @param capabilities
     * @return DOM representation of the <code>WFSCapabilities</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static WFSCapabilitiesDocument export( WFSCapabilities capabilities )
                            throws IOException {
        LOG.entering();
        WFSCapabilitiesDocument capabilitiesDocument = new WFSCapabilitiesDocument();

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
            FeatureTypeList featureTypeList = capabilities.getFeatureTypeList();
            if ( featureTypeList != null ) {
                appendFeatureTypeList( root, featureTypeList );
            }
            GMLObject[] servesGMLObjectTypes = capabilities.getServesGMLObjectTypeList();
            if ( servesGMLObjectTypes != null ) {
                appendGMLObjectTypeList( root, WFS, "wfs:ServesGMLObjectTypeList",
                                         servesGMLObjectTypes );
            }
            GMLObject[] supportsGMLObjectTypes = capabilities.getSupportsGMLObjectTypeList();
            if ( supportsGMLObjectTypes != null ) {
                appendGMLObjectTypeList( root, WFS, "wfs:SupportsGMLObjectTypeList",
                                         supportsGMLObjectTypes );
            }
            Contents contents = capabilities.getContents();
            if ( contents != null ) {
                // appendContents(root, contents);
            }

            FilterCapabilities filterCapabilities = capabilities.getFilterCapabilities();
            if ( filterCapabilities != null ) {
                org.deegree.model.filterencoding.XMLFactory.appendFilterCapabilities110( root,
                                                                                         filterCapabilities );
            }
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
        }
        LOG.exiting();
        return capabilitiesDocument;
    }

    /**
     * Exports a <code>WFSCapabilities</code> instance to a <code>WFSCapabilitiesDocument</code>.
     * 
     * @param capabilities
     * @param sections
     *             names of sections to be exported, may contain 'All'
     * @return DOM representation of the <code>WFSCapabilities</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static WFSCapabilitiesDocument export( WFSCapabilities capabilities, String[] sections )
                            throws IOException {

        // TODO only export requested sections
        return export( capabilities );
    }

    /**
     * Appends the DOM representation of the {@link ServiceIdentification} section to the
     * passed {@link Element}.
     * 
     * @param root
     * @param serviceIdentification
     */
    protected static void appendServiceIdentification( Element root,
                                                      ServiceIdentification serviceIdentification ) {

        // 'ServiceIdentification'-element
        Element serviceIdentificationNode = XMLTools.appendElement( root, OWSNS,
                                                                    "ows:ServiceIdentification" );

        // 'ServiceType'-element
        XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:ServiceType",
                                serviceIdentification.getServiceType().getCode() );

        // 'ServiceTypeVersion'-elements
        String[] versions = serviceIdentification.getServiceTypeVersions();
        for ( int i = 0; i < versions.length; i++ ) {
            XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:ServiceTypeVersion",
                                    versions[i] );
        }

        // 'Fees'-element
        XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:Fees",
                                serviceIdentification.getFees() );

        // 'AccessConstraints'-element
        String[] constraints = serviceIdentification.getAccessConstraints();
        if ( constraints != null ) {
            for ( int i = 0; i < constraints.length; i++ ) {
                XMLTools.appendElement( serviceIdentificationNode, OWSNS, "ows:AccessConstraints",
                                        constraints[i] );
            }
        }
    }

    /**
     * Exports a <code>GetFeature</code> instance to a <code>GetFeatureDocument</code>.
     * 
     * @param getFeature request to be exported
     * @return XML representation of the <code>GetFeature</code> request
     * @throws IOException 
     * @throws XMLParsingException 
     */
    public static XMLFragment export( GetFeature getFeature )
                            throws IOException, XMLParsingException {
        LOG.entering();
        XMLFragment xml = new XMLFragment();
        try {
            xml.load( XMLFactory.class.getResource( "GetFeatureTemplate.xml" ) );
        } catch ( SAXException e ) {
            throw new XMLParsingException( "could not parse GetFeatureTemplate.xml", e );
        }
        Element root = xml.getRootElement();
        root.setAttribute( "outputFormat", getFeature.getOutputFormat() );
        root.setAttribute( "service", "WFS" );
        root.setAttribute( "version", getFeature.getVersion() );
        if ( getFeature.getHandle() != null ) {
            root.setAttribute( "handle", getFeature.getHandle() );
        }
        if ( getFeature.getResultType() == RESULT_TYPE.HITS ) {
            root.setAttribute( "resultType", "hits" );
        } else {
            root.setAttribute( "resultType", "results" );
        }
        root.setAttribute( "maxFeatures", "" + getFeature.getMaxFeatures() );
        if ( getFeature.getStartPosition() > 0 ) {
            root.setAttribute( "startPosition", "" + getFeature.getStartPosition() );
        }
        if ( getFeature.getTraverseXLinkDepth() > 0 ) {
            root.setAttribute( "traverseXLinkDepth", "" + getFeature.getTraverseXLinkDepth() );
        }
        if ( getFeature.getTraverseXLinkExpiry() > 0 ) {
            root.setAttribute( "traverseXLinkExpiry", "" + getFeature.getTraverseXLinkExpiry() );
        }
        Query[] queries = getFeature.getQuery();
        for ( int i = 0; i < queries.length; i++ ) {
            appendQuery( root, queries[i] );
        }
        LOG.exiting();
        return xml;
    }

    /**
     * Exports a <code>Transaction</code> instance to it's XML representation.
     * 
     * @param transaction transaction to export
     * @return XML representation of transaction
     * @throws IOException
     * @throws XMLParsingException
     */
    public static XMLFragment export( Transaction transaction )
                            throws IOException, XMLParsingException {

        XMLFragment xml = new XMLFragment();
        try {
            xml.load( TransactionResponse.class.getResource( "TransactionTemplate.xml" ) );
        } catch ( SAXException e ) {
            throw new IOException(
                                   "Internal error: Could not parse TransactionResponseTemplate.xml." );
        }
        Element root = xml.getRootElement();
        List ops = transaction.getOperations();
        for ( int i = 0; i < ops.size(); i++ ) {
            try {
                if ( ops.get( i ) instanceof Insert ) {
                    appendInsert( root, (Insert) ops.get( i ) );
                } else if ( ops.get( i ) instanceof Update ) {
                    appendUpdate( root, (Update) ops.get( i ) );
                } else if ( ops.get( i ) instanceof Delete ) {
                    appendDelete( root, (Delete) ops.get( i ) );
                }
            } catch ( XMLException e ) {
                LOG.logError( e.getMessage(), e );
                throw new XMLParsingException( e.getMessage() );
            } catch ( FeatureException e ) {
                LOG.logError( e.getMessage(), e );
                throw new XMLParsingException( e.getMessage() );
            } catch ( SAXException e ) {
                LOG.logError( e.getMessage(), e );
                throw new XMLParsingException( e.getMessage() );
            }
        }
        return xml;
    }

    /**
     * Adds the XML representation of a <code>Delete</code> operation to the given element.
     * 
     * @param root
     * @param delete
     */
    private static void appendDelete( Element root, Delete delete ) {
        Element el = XMLTools.appendElement( root, WFS, "Delete" );
        if ( delete.getHandle() != null ) {
            el.setAttribute( "handle", delete.getHandle() );
        }
        // TODO What about the namespace binding here?
        el.setAttribute( "typeName", delete.getTypeName().getAsString() );

        Filter filter = delete.getFilter();
        if ( filter != null ) {
            org.deegree.model.filterencoding.XMLFactory.appendFilter( el, filter );
        }
        root.appendChild( el );
    }

    /**
     * Adds the XML representation of an <code>Update</code> operation to the given element.
     * <p>
     * Respects the deegree-specific extension to the Update operation: instead of specifying
     * properties and their values, it's also possible to only specify just one feature that
     * replaces the matched feature. 
     * 
     * @param root
     * @param update
     * @throws SAXException 
     * @throws IOException 
     * @throws FeatureException 
     */
    private static void appendUpdate( Element root, Update update )
                            throws FeatureException, IOException, SAXException {

        Element el = XMLTools.appendElement( root, WFS, "Update" );
        if ( update.getHandle() != null ) {
            el.setAttribute( "handle", update.getHandle() );
        }
        // TODO What about the namespace binding here?
        el.setAttribute( "typeName", update.getTypeName().getAsString() );

        Feature replacement = update.getFeature();
        if ( replacement != null ) {
            GMLFeatureAdapter adapter = new GMLFeatureAdapter();
            adapter.append( root, replacement );
        } else {
            Map<PropertyPath, Node> replaces = update.getRawProperties();
            for ( PropertyPath propertyName : replaces.keySet() ) {
                Element propElement = XMLTools.appendElement( el, WFS, "Property" );
                Element nameElement = XMLTools.appendElement( propElement, WFS, "Name" );
                org.deegree.ogcbase.XMLFactory.appendPropertyPath( nameElement, propertyName );
                Element valueElement = XMLTools.appendElement( propElement, WFS, "Value" );
                Node value = replaces.get( propertyName );
                Node imported = valueElement.getOwnerDocument().importNode( value, true );
                valueElement.appendChild( imported );
            }
        }

        Filter filter = update.getFilter();
        if ( filter != null ) {
            org.deegree.model.filterencoding.XMLFactory.appendFilter( el, filter );
        }
        root.appendChild( el );
    }

    /**
     * Adds the XML representation of an <code>Insert</code> operation to the given element.
     * 
     * @param root
     * @param insert
     * @throws SAXException 
     * @throws IOException 
     * @throws FeatureException 
     */
    private static void appendInsert( Element root, Insert insert )
                            throws IOException, FeatureException, XMLException, SAXException {

        Element el = XMLTools.appendElement( root, WFS, "Insert" );
        if ( insert.getHandle() != null ) {
            el.setAttribute( "handle", insert.getHandle() );
        }
        if ( insert.getIdGen() != null ) {
            el.setAttribute( "idgen", insert.getIdGen().name() );
        }

        GMLFeatureAdapter adapter = new GMLFeatureAdapter();
        adapter.append( el, insert.getFeatures() );
    }

    /**
     * Exports an instance of TransactionResponse to its XML representation.
     * 
     * @param response TransactionResponse to export
     * @return XML representation of TransactionResponse
     * @throws IOException 
     */
    public static XMLFragment export( TransactionResponse response )
                            throws IOException {

        XMLFragment xml = new XMLFragment();
        try {
            xml.load( TransactionResponse.class.getResource( "TransactionResponseTemplate.xml" ) );
        } catch ( SAXException e ) {
            throw new IOException(
                                   "Internal error: Could not parse TransactionResponseTemplate.xml." );
        }
        Element root = xml.getRootElement();
        appendTransactionSummary( root, response.getTotalInserted(), response.getTotalUpdated(),
                                  response.getTotalDeleted() );
        appendInsertResults( root, response.getInsertResults() );

        return xml;
    }

    /**
     * Appends a 'wfs:TransactionSummary' element to the given element.
     * 
     * @param root
     * @param totalInserted
     * @param totalUpdated
     * @param totalDeleted
     */
    private static void appendTransactionSummary( Element root, int totalInserted,
                                                 int totalUpdated, int totalDeleted ) {
        Element taSummary = XMLTools.appendElement( root, WFS, "wfs:TransactionSummary" );
        XMLTools.appendElement( taSummary, WFS, "wfs:totalInserted", "" + totalInserted );
        XMLTools.appendElement( taSummary, WFS, "wfs:totalUpdated", "" + totalUpdated );
        XMLTools.appendElement( taSummary, WFS, "wfs:totalDeleted", "" + totalDeleted );
    }

    /**
     * Appends an 'wfs:InsertResults' element to the given element (only if necessary).
     * 
     * @param root
     * @param insertResults
     */
    private static void appendInsertResults( Element root, Collection<InsertResults> insertResults ) {
        if ( insertResults.size() > 0 ) {
            Element insertResultsElement = XMLTools.appendElement( root, WFS, "wfs:InsertResults" );
            Iterator<InsertResults> iter = insertResults.iterator();
            while ( iter.hasNext() ) {
                appendFeatureIds( insertResultsElement, iter.next() );
            }
        }
    }

    /**
     * Appends a 'wfs:Feature' element to the given element.
     * 
     * @param root
     * @param results
     */
    private static void appendFeatureIds( Element root, InsertResults results ) {
        Element featureElement = XMLTools.appendElement( root, WFS, "wfs:Feature" );
        String handle = results.getHandle();
        if ( handle != null ) {
            featureElement.setAttribute( "handle", handle );
        }
        Iterator<FeatureId> iter = results.getFeatureIDs().iterator();
        while ( iter.hasNext() ) {
            Element featureIdElement = XMLTools.appendElement( featureElement, OGCNS,
                                                               "ogc:FeatureId" );
            featureIdElement.setAttribute( "fid", iter.next().getAsString() );
        }
    }

    /**
     * Appends the XML representation of the given <code>Query</code> to an element.
     * 
     * @param query
     */
    private static void appendQuery( Element root, Query query )
                            throws IOException, XMLParsingException {

        Element queryElem = XMLTools.appendElement( root, WFS, "wfs:Query" );
        if ( query.getHandle() != null ) {
            queryElem.setAttribute( "handle", query.getHandle() );
        }
        if ( query.getFeatureVersion() != null ) {
            queryElem.setAttribute( "featureVersion", query.getFeatureVersion() );
        }
        QualifiedName[] qn = query.getTypeNames();
        String[] na = new String[qn.length];
        for ( int i = 0; i < na.length; i++ ) {
            na[i] = qn[i].getAsString();
            queryElem.setAttribute( "xmlns:" + qn[i].getPrefix(),
                                    qn[i].getNamespace().toASCIIString() );
        }
        String tn = StringTools.arrayToString( na, ',' );
        queryElem.setAttribute( "typeName", tn );

        PropertyPath[] propertyNames = query.getPropertyNames();
        for ( int i = 0; i < propertyNames.length; i++ ) {
            Element propertyNameElement = XMLTools.appendElement( queryElem, WFS,
                                                                  "wfs:PropertyName" );
            appendPropertyPath( propertyNameElement, propertyNames[i] );
        }
        Function[] fn = query.getFunctions();
        // copy function definitions into query node
        if ( fn != null ) {
            for ( int i = 0; i < fn.length; i++ ) {
                StringReader sr = new StringReader( fn[i].toXML().toString() );
                Document doc;
                try {
                    doc = XMLTools.parse( sr );
                } catch ( SAXException e ) {
                    throw new XMLParsingException( "could not parse filter function", e );
                }
                XMLTools.copyNode( doc.getDocumentElement(), queryElem );
            }
        }
        // copy filter into query node
        if ( query.getFilter() != null ) {
            StringReader sr = new StringReader( query.getFilter().toXML().toString() );
            Document doc;
            try {
                doc = XMLTools.parse( sr );
            } catch ( SAXException e ) {
                throw new XMLParsingException( "could not parse filter", e );
            }
            Element elem = XMLTools.appendElement( queryElem, OGCNS, "ogc:Filter" );
            XMLTools.copyNode( doc.getDocumentElement(), elem );
        }

        SortProperty[] sp = query.getSortProperties();
        if ( sp != null ) {
            Element sortBy = XMLTools.appendElement( queryElem, OGCNS, "ogc:SortBy" );
            for ( int i = 0; i < sp.length; i++ ) {
                Element sortProp = XMLTools.appendElement( sortBy, OGCNS, "ogc:SortProperty" );
                XMLTools.appendElement( sortProp, OGCNS, "ogc:PropertyName",
                                        sp[i].getSortProperty().getAsString() );
                if ( !sp[i].getSortOrder() ) {
                    XMLTools.appendElement( sortBy, OGCNS, "ogc:SortOrder", "DESC" );
                }
            }
        }
    }

    /**
     * Appends the XML representation of the <code>wfs:FeatureTypeList</code>- section to the
     * passed <code>Element</code>.
     * 
     * @param root
     * @param featureTypeList
     */
    public static void appendFeatureTypeList( Element root, FeatureTypeList featureTypeList ) {
        LOG.entering();
        Element featureTypeListNode = XMLTools.appendElement( root, WFS, "wfs:FeatureTypeList",
                                                              null );
        Operation[] operations = featureTypeList.getGlobalOperations();
        if ( operations != null ) {
            Element operationsNode = XMLTools.appendElement( featureTypeListNode, WFS,
                                                             "wfs:Operations" );
            for ( int i = 0; i < operations.length; i++ ) {
                XMLTools.appendElement( operationsNode, WFS, "wfs:Operation",
                                        operations[i].getOperation() );
            }
        }
        WFSFeatureType[] featureTypes = featureTypeList.getFeatureTypes();
        if ( featureTypes != null ) {
            for ( int i = 0; i < featureTypes.length; i++ ) {
                appendWFSFeatureType( featureTypeListNode, featureTypes[i] );
            }
        }

        LOG.exiting();
    }

    /**
     * Appends the XML representation of the <code>WFSFeatureType</code> instance to the passed
     * <code>Element</code>.
     * 
     * @param root
     * @param featureType
     */
    public static void appendWFSFeatureType( Element root, WFSFeatureType featureType ) {
        LOG.entering();
        Element featureTypeNode = XMLTools.appendElement( root, WFS, "wfs:FeatureType" );

        if ( featureType.getName().getPrefix() != null ) {
            XMLTools.appendNSBinding( featureTypeNode, featureType.getName().getPrefix(),
                                      featureType.getName().getNamespace() );
        }
        XMLTools.appendElement( featureTypeNode, WFS, "wfs:Name",
                                featureType.getName().getAsString() );
        XMLTools.appendElement( featureTypeNode, WFS, "wfs:Title", featureType.getTitle() );
        String abstract_ = featureType.getAbstract();
        if ( abstract_ != null ) {
            XMLTools.appendElement( featureTypeNode, WFS, "wfs:Abstract", featureType.getAbstract() );
        }
        Keywords[] keywords = featureType.getKeywords();
        if ( keywords != null ) {
            appendOWSKeywords( featureTypeNode, keywords );
        }
        URI defaultSrs = featureType.getDefaultSRS();
        if ( defaultSrs != null ) {
            XMLTools.appendElement( featureTypeNode, WFS, "wfs:DefaultSRS", defaultSrs.toString() );
            URI[] otherSrs = featureType.getOtherSrs();
            if ( otherSrs != null ) {
                for ( int i = 0; i < otherSrs.length; i++ ) {
                    XMLTools.appendElement( featureTypeNode, WFS, "wfs:OtherSRS",
                                            otherSrs[i].toString() );
                }
            }
        } else {
            XMLTools.appendElement( featureTypeNode, WFS, "wfs:Title" );
        }
        Operation[] operations = featureType.getOperations();
        if ( operations != null ) {
            Element operationsNode = XMLTools.appendElement( featureTypeNode, WFS, "wfs:Operations" );
            for ( int i = 0; i < operations.length; i++ ) {
                XMLTools.appendElement( operationsNode, WFS, "wfs:Operation",
                                        operations[i].getOperation() );
            }
        }
        FormatType[] formats = featureType.getOutputFormats();
        if ( formats != null ) {
            appendOutputFormats( featureTypeNode, formats );
        }
        Envelope[] wgs84BoundingBoxes = featureType.getWgs84BoundingBoxes();
        for ( int i = 0; i < wgs84BoundingBoxes.length; i++ ) {
            appendWgs84BoundingBox( featureTypeNode, wgs84BoundingBoxes[i] );
        }

        LOG.exiting();
    }

    /**
     * Appends the XML representation of the <code>wfs:ServesGMLObjectTypeList</code>- section to
     * the passed <code>Element</code> as a new element with the given qualified name.
     * 
     * @param root
     * @param elementNS
     * @param elementName
     * @param gmlObjectTypes
     */
    public static void appendGMLObjectTypeList( Element root, URI elementNS, String elementName,
                                               GMLObject[] gmlObjectTypes ) {
        LOG.entering();
        Element gmlObjectTypeListNode = XMLTools.appendElement( root, elementNS, elementName );
        for ( int i = 0; i < gmlObjectTypes.length; i++ ) {
            appendGMLObjectTypeType( gmlObjectTypeListNode, gmlObjectTypes[i] );
        }
        LOG.exiting();
    }

    /**
     * Appends the XML representation of the <code>wfs:GMLObjectTypeType</code>- element to the
     * passed <code>Element</code>.
     * 
     * @param root
     * @param gmlObjectType
     */
    public static void appendGMLObjectTypeType( Element root, GMLObject gmlObjectType ) {
        LOG.entering();
        Element gmlObjectTypeNode = XMLTools.appendElement( root, WFS, "wfs:GMLObjectType" );

        if ( gmlObjectType.getName().getPrefix() != null ) {
            XMLTools.appendNSBinding( gmlObjectTypeNode, gmlObjectType.getName().getPrefix(),
                                      gmlObjectType.getName().getNamespace() );
        }
        XMLTools.appendElement( gmlObjectTypeNode, WFS, "wfs:Name",
                                gmlObjectType.getName().getAsString() );
        if ( gmlObjectType.getTitle() != null ) {
            XMLTools.appendElement( gmlObjectTypeNode, WFS, "wfs:Title", gmlObjectType.getTitle() );
        }
        String abstract_ = gmlObjectType.getAbstract();
        if ( abstract_ != null ) {
            XMLTools.appendElement( gmlObjectTypeNode, WFS, "wfs:Abstract",
                                    gmlObjectType.getAbstract() );
        }
        Keywords[] keywords = gmlObjectType.getKeywords();
        if ( keywords != null ) {
            appendOWSKeywords( gmlObjectTypeNode, keywords );
        }
        FormatType[] formats = gmlObjectType.getOutputFormats();
        if ( formats != null ) {
            appendOutputFormats( gmlObjectTypeNode, formats );
        }
        LOG.exiting();
    }

    /**
     * Appends the XML representation of the given <code>Envelope</code> (as
     * WGS84BoundingBoxType[]) to the passed <code>Element</code>.
     * 
     * @param root
     * @param envelope
     */
    public static void appendWgs84BoundingBox( Element root, Envelope envelope ) {
        LOG.entering();
        Element wgs84BoundingBoxElement = XMLTools.appendElement( root, OWS, "ows:WGS84BoundingBox" );
        XMLTools.appendElement( wgs84BoundingBoxElement, OWS, "ows:LowerCorner",
                                envelope.getMin().getX() + " " + envelope.getMin().getY() );
        XMLTools.appendElement( wgs84BoundingBoxElement, OWS, "ows:UpperCorner",
                                envelope.getMax().getX() + " " + envelope.getMax().getY() );
        LOG.exiting();
    }

    /**
     * Appends the XML representation of the given <code>OutputFormatType</code> (as FormatType[])
     * to the passed <code>Element</code>.
     * 
     * @param root
     * @param formats
     */
    public static void appendOutputFormats( Element root, FormatType[] formats ) {
        LOG.entering();

        Element outputFormatsNode = XMLTools.appendElement( root, WFS, "wfs:OutputFormats" );
        for ( int i = 0; i < formats.length; i++ ) {
            Element formatNode = XMLTools.appendElement( outputFormatsNode, WFS, "wfs:Format",
                                                         formats[i].getValue() );
            if ( formats[i].getInFilter() != null ) {
                formatNode.setAttributeNS( DEEGREEWFS.toString(), "deegree:inFilter",
                                           formats[i].getInFilter().toString() );
            }
            if ( formats[i].getOutFilter() != null ) {
                formatNode.setAttributeNS( DEEGREEWFS.toString(), "deegree:outFilter",
                                           formats[i].getOutFilter().toString() );
            }
            if ( formats[i].getSchemaLocation() != null ) {
                formatNode.setAttributeNS( DEEGREEWFS.toString(), "deegree:schemaLocation",
                                           formats[i].getSchemaLocation().toString() );
            }
        }

        LOG.exiting();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: XMLFactory.java,v $
 Revision 1.27  2006/10/26 13:41:04  mschneider
 Fixed issue 91 from bugtracker.

 Revision 1.26  2006/09/20 12:58:20  mschneider
 Added overriden #appendServiceIdentification() method for OWS 1.0.0 compliance.

 Revision 1.25  2006/08/21 15:49:42  mschneider
 Fixed javadoc version info.

 Revision 1.24  2006/08/18 12:32:12  poth
 appending SortBy element for GetFeature request implemented

 Revision 1.23  2006/08/14 13:17:48  mschneider
 Fixed imports. Javadoc corrections.

 Revision 1.22  2006/07/21 14:09:48  mschneider
 Added stub for GetFeature export that appends only the requested sections.

 Revision 1.21  2006/07/14 11:33:35  poth
 bug fix - append wfs:Properties to the right node when building a WFS Update operation

 Revision 1.20  2006/06/21 10:25:34  mschneider
 Implemented appendUpdate(). Fixes + javadoc corrections.

 ********************************************************************** */