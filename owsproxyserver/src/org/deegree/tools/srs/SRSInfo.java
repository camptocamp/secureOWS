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
package org.deegree.tools.srs;

import java.util.HashMap;

import org.deegree.model.crs.CRSFactory;

/**
 * utility program around coordinate systems implemented in deegree
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/11/27 09:07:53 $
 *
 * @since 2.0
 */
public class SRSInfo {
    
    /**
     * returns true if the the passed SRS is available in deegree
     * @param srs
     * @return
     */
    public static boolean isAvailable(String srs) {
        
        try {
            CRSFactory.create( srs );
        } catch ( Exception e ) {
            System.out.println( "Coordinates System: " + srs + " is not available in deegree" );
            return false;
        }
        
        return true;
    }

    public static void main( String[] args ) throws Exception{
        HashMap map = new HashMap();
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
        if ( map.get( "-isAvailable" ) != null ) {
            SRSInfo.isAvailable( (String)map.get( "-isAvailable" ) );
        } else {
            System.out.println("-isAvailable must be set; e.g. -isAvailable EPSG:4326");
        }

    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SRSInfo.java,v $
Revision 1.8  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/05/25 20:04:09  poth
adapted to the new deegree CRS implementation

Revision 1.5  2006/05/01 20:15:27  poth
*** empty log message ***

Revision 1.4  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.2  2006/02/06 10:19:36  poth
*** empty log message ***

Revision 1.1  2006/02/06 09:57:46  poth
*** empty log message ***


********************************************************************** */
