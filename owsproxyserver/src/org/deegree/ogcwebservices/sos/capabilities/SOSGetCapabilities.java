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
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos.capabilities;

import java.util.HashMap;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.w3c.dom.Document;

/**
 * represents a sOs getCapabilities Request
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class SOSGetCapabilities extends GetCapabilities {

    private static final ILogger LOG = LoggerFactory.getLogger( SOSGetCapabilities.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * creates a GetCapabilities Request from a KVP Map
     * 
     * @param map
     */
    public static SOSGetCapabilities create( Map<String,String> map ) {

        String id = getParam( "ID", map, ""+ System.currentTimeMillis() );

        // optional
        String version = getParam( "VERSION", map, null );

        // optional
        String updateSequence = getParam( "UPDATESEQUENCE", map, null );

        // optional and unbounded
        String[] sections = null;
        if ( map.get( "SECTIONS" ) != null ) {
            String tmp = getParam( "SECTIONS", map, null );
            sections = StringTools.toArray( tmp, ",", false );
        }

        // optional and unbounded
        String[] acceptVersions = null;
        if ( map.get( "ACCEPTVERSIONS" ) != null ) {
            String tmp = getParam( "ACCEPTVERSIONS", map, null );
            acceptVersions = StringTools.toArray( tmp, ",", false );
        }

        // optional and unbounded
        String[] acceptFormats = null;
        if ( map.get( "ACCEPTFORMATS" ) != null ) {
            String tmp = getParam( "ACCEPTFORMATS", map, null );
            acceptFormats = StringTools.toArray( tmp, ",", false );
        }

        return new SOSGetCapabilities( id, updateSequence, acceptVersions, sections, 
                                       acceptFormats, map );

    }

    /**
     * creates GetCapabilities Request from XML
     * 
     * @param id
     * @param doc
     * @throws OGCWebServiceException
     * 
     */
    public static SOSGetCapabilities create( String id, Document doc )
        throws OGCWebServiceException {

        try {
            // optional
            // String version = XMLTools.getNodeAsString( doc, "ows:GetCapabilities/@version",
            // nsContext,
            // null );

            // optional
            String updateSequence = XMLTools.getNodeAsString( doc,
                "ows:GetCapabilities/@updateSequence", nsContext, null );

            // optional and unbounded
            String[] sections = XMLTools.getNodesAsStrings( doc,
                "ows:GetCapabilities/ows:Sections/ows:Section/text()", nsContext );

            // optional and unbounded
            String[] acceptVersions = XMLTools.getNodesAsStrings( doc,
                "ows:GetCapabilities/ows:AcceptVersions/ows:Version/text()", nsContext );

            // optional and unbounded
            String[] acceptFormats = XMLTools.getNodesAsStrings( doc,
                "ows:GetCapabilities/ows:AcceptFormats/ows:OutputFormat/text()", nsContext );

            return new SOSGetCapabilities( id, updateSequence, acceptVersions, sections,
                                           acceptFormats, new HashMap<String,String>() );

        } catch (XMLParsingException e) {
            e.printStackTrace();
            throw new OGCWebServiceException( "sos webservice failure" );
        }
    }

    /**
     * creates GetCapabilities Request
     * 
     * @param sections
     * @param acceptFormats
     * @param acceptVersions
     * @param update
     * @param id
     * @param version
     * @param vendoreSpec
     * 
     */
    public static SOSGetCapabilities create( String id, String[] sections, String[] acceptFormats,
                                            String[] acceptVersions, String updateSequence,
                                            Map<String,String> vendoreSpec) {
        return new SOSGetCapabilities( id, updateSequence, acceptVersions, sections, 
                                       acceptFormats, vendoreSpec );
    }

    /**
     * @param id
     * @param version
     * @param updateSequence
     * @param acceptVersions
     * @param sections
     * @param acceptFormats
     * @param vendoreSpec
     */
    public SOSGetCapabilities( String id, String updateSequence, String[] acceptVersions,
                              String[] sections, String[] acceptFormats, Map<String,String> vendoreSpec ) {
        super( id, null, updateSequence, acceptVersions, sections, acceptFormats, vendoreSpec );
    }

    /**
     * fixed 'SOS'
     */
    public String getServiceName() {
        return "SOS";
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSGetCapabilities.java,v $
Revision 1.9  2006/10/27 13:26:33  poth
support for vendorspecific parameters added

Revision 1.8  2006/08/24 06:42:17  poth
File header corrected

Revision 1.7  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
