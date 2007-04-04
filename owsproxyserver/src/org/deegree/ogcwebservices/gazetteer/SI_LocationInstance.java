// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/gazetteer/SI_LocationInstance.java,v 1.13 2006/05/11 20:25:20 poth Exp $
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
 53115 Bonn
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
package org.deegree.ogcwebservices.gazetteer;

import org.deegree.model.spatialschema.Geometry;

/**
 * @version $Revision: 1.13 $
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.13 $, $Date: 2006/05/11 20:25:20 $
 * 
 * @since 1.1
 */

public class SI_LocationInstance implements Comparable {

    private String identifier;
    private String geoIdentifier;
    private Geometry geomExtent;

    /**
     * 
     * @param identifier unique identifier
     * @param geoIdentifier geographic identifier according to ISO 19112
     * @param geomExtent geographic extent according to ISO 19112
     */
    public SI_LocationInstance( String identifier, String geoIdentifier, Geometry geomExtent ) {
        this.identifier = null;
        this.geoIdentifier = null;
        this.geomExtent = null;
        this.identifier = identifier;
        this.geoIdentifier = geoIdentifier;
        this.geomExtent = geomExtent;
    }

    /**
     * returns the geographic identifier according to ISO 19112
     * @return
     */
    public String getGeoIdentifier() {
        return geoIdentifier;
    }

    /**
     * returns the geographic extent according to ISO 19112
     * @return
     */
    public Geometry getGeomExtent() {
        return geomExtent;
    }

    /**
     * retuns the unique identifer of a location instance
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    public int compareTo( Object arg0 ) {
        if ( arg0 instanceof SI_LocationInstance ) {
            return geoIdentifier.compareTo( ( (SI_LocationInstance) arg0 ).getGeoIdentifier() );
        } 
        return -1; 
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: SI_LocationInstance.java,v $
 * Changes to this class. What the people have been up to: Revision 1.13  2006/05/11 20:25:20  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12  2006/04/06 20:25:31  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/04/04 20:39:44  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/03/30 21:20:28  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2005/11/30 13:59:16  poth
 * Changes to this class. What the people have been up to: no message
 * Changes to this class. What the people have been up to:
 * Revision 1.8 2005/08/30 13:40:03 poth no message
 * 
 * Revision 1.7 2005/06/16 08:27:31 poth no message
 * 
 *  
 **************************************************************************************************/