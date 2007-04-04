/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon GmbH
 http://www.lat-lon.de

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

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.media.jai.ParameterList;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.ParameterListImpl;
import javax.media.jai.util.Range;

import org.deegree.model.csct.units.Unit;

/**
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class ConvenienceCSFactory {

    private static ConvenienceCSFactory factory = null;

    private CoordinateSystemFactory csFactory = CoordinateSystemFactory.getDefault();

    private static ParameterListDescriptor pld = getDescriptor( new Object[] {
                                                                              "semi_major",
                                                                              Double.class,
                                                                              ParameterListDescriptor.NO_PARAMETER_DEFAULT,
                                                                              null,
                                                                              "semi_minor",
                                                                              Double.class,
                                                                              ParameterListDescriptor.NO_PARAMETER_DEFAULT,
                                                                              null,
                                                                              "central_meridian",
                                                                              Double.class,
                                                                              new Double( 0 ),
                                                                              null,
                                                                              "latitude_of_origin",
                                                                              Double.class,
                                                                              new Double( 0 ),
                                                                              null,
                                                                              "false_easting",
                                                                              Double.class,
                                                                              new Double( 0 ),
                                                                              null,
                                                                              "false_northing",
                                                                              Double.class,
                                                                              new Double( 0 ),
                                                                              null,
                                                                              "scale_factor",
                                                                              Double.class,
                                                                              new Double( 1 ),
                                                                              null,
                                                                              "longitudeOfFalseOrigin",
                                                                              Double.class,
                                                                              new Double( 0 ),
                                                                              null,
                                                                              "latitudeofFalseOrigin",
                                                                              Double.class,
                                                                              new Double( 90 ),
                                                                              null,
                                                                              "standard_parallel1",
                                                                              Double.class,
                                                                              new Double( 30 ),
                                                                              null,
                                                                              "standard_parallel2",
                                                                              Double.class,
                                                                              new Double( 40 ),
                                                                              null, "hemisphere",
                                                                              Integer.class,
                                                                              new Integer( 1 ),
                                                                              null } );

    // keys are names (i.e. "EPSG:4326"), values are CoordinateSystems
    private Hashtable systems = new Hashtable();

    // values are names (Strings)
    private Vector names = new Vector();

    /**
     * implementation f the singleton pattern.
     */
    public static ConvenienceCSFactory getInstance() {
        if ( factory == null ) {
            factory = new ConvenienceCSFactory();
        }

        return factory;
    }

    /**
     *
     *
     * @param cs 
     */
    private void save( CoordinateSystem cs ) {
        systems.put( cs.getName( Locale.getDefault() ), cs );
        names.add( cs.getName( Locale.getDefault() ) );
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public CoordinateSystem getCSByName( String name ) {
        name = name.toUpperCase();

        CoordinateSystem cs = (CoordinateSystem) systems.get( name.toUpperCase() );

        if ( cs != null ) {
            return cs;
        }

        //        if ( name.equalsIgnoreCase( "EPSG:2056" ) ) {
        //            addEPSG2056();
        //        } else 
        if ( name.equalsIgnoreCase( "EPSG:4269" ) ) {
            addEPSG4269();
        } else if ( name.equalsIgnoreCase( "EPSG:4267" ) ) {
            addEPSG4267();
        } else if ( name.equalsIgnoreCase( "EPSG:20790" ) ) {
            addEPSG20790();
        } else if ( name.equalsIgnoreCase( "EPSG:21780" ) ) {
            addEPSG21780();
        } else if ( name.equalsIgnoreCase( "EPSG:4806" ) ) {
            addEPSG4806();
        } else if ( name.equalsIgnoreCase( "EPSG:4322" ) ) {
            addEPSG4322();
        } else if ( name.equalsIgnoreCase( "EPSG:4324" ) ) {
            addEPSG4324();
        } else if ( name.equalsIgnoreCase( "EPSG:4274" ) ) {
            addEPSG4274();
        } else if ( name.equalsIgnoreCase( "EPSG:4803" ) ) {
            addEPSG4803();
        } else if ( name.equalsIgnoreCase( "EPSG:4308" ) ) {
            addEPSG4308();
        } else if ( name.equalsIgnoreCase( "EPSG:4807" ) ) {
            addEPSG4807();
        } else if ( name.equalsIgnoreCase( "EPSG:4817" ) ) {
            addEPSG4817();
        } else if ( name.equalsIgnoreCase( "EPSG:4801" ) ) {
            addEPSG4801();
        } else if ( name.equalsIgnoreCase( "EPSG:4272" ) ) {
            addEPSG4272();
        } else if ( name.equalsIgnoreCase( "EPSG:21781" ) ) {
            addEPSG21781();
        } else if ( name.equalsIgnoreCase( "EPSG:23028" ) ) {
            addEPSG23028();
        } else if ( name.equalsIgnoreCase( "EPSG:23029" ) ) {
            addEPSG23029();
        } else if ( name.equalsIgnoreCase( "EPSG:23030" ) ) {
            addEPSG23030();
        } else if ( name.equalsIgnoreCase( "EPSG:23031" ) ) {
            addEPSG23031();
        } else if ( name.equalsIgnoreCase( "EPSG:23032" ) ) {
            addEPSG23032();
        } else if ( name.equalsIgnoreCase( "EPSG:23033" ) ) {
            addEPSG23033();
        } else if ( name.equalsIgnoreCase( "EPSG:23034" ) ) {
            addEPSG23034();
        } else if ( name.equalsIgnoreCase( "EPSG:23035" ) ) {
            addEPSG23035();
        } else if ( name.equalsIgnoreCase( "EPSG:23036" ) ) {
            addEPSG23036();
        } else if ( name.equalsIgnoreCase( "EPSG:23037" ) ) {
            addEPSG23037();
        } else if ( name.equalsIgnoreCase( "EPSG:23038" ) ) {
            addEPSG23038();
        } else if ( name.equalsIgnoreCase( "EPSG:23090" ) ) {
            addEPSG23090();
        } else if ( name.equalsIgnoreCase( "EPSG:23095" ) ) {
            addEPSG23095();
        } else if ( name.equalsIgnoreCase( "EPSG:25884" ) ) {
            addEPSG25884();
        } else if ( name.equalsIgnoreCase( "EPSG:25828" ) || name.equalsIgnoreCase( "EPSG:25829" )
                    || name.equalsIgnoreCase( "EPSG:25830" )
                    || name.equalsIgnoreCase( "EPSG:25831" )
                    || name.equalsIgnoreCase( "EPSG:25832" )
                    || name.equalsIgnoreCase( "EPSG:25833" )
                    || name.equalsIgnoreCase( "EPSG:25834" )
                    || name.equalsIgnoreCase( "EPSG:25835" )
                    || name.equalsIgnoreCase( "EPSG:25836" )
                    || name.equalsIgnoreCase( "EPSG:25837" )
                    || name.equalsIgnoreCase( "EPSG:25838" ) ) {
            String s = name.substring( 8, 10 );
            int code = Integer.parseInt( s );
            addEPSG258XX( code );
        } else if ( name.equalsIgnoreCase( "EPSG:26591" ) ) {
            addEPSG26591();
        } else if ( name.equalsIgnoreCase( "EPSG:26592" ) ) {
            addEPSG26592();
        } else if ( name.equalsIgnoreCase( "EPSG:27391" ) ) {
            addEPSG27391();
        } else if ( name.equalsIgnoreCase( "EPSG:27392" ) ) {
            addEPSG27392();
        } else if ( name.equalsIgnoreCase( "EPSG:27393" ) ) {
            addEPSG27393();
        } else if ( name.equalsIgnoreCase( "EPSG:27394" ) ) {
            addEPSG27394();
        } else if ( name.equalsIgnoreCase( "EPSG:27395" ) ) {
            addEPSG27395();
        } else if ( name.equalsIgnoreCase( "EPSG:27396" ) ) {
            addEPSG27396();
        } else if ( name.equalsIgnoreCase( "EPSG:27397" ) ) {
            addEPSG27397();
        } else if ( name.equalsIgnoreCase( "EPSG:27398" ) ) {
            addEPSG27398();
        } else if ( name.equalsIgnoreCase( "EPSG:27429" ) ) {
            addEPSG27429();
        } else if ( name.equalsIgnoreCase( "EPSG:27700" ) ) {
            addEPSG27700();
        } else if ( name.equalsIgnoreCase( "EPSG:28402" ) ) {
            addEPSG28402();
        } else if ( name.equalsIgnoreCase( "EPSG:28403" ) ) {
            addEPSG28403();
        } else if ( name.equalsIgnoreCase( "EPSG:28404" ) ) {
            addEPSG28404();
        } else if ( name.equalsIgnoreCase( "EPSG:28405" ) ) {
            addEPSG28405();
        } else if ( name.equalsIgnoreCase( "EPSG:28406" ) ) {
            addEPSG28406();
        } else if ( name.equalsIgnoreCase( "EPSG:28407" ) ) {
            addEPSG28407();
        } else if ( name.equalsIgnoreCase( "EPSG:28408" ) ) {
            addEPSG28408();
        } else if ( name.equalsIgnoreCase( "EPSG:28409" ) ) {
            addEPSG28409();
        } else if ( name.equalsIgnoreCase( "EPSG:28462" ) ) {
            addEPSG28462();
        } else if ( name.equalsIgnoreCase( "EPSG:28462" ) || name.equalsIgnoreCase( "EPSG:28463" )
                    || name.equalsIgnoreCase( "EPSG:28464" )
                    || name.equalsIgnoreCase( "EPSG:28465" )
                    || name.equalsIgnoreCase( "EPSG:28466" )
                    || name.equalsIgnoreCase( "EPSG:28467" )
                    || name.equalsIgnoreCase( "EPSG:28468" )
                    || name.equalsIgnoreCase( "EPSG:28469" ) ) {
            String s = name.substring( 9 );
            int code = Integer.parseInt( s );
            addEPSG2846X( code );
        } else if ( name.equalsIgnoreCase( "EPSG:29900" ) ) {
            addEPSG29900();
        } else if ( name.equalsIgnoreCase( "EPSG:30800" ) ) {
            addEPSG30800();
        } else if ( name.equalsIgnoreCase( "EPSG:31275" ) ) {
            addEPSG31275();
        } else if ( name.equalsIgnoreCase( "EPSG:31276" ) ) {
            addEPSG31276();
        } else if ( name.equalsIgnoreCase( "EPSG:31277" ) ) {
            addEPSG31277();
        } else if ( name.equalsIgnoreCase( "EPSG:31278" ) ) {
            addEPSG31278();
        } else if ( name.equalsIgnoreCase( "EPSG:31281" ) ) {
            addEPSG31281();
        } else if ( name.equalsIgnoreCase( "EPSG:31282" ) ) {
            addEPSG31282();
        } else if ( name.equalsIgnoreCase( "EPSG:31283" ) ) {
            addEPSG31283();
        } else if ( name.equalsIgnoreCase( "EPSG:31284" ) ) {
            addEPSG31284();
        } else if ( name.equalsIgnoreCase( "EPSG:31285" ) ) {
            addEPSG31285();
        } else if ( name.equalsIgnoreCase( "EPSG:31286" ) ) {
            addEPSG31286();
        } else if ( name.equalsIgnoreCase( "EPSG:31466" ) || name.equalsIgnoreCase( "EPSG:31467" )
                    || name.equalsIgnoreCase( "EPSG:31468" )
                    || name.equalsIgnoreCase( "EPSG:31469" ) ) {
            String s = name.substring( 9 );
            int code = Integer.parseInt( s );
            addEPSG3146X( code );
        } else if ( name.equalsIgnoreCase( "EPSG:31491" ) || name.equalsIgnoreCase( "EPSG:31492" )
                    || name.equalsIgnoreCase( "EPSG:31493" )
                    || name.equalsIgnoreCase( "EPSG:31494" )
                    || name.equalsIgnoreCase( "EPSG:31495" ) ) {
            String s = name.substring( 9 );
            int code = Integer.parseInt( s );
            addEPSG3149X( code );
        } else if ( name.equalsIgnoreCase( "EPSG:32201" ) || name.equalsIgnoreCase( "EPSG:32202" )
                    || name.equalsIgnoreCase( "EPSG:32203" )
                    || name.equalsIgnoreCase( "EPSG:32204" )
                    || name.equalsIgnoreCase( "EPSG:32205" )
                    || name.equalsIgnoreCase( "EPSG:32206" )
                    || name.equalsIgnoreCase( "EPSG:32207" )
                    || name.equalsIgnoreCase( "EPSG:32208" )
                    || name.equalsIgnoreCase( "EPSG:32209" )
                    || name.equalsIgnoreCase( "EPSG:32210" )
                    || name.equalsIgnoreCase( "EPSG:32211" )
                    || name.equalsIgnoreCase( "EPSG:32212" )
                    || name.equalsIgnoreCase( "EPSG:32213" )
                    || name.equalsIgnoreCase( "EPSG:32214" )
                    || name.equalsIgnoreCase( "EPSG:32215" )
                    || name.equalsIgnoreCase( "EPSG:32216" )
                    || name.equalsIgnoreCase( "EPSG:32217" )
                    || name.equalsIgnoreCase( "EPSG:32218" )
                    || name.equalsIgnoreCase( "EPSG:32219" )
                    || name.equalsIgnoreCase( "EPSG:32220" )
                    || name.equalsIgnoreCase( "EPSG:32221" )
                    || name.equalsIgnoreCase( "EPSG:32222" )
                    || name.equalsIgnoreCase( "EPSG:32223" )
                    || name.equalsIgnoreCase( "EPSG:32224" )
                    || name.equalsIgnoreCase( "EPSG:32225" )
                    || name.equalsIgnoreCase( "EPSG:32226" )
                    || name.equalsIgnoreCase( "EPSG:32227" )
                    || name.equalsIgnoreCase( "EPSG:32228" )
                    || name.equalsIgnoreCase( "EPSG:32229" )
                    || name.equalsIgnoreCase( "EPSG:32230" )
                    || name.equalsIgnoreCase( "EPSG:32231" )
                    || name.equalsIgnoreCase( "EPSG:32232" )
                    || name.equalsIgnoreCase( "EPSG:32233" )
                    || name.equalsIgnoreCase( "EPSG:32234" )
                    || name.equalsIgnoreCase( "EPSG:32235" )
                    || name.equalsIgnoreCase( "EPSG:32236" )
                    || name.equalsIgnoreCase( "EPSG:32237" )
                    || name.equalsIgnoreCase( "EPSG:32238" )
                    || name.equalsIgnoreCase( "EPSG:32239" )
                    || name.equalsIgnoreCase( "EPSG:32240" )
                    || name.equalsIgnoreCase( "EPSG:32241" )
                    || name.equalsIgnoreCase( "EPSG:32242" )
                    || name.equalsIgnoreCase( "EPSG:32243" )
                    || name.equalsIgnoreCase( "EPSG:32244" )
                    || name.equalsIgnoreCase( "EPSG:32245" )
                    || name.equalsIgnoreCase( "EPSG:32246" )
                    || name.equalsIgnoreCase( "EPSG:32247" )
                    || name.equalsIgnoreCase( "EPSG:32248" )
                    || name.equalsIgnoreCase( "EPSG:32249" )
                    || name.equalsIgnoreCase( "EPSG:32250" )
                    || name.equalsIgnoreCase( "EPSG:32251" )
                    || name.equalsIgnoreCase( "EPSG:32252" )
                    || name.equalsIgnoreCase( "EPSG:32253" )
                    || name.equalsIgnoreCase( "EPSG:32254" )
                    || name.equalsIgnoreCase( "EPSG:32255" )
                    || name.equalsIgnoreCase( "EPSG:32256" )
                    || name.equalsIgnoreCase( "EPSG:32257" )
                    || name.equalsIgnoreCase( "EPSG:32258" )
                    || name.equalsIgnoreCase( "EPSG:32259" )
                    || name.equalsIgnoreCase( "EPSG:32260" ) ) {
            String s = name.substring( 8, 10 );
            int code = Integer.parseInt( s );
            addEPSG322XX( code );
        } else if ( name.equalsIgnoreCase( "EPSG:32401" ) || name.equalsIgnoreCase( "EPSG:32402" )
                    || name.equalsIgnoreCase( "EPSG:32403" )
                    || name.equalsIgnoreCase( "EPSG:32404" )
                    || name.equalsIgnoreCase( "EPSG:32405" )
                    || name.equalsIgnoreCase( "EPSG:32406" )
                    || name.equalsIgnoreCase( "EPSG:32407" )
                    || name.equalsIgnoreCase( "EPSG:32408" )
                    || name.equalsIgnoreCase( "EPSG:32409" )
                    || name.equalsIgnoreCase( "EPSG:32410" )
                    || name.equalsIgnoreCase( "EPSG:32411" )
                    || name.equalsIgnoreCase( "EPSG:32412" )
                    || name.equalsIgnoreCase( "EPSG:32413" )
                    || name.equalsIgnoreCase( "EPSG:32414" )
                    || name.equalsIgnoreCase( "EPSG:32415" )
                    || name.equalsIgnoreCase( "EPSG:32416" )
                    || name.equalsIgnoreCase( "EPSG:32417" )
                    || name.equalsIgnoreCase( "EPSG:32418" )
                    || name.equalsIgnoreCase( "EPSG:32419" )
                    || name.equalsIgnoreCase( "EPSG:32420" )
                    || name.equalsIgnoreCase( "EPSG:32421" )
                    || name.equalsIgnoreCase( "EPSG:32422" )
                    || name.equalsIgnoreCase( "EPSG:32423" )
                    || name.equalsIgnoreCase( "EPSG:32424" )
                    || name.equalsIgnoreCase( "EPSG:32425" )
                    || name.equalsIgnoreCase( "EPSG:32426" )
                    || name.equalsIgnoreCase( "EPSG:32427" )
                    || name.equalsIgnoreCase( "EPSG:32428" )
                    || name.equalsIgnoreCase( "EPSG:32429" )
                    || name.equalsIgnoreCase( "EPSG:32430" )
                    || name.equalsIgnoreCase( "EPSG:32431" )
                    || name.equalsIgnoreCase( "EPSG:32432" )
                    || name.equalsIgnoreCase( "EPSG:32433" )
                    || name.equalsIgnoreCase( "EPSG:32434" )
                    || name.equalsIgnoreCase( "EPSG:32435" )
                    || name.equalsIgnoreCase( "EPSG:32436" )
                    || name.equalsIgnoreCase( "EPSG:32437" )
                    || name.equalsIgnoreCase( "EPSG:32438" )
                    || name.equalsIgnoreCase( "EPSG:32439" )
                    || name.equalsIgnoreCase( "EPSG:32440" )
                    || name.equalsIgnoreCase( "EPSG:32441" )
                    || name.equalsIgnoreCase( "EPSG:32442" )
                    || name.equalsIgnoreCase( "EPSG:32443" )
                    || name.equalsIgnoreCase( "EPSG:32444" )
                    || name.equalsIgnoreCase( "EPSG:32445" )
                    || name.equalsIgnoreCase( "EPSG:32446" )
                    || name.equalsIgnoreCase( "EPSG:32447" )
                    || name.equalsIgnoreCase( "EPSG:32448" )
                    || name.equalsIgnoreCase( "EPSG:32449" )
                    || name.equalsIgnoreCase( "EPSG:32450" )
                    || name.equalsIgnoreCase( "EPSG:32451" )
                    || name.equalsIgnoreCase( "EPSG:32452" )
                    || name.equalsIgnoreCase( "EPSG:32453" )
                    || name.equalsIgnoreCase( "EPSG:32454" )
                    || name.equalsIgnoreCase( "EPSG:32455" )
                    || name.equalsIgnoreCase( "EPSG:32456" )
                    || name.equalsIgnoreCase( "EPSG:32457" )
                    || name.equalsIgnoreCase( "EPSG:32458" )
                    || name.equalsIgnoreCase( "EPSG:32459" )
                    || name.equalsIgnoreCase( "EPSG:32460" ) ) {
            String s = name.substring( 8, 10 );
            int code = Integer.parseInt( s );
            addEPSG324XX( code );
        } else if ( name.equalsIgnoreCase( "EPSG:32601" ) || name.equalsIgnoreCase( "EPSG:32602" )
                    || name.equalsIgnoreCase( "EPSG:32603" )
                    || name.equalsIgnoreCase( "EPSG:32604" )
                    || name.equalsIgnoreCase( "EPSG:32605" )
                    || name.equalsIgnoreCase( "EPSG:32606" )
                    || name.equalsIgnoreCase( "EPSG:32607" )
                    || name.equalsIgnoreCase( "EPSG:32608" )
                    || name.equalsIgnoreCase( "EPSG:32609" )
                    || name.equalsIgnoreCase( "EPSG:32610" )
                    || name.equalsIgnoreCase( "EPSG:32611" )
                    || name.equalsIgnoreCase( "EPSG:32612" )
                    || name.equalsIgnoreCase( "EPSG:32613" )
                    || name.equalsIgnoreCase( "EPSG:32614" )
                    || name.equalsIgnoreCase( "EPSG:32615" )
                    || name.equalsIgnoreCase( "EPSG:32616" )
                    || name.equalsIgnoreCase( "EPSG:32617" )
                    || name.equalsIgnoreCase( "EPSG:32618" )
                    || name.equalsIgnoreCase( "EPSG:32619" )
                    || name.equalsIgnoreCase( "EPSG:32620" )
                    || name.equalsIgnoreCase( "EPSG:32621" )
                    || name.equalsIgnoreCase( "EPSG:32622" )
                    || name.equalsIgnoreCase( "EPSG:32623" )
                    || name.equalsIgnoreCase( "EPSG:32624" )
                    || name.equalsIgnoreCase( "EPSG:32625" )
                    || name.equalsIgnoreCase( "EPSG:32626" )
                    || name.equalsIgnoreCase( "EPSG:32627" )
                    || name.equalsIgnoreCase( "EPSG:32628" )
                    || name.equalsIgnoreCase( "EPSG:32629" )
                    || name.equalsIgnoreCase( "EPSG:32630" )
                    || name.equalsIgnoreCase( "EPSG:32631" )
                    || name.equalsIgnoreCase( "EPSG:32632" )
                    || name.equalsIgnoreCase( "EPSG:32633" )
                    || name.equalsIgnoreCase( "EPSG:32634" )
                    || name.equalsIgnoreCase( "EPSG:32635" )
                    || name.equalsIgnoreCase( "EPSG:32636" )
                    || name.equalsIgnoreCase( "EPSG:32637" )
                    || name.equalsIgnoreCase( "EPSG:32638" )
                    || name.equalsIgnoreCase( "EPSG:32639" )
                    || name.equalsIgnoreCase( "EPSG:32640" )
                    || name.equalsIgnoreCase( "EPSG:32641" )
                    || name.equalsIgnoreCase( "EPSG:32642" )
                    || name.equalsIgnoreCase( "EPSG:32643" )
                    || name.equalsIgnoreCase( "EPSG:32644" )
                    || name.equalsIgnoreCase( "EPSG:32645" )
                    || name.equalsIgnoreCase( "EPSG:32646" )
                    || name.equalsIgnoreCase( "EPSG:32647" )
                    || name.equalsIgnoreCase( "EPSG:32648" )
                    || name.equalsIgnoreCase( "EPSG:32649" )
                    || name.equalsIgnoreCase( "EPSG:32650" )
                    || name.equalsIgnoreCase( "EPSG:32651" )
                    || name.equalsIgnoreCase( "EPSG:32652" )
                    || name.equalsIgnoreCase( "EPSG:32653" )
                    || name.equalsIgnoreCase( "EPSG:32654" )
                    || name.equalsIgnoreCase( "EPSG:32655" )
                    || name.equalsIgnoreCase( "EPSG:32656" )
                    || name.equalsIgnoreCase( "EPSG:32657" )
                    || name.equalsIgnoreCase( "EPSG:32658" )
                    || name.equalsIgnoreCase( "EPSG:32659" )
                    || name.equalsIgnoreCase( "EPSG:32660" ) ) {
            String s = name.substring( 8, 10 );
            int code = Integer.parseInt( s );
            addEPSG326XX( code );
        } else if ( name.equalsIgnoreCase( "EPSG:32701" ) || name.equalsIgnoreCase( "EPSG:32702" )
                    || name.equalsIgnoreCase( "EPSG:32703" )
                    || name.equalsIgnoreCase( "EPSG:32704" )
                    || name.equalsIgnoreCase( "EPSG:32705" )
                    || name.equalsIgnoreCase( "EPSG:32706" )
                    || name.equalsIgnoreCase( "EPSG:32707" )
                    || name.equalsIgnoreCase( "EPSG:32708" )
                    || name.equalsIgnoreCase( "EPSG:32709" )
                    || name.equalsIgnoreCase( "EPSG:32710" )
                    || name.equalsIgnoreCase( "EPSG:32711" )
                    || name.equalsIgnoreCase( "EPSG:32712" )
                    || name.equalsIgnoreCase( "EPSG:32713" )
                    || name.equalsIgnoreCase( "EPSG:32714" )
                    || name.equalsIgnoreCase( "EPSG:32715" )
                    || name.equalsIgnoreCase( "EPSG:32716" )
                    || name.equalsIgnoreCase( "EPSG:32717" )
                    || name.equalsIgnoreCase( "EPSG:32718" )
                    || name.equalsIgnoreCase( "EPSG:32719" )
                    || name.equalsIgnoreCase( "EPSG:32720" )
                    || name.equalsIgnoreCase( "EPSG:32721" )
                    || name.equalsIgnoreCase( "EPSG:32722" )
                    || name.equalsIgnoreCase( "EPSG:32723" )
                    || name.equalsIgnoreCase( "EPSG:32724" )
                    || name.equalsIgnoreCase( "EPSG:32725" )
                    || name.equalsIgnoreCase( "EPSG:32726" )
                    || name.equalsIgnoreCase( "EPSG:32727" )
                    || name.equalsIgnoreCase( "EPSG:32728" )
                    || name.equalsIgnoreCase( "EPSG:32729" )
                    || name.equalsIgnoreCase( "EPSG:32730" )
                    || name.equalsIgnoreCase( "EPSG:32731" )
                    || name.equalsIgnoreCase( "EPSG:32732" )
                    || name.equalsIgnoreCase( "EPSG:32733" )
                    || name.equalsIgnoreCase( "EPSG:32734" )
                    || name.equalsIgnoreCase( "EPSG:32735" )
                    || name.equalsIgnoreCase( "EPSG:32736" )
                    || name.equalsIgnoreCase( "EPSG:32737" )
                    || name.equalsIgnoreCase( "EPSG:32738" )
                    || name.equalsIgnoreCase( "EPSG:32739" )
                    || name.equalsIgnoreCase( "EPSG:32740" )
                    || name.equalsIgnoreCase( "EPSG:32741" )
                    || name.equalsIgnoreCase( "EPSG:32742" )
                    || name.equalsIgnoreCase( "EPSG:32743" )
                    || name.equalsIgnoreCase( "EPSG:32744" )
                    || name.equalsIgnoreCase( "EPSG:32745" )
                    || name.equalsIgnoreCase( "EPSG:32746" )
                    || name.equalsIgnoreCase( "EPSG:32747" )
                    || name.equalsIgnoreCase( "EPSG:32748" )
                    || name.equalsIgnoreCase( "EPSG:32749" )
                    || name.equalsIgnoreCase( "EPSG:32750" )
                    || name.equalsIgnoreCase( "EPSG:32751" )
                    || name.equalsIgnoreCase( "EPSG:32752" )
                    || name.equalsIgnoreCase( "EPSG:32753" )
                    || name.equalsIgnoreCase( "EPSG:32754" )
                    || name.equalsIgnoreCase( "EPSG:32755" )
                    || name.equalsIgnoreCase( "EPSG:32756" )
                    || name.equalsIgnoreCase( "EPSG:32757" )
                    || name.equalsIgnoreCase( "EPSG:32758" )
                    || name.equalsIgnoreCase( "EPSG:32759" )
                    || name.equalsIgnoreCase( "EPSG:32760" ) ) {
            String s = name.substring( 8, 10 );
            int code = Integer.parseInt( s );
            addEPSG327XX( code );
        } else if ( name.equalsIgnoreCase( "EPSG:4120" ) ) {
            addEPSG4120();
        } else if ( name.equalsIgnoreCase( "EPSG:4121" ) ) {
            addEPSG4121();
        } else if ( name.equalsIgnoreCase( "EPSG:4124" ) ) {
            addEPSG4124();
        } else if ( name.equalsIgnoreCase( "EPSG:4149" ) ) {
            addEPSG4149();
        } else if ( name.equalsIgnoreCase( "EPSG:4150" ) ) {
            addEPSG4150();
        } else if ( name.equalsIgnoreCase( "EPSG:4151" ) ) {
            addEPSG4151();
        } else if ( name.equalsIgnoreCase( "EPSG:4171" ) ) {
            addEPSG4171();
        } else if ( name.equalsIgnoreCase( "EPSG:4173" ) ) {
            addEPSG4173();
        } else if ( name.equalsIgnoreCase( "EPSG:4230" ) ) {
            addEPSG4230();
        } else if ( name.equalsIgnoreCase( "EPSG:4231" ) ) {
            addEPSG4231();
        } else if ( name.equalsIgnoreCase( "EPSG:4237" ) ) {
            addEPSG4237();
        } else if ( name.equalsIgnoreCase( "EPSG:4258" ) ) {
            addEPSG4258();
        } else if ( name.equalsIgnoreCase( "EPSG:4265" ) ) {
            addEPSG4265();
        } else if ( name.equalsIgnoreCase( "EPSG:4275" ) ) {
            addEPSG4275();
        } else if ( name.equalsIgnoreCase( "EPSG:4277" ) ) {
            addEPSG4277();
        } else if ( name.equalsIgnoreCase( "EPSG:4284" ) ) {
            addEPSG4284();
        } else if ( name.equalsIgnoreCase( "EPSG:4289" ) ) {
            addEPSG4289();
        } else if ( name.equalsIgnoreCase( "EPSG:4299" ) ) {
            addEPSG4299();
        } else if ( name.equalsIgnoreCase( "EPSG:4312" ) ) {
            addEPSG4312();
        } else if ( name.equalsIgnoreCase( "EPSG:4313" ) ) {
            addEPSG4313();
        } else if ( name.equalsIgnoreCase( "EPSG:4314" ) ) {
            addEPSG4314();
        } else if ( name.equalsIgnoreCase( "EPSG:32661" ) ) {
            addEPSG32661();
        } else if ( name.equalsIgnoreCase( "EPSG:4326" )
                    || name.equalsIgnoreCase( "urn:ogc:def:crs:OGC:2:84" )
                    || ( name.equalsIgnoreCase( "CRS:84" ) ) ) {
            addEPSG4326( name );
        } else if ( name.equalsIgnoreCase( "LuRef" ) ) {
            addLuRef();
        } else if ( name.equalsIgnoreCase( "EPSG:31287" ) ) {
            addEPSG31287();
        } else if ( name.equalsIgnoreCase( "EPSG:31300" ) ) {
            addEPSG31300();
        } else if ( name.equalsIgnoreCase( "EPSG:27561" ) ) {
            addEPSG27561();
        } else if ( name.equalsIgnoreCase( "EPSG:27562" ) ) {
            addEPSG27562();
        } else if ( name.equalsIgnoreCase( "EPSG:27563" ) ) {
            addEPSG27563();
        } else if ( name.equalsIgnoreCase( "EPSG:27564" ) ) {
            addEPSG27564();
        } else if ( name.equalsIgnoreCase( "EPSG:27571" ) ) {
            addEPSG27571();
        } else if ( name.equalsIgnoreCase( "EPSG:27572" ) ) {
            addEPSG27572();
        } else if ( name.equalsIgnoreCase( "EPSG:27573" ) ) {
            addEPSG27573();
        } else if ( name.equalsIgnoreCase( "EPSG:27574" ) ) {
            addEPSG27574();
        } else if ( name.equalsIgnoreCase( "EPSG:27581" ) ) {
            addEPSG27581();
        } else if ( name.equalsIgnoreCase( "EPSG:27582" ) ) {
            addEPSG27582();
        } else if ( name.equalsIgnoreCase( "EPSG:27583" ) ) {
            addEPSG27583();
        } else if ( name.equalsIgnoreCase( "EPSG:27584" ) ) {
            addEPSG27584();
        } else if ( name.equalsIgnoreCase( "EPSG:27591" ) ) {
            addEPSG27591();
        } else if ( name.equalsIgnoreCase( "EPSG:27592" ) ) {
            addEPSG27592();
        } else if ( name.equalsIgnoreCase( "EPSG:27593" ) ) {
            addEPSG27593();
        } else if ( name.equalsIgnoreCase( "EPSG:27594" ) ) {
            addEPSG27594();
        } else if ( name.equalsIgnoreCase( "EPSG:27291" ) ) {
            addEPSG27291();
        } else if ( name.equalsIgnoreCase( "EPSG:27292" ) ) {
            addEPSG27292();
        } else if ( name.equalsIgnoreCase( "EPSG:27200" ) ) {
            addEPSG27200();
        } else if ( name.equalsIgnoreCase( "EPSG:26716" ) ) {
            addEPSG26716();
        } else if ( name.equalsIgnoreCase( "EPSG:28992" ) ) {
            addEPSG28992();
        } else if ( name.equalsIgnoreCase( "EPSG:26912" ) ) {
            addEPSG26912();
        } else if ( name.equalsIgnoreCase( "EPSG:2152" ) ) {
            addEPSG2152();
        }
        return (CoordinateSystem) systems.get( name );
    }

    /**
     *
     */
    private void addEPSG4267() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Clarke 1866", 6378206.4,
                                                               294.978698, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -3;
        convInfo.dy = 142;
        convInfo.dz = 183;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4267",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    private void addEPSG4269() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1980", 6378137.0, 298.2572221,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4269",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     * Returns the parameter list descriptor for the specified properties list.
     */
    private static ParameterListDescriptor getDescriptor( final Object[] properties ) {
        final String[] names = new String[properties.length / 4];
        final Class[] classes = new Class[names.length];
        final Object[] defaults = new Object[names.length];
        final Range[] ranges = new Range[names.length];
        for ( int i = 0; i < names.length; i++ ) {
            final int j = i * 4;
            names[i] = (String) properties[j + 0];
            classes[i] = (Class) properties[j + 1];
            defaults[i] = properties[j + 2];
            ranges[i] = (Range) properties[j + 3];
        }
        return new ParameterListDescriptorImpl( null, names, classes, defaults, ranges );
    }

    /**
     *
     */
    private void addEPSG4326( String name ) {
        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    name,
                                                                                    Unit.DEGREE,
                                                                                    HorizontalDatum.WGS84,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );

        save( cs );
    }

    /**
     * <p>Norway - onshore.</p>
     * <p>Geodetic survey. Recommended coordinate axis representation for the 
     * human interface.</p>
     */
    private void addEPSG4817() {
        Ellipsoid ellipsoid = csFactory.createFlattenedSphere( "NGO 1948 (Oslo)", 6377492.018,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 0;
        convInfo.dy = 0;
        convInfo.dz = 0;

        HorizontalDatum hz = csFactory.createHorizontalDatum( "NGO 1948 (Oslo)", DatumType.CLASSIC,
                                                              ellipsoid, convInfo );

        PrimeMeridian pm = csFactory.createPrimeMeridian( "NGO 1948 (Oslo)", Unit.DEGREE,
                                                          10.72291667 );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4817",
                                                                                    Unit.DEGREE,
                                                                                    hz,
                                                                                    pm,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );

        save( cs );
    }

    /**
     *
     */
    private void addEPSG4322() {
        Ellipsoid ellipsoid = csFactory.createFlattenedSphere( "WGS 72", 6378135, 298.26,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 0;
        convInfo.dy = 0;
        convInfo.dz = 4.5;
        convInfo.ex = 0;
        convInfo.ey = 0;
        convInfo.ez = 0.554;
        convInfo.ppm = 0.2263;

        HorizontalDatum hz = csFactory.createHorizontalDatum( "WGS 72", DatumType.CLASSIC,
                                                              ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4322",
                                                                                    Unit.DEGREE,
                                                                                    hz,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );

        save( cs );
    }

    /**
     *
     */
    private void addEPSG4324() {
        Ellipsoid ellipsoid = csFactory.createFlattenedSphere( "WGS 72BE", 6378135, 298.26,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 0;
        convInfo.dy = 0;
        convInfo.dz = 1.9;
        convInfo.ex = 0;
        convInfo.ey = 0;
        convInfo.ez = 0.814;
        convInfo.ppm = -0.38;

        HorizontalDatum hz = csFactory.createHorizontalDatum( "WGS 72 Transit Broadcast Ephemeris",
                                                              DatumType.CLASSIC, ellipsoid,
                                                              convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4324",
                                                                                    Unit.DEGREE,
                                                                                    hz,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );

        save( cs );
    }

    /**
     *
     */
    private void addEPSG4314() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        convInfo.dx = 606.0;
        convInfo.dy = 23;
        convInfo.dz = 413;
        convInfo.ex = 0;
        convInfo.ey = 0;
        convInfo.ez = 0;
        convInfo.ppm = 0;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4314",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     * NZGD49 / alias = GD49
     * <p>New Zealand</p>
     * <p>Geodetic survey. Recommended coordinate axis representation for the 
     * human interface. Superseded by NZGD49 in March 2000. New Zealand Department 
     * of Lands and Surveys Technical Report No. 1; 1978.</p>
     */
    private void addEPSG4272() {

        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "International 1924", 6378388.0,
                                                               297.0, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 59.47;
        convInfo.dy = -5.04;
        convInfo.dz = 187.44;
        convInfo.ex = -0.47;
        convInfo.ey = 0.1;
        convInfo.ez = -1.024;
        convInfo.ppm = -4.5993;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4272",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4230() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "International 1924", 6378388.0,
                                                               297.0, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -87;
        convInfo.dy = -98;
        convInfo.dz = -121;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4230",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4801() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 660.077;
        convInfo.dy = 13.551;
        convInfo.dz = 369.344;
        convInfo.ex = 2.484;
        convInfo.ey = 1.738;
        convInfo.ez = 2.939;
        convInfo.ppm = 5.66;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4801",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4806() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "International 1924", 6378388.0,
                                                               297.0, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -225;
        convInfo.dy = -65;
        convInfo.dz = -9;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        PrimeMeridian pm = csFactory.createPrimeMeridian( "Rome", Unit.DEGREE, 12.45233333333333 );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4806",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    pm,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23028() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = -15.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23028",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23029() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = -9.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23029",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23030() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = -3.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23030",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23031() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 3.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23031",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23032() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 9.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23032",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23033() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 15.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23033",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23034() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 21.0;
        double easting = 0;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23034",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23035() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 27.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23035",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23036() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 33.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23036",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23037() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 39.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23037",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23038() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 45.0;
        double easting = 500000;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23038",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23090() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 0.0;
        double easting = 0;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23090",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG23095() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 5.0;
        double easting = 0;
        double northing = 0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:23095",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * German Gauss-Kr�ger Zones
     *
     * @param code 
     */
    private void addEPSG3146X( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4314" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( ( code - 6 ) * 3.0 ) + 6;
        double easting = 2500000 + ( ( code - 6 ) * 1000000 );
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:3146"
                                                                                  + code, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * German Gauss-Kr�ger Zones (old EPSG:code)
     *
     * @param code 
     */
    private void addEPSG3149X( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4314" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( code * 3.0 );
        double easting = 2500000 + ( ( code - 2 ) * 1000000 );
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:3149"
                                                                                  + code, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4231() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "International 1924", 6378388.0,
                                                               297.0, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -82.981;
        convInfo.dy = -99.719;
        convInfo.dz = -110.709;
        convInfo.ex = -0.5076;
        convInfo.ey = -0.35;
        convInfo.ez = 0.3898;
        convInfo.ppm = -0.3143;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4231",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4258() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1980", 6378137.0, 298.2572221,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4258",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4150() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 674.374;
        convInfo.dy = 15.056;
        convInfo.dz = 405.346;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4150",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4120() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -199.87;
        convInfo.dy = 74.79;
        convInfo.dz = 246.62;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4120",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4124() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 419.3836;
        convInfo.dy = 99.3335;
        convInfo.dz = 591.3451;
        convInfo.ex = -0.850389;
        convInfo.ey = -1.817277;
        convInfo.ez = 7.862238;
        convInfo.ppm = -0.99496;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4124",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4149() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 660.077;
        convInfo.dy = 13.551;
        convInfo.dz = 369.344;
        convInfo.ex = 2.484;
        convInfo.ey = 1.783;
        convInfo.ez = 2.939;
        convInfo.ppm = 5.66;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4149",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4151() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1980", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 674.374;
        convInfo.dy = 15.056;
        convInfo.dz = 405.346;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4151",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4121() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1980", 6378137.0, 298.2572221,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -199.87;
        convInfo.dy = 74.79;
        convInfo.dz = 246.62;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4121",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4171() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1980", 6378137.0, 298.2572221,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4171",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4173() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1980", 6378137.0, 298.2572221,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4173",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4237() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "GRS 1967", 6378160.0, 298.2471674,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -56;
        convInfo.dy = 75.77;
        convInfo.dz = 15.31;
        convInfo.ex = -0.37;
        convInfo.ey = -0.2;
        convInfo.ez = -0.21;
        convInfo.ppm = -1.01;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4237",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4265() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "International 1924", 6378388.0,
                                                               297.0, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -225;
        convInfo.dy = -65;
        convInfo.dz = 9;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4265",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4275() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Clark 1880 (IGN)", 6378249.2,
                                                               293.466021, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -168;
        convInfo.dy = -60;
        convInfo.dz = 320;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4275",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4807() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Clark 1880 (IGN)", 6378249.2,
                                                               293.466021, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -168;
        convInfo.dy = -60;
        convInfo.dz = +320;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        PrimeMeridian pm = csFactory.createPrimeMeridian( "Paris", Unit.DEGREE, 2.337229166666667 ); // 2.5969213 );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4807",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    pm,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4277() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Airy 1830", 6377563.0, 299.3249646,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 535.948;
        convInfo.dy = -31.357;
        convInfo.dz = 665.16;
        convInfo.ex = 0.15;
        convInfo.ey = 0.247;
        convInfo.ez = 0.998;
        convInfo.ppm = -21.689;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4277",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4284() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Krassowsky 1940", 6378245.0, 298.3,
                                                               Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 21.53219;
        convInfo.dy = -97.00027;
        convInfo.dz = -60.74046;
        convInfo.ex = -0.99548;
        convInfo.ey = -0.58147;
        convInfo.ez = -0.2418;
        convInfo.ppm = -4.5981;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4284",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4289() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 593.16;
        convInfo.dy = 26.15;
        convInfo.dz = 478.54;
        convInfo.ex = -6.3239;
        convInfo.ey = -0.5008;
        convInfo.ez = -5.5487;
        convInfo.ppm = 4.0775;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4289",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4299() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Airy Modified 1849", 6377340.189,
                                                               299.3249646, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 506;
        convInfo.dy = -122;
        convInfo.dz = 611;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4299",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4312() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = 682;
        convInfo.dy = -203;
        convInfo.dz = 480;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4312",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4308() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Bessel 1841", 6377397.155,
                                                               299.1528128, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        //??????
        convInfo.dx = 682;
        convInfo.dy = -203;
        convInfo.dz = 480;

        //??????
        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4308",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     * <p>Portugal - onshore.</p>
     * <p>Geodetic survey. Recommended coordinate axis representation for the 
     * human interface. Supersedes Lisbon 1890 system which used Bessel 1841 
     * ellipsoid.  Superseded by Datum 73 (code 4274).
     */
    private void addEPSG4803() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Lisbon 1937 (Lisbon)", 6378388,
                                                               297, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        //??????
        convInfo.dx = 0;
        convInfo.dy = 0;
        convInfo.dz = 0;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4803",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     * <p>Portugal - onshore.</p>
     * <p>Geodetic survey. Recommended coordinate axis representation for the 
     * human interface.</p>
     */
    private void addEPSG4274() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "Datum 73", 6378388, 297, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();

        convInfo.dx = -231;
        convInfo.dy = 102.6;
        convInfo.dz = 29.8;
        convInfo.ex = -0.615;
        convInfo.ey = -0.198;
        convInfo.ez = 0.881;
        convInfo.ppm = 1.79;

        HorizontalDatum horDatum = new HorizontalDatum( "Datum 73", DatumType.CLASSIC, ellipsoid,
                                                        convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4274",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG4313() {
        Ellipsoid ellipsoid = Ellipsoid.createFlattenedSphere( "International 1924", 6378388.0,
                                                               297.0, Unit.METRE );

        WGS84ConversionInfo convInfo = new WGS84ConversionInfo();
        convInfo.dx = -99.059;
        convInfo.dy = 53.322;
        convInfo.dz = -112.486;
        convInfo.ex = -0.419;
        convInfo.ey = 0.83;
        convInfo.ez = -1.885;
        convInfo.ppm = 0.999999;

        HorizontalDatum horDatum = new HorizontalDatum( "My HorizontalDatum", DatumType.CLASSIC,
                                                        ellipsoid, convInfo );

        GeographicCoordinateSystem cs = csFactory.createGeographicCoordinateSystem(
                                                                                    "EPSG:4313",
                                                                                    Unit.DEGREE,
                                                                                    horDatum,
                                                                                    PrimeMeridian.GREENWICH,
                                                                                    AxisInfo.LONGITUDE,
                                                                                    AxisInfo.LATITUDE );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG20790() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4803" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 1.0;
        double easting = 200000.0;
        double northing = 300000.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:20790",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG21780() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4801" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 0.0;
        double easting = 0.0;

        Projection projection = null;

        /*
         *   implement factory for oblique mercator projection
         */
        projection = csFactory.createProjection( "My projection", "Transverse_Mercator", ellipsoid,
                                                 new Point2D.Double( centerMeridian, 0 ),
                                                 new Point2D.Double( easting, 0 ), 0 );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:21780",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG21781() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4149" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 7.495833333; // (7-26-22,5)
        double easting = 600000.0;

        Projection projection = null;

        projection = csFactory.createProjection( "My projection", "Transverse_Mercator", ellipsoid,
                                                 new Point2D.Double( centerMeridian, 0 ),
                                                 new Point2D.Double( easting, 0 ), 0 );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem(
                                                                                  "EPSG:21781",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  org.deegree.model.csct.units.Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     *
     * @param code 
     */
    private void addEPSG258XX( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4258" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( ( code - 28 ) * 6.0 ) - 15.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem(
                                                                                  "EPSG:258" + code,
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );

        geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4314" );

        ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        save( cs );
    }

    /**
     *
     */
    private void addEPSG25884() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4258" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 24.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:25884",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG26591() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4806" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 9.0;
        double easting = 1500000.0;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:26591",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG26592() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4806" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 15.0;
        double easting = 2520000.0;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:26592",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27391() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 4.66667; //(4-40-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27391",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27392() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 2.33333; // (2-20-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27392",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27393() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 0.0;
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27393",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27394() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 2.50; //(2-30-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27394",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27395() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 6.16667; //(6-10-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27395",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27396() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 10.16667; //(10-10-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27396",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27397() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 14.16667; //(14-10-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27397",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27398() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4817" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 18.33333; //(18-20-0)
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27398",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27429() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4274" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 9.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27429",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG27700() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4277" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 2.0;
        double easting = 400000.0;
        double northing = -100000.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27700",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28402() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 9.0;
        double easting = 2500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28402",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28403() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 15.0;
        double easting = 3500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28403",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28404() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 21.0;
        double easting = 4500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28404",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28405() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 27.0;
        double easting = 5500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28405",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28406() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 33.0;
        double easting = 6500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28406",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28407() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 39.0;
        double easting = 7500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28407",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28408() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 45.0;
        double easting = 8500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28408",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28409() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 51.0;
        double easting = 9500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28409",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG28462() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 9.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28462",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     *
     * @param code 
     */
    private void addEPSG2846X( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4284" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( code * 6 ) - 3;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:2846X"
                                                                                  + code, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG29900() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4299" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 8.0;
        double easting = 200000.0;
        double northing = 250000.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:29900",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG30800() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4299" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 15.88277; //15-48-29,8
        double easting = 1500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:30800",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31275() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 15.0;
        double easting = 5500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31275",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31276() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 18.0;
        double easting = 6500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31276",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31277() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 21.0;
        double easting = 7500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31277",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31278() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 21.0;
        double easting = 7500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31278",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31281() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4314" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 28.0;
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31281",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31282() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4314" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 31.0;
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31282",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31283() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4314" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 34.0;
        double easting = 0.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31283",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31284() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 10.33333; //10-20-0
        double easting = 150000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31284",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31285() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 13.33333; //13-20-0
        double easting = 450000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31285",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG31286() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 16.33333; //16-20-0
        double easting = 750000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31286",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     *
     * @param code 
     */
    private void addEPSG322XX( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4322" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( ( code - 28 ) * 6.0 ) - 15.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        String s = null;

        if ( code < 10 ) {
            s = "EPSG:3220" + code;
        } else {
            s = "EPSG:322" + code;
        }

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( s, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     *
     * @param code 
     */
    private void addEPSG324XX( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4324" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( ( code - 28 ) * 6.0 ) - 15.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );
        String s = null;

        if ( code < 10 ) {
            s = "EPSG:3240" + code;
        } else {
            s = "EPSG:324" + code;
        }

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( s, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     *
     * @param code 
     */
    private void addEPSG326XX( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4326" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( ( code - 28 ) * 6.0 ) - 15.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        String s = null;

        if ( code < 10 ) {
            s = "EPSG:3260" + code;
        } else {
            s = "EPSG:326" + code;
        }

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( s, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     *
     * @param code 
     */
    private void addEPSG327XX( int code ) {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4326" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = ( ( code - 33 ) * 6.0 ) - 15.0;
        double easting = 500000.0;
        double northing = 1000000.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        String s = null;

        if ( code < 10 ) {
            s = "EPSG:3270" + code;
        } else {
            s = "EPSG:327" + code;
        }

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( s, geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addEPSG32661() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4326" );

        if ( geoCS == null ) {
            return;
        }

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 0.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 1.0;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:32661",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     *
     */
    private void addLuRef() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4230" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = 6.16666666666666666666666;
        double centerLat = 49.833333333333333333333333;
        double easting = 80000;
        double northing = 100000;
        double scaleFactor = 1;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian,
                                                                                centerLat ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "LuRef", geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * MGI / Austria Lambert
     */
    private void addEPSG31287() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4312" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 400000.0 );
        param.setParameter( "false_northing", 400000.0 );
        param.setParameter( "central_meridian", 13.33333333333333 );
        param.setParameter( "latitude_of_origin", 47.5 );
        param.setParameter( "standard_parallel1", 49.0 );
        param.setParameter( "standard_parallel2", 46.0 );
        param.setParameter( "scale_factor", 1.0 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31287",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * Belge 1972 / Belge Lambert 72
     */
    private void addEPSG31300() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4313" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 150000.013 );
        param.setParameter( "false_northing", 5400088.438 );
        param.setParameter( "central_meridian", 4.3674866666667 );
        param.setParameter( "latitude_of_origin", 90.0 );
        param.setParameter( "standard_parallel1", 49.8333339 );
        param.setParameter( "standard_parallel2", 51.1666672333333 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:31300",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**************************************************************************************************************
     * NTF (Paris) / Lambert Nord France
     */
    private void addEPSG27561() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 49.5 );
        param.setParameter( "standard_parallel1", 48.59852277777780 );
        param.setParameter( "standard_parallel2", 50.39591166666670 );
        param.setParameter( "scale_factor", 0.999877341 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27561",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert Centre France
     */
    private void addEPSG27562() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 46.80 );
        param.setParameter( "standard_parallel1", 45.8989188888889 );
        param.setParameter( "standard_parallel2", 47.6960144444444 );
        param.setParameter( "scale_factor", 0.99987742 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27562",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert Sud France
     */
    private void addEPSG27563() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 44.1 );
        param.setParameter( "standard_parallel1", 43.1992913888888889 );
        param.setParameter( "standard_parallel2", 44.9960938888888889 );
        param.setParameter( "scale_factor", 0.999877499 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27563",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert Corse
     */
    private void addEPSG27564() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 234.358 );
        param.setParameter( "false_northing", 185861.369 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 42.165 );
        param.setParameter( "standard_parallel1", 41.56038777777778 );
        param.setParameter( "standard_parallel2", 42.76766333333333 );
        param.setParameter( "scale_factor", 0.99994471 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27564",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert zone I
     */
    private void addEPSG27571() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 1200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 49.5 );
        param.setParameter( "standard_parallel1", 48.59852277777780 );
        param.setParameter( "standard_parallel2", 50.39591166666670 );
        param.setParameter( "scale_factor", 0.999877341 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27571",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert zone II (France II etendu)
     */
    private void addEPSG27572() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 2200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 46.8 );
        param.setParameter( "standard_parallel1", 45.8989188888889 );
        param.setParameter( "standard_parallel2", 47.6960144444444 );
        param.setParameter( "scale_factor", 0.99987742 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27572",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert zone III
     */
    private void addEPSG27573() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 3200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 44.1 );
        param.setParameter( "standard_parallel1", 43.1992913888888889 );
        param.setParameter( "standard_parallel2", 44.9960938888888889 );
        param.setParameter( "scale_factor", 0.999877499 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27573",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Lambert zone IV
     */
    private void addEPSG27574() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 234.358 );
        param.setParameter( "false_northing", 4185861.369 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 42.165 );
        param.setParameter( "standard_parallel1", 41.56038777777778 );
        param.setParameter( "standard_parallel2", 42.76766333333333 );
        param.setParameter( "scale_factor", 0.99994471 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27574",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / France I (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27581() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 1200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 49.5 );
        param.setParameter( "standard_parallel1", 48.59852277777780 );
        param.setParameter( "standard_parallel2", 50.39591166666670 );
        param.setParameter( "scale_factor", 0.999877341 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27581",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / France II (France II etendu) (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27582() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 2200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 46.8 );
        param.setParameter( "standard_parallel1", 45.8989188888889 );
        param.setParameter( "standard_parallel2", 47.6960144444444 );
        param.setParameter( "scale_factor", 0.99987742 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27582",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / France III (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27583() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 3200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 44.1 );
        param.setParameter( "standard_parallel1", 43.1992913888888889 );
        param.setParameter( "standard_parallel2", 44.9960938888888889 );
        param.setParameter( "scale_factor", 0.999877499 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27583",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / France IV (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27584() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 234.358 );
        param.setParameter( "false_northing", 4185861.369 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 42.165 );
        param.setParameter( "standard_parallel1", 41.56038777777778 );
        param.setParameter( "standard_parallel2", 42.76766333333333 );
        param.setParameter( "scale_factor", 0.99994471 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27584",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Nord France (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27591() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 49.5 );
        param.setParameter( "standard_parallel1", 48.59852277777780 );
        param.setParameter( "standard_parallel2", 50.39591166666670 );
        param.setParameter( "scale_factor", 0.999877341 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27591",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Centre France (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27592() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 46.80 );
        param.setParameter( "standard_parallel1", 45.8989188888889 );
        param.setParameter( "standard_parallel2", 47.6960144444444 );
        param.setParameter( "scale_factor", 0.99987742 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27592",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Sud France (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27593() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 600000.0 );
        param.setParameter( "false_northing", 200000.0 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 44.1 );
        param.setParameter( "standard_parallel1", 43.1992913888888889 );
        param.setParameter( "standard_parallel2", 44.9960938888888889 );
        param.setParameter( "scale_factor", 0.999877499 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27593",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * NTF (Paris) / Corse (Deprecated for EPSG version 6.5)
     */
    private void addEPSG27594() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4807" );

        if ( geoCS == null ) {
            return;
        }

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMajorAxis() );
        param.setParameter( "semi_minor",
                            geoCS.getHorizontalDatum().getEllipsoid().getSemiMinorAxis() );
        param.setParameter( "false_easting", 234.358 );
        param.setParameter( "false_northing", 185861.369 );
        param.setParameter( "central_meridian", 2.33722916666667 );
        param.setParameter( "latitude_of_origin", 42.165 );
        param.setParameter( "standard_parallel1", 41.56038777777778 );
        param.setParameter( "standard_parallel2", 42.76766333333333 );
        param.setParameter( "scale_factor", 0.99994471 );

        Projection projection = csFactory.createProjection( "My projection",
                                                            "Lambert_Conformal_Conic_2SP", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27594",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * <p>NZGD49 / North Island Grid </p>
     * <p>alias = GD49 / North Island Grid</p>
     * <p>New Zealand - North Island.</p>
     * <p>Large and medium scale topographic mapping and engineering survey.
     * Sears 1922 British foot-metre conversion factor applied to ellipsoid.  
     * Superseded by 27200 (GD49 / New Zealand Map Grid) in 1972.</p>
     * notice: british yards are used as units!
     */
    private void addEPSG27291() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4272" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "hemisphere", -1 );
        param.setParameter( "semi_major", ellipsoid.getSemiMajorAxis() );
        param.setParameter( "semi_minor", ellipsoid.getSemiMinorAxis() );
        param.setParameter( "false_easting", 300000.0 );
        param.setParameter( "false_northing", 400000.0 );
        param.setParameter( "central_meridian", -175.5 );
        param.setParameter( "latitude_of_origin", -39.0 );
        param.setParameter( "scale_factor", 1.0 );

        Projection projection = csFactory.createProjection( "My projection", "Transverse_Mercator",
                                                            param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27291",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.BRITISHYARD,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * <p>NZGD49 / South Island Grid</p>
     * <p>alias = GD49 / South Island Grid</p>
     * <p>New Zealand - South Island.</p>
     * <p>Large and medium scale topographic mapping and engineering survey.
     * Sears 1922 British foot-metre conversion factor applied to ellipsoid.  
     * Superseded by 27200 (GD49 / New Zealand Map Grid) in 1972.</p>
     * notice: british yards are used as units!
     */
    private void addEPSG27292() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4272" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "hemisphere", -1 );
        param.setParameter( "semi_major", ellipsoid.getSemiMajorAxis() );
        param.setParameter( "semi_minor", ellipsoid.getSemiMinorAxis() );
        param.setParameter( "false_easting", 500000.0 );
        param.setParameter( "false_northing", 500000.0 );
        param.setParameter( "central_meridian", -171.5 );
        param.setParameter( "latitude_of_origin", -44.0 );
        param.setParameter( "scale_factor", 1.0 );

        Projection projection = csFactory.createProjection( "My projection", "Transverse_Mercator",
                                                            param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27292",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.BRITISHYARD,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * <p>NNZGD49 / New Zealand Map Grid</p>
     * <p>alias = GD49 / NZ Map Grid</p>
     * <p>New Zealand</p>
     * <p>Large and medium scale topographic mapping and engineering survey.
     * Supersedes 27291 (NZGD49 / North Island Grid) and 27292 (NZGD49 / South 
     * Island Grid) from 1972.
     */
    private void addEPSG27200() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4272" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "hemisphere", -1 );
        param.setParameter( "semi_major", ellipsoid.getSemiMajorAxis() );
        param.setParameter( "semi_minor", ellipsoid.getSemiMinorAxis() );
        param.setParameter( "false_easting", 2510000.0 );
        param.setParameter( "false_northing", 6023150.0 );
        param.setParameter( "central_meridian", 173.0 );
        param.setParameter( "latitude_of_origin", -41.0 );
        param.setParameter( "scale_factor", 1.0 );

        Projection projection = csFactory.createProjection( "My projection", "Transverse_Mercator",
                                                            param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:27200",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );

        save( cs );
    }

    /**
     *
     */
    private void addEPSG26716() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4267" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = -87.000000;
        double easting = 500000.000000;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:26716",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    /**
     * <p>RD new / Amersfoort Rijksdriehoekstelsel</p>
     * 
     * <p>The Netherlands</p>
     * <p>Large and medium scale topographic mapping and engineering survey.
     * Supersedes 28991 (Amersfoort / RD Old) 
     * 
     */
    private void addEPSG28992() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4289" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();
        ParameterList param = new ParameterListImpl( pld );
        param.setParameter( "semi_major", ellipsoid.getSemiMajorAxis() );
        param.setParameter( "semi_minor", ellipsoid.getSemiMinorAxis() );
        param.setParameter( "false_easting", 155000.0 );

        param.setParameter( "false_northing", 463000.0 );
        param.setParameter( "central_meridian", 5.38763889 );
        param.setParameter( "latitude_of_origin", 52.15616055 );
        param.setParameter( "scale_factor", 0.99990790 );

        Projection projection = csFactory.createProjection( "My projection", "Stereographic", param );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:28992",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    private void addEPSG26912() {
        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4269" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = -111.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:26912",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

    private void addEPSG2152() {

        GeographicCoordinateSystem geoCS = (GeographicCoordinateSystem) getCSByName( "EPSG:4269" );

        Ellipsoid ellipsoid = geoCS.getHorizontalDatum().getEllipsoid();

        double centerMeridian = -111.0;
        double easting = 500000.0;
        double northing = 0.0;
        double scaleFactor = 0.9996;

        Projection projection = csFactory.createProjection(
                                                            "My projection",
                                                            "Transverse_Mercator",
                                                            ellipsoid,
                                                            new Point2D.Double( centerMeridian, 0 ),
                                                            new Point2D.Double( easting, northing ),
                                                            scaleFactor );

        ProjectedCoordinateSystem cs = csFactory.createProjectedCoordinateSystem( "EPSG:2152",
                                                                                  geoCS,
                                                                                  projection,
                                                                                  Unit.METRE,
                                                                                  AxisInfo.X,
                                                                                  AxisInfo.Y );
        save( cs );
    }

}