//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/capabilities/CatalogueGetCapabilitiesDocument.java,v 1.4 2006/10/27 13:25:48 poth Exp $
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
package org.deegree.ogcwebservices.csw.capabilities;

import java.util.HashMap;

import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.w3c.dom.Element;

/**
 * Parser for "csw:GetCapabilities" requests.  
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/10/27 13:25:48 $
 * 
 * @since 2.0
 */
public class CatalogueGetCapabilitiesDocument extends OGCDocument {

    private static final long serialVersionUID = -7155778875151820291L;

    /**
     * Parses the underlying document into a <code>GetCapabilities</code> request object.
     * 
     * @param id
     * @return corresponding <code>GetCapabilities</code> object
     * @throws XMLParsingException 
     * @throws InvalidParameterValueException 
     */
    public CatalogueGetCapabilities parse( String id )
                            throws XMLParsingException {

        String version = "2.0.0";
        String service = null;
        String updateSeq = null;
        String[] acceptVersions = null;
        String[] acceptFormats = null;
        String[] sections = null;
        Element root = this.getRootElement();

        // 'service'-attribute (required, must be CSW)
        service = XMLTools.getAttrValue( root, "service" );
        if ( service == null ) {
            throw new XMLParsingException ( "Mandatory attribute 'service' is missing");
        } else if ( !service.equals( "CSW" ) ) {
            throw new XMLParsingException( "Attribute 'service' must be 'CSW'");
        }
        // 'updateSequence'-attribute (optional)
        updateSeq = XMLTools.getAttrValue( root, "updateSequence" );

        // '<csw:AcceptVersions>'-element (optional)
        Element acceptVersionsElement = XMLTools.getChildElement( "AcceptVersions",
                                                                  CommonNamespaces.CSWNS, root );
        if ( acceptVersionsElement != null ) {
            acceptVersions = XMLTools.getRequiredNodesAsStrings( acceptVersionsElement,
                                                                 "csw:Version", nsContext );
        }

        // '<csw:AcceptFormats>'-element (optional)
        Element acceptFormatsElement = XMLTools.getChildElement( "AcceptFormats",
                                                                 CommonNamespaces.CSWNS, root );
        if ( acceptFormatsElement != null ) {
            ElementList formatsList = XMLTools.getChildElements( "OutputFormat",
                                                                 CommonNamespaces.CSWNS,
                                                                 acceptFormatsElement );
            acceptFormats = new String[formatsList.getLength()];
            for ( int i = 0; i < acceptFormats.length; i++ ) {
                acceptFormats[i] = XMLTools.getStringValue( formatsList.item( i ) );
            }
        }

        // '<csw:Sections>'-element (optional)
        Element sectionsElement = XMLTools.getChildElement( "Sections", CommonNamespaces.CSWNS,
                                                            root );
        if ( sectionsElement != null ) {
            ElementList sectionList = XMLTools.getChildElements( "Section", CommonNamespaces.CSWNS,
                                                                 sectionsElement );
            sections = new String[sectionList.getLength()];
            for ( int i = 0; i < sections.length; i++ ) {
                sections[i] = XMLTools.getStringValue( sectionList.item( i ) );
            }
        }

        return new CatalogueGetCapabilities( id, updateSeq, version, acceptVersions, acceptFormats,
                                             sections, new HashMap<String,String>() );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: CatalogueGetCapabilitiesDocument.java,v $
 Revision 1.4  2006/10/27 13:25:48  poth
 support for vendorspecific parameters added

 Revision 1.3  2006/08/07 07:09:03  poth
 bug fix - version parameter set correctly

 Revision 1.2  2006/08/02 14:35:02  poth
 bug fix - setting passing version to constructor (parse)

 Revision 1.1  2006/06/19 19:25:33  mschneider
 Moved XML parsing from CatalogueGetCapabilities.

 ********************************************************************** */

