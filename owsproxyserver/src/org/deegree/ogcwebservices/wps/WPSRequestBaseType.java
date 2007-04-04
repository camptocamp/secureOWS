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

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;

/**
 * WPSRequestBaseType.java
 * 
 * Created on 09.03.2006. 22:47:16h
 * 
 * WPS operation request base, for all WPS operations except GetCapabilities. In
 * this XML encoding, no "request" parameter is included, since the element name
 * specifies the specific operation.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0
 * @since 2.0
 */

public class WPSRequestBaseType extends AbstractOGCWebServiceRequest {

	/**
	 * Service type identifier.
	 */
	protected static final String service = "WPS";

	/**
	 * Version identifier.
	 */
	protected static final String supportedVersion = "0.4.0";

	private static final ILogger LOG = LoggerFactory.getLogger( WPSRequestBaseType.class );

	/**
	 * @param version
	 * @param id
	 * @param vendorSpecificParameter
	 */
	public WPSRequestBaseType( String version, String id, Map vendorSpecificParameter ) {
		super( version, id, vendorSpecificParameter );
	}

	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return supportedVersion;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws MissingParameterValueException
	 * @throws InvalidParameterValueException
	 */
	protected static String extractVersionParameter( Map request )
			throws MissingParameterValueException, InvalidParameterValueException {
		String version = ( String ) request.get( "VERSION" );
		if ( null == version ) {
			String msg = "Version parameter must be set.";
			LOG.logError( msg );
			throw new MissingParameterValueException( "version", msg );
		} else if ( "".equals( version ) ) {
			String msg = "Version parameter must not be empty.";
			LOG.logError( msg );
			throw new InvalidParameterValueException( "version", msg );
		} else if ( !supportedVersion.equals( version ) ) {
			String msg = "Only version 0.4.0 is currently supported by this wpserver instance";
			LOG.logError( msg );
			throw new InvalidParameterValueException( "version", msg );
		}
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deegree.ogcwebservices.OGCWebServiceRequest#getServiceName()
	 */
	public String getServiceName() {
		return service;
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSRequestBaseType.java,v $
Revision 1.4  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
