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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.deegree.framework.util.ObjectPool;

/**
 * class to manage a pool of database connections.
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version 07.02.2001
 */
public class DBPool extends ObjectPool {

    private String driver = null;

    private String database = null;

    private Properties properties = new Properties();

    // private constructor to protect initializing
    public DBPool(final String driver, final String database,
            final String user, final String password) {

        this.driver = driver;
        this.database = database;
        properties.put("user", user);
        properties.put("password", password);
    }

    // private constructor to protect initializing
    public DBPool(final String driver, final String database,
            final Properties properties) {

        this.driver = driver;
        this.database = database;
        this.properties = properties;
    }

    /**
     * get an object from the object pool
     */
    public synchronized Object acquireObject() throws DBPoolException {
        try {
            // if the maximum amount of instances are in use
            // wait until an instance has been released back
            // to the pool or 20 seconds has passed
            long timediff = 0;
            while (in_use.size() == getMaxInstances() && timediff < 20000) {
                Thread.sleep(100);
                timediff += 100;
            }
            // if no instance has been released within 20 seconds
            // or can newly be instantiated return null
            if (timediff >= 20000)
                return null;

            // if a none used is available from the pool
            if (available.size() > 0) {

                // get/remove ojebct from the pool
                Object o = available.remove(available.size() - 1);
                if (((Connection) o).isClosed()) {
                    o = acquireObject();
                }

                // add it to 'in use' container
                in_use.add(o);

                // reset its start life time
                startLifeTime.put(o, new Long(System.currentTimeMillis()));
                // set the start of its usage
                startUsageTime.put(o, new Long(System.currentTimeMillis()));

                // return the object
                return o;

            }
            // else instatiate a new object
            // create a new class instance
            DriverManager.registerDriver((Driver) Class.forName(driver)
                    .newInstance());

            Properties prop = (Properties) properties.clone();
            Object connection = DriverManager.getConnection(database, prop);

            existingInstances++;

            // add it to 'in use' container
            in_use.add(connection);
            // set the start of its life time
            startLifeTime.put(connection, new Long(System.currentTimeMillis()));
            // set the start of its usage
            startUsageTime
                    .put(connection, new Long(System.currentTimeMillis()));
            // return the object
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBPoolException("Error while acquiring connection: "
                    + e.getMessage(), e);
        }
    }

    /**
     * will be called when the object is removed from the pool
     */
    public void onObjectKill(Object o) {
        try {
            ((Connection) o).close();
        } catch (SQLException e) {
        }
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBPool.java,v $
Revision 1.7  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
