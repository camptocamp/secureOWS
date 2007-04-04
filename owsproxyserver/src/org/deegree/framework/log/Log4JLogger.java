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
package org.deegree.framework.log;

import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

/**
 * Log service provided by log4j. The log environment is fully configurable
 * using a configuration file. The configuration file name is
 * <code>log4j.properties</code>. The default location of the log file is the
 * system property <code>user.home</code>.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0 . $Revision: 1.11 $, $Date: 2006/09/12 12:49:47 $
 * 
 * @see <a href="http://jakarta.apache.org/log4j">Log4J home </a>
 * 
 * @since 2.0
 */
final class Log4JLogger extends LoggerService {

    private static String PROP_FILE = "log4j.properties";

    private Logger log;

    Log4JLogger() {
        super();
        this.bindClass(Log4JLogger.class);

        URL urlToLog4jProps = Log4JLogger.class.getResource("/"+PROP_FILE);
        if (urlToLog4jProps == null) {
            urlToLog4jProps = Log4JLogger.class.getResource(PROP_FILE);
        }
        if (urlToLog4jProps != null) {
            PropertyConfigurator.configure(urlToLog4jProps);
            this.logDebug("Log4J: found log4j.properties, initialized the Logger with configuration found in file " + urlToLog4jProps);
        } else {
            // Set up a simple configuration that logs on the console.
            BasicConfigurator.configure();
            this.logDebug("Log4J: No log4j.properties found, initialized Log4J with a BasicConfiguration.");
        }
    }

    public void bindClass(String name) {
        log = Logger.getLogger(name);
    }

    public void bindClass(Class clazz) {
        this.bindClass(clazz.getName());
    }

    @Override
    public void logDebug(String message, Throwable e) {
        log.debug(message, e);
    }

    @Override
    public void logInfo(String message, Throwable e) {
        log.info(message, e);
    }

    @Override
    public void logWarning(String message, Throwable e) {
        log.warn(message, e);
    }

    public void logError(String message, Throwable e) {
        log.error(message, e);
    }

    @Override
    public void logDebug(String message) {
        log.debug(message);
    }

    @Override
    public void logInfo(String message) {
        log.info(message);
    }

    @Override
    public void logWarning(String message) {
        log.warn(message);
    }

    public void logError(String message) {
        log.error(message);
    }

    public void logInfo(String message, Object tracableObject) {
        log.info(message + ": " + tracableObject );
    }

    public void logDebug(String message, Object tracableObject) {
        log.debug(message + ": " + tracableObject );
    }

    public void log(int priority, String message, Throwable ex) {
        log.log(Level.DEBUG, message, ex);
    }

    public void log(int priority, String message, Object source, Throwable ex) {
        log.log(message, Level.DEBUG, source, ex);
    }

    @Override
    public String toString() {
        return ("Logging Class: " + this.log.getClass().getName());
    }

    public int getLevel() {
        return this.getInternalLevel(this.log.getEffectiveLevel());
    }

    public void setLevel(int level) {
        this.log.setLevel(this.getLog4JLevel(level));
    }

    private Level getLog4JLevel(int logLevel) {
        Level log4jlevel;
        switch (logLevel) {
        case ILogger.LOG_DEBUG:
            log4jlevel = Level.DEBUG;
            break;
        case ILogger.LOG_INFO:
            log4jlevel = Level.INFO;
            break;
        case ILogger.LOG_WARNING:
            log4jlevel = Level.WARN;
            break;
        case ILogger.LOG_ERROR:
            log4jlevel = Level.ERROR;
            break;
        default:
            log4jlevel = Level.INFO;
            break;
        }
        return log4jlevel;
    }

    private int getInternalLevel(Level log4JLevel) {
        int intloglevel = ILogger.LOG_INFO;
        if (log4JLevel != null) {
            switch (log4JLevel.toInt()) {
            case Priority.DEBUG_INT:
                intloglevel = ILogger.LOG_DEBUG;
                break;
            case Priority.INFO_INT:
                intloglevel = ILogger.LOG_INFO;
                break;
            case Priority.WARN_INT:
                intloglevel = ILogger.LOG_WARNING;
                break;
            case Priority.FATAL_INT:
            case Priority.ERROR_INT:
                intloglevel = ILogger.LOG_ERROR;
                break;
            default:
                intloglevel = ILogger.LOG_INFO;
                break;
            }
        }
        return intloglevel;
    }

    public boolean isDebug() {
        return log.isDebugEnabled();
    }
    
}
/* ******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * Log4JLogger.java,v $ Revision 1.3 2004/06/15 15:06:22 tf fixed bugs
 * 
 * Revision 1.2 2004/06/15 14:58:19 tf refactored Logger and add new methods
 * 
 * Revision 1.1 2004/05/14 15:26:38 tf initial checkin
 * 
 * ************************************************************************** */

