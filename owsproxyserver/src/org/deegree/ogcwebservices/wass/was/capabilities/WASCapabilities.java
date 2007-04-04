//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/capabilities/WASCapabilities.java,v 1.7 2006/06/27 13:10:47 bezema Exp $
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

package org.deegree.ogcwebservices.wass.was.capabilities;

import java.util.ArrayList;

import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wass.common.OWSCapabilitiesBaseType_1_0;
import org.deegree.ogcwebservices.wass.common.OperationsMetadata_1_0;
import org.deegree.ogcwebservices.wass.common.SupportedAuthenticationMethod;

/**
 * Encapsulates: GDI NRW WAS capabilities according to V1.0
 * 
 * Namespace: http://www.gdi-nrw.org/was
 * 
 * This class does not really contain any special data and is only used for consistent interface
 * reasons.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/06/27 13:10:47 $
 * 
 * @since 2.0
 */
public class WASCapabilities extends OWSCapabilitiesBaseType_1_0 {

    private static final long serialVersionUID = 4049719938261335584L;

    /**
     * Constructs new one from given values.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param supportedAuthenticationMethods
     */
    public WASCapabilities( String version, String updateSequence,
                           ServiceIdentification serviceIdentification,
                           ServiceProvider serviceProvider, OperationsMetadata_1_0 operationsMetadata,
                           ArrayList<SupportedAuthenticationMethod> supportedAuthenticationMethods ) {
        super( version, updateSequence, serviceIdentification, serviceProvider, operationsMetadata,
               supportedAuthenticationMethods );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WASCapabilities.java,v $
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/23 10:23:50  schmitz
 * Changes to this class. What the people have been up to: Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.5 2006/05/30 09:17:02 schmitz Changes to this class. What the
 * people have been up to: Docu test Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.4 2006/05/30 08:54:30 schmitz Changes
 * to this class. What the people have been up to: und mehr Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.3 2006/05/30
 * 08:54:07 schmitz Changes to this class. What the people have been up to: cvs test Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.2 2006/05/30 08:44:48 bezema Changes to this class. What the people have been up to:
 * Reararranging the layout (again) to use features of OOP. The owscommonDocument is the real
 * baseclass now. Changes to this class. What the people have been up to: Changes to this class.
 * What the people have been up to: Revision 1.1 2006/05/29 12:00:58 bezema Changes to this class.
 * What the people have been up to: Refactored the security and authentication webservices into one
 * package WASS (Web Authentication -and- Security Services), also created a common package and a
 * saml package which could be updated to work in the future. Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.4 2006/05/26
 * 14:38:32 schmitz Changes to this class. What the people have been up to: Added some KVP
 * constructors to WAS operations. Changes to this class. What the people have been up to: Added
 * some comments, updated the plan. Changes to this class. What the people have been up to:
 * Restructured WAS operations by adding an AbstractRequest base class. Changes to this class. What
 * the people have been up to: Changes to this class. What the people have been up to: Revision 1.3
 * 2006/05/22 15:47:05 bezema Changes to this class. What the people have been up to: Cleaning up
 * redundant Documents Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.2 2006/05/19 15:35:35 schmitz Changes to this
 * class. What the people have been up to: Updated the documentation, added the GetCapabilities
 * operation and implemented a rough WAService outline. Fixed some warnings. Changes to this class.
 * What the people have been up to: Changes to this class. What the people have been up to: Revision
 * 1.1 2006/05/15 09:54:16 bezema Changes to this class. What the people have been up to: New
 * approach to the nrw:gdi specs. Including ows_1_0 spec and saml spec Changes to this class. What
 * the people have been up to:
 * 
 **************************************************************************************************/
