
package org.deegree.enterprise.control;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author  Administrator
 */

public class RPCWebEvent extends WebEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     * @uml.property name="mc"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private RPCMethodCall mc = null;

    
    /** Creates a new instance of RPCWebEvent */
    public RPCWebEvent(HttpServletRequest request) {
        super( request );
    }
    
    /** Creates a new instance of RPCWebEvent */
    public RPCWebEvent(HttpServletRequest request, RPCMethodCall mc) {
        super( request );
        this.mc = mc;
    }
    
    /** Creates a new instance of RPCWebEvent */
    public RPCWebEvent(FormEvent parent, RPCMethodCall mc) {
        super( (HttpServletRequest)parent.getSource() );
        this.mc = mc;
    }
    
    /**
     * returns the the RPC methodcall extracted from the <tt>HttpServletRequest</tt>
     * passed to the first constructor.
     */
    public RPCMethodCall getRPCMethodCall() {
        if ( mc == null ) {
            try {
                mc = getMethodCall( (ServletRequest)this.getSource() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mc;
    }
    
    /**
     * extracts the RPC method call from the 
     * @param request
     * @throws RPCException
     */
    private RPCMethodCall getMethodCall(ServletRequest request ) throws RPCException {
        
        StringBuffer sb = new StringBuffer(1000);
        try {
            BufferedReader br = request.getReader();
            String line = null;
            while ( (line = br.readLine() ) != null ) {
                sb.append( line );
            }
            br.close();
        } catch (Exception e) {
            throw new RPCException( "Error reading stream from servlet\n" + e.toString() );
        }
        
        String s = sb.toString();
        int pos1 = s.indexOf( "<methodCall>" );
        int pos2 = s.indexOf( "</methodCall>" );
        if ( pos1 < 0 ) {
            throw new RPCException( "request doesn't contain a RPC methodCall" );
        }
        s = s.substring( pos1, pos2 + 13 );
        
        StringReader reader = new StringReader( s );
        RPCMethodCall mc = RPCFactory.createRPCMethodCall( reader );
        
        return mc;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RPCWebEvent.java,v $
Revision 1.5  2006/07/29 08:49:25  poth
references to deprecated classes removed

Revision 1.4  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
