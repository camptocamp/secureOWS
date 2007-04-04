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
package org.deegree.ogcwebservices.wps.configuration;

import java.net.MalformedURLException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.wps.capabilities.WPSCapabilitiesDocument;
import org.deegree.ogcwebservices.wps.execute.RequestQueueManager;
import org.w3c.dom.Element;

/**
 * WPSConfigurationDocument.java
 * 
 * Created on 08.03.2006. 20:38:30h
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * 
 * @version 1.0.
 * 
 * @since 2.0
 */
public class WPSConfigurationDocument extends WPSCapabilitiesDocument {

	protected static final ILogger LOG = LoggerFactory.getLogger( WPSConfigurationDocument.class );

	/**
	 * Creates a class representation of the document.
	 * 
	 * @return class representation of the configuration document
	 */
	public WPSConfiguration getConfiguration() throws InvalidConfigurationException {
		WPSConfiguration WPSConfiguration = null;

		try {
			// last Parameter <code>ProcessOfferings</code> set to null, because
			// <code>WPSConfiguration</code>
			// constructor is responsible for instantiating ProcessOfferings
			WPSConfiguration = new WPSConfiguration( parseVersion(), parseUpdateSequence(),
					getServiceIdentification(), getServiceProvider(), getOperationsMetadata(),
					null, getDeegreeParams() );
		} catch ( XMLParsingException e ) {
			LOG.logError( e.getMessage() );
			throw new InvalidConfigurationException( e.getMessage() + "\n"
					+ StringTools.stackTraceToString( e ) );
		}
		return WPSConfiguration;
	}

	/**
	 * Creates a class representation of the <code>deegreeParams</code>-
	 * section.
	 * 
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public WPSDeegreeParams getDeegreeParams() throws InvalidConfigurationException {
		LOG.logInfo( nsContext.toString() );
		WPSDeegreeParams deegreeParams = null;

		try {
			Element element = ( Element ) XMLTools.getRequiredNode( getRootElement(),
					"./deegreewps:deegreeParams", nsContext );
			OnlineResource defaultOnlineResource = parseOnLineResource( ( Element ) XMLTools
					.getRequiredNode( element, "./deegreewps:DefaultOnlineResource", nsContext ) );
			int cacheSize = XMLTools.getNodeAsInt( element, "./deegreewps:CacheSize/text()",
					nsContext, 100 );
			int requestTimeLimit = XMLTools.getNodeAsInt( element,
					"./deegreewps:RequestTimeLimit/text()", nsContext, 2 );

			String[] processDirectories = XMLTools.getNodesAsStrings( element,
					"./deegreewps:ProcessDirectoryList/deegreewps:ProcessDirectory/text()",
					nsContext );
			int lengthOfProcessDirectoryList = processDirectories.length;
			if ( 0 == lengthOfProcessDirectoryList ) {
				LOG
						.logInfo( "No process directory specified. Using configuration document directory." );
				processDirectories = new String[] { "." };
			}

			for ( int i = 0; i < lengthOfProcessDirectoryList; i++ ) {
				try {
					processDirectories[i] = resolve( processDirectories[i] ).getFile();
				} catch ( MalformedURLException e ) {
					LOG.logError( "Process directory '" + processDirectories[i]
							+ "' cannot be resolved as a directory: " + e.getMessage(), e );
					throw new InvalidConfigurationException( "Process directory '"
							+ processDirectories[i] + "' cannot be resolved as a directory: "
							+ e.getMessage(), e );

				}
			}
			RequestQueueManager requestQueueManagerClass = getRequestQueueManagerClass( element );

			deegreeParams = new WPSDeegreeParams( defaultOnlineResource, cacheSize,
					requestTimeLimit, processDirectories, requestQueueManagerClass );

		} catch ( XMLParsingException e ) {
			LOG.logError( e.getMessage() );
			throw new InvalidConfigurationException( "Error parsing the deegreeParams "
					+ "section of the WPS configuration: \n" + e.getMessage()
					+ StringTools.stackTraceToString( e ) );
		}
		return deegreeParams;
	}

	private RequestQueueManager getRequestQueueManagerClass( Element deegreeParamsNode )
			throws XMLParsingException {

		// Get resonsible class for requestqueuemanager from deegreeParams
		// section
		RequestQueueManager requestQueueManager = null;

		String requestQueueManagerClass = XMLTools.getRequiredNodeAsString( deegreeParamsNode,
				"./deegreewps:RequestQueueManager/deegreewps:ResponsibleClass/text()", nsContext );

		Object tmp = null;
		try {
			tmp = Class.forName( requestQueueManagerClass ).newInstance();
		} catch ( ClassNotFoundException clnfe ) {

			String msg = "Responsible class for queue management: '" + requestQueueManagerClass
					+ "' not found.";
			LOG.logError( msg, clnfe );
			throw new XMLParsingException( msg, clnfe );
		} catch ( InstantiationException ia ) {

			String msg = "Responsible class for queue management: '" + requestQueueManagerClass
					+ "' can not be instantiated.";
			LOG.logError( msg, ia );
			throw new XMLParsingException( msg, ia );
		} catch ( IllegalAccessException iae ) {

			String msg = "Responsible class for queue management: '" + requestQueueManagerClass
					+ "' can not be accessed.";

			LOG.logError( msg, iae );
			throw new XMLParsingException( msg, iae );
		}

		if ( tmp instanceof RequestQueueManager ) {
			requestQueueManager = ( RequestQueueManager ) tmp;
		} else {
			String msg = "Responsible class for queue management: '"
					+ requestQueueManagerClass
					+ "' does not implement required Interface 'org.deegree.ogcwebservices.wps.execute.RequestQueueManager'.";
			;
			LOG.logError( msg );
			throw new XMLParsingException( msg );
		}
		LOG.logInfo( "requestQueueManager: " + requestQueueManagerClass );
		return requestQueueManager;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPSConfigurationDocument.java,v $
Revision 1.6  2006/08/24 06:42:16  poth
File header corrected

Revision 1.5  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.4  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
