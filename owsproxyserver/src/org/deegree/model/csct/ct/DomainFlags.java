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

// OpenGIS dependencies
import java.io.ObjectStreamException;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.media.jai.EnumeratedParameter;

import org.deegree.model.csct.resources.XArray;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * Flags indicating parts of domain covered by a convex hull.
 * These flags can be combined.  For example, the enum
 * <code>{@link #INSIDE}.or({@link #OUTSIDE})</code>
 * means that some parts of the convex hull are inside the domain,
 * and some parts of the convex hull are outside the domain.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.ct.CT_DomainFlags
 */
public final class DomainFlags extends EnumeratedParameter {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 6991557069252861278L;

    /**
     * Domain flags by value. Used to
     * canonicalize after deserialization.
     */
    private static final DomainFlags[] ENUMS = new DomainFlags[8];

    /**
     * At least one point in a convex hull is inside the transform's domain.
     */
    public static final int CT_DF_Inside = 1;

    /**
     * At least one point in a convex hull is outside the transform's domain.
     */
    public static final int CT_DF_Outside = 2;

    /**
     * At least one point in a convex hull is not transformed continuously.
     * As an example, consider a "Longitude_Rotation" transform which adjusts
     * longitude coordinates to take account of a change in Prime Meridian.
     * If the rotation is 5 degrees east, then the point (Lat=0,Lon=175)
     * is not transformed continuously, since it is on the meridian line
     * which will be split at +180/-180 degrees.
     */
    public static final int CT_DF_Discontinuous = 4;

    /**
     * Initialize {@link #ENUMS} during class loading.
     * This code must be done before we initialize public fields.
     */
    static {
        for ( int i = ENUMS.length; --i >= 0; ) {
            String name = null;
            switch ( i ) {
            case 0:
                name = "UNKNOW";
                break;
            case CT_DF_Inside:
                name = "INSIDE";
                break;
            case CT_DF_Outside:
                name = "OUTSIDE";
                break;
            case CT_DF_Discontinuous:
                name = "DISCONTINUOUS";
                break;
            }
            ENUMS[i] = new DomainFlags( name, i );
        }
    }

    /**
     * At least one point in a convex hull is inside the transform's domain.
     *
     * @see org.opengis.ct.CT_DomainFlags#CT_DF_Inside
     */
    public static final DomainFlags INSIDE = ENUMS[CT_DF_Inside];

    /**
     * At least one point in a convex hull is outside the transform's domain.
     *
     * @see org.opengis.ct.CT_DomainFlags#CT_DF_Outside
     */
    public static final DomainFlags OUTSIDE = ENUMS[CT_DF_Outside];

    /**
     * At least one point in a convex hull is not transformed continuously.
     * As an example, consider a "Longitude_Rotation" transform which adjusts
     * longitude coordinates to take account of a change in Prime Meridian.
     * If the rotation is 5 degrees east, then the point (Lat=0,Lon=175)
     * is not transformed continuously, since it is on the meridian line
     * which will be split at +180/-180 degrees.
     *
     * @see org.opengis.ct.CT_DomainFlags#CT_DF_Discontinuous
     */
    public static final DomainFlags DISCONTINUOUS = ENUMS[CT_DF_Discontinuous];

    /**
     * Construct a new enum value.
     */
    private DomainFlags( final String name, final int value ) {
        super( name, value );
    }

    /**
     * Return the enum for the specified value.
     * This method is provided for compatibility with
     * {@link org.opengis.ct.CT_DomainFlags}.
     *
     * @param  value The enum value.
     * @return The enum for the specified value.
     * @throws NoSuchElementException if there is no enum for the specified value.
     */
    public static DomainFlags getEnum( final int value )
                            throws NoSuchElementException {
        if ( value >= 1 && value < ENUMS.length )
            return ENUMS[value];
        throw new NoSuchElementException( String.valueOf( value ) );
    }

    /**
     * Returns enum's names in the specified locale.
     * For example if this enum has value "3", then <code>getNames</code>
     * returns an array of two elements: "Inside" and "Outside".
     *
     * @param  locale The locale, or <code>null</code> for the current default locale.
     * @return Enum's names in the specified locale (never <code>null</code>).
     */
    public String[] getNames( final Locale locale ) {
        int count = 0;
        int bits = getValue();
        Resources resources = null;
        final int[] nameKeys = { ResourceKeys.INSIDE, ResourceKeys.OUTSIDE,
                                ResourceKeys.DISCONTINUOUS };
        final String[] names = new String[nameKeys.length];
        for ( int i = 0; i < nameKeys.length; i++ ) {
            if ( ( bits & 1 ) != 0 ) {
                if ( resources == null )
                    resources = Resources.getResources( locale );
                names[count++] = resources.getString( nameKeys[i] );
            }
            bits >>>= 1;
        }
        return (String[]) XArray.resize( names, count );
    }

    /**
     * Returns a combination of two domain flags.
     * This is equivalent to <code>getEnum(this.getValue()&nbsp;|&nbsp;flags.getValue())</code>.
     */
    public DomainFlags or( final DomainFlags flags ) {
        return getEnum( getValue() | flags.getValue() );
    }

    /**
     * Use a single instance of {@link DomainFlags} after deserialization.
     * It allow client code to test <code>enum1==enum2</code> instead of
     * <code>enum1.equals(enum2)</code>.
     *
     * @return A single instance of this enum.
     * @throws ObjectStreamException is deserialization failed.
     */
    private Object readResolve() {
        final int value = getValue();
        if ( value >= 0 && value < ENUMS.length ) {
            return ENUMS[value]; // Canonicalize
        }
        return ENUMS[0]; // Collapse unknow value to a single canonical one
    }
}
