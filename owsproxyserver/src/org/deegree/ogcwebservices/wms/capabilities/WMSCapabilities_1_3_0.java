//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/capabilities/WMSCapabilities_1_3_0.java,v 1.3 2006/09/08 08:42:02 schmitz Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wms.capabilities;

import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;
import org.deegree.owscommon_new.ServiceProvider;

/**
 * This class is an 1.3.0 extension of the WMSCapabilities class.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/09/08 08:42:02 $
 * 
 * @since 2.0
 */

public class WMSCapabilities_1_3_0 extends WMSCapabilities {

    private static final long serialVersionUID = 1081688101474553998L;

    private int maxWidth;
    
    private int maxHeight;
    
    private int layerLimit;
    
    /**
     * Constructs a new object with the given values. The difference to the 1.1.1 capabilities is
     * that no user defined symbolization can be used, and layerLimit, maxWidth and maxHeight were
     * added.
     */
    protected WMSCapabilities_1_3_0( String version, String updateSequence, 
                                     ServiceIdentification serviceIdentification,
                                     ServiceProvider serviceProvider,
                                     OperationsMetadata metadata, Layer layer, int layerLimit,
                                     int maxWidth, int maxHeight ) {
        super( version, updateSequence, serviceIdentification, serviceProvider, null, metadata, layer );
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.layerLimit = layerLimit;
    }

    /**
     * @return the layerLimit.
     */
    public int getLayerLimit() {
        return layerLimit;
    }

    /**
     * @return the maxHeight.
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * @return the maxWidth.
     */
    public int getMaxWidth() {
        return maxWidth;
    }
          
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSCapabilities_1_3_0.java,v $
Revision 1.3  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.2  2006/08/24 06:42:16  poth
File header corrected

Revision 1.1  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.



********************************************************************** */