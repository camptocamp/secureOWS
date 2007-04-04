//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/schema/AbstractPropertyType.java,v 1.6 2006/08/24 06:40:27 poth Exp $
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
package org.deegree.model.feature.schema;

import org.deegree.datatypes.QualifiedName;

/**
 * Abstract base class for representation of property types in GML feature type definitions.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/08/24 06:40:27 $
 * 
 * @since 2.0
 */
public abstract class AbstractPropertyType implements PropertyType {

    private QualifiedName name;

    private int type;

    private int minOccurs;

    private int maxOccurs;

    AbstractPropertyType( QualifiedName name, int type, int minOccurs, int maxOccurs ) {
        this.name = name;
        this.type = type;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
    }

    /**
     * @return The name of the property (corresponds to it's element name).
     */
    public QualifiedName getName() {
        return this.name;
    }

    /**
     * @return The data type that the property holds.
     */
    public int getType() {
        return this.type;
    }

    /**
     * @return The minimum occurrences of the property within the surrounding feature. Returns '0'
     *         if the property is 'nillable'.
     */
    public final int getMinOccurs() {
        return this.minOccurs;
    }

    /**
     * @return The maximum occurrences of the property within the surrounding feature. Returns
     *         <code>Integer.MAX_VALUE</code> if the number of occurences is 'unbounded'.
     */
    public final int getMaxOccurs() {
        return this.maxOccurs;
    }

    /**
     * Returns if this <code>PropertyType</code> is equal to the given <code>PropertyType</code>.
     * 
     * @return true, if this PropertyType is equal to the given PropertyType, false otherwise
     */
    public boolean equals( PropertyType that ) {
        return that.getName().equals( this.getName() );
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractPropertyType.java,v $
Revision 1.6  2006/08/24 06:40:27  poth
File header corrected

Revision 1.5  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
