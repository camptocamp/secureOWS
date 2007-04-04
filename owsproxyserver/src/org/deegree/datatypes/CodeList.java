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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/04/06 20:25:31 $
 *
 * @since 2.0
 */
public class CodeList implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private URI codeSpace = null;
    private String name = null;
    private List codes = new ArrayList();
    
    /**
     * @param name
     * @param codes
     */
    public CodeList(String name, String[] codes) {        
        this( name, codes, null );
    }
    
    /**
     * @param name
     * @param codes
     * @param codeSpace
     */
    public CodeList(String name, String[] codes, URI codeSpace) {
        setName(name);
        setCodes(codes);
        setCodeSpace(codeSpace);        
    }

    /**
     * @return Returns the name.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     * 
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
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
     * @return Returns the codes.
     * 
     * @uml.property name="codes"
     */
    public String[] getCodes() {
        return (String[]) codes.toArray(new String[codes.size()]);
    }

    /**
     * @param codes The codes to set.
     */
    public void setCodes(String[] codes) {
        this.codes = Arrays.asList( codes );
    }
    
    /**
     * @param code The code to add
     */
    public void addCode(String code) {
        codes.add( code );
    }
    
    /**
     * @param code The code to remove
     */
    public void removeCode(String code) {
        codes.remove( code );
    }

    /**
     * returns true if a CodeList contains the passed codeSpace-value combination. 
     * Otherwise false will be returned
     * @param value
     * @return
     */
    public boolean validate(String codeSpace, String value) {
        String[] codes = getCodes();
        URI space = getCodeSpace();
        String csp = null;
        if ( space != null ) {
            csp = space.toString();
        }        
        for (int j = 0; j < codes.length; j++) {
            if ( (csp != null && csp.equals(codeSpace)) ||
                 (csp == null && codeSpace == null) && 				 
                 codes[j].equals(value) ) {            	
                return true;
            }             
        }
        return false;
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: CodeList.java,v $
   Revision 1.5  2006/04/06 20:25:31  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:28  poth
   *** empty log message ***

   Revision 1.3  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.2.2.1  2005/11/14 11:30:43  deshmukh
   inserted: serialVersionID

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.4  2004/08/30 15:44:32  ap
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:46:13  ap
   no message


********************************************************************** */
