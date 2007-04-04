//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/configuration/WMPSConfiguration.java,v 1.17 2006/08/24 06:42:17 poth Exp $
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
package org.deegree.ogcwebservices.wmps.configuration;

import java.net.URL;

import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wmps.capabilities.WMPSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.UserDefinedSymbolization;

/**
 * Represents the configuration for a deegree WMPS 1.0 instance.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a> *
 * 
 * @version 2.0
 * 
 */
public class WMPSConfiguration extends WMPSCapabilities {

    private static final long serialVersionUID = -7940857863171829848L;

    private URL baseURL;

    private WMPSDeegreeParams deegreeParams;

    /**
     * Generates a new <code>WFSConfiguration</code> instance from the given parameters.
     * 
     * @param version
     * 
     * @param serviceIdentification
     * @param serviceProvider
     * @param userDefinedSymbolization
     * @param metadata
     * @param layer
     * @param deegreeParams
     * @param baseURL
     */
    public WMPSConfiguration( String version, ServiceIdentification serviceIdentification,
                             ServiceProvider serviceProvider,
                             UserDefinedSymbolization userDefinedSymbolization,
                             OperationsMetadata metadata, Layer layer,
                             WMPSDeegreeParams deegreeParams, URL baseURL ) {

        super( version, serviceIdentification, serviceProvider, userDefinedSymbolization, metadata,
               layer );

        this.deegreeParams = deegreeParams;
        this.baseURL = baseURL;
    }

    /**
     * @return Returns the deegreeParams.
     */
    public WMPSDeegreeParams getDeegreeParams() {
        return this.deegreeParams;
    }

    /**
     * @param deegreeParams
     *            The deegreeParams to set.
     */
    public void setDeegreeParams( WMPSDeegreeParams deegreeParams ) {
        this.deegreeParams = deegreeParams;
    }

    /**
     * @return Gets the base URL which is used to resolve file resource (XSL sheets).
     */
    public URL getBaseURL() {
        return this.baseURL;
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WMPSConfiguration.java,v $
 * Changes to this class. What the people have been up to: Revision 1.17  2006/08/24 06:42:17  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.16  2006/08/10 07:11:35  deshmukh
 * Changes to this class. What the people have been up to: WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.15  2006/08/01 13:41:47  deshmukh
 * Changes to this class. What the people have been up to: The wmps configuration has been modified and extended. Also fixed the javadoc.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.14  2006/07/31 11:21:07  deshmukh
 * Changes to this class. What the people have been up to: wmps implemention...
 * Changes to this class. What the people have been up to: Revision
 * 1.13 2006/07/12 14:46:18 poth comment footer added
 * 
 **************************************************************************************************/
