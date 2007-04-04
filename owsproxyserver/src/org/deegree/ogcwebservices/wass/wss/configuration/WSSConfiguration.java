//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/configuration/WSSConfiguration.java,v 1.21 2006/08/24 06:42:17 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
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

package org.deegree.ogcwebservices.wass.wss.configuration;

import org.deegree.ogcwebservices.wass.wss.capabilities.WSSCapabilities;

/**
 * The configuration class represents the capabilities and the "deegree specific parameter" which
 * will be sent back to the clienst getCapabilities request.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.21 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */

public class WSSConfiguration extends WSSCapabilities {

    private static final long serialVersionUID = -1135265074478652811L;

    private WSSDeegreeParams deegreeParams = null;

    /**
     * @param capabilities
     * @param deegreeParams
     */
    public WSSConfiguration( WSSCapabilities capabilities, WSSDeegreeParams deegreeParams ) {
        super( capabilities.getVersion(), capabilities.getUpdateSequence(),
               capabilities.getServiceIdentification(), capabilities.getServiceProvider(),
               capabilities.getOperationsMetadata(), capabilities.getSecuredServiceType(),
               capabilities.getAuthenticationMethods() );
        this.deegreeParams = deegreeParams;
    }
    
    

    /**
     * @return Returns the deegree specific Parameters.
     */
    public WSSDeegreeParams getDeegreeParams() {
        return deegreeParams;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WSSConfiguration.java,v $
 * Changes to this class. What the people have been up to: Revision 1.21  2006/08/24 06:42:17  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.20  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.19  2006/06/20 15:31:05  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.18 2006/06/19 15:34:04 bezema Changes
 * to this class. What the people have been up to: changed wass to handle things the right way
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.17 2006/06/12 13:32:29 bezema Changes to this class. What the people
 * have been up to: kvp is implemented Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.16 2006/06/09 12:58:32 schmitz
 * Changes to this class. What the people have been up to: Set up some tests for WAS/WSS and the URN
 * class. Changes to this class. What the people have been up to: Commented out some of the deegree
 * param stuff in order for the Changes to this class. What the people have been up to: tests to
 * run. Changes to this class. What the people have been up to: Tests have hardcoded URLs in them,
 * so they won't run just anywhere. Changes to this class. What the people have been up to: Revision
 * 1.15 2006/05/30 10:12:02 bezema Putting the cvs asci option to -kkv which will update the
 * $revision$ $author$ and $date$ variables in a cvs commit
 * 
 * Revision 1.14 2006/05/30 09:29:29 bezema docu test
 * 
 * Revision 1.13 2006/05/30 09:27:31 bezema docu test
 * 
 * Revision 1.12 2006/05/30 09:25:26 bezema docu test
 * 
 * Revision 1.11 2006/05/30 09:25:11 bezema docu test
 * 
 * Revision 1.10 2006/05/30 09:24:24 bezema docu test
 * 
 * Revision 1.9 2006/05/30 09:23:30 bezema docu test
 * 
 * Revision 1.8 2006/05/30 09:23:13 bezema docu test
 * 
 * Revision 1.7 2006/05/30 09:22:43 bezema docu test
 * 
 * Revision 1.6 2006/05/30 09:20:57 bezema docu test
 * 
 * Revision 1.5 2006/05/30 08:54:53 bezema docu test
 * 
 * Revision 1.4 2006/05/30 08:49:47 bezema docu test
 * 
 * Revision 1.3 2006/05/30 08:48:42 bezema docu test
 * 
 * Revision 1.2 2006/05/30 08:44:48 bezema Reararranging the layout (again) to use features of OOP.
 * The owscommonDocument is the real baseclass now.
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.1 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
