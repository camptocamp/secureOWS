
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




/**
 * Class representing a field descriptor of a dBase III/IV file
 * 
 * @version 28.04.2000
 * @author Andreas Poth
 */


public class FieldDescriptor {

   /**
    * fieldinformation as byte array
    */
    private byte[] data = null; 


   /**
    * constructor
    * recieves name and type of the field, the length of the field
    * in bytes and the decimalcount. the decimalcount is only considered
    * if type id "N" or "F", it's maxvalue if fieldlength - 2!
    */
    public FieldDescriptor(String name, String type, byte fieldlength, 
                           byte decimalcount) throws DBaseException {

        if ( (!type.equalsIgnoreCase("C")) &&
             (!type.equalsIgnoreCase("D")) &&
             (!type.equalsIgnoreCase("F")) &&
             (!type.equalsIgnoreCase("N")) &&
             (!type.equalsIgnoreCase("M")) &&
             (!type.equalsIgnoreCase("L")) ) throw new DBaseException("data type is not supported");

        data = new byte[32];
    
        // fill first 11 bytes with ASCII zero
        for (int i = 0; i <= 10; i++) data[i] = 0x0;        

        // copy name into the first 11 bytes
        byte[] dum = name.getBytes();

        int cnt = dum.length;

        if (cnt > 11) cnt = 11;

        for (int i = 0; i < cnt; i++) data[i] = dum[i];        

        byte[] b = type.getBytes();

        data[11] = b[0];

        // set fieldlength
        data[16] = fieldlength;

        // set decimalcount
        if (type.equalsIgnoreCase("N") || type.equalsIgnoreCase("F") ) 
            data[17] = decimalcount;
            else data[17] = 0;        

        // throw DBaseException if the decimalcount is larger then the
        // number off fields required for plotting a float number
        // as string
        if (data[17] > data[16]-2) 
            throw new DBaseException("invalid fieldlength and/or decimalcount");

        // work area id (don't know if it should be 1)
        data[20] = 1;

        // has no index tag in a MDX file
        data[31] = 0x00;

        // all other fields are reserved!

    }    

   /**
    * method: public byte[] getFieldDescriptor()
    * returns the field descriptor as byte array
    */
    public byte[] getFieldDescriptor() {

        return data;

    }

}
/*
 * Last changes:
 * $Log: FieldDescriptor.java,v $
 * Revision 1.6  2006/07/12 14:46:16  poth
 * comment footer added
 *
 * Revision 1.5  2006/04/06 20:25:30  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/04/04 20:39:44  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/30 21:20:28  poth
 * *** empty log message ***
 *
 * Revision 1.2  2005/02/13 21:34:58  friebe
 * fix javadoc errors
 *
 * 28.04.00 ap: constructor declared and implemented<BR>
 * 28.04.00 ap: method getFieldDescriptor() declared and implemented<BR>
 *//* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FieldDescriptor.java,v $
Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
