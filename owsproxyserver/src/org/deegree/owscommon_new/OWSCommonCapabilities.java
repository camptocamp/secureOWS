//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/OWSCommonCapabilities.java,v 1.2 2006/08/24 06:43:04 poth Exp $
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

import java.util.Map;

/**
 * <code>OWSCommonCapabilities</code> stores the basic information common to all
 * OGC capabilities documents according to the OWS common specification version 1.0.0.
 * It is designed to be used by all OGC web services. The service specific content should
 * go into a subclass of the <code>Content</code> class.
 * 
 * @see Content
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:43:04 $
 * 
 * @since 2.0
 */

public class OWSCommonCapabilities {

    private String version = null;
    
    private String updateSequence = null;
    
    private ServiceIdentification serviceIdentification = null;
    
    private ServiceProvider serviceProvider = null;
    
    private OperationsMetadata operationsMetadata = null;
    
    private Map<String, Content> contents = null;

    /**
     * Standard constructor that initializes all encapsulated data.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param contents
     */
    public OWSCommonCapabilities( String version, String updateSequence,
                                  ServiceIdentification serviceIdentification,
                                  ServiceProvider serviceProvider,
                                  OperationsMetadata operationsMetadata,
                                  Map<String, Content> contents ) {
        this.version = version;
        this.updateSequence = updateSequence;
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.contents = contents;
    }
                                  
    /**
     * @return Returns the contents.
     */
    public Map<String, Content> getContents() {
        return contents;
    }

    /**
     * @return Returns the operationsMetadata.
     */
    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * @return Returns the serviceIdentification.
     */
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    /**
     * @return Returns the serviceProvider.
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * @return Returns the updateSequence.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSCommonCapabilities.java,v $
Revision 1.2  2006/08/24 06:43:04  poth
File header corrected

Revision 1.1  2006/08/23 07:10:21  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.1  2006/08/01 11:46:07  schmitz
Added data classes for the new OWS common capabilities framework
according to the OWS 1.0.0 common specification.
Added name to service identification.



********************************************************************** */