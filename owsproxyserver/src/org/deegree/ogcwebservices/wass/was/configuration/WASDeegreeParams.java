//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/configuration/WASDeegreeParams.java,v 1.6 2006/10/17 20:31:17 poth Exp $
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
package org.deegree.ogcwebservices.wass.was.configuration;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.io.JDBCConnection;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * Encapsulates the deegree parameters for a WAS configuration.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/10/17 20:31:17 $
 * 
 * @since 2.0
 */
public class WASDeegreeParams extends DeegreeParams {

    private static final long serialVersionUID = 2700771143650528537L;

    private OnlineResource wasAddress = null;
    
    private int sessionLifetime = 0;
    
    private JDBCConnection databaseConnection;

    /**
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param wasAddress
     * @param sessionLifetime 
     * @param database 
     */
    public WASDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                            int requestTimeLimit, OnlineResource wasAddress, int sessionLifetime,
                            JDBCConnection database ) {
        this( defaultOnlineResource, cacheSize, requestTimeLimit,
              CharsetUtils.getSystemCharset(), wasAddress, sessionLifetime, database );
    }

    /**
     * 
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param characterSet
     * @param wasAddress
     * @param sessionLifetime 
     * @param database 
     */
    public WASDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                            int requestTimeLimit, String characterSet, OnlineResource wasAddress,
                            int sessionLifetime, JDBCConnection database ) {
        super( defaultOnlineResource, cacheSize, requestTimeLimit, characterSet );
        this.wasAddress = wasAddress;
        this.sessionLifetime = sessionLifetime;
        this.databaseConnection = database;
    }

    /**
     * returns the address of the WAS to be used to authenticate users
     * 
     * @return the address
     */
    public OnlineResource getWASAddress() {
        return wasAddress;
    }
    
    /**
     * @return the maximum session lifetime in milliseconds.
     */
    public int getSessionLifetime() {
        return sessionLifetime;
    }
    
    /**
     * @return an object containing database connection information
     */
    public JDBCConnection getDatabaseConnection() {
        return databaseConnection;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WASDeegreeParams.java,v $
 * Changes to this class. What the people have been up to: Revision 1.6  2006/10/17 20:31:17  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/07/12 16:59:32  poth
 * Changes to this class. What the people have been up to: required adaptions according to renaming of OnLineResource to OnlineResource
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/07/07 15:03:03  schmitz
 * Changes to this class. What the people have been up to: Fixed a few warnings.
 * Changes to this class. What the people have been up to: Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/07/07 10:41:18  schmitz
 * Changes to this class. What the people have been up to: Fixed unnecessary entries in the was configuration.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/06/09 12:58:32  schmitz
 * Changes to this class. What the people have been up to: Set up some tests for WAS/WSS and the URN class.
 * Changes to this class. What the people have been up to: Commented out some of the deegree param stuff in order for the
 * Changes to this class. What the people have been up to: tests to run.
 * Changes to this class. What the people have been up to: Tests have hardcoded URLs in them, so they won't run just anywhere.
 * Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.1 2006/05/29 12:00:58 bezema Changes to this class. What the
 * people have been up to: Refactored the security and authentication webservices into one package
 * WASS (Web Authentication -and- Security Services), also created a common package and a saml
 * package which could be updated to work in the future. Changes to this class. What the people have
 * been up to: Changes to this class. What the people have been up to: Revision 1.1 2006/05/26
 * 11:55:09 schmitz Changes to this class. What the people have been up to: Extended the handlers to
 * actually do something useful. Added configuration package to WAS, added plan text file. Added
 * GetSessionHandler interface, added CloseSessionHandler. Changes to this class. What the people
 * have been up to:
 **************************************************************************************************/
