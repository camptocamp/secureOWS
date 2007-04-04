//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/operation/WASGetCapabilitiesDocument.java,v 1.6 2006/10/27 13:26:33 poth Exp $
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
package org.deegree.ogcwebservices.wass.was.operation;

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
 * Parser for a WAS GetCapabilities request.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/10/27 13:26:33 $
 * 
 * @since 2.0
 */

public class WASGetCapabilitiesDocument extends XMLFragment {

    private static final long serialVersionUID = -859662737770813155L;

    private static final ILogger LOG = LoggerFactory.getLogger( WASGetCapabilitiesDocument.class );

    // this pattern matches parametrized mime types, so if something funny happens, this may be the
    // reason
    private static Pattern pattern = Pattern.compile( "(application|audio|image|text|video|message|multipart|model)/.+(;\\s*.+=.+)*" );

    private static final String PRE = CommonNamespaces.OWS_PREFIX;

    /**
     * Parses a WASGetCapabilities XML request.
     * 
     * @param id
     * @param root
     * @return a new request object
     * @throws XMLParsingException
     */
    public WASGetCapabilities parseCapabilities( String id, Element root )
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
        if ( !service.equals( "WAS" ) )
            throw new XMLParsingException(
                                           Messages.getString( "ogcwebservices.wass.ERROR_NO_SERVICE_ATTRIBUTE" ) );

        WASGetCapabilities result = new WASGetCapabilities( id, "1.0", updateSequence,
                                                            acceptVersions, sections, acceptFormats,
                                                            new HashMap<String,String>() );

        LOG.exiting();
        return result;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WASGetCapabilitiesDocument.java,v $
 * Revision 1.6  2006/10/27 13:26:33  poth
 * support for vendorspecific parameters added
 *
 * Revision 1.5  2006/06/23 13:53:48  schmitz
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Revision 1.4 2006/06/23 10:23:50 schmitz
 * Completed the WAS, GetSession and
 * CloseSession work. Revision 1.3
 * 2006/06/19 15:34:04 bezema changed wass to handle things the right way
 * 
 * Revision 1.2 2006/06/19 11:24:53 schmitz WAS and WSS operation tests are now completed and
 * running.
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.6 2006/05/26 14:38:32 schmitz Added some KVP constructors to WAS operations. Added
 * some comments, updated the plan. Restructured WAS operations by adding an AbstractRequest base
 * class.
 * 
 * Revision 1.5 2006/05/23 15:20:50 bezema Cleaned up the warnings and added some minor methods
 * 
 * Revision 1.4 2006/05/22 12:17:31 schmitz Restructured the GetCapabilities/Document classes.
 * 
 * Revision 1.3 2006/05/22 11:38:36 bezema Updating the format
 * 
 * Revision 1.2 2006/05/22 11:29:35 bezema Reviewing the getcapabilities classes
 * 
 * Revision 1.1 2006/05/19 15:35:35 schmitz Updated the documentation, added the GetCapabilities
 * operation and implemented a rough WAService outline. Fixed some warnings.
 * 
 * 
 * 
 **************************************************************************************************/
