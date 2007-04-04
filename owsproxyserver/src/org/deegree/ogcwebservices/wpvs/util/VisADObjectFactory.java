//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/VisADObjectFactory.java,v 1.8 2006/11/27 09:07:52 poth Exp $
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
 * A factory for VisAD objects. This class takes care of instantiation of
 * VisAD objects. 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
//public class VisADObjectFactory {
//    
//    private static final ILogger LOG = LoggerFactory.getLogger( VisADObjectFactory.class );
//    
//    public static final FunctionType FUNCTION_TYPE; 
//    
//    static{
//        FunctionType tmpFunctionType = null;
//
//        try {
//            tmpFunctionType = new FunctionType( 
//                    new RealTupleType(RealType.XAxis, RealType.YAxis), RealType.ZAxis);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        FUNCTION_TYPE = tmpFunctionType;
//    }
//    
//
//    
//    /**
//     * Triangulate <code>GM_Point</code>s contained in <code>gmPointsList</code>
//     * using the Clarkson algorithm. This method returns a <code>FlatField</code>
//     * containing all points triangulated and with their elevation values.<br/> 
//     * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
//     * 
//     * @param gmPointsList the list of <code>GM_Point</code>s. Cannot be null 
//     * and must contain at least 3 <code>GM_Point</code>s.
//     * @return a <code>FlatField</code> containg a TIN (with an 
//     * <code>Irregular2DSet</code> as its domain set and the elevation values)
//     * 
//     */
//    public static FlatField triangulatePoints(List gmPointsList){
//        
//        LOG.entering();
//        
//        if( gmPointsList == null || gmPointsList.size() < 3){
//            throw new IllegalArgumentException("Points list cannot be null and must contain at least 3 GM_Points.");
//        }
//        
//        float[][] triPoints = new float[3][ gmPointsList.size() ];
//        int cnt = 0;
//        for (Iterator iter = gmPointsList.iterator(); iter.hasNext();cnt++) {
//            Point p = (Point) iter.next();
//            triPoints[0][cnt] = (float)p.getX();
//            triPoints[1][cnt] = (float)p.getY();
//            triPoints[2][cnt] = (float)p.getZ();
//        }
//
//        Delaunay delan = null;
//        Irregular2DSet pointsSet = null;
//        FlatField ff = null;
//        try {
//            float[][] ptsXY = new float[][]{triPoints[0],triPoints[1]};
//            
////            ptsXY = Delaunay.perturb(ptsXY,0.21f, false);
//            	LOG.logDebug( "Number of Points = " + triPoints[0].length );
//			
//			delan = new DelaunayClarkson( ptsXY );
//            pointsSet = new Irregular2DSet( FUNCTION_TYPE.getDomain(), ptsXY, null,null,null,delan );
//            
//            ff = new FlatField(FUNCTION_TYPE, pointsSet );
//
//            ff.setSamples( new float[][]{triPoints[2]}, true );  
//
//            return ff;
//            
//        } catch (VisADException e) {
//            e.printStackTrace();
//            return null;
//        } catch (RemoteException re) {
//            re.printStackTrace();
//            return null;
//        } finally {
//            gmPointsList = null;
//            LOG.exiting();
//        }
//        
//    }
//
//    
//}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: VisADObjectFactory.java,v $
Revision 1.8  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.7  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.6  2006/08/24 06:42:16  poth
File header corrected

Revision 1.5  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
