//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/OWSCapabilitiesBaseType_1_0.java,v 1.4 2006/06/27 13:10:47 bezema Exp $
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

import java.util.ArrayList;

import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;

/**
 * Encapsulates: OWS capabilities according to V1.0
 * 
 * Namespace: http://www.opengis.net/ows
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/06/27 13:10:47 $
 * 
 * @since 2.0
 */
public class OWSCapabilitiesBaseType_1_0 extends OGCCapabilities {

    private static final long serialVersionUID = -7316008493729217865L;

    private ServiceIdentification serviceIdentification = null;

    private ServiceProvider serviceProvider = null;

    private OperationsMetadata_1_0 operationsMetadata = null;

    private ArrayList<SupportedAuthenticationMethod> authenticationMethods = null;
    
    private boolean passwordAuthenticationSupported = false;

    private boolean sessionAuthenticationSupported = false;
    
    private boolean wasAuthenticationSupported = false;
    
    private boolean anonymousAuthenticationSupported = false;

    /**
     * Creates new instance from the given data.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param authenticationMethods
     */
    public OWSCapabilitiesBaseType_1_0(
                                       String version,
                                       String updateSequence,
                                       ServiceIdentification serviceIdentification,
                                       ServiceProvider serviceProvider,
                                       OperationsMetadata_1_0 operationsMetadata,
                                       ArrayList<SupportedAuthenticationMethod> authenticationMethods ) {
        super( version, updateSequence );
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.authenticationMethods = authenticationMethods;
        
        for( SupportedAuthenticationMethod method: this.authenticationMethods ){
            if (method.getMethod().isWellformedGDINRW() && method.getMethod().getAuthenticationMethod().equals( "password" ) )
                passwordAuthenticationSupported = true;
            if (method.getMethod().isWellformedGDINRW() && method.getMethod().getAuthenticationMethod().equals( "session" ) )
                sessionAuthenticationSupported = true;
            if (method.getMethod().isWellformedGDINRW() && method.getMethod().getAuthenticationMethod().equals( "was" ) )
                wasAuthenticationSupported = true;
            if (method.getMethod().isWellformedGDINRW() && method.getMethod().getAuthenticationMethod().equals( "anonymous" ) )
                anonymousAuthenticationSupported = true;
        }
        
        
    }

    /**
     * @return the OperationsMetadata
     */
    public OperationsMetadata_1_0 getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * @return the ServiceIdentification
     */
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    /**
     * @return the ServiceProvider
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * @return Returns the SupportedAuthenticationMethods.
     */
    public ArrayList<SupportedAuthenticationMethod> getAuthenticationMethods() {
        return authenticationMethods;
    }
    
    /**
     * @param authMethod the method to check for.
     * @return true if the method is supported
     */
    public boolean isAuthenticationMethodSupported( String authMethod ){
        for ( SupportedAuthenticationMethod method : authenticationMethods ){
            if( method.getMethod().getAuthenticationMethod().equals( authMethod ) )
                return true;
        }
        return false;
    }

    /**
     * @return Returns true if anonymousAuthentication is Supported.
     */
    public boolean isAnonymousAuthenticationSupported() {
        return anonymousAuthenticationSupported;
    }

    /**
     * @return Returns true if passwordAuthentication is Supported.
     */
    public boolean isPasswordAuthenticationSupported() {
        return passwordAuthenticationSupported;
    }

    /**
     * @return Returns true if sessionAuthentication is Supported.
     */
    public boolean isSessionAuthenticationSupported() {
        return sessionAuthenticationSupported;
    }

    /**
     * @return Returns true if wasAuthentication (SAML) is Supported.
     */
    public boolean isWasAuthenticationSupported() {
        return wasAuthenticationSupported;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: OWSCapabilitiesBaseType_1_0.java,v $
 * Changes to this class. What the people have been up to: Revision 1.4  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/05/30 11:44:51  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed some warnings.
 * Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.2 2006/05/30 08:44:48 bezema Changes to this class. What the
 * people have been up to: Reararranging the layout (again) to use features of OOP. The
 * owscommonDocument is the real baseclass now. Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Changes to this class. What the people have been up to: Refactored the security and
 * authentication webservices into one package WASS (Web Authentication -and- Security Services),
 * also created a common package and a saml package which could be updated to work in the future.
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.3 2006/05/26 14:38:32 schmitz Changes to this class. What the people
 * have been up to: Added some KVP constructors to WAS operations. Changes to this class. What the
 * people have been up to: Added some comments, updated the plan. Changes to this class. What the
 * people have been up to: Restructured WAS operations by adding an AbstractRequest base class.
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.2 2006/05/19 15:35:35 schmitz Changes to this class. What the people
 * have been up to: Updated the documentation, added the GetCapabilities operation and implemented a
 * rough WAService outline. Fixed some warnings. Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/15 09:54:16
 * bezema Changes to this class. What the people have been up to: New approach to the nrw:gdi specs.
 * Including ows_1_0 spec and saml spec Changes to this class. What the people have been up to:
 * 
 **************************************************************************************************/
