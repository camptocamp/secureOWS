//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/ElevationModelFactory.java,v 1.6 2006/11/27 09:07:52 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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

package org.deegree.ogcwebservices.wpvs.j3d;


/**
 * This class is used to construct Shapes3D, which represent digital elevation models in the deegree
 *  Web Perspective View Service
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class ElevationModelFactory {
//
//    /**
//     * Creates an array (len = 0) of Java 3D shapes. 
//     * @param triangleList the List containing individual triangles, which will be triangulated by this method.
//     * @param masterEnvelope the envelope coverign the whole area (defined by all surfaces). This envelope is used in scale texture coordinates.
//     * @param boxes the boxes to be used for the individual TextureUnitStates 
//     * @param scale the value by which elevation data will be scaled (for vertical exaggeration)
//     * @param texImages image used for multiple texturing
//     * @return a new Shape3D[1] (with an instance of a TriangleArray). 
//     * @throws IllegalArgumentException if number of boxes is not equal to number if texture images
//     */
//    public Shape3D[] createElevationModel( List<float[][]> triangleList,
//                                               Envelope masterEnvelope, /*Surface[]*/ ArrayList<ResolutionStripe> boxes,
//                                               float scale, BufferedImage[] texImages ){
//        
//        if( boxes != null && texImages != null && boxes.size() != texImages.length ){
//            throw new IllegalArgumentException( "number of surfaces must be equal to the number of images.");
//        }
//        
//        /*GeometryArray geom = TINtoTINArrayConverter.createGeometryArray( triangleList,
//                                                                         masterEnvelope, boxes,
//                                                                         scale );
//        */
//        GeometryArray geom = null;
//        Shape3D tinObject = new Shape3D();
//        tinObject.setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
//        tinObject.setGeometry( geom );
//        
//        Appearance app = AppearanceFactory.createAppearance( texImages );
//        tinObject.setAppearance( app );
//
//        return new Shape3D[] { tinObject };
//    }

//    /**
//     * Creates an array (len = triangleStripList.size() ) of Java 3D shapes.
//     * @param triangleStripList the list of TriangleStripHelpers
//     * @param texImages the images used for texturing the individual GeometryArrays
//     * @return an array Shape3D[ triangleStripList.size()  ] of TriangleStripArrays
//     * @throws IllegalArgumentException if size of triangleStripList is not equal to number if texture images
// 
//     */
//    public Shape3D[] createElevationModel( List<TriangleStripHelper> triangleStripList, 
//                                           BufferedImage[] texImages ) {
//        
//        if( triangleStripList != null && texImages != null && triangleStripList.size() != texImages.length ){
//            throw new IllegalArgumentException( "Size of triangleStripList must be equal to the number of images.");
//        }
//        
//        BufferedImage[] bis = new BufferedImage[ texImages.length ];
//        for ( int i = 0; i < bis.length; i++ ) {
//            bis[ i ] = texImages[ bis.length - 1 - i ];
//        }
//        
//        ImageToGeoArrayConverter.TriangleStripHelper[] tt = 
//            new ImageToGeoArrayConverter.TriangleStripHelper[ triangleStripList.size() ];
//        TriangleStripHelper[] triHelpers = triangleStripList.toArray( tt );
//        
//        Shape3D[] tinObjects = new Shape3D[ triHelpers.length ];
//        
//        for ( int i = 0; i < triHelpers.length; i++ ) {
//            tinObjects[i] = new Shape3D();
//            GeometryArray geom = new ImageToGeoArrayConverter().createTriangleStripArray( triHelpers[i] );
//            
//            tinObjects[i].setGeometry( geom );
//            Appearance app = AppearanceFactory.createAppearance( new BufferedImage[]{bis[i]} );
//            tinObjects[i].setAppearance( app );
//                            
//        }
//        
//        return tinObjects;
//    }
    
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ElevationModelFactory.java,v $
Revision 1.6  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.5  2006/11/23 11:47:21  bezema
The initial version of the new wpvs

Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/08/23 06:51:42  poth
code formating

Revision 1.1  2006/07/18 15:14:45  taddei
changes in DEM (WCS) geometry


********************************************************************** */