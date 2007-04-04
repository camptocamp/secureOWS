// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/ShapeResolution.java,v 1.7 2006/07/12 14:46:19 poth Exp $
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
package org.deegree.ogcwebservices.wcs.configuration;

/**
 * modls a <tt>Resolution<tT> by describing the assigned coverages through * a Shapefile containing name an boundingbox of each available file *  * @version $Revision: 1.7 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.7 $, $Date: 2006/07/12 14:46:19 $ *  * @since 2.0
 */

public class ShapeResolution extends AbstractResolution {


    private Shape shape = null;
  
    /**
     * @param minScale
     * @param maxScale
     * @param ranges
     * @param shape
     * @throws IllegalArgumentException
     */
    public ShapeResolution(double minScale, double maxScale, Range[] ranges, 
                           Shape shape) throws IllegalArgumentException {
        super(minScale, maxScale, ranges);
        this.shape = shape;
    }

    /**
     * @return Returns the shape.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * @param shape The shape to set.
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShapeResolution.java,v $
Revision 1.7  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
