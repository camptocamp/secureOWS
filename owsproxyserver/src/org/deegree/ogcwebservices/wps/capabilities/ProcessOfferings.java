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

package org.deegree.ogcwebservices.wps.capabilities;

import java.util.ArrayList;
import java.util.List;

import org.deegree.ogcwebservices.wps.ProcessBrief;

/**
 * ProcessOfferings.java
 * 
 * Created on 09.03.2006. 14:11:05h
 * 
 * List of brief descriptions of the processes offered by this WPS server.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class ProcessOfferings {

	/**
	 * 
	 * @param processBriefTypesList
	 */
	public ProcessOfferings( List<ProcessBrief> processBriefTypesList ) {
		this.processBriefTypesList = processBriefTypesList;
	}

	/**
	 * Unordered list of one or more brief descriptions of all the processes
	 * offered by this WPS server.
	 */
	protected List<ProcessBrief> processBriefTypesList;

	/**
	 * Gets the value of the process property.
	 */
	public List<ProcessBrief> getProcessBriefTypesList() {
		if ( null == processBriefTypesList ) {
			processBriefTypesList = new ArrayList<ProcessBrief>();
		}
		return this.processBriefTypesList;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ProcessOfferings.java,v $
Revision 1.4  2006/11/27 09:07:51  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
