
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
import org.deegree.model.spatialschema.MultiPoint;

/**
 * Class representig a collection of points<BR>
 *  * @version 16.08.2000 * @author Andreas Poth
 */


public class SHPMultiPoint extends SHPGeometry {

    /**
     * 
     * @uml.property name="points"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    public SHPPoint[] points = null;

    public int numPoints 		= 0;
    
    public SHPMultiPoint() {}
    
    /**
     * constructor: recieves a stream <BR>
     */
    public SHPMultiPoint(byte[] recBuf) {
        
        super(recBuf);
        
        envelope = ShapeUtils.readBox(recBuf,4);
        
        numPoints = ByteUtils.readLEInt(recBuffer, 36);
        
        points = new SHPPoint[numPoints];
        
        for (int i = 0; i < numPoints; i++) {
            points[i] = new SHPPoint(recBuffer,40 + i * 16);
        }
        
    }
    
    /**
     * constructor: recieves an array of gm_points
     */
    public SHPMultiPoint(MultiPoint multipoint) {
        
        double xmin = multipoint.getEnvelope().getMin().getX();
        double xmax = multipoint.getEnvelope().getMax().getX();
        double ymin = multipoint.getEnvelope().getMin().getY();
        double ymax = multipoint.getEnvelope().getMax().getY();
        
        try {
            points = new SHPPoint[multipoint.getSize()];
            for (int i = 0; i < multipoint.getSize(); i++) {
                points[i] = new SHPPoint( multipoint.getPointAt(i).getPosition() );
                if (points[i].x > xmax) {
                    xmax = points[i].x;
                } else if (points[i].x < xmin) {
                    xmin = points[i].x;
                }
                if (points[i].y > ymax) {
                    ymax = points[i].y;
                } else if (points[i].y < ymin) {
                    ymin = points[i].y;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        envelope = new SHPEnvelope( xmin, xmax, ymax, ymin);
        
    }
    
    /**
     * method: writeSHPmultipoint (byte [] bytearray, int start)<BR>
     *         loops through the point array and writes each point to the bytearray<BR>
     */
    public byte[] writeSHPMultiPoint(byte [] bytearray, int start) {
        
        int offset = start;
        
        double xmin = points[0].x;
        double xmax = points[0].x;
        double ymin = points[0].y;
        double ymax = points[0].y;
        
        // write shape type identifier ( 8 = multipoint )
        ByteUtils.writeLEInt(bytearray, offset, 8);
        
        offset += 4;
        // save offset of the bounding box
        int tmp = offset;
        
        // increment offset with size of the bounding box
        offset += (4*8);
        
        // write number of points
        ByteUtils.writeLEInt(bytearray, offset, points.length);
        
        offset += 4;
        
        for (int i = 0; i < points.length; i++) {
            
            // calculate bounding box
            if (points[i].x > xmax) {
                xmax = points[i].x;
            } else if (points[i].x < xmin) {
                xmin = points[i].x;
            }
            
            if (points[i].y > ymax) {
                ymax = points[i].y;
            } else if (points[i].y < ymin) {
                ymin = points[i].y;
            }
            
            // write x-coordinate
            ByteUtils.writeLEDouble(bytearray, offset, points[i].x);
            
            offset += 8;
            
            // write y-coordinate
            ByteUtils.writeLEDouble(bytearray, offset, points[i].y);
            
            offset += 8;
            
        }
        
        // jump back to the offset of the bounding box
        offset = tmp;
        
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
     * returns the size of the multipoint shape in bytes<BR>
     */
    public int size() {
        return 40 + points.length * 16;
    }
    
}
/*
 * Last changes:
 * $Log: SHPMultiPoint.java,v $
 * Revision 1.8  2006/07/12 14:46:14  poth
 * comment footer added
 *
 * Revision 1.7  2006/04/06 20:25:23  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/04/04 20:39:41  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/30 21:20:24  poth
 * *** empty log message ***
 *
 * Revision 1.4  2005/12/06 13:45:20  poth
 * System.out.println substituted by logging api
 *
 * Revision 1.3  2005/02/13 21:34:58  friebe
 * fix javadoc errors
 *
 * 21.03.2000 ap: constructor declared and implemented<BR>
 * 14.08.2000 ap: constructor SHPMultiPoint(Point[] gm_points) added<BR>
 * 14.08.2000 ap: method writeSHPMultiPoint(..) added<BR>
 * 14.08.2000 ap: import clause added<BR>
 * 16.08.2000 ap: constructor SHPMultiPoint(Point[] gm_points) modified<BR>
 *//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SHPMultiPoint.java,v $
Revision 1.8  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
