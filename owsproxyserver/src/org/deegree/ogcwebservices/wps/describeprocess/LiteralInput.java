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

import org.deegree.datatypes.values.ValueRange;
import org.deegree.owscommon.OWSMetadata;
import org.deegree.owscommon.com110.OWSAllowedValues;

/**
 * LiteralInput.java
 * 
 * Created on 09.03.2006. 22:57:28h
 * 
 * Description of a process input that consists of a simple literal value (e.g.,
 * "2.1"). (Informative: This type is a subset of the ows:UnNamedDomainType
 * defined in owsDomaintype.xsd.)
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class LiteralInput extends LiteralOutput {

	/**
	 * Indicates that there are a finite set of values and ranges allowed for
	 * this input, and contains list of all the valid values and/or ranges of
	 * values. Notice that these values and ranges can be displayed to a human
	 * client.
	 */
	protected OWSAllowedValues allowedValues;

	/**
	 * Indicates that any value is allowed for this input. This element shall be
	 * included when there are no restrictions, except for data type, on the
	 * allowable value of this input.
	 */
	protected boolean anyValueAllowed;

	/**
	 * Indicates that there are a finite set of values and ranges allowed for
	 * this input, which are specified in the referenced list.
	 */
	protected OWSMetadata valuesReference;

	/**
	 * Optional default value for this quantity, which should be included when
	 * this quantity has a default value.
	 */
	protected ValueRange defaultValue;

	/**
	 * 
	 * @param domainMetadataType
	 * @param supportedUOMsType
	 * @param allowedValues
	 * @param anyValueAllowed
	 * @param valueRange
	 * @param valuesReference
	 */
	public LiteralInput( OWSMetadata domainMetadataType, SupportedUOMs supportedUOMsType,
			OWSAllowedValues allowedValues, boolean anyValueAllowed, ValueRange defaultValue,
			OWSMetadata valuesReference ) {
		super( domainMetadataType, supportedUOMsType );
		this.allowedValues = allowedValues;
		this.anyValueAllowed = anyValueAllowed;
		this.defaultValue = defaultValue;
		this.valuesReference = valuesReference;
	}

	/**
	 * @return Returns the allowedValues.
	 */
	public OWSAllowedValues getAllowedValues() {
		return allowedValues;
	}

	/**
	 * @param allowedValues
	 *            The allowedValues to set.
	 */
	public void setAllowedValues( OWSAllowedValues value ) {
		this.allowedValues = value;
	}

	/**
	 * @return Returns the anyValue.
	 */
	public boolean getAnyValue() {
		return anyValueAllowed;
	}

	/**
	 * @param anyValue
	 *            The anyValue to set.
	 */
	public void setAnyValue( boolean anyValueAllowed ) {
		this.anyValueAllowed = anyValueAllowed;
	}

	/**
	 * @return Returns the valuesReference.
	 */
	public OWSMetadata getValuesReference() {
		return valuesReference;
	}

	/**
	 * @param valuesReference
	 *            The valuesReference to set.
	 */
	public void setValuesReference( OWSMetadata value ) {
		this.valuesReference = value;
	}

	/**
	 * @return Returns the defaultValue.
	 */
	public ValueRange getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            The defaultValue to set.
	 */
	public void setDefaultValue( ValueRange value ) {
		this.defaultValue = value;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LiteralInput.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
