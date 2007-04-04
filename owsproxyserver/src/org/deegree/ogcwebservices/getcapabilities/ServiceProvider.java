// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/getcapabilities/ServiceProvider.java,v 1.9 2006/07/12 14:46:16 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
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
 Aennchenstr. 19
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
package org.deegree.ogcwebservices.getcapabilities;

import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.TypeCode;

/**
 * Represents the ServiceProvider section of the capabilities of an OGC
 * compliant web service according to the OGC Common Implementation
 * Specification 0.3.
 * 
 * This section corresponds to and expands the SV_ServiceProvider class in ISO
 * 19119.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $
 * 
 * @since 2.0
 */

public class ServiceProvider {

    private String providerName;
    private SimpleLink providerSite;
    private String individualName;
    private String positionName;
    private ContactInfo contactInfo;

    private TypeCode role;

    /**
     * Constructs a new ServiceProvider object.
     * 
     * @param providerName
     * @param providerSite
     * @param individualName
     * @param positionName
     * @param contactInfo
     * @param role
     */
    public ServiceProvider(String providerName, SimpleLink providerSite,
            String individualName, String positionName,
            ContactInfo contactInfo, TypeCode role) {
        this.providerName = providerName;
        this.providerSite = providerSite;
        this.individualName = individualName;
        this.positionName = positionName;
        this.contactInfo = contactInfo;
        this.role = role;
    }

    /**
     * @return Returns the contactInfo.
     * 
     */
    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    /**
     * @param contactInfo
     *            The contactInfo to set.
     * 
     */
    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * @return Returns the individualName.
     * 
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * @param individualName
     *            The individualName to set.
     * 
     */
    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }

    /**
     * @return Returns the positionName.
     * 
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * @param positionName
     *            The positionName to set.
     * 
     */
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    /**
     * @return Returns the providerName.
     * 
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * @param providerName
     *            The providerName to set.
     * 
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * @return Returns the providerSite.
     * 
     */
    public SimpleLink getProviderSite() {
        return providerSite;
    }

    /**
     * @param providerSite
     *            The providerSite to set.
     * 
     */
    public void setProviderSite(SimpleLink providerSite) {
        this.providerSite = providerSite;
    }
    /**
     * @return Returns the role.
     */
    public TypeCode getRole() {
        return role;
    }
    /**
     * @param role The role to set.
     */
    public void setRole(TypeCode role) {
        this.role = role;
    }
}
/*******************************************************************************
 * $Log: ServiceProvider.java,v $
 * Revision 1.9  2006/07/12 14:46:16  poth
 * comment footer added
 *
 * Revision 1.8  2006/04/06 20:25:25  poth
 * *** empty log message ***
 *
 * Revision 1.7  2006/04/04 20:39:42  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/03/30 21:20:25  poth
 * *** empty log message ***
 *
 * Revision 1.5  2005/06/10 07:07:39  poth
 * no message
 *
 * Revision 1.4  2005/02/23 18:05:27  mschneider
 * *** empty log message ***
 * Revision 1.3 2005/02/23 13:48:59 mschneider
 * *** empty log message *** Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.4 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.3 2004/07/07 14:20:20 mschneider More work on the
 * CatalogConfiguration and capabilities framework. For the
 * CatalogConfiguration, the following sections should now be fully convertible
 * in both directions (XML -> Java), (Java -> XML): deegreeParams,
 * ServiceIdentification, ServiceProvider.
 * 
 * Revision 1.2 2004/07/06 16:44:25 mschneider More work on CatalogConfiguration
 * and CatalogConfigurationDocument. This includes the hierarchy of these
 * classes.
 *  
 ******************************************************************************//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServiceProvider.java,v $
Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
