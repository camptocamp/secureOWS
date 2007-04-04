//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/schema/MultiGeometryPropertyType.java,v 1.7 2006/08/24 06:40:27 poth Exp $
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
 * Representation of property types that contain aggregated spatial data types.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/08/24 06:40:27 $
 */
public class MultiGeometryPropertyType extends ComplexPropertyType {

    /**
     * Constructs a new instance of <code>MultiGeometryPropertyType</code> from the given
     * parameters.
     * 
     * @param name
     * @param typeName
     * @param type
     * @param minOccurs
     * @param maxOccurs
     */
    public MultiGeometryPropertyType( QualifiedName name, QualifiedName typeName, int type,
                                     int minOccurs, int maxOccurs ) {
        super( name, typeName, type, minOccurs, maxOccurs );
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MultiGeometryPropertyType.java,v $
Revision 1.7  2006/08/24 06:40:27  poth
File header corrected

Revision 1.6  2006/08/21 15:48:58  mschneider
Javadoc improvements.

Revision 1.5  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
