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
package org.deegree.ogcwebservices.wps;

import org.deegree.datatypes.Code;

/**
 * WPSDescription.java
 * 
 * Created on 09.03.2006. 15:00:05h
 * 
 * Description of a WPS process, input, or output object.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */
public class WPSDescription {

	/**
	 * Unambiguous identifier or name of a process, unique for this server, or
	 * unambiguous identifier or name of an input or output, unique for this
	 * process.
	 */
	protected Code identifier;

	/**
	 * Title of a process, input, or output, normally available for display to a
	 * human.
	 */
	protected String title;

	/**
	 * Brief narrative description of a process, input, or output, normally
	 * available for display to a human.
	 */
	protected String _abstract;

	/**
	 * 
	 * @param identifier
	 * @param title
	 * @param _abstract
	 */
	public WPSDescription( Code identifier, String title, String _abstract ) {
		this.identifier = identifier;
		this.title = title;
		this._abstract = _abstract;
	}

	/**
	 * 
	 * @param identifier
	 * @param title
	 */
	public WPSDescription( Code identifier, String title ) {
		this.identifier = identifier;
		this.title = title;
	}

	/**
	 * 
	 * @return
	 */
	public Code getIdentifier() {
		return identifier;
	}

	/**
	 * 
	 * @param value
	 */
	public void setIdentifier( Code value ) {
		this.identifier = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param value
	 */
	public void setTitle( String value ) {
		this.title = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getAbstract() {
		return _abstract;
	}

	/**
	 * 
	 * @param value
	 */
	public void setAbstract( String value ) {
		this._abstract = value;
	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSDescription.java,v $
Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
