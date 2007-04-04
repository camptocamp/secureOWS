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

/**
 * Class representing a record of an ESRI .shx file.
 * 
 * 
 * @author Klaus Fretter
 * @author Andreas Poth
 * @version 31.08.2000
 */

public class IndexRecord {

    protected int offset;

    protected int length;

    public IndexRecord() {
        this.offset = 0;
        this.length = 0;
    }

    public IndexRecord(int off, int len) {
        this.offset = off;
        this.length = len;
    }

    public IndexRecord(byte[] recBuf) {
        this.offset = ByteUtils.readBEInt(recBuf, 0);
        this.length = ByteUtils.readBEInt(recBuf, 4);
    }

    /**
     * 
     * @uml.property name="length"
     */
    public int getLength() {
        return length;
    }

    /**
     * 
     * @uml.property name="offset"
     */
    public int getOffset() {
        return offset;
    }

    public byte[] writeIndexRecord() {
        byte[] arr = new byte[8];
        ByteUtils.writeBEInt(arr, 0, offset);
        ByteUtils.writeBEInt(arr, 4, length);
        return arr;
    }

} // end of class IndexRecord
/*
 * Changes: $Log: IndexRecord.java,v $
 * Changes: Revision 1.7  2006/07/12 14:46:14  poth
 * Changes: comment footer added
 * Changes:
 * Changes: Revision 1.6  2006/04/06 20:25:23  poth
 * Changes: *** empty log message ***
 * Changes:
 * Changes: Revision 1.5  2006/04/04 20:39:41  poth
 * Changes: *** empty log message ***
 * Changes:
 * Changes: Revision 1.4  2006/03/30 21:20:24  poth
 * Changes: *** empty log message ***
 * Changes:
 * Changes: Revision 1.3  2005/02/14 11:19:43  friebe
 * Changes: fix javadoc errors
 * Changes: 31.08.2000 ap: method getLength() added <BR> 31.08.2000 ap:
 * method getOffset() added <BR> 31.08.2000 ap: method writeIndexRecord added
 * <BR>
 *//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IndexRecord.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
