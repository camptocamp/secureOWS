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

// G�ometry
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 * Static utilities methods. Those methods operate on geometric
 * shapes from the <code>java.awt.geom</code> package.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public final class Geometry {
    /**
     * Valeur limite pour d�tecter si des points sont
     * colin�aires ou si des coordonn�es sont identiques.
     */
    private static final double EPS = 1E-6;

    /**
     * Constante pour les calculs de paraboles. Cette constante indique que l'axe des
     * <var>x</var> de la parabole doit �tre parall�le � la droite joignant les points
     * P0 et P2.
     */
    public static final int PARALLEL = 0;

    /**
     * Constante pour les calculs de paraboles. Cette constante indique que l'axe des
     * <var>x</var> de la parabole doit �tre horizontale, quelle que soit la pente de
     * la droite joignant les points P0 et P2.
     */
    public static final int HORIZONTAL = 1;

    /**
     * Interdit la cr�ation
     * d'objets de cette classe.
     */
    private Geometry() {
    }

    /**
     * Retourne le point d'intersection de deux segments de droites.
     * Cette m�thode ne prolonge pas les segments de droites � l'infini.
     * Si les deux segments ne s'interceptent pas (soit par ce qu'ils sont
     * parall�les, ou soit parce qu'ils ne se prolongent pas assez loin
     * pour se toucher), alors cette m�thode retourne <code>null</code>.
     *
     * @param  a Premi�re ligne.
     * @param  b Deuxi�me ligne.
     * @return Si une intersection fut trouv�e, les coordonn�es de cette
     *         intersection. Si aucune intersection n'a �t� trouv�e, alors
     *         cette m�thode retourne <code>null</code>.
     */
    public static Point2D intersectionPoint( final Line2D a, final Line2D b ) {
        return intersectionPoint( a.getX1(), a.getY1(), a.getX2(), a.getY2(), b.getX1(), b.getY1(),
                                  b.getX2(), b.getY2() );
    }

    /**
     * Retourne le point d'intersection de deux segments de droites.
     * Cette m�thode ne prolonge pas les segments de droites � l'infini.
     * Si les deux segments ne s'interceptent pas (soit par ce qu'ils sont
     * parall�les, ou soit parce qu'ils ne se prolongent pas assez loin
     * pour se toucher), alors cette m�thode retourne <code>null</code>.
     *
     * @return Si une intersection fut trouv�e, les coordonn�es de cette
     *         intersection. Si aucune intersection n'a �t� trouv�e, alors
     *         cette m�thode retourne <code>null</code>.
     */
    public static Point2D intersectionPoint( final double ax1, final double ay1, double ax2,
                                            double ay2, final double bx1, final double by1,
                                            double bx2, double by2 ) {
        ax2 -= ax1;
        ay2 -= ay1;
        bx2 -= bx1;
        by2 -= by1;
        double x = ay2 * bx2;
        double y = ax2 * by2;
        /*
         * Les x et y calcul�s pr�c�demment ne sont que des valeurs temporaires. Si et
         * seulement si les deux droites sont parall�les, alors x==y. Ensuite seulement,
         * la paire (x,y) ci-dessous sera les v�ritables coordonn�es du point d'intersection.
         */
        x = ( ( by1 - ay1 ) * ( ax2 * bx2 ) + x * ax1 - y * bx1 ) / ( x - y );
        y = Math.abs( bx2 ) > Math.abs( ax2 ) ? ( by2 / bx2 ) * ( x - bx1 ) + by1 : ( ay2 / ax2 )
                                                                                    * ( x - ax1 )
                                                                                    + ay1;
        /*
         * Les expressions '!=0' ci-dessous sont importantes afin d'�viter des probl�mes
         * d'erreurs d'arrondissement lorsqu'un segment est vertical ou horizontal. Les
         * '!' qui suivent sont importants pour un fonctionnement correct avec NaN.
         */
        if ( ax2 != 0
             && !( ax2 < 0 ? ( x <= ax1 && x >= ax1 + ax2 ) : ( x >= ax1 && x <= ax1 + ax2 ) ) )
            return null;
        if ( bx2 != 0
             && !( bx2 < 0 ? ( x <= bx1 && x >= bx1 + bx2 ) : ( x >= bx1 && x <= bx1 + bx2 ) ) )
            return null;
        if ( ay2 != 0
             && !( ay2 < 0 ? ( y <= ay1 && y >= ay1 + ay2 ) : ( y >= ay1 && y <= ay1 + ay2 ) ) )
            return null;
        if ( by2 != 0
             && !( by2 < 0 ? ( y <= by1 && y >= by1 + by2 ) : ( y >= by1 && y <= by1 + by2 ) ) )
            return null;
        return new Point2D.Double( x, y );
    }

    /**
     * Retourne le point sur le segment de droite <code>line</code> qui se trouve le
     * plus pr�s du point <code>point</code> sp�cifi�. Appellons <code>result</code>
     * le point retourn� par cette m�thode. Il est garanti que <code>result</code>
     * r�pond aux conditions suivantes (aux erreurs d'arrondissements pr�s):
     *
     * <ul>
     *   <li><code>result</code> est un point du segment de droite <code>line</code>.
     *       Il ne trouve pas au del� des points extr�mes P1 et P2 de ce segment.</li>
     *   <li>La distance entre les points <code>result</code> et <code>point</code>
     *       est la plus courte distance possible pour les points qui respectent la
     *       condition pr�c�dente. Cette distance peut �tre calcul�e par
     *       <code>point.distance(result)</code>.</li>
     * </ul>
     *
     * @see #colinearPoint(Line2D, Point2D, double)
     */
    public static Point2D nearestColinearPoint( final Line2D segment, final Point2D point ) {
        return nearestColinearPoint( segment.getX1(), segment.getY1(), segment.getX2(),
                                     segment.getY2(), point.getX(), point.getY() );
    }

    /**
     * Retourne le point sur le segment de droite <code>(x1,y1)-(x2,y2)</code> qui se trouve le
     * plus pr�s du point <code>(x,y)</code> sp�cifi�. Appellons <code>result</code> le point
     * retourn� par cette m�thode. Il est garanti que <code>result</code> r�pond aux conditions
     * suivantes (aux erreurs d'arrondissements pr�s):
     *
     * <ul>
     *   <li><code>result</code> est un point du segment de droite <code>(x1,y1)-(x2,y2)</code>. Il
     *       ne trouve pas au del� des points extr�mes <code>(x1,y1)</code> et <code>(x2,y2)</code>
     *       de ce segment.</li>
     *   <li>La distance entre les points <code>result</code> et <code>(x,y)</code>
     *       est la plus courte distance possible pour les points qui respectent la
     *       condition pr�c�dente. Cette distance peut �tre calcul�e par
     *       <code>new&nbsp;Point2D.Double(x,y).distance(result)</code>.</li>
     * </ul>
     *
     * @see #colinearPoint(double,double , double,double , double,double , double)
     */
    public static Point2D nearestColinearPoint( final double x1, final double y1, final double x2,
                                               final double y2, double x, double y ) {
        final double slope = ( y2 - y1 ) / ( x2 - x1 );
        if ( !Double.isInfinite( slope ) ) {
            final double y0 = ( y2 - slope * x2 );
            x = ( ( y - y0 ) * slope + x ) / ( slope * slope + 1 );
            y = x * slope + y0;
        } else
            x = x2;
        if ( x1 <= x2 ) {
            if ( x < x1 )
                x = x1;
            if ( x > x2 )
                x = x2;
        } else {
            if ( x > x1 )
                x = x1;
            if ( x < x2 )
                x = x2;
        }
        if ( y1 <= y2 ) {
            if ( y < y1 )
                y = y1;
            if ( y > y2 )
                y = y2;
        } else {
            if ( y > y1 )
                y = y1;
            if ( y < y2 )
                y = y2;
        }
        return new Point2D.Double( x, y );
    }

    /**
     * Retourne le point sur le segment de droite <code>line</code> qui se trouve � la
     * distance <code>distance</code> sp�cifi�e du point <code>point</code>. Appellons
     * <code>result</code> le point retourn� par cette m�thode. Si <code>result</code>
     * est non-nul, alors il est garanti qu'il r�pond aux conditions suivantes (aux
     * erreurs d'arrondissements pr�s):
     *
     * <ul>
     *   <li><code>result</code> est un point du segment de droite <code>line</code>.
     *       Il ne trouve pas au del� des points extr�mes P1 et P2 de ce segment.</li>
     *   <li>La distance entre les points <code>result</code> et <code>point</code>
     *       est exactement <code>distance</code> (aux erreurs d'arrondissements pr�s).
     *       Cette distance peut �tre calcul�e par <code>point.distance(result)</code>.</li>
     * </ul>
     *
     * Si aucun point ne peut r�pondre � ces conditions, alors cette m�thode retourne
     * <code>null</code>. Si deux points peuvent r�pondre � ces conditions, alors par
     * convention cette m�thode retourne le point le plus pr�s du point <code>line.getP1()</code>.
     *
     * @see #nearestColinearPoint(Line2D, Point2D)
     */
    public static Point2D colinearPoint( final Line2D line, final Point2D point,
                                        final double distance ) {
        return colinearPoint( line.getX1(), line.getY1(), line.getX2(), line.getY2(), point.getX(),
                              point.getY(), distance );
    }

    /**
     * Retourne le point sur le segment de droite <code>(x1,y1)-(x2,y2)</code> qui se trouve
     * � la distance <code>distance</code> sp�cifi�e du point <code>point</code>. Appellons
     * <code>result</code> le point retourn� par cette m�thode. Si <code>result</code>
     * est non-nul, alors il est garanti qu'il r�pond aux conditions suivantes (aux
     * erreurs d'arrondissements pr�s):
     *
     * <ul>
     *   <li><code>result</code> est un point du segment de droite <code>(x1,y1)-(x2,y2)</code>. Il
     *       ne trouve pas au del� des points extr�mes <code>(x1,y1)</code> et <code>(x2,y2)</code>
     *       de ce segment.</li>
     *   <li>La distance entre les points <code>result</code> et <code>point</code>
     *       est exactement <code>distance</code> (aux erreurs d'arrondissements pr�s).
     *       Cette distance peut �tre calcul�e par <code>point.distance(result)</code>.</li>
     * </ul>
     *
     * Si aucun point ne peut r�pondre � ces conditions, alors cette m�thode retourne
     * <code>null</code>. Si deux points peuvent r�pondre � ces conditions, alors par
     * convention cette m�thode retourne le point le plus pr�s du point <code>(x1,y1)</code>.
     *
     * @see #nearestColinearPoint(double,double , double,double , double,double)
     */
    public static Point2D colinearPoint( double x1, double y1, double x2, double y2, double x,
                                        double y, double distance ) {
        final double ox1 = x1;
        final double oy1 = y1;
        final double ox2 = x2;
        final double oy2 = y2;
        distance *= distance;
        if ( x1 == x2 ) {
            double dy = x1 - x;
            dy = Math.sqrt( distance - dy * dy );
            y1 = y - dy;
            y2 = y + dy;
        } else if ( y1 == y2 ) {
            double dx = y1 - y;
            dx = Math.sqrt( distance - dx * dx );
            x1 = x - dx;
            x2 = x + dx;
        } else {
            final double m = ( y1 - y2 ) / ( x2 - x1 );
            final double y0 = ( y2 - y ) + m * ( x2 - x );
            final double B = m * y0;
            final double A = m * m + 1;
            final double C = Math.sqrt( B * B + A * ( distance - y0 * y0 ) );
            x1 = ( B + C ) / A;
            x2 = ( B - C ) / A;
            y1 = y + y0 - m * x1;
            y2 = y + y0 - m * x2;
            x1 += x;
            x2 += x;
        }
        boolean in1, in2;
        if ( oy1 > oy2 ) {
            in1 = y1 <= oy1 && y1 >= oy2;
            in2 = y2 <= oy1 && y2 >= oy2;
        } else {
            in1 = y1 >= oy1 && y1 <= oy2;
            in2 = y2 >= oy1 && y2 <= oy2;
        }
        if ( ox1 > ox2 ) {
            in1 &= x1 <= ox1 && x1 >= ox2;
            in2 &= x2 <= ox1 && x2 >= ox2;
        } else {
            in1 &= x1 >= ox1 && x1 <= ox2;
            in2 &= x2 >= ox1 && x2 <= ox2;
        }
        if ( !in1 && !in2 )
            return null;
        if ( !in1 )
            return new Point2D.Double( x2, y2 );
        if ( !in2 )
            return new Point2D.Double( x1, y1 );
        x = x1 - ox1;
        y = y1 - oy1;
        final double d1 = x * x + y * y;
        x = x2 - ox1;
        y = y2 - oy1;
        final double d2 = x * x + y * y;
        if ( d1 > d2 ) {
            return new Point2D.Double( x2, y2 );
        }
            
        return new Point2D.Double( x1, y1 );
    }

    /**
     * Retourne une courbe quadratique passant par les trois points sp�cifi�s. Il peut exister une infinit� de courbes
     * quadratiques passant par trois points. On peut voir les choses en disant qu'une courbe quadratique correspond �
     * une parabole produite par une �quation de la forme <code>y=ax�+bx+c</code>,  mais que l'axe des <var>x</var> de
     * cette �quation n'est pas n�cessairement horizontal. La direction de cet axe des <var>x</var> d�pend du param�tre
     * <code>orientation</code> sp�cifi� � cette m�thode. La valeur {@link #HORIZONTAL} signifie que l'axe des <var>x</var>
     * de la parabole sera toujours horizontal. La courbe quadratique produite ressemblera alors � une parabole classique
     * telle qu'on en voit dans les ouvrages de math�matiques �l�mentaires. La valeur {@link #PARALLEL} indique plut�t que
     * l'axe des <var>x</var> de la parabole doit �tre parall�le � la droite joignant les points <code>P0</code> et
     * <code>P2</code>. Ce dernier type produira le m�me r�sultat que {@link #HORIZONTAL} si <code>P0.y==P2.y</code>.
     *
     * @param  P0 Premier point de la courbe quadratique.
     * @param  P1 Point par lequel la courbe quadratique doit passer. Il n'est pas obligatoire que ce point soit situ�
     *         entre <code>P0</code> et <code>P1</code>. Toutefois, il ne doit pas �tre colin�aire avec <code>P0</code>
     *         et <code>P1</code>.
     * @param  P2 Dernier point de la courbe quadratique.
     * @param  orientation Orientation de l'axe des <var>x</var> de la parabole: {@link #PARALLEL} ou {@link #HORIZONTAL}.
     * @return Une courbe quadratique passant par les trois points sp�cifi�s. La courbe commencera au point <code>P0</code>
     *         et se terminera au point <code>P2</code>. Si deux points ont des coordonn�es presque identiques, ou si les
     *         trois points sont colin�aires, alors cette m�thode retourne <code>null</code>.
     * @throws IllegalArgumentException si l'argument <code>orientation</code> n'est pas une des constantes valides.
     */
    public static QuadCurve2D fitParabol( final Point2D P0, final Point2D P1, final Point2D P2,
                                         final int orientation )
                            throws IllegalArgumentException {
        return fitParabol( P0.getX(), P0.getY(), P1.getX(), P1.getY(), P2.getX(), P2.getY(),
                           orientation );
    }

    /**
     * Retourne une courbe quadratique passant par les trois points sp�cifi�s. Il peut exister une infinit� de courbes
     * quadratiques passant par trois points. On peut voir les choses en disant qu'une courbe quadratique correspond �
     * une parabole produite par une �quation de la forme <code>y=ax�+bx+c</code>,  mais que l'axe des <var>x</var> de
     * cette �quation n'est pas n�cessairement horizontal. La direction de cet axe des <var>x</var> d�pend du param�tre
     * <code>orientation</code> sp�cifi� � cette m�thode. La valeur {@link #HORIZONTAL} signifie que l'axe des <var>x</var>
     * de la parabole sera toujours horizontal. La courbe quadratique produite ressemblera alors � une parabole classique
     * telle qu'on en voit dans les ouvrages de math�matiques �l�mentaires. La valeur {@link #PARALLEL} indique plut�t que
     * l'axe des <var>x</var> de la parabole doit �tre parall�le � la droite joignant les points <code>(x0,y0)</code> et
     * <code>(x2,y2)</code>. Ce dernier type produira le m�me r�sultat que {@link #HORIZONTAL} si <code>y0==y2</code>.
     *
     * @param  orientation Orientation de l'axe des <var>x</var> de la parabole: {@link #PARALLEL} ou {@link #HORIZONTAL}.
     * @return Une courbe quadratique passant par les trois points sp�cifi�s. La courbe commencera au point <code>(x0,y0)</code>
     *         et se terminera au point <code>(x2,y2)</code>. Si deux points ont des coordonn�es presque identiques, ou si les
     *         trois points sont colin�aires, alors cette m�thode retourne <code>null</code>.
     * @throws IllegalArgumentException si l'argument <code>orientation</code> n'est pas une des constantes valides.
     */
    public static QuadCurve2D fitParabol( final double x0, final double y0, final double x1,
                                         final double y1, final double x2, final double y2,
                                         final int orientation )
                            throws IllegalArgumentException {
        final Point2D p = parabolicControlPoint( x0, y0, x1, y1, x2, y2, orientation, null );
        return ( p != null ) ? new QuadCurve2D.Double( x0, y0, p.getX(), p.getY(), x2, y2 ) : null;
    }

    /**
     * Retourne le point de contr�le d'une courbe quadratique passant par les trois points sp�cifi�s. Il peut exister une infinit�
     * de courbes quadratiques passant par trois points. On peut voir les choses en disant qu'une courbe quadratique correspond �
     * une parabole produite par une �quation de la forme <code>y=ax�+bx+c</code>,  mais que l'axe des <var>x</var> de
     * cette �quation n'est pas n�cessairement horizontal. La direction de cet axe des <var>x</var> d�pend du param�tre
     * <code>orientation</code> sp�cifi� � cette m�thode. La valeur {@link #HORIZONTAL} signifie que l'axe des <var>x</var>
     * de la parabole sera toujours horizontal. La courbe quadratique produite ressemblera alors � une parabole classique
     * telle qu'on en voit dans les ouvrages de math�matiques �l�mentaires. La valeur {@link #PARALLEL} indique plut�t que
     * l'axe des <var>x</var> de la parabole doit �tre parall�le � la droite joignant les points <code>(x0,y0)</code> et
     * <code>(x2,y2)</code>. Ce dernier type produira le m�me r�sultat que {@link #HORIZONTAL} si <code>y0==y2</code>.
     *
     * @param  orientation Orientation de l'axe des <var>x</var> de la parabole: {@link #PARALLEL} ou {@link #HORIZONTAL}.
     * @return Le point de contr�le d'une courbe quadratique passant par les trois points sp�cifi�s. La courbe commencera au
     *         point <code>(x0,y0)</code> et se terminera au point <code>(x2,y2)</code>. Si deux points ont des coordonn�es
     *         presque identiques, ou si les trois points sont colin�aires, alors cette m�thode retourne <code>null</code>.
     * @throws IllegalArgumentException si l'argument <code>orientation</code> n'est pas une des constantes valides.
     */
    public static Point2D parabolicControlPoint( final double x0, final double y0, double x1,
                                                double y1, double x2, double y2,
                                                final int orientation, final Point2D dest )
                            throws IllegalArgumentException {
        /*
         * Applique une translation de fa�on � ce que (x0,y0)
         * devienne l'origine du syst�me d'axes. Il ne faudra
         * plus utiliser (x0,y0) avant la fin de ce code.
         */
        x1 -= x0;
        y1 -= y0;
        x2 -= x0;
        y2 -= y0;
        switch ( orientation ) {
        case PARALLEL: {
            /*
             * Applique une rotation de fa�on � ce que (x2,y2)
             * tombe sur l'axe des x, c'est-�-dire que y2=0.
             */
            final double rx2 = x2;
            final double ry2 = y2;
            x2 = XMath.hypot( x2, y2 );
            y2 = ( x1 * rx2 + y1 * ry2 ) / x2; // use 'y2' as a temporary variable for 'x1'
            y1 = ( y1 * rx2 - x1 * ry2 ) / x2;
            x1 = y2;
            y2 = 0;
            /*
             * Calcule maintenant les coordonn�es du point
             * de contr�le selon le nouveau syst�me d'axes.
             */
            final double x = 0.5; // Really "x/x2"
            final double y = ( y1 * x * x2 ) / ( x1 * ( x2 - x1 ) ); // Really "y/y2"
            final double check = Math.abs( y );
            if ( !( check <= 1 / EPS ) )
                return null; // Deux points ont les m�mes coordonn�es.
            if ( !( check >= EPS ) )
                return null; // Les trois points sont colin�aires.
            /*
             * Applique une rotation inverse puis une translation pour
             * ramener le syst�me d'axe dans sa position d'origine.
             */
            x1 = ( x * rx2 - y * ry2 ) + x0;
            y1 = ( y * rx2 + x * ry2 ) + y0;
            break;
        }
        case HORIZONTAL: {
            final double a = ( y2 - y1 * x2 / x1 ) / ( x2 - x1 ); // Really "a*x2"
            final double check = Math.abs( a );
            if ( !( check <= 1 / EPS ) )
                return null; // Deux points ont les m�mes coordonn�es.
            if ( !( check >= EPS ) )
                return null; // Les trois points sont colin�aires.
            final double b = y2 / x2 - a;
            x1 = ( 1 + b / ( 2 * a ) ) * x2 - y2 / ( 2 * a );
            y1 = y0 + b * x1;
            x1 += x0;
            break;
        }
        default:
            throw new IllegalArgumentException();
        }
        if ( dest != null ) {
            dest.setLocation( x1, y1 );
            return dest;
        }
        return new Point2D.Double( x1, y1 );
    }

    /**
     * Retourne un cercle qui passe par
     * chacun des trois points sp�cifi�s.
     */
    public static Ellipse2D fitCircle( final Point2D P1, final Point2D P2, final Point2D P3 ) {
        final Point2D center = circleCentre( P1.getX(), P1.getY(), P2.getX(), P2.getY(), P3.getX(),
                                             P3.getY() );
        final double radius = center.distance( P2 );
        return new Ellipse2D.Double( center.getX() - radius, center.getY() - radius, 2 * radius,
                                     2 * radius );
    }

    /**
     * Retourne la coordonn�e centrale d'un cercle passant
     * pas les trois points sp�cifi�s. La distance entre
     * le point retourn� et n'importe quel des points
     * (<var>x</var><sub>1</sub>,<var>y</var><sub>1</sub>),
     * (<var>x</var><sub>2</sub>,<var>y</var><sub>2</sub>),
     * (<var>x</var><sub>3</sub>,<var>y</var><sub>3</sub>)
     * sera constante; ce sera le rayon d'un cercle centr�
     * au point retourn� et passant par les trois points
     * sp�cifi�s.
     */
    public static Point2D circleCentre( double x1, double y1, double x2, double y2, double x3,
                                       double y3 ) {
        x2 -= x1;
        x3 -= x1;
        y2 -= y1;
        y3 -= y1;
        final double sq2 = ( x2 * x2 + y2 * y2 );
        final double sq3 = ( x3 * x3 + y3 * y3 );
        final double x = ( y2 * sq3 - y3 * sq2 ) / ( y2 * x3 - y3 * x2 );
        return new Point2D.Double( x1 + 0.5 * x, y1 + 0.5 * ( sq2 - x * x2 ) / y2 );
    }

    /**
     * Tente de remplacer la forme g�om�trique <code>path</code> par une des formes standards
     * de Java2D. Par exemple, si <code>path</code> ne contient qu'un simple segment de droite
     * ou une courbe quadratique, alors cette m�thode retournera un objet {@link Line2D} ou
     * {@link QuadCurve2D} respectivement.
     *
     * @param  path Forme g�om�trique � simplifier (g�n�ralement un objet {@link GeneralPath}).
     * @return Forme g�om�trique standard, ou <code>path</code> si aucun remplacement n'est propos�.
     */
    public static Shape toPrimitive( final Shape path ) {
        final float[] buffer = new float[6];
        final PathIterator it = path.getPathIterator( null );
        if ( !it.isDone() && it.currentSegment( buffer ) == PathIterator.SEG_MOVETO && !it.isDone() ) {
            final float x1 = buffer[0];
            final float y1 = buffer[1];
            final int code = it.currentSegment( buffer );
            if ( it.isDone() )
                switch ( code ) {
                case PathIterator.SEG_LINETO:
                    return new Line2D.Float( x1, y1, buffer[0], buffer[1] );
                case PathIterator.SEG_QUADTO:
                    return new QuadCurve2D.Float( x1, y1, buffer[0], buffer[1], buffer[2],
                                                  buffer[3] );
                case PathIterator.SEG_CUBICTO:
                    return new CubicCurve2D.Float( x1, y1, buffer[0], buffer[1], buffer[2],
                                                   buffer[3], buffer[4], buffer[5] );
                }
        }
        return path;
    }
}
