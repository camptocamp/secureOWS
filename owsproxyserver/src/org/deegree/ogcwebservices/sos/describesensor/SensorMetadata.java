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
package org.deegree.ogcwebservices.sos.describesensor;

import org.deegree.ogcwebservices.sos.ComponentMetadata;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.ComponentDescription;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.deegree.ogcwebservices.sos.sensorml.Product;

/**
 * represents the metadata of a single sensor
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 *  
 */

public class SensorMetadata extends ComponentMetadata {
	
	private Product[] measures = null;

	/**
	 * constructor
	 * 
	 * @param identifiedAs
	 * @param classifiedAs
	 * @param hasCRS
	 * @param locatedUsing
	 * @param describedBy
	 * @param attachedTo
	 * @param measures
	 */
	public SensorMetadata(Identifier[] identifiedAs, Classifier[] classifiedAs,
			EngineeringCRS hasCRS, LocationModel[] locatedUsing,
			ComponentDescription describedBy, String attachedTo,
			Product[] measures) {
		
		super(identifiedAs, classifiedAs, hasCRS, locatedUsing, describedBy,
				attachedTo);
		
		this.measures = measures;

	}

	/**
	 * 
	 * @return
	 */
	public Product[] getMeasures() {
		return measures;
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SensorMetadata.java,v $
Revision 1.6  2006/10/18 17:00:56  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.5  2006/08/24 06:42:17  poth
File header corrected

Revision 1.4  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
