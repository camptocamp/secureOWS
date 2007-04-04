//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/Attic/InsertRow.java,v 1.18 2006/09/26 16:45:44 mschneider Exp $
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
package org.deegree.io.datastore.sql.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.TransactionException;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;

/**
 * Represents a table row (columns + values) which has to be inserted as part of a {@link Insert}
 * operation.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.18 $, $Date: 2006/09/26 16:45:44 $
 */
public class InsertRow {

    static final ILogger LOG = LoggerFactory.getLogger( InsertRow.class );

    protected String table;

    // key: column name, value: InsertField
    protected Map<String, InsertField> columnMap = new LinkedHashMap<String, InsertField>();

    /**
     * Creates a new <code>InsertRow</code> instance for the given table.
     * 
     * @param table
     */
    public InsertRow( String table ) {
        this.table = table;
    }

    /**
     * Returns the name of table.
     * 
     * @return the name of table
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Sets the value to be inserted in the given table column.
     * <p>
     * In complex + erroneous mappings (or feature instances), it may happen that different
     * property values (mapped to the same column) imply different values. This is checked for
     * and in case an {@link TransactionException} is thrown.
     * 
     * @param column
     * @param value
     * @param sqlType
     * @param isPK
     * @return column + value to be set
     * @throws TransactionException
     *            if the value for the column clashes
     */
    public InsertField setColumn( String column, Object value, int sqlType, boolean isPK )
                            throws TransactionException {

        InsertField field = new InsertField( this, column, sqlType, value, isPK );
        InsertField presentField = columnMap.get( column );
        if ( presentField != null
             && ( !presentField.getValue().toString().equals( value.toString() ) ) ) {
            Object[] params = new Object[] { column, this.table,
                                            presentField.getValue().toString(), value.toString() };
            String msg = Messages.getMessage( "DATASTORE_AMBIGOUS_COLUMN_VALUES", params );
            throw new TransactionException( msg );
        }
        if ( presentField == null ) {
            this.columnMap.put( column, field );
        }
        // TODO type check
        return field;
    }

    /**
     * Sets the value to be inserted in the given table column - the column references the given
     * {@link InsertField} and thus must have the same value as the referenced column.
     * <p>
     * In complex + erroneous mappings (or feature instances), it may happen that different
     * property values (mapped to the same column) imply different values. This is checked for
     * and in case an {@link TransactionException} is thrown.
     * 
     * @param column
     * @param referencedField
     * @throws TransactionException
     *            if the value for the column clashes
     */    
    public void linkColumn( String column, InsertField referencedField )
                            throws TransactionException {

        if ( referencedField.getValue() == null ) {
            LOG.logError( "linkColumn (): referencedField is null" );
            throw new TransactionException( "linkColumn (): referenced field is null!" );
        }

        InsertField presentField = this.columnMap.get( column );
        if ( presentField != null
             && ( !presentField.getValue().toString().equals( referencedField.getValue().toString() ) ) ) {
            Object[] params = new Object[] { column, this.table,
                                            presentField.getValue().toString(),
                                            referencedField.getValue().toString() };
            String msg = Messages.getMessage( "DATASTORE_AMBIGOUS_COLUMN_VALUES", params );
            throw new TransactionException( msg );
        }

        InsertField field = presentField;
        if ( presentField != null ) {
            presentField.relinkField( referencedField );
        } else {
            field = new InsertField( this, column, referencedField );
            this.columnMap.put( column, field );
        }
        referencedField.addReferencingField( field );
    }

    /**
     * Returns all {@link InsertField}s.
     * 
     * @return all InsertFields
     */
    public Collection<InsertField> getColumns() {
        return this.columnMap.values();
    }

    /**
     * Returns the {@link InsertField} for the given column name.
     * 
     * @param column
     *            requested column name
     * @return the InsertField for the given column name, or null if it does not exist
     */    
    public InsertField getColumn( String column ) {
        return this.columnMap.get( column );
    }

    /**
     * Returns the {@link InsertField} for the primary key column.
     *  
     * @return the InsertField for the primary key column
     */
    public InsertField getPKColumn() {
        InsertField pkField = null;
        for ( InsertField field : this.columnMap.values() ) {
            if ( field.isPK() ) {
                pkField = field;
                break;
            }
        }
        return pkField;
    }

    /**
     * Returns all {@link InsertField} that reference a column in this <code>InsertRow</code>.
     * The fields may well be from other tables.
     *  
     * @return all InsertFields that reference a column in this InsertRow
     */    
    public List<InsertField> getReferencingFields() {
        List<InsertField> referencingFields = new ArrayList<InsertField>();
        Iterator<InsertField> iter = this.columnMap.values().iterator();
        while ( iter.hasNext() ) {
            InsertField field = iter.next();
            referencingFields.addAll( field.getReferencingFields() );
        }
        return referencingFields;
    }

    /**
     * Returns all {@link InsertRows}s that reference a column in this <code>InsertRow</code>.
     * The rows may well be from other tables.
     *  
     * @return all InsertRows that reference a column in this InsertRow
     */    
    Collection<InsertRow> getReferencingRows() {
        Set<InsertRow> referencingRows = new HashSet<InsertRow>();
        Iterator<InsertField> iter = this.columnMap.values().iterator();
        while ( iter.hasNext() ) {
            InsertField field = iter.next();
            referencingRows.addAll( field.getReferencingRows() );
        }
        return referencingRows;
    }

    /**
     * Returns all {@link InsertField}s that are referenced by a field from this
     * <code>InsertRow</code>. The fields may well be from other tables.
     *  
     * @return all InsertField that are referenced by a field from this InsertRow
     */    
    List<InsertField> getReferencedFields() {
        List<InsertField> referencedFields = new ArrayList<InsertField>();
        Iterator<InsertField> iter = this.columnMap.values().iterator();
        while ( iter.hasNext() ) {
            InsertField field = iter.next();
            InsertField referencedField = field.getReferencedField();
            if ( referencedField != null ) {
                referencedFields.add( referencedField );
            }
        }
        return referencedFields;
    }

    /**
     * Returns all {@link InsertRow}s that are referenced by a field from this
     * <code>InsertRow</code>. The rows may well be from other tables.
     *  
     * @return all InsertRows that are referenced by a field from this InsertRow
     */     
    private Collection<InsertRow> getReferencedRows() {
        Set<InsertRow> referencedRows = new HashSet<InsertRow>();
        Iterator<InsertField> iter = this.columnMap.values().iterator();
        while ( iter.hasNext() ) {
            InsertField field = iter.next();
            InsertRow referencedRow = field.getReferencedRow();
            if ( referencedRow != null ) {
                referencedRows.add( referencedRow );
            }
        }
        return referencedRows;
    }

    /**
     * Sorts the given <code>InsertRow</code>s topologically (respecting the foreign key
     * constraints), so they can be inserted in the resulting order without causing foreign key
     * violations.
     * <p>
     * Number of precedessors (pre): number of fields that *are referenced by* this row
     * Number of successors (post) : number of fields that *reference* this row
     * 
     * @param inserts
     *            insert rows to sort
     * @return the given insert rows in topological order
     * @throws TransactionException
     *             if there is no topological order (i.e. there is a cyclic constraint)
     */
    public static List<InsertRow> sortInsertRows( List<InsertRow> inserts )
                            throws TransactionException {

        List<InsertRow> result = new ArrayList<InsertRow>();

        // key: inserts, value: number of fields that are referenced by this row
        Map<InsertRow, Integer> preMap = new HashMap<InsertRow, Integer>();

        // key: inserts with no foreign key constraints
        List<InsertRow> noPre = new ArrayList<InsertRow>();

        // build map
        Iterator<InsertRow> insertIter = inserts.iterator();
        while ( insertIter.hasNext() ) {
            InsertRow insertRow = insertIter.next();
            int pre = insertRow.getReferencedFields().size();
            LOG.logDebug( "Adding row to preMap: " + insertRow );
            preMap.put( insertRow, pre );
            if ( pre == 0 ) {
                noPre.add( insertRow );
            }
        }

        while ( !noPre.isEmpty() ) {
            // select an insert row that has no open fk constraints
            InsertRow insertRow = noPre.get( 0 );
            noPre.remove( 0 );
            result.add( insertRow );

            // decrease the number of pending fk constraints for all insert rows that
            // reference the currently processed insert row
            Collection<InsertField> postList = insertRow.getReferencingFields();
            Iterator<InsertField> iter = postList.iterator();
            while ( iter.hasNext() ) {
                InsertField postField = iter.next();
                if ( preMap.get( postField.row ) == null ) {
                    LOG.logDebug( "No pre info for: " + postField.row );
                }
                int pre = preMap.get( postField.row );
                preMap.put( postField.row, --pre );
                if ( pre == 0 ) {
                    noPre.add( postField.row );
                }
            }
        }

        if ( result.size() != inserts.size() ) {
            throw new TransactionException( "Cannot process Insert: cycle in foreign "
                                            + "key constraints." );
        }
        return result;
    }

    /**
     * Checks if the given <code>InsertRow</code>s contain cyclic fk constraints.
     *  
     * @param rows
     * @return steps of the cycle, or null if there is none
     */
    public static Collection<InsertRow> findCycle( Collection<InsertRow> rows ) {

        Iterator<InsertRow> rowsIter = rows.iterator();
        while ( rowsIter.hasNext() ) {
            InsertRow begin = rowsIter.next();
            Iterator<InsertRow> refIter = begin.getReferencedRows().iterator();
            while ( refIter.hasNext() ) {
                InsertRow referencedRow = refIter.next();
                List<InsertRow> cycle = findCycleRecursion( referencedRow, begin,
                                                            new ArrayList<InsertRow>() );
                if ( cycle != null ) {
                    return cycle;
                }
            }
        }
        return null;
    }
   
    private static List<InsertRow> findCycleRecursion( InsertRow next, InsertRow begin,
                                                      List<InsertRow> steps ) {

        if ( steps.contains( next ) ) {
            steps.add( next );
            return steps;
        }
        steps.add( next );

        Iterator<InsertRow> refIter = next.getReferencedRows().iterator();
        while ( refIter.hasNext() ) {
            InsertRow referencedRow = refIter.next();
            List<InsertRow> cycle = findCycleRecursion( referencedRow, begin, steps );
            if ( cycle != null ) {
                return cycle;
            }
        }
        steps.remove( steps.size() - 1 );
        return null;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "InsertRow, table: '" );
        sb.append( this.table );
        sb.append( "'" );
        Iterator<String> columnIter = this.columnMap.keySet().iterator();
        while ( columnIter.hasNext() ) {
            sb.append( ", " );
            String column = columnIter.next();
            InsertField field = this.columnMap.get( column );
            sb.append( field );
        }
        return sb.toString();
    }

    /**
     * A field of an {@link InsertRow}.  
     * 
     * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
     * @author last edited by: $Author: mschneider $
     * 
     * @version $Revision: 1.18 $, $Date: 2006/09/26 16:45:44 $
     */
    public static class InsertField {

        private String columnName;

        private Object value;

        private int sqlType;

        private boolean isPK;

        private InsertField referencedField;

        private List<InsertField> referencingFields = new ArrayList<InsertField>();

        InsertRow row;

        InsertField( InsertRow row, String columnName, int sqlType, Object value,
                           boolean isPK ) {
            this.row = row;
            this.columnName = columnName;
            this.sqlType = sqlType;
            this.value = value;
            this.isPK = isPK;
        }

        InsertField( InsertRow row, String columnName, InsertField referencedField ) {
            this.row = row;
            this.columnName = columnName;
            this.referencedField = referencedField;
        }

        /**
         * Returns the name of the table that this <code>InsertField</code> belongs to.
         * 
         * @return the name of the table that this <code>InsertField</code> belongs to
         */
        public String getTable() {
            return this.row.getTable();
        }

        /**
         * Returns whether this <code>InsertField</code> is a primary key of the table.
         * 
         * @return true, if this InsertField is a primary key of the table, false otherwise
         */
        public boolean isPK() {
            return this.isPK;
        }

        /**
         * Returns the name of the column that this <code>InsertField</code> represents.
         * 
         * @return the name of the column that this <code>InsertField</code> represents
         */        
        public String getColumnName() {
            return this.columnName;
        }

        /**
         * Returns the sql type code for the column.
         * 
         * @return the sql type code for the column
         */
        public int getSQLType() {
            if ( this.referencedField != null ) {
                return this.referencedField.getSQLType();
            }
            return this.sqlType;
        }

        /**
         * Returns the value to be inserted.
         * 
         * @return the value to be inserted
         */        
        public Object getValue() {
            if ( this.referencedField != null ) {
                return this.referencedField.getValue();
            }
            return this.value;
        }

        /**
         * Returns the <code>InsertField</code> that this <code>InsertField</code> references.
         * 
         * @return the referenced InsertField or null, if no field is referenced
         */
        public InsertField getReferencedField() {
            return this.referencedField;
        }

        InsertRow getReferencedRow() {
            if ( this.referencedField == null ) {
                return null;
            }
            return this.referencedField.row;
        }


        /**
         * Sets this <code>InsertField</code> to be a reference to the given
         * <code>InsertField</code>.
         * 
         * @param field
         */
        public void relinkField( InsertField field ) {
            if ( referencedField != null ) {
                this.referencedField.removeReferencingField( this );
            }
            this.referencedField = field;
            field.addReferencingField( this );
        }

        void addReferencingField( InsertField referencingField ) {
            if ( referencingField == null ) {
                LOG.logError( "addReferencingField() called with null value" );

            } else {
                this.referencingFields.add( referencingField );
            }
        }

        /**
         * Removes the given <code>InsertField</code> from the list of references to
         * this <code>InsertField</code>.
         * 
         * @param referencingField
         */
        public void removeReferencingField( InsertField referencingField ) {
            this.referencingFields.remove( referencingField );
        }

        Collection<InsertField> getReferencingFields() {
            return this.referencingFields;
        }

        Collection<InsertRow> getReferencingRows() {
            List<InsertRow> referencingRows = new ArrayList<InsertRow>();
            Iterator<InsertField> iter = this.referencingFields.iterator();
            while ( iter.hasNext() ) {
                referencingRows.add( iter.next().row );
            }
            return referencingRows;
        }

        /**
         * Returns a string representation of the object.
         * 
         * @return a string representation of the object
         */
        @Override        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            String stringValue = getValue().toString();
            if ( stringValue.length() > 20 ) {
                stringValue = stringValue.substring( 0, 19 );
                stringValue += "...";
            }

            int type = getSQLType();
            String typeString = "" + type;
            try {
                typeString = Types.getTypeNameForSQLTypeCode( type );
            } catch ( UnknownTypeException e ) {
                LOG.logError( e.getMessage(), e );
            }
            sb.append( getColumnName() );
            sb.append( "(" );
            sb.append( typeString );
            sb.append( ")='" );
            sb.append( stringValue );
            sb.append( '\'' );

            InsertField referencedField = getReferencedField();
            if ( referencedField != null ) {
                sb.append( " [fk to '" );
                sb.append( referencedField.getTable() );
                sb.append( '.' );
                sb.append( referencedField.getColumnName() );
                sb.append( "']" );
            }
            return sb.toString();
        }
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: InsertRow.java,v $
 * Revision 1.18  2006/09/26 16:45:44  mschneider
 * Javadoc corrections + fixed warnings.
 *
 * Revision 1.17  2006/09/20 11:35:41  mschneider
 * Merged datastore related messages with org.deegree.18n.
 *
 * Revision 1.16  2006/09/05 14:43:24  mschneider
 * Adapted due to merging of messages.
 *
 * Revision 1.15  2006/05/21 19:07:16  poth
 * several methods set to public; required by SDE datastore
 *
 * Revision 1.14  2006/04/26 13:34:09  poth
 * *** empty log message ***
 *
 * Revision 1.13  2006/04/19 18:27:03  mschneider
 * Fixed wrong import.
 * 
 * Revision 1.12  2006/04/07 17:13:36  mschneider
 * Added method getPKColumn().
 * 
 * Revision 1.11  2006/04/06 20:25:27  poth
 * *** empty log message ***
 * 
 * Revision 1.10  2006/04/04 17:51:35  mschneider
 * Made removeReferencingField() public.
 * 
 * Revision 1.9  2006/03/09 12:56:15  mschneider
 * Improved javadoc. Fixed cycle check.
 * 
 * Revision 1.8  2006/03/03 13:36:18  mschneider
 * Removed unnecessary null checks.
 * 
 * Revision 1.7  2006/03/02 18:03:51  poth
 * *** empty log message ***
 * 
 * Revision 1.6  2006/02/24 14:35:11  mschneider
 * Enabled checking for ambigous field values.
 * 
 * Revision 1.5  2006/02/23 15:30:04  mschneider
 * Fixes.
 * 
 * Revision 1.4  2006/02/23 13:06:02  mschneider
 * Added findCycle().
 * 
 * Revision 1.3  2006/02/22 02:33:34  mschneider
 * Added merging of "equal" InsertRows.
 * 
 * Revision 1.2 2006/02/20 16:33:30 mschneider
 * More work on Insert functionality.
 * 
 * Revision 1.1 2006/02/17 14:41:08 mschneider
 * Initial version.
 * 
 **************************************************************************************************/