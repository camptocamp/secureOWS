//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/TableRelation.java,v 1.18 2006/08/30 16:59:58 mschneider Exp $
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
package org.deegree.io.datastore.schema;

import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.idgenerator.IdGenerationException;
import org.deegree.io.datastore.idgenerator.IdGenerator;
import org.deegree.io.datastore.schema.content.MappingField;

/**
 * Describes a relation (join condition) between two database tables.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.18 $, $Date: 2006/08/30 16:59:58 $
 */
public class TableRelation {

    /**
     * Used to represent the foreign key position.
     */
    public static enum FK_INFO {
        
        /**
         * No foreign key information available (sufficient for read access).
         */
        noFKInfo,
        
        /**
         * Foreign stored in 'To'-table, primary key in 'From'-table.
         */
        fkIsToField,
        
        /**
         * Foreign stored in 'From'-table, primary key in 'To'-table.
         */
        fkIsFromField
    }    
   
    private MappingField[] fromFields;

    private MappingField[] toFields;

    private FK_INFO fkInfo;
    
    private IdGenerator idGenerator;

    /**
     * Initializes a newly created <code>TableRelation</code> instance with the given
     * parameters. 
     *  
     * @param fromFields
     * @param toFields
     * @param fkInfo
     * @param idGenerator
     */
    public TableRelation( MappingField[] fromFields, MappingField[] toFields, FK_INFO fkInfo,
                         IdGenerator idGenerator ) {
        if ( fromFields.length < 1 ) {
            throw new IllegalArgumentException(
                "A relation between two tables must have at least one 'from' field." );
        }
        if ( toFields.length < 1 ) {
            throw new IllegalArgumentException(
                "A relation between two tables must have at least one 'to' field." );
        }
        if ( fromFields.length != toFields.length ) {
            throw new IllegalArgumentException(
                "A relation between two tables must have the same number of 'from' and 'to' fields." );
        }
        this.fromFields = fromFields;
        this.toFields = toFields;
        this.fkInfo = fkInfo;
        this.idGenerator = idGenerator;
    }

    /**
     * Returns the name of the table where the relation starts ('From'-table).
     * 
     * @return the name of the table where the relation starts ('From'-table)
     */
    public String getFromTable() {
        return fromFields[0].getTable();
    }

    /**
     * Returns the name of the table where the relation ends ('To'-table).
     * 
     * @return the name of the table where the relation ends ('To'-table)
     */    
    public String getToTable() {
        return toFields[0].getTable();
    }    

    /**
     * Returns the {@link MappingField}s that constitute the key in the 'From'-table.
     *  
     * @return the MappingFields that constitute the key in the 'From'-table
     */
    public MappingField [] getFromFields () {
        return this.fromFields;
    }

    /**
     * Returns the {@link MappingField}s that constitute the key in the 'To'-table.
     *  
     * @return the MappingFields that constitute the key in the 'To'-table
     */    
    public MappingField [] getToFields () {
        return this.toFields;
    }    

    /**
     * Returns the foreign key position.
     * 
     * @return the foreign key position
     */
    public FK_INFO getFKInfo () {
        return this.fkInfo;
    }

    /**
     * Returns whether the foreign key is stored in the 'From'-table.
     * 
     * @return true, if foreign key information is available and foreign key is in 'From'-table,
     *         false otherwise
     */
    public boolean isFromFK() {
        boolean isFromFK = false;
        switch (this.fkInfo) {
        case fkIsFromField: {
            isFromFK = true;
            break;
        }
        case fkIsToField: {
            isFromFK = false;
            break;
        }
        default: {
            throw new RuntimeException( "No foreign key information available for relation: "
                + this );
        }
        }
        return isFromFK;
    }


    /**
     * Returns the {@link IdGenerator} instance that may be used to generate new primary keys.
     * 
     * @return IdGenerator instance that may be used to generate new primary keys
     */
    public IdGenerator getIdGenerator () {
        return this.idGenerator;
    }
    
    /**
     * Returns a new primary key generated by the associated {@link IdGenerator}.
     * 
     * @param ta
     * @return a new primary key
     * @throws IdGenerationException
     */
    public Object getNewPK( DatastoreTransaction ta ) throws IdGenerationException {
        return this.idGenerator.getNewId( ta );
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( getFromTable() );
        if (this.fkInfo == FK_INFO.fkIsFromField) {
            sb.append(" (fk) ");
        }
        sb.append( " -> " );
        sb.append( getToTable() );
        if (this.fkInfo == FK_INFO.fkIsToField) {
            sb.append(" (fk) ");
        }        
        sb.append( " (" );
        for (int i = 0; i < fromFields.length; i++) {
            sb.append( fromFields[i] );
            sb.append( "=" );
            sb.append( toFields[i] );
            if ( i != fromFields.length - 1 ) {
                sb.append( " AND " );
            }
        }
        sb.append( ")" );
        return sb.toString();
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TableRelation.java,v $
Revision 1.18  2006/08/30 16:59:58  mschneider
Improved javadoc.

Revision 1.17  2006/08/28 16:42:15  mschneider
Javadoc fixes. Fixed @Override annotations.

Revision 1.16  2006/08/24 06:40:05  poth
File header corrected

Revision 1.15  2006/08/23 12:18:02  mschneider
Javadoc fixes.

Revision 1.14  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.

Revision 1.13  2006/08/21 16:42:36  mschneider
Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

Revision 1.12  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */