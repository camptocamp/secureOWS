//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/capabilities/WSSCapabilities.java,v 1.6 2006/08/24 06:42:17 poth Exp $
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

package org.deegree.ogcwebservices.wass.wss.capabilities;

import java.util.ArrayList;

import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wass.common.OWSCapabilitiesBaseType_1_0;
import org.deegree.ogcwebservices.wass.common.OperationsMetadata_1_0;
import org.deegree.ogcwebservices.wass.common.SupportedAuthenticationMethod;

/**
 * A <code>WSSCapabilities</code> class encapsulates all the data which can be requested with a
 * GetCapabilities request. It's base class is OWSCapabilitiesBaseType_1_0 which is conform the
 * gdi-nrw access control version 1.0 specification.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */

public class WSSCapabilities extends OWSCapabilitiesBaseType_1_0 {
    /**
     * 
     */
    private static final long serialVersionUID = 2181625093642200664L;

    private String securedServiceType = null;

    /**
     * @param version
     * @param updateSequence
     * @param sf
     * @param sp
     * @param om
     * @param securedServiceType
     * @param am
     */
    public WSSCapabilities( String version, String updateSequence, ServiceIdentification sf,
                           ServiceProvider sp, OperationsMetadata_1_0 om, String securedServiceType,
                           ArrayList<SupportedAuthenticationMethod> am ) {
        super( version, updateSequence, sf, sp, om, am );

        this.securedServiceType = securedServiceType;
    }

    /**
     * @return the securedServiceType.
     */
    public String getSecuredServiceType() {
        return securedServiceType;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WSSCapabilities.java,v $
 * Changes to this class. What the people have been up to: Revision 1.6  2006/08/24 06:42:17  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/06/19 15:34:04  bezema
 * Changes to this class. What the people have been up to: changed wass to handle things the right way
 * Changes to this class. What the people have been up to: Revision
 * 1.3 2006/05/30 10:12:02 bezema Putting the cvs asci option to -kkv which will update the
 * $revision$ $author$ and $date$ variables in a cvs commit
 * 
 * Revision 1.2 2006/05/30 08:44:48 bezema Reararranging the layout (again) to use features of OOP.
 * The owscommonDocument is the real baseclass now.
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * Revision 1.1 2006/05/22 15:48:16 bezema Starting the parsing of the xml request in wss
 * 
 * 
 **************************************************************************************************/
