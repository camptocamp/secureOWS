//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/RemoteWMSDataSource.java,v 1.11 2006/09/08 08:42:01 schmitz Exp $
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
package org.deegree.ogcwebservices.wms.configuration;

import java.awt.Color;
import java.net.URL;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.wms.RemoteWMService;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.operation.GetMap;


/**
 * Data source description for a REMOTEWMS datasource 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision: 1.11 $, $Date: 2006/09/08 08:42:01 $
 */
public class RemoteWMSDataSource extends AbstractDataSource {
        
    private GetMap getMap = null;
    private Color[] transparentColors = null;
    
    /**
     * Creates a new DataSource object.
     * @param querable 
     * @param failOnException 
     * 
     * @param name
     *            name of the featuretype to access
     * @param type
     *            type of the data source (REMOTEWCS, LOCALWCS)
     * @param ows
     *            <tt>OGCWebService</tt> instance for accessing the data
     *            source
     * @param capabilitiesURL 
     * @param scaleHint
     *            filter condition
     * @param validArea 
     * @param getMap 
     * @param transparentColors 
     * @param featureInfoTransform 
     * @param reqTimeLimit 
     */
    public RemoteWMSDataSource( boolean querable, boolean failOnException, QualifiedName name, 
                               int type, OGCWebService ows, URL capabilitiesURL, ScaleHint scaleHint, 
                               Geometry validArea, GetMap getMap, Color[] transparentColors, 
                               URL featureInfoTransform, int reqTimeLimit ) {
        super( querable, failOnException, name, type, ows, capabilitiesURL, 
               scaleHint, validArea, featureInfoTransform, reqTimeLimit  );
        this.getMap = getMap;
        this.transparentColors = transparentColors;
    }

    /**
     * returns an instance of a <tt>GetMapRequest</tt> encapsulating the
     * filter conditions against a remote WMS. The request object contains:
     * WMTVER, LAYERS, STYLES, FORMAT, TRANSPARENT, VENDORSPECIFICPARAMETERS
     * 
     * @return filter conditions
     *  
     */
    public GetMap getGetMapRequest() {
        return getMap;
    }
    
    /**
     * @return an array of colors that shall be treated as transparent
     */     
    public Color[] getTransparentColors() {
        return transparentColors;
    }
    
    /**
     * returns an instance of the <tt>OGCWebService</tt> that represents the
     * datasource. Notice: if more than one layer uses data that are offered by
     * the same OWS the deegree WMS shall just use one instance for accessing
     * the OWS
     *  
     */
    @Override
    public OGCWebService getOGCWebService() {       
        try {
            return new RemoteWMService( (WMSCapabilities)ows.getCapabilities() ); 
        } catch (Exception ignore) {}   
        return null;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoteWMSDataSource.java,v $
Revision 1.11  2006/09/08 08:42:01  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.10  2006/07/27 13:08:46  poth
support for request time limit added for each datasource added

Revision 1.9  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.8  2006/04/04 20:39:41  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.6  2005/11/22 17:19:13  poth
no message

Revision 1.5  2005/08/05 09:42:20  poth
no message

Revision 1.4  2005/08/04 08:56:58  poth
no message

Revision 1.3  2005/07/22 20:51:54  poth
no message

Revision 1.2  2005/06/28 15:58:11  poth
no message

Revision 1.1  2005/06/22 15:33:00  poth
no message



********************************************************************** */