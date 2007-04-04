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

package org.deegree.io.shpapi;

/**
 * Class containing all constants needed for reading of a shape file <BR>
 * 
 * @version 14.12.1999
 * @author Andreas Poth
 *  
 */
public class ShapeConst {

    /**
     * The length of a shape file record header in bytes. (8)
     */
    public static final int SHAPE_FILE_RECORD_HEADER_LENGTH = 8;

    /**
     * The length of a shape file header in bytes. (100)
     */
    public static final int SHAPE_FILE_HEADER_LENGTH = 100;

    /**
     * A Shape File's magic number.
     */
    public static final int SHAPE_FILE_CODE = 9994;

    /**
     * The currently handled version of Shape Files.
     */
    public static final int SHAPE_FILE_VERSION = 1000;

    /**
     * The indicator for a null shape type. (0)
     */
    public static final int SHAPE_TYPE_NULL = 0;

    /**
     * The indicator for a point shape type. (1)
     */
    public static final int SHAPE_TYPE_POINT = 1;

    /**
     * The indicator for an polyline shape type. (3)
     */
    public static final int SHAPE_TYPE_POLYLINE = 3;

    /**
     * The indicator for a polygon shape type. (5)
     */
    public static final int SHAPE_TYPE_POLYGON = 5;

    /**
     * The indicator for a multipoint shape type. (8)
     */
    public static final int SHAPE_TYPE_MULTIPOINT = 8;
    
    /** 
     * The indicator for a polygonz shape type. (15) 
     */
    public static final int SHAPE_TYPE_POLYGONZ = 15;

    /**
     * start point of field parts in ESRI shape record
     */
    public static final int PARTS_START = 44;

}
/*
 * Last changes: $Log: ShapeConst.java,v $
 * Last changes: Revision 1.7  2006/07/12 14:46:14  poth
 * Last changes: comment footer added
 * Last changes:
 * Last changes: Revision 1.6  2006/06/05 15:21:53  poth
 * Last changes: support for polygonz type added
 * Last changes:
 * Last changes: Revision 1.5  2006/04/06 20:25:23  poth
 * Last changes: *** empty log message ***
 * Last changes:
 * Last changes: Revision 1.4  2006/04/04 20:39:41  poth
 * Last changes: *** empty log message ***
 * Last changes:
 * Last changes: Revision 1.3  2006/03/30 21:20:24  poth
 * Last changes: *** empty log message ***
 * Last changes:
 * Last changes: Revision 1.2  2005/04/25 14:15:06  friebe
 * Last changes: *** empty log message ***
 * Last changes: 
 * 21.12.1999 ap: all constants declared 
 *  
 */

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShapeConst.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
