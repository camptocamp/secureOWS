//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/SimplePropertyNode.java,v 1.7 2006/08/28 16:38:59 mschneider Exp $
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

import org.deegree.io.datastore.schema.MappedSimplePropertyType;

/**
 * Represents a {@link MappedSimplePropertyType} as a node in a {@link QueryTableTree}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/08/28 16:38:59 $
 */
public class SimplePropertyNode extends AbstractPropertyNode {

    /**
     * Creates a new <code>SimplePropertyNode</code> instance from the given parameters.
     * 
     * @param property
     *            the property that this node represents in the query tree
     * @param parent
     *            the parent feature type node
     * @param tableAliases
     *            the aliases for the tables that lead from the parent feature type node's table to
     *            the table where the property's value is stored
     */
    public SimplePropertyNode( MappedSimplePropertyType property, FeatureTypeNode parent,
                       String[] tableAliases ) {
        super( property, parent, tableAliases );
    }

    @Override
    public String toString( String indent ) {
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "+ " );
        sb.append( this.getProperty().getName() );
        sb.append( " (SimplePropertyNode" );
        if ( this.getTableAliases() != null ) {
            for (int i = 0; i < this.getTableAliases().length; i++) {
                sb.append( " [" );
                sb.append( getPathFromParent()[i] );
                sb.append( " target alias: '" );
                sb.append( this.getTableAliases()[i] );
                sb.append( "'" );
                if ( i != this.getTableAliases().length - 1 ) {
                    sb.append( ", " );
                } else {
                    sb.append( "]" );
                }
            }
        }
        sb.append( ")\n" );
        return sb.toString();
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: SimplePropertyNode.java,v $
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