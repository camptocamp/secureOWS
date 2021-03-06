//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/GetMapHandler.java,v 1.8 2006/10/02 07:03:01 poth Exp $
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
package org.deegree.ogcwebservices.wmps;

import java.awt.Graphics;

import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 * Interface for defining access to GetMapHandlers. Default implementation to be used if no other is
 * specified in deegree WMS configuration is
 * 
 * @see org.deegree.ogcwebservices.wms.DefaultGetMapHandler
 * 
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/10/02 07:03:01 $
 * 
 * @since 1.1
 */
public interface GetMapHandler {

    public void performGetMap( Graphics g )
                            throws OGCWebServiceException;

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetMapHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/10/02 07:03:01  poth
 * Changes to this class. What the people have been up to: footer comment corrected
 * Changes to this class. What the people have been up to:
 * Revision 1.7  2006/06/12 09:34:53  deshmukh
 * extended the print map capabilites to support the get request and changed the db structure.
 * Revision 1.6
 * 2006/05/11 20:10:29 poth ** empty log message ***
 * 
 * Revision 1.5 2006/04/06 20:25:29 poth ** empty log message ***
 * 
 * Revision 1.4 2006/03/30 21:20:27 poth ** empty log message ***
 * 
 * Revision 1.3 2005/08/10 07:41:07 poth no message
 * 
 * Revision 1.2 2005/04/18 19:14:14 poth no message
 * 
 * 
 **************************************************************************************************/