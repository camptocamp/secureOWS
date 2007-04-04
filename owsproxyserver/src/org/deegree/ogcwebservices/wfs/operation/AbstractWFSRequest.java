//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/AbstractWFSRequest.java,v 1.13 2006/11/16 08:53:21 mschneider Exp $
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wfs.operation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.i18n.Messages;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wfs.WFService;

/**
 * Abstract base class for requests to web feature services.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.13 $, $Date: 2006/11/16 08:53:21 $
 */
public class AbstractWFSRequest extends AbstractOGCWebServiceRequest {

    private static final long serialVersionUID = 6691114984307038750L;

    /** GML2 format **/
    public static String FORMAT_GML2 = "text/xml; subtype=gml/2.1.2";

    /** GML2 format (WFS 1.00 style) **/
    public static String FORMAT_GML2_WFS100 = "GML2";

    /** GML3 format **/
    public static String FORMAT_GML3 = "text/xml; subtype=gml/3.1.1";

    /** Generic XML format **/
    public static String FORMAT_XML = "XML";

    private String handle = null;

    /**
     * Creates a new <code>AbstractWFSRequest</code> instance.
     * 
     * @param version
     * @param id
     * @param handle
     * @param vendorSpecificParameter
     */
    protected AbstractWFSRequest( String version, String id, String handle,
                                  Map<String, String> vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
        this.handle = handle;
    }

    /**
     * Returns the value of the service attribute (WFS).
     * 
     * @return the value of the service attribute (WFS)
     */
    public String getServiceName() {
        return "WFS";
    }

    /**
     * Returns the value of the handle attribute.
     * <p>
     * The purpose of the <b>handle</b> attribute is to allow a client application to associate a
     * mnemonic name with a request for error handling purposes. If a <b>handle</b> is specified,
     * and an exception is encountered, a Web Feature Service may use the <b>handle</b> to identify
     * the offending element.
     * 
     * @return the value of the handle attribute
     */
    public String getHandle() {
        return this.handle;
    }

    /**
     * Checks that the "VERSION" parameter value equals a supported version.
     * 
     * @param model
     *            contains the parameters of the request
     * @return value for "VERSION" parameter, never null
     * @throws InconsistentRequestException
     *             if parameter is not present
     * @throws InvalidParameterValueException 
     */
    protected static String checkVersionParameter( Map<String, String> model )
                            throws InconsistentRequestException, InvalidParameterValueException {
        String version = model.get( "VERSION" );
        if ( version == null ) {
            throw new InconsistentRequestException( "'VERSION' parameter must be set." );
        }
        if ( !WFService.VERSION.equals( version ) ) {
            String msg = org.deegree.i18n.Messages.getMessage( "WFS_REQUEST_UNSUPPORTED_VERSION",
                                                               version, WFService.VERSION );
            throw new InvalidParameterValueException( msg );
        }
        return version;
    }

    /**
     * Checks that the "SERVICE" parameter value equals the name of the service.
     * 
     * TODO move this to AbstractOGCWebServiceRequest
     * 
     * @param model
     *            contains the parameters of the request
     * @throws InconsistentRequestException
     *             if parameter is not present or does not the service name
     */
    protected static void checkServiceParameter( Map<String, String> model )
                            throws InconsistentRequestException {
        String service = model.get( "SERVICE" );
        if ( !"WFS".equals( service ) ) {
            throw new InconsistentRequestException( "'SERVICE' parameter must be 'WFS', but is '"
                                                    + service + "'." );
        }
    }

    /**
     * Extracts the qualified type names from the TYPENAME parameter.
     * 
     * @param kvp
     * @return qualified type names (empty array if TYPENAME parameter is not present)
     * @throws InvalidParameterValueException
     */
    protected static QualifiedName[] extractTypeNames( Map<String, String> kvp )
                            throws InvalidParameterValueException {
        QualifiedName[] typeNames = new QualifiedName[0];
        NamespaceContext nsContext = extractNamespaceParameter( kvp );
        String typeNameString = kvp.get( "TYPENAME" );
        if ( typeNameString != null ) {
            String[] typeNameStrings = typeNameString.split( "," );
            typeNames = new QualifiedName[typeNameStrings.length];
            for ( int i = 0; i < typeNameStrings.length; i++ ) {
                typeNames[i] = transformToQualifiedName( typeNameStrings[i], nsContext );
            }
        }
        return typeNames;
    }

    /**
     * Extracts the namespace bindings from the NAMESPACE parameter.
     * <p>
     * Example:
     * <ul>
     * <li><code>NAMESPACE=xmlns(myns=http://www.someserver.com),xmlns(yourns=http://www.someotherserver.com)</code></li>
     * </ul>
     * <p>
     * The default namespace may also be bound (two variants are supported):
     * <ul>
     * <li><code>NAMESPACE=xmlns(=http://www.someserver.com)</code></li>
     * <li><code>NAMESPACE=xmlns(http://www.someserver.com)</code></li>
     * </ul>
     * 
     * @param model
     *            the parameters of the request
     * @return namespace context
     * @throws InvalidParameterValueException
     */
    protected static NamespaceContext extractNamespaceParameter( Map<String, String> model )
                            throws InvalidParameterValueException {

        String nsString = model.get( "NAMESPACE" );

        NamespaceContext nsContext = new NamespaceContext();
        if ( nsString != null ) {
            String nsDecls[] = nsString.split( "," );
            for ( int i = 0; i < nsDecls.length; i++ ) {
                String nsDecl = nsDecls[i];
                if ( nsDecl.startsWith( "xmlns(" ) && nsDecl.endsWith( ")" ) ) {
                    nsDecl = nsDecl.substring( 6, nsDecl.length() - 1 );
                    int assignIdx = nsDecl.indexOf( '=' );
                    String prefix = "";
                    String nsURIString = null;
                    if ( assignIdx != -1 ) {
                        prefix = nsDecl.substring( 0, assignIdx );
                        nsURIString = nsDecl.substring( assignIdx + 1 );
                    } else {
                        nsURIString = nsDecl;
                    }
                    try {
                        URI nsURI = new URI( nsURIString );
                        nsContext.addNamespace( prefix, nsURI );
                    } catch ( URISyntaxException e ) {
                        String msg = Messages.getMessage( "WFS_NAMESPACE_PARAM_INVALID_URI",
                                                          nsURIString, prefix );
                        throw new InvalidParameterValueException( msg );
                    }
                } else {
                    String msg = Messages.getMessage( "WFS_NAMESPACE_PARAM" );
                    throw new InvalidParameterValueException( msg );
                }
            }
        }
        return nsContext;
    }

    /**
     * Transforms a type name to a qualified name using the given namespace bindings.
     * 
     * @param name
     * @param nsContext
     * @return QualifiedName
     * @throws InvalidParameterValueException
     */
    private static QualifiedName transformToQualifiedName( String name, NamespaceContext nsContext )
                            throws InvalidParameterValueException {
        QualifiedName typeName;
        String prefix = "";
        int idx = name.indexOf( ':' );
        if ( idx != -1 ) {
            prefix = name.substring( 0, idx );
            String localName = name.substring( idx + 1 );
            URI nsURI = nsContext.getURI( prefix );
            if ( nsURI == null ) {
                String msg = Messages.getMessage( "WFS_TYPENAME_PARAM_INVALID_URI", prefix );
                throw new InvalidParameterValueException( msg );
            }
            typeName = new QualifiedName( prefix, localName, nsURI );
        } else {
            // default namespace prefix ("")
            URI nsURI = nsContext.getURI( "" );
            typeName = new QualifiedName( name, nsURI );
        }
        return typeName;
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * Revision 1.5.2.2 2005/11/07 16:25:57 deshmukh NodeList to List
 * 
 * Revision 1.5.2.1 2005/11/07 15:38:04 mschneider Refactoring: use NamespaceContext instead of Node
 * for namespace bindings.
 * 
 * Revision 1.5 2005/08/26 21:11:30 poth no message
 * 
 * Revision 1.4 2005/07/22 15:17:54 mschneider Added constants for output formats.
 * 
 * Revision 1.3 2005/04/06 13:44:06 poth no message
 * 
 * Revision 1.2 2005/04/06 12:02:08 poth no message
 * 
 * Revision 1.1 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.2 2005/03/09 11:55:46 mschneider *** empty log message ***
 * 
 * Revision 1.1 2005/03/01 17:17:55 poth no message
 * 
 * Revision 1.8 2005/03/01 14:39:08 mschneider *** empty log message *** Revision 1.7 2005/02/24
 * 20:04:04 poth no message
 * 
 * Revision 1.6 2005/02/22 22:45:14 friebe fix javadoc
 * 
 * Revision 1.5 2005/02/21 13:53:48 poth no message
 * 
 * Revision 1.4 2005/02/21 11:24:33 poth no message
 * 
 * Revision 1.3 2005/02/18 20:54:18 poth no message
 * 
 * Revision 1.2 2005/02/07 07:56:57 poth no message
 * 
 * Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.3 2004/08/24 11:48:26 tf no message Revision 1.2 2004/06/21 08:05:49 ap no message
 * 
 * Revision 1.1 2004/06/07 13:38:34 tf code adapted to wfs1 refactoring
 * 
 * Revision 1.7 2004/03/26 11:19:32 poth no message
 * 
 * Revision 1.6 2004/03/12 15:56:48 poth no message
 * 
 * Revision 1.5 2004/01/26 08:10:37 poth no message
 * 
 * Revision 1.4 2003/05/05 15:52:50 poth no message
 * 
 * Revision 1.3 2003/04/10 07:32:35 poth no message
 * 
 * Revision 1.2 2003/04/07 07:26:53 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:24 poth no message
 * 
 * Revision 1.7 2002/08/15 10:01:40 ap no message
 * 
 * Revision 1.6 2002/08/09 15:36:30 ap no message
 * 
 * Revision 1.5 2002/05/14 14:39:51 ap no message
 * 
 * Revision 1.4 2002/05/13 16:11:02 ap no message
 * 
 * Revision 1.3 2002/04/26 09:05:36 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 * 
 ************************************************************************************************* */