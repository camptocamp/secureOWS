//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/RankedSurface.java,v 1.5 2006/11/27 09:07:52 poth Exp $
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

package org.deegree.ogcwebservices.wpvs.util;


/**
 * A RankedSurface encapsulates a Surface and gives it a rank. The closer to the observer, the 
 * higher the rank should be.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class RankedSurface extends SurfaceImpl {
//
//	/**
//     * 
//     */
//    private static final long serialVersionUID = 9121065075577027037L;
//    private final int rank;
//	private final Surface surface;
//
//    /**
//     * Constructs a new RankedSurface. 
//     * @param s the surface to encapsulate
//     * @param r the ranke of the surface
//     * @throws GeometryException (I could say what it was, if it were javadocumented...)
//     */
//	public RankedSurface( Surface s, int r) throws GeometryException {
//		super( s.getSurfacePatchAt( 0 ) );
//		this.surface = s;
//		this.rank = r;
//	}
//
//	/**
//	 * @return the rank of this surface based on the distance to viewer
//	 */
//	public int getRank() {
//		return rank;
//	}
//
//	/**
//	 * @return the surface 
//	 */
//	public Surface getSurface() {
//		return surface;
//	}
//	
//	@Override
//	public String toString() {
//		
//		return "RankedSurface a = " + getArea() + ", rank = " + getRank();
//	}	
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RankedSurface.java,v $
Revision 1.5  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.4  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.3  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.2  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.1  2006/04/05 09:02:31  taddei
added code for computing res of different surfs


********************************************************************** */