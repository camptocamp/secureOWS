/*
 * Created on 20.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.deegree.framework.jndi;

/**
 * ServiceLocator *  * @author Administrator *  * @author last edited by: $Author: poth $ *  * @version 2.0, $Revision: 1.3 $, $Date: 2006/07/12 14:46:19 $ *   * @since 2.0 *  * @see <a href="http://java.sun.com/blueprints/corej2eepatterns/Patterns/ServiceLocator.html">CapabilitiesService Locator</a>
 */

public final class ServiceLocator {

    /**
     * 
     * @uml.property name="singleton"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private static final ServiceLocator singleton = new ServiceLocator();

    /**
     * @return
     */
    public static final ServiceLocator getInstance() {
        return singleton;
    }
    
    /**
     * 
     */
    private ServiceLocator(){
        
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServiceLocator.java,v $
Revision 1.3  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
