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

package org.deegree.owscommon.com110;

import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.ValueRange;

/**
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class OWSAllowedValues {

	/** FIXME 
	 * is ValueRange realy what is wanted here?
	 * 
	 * ValueRange contains:
	 * 		TypedLiteral min
	 * 		TypedLiteral max
	 * 		URI type
	 * 		URI semantic
	 * 		boolean atomic
	 * 
	 * ows:Range needs:
	 * 		ows:ValueType min
	 * 		ows:ValueType max
	 * 		ows:ValueType spacing
	 * 
	 * this does not seem to fit!
	 * 
	 */
	private TypedLiteral[] owsValues;
	private ValueRange[] valueRanges; 
	
	/**
	 * TODO
	 * 
	 * @param owsValues
	 * @param valueRanges
	 */
	public OWSAllowedValues( TypedLiteral[] owsValues, ValueRange[] valueRanges ) {

		this.owsValues = owsValues;
		this.valueRanges = valueRanges;
		
	}

	/**
	 * @return Returns the owsValues.
	 */
	public TypedLiteral[] getOwsValues() {
		return owsValues;
	}

	/**
	 * @return Returns the valueRanges.
	 */
	public ValueRange[] getValueRanges() {
		return valueRanges;
	}

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.5  2006/08/29 19:54:14  poth
footer corrected

Revision 1.4  2006/08/24 06:42:48  poth
File header corrected

Revision 1.3  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.1  2005/12/19 10:11:25  mays
move class from package ../ogcwebservices/wpvs/operation to package ../owscommon/com110

Revision 1.1  2005/12/16 15:29:38  mays
necessary changes due to new definition of ows:OperationsMetadata

********************************************************************** */
