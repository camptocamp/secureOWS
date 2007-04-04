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

import java.util.ArrayList;

import org.deegree.framework.util.StringTools;
import org.deegree.model.crs.CoordinateSystem;

/**
 * Factory to create geometry instances.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.17 $, $Date: 2006/11/23 09:17:39 $
 * 
 */
public final class GeometryFactory {
    
    private GeometryFactory() {
        //Hidden default constructor.
    }
    
    
    
    /**
     * creates a Envelope object out from two corner coordinates
     * @param minx lower x-axis coordinate
     * @param miny lower y-axis coordinate
     * @param maxx upper x-axis coordinate
     * @param maxy upper y-axis coordinate
     * @param crs The coordinate system
     * @return an Envelope with given parameters
     */
    public static Envelope createEnvelope( double minx, double miny, 
    									   double maxx, double maxy,
										   CoordinateSystem crs) {
        Position min = createPosition( minx, miny );
        Position max = createPosition( maxx, maxy );
        return new EnvelopeImpl( min, max, crs );
    }
    
    /**
     * creates a Envelope object out from two corner coordinates
     * @param min lower point
     * @param max upper point
     * @param crs The coordinate system
     * @return an Envelope with given parameters
     */
    public static Envelope createEnvelope( Position min, Position max,
    									   CoordinateSystem crs) {
        return new EnvelopeImpl( min, max, crs );
    }
    
    /**
     * creates an Envelope from a comma seperated String; e.g.: 10,34,15,48
     * @param bbox the boundingbox of the created Envelope
     * @param crs The coordinate system
     * @return an Envelope with given parameters
     */
    public static Envelope createEnvelope(String bbox, CoordinateSystem crs) {
        double[] d = StringTools.toArrayDouble( bbox, "," );
        return createEnvelope( d[0], d[1], d[2], d[3], crs );
    }

    /**
     * creates a Position from two coordinates.
     * @param x coordinate on the x-axis
     * @param y coordinate on the y-axis
     * @return a Position defining position x, y
     */
    public static Position createPosition( double x, double y ) {
        return new PositionImpl( x, y );
    }
    
    /**
     * creates a Position from three coordinates.
     * @param x coordinate on the x-axis
     * @param y coordinate on the y-axis
     * @param z coordinate on the z-axis
     * @return a Position defining position x, y, z
     */
    public static Position createPosition(double x, double y, double z) {
        return new PositionImpl(new double[] { x, y, z});
    }

    /**
     * creates a Position from an array of double.
     * 
     * @param p
     *            list of points
     * @return the Position defined by the array.
     */
    public static Position createPosition( double[] p ) {
        return new PositionImpl( p );
    }

    /**
     * creates a Point from two coordinates.
     * @param x x coordinate
     * @param y y coordinate
     * @param crs spatial reference system of the point geometry
     * @return a Position defining position x, y in the given CRS
     */
    public static Point createPoint( double x, double y, CoordinateSystem crs ) {
        return new PointImpl( x, y, crs );
    }
    
    /**
     * creates a Point from two coordinates.
     * @param x x coordinate
     * @param y y coordinate
     * @param z coordinate on the z-axis
     * @param crs spatial reference system of the point geometry
     * @return a Position defining position x, y, z in the given CRS
     */
    public static Point createPoint( double x, double y, double z, 
    							     CoordinateSystem crs ) {
        return new PointImpl( x, y, z, crs );
    }

    /**
     * creates a Point from a position.
     * @param position position
     * @param crs spatial reference system of the point geometry
     * @return the Position defined by the array in the given CRS
     */
    public static Point createPoint( Position position, CoordinateSystem crs ) {
        return new PointImpl( position, crs );
    }

    /**
     * creates a Point from a wkb.
     * @param wkb geometry in Well-Known Binary (WKB) format
     * @param srs spatial reference system of the geometry
     * @return the Position defined by the WKB and the given CRS
     * @throws GeometryException if the wkb is not known or invalid
     */
    public static Point createPoint( byte[] wkb, CoordinateSystem srs ) throws GeometryException {
        int wkbType = -1;
        double x = 0;
        double y = 0;

        byte byteorder = wkb[0];

        if ( byteorder == 0 ) {
            wkbType = ByteUtils.readBEInt( wkb, 1 );
        } else {
            wkbType = ByteUtils.readLEInt( wkb, 1 );
        }

        if ( wkbType != 1 ) {
            throw new GeometryException( "invalid byte stream" );
        }

        if ( byteorder == 0 ) {
            x = ByteUtils.readBEDouble( wkb, 5 );
            y = ByteUtils.readBEDouble( wkb, 13 );
        } else {
            x = ByteUtils.readLEDouble( wkb, 5 );
            y = ByteUtils.readLEDouble( wkb, 13 );
        }

        return new PointImpl( x, y, srs );
    }

    /**
     * creates a CurveSegment from an array of points.
     *
     * @param points array of Point
     * @param crs CS_CoordinateSystem spatial reference system of the curve
     * @return A curve defined by the given Points in the CRS.
     * @throws GeometryException if the point array is empty
     */
    public static CurveSegment createCurveSegment( Position[] points, CoordinateSystem crs )
                                          throws GeometryException {
        return new LineStringImpl( points, crs );
    }

    /**
     * creates a Curve from an array of Positions.
     *
     * @param positions positions
     * @param crs spatial reference system of the geometry
     * @return A curve defined by the given Points in the CRS.
     * @throws GeometryException if the point array is empty
     */
    public static Curve createCurve( Position[] positions, CoordinateSystem crs )
                            throws GeometryException {
        CurveSegment[] cs = new CurveSegment[1];
        cs[0] = createCurveSegment( positions, crs );
        return new CurveImpl( cs );
    }

    /**
     * creates a Curve from one curve segment.
     *
     * @param segment CurveSegments     
     * @return a new CurveSegment
     * @throws GeometryException if the segment is null
     */
    public static Curve createCurve( CurveSegment segment ) throws GeometryException {
        return new CurveImpl( new CurveSegment[] { segment } );
    }

    /**
     * creates a Curve from an array of curve segments.
     *
     * @param segments array of CurveSegments     
     * @return a new CurveSegment
     * @throws GeometryException if the segment is null or has no values

     */
    public static Curve createCurve( CurveSegment[] segments ) throws GeometryException {
        return new CurveImpl( segments );
    }
    
    /**
     * creates a GM_Curve from an array of ordinates
     * 
     * TODO: If resources are available, think about good programming style.
     * @param ord the ordinates
     * @param dim the dimension of the ordinates
     * @param crs the spatial reference system of the geometry
     *
     * @return the Curve defined by the given parameters
     * @throws GeometryException if the ord array is empty
     */
    public static Curve createCurve( double[] ord, int dim, CoordinateSystem crs ) 
    												throws GeometryException {
    	Position[] pos = new Position[ord.length/dim];
    	int i = 0;
    	while (i < ord.length) {
    		double[] o = new double[dim]; 
			for (int j = 0; j < dim; j++) {
				o[j] = ord[i++];
			}
			pos[i/dim -1] = GeometryFactory.createPosition( o );
		}    	
        return GeometryFactory.createCurve( pos, crs );
    }

    /**
     * creates a SurfacePatch from array(s) of Position
     *
     * @param exteriorRing exterior ring of the patch
     * @param interiorRings interior rings of the patch
     * @param si SurfaceInterpolation
     * @param crs CS_CoordinateSystem spatial reference system of the surface patch
     * @return a Surfacepatch defined by the given Parameters
     * @throws GeometryException 
     */
    public static SurfacePatch createSurfacePatch( Position[] exteriorRing, 
                                                  Position[][] interiorRings, 
                                                  SurfaceInterpolation si, 
                                                  CoordinateSystem crs )
                                          throws GeometryException {
        return new PolygonImpl( si, exteriorRing, interiorRings, crs );
    }

    /**
     * creates a Curve from a wkb.
     *
     * @param wkb byte stream that contains the wkb information
     * @param crs CS_CoordinateSystem spatial reference system of the curve
     * @return the Curve defined by the WKB and the given CRS
     * @throws GeometryException if the wkb is not known or invalid     
     * 
     */
    public static Curve createCurve( byte[] wkb, CoordinateSystem crs ) throws GeometryException {
        int wkbType = -1;
        int numPoints = -1;
        Position[] points = null;
        double x = 0;
        double y = 0;

        byte byteorder = wkb[0];

        if ( byteorder == 0 ) {
            wkbType = ByteUtils.readBEInt( wkb, 1 );
        } else {
            wkbType = ByteUtils.readLEInt( wkb, 1 );
        }

        // check if it's realy a linestrin/curve
        if ( wkbType != 2 ) {
            throw new GeometryException( "invalid byte stream for Curve" );
        }

        // read number of points
        if ( byteorder == 0 ) {
            numPoints = ByteUtils.readBEInt( wkb, 5 );
        } else {
            numPoints = ByteUtils.readLEInt( wkb, 5 );
        }

        int offset = 9;

        points = new Position[numPoints];

        // read the i-th point depending on the byteorde
        if ( byteorder == 0 ) {
            for ( int i = 0; i < numPoints; i++ ) {
                x = ByteUtils.readBEDouble( wkb, offset );
                offset += 8;
                y = ByteUtils.readBEDouble( wkb, offset );
                offset += 8;
                points[i] = new PositionImpl( x, y );
            }
        } else {
            for ( int i = 0; i < numPoints; i++ ) {
                x = ByteUtils.readLEDouble( wkb, offset );
                offset += 8;
                y = ByteUtils.readLEDouble( wkb, offset );
                offset += 8;
                points[i] = new PositionImpl( x, y );
            }
        }

        CurveSegment[] segment = new CurveSegment[1];

        segment[0] = createCurveSegment( points, crs );

        return createCurve( segment );
    }

    /**
     * creates a Surface composed of one SurfacePatch from array(s)
     * of Position
     *
     * @param exteriorRing exterior ring of the patch
     * @param interiorRings interior rings of the patch
     * @param si SurfaceInterpolation
     * @param crs CS_CoordinateSystem spatial reference system of the surface patch
     * @return a Surface composed of one SurfacePatch from array(s)
     * of Position
     * @throws GeometryException if the implicite orientation is not '+' or '-', or the rings aren't closed
     */
    public static Surface createSurface( Position[] exteriorRing, Position[][] interiorRings, 
                                        SurfaceInterpolation si, CoordinateSystem crs )
                                throws GeometryException {
        SurfacePatch sp = new PolygonImpl( si, exteriorRing, interiorRings, crs );
        return createSurface( sp );
    }

    /**
     * creates a Surface from an array of SurfacePatch.
     *
     * @param patch patches that build the surface
     * @return a Surface from an array of SurfacePatch.
     * @throws GeometryException if implicite the orientation is not '+' or '-'
     */
    public static Surface createSurface( SurfacePatch patch ) throws GeometryException {
        return new SurfaceImpl( patch );
    }    
    
    /**
     * creates a Surface from an array of SurfacePatch.
     * @param patches patches that build the surface
     *
     * @return a Surface from an array of SurfacePatch.
     * @throws GeometryException if implicite the orientation is not '+' or '-'
     */
    public static Surface createSurface( SurfacePatch[] patches ) throws GeometryException {
        return new SurfaceImpl( patches );
    }

    /**
     * creates a Surface from a wkb.
     *
     * @param wkb byte stream that contains the wkb information
     * @param crs CS_CoordinateSystem spatial reference system of the curve
     * @param si SurfaceInterpolation
     * @return a Surface from a wkb.
     * @throws GeometryException if the implicite orientation is not '+' or '-' or the wkb is not known or invalid     
     */
    public static Surface createSurface( byte[] wkb, CoordinateSystem crs, 
                                        SurfaceInterpolation si ) throws GeometryException {
        int wkbtype = -1;
        int numRings = 0;
        int numPoints = 0;
        int offset = 0;
        double x = 0;
        double y = 0;

        Position[] externalBoundary = null;
        Position[][] internalBoundaries = null;

        byte byteorder = wkb[offset++];

        if ( byteorder == 0 ) {
            wkbtype = ByteUtils.readBEInt( wkb, offset );
        } else {
            wkbtype = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        if ( wkbtype == 6 ) {
            return null;
        }

        // is the geometry respresented by wkb a polygon?
        if ( wkbtype != 3 ) {
            throw new GeometryException( "invalid byte stream for Surface " + wkbtype );
        }

        // read number of rings of the polygon
        if ( byteorder == 0 ) {
            numRings = ByteUtils.readBEInt( wkb, offset );
        } else {
            numRings = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        // read number of points of the external ring
        if ( byteorder == 0 ) {
            numPoints = ByteUtils.readBEInt( wkb, offset );
        } else {
            numPoints = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        // allocate memory for the external boundary
        externalBoundary = new Position[numPoints];

        if ( byteorder == 0 ) {
            // read points of the external boundary from the byte[]
            for ( int i = 0; i < numPoints; i++ ) {
                x = ByteUtils.readBEDouble( wkb, offset );
                offset += 8;
                y = ByteUtils.readBEDouble( wkb, offset );
                offset += 8;
                externalBoundary[i] = new PositionImpl( x, y );
            }
        } else {
            // read points of the external boundary from the byte[]
            for ( int i = 0; i < numPoints; i++ ) {
                x = ByteUtils.readLEDouble( wkb, offset );
                offset += 8;
                y = ByteUtils.readLEDouble( wkb, offset );
                offset += 8;
                externalBoundary[i] = new PositionImpl( x, y );
            }
        }

        // only if numRings is larger then one there internal rings
        if ( numRings > 1 ) {
            internalBoundaries = new Position[numRings - 1][];
        }

        if ( byteorder == 0 ) {
            for ( int j = 1; j < numRings; j++ ) {
                // read number of points of the j-th internal ring
                numPoints = ByteUtils.readBEInt( wkb, offset );
                offset += 4;

                // allocate memory for the j-th internal boundary
                internalBoundaries[j - 1] = new Position[numPoints];

                // read points of the external boundary from the byte[]
                for ( int i = 0; i < numPoints; i++ ) {
                    x = ByteUtils.readBEDouble( wkb, offset );
                    offset += 8;
                    y = ByteUtils.readBEDouble( wkb, offset );
                    offset += 8;
                    internalBoundaries[j - 1][i] = new PositionImpl( x, y );
                }
            }
        } else {
            for ( int j = 1; j < numRings; j++ ) {
                // read number of points of the j-th internal ring
                numPoints = ByteUtils.readLEInt( wkb, offset );
                offset += 4;

                // allocate memory for the j-th internal boundary
                internalBoundaries[j - 1] = new Position[numPoints];

                // read points of the external boundary from the byte[]
                for ( int i = 0; i < numPoints; i++ ) {
                    x = ByteUtils.readLEDouble( wkb, offset );
                    offset += 8;
                    y = ByteUtils.readLEDouble( wkb, offset );
                    offset += 8;
                    internalBoundaries[j - 1][i] = new PositionImpl( x, y );
                }
            }
        }

        SurfacePatch patch = createSurfacePatch( externalBoundary, internalBoundaries, si, 
                                                       crs );

        return createSurface( patch );
    }    

    /**
     * Creates a <tt>Surface</tt> from a <tt>Envelope</tt>.
     * <p>
     * @param bbox envelope to be converted
     * @param crs spatial reference system of the surface
     * @return corresponding surface
     *
     * @throws GeometryException if the implicite orientation is not '+' or '-'
     */
    public static Surface createSurface (Envelope bbox, CoordinateSystem crs)
        throws GeometryException {
        
        Position min = bbox.getMin ();
        Position max = bbox.getMax ();    
        Position [] exteriorRing = null;
        if ( min.getCoordinateDimension() == 2 ) {
            exteriorRing = new Position [] {
                    min, new PositionImpl (min.getX(), max.getY()), 
                    max, new PositionImpl (max.getX(), min.getY()), min
                };
        } else {
            exteriorRing = new Position [] {
                    min, new PositionImpl (min.getX(), max.getY(), min.getZ()+((max.getZ() - min.getZ())*0.5) ), 
                    max, new PositionImpl (max.getX(), min.getY(), min.getZ()+((max.getZ() - min.getZ())*0.5)), min
                };
        }

        return createSurface (exteriorRing, null,
                                 new SurfaceInterpolationImpl (), crs);
    }
    
    /**
     * Creates a <tt>GM_Surface</tt> from the ordinates of the exterior ring
     * and the the interior rings
     * <p>     
     * @param exterior ring
     * @param interior ring
     * @param dim of the surface
     * @param crs spatial reference system of the surface
     * @return corresponding surface
     * @throws GeometryException if the implicite orientation is not '+' or '-' 
     *
     */
    public static Surface createSurface (double[] exterior, double[][] interior, 
    										   int dim, CoordinateSystem crs)
        									   throws GeometryException {
        
    	// get exterior ring
    	Position[] ext = new Position[exterior.length/dim];
    	int i = 0;
        int k = 0;
    	while (i < exterior.length-1) {
    		double[] o = new double[dim]; 
			for (int j = 0; j < dim; j++) {
				o[j] = exterior[i++];
			}
			ext[k++] = GeometryFactory.createPosition( o );
		}
    	
    	// get interior rings if available
    	Position[][] in = null;
    	if ( interior != null && interior.length > 0 ) {
	    	in = new Position[interior.length][];
	    	for (int j = 0; j < in.length; j++) {
	    		in[j] = new Position[interior[j].length/dim];
	    		i = 0;
	        	while (i < interior[j].length) {
	        		double[] o = new double[dim]; 
	    			for (int z = 0; z < dim; z++) {
	    				o[z] = interior[j][i++];
	    			}
	    			in[j][i/dim-1] = GeometryFactory.createPosition( o );
	    		}
			}
    	}

    	// default - linear - interpolation
    	SurfaceInterpolation si = new SurfaceInterpolationImpl();
        return GeometryFactory.createSurface( ext, in, si, crs );
    }
    
    /**
     * creates a MultiPoint from an array of Point.
     *
     * @param points array of Points
     * @return a MultiPoint from an array of Point.
     *
     */
    public static MultiPoint createMultiPoint( Point[] points ) {
        return new MultiPointImpl( points );
    }

    /**
     * creates a MultiPoint from a wkb.
     *
     * @param wkb byte stream that contains the wkb information
     * @param crs CS_CoordinateSystem spatial reference system of the curve
     * @return the MultiPoint defined by the WKB and the given CRS
     * @throws GeometryException if the wkb is not known or invalid
     *
     */
    public static MultiPoint createMultiPoint( byte[] wkb, CoordinateSystem crs )
                                      throws GeometryException {
        Point[] points = null;
        int wkbType = -1;
        int numPoints = -1;
        double x = 0;
        double y = 0;
        byte byteorder = wkb[0];

        // read wkbType
        if ( byteorder == 0 ) {
            wkbType = ByteUtils.readBEInt( wkb, 1 );
        } else {
            wkbType = ByteUtils.readLEInt( wkb, 1 );
        }

        // if the geometry isn't a multipoint throw exception
        if ( wkbType != 4 ) {
            throw new GeometryException( "Invalid byte stream for MultiPoint" );
        }

        // read number of points
        if ( byteorder == 0 ) {
            numPoints = ByteUtils.readBEInt( wkb, 5 );
        } else {
            numPoints = ByteUtils.readLEInt( wkb, 5 );
        }

        points = new Point[numPoints];

        int offset = 9;

        Object[] o = new Object[3];
        o[2] = crs;

        // read all points
        for ( int i = 0; i < numPoints; i++ ) {
            // byteorder of the i-th point
            byteorder = wkb[offset];

            // wkbType of the i-th geometry
            if ( byteorder == 0 ) {
                wkbType = ByteUtils.readBEInt( wkb, offset + 1 );
            } else {
                wkbType = ByteUtils.readLEInt( wkb, offset + 1 );
            }

            // if the geometry isn't a point throw exception
            if ( wkbType != 1 ) {
                throw new GeometryException( "Invalid byte stream for Point as " + 
                                        "part of a multi point" );
            }

            // read the i-th point depending on the byteorde
            if ( byteorder == 0 ) {
                x = ByteUtils.readBEDouble( wkb, offset + 5 );
                y = ByteUtils.readBEDouble( wkb, offset + 13 );
            } else {
                x = ByteUtils.readLEDouble( wkb, offset + 5 );
                y = ByteUtils.readLEDouble( wkb, offset + 13 );
            }
            
            offset += 21;

            points[i] = new PointImpl( x, y, crs );
        }

        return createMultiPoint( points );
    }
    

    /**
     * creates a MultiCurve from an array of Curves.
     *
     * @param curves
     * @return a MultiCurve from an array of Curves.
     */
    public static MultiCurve createMultiCurve( Curve[] curves ) {
        return new MultiCurveImpl( curves );
    }

    /**
     * creates a MultiCurve from a wkb.
     *
     * @param wkb byte stream that contains the wkb information
     * @param crs CS_CoordinateSystem spatial reference system of the curve
     * @return the MultiCurve defined by the WKB and the given CRS
     * @throws GeometryException if the wkb is not known or invalid
     */
    public static MultiCurve createMultiCurve( byte[] wkb, CoordinateSystem crs )
                                      throws GeometryException {
        int wkbType = -1;
        int numPoints = -1;
        int numParts = -1;
        double x = 0;
        double y = 0;
        Position[][] points = null;
        int offset = 0;
        byte byteorder = wkb[offset++];

        if ( byteorder == 0 ) {
            wkbType = ByteUtils.readBEInt( wkb, offset );
        } else {
            wkbType = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        // check if it's realy a linestring
        if ( wkbType != 5 ) {
            throw new GeometryException( "Invalid byte stream for MultiCurve" );
        }

        // read number of linestrings
        if ( byteorder == 0 ) {
            numParts = ByteUtils.readBEInt( wkb, offset );
        } else {
            numParts = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        points = new Position[numParts][];

        // for every linestring
        for ( int j = 0; j < numParts; j++ ) {
            byteorder = wkb[offset++];

            if ( byteorder == 0 ) {
                wkbType = ByteUtils.readBEInt( wkb, offset );
            } else {
                wkbType = ByteUtils.readLEInt( wkb, offset );
            }

            offset += 4;

            // check if it's realy a linestring
            if ( wkbType != 2 ) {
                throw new GeometryException( "Invalid byte stream for Curve as " + 
                                        " part of a MultiCurve." );
            }

            // read number of points
            if ( byteorder == 0 ) {
                numPoints = ByteUtils.readBEInt( wkb, offset );
            } else {
                numPoints = ByteUtils.readLEInt( wkb, offset );
            }

            offset += 4;

            points[j] = new Position[numPoints];

            // read the i-th point depending on the byteorde
            if ( byteorder == 0 ) {
                for ( int i = 0; i < numPoints; i++ ) {
                    x = ByteUtils.readBEDouble( wkb, offset );
                    offset += 8;
                    y = ByteUtils.readBEDouble( wkb, offset );
                    offset += 8;
                    points[j][i] = new PositionImpl( x, y );
                }
            } else {
                for ( int i = 0; i < numPoints; i++ ) {
                    x = ByteUtils.readLEDouble( wkb, offset );
                    offset += 8;
                    y = ByteUtils.readLEDouble( wkb, offset );
                    offset += 8;
                    points[j][i] = new PositionImpl( x, y );
                }
            }
        }

        CurveSegment[] segment = new CurveSegment[1];
        Curve[] curves = new Curve[numParts];

        for ( int i = 0; i < numParts; i++ ) {
            segment[0] = createCurveSegment( points[i], crs );
            curves[i] = createCurve( segment );
        }

        return createMultiCurve( curves );
    }
    
    /**
     * creates a MultiSurface from an array of surfaces
     * @param surfaces 
     * @return a MultiSurface from an array of surfaces
     */
    public static MultiSurface createMultiSurface( Surface[] surfaces ) {
        return new MultiSurfaceImpl( surfaces );
    }

    /**
     * creates a MultiSurface from a wkb
     * @param wkb geometry in Well-Known Binary (WKB) format
     * @param crs spatial reference system of the geometry
     * @param si surface interpolation
     * @return the MultiSurface defined by the WKB and the given CRS
     * @throws GeometryException if the wkb is not known or invalid
     */
    public static MultiSurface createMultiSurface( byte[] wkb, CoordinateSystem crs, 
                                                  SurfaceInterpolation si )
                                          throws GeometryException {
        int wkbtype = -1;
        int numPoly = 0;
        int numRings = 0;
        int numPoints = 0;
        int offset = 0;
        double x = 0;
        double y = 0;
        Position[] externalBoundary = null;
        Position[][] internalBoundaries = null;
        byte byteorder = wkb[offset++];

        if ( byteorder == 0 ) {
            wkbtype = ByteUtils.readBEInt( wkb, offset );
        } else {
            wkbtype = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        // is the wkbmetry a multipolygon?
        if ( wkbtype != 6 ) {
            throw new GeometryException( "Invalid byte stream for MultiSurface" );
        }

        // read number of polygons on the byte[]
        if ( byteorder == 0 ) {
            numPoly = ByteUtils.readBEInt( wkb, offset );
        } else {
            numPoly = ByteUtils.readLEInt( wkb, offset );
        }

        offset += 4;

        ArrayList<Surface> list = new ArrayList<Surface>( numPoly );

        for ( int ip = 0; ip < numPoly; ip++ ) {
            byteorder = wkb[offset];
            offset++;

            if ( byteorder == 0 ) {
                wkbtype = ByteUtils.readBEInt( wkb, offset );
            } else {
                wkbtype = ByteUtils.readLEInt( wkb, offset );
            }

            offset += 4;

            // is the geometry respresented by wkb a polygon?
            if ( wkbtype != 3 ) {
                throw new GeometryException( "invalid byte stream for Surface " + wkbtype );
            }

            // read number of rings of the polygon
            if ( byteorder == 0 ) {
                numRings = ByteUtils.readBEInt( wkb, offset );
            } else {
                numRings = ByteUtils.readLEInt( wkb, offset );
            }

            offset += 4;

            // read number of points of the external ring
            if ( byteorder == 0 ) {
                numPoints = ByteUtils.readBEInt( wkb, offset );
            } else {
                numPoints = ByteUtils.readLEInt( wkb, offset );
            }

            offset += 4;

            // allocate memory for the external boundary
            externalBoundary = new Position[numPoints];

            if ( byteorder == 0 ) {
                // read points of the external boundary from the byte[]
                for ( int i = 0; i < numPoints; i++ ) {
                    x = ByteUtils.readBEDouble( wkb, offset );
                    offset += 8;
                    y = ByteUtils.readBEDouble( wkb, offset );
                    offset += 8;
                    externalBoundary[i] = new PositionImpl( x, y );
                }
            } else {
                // read points of the external boundary from the byte[]
                for ( int i = 0; i < numPoints; i++ ) {
                    x = ByteUtils.readLEDouble( wkb, offset );
                    offset += 8;
                    y = ByteUtils.readLEDouble( wkb, offset );
                    offset += 8;
                    externalBoundary[i] = new PositionImpl( x, y );
                }
            }

            // only if numRings is larger then one there internal rings
            if ( numRings > 1 ) {
                internalBoundaries = new Position[numRings - 1][];
            }

            if ( byteorder == 0 ) {
                for ( int j = 1; j < numRings; j++ ) {
                    // read number of points of the j-th internal ring
                    numPoints = ByteUtils.readBEInt( wkb, offset );
                    offset += 4;

                    // allocate memory for the j-th internal boundary
                    internalBoundaries[j - 1] = new Position[numPoints];

                    // read points of the external boundary from the byte[]
                    for ( int i = 0; i < numPoints; i++ ) {
                        x = ByteUtils.readBEDouble( wkb, offset );
                        offset += 8;
                        y = ByteUtils.readBEDouble( wkb, offset );
                        offset += 8;
                        internalBoundaries[j - 1][i] = new PositionImpl( x, y );
                    }
                }
            } else {
                for ( int j = 1; j < numRings; j++ ) {
                    // read number of points of the j-th internal ring
                    numPoints = ByteUtils.readLEInt( wkb, offset );
                    offset += 4;

                    // allocate memory for the j-th internal boundary
                    internalBoundaries[j - 1] = new Position[numPoints];

                    // read points of the external boundary from the byte[]
                    for ( int i = 0; i < numPoints; i++ ) {
                        x = ByteUtils.readLEDouble( wkb, offset );
                        offset += 8;
                        y = ByteUtils.readLEDouble( wkb, offset );
                        offset += 8;
                        internalBoundaries[j - 1][i] = new PositionImpl( x, y );
                    }
                }
            }

            SurfacePatch patch = createSurfacePatch( externalBoundary, internalBoundaries, si, 
                                                           crs );

            list.add( createSurface( patch ) );
        }

        MultiSurface multisurface = new MultiSurfaceImpl( crs );

        for ( int i = 0; i < list.size(); i++ ) {
            multisurface.addSurface( list.get( i ) );
        }

        return multisurface;
    }   

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeometryFactory.java,v $
Revision 1.17  2006/11/23 09:17:39  bezema
the middled z-values should be added to the min.getZ() value in create surface from envelope

Revision 1.16  2006/11/03 14:16:22  poth
bug fix - creating a surface from an envelope

Revision 1.15  2006/09/19 07:23:03  bezema
fixed documentation

Revision 1.14  2006/09/18 14:10:28  bezema
added documentation

Revision 1.13  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
