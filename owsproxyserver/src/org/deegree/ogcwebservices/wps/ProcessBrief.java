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

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.Code;
import org.deegree.ogcwebservices.MetadataType;

/**
 * ProcessBrief.java
 * 
 * Created on 09.03.2006. 14:13:37h
 * 
 * Brief description of a Process, designed for Process discovery.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */
public class ProcessBrief extends WPSDescription {

	/**
	 * Optional unordered list of additional metadata about this process. A list
	 * of optional and/or required metadata elements for this process could be
	 * specified in a specific Application Profile for this service.
	 */
	private List<MetadataType> metadata;

	/**
	 * Optional unordered list of additional metadata about this process. A list
	 * of optional and/or required metadata elements for this process could be
	 * specified in a specific Application Profile for this service.
	 */
	private String processVersion;

	/**
	 * 
	 * @param identifier
	 * @param title
	 * @param _abstract
	 * @param processVersion
	 * @param metadata
	 */
	public ProcessBrief( Code identifier, String title, String _abstract, String processVersion,
			List<MetadataType> metadata ) {
		super( identifier, title, _abstract );
		this.processVersion = processVersion;
		this.metadata = metadata;
	}

	/**
	 * 
	 * @param identifier
	 * @param title
	 */
	public ProcessBrief( Code identifier, String title ) {
		super( identifier, title );
	}

	/**
	 * 
	 * @return
	 */
	public List<MetadataType> getMetadata() {
		if ( metadata == null ) {
			metadata = new ArrayList<MetadataType>();
		}
		return this.metadata;
	}

	/**
	 * 
	 * @param metadataType
	 */
	public void setMetadata( List<MetadataType> metadataType ) {
		this.metadata = metadataType;
	}

	/**
	 * 
	 * @return
	 */
	public String getProcessVersion() {
		return processVersion;
	}

	/**
	 * 
	 * @param value
	 */
	public void setProcessVersion( String value ) {
		this.processVersion = value;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ProcessBrief.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
