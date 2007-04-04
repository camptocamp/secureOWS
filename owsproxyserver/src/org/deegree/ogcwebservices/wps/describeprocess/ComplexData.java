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

import java.util.ArrayList;
import java.util.List;

/**
 * ComplexData.java
 * 
 * Created on 09.03.2006. 22:40:34h
 * 
 * Indicates that this input shall be a complex data structure (such as a GML
 * document), and provides a list of formats and encodings supported for this
 * Input. The value of this ComplexData structure can be input either embedded
 * in the Execute request or remotely accessible to the server. This element
 * also provides a list of formats, encodings, and schemas supported for this
 * output. The client can select from among the identified combinations of
 * formats, encodings, and schemas to specify the form of the output. This
 * allows for complete specification of particular versions of GML, or image
 * formats.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class ComplexData {

	protected List<SupportedComplexData> supportedComplexData;

	/**
	 * Reference to the default encoding supported for this input or output. The
	 * process will expect input using or produce output using this encoding
	 * unless the Execute request specifies another supported encoding. This
	 * parameter shall be included when the default Encoding is other than the
	 * encoding of the XML response document (e.g. UTF-8). This parameter shall
	 * be omitted when there is no Encoding required for this input/output.
	 */
	protected String defaultEncoding;

	/**
	 * Identifier of the default Format supported for this input or output. The
	 * process shall expect input in or produce output in this Format unless the
	 * Execute request specifies another supported Format. This parameter shall
	 * be included when the default Format is other than text/XML. This
	 * parameter is optional if the Format is text/XML.
	 */
	protected String defaultFormat;

	/**
	 * Reference to the definition of the default XML element or type supported
	 * for this input or output. This XML element or type shall be defined in a
	 * separate XML Schema Document. The process shall expect input in or
	 * produce output conformant with this XML element or type unless the
	 * Execute request specifies another supported XML element or type. This
	 * parameter shall be omitted when there is no XML Schema associated with
	 * this input/output (e.g., a GIF file). This parameter shall be included
	 * when this input/output is XML encoded using an XML schema. When included,
	 * the input/output shall validate against the referenced XML Schema. Note:
	 * If the input/output uses a profile of a larger schema, the server
	 * administrator should provide that schema profile for validation purposes.
	 */
	protected String defaultSchema;

	/**
	 * @param encoding
	 * @param format
	 * @param schema
	 * @param data
	 */
	public ComplexData( String defaultEncoding, String defaultFormat, String defaultSchema,
			List<SupportedComplexData> supportedComplexData ) {
		this.defaultEncoding = defaultEncoding;
		this.defaultFormat = defaultFormat;
		this.defaultSchema = defaultSchema;
		this.supportedComplexData = supportedComplexData;
	}

	/**
	 * @return Returns the supportedComplexData.
	 */
	public List<SupportedComplexData> getSupportedComplexData() {
		if ( supportedComplexData == null ) {
			supportedComplexData = new ArrayList<SupportedComplexData>();
		}
		return this.supportedComplexData;
	}

	/**
	 * @return Returns the defaultEncoding.
	 */
	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	/**
	 * @param defaultEncoding
	 *            The defaultEncoding to set.
	 */
	public void setDefaultEncoding( String value ) {
		this.defaultEncoding = value;
	}

	/**
	 * @return Returns the defaultFormat.
	 */
	public String getDefaultFormat() {
		return defaultFormat;
	}

	/**
	 * @param defaultFormat
	 *            The defaultFormat to set.
	 */
	public void setDefaultFormat( String value ) {
		this.defaultFormat = value;
	}

	/**
	 * @return Returns the defaultSchema.
	 */
	public String getDefaultSchema() {
		return defaultSchema;
	}

	/**
	 * @param defaultSchema
	 *            The defaultSchema to set.
	 */
	public void setDefaultSchema( String value ) {
		this.defaultSchema = value;
	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ComplexData.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
