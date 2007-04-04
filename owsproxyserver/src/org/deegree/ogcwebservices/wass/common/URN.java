//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/URN.java,v 1.4 2006/06/27 13:10:47 bezema Exp $
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
 * Encapsulates a Uniform Resource Name (URN) which encodes an authentication method according to
 * the GDI NRW access control specification.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/06/27 13:10:47 $
 * 
 * @since 2.0
 */
public class URN {

    private String urn;

    /**
     * Creates new one from a String.
     * 
     * @param urn
     *            the string
     */
    public URN( String urn ) {
        this.urn = urn;
    }

    /**
     * Returns the last part of the name, or null, if it is not a wellformed GDI NRW authentication.
     * method URN.
     * 
     * @return the name, or null
     */
    public String getAuthenticationMethod() {
        if ( !isWellformedGDINRW() )
            return null;
        return getLastName();
    }

    /**
     * Returns the last part of the name, or null, if it is not a URN.
     * 
     * @return the last part of this URN
     */
    public String getLastName() {
        if ( urn == null )
            return null;
        if ( !urn.startsWith( "urn:" ) )
            return null;
        return urn.substring( urn.lastIndexOf( ':' ) + 1 );
    }

    /**
     * Returns, whether this is a wellformed GDI NRW authentication method URN.
     * 
     * @return true, if it is
     */
    public boolean isWellformedGDINRW() {
        if ( urn == null )
            return false;
        String lastName = getLastName();
        if ( urn.startsWith( "urn:x-gdi-nrw:authnMethod:1.0:" ) )
            if ( lastName.equalsIgnoreCase( "password" ) || lastName.equalsIgnoreCase( "was" )
                 || lastName.equalsIgnoreCase( "session" )
                 || lastName.equalsIgnoreCase( "anonymous" ) )
                return true;
        return false;
    }
    
    /**
     * @param other
     * @return true if other equals this URN
     */
    public boolean equals ( URN other ){
        if( other == null )
            return false;
        if( !other.isWellformedGDINRW() || !this.isWellformedGDINRW() )
            return false;
        return other.getLastName().equals( this.getLastName() );
    }

    @Override
    public String toString() {
        return urn;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: URN.java,v $
 * Changes to this class. What the people have been up to: Revision 1.4  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/09 12:58:32  schmitz
 * Changes to this class. What the people have been up to: Set up some tests for WAS/WSS and the URN class.
 * Changes to this class. What the people have been up to: Commented out some of the deegree param stuff in order for the
 * Changes to this class. What the people have been up to: tests to run.
 * Changes to this class. What the people have been up to: Tests have hardcoded URLs in them, so they won't run just anywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/05/30 11:44:51  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed some warnings.
 * Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Refactored the security and authentication webservices into one package WASS (Web
 * Authentication -and- Security Services), also created a common package and a saml package which
 * could be updated to work in the future.
 * 
 * Revision 1.4 2006/05/26 14:38:32 schmitz Added some KVP constructors to WAS operations. Added
 * some comments, updated the plan. Restructured WAS operations by adding an AbstractRequest base
 * class.
 * 
 * Revision 1.3 2006/05/26 11:55:09 schmitz Extended the handlers to actually do something useful.
 * Added configuration package to WAS, added plan text file. Added GetSessionHandler interface,
 * added CloseSessionHandler.
 * 
 * Revision 1.2 2006/05/15 15:22:19 bezema The authentication.xsd elements will now be parsed by
 * AuthenticationDocument. Data containing classes have also been created.
 * 
 * Revision 1.1 2006/04/24 16:01:48 bezema Initializing gac (GDI NRW Access Control)
 * 
 * 
 **************************************************************************************************/
