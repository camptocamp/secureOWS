//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/PropertyPathStep.java,v 1.9 2006/04/06 20:25:22 poth Exp $
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
package org.deegree.ogcbase;

import org.deegree.datatypes.QualifiedName;

/**
 * Represents one step in a <code>PropertyPath</code>.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/04/06 20:25:22 $
 * 
 * @since 2.0
 * @see PropertyPath
 */
public abstract class PropertyPathStep {

    protected QualifiedName propertyName;

    /**
     * Creates a new instance of <code>PropertyPathStep</code> that selects the property with the
     * given name.
     * 
     * @param propertyName
     */
    PropertyPathStep( QualifiedName propertyName ) {
        this.propertyName = propertyName;
    }

    /**
     * Returns the name of the selected property.
     * 
     * @return the name of the property
     */
    public QualifiedName getPropertyName() {
        return this.propertyName;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PropertyPathStep.java,v $
Revision 1.9  2006/04/06 20:25:22  poth
*** empty log message ***

Revision 1.8  2006/04/04 20:39:40  poth
*** empty log message ***

Revision 1.7  2006/04/04 10:34:10  mschneider
Added handling of attributes to PropertyPaths.

Revision 1.6  2006/03/30 21:20:24  poth
*** empty log message ***

Revision 1.5  2006/03/29 14:56:31  mschneider
Fixed header.

********************************************************************** */