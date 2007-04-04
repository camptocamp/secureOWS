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
package org.deegree.io.quadtree;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * access to (SQL) datatype mapping depending on Quadtree backend 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/20 07:56:00 $
 *
 * @since 2.0
 */
public class DBQuadtreeDataTypes {
    
    private static final String BUNDLE_NAME = "org.deegree.io.quadtree.quadtreedatatypes"; 

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

    private DBQuadtreeDataTypes() {
    }

    public static String getString( String key ) {
        // TODO Auto-generated method stub
        try {
            return RESOURCE_BUNDLE.getString( key );
        } catch ( MissingResourceException e ) {
            return '!' + key + '!';
        }
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBQuadtreeDataTypes.java,v $
Revision 1.1  2006/10/20 07:56:00  poth
core methods extracted to interfaces

Revision 1.3  2006/10/13 08:23:07  poth
class comment added

Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
