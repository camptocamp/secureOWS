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

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OperationNotSupportedException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.w3c.dom.Element;

/**
 * WPSGetCapabilitiesRequest.java
 * 
 * 
 * Created on 08.03.2006. 22:15:18h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */
public class WPSGetCapabilitiesRequest extends GetCapabilities {

	private static final ILogger LOG = LoggerFactory.getLogger( WPSGetCapabilitiesRequest.class );

	/**
	 * 
	 * @param id
	 * @param version
	 * @param updateSequence
	 * @param acceptVersions
	 * @param sections
	 * @param acceptFormats
     * @param vendoreSpec
	 */
	protected WPSGetCapabilitiesRequest( String id, String version, String updateSequence,
			String[] acceptVersions, String[] sections, String[] acceptFormats, 
            Map<String,String> vendoreSpec) {
		super( id, version, updateSequence, acceptVersions, sections, acceptFormats, vendoreSpec );
	}

	/**
	 * creates a <tt>WFSGetCapabilitiesRequest</tt> object.
	 * 
	 * @param id
	 *            id of the request
	 * @param vendorSpecificParameter
	 *            none standadized parameters as name-value pairs
	 * @param native_
	 *            is intended to allow access to vendor specific capabilities
	 */
	public static WPSGetCapabilitiesRequest create( String id, String version,
			String updateSequence, String[] acceptVersions, String[] sections,
			String[] acceptFormats, Map<String,String> vendoreSpec ) {
		return new WPSGetCapabilitiesRequest( id, version, updateSequence, acceptVersions,
				sections, acceptFormats, vendoreSpec);
	}

	/**
	 * creates a WPS GetCapabilities request class representation from a
	 * key-value-pair encoded request
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@SuppressWarnings ( "unchecked")
	public static WPSGetCapabilitiesRequest create( String id, String request )
			throws InvalidParameterValueException, MissingParameterValueException {
		Map map = KVP2Map.toMap( request );
		map.put( "ID", id );
		return create( map );
	}

	/**
	 * creates a WPS GetCapabilities request class representation form a
	 * key-value-pair encoded request
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	public static WPSGetCapabilitiesRequest create( Map<String,String> request )
			throws InvalidParameterValueException, MissingParameterValueException {
		LOG.entering();

		String id = request.remove( "ID" );
		String service = request.remove( "SERVICE" );
		if ( null == service ) {
			throw new MissingParameterValueException( "WPSGetCapabilities",
					"'service' parameter is missing" );
		}
		if ( !"WPS".equals( service ) ) {
			throw new InvalidParameterValueException( "WPSGetCapabilities",
					"service attribute must equal 'WPS'" );
		}
		String updateSeq = request.remove( "UPDATESEQUENCE" );
		String version = request.remove( "VERSION" );
		LOG.exiting();
		// accept versions, sections, accept formats not supported
		return new WPSGetCapabilitiesRequest( id, version, updateSeq, null, null, null, request );
	}

	/**
	 * XML-coded get capabilities request not supported.
	 * 
	 * @see OGC 05-007r4 Subclause 8.2
	 * 
	 * @param id
	 * @param element
	 * @return
	 * @throws OGCWebServiceException
	 * @throws MissingParameterValueException
	 * @throws InvalidParameterValueException
	 */
	public static WPSGetCapabilitiesRequest create( String id, Element element )
			throws OGCWebServiceException  {
		throw new OperationNotSupportedException(
				"HTTP post transfer of XML encoded get capabilities request not supported." );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deegree.ogcwebservices.OGCWebServiceRequest#getServiceName()
	 */
	public String getServiceName() {
		return "WPS";
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSGetCapabilitiesRequest.java,v $
Revision 1.5  2006/10/27 13:28:27  poth
support for vendorspecific parameters added

Revision 1.4  2006/08/24 06:42:16  poth
File header corrected

Revision 1.3  2006/08/07 12:16:25  poth
never thrown exception removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
