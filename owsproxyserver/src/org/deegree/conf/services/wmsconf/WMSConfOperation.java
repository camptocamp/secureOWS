/*
 * Created on 28.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.deegree.conf.services.wmsconf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sncho
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WMSConfOperation {

    protected WMSConfConfiguration config;
    
    public abstract String performOperation( HttpServletRequest request, 
                                             HttpServletResponse response ) 
    	throws Exception;

    public abstract String getOperationName();

    
    /**
     * @param config
     */
    public void setConfiguration( WMSConfConfiguration config ){
        if ( config == null ){
            throw new NullPointerException( "config cannot be null." );
        }
        this.config = config;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSConfOperation.java,v $
Revision 1.6  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.5  2005/12/12 09:39:49  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.4  2005/12/07 16:06:46  taddei
styling

Revision 1.3  2005/12/07 14:48:26  taddei
added abstract perform method

Revision 1.2  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */