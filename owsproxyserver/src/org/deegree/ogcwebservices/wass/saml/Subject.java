//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/saml/Subject.java,v 1.3 2006/06/19 12:47:09 schmitz Exp $
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

package org.deegree.ogcwebservices.wass.saml;

import java.net.URI;

/**
 * Encapsulated data: Subject element
 * 
 * Namespace: http://urn:oasis:names:tc.SAML:1.0:assertion
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/06/19 12:47:09 $
 * 
 * @since 2.0
 */
public class Subject {

    private String name = null;

    private String nameQualifier = null;

    private URI format = null;

    private URI[] confirmationMethods = null;

    private String confirmationData = null;

    /**
     * @param name
     * @param nameQualifier
     * @param format
     */
    public Subject( String name, String nameQualifier, URI format ) {
        this.name = name;
        this.nameQualifier = nameQualifier;
        this.format = format;
    }

    /**
     * @param name
     * @param nameQualifier
     * @param format
     * @param confirmationMethods
     * @param confirmationData
     */
    public Subject( String name, String nameQualifier, URI format, URI[] confirmationMethods,
                   String confirmationData ) {
        this( name, nameQualifier, format );
        this.confirmationMethods = confirmationMethods;
        this.confirmationData = confirmationData;
    }

    /**
     * @param confirmationMethods
     * @param confirmationData
     */
    public Subject( URI[] confirmationMethods, String confirmationData ) {
        this.confirmationMethods = confirmationMethods;
        this.confirmationData = confirmationData;
    }

    /**
     * @return Returns the confirmationData.
     */
    public String getConfirmationData() {
        return confirmationData;
    }

    /**
     * @return Returns the confirmationMethods.
     */
    public URI[] getConfirmationMethods() {
        return confirmationMethods;
    }

    /**
     * @return Returns the format.
     */
    public URI getFormat() {
        return format;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the nameQualifier.
     */
    public String getNameQualifier() {
        return nameQualifier;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: Subject.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/19 12:47:09  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/05/29 16:24:59  bezema
 * Changes to this class. What the people have been up to: Rearranging the layout of the wss and creating the doservice classes. The WSService class is implemented as well
 * Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Refactored the security and authentication webservices into one package WASS (Web
 * Authentication -and- Security Services), also created a common package and a saml package which
 * could be updated to work in the future.
 * 
 * Revision 1.1 2006/05/15 09:54:16 bezema New approach to the nrw:gdi specs. Including ows_1_0 spec
 * and saml spec
 * 
 * 
 **************************************************************************************************/