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

package org.deegree.io.shpapi;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.deegree.model.spatialschema.ByteUtils;


/**
 * Class representing an ESRI Shape File. 
 * <p>
 * Uses class ByteUtils modified 
 * from the original package com.bbn.openmap.layer.shape
 * <br>
 * Copyright (C) 1998 BBN Corporation 10 Moulton St. Cambridge, MA 02138
 * <br>
 * 
 * @version 16.08.2000
 * @author Andreas Poth
 *
 */
public class MainFile {

   /* 
    * A buffer for current record's header. 
    */
    protected byte[] recHdr = new byte[ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH];
    
   /*
    * instance variables
    */
    private FileHeader fh;
    private IndexFile shx;
    
   /* 
    * file suffixes for shp
    */
    private static final String _shp = ".shp";    

   /*
    * references to the main file 
    */ 
    private RandomAccessFile raf;    
    

   /**
    * Construct a MainFile from a file name.
    */
    public MainFile(String url) throws IOException {
       
       /* 
        * creates raf 
        */
        raf= new RandomAccessFile(url + _shp,"r"); 

        fh = new FileHeader(raf);        

        shx = new IndexFile(url);        

    }

   /**
    * Construct a MainFile from a file name.
    */
    public MainFile(String url, String rwflag) throws IOException {
       
        // delet file if it exists
        File file = new File(url + _shp);
        
        if (rwflag.indexOf('w') > -1 && file.exists()) file.delete();
        file = null;

       /* 
        * creates raf 
        */
        raf= new RandomAccessFile(url + _shp,rwflag); 

        fh = new FileHeader(raf, rwflag.indexOf('w') > -1);

        shx = new IndexFile(url,rwflag);   

    }
    
    public void close()
    {
    	try {
    		raf.close();    		
	    } catch (Exception ex) { 
            ex.printStackTrace();
        }
        try {
            shx.close();            
        } catch (Exception ex) { 
            ex.printStackTrace();
        }	   
    }


   
   /**
    * method: getFileMBR()<BR>
    * returns the minimum bounding rectangle of geometries<BR>
    * within the shape-file
    */
    public SHPEnvelope getFileMBR() {

        return fh.getFileMBR();

    }

   /**
    * method: getRecordNum()<BR>
    * returns the number of record with in a shape-file<BR>
    */
    public int getRecordNum() {  

        return shx.getRecordNum();

    }

   /**
    * method: getRecordMBR(int RecNo)<BR>
    * returns the minimum bound rectangle of RecNo's Geometrie 
    * of the shape-file<BR>
    */
    public SHPEnvelope getRecordMBR(int RecNo) throws IOException{

        SHPEnvelope recordMBR = null;
        byte[] recBuf= null; 

        // index in IndexArray (see IndexFile) 
        int iaIndex = RecNo-1; 

        int off = shx.getRecordOffset(iaIndex);

        // calculate length from 16-bit words (= 2 bytes) to lenght in bytes
        int len = shx.getRecordLength(iaIndex) * 2;

        // off holds the offset of the shape-record in 16-bit words (= 2 byte)
        // multiply with 2 gets number of bytes to seek
        long rafPos = off*2;

        // fetch shape record 
        raf.seek(rafPos + ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH);

        recBuf = null;
        recBuf = new byte[len];

        if ( raf.read(recBuf, 0, len) != -1 ) {

            int shpType = ByteUtils.readLEInt(recBuf, 0);

           /*
            * only for PolyLines, Polygons and MultiPoints 
            * minimum bounding rectangles are defined
            */
            if ( (shpType == ShapeConst.SHAPE_TYPE_POLYLINE) || 
                 (shpType == ShapeConst.SHAPE_TYPE_POLYGON) ||
                 (shpType == ShapeConst.SHAPE_TYPE_MULTIPOINT) ) {

                recordMBR = new SHPEnvelope(recBuf);

            } // end if shpType

        } // end if result

        return recordMBR;
    } 


   /**
    * method: getByRecNo (int RecNo)<BR>
    * retruns a ShapeRecord-Geometry by RecorcNumber<BR>
    */
    public SHPGeometry getByRecNo (int RecNo) throws IOException {

        SHPGeometry  shpGeom = null;
        byte[] recBuf= null;

        // index in IndexArray (see IndexFile) 
        int iaIndex = RecNo-1; 

        int off = shx.getRecordOffset(iaIndex);

        // calculate length from 16-bit words (= 2 bytes) to lenght in bytes
        int len = shx.getRecordLength(iaIndex) * 2;        

        // off holds the offset of the shape-record in 16-bit words (= 2 byte)
        // multiply with 2 gets number of bytes to seek
        long rafPos= off*2;

        //fetch record header
        raf.seek(rafPos);

        recBuf = null;
        recBuf = new byte[ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH];        

        // fetch shape record 
        raf.seek(rafPos + ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH);

        recBuf = null;
        recBuf = new byte[len];

        if ( raf.read(recBuf, 0, len) != -1 ) {

            int shpType = ByteUtils.readLEInt(recBuf, 0);            

            // create a geometry out of record buffer with shapetype
            if (shpType == ShapeConst.SHAPE_TYPE_POINT) {
                shpGeom = new SHPPoint(recBuf,4);
            } else if (shpType == ShapeConst.SHAPE_TYPE_MULTIPOINT) {
                shpGeom = new SHPMultiPoint(recBuf);
            } else if (shpType == ShapeConst.SHAPE_TYPE_POLYLINE) {
                shpGeom = new SHPPolyLine(recBuf);
            } else if (shpType == ShapeConst.SHAPE_TYPE_POLYGON) {
                shpGeom = new SHPPolygon(recBuf);
            } else if( shpType == ShapeConst.SHAPE_TYPE_POLYGONZ ){
                shpGeom = new SHPPolygon3D(recBuf);
            }   
         
        } // end if result
   
        return shpGeom;

    }

   /**
    * method: getShapeType(int RecNo)<BR>
    * returns the minimum bound rectangle of RecNo's Geometrie 
    * of the shape-file<BR>
    */
    public int getShapeTypeByRecNo(int RecNo) throws IOException {

        byte[] recBuf = null;
        int shpType = -1;

        // index in IndexArray (see IndexFile) 
        int iaIndex = RecNo-1; 

        int off = shx.getRecordOffset(iaIndex);

        // calculate length from 16-bit words (= 2 bytes) to lenght in bytes
        int len = shx.getRecordLength(iaIndex) * 2;

        // off holds the offset of the shape-record in 16-bit words (= 2 byte)
        // multiply with 2 gets number of bytes to seek
        long rafPos= off*2;

        // fetch shape record 
        raf.seek(rafPos + ShapeConst.SHAPE_FILE_RECORD_HEADER_LENGTH);

        recBuf = null;
        recBuf = new byte[len];

        if ( raf.read(recBuf, 0, len) != -1 ) {

            shpType = ByteUtils.readLEInt(recBuf, 0);

        } // end if result

        return shpType;
    } 

   /**
    * method: public void write(byte[] bytearray)<BR>
    * appends a bytearray to the shape file<BR>
    */
    public void write(byte[] bytearray, IndexRecord record, SHPEnvelope mbr) throws IOException {
        raf.seek(record.offset*2);
        raf.write(bytearray);
        shx.appendRecord(record,mbr);
    }

   /**
    * method: public void writeHeader(int filelength, byte shptype, SHPEnvelope mbr)<BR>
    * writes a header to the shape and index file<BR>
    */
    public void writeHeader(int filelength, byte shptype, SHPEnvelope mbr) throws IOException {
        fh.writeHeader(filelength, shptype,mbr);
        shx.writeHeader(shptype,mbr);
    }
   
}
/*
 * Last changes:
 * $Log: MainFile.java,v $
 * Revision 1.12  2006/07/12 14:46:14  poth
 * comment footer added
 *
 * Revision 1.11  2006/06/05 15:21:53  poth
 * support for polygonz type added
 *
 * Revision 1.10  2006/05/29 16:16:15  poth
 * code simplification
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
 * Revision 1.5  2005/02/14 11:19:43  friebe
 * fix javadoc errors
 *
 * 14.12.1999 ap: import clauses added<BR>
 * 14.12.1999 ap: private variable declarations<BR>
 * 14.12.1999 ap: all public static references removed<BR>
 * 14.12.1999 ap: constructor implemented<BR>
 * 14.12.1999 ap: method: openFiles (String url) implemented<BR>
 * 14.12.1999 ap: method: getFileMBR() implemented<BR>
 * 14.12.1999 ap: method: getRecordNum() implemented<BR>
 * 14.12.1999 ap: method: getRecordMBR(int RecNo) implemented<BR>
 * 14.12.1999 ap: method: getByRecNo(int RecNo) implemented<BR>
 * 14.12.1999 ap: method: getShapeTypeByRecNo(int RecNo) implemented<BR>
 * 21.12.1999 ap: method: openfiles(String url) removed<BR>
 * 21.12.1999 ap: all static final declarations replaced<BR>
 * 07.01.2000 ap: return types of the methods changed from WKBxxxx and Rectangle<BR>
 *                to SHPxxxx<BR>
 * 07.01.2000 ap: method getRecordMBR modified - SHAPE_TYPE_MULTIPOINT added<BR>
 * 13.01.2000 ap: method getByRecNo re-implemented<BR>
 * 21.03.2000 ap: method getByRecNo completed; multipoint is now supported<BR> 
 * 16.08.2000 ap: method write(..) added<BR>
 * 16.08.2000 ap: method writeHeader(..) added<BR>
*//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MainFile.java,v $
Revision 1.12  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
