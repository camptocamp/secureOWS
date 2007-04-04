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
package org.deegree.model.table;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision: 1.9 $ $Date: 2006/07/12 14:46:19 $
 */
public class DefaultTable implements Table {

    private String tableName = "";

    private ArrayList rows = null;

    private String[] columnNames = null;

    private int[] columnTypes = null;

    private HashMap columnNamesMap = new HashMap();

    /**
     * @param tableName
     * @param columnNames
     * @param columnTypes
     * @throws TableException
     */
    public DefaultTable(String tableName, String[] columnNames,
                        int[] columnTypes) throws TableException {
        setTableName(tableName);

        if (columnTypes == null)
            throw new TableException(
                    "Invalid column types. Column types = null");

        this.columnTypes = columnTypes;

        if (columnNames == null) {
            this.columnNames = new String[columnTypes.length];
            for (int i = 0; i < this.columnNames.length; i++) {
                this.columnNames[i] = "";
            }
        } else {
            this.columnNames = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                this.columnNames[i] = columnNames[i].toUpperCase();
                columnNamesMap.put(this.columnNames[i], new Integer(i));
            }
        }

        if (columnTypes.length != this.columnNames.length) {
            throw new TableException("column names and types are not of the "
                    + "same length");
        }

        rows = new ArrayList(1000);
    }

    /**
     * @param tableName
     * @param columnNames
     * @param columnTypes
     * @param data
     * @throws TableException
     */
    public DefaultTable(String tableName, String[] columnNames,
            int[] columnTypes, Object[][] data) throws TableException {
        this(tableName, columnNames, columnTypes);

        rows = new ArrayList(data.length);
        for (int i = 0; i < data.length; i++) {
            appendRow(data[i]);
        }
    }

    /**
     * @param tableName
     * @param columnNames
     * @param columnTypes
     * @param initialCapacity
     * @throws TableException
     */
    public DefaultTable(String tableName, String[] columnNames,
            int[] columnTypes, int initialCapacity) throws TableException {
        this(tableName, columnNames, columnTypes);
        rows.ensureCapacity(initialCapacity);
    }

    /**
     * returns the name of the table. If the table hasn't a name an empty string
     * ("") will be returned.
     * 
     * @uml.property name="tableName"
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @see org.deegree.model.table.DefaultTable#getTableName()
     * 
     * @uml.property name="tableName"
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * returns the value of the table field indexed by <tt>row</tt> and
     * <tt>col</tt>
     */
    public Object getValueAt(int row, int col) {
        ArrayList tmp = (ArrayList) rows.get(row);
        return tmp.get(col);
    }

    /**
     * set a value at the table field indexed by <tt>row</tt> and <tt>col</tt>
     */
    public void setValueAt(Object value, int row, int col) {
        ArrayList tmp = (ArrayList) rows.get(row);
        tmp.set(col, value);
    }

    /**
     * returns the data of the row'th row of the table
     */
    public Object[] getRow(int row) {
        ArrayList tmp = (ArrayList) rows.get(row);
        return tmp.toArray();
    }

    /**
     * sets the data of the row'th row
     */
    public void setRow(Object[] data, int row) throws TableException {
        if (this.getColumnCount() != data.length) {
            throw new TableException(
                    "submitted row doesn't have the same length"
                            + " as the table has columns.");
        }
        ArrayList tmp = (ArrayList) rows.get(row);

        for (int i = 0; i < data.length; i++) {
            tmp.set(i, data[i]);
        }
    }

    /**
     * appends a row to the table and sets its data
     */
    public void appendRow(Object[] data) throws TableException {
        if (this.getColumnCount() != data.length) {
            throw new TableException(
                    "submitted row doesn't have the same length"
                            + " as the table has columns.");
        }
        ArrayList tmp = new ArrayList(data.length);
        for (int i = 0; i < data.length; i++) {
            tmp.add(data[i]);
        }
        rows.add(tmp);
    }

    /**
     * returns the number rows of the table
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * adds a new column to the table. for this a computional expensive
     * operation this method should be used with care.
     */
    public void addColumn(String name, int type) {
        String[] s1 = new String[columnNames.length + 1];
        int[] s2 = new int[columnNames.length + 1];
        for (int i = 0; i < columnNames.length; i++) {
            s1[i] = columnNames[i];
            s2[i] = columnTypes[i];
        }
        s1[s1.length - 1] = name;
        s2[s2.length - 1] = type;
        columnNames = s1;
        columnTypes = s2;

        for (int i = 0; i < rows.size(); i++) {
            ArrayList tmp = (ArrayList) rows.get(i);
            tmp.add("");
        }

    }

    /**
     * returns the number columns of the table
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * returns the names of all table columns. If a column hasn't a name a empty
     * String ("") will be returned.
     * 
     * @uml.property name="columnNames"
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * returns the name of the specified column. If a column hasn't a name a
     * empty String ("") will be returned.
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * returns the names of all column types. For each column a type (name of a
     * java class) has to be defined.
     * 
     * @uml.property name="columnTypes"
     */
    public int[] getColumnTypes() {
        return columnTypes;
    }

    /**
     * returns the name of the type of the specifies column. For each column a
     * type (name of a java class) has to be defined.
     */
    public int getColumnType(int col) {
        return columnTypes[col];
    }

    /**
     * sets the type of a column.
     */
    public void setColumnType(int col, int type) throws TableException {
        columnTypes[col] = type;
    }

    /**
     * sets the name of a column.
     */
    public void setColumnName(int col, String name) {
        columnNames[col] = name;
    }

    /**
     * removes a row from the table
     */
    public Object[] removeRow(int index) {
        ArrayList list = (ArrayList) rows.remove(index);
        return list.toArray();
    }

    /**
     * export the table as a instance of <tt>javax.swing.table.TableModel</tt>.
     * The instance will be created using the
     * <tt>javax.swing.table.DefaultTableModel</tt>
     */
    public javax.swing.table.TableModel exportAsJDKTableModel() {
        return null;
    }

    /**
     * returns the index of the submitted columns name. If no column with that
     * name if present -1 will be returned. the test is not case sensitive
     *  
     */
    public int getColumnIndex(String columnName) {
        Integer index = (Integer) columnNamesMap.get(columnName.toUpperCase());
        return index.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100000);
        for (int i = 0; i < getRowCount(); i++) {
            sb.append("row: " + i);
            for (int c = 0; c < getColumnCount(); c++) {
                sb.append(getColumnName(c) + ": " + getValueAt(i, c));
            }
        }
        return sb.toString();
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultTable.java,v $
Revision 1.9  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
