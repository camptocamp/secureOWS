
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


import java.io.Serializable;

import org.deegree.model.spatialschema.ByteUtils;

/**
 * Class representing a rectangle - envelope. 
 * 
 * <P>
 * <B>Last changes<B>:<BR>
 * 07.01.2000 ap: all methods copied from Rectangle.java<BR>
 * 07.01.2000 ap: constructor renamed<BR>
 * 17.01.2000 ap: constructor SHPEnvelope(ESRIBoundingBox Ebb) removed<BR>
 * 17.01.2000 ap: constructor SHPEnvelope(SHPEnvelope env)implemented<BR>
 * 01.08.2000 ap: method writeSHPEnvelope() added<BR>
 *
 * <!---------------------------------------------------------------------------->
 * @version 01.08.2000
 * @author Andreas Poth
 *
 */



public class SHPEnvelope implements Serializable{
       
    /**
     * this order:
     *
     * west, east, north, south
     */

    // each double 8 byte distance, offset due to position in .shp-file-record
    public static final int recWest= 4;
    public static final int recSouth= 12;
    public static final int recEast= 20;
    public static final int recNorth= 28;
    
  
    //west bounding coordinate
    public double west;
    //east bounding coordinate
    public double east;
    //north bounding coordinate
    public double north;
    //south bounding coordinate
    public double south;


    //------------- CONSTRUTOR IMPLEMENTATION BEGIN
    public SHPEnvelope() {

        west = 0.0;
        east = 0.0;
        north = 0.0;
        south = 0.0; 

    }


    public SHPEnvelope(double westbc, double eastbc, double northbc, double southbc) {

        this.west= westbc; // west bounding coordinate
        this.east= eastbc; // east bounding coordinate
        this.north= northbc; // north bounding coordinate
        this.south= southbc; // south bounding coordinate

    }

   /**
    * Transform from WKBPoint to Rectangle
    */
    public SHPEnvelope(SHPPoint min, SHPPoint max) {

        //west bounding coordinate = minEsri.x
        this.west= min.x;
        //east bounding coordinate = maxEsri.x
        this.east= max.x;
        //north bounding coordinate = maxEsri.y
        this.north= max.y;
        //south bounding coordinate = minEsri.y
        this.south= min.y;

    }

   /**
    * create from an existing SHPEnvelope 
    */
    public SHPEnvelope(SHPEnvelope env) {

        //west bounding coordinate = Ebb.min.x
        this.west= env.west;
        //east bounding coordinate = Ebb.max.x
        this.east= env.east;
        //north bounding coordinate = Ebb.max.y
        this.north= env.north;
        //south bounding coordinate = Ebb.min.y
        this.south= env.south;

    }


    public SHPEnvelope(byte[] recBuf) {

        //west bounding coordinate = xmin of rec-Box
        this.west= ByteUtils.readLEDouble(recBuf, recWest);
        //east bounding coordinate = xmax of rec-Box
        this.east= ByteUtils.readLEDouble(recBuf, recEast);
        //north bounding coordinate = ymax of rec-Box
        this.north= ByteUtils.readLEDouble(recBuf, recNorth);
        //south bounding coordinate = ymin of rec-Box
        this.south= ByteUtils.readLEDouble(recBuf, recSouth);

    }

    public byte[] writeLESHPEnvelope() {
        byte[] recBuf = new byte[8*4];
        //west bounding coordinate = xmin of rec-Box
        ByteUtils.writeLEDouble(recBuf, 0, west);
        //south bounding coordinate = ymin of rec-Box
        ByteUtils.writeLEDouble(recBuf, 8, south);
        //east bounding coordinate = xmax of rec-Box
        ByteUtils.writeLEDouble(recBuf, 16, east);
        //north bounding coordinate = ymax of rec-Box
        ByteUtils.writeLEDouble(recBuf, 24, north);
        
        return recBuf;
    }

    public byte[] writeBESHPEnvelope() {
        byte[] recBuf = new byte[8*4];
        //west bounding coordinate = xmin of rec-Box
        ByteUtils.writeBEDouble(recBuf, 0, west);
        //south bounding coordinate = ymin of rec-Box
        ByteUtils.writeBEDouble(recBuf, 8, south);
        //east bounding coordinate = xmax of rec-Box
        ByteUtils.writeBEDouble(recBuf, 16, east);
        //north bounding coordinate = ymax of rec-Box
        ByteUtils.writeLEDouble(recBuf, 24, north);
        
        return recBuf;
    }


    //----------------- METHOD IMPLEMENTATION
    public String toString() {

        return "RECTANGLE" +
                "\n[west: " + this.west + "]" + 
                "\n[east: " + this.east + "]" +
                "\n[north: " + this.north + "]" +
                "\n[south: " + this.south + "]" ;

    }



}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SHPEnvelope.java,v $
Revision 1.5  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
