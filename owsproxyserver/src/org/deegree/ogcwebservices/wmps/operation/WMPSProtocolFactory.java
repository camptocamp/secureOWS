// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/wms/protocol/WMPSProtocolFactory.java,v
// 1.7 2004/07/12 06:12:11 ap Exp $
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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wmps.capabilities.WMPSCapabilities;

/**
 * Factory that builds the different types of WMPS-Requests & Responses.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * 
 * @version 2.0
 */
public class WMPSProtocolFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( WMPSProtocolFactory.class );

    /**
     * creates an instance of a <tt>WMPSGetCapabilitiesResult</tt> object
     * 
     * @param request
     *            request that lead to the response
     * @param exception
     *            exception if one occuered
     * @param capabilities
     *            WMS capabilities
     * 
     * @return <tt>WMPSGetCapabilitiesResult</tt>
     */
    public static WMPSGetCapabilitiesResult createGetCapabilitiesResult(
                                                                        OGCWebServiceRequest request,
                                                                        OGCWebServiceException exception,
                                                                        WMPSCapabilities capabilities ) {
        LOG.entering();

        WMPSGetCapabilitiesResult res = null;
        if ( exception == null ) {
            res = new WMPSGetCapabilitiesResult( request, capabilities );
        } else {
            res = new WMPSGetCapabilitiesResult( request, exception );
        }

        LOG.exiting();
        return res;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMPSProtocolFactory.java,v $
Revision 1.15  2006/10/22 20:32:08  poth
support for vendor specific operation GetScaleBar removed

Revision 1.14  2006/08/10 07:11:35  deshmukh
WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.

Revision 1.13  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
