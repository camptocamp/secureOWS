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
package org.deegree.ogcwebservices.csw.configuration;

import org.deegree.owscommon.OWSDomainType;
import org.deegree.owscommon.OWSMetadata;

/**
 *  Specialized implementation of the <code>OWSDomainType</code> used for
 * specifying the "typeName" parameter of the "GetRecords"-operation.
 * <p>
 * Every value has 1 additional attribute:
 * <ul>
 * <li>deegree:schema</li>
 * </ul>
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/07/12 14:46:16 $
 *
 * @since 2.0
 */
public class CatalogueTypeNameSchemaParameter extends OWSDomainType {
    
    private CatalogueTypeNameSchemaValue[] values;
    
    /**
     * 
     * @param name
     * @param schemaLocation
     * @param metadata
     */
    public CatalogueTypeNameSchemaParameter(String name, 
            CatalogueTypeNameSchemaValue[] values,
            OWSMetadata[] metadata) {
        super(name, metadata);
        this.values = values;
    }
    
    /**
     * returns the 
     * @return
     */
    public CatalogueTypeNameSchemaValue[] getSpecializedValues() {
        return values;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueTypeNameSchemaParameter.java,v $
Revision 1.5  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
