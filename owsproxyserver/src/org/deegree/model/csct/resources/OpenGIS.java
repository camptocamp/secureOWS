/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon GmbH
 http://www.lat-lon.de

 It has been implemented within SEAGIS - An OpenSource implementation of OpenGIS specification
 (C) 2001, Institut de Recherche pour le D�veloppement (http://sourceforge.net/projects/seagis/)
 SEAGIS Contacts:  Surveillance de l'Environnement Assist�e par Satellite
 Institut de Recherche pour le D�veloppement / US-Espace
 mailto:seasnet@teledetection.fr


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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.model.csct.resources;

// OpenGIS dependencies (SEAGIS)
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.deegree.model.csct.cs.AxisInfo;
import org.deegree.model.csct.cs.AxisOrientation;
import org.deegree.model.csct.cs.CompoundCoordinateSystem;
import org.deegree.model.csct.cs.CoordinateSystem;
import org.deegree.model.csct.cs.GeographicCoordinateSystem;
import org.deegree.model.csct.cs.HorizontalCoordinateSystem;
import org.deegree.model.csct.cs.HorizontalDatum;
import org.deegree.model.csct.cs.TemporalCoordinateSystem;
import org.deegree.model.csct.cs.TemporalDatum;
import org.deegree.model.csct.cs.VerticalCoordinateSystem;
import org.deegree.model.csct.cs.VerticalDatum;
import org.deegree.model.csct.ct.CoordinateTransformation;
import org.deegree.model.csct.ct.CoordinateTransformationFactory;
import org.deegree.model.csct.ct.MathTransform;
import org.deegree.model.csct.ct.MathTransform2D;
import org.deegree.model.csct.ct.TransformException;
import org.deegree.model.csct.pt.AngleFormat;
import org.deegree.model.csct.pt.CoordinatePoint;
import org.deegree.model.csct.pt.Envelope;
import org.deegree.model.csct.pt.Latitude;
import org.deegree.model.csct.pt.Longitude;
import org.deegree.model.csct.pt.MismatchedDimensionException;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * A set of static methods working on OpenGIS objects.  Some of those methods
 * are useful, but not really rigorous. This is why they do not appear in the
 * "official" package, but instead in this private one. <strong>Do not rely on
 * this API!</strong> It may change in incompatible way in any future version.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public final class OpenGIS {
    /**
     * Do not allow creation of
     * instances of this class.
     */
    private OpenGIS() {
    }

    /**
     * Returns the dimension of the first axis of a particular type.
     * For example, <code>getDimensionOf(cs,&nbsp;AxisInfo.TIME)</code>
     * would returns the dimension number of time axis.
     */
    public static int getDimensionOf( final CoordinateSystem cs, final AxisInfo axis ) {
        final int dimension = cs.getDimension();
        final AxisOrientation orientation = axis.orientation.absolute();
        for ( int i = 0; i < dimension; i++ )
            if ( orientation.equals( cs.getAxis( i ).orientation.absolute() ) )
                return i;
        return -1;
    }

    /**
     * Returns a two-dimensional coordinate system representing the two first dimensions
     * of the specified coordinate system. If <code>cs</code> is already a two-dimensional
     * coordinate system, then it is returned unchanged. Otherwise, if it is a
     * {@link CompoundCoordinateSystem}, then the head coordinate system is examined.
     *
     * @param  cs The coordinate system.
     * @return A two-dimensional coordinate system that represents the two first
     *         dimensions of <code>cs</code>.
     * @throws IllegalArgumentException if <code>cs</code> can't be reduced to
     *         a two-coordinate system.
     */
    public static CoordinateSystem getCoordinateSystem2D( CoordinateSystem cs )
                            throws IllegalArgumentException {
        if ( cs != null ) {
            while ( cs.getDimension() != 2 ) {
                if ( !( cs instanceof CompoundCoordinateSystem ) ) {
                    throw new IllegalArgumentException(
                                                        Resources.format(
                                                                          ResourceKeys.ERROR_CANT_REDUCE_TO_TWO_DIMENSIONS_$1,
                                                                          cs.getName( null ) ) );
                }
                cs = ( (CompoundCoordinateSystem) cs ).getHeadCS();
            }
        }
        return cs;
    }

    /**
     * Returns the first horizontal datum found in a coordinate system,
     * or <code>null</code> if there is none. Note: in a future version,
     * we may implement this method directly into {@link CoordinateSystem}
     * (not sure yet if it would be a good idea).
     */
    public static HorizontalDatum getHorizontalDatum( final CoordinateSystem cs ) {
        if ( cs instanceof HorizontalCoordinateSystem ) {
            return ( (HorizontalCoordinateSystem) cs ).getHorizontalDatum();
        }
        if ( cs instanceof CompoundCoordinateSystem ) {
            HorizontalDatum datum;
            final CompoundCoordinateSystem comp = (CompoundCoordinateSystem) cs;
            if ( ( datum = getHorizontalDatum( comp.getHeadCS() ) ) != null )
                return datum;
            if ( ( datum = getHorizontalDatum( comp.getTailCS() ) ) != null )
                return datum;
        }
        return null;
    }

    /**
     * Returns the first vertical datum found in a coordinate system,
     * or <code>null</code> if there is none. Note: if a future version,
     * we may implement this method directly into {@link CoordinateSystem}
     * (not sure yet if it would be a good idea).
     */
    public static VerticalDatum getVerticalDatum( final CoordinateSystem cs ) {
        if ( cs instanceof VerticalCoordinateSystem ) {
            return ( (VerticalCoordinateSystem) cs ).getVerticalDatum();
        }
        if ( cs instanceof CompoundCoordinateSystem ) {
            VerticalDatum datum;
            final CompoundCoordinateSystem comp = (CompoundCoordinateSystem) cs;
            if ( ( datum = getVerticalDatum( comp.getHeadCS() ) ) != null )
                return datum;
            if ( ( datum = getVerticalDatum( comp.getTailCS() ) ) != null )
                return datum;
        }
        return null;
    }

    /**
     * Returns the first temporal datum found in a coordinate system,
     * or <code>null</code> if there is none. Note: if a future version,
     * we may implement this method directly into {@link CoordinateSystem}
     * (not sure yet if it would be a good idea).
     */
    public static TemporalDatum getTemporalDatum( final CoordinateSystem cs ) {
        if ( cs instanceof TemporalCoordinateSystem ) {
            return ( (TemporalCoordinateSystem) cs ).getTemporalDatum();
        }
        if ( cs instanceof CompoundCoordinateSystem ) {
            TemporalDatum datum;
            final CompoundCoordinateSystem comp = (CompoundCoordinateSystem) cs;
            if ( ( datum = getTemporalDatum( comp.getHeadCS() ) ) != null )
                return datum;
            if ( ( datum = getTemporalDatum( comp.getTailCS() ) ) != null )
                return datum;
        }
        return null;
    }

    /**
     * Transform an envelope. The transformation is only approximative.
     *
     * @param  transform The transform to use.
     * @param  envelope Envelope to transform. This envelope will not be modified.
     * @return The transformed envelope. It may not have the same number of dimensions
     *         than the original envelope.
     * @throws TransformException if a transform failed.
     */
    public static Envelope transform( final MathTransform transform, final Envelope envelope )
                            throws TransformException {
        final int sourceDim = transform.getDimSource();
        final int targetDim = transform.getDimTarget();
        if ( envelope.getDimension() != sourceDim ) {
            throw new MismatchedDimensionException( sourceDim, envelope.getDimension() );
        }
        int coordinateNumber = 0;
        Envelope transformed = null;
        CoordinatePoint targetPt = null;
        final CoordinatePoint sourcePt = new CoordinatePoint( sourceDim );
        for ( int i = sourceDim; --i >= 0; )
            sourcePt.ord[i] = envelope.getMinimum( i );

        loop: do {
            // Transform a point and add the transformed
            // point to the destination envelope.
            targetPt = transform.transform( sourcePt, targetPt );
            if ( transformed != null )
                transformed.add( targetPt );
            else
                transformed = new Envelope( targetPt, targetPt );

            // Get the next point's coordinate.   The 'coordinateNumber' variable should
            // be seen as a number in base 3 where the number of digits is equals to the
            // number of dimensions. For example, a 4-D space would have numbers ranging
            // from "0000" to "2222". The digits are then translated into minimal, central
            // or maximal ordinates.
            int n = ++coordinateNumber;
            for ( int i = sourceDim; --i >= 0; ) {
                switch ( n % 3 ) {
                case 0:
                    sourcePt.ord[i] = envelope.getMinimum( i );
                    n /= 3;
                    break;
                case 1:
                    sourcePt.ord[i] = envelope.getCenter( i );
                    continue loop;
                case 2:
                    sourcePt.ord[i] = envelope.getMaximum( i );
                    continue loop;
                }
            }
            break;
        } while ( true );
        return transformed;
    }

    /**
     * Transform an envelope. The transformation is only approximative.
     * Invoking this method is equivalent to invoking the following:
     * <br>
     * <pre>transform(transform, new Envelope(source)).toRectangle2D()</pre>
     *
     * @param  transform The transform to use. Source and target dimension must be 2.
     * @param  source The rectangle to transform (may be <code>null</code>).
     * @param  dest  The destination rectangle (may be <code>source</code>).
     *         If <code>null</code>, a new rectangle will be created and returned.
     * @return <code>dest</code>, or a new rectangle if <code>dest</code> was non-null
     *         and <code>source</code> was null.
     * @throws TransformException if a transform failed.
     */
    public static Rectangle2D transform( final MathTransform2D transform, final Rectangle2D source,
                                        final Rectangle2D dest )
                            throws TransformException {
        if ( source == null ) {
            return null;
        }
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        final Point2D.Double point = new Point2D.Double();
        for ( int i = 0; i < 8; i++ ) {
            /*
             *   (0)----(5)----(1)
             *    |             |
             *   (4)           (7)
             *    |             |
             *   (2)----(6)----(3)
             */
            point.x = ( i & 1 ) == 0 ? source.getMinX() : source.getMaxX();
            point.y = ( i & 2 ) == 0 ? source.getMinY() : source.getMaxY();
            switch ( i ) {
            case 5: // fallthrough
            case 6:
                point.x = source.getCenterX();
                break;
            case 7: // fallthrough
            case 4:
                point.y = source.getCenterY();
                break;
            }
            transform.transform( point, point );
            if ( point.x < xmin )
                xmin = point.x;
            if ( point.x > xmax )
                xmax = point.x;
            if ( point.y < ymin )
                ymin = point.y;
            if ( point.y > ymax )
                ymax = point.y;
        }
        if ( dest != null ) {
            dest.setRect( xmin, ymin, xmax - xmin, ymax - ymin );
            return dest;
        }
        return new XRectangle2D( xmin, ymin, xmax - xmin, ymax - ymin );
    }

    /**
     * Retourne une cha�ne de caract�res repr�sentant la r�gion g�ographique sp�cifi�e. La
     * cha�ne retourn�e sera de la forme "45�00.00'N-50�00.00'N 30�00.00'E-40�00.00'E". Si
     * une projection cartographique est n�cessaire pour obtenir cette repr�sentation, elle
     * sera faite automatiquement. Cette cha�ne sert surtout � des fins de d�boguage et sa
     * forme peut varier.
     */
    public static String toWGS84String( final CoordinateSystem cs, Rectangle2D bounds ) {
        StringBuffer buffer = new StringBuffer();
        try {
            if ( !GeographicCoordinateSystem.WGS84.equivalents( cs ) ) {
                final CoordinateTransformation tr = CoordinateTransformationFactory.getDefault().createFromCoordinateSystems(
                                                                                                                              cs,
                                                                                                                              GeographicCoordinateSystem.WGS84 );
                bounds = transform( (MathTransform2D) tr.getMathTransform(), bounds, null );
            }
            final AngleFormat fmt = new AngleFormat( "DD�MM.m'" );
            buffer = fmt.format( new Latitude( bounds.getMinY() ), buffer, null );
            buffer.append( '-' );
            buffer = fmt.format( new Latitude( bounds.getMaxY() ), buffer, null );
            buffer.append( ' ' );
            buffer = fmt.format( new Longitude( bounds.getMinX() ), buffer, null );
            buffer.append( '-' );
            buffer = fmt.format( new Longitude( bounds.getMaxX() ), buffer, null );
        } catch ( TransformException exception ) {
            buffer.append( Utilities.getShortClassName( exception ) );
            final String message = exception.getLocalizedMessage();
            if ( message != null ) {
                buffer.append( ": " );
                buffer.append( message );
            }
        }
        return buffer.toString();
    }
}
