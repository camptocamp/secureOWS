
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


import java.io.IOException;
import java.io.RandomAccessFile;

import org.deegree.model.spatialschema.ByteUtils;

/**
 * Class representing an ESRI Index File Header. 
 * <p>
 * Uses class ByteUtils ShapeUtils modified 
 * from the original package com.bbn.openmap.layer.shape
 * <br>
 * Copyright (C) 1998 BBN Corporation 10 Moulton St. Cambridge, MA 02138
 * <br>
 * 
 * @version 16.08.2000
 * @author Andreas Poth
 *
 */


 public class FileHeader {

   /*
    * The buffer that holds the 100 byte header. 
    */
    private byte[] header;

   /*
    * Holds the length of the file, in bytes. 
    */
    private long fileLength;

   /*
    * Holds the version of the file, as an int. 
    */
    private int fileVersion;

   /*
    * Holds the shape type of the file. 
    */
    private int fileShapeType;

   /*
    * Holds the bounds of the file (four pairs of doubles). 
    */
    private SHPEnvelope fileMBR;   

   /*
    * local copy of the index-file randomaccess variable;
    */
    private RandomAccessFile rafShp = null;


   /**
    * Construct a IndexFileHeader from a file name.
    */
    public FileHeader(RandomAccessFile rafShp_) throws IOException {

        rafShp = rafShp_;        

        initHeader(false);        
    }

   /**
    * Construct a IndexFileHeader from a file name.
    */
    public FileHeader(RandomAccessFile rafShp_, boolean newHeader) throws IOException {

        rafShp = rafShp_;        

        initHeader(newHeader);        
    }


   /**
    * method: getFileMBR();<BR>
    * Returns the bounding box of this shape file.  The bounding box<BR>
    * is the smallest rectangle that encloses all the shapes in the<BR>
    * file.<BR>
    */

    public SHPEnvelope getFileMBR() {

        return fileMBR;

    }

   /**
    * method: getFileLength()<BR>
    * returns the length of the shape file in bytes<BR>
    */
    public long getFileLength() {

        return fileLength;

    }

   /**
    * method: getFileVersion()<BR>
    * returns the version of the shape file<BR>
    */
    public int getFileVersion() {

        return fileVersion;

    }

   /**
    * method: getFileShapeType()<BR>
    * returns the code for the shape type of the file<BR>
    */
    public int getFileShapeType() {
        return fileShapeType;
    }

   /**
    * Reads the header of a Shape file.  If the file<BR>
    * is empty, a blank header is written and then read.  If the<BR>
    * file is not empty, the header is read.<BR>
    * After this function runs, the file pointer is set to byte 100,<BR>
    * the first byte of the first record in the file.<BR>
    */
  
    private void initHeader(boolean newHeader) throws IOException {

        if (newHeader) writeHeader();
        /*
        if (rafShp.read() == -1) { 
            //File is empty, write a new one (what else???)
            writeHeader();
        } 
        */ 
        readHeader();
    }

   /**
    * method: writeHeader()<BR>
    * Writes a blank header into the shape file.<BR>
    */
    public void writeHeader() throws IOException {

        header = new byte[ShapeConst.SHAPE_FILE_HEADER_LENGTH];

        ByteUtils.writeBEInt(header, 0, ShapeConst.SHAPE_FILE_CODE);
        ByteUtils.writeBEInt(header, 24, 50);

        // empty shape file size in 16 bit words
        ByteUtils.writeLEInt(header, 28, ShapeConst.SHAPE_FILE_VERSION);
        ByteUtils.writeLEInt(header, 32, ShapeConst.SHAPE_TYPE_NULL);
        ByteUtils.writeLEDouble(header, 36, 0.0);
        ByteUtils.writeLEDouble(header, 44, 0.0);
        ByteUtils.writeLEDouble(header, 52, 0.0);
        ByteUtils.writeLEDouble(header, 60, 0.0);

        rafShp.seek(0);
        rafShp.write(header, 0, ShapeConst.SHAPE_FILE_HEADER_LENGTH);

    }

   /**
    * method: writeHeader(int filelength, byte shptype,SHPEnvelope mbr)<BR>
    * Writes a header into the shape file.<BR>
    */
    public void writeHeader(int filelength, int shptype, SHPEnvelope mbr) throws IOException {

        header = new byte[ShapeConst.SHAPE_FILE_HEADER_LENGTH];

        ByteUtils.writeBEInt(header, 0, ShapeConst.SHAPE_FILE_CODE);
        ByteUtils.writeBEInt(header, 24, filelength / 2);
        ByteUtils.writeLEInt(header, 28, ShapeConst.SHAPE_FILE_VERSION);
        ByteUtils.writeLEInt(header, 32, shptype);
        ShapeUtils.writeBox(header, 36, mbr);

        rafShp.seek(0);
        rafShp.write(header, 0, ShapeConst.SHAPE_FILE_HEADER_LENGTH);
    }

   /**
    * Reads and parses the header of the file. Values from the header<BR>
    * are stored in the fields of this class.<BR>
    */   
    private void readHeader() throws IOException {

        header = new byte[ShapeConst.SHAPE_FILE_HEADER_LENGTH];

       /*
        * Make sure we're at the beginning of the file
        */
        rafShp.seek(0); 

        rafShp.read(header, 0, ShapeConst.SHAPE_FILE_HEADER_LENGTH);

        int fileCode = ByteUtils.readBEInt(header, 0);

        if (fileCode != ShapeConst.SHAPE_FILE_CODE) {

            throw new IOException("Invalid file code, " + "probably not a shape file");

        }

        fileVersion = ByteUtils.readLEInt(header, 28);

        if (fileVersion != ShapeConst.SHAPE_FILE_VERSION) {

            throw new IOException("Unable to read shape files with version " +
                                  fileVersion);

        }

        fileLength = ByteUtils.readBEInt(header, 24);

       /* 
        * convert from 16-bit words to 8-bit bytes
        */
        fileLength *= 2;

        fileShapeType = ByteUtils.readLEInt(header, 32);

       /*
        * read ESRIBoundingBox and convert to SHPEnvelope
        */
        fileMBR = new SHPEnvelope(ShapeUtils.readBox(header, 36));

    } 

} // endof class ShapeFileHeader

/*
  * 
  * Last changes:
  * $Log: FileHeader.java,v $
  * Revision 1.9  2006/07/12 14:46:14  poth
  * comment footer added
  *
  * Revision 1.8  2006/04/06 20:25:23  poth
  * *** empty log message ***
  *
  * Revision 1.7  2006/04/04 20:39:41  poth
  * *** empty log message ***
  *
  * Revision 1.6  2006/03/30 21:20:24  poth
  * *** empty log message ***
  *
  * Revision 1.5  2006/01/16 20:36:39  poth
  * *** empty log message ***
  *
  * Revision 1.4  2005/02/14 11:19:43  friebe
  * fix javadoc errors
  *
  * 07.01.2000 ap: all methods copied from ShapeFileHeader<BR> 
  * 07.01.2000 ap: newHeaderor renamed<BR>
  * 16.08.2000 ap: method writeHeader(..) added<BR>
  *//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FileHeader.java,v $
Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
