//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/WSSGetCapabilitiesDocument.java,v 1.8 2006/10/27 13:26:33 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
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

package org.deegree.ogcwebservices.wass.wss.operation;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wass.common.Messages;
import org.w3c.dom.Element;

/**
 * A WSSGetCapabilitiesClass xml request parser.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/10/27 13:26:33 $
 * 
 * @since 2.0
 */

public class WSSGetCapabilitiesDocument extends XMLFragment {

    private static final long serialVersionUID = 2601051525408253639L;

    private static final ILogger LOG = LoggerFactory.getLogger( WSSGetCapabilitiesDocument.class );

    private static Pattern pattern = Pattern.compile( "(application|audio|image|text|video|message|multipart|model)/.+(;\\s*.+=.+)*" );

    private static final String PRE = CommonNamespaces.OWS_PREFIX;

    protected WSSGetCapabilities parseCapabilities( String id, Element root )
                            throws XMLParsingException {
        LOG.entering();
        Element acceptVersionsNode = (Element) XMLTools.getNode( root, PRE + "AcceptVersions",
                                                                 nsContext );
        String[] acceptVersions = null;
        if ( acceptVersionsNode != null )
            acceptVersions = XMLTools.getRequiredNodesAsStrings( acceptVersionsNode, PRE
                                                                                     + "Version",
                                                                 nsContext );

        Element sectionsNode = (Element) XMLTools.getNode( root, PRE + "Sections", nsContext );
        String[] sections = null;
        if ( sectionsNode != null )
            sections = XMLTools.getNodesAsStrings( sectionsNode, PRE + "Section", nsContext );

        Element acceptFormatsNode = (Element) XMLTools.getNode( root, PRE + "AcceptFormats",
                                                                nsContext );
        String[] acceptFormats = null;
        if ( acceptFormatsNode != null ) {
            acceptFormats = XMLTools.getNodesAsStrings( acceptFormatsNode, PRE + "OutputFormat",
                                                        nsContext );
            for ( String str : acceptFormats )
                if ( !pattern.matcher( str ).matches() )
                    throw new XMLParsingException(
                                                   Messages.getString( "ogcwebservices.wass.ERROR_VALUE_NO_MIMETYPE" ) );
        }

        String updateSequence = XMLTools.getNodeAsString( root, "@updateSequence", nsContext, null );

        String service = XMLTools.getRequiredNodeAsString( root, "@service", nsContext );
        if ( !service.equals( "WSS" ) )
            throw new XMLParsingException( Messages.getString( "ogcwebservices.wass.ERROR_NOT_WSS" ) );
        LOG.exiting();
        return new WSSGetCapabilities( id, "1.0", updateSequence, acceptFormats, acceptVersions,
                                       sections, new HashMap<String,String>() );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WSSGetCapabilitiesDocument.java,v $
 * Revision 1.8  2006/10/27 13:26:33  poth
 * support for vendorspecific parameters added
 *
 * Revision 1.7  2006/08/24 06:42:16  poth
 * File header corrected
 * Changes to this class. What the people have been up to:
 * Revision 1.6  2006/06/23 13:53:47  schmitz
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Revision 1.5 2006/06/20 15:31:04 bezema
 * It looks like the completion of wss. was
 * needs further checking in a tomcat environment. The Strings must still be externalized. Logging
 * is done, so is the formatting. Changes to
 * this class. What the people have been up to: Revision 1.4 2006/06/19 15:34:04 bezema Changes to
 * this class. What the people have been up to: changed wass to handle things the right way Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.3 2006/06/16 15:01:05 schmitz Changes to this class. What the people have been
 * up to: Fixed the WSS to work with all kinds of operation tests. It checks out Changes to this
 * class. What the people have been up to: with both XML and KVP requests. Changes to this class.
 * What the people have been up to: Revision 1.2 2006/05/30 10:12:02 bezema Putting the cvs asci
 * option to -kkv which will update the $revision$ $author$ and $date$ variables in a cvs commit
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * Revision 1.1 2006/05/22 15:48:16 bezema Starting the parsing of the xml request in wss
 * 
 * 
 **************************************************************************************************/
