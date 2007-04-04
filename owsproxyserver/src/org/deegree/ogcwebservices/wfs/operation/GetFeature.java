//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/GetFeature.java,v 1.61 2006/11/16 08:53:21 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.deegree.ogcbase.PropertyPathStep;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a <code>GetFeature</code> request to a web feature service.
 * <p>
 * The GetFeature operation allows the retrieval of features from a web feature service.
 * A GetFeature request is processed by a WFS and when the value of the outputFormat attribute is
 * set to text/gml; subtype=gml/3.1.1, a GML instance document, containing the result set, is
 * returned to the client.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version  $Revision: 1.61 $, $Date: 2006/11/16 08:53:21 $
 */
public class GetFeature extends AbstractWFSRequest {
    
    private static final ILogger LOG = LoggerFactory.getLogger( GetFeature.class );

    private static final long serialVersionUID = 8885456550385433051L;

    /** Serialized java object format (deegree specific extension) **/
    public static final String FORMAT_FEATURECOLLECTION = "FEATURECOLLECTION";

    /**
     * Known result types.
     */
    public static enum RESULT_TYPE {

        /** A full response should be generated. */
        RESULTS,

        /** Only a count of the number of features should be returned. */
        HITS
    }

    private RESULT_TYPE resultType = RESULT_TYPE.RESULTS;

    private String outputFormat;

    private int maxFeatures;

    private int traverseXLinkDepth;

    private int traverseXLinkExpiry;

    private List<Query> queries;

    // deegree specific extension, default: 1 (start at first feature)
    private int startPosition;

    /**
     * Creates a new <code>GetFeature</code> instance.
     * 
     * @param version
     *            request version
     * @param id
     *            id of the request
     * @param handle
     * @param resultType
     *            desired result type (results | hits)
     * @param outputFormat
     *            requested result format
     * @param maxFeatures
     * @param startPosition
     *            deegree specific parameter defining where to start considering features
     * @param traverseXLinkDepth
     * @param traverseXLinkExpiry
     * @param queries
     * @param vendorSpecificParam
     */
    GetFeature( String version, String id, String handle, RESULT_TYPE resultType,
                String outputFormat, int maxFeatures, int startPosition, int traverseXLinkDepth,
                int traverseXLinkExpiry, Query[] queries, Map<String, String> vendorSpecificParam ) {
        super( version, id, handle, vendorSpecificParam );
        this.setQueries( queries );
        this.outputFormat = outputFormat;
        this.maxFeatures = maxFeatures;
        this.startPosition = startPosition;
        this.resultType = resultType;
        this.traverseXLinkDepth = traverseXLinkDepth;
        this.traverseXLinkExpiry = traverseXLinkExpiry;
    }

    /**
     * Creates a new <code>GetFeature</code> instance from the given parameters.
     * 
     * @param version
     *            request version
     * @param id
     *            id of the request
     * @param resultType
     *            desired result type (results | hits)
     * @param outputFormat
     *            requested result format
     * @param handle
     * @param maxFeatures
     *            default = -1 (all features)
     * @param startPosition
     *            default = 0 (starting at the first feature)
     * @param traverseXLinkDepth
     * @param traverseXLinkExpiry
     * @param queries
     *            a set of Query objects that describes the query to perform
     * @return new <code>GetFeature</code> request
     */
    public static GetFeature create( String version, String id, RESULT_TYPE resultType,
                                     String outputFormat, String handle, int maxFeatures,
                                     int startPosition, int traverseXLinkDepth,
                                     int traverseXLinkExpiry, Query[] queries ) {
        return new GetFeature( version, id, handle, resultType, outputFormat, maxFeatures,
                               startPosition, traverseXLinkDepth, traverseXLinkExpiry, queries,
                               null );
    }

    /**
     * Creates a new <code>GetFeature</code> instance from a document that contains the DOM
     * representation of the request.
     * 
     * @param id
     *            of the request
     * @param root
     *            element that contains the DOM representation of the request
     * @return new <code>GetFeature</code> request
     * @throws OGCWebServiceException
     */
    public static GetFeature create( String id, Element root )
                            throws OGCWebServiceException {
        GetFeatureDocument doc = new GetFeatureDocument();
        doc.setRootElement( root );
        GetFeature request;
        try {
            request = doc.parse( id );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "GetFeature", e.getMessage() );
        }
        return request;
    }

    /**
     * Creates a new <code>GetFeature</code> instance from the given key-value pair encoded
     * request.
     * 
     * @param id
     *            request identifier
     * @param request
     * @return new <code>GetFeature</code> request
     * @throws InvalidParameterValueException
     * @throws InconsistentRequestException
     */
    public static GetFeature create( String id, String request )
                            throws InconsistentRequestException, InvalidParameterValueException {
        Map<String, String> map = KVP2Map.toMap( request );
        map.put( "ID", id );
        return create( map );
    }

    /**
     * Creates a new <code>GetFeature</code> request from the given map.
     * 
     * @param kvp
     *            key-value pairs, keys have to be uppercase
     * @return new <code>GetFeature</code> request
     * @throws InconsistentRequestException
     * @throws InvalidParameterValueException
     */
    public static GetFeature create( Map<String, String> kvp )
                            throws InconsistentRequestException, InvalidParameterValueException {

        // SERVICE
        checkServiceParameter( kvp );

        // ID (deegree specific)
        String id = kvp.get( "ID" );

        // VERSION
        String version = checkVersionParameter( kvp );

        // OUTPUTFORMAT
        String outputFormat = getParam( "OUTPUTFORMAT", kvp, FORMAT_GML3 );

        // RESULTTYPE
        RESULT_TYPE resultType = RESULT_TYPE.RESULTS;
        String resultTypeString = kvp.get( "RESULTTYPE" );
        if ( "hits".equals( resultTypeString ) ) {
            resultType = RESULT_TYPE.HITS;
        }

        // FEATUREVERSION
        String featureVersion = kvp.get( "FEATUREVERSION" );

        // MAXFEATURES
        String maxFeaturesString = kvp.get( "MAXFEATURES" );
        // -1: fetch all features
        int maxFeatures = -1;
        if ( maxFeaturesString != null ) {
            try {
                maxFeatures = Integer.parseInt( maxFeaturesString );
                if ( maxFeatures < 1 ) {
                    throw new NumberFormatException();
                }
            } catch ( NumberFormatException e ) {
                LOG.logError( e.getMessage(), e );
                String msg = Messages.getMessage( "WFS_PARAMETER_INVALID_INT", maxFeaturesString,
                                              "MAXFEATURES" );
                throw new InvalidParameterValueException( msg );
            }
        }

        // STARTPOSITION (deegree specific)
        String startPosString = getParam( "STARTPOSITION", kvp, "1" );
        int startPosition = 1;
        try {
            startPosition = Integer.parseInt( startPosString );
            if ( startPosition < 1 ) {
                throw new NumberFormatException();
            }
        } catch ( NumberFormatException e ) {
            LOG.logError( e.getMessage(), e );
            String msg = Messages.getMessage( "WFS_PARAMETER_INVALID_INT", startPosString,
                                          "STARTPOSITION" );
            throw new InvalidParameterValueException( msg );
        }

        // SRSNAME
        String srsName = kvp.get( "SRSNAME" );

        // TYPENAME
        QualifiedName[] typeNames = extractTypeNames( kvp );
        if ( typeNames == null ) {
            // no TYPENAME parameter -> FEATUREID must be present
            String featureId = kvp.get( "FEATUREID" );
            if ( featureId != null ) {
                String msg = Messages.getMessage( "WFS_FEATUREID_PARAM_UNSUPPORTED" );
                throw new InvalidParameterValueException( msg );
            }
            String msg = Messages.getMessage( "WFS_TYPENAME+FID_PARAMS_MISSING" );
            throw new InvalidParameterValueException( msg );
        }

        // BBOX
        Filter bboxFilter = extractBBOXFilter( kvp );

        // FILTER (prequisite: TYPENAME)
        Map<QualifiedName, Filter> filterMap = extractFilters( kvp, typeNames );
        if ( bboxFilter != null && filterMap.size() > 0 ) {
            String msg = Messages.getMessage( "WFS_BBOX+FILTER_INVALID" );
            throw new InvalidParameterValueException( msg );
        }

        // PROPERTYNAME
        Map<QualifiedName, PropertyPath[]> propertyNameMap = extractPropNames( kvp, typeNames );

        // SORTBY
        SortProperty[] sortProperties = null;

        // TRAVERSEXLINKDEPTH
        int traverseXLinkDepth = -1;

        // TRAVERSEXLINKEXPIRY
        int traverseXLinkExpiry = -1;

        // build a Query instance for each requested feature type (later also for each featureid...)
        Query[] queries = new Query[typeNames.length];
        for ( int i = 0; i < queries.length; i++ ) {
            QualifiedName ftName = typeNames[i];
            PropertyPath[] properties = propertyNameMap.get( ftName );
            Filter filter;
            if ( bboxFilter != null ) {
                filter = bboxFilter;
            } else {
                filter = filterMap.get( ftName );
            }
            QualifiedName[] ftNames = new QualifiedName[] { ftName };
            queries[i] = new Query( properties, null, sortProperties, null, featureVersion,
                                    ftNames, srsName, filter, resultType, maxFeatures,
                                    startPosition );
        }

        // build a GetFeature request that contains all queries
        GetFeature request = new GetFeature( version, id, null, resultType, outputFormat,
                                             maxFeatures, startPosition, traverseXLinkDepth,
                                             traverseXLinkExpiry, queries, null );
        return request;
    }

    /**
     * Extracts a <code>Filter</code> from the BBOX parameter.
     * 
     * TODO handle other dimension count and crs
     * 
     * @param model
     * @return filter representing the BBOX parameter (null, if no BBOX parameter specified)
     * @throws InvalidParameterValueException
     */
    private static Filter extractBBOXFilter( Map<String, String> model )
                            throws InvalidParameterValueException {

        ComplexFilter filter = null;
        String bboxString = model.get( "BBOX" );
        if ( bboxString != null ) {
            String msg = "Parameter 'BBOX' is currently not supported. Please use the 'FILTER' parameter instead.";
            throw new InvalidParameterValueException( msg );
            //            String[] parts = bboxString.split( "," );
            //            double[] coords = new double[4];
            //
            //            if ( parts.length > 5 ) {
            //                String msg = Messages.getString( "WFS_BBOX_PARAM_WRONG_COORD_COUNT" );
            //                throw new InvalidParameterValueException( msg );
            //            }
            //
            //            for ( int i = 0; i < coords.length; i++ ) {
            //                try {
            //                    coords[i] = Double.parseDouble( parts[i] );
            //                } catch ( NumberFormatException e ) {
            //                    String msg = Messages.getMessage( "WFS_BBOX_PARAM_COORD_INVALID", coords[i] );
            //                    throw new InvalidParameterValueException( msg );
            //                }
            //            }
            //
            //            // build filter
            //            Envelope bbox = GeometryFactory.createEnvelope( coords[0], coords[1], coords[2],
            //                                                            coords[3], null );
            //            Surface surface;
            //            try {
            //                surface = GeometryFactory.createSurface( bbox, null );
            //            } catch ( GeometryException e ) {
            //                String msg = Messages.getMessage( "WFS_BBOX_PARAM_BBOX_INVALID", e.getMessage() );
            //                throw new InvalidParameterValueException( msg );
            //            }
            //            Operation op = new SpatialOperation( OperationDefines.BBOX, null, surface );
            //            filter = new ComplexFilter( op );
        }
        return filter;
    }

    /**
     * Extracts the FILTER parameter and assigns them to the requested type names.
     * <p>
     * This is necessary, because it is allowed to specify a filter for each requested feature
     * type.
     * 
     * @param kvp
     * @param typeNames
     * @return map with the assignments of type names to filters
     * @throws InvalidParameterValueException 
     */
    private static Map<QualifiedName, Filter> extractFilters( Map<String, String> kvp,
                                                              QualifiedName[] typeNames )
                            throws InvalidParameterValueException {
        Map<QualifiedName, Filter> filterMap = new HashMap<QualifiedName, Filter>();
        String filterString = kvp.get( "FILTER" );
        if ( filterString != null ) {
            String[] filterStrings = filterString.split( "\\)" );
            if ( filterStrings.length != typeNames.length ) {
                String msg = Messages.getMessage( "WFS_FILTER_PARAM_WRONG_COUNT",
                                              filterStrings.length, typeNames.length );
                throw new InvalidParameterValueException( msg );
            }
            for ( int i = 0; i < filterStrings.length; i++ ) {
                // remove possible leading parenthesis
                if ( filterStrings[i].startsWith( "(" ) ) {
                    filterStrings[i] = filterStrings[i].substring( 1 );
                }
                Document doc;
                try {
                    doc = XMLTools.parse( new StringReader( filterStrings[i] ) );
                    Filter filter = AbstractFilter.buildFromDOM( doc.getDocumentElement() );
                    filterMap.put( typeNames[i], filter );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = Messages.getMessage( "WFS_FILTER_PARAM_PARSING", e.getMessage() );
                    throw new InvalidParameterValueException( msg );
                }
            }
        }
        return filterMap;
    }

    /**
     * Extracts the PROPERTYNAME parameter and assigns them to the requested type names.
     *
     * @param kvp
     * @param typeNames
     * @return map with the assignments of type names to property names
     * @throws InvalidParameterValueException 
     */
    private static Map<QualifiedName, PropertyPath[]> extractPropNames( Map<String, String> kvp,
                                                                        QualifiedName[] typeNames )
                            throws InvalidParameterValueException {
        Map<QualifiedName, PropertyPath[]> propMap = new HashMap<QualifiedName, PropertyPath[]>();
        String propNameString = kvp.get( "PROPERTYNAME" );
        if ( propNameString != null ) {
            String[] propNameLists = propNameString.split( "\\)" );
            if ( propNameLists.length != typeNames.length ) {
                String msg = Messages.getMessage( "WFS_PROPNAME_PARAM_WRONG_COUNT",
                                              propNameLists.length, typeNames.length );
                throw new InvalidParameterValueException( msg );
            }
            NamespaceContext nsContext = extractNamespaceParameter( kvp );
            for ( int i = 0; i < propNameLists.length; i++ ) {
                String propNameList = propNameLists[i];
                if ( propNameList.startsWith( "(" ) ) {
                    propNameList = propNameList.substring( 1 );
                }
                String[] propNames = propNameList.split( "," );
                PropertyPath[] paths = new PropertyPath[propNames.length];
                for ( int j = 0; j < propNames.length; j++ ) {
                    PropertyPath path = transformToPropertyPath( propNames[j], nsContext );
                    paths[j] = ( path );
                }
                propMap.put( typeNames[i], paths );
            }
        }
        return propMap;
    }

    /**
     * Transforms the given property name to a (qualified) <code>PropertyPath</code> object by
     * using the specified namespace bindings.
     * 
     * @param propName
     * @param nsContext
     * @return (qualified) <code>PropertyPath</code> object
     * @throws InvalidParameterValueException 
     */
    private static PropertyPath transformToPropertyPath( String propName, NamespaceContext nsContext )
                            throws InvalidParameterValueException {
        String[] steps = propName.split( "/" );
        List<PropertyPathStep> propertyPathSteps = new ArrayList<PropertyPathStep>( steps.length );

        for ( int i = 0; i < steps.length; i++ ) {
            PropertyPathStep propertyStep = null;
            QualifiedName propertyName = null;
            String step = steps[i];
            boolean isAttribute = false;
            boolean isIndexed = false;
            int selectedIndex = -1;

            // check if step begins with '@' -> must be the final step then
            if ( step.startsWith( "@" ) ) {
                if ( i != steps.length - 1 ) {
                    String msg = "PropertyName '" + propName
                                 + "' is illegal: the attribute specifier may only "
                                 + "be used for the final step.";
                    throw new InvalidParameterValueException( msg );
                }
                step = step.substring( 1 );
                isAttribute = true;
            }

            // check if the step ends with brackets ([...])
            if ( step.endsWith( "]" ) ) {
                if ( isAttribute ) {
                    String msg = "PropertyName '" + propName
                                 + "' is illegal: if the attribute specifier ('@') is used, "
                                 + "index selection ('[...']) is not possible.";
                    throw new InvalidParameterValueException( msg );
                }
                int bracketPos = step.indexOf( '[' );
                if ( bracketPos < 0 ) {
                    String msg = "PropertyName '" + propName
                                 + "' is illegal. No opening brackets found for step '" + step
                                 + "'.";
                    throw new InvalidParameterValueException( msg );
                }
                try {
                    selectedIndex = Integer.parseInt( step.substring( bracketPos + 1,
                                                                      step.length() - 1 ) );
                } catch ( NumberFormatException e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = "PropertyName '" + propName + "' is illegal. Specified index '"
                                 + step.substring( bracketPos + 1, step.length() - 1 )
                                 + "' is not a number.";
                    throw new InvalidParameterValueException( msg );
                }
                step = step.substring( 0, bracketPos );
                isIndexed = true;
            }

            // determine namespace prefix and binding (if any)
            int colonPos = step.indexOf( ':' );
            String prefix = "";
            String localName = step;
            if ( colonPos > 0 ) {
                prefix = step.substring( 0, colonPos );
                localName = step.substring( colonPos + 1 );
            }
            URI nsURI = nsContext.getURI( prefix );
            if ( nsURI == null && prefix.length() > 0 ) {
                String msg = "PropertyName '" + propName + "' uses an unbound namespace prefix: "
                             + prefix;
                throw new InvalidParameterValueException( msg );
            }
            propertyName = new QualifiedName( prefix, localName, nsURI );

            if ( isAttribute ) {
                propertyStep = PropertyPathFactory.createAttributePropertyPathStep( propertyName );
            } else if ( isIndexed ) {
                propertyStep = PropertyPathFactory.createPropertyPathStep( propertyName,
                                                                           selectedIndex );
            } else {
                propertyStep = PropertyPathFactory.createPropertyPathStep( propertyName );
            }
            propertyPathSteps.add( propertyStep );
        }
        return PropertyPathFactory.createPropertyPath( propertyPathSteps );
    }

    /**
     * Returns the output format.
     * <p>
     * The outputFormat attribute defines the format to use to generate the result set. Vendor
     * specific formats, declared in the capabilities document are possible. The WFS-specs implies
     * GML as default output format.
     * 
     * @return the output format.
     */
    public String getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * The query defines which feature type to query, what properties to retrieve and what
     * constraints (spatial and non-spatial) to apply to those properties.
     * <p>
     * only used for xml-coded requests
     * 
     * @return contained queries
     */
    public Query[] getQuery() {
        return queries.toArray( new Query[queries.size()] );
    }

    /**
     * sets the <Query>
     */
    private void setQueries( Query[] query ) {
        this.queries = new ArrayList<Query>( query.length );
        if ( query != null ) {
            for ( int i = 0; i < query.length; i++ ) {
                this.queries.add( query[i] );
            }
        }
    }

    /**
     * The optional maxFeatures attribute can be used to limit the number of features that a
     * GetFeature request retrieves. Once the maxFeatures limit is reached, the result set is
     * truncated at that point. If not limit is set -1 will be returned.
     * 
     * @return number of feature to fetch, -1 if no limit is set
     */
    public int getMaxFeatures() {
        return maxFeatures;
    }

    /**
     * @see #getMaxFeatures()
     * @param max
     */
    public void setMaxFeatures( int max ) {
        this.maxFeatures = max;
    }

    /**
     * The startPosition parameter identifies the first result set entry to be returned specified
     * the default is the first record. If not startposition is set 0 will be returned
     * 
     * @return  the first result set entry to be returned
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Returns the desired result type of the GetFeature operation. Possible values are 'results'
     * and 'hits'.
     * 
     * @return the desired result type
     */
    public RESULT_TYPE getResultType() {
        return this.resultType;
    }

    /**
     * The optional traverseXLinkDepth attribute indicates the depth to which nested property XLink
     * linking element locator attribute (href) XLinks in all properties of the selected feature(s)
     * are traversed and resolved if possible. A value of "1" indicates that one linking element
     * locator attribute (href) XLink will be traversed and the referenced element returned if
     * possible, but nested property XLink linking element locator attribute (href) XLinks in the
     * returned element are not traversed. A value of "*" indicates that all nested property XLink
     * linking element locator attribute (href) XLinks will be traversed and the referenced elements
     * returned if possible. The range of valid values for this attribute consists of positive
     * integers plus "*".
     * 
     * @return the depth to which nested property XLinks are traversed and resolved
     */
    public int getTraverseXLinkDepth() {
        return traverseXLinkDepth;
    }

    /**
     * The traverseXLinkExpiry attribute is specified in minutes. It indicates how long a Web
     * Feature Service should wait to receive a response to a nested GetGmlObject request. If no
     * traverseXLinkExpiry attribute is present for a GetGmlObject request, the WFS wait time is
     * implementation dependent.
     * 
     * @return how long to wait  to receive a response to a nested GetGmlObject request
     */
    public int getTraverseXLinkExpiry() {
        return traverseXLinkExpiry;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        String ret = null;
        ret = "WFSGetFeatureRequest: { \n ";
        ret += "outputFormat = " + outputFormat + "\n";
        ret += ( "handle = " + getHandle() + "\n" );
        ret += ( "query = " + queries + "\n" );
        ret += "}\n";
        return ret;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * Revision 1.25.2.4 2005/11/10 15:24:44 mschneider Refactoring: use "PropertyPath" in
 * "org.deegree.model.filterencoding.PropertyName".
 * 
 * Revision 1.25.2.3 2005/11/09 18:02:29 mschneider More refactoring. Revision 1.25.2.2 2005/11/07
 * 16:45:08 deshmukh NodeList to List
 * 
 * Revision 1.25.2.1 2005/11/07 15:38:04 mschneider Refactoring: use NamespaceContext instead of
 * Node for namespace bindings. Revision 1.25 2005/10/04 15:55:05 poth no message
 * 
 * Revision 1.24 2005/09/27 19:53:19 poth no message
 * 
 * Revision 1.23 2005/09/22 12:05:03 poth no message
 * 
 * Revision 1.22 2005/08/29 17:19:10 mschneider PropertyNames are represented by Strings instead of
 * QualifiedNames (actually they are XPath-expressions).
 * 
 * Revision 1.21 2005/08/26 21:11:29 poth no message
 * 
 * Revision 1.5 2005/08/24 16:12:55 mschneider Renamed GenericName to QualifiedName.
 * 
 * Revision 1.4 2005/07/22 15:17:54 mschneider Added constants for output formats.
 * 
 * Revision 1.3 2005/04/23 15:32:05 poth no message
 * 
 * Revision 1.2 2005/04/21 12:41:00 mschneider Changed error message in case of broken filter
 * expressions.
 * 
 * Revision 1.1 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.19 2005/03/09 11:52:59 mschneider *** empty log message ***
 * 
 * Revision 1.17 2005/03/01 16:20:15 poth no message
 * 
 * Revision 1.16 2005/03/01 14:39:08 mschneider *** empty log message ***
 * 
 * Revision 1.1 2004/06/07 13:38:34 tf code adapted to wfs1 refactoring
 * 
 * Revision 1.11 2004/02/23 07:47:51 poth no message
 * 
 **************************************************************************************************/