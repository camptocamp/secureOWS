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
package org.deegree.ogcwebservices.wps.execute;

import java.net.URI;
import java.net.URL;

/**
 * ComplexValueEncodign.java
 * 
 * Created on 24.03.2006. 17:29:57h
 * 
 * References the XML schema, format, and encoding of a complex value.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */
public class ComplexValueEncoding {

	/**
	 * The encoding of this input or requested for this output (e.g., UTF-8).
	 * This "encoding" shall be included whenever the encoding required is not
	 * the default encoding indicated in the Process full description. When
	 * included, this encoding shall be one published for this output or input
	 * in the Process full description.
	 */
	protected URI encoding;

	/**
	 * The Format of this input or requested for this output (e.g., text/XML).
	 * This element shall be omitted when the Format is indicated in the http
	 * header of the output. When included, this format shall be one published
	 * for this output or input in the Process full description.
	 */
	protected String format;

	/**
	 * Web-accessible XML Schema Document that defines the content model of this
	 * complex resource (e.g., encoded using GML 2.2 Application Schema). This
	 * reference should be included for XML encoded complex resources to
	 * facilitate validation.
	 */
	protected URL schema;

	/**
	 * @param encoding
	 * @param format
	 * @param schema
	 */
	public ComplexValueEncoding( String format, URI encoding, URL schema ) {
		this.encoding = encoding;
		this.format = format;
		this.schema = schema;
	}

	/**
	 * @return Returns the encoding.
	 */
	public URI getEncoding() {
		return encoding;
	}

	/**
	 * @return Returns the format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return Returns the schema.
	 */
	public URL getSchema() {
		return schema;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ComplexValueEncoding.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
