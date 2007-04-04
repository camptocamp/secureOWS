//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/FeatureTypeNode.java,v 1.8 2006/08/28 16:38:59 mschneider Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedPropertyType;

/**
 * Represents a {@link MappedFeatureType} as a node in a {@link QueryTableTree}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/08/28 16:38:59 $
 */
class FeatureTypeNode {

    // associated MappedFeatureType instance (contains the table name)
    private MappedFeatureType featureType;

    // unique alias for the table
    private String tableAlias;

    private Map<MappedPropertyType, List<PropertyNode>> propertyMap = new HashMap<MappedPropertyType, List<PropertyNode>>();

    /**
     * Creates a new <code>FeatureTypeNode</code> from the given parameters.
     * 
     * @param featureType
     * @param tableAlias
     */
    FeatureTypeNode( MappedFeatureType featureType, String tableAlias ) {
        this.featureType = featureType;
        this.tableAlias = tableAlias;
    }

    /**
     * Returns the associated {@link MappedFeatureType}.
     * 
     * @return associated MappedFeatureType
     */
    public MappedFeatureType getFeatureType() {
        return this.featureType;
    }

    /**
     * Returns the name of the associated table.
     * 
     * @return the name of the associated table
     */
    public String getTable() {
        return this.featureType.getTable();
    }

    /**
     * Returns the alias that uniquely identifies the table (in an SQL query).
     * 
     * @return the unique alias for the table
     */
    public String getAlias() {
        return this.tableAlias;
    }

    /**
     * Returns all child {@link PropertyNode}s.
     * 
     * @return all child PropertyNodes
     */
    public PropertyNode[] getPropertyNodes() {
        List<PropertyNode> propertyNodeList = new ArrayList<PropertyNode>();
        Iterator iter = this.propertyMap.values().iterator();
        while ( iter.hasNext() ) {
            Iterator iter2 = ( (List) iter.next() ).iterator();
            while ( iter2.hasNext() ) {
                propertyNodeList.add( (PropertyNode) iter2.next() );
            }
        }
        return propertyNodeList.toArray( new PropertyNode[propertyNodeList.size()] );
    }

    /**
     * Returns the child {@link PropertyNode}s with the given type.
     * 
     * @param type
     *            the property type to look up
     * @return the child PropertyNode for the given property, may be null
     */
    public PropertyNode getPropertyNode( MappedPropertyType type ) {
        PropertyNode propertyNode = null;
        List propertyNodeList = this.propertyMap.get( type );
        if ( propertyNodeList != null ) {
            Iterator propertyNodeIter = propertyNodeList.iterator();
            boolean found = false;
            while ( propertyNodeIter.hasNext() ) {
                propertyNode = (PropertyNode) propertyNodeIter.next();
                if ( propertyNode.getProperty() == type ) {
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                propertyNode = null;
            }
        }
        return propertyNode;
    }

    /**
     * Adds the given property node as a child.
     * 
     * @param propertyNode
     *            the child node to add
     */
    public void addPropertyNode( PropertyNode propertyNode ) {
        List<PropertyNode> propertyNodeList = this.propertyMap.get( propertyNode.getProperty() );
        if ( propertyNodeList == null ) {
            propertyNodeList= new ArrayList<PropertyNode>();
            this.propertyMap.put( propertyNode.getProperty(), propertyNodeList );
        }
        propertyNodeList.add( propertyNode );
    }

    /**
     * Returns an indented string representation of the object.
     * 
     * @return an indented string representation of the object
     */
    String toString( String indent ) {
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "- " );
        sb.append( this.featureType.getName() );
        sb.append( " (FeatureTypeNode, table: '" );
        sb.append( this.featureType.getTable() );
        sb.append( "', alias: '" );
        sb.append( this.tableAlias );
        sb.append( "')\n" );
        Iterator iter = this.propertyMap.values().iterator();
        while ( iter.hasNext() ) {
            List propertyNodeList = (List) iter.next();
            Iterator iter2 = propertyNodeList.iterator();
            while ( iter2.hasNext() ) {
                PropertyNode propertyNode = (PropertyNode) iter2.next();
                sb.append( propertyNode.toString( indent + "  " ) );
            }
        }
        return sb.toString();
    }    
    
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || ( !( obj instanceof FeatureTypeNode ) ) ) {
            return false;
        }
        FeatureTypeNode that = (FeatureTypeNode) obj;
        if ( this.getTable().equals( that.getTable() ) && this.tableAlias.equals( that.tableAlias ) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.tableAlias.hashCode();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FeatureTypeNode.java,v $
 Revision 1.8  2006/08/28 16:38:59  mschneider
 Javadoc fixes.

 Revision 1.7  2006/08/24 06:40:05  poth
 File header corrected

 Revision 1.6  2006/08/06 20:50:08  poth
 unneccessary type cast removed

 Revision 1.5  2006/06/01 12:18:31  mschneider
 Fixed footer. Added use of Generics.

 ********************************************************************** */