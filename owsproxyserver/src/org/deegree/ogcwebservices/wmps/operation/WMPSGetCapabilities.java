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
package org.deegree.ogcwebservices.wmps.operation;

import java.util.Map;

import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;

/**
 * This interface desribes the access to the parameters common to a OGC GetCapabilities request. It
 * inherits three accessor methods from the general OGC web service request interface.
 * 
 * <p>
 * --------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public class WMPSGetCapabilities extends GetCapabilities {

    private static final long serialVersionUID = -609973973617914526L;

    /**
     * creates an WMPS GetCapabilities Request
     * 
     * @param paramMap
     *            the parameters of the request
     * @return the GetCapabilities request
     * @throws InconsistentRequestException
     *             if the request is inconsistent
     */
    public static WMPSGetCapabilities create( Map<String,String> paramMap )
                            throws InconsistentRequestException {

        String version = paramMap.remove( "VERSION" );

        if ( version == null ) {
            version = paramMap.remove( "WMTVER" );
        }

        String service = paramMap.remove( "SERVICE" );
        String updateSequence = paramMap.remove( "UPDATESEQUENCE" );

        if ( service == null ) {
            throw new InconsistentRequestException( "Required parameter 'SERVICE' is missing." );
        }

        if ( !service.equals( "WMPS" ) ) {
            throw new InconsistentRequestException( "Parameter 'SERVICE' must be 'WMPS'." );
        }

        return new WMPSGetCapabilities( paramMap.remove( "ID" ), version, updateSequence,
                                        paramMap );
    }

    /**
     * Creates a new WMPSGetCapabilities object.
     * 
     * @param version
     * @param id
     * @param updateSequence
     * @param vendorSpecificParameter
     */
    WMPSGetCapabilities( String version, String id, String updateSequence,
                         Map<String, String> vendorSpecificParameter ) {
        super( version, id, updateSequence, null, null, null, vendorSpecificParameter );
    }

    /**
     * returns the URI of a HTTP GET request.
     * 
     * @return String
     */
    @Override
    public String getRequestParameter() {
        if ( getVersion().equals( "1.0.0" ) ) {
            return "service=WMPS&version=" + getVersion() + "&request=capabilities";
        }
        return "service=WMPS&version=" + getVersion() + "&request=GetCapabilities";
    }

    /**
     * returns 'WMPS' as service name
     * 
     * @return String
     */
    public String getServiceName() {
        return "WMPS";
    }

   
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WMPSGetCapabilities.java,v $
 * Revision 1.16  2006/10/27 13:26:33  poth
 * support for vendorspecific parameters added
 *
 * Revision 1.15  2006/08/10 07:11:35  deshmukh
 * WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to:
 * Revision 1.14 2006/08/01 13:41:48
 * deshmukh The wmps configuration has been
 * modified and extended. Also fixed the javadoc. Changes to this class. What the people have been
 * up to: Revision 1.13 2006/07/20 13:24:12 deshmukh Removed a few floating bugs.
 * 
 * Revision 1.12 2006/07/12 14:46:18 poth comment footer added
 * 
 **************************************************************************************************/
