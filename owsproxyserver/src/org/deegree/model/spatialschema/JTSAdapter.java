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
package org.deegree.model.spatialschema;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Adapter between deegree-<tt>Geometry</tt>s and JTS-<tt>Geometry<tt> objects.
 * <p>
 * Please note that the generated deegree-objects use null as
 * <tt>CS_CoordinateSystem</tt>!
 * <p>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.12 $ $Date: 2006/11/27 09:07:51 $
 */
public class JTSAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( JTSAdapter.class );

    // precision model that is used for all JTS-Geometries
    private static PrecisionModel pm = new PrecisionModel();

    // factory for creating JTS-Geometries
    private static com.vividsolutions.jts.geom.GeometryFactory jtsFactory = new com.vividsolutions.jts.geom.GeometryFactory(
                                                                                                                             pm,
                                                                                                                             0 );

    /**
     * Converts a <tt>Geometry</tt> to a corresponding JTS-<tt>Geometry</tt>
     * object.
     * <p>
     * Currently, the following conversions are supported:
     * <ul>
     * <li>Point -> Point
     * <li>MultiPoint -> MultiPoint
     * <li>Curve -> LineString
     * <li>MultiCurve ->  MultiLineString
     * <li>Surface -> Polygon
     * <li>MultiSurface -> MultiPolygon
     * <li>MultiPrimitive -> GeometryCollection
     * </ul>
     * <p>
     * @param gmObject the object to be converted
     * @return the corresponding JTS-<tt>Geometry</tt> object
     * @throws GeometryException if type unsupported or conversion failed
     */
    public static com.vividsolutions.jts.geom.Geometry export( Geometry gmObject )
                            throws GeometryException {

        com.vividsolutions.jts.geom.Geometry geometry = null;
        if ( gmObject instanceof Point ) {
            geometry = export( (Point) gmObject );
        } else if ( gmObject instanceof MultiPoint ) {
            geometry = export( (MultiPoint) gmObject );
        } else if ( gmObject instanceof Curve ) {
            geometry = export( (Curve) gmObject );
        } else if ( gmObject instanceof MultiCurve ) {
            geometry = export( (MultiCurve) gmObject );
        } else if ( gmObject instanceof Surface ) {
            geometry = export( (Surface) gmObject );
        } else if ( gmObject instanceof MultiSurface ) {
            geometry = export( (MultiSurface) gmObject );
        } else if ( gmObject instanceof MultiPrimitive ) {
            geometry = export( (MultiPrimitive) gmObject );
        } else {
            throw new GeometryException( "JTSAdapter.export does not support type '"
                                         + gmObject.getClass().getName() + "'!" );
        }
        return geometry;
    }

    /**
     * Converts a JTS-<tt>Geometry</tt> object to a corresponding
     * <tt>Geometry</tt>.
     * <p>
     * Currently, the following conversions are supported:
     * <ul>
     * <li>Point -> Point
     * <li>MultiPoint -> MultiPoint
     * <li>LineString -> Curve
     * <li>MultiLineString -> MultiCurve
     * <li>Polygon -> Surface
     * <li>MultiPolygon -> MultiSurface
     * <li>GeometryCollection -> MultiPrimitive
     * </ul>
     * <p>
     * @param geometry the JTS-<tt>Geometry</tt> to be converted
     * @return the corresponding <tt>Geometry</tt>
     * @throws GeometryException if type unsupported or conversion failed
     */
    public static Geometry wrap( com.vividsolutions.jts.geom.Geometry geometry )
                            throws GeometryException {

        Geometry gmObject = null;
        if ( geometry instanceof com.vividsolutions.jts.geom.Point ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.Point) geometry );
        } else if ( geometry instanceof com.vividsolutions.jts.geom.MultiPoint ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.MultiPoint) geometry );
        } else if ( geometry instanceof com.vividsolutions.jts.geom.LineString ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.LineString) geometry );
        } else if ( geometry instanceof com.vividsolutions.jts.geom.MultiLineString ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.MultiLineString) geometry );
        } else if ( geometry instanceof com.vividsolutions.jts.geom.Polygon ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.Polygon) geometry );
        } else if ( geometry instanceof com.vividsolutions.jts.geom.MultiPolygon ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.MultiPolygon) geometry );
        } else if ( geometry instanceof com.vividsolutions.jts.geom.GeometryCollection ) {
            gmObject = wrap( (com.vividsolutions.jts.geom.GeometryCollection) geometry );
        } else {
            throw new GeometryException( "JTSAdapter.wrap does not support type '"
                                         + geometry.getClass().getName() + "'!" );
        }
        return gmObject;
    }

    /**
     * Converts a <tt>Point</tt> to a <tt>Point</tt>.
     * <p>
     * @param gmPoint point to be converted
     * @return the corresponding <tt>Point</tt> object
     */
    private static com.vividsolutions.jts.geom.Point export( Point gmPoint ) {
       
        com.vividsolutions.jts.geom.Coordinate coord = 
            new com.vividsolutions.jts.geom.Coordinate(gmPoint.getX(),gmPoint.getY() );
       
        return jtsFactory.createPoint( coord );
    }

    /**
     * Converts a <tt>MultiPoint</tt> to a <tt>MultiPoint</tt>.
     * <p>
     * @param gmMultiPoint multipoint to be converted
     * @return the corresponding <tt>MultiPoint</tt> object
     */
    private static com.vividsolutions.jts.geom.MultiPoint export( MultiPoint gmMultiPoint ) {
        Point[] gmPoints = gmMultiPoint.getAllPoints();
        com.vividsolutions.jts.geom.Point[] points = new com.vividsolutions.jts.geom.Point[gmPoints.length];
        for ( int i = 0; i < points.length; i++ ) {
            points[i] = export( gmPoints[i] );
        }
        return jtsFactory.createMultiPoint( points );
    }

    /**
     * Converts a <tt>Curve</tt> to a <tt>LineString</tt>.
     * <p>
     * @param curve <tt>Curve</tt> to be converted
     * @return the corresponding <tt>LineString</tt> object
     * @throws GeometryException
     */
    private static com.vividsolutions.jts.geom.LineString export( Curve curve )
                            throws GeometryException {

        LineString lineString = curve.getAsLineString();
        com.vividsolutions.jts.geom.Coordinate[] coords = new com.vividsolutions.jts.geom.Coordinate[lineString.getNumberOfPoints()];
        for ( int i = 0; i < coords.length; i++ ) {
            Position position = lineString.getPositionAt( i );
            coords[i] = new com.vividsolutions.jts.geom.Coordinate( position.getX(),
                                                                    position.getY() );
        }
        return jtsFactory.createLineString( coords );
    }

    /**
     * Converts a <tt>MultiCurve</tt> to a <tt>MultiLineString</tt>.
     * <p>
     * @param multi <tt>MultiCurve</tt> to be converted
     * @return the corresponding <tt>MultiLineString</tt> object
     * @throws GeometryException
     */
    private static com.vividsolutions.jts.geom.MultiLineString export( MultiCurve multi )
                            throws GeometryException {

        Curve[] curves = multi.getAllCurves();
        com.vividsolutions.jts.geom.LineString[] lineStrings = new com.vividsolutions.jts.geom.LineString[curves.length];
        for ( int i = 0; i < curves.length; i++ ) {
            lineStrings[i] = export( curves[i] );
        }
        return jtsFactory.createMultiLineString( lineStrings );
    }

    /**
     * Converts an array of <tt>Position</tt>s to a <tt>LinearRing</tt>.
     * <p>
     * @param positions an array of <tt>Position</tt>s
     * @return the corresponding <tt>LinearRing</tt> object
     */
    private static com.vividsolutions.jts.geom.LinearRing export( Position[] positions ) {
        com.vividsolutions.jts.geom.Coordinate[] coords = new com.vividsolutions.jts.geom.Coordinate[positions.length];
        for ( int i = 0; i < positions.length; i++ ) {
            coords[i] = new com.vividsolutions.jts.geom.Coordinate( positions[i].getX(),
                                                                    positions[i].getY() );
        }
        return jtsFactory.createLinearRing( coords );
    }

    /**
     * Converts a <tt>Surface</tt> to a <tt>Polygon</tt>.
     * <p>
     * Currently, the <tt>Surface</tt> _must_ contain exactly one patch!
     * <p>
     * @param surface a <tt>Surface</tt>
     * @return the corresponding <tt>Polygon</tt> object
     */
    private static com.vividsolutions.jts.geom.Polygon export( Surface surface ) {
        SurfacePatch patch = null;
        try {
            patch = surface.getSurfacePatchAt( 0 );
        } catch ( GeometryException e ) {
            LOG.logError( "", e );
        }
        Position[] exteriorRing = patch.getExteriorRing();
        Position[][] interiorRings = patch.getInteriorRings();

        com.vividsolutions.jts.geom.LinearRing shell = export( exteriorRing );
        com.vividsolutions.jts.geom.LinearRing[] holes = new com.vividsolutions.jts.geom.LinearRing[0];
        if ( interiorRings != null )
            holes = new com.vividsolutions.jts.geom.LinearRing[interiorRings.length];
        for ( int i = 0; i < holes.length; i++ ) {
            holes[i] = export( interiorRings[i] );
        }
        return jtsFactory.createPolygon( shell, holes );
    }

    /**
     * Converts a <tt>MultiSurface</tt> to a <tt>MultiPolygon</tt>.
     * <p>
     * Currently, the contained <tt>Surface</tt> _must_ have exactly one
     * patch!
     * <p>
     * @param msurface a <tt>MultiSurface</tt>
     * @return the corresponding <tt>MultiPolygon</tt> object
     */
    private static com.vividsolutions.jts.geom.MultiPolygon export( MultiSurface msurface ) {

        Surface[] surfaces = msurface.getAllSurfaces();
        com.vividsolutions.jts.geom.Polygon[] polygons = new com.vividsolutions.jts.geom.Polygon[surfaces.length];

        for ( int i = 0; i < surfaces.length; i++ ) {
            polygons[i] = export( surfaces[i] );
        }
        return jtsFactory.createMultiPolygon( polygons );
    }

    /**
     * Converts a <tt>MultiPrimitive</tt> to a <tt>GeometryCollection</tt>.
     * <p>
     * @param multi a <tt>MultiPrimtive</tt>
     * @return the corresponding <tt>GeometryCollection</tt> object
     * @throws GeometryException
     */
    private static com.vividsolutions.jts.geom.GeometryCollection export( MultiPrimitive multi )
                            throws GeometryException {

        Geometry[] primitives = multi.getAllPrimitives();
        com.vividsolutions.jts.geom.Geometry[] geometries = new com.vividsolutions.jts.geom.Geometry[primitives.length];

        for ( int i = 0; i < primitives.length; i++ ) {
            geometries[i] = export( primitives[i] );
        }
        return jtsFactory.createGeometryCollection( geometries );
    }

    /**
     * Converts a <tt>Point</tt> to a <tt>Point</tt>s.
     * <p>
     * @param point a <tt>Point</tt> object
     * @return the corresponding <tt>Point</tt>
     */
    private static Point wrap( com.vividsolutions.jts.geom.Point point ) {
        com.vividsolutions.jts.geom.Coordinate coord = point.getCoordinate();
        return Double.isNaN( coord.z ) ? new PointImpl( coord.x, coord.y, null )
                                      : new PointImpl( coord.x, coord.y, coord.z, null );
    }

    /**
     * Converts a <tt>MultiPoint</tt> to a <tt>MultiPoint</tt>.
     * <p>
     * @param multi a <tt>MultiPoint</tt> object
     * @return the corresponding <tt>MultiPoint</tt>
     */
    private static MultiPoint wrap( com.vividsolutions.jts.geom.MultiPoint multi ) {
        Point[] gmPoints = new Point[multi.getNumGeometries()];
        for ( int i = 0; i < gmPoints.length; i++ ) {
            gmPoints[i] = wrap( (com.vividsolutions.jts.geom.Point) multi.getGeometryN( i ) );
        }
        return new MultiPointImpl( gmPoints, null );
    }

    /**
     * Converts a <tt>LineString</tt> to a <tt>Curve</tt>.
     * <p>
     * @param line a <tt>LineString</tt> object
     * @return the corresponding <tt>Curve</tt>
     * @throws GeometryException
     */
    private static Curve wrap( com.vividsolutions.jts.geom.LineString line )
                            throws GeometryException {
        com.vividsolutions.jts.geom.Coordinate[] coords = line.getCoordinates();
        Position[] positions = new Position[coords.length];
        for ( int i = 0; i < coords.length; i++ ) {
            positions[i] = new PositionImpl( coords[i].x, coords[i].y );
        }
        return GeometryFactory.createCurve( positions, null );
    }

    /**
     * Converts a <tt>MultiLineString</tt> to a <tt>MultiCurve</tt>.
     * <p>
     * @param multi a <tt>MultiLineString</tt> object
     * @return the corresponding <tt>MultiCurve</tt>
     * @throws GeometryException
     */
    private static MultiCurve wrap( com.vividsolutions.jts.geom.MultiLineString multi )
                            throws GeometryException {
        Curve[] curves = new Curve[multi.getNumGeometries()];
        for ( int i = 0; i < curves.length; i++ ) {
            curves[i] = wrap( (com.vividsolutions.jts.geom.LineString) multi.getGeometryN( i ) );
        }
        return GeometryFactory.createMultiCurve( curves );
    }

    /**
     *
     * Converts a <tt>Polygon</tt> to a <tt>Surface</tt>.
     * <p>
     * @param polygon a <tt>Polygon</tt>
     * @return the corresponding <tt>Surface</tt> object
     * @throws GeometryException
     */
    private static Surface wrap( com.vividsolutions.jts.geom.Polygon polygon )
                            throws GeometryException {

        Position[] exteriorRing = createGMPositions( polygon.getExteriorRing() );
        Position[][] interiorRings = new Position[polygon.getNumInteriorRing()][];

        for ( int i = 0; i < interiorRings.length; i++ ) {
            interiorRings[i] = createGMPositions( polygon.getInteriorRingN( i ) );
        }
        SurfacePatch patch = new PolygonImpl( new SurfaceInterpolationImpl(), exteriorRing,
                                              interiorRings, null );

        return new SurfaceImpl( patch );
    }

    /**
     * Converts a <tt>MultiPolygon</tt> to a <tt>MultiSurface</tt>.
     * <p>
     * @param multiPolygon a <tt>MultiPolygon</tt>
     * @return the corresponding <tt>MultiSurface</tt> object
     * @throws GeometryException
     */
    private static MultiSurface wrap( com.vividsolutions.jts.geom.MultiPolygon multiPolygon )
                            throws GeometryException {

        Surface[] surfaces = new Surface[multiPolygon.getNumGeometries()];
        for ( int i = 0; i < surfaces.length; i++ ) {
            surfaces[i] = wrap( (com.vividsolutions.jts.geom.Polygon) multiPolygon.getGeometryN( i ) );
        }
        return new MultiSurfaceImpl( surfaces );
    }

    /**
     * Converts a <tt>GeometryCollection</tt> to a <tt>MultiPrimitve</tt>.
     * <p>
     * @param collection a <tt>GeometryCollection</tt>
     * @return the corresponding <tt>MultiPrimitive</tt> object
     * @throws GeometryException
     */
    private static MultiPrimitive wrap( com.vividsolutions.jts.geom.GeometryCollection collection )
                            throws GeometryException {

        MultiPrimitive multi = new MultiPrimitiveImpl( null );
        for ( int i = 0; i < collection.getNumGeometries(); i++ ) {
            multi.add( wrap( collection.getGeometryN( i ) ) );
        }
        return multi;
    }

    /**
     * Converts a <tt>LineString</tt> to an array of <tt>Position</tt>s.
     * <p>
     * @param line a <tt>LineString</tt> object
     * @return the corresponding array of <tt>Position</tt>s
     */
    private static Position[] createGMPositions( com.vividsolutions.jts.geom.LineString line ) {
        com.vividsolutions.jts.geom.Coordinate[] coords = line.getCoordinates();
        Position[] positions = new Position[coords.length];
        for ( int i = 0; i < coords.length; i++ ) {
            positions[i] = new PositionImpl( coords[i].x, coords[i].y );
        }
        return positions;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: JTSAdapter.java,v $
 Revision 1.12  2006/11/27 09:07:51  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.11  2006/07/04 18:31:35  poth
 bug fix handling GeometryCollections


 ********************************************************************** */