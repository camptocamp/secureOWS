//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/RequestManager.java,v 1.11 2006/10/02 06:30:35 poth Exp $
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

import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wmps.operation.PrintMapResponseDocument;

/**
 * Interface for defining access to RequestManager. Default implementation to be used if no other is
 * specified in deegree WMPS configuration is
 * 
 * @see org.deegree.ogcwebservices.wms.DefaultGetMapHandler
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public interface RequestManager {

    public void saveRequestToDB()
                            throws OGCWebServiceException;

    public PrintMapResponseDocument createInitialResponse( String message )
                            throws OGCWebServiceException;

    public void sendEmail( PrintMapResponseDocument response )
                            throws OGCWebServiceException;

    public PrintMapResponseDocument createFinalResponse( String message,
                                                         String exception )
                            throws OGCWebServiceException;
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: RequestManager.java,v $
 * Changes to this class. What the people have been up to: Revision 1.11  2006/10/02 06:30:35  poth
 * Changes to this class. What the people have been up to: bug fixes
 * Changes to this class. What the people have been up to:
 * Revision 1.10  2006/08/23 10:21:12  deshmukh
 * detailed response messages in case of exception added.
 * Changes to
 * this class. What the people have been up to: Revision 1.9 2006/07/31 11:21:07 deshmukh Changes to
 * this class. What the people have been up to: wmps implemention... Changes to this class. What the
 * people have been up to: Revision 1.8 2006/07/12 14:46:16 poth comment footer added
 * 
 **************************************************************************************************/
