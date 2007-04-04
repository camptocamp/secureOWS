
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
53115 Bonn
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

package org.deegree.io.shpapi;

import org.deegree.model.spatialschema.ByteUtils;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.CurveSegment;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

/**
 * Class representig a two dimensional ESRI Polygon<BR> *  * @version 16.08.2000 * @author Andreas Poth
 */
public class SHPPolygon extends SHPGeometry {
    
    public int numRings = 0;
    public int numPoints = 0;  
    public SHPPolyLine rings = null;

    
    
    /**
     * constructor: recieves a stream <BR>
     */
    public SHPPolygon(byte[] recBuf) {
        
        super(recBuf);
        
        envelope = ShapeUtils.readBox(recBuf,4);
        
        rings = new SHPPolyLine(recBuf);
        
        numPoints = rings.numPoints;
        numRings = rings.numParts;
        
    }
    
    /**
     * constructor: recieves an array of arrays of Points  <BR>
     */
    public SHPPolygon(Surface[] surface) {
        
        try {
            int count = 0;
            
            for (int i = 0; i < surface.length; i++) {
                // increment for exterior ring
                count++;
                // increment for inner rings
                Ring[] rings = surface[i].getSurfaceBoundary().getInteriorRings();
                if ( rings != null ) {
                    count += rings.length;
                }
            }
            
            Curve[] curves = new Curve[count];
            
            count = 0;
            for (int i = 0; i < surface.length; i++) {
                
                CurveSegment cs =
                surface[i].getSurfaceBoundary().getExteriorRing().getAsCurveSegment();
                curves[count++] = GeometryFactory.createCurve( cs );
                
                Ring[] rings = surface[i].getSurfaceBoundary().getInteriorRings();
                if ( rings != null ) {
                    for (int j = 0; j < rings.length; j++) {
                        cs =rings[j].getAsCurveSegment();
                        curves[count++] = GeometryFactory.createCurve( cs );
                    }
                }
            }
            
            rings = new SHPPolyLine(curves);
            
            envelope = rings.envelope;
            
            numPoints = rings.numPoints;
            numRings = rings.numParts;
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * method: writeSHPPolygon(byte[] bytearray, int start)<BR>
     */
    public byte[] writeSHPPolygon(byte[] bytearray, int start) {
        
        int offset = start;
        
        double xmin = rings.points[0][0].x;
        double xmax = rings.points[0][0].x;
        double ymin = rings.points[0][0].y;
        double ymax = rings.points[0][0].y;
        
        // write shape type identifier
        ByteUtils.writeLEInt(bytearray, offset, ShapeConst.SHAPE_TYPE_POLYGON);
        
        offset += 4;
        // save offset of the bounding box
        int tmp1 = offset;
        
        // increment offset with size of the bounding box
        offset += (4*8);
        
        // write numRings
        ByteUtils.writeLEInt(bytearray, offset, numRings);
        offset += 4;
        // write numpoints
        ByteUtils.writeLEInt(bytearray, offset, numPoints);
        offset += 4;
        
        // save offset of the list of offsets for each polyline
        int tmp2 = offset;
        
        // increment offset with numRings
        offset +=  (4*numRings);
        
        int count = 0;
        for (int i = 0; i < rings.points.length; i++) {
            
            // stores the index of the i'th part
            ByteUtils.writeLEInt(bytearray, tmp2 , count);
            tmp2 += 4;
            
            // write the points of the i'th part and calculate bounding box
            for (int j = 0; j < rings.points[i].length; j++) {
                // number of the current point
                count++;
                
                // calculate bounding box
                if (rings.points[i][j].x > xmax) {
                    xmax = rings.points[i][j].x;
                } else if (rings.points[i][j].x < xmin) {
                    xmin = rings.points[i][j].x;
                }
                
                if (rings.points[i][j].y > ymax) {
                    ymax = rings.points[i][j].y;
                } else if (rings.points[i][j].y < ymin) {
                    ymin = rings.points[i][j].y;
                }
                
                // write x-coordinate
                ByteUtils.writeLEDouble(bytearray, offset, rings.points[i][j].x);
                offset += 8;
                
                // write y-coordinate
                ByteUtils.writeLEDouble(bytearray, offset, rings.points[i][j].y);
                offset += 8;
                
            }
            
        }
        
        // jump back to the offset of the bounding box
        offset = tmp1;
        
        // write bounding box to the byte array
        ByteUtils.writeLEDouble(bytearray, offset, xmin);
        offset += 8;
        ByteUtils.writeLEDouble(bytearray, offset, ymin);
        offset += 8;
        ByteUtils.writeLEDouble(bytearray, offset, xmax);
        offset += 8;
        ByteUtils.writeLEDouble(bytearray, offset, ymax);
        
        return bytearray;
    }
    
    /**
     * returns the polygon shape size in bytes<BR>
     */
    public int size() {
        return 44 + numRings * 4 + numPoints * 16;
    }
    
    
    
    public String toString() {
        
        return "WKBPOLYGON" + " numRings: " + numRings;
        
    }
}
/*
 * Last changes:
 * $Log: SHPPolygon.java,v $
 * Revision 1.11  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.10  2006/07/12 14:46:14  poth
 * comment footer added
 *
 * Revision 1.9  2006/04/06 20:25:23  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/04/04 20:39:41  poth
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/30 21:20:24  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/01/16 20:36:39  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/01/11 14:52:32  poth
 * *** empty log message ***
 *
 * Revision 1.4  2005/12/06 13:45:20  poth
 * System.out.println substituted by logging api
 *
 * Revision 1.3  2005/02/13 21:34:58  friebe
 * fix javadoc errors
 *
 * 12.01.2000 ap: constructor re-declared<BR>
 * 25.01.2000 ap: constructor modified;
 * 25.01.2000 ap: public variables numRings and numPoints declared<BR>
 * 21.03.2000 ap: parameter list of the second constructor modified<BR>
 * 14.08.2000 ap: constructor SHPPolygon (Point[][] gm_points) added<BR>
 * 14.08.2000 ap: method writeSHPPolygon(..) added<BR>
 * 14.08.2000 ap: import clauses added<BR>
 * 14.08.2000 ap: method size() added<BR>
 * 16.08.2000 ap: constructor SHPPolygon (byte[] recBuf) modified<BR>
 */
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SHPPolygon.java,v $
Revision 1.11  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.10  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
