package org.deegree.security.drm;

import org.deegree.security.GeneralSecurityException;

/**
 * Marks that the entered credentials (password) are wrong.
 * <p>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.3 $
 */
public class WrongCredentialsException extends GeneralSecurityException {

	public WrongCredentialsException (String msg) {
		super (msg);
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WrongCredentialsException.java,v $
Revision 1.3  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
