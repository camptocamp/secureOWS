//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/OWSCommonCapabilitiesDocument.java,v 1.4 2006/11/03 12:06:10 schmitz Exp $
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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.owscommon_new;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.Constraints;
import org.deegree.model.metadata.iso19115.ISO19115Document;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wms.XMLFactory;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.w3c.dom.Element;

/**
 * <code>OWSCommonCapabilitiesDocument</code> is the parser class for the
 * <code>OWSCommonCapabilities</code> data class.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/11/03 12:06:10 $
 * 
 * @since 2.0
 */

public class OWSCommonCapabilitiesDocument extends XMLFragment {

    private static final long serialVersionUID = 5069379023892304513L;

    private static final String POWS = CommonNamespaces.OWS_PREFIX + ":";

    private ISO19115Document iso = new ISO19115Document();

    /**
     * @param contents the service specific contents
     * @return the data object containing all parsed information.
     * @throws XMLParsingException 
     */
    public OWSCommonCapabilities parseCapabilities( Map<String, Content> contents )
                            throws XMLParsingException {
        Element root = getRootElement();

        String version = XMLTools.getRequiredNodeAsString( root, "@version", nsContext );
        String updateSequence = XMLTools.getNodeAsString( root, "@updateSequence", nsContext, null );

        Element elem = (Element) XMLTools.getNode( root, POWS + "ServiceIdentification", nsContext );
        ServiceIdentification serviceIdentification = null;
        if ( elem != null )
            serviceIdentification = parseServiceIdentification( elem );

        elem = (Element) XMLTools.getNode( root, POWS + "ServiceProvider", nsContext );
        ServiceProvider serviceProvider = null;
        if ( elem != null )
            serviceProvider = parseServiceProvider( elem );

        OperationsMetadata operationsMetadata = null;
        elem = (Element) XMLTools.getNode( root, POWS + "OperationsMetadata", nsContext );
        if ( elem != null )
            operationsMetadata = parseOperationsMetadata( elem );

        OWSCommonCapabilities capabilities = new OWSCommonCapabilities( version, updateSequence,
                                                                        serviceIdentification,
                                                                        serviceProvider,
                                                                        operationsMetadata,
                                                                        contents );
        return capabilities;
    }

    private OperationsMetadata parseOperationsMetadata( Element root )
                            throws XMLParsingException {
        List<Element> operationElements = XMLTools.getRequiredNodes( root, POWS + "Operation",
                                                                     nsContext );
        if ( operationElements.size() < 2 ) {
            throw new XMLParsingException( "Too few operations defined in the Operations"
                                           + "Metadata element." );
        }
        
        List<Operation> operations = new ArrayList<Operation>();
        for ( Element element : operationElements ) {
            operations.add( parseOperation( element ) );
        }

        List<Element> parameterElements = XMLTools.getNodes( root, POWS + "Parameter", nsContext );
        List<Parameter> parameters = new ArrayList<Parameter>();

        for ( Element parameter : parameterElements ) {
            parameters.add( parseDomainType( parameter, true, true, false, false ) );
        }

        List<Element> constraintElements = XMLTools.getNodes( root, POWS + "Constraint", nsContext );
        List<DomainType> constraints = new ArrayList<DomainType>();

        for ( Element constraint : constraintElements ) {
            constraints.add( parseDomainType( constraint, true, true, false, false ) );
        }

        // extended capabilities are ignored for now

        OperationsMetadata result = new OperationsMetadata( parameters, constraints, operations,
                                                            null );
        return result;
    }

    private Operation parseOperation( Element root )
                            throws XMLParsingException {
        QualifiedName name = XMLTools.getRequiredNodeAsQualifiedName( root, "@name", nsContext );

        List<DCP> dcps = parseDCPs( root );

        List<Element> parameterElements = XMLTools.getNodes( root, POWS + "Parameter", nsContext );
        List<Parameter> parameters = new ArrayList<Parameter>();

        for ( Element parameter : parameterElements )
            parameters.add( parseDomainType( parameter, true, true, false, false ) );

        List<Element> constraintElements = XMLTools.getNodes( root, POWS + "Constraint", nsContext );
        List<DomainType> constraints = new ArrayList<DomainType>();

        for ( Element constraint : constraintElements )
            constraints.add( parseDomainType( constraint, true, true, false, false ) );

        List<Element> metadataElements = XMLTools.getNodes( root, POWS + "Metadata", nsContext );
        List<Metadata> metadatas = new ArrayList<Metadata>();

        for ( Element metadata : metadataElements )
            metadatas.add( parseMetadata( metadata ) );

        Operation result = new Operation( name, dcps, parameters, constraints, metadatas, "n/a" );
        return result;
    }

    // parameter should be the element containing the DCP element
    private List<DCP> parseDCPs( Element root )
                            throws XMLParsingException {
        List<DCP> result = new ArrayList<DCP>();
        List<Element> dcps = XMLTools.getNodes( root, POWS + "DCP", nsContext );

        for ( Element dcp : dcps )
            result.add( parseHTTP( dcp ) );

        return result;
    }

    private HTTP parseHTTP( Element root )
                            throws XMLParsingException {

        List<Element> get = XMLTools.getNodes( root, POWS + "HTTP/" + POWS + "Get", nsContext );
        List<Element> post = XMLTools.getNodes( root, POWS + "HTTP/" + POWS + "Post", nsContext );

        if ( ( get.size() + post.size() ) == 0 )
            throw new XMLParsingException( "At least one of Get or Post "
                                           + "must be specified under DCP/HTTP." );

        List<HTTP.Type> types = new ArrayList<HTTP.Type>();
        List<OnlineResource> links = new ArrayList<OnlineResource>();
        List<List<DomainType>> constraints = new ArrayList<List<DomainType>>();

        for ( Element elem : get ) {
            OnlineResource link = iso.parseOnlineResource( elem );
            List<Element> constraintElements = XMLTools.getNodes( elem, POWS + "Constraint",
                                                                  nsContext );
            List<DomainType> myConstr = new ArrayList<DomainType>();
            for ( Element constraint : constraintElements )
                myConstr.add( parseDomainType( constraint, true, true, false, false ) );

            types.add( HTTP.Type.Get );
            constraints.add( myConstr );
            links.add( link );
        }

        for ( Element elem : post ) {
            OnlineResource link = iso.parseOnlineResource( elem );
            List<Element> constraintElements = XMLTools.getNodes( elem, POWS + "Constraint",
                                                                  nsContext );
            List<DomainType> myConstr = new ArrayList<DomainType>();
            for ( Element constraint : constraintElements )
                myConstr.add( parseDomainType( constraint, true, true, false, false ) );

            types.add( HTTP.Type.Post );
            constraints.add( myConstr );
            links.add( link );
        }

        return new HTTP( links, constraints, types );
    }

    private Metadata parseMetadata( Element root )
                            throws XMLParsingException {
        SimpleLink link = parseSimpleLink( root );
        URI about = XMLTools.getNodeAsURI( root, "@about", nsContext, null );
        return new Metadata( link, about, null );
    }

    private DomainType parseDomainType( Element root, boolean optional, boolean repeatable,
                                       boolean noValuesAllowed, boolean anyValueAllowed )
                            throws XMLParsingException {
        String[] valueArray = XMLTools.getRequiredNodesAsStrings( root, POWS + "Value", nsContext );
        List<TypedLiteral> values = new ArrayList<TypedLiteral>();
        URI stringURI = null;
        try {
            stringURI = new URI( null, "String", null );
        } catch ( URISyntaxException e ) {
            // cannot happen, why do I have to catch this?
        }
        for ( String value : valueArray )
            values.add( new TypedLiteral( value, stringURI ) );

        List<Element> metadataElements = XMLTools.getNodes( root, POWS + "Metadata", nsContext );
        List<Metadata> metadata = new ArrayList<Metadata>();

        for ( Element element : metadataElements )
            metadata.add( parseMetadata( element ) );

        QualifiedName name = XMLTools.getRequiredNodeAsQualifiedName( root, "@name", nsContext );

        DomainType result = new DomainType( optional, repeatable, "n/a", 0, name, values, null,
                                            null, anyValueAllowed, null, noValuesAllowed, null,
                                            null, null, metadata );
        return result;
    }

    private ServiceProvider parseServiceProvider( Element root )
                            throws XMLParsingException {
        String providerName = XMLTools.getRequiredNodeAsString( root, POWS + "ProviderName",
                                                                nsContext );

        OnlineResource providerSite = null;
        Element siteElement = (Element) XMLTools.getNode( root, POWS + "ProviderSite", nsContext );
        if ( siteElement != null )
            providerSite = iso.parseOnlineResource( siteElement );

        Element partyElement = (Element) XMLTools.getRequiredNode( root, POWS + "ServiceContact",
                                                                   nsContext );
        CitedResponsibleParty party = iso.parseCitedResponsibleParty( partyElement );

        ServiceProvider result = new ServiceProvider( providerName, providerSite, party );
        return result;
    }

    private ServiceIdentification parseServiceIdentification( Element root )
                            throws XMLParsingException {
        Element elem = (Element) XMLTools.getRequiredNode( root, POWS + "ServiceType", nsContext );
        Code serviceType = iso.parseCode( elem );

        List<String> versions = Arrays.asList( XMLTools.getRequiredNodesAsStrings(
                                                                                   root,
                                                                                   POWS
                                                                                                           + "ServiceTypeVersion",
                                                                                   nsContext ) );

        String fee = XMLTools.getNodeAsString( root, POWS + "Fees", nsContext, null );

        List<Element> constraintElements = XMLTools.getNodes( root, POWS + "AccessConstraints",
                                                              nsContext );
        List<Constraints> constraints = new ArrayList<Constraints>();

        for ( Element constraint : constraintElements )
            constraints.add( iso.parseConstraint( constraint, fee ) );

        String title = XMLTools.getNodeAsString( root, POWS + "Title", nsContext, null );
        String abstractString = XMLTools.getNodeAsString( root, POWS + "Abstract", nsContext, null );

        List<Element> keywordsElements = XMLTools.getNodes( root, POWS + "Keywords", nsContext );
        List<Keywords> keywords = new ArrayList<Keywords>();
        for ( Element keyword : keywordsElements )
            keywords.add( iso.parseKeywords( keyword ) );

        // the next one is an extension
        List<String> alternativeTitles = Arrays.asList( XMLTools.getNodesAsStrings(
                                                                                    root,
                                                                                    POWS
                                                                                                            + "AlternateTitle",
                                                                                    nsContext ) );

        Date date = new Date( System.currentTimeMillis() );

        String identification = title;

        ServiceIdentification result = new ServiceIdentification( serviceType, versions, title,
                                                                  alternativeTitles, date,
                                                                  identification, abstractString,
                                                                  keywords, constraints );
        return result;
    }

    // just for some quick testing
    public static void main( String[] args )
                            throws Exception {
        WMSCapabilitiesDocument doc = new WMSCapabilitiesDocument();
        doc.load( new File( "/tmp/caps.xml" ).toURL() );
        WMSCapabilities cap = (WMSCapabilities) doc.parseCapabilities();
        WMSCapabilitiesDocument doc2 = XMLFactory.export( cap );
        doc2.prettyPrint( System.out );
        //        OWSCommonCapabilitiesDocument doc = new OWSCommonCapabilitiesDocument();
        //        doc.load( new File( "/tmp/caps.xml" ).toURL() );
        //        OWSCommonCapabilities cap = doc.parseCapabilities( null );
        //        WASCapabilitiesDocument doc2 = new WASCapabilitiesDocument();
        //        doc2.createEmptyDocument();
        //        XMLFactory.appendBaseCapabilities( doc2.getRootElement(), cap );
        //        doc2.prettyPrint( new FileOutputStream( "/tmp/capneu.xml" ) );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: OWSCommonCapabilitiesDocument.java,v $
 Revision 1.4  2006/11/03 12:06:10  schmitz
 Fixed some style guide issues.

 Revision 1.3  2006/09/08 08:42:02  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.2  2006/08/24 06:43:04  poth
 File header corrected

 Revision 1.1  2006/08/23 07:10:21  schmitz
 Renamed the owscommon_neu package to owscommon_new.

 Revision 1.3  2006/08/22 10:25:01  schmitz
 Updated the WMS to use the new OWS common package.
 Updated the rest of deegree to use the new data classes returned
 by the updated WMS methods/capabilities.

 Revision 1.2  2006/08/08 10:21:52  schmitz
 Parser is finished, as well as the iso XMLFactory.

 Revision 1.1  2006/08/04 15:16:26  schmitz
 Half the OWS common 1.0.0 parser is finished. Data classes should be complete.



 ********************************************************************** */