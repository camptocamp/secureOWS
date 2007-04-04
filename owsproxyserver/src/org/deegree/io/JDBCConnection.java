//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/JDBCConnection.java,v 1.8 2006/08/24 06:40:05 poth Exp $
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
package org.deegree.io;

/**
 * Class representation for an element of type "deegreejdbc:JDBCConnectionType" as defined in
 * datastore_configuration.xsd.
 *
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author last edited by: $Author: poth $
 *
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/08/24 06:40:05 $
 *
 * @since 2.0
 *
 * @TODO Change the type name!
 */
public class JDBCConnection {

    private String driver;

    private String url;

    private String user;

    private String password;

    private String securityConstraints;

    private String encoding;

    private String aliasPrefix;

    private String sdeDatabase;

    private String sdeVersion;

    /**
     *
     * @param driver
     *            JDBC driver
     * @param url
     *            JDBC connection string
     * @param user
     *            user name
     * @param password
     *            users password
     * @param securityConstraints
     *            constraints to consider (not implemented yet)
     * @param encoding
     *            encoding to be used for connection
     * @param aliasPrefix ?
     */
    public JDBCConnection( String driver, String url, String user, String password,
                          String securityConstraints, String encoding, String aliasPrefix ) {
        this( driver, url, user, password, securityConstraints, encoding, aliasPrefix, (String) null, (String) null );
    }

    /**
     *
     * @param driver
     *            JDBC driver
     * @param url
     *            JDBC connection string
     * @param user
     *            user name
     * @param password
     *            users password
     * @param securityConstraints
     *            constraints to consider (not implemented yet)
     * @param encoding
     *            encoding to be used for connection
     * @param aliasPrefix ?
     */
    public JDBCConnection( String driver, String url, String user, String password,
                          String securityConstraints, String encoding, String aliasPrefix,
                          String sdeDatabase, String sdeVersion ) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.securityConstraints = securityConstraints;
        this.encoding = encoding;
        this.aliasPrefix = aliasPrefix;
        this.sdeDatabase = sdeDatabase;
        this.sdeVersion = sdeVersion;
    }

    public String getDriver() {
        return driver;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getAliasPrefix() {
        return aliasPrefix;
    }

    public String getURL() {
        return url;
    }

    public String getPassword() {
        return password;
    }

    public String getSecurityConstraints() {
        return securityConstraints;
    }

    public String getUser() {
        return user;
    }

    public String getSDEDatabase() {
        return sdeDatabase;
    }

    public String getSDEVersion() {
        return sdeVersion;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj
     *            the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    public boolean equals( Object o ) {
        if ( !( o instanceof JDBCConnection ) ) {
            return false;
        }
        JDBCConnection that = (JDBCConnection) o;
        if ( !this.driver.equals( that.driver ) ) {
            return false;
        }
        if ( !this.url.equals( that.url ) ) {
            return false;
        }
        if ( !this.user.equals( that.user ) ) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "Driver: '" );
        sb.append( this.driver );
        sb.append( "', URL: '" );
        sb.append( this.url );
        sb.append( "', User: '" );
        sb.append( this.user );
        return sb.toString();
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: JDBCConnection.java,v $
 * Revision 1.8  2006/08/24 06:40:05  poth
 * File header corrected
 *
 * Revision 1.7  2006/05/21 19:13:03  poth
 * several changes required by implemented SDEDatastore / adapted to ArcSDE 9 java API
 *
 * Revision 1.2  2006/05/09 14:51:04  polli
 * SDE parameters added
 *
 * Revision 1.1.1.1  2006/04/12 20:37:06  polli
 * no message
 *
 * Revision 1.6  2006/04/06 20:25:31  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/04 20:39:44  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:28  poth
 * *** empty log message ***
 *
 * Revision 1.3  2005/11/16 13:45:01  mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.1.2.1  2005/11/09 08:00:50  mschneider
 * More refactoring of 'org.deegree.io.datastore'.
 * Revision
 * 1.1 2005/10/07 10:30:41 poth no message
 *
 **************************************************************************************************/
