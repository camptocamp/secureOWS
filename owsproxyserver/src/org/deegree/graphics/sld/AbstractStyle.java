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
Aennchenstr. 19
53115 Bonn
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
package org.deegree.graphics.sld;

import org.deegree.framework.xml.Marshallable;



/**
 * 
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.7 $ $Date: 2006/09/15 09:17:33 $
 */
public abstract class AbstractStyle implements Marshallable {
    
    protected String name = null;

    /**
     * Creates a new AbstractStyle object.
     *
     * @param name 
     */
    AbstractStyle( String name ) {
        this.name = name;
    }

    /**
     * The given Name is equivalent to the name of a WMS named style and is used
     * to reference the style externally when an SLD is used in library mode and
     * identifies the named style to redefine when an SLD is inserted into a WMS.
     * @return the name
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute's value of the AbstractStyle.
     * @param name the name of the style
     * <p>
     * 
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractStyle.java,v $
Revision 1.7  2006/09/15 09:17:33  schmitz
Implementing Marshallable, since all subclasses to it...

Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
