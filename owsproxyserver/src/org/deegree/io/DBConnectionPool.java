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
package org.deegree.io;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * class to manage a database connection pool. this is part of the combination
 * of the object pool pattern an the singelton pattern.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version 07.02.2001
 */
public class DBConnectionPool {

    
    private static DBConnectionPool instance = null;

    private Map pools = null;

    /**
     * Creates a new DBConnectionPool object.
     */
    private DBConnectionPool() {
        pools = new HashMap();
    }

    /**
     * realize singelton pattern using double checked locking pattern.
     * 
     * @return an instance of the data base pool. it is gauranteed that there
     *         exists only one instance of pool for each submitted class name.
     * 
     */
    public static DBConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DBConnectionPool.class) {
                if (instance == null) {
                    instance = new DBConnectionPool();
                }
            }
        }

        return instance;
    }

    /**
     * get an object from the object pool
     */
    public synchronized Connection acquireConnection(final String driver,
            final String database, final String user, final String password)
            throws DBPoolException {
        String q = driver + database + user + password;
        DBPool pool = null;
        if (pools.get(q) == null) {
            pool = new DBPool(driver, database, user, password);
            pools.put(q, pool);
        } else {
            pool = (DBPool) pools.get(q);
        }
        return (Connection) pool.acquireObject();
    }

    /**
     * get an object from the object pool
     */
    public synchronized Connection acquireConnection(final String driver,
            final String database, final Properties properties)
            throws DBPoolException {
        String q = driver + database + properties.toString();

        if (pools.get(q) == null) {
            DBPool pool = new DBPool(driver, database, properties);
            pools.put(q, pool);
            return (Connection) pool.acquireObject();
        }
        DBPool pool = (DBPool) pools.get(q);
        return (Connection) pool.acquireObject();
    }

    /**
     * releases a connection back to the pool
     */
    public synchronized void releaseConnection(final Connection con,
            final String driver, final String database, final String user,
            final String password) throws DBPoolException {
        String q = driver + database + user + password;
        DBPool pool = (DBPool) pools.get(q);
        try {
            pool.releaseObject(con);
        } catch (Exception e) {
            throw new DBPoolException( "could not release connection", e );
        }
    }

    /**
     * releases a connection back to the pool
     */
    public synchronized void releaseConnection(final Connection con,
            final String driver, final String database,
            final Properties properties) throws Exception {
        String q = driver + database + properties.toString();
        DBPool pool = (DBPool) pools.get(q);
        pool.releaseObject(con);
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBConnectionPool.java,v $
Revision 1.12  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
