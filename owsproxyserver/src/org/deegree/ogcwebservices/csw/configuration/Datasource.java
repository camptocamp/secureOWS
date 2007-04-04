//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/configuration/Datasource.java,v 1.4 2006/07/12 16:59:32 poth Exp $
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
package org.deegree.ogcwebservices.csw.configuration;

import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.OGCWebService;

/**
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/07/12 16:59:32 $
 *
 * @since 2.0
 */
public class Datasource {

    private OnlineResource onlineResource = null;
    private OGCWebService dataService = null;
        
    /**
     * @param onlineResource
     * @param dataService
     */
    public Datasource(OnlineResource onlineResource, OGCWebService dataService) {
        super();
        this.onlineResource = onlineResource;
        this.dataService = dataService;
    }
        
    /**
     * returns the data service assigned with the <code>Datasource</code> 
     * @return
     */
    public OGCWebService getDataService() {
        return dataService;
    }
    
    /**
     * returns the @see OnLineResource where to access the capabilities 
     * of the the OWS assigned with the <code>Datasource</code>
     * @return
     */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Datasource.java,v $
Revision 1.4  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.3  2006/04/06 20:25:27  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.1  2005/09/09 08:19:36  poth
no message


********************************************************************** */