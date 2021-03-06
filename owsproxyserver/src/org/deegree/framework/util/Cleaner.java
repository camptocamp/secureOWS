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

import java.util.Timer;
import java.util.TimerTask;


/**
 * Simple class which performs an action in continuing intervals. per default
 * the garbage collector will be called. But extending classes can change this
 * behavior by overwriting the run() method.
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class Cleaner extends TimerTask {
    
    /** 
     * Creates a new instance of Cleaner 
     *
     * @param interval milliseconds the run-method will be continuing called
     */
    public Cleaner( int interval ) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( this, 60000, interval );
    }

    /**
     * the run mehtod will called after the interval (milli seconds) passed
     * to the constructor. An extending class can overwrite this method to
     * perform something else then the default action. Per default the garbage
     * collector will be called --> System.gc();
     */
    public void run() {
        System.gc();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Cleaner.java,v $
Revision 1.5  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
