//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/capabilities/FormatType.java,v 1.6 2006/10/02 16:53:08 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.capabilities;

import java.net.URI;

public class FormatType {
    
    private URI inFilter, outFilter, schemaLocation;
    
    private String value;
    
    public FormatType (URI inFilter, URI outFilter, URI schemaLocation, String value) {
        this.inFilter = inFilter;
        this.outFilter = outFilter;
        this.schemaLocation = schemaLocation;
        this.value = value;        
    }
    /**
     * @return Returns the inFilter.
     */
    public URI getInFilter() {
        return inFilter;
    }
    /**
     * @param inFilter The inFilter to set.
     */
    public void setInFilter(URI inFilter) {
        this.inFilter = inFilter;
    }
    /**
     * @return Returns the outFilter.
     */
    public URI getOutFilter() {
        return outFilter;
    }
    /**
     * @param outFilter The outFilter to set.
     */
    public void setOutFilter(URI outFilter) {
        this.outFilter = outFilter;
    }
    /**
     * @return Returns the schemaLocation.
     */
    public URI getSchemaLocation() {
        return schemaLocation;
    }
    /**
     * @param schemaLocation The schemaLocation to set.
     */
    public void setSchemaLocation(URI schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns whether the format definition is virtual, i.e. it is processed using an (input)
     * XSLT-script.
     * 
     * @return true, if the format is virtual, false otherwise
     */
    public boolean isVirtual() {
        return this.inFilter != null;
    }     
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FormatType.java,v $
Revision 1.6  2006/10/02 16:53:08  mschneider
Added #isVirtual().

Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */