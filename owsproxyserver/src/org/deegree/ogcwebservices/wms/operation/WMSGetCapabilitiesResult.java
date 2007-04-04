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
package org.deegree.ogcwebservices.wms.operation;

import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType;


/**
* 
*
* <p>--------------------------------------------------------</p>
*
* @author Katharina Lupp <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
* @version $Revision: 1.10 $ $Date: 2006/09/08 08:42:02 $
*/
public class WMSGetCapabilitiesResult extends DefaultOGCWebServiceResponse {

    private WMSConfigurationType capabilities = null;
    
    /**
     * constructor initializing the class with the <WMSFilterServiceResponse>
     */
     WMSGetCapabilitiesResult( OGCWebServiceRequest request, 
                               WMSConfigurationType capabilities ) {
         super( request );
         setCapabilities( capabilities );
     }

    /**
    * constructor initializing the class with the <WMSFilterServiceResponse>
    */
    WMSGetCapabilitiesResult( OGCWebServiceRequest request, 
                              OGCWebServiceException exception ) {
        super( request, exception );
        setCapabilities( capabilities );
    }

    /**
     * returns the capabilities as result of an GetCapabilities request. If an
     * excption raised processing the request or the request has been invalid
     * <tt>null</tt> will be returned.
     * @return the capabilities object or null
     */
    public WMSConfigurationType getCapabilities() {
        return capabilities;
    }

    /**
     * sets the capabilities as result of an GetCapabilities request.
     * @param capabilities 
    */
    public void setCapabilities( WMSConfigurationType capabilities ) {
        this.capabilities = capabilities;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSGetCapabilitiesResult.java,v $
Revision 1.10  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
