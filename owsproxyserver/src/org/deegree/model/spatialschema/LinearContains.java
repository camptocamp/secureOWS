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

import java.util.ArrayList;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/08/08 09:17:51 $
 *
 * @since 2.0
 */
class LinearContains {
    /**
     * the operations returns true if two the submitted points contains
     */
    public static boolean contains( Position point1, Position point2 ) {
        throw new UnsupportedOperationException( "contains(Position, Position)" + 
                                         " not supported at the moment." );
    }

    /**
     * the operations returns true if the submitted point contains
     * the submitted curve segment
     */
    public static boolean contains( CurveSegment curve, Position point ) {
        throw new UnsupportedOperationException( "contains(CurveSegment, Position)" + 
                                         " not supported at the moment." );
    }

    /**
     * the operation returns true if the submitted point contains
     * the submitted surface patch
     */
    public static boolean contains( SurfacePatch surface, Position point ) {
        boolean con = false;
        Position[] ex = surface.getExteriorRing();
        con = contains( ex, point );

        if ( con ) {
            Position[][] inner = surface.getInteriorRings();

            if ( inner != null ) {
                for ( int i = 0; i < inner.length; i++ ) {
                    if ( contains( inner[i], point ) ) {
                        con = false;
                        break;
                    }
                }
            }
        }

        return con;
    }

    /**
     * the operation returns true if the two submitted curves segments contains
     */
    public static boolean contains( CurveSegment curve1, CurveSegment curve2 ) {
        throw new UnsupportedOperationException( "contains(CurveSegment, CurveSegment)" + 
                                         " not supported at the moment." );
    }

    /**
     * the operation returns true if the submitted curve segment contains
     * the submitted surface patch
     */
    public static boolean contains( SurfacePatch surface, CurveSegment curve ) {
        boolean con = true;
        Position[] ex = surface.getExteriorRing();
        Position[] cu = curve.getPositions();

        for ( int i = 0; i < cu.length; i++ ) {
            if ( !contains( ex, cu[i] ) ) {
                con = false;
                break;
            }
        }

        if ( con ) {
            Position[][] inner = surface.getInteriorRings();

            if ( inner != null ) {
                for ( int i = 0; i < inner.length; i++ ) {
                    for ( int j = 0; j < cu.length; j++ ) {
                        if ( contains( inner[i], cu[j] ) ) {
                            con = false;
                            break;
                        }
                    }

                    if ( !con ) {
                        break;
                    }
                }
            }
        }

        return con;
    }

    /**
     * the operation returns true if the first surface patches contains
     * the second one
     */
    public static boolean contains( SurfacePatch surface1, SurfacePatch surface2 ) {
        boolean con = true;
        Position[] ex = surface1.getExteriorRing();
        Position[] ex_ = surface2.getExteriorRing();

        for ( int i = 0; i < ex_.length; i++ ) {
            if ( !contains( ex, ex_[i] ) ) {
                con = false;
                break;
            }
        }

        if ( con ) {
            Position[][] inner = surface1.getInteriorRings();
            Position[][] inner_ = surface2.getInteriorRings();

            if ( inner != null ) {
                for ( int i = 0; i < inner.length; i++ ) {
                    // a point of the second exterior is not allowed to be
                    // within a inner ring of the first
                    for ( int j = 0; j < ex_.length; j++ ) {
                        if ( contains( inner[i], ex_[j] ) ) {
                            con = false;
                            break;
                        }
                    }

                    if ( !con ) {
                        break;
                    }

                    // a point of the inner rings of the second is not allowed
                    // to be within a inner ring of the first
                    if ( inner_ != null ) {
                        for ( int k = 0; k < inner_.length; k++ ) {
                            for ( int j = 0; j < inner_[k].length; j++ ) {
                                if ( contains( inner[i], inner_[k][j] ) ) {
                                    con = false;
                                    break;
                                }
                            }

                            if ( !con ) {
                                break;
                            }
                        }
                    }

                    // a point of the inner rings of the first is not allowed
                    // to be within the second surface
                    for ( int j = 0; j < inner[i].length; j++ ) {
                        if ( contains( surface2, inner[i][j] ) ) {
                            con = false;
                            break;
                        }
                    }

                    if ( !con ) {
                        break;
                    }
                }
            }
        }

        // surface2 is not allowed to contain one point of surface1
        if ( con ) {
            for ( int i = 0; i < ex.length; i++ ) {
                if ( contains( surface2, ex[i] ) ) {
                    con = false;
                    break;
                }
            }
        }

        return con;
    }

    /**
     * the operations returns true if two the submitted points contains
     */
    public static boolean contains( Point point1, Point point2 ) {
        throw new UnsupportedOperationException( "contains(Point, Point)" + 
                                         " not supported at the moment." );
    }

    /**
     * the operations returns true if the submitted point contains
     * the submitted curve
     */
    public static boolean contains( Curve curve, Point point ) {
        throw new UnsupportedOperationException( "contains(Curve, Point)" + 
                                         " not supported at the moment." );
    }

    /**
     * the operation returns true if the submitted point contains
     * the submitted surface
     */
    public static boolean contains( Surface surface, Point point ) throws Exception {
        boolean contain = false;
        int cnt = surface.getNumberOfSurfacePatches();

        for ( int i = 0; i < cnt; i++ ) {
            if ( contains( surface.getSurfacePatchAt( i ), point.getPosition() ) ) {
                contain = true;
                break;
            }
        }

        return contain;
    }

    /**
     * the operation returns true if the two submitted curves contains
     */
    public static boolean contains( Curve curve1, Curve curve2 )  {
        throw new UnsupportedOperationException( "contains(Curve, Curve)" + 
                                         " not supported at the moment." );
    }

    /**
     * Convenience method to extract all <tt>Position</tt>s from a
     * <tt>Curve</tt>.
     */
    private static Position[] getPositions( Curve curve ) throws GeometryException {
        ArrayList positions = new ArrayList(1000);

        for ( int i = 0; i < curve.getNumberOfCurveSegments(); i++ ) {
            CurveSegment segment = curve.getCurveSegmentAt( i );
            Position[] segmentPos = segment.getPositions();

            for ( int j = 0; j < segmentPos.length; j++ )
                positions.add( segmentPos[j] );
        }

        return (Position[])positions.toArray();
    }

    /**
     * the operation returns true if the submitted curve contains
     * the submitted surface
     */
    public static boolean contains( Surface surface, Curve curve ) throws GeometryException {
        // gather the positions of the crings (exterior and interior) and 
        // the curve as arrays of Positions
        SurfaceBoundary boundary = (SurfaceBoundary)surface.getBoundary();
        Ring extRing = boundary.getExteriorRing();
        Ring[] intRings = boundary.getInteriorRings();

        Position[] curvePos = getPositions( curve );
        Position[] extRingPos = extRing.getPositions();
        Position[][] intRingsPos = new Position[intRings.length][];

        for ( int i = 0; i < intRings.length; i++ )
            intRingsPos[i] = intRings[i].getPositions();

        // necessary condition: all points of the curve have to be inside
        // of the surface's exterior ring and none must be inside of one
        // of the interior rings
        for ( int i = 0; i < curvePos.length; i++ ) {
            if ( !contains( extRingPos, curvePos[i] ) ) {
                return false;
            }

            for ( int j = 0; j < intRings.length; j++ ) {
                if ( contains( intRingsPos[j], curvePos[i] ) ) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * the operation returns true if the two submitted surfaces contains
     */
    public static boolean contains( Surface surface2, Surface surface1 ) throws Exception {
        return contains( surface2.getSurfacePatchAt( 0 ), surface1.getSurfacePatchAt( 0 ) );
    }

    /**
     * the operation returns true if polygon defined by an array of Position
     * contains the submitted point.
     */
    protected static boolean contains( Position[] positions, Position point ) {
        if ( positions.length <= 2 ) {
            return false;
        }

        int hits = 0;

        double lastx = positions[positions.length - 1].getX();
        double lasty = positions[positions.length - 1].getY();
        double curx;
        double cury;

        // Walk the edges of the polygon
        for ( int i = 0; i < positions.length; lastx = curx, lasty = cury, i++ ) {
            curx = positions[i].getX();
            cury = positions[i].getY();

            if ( cury == lasty ) {
                continue;
            }

            double leftx;

            if ( curx < lastx ) {
                if ( point.getX() >= lastx ) {
                    continue;
                }

                leftx = curx;
            } else {
                if ( point.getX() >= curx ) {
                    continue;
                }

                leftx = lastx;
            }

            double test1;
            double test2;

            if ( cury < lasty ) {
                if ( ( point.getY() < cury ) || ( point.getY() >= lasty ) ) {
                    continue;
                }

                if ( point.getX() < leftx ) {
                    hits++;
                    continue;
                }

                test1 = point.getX() - curx;
                test2 = point.getY() - cury;
            } else {
                if ( ( point.getY() < lasty ) || ( point.getY() >= cury ) ) {
                    continue;
                }

                if ( point.getX() < leftx ) {
                    hits++;
                    continue;
                }

                test1 = point.getX() - lastx;
                test2 = point.getY() - lasty;
            }

            if ( test1 < ( test2 / ( lasty - cury ) * ( lastx - curx ) ) ) {
                hits++;
            }
        }

        return ( ( hits & 1 ) != 0 );
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LinearContains.java,v $
Revision 1.6  2006/08/08 09:17:51  poth
NoSuchMethodException substituted by UnsupportedOperation exception

Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
