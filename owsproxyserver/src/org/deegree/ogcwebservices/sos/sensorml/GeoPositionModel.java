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
package org.deegree.ogcwebservices.sos.sensorml;

import java.util.ArrayList;

/**
 * represents a GeoPositionModel; please read the SensorML spec
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 *  
 */

public class GeoPositionModel extends LocationModel {

	private Object[] usesParametersObjects = null;

	/**
	 * @param identifiedAs
	 * @param classifiedAs
	 * @param description
	 * @param sourceCRS
	 * @param referenceCRS
	 * @param usesParameters cannot be null
	 */
	public GeoPositionModel(String id, Identifier[] identifiedAs,
			Classifier[] classifiedAs, Discussion[] description,
			EngineeringCRS sourceCRS, CoordinateReferenceSystem referenceCRS,
			Object[] usesParametersObjects) {

		super(id, identifiedAs, classifiedAs, description, sourceCRS,
				referenceCRS);

		//use only valid objects
		ArrayList temp = new ArrayList();
		for (int i = 0; i < usesParametersObjects.length; i++) {
			if (usesParametersObjects[i] instanceof GeoLocation) {
				temp.add(usesParametersObjects[i]);
			}

			/*
			 * other possible types are Attitude, GeoPosition and Position but
			 * not yet implemented
			 */
		}
		
		this.usesParametersObjects = temp.toArray(new Object[temp.size()]);
	
	}

	public Object[] getUsesParametersObjects() {
		return usesParametersObjects;
	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoPositionModel.java,v $
Revision 1.6  2006/08/24 06:42:16  poth
File header corrected

Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
