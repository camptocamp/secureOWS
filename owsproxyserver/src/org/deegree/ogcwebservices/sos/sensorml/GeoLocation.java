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

/**
 * represents a GeoLocation; please read the SensorML spec
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 *  
 */

public class GeoLocation {

	private String id = null;

	private Quantity latitude = null;

	private Quantity longitude = null;

	private Quantity altitude = null;

	private Quantity trueHeading = null;

	private Quantity speed = null;

	public GeoLocation(String id, Quantity latitude, Quantity longitude,
			Quantity altitude, Quantity trueHeading, Quantity speed) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.trueHeading = trueHeading;
		this.speed = speed;
	}

	public Quantity getAltitude() {
		return altitude;
	}

	public String getId() {
		return id;
	}

	public Quantity getLatitude() {
		return latitude;
	}

	public Quantity getLongitude() {
		return longitude;
	}

	public Quantity getSpeed() {
		return speed;
	}

	public Quantity getTrueHeading() {
		return trueHeading;
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoLocation.java,v $
Revision 1.5  2006/08/24 06:42:16  poth
File header corrected

Revision 1.4  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
