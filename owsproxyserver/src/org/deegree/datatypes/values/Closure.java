package org.deegree.datatypes.values;

import java.io.Serializable;

/**
 * 
 * 
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/04/09 12:20:40 $
 * 
 * @since 2.0
 */
public class Closure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Comment for <code>CLOSED</code>
	 */
	public static final String CLOSED = "closed";

	/**
	 * Comment for <code>OPENED</code>
	 */
	public static final String OPENED = "open";

	/**
	 * Comment for <code>OPENED-CLOSED</code>
	 */
	public static final String OPENED_CLOSED = "open-closed";

	/**
	 * Comment for <code>CLOSED-OPENED</code>
	 */
	public static final String CLOSED_OPENED = "closed-open";

	/**
	 * Comment for <code>value</code>
	 */
	public String value = CLOSED;

	/**
	 * default = CLOSED
	 */
	public Closure() {
	}

	/**
	 * @param value
	 */
	public Closure( String value ) {
		this.value = value;
	}

	/**
	 * Compares the specified object with this enum for equality.
	 */
	public boolean equals( Object object ) {
		if ( object != null && getClass().equals( object.getClass() ) ) {
			return ( ( Closure ) object ).value.equals( value );
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final long longCode = value.hashCode();
		return ( ( ( int ) ( longCode >>> 32 ) ) ^ ( int ) longCode ) + 37 * super.hashCode();
	}

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log: Closure.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/04/09 12:20:40  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.2 2005/11/16 13:45:00 mschneider Merge of wfs development branch.
 * 
 * Revision 1.1.1.1.2.1 2005/11/14 11:34:50 deshmukh inserted: serialVersionID
 * 
 * Revision 1.1.1.1 2005/01/05 10:39:04 poth no message
 * 
 * Revision 1.4 2004/08/16 06:23:33 ap no message
 * 
 * Revision 1.3 2004/07/21 06:16:00 ap no message
 * 
 * Revision 1.2 2004/07/09 07:01:33 ap no message
 * 
 * Revision 1.1 2004/05/25 12:55:02 ap no message
 * 
 * 
 ******************************************************************************/
