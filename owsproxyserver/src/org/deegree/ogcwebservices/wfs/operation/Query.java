//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/Query.java,v 1.36 2006/11/09 17:45:20 mschneider Exp $
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

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Function;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;
import org.w3c.dom.Element;

/**
 * Represents a <code>Query</code> operation as a part of a {@link GetFeature} request. 
 * 
 * Each individual query packaged in a {@link GetFeature} request is defined using the query value.
 * The query value defines which feature type to query, what properties to retrieve and what
 * constraints (spatial and non-spatial) to apply to those properties.
 * <p>
 * The mandatory <code>typeName</code> attribute is used to indicate the name of one or more
 * feature type instances or class instances to be queried. Its value is a list of
 * namespace-qualified names (XML Schema type QName, e.g. myns:School) whose value must match one of
 * the feature types advertised in the Capabilities document of the WFS. Specifying more than one
 * typename indicates that a join operation is being performed. All the names in the typeName list
 * must be valid types that belong to this query's feature content as defined by the GML Application
 * Schema. Optionally, individual feature type names in the typeName list may be aliased using the
 * format QName=Alias. The following is an example typeName value that indicates that a join
 * operation is to be performed and includes aliases: <BR>
 * <code>typeName="ns1:InwaterA_1m=A,ns1:InwaterA_1m=B,ns2:CoastL_1M=C"</code><BR>
 * This example encodes a join between three feature types aliased as A, B and C. The join between
 * feature type A and B is a self-join.
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.36 $, $Date: 2006/11/09 17:45:20 $
 */
public class Query {

    private String handle;

    private QualifiedName[] typeNames;

    private String featureVersion;

    private String srsName;

    private PropertyPath[] propertyNames;

    private Function[] functions;

    private Filter filter;

    private SortProperty[] sortProperties;

    // deegree specific extension ("inherited" from GetFeature container)    
    private RESULT_TYPE resultType;

    // deegree specific extension ("inherited" from GetFeature container)
    private int maxFeatures = -1;

    // deegree specific extension ("inherited" from GetFeature container)    
    private int startPosition = 1;

    /**
     * Creates a new <code>Query</code> instance.
     * 
     * @param propertyNames
     *            names of the requested properties, may be null or empty
     * @param functions
     *            names of the requested functions, may be null or empty
     * @param sortProperties
     *            sort criteria, may be null or empty
     * @param handle
     *            client-generated identifier for the query, may be null
     * @param featureVersion
     *            version of the feature instances to fetched, may be null
     * @param typeNames
     *            list of requested feature types. if more than one feature types is set a JOIN will
     *            be created (not yet supported)
     * @param srsName
     *            name of the spatial reference system
     * @param filter
     *            spatial and none-spatial constraints
     * @param resultType
     *            deegree specific extension ("inherited" from GetFeature container)
     * @param maxFeatures
     *            deegree specific extension ("inherited" from GetFeature container)
     * @param startPosition
     *            deegree specific extension ("inherited" from GetFeature container)
     */
    Query( PropertyPath[] propertyNames, Function[] functions, SortProperty[] sortProperties,
          String handle, String featureVersion, QualifiedName[] typeNames, String srsName,
          Filter filter, RESULT_TYPE resultType, int maxFeatures, int startPosition ) {
        if ( propertyNames == null ) {
            this.propertyNames = new PropertyPath[0];
            //this.propertyNames[0] = new PropertyPath( typeNames[0] );
        } else {
            this.propertyNames = propertyNames;
        }
        this.functions = functions;
        this.sortProperties = sortProperties;
        this.handle = handle;
        this.featureVersion = featureVersion;
        this.typeNames = typeNames;
        this.srsName = srsName;
        this.filter = filter;
        this.resultType = resultType;
        this.maxFeatures = maxFeatures;
        this.startPosition = startPosition;
    }

    /**
     * Creates a new <code>Query</code> instance.
     *
     * @param propertyNames
     *            names of the requested properties, may be null or empty
     * @param functions
     *            names of the requested functions, may be null or empty
     * @param sortProperties
     *            sort criteria, may be null or empty
     * @param handle
     *            client-generated identifier for the query, may be null
     * @param featureVersion
     *            version of the feature instances to fetched, may be null
     * @param typeNames
     *            list of requested feature types. if more than one feature types is set a JOIN will
     *            be created (not yet supported)
     * @param srsName
     *            name of the spatial reference system
     * @param filter
     *            spatial and none-spatial constraints
     * @param resultType
     *            deegree specific extension ("inherited" from GetFeature container)
     * @param maxFeatures
     *            deegree specific extension ("inherited" from GetFeature container)
     * @param startPosition
     *            deegree specific extension ("inherited" from GetFeature container)
     * @return new <code>Query</code> instance
     */
    public static Query create( PropertyPath[] propertyNames, Function[] functions,
                               SortProperty[] sortProperties, String handle, String featureVersion,
                               QualifiedName[] typeNames, String srsName, Filter filter,
                               int maxFeatures, int startPosition, RESULT_TYPE resultType ) {
        return new Query( propertyNames, functions, sortProperties, handle, featureVersion,
                          typeNames, srsName, filter, resultType, maxFeatures, startPosition );
    }

    /**
     * Creates a new simple <code>Query</code> instance that selects the whole feature type.
     * 
     * @param typeName
     *            name of the feature to be queried
     * @return new <code>Query</code> instance
     */
    public static Query create( QualifiedName typeName ) {
        return new Query( null, null, null, null, null, new QualifiedName[] { typeName }, null,
                          null, RESULT_TYPE.RESULTS, -1, 0 );
    }

    /**
     * Creates a new simple <code>Query</code> instance that selects the whole feature type.
     * 
     * @param typeName
     *            name of the feature to be queried
     * @param filter
     *            spatial and none-spatial constraints
     * @return new <code>Query</code> instance
     */
    public static Query create( QualifiedName typeName, Filter filter ) {
        return new Query( null, null, null, null, null, new QualifiedName[] { typeName }, null,
                          filter, RESULT_TYPE.RESULTS, -1, 0 );
    }

    /**
     * Creates a <code>Query</code> instance from a document that contains the DOM
     * representation of the request.
     * <p>
     * Note that the following attributes from the surrounding element are also considered (if
     * it present):
     * <ul>
     * <li>resultType</li>
     * <li>maxFeatures</li>
     * <li>startPosition</li>
     * </ul>
     * 
     * @param element
     * @return corresponding <code>Query</code> instance 
     * @throws XMLParsingException 
     */
    public static Query create( Element element )
                            throws XMLParsingException {

        GetFeatureDocument doc = new GetFeatureDocument();
        Query query = doc.parseQuery( element );
        return query;
    }

    /**
     * Returns the handle attribute.
     * <p>
     * The handle attribute is included to allow a client to associate a mnemonic name to the
     * query. The purpose of the handle attribute is to provide an error handling mechanism for
     * locating a statement that might fail.
     * 
     * @return the handle attribute
     */
    public String getHandle() {
        return this.handle;
    }

    /**
     * Returns the typeName attribute.
     * <p>
     * The typeName attribute is used to indicate the names of the feature types or classes to be
     * queried.
     * 
     * @return the typeName attribute
     */
    public QualifiedName[] getTypeNames() {
        return this.typeNames;
    }

    /**
     * Returns the srsName attribute.
     * 
     * @return the srsName attribute
     */
    public String getSrsName() {
        return this.srsName;
    }

    /**
     * Sets the srsName attribute to given value.
     * 
     * @param srsName
     *            name of the requested SRS
     */
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }    
    
    /**
     * Returns the featureVersion attribute.
     * 
     * The version attribute is included in order to accommodate systems that support feature
     * versioning. A value of ALL indicates that all versions of a feature should be fetched.
     * Otherwise an integer can be specified to return the n th version of a feature. The version
     * numbers start at '1' which is the oldest version. If a version value larger than the largest
     * version is specified then the latest version is return. The default action shall be for the
     * query to return the latest version. Systems that do not support versioning can ignore the
     * parameter and return the only version that they have.
     * 
     * @return the featureVersion attribute
     */
    public String getFeatureVersion() {
        return this.featureVersion;
    }

    /**
     * Returns the requested properties.
     *  
     * @return the requested properties
     * 
     * @see #getFunctions()
     */
    public PropertyPath[] getPropertyNames() {
        return this.propertyNames;
    }

    /**
     * Beside property names a query may contains 0 to n functions modifying the values of one or
     * more original properties. E.g. instead of area and population the density of a country can be
     * requested by using a function instead:
     * 
     * <pre>
     * &lt;ogc:Div&gt;
     *  &lt;ogc:PropertyName&gt;population&lt;/ogc:PropertyName&gt;
     *  &lt;ogc:PropertyName&gt;area&lt;/ogc:PropertyName&gt;
     * &lt;/ogc:Div&gt;
     * </pre>
     * <p>
     * If no functions and no property names are specified all properties should be fetched.
     * </p>
     * 
     * @return requested functions
     * 
     * @see #getPropertyNames()
     */
    public Function[] getFunctions() {
        return this.functions;
    }

    /**
     * Returns the filter that limits the query.
     * 
     * @return the filter that limits the query
     */
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Returns the sort criteria for the result.
     * 
     * @return the sort criteria for the result
     */
    public SortProperty[] getSortProperties() {
        return this.sortProperties;
    }

    /**
     * Returns the value of the resultType attribute ("inherited" from the GetFeature container).
     * 
     * @return the value of the resultType attribute
     */
    public RESULT_TYPE getResultType() {
        return this.resultType;
    }

    /**
     * Returns the value of the maxFeatures attribute ("inherited" from the GetFeature container).
     * 
     * The optional maxFeatures attribute can be used to limit the number of features that a
     * GetFeature request retrieves. Once the maxFeatures limit is reached, the result set is
     * truncated at that point. If not limit is set -1 will be returned
     * 
     * @return the value of the maxFeatures attribute
     */
    public int getMaxFeatures() {
        return this.maxFeatures;
    }

    /**
     * Returns the value of the startPosition attribute ("inherited" from the GetFeature
     * container).
     * <p>
     * The startPosition parameter identifies the first result set entry to be returned. If no
     * startPosition is set explicitly, 1 will be returned.
     * 
     * @return the value of the startPosition attribute, 1 if undefined
     */
    public int getStartPosition() {
        return this.startPosition;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        String ret = null;
        ret = "propertyNames = " + propertyNames + "\n";
        ret += ( "handle = " + handle + "\n" );
        ret += ( "version = " + featureVersion + "\n" );
        ret += ( "typeName = " + typeNames + "\n" );
        ret += ( "filter = " + filter + "\n" );
        return ret;
    }
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: Query.java,v $
 * Revision 1.36  2006/11/09 17:45:20  mschneider
 * Added #setSrsName(String).
 *
 * Revision 1.35  2006/10/12 16:24:00  mschneider
 * Javadoc + compiler warning fixes.
 *
 * Revision 1.34  2006/08/14 16:48:58  mschneider
 * Improved javadoc.
 *
 * Revision 1.33  2006/06/08 09:37:46  mschneider
 * Added convenience create() methods.
 *
 * Revision 1.32  2006/06/07 17:18:39  mschneider
 * Fixed header + footer. Improved javadoc.
 *
 * Revision 1.31  2006/05/30 07:57:36  taddei
 * *** empty log message ***
 *
 * Revision 1.30  2006/05/16 16:28:31  mschneider
 * Renamed WFSRequestBase to AbstractWFSRequest.
 *
 * Revision 1.29  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.28  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.27  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.26  2006/03/29 14:58:34  mschneider
 * Changed result type constants to enum.
 *
 * Revision 1.25  2006/02/23 21:00:18  poth
 * *** empty log message ***
 *
 * Revision 1.24  2005/12/15 16:43:45  poth
 * no message
 *
 * Revision 1.23  2005/12/07 10:52:55  poth
 * no message
 *
 * Revision 1.22  2005/12/04 14:45:45  poth
 * no message
 *
 * Revision 1.21  2005/11/22 18:08:04  deshmukh
 * Transaction WFS.. work in progress
 * 
 * Revision 1.20 2005/11/17 08:20:12 deshmukh Renamed nsNode to nsContext
 * 
 * Revision 1.19 2005/11/16 13:44:59 mschneider Merge of wfs development branch. 
 ********************************************************************** */