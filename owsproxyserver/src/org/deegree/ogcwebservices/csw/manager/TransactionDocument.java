//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/manager/TransactionDocument.java,v 1.12 2006/11/22 21:25:07 poth Exp $
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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The Transaction operation defines an interface for creating, modifying and deleting catalogue
 * records. The specific payload being manipulated must be defined in a profile.
 * 
 * @version $Revision: 1.12 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.12 $, $Date: 2006/11/22 21:25:07 $
 * 
 * @since 2.0
 */
public class TransactionDocument extends XMLFragment {

    private static final long serialVersionUID = 7914686453810419662L;

    protected static final ILogger LOG = LoggerFactory.getLogger( TransactionDocument.class );

    private NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    /**
     * initializes an empty TransactionDocument
     * 
     */
    public TransactionDocument() {
        try {
            setSystemId( XMLFragment.DEFAULT_URL );
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
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
                                            "csw:Transaction" );
        setRootElement( root );

    }

    /**
     * initializes a TransactionDocument by reading a DOM object from the passed
     * 
     * @see InputStream
     * 
     * @param transRoot
     * @throws XMLException
     * @throws IOException
     */
    public TransactionDocument( Element transRoot ) throws XMLException, IOException {
        setRootElement( transRoot );
        setSystemId( XMLFragment.DEFAULT_URL );
    }

    /**
     * parses a CS-W 2.0 transaction request
     * 
     * @return
     * @throws XMLParsingException
     * @throws InvalidParameterValueException 
     * @throws MissingParameterValueException 
     */
    public Transaction parse( String id )
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException {

        LOG.logDebug( "parsing CS-W Transaction request" );
        String version = XMLTools.getAttrValue( getRootElement(), "version" );
        boolean verbose = XMLTools.getNodeAsBoolean( getRootElement(), "./@verboseResponse", nsc,
                                                     false );

        List<Operation> ops = new ArrayList<Operation>();

        ElementList el = XMLTools.getChildElements( getRootElement() );
        for ( int i = 0; i < el.getLength(); i++ ) {
            Element e = el.item( i );
            // TODO check for qualified name
            if ( "Insert".equals( e.getLocalName() ) ) {
                ops.add( parseInsert( e ) );
            } else if ( "Update".equals( e.getLocalName() ) ) {
                ops.add( parseUpdate( e ) );
            } else if ( "Delete".equals( e.getLocalName() ) ) {
                ops.add( parseDelete( e ) );
            }
        }

        return new Transaction( version, id, null, ops, verbose );
    }

    /**
     * parses a Delete element contained in a CS-W Transaction.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws MissingParameterValueException 
     * @throws InvalidParameterValueException 
     */
    private Delete parseDelete( Element element )
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException {

        LOG.logDebug( "parsing CS-W Transaction-Delete" );

        String handle = XMLTools.getAttrValue( element, "handle" );
        String tmp = XMLTools.getAttrValue( element, "typeName" );
        URI typeName = null;
        if ( tmp != null ) {
            // part of the corrected CS-W 2.0 spec
            try {
                typeName = new URI( tmp );
            } catch ( Exception e ) {
                throw new XMLParsingException( "if defined attribute 'typeName' must be "
                                               + "a valid URI" );
            }
        }

        Element elem = (Element) XMLTools.getRequiredNode( element, "./csw:Constraint", nsc );
        String ver = XMLTools.getAttrValue( elem, "version" );
        if ( ver == null ) {
            String s = Messages.getMessage( "CSW_MISSING_CONSTRAINT_VERSION" );
            throw new MissingParameterValueException( s );
        }
        if ( !"1.0.0".equals( ver ) && !"1.1.0".equals( ver ) ) {
            String s = Messages.getMessage( "CSW_INVALID_CONSTRAINT_VERSION", ver );
            throw new InvalidParameterValueException( s );
        }

        elem = (Element) XMLTools.getRequiredNode( elem, "./ogc:Filter", nsc );

        Filter constraint = AbstractFilter.buildFromDOM( elem );
        return new Delete( handle, typeName, constraint );
    }

    /**
     * parses a Update element contained in a CS-W Transaction.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws MissingParameterValueException 
     * @throws InvalidParameterValueException 
     */
    private Update parseUpdate( Element element )
                            throws XMLParsingException, MissingParameterValueException,
                            InvalidParameterValueException {

        LOG.logDebug( "parsing CS-W Transaction-Update" );

        String handle = XMLTools.getAttrValue( element, "handle" );
        String tmp = XMLTools.getAttrValue( element, "typeName" );
        URI typeName = null;
        if ( tmp != null ) {
            // part of the corrected CS-W 2.0 spec
            try {
                typeName = new URI( tmp );
            } catch ( Exception e ) {
                throw new XMLParsingException( "if defined attribute 'typeName' must be a valid URI" );
            }
        }
        Element elem = (Element) XMLTools.getRequiredNode( element, "./csw:Constraint", nsc );
        String ver = XMLTools.getAttrValue( elem, "version" );
        if ( ver == null ) {
            String s = Messages.getMessage( "CSW_MISSING_CONSTRAINT_VERSION" );
            throw new MissingParameterValueException( s );
        }
        if ( !"1.0.0".equals( ver ) && !"1.1.0".equals( ver ) ) {
            String s = Messages.getMessage( "CSW_INVALID_CONSTRAINT_VERSION", ver );
            throw new InvalidParameterValueException( s );
        }

        elem = (Element) XMLTools.getRequiredNode( elem, "./ogc:Filter", nsc );

        Filter constraint = AbstractFilter.buildFromDOM( elem );

        List children = null;
        List rp = XMLTools.getNodes( getRootElement(), "./csw:RecordProperty", nsc );
        if ( rp.size() != 0 ) {
            // at the moment will always be null because it is part of the
            // CS-W 2.0 corrected version that will not be implemented yet
        } else {
            children = XMLTools.getNodes( element, "./child::*", nsc );
            if ( children.size() == 0 ) {
                throw new XMLParsingException( "one record must be defined within a CS-W update element" );
            }
        }
        return new Update( handle, typeName, constraint, (Element) children.get( 0 ), null );
    }

    /**
     * parses a Insert element contained in a CS-W Transaction.
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    private Insert parseInsert( Element element )
                            throws XMLParsingException {

        LOG.logDebug( "parsing CS-W Transaction-Insert" );

        String handle = XMLTools.getAttrValue( element, "handle" );
        List children = XMLTools.getNodes( element, "./child::*", nsc );
        if ( children.size() == 0 ) {
            LOG.logError( "at least one record must be defined within a CS-W insert element" );
            throw new XMLParsingException( "at least one record must be defined "
                                           + "within a CS-W insert element" );
        }
        List<Element> recList = new ArrayList<Element>( children.size() );
        for ( Iterator iterator = children.iterator(); iterator.hasNext(); ) {
            recList.add( (Element) iterator.next() );
        }
        return new Insert( handle, recList );
    }
}

/* ***********************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: TransactionDocument.java,v $
 * Revision 1.12  2006/11/22 21:25:07  poth
 * bug fix - setting mandatory Constraint@version attribute
 *
 * Revision 1.11  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.10  2006/07/10 20:57:37  mschneider
 * Renamed parseTransaction() to parse() for unification.
 *
 * Revision 1.9  2006/07/10 15:00:52  poth
 * bug fix - creating operations
 *
 *  Revision 1.8  2006/07/06 17:37:10  mschneider
 *  Fixed Transaction parsing. Order of operations is now respected.
 * 
 *  Revision 1.7  2006/04/06 20:25:27  poth
 *  *** empty log message ***
 * 
 *  Revision 1.6  2006/04/04 20:39:43  poth
 *  *** empty log message ***
 *  Revision 1.5 2006/03/30 21:20:26
 * poth ** empty log message ***
 * 
 * Revision 1.4 2006/03/27 20:16:03 poth ** empty log message ***
 * 
 * Revision 1.3 2006/03/06 12:41:22 poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/20 09:30:18 poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/17 16:28:12 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 *********************************************************************************************** */
