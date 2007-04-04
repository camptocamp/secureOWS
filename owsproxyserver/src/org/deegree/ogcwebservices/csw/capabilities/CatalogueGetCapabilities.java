//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/capabilities/CatalogueGetCapabilities.java,v 1.13 2006/10/27 13:25:48 poth Exp $
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

import java.util.Map;

import org.deegree.framework.util.StringTools;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.w3c.dom.Element;

/**
 * Class representation of an <code>OGC-GetCapabilities</code> request in <code>CSW</code>
 * flavour.
 * <p>
 * Special to the <code>CSW</code> version of the <code>GetCapabilities</code> request are these
 * two additional parameters: <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Occurences</th>
 * <th>Function</th>
 * </tr>
 * <tr>
 * <td>AcceptVersions</td>
 * <td align="center">0|1</td>
 * <td>Protocol versions supported by this service.</td>
 * </tr>
 * <tr>
 * <td>AcceptFormats</td>
 * <td align="center">0|1</td>
 * <td>Formats accepted by this service.</td>
 * </tr>
 * </table>
 * 
 * @since 2.0
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.13 $
 */
public class CatalogueGetCapabilities extends GetCapabilities {

    private static final long serialVersionUID = 7690283041658363481L;
    
    /**
     * Creates a new <code>CatalogueGetCapabilities</code> instance.
     * 
     * @param id request identifier
     * @param updateSequence
     * @param version
     * @param acceptVersions
     * @param acceptFormats
     * @param sections
     * @param vendoreSpec
     */    
    CatalogueGetCapabilities( String id, String updateSequence, String version,
                                     String[] acceptVersions, String[] acceptFormats,
                                     String[] sections, Map<String,String> vendoreSpec ) {
        super( id, version, updateSequence, acceptVersions, sections, acceptFormats,
               vendoreSpec );
    }    
    
    /**
     * Creates a <code>CatalogGetCapabilities</code> request from its KVP representation.
     * 
     * @param kvp
     *            Map containing the key-value pairs
     * @return created <code>CatalogGetCapabilities</code> object
     */
    public static CatalogueGetCapabilities create( Map<String,String> kvp ) {
        
        String id = getParam( "ID", kvp, null );
        String version = getParam( "VERSION", kvp, "2.0.0" );
        String updateSequence = getParam( "UPDATESEQUENCE", kvp, null );
        String[] acceptVersions = null;
        if ( kvp.get( "ACCEPTVERSION" ) != null ) {
            String tmp = getParam( "ACCEPTVERSION", kvp, null );
            acceptVersions = StringTools.toArray( tmp, ",", false );
        }
        String[] acceptFormats = null;
        if ( kvp.get( "OUTPUTFORMAT" ) != null ) {
            String tmp = getParam( "OUTPUTFORMAT", kvp, null );
            acceptFormats = StringTools.toArray( tmp, ",", false );
        }
        String[] sections = null;
        if ( kvp.get( "SECTIONS" ) != null ) {
            String tmp = getParam( "SECTIONS", kvp, null );
            sections = StringTools.toArray( tmp, ",", false );
        }
        CatalogueGetCapabilities capa = 
            new CatalogueGetCapabilities( id, updateSequence, version, acceptVersions, 
                                          acceptFormats, sections, kvp );
        
        return capa;
    }

    /**
     * Creates a <code>CatalogGetCapabilities</code> request from its XML representation.
     * 
     * @param id
     *            unique ID of the request
     * @param root
     *            XML representation of the request
     * @return created <code>CatalogGetCapabilities</code> object
     * @throws OGCWebServiceException
     *             thrown if something in the request is wrong
     */
    public static CatalogueGetCapabilities create( String id, Element root )
        throws OGCWebServiceException {

        CatalogueGetCapabilitiesDocument doc = new CatalogueGetCapabilitiesDocument();
        doc.setRootElement( root );
        CatalogueGetCapabilities request;
        try {
            request = doc.parse(id);
        } catch ( Exception e ) {
            ExceptionCode code = ExceptionCode.INVALID_FORMAT;
            throw new OGCWebServiceException( "CatalogGetCapabilities", StringTools
                .stackTraceToString( e ), code );
        }
        return request;
    }

    /**
     * returns WCS as service name
     */
    public String getServiceName() {
        return "CSW";
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueGetCapabilities.java,v $
Revision 1.13  2006/10/27 13:25:48  poth
support for vendorspecific parameters added

Revision 1.12  2006/08/02 14:33:39  poth
bug fix - setting passing version to constructor (create(Map) )

Revision 1.11  2006/07/21 14:08:04  mschneider
Improved javadoc.

Revision 1.10  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */