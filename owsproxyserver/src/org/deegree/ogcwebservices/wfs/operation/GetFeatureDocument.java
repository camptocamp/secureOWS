//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/GetFeatureDocument.java,v 1.5 2006/11/16 08:53:21 mschneider Exp $
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

import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Function;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Parser for "wfs:GetFeature" requests.  
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/11/16 08:53:21 $
 */
public class GetFeatureDocument extends AbstractWFSRequestDocument {

    private static final long serialVersionUID = -3411186861123355322L;

    /**
     * Parses the underlying document into a <code>GetFeature</code> request object.
     * 
     * @param id
     * @return corresponding <code>GetFeature</code> object
     * @throws XMLParsingException
     */
    public GetFeature parse( String id )
                            throws XMLParsingException {

        checkServiceAttribute();
        String version = checkVersionAttribute();

        Element root = this.getRootElement();
        String handle = XMLTools.getNodeAsString( root, "@handle", nsContext, null );
        String outputFormat = XMLTools.getNodeAsString( root, "@outputFormat", nsContext,
                                                        AbstractWFSRequest.FORMAT_GML3 );

        int maxFeatures = XMLTools.getNodeAsInt( root, "@maxFeatures", nsContext, -1 );
        int startPosition = XMLTools.getNodeAsInt( root, "@startPosition", nsContext, 1 );
        if ( startPosition < 1 ) {
            String msg = org.deegree.i18n.Messages.getMessage( "WFS_INVALID_STARTPOSITION",
                                                               startPosition );
            throw new XMLParsingException( msg );
        }

        int traverseXLinkDepth = XMLTools.getNodeAsInt( root, "@traverseXLinkDepth", nsContext, -1 );
        int traverseXLinkExpiry = XMLTools.getNodeAsInt( root, "@traverseXLinkExpiry", nsContext,
                                                         -1 );

        String resultTypeString = XMLTools.getNodeAsString( root, "@resultType", nsContext,
                                                            "results" );
        RESULT_TYPE resultType;
        if ( "results".equals( resultTypeString ) ) {
            resultType = RESULT_TYPE.RESULTS;
        } else if ( "hits".equals( resultTypeString ) ) {
            resultType = RESULT_TYPE.HITS;
        } else {
            String msg = Messages.getMessage( "WFS_INVALID_RESULT_TYPE", resultTypeString );
            throw new XMLParsingException( msg );
        }

        List nl = XMLTools.getRequiredNodes( root, "wfs:Query", nsContext );
        Query[] queries = new Query[nl.size()];
        for ( int i = 0; i < queries.length; i++ ) {
            queries[i] = parseQuery( (Element) nl.get( i ) );
        }

        // vendorspecific attributes; required by deegree rights management
        Map<String, String> vendorSpecificParams = parseDRMParams( root );

        GetFeature req = new GetFeature( version, id, handle, resultType, outputFormat,
                                         maxFeatures, startPosition, traverseXLinkDepth,
                                         traverseXLinkExpiry, queries, vendorSpecificParams );
        return req;
    }

    /**
     * Parses the given query element into a <code>Query</code> object.
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
     *            query element
     * @return corresponding <code>Query</code> object
     * @throws XMLParsingException 
     */
    Query parseQuery( Element element )
                            throws XMLParsingException {

        String handle = XMLTools.getNodeAsString( element, "@handle", nsContext, null );

        // TODO clean this up
        String tmp = XMLTools.getRequiredNodeAsString( element, "@typeName", nsContext );
        String[] values = StringTools.toArray( tmp, ",", false );
        QualifiedName[] typeNames = transformToQualifiedNames( values, element );

        String featureVersion = XMLTools.getNodeAsString( element, "@featureVersion", nsContext,
                                                          null );
        String srsName = XMLTools.getNodeAsString( element, "@srsName", nsContext, null );

        List nl = XMLTools.getNodes( element, "wfs:PropertyName/text()", nsContext );
        PropertyPath[] propertyNames = new PropertyPath[nl.size()];
        for ( int i = 0; i < propertyNames.length; i++ ) {
            propertyNames[i] = OGCDocument.parsePropertyPath( (Text) nl.get( i ) );
        }

        nl = XMLTools.getNodes( element, "ogc:Function", nsContext );
        Function[] functions = new Function[nl.size()];
        for ( int i = 0; i < functions.length; i++ ) {
            functions[i] = (Function) Function.buildFromDOM( (Element) nl.get( i ) );
        }

        Filter filter = null;
        Element filterElement = (Element) XMLTools.getNode( element, "ogc:Filter", nsContext );
        if ( filterElement != null ) {
            filter = AbstractFilter.buildFromDOM( filterElement );
        }

        SortProperty[] sortProps = null;
        Element sortByElement = (Element) XMLTools.getNode( element, "ogc:SortBy", nsContext );
        if ( sortByElement != null ) {
            sortProps = parseSortBy( sortByElement );
        }

        //----------------------------------------------------------------------------------------
        // parse "inherited" attributes from GetFeature element (but very kindly)
        //----------------------------------------------------------------------------------------        

        String resultTypeString = "results";
        RESULT_TYPE resultType;
        try {
            resultTypeString = XMLTools.getNodeAsString( element, "../@resultType", nsContext,
                                                         "results" );
        } catch ( XMLParsingException doNothing ) {
            // it's o.k. - let's be really kind here
        }

        if ( "results".equals( resultTypeString ) ) {
            resultType = RESULT_TYPE.RESULTS;
        } else if ( "hits".equals( resultTypeString ) ) {
            resultType = RESULT_TYPE.HITS;
        } else {
            String msg = Messages.getMessage( "WFS_INVALID_RESULT_TYPE", resultTypeString );
            throw new XMLParsingException( msg );
        }

        int maxFeatures = -1;
        try {
            maxFeatures = XMLTools.getNodeAsInt( element, "../@maxFeatures", nsContext, -1 );
        } catch ( XMLParsingException doNothing ) {
            // it's o.k. - let's be really kind here            
        }

        int startPosition = -1;
        try {
            startPosition = XMLTools.getNodeAsInt( element, "../@startPosition", nsContext, 0 );
        } catch ( XMLParsingException doNothing ) {
            // it's o.k. - let's be really kind here            
        }

        return new Query( propertyNames, functions, sortProps, handle, featureVersion, typeNames,
                          srsName, filter, resultType, maxFeatures, startPosition );
    }

    /**
     * Parses the given "ogc:SortBy" element.
     * 
     * @param root
     *            "ogc:SortBy" element
     * @return corresponding <code>SortProperty</code> instances (in original order)
     */
    private SortProperty[] parseSortBy( Element root )
                            throws XMLParsingException {

        List nl = XMLTools.getRequiredNodes( root, "ogc:SortProperty", nsContext );
        SortProperty[] sortProps = new SortProperty[nl.size()];
        for ( int i = 0; i < nl.size(); i++ ) {
            sortProps[i] = SortProperty.create( (Element) nl.get( i ) );
        }
        return sortProps;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:

 $Log: GetFeatureDocument.java,v $
 Revision 1.5  2006/11/16 08:53:21  mschneider
 Merged messages from org.deegree.ogcwebservices.wfs and its subpackages.

 Revision 1.4  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.3  2006/10/10 15:53:59  mschneider
 Added handling of startPosition.

 Revision 1.2  2006/08/14 16:48:30  mschneider
 Cleanup. Javadoc fixes.

 Revision 1.1  2006/06/07 17:17:56  mschneider
 Moved XML parsing functionality from GetFeature here.

 Revision 1.1  2006/06/06 17:05:50  mschneider
 Initial version. Outfactored parser.

 ********************************************************************** */