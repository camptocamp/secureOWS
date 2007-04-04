//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/manager/TransactionResultDocument.java,v 1.8 2006/10/13 14:22:08 poth Exp $
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * 
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/10/13 14:22:08 $
 * 
 * @since 2.0
 */
public class TransactionResultDocument extends XMLFragment {

    private static final ILogger LOG = LoggerFactory.getLogger( TransactionDocument.class );

    private NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    /**
     * initializes an empty TransactionDocument
     * 
     */
    public TransactionResultDocument() {
        try {
            setSystemId( XMLFragment.DEFAULT_URL );
        } catch ( MalformedURLException e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * 
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument() {
        Document doc = XMLTools.create();
        Element root = doc.createElementNS( CommonNamespaces.CSWNS.toASCIIString(),
                                            "csw:TransactionResponse" );
        setRootElement( root );

    }

    /**
     * parses a CS-W TransactionResponse document and creates a jave class representation from it.
     * 
     * @return
     * @throws XMLParsingException
     */
    public TransactionResult parseTransactionResponse( Transaction transaction )
                            throws XMLParsingException {

        Element root = getRootElement();
        int inserted = XMLTools.getNodeAsInt( root, "./csw:TransactionSummary/csw:totalInserted",
                                              nsc, 0 );
        int deleted = XMLTools.getNodeAsInt( root, "./csw:TransactionSummary/csw:totalDeleted",
                                             nsc, 0 );
        int updated = XMLTools.getNodeAsInt( root, "./csw:TransactionSummary/csw:totalUpdated",
                                             nsc, 0 );

        List list = XMLTools.getNodes( root, "./csw:InsertResult/child::*", nsc );
        List<Node> records = new ArrayList<Node>( list.size() );
        for ( int i = 0; i < list.size(); i++ ) {
            records.add( (Node) list.get( i ) );
        }
        InsertResults ir = new InsertResults( records );

        return new TransactionResult( transaction, inserted, deleted, updated, ir );

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: TransactionResultDocument.java,v $
 * Revision 1.8  2006/10/13 14:22:08  poth
 * changes required because of extending TransactionResult from DefaultOGCWebServiceResponse
 *
 * Revision 1.7  2006/08/08 09:24:54  poth
 * log statement added / footer corrected
 *
 * Revision 1.6  2006/04/06 20:25:27  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.5  2006/04/04 20:39:43  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.4 2006/04/04 10:22:02 poth ** empty log message ***
 * 
 * Revision 1.3 2006/03/30 21:20:26 poth ** empty log message ***
 * 
 * Revision 1.2 2006/03/27 20:16:03 poth ** empty log message ***
 * 
 * Revision 1.1 2006/03/25 15:58:36 poth ** empty log message ***
 * 
 * Revision 1.3 2006/02/21 19:47:49 poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/20 14:14:00 poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/20 12:40:02 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/