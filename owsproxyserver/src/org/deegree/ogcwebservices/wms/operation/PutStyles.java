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

/**
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class PutStyles extends WMSRequestBase {

    private static final long serialVersionUID = 7539586791686753828L;

    /**
     * Creates a new WMSPutStyleRequest_Impl object.
     *
     * @param version 
     * @param id 
     * @param vendorSpecificParameter 
     */
    PutStyles( String version, String id, Map<String, String> vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: PutStyles.java,v $
 Revision 1.8  2006/09/15 09:18:29  schmitz
 Updated WMS to use SLD or SLD_BODY sld documents as default when also giving
 LAYERS and STYLES parameters at the same time.

 Revision 1.7  2006/09/08 08:42:02  schmitz
 Updated the WMS to be 1.1.1 conformant once again.
 Cleaned up the WMS code.
 Added cite WMS test data.

 Revision 1.6  2006/07/12 14:46:16  poth
 comment footer added

 ********************************************************************** */
