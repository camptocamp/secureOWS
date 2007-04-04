//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/operation/WMSRequestBase.java,v 1.5 2006/09/15 09:18:29 schmitz Exp $
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

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/09/15 09:18:29 $
 *
 * @since 2.0
 */
class WMSRequestBase extends AbstractOGCWebServiceRequest {
    
    private static final long serialVersionUID = 5111124848569833598L;

    /**
     * @param version
     * @param id
     * @param vendorSpecificParameter
     */
    public WMSRequestBase(String version, String id, Map<String, String> vendorSpecificParameter) {
        super( version, id, vendorSpecificParameter );
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
$Log: WMSRequestBase.java,v $
Revision 1.5  2006/09/15 09:18:29  schmitz
Updated WMS to use SLD or SLD_BODY sld documents as default when also giving
LAYERS and STYLES parameters at the same time.

Revision 1.4  2006/07/28 08:01:27  schmitz
Updated the WMS for 1.1.1 compliance.
Fixed some documentation.

Revision 1.3  2006/04/06 20:25:27  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.1  2005/06/27 08:59:06  poth
no message

Revision 1.2  2005/04/06 12:02:08  poth
no message

Revision 1.1  2005/02/21 11:24:33  poth
no message


********************************************************************** */