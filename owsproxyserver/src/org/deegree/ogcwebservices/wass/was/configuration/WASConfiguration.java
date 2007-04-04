//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/configuration/WASConfiguration.java,v 1.7 2006/07/07 15:03:03 schmitz Exp $
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

package org.deegree.ogcwebservices.wass.was.configuration;

import org.deegree.ogcwebservices.wass.was.capabilities.WASCapabilities;

/**
 * Encapsulates the configuration data for a WAS.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/07/07 15:03:03 $
 * 
 * @since 2.0
 */

public class WASConfiguration extends WASCapabilities {

    private static final long serialVersionUID = -1135265074478652811L;

    private WASDeegreeParams deegreeParams = null;

    /**
     * @param cap
     * @param deegreeParams
     */
    public WASConfiguration( WASCapabilities cap, WASDeegreeParams deegreeParams ) {
        super( cap.getVersion(), cap.getUpdateSequence(), cap.getServiceIdentification(),
               cap.getServiceProvider(), cap.getOperationsMetadata(),
               cap.getAuthenticationMethods() );
        this.deegreeParams = deegreeParams;
    }

    /**
     * @return Returns the deegreeParams.
     */
    public WASDeegreeParams getDeegreeParams() {
        return deegreeParams;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WASConfiguration.java,v $
 * Changes to this class. What the people have been up to: Revision 1.7  2006/07/07 15:03:03  schmitz
 * Changes to this class. What the people have been up to: Fixed a few warnings.
 * Changes to this class. What the people have been up to: Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/23 10:23:50  schmitz
 * Changes to this class. What the people have been up to: Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/19 15:34:04  bezema
 * Changes to this class. What the people have been up to: changed wass to handle things the right way
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/06/09 12:58:32  schmitz
 * Changes to this class. What the people have been up to: Set up some tests for WAS/WSS and the URN class.
 * Changes to this class. What the people have been up to: Commented out some of the deegree param stuff in order for the
 * Changes to this class. What the people have been up to: tests to run.
 * Changes to this class. What the people have been up to: Tests have hardcoded URLs in them, so they won't run just anywhere.
 * Changes to this class. What the people have been up to: Revision 1.3 2006/05/30 15:11:28
 * bezema Working on the postclient from apachecommons to place a request to the services behind the
 * wss proxy
 * 
 * Revision 1.2 2006/05/30 08:44:48 bezema Reararranging the layout (again) to use features of OOP.
 * The owscommonDocument is the real baseclass now.
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.1 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
 * 
 * Revision 1.1 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
