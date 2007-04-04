//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/GetFeatureInfoHandler.java,v 1.6 2006/07/11 14:08:37 schmitz Exp $
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
package org.deegree.ogcwebservices.wms;

import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfoResult;

/**
 * Interface for defining access to GetFeatureInfoHandlers. Default implementation to 
 * be used if no other is specified in deegree WMS configuration is 
 * @see org.deegree.ogcwebservices.wms.DefaultGetFeatureInfoHandler
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/07/11 14:08:37 $
 *
 * @since 1.1
 */
public interface GetFeatureInfoHandler {
    /**
     * performs a GetFeatureInfo request and retruns the result encapsulated within
     * a <tt>WMSFeatureInfoResponse</tt> object. <p>
     * The method throws an WebServiceException that only shall be thrown if an
     * fatal error occurs that makes it imposible to return a result.
     * If something wents wrong performing the request (none fatal error) The 
     * exception shall be encapsulated within the response object to be returned 
     * to the client as requested (GetFeatureInfo-Request EXCEPTION-Parameter).
     *     
     * @return response to the GetFeatureInfo response
     * @throws OGCWebServiceException 
     */
    public abstract GetFeatureInfoResult performGetFeatureInfo() throws OGCWebServiceException;
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetFeatureInfoHandler.java,v $
Revision 1.6  2006/07/11 14:08:37  schmitz
Fixed some documentation warnings.

Revision 1.5  2006/04/06 20:25:29  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.3  2005/04/18 19:14:14  poth
no message


********************************************************************** */