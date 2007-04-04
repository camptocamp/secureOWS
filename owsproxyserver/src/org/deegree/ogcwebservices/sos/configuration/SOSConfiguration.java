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
package org.deegree.ogcwebservices.sos.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.sos.capabilities.SOSCapabilities;
import org.xml.sax.SAXException;

/**
 * represent the sos configuration
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */
public class SOSConfiguration extends SOSCapabilities {

	private SOSDeegreeParams sosDeegreeParams;

	private static final ILogger LOG = LoggerFactory.getLogger(SOSConfiguration.class);

	/**
	 * @param url
	 *  
	 */
	public static SOSConfiguration create(URL url) throws IOException,
			SAXException, InvalidCapabilitiesException,
			InvalidConfigurationException {
		SOSConfigurationDocument confDoc = new SOSConfigurationDocument();
        confDoc.load( url );
		return confDoc.getConfiguration();
	}

	/**
	 * @param sosDeegreeParams
	 * @param sensorList
	 * @param platformList
	 * @param operationsMetadata
	 * @param serviceProvider
	 * @param serviceIdentification
	 * @param updateSequence
	 * @param version
	 *  
	 */
	public SOSConfiguration(SOSDeegreeParams sosDeegreeParams,
			ArrayList sensorList, ArrayList platformList,
			OperationsMetadata operationsMetadata,
			ServiceProvider serviceProvider,
			ServiceIdentification serviceIdentification, String updateSequence,
			String version, Contents contents) {

		super(version, updateSequence, serviceIdentification, serviceProvider,
			  operationsMetadata, contents, platformList, sensorList);

		this.sosDeegreeParams = sosDeegreeParams;
	}

	/**
	 * 
	 * @return
	 */
	public SOSDeegreeParams getSOSDeegreeParams() {
		return sosDeegreeParams;
	}

	
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSConfiguration.java,v $
Revision 1.6  2006/08/24 06:42:17  poth
File header corrected

Revision 1.5  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
