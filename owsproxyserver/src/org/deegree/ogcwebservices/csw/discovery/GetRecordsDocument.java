//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/GetRecordsDocument.java,v 1.34 2006/11/22 11:20:45 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
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
 Meckenheimer Allee 176
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
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.discovery.GetRecords.RESULT_TYPE;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents an XML GetRecords document of an OGC CSW 2.0 compliant service.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.34 $, $Date: 2006/11/22 11:20:45 $
 */
public class GetRecordsDocument extends OGCDocument {

    private static final long serialVersionUID = 2796229558893029054L;

    private static final ILogger LOG = LoggerFactory.getLogger( GetRecordsDocument.class );

    private static final String XML_TEMPLATE = "GetRecordsTemplate.xml";

    /**
     * Extracts a <code>GetRecords</code> representation of this object.
     * 
     * @param id
     *            unique ID of the request
     * @return GetRecords representation of this object
     * @throws MissingParameterValueException
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     */
    public GetRecords parse( String id )
                            throws MissingParameterValueException, InvalidParameterValueException,
                            OGCWebServiceException {

        String version = "2.0.0";
        Map vendorSpecificParameters = null;
        RESULT_TYPE resultType = RESULT_TYPE.RESULTS;
        String outputFormat = "text/xml";
        String outputSchema = "OGCCORE";
        int startPosition = 1;
        int maxRecords = 10;
        int hopCount = 2;
        String[] responseHandlers = null;
        Query[] queries = null;

        try {
            // '<csw:GetRecords>'-element (required)
            Node contextNode = XMLTools.getRequiredNode( this.getRootElement(),
                                                         "self::csw:GetRecords", nsContext );

            // 'service'-attribute (required, must be CSW)
            String service = XMLTools.getRequiredNodeAsString( contextNode, "@service", nsContext );
            if ( !service.equals( "CSW" ) ) {
                ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
                throw new InvalidParameterValueException( "GetRecords", "'service' must be 'CSW'",
                                                          code );
            }

            // 'version'-attribute (required)
            version = XMLTools.getRequiredNodeAsString( contextNode, "@version", nsContext );

            // 'resultType'-attribute
            // type="csw:ResultType" use="optional" default="hits"
            String resultTypeString = XMLTools.getNodeAsString(
                                                                contextNode,
                                                                "@resultType",
                                                                nsContext,
                                                                GetRecords.RESULT_TYPE_STRING_RESULTS );
            if ( GetRecords.RESULT_TYPE_STRING_HITS.equalsIgnoreCase( resultTypeString ) ) {
                resultType = RESULT_TYPE.HITS;
            } else if ( GetRecords.RESULT_TYPE_STRING_RESULTS.equalsIgnoreCase( resultTypeString ) ) {
                resultType = RESULT_TYPE.RESULTS;
            } else if ( GetRecords.RESULT_TYPE_STRING_VALIDATE.equalsIgnoreCase( resultTypeString ) ) {
                resultType = RESULT_TYPE.VALIDATE;
            } else {
                String msg = "Value '" + resultTypeString
                             + "' for parameter 'resultType' is invalid. Valid values are '"
                             + GetRecords.RESULT_TYPE_STRING_HITS + "', '"
                             + GetRecords.RESULT_TYPE_STRING_RESULTS + "' and '"
                             + GetRecords.RESULT_TYPE_STRING_VALIDATE + "'.";
                throw new InvalidParameterValueException( msg );
            }

            // 'outputFormat'-attribute
            // type="xsd:string" use="optional" default="text/xml"
            outputFormat = XMLTools.getNodeAsString( contextNode, "@outputFormat", nsContext,
                                                     outputFormat );

            // 'outputSchema'-attribute
            // type="xsd:anyURI" use="optional" default="OGCCORE"
            outputSchema = XMLTools.getNodeAsString( contextNode, "@outputSchema", nsContext,
                                                     outputSchema );

            // 'startPosition'-attribute
            // type="xsd:positiveInteger" use="optional" default="1"
            startPosition = XMLTools.getNodeAsInt( contextNode, "@startPosition", nsContext,
                                                   startPosition );
            if ( startPosition < 1 ) {
                String msg = Messages.getMessage("CSW_INVALID_STARTPOSITION", startPosition);
                throw new InvalidParameterValueException (msg);
            }

            // 'maxRecords'-attribute
            // type="xsd:nonNegativeInteger" use="optional" default="10"
            maxRecords = XMLTools.getNodeAsInt( contextNode, "@maxRecords", nsContext, maxRecords );

            // '<csw:DistributedSearch>'-element (optional)
            Node distributedSearchElement = XMLTools.getNode( contextNode, "csw:DistributedSearch",
                                                              nsContext );
            if ( distributedSearchElement != null ) {
                hopCount = XMLTools.getNodeAsInt( contextNode, "@hopCount", nsContext, hopCount );
            }

            // '<csw:ResponseHandler>'-elements (optional)
            responseHandlers = XMLTools.getNodesAsStrings( contextNode, "csw:ResponseHandler",
                                                           nsContext );

            // '<csw:Query>'-elements (required)
            List nl = XMLTools.getRequiredNodes( contextNode, "csw:Query", nsContext );
            queries = new Query[nl.size()];
            for ( int i = 0; i < nl.size(); i++ ) {
                String elementSetName = null;
                String typeNames = null;
                String[] elementNames = null;
                Filter constraint = null;
                SortProperty[] sortProperties = null;

                // 'typeName'-attribute
                // use="required"
                typeNames = XMLTools.getRequiredNodeAsString( (Node) nl.get( i ), "@typeNames",
                                                              nsContext );

                // '<csw:ElementSetName>'-element (optional)
                Node elementSetNameElement = XMLTools.getNode( (Node) nl.get( i ),
                                                               "csw:ElementSetName", nsContext );

                if ( elementSetNameElement != null ) {
                    // must contain one of the values 'brief', 'summary' or
                    // 'full'
                    elementSetName = XMLTools.getRequiredNodeAsString( elementSetNameElement,
                                                                       "text()", nsContext,
                                                                       new String[] { "brief",
                                                                                     "summary",
                                                                                     "full" } );

                }

                // '<csw:ElementName>'-element (required, if no
                // '<csw:ElementSetName>' is given)
                if ( elementSetNameElement == null ) {
                    elementNames = XMLTools.getNodesAsStrings( (Node) nl.get( i ),
                                                               "csw:ElementName/text()", nsContext );
                    if ( elementNames.length == 0 ) {
                        throw new XMLParsingException( Messages.getMessage( "CSW_MISSING_QUERY_ELEMENT(SET)NAME" ) );
                    }
                }

                // '<csw:Constraint>'-element (optional)
                Node constraintElement = XMLTools.getNode( (Node) nl.get( i ), "csw:Constraint",
                                                           nsContext );
                if ( constraintElement != null ) {
                    String ver = XMLTools.getAttrValue( constraintElement, "version" );
                    if ( ver == null ) {
                        throw new MissingParameterValueException( Messages.getMessage( "CSW_MISSING_CONSTRAINT_VERSION" ) );
                    }
                    if ( !"1.0.0".equals( ver ) && !"1.1.0".equals( ver ) ) {
                        throw new InvalidParameterValueException( Messages.getMessage( "CSW_INVALID_CONSTRAINT_VERSION", ver ) );
                    }
                    Node filterElement = XMLTools.getNode( constraintElement, "ogc:Filter",
                                                           nsContext );
                    try {
                        constraint = AbstractFilter.buildFromDOM( (Element) filterElement );
                    } catch ( Exception e ) {
                        String s = Messages.getMessage( "CSW_INVALID_CONSTRAINT_CONTENET", e.getMessage() );
                        throw new InvalidParameterValueException( s );
                    }
                }

                // '<ogc:SortBy>'-element (optional)
                Node sortByElement = XMLTools.getNode( (Node) nl.get( i ), "ogc:SortBy", nsContext );
                if ( sortByElement != null ) {
                    List sortPropertyList = XMLTools.getNodes( sortByElement, "ogc:SortProperty",
                                                               nsContext );
                    if ( sortPropertyList.size() == 0 ) {
                        throw new XMLParsingException( "At least one 'ogc:SortBy'-element in 'ogc:SortProperty' is required." );
                    }
                    sortProperties = new SortProperty[sortPropertyList.size()];
                    for ( int j = 0; j < sortPropertyList.size(); j++ ) {
                        sortProperties[j] = SortProperty.create( (Element) sortPropertyList.get( j ) );
                    }
                }
                queries[i] = new Query( elementSetName, elementNames, constraint, sortProperties,
                                        new String[] { typeNames } );
            }

        } catch ( Exception e ) {
            LOG.logError( "CatalogGetCapabilities", e );
            ExceptionCode code = ExceptionCode.INVALID_FORMAT;
            throw new OGCWebServiceException( "CatalogGetCapabilities", e.getMessage(), code );
        }

        return new GetRecords( id, version, vendorSpecificParameters, null, resultType,
                               outputFormat, outputSchema, startPosition, maxRecords, hopCount,
                               responseHandlers, queries );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.framework.xml.XMLFragment#createEmptyDocument()
     */
    void createEmptyDocument()
                            throws IOException, SAXException {
        URL url = GetRecordsDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetRecordsDocument.java,v $
 Revision 1.34  2006/11/22 11:20:45  poth
 bug fix - checking for mandatory Constraint@version attribute

 Revision 1.33  2006/10/10 15:53:59  mschneider
 Added handling of startPosition.

 Revision 1.32  2006/08/18 12:30:46  poth
 bug fix - parsing sortBy element

 Revision 1.31  2006/06/29 12:03:03  poth
 set default resultType to RESULTS

 Revision 1.30  2006/06/29 12:00:11  poth
 caused by some ambigous parts of the CSW spec resultType in GetRecords requests now wil be treated not case-sensitive

 Revision 1.29  2006/06/29 10:27:17  mschneider
 Changed resultType of GetRecords to enum.

 Revision 1.28  2006/06/19 19:27:30  mschneider
 Changed visibility. Added footer.

 ********************************************************************** */