/*
 * SEAGIS - An OpenSource implementation of OpenGIS specification
 *          (C) 2002, Institut de Recherche pour le D�veloppement
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
 */
package org.deegree.model.csct.units.resources;

// Miscellaneous
import java.util.Locale;
import java.util.MissingResourceException;

import org.deegree.model.csct.resources.ResourceBundle;


/**
 * Base class for local-dependent resources. Instances of this class should
 * never been created directly. Use the factory method {@link #getResources}
 * or use static methods instead.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class Resources extends ResourceBundle
{
    /**
     * Construct a resource bundle using english language.
     * This is the default when no resource are available
     * in user language.
     */
    public Resources()
    {
        super(// Set 'true' in front of language to use as default.
              false ? Resources_fr.FILEPATH :
               true ? Resources_en.FILEPATH :
               null);
    }

    /**
     * Construct a resource bundle
     * using the specified UTF8 file.
     */
    Resources(final String filepath)
    {super(filepath);}

    /**
     * Returns the name of the logger to use,
     * which is <code>javax.units</code>.
     */
    protected String getLoggerName()
    {return "javax.units";}

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or <code>null</code> for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Resources getResources(Locale locale) throws MissingResourceException
    {
        if (locale==null) locale = Locale.getDefault();
        return (Resources) getBundle(Resources.class.getName(), locale);
        /*
         * We rely on cache capability of {@link java.util.ResourceBundle}.
         */
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int key) throws MissingResourceException
    {return getResources(null).getString(key);}

    /**
     * Gets a string for the given key are replace all occurence of "{0}"
     * with values of <code>arg0</code>.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int key, final Object arg0) throws MissingResourceException
    {return getResources(null).getString(key, arg0);}

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of <code>arg0</code>, <code>arg1</code>.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int key, final Object arg0, final Object arg1) throws MissingResourceException
    {return getResources(null).getString(key, arg0, arg1);}
}
