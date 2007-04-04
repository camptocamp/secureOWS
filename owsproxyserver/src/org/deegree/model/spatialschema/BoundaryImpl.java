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
package org.deegree.model.spatialschema;

import java.io.Serializable;

import org.deegree.model.crs.CoordinateSystem;


/**
* default implementation of the Boundary interface. The class is
* abstract because there isn't a boundary without a geometry type.
* Concrete implementations are <tt>CurveBoundary</tt> or
* <tt>SurfaceBoundary</tt> for example.
* 
* <p>------------------------------------------------------------</p>
* @version 5.6.2001
* @author Andreas Poth
* <p>
*/
abstract class BoundaryImpl extends GeometryImpl implements Boundary, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -6057663115928108209L;

    /**
     * @param crs the reference system
     */
    public BoundaryImpl( CoordinateSystem crs ) {
        super( crs );
    }

    /** A geometric object, which has no boundary
     *    is a cycle.
     */
    public boolean isCycle() {
        return true;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: BoundaryImpl.java,v $
Revision 1.7  2006/09/19 07:20:03  bezema
changed srs in crs

Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
