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

import java.io.Serializable;

import org.deegree.model.crs.CoordinateSystem;

/**
 * default implementation of the SurfacePatch interface from * package jago.model. the class is abstract because it should be * specialized by derived classes <code>Polygon</code> for example *  * ------------------------------------------------------------ * @version 11.6.2001 * @author Andreas Poth
 */

abstract class SurfacePatchImpl implements GenericSurface, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 7641735268892225180L;

    protected CoordinateSystem crs = null;
    protected Envelope envelope = null;
    protected Point centroid = null;
    protected SurfaceInterpolation interpolation = null;
    protected Position[] exteriorRing = null;
    protected Position[][] interiorRings = null;

    protected double area = 0;
    protected boolean valid = false;

    /**
     * Creates a new SurfacePatchImpl object.
     *
     * @param interpolation 
     * @param exteriorRing 
     * @param interiorRings 
     * @param crs 
     *
     * @throws GeometryException 
     */
    protected SurfacePatchImpl( SurfaceInterpolation interpolation, 
                                    Position[] exteriorRing, Position[][] interiorRings, 
                                    CoordinateSystem crs ) throws GeometryException {
        this.crs = crs;

        if ( ( exteriorRing == null ) || ( exteriorRing.length < 3 ) ) {
            throw new GeometryException( "The exterior ring doesn't contains enough point!" );
        }

        // check, if the exteriorRing of the polygon is closed
        // and if the interiorRings (if !=null) are closed
        if ( !exteriorRing[0].equals( exteriorRing[exteriorRing.length - 1] ) ) {
            throw new GeometryException( "The exterior ring isn't closed!" );
        }

        if ( interiorRings != null ) {
            for ( int i = 0; i < interiorRings.length; i++ ) {
                if ( !interiorRings[i][0].equals( interiorRings[i][interiorRings[i].length - 1] ) ) {
                    throw new GeometryException( "The interior ring " + i + " isn't closed!" );
                }
            }
        }

        this.interpolation = interpolation;
        this.exteriorRing = exteriorRing;
        this.interiorRings = interiorRings;

        setValid( false );
    }

    /**
     * invalidates the calculated parameters of the Geometry
     */
    protected void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * returns true if the calculated parameters of the Geometry are valid
     * and false if they must be recalculated
     */
    protected boolean isValid() {
        return valid;
    }


    /**
     *
     */
    private void calculateEnvelope() {
//        double[] min = new double[exteriorRing[0].getCoordinateDimension()];
//        for ( int j = 0; j < exteriorRing[0].getCoordinateDimension(); j++ ) {
//            min[j] = exteriorRing[0].getAsArray()[j];
//        }
        double[] min = exteriorRing[0].getAsArray().clone();
        
        double[] max = min.clone();

        for ( int i = 1; i < exteriorRing.length; i++ ) {
            double[] pos = exteriorRing[i].getAsArray();

            for ( int j = 0; j < exteriorRing[i].getCoordinateDimension(); j++ ) {
                if ( pos[j] < min[j] ) {
                    min[j] = pos[j];
                } else if ( pos[j] > max[j] ) {
                    max[j] = pos[j];
                }
            }
        }

        envelope = new EnvelopeImpl( new PositionImpl( min ), new PositionImpl( max ), crs );
        
    }

    /**
     * The interpolation determines the surface interpolation mechanism 
     * used for this SurfacePatch. This mechanism uses the control 
     * points and control parameters defined in the various subclasses 
     * to determine the position of this SurfacePatch.
     */
    public SurfaceInterpolation getInterpolation() {
        return interpolation;
    }

    /**
     * returns the bounding box of the surface patch
     */
    public Envelope getEnvelope() {
        if (!isValid()) {
            calculateParam();
        }
        return envelope;
    }

    /**
     * returns a reference to the exterior ring of the surface
     */
    public Position[] getExteriorRing() {
        return exteriorRing;
    }

    /**
     * returns a reference to the interior rings of the surface
     */
    public Position[][] getInteriorRings() {
        return interiorRings;
    }


    /**
     * returns the length of all boundaries of the surface
     * in a reference system appropriate for measuring distances.
     */
    public double getPerimeter() {
        return -1;
    }

    /**
     * returns the coordinate system of the surface patch
     */
    public CoordinateSystem getCoordinateSystem() {
        return crs;
    }

    /**
     *
     *
     * @param other 
     *
     * @return 
     */
    public boolean equals( Object other ) {
        if ( ( other == null ) || !( other instanceof SurfacePatchImpl ) ) {
            return false;
        }

        // Assuming Interpolation can be null (not checked by Constructor)
        if ( getInterpolation() != null ) {
            if ( !getInterpolation().equals( ( (SurfacePatch)other ).getInterpolation() ) ) {
                return false;
            }
        } else {
            if ( ( (SurfacePatch)other ).getInterpolation() != null ) {
                return false;
            }
        }

        // Assuming envelope cannot be null (always calculated)
        if ( !envelope.equals( ( (SurfacePatch)other ).getEnvelope() ) ) {
            return false;
        }

        // Assuming exteriorRing cannot be null (checked by Constructor)
        // if ( !Arrays.equals( exteriorRing, ( (SurfacePatch)other ).getExteriorRing() ) ) {
        // TODO 
        // correct comparing of each point considering current tolerance level
        // }

        // Assuming either can have interiorRings set to null (not checked
        //by Constructor)
        if ( interiorRings != null ) {
            if ( ( (SurfacePatch)other ).getInteriorRings() == null ) {
                return false;
            }

            if ( interiorRings.length != ( (SurfacePatch)other ).getInteriorRings().length ) {
                return false;
            }

            for ( int i = 0; i < interiorRings.length; i++ ) {
                //TODO 
                // correct comparing of each point considering current tolerance level
            }
        } else {
            if ( ( (SurfacePatch)other ).getInteriorRings() != null ) {
                return false;
            }
        }

        return true;
    }

    /**
     * The operation "centroid" shall return the mathematical centroid for this
     * Geometry. The result is not guaranteed to be on the object.
     */
    public Point getCentroid() {
        if (!isValid()) {
            calculateParam();
        }
        return centroid;
    }

    /**
     * The operation "area" shall return the area of this GenericSurface.
     * The area of a 2 dimensional geometric object shall be a numeric
     * measure of its surface area Since area is an accumulation (integral) 
     * of the product of two distances, its return value shall be in a unit
     * of measure appropriate for measuring distances squared.
     */
    public double getArea() {
        if (!isValid()) {
            calculateParam();
        }
        return area;
    }

    /**
     * calculates the centroid and area of the surface patch. this
     * method is only valid for the two-dimensional case.
     */
    private void calculateCentroidArea() {
        Position centroid_ = calculateCentroid( exteriorRing );
        
        double varea = calculateArea( exteriorRing );

        double x = centroid_.getX();
        double y = centroid_.getY();

        x *= varea;
        y *= varea;

        if ( interiorRings != null ) {
            for ( int i = 0; i < interiorRings.length; i++ ) {
                double dum = -1 * calculateArea( interiorRings[i] );
                Position temp = calculateCentroid( interiorRings[i] );
                x += ( temp.getX() * dum );
                y += ( temp.getY() * dum );
                varea += dum;
            }
        }

        area = varea;
        centroid = new PointImpl( x / varea, y / varea, crs );
                
    }

    /**
     * calculates the centroid and the area of the surface patch
     */
    protected void calculateParam() {
        calculateEnvelope();
        calculateCentroidArea();
        setValid( true );
    }

    /**
     * calculates the area of the surface patch <p></p> taken from gems iv  
     * (modified)<p></p> this method is only valid for the two-dimensional case.
     */
    private double calculateArea( Position[] point ) {
        int i;
        int j;
        double ai;
        double atmp = 0;

        for ( i = point.length - 1, j = 0; j < point.length; i = j, j++ ) {
        	double xi = point[i].getX() - point[0].getX();
        	double yi = point[i].getY() - point[0].getY();
        	double xj = point[j].getX() - point[0].getX();
        	double yj = point[j].getY() - point[0].getY();
            ai = ( xi * yj ) - ( xj * yi );
            atmp += ai;
        }

        return Math.abs( atmp / 2 );
    }

    /**
     * calculates the centroid of the surface patch <p> taken from gems iv  
     * (modified)<p></p> this method is only valid for the two-dimensional case.
     */
    protected Position calculateCentroid( Position[] point ) {

        int i;
        int j;
        double ai;
        double x;
        double y;
        double atmp = 0;
        double xtmp = 0;
        double ytmp = 0;

		// move points to the origin of the coordinate space
		// (to solve precision issues) 
		double transX = point [0].getX();
		double transY = point [0].getY();

		for ( i = point.length - 1, j = 0; j < point.length; i = j, j++ ) {
			double x1 = point[i].getX() - transX;
			double y1 = point[i].getY() - transY;
			double x2 = point[j].getX() - transX;
			double y2 = point[j].getY() - transY;
			ai = ( x1 * y2 ) - ( x2 * y1 );
			atmp += ai;
			xtmp += ( ( x2 + x1 ) * ai );
			ytmp += ( ( y2 + y1 ) * ai );
		}

		if ( atmp != 0 ) {
			x = xtmp / ( 3 * atmp ) + transX;
			y = ytmp / ( 3 * atmp ) + transY;
		} else {
			x = point[0].getX();
			y = point[0].getY();
		}

        return new PositionImpl( x, y );
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = "SurfacePatch: ";
        ret = "interpolation = " + interpolation + "\n";
        ret += "exteriorRing = \n";

        for ( int i = 0; i < exteriorRing.length; i++ ) {
            ret += ( exteriorRing + "\n" );
        }

        ret += ( "interiorRings = " + interiorRings + "\n" );
        ret += ( "envelope = " + envelope + "\n" );
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SurfacePatchImpl.java,v $
Revision 1.13  2006/11/27 09:07:51  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.12  2006/11/23 09:20:49  bezema
updated the copying of position values

Revision 1.11  2006/09/18 16:41:34  bezema
added the crs to the envelope creation

Revision 1.10  2006/08/28 14:22:56  poth
bug fix coordinate dimension

Revision 1.9  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
