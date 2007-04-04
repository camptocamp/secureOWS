//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/VisADWrapper.java,v 1.10 2006/11/23 11:46:40 bezema Exp $
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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.spatialschema.Point;

import visad.Delaunay;
import visad.DelaunayClarkson;
import visad.FlatField;
import visad.FunctionType;
import visad.Irregular2DSet;
import visad.RealTupleType;
import visad.RealType;
import visad.VisADException;

/**
 * A wrapper for VisAD objects. This class takes care of collecting points to build a TIN, of TIN
 * creation itself and its output as a geometry collection.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class VisADWrapper {

    private static final ILogger LOG = LoggerFactory.getLogger( VisADWrapper.class );

    /**
     * A list for hold points representing te DEM/TIN.
     */
    private List<Point> pointsList;

    /**
     * The minimum altitude value used for boundary points of the main envelope. This is calculated
     * for each request and it is a better approach then extrapolating the altitude values.
     */
    private float minimumAltitude = Float.POSITIVE_INFINITY;

    /**
     * Initializes the object by creating a common domain field from the geometrical information
     * (the envelope, the width and the height) supplied. The envelope cannot the null, nor can the
     * dimensions by < 1.
     * 
     * @param ptsList
     *            a list of Points
     */
    public VisADWrapper( List<Point> ptsList ) {
        this.pointsList = ptsList;
    }

    /**
     * Add <code>Point</code>s to the internal list. Lists without any elements (or null lists)
     * are ignored.
     * 
     * @param points
     *            to be added to the list
     */
    public final void addPoints( List<Point> points ) {
        LOG.entering();

        if ( points == null || points.size() == 0 ) {
            return;
        }

        this.pointsList.addAll( points );

        LOG.exiting();
    }

    /**
     * Generates a list of tringles containing the triangles representing the TIN. Triangles are
     * represented float[3][3]
     * 
     * @return a collection of <code>float[3][3]</code>, each of which representing a TIN
     *         triangle
     * 
     */
    public final List<float[][]> getTriangleCollectionAsList() {

        LOG.entering();

        List<float[][]> list = null;
        long time = System.currentTimeMillis();
        FlatField tinField = triangulatePoints(  );
        if( tinField == null )
            return null;
        LOG.logDebug( "Triangulation time: " + ( System.currentTimeMillis() - time ) / 1000d );

        try {
            list = toGeoCollectionList( tinField );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        LOG.exiting();
        return list;
    }

    /**
     * Triangulate <code>GM_Point</code>s contained in <code>gmPointsList</code> using the
     * Clarkson algorithm. This method returns a <code>FlatField</code> containing all points
     * triangulated and with their elevation values.<br/>
     * 
     * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
     * 
     * @param gmPointsList
     *            the list of <code>GM_Point</code>s. Cannot be null and must contain at least 3
     *            <code>GM_Point</code>s.
     * @return a <code>FlatField</code> containg a TIN (with an <code>Irregular2DSet</code> as
     *         its domain set and the elevation values)
     * 
     */
    private FlatField triangulatePoints( ) {

        LOG.entering();

        if ( this.pointsList == null || this.pointsList.size() < 3 ) {
            throw new IllegalArgumentException(
                                                "Points list cannot be null and must contain at least 3 GM_Points." );
        }

        float[][] triPoints = new float[3][this.pointsList.size()];
        int cnt = 0;
        for ( Point p : this.pointsList) {
            triPoints[0][cnt] = (float) p.getX();
            triPoints[1][cnt] = (float) p.getY();
            triPoints[2][cnt++] = (float) p.getZ();
        }
        
        try {
            FunctionType functionType = new FunctionType(
                                                         new RealTupleType( RealType.XAxis, RealType.YAxis ),
                                                         RealType.ZAxis );
            float[][] ptsXY = new float[][] { triPoints[0], triPoints[1] };

            // ptsXY = Delaunay.perturb(ptsXY,0.21f, false);
            LOG.logDebug( "Number of Points = " + triPoints[0].length );

            Delaunay delan = new DelaunayClarkson( ptsXY );
            Irregular2DSet pointsSet = new Irregular2DSet( functionType.getDomain(), ptsXY, null, null, null,
                                            delan );

            FlatField ff = new FlatField( functionType, pointsSet );

            ff.setSamples( new float[][] { triPoints[2] }, true );

            return ff;

        } catch ( VisADException e ) {
            e.printStackTrace();
            return null;
        } catch ( RemoteException re ) {
            re.printStackTrace();
            return null;
        } catch ( IndexOutOfBoundsException ioobe ){
            ioobe.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generated a list of triangles from the FlatField passed in as tinField
     * 
     * @param tinField
     *            the FlatField containing triangles
     * @return a collection of <code>float[3][3]</code>, each of which representing a TIN
     *         triangle
     * @throws Exception
     *             in the unlikely event that a VisAD expcetion is thrown
     */
    private final List<float[][]> toGeoCollectionList( FlatField tinField )
                            throws Exception {
        LOG.entering();

        if ( tinField == null ) {
            throw new RuntimeException( "FlatField cannot be null." );
        }

        List<float[][]> geoCollec = new ArrayList<float[][]>( 5000 );

        Irregular2DSet domainSet = (Irregular2DSet) tinField.getDomainSet();

        float[][] xyPositions = domainSet.getSamples();
        float[][] zValues = tinField.getFloats();
        int[][] indices = domainSet.Delan.Tri;




        // loop over triangles...
        for ( int i = 0; i < indices.length; i++ ) {

            // indices[i].length == coords.length == 3
            // this is float[3][3] -> 3 points per triabngle, each point with 3 coords
            float[][] myCoords = new float[3][3];

            // ...then over points
            for ( int j = 0; j < indices[i].length; j++ ) {

                int index = indices[i][j];
                myCoords[j] = new float[3];
                myCoords[j][0] = xyPositions[0][index];
                myCoords[j][1] = xyPositions[1][index];
                myCoords[j][2] = zValues[0][index];
            }

            geoCollec.add( myCoords );
        }

        tinField = null;

        LOG.exiting();
        return geoCollec;
    }

    /**
     * Clear all points and invalidate list.
     * 
     */
    public void clear() {
        this.pointsList.clear();
        this.pointsList = null;

    }

    /**
     * Get the minimum altitude value. This is used for points on the edge of the WTS view.
     * 
     * @return the minimum altitude value of the points' list of this object
     */
    public final float getMinimumAltitude() {
        // Debug.debugMethodBegin();
        for ( Iterator iter = pointsList.iterator(); iter.hasNext(); ) {
            Point point = (Point) iter.next();
            minimumAltitude = Math.min( minimumAltitude, (float) point.getZ() );
        }
        // Debug.debugMethodEnd();
        return this.minimumAltitude;
    }

    // public void setPointList( List<Point> ptsList ) {
    // this.pointsList = ptsList;
    // }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: VisADWrapper.java,v $
 * Changes to this class. What the people have been up to: Revision 1.10  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision 1.9
 * 2006/07/12 14:46:19 poth comment footer added
 * 
 **************************************************************************************************/
