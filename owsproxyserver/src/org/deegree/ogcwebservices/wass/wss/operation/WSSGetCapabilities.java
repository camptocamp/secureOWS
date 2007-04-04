//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/WSSGetCapabilities.java,v 1.8 2006/10/27 13:26:33 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
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

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wass.wss.operation;

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.w3c.dom.Element;

/**
 * The WSS specific GetCapabilities request parameters are stored in this "bean".
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/10/27 13:26:33 $
 * 
 * @since 2.0
 */

public class WSSGetCapabilities extends GetCapabilities {

    /**
     * 
     */
    private static final long serialVersionUID = 8041982281107855304L;

    /**
     * The logger enhances the quality and simplicity of Debugging within the deegree2 framework
     */
    private static final ILogger LOG = LoggerFactory.getLogger( WSSGetCapabilities.class );

    private static final String SERVICE = "WSS";

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
    public WSSGetCapabilities( String id, String version, String updateSequence,
                                 String[] acceptVersions, String[] sections, String[] acceptFormats,
                                 Map<String,String> vendoreSpec) {
        super( id, version, updateSequence, acceptVersions, sections, acceptFormats, vendoreSpec );
    }

    /**
     * @param id
     *            the request id
     * @param keyValues
     */
    public WSSGetCapabilities( String id, Map<String, String> keyValues ) {
        super( id, getParam( "VERSION", keyValues, null ), null, null, null, null, keyValues );
    }

    /*
     * (non-Javadoc)
     * 
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
        try {
            return new WSSGetCapabilitiesDocument().parseCapabilities( id, documentElement );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        }
    }

    /**
     * @param id
     * @param kvp
     * @return a new instance of this class
     */
    public static OGCWebServiceRequest create( String id, Map<String, String> kvp ) {
        return new WSSGetCapabilities( id, kvp );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WSSGetCapabilities.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/10/27 13:26:33  poth
 * Changes to this class. What the people have been up to: support for vendorspecific parameters added
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/23 10:23:50  schmitz
 * Changes to this class. What the people have been up to: Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/20 15:31:04  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to:
 * Revision 1.4 2006/06/19 15:34:04 bezema changed wass to handle things the right way
 * 
 * Revision 1.3 2006/06/12 13:32:29 bezema kvp is implemented
 * 
 * Revision 1.2 2006/05/30 10:12:02 bezema Putting the cvs asci option to -kkv which will update the
 * $revision$ $author$ and $date$ variables in a cvs commit
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * Revision 1.1 2006/05/22 15:48:16 bezema Starting the parsing of the xml request in wss
 * 
 * 
 **************************************************************************************************/