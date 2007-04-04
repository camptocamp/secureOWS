package org.deegree.model.table;

public interface Table {

    /**
     * returns the name of the table. If the table hasn't a name an empty string
     * ("") will be returned.
     * 
     * @uml.property name="tableName"
     */
    String getTableName();

    /**
     * @see Table#getTableName()
     * 
     * @uml.property name="tableName"
     */
    void setTableName(String tableName);

   /**
    * returns the value of the table field indexed by <tt>row</tt> and <tt>col</tt>
    */
    Object getValueAt(int row, int col);

   /**
    * set a value at the table field indexed by <tt>row</tt> and <tt>col</tt>
    */
    void setValueAt(Object value, int row, int col);

   /**
    * returns the data of the row'th row of the table
    */
    Object[] getRow(int row);

   /**
    * sets the data of the row'th row
    */
    void setRow(Object[] data, int row) throws TableException;

   /**
    * appends a row to the table and sets its data
    */
    void appendRow(Object[] data) throws TableException;

   /**
    * returns the number rows of the table
    */
    int getRowCount();
    
   /**
    * adds a new column to the table
    */ 
    void addColumn(String name, int type);

   /**
    * returns the number columns of the table
    */
    int getColumnCount();

   /**
    * returns the names of all table columns. If a column hasn't a name a empty
    * String ("") will be returned.
    */
    String[] getColumnNames();

   /**
    * returns the name of the specified column. If a column hasn't a name a empty
    * String ("") will be returned.
    */
    String getColumnName(int col);

   /**
    * returns the names of all column types. For each column a type (name of a
    * java class) has to be defined.
    */
    int[] getColumnTypes();

   /**
    * returns the name of the type of the specifies column. For each column a
    * type (name of a java class) has to be defined.
    */
    int getColumnType(int col);
    
   /**
    * sets the type of a column. the implementing class have to
    * ensure that this is a valid operation
    */
    void setColumnType(int col, int type) throws TableException;
    
   /**
    * sets the name of a column. 
    */
    void setColumnName(int col, String name);

   /**
    * removes a row from the table
    */
    Object[] removeRow(int index);
    
    /**
     * returns the index of the submitted columns name. If no column with that 
     * name if present -1 will be returned.
     */
    int getColumnIndex(String columnName);


}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Table.java,v $
Revision 1.4  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
