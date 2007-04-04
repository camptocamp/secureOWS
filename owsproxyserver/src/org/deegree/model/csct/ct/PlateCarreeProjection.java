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

// OpenGIS (SEAS) dependencies
import java.awt.geom.Point2D;
import java.util.Locale;

import org.deegree.model.csct.cs.Projection;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 *
 */
final class PlateCarreeProjection extends CylindricalProjection
{      

	private double long0 	= 0;
	private double lat0 	= 0;
	private double e2		= 0;
	private double r		= 0;
   
    /**
     * Construct a new map projection from the suplied parameters.
     *
     * @param  parameters The parameter values in standard units.
     * @throws MissingParameterException if a mandatory parameter is missing.
     */
    protected PlateCarreeProjection(final Projection parameters) throws MissingParameterException
    {
    	super( parameters );
    	
    	// calculate constants
    	e2 = (a*a - b*b)/(a*a);
		r = Math.sqrt( a*a * (1 - e2) );
    }

    /**
     * Returns a human readable name localized for the specified locale.
     */
    public String getName(final Locale locale)
    {return Resources.getResources(locale).getString(ResourceKeys.PLATE_CARREE_PROJECTION);}

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinate
     * and stores the result in <code>ptDst</code>.
     */
    protected Point2D transform(double x, double y, final Point2D ptDst) throws TransformException
    {
    	/*
		For the forward calculation:
		
		X =  R . (Long - longO) . cos(latO)
		Y =  R .  Lat
		
		where R = SQRT[a^2 * (1-e^2)]
		and Lat and Long are expressed in radians.
		
		For the reverse calculation:
		
		Lat = Y / R  
		Long = LongO + (X / R cos(latO))
		*/		
		x = r * ( x - long0 ) * Math.cos( lat0 );
		y = r * y;
    	return new Point2D.Double(x,y);
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinate
     * and stores the result in <code>ptDst</code>.
     */
    protected Point2D inverseTransform(double x, double y, final Point2D ptDst) throws TransformException
    {
    	x = long0 + ( x / r * Math.cos(lat0) );
    	y = y / r;
        return new Point2D.Double(x,y);
    }

    /**
     * Returns a hash value for this map projection.
     */
    public int hashCode()
    {
        final long code = Double.doubleToLongBits(1234);
        return ((int)code ^ (int)(code >>> 32)) + 37*super.hashCode();
    }

    /**
     * Compares the specified object with
     * this map projection for equality.
     */
    public boolean equals(final Object object)
    {
        return false;
    }

    /**
     * Impl�mentation de la partie entre crochets
     * de la cha�ne retourn�e par {@link #toString()}.
     */
    void toString(final StringBuffer buffer)
    {
        super.toString(buffer);
    }

    /**
     * Informations about a {@link StereographicProjection}.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    static final class Provider extends MapProjection.Provider
    {        

        /**
         * Construct a new provider. The type (polar, oblique
         * or equatorial) will be choosen automatically according
         * the latitude or origin.
         */
        public Provider()
        {
            super("PlateCarree", ResourceKeys.PLATE_CARREE_PROJECTION);
     
        }        

        /**
         * Create a new map projection.
         */
        protected Object create(final Projection parameters)
        {return new PlateCarreeProjection(parameters);}
    }
}
