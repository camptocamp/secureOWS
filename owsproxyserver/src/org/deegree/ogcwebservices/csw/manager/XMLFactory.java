//$Header$
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
package org.deegree.ogcwebservices.csw.manager;

import java.net.URI;
import java.util.List;

import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version 1.0. $Revision$, $Date$
 * 
 * @since 2.0
 */
public class XMLFactory {

    private static final URI CSWNS = CommonNamespaces.CSWNS;

    /**
     * returns a XML representation of a
     * 
     * @see Transaction object
     * 
     * @param transaction
     * @return
     * @throws XMLException
     * @throws OGCWebServiceException
     */
    public static final TransactionDocument export( Transaction transaction )
                            throws XMLParsingException, OGCWebServiceException {

        TransactionDocument transDoc = new TransactionDocument();
        try {
            transDoc.createEmptyDocument();
        } catch ( Exception e ) {
            throw new XMLParsingException( e.getMessage() );
        }

        transDoc.getRootElement().setAttribute( "service", "CSW" );
        transDoc.getRootElement().setAttribute( "version", transaction.getVersion() );
        transDoc.getRootElement().setAttribute( "verboseResponse",
                                                "" + transaction.verboseResponse() );

        List<Operation> ops = transaction.getOperations();
        for ( int i = 0; i < ops.size(); i++ ) {
            Operation operation = ops.get( i );
            appendOperation( transDoc.getRootElement(), operation );
        }

        return transDoc;

    }

    /**
     * returns a XML representation of a
     * 
     * @see TransactionResponse object
     * 
     * @param response
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     */
    public static final HarvetResultDocument export( HarvestResult response )
                            throws XMLParsingException {

        HarvetResultDocument harvestRespDoc = new HarvetResultDocument();
        try {
            harvestRespDoc.createEmptyDocument();
        } catch ( Exception e ) {
            throw new XMLParsingException( e.getMessage() );
        }

        Element root = harvestRespDoc.getRootElement();
        root.setAttribute( "version", response.getRequest().getVersion() );

        Element elem = XMLTools.appendElement( root, CommonNamespaces.CNTXTNS,
                                               "csw:TransactionSummary" );
        root.appendChild( elem );
        XMLTools.appendElement( elem, CommonNamespaces.CNTXTNS, "csw:totalInserted",
                                Integer.toString( response.getTotalInserted() ) );
        XMLTools.appendElement( elem, CommonNamespaces.CNTXTNS, "csw:totalUpdated",
                                Integer.toString( response.getTotalUpdated() ) );
        XMLTools.appendElement( elem, CommonNamespaces.CNTXTNS, "csw:totalDeleted",
                                Integer.toString( response.getTotalDeleted() ) );

        List<Node> records = response.getResults().getRecords();
        if ( records.size() > 0 ) {
            elem = XMLTools.appendElement( root, CommonNamespaces.CNTXTNS, "csw:InsertResult" );
            for ( int i = 0; i < records.size(); i++ ) {
                XMLTools.insertNodeInto( records.get( i ), elem );
            }
            root.appendChild( elem );
        }

        return harvestRespDoc;
    }

    /**
     * returns a XML representation of a
     * 
     * @see TransactionResponse object
     * 
     * @param response
     * @throws XMLParsingException
     * @throws OGCWebServiceException
     */
    public static final TransactionResultDocument export( TransactionResult response )
                            throws XMLParsingException {

        TransactionResultDocument transRespDoc = new TransactionResultDocument();
        try {
            transRespDoc.createEmptyDocument();
        } catch ( Exception e ) {
            throw new XMLParsingException( e.getMessage() );
        }

        Element root = transRespDoc.getRootElement();
        root.setAttribute( "version", response.getRequest().getVersion() );

        Element elem = XMLTools.appendElement( root, CommonNamespaces.CNTXTNS,
                                               "csw:TransactionSummary" );
        root.appendChild( elem );
        XMLTools.appendElement( elem, CommonNamespaces.CNTXTNS, "csw:totalInserted",
                                Integer.toString( response.getTotalInserted() ) );
        XMLTools.appendElement( elem, CommonNamespaces.CNTXTNS, "csw:totalUpdated",
                                Integer.toString( response.getTotalUpdated() ) );
        XMLTools.appendElement( elem, CommonNamespaces.CNTXTNS, "csw:totalDeleted",
                                Integer.toString( response.getTotalDeleted() ) );

        List<Node> records = response.getResults().getRecords();
        if ( records.size() > 0 ) {
            elem = XMLTools.appendElement( root, CommonNamespaces.CNTXTNS, "csw:InsertResult" );
            for ( int i = 0; i < records.size(); i++ ) {
                XMLTools.insertNodeInto( records.get( i ), elem );
            }
            root.appendChild( elem );
        }

        return transRespDoc;

    }

    /**
     * 
     * @param root
     * @param operation
     * @throws OGCWebServiceException
     */
    private static void appendOperation( Element root, Operation operation )
                            throws OGCWebServiceException {

        if ( "Insert".equals( operation.getName() ) ) {
            appendInsert( root, (Insert) operation );
        } else if ( "Update".equals( operation.getName() ) ) {
            appendUpdate( root, (Update) operation );
        } else if ( "Delete".equals( operation.getName() ) ) {
            appendDelete( root, (Delete) operation );
        } else {
            throw new OGCWebServiceException( "unknown CS-W transaction operation: "
                                              + operation.getName() );
        }
    }

    /**
     * appends an Delete operation to the passed root element
     * 
     * @param root
     * @param delete
     */
    private static void appendDelete( Element root, Delete delete ) {
        Document doc = root.getOwnerDocument();
        Element op = doc.createElementNS( CSWNS.toASCIIString(), "csw:" + delete.getName() );
        if ( delete.getHandle() != null ) {
            op.setAttribute( "handle", delete.getHandle() );
        }
        if ( delete.getTypeName() != null ) {
            op.setAttribute( "typeName", delete.getTypeName().toASCIIString() );
        }

        Filter filter = delete.getConstraint();
        Element constraint = doc.createElementNS( CSWNS.toASCIIString(), "csw:Constraint" );
        constraint.setAttribute( "version", "1.0.0" );
        op.appendChild( constraint );
        org.deegree.model.filterencoding.XMLFactory.appendFilter( constraint, filter );
        root.appendChild( op );

    }

    /**
     * appends an Update operation to the passed root element
     * 
     * @param root
     * @param update
     */
    private static void appendUpdate( Element root, Update update ) {
        Document doc = root.getOwnerDocument();
        Element op = doc.createElementNS( CSWNS.toASCIIString(), "csw:" + update.getName() );
        if ( update.getHandle() != null ) {
            op.setAttribute( "handle", update.getHandle() );
        }
        if ( update.getTypeName() != null ) {
            op.setAttribute( "typeName", update.getTypeName().toASCIIString() );
        }
        XMLTools.insertNodeInto( update.getRecord(), op );
        Filter filter = update.getConstraint();
        Element constraint = doc.createElementNS( CSWNS.toASCIIString(), "csw:Constraint" );
        constraint.setAttribute( "version", "1.0.0" );
        op.appendChild( constraint );
        org.deegree.model.filterencoding.XMLFactory.appendFilter( constraint, filter );
        root.appendChild( op );
    }

    /**
     * appends an Insert operation to the passed root element
     * 
     * @param root
     * @param insert
     */
    private static void appendInsert( Element root, Insert insert ) {
        Document doc = root.getOwnerDocument();
        Element op = doc.createElementNS( CSWNS.toASCIIString(), "csw:" + insert.getName() );
        if ( insert.getHandle() != null ) {
            op.setAttribute( "handle", insert.getHandle() );
        }
        List<Element> list = insert.getRecords();
        for ( int i = 0; i < list.size(); i++ ) {
            XMLTools.insertNodeInto( list.get( i ), op );
        }
        root.appendChild( op );

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log$
 * Changes to this class. What the people have been up to: Revision 1.11  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/11/22 21:25:07  poth
 * Changes to this class. What the people have been up to: bug fix - setting mandatory Constraint@version attribute
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/10/13 14:22:08  poth
 * Changes to this class. What the people have been up to: changes required because of extending TransactionResult from DefaultOGCWebServiceResponse
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/08/29 19:54:14  poth
 * Changes to this class. What the people have been up to: footer corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/04/06 20:25:27  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/04/04 20:39:43  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Revision 1.5 2006/03/30 21:20:26
 * poth ** empty log message ***
 * 
 * Revision 1.4 2006/03/27 20:16:03 poth ** empty log message ***
 * 
 * Revision 1.3 2006/03/25 15:58:36 poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/20 14:14:00 poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/20 09:30:18 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/
