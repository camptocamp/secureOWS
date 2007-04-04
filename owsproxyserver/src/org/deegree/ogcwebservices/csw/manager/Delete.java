//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/manager/Delete.java,v 1.7 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.ogcwebservices.csw.manager;

import java.net.URI;

import org.deegree.model.filterencoding.Filter;

/**
 * A Delete object constains a constraint that defines a set of records that are to be deleted from
 * the catalogue. A constraint must be specified in order to prevent every record in the catalogue
 * from inadvertently being deleted.
 * 
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/08/29 19:54:14 $
 * 
 * @since 2.0
 */
public class Delete extends Operation {

    private URI typeName = null;

    private Filter constraint = null;

    /**
     * 
     * @param handle
     * @param typeName
     * @param filter
     */
    public Delete( String handle, URI typeName, Filter constraint ) {
        super( "Delete", handle );
        this.typeName = typeName;
        this.constraint = constraint;
    }

    /**
     * The number of records affected by a delete action is determined by the contents of the
     * constraint.
     * 
     * @return
     */
    public Filter getConstraint() {
        return constraint;
    }

    /**
     * The typeName attribute is used to specify the collection name from which records will be
     * deleted.
     * 
     * @return
     */
    public URI getTypeName() {
        return typeName;
    }

}

/* ************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: Delete.java,v $
 * Revision 1.7  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.6  2006/07/21 15:37:38  poth
 * footer corrected
 *
 * Revision 1.5  2006/04/06 20:25:27  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.4  2006/04/04 20:39:43  poth
 * *** empty log message ***
 * Revision 1.3 2006/03/30 21:20:26
 * poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/20 09:30:18 poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/17 16:28:12 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 *********************************************************************************************** */