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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;


/**
 * class to manage the object pool. this is part
 * of the combination of the object pool pattern an the singelton
 * pattern.
 *
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version 07.02.2001
 *
 */
public abstract class ObjectPool extends TimerTask {
    
    private static final ILogger LOG = LoggerFactory.getLogger( ObjectPool.class );
    
    protected List available = null;
    protected List in_use = null;
    protected Map startLifeTime = null;
    protected Map startUsageTime = null;
    protected int existingInstances = 0;
    private int maxInstances = 50;
    // min * sec * millisec. example: 5*60*1000 = 5 minutes
    private int maxLifeTime = 15 * 60 * 1000;
    // min * sec * millisec. example: 5*60*1000 = 5 minutes
    private int maxUsageTime = 5 * 60 * 1000;
    // milliseconds
    private int updateInterval = 15000;

    /**
     * Creates a new ObjectPool object.
     */
    protected ObjectPool() {
        available = Collections.synchronizedList( new ArrayList() );
        in_use = Collections.synchronizedList( new ArrayList() );
        startLifeTime = Collections.synchronizedMap( new HashMap() );
        startUsageTime = Collections.synchronizedMap( new HashMap() );
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( this, 10000, updateInterval );
    }

    /**
     * dummy
     *
     * @return null
     */
    public static ObjectPool getInstance() {
        return null;
    }


    /**
     * clears the complete pool. objects in used while the clear() method has 
     * been called won't be put back to the pool if released back through the
     * <tt>releaseObject</tt> method.
     */
    public void clear() {
        synchronized ( in_use ) {
            synchronized ( available ) {
                synchronized ( startUsageTime ) {
                    synchronized ( startLifeTime ) {
                        in_use.clear();
                        available.clear();
                        startUsageTime.clear();
                        startLifeTime.clear();
                    }
                }
            }
        }
    }

    /**
     * release an object back to the pool so it is available for
     * other requests.
     */
    public void releaseObject( Object object ) throws Exception {

        if ( in_use.contains( object ) ) {           
            // remove the object from the 'in use' container
            in_use.remove( object );
            // remove the objects entry from the 'usage star time' container
            startUsageTime.remove( object );
             // push the object to the list of available objects
            available.add( object );
        }

    }

    /**
     * this method will be called when the submitted object
     * will be removed from the pool
     */
    public abstract void onObjectKill( Object o );

    /**
     * @return
     */
    public int getMaxLifeTime() {
        return (this.maxLifeTime);
    }

    /**
     * @param maxLifeTime
     */
    public void setMaxLifeTime(int maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
    }

    /**
     * @return
     */
    public int getMaxUsageTime() {
        return (this.maxUsageTime);
    }

    /**
     * @param maxUsageTime
     */
    public void setMaxUsageTime(int maxUsageTime) {
        this.maxUsageTime = maxUsageTime;
    }

    /**
     * @return
     */
    public int getUpdateInterval() {
        return (this.updateInterval);
    }

    /**
     * @param updateInterval
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * @return
     */
    public int getMaxInstances() {
        return (this.maxInstances);
    }

    /**
     * @param maxInstances
     */
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = getClass().getName() + "\n";
        ret = "startLifeTime = " + startLifeTime + "\n";
        ret += ( "startUsageTime = " + startUsageTime + "\n" );
        ret += ( "maxLifeTime = " + maxLifeTime + "\n" );
        ret += ( "maxUsageTime = " + maxUsageTime + "\n" );
        ret += ( "updateInterval = " + updateInterval + "\n" );
        ret += ( "maxInstances = " + maxInstances + "\n" );
        return ret;
    }
    
    public void run() {
        cleaner();
        usage();
    }
    
    private void cleaner() {
    
        try {
            synchronized ( available ) {
                synchronized ( startLifeTime ) {
                    Object[] os = available.toArray();
                    for (int i = 0; i < os.length; i++ ) {
                        Object o = os[i];
                        Long lng = (Long)startLifeTime.get( o );
                        long l = System.currentTimeMillis();
                        if ( ( l - lng.longValue() ) > maxLifeTime ) {
                            available.remove( o );
                            startLifeTime.remove( o );
                            onObjectKill( o );
                            existingInstances--;
                        }
                    }
                }
            }

        } catch ( Exception e ) {
            LOG.logError( "ObjectPool Cleaner ", e );
        }

    }
    
    private void usage() {
        try {
            synchronized ( in_use ) {
                synchronized ( startUsageTime ) {
                    synchronized ( startLifeTime ) {
                    	Object[] os = in_use.toArray();
                    	for (int i = 0; i < os.length; i++ ) {
                    		Object o = os[i];
                            Long lng = (Long)startUsageTime.get( o );
                            long l = System.currentTimeMillis();

                            if ( ( l - lng.longValue() ) > maxUsageTime ) {
                                in_use.remove( o );
                                startUsageTime.remove( o );
                                startLifeTime.remove( o );
                                onObjectKill( o );
                            }
                        }
                    }
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "UsageChecker ", e );
        }
       
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ObjectPool.java,v $
Revision 1.10  2006/07/29 08:50:23  poth
references to deprecated classes removed

Revision 1.9  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
