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
 * ProcessStartedType.java
 * 
 * Created on 09.03.2006. 23:21:37h
 * 
 * Indicates that this process has been has been accepted by the server, and
 * processing has begun.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class ProcessStarted {

	/**
	 * A human-readable text string whose contents are left open to definition
	 * by each WPS server, but is expected to include any messages the server
	 * may wish to let the clients know. Such information could include how much
	 * longer the process may take to execute, or any warning conditions that
	 * may have been encountered to date. The client may display this text to a
	 * human user.
	 */
	private String value;

	/**
	 * Percentage of the process that has been completed, where 0 means the
	 * process has just started, and 100 means the process is complete. This
	 * attribute should be included if the process is expected to execute for a
	 * long time (i.e. more than a few minutes). This percentage is expected to
	 * be accurate to within ten percent
	 */
	private int percentCompleted;

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue( String value ) {
		this.value = value;
	}

	/**
	 * @return Returns the percentCompleted.
	 */
	public int getPercentCompleted() {
		return percentCompleted;
	}

	/**
	 * @param percentCompleted
	 *            The percentCompleted to set.
	 */
	public void setPercentCompleted( int value ) {
		this.percentCompleted = value;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ProcessStarted.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
