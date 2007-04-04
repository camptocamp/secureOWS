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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.Code;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OperationNotSupportedException;
import org.deegree.ogcwebservices.wps.WPSRequestBaseType;
import org.w3c.dom.Element;

/**
 * DescribeProcessRequest.java
 * 
 * Created on 09.03.2006. 22:33:16h
 * 
 * WPS DescribeProcess operation request.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */

public class DescribeProcessRequest extends WPSRequestBaseType {

	private static final ILogger LOG = LoggerFactory.getLogger( DescribeProcessRequest.class );

	/**
	 * @param version
	 * @param id
	 * @param vendorSpecificParameter
	 * @param identifier
	 */
	public DescribeProcessRequest( String version, String id, Map vendorSpecificParameter,
			List<Code> identifier ) {
		super( version, id, vendorSpecificParameter );
		this.identifier = identifier;

	}

	/**
	 * Unordered list of one or more identifiers of the processes for which the
	 * client is requesting detailed descriptions. This element shall be
	 * repeated for each process for which a description is requested. These
	 * Identifiers are unordered, but the WPS shall return the descriptions in
	 * the order in which they were requested.
	 */
	protected List<Code> identifier;

	/**
	 * @return Returns the identifier.
	 */
	public List<Code> getIdentifier() {
		if ( identifier == null ) {
			identifier = new ArrayList<Code>();
		}
		return this.identifier;
	}

	/**
	 * Creates a WPSDescribeProcess Request class representation from a
	 * key/value pair encoded request
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws InconsistentRequestException
	 * @throws InvalidParameterValueException
	 * @throws XMLParsingException
	 * @throws MissingParameterValueException
	 */
	@SuppressWarnings ( "unchecked")
	public static DescribeProcessRequest create( String id, String request )
			throws InvalidParameterValueException, MissingParameterValueException {
		Map map = KVP2Map.toMap( request );
		map.put( "ID", id );
		return create( map );

	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws InconsistentRequestException
	 * @throws InvalidParameterValueException
	 * @throws XMLParsingException
	 * @throws MissingParameterValueException
	 */
	public static DescribeProcessRequest create( Map request ) throws  InvalidParameterValueException, 
                                                                MissingParameterValueException {

		String version = extractVersionParameter( request );
		String id = ( String ) request.get( "ID" );
		Map vendorSpecificParameters = null;

		List<Code> identifier = null;

		String tmp = ( String ) request.get( "IDENTIFIER" );

		if ( null == tmp ) {
			String msg = "Parameter 'Identifier' must be set.";
			LOG.logError( msg );
			throw new MissingParameterValueException( msg );
		}
		String[] values = StringTools.toArray( tmp, ",", false );

		if ( values.length == 0 ) {
			String msg = "Identifier must at least declare one process name.";
			LOG.logError( msg );
			throw new InvalidParameterException( msg );
		}
		identifier = new ArrayList<Code>( values.length );
		for ( int i = 0; i < values.length; i++ ) {

			identifier.add( new Code( values[i], null ) );
		}
		
		return new DescribeProcessRequest( version, id, vendorSpecificParameters, identifier );
	}

	/**
	 * XML-coded decribe process request currently not supported. This is not a
	 * mandatory operation.
	 * 
	 * @see OGC 05-007r4 Subclause 9.2.3
	 * 
	 * @param id
	 * @param element
	 * @return
	 * @throws OGCWebServiceException
	 * @throws MissingParameterValueException
	 * @throws InvalidParameterValueException
	 */
	public static DescribeProcessRequest create( String id, Element element )
			throws OGCWebServiceException {
		throw new OperationNotSupportedException(
				"HTTP post transfer of XML encoded describe process request not supported." );
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeProcessRequest.java,v $
Revision 1.4  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
