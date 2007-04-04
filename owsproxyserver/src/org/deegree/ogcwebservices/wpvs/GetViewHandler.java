//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/GetViewHandler.java,v 1.14 2006/11/27 11:54:33 bezema Exp $
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

package org.deegree.ogcwebservices.wpvs;

import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wpvs.operation.GetView;
import org.deegree.ogcwebservices.wpvs.operation.GetViewResponse;

/**
 * Super class for GetView handlers.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.14 $, $Date: 2006/11/27 11:54:33 $
 * 
 */
public abstract class GetViewHandler {

    private WPVService owner;
    
    /**
     * Creates a new GetViewHandler using <code>ownerService</code>. 
     * @param ownerService
     */
    protected GetViewHandler( WPVService ownerService ) {
        this.owner = ownerService;
    }

    /**
     * Handle the GetView request given by <code>getViewRequest</code>
     * @param getViewRequest te WPVS GetView request
     * @return an instance of GetViewResponse
     * @throws OGCWebServiceException 
     */
    public abstract GetViewResponse handleRequest( GetView getViewRequest ) 
    	throws OGCWebServiceException;

    /**
     * Returns the WPVService which owns this configuration
     * @return the WPVService which owns this configuration
     */
    protected final WPVService getOwner() {
        return owner;
    }
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetViewHandler.java,v $
Revision 1.14  2006/11/27 11:54:33  bezema
Cleaned up and fixed javadocs

Revision 1.12  2006/11/23 11:46:02  bezema
The initial version of the new wpvs

Revision 1.11  2006/07/18 15:13:32  taddei
changes in DEM (WCS) geometry

Revision 1.10  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.9  2006/04/06 20:25:30  poth
*** empty log message ***

Revision 1.8  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.7  2006/03/29 15:07:03  taddei
removed unused method

Revision 1.6  2006/03/16 11:32:58  taddei
removed unused and unwanted method

Revision 1.5  2006/01/18 08:58:13  taddei
implementation (WFS)

Revision 1.3  2005/12/21 13:49:01  taddei
dummy functions for live testing

Revision 1.2  2005/12/16 15:18:13  taddei
made abstract, added abstract methods

Revision 1.1  2005/12/15 16:54:15  taddei
added GetView handler


********************************************************************** */