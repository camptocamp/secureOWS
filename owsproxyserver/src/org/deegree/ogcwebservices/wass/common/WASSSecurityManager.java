//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/WASSSecurityManager.java,v 1.8 2006/08/29 19:14:17 poth Exp $
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

package org.deegree.ogcwebservices.wass.common;

import java.util.Properties;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.JDBCConnection;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;

/**
 * This class will hold the SecurityAccessManager Instance and will be able to parse the
 * user/password key for the security database.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/08/29 19:14:17 $
 * 
 * @since 2.0
 */

public class WASSSecurityManager {

    private JDBCConnection databaseInfo = null;
    
    private SecurityAccessManager securityAccessManager = null;

    private static final ILogger LOG = LoggerFactory.getLogger( WASSSecurityManager.class );

    /**
     * This constructor initializes the connection to the security database.
     * 
     * @param dbInfo a database information object
     * 
     * @throws GeneralSecurityException
     */
    public WASSSecurityManager( JDBCConnection dbInfo ) throws GeneralSecurityException {
        databaseInfo = dbInfo;
        initializeSecurityAccessManager();
    }

    /**
     * Loads the deegree SecurityAccesManager if no instance is present jet.
     * 
     * @throws GeneralSecurityException
     *             if the no instance of the deegree securitymanager could be touched.
     */
    private void initializeSecurityAccessManager()
                            throws GeneralSecurityException {
        LOG.entering();
        if( databaseInfo == null ) {
            LOG.logError( Messages.getString( "ogcwebservices.wass.ERROR_SECURITYACCESSMANAGER_NO_DBINFO" ) );
            return;
        }
        Properties properties = new Properties();
        properties.setProperty( "driver", databaseInfo.getDriver() );
        properties.setProperty( "url", databaseInfo.getURL() );
        properties.setProperty( "user", databaseInfo.getUser() );
        properties.setProperty( "password", databaseInfo.getPassword() );
        try {
            securityAccessManager = SecurityAccessManager.getInstance();
        } catch ( GeneralSecurityException gse ) {
            try {
                SecurityAccessManager.initialize( "org.deegree.security.drm.SQLRegistry",
                                                  properties, 60 * 1000 );
                securityAccessManager = SecurityAccessManager.getInstance();
            } catch ( GeneralSecurityException gse2 ) {
                LOG.logError( Messages.getString( "ogcwebservices.wass.ERROR_SECURITYACCESSMANAGER" ) );
                LOG.logError( gse2.getLocalizedMessage(), gse2 );
                throw new GeneralSecurityException(
                                                   Messages.getString( "ogcwebservices.wass.ERROR_SECURITYACCESSMANAGER" ) );
            }
        }
        LOG.exiting();
    }

    /**
     * @return Returns the deegree securityAccessManager.
     * @throws GeneralSecurityException
     */
    public SecurityAccessManager getSecurityAccessManager()
                            throws GeneralSecurityException {
        LOG.entering();
        if ( securityAccessManager == null ) {
            throw new GeneralSecurityException(
                                                Messages.getString( "ogcwebservices.wass.ERROR_SECURITYACCESSMANAGER_NO_INIT" ) );
        }
        LOG.exiting();
        return securityAccessManager;
    }

}

/* *************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WASSSecurityManager.java,v $
 * Revision 1.8  2006/08/29 19:14:17  poth
 * code formating / footer correction
 *
 * Revision 1.7  2006/07/07 15:03:03  schmitz
 * Fixed a few warnings.
 * Added database options to WASS deegree params.
 * Changes to this class. What the people have been up to:
 * Revision 1.6  2006/06/23 13:53:47  schmitz
 * Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Revision 1.5 2006/06/19 12:47:26 schmitz Updated the documentation, fixed the warnings and
 * implemented logging everywhere.
 * 
 * Revision 1.4 2006/06/19 11:24:53 schmitz WAS and WSS operation tests are now completed and
 * running. Changes to this class. What the
 * people have been up to: Revision 1.3 2006/06/16 15:01:05 schmitz Changes to this class. What the
 * people have been up to: Fixed the WSS to work with all kinds of operation tests. It checks out
 * with both XML and KVP requests. Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.2 2006/06/13 15:16:18 bezema Changes to this class. What the people have been
 * up to: DoService Test seems to work Changes to this class. What the people have been up to:
 * Revision 1.1 2006/05/29 16:24:59 bezema
 * Rearranging the layout of the wss and
 * creating the doservice classes. The WSService class is implemented as well Changes to this class.
 * What the people have been up to:
 * 
 ************************************************************************************************ */
