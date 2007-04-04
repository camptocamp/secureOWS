package org.deegree.model.table;

public class TableException extends Exception {
	
	private String message = "org.deegree.model.table.TableException: ";
	
	public TableException(String message)
	{
		super( message );
		this.message = this.message + message;
	}
	
	public String toString()
	{
		return message;
	}
	
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TableException.java,v $
Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
