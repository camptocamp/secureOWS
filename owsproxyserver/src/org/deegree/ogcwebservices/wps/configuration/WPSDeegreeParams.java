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

package org.deegree.ogcwebservices.wps.configuration;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.wps.execute.RequestQueueManager;

/**
 * WPSDeegreeParams.java
 * 
 * Created on 08.03.2006. 18:40:24h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */
public class WPSDeegreeParams extends DeegreeParams {

	private String[] processDirectories;

	private RequestQueueManager requestQueueManager;

	/**
	 * 
	 * @param defaultOnlineResource
	 * @param cacheSize
	 * @param requestTimeLimit
	 * @param processDirectories
	 * @param requestStorageManager
	 */
	public WPSDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
			int requestTimeLimit, String[] processDirectories,
			RequestQueueManager requestQueueManager ) {
		super( defaultOnlineResource, cacheSize, requestTimeLimit );
		this.processDirectories = processDirectories;
		this.requestQueueManager = requestQueueManager;
	}

	/**
	 * @return Returns the processDirectories.
	 */
	public String[] getProcessDirectories() {
		return processDirectories;
	}

	/**
	 * 
	 * @return
	 */
	public RequestQueueManager getRequestQueueManager() {
		return requestQueueManager;
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSDeegreeParams.java,v $
Revision 1.6  2006/08/24 06:42:16  poth
File header corrected

Revision 1.5  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.4  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
