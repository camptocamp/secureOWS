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

package org.deegree.io.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Replaces inner class Reloader in AbstractOGCServlet.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @see org.deegree.enterprise.AbstractOGCServlet
 * @see java.io.File#lastModified
 * @see <a href="http://www.javaworld.com/javaworld/javatips/jw-javatip125.html">Java
 *      Tip 125 </a>
 * 
 * @since 2.0
 */

public class FileMonitor {

    private static final FileMonitor instance = new FileMonitor();

    private Timer timer;

    private Hashtable timerEntries;

    /**
     * Factory method to get singleton instance.
     * 
     * @return
     * 
     */
    public static FileMonitor getInstance() {
        return instance;
    }

    protected FileMonitor() {
        // Create timer, run timer thread as daemon.
        timer = new Timer( true );
        timerEntries = new Hashtable();
    }

    /**
     * Add a monitored file with a FileChangeListener.
     * 
     * @param listener
     *            listener to notify when the file changed.
     * @param fileName
     *            name of the file to monitor.
     * @param period
     *            polling period in milliseconds.
     */
    public void addFileChangeListener( FileChangeListener listener, String fileName, long period )
                            throws FileNotFoundException {
        removeFileChangeListener( listener, fileName );
        FileMonitorTask task = new FileMonitorTask( listener, fileName );
        timerEntries.put( fileName + listener.hashCode(), task );
        timer.schedule( task, period, period );
    }

    /**
     * Remove the listener from the notification list.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeFileChangeListener( FileChangeListener listener, String fileName ) {
        FileMonitorTask task = (FileMonitorTask) timerEntries.remove( fileName
                                                                      + listener.hashCode() );
        if ( task != null ) {
            task.cancel();
        }
    }

    protected void fireFileChangeEvent( FileChangeListener listener, String fileName ) {
        listener.fileChanged( fileName );
    }

    class FileMonitorTask extends TimerTask {

        FileChangeListener listener;

        String fileName;

        File monitoredFile;

        long lastModified;

        public FileMonitorTask( FileChangeListener listener, String fileName )
                                throws FileNotFoundException {
            this.listener = listener;
            this.fileName = fileName;
            this.lastModified = 0;

            monitoredFile = new File( fileName );
            if ( !monitoredFile.exists() ) { // but is it on CLASSPATH?
                URL fileURL = listener.getClass().getClassLoader().getResource( fileName );
                if ( fileURL != null ) {
                    monitoredFile = new File( fileURL.getFile() );
                } else {
                    throw new FileNotFoundException( "File Not Found: " + fileName );
                }
            }
            this.lastModified = monitoredFile.lastModified();
        }

        public void run() {
            long lastModified = monitoredFile.lastModified();
            if ( lastModified != this.lastModified ) {
                this.lastModified = lastModified;
                fireFileChangeEvent( this.listener, this.fileName );
            }
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FileMonitor.java,v $
Revision 1.7  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
