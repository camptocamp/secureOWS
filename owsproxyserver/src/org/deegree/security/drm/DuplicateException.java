package org.deegree.security.drm;

import org.deegree.security.GeneralSecurityException;

/**
 * Marks that the requested operation failed, because an entity with
 * the same name already existed.
 *
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.3 $
 */
public class DuplicateException extends GeneralSecurityException {

	public DuplicateException (String msg) {
		super (msg);
	}
}
 /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DuplicateException.java,v $
Revision 1.3  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
