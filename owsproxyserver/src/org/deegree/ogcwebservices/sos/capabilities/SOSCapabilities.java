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
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos.capabilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.owscommon.OWSCommonCapabilities;
import org.xml.sax.SAXException;

/**
 * Represents the SOS Capabilities
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class SOSCapabilities extends OWSCommonCapabilities {

    private static final ILogger LOG = LoggerFactory.getLogger(SOSCapabilities.class);
    
	private ArrayList sensorList = new ArrayList();
	private ArrayList platformList = new ArrayList();
	

	/**
	 * createCapabilities
	 * 
	 * @param url
	 *  
	 */
	public static OGCCapabilities createCapabilities(URL url)
			throws IOException, SAXException, InvalidCapabilitiesException {
		OGCCapabilities capabilities = null;
		CapabilitiesDocument capabilitiesDoc = new CapabilitiesDocument();
        capabilitiesDoc.load( url );
		capabilities = capabilitiesDoc.parseCapabilities();
		return capabilities;
	}

	/**
	 * 
	 * @param version
	 * @param updateSequence
	 * @param serviceIdentification
	 * @param serviceProvider
	 * @param operationsMetadata
	 * @param contents
	 * @param platformList
	 * @param sensorList
	 */
	public SOSCapabilities(String version, String updateSequence,
			ServiceIdentification serviceIdentification,
			ServiceProvider serviceProvider,
			OperationsMetadata operationsMetadata, Contents contents,
			ArrayList platformList, ArrayList sensorList) {

		super(version, updateSequence, serviceIdentification, serviceProvider,
				operationsMetadata, contents);

		this.platformList = platformList;

		this.sensorList = sensorList;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList getSensorList() {
		return this.sensorList;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList getPlatformList() {
		return this.platformList;
	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSCapabilities.java,v $
Revision 1.6  2006/08/24 06:42:17  poth
File header corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
