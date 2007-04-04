//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/schema/PropertyType.java,v 1.5 2006/08/24 06:40:27 poth Exp $
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
 * Represents a property type in a GML feature type definition.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/08/24 06:40:27 $
 * 
 * @since 2.0
 */
public interface PropertyType {

    /**
     * Returns the name of the property.
     * 
     * @return the name of the property
     */
    public QualifiedName getName();

    /**
     * Returns the code of the data type that the property contains.
     * 
     * @return the code of the data type that the property contains
     */
    public int getType();

    /**
     * Returns the minimum number of occurrences of the property within a feature. The method
     * returns 0 if the property is nillable.
     * 
     * @return minimum number of occurrences of the property, 0 if the property is nillable
     */
    int getMinOccurs();

    /**
     * Returns the maximum number of occurrences of the property within a feature. The method
     * returns -1 if the number of occurences is unbounded.
     * 
     * @return maximum number of occurrences of the property, -1 if the number of occurences is
     *         unbounded
     */
    int getMaxOccurs();

}
/*
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: PropertyType.java,v $
 * Revision 1.5  2006/08/24 06:40:27  poth
 * File header corrected
 *
 * Revision 1.4  2006/04/06 20:25:21  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/04/04 20:39:40  poth
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/30 21:20:23  poth
 * *** empty log message ***
 *
 * Revision 1.1  2006/01/31 16:25:55  mschneider
 * Changes due to refactoring of org.deegree.model.feature package.
 *
 * Revision 1.2  2005/11/16 13:44:59  mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.1.2.3  2005/11/14 00:54:40  mschneider
 * MappedPropertyType -> PropertyType.
 * Revision 1.1.2.1 2005/11/09 08:00:50 mschneider More
 * refactoring of 'org.deegree.io.datastore'.
 * 
 * Revision 1.1.2.1 2005/11/07 11:19:09 deshmukh Refactoring of 'createPropertyType()' methods in
 * FeatureFactory.
 * 
 * Revision 1.5 2005/08/30 13:40:03 poth no message
 * 
 * Revision 1.4 2005/07/11 14:42:01 poth no message
 * 
 * Revision 1.3 2005/06/16 08:27:31 poth no message
 * 
 * Revision 1.2 2005/01/20 11:11:00 poth no message
 * 
 * Revision 1.1 2004/05/22 09:55:58 ap no message
 * 
 * Revision 1.2 2004/02/09 07:57:01 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:47 poth no message
 * 
 * Revision 1.2 2002/08/15 10:02:25 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 * 
 * Revision 1.4 2002/03/04 10:20:31 ap no message
 * 
 * Revision 1.3 2001/10/23 13:41:52 ap no message
 * 
 * Revision 1.2 2001/10/15 14:48:19 ap no message
 * 
 * Revision 1.1 2001/10/05 15:19:43 ap no message
 * 
 */
