//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/operation/WASGetCapabilities.java,v 1.4 2006/10/27 13:26:33 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
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
 Meckenheimer Allee 176
 53115 Bonn
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
package org.deegree.ogcwebservices.wass.was.operation;

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.w3c.dom.Element;

/**
 * This is the bean class for the WAS GetCapabilities operation.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/10/27 13:26:33 $
 * 
 * @since 2.0
 */

public class WASGetCapabilities extends GetCapabilities {

	private static final long serialVersionUID = -5377481260861904187L;

	private static final String SERVICE = "WAS";
    
    private static final ILogger LOG = LoggerFactory.getLogger(WASGetCapabilities.class);

    /**
     * @param id
     *            the request id
     * @param version
     * @param updateSequence
     * @param acceptVersions
     * @param sections
     * @param acceptFormats
     * @param vendoreSpec
     */
    public WASGetCapabilities( String id, String version, String updateSequence,
                                 String[] acceptVersions, String[] sections, String[] acceptFormats,
                                 Map<String,String> vendoreSpec) {
        super( id, version, updateSequence, acceptVersions, sections, acceptFormats, vendoreSpec );
    }
    
    /**
     * @param id the request id
     * @param keyValues 
     */
    public WASGetCapabilities( String id, Map<String, String> keyValues ){
        super( id, getParam( "VERSION", keyValues, null ), null, null, null, null, keyValues );
    }

	/* (non-Javadoc)
	 * @see org.deegree.ogcwebservices.OGCWebServiceRequest#getServiceName()
	 */
	public String getServiceName() {
		return SERVICE;
	}

    /**
     * @param id
     * @param documentElement
     * @return a new instance of this class
     * @throws OGCWebServiceException
     */
    public static OGCWebServiceRequest create( String id, Element documentElement )
                            throws OGCWebServiceException {
        LOG.entering();
        
        WASGetCapabilities res = null;
        try {
            res = new WASGetCapabilitiesDocument().parseCapabilities( id, documentElement );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        }
        
        LOG.exiting();
        return res;
    }

    /**
     * @param id
     * @param kvp
     * @return a new instance of this class
     */
    public static OGCWebServiceRequest create( String id, Map<String, String> kvp ) {
        return new WASGetCapabilities(id, kvp);
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WASGetCapabilities.java,v $
 Revision 1.4  2006/10/27 13:26:33  poth
 support for vendorspecific parameters added

 Revision 1.3  2006/06/23 10:23:50  schmitz
 Completed the WAS, GetSession and CloseSession work.

 Revision 1.2  2006/06/12 13:32:29  bezema
 kvp is implemented

 Revision 1.1  2006/05/29 12:00:58  bezema
 Refactored the security and authentication webservices into one package WASS (Web Authentication -and- Security Services), also created a common package and a saml package which could be updated to work in the future.

 Revision 1.4  2006/05/23 15:20:50  bezema
 Cleaned up the warnings and added some minor methods

 Revision 1.3  2006/05/22 12:17:31  schmitz
 Restructured the GetCapabilities/Document classes.

 Revision 1.2  2006/05/22 11:29:35  bezema
 Reviewing the getcapabilities classes

 Revision 1.1  2006/05/19 15:35:35  schmitz
 Updated the documentation, added the GetCapabilities operation and implemented a rough WAService outline. Fixed some warnings.



 ********************************************************************** */