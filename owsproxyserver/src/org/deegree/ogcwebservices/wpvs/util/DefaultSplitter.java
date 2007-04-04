//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/DefaultSplitter.java,v 1.15 2006/11/27 09:07:52 poth Exp $
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
 * Splitter class to subdivide a view region into a number of surfaces. Each surface
 * has a resolution (given a fixed image dimension), which is optimal for a given stripe.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.15 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

//public class DefaultSplitter implements Splitter {
//
//	private Surface intersectingSurface;
//	
//	private Envelope envelope;
//
//    private Surface[] stripes;
//
//	private Position viewer;
//
//	private final float overlappingPercentage;
//
//    private Surface bigStripe;
//
//	/**
//	 * Creates a new FractalSplitter from an intersecting surface and resolution stripes.
//	 * @param intersectingSurf the surface representing the view region
//	 * @param resStripes surfaces representing resolution stripes. Inside each stripe, resolution
//	 * is suposed to be the same
//	 * @param viewPoint of the Observer
//	 * @param overlappingPercentage 
//	 */
//	public DefaultSplitter( Surface intersectingSurf, Surface[] resStripes, ViewPoint viewPoint, float overlappingPercentage ) {
//
//		this.intersectingSurface = intersectingSurf;
//		this.overlappingPercentage = overlappingPercentage;
//        this.envelope = GeometryUtils.ensureSquareEnvelope( intersectingSurf.getEnvelope() );
//        
//        this.stripes = resStripes;
//        Point3d observerPosition = viewPoint.getObserverPosition();
//        Position p = 
//        	GeometryFactory.createPosition( observerPosition.x, observerPosition.y );
//        
//        this.viewer = p;
//        
//	}
//
//	/**
//	 * Subdivides and envelope env, adding it to the surfaces list. 
//	 * @param env
//	 * @param surfaces
//	 * @param level the recursion level
//	 */
//	private void subdivide( Envelope env, List<Envelope> surfaces, int level){
//		
//		level++;
//		final List<Envelope> envs = GeometryUtils.subdivideEnvelope( env );
//		for (Envelope e : envs) {
//			
//			Geometry envGeom = GeometryUtils.toSurface( e );
//			
//			if ( shouldSplit( env, level ) && intersectingSurface.intersects( envGeom )  ){
//	
//				if ( intersectingSurface.contains( envGeom ) ){
//					surfaces.add( e );
//					
//				} else {
//					subdivide( e, surfaces, level );
//					
//				} 
//			} else if ( intersectingSurface.intersects( envGeom ) ){
//					surfaces.add( e );
//			}
//		}
//	}
//	
//	/**
//	 * Tests whether and envelope e should be split.
//	 * @param e
//	 * @param level the recursion level. If level > 5, not further split should be done
//	 * @return
//	 */
//	private boolean shouldSplit( Envelope e, int level ){
//		if ( level > 3 ){
//			return false;
//		}
//		List<Surface> list = findIntersectingStripe( e );
//		if ( list.size() == 0 ) {
//			return false;
//		}
//        
//        if ( level > 2 && bigStripe.intersects( GeometryUtils.toSurface( e ) ) ){
//            return false;
//        }
//        
//		if ( list.size() > 2 ) {
//			return true;
//		} 
//		if ( list.size() == 1 ) {
//			if ( list.get( 0 ).contains( GeometryUtils.toSurface( e ) ) ) {
//				return false;
//			}
//		}
//		
//		Surface sur = list.get( 0 );
//		Position[] p = calcNearAndFarPoints( e );
//		double d1 =  distance( p[0], p[1] );
//		Position p1 = null;
//		Position p2 = null;
//		Position p3 = null;
//		try {
//			
//			Position[] positions = sur.getSurfacePatchAt(0).getExteriorRing(); 
//			
//			p1 = positions[0];
//			p2 = positions[1];
//			p3 = positions[2];
//		} catch (GeometryException e1) {
//			e1.printStackTrace();
//		}
//		double d2 = perpendicularDistance( p1, p2, p3 );
//		/*
//		if ( level < -3 ) {
//			double x = e.getMin().getX() + e.getWidth()/2d;
//			double y = e.getMin().getY() + e.getHeight()/2d;		
//			boolean cn = sur.contains( GeometryFactory.createPosition( x, y ) );
//	
//			return d1 > d2 || cn;
//		} */
//		return d1 > d2;
//		
//	}
//	
//	private Surface createStretchedStripe(Surface stripe) {
//		
//		// number of times line should be stretched
//		final double n = 100000;
//		
//		Position[] positions = stripe.getSurfaceBoundary().getExteriorRing().getPositions();
//	
//		Position[] out = new Position[5];
//		int k = 0;
//		for (int i = 0; i < positions.length - 1; i+=2) {
//			out[k++] = positions[i];
//			out[k++] = positions[i+1];
//		}
//		
//		double dx = out[0].getX() - out[1].getX();
//		double dy = out[0].getY() - out[1].getY();
//		dx = n * Math.abs( dx );
//		dy = n * Math.abs( dy );
//		
//		double x1 = out[0].getX() + dx;
//		double y1 = out[0].getY() - dy;
//		double x2 = out[1].getX() - dx;
//		double y2 = out[1].getY() + dy;
//		out[0] = GeometryFactory.createPosition( x1, y1 );
//		out[1] = GeometryFactory.createPosition( x2, y2 );
//		
//		dx = out[2].getX() - out[3].getX();
//		dy = out[2].getY() - out[3].getY();
//		dx = n * Math.abs( dx );
//		dy = n * Math.abs( dy );
//		
//		x1 = out[2].getX() + dx;
//		y1 = out[2].getY() - dy;
//		x2 = out[3].getX() - dx;
//		y2 = out[3].getY() + dy;
//		out[2] = GeometryFactory.createPosition( x1, y1 );
//		out[3] = GeometryFactory.createPosition( x2, y2 );
//		
//		out[4] = out[0];
//		
//		Surface sur = null;
//		try {
//			sur = GeometryFactory.createSurface( out, null, new SurfaceInterpolationImpl(), null );
//		} catch (GeometryException e) {
//			e.printStackTrace();
//		}
//		return sur; 
//	}
//
//	
//	private Position[] calcNearAndFarPoints( Envelope e ){
//		Surface envGeom = GeometryUtils.toSurface( e );
//		
//		Position[] points = envGeom.getSurfaceBoundary().getExteriorRing().getPositions();
//		
//		Position far = points[0];  
//			
//		Position near = points[1]; 
//		
//		for (int i = 0; i < points.length - 1; i++) {
//			
//			if ( distance( points[i] ) > distance( far ) ){
//				far = points[i];
//			}
//			if ( distance( points[i] ) < distance( near ) ){
//				near = points[i];
//			}
//		}
//		return new Position[]{near,far};
//	}
//	
//	private double distance( Position p ){
//		
//		return distance( viewer, p);
//	}
//	
//	private double distance( Position p, Position p2 ){
//		return Math.sqrt( ( Math.pow(p2.getX() - p.getX(), 2 ) + 
//					Math.pow(p2.getX() - p.getY(), 2) ) );
//	}
//	
//	
//	private double perpendicularDistance( Position a, Position b, Position p ){
//		Coordinate aCoord = null;
//		Coordinate bCoord = null;
//		Coordinate cCoord = null;
//		try {
//			com.vividsolutions.jts.geom.Geometry ag =
//				JTSAdapter.export( GeometryFactory.createPoint( a, null) );
//			aCoord = ag.getCoordinate();
//			
//			com.vividsolutions.jts.geom.Geometry bg =
//				JTSAdapter.export( GeometryFactory.createPoint( b, null) );
//			bCoord = bg.getCoordinate();
//			com.vividsolutions.jts.geom.Geometry cg =
//				JTSAdapter.export( GeometryFactory.createPoint( p, null) );
//			cCoord = cg.getCoordinate();
//		} catch (GeometryException e) {
//			e.printStackTrace();
//		}
//
//		 return CGAlgorithms.distancePointLinePerpendicular( aCoord, bCoord, cCoord );
//	}
//	
//	/**
//	 * Finds the smallest stripe intersecting envelope e
//	 * @param e
//	 * @return
//	 */
//    private List<Surface> findIntersectingStripe( Envelope e ){
//        Surface s = GeometryUtils.toSurface( e );
//        List<Surface> list = new ArrayList<Surface>();
//        for (Surface stripe : stripes) {
//        	stripe = createStretchedStripe( stripe );
//            if ( s.intersects( stripe ) ) {
//                list.add( stripe );
//            }
//        }
//        return list;
//    }  	
//	
//	public Surface[] createSurfaces() {
//		
//		
//		bigStripe = stripes[0];
//		Surface small = stripes[ stripes.length - 1 ];
//        for (Surface s : stripes) {
//            if( s.getArea() > bigStripe.getArea() ) {
//                bigStripe = s;
//            }
//            if( s.getArea() < small.getArea() ) {
//                small = s;
//            }
//        }
//		
//		List<Envelope> envelopeList = new ArrayList<Envelope>(32);
//		
//        List<Envelope> majorEnvs = GeometryUtils.subdivideEnvelope( envelope );
//        
//        for (Envelope e : majorEnvs) {
//        	
//			boolean canSplit = true;
//			int level = 0;
//			
//            for (Surface s : stripes) {
//                if( s != bigStripe && !s.intersects( GeometryUtils.toSurface( e ) ) ) {
//                    canSplit = false;
//                } 
//            }
//            if( canSplit ) {
//                subdivide( e, envelopeList, level );
//                
//            } else {
//                envelopeList.add( e );
//            }
//        	
//        	/*int level = 0;
//            subdivide( e, envelopeList, level );*/
//        }
//		
//		List<Surface> surfaces = new ArrayList<Surface>( envelopeList.size() );
//		for (Envelope e : envelopeList) {
//			// here should calculate how much in % of image a 1px is 
//			e = e.getBuffer( e.getWidth() * this.overlappingPercentage );
//			surfaces.add( GeometryUtils.toSurface( e ) );
//		}
//		
//		return surfaces.toArray( new Surface[ surfaces.size() ] ); 
//	}
//				
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultSplitter.java,v $
Revision 1.15  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.14  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.13  2006/08/07 10:03:52  poth
bug fix - calculation of perpendicular distance

Revision 1.12  2006/05/12 13:14:44  taddei
new splitter, removed old, changed overlap

Revision 1.11  2006/04/26 12:13:51  taddei
added if to reduce number of tiles

Revision 1.10  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.9  2006/04/05 09:00:43  taddei
not splitting major enves anymore; there were too many

Revision 1.7  2006/03/16 11:38:18  taddei
now with overlapping percentage

Revision 1.6  2006/03/10 09:00:01  taddei
fixed bug in create stretechedStripe, and some level fine tuning

Revision 1.5  2006/03/07 15:19:34  taddei
former default splitter is now fractal and vice-versa

Revision 1.9  2006/03/02 15:27:11  taddei

Revision 1.8  2006/03/01 13:36:48  taddei
same as before, but now cleaned-up

Revision 1.7  2006/03/01 13:08:08  taddei
commit of splitter as is, clean-up coming (AOK from AP)

Revision 1.6  2006/02/22 13:35:44  taddei
fiddling with shouldDivide()

Revision 1.5  2006/02/21 12:58:53  taddei
buffering envelope to improve image quality

Revision 1.4  2006/02/21 09:27:44  taddei
refactoring; fractal splitter is now the default used

Revision 1.3  2006/02/17 15:40:25  taddei
*** empty log message ***


********************************************************************** */