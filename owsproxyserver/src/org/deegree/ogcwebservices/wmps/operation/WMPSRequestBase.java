//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/operation/WMPSRequestBase.java,v 1.14 2006/08/10 07:11:35 deshmukh Exp $
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

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * Class represents the Request Base.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 * 
 */
class WMPSRequestBase extends AbstractOGCWebServiceRequest {

    private static final long serialVersionUID = 1722634545312388641L;

    /**
     * Initializing the WMPSRequestBase instance.
     * 
     * @param version
     * @param id
     * @param vendorSpecificParameter
     */
    public WMPSRequestBase( String version, String id, Map vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
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
 * Changes to this class. What the people have been up to: $Log: WMPSRequestBase.java,v $
 * Changes to this class. What the people have been up to: Revision 1.14  2006/08/10 07:11:35  deshmukh
 * Changes to this class. What the people have been up to: WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.13 2006/08/01 13:41:48 deshmukh Changes
 * to this class. What the people have been up to: The wmps configuration has been modified and
 * extended. Also fixed the javadoc. Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.12 2006/07/21 14:49:01 deshmukh
 * Changes to this class. What the people have been up to: Added vendor specific parameters for the
 * wmps Changes to this class. What the people have been up to: Revision 1.11 2006/07/12 14:46:18
 * poth comment footer added
 * 
 **************************************************************************************************/
