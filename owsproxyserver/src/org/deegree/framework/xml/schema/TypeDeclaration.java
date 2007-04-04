//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/xml/schema/TypeDeclaration.java,v 1.8 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.framework.xml.schema;

import org.deegree.datatypes.QualifiedName;

/**
 * Represents an XML type declaration in an {@link XMLSchema}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/08/29 19:54:14 $
 */
public interface TypeDeclaration {

    /**
     * Returns the qualified name of the declared XML type.
     *  
     * @return the qualified name of the declared XML type
     */
    public QualifiedName getName ();

    /**
     * Returns a string representation of the object (indented for better readablity,
     * as this is part of a hierarchical structure).
     * 
     * @param indent
     *             current indentation (as a whitespace string)
     * @return an indented string representation of the object
     */
    public String toString (String indent);
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TypeDeclaration.java,v $
Revision 1.8  2006/08/29 19:54:14  poth
footer corrected

Revision 1.7  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.

Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */