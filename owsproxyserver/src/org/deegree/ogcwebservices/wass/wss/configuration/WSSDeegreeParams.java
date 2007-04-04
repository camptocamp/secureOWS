//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/configuration/WSSDeegreeParams.java,v 1.11 2006/10/17 20:31:20 poth Exp $
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

 Jens Fitzke
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wass.wss.configuration;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.io.JDBCConnection;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * Encapsulates deegree parameter data for a WSS.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/10/17 20:31:20 $
 * 
 * @since 2.0
 */
public class WSSDeegreeParams extends DeegreeParams {

    private static final long serialVersionUID = 2700771143650528537L;

    private OnlineResource securedServiceAddress = null;

    private OnlineResource wasAddress = null;
    
    private int sessionLifetime = 0;
    
    private JDBCConnection databaseConnection = null;
    

    /**
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param securedServiceAddress
     * @param wasAddress
     * @param sessionLifetime 
     * @param db 
     */
    public WSSDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                            int requestTimeLimit, OnlineResource securedServiceAddress,
                            OnlineResource wasAddress, int sessionLifetime, JDBCConnection db ) {
        this( defaultOnlineResource, cacheSize, requestTimeLimit,
              CharsetUtils.getSystemCharset(), securedServiceAddress, wasAddress, sessionLifetime, db );
    }

    /**
     * 
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param characterSet
     * @param securedServiceAddress
     * @param wasAddress
     * @param sessionLifetime 
     * @param db 
     */
    public WSSDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                            int requestTimeLimit, String characterSet,
                            OnlineResource securedServiceAddress, OnlineResource wasAddress,
                            int sessionLifetime, JDBCConnection db ) {
        super( defaultOnlineResource, cacheSize, requestTimeLimit, characterSet );
        this.securedServiceAddress = securedServiceAddress;
        this.wasAddress = wasAddress;
        this.sessionLifetime = sessionLifetime;
        this.databaseConnection = db;
    }

    /**
     * returns the online resource to access the capabilities of the service hidden behind a WSS
     * 
     * @return the address
     */
    public OnlineResource getSecuredServiceAddress() {
        return securedServiceAddress;
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
     * Returns the maximum lifetime of a session in milliseconds.
     * @return the lifetime
     */
    public int getSessionLifetime() {
        return sessionLifetime;
    }
    
    /**
     * @return an object with database connection information
     */
    public JDBCConnection getDatabaseConnection() {
        return databaseConnection;
    }
    
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WSSDeegreeParams.java,v $
 * Changes to this class. What the people have been up to: Revision 1.11  2006/10/17 20:31:20  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/08/24 06:42:17  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/07/12 16:59:32  poth
 * Changes to this class. What the people have been up to: required adaptions according to renaming of OnLineResource to OnlineResource
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/07/07 15:03:03  schmitz
 * Changes to this class. What the people have been up to: Fixed a few warnings.
 * Changes to this class. What the people have been up to: Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/06/23 10:23:50  schmitz
 * Changes to this class. What the people have been up to: Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/06/20 15:31:05  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.4 2006/06/09 12:58:32 schmitz Changes
 * to this class. What the people have been up to: Set up some tests for WAS/WSS and the URN class.
 * Changes to this class. What the people have been up to: Commented out some of the deegree param
 * stuff in order for the Changes to this class. What the people have been up to: tests to run.
 * Changes to this class. What the people have been up to: Tests have hardcoded URLs in them, so
 * they won't run just anywhere. Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.3 2006/06/06 15:28:05 bezema Changes to
 * this class. What the people have been up to: Added a "null" prefix check in xmltools so that it,
 * added a characterset to the deegreeparams and the WSS::DoService class is almost done Changes to
 * this class. What the people have been up to: Changes to this class. What the people have been up
 * to: Revision 1.2 2006/05/30 10:12:02 bezema Changes to this class. What the people have been up
 * to: Putting the cvs asci option to -kkv which will update the $revision$ $author$ and $date$
 * variables in a cvs commit Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58 bezema Changes to this
 * class. What the people have been up to: Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future. Changes to this class. What the
 * people have been up to: Changes to this class. What the people have been up to: Revision 1.1
 * 2006/05/23 15:22:02 bezema Changes to this class. What the people have been up to: Added
 * configuration files to the wss and wss is able to parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/
