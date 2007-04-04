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

import static org.deegree.ogcwebservices.wps.capabilities.WPSOperationsMetadata.DESCRIBEPROCESS;
import static org.deegree.ogcwebservices.wps.capabilities.WPSOperationsMetadata.EXECUTE;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.OWSCommonCapabilitiesDocument;
import org.deegree.owscommon.OWSDomainType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * WPSCapabilitiesDocument.java
 * 
 * Creates an object representation of the sample_wps_capabilities.xml document
 * 
 * Created on 08.03.2006. 18:57:37h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */
public class WPSCapabilitiesDocument extends OWSCommonCapabilitiesDocument {

	private static final ILogger LOG = LoggerFactory.getLogger( WPSCapabilitiesDocument.class );

	private static final String XML_TEMPLATE = "WPSCapabilitiesTemplate.xml";

	/**
	 * @throws IOException
	 * @throws SAXException
	 */
	public void createEmptyDocument() throws IOException, SAXException {
		URL url = WPSCapabilitiesDocument.class.getResource( XML_TEMPLATE );
		if ( null == url ) {
			throw new IOException( "The resource '" + XML_TEMPLATE + " ' could not be found" );
		}
		load( url );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deegree.ogcwebservices.getcapabilities.OGCCapabilitiesDocument#parseCapabilities()
	 */
	@Override
	public OGCCapabilities parseCapabilities() throws InvalidCapabilitiesException {
		try {

			return new WPSCapabilities( parseVersion(), parseUpdateSequence(),
					getServiceIdentification(), getServiceProvider(), getOperationsMetadata(), null );
		} catch ( Exception e ) {
			LOG.logError( e.getMessage(), e );
			throw new InvalidCapabilitiesException( e.getMessage() );
		}
	}

	/**
	 * Creates an object representation of the
	 * <code>ows:OperationsMetadata</code> section.
	 * 
	 * @return object representation of the <code>ows:OperationsMetadata</code>
	 *         section
	 * @throws XMLParsingException
	 */
	@SuppressWarnings ( "unchecked")
	public OperationsMetadata getOperationsMetadata() throws XMLParsingException {
		LOG.entering();
		List operationElementList = XMLTools.getNodes( getRootElement(),
				"ows:OperationsMetadata/ows:Operation", nsContext );

		// build HashMap of 'ows:Operation'-elements for easier access
		Map operations = new HashMap();
		for ( int i = 0; i < operationElementList.size(); i++ ) {
			operations.put( XMLTools.getRequiredNodeAsString(
					( Node ) operationElementList.get( i ), "@name", nsContext ),
					operationElementList.get( i ) );
		}

		Operation getCapabilities = getOperation( OperationsMetadata.GET_CAPABILITIES_NAME, true,
				operations );
		Operation describeProcess = getOperation( DESCRIBEPROCESS, true, operations );
		Operation execute = getOperation( EXECUTE, true, operations );

		List parameterElementList = XMLTools.getNodes( getRootElement(),
				"ows:OperationsMetadata/ows:Parameter", nsContext );
		OWSDomainType[] parameters = new OWSDomainType[parameterElementList.size()];
		for ( int i = 0; i < parameters.length; i++ ) {
			parameters[i] = getOWSDomainType( null, ( Element ) parameterElementList.get( i ) );
		}

		List constraintElementList = XMLTools.getNodes( getRootElement(),
				"ows:OperationsMetadata/ows:Constraint", nsContext );
		OWSDomainType[] constraints = new OWSDomainType[constraintElementList.size()];
		for ( int i = 0; i < constraints.length; i++ ) {
			constraints[i] = getOWSDomainType( null, ( Element ) constraintElementList.get( i ) );
		}
		WPSOperationsMetadata metadata = new WPSOperationsMetadata( getCapabilities,
				describeProcess, execute, parameters, constraints );
		LOG.exiting();
		return metadata;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSCapabilitiesDocument.java,v $
Revision 1.5  2006/08/24 06:42:16  poth
File header corrected

Revision 1.4  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
