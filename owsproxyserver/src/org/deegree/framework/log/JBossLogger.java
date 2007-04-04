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
package org.deegree.framework.log;

/**
 * Log service provided for JBoss log service.
 * To configure the log system use the JBoss log configuration file.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/09/12 12:49:47 $
 * 
 * @see <a href="http://www.jboss.org/developers/guides/logging">JBoss logging
 *      </a>
 * 
 * @since 2.0
 */
final class JBossLogger extends LoggerService {

    private org.jboss.logging.Logger log;

    /**
     * Factory method to retrieve the instance of the Server.
     *
     * @returns an instance of LogServer
     */
    JBossLogger() {
        super();
    }

    public void bindClass(String name) {
        this.log = org.jboss.logging.Logger.getLogger(name);
    }

    public void bindClass(Class name) {
        this.log = org.jboss.logging.Logger.getLogger(name);
    }

    public void logError(String message) {
        this.log.error(message);
    }

    public void logError(String message, Throwable ex) {
        this.log.error(message, ex);
    }

    @Override
    public void logWarning(String message) {
        this.log.warn(message);
    }

    @Override
    public void logWarning(String message, Throwable ex) {
        this.log.warn(message, ex);
    }
    
    @Override
    public void logInfo(String message) {
        if (this.log.isInfoEnabled()) {
            this.log.info(message);
        }
    }

    @Override
    public void logInfo(String message, Throwable ex) {
        if (this.log.isInfoEnabled()) {
            this.log.info(message, ex);
        }
    }

    public void logInfo(String message, Object tracableObject) {
        if (this.log.isInfoEnabled()) {
            this.log.info(message + tracableObject);
        }
    }

    @Override
    public void logDebug(String message) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(message);
        }
    }

    @Override
    public void logDebug(String message, Throwable ex) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(message, ex);
        }
    }

    public void logDebug(String message, Object tracableObject) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(message + tracableObject);
        }
    }

    public void log(int priority, String message, Throwable ex) {
        this.logDebug(message, ex);
    }

    public void log(int priority, String message, Object source, Throwable ex) {
        this.logDebug(message + source.toString(), ex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree_framework.log.ILogger#getLevel()
     */
    public int getLevel() {
        int logLevel;
        if (this.log.isDebugEnabled()) {
            logLevel = LOG_DEBUG;
        } else if (this.log.isInfoEnabled()) {
            logLevel = LOG_INFO;
        } else if (this.log.isTraceEnabled()) {
            logLevel = LOG_DEBUG;
        } else {
            logLevel = LOG_WARNING;
        }
        return logLevel;
    }

    /**
     * Set the log level.
     *  
     * @see org.deegree.framework.log.ILogger#setLevel(int)
     * @BUG Currently not supported by JBoss Logging.
     */
    public void setLevel(int level) {
        // not supported API
    }
    
    public boolean isDebug() {
        return log.isDebugEnabled();
    }

    @Override
    public String toString() {
        return ("Logging Class: " + this.log.getClass().getName());
    }
}

/*
 * *****************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * JBossLogger.java,v $ Revision 1.2 2004/06/15 14:58:19 tf refactored Logger
 * and add new methods
 * 
 * Revision 1.1 2004/05/14 15:26:38 tf initial checkin
 * 
 * 
 * ***************************************************************************
 */

