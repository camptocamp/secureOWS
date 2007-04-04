//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/XMLFactory.java,v 1.33 2006/11/22 11:24:51 poth Exp $
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

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.SortProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0. $Revision: 1.33 $, $Date: 2006/11/22 11:24:51 $
 * 
 * @since 2.0
 */
public class XMLFactory extends org.deegree.ogcbase.XMLFactory {
    
    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    private static final URI CSWNS = CommonNamespaces.CSWNS;

    private static final URI OGCNS = CommonNamespaces.OGCNS;

    /**
     * Exports a <code>GetRecordsResponse</code> instance to a
     * <code>GetRecordsResponseDocument</code>.
     * 
     * @param response
     * @return DOM representation of the <code>GetRecordsResponse</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static GetRecordsResultDocument export(GetRecordsResult response)
            throws XMLException {

        GetRecordsResultDocument responseDocument = new GetRecordsResultDocument();

        try {
            responseDocument.createEmptyDocument();
            Element rootElement = responseDocument.getRootElement();
            Document doc = rootElement.getOwnerDocument();
            
            // set required namespaces
            Element recordRespRoot = 
                response.getSearchResults().getRecords().getOwnerDocument().getDocumentElement();
            NamedNodeMap nnm = recordRespRoot.getAttributes();
            for (int i = 0; i < nnm.getLength(); i ++) {
                Node node = nnm.item(i);
                if ( node instanceof Attr ) {
                    rootElement.setAttribute( node.getNodeName(), node.getNodeValue() );
                }             
            }
            // 'version'-attribute
            rootElement.setAttribute("version", response.getVersion());

            // 'RequestId'-element (optional)
            if (response.getRequest().getId() != null) {
                Element requestIdElement = doc.createElementNS( CSWNS.toString(), "csw:RequestId" );
                requestIdElement.appendChild( doc.createTextNode( response.getRequest().getId() ) );
                rootElement.appendChild(requestIdElement);
            }

            // 'SearchStatus'-element (required)
            Element searchStatusElement = doc.createElementNS( CSWNS.toString(), "csw:SearchStatus" );
            // 'status'-attribute (required)
            searchStatusElement.setAttribute( "status", response.getSearchStatus().getStatus() );
            // 'timestamp'-attribute (optional)
            if ( response.getSearchStatus().getTimestamp() != null ) {
                Date date = response.getSearchStatus().getTimestamp();
                String time = TimeTools.getISOFormattedTime( date );
                searchStatusElement.setAttribute("timestamp", time  );
            }
            rootElement.appendChild(searchStatusElement);

            // 'SeachResults'-element (required)
            Element searchResultsElement = 
                doc.createElementNS(CSWNS.toString(), "csw:SearchResults");
            SearchResults results = response.getSearchResults();

            // 'resultSetId'-attribute (optional)
            if (results.getResultSetId() != null) {
                searchResultsElement.setAttribute("resultSetId", results.getResultSetId().toString());
            }
            // 'elementSet'-attribute (optional)
            if (results.getElementSet() != null) {
                searchResultsElement.setAttribute("elementSet", results.getElementSet().toString());
            }
            // 'recordSchema'-attribute (optional)
            if (results.getRecordSchema() != null) {
                searchResultsElement.setAttribute("recordSchema", results.getRecordSchema().toString());
            }
            // 'numberOfRecordsMatched'-attribute (required)
            searchResultsElement.setAttribute("numberOfRecordsMatched", ""
                    + results.getNumberOfRecordsMatched());
            // 'numberOfRecordsReturned'-attribute (required)
            searchResultsElement.setAttribute("numberOfRecordsReturned", ""
                    + results.getNumberOfRecordsReturned());
            // 'nextRecord'-attribute (required)
            searchResultsElement.setAttribute("nextRecord", "" + results.getNextRecord());
            // 'expires'-attribute (optional)
            if (results.getExpires() != null) {
                Date date = results.getExpires();
                String time = TimeTools.getISOFormattedTime( date );
                searchResultsElement.setAttribute("expires", time );
            }
            // append all children of the records container node
            NodeList nl = results.getRecords().getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node copy = doc.importNode( nl.item(i), true) ;
                searchResultsElement.appendChild( copy );
            }
            rootElement.appendChild(searchResultsElement);
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException(e.getMessage());
        }
        return responseDocument;
    }
    
   
    /**
     * Exports a instance of @see GetRecordByIdResult to a
     * @see GetRecordByIdResultDocument
     *.
     * @param response
     * @return
     * @throws XMLParsingException
     */
    public static GetRecordByIdResultDocument export(GetRecordByIdResult response) throws
                                                                        XMLException{
        
        GetRecordByIdResultDocument doc = new GetRecordByIdResultDocument();
        try {
            doc.createEmptyDocument();
            Document owner = doc.getRootElement().getOwnerDocument();
            if ( response != null && response.getRecord() != null ) {
                Node copy = owner.importNode( response.getRecord(), true) ;
                doc.getRootElement().appendChild( copy );
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e.getMessage() );
        }
        
        return doc;
    }

    /**
     * Exports a <code>DescribeRecordResponse</code> instance to a
     * <code>DescribeRecordResponseDocument</code>.
     * 
     * @param response
     * @return DOM representation of the <code>DescribeRecordResponse</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static DescribeRecordResultDocument export(
            DescribeRecordResult response) throws XMLException {

        DescribeRecordResultDocument responseDocument = new DescribeRecordResultDocument();

        try {
            responseDocument.createEmptyDocument();
            Element rootElement = responseDocument.getRootElement();
            Document doc = rootElement.getOwnerDocument();

            // 'SchemaComponent'-elements (required)
            SchemaComponent[] components = response.getSchemaComponents();
            for (int i = 0; i < components.length; i++) {
                Element schemaComponentElement = doc.createElementNS(CSWNS.toString() ,
                        "csw:SchemaComponent");

                // 'targetNamespace'-attribute (required)
                schemaComponentElement.setAttribute("targetNamespace",
                        components[i].getTargetNamespace().toString());

                // 'parentSchema'-attribute (optional)
                if (components[i].getParentSchema() != null) {
                    schemaComponentElement.setAttribute("parentSchema",
                            components[i].getParentSchema().toString());
                }

                // 'schemaLanguage'-attribute (required)
                schemaComponentElement.setAttribute("schemaLanguage",
                        components[i].getSchemaLanguage().toString());
                
                XMLTools.insertNodeInto(components[i].getSchema().getRootElement(), 
                                        schemaComponentElement);
                rootElement.appendChild(schemaComponentElement);
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException(e.getMessage());
        }
        return responseDocument;
    }

    /**
     * Exports a <code>GetRecords</code> instance to a
     * <code>GetRecordsDocument</code>.
     * 
     * @param request
     * @return DOM representation of the <code>GetRecords</code>
     * @throws IOException
     *             if XML template could not be loaded
     */
    public static GetRecordsDocument export(GetRecords request)
            throws XMLException {

        GetRecordsDocument getRecordsDocument = new GetRecordsDocument();
        try {
            getRecordsDocument.createEmptyDocument();
        } catch (Exception e) {
            throw new XMLException(e.getMessage());
        }
        Element rootElement = getRecordsDocument.getRootElement();
        Document doc = rootElement.getOwnerDocument();

        // 'version'-attribute
        rootElement.setAttribute("version", request.getVersion());

        // 'resultType'-attribute
        rootElement.setAttribute("resultType", request.getResultTypeAsString());

        // 'outputFormat'-attribute
        rootElement.setAttribute("outputFormat", request.getOutputFormat());

        // 'outputSchema'-attribute
        rootElement.setAttribute("outputSchema", request.getOutputSchema());

        // 'startPosition'-attribute
        rootElement.setAttribute("startPosition", ""
                + request.getStartPosition());

        // 'maxRecords'-attribute
        rootElement.setAttribute("maxRecords", "" + request.getMaxRecords());

        // '<csw:DistributedSearch>'-element
        if (request.getHopCount() != -1) {
            Element distributedSearchElement = doc.createElementNS(CSWNS.toString(),
                    "csw:DistributedSearch");

            // 'hopCount'-attribute
            distributedSearchElement.setAttribute("hopCount", ""
                    + request.getHopCount());
            rootElement.appendChild(distributedSearchElement);
        }

        // '<csw:ResponseHandler>'-elements (optional)
        String[] responseHandlers = request.getResponseHandlers();
        if (responseHandlers != null) {
            for (int i = 0; i < responseHandlers.length; i++) {
                Element responseHandlerElement = doc.createElementNS(CSWNS.toString(),
                        "csw:ResponseHandler");
                responseHandlerElement.appendChild(doc
                        .createTextNode(responseHandlers[i]));
                rootElement.appendChild(responseHandlerElement);
            }
        }

        // '<csw:Query>'-elements (required)
        Query[] queries = request.getQueries();
        for (int i = 0; i < queries.length; i++) {
            Element queryElement = doc.createElementNS( CSWNS.toString(), "csw:Query" );

            // 'typeName'-attribute
            String s = StringTools.arrayToString( queries[i].getTypeNames(), ',' );
            queryElement.setAttribute("typeNames", s );

            // '<csw:ElementSetName>'-element (optional)
            if (queries[i].getElementSetName() != null) {
                Element elementSetNameElement = doc.createElementNS(CSWNS.toString(),
                        "csw:ElementSetName");
                elementSetNameElement.appendChild(doc.createTextNode(queries[i]
                        .getElementSetName()));
                queryElement.appendChild(elementSetNameElement);
            }

            // '<csw:ElementName>'-elements (optional)
            if (queries[i].getElementsNames() != null) {
                String[] elementNames = queries[i].getElementsNames();
                for (int j = 0; j < elementNames.length; j++) {
                    Element elementNameElement = doc.createElementNS(CSWNS.toString(),
                            "csw:ElementName");
                    elementNameElement.appendChild(doc
                            .createTextNode(elementNames[j]));
                    queryElement.appendChild(elementNameElement);
                }
            }

            // '<csw:Constraint>'-element (optional)
            if (queries[i].getContraint() != null) {
                Element constraintElement = doc.createElementNS(CSWNS.toString(), "csw:Constraint");
                constraintElement.setAttribute( "version", "1.0.0" );
                org.deegree.model.filterencoding.XMLFactory.appendFilter(
                        constraintElement, queries[i].getContraint());
                queryElement.appendChild(constraintElement);
            }

            // '<ogc:SortBy>'-element (optional)
            SortProperty[] sortProperties = queries[i].getSortProperties();
            if (sortProperties != null && sortProperties.length != 0) {          
                Element sortByElement = 
                    doc.createElementNS(OGCNS.toString(), "ogc:SortBy");

                // '<ogc:SortProperty>'-elements
                for (int j = 0; j < sortProperties.length; j++) {
                    Element sortPropertiesElement = 
                        doc.createElementNS(OGCNS.toString(), "ogc:SortProperty");

                    // '<ogc:PropertyName>'-element (required)
                    Element propertyNameElement = 
                        doc.createElementNS(OGCNS.toString(), "ogc:PropertyName");
                    appendPropertyPath(propertyNameElement, sortProperties[i].getSortProperty());

                    // '<ogc:SortOrder>'-element (optional)
                    Element sortOrderElement = 
                        doc.createElementNS(OGCNS.toString(), "ogc:SortOrder");
                    Node tn = doc.createTextNode(sortProperties[i].getSortOrder() ? "ASC" : "DESC");
                    sortOrderElement.appendChild( tn );
                    
                    sortPropertiesElement.appendChild( propertyNameElement );
                    sortPropertiesElement.appendChild( sortOrderElement );
                    sortByElement.appendChild( sortPropertiesElement );
                }
                queryElement.appendChild(sortByElement);
            }
            rootElement.appendChild(queryElement);
        }
        return getRecordsDocument;
    }
    
    /**
     * Exports a <code>GetRecordById</code> instance to a
     * <code>GetRecordByIdDocument</code>.
     * 
     * @param request
     * @return DOM representation of the <code>GetRecordById</code>
     * @throws IOException if XML template could not be loaded
     */
    public static GetRecordByIdDocument export(GetRecordById request) throws XMLException {

        GetRecordByIdDocument getRecordByIdDoc = new GetRecordByIdDocument();
        try {
            getRecordByIdDoc.createEmptyDocument();
        } catch (Exception e) {
            throw new XMLException(e.getMessage());
        }
        Element rootElement = getRecordByIdDoc.getRootElement();
        Document doc = rootElement.getOwnerDocument();

        // 'version'-attribute
        rootElement.setAttribute("version", request.getVersion());
        
        String[] ids = request.getIds();
        for (int i = 0; i < ids.length; i++) {
            Element idElement = doc.createElementNS( CSWNS.toString() , "csw:Id");
            idElement.appendChild( doc.createTextNode( ids[i] ) );
            rootElement.appendChild( idElement );
        }
        
        String elementSetName = request.getElementSetName();
        if ( elementSetName != null ) {
            Element esnElement = doc.createElementNS( CSWNS.toString(), "csw:ElementSetName" );
            esnElement.appendChild( doc.createTextNode( elementSetName ) );
            rootElement.appendChild( esnElement );
        }
        
        return getRecordByIdDoc;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XMLFactory.java,v $
Revision 1.33  2006/11/22 11:24:51  poth
bug fix - setting mandatory Constraint@version attribute

Revision 1.32  2006/10/18 17:00:55  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.31  2006/10/09 20:03:53  poth
bug fixes

Revision 1.30  2006/08/18 12:31:17  poth
bug fix - creating SortBy element

Revision 1.29  2006/08/08 09:22:32  poth
unnecessary import removed

Revision 1.28  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
