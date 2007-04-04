//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/GetSessionHandler.java,v 1.3 2006/06/19 12:47:26 schmitz Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Meckenheimer Allee 176
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
package org.deegree.ogcwebservices.wass.common;

import org.deegree.security.GeneralSecurityException;
import org.deegree.security.session.SessionStatusException;

/**
 * Interface to be used for all GetSession handlers.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/06/19 12:47:26 $
 * 
 * @since 2.0
 */

public interface GetSessionHandler {

    /**
     * Returns a new session ID.
     * 
     * @param request
     *            the request on which to base the authentication
     * @return the new session ID or null
     * @throws SessionStatusException
     * @throws GeneralSecurityException
     */
    public String handleRequest( GetSession request )
                            throws SessionStatusException, GeneralSecurityException;

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetSessionHandler.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/19 12:47:26  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.2 2006/05/30 11:44:51 schmitz Changes to this class. What the
 * people have been up to: Updated the documentation, fixed some warnings. Changes to this class.
 * What the people have been up to: Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security
 * and authentication webservices into one package WASS (Web Authentication -and- Security
 * Services), also created a common package and a saml package which could be updated to work in the
 * future.
 * 
 * Revision 1.2 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
 * 
 * 
 * 
 **************************************************************************************************/
