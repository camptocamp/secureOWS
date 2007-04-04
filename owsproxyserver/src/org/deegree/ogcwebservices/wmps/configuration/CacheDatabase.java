//$ Header: Exp $
/*---------------- FILE HEADER ------------------------------------------
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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Dr. Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
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

//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/configuration/CacheDatabase.java,v 1.3 2006/08/24 06:42:17 poth Exp $
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

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 Germany
 E-Mail: fitzke@lat-lon.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wmps.configuration;

/**
 * This class is a container for the database used to cache the asynchronous request.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */

public class CacheDatabase {

    private String driver;

    private String url;

    private String user;

    private String password;

    /**
     * Create a new CacheDatabase instance.
     * 
     * @param driver
     * @param url
     * @param user
     * @param password
     */
    public CacheDatabase( String driver, String url, String user, String password ) {

        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * @return Returns the driver.
     */
    public String getDriver() {
        return this.driver;
    }

    /**
     * @param driver
     *            The driver to set.
     */
    public void setDriver( String driver ) {
        this.driver = driver;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url
     *            The url to set.
     */
    public void setUrl( String url ) {
        this.url = url;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return this.user;
    }

    /**
     * @param user
     *            The user to set.
     */
    public void setUser( String user ) {
        this.user = user;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: CacheDatabase.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/08/24 06:42:17  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/08/10 07:11:35  deshmukh
 * Changes to this class. What the people have been up to: WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.1 2006/07/31 11:21:07 deshmukh Changes to
 * this class. What the people have been up to: wmps implemention... Changes to this class. What the
 * people have been up to:
 **************************************************************************************************/
