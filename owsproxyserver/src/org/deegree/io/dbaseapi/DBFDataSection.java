
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

package org.deegree.io.dbaseapi;


import java.util.ArrayList;

/**
 * Class representing a record of the data section of a dBase III/IV file<BR> * at the moment only the daata types character ("C") and numeric ("N") * are supported *  * @version 03.05.2000 * @author Andreas Poth
 */


public class DBFDataSection {    

    // length of one record in bytes
    private int recordlength = 0;

    /**
     * 
     * @uml.property name="fieldDesc"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private FieldDescriptor[] fieldDesc = null;

    private ArrayList data = new ArrayList();

   /**
    * constructor
    */
    public DBFDataSection(FieldDescriptor[] fieldDesc) {

        this.fieldDesc = fieldDesc;
        
        // calculate length of the data section
        recordlength = 0;
        for (int i = 0; i < this.fieldDesc.length; i++) {            

            byte[] fddata = this.fieldDesc[i].getFieldDescriptor();

            recordlength += fddata[16];

            fddata = null;

        }

        recordlength++;

    }

   /**
    * method: public setRecord(ArrayList recData)
    * writes a data record to byte array representing the data
    * section of the dBase file. The method gets the data type
    * of each field in recData from fieldDesc wich has been
    * set at the constructor. 
    */
    public void setRecord(ArrayList recData) throws DBaseException {

        setRecord(data.size(),recData);

    }

   /**
    * method: public setRecord(int index, ArrayList recData)
    * writes a data record to byte array representing the data
    * section of the dBase file. The method gets the data type
    * of each field in recData from fieldDesc wich has been
    * set at the constructor. index specifies the location
    * of the retrieved record in the datasection. if an invalid
    * index is used an exception will be thrown
    */
    public void setRecord(int index, ArrayList recData) throws DBaseException {

        ByteContainer datasec = new ByteContainer(recordlength);

        if ( (index < 0) || (index > data.size()) )
            throw new DBaseException("invalid index: "+index);        
        
        if (recData.size() != this.fieldDesc.length) 
            throw new DBaseException("invalid size of recData");        

        int offset = 0;

        datasec.data[offset] = 0x20;

        offset++;

        byte[] b = null;

        // write every field on the ArrayList to the data byte array
        for (int i = 0; i < recData.size(); i++) {
            byte[] fddata = this.fieldDesc[i].getFieldDescriptor();            
            switch (fddata[11]) {

                // if data type is character            
                case (byte)'C': if ( recData.get(i) != null &&
                                    !(recData.get(i) instanceof String)) {        
                                    throw new DBaseException("invalid data type at field: "+i);
                                }
                                if ( recData.get(i) == null ) {
                                    b = new byte[0]; 
                                } else {
                                    b = ((String)recData.get(i)).getBytes();
                                }
                                if (b.length > fddata[16]) 
                                    throw new DBaseException("string contains too many characters "+
                                                        (String)recData.get(i));
                                for (int j = 0; j < b.length; j++) datasec.data[offset+j] = b[j];
                                for (int j = b.length; j < fddata[16]; j++) datasec.data[offset+j] = 0x20;
                                break;
                case (byte)'N': if ( recData.get(i) != null &&
                                    !(recData.get(i) instanceof Number))
                                    throw new DBaseException("invalid data type at field: "+i);
                                if ( recData.get(i) == null ) {
                                    b = new byte[0]; 
                                } else {
                                    b = ((Number)recData.get(i)).toString().getBytes();
                                }
                                if (b.length > fddata[16]) 
                                    throw new DBaseException("string contains too many characters "+
                                                        (String)recData.get(i));
                                for (int j = 0; j < b.length; j++) datasec.data[offset+j] = b[j];        
                                for (int j = b.length; j < fddata[16]; j++) datasec.data[offset+j] = 0x0;
                                break;
                default: throw new DBaseException("data type not supported");

            }

            offset += fddata[16];

        }

        // puts the record to the ArrayList (container)
        data.add( index, datasec );

    }

   /**
    * method: public byte[] getDataSection()
    * returns the data section as a byte array. 
    */
    public byte[] getDataSection() {

        // allocate memory for all datarecords on one array + 1 byte
        byte[] outdata = new byte[data.size()*recordlength+1];

        // set the file terminating byte
        outdata[outdata.length-1] = 0x1A;

        // get all records from the ArrayList and put it 
        // on a single array
        int j = 0;
        for (int i = 0; i < data.size(); i++) {
 
            ByteContainer bc = (ByteContainer) data.get(i);

            for (int k = 0; k < recordlength; k++) {
                outdata[j++] = bc.data[k];
            }

        }

        return outdata;

    }

   /**
    * method: public int getNoOfRecords()
    * returns the number of records within the container
    */
    public int getNoOfRecords() {

        return data.size();

    }


}


class  ByteContainer {

    public byte[] data = null;

    public ByteContainer(int size) {

        data = new byte[size];

    }

}
/*
 * Last changes:
 * $Log: DBFDataSection.java,v $
 * Revision 1.13  2006/07/12 14:46:16  poth
 * comment footer added
 *
 * Revision 1.12  2006/05/12 15:26:05  poth
 * *** empty log message ***
 *
 * Revision 1.11  2006/04/06 20:25:30  poth
 * *** empty log message ***
 *
 * Revision 1.10  2006/04/04 20:39:44  poth
 * *** empty log message ***
 *
 * Revision 1.9  2006/03/30 21:20:28  poth
 * *** empty log message ***
 *
 * Revision 1.8  2005/12/18 19:06:30  poth
 * no message
 *
 * Revision 1.7  2005/11/06 21:33:43  poth
 * no message
 *
 * Revision 1.6  2005/10/20 19:03:10  poth
 * no message
 *
 * Revision 1.5  2005/07/08 13:24:53  poth
 * no message
 *
 * Revision 1.4  2005/06/16 08:27:31  poth
 * no message
 *
 * Revision 1.3  2005/02/13 21:34:58  friebe
 * fix javadoc errors
 *
 * 28.04.00 ap: constructor declared and implemented<BR>
 * 28.04.00 ap: method setRecord(ArrayList recData) declared and implemented<BR>
 * 28.04.00 ap: method getDataSection() declared and implemented<BR>
 * 03.05.00 ap: method setRecord(ArrayList recData) modified<BR>
 * 03.05.00 ap: method setRecord(int index, ArrayList recData) declared and implemented<BR>
 * 03.05.00 ap: method getDataSection() modified<BR>
 *//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBFDataSection.java,v $
Revision 1.13  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
