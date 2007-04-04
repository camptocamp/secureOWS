//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/OperationsMetadata_1_0.java,v 1.3 2006/06/19 12:47:26 schmitz Exp $
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

import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.OWSDomainType;

/**
 * Encapsulated data: OperationsMetadata
 * 
 * Namespace: http://www.opengis.net/ows
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/06/19 12:47:26 $
 * 
 * @since 2.0
 */
public class OperationsMetadata_1_0 extends OperationsMetadata {

    private static final long serialVersionUID = 3587847964265446945L;

    private String extendedCapabilities = null;

    private Operation_1_0[] allOperations = null;

    /**
     * Creates new one from data.
     * 
     * @param operations
     * @param parameters
     * @param constraints
     * @param extendedCapabilities
     * @param describeUser
     * @param getCapabilities
     */
    public OperationsMetadata_1_0( Operation_1_0[] operations, OWSDomainType[] parameters,
                                  OWSDomainType[] constraints, String extendedCapabilities,
                                  Operation_1_0 describeUser, Operation_1_0 getCapabilities ) {
        super( getCapabilities, parameters, constraints );
        this.allOperations = operations;
        this.describeUser = describeUser;
        this.extendedCapabilities = extendedCapabilities;
    }

    /*
     * Not specified in nrwgdi but needed by deegree to get the id (ip) from a user.
     */
    private Operation_1_0 describeUser = null;

    /**
     * @return all operations
     */
    public Operation_1_0[] getAllOperations() {
        return allOperations;
    }

    /**
     * @return Returns the describeUser operation.
     */
    public Operation_1_0 getDescribeUser() {
        return describeUser;
    }

    /**
     * @return Returns the extendedCapabilities.
     */
    public String getExtendedCapabilities() {
        return extendedCapabilities;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: OperationsMetadata_1_0.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/19 12:47:26  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2 2006/05/30 11:44:51 schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed some
 * warnings. Changes to this class. What the people have been up to: Revision 1.1 2006/05/29
 * 16:13:00 schmitz Added an XMLFactory to create WAS and WSS GetCapabilities responses. Also added
 * the XML templates for this purpose. Renamed the WASOperationsMetadata to OperationsMetadata_1_0
 * and made some minor changes to some of the related bean classes.
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/26 14:38:32 schmitz Added some KVP constructors to WAS operations. Added
 * some comments, updated the plan. Restructured WAS operations by adding an AbstractRequest base
 * class.
 * 
 * Revision 1.1 2006/05/15 09:54:16 bezema New approach to the nrw:gdi specs. Including ows_1_0 spec
 * and saml spec
 * 
 * 
 **************************************************************************************************/
