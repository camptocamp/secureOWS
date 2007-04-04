//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/SunLight.java,v 1.8 2006/07/05 15:59:13 poth Exp $
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
 Aennchenstraße 19
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
package org.deegree.ogcwebservices.wpvs.j3d;

import javax.vecmath.Vector3f;

/**
 * class for calculating sun light according to a specific tima, day of
 * the year (northern hemisper)
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/07/05 15:59:13 $
 *
 * @since 2.0
 */
public class SunLight {
    
    public static final float BASE_LIGHT_INTENSITY = 0.95f;    
    
    public synchronized static Vector3f calculateSunlight(double latitude, int year, 
                                                          int month, int date, 
                                                          int hour,  int minute,
                                                          float cloudFactor) {
                                                              
        double vDir = SunPosition.calcVerticalSunposition( latitude, year, month, 
                                                           date, hour, minute );
        float c = 7.25f*((float)Math.sin( vDir ));

        float r = (float)(BASE_LIGHT_INTENSITY + ((c)/16.0) + 0.05)*0.6f;
        float g = (float)(BASE_LIGHT_INTENSITY + ((c)/18.5) + 0.05)*0.6f;
        float b = (float)(BASE_LIGHT_INTENSITY  +((c)/17.0) + 0.05)*0.55f;
        if ( r > 1 ) r = 1;
        if ( g > 1 ) g = 1;
        if ( b > 1 ) b = 1;
        
        return new Vector3f( r, g, b );

    }
    
    public synchronized static float calcSunlightIntensity(double latitude, int year, 
                                                          int month, int date, 
                                                          int hour,  int minute,
                                                          float cloudFactor) {
        Vector3f vec = calculateSunlight( latitude, year, month, date, hour, minute,
                                          cloudFactor );
        return (vec.x + vec.y + vec.z)/3.0f;
    }
        
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SunLight.java,v $
Revision 1.8  2006/07/05 15:59:13  poth
comments corrected

Revision 1.7  2006/06/29 20:25:24  poth
corrections on sunlight calculation (-> darker)

Revision 1.6  2006/05/12 13:14:08  taddei
let there be more light

Revision 1.5  2006/05/10 15:02:18  taddei
increased light intensity

Revision 1.4  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.2  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.1  2005/12/21 13:50:03  taddei
first check in of old but good WTS classes


********************************************************************** */