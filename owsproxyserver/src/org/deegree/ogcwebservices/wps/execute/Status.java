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

/**
 * StatusType.java
 * 
 * Created on 09.03.2006. 23:19:43h
 * 
 * Description of the status of process execution.
 * 
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class Status {

	/**
	 * Indicates that this process has been accepted by the server, but is in a
	 * queue and has not yet started to execute. The contents of this
	 * human-readable text string is left open to definition by each server
	 * implementation, but is expected to include any messages the server may
	 * wish to let the clients know. Such information could include how long the
	 * queue is, or any warning conditions that may have been encountered. The
	 * client may display this text to a human user.
	 */

	private String processAccepted;

	/**
	 * Indicates that this process has been has been accepted by the server, and
	 * processing has begun.
	 */
	private ProcessStarted processStarted;

	/**
	 * Indicates that this process has successfully completed execution. The
	 * contents of this human-readable text string is left open to definition by
	 * each server, but is expected to include any messages the server may wish
	 * to let the clients know, such as how long the process took to execute, or
	 * any warning conditions that may have been encountered. The client may
	 * display this text string to a human user. The client should make use of
	 * the presence of this element to trigger automated or manual access to the
	 * results of the process. If manual access is intended, the client should
	 * use the presence of this element to present the results as downloadable
	 * links to the user.
	 */

	private String processSucceeded;

	/**
	 * Indicates that execution of this process has failed, and includes error
	 * information.
	 */
	private ProcessFailed processFailed;

	/**
	 * @return Returns the processAccepted.
	 */
	public String getProcessAccepted() {
		return processAccepted;
	}

	/**
	 * @param processAccepted
	 *            The processAccepted to set.
	 */
	public void setProcessAccepted( String value ) {
		this.processAccepted = value;
	}

	/**
	 * @return Returns the processStarted.
	 */
	public ProcessStarted getProcessStarted() {
		return processStarted;
	}

	/**
	 * @param processStarted
	 *            The processStarted to set.
	 */
	public void setProcessStarted( ProcessStarted value ) {
		this.processStarted = value;
	}

	/**
	 * @return Returns the processSucceeded.
	 */
	public String getProcessSucceeded() {
		return processSucceeded;
	}

	/**
	 * @param processSucceeded
	 *            The processSucceeded to set.
	 */
	public void setProcessSucceeded( String value ) {
		this.processSucceeded = value;
	}

	/**
	 * @return Returns the processFailed.
	 */
	public ProcessFailed getProcessFailed() {
		return processFailed;
	}

	/**
	 * @param processFailed
	 *            The processFailed to set.
	 */
	public void setProcessFailed( ProcessFailed value ) {
		this.processFailed = value;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Status.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
