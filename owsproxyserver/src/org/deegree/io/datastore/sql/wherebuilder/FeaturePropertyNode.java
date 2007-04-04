//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/FeaturePropertyNode.java,v 1.9 2006/08/28 16:38:59 mschneider Exp $$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.io.datastore.sql.wherebuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.deegree.io.datastore.schema.MappedFeaturePropertyType;

/**
 * Represents a {@link MappedFeaturePropertyType} as a node in a {@link QueryTableTree}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.9 $, $Date: 2006/08/28 16:38:59 $
 */
class FeaturePropertyNode extends AbstractPropertyNode {

    private Collection<FeatureTypeNode> children = new ArrayList<FeatureTypeNode> ();

    /**
     * Creates a new <code>FeaturePropertyNode</code> instance from the given parameters.
     * 
     * @param property
     *            the PropertyType that this FeaturePropertyNode represents
     * @param tableAliases
     *            the aliases for the tables that lead from the parent feature type node's table to
     *            the table where the property's value is stored
     * @param child
     *            the child of this node
     */
    FeaturePropertyNode( MappedFeaturePropertyType property, FeatureTypeNode parent,
                        String[] tableAliases, FeatureTypeNode child ) {
        super( property, parent, tableAliases );
        this.children.add (child);
    }

    /**
     * Returns the children of this <code>FeaturePropertyNode</code>.
     * 
     * @return the children of this FeaturePropertyNode.
     */
    public FeatureTypeNode[] getFeatureTypeNodes() {
        return this.children.toArray( new FeatureTypeNode[this.children.size()] );
    }    


    /**
     * Adds a new child to this <code>FeaturePropertyNode</code>.
     * 
     * @param newFeatureTypeNode child node to add
     */
    public void addFeatureTypeNode( FeatureTypeNode newFeatureTypeNode ) {
        this.children.add(newFeatureTypeNode);
    }    
    
    @Override
    public String toString( String indent ) {

        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "+ " );
        sb.append( this.getProperty().getName() );
        sb.append( " (FeaturePropertyNode" );
        if ( this.getTableAliases() != null ) {
            for (int i = 0; i < this.getTableAliases().length; i++) {
                sb.append( " [" );
                sb.append( getPathFromParent()[i] );
                sb.append( " target alias: '" );
                sb.append( getTableAliases()[i] );
                sb.append( "'" );
                if ( i != this.getTableAliases().length - 1 ) {
                    sb.append( ", " );
                } else {
                    sb.append( "]" );
                }
            }
        }
        sb.append( ")\n" );
        Iterator iter = this.children.iterator ();
        while (iter.hasNext()) {
            FeatureTypeNode child = (FeatureTypeNode) iter.next ();
            sb.append( child.toString( indent + "  " ) );    
        }        
        return sb.toString();
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeaturePropertyNode.java,v $
Revision 1.9  2006/08/28 16:38:59  mschneider
Javadoc fixes.

Revision 1.8  2006/08/24 06:40:05  poth
File header corrected

Revision 1.7  2006/08/06 20:50:08  poth
unneccessary type cast removed

Revision 1.6  2006/06/01 12:18:14  mschneider
Fixed header + footer. Added use of Generics.

********************************************************************** */