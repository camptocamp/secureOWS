//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/DatastoreException.java,v 1.8 2006/11/09 17:35:36 mschneider Exp $
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
 * Base datastore exception class.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/11/09 17:35:36 $
 */
public class DatastoreException extends Exception {

    private static final long serialVersionUID = -5522925850825597636L;

    /**
     * Creates a new <code>DatastoreException</code> without detail message.  
     */
    public DatastoreException() {
        super();
    }

    /**
     * @param message
     */
    public DatastoreException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public DatastoreException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public DatastoreException( String message, Throwable cause ) {
        super( message, cause );
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DatastoreException.java,v $
Revision 1.8  2006/11/09 17:35:36  mschneider
Javadoc improvements.

Revision 1.7  2006/06/01 12:14:50  mschneider
Added javadoc.

********************************************************************** */