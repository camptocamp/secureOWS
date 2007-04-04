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
 * Defining the surface geometry of the iso geometry model. a surface is made of 1..n surface
 * patches. for convention it is defined that Surface is a closed geometry. that means each surface
 * patch a surface is made of must touch at least one other surface patch if a surface is made of
 * more then one surface patch
 * 
 * <p>
 * -----------------------------------------------------
 * </p>
 * 
 * @author Andreas Poth
 * @author last edited by: $Author: bezema $
 * @version $Revision: 1.7 $ $Date: 2006/10/02 07:32:16 $
 *          
 */

public interface Surface extends OrientableSurface, GenericSurface {

    /**
     * @return the number of patches building the surface
     */
    int getNumberOfSurfacePatches();

    /**
     * @param index
     *            the index to look for.
     * @return the surface patch at the submitted index.
     * @throws GeometryException
     *             if the Index is negativ.
     */
    SurfacePatch getSurfacePatchAt( int index )
                            throws GeometryException;

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: Surface.java,v $
 * Changes to this class. What the people have been up to: Revision 1.7  2006/10/02 07:32:16  bezema
 * Changes to this class. What the people have been up to: formattted and documentation
 * Changes to this class. What the people have been up to: Revision 1.6
 * 2006/07/12 14:46:15 poth comment footer added
 * 
 **************************************************************************************************/
