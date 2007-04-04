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

// Miscellaneous
import java.awt.geom.Dimension2D;
import java.io.Serializable;


/**
 * Implement float and double version of {@link Dimension2D}. This class
 * is only temporary; it will disaspear if <em>JavaSoft</em> implements
 * <code>Dimension2D.Float</code> and <code>Dimension2D.Double</code>.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public final class XDimension2D
{
    /**
     * Do not allow instantiation of this class.
     */
    private XDimension2D()
    {}

    /**
     * Implement float version of {@link Dimension2D}. This class is
     * temporary;  it will disaspear if <em>JavaSoft</em> implements
     * <code>Dimension2D.Float</code> and <code>Dimension2D.Double</code>.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    public static final class Float extends Dimension2D implements Serializable
    {
        /**
         * Largeur de la dimension.
         */
        public float width;

        /**
         * Hauteur de la dimension.
         */
        public float height;

        /**
         * Construit un objet avec les dimensions (0,0).
         */
        public Float()
        {}

        /**
         * Construit un objet avec les dimensions sp�cifi�es.
         * @param w largeur.
         * @param h hauteur.
         */
        public Float(final float w, final float h)
        {width=w; height=h;}

        /**
         * Change les dimensions de cet objet.
         * @param w largeur.
         * @param h hauteur.
         */
        public void setSize(final double w, final double h)
        {width=(float) w; height=(float) h;}

        /**
         * Retourne la largeur.
         */
        public double getWidth()
        {return width;}

        /**
         * Retourne la hauteur.
         */
        public double getHeight()
        {return height;}

        /**
         * Retourne la dimension sous forme de cha�ne de caract�res.
         * La cha�ne sera de la forme "<code>Dimension2D[45,76]</code>".
         */
        public String toString()
        {return "Dimension2D["+width+','+height+']';}
    }

    /**
     * Implement double version of {@link Dimension2D}. This class is
     * temporary;  it will disaspear if <em>JavaSoft</em> implements
     * <code>Dimension2D.Float</code> and <code>Dimension2D.Double</code>.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    public static final class Double extends Dimension2D implements Serializable
    {
        /**
         * Largeur de la dimension.
         */
        public double width;

        /**
         * Hauteur de la dimension.
         */
        public double height;

        /**
         * Construit un objet avec les dimensions (0,0).
         */
        public Double()
        {}

        /**
         * Construit un objet avec les dimensions sp�cifi�es.
         * @param w largeur.
         * @param h hauteur.
         */
        public Double(final double w, final double h)
        {width=w; height=h;}

        /**
         * Change les dimensions de cet objet.
         * @param w largeur.
         * @param h hauteur.
         */
        public void setSize(final double w, final double h)
        {width=w; height=h;}

        /**
         * Retourne la largeur.
         */
        public double getWidth()
        {return width;}

        /**
         * Retourne la hauteur.
         */
        public double getHeight()
        {return height;}

        /**
         * Retourne la dimension sous forme de cha�ne de caract�res.
         * La cha�ne sera de la forme "<code>Dimension2D[45,76]</code>".
         */
        public String toString()
        {return "Dimension2D["+width+','+height+']';}
    }
}
