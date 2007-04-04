//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/ThreeDObjectFactory.java,v 1.8 2006/11/27 09:07:52 poth Exp $
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


/**
 * An interface to define the single operation a factory must implement, if it is to
 * create three-dimensional objects to place into the scene. 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public interface ThreeDObjectFactory extends FeatureCollectionAdapter {
//
//    /**
//     * Interface method describig the signature for al factory methods used to adapt FeatureCollections
//     * into Java3d Group objects.
//     * 
//     * TODO Perhaps change return type to Group.
//     * @param fc the FeatureCollection to be adapted into a Java3D Group object.
//     * @return a new Group containing a Java3D representation of the input FeatureCollection.
//     */
//	List<Group> create3DObjectsFromFeatureCollection( FeatureCollection fc );
//	
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ThreeDObjectFactory.java,v $
Revision 1.8  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.7  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.6  2006/08/24 06:42:16  poth
File header corrected

Revision 1.5  2006/07/04 09:08:06  taddei
buildings factory props, and possibility of setting builds to z = 0

Revision 1.4  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.3  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.2  2006/04/05 09:04:43  taddei
chnegd header yr: 05 -> 2006

Revision 1.1  2006/04/05 08:59:36  taddei
FC adapter and derived classes (retrofit of older classes)


********************************************************************** */