//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/WMSConfiguration.java,v 1.13 2006/09/08 08:42:01 schmitz Exp $
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
 Aennchenstraße 19
 53177 Bonn
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wms.configuration;

import java.net.URL;

import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.UserDefinedSymbolization;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;
import org.deegree.owscommon_new.ServiceProvider;

/**
 * Represents the configuration for a deegree WFS 1.1.1 instance (or earlier). Implements the
 * WMSConfigurationType interface required by all configurations.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.13 $, $Date: 2006/09/08 08:42:01 $
 * 
 * @since 2.0
 */
public class WMSConfiguration extends WMSCapabilities implements WMSConfigurationType {

    private static final long serialVersionUID = -9123110374834641390L;

    private URL baseURL;

    private WMSDeegreeParams deegreeParams;

    /**
     * Generates a new <code>WFSConfiguration</code> instance from the given parameters.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param userDefinedSymbolization 
     * @param metadata 
     * @param layer 
     * @param deegreeParams
     * @param baseURL
     */
    public WMSConfiguration( String version, String updateSequence,
                            ServiceIdentification serviceIdentification,
                            ServiceProvider serviceProvider,
                            UserDefinedSymbolization userDefinedSymbolization,
                            OperationsMetadata metadata, Layer layer,
                            WMSDeegreeParams deegreeParams, URL baseURL ) {
        super( version, updateSequence, serviceIdentification, serviceProvider,
            userDefinedSymbolization, metadata, layer );
        this.deegreeParams = deegreeParams;
        this.baseURL = baseURL;
    }

    /**
     * @return Returns the deegreeParams.
     */
    public WMSDeegreeParams getDeegreeParams() {
        return deegreeParams;
    }

    /**
     * @param deegreeParams
     *            The deegreeParams to set.
     */
    public void setDeegreeParams( WMSDeegreeParams deegreeParams ) {
        this.deegreeParams = deegreeParams;
    }

    /**
     * @return Gets the base URL which is used to resolve file resource (XSL sheets).
     */
    public URL getBaseURL() {
        return this.baseURL;
    }

    /* (non-Javadoc)
     * @see org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType#calculateVersion(java.lang.String)
     */
    public String calculateVersion( String version ) {
        return "1.1.1";
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSConfiguration.java,v $
Revision 1.13  2006/09/08 08:42:01  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.12  2006/08/24 06:42:17  poth
File header corrected

Revision 1.11  2006/08/23 07:10:21  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.10  2006/08/22 10:25:01  schmitz
Updated the WMS to use the new OWS common package.
Updated the rest of deegree to use the new data classes returned
by the updated WMS methods/capabilities.

Revision 1.9  2006/07/28 08:01:27  schmitz
Updated the WMS for 1.1.1 compliance.
Fixed some documentation.

Revision 1.8  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
