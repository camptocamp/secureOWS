//$Header$
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wpvs.capabilities;

/**
 * TODO this class is an extended copy of org.deegree.ogcwebservices.wms.capabilities.Dimension.
 * the wms version should be moved up and this one should be a specialisation.
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class Dimension {

    private String name;
    
	private String units;
	
	private String unitSymbol;
	
	private String default_;
	
	private Boolean multipleValues;
	
	private Boolean nearestValue;
	
	private Boolean current;
	
	private String value;

	/**
     * Creates a new dimension object from the given parameters.
     * 
     * @param name
     * @param units
     * @param unitSymbol
     * @param default_
     * @param multipleValues
     * @param nearestValue
     * @param current
     * @param value
     */
    public Dimension( String name, String units, String unitSymbol, String default_, 
    				  Boolean multipleValues, Boolean nearestValue, Boolean current, String value ) {
		
    	this.name = name;
		this.units = units;
		this.unitSymbol = unitSymbol;
		this.default_ = default_;
		this.multipleValues = multipleValues;
		this.nearestValue = nearestValue;
		this.current = current;
		this.value = value;    
		
    }

	/**
	 * @return Returns the current.
	 */
	public Boolean getCurrent() {
		return current;
	}

	/**
	 * @return Returns the default_.
	 */
	public String getDefault() {
		return default_;
	}

	/**
	 * @return Returns the multipleValues.
	 */
	public Boolean getMultipleValues() {
		return multipleValues;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the nearestValue.
	 */
	public Boolean getNearestValue() {
		return nearestValue;
	}

	/**
	 * @return Returns the units.
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @return Returns the unitSymbol.
	 */
	public String getUnitSymbol() {
		return unitSymbol;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/08/24 06:42:15  poth
File header corrected

Revision 1.5  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.3  2005/12/05 09:36:38  mays
revision of comments

Revision 1.2  2005/12/01 10:30:14  mays
add standard footer to all java classes in wpvs package

******************************************************************** */
