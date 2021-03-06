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

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcbase.OGCDocument;
import org.xml.sax.SAXException;

/**
 * ProcessDescriptionsDocument.java
 * 
 * Created on 10.03.2006. 13:00:42h
 * 
 * Creates an empty <code>WPSProcessDescriptions</code> Document for output as
 * an describe process response.
 * 
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */

public class ProcessDescriptionsDocument extends OGCDocument {

	private static final ILogger LOG = LoggerFactory.getLogger( ProcessDescriptionsDocument.class );

	private static final String XML_TEMPLATE = "ProcessDescriptionsTemplate.xml";

	public void createEmptyDocument() throws IOException, SAXException {

		LOG.entering();

		URL url = ProcessDescriptionsDocument.class.getResource( XML_TEMPLATE );
		if ( null == url ) {
			throw new IOException( "The resource '" + XML_TEMPLATE + " ' could not be found" );
		}
		load( url );

		LOG.exiting();
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ProcessDescriptionsDocument.java,v $
Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
