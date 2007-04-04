/*
 * CatalogClientException.java
 *
 * Created on 6. November 2003, 22:15
 */

package org.deegree.portal.context;

import org.deegree.framework.util.StringTools;



/**
 *
 * @author  Administrator
 */
public class ContextException extends java.lang.Exception {
        
    private String st = "";
    
    /**
     * Creates a new instance of <code>CatalogClientException</code> without detail message.
     */
    public ContextException() {
    	st = "ContextException";
    }
    
     
    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ContextException(String msg) {
        super( msg );
    }
       
    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ContextException(String msg, Exception e) {
        this( msg );
        st = StringTools.stackTraceToString( e.getStackTrace() );
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString() + "\n" + st;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ContextException.java,v $
Revision 1.3  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
