//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/SQLDatastoreConfiguration.java,v 1.9 2006/09/19 14:54:02 mschneider Exp $
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
package org.deegree.io.datastore.sql;

import org.deegree.io.JDBCConnection;
import org.deegree.io.datastore.DatastoreConfiguration;

/**
 * Represents the configuration for an SQL database which is used as a datastore backend.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.9 $, $Date: 2006/09/19 14:54:02 $
 */
public class SQLDatastoreConfiguration extends DatastoreConfiguration {

    private JDBCConnection connection;

    /**
     * Creates a new instance of <code>BackendConfiguration</code> from the given parameters.
     * 
     * @param type
     * @param datastoreClass
     * @param connection
     */
    public SQLDatastoreConfiguration( String type, Class datastoreClass, JDBCConnection connection ) {
        super( type, datastoreClass );
        this.connection = connection;
    }

    /**
     * Returns the JDBC connection information.
     * 
     * @return the JDBC connection information.
     */
    public JDBCConnection getJDBCConnection() {
        return this.connection;
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hashtables such as those provided by <code>java.util.Hashtable</code>.
     * 
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        sb.append( getType() );
        sb.append( connection );
        return sb.toString().hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj
     *            the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals( Object obj ) {
        if ( !( obj instanceof SQLDatastoreConfiguration ) ) {
            return false;
        }
        SQLDatastoreConfiguration that = (SQLDatastoreConfiguration) obj;
        if ( !this.getType().equals( that.getType() ) ) {
            return false;
        }
        if ( !this.connection.equals( that.connection ) ) {

            return false;
        }
        return true;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: SQLDatastoreConfiguration.java,v $
 * Revision 1.9  2006/09/19 14:54:02  mschneider
 * Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.
 *
 * Revision 1.8  2006/04/25 14:43:15  mschneider
 * Fixed header.
 *
 * Revision 1.7  2006/04/25 11:57:36  mschneider
 * Testing code formatter and cvs log.
 * 
 * Revision 1.6 2006/04/06 20:25:25 poth ** empty log message ***
 * 
 * Revision 1.5 2006/04/04 20:39:42 poth ** empty log message ***
 * 
 * Revision 1.4 2006/03/30 21:20:26 poth ** empty log message ***
 * 
 * Revision 1.3 2006/02/08 17:42:34 mschneider Fixed CVS keyword expansion mode.
 * 
 **************************************************************************************************/