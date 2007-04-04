package org.deegree.security.drm.model;

import org.deegree.security.drm.SecurityRegistry;

/**
 * Implementation of application specific objects that are securable.
 *
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.4 $
 */
public class SecuredObject extends SecurableObject {

	/**
	 * Creates a new <code>SecuredObject</code>-instance.
	 *
	 * @param id
	 * @param type
	 * @param name
	 * @param title
	 * @param registry
	 */
	public SecuredObject (int id, int type, String name, String title,
		SecurityRegistry registry) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.title = title;
		this.registry = registry;
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SecuredObject.java,v $
Revision 1.4  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
