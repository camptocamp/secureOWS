//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/PointListFactory.java,v 1.5 2006/11/27 15:42:03 bezema Exp $
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

import java.util.List;

import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Point;

/**
 * This is the common interface used by data sources that can generate elevation data.
 * Datasources should include an implementation of a <code>PointListFactory</code> in order
 * to generate a (partial) list of <code>Point</code>s. 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/11/27 15:42:03 $
 * 
 * @since 2.0
 */
//public interface PointListFactory extends FeatureCollectionAdapter {
//
//	/**
//	 * Creates a <code>List</code> containing <code>Points</code>s from a given
//	 * <code>FeatureCollection</code>
//	 * @param fc the <code>FeatureCollection</code> from which to extract points
//	 * @return a new list with <code>Points</code>s
//	 */
//	List<Point> createFromFeatureCollection( FeatureCollection fc );
//	
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PointListFactory.java,v $
Revision 1.5  2006/11/27 15:42:03  bezema
removed the FeatureCollectionAdaptre and the PointListFactory

Revision 1.4  2006/06/20 10:16:01  taddei
clean up and javadoc

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