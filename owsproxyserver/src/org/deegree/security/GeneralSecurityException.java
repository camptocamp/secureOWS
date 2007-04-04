package org.deegree.security;

import org.deegree.framework.util.StringTools;



/**
 * Marks that the requested operation failed, because of technical issues in the 
 * security subsystem or because of insufficient rights.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.3 $
 */
public class GeneralSecurityException extends Exception {
   
    String message = null;
    
    public GeneralSecurityException () {}
    
	public GeneralSecurityException (Throwable t) {
		super (t);
	}

	public GeneralSecurityException (String msg) {
		super (msg);
	}

    public GeneralSecurityException (String message, Throwable arg1) {        
        super(message, arg1);
        this.message = message + StringTools.stackTraceToString(arg1);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeneralSecurityException.java,v $
Revision 1.3  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
