package org.deegree.security;

import org.deegree.framework.util.StringTools;

/**
 * Marks that the requested operation is not permitted.
 * <p>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.4 $
 */
public class UnauthorizedException extends GeneralSecurityException {
    
    String message = null;
	
    /**
     * 
     */
    public UnauthorizedException() {
        super();
    }
    
    /**
     * @param arg0
     */
    public UnauthorizedException(String message) {
        this.message = message;
    }
    
    /**
     * @param arg0
     * @param arg1
     */
    public UnauthorizedException(String message, Throwable arg1) {        
        super(message, arg1);
        this.message = message + StringTools.stackTraceToString(arg1);
    }
    
    /**
     * @param arg0
     */
    public UnauthorizedException(Throwable arg0) {
        super( StringTools.stackTraceToString(arg0) );
    }
    
    public String getMessage() {
        return message;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: UnauthorizedException.java,v $
Revision 1.4  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
