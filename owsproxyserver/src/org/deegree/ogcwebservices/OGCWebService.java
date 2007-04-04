// $Header: /cvsroot/deegree/src/org/deegree/ogcwebservices/OGCWebService.java,v
// 1.7 2004/06/23 13:37:40 mschneider Exp $
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

import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/07/21 14:07:22 $
 * 
 * @since 2.0
 */
public interface OGCWebService {

    /**
     * returns the capabilities of a OGC web service
     * 
     * @return
     */
    OGCCapabilities getCapabilities();

    /**
     * the implementation of this method performs the handling of the passed
     * OGCWebServiceEvent directly and returns the result to the calling class/
     * method
     *
     * @param request request (WMS, WCS, WFS, CSW, WFS-G, WMPS) to perform
     *
     * @throws WebServiceException 
     */
    Object doService(OGCWebServiceRequest request)
            throws OGCWebServiceException;

}

/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * OGCWebService.java,v $ Revision 1.7 2004/06/23 13:37:40 mschneider More work
 * on the CatalogConfiguration.
 * 
 * Revision 1.6 2004/06/21 08:05:49 ap no message
 * 
 * Revision 1.5 2004/06/18 08:33:31 ap no message
 * 
 * Revision 1.4 2004/06/16 09:46:02 ap no message
 * 
 *  
 ******************************************************************************/