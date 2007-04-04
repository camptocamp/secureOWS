//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/shape/ShapeDatastoreConfiguration.java,v 1.1 2006/09/26 16:40:44 mschneider Exp $
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
package org.deegree.io.datastore.shape;

import java.net.URL;

import org.deegree.io.datastore.DatastoreConfiguration;

/**
 * Represents the configuration for a {@link ShapeDatastore} instance.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.1 $, $Date: 2006/09/26 16:40:44 $
 */
public class ShapeDatastoreConfiguration extends DatastoreConfiguration {

    private URL file;

    /**
     * Creates a new instance of <code>ShapeDatastoreConfiguration</code> from the given
     * parameters.
     * 
     * @param type
     * @param datastoreClass
     * @param file
     */
    public ShapeDatastoreConfiguration( String type, Class datastoreClass, URL file ) {
        super( type, datastoreClass );
        this.file = file;
    }

    /**
     * Returns the file that this datastore operates upon.
     * 
     * @return the file that this datastore operates upon.
     */
    public URL getFile() {
        return this.file;
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
        sb.append( this.file );
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
        if ( !( obj instanceof ShapeDatastoreConfiguration ) ) {
            return false;
        }
        ShapeDatastoreConfiguration that = (ShapeDatastoreConfiguration) obj;
        if ( !this.getType().equals( that.getType() ) ) {
            return false;
        }
        if ( !this.file.equals( that.file ) ) {
            return false;
        }
        return true;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShapeDatastoreConfiguration.java,v $
Revision 1.1  2006/09/26 16:40:44  mschneider
Former FileBasedDatastoreConfiguration.

Revision 1.8  2006/08/24 06:40:05  poth
File header corrected

Revision 1.7  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */