//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/SimpleSplitter.java,v 1.4 2006/11/27 09:07:52 poth Exp $
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
 * Simple Splitter class to subdivide a view region into a number of surfaces.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class SimpleSplitter implements Splitter {
//
//	private Surface intersectingSurface;
//	
//	private Envelope envelope;
//
//    private Surface[] stripes;
//
//	private Surface small;
//
//	private Surface big;
//
//	private final float overlappingPercentage;
//	
//	/**
//	 * Creates a new FractalSplitter from an intersecting surface and resolution stripes.
//	 * @param intersectingSurf the surface representing the view region
//	 * @param resStripes surfaces representing resolution stripes. Inside each stripe, resolution
//	 * is suposed to be the same
//	 * @param overlappingPercent How much the surfaces may overlap
//	 */
//	public SimpleSplitter( Surface intersectingSurf, Surface[] resStripes, float overlappingPercent ) {
//		
//		this.intersectingSurface = intersectingSurf;
//        this.envelope = GeometryUtils.ensureSquareEnvelope( intersectingSurf.getEnvelope() );
//        this.overlappingPercentage = overlappingPercent;
//        
//        this.stripes = resStripes;
//        
//	}
//
//	/**
//	 * Subdivides and envelope env, adding it to the surfaces list and removing the
//	 * parent envelope eParent, if available. 
//	 * @param env
//	 * @param eParent
//	 * @param surfaces
//	 */
//	private void subdivide( Envelope env, List<Envelope> surfaces){
//		
//		
//		final List<Envelope> envs = GeometryUtils.subdivideEnvelope( env );
//		for (Envelope e : envs) {
//			
//			Geometry envGeom = GeometryUtils.toSurface( e );
//			
//			if ( shouldSplit( env ) && intersectingSurface.intersects( envGeom )  ){
//	
//				if ( intersectingSurface.contains( envGeom ) ){
//					surfaces.add( e );
//					
//				} else {
//					
//					subdivide( e, surfaces );
//					
//				} 
//			} else if ( intersectingSurface.intersects( envGeom ) ){
//				surfaces.add( e );
//			}
//		}
//	}
//	
//	/**
//	 * Tests whether and envelope e should be split.
//	 * @param e
//	 * @return
//	 */
//	private boolean shouldSplit( Envelope e ){
//		
//		Surface s = findIntersectingStripe( e );
//        // these values are empirical ;-)
//		double validAreaDenominator = small == s ? .03125 : .00125d;
//		double validArea = s.getArea()/validAreaDenominator;
//		double area = e.getHeight() * e.getWidth();
//		
//		return area > validArea;
//	}
//	
//	/**
//	 * Finds the smallest stripe intersecting envelope e
//	 * @param e
//	 * @return
//	 */
//    private Surface findIntersectingStripe( Envelope e ){
//        Surface s = GeometryUtils.toSurface( e );
//        Surface chosenStripe = big;
//        for (Surface stripe : stripes) {
//            if ( //s.intersects( stripe ) 
//                                    s.contains( stripe.getCentroid() )
//                                    && stripe.getArea() < chosenStripe.getArea() ){
//                chosenStripe = stripe;
//            }
//        }
//        return chosenStripe;
//    }  	
//	
//	public Surface[] createSurfaces() {
//		List<Envelope> envelopeList = new ArrayList<Envelope>(32);
//		
//        List<Envelope> majorEnvs = GeometryUtils.subdivideEnvelope( envelope );
//        
//        big = stripes[0];
//        small = stripes[ stripes.length - 1 ];
//        for (Surface s : stripes) {
//            if( s.getArea() > big.getArea() ) {
//                big = s;
//            }
//            if( s.getArea() < small.getArea() ) {
//                small = s;
//            }
//        }
//
//        for (Envelope e : majorEnvs) {
//            boolean canSplit = true;
//            
//            for (Surface s : stripes) {
//                if( s != big && !s.intersects( GeometryUtils.toSurface( e ) ) ) {
//                    canSplit = false;
//                } 
//            }
//            if( canSplit ) {
//                subdivide( e, envelopeList );
//                
//            } else {
//                envelopeList.add( e );
//            }
//        }
//		
//		List<Surface> surfaces = new ArrayList<Surface>( envelopeList.size() );
//		for (Envelope e : envelopeList) {
//			e = e.getBuffer( e.getWidth() * this.overlappingPercentage );
//			surfaces.add( GeometryUtils.toSurface( e ) );
//		}
//		
//		return surfaces.toArray( new Surface[ surfaces.size() ] ); 
//	}
//				
//}
//

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SimpleSplitter.java,v $
Revision 1.4  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.3  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.2  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.1  2006/05/12 13:14:44  taddei
new splitter, removed old, changed overlap

Revision 1.14  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.13  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.12  2006/03/16 11:38:18  taddei
now with overlapping percentage

Revision 1.11  2006/03/10 15:53:44  taddei
removed unused code

Revision 1.10  2006/03/07 15:19:34  taddei
former default splitter is now fractal and vice-versa

Revision 1.6  2006/02/22 13:35:44  taddei
fiddling with shouldDivide()

Revision 1.5  2006/02/21 12:58:53  taddei
buffering envelope to improve image quality

Revision 1.4  2006/02/21 09:27:44  taddei
refactoring; fractal splitter is now the default used

Revision 1.3  2006/02/17 15:40:25  taddei
*** empty log message ***


********************************************************************** */