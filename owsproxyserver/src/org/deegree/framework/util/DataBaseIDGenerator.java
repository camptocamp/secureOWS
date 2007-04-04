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
package org.deegree.framework.util;

import java.sql.SQLException;

/**
 * This interface defines a generator for unique values for the primary key
 * in a database table.
 * <p>
 * NOTE: At the moment, every application has to take care of locking the table
 *       to prevent problems in multithreaded or multihosted applications. 
 * <p>
 * @author Markus Schneider <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.5 $ $Date: 2006/07/12 14:46:17 $
 */
public interface DataBaseIDGenerator {

	/**
	 * Generates a new Id, suitable as a primary key for the next dataset.
	 * <p>
	 * @return Id, the object type depends on the database field used as
	 *         primary key 
	 */
	public Object generateUniqueId () throws SQLException;
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DataBaseIDGenerator.java,v $
Revision 1.5  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
