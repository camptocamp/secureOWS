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

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.deegree.model.crs.CoordinateSystem;

/**
 * a boundingbox as child of a Polygon isn't part
 * of the iso19107 spec but it simplifies the geometry handling
 * 
 * <P>------------------------------------------------------------</P>
 * @author Andreas Poth href="mailto:poth@lat-lon.de"
 * @author Markus Bedel href="mailto:bedel@giub.uni-bonn.de"
 * @version $Id: EnvelopeImpl.java,v 1.15 2006/12/04 17:05:44 bezema Exp $
 */
public class EnvelopeImpl implements Envelope, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 1081219767894344990L;

    protected Position max = null;

    protected Position min = null;

    protected CoordinateSystem crs = null;

    /**
     * Creates a new EnvelopeImpl object.
     */
    protected EnvelopeImpl() {
        this.min = new PositionImpl();
        this.max = new PositionImpl();
    }

    /**
     * Creates a new EnvelopeImpl object.
     *
     * @param min 
     * @param max 
     */
    protected EnvelopeImpl( Position min, Position max ) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new EnvelopeImpl object.
     *
     * @param min 
     * @param max
     * @param crs  
     */
    protected EnvelopeImpl( Position min, Position max, CoordinateSystem crs ) {
        this.min = min;
        this.max = max;
        this.crs = crs;
    }

    /**
     *
     *
     * @return a shallow copy of this Envelope
     */
    @Override
    public Object clone() {
        return new EnvelopeImpl( (Position) ( (PositionImpl) min ).clone(),
                                 (Position) ( (PositionImpl) max ).clone(), this.crs );
    }

    /**
     * returns the spatial reference system of a geometry
     */
    public CoordinateSystem getCoordinateSystem() {
        return crs;
    }

    /**
     * returns the minimum coordinates of bounding box
     */
    public Position getMin() {
        return min;
    }

    /**
     * returns the maximum coordinates of bounding box
     */
    public Position getMax() {
        return max;
    }

    /**
     * returns the width of bounding box
     */
    public double getWidth() {
        return this.getMax().getX() - this.getMin().getX();
    }

    /**
     * returns the height of bounding box
     */
    public double getHeight() {
        return this.getMax().getY() - this.getMin().getY();
    }

    /**
     * returns true if the bounding box conatins the specified Point
     */
    public boolean contains( Position point ) {
        if ( ( point.getX() >= min.getX() ) && ( point.getX() <= max.getX() )
             && ( point.getY() >= min.getY() ) && ( point.getY() <= max.getY() ) ) {
            return true;
        }

        return false;
    }

    /**
     * returns true if this envelope and the submitted intersects
     */
    public boolean intersects( Envelope bb ) {
        // coordinates of this Envelope's BBOX
        double west1 = min.getX();
        double south1 = min.getY();
        double east1 = max.getX();
        double north1 = max.getY();

        // coordinates of the other Envelope's BBOX        
        double west2 = bb.getMin().getX();
        double south2 = bb.getMin().getY();
        double east2 = bb.getMax().getX();
        double north2 = bb.getMax().getY();

        // special cases: one box lays completly inside the other one
        if ( ( west1 <= west2 ) && ( south1 <= south2 ) && ( east1 >= east2 )
             && ( north1 >= north2 ) ) {
            return true;
        }

        if ( ( west1 >= west2 ) && ( south1 >= south2 ) && ( east1 <= east2 )
             && ( north1 <= north2 ) ) {
            return true;
        }

        // in any other case of intersection, at least one line of the BBOX has
        // to cross a line of the other BBOX
        // check western boundary of box 1
        // "touching" boxes must not intersect
        if ( ( west1 >= west2 ) && ( west1 < east2 ) ) {
            if ( ( south1 <= south2 ) && ( north1 > south2 ) ) {
                return true;
            }

            if ( ( south1 < north2 ) && ( north1 >= north2 ) ) {
                return true;
            }
        }

        // check eastern boundary of box 1
        // "touching" boxes must not intersect
        if ( ( east1 > west2 ) && ( east1 <= east2 ) ) {
            if ( ( south1 <= south2 ) && ( north1 > south2 ) ) {
                return true;
            }

            if ( ( south1 < north2 ) && ( north1 >= north2 ) ) {
                return true;
            }
        }

        // check southern boundary of box 1
        // "touching" boxes must not intersect
        if ( ( south1 >= south2 ) && ( south1 < north2 ) ) {
            if ( ( west1 <= west2 ) && ( east1 > west2 ) ) {
                return true;
            }

            if ( ( west1 < east2 ) && ( east1 >= east2 ) ) {
                return true;
            }
        }

        // check northern boundary of box 1
        // "touching" boxes must not intersect
        if ( ( north1 > south2 ) && ( north1 <= north2 ) ) {
            if ( ( west1 <= west2 ) && ( east1 > west2 ) ) {
                return true;
            }

            if ( ( west1 < east2 ) && ( east1 >= east2 ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * returns true if all points of the submitted
     * bounding box are within this bounding box
     */
    public boolean contains( Envelope bb ) {
        Position p1 = new PositionImpl( bb.getMin().getX(), bb.getMin().getY() );
        Position p2 = new PositionImpl( bb.getMin().getX(), bb.getMax().getY() );
        Position p3 = new PositionImpl( bb.getMax().getX(), bb.getMin().getY() );
        Position p4 = new PositionImpl( bb.getMax().getX(), bb.getMax().getY() );

        boolean ins = ( this.contains( p1 ) && this.contains( p2 ) && this.contains( p3 ) && this.contains( p4 ) );
        return ins;
    }

    /**
     * returns a new Envelope object representing the intersection of this
     * Envelope with the specified Envelope. * Note: If there is no
     * intersection at all Envelope will be null.
     * @param bb the Envelope to be intersected with this Envelope
     * @return the largest Envelope contained in both the specified Envelope
     * and in this Envelope.
     */
    public Envelope createIntersection( Envelope bb ) {
        Rectangle2D rect = new Rectangle2D.Double( bb.getMin().getX(), bb.getMin().getY(),
                                                   bb.getWidth(), bb.getHeight() );
        Rectangle2D rect2 = new Rectangle2D.Double( this.getMin().getX(), this.getMin().getY(),
                                                    this.getWidth(), this.getHeight() );

        if ( rect2.intersects( bb.getMin().getX(), bb.getMin().getY(), bb.getWidth(),
                               bb.getHeight() ) ) {
            rect = rect.createIntersection( rect2 );
        } else {
            rect = null;
        }

        if ( rect == null ) {
            return null;
        }

        double xmin = rect.getX();
        double ymin = rect.getY();
        double xmax = rect.getX() + rect.getWidth();
        double ymax = rect.getY() + rect.getHeight();

        Position p1 = new PositionImpl( xmin, ymin );
        Position p2 = new PositionImpl( xmax, ymax );

        return new EnvelopeImpl( p1, p2, this.crs );
    }

    /**
     * checks if this point is completly equal to the submitted geometry
     */
    @Override
    public boolean equals( Object other ) {
        if ( ( other == null ) || !( other instanceof EnvelopeImpl ) ) {
            return false;
        }
        Envelope envelope = (Envelope) other;
        if ( ( envelope.getCoordinateSystem() == null && getCoordinateSystem() != null )
             || ( envelope.getCoordinateSystem() != null && getCoordinateSystem() == null )
             || ( getCoordinateSystem() != null && !getCoordinateSystem().equals(
                                                                                  envelope.getCoordinateSystem() ) ) ) {
            return false;
        }

        return ( min.equals( ( (Envelope) other ).getMin() ) && max.equals( ( (Envelope) other ).getMax() ) );
    }

    public Envelope getBuffer( double b ) {
        Position bmin = new PositionImpl( new double[] { min.getX() - b, min.getY() - b } );
        Position bmax = new PositionImpl( new double[] { max.getX() + b, max.getY() + b } );
        return GeometryFactory.createEnvelope( bmin, bmax, getCoordinateSystem() );
    }

    /**
     * @see org.deegree.model.spatialschema.Envelope#merge(org.deegree.model.spatialschema.Envelope)
     */
    public Envelope merge( Envelope envelope )
                            throws GeometryException {

        CoordinateSystem crs1 = this.getCoordinateSystem();
        CoordinateSystem crs2 = envelope.getCoordinateSystem();

        if ( ( crs1 == null && crs2 != null ) || ( crs1 != null && crs2 == null )
             || ( crs1 != null && !crs1.equals( crs2 ) ) ) {
            String crs1Name = null;
            String crs2Name = null;
            if ( crs1 != null ) {
                crs1Name = crs1.getName();
            }
            if ( crs2 != null ) {
                crs2Name = crs2.getName();
            }
            throw new GeometryException( "Cannot merge envelopes with different CRS (" + crs1Name
                                         + "/" + crs2Name + ")!" );
        }
        double minx = min.getX();
        double miny = min.getY();
        double minz = min.getZ();
        double maxx = max.getX();
        double maxy = max.getY();
        double maxz = max.getZ();

        if ( envelope != null ) {
            if ( envelope.getMin().getX() < minx ) {
                minx = envelope.getMin().getX();
            }
            if ( envelope.getMin().getY() < miny ) {
                miny = envelope.getMin().getY();
            }
            if ( envelope.getMax().getX() > maxx ) {
                maxx = envelope.getMax().getX();
            }
            if ( envelope.getMax().getY() > maxy ) {
                maxy = envelope.getMax().getY();
            }
            if( !Double.isNaN( maxz ) && !Double.isNaN( envelope.getMax().getZ() ) ){
                if ( envelope.getMax().getZ() > maxz ) {
                    maxz = envelope.getMax().getZ();
                }
            } else if( Double.isNaN( maxz ) ){
                maxz = envelope.getMax().getZ();
            } 
            if( !Double.isNaN( minz ) && !Double.isNaN( envelope.getMin().getZ() ) ){
                if ( envelope.getMin().getZ() < minz ) {
                    minz = envelope.getMin().getZ();
                }
            } else if( Double.isNaN( minz ) ){
                minz = envelope.getMin().getZ();
            } 
            
        }
        Position minPos = GeometryFactory.createPosition( minx, miny, minz );
        Position maxPos = GeometryFactory.createPosition( maxx, maxy, maxz );
        return GeometryFactory.createEnvelope( minPos, maxPos, this.getCoordinateSystem() );
    }

    /**
     * ensures that the passed Envepole is contained within this.Envelope
     * @param other
     */
    public void expandToContain( Envelope other ) {
        double minx = min.getX();
        double miny = min.getY();
        double maxx = max.getX();
        double maxy = max.getY();
        if ( other.getMin().getX() < minx ) {
            minx = other.getMin().getX();
        }
        if ( other.getMax().getX() > maxx ) {
            maxx = other.getMax().getX();
        }
        if ( other.getMin().getY() < miny ) {
            miny = other.getMin().getY();
        }
        if ( other.getMax().getY() > maxy ) {
            maxy = other.getMax().getY();
        }
        min = new PositionImpl( minx, miny );
        max = new PositionImpl( maxx, maxy );
    }

    /**
     * translate a envelope in the direction defined by the two passed
     * values and retiurns the resulting envelope
     * @param x
     * @param y
     */
    public Envelope translate( double x, double y ) {
        min = new PositionImpl( this.getMin().getX() + x, this.getMin().getY() + y );
        max = new PositionImpl( this.getMax().getX() + x, this.getMax().getY() + y );
        return new EnvelopeImpl( min, max, this.crs );
    }

    @Override
    public String toString() {
        String ret = "min = " + min;
        ret += ( " max = " + max );
        return ret;
    }

}

/*
 * Changes to this class. What the people haven been up to:
 *
 * $Log: EnvelopeImpl.java,v $
 * Revision 1.15  2006/12/04 17:05:44  bezema
 * removed _
 *
 * Revision 1.14  2006/12/04 17:02:13  bezema
 * removed the null statement
 *
 * Revision 1.13  2006/11/23 09:13:02  bezema
 * Made the merge operetion 3d compatible
 *
 * Revision 1.12  2006/11/02 10:20:17  mschneider
 * Fixed #merge(Envelope). Changed visibility of constructors to package protected.
 *
 * Revision 1.11  2006/10/31 13:57:42  mschneider
 * Improved error message when merging envelopes that use different CRS.
 *
 * Revision 1.10  2006/09/18 14:11:09  bezema
 * added annotations and javadoc
 *
 * Revision 1.9  2006/07/05 12:57:05  poth
 * toString method corrected (line break removed)
 *
 * Revision 1.8  2006/05/31 12:59:36  poth
 * translate method added
 *
 * Revision 1.7  2006/05/01 20:15:25  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/04/06 20:25:22  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/04 20:39:41  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:23  poth
 * *** empty log message ***
 *
 * Revision 1.3  2005/10/23 20:53:56  poth
 * no message
 *
 * Revision 1.2  2005/01/18 22:08:54  poth
 * no message
 *
 * Revision 1.3  2004/09/01 08:06:03  ap
 * no message
 *
 * Revision 1.2  2004/08/12 10:39:32  ap
 * no message
 *
 * Revision 1.1  2004/06/28 06:39:46  ap
 * no message
 *
 * Revision 1.1  2004/05/24 07:05:33  ap
 * no message
 *
 * Revision 1.13  2004/03/02 07:38:14  poth
 * no message
 *
 * Revision 1.12  2004/02/23 07:47:50  poth
 * no message
 *
 * Revision 1.11  2004/01/27 07:55:44  poth
 * no message
 *
 * Revision 1.10  2004/01/08 09:50:22  poth
 * no message
 *
 * Revision 1.9  2003/09/14 14:05:08  poth
 * no message
 *
 * Revision 1.8  2003/07/10 15:24:23  mrsnyder
 * Started to implement LabelDisplayElements that are bound to a Polygon.
 * Fixed error in MultiSurfaceImpl.calculateCentroidArea().
 *
 * Revision 1.7  2003/07/03 12:32:26  poth
 * no message
 *
 * Revision 1.6  2003/03/20 12:10:29  mrsnyder
 * Rewrote intersects() method.
 *
 * Revision 1.5  2003/03/19 15:30:04  axel_schaefer
 * Intersects: crossing envelopes, but points are not in envelope
 *
 *
 */
