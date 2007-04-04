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

import org.deegree.model.crs.CoordinateSystem;


/**
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
class LinearIntersects {
    /**
     * the operations returns true if two the submitted points intersects
     */
    public static boolean intersects( Position point1, Position point2 ) {
        double mute = 0.001;

        double d = 0;
        double[] p1 = point1.getAsArray();
        double[] p2 = point2.getAsArray();

        for ( int i = 0; i < p1.length; i++ ) {
            d += ( ( p1[i] - p2[i] ) * ( p1[i] - p2[i] ) );
        }

        return Math.sqrt( d ) <  mute;
    }

    /**
     * the operations returns true if the submitted point intersects
     * the passed curve segment
     */
    public static boolean intersects( Position point, CurveSegment curve ) throws Exception {
        boolean inter = false;
        double mute = 0.001;

        Position[] points = curve.getPositions();

        for ( int i = 0; i < ( points.length - 1 ); i++ ) {
            if ( linesIntersect( points[i].getX(), points[i].getY(), points[i + 1].getX(), 
                                 points[i + 1].getY(), point.getX() - mute, point.getY() - mute, 
                                 point.getX() + mute, point.getY() - mute ) || 
                     linesIntersect( points[i].getX(), points[i].getY(), points[i + 1].getX(), 
                                     points[i + 1].getY(), point.getX() + mute, point.getY() - mute, 
                                     point.getX() + mute, point.getY() + mute ) || 
                     linesIntersect( points[i].getX(), points[i].getY(), points[i + 1].getX(), 
                                     points[i + 1].getY(), point.getX() + mute, point.getY() + mute, 
                                     point.getX() - mute, point.getY() + mute ) || 
                     linesIntersect( points[i].getX(), points[i].getY(), points[i + 1].getX(), 
                                     points[i + 1].getY(), point.getX() - mute, point.getY() + mute, 
                                     point.getX() - mute, point.getY() - mute ) ) {
                inter = true;
                break;
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the submitted point intersects
     * the submitted surface patch
     */
    public static boolean intersects( Position point, SurfacePatch surface ) {
        return LinearContains.contains( surface, point );
    }

    /**
     * the operation returns true if the two submitted curves segments intersects
     */
    public static boolean intersects( CurveSegment curve1, CurveSegment curve2 ) {
        Position[] points = curve1.getPositions();
        Position[] other = curve2.getPositions();
        boolean inter = false;

        for ( int i = 0; i < ( points.length - 1 ); i++ ) {
            for ( int j = 0; j < ( other.length - 1 ); j++ ) {
                if ( linesIntersect( points[i].getX(), points[i].getY(), points[i + 1].getX(), 
                                     points[i + 1].getY(), other[j].getX(), other[j].getY(), 
                                     other[j + 1].getX(), other[j + 1].getY() ) ) {
                    inter = true;
                    break;
                }
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the submitted curve segment intersects
     * the submitted surface patch
     */
    public static boolean intersects( CurveSegment curve, SurfacePatch surface )
                       throws Exception {
        boolean inter = false;
        // is the curve completly embedded within the surface patch        

        if ( LinearContains.contains( surface, curve ) ) {
            inter = true;
        }

        // intersects the curve the exterior ring of the surface patch
        if ( !inter ) {
            Position[] ex = surface.getExteriorRing();
            CurveSegment cs = new LineStringImpl( ex, surface.getCoordinateSystem() );

            if ( intersects( curve, cs ) ) {
                inter = true;
            }
        }

        // intersects the curve one of the interior rings of the surface patch
        if ( !inter ) {
            Position[][] interior = surface.getInteriorRings();

            if ( interior != null ) {
                for ( int i = 0; i < interior.length; i++ ) {
                    CurveSegment cs = new LineStringImpl( interior[i], 
                                                                 surface.getCoordinateSystem() );
    
                    if ( intersects( curve, cs ) ) {
                        inter = true;
                        break;
                    }
                }
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the two submitted surface patches intersects
     */
    public static boolean intersects( SurfacePatch surface1, SurfacePatch surface2 )
                       throws Exception {
        boolean inter = false;
        CoordinateSystem crs1 = surface1.getCoordinateSystem();
        CoordinateSystem crs2 = surface2.getCoordinateSystem();

        if ( LinearContains.contains( surface1, surface2 ) || 
        		LinearContains.contains( surface2, surface1 ) ) {
            inter = true;
        }

        if ( !inter ) {
            Position[] ex1 = surface1.getExteriorRing();
            CurveSegment cs1 = new LineStringImpl( ex1, crs1 );
            Position[] ex2 = surface2.getExteriorRing();
            CurveSegment cs2 = new LineStringImpl( ex2, crs2 );

            // intersects exterior rings ?
            inter = intersects( cs1, cs2 );

            // intersects first exterior ring one of the interior rings of the
            // second szrface patch
            if ( !inter ) {
                Position[][] interior = surface2.getInteriorRings();
                
                if ( interior != null ) {
                    for ( int i = 0; i < interior.length; i++ ) {
                        cs2 = new LineStringImpl( interior[i], crs2 );
    
                        if ( intersects( cs1, cs2 ) ) {
                            inter = true;
                            break;
                        }
                    }
                }
            }

            // intersects the interior rings of the first surface patch with one
            //of the interior rings of the second surface patch
            if ( !inter ) {
                Position[][] interior1 = surface1.getInteriorRings();
                Position[][] interior2 = surface2.getInteriorRings();

                if ( interior1 != null && interior2 != null ) {
                    for ( int i = 0; i < interior1.length; i++ ) {
                        cs1 = new LineStringImpl( interior1[i], crs1 );
    
                        for ( int j = 0; j < interior2.length; j++ ) {
                            cs2 = new LineStringImpl( interior2[j], crs2 );
    
                            if ( intersects( cs1, cs2 ) ) {
                                inter = true;
                                break;
                            }
                        }
    
                        if ( inter ) {
                            break;
                        }
                    }
                }
            }
        }

        return inter;
    }

    /**
     * the operations returns true if two the submitted points intersects
     */
    public static boolean intersects( Point point1, Point point2 ) {
        return intersects( point1.getPosition(), point2.getPosition() );
    }

    /**
     * the operations returns true if the submitted point intersects
     * the submitted curve
     */
    public static boolean intersects( Point point, Curve curve ) throws Exception {
        boolean inter = false;

        int cnt = curve.getNumberOfCurveSegments();

        for ( int i = 0; i < cnt; i++ ) {
            if ( intersects( point.getPosition(), curve.getCurveSegmentAt( i ) ) ) {
                inter = true;
                break;
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the submitted point intersects
     * the submitted surface
     */
    public static boolean intersects( Point point, Surface surface ) throws Exception {
        boolean inter = false;

        int cnt = surface.getNumberOfSurfacePatches();

        for ( int i = 0; i < cnt; i++ ) {
            if ( intersects( point.getPosition(), surface.getSurfacePatchAt( i ) ) ) {
                inter = true;
                break;
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the two submitted curves intersects
     */
    public static boolean intersects( Curve curve1, Curve curve2 ) throws Exception {
        boolean inter = false;
        int cnt1 = curve1.getNumberOfCurveSegments();
        int cnt2 = curve2.getNumberOfCurveSegments();

        for ( int i = 0; ( i < cnt1 ) && !inter; i++ ) {
            for ( int j = 0; j < cnt2; j++ ) {
                if ( intersects( curve1.getCurveSegmentAt( i ), curve2.getCurveSegmentAt( j ) ) ) {
                    inter = true;
                    break;
                }
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the submitted curve intersects
     * the submitted surface
     */
    public static boolean intersects( Curve curve, Surface surface ) throws Exception {
        boolean inter = false;
        int cnt1 = curve.getNumberOfCurveSegments();
        int cnt2 = surface.getNumberOfSurfacePatches();

        for ( int i = 0; i < cnt1; i++ ) {
            for ( int j = 0; j < cnt2; j++ ) {
                if ( intersects( curve.getCurveSegmentAt( i ), surface.getSurfacePatchAt( j ) ) ) {
                    inter = true;
                    break;
                }
            }

            if ( inter ) {
                break;
            }
        }

        return inter;
    }

    /**
     * the operation returns true if the two submitted surfaces intersects
     */
    public static boolean intersects( Surface surface1, Surface surface2 ) throws Exception {
        boolean inter = false;

        int cnt1 = surface1.getNumberOfSurfacePatches();
        int cnt2 = surface2.getNumberOfSurfacePatches();

        for ( int i = 0; i < cnt1; i++ ) {
            for ( int j = 0; j < cnt2; j++ ) {
                if ( intersects( surface1.getSurfacePatchAt( i ), surface2.getSurfacePatchAt( j ) ) ) {
                    inter = true;
                    break;
                }
            }

            if ( inter ) {
                break;
            }
        }

        return inter;
    }

    /**
     *
     *
     * @param X1 
     * @param Y1 
     * @param X2 
     * @param Y2 
     * @param PX 
     * @param PY 
     *
     * @return 
     */
    protected static int relativeCCW( double X1, double Y1, double X2, double Y2, double PX, double PY ) {
        X2 -= X1;
        Y2 -= Y1;
        PX -= X1;
        PY -= Y1;

        double ccw = ( PX * Y2 ) - ( PY * X2 );

        if ( ccw == 0.0 ) {
            ccw = ( PX * X2 ) + ( PY * Y2 );

            if ( ccw > 0.0 ) {
                PX -= X2;
                PY -= Y2;
                ccw = ( PX * X2 ) + ( PY * Y2 );

                if ( ccw < 0.0 ) {
                    ccw = 0.0;
                }
            }
        }

        return ( ccw < 0.0 ) ? ( -1 ) : ( ( ccw > 0.0 ) ? 1 : 0 );
    }

    /**
     * Tests if the line segment from (x1,&nbsp;y1) to
     * (x2,&nbsp;y2) intersects the line segment from (x3,&nbsp;y3)
     * to (x4,&nbsp;y4).
     * @return <code>true</code> if the first specified line segment
     *            and the second specified line segment intersect
     *            each other; <code>false</code> otherwise.
     */
    protected static boolean linesIntersect( double x1, double y1, double x2, double y2, double x3, 
                                      double y3, double x4, double y4 ) {
        return ( ( relativeCCW( x1, y1, x2, y2, x3, y3 ) * relativeCCW( x1, y1, x2, y2, x4, y4 ) <= 0 ) && 
               ( relativeCCW( x3, y3, x4, y4, x1, y1 ) * relativeCCW( x3, y3, x4, y4, x2, y2 ) <= 0 ) );
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LinearIntersects.java,v $
Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
