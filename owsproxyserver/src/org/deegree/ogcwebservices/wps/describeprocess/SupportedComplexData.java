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

/**
 * SupportedComplexData.java
 * 
 * Created on 09.03.2006. 22:27:42h
 * 
 * A combination of format, encoding, and/or schema supported by a process input
 * or output.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class SupportedComplexData {

	/**
	 * @param encoding
	 * @param format
	 * @param schema
	 */
	public SupportedComplexData( String encoding, String format, String schema ) {
		this.encoding = encoding;
		this.format = format;
		this.schema = schema;
	}

	/**
	 * Format supported for this input or output (e.g., text/XML). This element
	 * shall be included when the format for this ComplexDataType differs from
	 * the defaultFormat for this Input/Output. This element shall not be
	 * included if there is only one (i.e., the default) format supported for
	 * this Input/Output, or Format does not apply to this Input/Output.
	 */
	protected String format;

	/**
	 * Reference to an encoding supported for this input or output (e.g.,
	 * UTF-8). This element shall be included when the encoding for this
	 * ComplexDataType differs from the defaultEncoding for this Input/Output.
	 * This element shall not be included if there is only one (i.e., the
	 * default) encoding supported for this Input/Output, or Encoding does not
	 * apply to this Input/Output.
	 * 
	 */
	protected String encoding;

	/**
	 * Reference to a definition of XML elements or types supported for this
	 * Input or Output (e.g., GML 2.1 Application Schema). Each of these XML
	 * elements or types shall be defined in a separate XML Schema Document.
	 * This element shall be included when the schema for this ComplexDataType
	 * differs from the defaultSchema for this Input/Output. This element shall
	 * not be included if there is only one (i.e., the default) XML Schema
	 * Document supported for this Input/Output, or Schema does not apply to
	 * this Input/Output.
	 * 
	 */
	protected String schema;

	/**
	 * @return Returns the format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            The format to set.
	 */
	public void setFormat( String value ) {
		this.format = value;
	}

	/**
	 * @return Returns the encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            The encoding to set.
	 */
	public void setEncoding( String value ) {
		this.encoding = value;
	}

	/**
	 * @return Returns the schema.
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *            The schema to set.
	 */
	public void setSchema( String value ) {
		this.schema = value;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SupportedComplexData.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
