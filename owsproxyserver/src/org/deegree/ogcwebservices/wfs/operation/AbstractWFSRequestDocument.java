//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/AbstractWFSRequestDocument.java,v 1.4 2006/11/07 11:09:36 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.wfs.WFService;
import org.w3c.dom.Element;

/**
 * Abstract base class for WFS request documents / parsers.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/11/07 11:09:36 $
 */
public class AbstractWFSRequestDocument extends OGCDocument {

    private static final long serialVersionUID = -3826447710328793808L;

    private static String SERVICE_NAME = "WFS";

    /**
     * Checks that the "service" attribute in the root node matches the expected value (WFS).
     * 
     * @throws XMLParsingException 
     */
    protected void checkServiceAttribute()
                            throws XMLParsingException {
        String service = XMLTools.getNodeAsString( getRootElement(), "@service", nsContext,
                                                   SERVICE_NAME );
        if ( !SERVICE_NAME.equals( service ) ) {
            throw new XMLParsingException( "Service attribute must be '" + SERVICE_NAME + "'." );
        }
    }

    /**
     * Parses and checks the "version" attribute in the root node (must be "1.1.0"). If it is not
     * present, "1.1.0" is returned.
     * 
     * @return version
     * @throws XMLParsingException
     */
    protected String checkVersionAttribute()
                            throws XMLParsingException {
        String version = XMLTools.getNodeAsString( this.getRootElement(), "@version", nsContext,
                                                   WFService.VERSION );
        if ( !WFService.VERSION.equals( version ) ) {
            String msg = Messages.getMessage( "WFS_REQUEST_UNSUPPORTED_VERSION", version,
                                              WFService.VERSION );
            throw new XMLParsingException( msg );
        }
        return version;
    }

    /**
     * Transform an array of strings to an array of qualified names.
     * 
     * TODO adapt style (parseXYZ)
     * 
     * @param values
     * @param nsMap
     * @return QualifiedNames
     */
    protected QualifiedName[] transformToQualifiedNames( String[] values, Element element )
                            throws XMLParsingException {
        QualifiedName[] typeNames = new QualifiedName[values.length];
        for ( int i = 0; i < values.length; i++ ) {
            int idx = values[i].indexOf( ":" );
            if ( idx != -1 ) {
                String prefix = values[i].substring( 0, idx );
                String name = values[i].substring( idx + 1 );
                URI uri;
                try {
                    uri = XMLTools.getNamespaceForPrefix( prefix, element );
                } catch ( URISyntaxException e ) {
                    throw new XMLParsingException( e.getMessage(), e );
                }
                typeNames[i] = new QualifiedName( prefix, name, uri );
            } else {
                typeNames[i] = new QualifiedName( values[i] );
            }
        }
        return typeNames;
    }

    protected Map<String, String> parseDRMParams( Element root )
                            throws XMLParsingException {
        String user = XMLTools.getNodeAsString( root, "@user", nsContext, null );
        String password = XMLTools.getNodeAsString( root, "@password", nsContext, null );
        String sessionID = XMLTools.getNodeAsString( root, "@sessionID", nsContext, null );
        Map<String, String> vendorSpecificParam = new HashMap<String, String>();
        vendorSpecificParam.put( "USER", user );
        vendorSpecificParam.put( "PASSWORD", password );
        vendorSpecificParam.put( "SESSIONID", sessionID );
        return vendorSpecificParam;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: AbstractWFSRequestDocument.java,v $
 Revision 1.4  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.3  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.2  2006/06/07 17:14:29  mschneider
 Added parseVersion() + transformToQualifiedNames().

 Revision 1.1  2006/06/06 17:05:19  mschneider
 Initial version.

 ********************************************************************** */