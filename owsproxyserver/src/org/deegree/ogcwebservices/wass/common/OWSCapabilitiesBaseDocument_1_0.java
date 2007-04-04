// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/csw/capabilities/CatalogCapabilitiesDocument.java,v
// 1.22 2004/08/05 15:40:08 ap Exp $
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
package org.deegree.ogcwebservices.wass.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.owscommon.OWSCommonCapabilitiesDocument;
import org.deegree.owscommon.OWSDomainType;
import org.deegree.owscommon.OWSMetadata;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The owscapabilities 1.0 specification parser. This class is able to parse the changes made in the
 * ows capabilites specifications 1.0.
 * 
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.10 $, $Date: 2006/07/12 14:46:19 $
 * 
 * @since 2.0
 */
public abstract class OWSCapabilitiesBaseDocument_1_0 extends OWSCommonCapabilitiesDocument {

    private static final long serialVersionUID = -3518136831402464466L;

    private static final ILogger LOG = LoggerFactory.getLogger( OWSCapabilitiesBaseDocument_1_0.class );

    private static final String PRE = CommonNamespaces.OWS_PREFIX + ":";

    /**
     * Creates an empty document loaded from the template given.
     * 
     * @param template
     *            the location/name of the template
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument( String template )
                            throws IOException, SAXException {
        LOG.entering();
        URL url = OWSCapabilitiesBaseDocument_1_0.class.getResource( template );
        if ( url == null ) {
            throw new IOException( Messages.format( "ogcwebservices.wass.ERROR_RESOURCE_NOT_FOUND",
                                                    template ) );
        }
        load( url );
        LOG.exiting();
    }

    /**
     * Returns the class representation for the <code>ServiceIdentification</code> section of the
     * document, according to the ows 1.0 spec.
     * 
     * @return class representation for the <code>ServiceIdentification</code> section
     * @throws XMLParsingException
     */
    public ServiceIdentification parseServiceIdentification()
                            throws XMLParsingException {
        LOG.entering();

        Element element = (Element) XMLTools.getRequiredNode( getRootElement(),
                                                              PRE + "ServiceIdentification",
                                                              nsContext );

        // 'ServiceType' element (mandatory)
        Element serviceTypeElement = (Element) XMLTools.getRequiredNode( element, PRE
                                                                                  + "ServiceType",
                                                                         nsContext );
        Code serviceType = null;
        try {
            String codeSpace = XMLTools.getAttrValue( serviceTypeElement, "codeSpace" );
            URI uri = codeSpace != null ? new URI( codeSpace ) : null;
            serviceType = new Code( XMLTools.getStringValue( serviceTypeElement ), uri );
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException(
                                           Messages.format(
                                                            "ogcwebservices.wass.ERROR_CODESPACE_NOT_URI",
                                                            new String[] {
                                                                          XMLTools.getAttrValue(
                                                                                                 serviceTypeElement,
                                                                                                 "codeSpace" ),
                                                                          OWSNS.toString() } ) );
        }

        // 'ServiceTypeVersion' elements (mandatory)
        String[] serviceTypeVersions = XMLTools.getRequiredNodeAsStrings(
                                                                          element,
                                                                          PRE
                                                                                                  + "ServiceTypeVersion",
                                                                          nsContext, ",;" );

        // 'Title' element (optional)
        String title = XMLTools.getNodeAsString(
                                                 element,
                                                 PRE + "Title",
                                                 nsContext,
                                                 Messages.getString( "ogcwebservices.wass.NO_TITLE" ) );

        // 'Abstract' element (optional)
        String serviceAbstract = XMLTools.getNodeAsString(
                                                           element,
                                                           PRE + "Abstract",
                                                           nsContext,
                                                           Messages.getString( "ogcwebservices.wass.NO_ABSTRACT_DOCUMENTATION" ) );

        // 'Keywords' elements (optional)
        List keywordsList = XMLTools.getNodes( element, PRE + "Keywords", nsContext );
        Keywords[] keywords = getKeywords( keywordsList );

        // 'Fees' element (optional)
        String fees = XMLTools.getNodeAsString( element, PRE + "Fees", nsContext, null );

        // 'AccessConstraints' elements (optional)
        String[] accessConstraints = XMLTools.getNodesAsStrings( element,
                                                                 PRE + "AccessConstraints",
                                                                 nsContext );

        ServiceIdentification serviceIdentification = new ServiceIdentification(
                                                                                 serviceType,
                                                                                 serviceTypeVersions,
                                                                                 title,
                                                                                 serviceAbstract,
                                                                                 keywords, fees,
                                                                                 accessConstraints );
        LOG.exiting();
        return serviceIdentification;
    }

    /**
     * Wrapper to make it consistent.
     * 
     * @return the ServiceProvider representation
     * @throws XMLParsingException
     */
    public ServiceProvider parseServiceProvider()
                            throws XMLParsingException {
        return getServiceProvider();
    }

    /**
     * @return the OperationsMetada representation
     * @throws XMLParsingException
     * @throws InvalidCapabilitiesException
     * @throws URISyntaxException
     */
    public OperationsMetadata_1_0 parseOperationsMetadata()
                            throws XMLParsingException, InvalidCapabilitiesException,
                            URISyntaxException {
        LOG.entering();
        Element root = (Element) XMLTools.getRequiredNode( getRootElement(),
                                                           PRE + "OperationsMetadata", nsContext );

        ArrayList<Operation_1_0> operations = new ArrayList<Operation_1_0>();

        List nl = XMLTools.getRequiredNodes( root, PRE + "Operation", nsContext );

        for ( int i = 0; i < nl.size(); ++i ) {
            operations.add( parseOperation( (Element) nl.get( i ) ) );
        }

        List parameterList = XMLTools.getNodes( root, PRE + "Parameter", nsContext );
        OWSDomainType[] parameters = new OWSDomainType[parameterList.size()];
        for ( int i = 0; i < parameters.length; i++ ) {
            parameters[i] = parseOWSDomainType_1_0( (Element) parameterList.get( i ) );
        }

        List constraintList = XMLTools.getNodes( root, PRE + "Constraint", nsContext );
        OWSDomainType[] constraints = new OWSDomainType[constraintList.size()];
        for ( int i = 0; i < constraints.length; i++ ) {
            constraints[i] = parseOWSDomainType_1_0( (Element) constraintList.get( i ) );
        }

        String extendedCapabilities = XMLTools.getNodeAsString( root, PRE + "ExtendedCapabilities",
                                                                nsContext, null );

        // find the !not specified in the gdinrw! describeuser
        Operation_1_0 describeUser = null;
        // and find the GetCapabilities operation too.
        Operation_1_0 getCapa = null;
        for ( int i = 0; i < operations.size(); ++i ) {
            if ( operations.get( i ).getName().equalsIgnoreCase( "describeUser" ) ) {
                describeUser = operations.get( i );
            }
            if ( operations.get( i ).getName().equalsIgnoreCase( "GetCapabilities" ) ) {
                getCapa = operations.get( i );
            }
        }

        if ( getCapa == null ) {
            throw new InvalidCapabilitiesException(
                                                    Messages.format(
                                                                     "ogcwebservices.wass.ERROR_CAPABILITIES_MISSING_REQUIRED_OPERATION",
                                                                     "GetCapabilities" ) );
        }
        if ( describeUser == null ) {
            throw new InvalidCapabilitiesException(
                                                    Messages.format(
                                                                     "ogcwebservices.wass.ERROR_CAPABILITIES_MISSING_REQUIRED_OPERATION",
                                                                     "DescribeUser" ) );
        }

        OperationsMetadata_1_0 om = new OperationsMetadata_1_0(
                                                                operations.toArray( new Operation_1_0[operations.size()] ),
                                                                parameters, constraints,
                                                                extendedCapabilities, describeUser,
                                                                getCapa );

        LOG.exiting();

        return om;
    }

    /**
     * @param root
     *            the root node of the Operation xml fragment.
     * @return The operation representation 1.0 conform
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    @SuppressWarnings("unchecked")
    public Operation_1_0 parseOperation( Element root )
                            throws XMLParsingException, URISyntaxException {
        LOG.entering();
        DCPType[] dcps = getDCPs( XMLTools.getRequiredNodes( root, PRE + "DCP", nsContext ) );

        String name = XMLTools.getAttrValue( root, "name" );

        List parameterList = XMLTools.getNodes( root, PRE + "Parameter", nsContext );
        OWSDomainType[] parameters = new OWSDomainType[parameterList.size()];
        for ( int i = 0; i < parameters.length; i++ ) {
            parameters[i] = parseOWSDomainType_1_0( (Element) parameterList.get( i ) );
        }

        List constraintList = XMLTools.getNodes( root, PRE + "Constraint", nsContext );
        OWSDomainType[] constraints = new OWSDomainType[constraintList.size()];
        for ( int i = 0; i < constraints.length; i++ ) {
            constraints[i] = parseOWSDomainType_1_0( (Element) constraintList.get( i ) );
        }

        List metadata = XMLTools.getNodes( root, PRE + "Metadata", nsContext );

        ArrayList<OWSMetadata> metadatas = new ArrayList<OWSMetadata>();

        for ( int i = 0; i < metadata.size(); ++i ) {
            metadatas.add( parseOWSMetadata( (Element) metadata.get( i ), name ) );
        }

        Operation_1_0 operation = new Operation_1_0(
                                                     name,
                                                     dcps,
                                                     parameters,
                                                     constraints,
                                                     (OWSMetadata[]) metadata.toArray( new OWSMetadata[metadata.size()] ) );

        LOG.exiting();
        return operation;
    }

    private OWSMetadata parseOWSMetadata( Element root, String name )
                            throws XMLParsingException, URISyntaxException {
        LOG.entering();
        SimpleLink link = parseSimpleLink( root );
        URI about = new URI( XMLTools.getAttrValue( root, "about" ) );

        // TODO: find out real name
        OWSMetadata md = new OWSMetadata( about, link, name );
        LOG.exiting();
        return md;
    }

    /**
     * @param element
     *            The element to get the Domaintype from.
     * @return the OWSDomainType representation.
     * @throws XMLParsingException
     * 
     */
    private OWSDomainType parseOWSDomainType_1_0( Element element )
                            throws XMLParsingException {
        return getOWSDomainType( null, element );
    }

    /**
     * @param nameSpace
     *            The namespace of the supportedauthenticationlist should be wss or was
     * @return ArrayList<SupportedAuthenticationMethod>
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    public ArrayList<SupportedAuthenticationMethod> parseSupportedAuthenticationMethods(
                                                                                        String nameSpace )
                            throws XMLParsingException, MalformedURLException {
        LOG.entering();
        Element capability = (Element) XMLTools.getRequiredNode( getRootElement(), nameSpace
                                                                                   + ":Capability",
                                                                 nsContext );
        Element methodList = (Element) XMLTools.getRequiredNode(
                                                                 capability,
                                                                 nameSpace
                                                                                         + ":SupportedAuthenticationMethodList",
                                                                 nsContext );
        ArrayList<SupportedAuthenticationMethod> result = new AuthenticationDocument().parseSupportedAuthenticationMethodList( methodList );
        LOG.exiting();
        return result;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSCapabilitiesBaseDocument_1_0.java,v $
Revision 1.10  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
