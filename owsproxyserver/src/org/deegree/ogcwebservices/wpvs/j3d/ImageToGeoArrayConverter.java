//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/ImageToGeoArrayConverter.java,v 1.8 2006/11/27 09:07:52 poth Exp $
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
 * Utility class to convert elevation data from an image into a Java3D objct.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class ImageToGeoArrayConverter {
//
//    /**
//     * Create a new GeometryArray from a TriangleStripHelper.
//     * 
//     * @param triStripHelper
//     *            the object containing data for creating a georeferenced GeometryArray
//     * @return a new TriangleStripArray
//     */
//    public GeometryArray createTriangleStripArray( TriangleStripHelper triStripHelper ) {
//
//        GeometryInfo gi = new GeometryInfo( GeometryInfo.TRIANGLE_STRIP_ARRAY );
//
//        gi.setStripCounts( triStripHelper.getStripCounts() );
//        gi.setCoordinates( triStripHelper.getCoordinates() );
//
//        float[] texCoords = new float[2 * triStripHelper.getCoordinates().length / 3];
//
//        gi.setTextureCoordinateParams( 1, 2 );
//        // gi.setTextureCoordinates( 0, triStripHelper.tex );
//        gi.setTextureCoordinates( 0, texCoords );
//
//        GeometryArray ga = gi.getGeometryArray();
//
//        NormalGenerator ng = new NormalGenerator();
//        ng.generateNormals( gi );
//        gi.recomputeIndices();
//
//        ga = gi.getGeometryArray();
////        setTextCoordsForGeoArray( ga, triStripHelper.getCoveredRegion().getEnvelope(),
////                                  new Surface[] { triStripHelper.getCoveredRegion() } );
//
//        return ga;
//    }
//
////    /**
////     * Convenience method for setting texture coordinates for a GeometryArray.
////     * 
////     * @param geoArray
////     *            the GeoArray for which texture coordinates will be calculated
////     * @param masterEnvelope
////     *            the envelope encompassing all other geometries or the envelope used to scale the
////     *            texture coordinates.
////     * @param surfaces
////     *            the surfaces used in calculationg the parameters for the individual
////     *            TextureUnitStates
////     */
////    private void setTextCoordsForGeoArray( GeometryArray geoArray, Envelope masterEnvelope,
////                                          Surface[] surfaces ) {
////
////        final int numberOfTextures = surfaces.length;// * surfaces[0].length;
////
////        int[] texCoordSetMap = new int[numberOfTextures];
////        for ( int i = 0; i < texCoordSetMap.length; i++ ) {
////            texCoordSetMap[i] = i;
////        }
////
////        // box parameters of whole TIN
////        double tinMinX = masterEnvelope.getMin().getX();
////
////        double tinMinY = masterEnvelope.getMin().getY();
////
////        // box pars for individual tiles
////        double[][] boxPars = TINtoTINArrayConverter.createBoxParameters( surfaces );
////
////        double[] tileWidth = boxPars[2];
////        double[] tileHeight = boxPars[3];
////
////        int textCount = 0;
////
////        float texCoord[] = new float[2];
////        int coordNr = 0;
////
////        int vertexCount = geoArray.getVertexCount();
////
////        // can't do this way, at least not yet
////        // geoArray.getCoordRefFloat();
////        float[] coords = new float[3];
////
////        for ( int i = 0; i < vertexCount; i++ ) {
////
////            textCount = 0;
////            for ( int j = 0; j < numberOfTextures; j++ ) {
////
////                geoArray.getCoordinate( i, coords );
////
////                texCoord[0] = (float) ( ( -coords[0] - tinMinX ) / tileWidth[textCount] );
////
////                // float deltaY = (float) ( ( tileMinY[textCount] - tinMinY ) /
////                // tileHeight[textCount] );
////                texCoord[1] = (float) ( ( ( coords[2] - tinMinY ) ) * ( 1 / tileHeight[textCount] ) );
////
////                geoArray.setTextureCoordinate( textCount, coordNr, texCoord );
////                textCount++;
////            }
////            coordNr++;
////        }
////    }
//
//    /**
//     * Creates a new TriangleStripArray.<br/> Point ordering is as follows:
//     * 
//     * <pre>
//     *   1 -- 3 --5 etc...
//     *   |  / |  /
//     *   | /  | /
//     *   2 -- 4 
//     * </pre>
//     * 
//     * @param image
//     *            the image containing elevation data
//     * @param surf
//     *            the surface defining the image area
//     * @param scale
//     *            the value by which elevation data will be scaled (for vertical exaggeration)
//     * @return a new TriangleStripHelper
//     */
//    public static synchronized TriangleStripHelper createTriangleStripArray( BufferedImage image,
//                                                                            Surface surf,
//                                                                            float scale ) {
//
//        int w = image.getWidth();
//        int h = image.getHeight();
//
//        // 3 values (x,y,z) * width * ( height + 2 vertices per pixel + 2 vertices...
//        // ...for first and last pixel
//        float[] coordinates = new float[3 * w * ( h * 2 + 2 )];
//
//        DataBuffer db = image.getRaster().getDataBuffer();
//
//        Envelope env = surf.getEnvelope();
//        int x0 = (int) env.getMin().getX();
//
//        int xn = (int) env.getMax().getX();
//        int yn = (int) env.getMax().getY();
//
//        // FIXME not quite sure whether should be - 1 here
//        // that is float res = ( xn - x0 ) / (float) ( w - 1 );
//        float res = ( xn - x0 ) / (float) w;
//
//        int[] stripCounts = new int[w];
//
//        for ( int i = 0; i < stripCounts.length; i++ ) {
//            // each strip has 2 vertices per pixel (2* h pixels) and
//            // one vertice for first pixel (+1) and one for last (+1)
//            stripCounts[i] = 2 * h + 2;
//        }
//
//        int count = 0;
//        int coordCount = 0;
//
//        for ( int j = 0; j < h; j++ ) {
//            for ( int i = 0; i < w; i++ ) {
//
//                float z = db.getElemFloat( count );
//
//                float z1 = z;
//                if ( ( count + w ) < h * w ) {
//                    z1 = db.getElemFloat( count + w );
//                }
//
//                float z2 = z;
//                if ( ( count + 1 ) < h * w ) {
//                    if ( i == h - 1 ) {
//                        // z2 = z;
//                    } else {
//                        z2 = db.getElemFloat( count + 1 );
//                    }
//                }
//
//                float y3 = yn - j * res;
//                float y5 = yn - ( j + 1 ) * res;
//
//                float x14 = x0 + i * res;
//                float y14 = yn - ( j + 1 ) * res;
//                float x26 = x0 + ( i + 1 ) * res;
//                float y26 = yn - j * res;
//
//                if ( i == 0 ) { // open strip
//                    coordinates[coordCount] = -( x0 + i * res ); // x
//                    coordinates[coordCount + 1] = z * scale; // z
//                    coordinates[coordCount + 2] = y3; // y
//                    coordCount += 3;
//                }
//
//                coordinates[coordCount] = -x14; // x
//                coordinates[coordCount + 1] = z1 * scale; // z
//                coordinates[coordCount + 2] = y14; // y
//                coordCount += 3;
//
//                coordinates[coordCount] = -x26; // x
//                coordinates[coordCount + 1] = z2 * scale; // z
//                coordinates[coordCount + 2] = y26; // y
//                coordCount += 3;
//
//                if ( i == w - 1 ) { // close strip
//
//                    int ix = ( count < h * w - 1 ) ? count + w : count;
//                    float z5 = db.getElemFloat( ix );
//
//                    coordinates[coordCount] = -( x0 + ( i + 1 ) * res ); // x
//                    coordinates[coordCount + 1] = z5 * scale; // z
//                    coordinates[coordCount + 2] = y5; // y
//                    coordCount += 3;
//                }
//
//                count++;
//            }
//        }
//
//        return new TriangleStripHelper( surf, coordinates, stripCounts, null );
//    }
//
//    public static TriangleStripHelper mergeTriangleStripHelpers( TriangleStripHelper[] helpers ) {
//
//        int stripCountLength = 0;
//        int coordCountLength = 0;
//
//        Envelope masterEnvelope = helpers[0].getCoveredRegion().getEnvelope();
//
//        // first count how many
//        for ( TriangleStripHelper helper : helpers ) {
//
//            stripCountLength += helper.getStripCounts().length;
//            coordCountLength += helper.getCoordinates().length;
//            masterEnvelope.expandToContain( helper.getCoveredRegion().getEnvelope() );
//        }
//
//        int[] mergedStripCounts = new int[stripCountLength];
//        float[] mergedCoordinates = new float[coordCountLength];
//
//        // then fill in the blanks
//        int stripCount = 0;
//        int coordCount = 0;
//        for ( TriangleStripHelper helper : helpers ) {
//            int[] helperStripCounts = helper.getStripCounts();
//            for ( int i : helperStripCounts ) {
//                mergedStripCounts[stripCount++] = i;
//            }
//
//            float[] helperCoords = helper.getCoordinates();
//            for ( float f : helperCoords ) {
//                mergedCoordinates[coordCount++] = f;
//            }
//        }
//
//        Surface superSurface = null;
//        try {
//            superSurface = GeometryFactory.createSurface( masterEnvelope, null );
//        } catch ( GeometryException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        TriangleStripHelper mergedHelper = new TriangleStripHelper( superSurface,
//                                                                    mergedCoordinates,
//                                                                    mergedStripCounts, null );
//
//        return mergedHelper;
//    }
//
//    /**
//     * 
//     * A convenience class holding minimal data for creating a Java3D TriangleStripArray
//     * 
//     * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
//     * @author last edited by: $Author: poth $
//     * 
//     * @version 2.0, $Revision: 1.8 $, $Date: 2006/11/27 09:07:52 $
//     * 
//     * @since 2.0
//     */
//    public static class TriangleStripHelper {
//
//        private final float[] coordinates;
//
//        private final int[] stripCounts;
//
//        private final Surface coveredRegion;
//
//        private final float[] texCoords;
//
//        /**
//         * Contructs a new TriangleStripHelper. Note that data are not checked for validity!
//         * 
//         * @param surf
//         *            the surface defining the valid area
//         * @param coords
//         *            the x,y,z coordinates
//         * @param stripCounts
//         *            the stripCounts (please refer to the Java3D documentation)
//         * @param tex
//         *            the texture coordinates (currently ignored)
//         */
//        public TriangleStripHelper( Surface surf, float[] coords, int[] stripCounts, float[] tex ) {
//            this.coveredRegion = surf;
//            this.coordinates = coords;
//            this.stripCounts = stripCounts;
//            this.texCoords = tex;
//        }
//
//        public int[] getStripCounts() {
//            return stripCounts;
//        }
//
//        public float[] getCoordinates() {
//            return coordinates;
//        }
//
//        public Surface getCoveredRegion() {
//            return coveredRegion;
//        }
//
//    }
//
//}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: ImageToGeoArrayConverter.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/11/23 11:47:21  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to:
 * Revision 1.4 2006/08/24 06:42:16 poth File header corrected
 * 
 * Revision 1.3 2006/08/23 06:51:42 poth code formating
 * 
 * Revision 1.2 2006/07/20 08:19:03 taddei some clean up
 * 
 * Revision 1.1 2006/07/18 15:14:45 taddei changes in DEM (WCS) geometry
 * 
 * 
 **************************************************************************************************/