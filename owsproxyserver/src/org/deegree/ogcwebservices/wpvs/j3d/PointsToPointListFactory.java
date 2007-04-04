//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/PointsToPointListFactory.java,v 1.5 2006/11/27 15:42:03 bezema Exp $
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

import java.util.ArrayList;
import java.util.List;

import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Point;

/**
 * ... 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/11/27 15:42:03 $
 * 
 * @since 2.0
 */

public class PointsToPointListFactory/* implements PointListFactory*/ {


    /**
     * Builds a point list from the <code>Point</code>s in the 
     * FeatureCollection fc. 
     * 
     * @param fc a feature collection containing <code>Point</code>s. This collection cannot be null
     * and must contain a geometry property of <code>Point</code> type. No check for this is done.    
     * @return a List with <code>Point</code>s
     */
	public List<Point> createFromFeatureCollection( FeatureCollection fc  ) {
		if ( fc == null ) {
			 throw new NullPointerException("FeatureColection cannot be null.");
        }

        List<Point> ptsList = new ArrayList<Point>( fc.size() +1 );
        for ( int i = 0; i < fc.size(); i++ ) {
			Point point = (Point)fc.getFeature(i).getDefaultGeometryPropertyValue();
            System.out.println( point );
    	    ptsList.add( point );
        }

        return ptsList;
	}
	
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PointsToPointListFactory.java,v $
Revision 1.5  2006/11/27 15:42:03  bezema
removed the FeatureCollectionAdaptre and the PointListFactory

Revision 1.4  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.3  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.2  2006/04/05 09:04:43  taddei
chnegd header yr: 05 -> 2006

Revision 1.1  2006/04/05 08:59:36  taddei
FC adapter and derived classes (retrofit of older classes)

Revision 1.2  2006/03/07 15:18:16  taddei
javadoc

Revision 1.1  2006/03/07 08:47:59  taddei
changes due to pts list factories


********************************************************************** */