package org.deegree.security.drm;

/**
 * Marks that the requested operation failed, because of technical issues or
 * insufficient rights.
 * <p>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.2 $
 */
public class ManagementException extends Exception {

	public ManagementException (Throwable t) {
		super (t);
	}

	public ManagementException (String msg) {
		super (msg);
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ManagementException.java,v $
Revision 1.2  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
