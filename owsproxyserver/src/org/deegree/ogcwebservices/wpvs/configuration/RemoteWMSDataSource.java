//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/configuration/RemoteWMSDataSource.java,v 1.17 2006/11/27 11:33:33 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
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

package org.deegree.ogcwebservices.wpvs.configuration;

import java.awt.Color;
import java.net.URL;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.wms.RemoteWMService;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wpvs.capabilities.OWSCapabilities;

/**
 * This class represents a remote WPVS dataSource object.
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.17 $, $Date: 2006/11/27 11:33:33 $
 * 
 * @since 2.0
 */
public class RemoteWMSDataSource extends LocalWMSDataSource {

	private static final ILogger LOG = LoggerFactory.getLogger( RemoteWMSDataSource.class );
	
	/**
	 * Creates a new <code>RemoteWMSDataSource</code> object from the given parameters.
	 * 
	 * @param name
	 * @param owsCapabilities
	 * @param validArea
	 * @param minScaleDenominator
	 * @param maxScaleDenominator
	 * @param filterCondition
	 * @param transparentColors
	 */
	public RemoteWMSDataSource( QualifiedName name, OWSCapabilities owsCapabilities, Surface validArea, 
								double minScaleDenominator, double maxScaleDenominator, 
								GetMap filterCondition, Color[] transparentColors ) {
		
		super( name, owsCapabilities, validArea, minScaleDenominator,
			   maxScaleDenominator, filterCondition, transparentColors );
		this.setServiceType( AbstractDataSource.REMOTE_WMS );
		
	}

	/**
	 * The <code>filterCondition</code> is a map of key-value-pairs of an incomplete WMSRequest. 
	 * 
	 * @return Returns the filterCondition as a map of key-value-pairs.
	 */
	public Map getFilterConditionMap() {
		return (Map)getFilterCondition();
	}

	/**
     * @see org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource#getOGCWebService()
     */
    @Override
    protected OGCWebService createOGCWebService() {       
        try {
        	
        	URL url = getOwsCapabilities().getOnlineResource();
        	WMSCapabilitiesDocument wmsCapsDoc = new WMSCapabilitiesDocument();
        	wmsCapsDoc.load( url );
            WMSCapabilities wmsCaps = (WMSCapabilities)wmsCapsDoc.parseCapabilities(); 
            	
            return new RemoteWMService( wmsCaps ); 
            
        } catch (Exception e) {
        	
        	LOG.logError( "Could not instatiate Datasource: " + toString(), e );
            return null;
        }   
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoteWMSDataSource.java,v $
Revision 1.17  2006/11/27 11:33:33  bezema
UPdating javadocs and cleaning up

Revision 1.16  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.15  2006/08/29 19:54:14  poth
footer corrected

Revision 1.14  2006/08/24 06:42:16  poth
File header corrected

Revision 1.13  2006/06/20 07:45:21  taddei
datasources use quali names now

Revision 1.12  2006/04/06 20:25:24  poth
*** empty log message ***

Revision 1.11  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.10  2006/02/22 13:34:00  taddei
refactoring: added service, createOGCWebService; also better except handling

Revision 1.9  2006/02/14 15:14:15  taddei
printStackTrace

Revision 1.8  2006/02/10 16:06:05  taddei
chnages to accomodate remote WMS

Revision 1.7  2006/01/18 08:48:27  taddei
added getOGCWebService()

Revision 1.6  2005/12/23 10:37:49  mays
change object type of filterCondition

Revision 1.5  2005/12/07 09:45:14  mays
redesign of filterCondition request from String to Map form wcs and wms datasources

Revision 1.4  2005/12/06 12:48:19  mays
move param filterCondition from subclasses to AbstractDataSource

Revision 1.3  2005/12/05 09:42:07  mays
revision of comments

Revision 1.2  2005/12/01 15:54:59  mays
omitted serviceType in call of constructor, replacing it in super() by corresponding field

Revision 1.1  2005/12/01 12:08:47  mays
first implementation of new remote data source classes

********************************************************************** */
