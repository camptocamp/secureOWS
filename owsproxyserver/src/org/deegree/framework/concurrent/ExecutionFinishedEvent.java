// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/concurrent/ExecutionFinishedEvent.java,v 1.4 2006/08/10 13:26:34 mschneider Exp $
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
 53177 Bonn
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
package org.deegree.framework.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

/**
 * Event that is sent when asynchronous task finished.
 * <p>
 * This can mean:
 * <ul>
 * <li>it finished successfully</li>
 * <li>it terminated abnormally (with an exception or error)</li>
 * <li>a time out occurred during the performing of the task (or it's thread has been
 * cancelled)</li>
 * </ul>
 * </p>
 * <p>
 * If the task did not finish successfully, the thrown exception / error is rethrown when
 * {@link #getResult()} is called.
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 1.4 $, $Date: 2006/08/10 13:26:34 $
 * @param <T> type of return value
 */
public class ExecutionFinishedEvent<T> { 
    
    private Callable task;

    private T result;

    private Throwable t;   
    
    /**
     * Constructs an <code>ExecutionFinishedEvent</code> for a task that finished
     * successfully.
     * 
     * @param task
     * @param result
     */
    ExecutionFinishedEvent( Callable task, T result  ) {
        this.task = task;
        this.result = result;
    }

    /**
     * Constructs an <code>ExecutionFinishedEvent</code> for a task that terminated
     * abnormally.
     * 
     * @param t Throwable that the terminated task threw
     * @param task
     */
    ExecutionFinishedEvent( Throwable t, Callable task ) {
        this.task = task;
        this.t = t;
    }    

    /**
     * Returns the corresponding task instance.
     * 
     * @return the corresponding task instance
     */
    public Callable getTask() {
        return this.task;
    }

    /**
     * Returns the result value that the finished task returned.
     * <p>
     * If the task produced an exception or error, it is rethrown here. If the task has been
     * cancelled (usually this means that the time out occurred), a {@link CancellationException}
     * is thrown. 
     * 
     * @return the result value that the task returned
     * @throws CancellationException
     *            if task timed out / has been cancelled
     * @throws Throwable
     *            if task terminated with an exception or error
     */    
    public T getResult() throws CancellationException, Throwable {
        if (this.t != null) {
            throw t;
        }
        return this.result;
    }    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ExecutionFinishedEvent.java,v $
Revision 1.4  2006/08/10 13:26:34  mschneider
#getResult() throws CancellationException instead of TimeoutException now (for consistency). Improved documentation.

Revision 1.3  2006/08/08 09:56:44  mschneider
Added generics for type safety - constrained the return type of tasks.

Revision 1.2  2006/08/07 13:51:32  mschneider
Removed usage of Reflections. Added timeout and failure handling.

Revision 1.1  2006/07/29 08:50:00  poth
initial check in
********************************************************************** */