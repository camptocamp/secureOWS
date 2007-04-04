// $Id: ProfilerInterceptor.java,v 1.6 2006/07/12 14:46:17 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------
 
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
 Contact:
 
 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53115 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 
 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 
 ---------------------------------------------------------------------------*/

package org.deegree.framework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Interceptor to profile the application.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @see <a href="http://www.dofactory.com/patterns/PatternChain.aspx">Chain of
 *      Responsibility Design Pattern </a>
 * 
 * @since 2.0
 */
public class ProfilerInterceptor extends Interceptor {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                printStats();
            }
        }));
    }

    protected static HashMap data = new HashMap();

    public ProfilerInterceptor(Interceptor nextInterceptor) {
        this.nextInterceptor = nextInterceptor;
    }

    protected static void printStats() {
        NumberFormat zweiDezimalen = new DecimalFormat("### ###.##");

        Iterator iter = data.keySet().iterator();
        while (iter.hasNext()) {
            String m = (String) iter.next();
            ProfileEntry entry = (ProfileEntry) data.get(m);
            LOG.logInfo(entry.invocations + " calls " + entry.time
                    + " ms " + " avg "
                    + zweiDezimalen.format(entry.getAverage()) + m);
        }
    }

    protected static ProfileEntry getEntry(String m) {
        if (data.containsKey(m)) { return (ProfileEntry) data.get(m); }
        ProfileEntry entry = new ProfileEntry();
        data.put(m, entry);
        return entry;
    }

    protected Object handleInvocation(Method method, Object[] params)
            throws IllegalAccessException, InvocationTargetException {
        long start = System.currentTimeMillis();
        Object result = nextInterceptor.handleInvocation(method, params);
        long duration = System.currentTimeMillis() - start;
        ProfileEntry entry = getEntry(getTarget().getClass().getName() + "."
                + method.getName());
        entry.time += duration;
        entry.invocations++;
        return result;
    }

    static class ProfileEntry {

        long time;

        long invocations;

        public double getAverage() {
            return (double) time / (double) invocations;
        }

    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ProfilerInterceptor.java,v $
Revision 1.6  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
