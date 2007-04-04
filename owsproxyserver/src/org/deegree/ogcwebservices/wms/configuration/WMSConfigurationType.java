//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/WMSConfigurationType.java,v 1.1 2006/09/08 08:42:01 schmitz Exp $
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
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wms.configuration;

import java.net.URL;

import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;

/**
 * <code>WMSConfigurationType</code> defines the methods that each WMS configuration object has
 * to implement. It is used to unify the implementations for the different versions.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.1 $, $Date: 2006/09/08 08:42:01 $
 * 
 * @since 2.0
 */

public interface WMSConfigurationType {

    /**
     * @return Returns the deegreeParams.
     */
    public WMSDeegreeParams getDeegreeParams();

    /**
     * @param deegreeParams
     *            The deegreeParams to set.
     */
    public void setDeegreeParams( WMSDeegreeParams deegreeParams );

    /**
     * @return Gets the base URL which is used to resolve file resource (XSL sheets).
     */
    public URL getBaseURL();

    /**
     * @return the version
     */
    public String getVersion();
    
    /**
     * @return the <code>ServiceIdentification</code> object
     */
    public ServiceIdentification getServiceIdentification();
    
    /**
     * @return the updateSequence.
     */
    public String getUpdateSequence();
    
    /**
    *
    * @return the root layer provided by a WMS
    */
   public Layer getLayer();
   
   /**
    * 
    * @param name the layer name
    * @return the root layer provided by a WMS
    */
   public Layer getLayer( String name );
   
   /**
    * @return the operations metadata object
    */
   public OperationsMetadata getOperationMetadata();
   
   /**
    * 
    * @param version the input version
    * @return the nearest supported version according to the version negotiation rules.
    */
   public String calculateVersion( String version );

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSConfigurationType.java,v $
Revision 1.1  2006/09/08 08:42:01  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.



********************************************************************** */