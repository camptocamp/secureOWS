//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/ImageToTriangleListFactory.java,v 1.9 2006/11/27 09:07:52 poth Exp $
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
 * Factory idiom for creating a List of triangles from an image and a region.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class ImageToTriangleListFactory {
//
//
//    /**
//     * Creates a triangle list from a BufferedImage covering a region defined
//     * by the Surface <code>surf</code>. The triangulaton is defined below:<br/>
//     * 	<pre>			
//		3 -- 26
//		|  // |
//		| //  |
//		14 -- 5
//	 	<pre><br/>
//	 * Each pixel in the image defines two tringles.
//	 * Points 1, 2 and 3, define a triangle (note the surface orientation, pointing upwards).
//	 * Points 4,5,6 define the other triangle. Note that pont 1 and 4, and point 2 and 6
//	 * are coincident.
//     * 
//     * @param image the image from which elevatin values are extracted 
//     * @param surf a Surface defining the region covered by image
//     * @return a new List of tringles. Each triangle is given by 
//     */
//    public static List<float[][]> createTriangleList( BufferedImage image, Surface surf ){
//    	
//    	int w = image.getWidth();
//    	int h = image.getHeight();
//    	
//    	int size = image.getRaster().getDataBuffer().getSize();
//    	DataBuffer db = image.getRaster().getDataBuffer();
//        
//    	List<float[][]> triangleList = new ArrayList<float[][]>( 2 * size );
//    	
//    	Envelope env = surf.getEnvelope();
//    	int x0 = (int)env.getMin().getX();
//
//    	int xn = (int)env.getMax().getX();
//    	int yn = (int)env.getMax().getY();
//    	
//    	float res = (xn-x0)/(float)(w-1);
//
//    	int count = 0;
//    	for (int j = 0; j < h-1; j++) {
//    		for (int i = 0; i < w-1; i++) {                
//				float z = db.getElemFloat( count );
//                
//				float z1 = db.getElemFloat( count + w );
//				float z2 = db.getElemFloat( count + 1);
//				float z5 = z;
//				if( (count + w + 1) < h*w){
//					z5 = db.getElemFloat( count + w + 1);
//				}
//				
//				float y3 = yn - j*res;
//				float y5 = yn -(j+1)*res;
//				
//				float x14 = x0+i*res;
//				float y14 = yn - (j+1)*res;
//				float x26 = x0+(i+1)*res;
//				float y26 = yn - j*res;
//		
//				triangleList.add( 
//					GeometryUtils.createTriangle(
//            			x14, y14, z1,            // 1
//            			x26, y26, z2,            // 2
//            			x0+i*res, y3, z ) );     // 3 
//				
//            	triangleList.add( 
//        			GeometryUtils.createTriangle(
//            			x14, y14, z1,            // 4
//            			x0+(i+1)*res, y5, z5,    // 5
//            			x26, y26, z2 ) );        // 6
//
//            	count++;        
//            	if ( i == w-2 ){
//            		count++;
//            	}
//            }
//    	}
//
//    	return triangleList;
//    }
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ImageToTriangleListFactory.java,v $
Revision 1.9  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.8  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.7  2006/07/18 15:16:03  taddei
changes in DEM (WCS) geometry

Revision 1.6  2006/07/03 06:40:13  poth
*** empty log message ***

Revision 1.5  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.4  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.2  2006/03/17 13:33:30  taddei
dgm/wcs working; triangulation

Revision 1.1  2006/03/16 11:39:42  taddei
new class to triangulate  images; and convenience methods too


********************************************************************** */