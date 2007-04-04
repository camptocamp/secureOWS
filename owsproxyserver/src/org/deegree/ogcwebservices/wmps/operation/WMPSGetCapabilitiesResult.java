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

import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wmps.capabilities.WMPSCapabilities;

/**
 * Encapsulates a WMPS Result Object
 * 
 * <p>
 * --------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public class WMPSGetCapabilitiesResult extends DefaultOGCWebServiceResponse {
    private WMPSCapabilities capabilities;

    /**
     * constructor initializing the class with the <WPSFilterServiceResponse>
     * 
     * @param request
     * @param capabilities
     */
    WMPSGetCapabilitiesResult( OGCWebServiceRequest request, WMPSCapabilities capabilities ) {
        super( request );
        setCapabilities( capabilities );
    }

    /**
     * constructor initializing the class with the <WPSFilterServiceResponse> *
     * 
     * @param request
     * @param exception
     */
    WMPSGetCapabilitiesResult( OGCWebServiceRequest request, OGCWebServiceException exception ) {
        super( request, exception );
        setCapabilities( this.capabilities );
    }

    /**
     * returns the capabilities as result of an GetCapabilities request. If an excption raised
     * processing the request or the request has been invalid <tt>null</tt> will be returned.
     * 
     * @return WMPSCapabilites
     */
    public WMPSCapabilities getCapabilities() {
        return this.capabilities;
    }

    /**
     * sets the capabilities as result of an GetCapabilities request. If an excption raised
     * processing the request or the request has been invalid <tt>null</tt> will be returned.
     * 
     * @param capabilities
     */
    public void setCapabilities( WMPSCapabilities capabilities ) {
        this.capabilities = capabilities;
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WMPSGetCapabilitiesResult.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/08/10 07:11:35  deshmukh
 * Changes to this class. What the people have been up to: WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7 2006/08/01 13:41:48 deshmukh
 * Changes to this class. What the people have been up to: The wmps configuration has been modified
 * and extended. Also fixed the javadoc. Changes to this class. What the people have been up to:
 * Revision 1.6 2006/07/12 14:46:18 poth comment footer added
 * 
 **************************************************************************************************/
