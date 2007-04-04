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
package org.deegree.model.csct.cs;

// OpenGIS dependencies
import java.io.ObjectStreamException;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.media.jai.EnumeratedParameter;

import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * Orientation of axis. Some coordinate systems use non-standard orientations.
 * For example, the first axis in South African grids usually points West,
 * instead of East. This information is obviously relevant for algorithms
 * converting South African grid coordinates into Lat/Long.
 * <br><br>
 * The <em>natural ordering</em> for axis orientations is defined
 * as (EAST-WEST), (NORTH-SOUTH), (UP-DOWN), (FUTURE-PAST) and OTHER, which is
 * the ordering for a (<var>x</var>,<var>y</var>,<var>z</var>,<var>t</var>) coordinate
 * system. This means that when an array of <code>AxisOrientation</code>s is sorted using
 * {@link java.util.Arrays#sort(Object[])}, EAST and WEST orientations will
 * appears first. NORTH and SOUTH will be next, followed by UP and DOWN, etc.
 *
 * Care should be exercised if <code>AxisOrientation</code>s are to be used as keys in
 * a sorted map or elements in a sorted set, as <code>AxisOrientation</code>'s natural
 * ordering is inconsistent with equals. See {@link java.lang.Comparable},
 * {@link java.util.SortedMap} or {@link java.util.SortedSet} for more information.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_AxisOrientationEnum
 */
public final class AxisOrientation extends EnumeratedParameter implements Comparable {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 4649182002820021468L;

    /**
     * Unknown or unspecified axis orientation.
     * This can be used for local or fitted coordinate systems.
     */
    public static final int CS_AO_Other=0;

    /**
     * Increasing ordinates values go North.
     * This is usually used for Grid Y coordinates and Latitude.
     */
    public static final int CS_AO_North=1;

    /**
     * Increasing ordinates values go South.
     * This is rarely used.
     */
    public static final int CS_AO_South=2;

    /**
     * Increasing ordinates values go East.
     * This is rarely used.
     */
    public static final int CS_AO_East=3;

    /**
     * Increasing ordinates values go West.
     * This is usually used for Grid X coordinates and Longitude.
     */
    public static final int CS_AO_West=4;

    /**
     * Increasing ordinates values go up.
     * This is used for vertical coordinate systems.
     */
    public static final int CS_AO_Up=5;

    /**
     * Increasing ordinates values go down.
     * This is used for vertical coordinate systems.
     */
    public static final int CS_AO_Down=6;

    /**
     * Unknown or unspecified axis orientation.
     * This can be used for local or fitted coordinate systems.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_Other
     */
    public static final AxisOrientation OTHER = new AxisOrientation( "OTHER",
                                                                     CS_AO_Other,
                                                                     ResourceKeys.OTHER );

    /**
     * Increasing ordinates values go North.
     * This is usually used for Grid Y coordinates and Latitude.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_North
     */
    public static final AxisOrientation NORTH = new AxisOrientation( "NORTH",
                                                                     CS_AO_North,
                                                                     ResourceKeys.NORTH );

    /**
     * Increasing ordinates values go South.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_South
     */
    public static final AxisOrientation SOUTH = new AxisOrientation( "SOUTH",
                                                                     CS_AO_South,
                                                                     ResourceKeys.SOUTH );

    /**
     * Increasing ordinates values go East.
     * This is usually used for Grid X coordinates and Longitude.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_East
     */
    public static final AxisOrientation EAST = new AxisOrientation( "EAST",
                                                                    CS_AO_East,
                                                                    ResourceKeys.EAST );

    /**
     * Increasing ordinates values go West.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_West
     */
    public static final AxisOrientation WEST = new AxisOrientation( "WEST",
                                                                    CS_AO_West,
                                                                    ResourceKeys.WEST );

    /**
     * Increasing ordinates values go up.
     * This is used for vertical coordinate systems.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_Up
     */
    public static final AxisOrientation UP = new AxisOrientation( "UP",
                                                                  CS_AO_Up,
                                                                  ResourceKeys.UP );

    /**
     * Increasing ordinates values go down.
     * This is used for vertical coordinate systems.
     *
     * @see org.opengis.cs.CS_AxisOrientationEnum#CS_AO_Down
     */
    public static final AxisOrientation DOWN = new AxisOrientation( "DOWN",
                                                                    CS_AO_Down,
                                                                    ResourceKeys.DOWN );

    /**
     * Increasing time go toward future.
     * This is used for temporal axis.
     */
    public static final AxisOrientation FUTURE = new AxisOrientation( "FUTURE", 7,
                                                                      ResourceKeys.FUTURE );

    /**
     * Increasing time go toward past.
     * This is used for temporal axis.
     */
    public static final AxisOrientation PAST = new AxisOrientation( "PAST", 8, ResourceKeys.PAST );

    /**
     * The last paired value. Paired value are NORTH-SOUTH, EAST-WEST,
     * UP-DOWN, FUTURE-PAST.
     */
    private static final int LAST_PAIRED_VALUE = 8;

    /**
     * Axis orientations by value. Used to
     * canonicalize after deserialization.
     */
    private static final AxisOrientation[] ENUMS = { OTHER, NORTH, SOUTH, EAST, WEST, UP, DOWN,
                                                    FUTURE, PAST };

    /**
     * The axis order. Used for {@link #compareTo} implementation.
     */
    private static final AxisOrientation[] ORDER = { EAST, NORTH, UP, FUTURE };

    /**
     * Resource key, used for building localized name. This key doesn't need to
     * be serialized, since {@link #readResolve} canonicalize enums according their
     * {@link #value}. Furthermore, its value is implementation-dependent (which is
     * an other raison why it should not be serialized).
     */
    private transient final int key;

    /**
     * Construct a new enum with the specified value.
     */
    private AxisOrientation( final String name, final int value, final int key ) {
        super( name, value );
        this.key = key;
    }

    /**
     * Return the enum for the specified value.
     * This method is provided for compatibility with
     * {@link org.opengis.cs.CS_AxisOrientationEnum}.
     *
     * @param  value The enum value.
     * @return The enum for the specified value.
     * @throws NoSuchElementException if there is no enum for the specified value.
     */
    public static AxisOrientation getEnum( final int value )
                            throws NoSuchElementException {
        if ( value >= 0 && value < ENUMS.length )
            return ENUMS[value];
        throw new NoSuchElementException( String.valueOf( value ) );
    }

    /**
     * Returns this enum's name in the specified locale.
     * If no name is available for the specified locale, a default one will be used.
     *
     * @param  locale The locale, or <code>null</code> for the default locale.
     * @return Enum's name in the specified locale.
     */
    public String getName( final Locale locale ) {
        return Resources.getResources( locale ).getString( key );
    }

    /**
     * Returns the opposite orientation of this axis.
     * The opposite of North is South, and the opposite of South is North.
     * The same apply to East-West, Up-Down and Future-Past.
     * Other axis orientation are returned inchanged.
     */
    public AxisOrientation inverse() {
        final int value = getValue() - 1;
        if ( value >= 0 && value < LAST_PAIRED_VALUE ) {
            return ENUMS[( value ^ 1 ) + 1];
        }
        return this;
    }

    /**
     * Returns the "absolute" orientation of this axis.
     * This "absolute" operation is similar to the <code>Math.abs(int)</code> method
     * in that "negative" orientation (<code>SOUTH</code>, <code>WEST</code>, <code>DOWN</code>,
     * <code>PAST</code>) are changed for their positive counterpart (<code>NORTH</code>,
     * <code>EAST</code>, <code>UP</code>, <code>FUTURE</code>). More specifically, the
     * following conversion table is applied.
     * <br>&nbsp;
     * <table align="center" cellpadding="3" border="1" bgcolor="F4F8FF">
     *   <tr bgcolor="#B9DCFF">
     *     <th>&nbsp;&nbsp;Orientation&nbsp;&nbsp;</th>
     *     <th>&nbsp;&nbsp;Absolute value&nbsp;&nbsp;</th>
     *   </tr>
     *   <tr align="center"><td>NORTH</td> <td>NORTH</td> </tr>
     *   <tr align="center"><td>SOUTH</td> <td>NORTH</td> </tr>
     *   <tr align="center"><td>EAST</td>  <td>EAST</td>  </tr>
     *   <tr align="center"><td>WEST</td>  <td>EAST</td>  </tr>
     *   <tr align="center"><td>UP</td>    <td>UP</td>    </tr>
     *   <tr align="center"><td>DOWN</td>  <td>UP</td>    </tr>
     *   <tr align="center"><td>FUTURE</td><td>FUTURE</td></tr>
     *   <tr align="center"><td>PAST</td>  <td>FUTURE</td></tr>
     *   <tr align="center"><td>OTHER</td> <td>OTHER</td> </tr>
     * </table>
     */
    public AxisOrientation absolute() {
        final int value = getValue() - 1;
        if ( value >= 0 && value < LAST_PAIRED_VALUE ) {
            return ENUMS[( value & ~1 ) + 1];
        }
        return this;
    }

    /**
     * Compares this <code>AxisOrientation</code> with the specified orientation.
     * The <em>natural ordering</em> is defined as (EAST-WEST), (NORTH-SOUTH),
     * (UP-DOWN), (FUTURE-PAST) and OTHER, which is the ordering for a
     * (<var>x</var>,<var>y</var>,<var>z</var>,<var>t</var>) coordinate system.
     * Two <code>AxisOrientation</code> that are among the same axis but with an
     * opposite direction (e.g. EAST vs WEST) are considered equal by this method.
     *
     * @param  ao An <code>AxisOrientation</code> object to be compared with.
     * @throws ClassCastException if <code>ao</code> is not an <code>AxisOrientation</code> object.
     */
    public int compareTo( final Object ao ) {
        final AxisOrientation that = (AxisOrientation) ao;
        final int thisOrder = this.absolute().getOrder();
        final int thatOrder = that.absolute().getOrder();
        if ( thisOrder > thatOrder )
            return +1;
        if ( thisOrder < thatOrder )
            return -1;
        return 0;
    }

    /**
     * Returns the order for this axis orientation
     * (i.e. the index in the {@link #ORDER} table).
     */
    private int getOrder() {
        int i;
        for ( i = 0; i < ORDER.length; i++ )
            if ( equals( ORDER[i] ) )
                break;
        return i;
    }

    /**
     * Use a single instance of {@link AxisOrientation} after deserialization.
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
