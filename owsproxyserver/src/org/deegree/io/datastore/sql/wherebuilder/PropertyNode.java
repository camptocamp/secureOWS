//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/PropertyNode.java,v 1.7 2006/08/28 16:38:59 mschneider Exp $
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
package org.deegree.io.datastore.sql.wherebuilder;

import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.TableRelation;

/**
 * Represents a {@link MappedPropertyType} as a node in a {@link QueryTableTree}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/08/28 16:38:59 $
 */
public interface PropertyNode {

    /**
     * Returns the {@link MappedPropertyType} that this node represents.
     * 
     * @return the MappedPropertyType that this node represents
     */
    MappedPropertyType getProperty();

    /**
     * Returns the parent feature type node.
     * 
     * @return the parent feature type node
     */
    FeatureTypeNode getParent();

    /**
     * Returns the table relations that lead from the parent feature type node's table to the table
     * associated with this property.
     * 
     * @return the table relations that lead from the parent feature type node's table
     */
    TableRelation[] getPathFromParent();

    /**
     * Returns the aliases for the target tables in the table relations.
     * 
     * @return the aliases for the target tables
     */
    String[] getTableAliases();

    /**
     * Returns an indented string representation of the object.
     * 
     * @param indent
     *            current indentation (contains spaces to be prepended)
     * @return an indented string representation of the object
     */
    String toString( String indent );
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: PropertyNode.java,v $
 * Revision 1.7  2006/08/28 16:38:59  mschneider
 * Javadoc fixes.
 *
 * Revision 1.6  2006/08/24 06:40:05  poth
 * File header corrected
 *
 * Revision 1.5  2006/05/21 19:09:02  poth
 * several methods set to public; required by SDE datastore
 *
 ************************************************************************************************* */