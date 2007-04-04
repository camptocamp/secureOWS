//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/GetRecords.java,v 1.29 2006/10/10 15:53:59 mschneider Exp $
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

import java.io.StringReader;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.AbstractCSWRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class representation of a <code>GetRecords</code> request.
 * <p>
 * The primary means of resource discovery in the general model are the two operations search and
 * present. In the HTTP protocol binding these are combined in the form of the mandatory
 * <code>GetRecords</code> operation, which does a search.
 * <p>
 * Parameters specific to the <code>GetRecords</code> -request (omitting REQUEST, SERVICE and
 * VERSION): <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Occurences</th>
 * <th>Function</th>
 * </tr>
 * <tr>
 * <td align="center">NAMESPACE</td>
 * <td align="center">0|1</td>
 * <td>The NAMESPACE parameter is included in the KVP encoding to allow clients to bind any
 * namespace prefixes that might be used for qualified names specified in other parameters. For
 * example, the typeName parameter may include qualified names of the form namespace prefix:name.
 * The value of the NAMESPACE parameter is a comma separated list of character strings of the form
 * [namespace prefix:] namespace url. Not including the name namespace prefix binds the specified
 * URL to the default namespace. As in XML, only one default namespace may be bound. This parameter
 * is not required for the XML encoding since XML includes a mechanism for binding namespace
 * prefixes.</td>
 * </tr>
 * <tr>
 * <td align="center">resultType</td>
 * <td align="center">0|1 (default: RESULTS)</td>
 * <td>The resultType parameter may have the values HITS, RESULTS or VALIDATE and is used to
 * indicate whether the catalogue service returns the full result set, the number of hits the query
 * found or validates the request. If the resultType parameter is set to HITS, the catalogue service
 * shall return an empty &lt;GetRecordsResponse&gt; element with the numberOfRecordsMatched
 * attribute set to indicate the number of hits. The other attributes may be set to zero or not
 * specified at all if they are optional. If the resultType parameter is set to HITS, then the
 * values for the parameters outputFormat and outputSchema (if specified) shall be ignored since no
 * actual records will be returned. If the resultType parameter is set to RESULTS, the catalogue
 * service should generate a complete response with the &lt;GetRecordsResponse&gt; element
 * containing the result set for the request. If the resultType parameter is set to VALIDATE, the
 * catalogue service shall validate the request and return an empty &lt;GetRecordsResponse&gt;. All
 * mandatory attributes may be given a value of zero and all optional attributes may be omitted. If
 * the request does not validate then a service exception shall be raised as describe in Subclause
 * 10.3.2.3.</td>
 * </tr>
 * <tr>
 * <td align="center">outputFormat</td>
 * <td align="center">0|1 (default: text/xml)</td>
 * <td>The outputFormat parameter is used to control the format of the output that is generated in
 * response to a GetRecords request. Its value must be a MIME type. The default value, "text/xml",
 * means that the output shall be an XML document. All registries shall at least support XML as an
 * output format. Other output formats may be supported and may include output formats such as TEXT
 * (MIME type text/plain), or HTML (MIME type text/html). The list of output formats that a CSW
 * instance provides must be advertised in the Capabilities document. In the case where the output
 * format is text/xml, the CSW must generate an XML document that validates against a schema
 * document that is specified in the output document via the xsi:schemaLocation attribute defined in
 * XML.</td>
 * </tr>
 * <tr>
 * <td align="center">outputSchema</td>
 * <td align="center">0|1 (default: OGCCORE)</td>
 * <td>The outputSchema parameter is used to indicate the schema of the output that is generated in
 * response to a GetRecords request. The default value for this parameter shall be OGCCORE
 * indicating that the schema for the core returnable properties (as defined in subclause 6.3.3)
 * shall be used. Application profiles may define additional values for outputSchema and may
 * redefine the default value but all profiles must support the value OGCCORE. Examples values for
 * the outputSchema parameter might be FGDC, or ISO19119, ISO19139 or ANZLIC. The list of supported
 * output schemas must be advertised in the capabilities document.</tr>
 * <tr>
 * <td align="center">startPosition</td>
 * <td align="center">0|1 (default: 1)</td>
 * <td>The startPosition paramater is used to indicate at which record position the catalogue
 * should start generating output. The default value is 1 meaning it starts at the first record in
 * the result set.</td>
 * </tr>
 * <tr>
 * <td align="center">maxRecords</td>
 * <td align="center">0|1 (default: 10)</td>
 * <td>The maxRecords parameter is used to define the maximum number of records that should be
 * returned from the result set of a query. If it is not specified, then 10 records shall be
 * returned. If its value is set to zero, then the behavior is indentical to setting
 * "resultType=HITS" as described above.</td>
 * </tr>
 * <tr>
 * <td align="center">typeName</td>
 * <td align="center">1</td>
 * <td>The typeName parameter is a list of record type names that define a set of metadata record
 * element names which will be constrained in the predicate of the query. In addition, all or some
 * of the these names may be specified in the query to define which metadata record elements the
 * query should present in the response to the GetRecords operation.</td>
 * </tr>
 * <tr>
 * <td align="center">ElementSetName / ElementName</td>
 * <td align="center">* (default: 10)</td>
 * <td>The ElementName parameter is used to specify one or more metadata record elements that the
 * query should present in the response to the a GetRecords operation. Well known sets of element
 * may be named, in which case the ElementSetName parameter may be used (e.g.brief, summary or
 * full). If neither parameter is specified, then a CSW shall present all metadata record elements.
 * As mentioned above, if the outputFormat parameter is set to text/xml, then the response to the
 * GetRecords operation shall validate against a schema document that is referenced in the response
 * using the xmlns attributes. If the set of metadata record elements that the client specifies in
 * the query in insufficient to generate a valid XML response document, a CSW may augment the list
 * of elements presented to the client in order to be able to generate a valid document. Thus a
 * client application should expect to receive more than the requested elements if the output format
 * is set to XML. </td>
 * </tr>
 * <tr>
 * <td align="center">CONSTRAINTLANGUAGE / Constraint</td>
 * <td align="center">0|1</td>
 * <td>Each request encoding (XML and KVP) has a specific mechanism for specifying the predicate
 * language that will be used to constrain a query. In the XML encoding, the element
 * &lt;Constraint&gt; is used to define the query predicate. The root element of the content of the
 * &lt;Constraint&gt; element defines the predicate language that is being used. Two possible root
 * elements are &lt;ogc:Filter&gt; for the OGC XML filter encoding, and &lt;csw:CqlText&gt; for a
 * common query language string. An example predicate specification in the XML encoding is:
 * 
 * &lt;Constraint&gt; &lt;CqlText&gt;prop1!=10&lt;/CqlText&gt; &lt;/Constraint&gt;
 * 
 * In the KVP encoding, the parameter CONSTRAINTLANGUAGE is used to specify the predicate language
 * being used. The Constraint parameter is used to specify the actual predicate. For example, to
 * specify a CQL predicate, the following parameters would be set in the KVP encoding: <br>
 * 
 * ...CONSTRAINTLANGUAGE=CQL_TEXT&amp;CONSTRAINT=&quot;prop1!=10&quot;...
 * 
 * </td>
 * </tr>
 * <tr>
 * <td align="center">SortBy</td>
 * <td align="center">0|1</td>
 * <td>The result set may be sorted by specifying one or more metadata record elements upon which
 * to sort. In KVP encoding, the SORTBY parameter is used to specify the list of sort elements. The
 * value for the SORTBY parameter is a comma-separated list of metadata record element names upon
 * which to sort the result set. The format for each element in the list shall be either element
 * name:A indicating that the element values should be sorted in ascending order or element name:D
 * indicating that the element values should be sorted in descending order. For XML encoded
 * requests, the &lt;ogc:SortBy&gt; element is used to specify a list of sort metadata record
 * elements. The attribute sortOrder is used to specify the sort order for each element. Valid
 * values for the sortOrder attribute are ASC indicating an ascending sort and DESC indicating a
 * descending sort.</td>
 * </tr>
 * <tr>
 * <td align="center">DistributedSearch / hopCount</td>
 * <td align="center">0|1 (default: FALSE)</td>
 * <td>The DistributedSearch parameter may be used to indicate that the query should be
 * distributed. The default query behaviour, if the DistributedSearch parameter is set to FALSE (or
 * is not specified at all), is to execute the query on the local server. In the XML encoding, if
 * the &lt;DistributedSearch&gt; element is not specified then the query is executed on the local
 * server. <br>
 * <br>
 * The hopCount parameter controls the distributed query behaviour by limiting the maximum number of
 * message hops before the search is terminated. Each catalogue decrements this value by one when
 * the request is received and does not propagate the request if the hopCount=0.</td>
 * </tr>
 * <tr>
 * <td align="center">ResponseHandler</td>
 * <td align="center">0|1</td>
 * <td>The ResponseHandler parameter is a flag that indicates how the GetRecords operation should
 * be processed by a CSW. If the parameter is not present, then the GetRecords operation is
 * processed synchronously meaning that the client sends the GetRecords request to a CSW and waits
 * to receive a valid response or exception message. The CSW immediately processes the GetRecords
 * request while the client waits for a response. The problem with this mode of operation is that
 * the client may timeout waiting for the CSW to process the request. If the ResponseHandler
 * parameter is present, the GetRecords operation is processed asynchronously. In this case, the CSW
 * responds immediately to a client's request with an acknowledgment message that tells the client
 * that the request has been received and validated, and notification of completion will be sent to
 * the URI specified as the value of the ResponseHandler parameter.</td>
 * </tr>
 * </table>
 * 
 * @since 2.0
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.29 $
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version 2.0, $Revision: 1.29 $, $Date: 2006/10/10 15:53:59 $
 */

public class GetRecords extends AbstractCSWRequest {

    private static final long serialVersionUID = 2796229558893029054L;

    private static final ILogger LOG = LoggerFactory.getLogger( GetRecords.class );

    public static enum RESULT_TYPE {
        HITS, VALIDATE, RESULTS
    };

    public static String RESULT_TYPE_STRING_HITS = "HITS";

    public static String RESULT_TYPE_STRING_VALIDATE = "VALIDATE";

    public static String RESULT_TYPE_STRING_RESULTS = "RESULTS";

    private RESULT_TYPE resultType = RESULT_TYPE.RESULTS;

    // keys are Strings (namespace prefix or "" for default namespace), values
    // are URIs
    private Map namespace;

    private String outputFormat;

    private String outputSchema;

    private int startPosition;

    private int maxRecords;

    private int hopCount;

    private String[] responseHandlers;

    private Query[] queries;

    /**
     * creates a GetRecords request from the XML fragment passed. The passed element must be valid
     * against the OGC CSW 2.0 GetRecords schema.
     * 
     * TODO respect namespaces (use QualifiedNames) for type names
     * 
     * @param id
     *            unique ID of the request
     * @param root
     *            root element of the GetRecors request
     * @return
     */
    public static GetRecords create( String id, Element root )
                            throws MissingParameterValueException, InvalidParameterValueException,
                            OGCWebServiceException {
        
        GetRecordsDocument document = new GetRecordsDocument();
        document.setRootElement( root );
        GetRecords ogcRequest = document.parse( id );
        
        return ogcRequest;
    }

    /**
     * Creates a new <code>GetRecords</code> instance from the values stored in the submitted Map.
     * Keys (parameter names) in the Map must be uppercase.
     * 
     * @TODO evaluate vendorSpecificParameter
     * 
     * @param kvp
     *            Map containing the parameters
     * @exception InvalidParameterValueException
     * @exception MissingParameterValueException
     */
    public static GetRecords create( Map kvp )
                            throws InvalidParameterValueException, MissingParameterValueException {

        String id = null;
        String version = "2.0.0";
        Map vendorSpecificParameters = null;
        Map namespaceMappings = null;
        RESULT_TYPE resultType = RESULT_TYPE.HITS;
        String outputFormat = "text/xml";
        String outputSchema = "OGCCORE";
        int startPosition = 1;
        int maxRecords = 10;
        int hopCount = 2;
        String[] responseHandlers = null;
        Query[] queries = null;

        id = getParam( "ID", kvp, "" );
        LOG.logDebug( "GetRecordRequest id=" + id );
        version = getRequiredParam( "VERSION", kvp );

        // extract namespace mappings
        namespaceMappings = getNSMappings( getParam( "NAMESPACE", kvp, null ) );

        String resultTypeString = getParam( "RESULTTYPE", kvp, RESULT_TYPE_STRING_RESULTS );
        if ( RESULT_TYPE_STRING_HITS.equalsIgnoreCase( resultTypeString ) ) {
            resultType = RESULT_TYPE.HITS;
        } else if ( RESULT_TYPE_STRING_RESULTS.equalsIgnoreCase( resultTypeString ) ) {
            resultType = RESULT_TYPE.RESULTS;
        } else if ( RESULT_TYPE_STRING_VALIDATE.equalsIgnoreCase( resultTypeString ) ) {
            resultType = RESULT_TYPE.VALIDATE;
        } else {
            String msg = "Value '" + resultTypeString
                         + "' for parameter 'resultType' is invalid. Valid values are '"
                         + RESULT_TYPE_STRING_HITS + "', '" + RESULT_TYPE_STRING_RESULTS
                         + "' and '" + RESULT_TYPE_STRING_VALIDATE + "'.";
            throw new InvalidParameterValueException( msg );
        }

        outputFormat = getParam( "OUTPUTFORMAT", kvp, "text/xml" );
        outputSchema = getParam( "OUTPUTSCHEMA", kvp, "OGCCORE" );
        startPosition = getParamAsInt( "STARTPOSITION", kvp, 1 );
        if ( startPosition < 1 ) {
            String msg = Messages.getMessage("CSW_INVALID_STARTPOSITION", startPosition);
            throw new InvalidParameterValueException (msg);
        }        
        maxRecords = getParamAsInt( "MAXRECORDS", kvp, 10 );

        // build Filter object (from CONSTRAINT parameter)
        Filter constraint = null;
        String constraintLanguage = getParam( "CONSTRAINTLANGUAGE", kvp, "FILTER" );
        if ( !constraintLanguage.equals( "FILTER" ) ) {
            String msg = "Value '" + constraintLanguage
                         + "'for Parameter CONSTRAINTLANGUAGE is invalid: "
                         + "Only 'FILTER' is supported.";
            throw new InvalidParameterValueException( msg );
        }
        String constraintString = (String) kvp.get( "CONSTRAINT" );
        if ( constraintString != null ) {
            try {
                Document doc = XMLTools.parse( new StringReader( constraintString ) );
                Element element = doc.getDocumentElement();
                constraint = AbstractFilter.buildFromDOM( element );
            } catch ( Exception e ) {
                String msg = "An error occured when parsing the 'CONSTRAINT' parameter "
                             + "Filter expression: " + e.getMessage();
                throw new InvalidParameterValueException( msg );
            }
        }

        SortProperty[] sortProperties = SortProperty.create( getParam( "SORTBY", kvp, null ) );

        String elementSetName = (String) kvp.get( "ELEMENTSETNAME" );
        String[] elementNames = null;
        if ( elementSetName == null ) {
            String elementNameString = (String) kvp.get( "ELEMENTNAME" );
            if ( elementNameString != null ) {
                elementNames = StringTools.toArray( elementNameString, ",", false );
                if ( elementNames.length == 0 ) {
                    elementNames = null;
                }
            }
        }
        if ( elementSetName == null && elementNames == null ) {
            elementSetName = "Full";
        }

        // build one Query object for each specified typeName
        String tmp = getRequiredParam( "TYPENAMES", kvp );
        String[] typeNames = StringTools.toArray( tmp, ",", false );
        if ( typeNames.length == 0 ) {
            throw new MissingParameterValueException( "Mandatory parameter 'TYPENAMES' is missing!" );
        }
        queries = new Query[typeNames.length];
        for ( int i = 0; i < typeNames.length; i++ ) {
            queries[i] = new Query( elementSetName, elementNames, constraint, sortProperties,
                                    typeNames );
        }

        // find out if the query should be performed locally or in a distributed
        // fashion
        hopCount = -1;
        String distributedSearch = getParam( "DISTRIBUTEDSEARCH", kvp, "false" );
        if ( distributedSearch.equalsIgnoreCase( "true" ) ) {
            hopCount = getParamAsInt( "HOPCOUNT", kvp, 2 );
        }

        return new GetRecords( id, version, vendorSpecificParameters, namespaceMappings,
                               resultType, outputFormat, outputSchema, startPosition, maxRecords,
                               hopCount, responseHandlers, queries );
    }

    /**
     * Creates a new <code>GetRecords</code> instance.
     * 
     * @param id
     * @param version
     * @param vendorSpecificParameters
     * @param namespace
     * @param resultType
     * @param outputFormat
     * @param outputSchema
     * @param startPosition
     * @param maxRecords
     * @param hopCount
     * @param responseHandlers
     * @param queries
     */
    GetRecords( String id, String version, Map vendorSpecificParameters, Map namespace,
                RESULT_TYPE resultType, String outputFormat, String outputSchema,
                int startPosition, int maxRecords, int hopCount, String[] responseHandlers,
                Query[] queries ) {
        super( version, id, vendorSpecificParameters );
        this.namespace = namespace;
        this.resultType = resultType;
        this.outputFormat = outputFormat;
        this.outputSchema = outputSchema;
        this.startPosition = startPosition;
        this.maxRecords = maxRecords;
        this.hopCount = hopCount;
        this.responseHandlers = responseHandlers;
        this.queries = queries;
    }

    /**
     * Used to specify a namespace and its prefix. Format must be [ <prefix>:] <url>. If the prefix
     * is not specified then this is the default namespace
     * <p>
     * Zero or one (Optional) ; Include value for each distinct namespace used by all qualified
     * names in the request. If not included, all qualified names are in default namespace
     * <p>
     * The NAMESPACE parameter is included in the KVP encoding to allow clients to bind any
     * namespace prefixes that might be used for qualified names specified in other parameters. For
     * example, the typeName parameter may include qualified names of the form namespace
     * prefix:name.
     * <p>
     * The value of the NAMESPACE parameter is separated list of character strings of the form
     * [namespace prefix:]namespace url. Not including the name namespace prefix binds the specified
     * URL to the default namespace. As in XML, only one default namespace may be bound.
     * 
     */
    public Map getNamespace() {
        return this.namespace;
    }

    /**
     * The resultType parameter may have the values HITS, RESULTS or VALIDATE and is used to
     * indicate whether the catalogue service returns the full result set, the number of hits the
     * query found or validates the request.
     * <p>
     * If the resultType parameter is set to HITS, the catalogue service shall return an empty
     * &lt;GetRecordsResponse&gt;element with the numberOfRecordsMatched attribute set to indicate
     * the number of hits. The other attributes may be set to zero or not specified at all if they
     * are optional.
     * <p>
     * If the resultType parameter is set to HITS, then the values for the parameters outputFormat
     * and outputSchema (if specified) shall be ignored since no actual records will be returned
     * <p>
     * If the resultType parameter is set to RESULTS, the catalogue service should generate a
     * complete response with the &lt;GetRecordsResponse&gt;element containing the result set for
     * the request
     * <p>
     * If the resultType parameter is set to VALIDATE, the catalogue service shall validate the
     * request and return an empty &lt;GetRecordsResponse&gt;. All mandatory attributes may be given
     * a value of zero and all optional attributes may be omitted. If the request does not validate
     * then a service exception shall be raised
     * 
     */
    public RESULT_TYPE getResultType() {
        return this.resultType;
    }

    /**
     * The resultType parameter may have the values HITS, RESULTS or VALIDATE and is used to
     * indicate whether the catalogue service returns the full result set, the number of hits the
     * query found or validates the request.
     * <p>
     * If the resultType parameter is set to HITS, the catalogue service shall return an empty
     * &lt;GetRecordsResponse&gt;element with the numberOfRecordsMatched attribute set to indicate
     * the number of hits. The other attributes may be set to zero or not specified at all if they
     * are optional.
     * <p>
     * If the resultType parameter is set to HITS, then the values for the parameters outputFormat
     * and outputSchema (if specified) shall be ignored since no actual records will be returned
     * <p>
     * If the resultType parameter is set to RESULTS, the catalogue service should generate a
     * complete response with the &lt;GetRecordsResponse&gt;element containing the result set for
     * the request
     * <p>
     * If the resultType parameter is set to VALIDATE, the catalogue service shall validate the
     * request and return an empty &lt;GetRecordsResponse&gt;. All mandatory attributes may be given
     * a value of zero and all optional attributes may be omitted. If the request does not validate
     * then a service exception shall be raised
     * 
     */
    public String getResultTypeAsString() {
        String resultTypeString = null;
        switch ( this.resultType ) {
        case HITS: {
            resultTypeString = RESULT_TYPE_STRING_HITS;
            break;
        }
        case RESULTS: {
            resultTypeString = RESULT_TYPE_STRING_RESULTS;
            break;
        }
        case VALIDATE: {
            resultTypeString = RESULT_TYPE_STRING_VALIDATE;
            break;
        }
        }
        return resultTypeString;
    }

    /**
     * sets the resultType of a request. This may be useful to perform a request first
     * with resultType = HITS to determine the total number of records matching a query
     * and afterwards performing the same request with resultType = RESULTS (and maxRecords
     * &lt; number of matched records).
     * @param resultType
     */
    public void setResultType( RESULT_TYPE resultType ) {
        this.resultType = resultType;
    }

    /**
     * returns <= 0 if no distributed search shall be performed. otherwise the recursion depht is
     * returned.
     * <p>
     * The hopCount parameter controls the distributed query behaviour by limiting the maximum
     * number of message hops before the search is terminated. Each catalogue decrements this value
     * by one when the request is received and does not propagate the request if the hopCount=0
     * 
     */
    public int getHopCount() {
        return this.hopCount;
    }

    /**
     * Value is Mime type;The only value that must be supported is text/xml. Other suppored values
     * may include text/html and text/plain
     * <p>
     * The outputFormat parameter is used to control the format of the output that is generated in
     * response to a GetRecords request. Its value must be a MIME type. The default value,
     * "text/xml", means that the output shall be an XML document. All registries shall at least
     * support XML as an output format. Other output formats may be supported and may include output
     * formats such as TEXT (MIME type text/plain), or HTML (MIME type text/html). The list of
     * output formats that a CSW instance provides must be advertised in the Capabilities document
     * <p>
     * In the case where the output format is text/xml, the CSW must generate an XML document that
     * validates against a schema document that is specified in the output document via the
     * xsi:schemaLocation attribute defined in XML
     * 
     */
    public String getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * The outputSchema parameter is used to indicate the schema of the output that is generated in
     * response to a GetRecords request. The default value for this parameter shall be OGCCORE
     * indicating that the schema for the core returnable properties shall be used. Application
     * profiles may define additional values for outputSchema and may redefine the default value but
     * all profiles must support the value OGCCORE
     * <p>
     * Examples values for the outputSchema parameter might be FGDC, or ISO19119, ISO19139 or
     * ANZLIC. The list of supported output schemas must be advertised in the capabilities document
     * 
     */
    public String getOutputSchema() {
        return this.outputSchema;
    }

    /**
     * number of the first returned dataset. Zero or one (Optional)Default value is 1. If
     * startPosition > the number of datasets satisfying the constraint, no dataset will be returned
     * 
     */
    public int getStartPosition() {
        return this.startPosition;
    }

    /**
     * The maxRecords parameter is used to define the maximum number of records that should be
     * returned from the result set of a query. If it is not specified, then 10 records shall be
     * returned. If its value is set to zero, then the behavior is indentical to setting
     * "resultType=HITS"
     * 
     */
    public int getMaxRecords() {
        return this.maxRecords;
    }

    /**
     * 
     */
    public String[] getResponseHandlers() {
        return responseHandlers;
    }

    /**
     * 
     */
    public Query[] getQueries() {
        return queries;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetRecords.java,v $
 Revision 1.29  2006/10/10 15:53:59  mschneider
 Added handling of startPosition.

 Revision 1.28  2006/08/18 12:30:09  poth
 code cleaned

 Revision 1.27  2006/07/10 20:57:05  mschneider
 Fixed footer. Indentation.

 ********************************************************************** */