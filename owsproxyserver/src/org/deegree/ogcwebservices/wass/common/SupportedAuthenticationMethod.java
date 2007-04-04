//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/SupportedAuthenticationMethod.java,v 1.3 2006/06/12 12:16:24 bezema Exp $
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
 * Encapsulated data: SupportedAuthenticationMethod element
 * 
 * Namespace: http://www.gdi-nrw.de/authentication
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/06/12 12:16:24 $
 * 
 * @since 2.0
 */
public class SupportedAuthenticationMethod {

    private URN method = null;

    private String metadata = null;

    private WASAuthenticationMethodMD wasMetadata = null;

    /**
     * Creates new instance with the given data.
     * 
     * @param methodURN
     * @param wasamd
     */
    public SupportedAuthenticationMethod( URN methodURN, WASAuthenticationMethodMD wasamd ) {
        this.method = methodURN;
        this.wasMetadata = wasamd;
    }

    /**
     * Creates new instance with the given data.
     * 
     * @param methodURN
     * @param ummd
     */
    public SupportedAuthenticationMethod( URN methodURN, String ummd ) {
        this.method = methodURN;
        this.metadata = ummd;
    }

    /**
     * @return Returns the metadata.
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * @return Returns the method.
     */
    public URN getMethod() {
        return method;
    }

    /**
     * @return Returns the wasMetadata.
     */
    public WASAuthenticationMethodMD getWasMetadata() {
        return wasMetadata;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: SupportedAuthenticationMethod.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/12 12:16:24  bezema
 * Changes to this class. What the people have been up to: Little rearanging of the GetSession classes, DoService should be ready updating some errors
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/05/30 11:44:51  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed some warnings.
 * Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Refactored the security and authentication webservices into one package WASS (Web
 * Authentication -and- Security Services), also created a common package and a saml package which
 * could be updated to work in the future.
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