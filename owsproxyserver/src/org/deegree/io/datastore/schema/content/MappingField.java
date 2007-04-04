//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/content/MappingField.java,v 1.4 2006/08/29 15:53:18 mschneider Exp $
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
 * Encapsulates a field of the backend (e.g. an SQL table column).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/08/29 15:53:18 $
 */
public class MappingField implements SimpleContent {

    private String table;

    private String field;

    private int type;

    private boolean auto;

    /**
     * Creates a new instance of <code>MappingField</code> from the given parameters.
     * 
     * @param table
     * @param field
     * @param type type code
     * @param auto
     * 
     * @see java.sql.Types
     */
    public MappingField( String table, String field, int type, boolean auto ) {
        this.table = table;
        this.field = field;
        this.type = type;
        this.auto = auto;
    }

    /**
     * Creates a new instance of <code>MappingField</code> from the given parameters with no
     * automatic generation of values.
     * 
     * @param table
     * @param field
     * @param type
     * 
     * @see java.sql.Types
     */
    public MappingField( String table, String field, int type ) {
        this.table = table;
        this.field = field;
        this.type = type;
    }

    /**
     * Returns true, because a db field may be updated.
     * 
     * @return true, because a db field may be updated
     */
    public boolean isUpdateable() {
        return true;
    }    

    /**
     * Returns true, because a db field is (in general) suitable as a sort criterion. 
     * 
     * @return true, because a db field is (in general) suitable as a sort criterion
     */    
    public boolean isSortable () {
        return true;
    }
    
    /**
     * Returns the name of the backend's (e.g. database) table.
     * 
     * @return the table name
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Sets the table to the given table name. This is currently needed, as
     * <code>MappedGMLSchema</code> must be able to resolve unspecified (null) table names.
     * 
     * @param table table name to set
     */
    public void setTable( String table ) {
        this.table = table;
    }

    /**
     * Returns the name of the backend's (e.g. database) field.
     * 
     * @return the field name
     */
    public String getField() {
        return this.field;
    }

    /**
     * Returns the SQL type code of the field.
     * 
     * @return the SQL type code
     * @see java.sql.Types
     */
    public int getType() {
        return this.type;
    }

    /**
     * Returns whether the backend generates the value automatically on insert.
     * 
     * @return true, if a value for this field is generated automatically, false otherwise
     */
    public boolean isAuto() {
        return this.auto;
    }

    /**
     * Returns <code>true</code> if the field has a numerical type.
     * 
     * @see java.sql.Types
     * 
     * @return <code>true</code> if the field has a numerical type, false otherwise
     */
    public boolean isNumeric() {
        switch (getType()) {
        case java.sql.Types.BIT:
        case java.sql.Types.BIGINT:
        case java.sql.Types.INTEGER:
        case java.sql.Types.FLOAT:
        case java.sql.Types.DOUBLE:
        case java.sql.Types.DECIMAL:
        case java.sql.Types.NUMERIC:
        case java.sql.Types.REAL:
        case java.sql.Types.SMALLINT:
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return this.table + "." + this.field;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MappingField.java,v $
Revision 1.4  2006/08/29 15:53:18  mschneider
Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable(). Added #isSortable().

Revision 1.3  2006/08/28 16:41:49  mschneider
Fixed @Override annotations.

Revision 1.2  2006/08/23 16:31:04  mschneider
Changed to implement SimpleContent.

Revision 1.1  2006/08/21 16:41:13  mschneider
Moved from parent package.

Revision 1.10  2006/08/21 15:43:20  mschneider
Cleanup. Added "content" subpackage. Removed (completely unused and outdated) FeatureArrayPropertyType.

Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */