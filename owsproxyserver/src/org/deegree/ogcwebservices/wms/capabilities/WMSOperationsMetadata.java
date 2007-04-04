//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/capabilities/WMSOperationsMetadata.java,v 1.7 2006/10/22 20:32:08 poth Exp $
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
package org.deegree.ogcwebservices.wms.capabilities;

import java.util.ArrayList;
import java.util.List;

import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.OWSDomainType;

/**
 * Represents the <code>OperationMetadata</code> part in the capabilities
 * document of a WFS according to the
 * <code>Web Feature Service Implementation Specification 1.1.0</code>.
 * <p>
 * In addition to the <code>GetCapabilities</code> operation that all
 * <code>OWS 0.3</code> compliant services must implement, it may define some
 * or all of the following operations: <table border="1">
 * <table>
 *  <tr>
 *      <td></td>
 *  <tr>
 * </table>
 * 
 * 
 * @see org.deegree.ogcwebservices.getcapabilities.OperationsMetadata
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/10/22 20:32:08 $
 * 
 * @since 2.0
 */

public class WMSOperationsMetadata extends OperationsMetadata {

    private static final long serialVersionUID = 3864454877736817781L;

    /**
     * The name of the GetMap operation.
     */
    public static final String GETMAP = "GetMap";
    
    /**
     * The name of the GetMap operation.
     */
    public static final String MAP = "Map";
    
    /**
     * The name of the GetFeatureInfo operation.
     */
    public static final String GETFEATUREINFO = "GetFeatureInfo";
    
    /**
     * The name of the GetFeatureInfo operation.
     */
    public static final String FEATUREINFO = "FeatureInfo";
    
    /**
     * The name of the DescribeLayer operation.
     */
    public static final String DESCRIBELAYER = "DescribeLayer";
    
    /**
     * The name of the GetLegendGraphic operation.
     */
    public static final String GETLEGENDGRAPHIC = "GetLegendGraphic";
    
    /**
     * The name of the GetStyles operation.
     */
    public static final String GETSTYLES = "GetStyles";
    
    /**
     * The name of the PutStyles operation.
     */
    public static final String PUTSTYLES = "PutStyles";
      
    private Operation getMap;

    private Operation getFeatureInfo;
    
    private Operation describeLayer;
    
    private Operation getLegendGraphic;
    
    private Operation getStyles;
    
    private Operation putStyles;
    
    /**
     * Constructs a new <code>WMSOperationsMetadata</code> instance from the
     * given parameters.
     *
     * @param getCapabilities 
     * @param getMap 
     * @param getFeatureInfo 
     * @param describeLayer 
     * @param getLegendGraphic 
     * @param getStyles 
     * @param putStyles 
     * @param parameters 
     * @param constraints 
     */
    public WMSOperationsMetadata(Operation getCapabilities, Operation getMap, Operation getFeatureInfo,
                                 Operation describeLayer, Operation getLegendGraphic,
                                 Operation getStyles, Operation putStyles, 
                                 OWSDomainType[] parameters, OWSDomainType[] constraints) {
        super(getCapabilities, parameters, constraints);
        this.getMap = getMap;
        this.getFeatureInfo = getFeatureInfo;
        this.getLegendGraphic = getLegendGraphic;
        this.describeLayer = describeLayer;
        this.getStyles = getStyles;
        this.putStyles = putStyles;
    }

    /**
     * @return all <code>Operations</code> known to the WFS.
     * 
     */
    @Override
    public Operation[] getOperations() {
        List<Operation> list = new ArrayList<Operation>(10);
        list.add( getMap );
        list.add( getCapabilitiesOperation );
        if ( describeLayer != null ) {
            list.add( describeLayer );
        }
        if ( getFeatureInfo != null ) {
            list.add( getFeatureInfo );
        }
        if ( getStyles != null ) {
            list.add( getStyles );
        }
        if ( putStyles != null ) {
            list.add( putStyles );
        }
        if ( getLegendGraphic != null ) {
            list.add( getLegendGraphic );
        }
        Operation[] ops = new Operation[list.size()];
        return list.toArray( ops );
    }

    /**
     * @return Returns the describeLayer.
     */
    public Operation getDescribeLayer() {
        return describeLayer;
    }

    /**
     * @param describeLayer The describeLayer to set.
     */
    public void setDescribeLayer( Operation describeLayer ) {
        this.describeLayer = describeLayer;
    }

    /**
     * @return Returns the getFeatureInfo.
     */
    public Operation getGetFeatureInfo() {
        return getFeatureInfo;
    }

    /**
     * @param getFeatureInfo The getFeatureInfo to set.
     */
    public void setGetFeatureInfo( Operation getFeatureInfo ) {
        this.getFeatureInfo = getFeatureInfo;
    }

    /**
     * @return Returns the getLegendGraphic.
     */
    public Operation getGetLegendGraphic() {
        return getLegendGraphic;
    }

    /**
     * @param getLegendGraphic The getLegendGraphic to set.
     */
    public void setGetLegendGraphic( Operation getLegendGraphic ) {
        this.getLegendGraphic = getLegendGraphic;
    }

    /**
     * @return Returns the getMap.
     */
    public Operation getGetMap() {
        return getMap;
    }

    /**
     * @param getMap The getMap to set.
     */
    public void setGetMap( Operation getMap ) {
        this.getMap = getMap;
    }

    /**
     * @return Returns the getStyles.
     */
    public Operation getGetStyles() {
        return getStyles;
    }

    /**
     * @param getStyles The getStyles to set.
     */
    public void setGetStyles( Operation getStyles ) {
        this.getStyles = getStyles;
    }

    /**
     * @return Returns the putStyles.
     */
    public Operation getPutStyles() {
        return putStyles;
    }

    /**
     * @param putStyles The putStyles to set.
     */
    public void setPutStyles( Operation putStyles ) {
        this.putStyles = putStyles;
    }
 
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSOperationsMetadata.java,v $
Revision 1.7  2006/10/22 20:32:08  poth
support for vendor specific operation GetScaleBar removed

Revision 1.6  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
