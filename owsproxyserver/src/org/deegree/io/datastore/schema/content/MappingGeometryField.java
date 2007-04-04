//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/content/MappingGeometryField.java,v 1.5 2006/11/29 17:00:16 mschneider Exp $
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
 Aennchenstraße 19
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
package org.deegree.io.datastore.schema.content;

/**
 * Encapsulates a geometry field of the backend (e.g. an SQL table column).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/11/29 17:00:16 $
 */
public class MappingGeometryField extends MappingField {

    private int srs;

    /**
     * Creates a new instance of <code>MappingGeometryField</code> from the given parameters.
     * 
     * @param table
     * @param field
     * @param typeCode
     * @param srs
     *            -1: no SRS information available
     */
    public MappingGeometryField( String table, String field, int typeCode, int srs ) {
        super( table, field, typeCode );
        this.srs = srs;
    }

    /**
     * Returns false, because a geometry db field is not suitable as a sort criterion. 
     * 
     * @return false, because a geometry db field is not suitable as a sort criterion
     */    
    @Override
    public boolean isSortable () {
        return false;
    }    
    
    /**
     * Always returns false.
     * 
     * @return false
     */
    @Override
    public boolean isNumeric() {
        return false;
    }

    /**
     * Returns the vendor dependent descriptor for the SRS of the stored geometries.
     * 
     * @return vendor dependent descriptor for the SRS, may be null
     */
    public int getSRS() {
        return this.srs;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MappingGeometryField.java,v $
Revision 1.5  2006/11/29 17:00:16  mschneider
Changed srs to integer.

Revision 1.4  2006/08/29 15:53:18  mschneider
Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable(). Added #isSortable().

Revision 1.3  2006/08/28 16:41:49  mschneider
Fixed @Override annotations.

Revision 1.2  2006/08/24 06:40:05  poth
File header corrected

Revision 1.1  2006/08/21 16:41:13  mschneider
Moved from parent package.

Revision 1.8  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */