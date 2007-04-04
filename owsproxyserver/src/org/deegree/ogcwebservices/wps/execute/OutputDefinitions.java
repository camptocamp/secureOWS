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

import java.util.ArrayList;
import java.util.List;

/**
 * OutputDefinitionsType.java
 * 
 * Created on 09.03.2006. 22:34:58h
 * 
 * List of definitions of the outputs (or parameters) requested from the
 * process. These outputs are not normally identified, unless the client is
 * specifically requesting a limited subset of outputs, and/or is requesting
 * output formats and/or schemas and/or encodings different from the defaults
 * and selected from the alternatives identified in the process description, or
 * wishes to customize the descriptive information about the output.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class OutputDefinitions {

	private List<OutputDefinition> outputs;

	/**
	 * @return Returns the outputs.
	 */
	public List<OutputDefinition> getOutputDefinitions() {
		if ( outputs == null ) {
			outputs = new ArrayList<OutputDefinition>();
		}
		return this.outputs;
	}

	public void setOutputDefinitions( List<OutputDefinition> outputDefinitions ) {
		this.outputs = outputDefinitions;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OutputDefinitions.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
