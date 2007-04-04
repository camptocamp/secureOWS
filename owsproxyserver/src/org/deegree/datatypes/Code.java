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
package org.deegree.datatypes;

import java.io.Serializable;
import java.net.URI;

/**
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/11/29 15:57:21 $
 *
 * @since 2.0
 */
public class Code implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String code = null;
    private URI codeSpace = null;
    private int ordinal = Integer.MIN_VALUE;
    
    /**
     * @param code
     */
    public Code(String code) {
        this.code = code;
    }

    /**
     * @param code
     * @param codeSpace
     */
    public Code(String code, URI codeSpace) {
        this.code = code;
        this.codeSpace = codeSpace;
    }
    
    /**
     * @param code
     * @param ordinal
     */
    public Code(String code, int ordinal) {
        this.code = code;
        this.ordinal = ordinal;
    }
    
    /**
     * @param code
     * @param codeSpace
     * @param ordinal
     */
    public Code(String code, URI codeSpace, int ordinal) {
        this.code = code;
        this.codeSpace = codeSpace;
        this.ordinal = ordinal;
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code to set.
     * 
     * @uml.property name="code"
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Returns the codeSpace.
     * 
     * @uml.property name="codeSpace"
     */
    public URI getCodeSpace() {
        return codeSpace;
    }

    /**
     * @param codeSpace The codeSpace to set.
     * 
     * @uml.property name="codeSpace"
     */
    public void setCodeSpace(URI codeSpace) {
        this.codeSpace = codeSpace;
    }

    /**
     * @return Returns the ordinal.
     * 
     * @uml.property name="ordinal"
     */
    public int getOrdinal() {
        return ordinal;
    }

    /**
     * @param ordinal The ordinal to set.
     * 
     * @uml.property name="ordinal"
     */
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    
    /**
     * Tests this Code for equality with another object.
     * @param other object to compare
     */
    @Override
    public boolean equals(Object other) {
    	if ( other == null || !(other instanceof Code) ) {
    		return false;
    	}
    	Code oc = (Code)other;
    	if ( ordinal != oc.ordinal ) {
        	return false;
        }
    	if ( !code.equals( oc ) ) {
    		return false;
    	}
        if ( !codeSpace.equals( oc ) ) {
        	return false;
        }        
    	return true;
    }
    
    @Override
    public String toString(){
        return ((codeSpace!= null)?(codeSpace.toASCIIString() + '/'):' ') + code;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Code.java,v $
   Revision 1.8  2006/11/29 15:57:21  bezema
   added toString and javadoc

   Revision 1.7  2006/04/06 20:25:31  poth
   *** empty log message ***

   Revision 1.6  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.5  2006/03/30 21:20:28  poth
   *** empty log message ***

   Revision 1.4  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.3.2.1  2005/11/14 11:30:43  deshmukh
   inserted: serialVersionID

   Revision 1.3  2005/02/23 17:06:56  poth
   no message

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.4  2004/09/03 06:24:41  ap
   no message

   Revision 1.3  2004/08/16 06:23:33  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:46:13  ap
   no message


********************************************************************** */
