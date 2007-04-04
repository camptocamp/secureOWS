//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/AbstractPropertyNode.java,v 1.8 2006/08/28 16:38:59 mschneider Exp $
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
 * Abstract base class for all representations of {@link MappedPropertyType}s in a
 * {@link QueryTableTree}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/08/28 16:38:59 $
 */
abstract class AbstractPropertyNode implements PropertyNode {

    private MappedPropertyType property;

    private FeatureTypeNode parent;

    private String[] tableAliases;

    /**
     * Creates a new <code>AbstractPropertyNode</code> instance from the given parameters.
     * 
     * @param property
     *            the property that this node represents in the query tree
     * @param parent
     *            the parent feature type node
     * @param tableAliases
     *            the aliases for the tables that lead from the parent feature type node's table to
     *            the table where the property's value is stored
     */
    AbstractPropertyNode( MappedPropertyType property, FeatureTypeNode parent, String[] tableAliases ) {
        this.property = property;
        this.parent = parent;
        this.tableAliases = tableAliases;
    }

    /**
     * Returns the <code>MappedSimplePropertyType</code> that this node represents.
     * 
     * @return the MappedSimplePropertyType that this node represents
     */
    public MappedPropertyType getProperty() {
        return this.property;
    }

    /**
     * Returns the parent feature type node.
     * 
     * @return the parent feature type node
     */
    public FeatureTypeNode getParent() {
        return this.parent;
    }

    /**
     * Returns the table relations that lead from the parent feature type node's table to the table
     * where this property's value is stored.
     * 
     * @return the table relations that lead from the parent feature type node's table
     */
    public TableRelation[] getPathFromParent() {
        return this.property.getTableRelations();
    }

    /**
     * Returns the aliases for the target tables in the table relations.
     * 
     * @return the aliases for the target tables
     */
    public String[] getTableAliases() {
        return this.tableAliases;
    }

    /**
     * Returns an indented string representation of the object.
     * 
     * @return an indented string representation of the object
     */
    public abstract String toString( String indent );
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractPropertyNode.java,v $
Revision 1.8  2006/08/28 16:38:59  mschneider
Javadoc fixes.

Revision 1.7  2006/08/24 06:40:05  poth
File header corrected

Revision 1.6  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.

Revision 1.5  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */