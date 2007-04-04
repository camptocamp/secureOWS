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
package org.deegree.demo;

import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * sample application for using deegree coordinate systems and their 
 * transformation
 * 
 *
 * @version $Revision: 1.14 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.14 $, $Date: 2006/11/29 09:38:50 $
 *
 * @since 2.0
 */
public class DemoCRSTransform {    
    
    public void run(String[] args) throws Exception {
        if ( args == null || args.length == 0 ) {
            args = new String[] { "epsg:4326", "EPSG:31467", "8", "52" };
        }
        String sourceCRS = args[0];
        String targetCRS = args[1];
        double x = Double.parseDouble( args[2] );
        double y = Double.parseDouble( args[3] );
        double z = 0;
                        
        // source CRS
        CoordinateSystem sourceCrs = CRSFactory.create( sourceCRS );        
        
        // target CRS
        CoordinateSystem targetCrs = CRSFactory.create( targetCRS );
        System.out.println( targetCrs.getUnits() );
                
        // point to transform
        Point point = GeometryFactory.createPoint( x, y, z, sourceCrs );        
        System.out.println( point );
        
        
        // perform transformation to target CRS

        IGeoTransformer gt = new GeoTransformer( targetCrs );
        Point pp = (Point)gt.transform( point );
        System.out.println( pp );
        
        // transform back to source CRS
        gt = new GeoTransformer( sourceCrs );
        point = (Point)gt.transform( pp );
        System.out.println( point );
        
    }
    
    public static void main(String[] args) throws Exception {
        
        DemoCRSTransform trans = new DemoCRSTransform();
        trans.run( args );
        
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DemoCRSTransform.java,v $
Revision 1.14  2006/11/29 09:38:50  poth
*** empty log message ***

Revision 1.13  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.12  2006/11/23 09:09:11  bezema
Removed the reflection statements

Revision 1.10  2006/10/07 15:03:44  poth
*** empty log message ***

Revision 1.9  2006/08/24 12:04:37  poth
bug fix - reading command line parameters

Revision 1.8  2006/08/02 18:50:40  poth
*** empty log message ***

Revision 1.7  2006/05/16 07:33:20  poth
commentation completed


********************************************************************** */