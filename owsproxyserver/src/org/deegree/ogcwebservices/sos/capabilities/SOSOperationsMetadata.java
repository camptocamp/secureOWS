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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;

/**
 * Describe the OWS OperationsMetadata, is a part of the SCS Capabilities
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe</a>
 * 
 * @version 1.0
 */
public class SOSOperationsMetadata extends OperationsMetadata {

	public static final String DESCRIBE_PLATFORM_NAME = "DescribePlatform";
	public static final String DESCRIBE_SENSOR_NAME = "DescribeSensor";
	public static final String GET_OBSERVATION_NAME = "GetObservation";

	private Operation describePlatform;
	private Operation describeSensor;
	private Operation getObservation;

	private static final ILogger LOG = LoggerFactory.getLogger(SOSOperationsMetadata.class);

	/**
	 * constructor
	 * 
	 * @param getCapabilities
	 * @param describePlatform
	 * @param describeSensor
	 * @param getObservation
	 */
	public SOSOperationsMetadata(Operation getCapabilities,
			Operation describePlatform, Operation describeSensor,
			Operation getObservation) {

		super( getCapabilities, null, null );

		this.describePlatform = describePlatform;
		this.describeSensor = describeSensor;
		this.getObservation = getObservation;

	}

	/**
	 *  
	 */
	public Operation[] getOperations() {
		return new Operation[] { getCapabilitiesOperation, describePlatform,
				describeSensor, getObservation };
	}

	/**
	 * 
	 * @return
	 */
	public Operation getDescribePlatform() {
		return describePlatform;
	}

	/**
	 * 
	 * @param describePlatform
	 */
	public void setDescribePlatform(Operation describePlatform) {
		this.describePlatform = describePlatform;
	}

	/**
	 * 
	 * @return
	 */
	public Operation getDescribeSensor() {
		return describeSensor;
	}

	/**
	 * 
	 * @param describeSensor
	 */
	public void setDescribeSensor(Operation describeSensor) {
		this.describeSensor = describeSensor;
	}

	/**
	 * 
	 * @return
	 */
	public Operation getGetObservation() {
		return getObservation;
	}

	/**
	 * 
	 * @param getObservation
	 */
	public void setGetObservation(Operation getObservation) {
		this.getObservation = getObservation;
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSOperationsMetadata.java,v $
Revision 1.5  2006/08/24 06:42:17  poth
File header corrected

Revision 1.4  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
