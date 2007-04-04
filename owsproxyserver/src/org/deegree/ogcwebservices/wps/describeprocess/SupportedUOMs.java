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

import org.deegree.owscommon.OWSMetadata;

/**
 * SupportedUOMs.java
 * 
 * Created on 09.03.2006. 22:55:34h
 * 
 * List of supported units of measure for a process input or output.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class SupportedUOMs {

	/**
	 * Unordered list of references to the Units of Measure supported for this
	 * input or output. This element shall not be included if there is only one
	 * (i.e., the default) UOM supported.
	 */
	protected List<OWSMetadata> uom;

	/**
	 * eference to the default UOM supported for this input or output, if any.
	 * The process shall expect input in or produce output in this UOM unless
	 * the Execute request specifies another supported UOM.
	 */
	protected OWSMetadata defaultUOM;

	/**
	 * @param defaultuom
	 * @param uom
	 */
	public SupportedUOMs( OWSMetadata defaultuom, List<OWSMetadata> uom ) {
		defaultUOM = defaultuom;
		this.uom = uom;
	}

	/**
	 * 
	 * @return
	 */
	public List<OWSMetadata> getUOM() {
		if ( uom == null ) {
			uom = new ArrayList<OWSMetadata>();
		}
		return this.uom;
	}

	/**
	 * Gets the value of the defaultUOM property.
	 * 
	 * @return possible object is {@link String  }
	 */
	public OWSMetadata getDefaultUOM() {
		return defaultUOM;
	}

	/**
	 * Sets the value of the defaultUOM property.
	 * 
	 * @param value
	 *            allowed object is {@link String  }
	 */
	public void setDefaultUOM( OWSMetadata defaultUOM ) {
		this.defaultUOM = defaultUOM;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SupportedUOMs.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
