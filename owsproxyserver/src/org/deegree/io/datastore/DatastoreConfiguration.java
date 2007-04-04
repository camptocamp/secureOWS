//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/DatastoreConfiguration.java,v 1.19 2006/09/26 16:41:44 mschneider Exp $
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
package org.deegree.io.datastore;

/**
 * Represents the configuration for a mapping (persistence) backend.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.19 $, $Date: 2006/09/26 16:41:44 $
 */
public class DatastoreConfiguration {

    private String type;
    
    private Class datastoreClass;
    
    /**
     * Creates a new instance of <code>DatastoreConfiguration</code> from the given parameters.
     * 
     * @param type
     * @param datastoreClass
     */
    public DatastoreConfiguration( String type, Class datastoreClass ) {
        this.type = type;
        this.datastoreClass = datastoreClass;
    }

    /**
     * Returns the type code of the datastore backend.
     * 
     * @return the type code of the datastore backend.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the class of the datastore backend.
     * 
     * @return the class of the datastore backend.
     */
    public Class getDatastoreClass() {
        return this.datastoreClass;
    }
   
    /**
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hashtables such as those provided by <code>java.util.Hashtable</code>.
     * 
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return this.type.hashCode();
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
        if ( !( obj instanceof DatastoreConfiguration ) ) {
            return false;
        }
        DatastoreConfiguration that = (DatastoreConfiguration) obj;
        if ( !this.getType().equals( that.getType() ) ) {
            return false;
        }
        return true;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DatastoreConfiguration.java,v $
Revision 1.19  2006/09/26 16:41:44  mschneider
Javadoc corrections + fixed warnings.

Revision 1.18  2006/06/01 12:14:39  mschneider
Formatting corrected.

Revision 1.17  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.16  2006/04/04 20:39:44  poth
*** empty log message ***

Revision 1.15  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.14  2006/02/08 17:41:30  mschneider
Added handling of suppressXLinkOutput parameter.

********************************************************************** */