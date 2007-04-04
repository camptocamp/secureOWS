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
 * OutputDescription.java
 * 
 * Created on 09.03.2006. 22:37:03h
 * 
 * Description of a process Output.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class OutputDescription extends WPSDescription {

	/**
	 * Indicates that this Output shall be a complex data structure (such as a
	 * GML fragment) that is returned by the execute operation response. The
	 * value of this complex data structure can be output either embedded in the
	 * execute operation response or remotely accessible to the client. When
	 * this output form is indicated, the process produces only a single output,
	 * and "store" is "false, the output shall be returned directly, without
	 * being embedded in the XML document that is otherwise provided by execute
	 * operation response. This element also provides a list of format,
	 * encoding, and schema combinations supported for this output. The client
	 * can select from among the identified combinations of formats, encodings,
	 * and schemas to specify the form of the output. This allows for complete
	 * specification of particular versions of GML, or image formats.
	 */
	protected ComplexData complexOutput;

	/**
	 * Indicates that this output shall be a simple literal value (such as an
	 * integer) that is embedded in the execute response, and describes that
	 * output.
	 */
	protected LiteralOutput literalOutput;

	/**
	 * Indicates that this output shall be a BoundingBox data structure, and
	 * provides a list of the CRSs supported in these Bounding Boxes. This
	 * element shall be included when this process output is an ows:BoundingBox
	 * element.
	 */
	protected SupportedCRSs boundingBoxOutput;

	/**
	 * 
	 * @param identifier
	 * @param title
	 * @param _abstract
	 * @param boundingBoxOutput
	 * @param complexOutput
	 * @param literalOutput
	 */
	public OutputDescription( Code identifier, String title, String _abstract,
			SupportedCRSs boundingBoxOutput, ComplexData complexOutput, LiteralOutput literalOutput ) {
		super( identifier, title, _abstract );
		this.boundingBoxOutput = boundingBoxOutput;
		this.complexOutput = complexOutput;
		this.literalOutput = literalOutput;
	}

	/**
	 * @return Returns the complexOutput.
	 */
	public ComplexData getComplexOutput() {
		return complexOutput;
	}

	/**
	 * @param complexOutput
	 *            The complexOutput to set.
	 */
	public void setComplexOutput( ComplexData value ) {
		this.complexOutput = value;
	}

	/**
	 * Gets the value of the literalOutput property.
	 */
	public LiteralOutput getLiteralOutput() {
		return literalOutput;
	}

	/**
	 * Sets the value of the literalOutput property.
	 * 
	 * @param value
	 */
	public void setLiteralOutput( LiteralOutput value ) {
		this.literalOutput = value;
	}

	/**
	 * Gets the value of the boundingBoxOutput property.
	 * 
	 * @return possible object is {@link SupportedCRSs  }
	 */
	public SupportedCRSs getBoundingBoxOutput() {
		return boundingBoxOutput;
	}

	/**
	 * @param boundingBoxOutput
	 *            The boundingBoxOutput to set.
	 */
	public void setBoundingBoxOutput( SupportedCRSs value ) {
		this.boundingBoxOutput = value;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OutputDescription.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
