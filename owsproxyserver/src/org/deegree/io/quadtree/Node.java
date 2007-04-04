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
package org.deegree.io.quadtree;

import java.util.List;

import org.deegree.model.spatialschema.Envelope;

interface Node {

    public abstract String getId();

    /**
     * inserts a new item into the quadtree
     * @param item
     * @param envelope
     */
    public abstract void insert( Object item, Envelope itemEnv )
                            throws IndexException;

    /**
     * returns a List containing all items whose envelope intersects 
     * with the passed one
     * 
     * @param envelope
     * @return
     */
    public abstract List query( Envelope searchEnv, List visitor, int level )
                            throws IndexException;

    /**
     * deletes a specific item from the tree (not the item itself will be deleted,
     * just its reference will be)
     * 
     * @param item
     */
    public abstract void deleteItem( Object item );

    /**
     * deletes all references of items whose envelope intersects
     * with the passed one ( @see #deleteItem(Object) )
     * @param envelope
     */
    public abstract void deleteRange( Envelope envelope );

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Node.java,v $
 Revision 1.12  2006/10/20 07:56:00  poth
 core methods extracted to interfaces


 ********************************************************************** */