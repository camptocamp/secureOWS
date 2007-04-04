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
 * represents a Identifier; please read the SensorML spec
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 *  
 */

public class Identifier {

	public static final int shortName = 1;

	public static final int longName = 2;

	public static final int serialNumber = 3;

	public static final int modelNumber = 4;

	public static final int missionNumber = 5;

	public static final int partNumber = 6;

	private String identifierValue = null;

	private int identifierType = 0;

	private String identifierCodeSpace = null;

	public Identifier(String identifierValue, int identifierType,
			String identifierCodeSpace) {

		this.identifierValue = identifierValue;

		if ((identifierType == 6) || (identifierType == 1)
				|| (identifierType == 2) || (identifierType == 3)
				|| (identifierType == 4) || (identifierType == 5)) {
			this.identifierType = identifierType;
		}

		this.identifierCodeSpace = identifierCodeSpace;
	}

	public Identifier(String identifierValue) {
		this.identifierValue = identifierValue;
	}

	public String getIdentifierCodeSpace() {
		return identifierCodeSpace;
	}

	public int getIdentifierType() {
		return identifierType;
	}

	public String getIdentifierValue() {
		return identifierValue;
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Identifier.java,v $
Revision 1.5  2006/08/24 06:42:16  poth
File header corrected

Revision 1.4  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
