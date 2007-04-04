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

import java.util.LinkedList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 * DefaultRequestQueueManager.java
 * 
 * Created on 24.03.2006. 19:50:08h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */

public class DefaultRequestQueueManager implements RequestQueueManager {

	private static final ILogger LOG = LoggerFactory.getLogger( DefaultRequestQueueManager.class );

	private LinkedList<ExecuteRequest> requestQueue = null;

	/**
	 * 
	 */
	public boolean addRequestToQueue( ExecuteRequest executeRequest ) throws OGCWebServiceException {
		boolean success = false;

		if ( null == requestQueue ) {
			requestQueue = new LinkedList<ExecuteRequest>();
		}

		try {
			requestQueue.add( executeRequest );
			success = true;
			LOG.logInfo( "Request successfully added to queue!" );
		} catch ( Exception e ) {
			LOG.logError( "Problem while saving request to queue!" );
		}
		return success;
	}

	/**
	 * 
	 */
	public ExecuteRequest getRequestFromQueue() throws OGCWebServiceException {
		ExecuteRequest execRequest = null;
		if ( requestQueue.size() > 0 ) {
			LOG.logInfo( "SIZE vorher: " + requestQueue.size() );
			execRequest = requestQueue.removeFirst();
			LOG.logInfo( "SIZE nachher: " + requestQueue.size() );
		}
		return execRequest;
	}

	/**
	 * 
	 */
	public int getLengthOfQueue() throws OGCWebServiceException {

		int lengthOfQueue = 0;

		if ( null != requestQueue ) {
			lengthOfQueue = requestQueue.size();
		}
		return lengthOfQueue;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultRequestQueueManager.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
