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

import org.deegree.datatypes.Code;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wps.WPSDescription;

/**
 * IOValue.java
 * 
 * Created on 24.03.2006. 16:33:24h
 * 
 * Value of one input to a process or one output from a process.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */

public class IOValue extends WPSDescription {

	/**
	 * Identifies this input or output value as a web accessible resource, and
	 * references that resource. For an input, this element may be used by a
	 * client for any process input coded as ComplexData in the
	 * ProcessDescription. For an output, this element shall be used by a server
	 * when "store" in the Execute request is "true".
	 */

	private ComplexValueReference complexValueReference;

	/**
	 * Identifies this input or output value as a complex value data structure
	 * encoded in XML (e.g., using GML), and provides that complex value data
	 * structure. For an input, this element may be used by a client for any
	 * process input coded as ComplexData in the ProcessDescription. For an
	 * output, this element shall be used by a server when "store" in the
	 * Execute request is "false".
	 */
	private ComplexValue complexValue;

	/**
	 * Identifies this input or output value as a literal value of a simple
	 * quantity (e.g., one number), and provides that value.
	 */
	private TypedLiteral literalValue;

	/**
	 * Identifies this input or output value as an ows:BoundingBox data
	 * structure, and provides that ows:BoundingBox data structure.
	 */
	private Envelope boundingBoxValue;

	/**
	 * 
	 * @param identifier
	 * @param title
	 * @param _abstract
	 * @param boundingBoxValue
	 * @param complexValue
	 * @param complexValueReference
	 * @param literalValue
	 */
	public IOValue( Code identifier, String title, String _abstract, Envelope boundingBoxValue,
			ComplexValue complexValue, ComplexValueReference complexValueReference,
            TypedLiteral literalValue ) {
		super( identifier, title, _abstract );
		this.boundingBoxValue = boundingBoxValue;
		this.complexValue = complexValue;
		this.complexValueReference = complexValueReference;
		this.literalValue = literalValue;
	}

	/**
	 * 
	 * @return
	 */
	public ComplexValueReference getComplexValueReference() {
		return complexValueReference;
	}

	/**
	 * 
	 * @param value
	 */
	public void setComplexValueReference( ComplexValueReference value ) {
		this.complexValueReference = value;
	}

	/**
	 * 
	 * @return
	 */
	public ComplexValue getComplexValue() {
		return complexValue;
	}

	/**
	 * 
	 * @param value
	 */
	public void setComplexValue( ComplexValue value ) {
		this.complexValue = value;
	}

	/**
	 * 
	 * @return
	 */
	public TypedLiteral getLiteralValue() {
		return literalValue;
	}

	/**
	 * 
	 * @param value
	 */
	public void setLiteralValue( TypedLiteral value ) {
		this.literalValue = value;
	}

	/**
	 * 
	 * @return
	 */
	public Envelope getBoundingBoxValue() {
		return boundingBoxValue;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isBoundingBoxValueType() {
		boolean boundingBoxValueType = false;
		if ( null != boundingBoxValue ) {
			boundingBoxValueType = true;
		}
		return boundingBoxValueType;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isComplexValueReferenceType() {
		boolean complexValueReferenceType = false;
		if ( null != complexValueReference ) {
			complexValueReferenceType = true;
		}
		return complexValueReferenceType;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isComplexValueType() {
		boolean complexValueType = false;
		if ( null != complexValue ) {
			complexValueType = true;
		}
		return complexValueType;
	}

	public boolean isLiteralValueType() {
		boolean literalValueType = false;
		if ( null != literalValue ) {
			literalValueType = true;
		}
		return literalValueType;
	}

	/**
	 * 
	 * @param value
	 */
	public void setBoundingBoxValue( Envelope value ) {
		this.boundingBoxValue = value;
	}

	public static class ComplexValueReference extends ComplexValueEncoding {

		protected URL reference;

		/**
		 * @param encoding
		 * @param format
		 * @param schema
		 */
		public ComplexValueReference( String format, URI encoding, URL schema, URL reference ) {
			super( format, encoding, schema );
			this.reference = reference;
		}

		public URL getReference() {
			return reference;
		}

	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IOValue.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/05/25 14:47:44  poth
LiteralValue substituted by TypedLiteral


********************************************************************** */