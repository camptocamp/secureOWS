/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
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

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.model.crs;

import java.net.URI;

import org.deegree.i18n.Messages;
import org.deegree.model.csct.cs.ConvenienceCSFactory;

/**
 * 
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/11/27 15:36:08 $
 *
 * @since 2.0
 */
public class CRSFactory {

    /**
     * 
     * @param prefix must be not null
     * @param localName
     * @param namespace
     * @return a CoordinateSystem corresponding to the given name, prefix and namespace
     * @throws UnknownCRSException 
     */
    public static CoordinateSystem create( String prefix, String localName, URI namespace )
                            throws UnknownCRSException {
        if ( ConvenienceCSFactory.getInstance().getCSByName( prefix + ':' + localName ) == null ) {
            throw new UnknownCRSException( Messages.getMessage( "CRS_UNKNOWNCRS" ) );
        }
        return new CoordinateSystem( prefix, localName, namespace );
    }

    /**
     * 
     * @param name
     * @param namespace
     * @return a CoordinateSystem corresponding to the given name and namespace
     * @throws UnknownCRSException 
     */
    public static CoordinateSystem create( String name, URI namespace )
                            throws UnknownCRSException {
        name = normalizeName( name );
        if ( ConvenienceCSFactory.getInstance().getCSByName( name ) == null ) {
            throw new UnknownCRSException( Messages.getMessage( "CRS_UNKNOWNCRS" ) );
        }
        return new CoordinateSystem( name, namespace );
    }

    /**
     * e.g. epgs:4326
     * @param name
     * @return a CoordinateSystem corresponding to the given name 
     * @throws UnknownCRSException if the crs-name is not known
     */
    public static CoordinateSystem create( String name )
                            throws UnknownCRSException {
        name = normalizeName( name );
        if ( ConvenienceCSFactory.getInstance().getCSByName( name ) == null ) {
            throw new UnknownCRSException( Messages.getMessage( "CRS_UNKNOWNCRS", name ) );
        }
        return new CoordinateSystem( name );
    }
    
    private static String normalizeName(String name ) {
        if ( name.startsWith( "http://www.opengis.net/gml/srs/" ) ) {
            // as declared in the GML 2.1.1 specification
            // http://www.opengis.net/gml/srs/epsg.xml#4326
            int p = name.lastIndexOf( "/" );

            if ( p >= 0 ) {
                name = name.substring( p, name.length() );
                p = name.indexOf( "." );

                String s1 = name.substring( 1, p ).toUpperCase();
                p = name.indexOf( "#" );

                String s2 = name.substring( p + 1, name.length() );
                if ( s2.toUpperCase().startsWith( "EPSG" ) ) {
                    name = s2;
                } else {
                    name = s1 + ":" + s2;
                }
            }
        }
        return name;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: CRSFactory.java,v $
 Revision 1.4  2006/11/27 15:36:08  bezema
 fixed javadocs

 Revision 1.3  2006/11/27 09:07:51  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.2  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */
