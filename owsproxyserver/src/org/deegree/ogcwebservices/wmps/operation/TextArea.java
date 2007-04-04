//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/operation/TextArea.java,v 1.10 2006/08/24 06:42:17 poth Exp $
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

package org.deegree.ogcwebservices.wmps.operation;

import java.io.Serializable;

/**
 * Encapsulates the Text Details for Map output entered via the PrintMap post request.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.10 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */

public class TextArea implements Serializable {

    private static final long serialVersionUID = 5578369335351213505L;

    private String name;

    private String text;

    /**
     * Creates a new TextArea instance
     * 
     * @param name
     * @param text
     */
    public TextArea( String name, String text ) {
        this.name = name;
        this.text = text;
    }

    /**
     * Get the TextArea name.
     * 
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the text entered in the text area.
     * 
     * @return String
     */
    public String getText() {
        return this.text;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TextArea.java,v $
Revision 1.10  2006/08/24 06:42:17  poth
File header corrected

Revision 1.9  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
