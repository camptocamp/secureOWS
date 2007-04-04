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

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;

/**
 * This interface desribes the access to the parameters common to a OGC
 * GetCapabilities request. It inherits three accessor methods from the
 * general OGC web service request interface.
 *
 * <p>--------------------------------------------------------</p>
 *
 * @author Katharina Lupp <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @version $Revision: 1.9 $ $Date: 2006/10/27 13:32:06 $
 */
public class WMSGetCapabilities extends GetCapabilities {

    private static final long serialVersionUID = -7885976233890866824L;

    private static final ILogger LOG = LoggerFactory.getLogger( WMSGetCapabilities.class );

    /**
     * creates an WMS GetCapabilities Request
     * 
     * @param paramMap
     *            the parameters of the request
     * @return the GetCapabilities request
     * @throws InconsistentRequestException
     *             if the request is inconsistent
     * @throws MissingParameterValueException 
     */
    public static WMSGetCapabilities create( Map<String,String> paramMap )
                            throws InconsistentRequestException, MissingParameterValueException {
        LOG.logDebug( "Request parameters: " + paramMap );
        String version = getParam( "VERSION", paramMap, null );

        if ( version == null ) {
            version = getParam( "WMTVER", paramMap, null );
        }

        String service = getRequiredParam( "SERVICE", paramMap );
        String updateSequence = getParam( "UPDATESEQUENCE", paramMap, null );

        if ( !service.equals( "WMS" ) ) {
            throw new InconsistentRequestException( "Parameter 'SERVICE' must be 'WMS'." );
        }

        return new WMSGetCapabilities( getParam( "ID", paramMap, null ), version, 
                                       updateSequence, paramMap );
    }

    /**
     * Creates a new WMSGetCapabilities object.
     *
     * @param updateSequence 
     * @param version 
     * @param id 
     * @param vendorSpecific
     */
    WMSGetCapabilities( String version, String id, String updateSequence,
                        Map<String,String> vendorSpecific ) {
        super( version, id, updateSequence, null, null, null, vendorSpecific );
    }

    /** returns the URI of a HTTP GET request. 
     *
     */
    @Override
    public String getRequestParameter()
                            throws OGCWebServiceException {
        if ( getVersion().equals( "1.0.0" ) ) {
            return "service=WMS&version=" + getVersion() + "&request=capabilities";
        }
        return "service=WMS&version=" + getVersion() + "&request=GetCapabilities";
    }

    /**
     * returns 'WMS' as service name
     */
    public String getServiceName() {
        return "WMS";
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WMSGetCapabilities.java,v $
 Revision 1.9  2006/10/27 13:32:06  poth
 support for vendorspecific parameters added

 Revision 1.8  2006/10/27 09:52:23  schmitz
 Brought the WMS up to date regarding 1.1.1 and 1.3.0 conformance.
 Fixed a bug while creating the default GetLegendGraphics URLs.

 Revision 1.7  2006/07/28 08:01:27  schmitz
 Updated the WMS for 1.1.1 compliance.
 Fixed some documentation.

 Revision 1.6  2006/07/13 12:33:39  poth
 useless parameter passed to constructor removed

 Revision 1.5  2006/07/12 14:46:16  poth
 comment footer added

 ********************************************************************** */
