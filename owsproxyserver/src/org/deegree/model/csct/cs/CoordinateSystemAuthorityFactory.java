/*
 * SEAGIS - An OpenSource implementation of OpenGIS specification
 *          (C) 2001, Institut de Recherche pour le D�veloppement
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Contacts:
 *     FRANCE: Surveillance de l'Environnement Assist�e par Satellite
 *             Institut de Recherche pour le D�veloppement / US-Espace
 *             mailto:seasnet@teledetection.fr
 *
 *     CANADA: Observatoire du Saint-Laurent
 *             Institut Maurice-Lamontagne
 *             mailto:osl@osl.gc.ca
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.deegree.model.csct.cs;

// Properties
import java.util.Map;
import java.util.NoSuchElementException;

import visad.Unit;


/**
 * Creates spatial reference objects using codes.
 * The codes are maintained by an external authority.
 * A commonly used authority is EPSG, which is also
 * used in the GeoTIFF standard.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_CoordinateSystemAuthorityFactory
 */
public abstract class CoordinateSystemAuthorityFactory
{
    /**
     * The underlying factory used for objects creation.
     */
    protected final CoordinateSystemFactory factory;

    /**
     * Construct an authority factory using the
     * specified coordinate system factory.
     *
     * @param factory The underlying factory used for objects creation.
     */
    public CoordinateSystemAuthorityFactory(final CoordinateSystemFactory factory)
    {
        Info.ensureNonNull("factory", factory);
        this.factory = factory;
    }

    /**
     * Returns the authority name.
     */
    public abstract String getAuthority();

    /**
     * Returns an {@link Ellipsoid} object from a code.
     *
     * @param  code Value allocated by authority.
     * @return The ellipsoid object.
     * @throws NoSuchElementException if this method can't find the requested code.
     *
     */
    public Ellipsoid createEllipsoid(final String code) throws NoSuchElementException
    {throw new NoSuchElementException(code);}


    /**
     * Returns a {@link Unit} object from a code.
     *
     * @param  code Value allocated by authority.
     * @return The unit object.
     * @throws NoSuchElementException if this method can't find the requested code.
     *
     */
    public Unit createUnit(final String code) throws NoSuchElementException
    {throw new NoSuchElementException(code);}


    /**
     * Set the properties fon an {@link Info} object. This method
     * should be invoked from all <code>create*</code> methods.
     *
     * @param info         The {@link Info} object to set properties.
     * @param code         The authority code (must not be <code>null</code>).
     * @param alias        The alias, or <code>null</code> if none.
     * @param abbreviation The abbreviation, or <code>null</code> if none.
     * @param remarks      The remarks, or <code>null</code> if none.
     */
    final void setProperties(final Info info, final String code, final String alias,
                             final String abbreviation, final String remarks)
    {
        Info.ensureNonNull("code", code);
        final Map properties = null;  // TODO: Fetch the properties from the Info object.
        if (properties!=null) return; // TODO
        properties.put("authority", getAuthority());
        properties.put("code", code);
        if (alias!=null)
        {
            properties.put("alias", alias);
        }
        if (abbreviation!=null)
        {
            properties.put("abbreviation", abbreviation);
        }
        if (remarks!=null)
        {
            properties.put("remarks", remarks);
        }
    }
}
