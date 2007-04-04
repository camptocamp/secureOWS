//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/WASAuthenticationMethodMD.java,v 1.2 2006/06/19 12:47:26 schmitz Exp $
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

import java.net.URL;
import java.util.ArrayList;

/**
 * Encapsulated data: WASAuthenticationMethodMD element
 * 
 * Namespace: http://www.gdi-nrw.de/authentication
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/06/19 12:47:26 $
 * 
 * @since 2.0
 */
public class WASAuthenticationMethodMD {

    private String name = null;

    private URL url = null;

    private ArrayList<URN> authenticationMethods = null;

    /**
     * @param name
     * @param url
     * @param authenticationMethods
     */
    public WASAuthenticationMethodMD( String name, URL url, ArrayList<URN> authenticationMethods ) {
        this.name = name;
        this.url = url;
        this.authenticationMethods = authenticationMethods;
    }

    /**
     * @return Returns the authenticationMethods.
     */
    public ArrayList<URN> getAuthenticationMethods() {
        return authenticationMethods;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the url.
     */
    public URL getUrl() {
        return url;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WASAuthenticationMethodMD.java,v $
 * Changes to this class. What the people have been up to: Revision 1.2  2006/06/19 12:47:26  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Revision 1.1 2006/06/12 12:16:24 bezema Little rearanging of the GetSession classes, DoService
 * should be ready updating some errors
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.3 2006/05/23 15:20:50 bezema Cleaned up the warnings and added some minor methods
 * 
 * Revision 1.2 2006/05/19 15:35:35 schmitz Updated the documentation, added the GetCapabilities
 * operation and implemented a rough WAService outline. Fixed some warnings.
 * 
 * Revision 1.1 2006/05/15 15:22:19 bezema The authentication.xsd elements will now be parsed by
 * AuthenticationDocument. Data containing classes have also been created.
 * 
 * 
 **************************************************************************************************/