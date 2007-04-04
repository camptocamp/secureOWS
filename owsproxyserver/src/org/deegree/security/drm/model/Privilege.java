package org.deegree.security.drm.model;


/**
 * Default implementation of privilege-objects. *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.4 $
 */

public class Privilege {

    // predefined privileges
    public static final Privilege WRITE = new Privilege(1, "write");
    public static final Privilege ADDUSER = new Privilege(2, "adduser");
    public static final Privilege ADDGROUP = new Privilege(3, "addgroup");
    public static final Privilege ADDROLE = new Privilege(4, "addrole");
    public static final Privilege ADDOBJECT = new Privilege(5, "addobject");


	private int id;
	private String name;
  
	/**
	 * Creates a new <code>Privilege</code>-instance.
	 * 
	 * @param id
	 * @param name
	 */
	public Privilege (int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the unique identifier of this privilege.
	 */
	public int getID () {
		return id;
	}

    /**
     * Returns the name of this privilege.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

	

	/**
	 * Indicates whether some other privilege is "equal to" this one.
	 *
	 * @param that
	 */	
	public boolean equals (Object that) {
		if (that instanceof Privilege) {
			return (((Privilege) that).getID () == getID ());
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
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Privilege.java,v $
Revision 1.4  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
