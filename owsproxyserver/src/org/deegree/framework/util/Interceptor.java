/*
 * Created on 21.06.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.deegree.framework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @see <a href="http://www.dofactory.com/patterns/PatternChain.aspx">Chain of
 *      Responsibility Design Pattern </a>
 * 
 * @since 2.0
 */
public abstract class Interceptor {

    protected static final ILogger LOG = LoggerFactory
    .getLogger(Interceptor.class);
    
    protected abstract Object handleInvocation(Method method, Object[] params)
            throws IllegalAccessException, InvocationTargetException;

    /**
     * 
     * @uml.property name="nextInterceptor"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    protected Interceptor nextInterceptor;

    protected Object getTarget() {
        return this.nextInterceptor.getTarget();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Interceptor.java,v $
Revision 1.4  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
