//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/AuthenticationData.java,v 1.6 2006/08/30 11:51:50 poth Exp $
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

/**
 * Encapsulated data: authn:AuthenticationData element
 * 
 * Namespace: http://www.gdi-nrw.org/authentication
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/08/30 11:51:50 $
 * 
 * @since 2.0
 */
public class AuthenticationData {

    private URN authenticationMethod = null;

    private String credentials = null;

    /**
     * @param authenticationMethod
     * @param credentials
     */
    public AuthenticationData( URN authenticationMethod, String credentials ) {
        this.authenticationMethod = authenticationMethod;
        this.credentials = credentials;
    }

    /**
     * @return the Method of authentication
     * @see org.deegree.ogcwebservices.wass.common.URN
     */
    public URN getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * @return the Credentials
     */
    public String getCredentials() {
        return credentials;
    }

    /**
     * @return true, if authenticationMethod is by password
     */
    public boolean usesPasswordAuthentication() {
        return authenticationMethod.isWellformedGDINRW()
               && authenticationMethod.getAuthenticationMethod().equals( "password" );
    }

    /**
     * @return true, if authenticationMethod is by session
     */
    public boolean usesSessionAuthentication() {
        return authenticationMethod.isWellformedGDINRW()
               && authenticationMethod.getAuthenticationMethod().equals( "session" );
    }

    /**
     * @return true, if authenticationMethod is by anonymous
     */
    public boolean usesAnonymousAuthentication() {
        return authenticationMethod.isWellformedGDINRW()
               && authenticationMethod.getAuthenticationMethod().equals( "anonymous" );
    }

    /**
     * @return the username of the credentials or null, if authenticationMethod is not password
     */
    public String getUsername() {
        if ( !usesPasswordAuthentication() ) {
            return null;
        }
        if ( credentials.indexOf( ',' ) > 0 ) {
            return credentials.substring( 0, credentials.indexOf( ',' ) );
        } 
        return credentials;
    }

    /**
     * @return the password of the credentials or null, if authenticationMethod is not password
     */
    public String getPassword() {
        if ( !usesPasswordAuthentication() || credentials.indexOf( ',' ) < 0 ) {
            return null;
        }
        return credentials.substring( credentials.indexOf( ',' ) + 1 );
       
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: AuthenticationData.java,v $
 * Revision 1.6  2006/08/30 11:51:50  poth
 * support for sessionID authentification/authorization added
 *
 * Revision 1.5  2006/08/29 19:14:17  poth
 * code formating / footer correction
 *
 * Revision 1.4  2006/06/16 15:01:05  schmitz
 * Fixed the WSS to work with all kinds of operation tests. It checks out
 * with both XML and KVP requests.
 * Changes to this class. What the people have been up to:
 * Revision 1.3  2006/05/30 12:46:33  bezema
 * DoService is now handled
 * Changes
 * to this class. What the people have been up to: Revision 1.2 2006/05/30 11:44:51 schmitz Changes
 * to this class. What the people have been up to: Updated the documentation, fixed some warnings.
 * Revision 1.1 2006/05/29 12:00:58 bezema
 * Refactored the security and authentication webservices into one package WASS (Web Authentication
 * -and- Security Services), also created a common package and a saml package which could be updated
 * to work in the future.
 * 
 * Revision 1.4 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
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
