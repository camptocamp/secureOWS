//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/TransactionDocument.java,v 1.13 2006/11/27 12:22:05 poth Exp $
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
package org.deegree.ogcwebservices.wfs.operation.transaction;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.feature.GMLFeatureDocument;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wfs.operation.AbstractWFSRequestDocument;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert.ID_GEN;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Parser for "wfs:Transaction" requests and contained elements.  
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.13 $, $Date: 2006/11/27 12:22:05 $
 */
public class TransactionDocument extends AbstractWFSRequestDocument {

    private static final long serialVersionUID = -394478447170286393L;

    /**
     * Parses the underlying document into a <code>Transaction</code> request object.
     * 
     * @param id
     * @return corresponding <code>Transaction</code> object
     * @throws XMLParsingException 
     * @throws InvalidParameterValueException 
     */
    public Transaction parse( String id )
                            throws XMLParsingException, InvalidParameterValueException {

        checkServiceAttribute();
        String version = checkVersionAttribute();

        Element root = this.getRootElement();
        String lockId = XMLTools.getNodeAsString( root, "wfs:LockId/text()", nsContext, null );
        boolean releaseAllFeatures = parseReleaseActionParameter();

        List<TransactionOperation> operations = new ArrayList<TransactionOperation>();
        List list = XMLTools.getNodes( root, "*", nsContext );
        for ( int i = 0; i < list.size(); i++ ) {
            Element element = (Element) list.get( i );
            TransactionOperation operation = parseOperation( element );
            operations.add( operation );
        }
        
        // vendorspecific attributes; required by deegree rights management
        Map<String, String> vendorSpecificParams = parseDRMParams( root );

        return new Transaction( id, version, vendorSpecificParams, lockId, operations, 
                                releaseAllFeatures, this );
    }

    /**
     * Parses the optional "releaseAction" attribute of the root element.
     * 
     * @return true, if releaseAction equals "ALL" (or is left out), false if it equals "SOME"
     * @throws InvalidParameterValueException if parameter 
     * @throws XMLParsingException
     */
    private boolean parseReleaseActionParameter()
                            throws InvalidParameterValueException, XMLParsingException {

        String releaseAction = XMLTools.getNodeAsString( getRootElement(), "@releaseAction",
                                                         nsContext, "ALL" );
        boolean releaseAllFeatures = true;
        if ( releaseAction != null ) {
            if ( "SOME".equals( releaseAction ) ) {
                releaseAllFeatures = false;
            } else if ( "ALL".equals( releaseAction ) ) {
                releaseAllFeatures = true;
            } else {
                throw new InvalidParameterValueException( "releaseAction", releaseAction );
            }
        }
        return releaseAllFeatures;
    }

    /**
     * Parses the given element as a <code>TransactionOperation</code>.
     * <p>
     * The given element must be one of the following:
     * <ul>
     * <li>wfs:Insert</li>
     * <li>wfs:Update</li>
     * <li>wfs:Delete</li>
     * <li>wfs:Native</li>
     * </ul>
     * 
     * @param element operation element
     * @return corresponding <code>TransactionOperation</code> object
     * @throws XMLParsingException
     */
    private TransactionOperation parseOperation( Element element )
                            throws XMLParsingException {

        TransactionOperation operation = null;

        if ( !element.getNamespaceURI().equals( CommonNamespaces.WFSNS.toString() ) ) {
            String msg = Messages.getMessage( "WFS_INVALID_OPERATION", element.getNodeName() );
            throw new XMLParsingException( msg );
        }
        if ( element.getLocalName().equals( "Insert" ) ) {
            operation = parseInsert( element );
        } else if ( element.getLocalName().equals( "Update" ) ) {
            operation = parseUpdate( element );
        } else if ( element.getLocalName().equals( "Delete" ) ) {
            operation = parseDelete( element );
        } else if ( element.getLocalName().equals( "Native" ) ) {
            operation = parseNative( element );
        } else {
            String msg = Messages.getMessage( "WFS_INVALID_OPERATION", element.getNodeName() );
            throw new XMLParsingException( msg );
        }

        return operation;
    }

    /**
     * Parses the given element as a "wfs:Insert" operation. 
     * 
     * @param element "wfs:Insert" operation
     * @return corresponding Insert object
     * @throws XMLParsingException 
     */
    private Insert parseInsert( Element element )
                            throws XMLParsingException {
        FeatureCollection fc = null;
        ID_GEN mode = parseIdGen( element );
        String handle = XMLTools.getNodeAsString( element, "@handle", nsContext, null );
        URI srsName = XMLTools.getNodeAsURI( element, "@srsName", nsContext, null );
        List childElementList = XMLTools.getRequiredNodes( element, "*", nsContext );

        // either one _gml:FeatureCollection element or any number of _gml:Feature elements
        boolean isFeatureCollection = isFeatureCollection( (Element) childElementList.get( 0 ) );

        if ( isFeatureCollection ) {
            LOG.logDebug( "Insert (FeatureCollection)" );
            GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument( false );
            doc.setRootElement( (Element) childElementList.get( 0 ) );
            doc.setSystemId( this.getSystemId() );
            fc = doc.parse();
        } else {
            LOG.logDebug( "Insert (Features)" );
            Feature[] features = new Feature[childElementList.size()];
            for ( int i = 0; i < childElementList.size(); i++ ) {
                try {
                    GMLFeatureDocument doc = new GMLFeatureDocument( false );
                    doc.setRootElement( (Element) childElementList.get( i ) );
                    doc.setSystemId( this.getSystemId() );
                    features[i] = doc.parseFeature();
                } catch ( Exception e ) {
                    throw new XMLParsingException( e.getMessage(), e );
                }
            }
            fc = FeatureFactory.createFeatureCollection( null, features );
        }

        Insert insert = new Insert( handle, mode, srsName, fc );
        return insert;
    }

    /**
     * Checks whether the given element is a (concrete) gml:_FeatureCollection element.
     * <p>
     * NOTE: This check is far from perfect. Instead of determining the type of the element by
     * inspecting the schema, the decision is made by checking for child elements with name
     * "gml:featureMember".
     * 
     * @param element
     *            potential gml:_FeatureCollection element
     * @return true, if the given element appears to be a gml:_FeatureCollection element, false
     *         otherwise
     * @throws XMLParsingException
     */
    private boolean isFeatureCollection( Element element )
                            throws XMLParsingException {
        boolean containsFeatureCollection = false;
        List nodeList = XMLTools.getNodes( element, "gml:featureMember", nsContext );
        if ( nodeList.size() > 0 ) {
            containsFeatureCollection = true;
        }
        return containsFeatureCollection;
    }

    /**
     * Parses the optional "idGen" attribute of the given "wfs:Insert" element.
     * 
     * @param element "wfs:Insert" element
     * @return "idGen" attribute code
     * @throws XMLParsingException
     */
    private ID_GEN parseIdGen( Element element )
                            throws XMLParsingException {
        ID_GEN mode;
        String idGen = XMLTools.getNodeAsString( element, "@idgen", nsContext,
                                                 Insert.ID_GEN_GENERATE_NEW_STRING );

        if ( Insert.ID_GEN_GENERATE_NEW_STRING.equals( idGen ) ) {
            mode = Insert.ID_GEN.GENERATE_NEW;
        } else if ( Insert.ID_GEN_USE_EXISTING_STRING.equals( idGen ) ) {
            mode = Insert.ID_GEN.USE_EXISTING;
        } else if ( Insert.ID_GEN_REPLACE_DUPLICATE_STRING.equals( idGen ) ) {
            mode = Insert.ID_GEN.REPLACE_DUPLICATE;
        } else {
            String msg = Messages.getMessage( "WFS_INVALID_IDGEN_VALUE", idGen,
                                              Insert.ID_GEN_GENERATE_NEW_STRING,
                                              Insert.ID_GEN_REPLACE_DUPLICATE_STRING,
                                              Insert.ID_GEN_USE_EXISTING_STRING );
            throw new XMLParsingException( msg );
        }
        return mode;
    }

    /**
     * Parses the given element as a "wfs:Update" operation. 
     * 
     * @param element "wfs:Update" operation
     * @return corresponding Update object
     * @throws XMLParsingException 
     */
    private Update parseUpdate( Element element )
                            throws XMLParsingException {

        Update update = null;
        String handle = XMLTools.getNodeAsString( element, "@handle", nsContext, null );
        QualifiedName typeName = XMLTools.getRequiredNodeAsQualifiedName( element, "@typeName",
                                                                          nsContext );

        Element filterElement = (Element) XMLTools.getNode( element, "ogc:Filter", nsContext );
        Filter filter = null;
        if ( filterElement != null ) {
            filter = AbstractFilter.buildFromDOM( filterElement );
        }

        List properties = XMLTools.getNodes( element, "wfs:Property", nsContext );
        if ( properties.size() > 0 ) {
            // "standard" update (specifies properties + their replacement values)
            LOG.logDebug( "Update (replacement Properties)" );
            Map<PropertyPath, Node> replacementProperties = new LinkedHashMap<PropertyPath, Node>();
            for ( int i = 0; i < properties.size(); i++ ) {
                Node propertyNode = (Node) properties.get( i );
                Text propertyNameNode = (Text) XMLTools.getRequiredNode( propertyNode,
                                                                         "wfs:Name/text()",
                                                                         nsContext );
                PropertyPath propertyName = parsePropertyPath( propertyNameNode );

                // TODO improve this
                Node valueNode = XMLTools.getNode( propertyNode, "wfs:Value/*", nsContext );
                if ( valueNode == null ) {
                    valueNode = XMLTools.getNode( propertyNode, "wfs:Value/text()", nsContext );
                }

                if ( replacementProperties.get( propertyName ) != null ) {
                    String msg = Messages.getMessage( "WFS_UPDATE_DUPLICATE_PROPERTY", handle,
                                                      propertyName );
                    throw new XMLParsingException( msg );
                }
                replacementProperties.put( propertyName, valueNode );
            }
            update = new Update( handle, typeName, replacementProperties, filter );
        } else {
            // deegree specific update (specifies a single replacement feature)
            LOG.logDebug( "Update (replacement Feature)" );
            Feature replacementFeature = null;
            List childElementList = XMLTools.getRequiredNodes( element, "*", nsContext );
            if ( ( filter == null && childElementList.size() != 1 )
                 || ( filter != null && childElementList.size() != 2 ) ) {
                String msg = Messages.getMessage( "WFS_UPDATE_FEATURE_REPLACE", handle );
                throw new XMLParsingException( msg );
            }
            try {
                GMLFeatureDocument doc = new GMLFeatureDocument( false );
                doc.setRootElement( (Element) childElementList.get( 0 ) );
                doc.setSystemId( this.getSystemId() );
                replacementFeature = doc.parseFeature();
            } catch ( Exception e ) {
                String msg = Messages.getMessage( "WFS_UPDATE_FEATURE_REPLACE", handle );
                throw new XMLParsingException( msg );
            }
            update = new Update( handle, typeName, replacementFeature, filter );
        }

        return update;
    }

    /**
     * Parses the given element as a "wfs:Delete" operation. 
     * 
     * @param element "wfs:Delete" operation
     * @return corresponding Delete object
     */
    private Delete parseDelete( Element element )
                            throws XMLParsingException {

        String handle = XMLTools.getNodeAsString( element, "@handle", nsContext, null );
        QualifiedName typeName = XMLTools.getRequiredNodeAsQualifiedName( element, "@typeName",
                                                                          nsContext );

        Element filterElement = (Element) XMLTools.getNode( element, "ogc:Filter", nsContext );
        Filter filter = null;
        if ( filterElement != null ) {
            filter = AbstractFilter.buildFromDOM( filterElement );
        }
        return new Delete( handle, typeName, filter );
    }

    /**
     * Parses the given element as a "wfs:Native" operation. 
     * 
     * @param element "wfs:Native" operation
     * @return corresponding Native object
     */
    private Native parseNative( Element element )
                            throws XMLParsingException {
        String handle = XMLTools.getNodeAsString( element, "@handle", nsContext, null );
        String vendorID = XMLTools.getRequiredNodeAsString( element, "@vendorId", nsContext );
        boolean safeToIgnore = XMLTools.getRequiredNodeAsBoolean( element, "@safeToIgnore",
                                                                  nsContext );
        return new Native( handle, element, vendorID, safeToIgnore );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TransactionDocument.java,v $
 Revision 1.13  2006/11/27 12:22:05  poth
 support for vendorspecific parameters: user, password and sessionid added

 Revision 1.12  2006/11/16 08:53:21  mschneider
 Merged messages from org.deegree.ogcwebservices.wfs and its subpackages.

 Revision 1.11  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.10  2006/10/05 16:58:55  poth
 required changes to support none-GML formated inserts and updates

 Revision 1.9  2006/09/14 00:01:20  mschneider
 Little corrections + javadoc fixes.

 Revision 1.8  2006/08/31 15:02:27  mschneider
 Deactivated guessing of simple types.

 Revision 1.7  2006/06/15 18:30:48  poth
 *** empty log message ***

 Revision 1.6  2006/06/07 17:19:40  mschneider
 Delegated version parsing to AbstractWFSRequestDocument.parseVersion(). Renamed parseTransaction() to parse().

 Revision 1.5  2006/06/06 17:09:02  mschneider
 Moved checkServiceParameter() to super class.

 Revision 1.4  2006/05/24 15:26:27  mschneider
 Use LinkedHashMap to keep insertion order of the property names to be replaced.

 Revision 1.3  2006/05/23 16:11:18  mschneider
 More work on update.

 Revision 1.2  2006/05/17 18:30:01  mschneider
 Fixed error in parsing of "wfs:Update" elements.

 Revision 1.1  2006/05/16 16:25:30  mschneider
 Moved transaction related classes from org.deegree.ogcwebservices.wfs.operation to org.deegree.ogcwebservices.wfs.operation.transaction.

 ********************************************************************** */