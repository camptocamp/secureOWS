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

// Geometry
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * Utility methods for affine transforms. This class provides a set
 * of public static methods working on any {@link AffineTransform}.
 * <br><br>
 * Class <code>XAffineTransform</code>  overrides all mutable methods
 * of {@link AffineTransform} in order to check for permission before
 * to change the transform's state. If {@link #checkPermission} is
 * defined to always thrown an exception, then <code>XAffineTransform</code>
 * is immutable.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public abstract class XAffineTransform extends AffineTransform
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 4891543057571195291L;

    /**
     * Tolerance value for floating point comparaisons.
     */
    private static final double EPS=1E-6;

    /**
     * Constructs a new <code>XAffineTransform</code> that is a
     * copy of the specified <code>AffineTransform</code> object.
     */
    protected XAffineTransform(final AffineTransform tr)
    {super(tr);}

    /**
     * Check if the caller is allowed to change this
     * <code>XAffineTransform</code>'s state.
     */
    protected abstract void checkPermission();

    /**
     * Check for permission before translating this transform.
     */
    public void translate(double tx, double ty)
    {
        checkPermission();
        super.translate(tx, ty);
    }

    /**
     * Check for permission before rotating this transform.
     */
    public void rotate(double theta)
    {
        checkPermission();
        super.rotate(theta);
    }

    /**
     * Check for permission before rotating this transform.
     */
    public void rotate(double theta, double x, double y)
    {
        checkPermission();
        super.rotate(theta, x, y);
    }

    /**
     * Check for permission before scaling this transform.
     */
    public void scale(double sx, double sy)
    {
        checkPermission();
        super.scale(sx, sy);
    }

    /**
     * Check for permission before shearing this transform.
     */
    public void shear(double shx, double shy)
    {
        checkPermission();
        super.shear(shx, shy);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setToIdentity()
    {
        checkPermission();
        super.setToIdentity();
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setToTranslation(double tx, double ty)
    {
        checkPermission();
        super.setToTranslation(tx, ty);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setToRotation(double theta)
    {
        checkPermission();
        super.setToRotation(theta);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setToRotation(double theta, double x, double y)
    {
        checkPermission();
        super.setToRotation(theta, x, y);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setToScale(double sx, double sy)
    {
        checkPermission();
        super.setToScale(sx, sy);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setToShear(double shx, double shy)
    {
        checkPermission();
        super.setToShear(shx, shy);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setTransform(AffineTransform Tx)
    {
        checkPermission();
        super.setTransform(Tx);
    }

    /**
     * Check for permission before setting this transform.
     */
    public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12)
    {
        checkPermission();
        super.setTransform(m00, m10, m01, m11, m02, m12);
    }

    /**
     * Check for permission before concatenating this transform.
     */
    public void concatenate(AffineTransform Tx)
    {
        checkPermission();
        super.concatenate(Tx);
    }

    /**
     * Check for permission before concatenating this transform.
     */
    public void preConcatenate(AffineTransform Tx)
    {
        checkPermission();
        super.preConcatenate(Tx);
    }

    /**
     * Retourne un rectangle qui contient enti�rement la transformation directe de <code>bounds</code>.
     * Cette op�ration est l'�quivalent de <code>createTransformedShape(bounds).getBounds2D()</code>.
     *
     * @param transform Transformation affine � utiliser.
     * @param bounds    Rectangle � transformer. Ce rectangle ne sera pas modifi�.
     * @param dest      Rectangle dans lequel placer le r�sultat. Si nul, un nouveau rectangle sera cr��.
     * @return          La transformation directe du rectangle <code>bounds</code>.
     */
    public static Rectangle2D transform(final AffineTransform transform, final Rectangle2D bounds, final Rectangle2D dest)
    {
        double xmin=Double.POSITIVE_INFINITY;
        double ymin=Double.POSITIVE_INFINITY;
        double xmax=Double.NEGATIVE_INFINITY;
        double ymax=Double.NEGATIVE_INFINITY;
        final Point2D.Double point=new Point2D.Double();
        for (int i=0; i<4; i++)
        {
            point.x = (i&1)==0 ? bounds.getMinX() : bounds.getMaxX();
            point.y = (i&2)==0 ? bounds.getMinY() : bounds.getMaxY();
            transform.transform(point, point);
            if (point.x<xmin) xmin=point.x;
            if (point.x>xmax) xmax=point.x;
            if (point.y<ymin) ymin=point.y;
            if (point.y>ymax) ymax=point.y;
        }
        if (dest!=null)
        {
            dest.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
            return dest;
        }
        return new Rectangle2D.Double(xmin, ymin, xmax-xmin, ymax-ymin);
    }

    /**
     * Retourne un rectangle qui contient enti�rement la transformation inverse de <code>bounds</code>.
     * Cette op�ration est l'�quivalent de <code>createInverse().createTransformedShape(bounds).getBounds2D()</code>.
     *
     * @param transform Transformation affine � utiliser.
     * @param bounds    Rectangle � transformer. Ce rectangle ne sera pas modifi�.
     * @param dest      Rectangle dans lequel placer le r�sultat. Si nul, un nouveau rectangle sera cr��.
     * @return          La transformation inverse du rectangle <code>bounds</code>.
     * @throws NoninvertibleTransformException si la transformation affine ne peut pas �tre invers�e.
     */
    public static Rectangle2D inverseTransform(final AffineTransform transform, final Rectangle2D bounds, final Rectangle2D dest) throws NoninvertibleTransformException
    {
        double xmin=Double.POSITIVE_INFINITY;
        double ymin=Double.POSITIVE_INFINITY;
        double xmax=Double.NEGATIVE_INFINITY;
        double ymax=Double.NEGATIVE_INFINITY;
        final Point2D.Double point=new Point2D.Double();
        for (int i=0; i<4; i++)
        {
            point.x = (i&1)==0 ? bounds.getMinX() : bounds.getMaxX();
            point.y = (i&2)==0 ? bounds.getMinY() : bounds.getMaxY();
            transform.inverseTransform(point, point);
            if (point.x<xmin) xmin=point.x;
            if (point.x>xmax) xmax=point.x;
            if (point.y<ymin) ymin=point.y;
            if (point.y>ymax) ymax=point.y;
        }
        if (dest!=null)
        {
            dest.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
            return dest;
        }
        return new Rectangle2D.Double(xmin, ymin, xmax-xmin, ymax-ymin);
    }

    /**
     * Calcule la transformation affine inverse d'un
     * point sans prendre en compte la translation.
     *
     * @param transform Transformation affine � utiliser.
     * @param source    Point � transformer. Ce rectangle ne sera pas modifi�.
     * @param dest      Point dans lequel placer le r�sultat. Si nul, un nouveau point sera cr��.
     * @return          La transformation inverse du point <code>source</code>.
     * @throws NoninvertibleTransformException si la transformation affine ne peut pas �tre invers�e.
     */
    public static Point2D inverseDeltaTransform(final AffineTransform transform, final Point2D source, final Point2D dest) throws NoninvertibleTransformException
    {
        final double m00 = transform.getScaleX();
        final double m11 = transform.getScaleY();
        final double m01 = transform.getShearX();
        final double m10 = transform.getShearY();
        final double det = m00*m11 - m01*m10;
        if (!(Math.abs(det) > Double.MIN_VALUE))
        {
            return transform.createInverse().deltaTransform(source, dest);
        }
        final double x = source.getX();
        final double y = source.getY();
        if (dest!=null)
        {
            dest.setLocation((x*m11 - y*m01)/det,
                             (y*m00 - x*m10)/det);
            return dest;
        }
        return new Point2D.Double((x*m11 - y*m01)/det,
                                  (y*m00 - x*m10)/det);
    }

    /**
     * Retourne le facteur d'�chelle <var>x</var> en annulant l'effet d'une �ventuelle rotation.
     * Ce facteur est calcul� par <IMG src="{@docRoot}/net/seas/map/layer/doc-files/equation1.gif">.
     */
    public static double getScaleX0(final AffineTransform zoom)
    {return XMath.hypot(zoom.getScaleX(), zoom.getShearX());}

    /**
     * Retourne le facteur d'�chelle <var>y</var> en annulant l'effet d'une �ventuelle rotation.
     * Ce facteur est calcul� par <IMG src="{@docRoot}/net/seas/map/layer/doc-files/equation2.gif">.
     */
    public static double getScaleY0(final AffineTransform zoom)
    {return XMath.hypot(zoom.getScaleY(), zoom.getShearY());}

    /**
     * Retourne une transformation affine repr�sentant un zoom fait autour d'un point
     * central (<var>x</var>,<var>y</var>). Les transformations laisseront inchang�es
     * la coordonn�e (<var>x</var>,<var>y</var>) sp�cifi�e.
     *
     * @param sx Echelle le long de l'axe des <var>x</var>.
     * @param sy Echelle le long de l'axe des <var>y</var>.
     * @param  x Coordonn�es <var>x</var> du point central.
     * @param  y Coordonn�es <var>y</var> du point central.
     * @return   Transformation affine d'un zoom qui laisse
     *           la coordonn�e (<var>x</var>,<var>y</var>)
     *           inchang�e.
     */
    public static AffineTransform getScaleInstance(final double sx, final double sy, final double x, final double y)
    {return new AffineTransform(sx, 0, 0, sy, (1-sx)*x, (1-sy)*y);}

    /*
     * V�rifie si les co�fficients de la matrice sont proches de valeurs enti�res.
     * Si c'est le cas, ces co�fficients seront arrondis aux valeurs enti�res les
     * plus proches.  Cet arrondissement est utile par exemple pour accel�rer les
     * affichages d'images. Il est surtout efficace lorsque l'on sait qu'une matrice
     * a des chances d'�tre proche de la matrice identit�e.
     */
    public static void round(final AffineTransform zoom)
    {
        double r;
        final double m00,m01,m10,m11;
        if (Math.abs((m00=Math.rint(r=zoom.getScaleX()))-r) <= EPS &&
            Math.abs((m01=Math.rint(r=zoom.getShearX()))-r) <= EPS &&
            Math.abs((m11=Math.rint(r=zoom.getScaleY()))-r) <= EPS &&
            Math.abs((m10=Math.rint(r=zoom.getShearY()))-r) <= EPS)
        {
            if ((m00!=0 || m01!=0) && (m10!=0 || m11!=0))
            {
                double m02=Math.rint(r=zoom.getTranslateX()); if (!(Math.abs(m02-r)<=EPS)) m02=r;
                double m12=Math.rint(r=zoom.getTranslateY()); if (!(Math.abs(m12-r)<=EPS)) m12=r;
                zoom.setTransform(m00,m10,m01,m11,m02,m12);
            }
        }
    }
}
