package org.deegree.security.drm.model;

import org.deegree.security.drm.SecurityRegistry;

/**
 * Abstract superclass of objects that are securable, i.e. which carry * information about which <code>Role</code>s have which <code>Right</code>s * concerning these objects. *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.3 $
 */

public abstract class SecurableObject {

	protected int id;
	protected int type;
	protected String name;
	protected String title;
    protected SecurityRegistry registry;


	/**
	 * Returns the unique identifier of this <code>SecurableObject</code>.
	 */
	public int getID () {
		return id;
	}

    /**
     * Returns the type of this <code>SecurableObject</code>.
     * <p>
     * NOTE: Unique in conjunction with name field.
     * 
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the name of this <code>SecurableObject</code>.
     * <p>
     * NOTE: Unique in conjunction with type field.
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the human readable name of this <code>SecurableObject</code>.
     * <p>
     * NOTE: This may not be unique.
     * 
     */
    public String getTitle() {
        return title;
    }

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param that
	 */	
	public boolean equals (Object that) {
		if (that instanceof SecurableObject) {
			return (((SecurableObject) that).getID () == getID ());
		}
		return false;
	}

	/**
	 * Returns a hash code value for the object. This method is supported
	 * for the benefit of hashtables such as those provided by
	 * java.util.Hashtable.
	 */		
	public int hashCode () {
		return id;
	}	

	/**
	 * Returns a <code>String</code> representation of this object.
	 */
	public String toString () {
		StringBuffer sb = new StringBuffer ("Id: ").
			append (id).append (", Name: ").append (name);
		return sb.toString ();
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SecurableObject.java,v $
Revision 1.3  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
