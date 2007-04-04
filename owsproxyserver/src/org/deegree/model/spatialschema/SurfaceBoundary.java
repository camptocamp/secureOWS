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

/** 
*
* Defining the boundary of a surface. The surface boundary is defined as
* ring surounding the exterior boundary of the surface and the rings
* surounding each interior ring of the surface.
*
* <p>-----------------------------------------------------</p>
*
* @author Axel Schaefer
* @version $Revision: 1.5 $ $Date: 2006/07/12 14:46:15 $
* <p>
*/ 

public interface SurfaceBoundary extends PrimitiveBoundary {
	/**
     * @clientCardinality 0..* 
     */    
    
    /* as
     * SurfaceBoundary::exterior[0,1] : Ring;
     * SurfaceBoundary::interior[0..*] : Ring;
     */
     
    /*
     * A SurfaceBoundary consists of some number of Rings,
     * corresponding to the various components of its boundary.
     * In the normal 2D case, one of these rings is distinguished
     * as being the exterior boundary.
     * In a general manifold this is not always possible, in which case
     * all boundaries shall be listed as interior boundaries, and the
     * exterior will be empty.
     */
    
	/*
	 * get the exterior ring
	 */	    
    public Ring getExteriorRing();
    	
	/*
	 * gets the interior ring(s)
	 */              
    public Ring[] getInteriorRings();
		
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SurfaceBoundary.java,v $
Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
