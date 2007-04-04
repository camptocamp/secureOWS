//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/operation/WPVSRequestBase.java,v 1.5 2006/11/23 11:46:40 bezema Exp $
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
 Aennchenstraße 19
 53177 Bonn
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

package org.deegree.ogcwebservices.wpvs.operation;

import java.util.Map;

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * Conveniece class for all WPVS requests (with the exception of GetCapabilities), for the moment
 * only the GetViewRequest is supported but it might necessary to implement the GetDescription
 * operation for this purpose this class is Abstract.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/11/23 11:46:40 $
 * 
 */
abstract class WPVSRequestBase extends AbstractOGCWebServiceRequest {

    /**
     * Default for <code>WPVSRequestBase</code>s.
     * 
     * @param version
     *            the service vrsion
     * @param id
     *            the servce id
     * @param vendorSpecificParameter
     *            a <code>Map</code> containing vendor-specifc parameters
     */
    public WPVSRequestBase( String version, String id, Map<String, String> vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
    }

    /**
     * returns 'WPVS' as service name.
     */
    public String getServiceName() {
        return "WPVS";
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WPVSRequestBase.java,v $
 * Changes to this class. What the people have been up to: Revision 1.5  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision
 * 1.4 2006/08/24 06:42:16 poth File header corrected
 * 
 * Revision 1.3 2006/04/06 20:25:31 poth ** empty log message ***
 * 
 * Revision 1.2 2006/03/30 21:20:28 poth ** empty log message ***
 * 
 * Revision 1.1 2005/12/15 15:22:33 taddei added WPVSRequestBase and GetView
 * 
 * 
 **************************************************************************************************/