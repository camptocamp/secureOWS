//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wmsconf/layerconf/RemoveLayerOperation.java,v 1.11 2006/10/17 20:31:17 poth Exp $
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
package org.deegree.conf.services.wmsconf.layerconf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.wmsconf.WMSConfOperation;
import org.deegree.framework.util.KVP2Map;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;

/**
 * @author sncho
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveLayerOperation extends WMSConfOperation {

    public static final String REQUEST_NAME = "RemoveLayer";
    
    private String layerName;

    /**
     * @param name
     */
    public RemoveLayerOperation( String name ) {
        this.layerName = name;
    }

    public static RemoveLayerOperation create(HttpServletRequest request) throws Exception {
        
        Map map = KVP2Map.toMap( request );
        
        String name = (String)map.get("NAME");
        
        if ( name == null ) {
	    	throw new InvalidParameterValueException( "Parameter 'NAME' is missing." );
	    }
        
        return new RemoveLayerOperation( name );
    }
    
    public String performOperation(  HttpServletRequest request, HttpServletResponse response )
    	throws Exception {
        
        WMSCapabilitiesDocument wmsCaps = config.getWmsCapabilities();
        
        
        WMSCapabilitiesUtils wmsUtils = new WMSCapabilitiesUtils( wmsCaps );
        wmsUtils.removeLayer( this.layerName );
        
        config.saveCapabilites();
        
        return "Removed layer '" + this.layerName + "'";
    }

    /**
     * 
     */
    public String getOperationName() {
        return REQUEST_NAME;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoveLayerOperation.java,v $
Revision 1.11  2006/10/17 20:31:17  poth
*** empty log message ***

Revision 1.10  2006/08/24 06:38:30  poth
File header corrected

Revision 1.9  2006/06/28 20:20:15  poth
some code clean ups

Revision 1.8  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.6  2006/01/16 20:36:40  poth
*** empty log message ***

Revision 1.5  2005/12/12 13:55:06  taddei
fixed typo

Revision 1.4  2005/12/12 09:39:49  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.3  2005/12/07 14:50:17  taddei
completed imlementation

Revision 1.2  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */