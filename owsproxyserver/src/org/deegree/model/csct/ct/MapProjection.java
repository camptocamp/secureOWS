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
package org.deegree.model.csct.ct;

// Coordinates
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Locale;

import javax.media.jai.ParameterList;

import org.deegree.model.csct.cs.Projection;
import org.deegree.model.csct.pt.Latitude;
import org.deegree.model.csct.pt.Longitude;
import org.deegree.model.csct.resources.Geometry;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * Provides transformation services between ellipsoidal and cartographic
 * projections. Ellipsoidal height values remain unchanged.
 *
 *
 * @version 1.0
 * @author Andr� Gosselin
 * @author Martin Desruisseaux
 */
abstract class MapProjection extends AbstractMathTransform implements MathTransform2D {


    /**
     * Marge de tol�rance pour les comparaisons de nombre r�els.
     */
    static final double EPS = 1.0E-6;

    /**
     * Marge de tol�rance pour les calculs it�ratifs.
     */
    static final double TOL = 1E-10;

    /**
     * Classification string for this projection
     * (e.g. "Transverse_Mercator").
     */
    private final String classification;

    /**
     * Indique si le mod�le terrestre est sph�rique. La valeur <code>true</code>
     * indique que le mod�le est sph�rique, c'est-�-dire que les champs {@link #a}
     * et {@link #b} ont la m�me valeur.
     */
    protected final boolean isSpherical;

    /**
     * Excentricit� de l'ellipse. L'excentricit� est 0
     * si l'ellipso�de est sph�rique, c'est-�-dire si
     * {@link #isSpherical} est <code>true</code>.
     */
    protected final double e;

    /**
     * Carr� de l'excentricit� de l'ellipse: e� = (a�-b�)/a�.
     */
    protected final double es;

    /**
     * Longueur de l'axe majeur de la terre, en m�tres.
     * Sa valeur par d�faut d�pend de l'�llipso�de par
     * d�faut (par exemple "WGS 1984").
     */
    protected final double a;

    /**
     * Longueur de l'axe mineur de la terre, en m�tres.
     * Sa valeur par d�faut d�pend de l'�llipso�de par
     * d�faut (par exemple "WGS 1984").
     */
    protected final double b;

    /**
     * Central longitude in <u>radians</u>.  Default value is 0, the Greenwich
     * meridian. <strong>Consider this field as final</strong>. It is not final
     * only  because {@link TransverseMercatorProjection} need to modify it at
     * construction time.
     */
    protected double centralMeridian;

    /**
     * Central latitude in <u>radians</u>. Default value is 0, the equator.
     * <strong>Consider this field as final</strong>. It is not final only
     * because some class need to modify it at construction time.
     */
    protected double centralLatitude;

    protected double false_easting;

    protected double false_northing;

    /**
     * The inverse of this map projection.
     * Will be created only when needed.
     */
    private transient MathTransform inverse;

    /**
     * Construct a new map projection from the suplied parameters.
     *
     * @param  parameters The parameter values in standard units.
     *         The following parameter are recognized:
     *         <ul>
     *           <li>"semi_major"   (default to WGS 1984)</li>
     *           <li>"semi_minor"   (default to WGS 1984)</li>
     *           <li>"central_meridian"   (default to 0�)</li>
     *           <li>"latitude_of_origin" (default to 0�)</li>
     *         </ul>
     * @throws MissingParameterException if a mandatory parameter is missing.
     */
    protected MapProjection( final Projection parameters ) throws MissingParameterException {
        this.classification = parameters.getClassName();
        this.a = parameters.getValue( "semi_major" );
        this.b = parameters.getValue( "semi_minor" );
        this.centralMeridian = longitudeToRadians( parameters.getValue( "central_meridian", 0 ),
                                                   true );
        this.centralLatitude = latitudeToRadians( parameters.getValue( "latitude_of_origin", 0 ),
                                                  true );
        this.isSpherical = ( a == b );
        this.es = 1.0 - ( b * b ) / ( a * a );
        this.e = Math.sqrt( es );

        false_easting = parameters.getValue( "false_easting" );
        false_northing = parameters.getValue( "false_northing" );

    }

    /**
     * Returns a human readable name localized for the specified locale.
     */
    public abstract String getName( final Locale locale );

    /**
     * Gets the dimension of input points.
     */
    public final int getDimSource() {
        return 2;
    }

    /**
     * Gets the dimension of output points.
     */
    public final int getDimTarget() {
        return 2;
    }

    /**
     * Convertit en radians une longitude exprim�e en degr�s. Au passage, cette m�thode v�rifiera
     * si la longitude est bien dans les limites permises (�180�). Cette m�thode est utile pour
     * v�rifier la validit� des param�tres de la projection, comme {@link #setCentralLongitude}.
     *
     * @param  x Longitude � v�rifier, en degr�s.
     * @param  edge <code>true</code> pour accepter les longitudes de �180�.
     * @return Longitude en radians.
     * @throws IllegalArgumentException si la longitude est invalide.
     */
    static double longitudeToRadians( final double x, boolean edge )
                            throws IllegalArgumentException {
        if ( edge ? ( x >= Longitude.MIN_VALUE && x <= Longitude.MAX_VALUE )
                 : ( x > Longitude.MIN_VALUE && x < Longitude.MAX_VALUE ) ) {
            return Math.toRadians( x );
        }
        throw new IllegalArgumentException(
                                            Resources.format(
                                                              ResourceKeys.ERROR_LONGITUDE_OUT_OF_RANGE_$1,
                                                              new Longitude( x ) ) );
    }

    /**
     * Convertit en radians une latitude exprim�e en degr�s. Au passage, cette m�thode v�rifiera
     * si la latitude est bien dans les limites permises (�90�). Cette m�thode est utile pour
     * v�rifier la validit� des param�tres de la projection, comme {@link #setCentralLongitude}.
     *
     * @param  y Latitude � v�rifier, en degr�s.
     * @param  edge <code>true</code> pour accepter les latitudes de �90�.
     * @return Latitude en radians.
     * @throws IllegalArgumentException si la latitude est invalide.
     */
    static double latitudeToRadians( final double y, boolean edge )
                            throws IllegalArgumentException {
        if ( edge ? ( y >= Latitude.MIN_VALUE && y <= Latitude.MAX_VALUE )
                 : ( y > Latitude.MIN_VALUE && y < Latitude.MAX_VALUE ) ) {
            return Math.toRadians( y );
        }
        throw new IllegalArgumentException(
                                            Resources.format(
                                                              ResourceKeys.ERROR_LATITUDE_OUT_OF_RANGE_$1,
                                                              new Latitude( y ) ) );
    }

 
    //////////////////////////////////////////////////////////////////////
    ////                                                              ////
    ////                          TRANSFORMS                          ////
    ////                                                              ////
    //////////////////////////////////////////////////////////////////////
    /**
     * Transforms the specified coordinate and stores the result in <code>ptDst</code>.
     * This method is guaranteed to be invoked with values of <var>x</var> in the range
     * <code>[-PI..PI]</code> and values of <var>y</var> in the range <code>[-PI/2..PI/2]</code>.
     *
     * @param x     The longitude of the coordinate, in <strong>radians</strong>.
     * @param y     The  latitude of the coordinate, in <strong>radians</strong>.
     * @param ptDst the specified coordinate point that stores the
     *              result of transforming <code>ptSrc</code>, or
     *              <code>null</code>. Ordinates will be in metres.
     * @return the coordinate point after transforming <code>ptSrc</code>
     *         and stroring the result in <code>ptDst</code>.
     * @throws TransformException if the point can't be transformed.
     */
    protected abstract Point2D transform( double x, double y, final Point2D ptDst )
                            throws TransformException;

    /**
     * Transforms the specified <code>ptSrc</code>
     * and stores the result in <code>ptDst</code>.
     *
     * @param ptSrc the specified coordinate point to be transformed.
     *              Ordinates must be in degrees.
     * @param ptDst the specified coordinate point that stores the
     *              result of transforming <code>ptSrc</code>, or
     *              <code>null</code>. Ordinates will be in metres.
     * @return the coordinate point after transforming <code>ptSrc</code>
     *         and stroring the result in <code>ptDst</code>.
     * @throws TransformException if the point can't be transformed.
     */
    public final Point2D transform( final Point2D ptSrc, Point2D ptDst )
                            throws TransformException {
        final double x = ptSrc.getX();
        final double y = ptSrc.getY();
        if ( !( x >= Longitude.MIN_VALUE && x <= Longitude.MAX_VALUE ) ) {
            throw new TransformException(
                                          Resources.format(
                                                            ResourceKeys.ERROR_LONGITUDE_OUT_OF_RANGE_$1,
                                                            new Longitude( x ) ) );
        }
        if ( !( y >= Latitude.MIN_VALUE && y <= Latitude.MAX_VALUE ) ) {
            throw new TransformException(
                                          Resources.format(
                                                            ResourceKeys.ERROR_LATITUDE_OUT_OF_RANGE_$1,
                                                            new Latitude( y ) ) );
        }
        ptDst = transform( Math.toRadians( x ), Math.toRadians( y ), ptDst );
        return ptDst;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     * Ordinates must be (<var>longitude</var>,<var>latitude</var>)
     * pairs in degrees.
     *
     * @throws TransformException if a point can't be transformed. This method try
     *         to transform every points even if some of them can't be transformed.
     *         Non-transformable points will have value {@link Double#NaN}. If more
     *         than one point can't be transformed, then this exception may be about
     *         an arbitrary point.
     */
    public final void transform( final double[] src, int srcOffset, final double[] dest,
                                int dstOffset, int numPts )
                            throws TransformException {
        /*
         * V�rifie s'il faudra parcourir le tableau en sens inverse.
         * Ce sera le cas si les tableaux source et destination se
         * chevauchent et que la destination est apr�s la source.
         */
        final boolean reverse = ( src == dest && srcOffset < dstOffset && srcOffset + ( 2 * numPts ) > dstOffset );
        if ( reverse ) {
            srcOffset += 2 * numPts;
            dstOffset += 2 * numPts;
        }
        final Point2D.Double point = new Point2D.Double();
        TransformException firstException = null;
        while ( --numPts >= 0 ) {
            try {
                point.x = src[srcOffset++];
                point.y = src[srcOffset++];
                transform( point, point );
                dest[dstOffset++] = point.x;
                dest[dstOffset++] = point.y;
            } catch ( TransformException exception ) {
                dest[dstOffset++] = Double.NaN;
                dest[dstOffset++] = Double.NaN;
                if ( firstException == null ) {
                    firstException = exception;
                }
            }
            if ( reverse ) {
                srcOffset -= 4;
                dstOffset -= 4;
            }
        }
        if ( firstException != null )
            throw firstException;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     * Ordinates must be (<var>longitude</var>,<var>latitude</var>)
     * pairs in degrees.
     *
     * @throws TransformException if a point can't be transformed. This method try
     *         to transform every points even if some of them can't be transformed.
     *         Non-transformable points will have value {@link Float#NaN}. If more
     *         than one point can't be transformed, then this exception may be about
     *         an arbitrary point.
     */
    public final void transform( final float[] src, int srcOffset, final float[] dest,
                                int dstOffset, int numPts )
                            throws TransformException {
        final boolean reverse = ( src == dest && srcOffset < dstOffset && srcOffset
                                                                          + ( numPts << 1 ) > dstOffset );
        if ( reverse ) {
            srcOffset += 2 * numPts;
            dstOffset += 2 * numPts;
        }
        final Point2D.Double point = new Point2D.Double();
        TransformException firstException = null;
        while ( --numPts >= 0 ) {
            try {
                point.x = src[srcOffset++];
                point.y = src[srcOffset++];
                transform( point, point );
                dest[dstOffset++] = (float) point.x;
                dest[dstOffset++] = (float) point.y;
            } catch ( TransformException exception ) {
                dest[dstOffset++] = Float.NaN;
                dest[dstOffset++] = Float.NaN;
                if ( firstException == null ) {
                    firstException = exception;
                }
            }
            if ( reverse ) {
                srcOffset -= 4;
                dstOffset -= 4;
            }
        }
        if ( firstException != null )
            throw firstException;
    }

    /**
     * Transforme la forme g�om�trique <code>shape</code> sp�cifi�e.
     * Cette projection peut remplacer certaines lignes droites
     * par des courbes. Tous les points de la forme g�om�trique
     * seront copi�s. Cette m�thode n'est donc pas � conseiller
     * si <code>shape</code> est volumineux, par exemple s'il
     * repr�sente une bathym�trie enti�re.
     *
     * @param shape Forme g�om�trique � transformer. Les coordonn�es des points
     *              de cette forme doivent �tre exprim�es en degr�s de latitudes
     *              et de longitudes.
     * @return      Forme g�om�trique transform�e. Les coordonn�es des points de
     *              cette forme seront exprim�es en m�tres.
     * @throws TransformException si une transformation a �chou�e.
     */
    public final Shape createTransformedShape( final Shape shape )
                            throws TransformException {
        return createTransformedShape( shape, null, null, Geometry.HORIZONTAL );
    }

    //////////////////////////////////////////////////////////////////////
    ////                                                              ////
    ////                      INVERSE TRANSFORMS                      ////
    ////                                                              ////
    //////////////////////////////////////////////////////////////////////

    /**
     * Transforms the specified coordinate and stores the result in <code>ptDst</code>.
     * This method shall returns <var>x</var> values in the range <code>[-PI..PI]</code>
     * and <var>y</var> values in the range <code>[-PI/2..PI/2]</code>. It will be checked
     * by the caller, so this method doesn't need to performs this check.
     *
     * @param x     The longitude of the coordinate, in metres.
     * @param y     The  latitude of the coordinate, in metres.
     * @param ptDst the specified coordinate point that stores the
     *              result of transforming <code>ptSrc</code>, or
     *              <code>null</code>. Ordinates will be in <strong>radians</strong>.
     * @return the coordinate point after transforming <code>ptSrc</code>
     *         and stroring the result in <code>ptDst</code>.
     * @throws TransformException if the point can't be transformed.
     */
    protected abstract Point2D inverseTransform( double x, double y, final Point2D ptDst )
                            throws TransformException;

    /**
     * Inverse transforms the specified <code>ptSrc</code>
     * and stores the result in <code>ptDst</code>.
     *
     * @param ptSrc the specified coordinate point to be transformed.
     *              Ordinates must be in metres.
     * @param ptDst the specified coordinate point that stores the
     *              result of transforming <code>ptSrc</code>, or
     *              <code>null</code>. Ordinates will be in degrees.
     * @return the coordinate point after transforming <code>ptSrc</code>
     *         and stroring the result in <code>ptDst</code>.
     * @throws TransformException if the point can't be transformed.
     */
    public final Point2D inverseTransform( final Point2D ptSrc, Point2D ptDst )
                            throws TransformException {
        final double x0 = ptSrc.getX();
        final double y0 = ptDst.getY();
        ptDst = inverseTransform( x0, y0, ptDst );
        final double x = Math.toDegrees( ptDst.getX() );
        final double y = Math.toDegrees( ptDst.getY() );
        ptDst.setLocation( x, y );
        if ( !( x >= Longitude.MIN_VALUE && x <= Longitude.MAX_VALUE ) ) {
            throw new TransformException(
                                          Resources.format(
                                                            ResourceKeys.ERROR_LONGITUDE_OUT_OF_RANGE_$1,
                                                            new Longitude( x ) ) );
        }
        if ( !( y >= Latitude.MIN_VALUE && y <= Latitude.MAX_VALUE ) ) {
            throw new TransformException(
                                          Resources.format(
                                                            ResourceKeys.ERROR_LATITUDE_OUT_OF_RANGE_$1,
                                                            new Latitude( y ) ) );
        }
        return ptDst;
    }

    /**
     * Inverse transforms a list of coordinate point ordinal values.
     * Ordinates must be (<var>x</var>,<var>y</var>) pairs in metres.
     *
     * @throws TransformException if a point can't be transformed. This method try
     *         to transform every points even if some of them can't be transformed.
     *         Non-transformable points will have value {@link Double#NaN}. If more
     *         than one point can't be transformed, then this exception may be about
     *         an arbitrary point.
     */
    public final void inverseTransform( final double[] src, int srcOffset, final double[] dest,
                                       int dstOffset, int numPts )
                            throws TransformException {
        /*
         * V�rifie s'il faudra parcourir le tableau en sens inverse.
         * Ce sera le cas si les tableaux source et destination se
         * chevauchent et que la destination est apr�s la source.
         */
        final boolean reverse = ( src == dest && srcOffset < dstOffset && srcOffset
                                                                          + ( numPts << 1 ) > dstOffset );
        if ( reverse ) {
            srcOffset += src.length * numPts;
            dstOffset += src.length * numPts;
        }
        final Point2D.Double point = new Point2D.Double();
        TransformException firstException = null;
        while ( --numPts >= 0 ) {
            try {
                point.x = src[srcOffset++];
                point.y = src[srcOffset++];
                inverseTransform( point, point );
                dest[dstOffset++] = point.x;
                dest[dstOffset++] = point.y;
            } catch ( TransformException exception ) {
                dest[dstOffset++] = Double.NaN;
                dest[dstOffset++] = Double.NaN;
                if ( firstException == null ) {
                    firstException = exception;
                }
            }
            if ( reverse ) {
                srcOffset -= 4;
                dstOffset -= 4;
            }
        }
        if ( firstException != null )
            throw firstException;
    }

    /**
     * Inverse transforms a list of coordinate point ordinal values.
     * Ordinates must be (<var>x</var>,<var>y</var>) pairs in metres.
     *
     * @throws TransformException if a point can't be transformed. This method try
     *         to transform every points even if some of them can't be transformed.
     *         Non-transformable points will have value {@link Float#NaN}. If more
     *         than one point can't be transformed, then this exception may be about
     *         an arbitrary point.
     */
    public final void inverseTransform( final float[] src, int srcOffset, final float[] dest,
                                       int dstOffset, int numPts )
                            throws TransformException {
        final boolean reverse = ( src == dest && srcOffset < dstOffset && srcOffset
                                                                          + ( numPts << 1 ) > dstOffset );
        if ( reverse ) {
            srcOffset += 2 * numPts;
            dstOffset += 2 * numPts;
        }
        final Point2D.Double point = new Point2D.Double();
        TransformException firstException = null;
        while ( --numPts >= 0 ) {
            try {
                point.x = src[srcOffset++];
                point.y = src[srcOffset++];
                inverseTransform( point, point );
                dest[dstOffset++] = (float) point.x;
                dest[dstOffset++] = (float) point.y;
            } catch ( TransformException exception ) {
                dest[dstOffset++] = Float.NaN;
                dest[dstOffset++] = Float.NaN;
                if ( firstException == null ) {
                    firstException = exception;
                }
            }
            if ( reverse ) {
                srcOffset -= 4;
                dstOffset -= 4;
            }
        }
        if ( firstException != null )
            throw firstException;
    }

    //////////////////////////////////////////////////////////////////////
    ////                                                              ////
    ////             INTERNAL COMPUTATIONS FOR SUBCLASSES             ////
    ////                                                              ////
    //////////////////////////////////////////////////////////////////////

    /**
     * Iteratively solve equation (7-9) from Snyder.
     */
    final double cphi2( final double ts )
                            throws TransformException {
        final double eccnth = 0.5 * e;
        double phi = ( Math.PI / 2 ) - 2.0 * Math.atan( ts );
        for ( int i = 0; i < 16; i++ ) {
            final double con = e * Math.sin( phi );
            final double dphi = ( Math.PI / 2 ) - 2.0
                                * Math.atan( ts * Math.pow( ( 1 - con ) / ( 1 + con ), eccnth ) )
                                - phi;
            phi += dphi;
            if ( Math.abs( dphi ) <= TOL )
                return phi;
        }
        throw new TransformException( Resources.format( ResourceKeys.ERROR_NO_CONVERGENCE ) );
    }

    /**
     * Compute function <code>f(s,c,es) = c/sqrt(1 - s�*es)</code>
     * needed for the true scale latitude (Snyder, p. 47), where
     * <var>s</var> and <var>c</var> are the sine and cosine of
     * the true scale latitude, and {@link #es} the eccentricity
     * squared.
     */
    final double msfn( final double s, final double c ) {
        return c / Math.sqrt( 1.0 - s * s * es );
    }

    /**
     * Compute function (15-9) from Snyder
     * equivalent to negative of function (7-7).
     */
    final double tsfn( final double phi, double sinphi ) {
        sinphi *= e;
        /*
         * NOTE: change sign to get the equivalent of Snyder (7-7).
         */
        return Math.tan( 0.5 * ( ( Math.PI / 2d ) - phi ) )
               / Math.pow( ( 1 - sinphi ) / ( 1 + sinphi ), 0.5 * e );
    }

    //////////////////////////////////////////////////////////////////////
    ////                                                              ////
    ////                        MISCELLANEOUS                         ////
    ////                                                              ////
    //////////////////////////////////////////////////////////////////////

    /**
     * Returns the inverse of this map projection.
     */
    public final synchronized MathTransform inverse() {
        if ( inverse == null )
            inverse = new Inverse();
        return inverse;
    }

    /**
     * Returns <code>false</code> since map
     * projections are not identity transforms.
     */
    public final boolean isIdentity() {
        return false;
    }

    /**
     * Returns a hash value for this map projection.
     */
    public int hashCode() {
        long code = Double.doubleToLongBits( a );
        code = code * 37 + Double.doubleToLongBits( b );
        code = code * 37 + Double.doubleToLongBits( centralMeridian );
        code = code * 37 + Double.doubleToLongBits( centralLatitude );
        return (int) code ^ (int) ( code >>> 32 );
    }

    /**
     * Compares the specified object with
     * this map projection for equality.
     */
    public boolean equals( final Object object ) {
        // Do not check 'object==this' here, since this
        // optimization is usually done in subclasses.
        if ( super.equals( object ) ) {
            final MapProjection that = (MapProjection) object;
            return Double.doubleToLongBits( this.a ) == Double.doubleToLongBits( that.a )
                   && Double.doubleToLongBits( this.b ) == Double.doubleToLongBits( that.b )
                   && Double.doubleToLongBits( this.centralMeridian ) == Double.doubleToLongBits( that.centralMeridian )
                   && Double.doubleToLongBits( this.centralLatitude ) == Double.doubleToLongBits( that.centralLatitude );
        }
        return false;
    }

    /**
     * Retourne une cha�ne de caract�res repr�sentant cette projection cartographique.
     * Cette cha�ne de caract�res contiendra entre autres le nom de la projection, les
     * coordonn�es du centre et celles de l'origine.
     */
    public final String toString() {
        final StringBuffer buffer = paramMT( classification );
        toString( buffer );
        buffer.append( ']' );
        return buffer.toString();
    }

    /**
     * Impl�mentation de la partie entre crochets
     * de la cha�ne retourn�e par {@link #toString()}.
     */
    void toString( final StringBuffer buffer ) {
        addParameter( buffer, "semi_major", a );
        addParameter( buffer, "semi_minor", b );
        addParameter( buffer, "central_meridian", Math.toDegrees( centralMeridian ) );
        addParameter( buffer, "latitude_of_origin", Math.toDegrees( centralLatitude ) );
    }

    /**
     * Inverse of a map projection.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    private final class Inverse extends AbstractMathTransform.Inverse implements MathTransform2D {
        public Inverse() {
            MapProjection.this.super();
        }

        public Point2D transform( final Point2D source, final Point2D dest )
                                throws TransformException {
            return MapProjection.this.inverseTransform( source, dest );
        }

        public void transform( final double[] source, final int srcOffset, final double[] dest,
                              final int dstOffset, final int length )
                                throws TransformException {
            MapProjection.this.inverseTransform( source, srcOffset, dest, dstOffset, length );
        }

        public void transform( final float[] source, final int srcOffset, final float[] dest,
                              final int dstOffset, final int length )
                                throws TransformException {
            MapProjection.this.inverseTransform( source, srcOffset, dest, dstOffset, length );
        }

        public Shape createTransformedShape( final Shape shape )
                                throws TransformException {
            return this.createTransformedShape( shape, null, null, Geometry.HORIZONTAL );
        }
    }

    /**
     * Informations about a {@link MapProjection}.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    static abstract class Provider extends MathTransformProvider {
        /**
         * Construct a new provider.
         *
         * @param classname The classification name.
         * @param nameKey Resources key for a human readable name.
         *        This is used for {@link #getName} implementation.
         */
        protected Provider( final String classname, final int nameKey ) {
            super( classname, nameKey, DEFAULT_PROJECTION_DESCRIPTOR );
        }

        /**
         * Create a new map projection for a parameter list.
         */
        public final MathTransform create( final ParameterList parameters ) {
            return (MathTransform) create( new Projection( "Generated", getClassName(), parameters ) );
        }

        /**
         * Create a new map projection.  NOTE: The returns type should
         * be {@link MathTransform}, but as of JDK 1.4-beta3, it force
         * class loading for all projection classes (MercatorProjection,
         * etc.) before than necessary. Changing the returns type to
         * Object is a trick to avoid too early class loading...
         */
        protected abstract Object create( final Projection parameters );
    }
}
