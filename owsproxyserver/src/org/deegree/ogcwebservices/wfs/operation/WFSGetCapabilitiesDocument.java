//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/WFSGetCapabilitiesDocument.java,v 1.6 2006/11/07 11:09:36 mschneider Exp $
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

import java.util.HashMap;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.w3c.dom.Element;

/**
 * Parser for "wfs:GetCapabilities" requests.  
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.6 $, $Date: 2006/11/07 11:09:36 $
 */
public class WFSGetCapabilitiesDocument extends AbstractWFSRequestDocument {

    private static final long serialVersionUID = -1901946322324983262L;

    /**
     * Parses the underlying document into a {@link WFSGetCapabilities} request object.
     * 
     * @param id
     * @return corresponding <code>GetCapabilities</code> object
     * @throws XMLParsingException 
     */
    public WFSGetCapabilities parse( String id )
                            throws XMLParsingException {

        checkServiceAttribute();

        Element root = this.getRootElement();

        String updateSeq = XMLTools.getNodeAsString( root, "@updateSequence",
                                                                 nsContext, null );
        String[] acceptVersions = XMLTools.getNodesAsStrings( root,
                                                              "ows:AcceptVersions/ows:Version",
                                                              nsContext );
        String[] sections = XMLTools.getNodesAsStrings( root, "ows:AcceptFormats/ows:Sections",
                                                        nsContext );
        String[] acceptFormats = XMLTools.getNodesAsStrings( root,
                                                             "ows:AcceptVersions/ows:OutputFormat",
                                                             nsContext );
        return new WFSGetCapabilities( id, updateSeq, acceptVersions, sections, acceptFormats, 
                                       new HashMap<String,String>() );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSGetCapabilitiesDocument.java,v $
 Revision 1.6  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.5  2006/10/27 13:26:33  poth
 support for vendorspecific parameters added

 Revision 1.4  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.3  2006/07/21 14:10:24  mschneider
 Javadoc fixes.

 Revision 1.2  2006/06/07 17:18:56  mschneider
 Removed unnecessary whitespace.

 Revision 1.1  2006/06/06 17:05:50  mschneider
 Initial version. Outfactored parser.

 ********************************************************************** */