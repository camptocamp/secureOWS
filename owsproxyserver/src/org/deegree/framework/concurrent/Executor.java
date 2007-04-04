// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/concurrent/Executor.java,v 1.8 2006/10/17 13:13:13 schmitz Exp $
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The <code>Executor</code> is deegree's central place to:
 * <ul>
 * <li>Perform a task asynchronously (in an independent thread) optionally with a maximum execution
 * time.</li>
 * <li>Perform a task synchronously with a maximum execution time.</li>
 * <li>Perform several task synchronously (but in parallel threads) with a maximum execution time.</li>
 * </ul>
 * <p>
 * The <code>Executor</code> class is realized as a singleton and uses a cached thread pool
 * internally to minimize overhead for acquiring the necessary {@link Thread} instances and to
 * manage the number of concurrent threads.
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/10/17 13:13:13 $
 * @see java.util.concurrent.ExecutorService
 * @see org.deegree.framework.concurrent.ExecutionFinishedListener
 */
public class Executor {

    private static Executor exec;

    private ExecutorService execService;

    /**
     * Private constructor required for singleton pattern.
     */
    private Executor() {
        this.execService = Executors.newCachedThreadPool();
    }

    /**
     * Returns the only instance of this class (singleton pattern).
     * 
     * @return the only instance of this class
     */
    public synchronized static Executor getInstance() {

        if ( exec == null ) {
            exec = new Executor();
        }
        return exec;
    }

    /**
     * Performs a task asynchronously (in an independent thread) without any time limit.
     * 
     * @param <T> 
     *            type of return value
     * @param task
     *            task to be performed (specified in the {@link Callable#call()} method)
     * @param finishedListener
     *            notified when the method call finishes (succesfully or abnormally), may be null
     */
    public <T> void performAsynchronously( Callable<T> task,
                                           ExecutionFinishedListener<T> finishedListener ) {

        AsyncPerformer runner = new AsyncPerformer<T>( task, finishedListener );
        this.execService.execute( runner );
    }

    /**
     * Performs a task asynchronously (in an independent thread) with a given time limit.
     * 
     * @param <T> 
     *            type of return value
     * @param task
     *            task to be performed (specified in the {@link Callable#call()}} method)
     * @param finishedListener
     *            notified when the method call finishes (succesfully or abnormally), may be null
     * @param timeout
     *            maximum time allowed for execution in milliseconds
     */
    public <T> void performAsynchronously( Callable<T> task,
                                           ExecutionFinishedListener<T> finishedListener,
                                           long timeout ) {

        AsyncPerformer runner = new AsyncPerformer<T>( task, finishedListener, timeout );
        this.execService.execute( runner );
    }

    /**
     * Performs a task synchronously with a given timeout.
     * 
     * @param <T>
     *            type of return value
     * @param task
     *            tasks to be performed (specified in the {@link Callable#call()} method)
     * @param timeout
     *            maximum time allowed for execution in milliseconds
     * @return result value of the called method
     * @throws CancellationException
     *             if the execution time exceeds the specified timeout / thread has been cancelled
     * @throws InterruptedException 
     *             if interrupted while waiting, in which case unfinished tasks are cancelled
     * @throws Throwable
     *             if the tasks throws an exception itself
     */
    public <T> T performSynchronously( Callable<T> task, long timeout )
                            throws CancellationException, InterruptedException, Throwable {

        T result;
        List<Callable<T>> tasks = new ArrayList<Callable<T>>( 1 );
        tasks.add( task );

        try {
            List<Future<T>> futures = this.execService.invokeAll(  tasks, timeout, TimeUnit.MILLISECONDS );
            Future<T> future = futures.get( 0 );
            result = future.get();              
        } catch ( ExecutionException e ) {
            throw ( e.getCause() );
        } 
        return result;
    }

    /**
     * Performs several tasks synchronously in parallel threads.
     * <p>
     * This method does not return before all tasks are finished (successfully or abnormally). For
     * each given {@link Callable}, an independent thread is used. For each task, an
     * {@link ExecutionFinishedEvent} is generated and returned.
     * @param <T> the result type of the callables
     * 
     * @param tasks
     *            tasks to be performed (specified in the {@link Callable#call()} methods)
     * @return ExecutionFinishedEvents for all tasks
     * @throws InterruptedException
     *            if the current thread was interrupted while waiting
     */
    public <T> List<ExecutionFinishedEvent<T>> performSynchronously( List<Callable<T>> tasks )
                            throws InterruptedException {

        List<ExecutionFinishedEvent<T>> results = new ArrayList<ExecutionFinishedEvent<T>>( tasks.size() );
        List<Future<T>> futures = this.execService.invokeAll( tasks );

        for ( int i = 0; i < tasks.size(); i++ ) {

            ExecutionFinishedEvent<T> finishedEvent = null;
            Callable<T> task = tasks.get( i );
            Future<T> future = futures.get( i );

            try {
                T result = future.get();
                finishedEvent = new ExecutionFinishedEvent<T>( task, result );
            } catch ( ExecutionException e ) {
                finishedEvent = new ExecutionFinishedEvent<T>( e.getCause(), task );
            } catch ( CancellationException e ) {
                finishedEvent = new ExecutionFinishedEvent<T>( e, task );
            }
            results.add( finishedEvent );
        }
        return results;
    }

    /**
     * Performs several tasks synchronously with a given timeout in parallel threads.
     * <p>
     * This method does not return before all tasks are finished (successfully or abnormally). For
     * each given {@link Callable}, an independent thread is used. For each task, an
     * {@link ExecutionFinishedEvent} is generated and returned.
     * @param <T> the result type of the tasks
     * 
     * @param tasks
     *            tasks to be performed (specified in the {@link Callable#call()} methods)
     * @param timeout
     *            maximum time allowed for execution (in milliseconds)
     * @return ExecutionFinishedEvents for all tasks
     * @throws InterruptedException
     *            if the current thread was interrupted while waiting
     */
    public <T> List<ExecutionFinishedEvent<T>> performSynchronously( List<Callable<T>> tasks,
                                                              long timeout )
                            throws InterruptedException {

        List<ExecutionFinishedEvent<T>> results = new ArrayList<ExecutionFinishedEvent<T>>( tasks.size() );
        List<Future<T>> futures = this.execService.invokeAll( tasks, timeout,
                                                                   TimeUnit.MILLISECONDS );

        for ( int i = 0; i < tasks.size(); i++ ) {

            ExecutionFinishedEvent<T> finishedEvent = null;
            Callable<T> task = tasks.get( i );
            Future<T> future = futures.get( i );

            try {
                T result = future.get();
                finishedEvent = new ExecutionFinishedEvent<T>( task, result );
            } catch ( ExecutionException e ) {
                finishedEvent = new ExecutionFinishedEvent<T>( e.getCause(), task );
            } catch ( CancellationException e ) {
                finishedEvent = new ExecutionFinishedEvent<T>( e, task );
            }
            results.add( finishedEvent );
        }
        return results;
    }

    // ///////////////////////////////////////////////////////////////////////////
    //                           inner classes                                  //
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Inner class for performing task asynchronously.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
     * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
     * @author last edited by: $Author: schmitz $
     * @version $Revision: 1.8 $
     */
    private class AsyncPerformer<T> implements Runnable {

        private Callable<T> task;

        private ExecutionFinishedListener<T> finishedListener;

        private long timeout = -1;

        private AsyncPerformer( Callable<T> task, ExecutionFinishedListener<T> finishedListener ) {
            this.task = task;
            this.finishedListener = finishedListener;
        }

        private AsyncPerformer( Callable<T> task, ExecutionFinishedListener<T> finishedListener,
                                long timeout ) {
            this.task = task;
            this.finishedListener = finishedListener;
            this.timeout = timeout;
        }

        /**
         * Performs the task using {@link Executor#performSynchronously(Callable, long)}.
         *
         * @see java.lang.Runnable#run()
         */
        public void run() {
            ExecutionFinishedEvent<T> finishedEvent = null;
            try {
                T result = null;
                if ( this.timeout < 0 ) {
                    result = this.task.call();
                } else {
                    result = Executor.getInstance().performSynchronously( task, timeout );
                }
                finishedEvent = new ExecutionFinishedEvent<T>( this.task, result );
            } catch ( Throwable t ) {
                finishedEvent = new ExecutionFinishedEvent<T>( t, this.task );
            }
            if ( this.finishedListener != null ) {
                this.finishedListener.executionFinished( finishedEvent );
            }
        }
    }
}

/* *************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: Executor.java,v $
 * Revision 1.8  2006/10/17 13:13:13  schmitz
 * Added the last missing type parameters.
 *
 * Revision 1.7  2006/10/08 18:29:05  poth
 * bug fix - method public <T> T performSynchronously( Callable<T> task, long timeout ) now considers max execution time as it should
 *
 * Revision 1.6  2006/08/10 13:29:30  mschneider
 * #performSynchronously( List<Callable<Object>>) and #performSynchronously( List<Callable<Object>>, long) return a List of ExecutionFinishedEvents now. Timeout or cancellation is now indicated by a CancellationException everywhere (for consistency and ease of use).
 *
 * Revision 1.5  2006/08/08 10:03:36  mschneider
 * Fixed comment footer.
 *
 * Revision 1.4  2006/08/08 09:56:44  mschneider
 * Added generics for type safety - constrained the return type of tasks.
 * 
 * Revision 1.3  2006/08/07 19:50:06  poth
 * bug fix - -> performSynchronously( Callable task, long timeout )
 * 
 * Revision 1.2  2006/08/07 13:51:32  mschneider
 * Removed usage of Reflections. Added timeout and failure handling.
 *
 * Revision 1.1 2006/07/29 08:50:00 poth initial check in
 **************************************************************************************************/