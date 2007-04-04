//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/XMLFactory.java,v 1.11 2006/06/27 13:10:47 bezema Exp $
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

package org.deegree.ogcwebservices.wass.common;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilities;
import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilitiesDocument;
import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilities;
import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilitiesDocument;
import org.deegree.owscommon.OWSDomainType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This is the XMLFactory for both the WAS and the WSS. Please note that it only works with the 1.0
 * version of the OWS base classes, mostly recognizable by the appendix _1_0.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/06/27 13:10:47 $
 * 
 * @since 2.0
 */

public class XMLFactory extends org.deegree.owscommon.XMLFactory {

    private static final URI WAS = CommonNamespaces.GDINRW_WAS;

    private static final String PWAS = CommonNamespaces.GDINRWWAS_PREFIX + ":";

    private static final URI WSS = CommonNamespaces.GDINRW_WSS;

    private static final String PWSS = CommonNamespaces.GDINRWWSS_PREFIX + ":";

    private static final URI OWS = CommonNamespaces.OWSNS;

    private static final String POWS = CommonNamespaces.OWS_PREFIX + ":";

    private static final URI AUTHN = CommonNamespaces.GDINRW_AUTH;

    private static final String PAUTHN = CommonNamespaces.GDINRW_AUTH_PREFIX + ":";

    private static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    /**
     * 
     * Exports the given WAS capabilities as XML document.
     * 
     * @param capabilities
     *            the data to export
     * @return the XMLFragment
     */
    public static WASCapabilitiesDocument export( WASCapabilities capabilities ) {
        LOG.entering();
        WASCapabilitiesDocument doc = new WASCapabilitiesDocument();

        try {
            doc.createEmptyDocument();
            Element root = doc.getRootElement();

            appendBaseCapabilities( capabilities, root );
            appendExtendedCapabilities( capabilities, root, WAS, PWAS );

        } catch ( SAXException e ) {
            LOG.logError( Messages.format( "ogcwebservices.wass.ERROR_XML_TEMPLATE_NOT_PARSED",
                                           new String[] { "WAS",
                                                         WASCapabilitiesDocument.XML_TEMPLATE } ),
                          e );
        } catch ( IOException e ) {
            LOG.logError( Messages.format( "ogcwebservices.wass.ERROR_XML_TEMPLATE_NOT_READ",
                                           new String[] { "WAS",
                                                         WASCapabilitiesDocument.XML_TEMPLATE } ),
                          e );
        }
        LOG.exiting();
        return doc;
    }

    /**
     * 
     * Exports the given WSS capabilities as XML document. Also appends the WSS specific element
     * SecuredServiceType.
     * 
     * @param capabilities
     *            the data to export
     * @return the XMLFragment
     */
    public static WSSCapabilitiesDocument export( WSSCapabilities capabilities ) {
        LOG.entering();
        WSSCapabilitiesDocument doc = new WSSCapabilitiesDocument();

        try {
            doc.createEmptyDocument();
            Element root = doc.getRootElement();

            appendBaseCapabilities( capabilities, root );
            Element cap = appendExtendedCapabilities( capabilities, root, WSS, PWSS );

            XMLTools.appendElement( cap, WSS, PWSS + "SecuredServiceType",
                                    capabilities.getSecuredServiceType() );
        } catch ( SAXException e ) {
            LOG.logError( Messages.format( "ogcwebservices.wass.ERROR_XML_TEMPLATE_NOT_PARSED",
                                           new String[] { "WSS",
                                                         WSSCapabilitiesDocument.XML_TEMPLATE } ),
                          e );
        } catch ( IOException e ) {
            LOG.logError( Messages.format( "ogcwebservices.wass.ERROR_XML_TEMPLATE_NOT_READ",
                                           new String[] { "WSS",
                                                         WSSCapabilitiesDocument.XML_TEMPLATE } ),
                          e );
        }
        LOG.exiting();
        return doc;
    }

    /**
     * 
     * Appends the WAS/WSS specific capabilities elements, but only those WAS and WSS have in
     * common.
     * 
     * @param capabilities
     *            the data to append
     * @param root
     *            the WAS/WSS_Capabilities element
     * @param namespace
     *            the namespace URI, WAS or WSS
     * @param prefix
     * @return the appended Capability element
     */
    private static Element appendExtendedCapabilities( OWSCapabilitiesBaseType_1_0 capabilities,
                                                      Element root, URI namespace, String prefix ) {
        LOG.entering();
        Element cap = XMLTools.appendElement( root, namespace, prefix + "Capability" );
        Element sams = XMLTools.appendElement( cap, namespace,
                                               prefix + "SupportedAuthenticationMethodList" );

        ArrayList<SupportedAuthenticationMethod> methods = capabilities.getAuthenticationMethods();
        for ( SupportedAuthenticationMethod method : methods )
            appendSupportedAuthenticationMethod( sams, method );

        LOG.exiting();
        return cap;
    }

    /**
     * 
     * Appends a SupportedAuthenticationMethod element to the given element.
     * 
     * @param sams
     *            the SupportedAuthenticationMethodList element
     * @param method
     *            the data to append
     */
    private static void appendSupportedAuthenticationMethod( Element sams,
                                                            SupportedAuthenticationMethod method ) {
        LOG.entering();
        Element sam = XMLTools.appendElement( sams, AUTHN, PAUTHN + "SupportedAuthenticationMethod" );
        Element authMethod = XMLTools.appendElement( sam, AUTHN, PAUTHN + "AuthenticationMethod" );
        authMethod.setAttribute( "id", method.getMethod().toString() );

        String unknownMD = method.getMetadata();
        if ( unknownMD != null )
            XMLTools.appendElement( sam, AUTHN, PAUTHN + "UnknownMethodMetadata", unknownMD );

        WASAuthenticationMethodMD metadata = method.getWasMetadata();
        if ( metadata != null )
            appendMetadata( sam, metadata );
        LOG.exiting();
    }

    /**
     * 
     * Appends the OWS base capabilities data to the given element.
     * 
     * @param capabilities
     *            the data to append
     * @param root
     *            the element to append to, WAS_- or WSS_Capabilities
     */
    private static void appendBaseCapabilities( OWSCapabilitiesBaseType_1_0 capabilities,
                                               Element root ) {
        LOG.entering();
        root.setAttribute( "version", capabilities.getVersion() );
        root.setAttribute( "updateSequence", capabilities.getUpdateSequence() );

        // may have to be changed/overwritten (?)
        ServiceIdentification serviceIdentification = capabilities.getServiceIdentification();
        if ( serviceIdentification != null )
            appendServiceIdentification( root, serviceIdentification );

        ServiceProvider serviceProvider = capabilities.getServiceProvider();
        if ( serviceProvider != null )
            appendServiceProvider( root, serviceProvider );

        OperationsMetadata_1_0 metadata = capabilities.getOperationsMetadata();
        if ( metadata != null )
            appendOperationsMetadata_1_0( root, metadata );
        LOG.exiting();
    }

    /**
     * 
     * Appends an OperationsMetadata element to the given element.
     * 
     * @param elem
     *            the element to append to
     * @param operationsMetadata
     *            the data to append
     */
    private static void appendOperationsMetadata_1_0( Element elem,
                                                     OperationsMetadata_1_0 operationsMetadata ) {
        LOG.entering();
        Element root = XMLTools.appendElement( elem, OWS, POWS + "OperationsMetadata" );

        Operation[] operations = operationsMetadata.getAllOperations();
        for ( Operation operation : operations )
            appendOperation_1_0( root, (Operation_1_0) operation );

        OWSDomainType[] parameters = operationsMetadata.getParameter();
        if ( parameters != null )
            for ( OWSDomainType parameter : parameters )
                appendParameter( root, parameter, POWS + "Parameter" );

        OWSDomainType[] constraints = operationsMetadata.getConstraints();
        if ( constraints != null )
            for ( OWSDomainType constraint : constraints )
                appendParameter( root, constraint, POWS + "Constraint" );

        String extCap = operationsMetadata.getExtendedCapabilities();
        if ( extCap != null )
            XMLTools.appendElement( root, OWS, POWS + "ExtendedCapabilities", extCap );
        LOG.exiting();
    }

    /**
     * 
     * Appends an Operation element to the argument element.
     * 
     * @param root
     *            the element to append to
     * @param operation
     *            the data to append
     */
    private static void appendOperation_1_0( Element root, Operation_1_0 operation ) {
        LOG.entering();
        Element op = XMLTools.appendElement( root, OWS, POWS + "Operation" );

        op.setAttribute( "Name", operation.getName() );

        DCPType[] dcps = operation.getDCPs();
        for ( DCPType dcp : dcps )
            appendDCP( op, dcp );

        OWSDomainType[] parameters = operation.getParameters();
        for ( OWSDomainType parameter : parameters )
            appendParameter( op, parameter, "Parameter" );

        OWSDomainType[] constraints = operation.getConstraints();
        for ( OWSDomainType constraint : constraints )
            appendParameter( op, constraint, "Constraint" );

        Object[] metadatas = operation.getMetadata();
        for ( Object metadata : metadatas )
            appendMetadata( op, metadata );
        LOG.exiting();
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: XMLFactory.java,v $
 * Changes to this class. What the people have been up to: Revision 1.11  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/06/23 13:53:47  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.9 2006/06/19 12:47:26 schmitz Changes to this
 * class. What the people have been up to: Updated the documentation, fixed the warnings and
 * implemented logging everywhere. Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.8 2006/06/16 15:01:05 schmitz Changes
 * to this class. What the people have been up to: Fixed the WSS to work with all kinds of operation
 * tests. It checks out Changes to this class. What the people have been up to: with both XML and
 * KVP requests. Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.7 2006/06/12 16:11:21 bezema Changes to this class. What
 * the people have been up to: JUnit test work with for a GetCapabilities request - example
 * configurationfiles in resources added Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6 2006/06/12 12:16:24 bezema
 * Changes to this class. What the people have been up to: Little rearanging of the GetSession
 * classes, DoService should be ready updating some errors Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.5 2006/06/09
 * 12:58:32 schmitz Changes to this class. What the people have been up to: Set up some tests for
 * WAS/WSS and the URN class. Changes to this class. What the people have been up to: Commented out
 * some of the deegree param stuff in order for the Changes to this class. What the people have been
 * up to: tests to run. Changes to this class. What the people have been up to: Tests have hardcoded
 * URLs in them, so they won't run just anywhere. Changes to this class. What the people have been
 * up to: Changes to this class. What the people have been up to: Revision 1.4 2006/05/30 11:44:51
 * schmitz Changes to this class. What the people have been up to: Updated the documentation, fixed
 * some warnings. Changes to this class. What the people have been up to: Changes to this class.
 * What the people have been up to: Revision 1.3 2006/05/30 09:59:44 schmitz Changes to this class.
 * What the people have been up to: Completed the XMLFactory to create WAS/WSS capabilities
 * documents. Changes to this class. What the people have been up to: Revision 1.2 2006/05/30
 * 07:47:27 schmitz Started on the XMLFactory.
 * 
 * Revision 1.1 2006/05/29 16:13:00 schmitz Added an XMLFactory to create WAS and WSS
 * GetCapabilities responses. Also added the XML templates for this purpose. Renamed the
 * WASOperationsMetadata to OperationsMetadata_1_0 and made some minor changes to some of the
 * related bean classes.
 * 
 * 
 **************************************************************************************************/
