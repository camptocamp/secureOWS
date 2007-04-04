// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/WCSConfiguration.java,v 1.12 2006/08/07 13:43:14 poth Exp $
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
 Aennchenstr. 19
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
package org.deegree.ogcwebservices.wcs.configuration;

import java.io.IOException;
import java.net.URL;

import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.Capability;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.Service;
import org.deegree.ogcwebservices.wcs.getcapabilities.ContentMetadata;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.12 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.12 $, $Date: 2006/08/07 13:43:14 $
 * 
 * @since 2.0
 */

public class WCSConfiguration extends WCSCapabilities {

    private WCSDeegreeParams deegreeParams = null;

    /**
     * creates a WCSConfiguration from an URL enabling access to a deegree WCS
     * configuration document
     * 
     * @param url
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public static WCSConfiguration create(URL url) throws IOException,
            SAXException, InvalidCapabilitiesException,
            InvalidConfigurationException {
        WCSConfigurationDocument confDoc = new WCSConfigurationDocument();
        confDoc.load(url);
        return new WCSConfiguration(confDoc);
    }

    /**
     * creates a WCSConfiguration from a deegree WCSConfigurationDocument
     * 
     * @param confDoc
     * @throws InvalidCapabilitiesException
     * @throws InvalidConfigurationException
     */
    private WCSConfiguration(WCSConfigurationDocument confDoc)
            throws InvalidCapabilitiesException, InvalidConfigurationException {                
        super(confDoc.parseVersion(), null, confDoc.parseServiceSection(), confDoc
                .getCapabilitySection(CommonNamespaces.WCSNS), confDoc
                .parseContentMetadataSection());        
        this.deegreeParams = confDoc.getDeegreeParamsSection();
    }

    /**
     * creates a WCSConfiguration from its sections
     * 
     * @param version
     * @param updateSequence
     * @param service
     * @param capabilitiy
     * @param contentMetadata
     */
    public WCSConfiguration(String version, String updateSequence,
            WCSDeegreeParams deegreeParams, Service service,
            Capability capabilitiy, ContentMetadata contentMetadata) {
        super(version, updateSequence, service, capabilitiy, contentMetadata);
        this.deegreeParams = deegreeParams;
    }

    /**
     * @return Returns the deegreeParam.
     * 
     */
    public WCSDeegreeParams getDeegreeParams() {
        return deegreeParams;
    }

    /**
     * @param deegreeParams
     *            The deegreeParam to set.
     */
    public void setDeegreeParam(WCSDeegreeParams deegreeParams) {
        this.deegreeParams = deegreeParams;
    }
   

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * WCSConfiguration.java,v $ Revision 1.3 2005/03/09 11:55:47 mschneider **
 * empty log message ***
 * 
 * Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.13 2004/07/12 11:14:19 ap no message
 * 
 * Revision 1.12 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.11 2004/07/05 13:42:38 mschneider Changed deegreeParam to
 * deegreeParams wherever it is used.
 * 
 * Revision 1.10 2004/07/02 15:36:11 ap no message
 * 
 * Revision 1.9 2004/06/21 08:05:49 ap no message
 * 
 * Revision 1.8 2004/06/14 14:52:20 ap no message
 * 
 * Revision 1.7 2004/06/08 07:01:51 ap no message
 * 
 * Revision 1.6 2004/06/03 09:02:20 ap no message
 * 
 * Revision 1.5 2004/06/02 14:09:02 ap no message
 * 
 * Revision 1.4 2004/05/25 15:13:23 ap no message
 * 
 * Revision 1.3 2004/05/25 12:55:02 ap no message
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:54:39 ap no message
 * 
 *  
 ******************************************************************************/
