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

// Divers
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.FieldPosition;
import java.text.NumberFormat;


/**
 * Serializable, high-performance double-precision rectangle. Instead of using
 * <code>x</code>, <code>y</code>, <code>width</code> and <code>height</code>,
 * this class store rectangle's coordinates into the following fields:
 * {@link #xmin}, {@link #xmax}, {@link #ymin} et {@link #ymax}. Methods likes
 * <code>contains</code> and <code>intersects</code> are faster, which make this
 * class more appropriate for using intensively inside a loop.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class XRectangle2D extends Rectangle2D implements Serializable
{
    /** Coordonn�es <var>x</var> minimale du rectangle. */ public double xmin;
    /** Coordonn�es <var>y</var> minimale du rectangle. */ public double ymin;
    /** Coordonn�es <var>x</var> maximale du rectangle. */ public double xmax;
    /** Coordonn�es <var>y</var> maximale du rectangle. */ public double ymax;

    /**
     * Construit un rectangle par d�faut. Les coordonn�es
     * du rectangle seront <code>(0,0,0,0)</code>.
     */
    public XRectangle2D()
    {}
    
    /**
     * Construit un rectangle avec les coordonn�es sp�cifi�es.
     */
    public XRectangle2D(final double x, final double y, final double width, final double height)
    {
        this.xmin = x;
        this.ymin = y;
        this.xmax = x+width;
        this.ymax = y+height;
    }
    
    /**
     * Construit un rectangle avec une copie
     * des coordonn�es du rectangle sp�cifi�e.
     */
    public XRectangle2D(final Rectangle2D rect)
    {setRect(rect);}
    
    /**
     * Determines whether the <code>RectangularShape</code> is empty.
     * When the <code>RectangularShape</code> is empty, it encloses no
     * area.
     *
     * @return <code>true</code> if the <code>RectangularShape</code> is empty;
     *      <code>false</code> otherwise.
     */
    public boolean isEmpty()
    {return !(xmin<xmax && ymin<ymax);}

    /**
     * Returns the X coordinate of the upper left corner of
     * the framing rectangle in <code>double</code> precision.
     *
     * @return the x coordinate of the upper left corner of the framing rectangle.
     */
    public double getX()
    {return xmin;}

    /**
     * Returns the Y coordinate of the upper left corner of
     * the framing rectangle in <code>double</code> precision.
     *
     * @return the y coordinate of the upper left corner of the framing rectangle.
     */
    public double getY()
    {return ymin;}
    
    /**
     * Returns the width of the framing rectangle in
     * <code>double</code> precision.
     * @return the width of the framing rectangle.
     */
    public double getWidth()
    {return xmax-xmin;}

    /**
     * Returns the height of the framing rectangle in <code>double</code> precision.
     *
     * @return the height of the framing rectangle.
     */
    public double getHeight()
    {return ymax-ymin;}

    /**
     * Returns the smallest X coordinate of the rectangle.
     */
    public double getMinX()
    {return xmin;}

    /**
     * Returns the smallest Y coordinate of the rectangle.
     */
    public double getMinY()
    {return ymin;}

    /**
     * Returns the largest X coordinate of the rectangle.
     */
    public double getMaxX()
    {return xmax;}

    /**
     * Returns the largest Y coordinate of the rectangle.
     */
    public double getMaxY()
    {return ymax;}

    /**
     * Returns the X coordinate of the center of the rectangle.
     */
    public double getCenterX()
    {return (xmin+xmax)/2;}

    /**
     * Returns the Y coordinate of the center of the rectangle.
     */
    public double getCenterY()
    {return (ymin+ymax)/2;}

    /**
     * Sets the location and size of this <code>Rectangle2D</code>
     * to the specified double values.
     *
     * @param x,&nbsp;y the coordinates to which to set the
     *        location of the upper left corner of this <code>Rectangle2D</code>
     * @param width the value to use to set the width of this <code>Rectangle2D</code>
     * @param height the value to use to set the height of this <code>Rectangle2D</code>
     */
    public void setRect(final double x, final double y, final double width, final double height)
    {
        this.xmin = x;
        this.ymin = y;
        this.xmax = x+width;
        this.ymax = y+height;
    }

    /**
     * Sets this <code>Rectangle2D</code> to be the same as the
     * specified <code>Rectangle2D</code>.
     *
     * @param r the specified <code>Rectangle2D</code>
     */
    public void setRect(final Rectangle2D r)
    {
        this.xmin = r.getMinX();
        this.ymin = r.getMinY();
        this.xmax = r.getMaxX();
        this.ymax = r.getMaxY();
    }

    /**
     * Tests if a specified coordinate is inside the boundary of this
     * <code>Rectangle2D</code>.
     *
     * @param x,&nbsp;y the coordinates to test.
     * @return <code>true</code> if the specified coordinates are
     *         inside the boundary of this <code>Rectangle2D</code>;
     *         <code>false</code> otherwise.
     */
    public boolean contains(final double x, final double y)
    {return (x>=xmin && y>=ymin && x<xmax && y<ymax);}

    /**
     * Tests if the interior of this <code>Rectangle2D</code>
     * intersects the interior of a specified set of rectangular
     * coordinates.
     *
     * @param x,&nbsp;y the coordinates of the upper left corner
     *        of the specified set of rectangular coordinates
     * @param width the width of the specified set of rectangular coordinates
     * @param height the height of the specified set of rectangular coordinates
     * @return <code>true</code> if this <code>Rectangle2D</code>
     * intersects the interior of a specified set of rectangular
     * coordinates; <code>false</code> otherwise.
     */
    public boolean intersects(final double x, final double y, final double width, final double height)
    {
        if (!(xmin<xmax && ymin<ymax && width>0 && height>0)) return false;
        return (x<xmax && y<ymax && x+width>xmin && y+height>ymin);
    }

    /**
     * Tests if the interior of this <code>Rectangle2D</code> entirely
     * contains the specified set of rectangular coordinates.
     *
     * @param x,&nbsp;y the coordinates of the upper left corner
     *        of the specified set of rectangular coordinates
     * @param width the width of the specified set of rectangular coordinates
     * @param height the height of the specified set of rectangular coordinates
     * @return <code>true</code> if this <code>Rectangle2D</code>
     *         entirely contains specified set of rectangular
     *         coordinates; <code>false</code> otherwise.
     */
    public boolean contains(final double x, final double y, final double width, final double height)
    {
        if (!(xmin<xmax && ymin<ymax && width>0 && height>0)) return false;
        return (x>=xmin && y>=ymin && (x+width)<=xmax && (y+height)<=ymax);
    }

    /**
     * Determines where the specified coordinates lie with respect
     * to this <code>Rectangle2D</code>.
     * This method computes a binary OR of the appropriate mask values
     * indicating, for each side of this <code>Rectangle2D</code>,
     * whether or not the specified coordinates are on the same side
     * of the edge as the rest of this <code>Rectangle2D</code>.
     *
     * @param x,&nbsp;y the specified coordinates
     * @return the logical OR of all appropriate out codes.
     *
     * @see #OUT_LEFT
     * @see #OUT_TOP
     * @see #OUT_RIGHT
     * @see #OUT_BOTTOM
     */
    public int outcode(final double x, final double y)
    {
        int out=0;
        if (!(xmax > xmin)) out |= OUT_LEFT | OUT_RIGHT;
        else if (x < xmin)  out |= OUT_LEFT;
        else if (x > xmax)  out |= OUT_RIGHT;

        if (!(ymax > ymin)) out |= OUT_TOP | OUT_BOTTOM;
        else if (y < ymin)  out |= OUT_TOP;
        else if (y > ymax)  out |= OUT_BOTTOM;
        return out;
    }

    /**
     * Returns a new <code>Rectangle2D</code> object representing the
     * intersection of this <code>Rectangle2D</code> with the specified
     * <code>Rectangle2D</code>.
     *
     * @param  rect the <code>Rectangle2D</code> to be intersected with this <code>Rectangle2D</code>
     * @return the largest <code>Rectangle2D</code> contained in both the specified
     *         <code>Rectangle2D</code> and in this <code>Rectangle2D</code>.
     */
    public Rectangle2D createIntersection(final Rectangle2D rect)
    {
        final XRectangle2D r=new XRectangle2D();
        r.xmin = Math.max(xmin, rect.getMinX());
        r.ymin = Math.max(ymin, rect.getMinY());
        r.xmax = Math.min(xmax, rect.getMaxX());
        r.ymax = Math.min(ymax, rect.getMaxY());
        return r;
    }
    
    /**
     * Returns a new <code>Rectangle2D</code> object representing the
     * union of this <code>Rectangle2D</code> with the specified
     * <code>Rectangle2D</code>.
     *
     * @param rect the <code>Rectangle2D</code> to be combined with
     *             this <code>Rectangle2D</code>
     * @return the smallest <code>Rectangle2D</code> containing both
     *         the specified <code>Rectangle2D</code> and this
     *         <code>Rectangle2D</code>.
     */
    public Rectangle2D createUnion(final Rectangle2D rect)
    {
        final XRectangle2D r=new XRectangle2D();
        r.xmin = Math.min(xmin, rect.getMinX());
        r.ymin = Math.min(ymin, rect.getMinY());
        r.xmax = Math.max(xmax, rect.getMaxX());
        r.ymax = Math.max(ymax, rect.getMaxY());
        return r;
    }

    /**
     * Adds a point, specified by the double precision arguments
     * <code>x</code> and <code>y</code>, to this <code>Rectangle2D</code>.
     * The resulting <code>Rectangle2D</code> is the smallest <code>Rectangle2D</code>
     * that contains both the original <code>Rectangle2D</code> and the specified point.
     * <p>
     * After adding a point, a call to <code>contains</code> with the
     * added point as an argument does not necessarily return
     * <code>true</code>. The <code>contains</code> method does not
     * return <code>true</code> for points on the right or bottom
     * edges of a rectangle. Therefore, if the added point falls on
     * the left or bottom edge of the enlarged rectangle,
     * <code>contains</code> returns <code>false</code> for that point.
     *
     */
    public void add(final double x, final double y)
    {
        if (x<xmin) xmin=x;
        if (x>xmax) xmax=x;
        if (y<ymin) ymin=y;
        if (y>ymax) ymax=y;
    }

    /**
     * Adds a <code>Rectangle2D</code> object to this <code>Rectangle2D</code>.
     * The resulting <code>Rectangle2D</code> is the union of the two
     * <code>Rectangle2D</code> objects.
     *
     * @param rect the <code>Rectangle2D</code> to add to this <code>Rectangle2D</code>.
     */
    public void add(final Rectangle2D rect)
    {
        double t;
        if ((t=rect.getMinX()) < xmin) xmin=t;
        if ((t=rect.getMaxX()) > xmax) xmax=t;
        if ((t=rect.getMinY()) < ymin) ymin=t;
        if ((t=rect.getMaxY()) > ymax) ymax=t;
    }

    /**
     * Returns the <code>String</code> representation of this <code>Rectangle2D</code>.
     *
     * @return a <code>String</code> representing this <code>Rectangle2D</code>.
     */
    public String toString()
    {
        final StringBuffer buffer = new StringBuffer(Utilities.getShortClassName(this));
        final NumberFormat format = NumberFormat.getNumberInstance();
        final FieldPosition dummy = new FieldPosition(0);
        buffer.append("[xmin="); format.format(xmin, buffer, dummy);
        buffer.append(" xmax="); format.format(xmax, buffer, dummy);
        buffer.append(" ymin="); format.format(ymin, buffer, dummy);
        buffer.append(" ymax="); format.format(ymax, buffer, dummy);
        buffer.append(']');
        return buffer.toString();
    }
}
