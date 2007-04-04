// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/concurrent/ExecutionFinishedListener.java,v 1.3 2006/08/08 09:56:44 mschneider Exp $
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

/**
 * Listener interface for sending a notification that the asynchronous execution of a
 * task has finished (successfully or abnormally).
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 1.3 $, $Date: 2006/08/08 09:56:44 $
 * @param <T> type of return value
 */
public interface ExecutionFinishedListener<T> {

    /**
     * Called after an asynchronous task has finished. 
     * 
     * @param finishedEvent
     *            event representing the state of the finished task
     */
    void executionFinished( ExecutionFinishedEvent<T> finishedEvent );
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ExecutionFinishedListener.java,v $
 Revision 1.3  2006/08/08 09:56:44  mschneider
 Added generics for type safety - constrained the return type of tasks.

 Revision 1.2  2006/08/07 13:51:32  mschneider
 Removed usage of Reflections. Added timeout and failure handling.

 Revision 1.1  2006/07/29 08:50:00  poth
 initial check in

 ********************************************************************** */