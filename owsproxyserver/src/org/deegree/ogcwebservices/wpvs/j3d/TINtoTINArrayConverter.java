//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/TINtoTINArrayConverter.java,v 1.10 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.ogcwebservices.wpvs.j3d;


/**
 * 
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @version $Revision: 1.10 $ $Date: 2006/11/27 09:07:52 $
 */
//public class TINtoTINArrayConverter {
//
//    private static final ILogger LOG = LoggerFactory.getLogger( TINtoTINArrayConverter.class );
//
//    /**
//     * Creates a Java3D <code>GeometryArray</code> from a list of triangles represeented by JTS
//     * <code>Polygon</code>s and using <code>scale</code> to vertically exaggerate the scene.
//     * 
//     * @param triangleCollection
//     *            a collection of triangles (internally represented by polygons.
//     * @param masterEnvelope
//     *            the Envelope envolving the whole visible area/trapeze
//     * @param surfaces
//     *            the surfaces used as tiles
//     * @param scale
//     *            the vertical exaggeration factor
//     * @return the GeometryArray representing the TIN (of a tile)
//     */
//    public static final GeometryArray createGeometryArray( List<float[][]> triangleCollection,
//                                                           Envelope env/*
//                                                                         * , Envelope
//                                                                         * masterEnvelope, ArrayList<ResolutionStripe>
//                                                                         * stripes, float scale
//                                                                         */ ) {
//
//        LOG.entering();
//
//        double width = env.getWidth();
//        double height = env.getHeight();
//        Position lowerLeft = env.getMin();
//
//        GeometryInfo geometryInfo = new GeometryInfo( GeometryInfo.TRIANGLE_ARRAY );
//        geometryInfo.setTextureCoordinateParams( 1, 2 );
//
//        Point3f[] coordinates = new Point3f[triangleCollection.size()];
//        TexCoord2f[] texCoords = new TexCoord2f[triangleCollection.size()];
//        
//        int coordNr = 0;
//        for( float[][] triangleCoords: triangleCollection ){
//            for ( int k = 0; k < 3; k++ ) {
//                Point3f modelCoordinate = new Point3f( triangleCoords[k][0], triangleCoords[k][1], triangleCoords[k][2] );
//                coordinates[coordNr] = modelCoordinate;
//                
//                double texCoordX = ( modelCoordinate.x - lowerLeft.getX()  ) / width;
//                
//                double texCoordY =  ( modelCoordinate.y - lowerLeft.getY()  ) / height;
//
//                texCoords[coordNr++] = new TexCoord2f( (float)texCoordX, (float)texCoordY );
//            }
//        }
//        geometryInfo.setCoordinates(coordinates);
//        geometryInfo.setTextureCoordinates(0,texCoords);
//        geometryInfo.recomputeIndices();
//        NormalGenerator ng = new NormalGenerator();
//        ng.generateNormals( geometryInfo );
//        
//        LOG.exiting();
//
//        return geometryInfo.getGeometryArray();
//
//    }
//
//    /**
//     * @param surfaces
//     * @return
//     */
//    public static double[][] createBoxParameters( ArrayList<ResolutionStripe>/* Surface[] */ surfaces ) {
//
//        final int numberOfTextures = surfaces.size();// * surfaces[0].length;
//
//        double[][] boxPars = new double[4][numberOfTextures];
//
//        int textCount = 0;
//
//        // for ( int i = surfaces.length - 1; i >= 0; i-- ) {
//        for( ResolutionStripe stripe : surfaces ){
//            Envelope gmEnv = stripe.getSurface().getEnvelope();
//
//            boxPars[0][textCount] = gmEnv.getMin().getX();
//            boxPars[1][textCount] = gmEnv.getMin().getY();
//            // width X
//            boxPars[2][textCount] = gmEnv.getMax().getX() - gmEnv.getMin().getX();
//            // width y
//            boxPars[3][textCount] = gmEnv.getMax().getY() - gmEnv.getMin().getY();
//
//            textCount++;
//        }
//
//        return boxPars;
//    }
//
//}
