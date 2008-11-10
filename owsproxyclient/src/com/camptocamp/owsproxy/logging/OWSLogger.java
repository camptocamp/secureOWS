package com.camptocamp.owsproxy.logging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.mortbay.log.Log;

/**
 * This class provides methods for logging to the different loggers used by the
 * OWSClient
 * 
 * @author jeichar
 */
public enum OWSLogger {

	/**
	 * A logger for messages that only have meaning to developers.
	 */
	DEV("com.camptocamp.owsproxy.DEV"), //$NON-NLS-1$
	/**
	 * A logger that will be displayed on the console
	 */
	USER("com.camptocamp.owsproxy.USER"); //$NON-NLS-1$

	private final static org.mortbay.log.Logger defaultLogger = Log.getLog();
	
	static {
		if (!System.getProperties()
				.containsKey("java.util.logging.config.file") //$NON-NLS-1$
				&& !System.getProperties().containsKey(
						"java.util.logging.config.class")) { //$NON-NLS-1$
			System.setProperty("java.util.logging.config.class", //$NON-NLS-1$
					"com.camptocamp.owsproxy.logging.OWSLoggingConfigurator"); //$NON-NLS-1$
			
			try {
				LogManager logManager = LogManager.getLogManager();
				logManager.readConfiguration();
			} catch (IOException e) {
				DEV.log.severe("Unable to read configuration file!!!"); //$NON-NLS-1$
			}
		
			// no logging preference file specified so use default
			Log.setLog(new org.mortbay.log.Logger(){

				public void debug(String msg, Throwable th) {
				}

				public void debug(String msg, Object arg0, Object arg1) {
				}

				public org.mortbay.log.Logger getLogger(String name) {
					return this;
				}

				public void info(String msg, Object arg0, Object arg1) {
				}

				public boolean isDebugEnabled() {
					return false;
				}

				public void setDebugEnabled(boolean enabled) {
				}

				public void warn(String msg, Throwable th) {
				}

				public void warn(String msg, Object arg0, Object arg1) {
				}
				
			});
		}

	}

	/**
	 * Sets system properties that configures the logging used by HTTPClient to
	 * show fairly detailed logging messages.
	 */
	public static void enableHttpClientDebug() {
		Log.setLog(defaultLogger);
		defaultLogger.setDebugEnabled(true);
		System.setProperty("DEBUG", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		
	    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog"); //$NON-NLS-1$ //$NON-NLS-2$
	    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true"); //$NON-NLS-1$ //$NON-NLS-2$
	    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug"); //$NON-NLS-1$ //$NON-NLS-2$
	    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");		 //$NON-NLS-1$ //$NON-NLS-2$

	}

	private Logger log;

	private OWSLogger(String loggerID) {
		log = Logger.getLogger(loggerID);
	}

	/**
	 * Look up the {@link OWSLogger} based on the id of the logger that it contains
	 */
	public static OWSLogger lookup(String loggerName) {
		for (OWSLogger logger : values()) {
			if (logger.log.getName().equals(loggerName)) {
				return logger;
			}
		}
		return null;
	}

	/**
	 * Returns the logger that the enumeration wraps
	 */
	public Logger logger() {
		return log;
	}

	/**
	 * Returns the key to the system property that is used to configure where
	 * the output file exists
	 * 
	 * @return the key to the system property that is used to configure where
	 *         the output file exists
	 */
	public String systemPropertyKey() {
		return log.getName();
	}

	// -------- Below here are all the methods that delegate to the log methods
	// --------------
	public void fine(String msg) {
		log.fine(msg);
	}

	public void finer(String msg) {
		log.finer(msg);
	}

	public void finest(String msg) {
		log.finest(msg);
	}

	public void info(String msg) {
		log.info(msg);
	}

	public boolean isLoggable(Level level) {
		return log.isLoggable(level);
	}

	public void log(Level level, String msg, Throwable thrown) {
		log.log(level, msg, thrown);
	}

	public void log(Level level, String msg) {
		log.log(level, msg);
	}

	public void severe(String msg) {
		log.severe(msg);
	}

	public void throwing(String sourceClass, String sourceMethod,
			Throwable thrown) {
		log.throwing(sourceClass, sourceMethod, thrown);
	}

	public void warning(String msg) {
		log.warning(msg);
	}

}
