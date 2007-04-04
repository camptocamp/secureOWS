//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/QuadSplitter.java,v 1.12 2006/11/27 09:07:52 poth Exp $
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

package org.deegree.ogcwebservices.wpvs.util;


/**
 * Spliter that divides an envelope into four.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class QuadSplitter implements Splitter {
//
//	private final Envelope envelope;
//	
//	private final float overlappingPercentage;
//
//    /**
//     * Creates a new QuadSplitter.
//     * @param env the envelope defining some area to be split into four.
//     * @param overlappingPercent the percentage that the four envelopes are allowed to overlap.
//     */
//	public QuadSplitter( Envelope env, float overlappingPercent ) {
//		
//		this.envelope = GeometryUtils.ensureSquareEnvelope( env ); 
//		this.overlappingPercentage = overlappingPercent;
//        
//	}
//
//    /**
//     * Creates four surfaces that equally divide the original envelope into four.
//     *
//     * @return four surfaces covering the area defined by the original envelope
//     */
//	public Surface[] createSurfaces() {
//		ArrayList<Envelope> envs = GeometryUtils.subdivideEnvelope( envelope ); 
//		ArrayList<Surface> surfs = new ArrayList<Surface>( envs.size() );
//		for (Envelope e : envs) {
//			e = e.getBuffer( e.getWidth() * this.overlappingPercentage );
//			try {
//				surfs.add( GeometryFactory.createSurface( e, null ) );
//			} catch (Exception ex) {
//				// not really
//				ex.printStackTrace();
//			}
//		}
//		return surfs.toArray( new Surface[ surfs.size() ] ) ;
//	}
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: QuadSplitter.java,v $
Revision 1.12  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.11  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.10  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.9  2006/05/10 15:03:17  taddei
overlapping percentage is now 0.005

Revision 1.8  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.6  2006/03/17 13:32:45  taddei
added overlap. percentage

Revision 1.5  2006/03/02 15:27:11  taddei
�

Revision 1.4  2006/02/21 14:04:44  taddei
javadoc, added better positioning for background

Revision 1.3  2006/02/21 12:59:53  taddei
added ensureSquare Env method, removed unused output methods

Revision 1.2  2006/02/14 15:18:32  taddei
refactoring

Revision 1.1  2006/02/09 15:47:24  taddei
bug fixes, refactoring and javadoc


********************************************************************** */