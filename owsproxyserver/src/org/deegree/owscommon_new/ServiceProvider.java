//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/ServiceProvider.java,v 1.2 2006/08/24 06:43:04 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
package org.deegree.owscommon_new;

import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * <code>ServiceProvider</code> stores metadata contained within a
 * ServiceProvider element according to the OWS common specification
 * version 1.0.0.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:43:04 $
 * 
 * @since 2.0
 */

public class ServiceProvider {

    private String providerName = null;
    
    private OnlineResource providerSite = null;
    
    private CitedResponsibleParty serviceContact = null;

    /**
     * Standard constructor that initializes all encapsulated data.
     * 
     * @param providerName
     * @param providerSite
     * @param serviceContact
     */
    public ServiceProvider( String providerName, OnlineResource providerSite,
                            CitedResponsibleParty serviceContact ) {
        this.providerName = providerName;
        this.providerSite = providerSite;
        this.serviceContact = serviceContact;
    }
    
    /**
     * @return Returns the providerName.
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * @return Returns the providerSite.
     */
    public OnlineResource getProviderSite() {
        return providerSite;
    }

    /**
     * @return Returns the serviceContact.
     */
    public CitedResponsibleParty getServiceContact() {
        return serviceContact;
    }
    
    
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServiceProvider.java,v $
Revision 1.2  2006/08/24 06:43:04  poth
File header corrected

Revision 1.1  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.1  2006/08/01 11:46:07  schmitz
Added data classes for the new OWS common capabilities framework
according to the OWS 1.0.0 common specification.
Added name to service identification.



********************************************************************** */