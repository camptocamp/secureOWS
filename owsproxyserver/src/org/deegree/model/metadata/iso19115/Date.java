/*
----------------    FILE HEADER  ------------------------------------------
 
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

package org.deegree.model.metadata.iso19115;


/**
 * Date_Impl.java
 *
 * Created on 16. September 2002, 09:58
 * <p>----------------------------------------------------------------------</p>
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer</a>
 * @version $Revision: 1.5 $ $Date: 2006/07/12 14:46:18 $
 */

public class Date {
    
    private String datetype = null;

    /** Creates a new instance of Date_Impl */
    public Date(String datetype) {
        setDateType(datetype);
    }

    /**
     * returns the DateType attribute
     * possible value are:<ul>
     * <li>creation
     * <li>publication
     * <li>revision
     * @return String
     */
    public String getDateType() {
        return datetype;
    }
    
    /**
     * @see #getDateType()
     */
    public void setDateType(String datetype) {
        this.datetype = datetype;
    }

	
	/**
     * to String method
     */
	public String toString() {
		String ret = null;
		ret = "datetype = " + datetype + "\n";
		return ret;
	}
    
    

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Date.java,v $
Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
