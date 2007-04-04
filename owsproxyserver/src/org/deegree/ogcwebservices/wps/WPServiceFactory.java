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

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wps.configuration.WPSConfiguration;
import org.deegree.ogcwebservices.wps.configuration.WPSConfigurationDocument;
import org.xml.sax.SAXException;

/**
 * WPServiceFactory.java
 * 
 * Created on 08.03.2006. 17:47:52h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */
public final class WPServiceFactory {

	private static WPSConfiguration CONFIG = null;

	private static final ILogger LOG = LoggerFactory.getLogger( WPSConfiguration.class );

	private WPServiceFactory() {

	}

	/**
	 * 
	 * @param config
	 * @return
	 */
	public static WPService getInstance( WPSConfiguration config ) {
		return new WPService( config );
	}

	/**
	 * 
	 * @return
	 * @throws OGCWebServiceException
	 */
	public static WPService getInstance() throws OGCWebServiceException {
		if ( null == CONFIG ) {
			throw new OGCWebServiceException( WPServiceFactory.class.getName(),
					"Configuration has not been initialized" );
		}
		return new WPService( CONFIG );
	}

	/**
	 * 
	 * @param wpsConfiguration
	 */
	public static void setConfiguration( WPSConfiguration wpsConfiguration ) {
		CONFIG = wpsConfiguration;
	}

	/**
	 * 
	 * @param serviceConfigurationUrl
	 * @throws InvalidConfigurationException
	 * @throws IOException
	 */
	public static void setConfiguration( URL serviceConfigurationUrl )
			throws InvalidConfigurationException, IOException {
		LOG.entering();
		WPSConfigurationDocument wpsConfDoc = new WPSConfigurationDocument();
		try {
			wpsConfDoc.load( serviceConfigurationUrl );
		} catch ( SAXException e ) {
			LOG.logError( "SAXException: " + e.getMessage() );
			throw new InvalidConfigurationException( "WPServiceFactory", e.getMessage() );
		}
		WPServiceFactory.setConfiguration( wpsConfDoc.getConfiguration() );

		LOG.exiting();
	}

	/**
	 * 
	 * @return
	 * @throws OGCWebServiceException
	 * @todo
	 */
	public static WPService getService() {
		return WPServiceFactory.getInstance( CONFIG );
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPServiceFactory.java,v $
Revision 1.6  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.5  2006/08/24 06:42:16  poth
File header corrected

Revision 1.4  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
