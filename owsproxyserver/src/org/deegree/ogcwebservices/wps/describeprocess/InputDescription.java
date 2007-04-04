/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
package org.deegree.ogcwebservices.wps.describeprocess;

import org.deegree.datatypes.Code;
import org.deegree.ogcwebservices.wps.WPSDescription;

/**
 * InputDescription.java
 * 
 * Created on 09.03.2006. 22:33:58h
 * 
 * Description of an input to a process.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class InputDescription extends WPSDescription {

	/**
	 * Indicates that this input shall be a complex data structure (such as a
	 * GML document), and provides a list of formats and encodings supported for
	 * this Input. The value of this ComplexData structure can be input either
	 * embedded in the Execute request or remotely accessible to the server.
	 * This element also provides a list of formats, encodings, and schemas
	 * supported for this output. The client can select from among the
	 * identified combinations of formats, encodings, and schemas to specify the
	 * form of the output. This allows for complete specification of particular
	 * versions of GML, or image formats.
	 */
	protected ComplexData complexData;

	/**
	 * Indicates that this input shall be a simple numeric value or character
	 * string that is embedded in the execute request, and describes the
	 * possible values.
	 */
	protected LiteralInput literalData;

	/**
	 * Indicates that this input shall be a BoundingBox data structure that is
	 * embedded in the execute request, and provides a list of the CRSs
	 * supported for this Bounding Box.
	 */
	protected SupportedCRSs boundingBoxData;

	/**
	 * The minimum number of times that values for this parameter are required.
	 * If MinimumOccurs is "0", this data input is optional. If MinimumOccurs is
	 * "1" or if this element is omitted, this process input is required.
	 */
	protected int minimumOccurs;

	/**
	 * 
	 * @param identifier
	 * @param title
	 * @param _abstract
	 * @param boundingBoxData
	 * @param complexData
	 * @param literalData
	 * @param occurs
	 */
	public InputDescription( Code identifier, String title, String _abstract,
			SupportedCRSs boundingBoxData, ComplexData complexData, LiteralInput literalData,
			int occurs ) {
		super( identifier, title, _abstract );
		this.boundingBoxData = boundingBoxData;
		this.complexData = complexData;
		this.literalData = literalData;
		minimumOccurs = occurs;
	}

	/**
	 * @return Returns the complexData.
	 */
	public ComplexData getComplexData() {
		return complexData;
	}

	/**
	 * @param complexData
	 *            The complexData to set.
	 */
	public void setComplexData( ComplexData value ) {
		this.complexData = value;
	}

	/**
	 * @return Returns the literalData.
	 */
	public LiteralInput getLiteralData() {
		return literalData;
	}

	/**
	 * @param literalData
	 *            The literalData to set.
	 */
	public void setLiteralData( LiteralInput value ) {
		this.literalData = value;
	}

	/**
	 * @return Returns the boundingBoxData.
	 */
	public SupportedCRSs getBoundingBoxData() {
		return boundingBoxData;
	}

	/**
	 * @param boundingBoxData
	 *            The boundingBoxData to set.
	 */
	public void setBoundingBoxData( SupportedCRSs value ) {
		this.boundingBoxData = value;
	}

	/**
	 * @return Returns the minimumOccurs.
	 */
	public int getMinimumOccurs() {
		return minimumOccurs;
	}

	/**
	 * @param minimumOccurs
	 *            The minimumOccurs to set.
	 */
	public void setMinimumOccurs( int value ) {
		this.minimumOccurs = value;
	}

	public boolean isBoundingBoxData() {
		boolean isBoundingBoxData = false;
		if ( null != boundingBoxData ) {
			isBoundingBoxData = true;
		}
		return isBoundingBoxData;
	}

	public boolean isComplexData() {
		boolean isComplexData = false;
		if ( null != complexData ) {
			isComplexData = true;
		}
		return isComplexData;
	}

	public boolean isLiteralData() {
		boolean isLiteralData = false;
		if ( null != literalData ) {
			isLiteralData = true;
		}
		return isLiteralData;
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InputDescription.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
