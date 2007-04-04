//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/GeometryUtils.java,v 1.23 2006/11/27 09:07:52 poth Exp $
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
 * Class contaning a number of utility methods for geomtry objects
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.23 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
//public class GeometryUtils {
//
//    private static ILogger LOG = LoggerFactory.getLogger( GeometryUtils.class );
//
//    public static final int[] RESOLUTION_VALUES = { 1024, 512, 256, 128, 64 };
//
//    private static final List<Integer> RESOLUTION_LIST;
//
//    static {
//
//        RESOLUTION_LIST = new ArrayList<Integer>( 5 );
//        RESOLUTION_LIST.add( 1024 );
//        RESOLUTION_LIST.add( 512 );
//        RESOLUTION_LIST.add( 256 );
//        RESOLUTION_LIST.add( 128 );
//        RESOLUTION_LIST.add( 64 );
//
//    }
//
//    private GeometryUtils() {
//        // never instatiate
//    }

//    /**
//     * Creates a <code>List</code> of points from all four corners of a given
//     * <code>GM_Envelope</code>.
//     * 
//     * @param env
//     *            the envelope from which the corner points will be extracted
//     * @param altitudeValue
//     *            the altitude value for the points
//     * @return a <code>List</code> containing four <code>GM_Point</code>s
//     */
//    public static final List<Point> createEnvelopePoints( Envelope env, double altitudeValue ) {
//
//        List<Point> envPoints = new ArrayList<Point>( 4 );
//        Position maxPos = env.getMax();
//        Position minPos = env.getMin();
//
//        envPoints.add( GeometryFactory.createPoint( maxPos.getX(), maxPos.getY(), altitudeValue,
//                                                    null ) );
//        envPoints.add( GeometryFactory.createPoint( minPos.getX(), minPos.getY(), altitudeValue,
//                                                    null ) );
//        envPoints.add( GeometryFactory.createPoint( maxPos.getX(), minPos.getY(), altitudeValue,
//                                                    null ) );
//        envPoints.add( GeometryFactory.createPoint( minPos.getX(), maxPos.getY(), altitudeValue,
//                                                    null ) );
//
//        return envPoints;
//    }

//    /**
//     * Creates a <code>Envelope</code> defining the whole scene region. This region also covers
//     * areas that are not really seen
//     * 
//     * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
//     * @return an <code>Envelope</code> defining the whole scene region
//     */
//    public static Envelope createMainEnvelope( /*Surface[]*/ ArrayList<ResolutionStripe> bboxes ) {
//
//        double minX = Double.POSITIVE_INFINITY;
//        double minY = Double.POSITIVE_INFINITY;
//        double maxX = Double.NEGATIVE_INFINITY;
//        double maxY = Double.NEGATIVE_INFINITY;
//
//        //for ( int i = 0; i < bboxes.length; i++ ) {
//        for( ResolutionStripe stripe : bboxes ){
//            Envelope e = stripe.getSurface().getEnvelope();
//            Position p = e.getMin();
//            minX = Math.min( minX, p.getX() );
//            minY = Math.min( minY, p.getY() );
//            p = e.getMax();
//            maxX = Math.max( maxX, p.getX() );
//            maxY = Math.max( maxY, p.getY() );
//        }
//
//        return new EnvelopeImpl( GeometryFactory.createPosition( minX, minY ),
//                                 GeometryFactory.createPosition( maxX, maxY ) );
//    }

//    /**
//     * Creates a single geometry by calling union on surfaces[i].union(surfaces[i+1])
//     * 
//     * @param surfaces
//     *            the surface to be united. Cannot be null or have length = 1
//     * @return a new geometry representing the union of all surfaces
//     */
//    public static final Geometry createUnion( Surface[] surfaces ) {
//        if ( surfaces == null || surfaces.length < 1 ) {
//            throw new IllegalArgumentException( "Surface array cannot be null and must "
//                                                + "have length >= 1. Length = " + surfaces.length );
//        }
//        Geometry surf = surfaces[0];
//        for ( int i = 1; i < surfaces.length; i++ ) {
//            surf = surf.union( surfaces[i] );
//        }
//        return surf;
//    }

//    /**
//     * Calculates the resolution based on the surface size and the dimensions (with, height) of the
//     * WTS request image
//     * 
//     * @param surf
//     *            the surface defining the region
//     * @param reqImgSize
//     *            the size in pixel of the surface
//     * @param crs
//     *            the coordinate system
//     * @return the resolution
//     */
//    public static final double calcResolution( Surface surf, int reqImgSize, CoordinateSystem crs ) {
//
//        /*
//         * float f = (float)(surf.getEnvelope().getWidth() * surf.getEnvelope().getHeight()) /(width *
//         * height);
//         */
//
//        int size = getImageSizeForSurface( (RankedSurface) surf, reqImgSize );
//
//        final double pxSize = 0.00028;
//        double s = 1;
//        try {
//            s = MapUtils.calcScale( size, size, surf.getEnvelope(), crs, pxSize );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//        return Math.sqrt( s );
//    }

//    /**
//     * Gets the optimum image size for a ranked surface.
//     * 
//     * @param surf
//     *            the ranked surface
//     * @param reqImgSize
//     *            the image size as calculated according to the request's HEIGHT and WIDTH
//     * @return a new image size (a power of 2 and between 128 and 1024)
//     */
//    public static int getImageSizeForSurface( RankedSurface surf, int reqImgSize ) {
//
//        int actualRank = RESOLUTION_LIST.indexOf( reqImgSize ) + surf.getRank();
//
//        // not using thisd: img resolution at the back of view is very bad...
//        int res = ( actualRank >= RESOLUTION_VALUES.length ) ? RESOLUTION_VALUES[RESOLUTION_VALUES.length - 1]
//                                                            : RESOLUTION_VALUES[actualRank];
//
//        return res;
//    }

//    /**
//     * Subidivides an envelope into 4, and return a List containing them
//     * 
//     * @param envelope
//     *            the envelope to be divided
//     * @return a List conatining 4 smaller envelopes
//     */
//    public static ArrayList<Envelope> subdivideEnvelope( Envelope envelope ) {
//
//        // subdivision is always by 4, so no need to go looping about N tiles
//
//        Envelope[] envelopes = new Envelope[4];
//
//        double w = envelope.getWidth();
//        double h = envelope.getHeight();
//        Position p = envelope.getMin();
//        double minX = p.getX();
//        double minY = p.getY();
//        p = envelope.getMax();
//
//        /*
//         * new envelopes C D A B
//         */
//        envelopes[0] = GeometryFactory.createEnvelope( minX, minY, minX + w / 2d, minY + h / 2d,
//                                                       null );
//        envelopes[1] = GeometryFactory.createEnvelope( minX + w / 2d, minY, minX + w,
//                                                       minY + h / 2d, null );
//
//        envelopes[2] = GeometryFactory.createEnvelope( minX, minY + h / 2d, minX + w / 2d,
//                                                       minY + h, null );
//        envelopes[3] = GeometryFactory.createEnvelope( minX + w / 2d, minY + h / 2d, minX + w, minY
//                                                                                               + h,
//                                                       null );
//
//        ArrayList<Envelope> envList = new ArrayList<Envelope>( 4 );
//        for ( int i = 0; i < envelopes.length; i++ ) {
//            envList.add( envelopes[i] );
//        }
//
//        return envList;
//    }

//    /**
//     * Convenience method to create a Surface from an Envelope
//     * 
//     * @param e
//     *            the envelope to be transfromed into a Surface
//     * @return a new Surface, comering the same area as the envelope
//     */
//    public static Surface toSurface( Envelope e ) {
//        Surface envGeom = null;
//        try {
//            envGeom = GeometryFactory.createSurface( e, null );
//        } catch ( GeometryException e1 ) {
//            e1.printStackTrace();
//        }
//        return envGeom;
//    }

//    /**
//     * Ensures that a given Envelope env has equal sides. If the input Envelope is not square it
//     * will have one if its side be enlarged till it is square. The bottom left corner stays put.
//     * 
//     * @param env
//     * @return a new square Envelope, or the same, if it were already a square
//     */
//    public static final Envelope ensureSquareEnvelope( Envelope env ) {
//
//        double w = env.getWidth();
//        double h = env.getHeight();
//        if ( h == w ) {
//            // is already a square
//            return env;
//        }
//
//        double largeDist = h >= w ? h : w;
//
//        return GeometryFactory.createEnvelope( env.getMin().getX(), env.getMin().getY(),
//                                               env.getMin().getX() + largeDist, env.getMin().getY()
//                                                                                + largeDist, null );
//    }

//    /**
//     * Creates an float[3][3], that is float[ number_of_points ][ coord_index ], array containing
//     * vertices of a triangle defined by x0, y0, z0, x1, etc..., z2. Note that number_of_points is
//     * always 3 (it is a <b>tri</b>angle ;-), and valid coord_indices are 0 (for x), 1 (for y) and
//     * 2 (for z).
//     * 
//     * @param x0
//     * @param y0
//     * @param z0
//     * @param x1
//     * @param y1
//     * @param z1
//     * @param x2
//     * @param y2
//     * @param z2
//     * @return
//     */
//    public static final float[][] createTriangle( float x0, float y0, float z0, float x1, float y1,
//                                                 float z1, float x2, float y2, float z2 ) {
//
//        return new float[][] { { x0, y0, z0 }, { x1, y1, z1 }, { x2, y2, z2 } };
//    }

//    /**
//     * Creates two triangles that cover the same area defined by the envelope. Height values are
//     * given by <code>altitude</code>
//     * 
//     * @param envelope
//     *            teh envelope defining (x,y) corner coordinates
//     * @param altitude
//     *            the value for z.
//     * @return
//     */
//    public static List<float[][]> createTrianglePairFromEnvelope( Envelope envelope, float altitude ) {
//
//        double[] values = envelope.getMin().getAsArray();
//        float minX = (float) values[0];
//        float minY = (float) values[1];
//        values = envelope.getMax().getAsArray();
//        float maxX = (float) values[0];
//        float maxY = (float) values[1];
//
//        List<float[][]> triList = new ArrayList<float[][]>( 2 );
//        triList.add( createTriangle( minX, minY, altitude, maxX, minY, altitude, maxX, maxY,
//                                     altitude ) );
//
//        triList.add( createTriangle( minX, minY, altitude, maxX, maxY, altitude, minX, maxY,
//                                     altitude ) );
//
//        return triList;
//    }

//    /**
//     * Makes RankedSurfaces out of the boxes given as a parameter. Stripes are used to determine the
//     * rank of the surfaces. The lower the stripe index, the higher the rank of the surface, if it
//     * intersects the stripe.
//     * 
//     * @param boxes
//     *            the surfaces to be ranked
//     * @param stripes
//     *            the stripes defining resolution areas
//     * @return a new array of RankedSurfaces
//     */
//    public static Surface[] createRankedSurfaces( Surface[] boxes, Surface[] stripes ) {
//
//        RankedSurface[] rs = new RankedSurface[boxes.length];
//
//        for ( int i = 0; i < boxes.length; i++ ) {
//            int rank = stripes.length - 1;
//            for ( int j = stripes.length - 1; j >= 0; j-- ) {
//                if ( boxes[i].intersects( stripes[j] ) ) {
//                    rank = j;
//                }
//            }
//            try {
//                rs[i] = new RankedSurface( boxes[i], rank );
//
//            } catch ( GeometryException e ) {
//                e.printStackTrace();
//            }
//        }
//
//        return rs;
//    }

//    /**
//     * Checks if the validArea intersects with the visible area
//     * 
//     * @param validArea
//     *            the valid area (of a given datasource)
//     * @param getViewHandler
//     *            the handler in which the visible area is defined
//     * @return true if the datasource valid area intersects the visible area
//     */
//    public static boolean isValidArea( Geometry validArea, Geometry visibleArea ) {
//
//        if ( validArea != null ) {
//            try {
//
//                    Geometry geom = visibleArea;
//
//                // TODO use commons' bbox definition, and then can do reqCRS.getName()
//                // String reqCRSName = reqCRS.getName()
//                // meanwhile
//                String reqCRSName = visibleArea.getCoordinateSystem().getAsString();
//
//                if ( !reqCRSName.equalsIgnoreCase( validArea.getCoordinateSystem().getName() ) ) {
//
//                    // if requested CRS is not identical to the CRS of the valid area
//                    // a transformation must be performed before intersection can
//                    // be checked
//                    GeoTransformer gt = new GeoTransformer( validArea.getCoordinateSystem() );
//                    geom = gt.transform( geom );
//                }
//
//                return geom.intersects( validArea );
//            } catch ( Exception e ) {
//                // should never happen
//                e.printStackTrace();
//                LOG.logError( "could not validate WPVS datasource area", e );
//            }
//        }
//        return true;
//    }

//}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GeometryUtils.java,v $
 * Changes to this class. What the people have been up to: Revision 1.23  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.22  2006/11/23 11:48:01  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision
 * 1.18 2006/07/04 09:03:18 bezema Exception message updated
 * 
 * Revision 1.17 2006/06/20 10:16:01 taddei clean up and javadoc
 * 
 * Revision 1.16 2006/06/20 07:46:28 taddei improved error mesg
 * 
 * Revision 1.15 2006/05/01 20:15:27 poth ** empty log message ***
 * 
 * Revision 1.14 2006/04/26 12:14:42 taddei fiddle with getImageSizeforSurfaced
 * 
 * Revision 1.13 2006/04/06 20:25:25 poth ** empty log message ***
 * 
 * Revision 1.12 2006/04/05 09:01:28 taddei added code for computing res of different surfs
 * 
 * Revision 1.10 2006/03/16 11:39:42 taddei new class to triangulate images; and convenience methods
 * too
 * 
 * Revision 1.9 2006/03/10 10:33:07 taddei changes regarding cood sys and scale calculation; javadoc
 * added
 * 
 * Revision 1.8 2006/03/07 08:49:01 taddei made list type safe
 * 
 * Revision 1.7 2006/03/02 15:27:11 taddei �
 * 
 * Revision 1.6 2006/02/21 14:04:44 taddei javadoc, added better positioning for background
 * 
 * Revision 1.5 2006/02/21 12:58:53 taddei buffering envelope to improve image quality
 * 
 * Revision 1.4 2006/02/17 15:40:39 taddei ** empty log message ***
 * 
 * Revision 1.3 2006/02/14 15:18:08 taddei added createUnion(), removed createTrapeze (was !nice)
 * 
 * Revision 1.2 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.1 2006/01/26 14:31:56 taddei added Geometry/Envelope utilities class
 * 
 * 
 **************************************************************************************************/