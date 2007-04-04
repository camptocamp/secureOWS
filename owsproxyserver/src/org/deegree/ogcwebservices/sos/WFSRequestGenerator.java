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
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.sos.getobservation.TInstant;
import org.deegree.ogcwebservices.sos.getobservation.TPeriod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * generate the wfs requests
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 */

public class WFSRequestGenerator {

    private static final String XML_TEMPLATE = "RequestFrame.xml";

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * cerates an empty WFS request
     * 
     * @return
     * @throws IOException
     * @throws SAXException
     */
    private static Document getEmptyWFSRequest()
                            throws IOException, SAXException {

        InputStream is = WFSRequestGenerator.class.getResourceAsStream( XML_TEMPLATE );
        if ( is == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }

        return XMLTools.parse( is );

    }

    /**
     * sets the QueryTypname in the WFS request
     * 
     * @param doc
     *            representing GetFeature DOM-Object
     * @param typename
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     */
    private static void setQueryTypeName( Document doc, QualifiedName typename )
                            throws XMLParsingException {

        Element query = (Element) XMLTools.getRequiredNode( doc, "wfs:GetFeature/wfs:Query",
                                                            nsContext );
        query.setAttribute( "xmlns:" + typename.getPrefix(),
                            typename.getNamespace().toASCIIString() );
        query.setAttribute( "typeName", typename.getPrefix() + ':' + typename.getLocalName() );
    }

    /**
     * sets a filter to the document
     * 
     * @param doc
     * @param filter
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     */
    private static void setFilter( Document doc, Filter filter )
                            throws XMLParsingException {

        Element query = (Element) XMLTools.getNode( doc, "wfs:GetFeature/wfs:Query", nsContext );

        org.deegree.model.filterencoding.XMLFactory.appendFilter( query, filter );

    }

    /**
     * creates a WFS Request with one or more isLike Operations
     * 
     * @param literals
     * @param featureType
     * @param propertyName
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     */
    public static Document createIsLikeOperationWFSRequest( String[] literals,
                                                           QualifiedName featureType,
                                                           QualifiedName propertyName )
                            throws IOException, SAXException, XMLParsingException {

        if ( ( literals == null ) || ( featureType == null ) || ( propertyName == null ) ) {
            String msg = "error: literals, featureType and propertyName can't be null";
            throw new IllegalArgumentException( msg );
        }

        Document request = WFSRequestGenerator.getEmptyWFSRequest();

        WFSRequestGenerator.setQueryTypeName( request, featureType );

        ArrayList al = new ArrayList();

        for ( int i = 0; i < literals.length; i++ ) {

            al.add( new PropertyIsLikeOperation( new PropertyName( propertyName ),
                                                 new Literal( literals[i] ), '*', '#', '!' ) );
        }

        // wenn nur ein feature abgefragt wird, dass <or> weglassen
        if ( al.size() == 1 ) {
            Filter filter = new ComplexFilter( ( (PropertyIsLikeOperation) al.get( 0 ) ) );
            WFSRequestGenerator.setFilter( request, filter );
        } else if ( al.size() > 1 ) {
            LogicalOperation lop = new LogicalOperation( OperationDefines.OR, al );
            WFSRequestGenerator.setFilter( request, new ComplexFilter( lop ) );
        }

        return request;

    }

    public static Document createBBoxWFSRequest( Envelope bbox, QualifiedName featureTypeName,
                                                QualifiedName coordPropertyName )
                            throws IOException, SAXException, XMLParsingException,
                            GeometryException {
        if ( ( bbox == null ) && ( featureTypeName == null ) && ( coordPropertyName == null ) ) {
            String msg = "error: bbox, featureType and coordPropertyName can't be null";
            throw new IllegalArgumentException( msg );
        }

        Document request = WFSRequestGenerator.getEmptyWFSRequest();

        WFSRequestGenerator.setQueryTypeName( request, featureTypeName );

        Geometry geom = GeometryFactory.createSurface( bbox, null );

        SpatialOperation bboxOperation = new SpatialOperation(
                                                               OperationDefines.BBOX,
                                                               new PropertyName( coordPropertyName ),
                                                               geom );

        ComplexFilter filter = new ComplexFilter( bboxOperation );
        WFSRequestGenerator.setFilter( request, filter );

        return request;
    }

    /**
     * 
     * @param times
     * @param featureTypeName
     * @param timePropertyName
     * @param filterOperation
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     */
    public static Document createObservationWFSRequest( Object[] times,
                                                       QualifiedName featureTypeName,
                                                       QualifiedName timePropertyName,
                                                       Operation filterOperation )
                            throws IOException, SAXException, XMLParsingException {

        if ( ( times == null ) || ( featureTypeName == null ) || ( timePropertyName == null ) ) {
            String msg = "error: times, featureType and timePropertyName can't be null";
            throw new IllegalArgumentException( msg );
        }

        Document request = WFSRequestGenerator.getEmptyWFSRequest();

        WFSRequestGenerator.setQueryTypeName( request, featureTypeName );

        ArrayList timeOperationList = new ArrayList();

        // creates the time Filters
        for ( int i = 0; i < times.length; i++ ) {
            // if TInstant
            if ( times[i] instanceof TInstant ) {
                Operation op = new PropertyIsCOMPOperation(
                                                            OperationDefines.PROPERTYISEQUALTO,
                                                            new PropertyName( timePropertyName ),
                                                            new Literal(
                                                                         ( (TInstant) times[i] ).getTPosition() ) );
                timeOperationList.add( op );
                // if TPeriod
            } else if ( times[i] instanceof TPeriod ) {

                ArrayList al = new ArrayList();
                Operation op = new PropertyIsCOMPOperation(
                                                            OperationDefines.PROPERTYISGREATERTHANOREQUALTO,
                                                            new PropertyName( timePropertyName ),
                                                            new Literal(
                                                                         ( (TPeriod) times[i] ).getBegin() ) );
                al.add( op );
                op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISLESSTHANOREQUALTO,
                                                  new PropertyName( timePropertyName ),
                                                  new Literal( ( (TPeriod) times[i] ).getEnd() ) );
                al.add( op );
                timeOperationList.add( new LogicalOperation( OperationDefines.AND, al ) );
            }
        }

        Operation timeOp = null;
        // connect time operations by <or>
        if ( timeOperationList.size() == 1 ) {
            timeOp = (Operation) timeOperationList.get( 0 );
        } else if ( timeOperationList.size() > 1 ) {
            timeOp = new LogicalOperation( OperationDefines.OR, timeOperationList );
        }

        // sets the filter by operations
        if ( ( timeOp != null ) && ( filterOperation != null ) ) {
            ArrayList operationList = new ArrayList();
            operationList.add( timeOp );
            operationList.add( filterOperation );

            Filter filter = new ComplexFilter( new LogicalOperation( OperationDefines.AND,
                                                                     operationList ) );
            WFSRequestGenerator.setFilter( request, filter );
        } else if ( timeOp != null ) {
            WFSRequestGenerator.setFilter( request, new ComplexFilter( timeOp ) );
        } else if ( filterOperation != null ) {
            WFSRequestGenerator.setFilter( request, new ComplexFilter( filterOperation ) );
        }

        return request;
    }

}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSRequestGenerator.java,v $
 Revision 1.13  2006/08/24 06:42:16  poth
 File header corrected

 Revision 1.12  2006/08/07 12:25:14  poth
 never thrown exceptions removed / never read variable removed

 Revision 1.11  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */
