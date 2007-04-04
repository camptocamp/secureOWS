/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
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
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@giub.uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.model.crs;

// OpenGIS dependencies
import java.net.URI;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.StringTools;
import org.deegree.model.csct.cs.ConvenienceCSFactory;
import org.deegree.model.csct.cs.GeocentricCoordinateSystem;
import org.deegree.model.csct.cs.GeographicCoordinateSystem;

/**
 *  A coordinate system is a mathematical space, where the elements of
 * the space are called positions.  Each position is described by a list
 * of numbers.  The length of the list corresponds to the dimension of
 * the coordinate system.  So in a 2D coordinate system each position is
 * described by a list containing 2 numbers.
 * <br><br>
 * However, in a coordinate system, not all lists of numbers correspond
 * to a position - some lists may be outside the domain of the coordinate
 * system.  For example, in a 2D Lat/Lon coordinate system, the list (91,91)
 * does not correspond to a position.
 * <br><br>
 * Some coordinate systems also have a mapping from the mathematical space
 * into locations in the real world.  So in a Lat/Lon coordinate system, the
 * mathematical position (lat, long) corresponds to a location on the surface
 * of the Earth.  This mapping from the mathematical space into real-world
 * locations is called a Datum.
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/11/29 09:39:04 $
 *
 * @since 2.0
 */
public class CoordinateSystem extends QualifiedName {

    private String name = null;

    private String identifier = null;

    /**
     * 
     * @param prefix must be not null
     * @param localName
     * @param namespace
     */
    CoordinateSystem( String prefix, String localName, URI namespace ) {
        super( prefix.toLowerCase(), localName, namespace );
    }

    /**
     * 
     * @param name
     * @param namespace
     */
    CoordinateSystem( String name, URI namespace ) {
        super( name, namespace );
    }

    /**
     * e.g. epgs:4326
     * @param name
     */
    protected CoordinateSystem( String name ) {
        super( name );
    }

    /**
     * returns the name of the CRS with - if available - prefix
     * seperated with ':' from the CRS code
     * @return
     */
    public String getName() {
        if ( name == null ) {
            String prefix = getPrefix();
            if ( prefix == null ) {
                prefix = "";
            }
            name = StringTools.concat( 50, prefix, ':', getLocalName() );
        }
        return name;
    }

    /**
     * in opposit to @see #getName() this method always returns the
     * prefix of a CRS name in lower case characters. This method will
     * be used to set CRS for transformation using proj4
     * @return
     */
    public String getIdentifier() {
        if ( identifier == null ) {
            String prefix = getPrefix();
            if ( prefix == null ) {
                prefix = "";
            }
            identifier = StringTools.concat( 50, prefix.toLowerCase(), ':', getLocalName() );
        }
        return identifier;
    }

    /**
     * returns the CRSs code. In case of EPSG:4326 it will be 4326; in case of 
     * adv:DE_DHDN_3GK2_NW177 it will be DE_DHDN_3GK2_NW177 
     * @return
     */
    public String getCode() {
        return getLocalName();
    }

    /**
     * returns the CRS name as URN. e.g. urn:epsg:crs:4326 or urn:adv:crs:DE_DHDN_3GK2_NW177
     * @return
     */
    public String getAsURN() {
        String prefix = getPrefix();
        if ( prefix == null ) {
            prefix = "";
        }
        return StringTools.concat( 100, "urn", prefix.toLowerCase(), "crs", getLocalName() );
    }

    /**
     * returns the units use by the CRS
     * @return
     */    
    public String getUnits() {
        // quuick and dirty hack
        // TODO
        org.deegree.model.csct.cs.CoordinateSystem cs = 
            ConvenienceCSFactory.getInstance().getCSByName( getName() );
        String units = "m";
        if ( cs instanceof GeographicCoordinateSystem || cs instanceof GeocentricCoordinateSystem ) {
            units = "°";
        }
        
        return units;
    }

    public int getDimension() {
        // TODO
        // determine crs dimension
        return 2;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: CoordinateSystem.java,v $
 Revision 1.9  2006/11/29 09:39:04  poth
 *** empty log message ***

 Revision 1.8  2006/11/27 09:07:51  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.7  2006/11/02 10:30:45  mschneider
 Removed #equals().

 Revision 1.6  2006/11/02 10:17:48  mschneider
 Fixed indentation. Implemented #equals(Object).

 Revision 1.5  2006/09/26 14:22:50  poth
 set constructor(String) to protected

 Revision 1.4  2006/05/16 06:47:15  poth
 getAsProjection method adapted to changed Projection implementation


 ********************************************************************** */