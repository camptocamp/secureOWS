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
package org.deegree.ogcwebservices;

import java.util.Map;

/**
 * This is the base interface for all request on OGC Web Services (OWS). Each
 * class that capsulates a request against an OWS has to implements this
 * interface.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0, $Revision: 1.10 $, $Date: 2006/07/12 14:46:16 $
 * 
 * @since 1.0
 *
 */
public interface OGCWebServiceRequest {   
    
    /**
     * Finally, the requests allow for optional vendor-specific parameters (VSPs)
     * that will enhance the results of a request. Typically, these are used for
     * private testing of non-standard functionality prior to possible
     * standardization. A generic client is not required or expected to make use
     * of these VSPs.
     * @return the vendor specificparameters
     */
    Map getVendorSpecificParameters();

    /**
     * Finally, the requests allow for optional vendor-specific parameters (VSPs)
     * that will enhance the results of a request. Typically, these are used for
     * private testing of non-standard functionality prior to possible
     * standardization. A generic client is not required or expected to make use
     * of these VSPs.
     * @param name the "key" of a vsp
     * @return the value requested by the key
     */
    String getVendorSpecificParameter(String name);

    /**
     * @return the ID of a request
     */
    String getId();
    
    /**
     * @return the requested service version
     */
    String getVersion();
    
    /**
     * @return the name of the service that is targeted by the request
     */
    String getServiceName();
        
    /**
     * @return the URI of a HTTP GET request. If the request doesn't support
     * HTTP GET a <tt>WebServiceException</tt> will be thrown
     * @throws OGCWebServiceException 
     * @deprecated should be replaced by a factory class TODO
     */
    String getRequestParameter() throws OGCWebServiceException;
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OGCWebServiceRequest.java,v $
Revision 1.10  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
